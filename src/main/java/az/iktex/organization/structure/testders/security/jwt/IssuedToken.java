package az.iktex.organization.structure.testders.security.jwt;
import az.iktex.organization.structure.testders.enums.TokenType;
import java.time.Instant;
public record IssuedToken(String value,String jti,TokenType type,Instant issuedAt,Instant expiresAt){
 @Override public String toString(){return "IssuedToken[value=***,jti="+jti+",type="+type+"]";}
}
