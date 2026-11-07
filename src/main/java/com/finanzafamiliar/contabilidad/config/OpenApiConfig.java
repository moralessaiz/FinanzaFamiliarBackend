package com.finanzafamiliar.contabilidad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Datos generales de la documentacion de la API (OpenAPI 3), que aparecen en la
 * parte superior de la pagina de Swagger UI: http://localhost:8080/swagger-ui.html
 *
 * La documentacion de cada servicio (ruta, parametros, cuerpo, respuestas y
 * errores) se escribe con anotaciones en los controladores y en los modelos;
 * springdoc las lee al arrancar la aplicacion y arma la pagina automaticamente.
 *
 * Evidencia GA7-220501096-AA5-EV03 - Diseño y desarrollo de servicios web.
 */
@Configuration
public class OpenApiConfig {

    /** Nombres de los grupos (tags) en los que se organizan los servicios. */
    public static final String TAG_USUARIOS = "Usuarios";
    public static final String TAG_MOVIMIENTOS = "Movimientos";
    public static final String TAG_CATEGORIAS = "Categorías";

    private static final String DESCRIPCION = """
            API REST del **Sistema de Contabilidad Familiar - FinanzaFamiliar**: permite registrar \
            los integrantes del grupo familiar y sus ingresos y gastos, y consultar el saldo del hogar.

            **Tecnologías:** Java 17, Spring Boot 3.5, JDBC (JdbcTemplate) y MySQL 8.

            **Formato:** todas las peticiones y respuestas usan JSON. Las fechas viajan como texto \
            `aaaa-mm-dd` y los montos como número con máximo 2 decimales.

            **Errores:** todos los errores responden con el mismo formato:
            `{ "mensaje": "...", "errores": { "campo": "detalle" } }`. El objeto `errores` solo \
            aparece cuando hay campos con datos inválidos.

            | Código | Significado |
            |---|---|
            | 200 | Consulta, actualización o eliminación correcta |
            | 201 | Registro creado |
            | 400 | Datos inválidos o regla de negocio no cumplida |
            | 404 | El registro o la ruta no existe |
            | 409 | Conflicto con datos existentes (correo repetido, usuario con movimientos) |
            | 503 | No hay conexión con la base de datos |
            """;

    @Bean
    public OpenAPI documentacionFinanzaFamiliar() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinanzaFamiliar - API REST")
                        .version("2.0.0")
                        .description(DESCRIPCION))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor local de desarrollo")))
                .tags(List.of(
                        new Tag().name(TAG_USUARIOS)
                                .description("Integrantes del grupo familiar (tabla usuario)"),
                        new Tag().name(TAG_MOVIMIENTOS)
                                .description("Ingresos y gastos del hogar (tabla movimiento) y resumen del saldo"),
                        new Tag().name(TAG_CATEGORIAS)
                                .description("Catálogo de categorías de ingreso y gasto (tabla categoria)")));
    }
}
