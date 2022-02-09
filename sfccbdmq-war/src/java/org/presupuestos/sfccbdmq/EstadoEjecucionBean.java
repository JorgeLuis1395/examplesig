/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
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
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.IngresosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "estadoEjecucionSfccbdmq")
@ViewScoped
public class EstadoEjecucionBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public EstadoEjecucionBean() {
    }
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private String nivel;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private IngresosFacade ejbIngresos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private Date desde;
    private Date hasta;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
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

        llenar();

        return null;
    }

    public Clasificadores nombreClasificador(String codigo) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo =:codigo");
        parametros.put("codigo", codigo);
        List<Clasificadores> cl;
        try {
            cl = ejbClasificadores.encontarParametros(parametros);
            for (Clasificadores c : cl) {
                return c;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(EstadoEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void llenar() {
        List<Codigos> listaCodigos = codigosBean.traerCodigoOrdenadoCodigo("ESTEJE");
        asignaciones = new LinkedList<>();
        AuxiliarAsignacion totalTipo = new AuxiliarAsignacion();
        AuxiliarAsignacion totalTotal = new AuxiliarAsignacion();
        String grupo = "";
        for (Codigos codigo : listaCodigos) {
            String[] aux = codigo.getParametros().split("#");
            AuxiliarAsignacion parcial = new AuxiliarAsignacion();
            parcial.setNombre(codigo.getNombre());
            if (!grupo.isEmpty()) {
                if (!grupo.equals(codigo.getDescripcion())) {
                    totalTipo.setFuente("total");
                    asignaciones.add(totalTipo);
                    totalTipo = new AuxiliarAsignacion();
                    grupo = codigo.getDescripcion();
                }
            } else {
                grupo = codigo.getDescripcion();
            }
            parcial.setFuente("titulo");
            asignaciones.add(parcial);
            for (String aux1 : aux) {

                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(aux1);
                a.setFuente("");
                Clasificadores c = nombreClasificador(aux1);
                if (c != null) {
                    a.setNombre(c.getNombre());
                    // traer la signacion
                    double valorAsignaciones = calculosBean.traerAsignaciones(anio, null, aux1, null);
                    // reformas
                    double valorReformas = calculosBean.traerReformas(null, aux1, desde, hasta, null);
                    double valorEjecutado;
                    if (c.getIngreso()) {
                        valorEjecutado = calculosBean.traerDevengadoIngresos(null, aux1, desde, hasta, null);
                    } else {
                        valorEjecutado = calculosBean.traerDevengado(null, aux1, desde, hasta, null);
                    }
                    a.setCodificado(valorAsignaciones + valorReformas);
                    a.setEjecutado(valorEjecutado);
                    a.setValor((valorAsignaciones + valorReformas) - (valorEjecutado));
                    int signo = 1;
                    if (!c.getIngreso()) {
                        signo = -1;
                    }
                    parcial.setValor(parcial.getValor() + a.getValor() * signo);
                    parcial.setCodificado(parcial.getCodificado() + a.getCodificado() * signo);
                    parcial.setEjecutado(parcial.getEjecutado() + a.getEjecutado() * signo);
                    //                ver ceros
                    if (a.getCodificado() + a.getEjecutado() + a.getValor() != 0) {
                        totalTipo.setNombre(grupo);
                        totalTipo.setValor(totalTipo.getValor() + a.getValor() * signo);
                        totalTipo.setCodificado(totalTipo.getCodificado() + a.getCodificado() * signo);
                        totalTipo.setEjecutado(totalTipo.getEjecutado() + a.getEjecutado() * signo);
                        totalTotal.setValor(totalTotal.getValor() + a.getValor() * signo);
                        totalTotal.setCodificado(totalTotal.getCodificado() + a.getCodificado() * signo);
                        totalTotal.setEjecutado(totalTotal.getEjecutado() + a.getEjecutado() * signo);
                        asignaciones.add(a);
                    }
                }
            }

        }
        if (!grupo.isEmpty()) {
            totalTipo.setNombre(grupo);
            totalTipo.setFuente("total");
            asignaciones.add(totalTipo);
        }
        totalTotal.setNombre("SUPERAVIT O DEFICIT PRESUPUESTARIO");
        totalTotal.setFuente("total");
        SimpleDateFormat sdf = new SimpleDateFormat("dd  MMMM  yyyy");
        asignaciones.add(totalTotal);
        // hacer todo positivo
        for (AuxiliarAsignacion a : asignaciones) {
            if (!a.getFuente().equals("total")) {
                a.setCodificado(Math.abs(a.getCodificado()));
                a.setEjecutado(Math.abs(a.getEjecutado()));
            }
            a.setValor(a.getCodificado() - a.getEjecutado());
        }
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "");
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("filtro", "Estado de ejecución presupuestaria \n al " + sdf.format(hasta));
        Calendar cal = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Ejecucion.jasper"),
                asignaciones, "ejecucion" + String.valueOf(cal.getTimeInMillis()) + ".pdf");
    }

    private void llenarAnt() {
        List<Codigos> listaCodigos = codigosBean.traerCodigoOrdenadoCodigo("ESTEJE");
        asignaciones = new LinkedList<>();
        AuxiliarAsignacion totalTipo = new AuxiliarAsignacion();
        AuxiliarAsignacion totalTotal = new AuxiliarAsignacion();
        String grupo = "";
        for (Codigos codigo : listaCodigos) {
            String[] aux = codigo.getParametros().split("#");
            AuxiliarAsignacion parcial = new AuxiliarAsignacion();
            parcial.setNombre(codigo.getNombre());
            if (!grupo.isEmpty()) {
                if (!grupo.equals(codigo.getDescripcion())) {
                    totalTipo.setFuente("total");
                    asignaciones.add(totalTipo);
                    totalTipo = new AuxiliarAsignacion();
                    grupo = codigo.getDescripcion();
                }
            } else {
                grupo = codigo.getDescripcion();
            }
            parcial.setFuente("titulo");
            asignaciones.add(parcial);
            for (String aux1 : aux) {
//                if (!grupo.equals(codigo.getDescripcion())) {
//                    if (!grupo.isEmpty()) {
//                        asignaciones.add(totalTipo);
//                    }
//                    totalTipo = new AuxiliarAsignacion();
//                    grupo = codigo.getDescripcion();
//                }
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(aux1);
                a.setFuente("");
                Clasificadores c = nombreClasificador(aux1);
                if (c != null) {
                    a.setNombre(c.getNombre());
                    // traer la signacion
                    double valorAsignaciones = traerAsignaciones(aux1);
                    // reformas
                    double valorReformas = traerReformas(aux1);
                    double valorEjecutado = traerEjecutado(aux1);
                    a.setCodificado(valorAsignaciones + valorReformas);
                    a.setEjecutado(valorEjecutado);
                    a.setValor((valorAsignaciones + valorReformas) - (valorEjecutado));
                    int signo = 1;
                    if (!c.getIngreso()) {
                        signo = -1;
                    }
                    parcial.setValor(parcial.getValor() + a.getValor() * signo);
                    parcial.setCodificado(parcial.getCodificado() + a.getCodificado() * signo);
                    parcial.setEjecutado(parcial.getEjecutado() + a.getEjecutado() * signo);
                    //                ver ceros
                    if (a.getCodificado() + a.getEjecutado() + a.getValor() != 0) {
                        totalTipo.setNombre(grupo);
                        totalTipo.setValor(totalTipo.getValor() + a.getValor() * signo);
                        totalTipo.setCodificado(totalTipo.getCodificado() + a.getCodificado() * signo);
                        totalTipo.setEjecutado(totalTipo.getEjecutado() + a.getEjecutado() * signo);
                        totalTotal.setValor(totalTotal.getValor() + a.getValor() * signo);
                        totalTotal.setCodificado(totalTotal.getCodificado() + a.getCodificado() * signo);
                        totalTotal.setEjecutado(totalTotal.getEjecutado() + a.getEjecutado() * signo);
                        asignaciones.add(a);
                    }
                }
            }

        }
        if (!grupo.isEmpty()) {
            totalTipo.setFuente("total");
            asignaciones.add(totalTipo);
        }
        totalTotal.setNombre("SUPERAVIT O DEFICIT PRESUPUESTARIO");
        totalTotal.setFuente("total");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        asignaciones.add(totalTotal);
        // hacer todo positivo
        for (AuxiliarAsignacion a : asignaciones) {
            if (a.getFuente().isEmpty()) {
                a.setCodificado(Math.abs(a.getCodificado()));

//                a.setValor(Math.abs(a.getValor()));
            }
            a.setEjecutado(Math.abs(a.getEjecutado()));
        }
        Map parametros = new HashMap();
        parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
        parametros.put("expresado", "REPORTE DE EJECUCION");
        parametros.put("nombrelogo", "logo-new.png");
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("filtro", "reporte al " + sdf.format(hasta));
        Calendar cal = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Ejecucion.jasper"),
                asignaciones, "ejecucion" + String.valueOf(cal.getTimeInMillis()) + ".pdf");
    }

    private double traerEjecutado(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor+o.valorimpuesto");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.detallecompromiso.detallecertificacion.asignacion.clasificador.codigo  like :codigo"
                    + " and o.obligacion.estado=2 and o.pagado between :desde and :hasta");
            double valorEjecutado = ejbRasCompras.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("codigo", codigo + "%");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            parametros.put(";where", "o.asigancion.clasificador.codigo  like :codigo"
                    + " and o.kardexbanco is not null and o.fecha between :desde and :hasta");
            double valorEjecutadoIngresos = ejbIngresos.sumarCampo(parametros).doubleValue();
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
            parametros.put(";where", "o.clasificadorencargo like :codigo and o.concepto is null "
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
            return valorEjecutado + valorNomina + valorEncargo + valorSubrogacion + valorEjecutadoIngresos;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EstadoEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerAsignaciones(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("anio", anio);
            parametros.put("codigo", codigo + "%");
            parametros.put(";where", "o.asignacion.clasificador.codigo like :codigo "
                    + "and o.asignacion.proyecto.anio=:anio and o.cabecera.definitivo=true and o.fecha between :desde and :hasta");
            parametros.put("hasta", hasta);
            parametros.put("desde", desde);
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EstadoEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerReformas(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("anio", anio);
            parametros.put("codigo", codigo + "%");
            parametros.put(";where", "o.clasificador.codigo like :codigo and o.proyecto.anio=:anio");
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EstadoEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
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
        String nombreForma = "EstadoEjecucionVista";
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
//                    return ejbAsignaciones.sumarCampo(parametros).doubleValue();
//                } catch (ConsultarException ex) {
//                    Logger.getLogger(ReporteEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
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
//            Logger.getLogger(ReporteEjecucionBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return 0;
//    }
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
