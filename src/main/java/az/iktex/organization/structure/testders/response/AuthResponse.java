package az.iktex.organization.structure.testders.response;
public record AuthResponse(String tokenType,String accessToken,long accessExpiresIn,String refreshToken,long refreshExpiresIn){
 @Override public String toString(){return "AuthResponse[tokenType=Bearer,accessToken=***,refreshToken=***]";}
}
