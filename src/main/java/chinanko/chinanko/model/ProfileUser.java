package chinanko.chinanko.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "PROFILES_USERS")
public class ProfileUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_profile_user")
    private Integer idProfileUser;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "born_date")
    private LocalDate bornDate;

    // --- RELACIONES ---

    @JoinColumn(name = "fk_id_user", nullable = false, unique = true)
    private Integer idUser;

    

    // Relación Muchos a Uno con Town
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_town", nullable = false)
    private Town town;


    //Relacion Uno a Muchos con Opinions Suggested Point
    @OneToMany(mappedBy = "profileUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OpinionsSuggestedPoints> opinionsSuggestedPoints;
}