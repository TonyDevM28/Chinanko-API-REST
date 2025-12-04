package chinanko.chinanko.mapper;

import org.springframework.stereotype.Component;

import chinanko.chinanko.dto.NotificationProfileUserResponse;
import chinanko.chinanko.model.NotificationProfileUser;

@Component
public class NotificationProfileUserMapper {

    public NotificationProfileUserResponse toResponse(NotificationProfileUser n){
        if(n==null) return null;
       return NotificationProfileUserResponse.builder()
                .idNotificationUser(n.getIdNotificationUser())
                .isRead(n.getState())
                .description(n.getNotification().getDescription())
                .typeName(n.getNotification().getTypeOfNotification().getType())
                .creatorName(n.getNotification().getCreator() != null ? n.getNotification().getCreator().getFirstName() : "System")
                .build();
    }
}
