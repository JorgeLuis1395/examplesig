/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.xml.bind.JAXBException;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
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
import org.beans.sfccbdmq.DescuentoscomprasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallescomprasFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.InformesFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosExteriorFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.ReembolsosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.DocumentosEmisionBean;
import org.contabilidad.sfccbdmq.RetencionesBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Descuentoscompras;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallescompras;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.PagosExterior;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Reembolsos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.pagos.sfccbdmq.PagoSaldosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.wscliente.sfccbdmq.Empleado;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "pagoRetencionesSfccbdmq")
@ViewScoped
public class PagoRetencionesBean {

    /**
     * @return the pagosExterior
     */
    public PagosExterior getPagosExterior() {
        return pagosExterior;
    }

    /**
     * @param pagosExterior the pagosExterior to set
     */
    public void setPagosExterior(PagosExterior pagosExterior) {
        this.pagosExterior = pagosExterior;
    }

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PagoRetencionesBean() {
        Calendar c = Calendar.getInstance();

        cabecerasDocumentos = new LazyDataModel<Cabdocelect>() {

            @Override
            public List<Cabdocelect> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Cabdocelect> cabecerasDocumentos;
    private List<Obligaciones> obligaciones;
    private List<Documentoselectronicos> listadoFacturas;
    private List<Documentoselectronicos> listadoObligaciones;
    private Documentoselectronicos factura;
    private Obligaciones obligacion = new Obligaciones();
    private List<AuxiliarCarga> renglonesAuxiliar;
    private List<AuxiliarCarga> renglonesReclasificacion;
    private List<AuxiliarCarga> renglonesDescuento;
    private List<AuxiliarCarga> renglonesAnticipos;
    private List<AuxiliarCarga> renglonesMultas;
    private List<AuxiliarCarga> renglonesExterior;
    private List<Detallescompras> listaDetalleCompras;
    private List<Retencionescompras> listaRetencionesCompras;
    private List<Retencionescompras> listaRetencionesComprasInt;
    private List<Fondos> listaFondos;
    private List<Fondos> listaFondosGrabar;
    private Compromisos compromiso;
    private PagosExterior pagosExterior;
    private Cabdocelect cabeceraDoc;
    private Impuestos impuesto;
    private Autorizaciones autorizacion;
    private Documentos documento;
    private List<AuxiliarCarga> totales;
    private List<Renglones> renglones;
    private List<Reembolsos> reembolsos;
    private List<Descuentoscompras> listaDescuentos;
    private List<String> partidas;
    private Puntoemision puntoEmision;
    private Reembolsos reembolso;
    private Descuentoscompras descuento;
    private Codigos tipoDocumento;
    private boolean proveedorBeneficiario;
    private boolean soloRetencion;
    private boolean verFacturas;
    private boolean contabilizado = false;
    private Date fechaAnular = new Date();
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioEditar = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioDetalleCuenta = new Formulario();
    private Formulario formularioDetalleDetalle = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Formulario formularioMotivo = new Formulario();
    private Formulario formularioLote = new Formulario();
    private Formulario formularioReembolso = new Formulario();
    private Formulario formularioDescuento = new Formulario();
    private Formulario formularioanular = new Formulario();
    private Formulario formularioCruceFondos = new Formulario();
    @EJB
    private CabdocelectFacade ejbCabdocelect;
    @EJB
    private PagosExteriorFacade ejbPagosExterior;
    @EJB
    private DetallecompromisoFacade ejbDetalleCompromiso;
    @EJB
    private ObligacionesFacade ejbObligaciones;
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
    @EJB
    private DetallescomprasFacade ejbDetallesCompras;
    @EJB
    private RetencionescomprasFacade ejbRetencionesCompras;
    @EJB
    private InformesFacade ejbInformes;
    @EJB
    private DescuentoscomprasFacade ejbDescuentos;
    @EJB
    private ReembolsosFacade ejbReembolsos;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private FondosFacade ejbFondos;

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
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    private Date desde;
    private String motivo;
    private Integer numero;
    private Date hasta;
    private Date fechaEmision;
    private Boolean iva;
    private boolean empleado;
    private ComprobanteRetencion comprobante;
    private Retencionescompras retencion;
    private Retencionescompras retencionLote;
    private Pagosvencimientos pago;
    private Resource reporte;
    private Formulario formularioReclasificacion = new Formulario();
    private Formulario formularioFechaEmision = new Formulario();
    private int contRetenciones = 1;
    // renglones para contabilizar
    private List<Renglones> renglonesAnticiposLocal;
    private List<Renglones> renglonesReclasificacionLocal;
    private List<Renglones> renglonesMultasLocal;
    private List<Renglones> renglonesDescuentoLocal;
    private List<Renglones> renglonesExteriorLocal;
    //

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

    public List<Cabdocelect> cargar(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
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
        if (contabilizado) {
            where += " and o.contabilizado=true";
        } else {
            where += " and (o.contabilizado=false or o.contabilizado is null)";
        }
        if (!((motivo == null) || (motivo.isEmpty()))) {
            where += " and upper(o.observaciones) like :motivo";
            parametros.put("motivo", motivo + "%");
        }
        if (proveedorBean.getProveedor() != null) {
            where += " and o.compromiso.proveedor=:proveedor";
            parametros.put("proveedor", proveedorBean.getProveedor());
        }
        if (entidadesBean.getEntidad() != null) {
            where += " and o.compromiso.beneficiario=:beneficiario";
            parametros.put("beneficiario", entidadesBean.getEntidad().getEmpleados());
        }
        if (getCompromiso() != null) {
            where += " and o.compromiso=:compromiso";
            parametros.put("compromiso", getCompromiso());
        }
        if (!((numero == null) || (numero <= 0))) {
            where = " o.id=:id";
            parametros = new HashMap();
            parametros.put("id", numero);
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

    public String buscar() {
        cabecerasDocumentos = new LazyDataModel<Cabdocelect>() {
            @Override
            public List<Cabdocelect> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargar(i, pageSize, scs, map);
            }
        };

        return null;
    }

    private Proveedores traeDetalle(Proveedores p, List<Proveedores> pl) {
        for (Proveedores d : pl) {
            if (d.equals(p)) {
                return null;
            }
        }
        return p;
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
        List<Compromisos> listadoCompromisos = new LinkedList<>();
        if (proveedorBean.getProveedor() == null) {
            Map parametros = new HashMap();
//        parametros.put(";where", "o.compromiso.contrato is null "
            parametros.put(";where", " "
                    + " o.compromiso.nomina is null and o.compromiso.devengado=0 "
                    + " and o.compromiso.impresion is not null "
                    + " and o.compromiso.proveedor is null and o.compromiso.beneficiario is not null "
                    + " and o.saldo!=0");
            try {
                List<Detallecompromiso> listaDetalle = ejbDetalleCompromiso.encontarParametros(parametros);
                for (Detallecompromiso d : listaDetalle) {
                    // ver si el compromiso ya fue utilizado en lote de pago antes de mandar a esto
                    estEnCompromiso(listadoCompromisos, d.getCompromiso());
//                }
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

            Map parametros = new HashMap();
//        parametros.put(";where", "o.compromiso.contrato is null "
            parametros.put(";where", " "
                    + " o.compromiso.nomina is null "
                    + " and o.compromiso.impresion is not null and o.compromiso.proveedor=:proveedor"
                    + " and o.saldo!=0");
            parametros.put("proveedor", proveedorBean.getProveedor());
            try {
                List<Detallecompromiso> listaDetalle = ejbDetalleCompromiso.encontarParametros(parametros);
                for (Detallecompromiso d : listaDetalle) {
                    // ver si el compromiso ya fue utilizado en lote de pago antes de mandar a esto
                    estEnCompromiso(listadoCompromisos, d.getCompromiso());
//                }
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Combos.getSelectItems(listadoCompromisos, true);
    }

    private void estaPartida(String partida) {
        if (partidas == null) {
            partidas = new LinkedList<>();
            partidas.add(partida);
            return;
        }
        for (String p : partidas) {
            if (p.equalsIgnoreCase(partida)) {
                return;
            }
        }
        partidas.add(partida);
    }

    private void verEndetalle(Detallecompromiso d, int tamano) {
        try {
            for (Detallescompras d1 : listaDetalleCompras) {
                if (d1.getDetallecompromiso().equals(d)) {
                    return;
                }
            }
            String cuentaPresupuesto = d.getAsignacion().getClasificador().getCodigo();
            String presupuesto = cuentaPresupuesto.substring(0, tamano);
            estaPartida(presupuesto);
            Map parametros = new HashMap();
            parametros.put(";where", "o.presupuesto=:presupuesto");
            parametros.put("presupuesto", cuentaPresupuesto);
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                Detallescompras dc = new Detallescompras();
                dc.setCuenta(c);
                dc.setDetallecompromiso(d);
                dc.setValor(new BigDecimal(BigInteger.ZERO));
                dc.setValorAnterior(0);
                listaDetalleCompras.add(dc);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarDetalleCompras() {
        if (cabeceraDoc == null) {
            return;
        }
        if (cabeceraDoc.getCompromiso() == null) {
            return;
        }
        Formatos f = ejbDocElec.traerFormato();
        if (f == null) {
            MensajesErrores.advertencia("Mal configurado formato");
            return;
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();
        Map parametros = new HashMap();
        parametros.put(";where", " "
                + " o.compromiso=:compromiso"
                + " and o.saldo!=0");
        parametros.put("compromiso", compromiso);
        try {
            listaDetalleCompras = new LinkedList<>();
            List<Detallecompromiso> lista = ejbDetalleCompromiso.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.cabeceradoc=:cabeceradoc "
                    + " ");
            parametros.put("cabeceradoc", cabeceraDoc);
            List<Detallescompras> lsitd = ejbDetallesCompras.encontarParametros(parametros);
            for (Detallescompras dt : lsitd) {
                dt.setValorAnterior(dt.getValor().doubleValue());
                listaDetalleCompras.add(dt);
            }
            for (Detallecompromiso d : lista) {
                verEndetalle(d, tamano);
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return;
    }

    public void cargarDetalleCompras1() {
        if (cabeceraDoc == null) {
            return;
        }
        if (cabeceraDoc.getCompromiso() == null) {
            return;
        }
        Formatos f = ejbDocElec.traerFormato();
        if (f == null) {
            MensajesErrores.advertencia("Mal configurado formato");
            return;
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();
        Map parametros = new HashMap();
        parametros.put(";where", " "
                + " o.compromiso=:compromiso"
                + " and o.saldo!=0");
        parametros.put("compromiso", compromiso);
        try {
            listaDetalleCompras = new LinkedList<>();
            List<Detallecompromiso> lista = ejbDetalleCompromiso.encontarParametros(parametros);
            if (lista.isEmpty()) {
                parametros = new HashMap();
                parametros.put(";where", "o.cabeceradoc=:cabeceradoc "
                        + " ");
                parametros.put("cabeceradoc", cabeceraDoc);
                List<Detallescompras> lsitd = ejbDetallesCompras.encontarParametros(parametros);
                for (Detallescompras dt : lsitd) {
                    dt.setValorAnterior(dt.getValor().doubleValue());
                    listaDetalleCompras.add(dt);
                }
            } else {
                if (obligacion.getId() == null) {
                    for (Detallecompromiso d : lista) {
                        // ver las cuentas por el detalle

                        String cuentaPresupuesto = d.getAsignacion().getClasificador().getCodigo();
                        String presupuesto = cuentaPresupuesto.substring(0, tamano);
                        estaPartida(presupuesto);
                        parametros = new HashMap();
                        parametros.put(";where", "o.presupuesto=:presupuesto");
                        parametros.put("presupuesto", cuentaPresupuesto);
                        List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
                        for (Cuentas c : cl) {
                            Detallescompras dc = new Detallescompras();
                            dc.setCuenta(c);
                            dc.setDetallecompromiso(d);
                            dc.setValor(new BigDecimal(BigInteger.ZERO));
                            dc.setValorAnterior(0);
                            listaDetalleCompras.add(dc);
                        }
                    }
                } else {
                    // los valores pendientes son el problema

                    for (Detallecompromiso d : lista) {
                        // ver las cuentas por el detalle
                        String cuentaPresupuesto = d.getAsignacion().getClasificador().getCodigo();
                        parametros = new HashMap();
                        parametros.put(";where", "o.presupuesto=:presupuesto");
                        parametros.put("presupuesto", cuentaPresupuesto);
                        List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
                        for (Cuentas c : cl) {
                            // ver si ya existe
                            parametros = new HashMap();
                            parametros.put(";where", "o.cuenta=:cuenta and o.cabeceradoc=:cabeceradoc "
                                    + " and o.detallecompromiso=:detalle");
                            parametros.put("cuenta", c);
                            parametros.put("detalle", d);
                            parametros.put("cabeceradoc", cabeceraDoc);
                            List<Detallescompras> lsitd = ejbDetallesCompras.encontarParametros(parametros);
                            if (lsitd.isEmpty()) {
                                Detallescompras dc = new Detallescompras();
                                dc.setCuenta(c);
                                dc.setDetallecompromiso(d);
                                dc.setValor(new BigDecimal(BigInteger.ZERO));
                                dc.setValorAnterior(0);
                                listaDetalleCompras.add(dc);
                            } else {
                                for (Detallescompras dt : lsitd) {
                                    dt.setValorAnterior(dt.getValor().doubleValue());
                                    listaDetalleCompras.add(dt);
                                }
                            }
                        }
                    } // fin del  for
                    // ver los valores proble

                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return;
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
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Combos.getSelectItems(listadoCompromisos, true);
    }

    public String modificar(Cabdocelect cabeceraDoc) {
        this.setCabeceraDoc(cabeceraDoc);
        setCompromiso(cabeceraDoc.getCompromiso());
        Map parametros = new HashMap();
        listadoFacturas = new LinkedList<>();
        listadoObligaciones = new LinkedList<>();
        cargarDetalleCompras();
        String ruc = "";
        if (cabeceraDoc.getCompromiso().getProveedor() != null) {
            proveedorBean.setProveedor(cabeceraDoc.getCompromiso().getProveedor());
            proveedorBean.setEmpresa(cabeceraDoc.getCompromiso().getProveedor().getEmpresa());
            ruc = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc();

        } else {
            if (cabeceraDoc.getCompromiso().getBeneficiario() != null) {
                ruc = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getPin();
            }
        }

        verFacturas = false;
        // traer los documentos

        parametros.put(";where", "o.cabeccera is null and o.ruc=:ruc");
        parametros.put("ruc", ruc);
        try {
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioObligacion.editar();
        formularioDetalleDetalle.insertar();
        formulario.editar();
        return null;
    }

    public String verContabilizar() {
        Map parametros = new HashMap();
        // traer los documentos
//        cargarDetalleCompras();
        parametros.put(";where", "o.cabeccera=:cabeceraDoc");
        parametros.put("cabeceraDoc", cabeceraDoc);
        try {
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            if (listadoFacturas.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            String error = ejbDocElec.
                    validaContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            if (error != null) {
                MensajesErrores.advertencia(error);
                return null;
            }

//            List<Renglones> renglones = ejbDocElec.preContabilizar(listadoFacturas,
//                    1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            double credito = 0;
            double creditoAnticipos = 0;
            double creditoReclasificacion = 0;
            double creditoMultas = 0;
            double creditoDescuentos = 0;
            double debito = 0;
            double debitoAnticipos = 0;
            double debitoMultas = 0;
            double debitoReclasificacion = 0;
            double debitoDescuentos = 0;
            renglonesAuxiliar = new LinkedList<>();
            renglonesAnticipos = new LinkedList<>();
            renglonesMultas = new LinkedList<>();
            renglonesReclasificacion = new LinkedList<>();
            renglonesDescuento = new LinkedList<>();
            //renglones
//            saco del Map
            Map retorno
                    = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            renglones = null;
            renglonesAnticiposLocal = new LinkedList<>();
            renglonesReclasificacionLocal = new LinkedList<>();
            renglonesMultasLocal = new LinkedList<>();
            renglonesDescuentoLocal = new LinkedList<>();
            Iterator it = retorno.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("renglones")) {
                    renglones = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasInversiones")) {
                    renglonesReclasificacionLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasAnticipos")) {
                    renglonesAnticiposLocal = (List<Renglones>) e.getValue();

                } else if (clave.contains("rasMultas")) {
                    renglonesMultasLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasDescuento")) {
                    renglonesDescuentoLocal = (List<Renglones>) e.getValue();
                }

            }
//            List<Renglones> renglonesAnticiposLocal = ejbDocElec.getRasAnticipo();
//            List<Renglones> renglonesReclasificacionLocal = ejbDocElec.getRasInversiones();
//            List<Renglones> renglonesMultasLocal = ejbDocElec.getRasMultas();
//            List<Renglones> renglonesDescuentoLocal = ejbDocElec.getRasDescuento();
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
            for (Renglones r : renglonesAnticiposLocal) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                a.setReferencia(r.getReferencia());
                a.setAuxiliar(r.getAuxiliar());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    debitoAnticipos += valor;
                    a.setIngresos(new BigDecimal(valor));
                } else {
                    creditoAnticipos += Math.abs(valor);
                    a.setEgresos(new BigDecimal(Math.abs(valor)));
                }
                this.renglonesAnticipos.add(a);
            }
            for (Renglones r : renglonesReclasificacionLocal) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                a.setReferencia(r.getReferencia());
                a.setAuxiliar(r.getAuxiliar());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    debitoReclasificacion += valor;
                    a.setIngresos(new BigDecimal(valor));
                } else {
                    creditoReclasificacion += Math.abs(valor);
                    a.setEgresos(new BigDecimal(Math.abs(valor)));
                }
                this.renglonesReclasificacion.add(a);
            }
            if (renglonesMultasLocal != null) {
                for (Renglones r : renglonesMultasLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoMultas += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoMultas += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesMultas.add(a);
                }
            }
            if (renglonesDescuentoLocal != null) {
                for (Renglones r : renglonesDescuentoLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoDescuentos += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoDescuentos += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesDescuento.add(a);
                }
            }
            AuxiliarCarga a = new AuxiliarCarga();
            a.setCuenta("TOTALES");
            a.setIngresos(new BigDecimal(Math.abs(debito)));
            a.setEgresos(new BigDecimal(Math.abs(credito)));
            renglonesAuxiliar.add(a);
            if (!renglonesAnticipos.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoAnticipos)));
                a.setEgresos(new BigDecimal(Math.abs(creditoAnticipos)));
                renglonesAnticipos.add(a);
            }
            if (!renglonesMultas.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoMultas)));
                a.setEgresos(new BigDecimal(Math.abs(creditoMultas)));
                renglonesMultas.add(a);
            }
            if (!renglonesReclasificacion.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoReclasificacion)));
                a.setEgresos(new BigDecimal(Math.abs(creditoReclasificacion)));
                renglonesReclasificacion.add(a);
            }
            if (!renglonesDescuento.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoDescuentos)));
                a.setEgresos(new BigDecimal(Math.abs(creditoDescuentos)));
                renglonesDescuento.add(a);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioObligacion.editar();
        formularioDetalleDetalle.consultar();
        formulario.editar();
        return null;
    }

    public String contabilizarPregunta(Cabdocelect cabeceraDoc) {
        contRetenciones = 1;
        this.setCabeceraDoc(cabeceraDoc);
        setCompromiso(cabeceraDoc.getCompromiso());
        Map parametros = new HashMap();
        // traer los documentos
        cargarDetalleCompras();
        parametros.put(";where", "o.cabeccera=:cabeceraDoc");
        parametros.put("cabeceraDoc", cabeceraDoc);
        try {
            double valorPresupuesto = 0;
            for (Detallescompras d : listaDetalleCompras) {
                valorPresupuesto += d.getValor().doubleValue();
            }
            cabeceraDoc.setValor(new BigDecimal(valorPresupuesto));
            double cuadre = Math.round((cabeceraDoc.getValor().doubleValue() - valorPresupuesto) * 100);
            if (cuadre != 0) {
                MensajesErrores.advertencia("No cuadra el valor utilizado del presupuesto con el valor del lote a contabilizar");
                return null;
            }
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            if (listadoFacturas.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            String error = ejbDocElec.
                    validaContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            if (error != null) {
                MensajesErrores.advertencia(error);
                return null;
            }
            double valorDocumentos = 0;
            for (Documentoselectronicos d : listadoFacturas) {
                valorDocumentos += d.getValortotal().doubleValue();
                if (d.getValortotal() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getTipodocumento() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getTipodocumento().getCodigo().equals("OTR")) {

                } else {
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
                    if (d.getObligacion().getApagar().doubleValue() < 0) {
                        MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                        return null;
                    }
                }

            }
            renglones = new LinkedList<>();
//            List<Renglones> renglones = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            double creditoReclasificacion = 0;
            double debitoReclasificacion = 0;
            double credito = 0;
            double creditoAnticipos = 0;
            double creditoMultas = 0;
            double creditoDescuentos = 0;
            double creditoExterior = 0;
            double debito = 0;
            double debitoAnticipos = 0;
            double debitoMultas = 0;
            double debitoDescuentos = 0;
            double debitoExterior = 0;
            renglonesAuxiliar = new LinkedList<>();
            renglonesReclasificacion = new LinkedList<>();
            renglonesAnticipos = new LinkedList<>();
            renglonesMultas = new LinkedList<>();
            renglonesDescuento = new LinkedList<>();
            renglonesExterior = new LinkedList<>();
            //renglones
//            saco del Map
            Map retorno
                    = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            renglones = null;
            renglonesAnticiposLocal = null;
            renglonesReclasificacionLocal = null;
            renglonesMultasLocal = null;
            renglonesDescuentoLocal = null;
            renglonesExteriorLocal = null;
            Iterator it = retorno.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("renglones")) {
                    renglones = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasInversiones")) {
                    renglonesReclasificacionLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasAnticipos")) {
                    renglonesAnticiposLocal = (List<Renglones>) e.getValue();

                } else if (clave.contains("rasMultas")) {
                    renglonesMultasLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasDescuento")) {
                    renglonesDescuentoLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasExterior")) {
                    renglonesExteriorLocal = (List<Renglones>) e.getValue();
                }

            }
//            List<Renglones> renglonesReclasificacionLocal = ejbDocElec.getRasInversiones();
//            List<Renglones> renglonesAnticiposLocal = ejbDocElec.getRasAnticipo();
//            List<Renglones> renglonesMultasLocal = ejbDocElec.getRasMultas();
//            List<Renglones> renglonesDescuentosLocal = ejbDocElec.getRasDescuento();
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
            for (Renglones r : renglonesAnticiposLocal) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                a.setReferencia(r.getReferencia());
                a.setAuxiliar(r.getAuxiliar());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    debitoAnticipos += valor;
                    a.setIngresos(new BigDecimal(valor));
                } else {
                    creditoAnticipos += Math.abs(valor);
                    a.setEgresos(new BigDecimal(Math.abs(valor)));
                }
                this.renglonesAnticipos.add(a);
            }
            for (Renglones r : renglonesReclasificacionLocal) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                a.setReferencia(r.getReferencia());
                a.setAuxiliar(r.getAuxiliar());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    debitoReclasificacion += valor;
                    a.setIngresos(new BigDecimal(valor));
                } else {
                    creditoReclasificacion += Math.abs(valor);
                    a.setEgresos(new BigDecimal(Math.abs(valor)));
                }
                this.renglonesReclasificacion.add(a);
            }
            if (renglonesMultasLocal != null) {
                for (Renglones r : renglonesMultasLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoMultas += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoMultas += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesMultas.add(a);
                }
            }
            if (renglonesDescuentoLocal != null) {
                for (Renglones r : renglonesDescuentoLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoDescuentos += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoDescuentos += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesDescuento.add(a);
                }
            }

            if (renglonesExteriorLocal != null) {
                for (Renglones r : renglonesExteriorLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoExterior += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoExterior += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    renglonesExterior.add(a);
                }
            }
            AuxiliarCarga a = new AuxiliarCarga();
            a.setCuenta("TOTALES");
            a.setIngresos(new BigDecimal(Math.abs(debito)));
            a.setEgresos(new BigDecimal(Math.abs(credito)));
            renglonesAuxiliar.add(a);
            if (!renglonesAnticipos.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoAnticipos)));
                a.setEgresos(new BigDecimal(Math.abs(creditoAnticipos)));
                renglonesAnticipos.add(a);
            }
            if (!renglonesMultas.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoMultas)));
                a.setEgresos(new BigDecimal(Math.abs(creditoMultas)));
                renglonesMultas.add(a);
            }
            if (!renglonesReclasificacion.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoReclasificacion)));
                a.setEgresos(new BigDecimal(Math.abs(creditoReclasificacion)));
                renglonesReclasificacion.add(a);
            }
            if (!renglonesDescuento.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoDescuentos)));
                a.setEgresos(new BigDecimal(Math.abs(creditoDescuentos)));
                renglonesDescuento.add(a);
            }
            if (!renglonesExterior.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoExterior)));
                a.setEgresos(new BigDecimal(Math.abs(creditoExterior)));
                renglonesExterior.add(a);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (renglonesReclasificacion.isEmpty()) {
            formularioContabilizar.insertar();
        } else {
            formularioReclasificacion.insertar();
        }
        return null;
    }

    public String contabilizar() {
        try {
            if (listadoFacturas.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            String error = ejbDocElec.
                    validaContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            if (error != null) {
                MensajesErrores.advertencia(error);
                return null;
            }
            double valorDocumentos = 0;
            for (Documentoselectronicos d : listadoFacturas) {
                valorDocumentos += d.getValortotal().doubleValue();
                if (d.getValortotal() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion() == null) {
                    MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                    return null;
                }
                if (d.getObligacion().getTipodocumento().getCodigo().equals("OTR")) {

                } else {
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
                    if (d.getObligacion().getApagar().doubleValue() < 0) {
                        MensajesErrores.advertencia("No se puede contabilizar existen obligaciones no terminadas");
                        return null;
                    }
                }
            }

//            renglones = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            double credito = 0;
            double creditoAnticipos = 0;
            double creditoMultas = 0;
            double creditoReclasificacion = 0;
            double creditoDescuentos = 0;
            double creditoExterior = 0;
            double debito = 0;
            double debitoAnticipos = 0;
            double debitoReclasificacion = 0;
            double debitoMultas = 0;
            double debitoDescuentos = 0;
            double debitoExterior = 0;
            renglonesAuxiliar = new LinkedList<>();
            renglonesAnticipos = new LinkedList<>();
            renglonesMultas = new LinkedList<>();
            renglonesReclasificacion = new LinkedList<>();
            renglonesDescuento = new LinkedList<>();
            renglonesExteriorLocal = null;

            //renglones
//            saco del Map
            Map retorno
                    = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);
            renglones = new LinkedList<>();
            renglonesAnticiposLocal = new LinkedList<>();
            renglonesReclasificacionLocal = new LinkedList<>();
            renglonesMultasLocal = new LinkedList<>();
            renglonesDescuentoLocal = new LinkedList<>();
            renglonesExterior = new LinkedList<>();
            Iterator it = retorno.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("renglones")) {
                    renglones = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasInversiones")) {
                    renglonesReclasificacionLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasAnticipos")) {
                    renglonesAnticiposLocal = (List<Renglones>) e.getValue();

                } else if (clave.contains("rasMultas")) {
                    renglonesMultasLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasDescuento")) {
                    renglonesDescuentoLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasExterior")) {
                    renglonesExteriorLocal = (List<Renglones>) e.getValue();
                }

            }
            //
