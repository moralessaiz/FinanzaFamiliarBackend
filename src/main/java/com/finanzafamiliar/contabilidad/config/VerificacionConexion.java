package com.finanzafamiliar.contabilidad.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Al iniciar la aplicacion abre una conexion JDBC con MySQL y muestra en la
 * consola a que base de datos se conecto. Reemplaza la prueba de conexion que
 * antes se hacia en ConexionBD.java.
 *
 * Si MySQL esta apagado o el usuario no existe, la API igual arranca, pero
 * muestra un aviso claro en la consola para que el problema sea facil de encontrar.
 */
@Component
public class VerificacionConexion implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificacionConexion.class);

    private final DataSource dataSource;

    public VerificacionConexion(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection conexion = dataSource.getConnection()) {
            DatabaseMetaData datos = conexion.getMetaData();
            LOGGER.info("Conexion JDBC exitosa a: {}", datos.getURL());
            LOGGER.info("Motor de base de datos: {} {}", datos.getDatabaseProductName(), datos.getDatabaseProductVersion());
        } catch (SQLException e) {
            LOGGER.error("NO fue posible conectarse a MySQL: {}", e.getMessage());
            LOGGER.error("Revise que el servicio MySQL80 este encendido y que se hayan ejecutado los scripts de la carpeta sql/");
        }
    }
}
