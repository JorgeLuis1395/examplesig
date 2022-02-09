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
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HorarioempleadoFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Horarioempleado;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "horariosxEmpleado")
@ViewScoped
public class HorariosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
//    @ManagedProperty(value = "#{imagenesSfccbdmq}")
//    private ImagenesBean imagenesBean;
    private List<Horarioempleado> listaHorarios;
    private Horarioempleado horario;
    private Formulario formulario = new Formulario();
    private int operativo;
    @EJB
    private HorarioempleadoFacade ejbHorarios;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean organigramaBean;

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
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "HorariosEmpleadosVista";
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
     * Creates a new instance of HorariosEmpleadoBean
     */
    public HorariosEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado ";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            setListaHorarios(ejbHorarios.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            Logger.getLogger(HorariosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscarTodos() {

        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null";
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            setListaHorarios(ejbHorarios.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            Logger.getLogger(HorariosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        horario = new Horarioempleado();
        horario.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
//        horario.setValor(horario.getHorario().getValor());
        getFormulario().insertar();
        return null;
    }

    public String nuevoTodos() {
//        if (empleadoBean.getEmpleadoSeleccionado() == null) {
//            MensajesErrores.advertencia("Ingrese un empleado");
//            return null;
//        }
        horario = new Horarioempleado();
//        horario.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
//        horario.setValor(horario.getHorario().getValor());
        getFormulario().insertar();
        return null;
    }

    public String modifica(Horarioempleado horario) {
        this.horario = horario;
        getFormulario().editar();
        return null;
    }

    public String borra(Horarioempleado horario) {
        this.horario = horario;
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {
        if ((horario.getNombre() == null) || (horario.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (horario.getHora() == null) {
            MensajesErrores.advertencia("Seleccione un horario");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbHorarios.create(horario, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String insertarTodos() {
        if (validar()) {
            return null;
        }
        try {
            Map parametros = new HashMap();

            String where = " ";

            switch (operativo) {
                case 1:

//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                    
                    where += " o.activo=true and o.tipocontrato is not null   "
                            + "and (o.fechasalida is null) and o.factor=3";
                    break;
                case 2:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                    where = " o.activo=true and o.tipocontrato is not null  "
                            + "and (o.fechasalida is null ) and o.factor=1";

                    break;
                case 3:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                    where += " o.activo=true and o.tipocontrato is not null   "
                            + "and (o.fechasalida is null ) and o.factor=1.67";
                    break;
                default:
//                parametros.put(";where", "o.cargoactual.organigrama=:organigrama and o.activo=true "
                    where += " o.activo=true and o.tipocontrato is not null "
                            + "and (o.fechasalida is null )";
                    break;
            }

//            if (getOrganigramaBean().getOrganigramaL() != null) {
//                where += " and o.cargoactual.organigrama.codigo like :organigrama";
//                parametros.put("organigrama", getOrganigramaBean().getOrganigramaL().getCodigo() + "%");
//            }
            parametros.put(";where", where);
            List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            for (Empleados e : listaEmpleados) {
                Horarioempleado h = new Horarioempleado();
                h.setEmpleado(e);
                h.setHora(horario.getHora());
                h.setIngreso(horario.getIngreso());
                h.setNombre(horario.getNombre());
                ejbHorarios.create(h, parametrosSeguridad.getLogueado().getUserid());
            }

        } catch (InsertarException | ConsultarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //buscar();
        buscarTodos();
        getFormulario().cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbHorarios.edit(horario, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String eliminar() {
        try {

            ejbHorarios.remove(horario, parametrosSeguridad.getLogueado().getUserid());
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
     * @return the listaHorarios
     */
    public List<Horarioempleado> getListaHorarios() {
        return listaHorarios;
    }

    /**
     * @param listaHorarios the listaHorarios to set
     */
    public void setListaHorarios(List<Horarioempleado> listaHorarios) {
        this.listaHorarios = listaHorarios;
    }

    /**
     * @return the horario
     */
    public Horarioempleado getHorario() {
        return horario;
    }

    /**
     * @param horario the horario to set
     */
    public void setHorario(Horarioempleado horario) {
        this.horario = horario;
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
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the organigramaBean
     */
    public CargoxOrganigramaBean getOrganigramaBean() {
        return organigramaBean;
    }

    /**
     * @param organigramaBean the organigramaBean to set
     */
    public void setOrganigramaBean(CargoxOrganigramaBean organigramaBean) {
        this.organigramaBean = organigramaBean;
    }

}