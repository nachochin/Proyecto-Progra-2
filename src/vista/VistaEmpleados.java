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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * * Vista para la gestión completa de empleados (CRUD).
 * <p>
 * Muestra una tabla con todos los empleados registrados y permite crear, editar
 * y eliminar empleados mediante un formulario integrado. Al crear un empleado
 * también inicializa su saldo de vacaciones en cero.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public class VistaEmpleados extends JPanel {

    // -------------------------------------------------------------------------
    // Componentes
    // -------------------------------------------------------------------------
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JTextField txtId, txtNombre, txtCorreo, txtSalario, txtFecha;
    private JComboBox<String> cmbTipoContrato;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar;
    private JLabel lblTituloFormulario;

    // DAOs
    private EmpleadoDAO empleadoDAO;
    private VacacionesDAO vacacionesDAO;

    /**
     * Indica si el formulario está en modo edición (true) o creación (false).
     */
    private boolean modoEdicion = false;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Construye la vista de gestión de empleados e inicializa los componentes.
     */
    public VistaEmpleados() {
        this.empleadoDAO = new EmpleadoDAO();
        this.vacacionesDAO = new VacacionesDAO();
        setLayout(new BorderLayout(12, 0));
        setBackground(new Color(245, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
        cargarTabla();
    }

    // -------------------------------------------------------------------------
    // Inicialización
    // -------------------------------------------------------------------------
    /**
     * Crea y organiza todos los componentes de la vista.
     */
    private void inicializarComponentes() {
        // --- Encabezado ---
        JLabel lblTitulo = new JLabel("Gestión de Empleados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 60));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 12, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Tabla ---
        String[] columnas = {"ID", "Nombre", "Correo", "Salario Base",
            "Tipo Contrato", "Fecha Ingreso"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaEmpleados.setRowHeight(28);
        tablaEmpleados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaEmpleados.setSelectionBackground(new Color(200, 210, 240));
        tablaEmpleados.setGridColor(new Color(230, 230, 235));

        JScrollPane scrollTabla = new JScrollPane(tablaEmpleados);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));

        // Listener de selección en tabla
        tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaEmpleados.getSelectedRow() >= 0) {
                cargarEmpleadoEnFormulario();
            }
        });

        // --- Formulario lateral ---
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        panelFormulario.setPreferredSize(new Dimension(280, 0));

        lblTituloFormulario = new JLabel("Nuevo empleado");
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTituloFormulario.setForeground(new Color(30, 30, 60));
        lblTituloFormulario.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtId = crearCampo("ID");
        txtNombre = crearCampo("Nombre completo");
        txtCorreo = crearCampo("Correo electrónico");
        txtSalario = crearCampo("Salario base (₡)");
        txtFecha = crearCampo("Fecha ingreso (YYYY-MM-DD)");

        JLabel lblContrato = new JLabel("Tipo de contrato");
        lblContrato.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContrato.setForeground(new Color(90, 90, 110));
        lblContrato.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbTipoContrato = new JComboBox<>(
                new String[]{"TIEMPO_COMPLETO", "MEDIO_TIEMPO"});
        cmbTipoContrato.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoContrato.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbTipoContrato.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 6, 6));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGuardar = crearBoton("Guardar", new Color(30, 120, 70));
        btnEditar = crearBoton("Actualizar", new Color(30, 80, 160));
        btnEliminar = crearBoton("Eliminar", new Color(180, 40, 40));
        btnLimpiar = crearBoton("Limpiar", new Color(100, 100, 120));

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        panelFormulario.add(lblTituloFormulario);
        panelFormulario.add(Box.createVerticalStrut(14));
        panelFormulario.add(txtId.getParent());
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(txtNombre.getParent());
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(txtCorreo.getParent());
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(txtSalario.getParent());
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(txtFecha.getParent());
        panelFormulario.add(Box.createVerticalStrut(8));
        panelFormulario.add(lblContrato);
        panelFormulario.add(Box.createVerticalStrut(4));
        panelFormulario.add(cmbTipoContrato);
        panelFormulario.add(Box.createVerticalStrut(16));
        panelFormulario.add(panelBotones);

        add(scrollTabla, BorderLayout.CENTER);
        add(panelFormulario, BorderLayout.EAST);

        registrarEventos();
    }

    // -------------------------------------------------------------------------
    // Eventos
    // -------------------------------------------------------------------------
    /**
     * Registra los listeners de los botones del formulario.
     */
    private void registrarEventos() {
        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnEditar.addActionListener(e -> actualizarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    // -------------------------------------------------------------------------
    // Operaciones CRUD
    // -------------------------------------------------------------------------
    /**
     * Crea un nuevo empleado con los datos del formulario y lo guarda. También
     * inicializa su saldo de vacaciones en cero.
     */
    private void guardarEmpleado() {
        try {
            Empleado emp = leerFormulario();
            empleadoDAO.guardar(emp);

            // Inicializar vacaciones para el nuevo empleado
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

    // -------------------------------------------------------------------------
    // Utilidades de la vista
    // -------------------------------------------------------------------------
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
        // Quitar formato de moneda para editar
        String salarioStr = modeloTabla.getValueAt(fila, 3).toString()
                .replace("₡", "").replace(",", "");
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

        if (id.isEmpty() || nombre.isEmpty() || correo.isEmpty()
                || salStr.isEmpty() || fecha.isEmpty()) {
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
        txtId.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtSalario.setText("");
        txtFecha.setText("");
        cmbTipoContrato.setSelectedIndex(0);
        txtId.setEditable(true);
        lblTituloFormulario.setText("Nuevo empleado");
        modoEdicion = false;
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaEmpleados.clearSelection();
    }

    /**
     * Crea un panel etiqueta + campo de texto alineado para el formulario.
     *
     * @param etiqueta Texto de la etiqueta superior del campo.
     * @return El {@link JTextField} creado (su parent contiene la etiqueta).
     */
    private JTextField crearCampo(String etiqueta) {
        JPanel panel = new JPanel(new BorderLayout(0, 3));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(90, 90, 110));

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 220), 1, true),
                new EmptyBorder(5, 8, 5, 8)));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(txt, BorderLayout.CENTER);
        return txt;
    }

    /**
     * Crea un botón estilizado para el panel de acciones del formulario.
     *
     * @param texto Texto del botón.
     * @param color Color de fondo del botón.
     * @return Botón configurado.
     */
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Muestra un mensaje de éxito en un diálogo informativo.
     *
     * @param mensaje Texto del mensaje.
     */
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un mensaje de error en un diálogo de advertencia.
     *
     * @param mensaje Texto del error.
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
