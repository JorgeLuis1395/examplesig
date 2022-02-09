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
import java.text.SimpleDateFormat;
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
import org.beans.sfccbdmq.HcontratosempeladoFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Hcontratosempelado;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "cambiaContratosEmpleado")
@ViewScoped
public class CambiarContratosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Hcontratosempelado> listaHistorial;
    private Hcontratosempelado contrato;
    private Hcontratosempelado contratoAnterior;
    private Formulario formulario = new Formulario();
    @EJB
    private HcontratosempeladoFacade ejbHcontratosempelado;
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
        String nombreForma = "CambioContratosEmpleadosVista";
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
     * Creates a new instance of HcontratosempeladoEmpleadoBean
     */
    public CambiarContratosEmpleadoBean() {
    }

    private void anterior() {
        for (Hcontratosempelado h : listaHistorial) {
            if (h.getTipocontrato().equals(empleadoBean.getEmpleadoSeleccionado().getTipocontrato())) {
                contratoAnterior = h;
                return;
            }
        }
        contratoAnterior = new Hcontratosempelado();
        contratoAnterior.setTipocontrato(empleadoBean.getEmpleadoSeleccionado().getTipocontrato());
        contratoAnterior.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        contratoAnterior.setMotivo("CONTRATO INICIAL DEL EMPLEADO");
        contratoAnterior.setDesde(empleadoBean.getEmpleadoSeleccionado().getFechaingreso());
        contratoAnterior.setFecha(new Date());
        contratoAnterior.setUsuario(parametrosSeguridad.getLogueado().getUserid());
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        contrato = new Hcontratosempelado();
        contratoAnterior = null;
        contrato.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        contrato.setDesde(new Date());
        contrato.setHasta(new Date());
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("HISTORIALTC");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        String where = "o.empleado=:empleado ";
        parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
        parametros.put(";where", where);
        parametros.put(";orden", "o.fecha desc");
        try {
            listaHistorial = ejbHcontratosempelado.encontarParametros(parametros);
            anterior();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CambiarContratosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    public boolean validar() {

        if (contrato.getTipocontrato() == null) {
            MensajesErrores.advertencia("Ingrese un contrato");
            return true;
        }
        if ((contrato.getMotivo() == null) || (contrato.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Ingrese un motivo");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getCargoactual() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido un cargo");
            return true;
        }
        if ((empleadoBean.getEmpleadoSeleccionado().getFechaingreso() == null)) {
            MensajesErrores.advertencia("Empleado no tiene definido una fecha de ingreso");
            return true;
        }

        if (contrato.getDesde() == null) {
            MensajesErrores.advertencia("Ingrese la fecha del contrato");
            return true;
        }
        if (contrato.getDesde().before(new Date())) {
            MensajesErrores.advertencia("Inicio de contrato mayor a hoy");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            if (contratoAnterior != null) {
                contratoAnterior.setHasta(contrato.getDesde());

            }
            Empleados e = empleadoBean.getEmpleadoSeleccionado();
            int dias = contrato.getTipocontrato().getDuracion();
            if (dias > 0) {
                Calendar c = Calendar.getInstance();
                c.setTime(contrato.getDesde());
                c.add(Calendar.DATE, dias);
                contrato.setHasta(c.getTime());
            }
            contrato.setEmpleado(e);
            contrato.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            contrato.setFecha(new Date());
            e.setTipocontrato(contrato.getTipocontrato());
            ejbEmpleados.edit(e, parametrosSeguridad.getLogueado().getUserid());
//            ejbHcontratosempelado.pornerActivo(e, contrato.getDesde());
            ejbHcontratosempelado.create(contrato, parametrosSeguridad.getLogueado().getUserid());
            if (contratoAnterior.getId() != null) {
                ejbHcontratosempelado.edit(contratoAnterior, parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbHcontratosempelado.create(contratoAnterior, parametrosSeguridad.getLogueado().getUserid());
            }
            if (contrato.getHasta() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                MensajesErrores.informacion("El contrato es válido hasta : " + sdf.format(contrato.getHasta()));
            } else {
                MensajesErrores.informacion("El contrato es de caracter indefinido");
            }
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        buscar();
        empleadoBean.setEmpleadoSeleccionado(null);
        empleadoBean.setApellidos(null);
        listaHistorial = null;
        contratoAnterior = null;
        contrato = null;
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
    public List<Hcontratosempelado> getListaHistorial() {
        return listaHistorial;
    }

    /**
     * @param listaHistorial the listaHistorial to set
     */
    public void setListaHistorial(List<Hcontratosempelado> listaHistorial) {
        this.listaHistorial = listaHistorial;
    }

    /**
     * @return the contrato
     */
    public Hcontratosempelado getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Hcontratosempelado contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the contratoAnterior
     */
    public Hcontratosempelado getContratoAnterior() {
        return contratoAnterior;
    }

    /**
     * @param contratoAnterior the contratoAnterior to set
     */
    public void setContratoAnterior(Hcontratosempelado contratoAnterior) {
        this.contratoAnterior = contratoAnterior;
    }

}
