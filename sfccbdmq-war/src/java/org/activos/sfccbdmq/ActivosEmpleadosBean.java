/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import org.talento.sfccbdmq.CargoxOrganigramaBean;
import javax.faces.application.Resource;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasrrhhFacade;
import org.beans.sfccbdmq.DireccionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.ExternosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Direcciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Externos;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Idiomas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "activosEmpleadoSfccbdmq")
@ViewScoped
public class ActivosEmpleadosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Empleados> empleados;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EntidadesFacade ejbEntidad;
    @EJB
    private DireccionesFacade ejbDirecciones;
    @EJB
    private ArchivosFacade ejbArchivos;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private HistorialcargosFacade ejbHistorial;
    @EJB
    private CabecerasrrhhFacade ejbCabeceras;
    @EJB
    private CabeceraempleadosFacade ejbCabecerasempleados;
    @EJB
    private RangoscabecerasFacade ejbrangocabecra;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private ExternosFacade ejbExternos;

    private Archivos archivoImagen;
    private Imagenes imagen;
    private Empleados empleado = new Empleados();
    private Empleados empleadoSeleccionado;
    // busqueda campos extras
    private Cabecerasrrhh cabeceraRrHh;
    private BigDecimal desdeNumerico;
    private BigDecimal hastaNumerico;
    private String textoBuscar;
    private Date desdeFecha;
    private Date hastaFecha;
    private List listaUsuarios;
    //
    private Direcciones direccion = new Direcciones();
    private Entidades entidad = new Entidades();
//    private Documentosempleado documentoempleado;
    private Estudios estudio;
    private Formulario formularioImagen = new Formulario();
    private Formulario formularioActa = new Formulario();
    private Direcciones empleadoDireccion;
    private Idiomas idioma;
    private String cedula;
    private String apellidos;
    private String email;
    private String nombres;
    private Organigrama organigrama;
    private Cargosxorganigrama cargo;
    private String buscarCedula;
    private List listadoempleados;
    private String nomCargoxOrg;
    private List listaCarxOrg;
    private Codigos genero;
