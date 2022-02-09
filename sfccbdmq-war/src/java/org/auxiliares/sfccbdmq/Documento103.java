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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author luis
 */
public class Documento103 {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;

    public Documento103() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/Formulario103.xls");
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

    public void ponerAnio(int anio) {
        Cell cell = hoja.getRow(4).getCell(18);
        cell.setCellValue(anio);
    }

    public void ponerRuc(String ruc) {
        Cell cell = hoja.getRow(9).getCell(1);
        cell.setCellValue(ruc);
    }

    public void ponerRazon(String razon) {
        Cell cell = hoja.getRow(9).getCell(14);
        cell.setCellValue(razon);
    }

    public void ponerRMU(double valor, double retenido) {
        Cell cell = hoja.getRow(14).getCell(23);
        cell.setCellValue(valor);
        Cell cell2 = hoja.getRow(14).getCell(30);
        cell2.setCellValue(retenido);

    }

    public void ponerConcepto(String codigo, double valor, double retenido) {
        int rows = 84;
        for (int i = 0; i < rows; i++) {
            Cell cell = hoja.getRow(i).getCell(21);
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                DecimalFormat df = new DecimalFormat("000");
                String valorStr = df.format(cell.getNumericCellValue());
                if (valorStr.equals(codigo)) {
                    Cell cell1 = hoja.getRow(i).getCell(23);
                    cell1.setCellValue(valor);
                    if (retenido > 0) {
                        Cell cell2 = hoja.getRow(i).getCell(30);
                        cell2.setCellValue(retenido);
                    }
                }
            }
        }

    }

    public Recurso traerRecurso() throws IOException {
//        for (int j = 0; j < hoja.getRow(0).getPhysicalNumberOfCells(); j++) {
//            hoja.autoSizeColumn(j);
//        }

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
