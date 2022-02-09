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
import org.beans.sfccbdmq.CalificacionesoposicionFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.DetallesoposicionFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.entidades.sfccbdmq.Calificacionesoposicion;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Detallesoposicion;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Postulaciones;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "pruebasOposicion")
@ViewScoped
public class RegistroPruebasOposicionBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of RegistroPruebasOposicionBean
     */

    private LazyDataModel<Postulaciones> listaPostulantes;
    private Formulario formulario = new Formulario();
    private Formulario formularioCal = new Formulario();
    private Cargosxorganigrama cargo;
    private Postulaciones postulante;
    private List<Detallesoposicion> listaDetallesCalificacion;
    private Detallesoposicion detalleCalificacion;
    private Calificacionesoposicion calificacion;
    private List<Calificacionesoposicion> listaCalificaciones;
    private Date fecha;
    private List listaCarxOrg;
    private String nomCargoxOrg;
    private String ciAsp;
    private String aplAsp;

    @EJB
    private PostulacionesFacade ejbPostulaciones;
    @EJB
    private CalificacionesoposicionFacade ejbCalificacionesOposicion;
    @EJB
    private DetallesoposicionFacade ejbDetallesCalificación;
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
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.area.tipo.estatus=false and o.area.prueba=true and o.area.tipo.activo=true and o.activo=true");
            parametros.put(";orden", "o.orden asc");
            listaDetallesCalificacion = ejbDetallesCalificación.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // fin perfiles

    public RegistroPruebasOposicionBean() {
        listaPostulantes = new LazyDataModel<Postulaciones>() {
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

        listaPostulantes = new LazyDataModel<Postulaciones>() {
            @Override
            public List<Postulaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                String where = "o.id is not null and o.activo=true and o.solicitudcargo.vigente=true";
                //Criterios de busqueda
                //CARGO
                if (cargo != null) {
                    where += " and o.solicitudcargo.cargovacante=:cargo";
                    parametros.put("cargo", cargo);
                }
                //APELLIDO ASPIRANTE
                if (!((aplAsp == null) || (aplAsp.isEmpty()))) {
                    where += " and upper(o.empleado.entidad.apellidos) like:apellido";
                    parametros.put("apellido", "%" + aplAsp.toUpperCase() + "%");
                }
                //CI ASPIRANTE
                if (!((ciAsp == null) || (ciAsp.isEmpty()))) {
                    where += " and upper(o.empleado.entidad.pin) like:pin";
                    parametros.put("pin", "%" + ciAsp.toUpperCase() + "%");
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
                getListaPostulantes().setRowCount(total);
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
        fecha = new Date();
        postulante.setFechaentrevista(fecha);
        formulario.insertar();
        listaCalificaciones = new LinkedList<>();
        for (Detallesoposicion d : listaDetallesCalificacion) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", " o.postulacion=:postulante and o.detalle=:detalle");
                parametros.put("postulante", postulante);
                parametros.put("detalle", d);
                parametros.put(";orden", "o.detalle.orden asc");
                List<Calificacionesoposicion> aux = ejbCalificacionesOposicion.encontarParametros(parametros);
                for (Calificacionesoposicion valor : aux) {
                    if (valor != null) {
                        listaCalificaciones.add(valor);
                    }
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (listaCalificaciones.isEmpty()) {
            for (Detallesoposicion d : listaDetallesCalificacion) {
                Calificacionesoposicion n = new Calificacionesoposicion();
                n.setPostulacion(postulante);
                n.setDetalle(d);
                n.setFecha(new Date());
                n.setDefinitivo(Boolean.FALSE);
                listaCalificaciones.add(n);
            }
        }
        return null;
    }

    public String insertar() {
        for (Calificacionesoposicion ap : listaCalificaciones) {
//Control ingreso notas
            if (ap.getValor() != null) {
                int ref = 0;
                int ref1 = 0;
                ref = ap.getValor().compareTo(BigDecimal.ZERO);
                ref1 = ap.getValor().compareTo(ap.getDetalle().getNotamaxima());
                if (ref <= 0) {
                    MensajesErrores.advertencia("Registro incorrecto de notas");
                    return null;
                }
                if (ref1 == 1) {
                    MensajesErrores.advertencia("Registro de notas superior al máximo");
                    return null;
                }
            }
            try {
                if (ap.getId() == null) {
                    ap.setPostulacion(postulante);
                    ap.setFecha(new Date());
                    ap.setDefinitivo(Boolean.FALSE);
                    ejbCalificacionesOposicion.create(ap, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ap.setFecha(new Date());
                    ejbCalificacionesOposicion.edit(ap, parametrosSeguridad.getLogueado().getUserid());
                }
            } catch (InsertarException | GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
                Logger.getLogger(RegistroPruebasOposicionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MensajesErrores.informacion("Registro guardado");
        formulario.cancelar();
        return null;
    }

    public String grabar() {

//        if (validar()) {
//            return null;
//        }
        // buscar();
        for (Calificacionesoposicion ap : listaCalificaciones) {
//Control ingreso notas
            if (ap.getValor() != null) {
                int ref = 0;
                int ref1 = 0;
                ref = ap.getValor().compareTo(BigDecimal.ZERO);
                ref1 = ap.getValor().compareTo(ap.getDetalle().getNotamaxima());
                if (ref <= 0) {
                    MensajesErrores.advertencia("Registro incorrecto de notas");
                    return null;
                }
                if (ref1 == 1) {
                    MensajesErrores.advertencia("Registro de notas superior al máximo");
                    return null;
                }
            }
            try {
                ap.setFecha(new Date());
                ejbCalificacionesOposicion.edit(ap, parametrosSeguridad.getLogueado().getUserid());
            } catch (GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage() + ";" + ex.getCause());
                Logger.getLogger(RegistroPruebasOposicionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MensajesErrores.informacion("Registro guardado");
        return null;
    }

    public String modificar() {
        postulante = (Postulaciones) listaPostulantes.getRowData();
        fecha = postulante.getFechaentrevista();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.postulacion=:postulante ");
            parametros.put("postulante", postulante);
            parametros.put(";orden", "o.detalle.orden asc");
            listaCalificaciones = ejbCalificacionesOposicion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(RegistroPruebasOposicionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String salir() {
        formulario.insertar();
        return null;
    }

    public String cerrarRegistroPruebas() {
        return "/sistema/BlancoVista.jsf?faces-redirect=true";
    }

    public boolean controlRegistroPruebas(Postulaciones p) {

        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.postulacion=:postulante and o.entrevista is null");
            parametros.put("postulante", p);
            parametros.put(";orden", "o.detalle.orden asc");
            List<Calificacionesoposicion> aux = ejbCalificacionesOposicion.encontarParametros(parametros);
            if (aux.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
//            where += "  upper(o.cargo.nombre) like:nombre and o.activo=true";
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
     * @return the listaDetallesCalificacion
     */
    public List<Detallesoposicion> getListaDetallesCalificacion() {
        return listaDetallesCalificacion;
    }

    /**
     * @param listaDetallesCalificacion the listaDetallesCalificacion to set
     */
    public void setListaDetallesCalificacion(List<Detallesoposicion> listaDetallesCalificacion) {
        this.listaDetallesCalificacion = listaDetallesCalificacion;
    }

    /**
     * @return the detalleCalificacion
     */
    public Detallesoposicion getDetalleCalificacion() {
        return detalleCalificacion;
    }

    /**
     * @param detalleCalificacion the detalleCalificacion to set
     */
    public void setDetalleCalificacion(Detallesoposicion detalleCalificacion) {
        this.detalleCalificacion = detalleCalificacion;
    }

    /**
     * @return the calificacion
     */
    public Calificacionesoposicion getCalificacion() {
        return calificacion;
    }

    /**
     * @param calificacion the calificacion to set
     */
    public void setCalificacion(Calificacionesoposicion calificacion) {
        this.calificacion = calificacion;
    }

    /**
     * @return the listaCalificaciones
     */
    public List<Calificacionesoposicion> getListaCalificaciones() {
        return listaCalificaciones;
    }

    /**
     * @param listaCalificaciones the listaCalificaciones to set
     */
    public void setListaCalificaciones(List<Calificacionesoposicion> listaCalificaciones) {
        this.listaCalificaciones = listaCalificaciones;
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
     * @return the formularioCal
     */
    public Formulario getFormularioCal() {
        return formularioCal;
    }

    /**
     * @param formularioCal the formularioCal to set
     */
    public void setFormularioCal(Formulario formularioCal) {
        this.formularioCal = formularioCal;
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
