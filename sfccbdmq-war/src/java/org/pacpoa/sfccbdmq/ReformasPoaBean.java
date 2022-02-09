/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCargaPoa;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerareformasFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabecerareformas;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Reformas;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reformasPoa")
@ViewScoped
public class ReformasPoaBean {

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

    public ReformasPoaBean() {
    }

    private Asignacionespoa asignacion;
    private Reformaspoa reforma;
    private List<Reformaspoa> reformas;
    private List<Reformaspoa> reformasb;
    private List<Cabecerareformaspoa> cabecerasReformaspoa;
    private Cabecerareformaspoa cabeceraReforma = new Cabecerareformaspoa();
    private Formulario formulario = new Formulario();
    private Formulario formularioCabecera = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private List errores;
    private List<AuxiliarCargaPoa> totales = new LinkedList<>();

    private Recurso reportepdf;
    private DocumentoPDF pdf;

    private Date vigente;

    //ES-2018-03-21
    private Integer nivelProyecto = 0;
    private Proyectospoa proyectoPadre;
    private String codigoHijo;
    private Cabecerareformas cabeceraReformaFinanciero;
    private List<Reformas> reformasfin;

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
    private DetallecertificacionesFacade ejbDetallecertificacionesFinancieras;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    private int anio;
    private Integer id;
    private String separador = ";";
    private Date desde;
    private Date hasta;
    private boolean bloqueaFuente;
    private String direccion;

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
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);

