/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.entidades.sfccbdmq.Historialsanciones;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "sancionesEmpleado")
@ViewScoped
public class SancionesEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Historialsanciones> listaHistorial;
    private Historialsanciones sancion;
    private Formulario formulario = new Formulario();
    @EJB
    private HistorialsancionesFacade ejbHistorialsanciones;
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

        String nombreForma = "SancionesEmpleadosVista";
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
     * Creates a new instance of HistorialsancionesEmpleadoBean
     */
    public SancionesEmpleadoBean() {
    }

//    public String buscar() {
//
//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            MensajesErrores.advertencia("Ingrese un empleado");
//            return null;
//        }
//
//        try {
//            Map parametros = new HashMap();
//            String where = "o.empleado=:empleado ";
//            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
//            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
//            listaHistorial = ejbHistorialsanciones.encontarParametros(parametros);
//            anterior();
//        } catch (ConsultarException ex) {
//            Logger.getLogger(CambiarsEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        sancion = new Historialsanciones();
        sancion.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        sancion.setFsancion(new Date());
        sancion.setFaprobacion(new Date());
        sancion.setPorcentaje(0);
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("SANCIONES");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        String where = "o.empleado=:empleado ";
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put(";where", where);
        parametros.put(";orden", "o.fsancion desc");
        try {
            listaHistorial = ejbHistorialsanciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SancionesEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    public boolean validar() {

        if (sancion.getTipo() == null) {
            MensajesErrores.advertencia("Ingrese un tipo de sanción");
            return true;
        }
        if (sancion.getValor() == null) {
            MensajesErrores.advertencia("Ingrese un porcentaje");
            return true;
        }
        if (sancion.getTipo().getEspecunaria()) {
            if (sancion.getValor().doubleValue() > sancion.getTipo().getSancion().doubleValue()) {
                MensajesErrores.advertencia("Porcentaje mayor al permitido");
                return true;
            }
        }
        if ((sancion.getMotivo() == null) || (sancion.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Ingrese un motivo");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
//            sancion.setValor(sancion.getTipo().getSancion());
            sancion.setEsleve(sancion.getTipo().getEsleve());
            sancion.setEspecunaria(sancion.getTipo().getEspecunaria());
            ejbHistorialsanciones.create(sancion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscar();
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        listaHistorial = null;
        sancion = null;
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
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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
     * @return the listaHistorial
     */
    public List<Historialsanciones> getListaHistorial() {
        return listaHistorial;
    }

    /**
     * @param listaHistorial the listaHistorial to set
     */
    public void setListaHistorial(List<Historialsanciones> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    /**
     * @return the sancion
     */
    public Historialsanciones getSancion() {
        return sancion;
    }

    /**
     * @param sancion the sancion to set
     */
    public void setSancion(Historialsanciones sancion) {
        this.sancion = sancion;
    }

}
