/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

/**
 ** Excepción lanzada cuando ocurre un error durante el cálculo de la nómina.
 * <p>
 * Se produce en los siguientes escenarios:
 * <ul>
 *   <li>El salario base es cero o negativo.</li>
 *   <li>La quincena especificada es inválida (valor distinto de 1 o 2).</li>
 *   <li>La configuración de porcentajes no está cargada.</li>
 *   <li>No existen tramos de renta configurados.</li>
 *   <li>Se intenta calcular una nómina ya existente para el mismo período.</li>
 * </ul>
 * </p>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>{@code
 * if (empleado.getSalarioBase() <= 0) {
 *     throw new CalculoNominaException(
 *         "El salario base debe ser mayor a cero. Empleado: " + empleado.getId());
 * }
 * }</pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class CalculoNominaException extends Exception{
    
    /**
     * Crea una excepción de cálculo de nómina con un mensaje descriptivo.
     *
     * @param mensaje Descripción del error de cálculo.
     */
    public CalculoNominaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una excepción de cálculo de nómina con mensaje y causa raíz.
     *
     * @param mensaje Descripción del error.
     * @param causa   Excepción original que originó este error.
     */
    public CalculoNominaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
