/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.utilitarios.sfccbdmq.Recurso;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
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
import org.apache.poi.ss.usermodel.Color;
import org.entidades.sfccbdmq.Activos;

/**
 *
 * @author luis
 */
public class EtiquetaPDF {

    private final File temp;
    private final Document documento;
    private PdfPTable tabla;
    private byte[] archivo;

    public EtiquetaPDF(String titulo, List<Activos> activos, String usuario) throws IOException, DocumentException {
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".pdf");
        Rectangle one = new Rectangle(140, 70);
        documento = new Document(one);
        // left rigth top bottom
        documento.setMargins(5, 5, 0, 0);
//        documento = new Document(PageSize.A4, 15, 15, 120, 30);
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(temp));
        documento.open();
        PdfContentByte cb = writer.getDirectContent();
        for (Activos a:activos) {
//        for (int i = 0; i <= 5; i++) {
            Paragraph linea = new Paragraph("CBDMQ", new Font(Font.TIMES_ROMAN, 8, Font.BOLD));
            linea.setAlignment(Element.ALIGN_LEFT);
            documento.add(linea);
            String descrip=a.getDescripcion().length()>20?a.getDescripcion().substring(0, 20):a.getDescripcion();
            
            Paragraph lineaDescripcion = new Paragraph(descrip, new Font(Font.TIMES_ROMAN, 8, Font.NORMAL));
            lineaDescripcion.setAlignment(Element.ALIGN_LEFT);
            documento.add(lineaDescripcion);
            Paragraph lineaSerie = new Paragraph("SERIE:"+a.getNumeroserie(), new Font(Font.TIMES_ROMAN, 8, Font.NORMAL));
            lineaSerie.setAlignment(Element.ALIGN_LEFT);
            documento.add(lineaSerie);
            Barcode128 uccEan128 = new Barcode128();
            uccEan128.setCodeType(Barcode128.CODE128_UCC);
            uccEan128.setCode(a.getCodigo());
            uccEan128.setBarHeight(uccEan128.getBarHeight()*0.7f);
            
//            uccEan128.setFont(Font.TIMES_ROMAN);
            
           
            uccEan128.setTextAlignment(Element.ALIGN_CENTER);
            documento.add(
                    uccEan128.createImageWithBarcode(cb, null, null));
        }

    }

    public void agregarLinea() {
        try {
            DottedLineSeparator separator = new DottedLineSeparator();
            separator.setPercentage(59500f / 523f);
            Chunk linebreak = new Chunk(separator);
            documento.add(linebreak);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void finDePagina() {
        documento.newPage();
    }

    public void agregarFila(int columnas, List<String> valores, boolean cabecera) {
        if (cabecera) {
            tabla = new PdfPTable(columnas);
            tabla.setWidthPercentage(100);
            tabla.setHeaderRows(1);
            for (String v : valores) {
                tabla.addCell(encabezadoIzquierda(v));
            }
        } else {
            for (String v : valores) {
                tabla.addCell(celdaIzquierda(v));
            }
        }
    }

    public void agregarTabla(List<String> titulos, List<AuxiliarReporte> valores, int tamanio, int porcentaje, String tituloTabla) {
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
            tablaInterna.setHorizontalAlignment(Element.ALIGN_LEFT);

            if (!((tituloTabla == null) || (tituloTabla.isEmpty()))) {
                filasTitulo++;
                tablaInterna.setHeaderRows(2);
                tablaInterna.addCell(encabezadoTabla(tituloTabla, titulos.size()));
                documento.add(tablaInterna);
//                tablaInterna.setHeaderRows(filasTitulo);

            } else {
                tablaInterna.setHeaderRows(filasTitulo);
            }

            for (String v : titulos) {
                tablaInterna.addCell(encabezadoIzquierda(v));
            }
            for (AuxiliarReporte v : valores) {
                switch (v.getDato()) {
                    case "String":
                        tablaInterna.addCell(celdaIzquierda((String) v.getValor()));
                        break;
                    case "Double":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celdaIzquierda(""));
                        } else {
                            DecimalFormat df = new DecimalFormat("###,##0.00");
                            double valor = (v.getValor() == null ? 0.0 : (double) v.getValor());
                            tablaInterna.addCell(celdaDerecha(df.format(valor)));
                        }
                        break;
                    case "Date":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celdaIzquierda(""));
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            tablaInterna.addCell(celdaIzquierda(sdf.format(v.getValor())));
                        }
                        break;
                }
            }

            documento.add(tablaInterna);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarTablaReporte(List<AuxiliarReporte> titulos, List<AuxiliarReporte> valores,
            int tamanio, int porcentaje, String tituloTabla) {
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
                tablaInterna.addCell(encabezado((String) v.getValor(), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
            }
            for (AuxiliarReporte v : valores) {
                switch (v.getDato()) {
                    case "String":
                        tablaInterna.addCell(celda((String) v.getValor(), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
                        break;
                    case "Double":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celdaIzquierda(""));
                        } else {
                            DecimalFormat df = new DecimalFormat("###,##0.00");
                            double valor = (v.getValor() == null ? 0.0 : (double) v.getValor());
                            tablaInterna.addCell(celda(df.format(valor), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
                        }
                        break;
                    case "Date":
                        if (v.getValor() == null) {
                            tablaInterna.addCell(celdaIzquierda(""));
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            tablaInterna.addCell(celda(sdf.format(v.getValor()), v.getAlineacion(), v.isNegrilla(), v.getColumnas(), v.getTamanio()));
                        }
                        break;
                }
            }

            documento.add(tablaInterna);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Recurso traerRecurso() throws IOException, DocumentException {
        documento.close();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return new Recurso(Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
//        return new Recurso(ec, temp.getName(), Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
    }

    private PdfPCell encabezadoIzquierda(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, new Font(Font.TIMES_ROMAN, 6, Font.BOLD)));
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorder(0);
        celda.setBorderWidthTop(1);
        celda.setBorderWidthBottom(1);
        return celda;
    }

    public void agregaTitulo(String texto) {
        Paragraph linea = new Paragraph(texto, new Font(Font.TIMES_ROMAN, 12, Font.BOLD));
        linea.setAlignment(Element.ALIGN_CENTER);
        try {
            documento.add(linea);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private PdfPCell encabezado(String texto, int alinear, boolean bold, int columnas, int fontSize) {
//        Paragraph p;
//        p = new Paragraph(columnas, texto);
//        p.setFont(new Font(Font.TIMES_ROMAN, 6, (bold ? Font.BOLD : Font.NORMAL)));

//        PdfPCell celda = new PdfPCell();
        PdfPCell celda = new PdfPCell(new Phrase(columnas, texto, new Font(Font.TIMES_ROMAN, fontSize, (bold ? Font.BOLD : Font.NORMAL))));
//        celda.addElement(p);
        celda.setHorizontalAlignment(alinear);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        celda.setColspan(columnas);
//        celda.setMinimumHeight(columnas);titulo
        celda.setBorder(0);
        celda.setBorderWidthTop(1);
        celda.setBorderWidthBottom(1);
        return celda;
    }

    private PdfPCell encabezadoDerecha(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, new Font(Font.TIMES_ROMAN, 6, Font.BOLD)));
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorder(0);
        celda.setBorderWidthTop(1);
        celda.setBorderWidthBottom(1);
        return celda;
    }

    private PdfPCell encabezadoTabla(String texto, int columnas) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, new Font(Font.TIMES_ROMAN, 8, Font.BOLD)));
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

    private PdfPCell celdaIzquierda(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, new Font(Font.TIMES_ROMAN, 6, Font.NORMAL)));
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorder(0);
        return celda;
    }

    private PdfPCell celdaDerecha(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, new Font(Font.TIMES_ROMAN, 6, Font.NORMAL)));
        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorder(0);
        return celda;
    }

    private PdfPCell celda(String texto, int alinear, boolean bold, int columnas, int fontSize) {
        PdfPCell celda = new PdfPCell(new Phrase(columnas, texto, new Font(Font.TIMES_ROMAN, fontSize, (bold ? Font.BOLD : Font.NORMAL))));
        celda.setHorizontalAlignment(alinear);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        celda.setColspan(columnas);
        celda.setBorder(0);
        return celda;
    }

    public void agregaParrafo(String texto) {
        Paragraph linea = new Paragraph(texto, new Font(Font.TIMES_ROMAN, 6, Font.NORMAL));
        linea.setAlignment(Element.ALIGN_LEFT);
        try {
            documento.add(linea);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregaParrafoCentro(String texto) {
        Paragraph linea = new Paragraph(texto, new Font(Font.TIMES_ROMAN, 6, Font.NORMAL));
        linea.setAlignment(Element.ALIGN_CENTER);
        try {
            documento.add(linea);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregaParrafo2(String texto) {
        Paragraph linea = new Paragraph(texto, new Font(Font.TIMES_ROMAN, 9, Font.NORMAL));
        linea.setAlignment(Element.ALIGN_JUSTIFIED);
        try {
            documento.add(linea);
        } catch (DocumentException ex) {
            Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
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

    class headerFooter extends PdfPageEventHelper {

        PdfTemplate t;
        Image total;
        List<String> titulos;
        String usuario;
        String titulo;

        public headerFooter(List<String> titulos, String usuario, String titulo) {
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
                table.setWidths(new int[]{50, 530, 105});
                table.setTotalWidth(520);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
//                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
                Image izq = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/images/logo.png"));
//                Image der = Image.getInstance(FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources/imagenes/escuela.png"));
                izq.scaleAbsolute(45f, 45f);
//                izq.scaleAbsolute(106f, 54f);
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
                table.writeSelectedRows(0, -1, 30, 820, writer.getDirectContent());
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
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)),
                    2, 4, 0);
        }
    }

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
                izq.scaleAbsolute(45f, 45f);
//                izq.scaleAbsolute(106f, 54f);
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
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)),
                    2, 4, 0);
        }
    }

    class cabeceraVertical extends PdfPageEventHelper {

        PdfTemplate t;
        Image total;
        String parametros;
        String empresa;
        String usuario;
        String titulo;

        public cabeceraVertical(String parametros, String empresa, String usuario, String titulo) {
            this.parametros = parametros;
            this.empresa = empresa;
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
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{580, 105});
                table.setTotalWidth(520);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
//                table.getDefaultCell().setBorder(Rectangle.BOTTOM);

//                izq.scaleAbsolute(45f, 22f);
                PdfPCell cell = new PdfPCell(new Phrase(empresa + "\n" + titulo, new Font(Font.TIMES_ROMAN, 10, Font.BOLD)));
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
//                table.writeSelectedRows(0, -1, 30, 590, writer.getDirectContent());
//                // Nuevo
//                table = new PdfPTable(1);
//                table.setWidthPercentage(100);
//                table.setWidths(new int[]{800});
//                table.setTotalWidth(800);
//                table.setLockedWidth(true);
//                table.getDefaultCell().setFixedHeight(20);

                cell = new PdfPCell(new Phrase(parametros, new Font(Font.TIMES_ROMAN, 8, Font.BOLD)));
                cell.setBorder(0);
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(cell);
                table.writeSelectedRows(0, -1, 30, 820, writer.getDirectContent());
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
            } catch (BadElementException ex) {
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)),
                    2, 4, 0);
        }
    }

    class cabeceraHorizontal extends PdfPageEventHelper {

        PdfTemplate t;
        Image total;
        String parametros;
        String empresa;
        String usuario;
        String titulo;

        public cabeceraHorizontal(String parametros, String empresa, String usuario, String titulo) {
            this.parametros = parametros;
            this.empresa = empresa;
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
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{580, 205});
                table.setTotalWidth(800);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
                PdfPCell cell = new PdfPCell(new Phrase(empresa + "\n" + titulo, new Font(Font.TIMES_ROMAN, 10, Font.BOLD)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(cell);

//                cell = new PdfPCell(der);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                cell = new PdfPCell(new Phrase("USUARIO : " + usuario + "\n FECHA : " + sdf.format(new Date()), new Font(Font.TIMES_ROMAN, 6, Font.BOLD)));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
//                table.writeSelectedRows(0, -1, 30, 590, writer.getDirectContent());
//                // Nuevo
//                table = new PdfPTable(1);
//                table.setWidthPercentage(100);
//                table.setWidths(new int[]{800});
//                table.setTotalWidth(800);
//                table.setLockedWidth(true);
//                table.getDefaultCell().setFixedHeight(20);

                cell = new PdfPCell(new Phrase(parametros, new Font(Font.TIMES_ROMAN, 8, Font.BOLD)));
                cell.setBorder(0);
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(cell);
                table.writeSelectedRows(0, -1, 30, 570, writer.getDirectContent());
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
            } catch (BadElementException ex) {
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DocumentException ex) {
                Logger.getLogger(EtiquetaPDF.class.getName()).log(Level.SEVERE, null, ex);
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