//TRAE LA LONGITUD DEL CODIGO DE PROYECTO QUE SE ENCUENTRA EN CÓDIGOS        
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
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

    }

    public String buscar() {
//        if (direccion == null || direccion.isEmpty()) {
//            MensajesErrores.advertencia("Seleccione una dirección");
//            return null;
//        }
        if (anio <= 0) {
            reformas = null;
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.fecha between :desde and :hasta and o.director is null and o.utilizado=true";

//        if (direccion != null) {
//            where += " and o.proyecto.direccion=:direccion";
//            parametros.put("direccion", direccion);
//        }
        if (id != null) {
//            parametros = new HashMap();
//            parametros.put(";where", "o.id=:id");
            where += " and o.id=:id";
            parametros.put("id", id);
        }
        parametros.put(";where", where);
        parametros.put("anio", anio);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fecha desc");
        try {
            cabecerasReformaspoa = ejbCabeceras.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearCabecera() {
        cabeceraReforma = new Cabecerareformaspoa();
//        cabeceraReforma.setDireccion(direccion);
        cabeceraReforma.setFecha(new Date());
        reformas = new LinkedList<>();
        reformasb = new LinkedList<>();
        errores = new LinkedList<>();
        formularioCabecera.insertar();
//        imagenesPoaBean.setListaDocumentos(new LinkedList<>());
        bloqueaFuente = false;
        return null;
    }

    public String crear() {

        formularioClasificador.insertar();
        reforma = new Reformaspoa();
        reforma.setAsignacion(asignacion);
        formulario.insertar();
        reforma.setFecha(vigente);
        partidasPoaBean.setPartidaPoa(null);
        proyectosPoaBean.setProyectoSeleccionado(null);
        partidasPoaBean.setCodigo(null);
        proyectosPoaBean.setCodigo(null);
        totales = new LinkedList<>();
        asignacionesPoaBean.setFuente(null);
        return null;
    }

    public String modificarCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo() == null) {
            cabeceraReforma.setDefinitivo(Boolean.FALSE);
        }
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
            return null;
        }
//        if (!cabeceraReforma.getDirector()) {
//            MensajesErrores.advertencia("Reforma no aprobada por el Director");
//            return null;
//        }
        this.cabeceraReforma = cabeceraReforma;
        this.cabeceraReforma.setDirector(null);
        this.cabeceraReforma.setUtilizado(Boolean.TRUE);
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotales();
        formularioCabecera.editar();
        errores = new LinkedList<>();
        return null;
    }

    public String imprimirCabecera(Cabecerareformaspoa cabeceraReforma) {

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
                    + anio, seguridadbean.getLogueado().getUserid());

            pdf.agregaParrafo("\nNúmero de control: " + cabeceraReforma.getId(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Tipo: " + consultasPoa.traerTipoReforma(cabeceraReforma.getTipo()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafo("Fecha: " + sdf.format(cabeceraReforma.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Concepto: " + cabeceraReforma.getMotivo(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "Ingresos"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "Egresos"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
//
//            for (AuxiliarCarga auxCarga : totales) {
//                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, auxCarga.getIngresos().doubleValue()));
//                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, auxCarga.getEgresos().doubleValue()));
//                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, auxCarga.getIngresos().doubleValue() - auxCarga.getEgresos().doubleValue()));
//            }
//            pdf.agregarTablaReporte(titulos, valores, 3, 100, "TOTALES");
//
//            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//
//            valores = new LinkedList<>();
//            titulos = new LinkedList<>();

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

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.editar();
        return null;
    }

    public String definitivaCabecera(Cabecerareformaspoa cabeceraReforma) {
//        if (cabeceraReforma.getDefinitivo()) {
//            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
//            return null;
//        }
        this.cabeceraReforma = cabeceraReforma;
        this.cabeceraReforma.setDirector(null);
//        if (!cabeceraReforma.getDirector()) {
//            MensajesErrores.advertencia("Reforma no aprobada por el Director");
//            return null;
//        }
        // buscar reformas

        try {
            // Asignaciones + Reformaspoa + Reforma Nueva < Certificaciones
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabeceraReforma);
            List<Reformaspoa> rl = ejbReformas.encontarParametros(parametros);

            double total = 0;
//            for (Reformaspoa a : rl) {
//                total += a.getValor().doubleValue();
//            }
            double totalIngreso = 0;
            double totalEgreso = 0;
            for (Reformaspoa r : rl) {
                if (r.getAsignacion().getPartida().getIngreso()) {
                    totalIngreso += r.getValor().doubleValue();
                } else {
                    totalEgreso += r.getValor().doubleValue();
                }
            }
            total = totalIngreso - totalEgreso;

            double cuadre2 = Math.round(total * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            double ingreso1 = (valortotal2.doubleValue());

            if (ingreso1 != 0) {
                MensajesErrores.advertencia("No cuadrada los totales");
                return null;
            }

            List<Reformas> listaReformasFinancieras = new LinkedList<>();
            for (Reformaspoa r : rl) {
                if (r.getAsignacion() != null) {
                    double asignaciones = r.getAsignacion().getValor().doubleValue();
                    double reformasTotal = calculaTotalReformaspoa(r);
                    double certificacionesTotal = calculaTotalCertificaciones(r);
                    double reformaactual = r.getValor().doubleValue();
                    // Asignaciones + Reformaspoa + Reforma Nueva > Certificaciones
                    double a = Math.round((asignaciones + reformasTotal + reformaactual) * 100);
                    double b = Math.round(certificacionesTotal * 100);

                    if (a < b) {
                        MensajesErrores.advertencia("No es posible colocar como definitiva reforma, no cuadra con certificaciones la cuenta- Proyecto:"
                                + r.getAsignacion().getProyecto().getCodigo() + " " + r.getAsignacion().getProyecto().getNombre());
                        return null;
                    }
                    // grabar la reforma en presupuesto
                    // ver el proyecto de la reforma
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", r.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    if (p == null) {
                        MensajesErrores.advertencia("Proyecto PAC-POA no tiene su similar en Sistema Financiero : " + r.getAsignacion().getProyecto().toString());
                        return null;
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", r.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }
                    if (c == null) {
                        MensajesErrores.advertencia("Partida PAC-POA no tiene su similar en Sistema Financiero : " + r.getAsignacion().getPartida().toString());
                        return null;
                    }
                    // Buscar la fuente
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", r.getAsignacion().getFuente());
                    Codigos co = null;
                    List<Codigos> listaCodigos = ejbCodigos.encontarParametros(parametros);
                    for (Codigos co1 : listaCodigos) {
                        if (co1.getCodigo().equals(r.getAsignacion().getFuente())) {
                            co = co1;
                        }
                    }
                    if (co == null) {
                        MensajesErrores.advertencia("Fuente de Financiamiento PAC-POA no tiene su similar en Sistema Financiero : " + r.getAsignacion().getFuente().toString());
                        return null;
                    }
                    // Buscar Asignación
                    parametros = new HashMap();
                    parametros.put(";where", "o.clasificador.codigo=:clasificador "
                            + " and o.proyecto.codigo=:proyecto and o.fuente.codigo=:fuente");
                    parametros.put("clasificador", c.getCodigo());
                    parametros.put("proyecto", p.getCodigo());
                    parametros.put("fuente", co.getCodigo());
                    List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                    Asignaciones af = null;
                    for (Asignaciones af1 : listaa) {
                        af = af1;
                    }
                    if (af == null) {

//                    if (r.getValor().doubleValue() >= 0) {
                        // creo la signación
                        af = new Asignaciones();
                        af.setProyecto(p);
                        af.setClasificador(c);
                        af.setCerrado(true);
                        af.setFuente(co);
                        af.setValor(new BigDecimal(BigInteger.ZERO));
                        ejbAsignacionesFinanciera.create(af, seguridadbean.getLogueado().getUserid());
//                    } else {
//                        MensajesErrores.advertencia("No existe dinero para ejecutar esta reforma en Sistema Financiero : "
//                                + c.toString() + " - " + p.toString() + " - "
//                                + r.getAsignacion().getFuente().toString());
//                        return null;
//                    }
                    }
                }
            }
            Codigos tipoCabecera = codigosBean.traerCodigoNombre(Constantes.TIPO_REFORMA, cabeceraReforma.getTipo());
            if (tipoCabecera == null) {
                List<Codigos> codigosTipo = codigosBean.traerCodigoMaestro(Constantes.TIPO_REFORMA);
                for (Codigos ct : codigosTipo) {
                    tipoCabecera = ct;
                }
            }
            Cabecerareformas cabFin = new Cabecerareformas();
            cabFin.setAnio(cabeceraReforma.getAnio());
            cabFin.setDefinitivo(false);
            cabFin.setFecha(cabeceraReforma.getFecha());
            cabFin.setMotivo(cabeceraReforma.getMotivo());
            cabFin.setTipo(tipoCabecera);
            cabFin.setPac(Boolean.TRUE);
            ejbCabecerasFinanciera.create(cabFin, seguridadbean.getLogueado().getUserid());
            for (Reformaspoa r : rl) {
                if (r.getAsignacion() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", r.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", r.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }

                    // Buscar la fuente
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", r.getAsignacion().getFuente());
                    Codigos co = null;
                    List<Codigos> listaCodigos = ejbCodigos.encontarParametros(parametros);
                    for (Codigos co1 : listaCodigos) {
                        co = co1;
                    }

                    // Buscar Asignación
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
                    rf.setAsignacion(af);
                    rf.setValor(r.getValor());
                    rf.setCabecera(cabFin);
                    rf.setFecha(cabFin.getFecha());
                    ejbReformasFinancieras.create(rf, seguridadbean.getLogueado().getUserid());
                } else {
                    if (reforma.getNombreproyecto() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.codiog=:codigo and o.activo=true and o.anio=:anio");
                        parametros.put("codigo", reforma.getProyecto().getCodigo());
                        parametros.put("anio", reforma.getProyecto().getCodigo());
                        List<Proyectospoa> listaP = ejbProyectos.encontarParametros(parametros);
                        List<Proyectos> listaPF = ejbProyectosFinanciera.encontarParametros(parametros);
                        if (!listaP.isEmpty()) {
                            Proyectospoa p = listaP.get(0);
                            p.setNombre(reforma.getNombreproyecto());
                            ejbProyectos.edit(p, seguridadbean.getLogueado().getUserid());
                        }
                        if (!listaPF.isEmpty()) {
                            Proyectos p = listaPF.get(0);
                            p.setNombre(reforma.getNombreproyecto());
                            ejbProyectosFinanciera.edit(p, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }  // fin for
            cabeceraReforma.setDefinitivo(Boolean.TRUE);
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma definitiva: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException | InsertarException  ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCabecera.cancelar();
        errores = new LinkedList<>();
        imprimirCabecera(cabeceraReforma);
        MensajesErrores.informacion("Reforma se grabó correctamente");
        return null;
    }

    public String NoDefinitivaCabecera(Cabecerareformaspoa cabeceraReforma) {
        try {
            this.cabeceraReforma = cabeceraReforma;
            // buscar reformas
            Map parametros = new HashMap();
            parametros.put(";where", "o.motivo=:motivo and o.fecha=:fecha and o.pac=true");
            parametros.put("motivo", cabeceraReforma.getMotivo());
            parametros.put("fecha", cabeceraReforma.getFecha());
            List<Cabecerareformas> lista = ejbCabecerasFinanciera.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Cabecerareformas cr = lista.get(0);
                if (cr.getDefinitivo()) {
                    MensajesErrores.advertencia("Reforma con Definitivo en presupuesto");
                    return null;
                } else {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera=:cabecera");
                    parametros.put("cabecera", cr);
                    List<Reformas> lista2 = ejbReformasFinancieras.encontarParametros(parametros);
                    for (Reformas r : lista2) {
                        ejbReformasFinancieras.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                    ejbCabecerasFinanciera.remove(cr, seguridadbean.getLogueado().getUserid());
                    cabeceraReforma.setDefinitivo(false);
                    ejbCabeceras.edit(this.cabeceraReforma, seguridadbean.getLogueado().getUserid());
                    MensajesErrores.informacion("Reforma editada correctamente");
                }
            }
            cabeceraReforma.setDefinitivo(false);
            ejbCabeceras.edit(this.cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (org.errores.sfccbdmq.ConsultarException | org.errores.sfccbdmq.BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificar() {
        reforma = reformas.get(formulario.getFila().getRowIndex());
//        direccion = reforma.getAsignacion().getProyecto().getDireccion();
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioClasificador.cancelar();
        asignacion = reforma.getAsignacion();
        partidasPoaBean.setPartidaPoa(asignacion.getPartida());
        partidasPoaBean.setCodigo(asignacion.getPartida().getCodigo());
        formulario.editar();
        return null;
    }

    public String eliminarCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo() == null) {
            cabeceraReforma.setDefinitivo(Boolean.FALSE);
        }
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
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
            cabeceraReforma.setDefinitivo(null);
            cabeceraReforma.setUtilizado(Boolean.TRUE);
//            cabeceraReforma.setDireccion(direccion);
//            cabeceraReforma.setFecha(new Date());
            cabeceraReforma.setAnio(c.get(Calendar.YEAR));
            //ES-2018-03-23 CREACION DE REFORMAS EN EL FINANCIERO
            ejbCabeceras.create(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            for (Reformaspoa r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
            }

//            imagenesPoaBean.setCabeceraReforma(cabeceraReforma);
//            imagenesPoaBean.insertarDocumentos("reformas");
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma creada: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
//            grabarAsignacionFinanciera(reformas);
        } catch (InsertarException ex) {
//        } catch (InsertarException | IOException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
            cabeceraReforma.setUtilizado(Boolean.TRUE);
//            cabeceraReforma.setDireccion(direccion);
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
//            imagenesPoaBean.insertarDocumentos("reformas");
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
//            grabarAsignacionFinanciera(reformas);
//        } catch (InsertarException | GrabarException | BorrarException | IOException ex) {
        } catch (InsertarException | GrabarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbCabeceras.remove(cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    private boolean validar() {

        double cuadre = 0;
        double ingreso = 0;
        double egreso = 0;
        for (AuxiliarCargaPoa a : totales) {
//            double i = a.getIngresos().doubleValue() * 100;
//            double e = a.getEgresos().doubleValue() * 100;
//            if (Math.round(i - e) != 0) {
//                MensajesErrores.advertencia("No está cuadrada la fuente de financiamiento [Ingresos - Egresos]: " + a.getFuente());
//                return true;
//            }
            ingreso += a.getIngresos().doubleValue();
            egreso += a.getEgresos().doubleValue();
            cuadre = a.getIngresos().doubleValue() + a.getEgresos().doubleValue();
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

        return false;
    }

    public String insertar() {
//
//        if (direccion == null || direccion.isEmpty()) {
//            MensajesErrores.advertencia("Seleccione una dirección");
//            return null;
//        }

        if (reforma.getValor() == null) {
            reforma.setValor(BigDecimal.ZERO);
        }
        // buscar Aisgnacion dependiendo del clasificador
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
        if (!errores.isEmpty()) {
            MensajesErrores.advertencia("Existen errores no se puede grabar");
            return null;
        }
        if (asignacionesPoaBean.getFuente() == null) {
            MensajesErrores.advertencia("Seleccione una Fuente");
            return null;
        }
        // buscar partida en asignación
        Map parametros = new HashMap();
        parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
        parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
        parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
        parametros.put("fuente", asignacionesPoaBean.getFuente());
        asignacion = null;
        try {
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
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        reforma.setAsignacion(asignacion);

        if (reforma.getAsignacion() == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }

//        reforma.setFecha(new Date());
        if (reformas == null) {
            reformas = new LinkedList<>();
        }
        double asignaciones = reforma.getAsignacion().getValor().doubleValue();
//        double reformasTotal = calculaTotalReformaspoa(reforma);
        double reformasTotal = calculaTotalReformaspoaAsignacion(reforma.getAsignacion());
//        double certificacionesTotal = calculaTotalCertificaciones(reforma);
        double certificacionesTotal = calculaTotalCertificacionesAsignacion(reforma.getAsignacion());
        double reformaactual = reforma.getValor().doubleValue();
        // Asignaciones + Reformaspoa + Reforma Nueva > Certificaciones
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
            MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                    + df.format(asignaciones) + " Reformaspoa Anteriores = "
                    + df.format(reformasTotal) + " Certificaciones : "
                    + df.format(certificacionesTotal));
            return null;
        }
        reformas.add(reforma);
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
        return null;
    }

    public String grabar() {
        reformas.set(formulario.getIndiceFila(), reforma);
        double asignaciones = reforma.getAsignacion().getValor().doubleValue();
//        double reformasTotal = calculaTotalReformaspoa(reforma);
        double reformasTotal = calculaTotalReformaspoaAsignacion(reforma.getAsignacion());
//        double certificacionesTotal = calculaTotalCertificaciones(reforma);
        double certificacionesTotal = calculaTotalCertificacionesAsignacion(reforma.getAsignacion());
        double reformaactual = reforma.getValor().doubleValue();
        // Asignaciones + Reformaspoa + Reforma Nueva > Certificaciones
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

//    private void calculaTotales() {
//        double totalIngreso = 0;
//        double totalEgreso = 0;
//        totales = new LinkedList<>();
//
//        AuxiliarCarga auxiliarCarga = new AuxiliarCarga();
//        auxiliarCarga.setFuente(null);
//        auxiliarCarga.setIngresos(new BigDecimal(0));
//        auxiliarCarga.setEgresos(new BigDecimal(0));
//        totales.add(auxiliarCarga);
//
//        for (Reformaspoa r : reformas) {
//            Asignaciones a = r.getAsignacion();
//            if (a != null) {
//
//                for (AuxiliarCarga auxCarga : totales) {
//
//                    if (a.getPartidaPoa().getIngreso()) {
//                        if (r.getValor().doubleValue() > 0) {
//                            // Es un egreso
//                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
//                            totalIngreso += r.getValor().doubleValue();
//                        } else {
//                            // es un ingreso
//                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue() * -1));
//                            totalEgreso += r.getValor().doubleValue() * -1;
//                        }
//                    } else { // se trata de un egreso
//                        if (r.getValor().doubleValue() > 0) {
//                            // es un egreso
//                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
//                            totalEgreso += r.getValor().doubleValue();
//                        } else {
//                            // es un ingreso
//                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue() * -1));
//                            totalIngreso += r.getValor().doubleValue() * -1;
//                        }
//                    } // fin if de ingreso y egreso
//                } // fin for auxCarga
//            }
//        }// fin for reforma
//
//        bloqueaFuente = !(totalIngreso == totalEgreso);
//    }
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
//                            if (r.getValor().doubleValue() > 0) {
                            // Es un egreso
                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
                            totalIngreso += r.getValor().doubleValue();
//                            } else {
//                                // es un ingreso
//                                auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue() * -1));
//                                totalEgreso += r.getValor().doubleValue() * -1;
//
//                            }
                        } else { // se trata de un egreso
//                            if (r.getValor().doubleValue() > 0) {
                            // es un egrso
                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
                            totalEgreso += r.getValor().doubleValue();
//                            } else {
//                                // es un ingreso
//                                auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue() * -1));
//                                totalIngreso += r.getValor().doubleValue() * -1;
//                            }
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
        bloqueaFuente = !(totalIngreso == totalEgreso);
    }

    public String cancelar() {
        formularioCabecera.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String salir() {
        formularioCabecera.cancelar();
        cabeceraReforma = new Cabecerareformaspoa();
        cabecerasReformaspoa = new LinkedList<>();
        return null;
    }

    public double getTotalReformaspoa() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
//        return calculaTotalReformaspoa(r);
        return calculaTotalReformaspoaAsignacion(r.getAsignacion());
    }

    public double getTotalReformaspoaImp() {
        Reformaspoa r = reformas.get(formularioImprimir.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
        return calculaTotalReformaspoa(r);
    }

    public double getSaldoActual() {
        double asiganacionLocal = getValorAsignaciones();
        double reformaLocal = getTotalReformaspoaAisgnacion();
        double certificacion = getValorCertificacion();
        return asiganacionLocal + reformaLocal - certificacion;
    }

    public double getTotalReformaspoaAisgnacion() {
//        if ((asignacion == null)) {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            // buscar asignacion

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
                Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());

//        return calculaTotalCertificaciones(r);
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
            Logger.getLogger(ReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        anio = cAnio.get(Calendar.YEAR);
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
                                parametros.put("anio", anio);
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

                    } catch (IOException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InsertarException ex) {
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

//        if (titulo.contains("partida")) {
//            // buscar el clasificador
//            am.setPartida(partidasPoaBean.traerCodigo(valor));
//        } else
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
                parametros.put("anio", anio);
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
     * @return the bloqueaFuente
     */
    public boolean isBloqueaFuente() {
        return bloqueaFuente;
    }

    /**
     * @param bloqueaFuente the bloqueaFuente to set
     */
    public void setBloqueaFuente(boolean bloqueaFuente) {
        this.bloqueaFuente = bloqueaFuente;
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
            Logger.getLogger(ReformasPoaBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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
                parametros.put("anio", anio);
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
            Logger.getLogger(ReformasPoaBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }
}
