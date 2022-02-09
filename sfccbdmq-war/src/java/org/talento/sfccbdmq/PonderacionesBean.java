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
import java.math.BigDecimal;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.NivelesgestionFacade;
import org.beans.sfccbdmq.PondnivelgestionFacade;
import org.entidades.sfccbdmq.Aspectos;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Pondnivelgestion;
import org.entidades.sfccbdmq.Zonasevaluacion;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "ponderaciones")
@ViewScoped
public class PonderacionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private Formulario formularioAspectos = new Formulario();
    private Formulario formulario = new Formulario();
    private List<Pondnivelgestion> listaPonderaciones;
    private List<Pondnivelgestion> listaPonderacionesValidacion;
    private Pondnivelgestion ponderacion;
    private Aspectos aspecto;

    @EJB
    private PondnivelgestionFacade ejbPonderaciones;
    @EJB
    private NivelesgestionFacade ejbNivelesGestion;
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
    public PonderacionesBean() {
    }

    public String ponderaciones(Aspectos aspecto) {
        this.aspecto = aspecto;
        buscar();
        formularioAspectos.insertar();
        return null;
    }

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo = true and o.aspecto=:aspecto");
        parametros.put("aspecto", aspecto);
        parametros.put(";orden", "o.nivelgestion.nombre");
        try {
            listaPonderaciones = ejbPonderaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboNivelesGestion() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true");
            List<Nivelesgestion> lng = new LinkedList<>();
            for (Nivelesgestion ng : ejbNivelesGestion.encontarParametros(parametros)) {
                parametros = new HashMap();
                parametros.put(";where", " o.nivelgestion=:nivelgestion and o.aspecto=:aspecto and o.activo=true");
                parametros.put("nivelgestion", ng);
                parametros.put("aspecto", aspecto);
                if (ejbPonderaciones.contar(parametros) == 0) {
                    lng.add(ng);
                }
            }
            return Combos.getSelectItems(lng, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        ponderacion = new Pondnivelgestion();
        ponderacion.setAspecto(aspecto);
        ponderacion.setActivo(Boolean.TRUE);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        ponderacion = listaPonderaciones.get(formulario.getFila().getRowIndex());
        formulario.editar();
        buscarPonderacionesTodas();
        return null;
    }

    public String borrar() {
        ponderacion = listaPonderaciones.get(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }

    private void buscarPonderacionesTodas() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo=true and o.aspecto.zonaevaluacion=:zona and o.nivelgestion=:nivelgestion");
        parametros.put("zona", ponderacion.getAspecto().getZonaevaluacion());
        parametros.put("nivelgestion", ponderacion.getNivelgestion());
        parametros.put(";orden", "o.id");
        try {
            listaPonderacionesValidacion = ejbPonderaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean ponderacionMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Pondnivelgestion p : listaPonderacionesValidacion) {
            suma = suma.add(p.getPonderacion());
        }
        int ref;
        BigDecimal tope = BigDecimal.valueOf(100.00);
        ref = suma.compareTo(tope);
        if (ref >= 1) {
            MensajesErrores.advertencia("No puede exceder " + tope);
            return true;
        }
        return false;
    }

    private boolean validar() {
        if (ponderacion.getPonderacion() == null || ponderacion.getPonderacion().compareTo(BigDecimal.ZERO) < 1) {
            MensajesErrores.advertencia("La ponderación debe ser un valor positivo");
            return true;
        }
        if (ponderacion.getNivelgestion() == null) {
            MensajesErrores.advertencia("Seleccione un Nivel de Gestión");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        buscarPonderacionesTodas();
        listaPonderacionesValidacion.add(ponderacion);
        if (ponderacionMaxima()) {
            buscarPonderacionesTodas();
            return null;
        }
        try {
            ejbPonderaciones.create(ponderacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        int indice = 0;
        for (Pondnivelgestion pg : listaPonderacionesValidacion) {
            if (ponderacion.getId().equals(pg.getId())) {
                listaPonderacionesValidacion.set(indice, ponderacion);
                break;
            }
            indice++;
        }
        if (ponderacionMaxima()) {
            buscarPonderacionesTodas();
            return null;
        }
        try {
            ejbPonderaciones.edit(ponderacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String eliminar() {
        ponderacion.setActivo(Boolean.FALSE);
        try {
            ejbPonderaciones.edit(ponderacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String salir() {
        formularioAspectos.cancelar();
        buscar();
        return null;
    }

    public BigDecimal getPonderacion(Aspectos a, Nivelesgestion n) {
        Map parametros = new HashMap();
        parametros.put(";where", " o.aspecto=:aspecto and o.nivelgestion=:nivel and o.activo=true");
        parametros.put("aspecto", a);
        parametros.put("nivel", n);
        try {
            List<Pondnivelgestion> lp = ejbPonderaciones.encontarParametros(parametros);
            if (!lp.isEmpty()) {
                return lp.get(0).getPonderacion();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(0);
    }

    public BigDecimal getPonderacionTotal(Nivelesgestion n, Zonasevaluacion z) {
        BigDecimal retorno = new BigDecimal(0);
        Map parametros = new HashMap();
        parametros.put(";where", "o.nivelgestion=:nivel and o.aspecto.zonaevaluacion=:z and o.activo=true and o.aspecto =true");
        parametros.put("nivel", n);
        parametros.put("z", z);
        try {
            for (Pondnivelgestion pg : ejbPonderaciones.encontarParametros(parametros)) {
                retorno = retorno.add(pg.getPonderacion());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PonderacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    /**
     * @return the formularioAspectos
     */
    public Formulario getFormularioAspectos() {
        return formularioAspectos;
    }

    /**
     * @param formularioAspectos the formularioAspectos to set
     */
    public void setFormularioAspectos(Formulario formularioAspectos) {
        this.formularioAspectos = formularioAspectos;
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
     * @return the listaPonderaciones
     */
    public List<Pondnivelgestion> getListaPonderaciones() {
        return listaPonderaciones;
    }

    /**
     * @param listaPonderaciones the listaPonderaciones to set
     */
    public void setListaPonderaciones(List<Pondnivelgestion> listaPonderaciones) {
        this.listaPonderaciones = listaPonderaciones;
    }

    /**
     * @return the ponderacion
     */
    public Pondnivelgestion getPonderacion() {
        return ponderacion;
    }

    /**
     * @param ponderacion the ponderacion to set
     */
    public void setPonderacion(Pondnivelgestion ponderacion) {
        this.ponderacion = ponderacion;
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

}
