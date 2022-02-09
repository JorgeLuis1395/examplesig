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
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CodigosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.errores.sfccbdmq.BorrarException;
import org.utilitarios.sfccbdmq.Recurso;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reformasPac")
@ViewScoped
public class ReformasPacBean {

    public ReformasPacBean() {
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
    private List<Proyectospoa> listaProyectosNuevo;
    private List<Proyectospoa> listaProyectosNoModificados;
    private List<Proyectospoa> listaProyecto;

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
        String where = "o.fecha between :desde and :hasta and o.reformapac is not null and o.proyecto is null and o.aprobadopac is not null";
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
            for (Trackingspoa tp : listaTrakings) {
                parametros = new HashMap();
                parametros.put(";where", "o.reformapac=:reformapac and o.aprobadopac is null and o.reforma is not null");
                parametros.put("reformapac", tp.getId());
                List<Trackingspoa> listaReforma = ejbTrackings.encontarParametros(parametros);
                if (!listaReforma.isEmpty()) {
                    Trackingspoa t = listaReforma.get(0);
                    if (t.getReforma().getDirector() == null) {
                        tp.setDirector(Boolean.FALSE);
                    } else {
                        if (t.getReforma().getDirector() == 4) {
                            tp.setDirector(Boolean.TRUE);
                        } else {
                            tp.setDirector(Boolean.FALSE);
                        }
                    }

                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        traking = new Trackingspoa();
        traking.setReformanombre(Boolean.FALSE);
        traking.setFecha(new Date());
        listaProyectos = new LinkedList<>();
        listaProyectosNuevo = new LinkedList<>();
        listaTrakings = new LinkedList<>();
        errores = new LinkedList<>();
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

            for (Proyectospoa p : listaProyectosNuevo) {
                Proyectospoa pn = new Proyectospoa();
                pn = p;
                pn.setPac(Boolean.FALSE);
                pn.setActivo(Boolean.FALSE);
                pn.setFechareforma(traking.getFecha());
                ejbProyectos.create(pn, seguridadbean.getLogueado().getUserid());
            }
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

            }
            Integer nuevoNuemero = num++;
            numeracion.setParametros(nuevoNuemero + "");
            ejbCodigos.edit(numeracion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException  ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir(traking);
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabarCertificacionPAC() {
        try {
            listaProyectos.add(proyectoSeleccionado);
            Map parametros = new HashMap();
            parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
            parametros.put("anio", anio);
            parametros.put("codigo", proyectoSeleccionado.getCodigo());
            List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
            Proyectospoa pre = null;
            for (Proyectospoa pp : lpe) {
                pre = pp;
            }
            if (pre != null) {
                listaProyectosNuevo.add(pre);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioLinea.cancelar();
        return null;
    }

    public String nuevo() {
        proyectoSeleccionado = null;
        formularioLinea.insertar();
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
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.editar();
        buscar();
        return null;
    }

    public String eliminar(Trackingspoa tr) {
        listaProyectos = new LinkedList<>();
        traking = tr;
        Map parametros = new HashMap();
        parametros.put(";where", "o.reformapac=:reformapac");
        parametros.put("reformapac", traking.getId());
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

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String borrar() {
        try {
            List<Proyectospoa> listaBorrar = new LinkedList<>();
            for (Proyectospoa p : listaProyectos) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.fechareforma=:fecha");
                parametros.put("codigo", p.getCodigo());
                parametros.put("fecha", traking.getFecha());
                List<Proyectospoa> listaReforma = ejbProyectos.encontarParametros(parametros);
                if (!listaReforma.isEmpty()) {
                    Proyectospoa p2 = listaReforma.get(0);
                    p = p2;
                    p.setActivo(Boolean.TRUE);
                    p.setPac(Boolean.TRUE);
                    p.setFechareforma(null);
                    p.setFechacreacion(vigente);
                    p.setFechapac(vigente);
                    ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
                    listaBorrar.add(p2);
                }
            }
            for (Proyectospoa pBorrar : listaBorrar) {
                ejbProyectos.remove(pBorrar, seguridadbean.getLogueado().getUserid());
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.reformapac=:reformapac");
            parametros.put("reformapac", traking.getId());
            List<Trackingspoa> lista = ejbTrackings.encontarParametros(parametros);
            for (Trackingspoa tBorrar : lista) {
                ejbTrackings.remove(tBorrar, seguridadbean.getLogueado().getUserid());
            }
            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", traking.getId());
            List<Trackingspoa> lista2 = ejbTrackings.encontarParametros(parametros);
            for (Trackingspoa tBorrar : lista2) {
                ejbTrackings.remove(tBorrar, seguridadbean.getLogueado().getUserid());
            }

        } catch (ConsultarException | GrabarException  ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BorrarException ex) {
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
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

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        anio = cAnio.get(Calendar.YEAR);
        listaProyectos = new LinkedList<>();
        listaProyectosNuevo = new LinkedList<>();
        listaProyectosNoModificados = new LinkedList<>();
        errores = new LinkedList();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        String sb;
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        int registro = 2;
                        while ((sb = entrada.readLine()) != null) {
                            Proyectospoa pu = new Proyectospoa();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(pu, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            // ver si esta ben el registro o es error 
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                            parametros.put("anio", anio);
                            parametros.put("codigo", pu.getCodigo());
                            List<Proyectospoa> lp = ejbProyectos.encontarParametros(parametros);
                            Proyectospoa pr = null;
                            Codigos cCpc = null;
                            Codigos cCompra = null;
                            Codigos cProducto = null;
                            Codigos cProcedimiento = null;
                            Codigos cRegimen = null;
                            for (Proyectospoa pp : lp) {
                                pr = pp;
                            }
                            if (pr == null) {
                                getErrores().add("Proyecto no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
                            if (pr != null) {
                                esReforma = false;
                                //Comparar los proyectos si son diferentes se hace la reforma sino no se hace nada 
                                estaIgual(pu, pr);
                                if (esReforma) {

                                    if (!(pu.getDetalle() == null || pu.getDetalle().isEmpty())) {
                                        pr.setDetalle(pu.getDetalle());
                                    }
                                    if (!(pu.getModificacion() == null || pu.getModificacion().isEmpty())) {
                                        pr.setModificacion(pu.getModificacion());
                                    }
                                    if (!(pu.getResponsable() == null || pu.getResponsable().isEmpty())) {
                                        pr.setResponsable(pu.getResponsable());
                                    }
                                    if (!(pu.getDocumento() == null || pu.getDocumento().isEmpty())) {
                                        pr.setDocumento(pu.getDocumento());
                                    }
//                                    if (!(pu.getValoriva() == null)) {
//                                        pr.setValoriva(pu.getValoriva());
//                                    }
                                    if (!(pu.getCpc() == null || pu.getCpc().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", "o.codigo=:codigo");
                                        parametros.put("codigo", pu.getCpc());
                                        List<Codigos> lcpc = ejbCodigos.encontarParametros(parametros);
                                        for (Codigos cC : lcpc) {
                                            cCpc = cC;
                                        }
                                        if (cCpc == null) {
                                            getErrores().add("CPC no válido en registro: " + String.valueOf(registro) + " - " + pu.getCpc());
                                        } else {
                                            pr.setCpc(cCpc.getCodigo());
                                        }
                                    }
                                    if (!(pu.getPartidaStr() == null || pu.getPartidaStr().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", "o.partida.codigo=:partida and o.proyecto.codigo=:proyecto");
                                        parametros.put("partida", pu.getPartidaStr());
                                        parametros.put("proyecto", pu.getCodigo());
                                        List<Asignacionespoa> lasig = ejbAsignacionespoa.encontarParametros(parametros);
                                        if (lasig.isEmpty()) {
                                            getErrores().add("Partida sin asignación : " + String.valueOf(registro) + " - " + pu.getPartidaStr());
//                                        parametros = new HashMap();
//                                        parametros.put(";where", "o.partida.codigo=:partida");
//                                        parametros.put("partida", pu.getPartidaStr());
//                                        List<Partidaspoa> lpar = ejbPartidaspoa.encontarParametros(parametros);
//                                        Partidaspoa part = null;
//                                        if (!lpar.isEmpty()) {
//                                            part = lpar.get(0);
//                                            Asignacionespoa a = new Asignacionespoa();
//                                            a.setProyecto(pr);
//                                            a.setActivo(Boolean.TRUE);
//                                            a.setPartida(part);
//                                            a.setValor(BigDecimal.ZERO);
//                                            a.setCerrado(Boolean.TRUE);
//                                            a.setFuente("002");
//                                        }
                                        }
                                    }
                                    if (!(pu.getTipocompra() == null || pu.getTipocompra().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", " upper(o.nombre)=:nombre");
                                        parametros.put("nombre", pu.getTipocompra().toUpperCase());
                                        List<Codigos> lc = ejbCodigos.encontarParametros(parametros);
                                        for (Codigos cCom : lc) {
                                            cCompra = cCom;
                                        }
                                        if (cCompra == null) {
                                            getErrores().add("Tipo de Compra no válido en registro: " + String.valueOf(registro) + " - " + pu.getTipocompra());
                                        } else {
                                            pr.setTipocompra(cCompra.getCodigo());
                                        }
                                    }
                                    if (!(pu.getCuatrimestre1() == null)) {
                                        pr.setCuatrimestre1(pu.getCuatrimestre1());
                                    }
                                    if (!(pu.getCuatrimestre2() == null)) {
                                        pr.setCuatrimestre2(pu.getCuatrimestre2());
                                    }
                                    if (!(pu.getCuatrimestre3() == null)) {
                                        pr.setCuatrimestre3(pu.getCuatrimestre3());
                                    }

                                    if (!(pu.getTipoproducto() == null || pu.getTipoproducto().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", " upper(o.nombre)=:nombre");
                                        parametros.put("nombre", pu.getTipoproducto().toUpperCase());
                                        List<Codigos> ltp = ejbCodigos.encontarParametros(parametros);
                                        for (Codigos cTP : ltp) {
                                            cProducto = cTP;
                                        }
                                        if (cProducto == null) {
                                            getErrores().add("Tipo de Producto no válido en registro: " + String.valueOf(registro) + " - " + pu.getTipoproducto());
                                        } else {
                                            pr.setTipoproducto(cProducto.getCodigo());
                                        }
                                    }
                                    if (!(pu.getProcedimientocontratacion() == null || pu.getProcedimientocontratacion().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", " upper(o.nombre)=:nombre");
                                        parametros.put("nombre", pu.getProcedimientocontratacion().toUpperCase());
                                        List<Codigos> lpc = ejbCodigos.encontarParametros(parametros);
                                        for (Codigos cTP : lpc) {
                                            cProcedimiento = cTP;
                                        }
                                        if (cProcedimiento == null) {
                                            getErrores().add("Tipo de Procedimiento no válido en registro: " + String.valueOf(registro) + " - " + pu.getProcedimientocontratacion());
                                        } else {
                                            pr.setProcedimientocontratacion(cProcedimiento.getCodigo());
                                        }
                                    }
                                    if (!(pu.getRegimen() == null || pu.getRegimen().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", " upper(o.nombre)=:nombre");
                                        parametros.put("nombre", pu.getRegimen().toUpperCase());
                                        List<Codigos> lregimen = ejbCodigos.encontarParametros(parametros);
                                        for (Codigos cr : lregimen) {
                                            cRegimen = cr;
                                        }
                                        if (cRegimen == null) {
                                            getErrores().add("Régimen no válido en registro: " + String.valueOf(registro) + " - " + pu.getRegimen());
                                        } else {
                                            pr.setRegimen(cRegimen.getCodigo());
                                        }
                                    }
                                    listaProyectos.add(pr);
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                                    parametros.put("anio", anio);
                                    parametros.put("codigo", pu.getCodigo());
                                    List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
                                    Proyectospoa pre = null;
                                    for (Proyectospoa pp : lpe) {
                                        pre = pp;
                                    }
                                    if (pre != null) {
                                        listaProyectosNuevo.add(pre);
                                    }
                                    registro++;
                                } else {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                                    parametros.put("anio", anio);
                                    parametros.put("codigo", pu.getCodigo());
                                    List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
                                    Proyectospoa pre = null;
                                    for (Proyectospoa pp : lpe) {
                                        pre = pp;
                                    }
                                    if (pre != null) {
                                        listaProyectosNoModificados.add(pre);
                                    }
                                }
                            }
                        }//fin while
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: " + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    private void ubicar(Proyectospoa p, String titulo, String valor) {
        if (titulo.contains("proyecto")) {
            p.setCodigo(valor);
        } else if (titulo.contains("detalle")) {
            p.setDetalle(valor);
        } else if (titulo.contains("compra")) {
            p.setTipocompra(valor);
        } else if (titulo.contains("partida")) {
            p.setPartidaStr(valor);
        } else if (titulo.contains("cpc")) {
            p.setCpc(valor);
//        } else if (titulo.contains("valor")) {
//            String valorSt = valor + "";
//            if (valorSt.trim().length() > 0) {
//                p.setValoriva(new BigDecimal(valor));
//            } else {
//                p.setValoriva(null);
//            }
        } else if (titulo.contains("cuatrimestre1")) {
            if ("S".equals(valor.toUpperCase())) {
                p.setCuatrimestre1(Boolean.TRUE);
            } else {
                if ("N".equals(valor.toUpperCase())) {
                    p.setCuatrimestre1(Boolean.FALSE);
                } else {
                    p.setCuatrimestre1(null);
                }
            }
        } else if (titulo.contains("cuatrimestre2")) {
            if ("S".equals(valor.toUpperCase())) {
                p.setCuatrimestre2(Boolean.TRUE);
            } else {
                if ("N".equals(valor.toUpperCase())) {
                    p.setCuatrimestre2(Boolean.FALSE);
                } else {
                    p.setCuatrimestre2(null);
                }
            }
        } else if (titulo.contains("cuatrimestre3")) {
            if ("S".equals(valor.toUpperCase())) {
                p.setCuatrimestre3(Boolean.TRUE);
            } else {
                if ("N".equals(valor.toUpperCase())) {
                    p.setCuatrimestre3(Boolean.FALSE);
                } else {
                    p.setCuatrimestre3(null);
                }
            }
        } else if (titulo.contains("procedimiento")) {
            p.setProcedimientocontratacion(valor);
        } else if (titulo.contains("producto")) {
            p.setTipoproducto(valor);
        } else if (titulo.contains("regimen")) {
            p.setRegimen(valor);
        } else if (titulo.contains("modificacion")) {
            p.setModificacion(valor);
        } else if (titulo.contains("responsable")) {
            p.setResponsable(valor);
        } else if (titulo.contains("documento")) {
            p.setDocumento(valor);
        } else if (titulo.contains("pac")) {
            if ("S".equals(valor.toUpperCase())) {
                p.setPac(Boolean.TRUE);
            } else {
                if ("N".equals(valor.toUpperCase())) {
                    p.setPac(Boolean.FALSE);
                } else {
                    p.setPac(null);
                }
            }
        }
    }

    private void estaIgual(Proyectospoa nuevo, Proyectospoa anterior) {
        if (nuevo.getCpc() != null && anterior.getCpc() != null) {
            if (!nuevo.getCpc().equals(anterior.getCpc())) {
                esReforma = true;
                return;
            }
        } else {
            esReforma = true;
            return;
        }
        if (nuevo.getTipocompra() != null && anterior.getTipocompra() != null) {
            if (!nuevo.getTipocompra().equals(consultasPoaBean.traerTipoCompra(anterior.getTipocompra()))) {
                esReforma = true;
                return;
            }
        } else {
            esReforma = true;
            return;
        }
        if (nuevo.getValoriva() != null && anterior.getValoriva() != null) {
            if (!nuevo.getValoriva().equals(anterior.getValoriva())) {
                esReforma = true;
                return;
            }
        } else {
            esReforma = true;
            return;
        }
        if (nuevo.getCuatrimestre1() != null) {
            if (nuevo.getCuatrimestre1() != null && anterior.getCuatrimestre1() != null) {
                if (!nuevo.getCuatrimestre1().equals(anterior.getCuatrimestre1())) {
                    esReforma = true;
                    return;
                }
            } else {
                esReforma = true;
                return;
            }
        }
        if (nuevo.getCuatrimestre2() != null) {
            if (nuevo.getCuatrimestre2() != null && anterior.getCuatrimestre2() != null) {
                if (!nuevo.getCuatrimestre2().equals(anterior.getCuatrimestre2())) {
                    esReforma = true;
                    return;
                }
            } else {
                esReforma = true;
                return;
            }
        }
        if (nuevo.getCuatrimestre3() != null) {
            if (nuevo.getCuatrimestre3() != null && anterior.getCuatrimestre3() != null) {
                if (!nuevo.getCuatrimestre3().equals(anterior.getCuatrimestre3())) {
                    esReforma = true;
                    return;
                }
            } else {
                esReforma = true;
                return;
            }
        }
        if (nuevo.getTipoproducto() != null && anterior.getTipoproducto() != null) {
            if (!nuevo.getTipoproducto().equals(consultasPoaBean.traerTipoProducto(anterior.getTipoproducto()))) {
                esReforma = true;
                return;
            }
        } else {
            esReforma = true;
            return;
        }
        if (nuevo.getProcedimientocontratacion() != null && anterior.getProcedimientocontratacion() != null) {
            if (!nuevo.getProcedimientocontratacion().equals(consultasPoaBean.traerTipoProcedimiento(anterior.getProcedimientocontratacion()))) {
                esReforma = true;
                return;
            }
        } else {
            esReforma = true;
            return;
        }
        if (nuevo.getRegimen() != null && anterior.getRegimen() != null) {
            if (!nuevo.getRegimen().equals(consultasPoaBean.traerTipoRegimen(anterior.getRegimen()))) {
                esReforma = true;
                return;
            }
        } else {
            esReforma = true;
            return;
        }
    }

    public boolean valorTotalProyectoPac() {
        try {
            if (proyectoSeleccionado != null) {
                double valorReformas = 0;
                Map parametros = new HashMap();
                parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.asignacion.proyecto.anio=:anio");
                parametros.put("proyecto", proyectoSeleccionado);
                parametros.put("anio", anio);
                parametros.put(";campo", "o.valor");
                valorReformas += ejbReformasPoa.sumarCampo(parametros).doubleValue();

                setValorTotal(valorReformas / 1.12);
                return true;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        setProyectoSeleccionado(null);
        if (listaProyecto == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Proyectospoa p : listaProyecto) {
            String aComparar = tipoBuscar == 1 ? p.getCodigo() : p.getNombre();
            if (aComparar.replaceAll("\\s", "").compareToIgnoreCase(newWord.replaceAll("\\s", "")) == 0) {
                setProyectoSeleccionado(p);
                codigo = p.getCodigo();
            }
        }
    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        setProyectoSeleccionado(null);
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            setProyectoSeleccionado(null);
            return;
        }
        // traer la consulta
        Map parametros = new HashMap();
        String where = "  o.imputable=true and o.activo=true and o.anio=:anio";
        parametros.put("anio", anio);
        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);

        listaProyecto = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            listaProyecto = ejbProyectospoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public String aprobar(Trackingspoa tr) {
        try {

            Map parametros = new HashMap();
            parametros.put(";where", "o.reformapac=:reformapac");
            parametros.put("reformapac", tr.getReformapac());
            List<Proyectospoa> listaP = ejbProyectos.encontarParametros(parametros);
            if (listaP.isEmpty()) {
                MensajesErrores.advertencia("No existen datos");
                return null;
            }
            tr.setAprobadopac(true);
            ejbTrackings.edit(tr, seguridadbean.getLogueado().getUserid());
            List<Proyectospoa> lista2 = new LinkedList<>();
            List<Proyectospoa> lista3 = new LinkedList<>();
            for (Proyectospoa p : listaP) {
                //del proyecto que esta activo se iguala el q guade y esta inactivo
                parametros = new HashMap();
                parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                parametros.put("anio", anio);
                parametros.put("codigo", p.getCodigo());
                List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
                Proyectospoa pre = null;
                for (Proyectospoa pp : lpe) {
                    pre = pp;
                }
                if (pre != null) {
                    pre.setNombre(p.getNombre());
                    pre.setCpc(p.getCpc());
                    pre.setTipocompra(p.getTipocompra());
                    pre.setDetalle(p.getDetalle());
                    pre.setCantidad(p.getCantidad());
                    pre.setUnidad(p.getUnidad());
                    pre.setValoriva(p.getValoriva());
                    pre.setImpuesto(p.getImpuesto());
                    pre.setCuatrimestre1(p.getCuatrimestre1());
                    pre.setCuatrimestre2(p.getCuatrimestre2());
                    pre.setCuatrimestre3(p.getCuatrimestre3());
                    pre.setTipoproducto(p.getTipoproducto());
                    pre.setCatalogoelectronico(p.getCatalogoelectronico());
                    pre.setProcedimientocontratacion(p.getProcedimientocontratacion());
                    pre.setFondobid(p.getFondobid());
                    pre.setNumerooperacion(p.getNumerooperacion());
                    pre.setCodigooperacion(p.getCodigooperacion());
                    pre.setRegimen(p.getRegimen());
                    pre.setPresupuesto(p.getPresupuesto());
                    pre.setReformapac(p.getReformapac());
                    lista2.add(pre);
                }
                parametros = new HashMap();
                parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                parametros.put("anio", anio);
                parametros.put("codigo", p.getCodigo());
                List<Proyectospoa> lpe2 = ejbProyectos.encontarParametros(parametros);
                Proyectospoa pre2 = null;
                for (Proyectospoa pp : lpe2) {
                    pre2 = pp;
                }
                if (pre2 != null) {
                    p.setNombre(pre2.getNombre());
                    p.setCpc(pre2.getCpc());
                    p.setTipocompra(pre2.getTipocompra());
                    p.setDetalle(pre2.getDetalle());
                    p.setCantidad(pre2.getCantidad());
                    p.setUnidad(pre2.getUnidad());
                    p.setValoriva(pre2.getValoriva());
                    p.setImpuesto(pre2.getImpuesto());
                    p.setCuatrimestre1(pre2.getCuatrimestre1());
                    p.setCuatrimestre2(pre2.getCuatrimestre2());
                    p.setCuatrimestre3(pre2.getCuatrimestre3());
                    p.setTipoproducto(pre2.getTipoproducto());
                    p.setCatalogoelectronico(pre2.getCatalogoelectronico());
                    p.setProcedimientocontratacion(pre2.getProcedimientocontratacion());
                    p.setFondobid(pre2.getFondobid());
                    p.setNumerooperacion(pre2.getNumerooperacion());
                    p.setCodigooperacion(pre2.getCodigooperacion());
                    p.setRegimen(pre2.getRegimen());
                    p.setPresupuesto(pre2.getPresupuesto());
                    p.setReformapac(pre2.getReformapac());
                    lista3.add(p);
                }
            }
            for (Proyectospoa p : lista3) {
                p.setPac(Boolean.FALSE);
                p.setActivo(Boolean.FALSE);
                p.setFechareforma(tr.getFecha());
                ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
            }
            for (Proyectospoa p : lista2) {

                p.setPac(Boolean.TRUE);
                p.setActivo(Boolean.TRUE);
                p.setFechapac(tr.getFecha());
                p.setFechacreacion(tr.getFecha());
                p.setFechautilizacion(null);
                p.setReformapac(null);
                ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
                Trackingspoa t = new Trackingspoa();
                t.setReformapac(tr.getId());
                t.setProyecto(p);
                t.setReformanombre(Boolean.FALSE);
                t.setFecha(tr.getFecha());
                t.setResponsable(seguridadbean.getLogueado().getUserid());
                ejbTrackings.create(t, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir(tr);
        formulario.cancelar();
        buscar();
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
}
