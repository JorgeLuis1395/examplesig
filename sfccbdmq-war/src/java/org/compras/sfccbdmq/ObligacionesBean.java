/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.utilitarios.sfccbdmq.Adicionales;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarComprobantes;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.PlantillaRetencion;
import org.auxiliares.sfccbdmq.Reportesds;
import org.utilitarios.sfccbdmq.infoTributaria;
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.FirmadorFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.DocumentosEmisionBean;
import org.contabilidad.sfccbdmq.PuntoEmisionBean;
import org.contabilidad.sfccbdmq.RetencionesBean;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retenciones;
import org.entidades.sfccbdmq.Tipoegreso;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.utilitarios.sfccbdmq.DOMUtils;
import org.utilitarios.sfccbdmq.FirmaXAdESBESS;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.icefaces.apache.commons.io.IOUtils;
import org.inventarios.sfccbdmq.IngresosTxBean;
import org.pagos.sfccbdmq.AnticiposProveedoresBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "obligacionesSfccbdmq")
@ViewScoped
public class ObligacionesBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ObligacionesBean() {
        Calendar c = Calendar.getInstance();
        obligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        listaActivos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        listaCabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Obligaciones> obligaciones;
    private LazyDataModel<Activos> listaActivos;
    private LazyDataModel<Cabecerainventario> listaCabeceras;
    private List<Rascompras> detalles;
    private List<Renglones> renglones;
    private List<Renglones> renglonesInv;
    private List<Rascompras> detallesInternos;
    private List<Rascompras> detallesImpresion;
    private List<Pagosvencimientos> promesas;
    private List<Pagosvencimientos> promesasb;
    private List<Rascompras> detallesb;
    private List<AuxiliarCarga> totales;
    private Rascompras detalle;
    private Rascompras detalleModificar;
    private Pagosvencimientos pago;
    private Obligaciones obligacion = new Obligaciones();
    private Impuestos impuesto;
    private Retenciones retencion;
    private Puntoemision puntoEmision;
    private ComprobanteRetencion comprobante;
    private Compromisos compromiso;
    private Cuentas cuentaProveedor;
    private Anticipos anticipo;
    private Autorizaciones autorizacion;
    private Documentos autorizacionDocumento;
    private double valorAnticipo;
    private double valorTotalActivos;
    private double valorTotalSuministros;
    private boolean listoContabilizar;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioDetalleCuenta = new Formulario();
    private Formulario formularioPromesas = new Formulario();
    private Formulario formularioInterno = new Formulario();
    private Formulario formularioRetencion = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioCompromiso = new Formulario();
    private Formulario formularioActivos = new Formulario();
    private Formulario formularioSuministros = new Formulario();
    private Formulario formularioImprimirSuministros = new Formulario();
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RascomprasFacade ejbDetalles;
    @EJB
    private PagosvencimientosFacade ejbVencimientos;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private DocumentosFacade ejbDocumnetos;
    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private RetencionesFacade ejbRetenciones;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private CabecerasFacade ejbCabRen;
    @EJB
    private ActivosFacade ejbActivos;
    @EJB
    private CabecerainventarioFacade ejbCabecerainventario;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private PuntoemisionFacade ejbPunto;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{facturaSri}")
    private FacturaSriBean facturaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{documentosElectronicosSfccbdmq}")
    private DocumentosElectronicosBean docElecBean;
    @ManagedProperty(value = "#{puntosemisionSfccbdmq}")
    private PuntoEmisionBean puntosBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String tipoFecha = "o.fechaemision";
    private String concepto;
//    private String clave;
    private Integer estado = 1;
    private Tipoegreso tipoEgreso;
    private Codigos tipoDocumento;
    private Contratos contrato;
    private Integer numero;
    private String numeroFacturaActivos;
    private Integer numeroFacturaSuministro;
    private Integer id;
    private Documentos documeto;
    private boolean iva;
    private boolean empleado;
    private boolean soloRetencion;
    private boolean tieneAmortizaciones = false;
    //
//    private File keyStore;
    private File archivo;
    private File pdfFile;
    // reportes para enviar por mail
    private Resource recursoXml;
    private Resource recursoPdf;
    // para reporte de retencion manual
    private Resource recursoRetencionManual;
    private Resource reporte;
    private Resource reporteSolicitud;
    private Formulario formularioRetencionManual = new Formulario();
    private Formulario formularioRetencionElectronica = new Formulario();

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
        String redirect = (String) params.get("faces-redirect");
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        if (redirect == null) {
//            return;
        } else {
            String perfilLocal = (String) params.get("p");
            String nombreForma = "ObligacionesVista";
            if (perfilLocal == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
            this.setPerfil(seguridadbean.traerPerfil(perfilLocal));
            if (this.getPerfil() == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    seguridadbean.cerraSession();
//                }
//            }
        }
    }

    public List<Obligaciones> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (tipoFecha == null) {
            tipoFecha = "o.fechaemision";
        }
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechaemision desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = tipoFecha + " between :desde and :hasta";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
        if (proveedorBean.getProveedor() != null) {
            if (contrato != null) {
                where += " and o.contrato=:contrato";
                parametros.put("contrato", contrato);
            } else {
                if (proveedorBean.getProveedor().getId() != null) {
                    where += " and o.proveedor=:proveedor";
                    parametros.put("proveedor", proveedorBean.getProveedor());
                }
            }
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.compromiso.beneficiario=:beneficiario";
            parametros.put("beneficiario", entidadesBean.getEntidad().getEmpleados());
        }
        if (estado != null) {
            where += " and o.estado=:estado";
            parametros.put("estado", estado);
        }
        if (tieneAmortizaciones) {
            where += " and o.amortizacion is null";
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.concepto) like :concepto";
            parametros.put("concepto", concepto.toUpperCase() + "%");
        }
        if (tipoEgreso != null) {
            where += " and o.tipoobligacion=:tipoobligacion";
            parametros.put("tipoobligacion", tipoEgreso);
        }
        if (tipoDocumento != null) {
            where += " and o.tipodocumento=:tipodocumento";
            parametros.put("tipodocumento", tipoDocumento);
            if (numero != null) {
                where += " and o.documento=:documento";
                parametros.put("documento", numero);
            }
        }

        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbObligaciones.contar(parametros);
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
        obligaciones.setRowCount(total);
        try {
            return ejbObligaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        obligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

        return null;
    }

