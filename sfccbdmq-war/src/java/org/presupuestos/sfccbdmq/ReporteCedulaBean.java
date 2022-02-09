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
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.model.chart.CartesianSeries;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCedulaSfccbdmq")
@ViewScoped
public class ReporteCedulaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    @ManagedProperty(value = "#{reporteDetalleComp}")
    private ReporteDetalleCompBean reporteDetalleCompBean;

    private Formulario formulario = new Formulario();

    private List<AuxiliarAsignacion> asignaciones;
    private List<Auxiliar> proyectos;
    private List<Detallecompromiso> detalleCompromiso;
    private Perfiles perfil;
    private int ingegrtodos;
    private int anio;

    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private boolean mensual;
    private String proyecto;
    private String partida;
    private Codigos fuenteFinanciamiento;
    private Date desde;
    private Date desdeInicio;
    private Date hasta;
    private AuxiliarAsignacion aSuperTotal;
    private String nivel;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private String titulo4;

    private CartesianSeries datosBarras;
    private Resource reporte;
    private Resource reporteXls;
    private DocumentoXLS xls;

    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private CodigosFacade ejbCodigos;

    public String buscar() {
        Calendar c = Calendar.getInstance();
        c.setTime(desde);
        int anio = c.get(Calendar.YEAR);
        Calendar fechaParaInicio = Calendar.getInstance();
        fechaParaInicio.set(anio, 0, 1);
        desdeInicio = fechaParaInicio.getTime();
        if (anio != 2018) {
            traerDetalleCompromiso(desdeInicio, hasta);
        }
        llenar();
        hojaElectronica();
//        llenarSinfuente();

        return null;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteCedulaVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
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
        setFuente((boolean) event.getNewValue());
        buscar();
    }

    public Integer getCuentasContables() {
        AuxiliarAsignacion a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        String where = " upper(o.presupuesto) like :presupuesto";
        parametros.put("presupuesto", a.getCodigo().toUpperCase() + "%");
//        parametros.put("codigo", a.getCodigo() + "%");
//        parametros.put(";campo", "o.valor");
        parametros.put(";where", where);
        try {
            return ejbCuentas.contar(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public SelectItem[] getComboNiveles() {
        try {
            // el primer numero de clasificador 
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
            items[i++] = new SelectItem(null, "--- Seleccione un nivel ---");
//            i++;
            String que = "";
            String totalL = "";
            for (String x : sinpuntos) {
                String longitud = "";
                que += x;

                longitud = que.replace("X", "_");
                items[i++] = new SelectItem(longitud, que);

            }
            return items;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaNivelesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // buscar en clasificacion y crearlos si no existen
        return null;
    }

    private void llenar() {
        totalEgresos = 0;
        totalIngresos = 0;
        try {
            asignaciones = new LinkedList<>();
            List<AuxiliarAsignacion> listaAsig = new LinkedList<>();
            Calendar ca = Calendar.getInstance();
            aSuperTotal = new AuxiliarAsignacion();
            ca.setTime(hasta);
            anio = ca.get(Calendar.YEAR);
            int mes = ca.get(Calendar.MONTH);
            proyectos = new LinkedList<>();
            Calendar cDesde = Calendar.getInstance();
            Calendar cHasta = Calendar.getInstance();
            if (mensual) {
                cDesde.set(anio, mes, 1);
                cHasta.set(anio, mes + 1, 1);
                cHasta.add(Calendar.DATE, -1);
            } else {
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
            }
            Map parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
            String where;
            if (ingegrtodos == 1) {
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
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if ((proyecto == null) || (proyecto.isEmpty())) {
                    where = "o.proyecto.anio=:anio and o.proyecto.ingreso=:ingreso";
                    parametros.put("ingreso", ing);
                } else {
                    where = "o.proyecto.anio=:anio and upper(o.proyecto.codigo) like :codigo and o.proyecto.ingreso=:ingreso";
                    parametros.put("codigo", proyecto + "%");
                    parametros.put("ingreso", ing);
                }
            } else {
                if ((proyecto == null) || (proyecto.isEmpty())) {
                    where = "o.proyecto.anio=:anio";
                } else {
                    where = "o.proyecto.anio=:anio and upper(o.proyecto.codigo) like :codigo ";
                    parametros.put("codigo", proyecto + "%");
                }
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and  upper(o.clasificador.codigo) like :partida";
                parametros.put("partida", partida + "%");
            }
            if (fuenteFinanciamiento != null) {
                where += " and  o.fuente =:fuente";
                parametros.put("fuente", fuenteFinanciamiento);
            }
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                if (ingegrtodos == 1) {
                    where += " and  upper(o.proyecto.codigo) like :proyecto and o.proyecto.ingreso=true";
                    parametros.put("proyecto", proyecto + "%");
                } else {
                    where += " and  upper(o.proyecto.codigo) like :proyecto and o.proyecto.ingreso=false";
                    parametros.put("proyecto", proyecto + "%");
                }
            }
            int anioLocal = 0;
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.proyecto.codigo,o.clasificador.codigo");
            parametros.put(";suma", "sum(o.valor)");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);
//            List<Asignaciones> listaAisgnaciones = ejbAsignaciones.encontarParametros(parametros);
            for (Object[] o : listaAgrupados) {
//            for (Asignaciones a : listaAisgnaciones) {
//                int signo = a.getProyecto().getIngreso() ? 1 : -1;

                String codProyecto = (String) o[0];
                String codClasificador = (String) o[1];
                BigDecimal valor = (BigDecimal) o[2];
                AuxiliarAsignacion aProyecto = new AuxiliarAsignacion();
                Calendar c = Calendar.getInstance();
                c.setTime(desde);
                anioLocal = c.get(Calendar.YEAR);
                Proyectos p = proyectosBean.traerCodigoAnio(codProyecto, anioLocal);
                aProyecto.setCodigo(codProyecto);
                aProyecto.setNombre("");
                aProyecto.setTipo("ASIGNACION");
                aProyecto.setTitulo1(codClasificador);
                aProyecto.setTitulo2("");
                aProyecto.setFuente("");
                double asignacion = valor.doubleValue();
                aProyecto.setAsignacion(asignacion);
                double reforma = calculosBean.
                        traerReformas(codProyecto, codClasificador, desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                aProyecto.setReforma(reforma);
                aProyecto.setCodificado(asignacion + reforma);
                //                                traerCertificaciones(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                double certficicaciones = calculosBean.traerCertificaciones(codProyecto, codClasificador, desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                double valorCom = calculosBean.traerCompromisosCertificacion(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aProyecto.setCertificacion(certficicaciones);
                aProyecto.setCertificado(aProyecto.getCodificado() - certficicaciones - valorCom);
                double compromisos = calculosBean.
                        traerCompromisos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                aProyecto.setCompromiso(compromisos);
                aProyecto.setComprometido(aProyecto.getCodificado() - compromisos);
                if (p == null) {
                    MensajesErrores.advertencia("error");
                    return;
                } else {
                    if (p.getIngreso()) {
//                        double valorDevengado = calculosBean.traerDevengadoIngresos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                        aProyecto.setDevengado(valorDevengado);
//                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
//                        double valorEjecutado = calculosBean.traerIngresos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                        aProyecto.setEjecutado(valorEjecutado);
//                        aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        String fuenteS = null;
                        if (fuenteFinanciamiento != null) {
                            fuenteS = fuenteFinanciamiento.getCodigo();
                        }
                        double valorDevengado = calculosBean.traerDevengadoIngresosVista(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteS);
                        aProyecto.setDevengado(valorDevengado);
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
                        double valorEjecutado = calculosBean.traerEjecutadoIngresosVista(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteS);
                        aProyecto.setEjecutado(valorEjecutado);
                        aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                    } else {
                        double valorDevengado = calculosBean.traerDevengado(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        aProyecto.setDevengado(valorDevengado);
                        // saldo por devengar
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
                        double valorEjecutado = 0;
                        if (anioLocal == 2018) {
                            String codigo = codProyecto.substring(0, 10);
                            valorEjecutado = calculosBean.traerejecutado2018(codigo, codClasificador);
                            aProyecto.setEjecutado(valorEjecutado);
                            aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        } else {

//                            valorEjecutado = calculosBean.traerEjecutado(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                            valorEjecutado = valorEjecutado(codProyecto, codClasificador);
                            aProyecto.setEjecutado(valorEjecutado);
                            aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        }
                    }
                    double vslorc = aProyecto.getCodificado() - certficicaciones - compromisos;
                    aProyecto.setSaldoCompromiso(aProyecto.getCodificado() - certficicaciones - compromisos);
                    // ver el ejecutado que debe ser igual al devengado
                    listaAsig.add(aProyecto);
                    aSuperTotal.setAsignacion(aProyecto.getAsignacion() + aSuperTotal.getAsignacion());
                    aSuperTotal.setCertificacion(aProyecto.getCertificacion() + aSuperTotal.getCertificacion());
                    aSuperTotal.setCertificado(aProyecto.getCertificado() + aSuperTotal.getCertificado());
                    aSuperTotal.setCodificado(aProyecto.getCodificado() + aSuperTotal.getCodificado());
                    aSuperTotal.setComprometido(aProyecto.getComprometido() + aSuperTotal.getComprometido());
                    aSuperTotal.setCompromiso(aProyecto.getCompromiso() + aSuperTotal.getCompromiso());
                    aSuperTotal.setDevengado(aProyecto.getDevengado() + aSuperTotal.getDevengado());
                    aSuperTotal.setSaldoDevengado(aProyecto.getSaldoDevengado() + aSuperTotal.getSaldoDevengado());
                    aSuperTotal.setReforma(aProyecto.getReforma() + aSuperTotal.getReforma());
                    if (anioLocal == 2018) {
                        aSuperTotal.setEjecutado(calculosBean.traerejecutado2018Valor());
                    } else {
                        aSuperTotal.setEjecutado(aProyecto.getEjecutado() + aSuperTotal.getEjecutado());
                    }
                    aSuperTotal.setSaldoEjecutado(aProyecto.getSaldoEjecutado() + aSuperTotal.getSaldoEjecutado());
                    aSuperTotal.setValor(aProyecto.getValor() + aSuperTotal.getValor());
                    aSuperTotal.setSaldoCompromiso(aProyecto.getSaldoCompromiso() + aSuperTotal.getSaldoCompromiso());
                }
            }

            datosBarras = new CartesianSeries() {
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    setType(CartesianSeries.CartesianType.BAR);
                    setPointLabels(true);
                    add("Asignación", aSuperTotal.getAsignacion());
                    add("Reformas", aSuperTotal.getReforma());
                    add("Asignación Codificada", aSuperTotal.getCodificado());
                    add("Certificación", aSuperTotal.getCertificacion());
                    add("Saldo Certificación", aSuperTotal.getCertificado());
                    add("Compromisos", aSuperTotal.getCompromiso());
                    add("Saldo Compromisos", aSuperTotal.getComprometido());
                    add("Devengado", aSuperTotal.getDevengado());
                    add("Saldo por devengar", aSuperTotal.getSaldoDevengado());
                    add("Pagado", aSuperTotal.getEjecutado());
                    add("Saldo por pagar", aSuperTotal.getSaldoEjecutado());
                    add("Saldo disponible", aSuperTotal.getSaldoCompromiso());
                    setLabel("Presupuesto Desde : " + sdf.format(desde) + " Hasta : " + sdf.format(hasta));
                }
            };
            // traer los proyectos y traer los clasificadores
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if ((proyecto == null) || (proyecto.isEmpty())) {
//                if ((proyectosBean.getProyectoSeleccionado()== null) ) {
                    parametros.put(";where", "o.anio=:anio and o.ingreso=:ingreso");
                    parametros.put("ingreso", ing);
                } else {
                    parametros.put(";where", "o.anio=:anio and upper(o.codigo) like :codigo and o.ingreso=:ingreso");
                    parametros.put("codigo", proyecto + "%");
//                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
                    parametros.put("ingreso", ing);
                }
            } else {
                if ((proyecto == null) || (proyecto.isEmpty())) {
//                if ((proyectosBean.getProyectoSeleccionado()== null)) {
                    parametros.put(";where", "o.anio=:anio");
                } else {
                    parametros.put(";where", "o.anio=:anio and upper(o.codigo) like :codigo ");
                    parametros.put("codigo", proyecto + "%");
//                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
                }
            }
            parametros.put("anio", anio);

            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            // traer los clasificadores
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            where = "";
            if (!((partida == null) || (partida.isEmpty()))) {
                where = " upper(o.codigo) like :partida";
                parametros.put("partida", partida + "%");

            }
            if (!fuente) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.imputable=true";
            }
            if (!where.isEmpty()) {
                parametros.put(";where", where);
            }
            // poner las cabeceras y el inicio del reporte
            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> columnas = new LinkedList<>();
            int totalCol = 0;
            if (ingegrtodos == 1) {
                // ingresos
                if (mensual) {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE INGRESOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), null, seguridadbean.getLogueado().getUserid());
                    // pocas columans preferible vertical
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROD / PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    totalCol = 5;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROD / PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR DEVENGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR RECAUDADAR"));
                    totalCol = 9;
                }

            } else {
                // egresos
                if (mensual) {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE GASTOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROD / PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    totalCol = 7;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROD / PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO CERTIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR DEVENGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR PAGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO DISPONIBLE"));
                    totalCol = 14;
                }
            }

//            asignaciones = listaAsig;
            asignaciones = new LinkedList<>();
            List<Clasificadores> listaCla = ejbClasificadores.encontarParametros(parametros);
            for (Proyectos p : pl) {
                AuxiliarAsignacion a = colocar(listaAsig, p, null);
                a.setFuente("titulo");
                if (!a.isCero()) {
//                    if (anioLocal == 2018) {
//                        if (p.getCodigo().length() < 10) {
//                            asignaciones.add(a);
//                            filaReporte(columnas, a);
//                        }
//                    } else {
                    System.err.println("Proyecto : " + a.getCodigo());
                    asignaciones.add(a);
                    filaReporte(columnas, a);
//                    }
                    // añadir fila de reporte
                    if (p.getImputable()) {
                        for (Clasificadores c : listaCla) {
                            AuxiliarAsignacion cax = colocar(listaAsig, p, c);
                            String codigo = cax.getCodigo();
                            String nombre = cax.getNombre();
                            cax.setCodigo(cax.getTitulo1());
                            cax.setNombre(cax.getTitulo2());
                            cax.setTitulo1(codigo);
                            cax.setTitulo2(nombre);
                            if (!cax.isCero()) {
                                asignaciones.add(cax);
                                filaReporte(columnas, cax);
                            }
                        }
                    }
                }
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            if (fuenteFinanciamiento != null) {
                parametros.put("expresado", "Cédula Presupuestaria  Fuente de Financiamieto :" + fuenteFinanciamiento.getNombre() + " " + configuracionBean.getConfiguracion().getExpresado());
            } else {
                parametros.put("expresado", "Cédula Presupuestaria : " + configuracionBean.getConfiguracion().getExpresado());
            }
            aSuperTotal.setFuente("total");
            aSuperTotal.setNombre("TOTAL");
            asignaciones.add(aSuperTotal);
            filaReporte(columnas, aSuperTotal);
            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
            reporte = pdf.traerRecurso();
//            parametros.put("nombrelogo", "logo-new.png");
//            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//            parametros.put("titulo1", getTitulo1());
//            parametros.put("titulo2", getTitulo2());
//            parametros.put("titulo3", getTitulo3());
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
//            Calendar c = Calendar.getInstance();
//            reporte = new Reportesds(parametros,
//                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Cedula.jasper"),
//                    asignaciones, "Cedula" + String.valueOf(c.getTimeInMillis()) + ".pdf");

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hojaElectronica() {
        totalEgresos = 0;
        totalIngresos = 0;
        try {
            xls = new DocumentoXLS("Reporte cédula presupuestaria");
            asignaciones = new LinkedList<>();
            List<AuxiliarAsignacion> listaAsig = new LinkedList<>();
            Calendar ca = Calendar.getInstance();
            aSuperTotal = new AuxiliarAsignacion();
            ca.setTime(hasta);
            anio = ca.get(Calendar.YEAR);
            int mes = ca.get(Calendar.MONTH);
            proyectos = new LinkedList<>();
            Calendar cDesde = Calendar.getInstance();
            Calendar cHasta = Calendar.getInstance();
            if (mensual) {
                cDesde.set(anio, mes, 1);
                cHasta.set(anio, mes + 1, 1);
                cHasta.add(Calendar.DATE, -1);
            } else {
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
            }
            Map parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
            String where;
            if (ingegrtodos == 1) {
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
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if ((proyecto == null) || (proyecto.isEmpty())) {
                    where = "o.proyecto.anio=:anio and o.proyecto.ingreso=:ingreso";
                    parametros.put("ingreso", ing);
                } else {
                    where = "o.proyecto.anio=:anio and upper(o.proyecto.codigo) like :codigo and o.proyecto.ingreso=:ingreso";
                    parametros.put("codigo", proyecto + "%");
                    parametros.put("ingreso", ing);
                }
            } else {
                if ((proyecto == null) || (proyecto.isEmpty())) {
                    where = "o.proyecto.anio=:anio";
                } else {
                    where = "o.proyecto.anio=:anio and upper(o.proyecto.codigo) like :codigo ";
                    parametros.put("codigo", proyecto + "%");
                }
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and  upper(o.clasificador.codigo) like :partida";
                parametros.put("partida", partida + "%");
            }
            if (fuenteFinanciamiento != null) {
                where += " and  o.fuente =:fuente";
                parametros.put("fuente", fuenteFinanciamiento);
            }
            int anioLocal = 0;
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.proyecto.codigo,o.clasificador.codigo");
            parametros.put(";suma", "sum(o.valor)");
            List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);
//            List<Asignaciones> listaAisgnaciones = ejbAsignaciones.encontarParametros(parametros);
            for (Object[] o : listaAgrupados) {
//            for (Asignaciones a : listaAisgnaciones) {
//                int signo = a.getProyecto().getIngreso() ? 1 : -1;

                String codProyecto = (String) o[0];
                String codClasificador = (String) o[1];
                BigDecimal valor = (BigDecimal) o[2];
                AuxiliarAsignacion aProyecto = new AuxiliarAsignacion();
                Calendar c = Calendar.getInstance();
                c.setTime(desde);
                anioLocal = c.get(Calendar.YEAR);
                Proyectos p = proyectosBean.traerCodigoAnio(codProyecto, anioLocal);
                aProyecto.setProyectoEntidad(p);
                if (p != null) {
                    aProyecto.setCodigo(codProyecto);
                    aProyecto.setNombre("");
                    aProyecto.setTipo("ASIGNACION");
                    aProyecto.setTitulo1(codClasificador);
                    aProyecto.setTitulo2("");
                    aProyecto.setFuente("");
                    double asignacion = valor.doubleValue();
                    aProyecto.setAsignacion(asignacion);
                    double reforma = calculosBean.
                            //                        traerReformas(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                            traerReformas(codProyecto, codClasificador, desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                    aProyecto.setReforma(reforma);
                    aProyecto.setCodificado(asignacion + reforma);
                    double certficicaciones
                            //                        = calculosBean.traerCertificaciones(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                            = calculosBean.traerCertificaciones(codProyecto, codClasificador, desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                    aProyecto.setCertificacion(certficicaciones);
                    double valorCom = calculosBean.traerCompromisosCertificacion(codProyecto, codClasificador, desdeInicio, cHasta.getTime(), fuenteFinanciamiento);
                    double certificadoValor = aProyecto.getCodificado() - certficicaciones - valorCom;
                    aProyecto.setCertificado(aProyecto.getCodificado() - certficicaciones - valorCom);
                    double compromisos = calculosBean.
                            traerCompromisos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    aProyecto.setCompromiso(compromisos);
                    aProyecto.setComprometido(aProyecto.getCodificado() - compromisos);
                    if (p.getIngreso()) {
//                        double valorDevengado = calculosBean.traerDevengadoIngresos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                        aProyecto.setDevengado(valorDevengado);
//                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
//                        double valorEjecutado = calculosBean.traerIngresos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                        aProyecto.setEjecutado(valorEjecutado);
//                        aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        String fuenteS = null;
                        if (fuenteFinanciamiento != null) {
                            fuenteS = fuenteFinanciamiento.getCodigo();
                        }
                        double valorDevengado = calculosBean.traerDevengadoIngresosVista(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteS);
                        aProyecto.setDevengado(valorDevengado);
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
                        double valorEjecutado = calculosBean.traerEjecutadoIngresosVista(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteS);
                        aProyecto.setEjecutado(valorEjecutado);
                        aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                    } else {
                        double valorDevengado = calculosBean.traerDevengado(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        aProyecto.setDevengado(valorDevengado);
                        // saldo por devengar
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
                        double valorEjecutado = 0;
                        if (anioLocal == 2018) {
                            String codigo = codProyecto.substring(0, 10);
                            valorEjecutado = calculosBean.traerejecutado2018(codigo, codClasificador);
                            aProyecto.setEjecutado(valorEjecutado);
                            aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        } else {

//                            valorEjecutado = calculosBean.traerEjecutado(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                            valorEjecutado = valorEjecutado(codProyecto, codClasificador);
                            aProyecto.setEjecutado(valorEjecutado);
                            aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        }
                    }
                    aProyecto.setSaldoCompromiso(aProyecto.getCodificado() - certficicaciones - compromisos);
                    // ver el ejecutado que debe ser igual al devengado
                    listaAsig.add(aProyecto);
                    aSuperTotal.setAsignacion(aProyecto.getAsignacion() + aSuperTotal.getAsignacion());
                    aSuperTotal.setCertificacion(aProyecto.getCertificacion() + aSuperTotal.getCertificacion());
                    aSuperTotal.setCertificado(aProyecto.getCertificado() + aSuperTotal.getCertificado());
                    aSuperTotal.setCodificado(aProyecto.getCodificado() + aSuperTotal.getCodificado());
                    aSuperTotal.setComprometido(aProyecto.getComprometido() + aSuperTotal.getComprometido());
                    aSuperTotal.setCompromiso(aProyecto.getCompromiso() + aSuperTotal.getCompromiso());
                    aSuperTotal.setDevengado(aProyecto.getDevengado() + aSuperTotal.getDevengado());
                    aSuperTotal.setSaldoDevengado(aProyecto.getSaldoDevengado() + aSuperTotal.getSaldoDevengado());
                    aSuperTotal.setReforma(aProyecto.getReforma() + aSuperTotal.getReforma());
                    if (anioLocal == 2018) {
                        aSuperTotal.setEjecutado(calculosBean.traerejecutado2018Valor());
                    } else {
                        aSuperTotal.setEjecutado(aProyecto.getEjecutado() + aSuperTotal.getEjecutado());
                    }
                    aSuperTotal.setSaldoEjecutado(aProyecto.getSaldoEjecutado() + aSuperTotal.getSaldoEjecutado());
                    aSuperTotal.setValor(aProyecto.getValor() + aSuperTotal.getValor());
                    aSuperTotal.setSaldoCompromiso(aProyecto.getSaldoCompromiso() + aSuperTotal.getSaldoCompromiso());
                }
            }

            datosBarras = new CartesianSeries() {
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    setType(CartesianSeries.CartesianType.BAR);
                    setPointLabels(true);
                    add("Asignación", aSuperTotal.getAsignacion());
                    add("Reformas", aSuperTotal.getReforma());
                    add("Asignación Codificada", aSuperTotal.getCodificado());
                    add("Certificación", aSuperTotal.getCertificacion());
                    add("Saldo Certificación", aSuperTotal.getCertificado());
                    add("Compromisos", aSuperTotal.getCompromiso());
                    add("Saldo Compromisos", aSuperTotal.getComprometido());
                    add("Devengado", aSuperTotal.getDevengado());
                    add("Saldo por devengar", aSuperTotal.getSaldoDevengado());
                    add("Pagado", aSuperTotal.getEjecutado());
                    add("Saldo por pagar", aSuperTotal.getSaldoEjecutado());
                    add("Saldo disponible", aSuperTotal.getSaldoCompromiso());
                    setLabel("Presupuesto Desde : " + sdf.format(desde) + " Hasta : " + sdf.format(hasta));
                }
            };
            // traer los proyectos y traer los clasificadores
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if ((proyecto == null) || (proyecto.isEmpty())) {
//                if ((proyectosBean.getProyectoSeleccionado()== null) ) {
                    parametros.put(";where", "o.anio=:anio and o.ingreso=:ingreso");
                    parametros.put("ingreso", ing);
                } else {
                    parametros.put(";where", "o.anio=:anio and upper(o.codigo) like :codigo and o.ingreso=:ingreso");
                    parametros.put("codigo", proyecto + "%");
//                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
                    parametros.put("ingreso", ing);
                }
            } else {
                if ((proyecto == null) || (proyecto.isEmpty())) {
//                if ((proyectosBean.getProyectoSeleccionado()== null)) {
                    parametros.put(";where", "o.anio=:anio");
                } else {
                    parametros.put(";where", "o.anio=:anio and upper(o.codigo) like :codigo ");
                    parametros.put("codigo", proyecto + "%");
//                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
                }
            }
            parametros.put("anio", anio);

            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            // traer los clasificadores
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            where = "";
            if (!((partida == null) || (partida.isEmpty()))) {
                where = " upper(o.codigo) like :partida";
                parametros.put("partida", partida + "%");

            }
            if (!fuente) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.imputable=true";
            }
            if (!where.isEmpty()) {
                parametros.put(";where", where);
            }
            // poner las cabeceras y el inicio del reporte

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> columnas = new LinkedList<>();
            int totalCol = 0;

            if (ingegrtodos == 1) {
                // ingresos

                if (mensual) {
// pocas columans preferible vertical

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA MENSUAL DE INGRESOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado());
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    totalCol = 5;
                    xls.agregarFila(titulos, true);
                } else {

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado());
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
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

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA MENSUAL DE GASTOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado());
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    totalCol = 7;
                    xls.agregarFila(titulos, true);
                } else {

                    xls = new DocumentoXLS("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado());
                    titulos = new LinkedList<>();
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
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

//            asignaciones = listaAsig;
            asignaciones = new LinkedList<>();
            List<Clasificadores> listaCla = ejbClasificadores.encontarParametros(parametros);
            for (Proyectos p : pl) {

                AuxiliarAsignacion a = colocar(listaAsig, p, null);
                a.setFuente("titulo");
                if (!a.isCero()) {
//                    if (anioLocal == 2018) {
//                        if (p.getCodigo().length() < 10) {
//                            asignaciones.add(a);
//                            filaReporteXls(a);
//                        }
//                    } else {
                    asignaciones.add(a);
                    filaReporteXls(a);
//                    }

                    // añadir fila de reporte
                    if (p.getImputable()) {
                        for (Clasificadores c : listaCla) {
                            AuxiliarAsignacion cax = colocar(listaAsig, p, c);
                            String codigo = cax.getCodigo();
                            String nombre = cax.getNombre();
                            cax.setCodigo(cax.getTitulo1());
                            cax.setNombre(cax.getTitulo2());
                            cax.setTitulo1(codigo);
                            cax.setTitulo2(nombre);
                            if (!cax.isCero()) {
                                asignaciones.add(cax);
                                filaReporteXls(cax);
                            }
                        }

                    }

                }

            }

            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            if (fuenteFinanciamiento != null) {
                parametros.put("expresado", "Cédula Presupuestaria  Fuente de Financiamieto :" + fuenteFinanciamiento.getNombre() + " " + configuracionBean.getConfiguracion().getExpresado());
            } else {
                parametros.put("expresado", "Cédula Presupuestaria : " + configuracionBean.getConfiguracion().getExpresado());
            }
            aSuperTotal.setFuente("total");
            aSuperTotal.setNombre("TOTAL");
            asignaciones.add(aSuperTotal);
            filaReporteXls(aSuperTotal);

//            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
            reporteXls = xls.traerRecurso();
//            parametros.put("nombrelogo", "logo-new.png");
//            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//            parametros.put("titulo1", getTitulo1());
//            parametros.put("titulo2", getTitulo2());
//            parametros.put("titulo3", getTitulo3());
//            parametros.put("desde", desde);
//            parametros.put("hasta", hasta);
//            Calendar c = Calendar.getInstance();
//            reporte = new Reportesds(parametros,
//                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Cedula.jasper"),
//                    asignaciones, "Cedula" + String.valueOf(c.getTimeInMillis()) + ".pdf");

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filaReporte(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        if (ingegrtodos == 1) {
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
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoCompromiso()));
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

    private void filaReporteXls(AuxiliarAsignacion a) {
        List<AuxiliarReporte> filas = new LinkedList<>();
        if (ingegrtodos == 1) {
            // ingresos
            if (mensual) {
                // pocas columans preferible vertical
                if (a.getProyectoEntidad() == null) {
                    a.setProyectoEntidad(new Proyectos());
                    a.getProyectoEntidad().setCodigo("_");
                }
                if (a.getProyectoEntidad().getCodigo().equals(a.getCodigo())) {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                } else {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo() != null ? a.getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre() != null ? a.getNombre() : ""));
                }
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                if (a.getProyectoEntidad() == null) {
                    a.setProyectoEntidad(new Proyectos());
                    a.getProyectoEntidad().setCodigo("_");
                }
                if (a.getProyectoEntidad().getCodigo().equals(a.getCodigo())) {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                } else {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo() != null ? a.getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre() != null ? a.getNombre() : ""));
                }
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoCompromiso()));
            }

        } else {
            // egresos
            if (mensual) {
                if (a.getProyectoEntidad() == null) {
                    a.setProyectoEntidad(new Proyectos());
                    a.getProyectoEntidad().setCodigo("_");
                }
                if (a.getProyectoEntidad().getCodigo().equals(a.getCodigo())) {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                } else {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo() != null ? a.getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre() != null ? a.getNombre() : ""));
                }
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                if (a.getProyectoEntidad() == null) {
                    a.setProyectoEntidad(new Proyectos());
                    a.getProyectoEntidad().setCodigo("_");
                }
                if (a.getProyectoEntidad().getCodigo().equals(a.getCodigo())) {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                } else {
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getProyectoEntidad() != null ? a.getProyectoEntidad().getNombre() : ""));
                    filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo() != null ? a.getCodigo() : ""));
                    filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre() != null ? a.getNombre() : ""));
                }
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
        xls.agregarFila(filas, false);
    }

    private AuxiliarAsignacion colocar(List<AuxiliarAsignacion> lista, Proyectos p, Clasificadores c) {
        AuxiliarAsignacion a = new AuxiliarAsignacion();
        a.setProyectoEntidad(p);
        if (p != null) {
            a.setCodigo(p.getCodigo());
            a.setNombre(p.getNombre());
        }
        if (c != null) {
            a.setTitulo1(c.getCodigo());
            a.setTitulo2(c.getNombre());
        }
        for (AuxiliarAsignacion au : lista) {
            if (p != null) {
                // tenemos ya el proyecto veo el clasificador
                if (c != null) {
                    // toca ahi ver si corresponde al proyecto y al clasificador
                    String codigoClasificador = au.getTitulo1().substring(0, c.getCodigo().length());
                    String codigoProyecto = au.getCodigo().substring(0, p.getCodigo().length());
                    if ((codigoProyecto.equals(p.getCodigo())) && codigoClasificador.equals(c.getCodigo())) {
                        // sumar al a
                        a.setAsignacion(au.getAsignacion() + a.getAsignacion());
                        a.setCertificacion(au.getCertificacion() + a.getCertificacion());
                        a.setCertificado(au.getCertificado() + a.getCertificado());
                        a.setCodificado(au.getCodificado() + a.getCodificado());
                        a.setComprometido(au.getComprometido() + a.getComprometido());
                        a.setCompromiso(au.getCompromiso() + a.getCompromiso());
                        a.setDevengado(au.getDevengado() + a.getDevengado());
                        a.setSaldoDevengado(au.getSaldoDevengado() + a.getSaldoDevengado());
                        a.setReforma(au.getReforma() + a.getReforma());
                        a.setEjecutado(au.getEjecutado() + a.getEjecutado());
                        a.setSaldoEjecutado(au.getSaldoEjecutado() + a.getSaldoEjecutado());
                        a.setSaldoCompromiso(au.getSaldoCompromiso() + a.getSaldoCompromiso());
                        a.setValor(au.getValor() + a.getValor());
                    }
                } else {// fin ei de claseificador
                    if (p.getCodigo().length() > au.getCodigo().length()) {
                        String codigoProyecto = au.getCodigo().substring(0, p.getCodigo().length());
                    }
                    String codigoProyecto = au.getCodigo().substring(0, p.getCodigo().length());
                    if ((codigoProyecto.equals(p.getCodigo()))) {
                        // sumar al a
                        a.setAsignacion(au.getAsignacion() + a.getAsignacion());
                        a.setCertificacion(au.getCertificacion() + a.getCertificacion());
                        a.setCertificado(au.getCertificado() + a.getCertificado());
                        a.setCodificado(au.getCodificado() + a.getCodificado());
                        a.setComprometido(au.getComprometido() + a.getComprometido());
                        a.setCompromiso(au.getCompromiso() + a.getCompromiso());
                        a.setDevengado(au.getDevengado() + a.getDevengado());
                        a.setSaldoDevengado(au.getSaldoDevengado() + a.getSaldoDevengado());
                        a.setReforma(au.getReforma() + a.getReforma());
                        a.setEjecutado(au.getEjecutado() + a.getEjecutado());
                        a.setSaldoEjecutado(au.getSaldoEjecutado() + a.getSaldoEjecutado());
                        a.setSaldoCompromiso(au.getSaldoCompromiso() + a.getSaldoCompromiso());
                        a.setValor(au.getValor() + a.getValor());
                    }
                } // fin if
            } else { //cuando no existe proyecto
                if (c != null) {
                    String codigoClasificador = au.getTitulo1().substring(0, c.getCodigo().length());
                    if (codigoClasificador.equals(c.getCodigo())) {
                        // sumar al a
                        a.setAsignacion(au.getAsignacion() + a.getAsignacion());
                        a.setCertificacion(au.getCertificacion() + a.getCertificacion());
                        a.setCertificado(au.getCertificado() + a.getCertificado());
                        a.setCodificado(au.getCodificado() + a.getCodificado());
                        a.setComprometido(au.getComprometido() + a.getComprometido());
                        a.setCompromiso(au.getCompromiso() + a.getCompromiso());
                        a.setDevengado(au.getDevengado() + a.getDevengado());
                        a.setSaldoDevengado(au.getSaldoDevengado() + a.getSaldoDevengado());
                        a.setReforma(au.getReforma() + a.getReforma());
                        a.setEjecutado(au.getEjecutado() + a.getEjecutado());
                        a.setSaldoEjecutado(au.getSaldoEjecutado() + a.getSaldoEjecutado());
                        a.setSaldoCompromiso(au.getSaldoCompromiso() + a.getSaldoCompromiso());
                        a.setValor(au.getValor() + a.getValor());
                    }
                }

            }
        }
        return a;
    }

    private void traerDetalleCompromiso(Date desde, Date hasta) {
        getReporteDetalleCompBean().setDesde(desdeInicio);
        getReporteDetalleCompBean().setHasta(hasta);
        getReporteDetalleCompBean().buscar();
        detalleCompromiso = getReporteDetalleCompBean().getListadoDetallecomprom();
    }

    private double valorEjecutado(String proyecto, String partida) {
        double retorno = 0;

        if (proyecto == null || proyecto.isEmpty()) {
            retorno = 0;
        }
        if (partida == null || partida.isEmpty()) {
            retorno = 0;
        }

        for (Detallecompromiso dc : detalleCompromiso) {
            if (proyecto.equals(dc.getAsignacion().getProyecto().getCodigo()) && partida.equals(dc.getAsignacion().getClasificador().getCodigo())) {
                retorno += dc.getValorEjecutado();
            }
        }
        return retorno;

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
     * @return the ingegrtodos
     */
    public int getIngegrtodos() {
        return ingegrtodos;
    }

    /**
     * @param ingegrtodos the ingegrtodos to set
     */
    public void setIngegrtodos(int ingegrtodos) {
        this.ingegrtodos = ingegrtodos;
    }

    /**
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    /**
     * @return the proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
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

    public boolean isVerColumna() {
        if (ingegrtodos == 1) {
            return false;
        }

        return true;
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
     * @return the xls
     */
    public DocumentoXLS getXls() {
        return xls;
    }

    /**
     * @param xls the xls to set
     */
    public void setXls(DocumentoXLS xls) {
        this.xls = xls;
    }

    /**
     * @return the desdeInicio
     */
    public Date getDesdeInicio() {
        return desdeInicio;
    }

    /**
     * @param desdeInicio the desdeInicio to set
     */
    public void setDesdeInicio(Date desdeInicio) {
        this.desdeInicio = desdeInicio;
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
     * @return the asignaciones
     */
    public List<AuxiliarAsignacion> getAsignaciones() {
        return asignaciones;
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
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
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
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<AuxiliarAsignacion> asignaciones) {
        this.asignaciones = asignaciones;
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
     * @return the datosBarras
     */
    public CartesianSeries getDatosBarras() {
        return datosBarras;
    }

    /**
     * @param datosBarras the datosBarras to set
     */
    public void setDatosBarras(CartesianSeries datosBarras) {
        this.datosBarras = datosBarras;
    }
    private Axis barDemoXAxis = new Axis() {
        {
            setType(AxisType.CATEGORY);
        }
    };

    public Axis getBarDemoXAxis() {
        return barDemoXAxis;
    }

    public void setBarDemoXAxis(Axis barDemoXAxis) {
        this.barDemoXAxis = barDemoXAxis;
    }

    public Axis getBarDemoDefaultAxis() {
        return barDemoDefaultAxis;
    }

    public void setBarDemoDefaultAxis(Axis barDemoDefaultAxis) {
        this.barDemoDefaultAxis = barDemoDefaultAxis;
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
    private Axis barDemoDefaultAxis = new Axis() {
        {
            setTickAngle(-30);
        }
    };
}
