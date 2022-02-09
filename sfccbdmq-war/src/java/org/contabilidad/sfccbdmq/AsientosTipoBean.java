/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.RenglonestipoFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglonestipo;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "asientosTipoSfccbdmq")
@ViewScoped
public class AsientosTipoBean {

    /**
     * Creates a new instance of RenglonestipoBean
     */
    public AsientosTipoBean() {
    }
    private Renglonestipo linea;
    private Codigos asientoTipo;
    private List<Renglonestipo> lineas;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private RenglonestipoFacade ejbRenglonestipo;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the linea
     */
    public Renglonestipo getLinea() {
        return linea;
    }

    /**
     * @param linea the linea to set
     */
    public void setLinea(Renglonestipo linea) {
        this.linea = linea;
    }

    /**
     * @return the lineas
     */
    public List<Renglonestipo> getRenglonestipo() {
        return lineas;
    }

    /**
     * @param lineas the lineas to set
     */
    public void setRenglonestipo(List<Renglonestipo> lineas) {
        this.lineas = lineas;
    }

    public String buscar() {
        if (asientoTipo == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.cuenta ");
        parametros.put(";where", "o.asientotipo=:asientoTipo");
        parametros.put("asientoTipo", asientoTipo);
        try {
            lineas = ejbRenglonestipo.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosTipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (asientoTipo == null) {
            MensajesErrores.advertencia("Seleccione una asiento Tipo primero");
            return null;
        }
        formulario.insertar();

        setLinea(new Renglonestipo());
        linea.setAsientotipo(asientoTipo);
        return null;
    }

    public String modificar(Renglonestipo linea) {

        this.setLinea(linea);
        proyectosBean.setProyectoSeleccionado(proyectosBean.traerCodigo(linea.getCentrocosto()));
        formulario.editar();

        return null;
    }

    public String eliminar(Renglonestipo linea) {

        this.setLinea(linea);
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

        if ((linea.getCuenta() == null) || (getLinea().getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Cuenta contable");
            return true;

        }

//        if ((linea.getRelacioncobranza()== null) || (linea.getRelacioncobranza()<=0)) {
//            MensajesErrores.advertencia("Es necesario Relación de cobranza válido en linea");
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
            ejbRenglonestipo.create(getLinea(), seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosTipoBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbRenglonestipo.edit(getLinea(), seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosTipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbRenglonestipo.remove(getLinea(), seguridadbean.getLogueado().getUserid());

            buscar();
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsientosTipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public Renglonestipo traer(Integer id) throws ConsultarException {
        return ejbRenglonestipo.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "AsientosTipoVista";
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
     * @return the asientoTipo
     */
    public Codigos getAsientoTipo() {
        return asientoTipo;
    }

    /**
     * @param asientoTipo the asientoTipo to set
     */
    public void setAsientoTipo(Codigos asientoTipo) {
        this.asientoTipo = asientoTipo;
    }

    public boolean isCredito(BigDecimal valor) {
        if (valor == null) {
            return false;
        }
        return valor.doubleValue() > 0;
    }

    public boolean isDebito(BigDecimal valor) {
        if (valor == null) {
            return false;
        }
        return valor.doubleValue() < 0;
    }

    /**
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
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
