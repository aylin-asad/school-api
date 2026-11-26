package az.iktex.organization.structure.testders.controller;
import az.iktex.organization.structure.testders.enums.Role;
import az.iktex.organization.structure.testders.request.ChangeRoleRequest;
import az.iktex.organization.structure.testders.response.AccountResponse;
import az.iktex.organization.structure.testders.security.AuthPrincipal;
import az.iktex.organization.structure.testders.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/admin/accounts") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {
 private final AccountService accounts;
 @GetMapping public List<AccountResponse> list(@RequestParam(required=false) Role role){return accounts.list(role);}
 @PatchMapping("/{id}/role") public AccountResponse role(@PathVariable Long id,@AuthenticationPrincipal AuthPrincipal p,@Valid @RequestBody ChangeRoleRequest req){return accounts.role(id,p.accountId(),req.role());}
 @PatchMapping("/{id}/disable") public AccountResponse disable(@PathVariable Long id,@AuthenticationPrincipal AuthPrincipal p){return accounts.disable(id,p.accountId());}
 @PatchMapping("/{id}/enable") public AccountResponse enable(@PathVariable Long id){return accounts.enable(id);}
 @PostMapping("/{id}/revoke-tokens") @ResponseStatus(HttpStatus.NO_CONTENT) public void revoke(@PathVariable Long id){accounts.revokeTokens(id);}
}
