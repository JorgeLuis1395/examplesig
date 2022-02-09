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
import org.entidades.sfccbdmq.Asignaciones;
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
@ManagedBean(name = "reporteConsolidadaPartidaSfccbdmq")
@ViewScoped
public class ReporteConsolidadaPartidaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteConsolidadaPartidaBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{reporteDetalleComp}")
    private ReporteDetalleCompBean reporteDetalleCompBean;
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
    private String proyecto;
    private String partida;
    private String rayaBaja;
    private String titulo1;
    private String titulo2;
    private String titulo3;
    private Codigos fuenteFinanciamiento;
    private Date desde;
    private Date hasta;
    private Date desdeInicio;
    private boolean mensual;
    private AuxiliarAsignacion aSuperTotal;
    private String nivel;
    private DocumentoXLS xls;
    private CartesianSeries datosBarras;
    private Resource reporte;
    private Resource reporteXls;

    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private CuentasFacade ejbCuentas;

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
        String nombreForma = "ReporteConsolidadaPartidaVista";
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

//    public double getValor() {
//        AuxiliarAsignacion a = asignaciones.get(formulario.getFila().getRowIndex());
//        switch (a.getTipo()) {
//            case "CUEN":
//                return a.getValor();
//            // sumar
//            case "FUEN":
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto.codigo=:proyecto ");
//                parametros.put("codigo", a.getCodigo());
//                parametros.put("proyecto", a.getFuente());
//                parametros.put(";campo", "o.valor");
//                try {
//
//                    return ejbAsignaciones.sumarCampo(parametros).doubleValue();
//                } catch (ConsultarException ex) {
//                    Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//                }
//        }
//
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.proyecto.codigo like :codigo ");
//        parametros.put("codigo", a.getCodigo() + "%");
//        parametros.put(";campo", "o.valor");
//        try {
//            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
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
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

