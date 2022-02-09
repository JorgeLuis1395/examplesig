/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.entidades.sfccbdmq.Certificacionespoa;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.errores.sfccbdmq.ConsultarException;
import java.io.Serializable;
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
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.entidades.sfccbdmq.Certificaciones;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "reporteCertificacionesPoa")
@ViewScoped
public class ReporteCertificacionesPoaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;

    private double totalCertificaciones;
    private List<Certificacionespoa> certificaciones;
    private Formulario formulario = new Formulario();

    @EJB
    private DetallecertificacionespoaFacade ejbDetalle;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    

    public ReporteCertificacionesPoaBean() {
    }

    @PostConstruct
    private void activar() {

    }

    public String buscar() {

        Map parametros = new HashMap();
        String where = " o.certificacion.impreso=true and o.certificacion.anulado=false";
//        if (proyectosPoaBean.getProyectoSeleccionado() != null) {
//            where += " and o.asignacion.proyecto=:proyecto ";
//            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
//        }
//        if (partidasPoaBean.getPartidaPoa() != null) {
//            where += " and o.asignacion.partida=:partida ";
//            parametros.put("partida", partidasPoaBean.getPartidaPoa());
//        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.certificacion.fecha desc");
        totalCertificaciones = 0;
        try {
            List<Detallecertificacionespoa> dl = ejbDetalle.encontarParametros(parametros);
            certificaciones = new LinkedList<>();
            Certificaciones c = new Certificaciones();
            for (Detallecertificacionespoa d : dl) {
                if (!c.equals(d.getCertificacion())) {
                    certificaciones.add(d.getCertificacion());
                }
                totalCertificaciones += d.getValor().doubleValue();
            }
//            parametros.put(";campo", "o.valor");
//            totalCertificaciones=ejbCertificaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getValor() {
        Certificacionespoa c = certificaciones.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
        parametros.put("certificacion", c);
        parametros.put(";orden", "o.certificaion.fecha desc");
        try {
            parametros.put(";campo", "o.valor");
            return ejbDetalle.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public double getSaldoCertificacion() {
        Certificacionespoa c = certificaciones.get(formulario.getFila().getRowIndex());
        return traerSaldoCert(c);
    }

    public double traerSaldoCert(Certificacionespoa c) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.compromiso.certificacion.pacpoa=:pacpoa");
        parametros.put("pacpoa", c.getId());
        try {
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldo() {
        Certificacionespoa c = certificaciones.get(formulario.getFila().getRowIndex());
        double saldo = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", c);
            double valorCert = ejbDetalle.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.compromiso.certificacion.pacpoa=:pacpoa");
            parametros.put("pacpoa", c.getId());
            double valorComp = ejbDetComp.sumarCampo(parametros).doubleValue();
            saldo = valorCert - valorComp;
        } catch ( ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }
    
    /**
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosBean() {
        return proyectosPoaBean;
    }

    /**
     * @param proyectosPoaBean the proyectosPoaBean to set
     */
    public void setProyectosPoaBean(ProyectosPoaBean proyectosPoaBean) {
        this.proyectosPoaBean = proyectosPoaBean;
    }

    /**
     * @return the totalCertificaciones
     */
    public double getTotalCertificaciones() {
        return totalCertificaciones;
    }

    /**
     * @param totalCertificaciones the totalCertificaciones to set
     */
    public void setTotalCertificaciones(double totalCertificaciones) {
        this.totalCertificaciones = totalCertificaciones;
    }

    /**
     * @return the certificaciones
     */
    public List<Certificacionespoa> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(List<Certificacionespoa> certificaciones) {
        this.certificaciones = certificaciones;
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
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosPoaBean() {
        return proyectosPoaBean;
    }

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasPoaBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
    }

}
