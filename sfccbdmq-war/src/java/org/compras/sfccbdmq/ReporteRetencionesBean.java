/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.auxiliares.sfccbdmq.Documento103;
import org.contabilidad.sfccbdmq.Formulario107Bean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AtsSri;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.ReembolsosFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Reembolsos;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Retenciones;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reproteRetencionesSfccbdmq")
@ViewScoped
public class ReporteRetencionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Retencionescompras> listadoRetencionescompras;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioReporteXls = new Formulario();
    private String tipoFecha = "o.inicio";
    private Date desde;
    private Date hasta;
    private int mes;
    private int anio;
    private Impuestos impuesto;
    private Retenciones retencion;
    private Retenciones retencionImpuesto;
    private int tipo = -1;
    private double totalRetencionescompras;
    private Resource archivoAnexo;
    private Resource formulario103;
    private Resource reporte;
    private Resource reporteXls;

    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RetencionesFacade ejbRetenciones;
    @EJB
    private RetencionescomprasFacade ejbRetencionesCompras;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private ReembolsosFacade ejbReembolsos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{facturaSri}")
    private FacturaSriBean facturaBean;

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

    /* @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH) + 1;
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        desde = c.getTime();
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        hasta = c.getTime();

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteRetencionesVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of ContratosEmpleadoBean
     */
    public ReporteRetencionesBean() {

        listadoRetencionescompras = new LazyDataModel<Retencionescompras>() {

            @Override
            public List<Retencionescompras> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }

        };
    }

    public String buscar() {

        totalRetencionescompras = 0;

        listadoRetencionescompras = new LazyDataModel<Retencionescompras>() {
            @Override
            public List<Retencionescompras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Calendar primerDia = Calendar.getInstance();
                Calendar ultimoDia = Calendar.getInstance();
                primerDia.set(anio, mes - 1, 1);
                ultimoDia.set(anio, mes, 1);
                ultimoDia.add(Calendar.DATE, -1);
                try {
                    Map parametros = new HashMap();
                    String where = "o.obligacion.fechar"
                            + " between :desde and :hasta and (o.retencion is not null or o.retencionimpuesto is not null) and o.obligacion.estado=2 ";

                    if (retencion != null) {
                        where += " and o.retencion=:retencion";
                        parametros.put("retencion", retencion);

                    }
                    if (retencionImpuesto != null) {
                        where += " and o.retencionimpuesto=:retimpuesto";
                        parametros.put("retimpuesto", retencionImpuesto);

                    }
                    if (impuesto != null) {
                        where += " and o.impuesto=:impuesto";
                        parametros.put("impuesto", impuesto);

                    }
                    if (tipo == 0) {
                        where += " and o.retencionimpuesto is not null";

                    } else if (tipo == 1) {
                        where += " and o.retencion is not null";
                    }
                    parametros.put(";where", where);
//                    parametros.put(";orden", "o.obligacion.fechacontable desc");
                    parametros.put("desde", primerDia.getTime());
                    parametros.put("hasta", ultimoDia.getTime());
                    parametros.put(";suma", "sum(o.baseimponible+o.baseimponible0),sum(o.iva),sum(o.valor),sum(o.valoriva)");
                    List<Object[]> listaResultados = ejbRetencionesCompras.sumar(parametros);

                    parametros = new HashMap();
                    where = "o.obligacion.fechar between :desde and :hasta "
                            + " and (o.retencion is not null or o.retencionimpuesto is not null)";
                    if (retencion != null) {
                        where += " and o.retencion=:retencion";
                        parametros.put("retencion", retencion);

                    }
                    if (retencionImpuesto != null) {
                        where += " and o.retencionimpuesto:retimpuesto";
                        parametros.put("retimpuesto", retencionImpuesto);

                    }
                    if (impuesto != null) {
                        where += " and o.impuesto=:impuesto";
                        parametros.put("impuesto", impuesto);

                    }
                    if (tipo == 0) {
                        where += " and o.retencionimpuesto is not null";

                    } else if (tipo == 1) {
                        where += " and o.retencion is not null";
                    }
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.obligacion.fechar asc");
                    parametros.put("desde", primerDia.getTime());
                    parametros.put("hasta", ultimoDia.getTime());
                    int total = ejbRetencionesCompras.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoRetencionescompras.setRowCount(total + 1);
                    List<Retencionescompras> rlista = ejbRetencionesCompras.encontarParametros(parametros);
                    for (Object[] r : listaResultados) {
                        Retencionescompras r1 = new Retencionescompras();
                        BigDecimal valor = (BigDecimal) r[0];
                        r1.setValor(valor);
//                        parametros.put(";suma", "sum(o.valor),sum(o.valorimpuesto),sum(o.vretimpuesto),sum(o.valorret)");
                        valor = (BigDecimal) r[1];
                        r1.setIva(valor);
                        valor = (BigDecimal) r[2];
                        r1.setValoriva(valor);
                        valor = (BigDecimal) r[3];
                        r1.setIva(valor);
                        r1.setPartida("Total");
                        rlista.add(r1);
                    }
                    return rlista;
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
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
     * @return the listadoRetencionescompras
     */
    public LazyDataModel<Retencionescompras> getListadoRetencionescompras() {
        return listadoRetencionescompras;
    }

    /**
     * @param listadoRetencionescompras the listadoRetencionescompras to set
     */
    public void setListadoRetencionescompras(LazyDataModel<Retencionescompras> listadoRetencionescompras) {
        this.listadoRetencionescompras = listadoRetencionescompras;
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
     * @return the totalRetencionescompras
     */
    public double getTotalRetencionescompras() {
        return totalRetencionescompras;
    }

    /**
     * @param totalRetencionescompras the totalRetencionescompras to set
     */
    public void setTotalRetencionescompras(double totalRetencionescompras) {
        this.totalRetencionescompras = totalRetencionescompras;
    }

    public double getValorObligacion() {
        Retencionescompras o = (Retencionescompras) listadoRetencionescompras.getRowData();
        String where = "o.obligacion=:obligacion";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("obligacion", o.getObligacion());
        parametros.put(";campo", "o.baseimponible+o.iva+o.baseimponible0");
        try {
            return ejbRetencionesCompras.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the retencionImpuesto
     */
    public Retenciones getRetencionImpuesto() {
        return retencionImpuesto;
    }

    /**
     * @param retencionImpuesto the retencionImpuesto to set
     */
    public void setRetencionImpuesto(Retenciones retencionImpuesto) {
        this.retencionImpuesto = retencionImpuesto;
    }

    /**
     * @return the tipo
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
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

    public String generaAts() {
        Calendar primerDia = Calendar.getInstance();
        Calendar ultimoDia = Calendar.getInstance();
        primerDia.set(anio, mes - 1, 1);
        ultimoDia.set(anio, mes, 1);
        ultimoDia.add(Calendar.DATE, -1);
        AtsSri ats = new AtsSri();
        DecimalFormat formatoMes = new DecimalFormat("00");
        ats.setAnio(String.valueOf(anio));
        ats.setMes(formatoMes.format(mes));
        ats.setIdInformante(configuracionBean.getConfiguracion().getRuc());
        ats.setRazonSocial(configuracionBean.getConfiguracion().getNombre());
        ats.setTipoIDInformante("R");
//        ats.setNumEstabRuc("001");
        ats.setTotalVentas("0.00");
        ats.setCodigoOperativo("IVA");
        Map parametros = new HashMap();
        //Que no salgan los que no tienen retencion o claver 
        parametros.put(";where", "o.fechaemision between :desde and :hasta and o.tipodocumento.parametros!='00' and o.estado=2 and (o.autoretencion is not null or o.claver is not null)");
//        parametros.put(";where", "o.fechar between :desde and :hasta and o.tipodocumento.parametros!='00'");
        parametros.put("desde", primerDia.getTime());
        parametros.put(";orden", "o.fechar asc");
        parametros.put("hasta", ultimoDia.getTime());
        boolean existe = false;

        try {
            List<Obligaciones> olista = ejbObligaciones.encontarParametros(parametros);
            int obli = 0;
            for (Obligaciones o : olista) {
//                if (o.getProveedor().getEmpresa().getRuc().equals("1768152560001")) {
//                    System.out.println("Hola");
//                }
                double valRetBien10 = 0;
                double baseImpExe = 0;
                double baseImponible = 0;
                double montoIva = 0;
                double valRetServ100 = 0;
                double valorRetServicios = 0;
                double valRetServ20 = 0;
                double baseNoGraIva = 0;
                double montoIce = 0;
                double totbasesImpReemb = 0;
                double baseImpGrav = 0;
                double valorRetBienes = 0;
                double valorRetServ50 = 0;

                String codSustento = o.getSustento() == null ? "02" : o.getSustento().getCodigo();
//                String codSustento = o.getTipodocumento().getParametros();
                String secuencial = o.getDocumento() == null ? "" : o.getDocumento().toString();
                String secRetencion1 = o.getNumeror() == null ? "88888888" : o.getNumeror().toString();
                String puntoEmision = o.getPuntoemision() == null ? "001" : o.getPuntoemision();
                Date fechaRegistro = o.getFechaemision();
                String establecimiento = o.getEstablecimiento() == null ? "001" : o.getEstablecimiento();
                String parteRel = "NO";
                String estabRetencion1 = o.getEstablecimientor() == null ? "001" : o.getEstablecimientor();
                String ptoEmiRetencion1 = o.getPuntor() == null ? "001" : o.getPuntor();
                String tipoComprobante = o.getTipodocumento().getParametros();

                Date fechaEmision = o.getFechaemision();
                if (o.getAutoretencion() == null || o.getAutoretencion().isEmpty()) {
                    o.setAutoretencion(null);
                }
//                String autRetencion1 = o.getAutoretencion() == null ? "0000000000" : o.getAutoretencion();
                String autRetencion1 = o.getAutoretencion() == null ? o.getClaver() : (o.getAutoretencion().isEmpty() ? o.getClaver() : o.getAutoretencion());
                String autorizacion = o.getAutorizacion() == null ? "0000000000" : o.getAutorizacion();
                if (!((o.getFacturaelectronica() == null) || (o.getFacturaelectronica().isEmpty()))) {
                    facturaBean.cargar(o.getFacturaelectronica());
//                    puntoEmision = "ELEC";
                    puntoEmision = facturaBean.getFactura().getInfoTributaria().getPtoEmi();
                    tipoComprobante = facturaBean.getFactura().getInfoTributaria().getCodDoc();
                    fechaEmision = facturaBean.getFactura().getFechaEmi();
                    fechaRegistro = facturaBean.getFactura().getFechaEmi();
                }
                Date fechaEmiRet1 = o.getFechar() == null ? o.getFechaemision() : o.getFechar();
                AtsSri.FormasDePago formasDePago = null;
                List<AtsSri.DetalleAir> air = new LinkedList<>();
                Proveedores p = o.getProveedor();
                Empresas e = p.getEmpresa();
                int longitudRuc = e.getRuc().trim().length();
                String tpIdProv = "";
                if (longitudRuc == 10) {
                    tpIdProv = "02";
                } else if (longitudRuc == 13) {
                    tpIdProv = "01";
                } else {
                    tpIdProv = "03";
                }
                String idProv = e.getRuc();
                // ver rascompras
                parametros = new HashMap();
//                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put(";where", "o.obligacion=:obligacion and (o.retencion is not null or o.retencionimpuesto is not null)");
                parametros.put("obligacion", o);
                parametros.put(";orden", "o.retencion.id");
//                parametros.put(";campo","sum(o.valorimpuesto),sum(o.valor),sum(o.ret");
                List<Retencionescompras> rLista = ejbRetencionesCompras.encontarParametros(parametros);
                double base = 0;
                double vret = 0;
                Retenciones ret = null;
                for (Retencionescompras r : rLista) {
                    if (ret != r.getRetencion()) {

                        if (ret != null) {
                            DecimalFormat df = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.US));
                            AtsSri.DetalleAir airDetalle = ats.cargaDetalle(df.format(vret),
                                    df.format(base), ret.getEtiqueta() + ret.getAts(), ret.getPorcentaje().toString());
                            air.add(airDetalle);
                        }
                        ret = r.getRetencion();
                        base = 0;
                        vret = 0;
                    }
                    if (r.getBaseimponible() == null) {
                        r.setBaseimponible(BigDecimal.ZERO);
                    }
                    if (r.getBaseimponible0() == null) {
                        r.setBaseimponible0(BigDecimal.ZERO);
                    }
                    vret += r.getValor().doubleValue();
                    base += r.getBaseimponible().doubleValue() + r.getBaseimponible0().doubleValue();
                    baseImponible += r.getBaseimponible0().doubleValue();
                    baseImpGrav += r.getBaseimponible().doubleValue();
//                    montoIva += r.getValoriva().doubleValue();
                    montoIva += r.getIva().doubleValue();
//                    baseImponible+=r.getValor().doubleValue();
//                    if (r.getIva().doubleValue() == 0) {
//                        baseImponible += r.getValor().doubleValue();
//                    } else {
//                        baseImpGrav += r.getValor().doubleValue();
//                        totbasesImpReemb += r.getValor().doubleValue();
//                    }
                    if (r.getRetencionimpuesto() != null) {
                        if (r.getRetencionimpuesto().getPorcentaje().doubleValue() == 10) {
                            // 10%
                            valRetBien10 += r.getValoriva().doubleValue();
                        } else if (r.getRetencionimpuesto().getPorcentaje().doubleValue() == 20) {
                            // 20%
                            valRetServ20 += r.getValoriva().doubleValue();
                        } else if (r.getRetencionimpuesto().getPorcentaje().doubleValue() == 30) {
                            // 30%
                            valorRetBienes += r.getValoriva().doubleValue();
                        } else if (r.getRetencionimpuesto().getPorcentaje().doubleValue() == 50) {
                            // 50%
                            valorRetServ50 += r.getValoriva().doubleValue();
                        } else if (r.getRetencionimpuesto().getPorcentaje().doubleValue() == 70) {
                            // 70% 
                            valorRetServicios += r.getValoriva().doubleValue();
                        } else if (r.getRetencionimpuesto().getPorcentaje().doubleValue() == 100) {
                            // 100%
                            valRetServ100 += r.getValoriva().doubleValue();
                        }

                    }// fin ce

                    // crear el air
                }// fin ras
                //Reembolsos
                totbasesImpReemb = 0;
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", o);
                parametros.put(";orden", "o.id");

                List<Reembolsos> listaReembolsos = new LinkedList<>();
                listaReembolsos = ejbReembolsos.encontarParametros(parametros);
                for (Reembolsos r : listaReembolsos) {
                    if (r.getBaseimponible() == null) {
                        r.setBaseimponible(BigDecimal.ZERO);
                    }
                    if (r.getBaseimponible0() == null) {
                        r.setBaseimponible0(BigDecimal.ZERO);
                    }
                    if (r.getValoriva() == null) {
                        r.setValoriva(BigDecimal.ZERO);
                    }
                    totbasesImpReemb += (r.getBaseimponible().doubleValue() + r.getBaseimponible0().doubleValue() + r.getValoriva().doubleValue());
                }

                if (ret != null) {
                    DecimalFormat df = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.US));
                    AtsSri.DetalleAir airDetalle = ats.cargaDetalle(df.format(vret),
                            df.format(base), ret.getEtiqueta() + ret.getAts(), ret.getPorcentaje().toString());
                    air.add(airDetalle);
                }
                existe = true;
                obli++;
                ats.cargaCompras(valRetBien10, codSustento,
                        secuencial, idProv, fechaEmiRet1,
                        secRetencion1, valorRetBienes, puntoEmision,
                        fechaRegistro, establecimiento, baseImpExe,
                        montoIva, autorizacion, baseImponible, valRetServ100,
                        parteRel, valorRetServicios,
                        valRetServ20, tpIdProv, baseNoGraIva,
                        estabRetencion1, ptoEmiRetencion1, formasDePago,
                        tipoComprobante, montoIce, totbasesImpReemb,
                        fechaEmision, autRetencion1, air, baseImpGrav,
                        valorRetServ50, listaReembolsos, o
                );

            }
            MensajesErrores.informacion("No registros =" + obli);
            if (existe) {
                String xml = ats.toString();
                String directorio = configuracionBean.getConfiguracion().getDirectorio();//"/home/edwin/Escritorio/comprobantes/";
//                File archivParaEmail = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xml");
//                File archivParaEmail = grabarXml(directorio + "/ats/",
//                        "ATS-" + String.valueOf(anio) + String.valueOf(mes) + "",
//                        xml);
                File archivParaEmail = grabarXml(xml);
                FacesContext fc = FacesContext.getCurrentInstance();
                ExternalContext ec = fc.getExternalContext();
                archivoAnexo = new Recurso(Files.readAllBytes(archivParaEmail.toPath()));
//                archivoAnexo = new anexoRecurso(ec, "ATS-" + String.valueOf(anio) + String.valueOf(mes) + Calendar.getInstance().getTimeInMillis() + ".xml", Files.readAllBytes(archivParaEmail.toPath()));
                MensajesErrores.informacion("Archivo generado corecctamente en : " + directorio + " Archivo : " + "ATS-" + String.valueOf(anio) + String.valueOf(mes) + ".xml");
                formularioReporte.insertar();
            } else {
                MensajesErrores.advertencia("No existen datos para generar");
            }
        } catch (ConsultarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private File grabarXml(String xml) {

        BufferedWriter writer = null;
        File archivoRetorno = null;
        try {
            Calendar c = Calendar.getInstance();
            archivoRetorno = File.createTempFile("" + Calendar.getInstance().getTimeInMillis(), ".xml");;
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

    /**
     * @return the archivoAnexo
     */
    public Resource getArchivoAnexo() {
        return archivoAnexo;
    }

    /**
     * @param archivoAnexo the archivoAnexo to set
     */
    public void setArchivoAnexo(Resource archivoAnexo) {
        this.archivoAnexo = archivoAnexo;
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
     * @return the formulario103
     */
    public Resource getFormulario103() {
        return formulario103;
    }

    /**
     * @param formulario103 the formulario103 to set
     */
    public void setFormulario103(Resource formulario103) {
        this.formulario103 = formulario103;
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

    public String grabarEnHoja() {
        try {
            Documento103 hoja = new Documento103();
            hoja.ponerMes(mes);
            hoja.ponerAnio(anio);
            hoja.ponerRuc(configuracionBean.getConfiguracion().getRuc());
            hoja.ponerRazon(configuracionBean.getConfiguracion().getNombre());
            double valorRmu = traerValorRmu();
            double valorRetenido = traerValorRet();
            hoja.ponerRMU(valorRmu, valorRetenido);

            // renglones
            Calendar primerDia = Calendar.getInstance();
            Calendar ultimoDia = Calendar.getInstance();
            primerDia.set(anio, mes - 1, 1);
            ultimoDia.set(anio, mes, 1);
            ultimoDia.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
            String where = "o.obligacion.fechar between :desde and :hasta";
            parametros.put(";where", where);
            parametros.put("desde", primerDia.getTime());
            parametros.put("hasta", ultimoDia.getTime());
            parametros.put(";suma", "sum(o.baseimponible+o.baseimponible0),sum(o.iva),sum(o.valoriva),sum(o.valor)");
            parametros.put(";campo", "o.retencion.etiqueta");
            List<Object[]> listaResultados = ejbRetencionesCompras.sumar(parametros);
            for (Object[] r : listaResultados) {
                String aux = (String) r[0];
                BigDecimal valor = (BigDecimal) r[1];
                BigDecimal valorRetencion = (BigDecimal) r[4];
                hoja.ponerConcepto(aux, valor.doubleValue(), valorRetencion.doubleValue());
            }
            double valor332 = traerValor332();
            hoja.ponerConcepto("332", valor332, 0);
            //
            formulario103 = hoja.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir() {
        try {
            DecimalFormat df = new DecimalFormat("00");
            DocumentoPDF pdf = new DocumentoPDF("LISTADO DE RETENCIONES\n" + "MES: " + df.format(mes) + " AÑO :" + df.format(anio), parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Factura"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nro. Ret."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Retenido"));
            // asrenglones
            Calendar primerDia = Calendar.getInstance();
            Calendar ultimoDia = Calendar.getInstance();
            primerDia.set(anio, mes - 1, 1);
            ultimoDia.set(anio, mes, 1);
            ultimoDia.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
            String where = "o.impuesto=false";
            parametros.put(";where", where);
            parametros.put(";orden", "o.etiqueta");
            List<Retenciones> listaRetenciones = ejbRetenciones.encontarParametros(parametros);
            double total = 0;
            double totalRet = 0;
            for (Retenciones r : listaRetenciones) {
                System.out.println(r.toString());
                if (r.getId() == 20) {
                    int x = 0;
                }
                if (r.getId() == 21) {
                    int x = 0;
                }
                double totalInt = 0;
                double totalIntRet = 0;
                parametros = new HashMap();
                parametros.put("desde", primerDia.getTime());
                parametros.put("hasta", ultimoDia.getTime());
                parametros.put("retencion", r);
                parametros.put(";orden", "o.obligacion.fechar asc");
                parametros.put(";where", "o.obligacion.fechar between :desde and :hasta and o.obligacion.estado=2 "
                        //                        + "and o.retencion=:retencion ");
                        //                        + "and o.retencion=:retencion  and  o.obligacion.tipodocumento.codigo='FACT'");
                        + "and o.retencion=:retencion  and  o.obligacion.tipodocumento.parametros!='00'");
                List<Retencionescompras> listaRas = ejbRetencionesCompras.encontarParametros(parametros);
                columnas = new LinkedList<>();
                boolean entra = false;
                for (Retencionescompras c : listaRas) {
                    entra = true;
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getFechar()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
                    String estable = c.getObligacion().getEstablecimiento();
                    String punt = c.getObligacion().getPuntoemision();
                    String doc;
                    if (c.getObligacion().getDocumento() == null) {
                        doc = "";
                    } else {
                        doc = c.getObligacion().getDocumento() + "";
                    }
                    String factura;
                    if (estable == null && punt == null && doc == null) {
                        factura = "";
                    } else {
                        factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() + "-" + c.getObligacion().getDocumento();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
                    String numeror = "";
                    if (c.getObligacion().getNumeror() == null) {
                        numeror = "***";
                    } else {
                        numeror = c.getObligacion().getNumeror().toString();
                    }
                    String retencioNo
                            = c.getObligacion().getEstablecimientor() + "-"
                            + c.getObligacion().getPuntor() + "-"
                            + numeror;
//                    String retencioNo = c.getObligacion().getEstablecimientor() + "-" + c.getObligacion().getPuntor() + "-" + c.getObligacion().getNumeror().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, retencioNo));
                    double valor = c.getBaseimponible() == null ? 0 : c.getBaseimponible().doubleValue();
                    valor += c.getBaseimponible0() == null ? 0 : c.getBaseimponible0().doubleValue();
                    double valorRet = c.getValor().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRet));
                    total += valor;
                    totalInt += valor;
                    totalRet += valorRet;
                    totalIntRet += valorRet;
                }
                if (entra) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL " + r.getNombre().toUpperCase()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalInt));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntRet));
                    pdf.agregarTablaReporte(titulos, columnas, 7, 100, r.getEtiqueta() + " - " + r.getNombre());
                    pdf.agregaParrafo("\n\n");
                }
            }
            // imprimir Total
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL RETENCIONES EN LA FUENTE"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalRet));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "TOTAL RETENCIONES EN LA FUENTE");
            pdf.agregaParrafo("\n\n");
            // 332
//            parametros = new HashMap();
//            where = "o.obligacion.fechar between :desde and :hasta "
//                    + "and o.retencion is null";
////                    + "and o.retencion is null and   o.obligacion.tipodocumento.codigo='FACT'";
//            parametros.put(";where", where);
//            parametros.put("desde", primerDia.getTime());
//            parametros.put("hasta", ultimoDia.getTime());
//            List<Retencionescompras> listaRas = ejbRetencionesCompras.encontarParametros(parametros);
            double totalInt = 0;
            double totalIntRet = 0;
//            columnas = new LinkedList<>();
//            for (Retencionescompras c : listaRas) {
//                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getFechar()));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
//                String factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() +"-"+ c.getObligacion().getDocumento().toString();
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
//                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
//                String retencioNo = c.getObligacion().getEstablecimientor() + "-" + c.getObligacion().getPuntor() +"-"+ c.getObligacion().getNumeror().toString();
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retencioNo));
//                double valor = c.getValor().doubleValue();
//                double valorRet = 0;
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRet));
//                total += valor;
//                totalInt += valor;
//                totalRet += valorRet;
//                totalIntRet += valorRet;
//            }
//            if (totalInt != 0) {
//                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL RETENCION 0%"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalInt));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntRet));
//                pdf.agregarTablaReporte(titulos, columnas, 7, 100, "332 - RETENCION 0%");
//                pdf.agregaParrafo("\n\n");
//            }
            // fin 332
            double totalTotal = total;
            double totalRetTotal = totalRet;
            total = 0;
            totalRet = 0;
            // retenciones del iva
            parametros = new HashMap();
            where = "o.impuesto=true";
            parametros.put(";where", where);
            listaRetenciones = ejbRetenciones.encontarParametros(parametros);
            pdf.agregaParrafo("\n\n");
            for (Retenciones r : listaRetenciones) {
                totalInt = 0;
                totalIntRet = 0;
                parametros = new HashMap();
                parametros.put("desde", primerDia.getTime());
                parametros.put("hasta", ultimoDia.getTime());
                parametros.put("retencion", r);
                parametros.put(";orden", "o.obligacion.fechar asc");
                parametros.put(";where", "o.obligacion.fechar between :desde and :hasta and o.obligacion.estado=2 "
                        //                        + "and o.retencionimpuesto=:retencion");
                        //                        + "and o.retencionimpuesto=:retencion  and  o.obligacion.tipodocumento.codigo='FACT'");
                        + "and o.retencionimpuesto=:retencion  and  o.obligacion.tipodocumento.parametros!='00'");
                List<Retencionescompras> listaRas = ejbRetencionesCompras.encontarParametros(parametros);
                columnas = new LinkedList<>();
                boolean entra = false;
                for (Retencionescompras c : listaRas) {
                    entra = true;
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getFechar()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
                    String factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() + c.getObligacion().getDocumento();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
                    String numeror = "";
                    if (c.getObligacion().getNumeror() == null) {
                        numeror = "***";
                    } else {
                        numeror = c.getObligacion().getNumeror().toString();
                    }
                    String retencioNo
                            = c.getObligacion().getEstablecimientor() + "-"
                            + c.getObligacion().getPuntor() + "-"
                            + numeror;
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, retencioNo));
                    double valor = c.getIva() == null ? 0 : c.getIva().doubleValue();
                    double valorRet = c.getValoriva().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRet));
                    total += valor;
                    totalInt += valor;
                    totalRet += valorRet;
                    totalIntRet += valorRet;
                }
                if (entra) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL " + r.getNombre().toUpperCase()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalInt));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntRet));
                    pdf.agregarTablaReporte(titulos, columnas, 7, 100, r.getEtiqueta() + " - " + r.getNombre());
                    pdf.agregaParrafo("\n\n");
                }
            }
            // Toatal retenciones iva
            // imprimir Total
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL RETENCIONES IVA"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalRet));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "TOTAL RETENCIONES IVA");
            pdf.agregaParrafo("\n\n");

//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL "));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalRet));
//            pdf.agregarTablaReporte(titulos, columnas, 7, 100, null);
            reporte = pdf.traerRecurso();
            formularioImprimir.insertar();
        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private double traerValorRmu() {
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.mes=:mes";
        where += " and o.concepto is null and o.prestamo is null and o.sancion is null";
        parametros.put(";campo", "o.valor+o.cantidad+o.encargo");
        parametros.put(";where", where);
        parametros.put("anio", anio);
        parametros.put("mes", mes);
        double valor = 0;
        try {
            valor = ejbPagosEmpleados.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }
        parametros = new HashMap();
        where = "  o.concepto.codigo in ('HEFINSEMANA','HELUNVIER','HECODLAB','HESUPLE') and o.mes=:mes and o.anio=:anio";
        parametros.put(";where", where);
        parametros.put("anio", anio);
        parametros.put("mes", mes);
        try {
            valor += ejbPagosEmpleados.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valor;
    }

    private double traerValorRet() {
        Map parametros = new HashMap();
        double valor = 0;
        String where = "  o.concepto.codigo in ('IMPRENT') and o.mes=:mes and o.anio=:anio";
        parametros.put(";where", where);
        parametros.put("anio", anio);
        parametros.put("mes", mes);
        try {
            valor += ejbPagosEmpleados.sumarCampoDoble(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valor;
    }

    private double traerValor332() {
        Map parametros = new HashMap();
        Calendar primerDia = Calendar.getInstance();
        Calendar ultimoDia = Calendar.getInstance();
        primerDia.set(anio, mes - 1, 1);
        ultimoDia.set(anio, mes, 1);
        ultimoDia.add(Calendar.DATE, -1);
        double valor = 0;
        String where = "o.obligacion.fechar between :desde and :hasta "
                + "and o.retencion is null and  o.obligacion.tipodocumento.codigo='FACT'";
        parametros.put(";where", where);
        parametros.put("desde", primerDia.getTime());
        parametros.put("hasta", ultimoDia.getTime());
        try {
            valor += ejbRetencionesCompras.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valor;
    }

    public String hojaElectronica() {

        try {

            /*FM 26 DIC 2018*/
            DecimalFormat df = new DecimalFormat("00");
            DocumentoXLS xls = new DocumentoXLS("Retenciones");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Factura"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base imponible"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base imponible 0"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Impuesto"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Impuesto"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nro. Ret."));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Ret. Fuente"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Ret. Impuesto"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total retención"));
            xls.agregarFila(titulos, true);
// asrenglones
            Calendar primerDia = Calendar.getInstance();
            Calendar ultimoDia = Calendar.getInstance();
            primerDia.set(anio, mes - 1, 1);
            ultimoDia.set(anio, mes, 1);
            ultimoDia.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
            String where = "o.impuesto=false";
            parametros.put(";where", where);
            parametros.put(";orden", "o.etiqueta");
            List<Retenciones> listaRetenciones = ejbRetenciones.encontarParametros(parametros);
            double total = 0;
            double totalRet = 0;
            for (Retenciones r : listaRetenciones) {
                double totalInt = 0;
                double totalIntRet = 0;
                parametros = new HashMap();
                parametros.put("desde", primerDia.getTime());
                parametros.put("hasta", ultimoDia.getTime());
                parametros.put("retencion", r);
                parametros.put(";where", "o.obligacion.fechar between :desde and :hasta and o.retencion=:retencion  and  o.obligacion.tipodocumento.parametros!='00'");
                parametros.put(";orden", "o.obligacion.fechar asc");
                List<Retencionescompras> listaRas = ejbRetencionesCompras.encontarParametros(parametros);
                boolean entra = false;
                for (Retencionescompras c : listaRas) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    entra = true;
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(c.getObligacion().getFechar())));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
                    String factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() + "-" + c.getObligacion().getDocumento().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
                    /*FM 26 DIC 2018*/

                    if (c.getBaseimponible() != null) {
                        double baseImponible = c.getBaseimponible().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, baseImponible));
                    }

                    if (c.getBaseimponible0() != null) {
                        double baseImponible0 = c.getBaseimponible0().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, baseImponible0));
                    }

                    if (c.getValor() != null) {
                        double valor = c.getValor().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    }
                    String nombreImpuesto = "";
                    if (c.getImpuesto() != null) {

                        nombreImpuesto = c.getImpuesto().getNombre();
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, nombreImpuesto));

                    } else {
                        nombreImpuesto = "-----";
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, nombreImpuesto));
                    }

                    if (c.getIva() != null) {
                        double valorIva = c.getIva().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorIva));
                    }

                    if (c.getIva() != null && c.getValor() != null) {
                        double valorTotal = c.getValor().doubleValue() + c.getIva().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                    }

                    if (c.getObligacion().getNumeror() == null) {
                        int x = 0;
                    }
                    String retencioNo = c.getObligacion().getEstablecimientor() + "-" + c.getObligacion().getPuntor() + "-"
                            + c.getObligacion().getNumeror() == null ? "NA" : c.getObligacion().getNumeror().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retencioNo));

                    double valor = c.getValor().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));

                    double valorRet = c.getValoriva().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRet));
                    double valorRetTotal = c.getValor().doubleValue() + c.getValoriva().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRetTotal));

                    total += valor;
                    totalInt += valor;
                    totalRet += valorRet;
                    totalIntRet += valorRet;
                    xls.agregarFila(columnas, false);
                }
                if (entra) {
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL "
                            + r.getNombre().toUpperCase()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalInt));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntRet));
                    xls.agregarFila(columnas, false);
                }
            }
            // imprimir Total
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL RETENCIONES EN LA FUENTE"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalRet));
            xls.agregarFila(columnas, false);
            double totalInt = 0;
            double totalIntRet = 0;
            double totalTotal = total;
            double totalRetTotal = totalRet;
            total = 0;
            totalRet = 0;
            // retenciones del iva
            parametros = new HashMap();
            where = "o.impuesto=true";
            parametros.put(";where", where);
            parametros.put(";orden", "o.etiqueta");
            listaRetenciones = ejbRetenciones.encontarParametros(parametros);

            for (Retenciones r : listaRetenciones) {
                totalInt = 0;
                totalIntRet = 0;
                parametros = new HashMap();
                parametros.put("desde", primerDia.getTime());
                parametros.put("hasta", ultimoDia.getTime());
                parametros.put("retencion", r);
                parametros.put(";where", "o.obligacion.fechar between :desde and :hasta "
                        + "and o.retencionimpuesto=:retencion  and  o.obligacion.tipodocumento.parametros!='00'");
                parametros.put(";orden", "o.obligacion.fechar asc");
                List<Retencionescompras> listaRas = ejbRetencionesCompras.encontarParametros(parametros);
                boolean entra = false;
                for (Retencionescompras c : listaRas) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    entra = true;
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(c.getObligacion().getFechar())));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
                    String factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() + "-" + c.getObligacion().getDocumento().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
                    /*FM 26 DIC 2018*/

                    if (c.getBaseimponible() != null) {
                        double baseImponible = c.getBaseimponible().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, baseImponible));
                    }
                    if (c.getBaseimponible0() != null) {
                        double baseImponible0 = c.getBaseimponible0().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, baseImponible0));
                    }
                    if (c.getValor() != null) {
                        double valor = c.getValor().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    }

                    if (c.getImpuesto() != null) {
                        String nombreImpuesto = c.getImpuesto().getNombre();
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, nombreImpuesto));
                    }

                    if (c.getIva() != null) {
                        double valorIva = c.getIva().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorIva));
                    }

                    if (c.getIva() != null && c.getValor() != null) {
                        double valorTotal = c.getValor().doubleValue() + c.getIva().doubleValue();
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                    }

                    if (c.getObligacion().getNumeror() == null) {
                        int x = 0;
                    }
                    String retencioNo = c.getObligacion().getEstablecimientor() + "-" + c.getObligacion().getPuntor() + "-"
                            + c.getObligacion().getNumeror() == null ? "NA" : c.getObligacion().getNumeror().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retencioNo));

                    double valor = c.getValor().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));

                    double valorRet = c.getValoriva().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRet));
                    double valorRetTotal = c.getValor().doubleValue() + c.getValoriva().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorRetTotal));

                    xls.agregarFila(columnas, false);
                }
            }
            // Toatal retenciones iva
            // imprimir Total
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL RETENCIONES IVA"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalRet));
            xls.agregarFila(columnas, false);

//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL "));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalRet));
//            pdf.agregarTablaReporte(titulos, columnas, 7, 100, null);
            setReporteXls(xls.traerRecurso());
            getFormularioReporteXls().insertar();
        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the formularioReporteXls
     */
    public Formulario getFormularioReporteXls() {
        return formularioReporteXls;
    }

    /**
     * @param formularioReporteXls the formularioReporteXls to set
     */
    public void setFormularioReporteXls(Formulario formularioReporteXls) {
        this.formularioReporteXls = formularioReporteXls;
    }

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }
}
