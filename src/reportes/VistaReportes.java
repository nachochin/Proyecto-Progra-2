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
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Vista para la generación de reportes PDF y envío de correos electrónicos.
 * <p>
 * Permite seleccionar un empleado y período, generar comprobantes de nómina en
 * PDF para el empleado o el patrono, y enviarlos por correo electrónico
 * mediante SMTP con SSL.
 * </p>
 *
 * @version 1.1
 */
public class VistaReportes extends JPanel {

    /**
     * Carpeta donde se almacenan los reportes PDF generados.
     */
    private static final String CARPETA_REPORTES = "reportes/";

    // ── Componentes ──────────────────────────────────────────────────────────
    /**
     * Selector de empleado para el reporte.
     */
    private JComboBox<String> cmbEmpleados;

    /**
     * Spinners de período.
     */
    private JSpinner spnAnio, spnMes, spnQuincena;

    /**
     * Campos de configuración SMTP.
     */
    private JTextField txtHost;
    private JSpinner spnPuerto;
    private JTextField txtUsuarioSMTP;
    private JPasswordField txtClaveSMTP;
    private JTextField txtCorreoPatrono;

    /**
     * Botones de acción.
     */
    private JButton btnGenerarEmpleado, btnGenerarPatrono;
    private JButton btnEnviarEmpleado, btnEnviarPatrono;

    /**
     * Área de log de actividad.
     */
    private JTextArea txtLog;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    /**
     * DAO de empleados.
     */
    private final EmpleadoDAO empleadoDAO;

    /**
     * DAO de nóminas.
     */
    private final NominaDAO nominaDAO;

    // ── Paleta ───────────────────────────────────────────────────────────────
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
     * Color para botón de acción secundaria.
     */
    private static final Color VERDE = new Color(22, 163, 74);

    /**
     * Color para botón de envío patronal.
     */
    private static final Color PURPURA = new Color(124, 58, 237);

    // ── Constructor ──────────────────────────────────────────────────────────
    /**
     * Construye la vista de reportes e inicializa los componentes.
     */
    public VistaReportes() {
        this.empleadoDAO = new EmpleadoDAO();
        this.nominaDAO = new NominaDAO();
        setLayout(new BorderLayout(0, 0));
        setBackground(FONDO);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        crearCarpetaReportes();
        inicializarComponentes();
        cargarEmpleados();
    }

    // ── Inicialización ───────────────────────────────────────────────────────
    /**
     * Crea y organiza todos los componentes de la vista.
     */
    private void inicializarComponentes() {

        // Encabezado
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel lblTitulo = new JLabel("Reportes y Correos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(TEXTO);
        JLabel lblSub = new JLabel("Generá PDFs de nómina y enviálos por correo electrónico");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(GRIS);
        header.add(lblTitulo);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCentro.setBackground(FONDO);

        // ── Panel izquierdo: período y generar PDF ───────────────────────────
        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        panelIzq.add(seccion("Seleccionar período"));

        panelIzq.add(etiqueta("EMPLEADO"));
        panelIzq.add(Box.createVerticalStrut(4));
        cmbEmpleados = new JComboBox<>();
        cmbEmpleados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpleados.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbEmpleados.setAlignmentX(LEFT_ALIGNMENT);
        cmbEmpleados.setBackground(Color.WHITE);
        panelIzq.add(cmbEmpleados);
        panelIzq.add(Box.createVerticalStrut(10));

        panelIzq.add(etiqueta("AÑO"));
        panelIzq.add(Box.createVerticalStrut(4));
        spnAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2099, 1));
        spnAnio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnAnio.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(spnAnio);
        panelIzq.add(Box.createVerticalStrut(8));

        panelIzq.add(etiqueta("MES (1-12)"));
        panelIzq.add(Box.createVerticalStrut(4));
        spnMes = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        spnMes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnMes.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(spnMes);
        panelIzq.add(Box.createVerticalStrut(8));

        panelIzq.add(etiqueta("QUINCENA (1 O 2)"));
        panelIzq.add(Box.createVerticalStrut(4));
        spnQuincena = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
        spnQuincena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnQuincena.setAlignmentX(LEFT_ALIGNMENT);
        panelIzq.add(spnQuincena);
        panelIzq.add(Box.createVerticalStrut(20));

        panelIzq.add(seccion("Generar PDF"));
        btnGenerarEmpleado = crearBoton("Generar PDF del empleado", AZUL);
        btnGenerarPatrono = crearBoton("Generar PDF del patrono", TEXTO);
        panelIzq.add(btnGenerarEmpleado);
        panelIzq.add(Box.createVerticalStrut(8));
        panelIzq.add(btnGenerarPatrono);
        panelIzq.add(Box.createVerticalGlue());

