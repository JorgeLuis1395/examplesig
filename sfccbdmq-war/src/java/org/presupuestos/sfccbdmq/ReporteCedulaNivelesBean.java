/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCedulaNivelesSfccbdmq")
@ViewScoped
public class ReporteCedulaNivelesBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaNivelesBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();

    private Formulario formularioXls = new Formulario();
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private PagosvencimientosFacade ejbPagos;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private CodigosFacade ejbCodigos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{reporteDetalleComp}")
    private ReporteDetalleCompBean reporteDetalleCompBean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private boolean ingresos;
    private boolean mensual;
    private String nivel;
    private String digito;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private String titulo4;
    private Codigos fuenteFinanciamiento;
    private Date desde;
    private Date desdeInicio;
    private Date hasta;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private Resource reporte;
    private Resource reporteXls;

    private DocumentoXLS xls;
    private String clasificador;
    /////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////Para sacar la cedula contable////////////////////////
    private List<AuxiliarAsignacion> cedulaContableDevengado;
    private List<AuxiliarAsignacion> cedulaContableDevengadoEjecutado;
    private List<Detallecompromiso> detalleCompromiso;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean partidasBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;

    public String buscar() {
        ///////LLENADO de la cedula contable
//        for (AuxiliarAsignacion a : cedulaContableDevengado) {
//            System.out.println(a.getCodigo() + "\t" + a.getValor() + "\t" + a.getAvance());
//        }
        ////////////////////////////////////
//        llenarSinfuente();

        Calendar c = Calendar.getInstance();
        c.setTime(desde);
        int anio = c.get(Calendar.YEAR);
        Calendar fechaParaInicio = Calendar.getInstance();
        fechaParaInicio.set(anio, 0, 1);
        desdeInicio = fechaParaInicio.getTime();
        if (anio != 2018) {
            traerDetalleCompromiso(desdeInicio, hasta);
        }

        llenarSinfuenteXls();
//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Cédula consolidada : " + configuracionBean.getConfiguracion().getExpresado());
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        parametros.put("titulo1", getTitulo1());
//        parametros.put("titulo2", getTitulo2());
//        parametros.put("titulo3", getTitulo3());
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Cedula.jasper"),
//                asignaciones, "Cedula" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        return null;
    }

    public String generarPdf() {
        llenarSinfuenteXls();
        return null;
    }

    private void llenarSinfuenteXls() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(desde);
        int anio = cal.get(Calendar.YEAR);
        Calendar fechaParaInicio = Calendar.getInstance();
        fechaParaInicio.set(anio, 0, 1);
        desdeInicio = fechaParaInicio.getTime();
        try {

            xls = new DocumentoXLS("Cédula");
            totalEgresos = 0;
            totalIngresos = 0;
            Calendar ca = Calendar.getInstance();
            ca.setTime(desde);
            Calendar diaAntesDesde = Calendar.getInstance();
            diaAntesDesde.setTime(desde);
            diaAntesDesde.add(Calendar.DATE, -1);
            anio = ca.get(Calendar.YEAR);
            int mes = ca.get(Calendar.MONTH) + 1;
            Calendar cDesde = Calendar.getInstance();
            Calendar cHasta = Calendar.getInstance();

            if (mensual) {
                cHasta.setTime(hasta);
                int mes1 = cHasta.get(Calendar.MONTH);
                cDesde.set(anio, mes1, 1);
                cHasta.set(anio, mes1 + 1, 1);
                cHasta.add(Calendar.DATE, -1);
                hasta = cHasta.getTime();
            } else {
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
            }
            String nivelBuscar = nivel;
            if (!((digito == null) || (digito.isEmpty()))) {
                nivelBuscar = digito + nivel.substring(1);
            }
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
//            reporte
            if (ingresos) {
                titulo1 = "Devengado (f)";
                titulo2 = "Saldo por Devengar (a-f)";
                titulo3 = "Saldo por Recaudadar (a-g)";
                titulo4 = "Recaudado (g)";
            } else {
                titulo1 = "Devengado (f) ";
                titulo2 = "Saldo por Devengar (a-f) ";
                titulo3 = "Saldo por pagar (a-g)";
                titulo4 = "Pagado (g)";

            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> columnas = new LinkedList<>();
            int totalCol = 0;
            if (ingresos) {
                // ingresos
                if (mensual) {

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA MENSUAL DE INGRESOS\n MES :" + mes);
// pocas columans preferible vertical
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    totalCol = 5;
                    xls.agregarFila(titulos, true);
                } else {

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta));
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR DEVENGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR RECAUDADAR"));
                    totalCol = 9;
                    xls.agregarFila(titulos, true);
                }

            } else {
                // egresos
                if (mensual) {

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA MENSUAL DE GASTOS\n MES :" + mes);
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    totalCol = 7;
                    xls.agregarFila(titulos, true);
                } else {

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta));
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACIONES"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO CERTIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR DEVENGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR PAGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO DISPONIBLE"));
                    totalCol = 14;
                    xls.agregarFila(titulos, true);
                }
            }
