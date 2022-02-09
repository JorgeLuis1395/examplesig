/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;
import java.io.Serializable;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.RmuEncargoSubrogacion;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.ConfiguracionFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "rolEmpleadosSfccbdmq")
@ViewScoped
public class RolEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

//    private List<Pagosempleados> listaPagos;
//    private List<AuxiliarRol> listaRoles;
    private List<Conceptos> listaConceptosIngresos;
    private List<Conceptos> listaConceptosEgresos;
    private List<Empleados> listaEmpleados;
//    private List<AuxiliarAsignacion> listaTotales;
//    private List<Empleados> listaEmpleados;
    private Formulario formulario = new Formulario();

    @EJB
    private ConceptosFacade ejbCon;
    @EJB
    private HistorialcargosFacade ejbHistorial;
    @EJB
    private EmpleadosFacade ejbEmpleados;

    @EJB
    private AmortizacionesFacade ejbPrestamos;
    @EJB
    private PagosempleadosFacade ejbPagos;
    @EJB
    private HistorialsancionesFacade ejbSanciones;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private ConfiguracionFacade ejbConfig;

    @EJB
    private LicenciasFacade ejbLicencia;
    @EJB
    private CabeceraempleadosFacade ejbCabecerasempleados;
    @EJB
    private RangoscabecerasFacade ejbrangocabecra;
    private int mes;
    private int anio;

//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean cxOrganigramaBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{reporteRolEmpleado}")
    private ReporteRolEmpleadoBean reporteRolBean;
    private Formulario formularioReporte = new Formulario();
    private Resource reporte;
    // busquedas
    private Cabecerasrrhh cabeceraRrHh;
    private BigDecimal desdeNumerico;
    private BigDecimal hastaNumerico;
    private String textoBuscar;
    private Date desdeFecha;
    private Date hastaFecha;
    private Estudios estudio;
    private String cedula;
    private String apellidos;
    private String email;
    private String nombres;
    private Organigrama organigrama;
    private Cargosxorganigrama cargo;
    private String buscarCedula;
    private String nomCargoxOrg;
    private String empleadoMostrar;
    private Codigos genero;
    private Codigos subrogacionCodigos;
