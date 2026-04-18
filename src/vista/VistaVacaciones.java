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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
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
 * @author ekaro
 * @version 2.0
 */
public class VistaVacaciones extends JPanel {

    // -------------------------------------------------------------------------
    // Componentes
    // -------------------------------------------------------------------------
    private JComboBox<String> cmbEmpleados;
    private JLabel lblAcumulados, lblConsumidos, lblRestantes, lblPagoEstimado;

    // Campos de fecha para solicitar vacaciones
    private JTextField txtFechaInicio, txtFechaFin;
    private JLabel lblDiasHabiles;
    private JButton btnCalcularDias, btnRegistrarConsumo, btnRefrescar, btnReiniciarAnual;

    // DAOs
    private EmpleadoDAO empleadoDAO;
    private VacacionesDAO vacacionesDAO;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Construye la vista de vacaciones e inicializa los componentes.
     */
    public VistaVacaciones() {
        this.empleadoDAO = new EmpleadoDAO();
        this.vacacionesDAO = new VacacionesDAO();
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(245, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
        cargarEmpleados();
    }

    // -------------------------------------------------------------------------
    // Inicialización
    // -------------------------------------------------------------------------
    /**
     * Crea y organiza todos los componentes de la vista.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Gestión de Vacaciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 60));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 4, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new BorderLayout(16, 0));
        panelCentro.setBackground(new Color(245, 245, 248));

        // ---- Panel izquierdo: selector + solicitud por fechas ----
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        panelIzq.setPreferredSize(new Dimension(290, 0));

        // Selector de empleado
        panelIzq.add(seccion("Empleado"));
        panelIzq.add(etiqueta("Seleccionar empleado"));
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpleados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbEmpleados.setAlignmentX(LEFT_ALIGNMENT);
        cmbEmpleados.addActionListener(e -> actualizarSaldo());
        panelIzq.add(cmbEmpleados);
        panelIzq.add(espacio(16));

        // Solicitud de vacaciones por fechas
        JLabel lblRegla = new JLabel(
                "<html><i>Solo días hábiles (lun–vie). Máximo 12 días/año.</i></html>");
        lblRegla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRegla.setForeground(new Color(120, 100, 40));
        lblRegla.setAlignmentX(LEFT_ALIGNMENT);

        panelIzq.add(seccion("Solicitar vacaciones"));
        panelIzq.add(lblRegla);
        panelIzq.add(espacio(8));

        panelIzq.add(etiqueta("Fecha inicio (YYYY-MM-DD)"));
        txtFechaInicio = campoTexto();
        panelIzq.add(txtFechaInicio);
        panelIzq.add(espacio(6));

        panelIzq.add(etiqueta("Fecha fin (YYYY-MM-DD)"));
        txtFechaFin = campoTexto();
        panelIzq.add(txtFechaFin);
        panelIzq.add(espacio(8));

        // Resultado de días hábiles calculados
        JPanel panelDiasHabiles = new JPanel(new BorderLayout(8, 0));
        panelDiasHabiles.setBackground(new Color(240, 245, 255));
        panelDiasHabiles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 240), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        panelDiasHabiles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panelDiasHabiles.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblDiasLbl = new JLabel("Días hábiles:");
        lblDiasLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDiasLbl.setForeground(new Color(60, 80, 140));

        lblDiasHabiles = new JLabel("—");
        lblDiasHabiles.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDiasHabiles.setForeground(new Color(30, 60, 160));

        panelDiasHabiles.add(lblDiasLbl, BorderLayout.WEST);
        panelDiasHabiles.add(lblDiasHabiles, BorderLayout.EAST);
        panelIzq.add(panelDiasHabiles);
        panelIzq.add(espacio(8));

        btnCalcularDias = boton("Calcular días hábiles", new Color(60, 100, 180));
        btnRegistrarConsumo = boton("Registrar vacaciones", new Color(30, 100, 60));
        btnRefrescar = boton("Refrescar saldo", new Color(60, 60, 110));
        btnReiniciarAnual = boton("Reiniciar saldo anual (12 días)", new Color(140, 60, 20));

        for (JButton b : new JButton[]{btnCalcularDias, btnRegistrarConsumo,
            btnRefrescar, btnReiniciarAnual}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            panelIzq.add(b);
            panelIzq.add(espacio(6));
        }

        panelIzq.add(Box.createVerticalGlue());

        // ---- Panel derecho: tarjetas de saldo ----
        JPanel panelDer = new JPanel(new GridLayout(2, 2, 14, 14));
        panelDer.setBackground(new Color(245, 245, 248));

        lblAcumulados = new JLabel("—");
        lblConsumidos = new JLabel("—");
        lblRestantes = new JLabel("—");
        lblPagoEstimado = new JLabel("—");

        panelDer.add(tarjetaSaldo("Días anuales (fijo)",
                lblAcumulados, new Color(30, 80, 160),
                "12 días hábiles por año laboral"));
        panelDer.add(tarjetaSaldo("Días consumidos",
                lblConsumidos, new Color(160, 60, 30),
                "días tomados este año"));
        panelDer.add(tarjetaSaldo("Días restantes",
                lblRestantes, new Color(20, 120, 60),
                "días disponibles (lun–vie)"));
        panelDer.add(tarjetaSaldo("Pago estimado",
                lblPagoEstimado, new Color(80, 60, 140),
                "por días restantes disponibles"));

        panelCentro.add(panelIzq, BorderLayout.WEST);
        panelCentro.add(panelDer, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        add(construirTablaResumen(), BorderLayout.SOUTH);

        registrarEventos();
    }

    // -------------------------------------------------------------------------
    // Eventos
    // -------------------------------------------------------------------------
    /**
     * Registra los listeners de todos los botones.
     */
    private void registrarEventos() {
        btnCalcularDias.addActionListener(e -> calcularDiasHabiles());
        btnRegistrarConsumo.addActionListener(e -> registrarConsumo());
        btnRefrescar.addActionListener(e -> actualizarSaldo());
        btnReiniciarAnual.addActionListener(e -> reiniciarSaldoAnual());
    }

    // -------------------------------------------------------------------------
    // Lógica
    // -------------------------------------------------------------------------
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

            GestionVacaciones gv = new GestionVacaciones(
                    new ConfigNomina(), emp, vacacionesDAO);

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
     * Calcula y muestra los días hábiles entre las fechas ingresadas. Muestra
     * advertencia si alguna fecha cae en fin de semana.
     */
    private void calcularDiasHabiles() {
        LocalDate inicio = parsearFecha(txtFechaInicio.getText().trim(), "inicio");
        LocalDate fin = parsearFecha(txtFechaFin.getText().trim(), "fin");
        if (inicio == null || fin == null) {
            return;
        }

        try {
            GestionVacaciones gv = new GestionVacaciones(
                    new ConfigNomina(), null, vacacionesDAO);
            gv.validarFechasVacaciones(inicio, fin);

            double dias = gv.contarDiasHabiles(inicio, fin);
            lblDiasHabiles.setText(String.format("%.0f días hábiles", dias));
            lblDiasHabiles.setForeground(new Color(30, 100, 30));

        } catch (CalculoNominaException ex) {
            lblDiasHabiles.setText("Fecha inválida");
            lblDiasHabiles.setForeground(new Color(180, 40, 40));
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Registra las vacaciones usando las fechas ingresadas. Calcula
     * automáticamente los días hábiles y valida el saldo.
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

            GestionVacaciones gv = new GestionVacaciones(
                    new ConfigNomina(), emp, vacacionesDAO);

            double diasHabiles = gv.contarDiasHabiles(inicio, fin);
            gv.consumirVacaciones(inicio, fin);

            txtFechaInicio.setText("");
            txtFechaFin.setText("");
            lblDiasHabiles.setText("—");
            actualizarSaldo();

            JOptionPane.showMessageDialog(this,
                    String.format(
                            "Vacaciones registradas para %s.\n"
                            + "Del %s al %s\n"
                            + "Días hábiles descontados: %.0f",
                            emp.getNombre(), inicio, fin, diasHabiles),
                    "Registrado", JOptionPane.INFORMATION_MESSAGE);

        } catch (CalculoNominaException ex) {
            mostrarError(ex.getMessage());
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    /**
     * Reinicia el saldo vacacional del empleado seleccionado a 12 días. Se usa
     * al comenzar un nuevo año laboral.
     */
    private void reiniciarSaldoAnual() {
        if (cmbEmpleados.getSelectedItem() == null) {
            mostrarError("Seleccione un empleado.");
            return;
        }
        String idEmp = cmbEmpleados.getSelectedItem().toString().split(" — ")[0];

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Reiniciar el saldo vacacional a 12 días para este empleado?\n"
                + "Esta acción representa el inicio de un nuevo año laboral.",
                "Confirmar reinicio", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Empleado emp = empleadoDAO.buscarPorId(idEmp);
            if (emp == null) {
                return;
            }

            GestionVacaciones gv = new GestionVacaciones(
                    new ConfigNomina(), emp, vacacionesDAO);
            gv.reiniciarSaldoAnual();
            actualizarSaldo();

            JOptionPane.showMessageDialog(this,
                    "Saldo reiniciado a 12 días para: " + emp.getNombre(),
                    "Reinicio completado", JOptionPane.INFORMATION_MESSAGE);

        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al reiniciar saldo: " + ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Tabla resumen
    // -------------------------------------------------------------------------
    /**
     * Construye la tabla resumen de vacaciones de todos los empleados.
     *
     * @return Panel con la tabla de resumen.
     */
    private JPanel construirTablaResumen() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(new Color(245, 245, 248));

        JLabel lbl = new JLabel("Resumen general de vacaciones");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 60, 80));

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
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.setGridColor(new Color(230, 230, 235));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(0, 120));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------
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
            mostrarError("Formato de fecha inválido para " + nombreCampo
                    + ". Use YYYY-MM-DD. Ejemplo: 2024-07-15");
            return null;
        }
    }

    private JPanel tarjetaSaldo(String titulo, JLabel lblValor,
            Color color, String subtitulo) {
        JPanel tarjeta = new JPanel(new GridLayout(3, 1, 0, 4));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                new EmptyBorder(14, 16, 14, 16)));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(new Color(80, 80, 100));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(color);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(160, 160, 180));

        tarjeta.add(lblTit);
        tarjeta.add(lblValor);
        tarjeta.add(lblSub);
        return tarjeta;
    }

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

    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(30, 30, 60));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(90, 90, 110));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Component espacio(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
