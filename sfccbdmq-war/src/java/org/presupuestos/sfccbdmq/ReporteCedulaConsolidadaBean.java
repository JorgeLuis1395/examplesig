/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
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
@ManagedBean(name = "reporteCedulaConsolidadaSfccbdmq")
@ViewScoped
public class ReporteCedulaConsolidadaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaConsolidadaBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<AuxiliarAsignacion> totales;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
    private int ingegrtodos;
    private Formulario formulario = new Formulario();
    @EJB
    private AsignacionesFacade ejbAsignaciones;
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
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private boolean mensual;
    private String proyecto;
    private String partida;
    private String rayaBaja;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private String titulo4;
    private Codigos fuenteFinanciamiento;
    private Date desde;
    private Date hasta;
    private AuxiliarAsignacion aSuperTotal;
    private String nivel;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private CartesianSeries datosBarras;
    private Resource reporte;
    private Resource reporteXls;
    private DocumentoXLS xls;

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

    public String buscar() {
        llenarConFuente();
//        llenarSinfuente();
//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "Cédula consolidada : " + configuracionBean.getConfiguracion().getExpresado());
//        parametros.put("nombrelogo", "logo-new.png");
//        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//        parametros.put("titulo1", titulo1);
//        parametros.put("titulo2", titulo2);
//        parametros.put("titulo3", titulo3);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Cedula.jasper"),
//                asignaciones, "Cedula" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        return null;
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
        String nombreForma = "ReporteCedulaConsolidadaVista";
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

    public double getValor() {
        AuxiliarAsignacion a = asignaciones.get(formulario.getFila().getRowIndex());
        switch (a.getTipo()) {
            case "CUEN":
                return a.getValor();
            // sumar
            case "FUEN":
                Map parametros = new HashMap();
                parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto.codigo=:proyecto ");
                parametros.put("codigo", a.getCodigo());
                parametros.put("proyecto", a.getFuente());
                parametros.put(";campo", "o.valor");
                try {

                    return ejbAsignaciones.sumarCampo(parametros).doubleValue();
                } catch (ConsultarException ex) {
                    Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.codigo like :codigo ");
        parametros.put("codigo", a.getCodigo() + "%");
        parametros.put(";campo", "o.valor");
        try {
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

    public void cambiaFuente(ValueChangeEvent event) {
        // cambia el texto de la cedula
        fuente = (boolean) event.getNewValue();
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
            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

//    /**
//     * @return the proyecto
//     */
//    public String getProyecto() {
//        return proyecto;
//    }
//
//    /**
//     * @param proyecto the proyecto to set
//     */
//    public void setProyecto(String proyecto) {
//        this.proyecto = proyecto;
//    }
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
    private Axis barDemoDefaultAxis = new Axis() {
        {
            setTickAngle(-30);
        }
    };

    private void llenarConFuente() {
        totalEgresos = 0;
        totalIngresos = 0;
        fuenteFinanciamiento = null;
        try {
            asignaciones = new LinkedList<>();
            List<AuxiliarAsignacion> listaAsig = new LinkedList<>();
            xls = new DocumentoXLS("Cédula Presupuestaria");
            List<AuxiliarReporte> campos = new LinkedList<>();
            Calendar ca = Calendar.getInstance();
            setaSuperTotal(new AuxiliarAsignacion());
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
                hasta = cHasta.getTime();
            } else {
                Calendar c = Calendar.getInstance();
                c.setTime(hasta);
                int anio = c.get(Calendar.YEAR);
                Calendar fechaParaInicio = Calendar.getInstance();
                fechaParaInicio.set(anio, 0, 1);
                cDesde = fechaParaInicio;

                cHasta.setTime(hasta);
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo desc");
            String where = "o.anio=:anio";
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if ((proyecto == null) || (proyecto.isEmpty())) {
                    where += " and o.ingreso=:ingreso";
                    parametros.put("ingreso", ing);
                } else {
                    where += " and upper(o.codigo) like :codigo and o.ingreso=:ingreso";
                    parametros.put("codigo", proyecto + "%");
                    parametros.put("ingreso", ing);
                }
                if (ing) {
                    setTitulo1("Devengado (f)");
                    setTitulo2("Saldo por Devengar (a-f)");
                    setTitulo3("Saldo por Recaudadar (a-g)");
                    setTitulo3("Recaudado(g)");
                } else {
                    setTitulo1("Devengado (f) ");
                    setTitulo2("Saldo por Devengar (a-f) ");
                    setTitulo3("Saldo por pagar (a-g)");
                    setTitulo4("Pagado (g)");
                }
            } else {
                if ((proyecto == null) || (proyecto.isEmpty())) {
                } else {
                    where += " and upper(o.codigo) like :codigo ";
                    parametros.put("codigo", proyecto + "%");
                }
            }

            parametros.put("anio", anio);
            parametros.put(";where", where);

            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            List<Proyectos> pl1 = new LinkedList<>();
            int i = 0;
            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
                for (Proyectos p : pl) {
                    if (p.getCodigo().length() <= rayaBaja.length()) {
                        pl1.add(p);
                    }
                    i++;
                }
            } else {
                pl1 = pl;
            }
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
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    // ecxel
                    campos.add(new AuxiliarReporte("String", "CODIGO"));
                    campos.add(new AuxiliarReporte("String", "NOMBRE"));
                    campos.add(new AuxiliarReporte("String", "PARTIDA"));
                    campos.add(new AuxiliarReporte("String", "REFORMAS"));
                    campos.add(new AuxiliarReporte("String", "DEVENGADO"));
                    campos.add(new AuxiliarReporte("String", "RECAUDADO"));
                    totalCol = 6;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGNACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR DEVENGAR"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "POR RECAUDADAR"));
                    totalCol = 10;
                    //excel
                    campos.add(new AuxiliarReporte("String", "CODIGO"));
                    campos.add(new AuxiliarReporte("String", "NOMBRE"));
                    campos.add(new AuxiliarReporte("String", "PARTIDA"));
                    campos.add(new AuxiliarReporte("String", "ASIGNACION"));
                    campos.add(new AuxiliarReporte("String", "REFORMAS"));
                    campos.add(new AuxiliarReporte("String", "CODIFICADO"));
                    campos.add(new AuxiliarReporte("String", "DEVENGADO"));
                    campos.add(new AuxiliarReporte("String", "POR DEVENGAR"));
                    campos.add(new AuxiliarReporte("String", "RECAUDADO"));
                    campos.add(new AuxiliarReporte("String", "POR RECAUDADAR"));
                }

            } else {
                // egresos
                if (mensual) {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA MENSUAL DE GASTOS\n MES :" + mes + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    totalCol = 8;
                    //excell
                    campos.add(new AuxiliarReporte("String", "CODIGO"));
                    campos.add(new AuxiliarReporte("String", "NOMBRE"));
                    campos.add(new AuxiliarReporte("String", "PARTIDA"));
                    campos.add(new AuxiliarReporte("String", "REFORMAS"));
                    campos.add(new AuxiliarReporte("String", "CERTIFICACION"));
                    campos.add(new AuxiliarReporte("String", "COMPROMISOS"));
                    campos.add(new AuxiliarReporte("String", "DEVENGADO"));
                    campos.add(new AuxiliarReporte("String", "PAGADO"));
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGANCION"));
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
                    totalCol = 14;
                    // excell
                    campos.add(new AuxiliarReporte("String", "CODIGO"));
                    campos.add(new AuxiliarReporte("String", "NOMBRE"));
                    campos.add(new AuxiliarReporte("String", "PARTIDA"));
                    campos.add(new AuxiliarReporte("String", "ASIGANCION"));
                    campos.add(new AuxiliarReporte("String", "REFORMAS"));
                    campos.add(new AuxiliarReporte("String", "CODIFICADO"));
                    campos.add(new AuxiliarReporte("String", "CERTIFICACIONES"));
                    campos.add(new AuxiliarReporte("String", "SALDO CERTIFICADO"));
                    campos.add(new AuxiliarReporte("String", "COMPROMISOS"));
                    campos.add(new AuxiliarReporte("String", "SALDO COMPROMISOS"));
                    campos.add(new AuxiliarReporte("String", "DEVENGADO"));
                    campos.add(new AuxiliarReporte("String", "SALDO POR DEVENGAR"));
                    campos.add(new AuxiliarReporte("String", "PAGADO"));
                    campos.add(new AuxiliarReporte("String", "SALDO POR PAGAR"));
                }
            }
            xls.agregarFila(campos, true);
            // lista de clasificadores
            parametros = new HashMap();
            parametros.put(";where", "o.imputable=true");
            parametros.put(";orden", "o.codigo");
            List<Clasificadores> listaClasificadores = ejbClasificadores.encontarParametros(parametros);
            AuxiliarAsignacion a2 = new AuxiliarAsignacion();
            for (Proyectos p : pl1) {

                // aui va el if de imputables
                if (p.getImputable()) {
//                        sin el clasificador

                    for (Clasificadores cla : listaClasificadores) {
                        AuxiliarAsignacion a = new AuxiliarAsignacion();
                        a.setCodigo(p.getCodigo());
                        a.setNombre(p.getNombre());
                        a.setClasificadorEntidad(null);
                        // con el clasificador
                        // Traer asignacion con el p
                        a.setClasificadorEntidad(cla);
                        // buscar las cuentas
                        double valorAisgnacion = calculosBean.traerAsignaciones(anio, p.getCodigo(), cla.getCodigo(),
                                fuenteFinanciamiento);

                        // reformas anteriores
                        a.setAsignacion(valorAisgnacion);
                        // reformas
                        double valorReformas = calculosBean.traerReformas(p.getCodigo(), cla.getCodigo(),
                                cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        a.setReforma(valorReformas);
                        // primmera columa de sumas 
                        if (Math.round((valorAisgnacion + valorReformas) * 100) != 0) {
                            a.setCodificado(valorAisgnacion + valorReformas);
                            // certificaciones
                            double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(),
                                    cla.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                            a.setCertificacion(valorCertificaciones);
                            // segundo campo sumado
                            a.setCertificado(a.getCodificado() - valorCertificaciones);
                            // ejecucion=comprometido?

                            double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(),
                                    cla.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                            a.setCompromiso(valorCompromiso);
                            a.setComprometido(a.getCodificado() - valorCompromiso);
                            if (p.getIngreso()) {
                                double valorDevengado = calculosBean.traerDevengadoIngresos(p.getCodigo(),
                                        cla.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                                a.setDevengado(valorDevengado);
                                // saldo por devengar
                                a.setSaldoDevengado(a.getCodificado() - valorDevengado);
                                // ejecutado
                                double valorEjecutado = calculosBean.traerIngresos(p.getCodigo(),
                                        cla.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                                a.setEjecutado(valorEjecutado);
                                // ver el ejecutado que debe ser igual al devengado
                                a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                            } else {
                                // devengado
                                double valorDevengado = calculosBean.traerDevengado(p.getCodigo(),
                                        cla.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                                // nomina
                                a.setDevengado(valorDevengado);
                                // saldo por devengar
                                a.setSaldoDevengado(a.getCodificado() - valorDevengado);

                                // ejecutado
                                double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(),
                                        cla.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                                // nomina

                                a.setEjecutado(valorEjecutado);
                                // ver el ejecutado que debe ser igual al devengado
                                a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                            }

                            if (mensual) {
                                if (!a.isCeroMes()) {
                                    asignaciones.add(a);
                                    filaReporte(columnas, a);
                                }
                            } else {
                                if (!a.isCero()) {
                                    asignaciones.add(a);
                                    filaReporte(columnas, a);
                                }
                            }
                            a2.setAsignacion(a2.getAsignacion() + a.getAsignacion());
                            a2.setAvance(a2.getAvance() + a.getAvance());
                            a2.setCertificacion(a2.getCertificacion() + a.getCertificacion());
                            a2.setCertificado(a2.getCertificado() + a.getCertificado());
                            a2.setCodificado(a2.getCodificado() + a.getCodificado());
                            a2.setComprometido(a2.getComprometido() + a.getComprometido());
                            a2.setCompromiso(a2.getCompromiso() + a.getCompromiso());
                            a2.setDevengado(a2.getDevengado() + a.getDevengado());
                            a2.setEjecutado(a2.getEjecutado() + a.getEjecutado());
                            a2.setReforma(a2.getReforma() + a.getReforma());
                            a2.setSaldoDevengado(a2.getSaldoDevengado() + a.getSaldoDevengado());
                            a2.setSaldoEjecutado(a2.getSaldoEjecutado() + a.getSaldoEjecutado());
                            a2.setValor(a2.getValor() + a.getValor());
                        }
                    } // fin clasificadores
                } else {
                    AuxiliarAsignacion a = new AuxiliarAsignacion();
                    a.setCodigo(p.getCodigo());
                    a.setNombre(p.getNombre());
                    a.setClasificadorEntidad(null);
                    double valorAisgnacion = calculosBean.traerAsignaciones(anio, p.getCodigo(), null, fuenteFinanciamiento);

                    // reformas anteriores
                    a.setAsignacion(valorAisgnacion);
                    // reformas
                    double valorReformas = calculosBean.traerReformas(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    a.setReforma(valorReformas);
                    // primmera columa de sumas 
                    a.setCodificado(valorAisgnacion + valorReformas);
                    // certificaciones
                    double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    a.setCertificacion(valorCertificaciones);
                    // segundo campo sumado
                    a.setCertificado(a.getCodificado() - valorCertificaciones);
                    // ejecucion=comprometido?

                    double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    a.setCompromiso(valorCompromiso);
                    a.setComprometido(a.getCodificado() - valorCompromiso);
                    if (p.getIngreso()) {
                        double valorDevengado = calculosBean.traerDevengadoIngresos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        a.setDevengado(valorDevengado);
                        // saldo por devengar
                        a.setSaldoDevengado(a.getCodificado() - valorDevengado);
                        // ejecutado
                        double valorEjecutado = calculosBean.traerIngresos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        a.setEjecutado(valorEjecutado);
                        // ver el ejecutado que debe ser igual al devengado
                        a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                    } else {
                        // devengado
                        double valorDevengado = calculosBean.traerDevengado(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        // nomina
                        a.setDevengado(valorDevengado);
                        // saldo por devengar
                        a.setSaldoDevengado(a.getCodificado() - valorDevengado);

                        // ejecutado
                        double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        // nomina

                        a.setEjecutado(valorEjecutado);
                        // ver el ejecutado que debe ser igual al devengado
                        a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                    }
                    if (mensual) {
                        if (!a.isCeroMes()) {
                            asignaciones.add(a);
                            filaReporte(columnas, a);
                        }
                    } else {
                        if (!a.isCero()) {
                            asignaciones.add(a);
                            filaReporte(columnas, a);
                        }
                    }
//                    if (a.getCodigo().length() == 1) {
//                        a2.setAsignacion(a2.getAsignacion() + a.getAsignacion());
//                        a2.setAvance(a2.getAvance() + a.getAvance());
//                        a2.setCertificacion(a2.getCertificacion() + a.getCertificacion());
//                        a2.setCertificado(a2.getCertificado() + a.getCertificado());
//                        a2.setCodificado(a2.getCodificado() + a.getCodificado());
//                        a2.setComprometido(a2.getComprometido() + a.getComprometido());
//                        a2.setCompromiso(a2.getCompromiso() + a.getCompromiso());
//                        a2.setDevengado(a2.getDevengado() + a.getDevengado());
//                        a2.setEjecutado(a2.getEjecutado() + a.getEjecutado());
//                        a2.setReforma(a2.getReforma() + a.getReforma());
//                        a2.setSaldoDevengado(a2.getSaldoDevengado() + a.getSaldoDevengado());
//                        a2.setSaldoEjecutado(a2.getSaldoEjecutado() + a.getSaldoEjecutado());
//                        a2.setValor(a2.getValor() + a.getValor());
//                    }
                } // fin del if de imputable
//                if (a.getCodigo().length() == 1) {

            } // fin proyectos
//            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a2.setNombre("TOTALES");
//            for (AuxiliarAsignacion a1 : asignaciones) {
//                if (a.getCodigo().length() == 1) {
//                    a.setAsignacion(a1.getAsignacion() + a.getAsignacion());
//                    a.setAvance(a1.getAvance() + a.getAvance());
//                    a.setCertificacion(a1.getCertificacion() + a.getCertificacion());
//                    a.setCertificado(a1.getCertificado() + a.getCertificado());
//                    a.setCodificado(a1.getCodificado() + a.getCodificado());
//                    a.setComprometido(a1.getComprometido() + a.getComprometido());
//                    a.setCompromiso(a1.getCompromiso() + a.getCompromiso());
//                    a.setDevengado(a1.getDevengado() + a.getDevengado());
//                    a.setEjecutado(a1.getEjecutado() + a.getEjecutado());
//                    a.setReforma(a1.getReforma() + a.getReforma());
//                    a.setSaldoDevengado(a1.getSaldoDevengado() + a.getSaldoDevengado());
//                    a.setSaldoEjecutado(a1.getSaldoEjecutado() + a.getSaldoEjecutado());
//                    a.setValor(a1.getValor() + a.getValor());
//                }
//            }
            Collections.sort(asignaciones, new valorComparator());
            a2.setNombre("TOTALES");
//            a2.setCodigo("zzzzzzzzzzzzzz");
            a2.setFuente("total");
            asignaciones.add(a2);
            // Llenat los Totales
            totales = new LinkedList<>();
            aSuperTotal = new AuxiliarAsignacion();
            aSuperTotal.setNombre("% CERTIFICADO");
            aSuperTotal.setAsignacion(a2.getCertificacion() / a2.getCodificado() * 100);
            aSuperTotal.setCodificado(a2.getCodificado());
            aSuperTotal.setCertificacion(a2.getCertificacion());
            totales.add(aSuperTotal);
            aSuperTotal = new AuxiliarAsignacion();
            aSuperTotal.setNombre("% COMPROMETIDO");
            aSuperTotal.setAsignacion(a2.getCompromiso() / a2.getCodificado() * 100);
            aSuperTotal.setCodificado(a2.getCodificado());
            aSuperTotal.setCertificacion(a2.getCompromiso());
            totales.add(aSuperTotal);
            aSuperTotal = new AuxiliarAsignacion();
            aSuperTotal.setNombre("% GESTIÓN CUMPLIDA");
            aSuperTotal.setAsignacion(a2.getDevengado() / a2.getCodificado() * 100);
            aSuperTotal.setCodificado(a2.getCodificado());
            aSuperTotal.setCertificacion(a2.getDevengado());
            totales.add(aSuperTotal);
            datosBarras = new CartesianSeries() {
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    setType(CartesianSeries.CartesianType.BAR);
                    setPointLabels(true);
                    add("% CERTIFICADO", a2.getCertificacion() / a2.getCodificado());
                    add("% COMPROMETIDO", a2.getCompromiso() / a2.getCodificado());
                    add("% GESTIÓN CUMPLIDA", a2.getDevengado() / a2.getCodificado());
                    setLabel("RESUMEN");
                }
            };
            filaReporte(columnas, a2);
//            reporteXls = xls.traerRecurso();
//            ResumenEjecutivo hoja = new ResumenEjecutivo();
//            hoja.llenar((a2.getCertificacion() / a2.getCodificado())*100, 
//                    (a2.getCompromiso() / a2.getCodificado())*100, (a2.getDevengado() / a2.getCodificado())*100);
//            reporteXls = hoja.traerRecurso();
            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
            reporte = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            AuxiliarAsignacion a = (AuxiliarAsignacion) o1;
            AuxiliarAsignacion a1 = (AuxiliarAsignacion) o2;
            return a.getCodigo().
                    compareTo(a1.getCodigo());

        }
    }

    private void llenarConFuenteAnt() {
        totalEgresos = 0;
        totalIngresos = 0;
        try {
            asignaciones = new LinkedList<>();
            List<AuxiliarAsignacion> listaAsig = new LinkedList<>();
            Calendar ca = Calendar.getInstance();
            setaSuperTotal(new AuxiliarAsignacion());
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
                hasta = cHasta.getTime();
            } else {
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            String where = "o.anio=:anio";
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if ((proyecto == null) || (proyecto.isEmpty())) {
                    where += " and o.ingreso=:ingreso";
                    parametros.put("ingreso", ing);
                } else {
                    where += " and upper(o.codigo) like :codigo and o.ingreso=:ingreso";
                    parametros.put("codigo", proyecto + "%");
                    parametros.put("ingreso", ing);
                }
                if (ing) {
                    setTitulo1("Recaudado (f)");
                    setTitulo2("Saldo por Recaudadar (a-f)");
                    setTitulo3("Saldo por cobrar (a-g)");
                } else {
                    setTitulo1("Devengado (f) ");
                    setTitulo2("Saldo por Devengar (a-f) ");
                    setTitulo3("Saldo por pagar (a-g)");
                }
            } else {
                if ((proyecto == null) || (proyecto.isEmpty())) {
                } else {
                    where += " and upper(o.codigo) like :codigo ";
                    parametros.put("codigo", proyecto + "%");
                }
            }
//            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
//                where += " and upper(o.codigo) like :codigo1 ";
//                parametros.put("codigo1", rayaBaja);
//            }
            parametros.put("anio", anio);
            parametros.put(";where", where);

            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            List<Proyectos> pl1 = new LinkedList<>();
            int i = 0;
            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
                for (Proyectos p : pl) {
                    if (p.getCodigo().length() <= rayaBaja.length()) {
                        pl1.add(p);
                    }
                    i++;
                }
            } else {
                pl1 = pl;
            }
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
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RECAUDADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COBRADO"));
                    totalCol = 5;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE INGRESOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
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
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACION"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
                    totalCol = 7;
                } else {
                    pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO"));
                    titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE"));
                    titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGANCION"));
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
                    totalCol = 13;
                }
            }

            AuxiliarAsignacion a2 = new AuxiliarAsignacion();
            for (Proyectos p : pl1) {
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(p.getCodigo());
                a.setNombre(p.getNombre());
                // Traer asignacion con el proyecto
                // buscar las cuentas
                double valorAisgnacion = calculosBean.traerAsignaciones(anio, p.getCodigo(), null, fuenteFinanciamiento);

                // reformas anteriores
                a.setAsignacion(valorAisgnacion);
                // reformas
                double valorReformas = calculosBean.traerReformas(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setReforma(valorReformas);
                // primmera columa de sumas 
                a.setCodificado(valorAisgnacion + valorReformas);
                // certificaciones
                double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setCertificacion(valorCertificaciones);
                // segundo campo sumado
                a.setCertificado(a.getCodificado() - valorCertificaciones);
                // ejecucion=comprometido?

                double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                a.setCompromiso(valorCompromiso);
                a.setComprometido(a.getCodificado() - valorCompromiso);
                if (p.getIngreso()) {
                    double valorDevengado = calculosBean.traerDevengadoIngresos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    a.setDevengado(valorDevengado);
                    // saldo por devengar
                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);
                    // ejecutado
                    double valorEjecutado = calculosBean.traerIngresos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    a.setEjecutado(valorEjecutado);
                    // ver el ejecutado que debe ser igual al devengado
                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                } else {
                    // devengado
                    double valorDevengado = calculosBean.traerDevengado(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    // nomina
                    a.setDevengado(valorDevengado);
                    // saldo por devengar
                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);

                    // ejecutado
                    double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    // nomina

                    a.setEjecutado(valorEjecutado);
                    // ver el ejecutado que debe ser igual al devengado
                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                }
                if (mensual) {
                    if (!a.isCeroMes()) {
                        asignaciones.add(a);
                        filaReporte(columnas, a);
                    }
                } else {
                    if (!a.isCero()) {
                        asignaciones.add(a);
                        filaReporte(columnas, a);
                    }
                }
                if (a.getCodigo().length() == 1) {
                    a2.setAsignacion(a2.getAsignacion() + a.getAsignacion());
                    a2.setAvance(a2.getAvance() + a.getAvance());
                    a2.setCertificacion(a2.getCertificacion() + a.getCertificacion());
                    a2.setCertificado(a2.getCertificado() + a.getCertificado());
                    a2.setCodificado(a2.getCodificado() + a.getCodificado());
                    a2.setComprometido(a2.getComprometido() + a.getComprometido());
                    a2.setCompromiso(a2.getCompromiso() + a.getCompromiso());
                    a2.setDevengado(a2.getDevengado() + a.getDevengado());
                    a2.setEjecutado(a2.getEjecutado() + a.getEjecutado());
                    a2.setReforma(a2.getReforma() + a.getReforma());
                    a2.setSaldoDevengado(a2.getSaldoDevengado() + a.getSaldoDevengado());
                    a2.setSaldoEjecutado(a2.getSaldoEjecutado() + a.getSaldoEjecutado());
                    a2.setValor(a2.getValor() + a.getValor());
                }

            }
//            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a2.setNombre("TOTALES");
//            for (AuxiliarAsignacion a1 : asignaciones) {
//                if (a.getCodigo().length() == 1) {
//                    a.setAsignacion(a1.getAsignacion() + a.getAsignacion());
//                    a.setAvance(a1.getAvance() + a.getAvance());
//                    a.setCertificacion(a1.getCertificacion() + a.getCertificacion());
//                    a.setCertificado(a1.getCertificado() + a.getCertificado());
//                    a.setCodificado(a1.getCodificado() + a.getCodificado());
//                    a.setComprometido(a1.getComprometido() + a.getComprometido());
//                    a.setCompromiso(a1.getCompromiso() + a.getCompromiso());
//                    a.setDevengado(a1.getDevengado() + a.getDevengado());
//                    a.setEjecutado(a1.getEjecutado() + a.getEjecutado());
//                    a.setReforma(a1.getReforma() + a.getReforma());
//                    a.setSaldoDevengado(a1.getSaldoDevengado() + a.getSaldoDevengado());
//                    a.setSaldoEjecutado(a1.getSaldoEjecutado() + a.getSaldoEjecutado());
//                    a.setValor(a1.getValor() + a.getValor());
//                }
//            }
            a2.setFuente("total");
            asignaciones.add(a2);
            filaReporte(columnas, a2);
            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(ReporteCedulaConsolidadaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filaReporte(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        List<AuxiliarReporte> campos = new LinkedList<>();
        if (ingegrtodos == 1) {
            // ingresos
            if (mensual) {
                // pocas columans preferible vertical
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                // excell
                campos.add(new AuxiliarReporte("String", a.getCodigo()));
                campos.add(new AuxiliarReporte("String", a.getNombre()));
                campos.add(new AuxiliarReporte("String", a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                campos.add(new AuxiliarReporte("Double", a.getReforma()));
                campos.add(new AuxiliarReporte("Double", a.getDevengado()));
                campos.add(new AuxiliarReporte("Double", a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
                // ecell
                campos.add(new AuxiliarReporte("String", a.getCodigo()));
                campos.add(new AuxiliarReporte("String", a.getNombre()));
                campos.add(new AuxiliarReporte("String", a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                campos.add(new AuxiliarReporte("Double", a.getAsignacion()));
                campos.add(new AuxiliarReporte("Double", a.getReforma()));
                campos.add(new AuxiliarReporte("Double", a.getCodificado()));
                campos.add(new AuxiliarReporte("Double", a.getDevengado()));
                campos.add(new AuxiliarReporte("Double", a.getSaldoDevengado()));
                campos.add(new AuxiliarReporte("Double", a.getEjecutado()));
                campos.add(new AuxiliarReporte("Double", a.getSaldoEjecutado()));
            }

        } else {
            // egresos
            if (mensual) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                // ecell
                campos.add(new AuxiliarReporte("String", a.getCodigo()));
                campos.add(new AuxiliarReporte("String", a.getNombre()));
                campos.add(new AuxiliarReporte("String", a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                campos.add(new AuxiliarReporte("Double", a.getReforma()));
                campos.add(new AuxiliarReporte("Double", a.getCertificacion()));
                campos.add(new AuxiliarReporte("Double", a.getCompromiso()));
                campos.add(new AuxiliarReporte("Double", a.getDevengado()));
                campos.add(new AuxiliarReporte("Double", a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
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
                // ecell
                campos.add(new AuxiliarReporte("String", a.getCodigo()));
                campos.add(new AuxiliarReporte("String", a.getNombre()));
                campos.add(new AuxiliarReporte("String", a.getClasificadorEntidad() == null ? "" : a.getClasificadorEntidad().getCodigo()));
                campos.add(new AuxiliarReporte("Double", a.getAsignacion()));
                campos.add(new AuxiliarReporte("Double", a.getReforma()));
                campos.add(new AuxiliarReporte("Double", a.getCodificado()));
                campos.add(new AuxiliarReporte("Double", a.getCertificacion()));
                campos.add(new AuxiliarReporte("Double", a.getCertificado()));
                campos.add(new AuxiliarReporte("Double", a.getCompromiso()));
                campos.add(new AuxiliarReporte("Double", a.getComprometido()));
                campos.add(new AuxiliarReporte("Double", a.getDevengado()));
                campos.add(new AuxiliarReporte("Double", a.getSaldoDevengado()));
                campos.add(new AuxiliarReporte("Double", a.getEjecutado()));
                campos.add(new AuxiliarReporte("Double", a.getSaldoEjecutado()));
            }
        }
        xls.agregarFila(campos, false);
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
     * @return the rayaBaja
     */
    public String getRayaBaja() {
        return rayaBaja;
    }

    /**
     * @param rayaBaja the rayaBaja to set
     */
    public void setRayaBaja(String rayaBaja) {
        this.rayaBaja = rayaBaja;
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
     * @return the totales
     */
    public List<AuxiliarAsignacion> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarAsignacion> totales) {
        this.totales = totales;
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
}
