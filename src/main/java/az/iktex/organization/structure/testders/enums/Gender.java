package az.iktex.organization.structure.testders.enums;

import java.util.Arrays;
import java.util.Objects;

public enum Gender {
    MALE(1), FEMALE(2);
    private final Integer dbCode;
    Gender(Integer dbCode) { this.dbCode = dbCode; }
    public Integer getDbCode() { return dbCode; }
    public static Gender findByDbCode(Integer code) {
        return Arrays.stream(values())
            .filter(g -> Objects.equals(g.dbCode, code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown gender code: " + code));
    }
}
