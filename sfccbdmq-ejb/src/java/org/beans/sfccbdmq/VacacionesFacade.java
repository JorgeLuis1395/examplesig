/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.entidades.sfccbdmq.Vacaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Licencias;

/**
 *
 * @author edwin
 */
@Stateless
public class VacacionesFacade extends AbstractFacade<Vacaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private LicenciasFacade ejbLicencias;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VacacionesFacade() {
        super(Vacaciones.class);
    }

    @Override
    protected String modificarObjetos(Vacaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<ganado>" + nuevo.getGanado() + "</ganado>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";
        retorno += "<utilizado>" + nuevo.getUtilizado() + "</utilizado>";
        return retorno;
    }

    public void actualizaSaldo(Empleados e, Date fecha) {
        Calendar fechaHoy = Calendar.getInstance();
//Necesitamos desde noviembre por empleado o desde septiembre ya veo la base
//        Calendar hoy1 = Calendar.getInstance();
//        hoy1.set(2018, 0, 16);
//        Calendar desde = Calendar.getInstance();
//        desde.set(2017, 0, 1);
//        int mesMaximo = hoy.get(Calendar.MONTH)+12;
        fechaHoy.setTime(fecha);
        int mesMaximo = fechaHoy.get(Calendar.MONTH) + 12;
        int mes = fechaHoy.get(Calendar.MONTH) +1;
        int anio = fechaHoy.get(Calendar.YEAR) + 12;
        Query qVac = em.createQuery("SELECT OBJECT(o) FROM Vacaciones as o "
                + " WHERE o.empleado=:empleado and o.mes=:mes and o.anio=:anio");
        qVac.setParameter("empleado", e);
        qVac.setParameter("mes", mes);
        qVac.setParameter("anio", anio);
        List<Vacaciones> listaV = qVac.getResultList();
        Vacaciones vac=null;
        for (Vacaciones va : listaV) {
            va.setUtilizado(0);
            va.setUtilizadofs(0);
            
            em.merge(va);
            vac=va;
        }
//        int anio = 2017;
//        int mes = -1;
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
            
            if (vac != null) {
                int usadoAnterior = vac.getUtilizado();
                int usadoAnteriorFs = vac.getUtilizadofs();
                int usado = (int) (utilizado * 22 / 30);
                Integer usadofs = (int) (utilizado - usado);
                vac.setUtilizado(usadoAnterior + usado);
                vac.setUtilizadofs(usadoAnteriorFs + usadofs);
                em.merge(vac);
            } else {
                vac = new Vacaciones();
                int usado = (int) (utilizado * 22 / 30);
                Integer usadofs = (int) (utilizado - usado);
                vac.setUtilizado(usado);
                vac.setGanado(0);
                vac.setGanadofs(0);
                vac.setProporcional(0);
                vac.setMes(mes + 1);
                vac.setAnio(anio);
                vac.setEmpleado(e);
                vac.setUtilizadofs(usadofs);
                em.merge(vac);
            }
//                        }
        }
        // fin entra adentro

    }

    public void actualizaSaldo1(Empleados e, Date fecha) {
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaFin = Calendar.getInstance();
        fechaActual.setTime(fecha);
        int mes = fechaActual.get(Calendar.MONTH) + 1;
        int anio = fechaActual.get(Calendar.YEAR);
        Query q = em.createQuery("Select OBJECT(o) FROM Vacaciones as o "
                + " where o.mes=:mes and o.anio=:anio and o.empleado=:empleado");
        q.setParameter("empleado", e);
        q.setParameter("anio", anio);
        q.setParameter("mes", mes);
        List<Vacaciones> lista = q.getResultList();
        Vacaciones vac = null;
        for (Vacaciones v : lista) {
            vac = v;
        }
        fechaActual.set(Calendar.DATE, 1);
        fechaFin.set(anio, mes, 1);
        fechaFin.add(Calendar.DATE, -1);
        // tengo el registro ahora sumo lo que necesito
        Query q1 = em.createQuery("Select SUM(o.cuanto) "
                + " from Licencias as o where o.inicio "
                + " between :desde and :hasta and o.empleado =:empleado and o.fechaanula is null and o.legalizado=true");
        q1.setParameter("empleado", e);
        q1.setParameter("desde", fechaActual.getTime());
        q1.setParameter("hasta", fechaFin.getTime());
        Long sum = (Long) q1.getSingleResult();
        Integer suma = 0;
        if (sum != null) {
            suma = sum.intValue();
        }

        if (vac == null) {
            vac = new Vacaciones();
            vac.setEmpleado(e);
            vac.setAnio(anio);
            vac.setMes(mes);
            vac.setGanado(0);
            vac.setGanadofs(0);
            int utilizado = (int) (suma * 22 / 30);
            Integer utilizadofs = (int) (suma - utilizado);
            vac.setUtilizado(utilizado);
            vac.setUtilizadofs(utilizadofs);
            em.persist(vac);
        } else {
            int utilizado = (int) (suma * 22 / 30);
            Integer utilizadofs = (int) (suma - utilizado);
            vac.setUtilizado(utilizado);
            vac.setUtilizadofs(utilizadofs);
            em.merge(vac);
        }
    }
}