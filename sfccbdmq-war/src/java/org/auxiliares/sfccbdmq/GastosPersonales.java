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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Empleados;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author luis
 */
public class GastosPersonales {

    private final File temp;
    private final Workbook documento;
    private final Sheet hoja;
    private final FileOutputStream fichero;
    private byte[] archivo;
    private final CellStyle estiloTexto;

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    public GastosPersonales() throws IOException, DocumentException {
//        String filename = "Formulario103.xls";
        String filename = FacesContext.getCurrentInstance().getExternalContext().getRealPath("comprobantes/GastosPersonales.xls");
        temp = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xls");
        FileInputStream fis = null;
        fis = new FileInputStream(filename);
        documento = new HSSFWorkbook(fis);
        hoja = documento.getSheetAt(0);
        fichero = new FileOutputStream(temp);
        estiloTexto = documento.createCellStyle();
    }

    public void llenar(List<Cabeceraempleados> list, Configuracion conf) {
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaS = fecha.format(new Date());
        Empleados e = list.get(0).getEmpleado();
        //Año
        ponerCent((fechaS.substring(9, 10)), 6, 10);
        ponerCent((fechaS.substring(8, 9)), 6, 9);
        ponerCent((fechaS.substring(7, 8)), 6, 8);
        ponerCent((fechaS.substring(6, 7)), 6, 7);

        ponerCent(("QUITO"), 7, 22);
        //Fecha
        ponerCent((fechaS.substring(9, 10)), 7, 29);
        ponerCent((fechaS.substring(8, 9)), 7, 28);
        ponerCent((fechaS.substring(7, 8)), 7, 27);
        ponerCent((fechaS.substring(6, 7)), 7, 26);
        ponerCent((fechaS.substring(4, 5)), 7, 31);
        ponerCent((fechaS.substring(3, 4)), 7, 30);
        ponerCent((fechaS.substring(1, 2)), 7, 33);
        ponerCent((fechaS.substring(0, 1)), 7, 32);
        //Informacion
        ponerCent((e.getEntidad().getPin()), 11, 2);
        ponerIzq((e.getEntidad().toString()), 11, 16);
        //lista
        for (Cabeceraempleados ce : list) {
            if (ce.getCabecera().getTexto().equals("103")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 13, 24);
            }
            if (ce.getCabecera().getTexto().equals("104")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 14, 24);
            }
            if (ce.getCabecera().getTexto().equals("106")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 17, 24);
            }
            if (ce.getCabecera().getTexto().equals("107")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 18, 24);
            }
            if (ce.getCabecera().getTexto().equals("108")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 19, 24);
            }
            if (ce.getCabecera().getTexto().equals("109")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 20, 24);
            }
            if (ce.getCabecera().getTexto().equals("110")) {
                if (ce.getValornumerico() == null) {
                    ce.setValornumerico(BigDecimal.ZERO);
                }
                ponerDoble((ce.getValornumerico().doubleValue()), 21, 24);
            }
        }
        String ruc = conf.getRuc();
        ponerCent((ruc.substring(0, 1)), 27, 2);
        ponerCent((ruc.substring(1, 2)), 27, 3);
        ponerCent((ruc.substring(2, 3)), 27, 4);
        ponerCent((ruc.substring(3, 4)), 27, 5);
        ponerCent((ruc.substring(4, 5)), 27, 6);
        ponerCent((ruc.substring(5, 6)), 27, 7);
        ponerCent((ruc.substring(6, 7)), 27, 8);
        ponerCent((ruc.substring(7, 8)), 27, 9);
        ponerCent((ruc.substring(8, 9)), 27, 10);
        ponerCent((ruc.substring(9, 10)), 27, 11);
        ponerCent((ruc.substring(10, 11)), 27, 12);
        ponerCent((ruc.substring(11, 12)), 27, 13);
        ponerCent((ruc.substring(12, 13)), 27, 14);

        //nombre
        String nombre = conf.getNombre();
        ponerIzq((nombre.toUpperCase()), 27, 16);
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

    private void ponerDoble(double valor, int fila, int col) {
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

    private void ponerIzq(String valor, int fila, int col) {
        CellStyle estilo = documento.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_LEFT);
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

    private void ponerCent(String valor, int fila, int col) {
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

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

}
