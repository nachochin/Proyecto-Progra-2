/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;
import entidades.ConfigNomina;
/**
 ** Clase abstracta base para todas las clases de lógica de negocio del sistema.
 * <p>
 * Define el comportamiento común que comparten las clases de cálculo:
 * todas necesitan acceso a la configuración de nómina ({@link ConfigNomina})
 * y deben implementar su propio método {@code calcular()}.
 * </p>
 * <p>
 * Las subclases concretas son:
 * <ul>
 *   <li>{@code CalculoNomina} — orquesta el cálculo completo quincenal.</li>
 *   <li>{@code Deducciones} — calcula CCSS, renta y otras deducciones.</li>
 *   <li>{@code CargasPatronales} — calcula los aportes a cargo del patrono.</li>
 *   <li>{@code GestionVacaciones} — acumula y gestiona días de vacaciones.</li>
 * </ul>
 * </p>
 * @author ekaro
 * @Version 1.0
 */
public abstract class LogicaBase {
    /**
     * Configuración de porcentajes y tramos de renta usada en los cálculos.
     * Disponible para todas las subclases.
     */
    protected ConfigNomina config;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor que inyecta la configuración de nómina en la lógica.
     *
     * @param config Configuración con porcentajes y tramos vigentes.
     */
    public LogicaBase(ConfigNomina config) {
        this.config = config;
    }

    // -------------------------------------------------------------------------
    // Método abstracto
    // -------------------------------------------------------------------------

    /**
     * Método principal de cálculo que cada subclase debe implementar
     * con su lógica específica.
     * <p>
     * Cada subclase define qué parámetros necesita y qué devuelve,
     * pero todas comparten la obligación de exponer un método de cálculo.
     * </p>
     *
     * @return Resultado del cálculo como {@code double}.
     *         El significado del valor depende de la subclase.
     */
    public abstract double calcular();

    // -------------------------------------------------------------------------
    // Métodos comunes
    // -------------------------------------------------------------------------

    /**
     * Obtiene la configuración de nómina actual.
     *
     * @return Instancia de {@link ConfigNomina} con los porcentajes vigentes.
     */
    public ConfigNomina getConfig() {
        return config;
    }

    /**
     * Actualiza la configuración de nómina utilizada en los cálculos.
     *
     * @param config Nueva configuración de nómina.
     */
    public void setConfig(ConfigNomina config) {
        this.config = config;
    }

    /**
     * Redondea un valor monetario a dos decimales.
     * <p>
     * Método utilitario disponible para todas las subclases para
     * garantizar consistencia en el redondeo de montos en colones.
     * </p>
     *
     * @param valor Monto a redondear.
     * @return Valor redondeado a dos decimales.
     */
    protected double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
