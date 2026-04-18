/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package reportes;

import excepciones.ArchivoInvalidoException;

/**
 * Interfaz que estandariza la generación de reportes en el sistema.
 * <p>
 * Toda clase que genere un reporte (PDF de empleado, PDF de patrono, etc.) debe
 * implementar esta interfaz, garantizando un contrato común de generación y
 * exportación.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public interface GeneradorReporte {

    /**
     * Genera el reporte y lo guarda en la ruta especificada.
     *
     * @param rutaDestino Ruta completa del archivo a generar (ej.
     * "reportes/nomina_001.pdf").
     * @throws ArchivoInvalidoException si ocurre un error al crear o escribir
     * el archivo.
     */
    void generar(String rutaDestino) throws ArchivoInvalidoException;

    /**
     * Devuelve el nombre sugerido para el archivo del reporte.
     * <p>
     * Ejemplo: {@code "nomina_001_2024_06_Q1.pdf"}
     * </p>
     *
     * @return Nombre de archivo sugerido con extensión .pdf.
     */
    String getNombreArchivo();
}
