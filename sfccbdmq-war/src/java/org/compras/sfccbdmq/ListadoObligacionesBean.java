/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import javax.faces.application.Resource;
import org.contabilidad.sfccbdmq.RetencionesBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.sfccbdmq.Adicionales;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.utilitarios.sfccbdmq.infoTributaria;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retenciones;
import org.entidades.sfccbdmq.Tipoegreso;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.icefaces.apache.commons.io.IOUtils;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "listadosObligacionesSfccbdmq")
@ViewScoped
public class ListadoObligacionesBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ListadoObligacionesBean() {
        Calendar c = Calendar.getInstance();
        obligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Obligaciones> obligaciones;
    private List<Rascompras> detalles;
    private List<Renglones> renglones;
    private List<Rascompras> detallesInternos;
    private List<Pagosvencimientos> promesas;
    private List<Pagosvencimientos> promesasb;
    private List<Rascompras> detallesb;
    private List<AuxiliarCarga> totales;
    private Rascompras detalle;
    private Pagosvencimientos pago;
    private Obligaciones obligacion = new Obligaciones();
    private Impuestos impuesto;
    private Retenciones retencion;
    private Puntoemision puntoEmision;
    private ComprobanteRetencion comprobante;
    private Anticipos anticipo;
    private Autorizaciones autorizacion;
    private double valorAnticipo;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioPromesas = new Formulario();
    private Formulario formularioInterno = new Formulario();
    private Formulario formularioRetencion = new Formulario();
    private Formulario formularioReporte = new Formulario();
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
//    @EJB
//    private FirmadorFacade ejbFirmador;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CuentasFacade ejbCuentas;
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
//    @ManagedProperty(value = "#{cuentasSfccbdmq}")
//    private CuentasBean cuentasBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String tipoFecha = "o.fechaemision";
    private String concepto;
    private String clave;
    private Integer estado;
    private Tipoegreso tipoEgreso;
    private Codigos tipoDocumento;
    private Contratos contrato;
    private Integer numero;
    private Integer id;
    private Documentos documeto;
    //
    private File keyStore;
    private File archivo;
    private File pdfFile;
    // reportes para enviar por mail
    private Resource recursoXml;
    private Resource recursoPdf;

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
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechaingreso desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = tipoFecha + " between :desde and :hasta and o.imprimecertificacion=true ";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
        if (proveedorBean.getProveedor()!= null) {
            if (contrato != null) {
                where += " and o.contrato=:contrato";
                parametros.put("desde", desde);
            } else {
                where += " and o.proveedor=:proveedor";
                parametros.put("proveedor", proveedorBean.getProveedor());
            }
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.concepto) like:concepto";
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

