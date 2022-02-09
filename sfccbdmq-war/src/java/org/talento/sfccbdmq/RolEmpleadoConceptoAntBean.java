/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.AuxiliarRol;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.RmuEncargoSubrogacion;
import org.beans.sfccbdmq.AmortizacionesFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ConceptosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.NovedadesxempleadoFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.RangoscabecerasFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Estudios;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rangoscabeceras;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "rolEmpleadosConceptoSfccbdmqAnt")
@ViewScoped
public class RolEmpleadoConceptoAntBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pagosempleados> listaPagos;
    private List<AuxiliarRol> listaRoles;
    private List<Conceptos> listaConceptosIngresos;
    private List<Conceptos> listaConceptosEgresos;
    private List<Empleados> listaEmpleados;
    private List<AuxiliarAsignacion> listaTotales;
//    private List<Empleados> listaEmpleados;
    private Formulario formulario = new Formulario();
    @EJB
    private NovedadesxempleadoFacade ejbNovedadesxEmpleado;

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
    private CuentasFacade ejbCuentas;
    @EJB
    private CertificacionesFacade ejbCertificaciones;
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
    private Edificios edificio;
    private Oficinas oficina;
    private Conceptos concepto;
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
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");

        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        // cargar los conceptos de ingresos y de egresos totales para el reporte
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.ingreso=true and o.sobre=true and o.activo=true"
//                + " and o.vacaciones=false and o.liquidacion=false");
//        parametros.put(";orden", "o.nombre asc");
//        try {
//            listaConceptosIngresos = ejbCon.encontarParametros(parametros);
//            parametros = new HashMap();
//            parametros.put(";where", "o.ingreso=false and o.sobre=true  and o.activo=true"
//                    + " and o.vacaciones=false and o.liquidacion=false");
//            parametros.put(";orden", "o.nombre asc");
//            listaConceptosEgresos = ejbCon.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.advertencia(ex.getMessage());
//            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of NovedadesxconceptoEmpleadoBean
     */
    public RolEmpleadoConceptoAntBean() {
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
            Logger.getLogger(RolEmpleadoConceptoBean.class.getName()).log(Level.SEVERE, null, ex);
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
                finMes.set(Calendar.HOUR_OF_DAY, 0);
                finMes.set(Calendar.MINUTE, 0);
                finMes.set(Calendar.SECOND, 0);
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
                                r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                            } else {
                                r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
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
                                r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                            } else {
                                r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
                            }
                            listadoSueldos.set(i, r);
                        }
                    }

                } else { // no termina el mes
                    // hay que ver si es una fecha anterior al inicio
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
                                        r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                    }
                                } else {
                                    if (aPoner == 0) {
//                                        r.setRmu(Math.abs(0));
//                                        r.setEncargo(Math.abs(sueldoActual));

                                    } else {
                                        r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
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
                                cambia.add(Calendar.DATE, -1);
                                k--;
                            }
                            sueldoActual = h.getCargo().getCargo().getEscalasalarial().getSueldobase().floatValue() / 30;
                            while (cambia.getTimeInMillis() <= h.getHasta().getTime()) {
                                RmuEncargoSubrogacion r = listadoSueldos.get(k);
//                                r.setRmu(Math.abs(r.getRmu()));
                                double rmuActual = r.getRmu();
                                double valorAponer = rmuActual - sueldoActual;
                                if (h.getTipoacciones().getParametros().equals("SUBROGACION")) {
                                    r.setSubrogacion(Math.abs(r.getRmu() - sueldoActual));
                                } else {
                                    r.setEncargo(Math.abs(r.getRmu() - sueldoActual));
                                }
                                listadoSueldos.set(k - 1, r);
                                cambia.add(Calendar.DATE, 1);
                                if (k < 29) {
                                    k++;
                                }
                            }
                        }
                    }

                }
                desdeNueva.setTime(h.getDesde());
            }
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
                    + " and (o.fechasalida is null or o.fechasalida>:iniciomes) and "
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

            if (!email.isEmpty() && getEmail() != null) {
                where += " and upper(o.entidad.email) like :email ";
                whereCampo += " and upper(o.empleado.entidad.email) like :email ";
                parametros.put("email", getEmail().toUpperCase() + "%");
            }
            if (getCxOrganigramaBean().getOrganigramaL() != null) {
                if (!(cargo == null)) {
                    where += " and o.cargoactual=:carg ";
                    whereCampo += " and o.empleado.cargoactual= :carg ";
                    parametros.put("carg", getCargo());
                } else {
                    where += " and o.cargoactual.organigrama=:organigrama ";
                    whereCampo += " and o.empleado.cargoactual.organigrama=:organigrama ";
                    parametros.put("organigrama", getCxOrganigramaBean().getOrganigramaL());
                }
            }
            if (!(genero == null)) {
                where += " and o.entidad.genero=:genero ";
                whereCampo += " and o.empleado.entidad.genero=:genero ";
                parametros.put("genero", getGenero());
            }

            if (!(tipsang == null)) {
                where += " and o.entidad.tiposangre=:tipsag ";
                whereCampo += " and o.empleado.entidad.tiposangre=:tipsag ";
                parametros.put("tipsag", getTipsang());
            }
            if (!(ecivil == null)) {
                where += " and o.entidad.estadocivil=:ecivil ";
                whereCampo += " and o.empleado.entidad.estadocivil=:ecivil ";
                parametros.put("ecivil", getEcivil());
            }
