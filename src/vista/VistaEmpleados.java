/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Módulo desarrollado por Erika Rojas 
package vista;

import datos.EmpleadoDAO;
import datos.VacacionesDAO;
import entidades.Empleado;
import excepciones.ArchivoInvalidoException;
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
 * Vista para la gestión completa de empleados (CRUD).
 * <p>
 * Muestra una tabla con todos los empleados registrados y permite crear, editar
 * y eliminar empleados mediante un formulario integrado. Al crear un empleado
 * también inicializa su saldo de vacaciones en cero.
 * </p>
 *
 * @version 1.1
 */
public class VistaEmpleados extends JPanel {

    // ── Componentes ──────────────────────────────────────────────────────────
    /**
     * Tabla que muestra el listado de empleados.
     */
    private JTable tablaEmpleados;

    /**
     * Modelo de datos de la tabla.
     */
    private DefaultTableModel modeloTabla;

    /**
     * Campos del formulario lateral.
     */
    private JTextField txtId, txtNombre, txtCorreo, txtSalario, txtFecha;

    /**
     * Selector del tipo de contrato.
     */
    private JComboBox<String> cmbTipoContrato;

    /**
     * Botones de acción del formulario.
     */
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;

    /**
     * Título dinámico del formulario (Nuevo / Editar).
     */
    private JLabel lblTituloFormulario;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    /**
     * DAO para operaciones CRUD de empleados.
     */
    private EmpleadoDAO empleadoDAO;

    /**
     * DAO para operaciones sobre vacaciones.
     */
    private VacacionesDAO vacacionesDAO;

    /**
     * Indica si el formulario está en modo edición (true) o creación (false).
     */
    private boolean modoEdicion = false;

    // ── Paleta ───────────────────────────────────────────────────────────────
    /**
     * Color de acento principal — azul corporativo.
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
     * Color de borde para campos y tarjetas.
     */
    private static final Color BORDE = new Color(226, 232, 240);

    /**
     * Color para botón de éxito (guardar/actualizar).
     */
    private static final Color VERDE = new Color(22, 163, 74);

    /**
     * Color para botón de peligro (eliminar).
     */
    private static final Color ROJO = new Color(220, 38, 38);

    // ── Constructor ──────────────────────────────────────────────────────────
    /**
     * Construye la vista de gestión de empleados e inicializa los componentes.
     */
    public VistaEmpleados() {
        this.empleadoDAO = new EmpleadoDAO();
        this.vacacionesDAO = new VacacionesDAO();
        setLayout(new BorderLayout(16, 0));
        setBackground(FONDO);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        inicializarComponentes();
        cargarTabla();
        limpiarFormulario(); // genera el ID automático al abrir
    }

    // ── Inicialización ───────────────────────────────────────────────────────
    /**
     * Crea y organiza todos los componentes de la vista.
     */
    private void inicializarComponentes() {

        // Encabezado
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lblTitulo = new JLabel("Gestión de Empleados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TEXTO);
        JLabel lblSub = new JLabel("Administrá el personal registrado en el sistema");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(GRIS);
        JPanel headerTexto = new JPanel(new GridLayout(2, 1, 0, 2));
        headerTexto.setBackground(FONDO);
        headerTexto.add(lblTitulo);
        headerTexto.add(lblSub);
        header.add(headerTexto, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Nombre", "Correo", "Salario Base", "Tipo Contrato", "Fecha Ingreso"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaEmpleados.setRowHeight(36);
        tablaEmpleados.setShowVerticalLines(false);
        tablaEmpleados.setGridColor(BORDE);
        tablaEmpleados.setBackground(Color.WHITE);
        tablaEmpleados.setSelectionBackground(new Color(219, 234, 254));
        tablaEmpleados.setSelectionForeground(TEXTO);
        tablaEmpleados.setIntercellSpacing(new Dimension(0, 0));

        // Header tabla
        JTableHeader tableHeader = tablaEmpleados.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tableHeader.setBackground(FONDO);
        tableHeader.setForeground(GRIS);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        tableHeader.setPreferredSize(new Dimension(0, 36));

        // Renderer centrado
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < columnas.length; i++) {
            tablaEmpleados.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollTabla = new JScrollPane(tablaEmpleados);
        scrollTabla.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEmpleados.getSelectedRow() >= 0) {
                cargarEmpleadoEnFormulario();
            }
        });

