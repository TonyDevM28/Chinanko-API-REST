package chinanko.chinanko.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import chinanko.chinanko.client.UserClient;
import chinanko.chinanko.dto.ProfileUserRequest;
import chinanko.chinanko.dto.ProfileUserResponse;
import chinanko.chinanko.dto.UserDto;
import chinanko.chinanko.mapper.ProfileUserMapper;
import chinanko.chinanko.model.ProfileUser;
import chinanko.chinanko.repository.ProfileUserRepository;
import lombok.RequiredArgsConstructor; // Recomendado en lugar de AllArgsConstructor para inyección final

@Service
@RequiredArgsConstructor 
public class ProfileUserServiceImpl implements ProfileUserService {

    private final ProfileUserRepository repository;
    private final ProfileUserMapper mapper;
    private final UserClient userClient; // Inyección del cliente Feign

    @Override
    public List<ProfileUserResponse> listAll() {
        List<ProfileUser> profiles = repository.findAll();
        List<ProfileUserResponse> responses = new ArrayList<>();

        // Iteramos los perfiles locales y buscamos sus datos remotos
        for (ProfileUser profile : profiles) {
            UserDto userDto = fetchUserSafe(profile.getIdUser());
            responses.add(mapper.toResponse(profile, userDto));
        }
        return responses;
    }

    @Override
    public ProfileUserResponse getById(Integer idProfileUser) {
        ProfileUser profile = repository.findById(idProfileUser).orElse(null);
        if (profile == null) return null;

        // Llamada al microservicio externo usando el ID guardado en la tabla profile
        UserDto userDto = fetchUserSafe(profile.getIdUser());
        
        return mapper.toResponse(profile, userDto);
    }

    @Override
    public ProfileUserResponse create(ProfileUserRequest p) {
        // Opcional: Podrías verificar aquí si el usuario existe antes de guardar
        // try { userClient.getUserById(p.getUserId()); } catch...
        
        ProfileUser entity = mapper.toEntity(p);
        ProfileUser saved = repository.save(entity);
        
        // Obtenemos datos para la respuesta
        UserDto userDto = fetchUserSafe(saved.getIdUser());
        return mapper.toResponse(saved, userDto);
    }

    @Override
    public ProfileUserResponse update(Integer idProfileUser, ProfileUserRequest p) {
        ProfileUser existing = repository.findById(idProfileUser).orElse(null);
        if (existing == null) return null;

        mapper.copyToEntity(existing, p);
        ProfileUser updated = repository.save(existing);
        
        UserDto userDto = fetchUserSafe(updated.getIdUser());
        return mapper.toResponse(updated, userDto);
    }

    // Método auxiliar para evitar que todo falle si el Auth Service está caído
    private UserDto fetchUserSafe(Integer userId) {
        if (userId == null) return null;
        try {
            return userClient.getUserById(userId);
        } catch (Exception e) {
            System.err.println("Error al obtener usuario del microservicio Auth: " + e.getMessage());
            // Retornamos null para que el Mapper ponga "Desconocido" en lugar de lanzar error 500
            return null; 
        }
    }
}