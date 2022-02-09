/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ActasFacade;
import org.beans.sfccbdmq.ActasactivosFacade;
import org.beans.sfccbdmq.ActivoobligacionFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.GrupoactivosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Actas;
import org.entidades.sfccbdmq.Actasactivos;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Marcas;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Polizas;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoactivo;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "inicioBajasActivosSfccbdmq")
@ViewScoped
public class InicioBajasActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public InicioBajasActivosBean() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private ActivoobligacionFacade ejbObligacion;
    @EJB
    private GrupoactivosFacade ejbGrupos;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ActasFacade ejbActas;
    @EJB
    private ActasactivosFacade ejbActasActivos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private DepreciacionesFacade ejbDepreciaciones;
    private Archivos archivoImagen;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private LazyDataModel<Activos> activos;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private List<Trackingactivos> tackings;
    private Trackingactivos tacking;
    private String codigo;
    private String nombre;
    private String nombreExterno;
    private String cedulaExterno;
    private String descripcion;
    private String inventario;
    private String alterno;
//    private String marca;
    private Marcas marca;
    private String modelo;
    private String numeroserie;
    private String codigobarras;
    private String externo;
    private Tipoactivo tipo;
    private Grupoactivos grupo;
    private Codigos clasificacion;
    private Codigos estado;
    private Codigos institucion;
    private Oficinas oficina;
    private Edificios edificio;
    private Polizas poliza;
    private List listaActivo;
    private Integer factura;
    private int garantia;
    private int operativo;
    private int control;
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private Formulario formularioImprimir = new Formulario();
    // autocompletar
//    private Empleados empleadoSeleccionado;
//    private List<Empleados> listaUsuarios;
    // autocompletar 
    private Entidades entidad;
    private String apellidos;
//    private List listaUsuarios;
    private List<Entidades> listaEntidades;
    private String tipoAuxiliar;
    private String nominativo;
    private String nombrepdf;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{obligacionesSfccbdmq}")
    private ObligacionesBean obligacionesBean;
    @ManagedProperty(value = "#{oficinasSfccbdmq}")
    private OficinasBean oficinasBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{actasActivosSfccbdmq}")
    private ActasActivosBean actasActivosBean;
    private Perfiles perfil;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "InicioBajaVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
//    }
    }

    public String buscar() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fechaalta is not null and o.baja is null and o.fechasolicitud is null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", "%" + codigo + "%");
                }
                if (!((nombre == null) || (nombre.isEmpty()))) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", nombre.toUpperCase() + "%");
                }
                if (!((nombreExterno == null) || (nombreExterno.isEmpty()))) {
                    where += " and upper(o.externo.nombre) like :nombreExterno";
                    parametros.put("nombreExterno", nombreExterno.toUpperCase() + "%");
                }
                if (!((cedulaExterno == null) || (cedulaExterno.isEmpty()))) {
                    where += " and upper(o.externo.empresa) like :cedulaExterno";
                    parametros.put("cedulaExterno", cedulaExterno.toUpperCase() + "%");
                }
