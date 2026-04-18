/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;
import java.util.ArrayList;
import java.util.List;

/**
 * Almacena toda la configuración de porcentajes y parámetros de nómina.
 * <p>
 * Esta clase centraliza los porcentajes legales de Costa Rica para deducciones
 * del trabajador, aportes patronales y los tramos de renta. Al ser
 * configurable, permite actualizar los valores sin modificar la lógica de
 * cálculo cuando cambien las leyes.
 * </p>
 *
 * <h3>Cargas sociales del trabajador (~11.67%):</h3>
 * <ul>
 * <li>CCSS salud + pensiones: 9.17%</li>
 * <li>Régimen Obligatorio Pensiones (ROP trabajador): 1.0%</li>
 * <li>Banco Popular (trabajador): 1.0%</li>
 * </ul>
 *
 * <h3>Aportes patronales (~26.33% – 36%):</h3>
 * <ul>
 * <li>CCSS patronal (salud + pensiones): 26.33%</li>
 * <li>INA: 1.5%</li>
 * <li>Banco Popular patronal: 0.5%</li>
 * <li>Fondo de Capitalización Laboral (FCL): 3.0%</li>
 * <li>ROP patronal: 3.25%</li>
 * </ul>
 *
 * @author ekaro
 * @version 1.0
 */
public class ConfigNomina {
    // -------------------------------------------------------------------------
    // Cargas del trabajador
    // -------------------------------------------------------------------------

    /**
     * Porcentaje CCSS a cargo del trabajador (salud + invalidez + vejez).
     * Valor: 0.0917
     */
    private double porcCCSSTrabajador;

    /**
     * Porcentaje ROP a cargo del trabajador. Valor: 0.01
     */
    private double porcROPTrabajador;

    /**
     * Porcentaje Banco Popular a cargo del trabajador. Valor: 0.01
     */
    private double porcBancoPopularTrabajador;

    // -------------------------------------------------------------------------
    // Aportes patronales
    // -------------------------------------------------------------------------
    /**
     * Porcentaje CCSS patronal. Valor: 0.2633
     */
    private double porcCCSSPatronal;

    /**
     * Porcentaje INA patronal. Valor: 0.015
     */
    private double porcINAPatronal;

    /**
     * Porcentaje Banco Popular patronal. Valor: 0.005
     */
    private double porcBancoPopularPatronal;

    /**
     * Porcentaje Fondo de Capitalización Laboral (FCL). Valor: 0.03
     */
    private double porcFCL;

    /**
     * Porcentaje ROP patronal. Valor: 0.0325
     */
    private double porcROPPatronal;

    // -------------------------------------------------------------------------
    // Tramos de renta
    // -------------------------------------------------------------------------
    /**
     * Lista ordenada de tramos de impuesto sobre la renta. Deben estar
     * ordenados de menor a mayor límite inferior.
     */
    private List<TramoRenta> tramosRenta;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------
    /**
     * Constructor que inicializa la configuración con los valores legales
     * vigentes en Costa Rica.
     * <p>
     * Estos valores pueden ser sobreescritos cargando la configuración desde el
     * archivo {@code data/config_nomina.txt}.
     * </p>
     */
    public ConfigNomina() {
        this.porcCCSSTrabajador = 0.0917;
        this.porcROPTrabajador = 0.01;
        this.porcBancoPopularTrabajador = 0.01;

        this.porcCCSSPatronal = 0.2633;
        this.porcINAPatronal = 0.015;
        this.porcBancoPopularPatronal = 0.005;
        this.porcFCL = 0.03;
        this.porcROPPatronal = 0.0325;

        this.tramosRenta = new ArrayList<>();
        inicializarTramosDefecto();
    }

    /**
     * Inicializa la tabla de tramos de renta con los valores típicos vigentes
     * (base mensual en colones). Estos valores se reemplazarán si se carga el
     * archivo de configuración.
     */
    private void inicializarTramosDefecto() {
        tramosRenta.add(new TramoRenta(0, 941000, 0.00));
        tramosRenta.add(new TramoRenta(941000, 1381000, 0.10));
        tramosRenta.add(new TramoRenta(1381000, 2423000, 0.15));
        tramosRenta.add(new TramoRenta(2423000, 4845000, 0.20));
        tramosRenta.add(new TramoRenta(4845000, Double.MAX_VALUE, 0.25));
    }

