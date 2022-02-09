/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import com.ejb.sfcarchivos.ArchivosFacade;
import com.entidades.sfcarchivos.Archivos;
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
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ActivoobligacionFacade;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DepreciacionesFacade;

import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.ErogacionesFacade;
import org.beans.sfccbdmq.GrupoactivosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Erogaciones;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
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
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "bajasActivosSfccbdmq")
@ViewScoped
public class BajasActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public BajasActivosBean() {
        activos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private List<Erogaciones> listadoErogaciones;
    @EJB
    private ErogacionesFacade ejbErogaciones;
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
    private DepreciacionesFacade ejbDepreciacion;
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
    private List<Renglones> renglones;
    private Trackingactivos tacking;
    private String codigo;
    private String nombre;
    private String nombreExterno;
    private String cedulaExterno;
    private String descripcion;
    private String inventario;
    private String alterno;
    private String marca;
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
    private int garantia;
    private int operativo;
    private int control;
    // autocompletar 
    private Entidades entidad;
    private String apellidos;
    private List<Entidades> listaUsuarios;
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
    private Perfiles perfil;

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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ContabBajaActivosVista";
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
                String where = "  o.fechaalta is not null and o.fechabaja is not null and o.fechasolicitud is not null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", codigo + "%");
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
                if (!((marca == null) || (marca.isEmpty()))) {
                    where += " and upper(o.marca) like :marca";
                    parametros.put("marca", marca.toUpperCase() + "%");
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
                    parametros.put("codigobarras", codigobarras.toUpperCase() + "%");
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
                if (institucion != null) {
                    where += " and o.externo.institucion=:institucion";
                    parametros.put("institucion", institucion);
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
            listadoErogaciones = ejbErogaciones.encontarParametros(parametros);
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
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

//    public String iniciaBaja() {
////        if (!menusBean.getPerfil().getModificacion()) {
////            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
////            return null;
////        }
//        activo = (Activos) activos.getRowData();
////        if (activo.getPadre() != null) {
////            // es hijo
////            if (activo.getPadre().getBaja() == null) {
////                MensajesErrores.advertencia("Es necesario dar baja al padre antes del hijo");
////                return null;
////            }
////        }
//        obligaciones = new LinkedList<>();
//        proveedoresBean.setEmpresa(null);
//        proveedoresBean.setRuc(null);
//        if (activo.getProveedor() != null) {
//            proveedoresBean.setEmpresa(activo.getProveedor().getEmpresa());
//            proveedoresBean.setRuc(activo.getProveedor().getEmpresa().getRuc());
//        }
//
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.padre=:padre and o.baja is null");
//        parametros.put("padre", activo);
//        try {
//            hijos = ejbActivos.encontarParametros(parametros);
//            parametros = new HashMap();
//            parametros.put(";where", "o.activo=:padre");
//            parametros.put("padre", activo);
//            tackings = ejbTracking.encontarParametros(parametros);
//            tacking = new Trackingactivos();
//            obligaciones = ejbObligacion.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.advertencia(ex.getMessage());
//            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formulario.insertar();
//        return null;
//    }
    private boolean validar() {
        if (activo.getBaja() == null) {
            MensajesErrores.advertencia("Tipo de Baja es obligatoria");
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
        if (renglones == null) {
            MensajesErrores.advertencia("No existe configuración de cuentas no puede contabilizar la baja");
            return true;
        }
        if (renglones.isEmpty()) {
            MensajesErrores.advertencia("No existe configuración de cuentas no puede contabilizar la baja");
            return true;
        }
        return false;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }

        try {
            // crear el codigo y ver si se tiene que hacer el asiento

            activo.setFechabaja(new Date());
            activo.setPoliza(null);
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            String vale = ejbCabecera.validarCierre(activo.getFechabaja());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            if (!activo.getDepreciable()) {
                // generar el asiento
                contabiliza(activo, true);
            }
            if (activo.getBaja().getContabiliza()) {
                contabilizaBaja(activo, true);
            }
//            Trackingactivos t = new Trackingactivos();
            tacking.setActivo(activo);
            tacking.setDescripcion("BAJA DE ACTIVO : " + activo.getCausa());
            tacking.setFecha(new Date());
            tacking.setTipo(-1);
//            tacking.setCuenta1();
            tacking.setCuenta2(activo.getBaja().getCuenta() + "#" + activo.getBaja().getDebito());
            tacking.setUsuario(getSeguridadbean().getLogueado().getUserid());
            tacking.setValor(activo.getValoralta().floatValue());
            tacking.setValornuevo(activo.getValoradquisiscion().floatValue());
            ejbTracking.create(tacking, getSeguridadbean().getLogueado().getUserid());
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.padre=:padre");
//            parametros.put("padre", activo);
//            List<Activos> la = ejbActivos.encontarParametros(parametros);
            for (Activos a : hijos) {
                a.setBaja(activo.getBaja());
                a.setFechabaja(new Date());
                a.setCausa(activo.getCausa());
                a.setLocalizacion(activo.getLocalizacion());
                a.setPoliza(null);
                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
                Trackingactivos t = new Trackingactivos();
                t.setActivo(a);
                t.setDescripcion("BAJA DE ACTIVO : " + a.getCausa());
                t.setFecha(new Date());
                t.setTipo(-1);
                t.setCuenta1(a.getAlta().getCuenta());
                t.setCuenta2(a.getAlta().getDebito());
                t.setUsuario(getSeguridadbean().getLogueado().getUserid());
                t.setValor(a.getValoralta().floatValue());
                t.setValornuevo(a.getValoradquisiscion().floatValue());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                if (!a.getDepreciable()) {
                    // generar el asiento
                    contabiliza(a, true);
                }
                if (a.getBaja().getContabiliza()) {
                    contabilizaBaja(a, true);
                }
            }

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

//    public String grabarNuevo() {
//        if (entidad == null) {
//            MensajesErrores.advertencia("Seleccione un solicitante primero");
//            return null;
//        }
//        if (activo.getFechasolicitud() == null) {
//            MensajesErrores.advertencia("Seleccione una solicitud primero");
//            return null;
//        }
//        try {
//            // crear el codigo y ver si se tiene que hacer el asiento
//            activo.setSolicitante(entidad.getEmpleados());
//            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
//
////            Trackingactivos t = new Trackingactivos();
//            tacking.setActivo(activo);
//            tacking.setDescripcion("SOLICITUD BAJA DE ACTIVO : " + activo.getCausa() + " Solicitado por :" + entidad.toString());
//            tacking.setFecha(new Date());
//            tacking.setTipo(-2);
////            t.setCuenta1("");
//            tacking.setCuenta2("");
//            tacking.setUsuario(getSeguridadbean().getLogueado().getUserid());
//            tacking.setValor(activo.getValoralta().floatValue());
//            tacking.setValornuevo(activo.getValoradquisiscion().floatValue());
//            ejbTracking.create(tacking, getSeguridadbean().getLogueado().getUserid());
////            Map parametros = new HashMap();
////            parametros.put(";where", "o.padre=:padre");
////            parametros.put("padre", activo);
////            List<Activos> la = ejbActivos.encontarParametros(parametros);
//            for (Activos a : hijos) {
//                a.setSolicitante(entidad.getEmpleados());
//                a.setFechasolicitud(activo.getFechasolicitud());
//                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
//                Trackingactivos t = new Trackingactivos();
//                t.setActivo(a);
//                t.setDescripcion("SOLICITUD BAJA DE ACTIVO : " + activo.getCausa() + " Solicitado por :" + entidad.toString());
//                t.setFecha(new Date());
//                t.setTipo(-1);
//                t.setCuenta1(a.getAlta().getCuenta());
//                t.setCuenta2(a.getAlta().getDebito());
//                t.setUsuario(getSeguridadbean().getLogueado().getUserid());
//                t.setValor(a.getValoralta().floatValue());
//                t.setValornuevo(a.getValoradquisiscion().floatValue());
//                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
////                if (!a.getDepreciable()) {
////                    // generar el asiento
////                    contabiliza(a);
////                }
////                if (a.getBaja().getContabiliza()) {
////                    contabilizaBaja(a);
////                }
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
    private void contabilizaBaja(Activos a, boolean definitiva) {
        Cabeceras cab = new Cabeceras();
        try {
            String cuentaDebito = a.getGrupo().getIniciodepreciccion() + a.getGrupo().getTipo().getCodigo();
            String cuentaDepreciacionAcumulada = a.getGrupo().getFindepreciacion() + a.getGrupo().getTipo().getCuenta();
            if (definitiva) {

                Tipoasiento ta = a.getBaja().getTipoasiento();

                int numero = ta.getUltimo();
                numero++;
                ta.setUltimo(numero);
//            String[] aux = a.getClasificacion().getParametros().split("#");
//            if (aux.length != 2) {
//                MensajesErrores.fatal("Mala configuración de clasificación");
//            }

                ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

//                cab.setDescripcion(tacking.getDescripcion());
                cab.setDescripcion("Baja activo Fijo :" + a.getNombre());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(ta);
                cab.setNumero(numero);
                cab.setFecha(new Date());
                cab.setIdmodulo(a.getId());
                cab.setOpcion("BAJA ACTIVOS");
                ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
            }
            Renglones r1 = new Renglones(); // reglon de banco
            double depre = traerDepreciacion(a);
            r1.setCabecera(cab);
            r1.setFecha(new Date());
//            r1.setReferencia(tacking.getDescripcion());
            r1.setReferencia("Baja activo Fijo :" + a.getNombre());
            r1.setValor(new BigDecimal(depre));
            r1.setCuenta(cuentaDepreciacionAcumulada);

            Renglones r2 = new Renglones(); // reglon de banco
            r2.setCabecera(cab);
            r2.setFecha(new Date());
//            r2.setReferencia(tacking.getDescripcion());
            r2.setReferencia("Baja activo Fijo :" + a.getNombre());
            r2.setValor(new BigDecimal(a.getValoralta().doubleValue() - depre));
            r2.setCuenta(a.getBaja().getCuenta1());

            Renglones r = new Renglones(); // reglon de banco
            r.setCabecera(cab);
            r.setReferencia(tacking.getDescripcion());
            r.setFecha(new Date());
            r.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
            r.setCuenta(cuentaDebito);
            if (definitiva) {
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            }

            renglones.add(r1);
            renglones.add(r2);
            renglones.add(r);
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    private void contabiliza(Activos a, boolean definitivo) {
        try {
            Cabeceras cab = new Cabeceras();
            if (definitivo) {
                Tipoasiento ta = a.getBaja().getTipoasientocontrol();

                int numero = ta.getUltimo();
                numero++;
                ta.setUltimo(numero);
                ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

//                cab.setDescripcion(tacking.getDescripcion());
                cab.setDescripcion("Baja activo Fijo :" + a.getNombre());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(ta);
                cab.setNumero(numero);
                cab.setFecha(new Date());
                cab.setIdmodulo(a.getId());
                cab.setOpcion("BAJA ACTIVOS");
                ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
            }
            Renglones r1 = new Renglones(); // reglon de banco
            r1.setCabecera(cab);
            r1.setFecha(new Date());
//            r1.setReferencia(tacking.getDescripcion());
            r1.setReferencia("Baja activo Fijo :" + a.getNombre());
            r1.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
//            r1.setCuenta(a.getBaja().getCuenta());
            r1.setCuenta(a.getGrupo().getCreditoorden());
            Renglones r = new Renglones(); // reglon de banco
            r.setCabecera(cab);
//            r.setReferencia(tacking.getDescripcion());
            r.setReferencia("Baja activo Fijo :" + a.getNombre());
            r.setFecha(new Date());
            r.setValor(new BigDecimal(a.getValoralta().doubleValue()));
//            r.setCuenta(a.getBaja().getDebito());
            r.setCuenta(a.getGrupo().getDebitoorden());
            if (definitivo) {
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            }
//            renglones.add(r);
//            renglones.add(r1);
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
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
     * @return the marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(String marca) {
        this.marca = marca;
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
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(BajasActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the listaUsuarios
     */
    public List<Entidades> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List<Entidades> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

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

    public List<Renglones> getPrevisualiza() {
        renglones = new LinkedList<>();
        if (activo == null) {
            return null;
        }
        if (activo.getBaja() == null) {
            return null;
        }
        for (Activos a : hijos) {
            if (a.getControl()) {
                contabiliza(a, false);
            }
            a.setBaja(activo.getBaja());
//            if (activo.getBaja()) {
            contabilizaBaja(a, false);
//            }
        }

        if (activo.getControl()) {
            // generar el asiento
            contabiliza(activo, false);
        }
//        if (activo.getBaja().getContabiliza()) {
        contabilizaBaja(activo, false);
//        }
        return renglones;
    }

    /**
     * @return the listadoErogaciones
     */
    public List<Erogaciones> getListadoErogaciones() {
        return listadoErogaciones;
    }

    /**
     * @param listadoErogaciones the listadoErogaciones to set
     */
    public void setListadoErogaciones(List<Erogaciones> listadoErogaciones) {
        this.listadoErogaciones = listadoErogaciones;
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

    public double traerDepreciacion(Activos a) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=:activo ");
//        parametros.put(";where", "o.activo=:activo and mes<=:mes and anio<=:anio");
        parametros.put(";campo", "o.valor");
        parametros.put("activo", a);
//        parametros.put("mes", mes);
//        parametros.put("anio", anio);
        try {
            return ejbDepreciacion.sumarCampoDoble(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteBienesAseguradosActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void cambiaApellido(ValueChangeEvent event) {
        entidad = null;
        if (listaUsuarios == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Entidades e : listaUsuarios) {
            if (e.getApellidos().compareToIgnoreCase(newWord) == 0) {
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
            listaUsuarios = ejbEntidades.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

    }
}
