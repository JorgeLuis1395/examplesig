/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.EjecucionpoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Ejecucionpoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
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
import org.errores.sfccbdmq.BorrarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "ejecucionPoa")
@ViewScoped
public class EjecucionPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPoa;
    @ManagedProperty(value = "#{asignacionesPoa}")
    private AsignacionesPoaBean asignacionesPoaBean;

    private Formulario formulario = new Formulario();
    private LazyDataModel<Ejecucionpoa> listadoEjecucion;
    private Asignacionespoa asignacion;
    private Ejecucionpoa ejecucion;
    private String fuente;

    private final Calendar fecha = Calendar.getInstance();
    private Date desde;
    private Date hasta;
    private Date vigente;

    @EJB
    private EjecucionpoaFacade ejbEjecucionpoa;

    public EjecucionPoaBean() {
        listadoEjecucion = new LazyDataModel<Ejecucionpoa>() {
            @Override
            public List<Ejecucionpoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @PostConstruct
    private void activar() {
        vigente = configuracionBean.getConfiguracion().getPvigentepresupuesto();
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
    }

    private List<Ejecucionpoa> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        String orden = "";
        if (scs.length == 0) {
            orden = " o.fecha desc";
        } else {
            orden += " o." + scs[0].getPropertyName() + (scs[0].isAscending() ? " ASC" : " DESC");
        }

        String where = "o.fecha between :desde and :hasta and o.comprometido=0.00";
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        parametros.put(";orden", orden);
        for (Map.Entry e : map.entrySet()) {
            String clave = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + clave + ") like :valor";
            parametros.put("valor", valor.toUpperCase() + "%");
        }
        if (proyectosPoaBean.getProyectoSeleccionado() != null) {
            where += " and o.proyecto=:proyecto";
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
        }
        if (partidasPoaBean.getPartidaPoa() != null) {
            where += " and o.partida=:partida";
            parametros.put("partida", partidasPoaBean.getPartidaPoa());
        }
        if (fuente != null) {
            where += " and o.fuente=:fuente";
            parametros.put("fuente", fuente);
        }
        if (proyectosPoaBean.getDireccion() != null) {
            where += " and o.proyecto.direccion=:direccion";
            parametros.put("direccion", proyectosPoaBean.getDireccion());
        }

        parametros.put(";where", where);
        int total = 0;
        try {
            total = ejbEjecucionpoa.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = i + pageSize;

        if (endIndex > total) {
            endIndex = total;
        }

        parametros.put(";inicial", i);
        parametros.put(";final", endIndex);
        listadoEjecucion.setRowCount(total);

        try {
            return ejbEjecucionpoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (desde == null) {
            desde = new Date();
        }
        if (hasta == null) {
            hasta = new Date();
        }

        fecha.setTime(desde);
        fecha.set(Calendar.HOUR_OF_DAY, 00);
        fecha.set(Calendar.MINUTE, 00);
        desde = fecha.getTime();

        fecha.setTime(hasta);
        fecha.set(Calendar.HOUR_OF_DAY, 23);
        fecha.set(Calendar.MINUTE, 59);
        hasta = fecha.getTime();

        listadoEjecucion = new LazyDataModel<Ejecucionpoa>() {
            @Override
            public List<Ejecucionpoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };

        return null;
    }

    public String nuevo() {
        if (partidasPoaBean.getPartidaPoa() == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }
        ejecucion = new Ejecucionpoa();
        ejecucion.setFecha(new Date());
        ejecucion.setEjecutado(BigDecimal.ZERO);
        ejecucion.setComprometido(BigDecimal.ZERO);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        ejecucion = (Ejecucionpoa) listadoEjecucion.getRowData();
        asignacionesPoaBean.setClaveBuscar(ejecucion.getProyecto().getCodigo());
        partidasPoaBean.setPartidaPoa(ejecucion.getPartida());
        asignacionesPoaBean.setFuente(ejecucion.getFuente());
        asignacion = asignacionesPoaBean.traerAsignacion(asignacionesPoaBean.getClaveBuscar(), partidasPoaBean.getPartidaPoa(), asignacionesPoaBean.getFuente());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        ejecucion = (Ejecucionpoa) listadoEjecucion.getRowData();
        asignacionesPoaBean.setClaveBuscar(ejecucion.getProyecto().getCodigo());
        partidasPoaBean.setPartidaPoa(ejecucion.getPartida());
        asignacionesPoaBean.setFuente(ejecucion.getFuente());
        asignacion = asignacionesPoaBean.traerAsignacion(asignacionesPoaBean.getClaveBuscar(), partidasPoaBean.getPartidaPoa(), asignacionesPoaBean.getFuente());
        formulario.eliminar();
        return null;

    }

    private boolean validar() {
        if (asignacionesPoaBean.getClaveBuscar() == null) {
            MensajesErrores.advertencia("Seleccione un producto");
            return true;
        }
        asignacion = asignacionesPoaBean.traerAsignacion(asignacionesPoaBean.getClaveBuscar(), partidasPoaBean.getPartidaPoa(), asignacionesPoaBean.getFuente());
        if (ejecucion.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesario");
            return true;
        }

        if (ejecucion.getEjecutado() == null || ejecucion.getEjecutado().doubleValue() <= 0.00) {
            MensajesErrores.advertencia("Valor debe ser mayor a 0");
            return true;
        }
        double comprometido = calculosPoa.traerCompromisos(asignacion.getProyecto().getCodigo(), asignacion.getPartida().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), asignacion.getFuente());
        double ejecutado;
        if (formulario.isNuevo()) {
            ejecutado = calculosPoa.traerEjecutado(asignacion.getProyecto().getCodigo(), asignacion.getPartida().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), asignacion.getFuente());

        } else {
            ejecutado = calculosPoa.traerEjecutado(asignacion.getProyecto().getCodigo(), asignacion.getPartida().getCodigo(), configuracionBean.getConfiguracion().getPinicialpresupuesto(), configuracionBean.getConfiguracion().getPfinalpresupuesto(), asignacion.getFuente(), ejecucion.getId());
        }

        if ((ejecutado + ejecucion.getEjecutado().doubleValue()) > comprometido) {
            MensajesErrores.advertencia("Valor de ejecución sobrepasa el valor comprometido");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejecucion.setFuente(asignacion.getFuente());
            ejecucion.setProyecto(asignacion.getProyecto());
            ejecucion.setPartida(asignacion.getPartida());
            ejbEjecucionpoa.create(ejecucion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(EjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbEjecucionpoa.edit(ejecucion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " " + ex.getCause());
            Logger.getLogger(EjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            ejbEjecucionpoa.remove(ejecucion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            Logger.getLogger(EjecucionPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosPoaBean() {
        return proyectosPoaBean;
    }

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @return the listadoEjecucion
     */
    public LazyDataModel<Ejecucionpoa> getListadoEjecucion() {
        return listadoEjecucion;
    }

    /**
     * @return the ejecucion
     */
    public Ejecucionpoa getEjecucion() {
        return ejecucion;
    }

    /**
     * @return the fecha
     */
    public Calendar getFecha() {
        return fecha;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @return the vigente
     */
    public Date getVigente() {
        return vigente;
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
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @param listadoEjecucion the listadoEjecucion to set
     */
    public void setListadoEjecucion(LazyDataModel<Ejecucionpoa> listadoEjecucion) {
        this.listadoEjecucion = listadoEjecucion;
    }

    /**
     * @param ejecucion the ejecucion to set
     */
    public void setEjecucion(Ejecucionpoa ejecucion) {
        this.ejecucion = ejecucion;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @param vigente the vigente to set
     */
    public void setVigente(Date vigente) {
        this.vigente = vigente;
    }

    /**
     * @return the asignacion
     */
    public Asignacionespoa getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(Asignacionespoa asignacion) {
        this.asignacion = asignacion;
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
     * @return the asignacionesPoaBean
     */
    public AsignacionesPoaBean getAsignacionesPoaBean() {
        return asignacionesPoaBean;
    }

    /**
     * @param asignacionesPoaBean the asignacionesPoaBean to set
     */
    public void setAsignacionesPoaBean(AsignacionesPoaBean asignacionesPoaBean) {
        this.asignacionesPoaBean = asignacionesPoaBean;
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