//            renglonesAnticiposLocal = ejbDocElec.getRasAnticipo();
//            renglonesReclasificacionLocal = ejbDocElec.getRasInversiones();
//            renglonesMultasLocal = ejbDocElec.getRasMultas();
//            renglonesDescuentoLocal = ejbDocElec.getRasDescuento();
            // fin 
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
            for (Renglones r : renglonesAnticiposLocal) {
                AuxiliarCarga a = new AuxiliarCarga();
                a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                a.setReferencia(r.getReferencia());
                a.setAuxiliar(r.getAuxiliar());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    debitoAnticipos += valor;
                    a.setIngresos(new BigDecimal(valor));
                } else {
                    creditoAnticipos += Math.abs(valor);
                    a.setEgresos(new BigDecimal(Math.abs(valor)));
                }
                this.renglonesAnticipos.add(a);
            }
            if (contRetenciones == 1) {
                for (Renglones r : renglonesReclasificacionLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoReclasificacion += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoReclasificacion += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesReclasificacion.add(a);
                }
            }
            if (renglonesMultasLocal != null) {
                for (Renglones r : renglonesMultasLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoMultas += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoMultas += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesMultas.add(a);
                }
            }
            if (renglonesDescuentoLocal != null) {
                for (Renglones r : renglonesDescuentoLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoDescuentos += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoDescuentos += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    this.renglonesDescuento.add(a);
                }
            }
            if (renglonesExteriorLocal != null) {
                for (Renglones r : renglonesExteriorLocal) {
                    AuxiliarCarga a = new AuxiliarCarga();
                    a.setCuenta(r.getCuenta());
//                a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                    a.setReferencia(r.getReferencia());
                    a.setAuxiliar(r.getAuxiliar());
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        debitoExterior += valor;
                        a.setIngresos(new BigDecimal(valor));
                    } else {
                        creditoExterior += Math.abs(valor);
                        a.setEgresos(new BigDecimal(Math.abs(valor)));
                    }
                    renglonesExterior.add(a);
                }
            }
            AuxiliarCarga a = new AuxiliarCarga();
            a.setCuenta("TOTALES");
            a.setIngresos(new BigDecimal(Math.abs(debito)));
            a.setEgresos(new BigDecimal(Math.abs(credito)));
            renglonesAuxiliar.add(a);
            if (!renglonesAnticipos.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoAnticipos)));
                a.setEgresos(new BigDecimal(Math.abs(creditoAnticipos)));
                renglonesAnticipos.add(a);
            }
            if (!renglonesReclasificacion.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoReclasificacion)));
                a.setEgresos(new BigDecimal(Math.abs(creditoReclasificacion)));
                renglonesReclasificacion.add(a);
            }
            if (!renglonesMultas.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoMultas)));
                a.setEgresos(new BigDecimal(Math.abs(creditoMultas)));
                renglonesMultas.add(a);
            }
            if (!renglonesDescuento.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoDescuentos)));
                a.setEgresos(new BigDecimal(Math.abs(creditoDescuentos)));
                renglonesDescuento.add(a);
            }
            if (!renglonesExterior.isEmpty()) {
                a = new AuxiliarCarga();
                a.setCuenta("TOTALES");
                a.setIngresos(new BigDecimal(Math.abs(debitoExterior)));
                a.setEgresos(new BigDecimal(Math.abs(creditoExterior)));
                renglonesExterior.add(a);
            }
            ///////////////////
            String ruc = "";
            String beneficiario = "";
            String direccion = "";
            if (cabeceraDoc.getCompromiso().getProveedor() == null) {
                if (cabeceraDoc.getCompromiso().getBeneficiario() != null) {
                    ruc = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getPin();
                    beneficiario = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().toString();
                    direccion = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getDireccion().toString();
                }

            } else {
                ruc = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc();
                beneficiario = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().toString();
                direccion = cabeceraDoc.getCompromiso().getProveedor().getDireccion();
            }
            //////////////////////
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("BORRADOR DE COMPROBANTES DE RETENCION", null, seguridadbean.getLogueado().getUserid());
            for (Documentoselectronicos docelec : listadoFacturas) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Sr. (es) :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, beneficiario));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "RUC :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ruc));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Dirección"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, direccion));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(cabeceraDoc.getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Documento"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, docelec.getObligacion().getTipodocumento().getCodigo()
                        + "-" + docelec.getObligacion().getEstablecimiento()
                        + "-" + docelec.getObligacion().getPuntoemision()
                        + "-" + docelec.getObligacion().getDocumento()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "email"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cabeceraDoc.getCompromiso().getProveedor() == null ? "" : cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getEmail()));

                pdf.agregarTabla(null, columnas, 4, 100, null);
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Base Imponible"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Base Imponible 0"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "IVA"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Ret IR"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Ret IR"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Ret IVA"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Ret IVA"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "A Pagar"));
                obligacion = docelec.getObligacion();
                cargarRetenciones();

                columnas = new LinkedList<>();
                double totalValor = 0;
                double totalValorIva = 0;
                double totalBase = 0;
                double totalBase0 = 0;
                double totaliva = 0;
                for (Retencionescompras r : listaRetencionesCompras) {
                    if (r.getRetencion() != null) {
                        double valor = r.getValor().doubleValue();
                        double valorIva = r.getValoriva().doubleValue();
                        double valorBase = r.getValor().doubleValue();
                        double valorBase0 = r.getValoriva().doubleValue();
                        double valorBaseIva = r.getIva().doubleValue();
                        double valorTotal = valorBase + valorBase0 + valorBaseIva;
                        double valorLinea = valorTotal - (valor + valorIva);
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorBase));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorBase0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorBaseIva));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencion().getEtiqueta().trim()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getRetencionimpuesto() == null ? "" : r.getRetencionimpuesto().getEtiqueta().trim()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorIva));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorLinea));
                        totalValor += valor;
                        totalValorIva += valorIva;
                        totalBase += valorBase;
                        totalBase0 += valorBase0;
                        totaliva += valorBaseIva;
                    }
                }
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBase));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBase));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totaliva));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBase + totalBase0 + totaliva));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTALES"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalValor));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalValorIva));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, (totalBase + totalBase0 + totaliva) - (totalValor - totalValorIva)));
                pdf.agregarTablaReporte(titulos, columnas, 9, 100, "");
                pdf.agregaParrafo("\n\n");
            }
            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioContabilizar.insertar();
        formularioReclasificacion.cancelar();
//        formularioObligacion.editar();
//        formulario.editar();

        return null;
    }
