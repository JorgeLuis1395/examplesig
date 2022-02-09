/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.DocumentosanuladosFacade;
import org.beans.sfccbdmq.FirmadorFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Documentosanulados;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Tipoegreso;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteObligacionesSfccbdmq")
@ViewScoped
public class ReporteObligacionesBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ReporteObligacionesBean() {
        Calendar c = Calendar.getInstance();
        obligaciones = new LazyDataModel<Obligaciones>() {

            @Override
            public List<Obligaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Obligaciones> obligaciones;

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioRetencion = new Formulario();
    private Formulario formularioCorreo = new Formulario();
    private Formulario formularioAnulacion = new Formulario();
    private Formulario formularioAnular = new Formulario();
    @EJB
    private ObligacionesFacade ejbObligaciones;

    @EJB
    private FirmadorFacade ejbFirmador;
    @EJB
    private RetencionescomprasFacade ejbRetComp;
    @EJB
    private DocumentosanuladosFacade ejbDocAnul;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String tipoFecha = "o.fechaemision";
    private String concepto;
    private String correo;
    private Integer estado = 1;
    private Tipoegreso tipoEgreso;
    private Codigos tipoDocumento;
    private Contratos contrato;
    private Integer numero;
    private Integer id;
    private Obligaciones obligacion;
    private boolean iva;
    private boolean empleado;
    private Resource reporte;
    private List<Retencionescompras> listaRetencionesCompras;
    //
    private Formulario formularioRetencionManual = new Formulario();

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
            String nombreForma = "ReporteObligacionesVista";
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
        String where = tipoFecha + " between :desde and :hasta and o.tipodocumento.codigo in ('FACT','NOTVENT','LIQCOM')";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();
            String palabra = "palabra";

            where += " and upper(o." + claveLocal + ") like :" + palabra;
            parametros.put(palabra, "%" + valor.toUpperCase() + "%");
        }
        if (proveedorBean.getProveedor() != null) {
            if (proveedorBean.getProveedor().getId() != null) {
                if (contrato != null) {
                    where += " and o.contrato=:contrato";
                    parametros.put("contrato", contrato);
                } else {
                    where += " and o.proveedor=:proveedor";
                    parametros.put("proveedor", proveedorBean.getProveedor());
                }
            }
        }
        if (estado != null) {
            where += " and o.estado=:estado";
            parametros.put("estado", estado);
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

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
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

    public BigDecimal getTotalValor() {
        Obligaciones o = (Obligaciones) obligaciones.getRowData();
        return traerValor(o);
    }

    public BigDecimal traerValor(Obligaciones o) {

        return o.getApagar();
    }

    public BigDecimal getTotalValorRet() {
        Obligaciones o = (Obligaciones) obligaciones.getRowData();
        return traerValorRet(o);
    }

    public BigDecimal traerValorRet(Obligaciones o) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valor");
        try {
            return ejbRetComp.sumarCampo(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigDecimal getTotalValorImpuesto() {
        Obligaciones o = (Obligaciones) obligaciones.getRowData();
        return traerTotalValorImpuesto(o);
    }

    public BigDecimal traerTotalValorImpuesto(Obligaciones o) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.valoriva");
        try {
            return ejbRetComp.sumarCampo(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigDecimal getTotalValorRetImpuesto() {
        Obligaciones o = (Obligaciones) obligaciones.getRowData();

        return traerTotalValorRetImpuesto(o);
    }

    public BigDecimal traerTotalValorRetImpuesto(Obligaciones o) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        parametros.put(";campo", "o.iva");
        try {
            return ejbRetComp.sumarCampo(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public String imprimir() {
        Map parametros = new HashMap();
        if (tipoFecha == null) {
            tipoFecha = "o.fechaemision";
        }

        String where = tipoFecha + " between :desde and :hasta ";
//                        + "and o.detcertificacion.certificacion.anulado=false ";

        if (proveedorBean.getProveedor() != null) {
            if (contrato != null) {
                where += " and o.contrato=:contrato";
                parametros.put("contrato", contrato);
            } else {
                where += " and o.proveedor=:proveedor";
                parametros.put("proveedor", proveedorBean.getProveedor());
            }
        }
        if (estado != null) {
            where += " and o.estado=:estado";
            parametros.put("estado", estado);
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
        parametros.put(";where", where);
        try {
            List<Obligaciones> lista = ejbObligaciones.encontarParametros(parametros);
            for (Obligaciones o : lista) {
                o.setMonto(traerValor(o).doubleValue());
                o.setValorRetenciones(traerValorRet(o).doubleValue());
                o.setIva(traerTotalValorImpuesto(o).doubleValue());
                o.setIvaRetenciones(traerTotalValorRetImpuesto(o).doubleValue());
            }

            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "Reporte de Obligaciones");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/ObligacionesFechas.jasper"),
                    lista, "Obligaciones" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
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

    public String imprimirRetencion() {
        try {
            Obligaciones o = (Obligaciones) obligaciones.getRowData();
            ComprobanteRetencion c = ejbRetComp.generaRetElectronica(o);
            File archivo = ejbRetComp.generarReporte(o, c);
            reporte = new Recurso(Files.readAllBytes(archivo.toPath()));
            formularioRetencion.editar();

        } catch (IOException ex) {
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void cargarRetenciones(Obligaciones o) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.obligacion=:obligacion");
        parametros.put("obligacion", o);
        try {
            listaRetencionesCompras = ejbRetComp.encontarParametros(parametros);
//            for (Retencionescompras r:listaRetencionesCompras){
//                if (r.getImpuesto()==null)
//            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String borradorRetencion() {
        try {
            Obligaciones o = (Obligaciones) obligaciones.getRowData();
            String ruc = "";
            String beneficiario = "";
            String direccion = "";
            if (o.getProveedor() == null) {
//                if (o.getBeneficiario() != null) {
//                    ruc = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getPin();
//                    beneficiario = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().toString();
//                    direccion = cabeceraDoc.getCompromiso().getBeneficiario().getEntidad().getDireccion().toString();
//                }

            } else {
                ruc = o.getProveedor().getEmpresa().getRuc();
                beneficiario = o.getProveedor().getEmpresa().toString();
                direccion = o.getProveedor().getDireccion();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("BORRADOR DE COMPROBANTES DE RETENCION", null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Sr. (es) :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, beneficiario));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "RUC :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ruc));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Dirección"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, direccion));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(o.getFechaemision())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Documento"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, o.getTipodocumento().getCodigo()
                    + "-" + o.getEstablecimiento()
                    + "-" + o.getPuntoemision()
                    + "-" + o.getDocumento()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "email"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, o.getProveedor().getEmpresa().getEmail()));

            pdf.agregarTabla(null, columnas, 4, 100, null);
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Base Imponible"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Base Imponible 0"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "IVA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, true, "Ret IR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Ret IR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, true, "Ret IVA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Ret IVA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "A Pagar"));
            cargarRetenciones(o);

            columnas = new LinkedList<>();
            double totalValor = 0;
            double totalValorIva = 0;
            double totalBase = 0;
            double totalBase0 = 0;
            double totaliva = 0;
            for (Retencionescompras r : listaRetencionesCompras) {
                if (r.getId() != null) {
                    double valor = r.getValor().doubleValue();
                    double valorIva = r.getValoriva().doubleValue();
//                    double valorBase = r.getValor().doubleValue();
                    double valorBase = r.getBaseimponible().doubleValue();
//                    double valorBase0 = r.getValoriva().doubleValue();
                    double valorBase0 = r.getBaseimponible0().doubleValue();
                    double valorBaseIva = r.getIva().doubleValue();
                    double valorTotal = valorBase + valorBase0 + valorBaseIva;
                    double valorLinea = valorTotal - (valor + valorIva);
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorBase));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorBase0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorBaseIva));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, false, r.getRetencion() != null ? r.getRetencion().getEtiqueta().trim() : ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, false, r.getRetencionimpuesto() == null ? "" : r.getRetencionimpuesto().getEtiqueta().trim()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorIva));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorLinea));
                    totalValor += valor;
                    totalValorIva += valorIva;
                    totalBase += valorBase;
                    totalBase0 += valorBase0;
                    totaliva += valorBaseIva;
                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTALES"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBase));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBase0));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totaliva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalBase + totalBase0 + totaliva));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalValor));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalValorIva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, (totalBase + totalBase0 + totaliva) - (totalValor + totalValorIva)));
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, "");
            pdf.agregaParrafo("\n\n");
            reporte = pdf.traerRecurso();
            formularioRetencion.editar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String consultarRetencion() {
        String donde = configuracionBean.getConfiguracion().getUrlsri();
        Obligaciones o = (Obligaciones) obligaciones.getRowData();
        ComprobanteRetencion c = ejbRetComp.generaRetElectronica(o);
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", o.getClaver());
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
        if (rcomprobante.getNumeroComprobantes().equals("0")) {
            MensajesErrores.advertencia("No existen comprobantes con esa clave");
            return null;
        }
        try {
            if (a != null) {
                if (a.getAutorizacion() != null) {
                    if (!a.getAutorizacion().isEmpty()) {
                        if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {
                            Date cuando = ejbDocumentos.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            MensajesErrores.informacion("AUTORIZADO : " + sdf.format(cuando));
                            return null;
                        }
                        String autorizacion = a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getMensaje() + " - "
                                + a.getAutorizacion().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();
                        MensajesErrores.informacion(autorizacion);
                        String estadoSri = a.getAutorizacion().get(0).getEstado();
                        o.setEstadoSri(estadoSri);
                        ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String verificarRetencion() {
        String donde = configuracionBean.getConfiguracion().getUrlsri();
        Map parametros = new HashMap();
        parametros.put(";where", "o.fechaEmisionSri is null and o.claver is not null");
        parametros.put(";inicial", 0);
        parametros.put(";final", 4000);
        try {
            List<Obligaciones> listado = ejbObligaciones.encontarParametros(parametros);
            if (listado.isEmpty()) {
                MensajesErrores.advertencia("Ya todo esta actualizado");
                return null;
            }
            for (Obligaciones o : listado) {
                RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", o.getClaver());
                RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
                if (!rcomprobante.getNumeroComprobantes().equals("0")) {
                    if (a != null) {
                        if (a.getAutorizacion() != null) {
                            if (!a.getAutorizacion().isEmpty()) {
                                if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {
                                    String estado = a.getAutorizacion().get(0).getEstado();
                                    Date cuando = ejbDocumentos.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    String xmlResultado = a.getAutorizacion().get(0).getComprobante();
                                    int desdeInicio = xmlResultado.indexOf("<fechaEmision>");
                                    int hastaInicio = xmlResultado.indexOf("</fechaEmision>");
                                    String fecha = xmlResultado.substring(desdeInicio, hastaInicio);
                                    fecha = fecha.replace("-", "/");
                                    fecha = fecha.replace("<fechaEmision>", "");
                                    fecha = fecha.replace("</fechaEmision>", "");
                                    String[] fechaAux = fecha.split("/");
                                    int dia = Integer.parseInt(fechaAux[0]);
                                    int mes = Integer.parseInt(fechaAux[1]);
                                    int anio = Integer.parseInt(fechaAux[2]);
//                                    Date f=new Date(anio,mes-1,dia);

                                    //Secuencial
                                    int desdesec = xmlResultado.indexOf("<secuencial>");
                                    int hastasec = xmlResultado.indexOf("</secuencial>");
                                    String secuencia = xmlResultado.substring(desdesec, hastasec);
                                    secuencia = secuencia.replace("<secuencial>", "");

                                    Calendar c = Calendar.getInstance();
                                    c.set(anio, mes - 1, dia);
                                    o.setFechaEmisionSri(c.getTime());
                                    o.setFechaAutorizaRetencion(cuando);
                                    o.setSecuencial(secuencia);
                                    o.setEstadoSri(estado);
                                    ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());

                                }

                            }
                        }
                    }
                }
            }
            //Actualizar todos  los campos si no existen
            parametros = new HashMap();
            parametros.put(";where", "o.claver is not null and o.estadoSri is null");
            parametros.put(";inicial", 0);
            parametros.put(";final", 4000);
            List<Obligaciones> listadoEstado = ejbObligaciones.encontarParametros(parametros);
            if (listadoEstado.isEmpty()) {
                MensajesErrores.advertencia("Ya todo esta actualizado en estado");
                return null;
            }
            for (Obligaciones o : listadoEstado) {
                RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", o.getClaver());
                RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
                if (!rcomprobante.getNumeroComprobantes().equals("0")) {
                    if (a != null) {
                        if (a.getAutorizacion() != null) {
                            if (!a.getAutorizacion().isEmpty()) {
                                String estadoSri = a.getAutorizacion().get(0).getEstado();
                                Date cuando = ejbDocumentos.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                String xmlResultado = a.getAutorizacion().get(0).getComprobante();
                                int desdeInicio = xmlResultado.indexOf("<fechaEmision>");
                                int hastaInicio = xmlResultado.indexOf("</fechaEmision>");
                                String fecha = xmlResultado.substring(desdeInicio, hastaInicio);
                                fecha = fecha.replace("-", "/");
                                fecha = fecha.replace("<fechaEmision>", "");
                                fecha = fecha.replace("</fechaEmision>", "");
                                String[] fechaAux = fecha.split("/");
                                int dia = Integer.parseInt(fechaAux[0]);
                                int mes = Integer.parseInt(fechaAux[1]);
                                int anio = Integer.parseInt(fechaAux[2]);
//                                    Date f=new Date(anio,mes-1,dia);

                                //Secuencial
                                int desdesec = xmlResultado.indexOf("<secuencial>");
                                int hastasec = xmlResultado.indexOf("</secuencial>");
                                String secuencia = xmlResultado.substring(desdesec, hastasec);
                                secuencia = secuencia.replace("<secuencial>", "");

                                Calendar c = Calendar.getInstance();
                                c.set(anio, mes - 1, dia);
                                if (o.getFechaEmisionSri() == null) {
                                    o.setFechaEmisionSri(c.getTime());
                                }
                                if (o.getFechaAutorizaRetencion() == null) {
                                    o.setFechaAutorizaRetencion(cuando);
                                }
                                if (o.getSecuencial() == null) {

                                    o.setSecuencial(secuencia);
                                }
                                if (o.getEstadoSri() == null) {
                                    o.setEstadoSri(estadoSri);
                                }
                                ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
                            }
                        }
                    }
                }
            }

            MensajesErrores.informacion("Terminado proceso");
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.error("Proceso terminado con errores");
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String reNumerar() {
        setObligacion((Obligaciones) obligaciones.getRowData());
        formularioAnulacion.editar();
        return null;
    }

    public String grabarReNumerar() {
        Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
        Map parametros = new HashMap();
        parametros.put(";where", "o.punto.codigo=:punto and o.documento=:documento and o.punto.sucursal.ruc=:sucursal");
        parametros.put("punto", getObligacion().getPuntor());
        parametros.put("sucursal", getObligacion().getEstablecimientor());
        parametros.put("documento", ret);
        Documentos documento = null;
        try {
            List<Documentos> documentos = ejbDocumentos.encontarParametros(parametros);
            for (Documentos d : documentos) {
                documento = d;
            }

            if (documento != null) {
                Documentosanulados da = new Documentosanulados();
                da.setFecha(new Date());
                da.setNumero(getObligacion().getNumeror());
                da.setDocumento(documento);
                da.setObservaciones(getObligacion().getEstablecimientor() + "-" + getObligacion().getPuntor());
                da.setAutorizacion(getObligacion().getClaver());
                ejbDocAnul.edit(da, seguridadbean.getLogueado().getUserid());
                getObligacion().setPuntor(documento.getPunto().getCodigo());
                getObligacion().setEstablecimientor(documento.getPunto().getSucursal().getRuc());
                getObligacion().setClaver(null);
                Integer numret = documento.getNumeroactual();
                numret++;
                getObligacion().setNumeror(numret);
                documento.setNumeroactual(numret);
                ejbDocumentos.edit(documento, seguridadbean.getLogueado().getUserid());
                getObligacion().setNumeror(numret);
                ejbObligaciones.edit(getObligacion(), seguridadbean.getLogueado().getUserid());
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnulacion.cancelar();
        return null;
    }

    public String anular() {
        setObligacion((Obligaciones) obligaciones.getRowData());
        formularioAnular.editar();
        return null;
    }

    public String grabaranular() {
        try {
            getObligacion().setEstado(-1);
            ejbObligaciones.edit(getObligacion(), seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnular.cancelar();
        return null;
    }

    public String pedirAutorizacion() {
        String donde = configuracionBean.getConfiguracion().getUrlsri();
        Obligaciones o = (Obligaciones) obligaciones.getRowData();
        ComprobanteRetencion c = ejbRetComp.generaRetElectronica(o);

//        ComprobanteRetencion cr = ejbRetComp.generaRetElectronica(o);
        String x = ejbRetComp.autorizarSri(o, c);
        if (x != null) {
            MensajesErrores.advertencia(x);
            return null;
        }
//        consutarAutorizacion(o, c);

        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", o.getClaver());
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();
        try {
            if (!rcomprobante.getNumeroComprobantes().equals("0")) {
                if (a != null) {
                    if (a.getAutorizacion() != null) {
                        if (!a.getAutorizacion().isEmpty()) {
                            String estadoSri = a.getAutorizacion().get(0).getEstado();
                            if (a.getAutorizacion().get(0).getEstado().equals("AUTORIZADO")) {
                                Date cuando = ejbDocumentos.convertXMLCalenarIntoDate(a.getAutorizacion().get(0).getFechaAutorizacion());
                                String xmlResultado = a.getAutorizacion().get(0).getComprobante();
                                int desdeInicio = xmlResultado.indexOf("<fechaEmision>");
                                int hastaInicio = xmlResultado.indexOf("</fechaEmision>");
                                String fecha = xmlResultado.substring(desdeInicio, hastaInicio);
                                fecha = fecha.replace("-", "/");
                                fecha = fecha.replace("<fechaEmision>", "");
                                fecha = fecha.replace("</fechaEmision>", "");
                                String[] fechaAux = fecha.split("/");
                                int dia = Integer.parseInt(fechaAux[0]);
                                int mes = Integer.parseInt(fechaAux[1]);
                                int anio = Integer.parseInt(fechaAux[2]);

                                //Secuencial
                                int desdesec = xmlResultado.indexOf("<secuencial>");
                                int hastasec = xmlResultado.indexOf("</secuencial>");
                                String secuencia = xmlResultado.substring(desdesec, hastasec);
                                secuencia = secuencia.replace("<secuencial>", "");

                                Calendar c2 = Calendar.getInstance();
                                c2.set(anio, mes - 1, dia);
                                o.setFechaEmisionSri(c2.getTime());
                                o.setFechaAutorizaRetencion(cuando);
                                o.setSecuencial(secuencia);
                                ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
                            }
                            o.setEstadoSri(estadoSri);
                            ejbObligaciones.edit(o, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public String reenviar() {
        setObligacion((Obligaciones) obligaciones.getRowData());
        correo = getObligacion().getProveedor().getEmpresa().getEmail();
        formularioCorreo.insertar();
        return null;
    }

    public String reenviarCorreo() {

        try {
            ComprobanteRetencion c = ejbRetComp.generaRetElectronica(getObligacion());

            ejbRetComp.reenviarCorreo(getObligacion(), c, correo);

        } catch (GrabarException ex) {
            Logger.getLogger(ReporteObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioCorreo.cancelar();
        return null;
    }

    /**
     * @return the formularioAnulacion
     */
    public Formulario getFormularioAnulacion() {
        return formularioAnulacion;
    }

    /**
     * @param formularioAnulacion the formularioAnulacion to set
     */
    public void setFormularioAnulacion(Formulario formularioAnulacion) {
        this.formularioAnulacion = formularioAnulacion;
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
     * @return the formularioAnular
     */
    public Formulario getFormularioAnular() {
        return formularioAnular;
    }

    /**
     * @param formularioAnular the formularioAnular to set
     */
    public void setFormularioAnular(Formulario formularioAnular) {
        this.formularioAnular = formularioAnular;
    }
}
