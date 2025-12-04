package chinanko.chinanko.config;

import feign.Logger; // Importar esto
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    // 1. AGREGA ESTE BEAN PARA VER LOGS
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    // 2. Tu interceptor (estaba bien, lo dejo igual)
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null) {
                        System.out.println(">>> Propagando Token en Feign: " + authHeader.substring(0, 15) + "...");
                        template.header("Authorization", authHeader);
                    }
                }
            }
        };
    }
}