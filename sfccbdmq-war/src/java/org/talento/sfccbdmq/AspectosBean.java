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
import org.beans.sfccbdmq.AspectosFacade;
import org.entidades.sfccbdmq.Aspectos;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Perspectivas;
import org.entidades.sfccbdmq.Zonasevaluacion;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "aspectos")
@ViewScoped
public class AspectosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{datosLogueo}")
    private ParametrosSfccbdmqBean seguridadbean;

    private Formulario formulario = new Formulario();
    private Aspectos aspecto;
    private List<Aspectos> listaAspectos;

    private Oficinas area;
    private Cargos cargo;
    private Perspectivas perspectiva;
    private Zonasevaluacion zonaevaluacion;

    @EJB
    private AspectosFacade ejbAspectos;

    public AspectosBean() {
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

    public String crear() {

        if (zonaevaluacion == null) {
            MensajesErrores.advertencia("Seleccione una zona de evaluación");
            return null;
        }

        aspecto = new Aspectos();
        aspecto.setZonaevaluacion(zonaevaluacion);
        aspecto.setActivo(Boolean.TRUE);

        formulario.insertar();
        buscar();
        return null;

    }

    public String buscar() {

        if (zonaevaluacion == null) {
            MensajesErrores.advertencia("Seleccione una zona de evaluación");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.activo=true";
        if (zonaevaluacion != null) {
            where += " and o.zonaevaluacion=:zonaevaluacion";
            parametros.put("zonaevaluacion", zonaevaluacion);
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.zonaevaluacion.zonaevaluacion, o.id");

        try {
            listaAspectos = ejbAspectos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AspectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificar() {

        aspecto = listaAspectos.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {

        aspecto = listaAspectos.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }

    public String borrar() {
        aspecto.setActivo(Boolean.FALSE);

        try {
            ejbAspectos.edit(aspecto, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AspectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    private boolean validar() {
        if (aspecto.getAspecto() == null || aspecto.getAspecto().isEmpty()) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (aspecto.getDescripcion() == null || aspecto.getDescripcion().isEmpty()) {
            MensajesErrores.advertencia("Descripción es mencesari");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            ejbAspectos.create(aspecto, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AspectosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbAspectos.edit(aspecto, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AspectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public SelectItem[] getComboAspectos() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.zonaevaluacion=:cargo and o.activo = true");
            parametros.put("zonaevaluacion", zonaevaluacion);
            List<Aspectos> lp = ejbAspectos.encontarParametros(parametros);
            return Combos.getSelectItems(lp, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(AspectosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Aspectos traer(Integer id) throws ConsultarException {
        return ejbAspectos.find(id);
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
     * @return the aspecto
     */
    public Aspectos getAspecto() {
        return aspecto;
    }

    /**
     * @param aspecto the aspecto to set
     */
    public void setAspecto(Aspectos aspecto) {
        this.aspecto = aspecto;
    }

    /**
     * @return the listaAspectos
     */
    public List<Aspectos> getListaAspectos() {
        return listaAspectos;
    }

    /**
     * @param listaAspectos the listaAspectos to set
     */
    public void setListaAspectos(List<Aspectos> listaAspectos) {
        this.listaAspectos = listaAspectos;
    }

    /**
     * @return the area
     */
    public Oficinas getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(Oficinas area) {
        this.area = area;
    }

    /**
     * @return the cargo
     */
    public Cargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the perspectiva
     */
    public Perspectivas getPerspectiva() {
        return perspectiva;
    }

    /**
     * @param perspectiva the perspectiva to set
     */
    public void setPerspectiva(Perspectivas perspectiva) {
        this.perspectiva = perspectiva;
    }

    /**
     * @return the zonaevaluacion
     */
    public Zonasevaluacion getZonaevaluacion() {
        return zonaevaluacion;
    }

    /**
     * @param zonaevaluacion the zonaevaluacion to set
     */
    public void setZonaevaluacion(Zonasevaluacion zonaevaluacion) {
        this.zonaevaluacion = zonaevaluacion;
    }

}