//            if (getDesde() != null || getHasta() != null) {
//                where += " and o.entidad.fecha between :desde and :hasta ";
//                whereCampo += " and o.empleado.entidad.fecha between :desde and :hasta ";
//                parametros.put("desde", getDesde());
//                parametros.put("hasta", getHasta());
//            }
            //CONTRATO
            if (contrato != null) {
                where += " and o.tipocontrato=:contrato ";
                whereCampo += " and o.empleado.tipocontrato=:contrato ";
                parametros.put("contrato", contrato);
            }
            //OFICINA
            if (getOficina() != null) {
                where += " and o.oficina=:oficina ";
                whereCampo += " and o.empleado.oficina=:oficina ";
                parametros.put("oficina", getOficina());
            }
            //EDIFICIO
            if (getEdificio() != null) {
                where += " and o.oficina.edificio=:edificio ";
                whereCampo += " and o.empleado.oficina.edificio=:edificio ";
                parametros.put("edificio", getEdificio());
            }

            //NOMBRAMIENTO
            if (getNombramiento() != null) {
                if (getNombramiento() == 1) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", true);
                } else if (getNombramiento() == 2) {
                    where += " and o.tipocontrato.nombramiento=:nombramiento ";
                    whereCampo += " and o.empleado.tipocontrato.nombramiento=:nombramiento ";
                    parametros.put("nombramiento", false);
                }
            }

            if (getCabeceraRrHh() != null) {
                whereCampo += " and o.cabecera=:cabeccera ";
                parametros.put("cabeccera", getCabeceraRrHh());

                if (getCabeceraRrHh().getTipodato() == 0) { // combo
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and o.valortexto=:valortexto ";
                        parametros.put("valortexto", getTextoBuscar());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 1) { // numerico
                    if (!((desdeNumerico == null) || (hastaNumerico == null))) {
                        whereCampo += " and o.valornumerico >= :desdeNumerico and valornumerico<= :hastaNumerico ";
                        parametros.put("desdeNumerico", getDesdeNumerico());
                        parametros.put("hastaNumerico", getHastaNumerico());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 3) { // numerico
                    if (!((desdeFecha == null) || (hastaFecha == null))) {
                        whereCampo += " and o.valorfecha between :desdeFecha and :hastaFecha ";
                        parametros.put("desdeFecha", getDesdeFecha());
                        parametros.put("hastaFecha", getHastaFecha());
                    }
                } else if (getCabeceraRrHh().getTipodato() == 2) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.valortexto) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                } else if (getCabeceraRrHh().getTipodato() == 4) { // texto
                    if (!((textoBuscar == null) || (textoBuscar.isEmpty()))) {
                        whereCampo += " and upper(o.codigo) like :valorBuscar ";
                        parametros.put("valorBuscar", getTextoBuscar() + "%");
                    }
                }

                parametros.put(";where", whereCampo);
                parametros.put(";orden", "o.empleado.entidad.apellidos");
                listaEmpleados = new LinkedList<>();
                List<Cabeceraempleados> cl = ejbCabecerasempleados.encontarParametros(parametros);
                if (entidadesBean.getEntidad() != null) {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                            where = "o.tipoacciones.parametros like 'LICENC%' "
                                    + " and o.empleado=:empleado  and o.empleado.activo=true and (o.desde between :inicioMes1 and :finMes1)"
                                    + " and  (o.hasta between :inicioMes and :finMes "
                                    + "or o.hasta is null or o.hasta >:finMes2)";
                            parametros = new HashMap();
                            parametros.put(";where", where);
                            parametros.put(";orden", "o.desde desc");
                            parametros.put("empleado", e);
                            parametros.put("inicioMes1", inicioMes.getTime());
                            parametros.put("finMes", finMes.getTime());
                            parametros.put("finMes1 ", finMes.getTime());
                            parametros.put("finMes2 ", finMes.getTime());
                            parametros.put("inicioMes ", inicioMes.getTime());
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
                    }
                } else {
                    for (Cabeceraempleados c : cl) {
                        Empleados e = c.getEmpleado();
                        // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                        where = "o.tipoacciones.parametros like 'LICENC%' "
                                + " and o.empleado=:empleado  and o.empleado.activo=true and (o.desde between :inicioMes1 and :finMes1)"
                                + " and  (o.hasta between :inicioMes and :finMes "
                                + "or o.hasta is null or o.hasta >:finMes2)";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put(";orden", "o.desde desc");
                        parametros.put("empleado", e);
                        parametros.put("inicioMes1", inicioMes.getTime());
                        parametros.put("finMes", finMes.getTime());
                        parametros.put("finMes1 ", finMes.getTime());
                        parametros.put("finMes2 ", finMes.getTime());
                        parametros.put("inicioMes ", inicioMes.getTime());
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
                }
            } else {
                parametros.put(";where", where);
                parametros.put(";orden", "o.entidad.apellidos");
                if (entidadesBean.getEntidad() != null) {
                    List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
                    listaEmpleados = new LinkedList<>();
                    for (Empleados e : lista) {
                        if (!e.getEntidad().equals(entidadesBean.getEntidad())) {
                            // BUSCAR LA ACCIÓN Y SE PONDE LA FECHA DE SALIDA SI CORRESPONDE
                            where = "o.tipoacciones.parametros like 'LICENC%'"
                                    + " and o.empleado=:empleado and o.empleado.activo=true and (o.desde between :inicioMes1 and :finMes1)"
                                    + " and  o.hasta > :finMes ";
//                                    + "or o.hasta is null or o.hasta >:finMes2)";
                            parametros = new HashMap();
                            parametros.put(";where", where);
                            parametros.put(";orden", "o.desde desc");
                            parametros.put("empleado", e);
                            parametros.put("inicioMes1", inicioMes.getTime());
                            parametros.put("finMes", finMes.getTime());
                            parametros.put("finMes1 ", finMes.getTime());
//                            parametros.put("finMes2 ", finMes.getTime());
                            parametros.put("inicioMes ", inicioMes.getTime());
                            List<Historialcargos> listah = ejbHistorial.encontarParametros(parametros);

                            Date salida = null;
                            boolean pone = true;
                            for (Historialcargos h : listah) {
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
                    }

                    // exxcluye a alguien
                } else {
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
//                        parametros.put("inicioMes1", inicioMes.getTime());
                        parametros.put("finMes", finMes.getTime());
//                        parametros.put("finMes1 ", finMes.getTime());
//                            parametros.put("finMes2 ", finMes.getTime());
//                        parametros.put("inicioMes ", inicioMes.getTime());
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

                }
            }
//                    int total = ejbEmpleados.contar(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
    }

    public String buscar() {

        // generar todo el proceso de rol
        // primero empezar con la RMU y los encargos
        // traer los empleados
        // traer los formatos  que se necesita
        try {
            if (concepto == null) {
                MensajesErrores.advertencia("Seleccione un concepto");
                return null;
            }
            Map parametros = new HashMap();
            Compromisos compromiso = null;
            String where = "o.anio=:anio and o.mes=:mes and o.liquidacion=false and o.compromiso is not null";
            // traer compromiso y ver si ya esta impreso
            Calendar cParaMesAnio = Calendar.getInstance();

            cParaMesAnio.set(Calendar.YEAR, anio);
            int diainicio = configuracionBean.getConfiguracion().getInicionomina() == null
                    ? 24 : configuracionBean.getConfiguracion().getInicionomina();
            int diafin = configuracionBean.getConfiguracion().getFinnomina() == null
                    ? 24 : configuracionBean.getConfiguracion().getFinnomina();
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
                return null;
            }
            parametros.put("where", where);
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            int total = ejbPagos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto, no se puede reprocesar rol");
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

            listaRoles = new LinkedList<>();
            listaTotales = new LinkedList<>();
            listaPagos = new LinkedList<>();

            cargar();
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            // conceptos de ingresos
            where = "o.ingreso=true "
                    + " and o.liquidacion=false "
                    + " and o.vacaciones=false "
                    + " and o.activo=true";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.orden");
            List<Conceptos> conceptoIngreso = ejbCon.encontarParametros(parametros);
//            Egresos 
            where = "o.ingreso=false "
                    + " and o.liquidacion=false "
                    + " and o.activo=true"
                    + " and o.vacaciones=false";
            parametros = new HashMap();
            parametros.put(";where", where);
            parametros.put(";orden", "o.orden");
            List<Conceptos> conceptoEgreso = ejbCon.encontarParametros(parametros);
            for (Empleados e : listaEmpleados) {
//                if (e.getEntidad().getApellidos().contains("RIVERA")) {
//                    MensajesErrores.advertencia("Cristian");
//                }
//                calcula el RMU en base de si existe un cambio temporal de puesto
//                double rmuCalculado = traerRmu(e);
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
//                            dias = fechaSalida.get(Calendar.DATE)-1;
                            rmuCalculado = rmuDiario * (dias);
                        }
                    }
                }
