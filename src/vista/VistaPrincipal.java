/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import reportes.VistaReportes;

/**
 * * Ventana principal del sistema de nómina.
 * <p>
 * Contiene la barra lateral de navegación con los módulos: Empleados, Nómina,
 * Vacaciones, Reportes y Configuración.
 * </p>
 *
 * @author ekaro
 * @version 1.0
 */
public class VistaPrincipal extends JFrame {

    private Usuario usuarioActual;
    private JPanel panelContenido;
    private JButton btnActivo;

    private static final Color COLOR_SIDEBAR = new Color(25, 25, 55);
    private static final Color COLOR_BTN_ACTIVO = new Color(50, 50, 100);
    private static final Color COLOR_BTN_HOVER = new Color(40, 40, 80);
    private static final Color COLOR_FONDO = new Color(245, 245, 248);

    /**
     * Construye la ventana principal para el usuario autenticado.
     *
     * @param usuarioActual Usuario que completó el proceso de login.
     */
    public VistaPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Sistema de Nómina");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));

        JPanel panelLogo = new JPanel(new GridLayout(2, 1, 0, 2));
        panelLogo.setBackground(COLOR_SIDEBAR);
        panelLogo.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel lblSistema = new JLabel("Nómina CR");
        lblSistema.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSistema.setForeground(Color.WHITE);

        JLabel lblRol = new JLabel(usuarioActual.getNombreUsuario()
                + " · " + usuarioActual.getRol());
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(new Color(180, 180, 210));

        panelLogo.add(lblSistema);
        panelLogo.add(lblRol);

        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(COLOR_SIDEBAR);
        panelMenu.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnEmpleados = crearBotonMenu("Empleados");
        JButton btnNomina = crearBotonMenu("Nómina");
        JButton btnVacaciones = crearBotonMenu("Vacaciones");
        JButton btnReportes = crearBotonMenu("Reportes");
        JButton btnConfiguracion = crearBotonMenu("Configuración");

        panelMenu.add(btnEmpleados);
        panelMenu.add(btnNomina);
        panelMenu.add(btnVacaciones);
        panelMenu.add(btnReportes);
        panelMenu.add(Box.createVerticalGlue());
        panelMenu.add(btnConfiguracion);

        JPanel panelCerrar = new JPanel(new BorderLayout());
        panelCerrar.setBackground(COLOR_SIDEBAR);
        panelCerrar.setBorder(new EmptyBorder(0, 12, 16, 12));
        JButton btnCerrar = new JButton("Cerrar sesión");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrar.setForeground(new Color(180, 180, 210));
        btnCerrar.setBackground(new Color(40, 20, 20));
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> cerrarSesion());
        panelCerrar.add(btnCerrar);

        sidebar.add(panelLogo, BorderLayout.NORTH);
        sidebar.add(panelMenu, BorderLayout.CENTER);
        sidebar.add(panelCerrar, BorderLayout.SOUTH);

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(COLOR_FONDO);
        mostrarBienvenida();

        add(sidebar, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        btnEmpleados.addActionListener(e -> {
            activarBoton(btnEmpleados);
            cargarVista(new VistaEmpleados());
        });
        btnNomina.addActionListener(e -> {
            activarBoton(btnNomina);
            cargarVista(new VistaNomina());
        });
        btnVacaciones.addActionListener(e -> {
            activarBoton(btnVacaciones);
            cargarVista(new VistaVacaciones());
        });
        btnReportes.addActionListener(e -> {
            activarBoton(btnReportes);
            cargarVista(new VistaReportes());
        });
        btnConfiguracion.addActionListener(e -> {
            activarBoton(btnConfiguracion);
            cargarVista(new VistaConfiguracion());
        });
    }

    private void cargarVista(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarBienvenida() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(COLOR_FONDO);
        JPanel tarjeta = new JPanel(new GridLayout(3, 1, 0, 8));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(32, 48, 32, 48)));

        JLabel l1 = new JLabel("Bienvenido, " + usuarioActual.getNombreUsuario(),
                SwingConstants.CENTER);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l1.setForeground(new Color(30, 30, 60));

        JLabel l2 = new JLabel("Seleccione un módulo en el menú lateral.",
                SwingConstants.CENTER);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l2.setForeground(new Color(120, 120, 140));

        JLabel l3 = new JLabel(
                "Empleados · Nómina · Vacaciones · Reportes · Configuración",
                SwingConstants.CENTER);
        l3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l3.setForeground(new Color(160, 160, 180));

        tarjeta.add(l1);
        tarjeta.add(l2);
        tarjeta.add(l3);
        p.add(tarjeta);
        cargarVista(p);
    }

    private void activarBoton(JButton btn) {
        if (btnActivo != null) {
            btnActivo.setBackground(COLOR_SIDEBAR);
        }
        btnActivo = btn;
        btnActivo.setBackground(COLOR_BTN_ACTIVO);
    }

    private void cerrarSesion() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            dispose();
            new VistaLogin().setVisible(true);
        }
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(200, 200, 220));
        btn.setBackground(COLOR_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) {
                    btn.setBackground(COLOR_BTN_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) {
                    btn.setBackground(COLOR_SIDEBAR);
                }
            }
        });
        return btn;
    }

    /**
     * @return Usuario actualmente autenticado.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
