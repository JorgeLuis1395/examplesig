/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CalificacionesoposicionFacade;
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.DetallesentrevistaFacade;
import org.beans.sfccbdmq.DetallesoposicionFacade;
import org.beans.sfccbdmq.EntrevistasFacade;
import org.beans.sfccbdmq.PostulacionesFacade;
import org.beans.sfccbdmq.RequerimientoscargoFacade;
import org.beans.sfccbdmq.SolicitudescargoFacade;
import org.entidades.sfccbdmq.Calificacionesoposicion;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallesentrevista;
import org.entidades.sfccbdmq.Detallesoposicion;
import org.entidades.sfccbdmq.Entrevistas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Postulaciones;
import org.entidades.sfccbdmq.Requerimientoscargo;
import org.entidades.sfccbdmq.Solicitudescargo;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reportesEntrevista")
@ViewScoped
public class ReportesEntrevistaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of ReportesEntrevistaBean
     */

   
    private LazyDataModel<Postulaciones> listaPostulantes;
    private Formulario formulario = new Formulario();
    private Entrevistas entrevista;
    private Codigos codigo;
    private String msjbusqueda;
    private Cargosxorganigrama cargo;
    private Postulaciones postulante;
    private List<Requerimientoscargo> listaRequerimientos;
    private Requerimientoscargo requerimiento;
    private List<Detallesoposicion> listaDetallesCalificacion;
    private Detallesoposicion detalleCalificacion;
    private List<Detallesentrevista> listaDetallesEntrevista;
    private Detallesentrevista detalleEntrevista;
    private Calificacionesoposicion calificacion;
    private List<Calificacionesoposicion> listaCalificaciones;
    private Formulario formularioReq = new Formulario();
    private Formulario formularioCal = new Formulario();
    private List<Detallesentrevista> listaDetallesFormativo;
    private List<Detallesentrevista> listaDetallesProfesional;
    private List<Detallesentrevista> listaMotivacion;
    private List<Detallesentrevista> listaHabilidades;
    private List<Detallesentrevista> listaNecesidades;
    private List<Detallesentrevista> listaOtros;
    private List<Detallesentrevista> listaDetallesFormativob;
    private List<Detallesentrevista> listaDetallesProfesionalb;
    private List<Detallesentrevista> listaMotivacionb;
    private List<Detallesentrevista> listaHabilidadesb;
    private List<Detallesentrevista> listaNecesidadesb;
    private List<Detallesentrevista> listaOtrosb;
    private List<Entrevistas> listaEntrevistas;
    private boolean imprimir;
    private Date fecha;
    private String variable;
    private boolean controEntrevista = Boolean.FALSE;
    private List listaCarxOrg;
    private String nomCargoxOrg;
    private String ciAsp;
    private String aplAsp;

    @EJB
    private EntrevistasFacade ejbEntrevistas;
    @EJB
    private PostulacionesFacade ejbPostulaciones;
    @EJB
    private DetallesentrevistaFacade ejbDetallesEntrevista;
    @EJB
    private CalificacionesoposicionFacade ejbCalificacionesOposicion;
    @EJB
    private DetallesoposicionFacade ejbDetallesCalificación;
    @EJB
    private CargosFacade ejbCargos;
    @EJB
    private SolicitudescargoFacade ejbSolicitudes;
    @EJB
    private RequerimientoscargoFacade ejbRequerimiento;
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
            parametros.put(";where", "o.area.tipo.estatus=false and o.area.prueba=false and o.area.tipo.activo=true and o.activo=true");
            parametros.put(";orden", "o.orden asc");
            listaDetallesCalificacion = ejbDetallesCalificación.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // fin perfiles
   

    public ReportesEntrevistaBean() {
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
                listaPostulantes.setRowCount(total);
                try {
                    List<Postulaciones> aux = ejbPostulaciones.encontarParametros(parametros);
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
        entrevista = new Entrevistas();
        entrevista.setPostulacion(postulante);
        entrevista.setResponsable(parametrosSeguridad.getLogueado());
        entrevista.setFecha(fecha);
        listaDetallesEntrevista = new LinkedList<>();
        listaRequerimientos = new LinkedList<>();
        listaDetallesFormativo = new LinkedList<>();
        listaDetallesProfesional = new LinkedList<>();
        listaMotivacion = new LinkedList<>();
        listaHabilidades = new LinkedList<>();
        listaNecesidades = new LinkedList<>();
        listaOtros = new LinkedList<>();
        formulario.insertar();
        listaCalificaciones = new LinkedList<>();
        imprimir = false;
        for (Detallesoposicion d : listaDetallesCalificacion) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", " o.postulacion=:postulante and o.entrevista=:entrevista and o.detalle=:detalle");
                parametros.put("postulante", postulante);
                parametros.put("entrevista", entrevista);
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
                n.setEntrevista(entrevista);
                n.setDetalle(d);
                n.setFecha(new Date());
                n.setDefinitivo(Boolean.FALSE);
                listaCalificaciones.add(n);
            }
        }
        return null;
    }

    public boolean validar() {
        if (fecha == null) {
            MensajesErrores.advertencia("Indicar la fecha de la entrevista");
            return true;
        }
        if (entrevista.getObservacion() == null || entrevista.getObservacion().isEmpty()) {
            MensajesErrores.advertencia("Indicar Observación de la entrevista");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        for (Calificacionesoposicion cv : listaCalificaciones) {
//Control ingreso notas
            if (cv.getValor() != null) {
                int ref = 0;
                int ref1 = 0;
                ref = cv.getValor().compareTo(BigDecimal.ZERO);
                ref1 = cv.getValor().compareTo(cv.getDetalle().getNotamaxima());
                if (ref <= 0) {
                    MensajesErrores.advertencia("Existe un registro de nota incorrecto");
                    return null;
                }
                if (ref1 == 1) {
                    MensajesErrores.advertencia("Existen un registro de nota superior al máximo");
                    return null;
                }
            }
        }

        try {
            ejbPostulaciones.edit(postulante, parametrosSeguridad.getLogueado().getUserid());
            ejbEntrevistas.create(entrevista, parametrosSeguridad.getLogueado().getUserid());
            for (Detallesentrevista d : listaDetallesFormativo) {
                d.setEntrevista(entrevista);
                ejbDetallesEntrevista.create(d, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Detallesentrevista d : listaDetallesProfesional) {
                d.setEntrevista(entrevista);
                ejbDetallesEntrevista.create(d, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Detallesentrevista d : listaMotivacion) {
                d.setEntrevista(entrevista);
                ejbDetallesEntrevista.create(d, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Detallesentrevista d : listaHabilidades) {
                d.setEntrevista(entrevista);
                ejbDetallesEntrevista.create(d, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Detallesentrevista d : listaNecesidades) {
                d.setEntrevista(entrevista);
                ejbDetallesEntrevista.create(d, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Detallesentrevista d : listaOtros) {
                d.setEntrevista(entrevista);
                ejbDetallesEntrevista.create(d, parametrosSeguridad.getLogueado().getUserid());
            }
            for (Calificacionesoposicion c : listaCalificaciones) {
                c.setPostulacion(postulante);
                c.setFecha(entrevista.getFecha());
                ejbCalificacionesOposicion.create(c, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        imprimir = false;
        return null;
    }

    public String modificar() {
        postulante = (Postulaciones) listaPostulantes.getRowData();
        fecha = postulante.getFechaentrevista();
        listaDetallesFormativo = new LinkedList<>();
        listaDetallesProfesional = new LinkedList<>();
        listaMotivacion = new LinkedList<>();
        listaHabilidades = new LinkedList<>();
        listaNecesidades = new LinkedList<>();
        listaOtros = new LinkedList<>();
        listaDetallesFormativob = new LinkedList<>();
        listaDetallesProfesionalb = new LinkedList<>();
        listaMotivacionb = new LinkedList<>();
        listaHabilidadesb = new LinkedList<>();
        listaNecesidadesb = new LinkedList<>();
        listaOtrosb = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.postulacion=:postulante");
            parametros.put("postulante", postulante);
            listaEntrevistas = ejbEntrevistas.encontarParametros(parametros);
            for (Entrevistas e : listaEntrevistas) {
                entrevista = e;
                parametros = new HashMap();
                parametros.put(";where", "o.postulacion=:postulacion and o.definitivo=false and o.entrevista=:entrevista");
                parametros.put("postulacion", postulante);
                parametros.put("entrevista", e);
                listaCalificaciones = ejbCalificacionesOposicion.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='HF'");
                parametros.put("entrevista", e);
                listaDetallesFormativo = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='HP'");
                parametros.put("entrevista", e);
                listaDetallesProfesional = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='MP'");
                parametros.put("entrevista", e);
                listaMotivacion = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='HD'");
                parametros.put("entrevista", e);
                listaHabilidades = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='NCD'");
                parametros.put("entrevista", e);
                listaNecesidades = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='OD'");
                parametros.put("entrevista", e);
                listaOtros = ejbDetallesEntrevista.encontarParametros(parametros);

            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ReportesEntrevistaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        imprimir = false;
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        for (Calificacionesoposicion cv : listaCalificaciones) {
//Control ingreso notas
            if (cv.getValor() != null) {
                int ref = 0;
                int ref1 = 0;
                ref = cv.getValor().compareTo(BigDecimal.ZERO);
                ref1 = cv.getValor().compareTo(cv.getDetalle().getNotamaxima());
                if (ref <= 0) {
                    MensajesErrores.advertencia("Existen estudiantes con registro de notas incorrecto");
                    return null;
                }
                if (ref1 == 1) {
                    MensajesErrores.advertencia("Existen estudiantes con notas superior al máximo");
                    return null;
                }
            }
        }

        try {
            postulante.setFechaentrevista(fecha);
            ejbPostulaciones.edit(postulante, parametrosSeguridad.getLogueado().getUserid());
            entrevista.setFecha(fecha);
            ejbEntrevistas.edit(entrevista, parametrosSeguridad.getLogueado().getUserid());
            for (Detallesentrevista de : listaDetallesFormativo) {
                de.setEntrevista(entrevista);
                if (de.getId() == null) {
                    ejbDetallesEntrevista.create(de, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesEntrevista.edit(de, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista deb : listaDetallesFormativob) {
                if (deb.getId() != null) {
                    deb.setActivo(Boolean.FALSE);
                    ejbDetallesEntrevista.edit(deb, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista hp : listaDetallesProfesional) {
                hp.setEntrevista(entrevista);
                if (hp.getId() == null) {
                    ejbDetallesEntrevista.create(hp, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesEntrevista.edit(hp, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista hpb : listaDetallesProfesionalb) {
                if (hpb.getId() != null) {
                    hpb.setActivo(Boolean.FALSE);
                    ejbDetallesEntrevista.edit(hpb, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista mp : listaMotivacion) {
                mp.setEntrevista(entrevista);
                if (mp.getId() == null) {
                    ejbDetallesEntrevista.create(mp, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesEntrevista.edit(mp, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista mpb : listaMotivacionb) {
                if (mpb.getId() != null) {
                    mpb.setActivo(Boolean.FALSE);
                    ejbDetallesEntrevista.edit(mpb, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista hd : listaHabilidades) {
                hd.setEntrevista(entrevista);
                if (hd.getId() == null) {
                    ejbDetallesEntrevista.create(hd, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesEntrevista.edit(hd, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista hdb : listaHabilidadesb) {
                if (hdb.getId() != null) {
                    hdb.setActivo(Boolean.FALSE);
                    ejbDetallesEntrevista.edit(hdb, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista ncd : listaNecesidades) {
                ncd.setEntrevista(entrevista);
                if (ncd.getId() == null) {
                    ejbDetallesEntrevista.create(ncd, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesEntrevista.edit(ncd, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista ncdb : listaNecesidadesb) {
                if (ncdb.getId() != null) {
                    ncdb.setActivo(Boolean.FALSE);
                    ejbDetallesEntrevista.edit(ncdb,parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista od : listaOtros) {
                od.setEntrevista(entrevista);
                if (od.getId() == null) {
                    ejbDetallesEntrevista.create(od, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesEntrevista.edit(od, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Detallesentrevista odb : listaOtrosb) {
                if (odb.getId() != null) {
                    odb.setActivo(Boolean.FALSE);
                    ejbDetallesEntrevista.edit(odb, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            for (Calificacionesoposicion co : listaCalificaciones) {
                co.setEntrevista(entrevista);
                if (co.getId() != null) {
                    ejbCalificacionesOposicion.edit(co, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String salir() {
        formulario.insertar();
        return null;
    }

    public String cerrarEntrevista() {
        return "/sistema/BlancoVista.jsf?faces-redirect=true";
    }

    //Requerimiento
    public String nuevoReq(String cod) {
        detalleEntrevista = new Detallesentrevista();
        detalleEntrevista.setEntrevista(entrevista);
        detalleEntrevista.setActivo(Boolean.TRUE);
        variable = cod;
        listaDetallesEntrevista = new LinkedList<>();
        formularioReq.insertar();
        return null;
    }

    public boolean validarReq() {
        if (detalleEntrevista.getDescripcion() == null || detalleEntrevista.getDescripcion().isEmpty()) {
            MensajesErrores.advertencia("Indique Detalle");
            return true;
        }
        return false;
    }

    public String insertarReq() {
        if (validarReq()) {
            return null;
        }
        detalleEntrevista.setActivo(Boolean.TRUE);
        switch (variable) {
            case "HF":
                listaDetallesFormativo.add(detalleEntrevista);
                formularioReq.cancelar();
                break;
            case "HP":
                listaDetallesProfesional.add(detalleEntrevista);
                formularioReq.cancelar();
                break;
            case "MP":
                listaMotivacion.add(detalleEntrevista);
                formularioReq.cancelar();
                break;
            case "HD":
                listaHabilidades.add(detalleEntrevista);
                formularioReq.cancelar();
                break;
            case "NCD":
                listaNecesidades.add(detalleEntrevista);
                formularioReq.cancelar();
                break;
            case "OD":
                listaOtros.add(detalleEntrevista);
                formularioReq.cancelar();
                break;
        }
        return null;
    }

    public String modificarReq(Detallesentrevista det, int index) {
        detalleEntrevista = det;
        formularioReq.setIndiceFila(index);
        formularioReq.editar();
        variable = det.getRequerimiento().getCompetencia().getCodigo();
        return null;
    }

    public String grabarReq() {
        if (validarReq()) {
            return null;
        }
        switch (detalleEntrevista.getRequerimiento().getCompetencia().getCodigo()) {
            case "HF":
                listaDetallesFormativo.set(formularioReq.getIndiceFila(), detalleEntrevista);
                break;
            case "HP":
                listaDetallesProfesional.set(formularioReq.getIndiceFila(), detalleEntrevista);
                break;
            case "MP":
                listaMotivacion.set(formularioReq.getIndiceFila(), detalleEntrevista);
                break;
            case "HD":
                listaHabilidades.set(formularioReq.getIndiceFila(), detalleEntrevista);
                break;
            case "NCD":
                listaNecesidades.set(formularioReq.getIndiceFila(), detalleEntrevista);
                break;
            case "OD":
                listaOtros.set(formularioReq.getIndiceFila(), detalleEntrevista);
                break;
        }
        formularioReq.cancelar();
        return null;
    }

    public String eliminarReq(Detallesentrevista det, int index) {
        detalleEntrevista = det;
        formularioReq.setIndiceFila(index);
        formularioReq.eliminar();
        variable = det.getRequerimiento().getCompetencia().getCodigo();
        return null;
    }

    public String borrarReq() {
        switch (detalleEntrevista.getRequerimiento().getCompetencia().getCodigo()) {
            case "HF":
                listaDetallesFormativo.remove(formularioReq.getIndiceFila());
                listaDetallesFormativob.add(detalleEntrevista);
                break;
            case "HP":
                listaDetallesProfesional.remove(formularioReq.getIndiceFila());
                listaDetallesProfesionalb.add(detalleEntrevista);
                break;
            case "MP":
                listaMotivacion.remove(formularioReq.getIndiceFila());
                listaMotivacionb.add(detalleEntrevista);
                break;
            case "HD":
                listaHabilidades.remove(formularioReq.getIndiceFila());
                listaHabilidadesb.add(detalleEntrevista);
                break;
            case "NCD":
                listaNecesidades.remove(formularioReq.getIndiceFila());
                listaNecesidadesb.add(detalleEntrevista);
                break;
            case "OD":
                listaOtros.remove(formularioReq.getIndiceFila());
                listaOtrosb.add(detalleEntrevista);
                break;
        }
        formularioReq.cancelar();
        return null;
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

    public String cancelarReporte() {
        setImprimir(false);
        buscar();
        formulario.cancelar();
        return null;
    }

    //codigos que  no consten en los requerimientos
    private List<Requerimientoscargo> traerRequerimientos() {

        List<Requerimientoscargo> aux = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.competencia.codigo=:competencia");
            parametros.put("competencia", variable);
            parametros.put(";orden", "o.orden asc");
            List<Requerimientoscargo> aux1 = ejbRequerimiento.encontarParametros(parametros);
            for (Requerimientoscargo req : aux1) {
                int i = ejbRequerimiento.contar(parametros);
                if (i == 0) {
                    aux.add(req);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ReportesEntrevistaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return aux;
    }

    public SelectItem[] getComboReq() {

        return Combos.getSelectItems(traerRequerimientos(), true);
    }

    public SelectItem[] comboRequerimientos() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true and o.competencia.codigo=:competencia");
            parametros.put("competencia", variable);
            parametros.put(";orden", "o.orden asc");
            //List<Requerimientoscargo> listaRequerimientos = ejbRequerimiento.encontarParametros(parametros);
            return Combos.getSelectItems(ejbRequerimiento.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ReportesEntrevistaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigDecimal totalNotaMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaCalificaciones) {
            Calificacionesoposicion apsi = (Calificacionesoposicion) as;
            if (apsi != null && apsi.getDetalle().getNotamaxima() != null) {
                suma = suma.add(apsi.getDetalle().getNotamaxima());
            }
        }
        return suma;
    }

    public boolean controlEntrevista(Postulaciones p) {

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.postulacion=:postulacion");
            parametros.put("postulacion", p);
            List<Entrevistas> aux = ejbEntrevistas.encontarParametros(parametros);
            if (aux.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ReportesEntrevistaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //IMPRIMIR
    public String imprimir() {
        postulante = (Postulaciones) listaPostulantes.getRowData();
        listaDetallesFormativo = new LinkedList<>();
        listaDetallesProfesional = new LinkedList<>();
        listaMotivacion = new LinkedList<>();
        listaHabilidades = new LinkedList<>();
        listaNecesidades = new LinkedList<>();
        listaOtros = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.postulacion=:postulante");
            parametros.put("postulante", postulante);
            listaEntrevistas = ejbEntrevistas.encontarParametros(parametros);
            for (Entrevistas e : listaEntrevistas) {
                entrevista = e;
                parametros = new HashMap();
                parametros.put(";where", "o.postulacion=:postulacion and o.definitivo=false and o.entrevista=:entrevista");
                parametros.put("postulacion", postulante);
                parametros.put("entrevista", e);
                listaCalificaciones = ejbCalificacionesOposicion.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='HF'");
                parametros.put("entrevista", e);
                listaDetallesFormativo = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='HP'");
                parametros.put("entrevista", e);
                listaDetallesProfesional = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='MP'");
                parametros.put("entrevista", e);
                listaMotivacion = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='HD'");
                parametros.put("entrevista", e);
                listaHabilidades = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='NCD'");
                parametros.put("entrevista", e);
                listaNecesidades = ejbDetallesEntrevista.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.entrevista=:entrevista and o.activo=true and o.requerimiento.competencia.codigo='OD'");
                parametros.put("entrevista", e);
                listaOtros = ejbDetallesEntrevista.encontarParametros(parametros);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger
                    .getLogger(ReportesEntrevistaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        imprimir = true;
        return null;
    }

    public List<Detallesentrevista> getListaDetallesImp(String c) {
        if (entrevista == null || c == null) {
            return null;
        }

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.entrevista=:entrevista and o.activo=true o.requerimiento.competencia=:codigo");
            parametros.put("entrevista", entrevista);
            parametros.put("codigo", c);
            return ejbDetallesEntrevista.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Calificacionesoposicion> getListaCalificacionesImp() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.postulacion=:postulante and o.entrevista=:entrevista ");
            parametros.put("postulante", postulante);
            parametros.put("entrevista", entrevista);
            parametros.put(";orden", "o.detalle.orden asc");
            List<Calificacionesoposicion> aux = ejbCalificacionesOposicion.encontarParametros(parametros);
            Calificacionesoposicion total = new Calificacionesoposicion();
            total.setId(-1);
            aux.add(total);
            return aux;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Detallesoposicion> getListaDetallesOposicion() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.area.tipo.estatus=false and o.area.prueba=false and o.area.tipo.activo=true and o.activo=true");
            parametros.put(";orden", "o.orden asc");
            return ejbDetallesCalificación.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double traerNotasOposicion(Detallesoposicion d, Postulaciones p) {
        double retorno = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.postulacion=:postulacion and o.detalle=:detalle");
            parametros.put("detalle", d);
            parametros.put("postulacion", p);
            List<Calificacionesoposicion> aux = ejbCalificacionesOposicion.encontarParametros(parametros);
            for (Calificacionesoposicion co : aux) {
                retorno = co.getValor().doubleValue();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String traerTotalEntrevista() {
        double retorno = 0;
        double max = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.postulacion=:postulante and o.entrevista=:entrevista ");
            parametros.put("postulante", postulante);
            parametros.put("entrevista", entrevista);
            List<Calificacionesoposicion> aux = ejbCalificacionesOposicion.encontarParametros(parametros);
            for (Calificacionesoposicion c : aux) {
                if (c.getId() != -1) {
                    retorno += c.getValor().doubleValue();
                    max += c.getDetalle().getNotamaxima().doubleValue();
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "" + retorno + " / " + max;
    }

    public double getTotalEntrevista(Postulaciones p) {
        double retorno = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.postulacion=:postulacion");
            parametros.put("postulacion", p);
            List<Calificacionesoposicion> aux = ejbCalificacionesOposicion.encontarParametros(parametros);
            for (Calificacionesoposicion co : aux) {
                retorno += co.getValor().doubleValue();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportesEntrevistaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Math.rint(retorno * 100) / 100;
    }

    //AUTOCOMPLETE
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
     * @return the codigo
     */
    public Codigos getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(Codigos codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the msjbusqueda
     */
    public String getMsjbusqueda() {
        return msjbusqueda;
    }

    /**
     * @param msjbusqueda the msjbusqueda to set
     */
    public void setMsjbusqueda(String msjbusqueda) {
        this.msjbusqueda = msjbusqueda;
    }

    /**
     * @return the listaRequerimientos
     */
    public List<Requerimientoscargo> getListaRequerimientos() {
        return listaRequerimientos;
    }

    /**
     * @param listaRequerimientos the listaRequerimientos to set
     */
    public void setListaRequerimientos(List<Requerimientoscargo> listaRequerimientos) {
        this.listaRequerimientos = listaRequerimientos;
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
     * @return the requerimiento
     */
    public Requerimientoscargo getRequerimiento() {
        return requerimiento;
    }

    /**
     * @param requerimiento the requerimiento to set
     */
    public void setRequerimiento(Requerimientoscargo requerimiento) {
        this.requerimiento = requerimiento;
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
     * @return the listaDetallesEntrevista
     */
    public List<Detallesentrevista> getListaDetallesEntrevista() {
        return listaDetallesEntrevista;
    }

    /**
     * @param listaDetallesEntrevista the listaDetallesEntrevista to set
     */
    public void setListaDetallesEntrevista(List<Detallesentrevista> listaDetallesEntrevista) {
        this.listaDetallesEntrevista = listaDetallesEntrevista;
    }

    /**
     * @return the detalleEntrevista
     */
    public Detallesentrevista getDetalleEntrevista() {
        return detalleEntrevista;
    }

    /**
     * @param detalleEntrevista the detalleEntrevista to set
     */
    public void setDetalleEntrevista(Detallesentrevista detalleEntrevista) {
        this.detalleEntrevista = detalleEntrevista;
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
     * @return the formularioReq
     */
    public Formulario getFormularioReq() {
        return formularioReq;
    }

    /**
     * @param formularioReq the formularioReq to set
     */
    public void setFormularioReq(Formulario formularioReq) {
        this.formularioReq = formularioReq;
    }

    /**
     * @return the listaDetallesFormativo
     */
    public List<Detallesentrevista> getListaDetallesFormativo() {
        return listaDetallesFormativo;
    }

    /**
     * @param listaDetallesFormativo the listaDetallesFormativo to set
     */
    public void setListaDetallesFormativo(List<Detallesentrevista> listaDetallesFormativo) {
        this.listaDetallesFormativo = listaDetallesFormativo;
    }

    /**
     * @return the listaDetallesProfesional
     */
    public List<Detallesentrevista> getListaDetallesProfesional() {
        return listaDetallesProfesional;
    }

    /**
     * @param listaDetallesProfesional the listaDetallesProfesional to set
     */
    public void setListaDetallesProfesional(List<Detallesentrevista> listaDetallesProfesional) {
        this.listaDetallesProfesional = listaDetallesProfesional;
    }

    /**
     * @return the listaMotivacion
     */
    public List<Detallesentrevista> getListaMotivacion() {
        return listaMotivacion;
    }

    /**
     * @param listaMotivacion the listaMotivacion to set
     */
    public void setListaMotivacion(List<Detallesentrevista> listaMotivacion) {
        this.listaMotivacion = listaMotivacion;
    }

    /**
     * @return the listaHabilidades
     */
    public List<Detallesentrevista> getListaHabilidades() {
        return listaHabilidades;
    }

    /**
     * @param listaHabilidades the listaHabilidades to set
     */
    public void setListaHabilidades(List<Detallesentrevista> listaHabilidades) {
        this.listaHabilidades = listaHabilidades;
    }

    /**
     * @return the listaNecesidades
     */
    public List<Detallesentrevista> getListaNecesidades() {
        return listaNecesidades;
    }

    /**
     * @param listaNecesidades the listaNecesidades to set
     */
    public void setListaNecesidades(List<Detallesentrevista> listaNecesidades) {
        this.listaNecesidades = listaNecesidades;
    }

    /**
     * @return the listaOtros
     */
    public List<Detallesentrevista> getListaOtros() {
        return listaOtros;
    }

    /**
     * @param listaOtros the listaOtros to set
     */
    public void setListaOtros(List<Detallesentrevista> listaOtros) {
        this.listaOtros = listaOtros;
    }

    /**
     * @return the listaDetallesFormativob
     */
    public List<Detallesentrevista> getListaDetallesFormativob() {
        return listaDetallesFormativob;
    }

    /**
     * @param listaDetallesFormativob the listaDetallesFormativob to set
     */
    public void setListaDetallesFormativob(List<Detallesentrevista> listaDetallesFormativob) {
        this.listaDetallesFormativob = listaDetallesFormativob;
    }

    /**
     * @return the listaDetallesProfesionalb
     */
    public List<Detallesentrevista> getListaDetallesProfesionalb() {
        return listaDetallesProfesionalb;
    }

    /**
     * @param listaDetallesProfesionalb the listaDetallesProfesionalb to set
     */
    public void setListaDetallesProfesionalb(List<Detallesentrevista> listaDetallesProfesionalb) {
        this.listaDetallesProfesionalb = listaDetallesProfesionalb;
    }

    /**
     * @return the listaMotivacionb
     */
    public List<Detallesentrevista> getListaMotivacionb() {
        return listaMotivacionb;
    }

    /**
     * @param listaMotivacionb the listaMotivacionb to set
     */
    public void setListaMotivacionb(List<Detallesentrevista> listaMotivacionb) {
        this.listaMotivacionb = listaMotivacionb;
    }

    /**
     * @return the listaHabilidadesb
     */
    public List<Detallesentrevista> getListaHabilidadesb() {
        return listaHabilidadesb;
    }

    /**
     * @param listaHabilidadesb the listaHabilidadesb to set
     */
    public void setListaHabilidadesb(List<Detallesentrevista> listaHabilidadesb) {
        this.listaHabilidadesb = listaHabilidadesb;
    }

    /**
     * @return the listaNecesidadesb
     */
    public List<Detallesentrevista> getListaNecesidadesb() {
        return listaNecesidadesb;
    }

    /**
     * @param listaNecesidadesb the listaNecesidadesb to set
     */
    public void setListaNecesidadesb(List<Detallesentrevista> listaNecesidadesb) {
        this.listaNecesidadesb = listaNecesidadesb;
    }

    /**
     * @return the listaOtrosb
     */
    public List<Detallesentrevista> getListaOtrosb() {
        return listaOtrosb;
    }

    /**
     * @param listaOtrosb the listaOtrosb to set
     */
    public void setListaOtrosb(List<Detallesentrevista> listaOtrosb) {
        this.listaOtrosb = listaOtrosb;
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
     * @return the variable
     */
    public String getVariable() {
        return variable;
    }

    /**
     * @param variable the variable to set
     */
    public void setVariable(String variable) {
        this.variable = variable;
    }

    /**
     * @return the listaEntrevistas
     */
    public List<Entrevistas> getListaEntrevistas() {
        return listaEntrevistas;
    }

    /**
     * @param listaEntrevistas the listaEntrevistas to set
     */
    public void setListaEntrevistas(List<Entrevistas> listaEntrevistas) {
        this.listaEntrevistas = listaEntrevistas;
    }

    /**
     * @return the controEntrevista
     */
    public boolean isControEntrevista() {
        return controEntrevista;
    }

    /**
     * @param controEntrevista the controEntrevista to set
     */
    public void setControEntrevista(boolean controEntrevista) {
        this.controEntrevista = controEntrevista;
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

}
