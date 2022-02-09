/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.VistaDevengadoFacade;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.VistaDevengado;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteDetalleDevengadoSfccbdmq")
@ViewScoped
public class ReporteDetalleDevengadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<VistaDevengado> listadoVistaDevengado;
    private List<VistaDevengado> listadoDetallecert;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private int validado;
    private Date desde;
    private Date hasta;
    private Organigrama direccion;
    private List<Rascompras> listaRascompras;
    private String clasificador;
    private String codigo;
    private String motivo;
    private Integer numero;
    private Integer pacpoa;
    private Double valor;
    private VistaDevengado detalle;
    private String impresas = "TRUE";
    private String anuladas = "TRUE";
    private double valorTotal;
    private double valorsaldo;
    private Map parametrosExternos;
    @EJB
    private VistaDevengadoFacade ejbVistaDevengado;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    private List<Compromisos> lcompromisos;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    private Resource reporte;
    private Resource reporteXls;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;

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
        String nombreForma = "ReporteDetalleDevVista";
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

//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        listadoDetallecert = new LinkedList<>();
    }

    // fin perfiles
    /**
     * Creates a new instance of VistaDevengadoEmpleadoBean
     */
    public ReporteDetalleDevengadoBean() {
        listadoVistaDevengado = new LazyDataModel<VistaDevengado>() {

            @Override
            public List<VistaDevengado> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoDetallecert = new LinkedList<>();
        listadoVistaDevengado = new LazyDataModel<VistaDevengado>() {
            @Override
            public List<VistaDevengado> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
//                String where = "o.certificacion.fecha between :desde and :hasta";
                String where = "o.fecha between :desde and :hasta";
                Map parametros = new HashMap();
//                parametros.put(";orden", "o.certificacion.fecha desc");
                parametros.put(";orden", "o.fecha desc");
                for (Map.Entry e : map.entrySet()) {

                    String clave = (String) e.getKey();

                    if ((clave.contains("compromisoId")) || (clave.contains("compromisoTipo"))) {
                        where += " and o." + clave + "=:" + clave;
                        String valor = (String) e.getValue();
                        parametros.put(clave, Integer.parseInt(valor));

                    } else {
                        String valor = (String) e.getValue();
                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                }
                int total;
                try {

                    parametros.put(";where", where);
//                    parametros.put(";orden", "o.certificacion.fecha desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    listadoDetallecert = ejbVistaDevengado.encontarParametros(parametros);
                    total = ejbVistaDevengado.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametrosExternos = parametros;
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListadoVistaDevengado().setRowCount(total);
                    return ejbVistaDevengado.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String hojaElectronica() {

        String where = "o.fecha between :desde and :hasta";
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fecha asc");
        try {

            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
//            List<VistaDevengado> dl = ejbVistaDevengado.encontarParametros(parametrosExternos);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "DETALLE DEL DEVENGADO");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("filtro", "Fecha desde : " + sdf.format(desde) + " hasta " + sdf.format(hasta));
            DocumentoXLS xls = new DocumentoXLS("Reporte de certificaciones");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "No COMP"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Motivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Tipo"));
//            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha Contab"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Origen"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);

            for (VistaDevengado dc : listadoDetallecert) {
                if (dc.getValorDevengado() == null) {
                    dc.setValorDevengado(BigDecimal.ZERO);
                }
                campos = new LinkedList<>();
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getFecha() != null ? (sdf.format(dc.getFecha())) : ""));
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, dc.getCompromisoId()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromisoMotivo()));
                campos.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromisoTipo() != null ? dc.getCompromisoTipo() : 0));
//                campos.add(new AuxiliarReporte("Date", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromisoFechaContable()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getOrigen()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCodigoProyecto()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getNombreProyecto()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCodigoPartida()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getNombrePartida()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValorDevengado())));
                xls.agregarFila(campos, false);

            }

            setNombreArchivo("DetalleDevengado.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
//            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteDetalleDevengadoBean.class.getName()).log(Level.SEVERE, null, ex);
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
    public LazyDataModel<VistaDevengado> getListadoVistaDevengado() {
        return listadoVistaDevengado;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<VistaDevengado> listadoFamiliares) {
        this.setListadoVistaDevengado(listadoFamiliares);
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
     * @param listadoVistaDevengado the listadoVistaDevengado to set
     */
    public void setListadoVistaDevengado(LazyDataModel<VistaDevengado> listadoVistaDevengado) {
        this.listadoVistaDevengado = listadoVistaDevengado;
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
     * @return the detalle
     */
    public VistaDevengado getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(VistaDevengado detalle) {
        this.detalle = detalle;
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
     * @return the anuladas
     */
    public String getAnuladas() {
        return anuladas;
    }

    /**
     * @param anuladas the anuladas to set
     */
    public void setAnuladas(String anuladas) {
        this.anuladas = anuladas;
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

    /**
     * @return the valorsaldo
     */
    public double getValorsaldo() {
        return valorsaldo;
    }

    /**
     * @param valorsaldo the valorsaldo to set
     */
    public void setValorsaldo(double valorsaldo) {
        this.valorsaldo = valorsaldo;
    }

    /**
     * @return the listadoDetallecert
     */
    public List<VistaDevengado> getListadoDetallecert() {
        return listadoDetallecert;
    }

    /**
     * @param listadoDetallecert the listadoDetallecert to set
     */
    public void setListadoDetallecert(List<VistaDevengado> listadoDetallecert) {
        this.listadoDetallecert = listadoDetallecert;
    }

}
