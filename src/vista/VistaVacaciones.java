/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import datos.EmpleadoDAO;
import datos.VacacionesDAO;
import entidades.ConfigNomina;
import entidades.Empleado;
import entidades.Vacaciones;
import excepciones.ArchivoInvalidoException;
import excepciones.CalculoNominaException;
import logica.GestionVacaciones;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Vista para la gestión del saldo de vacaciones por empleado.
 * <p>
 * Reglas aplicadas:
 * <ul>
 * <li>12 días hábiles anuales, no acumulables entre años.</li>
 * <li>Sábados y domingos NO cuentan ni son válidos como fecha de inicio o
 * fin.</li>
 * <li>El sistema calcula automáticamente los días hábiles entre las
 * fechas.</li>
 * </ul>
 * </p>
 *
 * @version 2.1
 */
public class VistaVacaciones extends JPanel {

    // ── Componentes ──────────────────────────────────────────────────────────
    /**
     * Selector del empleado.
     */
    private JComboBox<String> cmbEmpleados;

    /**
     * Labels de las tarjetas de saldo.
     */
    private JLabel lblAcumulados, lblConsumidos, lblRestantes, lblPagoEstimado;

    /**
     * Campos de fecha para solicitar vacaciones.
     */
    private JTextField txtFechaInicio, txtFechaFin;

    /**
     * Label que muestra los días hábiles calculados.
     */
    private JLabel lblDiasHabiles;

    /**
     * Botones de acción.
     */
    private JButton btnCalcularDias, btnRegistrarConsumo, btnRefrescar, btnReiniciarAnual;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    /**
     * DAO de empleados.
     */
    private EmpleadoDAO empleadoDAO;

    /**
     * DAO de vacaciones.
     */
    private VacacionesDAO vacacionesDAO;

    // ── Paleta ───────────────────────────────────────────────────────────────
    /**
     * Color de acento principal.
     */
    private static final Color AZUL = new Color(37, 99, 235);

    /**
     * Color de fondo general.
     */
    private static final Color FONDO = new Color(248, 249, 252);

    /**
     * Color de texto principal.
     */
    private static final Color TEXTO = new Color(30, 30, 60);

    /**
     * Color de texto secundario.
     */
    private static final Color GRIS = new Color(100, 116, 139);

    /**
     * Color de borde.
     */
    private static final Color BORDE = new Color(226, 232, 240);

    /**
     * Color para valores positivos.
     */
    private static final Color VERDE = new Color(22, 163, 74);

    /**
     * Color para valores de consumo.
     */
    private static final Color ROJO = new Color(220, 38, 38);

    /**
     * Color púrpura para pago estimado.
     */
    private static final Color PURPURA = new Color(124, 58, 237);

    // ── Constructor ──────────────────────────────────────────────────────────
    /**
     * Construye la vista de vacaciones e inicializa los componentes.
     */
    public VistaVacaciones() {
        this.empleadoDAO = new EmpleadoDAO();
        this.vacacionesDAO = new VacacionesDAO();
        setLayout(new BorderLayout(0, 16));
        setBackground(FONDO);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        inicializarComponentes();
        cargarEmpleados();
    }

