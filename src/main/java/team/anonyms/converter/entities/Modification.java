package team.anonyms.converter.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * <p>
 *     Modifications are necessary to know how to apply a pattern.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "modifications")
public class Modification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String oldName;

    private String newName;

    private String newValue;

    private String newType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pattern_id", nullable = false)
    private Pattern pattern;
}