    private double calculaTotal() {
        totales = new LinkedList<>();
        AuxiliarCarga a = new AuxiliarCarga();
        a.setTotal("Compromiso Presupuestario");
        a.setIngresos(new BigDecimal(0));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Egresos");
        // sumar todos los ras
        double total = 0;
        double vAnticipos = 0;
        double vRetenciones = 0;
        if (detalles == null) {
            detalles = new LinkedList<>();
        }
        for (Rascompras r : detalles) {
            if (r.getCba().equals("A")) {
                vAnticipos -= r.getValor().doubleValue();

            } else if (r.getCba().equals("I")) {
                a.setIngresos(new BigDecimal(a.getIngresos().doubleValue() + r.getValor().doubleValue()));
            } else {
                total += r.getValor().doubleValue();
                vRetenciones += r.getValorret().doubleValue();
            }
        }
        a.setIngresos(new BigDecimal(total));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Retenciones");
        a.setIngresos(new BigDecimal(vRetenciones));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Anticipos");
        a.setIngresos(new BigDecimal(vAnticipos));
        totales.add(a);
        total -= vAnticipos;
        vAnticipos = 0;
        a = new AuxiliarCarga();
        a.setTotal("Pagos");
        if (promesas == null) {
            promesas = new LinkedList<>();
        }
        for (Pagosvencimientos p : promesas) {
            vAnticipos += p.getValor().doubleValue();
        }
        a.setIngresos(new BigDecimal(vAnticipos));
        obligacion.setApagar(new BigDecimal(vAnticipos));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Total");
        a.setIngresos(new BigDecimal(total - vAnticipos - vRetenciones));
        totales.add(a);
        // genera retencion

        if ((puntoEmision != null) && (puntoEmision.getAutomatica())) {
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
            infoRetencion.setObligadoContabilidad("NO");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            infoRetencion.setFechaEmision(sdf.format(obligacion.getFechaemision()));
            if (puntoEmision != null) {
                infoRetencion.setDirEstablecimiento(puntoEmision.getSucursal().getPrincipal() + " "
                        + puntoEmision.getSucursal().getNumero()
                        + " y " + puntoEmision.getSucursal().getSecuendaria());
                // poner establecimineto y demas
                infoTributariaRetencion.setEstab(puntoEmision.getSucursal().getRuc());
                infoTributariaRetencion.setPtoEmi(puntoEmision.getCodigo());
                // buscar retencion
                infoTributariaRetencion.setSecuencial(obligacion.getNumeror().toString());
                comprobante.setInfoTributaria(infoTributariaRetencion);
                comprobante.setInfoCompRetencion(infoRetencion);
            }
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
            String docSustento = autorizacion.getEstablecimiento() + autorizacion.getPuntoemision() + df1.format(obligacion.getDocumento());
            for (Rascompras r : detalles) {
                if (r.getCba().equals("I")) {
                    if (r.getRetencion() != null) {
                        comprobante.cargaImpuesto(r.getImpuesto().getEtiqueta().trim(),
                                r.getRetencion().getEtiqueta().trim(), sdf.format(obligacion.getFechaemision()), r.getRetencion().getPorcentaje().intValue(),
                                autorizacion.getTipodocumento().getParametros(), df.format(r.getValor()),
                                docSustento,
                                df.format(r.getValorret().doubleValue()));
                    }
                } else if (r.getCba().equals("C")) {
                    if (r.getRetencion() != null) {
                        comprobante.cargaImpuesto(configuracionBean.getConfiguracion().getRenta().toString().trim(),
                                r.getRetencion().getEtiqueta().trim(), sdf.format(obligacion.getFechaemision()), r.getRetencion().getPorcentaje().intValue(),
                                autorizacion.getTipodocumento().getParametros(), df.format(r.getValor()),
                                docSustento,
                                df.format(r.getValorret().doubleValue()));
                    }
                }
            }
            comprobante.cargaAdicional("email", obligacion.getProveedor().getEmpresa().getEmail());
            comprobante.cargaAdicional("telefono", obligacion.getProveedor().getEmpresa().getTelefono1().toString());
//        comprobante.cargaAdicional("direccion", obligacion.getProveedor().getEmpresa().getDireccionesList().toString());
            DecimalFormat dfAdicional = new DecimalFormat("00000000", new DecimalFormatSymbols(Locale.US));
            comprobante.calculaClave(dfAdicional.format(obligacion.getId()));
            comprobante.setInfoTributaria(infoTributariaRetencion);
            comprobante.setInfoCompRetencion(infoRetencion);
        }
        MensajesErrores.informacion("Totales calculados correctamente");
        return total - vAnticipos - vRetenciones;
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
     * @return the keyStore
     */
    public File getKeyStore() {
        return keyStore;
    }

    /**
     * @param keyStore the keyStore to set
     */
    public void setKeyStore(File keyStore) {
        this.keyStore = keyStore;
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
        recursoPdf = null;
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
                it.setPorcentajeRetener(i.getPorcentajeRetener().toString());
                it.setFechaEmisionCcompModificado(i.getFechaEmisionDocSustento());
                listaInfo.add(it);
            }

            Map parametros = new HashMap();
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
            File logo = new File(realPath + "/logo-new.png");
            isLogo = new FileInputStream(logo);
            parametros.put("RUC", configuracionBean.getConfiguracion().getRuc());
            parametros.put("NUM_AUT", obligacion.getAutoretencion());
            parametros.put("FECHA_AUT", sdf.format(obligacion.getFechar()));
            parametros.put("TIPO_EMISION", "NORMAL");
            parametros.put("RAZON_SOCIAL", configuracionBean.getConfiguracion().getNombre());
            parametros.put("DIR_MATRIZ", configuracionBean.getConfiguracion().getDireccion());
            parametros.put("CONT_ESPECIAL", configuracionBean.getConfiguracion().getEspecial());//"00162"
            parametros.put("LLEVA_CONTABILIDAD", "SI");
            parametros.put("RUC_COMPRADOR", comprobante.getInfoTributaria().getRuc());
            parametros.put("RS_COMPRADOR", comprobante.getInfoTributaria().getRazonSocial());
            parametros.put("SUBREPORT_DIR", realPath + "/");
            parametros.put("FECHA_EMISION", comprobante.getInfoCompRetencion().getFechaEmision());
            parametros.put("CLAVE_ACC", comprobante.getInfoTributaria().getClaveAcceso());
            parametros.put("NUM_FACT", obligacion.getDocumento().toString());
            parametros.put("EJERCICIO_FISCAL", comprobante.getInfoCompRetencion().getPeriodoFiscal());
            parametros.put("AMBIENTE", comprobante.getInfoTributaria().getAmbiente());
            parametros.put("NOM_COMERCIAL", comprobante.getInfoTributaria().getNombreComercial());
            String img1 = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/logo-new.png");
            parametros.put("firma1", img1);
//            parametros.put("GUIA", documento.getPeriodofiscal());

            Calendar c = Calendar.getInstance();
            recursoPdf = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/comprobanteRetencion.jasper"),
                    listaInfo, "R" + String.valueOf(c.getTimeInMillis()) + ".pdf");
//                    listaInfo, documento.getClaveadecceso() + ".pdf");
            InputStream is = recursoPdf.getInputStream();
//            InputStream is = recursoPdf.open();
//            byte[] bytes = IOUtils.toByteArray(is);
            pdfFile = stream2file(is, comprobante.getInfoTributaria().getClaveAcceso(), ".pdf");
//            mostrarArchivos = true;
        } catch (FileNotFoundException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ListadoObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

//    /**
//     * @return the cuentasBean
//     */
//    public CuentasBean getCuentasBean() {
//        return cuentasBean;
//    }
//
//    /**
//     * @param cuentasBean the cuentasBean to set
//     */
//    public void setCuentasBean(CuentasBean cuentasBean) {
//        this.cuentasBean = cuentasBean;
//    }
    

    private void traerRenglones() {
        if (obligacion != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='OBLIGACIONES'");
            parametros.put("id", obligacion.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ListadoObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String imprimirSolicitudPago(Obligaciones obligacion) {
        this.obligacion = obligacion;
        if (obligacion.getEstado() < 2) {
            MensajesErrores.advertencia("No puede imprimirse obligación no terminada");
            return null;
        }
        try {
            Map parametros = new HashMap();
            proveedorBean.setEmpresa(obligacion.getProveedor().getEmpresa());
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
                facturaBean.cargar(obligacion.getFacturaelectronica());
            }
//        proveedorBean.setTipobuscar("-2");
            proveedorBean.setRuc(obligacion.getProveedor().getEmpresa().getNombre());
            // buscar ras compras
            parametros = new HashMap();
            parametros.put(";where", "o.obligacion=:obligacion");
            parametros.put("obligacion", obligacion);

            detalles = ejbDetalles.encontarParametros(parametros);
            promesas = ejbVencimientos.encontarParametros(parametros);
            traerRenglones();
            // las promesas
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ListadoObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotalImpresion();
        formularioReporte.editar();

        return null;
    }

    private double calculaTotalImpresion() {
        totales = new LinkedList<>();
        AuxiliarCarga a = new AuxiliarCarga();
        // revisar el total
//        a.setTotal("<p><strong>Compromiso Presupuestario : </strong>" + obligacion.getCompromiso().getDetallecertificacion().getCertificacion().getMotivo() + " "
//                + "</p><strong>Clasificador presupuestario : </strong>" + obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getClasificador() + "</p>"
//                + "<p><strong>Actividad : </strong>" + obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getProyecto().getNombre()
//                + "</p> <p><strong>Fuente de Financiamineto :</strong>" + obligacion.getCompromiso().getDetallecertificacion().getAsignacion().getFuente().getNombre() + "</>");
//        a.setIngresos(obligacion.getValorcertificacion());
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Egresos");
        // sumar todos los ras
        double total = 0;
        double vAnticipos = 0;
        double vRetenciones = 0;
        if (detalles == null) {
            detalles = new LinkedList<>();
        }
        for (Rascompras r : detalles) {
            if (r.getCba().equals("A")) {
                vAnticipos -= r.getValor().doubleValue();
            } else {
                total += r.getValor().doubleValue();
                vRetenciones += r.getValorret().doubleValue();

            }
        }
        a.setIngresos(new BigDecimal(total));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Retenciones");
        a.setIngresos(new BigDecimal(vRetenciones));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Anticipos");
        a.setIngresos(new BigDecimal(vAnticipos));
        totales.add(a);
        total -= vAnticipos;
        vAnticipos = 0;
        a = new AuxiliarCarga();
        a.setTotal("Pagos");
        if (promesas == null) {
            promesas = new LinkedList<>();
        }
        for (Pagosvencimientos p : promesas) {
            vAnticipos += p.getValor().doubleValue();
        }
        obligacion.setApagar(new BigDecimal(vAnticipos));
        a.setIngresos(new BigDecimal(vAnticipos));
        totales.add(a);
        a = new AuxiliarCarga();
        a.setTotal("Total");
        a.setIngresos(new BigDecimal(total - vAnticipos - vRetenciones));
        totales.add(a);
        // genera retencion

        return total - vAnticipos - vRetenciones;
    }

    public String getValorStr() {
        if (obligacion == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(obligacion.getApagar());
    }
}
