/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author luis
 */
public class PlantillaRol {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloNumerico;
    private final CellStyle estiloFecha;
    private final CellStyle estiloTexto;
    private final CellStyle estiloTextoIzquierda;

    public PlantillaRol() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/loatip.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        Font cellFont = documento.createFont();
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellFont.setFontName("Calibri");
        cellFont.setFontHeightInPoints(Short.valueOf("11"));
        fichero = new FileOutputStream(temp);
        estiloNumerico = documento.createCellStyle();
        estiloFecha = documento.createCellStyle();
        estiloTexto = documento.createCellStyle();
        estiloTexto.setFont(cellFont);
        estiloTexto.setBorderBottom(new Short("1"));
        estiloTexto.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        estiloTexto.setBorderLeft(new Short("1"));
        estiloTexto.setBorderRight(new Short("1"));
        estiloTexto.setBorderTop(new Short("1"));
        estiloTextoIzquierda = documento.createCellStyle();
        estiloTextoIzquierda.setFont(cellFont);
        estiloTextoIzquierda.setBorderBottom(new Short("1"));
        estiloTextoIzquierda.setAlignment(CellStyle.ALIGN_LEFT);
        estiloTextoIzquierda.setBorderLeft(new Short("1"));
        estiloTextoIzquierda.setBorderRight(new Short("1"));
        estiloTextoIzquierda.setBorderTop(new Short("1"));
        estiloNumerico.setDataFormat(documento.createDataFormat().getFormat("###,##0.00"));
        estiloNumerico.setBorderBottom(new Short("1"));
        estiloNumerico.setBorderLeft(new Short("1"));
        estiloNumerico.setBorderRight(new Short("1"));
        estiloNumerico.setBorderTop(new Short("1"));
        estiloNumerico.setAlignment(CellStyle.ALIGN_RIGHT);
        estiloFecha.setDataFormat(documento.createDataFormat().getFormat("dd/MM/yyyy"));
        estiloFecha.setBorderBottom(new Short("1"));
        estiloFecha.setBorderLeft(new Short("1"));
        estiloFecha.setBorderRight(new Short("1"));
        estiloFecha.setBorderTop(new Short("1"));
        estiloFecha.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);

    }

    private void ponerCelda(int fila, int col, String valor, CellStyle estilo) {
        Row filaHoja = hoja.getRow(fila);
        if (filaHoja == null) {
            filaHoja = hoja.createRow(fila);
        }
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            estilo.setWrapText(true);
            cell.setCellStyle(estilo);
            cell.setCellValue(valor);
        } else {
            estilo.setWrapText(true);
            cell.setCellStyle(estilo);
            cell.setCellValue(valor);
        }
    }

    public CellStyle estiloTitulo() {
        CellStyle estilo = estiloTipoRol();
        estilo.setAlignment(CellStyle.ALIGN_LEFT);
        Font cellFont = documento.createFont();
//        Font cellFont = estilo.;
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellFont.setFontName("Calibri");
        cellFont.setFontHeightInPoints(Short.valueOf("11"));
        estilo.setFont(cellFont);
        return estilo;
    }

    public CellStyle estiloFormula() {
//        Cell cell = hoja.getRow(5).getCell(1);
        CellStyle estilo = estiloNumerico;
//        estilo.setDataFormat(documento.createDataFormat().getFormat("###,##0.00"));
//        estilo.setAlignment(CellStyle.ALIGN_RIGHT);
        Font cellFont = documento.createFont();
//        Font cellFont = estilo.;
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellFont.setFontName("Calibri");
        cellFont.setFontHeightInPoints(Short.valueOf("11"));
        estilo.setFont(cellFont);
        return estilo;
    }

    public CellStyle estiloTipoRol() {
        Cell cell = hoja.getRow(5).getCell(1);
        cell.getCellStyle().setAlignment(CellStyle.ALIGN_LEFT);
        return cell.getCellStyle();
    }

    public CellStyle estiloTipoFirmas() {
//        Cell cell = hoja.getRow(7).getCell(1);
        CellStyle estilo = documento.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        Font cellFont = documento.createFont();
//        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellFont.setFontName("Calibri");
        cellFont.setFontHeightInPoints(Short.valueOf("11"));
        estilo.setFont(cellFont);
        estilo.setBorderBottom(new Short("1"));
        estilo.setBorderLeft(new Short("1"));
        estilo.setBorderRight(new Short("1"));
        estilo.setBorderTop(new Short("1"));
        return estilo;
    }

    private void ponerString(String valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        float puntos = hoja.getRow(5).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloTipoRol());
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloTipoRol());
            cell.setCellValue(valor);
        }

    }

    private String generaRangoFormulaEnFila(int filaInicio, int filaFin, int letra) {
        // la columna donde se situa el primer tiempo será la A (codigo ASCII 65)

        if (letra > 90) {
            final byte columnaB = (byte) (letra - 26);
            final char primeraColumna = (char) columnaB;
            return "(A" + primeraColumna + filaInicio + ":A" + primeraColumna + filaFin + ")";
        } else {
            final byte columnaB = (byte) letra;
            final char primeraColumna = (char) columnaB;
            return "(" + primeraColumna + filaInicio + ":" + primeraColumna + filaFin + ")";
        }

    }

    private void ponerFormula(int filaInicio, int filaFin, int col) {
        Row filaHoja = hoja.getRow(filaFin);
        // poner el total
        CellStyle estilo = estiloFormula();
        float puntos = hoja.getRow(7).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);

