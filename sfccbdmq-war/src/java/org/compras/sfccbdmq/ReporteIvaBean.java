/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import org.contabilidad.sfccbdmq.Formulario107Bean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Documento104;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.FacturaSriBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.beans.sfccbdmq.ImpuestosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Retenciones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reproteIvaSfccbdmq")
@ViewScoped
public class ReporteIvaBean implements Serializable {

    private static final long serialVersionUID = 1L;

//    private LazyDataModel<Rascompras> listadoRascompras;
    private LazyDataModel<Retencionescompras> listadoRascompras;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private String tipoFecha = "o.inicio";
    private Date desde;
    private Date hasta;
    private int mes;
    private int anio;
    private Impuestos impuesto;
    private Retenciones retencion;
    private Retenciones retencionImpuesto;
    private int tipo = -1;
    private double totalRascompras;
    private Resource archivoAnexo;
    private Resource formulario104;
    private Resource reporte;
    @EJB
    private ImpuestosFacade ejbImpuestos;
//    @EJB
//    private RascomprasFacade ejbRasCompras;
    @EJB
    private RetencionescomprasFacade ejbRetencionescompras;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
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
        String nombreForma = "ReporteIvaVista";
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

    public ReporteIvaBean() {

//        listadoRascompras = new LazyDataModel<Rascompras>() {
        listadoRascompras = new LazyDataModel<Retencionescompras>() {

            @Override
//            public List<Rascompras> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
            public List<Retencionescompras> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }

        };
    }

    public String buscar() {

        totalRascompras = 0;

//        setListadoRascompras(new LazyDataModel<Rascompras>() {
        setListadoRascompras(new LazyDataModel<Retencionescompras>() {
            @Override
//            public List<Rascompras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
            public List<Retencionescompras> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Calendar primerDia = Calendar.getInstance();
                Calendar ultimoDia = Calendar.getInstance();
                primerDia.set(anio, mes - 1, 1);
                ultimoDia.set(anio, mes, 1);
                ultimoDia.add(Calendar.DATE, -1);
                try {
                    Map parametros = new HashMap();
                    String where = "o.obligacion.fechaemision"
                            + " between :desde and :hasta and (o.impuesto is not null) ";
                    if (retencion != null) {
                        where += " and o.retencion=:retencion";
                        parametros.put("retencion", retencion);

                    }
                    if (retencionImpuesto != null) {
                        where += " and o.retencionimpuesto=:retencionimpuesto";
                        parametros.put("retencionimpuesto", retencionImpuesto);

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
                    parametros.put("desde", primerDia.getTime());
                    parametros.put("hasta", ultimoDia.getTime());
//                    parametros.put(";suma", "sum(o.valor),sum(o.valorimpuesto),sum(o.vretencionimpuesto),sum(o.valorret)");
                    parametros.put(";suma", "sum(o.baseimponible),sum(o.baseimponible0),sum(o.iva),sum(o.valor),sum(o.valoriva)");
                    List<Object[]> listaResultados = ejbRetencionescompras.sumar(parametros);
                    parametros = new HashMap();
                    where = "o.obligacion.fechaemision between :desde and :hasta "
                            + " and (o.retencion is not null or o.retencionimpuesto is not null)";
                    if (retencion != null) {
                        where += " and o.retencion=:retencion";
                        parametros.put("retencion", retencion);

                    }
                    if (retencionImpuesto != null) {
                        where += " and o.retencionimpuesto=:retencionimpuesto";
                        parametros.put("retencionimpuesto", retencionImpuesto);

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
                    parametros.put(";orden", "o.obligacion.fechaemision asc");
                    parametros.put("desde", primerDia.getTime());
                    parametros.put("hasta", ultimoDia.getTime());
                    int total = ejbRetencionescompras.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListadoRascompras().setRowCount(total + 1);
                    List<Retencionescompras> rlista = ejbRetencionescompras.encontarParametros(parametros);
                    for (Object[] r : listaResultados) {
//                        Rascompras r1 = new Rascompras();
                        Retencionescompras r1 = new Retencionescompras();
                        BigDecimal valor = (BigDecimal) r[0];
                        r1.setBaseimponible(valor);
                        valor = (BigDecimal) r[1];
                        r1.setBaseimponible0(valor);
                        valor = (BigDecimal) r[2];
                        r1.setIva(valor);
                        valor = (BigDecimal) r[3];
                        r1.setValor(valor);
                        valor = (BigDecimal) r[4];
                        r1.setValoriva(valor);
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
        });

        return null;
    }

    public double getValorObligacion() {
        Rascompras o = (Rascompras) getListadoRascompras().getRowData();
        String where = "o.obligacion=:obligacion";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("obligacion", o.getObligacion());
        parametros.put(";campo", "o.baseimponible+o.baseimponible0+o.iva");
        try {
//            return ejbRasCompras.sumarCampo(parametros).doubleValue();
            return ejbRetencionescompras.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteIvaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String grabarEnHoja() {
        try {
            Documento104 hoja = new Documento104();
            hoja.ponerMes(mes);
//            hoja.ponerAnio(anio);
            hoja.ponerConcepto("102", anio, 0);
            hoja.ponerRuc(configuracionBean.getConfiguracion().getRuc());
            hoja.ponerRazon(configuracionBean.getConfiguracion().getNombre());
//            double valorRmu = traerValorRmu();
//            double valorRetenido = traerValorRet();
//            hoja.ponerRMU(valorRmu, valorRetenido);

            // renglones
            Calendar primerDia = Calendar.getInstance();
            Calendar ultimoDia = Calendar.getInstance();
            primerDia.set(anio, mes - 1, 1);
            ultimoDia.set(anio, mes, 1);
            ultimoDia.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
            String where = "o.obligacion.fechaemision between :desde and :hasta and o.impuesto is not null "
                    + " and  o.obligacion.tipodocumento.codigo='FACT'";
//                    + " and o.cba='C' and  o.obligacion.tipodocumento.codigo='FACT'";
            parametros.put(";where", where);
            parametros.put("desde", primerDia.getTime());
            parametros.put("hasta", ultimoDia.getTime());
            parametros.put(";suma", "sum(o.baseimponible),sum(o.baseimponible0),sum(o.iva),sum(o.valor),sum(o.valoriva)");
//            parametros.put(";suma", "sum(o.valor),sum(o.valorimpuesto),sum(o.vretencionimpuesto),sum(o.valorret)");
//            parametros.put(";campo", "o.retencion.etiqueta");
//            List<Object[]> listaResultados = ejbRasCompras.sumar(parametros);
            List<Object[]> listaResultados = ejbRetencionescompras.sumar(parametros);
            for (Object[] r : listaResultados) {
//                String aux = (String) r[0];
                BigDecimal valor = (BigDecimal) r[0];
                BigDecimal valor0 = (BigDecimal) r[1];
                BigDecimal valorRetencion = (BigDecimal) r[2];
                double total = valor.doubleValue() + valor0.doubleValue();
                if (total != 0) {
                    hoja.ponerConcepto("500", valor.doubleValue(), valor.doubleValue());
                }
                if (valorRetencion != null) {
                    hoja.ponerConcepto("520", valorRetencion.doubleValue(), 0);
                }
            }
            // iva 0

            double valorCero = traerValorCero();
            hoja.ponerConcepto("507", valorCero, valorCero);
            // retenciones
            parametros = new HashMap();
            where = "o.obligacion.fechar between :desde and :hasta";
//            where = "o.obligacion.fechaemision between :desde and :hasta";
            parametros.put(";where", where);
            parametros.put("desde", primerDia.getTime());
            parametros.put("hasta", ultimoDia.getTime());
            parametros.put(";suma", "sum(o.valor)");
//            parametros.put(";suma", "sum(o.vretencionimpuesto)");
//            parametros.put(";suma", "sum(o.valor),sum(o.valorimpuesto),sum(o.vretencionimpuesto),sum(o.valorret)");
            parametros.put(";campo", "o.retencionimpuesto.formulario");
//            listaResultados = ejbRasCompras.sumar(parametros);
            listaResultados = ejbRetencionescompras.sumar(parametros);
            for (Object[] r : listaResultados) {
                String aux = (String) r[0];
                BigDecimal valor = (BigDecimal) r[1];
//                BigDecimal valorRetencion = (BigDecimal) r[1];
                hoja.ponerConcepto(aux, valor.doubleValue(), 0);
            }
            //
            formulario104 = hoja.traerRecurso();
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
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nro. Fact."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Impuesto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
            // asrenglones
            Calendar primerDia = Calendar.getInstance();
            Calendar ultimoDia = Calendar.getInstance();
            primerDia.set(anio, mes - 1, 1);
            ultimoDia.set(anio, mes, 1);
            ultimoDia.add(Calendar.DATE, -1);
            Map parametros = new HashMap();
            String where = "o.activo=true";
            parametros.put(";where", where);
            parametros.put(";orden", "o.etiqueta");
            List<Impuestos> listaImpuestos = ejbImpuestos.encontarParametros(parametros);
            double total = 0;
            double totalImp = 0;
            for (Impuestos i : listaImpuestos) {
                double totalInt = 0;
                double totalIntImp = 0;
                parametros = new HashMap();
                parametros.put("desde", primerDia.getTime());
                parametros.put("hasta", ultimoDia.getTime());
                parametros.put("impuesto", i);
                parametros.put(";where", "o.obligacion.fechaemision between :desde and :hasta "
                        + "and o.impuesto=:impuesto and  o.obligacion.tipodocumento.codigo='FACT'");
//                        + "and o.impuesto=:impuesto and o.cba='C' and  o.obligacion.tipodocumento.codigo='FACT'");
//                List<Rascompras> listaRas = ejbRasCompras.encontarParametros(parametros);
                List<Retencionescompras> listaRas = ejbRetencionescompras.encontarParametros(parametros);
                columnas = new LinkedList<>();
//                for (Rascompras c : listaRas) {
                for (Retencionescompras c : listaRas) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getFechaemision()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
                    String factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() + c.getObligacion().getDocumento().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
                    String retencioNo = c.getObligacion().getEstablecimientor() + "-" + c.getObligacion().getPuntor() + c.getObligacion().getNumeror().toString();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retencioNo));
//                    double valor = c.getValor().doubleValue();
//                    double valorImp = c.getValorimpuesto().doubleValue();
                    double valor = c.getBaseimponible().doubleValue() + c.getBaseimponible0().doubleValue();
                    double valorImp = c.getIva().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorImp));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorImp + valor));
                    total += valor;
                    totalInt += valor;
                    totalImp += valorImp;
                    totalIntImp += valorImp;
                }
                if (totalInt != 0) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL " + i.getNombre().toUpperCase()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalInt));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntImp));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntImp + total));
                    pdf.agregarTablaReporte(titulos, columnas, 8, 100, i.getCodigo() + " - " + i.getNombre());
                    pdf.agregaParrafo("\n\n");
                }
            }
            // imprimir Total
