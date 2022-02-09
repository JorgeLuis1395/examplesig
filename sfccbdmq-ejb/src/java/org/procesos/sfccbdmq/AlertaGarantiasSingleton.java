/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Garantias;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class AlertaGarantiasSingleton {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;

    @Schedule(dayOfWeek = "*", month = "*", hour = "13", dayOfMonth = "*", year = "*", minute = "00", second = "0")
    public void alertaGarantias() {
        Date hoy = new Date();
        Calendar fecha = Calendar.getInstance();

//        String texto = "";
        // traer el texto del cumpleaños
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            Codigos mailGarantias = ejbCodigos.traerCodigo("MAILGARANTIAS", "MAILGARANTIAS");
            Codigos mailGarantiasAdm = ejbCodigos.traerCodigo("MAILGARANTIAS", "MAILGARANTIASADM");
            String texto = mailGarantias.getParametros();
            String textoAdm = mailGarantiasAdm.getParametros();
            int dias = Integer.parseInt(mailGarantias.getNombre());
            fecha.add(Calendar.DATE, dias);
            Query q = em.createQuery("SELECT Object(o) "
                    + " FROM  Garantias as o "
                    + " WHERE o.vencimiento =:fecha"
                    + " ");
//                    + " and o.fincanciera=true");
            q.setParameter("fecha", fecha.getTime());
            List<Garantias> lista = q.getResultList();
            for (Garantias g : lista) {
                texto = texto.replace("#vencimiento", sdf.format(g.getVencimiento()));
                textoAdm = textoAdm.replace("#vencimiento", sdf.format(g.getVencimiento()));
                texto = texto.replace("#contrato", g.getContrato().getObjeto());
                textoAdm = textoAdm.replace("#contrato", g.getContrato().getObjeto());
                texto = texto.replace("#valorcontrato", df.format(g.getContrato().getValor()));
                textoAdm = textoAdm.replace("#valorcontrato", df.format(g.getContrato().getValor()));
                texto = texto.replace("#numerocontrato", g.getContrato().getNumero());
                textoAdm = textoAdm.replace("#numerocontrato", g.getContrato().getNumero());
                texto = texto.replace("#proveedor", g.getContrato().getProveedor().getEmpresa().toString());
                textoAdm = textoAdm.replace("#proveedor", g.getContrato().getProveedor().getEmpresa().toString());
                texto = texto.replace("#administrador", g.getContrato().getAdministrador().toString());
                textoAdm = textoAdm.replace("#administrador", g.getContrato().getAdministrador().toString());
                texto = texto.replace("#rucproveedor", g.getContrato().getProveedor().getEmpresa().getRuc());
                textoAdm = textoAdm.replace("#rucproveedor", g.getContrato().getProveedor().getEmpresa().getRuc());
                texto = texto.replace("#tipo", g.getTipo().toString());
                textoAdm = textoAdm.replace("#tipo", g.getTipo().toString());
                texto = textoAdm.replace("#numero", g.getNumero());
                textoAdm = texto.replace("#numero", g.getNumero());
                texto = texto.replace("#aseguradora", g.getAseguradora().getNombre());
                textoAdm = textoAdm.replace("#aseguradora", g.getAseguradora().getNombre());
                texto = texto.replace("#descripcion", g.getObjeto());
                textoAdm = textoAdm.replace("#descripcion", g.getObjeto());
                texto = texto.replace("#valor", df.format(g.getMonto()));
                textoAdm = textoAdm.replace("#valor", df.format(g.getMonto()));
                texto = texto.replace("#hoy", sdf.format(new Date()));
                textoAdm = textoAdm.replace("#hoy", sdf.format(new Date()));
                if (mailGarantias.getDescripcion().contains(";")) {
                    String[] correo = mailGarantias.getDescripcion().split(";");
                    for (String dirMail : correo) {
                        ejbCorreos.enviarCorreo(dirMail, "RENOVACIÓN GARANTÍAS", texto);
                    }
                } else {
                    ejbCorreos.enviarCorreo(mailGarantias.getDescripcion(), "RENOVACIÓN GARANTÍAS", texto);
                }
                ejbCorreos.enviarCorreo(g.getContrato().getProveedor().getEmpresa().getEmail(), "RENOVACIÓN GARANTÍAS", texto);
                ejbCorreos.enviarCorreo(g.getContrato().getAdministrador().getEmail(), "RENOVACIÓN GARANTÍAS", textoAdm);
//                ejbCorreos.enviarCorreo("emoina@bomberosquito.gob,ec", "RENOVACIÓN GARANTÍAS", textoAdm);
//                    ejbCorreos.enviarCorreo(g.getContrato().getAdministrador().getEmail(), "RENOVACIÓN GARANTÍAS", texto);
//                    ejbCorreos.enviarCorreo(g.getAseguradora().getParametros(), "RENOVACIÓN GARANTÍAS", texto);

            }
        } catch (ConsultarException | MessagingException | UnsupportedEncodingException ex) {
            Logger.getLogger(AlertaGarantiasSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}