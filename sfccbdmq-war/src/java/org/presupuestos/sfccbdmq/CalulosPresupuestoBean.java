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
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallescomprasFacade;
import org.beans.sfccbdmq.Ejecutado2018Facade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.VistaAsignacionesFacade;
import org.beans.sfccbdmq.VistaCertificacionesFacade;
import org.beans.sfccbdmq.VistaCompromisosFacade;
import org.beans.sfccbdmq.VistaDevengadoFacade;
import org.beans.sfccbdmq.VistaEjecutadoFacade;
import org.beans.sfccbdmq.VistaEjecutadoRolesFacade;
import org.beans.sfccbdmq.VistaIngresosFacade;
import org.beans.sfccbdmq.VistaReformasFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Ejecutado2018;
import org.entidades.sfccbdmq.Ingresos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Reformas;
import org.entidades.sfccbdmq.VistaDevengado;
import org.entidades.sfccbdmq.VistaEjecutado;
import org.entidades.sfccbdmq.VistaEjecutadoRoles;
import org.entidades.sfccbdmq.VistaIngresos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "calulosPresupuesto")
@ViewScoped
public class CalulosPresupuestoBean {

    /**
     * @return the listadoDevengados
     */
    public List<VistaDevengado> getListadoDevengados() {
        return listadoDevengados;
    }

    /**
     * @param listadoDevengados the listadoDevengados to set
     */
    public void setListadoDevengados(List<VistaDevengado> listadoDevengados) {
        this.listadoDevengados = listadoDevengados;
    }

    /**
     * Creates a new instance of CalulosPresupuestoBean
     */
    public CalulosPresupuestoBean() {
    }

    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private DetallescomprasFacade ejbDetalleCompras;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private PagosvencimientosFacade ejbPagosVencimientos;
    @EJB
    private IngresosFacade ejbIngresos;
    @EJB
    private VistaAsignacionesFacade ejbVistaAsignaciones;
    @EJB
    private VistaReformasFacade ejbVistaReformas;
    @EJB
    private VistaEjecutadoFacade ejbVistaEjecutado;
    @EJB
    private VistaEjecutadoRolesFacade ejbVistaEjecutadoRoles;
    @EJB
    private VistaCompromisosFacade ejbVistaCompromiso;
    @EJB
    private VistaCertificacionesFacade ejbVistaCertificaciones;
    @EJB
    private VistaDevengadoFacade ejbVistaDevengado;
    @EJB
    private VistaIngresosFacade ejbVistaIngresos;
    @EJB
    private Ejecutado2018Facade ejbEjecutado2018;

    private Formulario formularioDevengado = new Formulario();
    private Formulario formularioEjecutado = new Formulario();
    private List<VistaDevengado> listadoDevengados;
    private List<Ingresos> listadoDevengadosIngresos;
    private List<VistaIngresos> listadoDevengadoIngresosVista;
    private List<VistaIngresos> listadoEjecutadoIngresosVista;
    private List<VistaEjecutado> listadoEjecutados;
    private List<Detallecompromiso> listadoEjecutadosDetalle;
    private List<VistaEjecutadoRoles> listadoEjecutadosRoles;

    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;

    public double traerAsignaciones(int anio, String proyecto, String clasificador, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.anioProyecto=:anio ";
        parametros.put("anio", anio);
        parametros.put(";campo", "o.valorAsignacion");
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }

        try {
            parametros.put(";where", where);
            return ejbVistaAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerAsignacionesAnterior(int anio, String proyecto, String clasificador, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.proyecto.anio=:anio and o.cerrado=true";
        parametros.put("anio", anio);
        parametros.put(";campo", "o.valor");
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuente);
        }

