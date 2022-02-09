/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import org.auxiliares.sfccbdmq.AuxiliarAnio;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
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
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteTrimestresSfccbdmq")
@ViewScoped
public class ReporteCedulaTrimestresBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaTrimestresBean() {
    }
    private int anio;
    private boolean ingresos;
    private List<AuxiliarAnio> asignaciones;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
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
    private IngresosFacade ejbIngresos;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private CodigosFacade ejbCodigos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
//    private boolean ingresos;
    private String nivel;
    private String digito;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private Resource recurso;

    /**
     * @return the asignaciones
     */
    public List<AuxiliarAnio> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<AuxiliarAnio> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String buscar() {
        llenarSinfuente();

        return null;
    }

    private void llenarSinfuente() {
        totalEgresos = 0;
        totalIngresos = 0;
        Calendar primerTrimestre = Calendar.getInstance();
        Calendar segundoTrimestre = Calendar.getInstance();
        Calendar tercerTrimestre = Calendar.getInstance();
        Calendar cuartoTrimestre = Calendar.getInstance();
        Calendar finAnio = Calendar.getInstance();
        primerTrimestre.setTime(configuracionBean.getConfiguracion().getPvigente());
        segundoTrimestre.setTime(configuracionBean.getConfiguracion().getPvigente());
        tercerTrimestre.setTime(configuracionBean.getConfiguracion().getPvigente());
        cuartoTrimestre.setTime(configuracionBean.getConfiguracion().getPvigente());
        finAnio.setTime(configuracionBean.getConfiguracion().getPvigente());
//        anio = ca.get(Calendar.YEAR);
        primerTrimestre.set(Calendar.YEAR, anio);
        segundoTrimestre.set(Calendar.YEAR, anio);
        tercerTrimestre.set(Calendar.YEAR, anio);
        cuartoTrimestre.set(Calendar.YEAR, anio);
        finAnio.set(Calendar.YEAR + 1, 0, 1);
        primerTrimestre.set(Calendar.DATE, 1);
        segundoTrimestre.set(Calendar.DATE, 1);
        tercerTrimestre.set(Calendar.DATE, 1);
        cuartoTrimestre.set(Calendar.DATE, 1);
        primerTrimestre.set(Calendar.MONTH, Calendar.JANUARY);
        segundoTrimestre.set(Calendar.MONTH, Calendar.APRIL);
        tercerTrimestre.set(Calendar.MONTH, Calendar.JUNE);
        cuartoTrimestre.set(Calendar.MONTH, Calendar.SEPTEMBER);
        try {
            // reporte

            DocumentoPDF pdf = new DocumentoPDF("PRESUPUESTO TRIMESTRAL DEL AÑO : \n" + anio,
                    seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, true, "NOMBRE"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, true, "NOMBRE"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "ASIGNACIÓN INICIAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "REFORMAS I-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "CERTIFICACIONES I-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "COMPROMISOS I-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "DEVENGADO I-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "PAGADO I-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "REFORMAS II-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "CERTIFICACIONES II-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "COMPROMISOS II-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "DEVENGADO II-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "PAGADO II-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "REFORMAS III-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "CERTIFICACIONES III-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "COMPROMISOS III-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "DEVENGADO III-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "PAGADO III-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "REFORMAS IV-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "CERTIFICACIONES IV-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "COMPROMISOS IV-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "DEVENGADO IV-T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "PAGADO IV-T"));
            List<AuxiliarReporte> columnas = new LinkedList<>();
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            proyectos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.ingreso=:ingresos and o.imputable=true");
            parametros.put("ingresos", ingresos);
            asignaciones = new LinkedList<>();
            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            // traer los clasificadores
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.imputable=true");
            List<Clasificadores> lisClasificadores = ejbClasificadores.encontarParametros(parametros);
            AuxiliarAnio aTotal = new AuxiliarAnio();
            aTotal.setNombre("*****  TOTAL *****");
            for (Proyectos p : pl) {
                // ingresa por todos los proyectos
                for (Clasificadores cl : lisClasificadores) {
                    // sumar asignaciones inicias
                    AuxiliarAnio a = new AuxiliarAnio();
                    a.setCodigo(p.getCodigo());
                    a.setNombre(p.getNombre());
                    a.setClasificador(cl);
                    a.setProyectos(p);
                    // Asignacion Inicial
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio and o.clasificador=:partida");
//                    parametros.put("codigo", p.getCodigo() + "%");
//                    parametros.put("partida", cl);
//                    parametros.put(";campo", "o.valor");
//                    parametros.put("anio", anio);
//                    double valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
                    double valorAsignacion = calculosBean.traerAsignaciones(anio,p.getCodigo(), cl.getCodigo(),null);
                    a.setAsignacion1(valorAsignacion);
                    // Reformas

                    double valorReformas = calculosBean.traerReformas(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
//                double valorReformas = traerReformas(p.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime());
                    a.setReforma1(valorReformas);
                    double valorReformas2 = calculosBean.traerReformas(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                    a.setReforma2(valorReformas2);
                    double valorReformas3 = calculosBean.traerReformas(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                    a.setReforma3(valorReformas3);
                    double valorReformas4 = calculosBean.traerReformas(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                    a.setReforma4(valorReformas4);
                    // fin reformas
                    //Certificaciones
                    if (valorAsignacion + valorReformas + valorReformas2 + valorReformas3 + valorReformas4 > 0) {
                        double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
                        a.setCertificacion1(valorCertificaciones);
                        double valorCertificaciones2 = calculosBean.traerCertificaciones(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                        a.setCertificacion2(valorCertificaciones2);
                        double valorCertificaciones3 = calculosBean.traerCertificaciones(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                        a.setCertificacion3(valorCertificaciones3);
                        double valorCertificaciones4 = calculosBean.traerCertificaciones(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                        a.setCertificacion4(valorCertificaciones4);
                        //fin Certificaciones
                        // comprometido
                        double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
                        a.setCompromiso1(valorCompromiso);
                        double valorCompromiso2 = calculosBean.traerCompromisos(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                        a.setCompromiso2(valorCompromiso2);
                        double valorCompromiso3 = calculosBean.traerCompromisos(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                        a.setCompromiso3(valorCompromiso3);
                        double valorCompromiso4 = calculosBean.traerCompromisos(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                        a.setCompromiso4(valorCompromiso4);
                        // fin comprometido
                        if (p.getIngreso()) {
                            // traer lo cobrado en ingresos
                            double valorDevengado = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
                            a.setDevengado1(valorDevengado);
                            double valorDevengado2 = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                            a.setDevengado2(valorDevengado2);
                            double valorDevengado3 = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                            a.setDevengado3(valorDevengado3);
                            double valorDevengado4 = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                            a.setDevengado4(valorDevengado4);
                            // ejecutado
                            // falta el ejecutado
                            double valorEjecutado = calculosBean.traerIngresos(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
                            a.setEjecutado1(valorEjecutado);
                            double valorEjecutado2 = calculosBean.traerIngresos(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                            a.setEjecutado2(valorEjecutado2);
                            double valorEjecutado3 = calculosBean.traerIngresos(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                            a.setEjecutado3(valorEjecutado3);
                            double valorEjecutado4 = calculosBean.traerIngresos(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                            a.setEjecutado4(valorEjecutado4);
                        } else {
                            // devengado
                            double valorDevengado = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
                            a.setDevengado1(valorDevengado);
                            double valorDevengado2 = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                            a.setDevengado2(valorDevengado2);
                            double valorDevengado3 = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                            a.setDevengado3(valorDevengado3);
                            double valorDevengado4 = calculosBean.traerDevengado(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                            a.setDevengado4(valorDevengado4);

                            // falta el ejecutado
                            double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(), cl.getCodigo(), primerTrimestre.getTime(), segundoTrimestre.getTime(), null);
                            a.setEjecutado1(valorEjecutado);
                            double valorEjecutado2 = calculosBean.traerEjecutado(p.getCodigo(), cl.getCodigo(), segundoTrimestre.getTime(), tercerTrimestre.getTime(), null);
                            a.setEjecutado2(valorEjecutado2);
                            double valorEjecutado3 = calculosBean.traerEjecutado(p.getCodigo(), cl.getCodigo(), tercerTrimestre.getTime(), cuartoTrimestre.getTime(), null);
                            a.setEjecutado3(valorEjecutado3);
                            double valorEjecutado4 = calculosBean.traerEjecutado(p.getCodigo(), cl.getCodigo(), cuartoTrimestre.getTime(), finAnio.getTime(), null);
                            a.setEjecutado4(valorEjecutado4);
                        }
                        // ejecutado

                        asignaciones.add(a);

                        //
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, false, a.getCodigo()));
                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, false, a.getNombre()));
                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, false, a.getClasificador().getCodigo()));
                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, false, a.getClasificador().getNombre()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getAsignacion1()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getReforma1()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCertificacion1()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCompromiso1()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getDevengado1()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getEjecutado1()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getReforma2()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCertificacion2()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCompromiso2()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getDevengado2()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getEjecutado2()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getReforma3()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCertificacion3()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCompromiso3()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getDevengado3()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getEjecutado3()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getReforma4()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCertificacion4()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getCompromiso4()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getDevengado4()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, a.getEjecutado4()));
                        //
                        if (p.getImputable()) {
                            aTotal.setAsignacion1(aTotal.getAsignacion1() + a.getAsignacion1());
                            aTotal.setCertificacion1(aTotal.getCertificacion1() + a.getCertificacion1());
                            aTotal.setCertificacion2(aTotal.getCertificacion2() + a.getCertificacion2());
                            aTotal.setCertificacion3(aTotal.getCertificacion3() + a.getCertificacion3());
                            aTotal.setCertificacion4(aTotal.getCertificacion4() + a.getCertificacion4());
                            aTotal.setComprometido1(aTotal.getComprometido1() + a.getComprometido1());
                            aTotal.setComprometido2(aTotal.getComprometido2() + a.getComprometido2());
                            aTotal.setComprometido3(aTotal.getComprometido3() + a.getComprometido3());
                            aTotal.setComprometido4(aTotal.getComprometido4() + a.getComprometido4());
                            aTotal.setDevengado1(aTotal.getDevengado1() + a.getDevengado1());
                            aTotal.setDevengado2(aTotal.getDevengado2() + a.getDevengado2());
                            aTotal.setDevengado3(aTotal.getDevengado3() + a.getDevengado3());
                            aTotal.setDevengado4(aTotal.getDevengado4() + a.getDevengado4());
                            aTotal.setEjecutado1(aTotal.getEjecutado1() + a.getEjecutado1());
                            aTotal.setEjecutado2(aTotal.getEjecutado2() + a.getEjecutado2());
                            aTotal.setEjecutado3(aTotal.getEjecutado3() + a.getEjecutado3());
                            aTotal.setEjecutado4(aTotal.getEjecutado4() + a.getEjecutado4());
                            aTotal.setReforma1(aTotal.getReforma1() + a.getReforma1());
                            aTotal.setReforma2(aTotal.getReforma2() + a.getReforma2());
                            aTotal.setReforma3(aTotal.getReforma3() + a.getReforma3());
                            aTotal.setReforma4(aTotal.getReforma4() + a.getReforma4());
                        }
                    }
                }
            }
            asignaciones.add(aTotal);
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 4, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, true, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, true, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 4, true, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getAsignacion1()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getReforma1()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCertificacion1()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCompromiso1()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getDevengado1()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getEjecutado1()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getReforma2()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCertificacion2()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCompromiso2()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getDevengado2()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getEjecutado2()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getReforma3()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCertificacion3()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCompromiso3()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getDevengado3()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getEjecutado3()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getReforma4()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCertificacion4()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getCompromiso4()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getDevengado4()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, aTotal.getEjecutado4()));
            pdf.agregarTablaReporte(titulos, columnas, 25, 100, null);
            recurso = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteTrimestresVista";
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

    public String getTitulo() {
        String titulo = "CEDULA PRESUPUEII-TARIA DE GAII-TOS";
        if (isIngresos()) {
            titulo = "CEDULA PRESUPUEII-TARIA DE INGRESOS";
        }
        return titulo;
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

    private double traerReformas(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.fecha >= :desde and fecha<:hasta");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        parametros.put(";campo", "o.valor");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerCertificaciones(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.certificacion.fecha >= :desde and o.certificacion.fecha<:hasta"
                + " and o.certificacion.impreso=true");
        try {
            return ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerCompromisos(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        parametros.put(";where", "o.detallecertificacion.asignacion.proyecto.codigo like :codigo "
                + "and o.compromiso.impresion is not null and o.fecha >=:desde and o.fecha<:hasta"
                + " ");
        try {
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerDevengado(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        parametros.put(";where", "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo like :codigo"
                + " and o.obligacion.estado=2 and o.obligacion.fechacontable>= :desde and o.obligacion.fechacontable<:hasta");
        try {
            double compras = ejbRasCompras.sumarCampo(parametros).doubleValue();
            // nomina
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificador like :codigo"
                    + " and o.fechapago between :desde and :hasta ");
            double valorNomina = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.encargo");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificadorencargo like :codigo"
                    + " and o.fechapago between :desde and :hasta ");
            double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.cantidad");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificadorencargo like :codigo"
                    + " and o.fechapago between :desde and :hasta  and o.concepto is null");
            double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);
            return compras + valorEncargo + valorNomina + valorSubrogacion;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerEjecutado(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        parametros.put(";where", "o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo like :codigo"
                + " and o.obligacion.estado=2 and  o.pagado>= :desde and o.o.pagado<:hasta");
        try {
            double compras = ejbRasCompras.sumarCampo(parametros).doubleValue();
            // nomina
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificador like :codigo"
                    + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null");
            double valorNomina = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.encargo");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificadorencargo like :codigo"
                    + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null");
            double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.cantidad");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificadorencargo like :codigo"
                    + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null and o.concepto is null");
            double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);
            return compras + valorEncargo + valorNomina + valorSubrogacion;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerIngresado(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", desde);
        parametros.put("desde", hasta);
        parametros.put(";where", "o.asigancion.proyecto.codigo like :codigo"
                + " and  o.fecha >=:desde and o.fecha<:hasta");
        try {
            return ejbIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerIngresadoEjecutado(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", desde);
        parametros.put("desde", hasta);
        parametros.put(";where", "o.asigancion.proyecto.codigo like :codigo"
                + " and  o.fecha >=:desde and o.fecha<:hasta and o.kardexbanco is not null");
        try {
            return ejbIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaTrimestresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
    }
}
