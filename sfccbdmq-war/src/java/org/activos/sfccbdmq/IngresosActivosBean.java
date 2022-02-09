/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

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
import org.beans.sfccbdmq.DepreciacionAcumuladaFacade;
import org.beans.sfccbdmq.ErogacionesFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TrackingactivosFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Erogaciones;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Grupoactivos;
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
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.ClasificadorBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ingresosActivosSfccbdmq")
@ViewScoped
public class IngresosActivosBean {

    /**
     * Creates a new instance of ActivoBean
     */
    public IngresosActivosBean() {
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
//    @EJB
//    private ExternosFacade ejbExternos;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private TrackingactivosFacade ejbTracking;
    @EJB
    private ArchivosFacade ejbArchivo;
    @EJB
    private DepreciacionAcumuladaFacade ejbDepAct;
    private Archivos archivoImagen;
//    private int tamano;
    private String codigoAnterior;
    private Formatos nivel;
    private Activos activo;
    private Formulario formulario = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private LazyDataModel<Activos> activos;
    private List<Activoobligacion> obligaciones;
    private List<Activos> hijos;
    private List<Renglones> renglones;
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
    private Integer factura;
//    private List listaExternos;
    private int garantia;
    private int operativo;
    private int control;
    private Cabeceras cabContabiliza;
    private Cabeceras cabIngreso;
    private List<Trackingactivos> tackings;
    private Trackingactivos tacking;
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
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{subGruposActivosSfccbdmq}")
    private SubGruposBean subgrupoBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
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
        String nombreForma = "IngresoActivosVista";
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
                String where = "  o.fechaalta is null ";
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
                if (garantia == 1) {
                    where += " and o.garantia=true ";
                } else if (garantia == -1) {
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
        activo.setFechaalta(activo.getFechaingreso());
        if (activo.getPadre() != null) {
            // es hijo
            if (activo.getPadre().getAlta() == null) {
                MensajesErrores.advertencia("Es necesario dar el alta al padre antes del hijo");
                return null;
            }
        }
        if (!((activo.getCcosto() == null) || (activo.getCcosto().isEmpty()))) {
            proyectosBean.setProyectoSeleccionado(proyectosBean.traerCodigo(activo.getCcosto()));
            proyectosBean.setTipoBuscar("-1");
            if (proyectosBean.getProyectoSeleccionado() == null) {
                activo.setCcosto(null);
            }
        }
        imagenesBean.setIdModulo(activo.getId());
        imagenesBean.setModulo("ACTIVO");
        imagenesBean.Buscar();
        obligaciones = new LinkedList<>();
        proveedoresBean.setEmpresa(null);
        proveedoresBean.setRuc(null);
        if (activo.getProveedor() != null) {
            proveedoresBean.setEmpresa(activo.getProveedor().getEmpresa());
            proveedoresBean.setProveedor(activo.getProveedor());
            proveedoresBean.setTipobuscar("-1");
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
        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(IngresosActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        parametros = new HashMap();
        parametros.put(";where", "o.imagen.modulo='ACTIVOFOTO' and o.imagen.idmodulo=:activo");
        parametros.put("activo", activo.getId());
        archivoImagen = null;
        List<Archivos> larch = ejbArchivo.encontarParametros(parametros);
        for (Archivos a : larch) {
            archivoImagen = a;
        }
//        for (Activos a : hijos) {
//            if (a.getControl()) {
//                contabiliza(a,false);
//            }
//            if (a.getAlta().getContabiliza()) {
//                contabilizaIngreso(a, false);
//            }
//        }
//
//        if (activo.getControl()) {
//            // generar el asiento
//            contabiliza(activo,false);
//        }
//        if (activo.getAlta().getContabiliza()) {
//            contabilizaIngreso(activo, false);
//        }
        formulario.editar();
        return null;
    }

    private boolean validar() {
        if (activo.getAlta() == null) {
            MensajesErrores.advertencia("Tipo de Alta es obligatoria");
            return true;
        }
        if (activo.getFechaalta() == null) {
            MensajesErrores.advertencia("Ingrese una fecha de alta");
            return true;
        }
        if (activo.getFechaalta().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha de alta debe ser mayor o igual al periodo vigente");
            return true;
        }
        if (activo.getCcosto() == null) {
            if (proyectosBean.getProyectoSeleccionado() == null) {
                MensajesErrores.advertencia("Centro de costo  es obligatorio");
                return true;
            }
            activo.setCcosto(proyectosBean.getProyectoSeleccionado().getCodigo());
            if (activo.getCcosto() == null) {
                MensajesErrores.advertencia("Centro de costo  es obligatorio");
                return true;
            }
        }
        if (activo.getAlta().getTipo() == 0) {
            // es compra
            if (proveedoresBean.getProveedor() == null) {
                MensajesErrores.advertencia("Proveedor es obligatorio");
                return true;
            }
            if (obligaciones == null) {
                MensajesErrores.advertencia("Obligaciones de pago son obligatorios");
                return true;
            }
            if (obligaciones.isEmpty()) {
                MensajesErrores.advertencia("Obligaciones de pago son obligatorios");
                return true;
            }
//            return true;
            activo.setProveedor(proveedoresBean.getEmpresa().getProveedores());
        } else {
            activo.setProveedor(null);
            obligaciones = new LinkedList<>();
        }
        return false;
    }

    public String grabar() {
        if (proyectosBean.getProyectoSeleccionado() != null) {
            activo.setCcosto(proyectosBean.getProyectoSeleccionado().getCodigo());
        }
        if (validar()) {
            return null;
        }
        
        String vale=ejbCabecera.validarCierre(activo.getFechaalta());
        if (vale!=null){
            MensajesErrores.advertencia(vale);
            return null;
        }
        try {
            renglones = new LinkedList<>();
            // crear el codigo y ver si se tiene que hacer el asiento
//            Integer numero = activo.getGrupo().getSecuencia();
//            numero++;

            Trackingactivos t = new Trackingactivos();
//            DecimalFormat df = new DecimalFormat("0000000000");
//            activo.setCodigo(activo.getGrupo().getTipo().getCodigo() + df.format(numero));
//            activo.setFechaalta(new Date());
            for (Activoobligacion a : obligaciones) {
                a.setActivo(activo);
                a.setFecha(new Date());
                a.setValor(a.getObligacion().getApagar().floatValue());
                ejbObligacion.create(a, getSeguridadbean().getLogueado().getUserid());
            }
            for (Activos a : hijos) {
//                numero++;
                a.setAlta(activo.getAlta());
                a.setFechaalta(activo.getFechaalta());
//                a.setCodigo(a.getGrupo().getTipo().getCodigo() + df.format(numero));
                a.setPoliza(null);
                ejbActivos.edit(a, getSeguridadbean().getLogueado().getUserid());
                t = new Trackingactivos();
                t.setActivo(a);
                t.setDescripcion("ALTA DE ACTIVO");
                t.setFecha(new Date());
                t.setTipo(2);
                if (activo.getExterno() == null) {
                    t.setCuenta2(activo.getCustodio().getEntidad().toString());
                } else {
                    t.setCuenta2(activo.getExterno().getInstitucion().getNombre() + " " + activo.getExterno().getNombre());
                }
//                t.setCuenta1(a.getAlta().getCuenta());
//                t.setCuenta2(a.getAlta().getDebito());
                if (a.getValoradquisiscion() == null) {
                    a.setValoradquisiscion(a.getValoralta());
                }
                t.setUsuario(getSeguridadbean().getLogueado().getUserid());
                t.setValor(a.getValoralta().floatValue());
                t.setValornuevo(a.getValoradquisiscion().floatValue());
                ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
                if (a.getControl()) {
//                if (!a.getDepreciable()) {
                    // generar el asiento
                    contabiliza(a, true);
                }
                if (a.getAlta().getContabiliza()) {
                    contabilizaIngreso(a, true);
                }
            }

            if (activo.getControl()) {
//            if (!activo.getDepreciable()) {
                // generar el asiento
                contabiliza(activo, true);
            }
            if (activo.getAlta().getContabiliza()) {
                contabilizaIngreso(activo, true);
            }
            if (renglones.isEmpty()) {
                MensajesErrores.advertencia("No exsite nada para contabilizar");
                return null;
            }
            for (Renglones r : renglones) {
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            ejbActivos.edit(activo, getSeguridadbean().getLogueado().getUserid());
            t = new Trackingactivos();
            t.setActivo(activo);
            t.setDescripcion("ALTA DE ACTIVO");
            t.setFecha(new Date());
            t.setTipo(2);
            t.setCuenta1(activo.getAlta().getCuenta());
            t.setCuenta2(activo.getAlta().getDebito());
            t.setUsuario(getSeguridadbean().getLogueado().getUserid());
            t.setValor(activo.getValoralta().floatValue());
            if (activo.getValoradquisiscion() == null) {
                activo.setValoradquisiscion(activo.getValoralta());
            }
            t.setValornuevo(activo.getValoradquisiscion().floatValue());
            ejbTracking.create(t, getSeguridadbean().getLogueado().getUserid());
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.padre=:padre");
//            parametros.put("padre", activo);
//            List<Activos> la = ejbActivos.encontarParametros(parametros);

//            Grupoactivos g = activo.getGrupo();
//            g.setSecuencia(numero);
//            ejbGrupos.edit(g, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioImprimir.insertar();
        buscar();
        return null;
    }

    public String grabarAcumulada() {
//        if (validar()) {
//            return null;
//        }

        try {
            for (Activos a : hijos) {
                a.setAlta(activo.getAlta());
                a.setFechaalta(activo.getFechaalta());
                ejbDepAct.depreciar(a, getSeguridadbean().getLogueado().getUserid());

            }
            ejbDepAct.depreciar(activo, getSeguridadbean().getLogueado().getUserid());
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Depreciación calculada correctamente");
        return null;
    }

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (renglones == null) {
            renglones = new LinkedList<>();
        }
        for (Renglones r : renglones) {
            if (r.getCabecera().equals(r1.getCabecera())) {
                if (r.getCentrocosto() == null) {
                    r.setCentrocosto("");
                }
                if ((r.getCuenta().equals(r1.getCuenta())) && r.getCentrocosto().equals(r1.getCentrocosto())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            }
        }
        renglones.add(r1);
        return false;
    }

    public void contabiliza(Activos a, boolean definitivo) {
        try {
            if (definitivo) {
                if (cabContabiliza == null) {
                    Tipoasiento ta = a.getAlta().getTipoasientocontrol();

                    int numero = ta.getUltimo();
                    numero++;
                    ta.setUltimo(numero);
                    ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
                    cabContabiliza = new Cabeceras();
                    cabContabiliza.setDescripcion(a.getObservaciones());
                    cabContabiliza.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    cabContabiliza.setDia(new Date());
                    cabContabiliza.setTipo(ta);
                    cabContabiliza.setNumero(numero);
                    cabContabiliza.setFecha(a.getFechaalta());
                    cabContabiliza.setIdmodulo(a.getId());
                    cabContabiliza.setOpcion("ALTA ACTIVOS");
                    ejbCabecera.create(cabContabiliza, seguridadbean.getLogueado().getUserid());
                }
            }
            Renglones r1 = new Renglones(); // reglon de banco
            if (definitivo) {
                r1.setCabecera(cabContabiliza);
            } else {
                r1.setCabecera(new Cabeceras());
            }
            r1.setFecha(a.getFechaalta());
            r1.setReferencia(a.getDescripcion());
            r1.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
            Cuentas cuentaValida = cuentasBean.traerCodigo(a.getAlta().getCuenta());
            if (cuentaValida.getCcosto()) {
                r1.setCentrocosto(a.getCcosto());
            }
            //r1.setCentrocosto(a.getCcosto());
            r1.setCuenta(a.getAlta().getCuenta());
            Renglones r = new Renglones(); // reglon de banco
            if (definitivo) {
                r.setCabecera(cabContabiliza);
            } else {
                r.setCabecera(new Cabeceras());
            }
            r.setReferencia(a.getDescripcion());
            r.setFecha(a.getFechaalta());
            cuentaValida = cuentasBean.traerCodigo(a.getAlta().getDebito());
            if (cuentaValida.getCcosto()) {
                r.setCentrocosto(a.getCcosto());
            }
            //r.setCentrocosto(a.getCcosto());
            r.setValor(new BigDecimal(a.getValoralta().doubleValue()));
            r.setCuenta(a.getAlta().getDebito());
            estaEnRas(r1);
            estaEnRas(r);
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
//            renglones.add(r);
//            renglones.add(r1);
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public void contabilizaIngreso(Activos a, boolean definitivo) {
        try {
            if (definitivo) {
                if (cabIngreso == null) {
                    Tipoasiento ta = a.getAlta().getTipoasiento();

                    int numero = ta.getUltimo();
                    numero++;
                    ta.setUltimo(numero);
                    ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
                    cabIngreso = new Cabeceras();
                    cabIngreso.setDescripcion(a.getDescripcion());
                    cabIngreso.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    cabIngreso.setDia(new Date());
                    cabIngreso.setTipo(ta);
                    cabIngreso.setNumero(numero);
                    cabIngreso.setFecha(a.getFechaalta());
                    cabIngreso.setIdmodulo(a.getId());
                    cabIngreso.setOpcion("ALTA ACTIVOS");
                    ejbCabecera.create(cabIngreso, seguridadbean.getLogueado().getUserid());
                }
            }
            String cuentaDebito = a.getGrupo().getIniciodepreciccion() + a.getGrupo().getTipo().getCodigo();
            Renglones r1 = new Renglones(); // reglon de banco
            if (definitivo) {
                r1.setCabecera(cabIngreso);
            } else {
                r1.setCabecera(new Cabeceras());
            }
            r1.setFecha(a.getFechaalta());
            r1.setReferencia(a.getDescripcion());
            r1.setValor(new BigDecimal(a.getValoralta().doubleValue()));
//            r1.setCentrocosto(a.getCcosto());
            Cuentas cuentaValida = cuentasBean.traerCodigo(cuentaDebito);
            if (cuentaValida == null) {
                MensajesErrores.advertencia("No existe cuenta contable : " + cuentaDebito);
                return;
            }
            if (cuentaValida.getCcosto()) {
                r1.setCentrocosto(a.getCcosto());
            }
            r1.setCuenta(cuentaDebito);
            Renglones r = new Renglones(); // reglon de banco
            if (definitivo) {
                r.setCabecera(cabIngreso);
            } else {
                r.setCabecera(new Cabeceras());
            }
            r.setReferencia("Alta activo Fijo :" + a.getDescripcion());
            r.setFecha(a.getFechaalta());
            r.setValor(new BigDecimal(a.getValoralta().doubleValue() * -1));
            r.setCuenta(a.getAlta().getCuenta1());
            cuentaValida = cuentasBean.traerCodigo(a.getAlta().getCuenta1());
            if (cuentaValida.getCcosto()) {
                r.setCentrocosto(a.getCcosto());
            }
//            r.setCentrocosto(a.getCcosto());
            estaEnRas(r1);
            estaEnRas(r);
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
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

    public List<Activos> getHijosFuera() {
        if (activos==null){
            return null;
        }
        if (!activos.isRowAvailable()){
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
        if (activos==null){
            return 0;
        }
        if (!activos.isRowAvailable()){
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
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
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
     * @return the cabContabiliza
     */
    public Cabeceras getCabContabiliza() {
        return cabContabiliza;
    }

    /**
     * @param cabContabiliza the cabContabiliza to set
     */
    public void setCabContabiliza(Cabeceras cabContabiliza) {
        this.cabContabiliza = cabContabiliza;
    }

    /**
     * @return the cabIngreso
     */
    public Cabeceras getCabIngreso() {
        return cabIngreso;
    }

    /**
     * @param cabIngreso the cabIngreso to set
     */
    public void setCabIngreso(Cabeceras cabIngreso) {
        this.cabIngreso = cabIngreso;
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
        if (activo==null){
            return null;
        }
        if (activo.getAlta() == null) {
            return null;
        }
        for (Activos a : hijos) {
            if (a.getControl()) {
                contabiliza(a, false);
            }
            a.setAlta(activo.getAlta());
            if (activo.getAlta().getContabiliza()) {
                contabilizaIngreso(a, false);
            }
        }

        if (activo.getControl()) {
            // generar el asiento
            contabiliza(activo, false);
        }
        if (activo.getAlta().getContabiliza()) {
            contabilizaIngreso(activo, false);
        }
        return renglones;
    }

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
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
//    public void cambiaExterno(ValueChangeEvent event) {
////        setExternoEntidad(null);
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//
//            List<Externos> aux = new LinkedList<>();
//            // traer la consulta
//            Map parametros = new HashMap();
//            String where = " upper(o.empresa) like :empresa ";
////            where += "  upper(o.empresa) like :empresa";
//            parametros.put("empresa", newWord.toUpperCase() + "%");
//            parametros.put(";where", where);
//            parametros.put(";orden", " o.empresa");
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbExternos.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            setListaExternos(new ArrayList());
//            for (Externos e : aux) {
//                SelectItem s = new SelectItem(e, e.getEmpresa());
//                getListaExternos().add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                setExternoEntidad((Externos) autoComplete.getSelectedItem().getValue());
//                // pone la localidad
//                
//            } else {
//
//                Externos tmp = null;
//                for (int i = 0, max = getListaExternos().size(); i < max; i++) {
//                    SelectItem e = (SelectItem) getListaExternos().get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Externos) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    setExternoEntidad(tmp);
//                    
//                }
//            }
//
//        }
//    }
//    public List getListaExternos() {
//        return listaExternos;
//    }
//
//    /**
//     * @param listaExternos the listaExternos to set
//     */
//    public void setListaExternos(List listaExternos) {
//        this.listaExternos = listaExternos;
//    }

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

    public void cambiaGrupo(ValueChangeEvent ve) {
        Grupoactivos valor = (Grupoactivos) ve.getNewValue();
        if (valor != null) {
            activo.setValorresidual(valor.getValorresidual().floatValue());
            activo.setDepreciable(valor.getDepreciable());
            activo.setVidautil(valor.getVidautil());
            getSubgrupoBean().setGrupo(valor);
            subgrupoBean.getComboSubGrupo();
        }
    }
}
