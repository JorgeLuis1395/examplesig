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
import org.icefaces.ace.model.chart.CartesianSeries;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCedulaMunicipioSfccbdmq")
@ViewScoped
public class ReporteCedulaMunicipioBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaMunicipioBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
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
    private Codigos fuenteFinanciamiento;
    private Date desde;
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
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private CartesianSeries datosBarras;
    private Resource reporte;

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
        llenar();
//        llenarSinfuente();

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
        String nombreForma = "ReporteCedulaMunicipioVista";
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

    private void llenar() {
        totalEgresos = 0;
        totalIngresos = 0;
        fuenteFinanciamiento = null;
        try {
            asignaciones = new LinkedList<>();
            List<AuxiliarAsignacion> listaAsig = new LinkedList<>();
            DocumentoXLS xls = new DocumentoXLS("Cédula Presupuestaria");
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

            titulo1 = "Devengado (f) ";
            titulo2 = "Saldo por Devengar (a-f) ";
            titulo3 = "Saldo por pagar (a-g)";
            titulo4 = "Pagado (g)";

            where = "o.anio=:anio and o.codigo like '____'";
            // titulos
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO PROGRAMA"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE PROGRAMA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CODIGO PROYECTO"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE PROYECTO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "GRUPO DEL GASTO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE PARTIDA"));
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
            xls.agregarFila(titulos, true);
            // fin titulos
            parametros.put(";where", where);
            parametros.put(";orden", "o.codigo");
            parametros.put("anio", anio);
            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
            List<AuxiliarReporte> columnas = new LinkedList<>();
//            List<Asignaciones> listaAisgnaciones = ejbAsignaciones.encontarParametros(parametros);
            for (Proyectos p : listaProyectos) {
                // traer la asignación sumada
                parametros = new HashMap();
                where = "o.proyecto.anio=:anio and o.proyecto.codigo like :proyecto";
                parametros.put(";campo", "o.clasificador.codigo");
                parametros.put(";orden", "o.clasificador.codigo");
                parametros.put(";suma", "sum(o.valor)");
                parametros.put(";where", where);
                parametros.put("proyecto", p.getCodigo() + "%");
                parametros.put("anio", anio);
                List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);
