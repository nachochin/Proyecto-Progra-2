/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Módulo desarrollado por Erika Rojas
package excepciones;

/**
 ** Excepción lanzada cuando el proceso de autenticación falla.
 * <p>
 * Se produce en los siguientes escenarios:
 * <ul>
 *   <li>El usuario no existe en el sistema.</li>
 *   <li>La contraseña ingresada no coincide con el hash almacenado.</li>
 *   <li>El usuario está inactivo o bloqueado.</li>
 * </ul>
 * </p>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>{@code
 * if (!passwordHash.equals(usuarioEncontrado.getPasswordHash())) {
 *     throw new AutenticacionFallidaException("Contraseña incorrecta para: " + nombreUsuario);
 * }
 * }</pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class AutenticacionFallidaException extends Exception {
     /**
     * Crea una excepción de autenticación fallida con un mensaje descriptivo.
     *
     * @param mensaje Descripción del motivo del fallo de autenticación.
     */
    public AutenticacionFallidaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una excepción de autenticación fallida con mensaje y causa raíz.
     *
     * @param mensaje Descripción del motivo del fallo.
     * @param causa   Excepción original que originó este error.
     */
    public AutenticacionFallidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
