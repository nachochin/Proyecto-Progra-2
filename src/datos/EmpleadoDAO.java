/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;
import entidades.Empleado;
import excepciones.ArchivoInvalidoException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 ** Clase de acceso a datos para la entidad {@link Empleado}.
 * <p>
 * Lee y escribe el archivo {@code data/empleados.txt} usando
 * {@link BufferedReader} y {@link BufferedWriter}.
 * Cada línea del archivo representa un empleado con el formato:
 * </p>
 * <pre>
 *   id|nombre|correo|salarioBase|tipoContrato|fechaIngreso
 *   Ejemplo:
 *   001|Juan Pérez|juan@empresa.com|600000.0|TIEMPO_COMPLETO|2023-01-15
 * </pre>
 * @author ekaro
 * @version 1.0
 */
public class EmpleadoDAO implements ManejadorArchivo<Empleado, String> {
   /** Ruta relativa al archivo de empleados. */
    private static final String RUTA_ARCHIVO = "data/empleados.txt";

    /** Separador de campos dentro de cada línea. */
    private static final String SEPARADOR = "|";

    /** Número de campos esperados por línea para validar el formato. */
    private static final int CAMPOS_ESPERADOS = 6;

    // -------------------------------------------------------------------------
    // guardar
    // -------------------------------------------------------------------------

    /**
     * Agrega un nuevo empleado al final del archivo {@code empleados.txt}.
     *
     * @param empleado Empleado a persistir.
     * @throws ArchivoInvalidoException si ocurre un error al escribir el archivo.
     */
    @Override
    public void guardar(Empleado empleado) throws ArchivoInvalidoException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(empleadoALinea(empleado));
            bw.newLine();
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                "Error al guardar empleado en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // buscarPorId
    // -------------------------------------------------------------------------

    /**
     * Busca un empleado por su identificador único en el archivo.
     *
     * @param id Identificador del empleado a buscar.
     * @return El empleado encontrado, o {@code null} si no existe.
     * @throws ArchivoInvalidoException si el archivo tiene un formato inválido
     *                                  o no puede ser leído.
     */
    @Override
    public Empleado buscarPorId(String id) throws ArchivoInvalidoException {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                Empleado empleado = lineaAEmpleado(linea);
                if (empleado.getId().equals(id)) {
                    return empleado;
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
     * Lee todos los empleados del archivo y los devuelve en una lista.
     *
     * @return Lista de todos los empleados registrados.
     *         Lista vacía si el archivo no existe o está vacío.
     * @throws ArchivoInvalidoException si alguna línea tiene formato inválido.
     */
    @Override
    public List<Empleado> listarTodos() throws ArchivoInvalidoException {
        List<Empleado> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                lista.add(lineaAEmpleado(linea));
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
     * Actualiza los datos de un empleado existente en el archivo.
     * <p>
     * Lee todas las líneas, reemplaza la que coincide con el ID
     * del empleado y reescribe el archivo completo.
     * </p>
     *
     * @param empleado Empleado con los datos actualizados. Su ID debe existir.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir.
     */
    @Override
    public void actualizar(Empleado empleado) throws ArchivoInvalidoException {
        List<Empleado> lista = listarTodos();
        boolean encontrado = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Empleado e : lista) {
                if (e.getId().equals(empleado.getId())) {
                    bw.write(empleadoALinea(empleado));
                    encontrado = true;
                } else {
                    bw.write(empleadoALinea(e));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                "Error al actualizar empleado en " + RUTA_ARCHIVO, e);
        }

        if (!encontrado) {
            throw new ArchivoInvalidoException(
                "No se encontró el empleado con ID: " + empleado.getId());
        }
    }

    // -------------------------------------------------------------------------
    // eliminar
    // -------------------------------------------------------------------------

    /**
     * Elimina un empleado del archivo según su identificador.
     * <p>
     * Lee todas las líneas, omite la que coincide con el ID
     * y reescribe el archivo sin esa línea.
     * </p>
     *
     * @param id Identificador del empleado a eliminar.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir.
     */
    @Override
    public void eliminar(String id) throws ArchivoInvalidoException {
        List<Empleado> lista = listarTodos();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Empleado e : lista) {
                if (!e.getId().equals(id)) {
                    bw.write(empleadoALinea(e));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                "Error al eliminar empleado en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // Métodos privados de conversión
    // -------------------------------------------------------------------------

    /**
     * Convierte un objeto {@link Empleado} a una línea de texto con formato pipe.
     *
     * @param e Empleado a convertir.
     * @return Línea con formato: {@code id|nombre|correo|salarioBase|tipoContrato|fechaIngreso}
     */
    private String empleadoALinea(Empleado e) {
        return e.getId() + SEPARADOR +
               e.getNombre() + SEPARADOR +
               e.getCorreo() + SEPARADOR +
               e.getSalarioBase() + SEPARADOR +
               e.getTipoContrato() + SEPARADOR +
               e.getFechaIngreso().toString();
    }

    /**
     * Convierte una línea de texto del archivo en un objeto {@link Empleado}.
     *
     * @param linea Línea leída del archivo con formato pipe.
     * @return Empleado construido con los datos de la línea.
     * @throws ArchivoInvalidoException si la línea no tiene el número de campos esperado
     *                                  o si algún dato tiene un tipo incorrecto.
     */
    private Empleado lineaAEmpleado(String linea) throws ArchivoInvalidoException {
        String[] campos = linea.split("\\|");
        if (campos.length != CAMPOS_ESPERADOS) {
            throw new ArchivoInvalidoException(
                "Formato inválido en empleados.txt. Se esperaban " +
                CAMPOS_ESPERADOS + " campos, se encontraron " +
                campos.length + ". Línea: " + linea);
        }
        try {
            String id           = campos[0].trim();
            String nombre       = campos[1].trim();
            String correo       = campos[2].trim();
            double salarioBase  = Double.parseDouble(campos[3].trim());
            String tipoContrato = campos[4].trim();
            LocalDate fecha     = LocalDate.parse(campos[5].trim());

            return new Empleado(id, nombre, correo, salarioBase, tipoContrato, fecha);
        } catch (NumberFormatException e) {
            throw new ArchivoInvalidoException(
                "Salario base inválido en empleados.txt. Línea: " + linea, e);
        } catch (Exception e) {
            throw new ArchivoInvalidoException(
                "Error al parsear fecha en empleados.txt. Línea: " + linea, e);
        }
    }
}
