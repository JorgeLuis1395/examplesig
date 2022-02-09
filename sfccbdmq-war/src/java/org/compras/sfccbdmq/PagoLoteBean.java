/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabdocelectFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.DocumentosEmisionBean;
import org.contabilidad.sfccbdmq.RetencionesBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "pagoLoteSfccbdmq")
@ViewScoped
public class PagoLoteBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PagoLoteBean() {
        Calendar c = Calendar.getInstance();

        cabecerasDocumentos = new LazyDataModel<Cabdocelect>() {

            @Override
            public List<Cabdocelect> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Cabdocelect> cabecerasDocumentos;
    private List<Obligaciones> obligaciones;
    private List<Documentoselectronicos> listadoFacturas;
    private List<Documentoselectronicos> listadoObligaciones;
    private Documentoselectronicos factura;
    private Obligaciones obligacion = new Obligaciones();
    private List<Rascompras> detalles;
    private List<Rascompras> renglonesInternos;
    private List<Renglones> renglones;
    private List<Rascompras> detallesb;
    private List<Rascompras> detallesInternos;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private List<Detallecompromiso> detalleCompromiso;
    private Detallecompromiso detCompromiso;
    private Compromisos compromiso;
    private Rascompras detalle;
    private Rascompras detalleModificar;
    private Cabdocelect cabeceraDoc;
    private Autorizaciones autorizacion;
    private Documentos documeto;
    private Cuentas cuentaProveedor;
    private Cuentas cuentaPrueba;
    private List<AuxiliarCarga> totales;
    private Puntoemision puntoEmision;
    private boolean proveedorBeneficiario;
    private boolean soloRetencion;
    private boolean verFacturas;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioEditar = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioDetalleCuenta = new Formulario();
    private Formulario formularioDetalleDetalle = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    @EJB
    private CabdocelectFacade ejbCabdocelect;
    @EJB
    private DetallecompromisoFacade ejbDetalleCompromiso;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RascomprasFacade ejbDetalles;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private DocumentosFacade ejbDocumnetos;
    @EJB
    private RetencionesFacade ejbRetenciones;
    @EJB
    private PagosvencimientosFacade ejbPagos;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private DocumentoselectronicosFacade ejbDocElec;
    @EJB
    private PuntoemisionFacade ejbPuntos;
    @EJB
    private CabecerasFacade ejbCabRen;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{facturaSri}")
    private FacturaSriBean facturaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    private Date desde;
    private String motivo;
    private Integer numero;
    private Date hasta;
    private Boolean iva;
    private boolean empleado;
    private ComprobanteRetencion comprobante;
//    private List<Pagosvencimientos> promesas;
//    private List<Pagosvencimientos> promesasb;
    private Pagosvencimientos pago;
    private Resource reporte;

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
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String perfilInterno = (String) params.get("p");
        String nombreForma = "PagoLoteVista";
        if (perfilInterno == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilInterno));
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
    }

    public String buscar() {
        cabecerasDocumentos = new LazyDataModel<Cabdocelect>() {
            @Override
            public List<Cabdocelect> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha asc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }

                String where = "o.fecha between :desde and :hasta ";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }

                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.observaciones) like :motivo";
                    parametros.put("motivo", motivo + "%");
                }
                if (proveedorBean.getProveedor() != null) {
                    where += " and o.compromiso.proveedor=:proveedor";
                    parametros.put("proveedor", proveedorBean.getProveedor());
                }
                if (getCompromiso() != null) {
                    where += " and o.compromiso=:compromiso";
                    parametros.put("compromiso", getCompromiso());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCabdocelect.contar(parametros);
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
                cabecerasDocumentos.setRowCount(total);
                try {
                    return ejbCabdocelect.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        return null;
    }

//    private void esta(DetallecabeceraDoc d) {
//        for (Obligaciones o : getObligaciones()) {
//            // es el ras compras
//            if (o.getCompromiso().equals(d)) {
//                return;
//            }
//
//        }
//        Obligaciones o = new Obligaciones();
//        o.setProveedor(d.getProveedor());
//        o.setContrato(null);
//        o.setFechaemision(new Date());
//        o.setEstado(1); // No se si 1 o que
//        o.setFechaingreso(new Date());
//        o.setApagar(d.getValor());
//        o.setCompromiso(cabeceraDoc);
//        o.setConcepto(d.getMotivo());
//        try {
//            ejbObligaciones.create(o, seguridadbean.getLogueado().getUserid());
////        facturaBean.setFactura(null);
//        } catch (InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        getObligaciones().add(o);
//    }
    private Proveedores traeDetalle(Proveedores p, List<Proveedores> pl) {
        for (Proveedores d : pl) {
            if (d.equals(p)) {
                return null;
            }
        }
        return p;
    }

    public Boolean getDeshabilitaProveedor() {
        if (detalles == null) {
            return false;
        }
        if (detalles.isEmpty()) {
            return false;
        }
        return true;
    }

    private void estEnCompromiso(List<Compromisos> listado, Compromisos c) {
        for (Compromisos c1 : listado) {
            if (c1.equals(c)) {
                return;
            }
        }
        listado.add(c);
    }

    public SelectItem[] getComboCompromisos() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        List<Compromisos> listadoCompromisos = new LinkedList<>();
        Map parametros = new HashMap();
//        parametros.put(";where", "o.compromiso.contrato is null "
        parametros.put(";where", " "
                + " o.compromiso.nomina is null "
                + " and o.compromiso.impresion is not null "
                + " and o.compromiso.proveedor=:proveedor "
                + " and o.saldo!=0");
        parametros.put("proveedor", proveedorBean.getProveedor());
        try {
            List<Detallecompromiso> listaDetalle = ejbDetalleCompromiso.encontarParametros(parametros);
            for (Detallecompromiso d : listaDetalle) {
                // ver si el compromiso ya fue utilizado en lote de pago antes de mandar a esto
//                parametros = new HashMap();
//                parametros.put(";where", "o.compromiso=:compromiso");
//                parametros.put("compromiso", d.getCompromiso());
//                int total = ejbCabdocelect.contar(parametros);
//                if (total == 0) {
                estEnCompromiso(listadoCompromisos, d.getCompromiso());
//                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Combos.getSelectItems(listadoCompromisos, true);
    }

    public List<Detallecompromiso> traerDetalleCompromiso() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        List<Detallecompromiso> listadoCompromisos = new LinkedList<>();
        Map parametros = new HashMap();
//        parametros.put(";where", "o.compromiso.contrato is null "
        parametros.put(";where", " "
                + " o.compromiso.nomina is null "
                + " and o.compromiso.impresion is not null "
                + " and o.compromiso.proveedor=:proveedor "
                + " and o.saldo!=0");
        parametros.put("proveedor", proveedorBean.getProveedor());
        try {
            return ejbDetalleCompromiso.encontarParametros(parametros);
//            for (Detallecompromiso d : listaDetalle) {
//                // ver si el compromiso ya fue utilizado en lote de pago antes de mandar a esto
////                parametros = new HashMap();
////                parametros.put(";where", "o.compromiso=:compromiso");
////                parametros.put("compromiso", d.getCompromiso());
////                int total = ejbCabdocelect.contar(parametros);
////                if (total == 0) {
//                estEnCompromiso(listadoCompromisos, d.getCompromiso());
////                }
//            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public SelectItem[] getComboCompromisosBuscar() {
        if (proveedorBean.getProveedor() == null) {
            return null;
        }
        List<Compromisos> listadoCompromisos = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso.proveedor=:proveedor");
        parametros.put("proveedor", proveedorBean.getProveedor());
        try {
            List<Cabdocelect> listaDetalle = ejbCabdocelect.encontarParametros(parametros);
            for (Cabdocelect d : listaDetalle) {
                estEnCompromiso(listadoCompromisos, d.getCompromiso());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Combos.getSelectItems(listadoCompromisos, true);
    }

    public String modificar(Cabdocelect cabeceraDoc) {
        this.setCabeceraDoc(cabeceraDoc);
        setCompromiso(cabeceraDoc.getCompromiso());
        Map parametros = new HashMap();
        listadoFacturas = new LinkedList<>();
        listadoObligaciones = new LinkedList<>();
        verFacturas = false;
        // traer los documentos
//        parametros.put(";where", "(o.cabeccera=:cabeceraDoc or o.cabeccera is null) and o.ruc=:ruc");
        parametros.put(";where", "o.cabeccera is null and o.ruc=:ruc");
//        parametros.put("cabeceraDoc", cabeceraDoc);
        parametros.put("ruc", cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc());
        try {

            listadoFacturas = ejbDocElec.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioObligacion.editar();
        formulario.editar();
        return null;
    }

    public String contabilizar(Cabdocelect cabeceraDoc) {
        this.setCabeceraDoc(cabeceraDoc);
        setCompromiso(cabeceraDoc.getCompromiso());
        Map parametros = new HashMap();
        // traer los documentos
        parametros.put(";where", "o.cabeccera=:cabeceraDoc");
        parametros.put("cabeceraDoc", cabeceraDoc);
        try {
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            if (listadoFacturas.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            String error = ejbDocElec.validaContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            if (error != null) {
                MensajesErrores.advertencia(error);
                return null;
            }
            for (Documentoselectronicos d : listadoFacturas) {
                if (d.getValortotal() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getEstablecimiento() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getPuntoemision() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getDocumento() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getApagar() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getApagar().doubleValue() <= 0) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }

            }
//            renglones = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            double credito = 0;
            double debito = 0;
            renglonesAuxiliar = new LinkedList<>();
            for (Renglones r : renglones) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                a.setReferencia(r.getReferencia());
                a.setAuxiliar(r.getAuxiliar());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    debito += valor;
                    a.setIngresos(new BigDecimal(valor));
                } else {
                    credito += Math.abs(valor);
                    a.setEgresos(new BigDecimal(Math.abs(valor)));
                }
                renglonesAuxiliar.add(a);
            }
            AuxiliarCarga a = new AuxiliarCarga();
            a.setCuenta("TOTALES");
            a.setIngresos(new BigDecimal(Math.abs(debito)));
            a.setEgresos(new BigDecimal(Math.abs(credito)));
            renglonesAuxiliar.add(a);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("BORRADOR DE COMPROBANTES DE RETENCION", null, seguridadbean.getLogueado().getUserid());
            for (Documentoselectronicos docelec : listadoFacturas) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Sr. (es) :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor().getEmpresa().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "RUC :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Dirección"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor().getDireccion()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(cabeceraDoc.getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Documento"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, docelec.getObligacion().getTipodocumento().getCodigo()
                        + "-" + docelec.getObligacion().getEstablecimiento()
                        + "-" + docelec.getObligacion().getPuntoemision()
                        + "-" + docelec.getObligacion().getDocumento()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "email"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getEmail()));

                pdf.agregarTabla(null, columnas, 4, 100, null);
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base Imponible"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cod. Impuesto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Impuesto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "% Ret."));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", docelec.getObligacion());
                List<Rascompras> ras = ejbDetalles.encontarParametros(parametros);
                columnas = new LinkedList<>();
                double totalRetenciones = 0;
                for (Rascompras r : ras) {
                    double valorDetalle = r.getValor().doubleValue();
                    if (!((r.getValorprima() == null) || (r.getValorprima().doubleValue() == 0))) {
                        valorDetalle = r.getValorprima().doubleValue();
                    }
                    if (r.getCba().equals("I")) {
                        if (r.getRetencion() != null) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, valorDetalle));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getEtiqueta().trim()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "FUENTE"));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getPorcentaje().toString()));
                            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getNombre()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValorret().doubleValue()));
                            totalRetenciones += r.getValorret().doubleValue();

                        }
                        if (r.getRetimpuesto() != null) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getValorimpuesto().doubleValue()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetimpuesto().getEtiqueta().trim()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "IVA"));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getRetimpuesto().getPorcentaje().toString()));
                            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetimpuesto().getNombre()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getVretimpuesto().doubleValue()));
                            totalRetenciones += r.getVretimpuesto().doubleValue();
                        }
                    } else if (r.getCba().equals("C")) {
                        if (r.getRetencion() != null) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, valorDetalle));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getEtiqueta().trim()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "FUENTE"));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getPorcentaje().toString()));
                            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getNombre()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValorret().doubleValue()));
                            totalRetenciones += r.getValorret().doubleValue();
                        }
                        if (r.getRetimpuesto() != null) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getValorimpuesto().doubleValue()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetimpuesto().getEtiqueta().trim()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "IVA"));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetimpuesto().getPorcentaje().toString()));
                            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetimpuesto().getNombre()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getVretimpuesto().doubleValue()));
                            totalRetenciones += r.getVretimpuesto().doubleValue();
                        }
                    }

                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalRetenciones));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "");
                pdf.agregaParrafo("\n\n");

            }

            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioContabilizar.insertar();