    // ── Inicialización ───────────────────────────────────────────────────────
    /**
     * Crea y organiza todos los componentes de la vista.
     */
    private void inicializarComponentes() {

        // Encabezado
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel lblTitulo = new JLabel("Gestión de Vacaciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TEXTO);
        JLabel lblSub = new JLabel("Control de días hábiles por empleado · máximo 12 días/año");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(GRIS);
        header.add(lblTitulo);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(16, 0));
        panelCentro.setBackground(FONDO);

        // ── Panel izquierdo: formulario ──────────────────────────────────────
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        panelIzq.setPreferredSize(new Dimension(295, 0));

        // Selector empleado
        panelIzq.add(seccion("Empleado"));
        panelIzq.add(etiqueta("SELECCIONAR EMPLEADO"));
        panelIzq.add(Box.createVerticalStrut(4));
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpleados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbEmpleados.setAlignmentX(LEFT_ALIGNMENT);
        cmbEmpleados.setBackground(Color.WHITE);
        cmbEmpleados.addActionListener(e -> actualizarSaldo());
        panelIzq.add(cmbEmpleados);
        panelIzq.add(Box.createVerticalStrut(16));

        // Aviso regla
        JPanel avisoPanel = new JPanel(new BorderLayout());
        avisoPanel.setBackground(new Color(254, 243, 199));
        avisoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        avisoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        avisoPanel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lblAviso = new JLabel("<html><b>Regla:</b> Solo días hábiles (lun–vie). Máx. 12 días/año.</html>");
        lblAviso.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAviso.setForeground(new Color(146, 64, 14));
        avisoPanel.add(lblAviso);
        panelIzq.add(avisoPanel);
        panelIzq.add(Box.createVerticalStrut(14));

        // Fechas
        panelIzq.add(seccion("Solicitar vacaciones"));
        panelIzq.add(etiqueta("FECHA INICIO (YYYY-MM-DD)"));
        panelIzq.add(Box.createVerticalStrut(4));
        txtFechaInicio = campoTexto();
        panelIzq.add(txtFechaInicio);
        panelIzq.add(Box.createVerticalStrut(8));
        panelIzq.add(etiqueta("FECHA FIN (YYYY-MM-DD)"));
        panelIzq.add(Box.createVerticalStrut(4));
        txtFechaFin = campoTexto();
        panelIzq.add(txtFechaFin);
        panelIzq.add(Box.createVerticalStrut(10));

        // Panel días hábiles
        JPanel panelDias = new JPanel(new BorderLayout(8, 0));
        panelDias.setBackground(new Color(239, 246, 255));
        panelDias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        panelDias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelDias.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lblDiasLbl = new JLabel("Días hábiles:");
        lblDiasLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDiasLbl.setForeground(AZUL);
        lblDiasHabiles = new JLabel("—");
        lblDiasHabiles.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDiasHabiles.setForeground(AZUL);
        panelDias.add(lblDiasLbl, BorderLayout.WEST);
        panelDias.add(lblDiasHabiles, BorderLayout.EAST);
        panelIzq.add(panelDias);
        panelIzq.add(Box.createVerticalStrut(12));

        // Botones
        btnCalcularDias = crearBoton("Calcular días hábiles", AZUL);
        btnRegistrarConsumo = crearBoton("Registrar vacaciones", VERDE);
        btnRefrescar = crearBoton("Refrescar saldo", GRIS);
        btnReiniciarAnual = crearBoton("Reiniciar saldo anual (12 días)", ROJO);

        for (JButton b : new JButton[]{btnCalcularDias, btnRegistrarConsumo, btnRefrescar, btnReiniciarAnual}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            panelIzq.add(b);
            panelIzq.add(Box.createVerticalStrut(7));
        }
        panelIzq.add(Box.createVerticalGlue());

        // ── Panel derecho: tarjetas de saldo ─────────────────────────────────
        lblAcumulados = new JLabel("—");
        lblConsumidos = new JLabel("—");
        lblRestantes = new JLabel("—");
        lblPagoEstimado = new JLabel("—");

        JPanel panelTarjetas = new JPanel(new GridLayout(2, 2, 14, 14));
        panelTarjetas.setBackground(FONDO);
        panelTarjetas.add(tarjetaSaldo("Días anuales", lblAcumulados, AZUL, "12 días hábiles por año laboral"));
        panelTarjetas.add(tarjetaSaldo("Días consumidos", lblConsumidos, ROJO, "días tomados este año"));
        panelTarjetas.add(tarjetaSaldo("Días restantes", lblRestantes, VERDE, "días disponibles (lun–vie)"));
        panelTarjetas.add(tarjetaSaldo("Pago estimado", lblPagoEstimado, PURPURA, "por días restantes disponibles"));

        panelCentro.add(panelIzq, BorderLayout.WEST);
        panelCentro.add(panelTarjetas, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);
        add(construirTablaResumen(), BorderLayout.SOUTH);

        registrarEventos();
    }

