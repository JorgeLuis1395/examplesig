/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

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
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteProductoSfccbdmq")
@ViewScoped
public class ReporteProductoBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteProductoBean() {
    }
    private int anio;
    private String codigo;
    private List<Asignaciones> asignaciones;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ProyectosFacade ejbProyectos;
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
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private String partida;
    private Date desde;
    private Date hasta;
    private Codigos fuenteFinanciamiento;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;

    /**
     * @return the asignaciones
     */
    public List<Asignaciones> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<Asignaciones> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String buscar() {
        totalEgresos = 0;
        totalIngresos = 0;
        llenarConfuente();

        return null;
    }

    private void llenarConfuente() {
        Map parametros = new HashMap();

        try {
            String where = "o.proyecto.anio=:anio and o.proyecto.ingreso=false";
            if (fuenteFinanciamiento != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuenteFinanciamiento);
            }
            if (proyectosBean.getProyectoSeleccionado() != null) {
                where += " and o.proyecto.codigo like :codigo";
                parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
            }
            if (!((partida == null) || (partida.isEmpty()))) {
                where += " and o.clasificador.codigo like :partida";
                parametros.put("partida", partida + "%");
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo");
            parametros.put("anio", anio);
            asignaciones = ejbAsignaciones.encontarParametros(parametros);
            Asignaciones aTotal = new Asignaciones();
            aTotal.setCodificado(0);
            aTotal.setCertificado(0);
            aTotal.setComprometido(0);
            aTotal.setDevengado(0);
            aTotal.setEjecutado(0);
            aTotal.setValor(BigDecimal.ZERO);
            for (Asignaciones a : asignaciones) {
                double valor = calculosBean.traerReformas(a.getProyecto().getCodigo(),
                        a.getClasificador().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(),
                        configuracionBean.getConfiguracion().getPfinalpresupuesto(), a.getFuente());
                a.setCodificado(a.getValor().doubleValue() + valor);
                valor = calculosBean.traerCertificaciones(a.getProyecto().getCodigo(),
                        a.getClasificador().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(),
                        configuracionBean.getConfiguracion().getPfinalpresupuesto(), a.getFuente());
                a.setCertificado(valor);
                valor = calculosBean.traerCompromisos(a.getProyecto().getCodigo(),
                        a.getClasificador().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(),
                        configuracionBean.getConfiguracion().getPfinalpresupuesto(), a.getFuente());
                a.setComprometido(valor);
                valor = calculosBean.traerDevengado(a.getProyecto().getCodigo(),
                        a.getClasificador().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(),
                        configuracionBean.getConfiguracion().getPfinalpresupuesto(), a.getFuente());
                a.setDevengado(valor);
                valor = calculosBean.traerEjecutado(a.getProyecto().getCodigo(),
                        a.getClasificador().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(),
                        configuracionBean.getConfiguracion().getPfinalpresupuesto(), a.getFuente());
                a.setEjecutado(valor);
//                if (a.getProyecto().getIngreso()) {
                    aTotal.setCodificado(aTotal.getCodificado() + a.getCodificado());
                    aTotal.setCertificado(aTotal.getCertificado() + a.getCertificado());
                    aTotal.setComprometido(aTotal.getComprometido() + a.getComprometido());
                    aTotal.setDevengado(aTotal.getDevengado() + a.getDevengado());
                    aTotal.setEjecutado(aTotal.getEjecutado() + a.getEjecutado());
                    aTotal.setValor(new BigDecimal(aTotal.getValor().doubleValue() + a.getValor().doubleValue()));
//                } else {
////                    aTotal.setCodificado(aTotal.getCodificado() - a.getCodificado());
////                    aTotal.setCertificado(aTotal.getCertificado() - a.getCertificado());
////                    aTotal.setComprometido(aTotal.getComprometido() - a.getComprometido());
////                    aTotal.setDevengado(aTotal.getDevengado() - a.getDevengado());
////                    aTotal.setEjecutado(aTotal.getEjecutado() - a.getEjecutado());
////                    aTotal.setValor(new BigDecimal(aTotal.getValor().doubleValue() - a.getValor().doubleValue()));
//                }
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
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public double getValor(Asignaciones a) {
//        Asignaciones a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        String where = "o.proyecto=:proyecto";
        if (fuenteFinanciamiento != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuenteFinanciamiento);
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and o.proyecto.codigo like :codigo";
            parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
        }
        if (!((partida == null) || (partida.isEmpty()))) {
            where += " and o.clasificador.codigo like :partida";
            parametros.put("partida", partida + "%");
        }
        parametros.put(";where", where);
//                parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto.codigo=:proyecto and o.anio=:anio");
        parametros.put("proyecto", a.getProyecto());
        parametros.put(";campo", "o.valor");
        try {
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public double getReformas(Asignaciones a) {
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
//            where += " and o.asignacion.clasificador.codigo like :partida";
//            parametros.put("partida", partida + "%");
//        }
        parametros.put(";where", where);
//                parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto.codigo=:proyecto and o.anio=:anio");

        parametros.put(";campo", "o.valor");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public double getReformasFuente(Asignaciones a) {
//        Asignaciones a = asignaciones.get(formulario.getFila().getRowIndex());

        Map parametros = new HashMap();
        parametros.put(";where", "o.asignacion=:asignacion");
//        parametros.put(";where", "o.asignacion.clasificador=:clasificador and o.asignacion.fuente=:fuente");
//        parametros.put(";where", "o.clasificador.codigo = :codigo and o.proyecto=:proyecto and o.anio=:anio");
        parametros.put("asignacion", a);
//        parametros.put("clasificador", a.getClasificador());
//        parametros.put("fuente", a.getFuente());
        parametros.put(";campo", "o.valor");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public double getCertificaciones(Asignaciones a) {
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
//                where += " and o.asignacion.clasificador.codigo like :partida";
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
            return ejbDetCert.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getCompromiso(Asignaciones a) {
        try {
            Map parametros = new HashMap();
            String where = "o.detallecertificacion.asignacion=:asignacion"
                    + " and o.compromiso.impresion is not null and o.fecha between :desde and :hasta";
            parametros.put("asignacion", a);
//            if (fuenteFinanciamiento != null) {
//                where += " and o.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//            if (proyectosBean.getProyectoSeleccionado() != null) {
//                where += " and o.detallecertificacion.asignacion.proyecto.codigo like :codigo";
//                parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
//            }
//            if (!((partida == null) || (partida.isEmpty()))) {
//                where += " and o.detallecertificacion.asignacion.clasificador.codigo like :partida";
//                parametros.put("partida", partida + "%");
//            }
            parametros.put(";where", where);
            parametros.put(";campo", "o.valor");
            parametros.put(";where", where);
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getDevengado(Asignaciones a) {
        try {
            if (a.getProyecto().getIngreso()) {
                // traer lo cobrado en ingresos
                Map parametros = new HashMap();
                String where = "o.asigancion=:asignacion"
                        + " and  o.contabilizar is not null and o.fecha between :desde and :hasta";
                parametros.put("asignacion", a);
                parametros.put(";where", where);
                parametros.put(";campo", "o.valor");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
//                        parametros.put("anio", anio);
                parametros.put("asignacion", a);
                parametros.put(";where", where);
                return ejbIngresos.sumarCampo(parametros).doubleValue();
            } else {
                // devengado
                Map parametros = new HashMap();
                String where = "o.detallecompromiso.detallecertificacion.asignacion=:asignacion"
                        + " and o.obligacion.estado=2 and o.obligacion.fechacontable between :desde and :hasta";
                parametros.put("asignacion", a);
                parametros.put(";campo", "o.valor+o.valorimpuesto");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", where);
                double valorDevengado = ejbRasCompras.sumarCampo(parametros).doubleValue();
                // nomina
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put("codigo", a.getClasificador().getCodigo());
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", "o.clasificador like :codigo"
                        + " and o.fechapago between :desde and :hasta and o.kardexbanco is  null");
                double valorNomina = ejbPagosEmpleados.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";campo", "o.encargo");
                parametros.put("codigo", a.getClasificador().getCodigo());
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", "o.clasificadorencargo like :codigo"
                        + " and o.fechapago between :desde and :hasta and o.kardexbanco is null");
                double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";campo", "o.cantidad");
                parametros.put("codigo", a.getClasificador().getCodigo());
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", "o.clasificadorencargo like :codigo"
                        + " and o.fechapago between :desde and :hasta and o.kardexbanco is null and o.concepto is null");
                double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);
                valorDevengado += valorEncargo + valorNomina + valorSubrogacion;
                return valorDevengado;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getEjecutado(Asignaciones a) {
        try {
            if (a.getProyecto().getIngreso()) {
                Map parametros = new HashMap();
                String where = "o.asigancion=:asignacion"
                        + " and  o.kardexbanco is not null and o.fecha between :desde and :hasta";
                parametros.put("asignacion", a);
//                if (fuenteFinanciamiento != null) {
//                    where += " and o.asignacion.fuente=:fuente";
//                    parametros.put("fuente", fuenteFinanciamiento);
//                }
//                if (proyectosBean.getProyectoSeleccionado() != null) {
//                    where += " and o.asignacion.proyecto.codigo like :codigo";
//                    parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
//                }
//                if (!((partida == null) || (partida.isEmpty()))) {
//                    where += " and o.asignacion.clasificador.codigo like :partida";
//                    parametros.put("partida", partida + "%");
//                }
                parametros.put(";where", where);
                parametros.put(";campo", "o.valor");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
//                        parametros.put("anio", anio);
                parametros.put("asignacion", a);
                parametros.put(";where", where);
                return ejbIngresos.sumarCampo(parametros).doubleValue();
            } else {
                Map parametros = new HashMap();
                String where = "o.detallecompromiso.detallecertificacion.asignacion=:asignacion"
                        + " and o.obligacion.estado=2 and o.pagado between :desde and :hasta";
                parametros.put("asignacion", a);
//            if (fuenteFinanciamiento != null) {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.fuente=:fuente";
//                parametros.put("fuente", fuenteFinanciamiento);
//            }
//            if (proyectosBean.getProyectoSeleccionado() != null) {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.proyecto.codigo like :codigo";
//                parametros.put("codigo", proyectosBean.getProyectoSeleccionado().getCodigo() + "%");
//            }
//            if (!((partida == null) || (partida.isEmpty()))) {
//                where += " and o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo like :partida";
//                parametros.put("partida", partida + "%");
//            }
                parametros.put(";campo", "o.valor+o.valorimpuesto");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", where);
                double valorEjecutado = ejbRasCompras.sumarCampo(parametros).doubleValue();
                // nomina
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put("codigo", a.getClasificador().getCodigo() + "%");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", "o.clasificador like :codigo"
                        + " and o.fechapago between :desde and :hasta and o.kardexbanco is  not null");
                double valorNomina = ejbPagosEmpleados.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";campo", "o.encargo");
                parametros.put("codigo", a.getClasificador().getCodigo());
//            parametros.put("codigo", a.getClasificador().getCodigo() + "%");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", "o.clasificadorencargo like :codigo"
                        + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null");
                double valorEncargo = ejbPagosEmpleados.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";campo", "o.cantidad");
                parametros.put("codigo", a.getClasificador().getCodigo());
//            parametros.put("codigo", a.getClasificador().getCodigo() + "%");
                parametros.put("hasta", hasta);
                parametros.put("desde", desde);
                parametros.put(";where", "o.clasificadorencargo like :codigo"
                        + " and o.fechapago between :desde and :hasta and o.kardexbanco is not null and o.concepto is null");
                double valorSubrogacion = ejbPagosEmpleados.sumarCampoDoble(parametros);
                valorEjecutado += valorEncargo + valorNomina + valorSubrogacion;
                return valorEjecutado;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteProductoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

    public double totalProducto(Proyectos p) {
        if (p == null) {
            return 0;
        } else {
            double asignacion = calculosBean.traerAsignaciones(p.getAnio(), p.getCodigo(), null, null);
            double reformas = calculosBean.traerReformas(p.getCodigo(), null, configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), null);
            return asignacion + reformas;
        }
    }

    public double totalProductoClasificador(Proyectos p, String clasificador) {
        if (p == null) {
            return 0;
        } else {
            double asignacion = calculosBean.traerAsignaciones(p.getAnio(), p.getCodigo(), clasificador, null);
            double reformas = calculosBean.traerReformas(p.getCodigo(), clasificador, configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), null);
            return asignacion + reformas;
        }
    }
}