    public String crear() {
        listoContabilizar = false;
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Por favor seleccione un proveedor");
            return null;
        }
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Por favor seleccione un proveedor");
            return null;
        }
        docElecBean.setProveedor(proveedorBean.getProveedor().getEmpresa().getRuc());
        if (contrato != null) {
            // ver si tiene anticipo 
            if (!((contrato.getAnticipo() == null) || (contrato.getAnticipo().doubleValue() == 0))) {
                // ver si se ha pagado el anticipo
                Map parametros = new HashMap();
                parametros.put(";where", "o.contrato=:contrato");
                parametros.put("contrato", contrato);
                try {
                    int total = ejbAnticipos.contar(parametros);
                    if (total == 0) {
                        MensajesErrores.advertencia("Por favor pague el anticipo antes de generar una obligación");
                        return null;
                    }
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        obligacion = new Obligaciones();
        detalles = new LinkedList<>();
        detallesb = new LinkedList<>();
        obligacion.setProveedor(proveedorBean.getProveedor());
        obligacion.setContrato(contrato);
        obligacion.setUsuario(seguridadbean.getLogueado());
//        obligacion.setFechaemision(new Date());
        obligacion.setEstado(0); // No se si 1 o que
        obligacion.setSustento(codigosBean.getSustentoDefault());
        getFacturaBean().setFactura(null);
        promesas = new LinkedList<>();
        promesasb = new LinkedList<>();
        comprobante = new ComprobanteRetencion();
        puntoEmision = null;
        autorizacion = null;
        totales = new LinkedList<>();
        renglones = null;
        renglonesInv = null;
        getFormulario().insertar();
//        formularioCompromiso.insertar();
        return null;
    }

    public String acetparCompromiso(Compromisos compromiso) {
        this.setCompromiso(compromiso);
        formularioCompromiso.cancelar();
        formularioDetalle.insertar();

        return null;
    }

    public String modificar(Obligaciones obligacion) {
        this.obligacion = obligacion;
        listoContabilizar = false;
        renglones = null;
        autorizacion = null;
        if (obligacion.getFechacontable() != null) {
            MensajesErrores.advertencia("Obligacion ya contabilizada no es posible modificar");
            return null;
        }
        if (obligacion.getEstado() == -1) {
            MensajesErrores.advertencia("Obligacion ya anulado");
            return null;
        }
        if (obligacion.getEstado() == 2) {
            MensajesErrores.advertencia("Obligacion ya contabilizada");
            return null;
        }

        try {
            tipoDocumento = obligacion.getTipodocumento();
            Map parametros = new HashMap();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            docElecBean.setProveedor(proveedorBean.getProveedor().getEmpresa().getRuc());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                getFacturaBean().setFactura(null);
                if (tipoDocumento != null) {
                    if (tipoDocumento.getCodigo().equals("LIQCOM")) {
//                    getFacturaBean().setFactura(null);
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
//                    parametros.put("tipodocumento", obligacion.getTipodocumento());
//                    parametros.put("autorizacion", obligacion.getAutorizacion());
//                    parametros.put("empresa", obligacion.getProveedor().getEmpresa());
//                    List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
//                    for (Autorizaciones a : la) {
//
//                        autorizacion = a;
//
//                    }
                    } else {
                        getFacturaBean().setFactura(null);
                        parametros = new HashMap();
                        parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
                        parametros.put("tipodocumento", obligacion.getTipodocumento());
                        parametros.put("autorizacion", obligacion.getAutorizacion());
                        parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                        List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                        for (Autorizaciones a : la) {

                            autorizacion = a;

                        }
                    }
                }
            } else {
                // generar factura electrónica
                getFacturaBean().cargar(obligacion.getFacturaelectronica());
                docElecBean.cargarDocumento();
            }
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            for (Rascompras d : detalles) {
                if (d.getDetallecompromiso() != null) {
                    setCompromiso(d.getDetallecompromiso().getCompromiso());
                }
            }
            promesas = ejbVencimientos.encontarParametros(parametros);
            // las promesas
            // ver punto de retencion
            if (obligacion.getPuntor() != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                parametros.put("codigo", obligacion.getPuntor());
                parametros.put("sucursal", obligacion.getEstablecimientor());
                List<Puntoemision> lpt = ejbPunto.encontarParametros(parametros);
                for (Puntoemision p : lpt) {
                    puntoEmision = p;
                    puntosBean.setSucursal(p.getSucursal());

                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotal();
        formulario.editar();

        return null;
    }

    public String modificarContab(Obligaciones obligacion) {
        this.obligacion = obligacion;
        listoContabilizar = true;
        autorizacion = null;
        if (obligacion.getFechacontable() != null) {
            MensajesErrores.advertencia("Obligacion ya contabilizada no es posible modificar");
            listoContabilizar = false;
            return null;
        }
        if (obligacion.getEstado() == -1) {
            MensajesErrores.advertencia("Obligacion ya anulado");
            listoContabilizar = false;
            return null;
        }
        if (obligacion.getEstado() == 2) {
            MensajesErrores.advertencia("Obligacion ya contabilizada");
            listoContabilizar = false;
            return null;
        }
        if (obligacion.getFechar() == null) {
            MensajesErrores.advertencia("Obligacion sin retención no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if (obligacion.getFechaemision() == null) {
            MensajesErrores.advertencia("Obligacion sin fecha de emisisón no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getPuntoemision() == null) || (obligacion.getPuntoemision().isEmpty())) {
            MensajesErrores.advertencia("Obligacion sin factura no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getPuntor() == null) || (obligacion.getPuntor().isEmpty())) {
            MensajesErrores.advertencia("Obligacion sin retención no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getEstablecimientor() == null) || (obligacion.getEstablecimientor().isEmpty())) {
            MensajesErrores.advertencia("Obligacion sin retención no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getEstablecimiento() == null) || (obligacion.getEstablecimiento().isEmpty())) {
            MensajesErrores.advertencia("Obligacion sin factura no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getDocumento() == null)) {
            MensajesErrores.advertencia("Obligacion sin factura no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getTipodocumento() == null)) {
            MensajesErrores.advertencia("Obligacion sin factura no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        if ((obligacion.getAutorizacion() == null) || (obligacion.getAutorizacion().isEmpty())) {
            MensajesErrores.advertencia("Obligacion sin factura no se puede contabilizar");
            listoContabilizar = false;
            return null;
        }
        try {
            Map parametros = new HashMap();
            tipoDocumento = obligacion.getTipodocumento();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            docElecBean.setProveedor(proveedorBean.getProveedor().getEmpresa().getRuc());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                getFacturaBean().setFactura(null);
                if (tipoDocumento.getCodigo().equals("LIQCOM")) {
//                    getFacturaBean().setFactura(null);
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
//                    parametros.put("tipodocumento", obligacion.getTipodocumento());
//                    parametros.put("autorizacion", obligacion.getAutorizacion());
//                    parametros.put("empresa", obligacion.getProveedor().getEmpresa());
//                    List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
//                    for (Autorizaciones a : la) {
//
//                        autorizacion = a;
//
//                    }
                } else {
                    getFacturaBean().setFactura(null);
                    parametros = new HashMap();
                    parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
                    parametros.put("tipodocumento", obligacion.getTipodocumento());
                    parametros.put("autorizacion", obligacion.getAutorizacion());
                    parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                    List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                    for (Autorizaciones a : la) {

                        autorizacion = a;

                    }
                }
            } else {
                // generar factura electrónica
                getFacturaBean().cargar(obligacion.getFacturaelectronica());
                docElecBean.cargarDocumento();
            }
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            for (Rascompras d : detalles) {
                if (d.getDetallecompromiso() != null) {
                    setCompromiso(d.getDetallecompromiso().getCompromiso());
                }
            }
            promesas = ejbVencimientos.encontarParametros(parametros);
            // las promesas
            // ver punto de retencion
            if (obligacion.getPuntor() != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                parametros.put("codigo", obligacion.getPuntor());
                parametros.put("sucursal", obligacion.getEstablecimientor());
                List<Puntoemision> lpt = ejbPunto.encontarParametros(parametros);
                for (Puntoemision p : lpt) {
                    puntoEmision = p;
                    puntosBean.setSucursal(p.getSucursal());

                }
            }
            if ((formularioActivos.getIndiceFila() == 1) || (formularioSuministros.getIndiceFila() == 1)) {
                renglones = ejbObligaciones.contabilizarAntes(obligacion,
                        seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(),
                        numeroFacturaActivos, numeroFacturaSuministro);
            } else {
                renglones = ejbObligaciones.contabilizarAntes(obligacion,
                        seguridadbean.getLogueado().getUserid(), 1,
                        perfil.getMenu().getIdpadre().getModulo(), null, null);
            }
            if (renglones.isEmpty()) {
                MensajesErrores.advertencia("No esposible contabilizar falta información");
                listoContabilizar = false;
                return null;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotal();

        formulario.editar();

        return null;
    }

    public String eliminar(Obligaciones obligacion) {
        this.obligacion = obligacion;

        if (obligacion.getEstado() == -1) {
            MensajesErrores.advertencia("Obligacion ya anulado");
            return null;
        }
        proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
        proveedorBean.setProveedor(obligacion.getProveedor());
//        proveedorBean.setTipobuscar("-2");
        proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
        // buscar ras compras
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
            promesas = ejbVencimientos.encontarParametros(parametros);
            for (Pagosvencimientos p : promesas) {
                if (p.getKardexbanco() != null) {
                    MensajesErrores.advertencia("Obligacion ya generada la Nota de Egreso");
                    return null;
                }
            }
            // las promesas
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRenglones();
//        calculaTotal();
        getFormulario().eliminar();

        return null;
    }

    public List<Activos> cargarActivos(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
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

        where += " and o.proveedor=:proveedor";
        parametros.put("proveedor", obligacion.getProveedor());

        where += " and o.factura=:factura";
        parametros.put("factura", numeroFacturaActivos);

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
        listaActivos.setRowCount(total);
        try {
            return ejbActivos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String activos() {
        numeroFacturaActivos = null;
        String noFactura = null;
        if (obligacion.getDocumento() == null) {
            if (getFacturaBean().getFactura() != null) {
                noFactura = getFacturaBean().getFactura().getInfoTributaria().getEstab() + "-"
                        + getFacturaBean().getFactura().getInfoTributaria().getPtoEmi()
                        + "-" + getFacturaBean().getFactura().getInfoTributaria().getSecuencial();
            }
        } else {
            if (autorizacion != null) {
                noFactura = autorizacion.getEstablecimiento() + "-"
                        + autorizacion.getPuntoemision() + "-"
                        + String.valueOf(obligacion.getDocumento());
            }
//            numeroFacturaActivos = obligacion.getDocumento();
        }
        if (noFactura == null) {
            MensajesErrores.advertencia("Ingrese un número de factura primero");
            return null;
        }
        if (obligacion.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }
        String where = "  o.fechaalta is null and o.proveedor=:proveedor and o.factura=:factura";

        Map parametrosSuma = new HashMap();
        parametrosSuma.put(";orden", "o.codigo");
        parametrosSuma.put("proveedor", obligacion.getProveedor());
        parametrosSuma.put("factura", noFactura);
        parametrosSuma.put(";where", where);
        parametrosSuma.put(";campo", "o.valoralta");
        try {
            valorTotalActivos = ejbActivos.sumarCampo(parametrosSuma).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaActivos = new LazyDataModel<Activos>() {

            @Override
            public List<Activos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargarActivos(i, i1, scs, map);
            }
        };
        getFormularioActivos().editar();
        formularioActivos.setIndiceFila(0);

        return null;
    }

    public String grabarActivos() {
        // saber el total de la factura == al total de los activos
        formularioActivos.setIndiceFila(1);
        obligacion.setSustento(codigosBean.getSustentoActivos());
        formularioActivos.cancelar();
        return null;
    }

    public List<Cabecerainventario> cargarCabecera(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        numeroFacturaSuministro = null;
        if (obligacion.getDocumento() == null) {
            if (getFacturaBean().getFactura() != null) {
                numeroFacturaSuministro = Integer.parseInt(getFacturaBean().getFactura().getInfoTributaria().getSecuencial());
            }
        } else {
            numeroFacturaSuministro = obligacion.getDocumento();
        }

        if (obligacion.getProveedor() == null) {

            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fechadigitacion desc");
        String where = "  o.estado=0 and o.txid.ingreso=true and  o.txid.contabiliza=false and o.obligacion is null";
        // Numero de factura
        where += " and o.factura =:documento";
        parametros.put("documento", numeroFacturaSuministro);

        if (obligacion.getContrato() != null) {
            where += " and o.contrato=:contrato";
            parametros.put("contrato", obligacion.getContrato());
        }
        where += " and o.proveedor=:proveedor";
        parametros.put("proveedor", obligacion.getProveedor());

        int total = 0;

        try {
            parametros.put(";where", where);
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
        listaCabeceras.setRowCount(total);
        try {
            return ejbCabecerainventario.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getValorIngresoCabecera() {
        Cabecerainventario c = (Cabecerainventario) listaCabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecerainventario");
        parametros.put("cabecerainventario", c);
        parametros.put(";campo", "(o.cantidad+o.cantidadinversion)*o.costo");
        try {
            return ejbKardex.sumarCampoFloat(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String suministros() {
        numeroFacturaSuministro = null;
        if (obligacion.getDocumento() == null) {
            if (getFacturaBean().getFactura() != null) {
                numeroFacturaSuministro = Integer.parseInt(getFacturaBean().getFactura().getInfoTributaria().getSecuencial());
            }
        } else {
            numeroFacturaSuministro = obligacion.getDocumento();
        }
        if (numeroFacturaSuministro == null) {
            MensajesErrores.advertencia("Ingrese un número de factura primero");
            return null;
        }
        if (obligacion.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }
        String where = "  o.cabecerainventario.estado=0 and o.cabecerainventario.txid.ingreso=true and o.cabecerainventario.obligacion is null"
                + " and  o.cabecerainventario.txid.contabiliza=false";
        Map parametros = new HashMap();
        where += " and o.cabecerainventario.factura =:documento";
        parametros.put("documento", numeroFacturaSuministro);

        if (obligacion.getContrato() != null) {
            where += " and o.cabecerainventario.contrato=:contrato";
            parametros.put("contrato", obligacion.getContrato());
        }
        where += " and o.cabecerainventario.proveedor=:proveedor";
        parametros.put("proveedor", obligacion.getProveedor());
        try {
            parametros.put(";where", where);
            parametros.put(";campo", "(o.cantidad+o.cantidadinversion)*o.costo");
            valorTotalSuministros = ejbKardex.sumarCampoFloat(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaCabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return cargarCabecera(i, i1, scs, map);
            }
        };
        getFormularioSuministros().editar();
        formularioSuministros.setIndiceFila(0);
        return null;
    }

    public String grabarSuministros() {
        // saber el total de la factura == al total de los activos
        formularioSuministros.setIndiceFila(1);
        formularioSuministros.cancelar();
        return null;
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

    public boolean validar() {
        if (obligacion.getSustento() == null) {
            List<Codigos> sustentosLista = codigosBean.getSustentos();
            for (Codigos c : sustentosLista) {
                obligacion.setSustento(c);
            }
        }
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion");
            return true;
        }
        if (obligacion.getFechaemision() == null) {
            MensajesErrores.advertencia("Falta fecha de emisión");
            return true;
        }
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return true;
        }
        if (detalles.isEmpty()) {
            MensajesErrores.advertencia("Necesario cuentas de obligación");
            return true;
        }
        if (promesas.isEmpty()) {
            MensajesErrores.advertencia("Necesario promesas de pagos");
            return true;
        }

        double calculaT = calculaTotal();
        calculaT = Math.round(calculaT);
        if (calculaT != 0) {
            MensajesErrores.advertencia("Obligación no cuadrada total debe ser igual a 0");
            return true;
        }
        // ver si egresos igual a certificacion
        double cuadre = 0;
        double egreso = 0;
        for (AuxiliarCarga a : totales) {
            switch (a.getCuenta()) {
                case "Compromiso Presupuestario":
                    cuadre += a.getIngresos().doubleValue();
                    break;
                case "Egresos":
                case "Cuentas":
                    cuadre -= a.getIngresos().doubleValue();
                    egreso += a.getIngresos().doubleValue();
                    break;
            }
        }

        cuadre = Math.round(cuadre);
        if (docElecBean.getDocumento() != null) {
            obligacion.setFacturaelectronica(docElecBean.getDocumento().getXml());
        }
        if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
            // factura manual
            if ((autorizacion == null) && (autorizacionDocumento == null)) {
                MensajesErrores.advertencia("Seleccione una Autorización");
                return true;
            }
            if (autorizacion != null) {
                if (obligacion.getFechaemision().before(autorizacion.getFechaemision())) {
                    MensajesErrores.advertencia("Fecha de emisión menor a fecha de autorización");
                    return true;
                }
                if (obligacion.getFechaemision().after(autorizacion.getFechacaducidad())) {
                    MensajesErrores.advertencia("Fecha de emisión mayor a fecha de caducidad");
                    return true;
                }
                if (obligacion.getFechaemision().before(configuracionBean.getConfiguracion().getPvigente())) {
                    MensajesErrores.advertencia("Fecha de emisión menor a periodo vigente");
                    return true;
                }
                if (obligacion.getDocumento() < autorizacion.getInicio()) {
                    MensajesErrores.advertencia("Número de documento menor a inicio de autorización");
                    return true;
                }
                if (obligacion.getDocumento() > autorizacion.getFin()) {
                    MensajesErrores.advertencia("Número de documento mayor a fin de autorización");
                    return true;
                }
                // contar si ya existe
                Map parametros = new HashMap();
                parametros.put(";where", "o.documento=:documento and o.id!=:id and o.establecimiento=:estab "
                        + "and o.proveedor=:proveedor"
                        + "and o.puntoemision=:punto");
                parametros.put("documento", obligacion.getDocumento());
                parametros.put("estab", autorizacion.getEstablecimiento());
                parametros.put("proveedor", obligacion.getProveedor());
                parametros.put("punto", autorizacion.getPuntoemision());
                parametros.put("id", obligacion.getId());
                int total1;
                try {
                    total1 = ejbObligaciones.contar(parametros);
                    if (total1 > 0) {
                        MensajesErrores.advertencia("Número de documento ya existe");
                        return true;
                    }
                } catch (ConsultarException ex) {
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }

                obligacion.setAutorizacion(autorizacion.getAutorizacion());
                obligacion.setTipodocumento(autorizacion.getTipodocumento());
                obligacion.setFechacaduca(autorizacion.getFechacaducidad());
                obligacion.setEstablecimiento(autorizacion.getEstablecimiento());
                obligacion.setPuntoemision(autorizacion.getPuntoemision());
            } else if (autorizacionDocumento != null) {
                if (obligacion.getFechaemision().before(autorizacionDocumento.getDesde())) {
                    MensajesErrores.advertencia("Fecha de emisión menor a fecha de autorización");
                    return true;
                }
                if (obligacion.getFechaemision().after(autorizacionDocumento.getHasta())) {
                    MensajesErrores.advertencia("Fecha de emisión mayor a fecha de caducidad");
                    return true;
                }
                if (obligacion.getFechaemision().before(configuracionBean.getConfiguracion().getPvigente())) {
                    MensajesErrores.advertencia("Fecha de emisión menor a periodo vigente");
                    return true;
                }
                if (obligacion.getDocumento() < autorizacionDocumento.getInicial()) {
                    MensajesErrores.advertencia("Número de documento menor a inicio de autorización");
                    return true;
                }
                if (obligacion.getDocumento() > autorizacionDocumento.getFinal1()) {
                    MensajesErrores.advertencia("Número de documento mayor a fin de autorización");
                    return true;
                }
                obligacion.setAutorizacion(autorizacionDocumento.getAutorizacion());
                obligacion.setTipodocumento(autorizacionDocumento.getDocumento());
                obligacion.setFechacaduca(autorizacionDocumento.getHasta());
                obligacion.setEstablecimiento(autorizacionDocumento.getPunto().getSucursal().getRuc());
                obligacion.setPuntoemision(autorizacionDocumento.getPunto().getCodigo());
            }
//            obligacion.setPuntoemision(autorizacion.getPuntoemision());

        } else {
            // factura electronica
            double importeTotal = Double.parseDouble(getFacturaBean().getFactura().getInfoFactura().getImporteTotal()) - egreso;
            egreso = egreso * 100;
            if (Math.round(importeTotal) != 0) {
                MensajesErrores.advertencia("Importe total no cuadra con los egresos en la factura");
//                ***************************************OJO SOLO EMITE EL MENSAJE
//                return true;
            }
            if (!facturaBean.getFactura().getInfoTributaria().getRuc().equals(obligacion.getProveedor().getEmpresa().getRuc())) {
                MensajesErrores.advertencia("RUC de factura electrónica no corresponde al emisor");
                return true;
            }
            if (!facturaBean.getFactura().getInfoFactura().getIdentificacionComprador().equals(configuracionBean.getConfiguracion().getRuc())) {
                MensajesErrores.advertencia("Factura no esta emitidad para " + configuracionBean.getConfiguracion().getNombre());
                return true;
            }
            obligacion.setFechaemision(getFacturaBean().getFactura().getFechaEmi());
            Map parametros = new HashMap();
            parametros.put(";where", "o.autorizacion=:autorizacion and o.estado!=-1");
            parametros.put("autorizacion", getFacturaBean().getFactura().getAutorizacion().getNumeroAutorizacion());
            try {
                int total = ejbObligaciones.contar(parametros);
                if (formulario.isNuevo()) {
                    if (total > 0) {
                        MensajesErrores.advertencia("Factura ya registrada no autorización " + getFacturaBean().getFactura().getAutorizacion().getNumeroAutorizacion());
                        return true;
                    }
                } else if (formulario.isModificar()) {
                    if (total > 1) {
                        MensajesErrores.advertencia("Factura ya registrada no autorización " + getFacturaBean().getFactura().getAutorizacion().getNumeroAutorizacion());
                        return true;
                    }
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            // buscar clave de acceso en base de datos para ver si ya esta ingresada
            obligacion.setAutorizacion(getFacturaBean().getFactura().getAutorizacion().getNumeroAutorizacion());
            obligacion.setDocumento(Integer.parseInt(getFacturaBean().getFactura().getInfoTributaria().getSecuencial()));
            obligacion.setFechaemision(getFacturaBean().getFactura().getFechaEmi());
//            obligacion.setFechacaduca(autorizacion.getFechacaducidad());
            obligacion.setEstablecimiento(getFacturaBean().getFactura().getInfoTributaria().getEstab());
            obligacion.setPuntoemision(getFacturaBean().getFactura().getInfoTributaria().getPtoEmi());
            obligacion.setElectronica(getFacturaBean().getFactura().getInfoTributaria().getSecuencial());
            // buscar tipo de documento con el parametro de tipo para colocarlo
            List<Codigos> cl = codigosBean.traerCodigoParametros(Constantes.DOCUMENTOS, getFacturaBean().getFactura().getInfoTributaria().getCodDoc());
            for (Codigos c : cl) {
                obligacion.setTipodocumento(c);
            }
        }
        // sumar el valor de retencion
        double suma = 0;
        double sumaTotal = 0;
        for (Rascompras r : detalles) {
            suma += (r.getValorret() == null ? 0 : r.getValorret().doubleValue()) + (r.getVretimpuesto() == null ? 0 : r.getVretimpuesto().doubleValue());
            sumaTotal += (r.getValor() == null ? 0 : r.getValor().doubleValue()) + (r.getValorimpuesto() == null ? 0 : r.getValorimpuesto().doubleValue());
//            if (r.getDetallecompromiso() != null) {
////                if (!(r.getDetallecompromiso().getFecha().equals(obligacion.getFechaemision()))) {
////                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
////                    MensajesErrores.advertencia("Fecha de compromiso no corresponde a fecha de emisión : " + sdf.format(r.getDetallecompromiso().getFecha()));
////                    return true;
////                }
//            }
        }
        if (suma > 0) {
            if (puntoEmision == null) {
                MensajesErrores.advertencia("Seleccione un punto de emisión");
                return true;
            }
            if (!puntoEmision.getAutomatica()) {
                // es numeración automática
                if (documeto == null) {
                    MensajesErrores.advertencia("Seleccione una autorización para la emisión de la retención");
                    return true;
                }
                if (obligacion.getFechar() == null) {
                    MensajesErrores.advertencia("Es necesaria la fecha de la emisión de la retención");
                    return true;
                }
                if (obligacion.getFechar().before(configuracionBean.getConfiguracion().getPvigente())) {
                    MensajesErrores.advertencia("Fecha de emisión de retenciónmenor a periodo vigente");
                    return true;
                }
                if ((obligacion.getNumeror() == null) || (obligacion.getNumeror() <= 0)) {
                    MensajesErrores.advertencia("Es necesario el número de documento de la emisión de la retención");
                    return true;
                }
                if (documeto.getInicial() > obligacion.getNumeror()) {
                    MensajesErrores.advertencia("Documento de la retención menor al número mínimo de la autorización");
                    return true;
                }
                if (documeto.getFinal1() < obligacion.getNumeror()) {
                    MensajesErrores.advertencia("Documento de la retención mayor al número máximo de la autorización");
                    return true;
                }
                if (documeto.getDesde().after(obligacion.getFechar())) {
                    MensajesErrores.advertencia("Fecha de autorización de retención menor a la fecha desde de la autorización");
                    return true;
                }
                if (obligacion.getFechar().after(new Date())) {
                    MensajesErrores.advertencia("Fecha de de retención de ser menor a hoy");
                    return true;
                }
                if (documeto.getHasta().before(obligacion.getFechar())) {
                    MensajesErrores.advertencia("Fecha de autorización de retención mayor a la fecha hasta de la autorización");
                    return true;
                }
                if (valorTotalActivos + valorTotalSuministros > 0) {
                    double totalCuadre = Math.floor(valorTotalActivos + valorTotalSuministros - sumaTotal);
                    if (totalCuadre != 0) {
                        MensajesErrores.advertencia("Valor total de activos/suministros sobrepasa el valor de la factura");
                        return true;
                    }
                }

                obligacion.setPuntor(documeto.getPunto().getCodigo());
                obligacion.setEstablecimientor(documeto.getPunto().getSucursal().getRuc());
                obligacion.setAutoretencion(documeto.getAutorizacion());
                // buscar numero de retencion 
                Map parametros = new HashMap();
                parametros.put(";where", "o.puntor=:punto and o.establecimientor=:establecimiento and o.numeror=:numero");
                parametros.put("punto", obligacion.getPuntor());
                parametros.put("establecimiento", obligacion.getEstablecimientor());
                parametros.put("numero", obligacion.getNumeror());
                try {
                    int total = ejbObligaciones.contar(parametros);
                    if (obligacion.getId() == null) {
                        if (total > 0) {
                            MensajesErrores.advertencia("Ya existe Número de Retención");
                            return true;
                        }
                    } else {
                        if (total > 1) {
                            MensajesErrores.advertencia("Ya existe Número de Retención");
                            return true;
                        }
                    }
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
//                if ((obligacion.getProveedor().getEmpresa().getEmail() == null) || (obligacion.getProveedor().getEmpresa().getEmail().isEmpty())) {
//                    MensajesErrores.advertencia("Necesario que proveedor cuente con un correo electrónico");
//                    return true;
//                }
            }
        }
        // validar fecha de compromisos igual a la de emision
//        for (Rascompras r : detalles) {
//            if (r.getDetallecompromiso() != null) {
//                if (!(r.getDetallecompromiso().getFecha().equals(obligacion.getFechaemision()))) {
//                    MensajesErrores.advertencia("Fecha de compromiso no corresponde a fecha de emisión");
//                    return true;
//                }
//            }
//        }

        return false;
    }

    // parece cuando es retencion manual
    public String insertar() {

        calculaTotal();
//        if (validar()) {
//            return null;
//        }
        try {
//            obligacion.setEstado(1);
            if (obligacion.getId() == null) {
                obligacion.setProveedor(getCompromiso().getProveedor());
                obligacion.setContrato(getCompromiso().getContrato());
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            } else {
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        grabaObligacion();
        String error = "";
//        try {
//            if ((formularioActivos.getIndiceFila() == 1) || (formularioSuministros.getIndiceFila() == 1)) {
//                error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), numeroFacturaActivos, numeroFacturaSuministro);
//            } else {
//                error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), null, null);
//            }
//        } catch (Exception ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        if (error.contains("ERROR")) {
//            MensajesErrores.advertencia(error);
//            return null;
//        }
        getFormulario().cancelar();
        anticipo = null;
//        MensajesErrores.informacion("Se creo asiento NO:" + error);
//        imprimirSolicitudPago(obligacion);
        // implementacion de retencion manual
//        imprimeRetencionManual();
        // fin retencion manual
        buscar();
        return null;
    }

    // parece cuando es retencion manual
    public String grabar() {

        calculaTotal();
        if (validar()) {
            return null;
        }
        String error = "";
        try {
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            }
//            obligacion.setEstado(1);
            grabaObligacion();
            activos();
            formularioActivos.cancelar();
            suministros();
            formularioSuministros.cancelar();
            if (valorTotalActivos > 0) {
                formularioActivos.setIndiceFila(1);
            }
            if (valorTotalSuministros > 0) {
                formularioSuministros.setIndiceFila(1);
            }
            if (listoContabilizar) {
                if ((formularioActivos.getIndiceFila() == 1) || (formularioSuministros.getIndiceFila() == 1)) {
                    error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), numeroFacturaActivos, numeroFacturaSuministro);
                } else {
                    error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), null, null);
                }
            }
            for (Rascompras r : detalles) {
                Detallecompromiso d = r.getDetallecompromiso();
                if (d != null) {
                    Map parametros = new HashMap();

                    parametros.put(";where", "o.detallecompromiso=:compromiso");
                    parametros.put("compromiso", d);
                    parametros.put(";campo", "o.valor");
                    double sumado = ejbRenglones.sumarCampo(parametros).doubleValue();

                    double valor = d.getValor().doubleValue();
                    d.setSaldo(new BigDecimal(valor - sumado));
                    ejbDetComp.edit(d, seguridadbean.getLogueado().getUserid());
                }
                //                    double valor = d.getValor().doubleValue() + d.getValorimpuesto().doubleValue();
//                    double anterior=det.getSaldo().doubleValue();
//                    det.setSaldo(new BigDecimal(anterior + valor));
            }
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (error.contains("ERROR")) {
            MensajesErrores.advertencia(error);
            return null;
        }
        // recalcular los saldos de los compromisos

        getFormulario().cancelar();
        anticipo = null;
        MensajesErrores.informacion("Se creo asiento NO:" + error);
        imprimirSolicitudPago(obligacion);
        // implementacion de retencion manual
//        imprimeRetencionManual();
        proveedorBean.setProveedor(null);
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        tipoDocumento = null;
        // fin retencion manual
        buscar();
        return null;
    }

    private void imprimeRetencionManual() {
        if (obligacion.getFechar() != null) {
            // implementacion de retencion manual
            DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
            DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Map parametros = new HashMap();
            parametros.put("sres", obligacion.getProveedor().getEmpresa().getNombrecomercial());
            parametros.put("ruc", obligacion.getProveedor().getEmpresa().getRuc());
            parametros.put("direccion", obligacion.getProveedor().getDireccion());
            parametros.put("fecha", sdf.format((obligacion.getFechar())));
            parametros.put("tipo", "FACTURA");
            parametros.put("numero", obligacion.getEstablecimiento() + "-" + obligacion.getPuntoemision() + "-" + df1.format(obligacion.getDocumento()));
//        parametros.put("numero", obligacion.getEstablecimientor() + "" + obligacion.getPuntor() + obligacion.getNumeror());

            List<AuxiliarComprobantes> lista = new LinkedList<>();
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

            Calendar c = Calendar.getInstance();
            c.setTime(configuracionBean.getConfiguracion().getPvigente());
            float total = 0;
            double suma = 0;
            for (Rascompras r : detalles) {
                AuxiliarComprobantes a = new AuxiliarComprobantes();
                suma += r.getValorret().doubleValue() + r.getVretimpuesto().doubleValue();
                if (r.getCba().equals("I")) {
                    if (r.getRetencion() != null) {
                        a.setTipo("RENTA");
//                    a.setTipo(r.getImpuesto().getCodigo().trim());
                        a.setCodigo(r.getRetencion().getEtiqueta().trim());
//                        a.setBase(df.format(r.getValor()));
                        a.setPorcentaje(r.getRetencion().getPorcentaje().toString());
                        if (r.getValorprima() != null) {
                            a.setBase(df.format(r.getValorprima()));
                        } else {
                            a.setBase(df.format(r.getValor()));
                        }
                        a.setEfiscal(String.valueOf(c.get(Calendar.YEAR)));
                        total += r.getValorret().doubleValue();
                        lista.add(a);
                        // flata la retencion del impuesto
                        if (r.getRetimpuesto() != null) {
                            AuxiliarComprobantes a1 = new AuxiliarComprobantes();
                            a1.setTipo(r.getImpuesto().getCodigo().trim());
                            a1.setCodigo(r.getRetimpuesto().getEtiqueta().trim());
                            a1.setBase(df.format(r.getValorimpuesto()));
                            a1.setPorcentaje(r.getRetimpuesto().getPorcentaje().toString());
                            a1.setEfiscal(String.valueOf(c.get(Calendar.YEAR)));
                            total += r.getVretimpuesto().doubleValue();
                            lista.add(a1);
                        }

                    }
                } else if (r.getCba().equals("C")) {
                    if (r.getRetencion() != null) {
                        a.setTipo("RENTA");
//                    a.setTipo(r.getImpuesto().getCodigo().trim());
                        a.setCodigo(r.getRetencion().getEtiqueta().trim());
                        if (r.getValorprima() != null) {
                            a.setBase(df.format(r.getValorprima()));
                        } else {
                            a.setBase(df.format(r.getValor()));
                        }
                        a.setPorcentaje(r.getRetencion().getPorcentaje().toString());
                        a.setValor(r.getValorret().floatValue());
                        a.setEfiscal(String.valueOf(c.get(Calendar.YEAR)));
                        total += r.getValorret().doubleValue();
                        lista.add(a);
                        // flata la retencion del impuesto
                        if (r.getRetimpuesto() != null) {
                            AuxiliarComprobantes a1 = new AuxiliarComprobantes();
                            a1.setTipo(r.getImpuesto().getCodigo().trim());
                            a1.setCodigo(r.getRetimpuesto().getEtiqueta().trim());
                            a1.setBase(df.format(r.getValorimpuesto()));
                            a1.setPorcentaje(r.getRetimpuesto().getPorcentaje().toString());
                            a1.setValor(r.getVretimpuesto().floatValue());
                            a1.setEfiscal(String.valueOf(c.get(Calendar.YEAR)));
                            total += r.getVretimpuesto().doubleValue();
                            lista.add(a1);
                        }
                    }
                }
            }
            if (suma > 0) {
                parametros.put("total", total);
                c = Calendar.getInstance();
                PlantillaRetencion plantilla;
                try {
                    plantilla = new PlantillaRetencion();
                    plantilla.llenar(lista, obligacion);
                    recursoRetencionManual = plantilla.traerRecurso();
                    formularioRetencionManual.insertar();
                } catch (IOException | DocumentException ex) {
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                formulario.cancelar();
            }
        } else {
            formulario.cancelar();
        }
        // fin retencion manual
    }

    public String grabaObligacion() {
        try {

            calculaTotal();
            obligacion.setFechaingreso(new Date());
            obligacion.setTipodocumento(tipoDocumento);
            if (autorizacion != null) {
                obligacion.setPuntoemision(autorizacion.getPuntoemision());
                obligacion.setEstablecimiento(autorizacion.getEstablecimiento());
                obligacion.setAutorizacion(autorizacion.getAutorizacion());
            } else if (autorizacionDocumento != null) {
                obligacion.setPuntoemision(autorizacion.getPuntoemision());
                obligacion.setEstablecimiento(autorizacion.getEstablecimiento());
                obligacion.setAutorizacion(autorizacionDocumento.getAutorizacion());
            }
            if (docElecBean.getDocumento() != null) {
                obligacion.setFacturaelectronica(docElecBean.getDocumento().getXml());
                docElecBean.grabarUtilizado(seguridadbean.getLogueado().getUserid());
            }
            if (docElecBean.getDocumento() != null) {
                obligacion.setFacturaelectronica(docElecBean.getDocumento().getXml());
            }
            if (puntoEmision != null) {
                obligacion.setPuntor(puntoEmision.getCodigo());
                obligacion.setEstablecimientor(puntoEmision.getSucursal().getRuc());

            }
            if (obligacion.getFechar() == null) {
                obligacion.setFechar(obligacion.getFechaemision());
            }
            if (comprobante != null) {
                obligacion.setElectronica(comprobante.toString());
            }
//            obligacion.setEstado(1);
            ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            docElecBean.grabarUtilizado(seguridadbean.getLogueado().getUserid());
            // grabar ras

            // lo mismo para promesas
            for (Pagosvencimientos p : promesas) {
                p.setObligacion(obligacion);
                p.setFecha(obligacion.getFechaemision());
                if (p.getId() == null) {
                    ejbVencimientos.create(p, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbVencimientos.edit(p, seguridadbean.getLogueado().getUserid());
                }
            }
            // borrar
            if (promesasb != null) {
                for (Pagosvencimientos p : promesasb) {
                    if (p.getId() == null) {
                    } else {
                        ejbVencimientos.remove(p, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
        } catch (GrabarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro grabado correctamente");
//        crear();
        proveedorBean.setProveedor(null);
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        tipoDocumento = null;
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            traerRenglones();
            for (Pagosvencimientos p : promesas) {
                ejbVencimientos.remove(p, seguridadbean.getLogueado().getUserid());
            }
            Cabeceras c = null;
            for (Renglones r : renglones) {
                c = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());
            }
            for (Renglones r : renglonesInv) {
                c = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());
            }
            for (Rascompras d : detalles) {
                Detallecompromiso det = d.getDetallecompromiso();
                if (det != null) {
//                    ************************OJO VER RECONSTRUCCION DE SALDO
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso=:compromiso");
                    parametros.put("compromiso", det);
                    parametros.put(";campo", "o.valor");
                    double sumado = ejbRenglones.sumarCampo(parametros).doubleValue();
                    double saldo = det.getValor().doubleValue() - sumado;
                    det.setSaldo(new BigDecimal(saldo));
                    ejbDetComp.edit(det, seguridadbean.getLogueado().getUserid());
                }
                ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
            }
            ejbObligaciones.remove(obligacion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
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

    /**
     * @return the obligaciones
     */
    public LazyDataModel<Obligaciones> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(LazyDataModel<Obligaciones> obligaciones) {
        this.obligaciones = obligaciones;
    }

    /**
     * @return the obligacion
     */
    public Obligaciones getObligacion() {
        return obligacion;
    }

    /**
     * @param obligacion the obligacion to set
     */
    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    /**
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
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
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
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
     * @return the tipoEgreso
     */
    public Tipoegreso getTipoEgreso() {
        return tipoEgreso;
    }

    /**
     * @param tipoEgreso the tipoEgreso to set
     */
    public void setTipoEgreso(Tipoegreso tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
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
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @return the detalles
     */
    public List<Rascompras> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Rascompras> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the promesas
     */
    public List<Pagosvencimientos> getPromesas() {
        return promesas;
    }

    /**
     * @param promesas the promesas to set
     */
    public void setPromesas(List<Pagosvencimientos> promesas) {
        this.promesas = promesas;
    }

    /**
     * @return the pago
     */
    public Pagosvencimientos getPago() {
        return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(Pagosvencimientos pago) {
        this.pago = pago;
    }

    /**
     * @return the detalle
     */
    public Rascompras getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Rascompras detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
    }

    /**
     * @return the formularioPromesas
     */
    public Formulario getFormularioPromesas() {
        return formularioPromesas;
    }

    /**
     * @param formularioPromesas the formularioPromesas to set
     */
    public void setFormularioPromesas(Formulario formularioPromesas) {
        this.formularioPromesas = formularioPromesas;
    }

    public String borraRas(Rascompras r) {
        if (detallesb == null) {
            detallesb = new LinkedList<>();
        }

        detallesb.add(r);
        detalles.remove(formularioDetalle.getFila().getRowIndex());
        calculaTotal();
        return null;
    }

    public String recalcular() {
        calculaTotal();
        return null;
    }

    public String retencionElectronica() {
        if (puntoEmision == null) {
            MensajesErrores.advertencia("Seleccione un punto de emisión");
            return null;
        }
        if (!puntoEmision.getAutomatica()) {
            MensajesErrores.advertencia("No es posible generar retención Automática en punto de venta con numeración manual");
            return null;
        }
        if (validar()) {
            return null;
        }
        if (obligacion.getId() == null) {
            try {

                obligacion.setProveedor(getCompromiso().getProveedor());
                obligacion.setContrato(getCompromiso().getContrato());
                if (docElecBean.getDocumento() != null) {
                    obligacion.setElectronica(docElecBean.getDocumento().getXml());
                }
//            obligacion.setImprimecertificacion(Boolean.FALSE);
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            } catch (InsertarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        calculaTotal();
        grabaObligacion();
        formularioRetencion.insertar();
        return null;
    }

    private double calculaTotal() {
        totales = new LinkedList<>();
        AuxiliarCarga a = new AuxiliarCarga();
        a.setTotal("Compromiso Presupuestario");
        a.setCuenta("Compromiso Presupuestario");
        a.setIngresos(new BigDecimal(BigInteger.ZERO));
//        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Gasto");
        a.setCuenta("Egresos");
        // sumar todos los ras
        double total = 0;
        double vAnticipos = 0;
        double vRetenciones = 0;
        if (detalles == null) {
            detalles = new LinkedList<>();
        }
        double valorCuentas = 0;
        double valorSinCuentas = 0;
        for (Rascompras r : detalles) {
            switch (r.getCba()) {
                case "E":
                    valorCuentas += r.getValor().doubleValue();
                    vRetenciones += r.getValorret().doubleValue();
                    if (r.getImpuesto() != null) {
                        valorCuentas += r.getValorimpuesto().doubleValue();
//                        total += r.getValorimpuesto().doubleValue();
                        vRetenciones += r.getVretimpuesto().doubleValue();
                    }
                    break;
                case "P":
                    valorCuentas += r.getValor().doubleValue();
                    vRetenciones += r.getValorret().doubleValue();
                    if (r.getImpuesto() != null) {
                        valorCuentas += r.getValorimpuesto().doubleValue();
//                        total += r.getValorimpuesto().doubleValue();
                        vRetenciones += r.getVretimpuesto().doubleValue();
                    }
                    break;
                default:
                    total += r.getValor().doubleValue();
                    double valorImpuesto = 0;
                    valorSinCuentas += r.getValorprima() == null ? 0 : r.getValorprima().doubleValue();
                    vRetenciones += r.getValorret().doubleValue();
                    if (r.getImpuesto() != null) {
                        valorImpuesto = r.getValorimpuesto().doubleValue();
                        if (r.getValorprima() != null) {
                            if (r.getBaseimponibleimpuesto() != null) {
                                if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
//                                    total += r.getBaseimponibleimpuesto().doubleValue()
//                                            * (r.getImpuesto().getPorcentaje().doubleValue() / 100);
                                    valorImpuesto = r.getBaseimponibleimpuesto().doubleValue()
                                            * (r.getImpuesto().getPorcentaje().doubleValue() / 100);

                                } else {
                                    valorImpuesto = r.getValorimpuesto().doubleValue();
//                                    total += r.getValorimpuesto().doubleValue();
                                }

                            } else {
                                valorImpuesto = r.getValor().doubleValue() * (r.getImpuesto().getPorcentaje().doubleValue() / 100);
                            }
                        } else {
                            valorImpuesto = r.getValorimpuesto().doubleValue();
                        }
                        total += valorImpuesto;
                        vRetenciones += r.getVretimpuesto() == null ? 0 : r.getVretimpuesto().doubleValue();
                    }
                    break;
            }
        }

        a.setIngresos(new BigDecimal(total));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Retenciones");
        a.setCuenta("Retenciones");
        a.setIngresos(new BigDecimal(vRetenciones));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Valor cuentas sin presupuesto");
        a.setCuenta("Cuentas");
        a.setIngresos(new BigDecimal(Math.abs(valorCuentas)));
        totales.add(a);
//        total += vAnticipos;
        double vPagos = 0;
        a = new AuxiliarCarga();
        a.setTotal("Por Pagar");
        a.setCuenta("Pagos");
        if (promesas == null) {
            promesas = new LinkedList<>();
        } else {
            if (promesasb == null) {
                promesasb = new LinkedList<>();
            }
            for (Pagosvencimientos p : promesas) {
                promesasb.add(p);
            }
            promesas = new LinkedList<>();
            Pagosvencimientos p = new Pagosvencimientos();
            p.setFechapago(new Date());
            p.setPagado(false);
//            p.setValor(new BigDecimal(total + vAnticipos - vRetenciones ));
            p.setValor(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
            promesas.add(p);
        }

        for (Pagosvencimientos p : promesas) {
            vPagos += p.getValor().doubleValue();
        }
        a.setIngresos(new BigDecimal(vPagos));
//        obligacion.setApagar(new BigDecimal(total + vAnticipos - vRetenciones ));
        obligacion.setApagar(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
        totales.add(a);
        if (getFacturaBean().getFactura() != null) {
            // existe valor de factura electrónica
            a = new AuxiliarCarga();
            a.setTotal("Valor Factura");
            a.setCuenta("FACTURA");
            a.setIngresos(new BigDecimal(getFacturaBean().getFactura().getInfoFactura().getImporteTotal()));
            totales.add(a);
        }
        a = new AuxiliarCarga();
        a.setTotal("Total");
        a.setCuenta("Total");
        a.setIngresos(new BigDecimal(total + vAnticipos - vRetenciones - vPagos + valorCuentas));
        totales.add(a);
        // genera retencion
        generaRetElectronica(true, false);
//        if (valorCuentas > 0) {
//            if (valorCuentas - valorSinCuentas != 0) {
//                MensajesErrores.advertencia("Asiento no cuadro valor de cuentas");
//                return valorCuentas - valorSinCuentas;
//            }
//        }
        MensajesErrores.informacion("Totales calculados correctamente");
        return total + vAnticipos - vRetenciones - vPagos + valorCuentas;
    }

    private void generaRetElectronica(boolean documetoActual, boolean codigoAlterno) {
        boolean continua = false;
        if (documetoActual) {
            if ((puntoEmision != null) && (puntoEmision.getAutomatica())) {
                continua = true;
            }
        } else {
            continua = true;
        }
        if (continua) {
            comprobante = new ComprobanteRetencion();
            ComprobanteRetencion.InfoTributaria infoTributariaRetencion;
            infoTributariaRetencion = comprobante.getInfoTributaria();
            infoTributariaRetencion.setAmbiente(configuracionBean.getConfiguracion().getAmbiente());
            infoTributariaRetencion.setTipoEmision("1");
            infoTributariaRetencion.setRazonSocial(configuracionBean.getConfiguracion().getNombre());
            infoTributariaRetencion.setNombreComercial(configuracionBean.getConfiguracion().getNombre());
            infoTributariaRetencion.setRuc(configuracionBean.getConfiguracion().getRuc());

            // falta info de sucursal establecimiento y secuencial que hay que calcular la aclave de acceso
            // ver el codigo del cdocumento
            // de codigos traer la retencion
            Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
            infoTributariaRetencion.setCodDoc(ret.getParametros());
            infoTributariaRetencion.setDirMatriz(configuracionBean.getConfiguracion().getDireccion());
            ComprobanteRetencion.InfoCompRetencion infoRetencion = comprobante.getInfoCompRetencion();
            // colocar un booleano sobre el obligadoa llevar contab
            infoRetencion.setContribuyenteEspecial(configuracionBean.getConfiguracion().getEspecial());
            infoRetencion.setObligadoContabilidad("SI");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            infoRetencion.setFechaEmision(sdf.format(obligacion.getFechaemision()));
            if (puntoEmision != null) {
                if ((obligacion.getProveedor().getDireccion() == null) || (obligacion.getProveedor().getDireccion().isEmpty())) {
                    infoRetencion.setDirEstablecimiento("NO DEFINIDA");
                } else {
                    infoRetencion.setDirEstablecimiento(obligacion.getProveedor().getDireccion());
                }
                infoTributariaRetencion.setEstab(puntoEmision.getSucursal().getRuc());
                if (codigoAlterno) {
                    infoTributariaRetencion.setPtoEmi(puntoEmision.getCodigoalterno());
                    obligacion.setPuntor(puntoEmision.getCodigoalterno());
                } else {
                    infoTributariaRetencion.setPtoEmi(puntoEmision.getCodigo());
                }
            } else {
                if ((obligacion.getProveedor().getDireccion() == null) || (obligacion.getProveedor().getDireccion().isEmpty())) {
                    infoRetencion.setDirEstablecimiento("NO DEFINIDA");
                } else {
                    infoRetencion.setDirEstablecimiento(obligacion.getProveedor().getDireccion());
                }
                infoTributariaRetencion.setEstab(obligacion.getEstablecimientor());
                infoTributariaRetencion.setPtoEmi(obligacion.getPuntor());
            }

            // poner establecimineto y demas
            // buscar retencion
            if (documetoActual) {
                documeto = traerDocumetos();

                Integer numeroActual = (documeto == null ? 0 : documeto.getNumeroactual()) + 1;
                if (documeto == null) {
                    documeto = new Documentos();
                }
                documeto.setNumeroactual(numeroActual);
                infoTributariaRetencion.setSecuencial(numeroActual.toString());
                obligacion.setNumeror(numeroActual);

            } else {
                infoTributariaRetencion.setSecuencial(obligacion.getNumeror().toString());
            }
            comprobante.setInfoTributaria(infoTributariaRetencion);
            comprobante.setInfoCompRetencion(infoRetencion);
//        }
            if (obligacion.getProveedor().getEmpresa().getRuc().length() == 10) {
                infoRetencion.setTipoIdentificacionSujetoRetenido("05");
            } else {
                infoRetencion.setTipoIdentificacionSujetoRetenido("04");
            }
            infoRetencion.setRazonSocialSujetoRetenido(obligacion.getProveedor().getEmpresa().getNombrecomercial());
            infoRetencion.setIdentificacionSujetoRetenido(obligacion.getProveedor().getEmpresa().getRuc());
            SimpleDateFormat sf1 = new SimpleDateFormat("MM/yyyy");
            infoRetencion.setPeriodoFiscal(sf1.format(configuracionBean.getConfiguracion().getPvigente()));
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
            DecimalFormat df1 = new DecimalFormat("000000000", new DecimalFormatSymbols(Locale.US));
            DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
            String docSustento = "";
            if (autorizacion != null) {
                docSustento = autorizacion.getEstablecimiento() + autorizacion.getPuntoemision() + df1.format(obligacion.getDocumento());
            } else {
                // es factura electronoca
                if (getFacturaBean().getFactura() != null) {
                    docSustento = getFacturaBean().getFactura().getInfoTributaria().getEstab()
                            + getFacturaBean().getFactura().getInfoTributaria().getPtoEmi()
                            + getFacturaBean().getFactura().getInfoTributaria().getSecuencial();
                } else {
                    MensajesErrores.advertencia("No existe información de emisión");
                    return;
                }
            }
            String codigoDocSustento = "";
            if (autorizacion != null) {
                codigoDocSustento = autorizacion.getTipodocumento().getNombre();
            } else {
                if (getFacturaBean().getFactura() != null) {
                    codigoDocSustento = getFacturaBean().getFactura().getInfoTributaria().getCodDoc();
//                    codigoDocSustento = getFacturaBean().getFactura().getInfoTributaria().getCodDoc();
                } else {
                    MensajesErrores.advertencia("No existe información de emisión");
                    return;
                }
            }
            for (Rascompras r : detalles) {
                if (r.getCba().equals("I")) {
                    if (r.getRetencion() != null) {
                        comprobante.cargaImpuesto(r.getImpuesto().getEtiqueta().trim(),
                                r.getRetencion().getEtiqueta().trim(), sdf.format(obligacion.getFechaemision()), r.getRetencion().getPorcentaje().intValue(),
                                codigoDocSustento, df.format(r.getValorprima() == null ? r.getValor() : r.getValorprima()),
                                docSustento,
                                df.format(r.getValorret().doubleValue()));
                    }
                    if (r.getRetimpuesto() != null) {
//                        comprobante.cargaImpuesto(configuracionBean.getConfiguracion().getRenta().toString().trim(),
                        comprobante.cargaImpuesto("IVA",
                                //                        comprobante.cargaImpuesto(r.getImpuesto().getEtiqueta().trim(),
                                r.getRetimpuesto().getEtiqueta().trim(),
                                sdf.format(obligacion.getFechaemision()), r.getRetimpuesto().getPorcentaje().intValue(),
                                codigoDocSustento, df.format(r.getValorimpuesto()),
                                docSustento,
                                df.format(r.getVretimpuesto()));
                    }
                } else if (r.getCba().equals("C")) {
                    if (r.getRetencion() != null) {
//                        comprobante.cargaImpuesto(configuracionBean.getConfiguracion().getRenta().toString().trim(),
                        comprobante.cargaImpuesto("RENTA",
                                //                        comprobante.cargaImpuesto(r.getImpuesto().getEtiqueta().trim(),
                                r.getRetencion().getEtiqueta().trim(),
                                sdf.format(obligacion.getFechaemision()), r.getRetencion().getPorcentaje().intValue(),
                                codigoDocSustento, df.format(r.getValorprima() == null ? r.getValor() : r.getValorprima()),
                                docSustento,
                                df.format(r.getValorret().doubleValue()));
                    }
                    if (r.getRetimpuesto() != null) {
//                        comprobante.cargaImpuesto(configuracionBean.getConfiguracion().getRenta().toString().trim(),
                        comprobante.cargaImpuesto("IVA",
                                //                        comprobante.cargaImpuesto(r.getImpuesto().getEtiqueta().trim(),
                                r.getRetimpuesto().getEtiqueta().trim(),
                                sdf.format(obligacion.getFechaemision()), r.getRetimpuesto().getPorcentaje().intValue(),
                                codigoDocSustento, df.format(r.getValorimpuesto()),
                                docSustento,
                                df.format(r.getVretimpuesto().doubleValue()));
                    }
                }
            }
            comprobante.cargaAdicional("email", obligacion.getProveedor().getEmpresa().getEmail() == null ? "NA" : obligacion.getProveedor().getEmpresa().getEmail());
            comprobante.cargaAdicional("telefono", obligacion.getProveedor().getEmpresa().getTelefono1() == null ? "NA" : obligacion.getProveedor().getEmpresa().getTelefono1().toString());
//        comprobante.cargaAdicional("direccion", obligacion.getProveedor().getEmpresa().getDireccionesList().toString());
            if (documetoActual) {
                comprobante.calculaClave(dfAdicional.format(obligacion.getId()));
            } else {
                if (codigoAlterno) {
                    comprobante.calculaClave(dfAdicional.format(obligacion.getId()));
                } else {
                    infoTributariaRetencion.setClaveAcceso(obligacion.getClaver());
                }
            }
            comprobante.setInfoTributaria(infoTributariaRetencion);
            comprobante.setInfoCompRetencion(infoRetencion);
        }
    }

    public Documentos traerDocumetos() {
        if (puntoEmision == null) {
            return null;
        }
        Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
        Map parametros = new HashMap();
        parametros.put(";where", "o.punto=:punto and o.documento=:documento");
        parametros.put("punto", puntoEmision);
        parametros.put("documento", ret);
        try {
            List<Documentos> ld = ejbDocumnetos.encontarParametros(parametros);
            for (Documentos d : ld) {
                return d;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private File grabarXml(String nombre, String xml) {
//    private File grabarXml(String directorio, String nombre, String xml) {

        BufferedWriter writer = null;
        File archivoRetorno = null;
        try {
//            archivoRetorno = File.createTempFile(nombre , ".xml");
            String directorio = System.getProperty("java.io.tmpdir");
            archivoRetorno = new File(directorio + "/" + nombre + ".xml");
//            archivoRetorno = new File(directorio + nombre + ".xml");
//            archivoRetorno = new File(directorio + "/xml/" + comprobante.getInfoTributaria().getClaveAcceso() + ".xml");
            writer = new BufferedWriter(new FileWriter(archivoRetorno));
            writer.write(xml);
        } catch (IOException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
//                        xmlFile=outFile; 
                writer.close();

            } catch (IOException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return archivoRetorno;
    }

    private boolean consutarAutorizacion(String directorio) throws GrabarException, MessagingException, UnsupportedEncodingException {
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(configuracionBean.getConfiguracion().getUrlsri() + "AutorizacionComprobantes?wsdl", comprobante.getInfoTributaria().getClaveAcceso());
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
        if (a.getAutorizacion() == null) {
            return true;
        }
        obligacion.setClaver(comprobante.getInfoTributaria().getClaveAcceso());
        obligacion.setPuntor(comprobante.getInfoTributaria().getPtoEmi());
        obligacion.setEstablecimientor(comprobante.getInfoTributaria().getEstab());
//        obligacion.setEstado(1);
        documeto.setFechaultimo(new Date());
//                documeto.setNumeroactual(documeto.getNumeroactual() + 1);
        if (a.getAutorizacion().isEmpty()) {
            MensajesErrores.informacion("No existe información de Autorización");
            return true;
        }
        if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {
            obligacion.setAutoretencion(a.getAutorizacion().get(0).getNumeroAutorizacion());
            // coloca la Autorizacion en el xml
            String xmlResultado = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                    + "<autorizacion>\n"
                    + "    <estado>"
                    + a.getAutorizacion().get(0).getEstado()
                    + "</estado>\n"
                    + "<numeroAutorizacion>" + a.getAutorizacion().get(0).getNumeroAutorizacion() + "</numeroAutorizacion>\n"
                    + "<fechaAutorizacion>" + a.getAutorizacion().get(0).getFechaAutorizacion() + "</fechaAutorizacion>\n"
                    + "<comprobante><![CDATA[" + a.getAutorizacion().get(0).getComprobante() + "]]></comprobante>\n"
                    + "</autorizacion>";
            obligacion.setElectronica(xmlResultado);
            Date f = ejbObligaciones.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
//                    Long aux=Long.parseLong(comprobante.getInfoTributaria().getClaveAcceso());
            obligacion.setFechar(f);
//            obligacion.setEstado(1);
            // grabar numero en documento
            ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            // enviar correos y cerrar
            generarReporte();

            File archivParaEmail = grabarXml(
                    "Autorizados" + comprobante.getInfoTributaria().getClaveAcceso(),
                    xmlResultado);
            Codigos textoCodigo = codigosBean.traerCodigo("MAIL", "RETENCION");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return true;
            }
            String texto = textoCodigo.getParametros().replace("#proveedor#", obligacion.getProveedor().getEmpresa().getNombrecomercial());
            texto = texto.replace("#fecha#", comprobante.getInfoCompRetencion().getFechaEmision());
            texto = texto.replace("#clave#", comprobante.getInfoTributaria().getClaveAcceso());

            try {
                ejbCorreos.enviarCorreo(obligacion.getProveedor().getEmpresa().getEmail(),
                        textoCodigo.getDescripcion(), texto, pdfFile, archivParaEmail);
//                    "EP SEGURIDAD Informa : Ud ha recibido un documento electrónico", texto, pdfFile, archivParaEmail);
            } catch (MessagingException ex) {
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            String texto = "<p>Estimad@:</p>";
//            texto += "<p><Strong>" + obligacion.getProveedor().getEmpresa().getNombrecomercial() + "</Strong></p>";
//            texto += "<p>Continuando con el compromiso de protección del medio ambiente y "
//                    + "mantener un servicio de calidad, adjuntamos su Retención Electrónica No:<Strong> "
//                    + comprobante.getInfoTributaria().getClaveAcceso() + "</Strong> emitida el <Strong>" + comprobante.getInfoCompRetencion().getFechaEmision() + " </Strong></p>";
//            texto += "<p></p><p></p><p></p><p></p>";
//            texto += "<p><Strong>Nota:</Strong> El envío de este correo es automático, por favor no lo responda.";
//
//            ejbCorreos.enviarCorreo(obligacion.getProveedor().getEmpresa().getEmail(),
//                    "EP SEGURIDAD Informa : Ud ha recibido un documento electrónico", texto, pdfFile, archivParaEmail);
//            // contabilizar
            String error = "";
            try {
                if (formularioActivos.getIndiceFila() == 1) {
                    error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), numeroFacturaActivos, numeroFacturaSuministro);
                } else {
                    error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), null, null);
                }
            } catch (Exception ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (error.contains("ERROR")) {
                MensajesErrores.advertencia(error);
                formulario.cancelar();
                formularioRetencion.cancelar();
                return true;
            }
            MensajesErrores.informacion(error);
        } else {
            formularioRetencion.cancelar();
            MensajesErrores.advertencia(a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional());
            return true;
        }
//                prueba = rcomprobante.getAutorizaciones().toString();
        return false;
    }

    private boolean crearArchivoFirmado() {
        generaRetElectronica(false, true);
        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
        File archivoTrabajo = grabarXml(
                "XML" + comprobante.getInfoTributaria().getClaveAcceso(),
                comprobante.toString());
        try {
            File archivoP12 = new File(configuracionBean.getConfiguracion().getDirectorio() + "/firma.p12");
            String clave = configuracionBean.getConfiguracion().getClave();
            FirmaXAdESBESS.firmar(archivoP12, archivoTrabajo, clave, directorio + "/firmados", comprobante.getInfoTributaria().getClaveAcceso() + ".xml");
//
            Path path = Paths.get(directorio + "/firmados/" + comprobante.getInfoTributaria().getClaveAcceso() + ".xml");
//
            DOMUtils.doTrustToCertificates();
            byte[] data = Files.readAllBytes(path);
            boolean conexion = ejbFirmador.existeConexion(configuracionBean.getConfiguracion().getUrlsri() + "RecepcionComprobantes?wsdl");
            if (!conexion) {
                MensajesErrores.fatal("No existe conexión");
                return true;
            }
            RespuestaSolicitud respuesta = ejbFirmador.transmitir(configuracionBean.getConfiguracion().getUrlsri() + "RecepcionComprobantes?wsdl", data);
//            String prueba = respuesta.getComprobantes().toString();
            if (respuesta.getEstado().equals("RECIBIDA")) {
                boolean si = consutarAutorizacion(directorio);
                if (si) {
                    return true;
                }
                ejbDocumnetos.edit(documeto, seguridadbean.getLogueado().getUserid());
            }
        } catch (KeyStoreException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String cambiarRetencion() {
        if (obligacion.getNumeror() != null) {
            try {
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            } catch (GrabarException ex) {
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public String autorizarSri() {
//        keyStore=configuracionBean.getKeyStore();
//        if (getKeyStore() == null) {
//            MensajesErrores.advertencia("No se ha seleccionado archivo de firma");
//            return null;
//        }
//        if ((clave == null) || (clave.isEmpty())) {
//            MensajesErrores.advertencia("es necesario la clave");
//            return null;
//        }
        grabaObligacion();
        ///////////////////////VER BASE IMPONIBLE CON LA PRIMA
        String directorio = System.getProperty("java.io.tmpdir");//"/home/edwin/Escritorio/comprobantes/";
        String nombreArchivo = comprobante.getInfoTributaria().getClaveAcceso();
//        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
        File archivoTrabajo = grabarXml(
                nombreArchivo,
                comprobante.toString());
        try {
//            File keFile=configuracionBean.getKeyStore();
            File archivoP12 = new File(configuracionBean.getConfiguracion().getDirectorio() + "/firma.p12");
            String clave = configuracionBean.getConfiguracion().getClave();
            FirmaXAdESBESS.firmar(archivoP12, archivoTrabajo, clave, directorio, "Firmado" + nombreArchivo + ".xml"
            );
//            FirmaXAdESBESS.firmar(getKeyStore(), archivoTrabajo, clave, directorio + "/firmados", "firmado" + ".xml");
//            Path path = Paths.get(directorio + "/firmados/" + "sinfirmar" + ".xml");
            Path path = Paths.get(directorio + "/Firmado" + nombreArchivo + ".xml");

            DOMUtils.doTrustToCertificates();
            byte[] data = Files.readAllBytes(path);
            boolean conexion = ejbFirmador.
                    existeConexion(configuracionBean.getConfiguracion().getUrlsri() + "RecepcionComprobantesOffline?wsdl");
            if (!conexion) {
                MensajesErrores.fatal("No existe conexión");
                return null;
            }
            RespuestaSolicitud respuesta = ejbFirmador.transmitir(configuracionBean.getConfiguracion().getUrlsri() + "RecepcionComprobantesOffline?wsdl", data);
//            RespuestaSolicitud respuesta = ejbFirmador.transmitir(configuracionBean.getConfiguracion().getUrlsri(), data);
//            String prueba = respuesta.getComprobantes().toString();

            if (respuesta.getEstado().equals("RECIBIDA")) {
//                boolean si = consutarAutorizacion(directorio);
//                if (si) {
//                    return null;
//                }
                obligacion.setClaver(comprobante.getInfoTributaria().getClaveAcceso());
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
                ejbDocumnetos.edit(documeto, seguridadbean.getLogueado().getUserid());
            } else {
                String auxError = respuesta.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje();
                if (auxError.contains("ERROR SECUENCIAL REGISTRADO")) {
                    MensajesErrores.informacion(auxError);
                    // generara auorizacón con pv alterno volver a firmar y generar todo
                    boolean si = crearArchivoFirmado();
                    if (si) {
                        return null;
                    }

                } else if (auxError.contains("CLAVE ACCESO REGISTRADA")) {
                    MensajesErrores.informacion(auxError);
                    boolean si = consutarAutorizacion(directorio);
                    if (si) {
                        return null;
                    }
                } else {
                    formularioRetencion.cancelar();
                    MensajesErrores.advertencia(auxError + " ["
                            + respuesta.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional() + "]");
                    return null;
                }
            }
            // luego hay que transmitir 
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("error").log(Level.SEVERE, null, ex);
            return null;
        }
        formularioRetencion.cancelar();

        formulario.cancelar();
//        obligacion.setEstado(1);
//        try {
//            ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
//        } catch (GrabarException ex) {
//            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        imprimirSolicitudPago(obligacion);

        // imprimir Egreso
        return null;
    }

    /**
     * @return the detallesInternos
     */
    public List<Rascompras> getDetallesInternos() {
        return detallesInternos;
    }

    /**
     * @param detallesInternos the detallesInternos to set
     */
    public void setDetallesInternos(List<Rascompras> detallesInternos) {
        this.detallesInternos = detallesInternos;
    }

    public String nuevoDetalle() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        detalle = new Rascompras();
        detalle.setBien(Boolean.FALSE);
        detallesInternos = new LinkedList<>();
//        formularioCompromiso.insertar();
        formularioDetalle.insertar();
        soloRetencion = false;
        detalleModificar = new Rascompras();
        detalleModificar.setValor(BigDecimal.ZERO);
        detalleModificar.setValorimpuesto(BigDecimal.ZERO);
        iva = false;
        return null;
    }

    public String nuevoDetalleCuenta() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        detalle = new Rascompras();
        detalle.setBien(Boolean.FALSE);
        detallesInternos = new LinkedList<>();
//        formularioCompromiso.insertar();
        cuentaProveedor = null;
        formularioDetalleCuenta.insertar();
        detalleModificar = new Rascompras();
        detalleModificar.setValor(BigDecimal.ZERO);
        soloRetencion = false;
        iva = false;
        return null;
    }

    public String modificaDetalle(Integer fila) {
        formularioDetalle.setIndiceFila(fila);
        detalle = detalles.get(fila);
        iva = false;
        double valorComparar = Math.round((detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue()) * 100) / 100;
        detalleModificar = new Rascompras();
//        esta en el anterior
        detalleModificar.setValor(new BigDecimal(valorComparar));
//        detalleModificar.setValor(detalle.getValor());
        detalleModificar.setValorimpuesto(detalle.getValorimpuesto());

        if (detalle.getValorprima() == null) {
            detalle.setValorprima(BigDecimal.ZERO);
        }
        detalleModificar.setValorprima(detalle.getValorprima());
        if (detalle.getValorprima() == null) {
            soloRetencion = false;
        } else if (detalle.getValorprima().doubleValue() == 0) {
            soloRetencion = false;
        } else {
            soloRetencion = true;
        }
//        soloRetencion = (!(detalle.getValorprima() == null) || (detalle.getValorprima().doubleValue() == 0));

        if (detalle.getDetallecompromiso() == null) {
            formularioDetalleCuenta.editar();
            // ver el tipo
            if (detalle.getCba().equals("E")) {
                empleado = true;
                Entidades e = entidadesBean.traerCedula(detalle.getCc());
                if (e != null) {
                    entidadesBean.setApellidos(e.getApellidos());
                    entidadesBean.setEntidad(e);
                }
            } else {
                empleado = false;
            }
//            cuenta del proveedor
            cuentaProveedor = cuentasBean.traerCodigo(detalle.getCuenta());
        } else {
            formularioDetalle.editar();
        }

        return null;
    }

    public String eliminaDetalle(Integer fila) {
        if (detallesb == null) {
            detallesb = new LinkedList<>();
        }
        formularioDetalle.setIndiceFila(fila);
        detalle = detalles.get(fila);
        if (detalle.getCba().equals("A")) {
            detallesb.add(detalle);
            detalles.remove(formularioDetalle.getFila().getRowIndex());
            return null;
        }
        detallesInternos = new LinkedList<>();
        int i = 0;
        double valorComparar = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
        detalleModificar = new Rascompras();
        detalleModificar.setValor(new BigDecimal(valorComparar));
        if (detalle.getDetallecompromiso() == null) {
            formularioDetalleCuenta.eliminar();
            if (detalle.getCba().equals("E")) {
                empleado = true;
                Entidades e = entidadesBean.traerCedula(detalle.getCuenta());
                if (e != null) {
                    entidadesBean.setApellidos(e.getApellidos());
                    entidadesBean.setEntidad(e);
                }
            } else {
                empleado = false;
            }
        } else {
            formularioDetalle.eliminar();
        }

        return null;
    }

    public boolean validarDetalle() {
        if ((detalle.getDetallecompromiso() == null)) {
            MensajesErrores.advertencia("Seleccione una cuenta de presupuesto");
            return true;
        }
        if (soloRetencion) {
            if ((detalle.getValorprima() == null) || (detalle.getValorprima().doubleValue() <= 0)) {
                MensajesErrores.advertencia("Valor de prima obligatorio");
                return true;
            }
            if ((detalle.getValorret() == null) || (detalle.getValorret().doubleValue() <= 0)) {
                MensajesErrores.advertencia("Valor retención obligatorio");
                return true;
            }
        } else {
            if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
                MensajesErrores.advertencia("Valor obligatorio");
                return true;
            }
        }
        if ((detalle.getReferencia() == null) || (detalle.getReferencia().isEmpty())) {
            MensajesErrores.advertencia("Referencia es  obligatorio");
            return true;
        }
        double valorCompromiso = detalle.getDetallecompromiso().getValor().doubleValue();
        double valorSaldo = detalle.getDetallecompromiso().getSaldo().doubleValue();
        double valorComparar = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
        double valorAnterior = detalleModificar.getValor().doubleValue() + detalleModificar.getValorimpuesto().doubleValue();
        double total = valorSaldo - valorComparar + valorAnterior;
        if (Math.round(total) < 0) {
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            String enMensaje = " Saldo Compromiso = " + df.format(valorSaldo);
            enMensaje += " Valor renglón = " + df.format(valorComparar);
            enMensaje += " Diferencia = " + df.format(Math.round(total));
            MensajesErrores.advertencia("Valor sobrepasa el compromiso :" + enMensaje);
            return true;
        }
        return false;
    }

    public boolean validarDetalleCuenta() {

        if (soloRetencion) {
            if ((detalle.getValorret() == null) || (detalle.getValorret().doubleValue() <= 0)) {
                MensajesErrores.advertencia("Valor retención obligatorio");
                return true;
            }
            detalle.setValor(BigDecimal.ZERO);
        } else {
            if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
                MensajesErrores.advertencia("Valor obligatorio");
                return true;
            }
        }
        if ((detalle.getReferencia() == null) || (detalle.getReferencia().isEmpty())) {
            MensajesErrores.advertencia("Referencia es  obligatorio");
            return true;
        }
        if ((detalle.getCuenta() == null) || (detalle.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Auxiliar es obligatorio");
            return true;
        }
        if (detalle.getIdcuenta() == null) {
            MensajesErrores.advertencia("Cuenta es obligatorio");
            return true;
        }
        return false;
    }

    public String insertarDetalle() {
        if (detalles == null) {
            detalles = new LinkedList<>();
        }
        if (detalle.getIdcuenta() == null) {
            MensajesErrores.advertencia("Seleccione una cuenta contable");
            return null;
        }
        detalle.setCba("C");
        detalle.setCuenta(detalle.getIdcuenta().getCodigo());
//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
        if (iva) {
            if (detalle.getImpuesto() != null) {
                double valor = detalle.getValor().doubleValue()
                        / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
                detalle.setValor(new BigDecimal(valor));
            }
        }
        if (detalle.getRetencion() != null) {
            if (!soloRetencion) {
                detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
                        * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
            } else {
                detalle.setValorret(new BigDecimal(detalle.getValorprima().doubleValue()
                        * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
            }
        } else {

            detalle.setValorret(BigDecimal.ZERO);
        }
        if (detalle.getImpuesto() != null) {
            if (!soloRetencion) {
                double impuestoDoble = Math.round(detalle.getValor().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            } else {
//                ************************************+AUMENTADO EL CALCULO DEL IMPUESTO CON EL VALOR DEL SEGURO
                double impuestoDoble = Math.round(detalle.getValorprima().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            }
        } else {
            detalle.setValorimpuesto(BigDecimal.ZERO);
            detalle.setVretimpuesto(BigDecimal.ZERO);
            detalle.setRetimpuesto(null);
        }
        if (validarDetalle()) {
            return null;
        }
        obligacion.setFechaingreso(new Date());
        try {
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            }
            detalle.setObligacion(obligacion);
            ejbDetalles.create(detalle, seguridadbean.getLogueado().getUserid());
            Detallecompromiso det = detalle.getDetallecompromiso();
            if (!soloRetencion) {
                double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
                double valorImpuesto = 0;
                if (detalle.getImpuesto() != null) {
                    valorImpuesto = detalle.getValor().doubleValue()
                            * detalle.getImpuesto().getPorcentaje().doubleValue() / 100;
                    if (detalle.getBaseimponibleimpuesto() != null) {
                        if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valorImpuesto = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                    * detalle.getImpuesto().getPorcentaje().doubleValue());
                        }
                    }
                }
                valor = detalle.getValor().doubleValue() + valorImpuesto;
                det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() - valor));

            } else {
                double valorImpuesto = 0;
                if (detalle.getImpuesto() != null) {
                    valorImpuesto = detalle.getValor().doubleValue()
                            * detalle.getImpuesto().getPorcentaje().doubleValue() / 100;
                    if (detalle.getBaseimponibleimpuesto() != null) {
                        if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valorImpuesto = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                    * detalle.getImpuesto().getPorcentaje().doubleValue());
                        }
                    }
                }
                double valor = detalle.getValor().doubleValue() + valorImpuesto;
                det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() - valor));
            }
            ejbDetComp.edit(det, seguridadbean.getLogueado().getUserid());
            detalles.add(detalle);
            // grabar el detalle

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formularioDetalle.cancelar();
        calculaTotal();
        return null;
    }

    public String insertarDetalleCuenta() {
        if (detalles == null) {
            detalles = new LinkedList<>();
        }
        if (cuentaProveedor == null) {
            MensajesErrores.advertencia("Seleccione una cuenta del proveedor");
            return null;
        }
        detalle.setCuenta(cuentaProveedor.getCodigo());
        if (isEmpleado()) {
            detalle.setCba("E");
            detalle.setCc(entidadesBean.getEntidad().getPin());
        } else {
            detalle.setCba("P");
            detalle.setCc(obligacion.getProveedor().getEmpresa().getRuc());
        }

//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
        if (iva) {
            if (detalle.getImpuesto() != null) {
                double valor = detalle.getValor().doubleValue() / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
                detalle.setValor(new BigDecimal(valor));
            }
        }
        if (detalle.getRetencion() != null) {
            detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
                    * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
        } else {
            detalle.setValorret(BigDecimal.ZERO);
        }
        if (detalle.getImpuesto() != null) {
            if (!soloRetencion) {
                double impuestoDoble = Math.round(detalle.getValor().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            } else {
//                ************************************+AUMENTADO EL CALCULO DEL IMPUESTO CON EL VALOR DEL SEGURO
                double impuestoDoble = Math.round(detalle.getValorprima().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            }
        } else {
            detalle.setValorimpuesto(BigDecimal.ZERO);
            detalle.setVretimpuesto(BigDecimal.ZERO);
            detalle.setRetimpuesto(null);
        }
        if (validarDetalleCuenta()) {
            return null;
        }
        obligacion.setFechaingreso(new Date());
        try {
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            }
            detalle.setObligacion(obligacion);
            ejbDetalles.create(detalle, seguridadbean.getLogueado().getUserid());
            detalles.add(detalle);
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        formularioDetalleCuenta.cancelar();
        calculaTotal();
        return null;
    }

    public String grabarDetalle() {

        detalle.setCba("C");
        detalle.setCuenta(detalle.getIdcuenta().getCodigo());
        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
        if (iva) {
            if (detalle.getImpuesto() != null) {
                double valor = detalle.getValor().doubleValue() / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
                detalle.setValor(new BigDecimal(valor));
            }
        }
        if (detalle.getRetencion() != null) {
            if (!soloRetencion) {
                detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
                        * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
            } else {
                detalle.setValorret(new BigDecimal(detalle.getValorprima().doubleValue()
                        * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
            }
        } else {
            detalle.setValorret(BigDecimal.ZERO);
        }
        if (detalle.getImpuesto() != null) {
            if (!soloRetencion) {
                double impuestoDoble = Math.round(detalle.getValor().doubleValue() * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            } else {
//                ************************************+AUMENTADO EL CALCULO DEL IMPUESTO CON EL VALOR DEL SEGURO
                double impuestoDoble = Math.round(detalle.getValorprima().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            }
        } else {
            detalle.setValorimpuesto(BigDecimal.ZERO);
            detalle.setVretimpuesto(BigDecimal.ZERO);
            detalle.setRetimpuesto(null);
        }
        if (validarDetalle()) {
            return null;
        }
        try {
//            OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOJOOOOOOOOOOOOOOOOOOOOOOOOOOOO
            ejbDetalles.edit(detalle, seguridadbean.getLogueado().getUserid());
            Detallecompromiso det = detalle.getDetallecompromiso();
            if (!soloRetencion) {
                double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
                double valorImpuesto = 0;
                if (detalle.getImpuesto() != null) {
                    valorImpuesto = detalle.getValor().doubleValue()
                            * detalle.getImpuesto().getPorcentaje().doubleValue() / 100;
                    if (detalle.getBaseimponibleimpuesto() != null) {
                        if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valorImpuesto = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                    * detalle.getImpuesto().getPorcentaje().doubleValue());
                        }
                    }
                }
                valor = detalle.getValor().doubleValue() + valorImpuesto;
                double anterior = detalleModificar.getValor().doubleValue();
                det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() - valor + anterior));
            } else {
                double valorImpuesto = 0;
                if (detalle.getImpuesto() != null) {
                    valorImpuesto = detalle.getValor().doubleValue()
                            * detalle.getImpuesto().getPorcentaje().doubleValue() / 100;
                    if (detalle.getBaseimponibleimpuesto() != null) {
                        if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valorImpuesto = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                    * detalle.getImpuesto().getPorcentaje().doubleValue());
                        }
                    }
                }
                double valor = detalle.getValor().doubleValue() + valorImpuesto;
                valorImpuesto = 0;
                if (detalleModificar.getImpuesto() != null) {
                    valorImpuesto = detalleModificar.getValor().doubleValue()
                            * detalleModificar.getImpuesto().getPorcentaje().doubleValue() / 100;
                    if (detalle.getBaseimponibleimpuesto() != null) {
                        if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valorImpuesto = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                    * detalle.getImpuesto().getPorcentaje().doubleValue());
                        }
                    }
                }
                double anterior = detalleModificar.getValor().doubleValue() + valorImpuesto;
                det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() - valor + anterior));
            }
            ejbDetComp.edit(det, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        detalles.set(formularioDetalle.getIndiceFila(), detalle);
        calculaTotal();
        formularioDetalle.cancelar();
        return null;
    }

    public String grabarDetalleCuenta() {
        if (cuentaProveedor == null) {
            MensajesErrores.advertencia("Seleccione una cuenta del proveedor");
            return null;
        }
        detalle.setCuenta(cuentaProveedor.getCodigo());
        if (isEmpleado()) {
            detalle.setCba("E");
            detalle.setCc(entidadesBean.getEntidad().getPin());
        } else {
            detalle.setCba("P");
            detalle.setCc(obligacion.getProveedor().getEmpresa().getRuc());
        }
//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
        if (iva) {
            if (detalle.getImpuesto() != null) {
                double valor = detalle.getValor().doubleValue() / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
                detalle.setValor(new BigDecimal(valor));
            }
        }
        if (detalle.getRetencion() != null) {
            if (!soloRetencion) {
                detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
                        * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
            } else {
                detalle.setValorret(new BigDecimal(detalle.getValorprima().doubleValue()
                        * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
            }
        } else {
            detalle.setValorret(BigDecimal.ZERO);
        }
        if (detalle.getImpuesto() != null) {
            if (!soloRetencion) {
                double impuestoDoble = Math.round(detalle.getValor().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            } else {
//                ************************************+AUMENTADO EL CALCULO DEL IMPUESTO CON EL VALOR DEL SEGURO
                double impuestoDoble = Math.round(detalle.getValorprima().doubleValue()
                        * detalle.getImpuesto().getPorcentaje().doubleValue());
                if (detalle.getBaseimponibleimpuesto() != null) {
                    if (detalle.getBaseimponibleimpuesto().doubleValue() > 0) {
                        impuestoDoble = Math.round(detalle.getBaseimponibleimpuesto().doubleValue()
                                * detalle.getImpuesto().getPorcentaje().doubleValue());
                    }
                }
                detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                if (detalle.getRetimpuesto() != null) {
                    double retimp = Math.round(impuestoDoble / 100
                            * detalle.getRetimpuesto().getPorcentaje().doubleValue());
                    detalle.setVretimpuesto(new BigDecimal(retimp / 100));
                }
            }
        } else {
            detalle.setValorimpuesto(BigDecimal.ZERO);
            detalle.setVretimpuesto(BigDecimal.ZERO);
            detalle.setRetimpuesto(null);
        }
        if (validarDetalleCuenta()) {
            return null;
        }
        try {

            ejbDetalles.edit(detalle, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        detalles.set(formularioDetalle.getIndiceFila(), detalle);
        calculaTotal();
        formularioDetalleCuenta.cancelar();
        return null;
    }

    public String borrarDetalle() {
        if (detallesb == null) {
            detallesb = new LinkedList<>();
        }
        detallesb.add(detalle);
        detalles.remove(formularioDetalle.getIndiceFila());
        try {

            Detallecompromiso det = detalle.getDetallecompromiso();
            double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
            det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() + valor));
            ejbDetComp.edit(det, seguridadbean.getLogueado().getUserid());
            ejbDetalles.remove(detalle, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formularioDetalle.cancelar();
        calculaTotal();
        return null;
    }

    public String borrarDetalleCuenta() {
        if (detallesb == null) {
            detallesb = new LinkedList<>();
        }
        detallesb.add(detalle);
        detalles.remove(formularioDetalle.getIndiceFila());
        try {

            ejbDetalles.remove(detalle, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formularioDetalleCuenta.cancelar();
        calculaTotal();
        return null;
    }

    /**
     * @return the impuesto
     */
    public Impuestos getImpuesto() {
        return impuesto;
    }

    /**
     * @param impuesto the impuesto to set
     */
    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    /**
     * @return the retencion
     */
    public Retenciones getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retenciones retencion) {
        this.retencion = retencion;
    }

    /**
     * @return the formularioInterno
     */
    public Formulario getFormularioInterno() {
        return formularioInterno;
    }

    /**
     * @param formularioInterno the formularioInterno to set
     */
    public void setFormularioInterno(Formulario formularioInterno) {
        this.formularioInterno = formularioInterno;
    }

    public void cambiaPunto(ValueChangeEvent ve) {
        Puntoemision valor = (Puntoemision) ve.getNewValue();
        comprobante = null;
        if (valor.getAutomatica()) {
            puntoEmision = valor;
            calculaTotal();
        }
    }

    public void cambiaDocumento(ValueChangeEvent ve) {
        Documentos valor = (Documentos) ve.getNewValue();
        if (valor != null) {
            obligacion.setNumeror(valor.getNumeroactual() + 1);
        }
    }

    /**
     * @return the anticipo
     */
    public Anticipos getAnticipo() {
        return anticipo;
    }

    /**
     * @param anticipo the anticipo to set
     */
    public void setAnticipo(Anticipos anticipo) {
        this.anticipo = anticipo;
    }

    /**
     * @return the valorAnticipo
     */
    public double getValorAnticipo() {
        return valorAnticipo;
    }

    /**
     * @param valorAnticipo the valorAnticipo to set
     */
    public void setValorAnticipo(double valorAnticipo) {
        this.valorAnticipo = valorAnticipo;
    }

    /**
     * @return the totales
     */
    public List<AuxiliarCarga> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCarga> totales) {
        this.totales = totales;
    }

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
    }

    /**
     * @return the autorizacion
     */
    public Autorizaciones getAutorizacion() {
        return autorizacion;
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(Autorizaciones autorizacion) {
        this.autorizacion = autorizacion;
    }

    public void xmlListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        File xmlFileFactura;
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                xmlFileFactura = i.getFile();
                // trarer los xml y grabarlos
                String xmlStr = "";
                String sb;
                try {
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFileFactura), "UTF-8"));
                    while ((sb = entrada.readLine()) != null) {
                        xmlStr += sb;
                    }
                    obligacion.setFacturaelectronica(xmlStr);
                    getFacturaBean().cargar(xmlStr);
                    //
                } catch (IOException ex) {
                    MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                    Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } //fin if
        }// fin for
    }

//    public void p12lListener(FileEntryEvent e) {
//        FileEntry fe = (FileEntry) e.getComponent();
//        FileEntryResults results = fe.getResults();
//        File parent = null;
//
//        for (FileEntryResults.FileInfo i : results.getFiles()) {
//            if (i.isSaved()) {
//                setKeyStore(i.getFile());
//            } //fin if
//        }// fin for
//    }
    /**
     * @return the facturaBean
     */
    public FacturaSriBean getFacturaBean() {
        return facturaBean;

    }

    /**
     * @param facturaBean the facturaBean to set
     */
    public void setFacturaBean(FacturaSriBean facturaBean) {
        this.facturaBean = facturaBean;
    }

    public String booraFacturaElectronica() {
        obligacion.setFacturaelectronica(null);
        getFacturaBean().setFactura(null);
        return null;
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

    /**
     * @return the comprobante
     */
    public ComprobanteRetencion getComprobante() {
        return comprobante;
    }

    /**
     * @param comprobante the comprobante to set
     */
    public void setComprobante(ComprobanteRetencion comprobante) {
        this.comprobante = comprobante;
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

    /**
     * @return the formularioRetencion
     */
    public Formulario getFormularioRetencion() {
        return formularioRetencion;
    }

    /**
     * @param formularioRetencion the formularioRetencion to set
     */
    public void setFormularioRetencion(Formulario formularioRetencion) {
        this.formularioRetencion = formularioRetencion;
    }

    /**
     * @return the clave
     */
//    public String getClave() {
//        return clave;
//    }
//
//    /**
//     * @param clave the clave to set
//     */
//    public void setClave(String clave) {
//        this.clave = clave;
//    }
//    /**
//     * @return the keyStore
//     */
//    public File getKeyStore() {
//        return keyStore;
//    }
//
//    /**
//     * @param keyStore the keyStore to set
//     */
//    public void setKeyStore(File keyStore) {
//        this.keyStore = keyStore;
//    }
    public SelectItem[] getComboDocumentos() {
        if (puntoEmision == null) {
            return null;
        }
        Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
        Map parametros = new HashMap();
        parametros.put(";where", "o.punto=:punto and o.documento=:documento");
        parametros.put("punto", puntoEmision);
        parametros.put("documento", ret);
        try {
            return Combos.getSelectItems(ejbDocumnetos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DocumentosEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the documeto
     */
    public Documentos getDocumeto() {
        return documeto;
    }

    /**
     * @param documeto the documeto to set
     */
    public void setDocumeto(Documentos documeto) {
        this.documeto = documeto;
    }

    private void generarReporte() {
        setRecursoPdf(null);
        InputStream isLogo = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            List<ComprobanteRetencion.Impuesto> li = comprobante.getImpuestos();
            List<Adicionales> la = new LinkedList<>();
            List<infoTributaria> listaInfo = new LinkedList<>();
            String rucComprador = "";
            for (ComprobanteRetencion.CampoAdicional a : comprobante.getInfoAdicional()) {
                Adicionales a1 = new Adicionales();
                a1.setNombre(a.getNombre());
                a1.setValor(a.getContent());
                la.add(a1);
            }
            for (ComprobanteRetencion.Impuesto i : li) {
                infoTributaria it = new infoTributaria();
                it.setInfoAdicional(la);
                it.setBaseImponible(i.getBaseImponible());
                it.setValorRetenido(i.getValorRetenido());
                it.setNombreComprobante(i.getCodDocSustento());
                it.setNumeroComprobante(i.getNumDocSustento());
                it.setNombreImpuesto(i.getCodigo());
                it.setPorcentajeRetener(i.getPorcentajeRetener());
                it.setFechaEmisionCcompModificado(i.getFechaEmisionDocSustento());
                listaInfo.add(it);
            }

            Map parametros = new HashMap();
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
            File logo = new File(realPath + "/logo-new.png");
            isLogo = new FileInputStream(logo);
            parametros.put("RUC", configuracionBean.getConfiguracion().getRuc());
            parametros.put("NUM_AUT", obligacion.getAutoretencion() == null ? comprobante.getInfoTributaria().getClaveAcceso() : obligacion.getAutoretencion());
            parametros.put("FECHA_AUT", sdf.format(obligacion.getFechar()));
            parametros.put("TIPO_EMISION", "NORMAL");
            parametros.put("RAZON_SOCIAL", configuracionBean.getConfiguracion().getNombre());
            parametros.put("DIR_MATRIZ", configuracionBean.getConfiguracion().getDireccion());
            parametros.put("CONT_ESPECIAL", configuracionBean.getConfiguracion().getEspecial());//"00162"
            parametros.put("LLEVA_CONTABILIDAD", comprobante.getInfoCompRetencion().getObligadoContabilidad());
            parametros.put("RUC_COMPRADOR", comprobante.getInfoCompRetencion().getIdentificacionSujetoRetenido());
            parametros.put("RS_COMPRADOR", comprobante.getInfoCompRetencion().getRazonSocialSujetoRetenido());
            parametros.put("SUBREPORT_DIR", realPath + "/");
            parametros.put("FECHA_EMISION", comprobante.getInfoCompRetencion().getFechaEmision());
            parametros.put("CLAVE_ACC", comprobante.getInfoTributaria().getClaveAcceso());
            if (getFacturaBean().getFactura() == null) {
//                parametros.put("NUM_FACT", obligacion.getDocumento().toString());
                parametros.put("NUM_FACT", obligacion.getEstablecimientor() + "-"
                        + obligacion.getPuntor() + "-" + comprobante.getInfoTributaria().getSecuencial());
            } else {
                parametros.put("NUM_FACT", getFacturaBean().getFactura().getInfoTributaria().getSecuencial());
            }
            parametros.put("EJERCICIO_FISCAL", comprobante.getInfoCompRetencion().getPeriodoFiscal());
            parametros.put("AMBIENTE", comprobante.getInfoTributaria().getAmbiente());
            parametros.put("NOM_COMERCIAL", comprobante.getInfoTributaria().getNombreComercial());
            String img1 = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/logo-new.png");
            parametros.put("firma1", img1);
//            parametros.put("GUIA", documento.getPeriodofiscal());
            Calendar c = Calendar.getInstance();
            setRecursoPdf(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/comprobanteRetencion.jasper"),
                    listaInfo, "R" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
//                    listaInfo, documento.getClaveadecceso() + ".pdf");
//            InputStream is = getRecursoPdf().getInputStream();
////            byte[] bytes = IOUtils.toByteArray(is);
//            pdfFile = stream2file(is, comprobante.getInfoTributaria().getClaveAcceso(), ".pdf");
//            mostrarArchivos = true;
        } catch (FileNotFoundException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                isLogo.close();
            } catch (IOException ex) {
                Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static File stream2file(InputStream in, String nombre, String extencion) throws IOException {
        final File tempFile = File.createTempFile(nombre, extencion);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
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
     * @return the formularioCompromiso
     */
    public Formulario getFormularioCompromiso() {
        return formularioCompromiso;
    }

    /**
     * @param formularioCompromiso the formularioCompromiso to set
     */
    public void setFormularioCompromiso(Formulario formularioCompromiso) {
        this.formularioCompromiso = formularioCompromiso;
    }

    /**
     * @return the detallesImpresion
     */
    public List<Rascompras> getDetallesImpresion() {
        return detallesImpresion;
    }

    /**
     * @param detallesImpresion the detallesImpresion to set
     */
    public void setDetallesImpresion(List<Rascompras> detallesImpresion) {
        this.detallesImpresion = detallesImpresion;
    }

    /**
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the iva
     */
    public boolean isIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(boolean iva) {
        this.iva = iva;
    }

    /**
     * @return the formularioDetalleCuenta
     */
    public Formulario getFormularioDetalleCuenta() {
        return formularioDetalleCuenta;
    }

    /**
     * @param formularioDetalleCuenta the formularioDetalleCuenta to set
     */
    public void setFormularioDetalleCuenta(Formulario formularioDetalleCuenta) {
        this.formularioDetalleCuenta = formularioDetalleCuenta;
    }

    /**
     * @return the empleado
     */
    public boolean isEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(boolean empleado) {
        this.empleado = empleado;
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
     * @return the recursoRetencionManual
     */
    public Resource getRecursoRetencionManual() {
        return recursoRetencionManual;
    }

    /**
     * @param recursoRetencionManual the recursoRetencionManual to set
     */
    public void setRecursoRetencionManual(Resource recursoRetencionManual) {
        this.recursoRetencionManual = recursoRetencionManual;
    }

    /**
     * @return the formularioRetencionManual
     */
    public Formulario getFormularioRetencionManual() {
        return formularioRetencionManual;
    }

    /**
     * @param formularioRetencionManual the formularioRetencionManual to set
     */
    public void setFormularioRetencionManual(Formulario formularioRetencionManual) {
        this.formularioRetencionManual = formularioRetencionManual;
    }

    /**
     * @return the renglonesInv
     */
    public List<Renglones> getRenglonesInv() {
        return renglonesInv;
    }

    /**
     * @param renglonesInv the renglonesInv to set
     */
    public void setRenglonesInv(List<Renglones> renglonesInv) {
        this.renglonesInv = renglonesInv;
    }

    /**
     * @return the cuentaProveedor
     */
    public Cuentas getCuentaProveedor() {
        return cuentaProveedor;
    }

    /**
     * @param cuentaProveedor the cuentaProveedor to set
     */
    public void setCuentaProveedor(Cuentas cuentaProveedor) {
        this.cuentaProveedor = cuentaProveedor;
    }

    /**
     * @return the soloRetencion
     */
    public boolean isSoloRetencion() {
        return soloRetencion;
    }

    /**
     * @param soloRetencion the soloRetencion to set
     */
    public void setSoloRetencion(boolean soloRetencion) {
        this.soloRetencion = soloRetencion;
    }

    /**
     * @return the listaActivos
     */
    public LazyDataModel<Activos> getListaActivos() {
        return listaActivos;
    }

    /**
     * @param listaActivos the listaActivos to set
     */
    public void setListaActivos(LazyDataModel<Activos> listaActivos) {
        this.listaActivos = listaActivos;
    }

    /**
     * @return the formularioActivos
     */
    public Formulario getFormularioActivos() {
        return formularioActivos;
    }

    /**
     * @param formularioActivos the formularioActivos to set
     */
    public void setFormularioActivos(Formulario formularioActivos) {
        this.formularioActivos = formularioActivos;
    }

    /**
     * @return the valorTotalActivos
     */
    public double getValorTotalActivos() {
        return valorTotalActivos;
    }

    /**
     * @param valorTotalActivos the valorTotalActivos to set
     */
    public void setValorTotalActivos(double valorTotalActivos) {
        this.valorTotalActivos = valorTotalActivos;
    }

    /**
     * @return the listaCabeceras
     */
    public LazyDataModel<Cabecerainventario> getListaCabeceras() {
        return listaCabeceras;
    }

    /**
     * @param listaCabeceras the listaCabeceras to set
     */
    public void setListaCabeceras(LazyDataModel<Cabecerainventario> listaCabeceras) {
        this.listaCabeceras = listaCabeceras;
    }

    /**
     * @return the formularioSuministros
     */
    public Formulario getFormularioSuministros() {
        return formularioSuministros;
    }

    /**
     * @param formularioSuministros the formularioSuministros to set
     */
    public void setFormularioSuministros(Formulario formularioSuministros) {
        this.formularioSuministros = formularioSuministros;
    }

    /**
     * @return the numeroFacturaSuministro
     */
    public Integer getNumeroFacturaSuministro() {
        return numeroFacturaSuministro;
    }

    /**
     * @param numeroFacturaSuministro the numeroFacturaSuministro to set
     */
    public void setNumeroFacturaSuministro(Integer numeroFacturaSuministro) {
        this.numeroFacturaSuministro = numeroFacturaSuministro;
    }

    /**
     * @return the valorTotalSuministros
     */
    public double getValorTotalSuministros() {
        return valorTotalSuministros;
    }

    /**
     * @param valorTotalSuministros the valorTotalSuministros to set
     */
    public void setValorTotalSuministros(double valorTotalSuministros) {
        this.valorTotalSuministros = valorTotalSuministros;
    }

    /**
     * @return the formularioImprimirSuministros
     */
    public Formulario getFormularioImprimirSuministros() {
        return formularioImprimirSuministros;
    }

    /**
     * @param formularioImprimirSuministros the formularioImprimirSuministros to
     * set
     */
    public void setFormularioImprimirSuministros(Formulario formularioImprimirSuministros) {
        this.formularioImprimirSuministros = formularioImprimirSuministros;
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
     * @return the tieneAmortizaciones
     */
    public boolean isTieneAmortizaciones() {
        return tieneAmortizaciones;
    }

    /**
     * @param tieneAmortizaciones the tieneAmortizaciones to set
     */
    public void setTieneAmortizaciones(boolean tieneAmortizaciones) {
        this.tieneAmortizaciones = tieneAmortizaciones;
    }

    private void traerRenglones() {
        if (obligacion != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and  o.cabecera.opcion like 'OBLIGACIONES%'");
            parametros.put("id", obligacion.getId());
            parametros.put(";orden", "o.valor desc");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id "
                        + "  and o.cabecera.opcion 'OBLIGACIONES_INV'");
                parametros.put("id", obligacion.getId());
                parametros.put(";orden", "o.valor desc");
//                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                renglonesInv = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            // se une con los otros?

        }
    }

    public String contabilizar(Obligaciones obligacion) {
        if (obligacion.getEstado() != 0) {
            MensajesErrores.advertencia("No puede contabilizar obligación no terminada");
            return null;
        }
        String error = "";
        try {
            if (formularioActivos.getIndiceFila() == 1) {
                error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), numeroFacturaActivos, numeroFacturaSuministro);
            } else {
                error = ejbObligaciones.contabilizar(obligacion, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), null, null);
            }
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (error.contains("ERROR")) {
            MensajesErrores.advertencia(error);
            return null;
        }
        imprimirSolicitudPago(obligacion);
        MensajesErrores.informacion("Se creo asiento NO:" + error);
        return null;
    }

    public String imprimirSolicitudPago(Obligaciones obligacion) {
        this.obligacion = obligacion;
        if (obligacion.getEstado() < 2) {
            MensajesErrores.advertencia("No puede imprimirse obligación no contabilizada");
            return null;
        }
        try {
            Map parametros = new HashMap();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                parametros = new HashMap();
                parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
                parametros.put("tipodocumento", obligacion.getTipodocumento());
                parametros.put("autorizacion", obligacion.getAutorizacion());
                parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                for (Autorizaciones a : la) {
                    autorizacion = a;
                }
                getFacturaBean().setFactura(null);
            } else {
                // generar factura electrónica
                getFacturaBean().cargar(obligacion.getFacturaelectronica());
            }
//        proveedorBean.setTipobuscar("-2");
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put(";orden", "o.detallecompromiso.id");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            detallesImpresion = detalles;
            Detallecompromiso compara = null;
            Rascompras rImprime = null;
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put(";orden", "o.id");
            parametros.put("obligacion", obligacion);
            promesas = ejbVencimientos.encontarParametros(parametros);
            detalles = ejbDetalles.encontarParametros(parametros);
            traerRenglones();
            calculaTotal();
            // reporte
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("ORDEN DE PAGO NO : " + obligacion.getId() + "\n  Valor A Pagar : " + getValorStr(), null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf.format(obligacion.getFechaemision())));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Documento"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, obligacion.getTipodocumento().getNombre() + " - "
                    + obligacion.getEstablecimiento() + "-" + obligacion.getPuntoemision() + "-" + obligacion.getDocumento()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Beneficiario"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, proveedorBean.getProveedor().getEmpresa().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "email"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, proveedorBean.getProveedor().getEmpresa().getEmail()));
            if (obligacion.getElectronica() == null) {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Retención : "));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, obligacion.getEstablecimientor() + "-" + obligacion.getPuntor() + "-" + obligacion.getNumeror()));
            } else {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "R.E.C. Accceso"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, obligacion.getClaver()));
            }
            if (obligacion.getContrato() == null) {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Número"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "SIN CONTRATO"));
            } else {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Contrato"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, obligacion.getContrato().toString()));
            }

            pdf.agregarTabla(null, columnas, 2, 100, null);
            // un parafo
            pdf.agregaParrafo("Concepto : " + obligacion.getConcepto() + "\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglones == null) {
                renglones = new LinkedList<>();
            }
            for (Renglones r : renglones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
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
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "CONTABILIZACION");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
//            valorModificaciones=0;
            sumaDebitos = 0;
            sumaCreditos = 0;
            if (renglonesInv != null) {
                for (Renglones r : renglonesInv) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
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
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 7, 100, "RECLASIFICACION");
            }
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            for (AuxiliarCarga a : totales) {
                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTotal()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getIngresos().doubleValue()));
            }
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "RESUMEN");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            double totalCompromiso = 0;
            for (Rascompras d : detallesImpresion) {
                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6,
                        false, d.getDetallecompromiso().toString()));
                double valorImpuesto = d.getValorimpuesto().doubleValue();
                if (d.getValorprima() != null) {
                    if (d.getImpuesto() != null) {
                        double baseImponible = d.getValor().doubleValue();
                        valorImpuesto = baseImponible * (1 + d.getImpuesto().getPorcentaje().doubleValue() / 100);
                    } else {
                        valorImpuesto = d.getValor().doubleValue();
                    }
                } else {
                    valorImpuesto = d.getValor().doubleValue() + valorImpuesto;
                }
                totalCompromiso += valorImpuesto;
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        valorImpuesto));

            }
            columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL "));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCompromiso));
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "AFECTACION PRESUPUESTARIA");
            reporte = pdf.traerRecurso();
            // las promesas
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        calculaTotalImpresion();

//        calculaTotalImpresion();
        formularioReporte.editar();

        return null;
    }

    public String reenviarCorreo(Obligaciones obligacion) {
        this.obligacion = obligacion;
//        if (obligacion.getEstado() < 2) {
//            MensajesErrores.advertencia("No puede imprimirse obligación no terminada");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                parametros = new HashMap();
                parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
                parametros.put("tipodocumento", obligacion.getTipodocumento());
                parametros.put("autorizacion", obligacion.getAutorizacion());
                parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                for (Autorizaciones a : la) {
                    autorizacion = a;
                }
            } else {
                // generar factura electrónica
                getFacturaBean().cargar(obligacion.getFacturaelectronica());
            }
//        proveedorBean.setTipobuscar("-2");
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            for (Rascompras r : detalles) {
                if (r.getDetallecompromiso() != null) {
                    setCompromiso(r.getDetallecompromiso().getCompromiso());
                }
            }
            promesas = ejbVencimientos.encontarParametros(parametros);
            traerRenglones();
            // las promesas
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotal();
//        autorizarSri();
//        calculaTotalImpresion();
        // lo del correo
        String xmlResultado = obligacion.getElectronica();
        // grabar numero en documento
        // enviar correos y cerrar
        generaRetElectronica(false, true);
        generarReporte();
//        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
        String directorio = System.getProperty("java.io.tmpdir");
        File archivParaEmail = grabarXml(
                "Autorizado" + comprobante.getInfoTributaria().getClaveAcceso(),
                xmlResultado);
        Codigos textoCodigo = codigosBean.traerCodigo("MAIL", "RETENCION");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        String texto = textoCodigo.getParametros().replace("#proveedor#", obligacion.getProveedor().getEmpresa().getNombrecomercial());
        texto = texto.replace("#fecha#", comprobante.getInfoCompRetencion().getFechaEmision());
        texto = texto.replace("#clave#", comprobante.getInfoTributaria().getClaveAcceso());
        try {
            ejbCorreos.enviarCorreo(obligacion.getProveedor().getEmpresa().getEmail(),
                    textoCodigo.getDescripcion(), texto, pdfFile, archivParaEmail);
//                    "EP SEGURIDAD Informa : Ud ha recibido un documento electrónico", texto, pdfFile, archivParaEmail);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // contabilizar
        String error = "";
        MensajesErrores.informacion("Correo enviado correctamente a la cuenta :" + obligacion.getProveedor().getEmpresa().getEmail());
//        formularioReporte.editar();
        return null;
    }

    public String imprimirElectronica(Obligaciones obligacion) {
        this.obligacion = obligacion;
//        if (obligacion.getEstado() < 2) {
//            MensajesErrores.advertencia("No puede imprimirse obligación no terminada");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                parametros = new HashMap();
                parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
                parametros.put("tipodocumento", obligacion.getTipodocumento());
                parametros.put("autorizacion", obligacion.getAutorizacion());
                parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                for (Autorizaciones a : la) {
                    autorizacion = a;
                }
                facturaBean.setFactura(null);
            } else {
                // generar factura electrónica
                getFacturaBean().cargar(obligacion.getFacturaelectronica());
            }
//        proveedorBean.setTipobuscar("-2");
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            for (Rascompras r : detalles) {
                if (r.getDetallecompromiso() != null) {
                    setCompromiso(r.getDetallecompromiso().getCompromiso());
                }
            }
            promesas = ejbVencimientos.encontarParametros(parametros);
            traerRenglones();
            // las promesas
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotal();
//        autorizarSri();
//        calculaTotalImpresion();
        // lo del correo
        String xmlResultado = obligacion.getElectronica();
        // grabar numero en documento
        // enviar correos y cerrar
        generaRetElectronica(false, true);
        generarReporte();
        formularioRetencionElectronica.insertar();
//        String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
//        String directorio = System.getProperty("java.io.tmpdir");
//        File archivParaEmail = grabarXml(
//                "Autorizado" + comprobante.getInfoTributaria().getClaveAcceso(),
//                xmlResultado);
//        Codigos textoCodigo = codigosBean.traerCodigo("MAIL", "RETENCION");
//        if (textoCodigo == null) {
//            MensajesErrores.fatal("No configurado texto para email en codigos");
//            return null;
//        }
//        String texto = textoCodigo.getParametros().replace("#proveedor#", obligacion.getProveedor().getEmpresa().getNombrecomercial());
//        texto = texto.replace("#fecha#", comprobante.getInfoCompRetencion().getFechaEmision());
//        texto = texto.replace("#clave#", comprobante.getInfoTributaria().getClaveAcceso());
//        try {
//            ejbCorreos.enviarCorreo(obligacion.getProveedor().getEmpresa().getEmail(),
//                    textoCodigo.getDescripcion(), texto, pdfFile, archivParaEmail);
////                    "EP SEGURIDAD Informa : Ud ha recibido un documento electrónico", texto, pdfFile, archivParaEmail);
//        } catch (MessagingException | UnsupportedEncodingException ex) {
//            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        // contabilizar
        String error = "";
//        MensajesErrores.informacion("Correo enviado correctamente a la cuenta :" + obligacion.getProveedor().getEmpresa().getEmail());
//        formularioReporte.editar();
        return null;
    }

    public String getValorStr() {
        if (obligacion == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(obligacion.getApagar());
    }

    public SelectItem[] getComboCuentas() {
        if (obligacion == null) {
            return null;
        }
        if (detalle.getDetallecompromiso() == null) {
            return null;
        }
        // solo para quitar el error
        String cuentaPresupuesto = detalle.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();
        parametros.put(";where", "o.presupuesto=:presupuesto");
        parametros.put("presupuesto", cuentaPresupuesto);
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            return Combos.getSelectItems(cl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void armarCuentaProveedor(Cuentas c, List<Cuentas> lista) {
        for (Cuentas c1 : lista) {
            if (c1.equals(c)) {
                return;
            }
        }
        lista.add(c);
    }

    public SelectItem[] getComboCuentasProveedor() {
        if (obligacion == null) {
            return null;
        }
        if (detalles == null) {
            return null;
        }
        List<Cuentas> listaCuentas = new LinkedList<>();
        for (Rascompras r : detalles) {
            if (r.getDetallecompromiso() != null) {
                Formatos f = ejbObligaciones.traerFormato();
                if (f != null) {
                    String xx = f.getFormato().replace(".", "#");
                    String[] aux = xx.split("#");
                    int tamano = aux[f.getNivel() - 1].length();
                    String presupuesto = r.getDetallecompromiso().
                            getAsignacion().getClasificador().getCodigo();
                    presupuesto = presupuesto.substring(0, tamano);
                    String cuentaProveedorInt = f.getCxpinicio() + presupuesto + f.getCxpfin();
                    armarCuentaProveedor(cuentasBean.traerCodigo(cuentaProveedorInt), listaCuentas);
                }
            }
        }
        // solo para quitar el error

        return Combos.getSelectItems(listaCuentas, false);
    }

    public SelectItem[] getComboCuentasDetalle() {

        // solo para quitar el error
        List<Codigos> lista = codigosBean.getListaCuetasCobrar();
        Map parametros = new HashMap();
        String where = "";
        for (Codigos c : lista) {
            if (!where.isEmpty()) {
                where += ",";
            }
            where += "'" + c.getDescripcion() + "'";
        }
        if (where.isEmpty()) {
            return null;
        }

        where = " o.codigo in (" + where + ")";
        parametros.put(";where", where);
        parametros.put(";orden", "o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            return Combos.getSelectItems(cl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SelectItem[] getComboCuentasPresupuesto() {
        if (getCompromiso() == null) {
            return null;
        }
        // solo para quitar el error
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso and o.saldo>0");
        parametros.put("compromiso", getCompromiso());
        try {
            List<Detallecompromiso> dl = ejbDetComp.encontarParametros(parametros);
            return Combos.getSelectItems(dl, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SelectItem[] getComboRetenciones() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false and o.bien=:bien");
//        parametros.put(";where", "o.impuesto=false and o.bien=:bien and o.especial=:especial");
        parametros.put("bien", detalle.getBien());
//        parametros.put("especial", obligacion.getProveedor().getEmpresa().getEspecial());
        try {
            return Combos.getSelectItems(ejbRetenciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboRetencionesImpuestos() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true and o.bien=:bien and o.especial=:especial");
        parametros.put("bien", detalle.getBien());
        parametros.put("especial", obligacion.getProveedor().getEmpresa().getEspecial());
        try {
            return Combos.getSelectItems(ejbRetenciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboCompromisos() {
        if (obligacion.getProveedor() == null) {
            return null;
        }
//        **************************************************Ojo limitar los usados
        // solo para quitar el error
//        String cuentaPresupuesto=obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();
        if (contrato != null) {
            parametros.put(";where", "o.contrato=:contrato");
            parametros.put("contrato", contrato);
        } else {
            parametros.put(";where", "o.proveedor=:proveedor and o.contrato is null");
            parametros.put("proveedor", obligacion.getProveedor());
        }

        try {
            List<Compromisos> dl = ejbCompromisos.encontarParametros(parametros);
            return Combos.getSelectItems(dl, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboAnticiposContrato() {
        if (contrato == null) {
            if (obligacion.getContrato() == null) {
                return null;
            } else {
                contrato = obligacion.getContrato();
            }
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato and o.pagado=false ");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.fechaemision desc");
        try {
            return Combos.getSelectItems(ejbAnticipos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String reimprimeManual(Obligaciones obligacion) {
        this.obligacion = obligacion;
        autorizacion = null;

        try {
            Map parametros = new HashMap();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                getFacturaBean().setFactura(null);
                parametros = new HashMap();
                parametros.put(";where", "o.tipodocumento=:tipodocumento and o.autorizacion=:autorizacion and o.empresa=:empresa");
                parametros.put("tipodocumento", obligacion.getTipodocumento());
                parametros.put("autorizacion", obligacion.getAutorizacion());
                parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                for (Autorizaciones a : la) {
                    autorizacion = a;
                }
            } else {
                // generar factura electrónica
                getFacturaBean().cargar(obligacion.getFacturaelectronica());
            }
//        proveedorBean.setTipobuscar("-2");
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            for (Rascompras d : detalles) {
                if (d.getDetallecompromiso() != null) {
                    setCompromiso(d.getDetallecompromiso().getCompromiso());
                }
            }
            promesas = ejbVencimientos.encontarParametros(parametros);
            // las promesas
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotal();
        imprimeRetencionManual();
        return null;
    }

    public String salirTotal() {
        if (detalles != null) {
            for (Rascompras r : detalles) {
                Detallecompromiso c = r.getDetallecompromiso();
                if (c != null) {
                    double saldo = c.getSaldo().doubleValue();
                    saldo = (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue());
                    c.setSaldo(new BigDecimal(saldo));
                    try {
                        ejbDetComp.edit(c, seguridadbean.getLogueado().getUserid());
                    } catch (GrabarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        proveedorBean.setProveedor(null);
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        formulario.cancelar();
        return null;
    }

    public String imprimirSuministro(Cabecerainventario cab) {

        // taer la kardex
        // solo egresos si es transferencia
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cab);
        try {
            List<Kardexinventario> listaKardex = ejbKardex.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", cab.getTxid().getNombre() + " No : " + cab.getId());
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", cab.getFecha());
            parametros.put("documento", cab.getId());
            parametros.put("modulo", cab.getBodega().getNombre());
            parametros.put("descripcion", cab.getObservaciones());
            parametros.put("transaccion", cab.getTxid().getNombre());
            parametros.put("proveedor", (cab.getProveedor() == null ? null : cab.getProveedor().getEmpresa().toString()));
            parametros.put("contrato", (cab.getContrato() == null ? null : cab.getContrato().toString()));
            for (Kardexinventario k : listaKardex) {
                if ((k.getCostopromedio() == null) || (k.getCostopromedio() == 0)) {
                    k.setCostopromedio(k.getCosto());
                }
            }
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Inventario.jasper"),
                    listaKardex, "Inventario" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimirSuministros.editar();
        return null;
    }

    public SelectItem[] getComboAutorizaciones() {
        if (proveedorBean.getProveedor() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        if (obligacion == null) {
            return null;
        }
        if (obligacion.getFechaemision() == null) {
            return null;
        }
        if (tipoDocumento == null) {
            return null;
        }

        Map parametros = new HashMap();
//        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento.codigo in ('FACT','OTR')" );
//        parametros.put(";where", "o.empresa=:empresa and "
//                + " o.tipodocumento.codigo not in ('RET') "
//                + " and :fecha between o.fechaemision and o.fechacaducidad");
//        parametros.put("empresa", proveedorBean.getProveedor().getEmpresa());
//        parametros.put("fecha", obligacion.getFechaemision());
//        parametros.put(";orden", "o.inicio desc");
        if (!tipoDocumento.getCodigo().equals("LIQCOM")) {
            String where = "o.empresa=:empresa and "
                    + " o.tipodocumento=:tipo"
                    + " and :fecha between o.fechaemision and o.fechacaducidad";
            parametros.put("empresa", proveedorBean.getProveedor().getEmpresa());
            parametros.put(";where", where);
            parametros.put("fecha", obligacion.getFechaemision());
            parametros.put("tipo", tipoDocumento);
            parametros.put(";orden", "o.inicio desc");
            try {
                List<Autorizaciones> autorizaciones = ejbAutorizaciones.encontarParametros(parametros);
                return Combos.getSelectItems(autorizaciones, false);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            String where = ""
                    + " o.documento=:tipo"
                    + " and :fecha between o.desde and o.hasta";
//            parametros.put("empresa", proveedorBean.getProveedor().getEmpresa());
            parametros.put(";where", where);
            parametros.put("fecha", obligacion.getFechaemision());
            parametros.put("tipo", tipoDocumento);
            parametros.put(";orden", "o.inicial desc");
            try {
                List<Documentos> listaDoc = ejbDocumnetos.encontarParametros(parametros);
//                List<Autorizaciones> autorizaciones=new LinkedList<>();
//                for (Documentos d:listaDoc){
//                    Autorizaciones a=new Autorizaciones();
//                    a.setAutorizacion(d.getAutorizacion());
//                    a.setEstablecimiento(d.getPunto().getSucursal().getRuc());
//                    a.setPuntoemision(d.getPunto().getCodigo());
//                    a.setFechacaducidad(d.getHasta());
//                    a.setFechaemision(d.getDesde());
//                    a.setFin(d.getFinal1());
//                    a.setInicio(d.getInicial());
//                    a.setTipodocumento(tipoDocumento);
//                    autorizaciones.add(a);
//                }
                return Combos.getSelectItems(listaDoc, false);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public double traerValor(Obligaciones o) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            return ejbDetalles.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the reporteSolicitud
     */
    public Resource getReporteSolicitud() {
        return reporteSolicitud;
    }

    /**
     * @param reporteSolicitud the reporteSolicitud to set
     */
    public void setReporteSolicitud(Resource reporteSolicitud) {
        this.reporteSolicitud = reporteSolicitud;
    }

    public String salirReporte() {
        proveedorBean.setProveedor(null);
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        formularioReporte.cancelar();
        formularioRetencionManual.cancelar();
        return null;
    }

    public void cuentasListener(FileEntryEvent e) {
        if (cuentaProveedor == null) {
            MensajesErrores.advertencia("Seleccione una cuenta contable");
            return;
        }
        if (detalle.getIdcuenta() == null) {
            MensajesErrores.advertencia("Seleccione una cuenta proveedor");
            return;
        }
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        File xmlFileFactura;
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                xmlFileFactura = i.getFile();
                // trarer los xml y grabarlos
                String xmlStr = "";
                String sb;
                try {
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFileFactura), "UTF-8"));
                    while ((sb = entrada.readLine()) != null) {
                        String[] strCuenta = sb.split(",");
                        Rascompras rasCompras = new Rascompras();
                        rasCompras.setIdcuenta(detalle.getIdcuenta());
                        rasCompras.setReferencia(detalle.getReferencia());
                        rasCompras.setBien(detalle.getBien());
                        rasCompras.setBien(detalle.getBien());
                        rasCompras.setCc(detalle.getCc());
                        rasCompras.setRetencion(detalle.getRetencion());
                        rasCompras.setImpuesto(detalle.getImpuesto());
                        rasCompras.setRetimpuesto(detalle.getRetimpuesto());
                        rasCompras.setValor(new BigDecimal(strCuenta[1]));

                        if (detalles == null) {
                            detalles = new LinkedList<>();
                        }

                        rasCompras.setCuenta(cuentaProveedor.getCodigo());
                        if (isEmpleado()) {
                            rasCompras.setCba("E");
                            rasCompras.setCc(strCuenta[0]);
                        } else {
                            rasCompras.setCba("P");
                            rasCompras.setCc(strCuenta[0]);
                        }

//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
                        rasCompras.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
                        if (iva) {
                            if (rasCompras.getImpuesto() != null) {
                                double valor = rasCompras.getValor().doubleValue()
                                        / (1 + (rasCompras.getImpuesto().getPorcentaje().doubleValue() / 100));
                                rasCompras.setValor(new BigDecimal(valor));
                            }
                        }
                        if (rasCompras.getRetencion() != null) {
                            rasCompras.setValorret(new BigDecimal(rasCompras.getValor().doubleValue()
                                    * rasCompras.getRetencion().getPorcentaje().doubleValue() / 100));
                        } else {
                            rasCompras.setValorret(BigDecimal.ZERO);
                        }
                        if (rasCompras.getImpuesto() != null) {
                            if (!soloRetencion) {
                                double impuestoDoble = Math.round(rasCompras.getValor().doubleValue()
                                        * rasCompras.getImpuesto().getPorcentaje().doubleValue());
                                rasCompras.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                                if (rasCompras.getRetimpuesto() != null) {
                                    double retimp = Math.round(impuestoDoble / 100
                                            * rasCompras.getRetimpuesto().getPorcentaje().doubleValue());
                                    rasCompras.setVretimpuesto(new BigDecimal(retimp / 100));
                                }
                            } else {
//                ************************************+AUMENTADO EL CALCULO DEL IMPUESTO CON EL VALOR DEL SEGURO
                                double impuestoDoble = Math.round(rasCompras.getValorprima().doubleValue()
                                        * rasCompras.getImpuesto().getPorcentaje().doubleValue());
                                rasCompras.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
                                if (rasCompras.getRetimpuesto() != null) {
                                    double retimp = Math.round(impuestoDoble / 100
                                            * rasCompras.getRetimpuesto().getPorcentaje().doubleValue());
                                    rasCompras.setVretimpuesto(new BigDecimal(retimp / 100));
                                }
                            }
                        } else {
                            rasCompras.setValorimpuesto(BigDecimal.ZERO);
                            rasCompras.setVretimpuesto(BigDecimal.ZERO);
                            rasCompras.setRetimpuesto(null);
                        }
                        if (soloRetencion) {
                            rasCompras.setValor(BigDecimal.ZERO);
                        }
                        obligacion.setFechaingreso(new Date());
                        try {
                            if (obligacion.getId() == null) {
                                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
                            }
                            rasCompras.setObligacion(obligacion);
                            ejbDetalles.create(rasCompras, seguridadbean.getLogueado().getUserid());
                            detalles.add(rasCompras);
                        } catch (InsertarException ex) {
                            MensajesErrores.fatal(ex.getMessage());
                            Logger.getLogger("").log(Level.SEVERE, null, ex);
                        }

                    }

                    //
                } catch (IOException ex) {
                    MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                    Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } //fin if
        }// fin for
        formularioDetalleCuenta.cancelar();
        calculaTotal();
    }

    /**
     * @return the autorizacionDocumento
     */
    public Documentos getAutorizacionDocumento() {
        return autorizacionDocumento;
    }

    /**
     * @param autorizacionDocumento the autorizacionDocumento to set
     */
    public void setAutorizacionDocumento(Documentos autorizacionDocumento) {
        this.autorizacionDocumento = autorizacionDocumento;
    }

    /**
     * @return the docElecBean
     */
    public DocumentosElectronicosBean getDocElecBean() {
        return docElecBean;
    }

    /**
     * @param docElecBean the docElecBean to set
     */
    public void setDocElecBean(DocumentosElectronicosBean docElecBean) {
        this.docElecBean = docElecBean;
    }

    /**
     * @return the puntosBean
     */
    public PuntoEmisionBean getPuntosBean() {
        return puntosBean;
    }

    /**
     * @param puntosBean the puntosBean to set
     */
    public void setPuntosBean(PuntoEmisionBean puntosBean) {
        this.puntosBean = puntosBean;
    }

    /**
     * @return the listoContabilizar
     */
    public boolean isListoContabilizar() {
        return listoContabilizar;
    }

    /**
     * @param listoContabilizar the listoContabilizar to set
     */
    public void setListoContabilizar(boolean listoContabilizar) {
        this.listoContabilizar = listoContabilizar;
    }

    /**
     * @return the formularioRetencionElectronica
     */
    public Formulario getFormularioRetencionElectronica() {
        return formularioRetencionElectronica;
    }

    /**
     * @param formularioRetencionElectronica the formularioRetencionElectronica
     * to set
     */
    public void setFormularioRetencionElectronica(Formulario formularioRetencionElectronica) {
        this.formularioRetencionElectronica = formularioRetencionElectronica;
    }

    /**
     * @return the recursoPdf
     */
    public Resource getRecursoPdf() {
        return recursoPdf;
    }

    /**
     * @param recursoPdf the recursoPdf to set
     */
    public void setRecursoPdf(Resource recursoPdf) {
        this.recursoPdf = recursoPdf;
    }
}
