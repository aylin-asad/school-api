package az.iktex.organization.structure.testders.security.jwt;
import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
@Service
public class JwtService {
 private final JwtProperties props; private final SecretKey key;
 public JwtService(JwtProperties props){this.props=props;this.key=Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.secret()));}
 public IssuedToken generate(Account a,TokenType type,String sid){
  Instant now=Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS), exp=now.plus(type==TokenType.ACCESS?props.accessTokenTtl():props.refreshTokenTtl());
  String jti=UUID.randomUUID().toString();
  JwtBuilder b=Jwts.builder().issuer(props.issuer()).subject(a.getUsername()).id(jti).claim("sid",sid).claim("type",type.name()).issuedAt(Date.from(now)).expiration(Date.from(exp));
  if(type==TokenType.ACCESS)b.claim("role",a.getRole().name());
  return new IssuedToken(b.signWith(key).compact(),jti,type,now,exp);
 }
 public Claims parse(String token){
  Claims c=Jwts.parser().verifyWith(key).requireIssuer(props.issuer()).build().parseSignedClaims(token).getPayload();
  if(c.getExpiration()==null||c.getIssuedAt()==null||c.getSubject()==null||c.getId()==null||c.get("sid",String.class)==null||c.get("type",String.class)==null)throw new MalformedJwtException("Missing token claims");
  try{UUID.fromString(c.getId());UUID.fromString(c.get("sid",String.class));}catch(IllegalArgumentException e){throw new MalformedJwtException("Invalid token identifiers");}
  return c;
 }
}
