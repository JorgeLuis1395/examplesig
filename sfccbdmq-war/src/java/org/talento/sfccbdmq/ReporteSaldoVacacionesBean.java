/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import org.activos.sfccbdmq.BajasActivosBean;
import org.activos.sfccbdmq.OficinasBean;
import org.presupuestos.sfccbdmq.AsignacionesBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasrrhhFacade;
import org.beans.sfccbdmq.DireccionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Direcciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Idiomas;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteSaldoVacaionesSfccbdmq")
@ViewScoped
public class ReporteSaldoVacacionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Empleados> empleados;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EntidadesFacade ejbEntidad;
    @EJB
    private DireccionesFacade ejbDirecciones;
    @EJB
    private VacacionesFacade ejbVacaciones;
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
    private Formulario formularioExportar = new Formulario();
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
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteSaldoVacacionesVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            return;
//            parametrosSeguridad.cerraSession();
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
    public ReporteSaldoVacacionesBean() {

    }

    public String buscar() {
        Map parametros = new HashMap();
        Map parametrosCampo = new HashMap();
        try {
            String where = "o.activo=true and o.tipocontrato is not null ";
            String whereCampo = "o.empleado.activo=true and o.empleado.tipocontrato is not null ";
//                  
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
                where += " and upper(o.entidad.apellidos) like :ape ";
                whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                parametros.put("ape", apellidos.toUpperCase() + "%");
            }

            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email)like:email ";
                whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (cxOrganigramaBean.getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    whereCampo += " and o.empleado.cargoactual=:carg ";
                    parametros.put("carg", cargo);
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", cxOrganigramaBean.getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                whereCampo += " and o.empleado.entidad.genero=:genero ";
                parametros.put("genero", genero);
            }
            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                whereCampo += " and o.empleado.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", tipsang);
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                whereCampo += " and o.empleado.entidad.estadocivil=:ecivil ";
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
                where += " and o.tipocontrato=:contrato ";
                whereCampo += " and o.empleado.tipocontrato=:contrato ";
                parametros.put("contrato", contratoL);
            }
            //OFICINA
            if (oficina != null) {
                where += " and o.oficina=:oficina ";
                whereCampo += " and o.empleado.oficina=:oficina ";
                parametros.put("oficina", oficina);
            }
            //EDIFICIO
            if (edificio != null) {
                where += " and o.oficina.edificio=:edificio ";
                whereCampo += " and o.empleado.oficina.edificio=:edificio ";
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
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (nombramiento == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }

            if (cabeceraRrHh != null) {
                whereCampo += " and o.cabecera=:cabeccera ";
                parametros.put("cabeccera", cabeceraRrHh);
                if (null != cabeceraRrHh.getTipodato()) {
                    switch (cabeceraRrHh.getTipodato()) {
                        case 0:
                            // combo
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and o.valortexto=:valortexto ";
                                parametros.put("valortexto", textoBuscar);
                            }
                            break;
                        case 1:
                            // numerico
                            if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                                whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                                parametros.put("desdeNumerico", desdeNumerico);
                                parametros.put("hastaNumerico", hastaNumerico);
                            }
                            break;
                        case 3:
                            // numerico
                            if (!((desdeFecha == null) || (hastaFecha == null))) {
                                whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                                parametros.put("hastaFecha", desdeFecha);
                                parametros.put("hastaFecha", hastaFecha);
                            }
                            break;
                        case 2:
                            // texto
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                                parametros.put("valorBuscar", textoBuscar + "%");
                            }
                            break;
                        case 4:
                            // texto
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and upper(o.codigo) like :valorBuscar ";
                                parametros.put("valorBuscar", textoBuscar + "%");
                            }
                            break;
                        default:
                            break;
                    }
                }

                empleados = new LinkedList<>();
                parametros.put(";where", whereCampo);
                parametros.put(";orden", "o.empleado.entidad.apellidos ");
                List<Cabeceraempleados> cl = ejbCabecerasempleados.encontarParametros(parametros);
                for (Cabeceraempleados c : cl) {
                    Empleados e = c.getEmpleado();
                    empleados.add(e);
                }
            } else {
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos ");

                empleados = ejbEmpleados.encontarParametros(parametros);
            }
