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
import java.util.Calendar;
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
import org.beans.sfccbdmq.RetencionesFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Retenciones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "retencionesSfccbdmq")
@ViewScoped
public class RetencionesBean {

    /**
     * Creates a new instance of RetencionesBean
     */
    public RetencionesBean() {
    }
    private Retenciones retencion;
    private List<Retenciones> retenciones;
    
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private RetencionesFacade ejbRetenciones;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
   

    /**
     * @return the retencion
     */
    public Retenciones getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retenciones retencion) {
        this.retencion= retencion;
    }

    /**
     * @return the retenciones
     */
    public List<Retenciones> getRetenciones() {
        return retenciones;
    }

    /**
     * @param retenciones the retenciones to set
     */
    public void setRetenciones(List<Retenciones> retenciones) {
        this.retenciones = retenciones;
    }

    public String buscar() {
       
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
            retenciones = ejbRetenciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       
        formulario.insertar();
        retencion = new Retenciones();
        return null;
    }

    public String modificar(Retenciones retencion) {

        this.retencion = retencion;
        formulario.editar();
//        cuentasBean.setCuenta(cuentasBean.traerCodigo(retencion.getCuenta()));
//        cuentasBean.setCodigo(retencion.getCuenta());
        return null;
    }
    public String eliminar(Retenciones retencion) {

        this.retencion = retencion;
        formulario.eliminar();
        cuentasBean.setCuenta(cuentasBean.traerCodigo(retencion.getCuenta()));
        cuentasBean.setCodigo(retencion.getCuenta());
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
//        if (cuentasBean.getCuenta()==null){
//            MensajesErrores.advertencia("Es necesario cuenta contable");
//            return true;
//        }
//        retencion.setCuenta(cuentasBean.getCuenta().getCodigo());
        if ((retencion.getCodigo()== null) || (retencion.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código de la retencion");
            return true;
        }
        if ((retencion.getEtiqueta()== null) || (retencion.getEtiqueta().isEmpty())) {
            MensajesErrores.advertencia("Es necesario etiqueta de la retencion");
            return true;
        }
        if ((retencion.getNombre()== null) || (retencion.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de la retencion");
            return true;
        }
//        if ((retencion.getNombre()== null) || (retencion.getNombre().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario nombre de la retencion");
//            return true;
//        }
//        cuentasBean.setCuenta(cuentasBean.traerCodigo(retencion.getCuenta()));
        return false;
//        return cuentasBean.validaCuentaMovimiento();
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            ejbRetenciones.create(retencion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbRetenciones.edit(retencion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbRetenciones.remove(retencion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Retenciones traer(Integer id) throws ConsultarException {
        return ejbRetenciones.find(id);
    }

    @PostConstruct
    private void activar() {
        Calendar c=Calendar.getInstance();
        
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "RetencionesVista";
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
    public SelectItem[] getComboRetenciones(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false");
        try {
            return Combos.getSelectItems(ejbRetenciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboRetencionesImpuestos(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true");
        try {
            return Combos.getSelectItems(ejbRetenciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
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
