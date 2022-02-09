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
import java.util.Calendar;
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
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reingresoEmpleado")
@ViewScoped
public class ReingresoEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Historialcargos> listaHistorial;
    private Historialcargos cargo;
    private Historialcargos cargoAnterior;
    private Formulario formulario = new Formulario();
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
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
        String nombreForma = "ReingresoEmpleadosVista";
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
     * Creates a new instance of HistorialcargosEmpleadoBean
     */
    public ReingresoEmpleadoBean() {
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
//            listaHistorial = ejbHistorialcargos.encontarParametros(parametros);
//            anterior();
//        } catch (ConsultarException ex) {
//            Logger.getLogger(CambiarCargosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    private void anterior() {
        for (Historialcargos h : listaHistorial) {
            if (h.getCargo().equals(empleadoBean.getEmpleadoSeleccionado().getCargoactual())) {
                cargoAnterior = h;
                return;
            }
        }
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        cargo = new Historialcargos();
        cargoAnterior=null;
        cargo.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        cargo.setDesde(new Date());
        cargo.setHasta(new Date());
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("REINGRESO");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        String where = "o.empleado=:empleado ";
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put(";where", where);
        parametros.put(";orden", "o.fecha desc");
        try {
            listaHistorial = ejbHistorialcargos.encontarParametros(parametros);
            anterior();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReingresoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    public boolean validar() {

        if (cargo.getCargo() == null) {
            MensajesErrores.advertencia("Ingrese un cargo");
            return true;
        }
        if ((cargo.getMotivo()== null) || (cargo.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Ingrese un motivo");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getTipocontrato()==null)) {
            MensajesErrores.advertencia("Empleado no tiene definido un tipo de contrato");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getFechaingreso()==null)) {
            MensajesErrores.advertencia("Empleado no tiene definido una fecha de ingreso");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            cargo.setActivo(Boolean.TRUE);
            if (cargoAnterior != null) {
                cargoAnterior.setHasta(cargo.getDesde());

            }
            Empleados e = empleadoBean.getEmpleadoSeleccionado();
            
            cargo.setActivo(false);
            cargo.setVigente(e.getTipocontrato().getNombramiento());
//            cargo.setAprobacion(true);
            cargo.setEmpleado(e);
            cargo.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            cargo.setFecha(new Date());
            cargo.setSueldobase(cargo.getCargo().getCargo().getEscalasalarial().getSueldobase());
            if (e.getTipocontrato().getNombramiento()) {
                cargo.setHasta(null);
            } else {
                int cuanto = e.getTipocontrato().getDuracion();
                Calendar ck = Calendar.getInstance();
                ck.setTime(e.getFechaingreso());
                ck.add(Calendar.DATE, cuanto);
                cargo.setHasta(ck.getTime());
//                     Date fechafin
//                    = getFormulario().cancelar();
            }
            e.setCargoactual(cargo.getCargo());
            e.setFechaingreso(cargo.getDesde());
            e.setFechasalida(null);
            e.setActivo(Boolean.TRUE);
            ejbHistorialcargos.create(cargo, parametrosSeguridad.getLogueado().getUserid());
            ejbEmpleados.edit(e, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscar();
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        listaHistorial=null;
        cargoAnterior=null;
        cargo=null;
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
    public List<Historialcargos> getListaHistorial() {
        return listaHistorial;
    }

    /**
     * @param listaHistorial the listaHistorial to set
     */
    public void setListaHistorial(List<Historialcargos> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    /**
     * @return the cargo
     */
    public Historialcargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Historialcargos cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the cargoAnterior
     */
    public Historialcargos getCargoAnterior() {
        return cargoAnterior;
    }

    /**
     * @param cargoAnterior the cargoAnterior to set
     */
    public void setCargoAnterior(Historialcargos cargoAnterior) {
        this.cargoAnterior = cargoAnterior;
    }

}
