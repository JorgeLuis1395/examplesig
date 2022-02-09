/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Polizas;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class AlertaPolizas {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    @Schedule(minute = "30", dayOfMonth = "*", month = "*", year = "*", hour = "7")

    public void generaAlerta() throws MessagingException, UnsupportedEncodingException {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 10);
        Query q = em.createQuery("Select Object(o) From Polizas as o Where o.hasta=:fecha");
        q.setParameter("fecha", c.getTime());
        List<Polizas> lista = q.getResultList();
        String garantias = "";
        for (Polizas g : lista) {
            garantias += "<p><Strong>Poliza :</Strong>" + g.getDescripcion() + ""
                    + "</p><p><Strong>Aseguradora : </Strong>" + g.getAseguradora().toString()
                    + "</p>";
        }
        if (!garantias.isEmpty()){
            String sql = "Select Object(o) from Codigos as o "
                    + " where  o.maestro.codigo='MAIL' and o.codigo='VENPOL'";
            q = em.createQuery(sql);
            List<Codigos> listaCodigos=q.getResultList();
            String correos="";
            String texto="";
            for (Codigos cx:listaCodigos){
                correos+=cx.getDescripcion();
                texto+=cx.getParametros()==null?"":cx.getParametros();
            }
                  ejbCorreos.enviarCorreo(correos, "alerta cumpleaños", garantias);
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
