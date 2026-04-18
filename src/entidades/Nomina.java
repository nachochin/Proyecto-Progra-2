/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

/**Representa el registro de nómina de un empleado para una quincena específica.
 * <p>
 * Almacena todos los montos calculados para un período: salario bruto,
 * deducciones al trabajador, aportes patronales y salario neto.
 * La clave única de un registro es la combinación de
 * {@code idEmpleado + anio + quincena}.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public class Nomina {
    /** Identificador del empleado al que pertenece esta nómina. */
    private String idEmpleado;

    /** Año del período de nómina (ej. 2024). */
    private int anio;

    /**
     * Número de quincena dentro del mes.
     * Valores válidos: 1 (días 1-15) o 2 (días 16-fin de mes).
     */
    private int quincena;

    /** Mes del período (1 = enero, 12 = diciembre). */
    private int mes;

    /** Salario bruto quincenal antes de deducciones (salarioBase / 2). */
    private double salarioBruto;

    /** Deducción por carga social CCSS (trabajador ~9.17%). */
    private double deduccionCCSS;

    /** Deducción por Fondo de Pensiones (trabajador ~1%). */
    private double deduccionPension;

    /** Deducción por Banco Popular (trabajador ~1%). */
    private double deduccionBancoPopular;

    /** Impuesto de renta calculado por tramos sobre la base imponible. */
    private double impuestoRenta;

    /** Otras deducciones (tardías, incapacidades, etc.). */
    private double otrasdeducciones;

    /** Suma total de todas las deducciones aplicadas al trabajador. */
    private double totalDeducciones;

    /** Aporte patronal total calculado (~26.33% CCSS + INA + FCL + ROP + BP). */
    private double aportesPatronales;

    /** Salario neto a pagar: salarioBruto - totalDeducciones. */
    private double salarioNeto;

    /** Monto de horas extra pagadas en este período. */
    private double horasExtra;

    // -------------------------------------------------------------------------
    // Constructores
    // -------------------------------------------------------------------------

    /**
     * Constructor por defecto.
     */
    public Nomina() {
    }

    /**Constructor completo para crear un registro de nómina quincenal.
     *
     * @param idEmpleado         Identificador del empleado.
     * @param anio               Año del período.
     * @param mes                Mes del período (1-12).
     * @param quincena           Quincena (1 o 2).
     * @param salarioBruto       Salario bruto quincenal.
     * @param deduccionCCSS      Deducción CCSS del trabajador.
     * @param deduccionPension   Deducción por pensión del trabajador.
     * @param deduccionBancoPopular Deducción Banco Popular.
     * @param impuestoRenta      Impuesto de renta calculado.
     * @param otrasdeducciones   Otras deducciones del período.
     * @param totalDeducciones   Total de deducciones al trabajador.
     * @param aportesPatronales  Total de aportes patronales.
     * @param salarioNeto        Salario neto a pagar.
     * @param horasExtra         Monto de horas extra del período.
     */
    public Nomina(String idEmpleado, int anio, int mes, int quincena,
                  double salarioBruto, double deduccionCCSS,
                  double deduccionPension, double deduccionBancoPopular,
                  double impuestoRenta, double otrasdeducciones,
                  double totalDeducciones, double aportesPatronales,
                  double salarioNeto, double horasExtra) {
        this.idEmpleado = idEmpleado;
        this.anio = anio;
        this.mes = mes;
        this.quincena = quincena;
        this.salarioBruto = salarioBruto;
        this.deduccionCCSS = deduccionCCSS;
        this.deduccionPension = deduccionPension;
        this.deduccionBancoPopular = deduccionBancoPopular;
        this.impuestoRenta = impuestoRenta;
        this.otrasdeducciones = otrasdeducciones;
        this.totalDeducciones = totalDeducciones;
        this.aportesPatronales = aportesPatronales;
        this.salarioNeto = salarioNeto;
        this.horasExtra = horasExtra;
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    /** @return Identificador del empleado. */
    public String getIdEmpleado() { return idEmpleado; }

    /** @param idEmpleado Nuevo identificador del empleado. */
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }

    /** @return Año del período. */
    public int getAnio() { return anio; }

    /** @param anio Nuevo año del período. */
    public void setAnio(int anio) { this.anio = anio; }

    /** @return Mes del período (1-12). */
    public int getMes() { return mes; }

    /** @param mes Nuevo mes del período. */
    public void setMes(int mes) { this.mes = mes; }

    /** @return Número de quincena (1 o 2). */
    public int getQuincena() { return quincena; }

    /** @param quincena Nueva quincena (1 o 2). */
    public void setQuincena(int quincena) { this.quincena = quincena; }

    /** @return Salario bruto quincenal. */
    public double getSalarioBruto() { return salarioBruto; }

    /** @param salarioBruto Nuevo salario bruto quincenal. */
    public void setSalarioBruto(double salarioBruto) { this.salarioBruto = salarioBruto; }

    /** @return Deducción CCSS del trabajador. */
    public double getDeduccionCCSS() { return deduccionCCSS; }

    /** @param deduccionCCSS Nueva deducción CCSS. */
    public void setDeduccionCCSS(double deduccionCCSS) { this.deduccionCCSS = deduccionCCSS; }

    /** @return Deducción por pensión del trabajador. */
    public double getDeduccionPension() { return deduccionPension; }

    /** @param deduccionPension Nueva deducción por pensión. */
    public void setDeduccionPension(double deduccionPension) { this.deduccionPension = deduccionPension; }

    /** @return Deducción Banco Popular. */
    public double getDeduccionBancoPopular() { return deduccionBancoPopular; }

    /** @param deduccionBancoPopular Nueva deducción Banco Popular. */
    public void setDeduccionBancoPopular(double deduccionBancoPopular) { this.deduccionBancoPopular = deduccionBancoPopular; }

    /** @return Impuesto de renta del período. */
    public double getImpuestoRenta() { return impuestoRenta; }

    /** @param impuestoRenta Nuevo impuesto de renta. */
    public void setImpuestoRenta(double impuestoRenta) { this.impuestoRenta = impuestoRenta; }

    /** @return Otras deducciones del período. */
    public double getOtrasDeducciones() { return otrasdeducciones; }

    /** @param otrasdeducciones Nuevas otras deducciones. */
    public void setOtrasDeducciones(double otrasdeducciones) { this.otrasdeducciones = otrasdeducciones; }

    /** @return Total de deducciones al trabajador. */
    public double getTotalDeducciones() { return totalDeducciones; }

    /** @param totalDeducciones Nuevo total de deducciones. */
    public void setTotalDeducciones(double totalDeducciones) { this.totalDeducciones = totalDeducciones; }

    /** @return Aportes patronales totales. */
    public double getAportesPatronales() { return aportesPatronales; }

    /** @param aportesPatronales Nuevos aportes patronales. */
    public void setAportesPatronales(double aportesPatronales) { this.aportesPatronales = aportesPatronales; }

    /** @return Salario neto a pagar. */
    public double getSalarioNeto() { return salarioNeto; }

    /** @param salarioNeto Nuevo salario neto. */
    public void setSalarioNeto(double salarioNeto) { this.salarioNeto = salarioNeto; }

    /** @return Monto de horas extra del período. */
    public double getHorasExtra() { return horasExtra; }

    /** @param horasExtra Nuevo monto de horas extra. */
    public void setHorasExtra(double horasExtra) { this.horasExtra = horasExtra; }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    /**
     * Representación en texto de la nómina, útil para depuración.
     *@return Cadena con los datos clave de la nómina.
     */
    @Override
    public String toString() {
        return "Nomina{idEmpleado='" + idEmpleado +
               "', anio=" + anio + ", mes=" + mes + ", quincena=" + quincena +
               ", bruto=" + salarioBruto + ", neto=" + salarioNeto + "}";
    }
}
