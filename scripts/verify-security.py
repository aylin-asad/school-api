#!/usr/bin/env python3
"""Local, disposable-account integration verification. No real passwords or JWTs printed."""
import base64, json, os, subprocess, sys, time, urllib.error, urllib.request, uuid
from pathlib import Path
BASE = os.getenv('BASE_URL', 'http://localhost:8080/test-demo').rstrip('/')
ADMIN = os.environ['ADMIN_PASSWORD']
PREFIX = 'sec_' + uuid.uuid4().hex[:10]
PASSWORD = 'Aa1' + uuid.uuid4().hex
NEW_PASSWORD = 'Bb2' + uuid.uuid4().hex
SECRETS = [ADMIN, PASSWORD, NEW_PASSWORD]
LOG = Path('logs/app.log')
LOG_START = LOG.stat().st_size if LOG.exists() else 0

def req(method, path, body=None, token=None, status=200, code=None):
    headers = {'Accept': 'application/json', 'Accept-Language':'eng', 'X-Trace-Id':'test-'+uuid.uuid4().hex}
    if token: headers['Authorization'] = 'Bearer ' + token
    data = None if body is None else json.dumps(body).encode()
    if data is not None: headers['Content-Type'] = 'application/json'
    request = urllib.request.Request(BASE+path, data=data, headers=headers, method=method)
    try:
        response = urllib.request.urlopen(request, timeout=20)
    except urllib.error.HTTPError as exc:
        response = exc
    with response:
        actual = response.code
        raw = response.read().decode()
    try: value = json.loads(raw) if raw else None
    except json.JSONDecodeError: value = None
    if actual != status:
        safe_code = value.get('code') if isinstance(value, dict) else None
        raise AssertionError(f'{method} {path}: expected HTTP {status}, got {actual}; error code={safe_code}')
    if code:
        assert isinstance(value, dict) and value.get('code') == code, f'{path}: expected {code}'
        assert value.get('status') == status and value.get('traceId') == headers['X-Trace-Id'] and value.get('message'), f'{path}: invalid error response'
    return value

def claim(token):
    part = token.split('.')[1]
    return json.loads(base64.urlsafe_b64decode(part + '='*((-len(part))%4)))

def login(name, password=PASSWORD):
    pair = req('POST','/api/auth/login',{'login':name,'password':password})
    assert pair['tokenType']=='Bearer'
    SECRETS.extend([pair['accessToken'],pair['refreshToken']])
    return pair

def register(suffix):
    name = PREFIX + '_' + suffix
    a = req('POST','/api/auth/register',{'username':name,'email':name+'@example.test','password':PASSWORD},status=201)
    assert a['role']=='USER' and 'password' not in a
    return name,a['id']

def sql(query, table=False):
    args=['docker','exec','school-postgres','psql','-X','-U','postgres','-d','school_db','-v','ON_ERROR_STOP=1']
    if not table: args+=['-At']
    return subprocess.check_output(args+['-c',query],text=True).strip()

def passed(n, detail): print(f'PASS {n:02d} | {detail}', flush=True)

