package az.iktex.organization.structure.testders.entity;
import az.iktex.organization.structure.testders.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="auth_tokens",uniqueConstraints=@UniqueConstraint(name="uk_auth_token_jti",columnNames="jti"),indexes={@Index(name="idx_auth_token_session",columnList="session_id"),@Index(name="idx_auth_token_account_revoked",columnList="account_id,revoked")})
@Getter @Setter @NoArgsConstructor
public class AuthToken {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=36) private String jti;
 @Column(nullable=false,length=36) private String sessionId;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private TokenType tokenType;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="account_id",nullable=false,foreignKey=@ForeignKey(name="fk_auth_token_account")) private Account account;
 @Column(nullable=false) private Instant issuedAt;
 @Column(nullable=false) private Instant expiresAt;
 @Column(nullable=false) private boolean revoked;
 private Instant revokedAt;
 @Enumerated(EnumType.STRING) @Column(length=30) private RevokeReason revokeReason;
}
