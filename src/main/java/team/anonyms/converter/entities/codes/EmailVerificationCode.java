package team.anonyms.converter.entities.codes;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "email_verification_codes")
public class EmailVerificationCode extends VerificationCode {
}
