/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
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
@ManagedBean(name = "reporteAniosSfccbdmq")
@ViewScoped
public class ReporteCedulaAnioBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteCedulaAnioBean() {
    }
    private int anio;
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
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private boolean ingresos;
    private String nivel;
    private String digito;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;

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
        Calendar primerAnio = Calendar.getInstance();
        Calendar segundoAnio = Calendar.getInstance();
        Calendar tercerAnio = Calendar.getInstance();
        Calendar finPrimerAnio = Calendar.getInstance();
        Calendar finSegundoAnio = Calendar.getInstance();
        Calendar finTercerAnio = Calendar.getInstance();
//        anio = ca.get(Calendar.YEAR);
        primerAnio.set(Calendar.YEAR, 0, 1);
        segundoAnio.set(Calendar.YEAR - 1, 0, 1);
        tercerAnio.set(Calendar.YEAR - 2, 0, 1);

        finPrimerAnio.set(Calendar.YEAR + 1, 0, 1);
        finSegundoAnio.set(Calendar.YEAR, 0, 1);
        finTercerAnio.set(Calendar.YEAR - 1, 0, 1);

        try {
//            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
            proyectos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.ingreso=:ingresos and o.imputable=true");
            parametros.put("ingresos", ingresos);
            asignaciones = new LinkedList<>();
            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
            AuxiliarAnio aTotal = new AuxiliarAnio();
            aTotal.setNombre("*****  TOTAL *****");
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.imputable=true");
            List<Clasificadores> lisClasificadores = ejbClasificadores.encontarParametros(parametros);
            for (Proyectos p : pl) {
                // sumar asignaciones inicias
                for (Clasificadores cl : lisClasificadores) {
                    AuxiliarAnio a = new AuxiliarAnio();
                    a.setCodigo(p.getCodigo());
                    a.setNombre(p.getNombre());
                    a.setClasificador(cl);
                    a.setProyectos(p);
                    // Asignacion Inicial Año actual es 3
                    parametros = new HashMap();
                    parametros.put(";where", "o.proyecto.codigo "
                            + "like :codigo and o.proyecto.anio=:anio and o.clasificador.codigo like :clasificador");
                    parametros.put("codigo", p.getCodigo() + "%");
                    parametros.put("clasificador", cl.getCodigo()+ "%");
                    parametros.put(";campo", "o.valor");
                    parametros.put("anio", anio);
                    double valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
                    a.setAsignacion3(valorAsignacion);
                    // Asiganacion año -1
                    parametros = new HashMap();
                    parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio "
                            + "and o.clasificador.codigo like :clasificador");
                    parametros.put("codigo", p.getCodigo() + "%");
                    parametros.put("clasificador", cl.getCodigo()+ "%");
                    parametros.put(";campo", "o.valor");
                    parametros.put("anio", anio - 1);
                    valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
                    a.setAsignacion2(valorAsignacion);
                    // Asiganacion año -2
                    parametros = new HashMap();
                    parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio "
                            + "and o.clasificador.codigo like :clasificador");
                    parametros.put("codigo", p.getCodigo() + "%");
                    parametros.put("clasificador", cl.getCodigo()+ "%");
                    parametros.put(";campo", "o.valor");
                    parametros.put("anio", anio - 2);
                    valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
                    a.setAsignacion1(valorAsignacion);
                    //  Asignacion 4
                    a.setAsignacion4((a.getAsignacion1() + a.getAsignacion2() + a.getAsignacion3()) / 3);
                    valorAsignacion = (a.getAsignacion1() + a.getAsignacion2() + a.getAsignacion3());
                    // Reformas año actual es 3
                    // anño 1
                    double valorReformas = calculosBean.traerReformas(p.getCodigo(),cl.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime(),null);
                    a.setReforma1(valorReformas);
                    valorReformas = calculosBean.traerReformas(p.getCodigo(),cl.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime(),null);
                    a.setReforma2(valorReformas);
                    valorReformas = calculosBean.traerReformas(p.getCodigo(),cl.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime(),null);
                    a.setReforma3(valorReformas);
                    a.setReforma4((a.getReforma1() + a.getReforma2() + a.getReforma3()) / 3);
                    valorReformas = (a.getReforma1() + a.getReforma2() + a.getReforma3());
                    if (valorAsignacion + valorReformas > 0) {
                        // fin reformas
                        //Certificaciones
                        double valorCertificaciones = calculosBean.traerCertificaciones(p.getCodigo(),cl.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime(),null);
                        a.setCertificacion1(valorCertificaciones);
                        valorCertificaciones =  calculosBean.traerCertificaciones(p.getCodigo(),cl.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime(),null);
                        a.setCertificacion2(valorCertificaciones);
                        valorCertificaciones =  calculosBean.traerCertificaciones(p.getCodigo(),cl.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime(),null);
                        a.setCertificacion3(valorCertificaciones);
                        a.setCertificacion4((a.getCertificacion1() + a.getCertificacion2() + a.getCertificacion3()) / 3);
                        //fin Certificaciones
                        // comprometido
                        double valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(),cl.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime(),null);
                        a.setCompromiso1(valorCompromiso);
                        valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(),cl.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime(),null);
                        a.setCompromiso2(valorCompromiso);
                        valorCompromiso = calculosBean.traerCompromisos(p.getCodigo(),cl.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime(),null);
                        a.setCompromiso3(valorCompromiso);
                        a.setCompromiso4((a.getCompromiso1() + a.getCompromiso2() + a.getCompromiso3()) / 3);
                        // fin comprometido
                        if (p.getIngreso()) {
                            // traer lo cobrado en ingresos
                            double valorDevengado = traerIngresado(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
                            a.setDevengado1(valorDevengado);
                            valorDevengado = traerIngresado(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
                            a.setDevengado2(valorDevengado);
                            valorDevengado = traerIngresado(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
                            a.setDevengado3(valorDevengado);
                            a.setDevengado4((a.getDevengado1() + a.getDevengado2() + a.getDevengado3()) / 3);
                            // saldo por devengar
                            // falta el ejecutado
                            double valorEjecutado = traerIngresadoEjecutado(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
                            a.setEjecutado1(valorEjecutado);
                            valorEjecutado = traerIngresadoEjecutado(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
                            a.setEjecutado2(valorEjecutado);
                            valorEjecutado = traerIngresadoEjecutado(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
                            a.setEjecutado3(valorEjecutado);
                        } else {
                            // devengado
                            double valorDevengado = calculosBean.traerDevengado(p.getCodigo(),cl.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime(),null);
                            a.setDevengado1(valorDevengado);
                            valorDevengado = calculosBean.traerDevengado(p.getCodigo(),cl.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime(),null);
                            a.setDevengado2(valorDevengado);
                            valorDevengado = calculosBean.traerDevengado(p.getCodigo(),cl.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime(),null);
                            a.setDevengado3(valorDevengado);
                            a.setDevengado4((a.getDevengado1() + a.getDevengado2() + a.getDevengado3()) / 3);

                            // falta el ejecutado
                            double valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(),cl.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime(),null);
                            a.setEjecutado1(valorEjecutado);
                            valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(),cl.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime(),null);
                            a.setEjecutado2(valorEjecutado);
                            valorEjecutado = calculosBean.traerEjecutado(p.getCodigo(),cl.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime(),null);
                            a.setEjecutado3(valorEjecutado);
                        }
                        // ejecutado
                        asignaciones.add(a);
                        if (p.getImputable()) {
                            aTotal.setAsignacion1(aTotal.getAsignacion1() + a.getAsignacion1());
                            aTotal.setAsignacion2(aTotal.getAsignacion2() + a.getAsignacion2());
                            aTotal.setAsignacion3(aTotal.getAsignacion3() + a.getAsignacion3());
                            aTotal.setAsignacion4(aTotal.getAsignacion4() + a.getAsignacion4());
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

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    private void llenarSinfuente1() {
//        totalEgresos = 0;
//        totalIngresos = 0;
//        Calendar primerAnio = Calendar.getInstance();
//        Calendar segundoAnio = Calendar.getInstance();
//        Calendar tercerAnio = Calendar.getInstance();
//        Calendar finPrimerAnio = Calendar.getInstance();
//        Calendar finSegundoAnio = Calendar.getInstance();
//        Calendar finTercerAnio = Calendar.getInstance();
////        anio = ca.get(Calendar.YEAR);
//        primerAnio.set(Calendar.YEAR, 0, 1);
//        segundoAnio.set(Calendar.YEAR - 1, 0, 1);
//        tercerAnio.set(Calendar.YEAR - 2, 0, 1);
//
//        finPrimerAnio.set(Calendar.YEAR + 1, 0, 1);
//        finSegundoAnio.set(Calendar.YEAR, 0, 1);
//        finTercerAnio.set(Calendar.YEAR - 1, 0, 1);
//
//        try {
////            List<Clasificadores> cl = ejbClasificadores.encontarParametros(parametros);
//            proyectos = new LinkedList<>();
//            Map parametros = new HashMap();
//            parametros.put(";orden", "o.codigo");
//            asignaciones = new LinkedList<>();
//            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
//            AuxiliarAnio aTotal = new AuxiliarAnio();
//            aTotal.setNombre("*****  TOTAL *****");
//            for (Proyectos p : pl) {
//                // sumar asignaciones inicias
//                AuxiliarAnio a = new AuxiliarAnio();
//                a.setCodigo(p.getCodigo());
//                a.setNombre(p.getNombre());
//                // Asignacion Inicial Año actual es 3
//                parametros = new HashMap();
//                parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio");
//                parametros.put("codigo", p.getCodigo() + "%");
//                parametros.put(";campo", "o.valor");
//                parametros.put("anio", anio);
//                double valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
//                a.setAsignacion3(valorAsignacion);
//                // Asiganacion año -1
//                parametros = new HashMap();
//                parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio");
//                parametros.put("codigo", p.getCodigo() + "%");
//                parametros.put(";campo", "o.valor");
//                parametros.put("anio", anio - 1);
//                valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
//                a.setAsignacion2(valorAsignacion);
//                // Asiganacion año -2
//                parametros = new HashMap();
//                parametros.put(";where", "o.proyecto.codigo like :codigo and o.proyecto.anio=:anio");
//                parametros.put("codigo", p.getCodigo() + "%");
//                parametros.put(";campo", "o.valor");
//                parametros.put("anio", anio - 2);
//                valorAsignacion = ejbAsignaciones.sumarCampo(parametros).doubleValue();
//                a.setAsignacion1(valorAsignacion);
//                //  Asignacion 4
//                a.setAsignacion4((a.getAsignacion1() + a.getAsignacion2() + a.getAsignacion3()) / 3);
//                // Reformas año actual es 3
//                // anño 1
//                double valorReformas = traerReformas(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                a.setReforma1(valorReformas);
//                valorReformas = traerReformas(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                a.setReforma2(valorReformas);
//                valorReformas = traerReformas(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                a.setReforma3(valorReformas);
//                a.setReforma4((a.getReforma1() + a.getReforma2() + a.getReforma3()) / 3);
//                // fin reformas
//                //Certificaciones
//                double valorCertificaciones = traerCertificaciones(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                a.setCertificacion1(valorCertificaciones);
//                valorCertificaciones = traerCertificaciones(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                a.setCertificacion2(valorCertificaciones);
//                valorCertificaciones = traerCertificaciones(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                a.setCertificacion3(valorCertificaciones);
//                a.setCertificacion4((a.getCertificacion1() + a.getCertificacion2() + a.getCertificacion3()) / 3);
//                //fin Certificaciones
//                // comprometido
//                double valorCompromiso = traerCompromisos(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                a.setCompromiso1(valorCompromiso);
//                valorCompromiso = traerCompromisos(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                a.setCompromiso2(valorCompromiso);
//                valorCompromiso = traerCompromisos(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                a.setCompromiso3(valorCompromiso);
//                a.setCompromiso4((a.getCompromiso1() + a.getCompromiso2() + a.getCompromiso3()) / 3);
//                // fin comprometido
//                if (p.getIngreso()) {
//                    // traer lo cobrado en ingresos
//                    double valorDevengado = traerIngresado(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                    a.setDevengado1(valorDevengado);
//                    valorDevengado = traerIngresado(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                    a.setDevengado2(valorDevengado);
//                    valorDevengado = traerIngresado(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                    a.setDevengado3(valorDevengado);
//                    a.setDevengado4((a.getDevengado1() + a.getDevengado2() + a.getDevengado3()) / 3);
//                    // saldo por devengar
//                    // falta el ejecutado
//                    double valorEjecutado = traerIngresadoEjecutado(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                    a.setEjecutado1(valorEjecutado);
//                    valorEjecutado = traerIngresadoEjecutado(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                    a.setEjecutado2(valorEjecutado);
//                    valorEjecutado = traerIngresadoEjecutado(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                    a.setEjecutado3(valorEjecutado);
//                } else {
//                    // devengado
//                    double valorDevengado = traerDevengado(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                    a.setDevengado1(valorDevengado);
//                    valorDevengado = traerDevengado(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                    a.setDevengado2(valorDevengado);
//                    valorDevengado = traerDevengado(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                    a.setDevengado3(valorDevengado);
//                    a.setDevengado4((a.getDevengado1() + a.getDevengado2() + a.getDevengado3()) / 3);
//
//                    // falta el ejecutado
//                    double valorEjecutado = traerEjecutado(p.getCodigo(), tercerAnio.getTime(), finTercerAnio.getTime());
//                    a.setEjecutado1(valorEjecutado);
//                    valorEjecutado = traerEjecutado(p.getCodigo(), segundoAnio.getTime(), finSegundoAnio.getTime());
//                    a.setEjecutado2(valorEjecutado);
//                    valorEjecutado = traerEjecutado(p.getCodigo(), primerAnio.getTime(), finPrimerAnio.getTime());
//                    a.setEjecutado3(valorEjecutado);
//                }
//                // ejecutado
//                asignaciones.add(a);
//                aTotal.setAsignacion1(aTotal.getAsignacion1() + a.getAsignacion1());
//                aTotal.setAsignacion2(aTotal.getAsignacion2() + a.getAsignacion2());
//                aTotal.setAsignacion3(aTotal.getAsignacion3() + a.getAsignacion3());
//                aTotal.setAsignacion4(aTotal.getAsignacion4() + a.getAsignacion4());
//                aTotal.setCertificacion1(aTotal.getCertificacion1() + a.getCertificacion1());
//                aTotal.setCertificacion2(aTotal.getCertificacion2() + a.getCertificacion2());
//                aTotal.setCertificacion3(aTotal.getCertificacion3() + a.getCertificacion3());
//                aTotal.setCertificacion4(aTotal.getCertificacion4() + a.getCertificacion4());
//                aTotal.setComprometido1(aTotal.getComprometido1() + a.getComprometido1());
//                aTotal.setComprometido2(aTotal.getComprometido2() + a.getComprometido2());
//                aTotal.setComprometido3(aTotal.getComprometido3() + a.getComprometido3());
//                aTotal.setComprometido4(aTotal.getComprometido4() + a.getComprometido4());
//                aTotal.setDevengado1(aTotal.getDevengado1() + a.getDevengado1());
//                aTotal.setDevengado2(aTotal.getDevengado2() + a.getDevengado2());
//                aTotal.setDevengado3(aTotal.getDevengado3() + a.getDevengado3());
//                aTotal.setDevengado4(aTotal.getDevengado4() + a.getDevengado4());
//                aTotal.setEjecutado1(aTotal.getEjecutado1() + a.getEjecutado1());
//                aTotal.setEjecutado2(aTotal.getEjecutado2() + a.getEjecutado2());
//                aTotal.setEjecutado3(aTotal.getEjecutado3() + a.getEjecutado3());
//                aTotal.setEjecutado4(aTotal.getEjecutado4() + a.getEjecutado4());
//                aTotal.setReforma1(aTotal.getReforma1() + a.getReforma1());
//                aTotal.setReforma2(aTotal.getReforma2() + a.getReforma2());
//                aTotal.setReforma3(aTotal.getReforma3() + a.getReforma3());
//                aTotal.setReforma4(aTotal.getReforma4() + a.getReforma4());
//
//            }
//            asignaciones.add(aTotal);
//
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

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
        String nombreForma = "ReporteAnioVista";
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
        String titulo = "CEDULA PRESUPUESTARIA DE GASTOS";
        if (ingresos) {
            titulo = "CEDULA PRESUPUESTARIA DE INGRESOS";
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

    private double traerReformasAnt(String codigo, Date desde, Date hasta) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.fecha >= :desde and fecha<:hasta");
        parametros.put("codigo", codigo + "%");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        parametros.put(";campo", "o.valor");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
                + "and o.compromiso.impresion is not null and o.fecha>=:desde and o.fecha<:hasta"
                + " ");
        try {
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
            double valorDevengado = ejbRasCompras.sumarCampo(parametros).doubleValue();
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
                    + " and o.fechapago between :desde and :hasta and o.kardexbanco is null");
            double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.cantidad");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificadorencargo like :codigo"
                    + " and o.fechapago between :desde and :hasta  and o.concepto is null");
            double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);
            return valorDevengado + valorEncargo + valorNomina + valorSubrogacion;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
                + " and  o.fecha >=:desde and o.contabilizar<:hasta");
        try {
            return ejbIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
                + " and  o.fecha >=:desde and o.contabilizar<:hasta and o.kardexbanco is not null");
        try {
            return ejbIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
                + " and o.obligacion.estado=2 and o.pagado >= :desde  and o.pagado<:hasta");
        try {
            double valorEjecutado = ejbRasCompras.sumarCampo(parametros).doubleValue();
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
                    + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null and o.concepto is null");
            double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            parametros.put(";campo", "o.cantidad");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.clasificadorencargo like :codigo"
                    + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null and o.concepto is null");
            double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);;
            return valorEjecutado + valorEncargo + valorNomina + valorSubrogacion;
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaAnioBean.class.getName()).log(Level.SEVERE, null, ex);
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
}
