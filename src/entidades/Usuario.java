/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 ** Representa un usuario del sistema de nómina.
 * <p>
 * Almacena las credenciales necesarias para el proceso de autenticación.
 * La contraseña debe almacenarse hasheada (nunca en texto plano)
 * en el archivo {@code data/usuarios.txt}.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public class Usuario {
     /** Nombre de usuario para iniciar sesión. */
    private String nombreUsuario;

    /**
     * Contraseña del usuario almacenada como hash SHA-256.
     * Nunca se guarda la contraseña en texto plano.
     */
    private String passwordHash;

    /**
     * Rol del usuario dentro del sistema.
     * Valores posibles: ADMIN, RRHH.
     */
    private String rol;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Constructor por defecto.
     */
    public Usuario() {
    }

    /**
     * Constructor completo para crear un usuario del sistema.
     *
     * @param nombreUsuario Nombre de usuario único.
     * @param passwordHash  Hash SHA-256 de la contraseña.
     * @param rol           Rol del usuario (ADMIN / RRHH).
     */
    public Usuario(String nombreUsuario, String passwordHash, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /**
     * Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param nombreUsuario Nuevo nombre de usuario.
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Obtiene el hash de la contraseña.
     *
     * @return Hash SHA-256 de la contraseña.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Establece el hash de la contraseña.
     *
     * @param passwordHash Nuevo hash SHA-256.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Obtiene el rol del usuario en el sistema.
     *
     * @return Rol del usuario (ADMIN / RRHH).
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     *
     * @param rol Nuevo rol del usuario.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    /**
     * Representación en texto del usuario (sin exponer el hash).
     *
     * @return Cadena con nombre de usuario y rol.
     */
    @Override
    public String toString() {
        return "Usuario{nombreUsuario='" + nombreUsuario + "', rol='" + rol + "'}";
    }
}
