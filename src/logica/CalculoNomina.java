/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import datos.NominaDAO;
import datos.VacacionesDAO;
import entidades.ConfigNomina;
import entidades.Empleado;
import entidades.Nomina;
import excepciones.ArchivoInvalidoException;
import excepciones.CalculoNominaException;

/**
 * Orquesta el cálculo completo de la nómina quincenal de un empleado.
 * <p>
 * Extiende {@link LogicaBase} y coordina las clases {@link Deducciones},
 * {@link CargasPatronales} y {@link GestionVacaciones} para producir un objeto
 * {@link Nomina} completamente calculado y persistido.
 * </p>
 *
 * <h3>Proceso de cálculo:</h3>
 * <ol>
 * <li>Validar datos de entrada (empleado, quincena, período).</li>
 * <li>Calcular salario bruto quincenal:
 * {@code salarioBase / 2 + horasExtra}.</li>
 * <li>Calcular deducciones del trabajador (CCSS, ROP, BP, renta, otras).</li>
 * <li>Calcular aportes patronales (CCSS, INA, BP, FCL, ROP).</li>
 * <li>Calcular salario neto: {@code salarioBruto - totalDeducciones}.</li>
 * <li>Acumular 0.5 días de vacaciones al empleado.</li>
 * <li>Persistir el registro de nómina en {@code data/nominas.txt}.</li>
 * </ol>
 *
 * @author ekaro
 * @version 1.0
 */
public class CalculoNomina extends LogicaBase {

    /**
     * Empleado al que se le calcula la nómina.
     */
    private Empleado empleado;

    /**
     * Año del período de nómina.
     */
    private int anio;

    /**
     * Mes del período de nómina (1-12).
     */
    private int mes;

    /**
     * Quincena del período (1 o 2).
     */
    private int quincena;

    /**
     * Monto de horas extra del período en colones.
     */
    private double horasExtra;

    /**
     * Otras deducciones opcionales del período (tardías, incapacidades).
     */
    private double otrasDeducciones;

    /**
     * DAO para persistir el registro de nómina calculado.
     */
    private NominaDAO nominaDAO;

    /**
     * DAO para actualizar el saldo de vacaciones del empleado.
     */
    private VacacionesDAO vacacionesDAO;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Crea una instancia completa de cálculo de nómina quincenal.
     *
     * @param config Configuración con porcentajes y tramos vigentes.
     * @param empleado Empleado al que se le calcula la nómina.
     * @param anio Año del período (ej. 2024).
     * @param mes Mes del período (1-12).
     * @param quincena Número de quincena (1 o 2).
     * @param horasExtra Monto en colones de horas extra del período.
     * @param otrasDeducciones Otras deducciones opcionales del período.
     * @param nominaDAO DAO para guardar el resultado.
     * @param vacacionesDAO DAO para actualizar vacaciones.
     */
    public CalculoNomina(ConfigNomina config, Empleado empleado,
            int anio, int mes, int quincena,
            double horasExtra, double otrasDeducciones,
            NominaDAO nominaDAO, VacacionesDAO vacacionesDAO) {
        super(config);
        this.empleado = empleado;
        this.anio = anio;
        this.mes = mes;
        this.quincena = quincena;
        this.horasExtra = horasExtra;
        this.otrasDeducciones = otrasDeducciones;
        this.nominaDAO = nominaDAO;
        this.vacacionesDAO = vacacionesDAO;
    }

    // -------------------------------------------------------------------------
    // calcular — implementación de LogicaBase
    // -------------------------------------------------------------------------
    /**
     * Calcula y devuelve el salario neto quincenal del empleado.
     * <p>
     * Este método solo realiza el cálculo matemático sin persistir ni acumular
     * vacaciones. Para el flujo completo usar {@link #procesarNomina()}.
     * </p>
     *
     * @return Salario neto quincenal en colones.
     */
    @Override
    public double calcular() {
        double salarioBruto = calcularSalarioBruto();
        Deducciones ded = new Deducciones(config, salarioBruto, otrasDeducciones);
        double totalDeducciones = ded.calcular();
        return redondear(salarioBruto - totalDeducciones);
    }

