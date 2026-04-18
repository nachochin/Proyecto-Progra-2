/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import datos.VacacionesDAO;
import entidades.ConfigNomina;
import entidades.Empleado;
import entidades.Vacaciones;
import excepciones.ArchivoInvalidoException;
import excepciones.CalculoNominaException;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Gestiona el ciclo de vida de las vacaciones de un empleado.
 * <p>
 * Extiende {@link LogicaBase} e implementa la lógica de vacaciones según las
 * reglas de la empresa:
 * </p>
 * <ul>
 * <li>12 días hábiles de vacaciones por año (lunes a viernes).</li>
 * <li>Las vacaciones NO son acumulables entre años. Al iniciar un nuevo año
 * laboral el saldo se reinicia a 12 días disponibles.</li>
 * <li>Sábados y domingos NO cuentan como días de vacaciones.</li>
 * <li>El saldo máximo disponible en cualquier momento es 12 días.</li>
 * </ul>
 *
 * @author ekaro
 * @version 2.0
 */

public class GestionVacaciones extends LogicaBase {
 
    /** DAO para persistir cambios en el saldo vacacional. */
    private VacacionesDAO vacacionesDAO;
 
    /** Empleado sobre el que se opera. */
    private Empleado empleado;
 
    /**
     * Total de días hábiles de vacaciones que corresponden por año.
     * Fijo: 12 días de lunes a viernes.
     */
    public static final double DIAS_VACACIONES_ANUALES = 12.0;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Crea una instancia de gestión de vacaciones para un empleado.
     *
     * @param config        Configuración de nómina vigente.
     * @param empleado      Empleado sobre el que se gestionan las vacaciones.
     * @param vacacionesDAO DAO para leer y persistir el saldo vacacional.
     */
    public GestionVacaciones(ConfigNomina config, Empleado empleado,
                             VacacionesDAO vacacionesDAO) {
        super(config);
        this.empleado      = empleado;
        this.vacacionesDAO = vacacionesDAO;
    }
 
    // -------------------------------------------------------------------------
    // calcular — implementación de LogicaBase
    // -------------------------------------------------------------------------
 
    /**
     * Calcula el monto monetario estimado de los días de vacaciones restantes.
     * <p>
     * Fórmula: {@code diasRestantes × (salarioBase / 30)}
     * </p>
     *
     * @return Monto de pago de vacaciones disponibles en colones.
     */
    @Override
    public double calcular() {
        double salarioDiario = empleado.getSalarioBase() / 30.0;
        try {
            Vacaciones saldo = obtenerSaldo();
            return redondear(saldo.getDiasRestantes() * salarioDiario);
        } catch (ArchivoInvalidoException e) {
            return 0.0;
        }
    }
 
    // -------------------------------------------------------------------------
    // Operaciones principales
    // -------------------------------------------------------------------------
 
    /**
     * Reinicia el saldo de vacaciones al iniciar un nuevo año laboral.
     * <p>
     * Establece {@code diasAcumulados = 12} y {@code diasConsumidos = 0}.
     * Debe llamarse una vez al año, normalmente al procesar la primera
     * quincena de enero.
     * </p>
     *
     * @throws ArchivoInvalidoException si ocurre un error al actualizar el archivo.
     */
    public void reiniciarSaldoAnual() throws ArchivoInvalidoException {
        Vacaciones saldo = obtenerSaldo();
        saldo.setDiasAcumulados(DIAS_VACACIONES_ANUALES);
        saldo.setDiasConsumidos(0.0);
        vacacionesDAO.actualizar(saldo);
    }
 
    /**
     * Registra días hábiles de vacaciones consumidos por el empleado.
     * <p>
     * Valida que la fecha de inicio no sea sábado ni domingo.
     * Calcula cuántos días hábiles (lunes–viernes) hay entre
     * {@code fechaInicio} y {@code fechaFin} inclusive, y verifica
     * que no supere el saldo disponible.
     * </p>
     *
     * @param fechaInicio Primer día de vacaciones solicitado.
     * @param fechaFin    Último día de vacaciones solicitado.
     * @throws CalculoNominaException   si las fechas son inválidas, caen en fin
     *                                  de semana, o no hay días disponibles.
     * @throws ArchivoInvalidoException si ocurre un error al actualizar el archivo.
     */
    public void consumirVacaciones(LocalDate fechaInicio, LocalDate fechaFin)
            throws CalculoNominaException, ArchivoInvalidoException {
 
        validarFechasVacaciones(fechaInicio, fechaFin);
 
        double diasHabiles = contarDiasHabiles(fechaInicio, fechaFin);
 
        Vacaciones saldo = obtenerSaldo();
        if (diasHabiles > saldo.getDiasRestantes()) {
            throw new CalculoNominaException(
                "Días insuficientes. Disponibles: " + saldo.getDiasRestantes() +
                ", solicitados: " + diasHabiles);
        }
 
        saldo.consumirDias(diasHabiles);
        vacacionesDAO.actualizar(saldo);
    }
 
    /**
     * Registra una cantidad directa de días de vacaciones consumidos.
     * <p>
     * Usado cuando no se trabaja con fechas sino con un número de días ya
     * calculado externamente (ej. ajustes manuales).
     * </p>
     *
     * @param dias Cantidad de días hábiles a consumir.
     * @throws CalculoNominaException   si no hay suficientes días disponibles.
     * @throws ArchivoInvalidoException si ocurre un error al actualizar el archivo.
     */
    public void consumirVacaciones(double dias)
            throws CalculoNominaException, ArchivoInvalidoException {
        if (dias <= 0) {
            throw new CalculoNominaException(
                "La cantidad de días a consumir debe ser mayor a cero.");
        }
        Vacaciones saldo = obtenerSaldo();
        if (dias > saldo.getDiasRestantes()) {
            throw new CalculoNominaException(
                "Días insuficientes. Disponibles: " + saldo.getDiasRestantes() +
                ", solicitados: " + dias);
        }
        saldo.consumirDias(dias);
        vacacionesDAO.actualizar(saldo);
    }
 
