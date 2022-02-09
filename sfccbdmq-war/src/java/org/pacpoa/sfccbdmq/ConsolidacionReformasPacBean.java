/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingspoa;
import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CodigosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.utilitarios.sfccbdmq.Recurso;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "consolidacionReformasPac")
@ViewScoped
public class ConsolidacionReformasPacBean {

    public ConsolidacionReformasPacBean() {
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
    private boolean esReforma = false;
    private double valorTotal;
    private Integer tipoBuscar = 2;
    private String codigo;
    private Proyectospoa proyectoSeleccionado;

    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioLinea = new Formulario();

    private List<Trackingspoa> listaTrakings;
    private List<Proyectospoa> listaProyectos;
    private List<Proyectospoa> listaProyectosNoModificados;
    private List<Proyectospoa> listaProyecto;
    private List<Trackingspoa> listaProyectoUtilizar;

    @EJB
    private TrackingspoaFacade ejbTrackings;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformasPoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;

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
        String where = "o.fecha between :desde and :hasta "
                + " and o.reformapac is not null and o.proyecto is null and o.reforma is null"
                + " and o.aprobadopac is null and o.utilizado = false";
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
            listaTrakings = new LinkedList<>();
            List<Trackingspoa> listaT = ejbTrackings.encontarParametros(parametros);
            for (Trackingspoa tp : listaT) {
                parametros = new HashMap();
                parametros.put(";where", "o.reformapac=:reformapac and o.aprobadopac is null and o.reforma is not null");
                parametros.put("reformapac", tp.getId());
                List<Trackingspoa> listaReforma = ejbTrackings.encontarParametros(parametros);
                if (!listaReforma.isEmpty()) {
                    Trackingspoa t = listaReforma.get(0);
                    if (t.getReforma().getDirector() == null) {
                        tp.setDirector(Boolean.FALSE);
                    } else {
                        if (t.getReforma().getDirector() == 4 || t.getReforma().getDirector() == 6) {
                            tp.setDirector(Boolean.TRUE);
                            listaTrakings.add(tp);
                        } else {
                            tp.setDirector(Boolean.FALSE);
                        }
                    }

                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        try {
            boolean selec = true;
            for (Trackingspoa cr : listaTrakings) {
                if (cr.getUtilizado()) {
                    selec = false;
                }
            }
            if (selec) {
                MensajesErrores.advertencia("Seleccione al menor una Reforma PAC");
                return null;
            }
            traking = new Trackingspoa();
            traking.setReformanombre(Boolean.FALSE);
            traking.setFecha(new Date());
            listaProyectos = new LinkedList<>();
            listaProyectoUtilizar = new LinkedList<>();
            errores = new LinkedList<>();
            for (Trackingspoa cr : listaTrakings) {
                if (!cr.getDirector()) {
                    MensajesErrores.advertencia("Solicitud no se encuentra aprobada por el Director");
                    return null;
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.reformapac=:reformapac");
                parametros.put("reformapac", cr.getReformapac());
                List<Proyectospoa> lista = ejbProyectospoa.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    for (Proyectospoa r : lista) {
                        listaProyectos.add(r);
                    }
                }
                listaProyectoUtilizar.add(cr);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String insertar() {
        if (!(getErrores() == null || getErrores().isEmpty())) {
            MensajesErrores.advertencia("Existen errores");
            return null;
        }
        if (listaProyectos == null || listaProyectos.isEmpty()) {
            MensajesErrores.advertencia("No existen registros para grabar");
            return null;
        }
        if (traking.getObservaciones() == null || traking.getObservaciones().isEmpty()) {
            MensajesErrores.advertencia("Falta observaciones");
            return null;
        }
        if (traking.getFecha() == null) {
            MensajesErrores.advertencia("Seleccione una fecha");
            return null;
        }
        Codigos numeracion = codigosBean.traerCodigo("NUM", "02");
        if (numeracion == null) {
            MensajesErrores.advertencia("No existe numeración para Reformas PAC");
            return null;
        }
        String numero = numeracion.getParametros();
        if ((numero == null) || (numero.isEmpty())) {
            numero = "1";
        }
        try {
            int num = Integer.parseInt(numero);
            traking.setReformapac(num);
            traking.setResponsable(seguridadbean.getLogueado().getUserid());
            traking.setAprobadopac(true);
            ejbTrackings.create(traking, seguridadbean.getLogueado().getUserid());

            for (Proyectospoa p : listaProyectos) {
//                p.setPac(Boolean.TRUE);
                p.setActivo(Boolean.TRUE);
                p.setFechapac(traking.getFecha());
                p.setFechacreacion(traking.getFecha());
                p.setFechautilizacion(null);
                ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
                Trackingspoa t = new Trackingspoa();
                t.setReformapac(traking.getId());
                t.setProyecto(p);
                t.setReformanombre(Boolean.FALSE);
                t.setFecha(traking.getFecha());
                t.setResponsable(seguridadbean.getLogueado().getUserid());
                ejbTrackings.create(t, seguridadbean.getLogueado().getUserid());
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.anio=:anio and o.id!=:id and o.activo=true");
                parametros.put("codigo", p.getCodigo());
                parametros.put("anio", p.getAnio());
                parametros.put("id", p.getId());
                List<Proyectospoa> lista = ejbProyectospoa.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    for (Proyectospoa pCambiar : lista) {
                        pCambiar.setActivo(Boolean.FALSE);
                        pCambiar.setPac(Boolean.FALSE);
                        ejbProyectos.edit(pCambiar, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            Integer nuevoNuemero = num++;
            numeracion.setParametros(nuevoNuemero + "");
            ejbCodigos.edit(numeracion, seguridadbean.getLogueado().getUserid());

            for (Trackingspoa cr : listaProyectoUtilizar) {
                cr.setUtilizado(true);
                ejbTrackings.edit(cr, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException  | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir(traking);
        formulario.cancelar();
        buscar();
        return null;
    }

    public String imprimir(Trackingspoa tr) {
        listaProyectos = new LinkedList<>();
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.reformapac=:reformapac");
        parametros.put("reformapac", tr.getId());
        try {
            List<Trackingspoa> lista = ejbTrackings.encontarParametros(parametros);
            for (Trackingspoa tp : lista) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.fechacreacion=:fecha");
                parametros.put("codigo", tp.getProyecto().getCodigo());
                parametros.put("fecha", tp.getFecha());
                parametros.put(";orden", "o.codigo");
                List<Proyectospoa> listaproye = ejbProyectos.encontarParametros(parametros);
                if (!listaproye.isEmpty()) {
                    Proyectospoa pr = listaproye.get(0);
                    listaProyectos.add(pr);
                }
            }

            pdf = new DocumentoPDF(""
                    + "Reformas PAC Presupuesto año "
                    + anio, seguridadbean.getLogueado().getUserid());

            pdf.agregaParrafo("\nNúmero de control: " + tr.getReformapac(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafo("Fecha: " + sdf.format(tr.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Concepto: " + tr.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 11, false);
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

            for (Proyectospoa p : listaProyectos) {
                if (p.getId() != null) {
                    String part = traerPartidaProyecto(p);
                    if (p.getValoriva() == null) {
                        p.setValoriva(BigDecimal.ZERO);
                    }
                    if (p.getCuatrimestre1() == null) {
                        p.setCuatrimestre1(Boolean.FALSE);
                    }
                    if (p.getCuatrimestre2() == null) {
                        p.setCuatrimestre2(Boolean.FALSE);
                    }
                    if (p.getCuatrimestre3() == null) {
                        p.setCuatrimestre3(Boolean.FALSE);
                    }
                    DecimalFormat df = new DecimalFormat("###,##0.00");
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCodigo()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getDetalle() != null ? p.getDetalle() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoCompra(p.getTipocompra())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, part != null ? part : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCpc() != null ? p.getCpc() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, df.format(p.getValoriva())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCuatrimestre1() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCuatrimestre2() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCuatrimestre3() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoProcedimiento(p.getProcedimientocontratacion())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoProducto(p.getTipoproducto())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, consultasPoaBean.traerTipoRegimen(p.getRegimen())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getModificacion() != null ? p.getModificacion() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getResponsable() != null ? p.getResponsable() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getDocumento() != null ? p.getDocumento() : ""));

                }
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("f.____________________________", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("Responsable", AuxiliarReporte.ALIGN_CENTER, 11, false);

            reportepdf = pdf.traerRecurso();

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ConsolidacionReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the formularioLinea
     */
    public Formulario getFormularioLinea() {
        return formularioLinea;
    }

    /**
     * @param formularioLinea the formularioLinea to set
     */
    public void setFormularioLinea(Formulario formularioLinea) {
        this.formularioLinea = formularioLinea;
    }

    /**
     * @return the esReforma
     */
    public boolean isEsReforma() {
        return esReforma;
    }

    /**
     * @param esReforma the esReforma to set
     */
    public void setEsReforma(boolean esReforma) {
        this.esReforma = esReforma;
    }

    /**
     * @return the listaProyectosNoModificados
     */
    public List<Proyectospoa> getListaProyectosNoModificados() {
        return listaProyectosNoModificados;
    }

    /**
     * @param listaProyectosNoModificados the listaProyectosNoModificados to set
     */
    public void setListaProyectosNoModificados(List<Proyectospoa> listaProyectosNoModificados) {
        this.listaProyectosNoModificados = listaProyectosNoModificados;
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
     * @return the listaProyecto
     */
    public List<Proyectospoa> getListaProyecto() {
        return listaProyecto;
    }

    /**
     * @param listaProyecto the listaProyecto to set
     */
    public void setListaProyecto(List<Proyectospoa> listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    /**
     * @return the tipoBuscar
     */
    public Integer getTipoBuscar() {
        return tipoBuscar;
    }

    /**
     * @param tipoBuscar the tipoBuscar to set
     */
    public void setTipoBuscar(Integer tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
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
     * @return the proyectoSeleccionado
     */
    public Proyectospoa getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    /**
     * @param proyectoSeleccionado the proyectoSeleccionado to set
     */
    public void setProyectoSeleccionado(Proyectospoa proyectoSeleccionado) {
        this.proyectoSeleccionado = proyectoSeleccionado;
    }

    /**
     * @return the listaProyectoUtilizar
     */
    public List<Trackingspoa> getListaProyectoUtilizar() {
        return listaProyectoUtilizar;
    }

    /**
     * @param listaProyectoUtilizar the listaProyectoUtilizar to set
     */
    public void setListaProyectoUtilizar(List<Trackingspoa> listaProyectoUtilizar) {
        this.listaProyectoUtilizar = listaProyectoUtilizar;
    }
}
