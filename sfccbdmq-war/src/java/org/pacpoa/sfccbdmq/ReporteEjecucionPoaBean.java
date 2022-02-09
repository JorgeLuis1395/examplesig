/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Partidaspoa;
import org.errores.sfccbdmq.ConsultarException;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author sigi-iepi
 */
@ManagedBean(name = "reporteEjecucionPoa")
@ViewScoped
public class ReporteEjecucionPoaBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteEjecucionPoaBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPoa;
    private int anio;
    private List<AuxiliarAsignacion> asignaciones;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();
    private String nivel;
    
    
    private double totalIngresos;
    private double totalEgresos;
    private boolean fuente;
    private Date desde;
    private Date hasta;
    private Resource reporte;
    
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    public String buscar() {

        llenar();

        return null;
    }

    private void llenar() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo");
        if (!((nivel == null) || (nivel.isEmpty()))) {
            parametros.put(";where", "o.codigo like :codigo");
            parametros.put("codigo", nivel);
        }
//        parametros.put(";where", "o.imputable=true and o.activo=true");
        totalEgresos = 0;
        totalIngresos = 0;
        Calendar ca = Calendar.getInstance();
        Calendar diaAntesDeses = Calendar.getInstance();
        ca.setTime(desde);
        diaAntesDeses.setTime(desde);
        diaAntesDeses.add(Calendar.DATE, -1);
        anio = ca.get(Calendar.YEAR);
        try {
            asignaciones = new LinkedList<>();
            List<Partidaspoa> cl = ejbPartidaspoa.encontarParametros(parametros);
            for (Partidaspoa c : cl) {
                AuxiliarAsignacion a = new AuxiliarAsignacion();
                a.setCodigo(c.getCodigo());
                a.setNombre(c.getNombre());
                // traer la signacion
                double valorAsignaciones = traerAsignaciones(c.getCodigo());
                // reformas anteriores
                double valorReformasAnteriores = traerReformas(c.getCodigo(),
                        configuracionBean.getConfiguracion().getPinicial()
                        ,diaAntesDeses.getTime());
                // reformas
                double valorReformas = traerReformas(c.getCodigo(),desde,hasta);
                double valorEjecutado = calculosPoa.traerEjecutado(null,c.getCodigo(),desde ,hasta , null);
                a.setCodificado(valorAsignaciones + valorReformas+valorReformasAnteriores);
                a.setEjecutado(valorEjecutado);
                a.setValor((valorAsignaciones + valorReformas) - (valorEjecutado));
//                ver ceros
                if (a.getCodificado() + a.getEjecutado() + a.getValor() != 0) {
                    asignaciones.add(a);
                }
            }
            // ver los totales parce más rápido
            parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.codigo like '_'");
            cl = ejbPartidaspoa.encontarParametros(parametros);
            AuxiliarAsignacion ingresos = new AuxiliarAsignacion();
            ingresos.setCodigo("");
            ingresos.setNombre("TOTAL INGRESOS");
            ingresos.setCodificado(0);
            ingresos.setEjecutado(0);
            ingresos.setValor(0);
            AuxiliarAsignacion egresos = new AuxiliarAsignacion();
            egresos.setCodigo("");
            egresos.setNombre("TOTAL EGRESOS");
            egresos.setCodificado(0);
            egresos.setEjecutado(0);
            egresos.setValor(0);
            AuxiliarAsignacion superabit = new AuxiliarAsignacion();
            superabit.setCodigo("");
            superabit.setNombre("SUPERAVIT O DEFICIT PRESUPUESTARIO");
            superabit.setCodificado(0);
            superabit.setEjecutado(0);
            superabit.setValor(0);
            AuxiliarAsignacion superabitCorriente = new AuxiliarAsignacion();
            superabitCorriente.setCodigo("");
            superabitCorriente.setNombre("SUPERAVIT O DEFICIT CORRIENTE");
            superabitCorriente.setCodificado(0);
            superabitCorriente.setEjecutado(0);
            superabitCorriente.setValor(0);
            AuxiliarAsignacion superabitInversion = new AuxiliarAsignacion();
            superabitInversion.setCodigo("");
            superabitInversion.setNombre("SUPERAVIT O DEFICIT DE INVERSION");
            superabitInversion.setCodificado(0);
            superabitInversion.setEjecutado(0);
            superabitInversion.setValor(0);
            AuxiliarAsignacion superabitFinanciamiento = new AuxiliarAsignacion();
            superabitFinanciamiento.setCodigo("");
            superabitFinanciamiento.setNombre("SUPERAVIT O DEFICIT DE FINANCIAMIENTO");
            superabitFinanciamiento.setCodificado(0);
            superabitFinanciamiento.setEjecutado(0);
            superabitFinanciamiento.setValor(0);
            for (Partidaspoa c : cl) {
                // traer la signacion
                double valorAsignaciones = traerAsignaciones(c.getCodigo());
                double valorReformasAnteriores = traerReformas(c.getCodigo(),configuracionBean.getConfiguracion().getPinicial(),diaAntesDeses.getTime());
                valorAsignaciones+=valorReformasAnteriores;
                // reformas
                double valorReformas = traerReformas(c.getCodigo(),desde,hasta);
                double valorEjecutado = calculosPoa.traerEjecutado(null,c.getCodigo(),desde ,hasta , null);
                
            }
            superabit.setCodificado(ingresos.getCodificado() - egresos.getCodificado());
            superabit.setEjecutado(ingresos.getEjecutado() - egresos.getEjecutado());
            superabit.setValor(ingresos.getValor() - egresos.getValor());
            asignaciones.add(superabitCorriente);
            asignaciones.add(superabitInversion);
            asignaciones.add(superabitFinanciamiento);
            asignaciones.add(ingresos);
            asignaciones.add(egresos);
            asignaciones.add(superabit);
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE EJECUCION");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("filtro", "reporte al "+sdf.format(hasta));
            Calendar cal = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Ejecucion.jasper"),
                    asignaciones, "ejecucion" + String.valueOf(cal.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteEjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private double traerReformas(String codigo,Date pdesde,Date phasta) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("anio", anio);
            parametros.put("codigo", codigo + "%");
            parametros.put(";where", "o.asignacion.partida.codigo like :codigo "
                    + "and o.asignacion.proyecto.anio=:anio and o.cabecera.definitivo=true and o.fecha between :desde and :hasta");
            parametros.put("hasta", phasta);
            parametros.put("desde", pdesde);
            return ejbReformaspoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteEjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double traerAsignaciones(String codigo) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("anio", anio);
            parametros.put("codigo", codigo + "%");
            parametros.put(";where", "o.partida.codigo like :codigo and o.proyecto.anio=:anio");
            return ejbAsignacionespoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteEjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(getConfiguracionBean().getConfiguracion().getPvigentepresupuesto());
        anio = c.get(Calendar.YEAR);
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteEjecucionVista";
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

//    public double getValor() {
//        AuxiliarAsignacion a = asignaciones.get(formulario.getFila().getRowIndex());
//        switch (a.getTipo()) {
//            case "CUEN":
//                return a.getValor();
//            // sumar
//            case "FUEN":
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.partida.codigo = :codigo and o.proyecto.codigo=:proyecto ");
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
   
}
