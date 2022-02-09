/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.procesos.sfccbdmq;

import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.beans.sfccbdmq.FirmadorFacade;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Obligaciones;
import org.utilitarios.sfccbdmq.ComprobanteRetencion;

/**
 *
 * @author edwin
 */
@Singleton
@LocalBean
public class ActualizaFechaRetencionFacade {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private FirmadorFacade ejbFirmador;
    
    
    @Schedule(month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*/5", second = "0", persistent = false)
    
    public void actualizaFecha() {
        Query q=em.createQuery("SELECT OBJECT(o) FROM Obligaciones as o where o.claver is not null and o.fechaAutorizaRetencion is null");
        q.setFirstResult(0);
        q.setMaxResults(50);
        List<Obligaciones> listaObligaciones=q.getResultList();
        for (Obligaciones o:listaObligaciones){
            RespuestaComprobante.Autorizaciones autorizacion = consultarAutorizacion(o);
            Date fechaAutorizacion;
            if (autorizacion != null) {
                if (autorizacion.getAutorizacion() != null) {
                    if (!autorizacion.getAutorizacion().isEmpty()) {
                        if (autorizacion.getAutorizacion().get(0).getFechaAutorizacion() != null) {
                            try {
                                fechaAutorizacion = autorizacion.getAutorizacion().get(0).getFechaAutorizacion().toGregorianCalendar().getTime();
                                o.setFechaAutorizaRetencion(fechaAutorizacion);
                                em.merge(o);
                            } catch (Exception x) {

                            }
                        }
                    }
                }
            }
        }
    }
    public RespuestaComprobante.Autorizaciones consultarAutorizacion(Obligaciones o) {
        Configuracion config = dondeConfig();
        String donde = config.getUrlsri();
//        ComprobanteRetencion c = generaRetElectronica(o);
        RespuestaComprobante rcomprobante = ejbFirmador.consultar(donde + "AutorizacionComprobantesOffline?wsdl", o.getClaver());
        RespuestaComprobante.Autorizaciones a = rcomprobante.getAutorizaciones();

        return a;
    }
    private Configuracion dondeConfig() {
        Query q = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o ");
        List<Configuracion> list = q.getResultList();
        for (Configuracion c : list) {
            return c;
        }
        return null;
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
