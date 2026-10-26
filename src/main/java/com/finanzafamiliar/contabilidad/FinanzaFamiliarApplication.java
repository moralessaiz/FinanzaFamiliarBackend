package com.finanzafamiliar.contabilidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la API REST del Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * La anotacion @SpringBootApplication activa la configuracion automatica de Spring Boot:
 * arranca un servidor Tomcat embebido en el puerto 8080, crea la conexion a MySQL
 * (DataSource y JdbcTemplate) con los datos de application.properties y registra
 * todos los controladores, servicios y repositorios de los subpaquetes.
 *
 * Evidencia GA7-220501096-AA3-EV01 - Codificacion de modulos del software stand-alone, web y movil.
 */
@SpringBootApplication
public class FinanzaFamiliarApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanzaFamiliarApplication.class, args);
    }
}
