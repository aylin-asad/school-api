package az.iktex.organization.structure.testders.response;
import az.iktex.organization.structure.testders.entity.Account;
import az.iktex.organization.structure.testders.enums.Role;
import java.time.Instant;
public record AccountResponse(Long id,String username,String email,Role role,boolean enabled,Instant createdAt){
 public static AccountResponse from(Account a){return new AccountResponse(a.getId(),a.getUsername(),a.getEmail(),a.getRole(),a.isEnabled(),a.getCreatedAt());}
}