    // -------------------------------------------------------------------------
    // procesarNomina — flujo completo
    // -------------------------------------------------------------------------
    /**
     * Ejecuta el flujo completo: valida, calcula, acumula vacaciones y
     * persiste.
     * <p>
     * Este es el método principal que debe llamar la capa de presentación
     * cuando el usuario solicita procesar la nómina de un empleado.
     * </p>
     *
     * @return Objeto {@link Nomina} completamente calculado y guardado.
     * @throws CalculoNominaException si los datos de entrada son inválidos o si
     * la nómina ya fue procesada.
     * @throws ArchivoInvalidoException si ocurre un error al persistir los
     * datos.
     */
    public Nomina procesarNomina() throws CalculoNominaException, ArchivoInvalidoException {
        validar();

        // 1. Salario bruto quincenal
        double salarioBruto = calcularSalarioBruto();

        // 2. Deducciones del trabajador
        Deducciones ded = new Deducciones(config, salarioBruto, otrasDeducciones);
        ded.validar();
        double deduccionCCSS = ded.calcularCCSS();
        double deduccionROP = ded.calcularROP();
        double deduccionBP = ded.calcularBancoPopular();
        double impuestoRenta = ded.calcularImpuestoRenta();
        double totalDeducciones = ded.calcular();

        // 3. Aportes patronales (no se rebajan al empleado)
        CargasPatronales cp = new CargasPatronales(config, salarioBruto);
        double aportesPatronales = cp.calcular();

        // 4. Salario neto
        double salarioNeto = redondear(salarioBruto - totalDeducciones);

        // 5. Construir objeto Nomina
        Nomina nomina = new Nomina(
                empleado.getId(),
                anio, mes, quincena,
                salarioBruto,
                deduccionCCSS,
                deduccionROP,
                deduccionBP,
                impuestoRenta,
                otrasDeducciones,
                totalDeducciones,
                aportesPatronales,
                salarioNeto,
                horasExtra
        );

        // 6. Persistir nómina
        nominaDAO.guardar(nomina);

        // Nota: las vacaciones son 12 días fijos anuales (no acumulables por quincena).
        // El saldo se gestiona desde VistaVacaciones con reiniciarSaldoAnual().
        return nomina;
    }

    // -------------------------------------------------------------------------
    // Cálculos auxiliares
    // -------------------------------------------------------------------------
    /**
     * Calcula el salario bruto quincenal del empleado.
     * <p>
     * Fórmula: {@code (salarioBase / 2) + horasExtra}
     * </p>
     *
     * @return Salario bruto quincenal en colones.
     */
    public double calcularSalarioBruto() {
        return redondear((empleado.getSalarioBase() / 2.0) + horasExtra);
    }

    /**
     * Calcula el valor de una hora ordinaria del empleado.
     * <p>
     * Jornada laboral: 48 horas semanales, 52 semanas al año. Fórmula:
     * {@code (salarioBase × 12) / (52 semanas × 48 horas)} Se multiplica el
     * salario mensual × 12 para obtener el anual, y se divide entre el total de
     * horas laborales anuales.
     * </p>
     *
     * @return Valor de hora ordinaria en colones.
     */
    public double calcularValorHoraOrdinaria() {
        // Horas laborales anuales: 52 semanas × 48 horas = 2496 horas/año
        double horasAnuales = 52.0 * 48.0;
        double salarioAnual = empleado.getSalarioBase() * 12.0;
        return redondear(salarioAnual / horasAnuales);
    }

    /**
     * Calcula el valor de una hora extra (1.5× el valor de hora ordinaria).
     *
     * @return Valor de hora extra en colones.
     */
    public double calcularValorHoraExtra() {
        return redondear(calcularValorHoraOrdinaria() * 1.5);
    }