//    private double traerAsignacion(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerReformas(Proyectos p, String partida, Date pdesde, Date phasta) {
//        Map parametros = new HashMap();
//        String where = "o.asignacion.proyecto.codigo like :proyecto "
//                + "and o.asignacion.proyecto.anio=:anio "
//                + "and o.cabecera.definitivo=true and o.fecha between :desde and :hasta";
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerCertificaciones(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerCompromisos(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerDevengadoIngresos(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerDevengadoObligaciones(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerDevengadoRoles(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoIngresos(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//    private double traerEjecutadoObligaciones(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoNomina(Proyectos p, String partida) {
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
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//
//    private double traerEjecutadoNominaEncargo(Proyectos p, String partida) {
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
//        parametros.put(";campo", "o.cantidad+o.encargo");
//        parametros.put(";where", where);
//        try {
//            return ejbPagosEmpleados.sumarCampo(parametros).doubleValue();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
//    private void llenarConFuente() {
//        totalEgresos = 0;
//        totalIngresos = 0;
//        boolean ing = false;
//        try {
//            List<AuxiliarAsignacion> listaAsig = new LinkedList<>();
//            Calendar ca = Calendar.getInstance();
//            aSuperTotal = new AuxiliarAsignacion();
//            ca.setTime(hasta);
//            anio = ca.get(Calendar.YEAR);
//            int mes = ca.get(Calendar.MONTH);
//            proyectos = new LinkedList<>();
//            Calendar cDesde = Calendar.getInstance();
//            Calendar cHasta = Calendar.getInstance();
//            if (mensual) {
//                cDesde.set(anio, mes, 1);
//                cHasta.set(anio, mes + 1, 1);
//                cHasta.add(Calendar.DATE, -1);
//                hasta = cHasta.getTime();
//            } else {
//                cDesde.setTime(desde);
//                cHasta.setTime(hasta);
//            }
//            Map parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
//            String where = "o.anio=:anio";
////            if (ingegrtodos > 0) {
//            ing = ingegrtodos == 1;
//            if ((proyecto == null) || (proyecto.isEmpty())) {
//                where += " and o.ingreso=:ingreso and o.imputable=true";
//                parametros.put("ingreso", ing);
//            } else {
//                where += " and upper(o.codigo) like :codigo and o.ingreso=:ingreso and o.imputable=true";
//                parametros.put("codigo", proyecto + "%");
//                parametros.put("ingreso", ing);
//            }
//            if (ing) {
//                setTitulo1("Recaudado (f)");
//                setTitulo2("Saldo por Recaudadar (a-f)");
//                setTitulo3("Saldo por cobrar (a-g)");
//            } else {
//                setTitulo1("Devengado (f) ");
//                setTitulo2("Saldo por Devengar (a-f) ");
//                setTitulo3("Saldo por pagar (a-g)");
//            }
////            } else {
////                if ((proyecto == null) || (proyecto.isEmpty())) {
////                } else {
////                    where += " and upper(o.codigo) like :codigo ";
////                    parametros.put("codigo", proyecto + "%");
////                }
////            }
//            parametros.put("anio", anio);
//            parametros.put(";where", where);
//
//            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
//
//            parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
//            asignaciones = new LinkedList<>();
//            if ((partida == null) || (partida.isEmpty())) {
//
//                if (!fuente) {
//                    parametros.put(";where", " o.ingreso=:ingreso and o.imputable=true");
//                } else {
//                    parametros.put(";where", " o.ingreso=:ingreso");
//                }
//            } else {
//                if (!fuente) {
//                    parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso and o.imputable=true");
//                    parametros.put("codigo", partida + "%");
//                } else {
//                    parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso");
//                    parametros.put("codigo", partida + "%");
//                }
//            }
//            parametros.put("ingreso", ing);
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
//            List<Clasificadores> cl1 = new LinkedList<>();
//            for (Clasificadores c : cl) {
//                if (c.getCodigo().length() <= nivel.length()) {
//                    cl1.add(c);
//                }
//            }
//            AuxiliarAsignacion a2 = new AuxiliarAsignacion();
//            for (Proyectos p : pl) {
//                for (Clasificadores c : cl1) {
//                    AuxiliarAsignacion a = new AuxiliarAsignacion();
//                    a.setCodigo(c.getCodigo());
//                    a.setNombre(c.getNombre());
//                    a.setProyecto(p.getNombre());
//                    a.setTipo(p.getCodigo());
//                    a.setProyectoEntidad(p);
//                    // Traer asignacion con el proyecto
//                    // buscar las cuentas
//                    double valorAisgnacion = calculosBean.traerAsignaciones(anio, p.getCodigo(), c.getCodigo(), fuenteFinanciamiento);
//                    a.setAsignacion(valorAisgnacion);
//                    // reformas
//                    double valorReformas = calculosBean.traerReformas(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    a.setReforma(valorReformas);
//                    // primmera columa de sumas 
//                    a.setCodificado(valorAisgnacion + valorReformas);
//                    // certificaciones
//                    double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    a.setCertificacion(valorCertificaciones);
//                    // segundo campo sumado
//                    a.setCertificado(a.getCodificado() - valorCertificaciones);
//                    // ejecucion=comprometido?
//
//                    double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                    a.setCompromiso(valorCompromiso);
//                    a.setComprometido(a.getCodificado() - valorCompromiso);
//                    if (p.getIngreso()) {
//                        double valorDevengado = calculosBean.traerDevengadoIngresos(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                        a.setDevengado(valorDevengado);
//                        // saldo por devengar
//                        a.setSaldoDevengado(a.getCodificado() - valorDevengado);
//                        // ejecutado
//                        double valorEjecutado = calculosBean.traerIngresos(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//                        a.setEjecutado(valorEjecutado);
//                        a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
//                    } else {
//                        // devengado
//                        double valorDevengado = calculosBean.traerDevengado(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//
//                        a.setDevengado(valorDevengado);
//                        // saldo por devengar
//                        a.setSaldoDevengado(a.getCodificado() - valorDevengado);
//
//                        // ejecutado
//                        double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(), c.getCodigo(), cDesde.getTime(), cHasta.getTime(), fuenteFinanciamiento);
//
//                        a.setEjecutado(valorEjecutado);
//                        a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
//                    }
//                    // ver el ejecutado que debe ser igual al devengado
//
//                    if (!a.isCero()) {
//                        asignaciones.add(a);
//                    }
//                    if (c.getCodigo().length() == 1) {
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
//                }
//
//            }
//            a2.setNombre("TOTALES");
//
//            asignaciones.add(a2);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger
//                    .getLogger(ReporteConsolidadaPartidaBean.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    private void llenarConFuenteAnt() {
//        totalEgresos = 0;
//        totalIngresos = 0;
//        boolean ing = false;
//        try {
//            asignaciones = new LinkedList<>();
////           List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
//            Calendar ca = Calendar.getInstance();
//            aSuperTotal = new AuxiliarAsignacion();
//            ca.setTime(desde);
//            Calendar diaAntesDesde = Calendar.getInstance();
//            diaAntesDesde.setTime(desde);
//            diaAntesDesde.add(Calendar.DATE, -1);
//            anio = ca.get(Calendar.YEAR);
//            proyectos = new LinkedList<>();
//            Map parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
//            String where = "o.anio=:anio";
//            if (ingegrtodos > 0) {
//                ing = ingegrtodos == 1;
//                if ((proyecto == null) || (proyecto.isEmpty())) {
//                    where += " and o.ingreso=:ingreso and o.codigo like '_'";
//                    parametros.put("ingreso", ing);
//                } else {
//                    where += " and upper(o.codigo) like :codigo and o.ingreso=:ingreso";
//                    parametros.put("codigo", proyecto + "%");
//                    parametros.put("ingreso", ing);
//                }
//                if (ing) {
//                    setTitulo1("Recaudado (f)");
//                    setTitulo2("Saldo por Recaudadar (a-f)");
//                    setTitulo3("Saldo por cobrar (a-g)");
//                } else {
//                    setTitulo1("Devengado (f) ");
//                    setTitulo2("Saldo por Devengar (a-f) ");
//                    setTitulo3("Saldo por pagar (a-g)");
//                }
//            } else {
//                if ((proyecto == null) || (proyecto.isEmpty())) {
//                } else {
//                    where += " and upper(o.codigo) like :codigo ";
//                    parametros.put("codigo", proyecto + "%");
//                }
//            }
////            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
////                where += " and upper(o.codigo) like :codigo1 ";
////                parametros.put("codigo1", rayaBaja);
////            }
//            parametros.put("anio", anio);
//            parametros.put(";where", where);
//
//            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
////            List<Proyectos> pl1 = new LinkedList<>();
////            int i = 0;
////            if (!(((rayaBaja == null) || (rayaBaja.equals("0"))))) {
////            for (Proyectos p : pl) {
////                if (p.getCodigo().length() <= rayaBaja.length()) {
////                    pl1.add(p);
////                }
////                i++;
////            }
////            } else {
////                pl1 = pl;
////            }
//            parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
//            asignaciones = new LinkedList<>();
//            if ((partida == null) || (partida.isEmpty())) {
//                parametros.put(";where", " o.ingreso=:ingreso");
////                parametros.put("codigo", nivel + "%");
//            } else {
//                parametros.put(";where", "o.codigo like :codigo and o.ingreso=:ingreso");
//                parametros.put("codigo", partida + "%");
//            }
////            parametros.put("longitud", nivel.length());
////            parametros.put("codigo", nivelBuscar);
//            parametros.put("ingreso", ing);
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
//            List<Clasificadores> cl1 = new LinkedList<>();
//            for (Clasificadores c : cl) {
//                if (c.getCodigo().length() <= nivel.length()) {
//                    cl1.add(c);
//                }
//            }
//            AuxiliarAsignacion a2 = new AuxiliarAsignacion();
//            for (Proyectos p : pl) {
//                for (Clasificadores c : cl1) {
//                    AuxiliarAsignacion a = new AuxiliarAsignacion();
//                    a.setCodigo(c.getCodigo());
//                    a.setNombre(c.getNombre());
//                    a.setProyecto(p.getNombre());
//                    a.setTipo(p.getCodigo());
//                    // Traer asignacion con el proyecto
//                    // buscar las cuentas
//                    double valorAisgnacion = traerAsignacion(p, c.getCodigo());
//                    double valorReformasAnteriores = traerReformas(p, c.getCodigo(),
//                            configuracionBean.getConfiguracion().getPinicial(), diaAntesDesde.getTime());
//                    a.setAsignacion(valorAisgnacion + valorReformasAnteriores);
//                    // reformas
//                    double valorReformas = traerReformas(p, c.getCodigo(), desde, hasta);
//                    a.setReforma(valorReformas);
//                    // primmera columa de sumas 
//                    a.setCodificado(valorAisgnacion + valorReformas);
//                    // certificaciones
//                    double valorCertificaciones = traerCertificaciones(p, c.getCodigo());
//                    a.setCertificacion(valorCertificaciones);
//                    // segundo campo sumado
//                    a.setCertificado(a.getCodificado() - valorCertificaciones);
//                    // ejecucion=comprometido?
//
//                    double valorCompromiso = traerCompromisos(p, c.getCodigo());
//                    a.setCompromiso(valorCompromiso);
//                    a.setComprometido(a.getCodificado() - valorCompromiso);
//                    if (p.getIngreso()) {
//                        double valorDevengado = traerDevengadoIngresos(p, c.getCodigo());
//                        a.setDevengado(valorDevengado);
//                        // saldo por devengar
//                        a.setSaldoDevengado(a.getCodificado() - valorDevengado);
//                        // ejecutado
//                        double valorEjecutado = traerEjecutadoIngresos(p, c.getCodigo());
//                        a.setEjecutado(valorEjecutado);
//                        a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
//                    } else {
//                        // devengado
//                        double valorDevengado = traerDevengadoObligaciones(p, c.getCodigo());
//                        // nomina
//                        double valorNomina = traerDevengadoRoles(p, c.getCodigo());
//                        double valorDev = valorDevengado + valorNomina;
//                        a.setDevengado(valorDev);
//                        // saldo por devengar
//                        a.setSaldoDevengado(a.getCodificado() - valorDev);
//
//                        // ejecutado
//                        double valorEjecutado = traerEjecutadoObligaciones(p, c.getCodigo());
//                        // nomina
//
//                        valorNomina = traerEjecutadoNomina(p, c.getCodigo());
//
//                        double valorEncargo = traerEjecutadoNominaEncargo(p, c.getCodigo());
//
//                        valorEjecutado += valorEncargo + valorNomina;
//                        a.setEjecutado(valorEjecutado);
//                        a.setSaldoEjecutado(a.getCodificado() - valorEjecutado);
//                    }
//                    // ver el ejecutado que debe ser igual al devengado
//
//                    if (!a.isCero()) {
//                        asignaciones.add(a);
//                    }
//                    if (c.getCodigo().length() == 1) {
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
//                }
//
//            }
////            AuxiliarAsignacion a = new AuxiliarAsignacion();
//            a2.setNombre("TOTALES");
////            for (AuxiliarAsignacion a1 : asignaciones) {
////                if (a.getCodigo().length() == 1) {
////                    a.setAsignacion(a1.getAsignacion() + a.getAsignacion());
////                    a.setAvance(a1.getAvance() + a.getAvance());
////                    a.setCertificacion(a1.getCertificacion() + a.getCertificacion());
////                    a.setCertificado(a1.getCertificado() + a.getCertificado());
////                    a.setCodificado(a1.getCodificado() + a.getCodificado());
////                    a.setComprometido(a1.getComprometido() + a.getComprometido());
////                    a.setCompromiso(a1.getCompromiso() + a.getCompromiso());
////                    a.setDevengado(a1.getDevengado() + a.getDevengado());
////                    a.setEjecutado(a1.getEjecutado() + a.getEjecutado());
////                    a.setReforma(a1.getReforma() + a.getReforma());
////                    a.setSaldoDevengado(a1.getSaldoDevengado() + a.getSaldoDevengado());
////                    a.setSaldoEjecutado(a1.getSaldoEjecutado() + a.getSaldoEjecutado());
////                    a.setValor(a1.getValor() + a.getValor());
////                }
////            }
//            asignaciones.add(a2);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger
//                    .getLogger(ReporteConsolidadaPartidaBean.class
//                            .getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    // lo nuevo
    private void llenar() {
        totalEgresos = 0;
        totalIngresos = 0;
        //Totales
        double asignacionTotal = 0;
        double reformaTotal = 0;
        double codificadaTotal = 0;
        double certificacionTotal = 0;
        double saldoCertificacionTotal = 0;
        double compromisoTotal = 0;
        double saldoCompromisoTotal = 0;
        double devengadoSaldoTotal = 0;
        double saldoDevengadoTotal = 0;
        double pagadoTotal = 0;
        double saldoPagadoTotal = 0;
        try {
            asignaciones = new LinkedList<>();
            List<Codigos> reporteCodigos = codigosBean.traerCodigoOrdenadoCodigo("CEDULA");

            Calendar ca = Calendar.getInstance();
            aSuperTotal = new AuxiliarAsignacion();
            ca.setTime(hasta);
            anio = ca.get(Calendar.YEAR);
            int mes = ca.get(Calendar.MONTH) + 1;
            proyectos = new LinkedList<>();
//            reporte
            // poner las cabeceras y el inicio del reporte
            DocumentoPDF pdf;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> titulosXls = new LinkedList<>();
            List<AuxiliarReporte> columnas = new LinkedList<>();
            int totalCol = 0;
            xls = new DocumentoXLS("Cédula");

            // egresos
            pdf = new DocumentoPDF("CEDULA PRESUPUESTARIA DE GASTOS\n AL :" + sdf.format(hasta) + " " + configuracionBean.getConfiguracion().getExpresado(), seguridadbean.getLogueado().getUserid());

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO_CEDULA"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO_CEDULA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PERIODO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ANIO"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "ANIO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_PROG"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_PROG"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROGRAMA"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROGRAMA"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_PROY"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_PROY"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROYECTO"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROYECTO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_PROD"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_PROD"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "ACTIVIDAD"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "ACTIVIDAD"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_OBRA"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_OBRA"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "OBRA"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "OBRA"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_FUENTE"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "COD_FUENTE"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulosXls.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGANCION"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "ASIGANCION"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REFORMAS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACIONES"));
//            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CERTIFICACIONES"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO CERTIFICADO"));
//            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO CERTIFICADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "COMPROMISOS"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO COMPROMISOS"));
//            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO COMPROMISOS"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DEVENGADO"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR DEVENGAR"));
//            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR DEVENGAR"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PAGADO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR PAGAR"));
            titulosXls.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "SALDO POR PAGAR"));
            totalCol = 20;
//            fin de reporte
            xls.agregarFila(titulosXls, true);
            Map parametros = new HashMap();
//            parametros.put(";orden", "o.imputable=true");
//            List<Clasificadores> listaClasi = ejbClasificadores.encontarParametros(parametros);
//            parametros.put(";orden", "o.codigo");
            String where;

            titulo1 = "Devengado (f) ";
            titulo2 = "Saldo por Devengar (a-f) ";
            titulo3 = "Saldo por pagar (a-g)";
//                titulo4 = "Pagado (g)";

            //Extras
            Codigos co = codigosBean.traerCodigoNombre("CEDULA", "Extras");
            String[] extra = co.getParametros().split("#");
            String t6c = extra[0];
            String t7c = extra[1];
            String t8c = extra[2];
            String t9c = extra[3];

            for (Codigos reporteCodigo : reporteCodigos) {
                where = "o.proyecto.anio=:anio and (";
                parametros = new HashMap();
                if (reporteCodigo.getParametros().contains(";")) {
                    String[] unidos = reporteCodigo.getParametros().split(";");
                    int i = 0;
                    String whereProyecto = "";
                    for (String queProyecto : unidos) {
                        if (i > 0) {
                            where += " or ";
                        }
                        i++;
                        where += " upper(o.proyecto.codigo) like :codigo_" + String.valueOf(i);
                        parametros.put("codigo_" + String.valueOf(i), queProyecto + "%");
                    }
                    where += ") ";

                } else {
                    where += " upper(o.proyecto.codigo) like :codigo )";
                    parametros.put("codigo", reporteCodigo.getParametros() + "%");
                }

                String[] titulosCodigos = reporteCodigo.getDescripcion().split("#");
                String t1c = titulosCodigos[0];
                String t2c = titulosCodigos[1];
                String t3c = titulosCodigos[2];
                String t4c = titulosCodigos[3];
                String t5c = titulosCodigos[4];

                parametros.put(";where", where);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.clasificador");
//                parametros.put(";campo", "o.proyecto,o.clasificador");
//                parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo");
                parametros.put(";orden", "o.clasificador.codigo");
                parametros.put(";suma", "sum(o.valor)");
                List<Object[]> listaAgrupados = ejbAsignaciones.sumar(parametros);
                for (Object[] o : listaAgrupados) {
                    aSuperTotal = new AuxiliarAsignacion();
                    Clasificadores cla = (Clasificadores) o[0];
                    aSuperTotal.setClasificadorEntidad(cla);
                    aSuperTotal.setTitulo1(cla.getCodigo());
//                    if (cla.getCodigo().equals("730404")) {
//                        MensajesErrores.advertencia("aqui");
//                    }
//                    aSuperTotal.setTitulo2(cla.getNombre());
                    String codClasificador = cla.getCodigo();
                    BigDecimal valor = (BigDecimal) o[1];
                    aSuperTotal.setAsignacion(valor.doubleValue());
                    //Total
                    asignacionTotal += valor.doubleValue();
//                    aSuperTotal.setTitulo1(reporteCodigo.getCodigo());
//                    aSuperTotal.setTitulo2(reporteCodigo.getNombre());
                    if (reporteCodigo.getParametros().contains(";")) {
                        String[] unidos = reporteCodigo.getParametros().split(";");
                        int i = 0;
                        for (String queProyecto : unidos) {
                            i++;
//                            Proyectos p = proyectosBean.traerCodigo(queProyecto);
                            String codProyecto = queProyecto;
                            AuxiliarAsignacion aProyecto = new AuxiliarAsignacion();
//                Proyectos p = proyectosBean.traerCodigo(codProyecto);
//                            aProyecto.setCodigo(queProyecto);
//                            aProyecto.setProyectoEntidad(p);
//                            aProyecto.setNombre(queProyecto);
                            //
                            

                            aProyecto.setCodigo(reporteCodigo.getNombre());
                            aProyecto.setNombre(t1c);
                            aProyecto.setTitulo1(t2c);
                            
                            aProyecto.setTipo("ASIGNACION");
                            aProyecto.setTitulo1(cla.getCodigo());
//                Clasificadores cla = clasificadorBean.traerCodigo(codClasificador);
                            aProyecto.setClasificadorEntidad(cla);
                            aProyecto.setTitulo2(cla.getNombre());
                            aProyecto.setFuente("");
                            double asignacion = valor.doubleValue();
//                            double asignacion = calculosBean.traerAsignaciones(anio, codProyecto, codClasificador, null);
                            aProyecto.setAsignacion(asignacion);
                            double reforma = calculosBean.
                                    traerReformas(codProyecto, codClasificador, desdeInicio, hasta, fuenteFinanciamiento);
                            aProyecto.setReforma(reforma);
                            aProyecto.setCodificado(asignacion + reforma);
                            double certficicaciones
                                    = calculosBean.
                                            traerCertificaciones(codProyecto, codClasificador, desdeInicio, hasta, fuenteFinanciamiento);
                            aProyecto.setCertificacion(certficicaciones);
                            aProyecto.setCertificado(aProyecto.getCodificado() - certficicaciones);
                            double compromisos = calculosBean.
                                    traerCompromisos(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento);
                            aProyecto.setCompromiso(compromisos);
                            aProyecto.setComprometido(aProyecto.getCodificado() - compromisos);
                            double valorDevengado = calculosBean.traerDevengado(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento);
                            aProyecto.setDevengado(valorDevengado);
                            // saldo por devengar
                            aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);

                            Calendar c = Calendar.getInstance();
                            c.setTime(desde);
                            int anioLocal = c.get(Calendar.YEAR);
                            double valorEjecutado = 0;
                            if (anioLocal == 2018) {
                                if (codProyecto.trim().length() > 6) {
                                    String codigo = codProyecto.substring(0, 7);
                                    valorEjecutado = calculosBean.traerejecutado2018(codigo, codClasificador);
                                    aProyecto.setEjecutado(valorEjecutado);
                                    aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                                }
                            } else {
//                                valorEjecutado = calculosBean.traerEjecutado(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento);
                                valorEjecutado = valorEjecutado(codProyecto, codClasificador);
                                aProyecto.setEjecutado(valorEjecutado);
                                aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                            }

                            aSuperTotal.setPeriodo(mes + "");
                            aSuperTotal.setAnio(anio + "");
                            aSuperTotal.setCodPrograma(t3c);
                            aSuperTotal.setCodProyecto(t4c);
                            aSuperTotal.setCodProducto(t5c);
                            aSuperTotal.setTipoCedula(t6c);
                            aSuperTotal.setCodobra(t7c);
                            aSuperTotal.setObra(t8c);
                            aSuperTotal.setCodFuente(t9c);
                            
                            // ver el ejecutado que debe ser igual al devengado
                            aSuperTotal.setReforma(aProyecto.getReforma() + aSuperTotal.getReforma());
//                            aSuperTotal.setCodificado(aProyecto.getCodificado() + aSuperTotal.getCodificado());
                            aSuperTotal.setCodificado(aSuperTotal.getAsignacion() + aSuperTotal.getReforma());
                            aSuperTotal.setCertificacion(aProyecto.getCertificacion() + aSuperTotal.getCertificacion());
//                            aSuperTotal.setCertificado(aProyecto.getCertificado() + aSuperTotal.getCertificado());
                            aSuperTotal.setCertificado(aSuperTotal.getCodificado() - aSuperTotal.getCertificacion());
                            aSuperTotal.setCompromiso(aProyecto.getCompromiso() + aSuperTotal.getCompromiso());
//                            aSuperTotal.setComprometido(aProyecto.getComprometido() + aSuperTotal.getComprometido());
                            aSuperTotal.setComprometido(aSuperTotal.getCodificado() - aSuperTotal.getCompromiso());
                            aSuperTotal.setDevengado(aProyecto.getDevengado() + aSuperTotal.getDevengado());
//                            aSuperTotal.setSaldoDevengado(aProyecto.getSaldoDevengado() + aSuperTotal.getSaldoDevengado());
                            aSuperTotal.setSaldoDevengado(aSuperTotal.getCodificado() - aSuperTotal.getDevengado());
                            aSuperTotal.setEjecutado(aProyecto.getEjecutado() + aSuperTotal.getEjecutado());
//                            aSuperTotal.setSaldoEjecutado(aProyecto.getSaldoEjecutado() + aSuperTotal.getSaldoEjecutado());
                            aSuperTotal.setSaldoEjecutado(aSuperTotal.getCodificado() - aSuperTotal.getEjecutado());
                            aSuperTotal.setValor(aProyecto.getValor() + aSuperTotal.getValor());
//                            asignaciones.add(aProyecto);
//                            filaReporte(columnas, aProyecto);
                        } // FIN DEL LAZO
                        String[] aux = reporteCodigo.getDescripcion().split("#");
                        aSuperTotal.setCodigo(reporteCodigo.getNombre());
                        aSuperTotal.setNombre(aux[0]);
                        aSuperTotal.setTitulo2(aux[1]);
                        aSuperTotal.setTipo("ASIGNACION");
                        asignaciones.add(aSuperTotal);
                        filaReporte(columnas, aSuperTotal);

                        //Totales
                        reformaTotal += aSuperTotal.getReforma();
                        codificadaTotal += aSuperTotal.getCodificado();
                        certificacionTotal += aSuperTotal.getCertificacion();
                        saldoCertificacionTotal += aSuperTotal.getCertificado();
                        compromisoTotal += aSuperTotal.getCompromiso();
                        saldoCompromisoTotal += aSuperTotal.getComprometido();
                        devengadoSaldoTotal += aSuperTotal.getDevengado();
                        saldoDevengadoTotal += aSuperTotal.getSaldoDevengado();
                        pagadoTotal += aSuperTotal.getEjecutado();
                        saldoPagadoTotal += aSuperTotal.getSaldoEjecutado();

                    } else {
//                        Proyectos p = (Proyectos) o[0];
//                        Clasificadores cla = (Clasificadores) o[1];
                        String codProyecto = reporteCodigo.getParametros();
//                        String codClasificador = cla.getCodigo();
//                        BigDecimal valor = (BigDecimal) o[2];
                        AuxiliarAsignacion aProyecto = new AuxiliarAsignacion();
//                Proyectos p = proyectosBean.traerCodigo(codProyecto);

                        aProyecto.setPeriodo(mes + "");
                        aProyecto.setAnio(anio + "");

                        aProyecto.setProyectoEntidad(null);
                        aProyecto.setCodigo(reporteCodigo.getNombre());
                        aProyecto.setNombre(t1c);
                        aProyecto.setTitulo1(t2c);

                        aProyecto.setCodPrograma(t3c);
                        aProyecto.setCodProyecto(t4c);
                        aProyecto.setCodProducto(t5c);

                        aProyecto.setTipoCedula(t6c);
                        aProyecto.setCodobra(t7c);
                        aProyecto.setObra(t8c);
                        aProyecto.setCodFuente(t9c);

                        aProyecto.setTipo("ASIGNACION");
                        aProyecto.setTitulo1(cla.getCodigo());
//                Clasificadores cla = clasificadorBean.traerCodigo(codClasificador);
                        aProyecto.setClasificadorEntidad(cla);
                        aProyecto.setTitulo2(cla.getNombre());
                        aProyecto.setFuente("");

                        //
                        aProyecto.setCodigo(reporteCodigo.getNombre());
                        aProyecto.setNombre(reporteCodigo.getDescripcion());
                        //

                        double asignacion = valor.doubleValue();
                        aProyecto.setAsignacion(asignacion);
                        double reforma = calculosBean.
                                traerReformas(codProyecto, codClasificador, desdeInicio, hasta, fuenteFinanciamiento);
                        aProyecto.setReforma(reforma);
                        aProyecto.setCodificado(asignacion + reforma);
                        double certficicaciones
                                = calculosBean.
                                        traerCertificaciones(codProyecto, codClasificador, desdeInicio, hasta, fuenteFinanciamiento);
                        aProyecto.setCertificacion(certficicaciones);
                        aProyecto.setCertificado(aProyecto.getCodificado() - certficicaciones);
                        double compromisos = calculosBean.
                                traerCompromisos(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento);
                        aProyecto.setCompromiso(compromisos);
                        aProyecto.setComprometido(aProyecto.getCodificado() - compromisos);
                        double valorDevengado = calculosBean.traerDevengado(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento);
                        aProyecto.setDevengado(valorDevengado);
                        // saldo por devengar
                        aProyecto.setSaldoDevengado(aProyecto.getCodificado() - valorDevengado);

                        Calendar c = Calendar.getInstance();
                        c.setTime(desde);
                        double anioLocal = c.get(Calendar.YEAR);
                        double valorEjecutado = 0;
                        if (anioLocal == 2018) {
                            if (codProyecto.trim().length() > 9) {
                                String codigo = codProyecto.substring(0, 10);
                                valorEjecutado = calculosBean.traerejecutado2018(codigo, codClasificador);
                                aProyecto.setEjecutado(valorEjecutado);
                                aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                            }
                        } else {
//                            valorEjecutado = calculosBean.traerEjecutado(codProyecto, codClasificador, desde, hasta, fuenteFinanciamiento);
                            valorEjecutado = valorEjecutado(codProyecto, codClasificador);
                            aProyecto.setEjecutado(valorEjecutado);
                            aProyecto.setSaldoEjecutado(aProyecto.getCodificado() - valorEjecutado);
                        }
                        asignaciones.add(aProyecto);
                        filaReporte(columnas, aProyecto);
                        // ver el ejecutado que debe ser igual al devengado

                        //Totales
                        reformaTotal += aProyecto.getReforma();
                        codificadaTotal += aProyecto.getCodificado();
                        certificacionTotal += aProyecto.getCertificacion();
                        saldoCertificacionTotal += aProyecto.getCertificado();
                        compromisoTotal += aProyecto.getCompromiso();
                        saldoCompromisoTotal += aProyecto.getComprometido();
                        devengadoSaldoTotal += aProyecto.getDevengado();
                        saldoDevengadoTotal += aProyecto.getSaldoDevengado();
                        pagadoTotal += aProyecto.getEjecutado();
                        saldoPagadoTotal += aProyecto.getSaldoEjecutado();

                    } // fin del ELSE

                } //Fin del Lazo 
            }
//            aSuperTotal.setFuente("total");
//            asignaciones.add(aSuperTotal);
//            filaReporte(columnas, aSuperTotal);
            pdf.agregarTablaReporte(titulos, columnas, totalCol, 100, "CEDULA PRESUPUESTARIA");
            reporte = pdf.traerRecurso();
            reporteXls = xls.traerRecurso();
            //Totales
            AuxiliarAsignacion aTotales = new AuxiliarAsignacion();
            aTotales.setTitulo1("Totales");
            aTotales.setAsignacion(asignacionTotal);
            aTotales.setReforma(reformaTotal);
            aTotales.setCodificado(codificadaTotal);
            aTotales.setCertificacion(certificacionTotal);
            aTotales.setCertificado(saldoCertificacionTotal);
            aTotales.setCompromiso(compromisoTotal);
            aTotales.setComprometido(saldoCompromisoTotal);
            aTotales.setDevengado(devengadoSaldoTotal);
            aTotales.setSaldoDevengado(saldoDevengadoTotal);
            if (anio == 2018) {
                aTotales.setEjecutado(calculosBean.traerejecutado2018Valor());
            } else {
                aTotales.setEjecutado(pagadoTotal);
            }
            aTotales.setSaldoEjecutado(saldoPagadoTotal);
            asignaciones.add(aTotales);

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filaReporte(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        List<AuxiliarReporte> columnas = new LinkedList<>();
//        for (Auxiliar au : proyectosBean.getTitulos()) {
//            String nombre = proyectosBean.dividir(au, a.getProyectoEntidad());
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, nombre));
//        }

        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTipoCedula()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTipoCedula()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getPeriodo()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getPeriodo()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getAnio()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getAnio()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodPrograma()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodPrograma()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
        filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodProyecto()));
        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodProyecto()));
        filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
        filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodProducto()));
        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodProducto()));
        filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodobra()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodobra()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getObra()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getObra()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodFuente()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodFuente()));
        filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
