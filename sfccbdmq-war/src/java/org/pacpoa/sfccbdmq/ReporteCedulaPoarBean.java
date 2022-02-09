/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.entidades.sfccbdmq.Partidaspoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.errores.sfccbdmq.ConsultarException;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.model.chart.CartesianSeries;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author sigi-iepi
 */
@ManagedBean(name = "reporteCedulaPoar")
@ViewScoped
public class ReporteCedulaPoarBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaPoarBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
    private int ingegrtodos;
    private Formulario formulario = new Formulario();
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
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
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPoaBean;
    private CartesianSeries datosBarras;
    private Resource reporte;

    public String buscar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        c.set(anio, 1, 1);
        desdeInicio = (c.getTime());
        llenar();
//        llenarSinfuente();

        return null;
    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteCedulaVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
//        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
////        if (nombreForma==null){
////            nombreForma="";
////        }
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
    private Axis barDemoDefaultAxis = new Axis() {
        {
            setTickAngle(-30);
        }
    };

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
                titulo1 = "Recaudado (f)";
                titulo2 = "Saldo por Recaudadar (a-f)";
                titulo3 = "Saldo por cobrar (a-g)";
                titulo4 = "Cobrado (g)";
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
                where += " and  upper(o.partida.codigo) like :partida";
                parametros.put("partida", partida + "%");
            }
            if (fuenteFinanciamiento != null) {
                where += " and  o.fuente =:fuente";
                parametros.put("fuente", fuenteFinanciamiento.getCodigo());
            }
            parametros.put(";where", where);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.proyecto.codigo,o.partida.codigo");
            parametros.put(";suma", "sum(o.valor)");
            List<Object[]> listaAgrupados;
            listaAgrupados = ejbAsignacionespoa.sumar(parametros);
//            List<Asignaciones> listaAisgnaciones = ejbAsignaciones.encontarParametros(parametros);
            for (Object[] o : listaAgrupados) {
//            for (Asignaciones a : listaAisgnaciones) {
//                int signo = a.getProyecto().getIngreso() ? 1 : -1;

                String codProyecto = (String) o[0];
                String codClasificador = (String) o[1];
                BigDecimal valor = (BigDecimal) o[2];
                AuxiliarAsignacion aProyecto = new AuxiliarAsignacion();
                Proyectospoa p = proyectosBean.traerCodigo(codProyecto);
                aProyecto.setCodigo(codProyecto);
                aProyecto.setNombre("");
                aProyecto.setTipo("ASIGNACION");
                aProyecto.setTitulo1(codClasificador);
                aProyecto.setTitulo2("");
                aProyecto.setFuente("");
                double asignacion = valor.doubleValue();
                aProyecto.setAsignacion(asignacion);
                double reforma = calculosPoaBean.traerReformas(codProyecto, codClasificador, desdeInicio, hasta, fuenteFinanciamiento != null ? fuenteFinanciamiento.getCodigo() : null);
                aProyecto.setReforma(reforma);
                aProyecto.setCodificado(asignacion + reforma);

                double certficicaciones = calculosPoaBean.traerCertificaciones(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento != null ? fuenteFinanciamiento.getCodigo() : null);
                aProyecto.setCertificacion(certficicaciones);
                aProyecto.setCertificado(aProyecto.getCodificado() - certficicaciones);
                double compromisos = calculosPoaBean.traerCompromisosCertificacion(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento != null ? fuenteFinanciamiento.getCodigo() : null);
                aProyecto.setCompromiso(compromisos);
                double contabiliadad = calculosPoaBean.traerContabilidad(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento != null ? fuenteFinanciamiento.getCodigo() : null);
                aProyecto.setContabilidad(contabiliadad);
                aProyecto.setSaldoCompromiso(aProyecto.getCodificado() - (certficicaciones + compromisos + contabiliadad));

                // ver el ejecutado que debe ser igual al devengado
                listaAsig.add(aProyecto);
                aSuperTotal.setAsignacion(aProyecto.getAsignacion() + aSuperTotal.getAsignacion());
                aSuperTotal.setReforma(aProyecto.getReforma() + aSuperTotal.getReforma());
                aSuperTotal.setCodificado(aProyecto.getCodificado() + aSuperTotal.getCodificado());
                aSuperTotal.setCertificacion(aProyecto.getCertificacion() + aSuperTotal.getCertificacion());
                aSuperTotal.setCertificado(aProyecto.getCertificado() + aSuperTotal.getCertificado());
                aSuperTotal.setCompromiso(aProyecto.getCompromiso() + aSuperTotal.getCompromiso());
                aSuperTotal.setContabilidad(aProyecto.getContabilidad() + aSuperTotal.getContabilidad());
                aSuperTotal.setSaldoCompromiso(aProyecto.getSaldoCompromiso() + aSuperTotal.getSaldoCompromiso());
            }

            datosBarras = new CartesianSeries() {
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    setType(CartesianSeries.CartesianType.BAR);
                    setPointLabels(true);
                    add("Asignación", getaSuperTotal().getAsignacion());
                    add("Reformas", getaSuperTotal().getReforma());
                    add("Asignación Codificada", getaSuperTotal().getCodificado());
                    add("Certificación", getaSuperTotal().getCertificacion());
                    add("Saldo Certificación", getaSuperTotal().getCertificado());
                    add("Compromisos", getaSuperTotal().getCompromiso());
                    add("Contabilidad", getaSuperTotal().getContabilidad());
                    setLabel("Presupuesto Desde : " + sdf.format(getDesde()) + " Hasta : " + sdf.format(getHasta()));
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

            List<Proyectospoa> pl = ejbProyectospoa.encontarParametros(parametros);
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
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COBRADO"));
                    totalCol = 5;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROD / PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR RECAUDADAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COBRADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR COBRAR"));
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
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CONTABILIDAD"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO DISPONIBLE"));
                    totalCol = 13;
                }
            }

