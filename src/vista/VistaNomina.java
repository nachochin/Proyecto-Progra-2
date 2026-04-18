/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;
import datos.ConfigNominaDAO;
import datos.EmpleadoDAO;
import datos.NominaDAO;
import datos.VacacionesDAO;
import entidades.ConfigNomina;
import entidades.Empleado;
import entidades.Nomina;
import excepciones.ArchivoInvalidoException;
import excepciones.CalculoNominaException;
import logica.CalculoNomina;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
/**
 *Vista para el cálculo y consulta de nóminas quincenales.
 * <p>
 * Permite seleccionar un empleado, ingresar el período (año, mes, quincena),
 * horas extra y otras deducciones, procesar la nómina y visualizar
 * el detalle del cálculo resultante.
 * También muestra el historial de nóminas del empleado seleccionado.
 * </p>
 * @author ekaro
 * @version 1.0
 */
public class VistaNomina extends JPanel {

    // -------------------------------------------------------------------------
    // Componentes
    // -------------------------------------------------------------------------

    private JComboBox<String> cmbEmpleados;
    private JSpinner spnAnio, spnMes, spnQuincena;
    private JTextField txtHorasExtra, txtOtrasDeducciones;
    private JButton btnCalcular, btnVerHistorial;

    // Panel de resultados
    private JLabel lblBruto, lblCCSS, lblROP, lblBP, lblRenta;
    private JLabel lblOtras, lblTotalDeducciones, lblPatronal, lblNeto;

    // Historial
    private JTable tablaHistorial;
    private DefaultTableModel modeloHistorial;

    // DAOs y configuración
    private EmpleadoDAO empleadoDAO;
    private NominaDAO nominaDAO;
    private VacacionesDAO vacacionesDAO;
    private ConfigNomina config;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Construye la vista de nómina e inicializa los componentes.
     */
    public VistaNomina() {
        this.empleadoDAO  = new EmpleadoDAO();
        this.nominaDAO    = new NominaDAO();
        this.vacacionesDAO = new VacacionesDAO();
        cargarConfiguracion();
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
        cargarEmpleados();
    }

    // -------------------------------------------------------------------------
    // Inicialización
    // -------------------------------------------------------------------------

    /**
     * Crea y organiza todos los componentes de la vista de nómina.
     */
    private void inicializarComponentes() {
        // Título
        JLabel lblTitulo = new JLabel("Cálculo de Nómina Quincenal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 60));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 14, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel izquierdo: formulario de cálculo
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(20, 20, 20, 20)));
        panelIzq.setPreferredSize(new Dimension(300, 0));

        // Selector de empleado
        JLabel lblEmp = encabezadoCampo("Empleado");
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpleados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbEmpleados.setAlignmentX(LEFT_ALIGNMENT);

        // Período
        JLabel lblPeriodo = encabezadoCampo("Año");
        spnAnio = new JSpinner(new SpinnerNumberModel(
                LocalDate.now().getYear(), 2020, 2099, 1));
        spnAnio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        spnAnio.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblMes = encabezadoCampo("Mes (1-12)");
        spnMes = new JSpinner(new SpinnerNumberModel(
                LocalDate.now().getMonthValue(), 1, 12, 1));
        spnMes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        spnMes.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblQuincena = encabezadoCampo("Quincena (1 o 2)");
        spnQuincena = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
        spnQuincena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        spnQuincena.setAlignmentX(LEFT_ALIGNMENT);

        // Extras
        JLabel lblHoras = encabezadoCampo("Monto horas extra (₡)");
        txtHorasExtra = campoTexto();

        JLabel lblOtras = encabezadoCampo("Otras deducciones (₡)");
        txtOtrasDeducciones = campoTexto();

        btnCalcular = botonAccion("Calcular nómina", new Color(30, 30, 60));
        btnCalcular.setAlignmentX(LEFT_ALIGNMENT);
        btnCalcular.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnVerHistorial = botonAccion("Ver historial", new Color(60, 100, 160));
        btnVerHistorial.setAlignmentX(LEFT_ALIGNMENT);
        btnVerHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        panelIzq.add(encabezadoSeccion("Empleado y período"));
        panelIzq.add(lblEmp);        panelIzq.add(cmbEmpleados);
        panelIzq.add(vspace(10));
        panelIzq.add(lblPeriodo);    panelIzq.add(spnAnio);
        panelIzq.add(vspace(8));
        panelIzq.add(lblMes);        panelIzq.add(spnMes);
        panelIzq.add(vspace(8));
        panelIzq.add(lblQuincena);   panelIzq.add(spnQuincena);
        panelIzq.add(vspace(14));
        panelIzq.add(encabezadoSeccion("Extras y deducciones"));
        panelIzq.add(lblHoras);      panelIzq.add(txtHorasExtra);
        panelIzq.add(vspace(8));
        panelIzq.add(lblOtras);      panelIzq.add(txtOtrasDeducciones);
        panelIzq.add(vspace(16));
        panelIzq.add(btnCalcular);
        panelIzq.add(vspace(8));
        panelIzq.add(btnVerHistorial);