//    *************************************************************************

    public String reContabilizar(Cabdocelect cabeceraDoc) {
        this.cabeceraDoc = cabeceraDoc;
        try {
            //renglones
//            saco del Map
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabeccera=:cabeceraDoc");
            parametros.put("cabeceraDoc", cabeceraDoc);

            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            if (listadoFacturas.isEmpty()) {
                MensajesErrores.advertencia("No existe nada para contabilizar");
                return null;
            }
            Map retorno
                    = ejbDocElec.preContabilizar(listadoFacturas, 1, perfil.getMenu().getIdpadre().getModulo(), cabeceraDoc);

            List<Renglones> renglonesBorrar = traerRenglones();
            Cabeceras cab = null;
            for (Renglones r : renglonesBorrar) {
                cab = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            List<Renglones> renglonesAnticiposBorrar = traerRenglonesAnticipo();
            Cabeceras cabAnticipos = null;
            for (Renglones r : renglonesAnticiposBorrar) {
                cabAnticipos = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            List<Renglones> renglonesReclasificacionBorrar = traerRenglonesInv();
            Cabeceras cabReclaficicacion = null;
            for (Renglones r : renglonesReclasificacionBorrar) {
                cabReclaficicacion = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            List<Renglones> renglonesMultasBorrar = traerRenglonesMultas();
            Cabeceras cabMultas = null;
            for (Renglones r : renglonesMultasBorrar) {
                cabMultas = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            List<Renglones> renglonesDescuentoBorrar = traerRenglonesDescuento();
            Cabeceras cabDescuentos = null;
            for (Renglones r : renglonesDescuentoBorrar) {
                cabDescuentos = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            renglones = new LinkedList<>();
            renglonesAnticiposLocal = new LinkedList<>();
            renglonesReclasificacionLocal = new LinkedList<>();
            renglonesMultasLocal = new LinkedList<>();
            renglonesDescuentoLocal = new LinkedList<>();
            Iterator it = retorno.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                String clave = (String) e.getKey();
                if (clave.contains("renglones")) {
                    renglones = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasInversiones")) {
                    renglonesReclasificacionLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasAnticipos")) {
                    renglonesAnticiposLocal = (List<Renglones>) e.getValue();

                } else if (clave.contains("rasMultas")) {
                    renglonesMultasLocal = (List<Renglones>) e.getValue();
                } else if (clave.contains("rasDescuento")) {
                    renglonesDescuentoLocal = (List<Renglones>) e.getValue();
                }

            }
            if (cab != null) {
                for (Renglones r : renglones) {
                    r.setCabecera(cab);
                    r.setFecha(cab.getFecha());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (cabAnticipos != null) {
                for (Renglones r : renglonesAnticiposLocal) {
                    r.setCabecera(cabAnticipos);
                    r.setFecha(cabAnticipos.getFecha());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (cabDescuentos != null) {
                for (Renglones r : renglonesDescuentoLocal) {
                    r.setCabecera(cabDescuentos);
                    r.setFecha(cabDescuentos.getFecha());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (cabMultas != null) {
                for (Renglones r : renglonesMultasLocal) {
                    r.setCabecera(cabMultas);
                    r.setFecha(cabMultas.getFecha());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (cabReclaficicacion != null) {
                for (Renglones r : renglonesReclasificacionLocal) {
                    r.setCabecera(cabReclaficicacion);
                    r.setFecha(cabReclaficicacion.getFecha());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
//  
//        

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Ejecutado correctamente");
        return null;
    }
    //    *******************************************************************************

    public String grabarContabilizar() {
        try {

            Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
            ejbDocElec.contabilizarRenglones(cabeceraDoc, seguridadbean.getLogueado().getUserid(),
                    1, perfil.getMenu().getIdpadre().getModulo(),
                    contRetenciones, renglones, renglonesAnticiposLocal,
                    renglonesReclasificacionLocal, renglonesMultasLocal, renglonesDescuentoLocal, renglonesExteriorLocal);
            for (Documentoselectronicos d : listadoFacturas) {
                Obligaciones o = d.getObligacion();
                o.setFechacontable(o.getFechaemision());
                o.setEstado(2);
                if (o.getPuntor() != null) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.punto.codigo=:punto and o.documento=:documento");
                    parametros.put("punto", o.getPuntor());
                    parametros.put("documento", ret);
                    documento = null;
                    List<Documentos> documentos = ejbDocumnetos.encontarParametros(parametros);
                    Documentos doc = null;
                    for (Documentos dx : documentos) {
                        doc = dx;
                    }
                    if (doc != null) {
                        Integer numret = doc.getNumeroactual();
                        numret++;
                        o.setNumeror(numret);
                        doc.setNumeroactual(numret);
                        ejbDocumnetos.edit(doc, seguridadbean.getLogueado().getUserid());
                    } else {
                        o.setNumeror(o.getId());
                    }

                    parametros = new HashMap();
                    parametros.put(";where", "o.exterior=:exterior and o.cerrado=false");
                    parametros.put("exterior", o.getPagoExterior());
                    List<Fondos> listaGrabar = ejbFondos.encontarParametros(parametros);
                    if (!listaGrabar.isEmpty()) {
                        for (Fondos f : listaGrabar) {
                            f.setCerrado(Boolean.TRUE);
                            ejbFondos.edit(f, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
                ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
            }
            cabeceraDoc.setContabilizado(true);
            ejbCabdocelect.edit(cabeceraDoc, seguridadbean.getLogueado().getUserid());

        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir(cabeceraDoc);
        buscar();
        formularioContabilizar.cancelar();
        return null;
    }

    public String nuevo() {
//        if (proveedorBean.getProveedor() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor primero");
//            return null;
//        }
        cabeceraDoc = new Cabdocelect();
        cabeceraDoc.setFecha(new Date());
        cabeceraDoc.setValor(BigDecimal.ZERO);
        listadoFacturas = new LinkedList<>();
        listadoObligaciones = new LinkedList<>();
        reembolsos = new LinkedList<>();

        verFacturas = false;
        cargarDetalleCompras();
        if (proveedorBean.getProveedor() != null) {
            Map parametros = new HashMap();
            // traer los documentos
            parametros.put(";where", "o.ruc=:ruc and o.tipo=0 and o.cabeccera is null");
            parametros.put("ruc", proveedorBean.getProveedor().getEmpresa().getRuc());
            try {

                listadoFacturas = ejbDocElec.encontarParametros(parametros);

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        formularioObligacion.insertar();
        formularioDetalleDetalle.insertar();
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

            List<Renglones> renglonesLocal = traerRenglonesGasto();
            List<Renglones> renglonesRecla = traerRenglonesInv();
            List<Renglones> renglonesAnticpo = traerRenglonesAnticipo();
            List<Renglones> renglonesMultasLocal = traerRenglonesMultas();
            List<Renglones> renglonesDescuentosLocal = traerRenglonesDescuento();
            List<Renglones> renglonesExteriorLoca = traerRenglonesExterior();

            //Anticipos y multas ya contabilizadas
            List<Renglones> listaRenglonesAnticipoAnterior = new LinkedList<>();
            List<Renglones> listaRenglonesMultaAnterior = new LinkedList<>();
            formularioImpresion.insertar();
//        Hacer el reporte
            String empresa = "";
            String email = "";
            if (cabeceraDoc.getCompromiso().getProveedor() == null) {
                if (cabeceraDoc.getCompromiso().getBeneficiario() != null) {
                    empresa = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().toString();
                    email = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getEmail();
                }

            } else {
                empresa = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().toString();
                email = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getEmail();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("ORDEN DE PAGO NO : " + cabeceraDoc.getId(), null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("Beneficiario : " + empresa);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(cabeceraDoc.getFecha())));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, empresa)
//            );
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "email"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, email));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            // un parafo
            pdf.agregaParrafo("Concepto : " + cabeceraDoc.getObservaciones() + "\n\n");
            DecimalFormat df = new DecimalFormat("000000");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo de Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No."));
//            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Retención"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Subtotal 0"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Subtotal"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Iva"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cód IR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Retención IR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cod IVA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Retención IVA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Anticipo/\nDescuento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Multas"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor a pagar"));

            double total = 0;
            columnas = new LinkedList<>();
            List<Rascompras> detalleImpresion = new LinkedList<>();
            double valorTotal = 0;
            double valorTotalIva = 0;
            double valorTotalFuente = 0;
            double valorTotalanticipos = 0;
            double valorTotalmultas = 0;
            double valorTotalbases = 0;
//            double valorTotalbases0 = 0;
            double valorTotalRiva = 0;
            reembolsos = new LinkedList<>();
            for (Documentoselectronicos d : listadoFacturas) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getTipodocumento().toString()));
//                if (d.getObligacion().getTipodocumento().getCodigo().equals("OTR")) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,
                        d.getObligacion().getEstablecimiento() == null ? "" : d.getObligacion().getEstablecimiento()
                        + "-" + d.getObligacion().getPuntoemision() == null ? "" : d.getObligacion().getPuntoemision() + "-"
                        + df.format(d.getObligacion().getDocumento() == null ? 0 : d.getObligacion().getDocumento())));
//                } else {
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                }
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getConcepto()));
                if (d.getObligacion().getPuntor() == null) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                } else {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getEstablecimientor() + "-"
                            + d.getObligacion().getPuntor() + "-"
                            + df.format(d.getObligacion().getNumeror() == null ? 0 : d.getObligacion().getNumeror())));
                }

                double valor = d.getValortotal().doubleValue();
                double base = d.getBaseimponible() == null ? 0 : d.getBaseimponible().doubleValue();
                double base0 = d.getBaseimponible0() == null ? 0 : d.getBaseimponible0().doubleValue();
                double ivaLocal = d.getIva() == null ? 0 : d.getIva().doubleValue();
                total += valor;
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Reembolsos> listaReembolsos = ejbReembolsos.encontarParametros(parametros);
                for (Reembolsos r : listaReembolsos) {
                    reembolsos.add(r);
                }
//                List<Rascompras> ras = ejbDetalles.encontarParametros(parametros);
                List<Rascompras> ras = new LinkedList<>();
                List<Pagosvencimientos> listaPagos = ejbPagos.encontarParametros(parametros);
                double retIva = 0;
                double retFuente = 0;
                String codFuente = "";
                String codIva = "";
                double valorAnticipo = 0;
                double valorMulta = 0;
                // Anticpos
                for (Pagosvencimientos p : listaPagos) {
                    if (p.getValoranticipo() != null) {
                        Anticipos a = p.getAnticipo();
                        valorAnticipo += p.getValoranticipo().doubleValue();
                        if (p.getAnticipo() != null) {
                            List<Renglones> listaAnticipoAnterior = traerRenglonesAnticipoAnterior(p);
                            if (!listaAnticipoAnterior.isEmpty()) {
                                for (Renglones ra : listaAnticipoAnterior) {
                                    listaRenglonesAnticipoAnterior.add(ra);
                                }
                            }
                        }
                    }
                    if (p.getValormulta() != null) {
                        valorMulta += p.getValormulta().doubleValue();
                        if (p.getMulta() != null) {
                            List<Renglones> listaMultasAnterior = traerRenglonesMultaAnterior(p);
                            if (!listaMultasAnterior.isEmpty()) {
                                for (Renglones ra : listaMultasAnterior) {
                                    listaRenglonesMultaAnterior.add(ra);
                                }
                            }
                        }
                    }
                }
                //Es valor del descuento se suma al anticipo
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Descuentoscompras> listaDescuento = ejbDescuentos.encontarParametros(parametros);
                for (Descuentoscompras dc : listaDescuento) {
                    valorAnticipo += dc.getValor().doubleValue();
                }
                valorTotalanticipos += valorAnticipo;
                valorTotalmultas += valorMulta;
                obligacion = d.getObligacion();
                cargarRetenciones();
                for (Retencionescompras r : listaRetencionesCompras) {
                    // ver las retenciones traer de abajo
                    if (r.getRetencionimpuesto() != null) {
                        retIva += r.getValoriva().doubleValue();
                        if (!codIva.isEmpty()) {
                            codIva = ",";
                        }
                        codIva += r.getRetencionimpuesto().getEtiqueta();
                    }
                    if (r.getRetencion() != null) {
                        retFuente += r.getValor().doubleValue();
                        if (!codFuente.isEmpty()) {
                            codFuente = ";";
                        }
                        codFuente += r.getRetencion().getEtiqueta();
                    }

                }
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, base0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, base + base0));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ivaLocal));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, codFuente));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retFuente));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, codIva));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retIva));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorAnticipo));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorMulta));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        valor - (retIva + retFuente + valorAnticipo + valorMulta)));
                valorTotalFuente += retFuente;
//                valorTotalIva += retIva;
                valorTotalIva += ivaLocal;
                valorTotal += valor;
                valorTotalbases += (base + base0);
//                valorTotalbases0 += base0;
                valorTotalRiva += retIva;
            } // fin documentos elecctronicos

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalbases0));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalbases));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalIva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalFuente));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalRiva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalanticipos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotalmultas));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal
                    - (valorTotalFuente + valorTotalRiva + valorTotalanticipos + valorTotalmultas)));
            pdf.agregarTablaReporte(titulos, columnas, 13, 100, "DOCUMENTOS");
            pdf.agregaParrafo("\n\n");

            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
//            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;

            if (renglonesLocal == null) {
                renglonesLocal = new LinkedList<>();
            }
            Cabeceras c = new Cabeceras();
            for (Renglones r : renglonesLocal) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    sumaDebitos += valor * r.getSigno();
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                    sumaCreditos += valor * -1 * r.getSigno();
                }
                c = r.getCabecera();
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION GASTO N° " + c.getNumero());
            // reclasificación
            if (renglonesRecla != null) {
                if (!renglonesRecla.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesRecla) {
                        Cuentas ctaNueva = cuentasBean.traerCodigo(r.getCuenta());
                        String nomCuenta = "";
                        if (ctaNueva != null) {
                            nomCuenta = ctaNueva.getNombre();
                        } else {
                            int x = 0;
                        }
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, nomCuenta));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor * r.getSigno();
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                            sumaCreditos += valor * -1 * r.getSigno();
                        }
                        c = r.getCabecera();
                    }
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION RECLASIFICACION N° " + c.getNumero());
                }
            }
            // anticipos
            if (renglonesAnticpo != null) {
                if (!renglonesAnticpo.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesAnticpo) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor * r.getSigno();
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                            sumaCreditos += valor * -1 * r.getSigno();
                        }
                        c = r.getCabecera();
                    }
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION ANTICIPO N° " + c.getNumero());
                }
            }
            if (renglonesMultasLocal != null) {
                if (!renglonesMultasLocal.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesMultasLocal) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor * r.getSigno();
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                            sumaCreditos += valor * -1 * r.getSigno();
                        }
                        c = r.getCabecera();
                    }
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION MULTAS N° " + c.getNumero());
                }
            }
            if (renglonesDescuentosLocal != null) {
                if (!renglonesDescuentosLocal.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesDescuentosLocal) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor * r.getSigno();
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                            sumaCreditos += valor * -1 * r.getSigno();
                        }
                        c = r.getCabecera();
                    }
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION DESCUENTOS N° " + c.getNumero());
                }
            }
            // anticipos Anteriores
//            if (listaRenglonesAnticipoAnterior != null) {
//                if (!listaRenglonesAnticipoAnterior.isEmpty()) {
//                    columnas = new LinkedList<>();
//                    sumaDebitos = 0.0;
//                    sumaCreditos = 0.0;
//                    for (Renglones r : listaRenglonesAnticipoAnterior) {
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
////                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
//                        double valor = r.getValor().doubleValue();
//                        if (valor > 0) {
//                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
//                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
//                            sumaDebitos += valor;
//                        } else {
//                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
//                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
//                            sumaCreditos += valor * -1;
//                        }
//                        c = r.getCabecera();
//                    }
////                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
//                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
//                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "ANTICIPO N° " + c.getNumero());
//                }
//            }
            //multas anterior
            if (listaRenglonesMultaAnterior != null) {
                if (!listaRenglonesMultaAnterior.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listaRenglonesMultaAnterior) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor * r.getSigno();
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                            sumaCreditos += valor * -1 * r.getSigno();
                        }
                        c = r.getCabecera();
                    }
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "MULTAS N° " + c.getNumero());
                }
            }
            //Exterior
            if (renglonesExteriorLoca != null) {
                if (!renglonesExteriorLoca.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesExteriorLoca) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                        double valor = r.getValor().doubleValue();
                        if (valor > 0) {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * r.getSigno()));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            sumaDebitos += valor * r.getSigno();
                        } else {
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1 * r.getSigno()));
                            sumaCreditos += valor * -1 * r.getSigno();
                        }
                        c = r.getCabecera();
                    }
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "LIQUIDACIÓN FONDOS AL EXTERIOR N° " + c.getNumero());
                }
            }
            pdf.agregaParrafo("\n\n");
            sumaDebitos = 0;
            sumaCreditos = 0;
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

            columnas = new LinkedList<>();
            double totalCompromiso = 0;
            Detallecompromiso detalleCompromiso = null;
            cargarDetalleCompras();
            valorTotal = 0;
            for (Detallescompras d : listaDetalleCompras) {

                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6,
                        false, d.getDetallecompromiso().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        d.getValor().doubleValue()));
                totalCompromiso += d.getValor().doubleValue();

            }

            columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL "));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCompromiso));
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "AFECTACION PRESUPUESTARIA");
            // reembolsos

            if (reembolsos != null) {
                if (!reembolsos.isEmpty()) {
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "RUC"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Comp."));
                    titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Base Imponible"));
                    columnas = new LinkedList<>();
                    for (Reembolsos r : reembolsos) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6,
                                false, r.getRuc()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6,
                                false, r.getComprobante().toString()));
                        String doc = r.getEstablecimiento() + "-" + r.getPunto() + "-" + r.getNumero();
                        columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6,
                                false, doc));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                                r.getBaseimponible().doubleValue()));
                    }
                    pdf.agregarTablaReporte(titulos, columnas, 4, 100, "REEMBOLSOS");
                }
            }
            // reembolsos fin
            pdf.agregaParrafo("\n\n");
            Codigos textoCodigo = getCodigosBean().traerCodigo("PAGOS", "PAGOS");
            String texto = "";
            if (textoCodigo != null) {
                texto = textoCodigo.getParametros().replace("#compromiso#", cabeceraDoc.getCompromiso().getId().toString());
                if (!((cabeceraDoc.getCompromiso().getNumeroanterior() == null) || (cabeceraDoc.getCompromiso().getNumeroanterior().isEmpty()))) {
                    texto += " (NO Comp Ant: " + cabeceraDoc.getCompromiso().getNumeroanterior() + ")";
                }
            }
