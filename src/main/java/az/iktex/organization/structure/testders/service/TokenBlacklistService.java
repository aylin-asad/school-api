package az.iktex.organization.structure.testders.service;
import az.iktex.organization.structure.testders.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class TokenBlacklistService {
 private final TokenBlacklistRepository blacklist;
 public boolean isBlacklisted(String jti){return blacklist.existsByJti(jti);}
}
