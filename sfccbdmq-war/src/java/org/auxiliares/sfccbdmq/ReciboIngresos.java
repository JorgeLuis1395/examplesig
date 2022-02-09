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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.entidades.sfccbdmq.Cabecerafacturas;
import org.entidades.sfccbdmq.Ingresos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author luis
 */
public class ReciboIngresos {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloTexto;

    @EJB
    private KardexbancoFacade ejbKardex;

    public ReciboIngresos() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/RecibodeIngreso.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
        estiloTexto = documento.createCellStyle();
    }

    public void llenarIngreso(Ingresos r) {
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        if (r.getValoraprobar() == null) {
            r.setValoraprobar(BigDecimal.ZERO);
        }
//        ponerDoble((r.getId().toString()), 3, 5);
        String numeroRecibo;
        if (r.getKardexbanco().getNumerorecibo() == null) {
            numeroRecibo = "0";
        } else {
            numeroRecibo = r.getKardexbanco().getNumerorecibo().toString();
        }
        ponerDoble((numeroRecibo), 3, 3);
        ponerDoble((df.format(r.getValoraprobar())), 3, 5);
        ponerString((r.getCliente().getEmpresa().toString()), 4, 1);
        ponerString((r.getCliente().getEmpresa().getRuc()), 4, 5);
        ponerString((ConvertirNumeroALetras.convertNumberToLetter(r.getValoraprobar().doubleValue())), 5, 1);
        ponerDoble((df.format(r.getValoraprobar())), 11, 1);
        ponerString((r.getKardexbanco().getBanco().getNombre()), 11, 3);
        ponerString((r.getKardexbanco().getBanco().getNumerocuenta()), 11, 5);
        ponerString((r.getObservaciones()), 12, 1);
        ponerDoble((fecha.format(r.getFecha())), 15, 5);
    }

    public void llenarCobro(Cabecerafacturas r) {
        double valor = r.getKardexbanco().getValor().doubleValue();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
//        ponerDoble((r.getId().toString()), 3, 5);
        String numeroRecibo;
        if (r.getKardexbanco().getNumerorecibo() == null) {
            numeroRecibo = "0";
        } else {
            numeroRecibo = r.getKardexbanco().getNumerorecibo().toString();
        }
        ponerDoble((numeroRecibo), 3, 3);
        ponerDoble((df.format(valor)), 3, 5);
        ponerString((r.getCliente().getEmpresa().toString()), 4, 1);
        ponerString((r.getCliente().getEmpresa().getRuc()), 4, 5);
        ponerString((ConvertirNumeroALetras.convertNumberToLetter(valor)), 5, 1);
        ponerDoble((df.format(valor)), 11, 1);
        ponerString((r.getKardexbanco().getBanco().getNombre()), 11, 3);
        ponerString((r.getKardexbanco().getBanco().getNumerocuenta()), 11, 5);
        ponerString((r.getObservaciones()), 12, 1);
        ponerDoble((fecha.format(r.getFecha())), 15, 5);
    }

    public void llenarCobroFactura(Cabecerafacturas r,double valor) {

        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        String numeroRecibo;
        if (r.getNrodocumento() == null) {
            numeroRecibo = "0";
        } else {
            numeroRecibo = r.getNrodocumento() + "";
        }
        ponerDoble((numeroRecibo), 2, 3);
        ponerDoble((df.format(valor)), 2, 5);
        ponerString((r.getCliente().getEmpresa().toString()), 3, 1);
        ponerString((r.getCliente().getEmpresa().getRuc()), 3, 5);
        ponerString((ConvertirNumeroALetras.convertNumberToLetter(valor)), 4, 1);
        ponerDoble((df.format(valor)), 10, 1);
//        ponerString((r.getKardexbanco().getBanco().getNombre()), 11, 3);
//        ponerString((r.getKardexbanco().getBanco().getNumerocuenta()), 11, 5);
        ponerString((r.getObservaciones()), 11, 1);
        ponerDoble((fecha.format(r.getFecha())), 14, 5);
    }

    public void llenarKardex(Kardexbanco r) {
        double valor = r.getValor().doubleValue();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("###,##0.00");
        String numeroRecibo;
        String nombre = "";
        if (r.getNumerorecibo() == null) {
            numeroRecibo = "0";
        } else {
            numeroRecibo = r.getNumerorecibo().toString();
        }
        if (r.getBeneficiario() != null) {
            nombre = r.getBeneficiario();
        } else {
            if (r.getProveedor() != null) {
                nombre = r.getProveedor().getEmpresa().toString();
            } else {
                nombre = "";
            }
        }
        ponerDoble((numeroRecibo), 2, 3);
        ponerDoble((df.format(valor)), 2, 5);
        ponerString((nombre), 3, 1);
        ponerString((r.getAuxiliar()), 3, 5);
        ponerString((ConvertirNumeroALetras.convertNumberToLetter(valor)), 4, 1);
        ponerDoble((df.format(valor)), 10, 1);
        ponerString((r.getBanco().getNombre()), 10, 3);
        ponerString((r.getBanco().getNumerocuenta()), 10, 5);
        ponerString((r.getObservaciones()), 11, 1);
        ponerDoble((fecha.format(r.getFechamov())), 14, 5);
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
