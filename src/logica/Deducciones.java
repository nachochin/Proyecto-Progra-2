/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import entidades.ConfigNomina;
import entidades.TramoRenta;
import excepciones.CalculoNominaException;
/****
 * Calcula todas las deducciones a cargo del trabajador para una quincena.
 * <p>
 * Extiende {@link LogicaBase} y aplica las reglas de la legislación
 * costarricense:
 * <ul>
 *   <li>CCSS trabajador (~9.17% del salario bruto quincenal)</li>
 *   <li>ROP trabajador (~1% del salario bruto quincenal)</li>
 *   <li>Banco Popular trabajador (~1% del salario bruto quincenal)</li>
 *   <li>Impuesto de renta: calculado por tramos sobre la base imponible mensual</li>
 *   <li>Otras deducciones opcionales (tardías, incapacidades, etc.)</li>
 * </ul>
 * </p>
 *
 * <p><b>Nota sobre el impuesto de renta y quincenas:</b><br>
 * Los tramos de renta están definidos en base mensual. Para una nómina
 * quincenal se proyecta el salario a mensual, se calcula el impuesto
 * mensual y se divide entre 2 para obtener el monto de la quincena.
 * </p>
 *
 *
 * @author ekaro
 * @version 1.0
 */
public class Deducciones extends LogicaBase {

    /** Salario bruto quincenal sobre el que se calculan las deducciones. */
    private double salarioBruto;

    /** Monto de otras deducciones opcionales (tardías, incapacidades, etc.). */
    private double otrasDeducciones;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Crea una instancia de cálculo de deducciones para una quincena.
     *
     * @param config           Configuración con los porcentajes vigentes.
     * @param salarioBruto     Salario bruto quincenal del empleado.
     * @param otrasDeducciones Otras deducciones opcionales del período.
     */
    public Deducciones(ConfigNomina config, double salarioBruto, double otrasDeducciones) {
        super(config);
        this.salarioBruto      = salarioBruto;
        this.otrasDeducciones  = otrasDeducciones;
    }

    // -------------------------------------------------------------------------
    // calcular — implementación de LogicaBase
    // -------------------------------------------------------------------------

    /**
     * Calcula y devuelve el total de deducciones del trabajador para la quincena.
     * <p>
     * Suma: CCSS + ROP + Banco Popular + impuesto de renta + otras deducciones.
     * </p>
     *
     * @return Total de deducciones en colones, redondeado a 2 decimales.
     */
    @Override
    public double calcular() {
        return redondear(
            calcularCCSS() +
            calcularROP() +
            calcularBancoPopular() +
            calcularImpuestoRenta() +
            otrasDeducciones
        );
    }

    // -------------------------------------------------------------------------
    // Cálculos individuales
    // -------------------------------------------------------------------------

    /**
     * Calcula la deducción CCSS a cargo del trabajador.
     * <p>
     * Fórmula: {@code salarioBruto × porcCCSSTrabajador}
     * </p>
     *
     * @return Monto de deducción CCSS en colones.
     */
    public double calcularCCSS() {
        return redondear(salarioBruto * config.getPorcCCSSTrabajador());
    }

    /**
     * Calcula la deducción ROP (Régimen Obligatorio de Pensiones) del trabajador.
     * <p>
     * Fórmula: {@code salarioBruto × porcROPTrabajador}
     * </p>
     *
     * @return Monto de deducción ROP en colones.
     */
    public double calcularROP() {
        return redondear(salarioBruto * config.getPorcROPTrabajador());
    }

    /**
     * Calcula la deducción Banco Popular a cargo del trabajador.
     * <p>
     * Fórmula: {@code salarioBruto × porcBancoPopularTrabajador}
     * </p>
     *
     * @return Monto de deducción Banco Popular en colones.
     */
    public double calcularBancoPopular() {
        return redondear(salarioBruto * config.getPorcBancoPopularTrabajador());
    }

    /**
     * Calcula el impuesto de renta quincenal aplicando los tramos progresivos.
     * <p>
     * Proceso:
     * <ol>
     *   <li>Proyectar salario bruto quincenal a mensual (× 2).</li>
     *   <li>Calcular base imponible mensual:
     *       {@code baseImponible = salarioMensual × (1 - totalCargasTrabajador)}</li>
     *   <li>Aplicar tramos progresivos sobre la base imponible mensual.</li>
     *   <li>Dividir el impuesto mensual entre 2 para obtener el quincenal.</li>
     * </ol>
     * </p>
     *
     * @return Impuesto de renta quincenal en colones.
     */
    public double calcularImpuestoRenta() {
        double salarioMensual   = salarioBruto * 2;
        double baseImponible    = salarioMensual * (1 - config.getTotalCargasTrabajador());
        double impuestoMensual  = 0.0;

        for (TramoRenta tramo : config.getTramosRenta()) {
            if (baseImponible <= tramo.getLimiteInferior()) break;

            double montoEnTramo = Math.min(baseImponible, tramo.getLimiteSuperior())
                                  - tramo.getLimiteInferior();
            impuestoMensual += montoEnTramo * tramo.getPorcentaje();
        }

        return redondear(impuestoMensual / 2);
    }

    /**
     * Valida que el salario bruto sea válido para el cálculo.
     *
     * @throws CalculoNominaException si el salario bruto es cero o negativo.
     */
    public void validar() throws CalculoNominaException {
        if (salarioBruto <= 0) {
            throw new CalculoNominaException(
                "El salario bruto debe ser mayor a cero. Valor recibido: " + salarioBruto);
        }
        if (config.getTramosRenta().isEmpty()) {
            throw new CalculoNominaException(
                "No hay tramos de renta configurados. Verifique config_nomina.");
        }
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /**
     * Obtiene el salario bruto quincenal.
     *
     * @return Salario bruto quincenal en colones.
     */
    public double getSalarioBruto() {
        return salarioBruto;
    }

    /**
     * Establece el salario bruto quincenal.
     *
     * @param salarioBruto Nuevo salario bruto quincenal.
     */
    public void setSalarioBruto(double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    /**
     * Obtiene el monto de otras deducciones opcionales.
     *
     * @return Otras deducciones en colones.
     */
    public double getOtrasDeducciones() {
        return otrasDeducciones;
    }

    /**
     * Establece otras deducciones opcionales del período.
     *
     * @param otrasDeducciones Monto de otras deducciones en colones.
     */
    public void setOtrasDeducciones(double otrasDeducciones) {
        this.otrasDeducciones = otrasDeducciones;
    }
}