//            fin reporte
            proyectos = new LinkedList<>();
            Map parametros = new HashMap();
            asignaciones = new LinkedList<>();
//            if (!fuente) {
//                parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso" );
//                parametros.put("codigo", digito + "%");
//            } else {
            String where = " o.ingreso=:ingreso";
            parametros.put("ingreso", ingresos);

            if (!((digito == null) || (digito.isEmpty()))) {
                where += " and o.codigo like :codigo";
                parametros.put("codigo", digito + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.codigo like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
//            parametros.put("codigo", digito + "%");
//            }
//            parametros.put("codigo", digito + "%");
//            parametros.put("longitud", nivel.length());
//            parametros.put("codigo", nivelBuscar);

            parametros.put(";where", where);
            parametros.put(";orden", "o.codigo");

            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            List<Clasificadores> cl1 = new LinkedList<>();
            for (Clasificadores c : cl) {
                if (fuente) {
                    if (c.getCodigo().length() <= nivel.length()) {
                        cl1.add(c);
                    }
                } else {
                    if (c.getCodigo().length() == nivel.length()) {
                        cl1.add(c);
                    }
                }
            }
            AuxiliarAsignacion aTotal = new AuxiliarAsignacion();
            aTotal.setTitulo2("*****  TOTAL *****");
            aTotal.setNombre("TOTAL");
            for (Clasificadores c : cl1) {
                columnas = new LinkedList<>();
                if (c.getIngreso()) {
                    setTitulo1("Devengado (f)");
                    setTitulo2("Saldo por Devengar (a-f)");
                    setTitulo3("Saldo por recaudar (a-g)");
                } else {
                    setTitulo1("Devengado (f) ");
                    setTitulo2("Saldo por Devengar (a-f) ");
                    setTitulo3("Saldo por pagar (a-g)");
                }
                // sumar asignaciones inicias
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(c.getCodigo());
                a.setNombre(c.getNombre());
                // Asignacion Inicial

                double valorAsignacion = calculosBean.traerAsignaciones(anio, null, c.getCodigo(), fuenteFinanciamiento);
                // reformas anteriores
                // Reformas
                a.setAsignacion(valorAsignacion);
                // Reformas
//                    double valorReformas = calculosBean.traerReformas(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    a.setReforma(valorReformas);

                double valorReformas = calculosBean.traerReformas(null, c.getCodigo(), desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                a.setReforma(valorReformas);
                // certificaciones
                // primmera columa de sumas
                a.setCodificado(valorAsignacion + valorReformas);
                //Certificaciones
//                    double valorCertificaciones = calculosBean.traerCertificaciones(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                double valorCertificaciones = calculosBean.traerCertificaciones(null, c.getCodigo(), desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                double valorCom = calculosBean.traerCompromisosCertificacion(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);

                a.setCertificacion(valorCertificaciones);
                // segundo campo sumado
                a.setCertificado(a.getCodificado() - valorCertificaciones - valorCom);
                double valorCompromiso = calculosBean.traerCompromisos(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setCompromiso(valorCompromiso);
                a.setComprometido(a.getCodificado() - valorCompromiso);
                double valorDevengado = 0;
                double valorEjecutado = 0;
                if (c.getIngreso()) {

                    valorDevengado = calculosBean.traerDevengadoIngresosVista(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), null);
                    valorEjecutado = calculosBean.traerEjecutadoIngresosVista(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), null);

                    // traer lo cobrado en ingresos
                    a.setDevengado(valorDevengado);
                    // saldo por devengar
                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);
                    a.setEjecutado(valorEjecutado);
                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                } else {
//                    valorDevengado = valorDevengadoPartida(c.getCodigo());
                    valorDevengado = calculosBean.traerDevengado(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
// devengado
                    a.setDevengado(valorDevengado);
                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);

                    valorEjecutado = 0;
                    Calendar canio = Calendar.getInstance();
                    canio.setTime(desde);
                    int anioLocal = canio.get(Calendar.YEAR);
                    if (anioLocal == 2018) {
                        valorEjecutado = calculosBean.traerejecutado2018(null, c.getCodigo());
                    } else {
                        valorEjecutado = valorEjecutado(c.getCodigo());
                    }
//                         valorDevengado = calculosBean.traerDevengado(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), null);
//                         valorEjecutado = calculosBean.traerEjecutado(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), null);

                    // saldo por ejecutar
                    a.setEjecutado(valorEjecutado);
                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                }
                a.setSaldoCompromiso(a.getCodificado() - valorCertificaciones - valorCompromiso);
                // ejecutado
                if (a.getAsignacion() != 0 || a.getReforma() != 0) {
                    asignaciones.add(a);
                    filaReporteXls(columnas, a);
                }
//                if (c.getCodigo().length() == 1) {
                aTotal.setAsignacion(aTotal.getAsignacion() + a.getAsignacion());
                aTotal.setCertificacion(aTotal.getCertificacion() + a.getCertificacion());
                aTotal.setCertificado(aTotal.getCertificado() + a.getCertificado());
                aTotal.setCodificado(aTotal.getCodificado() + a.getCodificado());
                aTotal.setComprometido(aTotal.getComprometido() + a.getComprometido());
                aTotal.setCompromiso(aTotal.getCompromiso() + a.getCompromiso());
                aTotal.setDevengado(aTotal.getDevengado() + a.getDevengado());
                aTotal.setReforma(aTotal.getReforma() + a.getReforma());
                aTotal.setValor(aTotal.getValor() + a.getValor());
                aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
                aTotal.setSaldoEjecutado(aTotal.getSaldoEjecutado() + a.getSaldoEjecutado());
                aTotal.setSaldoDevengado(aTotal.getSaldoDevengado() + a.getSaldoDevengado());
                aTotal.setSaldoCompromiso(aTotal.getSaldoCompromiso() + a.getSaldoCompromiso());
//                }
            }
            asignaciones.add(aTotal);
            filaReporteXls(columnas, aTotal);
            reporteXls = xls.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaNivelesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filaReporteXls(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        filas = new LinkedList<>();
        if (ingresos) {
            // ingresos
            if (mensual) {
                // pocas columans preferible vertical
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));

            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));

            }

        } else {
            // egresos
            if (mensual) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));

            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));

            }
        }
        xls.agregarFila(filas, false);
    }