    // ── Eventos ──────────────────────────────────────────────────────────────
    /**
     * Registra los listeners de todos los botones.
     */
    private void registrarEventos() {
        btnCalcularDias.addActionListener(e -> calcularDiasHabiles());
        btnRegistrarConsumo.addActionListener(e -> registrarConsumo());
        btnRefrescar.addActionListener(e -> actualizarSaldo());
        btnReiniciarAnual.addActionListener(e -> reiniciarSaldoAnual());
    }

    // ── Lógica ───────────────────────────────────────────────────────────────
    /**
     * Carga todos los empleados en el combo selector.
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
        actualizarSaldo();
    }

    /**
     * Actualiza las tarjetas de saldo con los datos del empleado seleccionado.
     */
    private void actualizarSaldo() {
        if (cmbEmpleados.getSelectedItem() == null) {
            return;
        }
        String idEmp = cmbEmpleados.getSelectedItem().toString().split(" — ")[0];
        try {
            Empleado emp = empleadoDAO.buscarPorId(idEmp);
            if (emp == null) {
                return;
            }
            GestionVacaciones gv = new GestionVacaciones(new ConfigNomina(), emp, vacacionesDAO);
            double acum = gv.getDiasAcumulados();
            double cons = gv.getDiasConsumidos();
            double rest = gv.getDiasRestantes();
            double pago = gv.calcularPagoVacaciones(rest);
            lblAcumulados.setText(String.format("%.0f", acum));
            lblConsumidos.setText(String.format("%.1f", cons));
            lblRestantes.setText(String.format("%.1f", rest));
            lblPagoEstimado.setText(String.format("₡%,.2f", pago));
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al leer saldo: " + ex.getMessage());
        }
    }

