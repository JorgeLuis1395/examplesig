/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.ComisionpostulacionFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EscalassalarialesFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.NivelesgestionFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.SolicitudescargoFacade;
import org.beans.sfccbdmq.ValoresrequerimientosFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Comisionpostulacion;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Escalassalariales;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.entidades.sfccbdmq.Valoresrequerimientos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "solicitudesCargo")
@ViewScoped
public class SolicitudesCargoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of SolicitudesCargoBean
     */
    public SolicitudesCargoBean() {
    }

    
    private List<Solicitudescargo> solicitudes;
    private List<Valoresrequerimientos> valoraciones;
    private List<Valoresrequerimientos> valoracionesnoespeci;
    @ManagedProperty(value = "#{Postulaciones}")
    private PostulacionesBean postulacionbean;
    private Formulario formulario = new Formulario();
    private Formulario formularioimprimir = new Formulario();
    private String[] diasTrabajo;
    private Nivelesgestion nivelB;
    private List listaCarxOrg;
    private String nomCargoxOrg;
    private Solicitudescargo solicitudcargo;
    private Organigrama organigrama;
    private List<Nivelesgestion> listaNiveles;
    private Organigrama organigramasol;
    private Cargosxorganigrama cargo;
    private Escalassalariales salarioB;
    private List<Escalassalariales> listaEscalas;
    private String apellidos;
    private String codigocargo;
    private Empleados empleado;
    private Cargosxorganigrama cargosolicitante;
    private List listadoempleados;
    private List listadomiembros;
    private String miembros;
    private Empleados miembro;
    private List<Comisionpostulacion> comision;
    private List<Comisionpostulacion> comisionB;
    private Comisionpostulacion miembrocomision;
    private Formulario formularioComision = new Formulario();
    @EJB
    private SolicitudescargoFacade ejbSolicitudCargo;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private ValoresrequerimientosFacade ejbValoraciones;
    @EJB
    private HistorialcargosFacade ejbHistorialcargo;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private CargosxorganigramaFacade ejbCargosOrganigrama;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private NivelesgestionFacade ejbNiveles;
    @EJB
    private EscalassalarialesFacade ejbEscalas;
    @EJB
    private ComisionpostulacionFacade ejbComision;
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
    }
    // fin perfiles

    public String buscar() {
        
        try {
            Map parametros = new HashMap();
            String where = "o.activo=true";

        
            if (getCodigocargo() != null && !getCodigocargo().isEmpty()) {
                where += " and upper(o.cargovacante.cargo.codigo)like:cod";
                parametros.put("cod", getCodigocargo().toUpperCase() + "%");
            }

            if (getNivelB() != null) {
                where += " and o.cargovacante.cargo.nivel=:nil";
                parametros.put("nil", getNivelB());
            }

            if (getSalarioB() != null) {
                where += " and o.cargovacante.cargo.escalasalarial=:esc";
                parametros.put("esc", getSalarioB());
            }
            if (getCargo() != null) {
                where += " and o.cargovacante=:cargo";
                parametros.put("cargo", getCargo());
            }
            parametros.put(";where", where);
            solicitudes = ejbSolicitudCargo.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public void cambiaCargosxOrganigrama(ValueChangeEvent event) {
//        setCargo(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargosxorganigrama> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true ";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCargosOrganigrama.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaCarxOrg(new ArrayList());
//            for (Cargosxorganigrama c : aux) {
//                //cargoCor = c;
//                SelectItem s = new SelectItem(c, c.getCargo().getNombre());
//                getListaCarxOrg().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setCargo((Cargosxorganigrama) autoComplete.getSelectedItem().getValue());
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = getListaCarxOrg().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaCarxOrg().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargosxorganigrama) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    setCargo(tmp);
//                }
//            }
//        }
//    }

    public SelectItem[] getComboNivelesGestion() {

        
        try {
            Map parametros = new HashMap();
            String where = "  o.activo=true";
            parametros.put(";where", where);
            listaNiveles = ejbNiveles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Combos.getSelectItems(getListaNiveles(), true);
    }

    
    public void cambiaOrganigrama(ValueChangeEvent event) {
        organigrama = (Organigrama) event.getNewValue();
    }

    public Solicitudescargo traer(Integer id) throws ConsultarException {
        return ejbSolicitudCargo.find(id);
    }

    public SelectItem[] getComboSolcitudesVig() {
        return Combos.getSelectItems(ejbSolicitudCargo.solicitudesvigentes(), true);
    }

    public String crear() {
        // se deberia chequear perfil?
        

        if (cargo == null) {
            MensajesErrores.advertencia("Elija un cargo");
            return null;
        }
        solicitudcargo = new Solicitudescargo();
        solicitudcargo.setSolicitud(new Date());
        solicitudcargo.setCargovacante(cargo);
        setCargosolicitante(new Cargosxorganigrama());
//        setEmpleado(new Empleados());
        String dias = "[1, 2, 3, 4, 5]";
        diasTrabajo = dias.replace("[", "").replace("]", "").split(", ");
        formulario.insertar();
        return null;
    }

    public String cancelar() {
        return "Rrhh.jsf?faces-redirect=true";
    }

    public boolean validar() {
//   
//   if(cargosolicitante==null){
//   MensajesErrores.advertencia("Ingrese el cargo del solicitante");
//   return true;
//   }
//   
//   
        if (cargo == null) {
            MensajesErrores.advertencia("Ingrese el cargo vacante");
            return true;
        }
//   System.out.println(Arrays.toString(diasTrabajo));

        if (solicitudcargo.getMotivovacante() == null || solicitudcargo.getMotivovacante().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el motivo de la vacante");
            return true;
        }

        if (solicitudcargo.getFechageneracionvacante() == null) {
            MensajesErrores.advertencia("Ingrese fecha generación de la vacante");
            return true;
        }

        if (solicitudcargo.getNumerovacantes() == null) {
            MensajesErrores.advertencia("Ingrese el número de vacantes por cubrir");
            return true;
        }

//   if(solicitudcargo.getNumerovacantes()> cargo.getPlazas()-ejbHistorialcargo.cargosocupados(cargo)){
//       MensajesErrores.advertencia("Plazas no disponibles para  cargo: "+cargo.getNombre()+". Plazas Max."+ cargo.getPlazas()+". Cargos ocupados: "+ ejbHistorialcargo.cargosocupados(cargo));
//       return true;
//   }
        if (solicitudcargo.getEstadocivil() == null) {
            MensajesErrores.advertencia("Ingrese el estado civil");
            return true;
        }

        if (solicitudcargo.getSexorequerido() == null) {
            MensajesErrores.advertencia("Ingrese el sexo");
            return true;
        }

        if (solicitudcargo.getDisponibilidad() == null) {
            MensajesErrores.advertencia("Ingrese la disponibilidad del puesto");
            return true;
        }

        if (solicitudcargo.getHorarioentradatrabajo() == null) {
            MensajesErrores.advertencia("Ingrese la hora de entrada");
            return true;
        }

        if (solicitudcargo.getHorariosalidatrabajo() == null) {
            MensajesErrores.advertencia("Ingrese la hora de salida");
            return true;
        }

//      if(solicitudcargo.getHorarioentradatrabajo().after(solicitudcargo.getHorariosalidatrabajo())){
//          MensajesErrores.advertencia("Hora entrada mayor a hora salida");
//          return true;
//      }
        if (solicitudcargo.getLugartrabajo() == null || solicitudcargo.getLugartrabajo().isEmpty()) {
            MensajesErrores.advertencia("Ingrese el lugar de trabajo");
            return true;
        }

//  if(solicitudcargo.getLugartrabajo()==null || solicitudcargo.getLugartrabajo().isEmpty()){
//    MensajesErrores.advertencia("Ingrese el lugar de trabajo");
//   return true;
//   }
        if (diasTrabajo.length == 0) {
            MensajesErrores.advertencia("Ingrese los días de Horario");
            return true;
        } else {
            solicitudcargo.setDiastrabajo(Arrays.toString(diasTrabajo));
        }

        if (solicitudcargo.getSolicitud() == null) {
            solicitudcargo.setVigente(Boolean.FALSE);
        }

        return false;
    }

    public String insertar() {
        
        if (validar()) {
            return null;
        }
        try {
            solicitudcargo.setCargosolicitante(getCargosolicitante());
            solicitudcargo.setCargovacante(getCargo());
            solicitudcargo.setActivo(Boolean.TRUE);
            if (empleado != null) {
                solicitudcargo.setRecomendadopor(empleado);
            }
            ejbSolicitudCargo.create(solicitudcargo, parametrosSeguridad.getLogueado().getUserid());
            for (Comisionpostulacion cp : comision) {
                if (cp.getId() == null) {
                    cp.setSolicitud(solicitudcargo);
                    ejbComision.create(cp, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
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
            solicitudcargo.setCargosolicitante(getCargosolicitante());
            solicitudcargo.setCargovacante(getCargo());
            if (empleado != null) {
                solicitudcargo.setRecomendadopor(empleado);
            }

            // solicitudcargo.setActivo(Boolean.TRUE);  
            ejbSolicitudCargo.edit(solicitudcargo, parametrosSeguridad.getLogueado().getUserid());
            for (Comisionpostulacion cp : comision) {
                if (cp.getId() == null) {
                    cp.setSolicitud(solicitudcargo);
                    ejbComision.create(cp, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbComision.edit(cp, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Comisionpostulacion cpb : comisionB) {
                if (cpb.getId() != null) {
                    cpb.setActivo(Boolean.FALSE);
                    ejbComision.edit(cpb, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        buscar();
        return null;
    }

    private String buscarDetalles() {
        organigramasol = solicitudcargo.getCargosolicitante().getOrganigrama();
        setCargosolicitante(solicitudcargo.getCargosolicitante());
        organigrama = solicitudcargo.getCargovacante().getOrganigrama();
        setCargo(solicitudcargo.getCargovacante());

        if (solicitudcargo.getRecomendadopor() != null) {
            setEmpleado(solicitudcargo.getRecomendadopor());
            apellidos = empleado.getEntidad().getApellidos() + " " + empleado.getEntidad().getNombres();
        }
        diasTrabajo = solicitudcargo.getDiastrabajo().replace("[", "").replace("]", "").split(", ");
        return null;
    }

    private String buscarCargoDetalles() {
        setValoraciones(ejbValoraciones.formacionesacademicascargo(getCargo().getCargo()));
//          setValoracionesnoespeci(ejbValoraciones.formacionnoespecificoscargo(cargo));
        return null;
    }

    public String modificar() {
        
        solicitudcargo = solicitudes.get(formulario.getFila().getRowIndex());
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.solicitud=:solicitud and o.activo=true ");
            parametros.put("solicitud", solicitudcargo);
            comision = ejbComision.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        comisionB = new LinkedList<>();
        buscarDetalles();
        formulario.editar();
        return null;
    }

    public String imprimir() {
        solicitudcargo = solicitudes.get(formulario.getFila().getRowIndex());
        buscarDetalles();
        buscarCargoDetalles();
        formularioimprimir.editar();
        return null;
    }

    public String salirimprimir() {
        formularioimprimir.cancelar();
        return null;
    }

    public String eliminar() {
        solicitudcargo = solicitudes.get(formulario.getFila().getRowIndex());
        buscarDetalles();
        formulario.eliminar();
        return null;
    }

    public String borrar() {
        
        try {
            solicitudcargo.setActivo(Boolean.FALSE);
            ejbSolicitudCargo.edit(solicitudcargo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }

    public SelectItem[] getComboCargos2() {
        return Combos.getSelectItems(ejbCargos.listarcargos(), true);
    }

    public SelectItem[] getComboCargos() {
        try {
            if (organigrama == null) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.organigrama=:organigrama");
            parametros.put(";orden", "o.cargo.nombre asc");
            parametros.put("organigrama", organigrama);
            return Combos.getSelectItems(ejbCargosOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public void cambiaTodos(ValueChangeEvent event) {
//       
//        setEmpleado(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Empleados> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
////             String where = " (o.rol  like '%1%')";
//            String where = " ";
////            parametros.put("tipo", "%1%");
////            parametros.put("tipo", getTipo());
//            where += "  upper(o.entidad.apellidos) like :pin and o.activo=true ";
//            parametros.put("pin", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.entidad.apellidos");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEmpleados.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListadoempleados(new ArrayList());
//            for (Empleados e : aux) {
//                String campo = "";
//                SelectItem s = new SelectItem(e, e.getEntidad().getApellidos() + " " + e.getEntidad().getNombres());
//                getListadoempleados().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setEmpleado((Empleados) autoComplete.getSelectedItem().getValue());
//            } else {
//                Empleados tmp = null;
//                for (int i = 0, max = getListadoempleados().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListadoempleados().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Empleados) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    setEmpleado(tmp);
//                }
//            }
//
//        }
//    }

    public SelectItem[] getComboEscalas() {
       
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true";
            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            setListaEscalas(ejbEscalas.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(getListaEscalas(), true);
    }

    public SelectItem[] getComboCargosOrga() {
        try {
            if (organigramasol == null) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.organigrama=:organigrama");
            parametros.put(";orden", "o.cargo.nombre asc");
            parametros.put("organigrama", organigramasol);
            return Combos.getSelectItems(ejbCargosOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOrganigramasol() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOrganigrama() {

       
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true ");
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbOrganigrama.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(SolicitudesCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //COMISION
    public String nuevoComision() {
        miembrocomision = new Comisionpostulacion();
        miembro = new Empleados();
        miembros = new String();
        formularioComision.insertar();
        return null;
    }

    public boolean validarComision() {
        if (miembro == null) {
            MensajesErrores.advertencia("Seleccione Empleado");
            return true;
        }
        return false;
    }

    public String insertarComision() {
        if (validarComision()) {
            return null;
        }
        miembrocomision.setActivo(Boolean.TRUE);
        miembrocomision.setEmpleado(miembro);
        comision.add(miembrocomision);
        formularioComision.cancelar();
        return null;
    }

    public String modificarComision() {
        miembrocomision = comision.get(formularioComision.getFila().getRowIndex());
        miembro = miembrocomision.getEmpleado();
        miembros = miembrocomision.getEmpleado().toString();
        formularioComision.setIndiceFila(formularioComision.getFila().getRowIndex());
        formularioComision.editar();
        return null;
    }

    public String grabarComision() {
        comision.set(formularioComision.getIndiceFila(), miembrocomision);
        formularioComision.cancelar();
        return null;
    }

    public String eliminarComision() {
        miembrocomision = comision.get(formularioComision.getFila().getRowIndex());
        miembro = miembrocomision.getEmpleado();
        miembros = miembrocomision.getEmpleado().toString();
        formularioComision.setIndiceFila(formularioComision.getFila().getRowIndex());
        formularioComision.eliminar();
        return null;
    }

    public String borrarComision() {
        comisionB.add(miembrocomision);
        comision.remove(miembrocomision);
        formularioComision.cancelar();
        return null;
    }

//    public void cambiaComision(ValueChangeEvent event) {
//       
//        miembro = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Empleados> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
////             String where = " (o.rol  like '%1%')";
//            String where = " ";
////            parametros.put("tipo", "%1%");
////            parametros.put("tipo", getTipo());
//            where += "  upper(o.entidad.apellidos) like :pin and o.activo=true ";
//            parametros.put("pin", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.entidad.apellidos");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEmpleados.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listadomiembros = new ArrayList();
//            for (Empleados e : aux) {
//                String campo = "";
//                SelectItem s = new SelectItem(e, e.getEntidad().getApellidos() + " " + e.getEntidad().getNombres());
//                getListadomiembros().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                miembro = (Empleados) autoComplete.getSelectedItem().getValue();
//            } else {
//                Empleados tmp = null;
//                for (int i = 0, max = getListadomiembros().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListadomiembros().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Empleados) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    setMiembro(tmp);
//                }
//            }
//        }
//    }

   

    /**
     * @return the solicitudes
     */
    public List<Solicitudescargo> getSolicitudes() {
        return solicitudes;
    }

    /**
     * @param solicitudes the solicitudes to set
     */
    public void setSolicitudes(List<Solicitudescargo> solicitudes) {
        this.solicitudes = solicitudes;
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
     * @return the valoraciones
     */
    public List<Valoresrequerimientos> getValoraciones() {
        return valoraciones;
    }

    /**
     * @param valoraciones the valoraciones to set
     */
    public void setValoraciones(List<Valoresrequerimientos> valoraciones) {
        this.valoraciones = valoraciones;
    }

    /**
     * @return the diasTrabajo
     */
    public String[] getDiasTrabajo() {
        return diasTrabajo;
    }

    /**
     * @param diasTrabajo the diasTrabajo to set
     */
    public void setDiasTrabajo(String[] diasTrabajo) {
        this.diasTrabajo = diasTrabajo;
    }

    /**
     * @return the postulacionbean
     */
    public PostulacionesBean getPostulacionbean() {
        return postulacionbean;
    }

    /**
     * @param postulacionbean the postulacionbean to set
     */
    public void setPostulacionbean(PostulacionesBean postulacionbean) {
        this.postulacionbean = postulacionbean;
    }

    /**
     * @return the formularioimprimir
     */
    public Formulario getFormularioimprimir() {
        return formularioimprimir;
    }

    /**
     * @param formularioimprimir the formularioimprimir to set
     */
    public void setFormularioimprimir(Formulario formularioimprimir) {
        this.formularioimprimir = formularioimprimir;
    }

    /**
     * @return the valoracionesnoespeci
     */
    public List<Valoresrequerimientos> getValoracionesnoespeci() {
        return valoracionesnoespeci;
    }

    /**
     * @param valoracionesnoespeci the valoracionesnoespeci to set
     */
    public void setValoracionesnoespeci(List<Valoresrequerimientos> valoracionesnoespeci) {
        this.valoracionesnoespeci = valoracionesnoespeci;
    }

    

    /**
     * @return the organigrama
     */
    public Organigrama getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    /**
     * @return the cargo
     */
    public Cargosxorganigrama getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargosxorganigrama cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the cargosolicitante
     */
    public Cargosxorganigrama getCargosolicitante() {
        return cargosolicitante;
    }

    /**
     * @param cargosolicitante the cargosolicitante to set
     */
    public void setCargosolicitante(Cargosxorganigrama cargosolicitante) {
        this.cargosolicitante = cargosolicitante;
    }

    /**
     * @return the listadoempleados
     */
    public List getListadoempleados() {
        return listadoempleados;
    }

    /**
     * @param listadoempleados the listadoempleados to set
     */
    public void setListadoempleados(List listadoempleados) {
        this.listadoempleados = listadoempleados;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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
     * @return the organigramasol
     */
    public Organigrama getOrganigramasol() {
        return organigramasol;
    }

    /**
     * @param organigramasol the organigramasol to set
     */
    public void setOrganigramasol(Organigrama organigramasol) {
        this.organigramasol = organigramasol;
    }

    /**
     * @return the codigocargo
     */
    public String getCodigocargo() {
        return codigocargo;
    }

    /**
     * @param codigocargo the codigocargo to set
     */
    public void setCodigocargo(String codigocargo) {
        this.codigocargo = codigocargo;
    }

    /**
     * @return the listaNiveles
     */
    public List<Nivelesgestion> getListaNiveles() {
        return listaNiveles;
    }

    /**
     * @param listaNiveles the listaNiveles to set
     */
    public void setListaNiveles(List<Nivelesgestion> listaNiveles) {
        this.listaNiveles = listaNiveles;
    }

    /**
     * @return the nivelB
     */
    public Nivelesgestion getNivelB() {
        return nivelB;
    }

    /**
     * @param nivelB the nivelB to set
     */
    public void setNivelB(Nivelesgestion nivelB) {
        this.nivelB = nivelB;
    }

    /**
     * @return the salarioB
     */
    public Escalassalariales getSalarioB() {
        return salarioB;
    }

    /**
     * @param salarioB the salarioB to set
     */
    public void setSalarioB(Escalassalariales salarioB) {
        this.salarioB = salarioB;
    }

    /**
     * @return the listaEscalas
     */
    public List<Escalassalariales> getListaEscalas() {
        return listaEscalas;
    }

    /**
     * @param listaEscalas the listaEscalas to set
     */
    public void setListaEscalas(List<Escalassalariales> listaEscalas) {
        this.listaEscalas = listaEscalas;
    }

    /**
     * @return the listaCarxOrg
     */
    public List getListaCarxOrg() {
        return listaCarxOrg;
    }

    /**
     * @param listaCarxOrg the listaCarxOrg to set
     */
    public void setListaCarxOrg(List listaCarxOrg) {
        this.listaCarxOrg = listaCarxOrg;
    }

    /**
     * @return the nomCargoxOrg
     */
    public String getNomCargoxOrg() {
        return nomCargoxOrg;
    }

    /**
     * @param nomCargoxOrg the nomCargoxOrg to set
     */
    public void setNomCargoxOrg(String nomCargoxOrg) {
        this.nomCargoxOrg = nomCargoxOrg;
    }

    /**
     * @return the miembros
     */
    public String getMiembros() {
        return miembros;
    }

    /**
     * @param miembros the miembros to set
     */
    public void setMiembros(String miembros) {
        this.miembros = miembros;
    }

    /**
     * @return the listadomiembros
     */
    public List getListadomiembros() {
        return listadomiembros;
    }

    /**
     * @param listadomiembros the listadomiembros to set
     */
    public void setListadomiembros(List listadomiembros) {
        this.listadomiembros = listadomiembros;
    }

    /**
     * @return the miembro
     */
    public Empleados getMiembro() {
        return miembro;
    }

    /**
     * @param miembro the miembro to set
     */
    public void setMiembro(Empleados miembro) {
        this.miembro = miembro;
    }

    /**
     * @return the comision
     */
    public List<Comisionpostulacion> getComision() {
        return comision;
    }

    /**
     * @param comision the comision to set
     */
    public void setComision(List<Comisionpostulacion> comision) {
        this.comision = comision;
    }

    /**
     * @return the comisionB
     */
    public List<Comisionpostulacion> getComisionB() {
        return comisionB;
    }

    /**
     * @param comisionB the comisionB to set
     */
    public void setComisionB(List<Comisionpostulacion> comisionB) {
        this.comisionB = comisionB;
    }

    /**
     * @return the miembrocomision
     */
    public Comisionpostulacion getMiembrocomision() {
        return miembrocomision;
    }

    /**
     * @param miembrocomision the miembrocomision to set
     */
    public void setMiembrocomision(Comisionpostulacion miembrocomision) {
        this.miembrocomision = miembrocomision;
    }

    /**
     * @return the formularioComision
     */
    public Formulario getFormularioComision() {
        return formularioComision;
    }

    /**
     * @param formularioComision the formularioComision to set
     */
    public void setFormularioComision(Formulario formularioComision) {
        this.formularioComision = formularioComision;
    }

}