//        int i = 76;
        for (int i = 71; i <= 77; i++) { // letra l

            Cell cell = filaHoja.getCell(col);
            if (cell == null) {
                cell = filaHoja.createCell(col);
                cell.setCellStyle(estilo);
                cell.setCellFormula("sum" + generaRangoFormulaEnFila(filaInicio, filaFin, i));
            } else {
                cell.setCellStyle(estilo);
                cell.setCellFormula("sum" + generaRangoFormulaEnFila(filaInicio, filaFin, i));
            }
            col++;
//            letra++;
        }

    }

    private void ponerDoble(double valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);

        Cell cell = filaHoja.getCell(col);
        float puntos = hoja.getRow(5).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloNumerico);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloNumerico);
            cell.setCellValue(valor);
        }
    }

    private void ponerFecha(Date valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloFecha);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloFecha);
            cell.setCellValue(valor);
        }
    }

    private void ponerInteger(int valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        if (filaHoja == null) {
            filaHoja = hoja.createRow(fila);
        }
        Cell cell = filaHoja.getCell(col);
        float puntos = hoja.getRow(5).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloTipoRol());
            cell.setCellValue(valor);

        } else {
            cell.setCellStyle(estiloTipoRol());
            cell.setCellValue(valor);
        }
    }

    private void ponerIntegerTotal(int valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        float puntos = hoja.getRow(7).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellValue(valor);
        } else {
            cell.setCellValue(valor);
        }
    }

    public void llenar(List<Empleados> listaRol, int mes, int anio,
            Codigos nom) {
//        Row filaMes = hoja.getRow(4);
//        Cell celdaMes = filaMes.getCell(1);
//        celdaMes.setCellValue("ROL DE PAGOS " + nombreMes(mes) + " - " + String.valueOf(anio));
        int fila = 4;
        int empleados = 0;
        int empleadosTotal = 0;
        int filaInicio = 8;
        String tipoRol = "";
        for (Empleados e : listaRol) {

            int i = 0;

            if (e.getCargoactual() != null) {
                ponerInteger(++empleados, fila, i++);
                ponerString(e.toString(), fila, i++);
                ponerString(e.getCargoactual().getCargo().getNombre(), fila, i++);
                String regimen = "";
                if (e.getCargoactual().getTipocontrato() != null) {
                    regimen = e.getCargoactual().getTipocontrato().getRegimen();
                }
                ponerString(regimen, fila, i++);
                ponerString(e.getPartidaindividual(), fila, i++);
                ponerString(e.getCargoactual().getCargo().getNivel().getNombre(), fila, i++);
//                ponerString(e.getCargoactual().getCodigo(), fila, i++);
                ponerDoble(e.getRmu(), fila, i++);//g
                ponerDoble(e.getRmu() * 12, fila, i++);//h
                ponerDoble((e.getRmu() + e.getTotalIngresos()) / 12, fila, i++);//i
                ponerDoble(e.getTotalEgresos(), fila, i++);//j
                ponerDoble(e.getTotalIngresos(), fila, i++);//k
                ponerDoble(e.getEncargo() + e.getSubrogacion(), fila, i++);//l
                double suma = (e.getRmu() + e.getTotalIngresos()) / 12 + e.getEncargo() + e.getSubrogacion() + e.getTotalEgresos() + e.getTotalIngresos();
                ponerDoble(suma, fila, i++);//l
                fila++;
            }

        }

        // poner titulos para probar
//        fila += 1;
        int i = 5;
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila, //last row  (0-based)
                        0, //first column (0-based)
                        5 //last column  (0-based)
                ));
        ponerCelda(fila, 0, "TOTAL DE REMUNERACIONES UNIFICADAS", estiloTexto);
        ponerFormula(5, fila, ++i);
        fila++;
        int filaInicial = fila;
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        0, //first column (0-based)
                        5 //last column  (0-based)
                ));
        ponerCelda(fila, 0, "FECHA ACTUALIZACIÓN DE LA INFORMACIÓN:", estiloTextoIzquierda);
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        0, //first column (0-based)
                        5 //last column  (0-based)
                ));
        ponerCelda(++fila, 0, "PERIODICIDAD DE ACTUALIZACIÓN DE LA INFORMACIÓN:", estiloTextoIzquierda);
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        0, //first column (0-based)
                        5 //last column  (0-based)
                ));
        ponerCelda(++fila, 0, "UNIDAD POSEEDORA DE LA INFORMACION - LITERAL c):", estiloTextoIzquierda);
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        0, //first column (0-based)
                       5 //last column  (0-based)
                ));
        ponerCelda(++fila, 0, "RESPONSABLE DE LA UNIDAD POSEEDORA DE LA INFORMACIÓN DEL LITERAL c):", estiloTextoIzquierda);
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        0, //first column (0-based)
                        5 //last column  (0-based)
                ));
        ponerCelda(++fila, 0, "CORREO ELECTRÓNICO DEL O LA RESPONSABLE DE LA UNIDAD POSEEDORA DE LA INFORMACIÓN:", estiloTextoIzquierda);
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        0, //first column (0-based)
                        5 //last column  (0-based)
                ));
        // la segunda parte
        fila = filaInicial;
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        6, //first column (0-based)
                        12 //last column  (0-based)
                ));
        ponerFecha(new Date(),fila, 6 );
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        6, //first column (0-based)
                        12 //last column  (0-based)
                ));
        ponerCelda(++fila, 6,"MENSUAL" , estiloTipoFirmas());
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        6, //first column (0-based)
                        12 //last column  (0-based)
                ));
        ponerCelda(++fila, 6,nom.getNombre(), estiloTipoFirmas());
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        6, //first column (0-based)
                        12 //last column  (0-based)
                ));
        ponerCelda(++fila, 6, nom.getDescripcion(), estiloTipoFirmas());
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        6, //first column (0-based)
                        12 //last column  (0-based)
                ));
        ponerCelda(++fila, 6, nom.getParametros(), estiloTipoFirmas());
        hoja.addMergedRegion(
                new CellRangeAddress(
                        fila, //first row (0-based)
                        fila , //last row  (0-based)
                        6, //first column (0-based)
                        12 //last column  (0-based)
                ));
    }

    public double valorIngresoSobre(Empleados e, String concepto) {
        if (e.getId() != null) {
            String[] conceptos = e.getListaIngresos().split("#");
            int i = 0;
//            String concepto = c.getCodigo();
            for (String c1 : conceptos) {
                String[] claveValor = c1.split("=");
                if (claveValor[0].equals(concepto)) {

                    return Double.parseDouble(claveValor[1]);
                }
            }
            conceptos = e.getListaEgresos().split("#");
            for (String c1 : conceptos) {
                String[] claveValor = c1.split("=");
                if (claveValor[0].equals(concepto)) {

                    return Double.parseDouble(claveValor[1]);
                }
            }
            conceptos = e.getListaProviciones().split("#");
            for (String c1 : conceptos) {
                String[] claveValor = c1.split("=");
                if (claveValor[0].equals(concepto)) {

                    return Double.parseDouble(claveValor[1]);
                }
            }
        }
        return 0;
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
