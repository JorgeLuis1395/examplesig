/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.utilitarios.sfccbdmq.Recurso;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author luis
 */
public class DocumentoXLS {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloFecha;
    private final CellStyle estiloNumerico;
    
    public DocumentoXLS(String titulo) throws IOException, DocumentException {
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        documento = new HSSFWorkbook();
        hoja = documento.createSheet(titulo);
        fichero = new FileOutputStream(temp);
        estiloFecha = documento.createCellStyle();
        estiloFecha.setDataFormat(documento.createDataFormat().getFormat("dd/MM/yyyy"));
        estiloNumerico = documento.createCellStyle();
        estiloNumerico.setDataFormat(documento.createDataFormat().getFormat("###,##0.00"));
    }

    public void agregarFila(List<AuxiliarReporte> valores, boolean cabecera) {
        Row fila;
        if (cabecera) {
            fila = hoja.createRow(0);
        } else {
            fila = hoja.createRow(hoja.getLastRowNum() + 1);
        }
        int j = 0;

        for (AuxiliarReporte au : valores) {
            Cell celda = fila.createCell(j++);
            switch (au.getDato()) {
                case "String": {
                    String valor = (String) au.getValor();
                    celda.setCellValue(valor);
                    break;
                }
                case "double": {
                    Double valor = (Double) au.getValor();
                    double cuadre = Math.round(valor.doubleValue() * 100);
                    double dividido = cuadre / 100;
                    BigDecimal valorb = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                    celda.setCellStyle(estiloNumerico);
                    celda.setCellValue(valorb.doubleValue());
                    break;
                }
                case "Double": {
                    Double valor = (Double) au.getValor();
                    double cuadre = Math.round(valor.doubleValue() * 100);
                    double dividido = cuadre / 100;
                    BigDecimal valorb = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                    celda.setCellStyle(estiloNumerico);
                    celda.setCellValue(valorb.doubleValue());
                    break;
                }
                case "Integer": {
                    Integer valor = (Integer) au.getValor();
                    celda.setCellValue(valor);
                    break;
                }
                case "fecha": {
                    Date valor = (Date) au.getValor();
                    celda.setCellStyle(estiloFecha);
                    celda.setCellValue(valor);
                    break;
                }
            }

        }
//        for (String v : valores) {
//            Cell celda = fila.createCell(j++);
//            celda.setCellValue(v);
//        }
    }

    public Recurso traerRecurso() throws IOException {
        for (int j = 0; j < hoja.getRow(0).getPhysicalNumberOfCells(); j++) {
            hoja.autoSizeColumn(j);
        }

        documento.write(fichero);
        fichero.flush();
        fichero.close();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return new Recurso(Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
//        return new Recurso(ec, temp.getName(), Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
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
}