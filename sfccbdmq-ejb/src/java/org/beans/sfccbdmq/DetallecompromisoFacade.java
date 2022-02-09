/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Proyectos;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Compromisos;

/**
 *
 * @author edwin
 */
@Stateless
public class DetallecompromisoFacade extends AbstractFacade<Detallecompromiso> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallecompromisoFacade() {
        super(Detallecompromiso.class);
    }

    @Override
    protected String modificarObjetos(Detallecompromiso nuevo) {
        String retorno = "";
        retorno += "<compromiso>" + nuevo.getCompromiso() + "</compromiso>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<saldo>" + nuevo.getSaldo() + "</saldo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";

        return retorno;
    }
    public Detallecompromiso traer(Proyectos proyecto,String clasificador,Codigos fuente){
        Query q=em.createQuery("Select OBJECT(o) FROM Detallecompromiso as o "
                + "WHERE o.detallecertificacion.asignacion.proyecto=:proyecto "
                + "and o.detallecertificacion.asignacion.fuente=:fuente "
                + "and o.detallecertificacion.asignacion.clasificador.codigo=:clasificador "
                + "");
        q.setParameter("proyecto", proyecto);
        q.setParameter("fuente", fuente);
        q.setParameter("clasificador", clasificador);
        List<Detallecompromiso> lista=q.getResultList();
        for (Detallecompromiso d:lista){
            return d;
        }
        return null;
    }
    public Detallecompromiso traer(Proyectos proyecto,String clasificador,Codigos fuente,Compromisos compromiso){
        Query q=em.createQuery("Select OBJECT(o) FROM Detallecompromiso as o "
                + "WHERE o.detallecertificacion.asignacion.proyecto=:proyecto "
                + " and o.detallecertificacion.asignacion.fuente=:fuente "
                + " and o.compromiso=:compromiso "
                + " and o.detallecertificacion.asignacion.clasificador.codigo=:clasificador "
                + "");
        q.setParameter("proyecto", proyecto);
        q.setParameter("fuente", fuente);
        q.setParameter("compromiso", compromiso);
        q.setParameter("clasificador", clasificador);
        List<Detallecompromiso> lista=q.getResultList();
        for (Detallecompromiso d:lista){
            return d;
        }
        return null;
    }
    
    
     public void calculaFechaEjecutado() {
        Query q = em.createNativeQuery("SELECT  calculafechaejecutado()");
        Object retorno = q.getSingleResult();
    }
}