//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
//            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL IMPUESTOS"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalImp));
//            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalImp+total));
//            pdf.agregarTablaReporte(titulos, columnas,8, 100, "TOTAL IMPUESTOS");
//            pdf.agregaParrafo("\n\n");
//            // 332
            parametros = new HashMap();
            where = "o.obligacion.fechar between :desde and :hasta "
                    + "and o.impuesto is null and  o.obligacion.tipodocumento.codigo='FACT'";
//                    + "and o.impuesto is null and o.cba='C' and  o.obligacion.tipodocumento.codigo='FACT'";
            parametros.put(";where", where);
            parametros.put("desde", primerDia.getTime());
            parametros.put("hasta", ultimoDia.getTime());
//            List<Rascompras> listaRas = ejbRasCompras.encontarParametros(parametros);
            List<Retencionescompras> listaRas = ejbRetencionescompras.encontarParametros(parametros);
            double totalInt = 0;
            double totalIntImp = 0;
            columnas = new LinkedList<>();
//            for (Rascompras c : listaRas) {
            for (Retencionescompras c : listaRas) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getFechar()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getProveedor().getEmpresa().toString()));
                String factura = c.getObligacion().getEstablecimiento() + "-" + c.getObligacion().getPuntoemision() + c.getObligacion().getDocumento().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, factura));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getObligacion().getConcepto()));
                String retencioNo = c.getObligacion().getEstablecimientor() + "-" + c.getObligacion().getPuntor() + c.getObligacion().getNumeror().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, retencioNo));