    /**
     * Calcula y muestra los días hábiles entre las fechas ingresadas.
     */
    private void calcularDiasHabiles() {
        LocalDate inicio = parsearFecha(txtFechaInicio.getText().trim(), "inicio");
        LocalDate fin = parsearFecha(txtFechaFin.getText().trim(), "fin");
        if (inicio == null || fin == null) {
            return;
        }
        try {
            GestionVacaciones gv = new GestionVacaciones(new ConfigNomina(), null, vacacionesDAO);
            gv.validarFechasVacaciones(inicio, fin);
            double dias = gv.contarDiasHabiles(inicio, fin);
            lblDiasHabiles.setText(String.format("%.0f días hábiles", dias));
            lblDiasHabiles.setForeground(VERDE);
        } catch (CalculoNominaException ex) {
            lblDiasHabiles.setText("Fecha inválida");
            lblDiasHabiles.setForeground(ROJO);
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Registra las vacaciones usando las fechas ingresadas.
     */
    private void registrarConsumo() {
        if (cmbEmpleados.getSelectedItem() == null) {
            mostrarError("Seleccione un empleado.");
            return;
        }
        LocalDate inicio = parsearFecha(txtFechaInicio.getText().trim(), "inicio");
        LocalDate fin = parsearFecha(txtFechaFin.getText().trim(), "fin");
        if (inicio == null || fin == null) {
            return;
        }
        String idEmp = cmbEmpleados.getSelectedItem().toString().split(" — ")[0];
        try {
            Empleado emp = empleadoDAO.buscarPorId(idEmp);
            if (emp == null) {
                return;
            }
            GestionVacaciones gv = new GestionVacaciones(new ConfigNomina(), emp, vacacionesDAO);
            double diasHabiles = gv.contarDiasHabiles(inicio, fin);
            gv.consumirVacaciones(inicio, fin);
            txtFechaInicio.setText("");
            txtFechaFin.setText("");
            lblDiasHabiles.setText("—");
            lblDiasHabiles.setForeground(AZUL);
            actualizarSaldo();
            JOptionPane.showMessageDialog(this,
                    String.format("Vacaciones registradas para %s.\nDel %s al %s\nDías hábiles descontados: %.0f",
                            emp.getNombre(), inicio, fin, diasHabiles),
                    "Registrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (CalculoNominaException ex) {
            mostrarError(ex.getMessage());
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    /**
     * Reinicia el saldo vacacional del empleado seleccionado a 12 días.
     */
    private void reiniciarSaldoAnual() {
        if (cmbEmpleados.getSelectedItem() == null) {
            mostrarError("Seleccione un empleado.");
            return;
        }
        String idEmp = cmbEmpleados.getSelectedItem().toString().split(" — ")[0];
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Reiniciar el saldo vacacional a 12 días para este empleado?\nEsta acción representa el inicio de un nuevo año laboral.",
                "Confirmar reinicio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Empleado emp = empleadoDAO.buscarPorId(idEmp);
            if (emp == null) {
                return;
            }
            GestionVacaciones gv = new GestionVacaciones(new ConfigNomina(), emp, vacacionesDAO);
            gv.reiniciarSaldoAnual();
            actualizarSaldo();
            JOptionPane.showMessageDialog(this,
                    "Saldo reiniciado a 12 días para: " + emp.getNombre(),
                    "Reinicio completado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al reiniciar saldo: " + ex.getMessage());
        }
    }

    // ── Tabla resumen ────────────────────────────────────────────────────────
    /**
     * Construye la tabla resumen de vacaciones de todos los empleados.
     *
     * @return Panel con la tabla de resumen.
     */
    private JPanel construirTablaResumen() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(FONDO);

        JLabel lbl = new JLabel("Resumen general de vacaciones");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXTO);

        String[] cols = {"ID", "Empleado", "Días/año", "Consumidos", "Restantes"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        try {
            List<Vacaciones> lista = vacacionesDAO.listarTodos();
            for (Vacaciones v : lista) {
                Empleado emp;
                try {
                    emp = empleadoDAO.buscarPorId(v.getIdEmpleado());
                } catch (ArchivoInvalidoException ex) {
                    emp = null;
                }
                String nombre = (emp != null) ? emp.getNombre() : v.getIdEmpleado();
                modelo.addRow(new Object[]{
                    v.getIdEmpleado(), nombre,
                    String.format("%.0f", v.getDiasAcumulados()),
                    String.format("%.1f", v.getDiasConsumidos()),
                    String.format("%.1f", v.getDiasRestantes())
                });
            }
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al cargar resumen: " + ex.getMessage());
        }

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(32);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(BORDE);
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(219, 234, 254));

        JTableHeader tableHeader = tabla.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tableHeader.setBackground(FONDO);
        tableHeader.setForeground(GRIS);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        tableHeader.setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 2; i < cols.length; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 130));
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Utilidades ───────────────────────────────────────────────────────────
    /**
     * Parsea una fecha en formato YYYY-MM-DD y muestra error si falla.
     *
     * @param texto Texto a parsear.
     * @param nombreCampo Nombre del campo para el mensaje de error.
     * @return Fecha parseada, o {@code null} si el formato es inválido.
     */
    private LocalDate parsearFecha(String texto, String nombreCampo) {
        if (texto.isEmpty()) {
            mostrarError("Ingrese la fecha de " + nombreCampo + " (YYYY-MM-DD).");
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            mostrarError("Formato de fecha inválido para " + nombreCampo + ". Use YYYY-MM-DD.");
            return null;
        }
    }

    /**
     * Crea una tarjeta de saldo con título, valor destacado y subtítulo.
     *
     * @param titulo Título de la tarjeta.
     * @param lblValor Label del valor a mostrar.
     * @param color Color del acento y del valor.
     * @param subtitulo Descripción debajo del valor.
     * @return Panel tarjeta configurado.
     */
    private JPanel tarjetaSaldo(String titulo, JLabel lblValor, Color color, String subtitulo) {
        JPanel tarjeta = new JPanel(new GridLayout(3, 1, 0, 4));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(GRIS);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));

        tarjeta.add(lblTit);
        tarjeta.add(lblValor);
        tarjeta.add(lblSub);
        return tarjeta;
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
     * Crea una etiqueta de sección con separador visual.
     *
     * @param texto Texto de la sección.
     * @return Etiqueta estilizada.
     */
    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXTO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
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
     * Muestra un mensaje de error en un diálogo de advertencia.
     *
     * @param msg Texto del error.
     */
    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