//    private void llenarSinfuente() {
//        desdeInicio = configuracionBean.getConfiguracion().getPinicialpresupuesto();
//        desdeInicio = desde;
//        totalEgresos = 0;
//        totalIngresos = 0;
//        Calendar ca = Calendar.getInstance();
//        ca.setTime(desde);
//        Calendar diaAntesDesde = Calendar.getInstance();
//        diaAntesDesde.setTime(desde);
//        diaAntesDesde.add(Calendar.DATE, -1);
//        anio = ca.get(Calendar.YEAR);
//        int mes = ca.get(Calendar.MONTH) + 1;
//        Calendar cDesde = Calendar.getInstance();
//        Calendar cHasta = Calendar.getInstance();
//
//        if (mensual) {
//            cHasta.setTime(hasta);
//            int mes1 = cHasta.get(Calendar.MONTH);
//            cDesde.set(anio, mes1, 1);
//            cHasta.set(anio, mes1 + 1, 1);
//            cHasta.add(Calendar.DATE, -1);
//            hasta = cHasta.getTime();
//        } else {
//            cDesde.setTime(desde);
//            cHasta.setTime(hasta);
//        }
//        String nivelBuscar = nivel;
//        if (!((digito == null) || (digito.isEmpty()))) {
//            nivelBuscar = digito + nivel.substring(1);
//        }
//        try {
////            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
////            reporte
//            if (ingresos) {
//                titulo1 = "Devengado (f)";
//                titulo2 = "Saldo por Devengar (a-f)";
//                titulo3 = "Saldo por Recaudadar (a-g)";
//                titulo4 = "Recaudado (g)";
//            } else {
//                titulo1 = "Devengado (f) ";
//                titulo2 = "Saldo por Devengar (a-f) ";
//                titulo3 = "Saldo por pagar (a-g)";
//                titulo4 = "Pagado (g)";
//
//            }
//            DocumentoPDF pdf;
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            List<AuxiliarReporte> titulos = new LinkedList<>();
//            List<AuxiliarReporte> columnas = new LinkedList<>();
//            int totalCol = 0;
//            if (ingresos) {
//                // ingresos
//                if (mensual) {
//                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE INGRESOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
//                    // pocas columans preferible vertical
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
//                    totalCol = 5;
//                } else {
//                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR DEVENGAR"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR RECAUDADAR"));
//                    totalCol = 9;
//                }
//
//            } else {
//                // egresos
//                if (mensual) {
//                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE GASTOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
//                    totalCol = 7;
//                } else {
//                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
//                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACIONES"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO CERTIFICADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO COMPROMISOS"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR DEVENGAR"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR PAGAR"));
//                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO DISPONIBLE"));
//                    totalCol = 14;
//                }
//            }
////            fin reporte
//            proyectos = new LinkedList<>();
//            Map parametros = new HashMap();
//            asignaciones = new LinkedList<>();
////            if (!fuente) {
////                parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso" );
////                parametros.put("codigo", digito + "%");
////            } else {
//            String where = " o.ingreso=:ingreso";
//            parametros.put("ingreso", ingresos);
//
//            if (!((digito == null) || (digito.isEmpty()))) {
//                where += " and o.codigo like :codigo";
//                parametros.put("codigo", digito + "%");
//            }
//            if (!((clasificador == null) || (clasificador.isEmpty()))) {
//                where += " and o.codigo like :clasificador";
//                parametros.put("clasificador", clasificador + "%");
//            }
////            parametros.put("codigo", digito + "%");
////            }
////            parametros.put("codigo", digito + "%");
////            parametros.put("longitud", nivel.length());
////            parametros.put("codigo", nivelBuscar);
//
//            parametros.put(";where", where);
//            parametros.put(";orden", "o.codigo");
//
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
//            List<Clasificadores> cl1 = new LinkedList<>();
//            for (Clasificadores c : cl) {
//                if (fuente) {
//                    if (c.getCodigo().length() <= nivel.length()) {
//                        cl1.add(c);
//                    }
//                } else {
//                    if (c.getCodigo().length() == nivel.length()) {
//                        cl1.add(c);
//                    }
//                }
//            }
//            AuxiliarAsignacion aTotal = new AuxiliarAsignacion();
//            aTotal.setTitulo2("*****  TOTAL *****");
//            aTotal.setNombre("TOTAL");
//            for (Clasificadores c : cl1) {
//                if (c.getIngreso()) {
//                    setTitulo1("Devengado (f)");
//                    setTitulo2("Saldo por Devengar (a-f)");
//                    setTitulo3("Saldo por Recaudadar (a-g)");
//                } else {
//                    setTitulo1("Devengado (f) ");
//                    setTitulo2("Saldo por Devengar (a-f) ");
//                    setTitulo3("Saldo por pagar (a-g)");
//                }
//                // sumar asignaciones inicias
//                AuxiliarAsignacion a = new AuxiliarAsignacion();
//                a.setCodigo(c.getCodigo());
//                a.setNombre(c.getNombre());
//                // Asignacion Inicial
//
//                double valorAsignacion = calculosBean.traerAsignaciones(anio, null, c.getCodigo(), fuenteFinanciamiento);
//                // reformas anteriores
//                // Reformas
//                a.setAsignacion(valorAsignacion);
//                // Reformas
////                double valorReformas = calculosBean.traerReformas(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
////                a.setReforma(valorReformas);
//
//                double valorReformas = calculosBean.traerReformas(null, c.getCodigo(), desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
//                a.setReforma(valorReformas);
//
//                // certificaciones
//                // primmera columa de sumas 
//                a.setCodificado(valorAsignacion + valorReformas);
//                //Certificaciones
////                double valorCertificaciones = calculosBean.traerCertificaciones(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                double valorCertificaciones = calculosBean.traerCertificaciones(null, c.getCodigo(), desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
//                a.setCertificacion(valorCertificaciones);
//                // segundo campo sumado
//                a.setCertificado(a.getCodificado() - valorCertificaciones);
//                double valorCompromiso = calculosBean.traerCompromisos(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                a.setCompromiso(valorCompromiso);
//                a.setComprometido(a.getCodificado() - valorCompromiso);
//                if (c.getIngreso()) {
//                    // traer lo cobrado en ingresos
////                    double valorDevengado = calculosBean.traerDevengadoIngresos(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    double valorDevengado = valorDevengadoPartida(c.getCodigo());
////                    double valorDevengado = ejbIngresos.sumarCampo(parametros).doubleValue();
//                    a.setDevengado(valorDevengado);
//                    // saldo por devengar
//                    if (c.getCodigo().equals("380101")) {
//                        int x = 0;
//                    }
//                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);
////                    double valorEjecutado = calculosBean.traerIngresos(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    double valorEjecutado = valorEjecutadoPartida(c.getCodigo());
//                    a.setEjecutado(-99999999999.99);
////                    a.setEjecutado(valorEjecutado);
//                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
//                } else {
//                    // devengado
//                    double valorDevengado = valorDevengadoPartida(c.getCodigo());//calculosBean.traerDevengado(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
////                    double valorDevengado = calculosBean.traerDevengado(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    a.setDevengado(valorDevengado);
//                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);
//                    double valorEjecutado = valorEjecutadoPartida(c.getCodigo());
////                    double valorEjecutado = calculosBean.traerEjecutado(null, c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    a.setEjecutado(valorEjecutado);
//                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
//                }
//                a.setSaldoCompromiso(a.getCodificado() - valorCertificaciones - valorCompromiso);
//                // ejecutado
//                asignaciones.add(a);
//
//                filaReporte(columnas, a);
////                if (c.getCodigo().length() == 1) {
//                aTotal.setAsignacion(aTotal.getAsignacion() + a.getAsignacion());
//                aTotal.setCertificacion(aTotal.getCertificacion() + a.getCertificacion());
//                aTotal.setCertificado(aTotal.getCertificado() + a.getCertificado());
//                aTotal.setCodificado(aTotal.getCodificado() + a.getCodificado());
//                aTotal.setComprometido(aTotal.getComprometido() + a.getComprometido());
//                aTotal.setCompromiso(aTotal.getCompromiso() + a.getCompromiso());
//                aTotal.setDevengado(aTotal.getDevengado() + a.getDevengado());
////                    aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
//                aTotal.setReforma(aTotal.getReforma() + a.getReforma());
//                aTotal.setValor(aTotal.getValor() + a.getValor());
//                aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
//                aTotal.setSaldoEjecutado(aTotal.getSaldoEjecutado() + a.getSaldoEjecutado());
//                aTotal.setSaldoDevengado(aTotal.getSaldoDevengado() + a.getSaldoDevengado());
//                aTotal.setSaldoCompromiso(aTotal.getSaldoCompromiso() + a.getSaldoCompromiso());
////                }
//
//            }
//            asignaciones.add(aTotal);
//            filaReporte(columnas, aTotal);
//            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
//            reporte = pdf.traerRecurso();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaNivelesBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException | DocumentException ex) {
//            Logger.getLogger(ReporteCedulaNivelesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private void filaReporte(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        if (ingresos) {
            // ingresos
            if (mensual) {
                // pocas columans preferible vertical
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
            }

        } else {
            // egresos
            if (mensual) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoCompromiso()));
            }
        }
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        desdeInicio = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteNivelesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
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

    public void cambiaFuente(ValueChangeEvent event) {
        // cambia el texto de la cedula
        fuente = (boolean) event.getNewValue();
        buscar();
    }

    public SelectItem[] getComboNiveles() {
        try {
            // el primer numero de clasificador 
            Codigos codigo = getCodigosBean().traerCodigo(Constantes.NOMBRE_NIVEL_CUENTAS, "02");
            if (codigo == null) {
                MensajesErrores.advertencia("Configure el nombre de los niveles de cuentas Maestro:" + Constantes.NOMBRE_NIVEL_CUENTAS + " Código :02");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            String digito = "1";
            for (Clasificadores c : cl) {
                digito = c.getCodigo();
            }
            Codigos primerNivel = ejbCodigos.traerCodigo("FRMCLA", digito);
            String formato = primerNivel.getDescripcion().replace(".", "#");
            String[] sinpuntos = formato.split("#");
            int size = sinpuntos.length;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            String[] auxCodigo = codigo.getParametros().split("#");
//            i++;
            String que = "";
            int j = 0;
            String totalL = "";
            for (String x : sinpuntos) {
                String longitud = "";
                que += x;

                longitud = que.replace("X", "_");
                items[i++] = new SelectItem(longitud, auxCodigo[j++]);

            }
            return items;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaNivelesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // buscar en clasificacion y crearlos si no existen
        return null;
    }

    public String getTitulo() {
        String titulo = "CEDULA PRESUPUESTARIA DE GASTOS";
        if (ingresos) {
            titulo = "CEDULA PRESUPUESTARIA DE INGRESOS";
        }
        return titulo;
    }

    private void traerDetalleCompromiso(Date desde, Date hasta) {
        getReporteDetalleCompBean().setDesde(desdeInicio);
        getReporteDetalleCompBean().setHasta(hasta);
        getReporteDetalleCompBean().buscar();
        setDetalleCompromiso(getReporteDetalleCompBean().getListadoDetallecomprom());
    }

    private double valorEjecutado(String partida) {
        double retorno = 0;
        if (partida == null || partida.isEmpty()) {
            retorno = 0;
        }
        if (nivel.equals("_")) {
            for (Detallecompromiso dc : detalleCompromiso) {
                String partidaBuscar = dc.getAsignacion().getClasificador().getCodigo().substring(0, 1);
                if (partida.equals(partidaBuscar)) {
                    retorno += dc.getValorEjecutado();
                }
            }
            return retorno;
        } else {
            if (nivel.equals("__")) {
                for (Detallecompromiso dc : detalleCompromiso) {
                    String partidaBuscar = dc.getAsignacion().getClasificador().getCodigo().substring(0, 2);
                    if (partida.equals(partidaBuscar)) {
                        retorno += dc.getValorEjecutado();
                    }
                }
                return retorno;
            } else {
                if (nivel.equals("____")) {
                    for (Detallecompromiso dc : detalleCompromiso) {
                        String partidaBuscar = dc.getAsignacion().getClasificador().getCodigo().substring(0, 4);
                        if (partida.equals(partidaBuscar)) {
                            retorno += dc.getValorEjecutado();
                        }
                    }
                    return retorno;
                } else {
                    if (nivel.equals("______")) {
                        for (Detallecompromiso dc : detalleCompromiso) {
                            String partidaBuscar = dc.getAsignacion().getClasificador().getCodigo().substring(0, 6);
                            if (partida.equals(partidaBuscar)) {
                                retorno += dc.getValorEjecutado();
                            }
                        }
                        return retorno;
                    }
                }
            }
        }
        return retorno;
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
     * @return the totalIngresos
     */
    public double getTotalIngresos() {
        return totalIngresos;
    }

    /**
     * @param totalIngresos the totalIngresos to set
     */
    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    /**
     * @return the totalEgresos
     */
    public double getTotalEgresos() {
        return totalEgresos;
    }

    /**
     * @param totalEgresos the totalEgresos to set
     */
    public void setTotalEgresos(double totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    /**
     * @return the fuente
     */
    public boolean isFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(boolean fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the proyectos
     */
    public List<Auxiliar> getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(List<Auxiliar> proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * @return the nivel
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the ingresos
     */
    public boolean isIngresos() {
        return ingresos;
    }

    /**
     * @param ingresos the ingresos to set
     */
    public void setIngresos(boolean ingresos) {
        this.ingresos = ingresos;
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
     * @return the digito
     */
    public String getDigito() {
        return digito;
    }

    /**
     * @param digito the digito to set
     */
    public void setDigito(String digito) {
        this.digito = digito;
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
     * @return the titulo1
     */
    public String getTitulo1() {
        return titulo1;
    }

    /**
     * @param titulo1 the titulo1 to set
     */
    public void setTitulo1(String titulo1) {
        this.titulo1 = titulo1;
    }

    /**
     * @return the titulo2
     */
    public String getTitulo2() {
        return titulo2;
    }

    /**
     * @param titulo2 the titulo2 to set
     */
    public void setTitulo2(String titulo2) {
        this.titulo2 = titulo2;
    }

    /**
     * @return the titulo3
     */
    public String getTitulo3() {
        return titulo3;
    }

    /**
     * @param titulo3 the titulo3 to set
     */
    public void setTitulo3(String titulo3) {
        this.titulo3 = titulo3;
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
     * @return the calculosBean
     */
    public CalulosPresupuestoBean getCalculosBean() {
        return calculosBean;
    }

    /**
     * @param calculosBean the calculosBean to set
     */
    public void setCalculosBean(CalulosPresupuestoBean calculosBean) {
        this.calculosBean = calculosBean;
    }

    /**
     * @return the fuenteFinanciamiento
     */
    public Codigos getFuenteFinanciamiento() {
        return fuenteFinanciamiento;
    }

    /**
     * @param fuenteFinanciamiento the fuenteFinanciamiento to set
     */
    public void setFuenteFinanciamiento(Codigos fuenteFinanciamiento) {
        this.fuenteFinanciamiento = fuenteFinanciamiento;
    }

    /**
     * @return the mensual
     */
    public boolean isMensual() {
        return mensual;
    }

    /**
     * @param mensual the mensual to set
     */
    public void setMensual(boolean mensual) {
        this.mensual = mensual;
    }

    /**
     * @return the titulo4
     */
    public String getTitulo4() {
        return titulo4;
    }

    /**
     * @param titulo4 the titulo4 to set
     */
    public void setTitulo4(String titulo4) {
        this.titulo4 = titulo4;
    }

    /**
     * @return the formularioXls
     */
    public Formulario getFormularioXls() {
        return formularioXls;
    }

    /**
     * @param formularioXls the formularioXls to set
     */
    public void setFormularioXls(Formulario formularioXls) {
        this.formularioXls = formularioXls;
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

    /**
     * @return the desdeInicio
     */
    public Date getDesdeR() {
        return desdeInicio;
    }

    /**
     * @param desdeInicio the desdeInicio to set
     */
    public void setDesdeR(Date desdeInicio) {
        this.desdeInicio = desdeInicio;
    }

    /**
     * @return the clasificador
     */
    public String getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(String clasificador) {
        this.clasificador = clasificador;
    }

    /**
     * @return the partidasBean
     */
    public ClasificadorBean getPartidasBean() {
        return partidasBean;
    }

    /**
     * @param partidasBean the partidasBean to set
     */
    public void setPartidasBean(ClasificadorBean partidasBean) {
        this.partidasBean = partidasBean;
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
     * @return the asignaciones
     */
    public List<AuxiliarAsignacion> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<AuxiliarAsignacion> asignaciones) {
        this.asignaciones = asignaciones;
    }

    /**
     * @return the reporteDetalleCompBean
     */
    public ReporteDetalleCompBean getReporteDetalleCompBean() {
        return reporteDetalleCompBean;
    }

    /**
     * @param reporteDetalleCompBean the reporteDetalleCompBean to set
     */
    public void setReporteDetalleCompBean(ReporteDetalleCompBean reporteDetalleCompBean) {
        this.reporteDetalleCompBean = reporteDetalleCompBean;
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
}
