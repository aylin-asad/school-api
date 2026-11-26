package az.iktex.organization.structure.testders.controller;
import az.iktex.organization.structure.testders.service.AccountService;
import az.iktex.organization.structure.testders.request.RegisterRequest;
import az.iktex.organization.structure.testders.response.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
 private final AccountService accounts;
 @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED) public AccountResponse register(@Valid @RequestBody RegisterRequest req){return accounts.register(req);}
}
