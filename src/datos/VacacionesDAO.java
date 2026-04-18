/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import entidades.Vacaciones;
import excepciones.ArchivoInvalidoException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 ** Clase de acceso a datos para la entidad {@link Vacaciones}.
 * <p>
 * Lee y escribe el archivo {@code data/vacaciones.txt} usando
 * {@link BufferedReader} y {@link BufferedWriter}. Cada línea representa el
 * saldo vacacional de un empleado.
 * </p>
 * <pre>
 *   Formato de línea:
 *   idEmpleado|diasAcumulados|diasConsumidos
 *
 *   Ejemplo:
 *   001|5.0|2.0
 * </pre>
 * <p>
 * Los días restantes NO se almacenan en el archivo porque son calculados en
 * tiempo real por {@link Vacaciones#getDiasRestantes()}.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */

public class VacacionesDAO implements ManejadorArchivo<Vacaciones, String> {

    /**
     * Ruta relativa al archivo de vacaciones.
     */
    private static final String RUTA_ARCHIVO = "data/vacaciones.txt";

    /**
     * Separador de campos dentro de cada línea.
     */
    private static final String SEPARADOR = "|";

    /**
     * Número de campos esperados por línea.
     */
    private static final int CAMPOS_ESPERADOS = 3;

    // -------------------------------------------------------------------------
    // guardar
    // -------------------------------------------------------------------------
    /**
     * Agrega un nuevo registro de vacaciones al archivo.
     * <p>
     * Llamar este método solo cuando se registra un empleado nuevo. Para
     * actualizar el saldo usar {@link #actualizar(Vacaciones)}.
     * </p>
     *
     * @param vacaciones Saldo vacacional inicial del empleado.
     * @throws ArchivoInvalidoException si ocurre un error al escribir el
     * archivo.
     */
    @Override
    public void guardar(Vacaciones vacaciones) throws ArchivoInvalidoException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(vacacionesALinea(vacaciones));
            bw.newLine();
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al guardar vacaciones en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------
    /**
     * Busca el saldo de vacaciones de un empleado por su ID.
     *
     * @param idEmpleado Identificador del empleado.
     * @return El saldo de vacaciones del empleado, o {@code null} si no existe.
     * @throws ArchivoInvalidoException si el archivo tiene un formato inválido.
     */
    @Override
    public Vacaciones buscarPorId(String idEmpleado) throws ArchivoInvalidoException {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                Vacaciones v = lineaAVacaciones(linea);
                if (v.getIdEmpleado().equals(idEmpleado)) {
                    return v;
                }
            }
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al leer " + RUTA_ARCHIVO, e);
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // listarTodos
    // -------------------------------------------------------------------------
    /**
     * Devuelve el saldo de vacaciones de todos los empleados.
     *
     * @return Lista de saldos vacacionales.
     * @throws ArchivoInvalidoException si alguna línea tiene formato inválido.
     */
    @Override
    public List<Vacaciones> listarTodos() throws ArchivoInvalidoException {
        List<Vacaciones> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                lista.add(lineaAVacaciones(linea));
            }
        } catch (FileNotFoundException e) {
            return lista;
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al leer " + RUTA_ARCHIVO, e);
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // actualizar
    // -------------------------------------------------------------------------
    /**
     * Actualiza el saldo de vacaciones de un empleado en el archivo.
     * <p>
     * Se usa después de acumular días al procesar una quincena, o después de
     * que el empleado consume días de vacaciones.
     * </p>
     *
     * @param vacaciones Saldo actualizado del empleado.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir, o
     * si el empleado no tiene registro previo.
     */
    @Override
    public void actualizar(Vacaciones vacaciones) throws ArchivoInvalidoException {
        List<Vacaciones> lista = listarTodos();
        boolean encontrado = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Vacaciones v : lista) {
                if (v.getIdEmpleado().equals(vacaciones.getIdEmpleado())) {
                    bw.write(vacacionesALinea(vacaciones));
                    encontrado = true;
                } else {
                    bw.write(vacacionesALinea(v));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al actualizar vacaciones en " + RUTA_ARCHIVO, e);
        }

        if (!encontrado) {
            throw new ArchivoInvalidoException(
                    "No se encontró registro de vacaciones para el empleado: "
                    + vacaciones.getIdEmpleado());
        }
    }

    // -------------------------------------------------------------------------
    // eliminar
    // -------------------------------------------------------------------------
    /**
     * Elimina el registro de vacaciones de un empleado.
     * <p>
     * Normalmente se llama cuando se elimina un empleado del sistema.
     * </p>
     *
     * @param idEmpleado Identificador del empleado.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir.
     */
    @Override
    public void eliminar(String idEmpleado) throws ArchivoInvalidoException {
        List<Vacaciones> lista = listarTodos();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Vacaciones v : lista) {
                if (!v.getIdEmpleado().equals(idEmpleado)) {
                    bw.write(vacacionesALinea(v));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al eliminar vacaciones en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // Métodos privados de conversión
    // -------------------------------------------------------------------------
    /**
     * Convierte un objeto {@link Vacaciones} a una línea de texto con formato
     * pipe.
     * <p>
     * Solo se persisten acumulados y consumidos. Los restantes se calculan.
     * </p>
     *
     * @param v Vacaciones a convertir.
     * @return Línea con formato:
     * {@code idEmpleado|diasAcumulados|diasConsumidos}
     */
    private String vacacionesALinea(Vacaciones v) {
        return v.getIdEmpleado() + SEPARADOR
                + v.getDiasAcumulados() + SEPARADOR
                + v.getDiasConsumidos();
    }

    /**
     * Convierte una línea del archivo en un objeto {@link Vacaciones}.
     *
     * @param linea Línea leída del archivo.
     * @return Vacaciones construidas con los datos de la línea.
     * @throws ArchivoInvalidoException si la línea tiene formato incorrecto.
     */
    private Vacaciones lineaAVacaciones(String linea) throws ArchivoInvalidoException {
        String[] c = linea.split("\\|");
        if (c.length != CAMPOS_ESPERADOS) {
            throw new ArchivoInvalidoException(
                    "Formato inválido en vacaciones.txt. Se esperaban "
                    + CAMPOS_ESPERADOS + " campos. Línea: " + linea);
        }
        try {
            return new Vacaciones(
                    c[0].trim(),
                    Double.parseDouble(c[1].trim()),
                    Double.parseDouble(c[2].trim())
            );
        } catch (NumberFormatException e) {
            throw new ArchivoInvalidoException(
                    "Valor numérico inválido en vacaciones.txt. Línea: " + linea, e);
        }
    }
}
