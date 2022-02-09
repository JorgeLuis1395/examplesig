/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
import org.entidades.sfccbdmq.Reformaspoa;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCargaPoa;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import static org.entidades.sfccbdmq.Clientes_.direccion;
import org.entidades.sfccbdmq.Codigos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.personal.sfccbdmq.SolicitudReformasBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "aprobarReformas")
@ViewScoped
public class AprobarReformasBean {

    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoa;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

    public AprobarReformasBean() {
    }

    private List<Reformaspoa> reformas;
    private List<Cabecerareformaspoa> cabecerasReformaspoa;
    private Cabecerareformaspoa cabeceraReforma = new Cabecerareformaspoa();
    private List<AuxiliarCargaPoa> totales = new LinkedList<>();
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private DocumentoPDF pdf;
    private int anio;
    private Date desde;
    private Date hasta;
    private Date vigente;
    private Recurso reportepdf;
    private Asignacionespoa asignacion;
    private Integer id;

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificaciones;
    @EJB
    private CabecerareformaspoaFacade ejbCabeceras;

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
        setVigente(getConfiguracionBean().getConfiguracion().getPvigentepresupuesto());
        setDesde(getConfiguracionBean().getConfiguracion().getPinicialpresupuesto());
        setHasta(getConfiguracionBean().getConfiguracion().getPfinalpresupuesto());
        Calendar ca = Calendar.getInstance();
        ca.setTime(getDesde());
        setAnio(ca.get(Calendar.YEAR));
    }

    public String buscar() {
        if (getAnio() <= 0) {
            setReformas(null);
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.fecha between :desde and :hasta and o.definitivo!=true and o.utilizado=false"
                + " and o.director not in (1,3) and  o.director is not null";
        parametros.put(";where", where);
        parametros.put("anio", getAnio());
        parametros.put("desde", getDesde());
        parametros.put("hasta", getHasta());
        parametros.put(";orden", "o.fecha desc");

        if (getId() != null) {
            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", getId());
        }
        try {
            setCabecerasReformaspoa(ejbCabeceras.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirCabecera(Cabecerareformaspoa cabeceraReforma) {

        this.setCabeceraReforma(cabeceraReforma);
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            setReformas(ejbReformas.encontarParametros(parametros));
            Reformaspoa rtotal = new Reformaspoa();
            rtotal.setTotalSubactividad(0);
            rtotal.setTotalReforma(0);
            rtotal.setTotalTotal(0);
            for (Reformaspoa r : getReformas()) {
                r.setTotalSubactividad((r.getAsignacion() != null ? r.getAsignacion().getValor().doubleValue() : 0) + calculaTotalReformaspoa(r));
                r.setTotalReforma(r.getValor().doubleValue());
                r.setTotalTotal(r.getTotalSubactividad() + r.getTotalReforma());
                rtotal.setTotalSubactividad(rtotal.getTotalSubactividad() + r.getTotalSubactividad());
                rtotal.setTotalReforma(rtotal.getTotalReforma() + r.getTotalReforma());
                rtotal.setTotalTotal(rtotal.getTotalTotal() + r.getTotalTotal());
            }
            getReformas().add(rtotal);
            calculaTotales();
            setPdf(new DocumentoPDF(""
                    + "Reformaspoa Presupuesto año " + getAnio(), getSeguridadbean().getLogueado().getUserid()));
            getPdf().agregaParrafo("\nNúmero de control: " + cabeceraReforma.getId(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            getPdf().agregaParrafo("Tipo: " + getConsultasPoa().traerTipoReforma(cabeceraReforma.getTipo()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            getPdf().agregaParrafo("Fecha: " + sdf.format(cabeceraReforma.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            getPdf().agregaParrafo("Concepto: " + cabeceraReforma.getMotivo(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            getPdf().agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();

            List<Auxiliar> lista = getProyectosPoaBean().getTitulos();
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

            for (Reformaspoa r : getReformas()) {
                if (r.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, (r.getAsignacion() != null ? (getProyectosPoaBean().dividir1(a, r.getAsignacion().getProyecto()))
                                : (r.getProyecto() != null ? (getProyectosPoaBean().dividir1(a, r.getProyecto())) : ""))));
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
                    for (AuxiliarCargaPoa ac : getTotales()) {
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, ac.getIngresos().doubleValue()));
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, ac.getEgresos().doubleValue()));
                    }
                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, r.getTotalTotal()));
                }
            }
            getPdf().agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            getPdf().agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado"));
            valores.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado"));
            getPdf().agregarTabla(null, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);

            setReportepdf(getPdf().traerRecurso());

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        getFormularioImprimir().editar();
        return null;
    }

    private void calculaTotales() {
        double totalIngreso = 0;
        double totalEgreso = 0;
        setTotales(new LinkedList<>());
        List<Codigos> fuentes = getCodigosBean().traerCodigoMaestro("FUENFIN");
        for (Codigos c : fuentes) {
            AuxiliarCargaPoa auxiliarCarga = new AuxiliarCargaPoa();
            auxiliarCarga.setFuente(c);
            auxiliarCarga.setIngresos(new BigDecimal(0));
            auxiliarCarga.setEgresos(new BigDecimal(0));
            getTotales().add(auxiliarCarga);
        }

        for (Reformaspoa r : getReformas()) {
            Asignacionespoa a = r.getAsignacion();
            for (AuxiliarCargaPoa auxCarga : getTotales()) {
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

    public double getTotalReformaspoa() {
        Reformaspoa r = getReformas().get(getFormulario().getFila().getRowIndex());
        return calculaTotalReformaspoaAsignacion(r.getAsignacion());
    }

    public double getTotalReformaspoaImp() {
        Reformaspoa r = getReformas().get(getFormularioImprimir().getFila().getRowIndex());
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
        if ((getPartidasPoaBean().getPartidaPoa() != null)) {
            // buscar asignacion

            if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.activo=true");
            parametros.put("clasificador", getPartidasPoaBean().getPartidaPoa());
            parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado());
            try {
                setAsignacion(null);
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    setAsignacion(a);
                }
                if (getAsignacion() == null) {
                    return 0;
                }
                return calculaTotalReformaspoaAsignacion(getAsignacion());
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getValorAsignaciones() {
        if ((getPartidasPoaBean().getPartidaPoa() != null)) {
            if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.proyecto=:actividad and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("partida", getPartidasPoaBean().getPartidaPoa());
            parametros.put("direccion", direccion);
            parametros.put("actividad", getProyectosPoaBean().getProyectoSeleccionado());
            try {
                setAsignacion(null);
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    setAsignacion(a);
                    return a.getValor().doubleValue();
                }
                if (getAsignacion() == null) {
                    return 0;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }

    public double getValorCertificacion() {
//        if ((asignacion == null)) {
        if ((getPartidasPoaBean().getPartidaPoa() != null)) {
            // buscar asignacion

            if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("clasificador", getPartidasPoaBean().getPartidaPoa());
            parametros.put("direccion", direccion);
            parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado());
            try {
                setAsignacion(null);
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    setAsignacion(a);
//                        return a.getValor().doubleValue();
                }
                if (getAsignacion() == null) {
                    return 0;
                }
                // calcula el total de certificaion
                double retorno = calculaTotalCertificacionesAsignacion(getAsignacion());
                return retorno;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            } else {
//            } else {
//            } else {
//            } else {
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
                Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformaspoa r = getReformas().get(getFormulario().getFila().getRowIndex());

//        return calculaTotalCertificaciones(r);
        return calculaTotalCertificacionesAsignacion(r.getAsignacion());
    }

    public double getTotalCertificacionesImp() {
        Reformaspoa r = getReformas().get(getFormularioImprimir().getFila().getRowIndex());

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
            Logger.getLogger(AprobarReformasBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String aprobarDirector() {
        try {
            if (cabeceraReforma.getDirector() == 4) {
                MensajesErrores.advertencia("Reforma aprobada");
                return null;
            }
            if (cabeceraReforma.getDirector() == 5) {
                MensajesErrores.advertencia("Reforma negada");
                return null;
            }
            if (cabeceraReforma.getDirector() == 6) {
                MensajesErrores.advertencia("Reforma finalizado");
                return null;
            }
            getCabeceraReforma().setDirector(4);
            ejbCabeceras.edit(getCabeceraReforma(), getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormularioImprimir().cancelar();
        return null;

    }

    public String negarDirector() {
        try {
            if (cabeceraReforma.getDirector() == 4) {
                MensajesErrores.advertencia("Reforma aprobada");
                return null;
            }
            if (cabeceraReforma.getDirector() == 5) {
                MensajesErrores.advertencia("Reforma negada");
                return null;
            }
            if (cabeceraReforma.getDirector() == 6) {
                MensajesErrores.advertencia("Reforma finalizado");
                return null;
            }
            getCabeceraReforma().setDirector(5);
            ejbCabeceras.edit(getCabeceraReforma(), getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SolicitudReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormularioImprimir().cancelar();
        return null;

    }

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasPoaBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
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
     * @return the reformas
     */
    public List<Reformaspoa> getReformas() {
        return reformas;
    }

    /**
     * @param reformas the reformas to set
     */
    public void setReformas(List<Reformaspoa> reformas) {
        this.reformas = reformas;
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
}
