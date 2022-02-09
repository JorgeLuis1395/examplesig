/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
import org.entidades.sfccbdmq.Reformaspoa;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
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
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "consolidarPoa")
@ViewScoped
public class ConsolidacionReformasPoaBean {

    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{asignacionesPoa}")
    private AsignacionesPoaBean asignacionesPoaBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    public ConsolidacionReformasPoaBean() {
    }

    private Asignacionespoa asignacion;
    private Reformaspoa reforma;
    private List<Reformaspoa> reformas;
    private List<Cabecerareformaspoa> cabecerasReformaspoa;
    private List<Cabecerareformaspoa> cabecerasReformaspoaUtilizar;
    private Cabecerareformaspoa cabeceraReforma = new Cabecerareformaspoa();
    private Formulario formulario = new Formulario();
    private Formulario formularioCabecera = new Formulario();
    private Date vigente;

    private int anio;
    private Integer id;
    private Date desde;
    private Date hasta;
    private String direccion;

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificaciones;
    @EJB
    private CabecerareformaspoaFacade ejbCabeceras;
    @EJB
    private TrackingspoaFacade ejbTrackings;

    @PostConstruct
    private void activar() {
        setVigente(getConfiguracionBean().getConfiguracion().getPvigentepresupuesto());
        setDesde(getConfiguracionBean().getConfiguracion().getPinicialpresupuesto());
        setHasta(getConfiguracionBean().getConfiguracion().getPfinalpresupuesto());
        Calendar ca = Calendar.getInstance();
        ca.setTime(getDesde());
        setAnio(ca.get(Calendar.YEAR));
        buscar();
    }

