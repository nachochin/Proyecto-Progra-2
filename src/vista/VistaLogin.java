// Módulo desarrollado por Erika Rojas
package vista;

import datos.UsuarioDAO;
import entidades.Usuario;
import excepciones.ArchivoInvalidoException;
import excepciones.AutenticacionFallidaException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Ventana de inicio de sesión del Sistema de Nómina CR.
 * <p>
 * Presenta una interfaz minimalista con logo, campos de usuario y contraseña,
 * y botón de acceso. Valida las credenciales contra el archivo de usuarios
 * mediante {@link UsuarioDAO} y redirige a {@link VistaPrincipal} si el
 * acceso es correcto.
 * </p>
 *
 * @version 1.1
 */
public class VistaLogin extends JFrame {

    /** Campo de texto para el nombre de usuario. */
    private JTextField txtUsuario;

    /** Campo de texto enmascarado para la contraseña. */
    private JPasswordField txtPassword;

    /** Botón que dispara el proceso de autenticación. */
    private JButton btnIngresar;

    /** Etiqueta que muestra mensajes de error al usuario. */
    private JLabel lblMensaje;

    /** DAO utilizado para autenticar las credenciales ingresadas. */
    private final UsuarioDAO usuarioDAO;

    /** Color de acento principal — azul corporativo. */
    private static final Color AZUL     = new Color(37, 99, 235);

    /** Color de fondo general de la ventana. */
    private static final Color FONDO    = new Color(248, 249, 252);

    /** Color del texto principal. */
    private static final Color TEXTO    = new Color(30, 30, 60);

    /** Color de texto secundario y etiquetas de campo. */
    private static final Color SUBTEXTO = new Color(144, 144, 168);

    /** Color de borde para campos de texto. */
    private static final Color BORDE    = new Color(224, 224, 238);

    /** Color para mensajes de error. */
    private static final Color ERROR    = new Color(220, 38, 38);

    /**
     * Construye la ventana de login e inicializa todos sus componentes.
     */
    public VistaLogin() {
        this.usuarioDAO = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
        registrarEventos();
    }

    /**
     * Configura las propiedades básicas de la ventana (título, cierre, resize).
     */
    private void configurarVentana() {
        setTitle("Nómina CR — Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Crea y dispone todos los componentes visuales dentro de la ventana.
     * Incluye logo animado, campos de entrada y botón de acceso.
     */
    private void inicializarComponentes() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(FONDO);
        fondo.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(fondo);

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(36, 40, 32, 40)));
        tarjeta.setPreferredSize(new Dimension(340, 420));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setAlignmentX(CENTER_ALIGNMENT);
        JLabel logoLabel = new JLabel("N") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AZUL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("N")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("N", x, y);
                g2.dispose();
            }
        };
        logoLabel.setPreferredSize(new Dimension(48, 48));
        logoPanel.add(logoLabel);
        tarjeta.add(logoPanel);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 14)));

        // Título y subtítulo
        JLabel lblTitulo = new JLabel("Nómina CR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TEXTO);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        tarjeta.add(lblTitulo);

        JLabel lblSub = new JLabel("Sistema de gestión de planilla", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(SUBTEXTO);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);
        tarjeta.add(lblSub);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 28)));

        // Campos
        tarjeta.add(campoCentrado("USUARIO", txtUsuario = new JTextField()));
        tarjeta.add(Box.createRigidArea(new Dimension(0, 14)));
        tarjeta.add(campoCentrado("CONTRASEÑA", txtPassword = new JPasswordField()));
        tarjeta.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botón ingresar
        btnIngresar = new JButton("Iniciar sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? AZUL.darker() :
                        getModel().isRollover() ? AZUL.brighter() : AZUL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnIngresar.setPreferredSize(new Dimension(260, 42));
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnIngresar.setAlignmentX(CENTER_ALIGNMENT);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tarjeta.add(btnIngresar);
        tarjeta.add(Box.createRigidArea(new Dimension(0, 12)));

        // Mensaje error
        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMensaje.setForeground(ERROR);
        lblMensaje.setAlignmentX(CENTER_ALIGNMENT);
        tarjeta.add(lblMensaje);
        tarjeta.add(Box.createVerticalGlue());

        // Versión
        JLabel lblVersion = new JLabel("Nómina CR · v1.1", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(SUBTEXTO);
        lblVersion.setAlignmentX(CENTER_ALIGNMENT);
        tarjeta.add(lblVersion);

        fondo.add(tarjeta);
        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Registra los listeners de eventos del botón y campo de contraseña,
     * y el foco automático al abrir la ventana.
     */
    private void registrarEventos() {
        btnIngresar.addActionListener(e -> autenticar());
        txtPassword.addActionListener(e -> autenticar());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
            }
        });
    }

    /**
     * Ejecuta el proceso de autenticación con las credenciales ingresadas.
     * <p>
     * Si las credenciales son válidas, cierra esta ventana y abre
     * {@link VistaPrincipal}. En caso de error muestra el mensaje
     * correspondiente.
     * </p>
     */
    private void autenticar() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }

        btnIngresar.setEnabled(false);

        try {
            Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, password);
            dispose();
            new VistaPrincipal(usuarioAutenticado).setVisible(true);
        } catch (AutenticacionFallidaException ex) {
            mostrarError(ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al leer archivo de usuarios.");
        } finally {
            btnIngresar.setEnabled(true);
        }
    }

    /**
     * Muestra un mensaje de error debajo del botón de ingreso.
     *
     * @param mensaje Texto del mensaje a mostrar.
     */
    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    /**
     * Crea un panel centrado que contiene una etiqueta y un campo de texto.
     *
     * @param etiqueta Texto de la etiqueta superior del campo.
     * @param campo    Campo de texto a estilizar y agregar.
     * @return Panel con etiqueta y campo listos para agregar a la tarjeta.
     */
    private JPanel campoCentrado(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(260, 70));

        JLabel lbl = new JLabel(etiqueta, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(SUBTEXTO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setMaximumSize(new Dimension(260, 38));
        campo.setPreferredSize(new Dimension(260, 38));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        campo.setBackground(Color.WHITE);
        campo.setForeground(TEXTO);

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(campo);
        return panel;
    }

    /**
     * Punto de entrada para pruebas independientes de la vista de login.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VistaLogin().setVisible(true));
    }
}