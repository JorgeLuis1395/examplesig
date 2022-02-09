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
import org.beans.sfccbdmq.BodegasFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Sucursales;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "bodegasSfccbdmq")
@ViewScoped
public class BodegasBean {

    /**
     * Creates a new instance of BodegasBean
     */
    public BodegasBean() {
    }
    private Bodegas bodega;
    private Sucursales sucursal;
    private List<Bodegas> bodegas;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private BodegasFacade ejbBodegas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the bodega
     */
    public Bodegas getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(Bodegas bodega) {
        this.bodega=bodega;
    }

    /**
     * @return the bodegas
     */
    public List<Bodegas> getBodegas() {
        return bodegas;
    }

    /**
     * @param bodegas the bodegas to set
     */
    public void setBodegas(List<Bodegas> bodegas) {
        this.bodegas = bodegas;
    }

    public String buscar() {
       if (sucursal==null){
           return null;
       }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre desc");
        parametros.put(";where", "o.sucursal=:sucursal");
        parametros.put("sucursal", sucursal);
        try {
            bodegas = ejbBodegas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       if (sucursal==null){
           MensajesErrores.advertencia("Seleccione una sucursal primero");
           return null;
       }
        formulario.insertar();
        
        setBodega(new Bodegas());
        getBodega().setSucursal(sucursal);
        return null;
    }

    public String modificar(Bodegas bodega) {

        this.setBodega(bodega);
        formulario.editar();

        return null;
    }
    public String eliminar(Bodegas bodega) {

        this.setBodega(bodega);
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

        
        if ((getBodega().getNombre()== null) || (getBodega().getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de bodega");
            return true;
        }
        
//        if ((bodega.getRelacioncobranza()== null) || (bodega.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en bodega");
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
            ejbBodegas.create(getBodega(), seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbBodegas.edit(getBodega(), seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbBodegas.remove(getBodega(), seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Bodegas traer(Integer id) throws ConsultarException {
        return ejbBodegas.find(id);
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
        String nombreForma = "BodegasVista";
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
    public SelectItem[] getComboBodegas(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        try {
            List<Bodegas> bl=ejbBodegas.encontarParametros(parametros);
            return Combos.getSelectItems(bl, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboBodegasSuministros(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        parametros.put(";where", "o.venta=true");
        try {
            List<Bodegas> bl=ejbBodegas.encontarParametros(parametros);
            return Combos.getSelectItems(bl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboBodegasSuministrost(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        parametros.put(";where", "o.venta=true");
        try {
            List<Bodegas> bl=ejbBodegas.encontarParametros(parametros);
            return Combos.getSelectItems(bl, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public List<Bodegas> getListaBodegasSuministros(){
        
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        parametros.put(";where", "o.venta=true");
        try {
            return ejbBodegas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    /**
     * @return the sucursal
     */
    public Sucursales getSucursal() {
        return sucursal;
    }

    /**
     * @param sucursal the sucursal to set
     */
    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
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