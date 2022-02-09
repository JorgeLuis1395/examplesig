/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.utilitarios.sfccbdmq.AuxiliarCodigos;
import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.errores.sfccbdmq.ConsultarException;

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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.entidades.sfccbdmq.Perfiles;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCedulaPoa")
@ViewScoped
public class ReporteCedulaPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPoa;
    private int anio;
    private List<AuxiliarProducto> asignaciones;
    private List<Auxiliar> proyectos;
    private Perfiles perfil;
    private Formulario formulario = new Formulario();

    private double totalIngresos;
    private double totalEgresos;
    private Date desde;
    private Date desdeInicio;
    private Date hasta;
    private String codigoProducto;
    private String direccion;
    private String presupuesto;
    private String fuente;
    private AuxiliarCodigos fuenteFinanciamiento;
    private LazyDataModel<Asignacionespoa> listaAsignaciones;

    private Date vigente;

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbDetCert;

    public ReporteCedulaPoaBean() {
        listaAsignaciones = new LazyDataModel<Asignacionespoa>() {

            @Override
            public List<Asignacionespoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @PostConstruct
    private void activar() {

        vigente = getConfiguracionBean().getConfiguracion().getPvigentepresupuesto();
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        desdeInicio = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);
    }

    public String buscar() {
        llenarLista();
        return null;
    }

    private void llenarLista() {
        totalEgresos = 0;
        totalIngresos = 0;
        try {
            Calendar ca = Calendar.getInstance();
            ca.setTime(desde);
            anio = ca.get(Calendar.YEAR);
            proyectos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            String where = "o.anio=:anio and o.activo=true";
            if (proyectosPoaBean.getProyectoSeleccionado() != null) {
                where += " and upper(o.codigo) = :codigo ";
                parametros.put("codigo", proyectosPoaBean.getProyectoSeleccionado().getCodigo().toUpperCase());
            }

            parametros.put("anio", anio);
            parametros.put(";where", where);
            List<Proyectospoa> pl = ejbProyectos.encontarParametros(parametros);

            if (partidasPoaBean.getPartidaPoa() != null) {
                proyectosPoaBean.setProyectoSeleccionado(null);
                proyectosPoaBean.setCodigo(null);
                pl = new LinkedList<>();
                parametros = new HashMap();
                where = "o.proyecto.anio=:anio and o.proyecto.activo=true and o.partida=:partida";
                parametros.put("anio", anio);
                parametros.put("partida", partidasPoaBean.getPartidaPoa());
                if (fuente != null) {
                    where += " and o.fuente=:fuente";
                    parametros.put("fuente", fuente);
                }

                parametros.put(";where", where);
                List<Asignacionespoa> lista = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : lista) {
                    pl.add(a.getProyecto());
                }
            }

            for (Proyectospoa p : pl) {
                Auxiliar a = new Auxiliar();
                a.setEstacion(p.getCodigo());
                a.setValor(p.getNombre());
                if (p.getIngreso()) {
                    a.setTitulo1("Recaudado (f)");
                    a.setTitulo2("Saldo por Recaudar (a-f)");
                } else {
                    a.setTitulo1("Devengado (f) ");
                    a.setTitulo2("Saldo por Devengar (a-f) ");
                }
                proyectos.add(a);
                if (p.getImputable()) {
                    // buscar las cuentas
                    parametros = new HashMap();
                    parametros.put(";orden", "o.partida.codigo");
                    where = "";
                    if (partidasPoaBean.getPartidaPoa() == null) {
                        where = "o.proyecto=:proyecto";
                    } else {
                        where = "o.proyecto=:proyecto and o.partida.codigo like :codigo";
                        parametros.put("codigo", partidasPoaBean.getPartidaPoa().getCodigo() + "%");
                    }
                    if (direccion != null) {
                        where += " and o.proyecto.direccion=:direccion";
                        parametros.put("direccion", direccion);
                    }
                    parametros.put(";where", where);
                    parametros.put("proyecto", p);
                    List<Asignacionespoa> asl = ejbAsignaciones.encontarParametros(parametros);
                    asignaciones = new LinkedList<>();
                    AuxiliarProducto aTotal = new AuxiliarProducto();
                    aTotal.setCodigo(p.getCodigo());
                    aTotal.setNombre("*****TOTAL " + p.getNombre() + "*****");
                    for (Asignacionespoa as : asl) {
                        AuxiliarProducto aCodigo = new AuxiliarProducto();
                        aCodigo.setCodigo(as.getPartida().getCodigo());
                        aCodigo.setNombre(as.getPartida().toString());
                        aCodigo.setTipo("CUENTA");
                        aCodigo.setFuente(as.getFuente());
                        aCodigo.setProyecto(p.getCodigo() + " - " + p.getNombre());
                        double valorAsignacion = as.getValor().doubleValue();
                        aCodigo.setAsignacion(valorAsignacion);

                        //nuevos valores
                        aCodigo.setDetalle(as.getProyecto().getDetalle());
                        aCodigo.setTipocompra(as.getProyecto().getTipocompra());
                        aCodigo.setCantidad(as.getProyecto().getCantidad());
                        aCodigo.setUnidad(as.getProyecto().getUnidad());
                        aCodigo.setTipoproducto(as.getProyecto().getTipoproducto());
                        aCodigo.setProcedimientocontratacion(as.getProyecto().getProcedimientocontratacion());
                        aCodigo.setRegimen(as.getProyecto().getRegimen());
                        aCodigo.setPresupuesto(as.getProyecto().getPresupuesto());

                        // traer reformas
                        parametros = new HashMap();
                        parametros.put(";campo", "o.valor");
                        parametros.put("codigo", as);
                        parametros.put(";where", "o.asignacion=:codigo "
                                + "and o.cabecera.definitivo=true and o.fecha between :desde and :hasta");
                        parametros.put("hasta", hasta);
                        parametros.put("desde", desdeInicio);
                        double valorReformas = ejbReformas.sumarCampo(parametros).doubleValue();
                        
                        aCodigo.setReforma(valorReformas);
                        aCodigo.setCodificado(valorAsignacion + valorReformas);
                        // certificaciones
                        aCodigo.setCertificacion(calculosPoa.traerCertificaciones(as.getProyecto().getCodigo(), as.getPartida().getCodigo(), desdeInicio, hasta, as.getFuente()));
                        
                        aCodigo.setCompromiso(calculosPoa.traerCompromisos(p.getCodigo(), as.getPartida().getCodigo(), desdeInicio, hasta, as.getFuente()));
                        aCodigo.setCertificacionr(aCodigo.getCertificacion() - aCodigo.getCompromiso());
                        aCodigo.setCertificado(aCodigo.getCodificado() - aCodigo.getCertificacionr() - aCodigo.getCompromiso());

                        aCodigo.setComprometido(aCodigo.getCodificado() - aCodigo.getCompromiso());

//                        aCodigo.setDevengado(calculosPoa.traerDevengado(p, as.getPartida().getCodigo(), desde, hasta, as.getFuente()));
//                        aCodigo.setSaldoDevengado(aCodigo.getCodificado() - aCodigo.getDevengado());
                        aCodigo.setEjecutado(calculosPoa.traerEjecutado(p.getCodigo(), as.getPartida().getCodigo(), desde, hasta, as.getFuente()));
                        aCodigo.setSaldoEjecutado(aCodigo.getCodificado() - aCodigo.getEjecutado());

                        asignaciones.add(aCodigo);
                        aTotal.setAsignacion(aTotal.getAsignacion() + aCodigo.getAsignacion());
                        aTotal.setReforma(aTotal.getReforma() + aCodigo.getReforma());
                        aTotal.setCodificado(aTotal.getCodificado() + aCodigo.getCodificado());
                        aTotal.setCertificacion(aTotal.getCertificacion() + aCodigo.getCertificacion());
                        aTotal.setCertificado(aTotal.getCertificado() + aCodigo.getCertificado());
                        aTotal.setComprometido(aTotal.getComprometido() + aCodigo.getComprometido());
                        aTotal.setCompromiso(aTotal.getCompromiso() + aCodigo.getCompromiso());
                        aTotal.setEjecutado(aTotal.getEjecutado() + aCodigo.getEjecutado());
                        aTotal.setSaldoEjecutado(aTotal.getSaldoEjecutado() + aCodigo.getSaldoEjecutado());
//                        aTotal.setDevengado(aTotal.getDevengado() + aCodigo.getDevengado());
//                        aTotal.setSaldoDevengado(aTotal.getSaldoDevengado() + aCodigo.getSaldoDevengado());

                        aTotal.setValor(aTotal.getValor() + aCodigo.getValor());
                    }

                    asignaciones.add(aTotal);
                    a.setIndice(asignaciones.size());
                    a.setAsignaciones(asignaciones);
////                    asignaciones.add(a);
//
////                } else {
////                    proyectos.add(a);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCedulaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getValor() {
        AuxiliarProducto a = asignaciones.get(formulario.getFila().getRowIndex());
        switch (a.getTipo()) {
            case "CUEN":
                return a.getValor();
            // sumar
            case "FUEN":
                Map parametros = new HashMap();
                parametros.put(";where", "o.partida.codigo = :codigo and o.proyecto.codigo=:proyecto ");
                parametros.put("codigo", a.getCodigo());
                parametros.put("proyecto", a.getProyecto());
                parametros.put(";campo", "o.valor");
                try {
                    return ejbAsignaciones.sumarCampo(parametros).doubleValue();
                } catch (ConsultarException ex) {
                    Logger.getLogger(ReporteCedulaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.codigo like :codigo ");
        parametros.put("codigo", a.getCodigo() + "%");
        parametros.put(";campo", "o.valor");
        try {
            return ejbAsignaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

//    public void cambiaFuente(ValueChangeEvent event) {
//        // cambia el texto de la cedula
//        fuente = (boolean) event.getNewValue();
//        buscar();
//    }
    public Integer getCuentasContables() {
        AuxiliarProducto a = asignaciones.get(formulario.getFila().getRowIndex());

//        Map parametros = new HashMap();
//        String where = " upper(o.presupuesto) like :presupuesto";
//        parametros.put("presupuesto", a.getCodigo().toUpperCase() + "%");
////        parametros.put("codigo", a.getCodigo() + "%");
////        parametros.put(";campo", "o.valor");
//        parametros.put(";where", where);
//        try {
//            return ejbCuentas.contar(parametros);
//        } catch (ConsultarException ex) {
//            Logger.getLogger(ReporteCedulaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return 0;
    }

    public String buscarAsignaciones() {
        listaAsignaciones = new LazyDataModel<Asignacionespoa>() {
            @Override
            public List<Asignacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                try {
                    Map parametros = new HashMap();
                    if (scs.length == 0) {
                        parametros.put(";orden", "o.proyecto.codigo");
                    } else {
                        parametros.put(";orden", "o." + scs[0].getPropertyName()
                                + (scs[0].isAscending() ? " ASC" : " DESC"));
                    }
                    String where = "o.proyecto.anio=:anio";

                    for (Map.Entry e : map.entrySet()) {
                        String clave = (String) e.getKey();
                        if (clave != null) {
                            String valor = (String) e.getValue();

                            where += " and upper(o." + clave + ") like :" + clave;
                            parametros.put(clave, valor.toUpperCase() + "%");
                        }

                    }

                    if (getDireccion() != null) {
                        where += " and o.proyecto.direccion=:direccion";
                        parametros.put("direccion", getDireccion());
                    }
                    if (getProyectosPoaBean().getProyectoSeleccionado() != null) {
                        where += " and upper(o.proyecto.codigo) =:codigo ";
                        parametros.put("codigo", getProyectosPoaBean().getProyectoSeleccionado().getCodigo());
                    }

                    parametros.put("anio", getAnio());
                    int total = 0;
                    parametros.put(";where", where);
                    total = ejbAsignaciones.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }

                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListaAsignaciones().setRowCount(total);

                    return ejbAsignaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger(ReporteCedulaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public double traerCodificado(Asignacionespoa asignacion) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("codigo", asignacion);
        parametros.put(";where", "o.asignacion=:codigo "
                + "and o.cabecera.definitivo=true and o.fecha between :desde and :hasta");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        try {
            return asignacion.getValor().doubleValue() + ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double traerReforma(Asignacionespoa asignacion) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("codigo", asignacion);
        parametros.put(";where", "o.asignacion=:codigo "
                + "and o.cabecera.definitivo=true and o.fecha between :desde and :hasta");
        parametros.put("hasta", hasta);
        parametros.put("desde", desde);
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedulaPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Double traerPorcentajeEjecucion(Double ejecutado, Double codificado) {
        if (ejecutado > 0.00 && codificado > 0) {
            return ejecutado / codificado;
        }
        return 0.00;
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
    public List<AuxiliarProducto> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<AuxiliarProducto> asignaciones) {
        this.asignaciones = asignaciones;
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
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
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
     * @return the listaAsignaciones
     */
    public LazyDataModel<Asignacionespoa> getListaAsignaciones() {
        return listaAsignaciones;
    }

    /**
     * @param listaAsignaciones the listaAsignaciones to set
     */
    public void setListaAsignaciones(LazyDataModel<Asignacionespoa> listaAsignaciones) {
        this.listaAsignaciones = listaAsignaciones;
    }

    /**
     * @return the presupuesto
     */
    public String getPresupuesto() {
        return presupuesto;
    }

    /**
     * @param presupuesto the presupuesto to set
     */
    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    /**
     * @return the codigoProducto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * @param codigoProducto the codigoProducto to set
     */
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * @return the fuenteFinanciamiento
     */
    public AuxiliarCodigos getFuenteFinanciamiento() {
        return fuenteFinanciamiento;
    }

    /**
     * @param fuenteFinanciamiento the fuenteFinanciamiento to set
     */
    public void setFuenteFinanciamiento(AuxiliarCodigos fuenteFinanciamiento) {
        this.fuenteFinanciamiento = fuenteFinanciamiento;
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

    /**
     * @return the fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

}
