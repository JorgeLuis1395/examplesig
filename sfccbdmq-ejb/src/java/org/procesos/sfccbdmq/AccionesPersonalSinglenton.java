/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.util.Calendar;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Historialcargos;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class AccionesPersonalSinglenton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Schedule(dayOfWeek = "*", month = "*", hour = "23", dayOfMonth = "*", year = "*", minute = "59", second = "0")

    public void accionesPersonal() {
        Query q = em.createQuery("SELECT OBJECT(o) FROM Historialcargos as o WHERE o.hasta=:hasta and o.activo=true");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        q.setParameter("hasta", c.getTime());
        List<Historialcargos> lista = q.getResultList();
        for (Historialcargos h : lista) {
            Empleados e = h.getEmpleado();
//            if (h.getCargoant()!=null){
            h.setActivo(Boolean.FALSE);
//                e.setCargoactual(h.getCargoant());
            em.merge(h);
//            }

            System.err.println("Fin Acción de personal: " + e.toString());
        }

    }

    private void accionesPersonalAnt() {
        Calendar hoy = Calendar.getInstance();
        hoy.add(Calendar.DATE, -1);
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        // create update
        CriteriaUpdate<Historialcargos> update = cb.
                createCriteriaUpdate(Historialcargos.class);

        // set the root class
        Root e = update.from(Historialcargos.class);

        // set update and where clause
        update.set("activo", false);
        update.where(cb.equal(e.get("hasta"), hoy.getTime()));

        // perform update
        int x = this.em.createQuery(update).executeUpdate();
        System.err.println("Fin " + x);

    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
