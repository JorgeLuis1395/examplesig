/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.CalificacionesempleadoFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.beans.sfccbdmq.ValoresrequerimientosFacade;
import org.entidades.sfccbdmq.Areasseleccion;
import org.entidades.sfccbdmq.Calificacionesempleado;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Postulaciones;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.entidades.sfccbdmq.Valoresrequerimientos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "calificacionPostulacion")
@ViewScoped
public class CalificacionPostulacionBean {

    @ManagedProperty(value = "#{datosLogueo}")
    private ParametrosSfccbdmqBean seguridadbean;
    private Formulario formulario = new Formulario();
    private List<Calificacionesempleado> calfemple;
    private List<Valoresrequerimientos> listvalores;
    private Postulaciones postulacion;
    private Cargosxorganigrama cargo;
    private LazyDataModel<Postulaciones> postulaciones;
    private Solicitudescargo solicitudcargo;
    private String nomCargoxOrg;
    private List listaCarxOrg;
    private String buscarCedula;
    private String buscarNombre;
    private String nombre;

    @ManagedProperty(value = "#{Postulaciones}")
    private PostulacionesBean postulacionBean;

    private Calificacionesempleado calificacion;
    @EJB
    private PostulacionesFacade ejbPostulaciones;
    @EJB
    private CalificacionesempleadoFacade ejbCalificaciones;

    @EJB
    private ValoresrequerimientosFacade ejbValoresRequerimientos;
    @EJB
    private CargosxorganigramaFacade ejbCargosOrganigrama;

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

    public CalificacionPostulacionBean() {

        postulaciones = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };

    }

    public String calificacionPost(Postulaciones postu) {
        setPostulacion(postu);
        calfemple = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.area.nombre desc");
            parametros.put(";where", "o.postulacion=:postulacion  and o.area.activo=true");
            parametros.put("postulacion", postu);

//          Obtengo todas las calificaciones. Averigar s. hay mas  Areas y si  las hay las  agregamos
            if (!ejbCalificaciones.encontarParametros(parametros).isEmpty()) {

                parametros = new HashMap();
                String where = "o.area.activo=true  and o.cargo=:cargo  and  o.activo=true";
                String campo = "o.area";
                String suma = "count(o.area)";
                parametros.put("cargo", postu.getSolicitudcargo().getCargovacante().getCargo());
                parametros.put(";where", where);
                parametros.put(";campo", campo);
                parametros.put(";suma", suma);

                List<Object[]> resp = ejbValoresRequerimientos.sumar(parametros);

                for (Object[] ar : resp) {
                    Areasseleccion area = (Areasseleccion) ar[0];
                    Calificacionesempleado cali = ejbCalificaciones.CalificacionesAreaEmpleado(getPostulacion(), area);
                    if (cali.getId() == null) {
                        calificacion = new Calificacionesempleado();
                        calificacion.setArea(area);
                        calificacion.setPostulacion(getPostulacion());
                        calificacion.setValor(BigDecimal.ZERO);
                        calfemple.add(calificacion);
                    } else {
                        calfemple.add(cali);
                    }
                }
            } else {

                parametros = new HashMap();
                String where = "o.area.activo=true  and o.cargo=:cargo  and  o.activo=true";
                String campo = "o.area";
                String suma = "count(o.area)";
                parametros.put("cargo", postu.getSolicitudcargo().getCargovacante().getCargo());
                parametros.put(";where", where);
                parametros.put(";campo", campo);
                parametros.put(";suma", suma);

                List<Object[]> resp = ejbValoresRequerimientos.sumar(parametros);

                for (Object[] ar : resp) {
                    Areasseleccion area = (Areasseleccion) ar[0];
                    calificacion = new Calificacionesempleado();
                    calificacion.setArea(area);
                    calificacion.setPostulacion(getPostulacion());
                    calificacion.setValor(BigDecimal.ZERO);
                    calfemple.add(calificacion);
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PostulacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String ListaValoresRequerimiento(Areasseleccion area) {

        String valores = "<ul>";
        listvalores = new LinkedList<>();
        listvalores = ejbValoresRequerimientos.formacionesacademicasarea(area, getPostulacion().getSolicitudcargo().getCargovacante().getCargo());
        for (Valoresrequerimientos vr : listvalores) {
            valores += "<li>" + vr.getValor() + "</li>";
        }
        valores += "</ul>";

        return valores;
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

    public String GrabarCalificaciones() {

        try {
            for (Calificacionesempleado cle : calfemple) {
                if (cle.getValor().doubleValue() > cle.getArea().getNotamaxima().doubleValue()) {
                    MensajesErrores.advertencia("Existen un registro de nota superior al máximo");
                    return null;
                }
                if (cle.getId() == null) {
                    cle.setPostulacion(getPostulacion());
                    ejbCalificaciones.create(cle, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbCalificaciones.edit(cle, seguridadbean.getLogueado().getUserid());
                }
            }

            postulacion.setValidacionmerito(Boolean.TRUE);
            ejbPostulaciones.edit(postulacion, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(CalificacionPostulacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        return null;
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

                if (cargo != null) {
                    where += " and o.solicitudcargo.cargovacante=:cargo";
                    parametros.put("cargo", cargo);
                }

                if (getBuscarCedula() != null && !buscarCedula.isEmpty()) {
                    where += " and upper(o.empleado.entidad.pin)=:pin";
                    parametros.put("pin", getBuscarCedula());
                }

                if (getBuscarNombre() != null && !buscarNombre.isEmpty()) {
                    where += " and upper(o.empleado.entidad.apellidos)like:apellidos";
                    parametros.put("apellidos", "%" + getBuscarNombre().toUpperCase() + "%");
                }

                if (getNombre() != null && !nombre.isEmpty()) {
                    where += " and upper(o.empleado.entidad.nombres)like:nom";
                    parametros.put("nom", "%" + getNombre().toUpperCase() + "%");
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

    public String salir() {
        return "BlancoVista.jsf?faces-redirect=true";
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
     * @return the calfemple
     */
    public List<Calificacionesempleado> getCalfemple() {
        return calfemple;
    }

    /**
     * @param calfemple the calfemple to set
     */
    public void setCalfemple(List<Calificacionesempleado> calfemple) {
        this.calfemple = calfemple;
    }

    /**
     * @return the calificacion
     */
    public Calificacionesempleado getCalificacion() {
        return calificacion;
    }

    /**
     * @param calificacion the calificacion to set
     */
    public void setCalificacion(Calificacionesempleado calificacion) {
        this.calificacion = calificacion;
    }

    /**
     * @return the postulacionBean
     */
    public PostulacionesBean getPostulacionBean() {
        return postulacionBean;
    }

    /**
     * @param postulacionBean the postulacionBean to set
     */
    public void setPostulacionBean(PostulacionesBean postulacionBean) {
        this.postulacionBean = postulacionBean;
    }

    /**
     * @return the listvalores
     */
    public List<Valoresrequerimientos> getListvalores() {
        return listvalores;
    }

    /**
     * @param listvalores the listvalores to set
     */
    public void setListvalores(List<Valoresrequerimientos> listvalores) {
        this.listvalores = listvalores;
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
