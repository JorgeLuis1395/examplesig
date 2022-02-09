/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import org.compras.sfccbdmq.ObligacionesCompromisoBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.AdicionalkardexFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.OrganigramasuministrosFacade;
import org.beans.sfccbdmq.SolicitudsuministrosFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.beans.sfccbdmq.UnidadesFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Detallesolicitud;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Solicitudsuministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.entidades.sfccbdmq.Unidades;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ingresosTxSfccbdmq")
@ViewScoped
public class IngresosTxBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public IngresosTxBean() {
        cabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private CabecerainventarioFacade ejbCabecerainventario;

    @EJB
    private TxinventariosFacade ejbTxInventario;
    @EJB
    private AdicionalkardexFacade ejbAdKardex;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private UnidadesFacade ejbUnidades;
    @EJB
    private SolicitudsuministrosFacade ejbSolSum;
    @EJB
    private DetallesolicitudFacade ejbDetSol;
    @EJB
    private OrganigramasuministrosFacade ejbOrgaSum;
    @EJB
    private BodegasuministroFacade ejbBodSum;
    @EJB
    private CabecerasFacade ejbCabeceras;
    private Cabecerainventario cabecera;
    private Kardexinventario kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Cabecerainventario> cabeceras;
    private List<Kardexinventario> listaKardex;
    private List<Kardexinventario> listaKardexb;
    private Integer factura;
    private Integer numero;
    private double descuentoInterno;
    private double descuentoExterno;
    private Integer estado = -10;
    private String observaciones;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Solicitudsuministros solicitud;
    private Bodegas bodega;
    private Bodegas bodegaDestino;
    private boolean transferencia;
    private boolean entragar;
    private Contratos contrato;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{obligacionesCompromisoSfccbdmq}")
    private ObligacionesCompromisoBean obligacionesBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
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

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "MovimientoInventarioVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
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
     * @return the cabecera
     */
    public Cabecerainventario getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabecerainventario cabecera) {
        this.cabecera = cabecera;
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
     * @return the cabeceras
     */
    public LazyDataModel<Cabecerainventario> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Cabecerainventario> cabeceras) {
        this.cabeceras = cabeceras;
    }

    public String buscar() {
        cabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fechadigitacion desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fechadigitacion between :desde and :hasta ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                // Numero de factura
                if (!((factura == null) || (factura <= 0))) {
                    where += " and o.factura =:documento";
                    parametros.put("documento", factura);
                }
                if (!((estado == null) || (estado <= -10))) {
                    where += " and o.estado =:estado";
                    parametros.put("estado", estado);
                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.numero =:numero";
                    parametros.put("numero", numero);
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("marca", observaciones.toUpperCase() + "%");
                }
                if (bodega != null) {
                    where += " and o.bodega=:bodega";
                    parametros.put("bodega", bodega);
                }

                if (tipo != null) {
                    where += " and o.txid=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (contrato != null) {
                    where += " and o.ordencompra.compromiso.contrato=:contrato";
                    parametros.put("contrato", contrato);
                }
                if (proveedoresBean.getProveedor() != null) {
                    where += " and o.ordencompra.compromiso.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getProveedor());
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbCabecerainventario.contar(parametros);
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
                cabeceras.setRowCount(total);
                try {
                    return ejbCabecerainventario.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo() {
        cabecera = new Cabecerainventario();
        formulario.insertar();
        cabecera.setEstado(0); // digitada
        listaKardex = new LinkedList<>();
        listaKardexb = new LinkedList<>();
        proveedoresBean.setRuc(null);
        proveedoresBean.setProveedor(null);
        descuentoExterno = 0;
        solicitud = new Solicitudsuministros();
        return null;
    }

    public String salir() {
        cabecera = null;
        formulario.cancelar();
        listaKardex = new LinkedList<>();
        listaKardexb = new LinkedList<>();
        proveedoresBean.setRuc(null);
        proveedoresBean.setProveedor(null);
        return null;
    }

    public String modifica() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("INVENTARIOSUM");
        imagenesBean.Buscar();
        descuentoExterno = 0;
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        if (cabecera.getProveedor() != null) {
//            proveedoresBean.setProveedor(cabecera.getOrdencompra().getCompromiso().getProveedor());
//            proveedoresBean.setRuc(cabecera.getOrdencompra().getCompromiso().getProveedor().getEmpresa().getRuc());
//        }
        solicitud = cabecera.getSolicitud();
        if (solicitud == null) {
            solicitud = new Solicitudsuministros();
        }
        listaKardexb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String imprimir() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", cabecera.getTxid().getNombre() + " No : " + cabecera.getId());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", cabecera.getFecha());
            parametros.put("documento", cabecera.getNumero());
            parametros.put("modulo", cabecera.getBodega().getNombre());
            parametros.put("descripcion", cabecera.getObservaciones());
            parametros.put("transaccion", cabecera.getTxid().getNombre());
            parametros.put("proveedor", (cabecera.getProveedor() == null ? null : cabecera.getProveedor().getEmpresa().toString()));
            parametros.put("contrato", (cabecera.getContrato() == null ? null : cabecera.getContrato().toString()));
            Calendar c = Calendar.getInstance();
            for (Kardexinventario k : listaKardex) {
                if ((k.getCostopromedio() == null) || (k.getCostopromedio() == 0)) {
                    k.setCostopromedio(k.getCosto());
                }
            }
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Inventario.jasper"),
                    listaKardex, "Inventario" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public String elimina() {

        cabecera = (Cabecerainventario) cabeceras.getRowData();
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        // taer la foto
        Map parametros = new HashMap();

        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((cabecera.getObservaciones() == null) || (cabecera.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es Obligatorio es obligatorio");
            return true;
        }
        if (cabecera.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesaria");
            return true;
        }

        String vale = ejbCabeceras.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return true;
        }

        if (cabecera.getBodega() == null) {
            MensajesErrores.advertencia("Bodega es necesaria");
            return true;
        }
        if (cabecera.getTxid() == null) {
            MensajesErrores.advertencia("Necesaria Transacción");
            return true;
        }
        if (transferencia) {
            if (cabecera.getTxid().getTransaferencia() == null) {
                MensajesErrores.advertencia("Transacción no definida para transferencia");
                return true;
            }
            if (cabecera.getTxid().getIngreso() == null) {
                MensajesErrores.advertencia("Transacción no definida para transferencia es un ingreso");
                return true;
            }
            if (bodegaDestino == null) {
                MensajesErrores.advertencia("Necesaria bodega de destino en transferencia");
                return true;
            }
            if (cabecera.getBodega().equals(bodegaDestino)) {
                MensajesErrores.advertencia("Necesaria bodega de destino en transferencia ya que es la misma de origen");
                return true;
            }
        }
        if (cabecera.getTxid().getProveedor()) {
            if (proveedoresBean.getProveedor() == null) {
                MensajesErrores.advertencia("Necesario proveedor en esta transacción");
                return true;
            }
        }

        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return true;
        }
        return false;
    }

    private void ceraActualizaSolicitud() {
        //
        try {
            if (!cabecera.getTxid().getIngreso()) {
                if (empleadosBean.getEmpleadoSeleccionado() != null) {
//                    if (solicitud.getOficina() != null) {
                    if (cabecera.getSolicitud() == null) {
//                        craer la solicitud
                        solicitud.setEmpleadosolicita(empleadosBean.getEmpleadoSeleccionado());
                        solicitud.setEmpleadodespacho(seguridadbean.getLogueado().getEmpleados());
                        solicitud.setFecha(cabecera.getFecha());
                        solicitud.setObservaciones("Solictud de suministros : " + cabecera.getObservaciones());
                        solicitud.setDespacho(cabecera.getFecha());
                        ejbSolSum.create(solicitud, getSeguridadbean().getLogueado().getUserid());
                        // detalle de la solictud
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(cabecera.getFecha());
                        int anio = cal.get(Calendar.YEAR);
                        for (Kardexinventario k : listaKardex) {
                            Detallesolicitud ds = new Detallesolicitud();
                            ds.setCantidad(k.getCantidad());
                            ds.setCantidadinvercion(k.getCantidadinversion());
                            ds.setPedido(k.getCantidad());
                            ds.setRecibido(k.getCantidad());
                            ds.setSolicitudsuministro(solicitud);
//                            Map parametros = new HashMap();
//                            parametros.put(";where", "o.oficina=:oficina and o.anio=:anio and o.suministro=:suministro");
//                            parametros.put("suministro", k.getSuministro());
////                                parametros.put("oficina", solicitud.getOficina());
//                            parametros.put("anio", anio);
//                            List<Organigramasuministros> ls = ejbOrgaSum.encontarParametros(parametros);
//                            Organigramasuministros o = null;
//                            for (Organigramasuministros os : ls) {
//                                o = os;
//                            }
//                            if (o == null) {
//                                o = new Organigramasuministros();
//                                o.setAnio(anio);
//                                o.setAprobado(k.getCantidad());
//                                o.setCantidad(k.getCantidad());
//                                o.setExplicacion("Creación de Solicitud por Organigrama : " + cabecera.getObservaciones());
//                                o.setEmpleado(empleadosBean.getEmpleadoSeleccionado());
////                                    o.setOficina(solicitud.getOficina());
//                                o.setSuministro(k.getSuministro());
//                                ejbOrgaSum.create(o, getSeguridadbean().getLogueado().getUserid());
//                            } else {
//                                o.setAprobado(o.getAprobado() + k.getCantidad());
//                                o.setCantidad(o.getCantidad() + k.getCantidad());
//                                ejbOrgaSum.edit(o, getSeguridadbean().getLogueado().getUserid());
//                            } // fin if o
//                                ds.setOrgsum(o);
                            ejbDetSol.create(ds, getSeguridadbean().getLogueado().getUserid());
                        }
                    } else {//fin if cabecera de solicitud en null
                        // actualiza
                        // busco el detalle y es jodido hay que borrar luego
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.solicitud:solicitud");
                        parametros.put("solicitud", solicitud);
                        List<Detallesolicitud> listaDetalleSolicitud = ejbDetSol.encontarParametros(parametros);
                        // todo el proceso annterior
                        solicitud.setEmpleadosolicita(empleadosBean.getEmpleadoSeleccionado());
                        solicitud.setEmpleadodespacho(seguridadbean.getLogueado().getEmpleados());
                        solicitud.setFecha(cabecera.getFecha());
                        solicitud.setObservaciones("Solictud de suministros : " + cabecera.getObservaciones());
                        solicitud.setDespacho(cabecera.getFecha());
                        ejbSolSum.edit(solicitud, getSeguridadbean().getLogueado().getUserid());
                        // detalle de la solictud
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(cabecera.getFecha());
                        int anio = cal.get(Calendar.YEAR);
                        for (Kardexinventario k : listaKardex) {
                            Detallesolicitud ds = new Detallesolicitud();
                            ds.setCantidad(k.getCantidad());
                            ds.setCantidadinvercion(k.getCantidadinversion());
                            ds.setAprobado(k.getCantidad());
                            ds.setAprobadoinversion(k.getCantidadinversion());
                            ds.setPedido(k.getCantidad());
                            ds.setRecibido(k.getCantidad());
                            ds.setSolicitudsuministro(solicitud);
                            ds.setSuministro(k.getSuministro());
                            ejbDetSol.create(ds, getSeguridadbean().getLogueado().getUserid());
                        }
                        for (Detallesolicitud d : listaDetalleSolicitud) {
                            ejbDetSol.remove(d, getSeguridadbean().getLogueado().getUserid());
                        }
                    } // fin else
//                    }
                }
            }
        } catch (InsertarException | GrabarException | ConsultarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public String insertar() {
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }

        if (validar()) {
            return null;
        }

        try {
            // crear solicitud
            cabecera.setEstado(0);
            ceraActualizaSolicitud();
            if (solicitud.getId() != null) {
                cabecera.setSolicitud(solicitud);
                cabecera.setEstado(1);
            }
            cabecera.setFechadigitacion(new Date());

//            Calcular el numero
            if (cabecera.getTxid().getProveedor()) {
//                ************************************************ OJOJOJOJOJOJJO
                cabecera.setProveedor(proveedoresBean.getProveedor());
//                cabecera.setContrato(null);
            } else {
                cabecera.setProveedor(null);
                cabecera.setContrato(null);
            }
            Txinventarios t = cabecera.getTxid();
            int numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
            t.setUltimo(numeroTx);
            cabecera.setNumero(numeroTx);
            ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
            // crear la solicitud

            ejbCabecerainventario.create(cabecera, getSeguridadbean().getLogueado().getUserid());
            for (Kardexinventario k : listaKardex) {
                if (cabecera.getTxid().getIngreso()) {
                    // ver descuento global
                    if (descuentoExterno > 0) {
                        // aplca a cada linea
                        double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                        k.setCosto(new Float(valor));
                    }
                    k.setSigno(1);
                } else {
                    k.setSigno(-1);
                }
                k.setCabecerainventario(cabecera);
                k.setBodega(cabecera.getBodega());
//                k.setFecha(cabecera.getFecha());
                ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());
            }
            if (transferencia) {
                t = cabecera.getTxid().getTransaferencia();
                numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
                t.setUltimo(numeroTx);
                ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
                Cabecerainventario c = new Cabecerainventario();
                c.setBodega(bodegaDestino);
                c.setCabecera(cabecera);
//                c.setContrato(cabecera.getContrato());
                c.setEstado(cabecera.getEstado());
                c.setFecha(cabecera.getFecha());
                c.setFechadigitacion(cabecera.getFechadigitacion());
                c.setObligacion(cabecera.getObligacion());
                c.setObservaciones(cabecera.getObservaciones());
//                c.setProveedor(cabecera.getProveedor());
                c.setTipo(cabecera.getTipo());
                c.setTxid(t);
                c.setNumero(numeroTx);
                ejbCabecerainventario.create(c, getSeguridadbean().getLogueado().getUserid());
                for (Kardexinventario k : listaKardex) {
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setCabecerainventario(c);
                    k1.setBodega(bodegaDestino);
                    k1.setCantidad(k.getCantidad());
                    k1.setCantidadinversion(k.getCantidadinversion());
                    k1.setSigno(1);
                    k1.setCosto(k.getCosto());
                    k1.setCostopromedio(k.getCostopromedio());
                    k1.setCostopinversion(k.getCostopinversion());
                    k1.setSaldoanterior(k.getSaldoanterior());
                    k1.setSuministro(k.getSuministro());
                    k1.setUnidad(k.getUnidad());
                    ejbKardex.create(k1, getSeguridadbean().getLogueado().getUserid());
                }
                cabecera.setCabecera(c);
                ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());

            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        buscar();
        return null;
    }

    public String grabar() {
        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return null;
        }
        try {
            if (cabecera.getTxid().getProveedor()) {
//                *********************************Ojo Orden de compra
//                cabecera.setProveedor(proveedoresBean.getProveedor());
//                cabecera.setContrato(null);
            } else {
//                cabecera.setProveedor(null);
//                cabecera.setContrato(null);
            }
            ceraActualizaSolicitud();
            if (solicitud.getId() != null) {
                cabecera.setSolicitud(solicitud);
                cabecera.setEstado(1);
            }

            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            // actualizar kardex i borrar los borrados
            for (Kardexinventario k : listaKardex) {
                k.setCabecerainventario(cabecera);
                k.setBodega(cabecera.getBodega());
                int signo = -1;
                if (cabecera.getTxid().getIngreso()) {
                    // ver descuento global
                    if (descuentoExterno > 0) {
                        // aplca a cada linea
                        double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                        k.setCosto(new Float(valor));
                    }
                    signo = 1;
                }
                if (k.getId() == null) {
                    k.setSigno(signo);
                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());
                    if (transferencia) {
                        Kardexinventario k1 = new Kardexinventario();
                        k1.setCabecerainventario(cabecera.getCabecera());
                        k1.setBodega(cabecera.getCabecera().getBodega());
                        k1.setCantidad(k.getCantidad());
                        k1.setCantidadinversion(k.getCantidadinversion());
                        k1.setCosto(k.getCosto());
                        k1.setSigno(signo * -1);
                        k1.setCostopromedio(k.getCostopromedio());
                        k1.setCostopinversion(k.getCostopinversion());
                        k1.setSaldoanterior(k.getSaldoanterior());
                        k1.setSuministro(k.getSuministro());
                        k1.setUnidad(k.getUnidad());
                        ejbKardex.create(k1, getSeguridadbean().getLogueado().getUserid());
                    }
                } else {
                    k.setSigno(signo);

                    ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());
                    // mas dificil ya que hay que ver en cardex i es transferencia
                    if (transferencia) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.cabecera=:cabecera and o.suministro=:suministro");
                        parametros.put("cabecera", k.getCabecerainventario());
                        parametros.put("suministro", k.getSuministro());
                        List<Kardexinventario> ListaCambiar = ejbKardex.encontarParametros(parametros);
                        for (Kardexinventario k1 : ListaCambiar) {
                            k1.setCantidadinversion(k.getCantidadinversion());
                            k1.setCantidad(k.getCantidad());
                            k1.setCosto(k.getCosto());
                            k1.setSigno(signo * -1);
                            k1.setCostopromedio(k.getCostopromedio());
                            k1.setCostopinversion(k.getCostopinversion());
                            k1.setSaldoanterior(k.getSaldoanterior());
                            k1.setSuministro(k.getSuministro());
                            k1.setUnidad(k.getUnidad());
                            ejbKardex.edit(k1, getSeguridadbean().getLogueado().getUserid());
                        }
                    }// fin if transferencia
                } // fin else si existe 
            }// fin for kardex
            for (Kardexinventario k : listaKardexb) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia
                    if (transferencia) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.cabecera=:cabecera and o.suministro=:suministro");
                        parametros.put("cabecera", k.getCabecerainventario());
                        parametros.put("suministro", k.getSuministro());
                        List<Kardexinventario> ListaCambiar = ejbKardex.encontarParametros(parametros);
                        for (Kardexinventario k1 : ListaCambiar) {
                            ejbKardex.remove(k1, getSeguridadbean().getLogueado().getUserid());
                        }
                    }// fin if transferencia
                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados
            if (transferencia) {

                Cabecerainventario c = cabecera.getCabecera();
                c.setCabecera(cabecera);
//                c.setContrato(cabecera.getContrato());
                c.setEstado(cabecera.getEstado());
                c.setFecha(cabecera.getFecha());
                c.setFechadigitacion(cabecera.getFechadigitacion());
                c.setObligacion(cabecera.getObligacion());
                c.setObservaciones(cabecera.getObservaciones());
//                c.setProveedor(cabecera.getProveedor());
                c.setTipo(cabecera.getTipo());
                ejbCabecerainventario.edit(c, getSeguridadbean().getLogueado().getUserid());
                for (Kardexinventario k : listaKardex) {
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setCabecerainventario(c);
                    k1.setBodega(bodegaDestino);
                    k1.setCantidad(k.getCantidad());
                    k1.setCantidadinversion(k.getCantidadinversion());
                    k1.setCosto(k.getCosto());
                    k1.setCostopromedio(k.getCostopromedio());
                    k1.setCostopinversion(k.getCostopinversion());
                    k1.setSaldoanterior(k.getSaldoanterior());
                    k1.setSuministro(k.getSuministro());
                    k1.setUnidad(k.getUnidad());
                    ejbKardex.create(k1, getSeguridadbean().getLogueado().getUserid());
                }
                cabecera.setCabecera(c);
                ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());

            }

        } catch (GrabarException | InsertarException | BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            cabecera.setEstado(-1);
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
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

    public Cabecerainventario traer(Integer id) throws ConsultarException {
        return ejbCabecerainventario.find(id);
    }

    /**
     * @return the tipo
     */
    public Txinventarios getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Txinventarios tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the formularioRenglones
     */
    public Formulario getFormularioRenglones() {
        return formularioRenglones;
    }

    /**
     * @param formularioRenglones the formularioRenglones to set
     */
    public void setFormularioRenglones(Formulario formularioRenglones) {
        this.formularioRenglones = formularioRenglones;
    }

    /**
     * @return the listaKardex
     */
    public List<Kardexinventario> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Kardexinventario> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the kardex
     */
    public Kardexinventario getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexinventario kardex) {
        this.kardex = kardex;
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
     * @return the bodega
     */
    public Bodegas getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
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
     * @return the bodegaDestino
     */
    public Bodegas getBodegaDestino() {
        return bodegaDestino;
    }

    /**
     * @param bodegaDestino the bodegaDestino to set
     */
    public void setBodegaDestino(Bodegas bodegaDestino) {
        this.bodegaDestino = bodegaDestino;
    }

    /**
     * @return the transferencia
     */
    public boolean isTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the transferencia to set
     */
    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    // ver si le dejo cambiar si no ha entregado el producto
    public String nuevoKardex() {
        if (cabecera.getTxid() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de movimiento antes de ingresar productos");
            return null;
        }
        if (cabecera.getBodega() == null) {
            MensajesErrores.advertencia("Seleccione una bodega antes de ingresar productos");
            return null;
        }
        if (cabecera.getTxid().getIngreso()) {
            if (transferencia) {
                MensajesErrores.advertencia("No se pueden realizar transferencias de transacciones de ingreso");
                return null;
            }
        }
        descuentoInterno = 0;
        kardex = new Kardexinventario();
        kardex.setCantidad(new Float(0));
        kardex.setCantidadinversion(new Float(0));
        formularioRenglones.insertar();
        return null;
    }

    public String modificaKardex(Kardexinventario kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());
        // ver si puede cambiar si no hay asociado un dato
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardex=:kardex");
        parametros.put("kardex", kardex);
        try {
            int total = ejbAdKardex.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible modificar pues ya tiene registros de entrega");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        descuentoInterno = 0;
        formularioRenglones.editar();
        return null;
    }

    public String borraKardex(Kardexinventario kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());
        // ver si puede cambiar si no hay asociado un dato
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardex=:kardex");
        parametros.put("kardex", kardex);
        try {
            int total = ejbAdKardex.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar pues ya tiene registros de entrega");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRenglones.eliminar();
        return null;
    }

    private boolean validarRenglon() {
        if ((kardex.getCantidad() == null) || (kardex.getCantidad() <= 0)) {
            if ((kardex.getCantidadinversion() == null) || (kardex.getCantidadinversion() <= 0)) {
                MensajesErrores.advertencia("Ingrese una cantidad válida");
                return true;
            }
        }
        if ((kardex.getSuministro() == null)) {
            MensajesErrores.advertencia("Ingrese un Suministro");
            return true;
        }
        if ((kardex.getUnidad() == null)) {
            MensajesErrores.advertencia("Ingrese una udidad del Suministro");
            return true;
        }
        if (cabecera.getTxid().getIngreso()) {
            if ((kardex.getCosto() == null) || (kardex.getCosto() <= 0)) {
                MensajesErrores.advertencia("Ingrese una costo para ingreso");
                return true;
            }
            if (descuentoInterno > 0) {
                double valor = kardex.getCosto().doubleValue() * (1 - descuentoInterno / 100);
                kardex.setCosto(new Float(valor));
            }
        } else {
            double cantidad = kardex.getCantidad() == null ? 0 : kardex.getCantidad();
            double cantidadInversion = kardex.getCantidadinversion() == null ? 0 : kardex.getCantidadinversion();
            cantidad = getSaldo() - cantidad;
            cantidadInversion = getSaldoInversion() - cantidadInversion;
            if (cantidad < 0) {
                MensajesErrores.advertencia("Cantidad sobrepasa el saldo de bodega");
                return true;
            }
            if (cantidadInversion < 0) {
                MensajesErrores.advertencia("Cantidad inversión sobrepasa el saldo de bodega");
                return true;
            }
        }
        // 
        return false;
    }

    private void estaSuministro(Kardexinventario k) {
        for (Kardexinventario k1 : listaKardex) {
            if (k1.getSuministro().equals(k.getSuministro())) {
                // suma la cantidad conserva el costo inicial o pone el promedio
                k1.setCantidad(k1.getCantidad() + k.getCantidad());
                return;
            }
        }
        listaKardex.add(k);
    }

    public String insertarRenglon() {
        if (validarRenglon()) {
            return null;
        }
        estaSuministro(kardex);
        formularioRenglones.cancelar();
        return null;
    }

    public String grabarRenglon() {
        if (validarRenglon()) {
            return null;
        }
        listaKardex.set(formularioRenglones.getIndiceFila(), kardex);
        formularioRenglones.cancelar();
        return null;
    }

    public String borrarRenglon() {

        listaKardex.remove(formularioRenglones.getIndiceFila());
        formularioRenglones.cancelar();
        return null;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
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

    public SelectItem[] getUnidadesSuministro() {
        if (kardex == null) {
            return null;
        }
        if (kardex.getSuministro() == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();

            Map parametros = new HashMap();
//            parametros.put(";orden", "o.base=:base");
            parametros.put(";where", "o.base=:base");
            parametros.put("base", kardex.getSuministro().getUnidad());
            List<Unidades> ls = ejbUnidades.encontarParametros(parametros);
            List<Unidades> retorno = new LinkedList<>();
            retorno.add(kardex.getSuministro().getUnidad());
            for (Unidades u : ls) {
                retorno.add(u);
            }
            return Combos.getSelectItems(retorno, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public boolean getMostrarObligacion() {
        Cabecerainventario cab = (Cabecerainventario) cabeceras.getRowData();
        if (cab.getCabecera() != null) {
            return false;
        }
        if (cab.getEstado() <= 1) {
            if (cab.getTxid().getIngreso()) {
                if (cab.getTxid().getProveedor()) {
                    if (cab.getOrdencompra() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean getMostrarModificar() {
        Cabecerainventario cab = (Cabecerainventario) cabeceras.getRowData();

        if (cab.getEstado() < 1) {
            if (cab.getTxid().getIngreso()) {
                if (cab.getTxid().getProveedor()) {
                    if (cab.getOrdencompra() == null) {
                        return true;
                    }
                }
            } else {
                if (cab.getSolicitud() == null) {
                    return true;
                }
//                poner estado que pueda ver

            }

        }
        return false;
    }

    public String traerObligaciones() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
//        proveedoresBean.setEmpresa(cabecera.getProveedor().getEmpresa());
//        proveedoresBean.setProveedor(cabecera.getProveedor());
        getObligacionesBean().setEstado(2);
        getObligacionesBean().setCompromiso(cabecera.getOrdencompra().getCompromiso());
        obligacionesBean.buscar();
        formularioObligacion.insertar();
        return null;
    }

    public String seleccionarObligaciones(Obligaciones o) {
        cabecera.setObligacion(o);
        try {
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        proveedoresBean.setEmpresa(null);
        proveedoresBean.setProveedor(null);
        cabecera = null;
        formularioObligacion.cancelar();
        buscar();
        return null;
    }

    public String salirObligaciones() {

        proveedoresBean.setEmpresa(null);
        proveedoresBean.setProveedor(null);
        cabecera = null;
        formularioObligacion.cancelar();
        buscar();
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
    public ObligacionesCompromisoBean getObligacionesBean() {
        return obligacionesBean;
    }

    /**
     * @param obligacionesBean the obligacionesBean to set
     */
    public void setObligacionesBean(ObligacionesCompromisoBean obligacionesBean) {
        this.obligacionesBean = obligacionesBean;
    }

    /**
     * @return the descuentoInterno
     */
    public double getDescuentoInterno() {
        return descuentoInterno;
    }

    /**
     * @param descuentoInterno the descuentoInterno to set
     */
    public void setDescuentoInterno(double descuentoInterno) {
        this.descuentoInterno = descuentoInterno;
    }

    /**
     * @return the descuentoExterno
     */
    public double getDescuentoExterno() {
        return descuentoExterno;
    }

    /**
     * @param descuentoExterno the descuentoExterno to set
     */
    public void setDescuentoExterno(double descuentoExterno) {
        this.descuentoExterno = descuentoExterno;
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
     * @return the solicitud
     */
    public Solicitudsuministros getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(Solicitudsuministros solicitud) {
        this.solicitud = solicitud;
    }

    public double getCantidadTotal() {
        if (listaKardex == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexinventario k : listaKardex) {
            retorno += k.getCantidad();
        }
        return retorno;
    }

    public double getCostoTotal() {
        if (listaKardex == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexinventario k : listaKardex) {
            if (k.getCosto() == null) {
                retorno += k.getCosto();
            }
        }
        return retorno;
    }

    public double getTotal() {
        if (listaKardex == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexinventario k : listaKardex) {
            double cantidad = (k.getCantidad() == null ? 0 : k.getCantidad());
            double cantidadinversion = (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
            if (k.getCosto() == null) {
                retorno += (cantidad + cantidadinversion);
            } else {
                retorno += (cantidad + cantidadinversion) * k.getCosto();
            }
        }
        return retorno;
    }

    public double getSaldo() {
        double retorno = 0;
        try {
            if (kardex == null) {
                return 0;
            }
            if (kardex.getSuministro() == null) {
                return 0;
            }
            if (cabecera.getTxid() == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
//            if (cabecera.getTxid().getIngreso()) {
//                return 0;
//            }

            // ir a la bodega a ver el saldo
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            parametros = new HashMap();
            if (cabecera.getId() == null) {
                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 and o.cabecerainventario.txid.ingreso=false");
            } else {
                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 "
                        + "and o.cabecerainventario!=:cabecera and o.cabecerainventario.txid.ingreso=false");
                parametros.put("cabecera", cabecera);
            }
//            ojo ver los ingresos y egresos ***************************************+
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            parametros.put(";campo", "o.cantidad*o.signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            retorno = bs.getSaldo() - valorOtros;
//        for (Kardexinventario k : listaKardex) {
//                retorno += k.getCantidad();
//        }

        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getSaldoInversion() {
        double retorno = 0;
        try {
            if (kardex == null) {
                return 0;
            }
            if (kardex.getSuministro() == null) {
                return 0;
            }
            if (cabecera.getTxid() == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
            if (cabecera.getTxid().getIngreso()) {
                return 0;
            }

            // ir a la bodega a ver el saldo
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            parametros = new HashMap();
            if (cabecera.getId() == null) {
                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0");
            } else {
                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 and o.cabecerainventario!=:cabecera");
                parametros.put("cabecera", cabecera.getId());
            }
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            parametros.put(";campo", "o.cantidadinversion*signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            retorno = bs.getSaldo() - valorOtros;
//        for (Kardexinventario k : listaKardex) {
//                retorno += k.getCantidad();
//        }

        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String grabarDefinitivo() {

        String retorno = ejbCabecerainventario.grabarDefinitivo(cabecera, listaKardex);
        if (retorno != null) {
            MensajesErrores.fatal(retorno);
            return null;
        }
        formulario.cancelar();
        formularioImprimir.insertar();

        buscar();
        return null;
    }
//    public String grabarDefinitivo() {
//
//        try {
//
//            // actualizar kardex i borrar los borrados
//            List<Bodegasuministro> listaBxS = new LinkedList<>();
//            for (Kardexinventario k : listaKardex) {
//                // costear si es ingreso
//                if (k.getUnidad() != null) {
//                    if (k.getUnidad().getBase() != null) {
//                        k.setCantidad(k.getCantidad() * k.getUnidad().getFactor());
//                        k.setUnidad(k.getUnidad().getBase());
//                    }
//                }
//
//                // bsucar en Bodega producto
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
//                parametros.put("bodega", cabecera.getBodega());
//                parametros.put("suministro", k.getSuministro());
//                List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
//                Bodegasuministro bs = null;
//                for (Bodegasuministro b : listaBodegas) {
//                    bs = b;
//                }
//                if (bs == null) {
//                    if (!cabecera.getTxid().getIngreso()) {
//
//                        MensajesErrores.fatal("No existe saldo en bodega para poder emitir salida Suministro :" + k.getSuministro().getNombre());
//                        return null;
//                    }
//                    bs = new Bodegasuministro();
//                    bs.setBodega(cabecera.getBodega());
//                    bs.setSuministro(k.getSuministro());
//                    bs.setSaldo(new Float(0));
//                    bs.setSaldoinversion(new Float(0));
//                    bs.setCostopromedio(new Float(0));
//                    bs.setFecha(new Date());
//                    bs.setHora(new Date());
//                    bs.setUnidad(k.getSuministro().getUnidad());
//                }
////                solo si es egreso
//
//                k.setFecha(new Date());
//                k.setHora(new Date());
//                
//                if (cabecera.getTxid().getIngreso()) {
//                    // toca costear
//                    
//                } else {
//                    // desscuenta de inventario
//                    if (k.getCantidad() - bs.getSaldo() > 0) {
//                        if (!cabecera.getTxid().getIngreso()) {
//                            MensajesErrores.fatal("No existe stock en bodega para poder sacar : Suministro" + k.getSuministro().getNombre());
//                            return null;
//                        }
//                    }
////                    double saldoBodega = bs.getSaldo() - (k.getCantidad() == null ? 0 : k.getCantidad());
////                    double saldoInvBodega = bs.getSaldoinversion() - (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
////                    bs.setSaldo(new Float(saldoBodega));
////                    bs.setSaldoinversion(new Float(saldoInvBodega));
//                }
//                costoPromedior(k, bs);
//                k.setCabecerainventario(cabecera);
//                if (k.getId() == null) {
//                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());
//                } else {
//                    ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());
//                }
//                listaBxS.add(bs);
//            }
////            for (Kardexinventario k : listaKardex) {
////
////                if (cabecera.getTxid().getIngreso()) {
////                    // toca costear
////                } else {
////                    // desscuenta de inventario
////                }
////                
////                // mas dificil ya que hay que ver en cardex i es transferencia
////
////            }// fin for kardex
//            for (Bodegasuministro b : listaBxS) {
//                if (b.getId() == null) {
//                    ejbBodSum.create(b, getSeguridadbean().getLogueado().getUserid());
//                } else {
//                    ejbBodSum.edit(b, getSeguridadbean().getLogueado().getUserid());
//                }
//            }
//            cabecera.setEstado(1);
//            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
//        } catch (GrabarException | InsertarException | ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//        formulario.cancelar();
//        formularioImprimir.insertar();
//
//        buscar();
//        return null;
//    }

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

}
