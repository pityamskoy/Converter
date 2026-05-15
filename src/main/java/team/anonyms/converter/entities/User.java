package team.anonyms.converter.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * {@code User} is basic user entity. A client should not be forced to register.
 * They should be able to use conversion without patterns.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isVerified;
}
