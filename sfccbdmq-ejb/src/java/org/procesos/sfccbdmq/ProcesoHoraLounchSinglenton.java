/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.beans.sfccbdmq.HorarioempleadoFacade;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.MarcacionesbiometricoFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Horarioempleado;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Marcacionesbiometrico;
import org.entidades.sfccbdmq.Tipopermisos;
import org.entidades.sfccbdmq.Vacaciones;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class ProcesoHoraLounchSinglenton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private LicenciasFacade ejbLicencias;
    @EJB
    private HorarioempleadoFacade ejbHorarios;
    @EJB
    private MarcacionesbiometricoFacade ejbMarcaciones;
    @EJB
    protected SfccbdmqCorreosFacade ejbCorreos;

//    @Schedule(dayOfWeek = "Mon-Fri", month = "*", hour = "16", dayOfMonth = "*", year = "*", minute = "20", second = "0")

    public void procesoHoraLounch() {
        try {
            Calendar hoy = Calendar.getInstance();
            hoy.add(Calendar.DATE, -1);
            int mesActuaL = hoy.get(Calendar.MONTH);
            int anioActuaL = hoy.get(Calendar.YEAR);
            int diaActuaL = hoy.get(Calendar.DATE);
            Map parametros = new HashMap();
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Proceso de Lounch : " + sfd.format(hoy.getTime()));
            //
            //
            // ver horarios para seguir si no hay como comparar abandonar
            parametros.put(";where", "o.ingreso=true");
//                parametros.put("empleado", e);
            List<Horarioempleado> hlista = ejbHorarios.encontarParametros(parametros);
            //necesito la primera salida y la segunda entrada
            boolean primero = true;
            Calendar primeraSalida1 = Calendar.getInstance();
            Calendar segundaEntrada1 = Calendar.getInstance();
            boolean primeraBolean1 = false;
            boolean segundaBolean1 = false;
            for (Horarioempleado h : hlista) {
                if (primero) {

                    primero = false;
                } else {
                    Calendar aux = Calendar.getInstance();
                    aux.setTime(h.getHora());
                    int hora = aux.get(Calendar.HOUR_OF_DAY);
                    int minuto = aux.get(Calendar.MINUTE);
                    primeraBolean1 = true;
                    segundaEntrada1.set(anioActuaL, mesActuaL, diaActuaL, hora, minuto);
                }
            }
            parametros = new HashMap();
            parametros.put(";where", " o.ingreso=false");
//                parametros.put("empleado", e);
            hlista = ejbHorarios.encontarParametros(parametros);
            primero = true;
            for (Horarioempleado h : hlista) {
                if (primero) {
                    Calendar aux = Calendar.getInstance();
                    aux.setTime(h.getHora());
                    int hora = aux.get(Calendar.HOUR_OF_DAY);
                    int minuto = aux.get(Calendar.MINUTE);
                    primeraSalida1.set(anioActuaL, mesActuaL, diaActuaL, hora, minuto);
                    segundaBolean1 = true;
                    primero = false;
                }
            }
            // Horarios globales
            Query q = em.createQuery("SELECT Object(o) "
                    + " FROM  Empleados as o "
                    + " WHERE o.cargoactual is not null "
                    + "  and o.activo=true and o.operativo=false"
                    + " order by o.entidad.apellidos", Empleados.class);
            List<Empleados> lista = q.getResultList();

            for (Empleados e : lista) {
                boolean primeraBolean = primeraBolean1;
                boolean segundaBolean = segundaBolean1;
                Calendar primeraSalida =primeraSalida1;
                Calendar segundaEntrada = segundaEntrada1;
                if (e.getEntidad().getId() == 1473) {
                    System.out.println("DELIA");
                }
                parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado "
                        + " and :fecha between "
                        + " o.inicio and o.fin and o.inicio!=o.fin");
                parametros.put("empleado", e);
                parametros.put("fecha", hoy.getTime());
                int total = ejbLicencias.contar(parametros);
                //
                //
                // ver horarios para seguir si no hay como comparar abandonar
                parametros = new HashMap();
                parametros.put(";where", "o.ingreso=true and o.empleado=:empleado");
                parametros.put("empleado", e);
                hlista = ejbHorarios.encontarParametros(parametros);
                //necesito la primera salida y la segunda entrada
                primero = true;
//                primeraSalida = Calendar.getInstance();
//                segundaEntrada = Calendar.getInstance();
//                primeraBolean = false;
//                segundaBolean = false;
                for (Horarioempleado h : hlista) {
                    if (primero) {

                        primero = false;
                    } else {
                        Calendar aux = Calendar.getInstance();
                        aux.setTime(h.getHora());
                        int hora = aux.get(Calendar.HOUR_OF_DAY);
                        int minuto = aux.get(Calendar.MINUTE);
                        primeraBolean = true;
                        segundaEntrada.set(anioActuaL, mesActuaL, diaActuaL, hora, minuto);
                    }
                }
                parametros = new HashMap();
                parametros.put(";where", " o.ingreso=false and o.empleado=:empleado");
                parametros.put("empleado", e);
                hlista = ejbHorarios.encontarParametros(parametros);
                primero = true;
                for (Horarioempleado h : hlista) {
                    if (primero) {
                        Calendar aux = Calendar.getInstance();
                        aux.setTime(h.getHora());
                        int hora = aux.get(Calendar.HOUR_OF_DAY);
                        int minuto = aux.get(Calendar.MINUTE);
                        primeraSalida.set(anioActuaL, mesActuaL, diaActuaL, hora, minuto);
                        segundaBolean = true;
                        primero = false;
                    }
                }
                if ((total == 0) && (primeraBolean) && (segundaBolean)) {
                    // traer la primera salida y la segunda entrada

                    // por si las moscas ver si tiene permiso y las horas corresponden al permiso
                    parametros = new HashMap();
                    parametros.put(";where", "o.empleado=:empleado "
                            + " and :fecha= "
                            + " o.inicio and o.inicio=o.fin");
                    parametros.put("empleado", e);
                    parametros.put("fecha", hoy.getTime());
                    List<Licencias> licencias = ejbLicencias.encontarParametros(parametros);
                    boolean estaConPermiso = false;
                    for (Licencias l : licencias) {
                        Calendar fIncio = Calendar.getInstance();
                        fIncio.setTime(l.getDesde());
                        int licHoraInicio = fIncio.get(Calendar.HOUR_OF_DAY);
                        int licMinutoInicio = fIncio.get(Calendar.MINUTE);
                        fIncio.set(anioActuaL, mesActuaL, diaActuaL, licHoraInicio, licMinutoInicio);
                        Calendar fFin = Calendar.getInstance();
                        fFin.setTime(l.getHasta());
                        int licHoraFin = fIncio.get(Calendar.HOUR_OF_DAY);
                        int licMinutoFin = fIncio.get(Calendar.MINUTE);
                        fFin.set(anioActuaL, mesActuaL, diaActuaL, licHoraInicio, licMinutoInicio);

                    }
                    if (!estaConPermiso) {
//                        Ahora si ver si as timbradas estan en los rangos
                        // ver los timres que deben estar
                        String where = "o.cedula=:codigo and o.fechahora between :desde and :hasta";
                        parametros = new HashMap();
                        parametros.put(";where", where);
                        parametros.put("codigo", e.getEntidad().getPin());
                        parametros.put(";orden", "o.id desc");
                        parametros.put("desde", primeraSalida.getTime());
                        parametros.put("hasta", segundaEntrada.getTime());
                        // todas las timbradas en ese rango
//                        lo de arriba es para analizar
                        List<Marcacionesbiometrico> blista = ejbMarcaciones.encontarParametros(parametros);
                        int segundos = 0;
                        boolean primera = true;
                        int timbrada = 0;
                        for (Marcacionesbiometrico m : blista) {
                            if (primera) {
                                segundos += m.getFechahora().getTime() / 1000;
                                primera = false;
                            } else {
                                segundos -= m.getFechahora().getTime() / 1000;
                                primera = true;
                            }
                            timbrada++;
                        }
                        int cuanto = 0;
                        if (segundos < 0) {
                            cuanto = 60;
                        } else if (timbrada < 2) {
                            cuanto = 60;
                        } else if (segundos > 60) {
                            // generar el permiso
                            cuanto = segundos - 60;
                        }
                        if (cuanto > 0) {
//                            genera el permiso
                            q = em.createQuery("Select OBJECT(o) FROM Vacaciones as o "
                                    + "where o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
//                            q.setParameter("fin", hoy.getTime());
                            q.setParameter("anio", mesActuaL);
                            q.setParameter("mes", anioActuaL);
                            q.setParameter("empleado", e);
                            List<Vacaciones> listaVac = q.getResultList();
                            Vacaciones vac = null;
                            for (Vacaciones v1 : listaVac) {
                                vac = v1;
                            }
                            if (vac == null) {
                                vac = new Vacaciones();
                                vac.setEmpleado(e);
                                vac.setAnio(anioActuaL);
                                vac.setMes(mesActuaL);
                                vac.setGanado(0);
                                vac.setGanadofs(0);
                                int utilizado = (int) (cuanto * 22 / 30);
                                Integer utilizadofs = (int) (cuanto - utilizado);
                                vac.setUtilizado(utilizado);
                                vac.setUtilizadofs(utilizadofs);
                                em.persist(vac);
                            } else {
                                Integer vienen = vac.getUtilizado() == null ? 0 : vac.getUtilizado();
                                Integer vienenfs = vac.getUtilizadofs() == null ? 0 : vac.getUtilizadofs();
                                int utilizado = (int) (cuanto * 22 / 30);
                                Integer utilizadofs = (int) (cuanto - utilizado);
                                vac.setUtilizado(vienen + utilizado);
                                vac.setUtilizadofs(vienenfs + utilizadofs);
                                em.merge(vac);
                            }
                            Query q1 = em.createQuery("SELECT OBJECT(o) "
                                    + " FROM Tipopermisos as o "
                                    + " WHERE o.lounch=true and o.cargovacaciones=true");
                            Tipopermisos tipo = null;
                            List<Tipopermisos> listaTipos = q1.getResultList();
                            for (Tipopermisos t : listaTipos) {
                                tipo = t;
                            }
                            Licencias l = new Licencias();
                            l.setTipo(tipo);
                            l.setEmpleado(e);
                            l.setAutoriza(e);
                            l.setLegalizado(true);
                            l.setAprobadog(true);
                            l.setObsaprobacion("GENERADO AUTOMATICAMENTE POR FALTA EN TIEMPO DE LOUNCH");
                            l.setObslegalizacion("GENERADO AUTOMATICAMENTE POR FALTA EN TIEMPO DE LOUNCH");
                            l.setObservaciones("GENERADO AUTOMATICAMENTE POR FALTA EN TIEMPO DE LOUNCH");
                            l.setFechaingreso(hoy.getTime());
                            l.setFautoriza(hoy.getTime());
                            l.setFlegaliza(hoy.getTime());
                            l.setFechaingreso(hoy.getTime());
                            l.setInicio(hoy.getTime());
                            l.setFin(hoy.getTime());
                            l.setDesde(new Date());
                            l.setCantidad(cuanto);
                            l.setCantidad(cuanto);
                            Calendar hastaTiempo = Calendar.getInstance();
                            hastaTiempo.add(Calendar.MINUTE, cuanto);
                            l.setHasta(hastaTiempo.getTime());
                            em.persist(l);
                            System.out.println("Permiso creado para" + e.toString());
//                            ejbCorreos.enviarCorreo(e.getEntidad().getEmail(),
//                                    "PERMISO AUTOMATICO", "SE GENERO UN PERMISO CON CARGO A VACACIONES AUTOMATICAMENTE POR INSONSISTENCIA EN TIMBRADA A LA HORA DEL ALMUERZO");
                        }

                    }
                }

            }
            System.out.println("Terminado Proceso de Lounch");
        } catch (ConsultarException ex) {
            Logger.getLogger(ProcesoHoraLounchSinglenton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
