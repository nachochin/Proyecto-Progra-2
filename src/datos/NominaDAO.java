/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import entidades.Nomina;
import excepciones.ArchivoInvalidoException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 ** Clase de acceso a datos para la entidad {@link Nomina}.
 * <p>
 * Lee y escribe el archivo {@code data/nominas.txt} usando
 * {@link BufferedReader} y {@link BufferedWriter}. Cada línea representa la
 * nómina de un empleado para una quincena específica. La clave única es:
 * {@code idEmpleado + año + mes + quincena}.
 * </p>
 * <pre>
 *   Formato de línea:
 *   idEmpleado|anio|mes|quincena|salarioBruto|deduccionCCSS|deduccionPension|
 *   deduccionBP|impuestoRenta|otrasDeducciones|totalDeducciones|aportesPatronales|
 *   salarioNeto|horasExtra
 *
 *   Ejemplo:
 *   001|2024|6|1|300000.0|27510.0|3000.0|3000.0|0.0|0.0|33510.0|103980.0|266490.0|0.0
 * </pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class NominaDAO implements ManejadorArchivo<Nomina, String> {

    /**
     * Ruta relativa al archivo de nóminas.
     */
    private static final String RUTA_ARCHIVO = "data/nominas.txt";

    /**
     * Separador de campos dentro de cada línea.
     */
    private static final String SEPARADOR = "|";

    /**
     * Número de campos esperados por línea.
     */
    private static final int CAMPOS_ESPERADOS = 14;

    // -------------------------------------------------------------------------
    // guardar
    // -------------------------------------------------------------------------
    /**
     * Agrega un nuevo registro de nómina al final del archivo.
     *
     * @param nomina Nómina a persistir.
     * @throws ArchivoInvalidoException si ocurre un error al escribir el
     * archivo.
     */
    @Override
    public void guardar(Nomina nomina) throws ArchivoInvalidoException {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(nominaALinea(nomina));
            bw.newLine();
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al guardar nomina en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // buscarPorId  (clave compuesta: "idEmpleado-anio-mes-quincena")
    // -------------------------------------------------------------------------
    /**
     * Busca una nómina por su clave compuesta.
     * <p>
     * El parámetro {@code id} debe tener el formato:
     * {@code "idEmpleado-anio-mes-quincena"}, por ejemplo:
     * {@code "001-2024-6-1"}.
     * </p>
     *
     * @param id Clave compuesta en formato
     * {@code idEmpleado-anio-mes-quincena}.
     * @return La nómina encontrada, o {@code null} si no existe.
     * @throws ArchivoInvalidoException si el archivo tiene un formato inválido.
     */
    @Override
    public Nomina buscarPorId(String id) throws ArchivoInvalidoException {
        String[] partes = id.split("-");
        if (partes.length != 4) {
            throw new ArchivoInvalidoException(
                    "ID invalido para buscar nomina. Formato esperado: idEmpleado-anio-mes-quincena");
        }
        String idEmp = partes[0];
        int anio = Integer.parseInt(partes[1]);
        int mes = Integer.parseInt(partes[2]);
        int quincena = Integer.parseInt(partes[3]);

        for (Nomina n : listarTodos()) {
            if (n.getIdEmpleado().equals(idEmp)
                    && n.getAnio() == anio
                    && n.getMes() == mes
                    && n.getQuincena() == quincena) {
                return n;
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // listarTodos
    // -------------------------------------------------------------------------
    /**
     * Lee todas las nóminas del archivo y las devuelve en una lista.
     *
     * @return Lista de todas las nóminas registradas.
     * @throws ArchivoInvalidoException si alguna línea tiene formato inválido.
     */
    @Override
    public List<Nomina> listarTodos() throws ArchivoInvalidoException {
        List<Nomina> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                lista.add(lineaANomina(linea));
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
    // listarPorEmpleado
    // -------------------------------------------------------------------------
    /**
     * Devuelve todas las nóminas de un empleado específico, ordenadas
     * cronológicamente (año → mes → quincena).
     *
     * @param idEmpleado Identificador del empleado.
     * @return Lista de nóminas del empleado. Lista vacía si no tiene registros.
     * @throws ArchivoInvalidoException si ocurre un error al leer el archivo.
     */
    public List<Nomina> listarPorEmpleado(String idEmpleado) throws ArchivoInvalidoException {
        List<Nomina> resultado = new ArrayList<>();
        for (Nomina n : listarTodos()) {
            if (n.getIdEmpleado().equals(idEmpleado)) {
                resultado.add(n);
            }
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // actualizar
    // -------------------------------------------------------------------------
    /**
     * Actualiza un registro de nómina existente identificado por la clave
     * compuesta {@code idEmpleado + anio + mes + quincena}.
     *
     * @param nomina Nómina con los datos actualizados.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir, o
     * si el registro no existe.
     */
    @Override
    public void actualizar(Nomina nomina) throws ArchivoInvalidoException {
        List<Nomina> lista = listarTodos();
        boolean encontrado = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Nomina n : lista) {
                if (esMismoPeriodo(n, nomina)) {
                    bw.write(nominaALinea(nomina));
                    encontrado = true;
                } else {
                    bw.write(nominaALinea(n));
                }
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al actualizar nomina en " + RUTA_ARCHIVO, e);
        }

        if (!encontrado) {
            throw new ArchivoInvalidoException(
                    "No se encontro nómina para actualizar: " + nomina);
        }
    }

    // -------------------------------------------------------------------------
    // eliminar
    // -------------------------------------------------------------------------
    /**
     * Elimina una nómina del archivo por su clave compuesta.
     *
     * @param id Clave compuesta en formato
     * {@code idEmpleado-anio-mes-quincena}.
     * @throws ArchivoInvalidoException si ocurre un error al leer o escribir.
     */
    @Override
    public void eliminar(String id) throws ArchivoInvalidoException {
        Nomina aEliminar = buscarPorId(id);
        if (aEliminar == null) {
            return;
        }

        List<Nomina> lista = listarTodos();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, false))) {
            for (Nomina n : lista) {
                if (!esMismoPeriodo(n, aEliminar)) {
                    bw.write(nominaALinea(n));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al eliminar nómina en " + RUTA_ARCHIVO, e);
        }
    }

    // -------------------------------------------------------------------------
    // Métodos privados de conversión
    // -------------------------------------------------------------------------
    /**
     * Convierte un objeto {@link Nomina} a una línea de texto con formato pipe.
     *
     * @param n Nómina a convertir.
     * @return Línea con los 14 campos separados por pipe.
     */
    private String nominaALinea(Nomina n) {
        return n.getIdEmpleado() + SEPARADOR
                + n.getAnio() + SEPARADOR
                + n.getMes() + SEPARADOR
                + n.getQuincena() + SEPARADOR
                + n.getSalarioBruto() + SEPARADOR
                + n.getDeduccionCCSS() + SEPARADOR
                + n.getDeduccionPension() + SEPARADOR
                + n.getDeduccionBancoPopular() + SEPARADOR
                + n.getImpuestoRenta() + SEPARADOR
                + n.getOtrasDeducciones() + SEPARADOR
                + n.getTotalDeducciones() + SEPARADOR
                + n.getAportesPatronales() + SEPARADOR
                + n.getSalarioNeto() + SEPARADOR
                + n.getHorasExtra();
    }

    /**
     * Convierte una línea del archivo en un objeto {@link Nomina}.
     *
     * @param linea Línea leída del archivo.
     * @return Nómina construida con los datos de la línea.
     * @throws ArchivoInvalidoException si el número de campos es incorrecto o
     * si algún valor numérico es inválido.
     */
    private Nomina lineaANomina(String linea) throws ArchivoInvalidoException {
        String[] c = linea.split("\\|");
        if (c.length != CAMPOS_ESPERADOS) {
            throw new ArchivoInvalidoException(
                    "Formato inválido en nominas.txt. Se esperaban "
                    + CAMPOS_ESPERADOS + " campos. Línea: " + linea);
        }
        try {
            return new Nomina(
                    c[0].trim(),
                    Integer.parseInt(c[1].trim()),
                    Integer.parseInt(c[2].trim()),
                    Integer.parseInt(c[3].trim()),
                    Double.parseDouble(c[4].trim()),
                    Double.parseDouble(c[5].trim()),
                    Double.parseDouble(c[6].trim()),
                    Double.parseDouble(c[7].trim()),
                    Double.parseDouble(c[8].trim()),
                    Double.parseDouble(c[9].trim()),
                    Double.parseDouble(c[10].trim()),
                    Double.parseDouble(c[11].trim()),
                    Double.parseDouble(c[12].trim()),
                    Double.parseDouble(c[13].trim())
            );
        } catch (NumberFormatException e) {
            throw new ArchivoInvalidoException(
                    "Valor numérico inválido en nominas.txt. Línea: " + linea, e);
        }
    }

    /**
     * Verifica si dos nóminas pertenecen al mismo período (mismo empleado, año,
     * mes y quincena).
     *
     * @param a Primera nómina.
     * @param b Segunda nómina.
     * @return {@code true} si son del mismo período.
     */
    private boolean esMismoPeriodo(Nomina a, Nomina b) {
        return a.getIdEmpleado().equals(b.getIdEmpleado())
                && a.getAnio() == b.getAnio()
                && a.getMes() == b.getMes()
                && a.getQuincena() == b.getQuincena();
    }
}
