/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import org.seguridad.sfccbdmq.*;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.BodegasFacade;
import org.beans.sfccbdmq.PermisosbodegasFacade;
import org.contabilidad.sfccbdmq.BodegasBean;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Permisosbodegas;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "permisosBodegasEmpleadosSfccbdmq")
@ViewScoped
public class PerfilesEmpleadosBodegasBean implements Serializable {

    /**
     * Creates a new instance of MenusBean
     */
    public PerfilesEmpleadosBodegasBean() {
    }
    
    private Formulario formulario = new Formulario();
    private List<Permisosbodegas> permisos;
    private Permisosbodegas permiso;
    @EJB
    private PermisosbodegasFacade ejbPermisos;
    @EJB
    private BodegasFacade ejbBodegas;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    private Perfiles perfil;

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
        String nombreForma = "PermisosBodegasEmpleadosVista";
       
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
//        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
//        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
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

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?

        if (empleadosBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }

        permiso = new Permisosbodegas();
        permiso.setUsuario(empleadosBean.getEmpleadoSeleccionado());
        formulario.insertar();
        return null;
    }

    public String modificar(Permisosbodegas permiso) {

        this.permiso = permiso;
        formulario.editar();
        return null;
    }

    public String eliminar(Permisosbodegas permiso) {

        this.permiso = permiso;
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }
    // buscar

    public String buscar() {

        if (empleadosBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.usuario=:usuario ");
            parametros.put(";orden", "o.bodega.nombre");
            parametros.put("usuario", empleadosBean.getEmpleadoSeleccionado());
            permisos = ejbPermisos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PerfilesEmpleadosBodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((permiso.getBodega() == null)) {
            MensajesErrores.advertencia("Es necesario Bodega");
            return true;
        }

        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
            ejbPermisos.create(permiso, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PerfilesEmpleadosBodegasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbPermisos.edit(permiso, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PerfilesEmpleadosBodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            ejbPermisos.remove(permiso, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(PerfilesEmpleadosBodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
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

    public SelectItem[] getComboBodegas() {
//        if (permiso == null) {
//            return null;
//        }
//        if (permiso.getUsuario() == null) {
//            return null;
//        }
        buscar();
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        parametros.put(";where", "o.venta=true");
        try {
            List<Bodegas> listaBodegas = ejbBodegas.encontarParametros(parametros);
            List<Bodegas> listaRetorno = new LinkedList<>();
            for (Bodegas b:listaBodegas){
                if (!estaBodega(b)){
                    listaRetorno.add(b);
                }
            }
            return Combos.getSelectItems(listaRetorno, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboBodegasLogueado() {
        
       
        Map parametros = new HashMap();
        parametros.put(";orden", "o.nombre");
        parametros.put(";where", "o.venta=true");
        try {
            List<Bodegas> listaBodegas = ejbBodegas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.usuario=:usuario ");
            parametros.put(";orden", "o.bodega.nombre");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getEmpleados());
            permisos = ejbPermisos.encontarParametros(parametros);
            List<Bodegas> listaRetorno = new LinkedList<>();
            for (Bodegas b:listaBodegas){
                if (estaBodega(b)){
                    listaRetorno.add(b);
                }
            }
            return Combos.getSelectItems(listaRetorno, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public boolean estaBodega(Bodegas b){
        for (Permisosbodegas p:permisos){
            if (p.getBodega().equals(b)){
                return true;
            }
        }
        return false;
    } 

    

    /**
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
    }

    /**
     * @return the permisos
     */
    public List<Permisosbodegas> getPermisos() {
        return permisos;
    }

    /**
     * @param permisos the permisos to set
     */
    public void setPermisos(List<Permisosbodegas> permisos) {
        this.permisos = permisos;
    }

    /**
     * @return the permiso
     */
    public Permisosbodegas getPermiso() {
        return permiso;
    }

    /**
     * @param permiso the permiso to set
     */
    public void setPermiso(Permisosbodegas permiso) {
        this.permiso = permiso;
    }

    /**
     * @return the parametrosSeguridad
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
}