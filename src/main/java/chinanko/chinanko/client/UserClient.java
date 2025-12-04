package chinanko.chinanko.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import chinanko.chinanko.config.FeignClientConfig; 
import chinanko.chinanko.dto.UserDto;

// CAMBIOS REALIZADOS:
// 1. name = "chinanko-roles": Debe coincidir EXACTAMENTE con spring.application.name del servicio de Auth.
// 2. Eliminé 'url': Así Feign le pregunta a Eureka "¿Dónde está chinanko-roles?" y Eureka le da la IP correcta.
@FeignClient(name = "chinanko-roles", configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    UserDto getUserById(@PathVariable Integer id);
}