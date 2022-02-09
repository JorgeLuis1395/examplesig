/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.EjecucionpoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.entidades.sfccbdmq.Reformaspoa;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "calculosPoa")
@ViewScoped
public class CalculosPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbDetCert;
    @EJB
    private EjecucionpoaFacade ejbEjecucionpoa;
    @EJB
    private DetallecompromisoFacade ejbDetComp;

    public CalculosPoaBean() {
    }

    public double traerAsignaciones(int anio, String proyecto, String partida, String fuente) {
        Map parametros = new HashMap();
        String where = "o.proyecto.anio=:anio and o.cerrado=true";
        parametros.put("anio", anio);
        parametros.put(";campo", "o.valor");
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.partida.codigo like :partida";
            parametros.put("partida", partida + "%");
        }
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuente);
        }

        try {
            parametros.put(";where", where);
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerReformas(String proyecto, String partida, Date desde, Date hasta, String fuente) {
        Map parametros = new HashMap();
        String where = "o.cabecera.definitivo=true and o.fecha between :desde and :hasta";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.asignacion.partida.codigo like :partida";
            parametros.put("partida", partida + "%");
        }
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }

        try {
            parametros.put(";where", where);
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerReformas(Asignacionespoa a, Date desde, Date hasta, Reformaspoa r, String fuente) {
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
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCertificaciones(String proyecto, String partida, Date desde, Date hasta, String fuente) {
        Map parametros = new HashMap();
        String where = "o.certificacion.fecha between :desde and :hasta and o.certificacion.impreso=true and o.certificacion.anulado=false";
        parametros.put(";campo", "o.valor");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((proyecto == null) || (proyecto.isEmpty()))) {
            where += " and o.asignacion.proyecto.codigo like :proyecto";
            parametros.put("proyecto", proyecto + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.asignacion.partida.codigo like :partida";
            parametros.put("partida", partida + "%");
        }
        if (!((fuente == null) || (fuente.isEmpty()))) {
            where += " and o.asignacion.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCertificaciones(Asignacionespoa a, Date desde, Date hasta, Detallecertificacionespoa detalle, String fuente) {
        try {
            Map parametros = new HashMap();
            String where = "o.certificacion.fecha between :desde and :hasta and o.certificacion.impreso=true and o.certificacion.anulado=false";
            parametros.put(";campo", "o.adjudicado");
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
            if (!((fuente == null) || (fuente.isEmpty()))) {
                where += " and o.asignacion.fuente=:fuente";
                parametros.put("fuente", fuente);
            }

            parametros.put(";where", where);

            double totalAdjudicado = ejbDetCert.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            where = "o.certificacion.fecha between :desde and :hasta and o.certificacion.impreso=true and o.certificacion.anulado=false and o.adjudicado is null";
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
            if (!((fuente == null) || (fuente.isEmpty()))) {
                where += " and o.asignacion.fuente=:fuente";
                parametros.put("fuente", fuente);
            }

            parametros.put(";where", where);

            double totalCertificado = ejbDetCert.sumarCampo(parametros).doubleValue();

            return totalCertificado + totalAdjudicado;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCompromisos(String proyecto, String partida, Date desde, Date hasta, String fuente) {
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
        parametros.put(";campo", "o.comprometido");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (proyecto != null) {
            where += " and o.proyecto.codigo=:proyecto";
            parametros.put("proyecto", proyecto);
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.partida.codigo=:partida";
            parametros.put("partida", partida);
        }
        if (fuente != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        try {
            parametros.put(";where", where);
            return ejbEjecucionpoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public double traerCompromisosCertificacion(String proyecto, String clasificador, Date desde, Date hasta, String fuente) {
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
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public double traerContabilidad(String proyecto, String clasificador, Date desde, Date hasta, String fuente) {
        Map parametros = new HashMap();
        String where = "(o.fecha between :desde and :hasta or o.compromiso.fechaanulado between :desde and :hasta) and o.compromiso.impresion is not null and o.compromiso.certificacion is null";
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
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerCompromisos(String proyecto, String partida, Date desde, Date hasta, String fuente, Integer id) {
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta";
        parametros.put(";campo", "o.comprometido");
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (proyecto != null) {
            where += " and o.proyecto.codigo=:proyecto";
            parametros.put("proyecto", proyecto);
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.partida.codigo=:partida";
            parametros.put("partida", partida);
        }
        if (fuente != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        if (id != null) {
            where += "  and o.id != :id";
            parametros.put("id", id);
        }
        try {
            parametros.put(";where", where);
            return ejbEjecucionpoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerEjecutado(String proyecto, String partida, Date desde, Date hasta, String fuente) {
        try {
            Map parametros = new HashMap();
            String where = "o.fecha between :desde and :hasta";
            parametros.put(";campo", "o.ejecutado");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            if (proyecto != null) {
                where += " and o.proyecto.codigo=:proyecto";
                parametros.put("proyecto", proyecto);
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and o.partida.codigo=:partida";
                parametros.put("partida", partida);
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            parametros.put(";where", where);
            return ejbEjecucionpoa.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerEjecutado(String proyecto, String partida, Date desde, Date hasta, String fuente, Integer id) {
        try {
            Map parametros = new HashMap();
            String where = "o.fecha between :desde and :hasta";
            parametros.put(";campo", "o.ejecutado");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            if (proyecto != null) {
                where += " and o.proyecto.codigo=:proyecto";
                parametros.put("proyecto", proyecto);
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and o.partida.codigo=:partida";
                parametros.put("partida", partida);
            }
            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            if (id != null) {
                where += "  and o.id != :id";
                parametros.put("id", id);
            }
            parametros.put(";where", where);
            return ejbEjecucionpoa.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CalculosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

}
