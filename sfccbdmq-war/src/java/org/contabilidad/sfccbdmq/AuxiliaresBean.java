/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.beans.sfccbdmq.AuxiliaresFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Auxiliares;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "auxiliaresSfccbdmq")
@ViewScoped
public class AuxiliaresBean {

    /**
     * Creates a new instance of AuxiliaresBean
     */
    public AuxiliaresBean() {
    }
    private Auxiliares auxiliar;
    private List<Auxiliares> auxiliares;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private AuxiliaresFacade ejbAuxiliares;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the auxiliar
     */
    public Auxiliares getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(Auxiliares auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the auxiliares
     */
    public List<Auxiliares> getAuxiliares() {
        return auxiliares;
    }

    /**
     * @param auxiliares the auxiliares to set
     */
    public void setAuxiliares(List<Auxiliares> auxiliares) {
        this.auxiliares = auxiliares;
    }

    public String buscar() {
       
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
            auxiliares = ejbAuxiliares.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliaresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       
        formulario.insertar();
        auxiliar = new Auxiliares();
        return null;
    }

    public String modificar(Auxiliares auxiliar) {

        this.auxiliar = auxiliar;
        formulario.editar();

        return null;
    }
    public String eliminar(Auxiliares auxiliar) {

        this.auxiliar = auxiliar;
        formulario.eliminar();

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

    private boolean validar() {
        if (auxiliar.getCodigo()== null) {
            MensajesErrores.advertencia("Es necesario código del Auxiliar");
            return true;
        }
        
        if ((auxiliar.getNombre()== null) || (auxiliar.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de auxiliar");
            return true;
        }
        
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            ejbAuxiliares.create(auxiliar, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliaresBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbAuxiliares.edit(auxiliar, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliaresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbAuxiliares.remove(auxiliar, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliaresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
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

    public Auxiliares traer(Integer id) throws ConsultarException {
        return ejbAuxiliares.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "AuxiliaresVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
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
   
}
