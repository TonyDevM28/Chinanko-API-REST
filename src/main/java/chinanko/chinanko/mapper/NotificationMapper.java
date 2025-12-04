package chinanko.chinanko.mapper;

import org.springframework.stereotype.Component;

import chinanko.chinanko.dto.NotificationProfileUserResponse;
import chinanko.chinanko.dto.NotificationResponse;
import chinanko.chinanko.model.Notification;
import chinanko.chinanko.model.NotificationProfileUser;

@Component
public class NotificationMapper {

    // Respuesta general de la notificación creada (Admin view)
    public static NotificationResponse toResponse(Notification e) {
        if (e == null) return null;

        return NotificationResponse.builder()
                .idNotification(e.getIdNotification())
                .description(e.getDescription())
                .typeName(e.getTypeOfNotification() != null ? e.getTypeOfNotification().getType() : "Unknown")
                .creatorName(e.getCreator() != null ? e.getCreator().getFirstName() + " " + e.getCreator().getLastName(): "System") // Asumiendo getFirstName en User
                .recipientsCount(e.getRecipients() != null ? e.getRecipients().size() : 0)
                .build();
    }

    // Respuesta para el usuario final (Inbox view)
    public static NotificationProfileUserResponse toProfileResponse(NotificationProfileUser e) {
        if (e == null) return null;

        return NotificationProfileUserResponse.builder()
                .idNotificationUser(e.getIdNotificationUser())
                .isRead(e.getState())
                // Datos aplanados desde la notificación padre
                .description(e.getNotification().getDescription())
                .typeName(e.getNotification().getTypeOfNotification().getType())
                .creatorName(e.getNotification().getCreator() != null ? e.getNotification().getCreator().getFirstName() : "System")
                .build();
    }
}