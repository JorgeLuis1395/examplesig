/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Vacaciones;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class ProcesoTemporalVacaionesSingleton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    private static final double FACTOR_LUNES_A_VIERNES = 22 / 30;
    @EJB
    private VacacionesFacade ejbVacaciones;
    @EJB
    private LicenciasFacade ejbLicencias;
//    int i = 0;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "10", dayOfMonth = "*", year = "*", minute = "00", second = "0")
    public String ejecuta() {
        for (int i = 1; i <= 26; i++) {
            String letra = "A";
            switch (i) {
                case 1:
                    letra = "B";
                    break;
                case 2:
                    letra = "C";
                    break;
                case 3:
                    letra = "D";
                    break;
                case 4:
                    letra = "E";
                    break;
                case 5:
                    letra = "F";
                    break;
                case 6:
                    letra = "G";
                    break;
                case 7:
                    letra = "H";
                    break;
                case 8:
                    letra = "I";
                    break;
                case 9:
                    letra = "J";
                    break;
                case 10:
                    letra = "K";
                    break;
                case 11:
                    letra = "L";
                    break;
                case 12:
                    letra = "M";
                    break;
                case 13:
                    letra = "N";
                    break;
                case 14:
                    letra = "Ñ";
                    break;
                case 15:
                    letra = "O";
                    break;
                case 16:
                    letra = "P";
                    break;
                case 17:
                    letra = "Q";
                    break;
                case 18:
                    letra = "R";
                    break;
                case 19:
                    letra = "S";
                    break;
                case 20:
                    letra = "T";
                    break;
                case 21:
                    letra = "U";
                    break;
                case 22:
                    letra = "V";
                    break;
                case 23:
                    letra = "W";
                    break;
                case 24:
                    letra = "X";
                    break;
                case 25:
                    letra = "Y";
                    break;
                case 26:
                    letra = "Z";
                    break;
                default:
                    break;
            }
            System.out.println("Incia Proceso de Vacaciones : " + letra);
            cargaTemporalVacaciones(letra);
        }
        System.out.println("Terminado Proceso de Vacaciones");
        return "Terminado Proceso de Vacaciones";
    }

    public void cargaTemporalVacaciones(String letra) {
        Calendar hoy = Calendar.getInstance();
//Necesitamos desde noviembre por empleado o desde septiembre ya veo la base
//        Calendar hoy1 = Calendar.getInstance();
//        hoy1.set(2018, 0, 16);
//        Calendar desde = Calendar.getInstance();
//        desde.set(2017, 0, 1);
        int mesMaximo = hoy.get(Calendar.MONTH);
        Query q = em.createQuery("SELECT Object(o) "
                + " FROM  Empleados as o "
                + " WHERE o.cargoactual is not null and o.entidad.apellidos like '" + letra + "%' "
                + "  and o.activo=true "
                + " order by o.entidad.apellidos", Empleados.class);
        List<Empleados> lista = q.getResultList();
        for (Empleados e : lista) {
            int anio = 2017;
            int mes = -1;
            for (int i = 0; i <= mesMaximo; i++) {
                mes++;
                if (i == 12) {
                    anio = 2018;
                    mes = 0;
                }
                q = em.createQuery("SELECT OBJECT(o) "
                        + " FROM Vacaciones as o "
                        + " WHERE o.anio=:anio "
                        + " AND o.mes=:mes "
                        + " AND o.empleado=:empleado");
                q.setParameter("mes", mes + 1);
                q.setParameter("anio", anio);
                q.setParameter("empleado", e);
                List<Vacaciones> listaVacaiones = q.getResultList();
                Vacaciones vac = null;
                for (Vacaciones v : listaVacaiones) {
                    vac = v;
                }

                if (vac != null) {
                    Calendar desde = Calendar.getInstance();
                    Calendar hasta = Calendar.getInstance();
                    desde.set(anio, mes, 1);
                    hasta.set(anio, mes + 1, 1);
                    hasta.add(Calendar.DATE, -1);

                    // buscar las licencias de ese mes
                    Query q1 = em.createQuery("Select OBJECT(o) FROM Licencias as o "
                            + " WHERE o.inicio between :desde "
                            + " and :hasta and o.empleado=:empleado AND o.fechaanula is null "
                            + " and o.aprovado=true and o.legalizado=true");
                    q1.setParameter("desde", desde.getTime());
                    q1.setParameter("hasta", hasta.getTime());
                    q1.setParameter("empleado", e);
                    List<Licencias> licencias = q1.getResultList();
                    double utilizado = 0;
                    for (Licencias l : licencias) {
                        ejbLicencias.calculaTiempo(l);
                        utilizado += l.getCuanto();
//                        }
                    }
                    // poner lo utilizado
                    int usado = (int) (utilizado * 22 / 30);
                    Integer usadofs = (int) (utilizado - usado);
                    vac.setUtilizado(usado);
                    vac.setUtilizadofs(usadofs);
                    em.merge(vac);
                }
            }
        }
    }
    private void enceraUtilizado(Empleados emp) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // create update
        CriteriaUpdate<Vacaciones> update = cb.
                createCriteriaUpdate(Vacaciones.class);

        // set the root class
        Root e = update.from(Vacaciones.class);

        // set update and where clause
        update.set("utilizado", 0);
        update.where(cb.equal(e.get("empleado"), emp));

        // perform update
        this.em.createQuery(update).executeUpdate();
    }
    private void enceraUtilizadoFs(Empleados emp) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // create update
        CriteriaUpdate<Vacaciones> update = cb.
                createCriteriaUpdate(Vacaciones.class);

        // set the root class
        Root e = update.from(Vacaciones.class);

        // set update and where clause
        update.set("utilizadofs", 0);
        update.where(cb.equal(e.get("empleado"), emp));

        // perform update
        this.em.createQuery(update).executeUpdate();
    }
    public void cargaTemporalVacacionesCedula(String cedula) {
        Calendar hoy = Calendar.getInstance();
//Necesitamos desde noviembre por empleado o desde septiembre ya veo la base
//        Calendar hoy1 = Calendar.getInstance();
//        hoy1.set(2018, 0, 16);
//        Calendar desde = Calendar.getInstance();
//        desde.set(2017, 0, 1);
//        int mesMaximo = hoy.get(Calendar.MONTH)+12;
        int mesMaximo = hoy.get(Calendar.MONTH) + 12;
        Query q = em.createQuery("SELECT Object(o) "
                + " FROM  Empleados as o "
                + " WHERE o.cargoactual is not null and o.entidad.pin = :cedula"
                + "  and o.activo=true "
                + " order by o.entidad.apellidos", Empleados.class);
        q.setParameter("cedula", cedula);
        List<Empleados> lista = q.getResultList();
        for (Empleados e : lista) {
//            enceraUtilizado(e);
//            enceraUtilizadoFs(e);
            Query qVac=em.createQuery("SELECT OBJECT(o) FROM Vacaciones as o WHERE o.empleado=:empleado");
            qVac.setParameter("empleado", e);
            List<Vacaciones> listaV=qVac.getResultList();
            for (Vacaciones va:listaV){
                va.setUtilizado(0);
                va.setUtilizadofs(0);
                em.merge(va);
            }
            System.out.println("Inicia Proceso de Vacaciones " + e.toString());
            int anio = 2017;
            int mes = -1;
            // entra adentro
            Query q1 = em.createQuery("Select OBJECT(o) FROM Licencias as o "
                    + " WHERE  "
                    + " o.empleado=:empleado AND o.fechaanula is null "
                    + " and o.aprovado=true and o.legalizado=true");

            q1.setParameter("empleado", e);
            List<Licencias> licencias = q1.getResultList();
            double utilizado = 0;
            for (Licencias l : licencias) {
                ejbLicencias.calculaTiempo(l);
                utilizado = l.getCuanto();
                Calendar calendario = Calendar.getInstance();
                calendario.setTime(l.getInicio());
                anio = calendario.get(Calendar.YEAR);
                mes = calendario.get(Calendar.MONTH);
                q = em.createQuery("SELECT OBJECT(o) "
                        + " FROM Vacaciones as o "
                        + " WHERE o.anio=:anio "
                        + " AND o.mes=:mes "
                        + " AND o.empleado=:empleado");
                q.setParameter("mes", mes + 1);
                q.setParameter("anio", anio);
                q.setParameter("empleado", e);
                List<Vacaciones> listaVacaiones = q.getResultList();
                Vacaciones vac = null;
                for (Vacaciones v : listaVacaiones) {
                    vac = v;
                }
                if (vac != null) {
                    int usadoAnterior = vac.getUtilizado();
                    int usadoAnteriorFs = vac.getUtilizadofs();
                    int usado = (int) (utilizado * 22 / 30);
                    Integer usadofs = (int) (utilizado - usado);
                    vac.setUtilizado(usadoAnterior + usado);
                    vac.setUtilizadofs(usadoAnteriorFs + usadofs);
                    em.merge(vac);
                } else {
                    vac=new Vacaciones();
                    int usado = (int) (utilizado * 22 / 30);
                    Integer usadofs = (int) (utilizado - usado);
                    vac.setUtilizado(usado);
                    vac.setGanado(0);
                    vac.setGanadofs(0);
                    vac.setProporcional(0);
                    vac.setMes(mes+1);
                    vac.setAnio(anio);
                    vac.setEmpleado(e);
                    vac.setUtilizadofs(usadofs);
                    em.merge(vac);
                }
//                        }
            }
            // fin entra adentro

        }
        System.out.println("Fin proceso");
    }

    public void cargaTemporalVacacionesCedula1(String cedula) {
        Calendar hoy = Calendar.getInstance();
//Necesitamos desde noviembre por empleado o desde septiembre ya veo la base
//        Calendar hoy1 = Calendar.getInstance();
//        hoy1.set(2018, 0, 16);
//        Calendar desde = Calendar.getInstance();
//        desde.set(2017, 0, 1);
//        int mesMaximo = hoy.get(Calendar.MONTH)+12;
        int mesMaximo = hoy.get(Calendar.MONTH) + 12;
        Query q = em.createQuery("SELECT Object(o) "
                + " FROM  Empleados as o "
                + " WHERE o.cargoactual is not null and o.entidad.pin = :cedula"
                + "  and o.activo=true "
                + " order by o.entidad.apellidos", Empleados.class);
        q.setParameter("cedula", cedula);
        List<Empleados> lista = q.getResultList();
        for (Empleados e : lista) {
            System.out.println("Inicia Proceso de Vacaciones " + e.toString());
            int anio = 2017;
            int mes = -1;
            for (int i = 0; i <= mesMaximo; i++) {
                mes++;
                if (i == 12) {
                    anio = 2018;
                    mes = 0;
                }
                q = em.createQuery("SELECT OBJECT(o) "
                        + " FROM Vacaciones as o "
                        + " WHERE o.anio=:anio "
                        + " AND o.mes=:mes "
                        + " AND o.empleado=:empleado");
                q.setParameter("mes", mes + 1);
                q.setParameter("anio", anio);
                q.setParameter("empleado", e);
                List<Vacaciones> listaVacaiones = q.getResultList();
                Vacaciones vac = null;
                for (Vacaciones v : listaVacaiones) {
                    vac = v;
                }

                if (vac != null) {
                    Calendar desde = Calendar.getInstance();
                    Calendar hasta = Calendar.getInstance();
                    desde.set(anio, mes, 1);
                    hasta.set(anio, mes + 1, 1);
                    hasta.add(Calendar.DATE, -1);

                    // buscar las licencias de ese mes
                    Query q1 = em.createQuery("Select OBJECT(o) FROM Licencias as o "
                            + " WHERE o.inicio between :desde "
                            + " and :hasta and o.empleado=:empleado AND o.fechaanula is null "
                            + " and o.aprovado=true and o.legalizado=true");
                    q1.setParameter("desde", desde.getTime());
                    q1.setParameter("hasta", hasta.getTime());
                    q1.setParameter("empleado", e);
                    List<Licencias> licencias = q1.getResultList();
                    double utilizado = 0;
                    for (Licencias l : licencias) {
                        ejbLicencias.calculaTiempo(l);
                        utilizado += l.getCuanto();
//                        }
                    }
                    // poner lo utilizado
                    int usado = (int) (utilizado * 22 / 30);
                    Integer usadofs = (int) (utilizado - usado);
                    vac.setUtilizado(usado);
                    vac.setUtilizadofs(usadofs);
                    em.merge(vac);
                }
            }
        }
        System.out.println("Fin proceso");
    }

    //Volvera validar despues
    public void cargaVacaciones() {
//        Date hoy = new Date();
//        Calendar desde = Calendar.getInstance();
//        Calendar hasta = Calendar.getInstance();
////        int mes = desde.get(Calendar.MONTH) + 1;
////        int anio = desde.get(Calendar.YEAR);
//        int dia = desde.get(Calendar.DATE);
//        Calendar mesSiguiente = Calendar.getInstance();
//        mesSiguiente.add(Calendar.DATE, 1);
//        int ulTimoDiaMes = 0;
//        if (mesSiguiente.get(Calendar.DATE) == 1) {
//            ulTimoDiaMes = 31;
//        }
//        Query q = em.createQuery("SELECT Object(o) "
//                + " FROM  Empleados as o "
//                + " WHERE o.cargoactual is not null "
//                + " and o.fechasalida is null and o.activo=true", Empleados.class);
//        List<Empleados> lista = q.getResultList();
//        int anio = 2018;
//        for (Empleados e : lista) {
//            Calendar fechaEntrada = Calendar.getInstance();
//            fechaEntrada.setTime(e.getFechaingreso());
//            int mesEntrada = fechaEntrada.get(Calendar.MONTH) + 1;
//            int diaEntrada = fechaEntrada.get(Calendar.DATE);
//            for (int mes = 0; mes <= 2; mes++) {
//                System.out.println("Proceso para :" + e.getEntidad().toString() + " " + diaEntrada);
//                int mesOnce = mesEntrada - 1;
//                int anioOnce = anio;
//                if (mesOnce == 0) {
//                    mesOnce = 12;
//                    anioOnce--;
//                }
//                if (mesOnce == mes) {
//                    q = em.createQuery("SELECT OBJECT(o) "
//                            + " FROM Vaccaciones as o "
//                            + " WHERE o.anio=:anio "
//                            + " AND o.mes=:mes "
//                            + " AND o.empleado=:empleado");
//                    q.setParameter("mes", mesOnce);
//                    q.setParameter("anio", anio);
//                    q.setParameter("empleado", e);
//                    List<Vaccaciones> listaVacaiones = q.getResultList();
//                    Vaccaciones vac = null;
//                    for (Vaccaciones v : listaVacaiones) {
//                        vac = v;
//                    }
//                    double cuanto = 2.5 * 2;
//                    if (e.getCargoactual().getTipocontrato() != null) {
//                        if (e.getCargoactual().getTipocontrato().getFactovacaciones() != null) {
//                            cuanto = e.getCargoactual().getTipocontrato().getFactovacaciones().doubleValue() * 2;
//                        }
//                    }
//                    cuanto = cuanto * 8 * 60;
//                    if (vac == null) {
//                        vac = new Vaccaciones();
//                        vac.setEmpleado(e);
//                        vac.setAnio(anio);
//                        vac.setMes(mes);
//                        vac.setGanado(0);
//                        vac.setGanadofs(0);
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setUtilizado(0);
//                        vac.setGanado(ganado);
//                        vac.setGanadofs(ganadofs);
//                        vac.setUtilizadofs(0);
////                        vac.setProporcional(ganado + ganadofs);
//                        em.persist(vac);
//                    } else {
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setGanado(ganado);
////                        vac.setProporcional(ganado + ganadofs);
//                        vac.setGanadofs(ganadofs);
//                        em.merge(vac);
//                    }
//
//                } else if (mesEntrada == mes) {
//                    // No hace nadda
//                } else {
//                    // mes normal
//                    q = em.createQuery("SELECT OBJECT(o) "
//                            + " FROM Vaccaciones as o "
//                            + " WHERE o.anio=:anio "
//                            + " AND o.mes=:mes "
//                            + " AND o.empleado=:empleado");
//                    q.setParameter("mes", mes);
//                    q.setParameter("anio", anio);
//                    q.setParameter("empleado", e);
//                    List<Vaccaciones> listaVacaiones = q.getResultList();
//                    Vaccaciones vac = null;
//                    for (Vaccaciones v : listaVacaiones) {
//                        vac = v;
//                    }
//                    double cuanto = 2.5;
//                    if (e.getCargoactual().getTipocontrato() != null) {
//                        if (e.getCargoactual().getTipocontrato().getFactovacaciones() != null) {
//                            cuanto = e.getCargoactual().getTipocontrato().getFactovacaciones().doubleValue();
//                        }
//                    }
//                    cuanto = cuanto * 8 * 60;
//                    if (vac == null) {
//                        vac = new Vaccaciones();
//                        vac.setEmpleado(e);
//                        vac.setAnio(anio);
//                        vac.setMes(mes);
//                        vac.setGanado(0);
//                        vac.setGanadofs(0);
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setUtilizado(0);
//                        vac.setGanado(ganado);
//                        vac.setGanadofs(ganadofs);
//                        vac.setUtilizadofs(0);
//                        em.persist(vac);
//                    } else {
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setGanado(ganado);
//                        vac.setGanadofs(ganadofs);
//                        em.merge(vac);
//                    }
//
//                } // fin if cyualquier mes
//
//            }
//        }
//        System.out.println("Terminado Proceso de Vacaciones");
    }

    public void cargaTemporalVacacionesAnt(String letra) {
//        Date hoy = new Date();
//Necesitamos desde noviembre por empleado o desde septiembre ya veo la base
//        Calendar hoy1 = Calendar.getInstance();
//        hoy1.set(2018, 0, 16);
//        Calendar desde = Calendar.getInstance();
//        desde.set(2017, 0, 1);
        Query q = em.createQuery("SELECT Object(o) "
                + " FROM  Empleados as o "
                + " WHERE o.cargoactual is not null and o.entidad.apellidos like '" + letra + "%' "
                + "  and o.activo=true and o.operativo=true"
                + " order by o.entidad.apellidos", Empleados.class);
        List<Empleados> lista = q.getResultList();
        for (Empleados e : lista) {
            int anio = 2017;
            int mes = 0;
            for (int i = 0; i <= 12; i++) {
                mes = i;
                if (i == 12) {
                    anio = 2018;
                    mes = 0;
                }
                q = em.createQuery("SELECT OBJECT(o) "
                        + " FROM Vacaciones as o "
                        + " WHERE o.anio=:anio "
                        + " AND o.mes=:mes "
                        + " AND o.empleado=:empleado");
                q.setParameter("mes", mes + 1);
                q.setParameter("anio", anio);
                q.setParameter("empleado", e);
                List<Vacaciones> listaVacaiones = q.getResultList();
                Vacaciones vac = null;
                for (Vacaciones v : listaVacaiones) {
                    vac = v;
                }

                if (vac != null) {
                    Calendar desde = Calendar.getInstance();
                    Calendar hasta = Calendar.getInstance();
                    desde.set(anio, mes, 1);
                    hasta.set(anio, mes + 1, 1);
                    hasta.add(Calendar.DATE, -1);

                    // buscar las licencias de ese mes
                    Query q1 = em.createQuery("Select OBJECT(o) FROM Licencias as o "
                            + " WHERE o.inicio between :desde "
                            + " and :hasta and o.empleado=:empleado AND o.fechaanula is null "
                            + " and o.aprovado=true and o.legalizado=true");
                    q1.setParameter("desde", desde.getTime());
                    q1.setParameter("hasta", hasta.getTime());
                    q1.setParameter("empleado", e);
                    List<Licencias> licencias = q1.getResultList();
                    double utilizado = 0;
                    for (Licencias l : licencias) {
                        if (l.getCuanto() != null) {
                            // calculemos cuanto

//                            if (l.getCuanto() == 1) {
                            // dañados
                            long dias = 0;
                            long horas = 0;
                            if (l.getInicio().getTime() == l.getFin().getTime()) {
//                                solo horas
                                horas = l.getHasta().getTime() - l.getDesde().getTime();
                            } else {
                                // paso a minutos los dias por 24 convertir las fechas en calendar para restar
                                Calendar desdeCalendario = Calendar.getInstance();
                                Calendar hastaCalendario = Calendar.getInstance();
                                Calendar auxiliar = Calendar.getInstance();
                                // empieso con la fecha
                                auxiliar.setTime(l.getInicio());
                                int diaAuxiliar = auxiliar.get(Calendar.DATE);
                                int anioAuxiliar = auxiliar.get(Calendar.YEAR);
                                int mesAuxiliar = auxiliar.get(Calendar.MONTH);
                                // ahora armo las horas y minutos
                                auxiliar.setTime(l.getDesde());
                                int horaAuxiliar = auxiliar.get(Calendar.HOUR_OF_DAY);
                                int minutoAuxiliar = auxiliar.get(Calendar.MINUTE);
                                // armo la fecha
                                desdeCalendario.set(anioAuxiliar, mesAuxiliar, diaAuxiliar, horaAuxiliar, minutoAuxiliar);
                                // fecha fin
                                auxiliar.setTime(l.getFin());
                                diaAuxiliar = auxiliar.get(Calendar.DATE);
                                anioAuxiliar = auxiliar.get(Calendar.YEAR);
                                mesAuxiliar = auxiliar.get(Calendar.MONTH);
                                // ahora armo las horas y minutos
                                auxiliar.setTime(l.getHasta());
                                horaAuxiliar = auxiliar.get(Calendar.HOUR_OF_DAY);
                                minutoAuxiliar = auxiliar.get(Calendar.MINUTE);
                                hastaCalendario.set(anioAuxiliar, mesAuxiliar, diaAuxiliar, horaAuxiliar, minutoAuxiliar);
                                dias = 0;
                                horas = hastaCalendario.getTimeInMillis() - desdeCalendario.getTimeInMillis();

                            }
                            // en milisegundos traasformos a minutos viendo el operativo
                            BigDecimal factor = factor(e);
                            double valorFactor = 1;
                            if (factor != null) {
                                valorFactor = factor.doubleValue();
                            }
                            int minutos = 0;
//                            if (e.getOperativo()) {
//                                minutos = (int) (dias / (1000 * 60 + 60 * 24) + horas / (1000 * 60) * valorFactor);
//                                l.setCuanto(minutos);
//                            } else {
                            minutos = (int) (dias / (1000 * 60 + 60 * 8) + horas / (1000 * 60) * valorFactor);
                            l.setCuanto(minutos);
//                            }
                            if (l.getCantidad() == null) {
                                l.setCantidad(0);
                            }
                            if (l.getCuanto() < l.getCantidad()) {
                                l.setCuanto(l.getCantidad());
                            }
                            System.out.println("Proceso para :"
                                    + e.getEntidad().toString() + " ID: " + e.getId() + " CUANTO:"
                                    + String.valueOf(minutos) + " - " + l.getCuanto());
                            em.merge(l);
//                            if (l.getCantidad() != null) {
//                                l.setCuanto(l.getCantidad());
//                                em.merge(l);
//                            }
                        }
                        utilizado += l.getCuanto();
//                        }
                    }
                    // poner lo utilizado
                    int usado = (int) (utilizado * 22 / 30);
                    Integer usadofs = (int) (utilizado - usado);
                    vac.setUtilizado(usado);
                    vac.setUtilizadofs(usadofs);
                    em.merge(vac);
                }
            }
        }
//            Calendar inicio = Calendar.getInstance();
//            Calendar fin = Calendar.getInstance();
//            

    }


    private BigDecimal factor(Empleados e) {
        if (e.getCargoactual().getOperativo()) {
            // contar los permisos con cargo a vacaciones
            // esta puesto que sea solo los que no non por horas
            Query q = em.createQuery("Select count(o) From Licencias as o "
                    + "WHERE o.empleado=:empleado and o.tipo.tipo=1 "
                    //                    + "and o.tipo.cargovacaciones=true "
                    //                    + " and  o.tipo.horas=false "
                    + "and o.autoriza is not null and o.aprovado=true");
            q.setParameter("empleado", e);
            int contar = ((Long) q.getSingleResult()).intValue();

            if (contar > 2) {
                return e.getFactor();
            }
        }

        return new BigDecimal(BigInteger.ONE);
    }

    private double diasPermiso(Licencias licencia) {
        if (licencia == null) {
            return 0;
        }
        if (licencia.getInicio() == null) {
            return 0;
        }
        if (licencia.getFin() == null) {
            return 0;
        }
        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
        BigDecimal factorEncontrado = factor(licencia.getEmpleado());
        double factor = 0;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
        return retorno;
    }

    private Integer horasPermiso(Licencias licencia) {
        if (licencia == null) {
            return null;
        }
        if (licencia.getDesde() == null) {
            return null;
        }
        if (licencia.getHasta() == null) {
            return null;
        }
        long retorno = (licencia.getHasta().getTime() - licencia.getDesde().getTime()) / 60000;
//        if (licencia.isLounch()) {
//            Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
//            int tiempo = Integer.parseInt(tiempoLounch.getParametros());
//            retorno -= tiempo;
//        }
        if (retorno < 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(licencia.getInicio());
            c.add(Calendar.DATE, 1);
            licencia.setFin(c.getTime());
        } else {
            licencia.setFin(licencia.getInicio());
        }
        BigDecimal factorEncontrado = factor(licencia.getEmpleado());
        double factor = 0;
        if (factorEncontrado != null) {
            factor = factorEncontrado.doubleValue();
        }
//        String strRetorno = String.valueOf(retorno + factor - 1);
        String strRetorno = String.valueOf(retorno);
        licencia.setHoras((int) (retorno));
//        licencia.setHoras((int) (retorno + factor - 1));
        return (int) (retorno);
    }

    private int extremos(Date desde, Date hasta) {
        int dias = (int) ((desde.getTime() - hasta.getTime()) / (1000 * 60 * 60 * 24));
        Calendar fecha = Calendar.getInstance();
        int retorno = 0;
        if (dias > 0) {
            fecha.setTime(desde);
            for (int i = 0; i <= dias; i++) {
                Query q2 = em.createQuery("Select COUNT(o) "
                        + " From  Festivos as o Where o.fecha =:fecha "
                        + " ");

                q2.setParameter("fecha", fecha.getTime());
                int cuanto = (int) q2.getSingleResult();
                if (cuanto == 0) {
                    return retorno;
                }
                retorno++;
            }
        } else {
            fecha.setTime(hasta);
            dias *= -1;
            for (int i = dias; i == 0; i--) {
                Query q2 = em.createQuery("Select COUNT(o) "
                        + " From  Festivos as o Where o.fecha =:fecha "
                        + " ");

                q2.setParameter("fecha", fecha.getTime());
                int cuanto = (int) q2.getSingleResult();
                if (cuanto == 0) {
                    return retorno;
                }
                retorno++;
            }
        }
        return retorno;
    }

    private void enceraProporcional(Empleados emp) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // create update
        CriteriaUpdate<Vacaciones> update = cb.
                createCriteriaUpdate(Vacaciones.class);

        // set the root class
        Root e = update.from(Vacaciones.class);

        // set update and where clause
        update.set("proporcional", 0);
        update.where(cb.equal(e.get("empleado"), emp));

        // perform update
        this.em.createQuery(update).executeUpdate();
    }

    private void enceraProporcional() {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // create update
        CriteriaUpdate<Vacaciones> update = cb.
                createCriteriaUpdate(Vacaciones.class);

        // set the root class
        Root e = update.from(Vacaciones.class);

        // set update and where clause
        update.set("proporcional", 0);

        // perform update
        this.em.createQuery(update).executeUpdate();
    }

    private double traerSaldo1(Empleados e) {
        // Traer la suma de vacaiones
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado");
        parametros.put(";campo", "o.empleado.id");
        parametros.put("empleado", e);
        parametros.put(";suma", "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),sum(o.utilizadofs),sum(o.proporcional)");
        double saldo = 0;
        String sql = "Select " + "sum(o.utilizado),sum(o.ganado),sum(o.ganadofs),sum(o.utilizadofs),sum(o.proporcional)" + " from " + "Vacaciones"
                + " as o " + "Where o.empleado=:empleado";
        Query q = em.createQuery(sql);
        q.setParameter("empleado", e);
        List<Object[]> lista = q.getResultList();
        for (Object[] o : lista) {
            Long utilizado = (Long) o[0];
            Long utilizadofs = (Long) o[3];
            Long ganado = (Long) o[1];
            Long ganadofs = (Long) o[2];
            Long proporcional = (Long) o[4];
            if (utilizado == null) {
                utilizado = new Long(0);
            }
            if (utilizadofs == null) {
                utilizadofs = new Long(0);
            }
            if (ganado == null) {
                ganado = new Long(0);
            }
            if (ganadofs == null) {
                ganadofs = new Long(0);
            }
            if (proporcional == null) {
                proporcional = new Long(0);
            }
            utilizado += utilizadofs;
            ganado += ganadofs;
            // vamos con lo utilizado
            saldo = ejbVacaciones.cuatosDias(ganado - utilizado, e.getOperativo(), true);
        }

        return saldo;
    }
    
    // nuevo para ejecutar desde la interfaz enviando mes año y dia  y empleado
    //Descomentar despues
    public void calculaGanadoVacaciones(int anio,int mes, int dia ) {
//        Date hoy = new Date();
//        Calendar mesSiguiente = Calendar.getInstance();
//        mesSiguiente.add(Calendar.DATE, 1);
//        int ulTimoDiaMes = 0;
//        if (mesSiguiente.get(Calendar.DATE) == 1) {
//            ulTimoDiaMes = 31;
//        }
//        Query q = em.createQuery("SELECT Object(o) "
//                + " FROM  Empleados as o "
//                + " WHERE o.cargoactual is not null "
//                + " and o.fechasalida is null and o.activo=true", Empleados.class);
//        List<Empleados> lista = q.getResultList();
//        for (Empleados e : lista) {
//            Calendar fechaEntrada = Calendar.getInstance();
//            fechaEntrada.setTime(e.getFechaingreso());
//            int mesEntrada = fechaEntrada.get(Calendar.MONTH) + 1;
//            int diaEntrada = fechaEntrada.get(Calendar.DATE);
//            
//            if ((diaEntrada == dia) || (diaEntrada <= ulTimoDiaMes)) {
//                System.out.println("Proceso para :" + e.getEntidad().toString() + " " + diaEntrada);
////                if (e.getId() == 597) {
////                    int x = 0;
////                }
//                int mesOnce = mesEntrada - 1;
//                int anioOnce = anio;
//                if (mesOnce == 0) {
//                    mesOnce = 12;
//                    anioOnce--;
//                }
//                if (mesOnce == mes) {
//                    q = em.createQuery("SELECT OBJECT(o) "
//                            + " FROM Vaccaciones as o "
//                            + " WHERE o.anio=:anio "
//                            + " AND o.mes=:mes "
//                            + " AND o.empleado=:empleado");
//                    q.setParameter("mes", mesOnce);
//                    q.setParameter("anio", anio);
//                    q.setParameter("empleado", e);
//                    List<Vaccaciones> listaVacaiones = q.getResultList();
//                    Vaccaciones vac = null;
//                    for (Vaccaciones v : listaVacaiones) {
//                        vac = v;
//                    }
//                    double cuanto = 2.5 * 2;
//                    if (e.getCargoactual().getTipocontrato() != null) {
//                        if (e.getCargoactual().getTipocontrato().getFactovacaciones() != null) {
//                            cuanto = e.getCargoactual().getTipocontrato().getFactovacaciones().doubleValue() * 2;
//                        }
//                    }
//                    cuanto = cuanto * 8 * 60;
//                    if (vac == null) {
//                        vac = new Vaccaciones();
//                        vac.setEmpleado(e);
//                        vac.setAnio(anio);
//                        vac.setMes(mes);
//                        vac.setGanado(0);
//                        vac.setGanadofs(0);
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setUtilizado(0);
//                        vac.setGanado(ganado);
//                        vac.setGanadofs(ganadofs);
//                        vac.setUtilizadofs(0);
//                        vac.setProporcional(ganado + ganadofs);
//                        em.persist(vac);
//                    } else {
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setGanado(ganado);
//                        vac.setProporcional(ganado + ganadofs);
//                        vac.setGanadofs(ganadofs);
//                        em.merge(vac);
//                    }
//                    enceraProporcional(e);
//                } else if (mesEntrada == mes) {
//                    // No hace nadda
//                } else {
//                    // mes normal
//                    q = em.createQuery("SELECT OBJECT(o) "
//                            + " FROM Vaccaciones as o "
//                            + " WHERE o.anio=:anio "
//                            + " AND o.mes=:mes "
//                            + " AND o.empleado=:empleado");
//                    q.setParameter("mes", mes);
//                    q.setParameter("anio", anio);
//                    q.setParameter("empleado", e);
//                    List<Vaccaciones> listaVacaiones = q.getResultList();
//                    Vaccaciones vac = null;
//                    for (Vaccaciones v : listaVacaiones) {
//                        vac = v;
//                    }
//                    double cuanto = 2.5;
//                    if (e.getCargoactual().getTipocontrato() != null) {
//                        if (e.getCargoactual().getTipocontrato().getFactovacaciones() != null) {
//                            cuanto = e.getCargoactual().getTipocontrato().getFactovacaciones().doubleValue();
//                        }
//                    }
//                    cuanto = cuanto * 8 * 60;
//                    if (vac == null) {
//                        vac = new Vaccaciones();
//                        vac.setEmpleado(e);
//                        vac.setAnio(anio);
//                        vac.setMes(mes);
//                        vac.setGanado(0);
//                        vac.setGanadofs(0);
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setUtilizado(0);
//                        vac.setGanado(ganado);
//                        vac.setGanadofs(ganadofs);
//                        vac.setUtilizadofs(0);
//                        vac.setProporcional(ganado + ganadofs);
//                        em.persist(vac);
//                    } else {
//                        int ganado = (int) (cuanto * 22 / 30);
//                        Integer ganadofs = (int) (cuanto - ganado);
//                        vac.setGanado(ganado);
//                        if (mesEntrada == mes) {
//                            enceraProporcional(e);
//                        } else {
//                            vac.setProporcional(ganado + ganadofs);
//                        }
//                        vac.setGanadofs(ganadofs);
//                        em.merge(vac);
//                    }
//                } // fin if cyualquier mes
//
//            }
//        }
//        System.out.println("Terminado Proceso de Vacaciones");
    }
}