//    private Codigos tipoNombramiento;
    private Codigos tipsang;
    private Historialcargos historia;
    private Codigos ecivil;
    private Date desde;
    private Date hasta;
    private Tiposcontratos contrato;
    private Integer nombramiento;
    private int porcentaje;
    private Edificios edificio;
    private Oficinas oficina;
    // fin busquedas

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        setAnio(c.get((Calendar.YEAR)));
        setMes(c.get((Calendar.MONTH)) + 1);
        c.set(Calendar.MONTH, mes);
        c.add(Calendar.DATE, -1);
        desde = configuracionBean.getConfiguracion().getPvigente();
        hasta = c.getTime();

        Calendar co = Calendar.getInstance();
        co.setTime(new Date());
        mes = co.get(Calendar.MONTH) + 1;

    }

    // fin perfiles
    /**
     * Creates a new instance of NovedadesxconceptoEmpleadoBean
     */
    public RolEmpleadoBean() {
    }

    public double traerRmu(Empleados e) {
        double rmu = e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().floatValue();
        int diasNormales = Constantes.DIAS_POR_MES;
        double retorno = 0;
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.setTime(desde);
//        inicioMes.set(Calendar.MINUTE, 0);
//        inicioMes.set(Calendar.YEAR, anio);
//        inicioMes.set(Calendar.MONTH, mes - 1);
//        inicioMes.set(Calendar.HOUR_OF_DAY, 0);
//        inicioMes.set(Calendar.DATE, 1);
        Calendar finMes = Calendar.getInstance();
        finMes.setTime(hasta);
//        finMes.set(Calendar.MINUTE, 0);
//        finMes.set(Calendar.YEAR, anio);
//        finMes.set(Calendar.MONTH, mes);
//        finMes.set(Calendar.HOUR_OF_DAY, 0);
//        finMes.set(Calendar.DATE, 1);
//        finMes.add(Calendar.DATE, -1);
//        String where = " o.empleado=:empleado and o.desde<=:iniciomes"
        String where = " o.empleado=:empleado  and o.empleado.activo=true"
                + " and (o.hasta between :desde and :hasta or o.hasta is null) "
                + " and o.tipoacciones.parametros='RMU'";
//                + " and o.aprobacion=true";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put(";orden", "o.desde desc");
        parametros.put("empleado", e);
        parametros.put("desde", inicioMes.getTime());
//        parametros.put("iniciomes", inicioMes.getTime());
        parametros.put("hasta", finMes.getTime());
        long primeroMes = inicioMes.getTimeInMillis();
        long ultimoMes = finMes.getTimeInMillis();
        Calendar auxiliar = Calendar.getInstance();

        try {
            List<Historialcargos> lHistorial = ejbHistorial.encontarParametros(parametros);
            for (Historialcargos h : lHistorial) {
                if (h.getDesde().getTime() > finMes.getTime().getTime()) {
                    // necesito el sueldo anterior
                    rmu = h.getCargoant().getCargo().getEscalasalarial().getSueldobase().floatValue();
                }
                if (h.getDesde().getTime() < inicioMes.getTime().getTime()) {
                    rmu = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue();
                    return rmu;
                }
                int diasCambio = 0;
                long desdeLargo = h.getDesde().getTime();
                auxiliar.setTime(h.getDesde());
                int diaDesde = auxiliar.get(Calendar.DATE);
                double nuevoSueldo = h.getCargoant().getCargo().getEscalasalarial().getSueldobase().floatValue();
                if (h.getHasta() == null) { // no se sabe cuando retorna a su puesto original
                    if (desdeLargo <= primeroMes) { // todo el mes
                        diasCambio = Constantes.DIAS_POR_MES;
                        diasNormales = 0;
                    } else { // parte del mes es normal
                        diasNormales = Constantes.DIAS_POR_MES - diaDesde + 1;
                        diasCambio = Constantes.DIAS_POR_MES - diasNormales;
                    }
                } else { // existe fecha del retorno
                    long hastaLargo = h.getHasta().getTime();
                    auxiliar.setTime(h.getHasta());
                    int diaHasta = auxiliar.get(Calendar.DATE);
                    if (desdeLargo <= primeroMes) { // inicia antes del primero
                        if (hastaLargo < ultimoMes) { // no termina el mes con ese sueldo
                            diasNormales = diaHasta - 1;
                            diasCambio = Constantes.DIAS_POR_MES - diasNormales;
                        } else {// pasa de todo el mes
                            diasNormales = 0;
                            diasCambio = Constantes.DIAS_POR_MES;
                        }
                    } else { // esta entre el mes
                        if (hastaLargo < ultimoMes) { // no termina el mes con ese sueldo
                            diasNormales = diaHasta - diaDesde - 1;
                            diasCambio = Constantes.DIAS_POR_MES - diasNormales;
                        } else {// pasa de todo el mes
                            diasNormales = Constantes.DIAS_POR_MES - diaDesde;
                            diasCambio = Constantes.DIAS_POR_MES - diasNormales;
                        }
                    }
                } // fin hasta == null
                retorno += (rmu * diasNormales + nuevoSueldo * diasCambio) / Constantes.DIAS_POR_MES;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (retorno
                == 0) {
            retorno = rmu;
//            if (e.getFechasalida() != null) {
//                Calendar fechaSalida = Calendar.getInstance();
//                fechaSalida.setTime(e.getFechasalida());
//                diasNormales = fechaSalida.get(Calendar.DATE);
//                retorno=retorno*diasNormales/Constantes.DIAS_POR_MES;
//            }
        }
        retorno = Math.rint(retorno * 100) / 100;
        return retorno;
    }

    /**
     *
     * @param e
     * @return
     */
    public RmuEncargoSubrogacion traerRmuEncargos(Empleados e) {
        double sueldoActual = e.getCargoactual().getCargo().getEscalasalarial().getSueldobase().floatValue();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int diasNormales = Constantes.DIAS_POR_MES;
        List<RmuEncargoSubrogacion> listadoSueldos = new LinkedList<>();
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.setTime(desde);
        Calendar finMes = Calendar.getInstance();
        finMes.setTime(hasta);
        // coloco el sueldo actual dia dia en el mes los 30 dias debo ver que pas asi pasa el 25 del mes siguiente
        for (int i = 1; i <= diasNormales; i++) {
            RmuEncargoSubrogacion r = new RmuEncargoSubrogacion();
            r.setRmu(sueldoActual / 30);
            r.setDia(inicioMes.get(Calendar.DATE));
//            inicioMes.add(Calendar.DATE, 1);
            listadoSueldos.add(r);
        }
        inicioMes.setTime(desde);

        String where = " o.empleado=:empleado and o.empleado.activo=true"
                + " and (o.hasta between :desde and :hasta or o.hasta is null) "
                + " and o.tipoacciones.parametros='RMU'";
//                + " and o.aprobacion=true";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put(";orden", "o.desde desc");
        parametros.put("empleado", e);
        parametros.put("desde", inicioMes.getTime());
//        parametros.put("iniciomes", inicioMes.getTime());
        parametros.put("hasta", finMes.getTime());
        Calendar desdeNueva = Calendar.getInstance();
        try {
            List<Historialcargos> lHistorial = ejbHistorial.encontarParametros(parametros);
            for (Historialcargos h : lHistorial) {
                desdeNueva.setTime(h.getDesde());
                if (!e.getCargoactual().equals(h.getCargo())) {
                    if (h.getDesde().getTime() > finMes.getTime().getTime()) {
                        // necesito el sueldo anterior para todo el mes
                        // ver si es el mismo mes o otro mes del que se esta procesando
                        Calendar c = Calendar.getInstance();
                        c.setTime(h.getDesde());
                        int desdeCuando = 0;
                        int mesCuando = c.get(Calendar.MONTH) + 1;
                        if (mesCuando == mes) {
                            diasNormales = c.get(Calendar.DATE) - 1;
                        }
                        sueldoActual = h.getCargoant().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                        for (int i = 0; i <= diasNormales - 1; i++) {
                            RmuEncargoSubrogacion r = new RmuEncargoSubrogacion();
                            r.setRmu(sueldoActual);
                            listadoSueldos.set(i, r);
                        }
                    } else {
                        // quiere decir que desde puede ser mayor que inicio de mes
                        if (h.getDesde().getTime() > inicioMes.getTime().getTime()) {
                            // son algunos dias  que cambio de sueldo el sueldo desde 
                            //debe ser el anterior hasta llegar a la fecha del desde
                            Calendar cambia = Calendar.getInstance();
                            cambia = inicioMes;
                            // pongo el primer dia del mes
                            ///////////////////OJOOJOJOJOJOJ/////////////
                            cambia.set(Calendar.DATE, 1);
                            int i = 0;
                            sueldoActual = h.getCargoant().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                            while (h.getDesde().getTime() > cambia.getTimeInMillis()) {
                                RmuEncargoSubrogacion r = new RmuEncargoSubrogacion();
                                r.setRmu(sueldoActual);
                                listadoSueldos.set(i, r);
                                if (i < 29) {
                                    i++;
                                }
                                cambia.add(Calendar.DATE, 1);
                            }
                        }
                    }
                }
            }// fin for
            // encargos y subrogaciones
            where = "o.tipoacciones.parametros in ('ENCARGO','SUBROGACION') "
                    + " and o.empleado=:empleado  and o.empleado.activo=true and (o.desde <= :inicioMes1 or  o.desde <=:finMes1)"
                    + " and  (o.hasta between :inicioMes and :finMes "
                    + "or o.hasta is null or o.hasta >:finMes2)";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.desde desc");
            parametros.put("empleado", e);
            parametros.put("inicioMes1", desde);
            parametros.put("finMes", hasta);
            parametros.put("finMes1 ", hasta);
            parametros.put("finMes2 ", hasta);
            parametros.put("inicioMes ", desde);

            List<Historialcargos> listaHistorial = ejbHistorial.encontarParametros(parametros);
            subrogacionCodigos = null;
//            if ((mes==4) && (anio==2018)){
//                Calendar cal=Calendar.getInstance();
//                cal.set(2018, 3, 24);
//                hasta=cal.getTime();
//            }
            for (Historialcargos h : listaHistorial) {
                subrogacionCodigos = h.getTipoacciones();
                inicioMes.setTime(desde);
                inicioMes.set(Calendar.HOUR_OF_DAY, 0);
                inicioMes.set(Calendar.MINUTE, 0);
                inicioMes.set(Calendar.SECOND, 0);
                finMes.setTime(hasta);
                finMes.set(Calendar.HOUR_OF_DAY, 0);
                finMes.set(Calendar.MINUTE, 0);
                finMes.set(Calendar.SECOND, 0);
                if ((h.getHasta() == null) || (h.getHasta().getTime() > finMes.getTimeInMillis())) {
                    // termina el mes y sigue encargado
                    // hay que ver si empieza antes o despues del inicio de mes
                    if (h.getDesde().getTime() <= inicioMes.getTimeInMillis()) {
                        //es todo el mes 
                        sueldoActual = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                        for (int i = 0; i <= diasNormales - 1; i++) {
                            RmuEncargoSubrogacion r = listadoSueldos.get(i);
//                            r.setRmu(Math.abs(r.getRmu()-sueldoActual));
                            if (h.getTipoacciones().getParametros().equals("SUBROGACION")) {
                                if (r.getSubrogacion() == 0) {
                                    r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                }
                            } else {
                                if (r.getEncargo() == 0) {
                                    r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
                                }
                            }
                            listadoSueldos.set(i, r);
                        }
                    } else {
//                        hay que ver desde cuando le toca hasta fin de mes
                        Calendar cambia = Calendar.getInstance();
                        cambia = inicioMes;
                        cambia.set(Calendar.HOUR_OF_DAY, 0);
                        cambia.set(Calendar.MINUTE, 0);
                        cambia.set(Calendar.SECOND, 0);
                        cambia.set(Calendar.MILLISECOND, 0);
//                         cambia.add(Calendar.DATE, -1);
                        int k = 0;
                        while (h.getDesde().getTime() > cambia.getTimeInMillis()) {
                            k++;
                            cambia.add(Calendar.DATE, 1);
                        }
                        sueldoActual = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                        for (int i = k; i <= diasNormales - 1; i++) {
                            RmuEncargoSubrogacion r = listadoSueldos.get(i);
//                            r.setRmu(Math.abs(r.getRmu()-sueldoActual));
                            if (h.getTipoacciones().getParametros().equals("SUBROGACION")) {
                                if (r.getSubrogacion() == 0) {
                                    r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                }
                            } else {
                                if (r.getEncargo() == 0) {
                                    r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
                                }
                            }
                            listadoSueldos.set(i, r);
                        }
                    }

                } else { // no termina el mes
                    // hay que ver si es una fecha anterior al inicio
                    int inicioMentero = inicioMes.get(Calendar.MONTH);
                    int inicioDentero = inicioMes.get(Calendar.DATE);
                    int inicioAentero = inicioMes.get(Calendar.YEAR);
                    Calendar ch = Calendar.getInstance();
                    ch.setTime(h.getHasta());
                    int hastaMentero = ch.get(Calendar.MONTH);
                    int hastaDentero = ch.get(Calendar.DATE);
                    int hastaAentero = ch.get(Calendar.YEAR);
                    boolean unDia = ((inicioMentero == hastaMentero) && (inicioDentero == hastaDentero) && (inicioAentero == hastaAentero));
                    if (h.getHasta().getTime() > inicioMes.getTimeInMillis()) {
                        // esta en el periodo correcto

                        if (h.getDesde().getTime() <= inicioMes.getTimeInMillis()) {
                            // desde el inicio de mes  
                            Calendar cambia = Calendar.getInstance();
                            Calendar hastaClendario = Calendar.getInstance();
                            hastaClendario.setTime(h.getHasta());
                            hastaClendario.set(Calendar.HOUR_OF_DAY, 0);
                            hastaClendario.set(Calendar.MINUTE, 0);
                            hastaClendario.set(Calendar.SECOND, 0);

                            cambia.setTime(desde);
                            cambia.set(Calendar.HOUR_OF_DAY, 0);
                            cambia.set(Calendar.MINUTE, 0);
                            cambia.set(Calendar.SECOND, 0);
//                            cambia.add(Calendar.DATE, -1);
                            int k = 0;
                            if (h.getCargo().equals(e.getCargoactual())) {

                                sueldoActual = h.getCargoant().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                            } else {
                                sueldoActual = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                            }
                            // ver por que no da tres vueltas
                            int cuantasVeces = 0;
                            int diferenciaDias = ejbCabecerasempleados.diferenciaDosFechas(hastaClendario, cambia);

//                            System.out.println("Desde " + sdf.format(cambia.getTime()) + " Hasta " + sdf.format(hastaClendario.getTime()));
////                            diferenciaDias = k1;
//                            System.out.println("Días " + diferenciaDias );
                            // por que 
                            for (int diasInt = 0; diasInt < diferenciaDias; diasInt++) {

                                RmuEncargoSubrogacion r = listadoSueldos.get(k);
                                double rmuTraido = r.getRmu();
                                double aPoner = rmuTraido - sueldoActual;

//                                r.setRmu(Math.abs(r.getRmu()));
                                if (h.getTipoacciones().getParametros().equals("SUBROGACION")) {
                                    if (aPoner == 0) {
//                                        r.setRmu(Math.abs(0));
//                                        r.setSubrogacion(Math.abs(sueldoActual));

                                    } else {
                                        if (r.getSubrogacion() == 0) {
                                            r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                        }
                                    }
                                } else {
                                    if (aPoner == 0) {
//                                        r.setRmu(Math.abs(0));
//                                        r.setEncargo(Math.abs(sueldoActual));

                                    } else {
                                        if (r.getEncargo() == 0) {
                                            r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
                                        }
                                    }

                                }
                                listadoSueldos.set(k, r);
                                cambia.add(Calendar.DATE, 1);
                                if (k < 29) {
                                    k++;
                                }
                                cuantasVeces++;
                            }
                            System.out.println("Cuantas " + cuantasVeces);
                        } else {
                            // parte del mes
                            Calendar hastaClendario = Calendar.getInstance();
                            hastaClendario.setTime(h.getDesde());
                            hastaClendario.set(Calendar.HOUR_OF_DAY, 0);
                            hastaClendario.set(Calendar.MINUTE, 0);
                            hastaClendario.set(Calendar.SECOND, 0);
                            Calendar cambia = Calendar.getInstance();
                            cambia = inicioMes;
                            cambia.set(Calendar.HOUR_OF_DAY, 0);
                            cambia.set(Calendar.MINUTE, 0);
                            cambia.set(Calendar.SECOND, 0);
                            cambia.set(Calendar.MILLISECOND, 0);
                            cambia.add(Calendar.DATE, -1);
                            int diferenciaDias = ejbCabecerasempleados.diferenciaDosFechas(hastaClendario, cambia);
                            System.out.println("Desde " + sdf.format(h.getDesde()) + " Hasta " + sdf.format(h.getHasta()));
//                            int diferenciaDias = (int) ((h.getDesde().getTime() - cambia.getTimeInMillis()) / (1000 * 60 * 60 * 24)) + 1;
                            int k = 0;
                            while (h.getDesde().getTime() > cambia.getTimeInMillis()) {
                                k++;
                                cambia.add(Calendar.DATE, 1);
                            }
                            Calendar cHasta = Calendar.getInstance();
                            cHasta.setTime(h.getHasta());
                            int diaHasta = cHasta.get(Calendar.DATE);
                            int diaFinal = finMes.get(Calendar.DATE);
                            if (diaHasta == diaFinal) {
                                RmuEncargoSubrogacion r = new RmuEncargoSubrogacion();
                                r.setRmu(sueldoActual / 30);
                                r.setDia(inicioMes.get(Calendar.DATE));
                                listadoSueldos.add(r);
                            }
                            k--;
                            sueldoActual = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
//                            while (cambia.getTimeInMillis() <= h.getHasta().getTime()) {
                            while (cambia.getTimeInMillis() <= h.getHasta().getTime()) {
                                RmuEncargoSubrogacion r = listadoSueldos.get(k);
//                                r.setRmu(Math.abs(r.getRmu()));
                                double rmuActual = r.getRmu();
                                double valorAponer = rmuActual - sueldoActual;
                                if (h.getTipoacciones().getParametros().equals("SUBROGACION")) {
                                    if (r.getSubrogacion() == 0) {
//                                        r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                        r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                    }
                                } else {
                                    if (r.getEncargo() == 0) {
                                        r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
                                    }
                                }
                                listadoSueldos.set(k, r);
                                cambia.add(Calendar.DATE, 1);
                                if (k <= 29) {
                                    k++;
                                } else {
                                    r.setRmu(0);
                                }
                            }
                        }
                    } else if (unDia) {
                        int diferenciaDias = 1;
                        RmuEncargoSubrogacion r = listadoSueldos.get(0);
                        double rmuActual = r.getRmu();
                        sueldoActual = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                        double valorAponer = (rmuActual - sueldoActual);
                        if (h.getTipoacciones().getParametros().equals("SUBROGACION")) {
                            if (r.getSubrogacion() == 0) {
                                r.setSubrogacion(Math.abs(valorAponer));
                            }
                        } else {
                            if (r.getEncargo() == 0) {
                                r.setEncargo(Math.abs(valorAponer));
                            }
                        }
                        listadoSueldos.set(0, r);
                    }

                }
                desdeNueva.setTime(h.getDesde());
            }//ver
            // fin encargo y subrogaciones

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        RmuEncargoSubrogacion retorno = new RmuEncargoSubrogacion();
        for (RmuEncargoSubrogacion r : listadoSueldos) {

            retorno.setEncargo(retorno.getEncargo() + r.getEncargo());
            retorno.setSubrogacion(retorno.getSubrogacion() + r.getSubrogacion());
            retorno.setRmu(retorno.getRmu() + r.getRmu());
            if (r.getRmu() > 0) {
                retorno.setCuantosRmu(retorno.getCuantosRmu() + 1);
            }
            if (r.getEncargo() > 0) {
                retorno.setCuantosEncargo(retorno.getCuantosEncargo() + 1);
            }
            if (r.getSubrogacion() > 0) {
                retorno.setCuantosSubrogacion(retorno.getCuantosSubrogacion() + 1);
            }
        }
        return retorno;
    }

    private void cargar() {
        try {
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mes - 1);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.MONTH, mes);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.add(Calendar.DATE, -1);
            String where = "o.activo=true and o.cargoactual is not null "
                    + " and (o.fechasalida is null or o.fechasalida>=:iniciomes) and "
                    + "  (o.fechaingreso<:finmes )";
            String whereCampo = "o.empleado.activo=true and o.empleado.cargoactual is not null and "
                    + " and (o.empleado.fechasalida is null or o.empleado.fechasalida>:iniciomes) and "
                    + "  (o.empleado.fechaingreso<:finmes )";
            Map parametros = new HashMap();
            parametros.put("iniciomes", inicioMes.getTime());
            parametros.put("finmes", finMes.getTime());

            if (!buscarCedula.isEmpty() && getBuscarCedula() != null) {
                where += " and upper(o.entidad.pin)like :pin ";
                whereCampo += " and upper(o.empleado.entidad.pin) like :pin ";
                parametros.put("pin", getBuscarCedula().toUpperCase());
            }

            if (!nombres.isEmpty() && getNombres() != null) {
                where += " and upper(o.entidad.nombres) like :noms ";
                whereCampo += " and upper(o.empleado.entidad.nombres) like :noms ";
                parametros.put("noms", getNombres().toUpperCase() + "%");
            }

            if (!apellidos.isEmpty() && getApellidos() != null) {
                where += " and upper(o.entidad.apellidos) like :ape ";
                whereCampo += " and upper(o.empleado.entidad.apellidos) like :ape ";
                parametros.put("ape", getApellidos().toUpperCase() + "%");
            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.entidad.apellidos");

            listaEmpleados = new LinkedList<>();
            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
            List<Empleados> listaEmpleadoses = ejbEmpleados.encontarParametros(parametros);

            for (Empleados e : listaEmpleadoses) {
                where = "o.tipoacciones.parametros like 'LICENC%'"
                        + " and o.empleado=:empleado and o.empleado.activo=true "
                        + " and  o.hasta > :finMes ";
//                                    + "or o.hasta is null or o.hasta >:finMes2)";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put(";orden", "o.desde desc");
                parametros.put("empleado", e);
                parametros.put("finMes", finMes.getTime());
                List<Historialcargos> lista = ejbHistorial.encontarParametros(parametros);

                Date salida = null;
                boolean pone = true;
                for (Historialcargos h : lista) {
                    if (h.getDesde().getTime() < inicioMes.getTimeInMillis()) {
                        pone = false;
                    } else {
                        e.setFechasalida(h.getDesde());
                        e.setIncluyeDia(1);
                        pone = true;
                    }
                }
                if (pone) {
                    listaEmpleados.add(e);
                }
            }
//                    int total = ejbEmpleados.contar(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public String buscar() {
        if (configuracionBean.getConfiguracion().getGenerandoNomina() == null) {
            configuracionBean.getConfiguracion().setGenerandoNomina(Boolean.FALSE);
        }
        if (configuracionBean.getConfiguracion().getGenerandoNomina()) {
            MensajesErrores.advertencia("Ya se esta generando la nomina por otro usuario");
            return null;
        }
        try {
            Configuracion c = configuracionBean.getConfiguracion();
            c.setGenerandoNomina(Boolean.TRUE);
            ejbConfig.edit(c, parametrosSeguridad.getLogueado().getUserid());
            ejbCon.crearSuperProceos();

            // generar todo el proceso de rol
            // primero empezar con la RMU y los encargos
            // traer los empleados
            // traer los formatos  que se necesita
            Map parametros = new HashMap();
            Compromisos compromiso = null;
            String where = "o.anio=:anio and o.mes=:mes and o.liquidacion=false and o.compromiso is not null "
                    + " and o.concepto.codigo!='BONOCOMP' and o.concepto.titulo not in ('Decimo Tercero Acumulado','Décimo Cuarto Acumulado')";
            // traer compromiso y ver si ya esta impreso
            Calendar cParaMesAnio = Calendar.getInstance();

            cParaMesAnio.set(Calendar.YEAR, anio);
            int diainicio = configuracionBean.getConfiguracion().getInicionomina() == null ? 24 : configuracionBean.getConfiguracion().getInicionomina();
            int diafin = configuracionBean.getConfiguracion().getFinnomina() == null ? 24 : configuracionBean.getConfiguracion().getFinnomina();
            if (diainicio == 1) {
                // dia fin es ultimo dia del mes
                cParaMesAnio.set(Calendar.MONTH, mes);
                cParaMesAnio.set(Calendar.DATE, 1);
                cParaMesAnio.add(Calendar.DATE, -1);
            } else {
                cParaMesAnio.set(Calendar.MONTH, mes - 1);
                cParaMesAnio.set(Calendar.DATE, diafin);
            }

            hasta = cParaMesAnio.getTime();
            cParaMesAnio = Calendar.getInstance();
            cParaMesAnio.set(Calendar.MONTH, diainicio == 1 ? mes - 1 : mes - 2);
            cParaMesAnio.set(Calendar.YEAR, anio);
            cParaMesAnio.set(Calendar.DATE, diainicio);
            desde = cParaMesAnio.getTime();
//            cParaMesAnio.setTime(hasta);
//            mes = cParaMesAnio.get(Calendar.MONTH) + 1;
//            anio = cParaMesAnio.get(Calendar.YEAR);
            if (hasta.before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Mes menor a periodo vigente");
                c.setGenerandoNomina(Boolean.FALSE);
                ejbConfig.edit(c, parametrosSeguridad.getLogueado().getUserid());
                return null;
            }
            parametros.put("where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            int total = ejbPagos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto, no se puede reprocesar rol");
                c.setGenerandoNomina(Boolean.FALSE);
                ejbConfig.edit(c, parametrosSeguridad.getLogueado().getUserid());
                return null;
            }
            Calendar inicioMes = Calendar.getInstance();
            inicioMes.set(Calendar.MINUTE, 0);
            inicioMes.set(Calendar.YEAR, anio);
            inicioMes.set(Calendar.MONTH, mes - 1);
            inicioMes.set(Calendar.HOUR_OF_DAY, 0);
            inicioMes.set(Calendar.DATE, 1);
            Calendar finMes = Calendar.getInstance();
            finMes.set(Calendar.MINUTE, 0);
            finMes.set(Calendar.YEAR, anio);
            finMes.set(Calendar.MONTH, mes);
            finMes.set(Calendar.HOUR_OF_DAY, 0);
            finMes.set(Calendar.DATE, 1);
            finMes.add(Calendar.DATE, -1);

            cargar();
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            Date dia = new Date();
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }

            int totalEmpleados = listaEmpleados.size();
            int numeroEmpleado = 0;
            empleadoMostrar = "Un momento por favor";
//            UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
            for (Empleados e : listaEmpleados) {
                porcentaje = (int) (++numeroEmpleado * 100 / totalEmpleados);
                empleadoMostrar = e.getEntidad().toString();
                System.out.println(String.valueOf(porcentaje) + " - Empleado : " + empleadoMostrar);
//                viewRoot.set
                RmuEncargoSubrogacion rmuCalculado1 = traerRmuEncargos(e);
                double rmuCalculado = rmuCalculado1.getRmu();
                double rmuDiario = rmuCalculado / Constantes.DIAS_POR_MES;
                double ingresos = 0;
                double totalIngresos = 0;
                double totalEgresos = 0;
                double encargos = rmuCalculado1.getEncargo();
                double subrogaciones = rmuCalculado1.getSubrogacion();
                int dias = 0;
                if (e.getPartida().length() < 6) {
                    MensajesErrores.fatal("No corresponde partida a formato buscado en empleado : "
                            + e.getEntidad().toString());
                    c.setGenerandoNomina(Boolean.FALSE);
                    ejbConfig.edit(c, parametrosSeguridad.getLogueado().getUserid());
                    return null;
                }
                String partida = e.getPartida().substring(0, 2);
                String cuenta = e.getPartida().substring(2, 6);

                List<AuxiliarRol> listaAxuiliar = new LinkedList<>();
                // ver encargos

                dias = Constantes.DIAS_POR_MES;
                // dias trabajados
                if (e.getFechaingreso().after(inicioMes.getTime())) {
                    // no trabajo completo
                    dias = (int) Math.round((e.getFechaingreso().getTime() - inicioMes.getTimeInMillis()) / Constantes.MILLSECS_PER_DAY) + 1;
                    rmuCalculado = rmuDiario * (Constantes.DIAS_POR_MES - dias);
                    dias = (Constantes.DIAS_POR_MES - dias);
                } else {
                    if (e.getFechasalida() != null) {
                        Calendar fechaSalida = Calendar.getInstance();
                        fechaSalida.setTime(e.getFechasalida());
                        int anioSalida = fechaSalida.get(Calendar.YEAR);
                        int mesSalida = fechaSalida.get(Calendar.MONTH) + 1;
                        if ((anio == anioSalida) && (mes == mesSalida)) {
                            dias = fechaSalida.get(Calendar.DATE) - e.getIncluyeDia();
                            if (dias > 30) {
                                dias = 30;
                            }
                            if ((mes == 2) && (dias >= 28)) {
                                dias = 30;
                            }
                            rmuCalculado = rmuDiario * (dias);
                        }
                    }
                }
                int diasNovedadTotal = rmuCalculado1.getCuantosEncargo() + rmuCalculado1.getCuantosSubrogacion();
                int diasNovedad = rmuCalculado1.getCuantosEncargo() + rmuCalculado1.getCuantosSubrogacion();
                // aqui estaba lo de los encargos y subrogaciones

                // ver licencias sin sueldo y ver el numero de dias que trabajo
                boolean conSueldo = true;
                parametros = new HashMap();

                parametros.put(";where", "o.empleado=:empleado and o.empleado.activo=true"
                        + " and o.tipo.rmu=true and o.fretorno is null");
                parametros.put("empleado", e);
                List<Licencias> listaLicencias = ejbLicencia.encontarParametros(parametros);
                for (Licencias l : listaLicencias) {
                    Calendar fretorno = Calendar.getInstance();
                    fretorno.setTime(l.getInicio());
                    int anioLic = fretorno.get(Calendar.YEAR);
                    if (anioLic != anio) {
                        conSueldo = false;
                    } else {
                        int mesLic = fretorno.get(Calendar.MONTH) + 1;
                        if (mesLic == mes) {
                            // ver los dias
                            int diasLic = fretorno.get(Calendar.DATE);
                            dias = 30 - diasLic;
                            // el rmu en numero de dias
                            rmuCalculado = rmuCalculado / 30 * dias;
                            encargos = encargos / 30 * dias;
                            subrogaciones = subrogaciones / 30 * dias;
//                            encargo=encargo/30*dias;
                        } else {
                            conSueldo = false;
                        }
                    }

                }
                // fin licencias
                ingresos = encargos + subrogaciones + rmuCalculado;

                //
                if (conSueldo) {
                    totalIngresos = rmuCalculado + encargos + subrogaciones;

                    int diasSubrogados = 0;

                    if ((subrogaciones > 0) || (encargos > 0)) {
                        diasSubrogados = diasNovedadTotal;
                    }
                    // ejecutar el Sp de conceptos
                    ejbCon.ejecutaSp(((float) rmuCalculado), ((float) (subrogaciones)),
                            configuracionBean.getConfiguracion().getSmv(),
                            dias, diasSubrogados, e.getId(), finMes.getTime(), dia, (float) encargos);

                }// fin del if sin sueldo
            } // fin del for empleados
            if (listaEmpleados.size() > 1) {
                ejbPagos.borrarNominaDia(mes, anio, dia);
            }

            formulario.insertar();
//        exportar();
            c.setGenerandoNomina(Boolean.FALSE);
            ejbConfig.edit(c, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String exportar() {
        try {
            listaConceptosIngresos = getReporteRolBean().getIngresosSobre();
            listaConceptosEgresos = getReporteRolBean().getEgresosSobre();
            List<Conceptos> listaConceptosProvisiones = getReporteRolBean().getProviciones();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            DocumentoXLS xls = new DocumentoXLS("Rol de Pagos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cédula"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Empleado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Subactividad"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Modalidad de Contratación"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cargo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proceso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha Ingreso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "RMU"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ENCARGO"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "SUBROGACIONES"));
            for (Conceptos c : listaConceptosIngresos) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL INGRESOS"));
            for (Conceptos c : listaConceptosEgresos) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "ANTICIPOS EMPLEADOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL EGRESOS"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "TOTAL"));
            for (Conceptos c : listaConceptosProvisiones) {
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Provisión " + c.getNombre()));
            }
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Total Provisiones"));
            xls.agregarFila(campos, true);
            getReporteRolBean().setAnio(anio);
            getReporteRolBean().setMesDesde(mes);
            for (Empleados e : listaEmpleados) {
                campos = new LinkedList<>();
                double tingresos = 0;
                double tegresos = 0;
                double tprovisiones = 0;
                double rmu = getReporteRolBean().getRmu(e);
                double encargos = getReporteRolBean().getEncargos(e);
                double subrogaciones = getReporteRolBean().getSubrogaciones(e);
                double anticipos = getReporteRolBean().getAnticipos(e);
                tingresos = rmu + subrogaciones + encargos;
                tegresos = anticipos;
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().getPin()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getEntidad().toString()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getProyecto() == null ? "" : e.getProyecto().toString()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getPartida()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getTipocontrato() == null ? ""
                        : e.getTipocontrato().getCodigo()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getCargo().getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, e.getCargoactual().getOrganigrama().getNombre()));
                campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(e.getFechaingreso())));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, rmu));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, encargos));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, subrogaciones));
                for (Conceptos c : listaConceptosIngresos) {
                    double valor = getReporteRolBean().getPagoconcepto(e, c);
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                    double cuadre = Math.round(valor * 100);
                    double dividido = cuadre / 100;
                    tingresos += dividido;
                }
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tingresos));
                for (Conceptos c : listaConceptosEgresos) {
                    double valor = getReporteRolBean().getPagoconcepto(e, c);
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                    double cuadre = Math.round(valor * 100);
                    double dividido = cuadre / 100;
                    tegresos += dividido;
                }

                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, anticipos));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tegresos));
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tingresos - tegresos));
                for (Conceptos c : listaConceptosProvisiones) {
                    double valor = getReporteRolBean().getPagoconcepto(e, c);
                    campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, valor));
                    double cuadre = Math.round(valor * 100);
                    double dividido = cuadre / 100;
                    tprovisiones += dividido;
                }
                campos.add(new AuxiliarReporte("double", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, tprovisiones));
                xls.agregarFila(campos, false);
            }
            reporte = xls.traerRecurso();
            formularioReporte.insertar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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

