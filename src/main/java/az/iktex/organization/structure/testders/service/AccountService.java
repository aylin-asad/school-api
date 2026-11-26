package az.iktex.organization.structure.testders.service;
import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.*;
import az.iktex.organization.structure.testders.repository.AccountRepository;
import az.iktex.organization.structure.testders.request.RegisterRequest;
import az.iktex.organization.structure.testders.response.AccountResponse;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class AccountService {
 private final AccountRepository accounts; private final PasswordEncoder encoder;
 private final TokenBlacklistService revocations;
 @Transactional public AccountResponse register(RegisterRequest req){
  if(accounts.existsByUsername(req.username())) throw new AlreadyExistsException(ErrorCode.ACCOUNT_USERNAME_EXISTS);
  if(accounts.existsByEmail(req.email())) throw new AlreadyExistsException(ErrorCode.ACCOUNT_EMAIL_EXISTS);
  Account a=new Account(); a.setUsername(req.username()); a.setEmail(req.email()); a.setPassword(encoder.encode(req.password())); a.setRole(Role.USER);
  return AccountResponse.from(accounts.saveAndFlush(a));
 }
 @Transactional(readOnly=true) public java.util.List<AccountResponse> list(Role role){return (role==null?accounts.findAll():accounts.findAllByRole(role)).stream().map(AccountResponse::from).toList();}
 private Account lock(Long id){return accounts.lockById(id).orElseThrow(()->new az.iktex.organization.structure.testders.exception.NotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));}
 private void notSelf(Long id,Long actor){if(id.equals(actor))throw new AlreadyExistsException(ErrorCode.ACCOUNT_SELF_MODIFICATION);}
 @Transactional public AccountResponse role(Long id,Long actor,Role role){notSelf(id,actor);Account a=lock(id);a.setRole(role);accounts.save(a);revocations.revokeAll(id,RevokeReason.ADMIN_REVOKE);return AccountResponse.from(a);}
 @Transactional public AccountResponse disable(Long id,Long actor){notSelf(id,actor);Account a=lock(id);a.setEnabled(false);accounts.save(a);revocations.revokeAll(id,RevokeReason.ACCOUNT_DISABLED);return AccountResponse.from(a);}
 @Transactional public AccountResponse enable(Long id){Account a=lock(id);a.setEnabled(true);return AccountResponse.from(accounts.save(a));}
 @Transactional public void revokeTokens(Long id){lock(id);revocations.revokeAll(id,RevokeReason.ADMIN_REVOKE);}
}