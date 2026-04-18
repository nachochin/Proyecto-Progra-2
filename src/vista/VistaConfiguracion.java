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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para la configuración de porcentajes de nómina y tramos de renta.
 * <p>
 * Permite al administrador actualizar los porcentajes de cargas sociales,
 * aportes patronales y los tramos del impuesto de renta, los cuales se
 * persisten en los archivos de configuración.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public class VistaConfiguracion extends JPanel {

    // -------------------------------------------------------------------------
    // Campos de porcentajes
    // -------------------------------------------------------------------------
    private JTextField txtCCSSTrabajador, txtROPTrabajador, txtBPTrabajador;
    private JTextField txtCCSSPatronal, txtINAPatronal, txtBPPatronal;
    private JTextField txtFCL, txtROPPatronal;
    private JButton btnGuardarConfig, btnRecargar;

    // Tabla de tramos de renta
    private JTable tablaTramos;
    private DefaultTableModel modeloTramos;
    private JTextField txtLimInf, txtLimSup, txtPorcTramo;
    private JButton btnAgregarTramo, btnEliminarTramo, btnGuardarTramos;

    // DAO
    private ConfigNominaDAO configDAO;
    private ConfigNomina config;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Construye la vista de configuración e inicializa los componentes.
     */
    public VistaConfiguracion() {
        this.configDAO = new ConfigNominaDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
        cargarConfiguracion();
    }

    // -------------------------------------------------------------------------
    // Inicialización
    // -------------------------------------------------------------------------
    /**
     * Crea y organiza todos los componentes de la vista de configuración.
     */
    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Configuración de Nómina");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 60));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 14, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCentro.setBackground(new Color(245, 245, 248));

        // ---- Panel de porcentajes ----
        JPanel panelPorc = new JPanel();
        panelPorc.setLayout(new BoxLayout(panelPorc, BoxLayout.Y_AXIS));
        panelPorc.setBackground(Color.WHITE);
        panelPorc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        // Cargas del trabajador
        panelPorc.add(seccion("Cargas del trabajador (decimal, ej: 0.0917)"));
        txtCCSSTrabajador = agregarCampo(panelPorc, "CCSS trabajador");
        txtROPTrabajador = agregarCampo(panelPorc, "ROP trabajador");
        txtBPTrabajador = agregarCampo(panelPorc, "Banco Popular trabajador");

        panelPorc.add(Box.createRigidArea(new Dimension(0, 12)));
        panelPorc.add(seccion("Aportes patronales (decimal, ej: 0.2633)"));
        txtCCSSPatronal = agregarCampo(panelPorc, "CCSS patronal");
        txtINAPatronal = agregarCampo(panelPorc, "INA patronal");
        txtBPPatronal = agregarCampo(panelPorc, "Banco Popular patronal");
        txtFCL = agregarCampo(panelPorc, "FCL");
        txtROPPatronal = agregarCampo(panelPorc, "ROP patronal");

        panelPorc.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel panelBotonesPorc = new JPanel(new GridLayout(1, 2, 8, 0));
        panelBotonesPorc.setBackground(Color.WHITE);
        panelBotonesPorc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panelBotonesPorc.setAlignmentX(LEFT_ALIGNMENT);
        btnGuardarConfig = boton("Guardar porcentajes", new Color(30, 30, 60));
        btnRecargar = boton("Recargar desde archivo", new Color(80, 80, 120));
        panelBotonesPorc.add(btnGuardarConfig);
        panelBotonesPorc.add(btnRecargar);
        panelPorc.add(panelBotonesPorc);
        panelPorc.add(Box.createVerticalGlue());

        // ---- Panel de tramos de renta ----
        JPanel panelTramos = new JPanel(new BorderLayout(0, 10));
        panelTramos.setBackground(Color.WHITE);
        panelTramos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTramos = new JLabel("Tramos de renta (valores mensuales en ₡)");
        lblTramos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTramos.setForeground(new Color(30, 30, 60));

        String[] cols = {"Límite inferior", "Límite superior", "Porcentaje"};
        modeloTramos = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tablaTramos = new JTable(modeloTramos);
        tablaTramos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaTramos.setRowHeight(26);
        tablaTramos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaTramos.setGridColor(new Color(230, 230, 235));
        JScrollPane scroll = new JScrollPane(tablaTramos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));

        // Formulario para agregar tramo
        JPanel panelAgregar = new JPanel(new GridLayout(2, 3, 8, 6));
        panelAgregar.setBackground(Color.WHITE);

        txtLimInf = campoCompacto("Límite inferior (₡)");
        txtLimSup = campoCompacto("Límite superior (₡)");
        txtPorcTramo = campoCompacto("Porcentaje (decimal)");

        btnAgregarTramo = boton("Agregar tramo", new Color(30, 100, 60));
        btnEliminarTramo = boton("Eliminar último", new Color(160, 50, 30));
        btnGuardarTramos = boton("Guardar tramos", new Color(30, 30, 60));

        panelAgregar.add(txtLimInf);
        panelAgregar.add(txtLimSup);
        panelAgregar.add(txtPorcTramo);
        panelAgregar.add(btnAgregarTramo);
        panelAgregar.add(btnEliminarTramo);
        panelAgregar.add(btnGuardarTramos);

        panelTramos.add(lblTramos, BorderLayout.NORTH);
        panelTramos.add(scroll, BorderLayout.CENTER);
        panelTramos.add(panelAgregar, BorderLayout.SOUTH);

        panelCentro.add(panelPorc);
        panelCentro.add(panelTramos);
        add(panelCentro, BorderLayout.CENTER);

        registrarEventos();
    }

    // -------------------------------------------------------------------------
    // Eventos
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Lógica
    // -------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------
    /**
     * Agrega un par etiqueta + campo de texto al panel dado y devuelve el
     * campo.
     *
     * @param panel Panel donde se agregan los componentes.
     * @param etiqueta Texto descriptivo del campo.
     * @return El {@link JTextField} creado.
     */
    private JTextField agregarCampo(JPanel panel, String etiqueta) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(90, 90, 110));
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 220), 1, true),
                new EmptyBorder(3, 8, 3, 8)));

        panel.add(lbl);
        panel.add(txt);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        return txt;
    }

    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(30, 30, 60));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    private JTextField campoCompacto(String placeholder) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setToolTipText(placeholder);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 220), 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        return txt;
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

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
