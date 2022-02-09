/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.TrackingcontratoFacade;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Trackingcontrato;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "administradorcontratosSfccbdmq")
@ViewScoped
public class ContratosBean {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of ContratosBean
     */
    public ContratosBean() {
    }
    private Contratos contrato;
    private List<Contratos> listaContratos;
    private List<Trackingcontrato> listaTrackingContratos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private ContratosFacade ejbContratos;
    @EJB
    private TrackingcontratoFacade ejbTrackingcontrato;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{trackingcontratoSfccbdmq}")
    private Trackingcontrato trackingcontratoBean;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteContratoVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
        this.setPerfil(getSeguridadbean().traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getSeguridadbean().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
    }

    public String buscar() {

        Map parametros = new HashMap();
        String where = "o.administrador=:administrador";
        parametros.put("administrador", seguridadbean.getLogueado());
        parametros.put(";where", where);
        try {
            setListaContratos(ejbContratos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public String traerHistorialAdministrador(Contratos contrato) {
        try {
            String observaciones = "";
            Map parametros = new HashMap();
            String where = "o.contrato=:contrato";
            parametros.put("contrato", contrato);
            parametros.put(";where", where);

            listaTrackingContratos = ejbTrackingcontrato.encontarParametros(parametros);
            for (Trackingcontrato t : listaTrackingContratos) {
                if (t != null) {
                    observaciones += t.getAdministradorcontrato() + " - ";
                }
                return observaciones;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ContratosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the listaContratos
     */
    public List<Contratos> getListaContratos() {
        return listaContratos;
    }

    /**
     * @param listaContratos the listaContratos to set
     */
    public void setListaContratos(List<Contratos> listaContratos) {
        this.listaContratos = listaContratos;
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
     * @return the perfil
     */
    public Perfiles getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(Perfiles perfil) {
        this.perfil = perfil;
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
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
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

    /**
     * @return the trackingcontratoBean
     */
    public Trackingcontrato getTrackingcontratoBean() {
        return trackingcontratoBean;
    }

    /**
     * @param trackingcontratoBean the trackingcontratoBean to set
     */
    public void setTrackingcontratoBean(Trackingcontrato trackingcontratoBean) {
        this.trackingcontratoBean = trackingcontratoBean;
    }

    /**
     * @return the listaTrackingContratos
     */
    public List<Trackingcontrato> getListaTrackingContratos() {
        return listaTrackingContratos;
    }

    /**
     * @param listaTrackingContratos the listaTrackingContratos to set
     */
    public void setListaTrackingContratos(List<Trackingcontrato> listaTrackingContratos) {
        this.listaTrackingContratos = listaTrackingContratos;
    }

}
