/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.errores.sfccbdmq.ConsultarException;
import javax.faces.application.Resource;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteDetalleCertPacpoa")
@ViewScoped
public class ReporteDetalleCertBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    private List<Compromisos> lcompromisos;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosBean;

    private LazyDataModel<Detallecertificacionespoa> listadoDetallecertificaciones;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Date desde;
    private Date hasta;
    private Organigrama direccion;
    private List<Rascompras> listaRascompras;
    private String clasificador;
    private String motivo;
    private Integer numero;
    private Integer pacpoa;
    private Double valor;
    private Detallecertificacionespoa detalle;
    private String impresas = "TRUE";
    private Resource reporte;
    
    @EJB
    private DetallecertificacionespoaFacade ejbDetallecertificaciones;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private RascomprasFacade ejbRascompras;

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteDetalleCertVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
    }

    // fin perfiles
    /**
     * Creates a new instance of DetallecertificacionesEmpleadoBean
     */
    public ReporteDetalleCertBean() {
        listadoDetallecertificaciones = new LazyDataModel<Detallecertificacionespoa>() {

            @Override
            public List<Detallecertificacionespoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoDetallecertificaciones = new LazyDataModel<Detallecertificacionespoa>() {
            @Override
            public List<Detallecertificacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.certificacion.fecha between :desde and :hasta";
                Map parametros = new HashMap();
                parametros.put(";orden", "o.certificacion.fecha desc");
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                int total;
                try {
                    if (!((motivo == null) || (motivo.isEmpty()))) {
                        where += " and o.certificacion.motivo like :motivo";
                        parametros.put("motivo", getMotivo() + "%");
                    }
                    if (!((numero == null) || (numero <= 0))) {
                        where += " and o.certificacion.id=:id";
                        parametros.put("id", getNumero());
                    }
                    if (!((clasificador == null) || (clasificador.isEmpty()))) {
                        where += " and o.asignacion.partida.codigo like :clasificador";
                        parametros.put("clasificador", getClasificador() + "%");
                    }
                    if (getProyectosBean().getProyectoSeleccionado() != null) {
                        where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                        parametros.put("proyecto", getProyectosBean().getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
                    }
                    if (getDireccion() != null) {
                        where += " and o.certificacion.direccion=:direccion";
                        parametros.put("direccion", getDireccion());
                    }
                    if (!((impresas == null) || (impresas.isEmpty()))) {
                        if (getImpresas().equalsIgnoreCase("true")) {
                            where += " and o.certificacion.impreso=true";
                        } else {
                            where += " and o.certificacion.impreso=false";
                        }
                    }
                    if (getValor() != null) {
                        where += " and o.valor=:valor";
                        parametros.put("valor", getValor());
                    }
                    parametros.put(";where", where);
                    parametros.put("desde", getDesde());
                    parametros.put("hasta", getHasta());
                    total = ejbDetallecertificaciones.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListadoDetallecertificaciones().setRowCount(total);
                    return ejbDetallecertificaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger(ReporteDetalleCertBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String imprimir() {

        String where = "o.certificacion.fecha between :desde and :hasta";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.certificacion.fecha asc");
        try {
            if (!((motivo == null) || (motivo.isEmpty()))) {
                where += " and o.certificacion.motivo like :motivo";
                parametros.put("motivo", motivo + "%");
            }
            if (!((numero == null) || (numero <= 0))) {
                where += " and o.certificacion.id= :id";
                parametros.put("id", numero);
            }
            if (!(pacpoa == null || pacpoa <= 0)) {
                where += " and o.certificacion.pacpoa=:pacpoa";
                parametros.put("pacpoa", pacpoa);
            }
            if (!((clasificador == null) || (clasificador.isEmpty()))) {
                where += " and o.asignacion.partida.codigo like :clasificador";
                parametros.put("clasificador", getClasificador() + "%");
            }
            if (proyectosBean.getProyectoSeleccionado() != null) {
                where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
            }
            if (direccion != null) {
                where += " and o.certificacion.direccion=:direccion";
                parametros.put("direccion", direccion);
            }
            if (!((impresas == null) || (impresas.isEmpty()))) {
                if (impresas.equalsIgnoreCase("true")) {
                    where += " and o.certificacion.impreso=true";
                } else {
                    where += " and o.certificacion.impreso=false";
                }
            }
            if (getValor() != null) {
                where += " and o.valor=:valor";
                parametros.put("valor", getValor());
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.certificacion.fecha desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Detallecertificacionespoa> dl = ejbDetallecertificaciones.encontarParametros(parametros);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE CERTIFICACIONES");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("filtro", "Fecha desde : " + sdf.format(desde) + " hasta " + sdf.format(hasta));
            Calendar cal = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/CompromisoProducto.jasper"),
                    dl, "Compromisos" + String.valueOf(cal.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDetalleCertBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

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
     * @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    /**
     * @return the lcompromisos
     */
    public List<Compromisos> getLcompromisos() {
        return lcompromisos;
    }

    /**
     * @param lcompromisos the lcompromisos to set
     */
    public void setLcompromisos(List<Compromisos> lcompromisos) {
        this.lcompromisos = lcompromisos;
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
     * @return the listadoDetallecertificaciones
     */
    public LazyDataModel<Detallecertificacionespoa> getListadoDetallecertificaciones() {
        return listadoDetallecertificaciones;
    }

    /**
     * @param listadoDetallecertificaciones the listadoDetallecertificaciones to set
     */
    public void setListadoDetallecertificaciones(LazyDataModel<Detallecertificacionespoa> listadoDetallecertificaciones) {
        this.listadoDetallecertificaciones = listadoDetallecertificaciones;
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
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
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
    public Organigrama getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the clasificador
     */
    public String getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(String clasificador) {
        this.clasificador = clasificador;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the pacpoa
     */
    public Integer getPacpoa() {
        return pacpoa;
    }

    /**
     * @param pacpoa the pacpoa to set
     */
    public void setPacpoa(Integer pacpoa) {
        this.pacpoa = pacpoa;
    }

    /**
     * @return the detalle
     */
    public Detallecertificacionespoa getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecertificacionespoa detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the impresas
     */
    public String getImpresas() {
        return impresas;
    }

    /**
     * @param impresas the impresas to set
     */
    public void setImpresas(String impresas) {
        this.impresas = impresas;
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
     * @return the valor
     */
    public Double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(Double valor) {
        this.valor = valor;
    }

    /**
     * @return the listaRascompras
     */
    public List<Rascompras> getListaRascompras() {
        return listaRascompras;
    }

    /**
     * @param listaRascompras the listaRascompras to set
     */
    public void setListaRascompras(List<Rascompras> listaRascompras) {
        this.listaRascompras = listaRascompras;
    }

    /**
     * @return the proyectosBean
     */
    public ProyectosPoaBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosPoaBean proyectosBean) {
        this.proyectosBean = proyectosBean;
    }
}
