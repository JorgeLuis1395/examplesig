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
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Sucursales;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "puntosemisionSfccbdmq")
@ViewScoped
public class PuntoEmisionBean {

    /**
     * Creates a new instance of PuntoemisionBean
     */
    public PuntoEmisionBean() {
    }
    private Puntoemision puntoemision;
    private Sucursales sucursal;
    private List<Puntoemision> puntosemision;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private PuntoemisionFacade ejbPuntoemision;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the puntoemision
     */
    public Puntoemision getPuntoemision() {
        return puntoemision;
    }

    /**
     * @param puntoemision the puntoemision to set
     */
    public void setPuntoemision(Puntoemision puntoemision) {
        this.puntoemision = puntoemision;
    }

    /**
     * @return the puntosemision
     */
    public List<Puntoemision> getPuntosemision() {
        return puntosemision;
    }

    /**
     * @param puntosemision the puntosemision to set
     */
    public void setPuntosemision(List<Puntoemision> puntosemision) {
        this.puntosemision = puntosemision;
    }

    public String buscar() {
       if (sucursal==null){
           return null;
       }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.sucursal=:sucursal");
        parametros.put("sucursal", sucursal);
        try {
            puntosemision = ejbPuntoemision.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
       if (sucursal==null){
           MensajesErrores.advertencia("Seleccione una sucursal primero");
           return null;
       }
        formulario.insertar();
        
        puntoemision = new Puntoemision();
        puntoemision.setSucursal(sucursal);
        return null;
    }

    public String modificar(Puntoemision puntoemision) {

        this.puntoemision = puntoemision;
        formulario.editar();

        return null;
    }
    public String eliminar(Puntoemision puntoemision) {

        this.puntoemision = puntoemision;
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

        if ((puntoemision.getCodigo()== null) || (puntoemision.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Código de puntoemision");
            return true;
        }
        if ((puntoemision.getNombre()== null) || (puntoemision.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de puntoemision");
            return true;
        }
        
//        if ((puntoemision.getRelacioncobranza()== null) || (puntoemision.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en puntoemision");
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
            ejbPuntoemision.create(puntoemision, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbPuntoemision.edit(puntoemision, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbPuntoemision.remove(puntoemision, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Puntoemision traer(Integer id) throws ConsultarException {
        return ejbPuntoemision.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "PuntoemisionVista";
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
    public SelectItem[] getComboPuntoemision(){
        if (sucursal==null){
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.sucursal=:sucursal and o.activo=true");
        parametros.put("sucursal", sucursal);
        try {
            return Combos.getSelectItems(ejbPuntoemision.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboPuntoemisionManual(){
        if (sucursal==null){
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.sucursal=:sucursal and o.automatica=false");
        parametros.put("sucursal", sucursal);
        try {
            return Combos.getSelectItems(ejbPuntoemision.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
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
