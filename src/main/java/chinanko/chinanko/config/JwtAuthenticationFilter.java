package chinanko.chinanko.config;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // IMPORTANTE
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import chinanko.chinanko.service.JwtService;
import io.jsonwebtoken.Claims; // IMPORTANTE
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(JwtService jwtService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 1. Validamos firma
                if (jwtService.isTokenValid(jwt, username)) {
                    
                    // 2. EXTRAEMOS LOS ROLES DEL TOKEN
                    Claims claims = jwtService.extractAllClaims(jwt);
                    
                    // Nota: Depende de cómo tu Auth Service guardó los roles. 
                    // Usualmente es una lista de Strings bajo la llave "roles" o "authorities"
                    List<String> roles = null;
                    
                    // Intentamos obtenerlo como lista
                    try {
                        roles = claims.get("roles", List.class); 
                    } catch (Exception e) {
                        // Si falla o no existe, roles queda null
                    }

                    // Convertimos la lista de Strings a SimpleGrantedAuthority
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    
                    if (roles != null) {
                        authorities = roles.stream()
                                .map(role -> {
                                    // Aseguramos que tenga el prefijo ROLE_ si Spring lo requiere
                                    if (!role.startsWith("ROLE_")) {
                                        return new SimpleGrantedAuthority("ROLE_" + role);
                                    }
                                    return new SimpleGrantedAuthority(role);
                                })
                                .collect(Collectors.toList());
                    }

                    // 3. Creamos el objeto de autenticación CON AUTORIDADES
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities // <--- AQUI PASAMOS LOS ROLES REALES
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Imprimir para debug en consola
                    System.out.println(">>> Usuario: " + username + " | Roles extraídos: " + authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}