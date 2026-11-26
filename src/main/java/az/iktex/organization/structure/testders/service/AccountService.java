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
 @Transactional public AccountResponse register(RegisterRequest req){
  if(accounts.existsByUsername(req.username())) throw new AlreadyExistsException(ErrorCode.ACCOUNT_USERNAME_EXISTS);
  if(accounts.existsByEmail(req.email())) throw new AlreadyExistsException(ErrorCode.ACCOUNT_EMAIL_EXISTS);
  Account a=new Account(); a.setUsername(req.username()); a.setEmail(req.email()); a.setPassword(encoder.encode(req.password())); a.setRole(Role.USER);
  return AccountResponse.from(accounts.saveAndFlush(a));
 }
}
