/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import reportes.VistaReportes;

/**
 * Ventana principal del Sistema de Nómina CR.
 * <p>
 * Contiene la barra lateral de navegación con acceso a los módulos: Empleados,
 * Nómina, Vacaciones, Reportes y Configuración. Se inicializa con el usuario
 * autenticado y muestra una pantalla de bienvenida al arrancar.
 * </p>
 *
 * @version 1.1
 */
public class VistaPrincipal extends JFrame {

    /**
     * Usuario que completó el proceso de autenticación.
     */
    private Usuario usuarioActual;

    /**
     * Panel central donde se cargan los módulos del sistema.
     */
    private JPanel panelContenido;

    /**
     * Referencia al botón del menú actualmente seleccionado.
     */
    private JButton btnActivo;

    /**
     * Color de acento principal — azul corporativo.
     */
    private static final Color AZUL = new Color(37, 99, 235);

    /**
     * Color de fondo del sidebar.
     */
    private static final Color SIDEBAR = new Color(30, 30, 60);

    /**
     * Color de fondo general de la aplicación.
     */
    private static final Color FONDO = new Color(248, 249, 252);

    /**
     * Color del texto de los ítems del sidebar.
     */
    private static final Color TEXTO_SIDEBAR = new Color(200, 200, 220);

    /**
     * Color del texto secundario.
     */
    private static final Color SUBTEXTO = new Color(100, 100, 130);

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

