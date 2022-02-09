/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.CertificacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Certificacionespoa;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.errores.sfccbdmq.ConsultarException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "reporteValoresPoaPac")
@ViewScoped
public class ReporteValoresPoaPacBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    private Formulario formulario = new Formulario();
    private List<Certificacionespoa> certificaciones;
    private Date desde;
    private Date hasta;
    private Date desdeinicio;

    @EJB
    private DetallecertificacionespoaFacade ejbDetalle;
    @EJB
    private CertificacionespoaFacade ejbCertificacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;

    public ReporteValoresPoaPacBean() {
    }

    @PostConstruct
    private void activar() {
        setDesde(getConfiguracionBean().getConfiguracion().getPinicial());
        setHasta(getConfiguracionBean().getConfiguracion().getPfinal());

        Calendar ca = Calendar.getInstance();
        ca.setTime(getConfiguracionBean().getConfiguracion().getPinicial());
        int anio = ca.get(Calendar.YEAR);
        Calendar fechaInicio = Calendar.getInstance();
        fechaInicio.set(anio, 0, 1);
        desdeinicio = fechaInicio.getTime();
    }

    public String buscar() {

        Map parametros = new HashMap();
        String where = " o.impreso=true "
                + " and o.fecha between :desde and :hasta and o.numero!='0'";
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        parametros.put(";where", where);
        parametros.put(";orden", "o.fecha desc,o.numero desc");
        try {
            certificaciones = ejbCertificacionespoa.encontarParametros(parametros);
            for (Certificacionespoa c : certificaciones) {
                numeroFechaPac(c);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteValoresPoaPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double valorCertificado(Certificacionespoa c) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", c);
            parametros.put(";campo", "o.valor");
            return ejbDetalle.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double valorPac(Certificacionespoa c) {
        try {
            Calendar ca = Calendar.getInstance();
            ca.setTime(c.getFecha());
            int anio = ca.get(Calendar.YEAR);
            double valorTotal = 0.00;
            double valorReformas = 0.00;
            double valorAsignacion = 0.00;
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", c);
            List<Detallecertificacionespoa> listaDC = ejbDetalle.encontarParametros(parametros);
            if (c.getNumeroPAC()!= null) {
                for (Detallecertificacionespoa detalle : listaDC) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.asignacion.proyecto.anio=:anio "
                            + " and o.fecha between :desde and :hasta");
                    parametros.put("proyecto", detalle.getAsignacion().getProyecto());
                    parametros.put("anio", anio);
                    parametros.put("desde", desdeinicio);
                    parametros.put("hasta", c.getFecha());
                    parametros.put(";campo", "o.valor");
                    valorReformas += ejbReformaspoa.sumarCampo(parametros).doubleValue();
                    if (detalle.getAsignacion().getProyecto().getValoriva() == null) {
                        detalle.getAsignacion().getProyecto().setValoriva(BigDecimal.ZERO);
                    }
                    valorAsignacion = detalle.getAsignacion().getProyecto().getValoriva().doubleValue();

                    if (detalle.getAsignacion().getProyecto().getImpuesto() == null) {
                        detalle.getAsignacion().getProyecto().setImpuesto(BigDecimal.ZERO);
                    }
                    if (detalle.getAsignacion().getProyecto().getImpuesto().doubleValue() == 0) {
                        valorTotal = valorAsignacion + (valorReformas);
                    } else {
                        double imp = (detalle.getAsignacion().getProyecto().getImpuesto().doubleValue() / 100) + 1;
                        valorTotal = valorAsignacion + (valorReformas / imp);
                    }
                }
            }
            return valorTotal;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void numeroFechaPac(Certificacionespoa c) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", c);
            List<Detallecertificacionespoa> listaDC = ejbDetalle.encontarParametros(parametros);

            if (c.getNumeropac() == null) {
                traerCertificacion(listaDC, c);
            } else {
                c.setFechaPAC(c.getFechapac());
                c.setNumeroPAC(c.getNumeropac());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void traerCertificacion(List<Detallecertificacionespoa> listaDC, Certificacionespoa c) {
        try {
            for (Detallecertificacionespoa detalle : listaDC) {
                if (detalle.getAsignacion().getProyecto().getCpc() != null) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.certificacion.generapac=true "
                            + " and o.certificacion.fecha between :desde and :hasta");
                    parametros.put("proyecto", detalle.getAsignacion().getProyecto());
                    parametros.put("desde", desdeinicio);
                    parametros.put("hasta", c.getFecha());
                    List<Detallecertificacionespoa> listadc2 = ejbDetalle.encontarParametros(parametros);
                    if (!listadc2.isEmpty()) {
                        Certificacionespoa c2 = listadc2.get(0).getCertificacion();
//                        c.setFechapac(c2.getFechapac());
//                        c.setNumeropac(c2.getNumeropac());
                        c.setFechaPAC(c2.getFechapac());
                        c.setNumeroPAC(c2.getNumeropac());
                        return;
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteValoresPoaPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the certificaciones
     */
    public List<Certificacionespoa> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(List<Certificacionespoa> certificaciones) {
        this.certificaciones = certificaciones;
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
     * @return the desdeinicio
     */
    public Date getDesdeinicio() {
        return desdeinicio;
    }

    /**
     * @param desdeinicio the desdeinicio to set
     */
    public void setDesdeinicio(Date desdeinicio) {
        this.desdeinicio = desdeinicio;
    }

}