//    /**
//     * @return the listaPagos
//     */
//    public List<Pagosempleados> getListaPagos() {
//        return listaPagos;
//    }
//
//    /**
//     * @param listaPagos the listaPagos to set
//     */
//    public void setListaPagos(List<Pagosempleados> listaPagos) {
//        this.listaPagos = listaPagos;
//    }
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
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
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
     * @return the listaEmpleados
     */
    public List<Empleados> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<Empleados> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the listaConceptosIngresos
     */
    public List<Conceptos> getListaConceptosIngresos() {
        return listaConceptosIngresos;
    }

    /**
     * @param listaConceptosIngresos the listaConceptosIngresos to set
     */
    public void setListaConceptosIngresos(List<Conceptos> listaConceptosIngresos) {
        this.listaConceptosIngresos = listaConceptosIngresos;
    }

    /**
     * @return the listaConceptosEgresos
     */
    public List<Conceptos> getListaConceptosEgresos() {
        return listaConceptosEgresos;
    }

    /**
     * @param listaConceptosEgresos the listaConceptosEgresos to set
     */
    public void setListaConceptosEgresos(List<Conceptos> listaConceptosEgresos) {
        this.listaConceptosEgresos = listaConceptosEgresos;
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
     * @return the cabeceraRrHh
     */
    public Cabecerasrrhh getCabeceraRrHh() {
        return cabeceraRrHh;
    }

    /**
     * @param cabeceraRrHh the cabeceraRrHh to set
     */
    public void setCabeceraRrHh(Cabecerasrrhh cabeceraRrHh) {
        this.cabeceraRrHh = cabeceraRrHh;
    }

    /**
     * @return the desdeNumerico
     */
    public BigDecimal getDesdeNumerico() {
        return desdeNumerico;
    }

    /**
     * @param desdeNumerico the desdeNumerico to set
     */
    public void setDesdeNumerico(BigDecimal desdeNumerico) {
        this.desdeNumerico = desdeNumerico;
    }

    /**
     * @return the hastaNumerico
     */
    public BigDecimal getHastaNumerico() {
        return hastaNumerico;
    }

    /**
     * @param hastaNumerico the hastaNumerico to set
     */
    public void setHastaNumerico(BigDecimal hastaNumerico) {
        this.hastaNumerico = hastaNumerico;
    }

    /**
     * @return the textoBuscar
     */
    public String getTextoBuscar() {
        return textoBuscar;
    }

    /**
     * @param textoBuscar the textoBuscar to set
     */
    public void setTextoBuscar(String textoBuscar) {
        this.textoBuscar = textoBuscar;
    }

    /**
     * @return the desdeFecha
     */
    public Date getDesdeFecha() {
        return desdeFecha;
    }

    /**
     * @param desdeFecha the desdeFecha to set
     */
    public void setDesdeFecha(Date desdeFecha) {
        this.desdeFecha = desdeFecha;
    }

    /**
     * @return the hastaFecha
     */
    public Date getHastaFecha() {
        return hastaFecha;
    }

    /**
     * @param hastaFecha the hastaFecha to set
     */
    public void setHastaFecha(Date hastaFecha) {
        this.hastaFecha = hastaFecha;
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the nombres
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * @return the organigrama
     */
    public Organigrama getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    /**
     * @return the cargo
     */
    public Cargosxorganigrama getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargosxorganigrama cargo) {
        this.cargo = cargo;
    }

    /**
     * @return the buscarCedula
     */
    public String getBuscarCedula() {
        return buscarCedula;
    }

    /**
     * @param buscarCedula the buscarCedula to set
     */
    public void setBuscarCedula(String buscarCedula) {
        this.buscarCedula = buscarCedula;
    }

    /**
     * @return the genero
     */
    public Codigos getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Codigos genero) {
        this.genero = genero;
    }

    /**
     * @return the tipsang
     */
    public Codigos getTipsang() {
        return tipsang;
    }

    /**
     * @param tipsang the tipsang to set
     */
    public void setTipsang(Codigos tipsang) {
        this.tipsang = tipsang;
    }

    /**
     * @return the historia
     */
    public Historialcargos getHistoria() {
        return historia;
    }

    /**
     * @param historia the historia to set
     */
    public void setHistoria(Historialcargos historia) {
        this.historia = historia;
    }

    /**
     * @return the ecivil
     */
    public Codigos getEcivil() {
        return ecivil;
    }

    /**
     * @param ecivil the ecivil to set
     */
    public void setEcivil(Codigos ecivil) {
        this.ecivil = ecivil;
    }

    /**
     * @return the nombramiento
     */
    public Integer getNombramiento() {
        return nombramiento;
    }

    /**
     * @param nombramiento the nombramiento to set
     */
    public void setNombramiento(Integer nombramiento) {
        this.nombramiento = nombramiento;
    }

    /**
     * @return the edificio
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    /**
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    /**
     * @return the contrato
     */
    public Tiposcontratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Tiposcontratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the nomCargoxOrg
     */
    public String getNomCargoxOrg() {
        return nomCargoxOrg;
    }

    /**
     * @param nomCargoxOrg the nomCargoxOrg to set
     */
    public void setNomCargoxOrg(String nomCargoxOrg) {
        this.nomCargoxOrg = nomCargoxOrg;
    }

    /**
     * @return the cxOrganigramaBean
     */
    public CargoxOrganigramaBean getCxOrganigramaBean() {
        return cxOrganigramaBean;
    }

    /**
     * @param cxOrganigramaBean the cxOrganigramaBean to set
     */
    public void setCxOrganigramaBean(CargoxOrganigramaBean cxOrganigramaBean) {
        this.cxOrganigramaBean = cxOrganigramaBean;
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

    public SelectItem[] getComboRangoBuscar() {
        if (cabeceraRrHh == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.texto asc");
        parametros.put(";where", "o.cabecera=:cabecerains");
        parametros.put("cabecerains", cabeceraRrHh);
        try {
            List<Rangoscabeceras> lrcab = ejbrangocabecra.encontarParametros(parametros);
            return Combos.comboToStrings(lrcab, true);
//             return listaRangocaberains;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CabecerasEmpleadosBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the porcentaje
     */
    public int getPorcentaje() {
        return porcentaje;
    }

    /**
     * @param porcentaje the porcentaje to set
     */
    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    /**
     * @return the empleadoMostrar
     */
    public String getEmpleadoMostrar() {
        return empleadoMostrar;
    }

    /**
     * @param empleadoMostrar the empleadoMostrar to set
     */
    public void setEmpleadoMostrar(String empleadoMostrar) {
        this.empleadoMostrar = empleadoMostrar;
    }

    /**
     * @return the reporteRolBean
     */
    public ReporteRolEmpleadoBean getReporteRolBean() {
        return reporteRolBean;
    }

    /**
     * @param reporteRolBean the reporteRolBean to set
     */
    public void setReporteRolBean(ReporteRolEmpleadoBean reporteRolBean) {
        this.reporteRolBean = reporteRolBean;
    }

}