    /**
     * Configura las propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setTitle("Nómina CR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
    }

    /**
     * Crea y dispone todos los componentes visuales de la ventana principal,
     * incluyendo el sidebar de navegación y el área de contenido.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // ── Sidebar ──────────────────────────────────────────────
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Logo
        JPanel panelLogo = new JPanel();
        panelLogo.setLayout(new BoxLayout(panelLogo, BoxLayout.Y_AXIS));
        panelLogo.setBackground(SIDEBAR);
        panelLogo.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel logoIcon = new JLabel("N") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AZUL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("N")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("N", x, y);
                g2.dispose();
            }
        };
        logoIcon.setPreferredSize(new Dimension(32, 32));
        logoIcon.setMaximumSize(new Dimension(32, 32));
        logoIcon.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblNombre = new JLabel("Nómina CR");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblRol = new JLabel(usuarioActual.getNombreUsuario()
                + " · " + usuarioActual.getRol());
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(SUBTEXTO);
        lblRol.setAlignmentX(LEFT_ALIGNMENT);

        panelLogo.add(logoIcon);
        panelLogo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelLogo.add(lblNombre);
        panelLogo.add(Box.createRigidArea(new Dimension(0, 2)));
        panelLogo.add(lblRol);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 25));
        sep.setBackground(SIDEBAR);

        // Menú
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(SIDEBAR);
        panelMenu.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton btnEmpleados = crearBotonMenu("  Empleados");
        JButton btnNomina = crearBotonMenu("  Nómina");
        JButton btnVacaciones = crearBotonMenu("  Vacaciones");
        JButton btnReportes = crearBotonMenu("  Reportes");
        JButton btnConfiguracion = crearBotonMenu("  Configuración");

        panelMenu.add(btnEmpleados);
        panelMenu.add(btnNomina);
        panelMenu.add(btnVacaciones);
        panelMenu.add(btnReportes);
        panelMenu.add(Box.createVerticalGlue());
        panelMenu.add(btnConfiguracion);

        // Cerrar sesión
        JPanel panelCerrar = new JPanel(new BorderLayout());
        panelCerrar.setBackground(SIDEBAR);
        panelCerrar.setBorder(new EmptyBorder(0, 12, 16, 12));

        JButton btnCerrar = new JButton("Cerrar sesión");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrar.setForeground(new Color(180, 100, 100));
        btnCerrar.setBackground(new Color(60, 20, 20));
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(new EmptyBorder(8, 12, 8, 12));
        btnCerrar.addActionListener(e -> cerrarSesion());
        panelCerrar.add(btnCerrar);

        JPanel sidebarTop = new JPanel(new BorderLayout());
        sidebarTop.setBackground(SIDEBAR);
        sidebarTop.add(panelLogo, BorderLayout.CENTER);
        sidebarTop.add(sep, BorderLayout.SOUTH);

        sidebar.add(sidebarTop, BorderLayout.NORTH);
        sidebar.add(panelMenu, BorderLayout.CENTER);
        sidebar.add(panelCerrar, BorderLayout.SOUTH);

        // ── Contenido ────────────────────────────────────────────
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(FONDO);
        mostrarBienvenida();

        add(sidebar, BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        // Eventos de navegación
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

    /**
     * Reemplaza el contenido del panel principal con el panel recibido.
     *
     * @param panel Panel del módulo a mostrar.
     */
    private void cargarVista(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /**
     * Muestra la pantalla de bienvenida con el nombre del usuario autenticado y
     * los módulos disponibles.
     */
    private void mostrarBienvenida() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(FONDO);

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 238), 1, true),
                new EmptyBorder(40, 56, 40, 56)));

        JLabel icono = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(37, 99, 235, 20));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(AZUL);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("N")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("N", x, y);
                g2.dispose();
            }
        };
        icono.setPreferredSize(new Dimension(64, 64));
        icono.setMaximumSize(new Dimension(64, 64));
        icono.setAlignmentX(CENTER_ALIGNMENT);

        JLabel l1 = new JLabel("Bienvenido, " + usuarioActual.getNombreUsuario(),
                SwingConstants.CENTER);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l1.setForeground(new Color(30, 30, 60));
        l1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel l2 = new JLabel("Seleccione un módulo en el menú lateral.",
                SwingConstants.CENTER);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l2.setForeground(SUBTEXTO);
        l2.setAlignmentX(CENTER_ALIGNMENT);

        JLabel l3 = new JLabel("Empleados · Nómina · Vacaciones · Reportes · Configuración",
                SwingConstants.CENTER);
        l3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l3.setForeground(new Color(160, 160, 180));
        l3.setAlignmentX(CENTER_ALIGNMENT);

        tarjeta.add(icono);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 16)));
        tarjeta.add(l1);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 8)));
        tarjeta.add(l2);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 6)));
        tarjeta.add(l3);

        p.add(tarjeta);
        cargarVista(p);
    }

    /**
     * Marca el botón recibido como activo en el sidebar y restaura el anterior.
     *
     * @param btn Botón del menú a activar.
     */
    private void activarBoton(JButton btn) {
        if (btnActivo != null) {
            btnActivo.setBackground(SIDEBAR);
            btnActivo.setForeground(TEXTO_SIDEBAR);
            btnActivo.setOpaque(true);
            btnActivo.repaint();
        }
        btnActivo = btn;
        btnActivo.setBackground(new Color(50, 80, 160));
        btnActivo.setForeground(Color.WHITE);
        btnActivo.setOpaque(true);
        btnActivo.repaint();
    }

    /**
     * Muestra un diálogo de confirmación y cierra la sesión actual, regresando
     * a la ventana de login.
     */
    private void cerrarSesion() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            dispose();
            new VistaLogin().setVisible(true);
        }
    }

    /**
     * Crea un botón estilizado para el menú lateral del sidebar.
     *
     * @param texto Texto que se mostrará en el botón.
     * @return Botón configurado con el estilo del sidebar.
     */
    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXTO_SIDEBAR);
        btn.setBackground(SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) {
                    btn.setBackground(new Color(45, 45, 80));
                    btn.repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != btnActivo) {
                    btn.setBackground(SIDEBAR);
                    btn.repaint();
                }
            }
        });
        return btn;
    }

    /**
     * Retorna el usuario actualmente autenticado en el sistema.
     *
     * @return Usuario autenticado.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