        // Formulario lateral
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(24, 20, 20, 20)));
        panelFormulario.setPreferredSize(new Dimension(290, 0));

        lblTituloFormulario = new JLabel("Nuevo empleado");
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTituloFormulario.setForeground(TEXTO);
        lblTituloFormulario.setAlignmentX(LEFT_ALIGNMENT);

        txtId = crearCampo("ID");
        txtNombre = crearCampo("Nombre completo");
        txtCorreo = crearCampo("Correo electrónico");
        txtSalario = crearCampo("Salario base (₡)");
        txtFecha = crearCampo("Fecha ingreso (YYYY-MM-DD)");

        JLabel lblContrato = new JLabel("TIPO DE CONTRATO");
        lblContrato.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblContrato.setForeground(GRIS);
        lblContrato.setAlignmentX(LEFT_ALIGNMENT);

        cmbTipoContrato = new JComboBox<>(new String[]{"TIEMPO_COMPLETO", "MEDIO_TIEMPO"});
        cmbTipoContrato.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoContrato.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbTipoContrato.setAlignmentX(LEFT_ALIGNMENT);
        cmbTipoContrato.setBackground(Color.WHITE);

        // Botones
        btnGuardar = crearBoton("Guardar", AZUL);
        btnEditar = crearBoton("Actualizar", VERDE);
        btnEliminar = crearBoton("Eliminar", ROJO);
        btnLimpiar = crearBoton("Limpiar", GRIS);

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        panelBotones.setAlignmentX(LEFT_ALIGNMENT);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelFormulario.add(lblTituloFormulario);
        panelFormulario.add(Box.createVerticalStrut(16));
        panelFormulario.add(txtId.getParent());
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(txtNombre.getParent());
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(txtCorreo.getParent());
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(txtSalario.getParent());
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(txtFecha.getParent());
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(lblContrato);
        panelFormulario.add(Box.createVerticalStrut(4));
        panelFormulario.add(cmbTipoContrato);
        panelFormulario.add(Box.createVerticalStrut(20));
        panelFormulario.add(panelBotones);

        add(scrollTabla, BorderLayout.CENTER);
        add(panelFormulario, BorderLayout.EAST);

        registrarEventos();
    }

    // ── Eventos ──────────────────────────────────────────────────────────────
    /**
     * Registra los listeners de los botones del formulario.
     */
    private void registrarEventos() {
        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnEditar.addActionListener(e -> actualizarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    // ── Operaciones CRUD ─────────────────────────────────────────────────────
    /**
     * Crea un nuevo empleado con los datos del formulario y lo guarda. También
     * inicializa su saldo de vacaciones en cero.
     */
    private void guardarEmpleado() {
        try {
            Empleado emp = leerFormulario();
            empleadoDAO.guardar(emp);
            GestionVacaciones gv = new GestionVacaciones(null, emp, vacacionesDAO);
            gv.inicializarSaldo();
            mostrarExito("Empleado guardado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (IllegalArgumentException ex) {
            mostrarError("Datos inválidos: " + ex.getMessage());
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    /**
     * Actualiza los datos del empleado seleccionado en la tabla.
     */
    private void actualizarEmpleado() {
        try {
            Empleado emp = leerFormulario();
            empleadoDAO.actualizar(emp);
            mostrarExito("Empleado actualizado correctamente.");
            cargarTabla();
            limpiarFormulario();
        } catch (IllegalArgumentException ex) {
            mostrarError("Datos inválidos: " + ex.getMessage());
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al actualizar: " + ex.getMessage());
        }
    }

    /**
     * Elimina el empleado seleccionado tras confirmar con el usuario.
     */
    private void eliminarEmpleado() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            mostrarError("Seleccione un empleado.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el empleado con ID " + id + "? Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                empleadoDAO.eliminar(id);
                vacacionesDAO.eliminar(id);
                mostrarExito("Empleado eliminado.");
                cargarTabla();
                limpiarFormulario();
            } catch (ArchivoInvalidoException ex) {
                mostrarError("Error al eliminar: " + ex.getMessage());
            }
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────
    /**
     * Recarga la tabla con todos los empleados del archivo.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Empleado> lista = empleadoDAO.listarTodos();
            for (Empleado e : lista) {
                modeloTabla.addRow(new Object[]{
                    e.getId(), e.getNombre(), e.getCorreo(),
                    String.format("₡%,.2f", e.getSalarioBase()),
                    e.getTipoContrato(), e.getFechaIngreso().toString()
                });
            }
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al cargar empleados: " + ex.getMessage());
        }
    }

    /**
     * Carga los datos del empleado seleccionado en la tabla al formulario.
     */
    private void cargarEmpleadoEnFormulario() {
        int fila = tablaEmpleados.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtCorreo.setText(modeloTabla.getValueAt(fila, 2).toString());
        String salarioStr = modeloTabla.getValueAt(fila, 3).toString().replace("₡", "").replace(",", "");
        txtSalario.setText(salarioStr);
        cmbTipoContrato.setSelectedItem(modeloTabla.getValueAt(fila, 4).toString());
        txtFecha.setText(modeloTabla.getValueAt(fila, 5).toString());
        lblTituloFormulario.setText("Editar empleado");
        txtId.setEditable(false);
        modoEdicion = true;
        btnGuardar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    /**
     * Lee los campos del formulario y construye un objeto {@link Empleado}.
     *
     * @return Empleado construido con los datos del formulario.
     * @throws IllegalArgumentException si algún campo está vacío o tiene
     * formato inválido.
     */
    private Empleado leerFormulario() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String salStr = txtSalario.getText().trim();
        String fecha = txtFecha.getText().trim();
        String tipo = (String) cmbTipoContrato.getSelectedItem();

        if (id.isEmpty() || nombre.isEmpty() || correo.isEmpty() || salStr.isEmpty() || fecha.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }
        double salario;
        try {
            salario = Double.parseDouble(salStr);
            if (salario <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El salario debe ser un número mayor a cero.");
        }
        LocalDate fechaIngreso;
        try {
            fechaIngreso = LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida. Use el formato YYYY-MM-DD.");
        }
        return new Empleado(id, nombre, correo, salario, tipo, fechaIngreso);
    }

    /**
     * Limpia todos los campos del formulario y regresa al modo creación.
     */
    private void limpiarFormulario() {
        try {
            txtId.setText(empleadoDAO.generarSiguienteId());
        } catch (ArchivoInvalidoException ex) {
            txtId.setText("");
        }
        txtNombre.setText("");
        txtCorreo.setText("");
        txtSalario.setText("");
        txtFecha.setText("");
        cmbTipoContrato.setSelectedIndex(0);
        txtId.setEditable(false);
        lblTituloFormulario.setText("Nuevo empleado");
        modoEdicion = false;
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaEmpleados.clearSelection();
    }

    /**
     * Crea un panel con etiqueta en mayúsculas y campo de texto estilizado.
     *
     * @param etiqueta Texto de la etiqueta superior del campo.
     * @return El {@link JTextField} creado (su parent contiene la etiqueta).
     */
    private JTextField crearCampo(String etiqueta) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lbl = new JLabel(etiqueta.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(GRIS);

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(TEXTO);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(txt, BorderLayout.CENTER);
        return txt;
    }

    /**
     * Crea un botón redondeado estilizado para el panel de acciones.
     *
     * @param texto Texto del botón.
     * @param color Color de fondo del botón.
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
                g2.setColor(isEnabled() ? Color.WHITE : new Color(150, 150, 160));
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
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    /**
     * Muestra un mensaje de éxito en un diálogo informativo.
     *
     * @param mensaje Texto del mensaje.
     */
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un mensaje de error en un diálogo de advertencia.
     *
     * @param mensaje Texto del error.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
