/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.DocumentosEmisionBean;
import org.contabilidad.sfccbdmq.RetencionesBean;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Documentos;
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
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.CompromisosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "fondoRotativoSfccbdmq")
@ViewScoped
public class FondoRotativoBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public FondoRotativoBean() {
        Calendar c = Calendar.getInstance();

        compromisos = new LazyDataModel<Compromisos>() {

            @Override
            public List<Compromisos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Compromisos> compromisos;
    private List<Obligaciones> obligaciones;
    private Obligaciones obligacion = new Obligaciones();
    private List<Rascompras> detalles;
    private List<Rascompras> detallesb;
    private List<Rascompras> detallesInternos;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private List<Detallecompromiso> detalleCompromiso;
    private Detallecompromiso detCompromiso;
    private Rascompras detalle;
    private Rascompras detalleModificar;
    private Compromisos compromiso;
    private Autorizaciones autorizacion;
    private Documentos documeto;
    private Cuentas cuentaproveedor;
    private List<AuxiliarCarga> totales;
    private Puntoemision puntoEmision;
    private boolean proveedorBeneficiario;
    private boolean soloRetencion;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioEditar = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioDetalleCuenta = new Formulario();
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RascomprasFacade ejbRasCompras;
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
    private RenglonesFacade ejbRenglones;
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
        String nombreForma = "FondoRotativoVista";
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
        compromisos = new LazyDataModel<Compromisos>() {
            @Override
            public List<Compromisos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha asc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                if (numero == null) {
                    String where = "o.fecha between :desde and :hasta and o.impresion is not null and o.proveedor is null and (o.nomina is null or o.nomina=false)";
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }

                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    if (!((motivo == null) || (motivo.isEmpty()))) {
                        where += " and upper(o.motivo) like :motivo";
                        parametros.put("motivo", motivo + "%");
                    }

                    int total = 0;
                    try {
                        parametros.put(";where", where);
                        total = ejbCompromisos.contar(parametros);
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
                    compromisos.setRowCount(total);
                    try {
                        return ejbCompromisos.encontarParametros(parametros);
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                        Logger.getLogger("").log(Level.SEVERE, null, ex);
                    }
                } else {
                    String where = "  o.id=:numero and o.impresion is not null and o.proveedor is null and (o.nomina is null or o.nomina=false)";
                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        String valor = (String) e.getValue();

                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                    parametros.put("numero", numero);
                    int total = 0;
                    try {
                        parametros.put(";where", where);
                        total = ejbCompromisos.contar(parametros);
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
                    compromisos.setRowCount(total);
                    try {
                        List<Compromisos> lf = ejbCompromisos.encontarParametros(parametros);
                        return lf;
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                        Logger.getLogger("").log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        };

        return null;
    }

//    private void esta(Detallecompromiso d) {
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
//        o.setCompromiso(compromiso);
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

    public SelectItem[] getComboProveedores() {
        if (detalleCompromiso == null) {
            return null;
        }
        List<Proveedores> pl = new LinkedList<>();
        for (Detallecompromiso d : detalleCompromiso) {
            Proveedores p = traeDetalle(d.getProveedor(), pl);
            if (p != null) {
                pl.add(p);
            }
        }
        if (pl.isEmpty()) {
            return null;
        }
        int size = pl.size() + 1;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        items[0] = new SelectItem(0, "--- Seleccione uno ---");
        i++;
        for (Proveedores x : pl) {
            items[i++] = new SelectItem(x, x.getEmpresa().toString());
        }
        return items;
    }

    public String modificar(Compromisos compromiso) {
        this.compromiso = compromiso;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        try {
            obligaciones = ejbObligaciones.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            detalleCompromiso = ejbDetallecompromiso.encontarParametros(parametros);
//            for (Detallecompromiso d : detalleCompromiso) {
//                esta(d);
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioObligacion.insertar();
        formulario.editar();
        return null;
    }

    public String imprimir(Compromisos compromiso) {
        this.compromiso = compromiso;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        try {
            obligaciones = ejbObligaciones.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            detalleCompromiso = ejbDetallecompromiso.encontarParametros(parametros);
//            for (Detallecompromiso d : detalleCompromiso) {
//                esta(d);
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRenglones();
        formularioImpresion.insertar();
//        formulario.editar();
        return null;
    }

    public String contabilizar(Compromisos compromiso) {
        this.compromiso = compromiso;
        Map parametros = new HashMap();
        parametros.put(";where", "o.detallecompromiso.compromiso.compromiso=:compromiso");
        parametros.put("compromiso", compromiso);
        try {
            detalles = ejbRasCompras.encontarParametros(parametros);
            double valorDetalles = 0;
            for (Rascompras r : detalles) {
                valorDetalles = +r.getValor().doubleValue() + r.getValorimpuesto().doubleValue();
            }
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            detalleCompromiso = ejbDetallecompromiso.encontarParametros(parametros);
            double valorCompromiso = 0;
            for (Detallecompromiso d : detalleCompromiso) {
                valorCompromiso += d.getValor().doubleValue();
            }
            double total = valorDetalles - valorCompromiso;
            if (Math.round(total) != 0) {
                MensajesErrores.advertencia("No es posible contabilizar Compromiso todavia no genera todas las obligaciones");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioObligacion.cancelar();
        formulario.editar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
    }

    public String modificarObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;

//        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            setDetalles(ejbRasCompras.encontarParametros(parametros));
            compromiso = obligacion.getCompromiso();
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
            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.editar();
        calculaTotal();
        return null;
    }

    public String borraObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;

//        getFormulario().editar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", obligacion);
        try {
            setDetalles(ejbRasCompras.encontarParametros(parametros));
            compromiso = obligacion.getCompromiso();
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
            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        formulario.cancelar();
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.eliminar();
        calculaTotal();
        return null;
    }

    public String nuevaObligacion() {
//        this.obligacion = obligacion;
        obligacion = new Obligaciones();
//        obligacion.setProveedor(compromiso.getProveedor());
        obligacion.setContrato(null);
        obligacion.setFechaemision(new Date());
        obligacion.setEstado(1); // No se si 1 o que
        obligacion.setFechaingreso(new Date());
//        obligacion.setApagar(d.getValor());
        obligacion.setCompromiso(compromiso);
        obligacion.setConcepto(compromiso.getMotivo());
//        obligacion.setConcepto(compromiso.getMotivo() + " - " + obligacion.getProveedor().getEmpresa().toString());
        detalles = new LinkedList<>();
        facturaBean.setFactura(null);
        proveedorBean.setEmpresa(null);
        proveedorBean.setProveedor(null);
//        formulario.cancelar();
        detallesb = null;
        formularioObligacion.cancelar();
        formularioEditar.editar();
        obligacion.setSustento(codigosBean.getSustentoDefault());
//        calculaTotal();
        return null;
    }

    public String eliminar(Compromisos compromiso) {
        this.compromiso = compromiso;

        if (compromiso.getFechareposicion() != null) {
            MensajesErrores.advertencia("Liquidación ya definicitiva  no es posible eliminar");
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
     * @return the compromisos
     */
    public LazyDataModel<Compromisos> getCompromisos() {
        return compromisos;
    }

    /**
     * @param compromisos the compromisos to set
     */
    public void setCompromisos(LazyDataModel<Compromisos> compromisos) {
        this.compromisos = compromisos;
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
     * @return the detalleCompromiso
     */
    public List<Detallecompromiso> getDetalleCompromiso() {
        return detalleCompromiso;
    }

    /**
     * @param detalleCompromiso the detalleCompromiso to set
     */
    public void setDetalleCompromiso(List<Detallecompromiso> detalleCompromiso) {
        this.detalleCompromiso = detalleCompromiso;
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

    /**
     * @return the detCompromiso
     */
    public Detallecompromiso getDetCompromiso() {
        return detCompromiso;
    }

    /**
     * @param detCompromiso the detCompromiso to set
     */
    public void setDetCompromiso(Detallecompromiso detCompromiso) {
        this.detCompromiso = detCompromiso;
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
//        detalle.setDetallecompromiso(detCompromiso);
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
                    String presupuesto = r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                    presupuesto = presupuesto.substring(0, tamano);
//            totalObligacion += r.getValor().doubleValue();
                    String cuentaProveedorInt = f.getCxpinicio() + presupuesto + f.getCxpfin();
                    armarCuentaProveedor(cuentasBean.traerCodigo(cuentaProveedorInt), listaCuentas);
                }
            }
        }
        // solo para quitar el error

        return Combos.getSelectItems(listaCuentas, false);
    }

//    public String nuevoDetalleCuenta() {
//        detalle = new Rascompras();
//        detalle.setBien(Boolean.FALSE);
////        formularioCompromiso.insertar();
//        getFormularioDetalleCuenta().insertar();
//        detalleModificar = new Rascompras();
//        detalleModificar.setValor(BigDecimal.ZERO);
//        cuentaproveedor = null;
//        iva = false;
//        return null;
//    }
//    public String modificaDetalle() {
//        formularioDetalle.setIndiceFila(formularioDetalle.getFila().getRowIndex());
//        detalle = detalles.get(formularioDetalle.getFila().getRowIndex());
//        iva = false;
//        double valorComparar = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
//        detalleModificar = new Rascompras();
//        detalleModificar.setValor(new BigDecimal(valorComparar));
//        if (detalle.getDetallecompromiso() == null) {
//            formularioDetalleCuenta.editar();
//            // ver el tipo
//            if (detalle.getCba().equals("E")) {
//                empleado = true;
//                Entidades e = entidadesBean.traerCedula(detalle.getCc());
//                if (e != null) {
//                    entidadesBean.setApellidos(e.getApellidos());
//                    entidadesBean.setEntidad(e);
//                }
//            } else {
//                empleado = false;
//            }
////            cuenta del proveedor
//            cuentaproveedor = getCuentasBean().traerCodigo(detalle.getCuenta());
//        } else {
//            formularioDetalle.editar();
//        }
//
//        return null;
//    }
//
//    public String eliminaDetalle() {
//        if (detallesb == null) {
//            detallesb = new LinkedList<>();
//        }
//        formularioDetalle.setIndiceFila(formularioDetalle.getFila().getRowIndex());
//        detalle = detalles.get(formularioDetalle.getFila().getRowIndex());
//        if (detalle.getCba().equals("A")) {
//            detallesb.add(detalle);
//            detalles.remove(formularioDetalle.getFila().getRowIndex());
//            return null;
//        }
//        int i = 0;
//        double valorComparar = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
//        detalleModificar = new Rascompras();
//        detalleModificar.setValor(new BigDecimal(valorComparar));
//        if (detalle.getDetallecompromiso() == null) {
//            formularioDetalleCuenta.eliminar();
//            if (detalle.getCba().equals("E")) {
//                setEmpleado((Boolean) true);
//                Entidades e = entidadesBean.traerCedula(detalle.getCuenta());
//                if (e != null) {
//                    entidadesBean.setApellidos(e.getApellidos());
//                    entidadesBean.setEntidad(e);
//                }
//            } else {
//                setEmpleado((Boolean) false);
//            }
//        } else {
//            formularioDetalle.eliminar();
//        }
//
//        return null;
//    }
//
//    public boolean validarDetalleCuenta() {
//
//        if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Valor obligatorio");
//            return true;
//        }
//        if ((detalle.getReferencia() == null) || (detalle.getReferencia().isEmpty())) {
//            MensajesErrores.advertencia("Referencia es  obligatorio");
//            return true;
//        }
//        if ((detalle.getCuenta() == null) || (detalle.getCuenta().isEmpty())) {
//            MensajesErrores.advertencia("Auxiliar es obligatorio");
//            return true;
//        }
//        if (detalle.getIdcuenta() == null) {
//            MensajesErrores.advertencia("Cuenta es obligatorio");
//            return true;
//        }
//        return false;
//    }
//
//    public boolean validarDetalle() {
//        if ((detalle.getDetallecompromiso() == null)) {
//            MensajesErrores.advertencia("Seleccione una cuenta de presupuesto");
//            return true;
//        }
//        if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Valor obligatorio");
//            return true;
//        }
//        if ((detalle.getReferencia() == null) || (detalle.getReferencia().isEmpty())) {
//            MensajesErrores.advertencia("Referencia es  obligatorio");
//            return true;
//        }
//        double valorCompromiso = detalle.getDetallecompromiso().getValor().doubleValue();
//        double valorSaldo = detalle.getDetallecompromiso().getSaldo().doubleValue();
//        double valorComparar = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
//        double valorAnterior = detalleModificar.getValor().doubleValue();
//        double total = valorSaldo - valorComparar + valorAnterior;
//        if (Math.round(total) < 0) {
//            DecimalFormat df = new DecimalFormat("###,###,##0.00");
//            String enMensaje = " Saldo Compromiso = " + df.format(valorSaldo);
//            enMensaje += " Valor renglón = " + df.format(valorComparar);
//            enMensaje += " Diferencia = " + df.format(Math.round(total));
//            MensajesErrores.advertencia("Valor sobrepasa el compromiso :" + enMensaje);
//            return true;
//        }
//        return false;
//    }
//
//    public String insertarDetalle() {
//        if (detalles == null) {
//            detalles = new LinkedList<>();
//        }
//        detalle.setCba("C");
//        detalle.setCuenta(detalle.getIdcuenta().getCodigo());
//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
//        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
//
//        if (getIva()) {
//            if (detalle.getImpuesto() != null) {
//                double divide = 1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100);
//                double valor = detalle.getValor().doubleValue() / divide;
//                detalle.setValor(new BigDecimal(valor));
//            }
//        }
//        if (validarDetalle()) {
//            return null;
//        }
//        if (detalle.getRetencion() != null) {
//            detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
//                    * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
//        } else {
//            detalle.setValorret(BigDecimal.ZERO);
//        }
//        if (detalle.getImpuesto() != null) {
//            double impuestoDoble = Math.round(detalle.getValor().doubleValue() * detalle.getImpuesto().getPorcentaje().doubleValue());
//            detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
//            if (detalle.getRetimpuesto() != null) {
//                double retimp = Math.round(impuestoDoble / 100
//                        * detalle.getRetimpuesto().getPorcentaje().doubleValue());
//                detalle.setVretimpuesto(new BigDecimal(retimp / 100));
//            }
//        } else {
//            detalle.setValorimpuesto(BigDecimal.ZERO);
//            detalle.setVretimpuesto(BigDecimal.ZERO);
//            detalle.setRetimpuesto(null);
//        }
//        // grabar obligacion y detalle
//        obligacion.setFechaingreso(new Date());
//        try {
//            if (obligacion.getId() == null) {
//                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
//            }
//            detalle.setObligacion(obligacion);
//            ejbRasCompras.create(detalle, seguridadbean.getLogueado().getUserid());
//            Detallecompromiso det = detalle.getDetallecompromiso();
//            double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
//            det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() - valor));
//            ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
//            detalles.add(detalle);
//        } catch (InsertarException | GrabarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioDetalle.cancelar();
//        calculaTotal();
//        return null;
//    }
//
//    public String insertarDetalleCuenta() {
//        if (detalles == null) {
//            detalles = new LinkedList<>();
//        }
//        if (cuentaproveedor == null) {
//            MensajesErrores.advertencia("Seleccione una cuenta del proveedor");
//            return null;
//        }
//        detalle.setCuenta(cuentaproveedor.getCodigo());
//        if (isEmpleado()) {
//            detalle.setCba("E");
//            detalle.setCc(entidadesBean.getEntidad().getPin());
//        } else {
//            detalle.setCba("P");
//            detalle.setCc(obligacion.getProveedor().getEmpresa().getRuc());
//        }
//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
//        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
//        if (iva) {
//            if (detalle.getImpuesto() != null) {
//                double valor = detalle.getValor().doubleValue() / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
//                detalle.setValor(new BigDecimal(valor));
//            }
//        }
//        if (detalle.getRetencion() != null) {
//            detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
//                    * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
//        } else {
//            detalle.setValorret(BigDecimal.ZERO);
//        }
//        if (detalle.getImpuesto() != null) {
//            double impuestoDoble = Math.round(detalle.getValor().doubleValue() * detalle.getImpuesto().getPorcentaje().doubleValue());
//            detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
//            if (detalle.getRetimpuesto() != null) {
//                double retimp = Math.round(impuestoDoble / 100
//                        * detalle.getRetimpuesto().getPorcentaje().doubleValue());
//                detalle.setVretimpuesto(new BigDecimal(retimp / 100));
//            }
//        } else {
//            detalle.setValorimpuesto(BigDecimal.ZERO);
//            detalle.setVretimpuesto(BigDecimal.ZERO);
//            detalle.setRetimpuesto(null);
//        }
//        if (validarDetalleCuenta()) {
//            return null;
//        }
//        obligacion.setFechaingreso(new Date());
//        try {
//            if (obligacion.getId() == null) {
//                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
//            }
//            detalle.setObligacion(obligacion);
//            ejbRasCompras.create(detalle, seguridadbean.getLogueado().getUserid());
//            detalles.add(detalle);
//        } catch (InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//
//        formularioDetalleCuenta.cancelar();
//        calculaTotal();
//        return null;
//    }
//
//    public String grabarDetalle() {
//
//        detalle.setCba("C");
//        detalle.setCuenta(detalle.getIdcuenta().getCodigo());
//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
//        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
//        if (getIva()) {
//            if (detalle.getImpuesto() != null) {
//                double valor = detalle.getValor().doubleValue() / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
//                detalle.setValor(new BigDecimal(valor));
//            }
//        }
//        if (detalle.getRetencion() != null) {
//            detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
//                    * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
//        } else {
//            detalle.setValorret(BigDecimal.ZERO);
//        }
//        if (detalle.getImpuesto() != null) {
//            double impuestoDoble = Math.round(detalle.getValor().doubleValue() * detalle.getImpuesto().getPorcentaje().doubleValue());
//            detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
//            if (detalle.getRetimpuesto() != null) {
//                double retimp = Math.round(impuestoDoble / 100
//                        * detalle.getRetimpuesto().getPorcentaje().doubleValue());
//                detalle.setVretimpuesto(new BigDecimal(retimp / 100));
//            }
//        } else {
//            detalle.setValorimpuesto(BigDecimal.ZERO);
//            detalle.setVretimpuesto(BigDecimal.ZERO);
//            detalle.setRetimpuesto(null);
//        }
//        if (validarDetalle()) {
//            return null;
//        }
//        try {
//
//            ejbRasCompras.edit(detalle, seguridadbean.getLogueado().getUserid());
//            Detallecompromiso det = detalle.getDetallecompromiso();
//            double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
//            double anterior = detalleModificar.getValor().doubleValue();
//            det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() - valor + anterior));
//            ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
//        } catch (GrabarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        detalles.set(formularioDetalle.getIndiceFila(), detalle);
//        formularioDetalle.cancelar();
//        calculaTotal();
//        return null;
//    }
//
//    public String grabarDetalleCuenta() {
//        if (cuentaproveedor == null) {
//            MensajesErrores.advertencia("Seleccione una cuenta del proveedor");
//            return null;
//        }
//        detalle.setCuenta(cuentaproveedor.getCodigo());
//        if (isEmpleado()) {
//            detalle.setCba("E");
//            detalle.setCc(entidadesBean.getEntidad().getPin());
//        } else {
//            detalle.setCba("P");
//            detalle.setCc(obligacion.getProveedor().getEmpresa().getRuc());
//        }
//        detalle.setValorret(new BigDecimal(BigInteger.ZERO));
//        detalle.setValorimpuesto(new BigDecimal(BigInteger.ZERO));
//        if (iva) {
//            if (detalle.getImpuesto() != null) {
//                double valor = detalle.getValor().doubleValue() / (1 + (detalle.getImpuesto().getPorcentaje().doubleValue() / 100));
//                detalle.setValor(new BigDecimal(valor));
//            }
//        }
//        if (detalle.getRetencion() != null) {
//            detalle.setValorret(new BigDecimal(detalle.getValor().doubleValue()
//                    * detalle.getRetencion().getPorcentaje().doubleValue() / 100));
//        } else {
//            detalle.setValorret(BigDecimal.ZERO);
//        }
//        if (detalle.getImpuesto() != null) {
//            double impuestoDoble = Math.round(detalle.getValor().doubleValue() * detalle.getImpuesto().getPorcentaje().doubleValue());
//            detalle.setValorimpuesto(new BigDecimal(impuestoDoble / 100));
//            if (detalle.getRetimpuesto() != null) {
//                double retimp = Math.round(impuestoDoble / 100
//                        * detalle.getRetimpuesto().getPorcentaje().doubleValue());
//                detalle.setVretimpuesto(new BigDecimal(retimp / 100));
//            }
//        } else {
//            detalle.setValorimpuesto(BigDecimal.ZERO);
//            detalle.setVretimpuesto(BigDecimal.ZERO);
//            detalle.setRetimpuesto(null);
//        }
//        if (validarDetalleCuenta()) {
//            return null;
//        }
//        try {
//
//            ejbRasCompras.edit(detalle, seguridadbean.getLogueado().getUserid());
//
//        } catch (GrabarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//        detalles.set(formularioDetalle.getIndiceFila(), detalle);
//        calculaTotal();
//        formularioDetalleCuenta.cancelar();
//        return null;
//    }
//
//    public String borrarDetalle() {
//        if (detallesb == null) {
//            detallesb = new LinkedList<>();
//        }
//        detallesb.add(detalle);
//        detalles.remove(formularioDetalle.getIndiceFila());
//        try {
//
//            Detallecompromiso det = detalle.getDetallecompromiso();
//            double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
//            det.setSaldo(new BigDecimal(det.getSaldo().doubleValue() + valor));
//            ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
//            ejbRasCompras.remove(detalle, seguridadbean.getLogueado().getUserid());
//        } catch (GrabarException | BorrarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioDetalle.cancelar();
//        calculaTotal();
//        return null;
//    }
//
//    public String borrarDetalleCuenta() {
//        if (detallesb == null) {
//            detallesb = new LinkedList<>();
//        }
//        detallesb.add(detalle);
//        detalles.remove(formularioDetalle.getIndiceFila());
//        try {
//
//            ejbRasCompras.remove(detalle, seguridadbean.getLogueado().getUserid());
//        } catch (BorrarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
//        }
//        formularioDetalleCuenta.cancelar();
//        calculaTotal();
//        return null;
//    }
//OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOJJJJJJJJJJJJJJJJJJJJOOOOOOOOOOOOOOO Tomado de obligaciones
    public String nuevoDetalle() {
        if ((obligacion.getConcepto() == null) || (obligacion.getConcepto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario concepto de obligacion para proceder a ingresar una cuenta");
            return null;
        }
        detalle = new Rascompras();
        detalle.setBien(Boolean.FALSE);
        setDetallesInternos(new LinkedList<>());
//        formularioCompromiso.insertar();
        formularioDetalle.insertar();
        setSoloRetencion(false);
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
        setDetallesInternos(new LinkedList<>());
//        formularioCompromiso.insertar();
        cuentaproveedor = null;
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
            cuentaproveedor = cuentasBean.traerCodigo(detalle.getCuenta());
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
        setDetallesInternos(new LinkedList<>());
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
        if (isSoloRetencion()) {
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

        if (isSoloRetencion()) {
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
            if (!isSoloRetencion()) {
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
            if (!isSoloRetencion()) {
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
            ejbRasCompras.create(detalle, seguridadbean.getLogueado().getUserid());
            Detallecompromiso det = detalle.getDetallecompromiso();
            if (!isSoloRetencion()) {
                double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
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
            ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
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
        if (cuentaproveedor == null) {
            MensajesErrores.advertencia("Seleccione una cuenta del proveedor");
            return null;
        }
        detalle.setCuenta(cuentaproveedor.getCodigo());
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
            if (!isSoloRetencion()) {
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
            ejbRasCompras.create(detalle, seguridadbean.getLogueado().getUserid());
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
            if (!isSoloRetencion()) {
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
            if (!isSoloRetencion()) {
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
            ejbRasCompras.edit(detalle, seguridadbean.getLogueado().getUserid());
            Detallecompromiso det = detalle.getDetallecompromiso();
            if (!isSoloRetencion()) {
                double valor = detalle.getValor().doubleValue() + detalle.getValorimpuesto().doubleValue();
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
            ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
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
        if (cuentaproveedor == null) {
            MensajesErrores.advertencia("Seleccione una cuenta del proveedor");
            return null;
        }
        detalle.setCuenta(cuentaproveedor.getCodigo());
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
            if (!isSoloRetencion()) {
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
            if (!isSoloRetencion()) {
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

            ejbRasCompras.edit(detalle, seguridadbean.getLogueado().getUserid());

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
            ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
            ejbRasCompras.remove(detalle, seguridadbean.getLogueado().getUserid());
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

            ejbRasCompras.remove(detalle, seguridadbean.getLogueado().getUserid());
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

    public double getValorTotalCompromiso() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", c);
        parametros.put(";campo", "o.valor");
        try {
            retorno = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getSaldoTotalCompromiso1() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", c);
        parametros.put(";campo", "o.saldo");
        try {
            retorno = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Math.abs(retorno);
    }

    public double getSaldoTotalCompromiso() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion.compromiso=:obligacion");
        parametros.put("obligacion", c);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            retorno = ejbRasCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Math.abs(retorno);
    }

    public String getEstado() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:obligacion and o.fechacontable is null");
        parametros.put("obligacion", c);
//        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            int total = ejbObligaciones.contar(parametros);
            if (total > 0) {
                return "Obligaciones no contabilizadas";
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Contabilizado";
    }

    public double getValorObligacion() {
        Obligaciones o = obligaciones.get(formularioObligacion.getFila().getRowIndex());
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            retorno = ejbRasCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            retorno = ejbRasCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
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
                    ejbDetallecompromiso.edit(c, seguridadbean.getLogueado().getUserid());
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
        salirTotal();
        obligacion = new Obligaciones();
        detalles = null;
        detallesb = null;
        facturaBean.setFactura(null);
        formularioObligacion.insertar();
        modificar(compromiso);
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
                    vRetenciones += r.getValorret().doubleValue();
                    if (r.getImpuesto() != null) {
                        total += r.getValorimpuesto().doubleValue();
                        vRetenciones += r.getVretimpuesto().doubleValue();
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
//        total -= vAnticipos;
        double vPagos = total + vAnticipos - vRetenciones + valorCuentas;
        a = new AuxiliarCarga();
        a.setTotal("Por Pagar");
        a.setCuenta("Pagos");

        a.setIngresos(new BigDecimal(vPagos));
//        p.setValor(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
        obligacion.setApagar(new BigDecimal(total + vAnticipos - vRetenciones + valorCuentas));
        totales.add(a);
        if (facturaBean.getFactura() != null) {
            // existe valor de factura electrónica
            a = new AuxiliarCarga();
            a.setTotal("Valor Factura");
            a.setCuenta("FACTURA");
            a.setIngresos(new BigDecimal(facturaBean.getFactura().getInfoFactura().getImporteTotal()));
            totales.add(a);
        }
        a = new AuxiliarCarga();
        a.setTotal("Total");
        a.setCuenta("Total");
        a.setIngresos(new BigDecimal(total + vAnticipos - vRetenciones - vPagos + valorCuentas));
        totales.add(a);
        // genera retencion

        return total + vAnticipos - vRetenciones - vPagos + valorCuentas;
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
        if (entidadesBean.getEntidad() == null) {
            MensajesErrores.advertencia("Seleccione un empleado responsable");
            return null;
        }
        if (compromiso.getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un Banco para la reposición");
            return null;
        }
        // validar el total de obligaciones con el total de comrpomisos

        try {
            double valorApagar = 0;
//            double valorObligaciones = 0;
            double valorObligaciones = 0;

            for (Obligaciones o : obligaciones) {
                double valor = o.getApagar().doubleValue();
                valorApagar += valor;
                valorObligaciones += taerValorObligacion(o);

            }
            double valorCompromisos = 0;
            for (Detallecompromiso d : detalleCompromiso) {
                valorCompromisos += d.getValor().doubleValue();
            }
            double total = Math.round((valorCompromisos - valorObligaciones) * 100) / 100;
            if (total != 0) {
                MensajesErrores.advertencia("Es necesario que el total de Obligaciones sea igual al total del Compromiso");
                return null;
            }
            for (Obligaciones o : obligaciones) {

                String respuesta = ejbObligaciones.validaContabilizar(o, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo());
                if (respuesta.contains("ERROR")) {
                    MensajesErrores.advertencia(respuesta);
                    return null;
                }

            }
            for (Obligaciones o : obligaciones) {

                String respuesta = ejbObligaciones.contabilizar(o, seguridadbean.getLogueado().getUserid(), 1, perfil.getMenu().getIdpadre().getModulo(), null, null);
                if (respuesta.contains("ERROR")) {
                    MensajesErrores.advertencia(respuesta);
                    return null;
                }

            }
            // buscar pago
            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            List<Pagosvencimientos> lp = ejbPagos.encontarParametros(parametros);
            Pagosvencimientos p = null;
            for (Pagosvencimientos p1 : lp) {
                p = p1;
            }
            if (p == null) {
                p = new Pagosvencimientos();
                p.setFechapago(new Date());
                p.setCompromiso(compromiso);
                p.setPagado(false);
                p.setValor(new BigDecimal(valorApagar));
                ejbPagos.create(p, seguridadbean.getLogueado().getUserid());
            } else {
//                p = new Pagosvencimientos();
                p.setFechapago(new Date());
                p.setCompromiso(compromiso);
                p.setPagado(false);
                p.setValor(new BigDecimal(valorApagar));
                ejbPagos.edit(p, seguridadbean.getLogueado().getUserid());
            }
            if (proveedorBeneficiario) {
                compromiso.setResponsable(entidadesBean.getEntidad().getEmpleados());
            } else {

            }

            compromiso.setFechareposicion(new Date());
            ejbCompromisos.edit(compromiso, seguridadbean.getLogueado().getUserid());
        } catch (Exception ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(FondoRotativoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        salirObligacion();
        traerRenglones();
        formularioImpresion.insertar();
        return null;
    }

    public String grabar() {
        if (puntoEmision != null) {
            if (puntoEmision.getAutomatica()) {
                MensajesErrores.advertencia("No es posible generar retención manual en punto de venta con numeración Automática");
                return null;
            }
        }

        calculaTotal();
        if (validar()) {
            return null;
        }
        obligacion.setEstado(1);
        grabaObligacion();
//        getFormulario().cancelar();
        modificar(compromiso);
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
//            MensajesErrores.advertencia("Fecha de emisión menor a fecha de compromiso");
//            return true;
//        }
        cuadre = Math.round(cuadre);
//        obligacion.setApagar(new BigDecimal(egreso));
//        if (cuadre != 0) {
//            MensajesErrores.advertencia("Egresos mayor a la compromiso presupuestario");
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
                parametros.put(";where", "o.establecimiento=:establecimiento "
                        + " and o.puntoemision=:puntoemision and o.documento=:documento and o.estado!=-1");
            } else {
                parametros.put(";where", "o.establecimiento=:establecimiento "
                        + " and o.puntoemision=:puntoemision and o.documento=:documento and o.id!=:id and o.estado!=-1");
                parametros.put("id", obligacion.getId());
            }
            parametros.put("establecimiento", obligacion.getEstablecimiento());
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
                if (!(r.getDetallecompromiso().getFecha().equals(obligacion.getFechaemision()))) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    MensajesErrores.advertencia("Fecha de compromiso no corresponde a fecha de emisión : " + sdf.format(r.getDetallecompromiso().getFecha()));
                    return true;
                }
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
        // validar saldo en compromisos

        return false;
    }

    private void grabaObligacion() {
        try {
//            obligacion.setProveedor(proveedorBean.getEmpresa().getProveedores());
//            obligacion.setImprimecertificacion(Boolean.FALSE);

            obligacion.setFechaingreso(new Date());
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            } else {
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            }
            // grabar ras

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                    ejbDetallecompromiso.edit(det, seguridadbean.getLogueado().getUserid());
                    ejbRasCompras.remove(r, seguridadbean.getLogueado().getUserid());
                }
            }
            ejbObligaciones.remove(obligacion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException | GrabarException ex) {
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
                    facturaBean.cargar(xmlStr);
                    //
                } catch (IOException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger
                            .getLogger(RetencionesBean.class
                                    .getName()).log(Level.SEVERE, null, ex);
                }
            } //fin if
        }// fin for
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
        if (obligacion == null) {
            return null;
        }
        if (obligacion.getProveedor() == null) {
            return null;
        }
        if (obligacion.getCompromiso() == null) {
            return null;
        }
        // solo para quitar el error
//        String cuentaPresupuesto=obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();
//        parametros.put(";where", "o.compromiso=:compromiso and o.proveedor=:proveedor");
        parametros.put(";where", "o.compromiso=:compromiso and o.proveedor=:proveedor and o.saldo>0");
        parametros.put("compromiso", obligacion.getCompromiso());
        parametros.put("proveedor", obligacion.getProveedor());
        try {
            List<Detallecompromiso> dl = ejbDetallecompromiso.encontarParametros(parametros);
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

    private void traerRenglones() {
        if (compromiso != null) {
            renglonesAuxiliar = new LinkedList<>();
            for (Obligaciones o : obligaciones) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id "
                        + "and o.cabecera.modulo=:modulo and o.cabecera.opcion in ('OBLIGACIONES','OBLIGACIONES_INV')");
                parametros.put("id", o.getId());
                parametros.put(";orden", "o.cabecera.id desc");
                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                try {
                    List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                    double credito = 0;
                    double debito = 0;
                    Cabeceras c = new Cabeceras();
                    for (Renglones r : rl) {
                        AuxiliarCarga a = new AuxiliarCarga();
                        a.setCuenta(r.getCuenta());
                        a.setTotal("Tipo : " + r.getCabecera().getTipo().getNombre() + " Asiento No:" + r.getCabecera().getNumero());
                        a.setReferencia(r.getReferencia());
                        a.setAuxiliar(r.getAuxiliar());
                        c = r.getCabecera();
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
                    if (c.getId() != null) {
                        AuxiliarCarga a1 = new AuxiliarCarga();
                        a1.setTotal("Tipo : " + c.getTipo().getNombre() + " Asiento No:" + c.getNumero());
                        a1.setIngresos(new BigDecimal(debito));
                        a1.setEgresos(new BigDecimal(credito));
                        a1.setReferencia("TOTAL Tipo : " + c.getTipo().getNombre() + " Asiento No: " + c.getNumero());
                        renglonesAuxiliar.add(a1);
                    }
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

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
                    ejbDetallecompromiso.edit(c, seguridadbean.getLogueado().getUserid());
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
    public Cuentas getCuentaproveedor() {
        return cuentaproveedor;
    }

    /**
     * @param cuentaproveedor the cuentaproveedor to set
     */
    public void setCuentaproveedor(Cuentas cuentaproveedor) {
        this.cuentaproveedor = cuentaproveedor;
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
}
