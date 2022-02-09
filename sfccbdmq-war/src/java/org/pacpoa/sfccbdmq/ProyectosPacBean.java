/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingproyectospoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingproyectospoa;
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
import org.beans.sfccbdmq.EntidadesFacade;
import org.entidades.sfccbdmq.Entidades;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "proyectosPac")
@ViewScoped
public class ProyectosPacBean {

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /*FM 11 ENE 2018*/
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;

    private LazyDataModel<Proyectospoa> proyectos;

    private Formulario formulario = new Formulario();
    private Formulario formularioCertificacion = new Formulario();

    private String codigo;
    private String nombre;
    private Integer anio;
    private String direccion;
    private Date vigente;
    private Date desde;
    private Date hasta;

    /*FM 11 ENE 2019*/
    private Formulario formularioProceso = new Formulario();
    private Formulario formularioAnalista = new Formulario();
    private Date inicioProceso;
    private String observacion;
    private Trackingproyectospoa trackingProyectosPoa;
    private Proyectospoa proyecto;
    private double valorTotal;

    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private ReformaspoaFacade ejbReformasPoa;

    /*FM 11 ENE 2018*/
    @EJB
    private TrackingproyectospoaFacade ejbTrackingproyectospoa;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;

    @PostConstruct
    private void activar() {
        vigente = configuracionBean.getConfiguracion().getPvigentepresupuesto();
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);

//        if (seguridadbean.traerDireccionEmpleado() != null) {
//            if (seguridadbean.traerDireccionEmpleado().getCodigo() != null) {
//                direccion = seguridadbean.traerDireccionEmpleado().getCodigo();
//            } else {
//                direccion = null;
//            }
//        }
    }

    public ProyectosPacBean() {
        proyectos = new LazyDataModel<Proyectospoa>() {

            @Override
            public List<Proyectospoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        proyectos = new LazyDataModel<Proyectospoa>() {

            @Override
            public List<Proyectospoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio and o.activo=true and o.pac=true";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    if (valor.toUpperCase().equals("SI") || valor.toUpperCase().equals("NO")) {
                        where += " and o." + clave + " =" + (valor.toUpperCase().equals("SI") ? "true" : "false");
                    } else if (valor.toUpperCase().equals("")) {
                        where += " and o." + clave + " is not null";

                    } else {
                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                }
                parametros.put("anio", getAnio());
                if (!((codigo == null) || (codigo.isEmpty()))) {
                    where += " and o.codigo like :codigo";
                    parametros.put("codigo", getCodigo() + "%");
                }
                if (!(nombre == null || nombre.isEmpty())) {
                    where += " and upper(o.nombre) like :nombre";
                    parametros.put("nombre", getNombre().toUpperCase() + "%");
                }
                if (!(direccion == null || direccion.isEmpty())) {
                    where += " and upper(o.direccion) =:direccion";
                    parametros.put("direccion", getDireccion().toUpperCase());
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbProyectospoa.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }

                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                getProyectos().setRowCount(total);
                try {
                    return ejbProyectospoa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public Proyectospoa traer(Integer id) throws ConsultarException {
        return ejbProyectospoa.find(id);
    }

    public Proyectospoa traerCodigo(String codigoProyecto) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo = :proyecto");
        parametros.put("proyecto", codigoProyecto);
        try {
            List<Proyectospoa> listaProyectospoa = ejbProyectospoa.encontarParametros(parametros);
            for (Proyectospoa p : listaProyectospoa) {
                return p;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerPartidaProyecto(Proyectospoa pro) {
        String asig = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto=:proyecto");
        parametros.put("proyecto", pro);
        try {
            List<Asignacionespoa> li = ejbAsignacionespoa.encontarParametros(parametros);
            for (Asignacionespoa a : li) {
                asig += a.getPartida().getCodigo() + " / ";
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return asig;
    }

    /*FM 11 EN 2018*/
    public String proceso(Proyectospoa pro) {

        proyecto = pro;
        observacion = "";
        inicioProceso = new Date();
        formularioProceso.insertar();
        return null;
    }

    public String publicacion(Proyectospoa pro) {
        observacion = "";
        inicioProceso = new Date();
        proyecto = pro;
        formularioProceso.editar();
        return null;
    }

    public String desierto(Proyectospoa pro) {
        observacion = "";
        inicioProceso = new Date();
        proyecto = pro;
        formularioProceso.eliminar();
        return null;
    }

    public String analista(Proyectospoa pro) {
        proyecto = pro;
        formularioAnalista.insertar();
        return null;
    }

    public boolean validar() {
        /*FM 26 DIC 2018*/
        if (inicioProceso == null) {
            MensajesErrores.advertencia("Ingrese una fecha");
            return true;
        }

        return false;
    }

    public String grabarProceso() {

        Trackingproyectospoa track = new Trackingproyectospoa();

        if (validar()) {
            return null;
        }
        track.setFechainicio(inicioProceso);
        track.setObservaciones(observacion);
        track.setUsuario(seguridadbean.getLogueado().toString());
        track.setProyecto(proyecto);

        try {

            ejbTrackingproyectospoa.create(track, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioProceso.cancelar();
        return null;
    }

    public String grabarPublicacion() {
        Trackingproyectospoa track = new Trackingproyectospoa();
        if (validar()) {
            return null;
        }
        track.setFechapublicacion(inicioProceso);
        track.setObservaciones(observacion);
        track.setUsuario(seguridadbean.getLogueado().toString());
        track.setProyecto(proyecto);

        try {

            ejbTrackingproyectospoa.create(track, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioProceso.cancelar();
        return null;
    }

    public String grabarDesierto() {

        Trackingproyectospoa track = new Trackingproyectospoa();
        if (validar()) {
            return null;
        }
        track.setFechadesierto(inicioProceso);
        track.setObservaciones(observacion);
        track.setUsuario(seguridadbean.getLogueado().toString());
        track.setProyecto(proyecto);
        try {

            ejbTrackingproyectospoa.create(track, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioProceso.cancelar();
        return null;
    }

    public String grabarAnalista() {

        Trackingproyectospoa track = new Trackingproyectospoa();
        track.setProyecto(proyecto);
        track.setAnalista(empleadoBean.getEmpleadoAdicional().getEntidad().toString());
        try {

            ejbTrackingproyectospoa.create(track, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnalista.cancelar();
        return null;
    }

    public String editarAnalista() {

        Trackingproyectospoa track = new Trackingproyectospoa();
        track.setProyecto(proyecto);
        if (track.getAnalista() == null) {
            MensajesErrores.advertencia("Ingrese un analista.");
        }
        track.setAnalista(empleadoBean.getEmpleadoAdicional().getEntidad().toString());
        try {

            ejbTrackingproyectospoa.edit(track, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnalista.cancelar();
        return null;
    }
    //TRAER NOMBRE DEL GARANTE

    public String traerGarante(Integer idGarante) throws org.errores.sfccbdmq.ConsultarException {
        Entidades retorno = ejbEntidades.find(idGarante);
        if (retorno == null) {
            return "---";
        } else {
            return retorno.toString();
        }
    }

    public String modificarPAC(Proyectospoa pro) {
        try {
            proyecto = pro;
            double valorReformas = 0;
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.asignacion.proyecto.anio=:anio");
            parametros.put("proyecto", proyecto);
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");
            valorReformas += ejbReformasPoa.sumarCampo(parametros).doubleValue();
            if (pro.getImpuesto() == null) {
                pro.setImpuesto(BigDecimal.ZERO);
            }
            if (pro.getImpuesto().doubleValue() == 0) {
                valorTotal = valorReformas;
            } else {
                double imp = (pro.getImpuesto().doubleValue() / 100) + 1;
                valorTotal = (valorReformas / imp);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioCertificacion.editar();
        return null;
    }

    public String grabarCertificacionPAC() {
        if (validarCertificacionPAC()) {
            return null;
        }
        try {
            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCertificacion.cancelar();
        return null;
    }

    private boolean validarCertificacionPAC() {
        if (proyecto.getCpc() == null) {
            MensajesErrores.advertencia("Seleccione CPC");
            return true;
        }
        if (proyecto.getTipocompra() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Compra");
            return true;
        }
        if (proyecto.getDetalle() == null || proyecto.getDetalle().isEmpty()) {
            MensajesErrores.advertencia("Ingresar Detalle");
            return true;
        }
        if (proyecto.getCantidad() == null) {
            MensajesErrores.advertencia("Ingresar Cantidad");
            return true;
        }
        if (proyecto.getUnidad() == null) {
            MensajesErrores.advertencia("Seleccione Unidad");
            return true;
        }
        if (proyecto.getRegimen() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Régimen");
            return true;
        }
        if (proyecto.getTipoproducto() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Producto");
            return true;
        }
        if (proyecto.getProcedimientocontratacion() == null) {
            MensajesErrores.advertencia("Seleccione Procedimiento de Contratación");
            return true;
        }
        if (proyecto.getPresupuesto() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Presupuesto de Inversión");
            return true;
        }
        if (proyecto.getFondobid()) {
            if (proyecto.getCodigooperacion() == null || proyecto.getCodigooperacion().isEmpty()) {
                MensajesErrores.advertencia("Ingrese código de operación");
                return true;
            }
            if (proyecto.getNumerooperacion() == null || proyecto.getNumerooperacion().isEmpty()) {
                MensajesErrores.advertencia("Ingrese número de operación");
                return true;
            }
        }
//        if (proyecto.getFechautilizacion() == null) {
//            MensajesErrores.advertencia("Seleccione Fecha de Utilización");
//            return true;
//        }
        return false;
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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
     * @return the proyectos
     */
    public LazyDataModel<Proyectospoa> getProyectos() {
        return proyectos;
    }

    /**
     * @param proyectos the proyectos to set
     */
    public void setProyectos(LazyDataModel<Proyectospoa> proyectos) {
        this.proyectos = proyectos;
    }

    /**
     * @return the formularioProceso
     */
    public Formulario getFormularioProceso() {
        return formularioProceso;
    }

    /**
     * @param formularioProceso the formularioProceso to set
     */
    public void setFormularioProceso(Formulario formularioProceso) {
        this.formularioProceso = formularioProceso;
    }

    /**
     * @return the inicioProceso
     */
    public Date getInicioProceso() {
        return inicioProceso;
    }

    /**
     * @param inicioProceso the inicioProceso to set
     */
    public void setInicioProceso(Date inicioProceso) {
        this.inicioProceso = inicioProceso;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the trackingProyectosPoa
     */
    public Trackingproyectospoa getTrackingProyectosPoa() {
        return trackingProyectosPoa;
    }

    /**
     * @param trackingProyectosPoa the trackingProyectosPoa to set
     */
    public void setTrackingProyectosPoa(Trackingproyectospoa trackingProyectosPoa) {
        this.trackingProyectosPoa = trackingProyectosPoa;
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
     * @return the proyecto
     */
    public Proyectospoa getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the formularioAnalista
     */
    public Formulario getFormularioAnalista() {
        return formularioAnalista;
    }

    /**
     * @param formularioAnalista the formularioAnalista to set
     */
    public void setFormularioAnalista(Formulario formularioAnalista) {
        this.formularioAnalista = formularioAnalista;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the formularioCertificacion
     */
    public Formulario getFormularioCertificacion() {
        return formularioCertificacion;
    }

    /**
     * @param formularioCertificacion the formularioCertificacion to set
     */
    public void setFormularioCertificacion(Formulario formularioCertificacion) {
        this.formularioCertificacion = formularioCertificacion;
    }

    /**
     * @return the valorTotal
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * @param valorTotal the valorTotal to set
     */
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
