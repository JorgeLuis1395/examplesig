/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.ejb.sfcarchivos.ArchivosFacade;
import com.ejb.sfcarchivos.ImagenesFacade;
import com.entidades.sfcarchivos.Archivos;
import com.entidades.sfcarchivos.Imagenes;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.activos.sfccbdmq.OficinasBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.GastosPersonales;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasrrhhFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.GrupocabecerasFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.contabilidad.sfccbdmq.Formulario107Bean;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Grupocabeceras;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;


/**
 *
 * @author evconsul
 */
@ManagedBean(name = "datosEmpleados")
@ViewScoped
public abstract class DatosEmpleadosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Empleados> empleados;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EntidadesFacade ejbEntidad;
    @EJB
    private ArchivosFacade ejbArchivos;
    @EJB
    private ImagenesFacade ejbimagenes;
    @EJB
    private CabecerasrrhhFacade ejbCabeceras;
    @EJB
    private CabeceraempleadosFacade ejbCabecerasempleados;
    @EJB
    private RangoscabecerasFacade ejbrangocabecra;
    @EJB
    private GrupocabecerasFacade ejbgrupocabecera;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private CabeceraempleadosFacade ejbCabecera;
    private Archivos archivoImagen;
    private Imagenes imagen;
    private Empleados empleado = new Empleados();
    // busqueda campos extras
    private Cabecerasrrhh cabeceraRrHh;
    private BigDecimal desdeNumerico;
    private BigDecimal hastaNumerico;
    private String textoBuscar;
    protected String campo;
    private Date desdeFecha;
    private Date hastaFecha;
    private List listaUsuarios;
    private Grupocabeceras cabecerasVer;
    //
    private Entidades entidad = new Entidades();