//        formularioObligacion.editar();
//        formulario.editar();
        return null;
    }

    public String grabarContabilizar() {
        try {
//            ejbDocElec.contabilizar(listadoFacturas, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo());
            List<Renglones> reng = new LinkedList<>();
            List<Renglones> rengInv = new LinkedList<>();
            for (Renglones r : renglones) {
                if (!r.getCuenta().contains("TOTALES")) {
                    if (r.getAuxiliarNombre() == null) {
                        reng.add(r);
                    } else {
                        rengInv.add(r);
                    }
                }
            }
            ejbDocElec.contabilizarRenglones(cabeceraDoc, seguridadbean.getLogueado().getUserid(),
                    1, perfil.getMenu().getIdpadre().getModulo(),1,renglones,null,null,null,null,null);
            for (Documentoselectronicos d : listadoFacturas) {
                Obligaciones o = d.getObligacion();
                o.setFechacontable(o.getFechaemision());
                o.setEstado(2);
                ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
            }
            cabeceraDoc.setContabilizado(true);
            ejbCabdocelect.edit(cabeceraDoc, seguridadbean.getLogueado().getUserid());
        } catch (Exception ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir(cabeceraDoc);
        buscar();
        formularioContabilizar.cancelar();
        return null;
    }

    public String nuevo() {
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }
        cabeceraDoc = new Cabdocelect();
        cabeceraDoc.setFecha(new Date());
        cabeceraDoc.setValor(BigDecimal.ZERO);
        listadoFacturas = new LinkedList<>();
        listadoObligaciones = new LinkedList<>();
        verFacturas = false;
        Map parametros = new HashMap();
        // traer los documentos
        parametros.put(";where", "o.ruc=:ruc and o.tipo=0 and o.cabeccera is null");
        parametros.put("ruc", proveedorBean.getProveedor().getEmpresa().getRuc());
        try {

            listadoFacturas = ejbDocElec.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioObligacion.insertar();
        formulario.editar();
        return null;
    }

    public String imprimir(Cabdocelect cabeceraDoc) {
        Map parametros = new HashMap();
        this.cabeceraDoc = cabeceraDoc;
        parametros.put(";where", "o.cabeccera=:cabeceraDoc");
        parametros.put("cabeceraDoc", cabeceraDoc);
        listadoFacturas = new LinkedList<>();
        listadoObligaciones = new LinkedList<>();
        verFacturas = false;
        try {

            listadoFacturas = ejbDocElec.encontarParametros(parametros);

            List<Renglones> renglones = traerRenglones();
            formularioImpresion.insertar();
//        Hacer el reporte
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("ORDEN DE PAGO NO : " + cabeceraDoc.getId(), null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(cabeceraDoc.getFecha())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor().getEmpresa().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "email"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getEmail()));
//            if (obligacion.getElectronica() == null) {
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Retención : "));
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, obligacion.getEstablecimientor() + "-" + obligacion.getPuntor() + "-" + obligacion.getNumeror()));
//            } else {
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "R.E.C. Accceso"));
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, obligacion.getClaver()));
//            }
//            if (obligacion.getContrato() == null) {
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true,"SIN CONTRATO"));
//            } else {
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
//                columnas.add(new AuxiliarReporte("String",1, AuxiliarReporte.ALIGN_LEFT, 6, true, obligacion.getContrato().toString()));
//            }

            pdf.agregarTabla(null, columnas, 2, 100, null);
            // un parafo
            pdf.agregaParrafo("Concepto : " + cabeceraDoc.getObservaciones() + "\n\n");
            DecimalFormat df = new DecimalFormat("000000");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo de Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No."));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Retención"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Retención FUENTE"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Retención IVA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Anticipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));

            double total = 0;
            columnas = new LinkedList<>();
            List<Rascompras> detalleImpresion = new LinkedList<>();
            double valorTotal = 0;
            double valorTotalIva = 0;
            double valorTotalFuente = 0;
            double valorTotalanticipos = 0;
            for (Documentoselectronicos d : listadoFacturas) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getTipodocumento().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,
                        d.getObligacion().getEstablecimiento() + "-" + d.getObligacion().getPuntoemision() + "-"
                        + df.format(d.getObligacion().getDocumento() == null ? 0 : d.getObligacion().getDocumento())));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getConcepto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getEstablecimientor() + "-"
                        + d.getObligacion().getPuntor() + "-"
                        + df.format(d.getObligacion().getNumeror() == null ? 0 : d.getObligacion().getNumeror())));

                double valor = d.getValortotal().doubleValue();
                total += valor;

                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Rascompras> ras = ejbDetalles.encontarParametros(parametros);
                List<Pagosvencimientos> listaPagos = ejbPagos.encontarParametros(parametros);
                double retIva = 0;
                double retFuente = 0;
                double valorAnticipo = 0;
                // Anticpos
                for (Pagosvencimientos p : listaPagos) {

                    if (p.getAnticipo() != null) {
                        Anticipos a = p.getAnticipo();
                        valorAnticipo += p.getValoranticipo().doubleValue();
                    }
                }
                valorTotalanticipos += valorAnticipo;
                for (Rascompras r : ras) {
                    detalleImpresion.add(r);
                    // ver las retenciones traer de abajo

                    if (r.getCba().equals("I")) {
                        if (r.getRetencion() != null) {
                            retFuente += r.getValorret().doubleValue();

                        }
                        if (r.getRetimpuesto() != null) {
                            retIva += r.getVretimpuesto().doubleValue();
                        }
                    } else if (r.getCba().equals("C")) {
                        if (r.getRetencion() != null) {
                            retFuente += r.getValorret().doubleValue();
                        }
                        if (r.getRetimpuesto() != null) {
                            retIva += r.getVretimpuesto().doubleValue();
                        }
                    }
                }
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retFuente));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retIva));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorAnticipo));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        valor - (retIva + retFuente + valorAnticipo)));
                valorTotalFuente += retFuente;
                valorTotalIva += retIva;
                valorTotal += valor;
            }

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalFuente));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalIva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalanticipos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal
                    - (valorTotalFuente + valorTotalIva + valorTotalanticipos)));
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, "DOCUMENTOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
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
//            if (renglonesInv != null) {
//                for (Renglones r : renglonesInv) {
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
//                    double valor = r.getValor().doubleValue();
//                    if (valor > 0) {
//                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
//                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
//                        sumaDebitos += valor;
//                    } else {
//                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
//                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
//                        sumaCreditos += valor * -1;
//                    }
//                }
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
//                pdf.agregarTablaReporte(titulos, columnas, 7, 100, "RECLASIFICACION");
//            }
            pdf.agregaParrafo("\n\n");