//    private Codigos tipoNombramiento;
    private Codigos tipsang;
    private Historialcargos historia;
    private Codigos ecivil;
    private Date desde;
    private Date hasta;
    //private Tiposcontratos contrato;
    private Tiposcontratos contratoL;
    private Integer nombramiento;
    private Edificios edificio;
    private Oficinas oficina;
    // todo para perfiles 
    private Perfiles perfil;
    private boolean esExterno = false;
    private Externos externoEntidad;
    private String tipobuscar = "2";
    private String externo;
    private List<Externos> listaExternos;
    private List<Cabeceraempleados> listaCabeceraempleado;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean cxOrganigramaBean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    private Formulario formulario = new Formulario();
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private Resource reporte;

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
        String nombreForma = "ActivosEmpleadosVista";
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

    public void cambiaEdificio(ValueChangeEvent event) {
        edificio = (Edificios) event.getNewValue();
    }

    /**
     * Creates a new instance of EmpleadosBean
     */
    public ActivosEmpleadosBean() {
        empleados = new LazyDataModel<Empleados>() {

            @Override
            public List<Empleados> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };

    }

    public String buscar() {

        setEmpleados(new LazyDataModel<Empleados>() {
            @Override
            public List<Empleados> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                Map parametrosCampo = new HashMap();
//                String orden="o.entidad.apellidos ASC";

                try {
//                    String where = "o.activo=true and o.tipocontrato is not null";
//                    String whereCampo = "o.empleado.activo=true and o.empleado.tipocontrato is not null";
                    String where = "o.tipocontrato is not null";
                    String whereCampo = "o.empleado.tipocontrato is not null";
//                    String whereCampo = "o.empleado.activo=true and o.empleado.cargoactual is not null";
//                    String where = "o.activo=true and o.cargoactual is not null";

                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }

//                    parametros.put(";campo", campo);
//                    parametros.put(";suma", suma);
//                    if (getExternoEntidad() != null) {
//                        buscarCedula = getExternoEntidad().getEmpresa();
//                    }
                    if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                        where += " and upper(o.entidad.pin) like :pin";
                        whereCampo += " and upper(o.empleado.entidad.pin) like :pin";
                        parametros.put("pin", getBuscarCedula().toUpperCase());
                    }

                    if (!nombres.isEmpty() && nombres != null) {
                        where += " and upper(o.entidad.nombres) like :noms";
                        whereCampo += " and upper(o.empleado.entidad.nombres) like :noms";
                        parametros.put("noms", nombres.toUpperCase() + "%");
                    }

                    if (!apellidos.isEmpty() && apellidos != null) {
                        where += " and upper(o.entidad.apellidos) like :ape";
                        whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape";
                        parametros.put("ape", apellidos.toUpperCase() + "%");
                    }

                    if (!email.isEmpty() && getEmail() != null) {
                        where += " and upper(o.entidad.email) like :email";
                        whereCampo += " and upper(o.empleado.entidad.email) like :email";
                        parametros.put("email", getEmail().toUpperCase() + "%");
                    }
                    if (cxOrganigramaBean.getOrganigramaL() != null) {
                        if (!(cargo == null)) {
                            where += " and o.cargoactual=:carg";
                            whereCampo += " and o.empleado.cargoactual=:carg";
                            parametros.put("carg", cargo);
                        } else {
                            where += " and o.cargoactual.organigrama=:organigrama";
                            whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama";
                            parametros.put("organigrama", cxOrganigramaBean.getOrganigramaL());
                        }
                    }
                    if (!(genero == null)) {
                        where += " and o.entidad.genero=:genero";
                        whereCampo += " and o.empleado.entidad.genero=:genero";
                        parametros.put("genero", genero);
                    }
//                    if (!(tipoNombramiento == null)) {
//                        where += " and o.nombramiento=:nombramiento";
//                        whereCampo += " and o.empleado.nombramiento=:nombramiento";
//                        parametros.put("nombramiento", tipoNombramiento);
//                    }
                    if (!(tipsang == null)) {
                        where += " and o.entidad.tiposangre=:tipsag";
                        whereCampo += " and o.empleado.entidad.tiposangre=:tipsag";
                        parametros.put("tipsag", tipsang);
                    }
                    if (!(ecivil == null)) {
                        where += " and o.entidad.estadocivil=:ecivil";
                        whereCampo += " and o.empleado.entidad.estadocivil=:ecivil";
                        parametros.put("ecivil", ecivil);
                    }
                    if (desde != null || hasta != null) {
                        where += " and o.entidad.fecha between :desde and :hasta ";
                        whereCampo += " and o.empleado.entidad.fecha between :desde and :hasta ";
                        parametros.put("desde", desde);
                        parametros.put("hasta", hasta);
                    }
                    //CONTRATO
                    if (contratoL != null) {
                        where += " and o.tipocontrato=:contrato";
                        whereCampo += " and o.empleado.tipocontrato=:contrato";
                        parametros.put("contrato", contratoL);
                    }
                    //OFICINA
                    if (oficina != null) {
                        where += " and o.oficina=:oficina";
                        whereCampo += " and o.empleado.oficina=:oficina";
                        parametros.put("oficina", oficina);
                    }
                    //EDIFICIO
                    if (edificio != null) {
                        where += " and o.oficina.edificio=:edificio";
                        whereCampo += " and o.empleado.oficina.edificio=:edificio";
                        parametros.put("edificio", edificio);
                    }
//                    if (proyectosBean.getProyectoSeleccionado() != null) {
//                        where += " and o.proyecto=:proyecto";
//                        whereCampo += " and o.empleado.proyecto=:proyecto";
//                        parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
//                    }
                    //NOMBRAMIENTO
                    if (nombramiento != null) {
                        if (nombramiento == 1) {
                            where += " and o.tipocontrato.nombramiento=:nombramiento";
                            whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento";
                            parametros.put("nombramiento", true);
                        } else if (nombramiento == 2) {
                            where += " and o.tipocontrato.nombramiento=:nombramiento";
                            whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento";
                            parametros.put("nombramiento", false);
                        }
                    }
                    int total;

                    if (cabeceraRrHh != null) {
                        whereCampo += " and o.cabecera=:cabeccera";
                        parametros.put("cabeccera", cabeceraRrHh);
                        if (cabeceraRrHh.getTipodato() == 0) { // combo
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and o.valortexto=:valortexto";
                                parametros.put("valortexto", textoBuscar);
                            }
                        } else if (cabeceraRrHh.getTipodato() == 1) { // numerico
                            if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                                whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico";
                                parametros.put("desdeNumerico", desdeNumerico);
                                parametros.put("hastaNumerico", hastaNumerico);
                            }
                        } else if (cabeceraRrHh.getTipodato() == 3) { // numerico
                            if (!((desdeFecha == null) || (hastaFecha == null))) {
                                whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha";
                                parametros.put("hastaFecha", desdeFecha);
                                parametros.put("hastaFecha", hastaFecha);
                            }
                        } else if (cabeceraRrHh.getTipodato() == 2) { // texto
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and upper(o.valortexto) like :valorBuscar";
                                parametros.put("valorBuscar", textoBuscar + "%");
                            }
                        } else if (cabeceraRrHh.getTipodato() == 4) { // texto
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and upper(o.codigo) like :valorBuscar";
                                parametros.put("valorBuscar", textoBuscar + "%");
                            }
                        }
                        if (scs.length == 0) {
                            parametros.put(";orden", "o.empleado.entidad.apellidos ASC");
                        } else {
                            parametros.put(";orden", "o.empleado." + scs[0].getPropertyName()
                                    + (scs[0].isAscending() ? " ASC" : " DESC"));
                        }
                        parametros.put(";where", whereCampo);
                        total = ejbCabecerasempleados.contar(parametros);
                        int endIndex = i + pageSize;
                        if (endIndex > total) {
                            endIndex = total;
                        }

                        parametros.put(";inicial", i);
                        parametros.put(";final", endIndex);
                        empleados.setRowCount(total);
                        List<Empleados> el = new LinkedList<>();
                        List<Cabeceraempleados> cl = ejbCabecerasempleados.encontarParametros(parametros);
                        for (Cabeceraempleados c : cl) {
                            Empleados e = c.getEmpleado();
                            el.add(e);
                        }
                        return el;
                    } else {
                        parametros.put(";where", where);
                        if (scs.length == 0) {
                            parametros.put(";orden", "o.entidad.apellidos ASC");
                        } else {
                            parametros.put(";orden", "o." + scs[0].getPropertyName()
                                    + (scs[0].isAscending() ? " ASC" : " DESC"));
                        }

                        total = ejbEmpleados.contar(parametros);
                        int endIndex = i + pageSize;
                        if (endIndex > total) {
                            endIndex = total;
                        }

                        parametros.put(";inicial", i);
                        parametros.put(";final", endIndex);
                        empleados.setRowCount(total);
                        return ejbEmpleados.encontarParametros(parametros);
                    }
