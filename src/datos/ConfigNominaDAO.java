/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import entidades.ConfigNomina;
import entidades.TramoRenta;
import excepciones.ArchivoInvalidoException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *Clase de acceso a datos para {@link ConfigNomina} y {@link TramoRenta}.
 * <p>
 * Maneja dos archivos de configuración:
 * <ul>
 * <li>{@code data/config_nomina.txt} — porcentajes de CCSS, INA, FCL, etc.</li>
 * <li>{@code data/tramos_renta.txt} — tabla de tramos del impuesto de
 * renta.</li>
 * </ul>
 * </p>
 *
 * <pre>
 *   Formato config_nomina.txt (clave=valor):
 *   ccss_trabajador=0.0917
 *   rop_trabajador=0.01
 *   bp_trabajador=0.01
 *   ccss_patronal=0.2633
 *   ina_patronal=0.015
 *   bp_patronal=0.005
 *   fcl=0.03
 *   rop_patronal=0.0325
 *
 *   Formato tramos_renta.txt (limiteInferior|limiteSuperior|porcentaje):
 *   0.0|941000.0|0.0
 *   941000.0|1381000.0|0.10
 *   1381000.0|2423000.0|0.15
 *   2423000.0|4845000.0|0.20
 *   4845000.0|1.7976931348623157E308|0.25
 * </pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class ConfigNominaDAO {

    /**
     * Ruta al archivo de porcentajes de nómina.
     */
    private static final String RUTA_CONFIG = "data/config_nomina.txt";

    /**
     * Ruta al archivo de tramos de renta.
     */
    private static final String RUTA_TRAMOS = "data/tramos_renta.txt";

    /**
     * Separador de campos para los tramos de renta.
     */
    private static final String SEPARADOR = "|";

    // -------------------------------------------------------------------------
    // cargarConfig
    // -------------------------------------------------------------------------
    /**
     * Lee ambos archivos de configuración y devuelve un objeto
     * {@link ConfigNomina} completamente inicializado.
     * <p>
     * Si alguno de los archivos no existe, se devuelve la configuración con los
     * valores por defecto definidos en {@link ConfigNomina#ConfigNomina()}.
     * </p>
     *
     * @return Configuración de nómina cargada desde los archivos.
     * @throws ArchivoInvalidoException si algún archivo tiene formato
     * incorrecto.
     */
    public ConfigNomina cargarConfig() throws ArchivoInvalidoException {
        ConfigNomina config = new ConfigNomina();
        cargarPorcentajes(config);
        cargarTramosRenta(config);
        return config;
    }

    // -------------------------------------------------------------------------
    // guardarConfig
    // -------------------------------------------------------------------------
    /**
     * Persiste los porcentajes y tramos de renta en sus respectivos archivos.
     * <p>
     * Reescribe ambos archivos completamente con los valores actuales del
     * objeto {@link ConfigNomina} recibido.
     * </p>
     *
     * @param config Configuración a guardar.
     * @throws ArchivoInvalidoException si ocurre un error al escribir.
     */
    public void guardarConfig(ConfigNomina config) throws ArchivoInvalidoException {
        guardarPorcentajes(config);
        guardarTramosRenta(config.getTramosRenta());
    }

    // -------------------------------------------------------------------------
    // Métodos privados — porcentajes
    // -------------------------------------------------------------------------
    /**
     * Lee el archivo {@code config_nomina.txt} y carga los porcentajes en el
     * objeto {@link ConfigNomina} recibido.
     *
     * @param config Objeto a poblar con los porcentajes leídos.
     * @throws ArchivoInvalidoException si el archivo tiene un formato inválido.
     */
    private void cargarPorcentajes(ConfigNomina config) throws ArchivoInvalidoException {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_CONFIG))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split("=");
                if (partes.length != 2) {
                    continue;
                }
                String clave = partes[0].trim();
                double valor = Double.parseDouble(partes[1].trim());
                asignarPorcentaje(config, clave, valor);
            }
        } catch (FileNotFoundException e) {
            // Si no existe el archivo se usan los valores por defecto del constructor
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al leer " + RUTA_CONFIG, e);
        } catch (NumberFormatException e) {
            throw new ArchivoInvalidoException(
                    "Valor numérico inválido en " + RUTA_CONFIG, e);
        }
    }

    /**
     * Asigna un porcentaje al campo correspondiente de {@link ConfigNomina}
     * según la clave leída del archivo.
     *
     * @param config Objeto de configuración a modificar.
     * @param clave Nombre de la clave leída (ej. "ccss_trabajador").
     * @param valor Valor decimal del porcentaje (ej. 0.0917).
     */
    private void asignarPorcentaje(ConfigNomina config, String clave, double valor) {
        switch (clave) {
            case "ccss_trabajador":
                config.setPorcCCSSTrabajador(valor);
                break;
            case "rop_trabajador":
                config.setPorcROPTrabajador(valor);
                break;
            case "bp_trabajador":
                config.setPorcBancoPopularTrabajador(valor);
                break;
            case "ccss_patronal":
                config.setPorcCCSSPatronal(valor);
                break;
            case "ina_patronal":
                config.setPorcINAPatronal(valor);
                break;
            case "bp_patronal":
                config.setPorcBancoPopularPatronal(valor);
                break;
            case "fcl":
                config.setPorcFCL(valor);
                break;
            case "rop_patronal":
                config.setPorcROPPatronal(valor);
                break;
        }
    }

    /**
     * Escribe los porcentajes de nómina en {@code config_nomina.txt}.
     *
     * @param config Configuración con los porcentajes a persistir.
     * @throws ArchivoInvalidoException si ocurre un error al escribir.
     */
    private void guardarPorcentajes(ConfigNomina config) throws ArchivoInvalidoException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_CONFIG, false))) {
            bw.write("# Porcentajes de nómina — Sistema de Nómina CR");
            bw.newLine();
            bw.write("ccss_trabajador=" + config.getPorcCCSSTrabajador());
            bw.newLine();
            bw.write("rop_trabajador=" + config.getPorcROPTrabajador());
            bw.newLine();
            bw.write("bp_trabajador=" + config.getPorcBancoPopularTrabajador());
            bw.newLine();
            bw.write("ccss_patronal=" + config.getPorcCCSSPatronal());
            bw.newLine();
            bw.write("ina_patronal=" + config.getPorcINAPatronal());
            bw.newLine();
            bw.write("bp_patronal=" + config.getPorcBancoPopularPatronal());
            bw.newLine();
            bw.write("fcl=" + config.getPorcFCL());
            bw.newLine();
            bw.write("rop_patronal=" + config.getPorcROPPatronal());
            bw.newLine();
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al guardar configuración en " + RUTA_CONFIG, e);
        }
    }

    // -------------------------------------------------------------------------
    // Métodos privados — tramos de renta
    // -------------------------------------------------------------------------
    /**
     * Lee el archivo {@code tramos_renta.txt} y carga los tramos en el objeto
     * {@link ConfigNomina}.
     *
     * @param config Objeto a poblar con los tramos leídos.
     * @throws ArchivoInvalidoException si el archivo tiene un formato inválido.
     */
    private void cargarTramosRenta(ConfigNomina config) throws ArchivoInvalidoException {
        List<TramoRenta> tramos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_TRAMOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("#")) {
                    continue;
                }
                String[] c = linea.split("\\|");
                if (c.length != 3) {
                    throw new ArchivoInvalidoException(
                            "Formato inválido en tramos_renta.txt. Línea: " + linea);
                }
                tramos.add(new TramoRenta(
                        Double.parseDouble(c[0].trim()),
                        Double.parseDouble(c[1].trim()),
                        Double.parseDouble(c[2].trim())
                ));
            }
            if (!tramos.isEmpty()) {
                config.setTramosRenta(tramos);
            }
        } catch (FileNotFoundException e) {
            // Si no existe el archivo se usan los tramos por defecto
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al leer " + RUTA_TRAMOS, e);
        } catch (NumberFormatException e) {
            throw new ArchivoInvalidoException(
                    "Valor numérico inválido en " + RUTA_TRAMOS, e);
        }
    }

    /**
     * Escribe los tramos de renta en {@code tramos_renta.txt}.
     *
     * @param tramos Lista de tramos a persistir.
     * @throws ArchivoInvalidoException si ocurre un error al escribir.
     */
    private void guardarTramosRenta(List<TramoRenta> tramos) throws ArchivoInvalidoException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_TRAMOS, false))) {
            bw.write("# Tramos de renta — limiteInferior|limiteSuperior|porcentaje");
            bw.newLine();
            for (TramoRenta t : tramos) {
                bw.write(t.getLimiteInferior() + SEPARADOR
                        + t.getLimiteSuperior() + SEPARADOR
                        + t.getPorcentaje());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al guardar tramos de renta en " + RUTA_TRAMOS, e);
        }
    }
}
