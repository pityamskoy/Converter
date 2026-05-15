package team.anonyms.converter.entities.codes;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "password_reset_verification_codes")
public class PasswordResetVerificationCode extends VerificationCode {
}
