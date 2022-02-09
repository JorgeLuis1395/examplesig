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
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Licencias;
import org.entidades.sfccbdmq.Vacaciones;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class legalizarVacaionesSingleton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    public static final double FACTOR_LUNES_A_VIERNES = 22 / 30;

//    @Schedule(dayOfWeek = "*", month = "*", hour = "0", dayOfMonth = "*", year = "*", minute = "45", second = "0")
    public void legalizaVacaciones() {
        Date hoy = new Date();
        Calendar desde = Calendar.getInstance();
        Calendar hasta = Calendar.getInstance();
        int mes = desde.get(Calendar.MONTH) + 1;
        int anio = desde.get(Calendar.YEAR);
        

        // legalizar vacaciones y permisos que fecha fin un dia mos sea legalizable automatizamente 
        desde.add(Calendar.DATE, -1);
        Query q = em.createQuery("Select OBJECT(o) FROM Licencias as o where o.fin=:fin and o.aprovado=true "
                + " and (o.fgerencia is not null  or o.fvalida is not null) and o.flegaliza is null "
                + " and o.tipo.legaliza=true");
        q.setParameter("fin", desde.getTime());
        List<Licencias> listaLicencias = q.getResultList();
        for (Licencias l : listaLicencias) {
            // ver si son normales sin cargo a vacaiones
            l.setFlegaliza(hoy);
            l.setFretorno(l.getFin());
            l.setLegalizado(Boolean.TRUE);
            l.setObslegalizacion("Permiso Legalizado Automáticamente");
            long cuanto = 0;
            if (l.getTipo().getTipo() != 2) {
                if (l.getCargoavacaciones()) {
                    // carga a vaciones
                    if (l.getTipo().getHoras()) {
                        cuanto = horasPermiso(l);
                    } else {
                        if (l.getEmpleado().getOperativo()) {
                            cuanto = (long) diasPermiso(l) * 24 * 60;
                        } else {
                            cuanto = (long) diasPermiso(l) * 8 * 60;
                        }
                    }
                    if (cuanto > 0) {
//                    Vacaciones
                        // cuanto esta en miutos
                        // ya esta en dias

                        double factor = factor(l.getEmpleado()).doubleValue();
                        l.setCantidad((int) cuanto);
                        cuanto = (long) ((int) cuanto * factor);
                        l.setCuanto((int) cuanto);
                        q = em.createQuery("Select OBJECT(o) FROM Vacaciones as o "
                                + "where o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
                        q.setParameter("fin", desde.getTime());
                        q.setParameter("anio", mes);
                        q.setParameter("mes", anio);
                        q.setParameter("empleado", l.getEmpleado());
                        List<Vacaciones> listaVac = q.getResultList();
                        Vacaciones vac = null;
                        for (Vacaciones v1 : listaVac) {
                            vac = v1;
                        }
                        if (vac == null) {
                            vac = new Vacaciones();
                            vac.setEmpleado(l.getEmpleado());
                            vac.setAnio(anio);
                            vac.setMes(mes);
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

                        if (!l.getTipo().getHoras()) {
                            int tiempoAccion = l.getTipo().getTiempoaccion() == null ? 0 : l.getTipo().getTiempoaccion();
                            if (cuanto >= tiempoAccion) {
                                // crear la acción de personal
                                if (l.getTipo().getAccion() != null) {
                                    Historialcargos hc = new Historialcargos();
                                    hc.setActivo(Boolean.TRUE);
                                    hc.setAprobacion(Boolean.TRUE);
                                    hc.setCargo(l.getEmpleado().getCargoactual());
                                    hc.setDesde(l.getInicio());
                                    hc.setHasta(l.getFin());
                                    hc.setMotivo(l.getTipo().getNombre());
                                    hc.setFecha(l.getInicio());
                                    hc.setSueldobase(l.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                                    hc.setEmpleado(l.getEmpleado());
                                    hc.setPartidaindividual(l.getEmpleado().getPartidaindividual());
                                    hc.setTipoacciones(l.getTipo().getAccion());
                                    hc.setTipocontrato(l.getEmpleado().getTipocontrato());
                                    hc.setUsuario("PROCESO AUTOMATICO");
                                    hc.setVigente(Boolean.TRUE);
                                    em.persist(hc);
                                    l.setAccion(hc);

                                } //fin if de tiempo de accion
                            } // fin if cuanto > tiempo accion
                        }// fin if es horas diferente
                    } else { // solo legaliza no carga a vacaiones

                    } // fin if
                } else { // es vacaciones
                    cuanto = (long) diasPermiso(l) * 8 * 60;
                    if (cuanto > 0) {
//                    Vacaciones
                        // cuanto esta en miutos
                        // ya esta en dias

                        double factor = factor(l.getEmpleado()).doubleValue();
                        l.setCantidad((int) cuanto);
                        cuanto = (long) ((int) cuanto * factor);
                        l.setCuanto((int) cuanto);
                        q = em.createQuery("Select OBJECT(o) FROM Vacaciones as o "
                                + "where o.empleado=:empleado and o.anio=:anio and o.mes=:mes");
//                        q.setParameter("fin", desde.getTime());
                        q.setParameter("anio", mes);
                        q.setParameter("mes", anio);
                        q.setParameter("empleado", l.getEmpleado());
                        List<Vacaciones> listaVac = q.getResultList();
                        Vacaciones vac = null;
                        for (Vacaciones v1 : listaVac) {
                            vac = v1;
                        }
                        if (vac == null) {
                            vac = new Vacaciones();
                            vac.setEmpleado(l.getEmpleado());
                            vac.setAnio(anio);
                            vac.setMes(mes);
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

                        int tiempoAccion = l.getTipo().getTiempoaccion() == null ? 0 : l.getTipo().getTiempoaccion();
                        if (cuanto >= tiempoAccion) {
                            // crear la acción de personal
                            if (l.getTipo().getAccion() != null) {
                                Historialcargos hc = new Historialcargos();
                                hc.setActivo(Boolean.TRUE);
                                hc.setAprobacion(Boolean.TRUE);
                                hc.setCargo(l.getEmpleado().getCargoactual());
                                hc.setDesde(l.getInicio());
                                hc.setHasta(l.getFin());
                                hc.setMotivo(l.getTipo().getNombre());
                                hc.setFecha(l.getInicio());
                                hc.setSueldobase(l.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase());
                                hc.setEmpleado(l.getEmpleado());
                                hc.setPartidaindividual(l.getEmpleado().getPartidaindividual());
                                hc.setTipoacciones(l.getTipo().getAccion());
                                hc.setTipocontrato(l.getEmpleado().getTipocontrato());
                                hc.setUsuario("PROCESO AUTOMATICO");
                                hc.setVigente(Boolean.TRUE);
                                em.persist(hc);
                                l.setAccion(hc);

                            } //fin if de tiempo de accion
                        } // fin if cuanto > tiempo accion
                    }
                }// fin if

            }// fin for

        }

    }

    public BigDecimal factor(Empleados e) {
        if (e.getCargoactual().getOperativo()) {
            // contar los permisos con cargo a vacaciones
            // esta puesto que sea solo los que no non por horas
            Query q = em.createQuery("Select count(o) From Licencias as o "
                    + "WHERE o.empleado=:empleado "
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

    public double diasPermiso(Licencias licencia) {
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

    public Integer horasPermiso(Licencias licencia) {
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
}
