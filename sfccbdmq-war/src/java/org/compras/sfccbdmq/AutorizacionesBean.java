/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "autorizacionesSfccbdmq")
@ViewScoped
public class AutorizacionesBean {

    /**
     * Creates a new instance of AutorizacionesBean
     */
    public AutorizacionesBean() {
    }
    private Autorizaciones autorizacion;
    private List<Autorizaciones> autorizaciones;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the autorizacion
     */
    public Autorizaciones getAutorizacion() {
        return autorizacion;
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(Autorizaciones autorizacion) {
        this.autorizacion = autorizacion;
    }

    /**
     * @return the autorizaciones
     */
    public List<Autorizaciones> getAutorizaciones() {
        return autorizaciones;
    }

    /**
     * @param autorizaciones the autorizaciones to set
     */
    public void setAutorizaciones(List<Autorizaciones> autorizaciones) {
        this.autorizaciones = autorizaciones;
    }

    public String buscar() {
        if (proveedorBean.getProveedor()== null) {
            autorizaciones = null;
            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.empresa=:empresa");
        parametros.put("empresa", proveedorBean.getProveedor().getEmpresa());
        parametros.put(";orden", "o.inicio desc");
        try {
            autorizaciones = ejbAutorizaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (proveedorBean.getProveedor()== null) {
            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        formulario.insertar();
        autorizacion = new Autorizaciones();
        autorizacion.setEmpresa(proveedorBean.getProveedor().getEmpresa());
        autorizacion.setFechaemision(new Date());
        autorizacion.setFechacaducidad(new Date());
        
        return null;
    }

    public String modificar(Autorizaciones autorizacion) {

        this.autorizacion = autorizacion;
        formulario.editar();

        return null;
    }
    public String eliminar(Autorizaciones autorizacion) {

        this.autorizacion = autorizacion;
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

        if ((autorizacion.getEstablecimiento()== null) || (autorizacion.getEstablecimiento().isEmpty())) {
            MensajesErrores.advertencia("Es necesario establecimiento de autorizacion");
            return true;
        }
         if ((autorizacion.getAutorizacion()== null) || (autorizacion.getAutorizacion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario autorización de autorizacion");
            return true;
        }
        if ((autorizacion.getPuntoemision()== null) || (autorizacion.getPuntoemision().isEmpty())) {
            MensajesErrores.advertencia("Es necesario punto de emisión de autorizacion");
            return true;
        }
        if ((autorizacion.getFechacaducidad()== null)) {
            MensajesErrores.advertencia("Es necesario fecha de caducidad de autorizacion");
            return true;
        }
        if ((autorizacion.getFechaemision()== null)) {
            MensajesErrores.advertencia("Es necesario fecha de emisión de autorizacion");
            return true;
        }
        if ((autorizacion.getFechacaducidad().before(autorizacion.getFechaemision()))) {
            MensajesErrores.advertencia("Fecha de caducidad debe ser mayor a fecha de emisión en autorizacion");
            return true;
        }
        if ((autorizacion.getInicio()== null) || (autorizacion.getInicio()<=0)) {
            MensajesErrores.advertencia("Es necesario Fin de serie válido en autorizacion");
            return true;
        }
        if ((autorizacion.getFin()== null) || (autorizacion.getFin()<=autorizacion.getInicio())) {
            MensajesErrores.advertencia("Es necesario Inicio de serie válido menor a finen autorizacion");
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
            ejbAutorizaciones.create(autorizacion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbAutorizaciones.edit(autorizacion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbAutorizaciones.remove(autorizacion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Autorizaciones traer(Integer id) throws ConsultarException {
        return ejbAutorizaciones.find(id);
    }

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
        String nombreForma = "AutorizacionesVista";
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
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
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
    public SelectItem[] getComboAutorizaciones(){
        if (proveedorBean.getProveedor()== null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        
        Map parametros = new HashMap();
        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento.codigo in ('FACT','OTR')" );
//        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento.codigo in ('FACT','OTR') and o.fin<=:vigente" );
        parametros.put("empresa", proveedorBean.getProveedor().getEmpresa());
//        parametros.put("vigente", configuracionBean.getConfiguracion().getPvigente());
        parametros.put(";orden", "o.inicio desc");
        try {
            return Combos.getSelectItems(ejbAutorizaciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboAutorizacionesNc(){
        if (proveedorBean.getProveedor()== null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento.codigo='NTCR'");
        parametros.put("empresa", proveedorBean.getProveedor().getEmpresa());
        parametros.put(";orden", "o.inicio desc");
        try {
            return Combos.getSelectItems(ejbAutorizaciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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
