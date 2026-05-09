package team.anonyms.converter.entities.codes;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Table(name = "email_verification_codes")
public class EmailVerificationCode extends VerificationCode {
}
