package az.iktex.organization.structure.testders.entity.converter;
import az.iktex.organization.structure.testders.enums.Role;
import jakarta.persistence.*;
@Converter
public class RoleConverter implements AttributeConverter<Role,Integer>{
 public Integer convertToDatabaseColumn(Role role){return role==null?null:role.getDbCode();}
 public Role convertToEntityAttribute(Integer code){return code==null?null:Role.findByDbCode(code);}
}
