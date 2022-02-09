/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.contabilidad.sfccbdmq.CuentasBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
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
import org.beans.sfccbdmq.TipoegresoFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipoegreso;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tiposEgresoSfccbdmq")
@ViewScoped
public class TiposEgresoBean {

    /**
     * Creates a new instance of TipoegresoBean
     */
    public TiposEgresoBean() {
    }
    private Tipoegreso tipoegreso;
    private List<Tipoegreso> tiposegresos;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private TipoegresoFacade ejbTipoegreso;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    /**
     * @return the tipoegreso
     */
    public Tipoegreso getTipoegreso() {
        return tipoegreso;
    }

    /**
     * @param tipoegreso the tipoegreso to set
     */
    public void setTipoegreso(Tipoegreso tipoegreso) {
        this.tipoegreso = tipoegreso;
    }

    /**
     * @return the tiposegresos
     */
    public List<Tipoegreso> getTiposegresos() {
        return tiposegresos;
    }

    /**
     * @param tiposegresos the tiposegresos to set
     */
    public void setTiposegresos(List<Tipoegreso> tiposegresos) {
        this.tiposegresos = tiposegresos;
    }

    public String buscar() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre asc");
        try {
            tiposegresos = ejbTipoegreso.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TiposEgresoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {

        formulario.insertar();
        tipoegreso = new Tipoegreso();
        cuentasBean.setCuenta(null);
        cuentasBean.setCodigo(null);
        return null;
    }
    public String crearAnticipo(Tipoegreso tipoegreso) {

        formulario.insertar();
        this.tipoegreso = new Tipoegreso();
        cuentasBean.setCuenta(null);
        cuentasBean.setCodigo(null);
//        tipoegreso.setPadre(tipoegreso);
        return null;
    }
    public String modificar(Tipoegreso tipoegreso) {

        this.tipoegreso = tipoegreso;
        formulario.editar();
        cuentasBean.setCuenta(cuentasBean.traerCodigo(tipoegreso.getCuenta()));
        cuentasBean.setCodigo(tipoegreso.getCuenta());
        return null;
    }

    public String eliminar(Tipoegreso tipoegreso) {

        this.tipoegreso = tipoegreso;
        formulario.eliminar();
        cuentasBean.setCuenta(cuentasBean.traerCodigo(tipoegreso.getCuenta()));
        cuentasBean.setCodigo(tipoegreso.getCuenta());
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
        
        if ((tipoegreso.getNombre() == null) || (tipoegreso.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de tipoegreso");
            return true;
        }
        return cuentasBean.validaCuentaMovimiento();
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            tipoegreso.setCuenta(cuentasBean.getCuenta().getCodigo());
            ejbTipoegreso.create(tipoegreso, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TiposEgresoBean.class.getName()).log(Level.SEVERE, null, ex);
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
            tipoegreso.setCuenta(cuentasBean.getCuenta().getCodigo());
            ejbTipoegreso.edit(tipoegreso, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TiposEgresoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbTipoegreso.remove(tipoegreso, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TiposEgresoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Tipoegreso traer(Integer id) throws ConsultarException {
        return ejbTipoegreso.find(id);
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
        String nombreForma = "TipoegresoVista";
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

    public SelectItem[] getComboTipoegreso() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre desc");
        try {
            return Combos.getSelectItems(ejbTipoegreso.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TiposEgresoBean.class.getName()).log(Level.SEVERE, null, ex);
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
