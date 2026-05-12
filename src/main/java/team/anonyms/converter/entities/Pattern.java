package team.anonyms.converter.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


/**
 * <p>
 *     Patterns provide possibility to alter a file during conversion.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "patterns")
public class Pattern {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
