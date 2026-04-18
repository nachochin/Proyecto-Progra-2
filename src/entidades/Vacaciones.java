/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**
 * 
 * @author ekaro
 * @version 1.0
 */
public class Vacaciones {
    /** Identificador del empleado al que pertenece este saldo. */
    private String idEmpleado;

    /**
     * Total de días de vacaciones acumulados desde el ingreso
     * o desde el último reinicio de saldo (1 día por mes trabajado).
     */
    private double diasAcumulados;

    /**
     * Total de días de vacaciones ya consumidos (disfrutados o pagados).
     */
    private double diasConsumidos;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    //Constructor por defecto.
    public Vacaciones() {
    }

    /**
     * Constructor completo para inicializar el saldo de vacaciones.
     *
     * @param idEmpleado     Identificador del empleado.
     * @param diasAcumulados Días de vacaciones acumulados.
     * @param diasConsumidos Días de vacaciones ya consumidos.
     */
    public Vacaciones(String idEmpleado, double diasAcumulados, double diasConsumidos) {
        this.idEmpleado = idEmpleado;
        this.diasAcumulados = diasAcumulados;
        this.diasConsumidos = diasConsumidos;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /**Obtiene el identificador del empleado.
     * @return Identificador del empleado.
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**Establece el identificador del empleado.
     * @param idEmpleado Nuevo identificador.
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /*Obtiene los días de vacaciones acumulados.
     @return Días acumulados.
     */
    public double getDiasAcumulados() {
        return diasAcumulados;
    }

    /*Establece los días de vacaciones acumulados.
     * @param diasAcumulados Nuevo valor de días acumulados.
     */
    public void setDiasAcumulados(double diasAcumulados) {
        this.diasAcumulados = diasAcumulados;
    }

    /*Obtiene los días de vacaciones consumidos.
    
     * @return Días consumidos.
     */
    public double getDiasConsumidos() {
        return diasConsumidos;
    }

    /*Establece los días de vacaciones consumidos.
     * @param diasConsumidos Nuevo valor de días consumidos.
     */
    public void setDiasConsumidos(double diasConsumidos) {
        this.diasConsumidos = diasConsumidos;
    }

   /**
     * Calcula y devuelve los días de vacaciones restantes disponibles.
     * <p>
     * Este valor es calculado en tiempo real:
     * {@code diasRestantes = diasAcumulados - diasConsumidos}
     * </p>
     *
     * @return Días restantes disponibles para tomar.
     */
    public double getDiasRestantes() {
        return diasAcumulados - diasConsumidos;
    }

    /**Agrega días al contador de acumulados (llamado al procesar cada mes).
     * 
     * @param dias Cantidad de días a acumular (normalmente 1 por mes).
     */
    public void acumularDias(double dias) {
        this.diasAcumulados += dias;
    }

    /**Registra días de vacaciones consumidas por el empleado.
     * @param dias Cantidad de días consumidos.
     * @throws IllegalArgumentException si se intenta consumir más días de los disponibles.
     */
    public void consumirDias(double dias) {
        if (dias > getDiasRestantes()) {
            throw new IllegalArgumentException(
                "No hay suficientes días disponibles. Restantes: " + getDiasRestantes());
        }
        this.diasConsumidos += dias;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    /**
     * Representación en texto del saldo de vacaciones.
     * @return Cadena con el resumen del saldo vacacional.
     */
    @Override
    public String toString() {
        return "Vacaciones{idEmpleado='" + idEmpleado +
               "', acumulados=" + diasAcumulados +
               ", consumidos=" + diasConsumidos +
               ", restantes=" + getDiasRestantes() + "}";
    }
}
