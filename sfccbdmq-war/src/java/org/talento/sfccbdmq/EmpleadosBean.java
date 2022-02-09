/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.activos.sfccbdmq.BajasActivosBean;
import org.activos.sfccbdmq.OficinasBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Codificador;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasrrhhFacade;
import org.beans.sfccbdmq.DireccionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.entidades.sfccbdmq.Actas;
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
import org.entidades.sfccbdmq.Grupocabeceras;
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
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.AsignacionesBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "empleados")
@ViewScoped
public class EmpleadosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Empleados> empleados;
    private List<Empleados> listaEmpleados;
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
    private List<Empleados> listaUsuarios;
    //
    private Direcciones direccion = new Direcciones();
    private Entidades entidad = new Entidades();
//    private Documentosempleado documentoempleado;
    private Estudios estudio;
    private Formulario formularioImagen = new Formulario();
    private Formulario formularioCese = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Direcciones empleadoDireccion;
    private Idiomas idioma;
    private String cedula;
    private String apellidos;
    private String apellidosAdicional;
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
    private Integer tipoHorario;
    private Date desde;
    private Date hasta;
    //private Tiposcontratos contrato;
    private Tiposcontratos contratoL;
    private Integer nombramiento;
    private Edificios edificio;
    private Oficinas oficina;
    // todo para perfiles 
    private Perfiles perfil;
    private Grupocabeceras cabecerasVer;
    private List<Cabeceraempleados> listaCabeceraempleado;
    private Resource reporteXls;
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

    private Empleados empleadoAdicional;
    private Entidades entidadAdicional = new Entidades();

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
        String nombreForma = "EmpleadosVista";
        if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
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
    public EmpleadosBean() {
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
                    String where = "o.activo=true and o.cargoactual is not null and o.fechasalida is null";
                    String whereCampo = "o.empleado.activo=true and "
                            + " o.empleado.cargoactual  is not null and o.empleado.fechasalida is null";
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
                    if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                        where += " and upper(o.entidad.pin) like :pin ";
                        whereCampo += " and upper(o.empleado.entidad.pin) like :pin ";
                        parametros.put("pin", getBuscarCedula().toUpperCase());
                    }

                    if (!nombres.isEmpty() && nombres != null) {
                        where += " and upper(o.entidad.nombres) like :noms ";
                        whereCampo += " and upper(o.empleado.entidad.nombres) like :noms ";
                        parametros.put("noms", nombres.toUpperCase() + "%");
                    }
                    if (tipoHorario != null) {
                        if (tipoHorario == 1) {
                            where += " and o.tipohorario=true";
                            whereCampo += " and o.empleado.tipohorario=true";
                        } else if (tipoHorario == 2) {
                            where += " and o.tipohorario=:false";
                            whereCampo += " and o.empleado.tipohorario=:false";
                        }

                    }
                    if (!apellidos.isEmpty() && apellidos != null) {
                        where += " and upper(o.entidad.apellidos) like :ape ";
                        whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                        parametros.put("ape", apellidos.toUpperCase() + "%");
                    }

                    if (!email.isEmpty() && getEmail() != null) {
                        where += " and upper(o.entidad.email) like :email ";
                        whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                        parametros.put("email", getEmail().toUpperCase() + "%");
                    }
                    if (cxOrganigramaBean.getOrganigramaL() != null) {
                        if (!(cargo == null)) {
                            where += " and o.cargoactual=:carg ";
                            whereCampo += " and o.empleado.cargoactual= :carg ";
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
//                    if (!(tipoNombramiento == null)) {
//                        where += " and o.nombramiento=:nombramiento";
//                        whereCampo += " and o.empleado.nombramiento=:nombramiento";
//                        parametros.put("nombramiento", tipoNombramiento);
//                    }
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
                    int total;

                    if (cabeceraRrHh != null) {
                        whereCampo += " and o.cabecera=:cabeccera ";
                        parametros.put("cabeccera", cabeceraRrHh);
                        if (cabeceraRrHh.getTipodato() == 0) { // combo
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and o.valortexto=:valortexto ";
                                parametros.put("valortexto", textoBuscar);
                            }
                        } else if (cabeceraRrHh.getTipodato() == 1) { // numerico
                            if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                                whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                                parametros.put("desdeNumerico", desdeNumerico);
                                parametros.put("hastaNumerico", hastaNumerico);
                            }
                        } else if (cabeceraRrHh.getTipodato() == 3) { // numerico
                            if (!((desdeFecha == null) || (hastaFecha == null))) {
                                whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                                parametros.put("hastaFecha", desdeFecha);
                                parametros.put("hastaFecha", hastaFecha);
                            }
                        } else if (cabeceraRrHh.getTipodato() == 2) { // texto
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                                parametros.put("valorBuscar", textoBuscar + "%");
                            }
                        } else if (cabeceraRrHh.getTipodato() == 4) { // texto
                            if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                                whereCampo += " and upper(o.codigo) like :valorBuscar ";
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
                            parametros.put(";orden", "o.entidad.apellidos ASC ");
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

    public String cancelar() {
        empleado = null;
        entidad = null;
        imagen = null;
        archivoImagen = null;
        listaCabeceraempleado = new LinkedList<>();
        oficinasBean.setEdificio(null);
        proyectosBean.setProyectoSeleccionado(null);
        proyectosBean.setCodigo(null);
        formulario.cancelar();
        return null;
    }

    public String crear() {
        // se deberia chequear perfil?

//        if (cxOrganigramaBean.getOrganigramaL() == null) {
//            MensajesErrores.advertencia("Elija un Proceso del organigrama");
//            return null;
//        }
        setEmpleado(new Empleados());
        setEntidad(new Entidades());
        empleadoSeleccionado = null;
//        proyectosBean.setProyectoSeleccionado(null);
//        proyectosBean.setCodigo(null);
        archivoImagen = null;
        imagen = null;
        setDireccion(new Direcciones());
//        empleado.setOficina(cxOrganigramaBean.getOrganigramaL().get);
//        cedula = null;
        historia = new Historialcargos();
        // trearer los datos de la cabecera
        Map parametros = new HashMap();
        parametros.put(";orden", "o.idgrupo.id,o.orden");
        parametros.put(";where", "o.activo=true");
        listaCabeceraempleado = new LinkedList<>();
        try {
            List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.codigo");
            String maximo = ejbEmpleados.maximoNumeroStr(parametros);
            Integer maximoNumero = Integer.parseInt(maximo) + 1;
            empleado.setCodigo(maximoNumero.toString());

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
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    private void estaCabecera(Cabecerasrrhh crr) {
        for (Cabeceraempleados c : listaCabeceraempleado) {
            if (c.getCabecera().equals(crr)) {
                return;
            }
        }
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

    public String cesar() {

        empleado = (Empleados) empleados.getRowData();
        if (empleado.getFechasalida() != null) {
            MensajesErrores.advertencia("Empleado ya tiene la fecha de salida");
            return null;
        }
        empleado.setFechasalida(new Date());
        formularioCese.insertar();
        return null;
    }

    public String modificar() {

        empleado = (Empleados) empleados.getRowData();
        empleadoSeleccionado = empleado;
        entidad = empleado.getEntidad();
        direccion = entidad.getDireccion();
        if (direccion == null) {
            direccion = new Direcciones();
        }

        // taer la foto
        Map parametros = new HashMap();
        parametros.put(";where", "o.imagen.modulo='EMPLEADOFOTO' and o.imagen.idmodulo=:activo");
        parametros.put("activo", entidad.getId());
        archivoImagen = null;
        imagen = null;
        historia = null;
        if (empleado.getCargoactual() == null) {
            historia = new Historialcargos();
        }
        List<Archivos> larch = ejbArchivos.encontarParametros(parametros);
        for (Archivos a : larch) {
            archivoImagen = a;
            imagen = a.getImagen();
        }
        if (empleado.getOficina() != null) {
            oficinasBean.setEdificio(empleado.getOficina().getEdificio());
        }
        // imageens
        imagenesBean.setIdModulo(empleado.getId());
        imagenesBean.setModulo("EMPLEADO");
        imagenesBean.Buscar();
//        if (empleado.getProyecto() == null) {
//            proyectosBean.setProyectoSeleccionado(null);
//            proyectosBean.setCodigo(null);
//        } else {
//            proyectosBean.setProyectoSeleccionado(empleado.getProyecto());
//            proyectosBean.setTipoBuscar("-1");
//            proyectosBean.setCodigo(empleado.getProyecto().getCodigo());
//        }
        parametros = new HashMap();
        parametros.put(";orden", "o.grupo,o.orden");
        parametros.put(";where", "o.activo=true");
        listaCabeceraempleado = new LinkedList<>();
        try {
            List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";orden", "o.cabecera.id,o.orden");
            parametros.put(";where", "o.empleado=:empleado");
            parametros.put("empleado", empleado);
            listaCabeceraempleado = ejbCabecerasempleados.encontarParametros(parametros);
            for (Cabecerasrrhh crr : listaCab) {
                estaCabecera(crr);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String modificarSeleccionado() {

//        entidad = empleado.getEntidad();
        direccion = entidad.getDireccion();
        if (direccion == null) {
            direccion = new Direcciones();
        }

        // taer la foto
        if (empleado != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.imagen.modulo='EMPLEADOFOTO' and o.imagen.idmodulo=:activo");
            parametros.put("activo", entidad.getId());
            archivoImagen = null;
            imagen = null;
            List<Archivos> larch = ejbArchivos.encontarParametros(parametros);
            for (Archivos a : larch) {
                archivoImagen = a;
                imagen = a.getImagen();
            }
            if (empleado.getOficina() != null) {
                oficinasBean.setEdificio(empleado.getOficina().getEdificio());
            }
            if (empleado.getProyecto() == null) {
                proyectosBean.setProyectoSeleccionado(null);
                proyectosBean.setCodigo(null);
            } else {
                proyectosBean.setProyectoSeleccionado(empleado.getProyecto());
                proyectosBean.setTipoBuscar("-1");
                proyectosBean.setCodigo(empleado.getProyecto().getCodigo());
            }
            parametros = new HashMap();
            parametros.put(";orden", "o.idgrupo.id,o.orden");
            parametros.put(";where", "o.activo=true");
            listaCabeceraempleado = new LinkedList<>();
            try {
                List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";orden", "o.grupo,o.orden");
                parametros.put(";where", "o.empleado=:empleado");
                parametros.put("empleado", empleado);
                listaCabeceraempleado = ejbCabecerasempleados.encontarParametros(parametros);
                for (Cabecerasrrhh crr : listaCab) {
                    estaCabecera(crr);
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        formulario.editar();
        return null;
    }

    protected boolean validar() {

        if ((entidad.getPin() == null) || (entidad.getPin().isEmpty())) {
            MensajesErrores.advertencia("CI o RUC es obligatorio");
            return true;
        }
        if (empleado.getCargoactual() == null) {
            MensajesErrores.advertencia("Cargo es obligatorio");
            return true;
        }
        if ((empleado.getPartida() == null) || (empleado.getPartida().isEmpty())) {
            MensajesErrores.advertencia("Partida es obligatoria");
            return true;
        }
        if ((getEntidad().getNombres() == null) || (getEntidad().getNombres().isEmpty())) {
            MensajesErrores.advertencia("Nombres es obligatorio");
            return true;
        }
        if ((getEntidad().getApellidos() == null) || (getEntidad().getApellidos().isEmpty())) {
            MensajesErrores.advertencia("Apellidos es obligatorio");
            return true;
        }
        if ((getEntidad().getEmail() == null) || (getEntidad().getEmail().isEmpty())) {
            MensajesErrores.advertencia("Email es obligatorio");
            return true;
        }
        if ((getEntidad().getFecha() == null)) {
            MensajesErrores.advertencia("Fecha nacimiento obligatorio");
            return true;
        }
        ///////////////REVISAR///////////////////////////////////////////////
        if ((empleado.getTipocontrato() == null)) {
            MensajesErrores.advertencia("Tipo de contrato es obligatorio");
            return true;
        }
        if ((getEntidad().getFecha().after(new Date()))) {
            MensajesErrores.advertencia("Fecha nacimiento menor a hoy");
            return true;
        }
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
//            parametros.put(";where", " o.activo = true and o.pin=:pin");
            parametros.put(";where", " o.pin=:pin");
            parametros.put("pin", getEntidad().getPin());
            try {
                int total = ejbEntidad.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Empleado con Número de Cédula o DNI ya  registrado.");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.advertencia(ex.getMessage());
                Logger.getLogger("").log(Level.SEVERE, null, ex);
            }
        }
        //validar datos extras
        DecimalFormat df = new DecimalFormat("###,##0.00");
        for (Cabeceraempleados c : listaCabeceraempleado) {
            if (c.getTipodato() == 1) {
                if (c.getValornumerico() != null) {
                    if (c.getCabecera().getMaximo() != null) {
                        if (c.getValornumerico().doubleValue() > c.getCabecera().getMaximo().doubleValue()) {
                            MensajesErrores.advertencia("Valor sobrepasa el máximo " + c.getAyuda() + " " + df.format(c.getCabecera().getMaximo()));
                            return true;
                        }
                    }
                    if (c.getCabecera().getMinimo() != null) {
                        if (c.getValornumerico().doubleValue() < c.getCabecera().getMinimo().doubleValue()) {
                            MensajesErrores.advertencia("Valor inferior al mínimo " + c.getAyuda() + " " + df.format(c.getCabecera().getMaximo()));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String insertar() {
        try {

            if (validar()) {
                return null;
            }
//            if (getDireccion().getId() == null) {
//                ejbDirecciones.create(getDireccion(), parametrosSeguridad.getLogueado().getUserid());
//            } else {
//                ejbDirecciones.edit(getDireccion(), parametrosSeguridad.getLogueado().getUserid());
//            }
            // consultar el codigo maximo
//            getEntidad().setDireccion(getDireccion());
            //Generacion Usuario
//            generarUsrEmpleado();
            //COntrasena Empleado
            Codificador c = new Codificador();
            String passw = c.getEncoded(getEntidad().getPin(), "MD5");
            getEntidad().setPwd(passw);

            if (getEntidad().getId() == null) {
                getEntidad().setActivo(Boolean.TRUE);
                ejbEntidad.create(getEntidad(), parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbEntidad.edit(getEntidad(), parametrosSeguridad.getLogueado().getUserid());
            }
//            empleado.setProyecto(proyectosBean.getProyectoSeleccionado());
            empleado.setEntidad(entidad);
            DecimalFormat df = new DecimalFormat("000");
            getEntidad().setActivo(Boolean.TRUE);
//            getEmpleado().setCargoactual(cargo);
            getEmpleado().setEntidad(getEntidad());
            getEmpleado().setActivo(Boolean.TRUE);
//            getEmpleado().setOficina(oficina);
            ejbEmpleados.create(getEmpleado(), parametrosSeguridad.getLogueado().getUserid());
            empleado.setPartidaindividual(empleado.getPartida() + "."
                    + empleado.getTipocontrato().getCodigoalterno() + "."
                    + empleado.getCargoactual().getOrganigrama().getCodigoalterno() + "."
                    + empleado.getCargoactual().getCodigo() + "."
                    + empleado.getCodigo());
//            ejbEmpleados.edit(getEmpleado(), parametrosSeguridad.getLogueado().getUserid());
            if (archivoImagen != null) {
                imagen.setModulo("EMPLEADOFOTO");
                imagen.setIdmodulo(entidad.getId());
                ejbimagenes.create(imagen);
                archivoImagen.setImagen(imagen);
                ejbArchivos.create(archivoImagen);
            }
            for (Cabeceraempleados ce : listaCabeceraempleado) {
                ce.setEmpleado(empleado);
                ejbCabecerasempleados.create(ce, parametrosSeguridad.getLogueado().getUserid());
            }
            // guarda el Historial del cargo
            if (empleado.getCargoactual() != null) {
                historia.setCargo(empleado.getCargoactual());
                historia.setDesde(empleado.getFechaingreso());
                historia.setActivo(false);
                historia.setVigente(empleado.getTipocontrato().getNombramiento());
                historia.setAprobacion(true);
                historia.setEmpleado(empleado);
                historia.setMotivo("CREACION EMPLEADO");
                historia.setTipocontrato(empleado.getTipocontrato());
                historia.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                historia.setFecha(new Date());
                historia.setSueldobase(empleado.getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                if (empleado.getTipocontrato().getNombramiento()) {
                    historia.setHasta(null);
                } else {
                    if (empleado.getFechaingreso() != null) {
                        int cuanto = empleado.getTipocontrato().getDuracion();
                        Calendar ck = Calendar.getInstance();
                        ck.setTime(empleado.getFechaingreso());
                        ck.add(Calendar.DATE, cuanto);
                        historia.setHasta(ck.getTime());
                    }
//                     Date fechafin
//                    = getFormulario().cancelar();
                }
                ejbHistorial.create(historia, parametrosSeguridad.getLogueado().getUserid());
            }
            ejbEmpleados.edit(getEmpleado(), parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        contratoL = null;
        edificio = null;
        oficina = null;
        oficinasBean.setEdificio(null);
        historia = null;
//        formulario.cancelar();
        proyectosBean.setProyectoSeleccionado(null);
        proyectosBean.setCodigo(null);
        empleadoSeleccionado = empleado;
        MensajesErrores.advertencia("Empleado creado correctamente");
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabarCese() {

        try {
            ejbEmpleados.edit(empleado, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCese.cancelar();
        empleadoSeleccionado = empleado;
        buscar();
        return null;
    }

    public String grabarEntidad() {
        if (validar()) {
            return null;
        }
        try {
            if (getDireccion().getId() == null) {
                ejbDirecciones.create(getDireccion(), parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbDirecciones.edit(getDireccion(), parametrosSeguridad.getLogueado().getUserid());
            }

            getEntidad().setDireccion(getDireccion());
            DecimalFormat df = new DecimalFormat("000");
            empleado.setPartidaindividual(empleado.getPartida() + "."
                    + empleado.getTipocontrato().getCodigoalterno() + "."
                    + empleado.getCargoactual().getOrganigrama().getCodigoalterno() + "."
                    + empleado.getCargoactual().getCodigo() + "."
                    + empleado.getCodigo());
//            empleado.setProyecto(proyectosBean.getProyectoSeleccionado());
//            Codificador c = new Codificador();
//            String passw = c.getEncoded(getEntidad().getPin(), "MD5");
//            getEntidad().setPwd(passw);

            if (getEntidad().getId() == null) {
                getEntidad().setActivo(Boolean.TRUE);
                ejbEntidad.create(getEntidad(), parametrosSeguridad.getLogueado().getUserid());
            } else {
                ejbEntidad.edit(getEntidad(), parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        empleadoSeleccionado = empleado;
        buscar();
//        proyectosBean.setProyectoSeleccionado(null);
//        proyectosBean.setCodigo(null);
        return null;
    }
//    private void generarUsrEmpleado() {
//        //UserID
//        String[] apellido = getEntidad().getApellidos().split("\\ ");
//        String[] nombre = getEntidad().getNombres().split("\\ ");
//        String usuario = quitarAcentos(nombre[0].substring(0, 1).toLowerCase() + apellido[0].toLowerCase());
//        getEntidad().setUserid(usuario);
//    }
//
//    private String quitarAcentos(String input) {
//        // Cadena de caracteres original a sustituir.
//        String original = "áàäéèëíìïóòöúùüñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
//        // Cadena de caracteres ASCII que reemplazarán los originales.
//        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
//        String output = input;
//        for (int i = 0; i < original.length(); i++) {
//            // Reemplazamos los caracteres especiales.
//            output = output.replace(original.charAt(i), ascii.charAt(i));
//        }//for i
//        return output;
//    }// 

    public String grabar() {
        if (validar()) {
            return null;
        }
//        cabecerasEmpleadoBean.grabarCabeceras();
        try {
            if (getDireccion() != null) {
                if (getDireccion().getId() == null) {
                    ejbDirecciones.create(getDireccion(), parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDirecciones.edit(getDireccion(), parametrosSeguridad.getLogueado().getUserid());
                }
                getEntidad().setDireccion(getDireccion());
//                ejbEntidad.edit(getEntidad(), parametrosSeguridad.getLogueado().getUserid());
            }
//            empleado.setProyecto(proyectosBean.getProyectoSeleccionado());
            ejbEntidad.edit(getEntidad(), parametrosSeguridad.getLogueado().getUserid());
//            empleado.setEntidad(entidad);
            ejbEmpleados.edit(getEmpleado(), parametrosSeguridad.getLogueado().getUserid());
            if (archivoImagen != null) {
                if (archivoImagen.getId() == null) {
                    imagen.setModulo("EMPLEADOFOTO");
                    imagen.setIdmodulo(entidad.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivos.create(archivoImagen);
                } else {
                    imagen.setModulo("EMPLEADOFOTO");
                    imagen.setIdmodulo(entidad.getId());
                    ejbimagenes.create(imagen);
                    archivoImagen.setImagen(imagen);
                    ejbArchivos.create(archivoImagen);
                }
            }
            for (Cabeceraempleados ce : listaCabeceraempleado) {
                ce.setEmpleado(empleado);
                if (ce.getId() == null) {
                    ejbCabecerasempleados.create(ce, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbCabecerasempleados.edit(ce, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            // guarda el Historial del cargo
            if (historia != null) {
                if (empleado.getCargoactual() != null) {
                    historia.setCargo(empleado.getCargoactual());
                    historia.setDesde(empleado.getFechaingreso());
                    historia.setActivo(false);
                    historia.setVigente(empleado.getTipocontrato().getNombramiento());
                    historia.setAprobacion(true);
                    historia.setTipocontrato(empleado.getTipocontrato());
                    historia.setEmpleado(empleado);
                    historia.setMotivo("CREACION EMPLEADO");
                    historia.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                    historia.setFecha(new Date());
                    historia.setSueldobase(empleado.getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                    if (empleado.getTipocontrato().getNombramiento()) {
                        historia.setHasta(null);
                    } else {
                        if (empleado.getFechaingreso() != null) {
                            int cuanto = empleado.getTipocontrato().getDuracion();
                            Calendar ck = Calendar.getInstance();
                            ck.setTime(empleado.getFechaingreso());
                            ck.add(Calendar.DATE, cuanto);
                            historia.setHasta(ck.getTime());
                        }
//                     Date fechafin
//                    = getFormulario().cancelar();
                    }
                    ejbHistorial.create(historia, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        getFormulario().cancelar();
        contratoL = new Tiposcontratos();
        edificio = null;
        oficinasBean.setEdificio(null);
        oficina = null;
        historia = null;
//        proyectosBean.setProyectoSeleccionado(null);
//        proyectosBean.setCodigo(null);
        empleadoSeleccionado = empleado;
        buscar();
        return null;
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

//    public void cambiaApellido(ValueChangeEvent event) {
//        empleadoSeleccionado = null;
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
//            String where = " o.activo=true and o.fechasalida is null and o.tipocontrato is not null";
//            where += " and  upper(o.entidad.apellidos) like :apellidos";
////            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
//            parametros.put("apellidos", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.entidad.apellidos");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEmpleados.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaUsuarios(new ArrayList());
//            for (Empleados e : aux) {
//                SelectItem s = new SelectItem(e, e.getEntidad().getApellidos());
//                getListaUsuarios().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                empleadoSeleccionado = (Empleados) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Empleados tmp = null;
//                for (int i = 0, max = getListaUsuarios().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaUsuarios().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Empleados) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    empleadoSeleccionado = tmp;
//                }
//            }
//
//        }
//    }
//
//    public void cambiaApellidoExEmpleado(ValueChangeEvent event) {
//        empleadoSeleccionado = null;
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
//            String where = " o.activo=false and o.fechasalida is not null and o.tipocontrato is not null";
//            where += " and  upper(o.entidad.apellidos) like :apellidos";
////            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
//            parametros.put("apellidos", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.entidad.apellidos");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEmpleados.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaUsuarios(new ArrayList());
//            for (Empleados e : aux) {
//                SelectItem s = new SelectItem(e, e.getEntidad().getApellidos());
//                getListaUsuarios().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                empleadoSeleccionado = (Empleados) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Empleados tmp = null;
//                for (int i = 0, max = getListaUsuarios().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaUsuarios().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Empleados) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    empleadoSeleccionado = tmp;
//                }
//            }
//
//        }
//    }
    /**
     * @return the listaUsuarios
     */
    public List<Empleados> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List<Empleados> listaUsuarios) {
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
                Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                formulario.insertar();
//            } else {
//                entidad = empleado.getEntidad();
//                direccion = entidad.getDireccion();
//                if (direccion == null) {
//                    direccion = new Direcciones();
//                }
//
//                // taer la foto
//                parametros = new HashMap();
//                parametros.put(";where", "o.imagen.modulo='EMPLEADOFOTO' and o.imagen.idmodulo=:activo");
//                parametros.put("activo", entidad.getId());
//                archivoImagen = null;
//                imagen = null;
//                historia = null;
//                if (empleado.getCargoactual() == null) {
//                    historia = new Historialcargos();
//                }
//                List<Archivos> larch = ejbArchivos.encontarParametros(parametros);
//                for (Archivos a : larch) {
//                    archivoImagen = a;
//                    imagen = a.getImagen();
//                }
//                if (empleado.getOficina() != null) {
//                    oficinasBean.setEdificio(empleado.getOficina().getEdificio());
//                }
//                parametros = new HashMap();
//                parametros.put(";orden", "o.grupo,o.orden");
//                parametros.put(";where", "o.activo=true");
//                listaCabeceraempleado = new LinkedList<>();
//                try {
//                    List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
//                    parametros = new HashMap();
//                    parametros.put(";orden", "o.grupo,o.orden");
//                    parametros.put(";where", "o.empleado=:empleado");
//                    parametros.put("empleado", empleado);
//                    listaCabeceraempleado = ejbCabecerasempleados.encontarParametros(parametros);
//                    for (Cabecerasrrhh crr : listaCab) {
//                        estaCabecera(crr);
//                    }
//                } catch (ConsultarException ex) {
//                    MensajesErrores.fatal(ex.getMessage());
//                    Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                formulario.editar();
            }
        }
    }

    public Empleados traer(Integer id) {
        try {
            return ejbEmpleados.find(id);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Entidades traerEntidad(Integer id) {
        try {
            return ejbEntidad.find(id);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
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
//            if (elista == null) {
//                return null;
//            }
//            int size = elista.size() + 1;
//            SelectItem[] items = new SelectItem[size];
//            int i = 0;
//            items[0] = new SelectItem(0, "--- Seleccione uno ---");
//            i++;
//            for (Empleados x : elista) {
//                items[i++] = new SelectItem(x, x.getEntidad().toString());
//            }
//            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String traerCuentaBancaria(Empleados empleado) {
        if ((empleado == null)) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.texto='NUMCUENTA' ");
        parametros.put("empleado", empleado);
        try {
            List<Cabeceraempleados> le = ejbCabecerasempleados.encontarParametros(parametros);
            for (Cabeceraempleados e : le) {
                return e.getValortexto();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
        c.setTime(configuracionBean.getConfiguracion().getPvigentepresupuesto());
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

//    public void exEmpleadoChangeEventHandler(TextChangeEvent event) {
//        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
//        Map parametros = new HashMap();
////        String where = " o.activo=false and o.fechasalida is not null ";
//        String where = "o.fechasalida is not null ";
//        where += " and  upper(o.entidad.apellidos) like :apellidos";
////            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
//        parametros.put("apellidos", codigoBuscar + "%");
//        parametros.put(";where", where);
//        parametros.put(";orden", " o.entidad.apellidos");
//        int total = 15;
//        // Contadores
//        parametros.put(";inicial", 0);
//        parametros.put(";final", total);
//        try {
//            listaUsuarios = ejbEmpleados.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//        }
//
//    }
    public void exEmpleadoChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = "o.fechasalida is  not null ";
        where += " and  upper(o.entidad.apellidos) like :apellidos";
//            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaUsuarios = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void empleadoChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
//        String where = " o.activo=true and o.fechasalida is  null and o.cargoactual is not null";
        String where = "";
        where += " upper(o.entidad.apellidos) like :apellidos";
//            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos");
        int total = 50;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaUsuarios = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }
    public void empleadoActivoChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
//        String where = " o.activo=true and o.fechasalida is  null and o.cargoactual is not null";
        String where = "";
        where += " upper(o.entidad.apellidos) like :apellidos and o.activo=true";
//            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos");
        int total = 50;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaUsuarios = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void empleadoLiquidadoChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
//        String where = " o.activo=true and o.fechasalida is  not null";
        String where = "";
        where += "  upper(o.entidad.apellidos) like :apellidos";
//            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.entidad.apellidos");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            listaUsuarios = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaApellido(ValueChangeEvent event) {
        entidad = null;
        empleadoSeleccionado = null;
        if (listaUsuarios == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Empleados e : listaUsuarios) {
            if (e.getEntidad().toString().compareToIgnoreCase(newWord) == 0) {
                empleadoSeleccionado = e;
                entidad = empleadoSeleccionado.getEntidad();
            }
        }

    }

    public Entidades traerCedulaEntidad(String cedula) {
        if ((cedula == null) || (cedula.isEmpty())) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.pin=:pin");
        parametros.put("pin", cedula);
        try {
            List<Entidades> le = ejbEntidad.encontarParametros(parametros);
            for (Entidades e : le) {
                return e;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //PARA LA CONSULTA DE GARANTES ES-2018-03-13
    public void cambiaApellidoAdicional(ValueChangeEvent event) {
        entidadAdicional = null;
        empleadoAdicional = null;
        if (listaUsuarios == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Empleados e : listaUsuarios) {
            if (e.getEntidad().toString().compareToIgnoreCase(newWord) == 0) {
                empleadoAdicional = e;
                entidadAdicional = empleadoAdicional.getEntidad();
            }
        }

    }

    /**
     * @return the formularioCese
     */
    public Formulario getFormularioCese() {
        return formularioCese;
    }

    /**
     * @param formularioCese the formularioCese to set
     */
    public void setFormularioCese(Formulario formularioCese) {
        this.formularioCese = formularioCese;
    }

    public SelectItem[] getComboEmpleadosSalientes() {

        Map parametros = new HashMap();
        parametros.put(";where", "o.fechasalida is not null and o.activo=true");
        parametros.put(";orden", "o.entidad.apellidos,o.entidad.nombres");
        try {
            List<Empleados> listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            return Combos.getSelectItems(listaEmpleados, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the tipoHorario
     */
    public Integer getTipoHorario() {
        return tipoHorario;
    }

    /**
     * @param tipoHorario the tipoHorario to set
     */
    public void setTipoHorario(Integer tipoHorario) {
        this.tipoHorario = tipoHorario;
    }

    /**
     * @return the cabecerasVer
     */
    public Grupocabeceras getCabecerasVer() {
        return cabecerasVer;
    }

    /**
     * @param cabecerasVer the cabecerasVer to set
     */
    public void setCabecerasVer(Grupocabeceras cabecerasVer) {
        this.cabecerasVer = cabecerasVer;
    }

    public List<Cabeceraempleados> getCabeceras() {
        if (cabecerasVer == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.grupo,o.orden");
        parametros.put("grupo", cabecerasVer);
        parametros.put(";where", "o.activo=true and o.idgrupo=:grupo");
        listaCabeceraempleado = new LinkedList<>();
        try {
            List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";orden", "o.cabecera.id,o.orden");
            parametros.put(";where", "o.empleado=:empleado and o.cabecera.idgrupo=:grupo");
            parametros.put("empleado", empleado);
            parametros.put("grupo", cabecerasVer);
            listaCabeceraempleado = ejbCabecerasempleados.encontarParametros(parametros);
            for (Cabecerasrrhh crr : listaCab) {
                estaCabecera(crr);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaCabeceraempleado;
    }

    public String exportar() {
        try {
            listaEmpleados = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "o.activo=true and o.cargoactual is not null and o.fechasalida is null";
            if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                where += " and upper(o.entidad.pin) like :pin ";
                parametros.put("pin", getBuscarCedula().toUpperCase());
            }
            if (!nombres.isEmpty() && nombres != null) {
                where += " and upper(o.entidad.nombres) like :noms ";
                parametros.put("noms", nombres.toUpperCase() + "%");
            }
            if (tipoHorario != null) {
                if (tipoHorario == 1) {
                    where += " and o.tipohorario=true";
                } else if (tipoHorario == 2) {
                    where += " and o.tipohorario=:false";
                }
            }
            if (!apellidos.isEmpty() && apellidos != null) {
                where += " and upper(o.entidad.apellidos) like :ape ";
                parametros.put("ape", apellidos.toUpperCase() + "%");
            }
            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (cxOrganigramaBean.getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    parametros.put("carg", cargo);
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", cxOrganigramaBean.getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                parametros.put("genero", genero);
            }
            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", tipsang);
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                parametros.put("ecivil", ecivil);
            }
            if (desde != null || hasta != null) {
                where += " and o.entidad.fecha between :desde and :hasta ";
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
            }
            //CONTRATO
            if (contratoL != null) {
                where += " and o.tipocontrato=:contrato ";
                parametros.put("contrato", contratoL);
            }
            //OFICINA
            if (oficina != null) {
                where += " and o.oficina=:oficina ";
                parametros.put("oficina", oficina);
            }
            //EDIFICIO
            if (edificio != null) {
                where += " and o.oficina.edificio=:edificio ";
                parametros.put("edificio", edificio);
            }

            //NOMBRAMIENTO
            if (nombramiento != null) {
                if (nombramiento == 1) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (nombramiento == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }
            parametros.put(";where", where);
            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
            if (listaEmpleados == null) {
                MensajesErrores.advertencia("Genere el reporte primero");
                return null;
            }
            DocumentoXLS xls = new DocumentoXLS("Empleados");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CÉDULA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "APELLIDOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NOMBRES"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "SUBACTIVIDAD"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PARTIDA"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MODALIDAD DE CONTRATACIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "DENOMINACIÓN"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "GRADO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "CARGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "PROCESO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "OPERATIVO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "R.M.U."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "NOMBREMIENTO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FECHA INGRESO"));
            xls.agregarFila(campos, true);

            for (Empleados e : listaEmpleados) {
                if (e != null) {
                    campos = new LinkedList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    campos.add(new AuxiliarReporte("String", e.getEntidad().getPin() != null ? e.getEntidad().getPin() : " "));
                    campos.add(new AuxiliarReporte("String", e.getEntidad().getApellidos() != null ? e.getEntidad().getApellidos() : " "));
                    campos.add(new AuxiliarReporte("String", e.getEntidad().getNombres() != null ? e.getEntidad().getNombres() : " "));
                    campos.add(new AuxiliarReporte("String", e.getProyecto() != null ? e.getProyecto().toString() : " "));
                    campos.add(new AuxiliarReporte("String", e.getPartida() != null ? e.getPartida() : " "));
                    campos.add(new AuxiliarReporte("String", e.getTipocontrato() != null ? e.getTipocontrato().getNombre() : " "));
                    campos.add(new AuxiliarReporte("String", e.getCargoactual() != null ? e.getCargoactual().getCargo().getEscalasalarial().getNombre() : " "));
                    campos.add(new AuxiliarReporte("String", e.getCargoactual() != null ? e.getCargoactual().getCargo().getEscalasalarial().getCodigo() : " "));
                    campos.add(new AuxiliarReporte("String", e.getCargoactual() != null ? e.getCargoactual().getCargo().getNombre() : " "));
                    campos.add(new AuxiliarReporte("String", e.getCargoactual() != null ? e.getCargoactual().getOrganigrama().getNombre() : " "));
                    campos.add(new AuxiliarReporte("String", e.getOperativo() ? "SI" : "NO"));
                    campos.add(new AuxiliarReporte("String", e.getCargoactual().getCargo().getEscalasalarial().getSueldobase() != null ? (df.format(e.getCargoactual().getCargo().getEscalasalarial().getSueldobase())) : " "));
                    campos.add(new AuxiliarReporte("String", e.getTipocontrato().getNombramiento() ? "SI" : "NO"));
                    campos.add(new AuxiliarReporte("String", e.getFechaingreso() != null ? (sdf.format(e.getFechaingreso())) : " "));
                    xls.agregarFila(campos, false);
                }
            }
            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the empleadoAdicional
     */
    public Empleados getEmpleadoAdicional() {
        return empleadoAdicional;
    }

    /**
     * @param empleadoAdicional the empleadoAdicional to set
     */
    public void setEmpleadoAdicional(Empleados empleadoAdicional) {
        this.empleadoAdicional = empleadoAdicional;
    }

    /**
     * @return the entidadAdicional
     */
    public Entidades getEntidadAdicional() {
        return entidadAdicional;
    }

    /**
     * @param entidadAdicional the entidadAdicional to set
     */
    public void setEntidadAdicional(Entidades entidadAdicional) {
        this.entidadAdicional = entidadAdicional;
    }

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }

    /**
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the apellidosAdicional
     */
    public String getApellidosAdicional() {
        return apellidosAdicional;
    }

    /**
     * @param apellidosAdicional the apellidosAdicional to set
     */
    public void setApellidosAdicional(String apellidosAdicional) {
        this.apellidosAdicional = apellidosAdicional;
    }
}
