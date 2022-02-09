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
import java.util.Calendar;
import java.util.Iterator;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author luis
 */
public class AccionPersonalHoja {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;

    public AccionPersonalHoja() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/accionpersonal_1.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
    }

    public void ponerFechaCreacion(String fechaCreacion) {
        Cell cell = hoja.getRow(2).getCell(9);
        cell.setCellValue(fechaCreacion);
        cell = hoja.getRow(46).getCell(4);
        cell.setCellValue(fechaCreacion);
    }

    public void ponerNumeroAccion(String numeroAccion, Integer numero) {
        Cell cell = hoja.getRow(1).getCell(9);
        cell.setCellValue("EP-EMSEG" + numeroAccion + "-" + numero.toString());
        cell = hoja.getRow(46).getCell(1);
        cell.setCellValue(numero);
    }

    public void ponerDecreto() {
        Cell cell = hoja.getRow(5).getCell(2);
        cell.setCellValue("X");
    }

    public void ponerAcuerdo() {
        Cell cell = hoja.getRow(5).getCell(6);
        cell.setCellValue("X");
    }

    public void ponerResolucion() {
        Cell cell = hoja.getRow(5).getCell(9);
        cell.setCellValue("X");
    }

    public void ponerNoDecreto(String numero, String fecha) {
        Cell cell = hoja.getRow(7).getCell(2);
        cell.setCellValue(numero);
        cell = hoja.getRow(7).getCell(6);
        cell.setCellValue(fecha);
    }
    public void ponerNoConcurso(String numero, String fecha) {
        Cell cell = hoja.getRow(36).getCell(2);
        cell.setCellValue(numero);
        cell = hoja.getRow(36).getCell(4);
        cell.setCellValue(fecha);
    }

    public void ponerApellidos(String apellidos) {
        Cell cell = hoja.getRow(9).getCell(0);
        cell.setCellValue(apellidos);
    }

    public void ponerNombres(String nombres) {
        Cell cell = hoja.getRow(9).getCell(6);
        cell.setCellValue(nombres);
    }

    public void ponerCedula(String cedula) {
        Cell cell = hoja.getRow(13).getCell(0);
        cell.setCellValue(cedula);
    }

    public void ponerDesde(String desde) {
        Cell cell = hoja.getRow(13).getCell(7);
        cell.setCellValue(desde);
    }

    public void ponerExplicacion(String explicacion) {
        Cell cell = hoja.getRow(15).getCell(0);
        cell.setCellValue(explicacion);
    }

    public void ponerMotivo(String motivo) {
        Cell cell = hoja.getRow(16).getCell(1);
        cell.setCellValue(motivo);
    }

    public void ponerTipoAccion(String codigo) {
        Iterator<Row> rowIterator = hoja.iterator();
        int i = 0;
        codigo = codigo.trim();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Cell celda;
            int j = 0;
            while (cellIterator.hasNext()) {

                celda = cellIterator.next();
                if (celda.getCellType() == Cell.CELL_TYPE_STRING) {
                    String valorStr = celda.getStringCellValue().trim();
                    if (valorStr.trim().length() > 0) {
                        if (codigo.contains(valorStr)) {
                            Cell cell1 = hoja.getRow(i).getCell(j + 1);
                            cell1.setCellValue("X");
                        }
                    }
                }
                j++;
            }
            i++;
        }
    }

    public void ponerFirma(String firma) {
        Cell cell = hoja.getRow(37).getCell(6);
        cell.setCellValue(firma);
    }
    public void ponerCargoRepresentante(String cargo) {
        Cell cell = hoja.getRow(38).getCell(6);
        cell.setCellValue(cargo);
    }
    public void ponerCargoNominadora(String cargo) {
        Cell cell = hoja.getRow(42).getCell(1);
        cell.setCellValue(cargo);
    }

    public void ponerFirmaNominadora(String firma) {
        Cell cell = hoja.getRow(41).getCell(1);
        cell.setCellValue(firma);
    }

    public void ponerSituacionActual(String proceso, String subProceso, String cargo, String lugar, double valor, String partida) {
        Cell cell = hoja.getRow(26).getCell(3);
        cell.setCellValue(proceso);
        cell = hoja.getRow(27).getCell(3);
        cell.setCellValue(subProceso);
        cell = hoja.getRow(28).getCell(3);
        cell.setCellValue(cargo);
        cell = hoja.getRow(29).getCell(3);
        cell.setCellValue(lugar);
        cell = hoja.getRow(30).getCell(3);
        cell.setCellValue(valor);
        cell = hoja.getRow(32).getCell(1);
        cell.setCellValue(partida);
    }

    public void ponerSituacionPropuesta(String proceso, String subProceso, String cargo, String lugar, double valor, String partida) {
        Cell cell = hoja.getRow(26).getCell(8);
        cell.setCellValue(proceso);
        cell = hoja.getRow(27).getCell(8);
        cell.setCellValue(subProceso);
        cell = hoja.getRow(28).getCell(8);
        cell.setCellValue(cargo);
        cell = hoja.getRow(29).getCell(8);
        cell.setCellValue(lugar);
        cell = hoja.getRow(30).getCell(8);
        cell.setCellValue(valor);
        cell = hoja.getRow(32).getCell(7);
        cell.setCellValue(partida);
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
