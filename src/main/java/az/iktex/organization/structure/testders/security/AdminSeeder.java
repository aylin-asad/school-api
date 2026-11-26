package az.iktex.organization.structure.testders.security;
import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
@Component @RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
 private final AccountRepository accounts; private final PasswordEncoder encoder;
 @Value("${app.security.admin.username}") private String username;
 @Value("${app.security.admin.email}") private String email;
 @Value("${app.security.admin.password}") private String password;
 public void run(String... args){
  if(!accounts.existsByRole(Role.ADMIN)){
   if(password==null || password.length()<8 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72) throw new IllegalStateException("Set a valid ADMIN_PASSWORD (8+ characters, at most 72 UTF-8 bytes)");
   Account a=new Account(); a.setUsername(username); a.setEmail(email); a.setPassword(encoder.encode(password)); a.setRole(Role.ADMIN); accounts.save(a);
  }
 }
}