//            titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
//
//            columnas = new LinkedList<>();
//            for (AuxiliarCarga a : totales) {
//                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, a.getTotal()));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, a.getIngresos().doubleValue()));
//            }
//            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "RESUMEN");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            double totalCompromiso = 0;
            Detallecompromiso detalleCompromiso = null;
            valorTotal = 0;
            for (Rascompras d : detalleImpresion) {
                if (detalleCompromiso != null) {
                    if (!detalleCompromiso.equals(d.getDetallecompromiso())) {
                        // cambio
                        columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6,
                                false, detalleCompromiso.toString()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                                valorTotal));
                        valorTotal = 0;
                        detalleCompromiso = d.getDetallecompromiso();
                    }

                } else {
                    detalleCompromiso = d.getDetallecompromiso();
                }
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
                valorTotal += valorImpuesto;

            }
            if (detalleCompromiso != null) {

                // cambio
                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6,
                        false, detalleCompromiso.toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        valorTotal));
            }
            columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL "));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCompromiso));
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "AFECTACION PRESUPUESTARIA");
            pdf.agregaParrafo("\n\n");
            Codigos textoCodigo = getCodigosBean().traerCodigo("PAGOS", "PAGOS");
            String texto = "";
            if (textoCodigo != null) {
                texto = textoCodigo.getParametros().replace("#compromiso#", cabeceraDoc.getCompromiso().getId().toString());
            }

            pdf.agregaParrafo("Programación de Caja : \n\n");
            pdf.agregaParrafo(texto);
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Pagado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));

            pdf.agregarTabla(null, columnas, 4, 100, null);
            setReporte(pdf.traerRecurso());

//        formulario.editar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrar(Cabdocelect cabeceraDoc) {
        try {
            Map parametros = new HashMap();
            this.cabeceraDoc = cabeceraDoc;
            parametros.put(";where", "o.cabeccera=:cabeceraDoc");
            parametros.put("cabeceraDoc", cabeceraDoc);
            listadoFacturas = new LinkedList<>();
            listadoObligaciones = new LinkedList<>();
            verFacturas = false;
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            List<Renglones> renglones = traerRenglones();
            // ver si tiene generado kardex banco
            for (Documentoselectronicos d : listadoFacturas) {
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Pagosvencimientos> pagos = ejbPagos.encontarParametros(parametros);
                for (Pagosvencimientos p : pagos) {
                    if (p.getKardexbanco() != null) {
                        MensajesErrores.fatal("No se puede borrar ya esta hecha la nota de egreso");
                        return null;
                    }
                }
            }
            for (Documentoselectronicos d : listadoFacturas) {
                parametros = new HashMap();
                Obligaciones obligacionBorrar = d.getObligacion();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Pagosvencimientos> pagos = ejbPagos.encontarParametros(parametros);
                List<Rascompras> detallesCompras = ejbDetalles.encontarParametros(parametros);
                for (Pagosvencimientos p : pagos) {
                    ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
                }

                for (Rascompras r : detallesCompras) {
                    double valorImpuesto = 0;
                    if (r.getImpuesto() != null) {
                        valorImpuesto = r.getValor().doubleValue()
                                * r.getImpuesto().getPorcentaje().doubleValue() / 100;
                        if (r.getBaseimponibleimpuesto() != null) {
                            if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
                                valorImpuesto = Math.round(r.getBaseimponibleimpuesto().doubleValue()
                                        * r.getImpuesto().getPorcentaje().doubleValue());
                            }
                        }
                    }
                    Detallecompromiso det = r.getDetallecompromiso();
                    if (det != null) {
//                    ************************OJO VER RECONSTRUCCION DE SALDO
                        parametros = new HashMap();
                        parametros.put(";where", "o.detallecompromiso=:compromiso");
                        parametros.put("compromiso", det);
                        parametros.put(";campo", "o.valor");
                        double sumado = ejbRenglones.sumarCampo(parametros).doubleValue();
//                        double saldo = det.getValor().doubleValue() - sumado;
                        double saldo = sumado - valorImpuesto;
                        det.setSaldo(new BigDecimal(saldo));
                        ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
                    }
                    ejbDetalles.remove(r, seguridadbean.getLogueado().getUserid());
                }

                if (d.getTipo() == 1) {
                    ejbDocElec.remove(d, seguridadbean.getLogueado().getUserid());
                } else {
                    d.setUtilizada(false);
                    d.setCabeccera(null);
                    d.setObligacion(null);
                    ejbDocElec.edit(d, seguridadbean.getLogueado().getUserid());
                }
                ejbObligaciones.remove(obligacionBorrar, seguridadbean.getLogueado().getUserid());

            }
            ejbCabdocelect.remove(cabeceraDoc, seguridadbean.getLogueado().getUserid());
            Cabeceras c = null;
            for (Renglones r : renglones) {
                c = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());
            }
