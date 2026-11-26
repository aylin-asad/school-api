package az.iktex.organization.structure.testders.controller;
import az.iktex.organization.structure.testders.service.*;
import az.iktex.organization.structure.testders.request.*;
import az.iktex.organization.structure.testders.response.*;
import az.iktex.organization.structure.testders.security.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
 private final AccountService accounts;private final AuthService auth;
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AccountResponse register(@Valid @RequestBody RegisterRequest req){return accounts.register(req);}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest req){return auth.login(req);}
 @PostMapping("/refresh") public AuthResponse refresh(@Valid @RequestBody RefreshRequest req){return auth.refresh(req);}
 @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) public void logout(@AuthenticationPrincipal AuthPrincipal p){auth.logout(p);}
 @PostMapping("/logout-all") @ResponseStatus(HttpStatus.NO_CONTENT) public void logoutAll(@AuthenticationPrincipal AuthPrincipal p){auth.logoutAll(p);}
 @GetMapping("/me") public AccountResponse me(@AuthenticationPrincipal AuthPrincipal p){return auth.me(p);}
 @GetMapping("/sessions") public List<SessionResponse> sessions(@AuthenticationPrincipal AuthPrincipal p){return auth.sessions(p);}
 @DeleteMapping("/sessions/{sessionId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void revoke(@AuthenticationPrincipal AuthPrincipal p,@PathVariable String sessionId){auth.revoke(p,sessionId);}
 @PutMapping("/password") @ResponseStatus(HttpStatus.NO_CONTENT) public void password(@AuthenticationPrincipal AuthPrincipal p,@Valid @RequestBody ChangePasswordRequest req){auth.password(p,req);}
}