    /**
     * Calcula el monto a pagar por un número específico de días de vacaciones.
     *
     * @param dias Cantidad de días de vacaciones a pagar.
     * @return Monto en colones correspondiente a los días indicados.
     */
    public double calcularPagoVacaciones(double dias) {
        double salarioDiario = empleado.getSalarioBase() / 30.0;
        return redondear(dias * salarioDiario);
    }
 
    /**
     * Inicializa el registro de vacaciones con 12 días disponibles
     * cuando se crea un empleado nuevo.
     *
     * @throws ArchivoInvalidoException si ocurre un error al escribir el archivo.
     */
    public void inicializarSaldo() throws ArchivoInvalidoException {
        Vacaciones saldoInicial = new Vacaciones(
            empleado.getId(), DIAS_VACACIONES_ANUALES, 0.0);
        vacacionesDAO.guardar(saldoInicial);
    }
 
    // -------------------------------------------------------------------------
    // Cálculo de días hábiles
    // -------------------------------------------------------------------------
 
    /**
     * Cuenta los días hábiles (lunes a viernes) entre dos fechas, inclusive.
     *
     * @param inicio Fecha de inicio del período.
     * @param fin    Fecha de fin del período.
     * @return Cantidad de días hábiles en el rango.
     */
    public double contarDiasHabiles(LocalDate inicio, LocalDate fin) {
        double count = 0;
        LocalDate fecha = inicio;
        while (!fecha.isAfter(fin)) {
            DayOfWeek dia = fecha.getDayOfWeek();
            if (dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY) {
                count++;
            }
            fecha = fecha.plusDays(1);
        }
        return count;
    }
 
    /**
     * Valida que las fechas de solicitud de vacaciones sean correctas.
     * <p>
     * Reglas:
     * <ul>
     *   <li>La fecha de inicio no puede ser posterior a la de fin.</li>
     *   <li>La fecha de inicio no puede ser sábado ni domingo.</li>
     *   <li>La fecha de fin no puede ser sábado ni domingo.</li>
     * </ul>
     * </p>
     *
     * @param inicio Fecha de inicio de las vacaciones.
     * @param fin    Fecha de fin de las vacaciones.
     * @throws CalculoNominaException si alguna regla no se cumple.
     */
    public void validarFechasVacaciones(LocalDate inicio, LocalDate fin)
            throws CalculoNominaException {
 
        if (inicio.isAfter(fin)) {
            throw new CalculoNominaException(
                "La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
 
        DayOfWeek diaInicio = inicio.getDayOfWeek();
        if (diaInicio == DayOfWeek.SATURDAY || diaInicio == DayOfWeek.SUNDAY) {
            throw new CalculoNominaException(
                "La fecha de inicio (" + inicio + ") cae en " +
                nombreDia(diaInicio) + ". Las vacaciones deben iniciar en día hábil.");
        }
 
        DayOfWeek diaFin = fin.getDayOfWeek();
        if (diaFin == DayOfWeek.SATURDAY || diaFin == DayOfWeek.SUNDAY) {
            throw new CalculoNominaException(
                "La fecha de fin (" + fin + ") cae en " +
                nombreDia(diaFin) + ". Las vacaciones deben terminar en día hábil.");
        }
    }
 
    /**
     * Devuelve el nombre en español del día de la semana.
     *
     * @param dia Día de la semana.
     * @return Nombre en español.
     */
    private String nombreDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY:    return "lunes";
            case TUESDAY:   return "martes";
            case WEDNESDAY: return "miércoles";
            case THURSDAY:  return "jueves";
            case FRIDAY:    return "viernes";
            case SATURDAY:  return "sábado";
            case SUNDAY:    return "domingo";
            default:        return dia.name();
        }
    }
 
    // -------------------------------------------------------------------------
    // Consultas
    // -------------------------------------------------------------------------
 
    /**
     * Obtiene el saldo actual de vacaciones del empleado desde el archivo.
     * Si el empleado no tiene registro previo, inicializa uno con 12 días.
     *
     * @return Saldo actual de vacaciones del empleado.
     * @throws ArchivoInvalidoException si ocurre un error al leer el archivo.
     */
    public Vacaciones obtenerSaldo() throws ArchivoInvalidoException {
        Vacaciones saldo = vacacionesDAO.buscarPorId(empleado.getId());
        if (saldo == null) {
            saldo = new Vacaciones(empleado.getId(), DIAS_VACACIONES_ANUALES, 0.0);
            vacacionesDAO.guardar(saldo);
        }
        return saldo;
    }
 
    /** @return Días acumulados, o 0.0 si ocurre un error. */
    public double getDiasAcumulados() {
        try { return obtenerSaldo().getDiasAcumulados(); }
        catch (ArchivoInvalidoException e) { return 0.0; }
    }
 
    /** @return Días consumidos, o 0.0 si ocurre un error. */
    public double getDiasConsumidos() {
        try { return obtenerSaldo().getDiasConsumidos(); }
        catch (ArchivoInvalidoException e) { return 0.0; }
    }
 
    /** @return Días restantes, o 0.0 si ocurre un error. */
    public double getDiasRestantes() {
        try { return obtenerSaldo().getDiasRestantes(); }
        catch (ArchivoInvalidoException e) { return 0.0; }
    }
 
    /** @return Empleado asociado a esta instancia. */
    public Empleado getEmpleado() { return empleado; }
 
    /** @param empleado Nuevo empleado. */
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}


 
   
