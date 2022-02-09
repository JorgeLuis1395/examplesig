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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Valescajas;
import org.personal.sfccbdmq.ValesCajaBean;

/**
 *
 * @author luis
 */
public class AperReposLiquiCaja {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloTexto;

    public AperReposLiquiCaja() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/Apertura,Reposicion,LiuidacionCaja.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
        estiloTexto = documento.createCellStyle();
    }
//1 => apertura
//2 => reposicion
//3 => liquidacion

    public void llenar(Cajas r, int sol, List<Valescajas> lista,double valorDep) {
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaS = "Quito, " + fecha.format(r.getFecha());
        DecimalFormat df = new DecimalFormat("###,##0.00");
        if (sol == 1) {
            ponerDoble((r.getNumeroapertura().toString()), 6, 5);
        }
        if (sol == 2) {
            ponerDoble((r.getNumeroreposicion().toString()), 6, 5);
        }
        if (sol == 3) {
            ponerDoble((r.getNumeroliquidacion().toString()), 6, 5);
        }
        ponerString((fechaS), 8, 1);
        ponerString((r.getEmpleado().toString()), 9, 1);
        ponerString((r.getDepartamento().toString()), 10, 1);
        if (sol == 1) {
            ponerCentro(("X"), 13, 1);
            ponerDoble((df.format(r.getValor())), 13, 5);
        }
        double total = 0;
        if (sol == 2) {
            for (Valescajas vc : lista) {
                total += vc.getValor().doubleValue();
            }
            ponerCentro(("X"), 14, 1);
            ponerDoble((df.format(total)), 14, 5);
        }
        if (sol == 3) {
            ponerCentro(("X"), 15, 1);
            ponerDoble((df.format(valorDep)), 15, 5);
        }
        if (sol == 2) {
            //llenar lista de reembolso
            int fila = 19;
            for (Valescajas vc : lista) {
                int i = 0;
                ponerString((vc.getTipodocumento().getNombre()), fila, i++);
                ponerString((vc.getNumero().toString()), fila, i++);
                ponerString((fecha.format(vc.getFecha())), fila, i++);
                ponerString((vc.getConcepto()), fila, i++);
                ponerString((vc.getProveedor().getEmpresa().toString()), fila, i++);
                ponerDoble((df.format(vc.getValor())), fila, i++);
                fila++;
            }
            ponerDoble((df.format(total)), 34, 5);
        }

        ponerString(("OBSERVACIONES: " +r.getObservaciones()), 36, 0);
        ponerString(("Nombre: "+r.getEmpleado().toString()), 42, 0);
        ponerString(("cc: "+r.getEmpleado().getEntidad().getPin()), 43, 0);
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

    private void ponerCentro(String valor, int fila, int col) {
        CellStyle estilo = documento.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
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
