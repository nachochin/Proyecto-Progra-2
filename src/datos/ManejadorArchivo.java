/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package datos;

import excepciones.ArchivoInvalidoException;
import java.util.List;

/**
 ** Interfaz que estandariza las operaciones de lectura y escritura en archivos
 * de texto para todas las clases DAO del sistema.
 * <p>
 * Cada DAO del paquete {@code datos} debe implementar esta interfaz,
 * garantizando que todas las entidades tengan el mismo contrato de acceso a
 * datos: guardar, buscar, listar, actualizar y eliminar.
 * </p>
 *
 * @param <T> Tipo de entidad que maneja el DAO (Empleado, Nomina, etc.).
 * @param <ID> Tipo del identificador único de la entidad (String, Integer,
 * etc.).
 *
 * @author ekaro
 * @version 1.0
 */
public interface ManejadorArchivo<T, ID> {

    /**
     * Guarda una nueva entidad al final del archivo de datos.
     *
     * @param entidad Objeto a persistir en el archivo.
     * @throws ArchivoInvalidoException si ocurre un error al escribir el
     * archivo.
     */
    void guardar(T entidad) throws ArchivoInvalidoException;

    /**
     * Busca y devuelve una entidad por su identificador único.
     *
     * @param id Identificador único de la entidad a buscar.
     * @return La entidad encontrada, o {@code null} si no existe.
     * @throws ArchivoInvalidoException si ocurre un error al leer el archivo.
     */
    T buscarPorId(ID id) throws ArchivoInvalidoException;

    /**
     * Devuelve todas las entidades almacenadas en el archivo.
     *
     * @return Lista con todas las entidades. Lista vacía si no hay datos.
     * @throws ArchivoInvalidoException si ocurre un error al leer el archivo.
     */
    List<T> listarTodos() throws ArchivoInvalidoException;

    /**
     * Actualiza los datos de una entidad existente en el archivo.
     * <p>
     * El proceso consiste en leer todas las líneas, reemplazar la línea que
     * coincide con el ID de la entidad y reescribir el archivo completo.
     * </p>
     *
     * @param entidad Objeto con los datos actualizados. Su ID debe existir.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir el
     * archivo.
     */
    void actualizar(T entidad) throws ArchivoInvalidoException;

    /**
     * Elimina una entidad del archivo según su identificador único.
     * <p>
     * El proceso consiste en leer todas las líneas, omitir la que coincide con
     * el ID y reescribir el archivo sin esa línea.
     * </p>
     *
     * @param id Identificador de la entidad a eliminar.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir el
     * archivo.
     */
    void eliminar(ID id) throws ArchivoInvalidoException;
}
