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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
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
@ManagedBean(name = "reporteDetalleCert")
@ViewScoped
public class ReporteDetalleCertBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Detallecertificaciones> listadoDetallecertificaciones;
    private List<Detallecertificaciones> listadoDetallecert;
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
    private Detallecertificaciones detalle;
    private String impresas = "TRUE";
    private String anuladas = "TRUE";
    private double valorTotal;
    private double valorsaldo;
    @EJB
    private DetallecertificacionesFacade ejbDetallecertificaciones;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private RascomprasFacade ejbRascompras;
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
     * Creates a new instance of DetallecertificacionesEmpleadoBean
     */
    public ReporteDetalleCertBean() {
        listadoDetallecertificaciones = new LazyDataModel<Detallecertificaciones>() {

            @Override
            public List<Detallecertificaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoDetallecert = new LinkedList<>();
        listadoDetallecertificaciones = new LazyDataModel<Detallecertificaciones>() {
            @Override
            public List<Detallecertificaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
//                String where = "o.certificacion.fecha between :desde and :hasta";
                String where = "o.fecha between :desde and :hasta";
                Map parametros = new HashMap();
//                parametros.put(";orden", "o.certificacion.fecha desc");
                parametros.put(";orden", "o.fecha desc");
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
                        parametros.put("motivo", motivo + "%");
                    }
                    if (!((numero == null) || (numero <= 0))) {
                        where += " and o.certificacion.id=:id";
                        parametros.put("id", numero);
                    }
                    if (!((codigo == null) || (codigo.isEmpty()))) {
                        where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                        parametros.put("proyecto", codigo.toUpperCase() + "%");
                    }
                    if (!(pacpoa == null || pacpoa <= 0)) {
                        where += " and o.certificacion.pacpoa=:pacpoa";
                        parametros.put("pacpoa", pacpoa);
                    }
                    if (!((clasificador == null) || (clasificador.isEmpty()))) {
                        where += " and o.asignacion.clasificador.codigo like :clasificador";
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
                    if (!((anuladas == null) || (anuladas.isEmpty()))) {
                        if (anuladas.equalsIgnoreCase("true")) {
                            where += " and o.certificacion.anulado=true";
                        } else {
                            where += " and o.certificacion.anulado=false";
                        }
                    }
                    if (getValor() != null) {
                        where += " and o.valor=:valor";
                        parametros.put("valor", getValor());
                    }
                    parametros.put(";where", where);
//                    parametros.put(";orden", "o.certificacion.fecha desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    listadoDetallecert = ejbDetallecertificaciones.encontarParametros(parametros);
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
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
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
                where += " and o.asignacion.clasificador.codigo like :clasificador";
                parametros.put("clasificador", getClasificador() + "%");
            }
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                parametros.put("proyecto", codigo.toUpperCase() + "%");
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
            if (!((anuladas == null) || (anuladas.isEmpty()))) {
                if (anuladas.equalsIgnoreCase("true")) {
                    where += " and o.certificacion.anulado=true";
                } else {
                    where += " and o.certificacion.anulado=false";
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
            List<Detallecertificaciones> dl = ejbDetallecertificaciones.encontarParametros(parametros);
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
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String hojaElectronica() {

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
                where += " and o.asignacion.clasificador.codigo like :clasificador";
                parametros.put("clasificador", getClasificador() + "%");
            }
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                parametros.put("proyecto", codigo.toUpperCase() + "%");
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
            if (!((anuladas == null) || (anuladas.isEmpty()))) {
                if (anuladas.equalsIgnoreCase("true")) {
                    where += " and o.certificacion.anulado=true";
                } else {
                    where += " and o.certificacion.anulado=false";
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
            List<Detallecertificaciones> dl = ejbDetallecertificaciones.encontarParametros(parametros);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE CERTIFICACIONES");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("filtro", "Fecha desde : " + sdf.format(desde) + " hasta " + sdf.format(hasta));
            DocumentoXLS xls = new DocumentoXLS("Reporte de certificaciones");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "No"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Motivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Doc"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);

            for (Detallecertificaciones dc : dl) {
                if (dc != null) {
                    if (dc.getCertificacion() != null) {

                        if (dc.getAsignacion() != null) {
                            campos = new LinkedList<>();
                            DecimalFormat df = new DecimalFormat("###,###,##0.00");
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getFecha() != null ? (sdf.format(dc.getCertificacion().getFecha())) : ""));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getId().toString()));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getMotivo()));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getNumerodocumeto() != null ? (dc.getCertificacion().getNumerodocumeto().toString() + "") : ""));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getProyecto().getCodigo()));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getProyecto().getNombre()));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getClasificador().getCodigo()));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getClasificador().getNombre()));
                            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValor())));
                            xls.agregarFila(campos, false);

                        }
                    }

                }

            }

            setNombreArchivo("DetalleCertificacion.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteDetalleCertBean.class.getName()).log(Level.SEVERE, null, ex);
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
    public LazyDataModel<Detallecertificaciones> getListadoDetallecertificaciones() {
        return listadoDetallecertificaciones;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Detallecertificaciones> listadoFamiliares) {
        this.setListadoDetallecertificaciones(listadoFamiliares);
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
     * @param listadoDetallecertificaciones the listadoDetallecertificaciones to
     * set
     */
    public void setListadoDetallecertificaciones(LazyDataModel<Detallecertificaciones> listadoDetallecertificaciones) {
        this.listadoDetallecertificaciones = listadoDetallecertificaciones;
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

    public String verCompromisos(Detallecertificaciones cert) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.detallecertificacion=:certificaciones");
//        parametros.put(";where", "o.detallecertificacion.certificacion=:certificaciones");
        parametros.put("certificaciones", cert);
//        parametros.put("certificaciones", cert.getCertificacion());
        parametros.put(";orden", "o.detallecertificacion.certificacion.id");
        detalle = cert;
        try {
            // paralelo le llevo el reporte
            // incio del reporte
            DocumentoPDF pdf = new DocumentoPDF("DETALLE DE CERTIFICACIÓN NO:" + cert.getId().toString(), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            pdf.agregaParrafo("Motivo : " + cert.getCertificacion().getMotivo() + "\n\n");
            lcompromisos = new LinkedList<>();
//            lcompromisos = ejbCompromisos.encontarParametros(parametros);
            Compromisos cTotal = new Compromisos();
            List<Detallecompromiso> dlComp = ejbDetComp.encontarParametros(parametros);
            double valorCompromisos = 0;
            double saldoCompromisos = 0;
            // poner las cabeceras
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Saldo"));

            columnas = new LinkedList<>();
            for (Detallecompromiso d : dlComp) {
                if (d.getCompromiso().equals(cTotal)) {
                    cTotal.setSaldo(cTotal.getSaldo() + d.getSaldo().doubleValue());
                    cTotal.setTotal(cTotal.getTotal() + d.getValor().doubleValue());
                } else {
                    cTotal = d.getCompromiso();
                    cTotal.setSaldo(d.getSaldo().doubleValue());
                    cTotal.setTotal(d.getValor().doubleValue());
                    getLcompromisos().add(cTotal);
                }
                valorCompromisos += d.getValor().doubleValue();
                saldoCompromisos += d.getSaldo().doubleValue();

            }
//            if (cTotal!=null){
//                getLcompromisos().add(cTotal);
//            }
            cTotal = new Compromisos();
            cTotal.setTotal(valorCompromisos);
            cTotal.setSaldo(saldoCompromisos);
            cTotal.setMotivo("Total");
            getLcompromisos().add(cTotal);

            double total = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.detallecompromiso.detallecertificacion=:cert");
            parametros.put("cert", cert);
            setListaRascompras(ejbRascompras.encontarParametros(parametros));
            Rascompras r1 = new Rascompras();
            r1.setReferencia("TOTAL");
            r1.setValor(BigDecimal.ZERO);
            r1.setValorimpuesto(BigDecimal.ZERO);
            r1.setValorret(BigDecimal.ZERO);
            r1.setVretimpuesto(BigDecimal.ZERO);
            for (Rascompras r : getListaRascompras()) {
                total += r.getObligacion().getApagar() == null ? 0 : r.getObligacion().getApagar().doubleValue();
                r1.setValor(new BigDecimal(r.getValor().doubleValue() + r1.getValor().doubleValue()));
                r1.setValorimpuesto(new BigDecimal(r.getValorimpuesto().doubleValue() + r1.getValorimpuesto().doubleValue()));
                r1.setValorret(new BigDecimal(r.getValorret().doubleValue() + r1.getValorret().doubleValue()));
                r1.setVretimpuesto(new BigDecimal(r.getVretimpuesto().doubleValue() + r1.getVretimpuesto().doubleValue()));
            }

            Obligaciones o = new Obligaciones();
            o.setApagar(new BigDecimal(total));
            o.setConcepto("TOTAL");
            r1.setObligacion(o);
            getListaRascompras().add(r1);
            // reporte interno

            formulario.insertar();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReporteDetalleCertBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(ReporteDetalleCertBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerSaldoCertificacion(Detallecertificaciones dc) {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.compromiso.certificacion=:certificacion and  o.fecha between :desde and :hasta and o.asignacion=:asignacion");
            parametros.put("certificacion", dc.getCertificacion());
            parametros.put("asignacion", dc.getAsignacion());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            double valorComp = ejbDetComp.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.certificacion=:certificacion and  o.fecha between :desde and :hasta and o.asignacion=:asignacion and o.id!=:id");
            parametros.put("certificacion", dc.getCertificacion());
            parametros.put("asignacion", dc.getAsignacion());
            parametros.put("id", dc.getId());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            double valorCert = ejbDetallecertificaciones.sumarCampo(parametros).doubleValue();
            if (dc.getValor().doubleValue() > 0) {
                double total = dc.getValor().doubleValue() - valorComp + valorCert;
                return df.format(total);
            } else {
                return "Liberación o anulación";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "0";
    }

    public double getTotalValor() {
        if (!(listadoDetallecert.isEmpty() || listadoDetallecert == null)) {
            valorTotal = 0;
            for (Detallecertificaciones dc : listadoDetallecert) {
                valorTotal += dc.getValor().doubleValue();
            }
        }
        return valorTotal;
    }

    public double getTotalSaldo() {
        if (!(listadoDetallecert.isEmpty() || listadoDetallecert == null)) {
            valorsaldo = 0;
            double valorComp = 0;
            for (Detallecertificaciones dc : listadoDetallecert) {
                if (dc.getValor().doubleValue() > 0) {
                    try {
                        Map parametros = new HashMap();
                        parametros.put(";campo", "o.valor");
                        parametros.put(";where", "o.compromiso.certificacion=:certificacion and  o.fecha between :desde and :hasta and o.asignacion=:asignacion and o.compromiso.impresion is not null");
                        parametros.put("certificacion", dc.getCertificacion());
                        parametros.put("asignacion", dc.getAsignacion());
                        parametros.put("desde", desde);
                        parametros.put("hasta", hasta);
                        valorComp += ejbDetComp.sumarCampo(parametros).doubleValue();
                    } catch (ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            valorsaldo = valorTotal - valorComp;
        }
        return valorsaldo;
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
    public Detallecertificaciones getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecertificaciones detalle) {
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
    public List<Detallecertificaciones> getListadoDetallecert() {
        return listadoDetallecert;
    }

    /**
     * @param listadoDetallecert the listadoDetallecert to set
     */
    public void setListadoDetallecert(List<Detallecertificaciones> listadoDetallecert) {
        this.listadoDetallecert = listadoDetallecert;
    }

}
