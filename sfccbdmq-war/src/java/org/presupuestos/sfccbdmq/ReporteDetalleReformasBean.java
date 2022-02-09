/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Reformas;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteDetalleReformas")
@ViewScoped
public class ReporteDetalleReformasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Reformas> listadoReformas;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private int validado;
    private Date desde;
    private Date hasta;
    private Contratos contrato;
//    private String proyecto;
    private String clasificador;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;

    @EJB
    private ReformasFacade ejbReformas;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    private Resource reporte;
    private Resource reporteXls;

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

    /* @return the parametrosSeguridad
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
        String nombreForma = "ReporteDetalleReformasVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
        this.setPerfil(getParametrosSeguridad().traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            getParametrosSeguridad().cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                getParametrosSeguridad().cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of ReformasEmpleadoBean
     */
    public ReporteDetalleReformasBean() {
        listadoReformas = new LazyDataModel<Reformas>() {

            @Override
            public List<Reformas> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        setListadoReformas(new LazyDataModel<Reformas>() {
            @Override
            public List<Reformas> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                String where = "o.fecha between :desde and :hasta ";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                int total;

                if (!((clasificador == null) || (clasificador.isEmpty()))) {
                    where += " and o.asignacion.clasificador.codigo like :clasificador";
                    parametros.put("clasificador", clasificador + "%");
                }
                if (proyectosBean.getProyectoSeleccionado() != null) {
                    where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                    parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
                }
                try {
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.fecha asc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbReformas.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListadoReformas().setRowCount(total);
                    return ejbReformas.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        return null;
    }

    public String imprimir() {
        String where = "o.fecha between :desde and :hasta ";
        Map parametros = new HashMap();

        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
        }
        try {
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha asc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            List<Reformas> lista = ejbReformas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "DETALLE DE REFORMAS ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Reformas.jasper"),
                    lista, "Reformas" + String.valueOf(c.getTimeInMillis()) + ".pdf");
            formularioImprimir.editar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String hojaElectronica() {
        String where = "o.fecha between :desde and :hasta ";
        Map parametros = new HashMap();

        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
        }
        try {
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha asc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            List<Reformas> lista = ejbReformas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "DETALLE DE REFORMAS ");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            //            FM 02Octubre2018
            DocumentoXLS xls = new DocumentoXLS("Detalles de reformas");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "No"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Motivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fuente"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor Asignación"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);

            for (Reformas r : lista) {
                if (r != null) {
                    campos = new LinkedList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getFecha() != null ? (sdf.format(r.getFecha())) : ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getCabecera().getId().toString()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getCabecera().getMotivo()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getAsignacion().getProyecto().getCodigo() + " - " + r.getAsignacion().getProyecto().getNombre()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getAsignacion().getClasificador().getNombre()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getAsignacion().getFuente().getNombre()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(r.getAsignacion().getValor())));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(r.getValor())));
                    xls.agregarFila(campos, false);
                }

            }

            setNombreArchivo("Asignacion.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            reporteXls = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteDetalleReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Reformas> getListadoReformas() {
        return listadoReformas;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Reformas> listadoFamiliares) {
        this.setListadoReformas(listadoFamiliares);
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
     * @return the validado
     */
    public int getValidado() {
        return validado;
    }

    /**
     * @param validado the validado to set
     */
    public void setValidado(int validado) {
        this.validado = validado;
    }

    /**
     * @param listadoReformas the listadoReformas to set
     */
    public void setListadoReformas(LazyDataModel<Reformas> listadoReformas) {
        this.listadoReformas = listadoReformas;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

//    /**
//     * @return the proyecto
//     */
//    public String getProyecto() {
//        return proyecto;
//    }
//
//    /**
//     * @param proyecto the proyecto to set
//     */
//    public void setProyecto(String proyecto) {
//        this.proyecto = proyecto;
//    }
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
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }

}