//                String que = "";
                String cuentaSubrogacion = "";
                if (subrogacionCodigos != null) {
                    cuentaSubrogacion = partida + subrogacionCodigos.getDescripcion();
                }

//                boolean encargo = false;
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
                    AuxiliarRol a = new AuxiliarRol();
                    a.setConceptoIngreso("REMUNERACION MENSUAL UNIFICADA");
                    a.setIngreso(new Float(rmuCalculado));
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    a.setCargo(e.getCargoactual().getCargo().getNombre());
                    a.setContrato(e.getTipocontrato() == null ? "SIN TIPO DE CONTRATO" : e.getTipocontrato().getNombre());
                    a.setProceso(e.getCargoactual().getOrganigrama().getNombre());

                    listaAxuiliar.add(a);
                    if (subrogaciones > 0) {
                        a = new AuxiliarRol();
                        a.setConceptoIngreso("SUBROGACIONES");
                        a.setIngreso(new Float(subrogaciones));
                        a.setEmpleado(e.getEntidad().toString());
                        a.setCedula(e.getEntidad().getPin());
//                        totalIngresos += subrogaciones - rmuSubrogacionDiario;
                        listaAxuiliar.add(a);
                    }

                    // Lista Pagos para SMV
                    Pagosempleados p1 = new Pagosempleados();
                    p1.setConcepto(null);
                    p1.setAnio(getAnio());
                    p1.setMes(getMes());
                    p1.setValor(new Float(rmuCalculado));
                    p1.setEmpleado(e);
                    p1.setCantidad(new Float(subrogaciones));
                    p1.setEncargo(new Float(encargos));
                    p1.setClasificador(partida + cuenta);
                    p1.setCuentaporpagar(ctaInicio + partida + ctaFin);
                    int diasSubrogados = 0;
                    /**
                     * **********************************Falta las cuentas de
                     * Subrogaciones ***************
                     */
                    if ((subrogaciones > 0) || (encargos > 0)) {
//                        if (encargo) {
//                            p1.setEncargo(new Float(encargos));
//                            subrogaciones = p1.getEncargo();
//                        } else {
//                            p1.setCantidad(new Float(subrogaciones));
//                        }

                        p1.setClasificadorencargo(cuentaSubrogacion);
                        // buscar la cuenta de subrogacion segun presupuesto
//                    listaPagos.add(p1);
                        parametros = new HashMap();
                        parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
                        parametros.put("clasificador", cuentaSubrogacion);
                        parametros.put(";orden", "o.codigo");
                        List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                        String ctaGasto = "";
                        for (Cuentas cta : lCuentas) {
                            if (!ctaGasto.isEmpty()) {
                                ctaGasto += "#";
                            }
                            ctaGasto += cta.getCodigo();
                        }
                        p1.setCuentasubrogacion(ctaGasto);
                        diasSubrogados = diasNovedadTotal;
//                        diasSubrogados = Constantes.DIAS_POR_MES - dias;
                    }
                    AuxiliarAsignacion aa = new AuxiliarAsignacion();
                    aa.setTitulo1("REMUNERACION MENSUAL UNIFICADA");
                    aa.setValor(p1.getValor());
                    esta(aa, true);
                    aa = new AuxiliarAsignacion();
                    aa.setTitulo1("ENCARGOS");
                    aa.setValor(p1.getEncargo());
                    esta(aa, true);
                    aa = new AuxiliarAsignacion();
                    aa.setTitulo1("SUBROGACIONES");
                    aa.setValor(p1.getCantidad());
                    esta(aa, true);
                    /// aumento esta linea

                    parametros = new HashMap();
                    parametros.put(";where", "o.presupuesto=:clasificador and o.imputable=true");
                    parametros.put("clasificador", partida + cuenta);
                    parametros.put(";orden", "o.codigo");
                    List<Cuentas> lCuentas = ejbCuentas.encontarParametros(parametros);
                    String ctaGasto = "";
                    for (Cuentas cta : lCuentas) {
                        if (!ctaGasto.isEmpty()) {
                            ctaGasto += "#";
                        }
                        ctaGasto += cta.getCodigo();
                    }
                    p1.setCuentagasto(ctaGasto);
                    p1.setCompromiso(compromiso);
                    if (p1.getEmpleado() == null) {
                        MensajesErrores.advertencia("No existe empleado");
                        return null;
                    }
                    if (p1.getEmpleado().getProyecto() == null) {
                        MensajesErrores.advertencia("No existe proyecto para el empleado " + e.toString());
                        return null;
                    }
                    if (p1.getEmpleado().getProyecto().getCodigo() == null) {
                        MensajesErrores.advertencia("No existe codigo para el proyecto" + e.getProyecto() + " del empleado " + e.toString());
                        return null;
                    }