    /**
     * Calcula el valor de una hora en feriado o día de descanso (2× ordinaria).
     *
     * @return Valor de hora en feriado en colones.
     */
    public double calcularValorHoraFeriado() {
        return redondear(calcularValorHoraOrdinaria() * 2.0);
    }

    /**
     * Calcula el aguinaldo del empleado basándose en el promedio de salarios de
     * los últimos 12 meses.
     * <p>
     * Fórmula: {@code sumatoriaSalariosBrutos12Meses / 12}
     * </p>
     *
     * @param totalSalariosAnio Sumatoria de todos los salarios brutos de las
     * nóminas del año en curso.
     * @return Monto del aguinaldo en colones.
     */
    public double calcularAguinaldo(double totalSalariosAnio) {
        return redondear(totalSalariosAnio / 12.0);
    }

    // -------------------------------------------------------------------------
    // Validación
    // -------------------------------------------------------------------------
    /**
     * Valida que todos los datos de entrada sean correctos antes de calcular.
     *
     * @throws CalculoNominaException si algún dato es inválido o el período ya
     * fue procesado.
     * @throws ArchivoInvalidoException si ocurre un error al verificar nóminas
     * existentes.
     */
    public void validar() throws CalculoNominaException, ArchivoInvalidoException {
        if (empleado == null) {
            throw new CalculoNominaException("El empleado no puede ser nulo.");
        }
        if (empleado.getSalarioBase() <= 0) {
            throw new CalculoNominaException(
                    "El salario base debe ser mayor a cero. Empleado: " + empleado.getId());
        }
        if (quincena != 1 && quincena != 2) {
            throw new CalculoNominaException(
                    "La quincena debe ser 1 o 2. Valor recibido: " + quincena);
        }
        if (mes < 1 || mes > 12) {
            throw new CalculoNominaException(
                    "El mes debe estar entre 1 y 12. Valor recibido: " + mes);
        }
        if (horasExtra < 0) {
            throw new CalculoNominaException(
                    "El monto de horas extra no puede ser negativo.");
        }
        if (otrasDeducciones < 0) {
            throw new CalculoNominaException(
                    "Las otras deducciones no pueden ser negativas.");
        }

        // Verificar que la nómina no haya sido procesada ya
        String claveNomina = empleado.getId() + "-" + anio + "-" + mes + "-" + quincena;
        Nomina existente = nominaDAO.buscarPorId(claveNomina);
        if (existente != null) {
            throw new CalculoNominaException(
                    "La nómina del período " + mes + "/" + anio
                    + " quincena " + quincena + " ya fue procesada para el empleado "
                    + empleado.getNombre() + ".");
        }
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------
    /**
     * @return Empleado al que se le calcula la nómina.
     */
    public Empleado getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado Nuevo empleado.
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    /**
     * @return Año del período.
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio Nuevo año del período.
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return Mes del período.
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes Nuevo mes del período.
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return Quincena del período (1 o 2).
     */
    public int getQuincena() {
        return quincena;
    }

    /**
     * @param quincena Nueva quincena (1 o 2).
     */
    public void setQuincena(int quincena) {
        this.quincena = quincena;
    }

    /**
     * @return Monto de horas extra del período.
     */
    public double getHorasExtra() {
        return horasExtra;
    }

    /**
     * @param horasExtra Nuevo monto de horas extra.
     */
    public void setHorasExtra(double horasExtra) {
        this.horasExtra = horasExtra;
    }

    /**
     * @return Otras deducciones del período.
     */
    public double getOtrasDeducciones() {
        return otrasDeducciones;
    }

    /**
     * @param otrasDeducciones Nuevas otras deducciones.
     */
    public void setOtrasDeducciones(double otrasDeducciones) {
        this.otrasDeducciones = otrasDeducciones;
    }
}
