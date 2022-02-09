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
import org.beans.sfccbdmq.CalificacionesoposicionFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.EntrevistasFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.beans.sfccbdmq.TiposseleccionFacade;
import org.entidades.sfccbdmq.Calificacionesempleado;
import org.entidades.sfccbdmq.Calificacionesoposicion;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Detallesoposicion;
import org.entidades.sfccbdmq.Entrevistas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Postulaciones;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.entidades.sfccbdmq.Tiposseleccion;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "calculoCalificacion")
@ViewScoped
public class CalculoCalificacionBean {

    @ManagedProperty(value = "#{datosLogueo}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{calificacionPostulacion}")
    private CalificacionPostulacionBean califposbean;
    private Postulaciones postulacion;
    private Formulario formulario = new Formulario();
    private Formulario formularioimprimir = new Formulario();
    private Tiposseleccion tiposseleccion;
    private List<Calificacionesempleado> calfemple;
    private Double calificacionmeritos;
    private LazyDataModel<Postulaciones> postulaciones;
    private Double calificacionmeritospondetotal;
    private List<Calificacionesoposicion> calfop;
    private Double calificacionoposicionEntrevista;
    private Double calificacionoposicionPrueba;
    private Double calificacionoposicionpondetotal;
    private Double calificacionoposicionTotal;
    private Entrevistas entrevista;
    private Double ponderacionMeritos;
    private Double ponderacionOposicion;
    private Double ponderacionTotal;
    private Double minimoMeritos;
    private Double minimoOposicion;
    private boolean vacioMeritos = Boolean.FALSE;
    private boolean vacioOposicion = Boolean.FALSE;
    private String msjMeritos;
    private String msjOposicion;
    private String nomCargoxOrg;
    private List listaCarxOrg;
    private String buscarCedula;
    private String buscarNombre;
    private String nombre;
    private Solicitudescargo solicitudcargo;
    private Cargosxorganigrama cargo;
    @EJB
    private CargosxorganigramaFacade ejbCargosOrganigrama;
    @EJB
    private CalificacionesempleadoFacade ejbCalificaciones;
    @EJB
    private TiposseleccionFacade ejbTiposseleccion;
    @EJB
    private CalificacionesoposicionFacade ejbCalificacionesO;
    @EJB
    private EntrevistasFacade ejbEntrevista;
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
    }

    // fin perfiles

    /**
     * Creates a new instance of CalculoMeritosBean
     */
    public CalculoCalificacionBean() {
//       institucion=seguridadbean.getInstitucion();
        postulaciones = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };

    }

    public String salir() {
        return "BlancoVista.jsf?faces-redirect=true";
    }

    public String salirimprimir() {
        formulario.cancelar();
        formularioimprimir.cancelar();
        return null;
    }

    //Aqui solo se calcula por el momento Calificacion por Meritos y es para ver  un informe
    public String informeCalificaciones(Postulaciones postula) {
        postulacion = postula;
        califposbean.setPostulacion(postulacion);
        CalcularCalificacionesTotalMeritos();
        CalcularCalificacionesTotalOposicion();
        formulario.insertar();
        formularioimprimir.insertar();
        return null;
    }

    //Solo para calcular calificacion  
    public String informeCalificaciones2(Postulaciones postula) {
        postulacion = postula;
        califposbean.setPostulacion(postulacion);
        //CalcularCalificacionesTotalMeritos();
        return null;
    }

    public String CalcularCalificacionesTotalMeritos() {
        try {
            calfemple = new LinkedList<>();
            calificacionmeritospondetotal = 0.0;
            calificacionmeritos = 0.0;
            minimoMeritos = 0.0;
            ponderacionMeritos = 0.0;
            Double notamaximaarea = 0.0;
            Double notamaximatipos = 0.0;
            Double notamaximaobtenida = 0.0;

            //En tabla calificacaciones solo voy a guardar de un tipo= Meritos
//         for(Tiposseleccion tp: tiposseleccion){
            Map parametros = new HashMap();
            parametros.put(";orden", "o.area.nombre asc");
            parametros.put(";where", "o.postulacion=:postulacion  and o.area.activo=true  and o.area is not null");
            parametros.put("postulacion", postulacion);

            if (!ejbCalificaciones.encontarParametros(parametros).isEmpty()) {
//            parametros.put("tipo", tp);
                for (Calificacionesempleado cl : ejbCalificaciones.encontarParametros(parametros)) {
                    //Nota maxima del tipo
                    notamaximaarea += cl.getArea().getNotamaxima().doubleValue();
//                 Nota Obtenida Maxima
                    notamaximaobtenida += cl.getValor().doubleValue();
                    calfemple.add(cl);
                    setTiposseleccion(cl.getArea().getTipo());
                }
//         }
                calificacionmeritos = (tiposseleccion.getNotamaxima().doubleValue() * notamaximaobtenida) / notamaximaarea;
//           notamaximatipos+=tp.getNotamaxima().doubleValue();
                calificacionmeritospondetotal = (tiposseleccion.getPonderacion().doubleValue() * calificacionmeritos) / tiposseleccion.getNotamaxima().doubleValue();
                ponderacionMeritos = tiposseleccion.getPonderacion().doubleValue();
                minimoMeritos = tiposseleccion.getNotaminima().doubleValue();
                postulacion.setTotalMeritos(calificacionmeritospondetotal);
            } else {
                vacioMeritos = true;
                msjMeritos = "No existen registros de calificación de Méritos";
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CalculoCalificacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Double CalificacionMeritos(Postulaciones postula) {
        postulacion = postula;
        informeCalificaciones2(postula);
        CalcularCalificacionesTotalMeritos();
        if (calificacionmeritospondetotal == 0.0) {
            vacioMeritos = true;
        }
        return Math.rint(calificacionmeritospondetotal * 100) / 100;
    }

//OPOSICION
    public Double CalificacionesOposicion(Detallesoposicion detalle) {
        Double nota = 0.0;
        if (detalle == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.detalle.area.nombre asc");
            parametros.put(";where", "o.postulacion=:postulacion  and o.detalle.area.activo=true and o.detalle=:detalle");
            parametros.put("postulacion", postulacion);
            parametros.put("detalle", detalle);
            List<Calificacionesoposicion> aux = ejbCalificacionesO.encontarParametros(parametros);
            if (!ejbCalificacionesO.encontarParametros(parametros).isEmpty()) {
                for (Calificacionesoposicion co : ejbCalificacionesO.encontarParametros(parametros)) {
                    nota += co.getValor().doubleValue();
                }
            }
            parametros = new HashMap();
            parametros.put(";where", "o.postulacion=:postulacion");
            parametros.put("postulacion", postulacion);
            for (Entrevistas en : ejbEntrevista.encontarParametros(parametros)) {
                entrevista = en;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CalculoCalificacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Math.rint(nota * 100) / 100;
    }

    public String CalcularCalificacionesTotalOposicion() {
        try {
            calfop = new LinkedList<>();
            calificacionoposicionTotal = 0.0;
            calificacionoposicionpondetotal = 0.0;
            calificacionoposicionEntrevista = 0.0;
            calificacionoposicionPrueba = 0.0;
            ponderacionOposicion = 0.0;
            minimoOposicion = 0.0;
            Map parametros = new HashMap();
            parametros.put(";orden", "o.detalle.area.nombre asc");
            parametros.put(";where", "o.postulacion=:postulacion  and o.detalle.area.activo=true");
            parametros.put("postulacion", postulacion);
            List<Calificacionesoposicion> aux = ejbCalificacionesO.encontarParametros(parametros);
            if (!ejbCalificacionesO.encontarParametros(parametros).isEmpty()) {
                for (Calificacionesoposicion co : ejbCalificacionesO.encontarParametros(parametros)) {
                    if (!co.getDetalle().getArea().getPrueba()) {
                        calificacionoposicionEntrevista += co.getValor().doubleValue();
                        calfop.add(co);
                    } else {
                        calificacionoposicionPrueba += co.getValor().doubleValue();
                        calfop.add(co);
                    }
                    setTiposseleccion(co.getDetalle().getArea().getTipo());
                }
                calificacionoposicionTotal = calificacionoposicionEntrevista + calificacionoposicionPrueba;
                calificacionoposicionpondetotal = (tiposseleccion.getPonderacion().doubleValue() * calificacionoposicionTotal) / tiposseleccion.getNotamaxima().doubleValue();
                ponderacionOposicion = tiposseleccion.getPonderacion().doubleValue();
                minimoOposicion = tiposseleccion.getNotaminima().doubleValue();
                postulacion.setTotalOposicion(calificacionoposicionpondetotal);
            } else {
                vacioOposicion = true;
                msjOposicion = "No existen registros de Entrevista y/o Pruebas de Oposición";
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CalculoCalificacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Double CalificacionOposicion(Postulaciones postula) {
        postulacion = postula;
        CalcularCalificacionesTotalOposicion();
        if (calificacionoposicionpondetotal == 0.0) {
            vacioOposicion = true;
        }
        return Math.rint(calificacionoposicionpondetotal * 100) / 100;
    }

    public Double TotalConcurso() {
        CalcularCalificacionesTotalOposicion();
        CalcularCalificacionesTotalMeritos();
        ponderacionTotal = ponderacionMeritos + ponderacionOposicion;
        return Math.rint((calificacionmeritospondetotal + calificacionoposicionpondetotal) * 100) / 100;
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

    public String buscar() {

//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
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
     * @return the calificacionmeritos
     */
    public Double getCalificacionmeritos() {
        return Math.rint(calificacionmeritos * 100) / 100;
    }

    /**
     * @param calificacionmeritos the calificacionmeritos to set
     */
    public void setCalificacionmeritos(Double calificacionmeritos) {
        this.calificacionmeritos = calificacionmeritos;
    }

    /**
     * @return the califposbean
     */
    public CalificacionPostulacionBean getCalifposbean() {
        return califposbean;
    }

    /**
     * @param califposbean the califposbean to set
     */
    public void setCalifposbean(CalificacionPostulacionBean califposbean) {
        this.califposbean = califposbean;
    }

    /**
     * @return the calificacionmeritospondetotal
     */
    public Double getCalificacionmeritospondetotal() {
        return Math.rint(calificacionmeritospondetotal * 100) / 100;
    }

    /**
     * @param calificacionmeritospondetotal the calificacionmeritospondetotal to
     * set
     */
    public void setCalificacionmeritospondetotal(Double calificacionmeritospondetotal) {
        this.calificacionmeritospondetotal = calificacionmeritospondetotal;
    }

    /**
     * @return the tiposseleccion
     */
    public Tiposseleccion getTiposseleccion() {
        return tiposseleccion;
    }

    /**
     * @param tiposseleccion the tiposseleccion to set
     */
    public void setTiposseleccion(Tiposseleccion tiposseleccion) {
        this.tiposseleccion = tiposseleccion;
    }

    /**
     * @return the calfop
     */
    public List<Calificacionesoposicion> getCalfop() {
        return calfop;
    }

    /**
     * @param calfop the calfop to set
     */
    public void setCalfop(List<Calificacionesoposicion> calfop) {
        this.calfop = calfop;
    }

    /**
     * @return the calificacionoposicionpondetotal
     */
    public Double getCalificacionoposicionpondetotal() {
        return calificacionoposicionpondetotal;
    }

    /**
     * @param calificacionoposicionpondetotal the
     * calificacionoposicionpondetotal to set
     */
    public void setCalificacionoposicionpondetotal(Double calificacionoposicionpondetotal) {
        this.calificacionoposicionpondetotal = calificacionoposicionpondetotal;
    }

    /**
     * @return the calificacionoposicionEntrevista
     */
    public Double getCalificacionoposicionEntrevista() {
        return calificacionoposicionEntrevista;
    }

    /**
     * @param calificacionoposicionEntrevista the
     * calificacionoposicionEntrevista to set
     */
    public void setCalificacionoposicionEntrevista(Double calificacionoposicionEntrevista) {
        this.calificacionoposicionEntrevista = calificacionoposicionEntrevista;
    }

    /**
     * @return the calificacionoposicionPrueba
     */
    public Double getCalificacionoposicionPrueba() {
        return calificacionoposicionPrueba;
    }

    /**
     * @param calificacionoposicionPrueba the calificacionoposicionPrueba to set
     */
    public void setCalificacionoposicionPrueba(Double calificacionoposicionPrueba) {
        this.calificacionoposicionPrueba = calificacionoposicionPrueba;
    }

    /**
     * @return the calificacionoposicionTotal
     */
    public Double getCalificacionoposicionTotal() {
        return Math.rint(calificacionoposicionTotal * 100) / 100;
    }

    /**
     * @param calificacionoposicionTotal the calificacionoposicionTotal to set
     */
    public void setCalificacionoposicionTotal(Double calificacionoposicionTotal) {
        this.calificacionoposicionTotal = calificacionoposicionTotal;
    }

    /**
     * @return the entrevista
     */
    public Entrevistas getEntrevista() {
        return entrevista;
    }

    /**
     * @param entrevista the entrevista to set
     */
    public void setEntrevista(Entrevistas entrevista) {
        this.entrevista = entrevista;
    }

    /**
     * @return the ponderacionMeritos
     */
    public Double getPonderacionMeritos() {
        return ponderacionMeritos;
    }

    /**
     * @param ponderacionMeritos the ponderacionMeritos to set
     */
    public void setPonderacionMeritos(Double ponderacionMeritos) {
        this.ponderacionMeritos = ponderacionMeritos;
    }

    /**
     * @return the ponderacionOposicion
     */
    public Double getPonderacionOposicion() {
        return ponderacionOposicion;
    }

    /**
     * @param ponderacionOposicion the ponderacionOposicion to set
     */
    public void setPonderacionOposicion(Double ponderacionOposicion) {
        this.ponderacionOposicion = ponderacionOposicion;
    }

    /**
     * @return the ponderacionTotal
     */
    public Double getPonderacionTotal() {
        return ponderacionTotal;
    }

    /**
     * @param ponderacionTotal the ponderacionTotal to set
     */
    public void setPonderacionTotal(Double ponderacionTotal) {
        this.ponderacionTotal = ponderacionTotal;
    }

    /**
     * @return the minimoMeritos
     */
    public Double getMinimoMeritos() {
        return minimoMeritos;
    }

    /**
     * @param minimoMeritos the minimoMeritos to set
     */
    public void setMinimoMeritos(Double minimoMeritos) {
        this.minimoMeritos = minimoMeritos;
    }

    /**
     * @return the minimoOposicion
     */
    public Double getMinimoOposicion() {
        return minimoOposicion;
    }

    /**
     * @param minimoOposicion the minimoOposicion to set
     */
    public void setMinimoOposicion(Double minimoOposicion) {
        this.minimoOposicion = minimoOposicion;
    }

    /**
     * @return the vacioMeritos
     */
    public boolean isVacioMeritos() {
        return vacioMeritos;
    }

    /**
     * @param vacioMeritos the vacioMeritos to set
     */
    public void setVacioMeritos(boolean vacioMeritos) {
        this.vacioMeritos = vacioMeritos;
    }

    /**
     * @return the vacioOposicion
     */
    public boolean isVacioOposicion() {
        return vacioOposicion;
    }

    /**
     * @param vacioOposicion the vacioOposicion to set
     */
    public void setVacioOposicion(boolean vacioOposicion) {
        this.vacioOposicion = vacioOposicion;
    }

    /**
     * @return the msjMeritos
     */
    public String getMsjMeritos() {
        return msjMeritos;
    }

    /**
     * @param msjMeritos the msjMeritos to set
     */
    public void setMsjMeritos(String msjMeritos) {
        this.msjMeritos = msjMeritos;
    }

    /**
     * @return the msjOposicion
     */
    public String getMsjOposicion() {
        return msjOposicion;
    }

    /**
     * @param msjOposicion the msjOposicion to set
     */
    public void setMsjOposicion(String msjOposicion) {
        this.msjOposicion = msjOposicion;
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
}
