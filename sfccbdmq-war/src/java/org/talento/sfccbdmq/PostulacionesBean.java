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
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.beans.sfccbdmq.SolicitudescargoFacade;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Postulaciones;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "Postulaciones")
@ViewScoped
public class PostulacionesBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of SolicitudesCargoBean
     */

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Formulario formulario = new Formulario();
    private Solicitudescargo solicitudcargo;
    private Postulaciones postulacion;
    private String pinempleado;
    private List listadoEmpleados;
//    private List<Postulaciones> postulaciones;
    private LazyDataModel<Postulaciones> postulaciones;
    private Empleados empleado;
    private Cargos cargo;
    private String buscarCedula;
    private String buscarNombre;
    private String nombre;

    @EJB
    private SolicitudescargoFacade ejbSolicitudCargo;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PostulacionesFacade ejbPostulaciones;
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
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "OficinasVista";
        if (perfilString == null) {
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
        
        if (parametrosSeguridad.getSolicitudcargo() != null) {
                solicitudcargo = parametrosSeguridad.getSolicitudcargo();

//           C empleadosBean.crearPostulante();
            formulario.insertar();
            setPostulacion(new Postulaciones());
            getPostulacion().setSolicitudcargo(solicitudcargo);
        }
    }

    // fin perfiles

    public PostulacionesBean() {
        postulaciones = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };

    }

    public String buscar() {

        setPostulaciones(new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();

                if (scs.length == 0) {
                    parametros.put(";orden", "o.empleado.entidad.apellidos asc,   o.fechaingreso DESC");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }

                String where = " o.activo=true and o.solicitudcargo.vigente=true";

                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                if (cargo != null) {
                    where += " and o.solicitudcargo.cargovacante=:cargo";
                    parametros.put("cargo", cargo);
                }

                if (buscarCedula != null && !buscarCedula.isEmpty()) {
                    where += " and upper(o.empleado.entidad.pin)=:pin";
                    parametros.put("pin", buscarCedula);
                }

                if (buscarNombre != null && !buscarNombre.isEmpty()) {
                    where += " and upper(o.empleado.entidad.apellidos)like:apellidos";
                    parametros.put("apellidos", "%" + buscarNombre.toUpperCase() + "%");
                }

                if (nombre != null && !nombre.isEmpty()) {
                    where += " and upper(o.empleado.entidad.nombres)like:nom";
                    parametros.put("nom", "%" + nombre.toUpperCase() + "%");
                }

                if (solicitudcargo != null) {
                    where += " and o.solicitudcargo=:solicitud";
                    parametros.put("solicitud", solicitudcargo);
                }

                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbPostulaciones.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }

                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                postulaciones.setRowCount(total);

                try {
                    return ejbPostulaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return null;

    }
   
    public String modificaPostulante() {
        empleado = new Empleados();
//         empleadosBean.buscarCurriculoEmp(empleado);
//        empleadosBean.modificar(empleado);
        return null;
    }

    public String postulanteHabilidades() {
        empleado = empleadosBean.getEmpleado();
//        empleadosBean.Habilidades(empleado);
        return null;
    }

    public String postulanteExperiencia() {
        empleado = empleadosBean.getEmpleado();
//        empleadosBean.Experiencia(empleado);
        return null;
    }

    public String postulanteFormacion() {
        empleado = empleadosBean.getEmpleado();
//        empleadosBean.Formacion(empleado);
        return null;
    }

//    public String calificacionPost(Postulaciones postu) {
//       parametrosSeguridad.setPostulacion(postu.getId());
//       return "CalificacionMeritosVista.jsf?faces-redirect=true";
//    }     
    public String salir() {
        return "BlancoVista.jsf?faces-redirect=true";
    }

    public String crear(Solicitudescargo solicitud) {
        // se deberia chequear perfil?
        
        solicitudcargo = solicitud;
        setPostulacion(new Postulaciones());
        getPostulacion().setSolicitudcargo(solicitudcargo);
        empleado = new Empleados();
//       listaestudiosempleados= new LinkedList<>();
//       formularioSolicititud.cancelar();
        formulario.editar();
        return "PostulacionesCargoVista.jsf?faces-redirect=true";
    }

    public String buscarSolicitud() {
//        parametrosSeguridad.setCedulapostulante(null);
//        parametrosSeguridad.setSolicitudcargo(null);
        return "EscojerSolicitudVista.jsf?faces-redirect=true";
    }

    public boolean validar() {

//POSTULANTE
        if (empleadosBean.getEmpleado().getId() == null) {
            MensajesErrores.advertencia("Ingrese Postulante");
            return true;

        }
//Postulante ya resgitrado???
        if (formulario.isNuevo()) {
//            if (!ejbPostulaciones.postulacionesEmpleado(solicitudcargo, empleadosBean.getEmpleado()).isEmpty() || solicitudcargo == null || empleadosBean.getEmpleado() == null) {
//                MensajesErrores.advertencia("Postulación ya Registrada.");
//                parametrosSeguridad.setMensaje(null);
//                return true;
//            }
        }

        return false;
    }

    public String insertar() {
        try {
           
            if (validar()) {
                return null;
            }
//            getPostulacion().
            getPostulacion().setValidacionmerito(Boolean.FALSE);
            getPostulacion().setValidacionposicion(Boolean.FALSE);
            getPostulacion().setGanador(Boolean.FALSE);
            getPostulacion().setEmpleado(empleadosBean.getEmpleado());
            getPostulacion().setActivo(Boolean.TRUE);
            //Ingreso calificaciones
            getPostulacion().setFechaingreso(new Date());
            ejbPostulaciones.create(getPostulacion(), parametrosSeguridad.getLogueado().getUserid());
            formulario.cancelar();
            buscar();
        } catch (InsertarException ex) {
            Logger.getLogger(PostulacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

//        parametrosSeguridad.setSolicitudcargo(null);
//        parametrosSeguridad.setCedulapostulante(null);
//        parametrosSeguridad.setMensaje("Postulación Ingresada con Éxito");
        return "EscojerSolicitudVista.jsf?face-redirect=true";
    }

    public String grabar() {
        if (validar()) {
            return null;
        }

        empleadosBean.grabar();

        getFormulario().cancelar();
        buscar();
        return null;
    }

    public String eliminar(Postulaciones postulacion) {
        
        this.setPostulacion(postulacion);
        solicitudcargo = postulacion.getSolicitudcargo();
        empleado = this.getPostulacion().getEmpleado();
        empleadosBean.setEmpleado(empleado);
        formulario.eliminar();
        return null;
    }

    public String cancelar() {

//        if((parametrosSeguridad.getSolicitudcargo()!=null  && parametrosSeguridad.getCedulapostulante()!=null){
//        return null;
//        
//        }
        parametrosSeguridad.setSolicitudcargo(null);
//        parametrosSeguridad.setCedulapostulante(null);
        empleadosBean.setEmpleado(empleado);
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
            getPostulacion().setActivo(Boolean.FALSE);
            ejbPostulaciones.edit(getPostulacion(), parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PostulacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

//        try {
//            ejbCodigos.remove(codigo, parametrosSeguridad.getLogueado().getUserid());
//        } catch (BorrarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger(CodigosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        formulario.cancelar();
        buscar();
        return null;
    }

//    
    /**
     * @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
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
     * @return the solicitudcargo
     */
    public Solicitudescargo getSolicitudcargo() {
        return solicitudcargo;
    }

    /**
     * @param solicitudcargo the solicitudcargo to set
     */
    public void setSolicitudcargo(Solicitudescargo solicitudcargo) {
        this.solicitudcargo = solicitudcargo;
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the pinempleado
     */
    public String getPinempleado() {
        return pinempleado;
    }

    /**
     * @param pinempleado the pinempleado to set
     */
    public void setPinempleado(String pinempleado) {
        this.pinempleado = pinempleado;
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
     * @return the listadoEmpleados
     */
    public List getListadoEmpleados() {
        return listadoEmpleados;
    }

    /**
     * @param listadoEmpleados the listadoEmpleados to set
     */
    public void setListadoEmpleados(List listadoEmpleados) {
        this.listadoEmpleados = listadoEmpleados;
    }

    /**
     * @return the cargo
     */
    public Cargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
    }


    /**
     * @return the buscarCedula
     */
    public String getBuscarCedula() {
        return buscarCedula;
    }

    /**
     * @param buscarCedula the buscarCedula to set
     */
    public void setBuscarCedula(String buscarCedula) {
        this.buscarCedula = buscarCedula;
    }

    /**
     * @return the buscarNombre
     */
    public String getBuscarNombre() {
        return buscarNombre;
    }

    /**
     * @param buscarNombre the buscarNombre to set
     */
    public void setBuscarNombre(String buscarNombre) {
        this.buscarNombre = buscarNombre;
    }

    /**
     * @return the postulaciones
     */
    public LazyDataModel<Postulaciones> getPostulaciones() {
        return postulaciones;
    }

    /**
     * @param postulaciones the postulaciones to set
     */
    public void setPostulaciones(LazyDataModel<Postulaciones> postulaciones) {
        this.postulaciones = postulaciones;
    }

    /**
     * @return the postulacion
     */
    public Postulaciones getPostulacion() {
        return postulacion;
    }

    /**
     * @param postulacion the postulacion to set
     */
    public void setPostulacion(Postulaciones postulacion) {
        this.postulacion = postulacion;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