    // -------------------------------------------------------------------------
    // Método utilitario: total cargas trabajador
    // -------------------------------------------------------------------------
    /**
     * Calcula el porcentaje total de cargas sociales a cargo del trabajador.
     * <p>
     * Equivale a: CCSS + ROP + Banco Popular
     * </p>
     *
     * @return Porcentaje total del trabajador (aprox. 0.1167).
     */
    public double getTotalCargasTrabajador() {
        return porcCCSSTrabajador + porcROPTrabajador + porcBancoPopularTrabajador;
    }

    /**
     * Calcula el porcentaje total de aportes patronales.
     * <p>
     * Equivale a: CCSS + INA + BP + FCL + ROP patronal
     * </p>
     *
     * @return Porcentaje total patronal (aprox. 0.3458).
     */
    public double getTotalAportesPatronales() {
        return porcCCSSPatronal + porcINAPatronal
                + porcBancoPopularPatronal + porcFCL + porcROPPatronal;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------
    /**
     * @return Porcentaje CCSS del trabajador.
     */
    public double getPorcCCSSTrabajador() {
        return porcCCSSTrabajador;
    }

    /**
     * @param v Nuevo porcentaje CCSS trabajador.
     */
    public void setPorcCCSSTrabajador(double v) {
        this.porcCCSSTrabajador = v;
    }

    /**
     * @return Porcentaje ROP del trabajador.
     */
    public double getPorcROPTrabajador() {
        return porcROPTrabajador;
    }

    /**
     * @param v Nuevo porcentaje ROP trabajador.
     */
    public void setPorcROPTrabajador(double v) {
        this.porcROPTrabajador = v;
    }

    /**
     * @return Porcentaje Banco Popular del trabajador.
     */
    public double getPorcBancoPopularTrabajador() {
        return porcBancoPopularTrabajador;
    }

    /**
     * @param v Nuevo porcentaje Banco Popular trabajador.
     */
    public void setPorcBancoPopularTrabajador(double v) {
        this.porcBancoPopularTrabajador = v;
    }

    /**
     * @return Porcentaje CCSS patronal.
     */
    public double getPorcCCSSPatronal() {
        return porcCCSSPatronal;
    }

    /**
     * @param v Nuevo porcentaje CCSS patronal.
     */
    public void setPorcCCSSPatronal(double v) {
        this.porcCCSSPatronal = v;
    }

    /**
     * @return Porcentaje INA patronal.
     */
    public double getPorcINAPatronal() {
        return porcINAPatronal;
    }

    /**
     * @param v Nuevo porcentaje INA patronal.
     */
    public void setPorcINAPatronal(double v) {
        this.porcINAPatronal = v;
    }

    /**
     * @return Porcentaje Banco Popular patronal.
     */
    public double getPorcBancoPopularPatronal() {
        return porcBancoPopularPatronal;
    }

    /**
     * @param v Nuevo porcentaje Banco Popular patronal.
     */
    public void setPorcBancoPopularPatronal(double v) {
        this.porcBancoPopularPatronal = v;
    }

    /**
     * @return Porcentaje FCL.
     */
    public double getPorcFCL() {
        return porcFCL;
    }

    /**
     * @param v Nuevo porcentaje FCL.
     */
    public void setPorcFCL(double v) {
        this.porcFCL = v;
    }

    /**
     * @return Porcentaje ROP patronal.
     */
    public double getPorcROPPatronal() {
        return porcROPPatronal;
    }

    /**
     * @param v Nuevo porcentaje ROP patronal.
     */
    public void setPorcROPPatronal(double v) {
        this.porcROPPatronal = v;
    }

    /**
     * Obtiene la lista de tramos de renta configurados.
     *
     * @return Lista de {@link TramoRenta}.
     */
    public List<TramoRenta> getTramosRenta() {
        return tramosRenta;
    }

    /**
     * Reemplaza la lista completa de tramos de renta.
     *
     * @param tramosRenta Nueva lista de tramos.
     */
    public void setTramosRenta(List<TramoRenta> tramosRenta) {
        this.tramosRenta = tramosRenta;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    /**
     * Representación en texto de la configuración de nómina.
     *
     * @return Cadena con los porcentajes principales configurados.
     */
    @Override
    public String toString() {
        return "ConfigNomina{"
                + "cargasTrabajador=" + String.format("%.2f%%", getTotalCargasTrabajador() * 100)
                + ", aportesPatronales=" + String.format("%.2f%%", getTotalAportesPatronales() * 100)
                + ", tramos=" + tramosRenta.size() + "}";
    }
}
