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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CodigosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.chart.Axis;
import org.icefaces.ace.component.chart.AxisType;
import org.icefaces.ace.model.chart.CartesianSeries;
import org.presupuestos.sfccbdmq.ReporteCedulaNivelesBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author sigi-iepi
 */
@ManagedBean(name = "reporteCedulaConsolidadaPoa")
@ViewScoped
public class ReporteCedulaConsolidadaPoaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaConsolidadaPoaBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
    private int ingegrtodos;
    private Formulario formulario = new Formulario();
    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private PartidaspoaFacade ejbClasificadores;
    @EJB
    private CodigosFacade ejbCodigos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private boolean mensual;
    private String partida;
    private String rayaBaja;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private Codigos fuenteFinanciamiento;
    private Date desde;
    private Date hasta;
    private AuxiliarAsignacion aSuperTotal;
    private String nivel;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosBean;
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
        llenarConFuente();
//        llenarSinfuente();
//        Map parametros = new HashMap();
//        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//        parametros.put("expresado", "C??dula consolidada : " + configuracionBean.getConfiguracion().getExpresado());
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
            MensajesErrores.fatal("Sin perfil v??lido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil v??lido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
                MensajesErrores.fatal("Sin perfil v??lido, grupo invalido");
                seguridadbean.cerraSession();
            }
        }
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
                    Logger.getLogger(ReporteCedulaConsolidadaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.codigo like :codigo ");
        parametros.put("codigo", a.getCodigo() + "%");
        parametros.put(";campo", "o.valor");
        try {
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaConsolidadaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
            List<Partidaspoa> cl = ejbClasificadores.encontarParametros(parametros);
            String digito = "1";
            for (Partidaspoa c : cl) {
                digito = c.getCodigo();
            }
            Codigos primerNivel = ejbCodigos.traerCodigo("FMTPTD", digito);
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

//    private double traerAsignacion(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.proyecto.codigo like :proyecto and o.proyecto.anio=:anio";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.clasificador.codigo like :codigo "
//                        + " and o.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put(";where", where);
//        try {
//            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerReformas(Proyectos p, Date pdesde, Date phasta) {
//        Map parametros = new HashMap();
//        String where = "o.asignacion.proyecto.codigo like :proyecto "
//                + "and o.asignacion.proyecto.anio=:anio and o.cabecera.definitivo=true and o.fecha between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.asignacion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.asignacion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += " "
//                        //                where += "and o.asignacion.clasificador.codigo like :codigo "
//                        + " and o.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", pdesde);
//        parametros.put("hasta", phasta);
//        parametros.put(";where", where);
//        try {
//            return ejbReformas.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerCertificaciones(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.asignacion.proyecto.codigo like :proyecto "
//                + " and o.asignacion.proyecto.anio=:anio "
//                + " and  o.certificacion.impreso=true "
//                + " and  o.certificacion.fecha between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.asignacion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.asignacion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.asignacion.clasificador.codigo like :codigo "
//                        + " and o.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        try {
//            return ejbDetCert.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerCompromisos(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.detallecertificacion.asignacion.proyecto.codigo like :proyecto "
//                + " and o.detallecertificacion.asignacion.proyecto.anio=:anio "
//                + " and  o.compromiso.impresion is not null and o.fecha between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.detallecertificacion.asignacion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.detallecertificacion.asignacion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.detallecertificacion.asignacion.clasificador.codigo like :codigo "
//                        + " and o.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        try {
//            return ejbDetComp.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerDevengadoIngresos(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.asigancion.proyecto.codigo like :proyecto "
//                + " and o.asigancion.proyecto.anio=:anio "
//                + " and   o.fecha between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.asigancion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.asigancion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.asigancion.clasificador.codigo like :codigo "
//                        + " and o.asigancion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        try {
//            return ejbIngresos.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoIngresos(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.asigancion.proyecto.codigo like :proyecto "
//                + " and o.asigancion.proyecto.anio=:anio "
//                + " and   o.fecha between :desde and :hasta and o.kardexbanco is not null";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.asigancion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.asigancion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.asigancion.clasificador.codigo like :codigo "
//                        + " and o.asigancion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        try {
//            return ejbIngresos.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerDevengadoObligaciones(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo like :proyecto "
//                + " and o.detallecompromiso.detallecertificacion.asignacion.proyecto.anio=:anio "
//                + " and  o.obligacion.estado=2 and o.obligacion.fechacontable between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo like :codigo "
//                        + " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        parametros.put(";campo", "o.valor+o.valorimpuesto");
//        try {
//            return ejbRasCompras.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerDevengadoRoles(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.detallecertificacion.asignacion.proyecto.codigo like :proyecto "
//                + " and o.detallecertificacion.asignacion.proyecto.anio=:anio "
//                + " and o.compromiso.nomina=true"
//                + " and  o.compromiso.impresion is not null and o.fecha between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.detallecertificacion.asignacion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.detallecertificacion.asignacion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.detallecertificacion.asignacion.clasificador.codigo like :codigo "
//                        + " and o.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        try {
//            return ejbDetComp.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoObligaciones(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo like :proyecto "
//                + " and o.detallecompromiso.detallecertificacion.asignacion.proyecto.anio=:anio "
//                + " and  o.obligacion.estado=2 and o.pagado between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo like :codigo "
//                        + " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        parametros.put(";campo", "o.valor+o.valorimpuesto");
//        try {
//            return ejbRasCompras.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoNomina(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.empleado.proyecto.codigo like :proyecto "
//                + " and o.empleado.proyecto.anio=:anio "
//                + " and  o.kardexbanco is not null "
//                + " and o.fechapago between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
////                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
////                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.clasificador like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.clasificador like :codigo ";
////                        + " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
////                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        try {
//            return ejbPagosEmpleados.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoNominaEncargo(Proyectos p) {
//        Map parametros = new HashMap();
//        String where = "o.empleado.proyecto.codigo like :proyecto "
//                + " and o.empleado.proyecto.anio=:anio "
//                + " and  o.kardexbanco is not null and o.concepto is null "
//                + " and o.fechapago between :desde and :hasta";
//        if ((partida == null) || (partida.isEmpty())) {
//            if (fuenteFinanciamiento == null) {
////                        parametros.put(";where", "o.proyecto.codigo like :proyecto");
//            } else {
////                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//////                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
////                parametros.put("fuente", fuenteFinanciamiento);
//            }
//
//        } else {
//            if (fuenteFinanciamiento == null) {
//                where += " and o.clasificadorencargo like :codigo ";
////                        parametros.put(";where", "o.proyecto=:proyecto and o.clasificador.codigo like :codigo ");
//                parametros.put("codigo", partida + "%");
//            } else {
//                where += "and o.clasificadorencargo like :codigo ";
////                        + " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("codigo", partida + "%");
////                parametros.put("fuente", fuenteFinanciamiento);
//            }
//        }
//        parametros.put("proyecto", p.getCodigo() + "%");
//        parametros.put("anio", anio);
//        parametros.put("desde", desde);
//        parametros.put("hasta", hasta);
//        parametros.put(";where", where);
//        parametros.put(";campo", "o.cantidad+o.encargo");
//        try {
//            return ejbPagosEmpleados.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaConsolidadaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }

    private void llenarConFuente() {
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
                hasta=cHasta.getTime();
            } else {
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            String where = "o.anio=:anio";
            if (ingegrtodos > 0) {
                boolean ing = ingegrtodos == 1;
                if (proyectosBean.getProyectoPadre() == null) {
                    where += " and o.ingreso=:ingreso";
                    parametros.put("ingreso", ing);
                } else {
                    where += " and upper(o.codigo) like :codigo and o.ingreso=:ingreso";
                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
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
                if (proyectosBean.getProyectoSeleccionado()== null) {
                } else {
                    where += " and upper(o.codigo) like :codigo ";
                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
                }
            }
//            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
//                where += " and upper(o.codigo) like :codigo1 ";
//                parametros.put("codigo1", rayaBaja);
//            }
            parametros.put("anio", anio);
            parametros.put(";where", where);

            List<Proyectospoa> pl = ejbProyectos.encontarParametros(parametros);
            List<Proyectospoa> pl1 = new LinkedList<>();
            int i = 0;
            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
                for (Proyectospoa p : pl) {
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
            for (Proyectospoa p : pl1) {
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(p.getCodigo());
                a.setNombre(p.getNombre());
                // Traer asignacion con el proyecto
                // buscar las cuentas
                double valorAisgnacion = calculosBean.traerAsignaciones(anio, p.getCodigo(), null, fuenteFinanciamiento.getCodigo());

                // reformas anteriores
                a.setAsignacion(valorAisgnacion);
                // reformas
                double valorReformas = calculosBean.traerReformas(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
                a.setReforma(valorReformas);
                // primmera columa de sumas 
                a.setCodificado(valorAisgnacion + valorReformas);
                // certificaciones
                double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
                a.setCertificacion(valorCertificaciones);
                // segundo campo sumado
                a.setCertificado(a.getCodificado() - valorCertificaciones);
                // ejecucion=comprometido?

                double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
                a.setCompromiso(valorCompromiso);
                a.setComprometido(a.getCodificado() - valorCompromiso);
                if (p.getIngreso()) {
//                    double valorDevengado = calculosBean.traerDevengado(p, null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
//                    a.setDevengado(valorDevengado);
                    // saldo por devengar
//                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);
                    // ejecutado
                    double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
                    a.setEjecutado(valorEjecutado);
                    // ver el ejecutado que debe ser igual al devengado
                    a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
                } else {
                    // devengado
//                    double valorDevengado = calculosBean.traerDevengado(p, null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
                    // nomina
//                    a.setDevengado(valorDevengado);
                    // saldo por devengar
//                    a.setSaldoDevengado(a.getCodificado() - valorDevengado);

                    // ejecutado
                    double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(), null, cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento.getCodigo());
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
                    .getLogger(ReporteCedulaConsolidadaPoaBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteCedulaConsolidadaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
            }
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
     * @return the calculosBean
     */
    public CalculosPoaBean getCalculosBean() {
        return calculosBean;
    }

    /**
     * @param calculosBean the calculosBean to set
     */
    public void setCalculosBean(CalculosPoaBean calculosBean) {
        this.calculosBean = calculosBean;
    }
}
