package com.finanzafamiliar.contabilidad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion de CORS (Cross-Origin Resource Sharing).
 *
 * El navegador bloquea por seguridad las peticiones que una pagina hace a un
 * servidor distinto al que la entrego. Como el front-end Angular corre en
 * http://localhost:4200 y la API en http://localhost:8080, aqui se le indica
 * al navegador que la API acepta peticiones que vengan desde Angular.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${finanzafamiliar.cors.origen-permitido}")
    private String origenPermitido;

    @Override
    public void addCorsMappings(CorsRegistry registro) {
        registro.addMapping("/api/**")
                .allowedOrigins(origenPermitido)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
