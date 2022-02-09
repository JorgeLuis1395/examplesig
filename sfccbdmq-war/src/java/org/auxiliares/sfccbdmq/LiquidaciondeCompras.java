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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.beans.sfccbdmq.ValescajasFacade;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Valescajas;
import org.entidades.sfccbdmq.Valesfondos;

/**
 *
 * @author luis
 */
public class LiquidaciondeCompras {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloTexto;

    @EJB
    private ValescajasFacade ejbVales;

    public LiquidaciondeCompras() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/LiquidaciondeCompras.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
        estiloTexto = documento.createCellStyle();
    }

    public void llenar(Cajas caj, List<Valescajas> lista) {

        Valescajas r = null;
        if (!lista.isEmpty()) {
            r = lista.get(0);
        }
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        ponerDoble((r.getReposicion().getNumerodocumento().toString()), 5, 6);
        ponerString((r.getCaja().getEmpleado().toString()), 10, 1);
        ponerString((r.getCaja().getEmpleado().getEntidad().getPin()), 11, 1);
        ponerString((fecha.format(r.getCaja().getFecha())), 11, 6);
        ponerString((r.getCaja().getDepartamento().toString()), 12, 1);
        ponerString((""), 12, 6);
        ponerString((r.getCaja().getObservaciones() + " - " + r.getReposicion().getDescripcion()), 15, 1);
        ponerDoble((df.format(r.getValor())), 15, 6);
        int fila = 25;
        for (Valescajas vc : lista) {
            int i = 0;
            String factura = vc.getEstablecimiento() + "-" + vc.getPuntoemision() + "-" + vc.getNumero();
            if (vc.getBaseimponible() == null) {
                vc.setBaseimponible(BigDecimal.ZERO);
            }
            if (vc.getBaseimponiblecero() == null) {
                vc.setBaseimponiblecero(BigDecimal.ZERO);
            }
            if (vc.getIva() == null) {
                vc.setIva(BigDecimal.ZERO);
            }
            String proveedor = "";
            if (vc.getProveedor() == null) {
                proveedor = "";
                if (vc.getProveedor().getEmpresa() == null) {
                    proveedor = "";
                    if (vc.getProveedor().getEmpresa().getRuc() == null) {
                        proveedor = "";
                    }
                }
            }
            ponerString((vc.getProveedor() != null ? (vc.getProveedor().getEmpresa() != null ? vc.getProveedor().getEmpresa().getRuc() : "") : ""), fila, i++);
            ponerString((factura), fila, i++);
            ponerDoble((df.format(vc.getBaseimponiblecero())), fila, i++);
            ponerDoble((df.format(vc.getBaseimponible())), fila, i++);
            ponerDoble((df.format(vc.getIva())), fila, i++);
            ponerString((""), fila, i++);
            ponerDoble((df.format(vc.getValor())), fila, i++);
            fila++;
        }
        double valor = r.getReposicion().getBase().doubleValue() + r.getReposicion().getBase0().doubleValue() + r.getReposicion().getIva().doubleValue();
        ponerDoble((df.format(r.getReposicion().getBase())), 38, 6);
        ponerDoble((df.format(r.getReposicion().getBase0())), 39, 6);
        ponerDoble((df.format(r.getReposicion().getIva())), 40, 6);
        ponerDoble((df.format(valor)), 41, 6);
    }

    public void llenarFondo(List<Valesfondos> lista) {

        Valesfondos r = null;
        if (!lista.isEmpty()) {
            r = lista.get(0);
        }
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        ponerDoble((r.getReposiscion().getNumerodocumento().toString()), 5, 6);
        ponerString((r.getFondo().getEmpleado().toString()), 10, 1);
        ponerString((r.getFondo().getEmpleado().getEntidad().getPin()), 11, 1);
        ponerString((fecha.format(r.getFondo().getFecha())), 11, 6);
        ponerString((r.getFondo().getDepartamento().toString()), 12, 1);
        ponerString((""), 12, 6);
        ponerString((r.getFondo().getObservaciones() + " - " + r.getReposiscion().getDescripcion()), 15, 1);
        ponerDoble((df.format(r.getValor())), 15, 6);
        int fila = 25;
        for (Valesfondos vc : lista) {
            int i = 0;
            if (vc.getBaseimponible() == null) {
                vc.setBaseimponible(BigDecimal.ZERO);
            }
            if (vc.getBaseimponiblecero() == null) {
                vc.setBaseimponiblecero(BigDecimal.ZERO);
            }
            if (vc.getIva() == null) {
                vc.setIva(BigDecimal.ZERO);
            }
            String factura = vc.getEstablecimiento() + "-" + vc.getPuntoemision() + "-" + vc.getNumero();
            ponerString((vc.getProveedor().getEmpresa().getRuc()), fila, i++);
            ponerString((factura), fila, i++);
            ponerDoble((df.format(vc.getBaseimponiblecero())), fila, i++);
            ponerDoble((df.format(vc.getBaseimponible())), fila, i++);
            ponerDoble((df.format(vc.getIva())), fila, i++);
            ponerString((""), fila, i++);
            ponerDoble((df.format(vc.getValor())), fila, i++);
            fila++;
        }
        if (r.getReposiscion().getBase() == null) {
            r.getReposiscion().setBase(BigDecimal.ZERO);
        }
        if (r.getReposiscion().getBase0() == null) {
            r.getReposiscion().setBase0(BigDecimal.ZERO);
        }
        if (r.getReposiscion().getIva() == null) {
            r.getReposiscion().setIva(BigDecimal.ZERO);
        }
        double valor = r.getReposiscion().getBase().doubleValue() + r.getReposiscion().getBase0().doubleValue() + r.getReposiscion().getIva().doubleValue();
        ponerDoble((df.format(r.getReposiscion().getBase())), 38, 6);
        ponerDoble((df.format(r.getReposiscion().getBase0())), 39, 6);
        ponerDoble((df.format(r.getReposiscion().getIva())), 40, 6);
        ponerDoble((df.format(valor)), 41, 6);
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
