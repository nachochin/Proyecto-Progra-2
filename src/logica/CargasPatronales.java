/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import entidades.ConfigNomina;
/**
 ** Calcula los aportes patronales para una quincena.
 * <p>
 * Extiende {@link LogicaBase}. Los aportes patronales NO se rebajan
 * al empleado, pero deben calcularse para el reporte del patrono.
 * Se calculan sobre el salario bruto quincenal.
 * </p>
 *
 * <h3>Aportes incluidos (~34% – 36% del salario bruto):</h3>
 * <ul>
 *   <li>CCSS patronal: ~26.33%</li>
 *   <li>INA: 1.5%</li>
 *   <li>Banco Popular patronal: 0.5%</li>
 *   <li>Fondo de Capitalización Laboral (FCL): 3.0%</li>
 *   <li>ROP patronal: 3.25%</li>
 * </ul>
 *
 * @author ekaro
 * @version 1.0
 */
public class CargasPatronales extends LogicaBase {

    /** Salario bruto quincenal sobre el que se calculan los aportes. */
    private double salarioBruto;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de cálculo de cargas patronales para una quincena.
     *
     * @param config       Configuración con los porcentajes patronales vigentes.
     * @param salarioBruto Salario bruto quincenal del empleado.
     */
    public CargasPatronales(ConfigNomina config, double salarioBruto) {
        super(config);
        this.salarioBruto = salarioBruto;
    }

    // -------------------------------------------------------------------------
    // calcular — implementación de LogicaBase
    // -------------------------------------------------------------------------

    /**
     * Calcula y devuelve el total de aportes patronales para la quincena.
     * <p>
     * Suma: CCSS + INA + Banco Popular + FCL + ROP patronal.
     * </p>
     *
     * @return Total de aportes patronales en colones, redondeado a 2 decimales.
     */
    @Override
    public double calcular() {
        return redondear(
            calcularCCSSPatronal() +
            calcularINA() +
            calcularBancoPopularPatronal() +
            calcularFCL() +
            calcularROPPatronal()
        );
    }

    // -------------------------------------------------------------------------
    // Cálculos individuales
    // -------------------------------------------------------------------------

    /**
     * Calcula el aporte CCSS a cargo del patrono.
     * <p>
     * Fórmula: {@code salarioBruto × porcCCSSPatronal}
     * </p>
     *
     * @return Monto de aporte CCSS patronal en colones.
     */
    public double calcularCCSSPatronal() {
        return redondear(salarioBruto * config.getPorcCCSSPatronal());
    }

    /**
     * Calcula el aporte al INA (Instituto Nacional de Aprendizaje).
     * <p>
     * Fórmula: {@code salarioBruto × porcINAPatronal}
     * </p>
     *
     * @return Monto de aporte INA en colones.
     */
    public double calcularINA() {
        return redondear(salarioBruto * config.getPorcINAPatronal());
    }

    /**
     * Calcula el aporte Banco Popular a cargo del patrono.
     * <p>
     * Fórmula: {@code salarioBruto × porcBancoPopularPatronal}
     * </p>
     *
     * @return Monto de aporte Banco Popular patronal en colones.
     */
    public double calcularBancoPopularPatronal() {
        return redondear(salarioBruto * config.getPorcBancoPopularPatronal());
    }

    /**
     * Calcula el aporte al Fondo de Capitalización Laboral (FCL).
     * <p>
     * Fórmula: {@code salarioBruto × porcFCL}
     * </p>
     *
     * @return Monto de aporte FCL en colones.
     */
    public double calcularFCL() {
        return redondear(salarioBruto * config.getPorcFCL());
    }

    /**
     * Calcula el aporte ROP (Régimen Obligatorio de Pensiones) patronal.
     * <p>
     * Fórmula: {@code salarioBruto × porcROPPatronal}
     * </p>
     *
     * @return Monto de aporte ROP patronal en colones.
     */
    public double calcularROPPatronal() {
        return redondear(salarioBruto * config.getPorcROPPatronal());
    }

    // -------------------------------------------------------------------------
    // Getter y Setter
    // -------------------------------------------------------------------------

    /**
     * Obtiene el salario bruto quincenal usado en los cálculos.
     *
     * @return Salario bruto quincenal en colones.
     */
    public double getSalarioBruto() {
        return salarioBruto;
    }

    /**
     * Establece el salario bruto quincenal.
     *
     * @param salarioBruto Nuevo salario bruto quincenal en colones.
     */
    public void setSalarioBruto(double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }
}

