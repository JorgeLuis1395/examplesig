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
import java.util.Calendar;
import java.util.Iterator;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author luis
 */
public class Documento101 {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;

    public Documento101() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/Formulario101.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
    }

    public void ponerMes(int mes) {
        Cell cell = hoja.getRow(4).getCell(1 + mes);
        cell.setCellValue("X");
    }

    

    public void ponerRuc(String ruc) {
        Cell cell = hoja.getRow(8).getCell(1);
        cell.setCellValue(ruc);
    }

    public void ponerRazon(String razon) {
        Cell cell = hoja.getRow(8).getCell(17);
        cell.setCellValue(razon);
    }

    

    public void ponerConcepto(String codigo, double valor, double retenido) {
        Iterator<Row> rowIterator = hoja.iterator();
        int i = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Cell celda;
            int j = 0;
            while (cellIterator.hasNext()) {

                celda = cellIterator.next();
                switch (celda.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        if (HSSFDateUtil.isCellDateFormatted(celda)) {

                        } else {
                            DecimalFormat df = new DecimalFormat("000");
                            String valorStr = df.format(celda.getNumericCellValue());
                            if (valorStr.equals(codigo)) {
                                Cell cell1 = hoja.getRow(i).getCell(j + 2);
                                cell1.setCellValue(Math.abs(valor));
                                if (retenido > 0) {
                                    Cell cell2 = hoja.getRow(i).getCell(j + 9);
                                    cell2.setCellValue(Math.abs(retenido));
                                }
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        String valorStr = celda.getStringCellValue();
                        if (valorStr.equals(codigo)) {
                            Cell cell1 = hoja.getRow(i).getCell(j + 2);
                            cell1.setCellValue(Math.abs(valor));
                            if (retenido > 0) {
                                Cell cell2 = hoja.getRow(i).getCell(j + 9);
                                cell2.setCellValue(Math.abs(retenido));
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        break;
                }
                j++;
            }
            i++;
        }

    }

   

    public Recurso traerRecurso() throws IOException {
        documento.write(fichero);
        fichero.flush();
        fichero.close();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return new Recurso( Files.readAllBytes(Paths.get(temp.getAbsolutePath())));
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