//            asignaciones = listaAsig;
            asignaciones = new LinkedList<>();
            List<Partidaspoa> listaCla = ejbPartidaspoa.encontarParametros(parametros);
            for (Proyectospoa p : pl) {
                AuxiliarAsignacion a = colocar(listaAsig, p, null);
                a.setFuente("titulo");
                if (!a.isCero()) {
                    asignaciones.add(a);
                    filaReporte(columnas, a);
                    // añadir fila de reporte
                    if (p.getImputable()) {
                        for (Partidaspoa c : listaCla) {
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

        } catch (IOException | DocumentException | ConsultarException ex) {
            Logger.getLogger(ReporteCedulaPoarBean.class.getName()).log(Level.SEVERE, null, ex);
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
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getContabilidad()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoCompromiso()));
            }
        }
    }

    private AuxiliarAsignacion colocar(List<AuxiliarAsignacion> lista, Proyectospoa p, Partidaspoa c) {
        AuxiliarAsignacion a = new AuxiliarAsignacion();
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
                        a.setReforma(au.getReforma() + a.getReforma());
                        a.setCodificado(au.getCodificado() + a.getCodificado());
                        a.setCertificacion(au.getCertificacion() + a.getCertificacion());
                        a.setCertificado(au.getCertificado() + a.getCertificado());
                        a.setCompromiso(au.getCompromiso() + a.getCompromiso());
                        a.setContabilidad(au.getContabilidad() + a.getContabilidad());
                        a.setSaldoCompromiso(au.getSaldoCompromiso() + a.getSaldoCompromiso());
                    }
                } else {// fin ei de claseificador
                    if (p.getCodigo().length() > au.getCodigo().length()) {
                        String codigoProyecto = au.getCodigo().substring(0, p.getCodigo().length());
                    }
                    String codigoProyecto = au.getCodigo().substring(0, p.getCodigo().length());
                    if ((codigoProyecto.equals(p.getCodigo()))) {
                        // sumar al a
                        a.setAsignacion(au.getAsignacion() + a.getAsignacion());
                        a.setReforma(au.getReforma() + a.getReforma());
                        a.setCodificado(au.getCodificado() + a.getCodificado());
                        a.setCertificacion(au.getCertificacion() + a.getCertificacion());
                        a.setCertificado(au.getCertificado() + a.getCertificado());
                        a.setCompromiso(au.getCompromiso() + a.getCompromiso());
                        a.setContabilidad(au.getContabilidad() + a.getContabilidad());
                        a.setSaldoCompromiso(au.getSaldoCompromiso() + a.getSaldoCompromiso());
                    }
                } // fin if
            } else { //cuando no existe proyecto
                if (c != null) {
                    String codigoClasificador = au.getTitulo1().substring(0, c.getCodigo().length());
                    if (codigoClasificador.equals(c.getCodigo())) {
                        // sumar al a
                        a.setAsignacion(au.getAsignacion() + a.getAsignacion());
                        a.setReforma(au.getReforma() + a.getReforma());
                        a.setCodificado(au.getCodificado() + a.getCodificado());
                        a.setCertificacion(au.getCertificacion() + a.getCertificacion());
                        a.setCertificado(au.getCertificado() + a.getCertificado());
                        a.setCompromiso(au.getCompromiso() + a.getCompromiso());
                        a.setContabilidad(au.getContabilidad() + a.getContabilidad());
                        a.setSaldoCompromiso(au.getSaldoCompromiso() + a.getSaldoCompromiso());
                    }
                }

            }
        }
        return a;
    }

    public boolean isVerColumna() {
        if (ingegrtodos == 1) {
            return false;
        }

        return true;
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
     * @return the aSuperTotal
     */
    public AuxiliarAsignacion getaSuperTotal() {
        return aSuperTotal;
    }

    /**
     * @param aSuperTotal the aSuperTotal to set
     */
    public void setaSuperTotal(AuxiliarAsignacion aSuperTotal) {
        this.aSuperTotal = aSuperTotal;
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
     * @return the datosBarras
     */
    public CartesianSeries getDatosBarras() {
        return datosBarras;
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
     * @return the proyectosBean
     */
    public ProyectosPoaBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosPoaBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }

    /**
     * @return the calculosPoaBean
     */
    public CalculosPoaBean getCalculosPoaBean() {
        return calculosPoaBean;
    }

    /**
     * @param calculosPoaBean the calculosPoaBean to set
     */
    public void setCalculosPoaBean(CalculosPoaBean calculosPoaBean) {
        this.calculosPoaBean = calculosPoaBean;
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

}
