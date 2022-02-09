/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Trackingactivos;
import org.entidades.sfccbdmq.Vidasutiles;
import org.errores.sfccbdmq.ConsultarException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
@LocalBean
public class DepreciacionAcumuladaFacade {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

//   @Schedule(minute = "39", hour = "9")
    public void depreciar(Activos activo,String usuario) throws ConsultarException {
        try {

            Date fechaMinima = activo.getFechaalta();
            Calendar cInicial = Calendar.getInstance();
            cInicial.setTime(fechaMinima);
            int anioInicial = cInicial.get(Calendar.YEAR);
            int aio2 = anioInicial + 1;
            int mesInicial = cInicial.get(Calendar.MONTH);
            List<Activos> listaActvos = new LinkedList<>();
            listaActvos.add(activo);
            while (anioInicial < 2016) {
//            while (anioInicial < 2016) {
                // buscar activos
                Calendar c = Calendar.getInstance();
                mesInicial++;
                if (mesInicial == 12) {
                    anioInicial++;
                    mesInicial = 0;
                }
                System.out.println("Mes anio" + mesInicial + " " + anioInicial);
                c.set(anioInicial, mesInicial, 1);
                c.add(Calendar.DATE, -1);
                Date fecha = c.getTime();
                int anio = c.get(Calendar.YEAR);
                int mes = c.get(Calendar.MONTH) + 2;

                for (Activos a : listaActvos) {
                    Calendar cActivo = Calendar.getInstance();
                    cActivo.setTime(a.getFechaalta());
                    cActivo.add(Calendar.MONTH, a.getVidautil() + 1);
//                cActivo.add(Calendar.MONTH, 1);
                    if (cActivo.getTime().after(c.getTime())) {
                        Query q = em.createQuery("Select sum(o.valor) FROM Depreciaciones as o WHERE o.activo=:activo");
                        q.setParameter("activo", a);
                        Double sumaDepreciacion = (Double) q.getSingleResult();
                        if (sumaDepreciacion == null) {
                            sumaDepreciacion = new Double(0);
                        }
                        q = em.createQuery("Select Object(o) FROM "
                                + " Vidasutiles as o "
                                + " WHERE o.activo=:activo and o.mes=:mes and o.anio=:anio");
                        q.setParameter("activo", a);
                        q.setParameter("anio", anio);
                        q.setParameter("mes", mes);
                        List<Vidasutiles> lvu = q.getResultList();
                        Vidasutiles v = null;
                        for (Vidasutiles vu : lvu) {
                            v = vu;
                        }
                        float porcentaje = (1 - (a.getValorresidual() == null ? 0 : a.getValorresidual()) / 100);
                        float valorResidual = a.getValoralta().floatValue() * porcentaje;
                        if (v == null) {
                            v = new Vidasutiles();
                            v.setVidautil(a.getVidautil());
                            v.setAnio(anio);
                            v.setMes(mes);
                            v.setValorresidual(valorResidual);
                            v.setVidautil(a.getVidautil());
                            v.setActivo(a);
                            em.persist(v);
                        }
                        Depreciaciones d = new Depreciaciones();
                        d.setActivo(a);
                        d.setAnio(anio);
                        d.setMes(mes);
                        d.setDepreciacion(v.getVidautil());

                        float valorDepreciar = (v.getValorresidual()) / v.getVidautil();
                        if (a.getGrupo().getMetododepreciacion().getCodigo().equals("DEPHOR")) {
                            valorDepreciar = (v.getValorresidual() * v.getVidautil()) / v.getUnidades();
                        }
                        d.setValor(valorDepreciar);
                        if (sumaDepreciacion + valorDepreciar <= valorResidual) {
                            em.persist(d);
                            // crear el tarcking
                            String anioMes = String.valueOf(anio) + "/" + String.valueOf(mes);
                            Trackingactivos tacking = new Trackingactivos();
                            tacking.setActivo(d.getActivo());
                            tacking.setDescripcion("Generación Depreciación :" + anioMes);
                            tacking.setFecha(new Date());
                            tacking.setTipo(8);
                            tacking.setUsuario(usuario);
                            em.persist(tacking);
//                            System.out.println("Graba Tracking");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ConsultarException("ERROR", e);
        }
    }
}
