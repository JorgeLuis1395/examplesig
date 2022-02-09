/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Entidades;
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
public class AlertasRecursos {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB private SfccbdmqCorreosFacade ejbCorreos;
//    @Schedule(minute = "55", second = "0", dayOfMonth = "1", month = "*", year = "*", hour = "9")
    @Schedule(minute = "7", second = "45", dayOfMonth = "1", month = "*", year = "*", hour = "0")
    private void alertaRecursos() throws MessagingException, UnsupportedEncodingException {
        Calendar c = Calendar.getInstance();
        int mes = c.get(Calendar.MONTH) + 1;
        int anio = c.get(Calendar.YEAR);
        int ultimo_dia = 0;
        if (mes == 12) {
            ultimo_dia = 31;
        } else {
            c.set(anio, mes, 1);
            c.add(Calendar.DATE, -1);
            ultimo_dia = c.get(Calendar.DATE);

        }
//        c.set(anio, mes - 1, 1);
        int j = 1;
        boolean guarda = false;
        String contedino = "";
        for (int i = 0; i < ultimo_dia; i++) {

            int dia = c.get(Calendar.DAY_OF_WEEK);
            Integer diaMostrar = c.get(Calendar.DATE);
            // buscar garatias de la fecha
            String garantias = "";

            String sql = "Select id from entidades where  date_part('MONTH' , fecha)=? and date_part('DAY' , fecha)=?";
            Query q = em.createNativeQuery(sql);
            q.setParameter(1, mes);
            q.setParameter(2, dia);
            List<Object> resultado = q.getResultList();
            for (Object o : resultado) {
                Entidades e = em.find(Entidades.class, o);
                contedino+="<p><strong>"+diaMostrar+"</strong>"+e.toString()+"</p>";
            }
            j++;
        }
        if (!contedino.isEmpty()){
            String sql = "Select Object(o) from Codigos as o "
                    + " where  o.maestro.codigo='MAIL' and o.codigo='RRHH'";
            Query q = em.createQuery(sql);
            List<Codigos> listaCodigos=q.getResultList();
            String correos="";
            String texto="";
            for (Codigos cx:listaCodigos){
                correos+=cx.getDescripcion();
                texto+=cx.getParametros()==null?"":cx.getParametros();
            }
                  ejbCorreos.enviarCorreo(correos, "alerta cumpleaños", contedino);
        }
        

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
