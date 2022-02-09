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
import org.beans.sfccbdmq.TipoasientoFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tipoAsientoSfccbdmq")
@ViewScoped
public class TipoAsientoBean {

    /**
     * Creates a new instance of TipoasientoBean
     */
    public TipoAsientoBean() {
    }
    private Tipoasiento tipo;
    private List<Tipoasiento> tipos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private TipoasientoFacade ejbTipoasiento;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the tipo
     */
    public Tipoasiento getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipoasiento tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the tipos
     */
    public List<Tipoasiento> getTipoasiento() {
        return tipos;
    }

    /**
     * @param tipos the tipos to set
     */
    public void setTipoasiento(List<Tipoasiento> tipos) {
        this.tipos = tipos;
    }

    public String buscar() {
       
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre desc");
        try {
            tipos = ejbTipoasiento.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       
        formulario.insertar();
        tipo = new Tipoasiento();
        tipo.setSigno(1);
        return null;
    }

    public String modificar(Tipoasiento tipo) {

        this.tipo = tipo;
        formulario.editar();
        if (tipo.getSigno()==null){
            tipo.setSigno(1);
        }

        return null;
    }
    public String eliminar(Tipoasiento tipo) {

        this.tipo = tipo;
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

         if ((tipo.getCodigo()== null) || (tipo.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código de tipo");
            return true;
        }
        if ((tipo.getNombre()== null) || (tipo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de tipo");
            return true;
        }
        
//        if ((tipo.getRelacioncobranza()== null) || (tipo.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en tipo");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            tipo.setUltimo(0);
            ejbTipoasiento.create(tipo, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbTipoasiento.edit(tipo, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbTipoasiento.remove(tipo, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Tipoasiento traer(Integer id) throws ConsultarException {
        return ejbTipoasiento.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "TipoasientoVista";
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
    public SelectItem[] getComboTipoasiento(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
//        parametros.put(";where", "o.editable=true");
        try {
            return Combos.getSelectItems(ejbTipoasiento.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboTipoasientoContab(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        parametros.put(";where", "o.modulo=:modulo and o.editable=true");
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        try {
            return Combos.getSelectItems(ejbTipoasiento.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TipoAsientoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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