//                    int total = ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the empleados
     */
    public List<Empleados> getEmpleados() {
        return empleados;
    }

    /**
     * @param empleados the empleados to set
     */
    public void setEmpleados(List<Empleados> empleados) {
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

    public String multimediaListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            try {
                File file = i.getFile();
                archivoImagen = new Archivos();
                imagen = new Imagenes();
                archivoImagen.setArchivo(Files.readAllBytes(file.toPath()));
                imagen.setNombre(i.getFileName());
                imagen.setFechaingreso(new Date());
//                archivoImagen.setTipo(i.getContentType());
            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
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

    public SelectItem[] getComboRango() {
        Cabeceraempleados preg = listaCabeceraempleado.get(formularioImagen.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";orden", "o.texto asc");
        parametros.put(";where", "o.cabecera=:cabecerains");
        parametros.put("cabecerains", preg.getCabecera());
        try {
            List<Rangoscabeceras> lrcab = ejbrangocabecra.encontarParametros(parametros);
            return Combos.comboToStrings(lrcab, true);
//             return listaRangocaberains;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboRangoBuscar() {
        if (cabeceraRrHh == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.texto asc");
        parametros.put(";where", "o.cabecera=:cabecerains");
        parametros.put("cabecerains", cabeceraRrHh);
        try {
            List<Rangoscabeceras> lrcab = ejbrangocabecra.encontarParametros(parametros);
            return Combos.comboToStrings(lrcab, true);
//             return listaRangocaberains;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public void cambiaPin(ValueChangeEvent ve) {
        String valor = (String) ve.getNewValue();
        if ((valor == null) || (valor.isEmpty())) {
            formulario.cancelar();
        } else {
            Map parametros = new HashMap();
            String where = " ";
            where += "  o.pin = :pin";
            parametros.put("pin", valor);
            parametros.put(";where", where);
            try {
                List<Entidades> el = ejbEntidad.encontarParametros(parametros);
                for (Entidades e : el) {
                    entidad = e;
                    entidad.setActivo(Boolean.TRUE);
//                    modificarSeleccionado();
                    direccion = entidad.getDireccion();
                    if (direccion == null) {
                        direccion = new Direcciones();
                    }

                    ejbEntidad.edit(e, "ACTUALIZA BORRADO EN FAMILIARES");
//                    formulario.editar();
                    return;
                }
            } catch (ConsultarException | GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (empleado == null) {
                setEmpleado(new Empleados());
                setEntidad(new Entidades());
                archivoImagen = null;
                imagen = null;
                entidad.setPin(valor);
                setDireccion(new Direcciones());
//        empleado.setOficina(cxOrganigramaBean.getOrganigramaL().get);
//        cedula = null;
                // trearer los datos de la cabecera
                parametros = new HashMap();
                parametros.put(";orden", "o.grupo,o.orden");
                parametros.put(";where", "o.activo=true");
                listaCabeceraempleado = new LinkedList<>();
                try {
                    List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
                    for (Cabecerasrrhh crr : listaCab) {
                        Cabeceraempleados c = new Cabeceraempleados();
                        c.setCabecera(crr);
                        c.setTipodato(crr.getTipodato());
                        c.setAyuda(crr.getAyuda());
                        c.setCodigo(crr.getCodigo());
                        c.setGrupo(crr.getGrupo());
                        c.setTexto(crr.getTexto());
                        c.setOrden(crr.getOrden());
                        c.setDatoimpresion(crr.getDatoimpresion());
                        listaCabeceraempleado.add(c);
                    }
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                formulario.insertar();

            }
        }
    }

    public Empleados traer(Integer id) {
        try {
            return ejbEmpleados.find(id);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComoEmpleadosOrganigrama() {
        if (empleadoSeleccionado == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cargoactual.organigrama=:organigrama");
        parametros.put(";orden", "o.entidad.apellidos");
        parametros.put("organigrama", empleadoSeleccionado.getCargoactual().getOrganigrama());
        try {
            List<Empleados> elista = ejbEmpleados.encontarParametros(parametros);
            return Combos.getSelectItems(elista, true);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerCedula(String cedula) {
        if ((cedula == null) || (cedula.isEmpty())) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.pin=:pin");
        parametros.put("pin", cedula);
        try {
            List<Entidades> le = ejbEntidad.encontarParametros(parametros);
            for (Entidades e : le) {
                return e.toString();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboClasificadores() {
        List<Codigos> codigos = codigosBean.traerCodigoMaestro("CLANOM");
        int size = codigos.size() + 1;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        items[0] = new SelectItem(null, "---");
        i++;
        for (Codigos x : codigos) {
            items[i++] = new SelectItem(x.getDescripcion(), x.getDescripcion() + " - " + x.getNombre());
        }
        return items;
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

    public SelectItem[] getComboProyectos() {
        if (empleado == null) {
            return null;
        }
        if (empleado.getPartida() == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anio = c.get(Calendar.YEAR);
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.codigo=:codigo");
        parametros.put("anio", anio);
        parametros.put("codigo", empleado.getPartida());
        parametros.put(";orden", "o.proyecto.codigo,o.proyecto.nombre");
        try {
            List<Proyectos> pl = new LinkedList<>();
            List<Asignaciones> asignaciones = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones a : asignaciones) {
                estaProyecto(pl, a.getProyecto());
            }
            return Combos.getSelectItems(pl, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void estaProyecto(List<Proyectos> pl, Proyectos p) {
        for (Proyectos p1 : pl) {
            if (p1.equals(p)) {
                return;
            }

        }
        pl.add(p);
    }

    public String getCabecerasEmpleado(Cabecerasrrhh c, Empleados empleado) {
//        empleado=empleados.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.cabecera=:cabecera");
        parametros.put("empleado", empleado);
        parametros.put("cabecera", c);
//        parametros.put(";orden", "o.cabeccera.id");
        try {
            List<Cabeceraempleados> listCabEmmp = ejbCabecerasempleados.encontarParametros(parametros);
            for (Cabeceraempleados ca : listCabEmmp) {
                if (c.getTipodato() == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    if (ca.getValorfecha() != null) {
                        return sdf.format(ca.getValorfecha());
                    }
                } else if (c.getTipodato() == 4) {
                    return ca.getCodigo();
                } else if (c.getTipodato() == 0) {
                    return ca.getValortexto();
                } else if (c.getTipodato() == 1) {
                    if (ca.getValornumerico() != null) {
                        return ca.getValornumerico().toString();
                    }
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Cabecerasrrhh> getCabeceras() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true");
        parametros.put(";orden", "o.texto");
        try {
            return ejbCabeceras.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerSaldo(Empleados e) {
        // Traer la suma de vacaciones
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";campo", "o.empleado.id");
        parametros.put("empleado", e);
        parametros.put(";suma", "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),"
                + "sum(o.utilizadofs),sum(o.proporcional),sum(o.perdido)");
        String saldo = "";
        try {

            List<Object[]> lista = ejbVacaciones.sumar(parametros);
            for (Object[] o : lista) {
                Long utilizado = (Long) o[1];
                Long utilizadofs = (Long) o[4];
                Long ganado = (Long) o[2];
                Long ganadofs = (Long) o[3];
                Long proporcional = (Long) o[5];
                Long perdido = (Long) o[6];
                if (utilizado == null) {
                    utilizado = new Long(0);
                }
                if (utilizadofs == null) {
                    utilizadofs = new Long(0);
                }
                if (ganado == null) {
                    ganado = new Long(0);
                }
                if (ganadofs == null) {
                    ganadofs = new Long(0);
                }
                if (proporcional == null) {
                    proporcional = new Long(0);
                }
                utilizado += utilizadofs;
                ganado += ganadofs;
                // vamos con lo utilizado

                saldo += "<p><strong>Efectivo " + "</strong> " + ejbVacaciones.saldoDias(ganado - proporcional, e.getOperativo(), false);
// pedido por delia 5Marzo2018 descomentado por delia 9de Marzo 2018
//                saldo += "<p><strong>Efectivo " + "</strong> " + ejbVacaciones.saldoDias(ganado -proporcional-utilizado-utilizadofs , e.getOperativo(), false);
                saldo += "<p><strong>Proporcional" + "</strong> " + ejbVacaciones.saldoDias(proporcional, e.getOperativo(), false);
                if (!((perdido == null) || (perdido > 0))) {
                    saldo += "<p><strong>Perdido " + "</strong> " + perdido + " días";
                }
                saldo += "<p><strong>Utilizado " + "</strong>" + ejbVacaciones.saldoDias(utilizado, e.getOperativo(), true) + "</P>";
                double cuantosDias = ejbVacaciones.cuatosDias(ganado, e.getOperativo(), false)
                        - ejbVacaciones.cuatosDias(utilizado, e.getOperativo(), true);
                if (cuantosDias > 60) {

                    saldo += "<strong>Saldo </strong>" + "60" + " días, "
                            + "00 horas,"
                            + "00 minutos";
                } else {
//                    delia deci comemntele lo que sea
                    saldo += "<strong>Saldo </strong>" + ejbVacaciones.saldoDias(ganado - utilizado, e.getOperativo(), true);
//                    if (e.getOperativo()) {
//                        long cuanto = ganado - utilizado * 8 / 24;
//                        saldo += "<strong>Saldo </strong>" + ejbVacaciones.saldoDias(cuanto, false, false);
//                    } else {
//                        saldo += "<strong>Saldo </strong>" + ejbVacaciones.saldoDias(ganado - utilizado, e.getOperativo(), true);
//                    }
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }

    public String traerSaldoUsuario(Empleados e) {
        // Traer la suma de vacaciones
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";campo", "o.empleado.id");
        parametros.put("empleado", e);
        parametros.put(";suma", "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),"
                + "sum(o.utilizadofs),sum(o.proporcional),sum(o.perdido)");
        String saldo = "";
        try {

            List<Object[]> lista = ejbVacaciones.sumar(parametros);
            for (Object[] o : lista) {
                Long utilizado = (Long) o[1];
                Long utilizadofs = (Long) o[4];
                Long ganado = (Long) o[2];
                Long ganadofs = (Long) o[3];
                Long proporcional = (Long) o[5];
                Long perdido = (Long) o[6];
                if (utilizado == null) {
                    utilizado = new Long(0);
                }
                if (utilizadofs == null) {
                    utilizadofs = new Long(0);
                }
                if (ganado == null) {
                    ganado = new Long(0);
                }
                if (ganadofs == null) {
                    ganadofs = new Long(0);
                }
                if (proporcional == null) {
                    proporcional = new Long(0);
                }
                utilizado += utilizadofs;
                ganado += ganadofs;
                // vamos con lo utilizado
                long efectivo = ganado - (proporcional);
                if (utilizado > efectivo) {
                    // efectivo es 0
                    saldo += "<p><strong>Efectivo : " + "</strong> 0 Días, 0 Horas, 0 Minutos</p>";
                    long resta=utilizado-efectivo;
                    saldo += "<p><strong>Proporcional : " + "</strong> " + ejbVacaciones.saldoDias(proporcional-resta, e.getOperativo(), true) + "</p>";;

                } else {
                    saldo += "<p><strong>Efectivo " + "</strong> " + ejbVacaciones.saldoDias(efectivo-utilizado, e.getOperativo(), false) + "</p>";
                    saldo += "<p><strong>Proporcional" + "</strong> " + ejbVacaciones.saldoDias(proporcional, e.getOperativo(), false) + "</p>";
                }
                
                saldo += "<p><strong>Utilizado " + "</strong>" + ejbVacaciones.saldoDias(utilizado, e.getOperativo(), true) + "</P>";
                double cuantosDias = ejbVacaciones.cuatosDias(ganado, e.getOperativo(), false)
                        - ejbVacaciones.cuatosDias(utilizado, e.getOperativo(), true);
                if (cuantosDias > 60) {

                    saldo += "<p><strong>Saldo </strong>" + "60" + " días, "
                            + "00 horas,"
                            + "00 minutos" + "</p>";;
                } else {
//                    delia deci comemntele lo que sea
                    saldo += "<p><strong>Saldo </strong>" + ejbVacaciones.saldoDias(ganado - utilizado, e.getOperativo(), true) + "</p>";
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }
    
    public String traerSaldoUsuarioSinEfectivoPersonal(Empleados e) {
        // Traer la suma de vacaciones
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";campo", "o.empleado.id");
        parametros.put("empleado", e);
        parametros.put(";suma", "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),"
                + "sum(o.utilizadofs),sum(o.proporcional),sum(o.perdido)");
        String saldo = "";
        try {

            List<Object[]> lista = ejbVacaciones.sumar(parametros);
            for (Object[] o : lista) {
                Long utilizado = (Long) o[1];
                Long utilizadofs = (Long) o[4];
                Long ganado = (Long) o[2];
                Long ganadofs = (Long) o[3];
                Long proporcional = (Long) o[5];
                Long perdido = (Long) o[6];
                if (utilizado == null) {
                    utilizado = new Long(0);
                }
                if (utilizadofs == null) {
                    utilizadofs = new Long(0);
                }
                if (ganado == null) {
                    ganado = new Long(0);
                }
                if (ganadofs == null) {
                    ganadofs = new Long(0);
                }
                if (proporcional == null) {
                    proporcional = new Long(0);
                }
                utilizado += utilizadofs;
                ganado += ganadofs;
                // vamos con lo utilizado
                long efectivo = ganado - (proporcional);
//                if (utilizado > efectivo) {
//                    // efectivo es 0
//                    saldo += "<p><strong>Efectivo : " + "</strong> 0 Días, 0 Horas, 0 Minutos</p>";
//                    long resta=utilizado-efectivo;
//                    saldo += "<p><strong>Proporcional : " + "</strong> " + ejbVacaciones.saldoDias(proporcional-resta, e.getOperativo(), true) + "</p>";;
//
//                } else {
//                    saldo += "<p><strong>Efectivo " + "</strong> " + ejbVacaciones.saldoDias(efectivo-utilizado, e.getOperativo(), false) + "</p>";
//                    saldo += "<p><strong>Proporcional" + "</strong> " + ejbVacaciones.saldoDias(proporcional, e.getOperativo(), false) + "</p>";
//                }

                double cuantosDias = ejbVacaciones.cuatosDias(ganado, e.getOperativo(), false)
                        - ejbVacaciones.cuatosDias(utilizado, e.getOperativo(), true);
                if (cuantosDias > 60) {

                    saldo += "<p><strong>Saldo </strong>" + "60" + " días, "
                            + "00 horas,"
                            + "00 minutos" + "</p>";;
                } else {
//                    delia deci comemntele lo que sea
                    saldo += "<p><strong>Saldo </strong>" + ejbVacaciones.saldoDias(ganado - utilizado, e.getOperativo(), true) + "</p>";
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteSaldoVacacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }

    public String exportar() {

        try {

            DocumentoXLS xls = new DocumentoXLS("Saldo e Vacaciones");
            List<AuxiliarReporte> campos = new LinkedList<>();

            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha de Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Apellidos"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombres"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proceso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cargo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Unidad Administrativa"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Efectivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proporcional"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Perdido"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Utilizado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo"));
            xls.agregarFila(campos, true);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
            for (Empleados e : empleados) {
                campos = new LinkedList<>();
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(e.getFechaingreso()))); // Fecha de ingreso
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getPin())); // CI
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getApellidos())); // Apellidos y nombres
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getNombres())); // Apellidos y nombres
                if (e.getCargoactual() == null) {
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "")); // Nombre Organigrama
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "")); // Nombre Organigrama
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "")); // Nombre Organigrama
                } else {
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getOrganigrama().getSuperior() == null ? "" : e.getCargoactual().getOrganigrama().getSuperior().getNombre())); // Nombre Organigrama
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getCargo().getNombre())); // Nombre Organigrama
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getOrganigrama().getNombre())); // Nombre Organigrama
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado");
                parametros.put(";campo", "o.empleado.id");
                parametros.put("empleado", e);
                parametros.put(";suma", "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),sum(o.utilizadofs),sum(o.proporcional),sum(o.perdido)");
                String saldo = "";
                Long utilizado = (0l);
                Long utilizadofs = (0l);
                Long ganado = (0l);
                Long ganadofs = (0l);
                Long proporcional = (0l);
                Long perdido = (0l);
                List<Object[]> lista = ejbVacaciones.sumar(parametros);
                for (Object[] o : lista) {
                    utilizado = (Long) o[1];
                    utilizadofs = (Long) o[4];
                    ganado = (Long) o[2];
                    ganadofs = (Long) o[3];
                    proporcional = (Long) o[5];
                    perdido = (Long) o[6];
                    if (utilizado == null) {
                        utilizado = new Long(0);
                    }
                    if (utilizadofs == null) {
                        utilizadofs = new Long(0);
                    }
                    if (ganado == null) {
                        ganado = new Long(0);
                    }
                    if (ganadofs == null) {
                        ganadofs = new Long(0);
                    }
                    if (proporcional == null) {
                        proporcional = new Long(0);
                    }
                    if (perdido == null) {
                        perdido = new Long(0);
                    }
                    utilizado += utilizadofs;
                    ganado += ganadofs;

                }
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, (int) (ganado - proporcional))); // Nombre Organigrama
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, proporcional.intValue())); // Nombre Organigrama
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, perdido.intValue())); // Nombre Organigrama
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, utilizado.intValue())); // Nombre Organigrama
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, (int) (ganado - utilizado))); // Nombre Organigrama
                xls.agregarFila(campos, false);

            }
            reporte = xls.traerRecurso();
            getFormularioExportar().insertar();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDistributivoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the formularioExportar
     */
    public Formulario getFormularioExportar() {
        return formularioExportar;
    }

    /**
     * @param formularioExportar the formularioExportar to set
     */
    public void setFormularioExportar(Formulario formularioExportar) {
        this.formularioExportar = formularioExportar;
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

    public String traerSaldoDias(Integer cuanto) {
        if (cuanto != null) {
            return ejbVacaciones.saldoDias(cuanto.longValue(), true, true);
        }
        return "";
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
}