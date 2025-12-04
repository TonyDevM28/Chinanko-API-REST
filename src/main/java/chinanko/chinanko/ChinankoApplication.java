package chinanko.chinanko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // Importar esto

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@EnableDiscoveryClient
@EnableFeignClients // <--- AGREGAR ESTO OBLIGATORIAMENTE
@OpenAPIDefinition(info = @Info(
    title = "Chinanko API", 
    version = "1.0", 
    description = "API para Chinanko"
))
@SpringBootApplication
public class ChinankoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinankoApplication.class, args);
    }

}