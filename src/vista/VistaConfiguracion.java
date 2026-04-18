/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import datos.ConfigNominaDAO;
import entidades.ConfigNomina;
import entidades.TramoRenta;
import excepciones.ArchivoInvalidoException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para la configuración de porcentajes de nómina y tramos de renta.
 * <p>
 * Permite al administrador actualizar los porcentajes de cargas sociales,
 * aportes patronales y los tramos del impuesto de renta, los cuales se
 * persisten en los archivos de configuración.
 * </p>
 * @version 1.1
 */
public class VistaConfiguracion extends JPanel {

    // ── Campos de porcentajes ─────────────────────────────────────────────────
    /**
     * Campo para el porcentaje CCSS del trabajador.
     */
    private JTextField txtCCSSTrabajador, txtROPTrabajador, txtBPTrabajador;

    /**
     * Campos para los aportes patronales.
     */
    private JTextField txtCCSSPatronal, txtINAPatronal, txtBPPatronal;

    /**
     * Campos para FCL y ROP patronal.
     */
    private JTextField txtFCL, txtROPPatronal;

    /**
     * Botones de acción del panel de porcentajes.
     */
    private JButton btnGuardarConfig, btnRecargar;

    // ── Tabla de tramos de renta ──────────────────────────────────────────────
    /**
     * Tabla que muestra los tramos de renta configurados.
     */
    private JTable tablaTramos;

    /**
     * Modelo de datos de la tabla de tramos.
     */
    private DefaultTableModel modeloTramos;

    /**
     * Campos para agregar nuevos tramos.
     */
    private JTextField txtLimInf, txtLimSup, txtPorcTramo;

    /**
     * Botones de la tabla de tramos.
     */
    private JButton btnAgregarTramo, btnEliminarTramo, btnGuardarTramos;

    // ── DAO ───────────────────────────────────────────────────────────────────
    /**
     * DAO de configuración de nómina.
     */
    private ConfigNominaDAO configDAO;

    /**
     * Configuración actual cargada desde el archivo.
     */
    private ConfigNomina config;

    // ── Paleta ────────────────────────────────────────────────────────────────
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
     * Color para botón guardar.
     */
    private static final Color VERDE = new Color(22, 163, 74);

    /**
     * Color para botón eliminar.
     */
    private static final Color ROJO = new Color(220, 38, 38);

    // ── Constructor ───────────────────────────────────────────────────────────
    /**
     * Construye la vista de configuración e inicializa los componentes.
     */
    public VistaConfiguracion() {
        this.configDAO = new ConfigNominaDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(FONDO);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        inicializarComponentes();
        cargarConfiguracion();
    }

    // ── Inicialización ────────────────────────────────────────────────────────
    /**
     * Crea y organiza todos los componentes de la vista de configuración.
     */
    private void inicializarComponentes() {

        // Encabezado
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lblTitulo = new JLabel("Configuración de Nómina");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TEXTO);
        JLabel lblSub = new JLabel("Parámetros de cargas sociales y tramos de renta");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(GRIS);
        header.add(lblTitulo);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCentro.setBackground(FONDO);

        // ── Panel de porcentajes ─────────────────────────────────────────────
        JPanel panelPorc = new JPanel();
        panelPorc.setLayout(new BoxLayout(panelPorc, BoxLayout.Y_AXIS));
        panelPorc.setBackground(Color.WHITE);
        panelPorc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        panelPorc.add(seccion("Cargas del trabajador"));
        panelPorc.add(etiqueta("Ingresá los valores en decimal  (ej: 0.0917 = 9.17%)"));
        panelPorc.add(Box.createVerticalStrut(10));

        txtCCSSTrabajador = agregarCampo(panelPorc, "CCSS TRABAJADOR");
        txtROPTrabajador = agregarCampo(panelPorc, "ROP TRABAJADOR");
        txtBPTrabajador = agregarCampo(panelPorc, "BANCO POPULAR TRABAJADOR");

        panelPorc.add(Box.createVerticalStrut(14));
        panelPorc.add(seccion("Aportes patronales"));
        panelPorc.add(Box.createVerticalStrut(6));

        txtCCSSPatronal = agregarCampo(panelPorc, "CCSS PATRONAL");
        txtINAPatronal = agregarCampo(panelPorc, "INA PATRONAL");
        txtBPPatronal = agregarCampo(panelPorc, "BANCO POPULAR PATRONAL");
        txtFCL = agregarCampo(panelPorc, "FCL");
        txtROPPatronal = agregarCampo(panelPorc, "ROP PATRONAL");

        panelPorc.add(Box.createVerticalStrut(18));

        JPanel panelBotonesPorc = new JPanel(new GridLayout(1, 2, 8, 0));
        panelBotonesPorc.setBackground(Color.WHITE);
        panelBotonesPorc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelBotonesPorc.setAlignmentX(LEFT_ALIGNMENT);
        btnGuardarConfig = crearBoton("Guardar porcentajes", AZUL);
        btnRecargar = crearBoton("Recargar", GRIS);
        panelBotonesPorc.add(btnGuardarConfig);
        panelBotonesPorc.add(btnRecargar);
        panelPorc.add(panelBotonesPorc);
        panelPorc.add(Box.createVerticalGlue());

