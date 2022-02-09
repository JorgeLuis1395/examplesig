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
import org.entidades.sfccbdmq.Proyectospoa;
import org.errores.sfccbdmq.ConsultarException;
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
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author sigi-iepi
 */
@ManagedBean(name = "reporteProductoPoa")
@ViewScoped
public class ReporteProductoPoaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteProductoPoaBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPoa;

    private int anio;
    private String codigo;
    private List<Asignacionespoa> asignaciones;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();

    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private String partida;
    private Date desde;
    private Date hasta;
    private Codigos fuenteFinanciamiento;

    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private DetallecertificacionespoaFacade ejbDetCertPoa;
    @EJB
    private EjecucionpoaFacade ejbEjecucionpoa;

    public String buscar() {
        totalEgresos = 0;
        totalIngresos = 0;
        llenarConfuente();

        return null;
    }

    private void llenarConfuente() {
        Map parametros = new HashMap();

        try {
            String where = "o.proyecto.anio=:anio";
            if (fuenteFinanciamiento != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuenteFinanciamiento.getCodigo());
            }
            if (proyectosPoaBean.getProyectoSeleccionado() != null) {
                where += " and o.proyecto.codigo like :codigo";
                parametros.put("codigo", proyectosPoaBean.getProyectoSeleccionado().getCodigo() + "%");
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and o.partida.codigo like :partida";
                parametros.put("partida", partida + "%");
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.proyecto.codigo,o.partida.codigo");
            parametros.put("anio", anio);
            asignaciones = ejbAsignacionespoa.encontarParametros(parametros);
            Asignacionespoa aTotal = new Asignacionespoa();
            aTotal.setCodificado(0);
            aTotal.setCertificado(0);
            aTotal.setComprometido(0);
            aTotal.setDevengado(0);
            aTotal.setEjecutado(0);
            aTotal.setValor(BigDecimal.ZERO);
            for (Asignacionespoa a : asignaciones) {
                double valor = calculosPoa.traerReformas(a.getProyecto().getCodigo(),
                        a.getPartida().getCodigo(), desde, hasta, a.getFuente());
                a.setCodificado(a.getValor().doubleValue() + valor);
                valor = calculosPoa.traerCertificaciones(a.getProyecto().getCodigo(),
                        a.getPartida().getCodigo(), desde, hasta, a.getFuente());
                a.setCertificado(valor);
                valor = calculosPoa.traerCompromisos(a.getProyecto().getCodigo(),
                        a.getPartida().getCodigo(), desde, hasta, a.getFuente());
                a.setComprometido(valor);
                valor = calculosPoa.traerEjecutado(a.getProyecto().getCodigo(),
                        a.getPartida().getCodigo(), desde, hasta, a.getFuente());
                a.setEjecutado(valor);
//                valor = calculosPoa.traerDevengado(a.getProyecto(), a.getPartida().getCodigo(), desde, hasta, a.getFuente());
                a.setDevengado(valor);
                aTotal.setCodificado(aTotal.getCodificado() + a.getCodificado());
                aTotal.setCertificado(aTotal.getCertificado() + a.getCertificado());
                aTotal.setComprometido(aTotal.getComprometido() + a.getComprometido());
                aTotal.setDevengado(aTotal.getDevengado() + a.getDevengado());
                aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
                aTotal.setValor(new BigDecimal(aTotal.getValor().doubleValue() + a.getValor().doubleValue()));
                if (a.getCodificado() != 0) {
                    a.setPcertificado(a.getCertificado() / a.getCodificado() * 100);
                    a.setPcomprometido(a.getComprometido() / a.getCodificado() * 100);
                    a.setPdevengado(a.getDevengado() / a.getCodificado() * 100);
                    a.setPejecutado(a.getEjecutado() / a.getCodificado() * 100);
                }

            }
            aTotal.setId(-1);
            asignaciones.add(aTotal);
//            List<Proyectos> pl = ejbProyectos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigentepresupuesto());
        anio = c.get(Calendar.YEAR);
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteProductoVista";
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
        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
                seguridadbean.cerraSession();
            }
        }
    }

    public double getValor(Asignacionespoa a) {
//        Asignaciones a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        String where = "o.proyecto=:proyecto";
        if (fuenteFinanciamiento != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuenteFinanciamiento.getCodigo());
        }
        if (proyectosPoaBean.getProyectoSeleccionado() != null) {
            where += " and o.proyecto.codigo like :codigo";
            parametros.put("codigo", proyectosPoaBean.getProyectoSeleccionado().getCodigo() + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.partida.codigo like :partida";
            parametros.put("partida", partida + "%");
        }
        parametros.put(";where", where);
//                parametros.put(";where", "o.partida.codigo = :codigo and o.proyecto.codigo=:proyecto and o.anio=:anio");
        parametros.put("proyecto", a.getProyecto());
        parametros.put(";campo", "o.valor");
        try {
            return ejbAsignacionespoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public double getReformas(Asignacionespoa a) {
//        Asignaciones a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        String where = "o.asignacion=:asignacion";
        parametros.put("asignacion", a);
//        if (fuenteFinanciamiento != null) {
//            where += " and o.asignacion.fuente=:fuente";
//            parametros.put("fuente", fuenteFinanciamiento);
//        }
//        if (proyectosBean.getProyectoSeleccionado() != null) {
//            where += " and o.asignacion.proyecto.codigo like :codigo";
//            parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
//        }
//        if (!((partida == null) || (partida.isEmpty()))) {
//            where += " and o.asignacion.partida.codigo like :partida";
//            parametros.put("partida", partida + "%");
//        }
        parametros.put(";where", where);
//                parametros.put(";where", "o.partida.codigo = :codigo and o.proyecto.codigo=:proyecto and o.anio=:anio");

        parametros.put(";campo", "o.valor");
        try {
            return ejbReformaspoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public double getReformasFuente(Asignacionespoa a) {
//        Asignaciones a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        parametros.put(";where", "o.asignacion.partida=:partida and o.asignacion.fuente=:fuente");
//                parametros.put(";where", "o.partida.codigo = :codigo and o.proyecto.codigo=:proyecto and o.anio=:anio");
        parametros.put("partida", a.getPartida());
        parametros.put("fuente", a.getFuente());
        parametros.put(";campo", "o.valor");
        try {
            return ejbReformaspoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public void cambiaFuente(ValueChangeEvent event) {
        // cambia el texto de la cedula
        fuente = (boolean) event.getNewValue();
        buscar();
    }

    public double getCertificaciones(Asignacionespoa a) {
        try {
            Map parametros = new HashMap();
            String where = "o.asignacion=:asignacion and o.certificacion.fecha between :desde and :hasta "
                    + " and o.certificacion.impreso=true";
            parametros.put("asignacion", a);
//            if (fuenteFinanciamiento != null) {
//                where += " and o.asignacion.fuente=:fuente";
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//            if (proyectosBean.getProyectoSeleccionado() != null) {
//                where += " and o.asignacion.proyecto.codigo like :codigo";
//                parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
//            }
//            if (!((partida == null) || (partida.isEmpty()))) {
//                where += " and o.asignacion.partida.codigo like :partida";
//                parametros.put("partida", partida + "%");
//            }
            parametros.put(";where", where);
            parametros.put(";campo", "o.valor");
//                        parametros.put("anio", anio);
            parametros.put("asignacion", a);
//            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.fecha between :desde and :hasta "
//                    + " and o.certificacion.impreso=true");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            return ejbDetCertPoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getCompromiso(Asignacionespoa a) {
        try {
            Map parametros = new HashMap();
            String where = "o.detallecertificacion.asignacion=:asignacion"
                    + " and o.compromiso.impresion is not null and o.fecha between :desde and :hasta";
            parametros.put("asignacion", a);
            parametros.put(";where", where);
            parametros.put(";campo", "o.comprometido");
            parametros.put(";where", where);
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            return ejbEjecucionpoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getEjecutado(Asignacionespoa a) {
        try {

            Map parametros = new HashMap();
            String where = "o.asigancion=:asignacion"
                    + " and o.fecha between :desde and :hasta";
            parametros.put("asignacion", a);
            parametros.put(";where", where);
            parametros.put(";campo", "o.valor");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put("asignacion", a);
            parametros.put(";where", where);
            return ejbEjecucionpoa.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double totalProducto(Proyectospoa p) {
        if (p == null) {
            return 0;
        } else {
            double asignacion = calculosPoa.traerAsignaciones(p.getAnio(), p.getCodigo(), null, null);
            double reformas = calculosPoa.traerReformas(p.getCodigo(), null, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), null);
            return asignacion + reformas;
        }
    }

    public double totalProductoClasificador(Proyectospoa p, String partida) {
        if (p == null) {
            return 0;
        } else {
            double asignacion = calculosPoa.traerAsignaciones(p.getAnio(), p.getCodigo(), partida, null);
            double reformas = calculosPoa.traerReformas(p.getCodigo(), partida, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), null);
            return asignacion + reformas;
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
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosPoaBean() {
        return proyectosPoaBean;
    }

    /**
     * @param proyectosPoaBean the proyectosPoaBean to set
     */
    public void setProyectosPoaBean(ProyectosPoaBean proyectosPoaBean) {
        this.proyectosPoaBean = proyectosPoaBean;
    }

    /**
     * @return the calculosPoa
     */
    public CalculosPoaBean getCalculosPoa() {
        return calculosPoa;
    }

    /**
     * @param calculosPoa the calculosPoa to set
     */
    public void setCalculosPoa(CalculosPoaBean calculosPoa) {
        this.calculosPoa = calculosPoa;
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the asignaciones
     */
    public List<Asignacionespoa> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<Asignacionespoa> asignaciones) {
        this.asignaciones = asignaciones;
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

}
