package chinanko.chinanko.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ProfileUserResponse {
    private Integer idProfileUser; // Es útil devolver el ID del perfil
    private String firstName;      // Del Perfil (BD Local)
    private String lastName;       // Del Perfil (BD Local)
    private LocalDate bornDate;    // Del Perfil (BD Local)
    private String town;           // Del Perfil (BD Local)
    
    // Datos del Microservicio de Auth
    private String username;       // nameUser del UserDto
    private String email;          // email del UserDto
}