//                    int total = ejbEmpleados.contar(parametros);

                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }
        );

        return null;

    }

    public String salir() {
        return "RecursosVista.jsf?faces-redirect=true";
    }

    public String getFechaActa() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd  MMMMMM  yyyy");
        return "Quito, " + sdf.format(new Date());
    }

    public String modificar() {

        empleado = (Empleados) empleados.getRowData();
        empleadoSeleccionado = empleado;
        entidad = empleado.getEntidad();

        Map parametros = new HashMap();

        parametros.put(";orden", "o.grupo.id,o.descripcion");
        parametros.put(";where", "o.custodio=:custodio and o.fechabaja is null");
        parametros.put("custodio", empleado);
        try {
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            if (listaActivos.isEmpty()) {
                formularioActa.insertar();
                return null;
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Detalle de Bienes Por usuario : ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Bienes.jasper"),
                    listaActivos, "Bienes" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ActivosEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public void cambiaexterno(ValueChangeEvent event) {
        if (listaExternos == null) {
            return;
        }
        externoEntidad = null;
        externo = null;
        String newWord = (String) event.getNewValue();
        for (Externos p : listaExternos) {
            int tbuscar = 0;
            if (getTipobuscar() != null) {
                tbuscar = Integer.parseInt(getTipobuscar());
            }

            switch (Math.abs(tbuscar)) {
                case 1:
                    //cedula
                    if (p.getEmpresa().compareToIgnoreCase(newWord) == 0) {
                        externoEntidad = new Externos();
                        externoEntidad = p;
                        return;
                    }
                    break;
                case 2:
                    //nombre
                    if (p.getNombre().compareToIgnoreCase(newWord) == 0) {
                        externoEntidad = new Externos();
                        externoEntidad = p;
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void externoCedulaChangeEventHandler(TextChangeEvent event) {
        //Buscra Por cedula
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        externoEntidad = null;
        externo = null;
        Map parametros = new HashMap();
        String where = "  ";
        //ruc
        where += " o.empresa like :ruc";
        parametros.put("ruc", newWord + "%");
        parametros.put(";orden", "o.empresa");
        try {
            parametros.put(";where", where);
            int total = 50;
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaExternos = ejbExternos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void nombreChangeEventHandler(TextChangeEvent event) {
        // Buscar por nombre
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        int tbuscar = 0;
        Map parametros = new HashMap();
        String where = "  ";
        externoEntidad = null;
        externo = null;
        where += " upper(o.nombre) like :nombre";
        parametros.put("nombre", "%" + newWord.toUpperCase() + "%");
        parametros.put(";orden", "o.nombre");
        try {
            parametros.put(";where", where);
            int total = 15;
            parametros.put(";inicial", 0);
            parametros.put(";final", total);
            listaExternos = ejbExternos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the empleados
     */
    public LazyDataModel<Empleados> getEmpleados() {
        return empleados;
    }

    /**
     * @param empleados the empleados to set
     */
    public void setEmpleados(LazyDataModel<Empleados> empleados) {
        this.empleados = empleados;
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
     * @return the estudio
     */
    public Estudios getEstudio() {
        return estudio;
    }

    /**
     * @param estudio the estudio to set
     */
    public void setEstudio(Estudios estudio) {
        this.estudio = estudio;
    }

    /**
     * @return the empleadoDireccion
     */
    public Direcciones getEmpleadoDireccion() {
        return empleadoDireccion;
    }

    /**
     * @param empleadoDireccion the empleadoDireccion to set
     */
    public void setEmpleadoDireccion(Direcciones empleadoDireccion) {
        this.empleadoDireccion = empleadoDireccion;
    }

    /**
     * @return the idioma
     */
    public Idiomas getIdioma() {
        return idioma;
    }

    /**
     * @param idioma the idioma to set
     */
    public void setIdioma(Idiomas idioma) {
        this.idioma = idioma;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
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
     * @return the Apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the Apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the nombres
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
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
     * @return the genero
     */
    public Codigos getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Codigos genero) {
        this.genero = genero;
    }

    /**
     * @return the tipsang
     */
    public Codigos getTipsang() {
        return tipsang;
    }

    /**
     * @param tipsang the tipsang to set
     */
    public void setTipsang(Codigos tipsang) {
        this.tipsang = tipsang;
    }

    /**
     * @return the ecivil
     */
    public Codigos getEcivil() {
        return ecivil;
    }

    /**
     * @param ecivil the ecivil to set
     */
    public void setEcivil(Codigos ecivil) {
        this.ecivil = ecivil;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the contratoL
     */
    public Tiposcontratos getContratoL() {
        return contratoL;
    }

    /**
     * @param contratoL the contratoL to set
     */
    public void setContratoL(Tiposcontratos contratoL) {
        this.contratoL = contratoL;
    }

    /**
     * @return the nombramiento
     */
    public Integer getNombramiento() {
        return nombramiento;
    }

    /**
     * @param nombramiento the nombramiento to set
     */
    public void setNombramiento(Integer nombramiento) {
        this.nombramiento = nombramiento;
    }

    /**
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    /**
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
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
     * @return the direccion
     */
    public Direcciones getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Direcciones direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the archivoImagen
     */
    public Archivos getArchivoImagen() {
        return archivoImagen;
    }

    /**
     * @param archivoImagen the archivoImagen to set
     */
    public void setArchivoImagen(Archivos archivoImagen) {
        this.archivoImagen = archivoImagen;
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
     * @return the cxOrganigramaBean
     */
    public CargoxOrganigramaBean getCxOrganigramaBean() {
        return cxOrganigramaBean;
    }

    /**
     * @param cxOrganigramaBean the cxOrganigramaBean to set
     */
    public void setCxOrganigramaBean(CargoxOrganigramaBean cxOrganigramaBean) {
        this.cxOrganigramaBean = cxOrganigramaBean;
    }

    /**
     * @return the oficinasBean
     */
    public OficinasBean getOficinasBean() {
        return oficinasBean;
    }

    /**
     * @param oficinasBean the oficinasBean to set
     */
    public void setOficinasBean(OficinasBean oficinasBean) {
        this.oficinasBean = oficinasBean;
    }

    /**
     * @return the formularioImagen
     */
    public Formulario getFormularioImagen() {
        return formularioImagen;
    }

    /**
     * @param formularioImagen the formularioImagen to set
     */
    public void setFormularioImagen(Formulario formularioImagen) {
        this.formularioImagen = formularioImagen;
    }

    /**
     * @return the listaCabeceraempleado
     */
    public List<Cabeceraempleados> getListaCabeceraempleado() {
        return listaCabeceraempleado;
    }

    /**
     * @param listaCabeceraempleado the listaCabeceraempleado to set
     */
    public void setListaCabeceraempleado(List<Cabeceraempleados> listaCabeceraempleado) {
        this.listaCabeceraempleado = listaCabeceraempleado;
    }

    /**
     * @return the cabeceraRrHh
     */
    public Cabecerasrrhh getCabeceraRrHh() {
        return cabeceraRrHh;
    }

    /**
     * @param cabeceraRrHh the cabeceraRrHh to set
     */
    public void setCabeceraRrHh(Cabecerasrrhh cabeceraRrHh) {
        this.cabeceraRrHh = cabeceraRrHh;
    }

    /**
     * @return the desdeNumerico
     */
    public BigDecimal getDesdeNumerico() {
        return desdeNumerico;
    }

    /**
     * @param desdeNumerico the desdeNumerico to set
     */
    public void setDesdeNumerico(BigDecimal desdeNumerico) {
        this.desdeNumerico = desdeNumerico;
    }

    /**
     * @return the hastaNumerico
     */
    public BigDecimal getHastaNumerico() {
        return hastaNumerico;
    }

    /**
     * @param hastaNumerico the hastaNumerico to set
     */
    public void setHastaNumerico(BigDecimal hastaNumerico) {
        this.hastaNumerico = hastaNumerico;
    }

    /**
     * @return the textoBuscar
     */
    public String getTextoBuscar() {
        return textoBuscar;
    }

    /**
     * @param textoBuscar the textoBuscar to set
     */
    public void setTextoBuscar(String textoBuscar) {
        this.textoBuscar = textoBuscar;
    }

    /**
     * @return the desdeFecha
     */
    public Date getDesdeFecha() {
        return desdeFecha;
    }

    /**
     * @param desdeFecha the desdeFecha to set
     */
    public void setDesdeFecha(Date desdeFecha) {
        this.desdeFecha = desdeFecha;
    }

    /**
     * @return the hastaFecha
     */
    public Date getHastaFecha() {
        return hastaFecha;
    }

    /**
     * @param hastaFecha the hastaFecha to set
     */
    public void setHastaFecha(Date hastaFecha) {
        this.hastaFecha = hastaFecha;
    }

    /**
     * @return the listaUsuarios
     */
    public List getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    /**
     * @return the empleadoSeleccionado
     */
    public Empleados getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }

    /**
     * @param empleadoSeleccionado the empleadoSeleccionado to set
     */
    public void setEmpleadoSeleccionado(Empleados empleadoSeleccionado) {
        this.empleadoSeleccionado = empleadoSeleccionado;
    }

    /**
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    /**
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the formularioActa
     */
    public Formulario getFormularioActa() {
        return formularioActa;
    }

    /**
     * @param formularioActa the formularioActa to set
     */
    public void setFormularioActa(Formulario formularioActa) {
        this.formularioActa = formularioActa;
    }

    /**
     * @return the esExterno
     */
    public boolean isEsExterno() {
        return esExterno;
    }

    /**
     * @param esExterno the esExterno to set
     */
    public void setEsExterno(boolean esExterno) {
        this.esExterno = esExterno;
    }

    /**
     * @return the externoEntidad
     */
    public Externos getExternoEntidad() {
        return externoEntidad;
    }

    /**
     * @param externoEntidad the externoEntidad to set
     */
    public void setExternoEntidad(Externos externoEntidad) {
        this.externoEntidad = externoEntidad;
    }

    /**
     * @return the tipobuscar
     */
    public String getTipobuscar() {
        return tipobuscar;
    }

    /**
     * @param tipobuscar the tipobuscar to set
     */
    public void setTipobuscar(String tipobuscar) {
        this.tipobuscar = tipobuscar;
    }

    /**
     * @return the externo
     */
    public String getExterno() {
        return externo;
    }

    /**
     * @param externo the externo to set
     */
    public void setExterno(String externo) {
        this.externo = externo;
    }

    /**
     * @return the listaExternos
     */
    public List<Externos> getListaExternos() {
        return listaExternos;
    }

    /**
     * @param listaExternos the listaExternos to set
     */
    public void setListaExternos(List<Externos> listaExternos) {
        this.listaExternos = listaExternos;
    }

}
