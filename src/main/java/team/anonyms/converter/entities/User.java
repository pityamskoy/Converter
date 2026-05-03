package team.anonyms.converter.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * <p>
 *     {@code User} is basic user entity. Not all client are saved in database as users since
 *     it is possible for a client to use the application without being registered.
 * </p>
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

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;
}
