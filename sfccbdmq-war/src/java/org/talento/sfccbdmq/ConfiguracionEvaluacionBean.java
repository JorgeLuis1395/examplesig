/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.Date;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.ConfiguracionesevFacade;
import org.entidades.sfccbdmq.Configuracionesev;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "configuracionEvaluacion")
@ViewScoped
public class ConfiguracionEvaluacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{preguntas}")
    private PreguntasBean preguntasBean;

    private Formulario formulario = new Formulario();
    private Configuracionesev configuracion;

    private List<Configuracionesev> listaConfiguraciones;

    @EJB
    private ConfiguracionesevFacade ejbConfiguraciones;

    /**
     * Creates a new instance of ConfiguracionEvaluacionBean
     */
    public ConfiguracionEvaluacionBean() {
    }

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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
    /* @return the parametrosSeguridad
     */

    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "OficinasVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }
    // fin perfiles

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo = true");
        parametros.put(";orden", " o.fechafin desc");

        try {
            listaConfiguraciones = ejbConfiguraciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConfiguracionEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        configuracion = new Configuracionesev();
        configuracion.setUsuario(parametrosSeguridad.getLogueado().toString());
        configuracion.setActivo(Boolean.TRUE);
        formulario.insertar();
        return null;
    }

    public String modificar() {

        configuracion = listaConfiguraciones.get(formulario.getFila().getRowIndex());
        preguntasBean.setNivelgestion(configuracion.getFormulario().getNivelgestion());
        formulario.editar();
        return null;
    }

    public String borrar() {
        configuracion = listaConfiguraciones.get(formulario.getFila().getRowIndex());
        preguntasBean.setNivelgestion(configuracion.getFormulario().getNivelgestion());
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if (configuracion.getFormulario() == null) {
            MensajesErrores.advertencia("Seleccione el formulario para la evaluación");
            return true;
        }
        if (configuracion.getFechainicio() == null) {
            MensajesErrores.advertencia("Ingrese fecha de inicio para la evaluación");
            return true;
        }
        if (configuracion.getFechafin() == null) {
            MensajesErrores.advertencia("Ingrese fecha de fin para la evaluación");
            return true;
        }
        if (configuracion.getFechainicio().before(new Date())) {
            MensajesErrores.advertencia("La fecha de inicio no puede ser anterior a la de hoy");
            return true;
        }
        if (configuracion.getFechafin().before(configuracion.getFechainicio())) {
            MensajesErrores.advertencia("La fecha de fin no debe ser anterior a la fecha de inicio");
            return true;
        }
        if (configuracion.getDescripcion() == null || configuracion.getDescripcion().isEmpty()) {
            MensajesErrores.advertencia("Descripción de la evaluación es necesaria");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbConfiguraciones.create(configuracion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConfiguracionEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbConfiguraciones.edit(configuracion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConfiguracionEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String eliminar() {
        try {
            configuracion.setActivo(Boolean.FALSE);
            ejbConfiguraciones.edit(configuracion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConfiguracionEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Configuracionesev traer(Integer id) throws ConsultarException {
        return ejbConfiguraciones.find(id);
    }

    public SelectItem[] getComboConfiguraciones() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo = true  and o.fechafin>=:fecha");
        parametros.put("fecha", new Date());
        parametros.put(";orden", " o.fechafin asc");
        try {
            return Combos.getSelectItems(ejbConfiguraciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ConfiguracionEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
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
     * @return the configuracion
     */
    public Configuracionesev getConfiguracion() {
        return configuracion;
    }

    /**
     * @param configuracion the configuracion to set
     */
    public void setConfiguracion(Configuracionesev configuracion) {
        this.configuracion = configuracion;
    }

    /**
     * @return the listaConfiguraciones
     */
    public List<Configuracionesev> getListaConfiguraciones() {
        return listaConfiguraciones;
    }

    /**
     * @param listaConfiguraciones the listaConfiguraciones to set
     */
    public void setListaConfiguraciones(List<Configuracionesev> listaConfiguraciones) {
        this.listaConfiguraciones = listaConfiguraciones;
    }

    /**
     * @return the preguntasBean
     */
    public PreguntasBean getPreguntasBean() {
        return preguntasBean;
    }

    /**
     * @param preguntasBean the preguntasBean to set
     */
    public void setPreguntasBean(PreguntasBean preguntasBean) {
        this.preguntasBean = preguntasBean;
    }

}
