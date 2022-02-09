/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.faces.application.Resource;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteCertificacionesSfccbdmq")
@ViewScoped
public class ReporteCertificacionesBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ReporteCertificacionesBean() {
    }
//    private Proyectos proyecto;
    private double totalCertificaciones;
    private List<Certificaciones> certificaciones;
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioReporte = new Formulario();
    @EJB
    private CertificacionesFacade ejbCertificaciones;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private DetallecertificacionesFacade ejbDetalle;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    private Perfiles perfil;
    private List<Compromisos> lcompromisos;
    private List<Detallecertificaciones> ldetalles;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
    private Integer numero;
    private Integer pacpoa;
    private String impresas = "TRUE";
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "ReporteCertificacionesVista";
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
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
    }

    /**
     * @return the certificaciones
     */
    public List<Certificaciones> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(List<Certificaciones> certificaciones) {
        this.certificaciones = certificaciones;
    }

    private void estaEnCeritificacion(Detallecertificaciones d) {
        for (Certificaciones c : certificaciones) {
            if (c.equals(d.getCertificacion())) {
                c.setMonto(c.getMonto() + d.getValor().doubleValue());
                return;
            }
        }
        Certificaciones c = d.getCertificacion();
//        c=d.getCertificacion();
        c.setMonto(d.getValor().doubleValue());
        certificaciones.add(c);
    }

    public String hojaPdf() {
        if (proyectosBean.getProyectoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un proyecto primero");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.asignacion.proyecto=:proyecto";
        parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
        if (!(numero == null || numero <= 0)) {
            where += " and o.certificacion.id=:id";
            parametros.put("id", numero);
        }
        if (!(pacpoa == null || pacpoa <= 0)) {
            where += " and o.certificacion.pacpoa=:pacpoa";
            parametros.put("pacpoa", pacpoa);
        }
        if (!((impresas == null) || (impresas.isEmpty()))) {
            if (impresas.equalsIgnoreCase("true")) {
                where += " and o.certificacion.impreso=true";
            } else {
                where += " and o.certificacion.impreso=false";
            }
        }

        parametros.put(";where", where);
        parametros.put(";orden", "o.certificacion.fecha desc");
        totalCertificaciones = 0;
        try {
            List<Detallecertificaciones> dl = ejbDetalle.encontarParametros(parametros);
            certificaciones = new LinkedList<>();
            Certificaciones c = new Certificaciones();
            for (Detallecertificaciones d : dl) {
                estaEnCeritificacion(d);
//                if (!c.equals(d.getCertificacion())) {
//                    d.getCertificacion().setMonto(d.getValor().doubleValue());
//                    certificaciones.add(d.getCertificacion());
//                }
                totalCertificaciones += d.getValor().doubleValue();
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE CERTIFICACIONES");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("filtro", "Proyecto : " + proyectosBean.getProyectoSeleccionado().toString());
            Calendar cal = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/CompromisoProducto.jasper"),
                    dl, "Compromisos" + String.valueOf(cal.getTimeInMillis()) + ".pdf");
//            parametros.put(";campo", "o.valor");
//            totalCertificaciones=ejbCertificaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String hojaElectronica() {
        if (proyectosBean.getProyectoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un proyecto primero");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.asignacion.proyecto=:proyecto";
        parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
        if (!(numero == null || numero <= 0)) {
            where += " and o.certificacion.id=:id";
            parametros.put("id", numero);
        }
        if (!(pacpoa == null || pacpoa <= 0)) {
            where += " and o.certificacion.pacpoa=:pacpoa";
            parametros.put("pacpoa", pacpoa);
        }
        if (!((impresas == null) || (impresas.isEmpty()))) {
            if (impresas.equalsIgnoreCase("true")) {
                where += " and o.certificacion.impreso=true";
            } else {
                where += " and o.certificacion.impreso=false";
            }
        }

        parametros.put(";where", where);
        parametros.put(";orden", "o.certificacion.fecha desc");
        totalCertificaciones = 0;
        try {
            List<Detallecertificaciones> dl = ejbDetalle.encontarParametros(parametros);
            certificaciones = new LinkedList<>();
            Certificaciones c = new Certificaciones();
            for (Detallecertificaciones d : dl) {
                estaEnCeritificacion(d);
//                if (!c.equals(d.getCertificacion())) {
//                    d.getCertificacion().setMonto(d.getValor().doubleValue());
//                    certificaciones.add(d.getCertificacion());
//                }
                totalCertificaciones += d.getValor().doubleValue();
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE CERTIFICACIONES");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("filtro", "Proyecto : " + proyectosBean.getProyectoSeleccionado().toString());
//            FM 02Octubre2018
            DocumentoXLS xls = new DocumentoXLS("Certificaciones");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "No"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Motivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Doc"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Clasificador"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);

            for (Detallecertificaciones dc : dl) {
                if (dc != null) {

                    campos = new LinkedList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    DecimalFormat df = new DecimalFormat("###,###,##0.00");
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getFecha() != null ? (sdf.format(dc.getFecha())) : ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getId().toString()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getMotivo()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCertificacion().getNumerodocumeto() != null ? (dc.getCertificacion().getNumerodocumeto().toString() + "") : ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getProyecto().getCodigo()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getProyecto().getNombre()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getClasificador().getCodigo()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValor())));
                    xls.agregarFila(campos, false);

                }

            }

            setNombreArchivo("Certificaciones.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            reporteXls = xls.traerRecurso();
            formularioReporte.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        hojaPdf();
        hojaElectronica();
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
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
    }

    /**
     * @return the totalCertificaciones
     */
    public double getTotalCertificaciones() {
        return totalCertificaciones;
    }

    /**
     * @param totalCertificaciones the totalCertificaciones to set
     */
    public void setTotalCertificaciones(double totalCertificaciones) {
        this.totalCertificaciones = totalCertificaciones;
    }

    public double getValor() {
        Certificaciones c = certificaciones.get(formulario.getFila().getRowIndex());
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion and o.certificacion.impreso=true");
        parametros.put("certificacion", c);
        parametros.put(";orden", "o.certificaion.fecha desc");
        try {
            parametros.put(";campo", "o.valor");
            return ejbDetalle.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

    public String verCompromisos(Certificaciones cert) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.detallecertificacion.certificacion=:certificaciones");
        parametros.put("certificaciones", cert);
        parametros.put(";orden", "o.detallecertificacion.certificacion.id");
        try {
            lcompromisos = new LinkedList<>();
//            lcompromisos = ejbCompromisos.encontarParametros(parametros);
            Compromisos cTotal = new Compromisos();
            List<Detallecompromiso> dlComp = ejbDetComp.encontarParametros(parametros);
            double valorCompromisos = 0;
            double saldoCompromisos = 0;
            for (Detallecompromiso d : dlComp) {
                if (d.getCompromiso().equals(cTotal)) {
                    cTotal.setSaldo(cTotal.getSaldo() + d.getSaldo().doubleValue());
                    cTotal.setTotal(cTotal.getTotal() + d.getValor().doubleValue());
                } else {
                    cTotal = d.getCompromiso();
                    cTotal.setSaldo(d.getSaldo().doubleValue());
                    cTotal.setTotal(d.getValor().doubleValue());
                    lcompromisos.add(cTotal);
                }
                valorCompromisos += d.getValor().doubleValue();
                saldoCompromisos += d.getSaldo().doubleValue();
            }
            cTotal = new Compromisos();
            cTotal.setTotal(valorCompromisos);
            cTotal.setSaldo(saldoCompromisos);
            cTotal.setMotivo("Total");
            lcompromisos.add(cTotal);
            formulario.insertar();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String verCertificaciones(Certificaciones cert) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificaciones");
        parametros.put("certificaciones", cert);
        parametros.put(";orden", "o.certificacion.id");
        try {
            lcompromisos = new LinkedList<>();
//            lcompromisos = ejbCompromisos.encontarParametros(parametros);
            Detallecertificaciones cTotal = new Detallecertificaciones();
            ldetalles = ejbDetalle.encontarParametros(parametros);
            double valorCompromisos = 0;
            double saldoCompromisos = 0;
            for (Detallecertificaciones d : ldetalles) {
//                if (d.getCompromiso().equals(cTotal)) {
//                    cTotal.setSaldo(cTotal.getSaldo() + d.getSaldo().doubleValue());
//                    cTotal.setTotal(cTotal.getTotal() + d.getValor().doubleValue());
//                } else {
//                    cTotal = d.getCompromiso();
//                    cTotal.setSaldo(d.getSaldo().doubleValue());
//                    cTotal.setTotal(d.getValor().doubleValue());
//                    lcompromisos.add(cTotal);
//                }
                valorCompromisos += d.getValor().doubleValue();
//                saldoCompromisos += d.getSaldo().doubleValue();
            }
//            cTotal = new Compromisos();
            cTotal.setValor(new BigDecimal(valorCompromisos));
//            cTotal.setSaldo(saldoCompromisos);
//            cTotal.setCivo("Total");
            ldetalles.add(cTotal);
            formularioDetalle.insertar();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the ldetalles
     */
    public List<Detallecertificaciones> getLdetalles() {
        return ldetalles;
    }

    /**
     * @param ldetalles the ldetalles to set
     */
    public void setLdetalles(List<Detallecertificaciones> ldetalles) {
        this.ldetalles = ldetalles;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
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