//            List<Asignaciones> listaAisgnaciones = ejbAsignaciones.encontarParametros(parametros);

                for (Object[] o : listaAgrupados) {
                    String codProyecto = p.getCodigo();
                    String codClasificador = (String) o[0];
                    BigDecimal valor = (BigDecimal) o[1];
                    AuxiliarAsignacion aProyecto = new AuxiliarAsignacion();
                    Clasificadores cla = clasificadorBean.traerCodigo(codClasificador);
                    aProyecto.setTitulo2(cla.getNombre());
                    // los dos anteriores
                    String codPrograma = codProyecto.substring(0, 2);
                    Proyectos pPrograma = proyectosBean.traerCodigoAnio(codPrograma, anio);
                    aProyecto.setProyectoEntidad(p);
                    aProyecto.setCodigo(pPrograma.getCodigo());
                    aProyecto.setNombre(pPrograma.getNombre());
                    // Grupo del gasto
                    Clasificadores claGasto = clasificadorBean.traerCodigoTodos(codClasificador.substring(0, 2));
                    aProyecto.setClasificadorEntidad(claGasto);
                    //
                    aProyecto.setTipo("ASIGNACION");
                    aProyecto.setTitulo1(codClasificador);

                    aProyecto.setFuente("");
                    double asignacion = valor.doubleValue();
                    aProyecto.setAsignacion(asignacion);
                    double reforma = calculosBean.
                            traerReformas(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    aProyecto.setReforma(reforma);
                    aProyecto.setCodificado(asignacion + reforma);
                    double certficicaciones
                            = calculosBean.
                                    traerCertificaciones(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    aProyecto.setCertificacion(certficicaciones);
                    aProyecto.setCertificado(aProyecto.getCodificado() - certficicaciones);
                    double compromisos = calculosBean.
                            traerCompromisos(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                    aProyecto.setCompromiso(compromisos);
                    aProyecto.setComprometido(aProyecto.getCodificado() - compromisos);

                    // saldo por devengar
                    if (p.getIngreso()) {
                        String fuente = null;
                        if (fuenteFinanciamiento != null) {
                            fuente = fuenteFinanciamiento.getCodigo();
                        }
                        double valorDevengado = calculosBean.traerDevengadoIngresosVista(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuente);
                        aProyecto.setDevengado(valorDevengado);
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
                        double valorEjecutado = calculosBean.traerEjecutadoIngresosVista(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuente);
                        aProyecto.setEjecutado(valorEjecutado);
                        aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                    } else {
                        double valorDevengado = calculosBean.traerDevengado(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        aProyecto.setDevengado(valorDevengado);
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);
                        double valorEjecutado = calculosBean.traerEjecutado(codProyecto, codClasificador, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
                        aProyecto.setEjecutado(valorEjecutado);
                        aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                    }
                    // ver el ejecutado que debe ser igual al devengado
                    // reporte
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", aProyecto.getProyectoEntidad().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", aProyecto.getProyectoEntidad().getNombre()));
                    columnas.add(new AuxiliarReporte("String", aProyecto.getCodigo()));
                    columnas.add(new AuxiliarReporte("String", aProyecto.getNombre()));
                    columnas.add(new AuxiliarReporte("String", aProyecto.getClasificadorEntidad().toString()));
                    columnas.add(new AuxiliarReporte("String", aProyecto.getTitulo1()));
                    columnas.add(new AuxiliarReporte("String", aProyecto.getTitulo2()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getAsignacion()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getReforma()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getCodificado()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getCertificacion()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getCertificado()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getCompromiso()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getComprometido()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getDevengado()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getSaldoDevengado()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getEjecutado()));
                    columnas.add(new AuxiliarReporte("Double", aProyecto.getSaldoEjecutado()));
                    xls.agregarFila(columnas, false);
                    // fin reporte
                    asignaciones.add(aProyecto);
                    aSuperTotal.setAsignacion(aProyecto.getAsignacion() + aSuperTotal.getAsignacion());
                    aSuperTotal.setCertificacion(aProyecto.getCertificacion() + aSuperTotal.getCertificacion());
                    aSuperTotal.setCertificado(aProyecto.getCertificado() + aSuperTotal.getCertificado());
                    aSuperTotal.setCodificado(aProyecto.getCodificado() + aSuperTotal.getCodificado());
                    aSuperTotal.setComprometido(aProyecto.getComprometido() + aSuperTotal.getComprometido());
                    aSuperTotal.setCompromiso(aProyecto.getCompromiso() + aSuperTotal.getCompromiso());
                    aSuperTotal.setDevengado(aProyecto.getDevengado() + aSuperTotal.getDevengado());
                    aSuperTotal.setSaldoDevengado(aProyecto.getSaldoDevengado() + aSuperTotal.getSaldoDevengado());
                    aSuperTotal.setReforma(aProyecto.getReforma() + aSuperTotal.getReforma());
                    aSuperTotal.setEjecutado(aProyecto.getEjecutado() + aSuperTotal.getEjecutado());
                    aSuperTotal.setSaldoEjecutado(aProyecto.getSaldoEjecutado() + aSuperTotal.getSaldoEjecutado());
                    aSuperTotal.setValor(aProyecto.getValor() + aSuperTotal.getValor());
                }

            }

            asignaciones.add(aSuperTotal);
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getAsignacion()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getReforma()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getCodificado()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getCertificacion()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getCertificado()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getCompromiso()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getComprometido()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getDevengado()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getSaldoDevengado()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getEjecutado()));
            columnas.add(new AuxiliarReporte("Double", aSuperTotal.getSaldoEjecutado()));
            xls.agregarFila(columnas, false);
            reporte = xls.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaMunicipioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReporteCedulaMunicipioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ReporteCedulaMunicipioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
    }
}
