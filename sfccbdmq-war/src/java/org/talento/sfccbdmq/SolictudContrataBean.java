/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.seguridad.sfccbdmq.ConfiguracionBean;
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
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Historialcargos;
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


@ManagedBean(name = "solictudcontrata")
@ViewScoped
public class SolictudContrataBean implements Serializable {

    @ManagedProperty(value = "#{calculoCalificacion}")
    private CalculoCalificacionBean calculoCalificacionBean;
    private Cargosxorganigrama cargo;
    private Historialcargos historial;
    private Formulario formulario = new Formulario();
    private Formulario formularioimprimir = new Formulario();
    private Postulaciones postulacion;
    private Solicitudescargo solicitudcargo;
    private LazyDataModel<Postulaciones> postulaciones;
    private String nomCargoxOrg;
    private List listaCarxOrg;
    private String buscarCedula;
    private String buscarNombre;
    private String nombre;
    @EJB
    private CargosxorganigramaFacade ejbCargosOrganigrama;
    @EJB
    private HistorialcargosFacade ejbHistorialCargo;
    @EJB
    private PostulacionesFacade ejbPostulaciones;
    @EJB
    private EmpleadosFacade ejbEmpleados;

    private static final long serialVersionUID = 1L;
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

    /**
     * Creates a new instance of solictudcontrataBean
     */
    public SolictudContrataBean() {
        postulaciones = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {

        if (cargo == null) {
            MensajesErrores.advertencia("Indique el Cargo de la Solicitud");
            return null;
        }

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

                if (getCargo() != null) {
                    where += " and o.solicitudcargo.cargovacante=:cargo";
                    parametros.put("cargo", getCargo());
                }

                if (getBuscarCedula() != null && !buscarCedula.isEmpty()) {
                    where += " and upper(o.empleado.entidad.pin)=:pin";
                    parametros.put("pin", getBuscarCedula());
                }

                if (getNombre() != null && !nombre.isEmpty()) {
                    where += " and upper(o.empleado.entidad.nombres)like:nom";
                    parametros.put("nom", "%" + getNombre().toUpperCase() + "%");
                }

                if (getBuscarNombre() != null && !buscarNombre.isEmpty()) {
                    where += " and upper(o.empleado.entidad.apellidos)like:apellidos";
                    parametros.put("apellidos", "%" + getBuscarNombre().toUpperCase() + "%");
                }

                if (getSolicitudcargo() != null) {
                    where += " and o.solicitudcargo=:solicitud";
                    parametros.put("solicitud", getSolicitudcargo());
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
                getPostulaciones().setRowCount(total);

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
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true";
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
//            listaCarxOrg = new ArrayList();
//            for (Cargosxorganigrama c : aux) {
//                //cargoCor = c;
//                SelectItem s = new SelectItem(c, c.getCargo().getNombre());
//                listaCarxOrg.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setCargo((Cargosxorganigrama) autoComplete.getSelectedItem().getValue());
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = listaCarxOrg.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaCarxOrg.get(i);
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

    public boolean validacion() {
        if (postulacion.getValidacionmerito() == false) {
            MensajesErrores.advertencia("No existe evaluación méritos.");
            return true;
        }
        //Regresa la  calificacion por Meritos
//        if (calculoCalificacionBean.getTiposseleccion().getNotaminima().doubleValue() > calculoCalificacionBean.CalificacionMeritos(postulacion)) {
//            MensajesErrores.advertencia("La calificación méritos no supera a califición mínima: " + calculoCalificacionBean.getTiposseleccion().getNotaminima().doubleValue());
//            return true;
//        }
        if (calculoCalificacionBean.getCalificacionmeritos() <= calculoCalificacionBean.getMinimoMeritos()) {
//           MensajesErrores.advertencia("La calificación de méritos es de " +calculoCalificacionBean.getCalificacionmeritos()+ " no supera a calificación mínima de " + calculoCalificacionBean.getMinimoMeritos());
            MensajesErrores.advertencia("La calificación de méritos no supera a calificación mínima requerida ");
            return true;
        }
        //Validación de notas de oposición
        if (calculoCalificacionBean.getCalificacionoposicionTotal() <= calculoCalificacionBean.getMinimoOposicion()) {
//           MensajesErrores.advertencia("La calificación de oposición es de "+calculoCalificacionBean.getCalificacionoposicionTotal()+" no supera a califición mínima de " + calculoCalificacionBean.getMinimoOposicion());
            MensajesErrores.advertencia("La calificación de oposición no supera a calificación mínima requerida ");
            return true;
        }
//        if(calculoCalificacionBean.CalificacionOposicion(postulacion)<= calculoCalificacionBean.getMinimoOposicion()){
//           MensajesErrores.advertencia("La calificación de oposición no supera a califición mínima: " + calculoCalificacionBean.getMinimoOposicion());
//            return true;
//        }
        return false;
    }

    public String contratar(Postulaciones postulaciones) {
        postulacion = postulaciones;
        if (validacion()) {
            return null;
        }
//        List<Historialcargos> histocar = ejbHistorialCargo.historialpostulacion(postulacion);
//        if (!histocar.isEmpty()) {
//            for (Historialcargos his : histocar) {
//                historial = his;
//            }
//            formulario.editar();
//        } else {
//            historial = new Historialcargos();
//            historial.setPostulacion(postulacion);
//            historial.setActivo(Boolean.TRUE);
//            historial.setCargo(postulaciones.getSolicitudcargo().getCargovacante());
//            historial.setEmpleado(postulaciones.getEmpleado());
//            formulario.insertar();
//        }
        return null;
    }

    public boolean validar() {

        historial.setSueldobase(postulacion.getSolicitudcargo().getCargovacante().getCargo().getEscalasalarial().getSueldobase());
//        if (historial.getSueldobase() == null) {
//            MensajesErrores.advertencia("Ingrese sueldo base");
//            return true;
//        }

        if (historial.getDesde() == null) {
            MensajesErrores.advertencia("Ingrese fecha desde");
            return true;
        }

        if (historial.getHasta() == null) {
            MensajesErrores.advertencia("Ingrese fecha hasta");
            return true;
        }
        if (historial.getDesde().after(historial.getHasta()) == true) {
            MensajesErrores.advertencia("Fecha desde no mayor a la fecha hasta");
            return true;
        }

        return false;
    }

    public String insertarCargo() {
        if (validar()) {
            return null;
        }
        try {
            postulacion.getEmpleado().setCargoactual(postulacion.getSolicitudcargo().getCargovacante());
            postulacion.getEmpleado().setFechaingreso(historial.getDesde());
            postulacion.getEmpleado().setFechasalida(historial.getHasta());
            postulacion.getEmpleado().setActivo(Boolean.TRUE);
            ejbEmpleados.edit(postulacion.getEmpleado(), parametrosSeguridad.getLogueado().getUserid());
//            historial.setPostulacion(postulacion);
            historial.setActivo(Boolean.TRUE);
            historial.setVigente(Boolean.TRUE);
            ejbHistorialCargo.create(historial, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(ContratacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    public String grabarCargo() {
        if (validar()) {
            return null;
        }
        try {
            postulacion.getEmpleado().setCargoactual(postulacion.getSolicitudcargo().getCargovacante());
            postulacion.getEmpleado().setFechaingreso(historial.getDesde());
            postulacion.getEmpleado().setFechasalida(historial.getHasta());
            postulacion.getEmpleado().setActivo(Boolean.TRUE);
//            historial.setPostulacion(postulacion);
            ejbEmpleados.edit(postulacion.getEmpleado(), parametrosSeguridad.getLogueado().getUserid());
            ejbHistorialCargo.edit(historial, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ContratacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
    }

    public String salir() {
        return "BlancoVista.jsf?faces-redirect=true";
    }

    /**
     * @return the calculoCalificacionBean
     */
    public CalculoCalificacionBean getCalculoCalificacionBean() {
        return calculoCalificacionBean;
    }

    /**
     * @param calculoCalificacionBean the calculoCalificacionBean to set
     */
    public void setCalculoCalificacionBean(CalculoCalificacionBean calculoCalificacionBean) {
        this.calculoCalificacionBean = calculoCalificacionBean;
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
     * @return the historial
     */
    public Historialcargos getHistorial() {
        return historial;
    }

    /**
     * @param historial the historial to set
     */
    public void setHistorial(Historialcargos historial) {
        this.historial = historial;
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