//        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
//        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
//        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
//        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
//        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
//        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
//        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
//        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
        filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
        xls.agregarFila(columnas, false);
    }

    private void filaReporteAnt(List<AuxiliarReporte> filas, AuxiliarAsignacion a) {
        List<AuxiliarReporte> columnas = new LinkedList<>();
        for (Auxiliar au : proyectosBean.getTitulos()) {
            String nombre = proyectosBean.dividir(au, a.getProyectoEntidad());
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, nombre));
        }
        if (ingegrtodos == 1) {
            // ingresos
            if (mensual) {
                // pocas columans preferible vertical
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
            }

        } else {
            // egresos
            if (mensual) {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
            } else {
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getCodigo()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getNombre()));
                filas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
                filas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo2()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getAsignacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getReforma()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCodificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificacion()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCertificado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getCompromiso()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getComprometido()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoDevengado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getEjecutado()));
                filas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, a.getSaldoEjecutado()));
            }
        }
        xls.agregarFila(columnas, false);
    }

    private void traerDetalleCompromiso(Date desde, Date hasta) {
        getReporteDetalleCompBean().setDesde(desdeInicio);
        getReporteDetalleCompBean().setHasta(hasta);
        getReporteDetalleCompBean().buscar();
        setDetalleCompromiso(getReporteDetalleCompBean().getListadoDetallecomprom());
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
            if (proyecto.equals(dc.getAsignacion().getProyecto().getCodigo().substring(0, 7)) && partida.equals(dc.getAsignacion().getClasificador().getCodigo())) {
                retorno += dc.getValorEjecutado();
            }
        }
        return retorno;

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
}
