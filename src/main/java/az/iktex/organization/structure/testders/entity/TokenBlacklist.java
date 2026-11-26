package az.iktex.organization.structure.testders.entity;
import az.iktex.organization.structure.testders.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
@Entity @Table(name="token_blacklist",uniqueConstraints=@UniqueConstraint(name="uk_blacklist_jti",columnNames="jti"),indexes=@Index(name="idx_blacklist_expires_at",columnList="expires_at"))
@Getter @Setter @NoArgsConstructor
public class TokenBlacklist {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=36) private String jti;
 @Column(nullable=false) private Long accountId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private TokenType tokenType;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private RevokeReason reason;
 @Column(nullable=false) private Instant expiresAt;
 @CreationTimestamp @Column(nullable=false) private Instant blacklistedAt;
}
