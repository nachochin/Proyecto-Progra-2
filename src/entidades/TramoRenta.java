/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/*** Representa un tramo del impuesto de renta según la legislación costarricense.
 * <p>
 * El impuesto se calcula por tramos progresivos. Cada tramo define un límite
 * inferior, un límite superior y el porcentaje que aplica al monto dentro
 * de ese rango.
 * </p>
 * <p>
 * Ejemplo de uso de tramos (valores 2024, configurables):
 * <ul>
 *   <li>Hasta ₡941,000 → 0% (exento)</li>
 *   <li>₡941,001 – ₡1,381,000 → 10%</li>
 *   <li>₡1,381,001 – ₡2,423,000 → 15%</li>
 *   <li>₡2,423,001 – ₡4,845,000 → 20%</li>
 *   <li>Más de ₡4,845,000 → 25%</li>
 *
 * @author ekaro
 * @version 1.0
 */
public class TramoRenta {
     /**
     * Límite inferior del tramo en colones (monto mínimo para que aplique).
     * El primer tramo siempre inicia en 0.
     */
    private double limiteInferior;

    /**
     * Límite superior del tramo en colones.
     * Para el último tramo usar {@code Double.MAX_VALUE} para indicar "sin límite".
     */
    private double limiteSuperior;

    /**
     * Porcentaje de impuesto que aplica dentro de este tramo.
     * Valor entre 0 y 1 (ej. 0.10 para 10%).
     */
    private double porcentaje;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Constructor por defecto.
     */
    public TramoRenta() {
    }

    /**
     * Constructor completo para definir un tramo de renta.
     *
     * @param limiteInferior Límite inferior del tramo en colones.
     * @param limiteSuperior Límite superior del tramo en colones.
     * @param porcentaje     Porcentaje de impuesto (0.0 a 1.0).
     */
    public TramoRenta(double limiteInferior, double limiteSuperior, double porcentaje) {
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.porcentaje = porcentaje;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /**
     * Obtiene el límite inferior del tramo.
     *
     * @return Límite inferior en colones.
     */
    public double getLimiteInferior() {
        return limiteInferior;
    }

    /**
     * Establece el límite inferior del tramo.
     *
     * @param limiteInferior Nuevo límite inferior en colones.
     */
    public void setLimiteInferior(double limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    /**
     * Obtiene el límite superior del tramo.
     *
     * @return Límite superior en colones.
     */
    public double getLimiteSuperior() {
        return limiteSuperior;
    }

    /**
     * Establece el límite superior del tramo.
     *
     * @param limiteSuperior Nuevo límite superior en colones.
     */
    public void setLimiteSuperior(double limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    /**
     * Obtiene el porcentaje de impuesto del tramo.
     *
     * @return Porcentaje como decimal (ej. 0.10 para 10%).
     */
    public double getPorcentaje() {
        return porcentaje;
    }

    /**
     * Establece el porcentaje de impuesto del tramo.
     *
     * @param porcentaje Nuevo porcentaje como decimal.
     */
    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    /**
     * Representación en texto del tramo de renta.
     *
     * @return Cadena con los límites y porcentaje del tramo.
     */
    @Override
    public String toString() {
        return "TramoRenta{inferior=" + limiteInferior +
               ", superior=" + limiteSuperior +
               ", porcentaje=" + (porcentaje * 100) + "%}";
    }
}