//             cargarReembolsos();
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
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrar(Cabdocelect cabeceraDoc) {
        try {
            String vale = ejbCabecera.validarCierre(cabeceraDoc.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            Map parametros = new HashMap();
            this.cabeceraDoc = cabeceraDoc;
            parametros.put(";where", "o.cabeccera=:cabeceraDoc");
            parametros.put("cabeceraDoc", cabeceraDoc);
            listadoFacturas = new LinkedList<>();
            listadoObligaciones = new LinkedList<>();
            verFacturas = false;
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            parametros = new HashMap();
            this.cabeceraDoc = cabeceraDoc;
            parametros.put(";where", "o.cabeceradoc=:cabeceraDoc");
            parametros.put("cabeceraDoc", cabeceraDoc);

            listaDetalleCompras = ejbDetallesCompras.encontarParametros(parametros);

            List<Renglones> renglonesBorrarGasto = traerRenglonesGasto();
            List<Renglones> renglonesBorrarAnticipo = traerRenglonesAnticipo();
            List<Renglones> renglonesBorrarInv = traerRenglonesInv();
            List<Renglones> renglonesBorrarDescuentos = traerRenglonesDescuento();
            List<Renglones> renglonesBorrarExterior = traerRenglonesExterior();
//            List<Renglones> renglonesBorrarCuentas = traerRenglonesCuentas();
            List<Renglones> renglonesBorrarMultas = traerRenglonesMultas();
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
            for (Detallescompras d : listaDetalleCompras) {

                Detallecompromiso det = d.getDetallecompromiso();
                double saldo = det.getSaldo().doubleValue() + d.getValor().doubleValue();
                det.setSaldo(new BigDecimal(saldo));
                ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
                ejbDetallesCompras.remove(d, seguridadbean.getLogueado().getUserid());
            }
            for (Documentoselectronicos d : listadoFacturas) {
                parametros = new HashMap();
                Obligaciones obligacionBorrar = d.getObligacion();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Pagosvencimientos> pagos = ejbPagos.encontarParametros(parametros);
//                List<Rascompras> detallesCompras = ejbDetalles.encontarParametros(parametros);
                List<Retencionescompras> detallesCompras = ejbRetencionesCompras.encontarParametros(parametros);
                for (Pagosvencimientos p : pagos) {
                    ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
                }

                for (Retencionescompras r : detallesCompras) {
                    ejbRetencionesCompras.remove(r, seguridadbean.getLogueado().getUserid());
                }

                if (d.getTipo() == 1) {
                    ejbDocElec.remove(d, seguridadbean.getLogueado().getUserid());
                } else {
                    d.setUtilizada(false);
                    d.setCabeccera(null);
                    d.setObligacion(null);
                    ejbDocElec.edit(d, seguridadbean.getLogueado().getUserid());
                }
                PagosExterior pagExt = obligacionBorrar.getPagoExterior();
                ejbObligaciones.remove(obligacionBorrar, seguridadbean.getLogueado().getUserid());
                if (pagExt != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.exterior=:exterior");
                    parametros.put("exterior", pagExt);
                    List<Fondos> listaGrabar = ejbFondos.encontarParametros(parametros);
                    if (!listaGrabar.isEmpty()) {
                        for (Fondos f : listaGrabar) {
                            f.setCerrado(Boolean.FALSE);
                            f.setExterior(null);
                            ejbFondos.edit(f, seguridadbean.getLogueado().getUserid());
                        }
                    }
                    ejbPagosExterior.remove(pagExt, seguridadbean.getLogueado().getUserid());
                }

            }

            // ver todas las cabecceras
            ejbCabdocelect.remove(cabeceraDoc, seguridadbean.getLogueado().getUserid());
            Cabeceras c = null;
            for (Renglones r : renglonesBorrarGasto) {
                c = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());

            }
            if (renglonesBorrarAnticipo != null) {
                c = null;
                for (Renglones r : renglonesBorrarAnticipo) {
                    c = r.getCabecera();
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                if (c != null) {
                    ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());

                }
            }
            if (renglonesBorrarInv != null) {
                c = null;
                for (Renglones r : renglonesBorrarInv) {
                    c = r.getCabecera();
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                if (c != null) {
                    ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());

                }
            }
            if (renglonesBorrarMultas != null) {
                c = null;
                for (Renglones r : renglonesBorrarMultas) {
                    c = r.getCabecera();
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                if (c != null) {
                    ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());

                }
            }
//            if (renglonesBorrarCuentas != null) {
//                c = null;
//                for (Renglones r : renglonesBorrarCuentas) {
//                    c = r.getCabecera();
//                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
//                }
//                if (c != null) {
//                    ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());
//
//                }
//            }
            if (renglonesBorrarDescuentos != null) {
                c = null;
                for (Renglones r : renglonesBorrarDescuentos) {
                    c = r.getCabecera();
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                if (c != null) {
                    ejbCabRen.remove(c, seguridadbean.getLogueado().getUserid());

                }
            }
        } catch (ConsultarException | BorrarException | GrabarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
        MensajesErrores.advertencia("Se ha borrado correctamente");
        listaDetalleCompras = null;
        listaRetencionesCompras = null;
        listadoFacturas = null;
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
    }

    private void cargarReembolsos() {

        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            reembolsos = ejbReembolsos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarDescuentos() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            listaDescuentos = ejbDescuentos.encontarParametros(parametros);
//            for (Retencionescompras r:listaRetencionesCompras){
//                if (r.getImpuesto()==null)
//            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarRetenciones() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            listaRetencionesCompras = ejbRetencionesCompras.encontarParametros(parametros);
//            for (Retencionescompras r:listaRetencionesCompras){
//                if (r.getImpuesto()==null)
//            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String modificarFactura(Documentoselectronicos factura) {
        this.factura = factura;
        this.obligacion = factura.getObligacion();
        setPagosExterior(obligacion.getPagoExterior());
        obligacion.setCompromiso(factura.getCabeccera().getCompromiso());
        cargarRetenciones();
        cargarReembolsos();
        cargarDescuentos();
//        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            pago = new Pagosvencimientos();

            List<Pagosvencimientos> listaPagos = ejbPagos.encontarParametros(parametros);
            for (Pagosvencimientos p : listaPagos) {
                pago = p;
            }
            if (obligacion.getProveedor() != null) {
                proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
                proveedorBean.setProveedor(obligacion.getProveedor());

                if (factura.getTipo() == 1) {
                    // factura manual
                    // buscar autorización
                    facturaBean.setFactura(null);
                    parametros = new HashMap();
                    parametros.put(";where", "o.tipodocumento=:tipodocumento "
                            + "and o.autorizacion=:autorizacion and o.empresa=:empresa");
                    parametros.put("tipodocumento", obligacion.getTipodocumento());
                    parametros.put("autorizacion", obligacion.getAutorizacion());
                    parametros.put("empresa", obligacion.getProveedor().getEmpresa());
                    List<Autorizaciones> la = ejbAutorizaciones.encontarParametros(parametros);
                    for (Autorizaciones a : la) {
                        autorizacion = a;
                    }
                } else {
                    try {
                        // generar factura electrónica

                        facturaBean.cargarMarshal(facturaBean.grabarXml("PRUEBA", obligacion.getFacturaelectronica()));
                    } catch (JAXBException ex) {
                        facturaBean.cargar(obligacion.getFacturaelectronica());
                    }

                }
            } else if (factura.getTipo() != 1) {
                try {
                    facturaBean.cargarMarshal(facturaBean.grabarXml("PRUEBA", obligacion.getFacturaelectronica()));
                } catch (JAXBException ex) {
                    facturaBean.cargar(obligacion.getFacturaelectronica());
                }
            }
            if (obligacion.getPuntor() != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                parametros.put("codigo", obligacion.getPuntor());
                parametros.put("sucursal", obligacion.getEstablecimientor());
                List<Puntoemision> lpt = ejbPuntos.encontarParametros(parametros);
                for (Puntoemision p : lpt) {
                    puntoEmision = p;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formularioObligacion.cancelar();
        formularioEditar.editar();
        calculaTotal();
        return null;
    }

    public String borraFactura(Documentoselectronicos factura) {
        String ru = factura.getRuc();
        try {

            this.factura = factura;
            obligacion = factura.getObligacion();
            factura.setObligacion(null);
            factura.setUtilizada(false);
            factura.setCabeccera(null);
            if (factura.getTipo() == 1) {
                ejbDocElec.remove(factura, seguridadbean.getLogueado().getUserid());
            } else {
                ejbDocElec.edit(factura, seguridadbean.getLogueado().getUserid());
            }
            if (obligacion != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", obligacion);
                List<Pagosvencimientos> lista = ejbPagos.encontarParametros(parametros);
                for (Pagosvencimientos p : lista) {
                    ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
                }
                cargarRetenciones();

                for (Retencionescompras r : listaRetencionesCompras) {
                    ejbRetencionesCompras.remove(r, seguridadbean.getLogueado().getUserid());
                }
                PagosExterior pagext = obligacion.getPagoExterior();
                ejbObligaciones.remove(obligacion, seguridadbean.getLogueado().getUserid());
                if (pagext != null) {
                    ejbPagosExterior.remove(pagext, seguridadbean.getLogueado().getUserid());
                }
            }

            MensajesErrores.advertencia("Factura borrada con exito");
            Map parametros = new HashMap();
            // traer los documentos
            parametros.put(";where", "o.ruc=:ruc and o.tipo=0 and o.cabeccera is null");
            parametros.put("ruc", ru);

            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.cabeccera=:cabecera");
            parametros.put("cabecera", cabeceraDoc);
            listadoObligaciones = ejbDocElec.encontarParametros(parametros);

        } catch (ConsultarException | BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
        return null;
    }

    public String borraEletronica(Documentoselectronicos factura) {
        String ru = factura.getRuc();
        try {
            if (factura.getUtilizada()) {
                MensajesErrores.advertencia("No se puede borrar ya utilizada");
                return null;
            }
            this.factura = factura;

            ejbDocElec.remove(factura, seguridadbean.getLogueado().getUserid());
            Map parametros = new HashMap();
            // traer los documentos
            parametros.put(";where", "o.ruc=:ruc and o.tipo=0 and o.cabeccera is null");
            parametros.put("ruc", ru);

            listadoFacturas = ejbDocElec.encontarParametros(parametros);
            MensajesErrores.advertencia("Factura borrada con exito");

        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
        return null;
    }

    public String nuevoPagoExterior() {
        setPagosExterior(new PagosExterior());
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
//        if ((cabeceraDoc.getValor() == null) || (cabeceraDoc.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Por favor indique un valor");
//            return null;
//        }
        if (cabeceraDoc.getId() == null) {
            try {
                ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());

            } catch (InsertarException ex) {
                Logger.getLogger(PagoRetencionesBean.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.factura = factura;
        this.factura.setCabeccera(cabeceraDoc);
        compromiso = cabeceraDoc.getCompromiso();
        obligacion = new Obligaciones();
        setPagosExterior(null);
        Empresas e = proveedorBean.taerRuc(factura.getRuc());

        listaRetencionesCompras = new LinkedList<>();
        listaDescuentos = new LinkedList<>();
        obligaciones = new LinkedList<>();
        reembolsos = new LinkedList<>();
        obligacion.setContrato(null);
        obligacion.setConcepto(cabeceraDoc.getObservaciones());
//        obligacion.setFechaemision(new Date());
        obligacion.setFechaemision(factura.getFechaemision());
        obligacion.setEstado(0); // No se si 1 o que
        if (e != null) {
            obligacion.setProveedor(e.getProveedores()); // No se si 1 o que
            proveedorBean.setEmpresa(e);
            proveedorBean.setProveedor(e.getProveedores());
        }
        obligacion.setFechaingreso(new Date());
        obligacion.setUsuario(seguridadbean.getLogueado());
        obligacion.setCompromiso(cabeceraDoc.getCompromiso());
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

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        obligacion.setFacturaelectronica(factura.getXml());

        try {

            facturaBean.cargarMarshal(facturaBean.grabarXml("PRUEBA", obligacion.getFacturaelectronica()));

        } catch (JAXBException ex) {
            facturaBean.cargar(obligacion.getFacturaelectronica());
//            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        facturaBean.cargar(obligacion.getFacturaelectronica());

        pago = new Pagosvencimientos();
//        promesas = new LinkedList<>();
//        promesasb = new LinkedList<>();
//        formulario.cancelar();
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
        setPagosExterior(null);
        if (cabeceraDoc.getCompromiso() == null) {
            MensajesErrores.advertencia("Seleccione un compromiso primero");
            return null;
        }
        if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Por favor indique el motivo");
            return null;
        }
//        if ((cabeceraDoc.getValor() == null) || (cabeceraDoc.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Por favor indique un valor");
//            return null;
//        }
        if (cabeceraDoc.getId() == null) {
            try {
                ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());

            } catch (InsertarException ex) {
                Logger.getLogger(PagoRetencionesBean.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        compromiso = cabeceraDoc.getCompromiso();
        this.factura = new Documentoselectronicos();
        if (cabeceraDoc.getCompromiso().getProveedor() == null) {
            if (cabeceraDoc.getCompromiso().getBeneficiario() == null) {
                factura.setRuc(cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getPin());
            }
        } else {
            factura.setRuc(cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc());
        }
        factura.setTipo(1);
        this.factura.setCabeccera(cabeceraDoc);

        listaRetencionesCompras = new LinkedList<>();
        listaDescuentos = new LinkedList<>();
        reembolsos = new LinkedList<>();
        obligacion = new Obligaciones();
//        Empresas e = proveedorBean.taerRuc(factura.getRuc());
//        obligacion.setProveedor(cabeceraDoc.getProveedor());
        obligacion.setContrato(null);
        obligacion.setFechaemision(new Date());
        obligacion.setEstado(0); // No se si 1 o que
        obligacion.setProveedor(compromiso.getProveedor()); // No se si 1 o que
        obligacion.setCompromiso(compromiso); // No se si 1 o que
        obligacion.setFechaingreso(new Date());
        obligacion.setConcepto(cabeceraDoc.getObservaciones());
        obligacion.setUsuario(seguridadbean.getLogueado());
        obligacion.setDocumento(0);
        facturaBean.setFactura(null);
//  trer la informacióndel punto electronico
        Map paraemtros = new HashMap();
//        paraemtros.put(";where", "o.automatica=true");
        try {
            List<Puntoemision> listaPunto = ejbPuntos.encontarParametros(paraemtros);
            for (Puntoemision pe : listaPunto) {
                puntoEmision = pe;
            }
            obligacion.setPuntoemision(puntoEmision.getCodigo());
            obligacion.setEstablecimientor(puntoEmision.getSucursal().getRuc());
            obligacion.setFechar(cabeceraDoc.getFecha());

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (compromiso.getProveedor() != null) {
            proveedorBean.setEmpresa(compromiso.getProveedor().getEmpresa());
            proveedorBean.setProveedor(compromiso.getProveedor());
        }
        obligacion.setFacturaelectronica(null);
        obligacion.setUsuario(seguridadbean.getLogueado());
        pago = new Pagosvencimientos();
        formularioObligacion.cancelar();
        formularioEditar.insertar();
        calculaTotal();
        obligacion.setSustento(codigosBean.getSustentoDefault());
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

    private void armarCuentaProveedor(Cuentas c, List<Cuentas> lista) {
        for (Cuentas c1 : lista) {
            if (c1.equals(c)) {
                return;
            }
        }
        lista.add(c);
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
        parametros.put(";campo", "o.valor");
        try {
            retorno = ejbDetalleCompromiso.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double taerValorObligacion(Obligaciones o) {
//        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor");
        try {
            retorno = ejbDetallesCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String salirObligacion() {
        formulario.cancelar();
        formularioObligacion.cancelar();
        return null;
    }

    public String salirModificacion() {
//        salirTotal();
//        obligacion = new Obligaciones();
        facturaBean.setFactura(null);
        formularioObligacion.editar();
//        for (Detallescompras d : listaDetalleCompras) {
//                
//                Detallecompromiso det=d.getDetallecompromiso();
//                double saldo=det.getSaldo().doubleValue()+d.getValor().doubleValue();
//                det.setSaldo(new BigDecimal(saldo));
//                ejbDetalleCompromiso.edit(det, seguridadbean.getLogueado().getUserid());
//                ejbDetallesCompras.remove(d, seguridadbean.getLogueado().getUserid());
//            }
//        modificar(getCabeceraDoc());
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
        if (impuesto != null) {
            if (factura.getBaseimponible() == null) {
                factura.setBaseimponible(BigDecimal.ZERO);
            }
            double base = factura.getBaseimponible().setScale(2, RoundingMode.HALF_UP).doubleValue();
            double cuadre = impuesto.getPorcentaje().setScale(2).doubleValue() / 100;
            double ajuste = (double) factura.getAjuste() / 100;
            double producto = (base * cuadre) + ajuste;
            DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
            String vstr = df.format(producto);
//            double cuadre = (factura.getBaseimponible().multiply(impuesto.getPorcentaje()).setScale(2).doubleValue() / 100);
//            double dividido = cuadre / 100;
            BigDecimal ivb = new BigDecimal(vstr).setScale(2, RoundingMode.HALF_UP);

//            double iv =   (factura.getBaseimponible().multiply(impuesto.getPorcentaje()).setScale(2).doubleValue() / 100);
//            int cuadre = (int) ((iv+(factura.getAjuste() / 100))*100);
//            double dividido = (double) cuadre / 100;
//            BigDecimal bd = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
//            iv = bd.doubleValue();
//            iv = Math.round(iv / 100);
            factura.setIva(ivb);
        }
        // sumar todos los ras
        double total = 0;

        if (listaRetencionesCompras == null) {
            listaRetencionesCompras = new LinkedList<>();
        }
        if (listaDescuentos == null) {
            listaDescuentos = new LinkedList<>();
        }
        double valorRetenciones = 0;
        double valorRetIva = 0;
        double valorRetFuente = 0;
        double valorBase = 0;
        double valorBase0 = 0;
        double valorBaseIva = 0;
        double valorDescuentos = 0;
        boolean entro = false;
        for (Descuentoscompras dc : listaDescuentos) {
            valorDescuentos += dc.getValor().doubleValue();
        }
        for (Retencionescompras rc : listaRetencionesCompras) {
            double valor = 0;
            entro = true;
            if (rc.getRetencion() != null) {
//                valor = (rc.getBaseimponible().doubleValue() + rc.getBaseimponible0().doubleValue())
//                        * rc.getRetencion().getPorcentaje().doubleValue();
                valor = rc.getValor().doubleValue();
            }

            double iva = 0;
            if (rc.getImpuesto() != null) {
//                iva = rc.getBaseimponible().doubleValue() * rc.getImpuesto().getPorcentaje().doubleValue() / 100;
                iva = rc.getIva().doubleValue();
            }
            double valorIva = 0;
            if (rc.getRetencionimpuesto() != null) {
//                valorIva = iva * rc.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
                valorIva = rc.getValoriva().doubleValue();
            }
//            rc.setIva(new BigDecimal(iva));
//            rc.setValor(new BigDecimal(valor / 100));
//            rc.setValoriva(new BigDecimal(valorIva / 100));

            valorRetFuente += valor;
            valorRetIva += valorIva;
            valorBase += rc.getBaseimponible().doubleValue();
            valorBase0 += rc.getBaseimponible0().doubleValue();
            valorBaseIva += rc.getIva().doubleValue();
            valorRetenciones += (valor + valorIva);
        }
        valorBase = factura.getBaseimponible() == null ? valorBase : factura.getBaseimponible().doubleValue();
        valorBase0 = factura.getBaseimponible0() == null ? valorBase0 : factura.getBaseimponible0().doubleValue();
        valorBaseIva = factura.getIva() == null ? valorBaseIva : factura.getIva().doubleValue();
        if (!entro) {
            valorRetenciones = 0;
            valorRetIva = 0;
            valorRetFuente = 0;

        }
//        totales.add(a);
        AuxiliarCarga a = new AuxiliarCarga();
        a.setTotal("Base Imponible 0");
        a.setCuenta("Egresos");
        a.setIngresos(new BigDecimal(valorBase0));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Base Imponible");
        a.setCuenta("Egresos");
        a.setIngresos(new BigDecimal(valorBase));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("IVA");
        a.setCuenta("Egresos");
        a.setIngresos(new BigDecimal(valorBaseIva));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("(-) Retención IR");
        a.setCuenta("Egresos");
        a.setIngresos(new BigDecimal(valorRetFuente));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("(-) Retención IVA");
        a.setCuenta("Egresos");
        a.setIngresos(new BigDecimal(valorRetIva));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Total Retenciones");
        a.setCuenta("Retenciones");
        a.setIngresos(new BigDecimal(valorRetenciones));
        double valorAntcipo = pago.getValoranticipo() == null ? 0 : pago.getValoranticipo().doubleValue();
        double valorMulta = pago.getMulta() == null ? 0 : pago.getMulta().getMulta().doubleValue();
        if (valorAntcipo != 0) {

            a = new AuxiliarCarga();
            a.setTotal("Total Anticipos");
            a.setCuenta("Anticipos");
            a.setIngresos(new BigDecimal(valorAntcipo));
            totales.add(a);
        }
        if (valorMulta != 0) {

            a = new AuxiliarCarga();
            a.setTotal("Total Multas");
            a.setCuenta("Multas");
            a.setIngresos(new BigDecimal(valorMulta));
            totales.add(a);
        }
        if (valorDescuentos > 0) {
            a = new AuxiliarCarga();
            a.setTotal("Total Descuentos");
            a.setCuenta("Descuentos");
            a.setIngresos(new BigDecimal(valorDescuentos));
            totales.add(a);
        }
//        totales.add(a);
//        total += vAnticipos;
        double vPagos = 0;
        a = new AuxiliarCarga();
        a.setTotal("Por Pagar");
        a.setCuenta("Pagos");
//        double valorAntcipo = pago.getValoranticipo() == null ? 0 : pago.getValoranticipo().doubleValue();
//        double valorMulta = pago.getMulta() == null ? 0 : pago.getMulta().getMulta().doubleValue();
        pago.setFecha(new Date());
        pago.setFechapago(new Date());
        pago.setPagado(false);
        pago.setValormulta(new BigDecimal(valorMulta));
        pago.setObligacion(obligacion);
//        pago.setValor(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
        pago.setValor(new BigDecimal((valorBase + valorBaseIva + valorBase0)
                - (valorRetFuente + valorRetIva + valorAntcipo + valorMulta + valorDescuentos)));
        vPagos += pago.getValor().doubleValue();
        a.setIngresos(new BigDecimal(vPagos));
//        obligacion.setApagar(new BigDecimal(total + vAnticipos - vRetenciones ));
//        obligacion.setApagar(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
        obligacion.setApagar(new BigDecimal((valorBase + valorBaseIva + valorBase0)
                - (valorRetFuente + valorRetIva + valorAntcipo + valorMulta + valorDescuentos)));
        totales.add(a);
        if (getFacturaBean().getFactura() != null) {
            // existe valor de factura electrónica
            if (getFacturaBean().getFactura().getInfoFactura().getImporteTotal() != null) {
                a = new AuxiliarCarga();
                a.setTotal("Valor Total Factura");
                a.setCuenta("FACTURA");
                String it = getFacturaBean().getFactura().getInfoFactura().getImporteTotal();
                a.setIngresos(new BigDecimal(it.trim()));
                totales.add(a);
            }
        }

        try {
            if (obligacion.getId() != null) {
                pago.setObligacion(obligacion);
                if (pago.getId() == null) {
                    ejbPagos.create(pago, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbPagos.edit(pago, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // genera retencion
//        generaRetElectronica(true, false);
//        MensajesErrores.informacion("Totales calculados correctamente");
        return (valorBase + valorBaseIva + valorBase0) - (valorRetFuente + valorRetIva);
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

    public String cruceFondos() {
        formularioCruceFondos.insertar();
        return null;
    }

    public String buscarCruceFondos() {
        listaFondos = new LinkedList<>();
        if (empleadosBean.getApellidos().isEmpty() || empleadosBean.getApellidos() == null) {
            if (empleadosBean.getEmpleadoSeleccionado() != null) {
                if (empleadosBean.getEmpleadoSeleccionado().getEntidad() != null) {
                    empleadosBean.getEmpleadoSeleccionado().setEntidad(null);
                }
            }
            empleadosBean.setEmpleadoSeleccionado(null);
        }
        Map parametros = new HashMap();
        String where = "o.empleado is not null and o.cerrado=false";
        if (getEmpleadosBean().getEmpleadoSeleccionado() != null) {
            where += " and o.empleado=:empleado";
            parametros.put("empleado", getEmpleadosBean().getEmpleadoSeleccionado());
        }
        parametros.put(";where", where);
        parametros.put(";orden", "o.empleado.entidad.apellidos");
        try {
            listaFondos = ejbFondos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarCruceFondos() {
        listaFondosGrabar = new LinkedList<>();
        for (Fondos f : listaFondos) {
            if (f.isSeleccionado()) {
                listaFondosGrabar.add(f);
            }
        }

        formularioCruceFondos.cancelar();
        return null;
    }

    /**
     *
     * @return
     */
    public String garbarTodo() {

        // validar el total de obligaciones con el total de comrpomisos
        try {
            double valorPresupuesto = 0;
            for (Detallescompras d : listaDetalleCompras) {
                if (d.getValor().doubleValue() > 0) {
                    double saldo = d.getDetallecompromiso().getSaldo().doubleValue();
                    double valor = d.getValor().doubleValue();
                    Detallecompromiso detalleCompromiso = d.getDetallecompromiso();
                    double saldoActual = saldo + d.getValorAnterior();
                    double acomparar = Math.round((saldoActual - valor) * 100);
                    valorPresupuesto += valor;
                    if (acomparar < 0) {
                        String paraMensaje = d.getDetallecompromiso().getAsignacion().getClasificador().getCodigo()
                                + "-" + d.getDetallecompromiso().getAsignacion().getProyecto().getCodigo()
                                + "-" + d.getDetallecompromiso().getAsignacion().getFuente().getCodigo()
                                + "-" + d.getCuenta().toString() + acomparar;
                        MensajesErrores.advertencia("Saldo de presupuesto menor a consumido " + paraMensaje);
                        return null;
                    }
                }
            }
            if (cabeceraDoc.getCompromiso() == null) {
                MensajesErrores.advertencia("Seleccione un compromiso primero");
                return null;
            }
            if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
                MensajesErrores.advertencia("Por favor indique el motivo");
                return null;
            }
            valorPresupuesto = Math.round(valorPresupuesto * 100) / 100;
            cabeceraDoc.setValor(new BigDecimal(valorPresupuesto));
            if (cabeceraDoc.getCompromiso().getProveedor() != null) {
                proveedorBean.grabarEmpresaProveedor();
            }
//            if ((cabeceraDoc.getValor() == null) || (cabeceraDoc.getValor().doubleValue() <= 0)) {
//                MensajesErrores.advertencia("Por favor indique un valor");
//                return null;
//            }
            double valorApagar = 0;
            for (Documentoselectronicos d : listadoObligaciones) {
                if (!d.getObligacion().getTipodocumento().getCodigo().equals("OTR")) {
                    if (d.getValortotal() != null) {
                        valorApagar += d.getValortotal().doubleValue();
                    }
                }
            }
            valorApagar = Math.round(valorApagar * 100) / 100;
//            if (valorApagar > valorPresupuesto) {
//                MensajesErrores.advertencia("Valor de facturas excede el límite de orden de pago");
//                return null;
//            }
            if (cabeceraDoc.getId() == null) {
                ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());
            } else {
                ejbCabdocelect.edit(getCabeceraDoc(), seguridadbean.getLogueado().getUserid());
            }
            for (Detallescompras d : listaDetalleCompras) {
                if (d.getValor().doubleValue() > 0) {
                    double saldo = d.getDetallecompromiso().getSaldo().doubleValue();
                    double valor = d.getValor().doubleValue();
                    Detallecompromiso detalleCompromiso = d.getDetallecompromiso();
                    double saldoActual = saldo + d.getValorAnterior();
                    double acomparar = Math.round((saldoActual - valor) * 100);
                    d.setDetallecompromiso(detalleCompromiso);
                    detalleCompromiso.setSaldo(new BigDecimal(acomparar / 100));
                    ejbDetalleCompromiso.edit(detalleCompromiso, seguridadbean.getLogueado().getUserid());

                } else if ((d.getValorAnterior() > 0) && (d.getValor().doubleValue() == 0)) {
                    double saldo = d.getDetallecompromiso().getSaldo().doubleValue();
                    double valor = d.getValor().doubleValue();
                    Detallecompromiso detalleCompromiso = d.getDetallecompromiso();
                    double saldoActual = saldo + d.getValorAnterior();
                    detalleCompromiso.setSaldo(new BigDecimal(saldoActual));
                    d.setDetallecompromiso(detalleCompromiso);
                    ejbDetalleCompromiso.edit(detalleCompromiso, seguridadbean.getLogueado().getUserid());
                }
                d.setCabeceradoc(cabeceraDoc);
                if (d.getId() == null) {
                    ejbDetallesCompras.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetallesCompras.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            double valorObligaciones = 0;

            // buscar pago
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        salirObligacion();
//        formularioObligacion.cancelar();
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
//        modificar(getCabeceraDoc());
        salirModificacion();
        llenarFacturasElectronicas();
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

        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion");
            return true;
        }
//        if (obligacion.getProveedor() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor primero");
//            return true;
//        }

//        if (listaRetencionesCompras.isEmpty()) {
//            MensajesErrores.advertencia("Necesario retenciones");
//            return true;
//        }
        double valorRetenciones = 0;
        double valorRetIva = 0;
        double valorRetFuente = 0;
        double valorBase = 0;
        double valorIva = 0;

        for (Retencionescompras rc : listaRetencionesCompras) {
            double iva = 0;
            valorBase += rc.getBaseimponible().doubleValue();
            valorBase += rc.getBaseimponible0().doubleValue();
            if (rc.getImpuesto() != null) {
                iva = rc.getIva().doubleValue();
//                iva = rc.getBaseimponible().doubleValue() * rc.getImpuesto().getPorcentaje().doubleValue() / 100;
            }
            double retFuente = 0;
            double retIva = 0;
            if (rc.getRetencionimpuesto() != null) {
                retIva += rc.getValor().doubleValue();
//                retIva += iva * rc.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
            }
            if (rc.getRetencion() != null) {
                retFuente = rc.getValoriva().doubleValue();
//                retFuente = (rc.getBaseimponible().doubleValue() + rc.getBaseimponible0().doubleValue())
//                        * rc.getRetencion().getPorcentaje().doubleValue() / 100;

            }
//            rc.setIva(new BigDecimal(iva));
//            rc.setValoriva(new BigDecimal(retIva));
//            rc.setValor(new BigDecimal(retFuente));
            valorIva += iva;
            valorRetIva += retIva;
            valorRetFuente += retFuente;
            valorRetenciones += valorRetFuente + valorRetIva;
        }
        double iva = factura.getIva() == null ? 0 : factura.getIva().doubleValue();
        double base = (factura.getBaseimponible() == null ? 0 : factura.getBaseimponible().doubleValue());
        base += (factura.getBaseimponible0() == null ? 0 : factura.getBaseimponible0().doubleValue());
        double cuadre = Math.round((valorIva - iva) * 100);
        if (!listaRetencionesCompras.isEmpty()) {

            if (cuadre != 0) {
                MensajesErrores.advertencia("No cuadra valor del iva");
                return true;
            }
//            if (Math.round((base - valorBase) * 100) != 0) {
//                MensajesErrores.advertencia("No cuadra base imponible");
//                return true;
//            }
        }
        factura.setValortotal(new BigDecimal(iva + base));
        Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
        Map parametros = new HashMap();
        parametros.put(";where", "o.punto=:punto and o.documento=:documento");
        parametros.put("punto", puntoEmision);
        parametros.put("documento", ret);
        documento = null;
        try {
            List<Documentos> documentos = ejbDocumnetos.encontarParametros(parametros);
            for (Documentos d : documentos) {
                documento = d;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // ver si egresos igual a certificacion
//        obligacion.setApagar(new BigDecimal(egreso));
//        if (cuadre != 0) {
//            MensajesErrores.advertencia("Egresos mayor a la cabeceraDoc presupuestario");
//            return true;
//        }
        if (facturaBean.getFactura() != null) {
            List<Codigos> cl = codigosBean.traerCodigoParametros(Constantes.DOCUMENTOS, facturaBean.getFactura().getInfoTributaria().getCodDoc());
            for (Codigos c : cl) {
                obligacion.setTipodocumento(c);
            }
        }
        if (obligacion.getTipodocumento() == null) {
            MensajesErrores.advertencia("No esta seleccionado el tipo de documento");
            return true;
        }
        if (obligacion.getTipodocumento().getCodigo().equals("OTR")) {

        } else {
            BigDecimal dias = BigDecimal.valueOf(consultaDias(obligacion.getFechaemision(), obligacion.getFechar()));
            double d = dias.doubleValue();
            if (d >= 10) {
                MensajesErrores.advertencia("Fecha de emisión de retención excede los 10 días");
            } else {
                if (obligacion.getFechar().before(obligacion.getFechaemision())) {
                    MensajesErrores.advertencia("Fecha de emisión de retención antes de Fecha de Factura");
                    return true;
                }
            }
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
                if (obligacion.getProveedor() != null) {
                    parametros = new HashMap();
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
                            MensajesErrores.advertencia("Factura ya registrada " + obligacion.getDocumento());
                            return true;
                        }

                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger
                                .getLogger(ObligacionesBean.class
                                        .getName()).log(Level.SEVERE, null, ex);
                    }
                }
//            obligacion.setPuntoemision(autorizacion.getPuntoemision());

            } else {
                // factura electronica
//            double importeTotal = Double.parseDouble(facturaBean.getFactura().getInfoFactura().getImporteTotal()) ;
//            if (Math.round(importeTotal) != 0) {
//                MensajesErrores.advertencia("Importe total no cuadra con los egresos en la factura");
//                return true;
//            }
                if (!facturaBean.getFactura().getInfoTributaria().getRuc().equals(obligacion.getProveedor().getEmpresa().getRuc())) {
                    MensajesErrores.advertencia("RUC de factura electrónica no corresponde al emisor");
                    return true;
                }
//            if (!facturaBean.getFactura().getInfoFactura().getIdentificacionComprador().equals(configuracionBean.getConfiguracion().getRuc())) {
//                MensajesErrores.advertencia("Factura no esta emitida para " + configuracionBean.getConfiguracion().getNombre());
//                return true;
//            }
                parametros = new HashMap();
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
                        MensajesErrores.advertencia("Factura ya registrada no autorización:" + facturaBean.getFactura().getAutorizacion().getNumeroAutorizacion());
                        return true;
                    }

                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger
                            .getLogger(ObligacionesBean.class
                                    .getName()).log(Level.SEVERE, null, ex);
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
        }
        double suma = 0;
        for (Retencionescompras r : listaRetencionesCompras) {
            suma += r.getValor().doubleValue();
        }
        if (suma > 0) {
            if (puntoEmision == null) {
                MensajesErrores.advertencia("Seleccione un punto de emisión");
                return true;
            }
            // traer una utorización de las retenciones

            if (!puntoEmision.getAutomatica()) {
                // es numeración automática
                /// Traer autorizacion igual que la electronica
                if (documento == null) {
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
//                if ((obligacion.getNumeror() == null) || (obligacion.getNumeror() <= 0)) {
//                    MensajesErrores.advertencia("Es necesario el número de documento de la emisión de la retención");
//                    return true;
//                }
//                if (documento.getInicial() > obligacion.getNumeror()) {
//                    MensajesErrores.advertencia("Documento de la retención menor al número mínimo de la autorización");
//                    return true;
//                }
//                if (documento.getFinal1() < obligacion.getNumeror()) {
//                    MensajesErrores.advertencia("Documento de la retención mayor al número máximo de la autorización");
//                    return true;
//                }
                if (documento.getDesde().after(obligacion.getFechar())) {
                    MensajesErrores.advertencia("Fecha de autorización de retención menor a la fecha desde de la autorización");
                    return true;
                }
                if (obligacion.getFechar().after(new Date())) {
                    MensajesErrores.advertencia("Fecha de de retención de ser menor a hoy");
                    return true;
                }
                if (documento.getHasta().before(obligacion.getFechar())) {
                    MensajesErrores.advertencia("Fecha de autorización de retención mayor a la fecha hasta de la autorización");
                    return true;
                }
                obligacion.setPuntor(documento.getPunto().getCodigo());
                obligacion.setEstablecimientor(documento.getPunto().getSucursal().getRuc());
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
            parametros = new HashMap();
            parametros.put("anticipo", pago.getAnticipo());
            parametros.put(";campo", "o.valoranticipo");
            parametros.put(";where", "o.anticipo=:anticipo  and o.id!=:id");
            parametros.put("id", pago.getId());
            try {
                double valor = cuantoAnticipo - ejbPagos.sumarCampo(parametros).doubleValue();

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

    public double consultaDias(Date d, Date h) {
        long retorno = ((h.getTime() - d.getTime()) / (60000 * 60 * 24)) + 1;
        double factor = 1;
        return retorno * factor - 1;
    }

    private void grabaObligacion() {

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
            if (fechaEmision != null) {
                obligacion.setFechaemision(fechaEmision);
            }
            if (pagosExterior != null) {
                if (pagosExterior.getId() == null) {
                    ejbPagosExterior.create(pagosExterior, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbPagosExterior.edit(pagosExterior, seguridadbean.getLogueado().getUserid());
                }
                for (Fondos f : listaFondosGrabar) {
                    f.setExterior(pagosExterior);
                    ejbFondos.edit(f, seguridadbean.getLogueado().getUserid());
                }
                obligacion.setPagoExterior(getPagosExterior());
            }
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            } else {
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            }
            // grabar ras

            // lo mismo para promesas
            if (pago.getId() == null) {
                pago.setObligacion(obligacion);
                ejbPagos.create(pago, seguridadbean.getLogueado().getUserid());
            } else {
                pago.setObligacion(obligacion);
                ejbPagos.edit(pago, seguridadbean.getLogueado().getUserid());
            }
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
            cargarRetenciones();
            cargarReembolsos();
            for (Retencionescompras r : listaRetencionesCompras) {
                ejbRetencionesCompras.remove(r, seguridadbean.getLogueado().getUserid());
            }
            for (Reembolsos r : reembolsos) {
                ejbReembolsos.remove(r, seguridadbean.getLogueado().getUserid());
            }
            PagosExterior pagoExterior = obligacion.getPagoExterior();
            ejbObligaciones.remove(obligacion, seguridadbean.getLogueado().getUserid());
            ejbPagosExterior.remove(pagoExterior, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        salirModificacion();
        vistaObligaciones();
        llenarFacturasElectronicas();
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

    public SelectItem[] getComboPartidas() {
        Formatos f = ejbDocElec.traerFormato();
        if (f == null) {
            MensajesErrores.advertencia("Mal configurado formato");
            return null;
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        if (listaDetalleCompras == null) {
            return null;
        }
        int tamano = aux[f.getNivel() - 1].length();
        for (Detallescompras d : listaDetalleCompras) {
            String cuentaPresupuesto = d.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            String presupuesto = cuentaPresupuesto.substring(0, tamano);
            estaPartida(presupuesto);
        }
        return Combos.comboStrings(partidas, false);
    }

    public SelectItem[] getComboRetenciones() {
        if (retencion == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false and o.bien=:bien");
        parametros.put("bien", retencion.getBien());
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
        if (retencion == null) {
            return null;
        }
        boolean especial = false;
        if (obligacion.getProveedor() != null) {

            especial = obligacion.getProveedor().getEmpresa().getEspecial();
        } else {
            Proveedores pro = cabeceraDoc.getCompromiso().getProveedor();
            if (pro != null) {
                obligacion.setProveedor(pro);
                especial = obligacion.getProveedor().getEmpresa().getEspecial();
            }
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true and o.bien=:bien and o.especial=:especial");
        parametros.put("especial", especial);
        parametros.put("bien", retencion.getBien());
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

    public SelectItem[] getComboRetencionesLote() {
        if (retencionLote == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false and o.bien=:bien");
        parametros.put("bien", retencionLote.getBien());
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

    public SelectItem[] getComboRetencionesLoteImpuestos() {
        if (retencionLote == null) {
            return null;
        }
        boolean especial = false;
        if (cabeceraDoc.getCompromiso().getProveedor() == null) {
            especial = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getEspecial();
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true and o.bien=:bien and o.especial=:especial");
        parametros.put("especial", especial);
        parametros.put("bien", retencionLote.getBien());
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

    public SelectItem[] getComboAutorizaciones() {
        if (obligacion == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        if (obligacion.getProveedor() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }

        if (obligacion.getTipodocumento() == null) {
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        Map parametros = new HashMap();
//        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento.codigo not in('RET')");
        parametros.put(";where", "o.empresa=:empresa and o.tipodocumento=:tipo");
        parametros.put("empresa", obligacion.getProveedor().getEmpresa());
        parametros.put(";orden", "o.inicio desc");
        parametros.put("tipo", obligacion.getTipodocumento());
        try {
            return Combos.getSelectItems(ejbAutorizaciones.encontarParametros(parametros), false);
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
                    + " and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_LOTE','OBLIGACIONES_INV_LOTE','OBLIGACIONES_ANTICIPO_LOTE','OBLIGACIONES_MULTAS_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesGasto() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesMultas() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_MULTAS_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesDescuento() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_DESCUENTO_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesExterior() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_EXTERIOR_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesInv() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_INV_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesAnticipo() {
        if (cabeceraDoc != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "  and o.cabecera.opcion "
                    + " in ('OBLIGACIONES_ANTICIPO_LOTE')");
            parametros.put("id", cabeceraDoc.getId());
            parametros.put(";orden", "o.valor,o.cuenta");
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesAnticipoAnterior(Pagosvencimientos p) {
        if (p != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.anticipo=:anticipo");
            parametros.put("anticipo", p.getAnticipo());
            try {
                List<Kardexbanco> listaKardex = ejbKardexbanco.encontarParametros(parametros);
                if (!listaKardex.isEmpty()) {
                    Kardexbanco k = listaKardex.get(0);
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('PAGO PROVEEDORES','ANTICIPO PROVEEDORES')");
                    parametros.put("id", k.getId());
                    parametros.put(";orden", "o.valor,o.cuenta");
                    List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                    return rl;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesMultaAnterior(Pagosvencimientos p) {
        if (p != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('MULTAS')");
            parametros.put("id", p.getMulta().getId());
            parametros.put(";orden", "o.valor,o.cuenta");
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

    public SelectItem[] getComboAnticipos() {

        // solo para quitar el error
        if (obligacion == null) {
            return null;
        }
        if (obligacion.getContrato() == null) {
            return null;
        }
        Calendar cuando = Calendar.getInstance();
        Calendar periodoActual = Calendar.getInstance();
//        cuando.setTime(obligacion.getContrato().getInicio());
        if (obligacion.getContrato().getFechaanticipo() == null) {
            cuando.setTime(obligacion.getContrato().getInicio());
        } else {
            cuando.setTime(obligacion.getContrato().getFechaanticipo());
        }
        periodoActual.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anioActual = periodoActual.get(Calendar.YEAR);
        int anioContrato = cuando.get(Calendar.YEAR);
        if (anioContrato < anioActual) {
            List<Anticipos> listaAn = new LinkedList<>();
            List<Renglones> lista = traerRenglonesCuentas();
            for (Renglones r : lista) {
                Anticipos a = new Anticipos();
                a.setValor(r.getValor().abs());
                a.setReferencia(r.getReferencia());
                listaAn.add(a);
            }
            return Combos.getSelectItems(listaAn, false);
        } else {
            Map parametros = new HashMap();
            String where = "o.contrato=:contrato and (o.aplicado=false or o.aplicado is null)";

            parametros.put(";where", where);
            parametros.put("contrato", obligacion.getContrato());
            try {
                List<Anticipos> lista = ejbAnticipos.encontarParametros(parametros);
                if (lista.isEmpty()) {
                    return null;
                }
                List<Anticipos> retorno = new LinkedList<>();
                // consultar cuanto esta consumido
                for (Anticipos a : lista) {
                    parametros = new HashMap();
                    where = "o.anticipo=:anticipo  and o.obligacion!=:obligacion";
                    parametros.put(";where", where);
                    parametros.put("anticipo", a);
                    parametros.put(";campo", "o.valoranticipo");
                    parametros.put("obligacion", obligacion);
                    double valor = ejbPagos.sumarCampo(parametros).doubleValue();
                    double saldo = Math.abs((a.getValor().doubleValue() - valor) * 100);
                    if (saldo > 0) {
                        retorno.add(a);
                    }
                }
                return Combos.getSelectItems(retorno, false);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        formulario.cancelar();
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
            compromiso = compromisoNuevo;
            cabeceraDoc.setCompromiso(compromiso);
            cargarDetalleCompras();
//            vistaFacturas();
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
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formularioDetalleDetalle.editar();
        return null;

    }

    public int getTotalObligaciones() {
        verFacturas = true;
        if (cabeceraDoc.getId() == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabeccera=:cabecera");
        parametros.put("cabecera", cabeceraDoc);
        try {
            return ejbDocElec.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formularioDetalleDetalle.editar();
        return 0;

    }

    public String vistaFacturas() {
        verFacturas = false;
        formularioDetalleDetalle.eliminar();
        return null;
    }

    public String vistaRetenciones() {

        verFacturas = false;
        formularioDetalleDetalle.resultados();
        if (cabeceraDoc.getId() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabeccera=:cabecera");
        parametros.put("cabecera", cabeceraDoc);
        listaRetencionesComprasInt = new LinkedList<>();
        try {
            listadoObligaciones = ejbDocElec.encontarParametros(parametros);
            for (Documentoselectronicos d : listadoObligaciones) {
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Retencionescompras> ls = ejbRetencionesCompras.encontarParametros(parametros);
                for (Retencionescompras r : ls) {
                    listaRetencionesComprasInt.add(r);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String vistaPresupuesto() {
        formularioDetalleDetalle.insertar();
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
     * @return the listaDetalleCompras
     */
    public List<Detallescompras> getListaDetalleCompras() {
        return listaDetalleCompras;
    }

    /**
     * @param listaDetalleCompras the listaDetalleCompras to set
     */
    public void setListaDetalleCompras(List<Detallescompras> listaDetalleCompras) {
        this.listaDetalleCompras = listaDetalleCompras;
    }

    /**
     * @return the listaRetencionesCompras
     */
    public List<Retencionescompras> getListaRetencionesCompras() {
        return listaRetencionesCompras;
    }

    /**
     * @param listaRetencionesCompras the listaRetencionesCompras to set
     */
    public void setListaRetencionesCompras(List<Retencionescompras> listaRetencionesCompras) {
        this.listaRetencionesCompras = listaRetencionesCompras;
    }

    public String nuevoDetalle() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        if (obligacion.getId() == null) {
            grabaObligacion();
        }

        retencion = new Retencionescompras();
        retencion.setBien(true);
        retencion.setBaseimponible(new BigDecimal(BigInteger.ZERO));
        retencion.setBaseimponible0(new BigDecimal(BigInteger.ZERO));
        retencion.setIva(new BigDecimal(BigInteger.ZERO));
        retencion.setValor(new BigDecimal(BigInteger.ZERO));
        retencion.setValoriva(new BigDecimal(BigInteger.ZERO));
        formularioDetalle.insertar();
        return null;
    }

    public String nuevoReembolso() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        if (obligacion.getId() == null) {
            grabaObligacion();
        }

        if (reembolsos == null) {
            reembolsos = new LinkedList<>();
        }
        reembolso = new Reembolsos();
        reembolso.setFechaemision(new Date());
        reembolso.setBaseimponible(BigDecimal.ZERO);
        reembolso.setBaseimponible0(BigDecimal.ZERO);
        reembolso.setValoriva(BigDecimal.ZERO);
        formularioReembolso.insertar();
        return null;
    }

    public String nuevoDescuento() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        if (obligacion.getId() == null) {
            grabaObligacion();
        }

        if (listaDescuentos == null) {
            listaDescuentos = new LinkedList<>();
        }
        descuento = new Descuentoscompras();
        formularioDescuento.insertar();
        return null;
    }

    public String modificaDetalle(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioDetalle.editar();
        return null;
    }

    public String modificaRembolso(Reembolsos reembolso) {
        this.reembolso = reembolso;
        formularioReembolso.editar();
        return null;
    }

//    public String modificaDescuentos(Descuentoscompras descuento) {
//        this.descuento = descuento;
//        formularioDescuento.editar();
//        return null;
//    }
    public String borraDetalle(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioDetalle.eliminar();
        return null;
    }

    public String borraDescuento(Descuentoscompras descuento) {
        this.descuento = descuento;
        formularioDescuento.eliminar();
        return null;
    }

    public String borraRembolso(Reembolsos reembolso) {
        this.reembolso = reembolso;
        formularioReembolso.eliminar();
        return null;
    }

    private boolean validarDetalle() {
//        if ((retencion.getBaseimponible() == null) || (retencion.getBaseimponible().doubleValue() < 0)) {
//            MensajesErrores.advertencia("Es necesario la base imponible");
//            return true;
//        }
//        if (retencion.getRetencion() == null) {
//            MensajesErrores.advertencia("Es necesario la retención");
//            return true;
//        }
        if (retencion.getRetencion() == null && retencion.getRetencionimpuesto() == null) {
            MensajesErrores.advertencia("Es necesario al menos una retención");
            return true;
        }
//        double valorRetencion = retencion.getBaseimponible().doubleValue() * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
//        retencion.setValor(new BigDecimal(valorRetencion));
        double valor = 0;
        if (retencion.getRetencion() != null) {
            if ((retencion.getValorprima() == null) || (retencion.getValorprima().doubleValue() <= 0)) {
                valor = (retencion.getBaseimponible().doubleValue() + retencion.getBaseimponible0().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            } else {
                valor = (retencion.getValorprima().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            }
        }

        double iva = 0;
        if (retencion.getImpuesto() != null) {
            iva = retencion.getBaseimponible().doubleValue()
                    * retencion.getImpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteIva();
            iva += ajuste / 100;
        }
        double valorIva = 0;
        if (retencion.getRetencionimpuesto() != null) {
            valorIva = iva * retencion.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteRi();
            valorIva += ajuste / 100;
        }
        retencion.setIva(new BigDecimal(iva));
//        retencion.setValor(new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP));

        retencion.setValor(new BigDecimal(valor));
        retencion.setValoriva(new BigDecimal(valorIva));
        return false;
    }

    private boolean validarReembolso() {
        double valor = 0;
        if (reembolso.getComprobante() == null) {
            MensajesErrores.advertencia("Es necesario tipo de comprobante");
            return true;
        }
        if ((reembolso.getEstablecimiento() == null) || (reembolso.getEstablecimiento().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Establecimiento");
            return true;
        }
        if ((reembolso.getPunto() == null) || (reembolso.getPunto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Punto");
            return true;
        }
        if (reembolso.getFechaemision() == null) {
            MensajesErrores.advertencia("Es necesario Punto");
            return true;
        }
        if ((reembolso.getNumero() == null) || (reembolso.getNumero().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Establecimiento");
            return true;
        }
        if ((reembolso.getRuc() == null) || (reembolso.getRuc().isEmpty())) {
            MensajesErrores.advertencia("Es necesario ID proveedor");
            return true;
        }
        if ((reembolso.getBaseimponible() == null) || (reembolso.getBaseimponible().doubleValue() < 0)) {
            MensajesErrores.advertencia("Es necesario Base imponible");
            return true;
        }
        if ((reembolso.getBaseimponible0() == null) || (reembolso.getBaseimponible0().doubleValue() < 0)) {
            MensajesErrores.advertencia("Es necesario Base imponible 0");
            return true;
        }
        if ((reembolso.getValoriva() == null) || (reembolso.getValoriva().doubleValue() < 0)) {
            MensajesErrores.advertencia("Es necesario valor iva");
            return true;
        }
        if ((reembolso.getAutorizacion() == null) || (reembolso.getAutorizacion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario autorización");
            return true;
        }

        return false;
    }

    private boolean validarDescuento() {
        double valor = 0;
        if (descuento.getCuentaContable() == null) {
            MensajesErrores.advertencia("Es necesario cuenta contable");
            return true;
        }
        if (descuento.getCuentaProveedor() == null) {
            MensajesErrores.advertencia("Es necesario cuenta proveedor");
            return true;
        }
        if (descuento.getEsEmpleado() == null) {
            if (entidadesBean.getEntidad() == null) {
                MensajesErrores.advertencia("Es necesario empleado");
                return true;
            }
        }

        if ((descuento.getValor() == null) || (descuento.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Es necesario Valor");
            return true;
        }

        return false;
    }

    public String insertarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        try {
            retencion.setObligacion(obligacion);
            ejbRetencionesCompras.create(retencion, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarRetenciones();
        calculaTotal();
        formularioDetalle.cancelar();
        return null;
    }

    public String insertarReembolso() {
        if (validarReembolso()) {
            return null;
        }
        try {
            reembolso.setObligacion(obligacion);
            ejbReembolsos.create(reembolso, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarReembolsos();
        formularioReembolso.cancelar();
        return null;
    }

    public String insertarDescuento() {
        if (validarDescuento()) {
            return null;
        }
        try {
            if (descuento.getEsEmpleado()) {
                descuento.setAuxiliar(entidadesBean.getEntidad().getPin());
            } else {
                descuento.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
            }
            descuento.setObligacion(obligacion);
            ejbDescuentos.create(descuento, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarDescuentos();
        calculaTotal();
        formularioDescuento.cancelar();
        return null;
    }

    public String grabarDetalle() {
//        if (validarDetalle()) {
//            return null;
//        }
        try {
            ejbRetencionesCompras.edit(retencion, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarRetenciones();
        calculaTotal();
        formularioDetalle.cancelar();
        return null;
    }

    public String grabarReembolso() {
        if (validarReembolso()) {
            return null;
        }
        try {
            ejbReembolsos.edit(reembolso, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarReembolsos();
        formularioReembolso.cancelar();
        return null;
    }

    public String borrarDetalle() {

        try {
            ejbRetencionesCompras.remove(retencion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarRetenciones();
        calculaTotal();
        formularioDetalle.cancelar();
        return null;
    }

    public String borrarReembolsos() {

        try {
            ejbReembolsos.remove(reembolso, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarReembolsos();
        formularioReembolso.cancelar();
        return null;
    }

    public String borrarDescuento() {

        try {
            ejbDescuentos.remove(descuento, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        cargarDescuentos();
        calculaTotal();
        formularioDescuento.cancelar();
        return null;
    }

    /**
     * @return the retencion
     */
    public Retencionescompras getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
    }

    public String getValorPresupuesto() {
        if (listaDetalleCompras == null) {
            return "0.00";
        }
        double valor = 0;
        for (Detallescompras d : listaDetalleCompras) {
            valor += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
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

    public boolean isTieneAnticipo() {
        if (obligacion == null) {
            return false;
        }
        if (obligacion.getContrato() == null) {
            return false;
        }
        Calendar cuando = Calendar.getInstance();
        Calendar periodoActual = Calendar.getInstance();
//        cuando.setTime(obligacion.getContrato().getInicio());
        if (obligacion.getContrato().getFechaanticipo() == null) {
            cuando.setTime(obligacion.getContrato().getInicio());
        } else {
//            cuando.setTime(obligacion.getContrato().getFechaanticipo());
        }
        periodoActual.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anioActual = periodoActual.get(Calendar.YEAR);
        int anioContrato = cuando.get(Calendar.YEAR);
        if (anioContrato < anioActual) {
            // contrato años anteriores
            // buscar la cuenta de anticpo en saldos iniciales
            List<Renglones> lista = traerRenglonesCuentas();
            if (!lista.isEmpty()) {
                return true;
            }
        } else {
            // contrato anio actual
            Map parametros = new HashMap();
            String where = "o.contrato=:contrato and (o.aplicado=false or o.aplicado is null)";

            parametros.put(";where", where);
            parametros.put("contrato", obligacion.getContrato());
            try {
                int total = ejbAnticipos.contar(parametros);
                if (total > 0) {
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }

    public boolean isTieneMultas() {
        if (obligacion == null) {
            return false;
        }
        if (obligacion.getContrato() == null) {
            return false;
        }
        // contrato años anteriores
        // buscar la cuenta de anticpo en saldos iniciales
        // contrato anio actual
        Map parametros = new HashMap();
        String where = "o.contrato=:contrato and  o.aplicado is null";

        parametros.put(";where", where);
        parametros.put("contrato", obligacion.getContrato());
        try {
            int total = ejbInformes.contar(parametros);
            if (total > 0) {
                return true;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private List<Renglones> traerRenglonesCuentas() {
        if (obligacion == null) {
            return null;
        }
        if (obligacion.getProveedor() == null) {
            return null;
        }
        // va el algoritmo de las cuentas
        Map parametros = new HashMap();
        parametros.put(";where", "o.escxp=true");
        try {
            Codigos codigo = codigosBean.traerCodigo("ANTANT", "01");
            if (codigo == null) {
                return null;
            }
//Solo para PetroEcuador
            if (obligacion.getProveedor().getEmpresa().getRuc().equals("1768153530001")) {
                String ctaInterna = codigo.getParametros();
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.tipo.codigo like '0%' "
                        + " and o.auxiliar=:auxiliar and o.cuenta like :cuenta and o.cabecera.fecha =:fecha");
                parametros.put("auxiliar", obligacion.getProveedor().getEmpresa().getRuc());

                parametros.put("cuenta", ctaInterna + "%");
                parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());

                return ejbRenglones.encontarParametros(parametros);
            } else {

                String ctaInterna = codigo.getNombre();
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.tipo.codigo like '0%' "
                        + " and o.auxiliar=:auxiliar and o.cuenta like :cuenta and o.cabecera.fecha =:fecha");
                parametros.put("auxiliar", obligacion.getProveedor().getEmpresa().getRuc());

                parametros.put("cuenta", ctaInterna + "%");
                parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());

                return ejbRenglones.encontarParametros(parametros);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cambiaCertificacion(ValueChangeEvent event) {
        Compromisos c = (Compromisos) event.getNewValue();
        if (c != null) {
            compromiso = c;
            cabeceraDoc.setCompromiso(compromiso);
            cargarDetalleCompras();
        }
    }

    /**
     * @return the renglonesReclasificacion
     */
    public List<AuxiliarCarga> getRenglonesReclasificacion() {
        return renglonesReclasificacion;
    }

    /**
     * @param renglonesReclasificacion the renglonesReclasificacion to set
     */
    public void setRenglonesReclasificacion(List<AuxiliarCarga> renglonesReclasificacion) {
        this.renglonesReclasificacion = renglonesReclasificacion;
    }

    /**
     * @return the renglonesAnticipos
     */
    public List<AuxiliarCarga> getRenglonesAnticipos() {
        return renglonesAnticipos;
    }

    /**
     * @param renglonesAnticipos the renglonesAnticipos to set
     */
    public void setRenglonesAnticipos(List<AuxiliarCarga> renglonesAnticipos) {
        this.renglonesAnticipos = renglonesAnticipos;
    }

    public SelectItem[] getComboContratos() {

        Map parametros = new HashMap();
//        parametros.put(";where", "o.proveedor=:proveedor and o.fcierre is null");

        if (cabeceraDoc.getCompromiso() != null) {
            if (cabeceraDoc.getCompromiso().getContrato() != null) {

                List<Contratos> lista = new LinkedList<>();
                lista.add(cabeceraDoc.getCompromiso().getContrato());
                return Combos.getSelectItems(lista, false);
            }
        }

        return null;
    }

    /**
     * @return the formularioMotivo
     */
    public Formulario getFormularioMotivo() {
        return formularioMotivo;
    }

    /**
     * @param formularioMotivo the formularioMotivo to set
     */
    public void setFormularioMotivo(Formulario formularioMotivo) {
        this.formularioMotivo = formularioMotivo;
    }

    public String editarMotivo() {
        formularioMotivo.insertar();
        return null;
    }

    public void xmlListener(FileEntryEvent e) {
        if (cabeceraDoc.getCompromiso() == null) {
            MensajesErrores.advertencia("Seleccione un compromiso primero");
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
                        xmlStr += sb;
                    }

                    String error = ejbDocElec.cargaFactura(xmlFileFactura, xmlStr, cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc());
                    if (error == null) {
                        MensajesErrores.informacion("Error en la carga de la factura");
                        return;
                    }
                    if (error.equals("OK")) {
                        MensajesErrores.informacion("Factura cargada con éxito");
                    } else {
                        MensajesErrores.fatal(error);
                        return;
                    }
                    //
                } catch (IOException ex) {
                    MensajesErrores.fatal(ex.getMessage() + ":" + ex.getCause());
                    Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } //fin if
        }// fin for
        llenarFacturasElectronicas();
    }

    public void llenarFacturasElectronicas() {
        Map parametros = new HashMap();
        listadoFacturas = new LinkedList<>();
        verFacturas = true;
        // traer los documentos
        String ruc = "";
        if (cabeceraDoc.getCompromiso().getProveedor() != null) {
            ruc = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc();
        } else {
            if (cabeceraDoc.getCompromiso().getBeneficiario() != null) {
                ruc = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getPin();
            }
        }
        parametros.put(";where", "o.cabeccera is null and o.ruc=:ruc");
        parametros.put("ruc", ruc);
        try {
            listadoFacturas = ejbDocElec.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the contabilizado
     */
    public boolean isContabilizado() {
        return contabilizado;
    }

    /**
     * @param contabilizado the contabilizado to set
     */
    public void setContabilizado(boolean contabilizado) {
        this.contabilizado = contabilizado;
    }

    /**
     * @return the listaRetencionesComprasInt
     */
    public List<Retencionescompras> getListaRetencionesComprasInt() {
        return listaRetencionesComprasInt;
    }

    /**
     * @param listaRetencionesComprasInt the listaRetencionesComprasInt to set
     */
    public void setListaRetencionesComprasInt(List<Retencionescompras> listaRetencionesComprasInt) {
        this.listaRetencionesComprasInt = listaRetencionesComprasInt;
    }

    public String sube() {
        int ajuste = factura.getAjuste();
        ajuste++;
        factura.setAjuste(ajuste);
        return null;
    }

    public String baja() {
        int ajuste = factura.getAjuste();
        ajuste--;
        factura.setAjuste(ajuste);
        return null;
    }

    public String subeRi() {
        int ajuste = retencion.getAjusteRi();
        ajuste++;
        retencion.setAjusteRi(ajuste);
        return null;
    }

    public String bajaRi() {
        int ajuste = retencion.getAjusteRi();
        ajuste--;
        retencion.setAjusteRi(ajuste);
        return null;
    }

    public String subeRf() {
        int ajuste = retencion.getAjusteRf();
        ajuste++;
        retencion.setAjusteRf(ajuste);
        return null;
    }

    public String bajaRf() {
        int ajuste = retencion.getAjusteRf();
        ajuste--;
        retencion.setAjusteRf(ajuste);
        return null;
    }

    public String subeIva() {
        int ajuste = retencion.getAjusteIva();
        ajuste++;
        retencion.setAjusteIva(ajuste);
        return null;
    }

    public String bajaIva() {
        int ajuste = retencion.getAjusteIva();
        ajuste--;
        retencion.setAjusteIva(ajuste);
        return null;
    }

    public boolean isSeleccionados() {
        if (listadoFacturas == null) {
            return false;
        }
        if (listadoFacturas.isEmpty()) {
            return false;
        }
        for (Documentoselectronicos d : listadoFacturas) {
            if (d.isSeleccionado()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSeleccionadosBorrar() {
        if (listadoObligaciones == null) {
            return false;
        }
        if (listadoObligaciones.isEmpty()) {
            return false;
        }
        for (Documentoselectronicos d : listadoObligaciones) {
            if (d.isSeleccionado()) {
                return true;
            }
        }
        return false;
    }

    public String lote() {
        if (cabeceraDoc.getCompromiso() == null) {
            MensajesErrores.advertencia("Seleccione un compromiso primero");
            return null;
        }
        if ((cabeceraDoc.getObservaciones() == null) || (cabeceraDoc.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Por favor indique el motivo");
            return null;
        }

        if (!isSeleccionados()) {
            MensajesErrores.advertencia("Seleccione facturas para poder usar esta opción");
            return null;
        }
        retencionLote = new Retencionescompras();
        retencionLote.setBaseimponible(BigDecimal.ZERO);
        retencionLote.setBaseimponible0(BigDecimal.ZERO);
        retencionLote.setIva(BigDecimal.ZERO);
        retencionLote.setValor(BigDecimal.ZERO);
        retencionLote.setValoriva(BigDecimal.ZERO);
        retencionLote.setBien(false);
        retencionLote.setFechaRetencion(cabeceraDoc.getFecha());
        formularioLote.insertar();

        return null;
    }

    public String borrarSeleccionadas() {

        if (listadoObligaciones == null) {
            return null;
        }
        boolean borrado = false;
        try {
            String ru = null;
            for (Documentoselectronicos d : listadoObligaciones) {
                if (d.isSeleccionado()) {
                    Obligaciones obligacionBorrar = d.getObligacion();
                    ru = d.getObligacion().getProveedor().getEmpresa().getRuc();
                    d.setObligacion(null);
                    d.setUtilizada(false);
                    d.setCabeccera(null);
                    if (d.getTipo() == 1) {
                        ejbDocElec.remove(d, seguridadbean.getLogueado().getUserid());
                    } else {
                        ejbDocElec.edit(d, seguridadbean.getLogueado().getUserid());
                    }
                    if (obligacionBorrar != null) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.obligacion=:obligacion");
                        parametros.put("obligacion", obligacionBorrar);
                        List<Pagosvencimientos> lista = ejbPagos.encontarParametros(parametros);
                        for (Pagosvencimientos p : lista) {
                            ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
                        }
                        List<Retencionescompras> listaRetenciones = ejbRetencionesCompras.encontarParametros(parametros);
                        for (Retencionescompras r : listaRetenciones) {
                            ejbRetencionesCompras.remove(r, seguridadbean.getLogueado().getUserid());
                        }
                        ejbObligaciones.remove(obligacionBorrar, seguridadbean.getLogueado().getUserid());
                    }
                    borrado = true;
                }
            }
            if (borrado) {
                Map parametros = new HashMap();
                // traer los documentos
                parametros.put(";where", "o.ruc=:ruc and o.tipo=0 and o.cabeccera is null");
                parametros.put("ruc", ru);

                listadoFacturas = ejbDocElec.encontarParametros(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.cabeccera=:cabecera");
                parametros.put("cabecera", cabeceraDoc);
                listadoObligaciones = ejbDocElec.encontarParametros(parametros);
                MensajesErrores.advertencia("Se borraron correctamente las facturas");
            }
        } catch (BorrarException | GrabarException | ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabaLote() {
        if (puntoEmision == null) {
            MensajesErrores.advertencia("Seleccione un punto de emision");
            return null;
        }
        if (cabeceraDoc.getId() == null) {
            try {
                ejbCabdocelect.create(cabeceraDoc, seguridadbean.getLogueado().getUserid());

            } catch (InsertarException ex) {
                Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
        for (Documentoselectronicos d : listadoFacturas) {
            if (d.isSeleccionado()) {
                try {
                    facturaBean.cargarMarshal(facturaBean.grabarXml("PRUEBA", d.getXml()));
                } catch (JAXBException ex) {
                    facturaBean.cargar(d.getXml());
                }
                try {
                    // crear la obligacion
                    if (d.getBaseimponible() == null) {
                        d.setBaseimponible(BigDecimal.ZERO);
                    }
                    if (d.getBaseimponible0() == null) {
                        d.setBaseimponible0(BigDecimal.ZERO);
                    }
                    if (d.getIva() == null) {
                        d.setIva(BigDecimal.ZERO);
                    }
                    Obligaciones o = new Obligaciones();
                    d.setCabeccera(cabeceraDoc);
                    compromiso = cabeceraDoc.getCompromiso();
                    Empresas e = proveedorBean.taerRuc(d.getRuc());

                    o.setContrato(null);
                    o.setConcepto(cabeceraDoc.getObservaciones());
                    o.setFechaemision(d.getFechaemision());
                    o.setEstado(0); // No se si 1 o que
                    o.setProveedor(e.getProveedores()); // No se si 1 o que
                    o.setFechaingreso(new Date());
                    o.setUsuario(seguridadbean.getLogueado());
//                    traer el numero de retencion de corresponde
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.punto.codigo=:punto and o.documento=:documento");
                    parametros.put("punto", o.getPuntor());
                    parametros.put("documento", ret);
                    documento = null;
                    List<Documentos> documentos = ejbDocumnetos.encontarParametros(parametros);
                    Documentos doc = null;
                    for (Documentos dx : documentos) {
                        doc = dx;
                    }
                    if (doc != null) {
                        Integer numret = doc.getNumeroactual();
                        numret++;
                        o.setNumeror(numret);
                        doc.setNumeroactual(numret);
                        ejbDocumnetos.edit(doc, seguridadbean.getLogueado().getUserid());
                    } else {
                        o.setNumeror(o.getId());
                    }
//                    Map paraemtros = new HashMap();
//                    paraemtros.put(";where", "o.automatica=true");
//                    List<Puntoemision> listaPunto = ejbPuntos.encontarParametros(paraemtros);
//                    for (Puntoemision pe : listaPunto) {
//                        puntoEmision = pe;
//                    }
                    o.setPuntor(puntoEmision.getCodigo());
                    o.setEstablecimientor(puntoEmision.getSucursal().getRuc());
                    o.setFechar(retencionLote.getFechaRetencion());

                    o.setFacturaelectronica(d.getXml());
                    o.setSustento(codigosBean.getSustentoDefault());
                    o.setFechaingreso(new Date());
                    o.setEstablecimiento(facturaBean.getFactura().getInfoTributaria().getEstab());
                    o.setAutorizacion(facturaBean.getFactura().getInfoTributaria().getClaveAcceso());
                    o.setPuntoemision(facturaBean.getFactura().getInfoTributaria().getPtoEmi());
                    o.setDocumento(Integer.parseInt(facturaBean.getFactura().getInfoTributaria().getSecuencial()));
                    o.setTipodocumento(codigosBean.getFacturas());
                    ejbObligaciones.create(o, seguridadbean.getLogueado().getUserid());
                    // grabar ras
                    if (retencionLote.getImpuesto() != null) {
                        d.setIva(new BigDecimal((d.getBaseimponible().doubleValue() * retencionLote.getImpuesto().getPorcentaje().doubleValue() / 100)));
                    }

                    Retencionescompras r = new Retencionescompras();
                    double valor = 0;
                    r.setBaseimponible(d.getBaseimponible());
                    r.setBaseimponible0(d.getBaseimponible0());
                    r.setImpuesto(retencionLote.getImpuesto());
                    r.setPartida(retencionLote.getPartida());
                    r.setRetencion(retencionLote.getRetencion());
                    r.setRetencionimpuesto(retencionLote.getRetencionimpuesto());
                    if (r.getRetencion() != null) {
                        valor = (r.getBaseimponible().doubleValue() + r.getBaseimponible0().doubleValue())
                                * retencionLote.getRetencion().getPorcentaje().doubleValue() / 100;

                    }

                    double iva = 0;
                    if (r.getImpuesto() != null) {
                        iva = r.getBaseimponible().doubleValue()
                                * retencionLote.getImpuesto().getPorcentaje().doubleValue() / 100;

                    }
                    double valorIva = 0;
                    if (r.getRetencionimpuesto() != null) {
                        valorIva = iva * r.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
                    }
                    r.setIva(new BigDecimal(iva));
                    r.setValor(new BigDecimal(valor));
                    r.setValoriva(new BigDecimal(valorIva));
                    r.setObligacion(o);

                    // sumar todos los ras
                    double valorRetIva = r.getValoriva().doubleValue();
                    double valorRetFuente = r.getValor().doubleValue();
                    double valorBase = r.getBaseimponible().doubleValue();
                    double valorBase0 = r.getBaseimponible0().doubleValue();
                    double valorBaseIva = r.getIva().doubleValue();
                    Pagosvencimientos p = new Pagosvencimientos();
                    p.setFecha(new Date());
                    p.setFechapago(new Date());
                    p.setPagado(false);
                    p.setObligacion(o);
                    p.setValor(new BigDecimal((valorBase + valorBaseIva + valorBase0) - (valorRetFuente + valorRetIva)));
                    o.setApagar(new BigDecimal((valorBase + valorBaseIva + valorBase0) - (valorRetFuente + valorRetIva)));

                    // lo mismo para promesas
                    if (p.getId() == null) {
                        p.setObligacion(o);
                        ejbPagos.create(p, seguridadbean.getLogueado().getUserid());
                    } else {
                        pago.setObligacion(o);
                        ejbPagos.edit(p, seguridadbean.getLogueado().getUserid());
                    }
                    d.setObligacion(o);
                    d.setUtilizada(true);
                    d.setValortotal(d.getBaseimponible().add(d.getBaseimponible0().add(d.getIva())));
                    ejbDocElec.edit(d, seguridadbean.getLogueado().getUserid());
                    ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
                    ejbRetencionesCompras.create(r, seguridadbean.getLogueado().getUserid());
                } catch (InsertarException | GrabarException ex) {
                    Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConsultarException ex) {
                    Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        llenarFacturasElectronicas();
        formularioLote.cancelar();

        return null;
    }

    /**
     * @return the formularioLote
     */
    public Formulario getFormularioLote() {
        return formularioLote;
    }

    /**
     * @param formularioLote the formularioLote to set
     */
    public void setFormularioLote(Formulario formularioLote) {
        this.formularioLote = formularioLote;
    }

    /**
     * @return the retencionLote
     */
    public Retencionescompras getRetencionLote() {
        return retencionLote;
    }

    /**
     * @param retencionLote the retencionLote to set
     */
    public void setRetencionLote(Retencionescompras retencionLote) {
        this.retencionLote = retencionLote;
    }

    public String seleccionarTodos() {
        for (Documentoselectronicos d : listadoFacturas) {
            d.setSeleccionado(true);
        }
        return null;
    }

    public String seleccionarTodosBorrar() {
        for (Documentoselectronicos d : listadoObligaciones) {
            d.setSeleccionado(true);
        }
        return null;
    }

    public String quitarTodos() {
        for (Documentoselectronicos d : listadoFacturas) {
            d.setSeleccionado(false);
        }
        return null;
    }

    public String quitarTodosBorrar() {
        for (Documentoselectronicos d : listadoObligaciones) {
            d.setSeleccionado(false);
        }
        return null;
    }

    public String getTotalFacturasSeleccionadas() {
        double retorno = 0;
        for (Documentoselectronicos d : listadoFacturas) {
            if (d.isSeleccionado()) {
                retorno += d.getBaseimponible() == null ? 0 : d.getBaseimponible().doubleValue();
                retorno += d.getBaseimponible0() == null ? 0 : d.getBaseimponible0().doubleValue();
                retorno += d.getIva() == null ? 0 : d.getIva().doubleValue();
            }
        }
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(retorno);
    }

    public int getNumeroFacturasSeleccionadas() {
        int retorno = 0;
        for (Documentoselectronicos d : listadoFacturas) {
            if (d.isSeleccionado()) {
                retorno++;
            }
        }
        return retorno;
    }

    public String getValorTotalObligaciones() {
        verFacturas = true;
        if (cabeceraDoc.getId() == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(traerTotalObligaciones(cabeceraDoc));
    }

    public BigDecimal traerTotalObligaciones(Cabdocelect cab) {

        Map parametros = new HashMap();
        parametros.put(";where", "o.cabeccera=:cabecera");
        parametros.put("cabecera", cab);
        parametros.put(";campo", "o.valortotal");
        try {

            return ejbDocElec.sumarCampo(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal(BigInteger.ZERO);

    }

    public BigDecimal getTotalObligaciones(Cabdocelect cab) {
        return traerTotalObligaciones(cab);
    }

    public SelectItem[] getComboMultas() {
        if (obligacion == null) {
            return null;
        }
        if (obligacion.getContrato() == null) {
            return null;
        }
        // contrato años anteriores
        // buscar la cuenta de anticpo en saldos iniciales
        // contrato anio actual
        Map parametros = new HashMap();
        String where = "o.contrato=:contrato and  o.aplicado is null";

        parametros.put(";where", where);
        parametros.put("contrato", obligacion.getContrato());
        try {
            return Combos.getSelectItems(ejbInformes.encontarParametros(parametros), true);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the renglonesMultas
     */
    public List<AuxiliarCarga> getRenglonesMultas() {
        return renglonesMultas;
    }

    /**
     * @param renglonesMultas the renglonesMultas to set
     */
    public void setRenglonesMultas(List<AuxiliarCarga> renglonesMultas) {
        this.renglonesMultas = renglonesMultas;
    }

    /**
     * @return the reembolsos
     */
    public List<Reembolsos> getReembolsos() {
        return reembolsos;
    }

    /**
     * @param reembolsos the reembolsos to set
     */
    public void setReembolsos(List<Reembolsos> reembolsos) {
        this.reembolsos = reembolsos;
    }

    /**
     * @return the reembolso
     */
    public Reembolsos getReembolso() {
        return reembolso;
    }

    /**
     * @param reembolso the reembolso to set
     */
    public void setReembolso(Reembolsos reembolso) {
        this.reembolso = reembolso;
    }

    /**
     * @return the formularioReembolso
     */
    public Formulario getFormularioReembolso() {
        return formularioReembolso;
    }

    /**
     * @param formularioReembolso the formularioReembolso to set
     */
    public void setFormularioReembolso(Formulario formularioReembolso) {
        this.formularioReembolso = formularioReembolso;
    }

    /**
     * @return the listaDescuentos
     */
    public List<Descuentoscompras> getListaDescuentos() {
        return listaDescuentos;
    }

    /**
     * @param listaDescuentos the listaDescuentos to set
     */
    public void setListaDescuentos(List<Descuentoscompras> listaDescuentos) {
        this.listaDescuentos = listaDescuentos;
    }

    /**
     * @return the descuento
     */
    public Descuentoscompras getDescuento() {
        return descuento;
    }

    /**
     * @param descuento the descuento to set
     */
    public void setDescuento(Descuentoscompras descuento) {
        this.descuento = descuento;
    }

    /**
     * @return the formularioDescuento
     */
    public Formulario getFormularioDescuento() {
        return formularioDescuento;
    }

    /**
     * @param formularioDescuento the formularioDescuento to set
     */
    public void setFormularioDescuento(Formulario formularioDescuento) {
        this.formularioDescuento = formularioDescuento;
    }

    public SelectItem[] getComboCuentasProveedor() {

        List<Cuentas> listaCuentas = new LinkedList<>();
        for (Detallescompras d : listaDetalleCompras) {
            Formatos f = ejbObligaciones.traerFormato();
            if (f != null) {
                String xx = f.getFormato().replace(".", "#");
                String[] aux = xx.split("#");
                int tamano = aux[f.getNivel() - 1].length();
                String presupuesto = d.getDetallecompromiso().
                        getAsignacion().getClasificador().getCodigo();
                presupuesto = presupuesto.substring(0, tamano);
                String cuentaProveedorInt = f.getCxpinicio() + presupuesto + f.getCxpfin();
                armarCuentaProveedor(cuentasBean.traerCodigo(cuentaProveedorInt), listaCuentas);
            }
        }
        List<Codigos> lista = codigosBean.getListaCuetasFondosRendir();
        Map parametros = new HashMap();
        String where = "";
        for (Codigos c : lista) {
            if (!where.isEmpty()) {
                where += ",";
            }
            where += "'" + c.getDescripcion() + "'";
        }
        if (where.isEmpty()) {
            return Combos.getSelectItems(listaCuentas, false);
        }

        where = " o.codigo in (" + where + ")";
        parametros.put(";where", where);
        parametros.put(";orden", "o.codigo");
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            for (Cuentas c : cl) {
                listaCuentas.add(c);
            }
//            return Combos.getSelectItems(cl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(listaCuentas, false);
    }

//    public SelectItem[] getComboCuentasDetalle() {
//
//        // solo para quitar el error
//        List<Codigos> lista = codigosBean.getListaCuetasCobrar();
//        Map parametros = new HashMap();
//        String where = "";
//        for (Codigos c : lista) {
//            if (!where.isEmpty()) {
//                where += ",";
//            }
//            where += "'" + c.getDescripcion() + "'";
//        }
//        if (where.isEmpty()) {
//            return null;
//        }
//
//        where = " o.codigo in (" + where + ")";
//        parametros.put(";where", where);
//        parametros.put(";orden", "o.codigo");
//        try {
//            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
//            return Combos.getSelectItems(cl, false);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
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
            // buscar el resto de cuentas que tiene los ingresos
            //

            if (cabeceraDoc.getCompromiso().getProveedor() != null) {
                String ruc = cabeceraDoc.getCompromiso().getProveedor().getEmpresa().getRuc();
                parametros = new HashMap();
                parametros.put(";where", "o.multa is null and o.cliente.empresa.ruc=:ruc and o.valoraprobar is not null");
                parametros.put(";campo", "o.cuenta");
                parametros.put(";suma", "sum(o.valoraprobar)");
                parametros.put("ruc", ruc);
                List<Object[]> listaObjetos = ejbIngresos.sumar(parametros);
                for (Object[] o : listaObjetos) {
                    String cta = (String) o[0];
                    Cuentas ctaNueva = cuentasBean.traerCodigo(cta);
                    if (ctaNueva != null) {
                        cl.add(ctaNueva);
                    }
                }
            }
            return Combos.getSelectItems(cl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String getUsurioRealizo() {
        String retorno = "";
        Cabdocelect cab = (Cabdocelect) cabecerasDocumentos.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabeccera=:cabecera");
        parametros.put("cabecera", cab);
        try {
            List<Documentoselectronicos> listaDoc = ejbDocElec.encontarParametros(parametros);

            for (Documentoselectronicos d : listaDoc) {
//               if (retorno.isEmpty()){
//                   retorno+=", ";
//               }
                retorno += "[" + d.getObligacion().getUsuario().getUserid() + "]";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
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

    /**
     * @return the contRetenciones
     */
    public int getContRetenciones() {
        return contRetenciones;
    }

    /**
     * @param contRetenciones the contRetenciones to set
     */
    public void setContRetenciones(int contRetenciones) {
        this.contRetenciones = contRetenciones;
    }

    /**
     * @return the formularioReclasificacion
     */
    public Formulario getFormularioReclasificacion() {
        return formularioReclasificacion;
    }

    /**
     * @param formularioReclasificacion the formularioReclasificacion to set
     */
    public void setFormularioReclasificacion(Formulario formularioReclasificacion) {
        this.formularioReclasificacion = formularioReclasificacion;
    }

    /**
     * @return the renglonesDescuento
     */
    public List<AuxiliarCarga> getRenglonesDescuento() {
        return renglonesDescuento;
    }

    /**
     * @param renglonesDescuento the renglonesDescuento to set
     */
    public void setRenglonesDescuento(List<AuxiliarCarga> renglonesDescuento) {
        this.renglonesDescuento = renglonesDescuento;
    }

    /**
     * @return the fechaEmision
     */
    public Date getFechaEmision() {
        return fechaEmision;
    }

    /**
     * @param fechaEmision the fechaEmision to set
     */
    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    /**
     * @return the formularioFechaEmision
     */
    public Formulario getFormularioFechaEmision() {
        return formularioFechaEmision;
    }

    /**
     * @param formularioFechaEmision the formularioFechaEmision to set
     */
    public void setFormularioFechaEmision(Formulario formularioFechaEmision) {
        this.formularioFechaEmision = formularioFechaEmision;
    }

    public String cambiarFechaEmision() {
        if (!seguridadbean.getLogueado().getUserid().equals("wtipan")) {
            MensajesErrores.advertencia("No tiene autorización para ocupar esta opción");
            return null;
        }
        fechaEmision = new Date();
        formularioFechaEmision.insertar();
        return null;
    }

    public String cancelaFechaEmision() {

        fechaEmision = null;
        formularioFechaEmision.cancelar();
        return null;
    }

    public String anular(Cabdocelect cabeceraDoc) {
        this.cabeceraDoc = cabeceraDoc;
        if (!cabeceraDoc.getContabilizado()) {
            MensajesErrores.advertencia("Pago por Lote no está contabilizado");
            return null;
        }
        if (cabeceraDoc.getAnulado() != null) {
            MensajesErrores.advertencia("Pago por Lote ya anulado");
            return null;
        }
        formularioanular.insertar();
        return null;
    }

    public String grabarAnular() {
        if (!cabeceraDoc.getContabilizado()) {
            MensajesErrores.advertencia("Pago por Lote no está contabilizado");
            return null;
        }
        if (cabeceraDoc.getAnulado() != null) {
            MensajesErrores.advertencia("Pago por Lote ya anulado");
            return null;
        }
        if (fechaAnular == null) {
            MensajesErrores.advertencia("Ingrese una fecha de anulación");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion like 'OBLIGACIONES_%'");
            parametros.put("idmodulo", cabeceraDoc.getId());
            List<Cabeceras> lista = ejbCabecera.encontarParametros(parametros);
            for (Cabeceras c : lista) {
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera=:cabecera");
                parametros.put("cabecera", c);
                List<Renglones> listaR = ejbRenglones.encontarParametros(parametros);
                Cabeceras cAnulada = new Cabeceras();
                cAnulada.setFecha(c.getFecha());
                cAnulada.setNumero(c.getNumero());
                cAnulada.setDescripcion("Anulación # " + c + " " + c.getDescripcion());
                cAnulada.setUsuario(seguridadbean.getLogueado().getUserid());
                cAnulada.setDia(new Date());
                cAnulada.setModulo(c.getModulo());
                cAnulada.setIdmodulo(c.getIdmodulo());
                cAnulada.setOpcion(c.getOpcion());
                cAnulada.setTipo(c.getTipo());
                ejbCabecera.create(cAnulada, seguridadbean.getLogueado().getUserid());
                for (Renglones r : listaR) {
                    Renglones rAnular = new Renglones();
                    rAnular.setCuenta(r.getCuenta());
                    rAnular.setReferencia("Anulación # " + c + " " + r.getReferencia());
                    rAnular.setValor(r.getValor());
                    rAnular.setCabecera(cAnulada);
                    rAnular.setCentrocosto(r.getCentrocosto());
                    rAnular.setAuxiliar(r.getAuxiliar());
                    rAnular.setPresupuesto(r.getPresupuesto());
                    rAnular.setFecha(r.getFecha());
                    rAnular.setConciliacion(r.getConciliacion());
                    rAnular.setDetallecompromiso(r.getDetallecompromiso());
                    rAnular.setSigno(-1);
                    rAnular.setFuente(r.getFuente());
                    ejbRenglones.create(rAnular, seguridadbean.getLogueado().getUserid());
                }
            }
            cabeceraDoc.setAnulado(fechaAnular);
            ejbCabdocelect.edit(cabeceraDoc, seguridadbean.getLogueado().getUserid());
            parametros = new HashMap();
            parametros.put(";where", "o.cabeceradoc=:cabeceradoc");
            parametros.put("cabeceradoc", cabeceraDoc);
            List<Detallescompras> listaDC = ejbDetallesCompras.encontarParametros(parametros);
            for (Detallescompras dc : listaDC) {
                if (dc.getValor().doubleValue() != 0) {
                    double nuevoSaldo = dc.getDetallecompromiso().getSaldo().doubleValue() + dc.getValor().doubleValue();
                    dc.getDetallecompromiso().setSaldo(new BigDecimal(nuevoSaldo));
                    ejbDetalleCompromiso.edit(dc.getDetallecompromiso(), seguridadbean.getLogueado().getUserid());
                    ejbDetallesCompras.remove(dc, seguridadbean.getLogueado().getUserid());
                }
            }

            parametros = new HashMap();
            parametros.put(";where", "o.cabeccera=:cabeceraDoc");
            parametros.put("cabeceraDoc", cabeceraDoc);
            List<Documentoselectronicos> listaDoc = ejbDocElec.encontarParametros(parametros);
            for (Documentoselectronicos d : listaDoc) {
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", d.getObligacion());
                List<Pagosvencimientos> pagos = ejbPagos.encontarParametros(parametros);
                List<Retencionescompras> detallesCompras = ejbRetencionesCompras.encontarParametros(parametros);
                for (Pagosvencimientos p : pagos) {
                    ejbPagos.remove(p, seguridadbean.getLogueado().getUserid());
                }
                //Se anula solo la obligacion
//                for (Retencionescompras dc : detallesCompras) {
//                    ejbRetencionesCompras.remove(dc, seguridadbean.getLogueado().getUserid());
//                }
                PagosExterior pagExt = null;
                if (d.getObligacion() != null) {
                    if (d.getObligacion().getPagoExterior() == null) {
                    } else {
                        pagExt = d.getObligacion().getPagoExterior();
                    }

                    d.getObligacion().setEstado(-1);
                    ejbObligaciones.edit(d.getObligacion(), seguridadbean.getLogueado().getUserid());
                }
                if (d.getTipo() == 1) {
                    ejbDocElec.remove(d, seguridadbean.getLogueado().getUserid());
                } else {
                    d.setUtilizada(false);
                    d.setCabeccera(null);
                    d.setObligacion(null);
                    ejbDocElec.edit(d, seguridadbean.getLogueado().getUserid());
                }
                if (pagExt != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.exterior=:exterior");
                    parametros.put("exterior", pagExt);
                    List<Fondos> listaGrabar = ejbFondos.encontarParametros(parametros);
                    if (!listaGrabar.isEmpty()) {
                        for (Fondos f : listaGrabar) {
                            f.setCerrado(Boolean.FALSE);
                            f.setExterior(null);
                            ejbFondos.edit(f, seguridadbean.getLogueado().getUserid());
                        }
                    }
                    ejbPagosExterior.remove(pagExt, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (ConsultarException | InsertarException | GrabarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir(cabeceraDoc);
        buscar();
        formularioanular.cancelar();
        return null;
    }

    /**
     * @return the formularioanular
     */
    public Formulario getFormularioanular() {
        return formularioanular;
    }

    /**
     * @param formularioanular the formularioanular to set
     */
    public void setFormularioanular(Formulario formularioanular) {
        this.formularioanular = formularioanular;
    }

    /**
     * @return the fechaAnular
     */
    public Date getFechaAnular() {
        return fechaAnular;
    }

    /**
     * @param fechaAnular the fechaAnular to set
     */
    public void setFechaAnular(Date fechaAnular) {
        this.fechaAnular = fechaAnular;
    }

    /**
     * @return the formularioCruceFondos
     */
    public Formulario getFormularioCruceFondos() {
        return formularioCruceFondos;
    }

    /**
     * @param formularioCruceFondos the formularioCruceFondos to set
     */
    public void setFormularioCruceFondos(Formulario formularioCruceFondos) {
        this.formularioCruceFondos = formularioCruceFondos;
    }

    /**
     * @return the listaFondos
     */
    public List<Fondos> getListaFondos() {
        return listaFondos;
    }

    /**
     * @param listaFondos the listaFondos to set
     */
    public void setListaFondos(List<Fondos> listaFondos) {
        this.listaFondos = listaFondos;
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
     * @return the listaFondosGrabar
     */
    public List<Fondos> getListaFondosGrabar() {
        return listaFondosGrabar;
    }

    /**
     * @param listaFondosGrabar the listaFondosGrabar to set
     */
    public void setListaFondosGrabar(List<Fondos> listaFondosGrabar) {
        this.listaFondosGrabar = listaFondosGrabar;
    }

    /**
     * @return the renglonesExteriorLocal
     */
    public List<Renglones> getRenglonesExteriorLocal() {
        return renglonesExteriorLocal;
    }

    /**
     * @param renglonesExteriorLocal the renglonesExteriorLocal to set
     */
    public void setRenglonesExteriorLocal(List<Renglones> renglonesExteriorLocal) {
        this.renglonesExteriorLocal = renglonesExteriorLocal;
    }

    /**
     * @return the renglonesExterior
     */
    public List<AuxiliarCarga> getRenglonesExterior() {
        return renglonesExterior;
    }

    /**
     * @param renglonesExterior the renglonesExterior to set
     */
    public void setRenglonesExterior(List<AuxiliarCarga> renglonesExterior) {
        this.renglonesExterior = renglonesExterior;
    }
}
