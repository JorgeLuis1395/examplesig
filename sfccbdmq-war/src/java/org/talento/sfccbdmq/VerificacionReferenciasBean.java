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
import java.math.BigDecimal;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.ExperienciasFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.beans.sfccbdmq.RecomendacionesFacade;
import org.beans.sfccbdmq.SolicitudescargoFacade;
import org.beans.sfccbdmq.ValoracionesdesempenioFacade;
import org.beans.sfccbdmq.VerificacionesdesempenioFacade;
import org.beans.sfccbdmq.VerificacionesreferenciasFacade;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Experiencias;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Postulaciones;
import org.entidades.sfccbdmq.Recomendaciones;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.entidades.sfccbdmq.Valoracionesdesempenio;
import org.entidades.sfccbdmq.Verificacionesdesempenio;
import org.entidades.sfccbdmq.Verificacionesreferencias;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "verificacionReferencias")
@ViewScoped
public class VerificacionReferenciasBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of VerificacionReferenciasBean
     */
    
    private LazyDataModel<Postulaciones> listaPostulantes;
    private Formulario formulario = new Formulario();
    private Cargosxorganigrama cargo;
    private Postulaciones postulante;
    private List<Valoracionesdesempenio> listaValoraciones;
    private Valoracionesdesempenio valoracion;
    private List<Experiencias> listaExperiencias;
    private Experiencias experiencia;
    private Recomendaciones recomendacion;
    private List<Recomendaciones> listaRecomendaciones;
    private boolean imprimir;
    private Date fecha;
    private Formulario formularioE = new Formulario();
    private Formulario formularioR = new Formulario();
    private Formulario formularioV = new Formulario();
    private List<Verificacionesreferencias> listaVReferencias;
    private List<Verificacionesreferencias> listaVLaborales;
    private List<Verificacionesdesempenio> listaVDesempenio;
    private Verificacionesreferencias laboral;
    private Verificacionesreferencias personal;
    private Verificacionesdesempenio desempenio;
    private List<Integer> listaValores;
    private List listaCarxOrg;
    private String nomCargoxOrg;
    private String ciAsp;
    private String aplAsp;

    @EJB
    private PostulacionesFacade ejbPostulaciones;
    @EJB
    private ValoracionesdesempenioFacade ejbValoraciones;
    @EJB
    private ExperienciasFacade ejbExperiencias;
    @EJB
    private RecomendacionesFacade ejbRecomendaciones;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private SolicitudescargoFacade ejbSolicitudes;
    @EJB
    private VerificacionesdesempenioFacade ejbVDesempenio;
    @EJB
    private VerificacionesreferenciasFacade ejbVReferencias;
    @EJB
    private CargosxorganigramaFacade ejbCarxOrg;

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

    public VerificacionReferenciasBean() {
        listaPostulantes = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
//        if (seguridadbean.getLogueado().getUserid() == null) {
//            MensajesErrores.advertencia("Opción solo para usuarios con rol específico");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.activo=true");
            listaValoraciones = ejbValoraciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaPostulantes = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                String where = "o.id is not null and o.activo=true and o.solicitudcargo.vigente=true";
                //Criterios de busqueda
                //CARGO
                if (getCargo() != null) {
                    where += " and o.solicitudcargo.cargovacante=:cargo";
                    parametros.put("cargo", getCargo());
                }
                parametros.put(";where", where);
                int total = 0;
                try {
                    total = ejbPostulaciones.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                listaPostulantes.setRowCount(total);
                try {
                    return ejbPostulaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo() {
        
        postulante = (Postulaciones) listaPostulantes.getRowData();
        listaExperiencias = new LinkedList<>();
        listaRecomendaciones = new LinkedList<>();
        formulario.insertar();
        imprimir = false;
        laboral = new Verificacionesreferencias();
        personal = new Verificacionesreferencias();
        desempenio = new Verificacionesdesempenio();

        //PARA REFERENCIAS LABORALES
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", postulante.getEmpleado());
            listaExperiencias = ejbExperiencias.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", postulante.getEmpleado());
            listaRecomendaciones = ejbRecomendaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean validar() {
        if (fecha == null) {
            MensajesErrores.advertencia("Indicar la fecha de la entrevista");
            return true;
        }
        return false;
    }

    //REFERENCIAS LABORALES
    public String verificarlaboral(Experiencias exp, int index) {
        //experiencia = listaExperiencias.get(formularioE.getFila().getRowIndex());
        experiencia = exp;
        formularioE.setIndiceFila(index);
        fecha = new Date();
        listaVDesempenio = new LinkedList<>();
        for (Valoracionesdesempenio vd : listaValoraciones) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.experiencia=:experiencia and o.valoracion=:valoracion and o.activo=true");
                parametros.put("experiencia", experiencia);
                parametros.put("valoracion", vd);
                List<Verificacionesdesempenio> aux = ejbVDesempenio.encontarParametros(parametros);
                for (Verificacionesdesempenio valor : aux) {
                    if (valor != null) {
                        listaVDesempenio.add(valor);
                    }
                }
                parametros = new HashMap();
                parametros.put(";where", "o.experiencia=:experiencia and o.activo=true");
                parametros.put("experiencia", experiencia);
                List<Verificacionesreferencias> reflab = ejbVReferencias.encontarParametros(parametros);
                if (reflab.isEmpty()) {
                    laboral = new Verificacionesreferencias();
                    laboral.setExperiencia(experiencia);
                    laboral.setActivo(Boolean.TRUE);
                    laboral.setFecha(fecha);
                } else {
                    for (Verificacionesreferencias valor : reflab) {
                        laboral = valor;
                        fecha = laboral.getFecha();
                    }
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (listaVDesempenio.isEmpty()) {
                for (Valoracionesdesempenio vn : listaValoraciones) {
                    Verificacionesdesempenio n = new Verificacionesdesempenio();
                    n.setExperiencia(experiencia);
                    n.setActivo(Boolean.TRUE);
                    n.setValoracion(vn);
                    listaVDesempenio.add(n);
                }
            }
        }
        formularioE.insertar();
        return null;
    }

    public boolean validarlaboral() {
        if (laboral.getCausadesvinculacion() == null || laboral.getCausadesvinculacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Causas de desvinculación'");
            return true;
        }
        if (laboral.getRecomiendacargo() == null || laboral.getRecomiendacargo().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Si lo recomendaría para el cargo'");
            return true;
        }
        if (laboral.getPuntosdesarrollar() == null || laboral.getPuntosdesarrollar().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Puntos a desarrollar'");
            return true;
        }
        if (laboral.getObservacion() == null || laboral.getObservacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Observaciones'");
            return true;
        }
        return false;
    }

    public String grabarlaboral() {
        if (validarlaboral()) {
            return null;
        }
        for (Verificacionesdesempenio cv : listaVDesempenio) {
            if (cv.getValor() != null) {
                int ref = 0;
                int refMin = 0;
                int refMax = 0;
                ref = cv.getValor().compareTo(BigDecimal.ZERO);
                refMin = cv.getValor().compareTo(cv.getValoracion().getMinimo());
                refMax = cv.getValor().compareTo(cv.getValoracion().getMaximo());

                if (ref == 0) {
                    MensajesErrores.advertencia("Existe un registro de puntaje von valor '0' ");
                    return null;
                }
                if (refMax == 1) {
                    MensajesErrores.advertencia("Existen un registro de puntaje superior al máximo");
                    return null;
                }
                if (refMin == -1) {
                    MensajesErrores.advertencia("Existen un registro de puntaje inferior al mínimo");
                    return null;
                }
            } else {
                MensajesErrores.advertencia("Existen un registro de puntaje nulo o vacio");
                return null;
            }
        }
        try {
            for (Verificacionesdesempenio vd : listaVDesempenio) {
                if (vd.getId() == null) {
                    ejbVDesempenio.create(vd, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbVDesempenio.edit(vd, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            if (laboral.getId() == null) {
                ejbVReferencias.create(laboral, parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbVReferencias.edit(laboral, parametrosSeguridad.getLogueado().getUserid());
            }
            experiencia.setValidado(Boolean.TRUE);
            ejbExperiencias.edit(experiencia, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioE.cancelar();
        return null;
    }

    //REFERENCIAS PERSONALES
    public String verificarpersonal(Recomendaciones exp, int index) {
        //recomendacion = listaRecomendaciones.get(formularioR.getFila().getRowIndex());
        recomendacion = exp;
        formularioR.setIndiceFila(index);
        fecha = new Date();
        listaVReferencias = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.recomendacion=:recomendacion and o.activo=true");
            parametros.put("recomendacion", recomendacion);
            List<Verificacionesreferencias> refper = ejbVReferencias.encontarParametros(parametros);
            if (refper.isEmpty()) {
                personal = new Verificacionesreferencias();
                personal.setRecomendacion(recomendacion);
                personal.setActivo(Boolean.TRUE);
                personal.setFecha(fecha);
            } else {
                for (Verificacionesreferencias valor : refper) {
                    personal = valor;
                    fecha = personal.getFecha();
                    //personal.equals(valor);
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioR.insertar();
        return null;
    }

    public boolean validarpersonal() {
        if (personal.getDescripcioncomportamiento() == null || personal.getDescripcioncomportamiento().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Descripción de Comportamiento'");
            return true;
        }
        if (personal.getObservacion() == null || personal.getObservacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar 'Observaciones'");
            return true;
        }
        return false;
    }

    public String grabarpersonal() {
        if (validarpersonal()) {
            return null;
        }
        try {
            if (personal.getId() == null) {
                ejbVReferencias.create(personal, parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbVReferencias.edit(personal, parametrosSeguridad.getLogueado().getUserid());
            }
            recomendacion.setValidado(Boolean.TRUE);
            ejbRecomendaciones.edit(recomendacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro guardado");
        formularioR.cancelar();
        return null;
    }

    public String salir() {
        formulario.insertar();
        return null;
    }

    public String cerrarVerificacion() {
        return "RrhhVista.jsf?faces-redirect=true";
    }

    public SelectItem[] getComboCargosSolicitudes() {
        try {
            List<Cargos> listaCargos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.id");
            parametros.put(";where", "o.activo=true");
            List<Cargos> aux = ejbCargos.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                for (Cargos cargo : aux) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.activo=true and o.cargovacante=:cargo");
                    parametros.put("cargo", cargo);
                    List<Solicitudescargo> solicitudes = ejbSolicitudes.encontarParametros(parametros);
                    if (!solicitudes.isEmpty()) {
                        for (Solicitudescargo s : solicitudes) {
                            listaCargos.add(s.getCargovacante().getCargo());
                        }
                    }
                }
            }
            return Combos.getSelectItems(listaCargos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ReportesEntrevistaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getRangoValores(Verificacionesdesempenio v) {

        String min = " ";
        String max = " ";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true  and o.id=:id");
            parametros.put("id", v.getValoracion().getId());
            List<Valoracionesdesempenio> aux;
            aux = ejbValoraciones.encontarParametros(parametros);
            if (!aux.isEmpty()) {
                min = aux.get(0).getMinimo() + "";
                max = aux.get(0).getMaximo() + "";
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(VerificacionReferenciasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return min + " - " + max;
    }

//    public void cambiaCargosxOrganigrama(ValueChangeEvent event) {
//        cargo = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            String newWord = (String) event.getNewValue();
//            List<Cargosxorganigrama> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " ";
//            where += "  upper(o.cargo.nombre) like:nombre ";
//            parametros.put("nombre", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.id desc");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbCarxOrg.encontarParametros(parametros);
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
//                cargo = (Cargosxorganigrama) autoComplete.getSelectedItem().getValue();
//            } else {
//                Cargosxorganigrama tmp = null;
//                for (int i = 0, max = listaCarxOrg.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaCarxOrg.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Cargosxorganigrama) e.getValue();
//                    }
//                }
//                if (tmp != null) {
//                    cargo = tmp;
//                }
//            }
//        }
//    }

    /**
     * @return the listaPostulantes
     */
    public LazyDataModel<Postulaciones> getListaPostulantes() {
        return listaPostulantes;
    }

    /**
     * @param listaPostulantes the listaPostulantes to set
     */
    public void setListaPostulantes(LazyDataModel<Postulaciones> listaPostulantes) {
        this.listaPostulantes = listaPostulantes;
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
     * @return the postulante
     */
    public Postulaciones getPostulante() {
        return postulante;
    }

    /**
     * @param postulante the postulante to set
     */
    public void setPostulante(Postulaciones postulante) {
        this.postulante = postulante;
    }

    /**
     * @return the listaValoraciones
     */
    public List<Valoracionesdesempenio> getListaValoraciones() {
        return listaValoraciones;
    }

    /**
     * @param listaValoraciones the listaValoraciones to set
     */
    public void setListaValoraciones(List<Valoracionesdesempenio> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
    }

    /**
     * @return the valoracion
     */
    public Valoracionesdesempenio getValoracion() {
        return valoracion;
    }

    /**
     * @param valoracion the valoracion to set
     */
    public void setValoracion(Valoracionesdesempenio valoracion) {
        this.valoracion = valoracion;
    }

    /**
     * @return the listaExperiencias
     */
    public List<Experiencias> getListaExperiencias() {
        return listaExperiencias;
    }

    /**
     * @param listaExperiencias the listaExperiencias to set
     */
    public void setListaExperiencias(List<Experiencias> listaExperiencias) {
        this.listaExperiencias = listaExperiencias;
    }

    /**
     * @return the listaRecomendaciones
     */
    public List<Recomendaciones> getListaRecomendaciones() {
        return listaRecomendaciones;
    }

    /**
     * @param listaRecomendaciones the listaRecomendaciones to set
     */
    public void setListaRecomendaciones(List<Recomendaciones> listaRecomendaciones) {
        this.listaRecomendaciones = listaRecomendaciones;
    }

    /**
     * @return the imprimir
     */
    public boolean isImprimir() {
        return imprimir;
    }

    /**
     * @param imprimir the imprimir to set
     */
    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the formularioE
     */
    public Formulario getFormularioE() {
        return formularioE;
    }

    /**
     * @param formularioE the formularioE to set
     */
    public void setFormularioE(Formulario formularioE) {
        this.formularioE = formularioE;
    }

    /**
     * @return the formularioR
     */
    public Formulario getFormularioR() {
        return formularioR;
    }

    /**
     * @param formularioR the formularioR to set
     */
    public void setFormularioR(Formulario formularioR) {
        this.formularioR = formularioR;
    }

    /**
     * @return the experiencia
     */
    public Experiencias getExperiencia() {
        return experiencia;
    }

    /**
     * @param experiencia the experiencia to set
     */
    public void setExperiencia(Experiencias experiencia) {
        this.experiencia = experiencia;
    }

    /**
     * @return the listaVReferencias
     */
    public List<Verificacionesreferencias> getListaVReferencias() {
        return listaVReferencias;
    }

    /**
     * @param listaVReferencias the listaVReferencias to set
     */
    public void setListaVReferencias(List<Verificacionesreferencias> listaVReferencias) {
        this.listaVReferencias = listaVReferencias;
    }

    /**
     * @return the listaVDesempenio
     */
    public List<Verificacionesdesempenio> getListaVDesempenio() {
        return listaVDesempenio;
    }

    /**
     * @param listaVDesempenio the listaVDesempenio to set
     */
    public void setListaVDesempenio(List<Verificacionesdesempenio> listaVDesempenio) {
        this.listaVDesempenio = listaVDesempenio;
    }

    /**
     * @return the laboral
     */
    public Verificacionesreferencias getLaboral() {
        return laboral;
    }

    /**
     * @param laboral the laboral to set
     */
    public void setLaboral(Verificacionesreferencias laboral) {
        this.laboral = laboral;
    }

    /**
     * @return the personal
     */
    public Verificacionesreferencias getPersonal() {
        return personal;
    }

    /**
     * @param personal the personal to set
     */
    public void setPersonal(Verificacionesreferencias personal) {
        this.personal = personal;
    }

    /**
     * @return the listaVLaborales
     */
    public List<Verificacionesreferencias> getListaVLaborales() {
        return listaVLaborales;
    }

    /**
     * @param listaVLaborales the listaVLaborales to set
     */
    public void setListaVLaborales(List<Verificacionesreferencias> listaVLaborales) {
        this.listaVLaborales = listaVLaborales;
    }

    /**
     * @return the desempenio
     */
    public Verificacionesdesempenio getDesempenio() {
        return desempenio;
    }

    /**
     * @param desempenio the desempenio to set
     */
    public void setDesempenio(Verificacionesdesempenio desempenio) {
        this.desempenio = desempenio;
    }

    /**
     * @return the recomendacion
     */
    public Recomendaciones getRecomendacion() {
        return recomendacion;
    }

    /**
     * @param recomendacion the recomendacion to set
     */
    public void setRecomendacion(Recomendaciones recomendacion) {
        this.recomendacion = recomendacion;
    }

    /**
     * @return the formularioV
     */
    public Formulario getFormularioV() {
        return formularioV;
    }

    /**
     * @param formularioV the formularioV to set
     */
    public void setFormularioV(Formulario formularioV) {
        this.formularioV = formularioV;
    }

    /**
     * @return the listaValores
     */
    public List<Integer> getListaValores() {
        return listaValores;
    }

    /**
     * @param listaValores the listaValores to set
     */
    public void setListaValores(List<Integer> listaValores) {
        this.listaValores = listaValores;
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
     * @return the ciAsp
     */
    public String getCiAsp() {
        return ciAsp;
    }

    /**
     * @param ciAsp the ciAsp to set
     */
    public void setCiAsp(String ciAsp) {
        this.ciAsp = ciAsp;
    }

    /**
     * @return the aplAsp
     */
    public String getAplAsp() {
        return aplAsp;
    }

    /**
     * @param aplAsp the aplAsp to set
     */
    public void setAplAsp(String aplAsp) {
        this.aplAsp = aplAsp;
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

}
