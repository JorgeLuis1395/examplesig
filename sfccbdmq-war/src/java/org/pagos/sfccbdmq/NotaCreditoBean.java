/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ReciboIngresos;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerafacturasFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallefacturasFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.FirmadorFacade;
import org.beans.sfccbdmq.ProductosFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SucursalesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.DocumentosEmisionBean;
import org.contabilidad.sfccbdmq.PuntoEmisionBean;
import org.contabilidad.sfccbdmq.SucursalesBean;
import org.delectronicos.sfccbdmq.TipoComprobanteEnum;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabecerafacturas;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallefacturas;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Productos;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Sucursales;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "notaCreditoSfcbdmq")
@ViewScoped
public class NotaCreditoBean implements Serializable {

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

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadBean;
    @ManagedProperty(value = "#{clientesSfccbdmq}")
    private ClientesBean clientesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Formulario formularioProducto = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioCorreo = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Resource reporte;
    private List<Cabecerafacturas> listaFacturas;
    private List<Productos> productos;
    private List<Detallefacturas> listadoDetalle;
    private List<Renglones> listaReglonesInversion;
    private String clave;
    private String correo;
    private Cabecerafacturas factura;
    private Perfiles perfil;
    private Detallefacturas detalle;
    private Codigos tipoDocumento;
    private Puntoemision puntoEmision;
    private Documentos documento;
    private Cabecerafacturas cab;
    private String tipoFecha = "o.fecha";
    private Date desde;
    private Date hasta;
    private String cuantaCobra = "";
    private Sucursales sucursal;
    private Resource reporteRecibo;
    private Resource reporteFac;

    @EJB
    private CabecerafacturasFacade ejbCabecerafacturas;
    @EJB
    private DetallefacturasFacade ejbDetallefacturas;
    @EJB
    private ProductosFacade ejbProductos;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private PuntoemisionFacade ejbPuntoemision;
    @EJB
    private SucursalesFacade ejbSucursales;

    public NotaCreditoBean() {
    }

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String p = (String) params.get("p");
        String nombreForma = "FacturasVista";

