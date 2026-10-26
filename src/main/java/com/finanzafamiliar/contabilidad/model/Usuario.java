package com.finanzafamiliar.contabilidad.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Clase modelo (POJO) que representa un usuario (integrante del grupo
 * familiar) del Sistema de Contabilidad Familiar - FinanzaFamiliar.
 * Corresponde a la tabla "usuario".
 *
 * Las anotaciones de validacion reemplazan los "if" que antes hacia
 * RegistrarUsuarioServlet: Spring las revisa antes de llamar al controlador
 * y, si algun dato no las cumple, responde 400 con el mensaje de cada campo.
 */
public class Usuario {

    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres")
    @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "El correo electrónico no es válido")
    private String correo;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "Administrador|Integrante", message = "El rol debe ser Administrador o Integrante")
    private String rol;

    public Usuario() {
    }

    public Usuario(Long idUsuario, String nombre, String correo, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