        // ── Panel derecho: SMTP y enviar ─────────────────────────────────────
        JPanel panelDer = new JPanel();
        panelDer.setLayout(new BoxLayout(panelDer, BoxLayout.Y_AXIS));
        panelDer.setBackground(Color.WHITE);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(20, 20, 20, 20)));

        panelDer.add(seccion("Configuración SMTP"));

        panelDer.add(etiqueta("SERVIDOR SMTP"));
        panelDer.add(Box.createVerticalStrut(4));
        txtHost = campo("securemail.comredcr.com");
        panelDer.add(txtHost);
        panelDer.add(Box.createVerticalStrut(8));

        panelDer.add(etiqueta("PUERTO"));
        panelDer.add(Box.createVerticalStrut(4));
        spnPuerto = new JSpinner(new SpinnerNumberModel(465, 1, 9999, 1));
        spnPuerto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        spnPuerto.setAlignmentX(LEFT_ALIGNMENT);
        panelDer.add(spnPuerto);
        panelDer.add(Box.createVerticalStrut(8));

        panelDer.add(etiqueta("CORREO REMITENTE"));
        panelDer.add(Box.createVerticalStrut(4));
        txtUsuarioSMTP = campo("curso_progra2@comredcr.com");
        panelDer.add(txtUsuarioSMTP);
        panelDer.add(Box.createVerticalStrut(8));

        panelDer.add(etiqueta("CLAVE DE APLICACIÓN"));
        panelDer.add(Box.createVerticalStrut(4));
        txtClaveSMTP = new JPasswordField();
        txtClaveSMTP.setText("u6X1h1p9@");
        txtClaveSMTP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtClaveSMTP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtClaveSMTP.setAlignmentX(LEFT_ALIGNMENT);
        txtClaveSMTP.setBackground(Color.WHITE);
        txtClaveSMTP.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        panelDer.add(txtClaveSMTP);
        panelDer.add(Box.createVerticalStrut(8));

        panelDer.add(etiqueta("CORREO DEL PATRONO"));
        panelDer.add(Box.createVerticalStrut(4));
        txtCorreoPatrono = campo("patrono@empresa.com");
        panelDer.add(txtCorreoPatrono);
        panelDer.add(Box.createVerticalStrut(20));

        panelDer.add(seccion("Enviar por correo"));
        btnEnviarEmpleado = crearBoton("Enviar comprobante al empleado", VERDE);
        btnEnviarPatrono = crearBoton("Enviar reporte al patrono", PURPURA);
        panelDer.add(btnEnviarEmpleado);
        panelDer.add(Box.createVerticalStrut(8));
        panelDer.add(btnEnviarPatrono);
        panelDer.add(Box.createVerticalGlue());

        panelCentro.add(panelIzq);
        panelCentro.add(panelDer);
        add(panelCentro, BorderLayout.CENTER);

        // Log
        txtLog = new JTextArea(4, 0);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLog.setBackground(new Color(15, 23, 42));
        txtLog.setForeground(new Color(134, 239, 172));
        txtLog.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(30, 41, 59)));
        scrollLog.setPreferredSize(new Dimension(0, 100));
        add(scrollLog, BorderLayout.SOUTH);

        registrarEventos();
    }

    // ── Eventos ──────────────────────────────────────────────────────────────
    /**
     * Registra los listeners de los botones de la vista.
     */
    private void registrarEventos() {
        btnGenerarEmpleado.addActionListener(e -> generarPDFEmpleado());
        btnGenerarPatrono.addActionListener(e -> generarPDFPatrono());
        btnEnviarEmpleado.addActionListener(e -> enviarCorreoEmpleado());
        btnEnviarPatrono.addActionListener(e -> enviarCorreoPatrono());
    }

    // ── Lógica ───────────────────────────────────────────────────────────────
    /**
     * Genera el PDF de nómina para el empleado seleccionado y lo abre.
     */
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
            try {
                Desktop.getDesktop().open(new File(ruta));
            } catch (IOException ex) {
                log("No se pudo abrir el PDF: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(this, "PDF generado correctamente:\n" + ruta,
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR al generar PDF: " + ex.getMessage());
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Genera el PDF de aportes patronales para el período seleccionado.
     */
    private void generarPDFPatrono() {
        int anio = (int) spnAnio.getValue();
        int mes = (int) spnMes.getValue();
        int quincena = (int) spnQuincena.getValue();
        try {
            Map<Empleado, Nomina> datos = obtenerNominasPeriodo(anio, mes, quincena);
            if (datos.isEmpty()) {
                mostrarError("No hay nóminas procesadas para el período seleccionado.");
                return;
            }
            GeneradorPDFPatrono gen = new GeneradorPDFPatrono(datos, anio, mes, quincena);
            String ruta = CARPETA_REPORTES + gen.getNombreArchivo();
            gen.generar(ruta);
            log("PDF patronal generado: " + ruta + " (" + datos.size() + " empleados)");
            try {
                Desktop.getDesktop().open(new File(ruta));
            } catch (IOException ex) {
                log("No se pudo abrir el PDF: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(this, "PDF del patrono generado:\n" + ruta,
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR al generar PDF patronal: " + ex.getMessage());
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Envía el comprobante de nómina al correo del empleado seleccionado.
     */
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
            JOptionPane.showMessageDialog(this, "Correo enviado a: " + emp.getCorreo(),
                    "Correo enviado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR PDF: " + ex.getMessage());
            mostrarError("Error al generar PDF: " + ex.getMessage());
        } catch (IOException ex) {
            log("ERROR adjunto: " + ex.getMessage());
            mostrarError("Error al preparar el archivo adjunto: " + ex.getMessage());
        } catch (MessagingException ex) {
            log("ERROR correo: " + ex.getMessage());
            mostrarError("Error al enviar correo: " + ex.getMessage()
                    + "\n\nVerificá la configuración SMTP y la clave de aplicación.");
        }
    }

    /**
     * Envía el reporte patronal al correo del patrono indicado.
     */
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
                mostrarError("No hay nóminas para el período seleccionado.");
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
            JOptionPane.showMessageDialog(this, "Reporte enviado al patrono: " + correoPatrono,
                    "Correo enviado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ArchivoInvalidoException ex) {
            log("ERROR PDF: " + ex.getMessage());
            mostrarError("Error al generar PDF: " + ex.getMessage());
        } catch (IOException ex) {
            log("ERROR adjunto: " + ex.getMessage());
            mostrarError("Error al preparar el archivo adjunto: " + ex.getMessage());
        } catch (MessagingException ex) {
            log("ERROR correo: " + ex.getMessage());
            mostrarError("Error al enviar correo: " + ex.getMessage());
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────
    /**
     * Obtiene el mapa de empleados y nóminas para un período específico.
     *
     * @param anio Año del período.
     * @param mes Mes del período.
     * @param quincena Quincena (1 o 2).
     * @return Mapa de empleado a nómina del período indicado.
     * @throws ArchivoInvalidoException si ocurre un error al leer los archivos.
     */
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

    /**
     * Obtiene la nómina correspondiente al empleado y período seleccionados.
     *
     * @return Nómina encontrada, o {@code null} si no existe.
     */
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
                mostrarError("No existe nómina para el período seleccionado.\nPrimero procesá la nómina en el módulo Nómina.");
            }
            return nomina;
        } catch (ArchivoInvalidoException ex) {
            log("ERROR: " + ex.getMessage());
            mostrarError(ex.getMessage());
            return null;
        }
    }

    /**
     * Construye el servicio de correo electrónico.
     * <p>
     * Utiliza el constructor sin parámetros de {@link EnvioCorreo} ya que las
     * credenciales SMTP están configuradas internamente en dicha clase con los
     * datos del servidor del curso.
     * </p>
     *
     * @return Instancia de {@link EnvioCorreo} lista para enviar correos.
     */
    private EnvioCorreo construirServicioCorreo() {
        return new EnvioCorreo();
    }

    /**
     * Carga el listado de empleados en el combo selector.
     */
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

    /**
     * Crea la carpeta de reportes si no existe.
     */
    private void crearCarpetaReportes() {
        new File(CARPETA_REPORTES).mkdirs();
    }

    /**
     * Agrega una línea al área de log con marca de tiempo.
     *
     * @param mensaje Texto a registrar en el log.
     */
    private void log(String mensaje) {
        String ts = LocalTime.now().toString().substring(0, 8);
        txtLog.append("[" + ts + "] " + mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    /**
     * Crea una etiqueta de sección con título en negrita.
     *
     * @param texto Texto de la sección.
     * @return Etiqueta estilizada.
     */
    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXTO);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        return lbl;
    }

    /**
     * Crea una etiqueta pequeña en mayúsculas para campos del formulario.
     *
     * @param texto Texto de la etiqueta.
     * @return Etiqueta estilizada.
     */
    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(GRIS);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un campo de texto con valor inicial y estilo estándar.
     *
     * @param valorInicial Texto inicial del campo.
     * @return Campo de texto estilizado.
     */
    private JTextField campo(String valorInicial) {
        JTextField txt = new JTextField(valorInicial);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setForeground(TEXTO);
        txt.setBackground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
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
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
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