        if (p == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadBean.cerraSession();
        }
        perfil = seguridadBean.traerPerfil((String) params.get("p"));
        if (this.perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadBean.cerraSession();
        }
//        if (nombreForma.contains(perfil.getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadBean.getGrupo().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadBean.cerraSession();
//            }
//        }
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        tipoDocumento = codigosBean.traerCodigo("DOCS", "NTCR");
    }

    public String buscar() {
        Map parametros = new HashMap();
        String where = " " + tipoFecha + " between :desde and :hasta and o.kardexbanco is null and o.denotacredito is null ";
//        if (clientesBean.getCliente().getId() == null) {
//            MensajesErrores.advertencia("Seleccione un Cliente");
//            return null;
//        }
        if (clientesBean.getCliente().getId() == null) {
        } else {
            where += " and o.cliente=:cliente ";
            parametros.put("cliente", clientesBean.getCliente());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";where", where);
        parametros.put(";orden", tipoFecha + " desc");
//        parametros.put(";orden", "o.fecha desc");
        try {
            listaFacturas = ejbCabecerafacturas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (clientesBean.getCliente().getId() == null) {
            MensajesErrores.advertencia("Seleccione un Cliente");
            return null;
        }
        factura = new Cabecerafacturas();
        factura.setCliente(clientesBean.getCliente());
        factura.setUsuario(seguridadBean.getLogueado().getEmpleados());
        factura.setFechacreacion(new Date());
        factura.setFecha(new Date());
        factura.setTipodocumento(tipoDocumento);
        listadoDetalle = new LinkedList<>();
        formulario.insertar();
        return null;
    }

    public String modificar(Cabecerafacturas factura) {
//        formulario.setIndiceFila(formulario.getFila().getRowIndex());
//        factura = listaFacturas.get(formulario.getIndiceFila());
        this.factura = factura;
//        factura.setTipodocumento(tipoDocumento);
        traerDetalles();
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", factura.getPuntoemision());
        List<Puntoemision> listaPunto;
        try {
            listaPunto = ejbPuntoemision.encontarParametros(parametros);
            if (!listaPunto.isEmpty()) {
                puntoEmision = listaPunto.get(0);
                sucursal = puntoEmision.getSucursal();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String contabilizar(Cabecerafacturas fac) {
//        factura = listaFacturas.get(formulario.getFila().getRowIndex());
        try {
            factura = fac;
            Map parametros = new HashMap();
            parametros.put(";where", "o.factura=:factura");
            parametros.put("factura", factura);
            if ((factura.getCliente().getCuentaingresos() == null) || (factura.getCliente().getCuentaingresos().isEmpty())) {
                MensajesErrores.advertencia("No existe cuenta de ingresos en cliente");
                return null;
            }
            listadoDetalle = ejbDetallefacturas.encontarParametros(parametros);
            if (listadoDetalle.isEmpty()) {
                MensajesErrores.advertencia("No se puede contabilizar no tiene detalle");
                return null;
            }
            List<Renglones> lista = getRenglones();
            if (getRenglones() == null) {
                MensajesErrores.advertencia("No hay nada para contabilizar");
                return null;
            }
            if (getRenglones().isEmpty()) {
                MensajesErrores.advertencia("No hay nada para contabilizar");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", factura.getPuntoemision());
            List<Puntoemision> listaPunto = ejbPuntoemision.encontarParametros(parametros);
            if (!listaPunto.isEmpty()) {
                puntoEmision = listaPunto.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        factura.setFechacontabilizacion(new Date());
        formularioContabilizar.editar();
        return null;
    }

    public String autorizar(Cabecerafacturas factura) {
//        factura = listaFacturas.get(formulario.getFila().getRowIndex());
        String respuesta = ejbCabecerafacturas.crearComprobante(factura);
        MensajesErrores.informacion(respuesta);
        return null;
    }

    public String eliminar(Cabecerafacturas factura) {
//        formulario.setIndiceFila(formulario.getFila().getRowIndex());
//        factura = listaFacturas.get(formulario.getIndiceFila());
        this.factura = factura;
        listadoDetalle = null;
        traerDetalles();
        formulario.eliminar();
        return null;
    }

    private void traerDetalles() {
        if (factura != null) {

            Map parametros = new HashMap();
            parametros.put(";where", "o.factura=:factura");
            parametros.put("factura", factura);
            try {
                listadoDetalle = ejbDetallefacturas.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
                Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean validar() {
        if (clientesBean.getCliente().getId() == null) {
            MensajesErrores.advertencia("Seleccione un Cliente");
            return true;
        }
        if (factura.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesario");
            return true;
        }
        if (puntoEmision == null) {
            MensajesErrores.advertencia("Punto de emisión es necesario");
            return true;
        }
        if (listadoDetalle == null) {
            MensajesErrores.advertencia("Sin productos");
            return true;
        }
        if (listadoDetalle.isEmpty()) {
            MensajesErrores.advertencia("Sin productos");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {

            factura.setPuntoemision(puntoEmision.getCodigo());
            factura.setSucursal(puntoEmision.getSucursal().getRuc());
            ejbCabecerafacturas.create(factura, seguridadBean.getLogueado().getUserid());
            for (Detallefacturas dv : listadoDetalle) {
                dv.setFactura(factura);
                ejbDetallefacturas.create(dv, seguridadBean.getLogueado().getUserid());
            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            factura.setPuntoemision(puntoEmision.getCodigo());
            factura.setSucursal(puntoEmision.getSucursal().getRuc());
            ejbCabecerafacturas.edit(factura, seguridadBean.getLogueado().getUserid());
            for (Detallefacturas dv : listadoDetalle) {
                if (dv.getId() == null) {
                    dv.setFactura(factura);
                    ejbDetallefacturas.create(dv, seguridadBean.getLogueado().getUserid());
                } else {
                    ejbDetallefacturas.edit(dv, seguridadBean.getLogueado().getUserid());
                }
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.factura=:factura");
            parametros.put("factura", factura);
            List<Detallefacturas> lista = ejbDetallefacturas.encontarParametros(parametros);
            if (!listadoDetalle.equals(lista)) {
                for (Detallefacturas dv : lista) {
                    ejbDetallefacturas.remove(dv, seguridadBean.getLogueado().getUserid());
                }
                for (Detallefacturas dv2 : listadoDetalle) {
                    dv2.setFactura(factura);
                    ejbDetallefacturas.create(dv2, seguridadBean.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | InsertarException | ConsultarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            List<Renglones> renglones = traerRenglones(factura);
            Cabeceras cab = null;
            Cabeceras cabRec = null;
            for (Renglones r : renglones) {
                if (r.getCabecera().getOpcion().contains("FACTURACION_RECLASIFICACION")) {
                    cabRec = r.getCabecera();
                } else {
                    cab = r.getCabecera();
                }
                ejbRenglones.remove(r, seguridadBean.getLogueado().getUserid());
            }
            if (cab != null) {
                ejbCabeceras.remove(cab, seguridadBean.getLogueado().getUserid());
            }
            if (cabRec != null) {
                ejbCabeceras.remove(cabRec, seguridadBean.getLogueado().getUserid());
            }
            for (Detallefacturas dv : listadoDetalle) {
                ejbDetallefacturas.remove(dv, seguridadBean.getLogueado().getUserid());
            }
            ejbCabecerafacturas.remove(factura, seguridadBean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public Cabecerafacturas traerc(Integer id) throws ConsultarException {
        return ejbCabecerafacturas.find(id);
    }

    public String insertarDetalle() {
        if (detalle.getCantidad() == null || detalle.getCantidad() < 0) {
            MensajesErrores.advertencia("Cantidad es necesaria y debe ser positiva");
            return null;
        }
        detalle.setImpuesto(detalle.getProducto().getImpuesto());
        for (Detallefacturas dv : listadoDetalle) {

            if (dv.getProducto().equals(detalle.getProducto())) {
                dv.setCantidad(dv.getCantidad() + detalle.getCantidad());
                if (detalle.getDescuento() != null) {
                    dv.setDescuento(detalle.getDescuento());
                }
                detalle = new Detallefacturas();
                clave = "";
                return null;
            }
        }

        listadoDetalle.add(detalle);
        detalle = new Detallefacturas();
        clave = "";

        return null;
    }

    public String eliminarDetalle(Detallefacturas d) {

        try {
            ejbDetallefacturas.remove(d, seguridadBean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void productoNombreChangeEventHandler(TextChangeEvent event) {
        detalle = new Detallefacturas();
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = "o.id is not null";
        where += " and upper(o.nombre) like :clave";
        parametros.put("clave", "%" + codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.nombre");

        productos = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        try {
            productos = ejbProductos.encontarParametros(parametros);
//            detalle = new Detallefacturas();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }

    public void cambiaProducto(ValueChangeEvent event) {
        if (productos == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Productos u : productos) {
            String aComparar = u.getNombre();
            if (aComparar.compareToIgnoreCase(newWord) == 0) {
                detalle = new Detallefacturas();
                detalle.setDescuento(BigDecimal.ZERO);
                detalle.setProducto(u);
                detalle.setValorimpuesto(u.getImpuesto().getPorcentaje());
                detalle.setImpuesto(u.getImpuesto());
                detalle.setValor(u.getPreciounitario());
            }
        }
    }

    public Double getDescuento(Detallefacturas dv) {
        double totaDecs = 0.00;
        if (dv.getDescuento().doubleValue() > 0) {
            totaDecs = dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue() * (dv.getDescuento().doubleValue() / 100);
        }
        return totaDecs;
    }

    public Double getTotal(Detallefacturas dv) {
        double total = 0;
        if (dv.getDescuento().doubleValue() > 0) {
            total = (dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue()
                    - (dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue() * (dv.getDescuento().doubleValue() / 100)));
        } else {
            total = dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue();
        }
        return total;
    }

    public Double getTotalIva(Detallefacturas dv) {
        double totalIva = 0;
        if (dv.getDescuento().doubleValue() > 0) {
            totalIva = (dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue()
                    - (dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue() * (dv.getDescuento().doubleValue() / 100)))
                    * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);
        } else {
            totalIva = dv.getCantidad().doubleValue() * dv.getProducto().getPreciounitario().doubleValue()
                    * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);
        }
        return totalIva;
    }

    public Double getSumaDesc() {
        return ejbCabecerafacturas.traerTotalDescuento(listadoDetalle);
    }

    public Double getSumaIva() {
        return ejbCabecerafacturas.traerTotalIva(listadoDetalle);
    }

    public Double getSuma() {
        return ejbCabecerafacturas.traerTotal(listadoDetalle);
    }

    public Documentos traerDocumetos() {
        if (factura.getPuntoemision() == null) {
            return null;
        }
        Codigos fac = codigosBean.traerCodigo("DOCS", "FACT");
        Map parametros = new HashMap();
        parametros.put(";where", "o.punto.codigo=:punto  and o.punto.sucursal.ruc =:sucursal and o.documento=:documento");
        parametros.put("punto", factura.getPuntoemision());
        parametros.put("sucursal", factura.getSucursal());
        parametros.put("documento", fac);
        try {
            List<Documentos> ld = ejbDocumentos.encontarParametros(parametros);
            for (Documentos d : ld) {
                return d;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the seguridadBean
     */
    public ParametrosSfccbdmqBean getSeguridadBean() {
        return seguridadBean;
    }

    /**
     * @param seguridadBean the seguridadBean to set
     */
    public void setSeguridadBean(ParametrosSfccbdmqBean seguridadBean) {
        this.seguridadBean = seguridadBean;
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
     * @return the listaFacturas
     */
    public List<Cabecerafacturas> getListaFacturas() {
        return listaFacturas;
    }

    /**
     * @param listaFacturas the listaFacturas to set
     */
    public void setListaFacturas(List<Cabecerafacturas> listaFacturas) {
        this.listaFacturas = listaFacturas;
    }

    /**
     * @return the productos
     */
    public List<Productos> getProductos() {
        return productos;
    }

    /**
     * @param productos the productos to set
     */
    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the factura
     */
    public Cabecerafacturas getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Cabecerafacturas factura) {
        this.factura = factura;
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
     * @return the detalle
     */
    public Detallefacturas getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallefacturas detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the clientesBean
     */
    public ClientesBean getClientesBean() {
        return clientesBean;
    }

    /**
     * @param clientesBean the clientesBean to set
     */
    public void setClientesBean(ClientesBean clientesBean) {
        this.clientesBean = clientesBean;
    }

    /**
     * @return the tipoDocumento
     */
    public Codigos getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento the tipoDocumento to set
     */
    public void setTipoDocumento(Codigos tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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
     * @return the puntoEmision
     */
    public Puntoemision getPuntoEmision() {
        return puntoEmision;
    }

    /**
     * @param puntoEmision the puntoEmision to set
     */
    public void setPuntoEmision(Puntoemision puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    private Asignaciones traer(String c, int anio) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador.codigo=:partida and o.proyecto.anio=:anio");
        parametros.put("anio", anio);
        parametros.put("partida", c);

        try {
            List<Asignaciones> listaAsig = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones a : listaAsig) {
                return a;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Renglones> getRenglones() {
        List<Renglones> retorno = new LinkedList<>();
        if (factura == null) {
            return null;
        }
        if (listadoDetalle == null) {
            return null;
        }
        if (listadoDetalle.isEmpty()) {
            return null;
        }
        // cuenta por cobrar viene del cliente
        Renglones r = new Renglones();

        r.setCuenta(factura.getCliente().getCuentaingresos());
        Cuentas cuenta = cuentasBean.traerCodigo(factura.getCliente().getCuentaingresos());
        if (cuenta == null) {
            return null;
        }
        if (cuenta.getAuxiliares() != null) {
            r.setAuxiliar(factura.getCliente().getEmpresa().getRuc());
        }
        double valor = 0;
        //Impuesto Cobra
        double valorImpuesto2 = 0;

        listaReglonesInversion = new LinkedList<>();
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(factura.getFecha());
        int anio = calendario.get(Calendar.YEAR);
        for (Detallefacturas d : listadoDetalle) {
            Renglones r1 = new Renglones();

            Cuentas cuentaRenglon = cuentasBean.traerCodigo(d.getProducto().getCategoria());
            if (cuentaRenglon.getAuxiliares() != null) {
                r1.setAuxiliar(factura.getCliente().getEmpresa().getRuc());
            }
            if (cuentaRenglon.getPresupuesto() == null) {
                MensajesErrores.advertencia("No existe información de clasificador para cuenta : " + cuentaRenglon.getCodigo());
                return null;
            }
            // inicio impuesto

            double valorProducto = d.getCantidad().doubleValue() * d.getValor().doubleValue();
            double valorDescuento = valorProducto * (d.getDescuento().doubleValue() / 100);
            double valorImpuesto = (valorProducto - valorDescuento) * (d.getValorimpuesto().doubleValue() / 100);
//            double valorLinea = valorProducto + valorImpuesto;
            double valorLinea = valorProducto;
            if (d.getValorimpuesto().doubleValue() != 0) {
                Cuentas cuentaImpuesto = cuentasBean.traerCodigo(d.getImpuesto().getCuentaventas());
                r1.setValor(new BigDecimal(valorProducto * -1));
                valor += valorLinea;
                valorImpuesto2 += valorImpuesto;
                r1.setCuenta(cuentaRenglon.getCodigo());
                r1.setSigno(-1);
                estaEnRas(r1, retorno);
                inversiones(cuenta, r1, 1);
                // Armar renglond e impuestos
                Renglones rImpuesto = new Renglones();
                if (cuentaImpuesto.getAuxiliares() != null) {
                    rImpuesto.setAuxiliar(factura.getCliente().getEmpresa().getRuc());
                }
                rImpuesto.setValor(new BigDecimal(valorImpuesto * -1));
                rImpuesto.setCuenta(cuentaImpuesto.getCodigo());
                rImpuesto.setSigno(-1);
                estaEnRas(rImpuesto, retorno);
                inversiones(cuentaImpuesto, rImpuesto, 1);
                cuantaCobra = d.getImpuesto().getCuentacompras();
            } else {
                valor = valorLinea;
                r1.setValor(new BigDecimal(valorProducto * -1));
                r1.setCuenta(cuentaRenglon.getCodigo());
                r1.setSigno(-1);
                estaEnRas(r1, retorno);
                inversiones(cuenta, r1, 1);
            }
            // fin impuesto
            // ver la asignación inicial
            Asignaciones a = traer(cuentaRenglon.getPresupuesto(), anio);
            if (a == null) {
                MensajesErrores.advertencia("No existe información de asignación para cuenta : "
                        + cuentaRenglon.getCodigo() + " " + cuentaRenglon.getPresupuesto());
                return null;
            }
            d.setAsignacion(a);
        }

        // inicio impuesto cobra
        if (!(cuantaCobra == null || cuantaCobra.isEmpty())) {
            Cuentas cuentaImpuesto2 = cuentasBean.traerCodigo(cuantaCobra);
            // Armar renglond e impuestos
            Renglones rImpuesto2 = new Renglones();
            if (cuentaImpuesto2.getAuxiliares() != null) {
                rImpuesto2.setAuxiliar(factura.getCliente().getEmpresa().getRuc());
            }
            rImpuesto2.setValor(new BigDecimal(valorImpuesto2));
            rImpuesto2.setCuenta(cuentaImpuesto2.getCodigo());
            rImpuesto2.setSigno(-1);
            estaEnRas(rImpuesto2, retorno);
            inversiones(cuentaImpuesto2, rImpuesto2, 1);
        }
        // fin impuesto
        r.setValor(new BigDecimal(valor));
        r.setSigno(-1);
        estaEnRas(r, retorno);
        inversiones(cuenta, r, 1);
        return retorno;
    }

    private boolean estaEnRas(Renglones r1, List<Renglones> listaReglones) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }

        for (Renglones r : listaReglones) {
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        listaReglones.add(r1);
        return false;
    }

    private void inversiones(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
//                rasInvInt.setDetallecompromiso(ras.getDetallecompromiso());
            }
            rasInvInt.setFecha(new Date());
            estaEnRasInversiones(rasInvInt);
            Renglones rasContrapate = new Renglones();
            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasContrapate.setFecha(new Date());
            estaEnRasInversiones(rasContrapate);

        }
    }

    private boolean estaEnRasInversiones(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
//        if (r1.getDetallecompromiso() == null) {
//            r1.setDetallecompromiso(new Detallecompromiso());
//        }
        for (Renglones r : getListaReglonesInversion()) {
//            if (r.getDetallecompromiso() == null) {
//                r.setDetallecompromiso(new Detallecompromiso());
//            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }

            if ((r.getCuenta().equals(r1.getCuenta()))
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        getListaReglonesInversion().add(r1);
        return false;
    }

    /**
     * @return the listaReglonesInversion
     */
    public List<Renglones> getListaReglonesInversion() {
        return listaReglonesInversion;
    }

    /**
     * @param listaReglonesInversion the listaReglonesInversion to set
     */
    public void setListaReglonesInversion(List<Renglones> listaReglonesInversion) {
        this.listaReglonesInversion = listaReglonesInversion;
    }

    /**
     * @return the formularioContabilizar
     */
    public Formulario getFormularioContabilizar() {
        return formularioContabilizar;
    }

    /**
     * @param formularioContabilizar the formularioContabilizar to set
     */
    public void setFormularioContabilizar(Formulario formularioContabilizar) {
        this.formularioContabilizar = formularioContabilizar;
    }

    public String cancelaContabilizar() {
        formularioContabilizar.cancelar();
        buscar();
        return null;
    }

    public String grabarContabilizar() {
        if ((factura.getObservaciones() == null) || (factura.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("No existe referencia para contabilizar");
            return null;
        }
        if (documento == null) {
            MensajesErrores.advertencia("No existen autorizaciones para emitir este documento");
            return null;
        }
        if (factura.getNrodocumento() == null) {
            MensajesErrores.advertencia("Necesita número de documento");
            return null;
        }
        /// Colocar el presupuesto
        List<Renglones> lista = getRenglones();
        if (lista.isEmpty()) {
            MensajesErrores.advertencia("No existe nada para contabilizar");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.modulo=:modulo");
        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
        Cabeceras cas = new Cabeceras();
        Tipoasiento tipo = null;
        List<Tipoasiento> listaTipo;
        try {
//            Documentos documento = traerDocumetos();
            // buscar que el número no este ocumado

            listaTipo = ejbTipoAsiento.encontarParametros(parametros);

            for (Tipoasiento t : listaTipo) {
                tipo = t;
            }
            if (tipo == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para este módulo");
                return null;
            }

            Cabecerafacturas nuevaFactura = new Cabecerafacturas();
            if (factura.getTipodocumento().getCodigo().equals("FACT")) {
                parametros = new HashMap();
                parametros.put(";where", "o.sucursal=:sucursal and o.puntoemision=:puntoemision "
                        + " and o.nrodocumento=:nrodocumento and o.autorizacionsri=:autorizacionsri"
                        + " and o.tipodocumento=:tipodocumento");
                parametros.put("sucursal", factura.getSucursal());
                parametros.put("puntoemision", factura.getPuntoemision());
                parametros.put("nrodocumento", factura.getNrodocumento());
                parametros.put("autorizacionsri", factura.getAutorizacionsri());
                parametros.put("tipodocumento", tipoDocumento);
                int cuantos = ejbCabecerafacturas.contar(parametros);
                if (cuantos > 0) {
                    MensajesErrores.advertencia("No Nota de Crédito ya utilizado");
                    return null;
                }

                String vale = ejbCabeceras.validarCierre(factura.getFechacontabilizacion());
                if (vale != null) {
                    MensajesErrores.advertencia(vale);
                    return null;
                }

                nuevaFactura = factura;
                nuevaFactura.setTipodocumento(tipoDocumento);
                ejbCabecerafacturas.create(nuevaFactura, seguridadBean.getLogueado().getUserid());
                factura.setDenotacredito(nuevaFactura.getId());
            }
            ejbCabecerafacturas.edit(factura, seguridadBean.getLogueado().getUserid());

            if (factura.getTipodocumento().getCodigo().equals("FACT")) {
                factura = nuevaFactura;
            }

            int numero = tipo.getUltimo() + 1;
            Cabeceras cabecera = new Cabeceras();
            cabecera.setFecha(factura.getFechacontabilizacion());
            cabecera.setUsuario(seguridadBean.getLogueado().getUserid());
            cabecera.setNumero(numero);
            cabecera.setDescripcion(factura.getObservaciones());
            cabecera.setDia(new Date());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setIdmodulo(factura.getId());
            cabecera.setOpcion("NOTA_CREDITO");
            cabecera.setTipo(tipo);
            ejbCabeceras.create(cabecera, seguridadBean.getLogueado().getUserid());
            numero++;
            //            CREACION DE RENGLONES
            for (Renglones r : lista) {
//                if (cabecera != null) {
                r.setFecha(cabecera.getFecha());
                r.setReferencia(cabecera.getDescripcion());
                r.setCabecera(cabecera);
//                }
                ejbRenglones.create(r, seguridadBean.getLogueado().getUserid());
            }
            if (!listaReglonesInversion.isEmpty()) {
                cabecera = new Cabeceras();
                cabecera.setFecha(factura.getFecha());
                cabecera.setUsuario(seguridadBean.getLogueado().getUserid());
                cabecera.setNumero(numero);
                cabecera.setDescripcion(factura.getObservaciones());
                cabecera.setDia(new Date());
                cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabecera.setIdmodulo(factura.getId());
                cabecera.setOpcion("NOTA_CREDITO_RECLASIFICACION");
                cabecera.setTipo(tipo);
                ejbCabeceras.create(cabecera, seguridadBean.getLogueado().getUserid());
                numero++;
                for (Renglones r : listaReglonesInversion) {
                    r.setFecha(cabecera.getFecha());
                    r.setReferencia(cabecera.getDescripcion());
                    r.setCabecera(cabecera);
                    ejbRenglones.create(r, seguridadBean.getLogueado().getUserid());
                }
            }

            tipo.setUltimo(numero);
            ejbTipoAsiento.edit(tipo, seguridadBean.getLogueado().getUserid());
            // grabar los detalles
            for (Detallefacturas d : listadoDetalle) {
                ejbDetallefacturas.edit(d, seguridadBean.getLogueado().getUserid());
            }
            numero = factura.getNrodocumento();
            numero++;
            factura.setAutorizacionsri(documento.getAutorizacion());
            ejbCabecerafacturas.edit(factura, seguridadBean.getLogueado().getUserid());
            documento.setNumeroactual(numero);
            documento.setFechaultimo(new Date());
            ejbDocumentos.edit(documento, seguridadBean.getLogueado().getUserid());
//            grabarEnHoja(factura);
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Facturra contabilizada correctamente");
        formularioContabilizar.cancelar();
        buscar();
        return null;
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

    public SelectItem[] getComboDocumentos() {
        if (puntoEmision == null) {
            return null;
        }
        Codigos ret = codigosBean.traerCodigo("DOCS", "NTCR");
        Map parametros = new HashMap();
        parametros.put(";where", "o.punto=:punto and o.documento=:documento");
//        parametros.put(";where", "o.id is not null");
        parametros.put("punto", puntoEmision);
        parametros.put("documento", ret);
        try {
            return Combos.getSelectItems(ejbDocumentos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the documento
     */
    public Documentos getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(Documentos documento) {
        this.documento = documento;
    }

    public List<Renglones> traerRenglones(Cabecerafacturas fac) {

//        if (factura == null) {
//            return null;
//        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id  and o.cabecera.opcion like 'FACTURACION%'");
        parametros.put("id", fac.getId());
        parametros.put(";orden", "o.valor desc");
//        parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());

        try {
            // lo nuevo también va
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double traerTotal(Cabecerafacturas fac) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.factura=:factura");
        parametros.put("factura", fac);
        try {
            List<Detallefacturas> lista = ejbDetallefacturas.encontarParametros(parametros);
            double total = 0;
            for (Detallefacturas dv : lista) {
                if (dv.getDescuento().doubleValue() > 0) {
                    double total1 = 0;
                    total1 += ((dv.getCantidad().doubleValue() * dv.getValor().doubleValue())
                            - (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                            * (dv.getDescuento().doubleValue() / 100)));
                    total += total1 + (total1 * dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100);
                } else {
                    total += (dv.getCantidad().doubleValue() * dv.getValor().doubleValue())
                            + (dv.getCantidad().doubleValue() * dv.getValor().doubleValue()
                            * (dv.getProducto().getImpuesto().getPorcentaje().doubleValue() / 100));
                }
            }

            return total;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the listadoDetalle
     */
    public List<Detallefacturas> getListadoDetalle() {
        return listadoDetalle;
    }

    /**
     * @param listadoDetalle the listadoDetalle to set
     */
    public void setListadoDetalle(List<Detallefacturas> listadoDetalle) {
        this.listadoDetalle = listadoDetalle;
    }

    public String consultarFactura(Cabecerafacturas cab) {

        String donde = getConfiguracionBean().getConfiguracion().getUrlsri();
        if (configuracionBean.getConfiguracion().getAmbiente().equals("1")) {
            donde = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
        }
        DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
        String clave = cab.getClave();
        if ((cab.getClave() == null) || (cab.getClave().isEmpty())) {
            clave = ejbCabecerafacturas.generaClave(cab.getFecha(), TipoComprobanteEnum.FACTURA.getCode(),
                    cab.getCliente().getEmpresa().getRuc(),
                    configuracionBean.getConfiguracion().getAmbiente(),
                    cab.getSucursal().concat(cab.getPuntoemision()), df1.format(cab.getNrodocumento()),
                    dfAdicional.format(cab.getId()), "1");
        }
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", clave);
        if (rcomprobante.getNumeroComprobantes().equals("0")) {
            MensajesErrores.advertencia("No existen comprobantes con esa clave");
            return null;
        }
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();

        if (a != null) {
            if (a.getAutorizacion() != null) {
                if (!a.getAutorizacion().isEmpty()) {
                    if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {
                        Date cuando = ejbAsignaciones.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        MensajesErrores.informacion("AUTORIZADO : " + sdf.format(cuando));
                        return null;
                    }
                    String autorizacion = a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getMensaje() + " - "
                            + a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();
                    MensajesErrores.informacion(autorizacion);
                    String estado = a.getAutorizacion().get(0).getEstado();

                }
            }
        }

        return null;
    }

    public String imprimirFactura(Cabecerafacturas cab) {
        String donde = getConfiguracionBean().getConfiguracion().getUrlsri();
//        donde = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
        DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String clave = cab.getClave();
        Date cuando = new Date();
        if ((cab.getClave() == null) || (cab.getClave().isEmpty())) {
            String pto = cab.getPuntoemision() == null ? "001" : cab.getPuntoemision();
            String suc = cab.getSucursal() == null ? "001" : cab.getPuntoemision();
            String num = df1.format(cab.getNrodocumento() == null ? cab.getId() : cab.getNrodocumento());

            clave = ejbCabecerafacturas.generaClave(cab.getFecha(), TipoComprobanteEnum.FACTURA.getCode(),
                    cab.getCliente().getEmpresa().getRuc(),
                    configuracionBean.getConfiguracion().getAmbiente(),
                    suc.concat(pto), num,
                    dfAdicional.format(cab.getId()), "1");
        }
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", clave);
//        if (rcomprobante.getNumeroComprobantes().equals("0")) {
//            MensajesErrores.advertencia("No existen comprobantes con esa clave");
//            return null;
//        }
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();

        if (a != null) {
            if (a.getAutorizacion() != null) {
                if (!a.getAutorizacion().isEmpty()) {
                    if (a.getAutorizacion().get(0).getEstado().contains("AUTORIZADO")) {
                        cuando = ejbAsignaciones.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());

//                        new Recurso(Files.readAllBytes(archivo.toPath()));
//                        MensajesErrores.informacion("AUTORIZADO : "+sdf.format(cuando));
//                        return null;
                    }
//                    String autorizacion = a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getMensaje() + " - "
//                            + a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();
//                    MensajesErrores.informacion(autorizacion);
//                    String estado = a.getAutorizacion().get(0).getEstado();

                }
            }
        }
        File pasa = ejbCabecerafacturas.imprimirComprobante(cab, sdf.format(cuando));
        try {
            reporte = new Recurso(Files.readAllBytes(pasa.toPath()));
        } catch (IOException ex) {
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioReporte.insertar();
        return null;
    }

    public String reenviar(Cabecerafacturas cab) {
        this.cab = cab;
        setCorreo(cab.getCliente().getEmpresa().getEmail());
        getFormularioCorreo().insertar();
        return null;
    }

    public String reenviarCorreo() {
        if ((correo == null) || (correo.isEmpty())) {
            MensajesErrores.advertencia("Correo necesario");
            return null;
        }

        String donde = getConfiguracionBean().getConfiguracion().getUrlsri();
//        donde = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
        DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
        DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String clave = cab.getClave();
        Date cuando = new Date();
        if ((cab.getClave() == null) || (cab.getClave().isEmpty())) {
            String pto = cab.getPuntoemision() == null ? "001" : cab.getPuntoemision();
            String suc = cab.getSucursal() == null ? "001" : cab.getPuntoemision();
            String num = df1.format(cab.getNrodocumento() == null ? cab.getId() : cab.getNrodocumento());

            clave = ejbCabecerafacturas.generaClave(cab.getFecha(), TipoComprobanteEnum.FACTURA.getCode(),
                    cab.getCliente().getEmpresa().getRuc(),
                    configuracionBean.getConfiguracion().getAmbiente(),
                    suc.concat(pto), num,
                    dfAdicional.format(cab.getId()), "1");
        }
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", clave);
//        if (rcomprobante.getNumeroComprobantes().equals("0")) {
//            MensajesErrores.advertencia("No existen comprobantes con esa clave");
//            return null;
//        }
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();

        if (a != null) {
            if (a.getAutorizacion() != null) {
                if (!a.getAutorizacion().isEmpty()) {
                    if (a.getAutorizacion().get(0).getEstado().contains("AUTORIZADO")) {
                        cuando = ejbAsignaciones.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
                        getFormularioCorreo().cancelar();
                        String xmlResultado = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                                + "<autorizacion>\n"
                                + "    <estado>"
                                + a.getAutorizacion().get(0).getEstado()
                                + "</estado>\n"
                                + "<numeroAutorizacion>" + a.getAutorizacion().get(0).getNumeroAutorizacion() + "</numeroAutorizacion>\n"
                                + "<fechaAutorizacion>" + a.getAutorizacion().get(0).getFechaAutorizacion() + "</fechaAutorizacion>\n"
                                + "<comprobante><![CDATA[" + a.getAutorizacion().get(0).getComprobante() + "]]></comprobante>\n"
                                + "</autorizacion>";
                        ejbCabecerafacturas.reenviarCorreo(cab, sdf.format(cuando), correo, xmlResultado);

//                        new Recurso(Files.readAllBytes(archivo.toPath()));
//                        MensajesErrores.informacion("AUTORIZADO : "+sdf.format(cuando));
//                        return null;
                    }
//                    String autorizacion = a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getMensaje() + " - "
//                            + a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();
//                    MensajesErrores.informacion(autorizacion);
//                    String estado = a.getAutorizacion().get(0).getEstado();

                }
            }
        }
        getFormularioCorreo().cancelar();
        return null;
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
     * @return the formularioCorreo
     */
    public Formulario getFormularioCorreo() {
        return formularioCorreo;
    }

    /**
     * @param formularioCorreo the formularioCorreo to set
     */
    public void setFormularioCorreo(Formulario formularioCorreo) {
        this.formularioCorreo = formularioCorreo;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
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
     * @return the cuantaCobra
     */
    public String getCuantaCobra() {
        return cuantaCobra;
    }

    /**
     * @param cuantaCobra the cuantaCobra to set
     */
    public void setCuantaCobra(String cuantaCobra) {
        this.cuantaCobra = cuantaCobra;
    }

    public SelectItem[] getComboSucursales() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.ruc desc");
        try {
            return Combos.getSelectItems(ejbSucursales.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SucursalesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPuntoemision() {
        if (sucursal == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.sucursal=:sucursal");
        parametros.put("sucursal", sucursal);
        try {
            return Combos.getSelectItems(ejbPuntoemision.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the sucursal
     */
    public Sucursales getSucursal() {
        return sucursal;
    }

    /**
     * @param sucursal the sucursal to set
     */
    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }

    public String grabarEnHoja(Cabecerafacturas factura) {
        reporteRecibo = null;
        reporteFac = null;
//        this.factura = factura;
        try {
            List<Renglones> renglones = traerRenglonesGasto(factura);
            double val = 0;
            for (Renglones r : renglones) {
                if (r.getValor().doubleValue() > 0) {
                    val += r.getValor().doubleValue();
                }
            }
            ReciboIngresos hoja = new ReciboIngresos();
            hoja.llenarCobroFactura(factura, val);
            reporteRecibo = hoja.traerRecurso();
            formularioImprimir.insertar();
            imprimir(factura);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir(Cabecerafacturas factura) {
        try {
            List<Renglones> renglonesLocal = traerRenglonesGasto(factura);
            List<Renglones> renglonesRecla = traerRenglonesReclasificacion(factura);
            String empresa = "";
            String email = "";
            if (factura.getCliente().getEmpresa() != null) {
                empresa = factura.getCliente().getEmpresa().toString();
                email = factura.getCliente().getEmpresa().getEmail();
            }

            Cabeceras c = new Cabeceras();
            if (renglonesLocal != null) {
                if (!renglonesLocal.isEmpty()) {
                    for (Renglones r : renglonesLocal) {
                        c = r.getCabecera();
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadBean.getLogueado().getUserid());
//            pdf.agregaTitulo("Beneficiario : " + empresa);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Beneficiario"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, empresa));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(factura.getFechacontabilizacion())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Documento"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getTipo().getNombre() + " - " + c.getNumero()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Módulo"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getModulo() + " - " + c.getIdmodulo()));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaParrafo("Descripción : " + factura.getObservaciones() + "\n\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            pdf.agregaParrafo("\n");
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglonesLocal != null) {
                if (!renglonesLocal.isEmpty()) {
                    for (Renglones r : renglonesLocal) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                            sumaCreditos += valor * -1;
                        }
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION N° ");
                }
            }
            pdf.agregaParrafo("\n");
            // reclasificación
            if (renglonesRecla != null) {
                if (!renglonesRecla.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesRecla) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor;
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                            sumaCreditos += valor * -1;
                        }
                        c = r.getCabecera();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION RECLASIFICACION N° " + c.getNumero());
                }
            }
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            pdf.agregarTabla(null, columnas, 3, 100, null);
//            setReporte(pdf.traerRecurso());
            reporteFac = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(NotaCreditoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerRenglonesReclasificacion(Cabecerafacturas factura) {
        if (factura != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('FACTURACION_RECLASIFICACION')");
            parametros.put("id", factura.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            try {
                List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                return rl;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public List<Renglones> traerRenglonesGasto(Cabecerafacturas factura) {

        if (factura != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id  and o.cabecera.opcion='FACTURACION'");
            parametros.put("id", factura.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");

            try {
                // lo nuevo también va
                return ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    /**
     * @return the reporteRecibo
     */
    public Resource getReporteRecibo() {
        return reporteRecibo;
    }

    /**
     * @param reporteRecibo the reporteRecibo to set
     */
    public void setReporteRecibo(Resource reporteRecibo) {
        this.reporteRecibo = reporteRecibo;
    }

    /**
     * @return the reporteFac
     */
    public Resource getReporteFac() {
        return reporteFac;
    }

    /**
     * @param reporteFac the reporteFac to set
     */
    public void setReporteFac(Resource reporteFac) {
        this.reporteFac = reporteFac;
    }
}
