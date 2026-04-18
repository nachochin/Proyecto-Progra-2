/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import java.time.LocalDate;

/** Representa a un empleado de la empresa.
 * <p>
 * Contiene todos los datos personales y laborales necesarios para
 * el cálculo de nómina, vacaciones y reportes.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public class Empleado {

    //Identificador unico del empleado.
    private String id;

    //Nombre completo del empleado.
    private String nombre;

    //Correo electronico institucional.
    private String correo;

    //Salario base mensual en colones.
    private double salarioBase;

    /**
     * Tipo de contrato del empleado. Valores posibles: TIEMPO_COMPLETO,
     * MEDIO_TIEMPO.
     */
    private String tipoContrato;

    /**
     * Fecha de ingreso a la empresa.
     */
    private LocalDate fechaIngreso;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------
    /**
     * Constructor por defecto requerido para instanciación genérica.
     */
    public Empleado() {
    }

    /*Constructor completo para crear un empleado con todos sus datos.
     *@param es una etiqueta de Javadoc que documenta un parámetro de un método.
     * @param id Identificador único del empleado.
     * @param nombre Nombre completo.
     * @param correo Correo electrónico.
     * @param salarioBase Salario base mensual en colones.
     * @param tipoContrato Tipo de contrato (TIEMPO_COMPLETO / MEDIO_TIEMPO).
     * @param fechaIngreso Fecha de ingreso a la empresa.
     */
    public Empleado(String id, String nombre, String correo,
            double salarioBase, String tipoContrato, LocalDate fechaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.salarioBase = salarioBase;
        this.tipoContrato = tipoContrato;
        this.fechaIngreso = fechaIngreso;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------
    /**
     * Obtiene el identificador del empleado.
     *
     * @return id del empleado.
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador del empleado.
     *
     * @param id Nuevo identificador.
     */
    public void setId(String id) {
        this.id = id;
    }

    /*Obtiene el nombre completo del empleado.
     * @return Nombre completo.
     */
    public String getNombre() {
        return nombre;
    }

    /*Establece el nombre completo del empleado.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /*Obtiene el correo electrónico del empleado.
     * @return Correo electrónico.
     */
    public String getCorreo() {
        return correo;
    }

    /*Establece el correo electrónico del empleado.
     * @param correo Nuevo correo electrónico.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /*Obtiene el salario base mensual del empleado en colones.
     * @return Salario base.
     */
    public double getSalarioBase() {
        return salarioBase;
    }

    /*Establece el salario base mensual del empleado.
     * @param salarioBase Nuevo salario base en colones.
     */
    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    /*Obtiene el tipo de contrato del empleado
     * @return Tipo de contrato.
     */
    public String getTipoContrato() {
        return tipoContrato;
    }

    /*Establece el tipo de contrato del empleado.
     * @param tipoContrato Nuevo tipo de contrato.
     */
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    /*Obtiene la fecha de ingreso del empleado.
     * @return Fecha de ingreso.
     */
    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    /*Establece la fecha de ingreso del empleado.
     * @param fechaIngreso Nueva fecha de ingreso.
     */
    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------
    /*Representación en texto del empleado, útil para depuración.
     * @return Cadena con los datos principales del empleado.
     */
    @Override
    public String toString() {
        return "Empleado{id='" + id + "', nombre='" + nombre
                + "', salarioBase=" + salarioBase
                + ", tipoContrato='" + tipoContrato
                + "', fechaIngreso=" + fechaIngreso + "}";
    }
}
