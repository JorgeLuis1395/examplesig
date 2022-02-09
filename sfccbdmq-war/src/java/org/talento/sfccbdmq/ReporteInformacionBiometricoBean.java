/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import javax.faces.application.Resource;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.AuxiliarAsistencia;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.BiometricoFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.HorarioempleadoFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.entidades.sfccbdmq.Biometrico;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Horarioempleado;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposcontratos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteInformacionBiometricoSfccbdmq")
@ViewScoped
public class ReporteInformacionBiometricoBean implements Serializable {

    private static final long serialVersionUID = 1L;

//    private LazyDataModel<AuxiliarAsistencia> listadoEmpleados;
    private List<AuxiliarAsistencia> listaEmpleados;
    private Formulario formulario = new Formulario();
    private Formulario formularioDos = new Formulario();
    private List<AuxiliarAsistencia> listadoEmpleados;
    @EJB
    private MarcacionesbiometricoFacade ejbMarcaciones;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private HorarioempleadoFacade ejbHorarios;
    @EJB
    private LicenciasFacade ejbLicencias;
    @EJB
    private BiometricoFacade ejbBiometrico;

    private Date fecha = new Date();
    private Date fechaHasta = new Date();
    private Tiposcontratos tipo;
    private String regimen;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean organigramaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
    private Resource reporte1;
    private int total;
    private int operativo;
    private String nombre;
    private String codigo;
    private Horarioempleado horaIngreso;
    private Biometrico ingresoEmpleado;
    private Date fechaLeida;
    private Date entrada;
    private Date salida;
    private Date entrada1;
    private Date fechaEntrada;
    private Date fechaSalidaLeida1;
    private Date fechaEntradaLeida2;
    private Date fechaSalidaLeida;
    private String dondeUno;
    private String dondeDos;
    private String dondeTres;
    private String dondeCuatro;
    private String mensaje;
    private int mes;
    private int anio;
    private Date nroTimbradas;

    private Date fechaDesdeReporte;
    private Date fechaHastaReporte;

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteInformacionBiometricoVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }

//        mes = 1;
//        anio = 2019;
        fechaHastaReporte = new Date();
        fechaDesdeReporte = new Date();

    }

    // fin perfiles
    /**
     * Creates a new instance of CursosEmpleadoBean
     */
    public ReporteInformacionBiometricoBean() {

    }

    public String buscar() {

        Calendar desdeCalendario = Calendar.getInstance();
        Calendar hastaCalendario = Calendar.getInstance();
        desdeCalendario.setTime(fechaDesdeReporte);
        hastaCalendario.setTime(fechaHastaReporte);

        int mesInicio = desdeCalendario.get(Calendar.MONTH) + 1;
        int anioInicio = desdeCalendario.get(Calendar.YEAR);

        int diferenciaDias = ejbEmpleados.diferenciaDosFechas(hastaCalendario, desdeCalendario);

        listadoEmpleados = new LinkedList<>();
        Map parametros = new HashMap();
        String where = " o.activo=true ";
        switch (operativo) {
            case 1:
                where += " and o.tipocontrato is not null   "
                        + "and (o.fechasalida is null) and o.factor=3";
                break;
            case 2:
                where = " and o.tipocontrato is not null  "
                        + "and (o.fechasalida is null ) and o.factor=1";

                break;
            case 3:
                where += " and o.tipocontrato is not null   "
                        + "and (o.fechasalida is null ) and o.factor=1.67";
                break;
            default:
                where += " and o.tipocontrato is not null "
                        + "and (o.fechasalida is null )";
                break;
        }

        if (!((nombre == null) || (nombre.isEmpty()))) {
            where += " and upper(o.entidad.nombres) like :nombre";
            parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
        }
        if (!((codigo == null) || (codigo.isEmpty()))) {
            where += " and upper(o.entidad.apellidos) like :codigo";
            parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
        }
        if (organigramaBean.getOrganigramaL() != null) {
            where += " and o.cargoactual.organigrama.codigo like :organigrama";
            parametros.put("organigrama", organigramaBean.getOrganigramaL().getCodigo() + "%");
        }

        parametros.put(";where", where);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy ");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        String fechaNormal = "";
        try {

            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            for (Empleados e : lista) {
                AuxiliarAsistencia aux = null;
                // primero las entradas no me importa la seguna si la primera
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=true");
                parametros.put("empleado", e);
                List<Horarioempleado> hlista = ejbHorarios.encontarParametros(parametros);
                boolean primero = true;
                Calendar horaEntrada = Calendar.getInstance();
                Calendar horaSalida = Calendar.getInstance();
                Calendar horaInicioPermiso = Calendar.getInstance();
                Calendar horaFinPermiso = Calendar.getInstance();
                for (Horarioempleado h : hlista) {
                    Calendar horarioEmpleado = Calendar.getInstance();
                    horarioEmpleado.setTime(h.getHora());
                    int hora = horarioEmpleado.get(Calendar.HOUR_OF_DAY);
                    int minuto = horarioEmpleado.get(Calendar.MINUTE);
                    horaEntrada.set(0, 0, 0, hora, minuto, 0);
                }

                /*SALIDA*/
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=false");
                parametros.put("empleado", e);
                hlista = ejbHorarios.encontarParametros(parametros);
                for (Horarioempleado h : hlista) {
                    Calendar horarioEmpleado = Calendar.getInstance();
                    horarioEmpleado.setTime(h.getHora());
                    int hora = horarioEmpleado.get(Calendar.HOUR_OF_DAY);
                    int minuto = horarioEmpleado.get(Calendar.MINUTE);
                    horaSalida.set(0, 0, 0, hora, minuto, 0);
                }

                //Fecha de inicio y fin de mes
                //Fecha de inicio
//                int diaDesde = 1;
//                Calendar desdeCalendario = Calendar.getInstance();
//                desdeCalendario.add(Calendar.HOUR_OF_DAY, 0);
//                desdeCalendario.add(Calendar.MINUTE, 0);
//                desdeCalendario.add(Calendar.SECOND, 0);
                //desdeCalendario.set(anio, mes - 1, diaDesde, 0, 0, 0);
                //Fecha de fin
//                Calendar hastaCalendario = Calendar.getInstance();
//                Calendar fechaE = Calendar.getInstance();
//                fechaE.set(anio, mes, 1);
//                fechaE.add(Calendar.DATE, -1);
//                int fin = fechaE.get(Calendar.DATE);
                //hastaCalendario.set(anio, mes - 1, fin, 23, 59, 59);
                for (int i = 1; i <= diferenciaDias; i++) {
                    AuxiliarAsistencia auxiliarFor = new AuxiliarAsistencia();
                    desdeCalendario.set(anioInicio, mesInicio - 1, i, 0, 0, 0);
//                    System.out.println("Fecha desde: " + desdeCalendario.getTime());
                    hastaCalendario.set(anioInicio, mesInicio - 1, i, 23, 59, 59);
//                    System.out.println("Fecha hasta: " + hastaCalendario.getTime());
                    /*TRAER TODO LAS TIMBRADAS DEL BIOMETRICO*/
                    where = "o.cedula=:codigo and o.fecha between :desde and :hasta";
                    parametros = new HashMap();
                    parametros.put(";where", where);
                    parametros.put("codigo", e.getEntidad().getPin());
                    parametros.put(";orden", "o.fecha asc");
                    parametros.put("desde", desdeCalendario.getTime());
                    parametros.put("hasta", hastaCalendario.getTime());

                    primero = true;
                    List<Biometrico> blista = ejbBiometrico.encontarParametros(parametros);
                    Calendar timbrada = null;
                    int idT = 0;
                    for (Biometrico b : blista) {
                        Calendar fechaBiometrico = Calendar.getInstance();
                        fechaBiometrico.setTime(b.getFecha());
                        fechaBiometrico.set(Calendar.HOUR, 0);
                        fechaBiometrico.set(Calendar.MINUTE, 0);
                        fechaBiometrico.set(Calendar.SECOND, 0);
                        if (timbrada == null) {
                            timbrada = Calendar.getInstance();
                            timbrada.set(Calendar.HOUR, 0);
                            timbrada.set(Calendar.MINUTE, 0);
                            timbrada.set(Calendar.SECOND, 0);
                            timbrada.setTime(b.getFecha());
                            aux = new AuxiliarAsistencia();
                            auxiliarFor.setEmpleado(e);
                            auxiliarFor.setOrganigrama(e.getCargoactual().getOrganigrama());
                            auxiliarFor.setFecha(b.getFecha());
                            auxiliarFor.setFechaNormal(i + "/" + mesInicio + "/" + anioInicio);
                            auxiliarFor.setEntrada(horaEntrada.getTime());
                            auxiliarFor.setSalida(horaSalida.getTime());
                        }

                        int anioBiometrico = fechaBiometrico.get(Calendar.YEAR);
                        int mesBiometrico = fechaBiometrico.get(Calendar.MONTH);
                        int diaBiometrico = fechaBiometrico.get(Calendar.DATE);

                        int anioTimbrada = timbrada.get(Calendar.YEAR);
                        int mesTimbrada = timbrada.get(Calendar.MONTH);
                        int diaTimbrada = timbrada.get(Calendar.DATE);

                        if (!((anioBiometrico == anioTimbrada) && (mesBiometrico == mesTimbrada) && (diaBiometrico == diaTimbrada))) {
                            if ((auxiliarFor.getEntrada() != null) && (auxiliarFor.getEntradaLeida() != null)) {
                                double cuanto = (double) (auxiliarFor.getEntrada().getTime() - auxiliarFor.getEntradaLeida().getTime());
                                cuanto = cuanto / (1000 * 60);
                                double cuanto1 = cuanto;
                                int cuanto2 = (int) Math.ceil(cuanto1 * -1);
                                if (cuanto > 0) {
                                    cuanto = 0;
                                    auxiliarFor.setCuantoUno((int) Math.abs(cuanto));
                                } else {
                                    auxiliarFor.setCuantoUno((int) Math.abs(cuanto2));
                                }
                            }

                            if ((auxiliarFor.getSalidaLeidaUno() != null) && (auxiliarFor.getEntradaLeidaDos() != null)) {

                                int cuanto = (int) (auxiliarFor.getEntradaLeidaDos().getTime() - auxiliarFor.getSalidaLeidaUno().getTime());
                                cuanto = cuanto / (1000 * 60);
                                int saldo = -cuanto + 60;
                                if (saldo > 0) {
                                    saldo = 0;
                                }
                                auxiliarFor.setCuantoDos(Math.abs(saldo));

                            }

                            if ((auxiliarFor.getSalida() != null) && (auxiliarFor.getSalidaLeida() != null)) {
                                int cuanto = (int) (consultaDias(auxiliarFor.getSalidaLeida(), auxiliarFor.getSalida()));
                                if (cuanto > 0) {
                                    cuanto = 0;
                                }
                                auxiliarFor.setCuantoTres(Math.abs(cuanto));

                            }

                            listaEmpleados.add(aux);
                            timbrada.setTime(b.getFecha());
                            timbrada.set(Calendar.HOUR, 0);
                            timbrada.set(Calendar.MINUTE, 0);
                            timbrada.set(Calendar.SECOND, 0);
                            auxiliarFor = new AuxiliarAsistencia();
                            auxiliarFor.setEmpleado(e);
                            auxiliarFor.setOrganigrama(e.getCargoactual().getOrganigrama());
                            auxiliarFor.setFecha(b.getFecha());
                            auxiliarFor.setEntrada(horaEntrada.getTime());
                            auxiliarFor.setSalida(horaSalida.getTime());
                            idT = 0;

                        }

                        Calendar fechaHoraBiometrico = Calendar.getInstance();
                        fechaHoraBiometrico.setTime(b.getFecha());
                        int horaBiometrico = fechaHoraBiometrico.get(Calendar.HOUR_OF_DAY);
                        int minutoBiometrico = fechaHoraBiometrico.get(Calendar.MINUTE);
                        fechaHoraBiometrico.set(0, 0, 0, horaBiometrico, minutoBiometrico);
                        if (idT == 0) {
                            if ((horaBiometrico > 14 && horaBiometrico <= 17)) {
                                auxiliarFor.setEntradaLeidaDos(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeTres(b.getDispositivo());
                                auxiliarFor.setCuantoUno(60);
                            } else {
                                auxiliarFor.setEntradaLeida(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeUno(b.getDispositivo());
                            }

                            if (auxiliarFor.getEntradaLeida() != null) {
                                nroTimbradas = auxiliarFor.getEntradaLeida();
                            }

                        } else if (idT == 1) {
                            if ((horaBiometrico > 14 && horaBiometrico < 17)) {
                                auxiliarFor.setSalidaLeida(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeCuatro(b.getDispositivo());
                                auxiliarFor.setCuantoDos(60);
                            }
                            if ((horaBiometrico >= 17 && minutoBiometrico > 0)) {
                                auxiliarFor.setSalidaLeida(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeCuatro(b.getDispositivo());
                                auxiliarFor.setCuantoDos(60);
                            } else {

                                auxiliarFor.setSalidaLeidaUno(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeDos(b.getDispositivo());
                            }

                            if (auxiliarFor.getSalidaLeidaUno() != null) {
                                nroTimbradas = auxiliarFor.getSalidaLeidaUno();
                            }

                        } else if (idT == 2) {
                            if ((horaBiometrico >= 17 && minutoBiometrico > 0)) {
                                auxiliarFor.setSalidaLeida(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeCuatro(b.getDispositivo());
                                auxiliarFor.setCuantoDos(60);
                            } else {
                                auxiliarFor.setEntradaLeidaDos(fechaHoraBiometrico.getTime());
                                auxiliarFor.setDondeTres(b.getDispositivo());
                            }
                            if (auxiliarFor.getEntradaLeidaDos() != null) {
                                nroTimbradas = auxiliarFor.getEntradaLeidaDos();
                            }

                        } else if (idT == 3) {
                            auxiliarFor.setSalidaLeida(fechaHoraBiometrico.getTime());
                            auxiliarFor.setDondeCuatro(b.getDispositivo());
                            if (auxiliarFor.getSalidaLeida() != null) {
                                nroTimbradas = auxiliarFor.getSalidaLeida();
                            }

                        } else if (idT > 3) {
                            auxiliarFor.setSalidaLeida(fechaHoraBiometrico.getTime());
                            auxiliarFor.setDondeCuatro(b.getDispositivo());
                            if (nroTimbradas != null) {
                                auxiliarFor.setObservaciones("<Strong>Existen más de cuatro timbradas en el día, revisar información del biométrico</Strong>" + "</br> <Strong>Timbradas: </Strong>" + sdf1.format(nroTimbradas) + "</br>");
                            }
                        }
                        idT++;
                    }//Cierra el biometrico

                    if (auxiliarFor != null) {
                        if ((auxiliarFor.getEntrada() != null) && (auxiliarFor.getEntradaLeida() != null)) {

                            double cuanto = (double) (auxiliarFor.getEntrada().getTime() - auxiliarFor.getEntradaLeida().getTime());
                            cuanto = cuanto / (1000 * 60);
                            double cuanto1 = cuanto;
//                            System.out.println("Cuanto: " + Math.ceil(cuanto1 * -1));
                            int cuanto2 = (int) Math.ceil(cuanto1 * -1);
                            if (cuanto > 0) {
                                cuanto = 0;
                                auxiliarFor.setCuantoUno((int) Math.abs(cuanto));
                            } else {
                                auxiliarFor.setCuantoUno((int) Math.abs(cuanto2));
                            }
                        }

                        if ((auxiliarFor.getSalidaLeidaUno() != null) && (auxiliarFor.getEntradaLeidaDos() != null)) {

                            int cuanto = (int) (auxiliarFor.getEntradaLeidaDos().getTime() - auxiliarFor.getSalidaLeidaUno().getTime());
                            cuanto = cuanto / (1000 * 60);
                            int saldo = -cuanto + 60;
                            if (saldo > 0) {
                                saldo = 0;
                            }
                            auxiliarFor.setCuantoDos(Math.abs(saldo));
                        }
                        if ((auxiliarFor.getSalida() != null) && (auxiliarFor.getSalidaLeida() != null)) {

                            int cuanto = (int) (consultaDias(auxiliarFor.getSalidaLeida(), auxiliarFor.getSalida()));
                            if (cuanto > 0) {
                                cuanto = 0;
                            }
                            auxiliarFor.setCuantoTres(Math.abs(cuanto));

                        }
                    }

                    auxiliarFor.setEmpleado(e);
                    auxiliarFor.setOrganigrama(e.getCargoactual().getOrganigrama());
                    auxiliarFor.setFechaNormal(i + "/" + mesInicio + "/" + anioInicio);
                    auxiliarFor.setEntrada(horaEntrada.getTime());
                    auxiliarFor.setSalida(horaSalida.getTime());
                    listadoEmpleados.add(auxiliarFor);

                    //PERMISOS Y VACACIONES
                    parametros = new HashMap();
                    parametros.put(";where", "o.empleado=:empleado and  o.inicio between :desde and :hasta ");
                    parametros.put("empleado", e);
                    parametros.put("desde", desdeCalendario.getTime());
                    parametros.put("hasta", hastaCalendario.getTime());
                    parametros.put(";orden", "o.inicio asc");
                    // ver los permisos
                    List<Licencias> listaLicencias = ejbLicencias.encontarParametros(parametros);
                    String observaciones = "";
                    for (Licencias l : listaLicencias) {
                        if (l != null) {

                            Calendar inicioPermiso = Calendar.getInstance();
                            inicioPermiso.setTime(l.getDesde());
                            int horaInicio = inicioPermiso.get(Calendar.HOUR_OF_DAY);
                            int minutoInicio = inicioPermiso.get(Calendar.MINUTE);
                            horaInicioPermiso.set(0, 0, 0, horaInicio, minutoInicio, 0);
                            //System.out.println("Inicio Permiso:" + sdf1.format(horaInicioPermiso.getTime()));
                            Calendar finPermiso = Calendar.getInstance();
                            finPermiso.setTime(l.getHasta());
                            int horaFin = finPermiso.get(Calendar.HOUR_OF_DAY);
                            int minutoFin = finPermiso.get(Calendar.MINUTE);
                            horaFinPermiso.set(0, 0, 0, horaFin, minutoFin, 0);
                            //System.out.println("Fin Permiso:" + sdf1.format(horaFinPermiso.getTime()));

                            Calendar entradaBio = Calendar.getInstance();
                            entradaBio.setTime(auxiliarFor.getEntrada());
                            int horaIngresoH = entradaBio.get(Calendar.HOUR_OF_DAY);
                            int minutoIngresoH = entradaBio.get(Calendar.MINUTE);
                            entradaBio.set(0, 0, 0, horaIngresoH, minutoIngresoH, 0);

                            if ((horaFinPermiso.getTime() != null) && (auxiliarFor.getEntradaLeida() != null)) {
                                if (horaIngresoH == horaInicio) {
                                    int cuanto = (int) (horaFinPermiso.getTime().getTime() - auxiliarFor.getEntradaLeida().getTime());
                                    cuanto = cuanto / (1000 * 60);
                                    if (cuanto > 0) {
                                        cuanto = 0;
                                    }
                                    auxiliarFor.setCuantoUno((int) Math.abs(cuanto));

                                }
                            }
                            if ((auxiliarFor.getSalidaLeida() != null)) {
                                if (horaFin >= 17) {
                                    int cuanto = (int) (horaFinPermiso.getTime().getTime() - auxiliarFor.getSalidaLeida().getTime());
                                    cuanto = cuanto / (1000 * 60);
                                    if (cuanto > 0) {
                                        cuanto = 0;
                                    }
                                    auxiliarFor.setCuantoTres(Math.abs(cuanto));
                                }
                            }

                            if (l.getAprovado() == null) {
                                mensaje = "POR LEGALIZAR";
                            } else {
                                if (l.getAprovado()) {
                                    mensaje = "LEGALIZADO";
                                } else {
                                    mensaje = "NEGADO";
                                }
                            }

                            observaciones += "<p><Strong>F.Solicitud: </Strong>" + sdf.format(l.getInicio())
                                    + "<Strong>, Número: </Strong>" + l.getId().toString()
                                    + "<Strong>, Tipo: </Strong>" + l.getTipo().toString()
                                    + "<Strong>, Desde: </Strong>" + sdf.format(l.getInicio())
                                    + "<Strong>, Hasta: </Strong>" + sdf.format(l.getFin())
                                    + "<Strong>, H Inicio: </Strong>" + sdf1.format(l.getDesde())
                                    + "<Strong>, H Fin: </Strong>" + sdf1.format(l.getHasta())
                                    + "<Strong>, Estado: </Strong>" + mensaje + "<br></p>";
                            auxiliarFor.setObservaciones(observaciones);
                        }
                    }

                }

            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteInformacionBiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getColor(int valor) {
        if (valor > 0) {
            return "RED";
        }
        return "";
    }

    private long traerHoras(Licencias l) {
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        inicio.setTime(l.getInicio());
        fin.setTime(l.getFin());

        Calendar horaInicio = Calendar.getInstance();
        Calendar horaFin = Calendar.getInstance();
        horaInicio.setTime(l.getDesde());
        horaFin.setTime(l.getHasta());

        inicio.set(Calendar.HOUR_OF_DAY, horaInicio.get(Calendar.HOUR_OF_DAY));
        inicio.set(Calendar.MINUTE, horaInicio.get(Calendar.MINUTE));
        inicio.set(Calendar.SECOND, horaInicio.get(Calendar.SECOND));
        inicio.set(Calendar.MILLISECOND, horaInicio.get(Calendar.MILLISECOND));

        fin.set(Calendar.HOUR_OF_DAY, horaFin.get(Calendar.HOUR_OF_DAY));
        fin.set(Calendar.MINUTE, horaFin.get(Calendar.MINUTE));
        fin.set(Calendar.SECOND, horaFin.get(Calendar.SECOND));
        fin.set(Calendar.MILLISECOND, horaFin.get(Calendar.MILLISECOND));

        Long retorno = (fin.getTime().getTime() - inicio.getTime().getTime());
        retorno = (retorno / (60000)) / 60;
        return Math.abs(retorno);

    }

    public int consultaDias(Date d, Date h) {
        Long retorno = (d.getTime() - h.getTime());
        retorno = retorno / (60000);
        return retorno.intValue();
    }

    public double contadorMinutos(Date d, Date h) {
        long retorno = (Math.abs((d.getTime() - h.getTime())));
        if (retorno > 60) {
            retorno = (60 - retorno) / (60000);
        } else {
            retorno = retorno / (60000);
        }

        return retorno;
    }

    public double salida(Date d, Date h) {
        long retorno = (Math.abs((d.getTime() - h.getTime())));
        if (retorno < 60) {
            retorno = (60 - retorno) / (60000);
        } else {
            retorno = retorno / (60000);
        }
        return retorno;
    }

    public void estaFecha(Empleados e, Date d, String o) {
        //Fecha de inicio y fin de mes
        //Fecha de inicio
//                s
        Calendar desdeCalendario = Calendar.getInstance();
        desdeCalendario.add(Calendar.HOUR_OF_DAY, 0);
        desdeCalendario.add(Calendar.MINUTE, 0);
        desdeCalendario.add(Calendar.SECOND, 0);
        //desdeCalendario.set(anio, mes - 1, diaDesde, 0, 0, 0);

        //Fecha de fin
        Calendar hastaCalendario = Calendar.getInstance();
        Calendar fechaE = Calendar.getInstance();
        fechaE.set(anio, mes, 1);
        fechaE.add(Calendar.DATE, -1);

        int fin = fechaE.get(Calendar.DATE);
        //hastaCalendario.set(anio, mes - 1, fin, 23, 59, 59);

        Calendar fechaDesde = Calendar.getInstance();
        fechaDesde.setTime(d);
        int anioDesde = fechaDesde.get(Calendar.YEAR);
        int mesDesde = fechaDesde.get(Calendar.MONTH);
        int diaDesde = fechaDesde.get(Calendar.DATE);

        for (AuxiliarAsistencia auxiliarFor : listadoEmpleados) {
            if (auxiliarFor != null) {
                Calendar fechaHastaAux = Calendar.getInstance();
//                fechaHastaAux.setTime(auxiliarFor.getFecha());
                int anioHasta = fechaHastaAux.get(Calendar.YEAR);
                int mesHasta = fechaHastaAux.get(Calendar.MONTH);
                int diaHasta = fechaHastaAux.get(Calendar.DATE);

                if ((auxiliarFor.getEmpleado().equals(e)) && ((anioDesde == anioHasta) && (mesDesde == mesHasta) && (diaDesde == diaHasta))) {
                    auxiliarFor.setObservaciones(o);
                    return;
                }
            }
        }

    }
//    public String getFechaMes() {
//        String fechaNormal = "";
//        //Fecha de inicio
//        int diaDesde = 1;
//        Calendar desdeCalendario = Calendar.getInstance();
//        desdeCalendario.add(Calendar.HOUR_OF_DAY, 0);
//        desdeCalendario.add(Calendar.MINUTE, 0);
//        desdeCalendario.add(Calendar.SECOND, 0);
//        desdeCalendario.set(anio, mes - 1, diaDesde, 0, 0, 0);
//        System.out.println("Fecha desde: " + desdeCalendario.getTime());
//
//        //Fecha de fin
//        Calendar hastaCalendario = Calendar.getInstance();
//        int inicio = 1;
//        Calendar fechaE = Calendar.getInstance();
//        fechaE.set(anio, mes, 1);
//        fechaE.add(Calendar.DATE, -1);
//        System.out.println("Fecha hasta: " + fechaE.getTime());
//
//        int fin = fechaE.get(Calendar.DATE);
//        hastaCalendario.set(anio, mes - 1, fin, 23, 59, 59);
//
//       
//        System.out.println("Fecha Normal: " + fechaNormal);
//        return fechaNormal;
//
//    }

    public List<AuxiliarAsistencia> getEmpleados() {
        Organigrama a = null;

        return traerEmpleados(a);
    }

    public List<AuxiliarAsistencia> traerEmpleados(Organigrama a) {

        Map parametros = new HashMap();

        Calendar desdeCalendario = Calendar.getInstance();
        Calendar hastaCalendario = Calendar.getInstance();
        desdeCalendario.setTime(fecha);
        hastaCalendario.setTime(fecha);
        desdeCalendario.set(Calendar.HOUR_OF_DAY, 0);
        desdeCalendario.set(Calendar.MINUTE, 0);
        desdeCalendario.set(Calendar.SECOND, 0);
        hastaCalendario.set(Calendar.HOUR_OF_DAY, 23);
        hastaCalendario.set(Calendar.MINUTE, 59);
        hastaCalendario.set(Calendar.SECOND, 59);
        parametros.put(";orden", "o.entidad.apellidos");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
        try {

            listaEmpleados = new LinkedList<>();
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            // ver la sistencia + los horarios
            for (Empleados e : lista) {
                boolean timbre = false;
                AuxiliarAsistencia aux = new AuxiliarAsistencia();
                aux.setEmpleado(e);
                aux.setOrganigrama(e.getCargoactual().getOrganigrama());
                // primero las entradas no me importa la seguna si la primera
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=true");
                parametros.put("empleado", e);
                List<Horarioempleado> hlista = ejbHorarios.encontarParametros(parametros);
                boolean primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        aux.setEntrada(h.getHora());
                        primero = false;
                    } else {
                        aux.setEntradaDos(h.getHora());
                    }
                }
                String where = "o.cedula=:codigo and o.fecha between :desde and :hasta";
                parametros = new HashMap();
                parametros.put(";where", where);
                parametros.put("codigo", e.getEntidad().getPin());
                parametros.put(";orden", "o.fecha asc");
                parametros.put("desde", desdeCalendario.getTime());
                parametros.put("hasta", hastaCalendario.getTime());
                primero = true;
                int idT = 0;

                List<Biometrico> blista = ejbBiometrico.encontarParametros(parametros);
                for (Biometrico b : blista) {
                    if (idT == 0) {
                        aux.setEntradaLeida(b.getFecha());
                        aux.setDondeUno(b.getDispositivo());
                    } else if (idT == 1) {
                        aux.setSalidaLeidaUno(b.getFecha());
                        aux.setDondeDos(b.getDispositivo());
                    } else if (idT == 2) {
                        aux.setEntradaLeidaDos(b.getFecha());
                        aux.setDondeTres(b.getDispositivo());
                    } else if (idT == 3) {
                        aux.setSalidaLeida(b.getFecha());
                        aux.setDondeCuatro(b.getDispositivo());
                    } else if (idT > 3) {
                        aux.setSalidaLeida(b.getFecha());
                        aux.setDondeCuatro(b.getDispositivo());
                    }
                    idT++;
                    timbre = true;
                }

                if ((aux.getEntrada() != null) && (aux.getEntradaLeida() != null)) {
                    int cuanto = (int) (consultaDias(aux.getEntrada(), aux.getEntradaLeida()));
                    if (cuanto > 0) {
                        cuanto = 0;
                    }
                    aux.setCuantoUno(Math.abs(cuanto));
                }

                if ((aux.getEntradaDos() != null) && (aux.getEntradaLeidaDos() != null)) {
                    int cuanto = (int) (aux.getSalidaLeidaUno().getTime() - aux.getEntradaLeida().getTime());
                    cuanto = cuanto / (1000 * 60);
                    if (cuanto > 0) {
                        cuanto = 0;
                    }
                    aux.setCuantoDos(Math.abs(cuanto));
                }

                if ((aux.getSalida() != null) && (aux.getSalidaLeida() != null)) {
                    int cuanto = (int) (salida(aux.getSalida(), aux.getSalidaLeida()));
                    if (cuanto > 0) {
                        cuanto = 0;
                    }
                    aux.setCuantoTres(Math.abs(cuanto));
                }
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.ingreso=false");
                parametros.put("empleado", e);
                hlista = ejbHorarios.encontarParametros(parametros);
                primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        aux.setSalidaUno(h.getHora());
                        primero = false;
                    } else {
                        aux.setSalida(h.getHora());
                    }
                }
                // salidas
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and :fecha between o.inicio and o.fin");
                parametros.put("empleado", e);
                parametros.put("fecha", fecha);
                // ver los permisos
                List<Licencias> listaLicencias = ejbLicencias.encontarParametros(parametros);
                String observaciones = "";
                for (Licencias l : listaLicencias) {
                    observaciones += "<p><Strong> Número = </Strong>" + l.getId().toString() + "<Strong> - Tipo = </Strong>" + l.getTipo().toString() + "<Strong> - Fecha Desde = </Strong>" + sdf.format(l.getInicio()) + "<p><Strong> - Fecha Hasta = </Strong>" + sdf.format(l.getFin()) + "<p><Strong> - Horas = </Strong>" + sdf.format(l.getHoras()) + "/" + "</br></p>";
                }
                aux.setObservaciones(observaciones);
                getListaEmpleados().add(aux);
            }

            total += lista.size();
            return getListaEmpleados();

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(ReporteInformacionBiometricoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String actualizarBiometrico() {

        try {
            Calendar desdeCalendario = Calendar.getInstance();
            Calendar hastaCalendario = Calendar.getInstance();
            desdeCalendario.setTime(fechaDesdeReporte);
            hastaCalendario.setTime(fechaHastaReporte);
            desdeCalendario.set(Calendar.HOUR_OF_DAY, 0);
            desdeCalendario.set(Calendar.MINUTE, 0);
            desdeCalendario.set(Calendar.SECOND, 0);
            hastaCalendario.set(Calendar.HOUR_OF_DAY, 23);
            hastaCalendario.set(Calendar.MINUTE, 59);
            hastaCalendario.set(Calendar.SECOND, 59);

            Map parametros = new HashMap();

            String where = " o.cedula is not null ";
            where += " and o.fechahora between :desde and :hasta ";
            parametros.put("desde", desdeCalendario.getTime());
            parametros.put("hasta", hastaCalendario.getTime());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechahora asc");
            List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);

            parametros = new HashMap();
            parametros.put(";where", "o.cedula is not null and o.fecha between :desde and :hasta ");
            parametros.put("desde", desdeCalendario.getTime());
            parametros.put("hasta", hastaCalendario.getTime());
            List<Biometrico> listabiometrico = ejbBiometrico.encontarParametros(parametros);
            if (listabiometrico.isEmpty()) {
                actualizar();
            } else {
                for (Marcacionesbiometrico b : blista) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.idmarcacion=:idbiometrico");
                    parametros.put("idbiometrico", b.getId());
                    System.out.println("Biometrico: "+b.getId());
                    List<Biometrico> listaCompararBiometrico = ejbBiometrico.encontarParametros(parametros);

                    if (listaCompararBiometrico.isEmpty()) {
                        Biometrico bio = new Biometrico();
                        bio.setCedula(b.getCedula());
                        bio.setIdmarcacion(b.getId());
                        bio.setDispositivo(b.getDispositivo());
                        bio.setSitio(b.getSitio());
                        bio.setFecha(b.getFechahora());
                        bio.setFechaproceso(b.getFechahoraproceso());
                        bio.setEntrada(Boolean.TRUE);
                        ejbBiometrico.create(bio, parametrosSeguridad.getLogueado().getUserid());

                    } else {
                        System.out.println("Marcaciones biométrico " + b.getId() + " y " + "Biométrico " + listaCompararBiometrico.get(0).getId());
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteInformacionBiometricoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InsertarException ex) {
            Logger.getLogger(ReporteInformacionBiometricoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        return null;
    }

    public String actualizar() {
        Calendar desdeCalendario = Calendar.getInstance();
        Calendar hastaCalendario = Calendar.getInstance();
        desdeCalendario.setTime(fechaDesdeReporte);
        hastaCalendario.setTime(fechaHastaReporte);
        desdeCalendario.set(Calendar.HOUR_OF_DAY, 0);
        desdeCalendario.set(Calendar.MINUTE, 0);
        desdeCalendario.set(Calendar.SECOND, 0);
        hastaCalendario.set(Calendar.HOUR_OF_DAY, 23);
        hastaCalendario.set(Calendar.MINUTE, 59);
        hastaCalendario.set(Calendar.SECOND, 59);

        try {

            Map parametros = new HashMap();
            String where = " o.cedula is not null ";
            where += " and o.fechahora between :desde and :hasta ";
            parametros.put("desde", desdeCalendario.getTime());
            parametros.put("hasta", hastaCalendario.getTime());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechahora asc");
            List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
            Biometrico biome = new Biometrico();
            for (Marcacionesbiometrico b : blista) {
                biome.setIdmarcacion(b.getId());
                biome.setCedula(b.getCedula());
                biome.setDispositivo(b.getDispositivo());
                biome.setSitio(b.getSitio());
                biome.setFecha(b.getFechahora());
                biome.setFechaproceso(b.getFechahoraproceso());
                biome.setEntrada(Boolean.TRUE);
                ejbBiometrico.create(biome, parametrosSeguridad.getLogueado().getUserid());
            }

        } catch (ConsultarException | InsertarException ex) {
            Logger.getLogger(ReporteInformacionBiometricoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getTotalempleados() {

        //Fecha de inicio
        int diaDesde = 1;
        Calendar desdeCalendario = Calendar.getInstance();
        desdeCalendario.add(Calendar.HOUR_OF_DAY, 0);
        desdeCalendario.add(Calendar.MINUTE, 0);
        desdeCalendario.add(Calendar.SECOND, 0);
        //desdeCalendario.set(anio, mes - 1, diaDesde, 0, 0, 0);

        //Fecha de fin
        Calendar hastaCalendario = Calendar.getInstance();
        Calendar fechaE = Calendar.getInstance();
        fechaE.set(anio, mes, 1);
        fechaE.add(Calendar.DATE, -1);

        int fin = fechaE.get(Calendar.DATE);
        //hastaCalendario.set(anio, mes - 1, fin, 23, 59, 59);

        Map parametros = new HashMap();
        if (operativo == 1) {
            parametros.put(";where", "o.activo=true and o.cargoacatual.operativo=true "
                    + "and o.tipocontrato is not null "
                    + "and (o.fechasalida is null )");
        } else if (operativo == 1) {
            parametros.put(";where", "o.activo=true and o.cargoacatual.operativo=true "
                    + "and o.tipocontrato is not null "
                    + "and (o.fechasalida is null or )");
        } else {
            parametros.put(";where", "o.activo=true "
                    + "and o.tipocontrato is not null "
                    + "and (o.fechasalida is null )");
        }

        parametros.put("desde", desdeCalendario);
        parametros.put("hasta", hastaCalendario);
        parametros.put(";orden", "o.entidad.apellidos");

        try {
            return ejbEmpleados.contar(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteInformacionBiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the tipo
     */
    public Tiposcontratos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposcontratos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the organigramaBean
     */
    public CargoxOrganigramaBean getOrganigramaBean() {
        return organigramaBean;
    }

    /**
     * @param organigramaBean the organigramaBean to set
     */
    public void setOrganigramaBean(CargoxOrganigramaBean organigramaBean) {
        this.organigramaBean = organigramaBean;
    }

    /**
     * @return the formularioDos
     */
    public Formulario getFormularioDos() {
        return formularioDos;
    }

    /**
     * @param formularioDos the formularioDos to set
     */
    public void setFormularioDos(Formulario formularioDos) {
        this.formularioDos = formularioDos;
    }

    /**
     * @return the fechaHasta
     */
    public Date getFechaHasta() {
        return fechaHasta;
    }

    /**
     * @param fechaHasta the fechaHasta to set
     */
    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
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
     * @return the reporte1
     */
    public Resource getReporte1() {
        return reporte1;
    }

    /**
     * @param reporte1 the reporte1 to set
     */
    public void setReporte1(Resource reporte1) {
        this.reporte1 = reporte1;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
     * @return the listaEmpleados
     */
    public List<AuxiliarAsistencia> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<AuxiliarAsistencia> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the horaIngreso
     */
    public Horarioempleado getHoraIngreso() {
        return horaIngreso;
    }

    /**
     * @param horaIngreso the horaIngreso to set
     */
    public void setHoraIngreso(Horarioempleado horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    /**
     * @return the ingresoEmpleado
     */
    public Biometrico getIngresoEmpleado() {
        return ingresoEmpleado;
    }

    /**
     * @param ingresoEmpleado the ingresoEmpleado to set
     */
    public void setIngresoEmpleado(Biometrico ingresoEmpleado) {
        this.ingresoEmpleado = ingresoEmpleado;
    }

    /**
     * @return the listadoEmpleados
     */
    public List<AuxiliarAsistencia> getListadoEmpleados() {
        return listadoEmpleados;
    }

    /**
     * @param listadoEmpleados the listadoEmpleados to set
     */
    public void setListadoEmpleados(List<AuxiliarAsistencia> listadoEmpleados) {
        this.listadoEmpleados = listadoEmpleados;
    }

    /**
     * @return the fechaLeida
     */
    public Date getFechaLeida() {
        return fechaLeida;
    }

    /**
     * @param fechaLeida the fechaLeida to set
     */
    public void setFechaLeida(Date fechaLeida) {
        this.fechaLeida = fechaLeida;
    }

    /**
     * @return the fechaSalidaLeida1
     */
    public Date getFechaSalidaLeida1() {
        return fechaSalidaLeida1;
    }

    /**
     * @param fechaSalidaLeida1 the fechaSalidaLeida1 to set
     */
    public void setFechaSalidaLeida1(Date fechaSalidaLeida1) {
        this.fechaSalidaLeida1 = fechaSalidaLeida1;
    }

    /**
     * @return the fechaEntradaLeida2
     */
    public Date getFechaEntradaLeida2() {
        return fechaEntradaLeida2;
    }

    /**
     * @param fechaEntradaLeida2 the fechaEntradaLeida2 to set
     */
    public void setFechaEntradaLeida2(Date fechaEntradaLeida2) {
        this.fechaEntradaLeida2 = fechaEntradaLeida2;
    }

    /**
     * @return the fechaSalidaLeida
     */
    public Date getFechaSalidaLeida() {
        return fechaSalidaLeida;
    }

    /**
     * @param fechaSalidaLeida the fechaSalidaLeida to set
     */
    public void setFechaSalidaLeida(Date fechaSalidaLeida) {
        this.fechaSalidaLeida = fechaSalidaLeida;
    }

    /**
     * @return the fechaEntrada
     */
    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * @param fechaEntrada the fechaEntrada to set
     */
    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * @return the dondeUno
     */
    public String getDondeUno() {
        return dondeUno;
    }

    /**
     * @param dondeUno the dondeUno to set
     */
    public void setDondeUno(String dondeUno) {
        this.dondeUno = dondeUno;
    }

    /**
     * @return the dondeDos
     */
    public String getDondeDos() {
        return dondeDos;
    }

    /**
     * @param dondeDos the dondeDos to set
     */
    public void setDondeDos(String dondeDos) {
        this.dondeDos = dondeDos;
    }

    /**
     * @return the dondeTres
     */
    public String getDondeTres() {
        return dondeTres;
    }

    /**
     * @param dondeTres the dondeTres to set
     */
    public void setDondeTres(String dondeTres) {
        this.dondeTres = dondeTres;
    }

    /**
     * @return the dondeCuatro
     */
    public String getDondeCuatro() {
        return dondeCuatro;
    }

    /**
     * @param dondeCuatro the dondeCuatro to set
     */
    public void setDondeCuatro(String dondeCuatro) {
        this.dondeCuatro = dondeCuatro;
    }

    /**
     * @return the entrada
     */
    public Date getEntrada() {
        return entrada;
    }

    /**
     * @param entrada the entrada to set
     */
    public void setEntrada(Date entrada) {
        this.entrada = entrada;
    }

    /**
     * @return the entrada1
     */
    public Date getEntrada1() {
        return entrada1;
    }

    /**
     * @param entrada1 the entrada1 to set
     */
    public void setEntrada1(Date entrada1) {
        this.entrada1 = entrada1;
    }

    /**
     * @return the salida
     */
    public Date getSalida() {
        return salida;
    }

    /**
     * @param salida the salida to set
     */
    public void setSalida(Date salida) {
        this.salida = salida;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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
     * @return the fechaDesdeReporte
     */
    public Date getFechaDesdeReporte() {
        return fechaDesdeReporte;
    }

    /**
     * @param fechaDesdeReporte the fechaDesdeReporte to set
     */
    public void setFechaDesdeReporte(Date fechaDesdeReporte) {
        this.fechaDesdeReporte = fechaDesdeReporte;
    }

    /**
     * @return the fechaHastaReporte
     */
    public Date getFechaHastaReporte() {
        return fechaHastaReporte;
    }

    /**
     * @param fechaHastaReporte the fechaHastaReporte to set
     */
    public void setFechaHastaReporte(Date fechaHastaReporte) {
        this.fechaHastaReporte = fechaHastaReporte;
    }

}