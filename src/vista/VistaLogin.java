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

public class VistaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JLabel lblMensaje;
    private final UsuarioDAO usuarioDAO;

    public VistaLogin() {
        this.usuarioDAO = new UsuarioDAO();
        configurarVentana();
        inicializarComponentes();
        registrarEventos();
    }

    private void configurarVentana() {
        setTitle("Sistema de Nomina - Iniciar sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(false);
        setFocusableWindowState(true);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitulo = new JLabel("Sistema de Nomina");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblUsuario = new JLabel("Usuario:");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(lblUsuario, gbc);

        txtUsuario = new JTextField(20);
        txtUsuario.setEnabled(true);
        txtUsuario.setEditable(true);
        txtUsuario.setFocusable(true);
        txtUsuario.setRequestFocusEnabled(true);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtUsuario, gbc);

        JLabel lblPassword = new JLabel("Contrasena:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setEnabled(true);
        txtPassword.setEditable(true);
        txtPassword.setFocusable(true);
        txtPassword.setRequestFocusEnabled(true);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtPassword, gbc);

        btnIngresar = new JButton("Ingresar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);
        gbc.gridy = 4;
        panel.add(lblMensaje, gbc);

        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setLocationRelativeTo(null);
    }

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

    private void autenticar() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        try {
            Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, password);
            dispose();
            new VistaPrincipal(usuarioAutenticado).setVisible(true);
        } catch (AutenticacionFallidaException ex) {
            mostrarError(ex.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        } catch (ArchivoInvalidoException ex) {
            mostrarError("Error al leer archivo de usuarios: " + ex.getMessage());
        } finally {
            btnIngresar.setEnabled(true);
            btnIngresar.setText("Ingresar");
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaLogin login = new VistaLogin();
            login.setVisible(true);
        });
    }
}