//                    p1.setProyecto(e.getProyecto().getCodigo());
                    p1.setProyecto(p1.getEmpleado().getProyecto().getCodigo());
                    listaPagos.add(p1);

                    //*************************Ingresos Normales *******************************
                    double valor = ejbPagos.calculaConcepto(rmuCalculado, concepto, subrogaciones + encargos, e, dias,
                            diasSubrogados, ingresos, finMes.getTime());
                    if (valor != 0) {
                        aa = new AuxiliarAsignacion();
                        aa.setTitulo1(concepto.getNombre());
                        aa.setValor(valor);
                        esta(aa, true);
                        if (concepto.getPeriodico()) {
                            // es parte de los ingresos
                            ingresos += valor;
                        }
                        // va al sobre
                        a = new AuxiliarRol();
                        a.setConceptoIngreso(concepto.getNombre());
                        a.setIngreso(new Float(valor));
                        a.setEmpleado(e.getEntidad().toString());
                        a.setCedula(e.getEntidad().getPin());
                        listaAxuiliar.add(a);
                        totalIngresos += valor;
                        // grabar en la lista de ingresos
                        Pagosempleados p = new Pagosempleados();
                        p.setConcepto(concepto);
                        p.setAnio(getAnio());
                        p.setEmpleado(e);
                        p.setMes(getMes());
                        p.setValor(new Float(valor));
                        p.setClasificador(null);
                        p.setLiquidacion(false);
                        ejbPagos.ponerCuentasPago(concepto, p, ctaInicio, ctaFin, partida, compromiso);

                        listaPagos.add(p);
                    }
