package chinanko.chinanko.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chinanko.chinanko.dto.NotificationProfileUserResponse;
import chinanko.chinanko.dto.NotificationRequest;
import chinanko.chinanko.dto.NotificationResponse;
import chinanko.chinanko.mapper.NotificationMapper;
import chinanko.chinanko.model.Notification;
import chinanko.chinanko.model.NotificationProfileUser;
import chinanko.chinanko.model.TypeOfNotification;
import chinanko.chinanko.model.ProfileUser;
import chinanko.chinanko.repository.NotificationProfileUserRepository;
import chinanko.chinanko.repository.NotificationRepository;
import chinanko.chinanko.repository.TypeOfNotificationRepository;
import chinanko.chinanko.repository.ProfileUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProfileUserRepository profileUserRepository;
    private final ProfileUserRepository userRepository;
    private final TypeOfNotificationRepository typeRepository;

    @Override
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        // 1. Validar entidades base
        ProfileUser creator = userRepository.findById(request.getCreatorUserId())
                .orElseThrow(() -> new EntityNotFoundException("Creator User not found"));

        TypeOfNotification type = typeRepository.findById(request.getTypeNotificationId())
                .orElseThrow(() -> new EntityNotFoundException("Type of Notification not found"));

        // 2. Crear Notificación Padre
        Notification notification = Notification.builder()
                .description(request.getDescription())
                .creator(creator)
                .typeOfNotification(type)
                .recipients(new ArrayList<>()) // Inicializamos lista
                .build();

        // 3. Crear destinatarios (Cascada)
        for (Integer recipientId : request.getRecipientUserIds()) {
            ProfileUser recipient = userRepository.findById(recipientId)
                    .orElseThrow(() -> new EntityNotFoundException("Recipient User not found: " + recipientId));

            NotificationProfileUser notifUser = NotificationProfileUser.builder()
                    .user(recipient)
                    .notification(notification) // Relación bidireccional
                    .state(false) // Por defecto no leída
                    .build();

            notification.getRecipients().add(notifUser);
        }

        // 4. Guardar (Cascade guardará todos los NotificationProfileUser)
        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public NotificationResponse update(Integer id, String newDescription) {
        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        
        existing.setDescription(newDescription);
        return NotificationMapper.toResponse(notificationRepository.save(existing));
    }

    @Override
    @Transactional
    public NotificationProfileUserResponse markAsRead(Integer idNotificationUser) {
        NotificationProfileUser npu = profileUserRepository.findById(idNotificationUser)
                .orElseThrow(() -> new EntityNotFoundException("User Notification link not found"));
        
        npu.setState(true); // Marcar como leída
        return NotificationMapper.toProfileResponse(profileUserRepository.save(npu));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(Integer id) {
        return NotificationMapper.toResponse(notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationProfileUserResponse> getByUser(Integer userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<NotificationProfileUser> result = profileUserRepository.findByUser_IdUser(userId, pageable);
        return result.stream().map(NotificationMapper::toProfileResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationProfileUserResponse> getByUserAndState(Integer userId, Boolean state, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<NotificationProfileUser> result = profileUserRepository.findByUser_IdUserAndState(userId, state, pageable);
        return result.stream().map(NotificationMapper::toProfileResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationProfileUserResponse> getByUserAndType(Integer userId, Integer typeId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<NotificationProfileUser> result = profileUserRepository
                .findByUser_IdUserAndNotification_TypeOfNotification_IdTypeNotification(userId, typeId, pageable);
        return result.stream().map(NotificationMapper::toProfileResponse).collect(Collectors.toList());
    }
}