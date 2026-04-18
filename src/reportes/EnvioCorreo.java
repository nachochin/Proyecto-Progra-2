/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes;

import entidades.Empleado;
import entidades.Nomina;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Servicio de envío de correos electrónicos con JavaMail.
 * <p>
 * Permite enviar los reportes de nómina en PDF como adjunto al correo del
 * empleado o al correo del patrono. Utiliza SMTP con autenticación TLS
 * (compatible con Gmail y otros).
 * </p>
 *
 * <h3>Dependencias requeridas en NetBeans:</h3>
 * <pre>
 *   javax.mail.jar  (o jakarta.mail.jar)
 *   Descargar: https://eclipse-ee4j.github.io/mail/
 * </pre>
 *
 * <h3>Configuración para Gmail:</h3>
 * <pre>
 *   host    = smtp.gmail.com
 *   puerto  = 587
 *   usuario = tu_correo@gmail.com
 *   clave   = contraseña de aplicación (no la del correo)
 *   Nota: activar "Contraseñas de aplicación" en la cuenta de Google.
 * </pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class EnvioCorreo {

    // -------------------------------------------------------------------------
    // Configuración SMTP
    // -------------------------------------------------------------------------
    /**
     * Servidor SMTP para el envío de correos.
     */
    private final String host;

    /**
     * Puerto del servidor SMTP (587 para TLS, 465 para SSL).
     */
    private final int puerto;

    /**
     * Correo remitente (cuenta desde la que se envían los correos).
     */
    private final String usuarioSMTP;

    /**
     * Contraseña o clave de aplicación del remitente.
     */
    private final String claveSMTP;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Crea una instancia del servicio de envío de correos con la configuración
     * SMTP especificada.
     *
     * @param host Servidor SMTP (ej. "smtp.gmail.com").
     * @param puerto Puerto del servidor (ej. 587).
     * @param usuarioSMTP Correo remitente (ej. "nomina@empresa.com").
     * @param claveSMTP Contraseña o clave de aplicación.
     */
    public EnvioCorreo(String host, int puerto,
            String usuarioSMTP, String claveSMTP) {
        this.host = host;
        this.puerto = puerto;
        this.usuarioSMTP = usuarioSMTP;
        this.claveSMTP = claveSMTP;
    }

    // -------------------------------------------------------------------------
    // Métodos públicos
    // -------------------------------------------------------------------------
    /**
     * Envía el comprobante de nómina al correo del empleado con el PDF adjunto.
     *
     * @param empleado Empleado destinatario (se usa su correo registrado).
     * @param nomina Nómina del período para personalizar el asunto.
     * @param rutaPDF Ruta local del archivo PDF a adjuntar.
     * @throws MessagingException si ocurre un error en el envío del correo.
     */
    public void enviarComprobanteEmpleado(Empleado empleado, Nomina nomina,
            String rutaPDF) throws MessagingException, IOException {
        String asunto = "Comprobante de nómina — "
                + nombreMes(nomina.getMes()) + " " + nomina.getAnio()
                + " Quincena " + nomina.getQuincena();

        String cuerpo = construirCuerpoEmpleado(empleado, nomina);

        enviar(empleado.getCorreo(), asunto, cuerpo, rutaPDF);
    }

    /**
     * Envía el reporte consolidado de aportes patronales al correo del patrono.
     *
     * @param correoPatrono Dirección de correo del patrono.
     * @param anio Año del período reportado.
     * @param mes Mes del período reportado.
     * @param quincena Quincena del período (1 o 2).
     * @param rutaPDF Ruta local del PDF del reporte patronal.
     * @throws MessagingException si ocurre un error en el envío.
     */
    public void enviarReportePatrono(String correoPatrono, int anio,
            int mes, int quincena,
            String rutaPDF) throws MessagingException, IOException {
        String asunto = "Reporte patronal — " + nombreMes(mes) + " "
                + anio + " Quincena " + quincena;

        String cuerpo = "Estimado patrono,\n\n"
                + "Adjunto encontrará el reporte consolidado de aportes patronales "
                + "correspondiente al período: " + nombreMes(mes) + " " + anio
                + ", quincena " + quincena + ".\n\n"
                + "Por favor revise el documento adjunto para verificar los montos.\n\n"
                + "Sistema de Nómina CR";

        enviar(correoPatrono, asunto, cuerpo, rutaPDF);
    }

    // -------------------------------------------------------------------------
    // Método central de envío
    // -------------------------------------------------------------------------
    /**
     * Método central que construye y envía el correo con adjunto.
     * <p>
     * Configura la sesión SMTP con TLS, construye el mensaje multipart con
     * cuerpo de texto y archivo PDF adjunto, y lo envía.
     * </p>
     *
     * @param destinatario Dirección de correo del destinatario.
     * @param asunto Asunto del correo.
     * @param cuerpo Cuerpo del mensaje en texto plano.
     * @param rutaAdjunto Ruta local del archivo a adjuntar (puede ser
     * {@code null}).
     * @throws MessagingException si ocurre un error en la configuración o
     * envío.
     */
    private void enviar(String destinatario, String asunto,
            String cuerpo, String rutaAdjunto) throws MessagingException, IOException {

        // 1. Configurar propiedades SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "securemail.comredcr.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 2. Crear sesión con autenticación
        Session sesion = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuarioSMTP, claveSMTP);
            }
        });

        // 3. Construir mensaje
        MimeMessage mensaje = new MimeMessage(sesion);
        mensaje.setFrom(new InternetAddress(usuarioSMTP, "Sistema de Nómina CR"));
        mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
        mensaje.setSubject(asunto, "UTF-8");

        // 4. Cuerpo multipart (texto + adjunto)
        Multipart multipart = new MimeMultipart();

        // Parte de texto
        MimeBodyPart parteCuerpo = new MimeBodyPart();
        parteCuerpo.setText(cuerpo, "UTF-8");
        multipart.addBodyPart(parteCuerpo);

        // Adjunto PDF
        if (rutaAdjunto != null && !rutaAdjunto.isEmpty()) {
            File archivoPDF = new File(rutaAdjunto);
            if (archivoPDF.exists()) {
                MimeBodyPart parteAdjunto = new MimeBodyPart();
                parteAdjunto.attachFile(archivoPDF);
                parteAdjunto.setFileName(archivoPDF.getName());
                multipart.addBodyPart(parteAdjunto);
            }
        }

        mensaje.setContent(multipart);

        // 5. Enviar
        Transport.send(mensaje);
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------
    /**
     * Construye el cuerpo del correo personalizado para el empleado.
     *
     * @param empleado Empleado destinatario.
     * @param nomina Nómina del período.
     * @return Texto del cuerpo del correo.
     */
    private String construirCuerpoEmpleado(Empleado empleado, Nomina nomina) {
        return "Estimado(a) " + empleado.getNombre() + ",\n\n"
                + "Adjunto encontrará su comprobante de nómina correspondiente al período:\n"
                + nombreMes(nomina.getMes()) + " " + nomina.getAnio()
                + ", quincena " + nomina.getQuincena() + ".\n\n"
                + "Resumen del período:\n"
                + "  Salario bruto:      " + String.format("%.2f", nomina.getSalarioBruto()) + "\n"
                + "  Total deducciones:  " + String.format("%.2f", nomina.getTotalDeducciones()) + "\n"
                + "  Salario neto:       " + String.format("%.2f", nomina.getSalarioNeto()) + "\n\n"
                + "Para mayor detalle, revise el documento PDF adjunto.\n\n"
                + "Atentamente,\n"
                + "Departamento de Recursos Humanos\n"
                + "Sistema de Nómina CR";
    }

    /**
     * Devuelve el nombre del mes en español.
     *
     * @param mes Número del mes (1-12).
     * @return Nombre del mes en español.
     */
    private String nombreMes(int mes) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo",
            "Junio", "Julio", "Agosto", "Setiembre", "Octubre",
            "Noviembre", "Diciembre"};
        return (mes >= 1 && mes <= 12) ? meses[mes] : "?";
    }
}
