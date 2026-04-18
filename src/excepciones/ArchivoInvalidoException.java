/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

/**
 ** Excepción lanzada cuando ocurre un error en la lectura o escritura
 * de los archivos de datos del sistema.
 * <p>
 * Se produce en los siguientes escenarios:
 * <ul>
 *   <li>El archivo de datos no existe o no puede abrirse.</li>
 *   <li>Una línea del archivo tiene un formato inválido (campos faltantes,
 *       separadores incorrectos, datos no numéricos donde se esperan números).</li>
 *   <li>Error de permisos al intentar escribir en el archivo.</li>
 *   <li>El archivo está vacío cuando se esperan datos.</li>
 * </ul>
 * </p>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>{@code
 * String[] campos = linea.split("\\|");
 * if (campos.length < 6) {
 *     throw new ArchivoInvalidoException(
 *         "Formato inválido en empleados.txt, línea: " + linea);
 * }
 * }</pre>
 * @author ekaro
 * @version 1.0
 */
public class ArchivoInvalidoException extends Exception {
    
    /**
     * Crea una excepción de archivo inválido con un mensaje descriptivo.
     *
     * @param mensaje Descripción del problema encontrado en el archivo.
     */
    public ArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una excepción de archivo inválido con mensaje y causa raíz.
     *
     * @param mensaje Descripción del problema.
     * @param causa   Excepción original (ej. {@code IOException}, {@code NumberFormatException}).
     */
    public ArchivoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