def main():
    if '--expiry' in sys.argv:
        u,_=register('expiry'); a=login(u); b=login(u)
        assert a['accessExpiresIn']==60, 'Start app with APP_JWT_ACCESS_TOKEN_TTL=1m'
        jti=claim(a['accessToken'])['jti']; revoked_jti=claim(b['accessToken'])['jti']
        req('POST','/api/auth/logout',token=b['accessToken'],status=204)
        print('Waiting until the 1-minute access token expires...',flush=True)
        time.sleep(62)
        req('GET','/api/students',token=a['accessToken'],status=401,code='ERR-2702');passed(12,'Access token expired')
        for _ in range(15):
            n=sql(f"select (select count(*) from auth_tokens where jti in ('{jti}','{revoked_jti}'))+(select count(*) from token_blacklist where jti='{revoked_jti}')")
            if n=='0': break
            time.sleep(5)
        assert n=='0','Cleanup did not remove expired records; set cron to every minute'
        assert LOG.exists() and 'Token cleanup:' in LOG.read_text(errors='replace'), 'Cleanup log missing'
        passed(13,'Expired records removed from both tables');return
    err=req('GET','/api/students?lang=rus',status=401,code='ERR-2700');assert err['message']=='Требуется авторизация';passed(1,'Localized 401 and traceId')
    u,uid=register('user');
    a=req('POST','/api/auth/login',{'login':u,'password':'incorrect'},status=401,code='ERR-2701')
    b=req('POST','/api/auth/login',{'login':PREFIX+'_absent','password':'incorrect'},status=401,code='ERR-2701')
    assert a['message']==b['message'];passed(2,'Unknown account and bad password return same message')
    admin=login(os.getenv('ADMIN_USERNAME','admin'),ADMIN); A=admin['accessToken']; AR=admin['refreshToken']
    sid=claim(A)['sid'];assert sid==claim(AR)['sid']
    assert sql(f"select count(*) from auth_tokens where session_id='{sid}' and revoked=false")=='2';passed(3,'Login issued two stored token IDs with the same session ID')
    req('GET','/api/students',token=A)
    assert LOG.exists() and b'[admin]' in LOG.read_bytes()[LOG_START:], 'Expected [admin] in logs';passed(4,'Authenticated GET and username in log')
    req('GET','/api/students',token=AR,status=401,code='ERR-2705');passed(5,'Refresh token cannot authenticate API requests')
    # Keep the evidence session distinct from rotation/reuse tests.
    p=login(u); access=p['accessToken']; s=claim(access)['sid']
    req('POST','/api/auth/logout',token=access,status=204)
    req('GET','/api/students',token=access,status=401,code='ERR-2704')
    assert sql(f"select count(*) from auth_tokens where session_id='{s}' and revoked and revoke_reason='LOGOUT'")=='2'
    assert sql(f"select count(*) from token_blacklist where reason='LOGOUT' and jti in (select jti from auth_tokens where session_id='{s}')")=='2';passed(6,'Immediate logout; both tokens recorded in blacklist')
    evidence=Path('screenshots/security');evidence.mkdir(parents=True,exist_ok=True)
    evidence_sql=f"""SELECT jti, session_id, token_type, revoked, revoked_at, revoke_reason FROM auth_tokens WHERE session_id = '{s}' ORDER BY token_type;\nSELECT jti, account_id, token_type, reason, expires_at, blacklisted_at FROM token_blacklist WHERE jti IN (SELECT jti FROM auth_tokens WHERE session_id = '{s}') ORDER BY token_type;\n"""
    (evidence/'logout-evidence.sql').write_text(evidence_sql)
    (evidence/'logout-evidence.txt').write_text(sql(evidence_sql,table=True)+'\n')
    print('Screenshot evidence SQL: screenshots/security/logout-evidence.sql',flush=True)
    u2,id2=register('reuse'); p=login(u2); second=login(u2)
    r=req('POST','/api/auth/refresh',{'refreshToken':p['refreshToken']});SECRETS.extend([r['accessToken'],r['refreshToken']])
    assert claim(p['accessToken'])['sid']==claim(r['accessToken'])['sid'] and claim(p['accessToken'])['jti']!=claim(r['accessToken'])['jti']
    req('GET','/api/students',token=p['accessToken'],status=401,code='ERR-2704')
    req('POST','/api/auth/refresh',{'refreshToken':p['refreshToken']},status=401,code='ERR-2706')
    req('GET','/api/students',token=r['accessToken'],status=401,code='ERR-2704');req('GET','/api/students',token=second['accessToken'],status=401,code='ERR-2704')
    assert sql(f'select count(*) from auth_tokens where account_id={int(id2)} and revoked=false')=='0';passed(7,'Reuse detection committed account-wide revocation (no rollback)')
    u3,_=register('sessions'); x=login(u3);y=login(u3)
    sessions=req('GET','/api/auth/sessions',token=x['accessToken']);assert len(sessions)==2 and sum(z['current'] for z in sessions)==1
    s2=claim(y['accessToken'])['sid'];req('DELETE','/api/auth/sessions/'+s2,token=x['accessToken'],status=204)
    req('GET','/api/students',token=y['accessToken'],status=401,code='ERR-2704');passed(8,'Two sessions; one revoked')
    req('DELETE','/api/students/1',token=x['accessToken'],status=403,code='ERR-2707');passed(9,'USER forbidden from DELETE')
    req('PUT','/api/auth/password',{'oldPassword':PASSWORD,'newPassword':NEW_PASSWORD},token=x['accessToken'],status=204)
    req('GET','/api/students',token=x['accessToken'],status=401,code='ERR-2704');passed(10,'Password change revoked old token')
    u4,id4=register('disable'); d=login(u4)
    req('PATCH',f'/api/admin/accounts/{id4}/disable',token=A);req('GET','/api/students',token=d['accessToken'],status=401,code='ERR-2704')
    req('POST','/api/auth/login',{'login':u4,'password':PASSWORD},status=403,code='ERR-2708');passed(11,'Disabled account rejected and existing token revoked')
    u5,_=register('lockout')
    for i in range(5):req('POST','/api/auth/login',{'login':u5,'password':'incorrect'},status=423 if i==4 else 401,code='ERR-2709' if i==4 else 'ERR-2701')
    req('POST','/api/auth/login',{'login':u5,'password':PASSWORD},status=423,code='ERR-2709');print('PASS bonus | Five failures trigger lockout')
    doc=req('GET','/v3/api-docs');assert doc['components']['securitySchemes']['bearerAuth']['scheme']=='bearer'
    for name in ('register','login','refresh'):
        assert doc['paths']['/api/auth/'+name]['post'].get('security')==[]
    assert '401' in doc['paths']['/api/students']['get']['responses'] and '403' in doc['paths']['/api/students']['get']['responses'];passed(15,'OpenAPI bearer scheme and public operations (UI screenshot still manual)')
    c=claim(A);assert {'sub','jti','sid','type','iss','iat','exp','role'}<=set(c) and 'password' not in c and 'email' not in c;passed(16,'JWT claim structure (decoded locally, no external upload)')
    # Check only new log content so unrelated previous assignments do not invalidate this test.
    logs=LOG.read_bytes()[LOG_START:].decode(errors='replace');assert not any(secret in logs for secret in SECRETS), 'Sensitive values detected in logs'
    passed(14,'No tested passwords or token values in appended logs')
    print('Standard checks passed. Run --expiry after restarting with 1m access TTL and minutely cleanup to verify tests 12–13.')
    print('Disposable test accounts remain in the development DB; no existing data was deleted.')

if __name__=='__main__':
    try: main()
    except Exception as exc:
        # Avoid printing raw HTTP responses or secrets.
        print(f'FAIL: {type(exc).__name__}: {exc}',file=sys.stderr);sys.exit(1)
