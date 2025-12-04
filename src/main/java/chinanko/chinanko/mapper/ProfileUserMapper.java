package chinanko.chinanko.mapper;

import org.springframework.stereotype.Component;
import chinanko.chinanko.dto.ProfileUserRequest;
import chinanko.chinanko.dto.ProfileUserResponse;
import chinanko.chinanko.dto.UserDto;
import chinanko.chinanko.model.ProfileUser;
import chinanko.chinanko.model.Town;

@Component
public class ProfileUserMapper {

    // AHORA ACEPTA EL UserDto COMO SEGUNDO PARÁMETRO
    public ProfileUserResponse toResponse(ProfileUser p, UserDto u) {
        if (p == null) return null;

        String townName = (p.getTown() != null) ? p.getTown().getNameTown() : "Sin Ciudad";
        
        // Manejo seguro de nulos por si el microservicio de Auth falla o no trae user
        String authUsername = (u != null) ? u.getNameUser() : "Desconocido";
        String authEmail = (u != null) ? u.getEmail() : "No disponible";

        return ProfileUserResponse.builder()
            .idProfileUser(p.getIdProfileUser())
            .firstName(p.getFirstName())
            .lastName(p.getLastName())
            .bornDate(p.getBornDate())
            .town(townName)
            .username(authUsername) // Dato externo
            .email(authEmail)       // Dato externo
            .build();
    }

    public ProfileUser toEntity(ProfileUserRequest p) {
        if (p == null) return null;
        return ProfileUser.builder()
            .firstName(p.getFirstName())
            .lastName(p.getLastName())
            .bornDate(p.getBornDate())
            .idUser(p.getUserId()) // Guardamos el ID del usuario externo
            .town(Town.builder().idTown(p.getTownId()).build())
            .build();
    }

    public void copyToEntity(ProfileUser p, ProfileUserRequest r) {
        if (p == null || r == null) return;
        p.setFirstName(r.getFirstName());
        p.setLastName(r.getLastName());
        p.setBornDate(r.getBornDate());
        p.setIdUser(r.getUserId());
        p.setTown(Town.builder().idTown(r.getTownId()).build());
    }
}