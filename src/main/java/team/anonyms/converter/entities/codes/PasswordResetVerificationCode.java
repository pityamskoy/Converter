package team.anonyms.converter.entities.codes;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Table(name = "password_reset_verification_codes")
public class PasswordResetVerificationCode extends VerificationCode {
}