        try {
            parametros.put(";where", where);
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerReformas(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fechaReforma between :desde and :hasta and o.definitivo=true";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        parametros.put(";campo", "o.valorReforma");
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }

        try {
            parametros.put(";where", where);
            return ejbVistaReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerReformasAnt(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.cabecera.definitivo=true and o.fecha between :desde and :hasta";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }

        try {
            parametros.put(";where", where);
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerReformas(Asignaciones a, Date desde, Date hasta, Reformas r, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.cabecera.definitivo=true and o.fecha between :desde and :hasta";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (a != null) {
            where += " and o.asignacion=:asignacion";
            parametros.put("asignacion", a);
        }
        if (r != null) {
            where += " and o.id!=:reforma";
            parametros.put("reforma", r.getId());
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCertificaciones(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fechaCertificacion between :desde and :hasta ";
        parametros.put(";campo", "o.valorCertificacion");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        try {
            parametros.put(";where", where);
            double valorCerttificaciones = ejbVistaCertificaciones.sumarCampo(parametros).doubleValue();
            double valorCompromisos = traerCompromisosCertificacion(proyecto, clasificador, desde, hasta, fuente);
            return valorCerttificaciones - valorCompromisos;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCertificacionesAnt(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.certificacion.fecha between :desde and :hasta and o.certificacion.impreso=true";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCertificaciones(Asignaciones a, Date desde, Date hasta, Detallecertificaciones detalle, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.certificacion.fecha between :desde and :hasta and o.certificacion.impreso=true";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (a != null) {
            where += " and o.asignacion=:asignacion";
            parametros.put("asignacion", a);
        }
        if (detalle != null) {
            where += " and o.id!=:detalle";
            parametros.put("detalle", detalle.getId());
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCompromisosVista(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fechaCompromiso between :desde and :hasta";
        parametros.put(";campo", "o.valorCompromiso");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        try {
            parametros.put(";where", where);
            return ejbVistaCompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCompromisos(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
//        String where = "o.compromiso.fecha between :desde and :hasta and o.compromiso.impresion is not null";
//Fecha del detalle compromiso
//        String where = "o.fecha between :desde and :hasta and o.compromiso.impresion is not null and (o.compromiso.anulado=false or o.compromiso.anulado is null)";
        String where = "(o.fecha between :desde and :hasta or o.compromiso.fechaanulado between :desde and :hasta) and o.compromiso.impresion is not null";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCompromisosCertificacion(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
//        String where = "o.compromiso.fecha between :desde and :hasta and o.compromiso.impresion is not null and o.compromiso.certificacion is not null";
        String where = "(o.fecha between :desde and :hasta or o.compromiso.fechaanulado between :desde and :hasta) and o.compromiso.impresion is not null and o.compromiso.certificacion is not null";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerIngresos(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.kardexbanco is not null";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asigancion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asigancion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asigancion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerEjecutadoIngresosVista(String proyecto, String clasificador, Date desde, Date hasta, String fuente) {
        if (clasificador.equals("370102")) {
            Calendar f = Calendar.getInstance();
            f.setTime(desde);
            f.add(Calendar.DATE, -1);
            desde = f.getTime();
        }

        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.kardex is not null and o.tipo='Renglones_ejecutado'";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.partida like :clasificador";
            parametros.put("clasificador", clasificador + "%");

            Cuentas c = cuentasBean.traerCodigoSinPresupuesto(clasificador);
            if (c != null) {
                where += " and o.observacion like : observacion";
                parametros.put("observacion", c.getCodigo() + "%");
            }

        }
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.proyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.fuente like :fuente";
            parametros.put("fuente", fuente + "%");
        }

        try {
            parametros.put(";where", where);
            return ejbVistaIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerEjecutado(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            if (clasificador.equals("370102")) {
                Calendar f = Calendar.getInstance();
                f.setTime(desde);
                f.add(Calendar.DATE, -1);
                desde = f.getTime();
            }
        }
        Map parametros = new HashMap();
        String where = "o.fechaEjecutado between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";where", where);
        parametros.put(";campo", "o.valorDevengadoEjecutado");
        try {
            double valorEjecutado = ejbVistaEjecutado.sumarCampo(parametros).doubleValue();

            // nomina
            parametros = new HashMap();
            where = " o.fechaEjecucion  between :desde and :hasta";
            parametros.put(";campo", "o.valorEjecutado");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.partida like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente.getId());
            }
            parametros.put(";where", where);
            double valorNomina = ejbVistaEjecutadoRoles.sumarCampo(parametros).doubleValue();
            return valorEjecutado + valorNomina;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerEjecutadoAnt(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        try {
            Map parametros = new HashMap();
            String where = "o.pagado between :desde and :hasta and  o.obligacion.estado=2";
            parametros.put(";campo", "o.valor+o.valorimpuesto");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);

            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            parametros.put(";where", where);
            double valorEjecutado = ejbRasCompras.sumarCampo(parametros).doubleValue();

            // nomina
            parametros = new HashMap();
            where = " o.fechapago  between :desde and :hasta and o.kardexbanco is not null";
            parametros.put(";campo", "o.valor");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.empleado.proyecto.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.clasificador like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            parametros.put(";where", where);
            double valorNomina = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            where = " o.fechapago  between :desde and :hasta and o.kardexbanco is not null";
            parametros.put(";campo", "o.encargo");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.empleado.proyecto.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.clasificadorencargo like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            parametros.put(";where", where);
            double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
            parametros = new HashMap();
            where = " o.fechapago  between :desde and :hasta and o.kardexbanco is not null";
            parametros.put(";campo", "o.cantidad");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.empleado.proyecto.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.clasificadorencargo like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            parametros.put(";where", where);
            double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);
            return valorEjecutado + valorNomina + valorEncargo + valorSubrogacion;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EstadoEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<AuxiliarAsignacion> traerEjecutado(Asignaciones a) {
        List<AuxiliarAsignacion> retorno = new LinkedList<>();
        try {

            Map parametros = new HashMap();
            String where = " "
                    + "   o.obligacion.estado=2 "
                    + " and o.detallecompromiso.detallecertificacion.asignacion=:asignacion";

            parametros.put("asignacion", a);
            parametros.put(";where", where);
            List<Rascompras> lista = ejbRasCompras.encontarParametros(parametros);
            for (Rascompras r : lista) {
                // traer el pago vencimientos pero en un kardex puede estar 
                // traer el pagovencimientos

                parametros = new HashMap();
                where = "o.obligacion=:obligacion";
                parametros.put("obligacion", r.getObligacion());
                parametros.put(";where", where);
                List<Pagosvencimientos> pl = ejbPagosVencimientos.encontarParametros(parametros);
                Kardexbanco k = null;
                for (Pagosvencimientos p : pl) {
                    k = p.getKardexbanco();
                }
                if (k != null) {
                    AuxiliarAsignacion auxiliar = new AuxiliarAsignacion();
                    auxiliar.setCodigo(k.getSpi() == null ? "" : k.getSpi().getId().toString());
                    auxiliar.setDesde(k.getFechamov());
                    auxiliar.setHasta(k.getSpi() == null ? null : k.getSpi().getFecha());
                    auxiliar.setValor(r.getValor().doubleValue() + r.getValorimpuesto().doubleValue());
                    auxiliar.setNombre(k.getObservaciones());
                    auxiliar.setProyecto(k.getEgreso().toString());
                    auxiliar.setFuente(k.getBanco().getNombre());
                    retorno.add(auxiliar);
                }
            }
            // nomina
            parametros = new HashMap();
            where = " o.kardexbanco is not null "
                    + " and o.fuente=:fuente and o.empleado.proyecto.codigo = :proyecto and o.clasificador = :clasificador";
            parametros.put(";campo", "o.kardexbanco");
            parametros.put(";suma", "sum(o.valor)");
            parametros.put("fuente", a.getFuente());
            parametros.put("proyecto", a.getProyecto().getCodigo());
            parametros.put("clasificador", a.getClasificador().getCodigo());
            parametros.put(";where", where);
            List<Object[]> respuesta = ejbPagosEmpleados.sumar(parametros);
            List<Kardexbanco> listaK = new LinkedList<>();
            for (Object[] o : respuesta) {
                Kardexbanco k = (Kardexbanco) o[0];
                estaKardexbanco(listaK, k);
            }
            for (Kardexbanco k : listaK) {
                AuxiliarAsignacion auxiliar = new AuxiliarAsignacion();
                auxiliar.setCodigo(k.getSpi() == null ? "" : k.getSpi().getId().toString());
                auxiliar.setDesde(k.getFechamov());
                auxiliar.setHasta(k.getSpi() == null ? null : k.getSpi().getFecha());
                auxiliar.setValor(k.getValor().doubleValue());
                auxiliar.setNombre(k.getObservaciones());
                auxiliar.setProyecto(k.getEgreso().toString());
                auxiliar.setFuente(k.getBanco().getNombre());
                retorno.add(auxiliar);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EstadoEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    private void estaKardexbanco(List<Kardexbanco> lista, Kardexbanco k) {
        for (Kardexbanco k1 : lista) {
            if (k1.equals(k)) {
                return;
            }
        }
        lista.add(k);
    }

    public double traerDevengadoIngresos(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asigancion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asigancion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asigancion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerDevengadoIngresosVista(String proyecto, String clasificador, Date desde, Date hasta, String fuente) {
        if (clasificador.equals("370102")) {
            Calendar f = Calendar.getInstance();
            f.setTime(desde);
            f.add(Calendar.DATE, -1);
            desde = f.getTime();
        }
        Calendar fechaAsiento = Calendar.getInstance();
        fechaAsiento.setTime(desde);
        int anio = fechaAsiento.get(Calendar.YEAR);
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.tipo!='Renglones_ejecutado'";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.partida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
            if (anio != 2018) {
                if (clasificador.length() > 1) {
                    if (clasificador.substring(0, 2).equals("13")) {
                        where += " and o.tipo!='Renglones_devengado'";
                    }
                }
            }
        }
        if (!((proyecto == null) || (clasificador.isEmpty()))) {
            where += " and o.proyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.fuente like :fuente";
            parametros.put("fuente", fuente + "%");
        }
        try {
            parametros.put(";where", where);
            return ejbVistaIngresos.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerDevengadoObligaciones(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";where", where);
        parametros.put(";campo", "o.valorDevengado");
        try {
            double valorUno = ejbVistaDevengado.sumarCampo(parametros).doubleValue();

            return valorUno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void renglonesDevengadoIngreso(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        listadoDevengados = new LinkedList<>();
        listadoDevengadosIngresos = new LinkedList<>();
        setListadoEjecutados(new LinkedList<>());
        listadoEjecutadosDetalle = new LinkedList<>();
        listadoDevengadoIngresosVista = new LinkedList<>();
        listadoEjecutadoIngresosVista = new LinkedList<>();

        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
//        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fecha");

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asigancion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asigancion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asigancion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            listadoDevengadosIngresos = ejbIngresos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void renglonesDevengadoIngresoVista(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        listadoDevengados = new LinkedList<>();
        listadoDevengadosIngresos = new LinkedList<>();
        setListadoEjecutados(new LinkedList<>());
        listadoEjecutadosDetalle = new LinkedList<>();
        listadoDevengadoIngresosVista = new LinkedList<>();
        listadoEjecutadoIngresosVista = new LinkedList<>();
        Calendar fechaAsiento = Calendar.getInstance();
        fechaAsiento.setTime(desde);
        int anio = fechaAsiento.get(Calendar.YEAR);
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.tipo!='Renglones_ejecutado' and o.valor!=0";
//        parametros.put(";campo", "o.valor");

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asigancion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.partida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
            if (anio != 2018) {
                if (clasificador.length() > 1) {
                    if (clasificador.substring(0, 2).equals("13")) {
                        where += " and o.tipo!='Renglones_devengado'";
                    }
                }
            }
        }
        if (fuente != null) {
            where += " and o.asigancion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put(";orden", "o.fecha");
            parametros.put(";where", where);
            listadoDevengadoIngresosVista = ejbVistaIngresos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void renglonesEjecutadoIngresoVista(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        listadoDevengados = new LinkedList<>();
        listadoDevengadosIngresos = new LinkedList<>();
        setListadoEjecutados(new LinkedList<>());
        listadoEjecutadosDetalle = new LinkedList<>();
        listadoDevengadoIngresosVista = new LinkedList<>();
        listadoEjecutadoIngresosVista = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.kardex is not null and o.tipo='Renglones_ejecutado' and o.valor!=0";
//        parametros.put(";campo", "o.valor");

        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asigancion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.partida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asigancion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put(";orden", "o.fecha");
            parametros.put(";where", where);
            listadoEjecutadoIngresosVista = ejbVistaIngresos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // nuevo trae esta lista
    public void renglonesDevengado(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        listadoDevengados = new LinkedList<>();
        listadoDevengadosIngresos = new LinkedList<>();
        setListadoEjecutados(new LinkedList<>());
        listadoEjecutadosDetalle = new LinkedList<>();
        listadoDevengadoIngresosVista = new LinkedList<>();
        listadoEjecutadoIngresosVista = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fecha");
        parametros.put(";where", where);
//        parametros.put(";campo", "o.valorDevengado");
        try {
//            double valorUno = ejbVistaDevengado.sumarCampo(parametros).doubleValue();

            listadoDevengados = ejbVistaDevengado.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double traerDevengadoCotratos(Contratos contrato) {
        Map parametros = new HashMap();
        String where = "o.cabeceradoc.compromiso.contrato=:contrato";

        parametros.put("contrato", contrato);
        parametros.put(";where", where);
        parametros.put(";campo", "o.valor");
        try {
            double valorUno = ejbDetalleCompras.sumarCampo(parametros).doubleValue();

            return valorUno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerDevengadoObligaciones(Integer compromiso) {
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.origen='PAGOS LOTES' and o.compromisoId=:id";

        parametros.put("id", compromiso);
        parametros.put(";where", where);
        parametros.put(";campo", "o.valorDevengado");
        try {
            double valorUno = ejbVistaDevengado.sumarCampo(parametros).doubleValue();

            return valorUno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerDevengadoAntesVista(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = "o.fechaDevengado between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";where", where);
        parametros.put(";campo", "o.valorDevengadoEjecutado");
        try {
            double valorUno = ejbVistaEjecutado.sumarCampo(parametros).doubleValue();

            return valorUno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /////////////////////Este es el original lo cambio para usar el contable
    public double traerDevengado(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        return traerDevengadoObligaciones(proyecto, clasificador, desde, hasta, fuente);
//        return traerDevengadoObligaciones(proyecto, clasificador, desde, hasta, fuente) + traerDevengadoRoles(proyecto, clasificador, desde, hasta, fuente);
    }
///////////////////////////////

    private double traerDevengadoRoles(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = " o.compromisoNomina=true"
                //                + " and  o.compromiso.impresion is not null "
                + " and o.fechaCompromiso between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";where", where);
        parametros.put(";campo", "o.valorCompromiso");
        try {
            return ejbVistaCompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerDevengadoRolesAnt(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        Map parametros = new HashMap();
        String where = " o.compromiso.nomina=true"
                + " and  o.compromiso.impresion is not null "
                + " and o.compromiso.fecha between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.detallecertificacion.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.detallecertificacion.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.detallecertificacion.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";where", where);
        try {
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<Detallecompromiso> traerDevengadoRoles(Asignaciones a) {
        Map parametros = new HashMap();
        String where = " o.compromiso.nomina=true"
                + " and  o.compromiso.impresion is not null "
                + " and o.detallecertificacion.asignacion=:asignacion";
        parametros.put("asignacion", a);
        parametros.put(";where", where);
        try {
            return ejbDetComp.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double traerReformas(Asignaciones a, Date desde, Date hasta, Reformas r, Codigos fuente, int anio) {
        Map parametros = new HashMap();
        String where;
        if (anio == 0) {
            where = "o.cabecera.definitivo=true and o.anio=:anio";
            parametros.put(";campo", "o.valor");
            parametros.put("anio", anio);

        } else {
            where = "o.cabecera.definitivo=true and o.fecha between :desde and :hasta";
            parametros.put(";campo", "o.valor");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
        }

        if (a != null) {
            where += " and o.asignacion=:asignacion";
            parametros.put("asignacion", a);
        }
        if (r != null) {
            where += " and o.id!=:reforma";
            parametros.put("reforma", r.getId());
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCertificaciones(Asignaciones a, Date desde, Date hasta, Detallecertificaciones detalle, Codigos fuente, int anio) {
        Map parametros = new HashMap();
        String where;
        if (anio == 0) {
            where = "oanio=:anio";
            parametros.put(";campo", "o.valor");
        } else {

            where = "o.certificacion.fecha between :desde and :hasta and o.certificacion.impreso=true";
            parametros.put(";campo", "o.valor");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
        }

        if (a != null) {
            where += " and o.asignacion=:asignacion";
            parametros.put("asignacion", a);
        }
        if (detalle != null) {
            where += " and o.id!=:detalle";
            parametros.put("detalle", detalle.getId());
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalulosPresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void renglonesEjecutado(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        listadoDevengados = new LinkedList<>();
        listadoDevengadosIngresos = new LinkedList<>();
        setListadoEjecutados(new LinkedList<>());
        listadoEjecutadosDetalle = new LinkedList<>();
        listadoDevengadoIngresosVista = new LinkedList<>();
        listadoEjecutadoIngresosVista = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.asignacion.fuente.codigo=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fecha");
        parametros.put(";where", where);
//        parametros.put(";campo", "o.valorDevengado");
        try {
//            double valorUno = ejbVistaDevengado.sumarCampo(parametros).doubleValue();
            setListadoEjecutadosDetalle(ejbDetComp.encontarParametros(parametros));

            // nomina
            parametros = new HashMap();
            where = " o.fechaEjecucion  between :desde and :hasta";
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.partida like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente.getId());
            }
            parametros.put(";orden", "o.fechaEjecucion");
            parametros.put(";where", where);
            listadoEjecutadosRoles = ejbVistaEjecutadoRoles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void renglonesEjecutadoAnt(String proyecto, String clasificador, Date desde, Date hasta, Codigos fuente) {
        listadoDevengados = new LinkedList<>();
        listadoDevengadosIngresos = new LinkedList<>();
        setListadoEjecutados(new LinkedList<>());
        listadoDevengadoIngresosVista = new LinkedList<>();
        listadoEjecutadoIngresosVista = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "o.fechaEjecutado between :desde and :hasta";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.codigoProyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.codigoPartida like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (fuente != null) {
            where += " and o.codigoNombre=:fuente";
            parametros.put("fuente", fuente.getCodigo());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fechaEjecutado");
        parametros.put(";where", where);
//        parametros.put(";campo", "o.valorDevengado");
        try {
//            double valorUno = ejbVistaDevengado.sumarCampo(parametros).doubleValue();
            setListadoEjecutados(ejbVistaEjecutado.encontarParametros(parametros));

            // nomina
            parametros = new HashMap();
            where = " o.fechaEjecucion  between :desde and :hasta";
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            if (!((proyecto == null) || (proyecto.isEmpty()))) {
                where += " and o.codigo like :proyecto";
                parametros.put("proyecto", proyecto + "%");
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.partida like :clasificador";
                parametros.put("clasificador", clasificador + "%");
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente.getId());
            }
            parametros.put(";orden", "o.fechaEjecucion");
            parametros.put(";where", where);
            listadoEjecutadosRoles = ejbVistaEjecutadoRoles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double traerejecutado2018(String proyecto, String clasificador) {
        Map parametros = new HashMap();
        String where = "o.id is not null";
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.proyecto like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.partida=:partida";
            parametros.put("partida", clasificador);
        }
        parametros.put(";where", where);
        parametros.put(";campo", "o.valor");
        try {
            double valorUno = ejbEjecutado2018.sumarCampo(parametros).doubleValue();
            return valorUno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerejecutado2018Valor() {
        Map parametros = new HashMap();
        String where = "o.id is not null";
        parametros.put(";where", where);
        parametros.put(";campo", "o.valor");
        try {
            double valorUno = ejbEjecutado2018.sumarCampo(parametros).doubleValue();
            return valorUno;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteConsolidadaPartidaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the formularioDevengado
     */
    public Formulario getFormularioDevengado() {
        return formularioDevengado;
    }

    /**
     * @param formularioDevengado the formularioDevengado to set
     */
    public void setFormularioDevengado(Formulario formularioDevengado) {
        this.formularioDevengado = formularioDevengado;
    }

    /**
     * @return the listadoDevengadosIngresos
     */
    public List<Ingresos> getListadoDevengadosIngresos() {
        return listadoDevengadosIngresos;
    }

    /**
     * @param listadoDevengadosIngresos the listadoDevengadosIngresos to set
     */
    public void setListadoDevengadosIngresos(List<Ingresos> listadoDevengadosIngresos) {
        this.listadoDevengadosIngresos = listadoDevengadosIngresos;
    }

    /**
     * @return the formularioEjecutado
     */
    public Formulario getFormularioEjecutado() {
        return formularioEjecutado;
    }

    /**
     * @param formularioEjecutado the formularioEjecutado to set
     */
    public void setFormularioEjecutado(Formulario formularioEjecutado) {
        this.formularioEjecutado = formularioEjecutado;
    }

    /**
     * @return the listadoEjecutados
     */
    public List<VistaEjecutado> getListadoEjecutados() {
        return listadoEjecutados;
    }

    /**
     * @param listadoEjecutados the listadoEjecutados to set
     */
    public void setListadoEjecutados(List<VistaEjecutado> listadoEjecutados) {
        this.listadoEjecutados = listadoEjecutados;
    }

    /**
     * @return the listadoDevengadoIngresosVista
     */
    public List<VistaIngresos> getListadoDevengadoIngresosVista() {
        return listadoDevengadoIngresosVista;
    }

    /**
     * @param listadoDevengadoIngresosVista the listadoDevengadoIngresosVista to
     * set
     */
    public void setListadoDevengadoIngresosVista(List<VistaIngresos> listadoDevengadoIngresosVista) {
        this.listadoDevengadoIngresosVista = listadoDevengadoIngresosVista;
    }

    /**
     * @return the listadoEjecutadoIngresosVista
     */
    public List<VistaIngresos> getListadoEjecutadoIngresosVista() {
        return listadoEjecutadoIngresosVista;
    }

    /**
     * @param listadoEjecutadoIngresosVista the listadoEjecutadoIngresosVista to
     * set
     */
    public void setListadoEjecutadoIngresosVista(List<VistaIngresos> listadoEjecutadoIngresosVista) {
        this.listadoEjecutadoIngresosVista = listadoEjecutadoIngresosVista;
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
     * @return the listadoEjecutadosRoles
     */
    public List<VistaEjecutadoRoles> getListadoEjecutadosRoles() {
        return listadoEjecutadosRoles;
    }

    /**
     * @param listadoEjecutadosRoles the listadoEjecutadosRoles to set
     */
    public void setListadoEjecutadosRoles(List<VistaEjecutadoRoles> listadoEjecutadosRoles) {
        this.listadoEjecutadosRoles = listadoEjecutadosRoles;
    }

    /**
     * @return the listadoEjecutadosDetalle
     */
    public List<Detallecompromiso> getListadoEjecutadosDetalle() {
        return listadoEjecutadosDetalle;
    }

    /**
     * @param listadoEjecutadosDetalle the listadoEjecutadosDetalle to set
     */
    public void setListadoEjecutadosDetalle(List<Detallecompromiso> listadoEjecutadosDetalle) {
        this.listadoEjecutadosDetalle = listadoEjecutadosDetalle;
    }

}
