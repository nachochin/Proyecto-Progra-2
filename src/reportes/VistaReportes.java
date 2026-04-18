package reportes;

import datos.EmpleadoDAO;
import datos.NominaDAO;
import entidades.Empleado;
import entidades.Nomina;
import excepciones.ArchivoInvalidoException;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VistaReportes extends JPanel {

    private static final String CARPETA_REPORTES = "reportes/";

    private JComboBox<String> cmbEmpleados;
    private JSpinner spnAnio;
    private JSpinner spnMes;
    private JSpinner spnQuincena;

    private JTextField txtHost;
    private JSpinner spnPuerto;
    private JTextField txtUsuarioSMTP;
    private JPasswordField txtClaveSMTP;
    private JTextField txtCorreoPatrono;

    private JButton btnGenerarEmpleado;
    private JButton btnGenerarPatrono;
    private JButton btnEnviarEmpleado;
    private JButton btnEnviarPatrono;

    private JTextArea txtLog;

    private final EmpleadoDAO empleadoDAO;
    private final NominaDAO nominaDAO;

    public VistaReportes() {
        this.empleadoDAO = new EmpleadoDAO();
        this.nominaDAO = new NominaDAO();

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 248));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        crearCarpetaReportes();
        inicializarComponentes();
        cargarEmpleados();
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Reportes y Correos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(30, 30, 60));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 14, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCentro.setBackground(new Color(245, 245, 248));

        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        panelIzq.add(seccion("Seleccionar periodo"));
        panelIzq.add(etiqueta("Empleado"));
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpleados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cmbEmpleados.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(cmbEmpleados);
        panelIzq.add(espacio(8));

        panelIzq.add(etiqueta("Anio"));
        spnAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2099, 1));
        spnAnio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spnAnio.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(spnAnio);
        panelIzq.add(espacio(8));

        panelIzq.add(etiqueta("Mes (1-12)"));
        spnMes = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnMes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spnMes.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(spnMes);
        panelIzq.add(espacio(8));

        panelIzq.add(etiqueta("Quincena (1 o 2)"));
        spnQuincena = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
        spnQuincena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spnQuincena.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(spnQuincena);
        panelIzq.add(espacio(16));

        panelIzq.add(seccion("Generar PDF"));
        btnGenerarEmpleado = boton("Generar PDF del empleado", new Color(30, 80, 160));
        btnGenerarPatrono = boton("Generar PDF del patrono", new Color(25, 25, 55));
        panelIzq.add(btnGenerarEmpleado);
        panelIzq.add(espacio(8));
        panelIzq.add(btnGenerarPatrono);
        panelIzq.add(Box.createVerticalGlue());

        JPanel panelDer = new JPanel();
        panelDer.setLayout(new BoxLayout(panelDer, BoxLayout.Y_AXIS));
        panelDer.setBackground(Color.WHITE);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        panelDer.add(seccion("Configuracion SMTP"));

        panelDer.add(etiqueta("Servidor SMTP"));
        txtHost = campo("smtp.gmail.com");
        panelDer.add(txtHost);
        panelDer.add(espacio(6));

        panelDer.add(etiqueta("Puerto"));
        spnPuerto = new JSpinner(new SpinnerNumberModel(587, 1, 9999, 1));
        spnPuerto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spnPuerto.setAlignmentX(LEFT_ALIGNMENT);
        panelDer.add(spnPuerto);
        panelDer.add(espacio(6));

        panelDer.add(etiqueta("Correo remitente"));
        txtUsuarioSMTP = campo("correo@gmail.com");
        panelDer.add(txtUsuarioSMTP);
        panelDer.add(espacio(6));

        panelDer.add(etiqueta("Clave de aplicacion"));
        txtClaveSMTP = new JPasswordField();
        txtClaveSMTP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtClaveSMTP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtClaveSMTP.setAlignmentX(LEFT_ALIGNMENT);
        txtClaveSMTP.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 220), 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        panelDer.add(txtClaveSMTP);
        panelDer.add(espacio(6));

        panelDer.add(etiqueta("Correo del patrono"));
        txtCorreoPatrono = campo("patrono@empresa.com");
        panelDer.add(txtCorreoPatrono);
        panelDer.add(espacio(16));

        panelDer.add(seccion("Enviar por correo"));
        btnEnviarEmpleado = boton("Enviar comprobante al empleado", new Color(20, 110, 60));
        btnEnviarPatrono = boton("Enviar reporte al patrono", new Color(100, 50, 140));
        panelDer.add(btnEnviarEmpleado);
        panelDer.add(espacio(8));
        panelDer.add(btnEnviarPatrono);
        panelDer.add(Box.createVerticalGlue());

        panelCentro.add(panelIzq);
        panelCentro.add(panelDer);
        add(panelCentro, BorderLayout.CENTER);

        txtLog = new JTextArea(4, 0);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(new Color(30, 30, 40));
        txtLog.setForeground(new Color(150, 220, 150));
        txtLog.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80)));
        scrollLog.setPreferredSize(new Dimension(0, 100));
        add(scrollLog, BorderLayout.SOUTH);

        registrarEventos();
    }

    private void registrarEventos() {
        btnGenerarEmpleado.addActionListener(e -> generarPDFEmpleado());
        btnGenerarPatrono.addActionListener(e -> generarPDFPatrono());
        btnEnviarEmpleado.addActionListener(e -> enviarCorreoEmpleado());
        btnEnviarPatrono.addActionListener(e -> enviarCorreoPatrono());
    }

    private void generarPDFEmpleado() {
        Nomina nomina = obtenerNomina();
        if (nomina == null) {
            return;
        }

        try {
            Empleado emp = empleadoDAO.buscarPorId(nomina.getIdEmpleado());
            if (emp == null) {
                log("ERROR: Empleado no encontrado.");
                return;
            }

            GeneradorPDFNomina gen = new GeneradorPDFNomina(emp, nomina);
            String ruta = CARPETA_REPORTES + gen.getNombreArchivo();
            gen.generar(ruta);

            log("PDF generado: " + ruta);
            JOptionPane.showMessageDialog(this,
                    "PDF generado correctamente:\n" + ruta,
                    "PDF generado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR al generar PDF: " + ex.getMessage());
            mostrarError(ex.getMessage());
        }
    }

    private void generarPDFPatrono() {
        int anio = (int) spnAnio.getValue();
        int mes = (int) spnMes.getValue();
        int quincena = (int) spnQuincena.getValue();

        try {
            Map<Empleado, Nomina> datos = obtenerNominasPeriodo(anio, mes, quincena);
            if (datos.isEmpty()) {
                mostrarError("No hay nominas procesadas para el periodo seleccionado.");
                return;
            }

            GeneradorPDFPatrono gen = new GeneradorPDFPatrono(datos, anio, mes, quincena);
            String ruta = CARPETA_REPORTES + gen.getNombreArchivo();
            gen.generar(ruta);

            log("PDF patronal generado: " + ruta + " (" + datos.size() + " empleados)");
            JOptionPane.showMessageDialog(this,
                    "PDF del patrono generado:\n" + ruta,
                    "PDF generado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR al generar PDF patronal: " + ex.getMessage());
            mostrarError(ex.getMessage());
        }
    }

    private void enviarCorreoEmpleado() {
        Nomina nomina = obtenerNomina();
        if (nomina == null) {
            return;
        }

        try {
            Empleado emp = empleadoDAO.buscarPorId(nomina.getIdEmpleado());
            if (emp == null) {
                log("ERROR: Empleado no encontrado.");
                return;
            }

            GeneradorPDFNomina gen = new GeneradorPDFNomina(emp, nomina);
            String ruta = CARPETA_REPORTES + gen.getNombreArchivo();
            if (!new File(ruta).exists()) {
                gen.generar(ruta);
            }

            EnvioCorreo servicio = construirServicioCorreo();
            log("Enviando correo a " + emp.getCorreo() + "...");
            servicio.enviarComprobanteEmpleado(emp, nomina, ruta);
            log("Correo enviado correctamente a: " + emp.getCorreo());

            JOptionPane.showMessageDialog(this,
                    "Correo enviado a: " + emp.getCorreo(),
                    "Correo enviado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR PDF: " + ex.getMessage());
            mostrarError("Error al generar PDF: " + ex.getMessage());
        } catch (IOException ex) {
            log("ERROR adjunto/correo: " + ex.getMessage());
            mostrarError("Error al preparar el archivo adjunto: " + ex.getMessage());
        } catch (MessagingException ex) {
            log("ERROR correo: " + ex.getMessage());
            mostrarError("Error al enviar correo: " + ex.getMessage()
                    + "\n\nVerifique la configuracion SMTP y la clave de aplicacion.");
        }
    }

    private void enviarCorreoPatrono() {
        String correoPatrono = txtCorreoPatrono.getText().trim();
        if (correoPatrono.isEmpty()) {
            mostrarError("Ingrese el correo del patrono.");
            return;
        }

        int anio = (int) spnAnio.getValue();
        int mes = (int) spnMes.getValue();
        int quincena = (int) spnQuincena.getValue();

        try {
            Map<Empleado, Nomina> datos = obtenerNominasPeriodo(anio, mes, quincena);
            if (datos.isEmpty()) {
                mostrarError("No hay nominas para el periodo seleccionado.");
                return;
            }

            GeneradorPDFPatrono gen = new GeneradorPDFPatrono(datos, anio, mes, quincena);
            String ruta = CARPETA_REPORTES + gen.getNombreArchivo();
            if (!new File(ruta).exists()) {
                gen.generar(ruta);
            }

            EnvioCorreo servicio = construirServicioCorreo();
            log("Enviando reporte patronal a " + correoPatrono + "...");
            servicio.enviarReportePatrono(correoPatrono, anio, mes, quincena, ruta);
            log("Reporte patronal enviado a: " + correoPatrono);

            JOptionPane.showMessageDialog(this,
                    "Reporte enviado al patrono: " + correoPatrono,
                    "Correo enviado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR PDF: " + ex.getMessage());
            mostrarError("Error al generar PDF: " + ex.getMessage());
        } catch (IOException ex) {
            log("ERROR adjunto/correo: " + ex.getMessage());
            mostrarError("Error al preparar el archivo adjunto: " + ex.getMessage());
        } catch (MessagingException ex) {
            log("ERROR correo: " + ex.getMessage());
            mostrarError("Error al enviar correo: " + ex.getMessage());
        }
    }

    private Map<Empleado, Nomina> obtenerNominasPeriodo(int anio, int mes, int quincena)
            throws ArchivoInvalidoException {
        List<Nomina> todasNominas = nominaDAO.listarTodos();
        Map<Empleado, Nomina> datos = new LinkedHashMap<>();

        for (Nomina n : todasNominas) {
            if (n.getAnio() == anio && n.getMes() == mes && n.getQuincena() == quincena) {
                Empleado emp = empleadoDAO.buscarPorId(n.getIdEmpleado());
                if (emp != null) {
                    datos.put(emp, n);
                }
            }
        }

        return datos;
    }

    private Nomina obtenerNomina() {
        if (cmbEmpleados.getSelectedItem() == null) {
            mostrarError("Seleccione un empleado.");
            return null;
        }

        String seleccionado = cmbEmpleados.getSelectedItem().toString();
        String idEmp = seleccionado.split(" - ")[0];
        int anio = (int) spnAnio.getValue();
        int mes = (int) spnMes.getValue();
        int quincena = (int) spnQuincena.getValue();
        String clave = idEmp + "-" + anio + "-" + mes + "-" + quincena;

        try {
            Nomina nomina = nominaDAO.buscarPorId(clave);
            if (nomina == null) {
                mostrarError("No existe nomina para el periodo seleccionado.\nPrimero procese la nomina en el modulo Nomina.");
            }
            return nomina;
        } catch (ArchivoInvalidoException ex) {
            log("ERROR: " + ex.getMessage());
            mostrarError(ex.getMessage());
            return null;
        }
    }

    private EnvioCorreo construirServicioCorreo() {
        return new EnvioCorreo(
                txtHost.getText().trim(),
                (int) spnPuerto.getValue(),
                txtUsuarioSMTP.getText().trim(),
                new String(txtClaveSMTP.getPassword())
        );
    }

    private void cargarEmpleados() {
        cmbEmpleados.removeAllItems();
        try {
            for (Empleado emp : empleadoDAO.listarTodos()) {
                cmbEmpleados.addItem(emp.getId() + " - " + emp.getNombre());
            }
        } catch (ArchivoInvalidoException ex) {
            log("ERROR al cargar empleados: " + ex.getMessage());
        }
    }

    private void crearCarpetaReportes() {
        new File(CARPETA_REPORTES).mkdirs();
    }

    private void log(String mensaje) {
        String ts = LocalTime.now().toString().substring(0, 8);
        txtLog.append("[" + ts + "] " + mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private JLabel seccion(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(30, 30, 60));
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 4, 0));
        return label;
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(90, 90, 110));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JTextField campo(String valorInicial) {
        JTextField txt = new JTextField(valorInicial);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txt.setAlignmentX(LEFT_ALIGNMENT);
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
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return btn;
    }

    private Component espacio(int alto) {
        return Box.createRigidArea(new Dimension(0, alto));
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
