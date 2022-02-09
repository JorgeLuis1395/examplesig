/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CodigosFacade;
import org.utilitarios.sfccbdmq.Recurso;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteReformasPac")
@ViewScoped
public class ReporteReformasPacBean {

    public ReporteReformasPacBean() {
    }

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoaBean;

    private Trackingspoa traking;
    private List errores;
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private Date vigente;
    private int anio;
    private Integer id;
    private String separador = ";";
    private Date desde;
    private Date hasta;

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();

    private List<Trackingspoa> listaTrakings;
    private List<Trackingspoa> listaTrakingsImprimir;
    private List<Proyectospoa> listaProyectos;
    private List<Proyectospoa> listaProyectosNuevo;

    @EJB
    private TrackingspoaFacade ejbTrackings;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    @PostConstruct
    private void activar() {
        vigente = getConfiguracionBean().getConfiguracion().getPvigentepresupuesto();
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);
    }

    public String buscar() {
        if (anio <= 0) {
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.fecha between :desde and :hasta and o.reformapac is not null and o.proyecto is not null";
        parametros.put(";where", where);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fecha desc");

        if (id != null) {
            parametros = new HashMap();
            parametros.put(";where", "o.reformapac=:reformapac");
            parametros.put("reformapac", id);
        }
        try {
            listaTrakings = ejbTrackings.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    public String imprimir(Trackingspoa tr) {
        traking = tr;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.reformapac=:reformapac and o.proyecto.fechapac=:fecha and o.proyecto.pac=true");
        parametros.put("reformapac", traking.getId());
        parametros.put("fecha", traking.getFecha());
        parametros.put(";orden", "o.id");
        try {
            listaTrakingsImprimir = ejbTrackings.encontarParametros(parametros);

            pdf = new DocumentoPDF(""
                    + "Reformas PAC Presupuesto año "
                    + anio, seguridadbean.getLogueado().getUserid());

            pdf.agregaParrafo("\nNúmero de control: " + traking.getReformapac(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafo("Fecha: " + sdf.format(traking.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Concepto: " + traking.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();

            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DETALLE"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO DE COMPRA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "CPC"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "VALOR"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CUATRIMESTRE 1"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CUATRIMESTRE 2"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CUATRIMESTRE 3"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "PROCEDIMIENTO CONTRATACIÓN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "TIPO DE PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "REGIMEN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "MODIFICACIÓN"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "RESPONSABLE"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DOCUMENTO"));

            for (Trackingspoa t : listaTrakingsImprimir) {
                if (t.getId() != null) {
                    String part = traerPartidaProyecto(t.getProyecto());
                    if (t.getProyecto().getValoriva() == null) {
                        t.getProyecto().setValoriva(BigDecimal.ZERO);
                    }
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getCodigo()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getDetalle() != null ? t.getProyecto().getDetalle() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoCompra(t.getProyecto().getTipocompra())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, part != null ? part : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getCpc() != null ? t.getProyecto().getCpc() : ""));
                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getValoriva()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getCuatrimestre1() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getCuatrimestre2() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getCuatrimestre3() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoProcedimiento(t.getProyecto().getProcedimientocontratacion())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoProducto(t.getProyecto().getTipoproducto())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoRegimen(t.getProyecto().getRegimen())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getModificacion() != null ? t.getProyecto().getModificacion() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getResponsable() != null ? t.getProyecto().getResponsable() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, t.getProyecto().getDocumento() != null ? t.getProyecto().getDocumento() : ""));

                }
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("f.____________________________", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("Responsable", AuxiliarReporte.ALIGN_CENTER, 11, false);

            reportepdf = pdf.traerRecurso();

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.editar();
        buscar();
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

    public String salir() {
        formulario.cancelar();
        return null;
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
     * @return the traking
     */
    public Trackingspoa getTraking() {
        return traking;
    }

    /**
     * @param traking the traking to set
     */
    public void setTraking(Trackingspoa traking) {
        this.traking = traking;
    }

    /**
     * @return the errores
     */
    public List getErrores() {
        return errores;
    }

    /**
     * @param errores the errores to set
     */
    public void setErrores(List errores) {
        this.errores = errores;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
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
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
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
     * @return the listaTrakings
     */
    public List<Trackingspoa> getListaTrakings() {
        return listaTrakings;
    }

    /**
     * @param listaTrakings the listaTrakings to set
     */
    public void setListaTrakings(List<Trackingspoa> listaTrakings) {
        this.listaTrakings = listaTrakings;
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
     * @return the listaProyectos
     */
    public List<Proyectospoa> getListaProyectos() {
        return listaProyectos;
    }

    /**
     * @param listaProyectos the listaProyectos to set
     */
    public void setListaProyectos(List<Proyectospoa> listaProyectos) {
        this.listaProyectos = listaProyectos;
    }

    /**
     * @return the consultasPoaBean
     */
    public ConsultasPoaBean getConsultasPoaBean() {
        return consultasPoaBean;
    }

    /**
     * @param consultasPoaBean the consultasPoaBean to set
     */
    public void setConsultasPoaBean(ConsultasPoaBean consultasPoaBean) {
        this.consultasPoaBean = consultasPoaBean;
    }

    /**
     * @return the listaTrakingsImprimir
     */
    public List<Trackingspoa> getListaTrakingsImprimir() {
        return listaTrakingsImprimir;
    }

    /**
     * @param listaTrakingsImprimir the listaTrakingsImprimir to set
     */
    public void setListaTrakingsImprimir(List<Trackingspoa> listaTrakingsImprimir) {
        this.listaTrakingsImprimir = listaTrakingsImprimir;
    }
}
