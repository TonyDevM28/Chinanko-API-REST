package chinanko.chinanko.model;

import java.sql.Time;
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
@Table(name = "EVENTS")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_event")
    private Integer idEvent;

    @Column(name = "name_event")
    private String nameEvent;

    @Column(name = "description")
    private String description;

    @Column(name = "time_begin")
    private LocalDate timeBegin;

    @Column(name = "time_end")
    private LocalDate timeEnd;

    @Column(name = "price")
    private float price;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_town")
    private Town town;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_type_of_event")
    private TypeOfEvent typeOfEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_state_of_event")
    private StateOfEvent stateOfEvent;

    // Configuración de Cascada: Crear/Actualizar/Borrar direcciones junto con el evento
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AddressEvent> addressEvent;
}