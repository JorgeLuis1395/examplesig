/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.BkdireccionesFacade;
import org.beans.sfccbdmq.DireccionesFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.entidades.sfccbdmq.Bkdirecciones;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "direccionesEmpleado")
@ViewScoped
public class DireccionesEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private List<Bkdirecciones> listaDirecciones;
    private Bkdirecciones bkdireccion;
    private Formulario formulario = new Formulario();
    private Formulario formularioPrincipal = new Formulario();
    @EJB
    private BkdireccionesFacade ejbBkdirecciones;
    @EJB
    private DireccionesFacade ejbDirecciones;
    @EJB
    private EntidadesFacade ejbEntidad;
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
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "DireccionesEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
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
    /**
     * Creates a new instance of BkdireccionesEmpleadoBean
     */
    public DireccionesEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            listaDirecciones = ejbBkdirecciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(DireccionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
//        direccion = empleadoBean.getEmpleadoSeleccionado().getEntidad().bkdireccion;
//        if (direccion == null) {
//            direccion = new Direcciones();
//        }
        bkdireccion = new Bkdirecciones();
        bkdireccion.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        getFormulario().insertar();
        return null;
    }

    public String editar(Bkdirecciones bkdireccion) {

        this.bkdireccion = bkdireccion;
        getFormulario().editar();
        return null;
    }

    public String borrar(Bkdirecciones bkdireccion) {

        this.bkdireccion = bkdireccion;
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {

        if ((bkdireccion.getPrincipal() == null) || bkdireccion.getPrincipal().isEmpty()) {
            MensajesErrores.advertencia("Calle primaria es necesaria");
            return true;
        }

        if ((bkdireccion.getSecundaria() == null) || bkdireccion.getSecundaria().isEmpty()) {
            MensajesErrores.advertencia("Calle secundaria es necesaria");
            return true;
        }
        if ((bkdireccion.getNumero() == null) || bkdireccion.getNumero().isEmpty()) {
            MensajesErrores.advertencia("Número es necesario");
            return true;
        }

        if ((bkdireccion.getReferencia() == null) || bkdireccion.getReferencia().isEmpty()) {
            MensajesErrores.advertencia("Referencia es necesaria");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // crear no respalda
            ejbBkdirecciones.create(bkdireccion, parametrosSeguridad.getLogueado().getUserid());

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DireccionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbBkdirecciones.edit(bkdireccion, parametrosSeguridad.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }
    public String eliminar() {
        try {
            ejbBkdirecciones.remove(bkdireccion, parametrosSeguridad.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
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

    

    /**
     * @return the formularioPrincipal
     */
    public Formulario getFormularioPrincipal() {
        return formularioPrincipal;
    }

    /**
     * @param formularioPrincipal the formularioPrincipal to set
     */
    public void setFormularioPrincipal(Formulario formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }

    /**
     * @return the listaDirecciones
     */
    public List<Bkdirecciones> getListaDirecciones() {
        return listaDirecciones;
    }

    /**
     * @param listaDirecciones the listaDirecciones to set
     */
    public void setListaDirecciones(List<Bkdirecciones> listaDirecciones) {
        this.listaDirecciones = listaDirecciones;
    }

    /**
     * @return the bkdireccion
     */
    public Bkdirecciones getBkdireccion() {
        return bkdireccion;
    }

    /**
     * @param bkdireccion the bkdireccion to set
     */
    public void setBkdireccion(Bkdirecciones bkdireccion) {
        this.bkdireccion = bkdireccion;
    }
    public String cargaListner() {
        buscar();
        formularioPrincipal.insertar();
        return null;
    }
}