//                double valor = c.getValor().doubleValue();
                double valor = c.getBaseimponible().doubleValue() + c.getBaseimponible0().doubleValue();
                double valorImp = 0;
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorImp));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor + valorImp));
                total += valor;
                totalInt += valor;
                totalImp += valorImp;
                totalIntImp += valorImp;
            }
            if (totalInt != 0) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL IMPUESTO 0%"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalInt));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntImp));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalIntImp + totalInt));
                pdf.agregarTablaReporte(titulos, columnas, 8, 100, "IMPUESTO 0%");
                pdf.agregaParrafo("\n\n");
            }
            // fin 332
            // Toatal retenciones iva
            // imprimir Total
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL IMPUESTOS"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalImp));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, totalImp + total));
            pdf.agregarTablaReporte(titulos, columnas, 8, 100, "TOTAL IMPUESTOS");
            pdf.agregaParrafo("\n\n");

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
            valor = ejbPagosEmpleados.sumarCampoFloat(parametros);
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
            valor += ejbPagosEmpleados.sumarCampoDoble(parametros);
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

    private double traerValorCero() {
        Map parametros = new HashMap();
        Calendar primerDia = Calendar.getInstance();
        Calendar ultimoDia = Calendar.getInstance();
        primerDia.set(anio, mes - 1, 1);
        ultimoDia.set(anio, mes, 1);
        ultimoDia.add(Calendar.DATE, -1);
        double valor = 0;
        String where = "o.obligacion.fechaemision between :desde and :hasta "
                + "and o.impuesto is null and  o.obligacion.tipodocumento.codigo='FACT'";
//                + "and o.impuesto is null and o.cba='C' and  o.obligacion.tipodocumento.codigo='FACT'";
//        String where = "o.obligacion.fechar between :desde and :hasta "
//                + "and o.retencion is null and o.cba='C' and  o.obligacion.tipodocumento.codigo='FACT'";
        parametros.put(";where", where);
        parametros.put("desde", primerDia.getTime());
        parametros.put("hasta", ultimoDia.getTime());
        try {
//            valor += ejbRasCompras.sumarCampo(parametros).doubleValue();
            valor += ejbRetencionescompras.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Formulario107Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valor;
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

//    /**
//     * @return the listadoRascompras
//     */
//    public LazyDataModel<Rascompras> getListadoRascompras() {
//        return listadoRascompras;
//    }
//
//    /**
//     * @param listadoRascompras the listadoRascompras to set
//     */
//    public void setListadoRascompras(LazyDataModel<Rascompras> listadoRascompras) {
//        this.listadoRascompras = listadoRascompras;
//    }
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
     * @return the totalRascompras
     */
    public double getTotalRascompras() {
        return totalRascompras;
    }

    /**
     * @param totalRascompras the totalRascompras to set
     */
    public void setTotalRascompras(double totalRascompras) {
        this.totalRascompras = totalRascompras;
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
    public Resource getFormulario104() {
        return formulario104;
    }

    /**
     * @param formulario104 the formulario103 to set
     */
    public void setFormulario104(Resource formulario104) {
        this.formulario104 = formulario104;
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

    /**
     * @return the listadoRascompras
     */
    public LazyDataModel<Retencionescompras> getListadoRascompras() {
        return listadoRascompras;
    }

    /**
     * @param listadoRascompras the listadoRascompras to set
     */
    public void setListadoRascompras(LazyDataModel<Retencionescompras> listadoRascompras) {
        this.listadoRascompras = listadoRascompras;
    }
}
