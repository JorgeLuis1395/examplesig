/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import java.io.Serializable;
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
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "trackingsPoa")
@ViewScoped
public class TrackingsPoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosPacpoa}")
    private ProyectosPoaBean proyectoPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private LazyDataModel<Trackingspoa> listaTrackings;

    private String proyectos = "";
    private String certificaciones = "";
    private String reformas = "TRUE";

    private Integer anio;
    private Date vigente;
    private Date desde;
    private Date hasta;
    private final Calendar fecha = Calendar.getInstance();

    @EJB
    private AsignacionespoaFacade ejbSubactividad;
    @EJB
    private TrackingspoaFacade ejbTrackings;

    public TrackingsPoaBean() {
        listaTrackings = new LazyDataModel<Trackingspoa>() {
            @Override
            public List<Trackingspoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
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
        vigente = getConfiguracionBean().getConfiguracion().getPvigente();
        desde = getConfiguracionBean().getConfiguracion().getPinicial();
        hasta = getConfiguracionBean().getConfiguracion().getPfinal();
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
        anio = ca.get(Calendar.YEAR);
    }

    public List<Trackingspoa> carga(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        try {
            Map parametros = new HashMap();
            String where = " o.fecha between :desde and :hasta";

            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            parametros.put(";orden", "o.fecha desc");
            if (!((reformas == null) || (reformas.isEmpty()))) {
                if (reformas.equalsIgnoreCase("true")) {
                    where += " and o.reformanombre=true";
                } else {
                    where += " and o.reformanombre=false";
                }
            }
//            //proyectos
//            if (proyectos != null) {
//                if (proyectos.equals("false")) {
//                    where += " and o.proyecto is null ";
//                    proyectoBean.setProyectoSeleccionado(null);
//                } else {
//                    where += " and o.proyecto is not null ";
//                }
//            }
//            //certificaciones
//            if (certificaciones != null) {
//                if (certificaciones.equals("false")) {
//                    where += " and o.certificacion is null ";
//                } else {
//                    where += " and o.certificacion is not null ";
//                }
//            }
//            //reformas
//            if (reformas != null) {
//                if (reformas.equals("false")) {
//                    where += " and o.reforma is null ";
//                } else {
//                    where += " and o.reforma is not null ";
//                }
//            }
//
//            //proyecto
//            if (proyectoBean.getProyectoSeleccionado() != null) {
//                where = " o.proyecto=:proyecto";
//                parametros.put("proyecto", proyectoBean.getProyectoSeleccionado());
//            }
            if (where.isEmpty()) {
                listaTrackings.setRowCount(0);
                return null;
            }

            parametros.put(";where", where);
            int total = 0;
            total = ejbTrackings.contar(parametros);
            int endIndex = i + pageSize;
            if (endIndex > total) {
                endIndex = total;
            }
            parametros.put(";inicial", i);
            parametros.put(";final", endIndex);
            listaTrackings.setRowCount(total);
            return ejbTrackings.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ProyectosPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (desde == null) {
            desde = new Date();
        }
        if (hasta == null) {
            desde = new Date();
        }

        fecha.setTime(desde);
        fecha.set(Calendar.HOUR_OF_DAY, 00);
        fecha.set(Calendar.MINUTE, 00);
        desde = fecha.getTime();

        fecha.setTime(hasta);
        fecha.set(Calendar.HOUR_OF_DAY, 23);
        fecha.set(Calendar.MINUTE, 59);
        hasta = fecha.getTime();

        listaTrackings = new LazyDataModel<Trackingspoa>() {
            @Override
            public List<Trackingspoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return carga(i, i1, scs, map);
            }
        };
        return null;
    }

    /**
     * @return the proyectoPoaBean
     */
    public ProyectosPoaBean getProyectoPoaBean() {
        return proyectoPoaBean;
    }

    /**
     * @param proyectoBean the proyectoPoaBean to set
     */
    public void setProyectoBean(ProyectosPoaBean proyectoPoaBean) {
        this.proyectoPoaBean = proyectoPoaBean;
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
     * @return the listaTrackings
     */
    public LazyDataModel<Trackingspoa> getListaTrackings() {
        return listaTrackings;
    }

    /**
     * @param listaTrackings the listaTrackings to set
     */
    public void setListaTrackings(LazyDataModel<Trackingspoa> listaTrackings) {
        this.listaTrackings = listaTrackings;
    }

    /**
     * @return the proyectos
     */
    public String getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(String proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * @return the certificaciones
     */
    public String getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(String certificaciones) {
        this.certificaciones = certificaciones;
    }

    /**
     * @return the reformas
     */
    public String getReformas() {
        return reformas;
    }

    /**
     * @param reformas the reformas to set
     */
    public void setReformas(String reformas) {
        this.reformas = reformas;
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
     * @return the anio
     */
    public Integer getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(Integer anio) {
        this.anio = anio;
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
     * @param proyectoPoaBean the proyectoPoaBean to set
     */
    public void setProyectoPoaBean(ProyectosPoaBean proyectoPoaBean) {
        this.proyectoPoaBean = proyectoPoaBean;
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
     * @return the fecha
     */
    public Calendar getFecha() {
        return fecha;
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

}
