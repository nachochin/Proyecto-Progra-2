/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import entidades.Empleado;
import entidades.Nomina;
import excepciones.ArchivoInvalidoException;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Genera el reporte PDF de nómina para un empleado usando la biblioteca iText.
 * <p>
 * El reporte incluye:
 * <ul>
 * <li>Encabezado con nombre de la empresa y datos del empleado.</li>
 * <li>Tabla de desglose: salario bruto, deducciones individuales y salario
 * neto.</li>
 * <li>Sección de aportes patronales (informativo).</li>
 * <li>Pie de página con fecha de generación.</li>
 * </ul>
 * </p>
 *
 * <h3>Dependencia requerida en NetBeans (build.xml / libs):</h3>
 * <pre>
 *   itextpdf-5.5.13.jar
 *   Descargar: https://github.com/itext/itextpdf/releases
 * </pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class GeneradorPDFNomina implements GeneradorReporte {
    // -------------------------------------------------------------------------
    // Fuentes y colores
    // -------------------------------------------------------------------------

    /**
     * Color azul oscuro para encabezados.
     */
    private static final BaseColor COLOR_HEADER = new BaseColor(25, 25, 55);

    /**
     * Color gris claro para filas alternas de la tabla.
     */
    private static final BaseColor COLOR_FILA_ALT = new BaseColor(245, 245, 250);

    /**
     * Color verde oscuro para resaltar el salario neto.
     */
    private static final BaseColor COLOR_NETO = new BaseColor(20, 100, 55);

    /**
     * Color rojo suave para totales de deducciones.
     */
    private static final BaseColor COLOR_DEDUCCION = new BaseColor(160, 40, 40);

    // Fuentes iText
    private static final Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE);
    private static final Font FUENTE_SUBTIT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE);
    private static final Font FUENTE_NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font FUENTE_NEGRITA = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FUENTE_NETO = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(20, 100, 55));
    private static final Font FUENTE_PEQUENA = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);

    // -------------------------------------------------------------------------
    // Datos del reporte
    // -------------------------------------------------------------------------
    /**
     * Empleado al que pertenece la nómina.
     */
    private final Empleado empleado;

    /**
     * Registro de nómina calculado a reportar.
     */
    private final Nomina nomina;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Construye el generador de PDF para una nómina específica.
     *
     * @param empleado Empleado al que pertenece la nómina.
     * @param nomina Nómina calculada con todos los montos.
     */
    public GeneradorPDFNomina(Empleado empleado, Nomina nomina) {
        this.empleado = empleado;
        this.nomina = nomina;
    }

    // -------------------------------------------------------------------------
    // generar
    // -------------------------------------------------------------------------
    /**
     * Genera el PDF de nómina del empleado y lo guarda en la ruta indicada.
     *
     * @param rutaDestino Ruta completa del archivo PDF a crear.
     * @throws ArchivoInvalidoException si ocurre un error al crear el
     * documento.
     */
    @Override
    public void generar(String rutaDestino) throws ArchivoInvalidoException {
        Document documento = new Document(PageSize.A4, 50, 50, 60, 50);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));
            documento.open();

            agregarEncabezado(documento);
            agregarDatosEmpleado(documento);
            agregarTablaDesglose(documento);
            agregarAportesPatronales(documento);
            agregarPiePagina(documento);

        } catch (DocumentException | IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al generar PDF de nómina: " + e.getMessage(), e);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Secciones del documento
    // -------------------------------------------------------------------------
    /**
     * Agrega el encabezado con el nombre de la empresa y el título del reporte.
     *
     * @param doc Documento iText al que se agrega el encabezado.
     * @throws DocumentException si ocurre un error al agregar elementos al
     * documento.
     */
    private void agregarEncabezado(Document doc) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(COLOR_HEADER);
        celda.setPadding(16);
        celda.setBorder(Rectangle.NO_BORDER);

        Paragraph titulo = new Paragraph("COMPROBANTE DE NÓMINA", FUENTE_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        celda.addElement(titulo);

        String periodo = "Período: " + nombreMes(nomina.getMes()) + " "
                + nomina.getAnio() + " — Quincena " + nomina.getQuincena();
        Paragraph subtitulo = new Paragraph(periodo, FUENTE_SUBTIT);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        celda.addElement(subtitulo);

        header.addCell(celda);
        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la sección con los datos personales y laborales del empleado.
     *
     * @param doc Documento al que se agrega la sección.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarDatosEmpleado(Document doc) throws DocumentException {
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(4);
        tabla.setSpacingAfter(12);

        agregarFilaDato(tabla, "ID empleado:", empleado.getId());
        agregarFilaDato(tabla, "Nombre:", empleado.getNombre());
        agregarFilaDato(tabla, "Correo:", empleado.getCorreo());
        agregarFilaDato(tabla, "Tipo contrato:", empleado.getTipoContrato());
        agregarFilaDato(tabla, "Salario base:", formatoMoneda(empleado.getSalarioBase()));
        agregarFilaDato(tabla, "Fecha ingreso:", empleado.getFechaIngreso().toString());

        doc.add(tabla);
    }

    /**
     * Agrega la tabla principal con el desglose completo de la nómina.
     *
     * @param doc Documento al que se agrega la tabla.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarTablaDesglose(Document doc) throws DocumentException {
        // Título de sección
        Paragraph titulo = new Paragraph("Desglose de nómina", FUENTE_NEGRITA);
        titulo.setSpacingBefore(6);
        titulo.setSpacingAfter(6);
        doc.add(titulo);

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 1.5f});

        // Encabezado de tabla
        agregarCeldaHeader(tabla, "Concepto");
        agregarCeldaHeader(tabla, "Monto (₡)");

        // Ingresos
        agregarFilaTabla(tabla, "Salario bruto quincenal",
                formatoMoneda(nomina.getSalarioBruto()), false, false);
        agregarFilaTabla(tabla, "Horas extra",
                formatoMoneda(nomina.getHorasExtra()), true, false);

        // Línea separadora visual
        agregarFilaSeparador(tabla, "— Deducciones del trabajador —");

        agregarFilaTabla(tabla, "CCSS trabajador",
                "- " + formatoMoneda(nomina.getDeduccionCCSS()), false, false);
        agregarFilaTabla(tabla, "ROP trabajador",
                "- " + formatoMoneda(nomina.getDeduccionPension()), true, false);
        agregarFilaTabla(tabla, "Banco Popular trabajador",
                "- " + formatoMoneda(nomina.getDeduccionBancoPopular()), false, false);
        agregarFilaTabla(tabla, "Impuesto de renta",
                "- " + formatoMoneda(nomina.getImpuestoRenta()), true, false);
        agregarFilaTabla(tabla, "Otras deducciones",
                "- " + formatoMoneda(nomina.getOtrasDeducciones()), false, false);

        // Total deducciones
        agregarFilaTotalDeducciones(tabla,
                "Total deducciones", formatoMoneda(nomina.getTotalDeducciones()));

        // Salario neto
        agregarFilaNeto(tabla,
                "SALARIO NETO A RECIBIR", formatoMoneda(nomina.getSalarioNeto()));

        tabla.setSpacingAfter(12);
        doc.add(tabla);
    }

    /**
     * Agrega la sección informativa de aportes patronales.
     *
     * @param doc Documento al que se agrega la sección.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarAportesPatronales(Document doc) throws DocumentException {
        Paragraph titulo = new Paragraph(
                "Aportes patronales (informativos, no se rebajan al empleado)",
                FUENTE_NEGRITA);
        titulo.setSpacingBefore(4);
        titulo.setSpacingAfter(6);
        doc.add(titulo);

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3f, 1.5f});

        agregarCeldaHeader(tabla, "Concepto patronal");
        agregarCeldaHeader(tabla, "Monto (₡)");

        agregarFilaTabla(tabla, "Total aportes patronales",
                formatoMoneda(nomina.getAportesPatronales()), false, false);

        tabla.setSpacingAfter(16);
        doc.add(tabla);
    }

    /**
     * Agrega el pie de página con la fecha de generación del reporte.
     *
     * @param doc Documento al que se agrega el pie.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarPiePagina(Document doc) throws DocumentException {
        Paragraph pie = new Paragraph(
                "Documento generado el " + java.time.LocalDate.now().toString()
                + "  |  Sistema de Nómina CR  |  Documento informativo",
                FUENTE_PEQUENA);
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(new LineSeparator());
        doc.add(pie);
    }

    // -------------------------------------------------------------------------
    // Helpers de construcción de celdas
    // -------------------------------------------------------------------------
    /**
     * Agrega una fila de dos celdas con etiqueta y valor a una tabla de datos.
     *
     * @param tabla Tabla donde se agrega la fila.
     * @param label Texto de la etiqueta (columna izquierda).
     * @param valor Texto del valor (columna derecha).
     */
    private void agregarFilaDato(PdfPTable tabla, String label, String valor) {
        PdfPCell celdaLabel = new PdfPCell(new Phrase(label, FUENTE_NEGRITA));
        celdaLabel.setBorder(Rectangle.BOTTOM);
        celdaLabel.setPadding(5);
        celdaLabel.setBorderColor(new BaseColor(220, 220, 230));

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, FUENTE_NORMAL));
        celdaValor.setBorder(Rectangle.BOTTOM);
        celdaValor.setPadding(5);
        celdaValor.setBorderColor(new BaseColor(220, 220, 230));

        tabla.addCell(celdaLabel);
        tabla.addCell(celdaValor);
    }

    /**
     * Agrega una celda de encabezado con fondo oscuro a la tabla de desglose.
     *
     * @param tabla Tabla donde se agrega la celda.
     * @param texto Texto del encabezado.
     */
    private void agregarCeldaHeader(PdfPTable tabla, String texto) {
        Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteHeader));
        celda.setBackgroundColor(COLOR_HEADER);
        celda.setPadding(7);
        celda.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celda);
    }

    /**
     * Agrega una fila estándar a la tabla de desglose con fondo alterno
     * opcional.
     *
     * @param tabla Tabla donde se agrega la fila.
     * @param concepto Descripción del concepto.
     * @param monto Monto formateado como texto.
     * @param alterno Si es {@code true} aplica el color de fila alterna.
     * @param negrita Si es {@code true} usa fuente negrita.
     */
    private void agregarFilaTabla(PdfPTable tabla, String concepto,
            String monto, boolean alterno, boolean negrita) {
        Font fuente = negrita ? FUENTE_NEGRITA : FUENTE_NORMAL;
        BaseColor fondo = alterno ? COLOR_FILA_ALT : BaseColor.WHITE;

        PdfPCell cConcepto = new PdfPCell(new Phrase(concepto, fuente));
        cConcepto.setBackgroundColor(fondo);
        cConcepto.setPadding(6);
        cConcepto.setBorderColor(new BaseColor(230, 230, 235));

        PdfPCell cMonto = new PdfPCell(new Phrase(monto, fuente));
        cMonto.setBackgroundColor(fondo);
        cMonto.setPadding(6);
        cMonto.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cMonto.setBorderColor(new BaseColor(230, 230, 235));

        tabla.addCell(cConcepto);
        tabla.addCell(cMonto);
    }

    /**
     * Agrega una fila separadora con texto centrado y fondo gris medio.
     *
     * @param tabla Tabla donde se agrega el separador.
     * @param titulo Texto del separador.
     */
    private void agregarFilaSeparador(PdfPTable tabla, String titulo) {
        Font fuenteSep = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC,
                new BaseColor(80, 80, 100));
        PdfPCell celda = new PdfPCell(new Phrase(titulo, fuenteSep));
        celda.setColspan(2);
        celda.setBackgroundColor(new BaseColor(230, 230, 240));
        celda.setPadding(5);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(celda);
    }

    /**
     * Agrega la fila de total de deducciones con color rojo.
     *
     * @param tabla Tabla donde se agrega la fila.
     * @param concepto Descripción del total.
     * @param monto Monto total de deducciones.
     */
    private void agregarFilaTotalDeducciones(PdfPTable tabla,
            String concepto, String monto) {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, COLOR_DEDUCCION);

        PdfPCell c1 = new PdfPCell(new Phrase(concepto, fuente));
        c1.setPadding(7);
        c1.setBackgroundColor(new BaseColor(255, 240, 240));

        PdfPCell c2 = new PdfPCell(new Phrase(monto, fuente));
        c2.setPadding(7);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setBackgroundColor(new BaseColor(255, 240, 240));

        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    /**
     * Agrega la fila final del salario neto con color verde y tamaño mayor.
     *
     * @param tabla Tabla donde se agrega la fila.
     * @param concepto Texto del concepto (salario neto).
     * @param monto Monto neto a pagar.
     */
    private void agregarFilaNeto(PdfPTable tabla, String concepto, String monto) {
        PdfPCell c1 = new PdfPCell(new Phrase(concepto, FUENTE_NETO));
        c1.setPadding(10);
        c1.setBackgroundColor(new BaseColor(230, 248, 236));

        PdfPCell c2 = new PdfPCell(new Phrase(monto, FUENTE_NETO));
        c2.setPadding(10);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setBackgroundColor(new BaseColor(230, 248, 236));

        tabla.addCell(c1);
        tabla.addCell(c2);
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------
    /**
     * Formatea un valor numérico como moneda costarricense.
     *
     * @param monto Valor a formatear.
     * @return Cadena con formato "₡1,234,567.89".
     */
    private String formatoMoneda(double monto) {
        return String.format("%.2f", monto);
    }

    /**
     * Devuelve el nombre del mes en español dado su número.
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

    // -------------------------------------------------------------------------
    // getNombreArchivo
    // -------------------------------------------------------------------------
    /**
     * Devuelve el nombre de archivo sugerido para este reporte.
     *
     * @return Nombre con formato "nomina_idEmp_anio_mes_Q#.pdf".
     */
    @Override
    public String getNombreArchivo() {
        return "nomina_" + empleado.getId() + "_"
                + nomina.getAnio() + "_"
                + String.format("%02d", nomina.getMes()) + "_Q"
                + nomina.getQuincena() + ".pdf";
    }

}
