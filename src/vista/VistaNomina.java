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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista para el cálculo y consulta de nóminas quincenales.
 * <p>
 * Permite seleccionar un empleado, ingresar el período (año, mes, quincena),
 * horas extra y otras deducciones, procesar la nómina y visualizar
 * el detalle del cálculo resultante.
 * También muestra el historial de nóminas del empleado seleccionado.
 * </p>
 *
 * @version 1.1
 */
public class VistaNomina extends JPanel {

    // ── Componentes ──────────────────────────────────────────────────────────
    /** Selector del empleado a procesar. */
    private JComboBox<String> cmbEmpleados;

    /** Spinners para año, mes y quincena del período. */
    private JSpinner spnAnio, spnMes, spnQuincena;

    /** Campos para monto de horas extra y otras deducciones. */
    private JTextField txtHorasExtra, txtOtrasDeducciones;

    /** Botones de acción principales. */
    private JButton btnCalcular, btnVerHistorial;

    /** Labels del panel de resultados. */
    private JLabel lblBruto, lblCCSS, lblROP, lblBP, lblRenta;
    private JLabel lblOtras, lblTotalDeducciones, lblPatronal, lblNeto;

    /** Tabla del historial de nóminas. */
    private JTable tablaHistorial;

    /** Modelo de datos del historial. */
    private DefaultTableModel modeloHistorial;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    /** DAO de empleados. */
    private EmpleadoDAO empleadoDAO;

    /** DAO de nóminas. */
    private NominaDAO nominaDAO;

    /** DAO de vacaciones. */
    private VacacionesDAO vacacionesDAO;

    /** Configuración de parámetros de nómina. */
    private ConfigNomina config;

    // ── Paleta ───────────────────────────────────────────────────────────────
    /** Color de acento principal. */
    private static final Color AZUL  = new Color(37, 99, 235);

    /** Color de fondo general. */
    private static final Color FONDO = new Color(248, 249, 252);

    /** Color de texto principal. */
    private static final Color TEXTO = new Color(30, 30, 60);

    /** Color de texto secundario. */
    private static final Color GRIS  = new Color(100, 116, 139);

    /** Color de borde. */
    private static final Color BORDE = new Color(226, 232, 240);

    /** Color para valores positivos (salario neto). */
    private static final Color VERDE = new Color(22, 163, 74);

    /** Color para valores de deducción. */
    private static final Color ROJO  = new Color(220, 38, 38);

    // ── Constructor ──────────────────────────────────────────────────────────
    /**
     * Construye la vista de nómina e inicializa los componentes.
     */
    public VistaNomina() {
        this.empleadoDAO   = new EmpleadoDAO();
        this.nominaDAO     = new NominaDAO();
        this.vacacionesDAO = new VacacionesDAO();
        cargarConfiguracion();
        setLayout(new BorderLayout(0, 0));
        setBackground(FONDO);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        inicializarComponentes();
        cargarEmpleados();
    }

    // ── Inicialización ───────────────────────────────────────────────────────
    /**
     * Crea y organiza todos los componentes de la vista de nómina.
     */
    private void inicializarComponentes() {

        // Encabezado
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lblTitulo = new JLabel("Cálculo de Nómina Quincenal");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TEXTO);
        JLabel lblSub = new JLabel("Procesá la planilla quincenal por empleado");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(GRIS);
        header.add(lblTitulo);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // ── Panel izquierdo: formulario ──────────────────────────────────────
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        panelIzq.setPreferredSize(new Dimension(300, 0));

        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpleados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbEmpleados.setAlignmentX(LEFT_ALIGNMENT);
        cmbEmpleados.setBackground(Color.WHITE);

        spnAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2099, 1));
        spnAnio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnAnio.setAlignmentX(LEFT_ALIGNMENT);

        spnMes = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnMes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnMes.setAlignmentX(LEFT_ALIGNMENT);

        spnQuincena = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
        spnQuincena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnQuincena.setAlignmentX(LEFT_ALIGNMENT);

        txtHorasExtra       = campoTexto();
        txtOtrasDeducciones = campoTexto();

        btnCalcular = crearBoton("Calcular nómina", AZUL);
        btnCalcular.setAlignmentX(LEFT_ALIGNMENT);
        btnCalcular.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        btnVerHistorial = crearBoton("Ver historial", GRIS);
        btnVerHistorial.setAlignmentX(LEFT_ALIGNMENT);
        btnVerHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        panelIzq.add(seccion("Empleado y período"));
        panelIzq.add(etiqueta("EMPLEADO"));
        panelIzq.add(Box.createVerticalStrut(4));
        panelIzq.add(cmbEmpleados);
        panelIzq.add(Box.createVerticalStrut(10));
        panelIzq.add(etiqueta("AÑO"));
        panelIzq.add(Box.createVerticalStrut(4));
        panelIzq.add(spnAnio);
        panelIzq.add(Box.createVerticalStrut(8));
        panelIzq.add(etiqueta("MES (1-12)"));
        panelIzq.add(Box.createVerticalStrut(4));
        panelIzq.add(spnMes);
        panelIzq.add(Box.createVerticalStrut(8));
        panelIzq.add(etiqueta("QUINCENA (1 O 2)"));
        panelIzq.add(Box.createVerticalStrut(4));
        panelIzq.add(spnQuincena);
        panelIzq.add(Box.createVerticalStrut(16));
        panelIzq.add(seccion("Extras y deducciones"));
        panelIzq.add(etiqueta("MONTO HORAS EXTRA (₡)"));
        panelIzq.add(Box.createVerticalStrut(4));
        panelIzq.add(txtHorasExtra);
        panelIzq.add(Box.createVerticalStrut(8));
        panelIzq.add(etiqueta("OTRAS DEDUCCIONES (₡)"));
        panelIzq.add(Box.createVerticalStrut(4));
        panelIzq.add(txtOtrasDeducciones);
        panelIzq.add(Box.createVerticalStrut(20));
        panelIzq.add(btnCalcular);
        panelIzq.add(Box.createVerticalStrut(8));
        panelIzq.add(btnVerHistorial);

        // ── Panel derecho: resultados + historial ────────────────────────────
        JPanel panelDer = new JPanel(new BorderLayout(0, 16));
        panelDer.setBackground(FONDO);
        panelDer.setBorder(new EmptyBorder(0, 16, 0, 0));

        // Tarjeta de resultados
        JPanel panelResultado = new JPanel(new GridLayout(9, 2, 8, 0));
        panelResultado.setBackground(Color.WHITE);
        panelResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        lblBruto            = filaResultado(panelResultado, "Salario bruto",            TEXTO, false);
        lblCCSS             = filaResultado(panelResultado, "Deduc. CCSS trabajador",   ROJO,  false);
        lblROP              = filaResultado(panelResultado, "Deduc. ROP trabajador",    ROJO,  false);
        lblBP               = filaResultado(panelResultado, "Deduc. Banco Popular",     ROJO,  false);
        lblRenta            = filaResultado(panelResultado, "Impuesto de renta",        ROJO,  false);
        this.lblOtras       = filaResultado(panelResultado, "Otras deducciones",        ROJO,  false);
        lblTotalDeducciones = filaResultado(panelResultado, "Total deducciones",        ROJO,  true);
        lblPatronal         = filaResultado(panelResultado, "Aportes patronales",       GRIS,  false);
        lblNeto             = filaResultado(panelResultado, "Salario neto",             VERDE, true);

        // Separador visual antes del neto
        panelResultado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(16, 20, 16, 20)));

        // Historial
        String[] cols = {"Año", "Mes", "Q", "Bruto", "Deducciones", "Patronal", "Neto"};
        modeloHistorial = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHistorial.setRowHeight(32);
        tablaHistorial.setShowVerticalLines(false);
        tablaHistorial.setGridColor(BORDE);
        tablaHistorial.setBackground(Color.WHITE);
        tablaHistorial.setSelectionBackground(new Color(219, 234, 254));
        tablaHistorial.setSelectionForeground(TEXTO);

        JTableHeader tableHeader = tablaHistorial.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tableHeader.setBackground(FONDO);
        tableHeader.setForeground(GRIS);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        tableHeader.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 3; i < cols.length; i++) {
            tablaHistorial.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(0, 180));

        panelDer.add(panelResultado, BorderLayout.CENTER);
        panelDer.add(scroll, BorderLayout.SOUTH);

        add(panelIzq, BorderLayout.WEST);
        add(panelDer, BorderLayout.CENTER);

        registrarEventos();
    }

    // ── Eventos ──────────────────────────────────────────────────────────────
    /**
     * Registra los listeners de los botones de cálculo e historial.
     */
    private void registrarEventos() {
        btnCalcular.addActionListener(e -> calcularNomina());
        btnVerHistorial.addActionListener(e -> cargarHistorial());
    }

    // ── Lógica ───────────────────────────────────────────────────────────────
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

    // ── Utilidades ───────────────────────────────────────────────────────────
    /**
     * Crea una fila de resultados con etiqueta y valor coloreado.
     *
     * @param panel  Panel donde se agregan los componentes.
     * @param nombre Texto de la etiqueta descriptiva.
     * @param color  Color del valor.
     * @param negrita Si el valor se muestra en negrita.
     * @return El {@link JLabel} de valor para actualizarlo después.
     */
    private JLabel filaResultado(JPanel panel, String nombre, Color color, boolean negrita) {
        JLabel lbl = new JLabel(nombre + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(GRIS);
        panel.add(lbl);

        JLabel lblValor = new JLabel("—");
        lblValor.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, negrita ? 14 : 13));
        lblValor.setForeground(color);
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblValor);
        return lblValor;
    }

    /**
     * Crea una etiqueta de sección con separador visual.
     *
     * @param texto Texto de la sección.
     * @return Panel con título de sección estilizado.
     */
    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXTO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    /**
     * Crea una etiqueta pequeña en mayúsculas para campos del formulario.
     *
     * @param texto Texto de la etiqueta.
     * @return Etiqueta estilizada.
     */
    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(GRIS);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un campo de texto con estilo estándar.
     *
     * @return Campo de texto estilizado.
     */
    private JTextField campoTexto() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setForeground(TEXTO);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return txt;
    }

    /**
     * Crea un botón redondeado estilizado.
     *
     * @param texto Texto del botón.
     * @param color Color de fondo.
     * @return Botón configurado con diseño moderno.
     */
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isEnabled()
                        ? (getModel().isPressed() ? color.darker() : getModel().isRollover() ? color.brighter() : color)
                        : new Color(200, 200, 210);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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

    /**
     * Muestra un mensaje de error en un diálogo de advertencia.
     *
     * @param msg Texto del error.
     */
    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}