        // Panel derecho: resultado + historial
        JPanel panelDer = new JPanel(new BorderLayout(0, 12));
        panelDer.setBackground(new Color(245, 245, 248));
        panelDer.setBorder(new EmptyBorder(0, 16, 0, 0));

        // Tarjeta de resultados
        JPanel panelResultado = new JPanel(new GridLayout(9, 2, 8, 6));
        panelResultado.setBackground(Color.WHITE);
        panelResultado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
            new EmptyBorder(16, 20, 16, 20)));

        lblBruto          = filaNomina(panelResultado, "Salario bruto");
        lblCCSS           = filaNomina(panelResultado, "Deduc. CCSS trabajador");
        lblROP            = filaNomina(panelResultado, "Deduc. ROP trabajador");
        lblBP             = filaNomina(panelResultado, "Deduc. Banco Popular");
        lblRenta          = filaNomina(panelResultado, "Impuesto de renta");
        this.lblOtras     = filaNomina(panelResultado, "Otras deducciones");
        lblTotalDeducciones = filaNomina(panelResultado, "Total deducciones");
        lblPatronal       = filaNomina(panelResultado, "Aportes patronales");
        lblNeto           = filaNomina(panelResultado, "Salario neto");
        lblNeto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNeto.setForeground(new Color(20, 120, 60));

        // Tabla de historial
        String[] cols = {"Año","Mes","Q","Bruto","Deducciones","Patronal","Neto"};
        modeloHistorial = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHistorial.setRowHeight(26);
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHistorial.setGridColor(new Color(230, 230, 235));
        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
        scroll.setPreferredSize(new Dimension(0, 160));

        panelDer.add(panelResultado, BorderLayout.CENTER);
        panelDer.add(scroll, BorderLayout.SOUTH);

        add(panelIzq, BorderLayout.WEST);
        add(panelDer, BorderLayout.CENTER);

        registrarEventos();
    }

    // -------------------------------------------------------------------------
    // Eventos
    // -------------------------------------------------------------------------

    /**
     * Registra los listeners de los botones de cálculo e historial.
     */
    private void registrarEventos() {
        btnCalcular.addActionListener(e -> calcularNomina());
        btnVerHistorial.addActionListener(e -> cargarHistorial());
    }

    // -------------------------------------------------------------------------
    // Lógica
    // -------------------------------------------------------------------------

    /**
     * Carga la configuración de nómina desde el archivo.
     * Si el archivo no existe usa los valores por defecto.
     */
    private void cargarConfiguracion() {
        try {
            config = new ConfigNominaDAO().cargarConfig();
        } catch (ArchivoInvalidoException e) {
            config = new ConfigNomina();
        }
    }

    /**
     * Carga el listado de empleados en el combo selector.
     */
    private void cargarEmpleados() {
        cmbEmpleados.removeAllItems();
        try {
            for (Empleado emp : empleadoDAO.listarTodos()) {
                cmbEmpleados.addItem(emp.getId() + " — " + emp.getNombre());
            }
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al cargar empleados: " + ex.getMessage());
        }
    }

    /**
     * Procesa la nómina del empleado seleccionado con los datos del formulario.
     * Muestra el resultado desglosado en la tarjeta de resultados.
     */
    private void calcularNomina() {
        if (cmbEmpleados.getSelectedItem() == null) {
            mostrarError("Seleccione un empleado.");
            return;
        }

        String idEmpleado = cmbEmpleados.getSelectedItem().toString().split(" — ")[0];
        int anio     = (int) spnAnio.getValue();
        int mes      = (int) spnMes.getValue();
        int quincena = (int) spnQuincena.getValue();

        double horasExtra, otrasDed;
        try {
            horasExtra = txtHorasExtra.getText().trim().isEmpty() ? 0
                : Double.parseDouble(txtHorasExtra.getText().trim());
            otrasDed   = txtOtrasDeducciones.getText().trim().isEmpty() ? 0
                : Double.parseDouble(txtOtrasDeducciones.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarError("Horas extra y otras deducciones deben ser valores numéricos.");
            return;
        }

        try {
            Empleado emp = empleadoDAO.buscarPorId(idEmpleado);
            if (emp == null) { mostrarError("Empleado no encontrado."); return; }

            CalculoNomina calc = new CalculoNomina(
                config, emp, anio, mes, quincena,
                horasExtra, otrasDed, nominaDAO, vacacionesDAO);

            Nomina n = calc.procesarNomina();
            mostrarResultado(n);
            cargarHistorial();
            JOptionPane.showMessageDialog(this,
                "Nómina procesada correctamente.\nSalario neto: ₡" +
                String.format("%,.2f", n.getSalarioNeto()),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (CalculoNominaException ex) {
            mostrarError("Error de cálculo: " + ex.getMessage());
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error de archivo: " + ex.getMessage());
        }
    }

    /**
     * Muestra el desglose de la nómina calculada en la tarjeta de resultados.
     *
     * @param n Nómina calculada a mostrar.
     */
    private void mostrarResultado(Nomina n) {
        lblBruto.setText(formatoMoneda(n.getSalarioBruto()));
        lblCCSS.setText(formatoMoneda(n.getDeduccionCCSS()));
        lblROP.setText(formatoMoneda(n.getDeduccionPension()));
        lblBP.setText(formatoMoneda(n.getDeduccionBancoPopular()));
        lblRenta.setText(formatoMoneda(n.getImpuestoRenta()));
        lblOtras.setText(formatoMoneda(n.getOtrasDeducciones()));
        lblTotalDeducciones.setText(formatoMoneda(n.getTotalDeducciones()));
        lblPatronal.setText(formatoMoneda(n.getAportesPatronales()));
        lblNeto.setText(formatoMoneda(n.getSalarioNeto()));
    }

    /**
     * Carga el historial de nóminas del empleado seleccionado en la tabla inferior.
     */
    private void cargarHistorial() {
        modeloHistorial.setRowCount(0);
        if (cmbEmpleados.getSelectedItem() == null) return;

        String idEmpleado = cmbEmpleados.getSelectedItem().toString().split(" — ")[0];
        try {
            List<Nomina> historial = nominaDAO.listarPorEmpleado(idEmpleado);
            for (Nomina n : historial) {
                modeloHistorial.addRow(new Object[]{
                    n.getAnio(), n.getMes(), n.getQuincena(),
                    formatoMoneda(n.getSalarioBruto()),
                    formatoMoneda(n.getTotalDeducciones()),
                    formatoMoneda(n.getAportesPatronales()),
                    formatoMoneda(n.getSalarioNeto())
                });
            }
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al cargar historial: " + ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Utilidades de la vista
    // -------------------------------------------------------------------------

    /**
     * Crea una fila de dos columnas en el panel de resultados:
     * etiqueta a la izquierda y label de valor a la derecha.
     *
     * @param panel  Panel donde se agregan los componentes.
     * @param nombre Texto de la etiqueta descriptiva.
     * @return El {@link JLabel} de valor, para actualizarlo después.
     */
    private JLabel filaNomina(JPanel panel, String nombre) {
        JLabel lbl = new JLabel(nombre + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(80, 80, 100));
        panel.add(lbl);

        JLabel lblValor = new JLabel("—");
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblValor);
        return lblValor;
    }

    /** @return Etiqueta de sección en negrita para el formulario. */
    private JLabel encabezadoSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(30, 30, 60));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /** @return Etiqueta pequeña para el nombre de un campo. */
    private JLabel encabezadoCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(90, 90, 110));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    /** @return Campo de texto con estilo estándar. */
    private JTextField campoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 220), 1, true),
            new EmptyBorder(4, 8, 4, 8)));
        return txt;
    }

    /** @return Botón de acción con color y estilo estándar. */
    private JButton botonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** @return Espacio vertical rígido para BoxLayout. */
    private Component vspace(int alto) {
        return Box.createRigidArea(new Dimension(0, alto));
    }

    /**
     * Formatea un monto en colones con símbolo y separadores de miles.
     *
     * @param monto Valor numérico a formatear.
     * @return Cadena con formato "₡1,234,567.89".
     */
    private String formatoMoneda(double monto) {
        return String.format("₡%,.2f", monto);
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
