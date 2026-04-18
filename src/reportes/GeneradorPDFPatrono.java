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
import java.util.Map;

/**
 * Genera el reporte PDF consolidado de aportes patronales para el patrono.
 * <p>
 * El reporte incluye una tabla con todos los empleados procesados en un
 * período, sus aportes patronales individuales y el total a pagar. Este reporte
 * es exclusivo para el patrono y no se comparte con empleados.
 * </p>
 *
 * <h3>Dependencia requerida:</h3>
 * <pre>
 *   itextpdf-5.5.13.jar
 * </pre>
 *
 * @author ekaro
 * @version 1.0
 */
public class GeneradorPDFPatrono implements GeneradorReporte {

    private static final BaseColor COLOR_HEADER = new BaseColor(25, 25, 55);
    private static final BaseColor COLOR_FILA_ALT = new BaseColor(245, 245, 250);
    private static final BaseColor COLOR_TOTAL = new BaseColor(25, 25, 55);

    private static final Font FUENTE_TITULO = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE);
    private static final Font FUENTE_SUBTIT = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE);
    private static final Font FUENTE_NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font FUENTE_NEGRITA = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FUENTE_TOTAL = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
    private static final Font FUENTE_PEQUENA = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);

    /**
     * Mapa de empleado → nómina para el período a reportar.
     */
    private final Map<Empleado, Nomina> datosNomina;

    /**
     * Año del período reportado.
     */
    private final int anio;

    /**
     * Mes del período reportado.
     */
    private final int mes;

    /**
     * Quincena del período reportado (1 o 2).
     */
    private final int quincena;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    /**
     * Construye el generador del reporte patronal para un período completo.
     *
     * @param datosNomina Mapa con los empleados y sus nóminas del período.
     * @param anio Año del período.
     * @param mes Mes del período (1-12).
     * @param quincena Quincena del período (1 o 2).
     */
    public GeneradorPDFPatrono(Map<Empleado, Nomina> datosNomina,
            int anio, int mes, int quincena) {
        this.datosNomina = datosNomina;
        this.anio = anio;
        this.mes = mes;
        this.quincena = quincena;
    }

    // -------------------------------------------------------------------------
    // generar
    // -------------------------------------------------------------------------
    /**
     * Genera el PDF del reporte patronal y lo guarda en la ruta indicada.
     *
     * @param rutaDestino Ruta completa del archivo PDF a crear.
     * @throws ArchivoInvalidoException si ocurre un error al generar el
     * documento.
     */
    @Override
    public void generar(String rutaDestino) throws ArchivoInvalidoException {
        Document documento = new Document(PageSize.A4.rotate(), 40, 40, 50, 40);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaDestino));
            documento.open();

            agregarEncabezado(documento);
            agregarTablaConsolidada(documento);
            agregarResumenTotales(documento);
            agregarPiePagina(documento);

        } catch (DocumentException | IOException e) {
            throw new ArchivoInvalidoException(
                    "Error al generar PDF del patrono: " + e.getMessage(), e);
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
     * Agrega el encabezado del reporte patronal.
     *
     * @param doc Documento al que se agrega el encabezado.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarEncabezado(Document doc) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(COLOR_HEADER);
        celda.setPadding(16);
        celda.setBorder(Rectangle.NO_BORDER);

        Paragraph titulo = new Paragraph("REPORTE DE APORTES PATRONALES", FUENTE_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        celda.addElement(titulo);

        String periodo = "Período: " + nombreMes(mes) + " " + anio
                + " — Quincena " + quincena;
        Paragraph subtitulo = new Paragraph(periodo, FUENTE_SUBTIT);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        celda.addElement(subtitulo);

        header.addCell(celda);
        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la tabla consolidada con todos los empleados y sus aportes.
     *
     * @param doc Documento al que se agrega la tabla.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarTablaConsolidada(Document doc) throws DocumentException {
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.2f, 2.5f, 1.8f, 1.8f, 1.8f});

        // Encabezados
        String[] headers = {"ID", "Empleado", "Salario bruto",
            "Total deducciones", "Aportes patronales"};
        for (String h : headers) {
            agregarCeldaHeader(tabla, h);
        }

        boolean alterno = false;
        for (Map.Entry<Empleado, Nomina> entry : datosNomina.entrySet()) {
            Empleado emp = entry.getKey();
            Nomina nom = entry.getValue();

            BaseColor fondo = alterno ? COLOR_FILA_ALT : BaseColor.WHITE;
            agregarCeldaFila(tabla, emp.getId(), fondo, false);
            agregarCeldaFila(tabla, emp.getNombre(), fondo, false);
            agregarCeldaFila(tabla, fmt(nom.getSalarioBruto()), fondo, true);
            agregarCeldaFila(tabla, fmt(nom.getTotalDeducciones()), fondo, true);
            agregarCeldaFila(tabla, fmt(nom.getAportesPatronales()), fondo, true);
            alterno = !alterno;
        }

        tabla.setSpacingAfter(16);
        doc.add(tabla);
    }

    /**
     * Agrega el resumen con los totales globales del período.
     *
     * @param doc Documento al que se agrega el resumen.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarResumenTotales(Document doc) throws DocumentException {
        double totalBruto = 0;
        double totalDeducciones = 0;
        double totalPatronal = 0;
        double totalNeto = 0;

        for (Nomina n : datosNomina.values()) {
            totalBruto += n.getSalarioBruto();
            totalDeducciones += n.getTotalDeducciones();
            totalPatronal += n.getAportesPatronales();
            totalNeto += n.getSalarioNeto();
        }

        Paragraph tituloResumen = new Paragraph("Resumen del período", FUENTE_NEGRITA);
        tituloResumen.setSpacingAfter(6);
        doc.add(tituloResumen);

        PdfPTable resumen = new PdfPTable(4);
        resumen.setWidthPercentage(100);

        agregarCeldaTotal(resumen, "Total salarios brutos", fmt(totalBruto));
        agregarCeldaTotal(resumen, "Total deducciones", fmt(totalDeducciones));
        agregarCeldaTotal(resumen, "Total aportes patronales", fmt(totalPatronal));
        agregarCeldaTotal(resumen, "Total salarios netos", fmt(totalNeto));

        resumen.setSpacingAfter(16);
        doc.add(resumen);
    }

    /**
     * Agrega el pie de página del reporte.
     *
     * @param doc Documento al que se agrega el pie.
     * @throws DocumentException si ocurre un error al agregar elementos.
     */
    private void agregarPiePagina(Document doc) throws DocumentException {
        Paragraph pie = new Paragraph(
                "Generado el " + java.time.LocalDate.now()
                + "  |  Sistema de Nómina CR  |  Uso exclusivo del patrono",
                FUENTE_PEQUENA);
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(new LineSeparator());
        doc.add(pie);
    }

    // -------------------------------------------------------------------------
    // Helpers de celdas
    // -------------------------------------------------------------------------
    /**
     * Agrega una celda de encabezado de tabla con fondo oscuro.
     *
     * @param tabla Tabla donde se agrega la celda.
     * @param texto Texto del encabezado.
     */
    private void agregarCeldaHeader(PdfPTable tabla, String texto) {
        Font f = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(COLOR_HEADER);
        c.setPadding(7);
        c.setBorder(Rectangle.NO_BORDER);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(c);
    }

    /**
     * Agrega una celda de datos a la tabla consolidada.
     *
     * @param tabla Tabla donde se agrega la celda.
     * @param texto Contenido de la celda.
     * @param fondo Color de fondo de la celda.
     * @param derecha Si es {@code true} alinea el texto a la derecha.
     */
    private void agregarCeldaFila(PdfPTable tabla, String texto,
            BaseColor fondo, boolean derecha) {
        PdfPCell c = new PdfPCell(new Phrase(texto, FUENTE_NORMAL));
        c.setBackgroundColor(fondo);
        c.setPadding(6);
        c.setBorderColor(new BaseColor(230, 230, 235));
        if (derecha) {
            c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        }
        tabla.addCell(c);
    }

    /**
     * Agrega una celda del resumen de totales con fondo oscuro.
     *
     * @param tabla Tabla de resumen.
     * @param concepto Descripción del total.
     * @param valor Valor formateado.
     */
    private void agregarCeldaTotal(PdfPTable tabla, String concepto, String valor) {
        PdfPCell cConcepto = new PdfPCell(new Phrase(concepto, FUENTE_TOTAL));
        cConcepto.setBackgroundColor(COLOR_TOTAL);
        cConcepto.setPadding(8);
        cConcepto.setBorder(Rectangle.NO_BORDER);

        PdfPCell cValor = new PdfPCell(new Phrase(valor, FUENTE_TOTAL));
        cValor.setBackgroundColor(new BaseColor(40, 40, 80));
        cValor.setPadding(8);
        cValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cValor.setBorder(Rectangle.NO_BORDER);

        tabla.addCell(cConcepto);
        tabla.addCell(cValor);
    }

    // -------------------------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------------------------
    /**
     * Formatea un monto como número con 2 decimales.
     */
    private String fmt(double monto) {
        return String.format("%.2f", monto);
    }

    /**
     * Devuelve el nombre del mes en español.
     */
    private String nombreMes(int mes) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo",
            "Junio", "Julio", "Agosto", "Setiembre", "Octubre",
            "Noviembre", "Diciembre"};
        return (mes >= 1 && mes <= 12) ? meses[mes] : "?";
    }

    /**
     * Devuelve el nombre de archivo sugerido para el reporte patronal.
     *
     * @return Nombre con formato "patrono_anio_mes_Q#.pdf".
     */
    @Override
    public String getNombreArchivo() {
        return "patrono_" + anio + "_"
                + String.format("%02d", mes) + "_Q" + quincena + ".pdf";
    }
}
