/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import org.beans.sfccbdmq.VacacionesFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Vacaciones;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class ProcesoVacaionesSingleton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    public static final double FACTOR_LUNES_A_VIERNES = 22 / 30;
    @EJB
    private VacacionesFacade ejbVacaciones;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "16", dayOfMonth = "*", year = "*", minute = "35", second = "0")
    @Schedule(dayOfWeek = "*", month = "*", hour = "01", dayOfMonth = "*", year = "*", minute = "40", second = "0")

    public void proporcionalvacaciones() {
        Calendar hoy = Calendar.getInstance();
        hoy.setTime(new Date());
        int dia = hoy.get(Calendar.DATE);
        int mes = hoy.get(Calendar.MONTH) + 1;
        int anio = hoy.get(Calendar.YEAR);

        Query q = em.createNativeQuery("SELECT proporcionalvacaciones(" + dia + "," + mes + "," + anio + ")");
        Integer result = (Integer) q.getSingleResult();
//        q.executeUpdate();
        Query qoct = em.createNativeQuery("SELECT proporcionalvacacionesoct(" + dia + "," + mes + "," + anio + ")");
        Integer result2 = (Integer) qoct.getSingleResult();
//        qoct.executeUpdate();
        SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaS = fecha.format(new Date());
        System.out.println("Terminado Proceso de Vacaciones " + fechaS);
    }

    //Descomentar despues 
    public void cargaVacaciones() {
//        Date hoy = new Date();
//        Calendar desde = Calendar.getInstance();
//        Calendar hasta = Calendar.getInstance();
//        int mes = desde.get(Calendar.MONTH) + 1;
//        int anio = desde.get(Calendar.YEAR);
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
////                    double saldoDoble = traerSaldo(e);
////                    if (saldoDoble > 60) {
////                        vac.setGanado(0);
////                        vac.setGanadofs(0);
////                        vac.setPerdido((int) saldoDoble - 60);
////                        em.merge(vac);
////                    }
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
////                        if (mesOnce == mes) {
////                            enceraProporcional(e);
////                        } else {
//                        vac.setProporcional(ganado + ganadofs);
////                        }
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
////                    A pedido de Delia pierde solo cuando se fectiviza 5Marzo 2018
////                    double saldoDoble = traerSaldo(e);
////                    if (saldoDoble > 60) {
////                        vac.setGanado(0);
////                        vac.setGanadofs(0);
//////                        vac.setProporcional(0);
////                        vac.setPerdido((int) saldoDoble - 60);
////                        em.merge(vac);
////                    }
//                } // fin if cyualquier mes
//
//            }
//        }
//        System.out.println("Terminado Proceso de Vacaciones");
    }

//    public BigDecimal factor(Empleados e) {
//        if (e.getCargoactual().getOperativo()) {
//            // contar los permisos con cargo a vacaciones
//            // esta puesto que sea solo los que no non por horas
//            Query q = em.createQuery("Select count(o) From Licencias as o "
//                    + "WHERE o.empleado=:empleado "
//                    //                    + "and o.tipo.cargovacaciones=true "
//                    //                    + " and  o.tipo.horas=false "
//                    + "and o.autoriza is not null and o.aprovado=true");
//            q.setParameter("empleado", e);
//            int contar = ((Long) q.getSingleResult()).intValue();
//
//            if (contar > 2) {
//                return e.getFactor();
//            }
//        }
//
//        return new BigDecimal(BigInteger.ONE);
//    }
//    public double diasPermiso(Licencias licencia) {
//        if (licencia == null) {
//            return 0;
//        }
//        if (licencia.getInicio() == null) {
//            return 0;
//        }
//        if (licencia.getFin() == null) {
//            return 0;
//        }
//        long retorno = ((licencia.getFin().getTime() - licencia.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
//        BigDecimal factorEncontrado = factor(licencia.getEmpleado());
//        double factor = 0;
//        if (factorEncontrado != null) {
//            factor = factorEncontrado.doubleValue();
//        }
//        return retorno;
//    }
//
//    public Integer horasPermiso(Licencias licencia) {
//        if (licencia == null) {
//            return null;
//        }
//        if (licencia.getDesde() == null) {
//            return null;
//        }
//        if (licencia.getHasta() == null) {
//            return null;
//        }
//        long retorno = (licencia.getHasta().getTime() - licencia.getDesde().getTime()) / 60000;
////        if (licencia.isLounch()) {
////            Codigos tiempoLounch = codigosBean.traerCodigo("MAXT", "LOUNCH");
////            int tiempo = Integer.parseInt(tiempoLounch.getParametros());
////            retorno -= tiempo;
////        }
//        if (retorno < 0) {
//            Calendar c = Calendar.getInstance();
//            c.setTime(licencia.getInicio());
//            c.add(Calendar.DATE, 1);
//            licencia.setFin(c.getTime());
//        } else {
//            licencia.setFin(licencia.getInicio());
//        }
//        BigDecimal factorEncontrado = factor(licencia.getEmpleado());
//        double factor = 0;
//        if (factorEncontrado != null) {
//            factor = factorEncontrado.doubleValue();
//        }
////        String strRetorno = String.valueOf(retorno + factor - 1);
//        String strRetorno = String.valueOf(retorno);
//        licencia.setHoras((int) (retorno));
////        licencia.setHoras((int) (retorno + factor - 1));
//        return (int) (retorno);
//    }
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

    private double traerSaldo(Empleados e) {
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
}
