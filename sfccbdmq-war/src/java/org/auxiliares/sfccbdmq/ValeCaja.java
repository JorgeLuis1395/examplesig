/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import org.utilitarios.sfccbdmq.Recurso;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.entidades.sfccbdmq.Valescajas;

/**
 *
 * @author luis
 */
public class ValeCaja {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloTexto;

    public ValeCaja() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/ValeCaja.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
        estiloTexto = documento.createCellStyle();
    }

    public void llenar(Valescajas r) {
        SimpleDateFormat dia = new SimpleDateFormat("dd");
        SimpleDateFormat mes = new SimpleDateFormat("MM");
        SimpleDateFormat anio = new SimpleDateFormat("yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        ponerDoble((r.getNumerovale().toString()), 4, 5);
        ponerString((r.getCaja().getEmpleado().toString()), 5, 1);
        ponerString((r.getCaja().getDepartamento().toString()), 7, 1);
        ponerString((r.getSolicitante().toString()), 10, 1);
        ponerDoble((dia.format(r.getFecha())), 10, 3);
        ponerDoble((mes.format(r.getFecha())), 10, 4);
        ponerDoble((anio.format(r.getFecha())), 10, 5);
        ponerString((r.getConcepto()), 14, 1);
        ponerDoble((df.format(r.getValor())), 14, 4);
        ponerString((ConvertirNumeroALetras.convertNumberToLetter(r.getValor().doubleValue())), 20, 2);
    }

    private void ponerString(String valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        if (filaHoja == null) {
            filaHoja = hoja.createRow(fila);
        }
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloTexto);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloTexto);
            cell.setCellValue(valor);
        }

    }

    private void ponerDoble(String valor, int fila, int col) {
        CellStyle estilo = documento.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_RIGHT);
        Row filaHoja = hoja.getRow(fila);
        if (filaHoja == null) {
            filaHoja = hoja.createRow(fila);
        }
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estilo);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estilo);
            cell.setCellValue(valor);
        }
    }

    public Recurso traerRecurso() throws IOException {
        documento.write(fichero);
        fichero.flush();
        fichero.close();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return new Recurso(Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
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