//            for (Renglones r : renglonesInv) {
//                c = r.getCabecera();
//                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
//            }
//            if (c != null) {
//                ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());
//            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BorrarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.advertencia("Se ha borrado correctamente");
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
    }

    public String modificarFactura(Documentoselectronicos factura) {
        this.factura = factura;
        this.obligacion = factura.getObligacion();

//        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            pago = new Pagosvencimientos();
            setDetalles(ejbDetalles.encontarParametros(parametros));
            List<Pagosvencimientos> listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosvencimientos p : listaPagos) {
                pago = p;
            }

//            compromiso = obligacion.getCompromiso();
//            if (detCompromiso == null) {
//                MensajesErrores.advertencia("No existe partidas para este proveedor");
//                return null;
//            }
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            proveedorBean.setProveedor(obligacion.getProveedor());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                facturaBean.setFactura(null);
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
                facturaBean.cargar(obligacion.getFacturaelectronica());
            }
            if (obligacion.getPuntor() != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                parametros.put("codigo", obligacion.getPuntor());
                parametros.put("sucursal", obligacion.getEstablecimientor());
                List<Puntoemision> lpt = ejbPuntos.encontarParametros(parametros);
                for (Puntoemision p : lpt) {
                    puntoEmision = p;
//                    puntosBean.setSucursal(p.getSucursal());

                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
//        generaRetElectronica(true, true);
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.editar();
        calculaTotal();
        return null;
    }

    public String borraFactura(Documentoselectronicos factura) {
        this.factura = factura;
        this.obligacion = factura.getObligacion();

//        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            setDetalles(ejbDetalles.encontarParametros(parametros));

            pago = new Pagosvencimientos();
            setDetalles(ejbDetalles.encontarParametros(parametros));
            List<Pagosvencimientos> listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosvencimientos p : listaPagos) {
                pago = p;
            }
//            if (detCompromiso == null) {
//                MensajesErrores.advertencia("No existe partidas para este proveedor");
//                return null;
//            }
//            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
            if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
                // factura manual
                // buscar autorización
                facturaBean.setFactura(null);
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
                facturaBean.cargar(obligacion.getFacturaelectronica());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.eliminar();
        calculaTotal();
        return null;
    }

    public String nuevaFactura(Documentoselectronicos factura) {
//        this.obligacion = obligacion;
        if (cabeceraDoc.getCompromiso() == null) {
            MensajesErrores.advertencia("Seleccione un compromiso primero");
            return null;
        }
        if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Por favor indique el motivo");
            return null;
        }
        if ((cabeceraDoc.getValor() == null) || (cabeceraDoc.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Por favor indique un valor");
            return null;
        }
        if (cabeceraDoc.getId() == null) {
            try {
                ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());
            } catch (InsertarException ex) {
                Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.factura = factura;
        this.factura.setCabeccera(cabeceraDoc);
        compromiso = cabeceraDoc.getCompromiso();
        obligacion = new Obligaciones();
        Empresas e = proveedorBean.taerRuc(factura.getRuc());
//        obligacion.setProveedor(cabeceraDoc.getProveedor());
        obligacion.setContrato(null);
        obligacion.setConcepto(compromiso.getMotivo());
        obligacion.setFechaemision(new Date());
        obligacion.setEstado(0); // No se si 1 o que
        obligacion.setProveedor(e.getProveedores()); // No se si 1 o que
        obligacion.setFechaingreso(new Date());
        obligacion.setUsuario(seguridadbean.getLogueado());
        detalles = new LinkedList<>();
//        facturaBean.setFactura(null);
//  trer la informacióndel punto electronico
        Map paraemtros = new HashMap();
        paraemtros.put(";where", "o.automatica=true");
        try {
            List<Puntoemision> listaPunto = ejbPuntos.encontarParametros(paraemtros);
            for (Puntoemision pe : listaPunto) {
                puntoEmision = pe;
            }
            obligacion.setPuntoemision(puntoEmision.getCodigo());
            obligacion.setEstablecimientor(puntoEmision.getSucursal().getRuc());
            obligacion.setFechar(cabeceraDoc.getFecha());
            generaRetElectronica(true, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        proveedorBean.setEmpresa(e);
        proveedorBean.setProveedor(e.getProveedores());
        obligacion.setFacturaelectronica(factura.getXml());
        facturaBean.cargar(obligacion.getFacturaelectronica());
        pago = new Pagosvencimientos();
//        promesas = new LinkedList<>();
//        promesasb = new LinkedList<>();
//        formulario.cancelar();
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.insertar();
        obligacion.setSustento(codigosBean.getSustentoDefault());
//        calculaTotal();
        return null;
    }

    public String nuevaFacturaManual() {
//        this.obligacion = obligacion;
        autorizacion = null;
        puntoEmision = null;

        if (cabeceraDoc.getCompromiso() == null) {
            MensajesErrores.advertencia("Seleccione un compromiso primero");
            return null;
        }
        if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Por favor indique el motivo");
            return null;
        }
        if ((cabeceraDoc.getValor() == null) || (cabeceraDoc.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Por favor indique un valor");
            return null;
        }
        if (cabeceraDoc.getId() == null) {
            try {
                ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());
            } catch (InsertarException ex) {
                Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        compromiso = cabeceraDoc.getCompromiso();
        this.factura = new Documentoselectronicos();
        factura.setRuc(cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc());
        factura.setTipo(1);
        this.factura.setCabeccera(cabeceraDoc);

        obligacion = new Obligaciones();
        Empresas e = proveedorBean.taerRuc(factura.getRuc());
//        obligacion.setProveedor(cabeceraDoc.getProveedor());
        obligacion.setContrato(null);
        obligacion.setFechaemision(new Date());
        obligacion.setEstado(0); // No se si 1 o que
        obligacion.setProveedor(e.getProveedores()); // No se si 1 o que
        obligacion.setFechaingreso(new Date());
        obligacion.setConcepto(compromiso.getMotivo());
        obligacion.setUsuario(seguridadbean.getLogueado());
        detalles = new LinkedList<>();
        facturaBean.setFactura(null);
//  trer la informacióndel punto electronico
        Map paraemtros = new HashMap();
        paraemtros.put(";where", "o.automatica=true");
        try {
            List<Puntoemision> listaPunto = ejbPuntos.encontarParametros(paraemtros);
            for (Puntoemision pe : listaPunto) {
                puntoEmision = pe;
            }
            obligacion.setPuntoemision(puntoEmision.getCodigo());
            obligacion.setEstablecimientor(puntoEmision.getSucursal().getRuc());
            obligacion.setFechar(cabeceraDoc.getFecha());
            generaRetElectronica(true, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        proveedorBean.setEmpresa(e);
        proveedorBean.setProveedor(e.getProveedores());
        obligacion.setFacturaelectronica(null);
        obligacion.setUsuario(seguridadbean.getLogueado());
//        promesas = new LinkedList<>();
//        promesasb = new LinkedList<>();
        pago = new Pagosvencimientos();
//        facturaBean.cargar();
//        formulario.cancelar();
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.insertar();
        obligacion.setSustento(codigosBean.getSustentoDefault());
//        calculaTotal();
        return null;
    }

    public String eliminar(Cabdocelect cabeceraDoc) {
        this.setCabeceraDoc(cabeceraDoc);

        if (cabeceraDoc.getContabilizado() != null) {
            MensajesErrores.advertencia("Ya esta contabilizada");
            return null;
        }
        getFormulario().eliminar();

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
     * @return the cabecerasDocumentos
     */
    public LazyDataModel<Cabdocelect> getCabdocelect() {
        return cabecerasDocumentos;
    }

    /**
     * @param cabecerasDocumentos the cabecerasDocumentos to set
     */
    public void setCabdocelect(LazyDataModel<Cabdocelect> cabecerasDocumentos) {
        this.cabecerasDocumentos = cabecerasDocumentos;
    }

    /**
     * @return the formularioImpresion
     */
    public Formulario getFormularioImpresion() {
        return formularioImpresion;
    }

    /**
     * @param formularioImpresion the formularioImpresion to set
     */
    public void setFormularioImpresion(Formulario formularioImpresion) {
        this.formularioImpresion = formularioImpresion;
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

//   
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
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
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
     * @return the obligaciones
     */
    public List<Obligaciones> getObligaciones() {
        return obligaciones;
    }

    /**
     * @param obligaciones the obligaciones to set
     */
    public void setObligaciones(List<Obligaciones> obligaciones) {
        this.obligaciones = obligaciones;
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

    public String borraRas(Rascompras r) {
        if (detallesb == null) {
            detallesb = new LinkedList<>();
        }

        detallesb.add(r);
        detalles.remove(formularioDetalle.getFila().getRowIndex());
        return null;
    }

//    public String nuevoDetalle() {
//        if (obligacion.getProveedor() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor primero");
//            return null;
//        }
//        detalle = new Rascompras();
//        detalle.setBien(Boolean.FALSE);
//        detalle.setDetallecabeceraDoc(detCompromiso);
//        formularioDetalle.insertar();
//        detalleModificar = new Rascompras();
//        detalleModificar.setValor(BigDecimal.ZERO);
//
//        setIva((Boolean) false);
//        return null;
//    }
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

    public String nuevoDetalle() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        grabaObligacion();
        detalle = new Rascompras();
        detalle.setBien(Boolean.FALSE);
        detalle.setReferencia(compromiso.getMotivo());
        setDetallesInternos(new LinkedList<>());
//        formularioCompromiso.insertar();
        formularioDetalle.insertar();
        setSoloRetencion(false);
        detalleModificar = new Rascompras();
        detalleModificar.setValor(BigDecimal.ZERO);
        detalleModificar.setValorimpuesto(BigDecimal.ZERO);
        renglonesInternos = new LinkedList<>();
        List<Detallecompromiso> lista = traerDetalleCompromiso();
        if (lista == null) {
            lista = new LinkedList<>();
        }
        // lena el nuevo detalle
        for (Detallecompromiso d : lista) {
            Rascompras r = new Rascompras();
            r.setDetallecompromiso(d);

            String cuentaPresupuesto = d.getAsignacion().getClasificador().getCodigo();
            Map parametros = new HashMap();
            parametros.put(";where", "o.presupuesto=:presupuesto");
            parametros.put("presupuesto", cuentaPresupuesto);
            try {
                List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
                for (Cuentas cu : cl) {
                    r.setIdcuenta(cu);
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            renglonesInternos.add(r);
        }
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
        setDetallesInternos(new LinkedList<>());
//        formularioCompromiso.insertar();
        cuentaProveedor = null;
        formularioDetalleCuenta.insertar();
        detalleModificar = new Rascompras();
        detalleModificar.setValor(BigDecimal.ZERO);
        setSoloRetencion(false);
        iva = false;
        return null;
    }

    public String modificaDetalle(Integer fila) {
        formularioDetalle.setIndiceFila(fila);
        detalle = detalles.get(fila);
        iva = false;
        double valorComparar = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
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
            setSoloRetencion(false);
        } else if (detalle.getValorprima().doubleValue() == 0) {
            setSoloRetencion(false);
        } else {
            setSoloRetencion(true);
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
                obligacion.setUsuario(seguridadbean.getLogueado());
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
            ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
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
                obligacion.setUsuario(seguridadbean.getLogueado());
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
            ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
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
            ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
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

    ///////////////////////////////////////////////////FIN OJO
    /**
     * @return the iva
     */
    public Boolean getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(Boolean iva) {
        this.iva = iva;
    }

    public double getValorObligacion() {
        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            retorno = ejbDetalles.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double taerValorObligacion(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            retorno = ejbDetalles.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String salirObligacion() {
        formulario.cancelar();
        if (detalles != null) {
            for (Rascompras r : detalles) {
                Detallecompromiso c = r.getDetallecompromiso();
                double saldo = c.getSaldo().doubleValue() + (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue());
                c.setSaldo(new BigDecimal(saldo));
                try {
                    ejbDetalleCompromiso.edit(c, seguridadbean.getLogueado().getUserid());
                } catch (GrabarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        formularioObligacion.cancelar();
        return null;
    }

    public String salirModificacion() {
//        salirTotal();
//        obligacion = new Obligaciones();
        detalles = null;
        detallesb = null;
        facturaBean.setFactura(null);
        formularioObligacion.insertar();

        modificar(getCabeceraDoc());
        return null;
    }

    public SelectItem[] getComboCuentas() {

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

    public SelectItem[] getComboCuentasNuevo() {
        Rascompras r = (Rascompras) formularioDetalleDetalle.getFila().getRowData();
        Detallecompromiso d = r.getDetallecompromiso();
        if (d == null) {
            return null;
        }
        // solo para quitar el error
        String cuentaPresupuesto = d.getAsignacion().getClasificador().getCodigo();
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
                    valorSinCuentas += r.getValorprima() == null ? 0 : r.getValorprima().doubleValue();
                    vRetenciones += r.getValorret().doubleValue();
                    if (r.getImpuesto() != null) {
                        if (r.getValorprima() != null) {
                            if (r.getBaseimponibleimpuesto() != null) {
                                if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
                                    total += r.getBaseimponibleimpuesto().doubleValue()
                                            * (r.getImpuesto().getPorcentaje().doubleValue() / 100);
                                }

                            } else {
                                total += r.getValor().doubleValue() * (r.getImpuesto().getPorcentaje().doubleValue() / 100);
                            }
                        } else {
                            total += r.getValorimpuesto().doubleValue();
                        }

                        vRetenciones += r.getVretimpuesto() == null ? 0 : r.getVretimpuesto().doubleValue();
                    }
                    break;
            }
        }

        a.setIngresos(new BigDecimal(total));
        if (factura.getTipo() == 1) {
            factura.setValortotal(new BigDecimal(total));
        }
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
//        if (promesas == null) {
//            promesas = new LinkedList<>();
//        } else {
//            if (promesasb == null) {
//                promesasb = new LinkedList<>();
//            }
//            for (Pagosvencimientos p : promesas) {
//                promesasb.add(p);
//            }
//            promesas = new LinkedList<>();
//            Pagosvencimientos p = new Pagosvencimientos();
        pago.setFecha(new Date());
        pago.setFechapago(new Date());
        pago.setPagado(false);
        pago.setObligacion(obligacion);
//            p.setValor(new BigDecimal(total + vAnticipos - vRetenciones ));
        pago.setValor(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
        vPagos += pago.getValor().doubleValue();
//            promesas.add(p);
//        }

//        for (Pagosvencimientos p : promesas) {
//            vPagos += p.getValor().doubleValue();
//        }
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
//                    return;
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
//                    return;
                }
            }
            for (Rascompras r : detalles) {
                double valorDetalle = r.getValor().doubleValue();
                if (!((r.getValorprima() == null) || (r.getValorprima().doubleValue() == 0))) {
                    valorDetalle = r.getValorprima().doubleValue();
                }
                if (r.getCba().equals("I")) {
                    if (r.getRetencion() != null) {
                        comprobante.cargaImpuesto(r.getImpuesto().getEtiqueta().trim(),
                                r.getRetencion().getEtiqueta().trim(), sdf.format(obligacion.getFechaemision()), r.getRetencion().getPorcentaje().intValue(),
                                codigoDocSustento, df.format(valorDetalle),
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
                                codigoDocSustento, df.format(valorDetalle),
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
                comprobante.calculaClave(dfAdicional.format(compromiso.getId()));
            } else {
                if (codigoAlterno) {
                    comprobante.calculaClave(dfAdicional.format(compromiso.getId()));
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

    public String recalcular() {
        calculaTotal();
        return null;
    }

    /**
     *
     * @return
     */
    public String garbarTodo() {

        // validar el total de obligaciones con el total de comrpomisos
        try {
            if (cabeceraDoc.getCompromiso() == null) {
                MensajesErrores.advertencia("Seleccione un compromiso primero");
                return null;
            }
            if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
                MensajesErrores.advertencia("Por favor indique el motivo");
                return null;
            }
            if ((cabeceraDoc.getValor() == null) || (cabeceraDoc.getValor().doubleValue() <= 0)) {
                MensajesErrores.advertencia("Por favor indique un valor");
                return null;
            }
            double valorApagar = 0;
            for (Documentoselectronicos d : listadoObligaciones) {
                valorApagar += d.getValortotal().doubleValue();
            }
            valorApagar = Math.round(valorApagar * 100) / 100;
            if (valorApagar > cabeceraDoc.getValor().doubleValue()) {
                MensajesErrores.advertencia("Valor de facturas excede el límite de orden de pago");
                return null;
            }
            if (cabeceraDoc.getId() == null) {
                try {
                    ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());
                } catch (InsertarException ex) {
                    Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                ejbCabdocelect.edit(getCabeceraDoc(), seguridadbean.getLogueado().getUserid());
            }

//            double valorObligaciones = 0;
            double valorObligaciones = 0;

            // buscar pago
        } catch (Exception ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        salirObligacion();
//        traerRenglones();
//salirModificacion();
        formularioObligacion.cancelar();
        return null;
    }

    public String grabar() {
        if (puntoEmision != null) {

        }

        calculaTotal();
        if (validar()) {
            return null;
        }
        obligacion.setEstado(0);
        grabaObligacion();
//        getFormulario().cancelar();
        modificar(getCabeceraDoc());
        salirModificacion();
//        buscar();
        return null;
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

    public boolean validar() {
//        if ((obligacion.getValorcertificacion() == null) || (obligacion.getValorcertificacion().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de obligacion");
//            return true;
//        }
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion");
            return true;
        }
//        if ((obligacion.getCuentaproveedor() == null) || (obligacion.getCuentaproveedor().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario cuenta provedor");
//            return true;
//        }
//        if ((obligacion.getDetcertificacion() == null)) {
//            MensajesErrores.advertencia("Es necesario partida ");
//            return true;
//        }
        if (obligacion.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return true;
        }
        if (detalles.isEmpty()) {
            MensajesErrores.advertencia("Necesario cuentas de obligación");
            return true;
        }
//        if (promesas.isEmpty()) {
//            MensajesErrores.advertencia("Necesario promesas de pagos");
//            return true;
//        }
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
                    cuadre -= a.getIngresos().doubleValue();
                    egreso += a.getIngresos().doubleValue();
                    break;
            }
        }
//        if (obligacion.getFechaemision().after(obligacion.getCompromiso().getFecha())) {
//            MensajesErrores.advertencia("Fecha de emisión menor a fecha de cabeceraDoc");
//            return true;
//        }
        cuadre = Math.round(cuadre);
//        obligacion.setApagar(new BigDecimal(egreso));
//        if (cuadre != 0) {
//            MensajesErrores.advertencia("Egresos mayor a la cabeceraDoc presupuestario");
//            return true;
//        }
        if ((obligacion.getFacturaelectronica() == null) || (obligacion.getFacturaelectronica().isEmpty())) {
            // factura manual
            if (autorizacion == null) {
                MensajesErrores.advertencia("Seleccione una Autorización");
                return true;
            }
            if (obligacion.getFechaemision().before(autorizacion.getFechaemision())) {
                MensajesErrores.advertencia("Fecha de emisión menor a fecha de autorización");
                return true;
            }
            if (obligacion.getFechaemision().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Fecha de emisión menor a periodo vigente");
                return true;
            }
            if (obligacion.getFechaemision().after(autorizacion.getFechacaducidad())) {
                MensajesErrores.advertencia("Fecha de emisión mayor a fecha de caducidad");
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
            obligacion.setAutorizacion(autorizacion.getAutorizacion());
            obligacion.setTipodocumento(autorizacion.getTipodocumento());
            obligacion.setFechacaduca(autorizacion.getFechacaducidad());
            obligacion.setEstablecimiento(autorizacion.getEstablecimiento());
            obligacion.setPuntoemision(autorizacion.getPuntoemision());

            // validar que el numero sea unico
            Map parametros = new HashMap();
            if (obligacion.getId() == null) {
                parametros.put(";where", "o.establecimiento=:establecimiento and o.proveedor=:proveedor"
                        + " and o.puntoemision=:puntoemision and o.documento=:documento and o.estado!=-1");
            } else {
                parametros.put(";where", "o.establecimiento=:establecimiento and o.proveedor=:proveedor"
                        + " and o.puntoemision=:puntoemision and o.documento=:documento and o.id!=:id and o.estado!=-1");
                parametros.put("id", obligacion.getId());
            }
            parametros.put("establecimiento", obligacion.getEstablecimiento());
            parametros.put("proveedor", obligacion.getProveedor());
            parametros.put("puntoemision", obligacion.getPuntoemision());
            parametros.put("documento", obligacion.getDocumento());
            try {
                int total = ejbObligaciones.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Factura ya registrada" + obligacion.getDocumento());
                    return true;
                }

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            obligacion.setPuntoemision(autorizacion.getPuntoemision());

        } else {
            // factura electronica
            double importeTotal = Double.parseDouble(facturaBean.getFactura().getInfoFactura().getImporteTotal()) - egreso;
            egreso = egreso * 100;
            if (Math.round(importeTotal) != 0) {
                MensajesErrores.advertencia("Importe total no cuadra con los egresos en la factura");
                return true;
            }
            if (!facturaBean.getFactura().getInfoTributaria().getRuc().equals(obligacion.getProveedor().getEmpresa().getRuc())) {
                MensajesErrores.advertencia("RUC de factura electrónica no corresponde al emisor");
                return true;
            }
            if (!facturaBean.getFactura().getInfoFactura().getIdentificacionComprador().equals(configuracionBean.getConfiguracion().getRuc())) {
                MensajesErrores.advertencia("Factura no esta emitidad para " + configuracionBean.getConfiguracion().getNombre());
                return true;
            }
            Map parametros = new HashMap();
            if (obligacion.getId() == null) {
                parametros.put(";where", "o.autorizacion=:autorizacion and o.estado!=-1");
            } else {
                parametros.put(";where", "o.autorizacion=:autorizacion and o.id!=:id and o.estado!=-1");
                parametros.put("id", obligacion.getId());
            }
            parametros.put("autorizacion", facturaBean.getFactura().getAutorizacion().getNumeroAutorizacion());
            try {
                int total = ejbObligaciones.contar(parametros);

                if (total > 0) {
                    MensajesErrores.advertencia("Factura ya registrada no autorización " + facturaBean.getFactura().getAutorizacion().getNumeroAutorizacion());
                    return true;
                }

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            // buscar clave de acceso en base de datos para ver si ya esta ingresada
            obligacion.setAutorizacion(facturaBean.getFactura().getAutorizacion().getNumeroAutorizacion());
            obligacion.setDocumento(Integer.parseInt(facturaBean.getFactura().getInfoTributaria().getSecuencial()));

//            obligacion.setFechacaduca(autorizacion.getFechacaducidad());
            obligacion.setEstablecimiento(facturaBean.getFactura().getInfoTributaria().getEstab());
            obligacion.setPuntoemision(facturaBean.getFactura().getInfoTributaria().getPtoEmi());
            obligacion.setElectronica(facturaBean.getFactura().getInfoTributaria().getSecuencial());
            obligacion.setFechaemision(facturaBean.getFactura().getFechaEmi());
            // buscar tipo de documento con el parametro de tipo para colocarlo
            List<Codigos> cl = codigosBean.traerCodigoParametros(Constantes.DOCUMENTOS, facturaBean.getFactura().getInfoTributaria().getCodDoc());
            for (Codigos c : cl) {
                obligacion.setTipodocumento(c);
            }
        }
        double suma = 0;
        for (Rascompras r : detalles) {
            suma += r.getValorret().doubleValue() + r.getVretimpuesto().doubleValue();
            if (r.getDetallecompromiso() != null) {
//                if (!(r.getDetallecompromiso().getFecha().equals(obligacion.getFechaemision()))) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                    MensajesErrores.advertencia("Fecha de detalle compromiso no corresponde a fecha de emisión : " + sdf.format(r.getDetallecompromiso().getFecha()));
//                    return true;
//                }
            }
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
                    MensajesErrores.advertencia("Fecha de emisión de retención menor a periodo vigente");
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
                obligacion.setPuntor(documeto.getPunto().getCodigo());
                obligacion.setEstablecimientor(documeto.getPunto().getSucursal().getRuc());
                obligacion.setAutoretencion(documeto.getAutorizacion());
                /// valida que no se dupliqque el no retención
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
                if ((obligacion.getProveedor().getEmpresa().getEmail() == null) || (obligacion.getProveedor().getEmpresa().getEmail().isEmpty())) {
                    MensajesErrores.advertencia("Necesario que proveedor cuente con un correo electrónico");
                    return true;
                }
            }
        }
        // validar saldo en cabecerasDocumentos
        if (pago.getAnticipo() != null) {
            // ver si el valor corresponde
            double cuantoAnticipo = obligacion.getContrato().getAnticipo().doubleValue();
            double cuantoPago = pago.getAnticipo().getValor().doubleValue();
            if (cuantoAnticipo < cuantoPago) {
                MensajesErrores.advertencia("Valor del anticipo a aplicar mayor del valor del anticipo");
                return true;
            }
            Map parametros = new HashMap();

            parametros.put("anticipo", pago.getAnticipo());
            parametros.put(";campo", "o.valoranticipo");
            parametros.put(";where", "o.anticipo=:anticipo  and o.id!=:id");
            parametros.put("id", pago.getId());
            try {
                double valor = cuantoAnticipo - ejbPagos.sumarCampo(parametros).doubleValue();

//            pago.setAnticipo(anticipoAplicar);
//            pago.setValoranticipo(new BigDecimal(cuantoAnticipo));
                if (valor < 0) {
                    MensajesErrores.advertencia("Anticipo ya aplicado");
                    return true;
                } else if (valor == 0) {

                    pago.getAnticipo().setAplicado(Boolean.TRUE);
                } else {
                    pago.getAnticipo().setAplicado(Boolean.FALSE);
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private void grabaObligacion() {

//            obligacion.setProveedor(proveedorBean.getEmpresa().getProveedores());
//            obligacion.setImprimecertificacion(Boolean.FALSE);
        try {
            calculaTotal();
            obligacion.setFechaingreso(new Date());
            if ((obligacion.getAutorizacion() == null) || (obligacion.getAutorizacion().isEmpty())) {
                if (autorizacion != null) {
                    obligacion.setTipodocumento(autorizacion.getTipodocumento());
                    obligacion.setAutorizacion(autorizacion.toString());
                }
            }

            if (puntoEmision != null) {
                obligacion.setPuntor(puntoEmision.getCodigo());
                obligacion.setEstablecimientor(puntoEmision.getSucursal().getRuc());
            }
//            obligacion.setEstado(1);
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            } else {
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            }
            // grabar ras

            // lo mismo para promesas
//            for (Pagosvencimientos p : promesas) {
//                p.setObligacion(obligacion);
//                if (p.getId() == null) {
            if (pago.getId() == null) {
                pago.setObligacion(obligacion);
                ejbPagos.create(pago, seguridadbean.getLogueado().getUserid());
            } else {
                pago.setObligacion(obligacion);
                ejbPagos.edit(pago, seguridadbean.getLogueado().getUserid());
            }

//                } else {
//                    ejbPagos.edit(p, seguridadbean.getLogueado().getUserid());
//                }
//            }
            // borrar
//            if (promesasb != null) {
//                for (Pagosvencimientos p : promesasb) {
//                    if (p.getId() == null) {
//                    } else {
//                        ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
//                    }
//                }
//            }
            factura.setObligacion(obligacion);
            factura.setUtilizada(true);
            factura.setFechaemision(obligacion.getFechaemision());
            if (autorizacion != null) {
                factura.setAutorizacion(autorizacion.toString());
            }
            DecimalFormat df = new DecimalFormat("000000");
            if (factura.getTipo() == 1) {
                factura.setClave(obligacion.getEstablecimiento() + "-"
                        + obligacion.getPuntoemision() + "-" + df.format(obligacion.getDocumento()));
            }
            if (factura.getId() == null) {
                ejbDocElec.create(factura, seguridadbean.getLogueado().getUserid());
            } else {
                ejbDocElec.edit(factura, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // grabar ras

    }

    public String eliminarObligacion() {
        try {
//            obligacion.setProveedor(proveedorBean.getEmpresa().getProveedores());
//            obligacion.setImprimecertificacion(Boolean.FALSE);
            for (Rascompras r : detalles) {
                Detallecompromiso det = r.getDetallecompromiso();
                if (det != null) {
                    double valor = r.getValor().doubleValue() + r.getValorimpuesto().doubleValue();
                    det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() + valor));
                    if (det.getSaldo() == null) {
                        det.setSaldo(BigDecimal.ZERO);
                    }
                    ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
                    ejbDetalles.remove(r, seguridadbean.getLogueado().getUserid());
                }
            }
            factura.setObligacion(null);
            factura.setUtilizada(false);
            factura.setCabeccera(null);
            if (factura.getTipo() == 1) {
                ejbDocElec.remove(factura, seguridadbean.getLogueado().getUserid());
            } else {
                ejbDocElec.edit(factura, seguridadbean.getLogueado().getUserid());
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);
            List<Pagosvencimientos> lista = ejbPagos.encontarParametros(parametros);
            for (Pagosvencimientos p : lista) {
                ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
            }
            ejbObligaciones.remove(obligacion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        salirModificacion();

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
            Logger
                    .getLogger(DocumentosEmisionBean.class
                            .getName()).log(Level.SEVERE, null, ex);
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
            Logger
                    .getLogger(RetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
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
            Logger
                    .getLogger(RetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public String booraFacturaElectronica() {
        obligacion.setFacturaelectronica(null);
        facturaBean.setFactura(null);
        return null;
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

    public SelectItem[] getComboCuentasPresupuesto() {
//        if (getCompromiso() == null) {
//            return null;
//        }

        // solo para quitar el error
//        String cuentaPresupuesto=obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();
//        parametros.put(";where", "o.cabeceraDoc=:cabeceraDoc and o.proveedor=:proveedor");
        parametros.put(";where", "o.compromiso=:compromiso  and o.saldo>0");
        parametros.put("compromiso", cabeceraDoc.getCompromiso());
//        parametros.put("proveedor", obligacion.getProveedor());
        try {
            List<Detallecompromiso> dl = ejbDetalleCompromiso.encontarParametros(parametros);
            return Combos.getSelectItems(dl, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(ObligacionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboAutorizaciones() {
        if (obligacion.getProveedor() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        if (obligacion == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento.codigo not in('RET')");
        parametros.put("empresa", obligacion.getProveedor().getEmpresa());
        parametros.put(";orden", "o.inicio desc");
        try {
            return Combos.getSelectItems(ejbAutorizaciones.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(AutorizacionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the formularioEditar
     */
    public Formulario getFormularioEditar() {
        return formularioEditar;
    }

    /**
     * @param formularioEditar the formularioEditar to set
     */
    public void setFormularioEditar(Formulario formularioEditar) {
        this.formularioEditar = formularioEditar;
    }

    public String getValorStr() {
        double retorno = 0;
        for (Obligaciones o : obligaciones) {
            retorno += o.getApagar().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    private List<Renglones> traerRenglones() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "and o.cabecera.modulo=:modulo and o.cabecera.opcion in ('OBLIGACIONES_LOTE','OBLIGACIONES_INV_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    /**
     * @return the renglonesAuxiliar
     */
    public List<AuxiliarCarga> getRenglonesAuxiliar() {
        return renglonesAuxiliar;
    }

    /**
     * @param renglonesAuxiliar the renglonesAuxiliar to set
     */
    public void setRenglonesAuxiliar(List<AuxiliarCarga> renglonesAuxiliar) {
        this.renglonesAuxiliar = renglonesAuxiliar;
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

    public SelectItem[] getComboAnticipos() {

        // solo para quitar el error
        if (obligacion == null) {
            return null;
        }
        if (obligacion.getContrato() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.contrato=:contrato and (o.aplicado=false or o.aplicado is null)";

        parametros.put(";where", where);
        parametros.put("contrato", obligacion.getContrato());
        try {
            return Combos.getSelectItems(ejbAnticipos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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

    public String salirTotal() {
        if (detalles != null) {
            for (Rascompras r : detalles) {
                Detallecompromiso c = r.getDetallecompromiso();
                double saldo = c.getSaldo().doubleValue() + (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue());
                c.setSaldo(new BigDecimal(saldo));
                try {
                    ejbDetalleCompromiso.edit(c, seguridadbean.getLogueado().getUserid());
                } catch (GrabarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
//        formulario.cancelar();
        return null;
    }

    /**
     * @return the cuentaproveedor
     */
    public Cuentas getCuentaProveedor() {
        return cuentaProveedor;
    }

    /**
     * @param cuentaproveedor the cuentaproveedor to set
     */
    public void setCuentaProveedor(Cuentas cuentaproveedor) {
        this.cuentaProveedor = cuentaproveedor;
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
     * @return the proveedorBeneficiario
     */
    public boolean isProveedorBeneficiario() {
        return proveedorBeneficiario;
    }

    /**
     * @param proveedorBeneficiario the proveedorBeneficiario to set
     */
    public void setProveedorBeneficiario(boolean proveedorBeneficiario) {
        this.proveedorBeneficiario = proveedorBeneficiario;
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

    /**
     * @return the factura
     */
    public Documentoselectronicos getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Documentoselectronicos factura) {
        this.factura = factura;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the listadoFacturas
     */
    public List<Documentoselectronicos> getListadoFacturas() {
        return listadoFacturas;
    }

    /**
     * @param listadoFacturas the listadoFacturas to set
     */
    public void setListadoFacturas(List<Documentoselectronicos> listadoFacturas) {
        this.listadoFacturas = listadoFacturas;
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
     * @return the cabeceraDoc
     */
    public Cabdocelect getCabeceraDoc() {
        return cabeceraDoc;
    }

    /**
     * @param cabeceraDoc the cabeceraDoc to set
     */
    public void setCabeceraDoc(Cabdocelect cabeceraDoc) {
        this.cabeceraDoc = cabeceraDoc;
    }

    /**
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    public void cambiaCompromiso(ValueChangeEvent evento) {
        Compromisos compromisoNuevo = (Compromisos) evento.getNewValue();
        if (compromisoNuevo != null) {
            if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
                cabeceraDoc.setObservaciones(compromisoNuevo.getMotivo());
            }
        }
    }

    /**
     * @return the verFacturas
     */
    public boolean isVerFacturas() {
        return verFacturas;
    }

    /**
     * @param verFacturas the verFacturas to set
     */
    public void setVerFacturas(boolean verFacturas) {
        this.verFacturas = verFacturas;
    }

    public String vistaObligaciones() {
        verFacturas = true;
        if (cabeceraDoc.getId() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabeccera=:cabecera");
        parametros.put("cabecera", cabeceraDoc);
        try {
            listadoObligaciones = ejbDocElec.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String vistaFacturas() {
        verFacturas = false;
        return null;
    }

    /**
     * @return the listadoObligaciones
     */
    public List<Documentoselectronicos> getListadoObligaciones() {
        return listadoObligaciones;
    }

    /**
     * @param listadoObligaciones the listadoObligaciones to set
     */
    public void setListadoObligaciones(List<Documentoselectronicos> listadoObligaciones) {
        this.listadoObligaciones = listadoObligaciones;
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
     * @return the renglonesInternos
     */
    public List<Rascompras> getRenglonesInternos() {
        return renglonesInternos;
    }

    /**
     * @param renglonesInternos the renglonesInternos to set
     */
    public void setRenglonesInternos(List<Rascompras> renglonesInternos) {
        this.renglonesInternos = renglonesInternos;
    }

    /**
     * @return the formularioDetalleDetalle
     */
    public Formulario getFormularioDetalleDetalle() {
        return formularioDetalleDetalle;
    }

    /**
     * @param formularioDetalleDetalle the formularioDetalleDetalle to set
     */
    public void setFormularioDetalleDetalle(Formulario formularioDetalleDetalle) {
        this.formularioDetalleDetalle = formularioDetalleDetalle;
    }

    /**
     * @return the cuentaPrueba
     */
    public Cuentas getCuentaPrueba() {
        return cuentaPrueba;
    }

    /**
     * @param cuentaPrueba the cuentaPrueba to set
     */
    public void setCuentaPrueba(Cuentas cuentaPrueba) {
        this.cuentaPrueba = cuentaPrueba;
    }
//    public void 

    public String getTotalInternoSeleccionado() {
        double retorno = 0;
        for (Rascompras r : renglonesInternos) {
            if (r.isSelecciona()) {
                retorno += r.getDetallecompromiso().getValor().doubleValue();
            }
        }
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(retorno);
    }
}