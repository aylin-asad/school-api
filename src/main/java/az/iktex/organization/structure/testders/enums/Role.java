package az.iktex.organization.structure.testders.enums;
import java.util.Arrays;
public enum Role {
 ADMIN(1), MANAGER(2), USER(3);
 private final Integer dbCode;
 Role(Integer code){this.dbCode=code;}
 public Integer getDbCode(){return dbCode;}
 public static Role findByDbCode(Integer code){return Arrays.stream(values()).filter(r->r.dbCode.equals(code)).findFirst().orElseThrow();}
}
