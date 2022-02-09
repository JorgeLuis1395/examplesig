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
import org.beans.sfccbdmq.EquivalenciasevFacade;
import org.entidades.sfccbdmq.Equivalenciasev;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "equivalencias")
@ViewScoped
public class EquivalenciasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of EquivalenciasBean
     */
    private List<Equivalenciasev> listaEquivalencias;
    private Equivalenciasev equivalencia;
    private Formulario formulario = new Formulario();

    @EJB
    private EquivalenciasevFacade ejbEquivalencias;

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

    public EquivalenciasBean() {
    }

    public String buscar() {
       
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true ");
            listaEquivalencias = ejbEquivalencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EquivalenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        
        equivalencia = new Equivalenciasev();
        equivalencia.setActivo(Boolean.TRUE);
        equivalencia.setNotamaxima(BigDecimal.ZERO);
        equivalencia.setNotaminima(BigDecimal.ZERO);
        equivalencia.setPuntajefijo(BigDecimal.ZERO);
        formulario.insertar();
        return null;
    }

    private boolean validar() {

        if ((equivalencia.getDescripcion() == null || equivalencia.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (equivalencia.getNotaminima() == null) {
            MensajesErrores.advertencia("Nota mínima es necesaria");
            return true;
        }
        if (equivalencia.getNotamaxima() == null) {
            MensajesErrores.advertencia("Nota máxima es necesario");
            return true;
        }
        if (equivalencia.getPuntajefijo()== null) {
            MensajesErrores.advertencia("Puntaje fijo es necesario");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            ejbEquivalencias.create(equivalencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EquivalenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }

    public String modificar() {
        
        equivalencia = listaEquivalencias.get(getFormulario().getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String grabar() {
        
        if (validar()) {
            return null;
        }
        try {
            ejbEquivalencias.edit(equivalencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EquivalenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String eliminar() {
        
        equivalencia = listaEquivalencias.get(getFormulario().getFila().getRowIndex());

        formulario.eliminar();
        return null;
    }

    public String borrar() {
        
        equivalencia.setActivo(Boolean.FALSE);
        try {
            ejbEquivalencias.edit(equivalencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EquivalenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cancelar() {
        getFormulario().cancelar();
        buscar();
        return null;
    }

    
    public Equivalenciasev traer(Integer id) throws ConsultarException {
        return ejbEquivalencias.find(id);
    }

    public SelectItem[] getComboEquivalenciasConducta() {
        List<Equivalenciasev> listaConducta = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.id");
        parametros.put(";where", "o.activo=true and o.grupo.codigo='CND'");
        try {
            listaConducta = ejbEquivalencias.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EquivalenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(listaConducta, false);
    }

    

    /**
     * @return the listaEquivalencias
     */
    public List<Equivalenciasev> getListaEquivalencias() {
        return listaEquivalencias;
    }

    /**
     * @param listaEquivalencias the listaEquivalencias to set
     */
    public void setListaEquivalencias(List<Equivalenciasev> listaEquivalencias) {
        this.listaEquivalencias = listaEquivalencias;
    }

    /**
     * @return the equivalencia
     */
    public Equivalenciasev getEquivalencia() {
        return equivalencia;
    }

    /**
     * @param equivalencia the equivalencia to set
     */
    public void setEquivalencia(Equivalenciasev equivalencia) {
        this.equivalencia = equivalencia;
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

    

}
