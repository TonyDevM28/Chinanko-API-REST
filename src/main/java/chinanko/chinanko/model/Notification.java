package chinanko.chinanko.model;

import java.util.ArrayList;
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
@Table(name = "NOTIFICATIONS")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_notification")
    private Integer idNotification;

    @Column(name = "description", nullable = false)
    private String description;

    // Estado global de la notificación (ej. activa/archivada para el sistema)
    @Column(name = "state", nullable = false)
    @Builder.Default
    private Boolean state = true; 

    // EL EMISOR: Quién creó esta notificación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_user_creator")
    private ProfileUser creator;

    // Relación en cascada: Al guardar la Notificación, se guardan los destinatarios
    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<NotificationProfileUser> recipients = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_type_of_notification", nullable = false)
    private TypeOfNotification typeOfNotification;
}