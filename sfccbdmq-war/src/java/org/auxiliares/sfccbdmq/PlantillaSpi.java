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
import java.util.Date;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.entidades.sfccbdmq.Obligaciones;

/**
 *
 * @author luis
 */
public class PlantillaSpi {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloNumerico;
    private final CellStyle estiloFecha;
    private final CellStyle estiloTexto;
    private final CellStyle estiloAmarillo;
    private CellStyle estiloAmarilloNumerico;
    private CellStyle estiloAmarilloFecha;

    public PlantillaSpi() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String directorio = System.getProperty("java.io.tmpdir");
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath( "comprobantes/spi.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        Font cellFont = documento.createFont();
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fichero = new FileOutputStream(temp);
        estiloNumerico = documento.createCellStyle();
        estiloFecha = documento.createCellStyle();
        estiloTexto = documento.createCellStyle();
        estiloAmarillo = documento.createCellStyle();
        estiloAmarilloNumerico = documento.createCellStyle();
        estiloAmarilloFecha = documento.createCellStyle();
        estiloNumerico.setDataFormat(documento.createDataFormat().getFormat("###,##0.00"));
        estiloFecha.setDataFormat(documento.createDataFormat().getFormat("dd/MM/yyyy"));
        estiloAmarillo.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        estiloAmarillo.setFillPattern(CellStyle.SOLID_FOREGROUND);
        estiloAmarillo.setFont(cellFont);
        estiloAmarilloNumerico.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        estiloAmarilloNumerico.setFillPattern(CellStyle.SOLID_FOREGROUND);
        estiloAmarilloNumerico.setDataFormat(documento.createDataFormat().getFormat("###,##0.00"));
        estiloAmarilloNumerico.setFont(cellFont);
        estiloAmarilloFecha.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        estiloAmarilloFecha.setFillPattern(CellStyle.SOLID_FOREGROUND);
        estiloAmarilloFecha.setDataFormat(documento.createDataFormat().getFormat("dd/MM/yyyy"));
        estiloAmarilloFecha.setFont(cellFont);
    }

    private void ponerTiulo(int fila, Obligaciones obligacion) {
        DecimalFormat df = new DecimalFormat("0000000000");
        CellStyle estilo = estiloTitulo();
        Row filaHoja = hoja.getRow(fila);
        ponerCeldaStr(fila, 1, obligacion.getProveedor().getEmpresa().toString());
        ponerFecha(obligacion.getFechaemision(), fila, 5);
        fila++;
        ponerCeldaStr(fila, 1, obligacion.getProveedor().getEmpresa().getRuc());
        ponerCeldaStr(fila, 5, obligacion.getTipodocumento().getNombre());
        fila++;
        ponerCeldaStr(fila, 1, obligacion.getProveedor().getDireccion());
        ponerCeldaStr(fila, 5, obligacion.getEstablecimiento() + "-" + obligacion.getPuntoemision() + "-" + df.format(obligacion.getDocumento()));
    }

    private void ponerCelda(int fila, int col, String valor, CellStyle estilo) {
        Row filaHoja = hoja.getRow(fila);

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

    private void ponerCeldaStr(int fila, int col, String valor) {
        Row filaHoja = hoja.getRow(fila);

        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellValue(valor);
        } else {
            cell.setCellValue(valor);
        }
    }

    public CellStyle estiloTitulo() {
        Cell cell = hoja.getRow(6).getCell(0);
        return cell.getCellStyle();
    }

    public CellStyle estiloTipoRol() {
        Cell cell = hoja.getRow(7).getCell(1);
        return cell.getCellStyle();
    }

    public CellStyle estiloTipoFirmas() {
        Cell cell = hoja.getRow(7).getCell(1);
        CellStyle estilo = documento.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        Font cellFont = documento.createFont();
        cellFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        estilo.setFont(cellFont);
        return estilo;
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

    private void ponerStringTotal(String valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        Cell cell = filaHoja.getCell(col);
        float puntos = hoja.getRow(7).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloAmarillo);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloAmarillo);
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

    private void ponerFormula(int filaInicio, int filaFin) {
        Row filaHoja = hoja.getRow(filaFin);
        // poner el total
        int col = 11;
        CellStyle estilo = estiloTipoRol();
        float puntos = hoja.getRow(7).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        estilo.setDataFormat(documento.createDataFormat().getFormat("###,##0.00"));
        for (int i = 76; i <= 98; i++) { // letra l

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
        if (filaHoja == null) {
            filaHoja = hoja.createRow(fila);
        }
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloNumerico);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloNumerico);
            cell.setCellValue(valor);
        }
    }

    private void ponerDobleTotal(double valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        float puntos = hoja.getRow(7).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloAmarilloNumerico);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloAmarilloNumerico);
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

    private void ponerFechaTotal(Date valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        float puntos = hoja.getRow(7).getHeightInPoints();
        filaHoja.setHeightInPoints(puntos);
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellStyle(estiloAmarilloFecha);
            cell.setCellValue(valor);
        } else {
            cell.setCellStyle(estiloAmarilloFecha);
            cell.setCellValue(valor);
        }
    }

    private void ponerInteger(int valor, int fila, int col) {
        Row filaHoja = hoja.getRow(fila);
        Cell cell = filaHoja.getCell(col);
        if (cell == null) {
            cell = filaHoja.createCell(col);
            cell.setCellValue(valor);
        } else {
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

    private String nombreMes(int mes) {
        String retorno = "";
        switch (mes) {
            case 1:
                retorno = "ENERO";
                break;
            case 2:
                retorno = "FEBERERO";
                break;
            case 3:
                retorno = "MARZO";
                break;
            case 4:
                retorno = "ABRIL";
                break;
            case 5:
                retorno = "MAYO";
                break;
            case 6:
                retorno = "JUNIO";
                break;
            case 7:
                retorno = "JULIO";
                break;
            case 8:
                retorno = "AGOSTO";
                break;
            case 9:
                retorno = "SEPTIEMBRE";
                break;
            case 10:
                retorno = "OCTUBRE";
                break;
            case 11:
                retorno = "NOVIEMBRE";
                break;
            case 12:
                retorno = "DICIEMBRE";
                break;
            default:
                break;
        }
        return retorno;
    }

    public void llenar(List<AuxiliarSpi> renglones) {
        int fila = 6;
        for (AuxiliarSpi r : renglones) {
            ponerString(r.getCedula(), fila, 0);
            ponerInteger(r.getReferencia(), fila, 1);
            ponerString(r.getNombre(), fila, 2);
            ponerString(r.getBanco(), fila, 3);
            ponerString(r.getCuenta(), fila, 4);
            ponerString(r.getTipocuenta(), fila, 5);
            ponerDoble(r.getValorDoble(), fila, 6);
            ponerString(r.getConcepto(), fila, 7);
            ponerString(r.getDetalle(), fila, 8);
            fila++;
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
