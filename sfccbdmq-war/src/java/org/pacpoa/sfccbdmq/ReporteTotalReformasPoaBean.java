/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.errores.sfccbdmq.ConsultarException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Organigrama;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "totalReformasPoa")
@ViewScoped
public class ReporteTotalReformasPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoaBean;

    private Integer anio;
    private Date vigente;
    private Date desde;
    private Date hasta;

    private List<Organigrama> listaDirecciones;
    private Formulario formulario = new Formulario();

    @EJB
    private CabecerareformaspoaFacade ejbCabecerareformaspoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;

    public ReporteTotalReformasPoaBean() {
    }

    @PostConstruct
    private void activar() {

        List<Codigos> configuracion = codigosBean.traerCodigoMaestro("CONF");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Codigos c : configuracion) {
            try {
                if (c.getCodigo().equals("PVGT")) {
                    vigente = sdf.parse(c.getParametros());
                }
                if (c.getCodigo().equals("PINI")) {
                    desde = sdf.parse(c.getParametros());
                }
                if (c.getCodigo().equals("PFIN")) {
                    hasta = sdf.parse(c.getParametros());
                }
            } catch (ParseException ex) {
                Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
        anio = ca.get(Calendar.YEAR);
    }

    public String buscar() {
        if (anio == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(vigente);
            anio = c.get(Calendar.YEAR);
        }

        listaDirecciones = consultasPoaBean.traerDirecciones();
        return null;
    }

    public List<Integer> getMeses() {
        List<Integer> lista = new LinkedList<>();
        lista.add(1);
        lista.add(2);
        lista.add(3);
        lista.add(4);
        lista.add(5);
        lista.add(6);
        lista.add(7);
        lista.add(8);
        lista.add(9);
        lista.add(10);
        lista.add(11);
        lista.add(12);
        return lista;
    }

    public String getNombreMes(Integer mes) {
        switch (mes) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
            default:
                return "";
        }
    }

    public Integer traerTotalReformas(Integer mes, String direccion) {
        Map parametros = new HashMap();
        String where = " o.definitivo = true";

        Calendar c = Calendar.getInstance();
        c.setTime(vigente);
        c.set(Calendar.YEAR, anio);
        Date d;
        Date h;

        if (mes != null && mes > 0) {
            c.set(Calendar.MONTH, mes - 1);
            d = c.getTime();
            c.set(Calendar.MONTH, mes);
            h = c.getTime();
        } else {
            c.set(Calendar.MONTH, 0);
            d = c.getTime();
            c.set(Calendar.MONTH, 11);
            h = c.getTime();
        }
        where += " and o.fecha between :desde and :hasta";
        parametros.put("desde", d);
        parametros.put("hasta", h);

        if (!direccion.isEmpty()) {
            where += " and o.direccion=:direccion";
            parametros.put("direccion", direccion);
        }

        parametros.put("where", where);

        try {
            return ejbCabecerareformaspoa.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Integer traerTotalAsignaciones(Integer mes, String direccion) {
        Map parametros = new HashMap();
        String where = " o.cabecera.definitivo = true";

        Calendar c = Calendar.getInstance();
        c.setTime(vigente);
        c.set(Calendar.YEAR, anio);
        Date d;
        Date h;

        if (mes != null && mes > 0) {
            c.set(Calendar.MONTH, mes - 1);
            d = c.getTime();
            c.set(Calendar.MONTH, mes);
            h = c.getTime();
        } else {
            c.set(Calendar.MONTH, 0);
            d = c.getTime();
            c.set(Calendar.MONTH, 11);
            h = c.getTime();
        }
        where += " and o.cabecera.fecha between :desde and :hasta";
        parametros.put("desde", d);
        parametros.put("hasta", h);

        if (!direccion.isEmpty()) {
            where += " and o.asignacion.proyecto.direccion=:direccion";
            parametros.put("direccion", direccion);
        }

        parametros.put("where", where);

        try {
            return ejbReformaspoa.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the listaDirecciones
     */
    public List<Organigrama> getListaDirecciones() {
        return listaDirecciones;
    }

    /**
     * @param listaDirecciones the listaDirecciones to set
     */
    public void setListaDirecciones(List<Organigrama> listaDirecciones) {
        this.listaDirecciones = listaDirecciones;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    /**
     * @return the vigente
     */
    public Date getVigente() {
        return vigente;
    }

    /**
     * @param vigente the vigente to set
     */
    public void setVigente(Date vigente) {
        this.vigente = vigente;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    /**
     * @return the consultasPoaBean
     */
    public ConsultasPoaBean getConsultasPoaBean() {
        return consultasPoaBean;
    }

    /**
     * @param consultasPoaBean the consultasPoaBean to set
     */
    public void setConsultasPoaBean(ConsultasPoaBean consultasPoaBean) {
        this.consultasPoaBean = consultasPoaBean;
    }

    /**
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }
}
