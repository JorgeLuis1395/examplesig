/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.DottedLineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author luis
 */
public class DocumentoPDF {

    private final File temp;
    private final Document documento;
    private byte[] archivo;
    private int numeroPaginas = 0;

    public DocumentoPDF(String titulo, List<String> titulos, String usuario) throws IOException, DocumentException {
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".pdf");
        // left rigth top bottom
        documento = new Document(PageSize.A4, 60, 60, 120, 30);
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(temp));
//        writer.setPageEvent(new headerFooterVertical(titulos, usuario, titulo));//para vertical
        writer.setPageEvent(new headerFooter(titulo));//para vertical
        documento.open();
    }

    public DocumentoPDF(String titulo, String usuario) throws IOException, DocumentException {
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".pdf");
        documento = new Document(PageSize.A4.rotate());
        // left rigth top bottom
        documento.setMargins(45, 20, 80, 30);
//        documento = new Document(PageSize.A4, 15, 15, 120, 30);
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(temp));
        writer.setPageEvent(new headerFooterRotate(usuario, titulo));
        documento.open();
    }

    public void agregarLinea() {
        try {
            DottedLineSeparator separator = new DottedLineSeparator();
            separator.setPercentage(59500f / 523f);
            Chunk linebreak = new Chunk(separator);
            documento.add(linebreak);
        } catch (DocumentException ex) {
            Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarTabla(List<String> titulos, List<AuxiliarReporte> valores, int tamanio, int porcentaje, String tituloTabla, int alineacion) {
        int filasTitulo = 1;
        try {
            if (titulos == null) {
                titulos = new LinkedList<>();
            }
            if (valores == null) {
                return;
            }
            if (valores.isEmpty()) {
                return;
            }
            if (titulos.isEmpty()) {
                filasTitulo = 0;
            } else {

            }
            PdfPTable tablaInterna = new PdfPTable(tamanio);
            tablaInterna.setWidthPercentage(porcentaje);
            tablaInterna.setHorizontalAlignment(alineacion);

            if (!((tituloTabla == null) || (tituloTabla.isEmpty()))) {
                filasTitulo++;
                tablaInterna.setHeaderRows(2);
                tablaInterna.addCell(encabezadoTabla(tituloTabla, titulos.size()));
                documento.add(tablaInterna);

            } else {
                tablaInterna.setHeaderRows(filasTitulo);
            }

            for (String v : titulos) {
                tablaInterna.addCell(encabezado(v, alineacion, false, tamanio, 8, false));
            }
            for (AuxiliarReporte v : valores) {
                switch (v.getDato()) {
                    case "String":
                        tablaInterna.addCell(celda((String) v.getValor(), alineacion, false, 6, 8));
                        break;
                    case "Double":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celda((""), alineacion, false, 6, 8));
                        } else {
                            DecimalFormat df = new DecimalFormat("###,##0.00");
                            double valor = (v.getValor() == null ? 0.0 : (double) v.getValor());
                            tablaInterna.addCell(celda(df.format(valor), Element.ALIGN_RIGHT, false, 6, 8));
                        }
                        break;
                    case "Date":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celda((""), alineacion, false, 6, 8));
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            tablaInterna.addCell(celda((sdf.format(v.getValor())), alineacion, false, 6, 8));
                        }
                        break;
                }
            }

            documento.add(tablaInterna);
        } catch (DocumentException ex) {
            Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarTablaReporte(
            List<AuxiliarReporte> titulos,
            List<AuxiliarReporte> valores,
            int tamanio,
            int porcentaje,
            String tituloTabla) {

        int filasTitulo = 1;
        try {
            if (titulos == null) {
                titulos = new LinkedList<>();
            }
            if (valores == null) {
                return;
            }
            if (valores.isEmpty()) {
                return;
            }
            if (titulos.isEmpty()) {
                filasTitulo = 0;
            }

            float[] anchoColumnas = new float[tamanio];
            int i = 0;
            for (AuxiliarReporte v : titulos) {
                anchoColumnas[i++] = v.getColumnas();
            }
            PdfPTable tablaInterna = new PdfPTable(anchoColumnas);
//            tablaInterna.setTotalWidth(documento.getPageSize().getWidth() - 80);
//            tablaInterna.setLockedWidth(true);
            tablaInterna.setWidthPercentage(porcentaje);
            tablaInterna.setHorizontalAlignment(Element.ALIGN_LEFT);

            if (!((tituloTabla == null) || (tituloTabla.isEmpty()))) {
                filasTitulo++;
                tablaInterna.setHeaderRows(2);
                tablaInterna.addCell(encabezadoTabla(tituloTabla, titulos.size()));
                documento.add(tablaInterna);
            } else {
                tablaInterna.setHeaderRows(filasTitulo);
            }

            for (AuxiliarReporte v : titulos) {
                tablaInterna.addCell(encabezado((String) v.getValor(), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio(), true));
            }
            for (AuxiliarReporte v : valores) {
                switch (v.getDato()) {
                    case "String":
                        tablaInterna.addCell(celda((String) v.getValor(), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
                        break;
                    case "Double":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celda("", Element.ALIGN_LEFT, false, 6, 8));
                        } else {
                            DecimalFormat df = new DecimalFormat("###,##0.00");
                            double valor = (v.getValor() == null ? 0.0 : (double) v.getValor());
                            tablaInterna.addCell(celda(df.format(valor), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
                        }
                        break;
                    case "Date":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celda("", Element.ALIGN_LEFT, false, 6, 8));
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            tablaInterna.addCell(celda(sdf.format(v.getValor()), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
                        }
                        break;
                }
            }

            documento.add(tablaInterna);
        } catch (DocumentException ex) {
            Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Recurso traerRecurso() throws IOException, DocumentException {
        documento.close();
        PdfReader pdfr = new PdfReader(Files.readAllBytes(temp.toPath()));
        setNumeroPaginas(pdfr.getNumberOfPages());
        pdfr.close();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return new Recurso(Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
    }

    public File traerArchivo() {
        return temp;
    }

    private PdfPCell encabezado(String texto, int alinear, boolean bold, int columnas, int fontSize, boolean cerrado) {
        PdfPCell celda = new PdfPCell(new Phrase(columnas, texto, new Font(Font.TIMES_ROMAN, fontSize, (bold ? Font.BOLDITALIC : Font.NORMAL))));
        celda.setHorizontalAlignment(alinear);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorder(0);
        if (cerrado) {
            celda.setBorderWidthTop(1);
            celda.setBorderWidthBottom(1);
        }
        return celda;
    }

    private PdfPCell encabezadoTabla(String texto, int columnas) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, new Font(Font.TIMES_ROMAN, 8, Font.BOLDITALIC)));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setColspan(columnas);
        celda.setBorder(0);
        celda.setBorderWidthTop(1);
        celda.setBorderWidthLeft(1);
        celda.setBorderWidthRight(1);
        celda.setBorderWidthBottom(0);
        return celda;
    }

    private PdfPCell celda(String texto, int alineacion, boolean bold, int columnas, int fontSize) {
        PdfPCell celda = new PdfPCell(new Phrase(columnas, texto, new Font(Font.TIMES_ROMAN, fontSize, (bold ? Font.BOLD : Font.NORMAL))));
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_TOP);
        celda.setBorder(0);
        return celda;
    }

    public void agregaParrafo(String texto, int alineacion, int tamanio, boolean bold) {
        Paragraph linea = new Paragraph(texto, new Font(Font.TIMES_ROMAN, tamanio, (bold ? Font.BOLD : Font.NORMAL)));
        linea.setAlignment(alineacion);
        try {
            documento.add(linea);
        } catch (DocumentException ex) {
            Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the archivo
     */
    public byte[] getArchivo() {
        return archivo;
    }

    /**
     * @param archivo the archivo to set
     */
    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    /**
     * @return the numeroPaginas
     */
    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    /**
     * @param numeroPaginas the numeroPaginas to set
     */
    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    /**
     * Clase para imprimir encabezado y pie de página para página A4 Verticales
     * sólo con el logo
     */
    class headerFooter extends PdfPageEventHelper {

        PdfTemplate t;
        Image total;
        String titulo;

        public headerFooter(String titulo) {
            this.titulo = titulo;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            t = writer.getDirectContent().createTemplate(0, 0);
            try {
                total = Image.getInstance(t);
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            try {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{90, 240, 90});
                table.setTotalWidth(420);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(51);
                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
                Image centro = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/images/logo.png"));
                centro.scaleAbsolute(50f, 50f);

                PdfPCell cell = new PdfPCell(new Phrase(""));
                cell.setBorder(0);
                table.addCell(cell);

                cell = new PdfPCell(centro);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(0);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(0);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("\n" + titulo, new Font(Font.TIMES_ROMAN, 12, Font.BOLD)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(0);
                table.addCell(cell);

                table.writeSelectedRows(0, -1, 80, 803, writer.getDirectContent());
                ColumnText.showTextAligned(
                        writer.getDirectContent(),
                        Element.ALIGN_CENTER,
                        new Phrase(String.format("%d", writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 7, Font.BOLDITALIC)),
                        (document.right() - document.left()) / 2 + document.leftMargin(),
                        document.bottom() - 10, 0);
            } catch (BadElementException | IOException ex) {
                Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 7, Font.BOLDITALIC)),
                    2, 4, 0);
        }
    }

    /**
     * Clase para imprimir encabezado y pie de página para página A4 Verticales
     */
    class headerFooterVertical extends PdfPageEventHelper {

        PdfTemplate t;
        Image total;
        List<String> titulos;
        String usuario;
        String titulo;

        public headerFooterVertical(List<String> titulos, String usuario, String titulo) {
            this.titulos = titulos;
            this.usuario = usuario;
            this.titulo = titulo;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            t = writer.getDirectContent().createTemplate(0, 0);
            try {
                total = Image.getInstance(t);
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            try {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{90, 240, 90});
                table.setTotalWidth(420);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(51);
                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
                Image izq = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/imagenes/logo.png"));
//                Image der = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/imagenes/escuela.png"));
                izq.scaleAbsolute(50f, 50f);
//                der.scaleAbsolute(40f, 40f);

                PdfPCell cell = new PdfPCell(izq);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("EMPRESA PUBLICA METROPOLITANA \nDE LOGISTICA PARA LA SEGURIDAD\n"
                        
                         
                        
                        + titulo, new Font(Font.TIMES_ROMAN, 8, Font.BOLDITALIC)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                cell = new PdfPCell(new Phrase("USUARIO : " + usuario + "\n FECHA : " + sdf.format(new Date()), new Font(Font.TIMES_ROMAN, 7, Font.ITALIC)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                table.writeSelectedRows(0, -1, 80, 803, writer.getDirectContent());
                ColumnText.showTextAligned(
                        writer.getDirectContent(),
                        Element.ALIGN_CENTER,
                        new Phrase(String.format("%d", writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 7, Font.BOLDITALIC)),
                        (document.right() - document.left()) / 2 + document.leftMargin(),
                        document.bottom() - 10, 0);
            } catch (BadElementException | IOException ex) {
                Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 7, Font.BOLDITALIC)),
                    2, 4, 0);
        }
    }

    /**
     * Clase para imprimir encabezado y pie de página para página A4
     * Horizontales
     */
    class headerFooterRotate extends PdfPageEventHelper {

        PdfTemplate t;
        Image total;
        String usuario;
        String titulo;

        public headerFooterRotate(String usuario, String titulo) {
            this.usuario = usuario;
            this.titulo = titulo;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            t = writer.getDirectContent().createTemplate(0, 0);
            try {
                total = Image.getInstance(t);
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            try {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{50, 530, 205});
                table.setTotalWidth(800);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
//                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
                Image izq = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/images/logo.png"));
//                Image der = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/imagenes/escuela.png"));
                izq.scaleAbsolute(50f, 50f);
//                izq.scaleAbsolute(45f, 22f);

                PdfPCell cell = new PdfPCell(izq);
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(titulo, new Font(Font.TIMES_ROMAN, 10, Font.BOLD)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(cell);

//                cell = new PdfPCell(der);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                cell = new PdfPCell(new Phrase("USUARIO : " + usuario + "\n FECHA : " + sdf.format(new Date()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, 30, 580, writer.getDirectContent());
//                table.writeSelectedRows(0, -1, 30, 803, writer.getDirectContent());
//                for (String s:titulos){
//                     document.add(new Paragraph(s,new Font(Font.TIMES_ROMAN, 8, Font.BOLD)));
//                }
                ColumnText.showTextAligned(
                        writer.getDirectContent(),
                        Element.ALIGN_CENTER,
                        new Phrase(String.format("%d", writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)),
                        (document.right() - document.left()) / 2 + document.leftMargin(),
                        document.bottom() - 10, 0);
            } catch (BadElementException | IOException ex) {
                Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(DocumentoPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)),
                    2, 4, 0);
        }
    }
}