//                if (!((marca == null) || (marca.isEmpty()))) {
//                    where += " and upper(o.marca) like :marca";
//                    parametros.put("marca", getMarca().toUpperCase() + "%");
//                }
                if (!(marca == null)) {
                    where += " and o.activomarca=:activomarca";
                    parametros.put("activomarca", marca);
                }
                if (!((descripcion == null) || (descripcion.isEmpty()))) {
                    where += " and upper(o.descripcion) like :descripcion";
                    parametros.put("descripcion", descripcion.toUpperCase() + "%");
                }
                if (!((inventario == null) || (inventario.isEmpty()))) {
                    where += " and upper(o.inventario) like :inventario";
                    parametros.put("inventario", inventario.toUpperCase() + "%");
                }
                if (!((alterno == null) || (alterno.isEmpty()))) {
                    where += " and upper(o.alterno) like :alterno";
                    parametros.put("alterno", alterno.toUpperCase() + "%");
                }
                if (!((codigobarras == null) || (codigobarras.isEmpty()))) {
                    where += " and upper(o.barras) like :codigobarras";
                    parametros.put("codigobarras", "%" + codigobarras.toUpperCase() + "%");
                }
                if (!((modelo == null) || (modelo.isEmpty()))) {
                    where += " and upper(o.modelo) like :modelo";
                    parametros.put("modelo", modelo.toUpperCase() + "%");
                }
                if (!((numeroserie == null) || (numeroserie.isEmpty()))) {
                    where += " and upper(o.numeroserie) like :numeroserie";
                    parametros.put("numeroserie", numeroserie.toUpperCase() + "%");
                }
                if (subgrupoBean.getGrupo() != null) {
                    if (subgrupoBean.getSubGrupo() != null) {
                        where += " and o.subgrupo=:subgrupo";
                        parametros.put("subgrupo", subgrupoBean.getSubGrupo());
                    } else {
                        where += " and o.grupo=:grupo";
                        parametros.put("grupo", subgrupoBean.getGrupo());
                    }
                }
                if (tipo != null) {
                    where += " and o.grupo.tipo=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (estado != null) {
                    where += " and o.estado=:estado";
                    parametros.put("estado", estado);
                }
                if (getInstitucion() != null) {
                    where += " and o.externo.institucion=:institucion";
                    parametros.put("institucion", getInstitucion());
                }
                if (clasificacion != null) {
                    where += " and o.clasificacion=:clasificacion";
                    parametros.put("clasificacion", clasificacion);
                }
                if (poliza != null) {
                    where += " and o.poliza=:poliza";
                    parametros.put("poliza", poliza);
                }
                if (proveedoresBean.getEmpresa() != null) {
                    where += " and o.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getEmpresa().getProveedores());
                }
                if (getEntidadesBean().getEntidad() != null) {
                    where += " and o.custodio=:custodio";
                    parametros.put("custodio", getEntidadesBean().getEntidad().getEmpleados());
                }
                if (getOficinasBean().getEdificio() != null) {
                    if (oficina != null) {
                        where += " and o.localizacion=:localizacion";
                        parametros.put("localizacion", oficina);
                    } else {
                        where += " and o.localizacion.edificio=:edificio";
                        parametros.put("edificio", getOficinasBean().getEdificio());
                    }
                }
                if (getGarantia() == 1) {
                    where += " and o.garantia=true ";
                } else if (getGarantia() == -1) {
                    where += " and o.garantia=false ";
                }
                if (operativo == 1) {
                    where += " and o.operativo=true ";
                } else if (operativo == -1) {
                    where += " and o.operativo=false ";
                }
                if (control == 1) {
                    where += " and o.control=true ";
                } else if (control == -1) {
                    where += " and o.control=false ";
                }
                if (factura != null) {
                    where += " and o.factura=:factura";
                    parametros.put("factura", factura);
                }
                if (!((tipoAuxiliar == null) || (tipoAuxiliar.isEmpty()))) {
                    where += " and o.tipoauxiliar=:tipoauxiliar";
                    parametros.put("tipoauxiliar", tipoAuxiliar);
                }
                if (!((nominativo == null) || (nominativo.isEmpty()))) {
                    where += " and o.nominativo=:nominativo";
                    parametros.put("nominativo", nominativo);
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbActivos.contar(parametros);
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
                activos.setRowCount(total);
                try {
                    return ejbActivos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String modifica() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        activo = (Activos) activos.getRowData();
//        if (activo.getPadre() != null) {
//            // es hijo
//            if (activo.getPadre().getBaja() == null) {
//                MensajesErrores.advertencia("Es necesario dar baja al padre antes del hijo");
//                return null;
//            }
//        }
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        obligaciones = new LinkedList<>();
        proveedoresBean.setEmpresa(null);
        proveedoresBean.setRuc(null);
        if (activo.getProveedor() != null) {
            proveedoresBean.setEmpresa(activo.getProveedor().getEmpresa());
            proveedoresBean.setRuc(activo.getProveedor().getEmpresa().getRuc());
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", activo);
        try {
            hijos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            tackings = ejbTracking.encontarParametros(parametros);
            tacking = new Trackingactivos();
            obligaciones = ejbObligacion.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.imagen.modulo='ACTIVOFOTO' and o.imagen.idmodulo=:activo");
            parametros.put("activo", activo.getId());
            archivoImagen = null;
            List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
            for (Archivos a : larch) {
                archivoImagen = a;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(InicioBajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String iniciaBaja() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        activo = (Activos) activos.getRowData();
//        if (activo.getPadre() != null) {
//            // es hijo
//            if (activo.getPadre().getBaja() == null) {
//                MensajesErrores.advertencia("Es necesario dar baja al padre antes del hijo");
//                return null;
//            }
//        }
        obligaciones = new LinkedList<>();
        proveedoresBean.setEmpresa(null);
        proveedoresBean.setRuc(null);
        if (activo.getProveedor() != null) {
            proveedoresBean.setEmpresa(activo.getProveedor().getEmpresa());
            proveedoresBean.setRuc(activo.getProveedor().getEmpresa().getRuc());
        }
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", activo);
        try {
            hijos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:padre");
            parametros.put("padre", activo);
            tackings = ejbTracking.encontarParametros(parametros);
            tacking = new Trackingactivos();
            obligaciones = ejbObligacion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(InicioBajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    private boolean validar() {
        if (activo.getBaja() == null) {
            MensajesErrores.advertencia("Tipo de Alta es obligatoria");
            return true;
        }
        if ((activo.getCausa() == null) || (activo.getCausa().isEmpty())) {
            MensajesErrores.advertencia("Causa es obligatoria");
            return true;
        }
        if (entidad == null) {
            MensajesErrores.advertencia("Seleccione un autorizador primero");
            return true;
        }
        return false;
    }

//    public String grabar() {
//        if (validar()) {
//            return null;
//        }
//
//        try {
//            // crear el codigo y ver si se tiene que hacer el asiento
//
//            activo.setFechabaja(new Date());
//            activo.setPoliza(null);
//            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
//            String vale = ejbCabecera.validarCierre(activo.getFechabaja());
//            if (vale != null) {
//                MensajesErrores.advertencia(vale);
//                return null;
//            }
//            if (!activo.getDepreciable()) {
//                // generar el asiento
//                contabiliza(activo);
//            }
//            if (activo.getBaja().getContabiliza()) {
//                contabilizaBaja(activo);
//            }
////            Trackingactivos t = new Trackingactivos();
//            tacking.setActivo(activo);
//            tacking.setDescripcion("BAJA DE ACTIVO : " + activo.getCausa());
//            tacking.setFecha(new Date());
//            tacking.setTipo(-1);
////            tacking.setCuenta1();
//            tacking.setCuenta2(activo.getBaja().getCuenta() + "#" + activo.getBaja().getDebito());
//            tacking.setUsuario(getSeguridadbean().getLogueado().getUserid());
//            tacking.setValor(activo.getValoralta().floatValue());
//            tacking.setValornuevo(activo.getValoradquisiscion().floatValue());
//            ejbTracking.create(tacking, getSeguridadbean().getLogueado().getUserid());
//            for (Activos a : hijos) {
//                a.setBaja(activo.getBaja());
//                a.setFechabaja(new Date());
//                a.setCausa(activo.getCausa());
//                a.setLocalizacion(activo.getLocalizacion());
//                a.setPoliza(null);
//                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
//                Trackingactivos t = new Trackingactivos();
//                t.setActivo(a);
//                t.setDescripcion("BAJA DE ACTIVO : " + a.getCausa());
//                t.setFecha(new Date());
//                t.setTipo(-1);
//                t.setCuenta1(a.getBaja() == null ? null : a.getBaja().getCuenta());
//                t.setCuenta2(a.getBaja() == null ? null : a.getBaja().getDebito());
//                t.setUsuario(getSeguridadbean().getLogueado().getUserid());
//                t.setValor(a.getValoralta() == null ? 0 : a.getValoralta().floatValue());
//                t.setValornuevo(a.getValoradquisiscion() == null ? 0 : a.getValoradquisiscion().floatValue());
//                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                if (!a.getDepreciable()) {
//                    // generar el asiento
////                    contabiliza(a);
//                }
//                if (a.getBaja().getContabiliza()) {
////                    contabilizaBaja(a);
//                }
//            }
//
//        } catch (GrabarException | InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//        formulario.cancelar();
//        buscar();
//        return null;
//    }
    public String grabarNuevo() {
        entidad = empleadosBean.getEmpleadoSeleccionado().getEntidad();
        if (entidad == null) {
            MensajesErrores.advertencia("Seleccione un solicitante primero");
            return null;
        }
        if (activo.getFechasolicitud() == null) {
            MensajesErrores.advertencia("Fecha de solicitud es necesaria");
            return null;
        }
        if (activo.getFechasolicitud().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha de solicitud menor a periodo vigente");
            return null;
        }
        try {
//Depreciar el activo hasta la fecha de solicitud de baja y contabilizar la depreciacion y la baja
            Calendar c = Calendar.getInstance();
            c.setTime(activo.getFechasolicitud());
            int anio = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH) + 1;
            int dia = c.get(Calendar.DAY_OF_MONTH);
            if (activo.getDepreciable() && (!activo.getControl())) {
                depreciar(activo, anio, mes, dia);
                contabilizaDepreciacion(activo);
            }
            contabilizaBaja(activo);
            // crear el codigo y ver si se tiene que hacer el asiento
//Numero de Acta De Inicio de Bajas
            Actas actaNueva = new Actas();
            actaNueva.setFecha(activo.getFechasolicitud());
            Codigos configuracion = codigosBean.traerCodigo(Constantes.CONFIGURACION_ACTAS, "02");
            Codigos tipoActa = codigosBean.traerCodigo(Constantes.ACTAS, "02");
            if (tipoActa == null) {
                MensajesErrores.advertencia("No existe tipo de acta de código 02");
                return null;
            }
            if (configuracion == null) {
                MensajesErrores.advertencia("No existe configuración de acta de código 02");
                return null;
            }
            actaNueva.setAceptacion(configuracion.getNombre());
            actaNueva.setAntecedentes(configuracion.getDescripcion());
            actaNueva.setTipo(tipoActa);
            String numeroActa = tipoActa.getParametros();
            if ((numeroActa == null) || (numeroActa.isEmpty())) {
                numeroActa = "1";
            }
            int num = Integer.parseInt(numeroActa);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            DecimalFormat df1 = new DecimalFormat("000000");
            actaNueva.setNumero(num);
            actaNueva.setNumerotexto(sdf.format(actaNueva.getFecha()) + df1.format(num));
            actaNueva.setEntregan(seguridadbean.getLogueado().toString() + "@" + "ELABORADO POR@" + seguridadbean.getLogueado().getPin() + "@");
            actaNueva.setReciben(entidad.toString() + "@" + "ADMINISTRADOR DEL CONTRATO@");
            if (validar()) {
                return null;
            }
            int nuevoNumero = num + 1;
            tipoActa.setParametros("" + nuevoNumero);
            ejbCodigos.edit(tipoActa, seguridadbean.getLogueado().getUserid());
            ejbActas.create(actaNueva, seguridadbean.getLogueado().getUserid());
            Actasactivos aa = new Actasactivos();
            aa.setActa(actaNueva);
            aa.setActivo(activo);
            ejbActasActivos.create(aa, seguridadbean.getLogueado().getUserid());
//            Trackingactivos t = new Trackingactivos();
            tacking.setActivo(activo);
            tacking.setDescripcion("SOLICITUD BAJA DE ACTIVO : " + activo.getCausa() + " Solicitado por :" + entidad.toString());
            tacking.setFecha(new Date());
            tacking.setTipo(-2);
            tacking.setCuenta1("Usuario Anterior: " + (activo.getCustodio() != null ? activo.getCustodio().toString() : "SIN USUARIO"));
            tacking.setCuenta2("SIN USUARIO - INGRESO A BOGEGA");
            tacking.setUsuario(getSeguridadbean().getLogueado().getUserid());
            tacking.setValor(activo.getValoralta() == null ? 0 : activo.getValoralta().floatValue());
            tacking.setValornuevo(activo.getValoradquisiscion() == null ? 0 : activo.getValoradquisiscion().floatValue());
            tacking.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
            ejbTracking.create(tacking, getSeguridadbean().getLogueado().getUserid());

            activo.setSolicitante(entidad.getEmpleados());
//            activo.setCustodio(empleadosBean.getEmpleadoSeleccionado());
            activo.setCustodio(null); //Ingresa a bodega

            //La misma fecha de baja del bien es igual a la fecha de solicitud de baja 
            //las depreciaciones se calcula hasta la fecha de solicitud
            activo.setFechabaja(activo.getFechasolicitud());
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());

            for (Activos a : hijos) {
                a.setSolicitante(entidad.getEmpleados());
                a.setFechasolicitud(activo.getFechasolicitud());
                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
                Trackingactivos t = new Trackingactivos();
                t.setActivo(a);
                t.setDescripcion("SOLICITUD BAJA DE ACTIVO : " + activo.getCausa() + " Solicitado por :" + entidad.toString());
                t.setFecha(new Date());
                t.setTipo(-2);
                t.setCuenta1(a.getAlta().getCuenta());
                t.setCuenta2(a.getAlta().getDebito());
                t.setUsuario(getSeguridadbean().getLogueado().getUserid());
                t.setValor(a.getValoralta().floatValue());
                t.setValornuevo(a.getValoradquisiscion().floatValue());
                t.setActa(actaNueva.getNumerotexto() + " " + actaNueva.getTipo().getNombre());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//                if (!a.getDepreciable()) {
//                    // generar el asiento
//                    contabiliza(a);
//                }
//                if (a.getBaja().getContabiliza()) {
//                    contabilizaBaja(a);
//                }
            }
            actasActivosBean.imprime(actaNueva);

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    private void contabilizaBaja(Activos a) {
        try {
            String cuentaDebito = a.getGrupo().getIniciodepreciccion() + a.getGrupo().getTipo().getCodigo();
            String cuentaDepreciacionAcumulada = a.getGrupo().getFindepreciacion() + a.getGrupo().getTipo().getCuenta();
            Tipoasiento ta = a.getBaja().getTipoasiento();
            int numero = ta.getUltimo();
            numero++;
            ta.setUltimo(numero);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

            Cabeceras cab = new Cabeceras();
            cab.setDescripcion("Baja activo Fijo :" + a.getNombre());
            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cab.setDia(new Date());
            cab.setTipo(ta);
            cab.setNumero(numero);
            cab.setFecha(a.getFechasolicitud());
            cab.setIdmodulo(a.getId());
            cab.setOpcion("BAJA ACTIVOS");
            ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
            if (a.getValoralta() == null) {
                a.setValoralta(BigDecimal.ZERO);
            }

            if (a.getDepreciable() && (!a.getControl())) {
                Renglones r1 = new Renglones(); // reglon de depreciación acumulada
                double depre = traerDepreciacion(a);
                r1.setCabecera(cab);
                r1.setFecha(a.getFechasolicitud());
                r1.setReferencia("Baja activo Fijo :" + a.getNombre());
                r1.setValor(new BigDecimal(depre));
                r1.setCuenta(cuentaDepreciacionAcumulada);
                r1.setSigno(1);
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                Renglones r2 = new Renglones(); // reglon del valor residual si tiene depreciación o valor alta si no tiene depreciacion
                r2.setCabecera(cab);
                r2.setFecha(a.getFechasolicitud());
                r2.setReferencia("Baja activo Fijo :" + a.getNombre());
                r2.setValor(new BigDecimal(a.getValoralta().doubleValue() - depre));
                r2.setCuenta(a.getBaja().getCuenta1());
                r2.setSigno(1);
                ejbRenglones.create(r2, seguridadbean.getLogueado().getUserid());

                Renglones r = new Renglones(); // reglon debito
                r.setCabecera(cab);
                r.setReferencia("Baja activo Fijo :" + a.getNombre());
                r.setFecha(a.getFechasolicitud());
                r.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
                r.setCuenta(cuentaDebito);
                r.setSigno(1);
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            } else {
                Renglones r = new Renglones(); // reglon credito
                r.setCabecera(cab);
                r.setReferencia("Baja activo Fijo :" + a.getNombre());
                r.setFecha(a.getFechasolicitud());
                r.setValor(new BigDecimal(a.getValoralta().doubleValue()));
                r.setCuenta(a.getGrupo().getDebitoorden());
                r.setSigno(1);
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());

                Renglones r1 = new Renglones(); // reglon de banco
                r1.setCabecera(cab);
                r1.setFecha(a.getFechasolicitud());
                r1.setReferencia("Baja activo Fijo :" + a.getNombre());
                r1.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
                r1.setCuenta(a.getGrupo().getCreditoorden());
                r1.setSigno(1);
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());

            }

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public double traerDepreciacion(Activos a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.activo=:activo ");
        parametros.put("activo", a);
        try {
            return ejbDepreciaciones.sumarCampoDoble(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteBienesAseguradosActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void contabilizaDepreciacion(Activos a) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(a.getFechasolicitud());
            int anio = c.get(Calendar.YEAR);
            int mes = c.get(Calendar.MONTH) + 1;

            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.activo=:activo and o.baja is not null");
            parametros.put("activo", a);
            double valor = ejbDepreciaciones.sumarCampoDoble(parametros);
            if (valor == 0) {
                return;
            }

            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
            Tipoasiento ta = configuracionBean.getConfiguracion().getTadepreciacion();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras asiento = new Cabeceras();
            asiento.setDescripcion("Asiento de depreciación de : " + anioMes);
            asiento.setDia(new Date());
            asiento.setTipo(ta);
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(a.getFechasolicitud());
            asiento.setIdmodulo(0);
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("DEPRECIACION" + anioMes);
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());

            String cuentaDebito = a.getClasificacion().getParametros();
            String cuentaCredito = a.getGrupo().getFindepreciacion() + a.getGrupo().getTipo().getCuenta();
            Renglones r = new Renglones();
            r.setCuenta(cuentaDebito);
            r.setFecha(a.getFechasolicitud());
            r.setReferencia("Cuenta Activo depreciación : " + anioMes);
            r.setValor(new BigDecimal(valor));
            r.setCabecera(asiento);
            r.setSigno(1);
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            r = new Renglones();
            r.setCuenta(cuentaCredito);
            r.setFecha(a.getFechasolicitud());
            r.setReferencia("Cuenta crédito depreciación : " + anioMes);
            r.setValor(new BigDecimal(valor * -1));
            r.setCabecera(asiento);
            r.setSigno(1);
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InicioBajaMasivaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//
//    public void contabilizaBaja(Activos a) {
//        try {
//            Tipoasiento ta = a.getAlta().getTipoasiento();
//
//            int numero = ta.getUltimo();
//            numero++;
//            ta.setUltimo(numero);
////            String[] aux = a.getClasificacion().getParametros().split("#");
////            if (aux.length != 2) {
////                MensajesErrores.fatal("Mala configuración de clasificación");
////            }
//            String cuentaDebito = a.getGrupo().getIniciodepreciccion() + a.getGrupo().getTipo().getCodigo();
//
//            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
//            Cabeceras cab = new Cabeceras();
//            cab.setDescripcion("Baja activo Fijo :" + a.getNombre());
//            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            cab.setDia(new Date());
//            cab.setTipo(ta);
//            cab.setNumero(numero);
//            cab.setFecha(new Date());
//            cab.setIdmodulo(a.getId());
//            cab.setOpcion("BAJA ACTIVOS");
//            ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
//            Renglones r1 = new Renglones(); // reglon de banco
//            r1.setCabecera(cab);
//            r1.setFecha(new Date());
//            r1.setReferencia("Baja activo Fijo :" + a.getNombre());
//            r1.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
//            r1.setCuenta(cuentaDebito);
//            Renglones r = new Renglones(); // reglon de banco
//            r.setCabecera(cab);
//            r.setReferencia("Baja activo Fijo :" + a.getNombre());
//            r.setFecha(new Date());
//            r.setValor(new BigDecimal(a.getValoralta().doubleValue()));
//            r.setCuenta(a.getAlta().getCuenta1());
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
////            renglones.add(r);
////            renglones.add(r1);
//        } catch (GrabarException | InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public void contabiliza(Activos a) {
//        try {
//            Tipoasiento ta = a.getAlta().getTipoasientocontrol();
//
//            int numero = ta.getUltimo();
//            numero++;
//            ta.setUltimo(numero);
//            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
//            Cabeceras cab = new Cabeceras();
//            cab.setDescripcion("Baja activo Fijo :" + a.getNombre() + " Causa : " + a.getCausa());
//            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            cab.setDia(new Date());
//            cab.setTipo(ta);
//            cab.setNumero(numero);
//            cab.setFecha(a.getFechabaja());
//            cab.setIdmodulo(a.getId());
//            cab.setOpcion("BAJA ACTIVOS");
//            ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
//            Renglones r1 = new Renglones(); // reglon de banco
//            r1.setCabecera(cab);
//            r1.setFecha(cab.getFecha());
//            r1.setReferencia("Baja activo Fijo :" + a.getNombre() + " Causa : " + a.getCausa());
//            r1.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
//            r1.setCuenta(a.getBaja().getCuenta());
//            Renglones r = new Renglones(); // reglon de banco
//            r.setCabecera(cab);
//            r.setReferencia("Baja activo Fijo :" + a.getNombre() + " Causa : " + a.getCausa());
//            r.setFecha(cab.getFecha());
//            r.setValor(new BigDecimal(a.getValoralta().doubleValue()));
//            r.setCuenta(a.getBaja().getDebito());
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
////            renglones.add(r);
////            renglones.add(r1);
//        } catch (GrabarException | InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//    }

    public String nuevaObligacion() {
        if (proveedoresBean.getEmpresa() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor");
            return null;
        }
        obligacionesBean.setEstado(2);
        formularioObligacion.insertar();
        return null;
    }

    public String agregarObligacion(Obligaciones o) {
        if (proveedoresBean.getEmpresa() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }
        if (esta(o)) {
            MensajesErrores.advertencia("Obligación ya seleccionada");
            return null;
        }
        Activoobligacion a = new Activoobligacion();
        a.setObligacion(o);
        a.setActivo(activo);
        obligaciones.add(a);
        formularioObligacion.cancelar();
        return null;
    }

    private boolean esta(Obligaciones o) {
        for (Activoobligacion a : obligaciones) {
            if (a.getObligacion().equals(o)) {
                return true;
            }
        }
        return false;
    }

    public String eliminarObligacion() {
        obligaciones.remove(formularioObligacion.getFila().getRowIndex());
        formularioObligacion.cancelar();
        return null;
    }

    public List<Activos> getHijosFuera() {
        if (activos == null) {
            return null;
        }
        if (!activos.isRowAvailable()) {
            return null;
        }
        Activos a = (Activos) activos.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", a);
        try {
            return ejbActivos.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(InicioBajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getHijosNumero() {
        if (activos == null) {
            return 0;
        }
        if (!activos.isRowAvailable()) {
            return 0;
        }
        Activos a = (Activos) activos.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.padre=:padre and o.baja is null");
        parametros.put("padre", a);
        try {
            return ejbActivos.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(InicioBajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void cambiaApellido(ValueChangeEvent event) {
        if (listaEntidades == null) {
            listaEntidades = new LinkedList<>();
        }
        entidad = null;
        String newWord = (String) event.getNewValue();
        for (Entidades e : getListaEntidades()) {
            if (e.toString().compareToIgnoreCase(newWord) == 0) {
                entidad = e;
            }
        }

    }

    public void entidadChangeEventHandler(TextChangeEvent event) {
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.apellidos");
        String where = "  o.activo=true ";
        where += " and upper(o.apellidos) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        parametros.put(";where", where);
        try {
            setListaEntidades(ejbEntidades.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }

    public String reporte(Activos activo) {
        this.activo = activo;
        String numeroActa = null;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", activo.getCodigo());
            List<Activos> listaActivos = ejbActivos.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo and o.acta.tipo.codigo='02'");
            parametros.put("activo", activo);
            List<Actasactivos> listaAA = ejbActasActivos.encontarParametros(parametros);
            if (!listaAA.isEmpty()) {
                Actasactivos aa = listaAA.get(0);
                numeroActa = aa.getActa().getNumerotexto();
            }
            pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("ACTIVOS FIJOS", AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("Acta Nro. " + (numeroActa != null ? numeroActa : ""), AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("ACTA DE BAJAS", AuxiliarReporte.ALIGN_CENTER, 9, false);
//            pdf.agregaParrafoCompleto("\n\n", AuxiliarReporte.ALIGN_CENTER, 9, false);

            Date fecha = activo.getFechaingreso();
            String anio = new SimpleDateFormat("yyyy").format(fecha);
            String mes = new SimpleDateFormat("MM").format(fecha);
            String nomMes = seguridadbean.traerNombreMes(Integer.parseInt(mes)).toLowerCase();
            String dia = new SimpleDateFormat("dd").format(fecha);
            String custodio = activo.getCustodio() != null ? activo.getCustodio().toString() : "";

            pdf.agregaParrafoCompleto(("En la ciudad de Quito, a los " + dia + " dias del mes de " + nomMes + " del " + anio + " los suscritos: "
                    + "el Sr.(a/ita). " + custodio + ", de la Unidad de Bienes  y el Guardalmacén de turno; nos constituimos en la Unidad "
                    + "de Control de Bienes, con el objeto de realizar la diligencia de baja de los bienes de la institución."), AuxiliarReporte.ALIGN_CENTER, 9, false);
            pdf.agregaParrafoCompleto("\n\n", AuxiliarReporte.ALIGN_CENTER, 6, false);
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "CODIGO" + "\n" + "ACTIVO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "DESCRIPCION"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "MARCA"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "MODELO"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "ESTADO DEL BIEN"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "SERIE"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 7, true, "VALOR"));
            for (Activos a : listaActivos) {
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getCodigo()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getDescripcion()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getActivomarca() != null ? a.getActivomarca().getNombre() : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getModelo()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getEstado().getNombre()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, a.getNumeroserie()));
                valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_RIGHT, 7, false, a.getValoralta().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, valores, 7, 100, "");
            pdf.agregaParrafo("\n");
            double iva = 0.00;
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Subtotal: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 7, false, activo.getValoralta().doubleValue()));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "IVA: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 7, false, activo.getIva() == null ? iva : (activo.getIva().equals(0.00)) ? iva : (activo.getValoralta().doubleValue() * activo.getIva().doubleValue())));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Total: "));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, listaActivos.size() + ""));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 7, false, activo.getIva() == null ? (activo.getValoralta().doubleValue()) : (activo.getIva().equals(0.00)) ? (activo.getValoralta().doubleValue()) : (activo.getValoralta().doubleValue() + (activo.getValoralta().doubleValue() * activo.getIva().doubleValue()))));
            pdf.agregarTabla(null, valores, 3, 100, "");
            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("OBSERVACIÓN :");
            pdf.agregaParrafo("Esta diligencia se realiza por reposición conforme a lo manifestado en:\n");
            pdf.agregaParrafo("\n");
//            Empleados e = null;
//            parametros = new HashMap();
//            parametros.put(";where", "o.cargoactual.descripcion='RESPONSABLE DE UNIDAD DE BIENES'");
//            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
//            if (!lista.isEmpty()) {
//                e = lista.get(0);
//            }
            Codigos bien = codigosBean.traerCodigo(Constantes.RESPONSABLE_UNIDAD, "01");
            if (bien == null) {
                MensajesErrores.advertencia("No existe Responsable de Bienes");
                return null;
            }
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "ELABORADO POR"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "REVISADO POR"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "AUTORIZADO POR"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "_______________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "GUARDALMACEN\n" + (seguridadbean.getLogueado().toString())));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "CONTROL DE BIENES\n" + (bien.getDescripcion() != null ? bien.getDescripcion() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "COORDINADOR ADMINISTRATIVO"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (seguridadbean.getLogueado().getPin())));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:" + (bien.getParametros() != null ? bien.getParametros() : "")));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 9, false, "C.I.:"));
            pdf.agregarTabla(null, valores, 3, 100, "");
            reportepdf = pdf.traerRecurso();

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(CambioCustodioActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        nombrepdf = "Inicio de Baja-" + numeroActa + ".pdf";
        formularioImprimir.insertar();
        return null;
    }

    public void depreciar(Activos a, int anio, int mes, int dia) {
        try {

            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=:activo");
            parametros.put("activo", a);
            parametros.put(";orden", "o.id desc");
            List<Depreciaciones> lista = ejbDepreciaciones.encontarParametros(parametros);
            if (!lista.isEmpty()) {

                int anioMax = lista.get(0).getAnio();
                int mesMax = lista.get(0).getMes();

                parametros = new HashMap();
                parametros.put(";where", "o.activo=:activo");
                parametros.put("activo", a);
                parametros.put(";campo", "o.activo");
                parametros.put(";suma", "sum(o.valor),count(o)");
                List<Object[]> listaObjetos = ejbDepreciaciones.sumar(parametros);

                for (Object[] objeto : listaObjetos) {
                    Double sumaDepreciacion = (Double) objeto[1];
                    int cuantasCoutas = ((Long) objeto[2]).intValue();

                    if (cuantasCoutas == a.getVidautil()) {
                        return;
                    }
                    int dias = 30;
                    float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                    float valorResidual = a.getValoralta().floatValue() * porcentaje;
                    float valorDepreciarDiaria = (valorResidual) / (a.getVidautil() * 30);
                    float valorDepreciar = valorDepreciarDiaria * dias;

                    if (mesMax == (mes - 1) && anioMax == anio) {
                        Depreciaciones d = new Depreciaciones();
                        d.setActivo(a);
                        d.setAnio(anio);
                        d.setMes(mes);
                        d.setDepreciacion(a.getVidautil());
                        String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                        String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                        d.setCuentaDebito(cuentaDebito);
                        d.setCuentaCredito(cuentaCredito);
                        d.setBaja(a.getFechasolicitud());
                        if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                            valorDepreciar = valorDepreciarDiaria * dia;
                            d.setValor(valorDepreciar);
                            d.setDias(dia);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                            return;
                        } else {
                            if (cuantasCoutas + 1 == a.getVidautil()) {
                                valorDepreciar = valorResidual - new Float(sumaDepreciacion);
                                d.setValor(valorDepreciar);
                                d.setDias(dias);
                                ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                return;
                            }
                        }
                    } else {
                        for (int i = mes; i <= mesMax; i++) {
                            Depreciaciones d = new Depreciaciones();
                            d.setActivo(a);
                            d.setAnio(anio);
                            d.setMes(mes);
                            d.setDepreciacion(a.getVidautil());
                            String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                            String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                            d.setCuentaDebito(cuentaDebito);
                            d.setCuentaCredito(cuentaCredito);
                            d.setBaja(a.getFechasolicitud());
                            if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                                if (i == mes) {
                                    valorDepreciar = valorDepreciarDiaria * dia;
                                    d.setValor(valorDepreciar);
                                    d.setDias(dia);
                                    ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                    return;
                                } else {
                                    valorDepreciar = valorDepreciarDiaria * dias;
                                    d.setValor(valorDepreciar);
                                    d.setDias(dias);
                                    ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                }
                            } else {
                                if (cuantasCoutas + 1 == a.getVidautil()) {
                                    valorDepreciar = valorResidual - new Float(sumaDepreciacion);
                                    d.setValor(valorDepreciar);
                                    d.setDias(dias);
                                    ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                                    return;
                                }
                            }

                        }
                    }
                }
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(a.getFechaalta());
                int mesMax = c.get(Calendar.MONTH) + 1;
                int diaMax = c.get(Calendar.DAY_OF_MONTH);
                int dias = 30;
                float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                float valorResidual = a.getValoralta().floatValue() * porcentaje;
                float valorDepreciarDiaria = (valorResidual) / (a.getVidautil() * 30);
                float valorDepreciar = new Float(0);
                Double sumaDepreciacion = 0.00;
                int cuantasCoutas = 0;
                int dia1 = dias - diaMax;

                valorDepreciar = valorDepreciarDiaria * dia1;

                Depreciaciones d = new Depreciaciones();
                d.setActivo(a);
                d.setAnio(anio);
                d.setMes(mesMax);
                d.setDepreciacion(a.getVidautil());
                String cuentaDebito = d.getActivo().getClasificacion().getParametros();
                String cuentaCredito = d.getActivo().getGrupo().getFindepreciacion() + d.getActivo().getGrupo().getTipo().getCuenta();
                d.setCuentaDebito(cuentaDebito);
                d.setCuentaCredito(cuentaCredito);
                d.setValor(valorDepreciar);
                d.setDias(dia1);
                d.setBaja(a.getFechasolicitud());
                ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());

                mesMax = mesMax + 1;
                sumaDepreciacion = d.getValor().doubleValue();

                for (int i = mesMax; i <= mes; i++) {
                    d = new Depreciaciones();
                    d.setActivo(a);
                    d.setAnio(anio);
                    d.setMes(mesMax);
                    d.setDepreciacion(a.getVidautil());
                    d.setCuentaDebito(cuentaDebito);
                    d.setCuentaCredito(cuentaCredito);
                    d.setBaja(a.getFechasolicitud());
                    valorDepreciar = valorDepreciarDiaria * dias;
                    if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                        if (i == mes) {
                            valorDepreciar = valorDepreciarDiaria * dia;
                            d.setValor(valorDepreciar);
                            d.setDias(dia);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                            return;
                        } else {
                            valorDepreciar = valorDepreciarDiaria * dias;
                            d.setValor(valorDepreciar);
                            d.setDias(dias);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                        }
                        sumaDepreciacion += d.getValor();
                        cuantasCoutas = cuantasCoutas + 1;
                    } else {
                        if (cuantasCoutas + 1 == a.getVidautil()) {
                            valorDepreciar = valorResidual - new Float(sumaDepreciacion);
                            d.setValor(valorDepreciar);
                            d.setDias(dias);
                            ejbDepreciaciones.create(d, seguridadbean.getLogueado().getUserid());
                            return;
                        }
                    }
                    mesMax++;
                }
            }

        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(InicioBajaMasivaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return;
    }

//    public void cambiaApellido(ValueChangeEvent event) {
//        entidad = null;
//        empleadoSeleccionado = null;
//        if (listaUsuarios == null) {
//            return;
//        }
//        String newWord = (String) event.getNewValue();
//        for (Empleados e : listaUsuarios) {
//            if (e.getEntidad().toString().compareToIgnoreCase(newWord) == 0) {
//                empleadoSeleccionado = e;
//                entidad = empleadoSeleccionado.getEntidad();
//            }
//        }
//
//    }
//    public void cambiaApellido(ValueChangeEvent event) {
//        entidad = null;
//        empleadoSeleccionado = null;
//        if (listaUsuarios == null) {
//            return;
//        }
//        String newWord = (String) event.getNewValue();
//        for (Empleados e : listaUsuarios) {
//            if (e.getEntidad().toString().compareToIgnoreCase(newWord) == 0) {
//                empleadoSeleccionado = e;
//                entidad = empleadoSeleccionado.getEntidad();
//            }
//        }
//
//    }
//    public void entidadChangeEventHandler(TextChangeEvent event) {
//        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
//        Map parametros = new HashMap();
//        String where = " o.activo=true and o.fechasalida is  null and o.cargoactual is not null";
//        where += " and  upper(o.entidad.apellidos) like :apellidos";
////            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
//        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
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
//    public void empleadoChangeEventHandler(TextChangeEvent event) {
//        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
//        Map parametros = new HashMap();
//        String where = " o.activo=true and o.fechasalida is  null and o.cargoactual is not null";
//        where += " and  upper(o.entidad.apellidos) like :apellidos";
////            where += " and  upper(o.entidad.apellidos) like :apellidos and o.cargoactual is not null";
//        parametros.put("apellidos", codigoBuscar.toUpperCase() + "%");
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
    //    public void cambiaApellido(ValueChangeEvent event) {
//        setEntidad(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Entidades> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " o.activo=true";
//            where += " and  upper(o.apellidos) like :apellidos";
//            parametros.put("apellidos", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.apellidos");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbEntidades.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaUsuarios(new ArrayList());
//            for (Entidades e : aux) {
//                SelectItem s = new SelectItem(e, e.getApellidos());
//                getListaUsuarios().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setEntidad((Entidades) autoComplete.getSelectedItem().getValue());
//            } else {
//
//                Entidades tmp = null;
//                for (int i = 0, max = getListaUsuarios().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaUsuarios().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Entidades) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    setEntidad(tmp);
//                }
//            }
//
//        }
//    }
    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
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
     * @return the tipoAuxiliar
     */
    public String getTipoAuxiliar() {
        return tipoAuxiliar;
    }

    /**
     * @param tipoAuxiliar the tipoAuxiliar to set
     */
    public void setTipoAuxiliar(String tipoAuxiliar) {
        this.tipoAuxiliar = tipoAuxiliar;
    }

    /**
     * @return the nominativo
     */
    public String getNominativo() {
        return nominativo;
    }

    /**
     * @param nominativo the nominativo to set
     */
    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    /**
     * @return the nombrepdf
     */
    public String getNombrepdf() {
        return nombrepdf;
    }

    /**
     * @param nombrepdf the nombrepdf to set
     */
    public void setNombrepdf(String nombrepdf) {
        this.nombrepdf = nombrepdf;
    }

    /**
     * @return the marca
     */
    public Marcas getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(Marcas marca) {
        this.marca = marca;
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

    /**
     * @return the codigoAnterior
     */
    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    /**
     * @param codigoAnterior the codigoAnterior to set
     */
    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    /**
     * @return the nivel
     */
    public Formatos getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Formatos nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the activo
     */
    public Activos getActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(Activos activo) {
        this.activo = activo;
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
     * @return the activos
     */
    public LazyDataModel<Activos> getActivos() {
        return activos;
    }

    /**
     * @param activos the activos to set
     */
    public void setActivos(LazyDataModel<Activos> activos) {
        this.activos = activos;
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
     * @return the listaActivo
     */
    public List getListaActivo() {
        return listaActivo;
    }

    /**
     * @param listaActivo the listaActivo to set
     */
    public void setListaActivo(List listaActivo) {
        this.listaActivo = listaActivo;
    }

    /**
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
    }

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

    public Activos traer(Integer id) throws ConsultarException {
        return ejbActivos.find(id);
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the inventario
     */
    public String getInventario() {
        return inventario;
    }

    /**
     * @param inventario the inventario to set
     */
    public void setInventario(String inventario) {
        this.inventario = inventario;
    }

    /**
     * @return the alterno
     */
    public String getAlterno() {
        return alterno;
    }

    /**
     * @param alterno the alterno to set
     */
    public void setAlterno(String alterno) {
        this.alterno = alterno;
    }

    /**
     * @return the modelo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /**
     * @return the numeroserie
     */
    public String getNumeroserie() {
        return numeroserie;
    }

    /**
     * @param numeroserie the numeroserie to set
     */
    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    /**
     * @return the codigobarras
     */
    public String getCodigobarras() {
        return codigobarras;
    }

    /**
     * @param codigobarras the codigobarras to set
     */
    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
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
     * @return the tipo
     */
    public Tipoactivo getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipoactivo tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the clasificacion
     */
    public Codigos getClasificacion() {
        return clasificacion;
    }

    /**
     * @param clasificacion the clasificacion to set
     */
    public void setClasificacion(Codigos clasificacion) {
        this.clasificacion = clasificacion;
    }

    /**
     * @return the estado
     */
    public Codigos getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Codigos estado) {
        this.estado = estado;
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
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the grupo
     */
    public Grupoactivos getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
    }

    /**
     * @return the obligaciones
     */
    public List<Activoobligacion> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Activoobligacion> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the formularioObligacion
     */
    public Formulario getFormularioObligacion() {
        return formularioObligacion;
    }

    /**
     * @param formularioObligacion the formularioObligacion to set
     */
    public void setFormularioObligacion(Formulario formularioObligacion) {
        this.formularioObligacion = formularioObligacion;
    }

    /**
     * @return the obligacionesBean
     */
    public ObligacionesBean getObligacionesBean() {
        return obligacionesBean;
    }

    /**
     * @param obligacionesBean the obligacionesBean to set
     */
    public void setObligacionesBean(ObligacionesBean obligacionesBean) {
        this.obligacionesBean = obligacionesBean;
    }

    /**
     * @return the hijos
     */
    public List<Activos> getHijos() {
        return hijos;
    }

    /**
     * @param hijos the hijos to set
     */
    public void setHijos(List<Activos> hijos) {
        this.hijos = hijos;
    }

    /**
     * @return the tackings
     */
    public List<Trackingactivos> getTackings() {
        return tackings;
    }

    /**
     * @param tackings the tackings to set
     */
    public void setTackings(List<Trackingactivos> tackings) {
        this.tackings = tackings;
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
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the poliza
     */
    public Polizas getPoliza() {
        return poliza;
    }

    /**
     * @param poliza the poliza to set
     */
    public void setPoliza(Polizas poliza) {
        this.poliza = poliza;
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
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the garantia
     */
    public int getGarantia() {
        return garantia;
    }

    /**
     * @param garantia the garantia to set
     */
    public void setGarantia(int garantia) {
        this.garantia = garantia;
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
     * @return the subgrupoBean
     */
    public SubGruposBean getSubgrupoBean() {
        return subgrupoBean;
    }

    /**
     * @param subgrupoBean the subgrupoBean to set
     */
    public void setSubgrupoBean(SubGruposBean subgrupoBean) {
        this.subgrupoBean = subgrupoBean;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the control
     */
    public int getControl() {
        return control;
    }

    /**
     * @param control the control to set
     */
    public void setControl(int control) {
        this.control = control;
    }

    /**
     * @return the tacking
     */
    public Trackingactivos getTacking() {
        return tacking;
    }

    /**
     * @param tacking the tacking to set
     */
    public void setTacking(Trackingactivos tacking) {
        this.tacking = tacking;
    }

    /**
     * @return the nombreExterno
     */
    public String getNombreExterno() {
        return nombreExterno;
    }

    /**
     * @param nombreExterno the nombreExterno to set
     */
    public void setNombreExterno(String nombreExterno) {
        this.nombreExterno = nombreExterno;
    }

    /**
     * @return the cedulaExterno
     */
    public String getCedulaExterno() {
        return cedulaExterno;
    }

    /**
     * @param cedulaExterno the cedulaExterno to set
     */
    public void setCedulaExterno(String cedulaExterno) {
        this.cedulaExterno = cedulaExterno;
    }

    /**
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
    }

    /**
     * @return the institucion
     */
    public Codigos getInstitucion() {
        return institucion;
    }

    /**
     * @param institucion the institucion to set
     */
    public void setInstitucion(Codigos institucion) {
        this.institucion = institucion;
    }

    /**
     * @return the listaEntidades
     */
    public List<Entidades> getListaEntidades() {
        return listaEntidades;
    }

    /**
     * @param listaEntidades the listaEntidades to set
     */
    public void setListaEntidades(List<Entidades> listaEntidades) {
        this.listaEntidades = listaEntidades;
    }

    /**
     * @return the actasActivosBean
     */
    public ActasActivosBean getActasActivosBean() {
        return actasActivosBean;
    }

    /**
     * @param actasActivosBean the actasActivosBean to set
     */
    public void setActasActivosBean(ActasActivosBean actasActivosBean) {
        this.actasActivosBean = actasActivosBean;
    }

}
