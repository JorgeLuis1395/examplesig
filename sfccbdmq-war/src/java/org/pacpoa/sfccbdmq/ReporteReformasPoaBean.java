/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Reformaspoa;
import org.errores.sfccbdmq.ConsultarException;
import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.AuxiliarCargaPoa;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteReformasPoa")
@ViewScoped
public class ReporteReformasPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    private List<Reformaspoa> reformas;
    private Formulario formulario = new Formulario();
    private List<AuxiliarCargaPoa> totales;

    private int anio;
    private double totalActividad;

    private Date vigente;
    private Date desde;
    private Date hasta;

    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificaciones;

    public ReporteReformasPoaBean() {
    }

    @PostConstruct
    private void activar() {
//        List<Codigos> configuracion = codigosBean.traerCodigoMaestro("CONF");
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        for (Codigos c : configuracion) {
//            try {
//                if (c.getCodigo().equals("PVGT")) {
//                    vigente = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PINI")) {
//                    desde = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PFIN")) {
//                    hasta = sdf.parse(c.getParametros());
//                }
//            } catch (ParseException ex) {
//                Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        vigente = getConfiguracionBean().getConfiguracion().getPvigentepresupuesto();
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);
    }

    public String buscar() {
        if (anio <= 0) {
            reformas = null;
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        String where = " o.cabecera.definitivo = true";
        if (proyectosPoaBean.getProyectoSeleccionado() != null) {
            where += " and o.asignacion.proyecto=:proyecto ";
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
        }
        if (partidasPoaBean.getPartidaPoa() != null) {
            where += " and o.asignacion.partida=:partida ";
            parametros.put("partida", partidasPoaBean.getPartidaPoa());
        }

        parametros.put(";where", where);
        parametros.put(";orden", "o.fecha desc");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
            parametros.put(";campo", "o.valor");
            totalActividad = ejbReformas.sumarCampo(parametros).doubleValue();
            calculaTotales();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void calculaTotales() {
        double totalIngreso = 0;
        double totalEgreso = 0;
        totales = new LinkedList<>();

        AuxiliarCargaPoa auxiliarCarga = new AuxiliarCargaPoa();
        auxiliarCarga.setFuente(null);
        auxiliarCarga.setIngresos(new BigDecimal(0));
        auxiliarCarga.setEgresos(new BigDecimal(0));
        totales.add(auxiliarCarga);

        for (Reformaspoa r : reformas) {
            Asignacionespoa a = r.getAsignacion();
            if (a != null) {
                for (AuxiliarCargaPoa auxCarga : totales) {

                    if (r.getValor().doubleValue() > 0) {
                        auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
                        totalIngreso += r.getValor().doubleValue();
                    } else {
                        auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
                        totalEgreso += r.getValor().doubleValue();
                    }

                } // fin for auxCarga
            }
        }// fin for reforma
//        AuxiliarCarga aux = new AuxiliarCarga();
//        aux.setFuente(null);
//        aux.setIngresos(new BigDecimal(totalIngreso));
//        aux.setEgresos(new BigDecimal(totalEgreso));
//        totales.add(aux);
    }

    public double getTotalReformas() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
        return calculaTotalReformas(r);
    }

    public double calculaTotalReformas(Reformaspoa r) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("subactividad", r.getAsignacion());
        parametros.put("id", r.getId());
        parametros.put(";where", "o.subactividad=:asignacion and o.cabecera.definitivo=true and o.id!=:id");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());

        return calculaTotalCertificaciones(r);
    }

    private double calculaTotalCertificaciones(Reformaspoa r) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("subactividad", r.getAsignacion());
        parametros.put(";where", "o.subactividad=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
        try {
            return ejbCertificaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosPoaBean() {
        return proyectosPoaBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosPoaBean proyectosPoaBean) {
        this.proyectosPoaBean = proyectosPoaBean;
    }

    /**
     * @return the reformas
     */
    public List<Reformaspoa> getReformas() {
        return reformas;
    }

    /**
     * @param reformas the reformas to set
     */
    public void setReformas(List<Reformaspoa> reformas) {
        this.reformas = reformas;
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
     * @return the totales
     */
    public List<AuxiliarCargaPoa> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCargaPoa> totales) {
        this.totales = totales;
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
     * @return the totalActividad
     */
    public double getTotalActividad() {
        return totalActividad;
    }

    /**
     * @param totalActividad the totalActividad to set
     */
    public void setTotalActividad(double totalActividad) {
        this.totalActividad = totalActividad;
    }

    /**
     * @return the vigente
     */
    public Date getVigente() {
        return vigente;
    }

    /**
     * @param vigente the vigente to set
     */
    public void setVigente(Date vigente) {
        this.vigente = vigente;
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
     * @param proyectosPoaBean the proyectosPoaBean to set
     */
    public void setProyectosPoaBean(ProyectosPoaBean proyectosPoaBean) {
        this.proyectosPoaBean = proyectosPoaBean;
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
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasPoaBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
    }

}
