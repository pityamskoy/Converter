package team.anonyms.converter.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Patterns")
public final class Pattern {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    // Example: .csv .json or .json .xml.
    @Column(name = "conversion_type", nullable = false)
    private String conversionType;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Modification> modifications;
}
