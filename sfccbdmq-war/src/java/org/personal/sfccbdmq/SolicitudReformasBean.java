/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.pacpoa.sfccbdmq.*;
import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
import org.entidades.sfccbdmq.Partidaspoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Reformaspoa;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.errores.sfccbdmq.BorrarException;
import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCargaPoa;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;

import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerareformasFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabecerareformas;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Reformas;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.presupuestos.sfccbdmq.CertificacionesBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.firma.FileUtils;
import org.utilitarios.firma.FirmadorPDF;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "solicitudReformasPersonal")
@ViewScoped
public class SolicitudReformasBean {

    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{asignacionesPoa}")
    private AsignacionesPoaBean asignacionesPoaBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{imagenesPoa}")
    private ImagenesPoaBean imagenesPoaBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoa;

    @ManagedProperty(value = "#{proyectosDireccionPoa}")
    private ProyectosDireccionPoaBean proyectosDirPoaBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoaBean;

    public SolicitudReformasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioCabecera = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioLinea = new Formulario();
    private Formulario formularioSubactividad = new Formulario();
    private Formulario formularioSolicitud = new Formulario();
    private Formulario formularioFirma = new Formulario();

    private Asignacionespoa asignacion;
    private Reformaspoa reforma;

    private List<Reformaspoa> reformas;
    private List<Reformaspoa> reformasb;
    private List<Cabecerareformaspoa> cabecerasReformaspoa;
    private List<AuxiliarCargaPoa> totales = new LinkedList<>();
    private List errores;
    private List erroresPac;
    private List<Trackingspoa> listaTrakings;
    private List<Proyectospoa> listaProyectosPac;
    private List<Proyectospoa> listaProyectosSubactividad;
    private List<Asignacionespoa> listaPartidas;
//    private List<Proyectospoa> listaProyectosNuevo;
//    private List<Proyectospoa> listaProyectosNoModificados;
    private List<Proyectospoa> listaProyecto;
    private List<Proyectospoa> listaProyectoNombre;
    private List<Reformas> reformasfin;

    private Cabecerareformaspoa cabeceraReforma;
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private Date vigente;
    private Integer nivelProyecto = 0;
    private Proyectospoa proyectoPadre;
    private String codigoHijo;
    private Cabecerareformas cabeceraReformaFinanciero;
    private Trackingspoa traking;
    private Recurso reportepdfPac;
    private DocumentoPDF pdfPAc;
    private int anio;
    private Integer id;
    private String separadorPac = ";";
    private Date desde;
    private Date hasta;
    private boolean esReforma = false;
    private double valorTotal;
    private String codigo;
    private String codigoNombre;
    private Proyectospoa proyectoSeleccionado;
    private Proyectospoa proyectoSubactividad;
    private Proyectospoa proyectoSubactividadNuevo;
    private Proyectospoa proyectoNombre;
    private String separador = ";";
    private String direccion;
    private String direccionMostrar;
    private Codigos tipoReforma;
    private String proyectoCambia;
    private Boolean puedeEditar = false;
    private Integer tipoBuscar = 1;
    private String codigoPartida;
    private Partidaspoa partidaPoa;
    private List<Partidaspoa> partidasLista;
    private Boolean ingreso;
    private String direccionFondo;
    private String proyectoFondo;
    private Asignacionespoa asignacionFondo;

    private String cargo = "";
    private String clave;
    private File archivoFirmar;
    private String nombreArchivo;
    private String tipoArchivo;
    private Recurso reporteSolicitudpdf;
    private Recurso recurso;

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private AsignacionesFacade ejbAsignacionesFinanciera;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private ReformasFacade ejbReformasFinancieras;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificaciones;
    @EJB
    private CabecerareformaspoaFacade ejbCabeceras;
    @EJB
    private CabecerareformasFacade ejbCabecerasFinanciera;
    @EJB
    private TrackingspoaFacade ejbTrackings;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private ProyectosFacade ejbProyectosFinanciera;
    @EJB
    private ClasificadoresFacade ejbPartidasFinanciera;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    @PostConstruct
    private void activar() {
//        List<Codigos> configuracion = codigosBean.traerCodigoMaestro("CONF");
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        for (Codigos c : configuracion) {
//            try {
//                if (c.getCodigo().equals("PVGT")) {
//                    vigente = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PINI")) {
//                    desde = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PFIN")) {
//                    hasta = sdf.parse(c.getParametros());
//                }
//            } catch (ParseException ex) {
//                Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        vigente = getConfiguracionBean().getConfiguracion().getPvigentepresupuesto();
        setDesde(getConfiguracionBean().getConfiguracion().getPinicialpresupuesto());
        setHasta(getConfiguracionBean().getConfiguracion().getPfinalpresupuesto());
        Calendar ca = Calendar.getInstance();
        ca.setTime(getDesde());
        setAnio(ca.get(Calendar.YEAR));

//TRAE LA LONGITUD DEL CODIGO DE PROYECTO QUE SE ENCUENTRA EN CÓDIGOS        
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(getAnio()));
        if (c != null && c.getParametros() != null && !c.getParametros().isEmpty()) {
            int total = c.getParametros().replaceAll("\\.", "").length();
//            nivelProyecto=total;
            String formato = c.getParametros().replace(".", "#");
            String[] sinpuntos = formato.split("#");
            int grupos = sinpuntos.length;
            if (grupos > 2) {
                //int total1 = sinpuntos[grupos - 1].length() + sinpuntos[grupos - 2].length();
                int total1 = sinpuntos[grupos - 1].length();
                nivelProyecto = total - total1;
            }
        }

        try {
            direccion = null;
            direccionMostrar = "";
            if (seguridadbean.getLogueado().getEmpleados() != null) {
                Organigrama oDireccion = null;
                Map parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.activo=true and o.desde between :desde and :hasta");
                parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
                parametros.put("desde", getConfiguracionBean().getConfiguracion().getPinicialpresupuesto());
                parametros.put("hasta", new Date());
                List<Historialcargos> listaBuscar = ejbHistorialcargos.encontarParametros(parametros);
                if (!listaBuscar.isEmpty()) {
                    Historialcargos hc = listaBuscar.get(0);
                    oDireccion = seguridadbean.traerDireccion(hc.getCargo().getOrganigrama().getCodigo());
                    if (oDireccion != null) {
                        // solo los direcctores pueden aprobar la solicitud creada
                        puedeEditar = true;
                        direccion = oDireccion.getCodigo();
                        direccionMostrar = oDireccion.toString();
                        proyectosPoaBean.setDireccion(direccion);
                        cargo = hc.getCargo().getCargo().getNombre();
                    }
                } else {
                    if (seguridadbean.getLogueado().getEmpleados().getCargoactual() != null) {
                        oDireccion = seguridadbean.traerDireccion(seguridadbean.getLogueado().getEmpleados().getCargoactual().getOrganigrama().getCodigo());
                        if (oDireccion != null) {
                            // solo los direcctores pueden aprobar la solicitud creada
                            puedeEditar = true;
                            direccion = oDireccion.getCodigo();
                            direccionMostrar = oDireccion.toString();
                            proyectosPoaBean.setDireccion(direccion);
                            cargo = seguridadbean.getLogueado().getEmpleados().getCargoactual().getCargo().getNombre();
                        }
                    }
                }
                if (direccion == null || direccion.isEmpty()) {
                    Organigrama o = seguridadbean.traerDireccion(seguridadbean.getLogueado().getEmpleados().getCargoactual().getOrganigrama().getCodigo().substring(0, 3));
                    if (o != null) {
                        // direccion
                        direccion = o.getCodigo();
                        direccionMostrar = o.toString();
                        proyectosPoaBean.setDireccion(direccion);
                        puedeEditar = false;
                    }
                }
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String buscar() {
        if (getAnio() <= 0) {
            reformas = null;
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.cabecera.anio=:anio and o.cabecera.fecha between :desde and :hasta "
                    //                    + "and o.cabecera.director is not null and o.cabecera.definitivo=false "
                    + " and o.cabecera.definitivo=false "
                    + "and o.asignacion.proyecto.direccion=:direccion and o.cabecera.director is not null";

            parametros.put(";orden", "o.fecha desc");
            if (getId() != null) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.cabecera.id=:id");
                where += " and o.id=:id";
                parametros.put("id", getId());
            }
            parametros.put(";where", where);
            parametros.put("anio", getAnio());
            parametros.put("desde", getDesde());
            parametros.put("hasta", getHasta());
            parametros.put("direccion", direccion);
            List<Reformaspoa> lista = ejbReformas.encontarParametros(parametros);
            cabecerasReformaspoa = new LinkedList<>();
            if (!lista.isEmpty()) {
                for (Reformaspoa r : lista) {
                    yaExiste(r.getCabecera());
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void yaExiste(Cabecerareformaspoa cr) {
        for (Cabecerareformaspoa c : cabecerasReformaspoa) {
            if (c.equals(cr)) {
                return;
            }
        }
        cabecerasReformaspoa.add(cr);
    }

    public String crearCabecera() {
        cabeceraReforma = new Cabecerareformaspoa();
        cabeceraReforma.setFecha(new Date());
        cabeceraReforma.setDirector(1);
        reformas = new LinkedList<>();
        reformasb = new LinkedList<>();
        errores = new LinkedList<>();
        formularioCabecera.insertar();
        traking = new Trackingspoa();
        traking.setReformanombre(Boolean.FALSE);
        traking.setFecha(new Date());
        traking.setObservaciones("");
        listaProyecto = new LinkedList<>();
        listaTrakings = new LinkedList<>();
        erroresPac = new LinkedList<>();

//        setListaProyectosNuevo(new LinkedList<>());
        return null;
    }

    public String crear() {

        formularioClasificador.insertar();
        reforma = new Reformaspoa();
        reforma.setAsignacion(asignacion);
        reforma.setFecha(vigente);
        partidasPoaBean.setPartidaPoa(null);
        proyectosPoaBean.setProyectoSeleccionado(null);
        partidasPoaBean.setCodigo(null);
        proyectosPoaBean.setCodigo(null);
        totales = new LinkedList<>();
        asignacionesPoaBean.setFuente("002");
        listaProyectosPac = new LinkedList();
        tipoReforma = null;
        proyectoSubactividad = null;
        codigo = null;
        proyectoCambia = "";
        proyectoFondo = null;
        asignacionFondo = null;
        direccionFondo = null;
        formulario.insertar();
//        listaProyectosNuevo = new LinkedList();
        return null;
    }

    public String modificarCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotales();
        formularioCabecera.editar();
        errores = new LinkedList<>();
        return null;
    }

    public String imprimirCabecera(Cabecerareformaspoa cabeceraReforma) {
        reportepdf = null;
        reportepdfPac = null;
        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
            Reformaspoa rtotal = new Reformaspoa();
            rtotal.setTotalSubactividad(0);
            rtotal.setTotalReforma(0);
            rtotal.setTotalTotal(0);
            for (Reformaspoa r : reformas) {
                r.setTotalSubactividad((r.getAsignacion() != null ? r.getAsignacion().getValor().doubleValue() : 0) + calculaTotalReformaspoa(r));
                r.setTotalReforma(r.getValor().doubleValue());
                r.setTotalTotal(r.getTotalSubactividad() + r.getTotalReforma());
                rtotal.setTotalSubactividad(rtotal.getTotalSubactividad() + r.getTotalSubactividad());
                rtotal.setTotalReforma(rtotal.getTotalReforma() + r.getTotalReforma());
                rtotal.setTotalTotal(rtotal.getTotalTotal() + r.getTotalTotal());
            }
            reformas.add(rtotal);
            calculaTotales();

            pdf = new DocumentoPDF(""
                    + "Reformaspoa Presupuesto año "
                    + getAnio(), seguridadbean.getLogueado().getUserid());

            pdf.agregaParrafo("\nNúmero de control: " + cabeceraReforma.getId(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Tipo: " + consultasPoa.traerTipoReforma(cabeceraReforma.getTipo()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafo("Fecha: " + sdf.format(cabeceraReforma.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Concepto: " + cabeceraReforma.getMotivo(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();

            List<Auxiliar> lista = proyectosPoaBean.getTitulos();
            for (Auxiliar a : lista) {
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
            }

            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "REQUERIMIENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "INCREMENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DECREMENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "TOTAL"));

            for (Reformaspoa r : reformas) {
                if (r.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, (r.getAsignacion() != null ? (proyectosPoaBean.dividir1(a, r.getAsignacion().getProyecto()))
                                : (r.getProyecto() != null ? (proyectosPoaBean.dividir1(a, r.getProyecto())) : ""))));
                    }

                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, (r.getAsignacion() != null ? r.getAsignacion().getProyecto().getNombre() : (r.getProyecto() != null ? r.getProyecto().getNombre() : ""))));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, (r.getAsignacion() != null ? r.getAsignacion().getProyecto().getCodigo() : (r.getProyecto() != null ? r.getProyecto().getCodigo() : ""))));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, (r.getAsignacion() != null ? r.getAsignacion().getPartida().getCodigo() : "")));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, (r.getRequerimiento())));

                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalSubactividad()));

                    if (r.getTotalReforma() > 0) {
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalReforma()));
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                    } else {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalReforma() * -1));
                    }

                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalTotal()));
                } else {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    }
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "TOTALES"));
                    for (AuxiliarCargaPoa ac : totales) {
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, ac.getIngresos().doubleValue()));
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, ac.getEgresos().doubleValue()));
                    }
                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, r.getTotalTotal()));
                }
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado"));
            pdf.agregarTabla(null, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);
            reportepdf = pdf.traerRecurso();

            //Si esq tiene reformas pac
            parametros = new HashMap();
            parametros.put(";where", "o.reforma=:reforma");
            parametros.put("reforma", cabeceraReforma);
            List<Trackingspoa> listaT = ejbTrackings.encontarParametros(parametros);
            if (!listaT.isEmpty()) {
                Trackingspoa t = listaT.get(0);
                if (t.getReformapac() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.id=:id");
                    parametros.put("id", t.getReformapac());
                    List<Trackingspoa> listaT2 = ejbTrackings.encontarParametros(parametros);
                    Trackingspoa t2 = listaT2.get(0);
                    if (t2.getReformapac() != null) {
                        int reformaPac = 0;
                        reformaPac = t2.getReformapac();
                        if (reformaPac != 0) {
                            parametros = new HashMap();
                            parametros.put(";where", "o.reformapac=:reformapac");
                            parametros.put("reformapac", reformaPac);
                            List<Proyectospoa> listaP = ejbProyectos.encontarParametros(parametros);
                            if (!listaP.isEmpty()) {
                                imprimirPac(listaP);
                            }
                        }
                    }
                }

            }
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.editar();
        return null;
    }

    public String modificar() {
        try {
            reforma = reformas.get(formulario.getFila().getRowIndex());
            if (reforma.getRequerimiento() != null && (!reforma.getRequerimiento().isEmpty())) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.nombre=:nombre");
                parametros.put("nombre", reforma.getRequerimiento());
                List<Codigos> lista = ejbCodigos.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    tipoReforma = lista.get(0);
                }

            }
            if (tipoReforma.getParametros().equals("2") || tipoReforma.getParametros().equals("3")) {
                MensajesErrores.advertencia("No se puede Modificar");
                return null;
            }
