/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.auxiliares.sfccbdmq.MensajesErrores;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Obligaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "obligacionesCompromisoSfccbdmq")
@ViewScoped
public class ObligacionesCompromisoBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ObligacionesCompromisoBean() {
        Calendar c = Calendar.getInstance();

    }

    private List<Obligaciones> obligaciones;
    private Compromisos compromiso;
    private int estado;
    @EJB
    private ObligacionesFacade ejbObligaciones;

    public String buscar() {
        if (compromiso == null) {
            return null;
        }
        Map Parametros = new HashMap();
        Parametros.put(";where", "o.estado=:estado and o.compromiso=:compromiso");
        Parametros.put("estado", estado);
        Parametros.put("compromiso", compromiso);
        try {
            obligaciones=ejbObligaciones.encontarParametros(Parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesCompromisoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the obligaciones
     */
    public List<Obligaciones> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Obligaciones> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the estado
     */
    public int getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(int estado) {
        this.estado = estado;
    }

}