        // ── Panel de tramos de renta ─────────────────────────────────────────
        JPanel panelTramos = new JPanel(new BorderLayout(0, 12));
        panelTramos.setBackground(Color.WHITE);
        panelTramos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JPanel tramoHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        tramoHeader.setBackground(Color.WHITE);
        JLabel lblTramos = new JLabel("Tramos de renta");
        lblTramos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTramos.setForeground(TEXTO);
        JLabel lblTramosSub = new JLabel("Valores mensuales en ₡ · porcentaje en decimal");
        lblTramosSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTramosSub.setForeground(GRIS);
        tramoHeader.add(lblTramos);
        tramoHeader.add(lblTramosSub);

        // Tabla
        String[] cols = {"Límite inferior", "Límite superior", "Porcentaje"};
        modeloTramos = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaTramos = new JTable(modeloTramos);
        tablaTramos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaTramos.setRowHeight(32);
        tablaTramos.setShowVerticalLines(false);
        tablaTramos.setGridColor(BORDE);
        tablaTramos.setBackground(Color.WHITE);
        tablaTramos.setSelectionBackground(new Color(219, 234, 254));
        tablaTramos.setSelectionForeground(TEXTO);

        JTableHeader tableHeader = tablaTramos.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tableHeader.setBackground(FONDO);
        tableHeader.setForeground(GRIS);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        tableHeader.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length; i++) {
            tablaTramos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tablaTramos);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);

        // Formulario agregar tramo
        JPanel panelAgregar = new JPanel(new BorderLayout(0, 8));
        panelAgregar.setBackground(Color.WHITE);

        JPanel camposTramo = new JPanel(new GridLayout(1, 3, 8, 0));
        camposTramo.setBackground(Color.WHITE);
        txtLimInf = campoCompacto("Límite inferior (₡)");
        txtLimSup = campoCompacto("Límite superior (₡)");
        txtPorcTramo = campoCompacto("Porcentaje (decimal)");
        camposTramo.add(txtLimInf);
        camposTramo.add(txtLimSup);
        camposTramo.add(txtPorcTramo);

        JPanel botonesTramo = new JPanel(new GridLayout(1, 3, 8, 0));
        botonesTramo.setBackground(Color.WHITE);
        btnAgregarTramo = crearBoton("Agregar", VERDE);
        btnEliminarTramo = crearBoton("Eliminar último", ROJO);
        btnGuardarTramos = crearBoton("Guardar tramos", AZUL);
        botonesTramo.add(btnAgregarTramo);
        botonesTramo.add(btnEliminarTramo);
        botonesTramo.add(btnGuardarTramos);

        panelAgregar.add(camposTramo, BorderLayout.CENTER);
        panelAgregar.add(botonesTramo, BorderLayout.SOUTH);

        panelTramos.add(tramoHeader, BorderLayout.NORTH);
        panelTramos.add(scroll, BorderLayout.CENTER);
        panelTramos.add(panelAgregar, BorderLayout.SOUTH);

        panelCentro.add(panelPorc);
        panelCentro.add(panelTramos);
        add(panelCentro, BorderLayout.CENTER);

        registrarEventos();
    }

    // ── Eventos ───────────────────────────────────────────────────────────────
    /**
     * Registra los listeners de todos los botones de la vista.
     */
    private void registrarEventos() {
        btnGuardarConfig.addActionListener(e -> guardarPorcentajes());
        btnRecargar.addActionListener(e -> cargarConfiguracion());
        btnAgregarTramo.addActionListener(e -> agregarTramo());
        btnEliminarTramo.addActionListener(e -> eliminarUltimoTramo());
        btnGuardarTramos.addActionListener(e -> guardarTramos());
    }

    // ── Lógica ────────────────────────────────────────────────────────────────
    /**
     * Lee la configuración desde los archivos y puebla los campos de la vista.
     */
    private void cargarConfiguracion() {
        try {
            config = configDAO.cargarConfig();
            txtCCSSTrabajador.setText(String.valueOf(config.getPorcCCSSTrabajador()));
            txtROPTrabajador.setText(String.valueOf(config.getPorcROPTrabajador()));
            txtBPTrabajador.setText(String.valueOf(config.getPorcBancoPopularTrabajador()));
            txtCCSSPatronal.setText(String.valueOf(config.getPorcCCSSPatronal()));
            txtINAPatronal.setText(String.valueOf(config.getPorcINAPatronal()));
            txtBPPatronal.setText(String.valueOf(config.getPorcBancoPopularPatronal()));
            txtFCL.setText(String.valueOf(config.getPorcFCL()));
            txtROPPatronal.setText(String.valueOf(config.getPorcROPPatronal()));
            cargarTablaTramos();
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al cargar configuración: " + ex.getMessage());
        }
    }

    /**
     * Lee los porcentajes de los campos y los guarda en el archivo.
     */
    private void guardarPorcentajes() {
        try {
            config.setPorcCCSSTrabajador(Double.parseDouble(txtCCSSTrabajador.getText().trim()));
            config.setPorcROPTrabajador(Double.parseDouble(txtROPTrabajador.getText().trim()));
            config.setPorcBancoPopularTrabajador(Double.parseDouble(txtBPTrabajador.getText().trim()));
            config.setPorcCCSSPatronal(Double.parseDouble(txtCCSSPatronal.getText().trim()));
            config.setPorcINAPatronal(Double.parseDouble(txtINAPatronal.getText().trim()));
            config.setPorcBancoPopularPatronal(Double.parseDouble(txtBPPatronal.getText().trim()));
            config.setPorcFCL(Double.parseDouble(txtFCL.getText().trim()));
            config.setPorcROPPatronal(Double.parseDouble(txtROPPatronal.getText().trim()));
            configDAO.guardarConfig(config);
            JOptionPane.showMessageDialog(this,
                    "Porcentajes guardados correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            mostrarError("Todos los porcentajes deben ser valores decimales (ej: 0.0917).");
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
        }
    }

    /**
     * Agrega un nuevo tramo de renta a la tabla usando los campos del
     * formulario.
     */
    private void agregarTramo() {
        try {
            double inf = Double.parseDouble(txtLimInf.getText().trim());
            double sup = Double.parseDouble(txtLimSup.getText().trim());
            double porc = Double.parseDouble(txtPorcTramo.getText().trim());
            if (sup <= inf) {
                mostrarError("El límite superior debe ser mayor al inferior.");
                return;
            }
            modeloTramos.addRow(new Object[]{
                String.format("%.2f", inf),
                String.format("%.2f", sup),
                String.format("%.4f", porc)
            });
            txtLimInf.setText("");
            txtLimSup.setText("");
            txtPorcTramo.setText("");
        } catch (NumberFormatException ex) {
            mostrarError("Los límites y porcentaje deben ser valores numéricos.");
        }
    }

    /**
     * Elimina el último tramo de la tabla.
     */
    private void eliminarUltimoTramo() {
        int filas = modeloTramos.getRowCount();
        if (filas > 0) {
            modeloTramos.removeRow(filas - 1);
        }
    }

    /**
     * Persiste los tramos mostrados en la tabla en el archivo de configuración.
     */
    private void guardarTramos() {
        List<TramoRenta> tramos = new ArrayList<>();
        for (int i = 0; i < modeloTramos.getRowCount(); i++) {
            double inf = Double.parseDouble(modeloTramos.getValueAt(i, 0).toString());
            double sup = Double.parseDouble(modeloTramos.getValueAt(i, 1).toString());
            double porc = Double.parseDouble(modeloTramos.getValueAt(i, 2).toString());
            tramos.add(new TramoRenta(inf, sup, porc));
        }
        config.setTramosRenta(tramos);
        try {
            configDAO.guardarConfig(config);
            JOptionPane.showMessageDialog(this,
                    "Tramos de renta guardados correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al guardar tramos: " + ex.getMessage());
        }
    }

    /**
     * Puebla la tabla de tramos con los datos de la configuración actual.
     */
    private void cargarTablaTramos() {
        modeloTramos.setRowCount(0);
        for (TramoRenta t : config.getTramosRenta()) {
            modeloTramos.addRow(new Object[]{
                String.format("%.2f", t.getLimiteInferior()),
                t.getLimiteSuperior() == Double.MAX_VALUE ? "Sin límite"
                : String.format("%.2f", t.getLimiteSuperior()),
                String.format("%.4f", t.getPorcentaje())
            });
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────
    /**
     * Agrega un par etiqueta + campo de texto al panel dado y devuelve el
     * campo.
     *
     * @param panel Panel donde se agregan los componentes.
     * @param etiqueta Texto descriptivo del campo en mayúsculas.
     * @return El {@link JTextField} creado.
     */
    private JTextField agregarCampo(JPanel panel, String etiqueta) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(GRIS);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setForeground(TEXTO);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));
        panel.add(txt);
        panel.add(Box.createVerticalStrut(8));
        return txt;
    }

    /**
     * Crea una etiqueta de sección con título en negrita.
     *
     * @param texto Texto de la sección.
     * @return Etiqueta estilizada.
     */
    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXTO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    /**
     * Crea una etiqueta pequeña descriptiva.
     *
     * @param texto Texto de la etiqueta.
     * @return Etiqueta estilizada.
     */
    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(GRIS);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un campo de texto compacto con tooltip para la tabla de tramos.
     *
     * @param placeholder Texto del tooltip descriptivo.
     * @return Campo de texto configurado.
     */
    private JTextField campoCompacto(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setToolTipText(placeholder);
        txt.setForeground(TEXTO);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
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
        btn.setPreferredSize(new Dimension(0, 38));
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