//        direccion = reforma.getAsignacion().getProyecto().getDireccion();
            formulario.setIndiceFila(formulario.getFila().getRowIndex());
            formularioClasificador.cancelar();
            asignacion = reforma.getAsignacion();
            partidasPoaBean.setPartidaPoa(asignacion.getPartida());
            partidasPoaBean.setCodigo(asignacion.getPartida().getCodigo());
            proyectosPoaBean.setProyectoSeleccionado(asignacion.getProyecto());
            formulario.editar();
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String eliminarCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible borrar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
//        imagenesPoaBean.setCabeceraReforma(cabeceraReforma);
//        imagenesPoaBean.buscarDocumentos();
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCabecera.eliminar();
        return null;
    }

    public String eliminar() {
        reforma = reformas.get(formulario.getFila().getRowIndex());
//        direccion = reforma.getAsignacion().getProyecto().getDireccion();
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        asignacion = reforma.getAsignacion();
        formulario.eliminar();
        return null;
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
            cabeceraReforma.setDirector(1);
            cabeceraReforma.setUtilizado(Boolean.FALSE);
            cabeceraReforma.setAnio(c.get(Calendar.YEAR));
            ejbCabeceras.create(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            if (!listaProyectosPac.isEmpty()) {
                Codigos numeracion = codigosBean.traerCodigo("NUM", "02");
                if (numeracion == null) {
                    MensajesErrores.advertencia("No existe numeración para Reformas PAC");
                    return null;
                }
                String numero = numeracion.getParametros();
                if ((numero == null) || (numero.isEmpty())) {
                    numero = "1";
                }
                int num = Integer.parseInt(numero);
                traking.setReformapac(num);
                traking.setAprobadopac(null);
                traking.setUtilizado(false);
                traking.setFecha(cabeceraReforma.getFecha());
                traking.setResponsable(seguridadbean.getLogueado().getUserid());
                ejbTrackings.create(traking, seguridadbean.getLogueado().getUserid());
                for (Proyectospoa p : getListaProyectos()) {
//                    p.setPac(Boolean.FALSE);
                    p.setActivo(Boolean.FALSE);
                    p.setReformapac(traking.getReformapac());
                    p.setFechacreacion(traking.getFecha());
                    p.setFechautilizacion(null);
                    ejbProyectos.create(p, seguridadbean.getLogueado().getUserid());
                }
                Integer nuevoNuemero = num + 1;
                numeracion.setParametros(nuevoNuemero + "");
                ejbCodigos.edit(numeracion, seguridadbean.getLogueado().getUserid());
            }
            for (Reformaspoa r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                r.setDocumento(cabeceraReforma.getDocumento());
                r.setAprobado(null);
                ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
            }

//            imagenesPoaBean.setCabeceraReforma(cabeceraReforma);
//            imagenesPoaBean.insertarDocumentos("reformas");
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setReformapac(traking.getId());
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma creada: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | org.errores.sfccbdmq.GrabarException ex) {
//        } catch (InsertarException | IOException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    public String grabarCabecera() {
        if (validar()) {
            return null;
        }
        try {
            if ((cabeceraReforma.getMotivo() == null) || (cabeceraReforma.getMotivo().isEmpty())) {
                MensajesErrores.advertencia("Ingrese una observación primero");
                return null;
            }
            cabeceraReforma.setDefinitivo(Boolean.FALSE);
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            for (Reformaspoa r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                if (r.getId() == null) {
                    ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbReformas.edit(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (reformasb != null) {
                for (Reformaspoa r : reformasb) {
                    if (r.getId() != null) {
                        ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma modificada: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    public String borrarCabecera() {
        try {
            for (Reformaspoa r : reformas) {
                if (r.getId() != null) {
                    ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                }

            }
            if (reformasb != null) {
                for (Reformaspoa r : reformasb) {
                    if (r.getId() != null) {
                        ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            borrarPac();
            ejbCabeceras.remove(cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    private boolean validar() {

        double ingreso = 0;
        double egreso = 0;
        for (AuxiliarCargaPoa a : totales) {
            ingreso += a.getIngresos().doubleValue();
            egreso += a.getEgresos().doubleValue();
        }

        double cuadre2 = Math.round(ingreso);
        double dividido2 = cuadre2 / 100;
        BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
        double ingreso1 = (valortotal2.doubleValue());

        double cuadre3 = Math.round(egreso);
        double dividido3 = cuadre3 / 100;
        BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
        double egreso2 = (valortotal3.doubleValue());

        if (ingreso1 - egreso2 != 0) {
            MensajesErrores.advertencia("No cuadrada los totales : ");
            return true;
        }

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
        if (cabeceraReforma.getFecha().before(vigente)) {
            MensajesErrores.advertencia("Fecha debe ser mayor o igual al periodo vigente");
            return true;
        }

        if (!(getErrores() == null || getErrores().isEmpty())) {
            MensajesErrores.advertencia("Existen errores");
            return false;
        }
        if (getListaProyectos() == null || getListaProyectos().isEmpty()) {
            MensajesErrores.advertencia("No existen registros para grabar");
            return false;
        }
        if (traking.getObservaciones() == null || traking.getObservaciones().isEmpty()) {
            MensajesErrores.advertencia("Falta observaciones");
            return false;
        }
        Codigos numeracion = codigosBean.traerCodigo("NUM", "02");
        if (numeracion == null) {
            MensajesErrores.advertencia("No existe numeración para Reformas PAC");
            return false;
        }

        return false;
    }

    public String insertar() {
        try {
            if (tipoReforma == null) {
                MensajesErrores.advertencia("Seleccione un requerimiento");
                return null;
            } else {
                reforma.setRequerimiento(tipoReforma.getNombre());
            }
            if (reforma.getValor() == null) {
                reforma.setValor(BigDecimal.ZERO);
            }
            // buscar Aisgnacion dependiendo del clasificador
            if ((tipoReforma.getParametros().equals("1")) || (tipoReforma.getParametros().equals("4")) || (tipoReforma.getParametros().equals("5"))) {
                if (partidasPoaBean.getPartidaPoa() == null) {
                    MensajesErrores.advertencia("Seleccione una partida");
                    return null;
                }
                if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                    MensajesErrores.advertencia("Seleccione un Programa");
                    return null;
                }

                if (!proyectosPoaBean.getProyectoSeleccionado().getIngreso().equals(partidasPoaBean.getPartidaPoa().getIngreso())) {
                    MensajesErrores.advertencia("La partida y el proyecto deben ser o sólo de ingresos o sólo de gastos");
                    return null;
                }
                if (asignacionesPoaBean.getFuente() == null) {
                    MensajesErrores.advertencia("Seleccione una Fuente");
                    return null;
                }

            }
            if (!errores.isEmpty()) {
                MensajesErrores.advertencia("Existen errores no se puede grabar");
                return null;
            }

            if ((tipoReforma.getParametros().equals("1")) || (tipoReforma.getParametros().equals("4")) || (tipoReforma.getParametros().equals("5"))) {
                // buscar partida en asignación
                Map parametros = new HashMap();
                parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
                parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
                parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
                parametros.put("fuente", asignacionesPoaBean.getFuente());
                asignacion = null;

                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
                }
                if (asignacion == null) {
                    asignacion = new Asignacionespoa();
//                asignacion.setDireccion(direccion);
                    asignacion.setFuente(asignacionesPoaBean.getFuente());
                    asignacion.setActivo(true);
                    asignacion.setPartida(partidasPoaBean.getPartidaPoa());
                    asignacion.setProyecto(proyectosPoaBean.getProyectoSeleccionado());
                    asignacion.setValor(BigDecimal.ZERO);
                    asignacion.setCerrado(Boolean.TRUE);
                    ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());
                }

                reforma.setAsignacion(asignacion);

                if (reforma.getAsignacion() == null) {
                    MensajesErrores.advertencia("Seleccione una partida");
                    return null;
                }
                if (reformas == null) {
                    reformas = new LinkedList<>();
                }
                double asignaciones = reforma.getAsignacion().getValor().doubleValue();
                double reformasTotal = calculaTotalReformaspoaAsignacion(reforma.getAsignacion());
                double certificacionesTotal = calculaTotalCertificacionesAsignacion(reforma.getAsignacion());
                double reformaactual = reforma.getValor().doubleValue();
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
                    MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                            + df.format(asignaciones) + " Reformaspoa Anteriores = "
                            + df.format(reformasTotal) + " Certificaciones : "
                            + df.format(certificacionesTotal));
                    return null;
                }
            }
            if ((tipoReforma.getParametros().equals("2")) && (tipoReforma.getParametros().equals("4"))) {
                reforma.setNombreproyecto(proyectoCambia);

            }
            if (tipoReforma.getParametros().equals("2")) {
                reforma.setProyecto(proyectoNombre);
                reforma.setNombreproyecto(proyectoCambia);

            }
            if (tipoReforma.getParametros().equals("3")) {
                reforma.setProyecto(proyectoSubactividadNuevo);

            }
            if (tipoReforma.getParametros().equals("6")) {
                double valorCertificacion = getValorCertificacion();
                if (valorCertificacion != 0) {
                    MensajesErrores.advertencia("Existe valor de Certificación, no se puede realizar el Requerimiento");
                    return null;
                }

                Map parametros = new HashMap();
                parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
                parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
                parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
                parametros.put("fuente", asignacionesPoaBean.getFuente());
                asignacion = null;

                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
                }
                if (asignacion == null) {
                    asignacion = new Asignacionespoa();
//                asignacion.setDireccion(direccion);
                    asignacion.setFuente(asignacionesPoaBean.getFuente());
                    asignacion.setActivo(true);
                    asignacion.setPartida(partidasPoaBean.getPartidaPoa());
                    asignacion.setProyecto(proyectosPoaBean.getProyectoSeleccionado());
                    asignacion.setValor(BigDecimal.ZERO);
                    asignacion.setCerrado(Boolean.TRUE);
                    ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());
                }

                reforma.setAsignacion(asignacion);

                if (reforma.getAsignacion() == null) {
                    MensajesErrores.advertencia("Seleccione una partida");
                    return null;
                }

                double saldo = getSaldoActual();
                BigDecimal saldo2 = new BigDecimal(saldo * -1);
                reforma.setValor(saldo2);

                DecimalFormat dfact = new DecimalFormat("000");
                parametros = new HashMap();
                parametros.put(";where", "o.codigo like :codigo and o.anio=:anio");
                parametros.put("codigo", proyectosPoaBean.getProyectoSeleccionado().getCodigo().substring(0, 7) + "%");
                parametros.put("anio", anio);
                parametros.put(";orden", "o.codigo desc");
                List<Proyectospoa> lista = ejbProyectos.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Proyectospoa p = lista.get(0);
                    String numero = p.getCodigo().substring(7, 10);
                    int numeroInt = Integer.parseInt(numero);
                    int nummeroMostrar = numeroInt + 1;

                    Proyectospoa pNuevo = new Proyectospoa();

                    pNuevo.setCodigo(proyectosPoaBean.getProyectoSeleccionado().getCodigo().substring(0, 7) + dfact.format(nummeroMostrar));
                    pNuevo.setNombre(proyectoCambia);
                    pNuevo.setAnio(anio);
                    pNuevo.setNivel(4);
                    pNuevo.setPadre(p.getPadre());
                    pNuevo.setActivo(Boolean.TRUE);
                    pNuevo.setDefinitivo(Boolean.FALSE);
                    pNuevo.setImputable(Boolean.TRUE);
                    pNuevo.setDireccion(direccion);
                    pNuevo.setIngreso(Boolean.FALSE);
                    pNuevo.setPac(proyectosPoaBean.getProyectoSeleccionado().getPac());
                    ejbProyectospoa.create(pNuevo, seguridadbean.getLogueado().getUserid());

                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", pNuevo.getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> lista2 = ejbProyectosFinanciera.encontarParametros(parametros);
                    if (lista2.isEmpty()) {
                        Proyectos pNuevoF = new Proyectos();
                        pNuevoF.setAnio(anio);
                        pNuevoF.setCodigo(pNuevo.getCodigo());
                        pNuevoF.setNombre(pNuevo.getNombre());
                        pNuevoF.setTipo("SUBA");
                        pNuevoF.setIngreso(Boolean.FALSE);
                        pNuevoF.setImputable(Boolean.TRUE);
                        ejbProyectosFinanciera.create(pNuevoF, seguridadbean.getLogueado().getUserid());
                    } else {
                        Proyectos pNuevoF = lista2.get(0);
                        pNuevoF.setAnio(anio);
                        pNuevoF.setNombre(pNuevo.getNombre());
                        pNuevoF.setImputable(Boolean.TRUE);
                        ejbProyectosFinanciera.edit(pNuevoF, seguridadbean.getLogueado().getUserid());
                    }
                    Asignacionespoa asig = new Asignacionespoa();
                    asig.setFuente(asignacionesPoaBean.getFuente());
                    asig.setActivo(true);
                    asig.setPartida(partidaPoa);
                    asig.setProyecto(pNuevo);
                    asig.setValor(BigDecimal.ZERO);
                    asig.setCerrado(Boolean.TRUE);
                    ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());

                    Reformaspoa r = new Reformaspoa();
                    r.setValor(new BigDecimal(saldo));
                    r.setAsignacion(asig);
                    r.setRequerimiento(reforma.getRequerimiento());
                    reformas.add(r);
                }
            }
            if (tipoReforma.getParametros().equals("7")) {
                if (direccionFondo == null || direccionFondo.isEmpty()) {
                    MensajesErrores.advertencia("Seleccione una Dirección");
                    return null;
                }
                if (proyectoFondo == null || proyectoFondo.isEmpty()) {
                    MensajesErrores.advertencia("Seleccione una Subactividad");
                    return null;
                }
                if (getAsignacionFondo() == null) {
                    MensajesErrores.advertencia("Seleccione una Partida");
                    return null;
                }
                if (reforma.getValor().doubleValue() > 0) {
                    MensajesErrores.advertencia("El valor debe ser negativo");
                    return null;
                }
                reforma.setAsignacion(asignacionFondo);
                double asignaciones = reforma.getAsignacion().getValor().doubleValue();
                double reformasTotal = calculaTotalReformaspoaAsignacion(reforma.getAsignacion());
                double certificacionesTotal = calculaTotalCertificacionesAsignacion(reforma.getAsignacion());
                double reformaactual = reforma.getValor().doubleValue();
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
                    MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                            + df.format(asignaciones) + " Reformaspoa Anteriores = "
                            + df.format(reformasTotal) + " Certificaciones : "
                            + df.format(certificacionesTotal));
                    return null;
                }

            }
            reformas.add(reforma);
            formulario.cancelar();
            formularioClasificador.cancelar();
            calculaTotales();
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {
        reformas.set(formulario.getIndiceFila(), reforma);
        if (tipoReforma == null) {
            MensajesErrores.advertencia("Seleccione un requerimiento");
            return null;
        } else {
            reforma.setRequerimiento(tipoReforma.getNombre());
        }
        double asignaciones = reforma.getAsignacion().getValor().doubleValue();
        double reformasTotal = calculaTotalReformaspoaAsignacion(reforma.getAsignacion());
        double certificacionesTotal = calculaTotalCertificacionesAsignacion(reforma.getAsignacion());
        double reformaactual = reforma.getValor().doubleValue();
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
            MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                    + df.format(asignaciones) + " Reformaspoa Anteriores = " + df.format(reformasTotal) + " Certificaciones : " + df.format(certificacionesTotal));
            return null;
        }
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
        return null;
    }

    public String borrar() {
        if (reformasb == null) {
            reformasb = new LinkedList<>();
        }
        reformas.remove(formulario.getIndiceFila());
        reformasb.add(reforma);
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
        return null;
    }

    private void calculaTotales() {
        double totalIngreso = 0;
        double totalEgreso = 0;
        totales = new LinkedList<>();
        List<Codigos> fuentes = codigosBean.traerCodigoMaestro("FUENFIN");
        for (Codigos c : fuentes) {
            AuxiliarCargaPoa auxiliarCarga = new AuxiliarCargaPoa();
            auxiliarCarga.setFuente(c);
            auxiliarCarga.setIngresos(new BigDecimal(0));
            auxiliarCarga.setEgresos(new BigDecimal(0));
            totales.add(auxiliarCarga);
        }

        for (Reformaspoa r : reformas) {
            Asignacionespoa a = r.getAsignacion();
            for (AuxiliarCargaPoa auxCarga : totales) {
                if (r.getAsignacion() != null) {
                    if (auxCarga.getFuente().getCodigo().equals(r.getAsignacion().getFuente())) {
                        if (a.getPartida().getIngreso()) {
                            if (r.getValor().doubleValue() > 0) {
                                // Es un egreso
                                auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
                                totalIngreso += r.getValor().doubleValue();
                            } else {
                                // es un ingreso
                                auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue() * -1));
                                totalEgreso += r.getValor().doubleValue() * -1;

                            }
                        } else { // se trata de un egreso
                            if (r.getValor().doubleValue() > 0) {
                                // es un egrso
                                auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
                                totalEgreso += r.getValor().doubleValue();
                            } else {
                                // es un ingreso
                                auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue() * -1));
                                totalIngreso += r.getValor().doubleValue() * -1;
                            }
                        } // fin if de ingreso y egreso
                    } // fin if fuente
                }
            } // fin for auxCarga
        }// fin for reforma
        AuxiliarCargaPoa aux = new AuxiliarCargaPoa();
        aux.setFuente(null);
        aux.setIngresos(new BigDecimal(totalIngreso));
        aux.setEgresos(new BigDecimal(totalEgreso));
        getTotales().add(aux);
    }

    public String cancelar() {
        formularioCabecera.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String salir() {
        formularioCabecera.cancelar();
        formularioLinea.cancelar();
        formularioImprimir.cancelar();
        cabeceraReforma = new Cabecerareformaspoa();
        cabecerasReformaspoa = new LinkedList<>();
        return null;
    }

    public double getTotalReformaspoa() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());
        return calculaTotalReformaspoaAsignacion(r.getAsignacion());
    }

    public double getTotalReformaspoaImp() {
        Reformaspoa r = reformas.get(formularioImprimir.getFila().getRowIndex());
        return calculaTotalReformaspoa(r);
    }

    public double getSaldoActual() {
        double asiganacionLocal = getValorAsignaciones();
        double reformaLocal = getTotalReformaspoaAisgnacion();
        double certificacion = getValorCertificacion();
        return asiganacionLocal + reformaLocal - certificacion;
    }

    public double getTotalReformaspoaAisgnacion() {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.activo=true");
            parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
            try {
                asignacion = null;
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
                }
                if (asignacion == null) {
                    return 0;
                }
                return calculaTotalReformaspoaAsignacion(asignacion);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getValorAsignaciones() {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.proyecto=:actividad and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("partida", partidasPoaBean.getPartidaPoa());
            parametros.put("direccion", direccion);
            parametros.put("actividad", proyectosPoaBean.getProyectoSeleccionado());
            try {
                asignacion = null;
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
                    return a.getValor().doubleValue();
                }
                if (asignacion == null) {
                    return 0;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }

    public double getValorCertificacion() {
//        if ((asignacion == null)) {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            // buscar asignacion

            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
            parametros.put("direccion", direccion);
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
            try {
                asignacion = null;
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
//                        return a.getValor().doubleValue();
                }
                if (asignacion == null) {
                    return 0;
                }
                // calcula el total de certificaion
                double retorno = calculaTotalCertificacionesAsignacion(asignacion);
                return retorno;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            } else {
//            } else {
//            } else {
//            } else {

        }
//        }
        return 0;
//        return calculaTotalCertificacionesAsignacion(asignacion);
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
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
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
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());
        return calculaTotalCertificacionesAsignacion(r.getAsignacion());
    }

    public double getTotalCertificacionesImp() {
        Reformaspoa r = reformas.get(formularioImprimir.getFila().getRowIndex());
        return calculaTotalCertificaciones(r);
    }

    private double calculaTotalCertificaciones(Reformaspoa r) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.adjudicado");
            parametros.put("asignacion", r.getAsignacion());
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.anulado=false and o.certificacion.impreso=true and o.adjudicado is not null");
            double totalAdjudicado = ejbCertificaciones.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("asignacion", r.getAsignacion());
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.anulado=false and o.certificacion.impreso=true and o.adjudicado is null");

            double totalCertificado = ejbCertificaciones.sumarCampo(parametros).doubleValue();

            return totalCertificado + totalAdjudicado;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        setAnio(cAnio.get(Calendar.YEAR));
        reformas = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();

                    BufferedReader entrada = null;
                    try {

                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));

                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        reformasb = reformas;
                        reformas = new LinkedList<>();
                        totales = new LinkedList<>();
                        List<Codigos> fuentes = codigosBean.traerCodigoMaestro("FUENFIN");
                        for (Codigos c : fuentes) {
                            AuxiliarCargaPoa auxiliarCarga = new AuxiliarCargaPoa();
                            auxiliarCarga.setFuente(c);
                            auxiliarCarga.setIngresos(new BigDecimal(0));
                            auxiliarCarga.setEgresos(new BigDecimal(0));
                            totales.add(auxiliarCarga);
                        }
                        double totalIngreso = 0;
                        double totalEgreso = 0;
                        setErrores(new LinkedList());
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
//                            Asignaciones a = new Asignaciones();
                            Reformaspoa r = new Reformaspoa();
                            Asignacionespoa a = new Asignacionespoa();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(a, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            registro++;
                            // ver si esta ben el registro o es error 
                            if (a.getFuente() == null) {
                                getErrores().add("Fuente no válida en registro: " + String.valueOf(registro));
                            } else if (a.getPartida() == null) {
                                getErrores().add("Clasificador no válido en registro: " + String.valueOf(registro));
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                                parametros.put("anio", getAnio());
                                //parametros.put("codigo", a.getProyecto().getCodigo());
                                parametros.put("codigo", a.getProyectoStr());
                                List<Proyectospoa> lp = ejbProyectos.encontarParametros(parametros);
                                Proyectospoa pr = null;

                                //CONTROL PARA IDENTIFICAR PROYECTOS PADRES Y CREAR EN CASO DE QUE NO EXISTAN ES-2018-03-21
                                //VALIDACION PARA PROYECTOS QUE NO EXISTEN 
                                if (lp.isEmpty()) {
                                    buscarPadreProyecto(a);
                                    if (proyectoPadre.getId() != null) {
                                        proyectosDirPoaBean.crear(proyectoPadre);
                                        proyectosDirPoaBean.getProyecto().setCodigo(codigoHijo);
                                        proyectosDirPoaBean.getProyecto().setNombre(a.getProyectoNom());
                                        proyectosDirPoaBean.getProyecto().setDireccion(proyectoPadre.getDireccion());
                                        proyectosDirPoaBean.insertar();
                                        if (proyectosDirPoaBean.getProyecto().getId() != null) {
                                            pr = proyectosDirPoaBean.getProyecto();
                                        } else {
                                            pr = null;
                                        }
                                    } else {
                                        pr = null;
                                    }
                                } else {
                                    for (Proyectospoa p : lp) {
                                        pr = p;
                                    }
                                }

                                if (pr == null) {
                                    getErrores().add("Proyecto no válido en registro: " + String.valueOf(registro) + " con proyecto " + a.getProyectoStr());
                                } else {
                                    // buscar la asignación mayores q cero
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente and o.partida=:partida and o.activo=true and o.valor>0");
                                    parametros.put("proyecto", pr);
                                    parametros.put("fuente", a.getFuente());
                                    parametros.put("partida", a.getPartida());
                                    List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);

                                    Asignacionespoa asig = null;
                                    for (Asignacionespoa ast : la) {
                                        asig = ast;
                                    }
                                    if (asig == null) {
                                        parametros = new HashMap();
                                        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente and o.partida=:partida and o.activo=true");
                                        parametros.put("proyecto", pr);
                                        parametros.put("fuente", a.getFuente());
                                        parametros.put("partida", a.getPartida());
                                        List<Asignacionespoa> la2 = ejbAsignaciones.encontarParametros(parametros);
                                        for (Asignacionespoa ast2 : la2) {
                                            asig = ast2;
                                        }
                                        if (asig == null) {
//                                        if (a.getValor().doubleValue() > 0) {
                                            asig = new Asignacionespoa();
                                            asig.setActivo(true);
                                            asig.setCerrado(true);
                                            asig.setFuente(a.getFuente());
                                            asig.setProyecto(pr);
                                            asig.setPartida(a.getPartida());
                                            asig.setValor(BigDecimal.ZERO);
                                            ejbAsignaciones.create(asig, seguridadbean.getLogueado().getUserid());
                                        }

//                                        } else {
////                                            getErrores().add("No existe partida para descontar: " + a.getPartida().getCodigo() + " Producto: " + a.getProyecto().toString());
//                                            if (a.getPartida() != null && a.getProyecto() != null) {
//                                                getErrores().add("No existe partida para descontar: " + a.getPartida().getCodigo() + " Producto: " + a.getProyecto().toStringNombre());
//                                            }
//                                        }
                                    }
//                                    } else {
                                    // ver el total
                                    if (a.getValor().doubleValue() == 0) {
                                        proyectosDirPoaBean.modificarNombre(pr);
                                        proyectosDirPoaBean.getProyecto().setNombre(a.getProyectoNom());
                                        proyectosDirPoaBean.grabarSoloPoa();
                                    }
                                    if (asig == null) {
                                        MensajesErrores.advertencia("FALTAL no existe asignación carga no continua");
                                        return;
                                    }
                                    Reformaspoa reformaSubir = new Reformaspoa();
                                    reformaSubir.setAsignacion(asig);
                                    reformaSubir.setFecha(new Date());
                                    reformaSubir.setValor(a.getValor());
                                    reformaSubir.setRequerimiento(a.getRequerimiento());
                                    reformaSubir.setDocumento(a.getDocumento());
                                    reformas.add(reformaSubir);
                                    for (AuxiliarCargaPoa auxCarga : totales) {
                                        if (a.getFuente().equals(auxCarga.getFuente().getCodigo())) {
                                            if (a.getValor().doubleValue() > 0) {
                                                auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + a.getValor().doubleValue()));
                                                totalIngreso += a.getValor().doubleValue();
                                            } else {
                                                auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + a.getValor().doubleValue() * -1));
                                                totalEgreso += a.getValor().doubleValue() * -1;
                                            }
                                        } // fin if fuente