//              // hay que ver el tamaño máximo
                    for (AuxiliarRol au : listaAxuiliar) {
                        listaRoles.add(au);
                    }
                    a = new AuxiliarRol();
                    a.setConceptoIngreso("TOTAL INGRESOS");
                    a.setConceptoEgreso("TOTAL EGRESOS");
                    a.setIngreso(new Float(totalIngresos));
                    a.setEgreso(new Float(totalEgresos));
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    getListaRoles().add(a);
                    a = new AuxiliarRol();
                    a.setConceptoIngreso("");
                    a.setConceptoEgreso("A RECIBIR");
                    a.setEgreso(new Float(totalIngresos - totalEgresos));
                    a.setEmpleado(e.getEntidad().toString());
                    a.setCedula(e.getEntidad().getPin());
                    getListaRoles().add(a);
                }// fin del if sin sueldo
            } // fin del for empleados
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(RolEmpleadoConceptoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String exportar() {
        try {
            DocumentoXLS xls = new DocumentoXLS("Rol de Pagos");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", "Empleado"));
            campos.add(new AuxiliarReporte("String", "RMU"));
            campos.add(new AuxiliarReporte("String", "ENCARGO"));
            campos.add(new AuxiliarReporte("String", "SUBROGACIONES"));
            campos.add(new AuxiliarReporte("String", concepto.getNombre()));
            xls.agregarFila(campos, true);
            for (Empleados e : listaEmpleados) {
                campos = new LinkedList<>();
                double tingresos = getTotalIngresos(e);
                double tegresos = getTotalEgresos(e);
                double rmu = getRmu(e);
                double encargos = getEncargos(e);
                double subrogaciones = getSubrogaciones(e);
                double anticipos = getAnticipos(e);
                campos.add(new AuxiliarReporte("String", e.getEntidad().toString()));
                campos.add(new AuxiliarReporte("double", rmu));
                campos.add(new AuxiliarReporte("double", encargos));
                campos.add(new AuxiliarReporte("double", subrogaciones));
                double valor = getPagoconcepto(e, concepto);
                campos.add(new AuxiliarReporte("double", valor));
                xls.agregarFila(campos, false);
            }
            reporte = xls.traerRecurso();
            formularioReporte.insertar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(RolEmpleadoConceptoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String grabar() {
        // toca borrar mes y año  ya procesado pero si no esta pagado para lo cual consultados el pagado
        Map parametros = new HashMap();
        parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.pagado=true and o.concepto=:concepto");
        parametros.put("mes", mes);
        parametros.put("anio", anio);
        parametros.put("concepto", concepto);
        try {
            int cuantasPagadas = ejbPagos.contar(parametros);
            if (cuantasPagadas > 0) {
                MensajesErrores.advertencia("Nómina de " + concepto.getNombre() + " este mes ya pagada, no es posible borrar");
                return null;
            }
            // borrar la nomina si ya esta grabada
            ejbPagos.borrarNomina(getMes(), getAnio());
            for (Pagosempleados p : listaPagos) {
                p.setPagado(Boolean.FALSE);
                ejbPagos.create(p, parametrosSeguridad.getLogueado().getUserid());
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(RolEmpleadoConceptoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Nómina preliminar grabada correctamente");
        formulario.cancelar();
        return null;
    }

    private void esta(AuxiliarAsignacion a, boolean ingreso) {
        for (AuxiliarAsignacion a1 : listaTotales) {
            if (a1.getTitulo1().equals(a.getTitulo1())) {
                if (ingreso) {
                    double valor = a1.getValor() + a.getValor();
                    a1.setValor(valor);
                    return;
                } else {
                    double valor = a1.getComprometido() + a.getComprometido();
                    a1.setComprometido(valor);
                    return;
                }
            }
        }
        listaTotales.add(a);

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
     * @return the listaPagos
     */
    public List<Pagosempleados> getListaPagos() {
        return listaPagos;
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<Pagosempleados> listaPagos) {
        this.listaPagos = listaPagos;
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
     * @return the listaRoles
     */
    public List<AuxiliarRol> getListaRoles() {
        return listaRoles;
    }

    /**
     * @param listaRoles the listaRoles to set
     */
    public void setListaRoles(List<AuxiliarRol> listaRoles) {
        this.listaRoles = listaRoles;
    }

    /**
     * @return the listaTotales
     */
    public List<AuxiliarAsignacion> getListaTotales() {
        return listaTotales;
    }

    /**
     * @param listaTotales the listaTotales to set
     */
    public void setListaTotales(List<AuxiliarAsignacion> listaTotales) {
        this.listaTotales = listaTotales;
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

    public float getRmu(Empleados e) {
        if (e.getId() == null) {
            return getTotalRmu();
        }
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getConcepto() == null)
                    && (p.getSancion() == null)
                    && (p.getEmpleado().equals(e))) {
                return p.getValor();
            }
        }

        return 0;
    }

    public float getTotalRmu() {
        float suma = 0;
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getSancion() == null)
                    && (p.getConcepto() == null)) {
                suma += p.getValor();
            }
        }
        return suma;
    }

    public float getEncargos(Empleados e) {
        if (e.getId() == null) {
            return getTotalEncargos();
        }
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getConcepto() == null)
                    && (p.getSancion() == null)
                    && (p.getEmpleado().equals(e))) {
                return p.getEncargo();
            }
        }

        return 0;
    }

    public float getTotalEncargos() {
        float suma = 0;
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getSancion() == null)
                    && (p.getConcepto() == null)) {
                suma += p.getEncargo();
            }
        }
        return suma;
    }

    public float getSubrogaciones(Empleados e) {
        if (e.getId() == null) {
            return getTotalSubrogaciones();
        }
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getConcepto() == null)
                    && (p.getSancion() == null)
                    && (p.getEmpleado().equals(e))) {
                return p.getCantidad();
            }
        }

        return 0;
    }

    public float getTotalSubrogaciones() {
        float suma = 0;
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getSancion() == null)
                    && (p.getConcepto() == null)) {
                suma += p.getCantidad();
            }
        }
        return suma;
    }

    public float getPagoconcepto(Empleados e, Conceptos c) {
        if (e.getId() == null) {
            return getTotalPagoconcepto(c);
        }
        for (Pagosempleados p : listaPagos) {
            if (p.getConcepto() != null) {
                if ((p.getPrestamo() == null)
                        && (p.getConcepto().equals(c))
                        && (p.getEmpleado().equals(e))) {
                    return p.getValor();
                }
            }
        }

        return 0;
    }

    public float getTotalPagoconcepto(Conceptos c) {
        float suma = 0;
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() == null)
                    && (p.getConcepto().equals(c))) {
                suma += p.getCantidad();
            }
        }
        return suma;
    }

    public float getAnticipos(Empleados e) {
        if (e.getId() == null) {
            return getTotalAnticipos();
        }
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() != null)
                    && (p.getSancion() == null)
                    && (p.getConcepto() == null)
                    && (p.getEmpleado().equals(e))) {
                return p.getValor();
            }
        }

        return 0;
    }

    public float getTotalAnticipos() {
        float suma = 0;
        for (Pagosempleados p : listaPagos) {
            if ((p.getPrestamo() != null)
                    && (p.getSancion() == null)
                    && (p.getConcepto() == null)) {
                suma += p.getCantidad();
            }
        }
        return suma;
    }

    public float getTotalEgresos(Empleados e) {
        float retorno = getAnticipos(e);
        for (Conceptos c : listaConceptosEgresos) {
            retorno += getPagoconcepto(e, c);
        }
        return retorno;
    }

    public float getTotalIngresos(Empleados e) {
        float retorno = getRmu(e) + getSubrogaciones(e) + getEncargos(e);
        for (Conceptos c : listaConceptosIngresos) {
            retorno += getPagoconcepto(e, c);
        }
        return retorno;
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
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
    }

}