    public String buscar() {
        cabecerasReformaspoa = new LinkedList<>();
        if (getAnio() <= 0) {
            setReformas(null);
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.fecha between :desde and :hasta and o.definitivo!=true and o.utilizado=false"
                + " and o.director=4";

        parametros.put(";where", where);
        parametros.put("anio", getAnio());
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        parametros.put(";orden", "o.fecha desc");

        if (getId() != null) {
            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", getId());
        }
        try {
            cabecerasReformaspoa = ejbCabeceras.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearCabecera() {
        if (verificar()) {
            MensajesErrores.advertencia("Seleccione las reformas a consolidar");
            return null;
        }
        cabecerasReformaspoaUtilizar = new LinkedList<>();
        try {
            cabeceraReforma = new Cabecerareformaspoa();
            cabeceraReforma.setFecha(new Date());
            reformas = new LinkedList<>();
            for (Cabecerareformaspoa cr : cabecerasReformaspoa) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.cabecera=:cabecera");
                parametros.put("cabecera", cr);
                List<Reformaspoa> lista = ejbReformas.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    for (Reformaspoa r : lista) {
                        reformas.add(r);
                    }
                }
                cabecerasReformaspoaUtilizar.add(cr);
            }
            formularioCabecera.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean verificar() {
        for (Cabecerareformaspoa crp : cabecerasReformaspoa) {
            if (crp.getUtilizado()) {
                return false;
            }
        }
        return true;
    }

    public String insertarCabecera() {
        if (validar()) {
            return null;
        }
        if ((cabeceraReforma.getMotivo() == null) || (cabeceraReforma.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Ingrese una observación primero");
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            cabeceraReforma.setDefinitivo(Boolean.FALSE);
            cabeceraReforma.setUtilizado(Boolean.TRUE);
            cabeceraReforma.setAnio(c.get(Calendar.YEAR));
            cabeceraReforma.setDirector(null);
            ejbCabeceras.create(cabeceraReforma, getSeguridadbean().getLogueado().getUserid());
            for (Reformaspoa r : getReformas()) {
                r.setCabecera(getCabeceraReforma());
                r.setFecha(getCabeceraReforma().getFecha());
                ejbReformas.create(r, getSeguridadbean().getLogueado().getUserid());
            }

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(getCabeceraReforma());
            tracking.setResponsable(getSeguridadbean().getLogueado().getApellidos() + " " + getSeguridadbean().getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma creada: \n"
                    + "Motivo :" + getCabeceraReforma().getMotivo());
            ejbTrackings.create(tracking, getSeguridadbean().getLogueado().getUserid());
            for (Cabecerareformaspoa cr : cabecerasReformaspoaUtilizar) {
                cr.setUtilizado(true);
                cr.setDirector(6);
                ejbCabeceras.edit(cr, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormularioCabecera().cancelar();
        return null;
    }

    private boolean validar() {

        if (reformas.isEmpty()) {
            MensajesErrores.advertencia("No existen  datos a importar");
            return true;
        }

        if (cabeceraReforma.getTipo() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de reforma");
            return true;
        }
        if (cabeceraReforma.getFecha() == null) {
            MensajesErrores.advertencia("Ingrese fecha de reforma");
            return true;
        }
        if (cabeceraReforma.getFecha().before(getVigente())) {
            MensajesErrores.advertencia("Fecha debe ser mayor o igual al periodo vigente");
            return true;
        }

        return false;
    }

    public String cancelar() {
        getFormularioCabecera().cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String salir() {
        formularioCabecera.cancelar();
        cabeceraReforma = new Cabecerareformaspoa();
        cabecerasReformaspoa = new LinkedList<>();
        buscar();
        return null;
    }

    public double getTotalReformaspoa() {
        Reformaspoa r = reformas.get(getFormulario().getFila().getRowIndex());
        return calculaTotalReformaspoaAsignacion(r.getAsignacion());
    }

    public double getTotalReformaspoaAisgnacion() {
        if ((getPartidasPoaBean().getPartidaPoa() != null)) {
            if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.activo=true");
            parametros.put("clasificador", getPartidasPoaBean().getPartidaPoa());
            parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado());
            try {
                setAsignacion(null);
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    setAsignacion(a);
                }
                if (getAsignacion() == null) {
                    return 0;
                }
                return calculaTotalReformaspoaAsignacion(getAsignacion());
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getValorAsignaciones() {
        if ((getPartidasPoaBean().getPartidaPoa() != null)) {
            if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.proyecto=:actividad and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("partida", getPartidasPoaBean().getPartidaPoa());
            parametros.put("direccion", getDireccion());
            parametros.put("actividad", getProyectosPoaBean().getProyectoSeleccionado());
            try {
                setAsignacion(null);
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    setAsignacion(a);
                    return a.getValor().doubleValue();
                }
                if (getAsignacion() == null) {
                    return 0;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }

    public double getValorCertificacion() {
        if ((getPartidasPoaBean().getPartidaPoa() != null)) {
            if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("clasificador", getPartidasPoaBean().getPartidaPoa());
            parametros.put("direccion", getDireccion());
            parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado());
            try {
                setAsignacion(null);
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    setAsignacion(a);
                }
                if (getAsignacion() == null) {
                    return 0;
                }
                double retorno = calculaTotalCertificacionesAsignacion(getAsignacion());
                return retorno;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    private double calculaTotalCertificacionesAsignacion(Asignacionespoa a) {
        if (a != null) {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("asignacion", a);
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
            try {
                return ejbCertificaciones.sumarCampo(parametros).doubleValue();
            } catch (ConsultarException ex) {
                Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double calculaTotalReformaspoa(Reformaspoa r) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", r.getAsignacion());
        parametros.put("id", r.getId());
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true and o.id!=:id");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double calculaTotalReformaspoaAsignacion(Asignacionespoa a) {
        if (a != null) {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("asignacion", a);
            parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
            try {
                return ejbReformas.sumarCampo(parametros).doubleValue();
            } catch (ConsultarException ex) {
                Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformaspoa r = getReformas().get(getFormulario().getFila().getRowIndex());
        return calculaTotalCertificacionesAsignacion(r.getAsignacion());
    }

    public String estadoDirector(int estado) {
        String e = estado + "";
        if (e.equals("1")) {
            return "Ingresado";
        }
        if (e.equals("2")) {
            return "Aprobado Director";
        }
        if (e.equals("3")) {
            return "Negado Director";
        }
        if (e.equals("4")) {
            return "Aprobado DGA";
        }
        if (e.equals("5")) {
            return "Negado DGA";
        }
        if (e.equals("6")) {
            return "Finalizado";
        }
        return "";
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
     * @return the reforma
     */
    public Reformaspoa getReforma() {
        return reforma;
    }

    /**
     * @param reforma the reforma to set
     */
    public void setReforma(Reformaspoa reforma) {
        this.reforma = reforma;
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
     * @return the cabecerasReformaspoa
     */
    public List<Cabecerareformaspoa> getCabecerasReformaspoa() {
        return cabecerasReformaspoa;
    }

    /**
     * @param cabecerasReformaspoa the cabecerasReformaspoa to set
     */
    public void setCabecerasReformaspoa(List<Cabecerareformaspoa> cabecerasReformaspoa) {
        this.cabecerasReformaspoa = cabecerasReformaspoa;
    }

    /**
     * @return the cabecerasReformaspoaUtilizar
     */
    public List<Cabecerareformaspoa> getCabecerasReformaspoaUtilizar() {
        return cabecerasReformaspoaUtilizar;
    }

    /**
     * @param cabecerasReformaspoaUtilizar the cabecerasReformaspoaUtilizar to
     * set
     */
    public void setCabecerasReformaspoaUtilizar(List<Cabecerareformaspoa> cabecerasReformaspoaUtilizar) {
        this.cabecerasReformaspoaUtilizar = cabecerasReformaspoaUtilizar;
    }

    /**
     * @return the cabeceraReforma
     */
    public Cabecerareformaspoa getCabeceraReforma() {
        return cabeceraReforma;
    }

    /**
     * @param cabeceraReforma the cabeceraReforma to set
     */
    public void setCabeceraReforma(Cabecerareformaspoa cabeceraReforma) {
        this.cabeceraReforma = cabeceraReforma;
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
     * @return the formularioCabecera
     */
    public Formulario getFormularioCabecera() {
        return formularioCabecera;
    }

    /**
     * @param formularioCabecera the formularioCabecera to set
     */
    public void setFormularioCabecera(Formulario formularioCabecera) {
        this.formularioCabecera = formularioCabecera;
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
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
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
}