//                                        } // fin for auxCarga
                                    } // fin if proyecto

                                }
                            }// fin else fuente

                        }//fin while
                        AuxiliarCargaPoa aux = new AuxiliarCargaPoa();
                        aux.setFuente(null);
                        aux.setIngresos(new BigDecimal(totalIngreso));
                        aux.setEgresos(new BigDecimal(totalEgreso));
                        getTotales().add(aux);
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);

                    } catch (IOException | InsertarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }

    }

    private void ubicar(Asignacionespoa am, String titulo, String valor) {

        if (titulo.contains("partida")) {
            // buscar el clasificador
            am.setPartida(partidasPoaBean.traerCodigo(valor));
        } else if (titulo.contains("proyecto")) {
            //am.setProyecto(proyectosPoaBean.traerCodigo(valor));
            am.setProyectoStr(valor);
        } else if (titulo.contains("fuente")) {
            am.setFuente(valor);
        } else if (titulo.contains("valor")) {
            am.setValor(new BigDecimal(valor));
        } else if (titulo.contains("nombre")) {
            am.setProyectoNom(valor);
            //am.getProyecto().setNombre(valor);
        } else if (titulo.contains("anio")) {
            am.setAnio(Integer.valueOf(valor));

        } else if (titulo.contains("requerimiento")) {
            am.setRequerimiento(valor);
        } else if (titulo.contains("documento")) {
            am.setDocumento(valor);
        }
    }

    private void buscarPadreProyecto(Asignacionespoa ap) throws ConsultarException {
        proyectoPadre = new Proyectospoa();
        codigoHijo = "";
        String codInicial = ap.getProyectoStr();
        if (codInicial.length() > nivelProyecto) {
            String codPadre = codInicial.substring(0, nivelProyecto);
            codigoHijo = codInicial.substring(nivelProyecto, ap.getProyectoStr().length());
            if (codPadre != null) {
                Map parametros = new HashMap();
                String where = "  o.anio=:anio and o.activo=true and o.codigo=:codigo";
                parametros.put("anio", getAnio());
                parametros.put("codigo", codPadre);
                parametros.put(";where", where);
                List<Proyectospoa> aux = ejbProyectos.encontarParametros(parametros);
                if (!aux.isEmpty()) {
                    proyectoPadre = aux.get(0);
                }
            }
        }
        return;
    }

    //PAC
    public String insertarPac() {

//        imprimirPac(getTraking());
        return null;
    }

    public String grabarCertificacionPAC() {
        try {
            listaProyectosPac.add(getProyectoSeleccionado());
            Map parametros = new HashMap();
            parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
            parametros.put("anio", getAnio());
            parametros.put("codigo", getProyectoSeleccionado().getCodigo());
            List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
            Proyectospoa pre = null;
            for (Proyectospoa pp : lpe) {
                pre = pp;
            }
            if (pre != null) {
//                getListaProyectosNuevo().add(pre);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioLinea().cancelar();
        return null;
    }

    public String nuevo(Proyectospoa p) {
        proyectoSeleccionado = new Proyectospoa();
        proyectoSeleccionado = p;
        getFormularioLinea().insertar();
        return null;
    }

    public String imprimirPac(List<Proyectospoa> lista) {
        // buscar reformas
        try {
            Proyectospoa pr = lista.get(0);

            pdf = new DocumentoPDF(""
                    + "Reformas PAC Presupuesto año "
                    + getAnio(), seguridadbean.getLogueado().getUserid());

            pdf.agregaParrafo("\nNúmero de control: " + pr.getReformapac(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafo("Fecha: " + sdf.format(pr.getFechacreacion()), AuxiliarReporte.ALIGN_LEFT, 11, false);
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

            for (Proyectospoa p : lista) {
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
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, getConsultasPoaBean().traerTipoCompra(p.getTipocompra())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, part != null ? part : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCpc() != null ? p.getCpc() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, df.format(p.getValoriva())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCuatrimestre1() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCuatrimestre2() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getCuatrimestre3() ? "S" : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, getConsultasPoaBean().traerTipoProcedimiento(p.getProcedimientocontratacion())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, getConsultasPoaBean().traerTipoProducto(p.getTipoproducto())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, getConsultasPoaBean().traerTipoRegimen(p.getRegimen())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getModificacion() != null ? p.getModificacion() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getResponsable() != null ? p.getResponsable() : ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getDocumento() != null ? p.getDocumento() : ""));

                }
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado"));
            pdf.agregarTabla(null, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);

            reportepdfPac = pdf.traerRecurso();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.editar();
        buscar();
        return null;
    }

    public String eliminarPac(Trackingspoa tr) {
        setListaProyectos(new LinkedList<>());
        setTraking(tr);
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
                    getListaProyectos().add(pr);
                }

            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrarPac() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.reforma=:reforma");
            parametros.put("reforma", cabeceraReforma);
            List<Trackingspoa> listaTR = ejbTrackings.encontarParametros(parametros);
            if (!listaTR.isEmpty()) {
                Trackingspoa tr = listaTR.get(0);
                if (tr.getReformapac() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.id=:id");
                    parametros.put("id", tr.getReformapac());
                    List<Trackingspoa> lista = ejbTrackings.encontarParametros(parametros);
                    Trackingspoa t2 = lista.get(0);
                    if (t2.getReformapac() != null) {
                        int reformaPac = 0;
                        reformaPac = t2.getReformapac();
                        if (reformaPac != 0) {
                            parametros = new HashMap();
                            parametros.put(";where", "o.reformapac=:reformapac");
                            parametros.put("reformapac", reformaPac);
                            List<Proyectospoa> listaP = ejbProyectos.encontarParametros(parametros);
                            if (!listaP.isEmpty()) {
                                for (Proyectospoa p : listaP) {
                                    ejbProyectospoa.remove(p, seguridadbean.getLogueado().getUserid());
                                }
                            }
                        }

                    }

                    for (Trackingspoa tBorrar : lista) {
                        ejbTrackings.remove(tBorrar, seguridadbean.getLogueado().getUserid());
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.id=:id");
                    parametros.put("id", tr.getId());
                    List<Trackingspoa> lista2 = ejbTrackings.encontarParametros(parametros);
                    for (Trackingspoa tBorrar : lista2) {
                        ejbTrackings.remove(tBorrar, seguridadbean.getLogueado().getUserid());
                    }

                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BorrarException ex) {
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerPartidaProyecto(Proyectospoa pro) {
        String asig = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto=:proyecto");
        parametros.put("proyecto", pro);
        try {
            List<Asignacionespoa> li = ejbAsignaciones.encontarParametros(parametros);
            for (Asignacionespoa a : li) {
                asig += a.getPartida().getCodigo() + " / ";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return asig;
    }

    public void archivoListenerPac(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        setAnio(cAnio.get(Calendar.YEAR));
        setListaProyectos(new LinkedList<>());
//        setListaProyectosNuevo(new LinkedList<>());
//        setListaProyectosNoModificados(new LinkedList<>());
        erroresPac = new LinkedList();
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
                                    ubicarPac(pu, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            // ver si esta ben el registro o es error 
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                            parametros.put("anio", getAnio());
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
                                getErroresPac().add("Proyecto no válido en registro: " + String.valueOf(registro) + " - " + pu.getCodigo());
                            }
                            if (pr != null) {
                                setEsReforma(false);
                                //Comparar los proyectos si son diferentes se hace la reforma sino no se hace nada 
                                estaIgualPac(pu, pr);
                                if (isEsReforma()) {

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
                                            getErroresPac().add("CPC no válido en registro: " + String.valueOf(registro) + " - " + pu.getCpc());
                                        } else {
                                            pr.setCpc(cCpc.getCodigo());
                                        }
                                    }
                                    if (!(pu.getPartidaStr() == null || pu.getPartidaStr().isEmpty())) {
                                        parametros = new HashMap();
                                        parametros.put(";where", "o.partida.codigo=:partida and o.proyecto.codigo=:proyecto");
                                        parametros.put("partida", pu.getPartidaStr());
                                        parametros.put("proyecto", pu.getCodigo());
                                        List<Asignacionespoa> lasig = ejbAsignaciones.encontarParametros(parametros);
                                        if (lasig.isEmpty()) {
                                            getErroresPac().add("Partida sin asignación : " + String.valueOf(registro) + " - " + pu.getPartidaStr());
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
                                            getErroresPac().add("Tipo de Compra no válido en registro: " + String.valueOf(registro) + " - " + pu.getTipocompra());
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
                                            getErroresPac().add("Tipo de Producto no válido en registro: " + String.valueOf(registro) + " - " + pu.getTipoproducto());
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
                                            getErroresPac().add("Tipo de Procedimiento no válido en registro: " + String.valueOf(registro) + " - " + pu.getProcedimientocontratacion());
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
                                            getErroresPac().add("Régimen no válido en registro: " + String.valueOf(registro) + " - " + pu.getRegimen());
                                        } else {
                                            pr.setRegimen(cRegimen.getCodigo());
                                        }
                                    }
                                    getListaProyectos().add(pr);
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                                    parametros.put("anio", getAnio());
                                    parametros.put("codigo", pu.getCodigo());
                                    List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
                                    Proyectospoa pre = null;
                                    for (Proyectospoa pp : lpe) {
                                        pre = pp;
                                    }
                                    if (pre != null) {
//                                        getListaProyectosNuevo().add(pre);
                                    }
                                    registro++;
                                } else {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.anio=:anio and o.codigo=:codigo and o.activo=true");
                                    parametros.put("anio", getAnio());
                                    parametros.put("codigo", pu.getCodigo());
                                    List<Proyectospoa> lpe = ejbProyectos.encontarParametros(parametros);
                                    Proyectospoa pre = null;
                                    for (Proyectospoa pp : lpe) {
                                        pre = pp;
                                    }
                                    if (pre != null) {
//                                        getListaProyectosNoModificados().add(pre);
                                    }
                                }
                            }
                        }//fin while
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ReformasPacBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: " + i.getStatus().getFacesMessage(FacesContext.getCurrentInstance(), fe, i).getSummary());
            }
        }
    }

    private void ubicarPac(Proyectospoa p, String titulo, String valor) {
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
            if ("S".equals(valor)) {
                p.setCuatrimestre1(Boolean.TRUE);
            } else {
                p.setCuatrimestre1(null);
            }
        } else if (titulo.contains("cuatrimestre2")) {
            if ("S".equals(valor)) {
                p.setCuatrimestre2(Boolean.TRUE);
            } else {
                p.setCuatrimestre2(null);
            }
        } else if (titulo.contains("cuatrimestre3")) {
            if ("S".equals(valor)) {
                p.setCuatrimestre3(Boolean.TRUE);
            } else {
                p.setCuatrimestre3(null);
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
        }
    }

    private void estaIgualPac(Proyectospoa nuevo, Proyectospoa anterior) {
        if (nuevo.getCpc() != null && anterior.getCpc() != null) {
            if (!nuevo.getCpc().equals(anterior.getCpc())) {
                setEsReforma(true);
                return;
            }
        } else {
            setEsReforma(true);
            return;
        }
        if (nuevo.getTipocompra() != null && anterior.getTipocompra() != null) {
            if (!nuevo.getTipocompra().equals(consultasPoaBean.traerTipoCompra(anterior.getTipocompra()))) {
                setEsReforma(true);
                return;
            }
        } else {
            setEsReforma(true);
            return;
        }
        if (nuevo.getValoriva() != null && anterior.getValoriva() != null) {
            if (!nuevo.getValoriva().equals(anterior.getValoriva())) {
                setEsReforma(true);
                return;
            }
        } else {
            setEsReforma(true);
            return;
        }
        if (nuevo.getCuatrimestre1() != null) {
            if (nuevo.getCuatrimestre1() != null && anterior.getCuatrimestre1() != null) {
                if (!nuevo.getCuatrimestre1().equals(anterior.getCuatrimestre1())) {
                    setEsReforma(true);
                    return;
                }
            } else {
                setEsReforma(true);
                return;
            }
        }
        if (nuevo.getCuatrimestre2() != null) {
            if (nuevo.getCuatrimestre2() != null && anterior.getCuatrimestre2() != null) {
                if (!nuevo.getCuatrimestre2().equals(anterior.getCuatrimestre2())) {
                    setEsReforma(true);
                    return;
                }
            } else {
                setEsReforma(true);
                return;
            }
        }
        if (nuevo.getCuatrimestre3() != null) {
            if (nuevo.getCuatrimestre3() != null && anterior.getCuatrimestre3() != null) {
                if (!nuevo.getCuatrimestre3().equals(anterior.getCuatrimestre3())) {
                    setEsReforma(true);
                    return;
                }
            } else {
                setEsReforma(true);
                return;
            }
        }
        if (nuevo.getTipoproducto() != null && anterior.getTipoproducto() != null) {
            if (!nuevo.getTipoproducto().equals(consultasPoaBean.traerTipoProducto(anterior.getTipoproducto()))) {
                setEsReforma(true);
                return;
            }
        } else {
            setEsReforma(true);
            return;
        }
        if (nuevo.getProcedimientocontratacion() != null && anterior.getProcedimientocontratacion() != null) {
            if (!nuevo.getProcedimientocontratacion().equals(consultasPoaBean.traerTipoProcedimiento(anterior.getProcedimientocontratacion()))) {
                setEsReforma(true);
                return;
            }
        } else {
            setEsReforma(true);
            return;
        }
        if (nuevo.getRegimen() != null && anterior.getRegimen() != null) {
            if (!nuevo.getRegimen().equals(consultasPoaBean.traerTipoRegimen(anterior.getRegimen()))) {
                setEsReforma(true);
                return;
            }
        } else {
            setEsReforma(true);
            return;
        }
    }

    public boolean valorTotalProyectoPac() {
        try {
            if (getProyectoSeleccionado() != null) {
                double valorReformas = 0;
                Map parametros = new HashMap();
                parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.asignacion.proyecto.anio=:anio");
                parametros.put("proyecto", getProyectoSeleccionado());
                parametros.put("anio", getAnio());
                parametros.put(";campo", "o.valor");
                valorReformas += ejbReformas.sumarCampo(parametros).doubleValue();

                setValorTotal(valorReformas / 1.12);
                return true;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ProyectosPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String nuevaSubactividad() {
        try {
            if (proyectoSubactividad == null) {
                MensajesErrores.advertencia("Seleccione una actividad primero");
                return null;
            }
            proyectoSubactividadNuevo = new Proyectospoa();
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo like :codigo and o.anio=:anio");
            parametros.put("codigo", proyectoSubactividad.getCodigo() + "%");
            parametros.put("anio", anio);
            parametros.put(";orden", "o.codigo desc");
            List<Proyectospoa> lista = ejbProyectos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Proyectospoa p = lista.get(0);
                String numero = p.getCodigo().substring(7, 10);
                int numeroInt = Integer.parseInt(numero);
                int nummeroMostrar = numeroInt + 1;
                DecimalFormat dfact = new DecimalFormat("000");
                proyectoSubactividadNuevo.setCodigo(dfact.format(nummeroMostrar));
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioSubactividad.insertar();
        return null;
    }

    public String insertarSubactividad() {
        if (proyectoSubactividadNuevo.getCodigo().length() < 3) {
            MensajesErrores.advertencia("Código debe tener 3 carcateres");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.activo=true and o.anio=:anio");
            parametros.put("codigo", proyectoSubactividad.getCodigo() + proyectoSubactividadNuevo.getCodigo());
            parametros.put("anio", anio);
            List<Proyectospoa> listaP = ejbProyectos.encontarParametros(parametros);
            if (!listaP.isEmpty()) {
                MensajesErrores.advertencia("Proyecto ya existe");
                return null;
            } else {
                proyectoSubactividadNuevo.setCodigo(proyectoSubactividad.getCodigo() + proyectoSubactividadNuevo.getCodigo());
                proyectoSubactividadNuevo.setAnio(anio);
                proyectoSubactividadNuevo.setNivel(4);
                proyectoSubactividadNuevo.setPadre(proyectoSubactividad);
                proyectoSubactividadNuevo.setActivo(Boolean.TRUE);
                proyectoSubactividadNuevo.setDefinitivo(Boolean.FALSE);
                proyectoSubactividadNuevo.setImputable(Boolean.TRUE);
                proyectoSubactividadNuevo.setDireccion(direccion);
                proyectoSubactividadNuevo.setIngreso(Boolean.FALSE);
                proyectoSubactividadNuevo.setPac(Boolean.FALSE);
                ejbProyectospoa.create(proyectoSubactividadNuevo, seguridadbean.getLogueado().getUserid());

                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                parametros.put("codigo", proyectoSubactividadNuevo.getCodigo());
                parametros.put("anio", anio);
                List<Proyectos> lista2 = ejbProyectosFinanciera.encontarParametros(parametros);
                if (lista2.isEmpty()) {
                    Proyectos pNuevo = new Proyectos();
                    pNuevo.setAnio(anio);
                    pNuevo.setCodigo(proyectoSubactividadNuevo.getCodigo());
                    pNuevo.setNombre(proyectoSubactividadNuevo.getNombre());
                    pNuevo.setTipo("SUBA");
                    pNuevo.setIngreso(proyectoSubactividadNuevo.getIngreso());
                    pNuevo.setImputable(Boolean.TRUE);
                    ejbProyectosFinanciera.create(pNuevo, seguridadbean.getLogueado().getUserid());
                } else {
                    Proyectos pNuevo = lista2.get(0);
                    pNuevo.setAnio(anio);
                    pNuevo.setNombre(proyectoSubactividadNuevo.getNombre());
                    pNuevo.setImputable(Boolean.TRUE);
                    ejbProyectosFinanciera.edit(pNuevo, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioSubactividad.cancelar();
        return null;
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        proyectoSubactividad = null;
        if (listaProyecto == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Proyectospoa p : listaProyecto) {
            String aComparar = p.getCodigo();
            if (aComparar.replaceAll("\\s", "").compareToIgnoreCase(newWord.replaceAll("\\s", "")) == 0) {
                proyectoSubactividad = p;
                codigo = p.getCodigo();
            }
        }
    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        proyectoSubactividad = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            proyectoSubactividad = null;
            return;
        }
        // traer la consulta
        Map parametros = new HashMap();
        String where = "  o.imputable=false and o.activo=true and o.anio=:anio and length(o.codigo)=7";
        parametros.put("anio", anio);
        where += " and  upper(o.codigo) like :codigo";
        parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
//        if (direccion != null) {
//            where += " and upper(o.direccion)=:direccion";
//            parametros.put("direccion", direccion);
//        }
        parametros.put(";orden", " o.codigo");

        listaProyecto = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        parametros.put(";where", where);
        try {
            listaProyecto = ejbProyectospoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaCodigoNombre(ValueChangeEvent event) {
        proyectoNombre = null;
        if (listaProyectoNombre == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Proyectospoa p : listaProyectoNombre) {
            String aComparar = p.getCodigo();
            if (aComparar.replaceAll("\\s", "").compareToIgnoreCase(newWord.replaceAll("\\s", "")) == 0) {
                proyectoNombre = p;
                codigoNombre = p.getCodigo();
            }
        }
    }

    public void codigoChangeEventHandlerNombre(TextChangeEvent event) {
        proyectoNombre = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            proyectoNombre = null;
            return;
        }
        // traer la consulta
        Map parametros = new HashMap();
        String where = "  o.imputable=true and o.activo=true and o.anio=:anio";
        parametros.put("anio", anio);
        where += " and  upper(o.codigo) like :codigo";
        parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
        if (direccion != null) {
            where += " and upper(o.direccion)=:direccion";
            parametros.put("direccion", direccion);
        }
        parametros.put(";orden", " o.codigo");

        listaProyectoNombre = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        parametros.put(";where", where);
        try {
            listaProyectoNombre = ejbProyectospoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public String aprobarDirector() {
        try {
            if (!cabeceraReforma.getDirector().equals(1)) {
                MensajesErrores.advertencia("Solicitud en otro estado");
                return null;
            }
            for (Reformaspoa rpoa : reformas) {
                if (rpoa.getId() != null) {
                    if (rpoa.getAsignacion().getProyecto().getDireccion().equals(direccion)) {
                        rpoa.setAprobado(Boolean.TRUE);
                        ejbReformas.edit(rpoa, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            if (yaEstaTodo()) {
                cabeceraReforma.setDirector(2);
            }
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioImprimir.cancelar();
        return null;

    }

    public boolean yaEstaTodo() {
        for (Reformaspoa rpoa : reformas) {
            if (rpoa.getId() != null) {
                if (rpoa.getAprobado() == null) {
                    return false;
                }
                if (!rpoa.getAprobado()) {
                    return false;
                }
            }
        }
        return true;
    }

    public String negarDirector() {
        try {
            if (!cabeceraReforma.getDirector().equals(1)) {
                MensajesErrores.advertencia("Solicitud en otro estado");
                return null;
            }
            for (Reformaspoa rpoa : reformas) {
                if (rpoa.getId() != null) {
                    if (rpoa.getAsignacion().getProyecto().getDireccion().equals(direccion)) {
                        rpoa.setAprobado(Boolean.FALSE);
                        ejbReformas.edit(rpoa, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            cabeceraReforma.setDirector(3);
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioImprimir.cancelar();
        return null;

    }

    public void cambiaCodigoPartida(ValueChangeEvent event) {
        setPartidaPoa(null);
        if (getPartidasLista() == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Partidaspoa p : getPartidasLista()) {
            String aComparar = getTipoBuscar() == 1 ? p.getCodigo() : p.getNombre();
            if (aComparar.replaceAll("\\s", "").compareToIgnoreCase(newWord.replaceAll("\\s", "")) == 0) {
                setPartidaPoa(p);
            }
        }
    }

    public void codigoPartidaChangeEventHandler(TextChangeEvent event) {
        setPartidaPoa(null);
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            setPartidaPoa(null);
        }
        setPartidasLista(new LinkedList<>());
        // traer la consulta
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true and o.ingreso=false";
        if (getIngreso() != null) {
            where += " and o.ingreso=:ingreso";
            parametros.put("ingreso", getIngreso());
        }
        if (getTipoBuscar() == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (getTipoBuscar() == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);
        setPartidasLista(null);
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            setPartidasLista(ejbPartidaspoa.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void partidaChangeEventHandler(TextChangeEvent event) {
        partidaPoa = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            partidaPoa = null;
        }
        partidasLista = new LinkedList<>();
        // traer la consulta
        Map parametros = new HashMap();
        String where = " o.activo=true and o.imputable=true";
        if (ingreso != null) {
            where += " and o.ingreso=:ingreso";
            parametros.put("ingreso", ingreso);
        }
        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);
        partidasLista = null;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 10);
        try {
            partidasLista = ejbPartidaspoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger(PartidasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String solicitud(Cabecerareformaspoa cr, boolean elec) {
        cabeceraReforma = cr;
        getFormularioSolicitud().insertar();
        try {
            traerImagen();
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            Codigos codigoTexto = codigosBean.traerCodigo("TEXSOLCERYREF", "01");
            if (codigoTexto == null) {
                MensajesErrores.advertencia("No existe texto de Solicitud de Certificación");
                return null;
            }
            String nombreD = "";
            String cargoD = "";
            if (codigoTexto.getNombre() != null) {
                String[] campos = codigoTexto.getNombre().split("@");
                if (campos != null && campos.length == 2) {
                    nombreD = campos[0];
                    cargoD = campos[1];
                }
            }
            Codigos codigoTextoR = codigosBean.traerCodigo("TEXSOLCERYREF", "02");
            if (codigoTextoR == null) {
                MensajesErrores.advertencia("No existe texto de Solicitud de Certificación");
                return null;
            }
            String texto1 = "";
            String texto2 = "";
            String texto3 = "";
            if (codigoTextoR.getDescripcion() != null) {
                String[] campos = codigoTextoR.getDescripcion().split("@");
                if (campos != null && campos.length == 2) {
                    texto1 = campos[0];
                    texto2 = campos[1];
                }
            }
            if (codigoTextoR.getParametros() != null) {
                texto3 = codigoTextoR.getParametros();
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cr);
            List<Reformaspoa> lista = ejbReformas.encontarParametros(parametros);

            Codigos codigoTextoC = codigosBean.traerCodigo("TEXSOLCERYREF", "03");
            if (codigoTextoC == null) {
                MensajesErrores.advertencia("No existe texto de Solicitud de Certificación");
                return null;
            }
            String nombre1 = "";
            String cargo1 = "";
            String nombre2 = "";
            String cargo2 = "";
            String nombre3 = "";
            String cargo3 = "";
            if (codigoTextoC.getNombre() != null) {
                String[] campos = codigoTextoC.getNombre().split("@");
                if (campos != null && campos.length == 2) {
                    nombre1 = campos[0];
                    cargo1 = campos[1];
                }
            }
            if (codigoTextoC.getDescripcion() != null) {
                String[] campos = codigoTextoC.getDescripcion().split("@");
                if (campos != null && campos.length == 2) {
                    nombre2 = campos[0];
                    cargo2 = campos[1];
                }
            }
            if (codigoTextoC.getParametros() != null) {
                String[] campos = codigoTextoC.getParametros().split("@");
                if (campos != null && campos.length == 2) {
                    nombre3 = campos[0];
                    cargo3 = campos[1];
                }
            }

            pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("Memorando Nro. CBDMQ-" + direccion + "-" + cr.getAnio() + "-" + cr.getId() + "MEM", AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("Quito, D.M., " + sdf.format(cr.getFecha()), AuxiliarReporte.ALIGN_RIGHT, 11, true);

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            List<AuxiliarReporte> valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, "PARA: "));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, false, nombreD));
            valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, ""));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, true, cargoD));
            valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, "ASUNTO: "));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, false, "Reforma Pac â Poa"));
            pdf.agregarTablaSolicitud(null, valores, new float[]{new Float(15), new Float(75)}, 100, "");

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafo(texto1, AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafo("\n" + texto2, AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 10, false);

            List<AuxiliarReporte> titulo = new LinkedList<>();
            titulo.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, "DETALLE"));
            titulo.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, true, "ACCIÓN"));
            titulo.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, "JUSTIFICACIÓN"));
            valores = new LinkedList<>();
            for (Reformaspoa r : lista) {
                valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, false, r.getAsignacion().getProyecto().getNombre()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, false, r.getRequerimiento()));
                valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, false, r.getCabecera().getMotivo()));
            }
            pdf.agregarTablaReporte(titulo, valores, 3, 100, "");

            pdf.agregaParrafo("\n\n\n" + texto3, AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafo("\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 10, false);
            if (elec) {
                pdf.agregaParrafo("Documento firmado electrónicamente", AuxiliarReporte.ALIGN_JUSTIFIED, 10, true);
            }
            pdf.agregaParrafo("Ing. " + seguridadbean.getLogueado().toString(), AuxiliarReporte.ALIGN_JUSTIFIED, 10, false);
            pdf.agregaParrafo(getCargo(), AuxiliarReporte.ALIGN_JUSTIFIED, 10, true);
            pdf.agregaParrafo("\n Copia:", AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + nombre1, AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + cargo1, AuxiliarReporte.ALIGN_JUSTIFIED, 9, true);
            pdf.agregaParrafo("\n         " + nombre2, AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + cargo2, AuxiliarReporte.ALIGN_JUSTIFIED, 9, true);
            pdf.agregaParrafo("\n         " + nombre3, AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + cargo3, AuxiliarReporte.ALIGN_JUSTIFIED, 9, true);
            setReporteSolicitudpdf(pdf.traerRecurso());
            setArchivoFirmar(pdf.traerArchivo());
        } catch (IOException | DocumentException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String firma() {
        getFormularioFirma().insertar();
        return null;

    }

    public String grabarFirma() {
        try {
            solicitud(cabeceraReforma, true);
            if (cabeceraReforma.getArchivosolicitud() != null) {
                MensajesErrores.advertencia("Solicitud ya firmada electronicamente");
                return null;
            }
            FirmadorPDF firmador = new FirmadorPDF("SolicitudCertificacion-" + this.cabeceraReforma.getId(), clave, seguridadbean.getLogueado().getPin(), configuracionBean.getConfiguracion().getDirectorio(), getArchivoFirmar(), pdf.getNumeroPaginas());
            boolean archivoOk = false;
            if (cabeceraReforma.getArchivosolicitud() != null) {
                File existente = new File(cabeceraReforma.getArchivosolicitud());
                if (existente.exists()) {
                    byte[] docByteArry = FileUtils.fileConvertToByteArray(existente);
                    if (docByteArry.length != 0) {
                        archivoOk = true;
                    }
                }
            }

            if (!archivoOk) {
                firmador.firmarSolicitud();
                cabeceraReforma.setArchivosolicitud(configuracionBean.getConfiguracion().getDirectorio() + "/firmados/" + "SolicitudCertificacion-" + this.cabeceraReforma.getId() + ".pdf");

                ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
                solicitud(cabeceraReforma, true);

            } else {
                firmador.editarFirma();
                solicitud(cabeceraReforma, true);
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        setClave(null);
//        formularioImpresion.cancelar();
        getFormularioFirma().cancelar();
        return null;
    }

    public String traerImagen() throws GrabarException {
        try {
            setRecurso(null);
            if (cabeceraReforma != null) {
                if (cabeceraReforma.getArchivosolicitud() != null) {
                    setNombreArchivo("Certificacion: " + cabeceraReforma.getId());
                    setTipoArchivo("Certificacion Presupuestaria");
                    setRecurso(new Recurso(Files.readAllBytes(Paths.get(cabeceraReforma.getArchivosolicitud()))));
                }
            }
        } catch (IOException ex) {
            if (getRecurso() == null) {
                cabeceraReforma.setArchivosolicitud(null);
                ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            }
            MensajesErrores.advertencia("Documento firmado no se encuentra");
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
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
    public List<Reformaspoa> getReformaspoa() {
        return reformas;
    }

    /**
     * @param reformas the reformas to set
     */
    public void setReformaspoa(List<Reformaspoa> reformas) {
        this.reformas = reformas;
    }

    /**
     * @return the reformasb
     */
    public List<Reformaspoa> getReformaspoab() {
        return reformasb;
    }

    /**
     * @param reformasb the reformasb to set
     */
    public void setReformaspoab(List<Reformaspoa> reformasb) {
        this.reformasb = reformasb;
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
     * @return the formularioClasificador
     */
    public Formulario getFormularioClasificador() {
        return formularioClasificador;
    }

    /**
     * @param formularioClasificador the formularioClasificador to set
     */
    public void setFormularioClasificador(Formulario formularioClasificador) {
        this.formularioClasificador = formularioClasificador;
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
     * @return the totales
     */
    public List<AuxiliarCargaPoa> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCargaPoa> totales) {
        this.totales = totales;
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
     * @return the imagenesPoaBean
     */
    public ImagenesPoaBean getImagenesPoaBean() {
        return imagenesPoaBean;
    }

    /**
     * @param imagenesPoaBean the imagenesPoaBean to set
     */
    public void setImagenesPoaBean(ImagenesPoaBean imagenesPoaBean) {
        this.imagenesPoaBean = imagenesPoaBean;
    }

    /**
     * @return the consultasPoa
     */
    public ConsultasPoaBean getConsultasPoa() {
        return consultasPoa;
    }

    /**
     * @param consultasPoa the consultasPoa to set
     */
    public void setConsultasPoa(ConsultasPoaBean consultasPoa) {
        this.consultasPoa = consultasPoa;
    }

    /**
     * @return the nivelProyecto
     */
    public Integer getNivelProyecto() {
        return nivelProyecto;
    }

    /**
     * @param nivelProyecto the nivelProyecto to set
     */
    public void setNivelProyecto(Integer nivelProyecto) {
        this.nivelProyecto = nivelProyecto;
    }

    /**
     * @return the proyectoPadre
     */
    public Proyectospoa getProyectoPadre() {
        return proyectoPadre;
    }

    /**
     * @param proyectoPadre the proyectoPadre to set
     */
    public void setProyectoPadre(Proyectospoa proyectoPadre) {
        this.proyectoPadre = proyectoPadre;
    }

    /**
     * @return the proyectosDirPoaBean
     */
    public ProyectosDireccionPoaBean getProyectosDirPoaBean() {
        return proyectosDirPoaBean;
    }

    /**
     * @param proyectosDirPoaBean the proyectosDirPoaBean to set
     */
    public void setProyectosDirPoaBean(ProyectosDireccionPoaBean proyectosDirPoaBean) {
        this.proyectosDirPoaBean = proyectosDirPoaBean;
    }

    /**
     * @return the codigoHijo
     */
    public String getCodigoHijo() {
        return codigoHijo;
    }

    /**
     * @param codigoHijo the codigoHijo to set
     */
    public void setCodigoHijo(String codigoHijo) {
        this.codigoHijo = codigoHijo;
    }

    /**
     * @return the reformasfin
     */
    public List<Reformas> getReformasfin() {
        return reformasfin;
    }

    /**
     * @param reformasfin the reformasfin to set
     */
    public void setReformasfin(List<Reformas> reformasfin) {
        this.reformasfin = reformasfin;
    }

    /**
     * @return the cabeceraReformaFinanciero
     */
    public Cabecerareformas getCabeceraReformaFinanciero() {
        return cabeceraReformaFinanciero;
    }

    /**
     * @param cabeceraReformaFinanciero the cabeceraReformaFinanciero to set
     */
    public void setCabeceraReformaFinanciero(Cabecerareformas cabeceraReformaFinanciero) {
        this.cabeceraReformaFinanciero = cabeceraReformaFinanciero;
    }

//ES-2018-03-26 PROCESO PARA GRABAR ASIGNACIONES FINANCIERAS    
    public void grabarAsignacionFinanciera(Proyectos p) {

        try {
            //Map parametros = new HashMap();
//            parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
//            parametros.put("codigo", codp);
//            parametros.put("anio", anio);
//            List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
//            Proyectos p = null;
//            for (Proyectos p1 : listap) {
//                p = p1;
//            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", p.getCodigo());
            List<Clasificadores> listac;
            listac = ejbPartidasFinanciera.encontarParametros(parametros);
            Clasificadores c = null;
            for (Clasificadores c1 : listac) {
                c = c1;
            }

            // Buscar la fuente
            Codigos co = null;
            List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
            for (Codigos co1 : listaCodigos) {
                co = co1;
            }
//            CREACION DE CABECERAS
            Calendar cal = Calendar.getInstance();
            cabeceraReformaFinanciero = new Cabecerareformas();
            cabeceraReformaFinanciero.setFecha(cabeceraReforma.getFecha());
            cabeceraReformaFinanciero.setDefinitivo(Boolean.FALSE);
            cabeceraReformaFinanciero.setAnio(cal.get(Calendar.YEAR));
            cabeceraReformaFinanciero.setMotivo(cabeceraReforma.getMotivo());
            cabeceraReformaFinanciero.setTipo(codigosBean.traerCodigoNombre("TIPREF", cabeceraReforma.getTipo()));
            ejbCabecerasFinanciera.create(cabeceraReformaFinanciero, seguridadbean.getLogueado().getUserid());
            parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador "
                    + " and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("clasificador", c);
            parametros.put("proyecto", p);
            parametros.put("fuente", co);
            List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
            Asignaciones af = null;
            for (Asignaciones af1 : listaa) {
                af = af1;
            }
            if (af == null) {
                // creo la signación
                af = new Asignaciones();
                af.setProyecto(p);
                af.setClasificador(c);
                af.setCerrado(true);
                af.setFuente(co);
                af.setValor(new BigDecimal(BigInteger.ZERO));
                ejbAsignacionesFinanciera.create(af, seguridadbean.getLogueado().getUserid());
            }
            if (!reformas.isEmpty()) {
                for (Reformaspoa r : reformas) {
                    Reformas rf = new Reformas();
                    rf.setCabecera(cabeceraReformaFinanciero);
                    rf.setFecha(cabeceraReforma.getFecha());
                    rf.setValor(r.getValor());
                    rf.setAsignacion(af);
                    ejbReformasFinancieras.create(rf, seguridadbean.getLogueado().getUserid());

                }
            }
        } catch (org.errores.sfccbdmq.ConsultarException | org.errores.sfccbdmq.InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void grabarAsignacionFinanciera(List<Reformaspoa> lr) {

        try {
            Calendar cal = Calendar.getInstance();
            cabeceraReformaFinanciero = new Cabecerareformas();
            cabeceraReformaFinanciero.setFecha(cabeceraReforma.getFecha());
            cabeceraReformaFinanciero.setDefinitivo(Boolean.FALSE);
            cabeceraReformaFinanciero.setAnio(cal.get(Calendar.YEAR));
            cabeceraReformaFinanciero.setMotivo(cabeceraReforma.getMotivo());
            cabeceraReformaFinanciero.setTipo(codigosBean.traerCodigo("TIPREF", cabeceraReforma.getTipo()));
            ejbCabecerasFinanciera.create(cabeceraReformaFinanciero, seguridadbean.getLogueado().getUserid());

            for (Reformaspoa rp : lr) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                parametros.put("codigo", rp.getAsignacion().getProyecto().getCodigo());
                parametros.put("anio", getAnio());
                List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                Proyectos p = null;
                for (Proyectos p1 : listap) {
                    p = p1;
                }
                if (p == null) {
                    Proyectos pf = new Proyectos();
                    pf.setAnio(rp.getAsignacion().getProyecto().getAnio());
                    pf.setCodigo(rp.getAsignacion().getProyecto().getCodigo());
                    pf.setNombre(rp.getAsignacion().getProyecto().getNombre());
                    pf.setObservaciones(rp.getAsignacion().getProyecto().getObservaciones());
                    pf.setIngreso(rp.getAsignacion().getProyecto().getIngreso());
                    pf.setImputable(rp.getAsignacion().getProyecto().getImputable());
                    pf.setAnio(rp.getAsignacion().getProyecto().getAnio());
                    ejbProyectosFinanciera.create(pf, seguridadbean.getLogueado().getUserid());
                    p = pf;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", rp.getAsignacion().getPartida().getCodigo());
                List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                Clasificadores c = null;
                for (Clasificadores c1 : listac) {
                    c = c1;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", rp.getAsignacion().getFuente());
                Codigos co = null;
                List<Codigos> listaCodigos = ejbCodigos.encontarParametros(parametros);
                for (Codigos co1 : listaCodigos) {
                    co = co1;
                }

                parametros = new HashMap();
                parametros.put(";where", "o.clasificador=:clasificador "
                        + " and o.proyecto=:proyecto and o.fuente=:fuente");
                parametros.put("clasificador", c);
                parametros.put("proyecto", p);
                parametros.put("fuente", co);
                List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                Asignaciones af = null;
                for (Asignaciones af1 : listaa) {
                    af = af1;
                }
                if (af == null) {
                    // creo la signación
                    af = new Asignaciones();
                    af.setProyecto(p);
                    af.setClasificador(c);
                    af.setCerrado(true);
                    af.setFuente(co);
                    af.setValor(new BigDecimal(BigInteger.ZERO));
                    ejbAsignacionesFinanciera.create(af, seguridadbean.getLogueado().getUserid());
                }
                Reformas rf = new Reformas();
                rf.setCabecera(cabeceraReformaFinanciero);
                rf.setFecha(cabeceraReforma.getFecha());
                rf.setValor(rp.getValor());
                rf.setAsignacion(af);
                ejbReformasFinancieras.create(rf, seguridadbean.getLogueado().getUserid());

            }
        } catch (org.errores.sfccbdmq.ConsultarException | org.errores.sfccbdmq.InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SelectItem[] getComboSubactividad() {
        try {
            listaProyectosSubactividad = new LinkedList<>();
            if (direccionFondo == null || direccionFondo.isEmpty()) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.anio=:anio  and o.activo=true and upper(o.direccion)=:direccion and o.imputable=true");
            parametros.put("direccion", direccionFondo.toUpperCase());
            parametros.put("anio", getAnio());
            listaProyectosSubactividad = ejbProyectospoa.encontarParametros(parametros);
            return Combos.getSelectItems(listaProyectosSubactividad, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Proyectospoa> getListaPro() {
        try {
            if (direccionFondo == null || direccionFondo.isEmpty()) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.codigo");
            parametros.put(";where", "o.anio=:anio  and o.activo=true and upper(o.direccion)=:direccion and o.imputable=true");
            parametros.put("direccion", direccionFondo.toUpperCase());
            parametros.put("anio", getAnio());
            listaProyectosSubactividad = ejbProyectospoa.encontarParametros(parametros);
            return listaProyectosSubactividad;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPartidas() {
        try {
            listaPartidas = new LinkedList<>();
            if (proyectoFondo == null || proyectoFondo.isEmpty()) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.partida.codigo");
            parametros.put(";where", "o.proyecto.codigo like :proyecto and o.fuente=:fuente");
            parametros.put("proyecto", proyectoFondo + "%");
            parametros.put("fuente", asignacionesPoaBean.getFuente());
            listaPartidas = ejbAsignaciones.encontarParametros(parametros);
            return Combos.getSelectItems(listaPartidas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPartidas2() {
        try {
            List<Asignacionespoa> asignaciones = new LinkedList<>();
            List<Partidaspoa> partidas = new LinkedList<>();
            if (asignacionesPoaBean.getFuente() == null) {
                return null;
            }
            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return null;
            }
            if (proyectosPoaBean.getProyectoSeleccionado().getCodigo().length() < 10) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.partida.codigo");
            parametros.put(";where", "o.proyecto.codigo like :proyecto and o.fuente=:fuente and o.proyecto.anio=:anio");
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado().getCodigo() + "%");
            parametros.put("fuente", asignacionesPoaBean.getFuente());
            parametros.put("anio", anio);
            asignaciones = ejbAsignaciones.encontarParametros(parametros);
            for (Asignacionespoa ap : asignaciones) {
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
                parametros.put("asignacion", ap);
                double valorReforma = ejbReformas.sumarCampo(parametros).doubleValue();
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
                parametros.put("asignacion", ap);
                double valorCertificacion = ejbCertificaciones.sumarCampo(parametros).doubleValue();
                double valorAsignacion = ap.getValor().doubleValue();
                double total = valorAsignacion - (valorReforma + valorCertificacion);
                if (total != 0) {
                    partidas.add(ap.getPartida());
                }
            }
            return Combos.getSelectItems(partidas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getTotalReformaspoaAisgnacionFondo() {
        if (asignacionFondo != null) {
            return calculaTotalReformaspoaAsignacion(asignacionFondo);
        }
        return 0;
    }

    public double getValorCertificacionFondo() {
        if (asignacionFondo != null) {
            double retorno = calculaTotalCertificacionesAsignacion(asignacionFondo);
            return retorno;
        }
        return 0;
    }

    public double getSaldoActualFondo() {
        if (asignacionFondo != null) {
            double asiganacionLocal = asignacionFondo.getValor().doubleValue();
            double reformaLocal = getTotalReformaspoaAisgnacionFondo();
            double certificacion = getValorCertificacionFondo();
            return asiganacionLocal + reformaLocal - certificacion;
        }
        return 0;
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

    public String estadoExtra(Cabecerareformaspoa c) {
        String retorno = "";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", c);
            List<Reformaspoa> lista = ejbReformas.encontarParametros(parametros);
            List<Reformaspoa> lista2 = new LinkedList<>();
            for (Reformaspoa r : lista) {
                yaEstaDireccion(r, lista2);
            }
            for (Reformaspoa r : lista2) {
                if (r.getAprobado() == null) {
                    r.setAprobado(Boolean.FALSE);
                }
                retorno += seguridadbean.traerDireccion(r.getAsignacion().getProyecto().getDireccion()).toString() + " - Aprobado: "
                        + (r.getAprobado() ? "SI " : "NO ") + " / ";
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public void yaEstaDireccion(Reformaspoa r, List<Reformaspoa> lista2) {
        for (Reformaspoa rpoa : lista2) {
            if (r.getAsignacion().getProyecto().getDireccion().trim().equals(rpoa.getAsignacion().getProyecto().getDireccion().trim())) {
                return;
            }
        }
        lista2.add(r);
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
     * @return the erroresPac
     */
    public List getErroresPac() {
        return erroresPac;
    }

    /**
     * @param erroresPac the erroresPac to set
     */
    public void setErroresPac(List erroresPac) {
        this.erroresPac = erroresPac;
    }

    /**
     * @return the reportepdfPac
     */
    public Recurso getReportepdfPac() {
        return reportepdfPac;
    }

    /**
     * @param reportepdfPac the reportepdfPac to set
     */
    public void setReportepdfPac(Recurso reportepdfPac) {
        this.reportepdfPac = reportepdfPac;
    }

    /**
     * @return the pdfPAc
     */
    public DocumentoPDF getPdfPAc() {
        return pdfPAc;
    }

    /**
     * @param pdfPAc the pdfPAc to set
     */
    public void setPdfPAc(DocumentoPDF pdfPAc) {
        this.pdfPAc = pdfPAc;
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
     * @return the listaProyectos
     */
    public List<Proyectospoa> getListaProyectos() {
        return listaProyectosPac;
    }

    /**
     * @param listaProyectos the listaProyectos to set
     */
    public void setListaProyectos(List<Proyectospoa> listaProyectos) {
        this.listaProyectosPac = listaProyectos;
    }

//    /**
//     * @return the listaProyectosNuevo
//     */
//    public List<Proyectospoa> getListaProyectosNuevo() {
//        return listaProyectosNuevo;
//    }
//
//    /**
//     * @param listaProyectosNuevo the listaProyectosNuevo to set
//     */
//    public void setListaProyectosNuevo(List<Proyectospoa> listaProyectosNuevo) {
//        this.listaProyectosNuevo = listaProyectosNuevo;
//    }
//
//    /**
//     * @return the listaProyectosNoModificados
//     */
//    public List<Proyectospoa> getListaProyectosNoModificados() {
//        return listaProyectosNoModificados;
//    }
//
//    /**
//     * @param listaProyectosNoModificados the listaProyectosNoModificados to set
//     */
//    public void setListaProyectosNoModificados(List<Proyectospoa> listaProyectosNoModificados) {
//        this.listaProyectosNoModificados = listaProyectosNoModificados;
//    }
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
     * @return the separadorPac
     */
    public String getSeparadorPac() {
        return separadorPac;
    }

    /**
     * @param separadorPac the separadorPac to set
     */
    public void setSeparadorPac(String separadorPac) {
        this.separadorPac = separadorPac;
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
     * @return the direccionMostrar
     */
    public String getDireccionMostrar() {
        return direccionMostrar;
    }

    /**
     * @param direccionMostrar the direccionMostrar to set
     */
    public void setDireccionMostrar(String direccionMostrar) {
        this.direccionMostrar = direccionMostrar;
    }

    /**
     * @return the tipoReforma
     */
    public Codigos getTipoReforma() {
        return tipoReforma;
    }

    /**
     * @param tipoReforma the tipoReforma to set
     */
    public void setTipoReforma(Codigos tipoReforma) {
        this.tipoReforma = tipoReforma;
    }

    /**
     * @return the proyectoSubactividad
     */
    public Proyectospoa getProyectoSubactividad() {
        return proyectoSubactividad;
    }

    /**
     * @param proyectoSubactividad the proyectoSubactividad to set
     */
    public void setProyectoSubactividad(Proyectospoa proyectoSubactividad) {
        this.proyectoSubactividad = proyectoSubactividad;
    }

    /**
     * @return the formularioSubactividad
     */
    public Formulario getFormularioSubactividad() {
        return formularioSubactividad;
    }

    /**
     * @param formularioSubactividad the formularioSubactividad to set
     */
    public void setFormularioSubactividad(Formulario formularioSubactividad) {
        this.formularioSubactividad = formularioSubactividad;
    }

    /**
     * @return the proyectoSubactividadNuevo
     */
    public Proyectospoa getProyectoSubactividadNuevo() {
        return proyectoSubactividadNuevo;
    }

    /**
     * @param proyectoSubactividadNuevo the proyectoSubactividadNuevo to set
     */
    public void setProyectoSubactividadNuevo(Proyectospoa proyectoSubactividadNuevo) {
        this.proyectoSubactividadNuevo = proyectoSubactividadNuevo;
    }

    /**
     * @return the codigoNombre
     */
    public String getCodigoNombre() {
        return codigoNombre;
    }

    /**
     * @param codigoNombre the codigoNombre to set
     */
    public void setCodigoNombre(String codigoNombre) {
        this.codigoNombre = codigoNombre;
    }

    /**
     * @return the listaProyectoNombre
     */
    public List<Proyectospoa> getListaProyectoNombre() {
        return listaProyectoNombre;
    }

    /**
     * @param listaProyectoNombre the listaProyectoNombre to set
     */
    public void setListaProyectoNombre(List<Proyectospoa> listaProyectoNombre) {
        this.listaProyectoNombre = listaProyectoNombre;
    }

    /**
     * @return the proyectoNombre
     */
    public Proyectospoa getProyectoNombre() {
        return proyectoNombre;
    }

    /**
     * @param proyectoNombre the proyectoNombre to set
     */
    public void setProyectoNombre(Proyectospoa proyectoNombre) {
        this.proyectoNombre = proyectoNombre;
    }

    /**
     * @return the proyectoCambia
     */
    public String getProyectoCambia() {
        return proyectoCambia;
    }

    /**
     * @param proyectoCambia the proyectoCambia to set
     */
    public void setProyectoCambia(String proyectoCambia) {
        this.proyectoCambia = proyectoCambia;
    }

    /**
     * @return the puedeEditar
     */
    public Boolean getPuedeEditar() {
        return puedeEditar;
    }

    /**
     * @param puedeEditar the puedeEditar to set
     */
    public void setPuedeEditar(Boolean puedeEditar) {
        this.puedeEditar = puedeEditar;
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
     * @return the codigoPartida
     */
    public String getCodigoPartida() {
        return codigoPartida;
    }

    /**
     * @param codigoPartida the codigoPartida to set
     */
    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

    /**
     * @return the partidaPoa
     */
    public Partidaspoa getPartidaPoa() {
        return partidaPoa;
    }

    /**
     * @param partidaPoa the partidaPoa to set
     */
    public void setPartidaPoa(Partidaspoa partidaPoa) {
        this.partidaPoa = partidaPoa;
    }

    /**
     * @return the partidasLista
     */
    public List<Partidaspoa> getPartidasLista() {
        return partidasLista;
    }

    /**
     * @param partidasLista the partidasLista to set
     */
    public void setPartidasLista(List<Partidaspoa> partidasLista) {
        this.partidasLista = partidasLista;
    }

    /**
     * @return the ingreso
     */
    public Boolean getIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(Boolean ingreso) {
        this.ingreso = ingreso;
    }

    /**
     * @return the listaProyectosSubactividad
     */
    public List<Proyectospoa> getListaProyectosSubactividad() {
        return listaProyectosSubactividad;
    }

    /**
     * @param listaProyectosSubactividad the listaProyectosSubactividad to set
     */
    public void setListaProyectosSubactividad(List<Proyectospoa> listaProyectosSubactividad) {
        this.listaProyectosSubactividad = listaProyectosSubactividad;
    }

    /**
     * @return the listaPartidas
     */
    public List<Asignacionespoa> getListaPartidas() {
        return listaPartidas;
    }

    /**
     * @param listaPartidas the listaPartidas to set
     */
    public void setListaPartidas(List<Asignacionespoa> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    /**
     * @return the proyectoFondo
     */
    public String getProyectoFondo() {
        return proyectoFondo;
    }

    /**
     * @param proyectoFondo the proyectoFondo to set
     */
    public void setProyectoFondo(String proyectoFondo) {
        this.proyectoFondo = proyectoFondo;
    }

    /**
     * @return the direccionFondo
     */
    public String getDireccionFondo() {
        return direccionFondo;
    }

    /**
     * @param direccionFondo the direccionFondo to set
     */
    public void setDireccionFondo(String direccionFondo) {
        this.direccionFondo = direccionFondo;
    }

    /**
     * @return the asignacionFondo
     */
    public Asignacionespoa getAsignacionFondo() {
        return asignacionFondo;
    }

    /**
     * @param asignacionFondo the asignacionFondo to set
     */
    public void setAsignacionFondo(Asignacionespoa asignacionFondo) {
        this.asignacionFondo = asignacionFondo;
    }

    /**
     * @return the formularioSolicitud
     */
    public Formulario getFormularioSolicitud() {
        return formularioSolicitud;
    }

    /**
     * @param formularioSolicitud the formularioSolicitud to set
     */
    public void setFormularioSolicitud(Formulario formularioSolicitud) {
        this.formularioSolicitud = formularioSolicitud;
    }

    /**
     * @return the cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the archivoFirmar
     */
    public File getArchivoFirmar() {
        return archivoFirmar;
    }

    /**
     * @param archivoFirmar the archivoFirmar to set
     */
    public void setArchivoFirmar(File archivoFirmar) {
        this.archivoFirmar = archivoFirmar;
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
     * @return the reporteSolicitudpdf
     */
    public Recurso getReporteSolicitudpdf() {
        return reporteSolicitudpdf;
    }

    /**
     * @param reporteSolicitudpdf the reporteSolicitudpdf to set
     */
    public void setReporteSolicitudpdf(Recurso reporteSolicitudpdf) {
        this.reporteSolicitudpdf = reporteSolicitudpdf;
    }

    /**
     * @return the formularioFirma
     */
    public Formulario getFormularioFirma() {
        return formularioFirma;
    }

    /**
     * @param formularioFirma the formularioFirma to set
     */
    public void setFormularioFirma(Formulario formularioFirma) {
        this.formularioFirma = formularioFirma;
    }

    /**
     * @return the recurso
     */
    public Recurso getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

}