//    private Documentosempleado documentoempleado;
    private Formulario formularioImagen = new Formulario();
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
    private List<AuxiliarAsignacion> gastosPersonales;
    private Resource gastos;
    private int anio;

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
        String nombreForma = "DatosEmpleadosVista";
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
    public DatosEmpleadosBean() {
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
                    String where = "o.activo=true and o.tipocontrato is not null";
                    String whereCampo = "o.empleado.activo=true and o.empleado.tipocontrato is not null";
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
                        where += " and upper(o.entidad.pin)like:pin";
                        whereCampo += " and upper(o.empleado.entidad.pin) like :pin";
                        parametros.put("pin", getBuscarCedula().toUpperCase());
                    }

                    if (!nombres.isEmpty() && nombres != null) {
                        where += " and upper(o.entidad.nombres)like:noms";
                        whereCampo += " and upper(o.empleado.entidad.nombres) like :noms";
                        parametros.put("noms", nombres.toUpperCase() + "%");
                    }

                    if (!apellidos.isEmpty() && apellidos != null) {
                        where += " and upper(o.entidad.apellidos) like :ape";
                        whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape";
                        parametros.put("ape", apellidos.toUpperCase() + "%");
                    }

                    if (!email.isEmpty() && getEmail() != null) {
                        where += " and upper(o.entidad.email)like:email";
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

    public String cancelar() {
        empleado = null;
        entidad = null;
        imagen = null;
        archivoImagen = null;
        listaCabeceraempleado = new LinkedList<>();
        oficinasBean.setEdificio(null);
        formulario.cancelar();
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

    public String modificar() {

        empleado = (Empleados) empleados.getRowData();
        entidad = empleado.getEntidad();

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
        parametros.put(";orden", "o.idgrupo.texto,o.orden");
        parametros.put(";where", "o.activo=true and o.idgrupo." + campo + "=true");
        listaCabeceraempleado = new LinkedList<>();
        try {
            List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";orden", "o.cabecera.idgrupo.id,o.orden");
            parametros.put(";where", "o.empleado=:empleado and o.cabecera.idgrupo." + campo + "=true");
            parametros.put("empleado", empleado);
            listaCabeceraempleado = ejbCabecerasempleados.encontarParametros(parametros);
            for (Cabecerasrrhh crr : listaCab) {
                estaCabecera(crr);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DatosEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String modificarInterno() {

        empleado = parametrosSeguridad.getLogueado().getEmpleados();
        entidad = empleado.getEntidad();

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
//        parametros = new HashMap();
//        parametros.put(";orden", "o.idgrupo.texto,o.orden");
//        parametros.put(";where", "o.activo=true and o.idgrupo." + campo + "=true");
//        listaCabeceraempleado = new LinkedList<>();
//        try {
//            List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
//            parametros = new HashMap();
//            parametros.put(";orden", "o.cabecera.idgrupo.id,o.orden");
//            parametros.put(";where", "o.empleado=:empleado and o.cabecera.idgrupo." + campo + "=true");
//            parametros.put("empleado", empleado);
//            listaCabeceraempleado = ejbCabecerasempleados.encontarParametros(parametros);
//            for (Cabecerasrrhh crr : listaCab) {
//                estaCabecera(crr);
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(DatosEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        formulario.editar();
        return null;
    }

    public String grabar() {
        try {
            DecimalFormat df = new DecimalFormat("###,##0.00");
            for (Cabeceraempleados c : listaCabeceraempleado) {
                if (c.getTipodato() == 1) {
                    if (c.getValornumerico() != null) {
                        if (c.getCabecera().getMaximo() != null) {
                            if (c.getValornumerico().doubleValue() > c.getCabecera().getMaximo().doubleValue()) {
                                MensajesErrores.advertencia("Valor sobrepasa el máximo " + c.getAyuda() + " " + df.format(c.getCabecera().getMaximo()));
                                return null;
                            }
                        }
                        if (c.getCabecera().getMinimo() != null) {
                            if (c.getValornumerico().doubleValue() < c.getCabecera().getMinimo().doubleValue()) {
                                MensajesErrores.advertencia("Valor inferior al mínimo " + c.getAyuda() + " " + df.format(c.getCabecera().getMaximo()));
                                return null;
                            }
                        }
                    }
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
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        if (cabecerasVer.getTexto().equals("GASTOS PERSONALES")) {
            gastosPersonales(listaCabeceraempleado);
        }
        getFormulario().cancelar();
//        contratoL = new Tiposcontratos();
//        edificio = null;
//        oficinasBean.setEdificio(null);
//        oficina = null;
//        historia = null;
//        proyectosBean.setProyectoSeleccionado(null);
//        proyectosBean.setCodigo(null);
//        buscar();
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

    public SelectItem[] getComboCabecera() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.texto asc");
        parametros.put(";where", "o." + campo + "=true");
        List<Grupocabeceras> aux = new LinkedList<>();
        try {
            aux = ejbgrupocabecera.encontarParametros(parametros);
            return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + ' ' + ex.getCause());
            Logger.getLogger(CabecerasEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public List<Cabeceraempleados> getCabeceras() {
        if (cabecerasVer == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.grupo,o.orden,o.texto");
        parametros.put("grupo", cabecerasVer);
        parametros.put(";where", "o.activo=true and o.idgrupo=:grupo");

        listaCabeceraempleado = new LinkedList<>();
        try {
            List<Cabecerasrrhh> listaCab = ejbCabeceras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";orden", "o.cabecera.texto,o.cabecera.id,o.orden");
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

    public String gastosPersonales(List<Cabeceraempleados> lista) {
        try {
            if (parametrosSeguridad.getLogueado().getEmpleados() == null) {
                MensajesErrores.advertencia("Seleccione un empleado primero");
                return null;
            }
            GastosPersonales hoja = new GastosPersonales();
            hoja.llenar(lista, configuracionBean.getConfiguracion());
            gastos = hoja.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DatosEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<AuxiliarAsignacion> llenar(List<Codigos> listaCodigos) {

        List<AuxiliarAsignacion> lista = new LinkedList<>();
        AuxiliarAsignacion totalTipo = new AuxiliarAsignacion();
        AuxiliarAsignacion totalTotal = new AuxiliarAsignacion();
        String grupo = "";
        Empleados empleado = parametrosSeguridad.getLogueado().getEmpleados();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPfinal());
        int anio = c.get(Calendar.YEAR);
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<rdep>";
        xmlStr += "<numRuc>" + configuracionBean.getConfiguracion().getRuc() + "</numRuc>\n";
        xmlStr += "<anio>" + anio + "</anio>\n";
        xmlStr += "<benGalpg>" + anio + "</benGalpg>\n";
        xmlStr += "<benGalpg>" + "NO" + "</benGalpg>\n";
        xmlStr += "<tipIdRet>" + "C" + "</tipIdRet>\n";
        xmlStr += "<idRet>" + empleado.getEntidad().getPin() + "</idRet>\n";
        xmlStr += "<apellidoTrab>" + empleado.getEntidad().getApellidos() + "</apellidoTrab>\n";
        xmlStr += "<nombreTrab>" + empleado.getEntidad().getNombres() + "</nombreTrab>\n";
        xmlStr += "<estab>" + "001" + "</estab>\n";
        xmlStr += "<residenciaTrab>" + "01" + "</residenciaTrab>\n";
        for (Codigos codigo : listaCodigos) {
            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setNombre(codigo.getNombre());
            a.setCodigo(codigo.getCodigo());
            if (codigo.getDescripcion().contains("e")) {
                // datos del empleado
                a.setValor(traerValorEmpleado(codigo.getParametros()));
            } else if (codigo.getDescripcion().contains("c")) {
                // datos de conceptos

                a.setValor(traerValorConcepto(codigo.getParametros()));
            } else if (codigo.getDescripcion().contains("1")) {
                // es suma pero hay que recorrer lo que pide
                String[] aux = codigo.getParametros().split("#");
                double total = 0;
                for (String aux1 : aux) {
                    String sinSigno = aux1.replace("-", "");
                    int signo = 1;
                    if (sinSigno.length() != aux1.length()) {
                        signo = -1;
                    }
                    double valor = traerValor(sinSigno, lista);
                    total += valor * signo;
                }
                if (total > 0) {
                    a.setValor(total);
                } else {
                    a.setValor(0);
                }
            } else if (codigo.getDescripcion().contains("-1")) {
                // es suma pero hay que recorrer lo que pide
                String[] aux = codigo.getParametros().split("#");
                double total = 0;
                for (String aux1 : aux) {
                    String sinSigno = aux1.replace("-", "");
                    int signo = 1;
                    if (sinSigno.length() != aux1.length()) {
                        signo = -1;
                    }
                    double valor = traerValor(sinSigno, lista);
                    total += valor * signo;
                }
                if (total < 0) {
                    a.setValor(total * -1);
                } else {
                    a.setValor(0);
                }
            } else {
                a.setCodigo("");
            }
            lista.add(a);

        }
        return lista;
    }

    private double traerValorEmpleado(String concepto) {
        Map parametros = new HashMap();
        String where = "o.texto=:texto and  o.empleado=:empleado";
        parametros.put("empleado", parametrosSeguridad.getLogueado().getEmpleados());
        parametros.put("texto", concepto);
        parametros.put(";where", where);
        parametros.put(";orden", "o.cabecera.texto,o.cabecera.id,o.orden");
        try {
            List<Cabeceraempleados> lista = ejbCabecera.encontarParametros(parametros);
            if (lista != null) {
                if (!lista.isEmpty()) {
                    for (Cabeceraempleados c : lista) {
                        if (c.getValornumerico() != null) {
                            return c.getValornumerico().doubleValue();
                        }
                    }
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    private double traerValorConcepto(String concepto) {
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.mes between 1 and 12 and o.empleado=:empleado";
        if (concepto.equals("rmu")) {
            where += " and o.concepto is null and o.prestamo is null and o.sancion is null";
            parametros.put(";campo", "o.valor+o.cantidad+o.encargo");
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put("empleado", parametrosSeguridad.getLogueado().getEmpleados());
            try {
                return ejbPagosEmpleados.sumarCampoDoble(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
//            where += " and o.concepto.concepto.codigo in (" + concepto + ")";
            where += " ";
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put("empleado", parametrosSeguridad.getLogueado().getEmpleados());
            try {
                return ejbPagosEmpleados.sumarCampoDoble(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }

    private double traerValor(String que, List<AuxiliarAsignacion> lista) {
        for (AuxiliarAsignacion a : lista) {
            if (a.getCodigo().equals(que)) {
                return a.getValor();
            }
        }
        return 0;
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

    public List<Cabeceraempleados> getCabecerasNuevo() {
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

    /**
     * @return the gastosPersonales
     */
    public List<AuxiliarAsignacion> getGastosPersonales() {
        return gastosPersonales;
    }

    /**
     * @param gastosPersonales the gastosPersonales to set
     */
    public void setGastosPersonales(List<AuxiliarAsignacion> gastosPersonales) {
        this.gastosPersonales = gastosPersonales;
    }

    /**
     * @return the gastos
     */
    public Resource getGastos() {
        return gastos;
    }

    /**
     * @param gastos the gastos to set
     */
    public void setGastos(Resource gastos) {
        this.gastos = gastos;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }
}
