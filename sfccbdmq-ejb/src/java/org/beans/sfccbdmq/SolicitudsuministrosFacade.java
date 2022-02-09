/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Solicitudsuministros;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class SolicitudsuministrosFacade extends AbstractFacade<Solicitudsuministros> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudsuministrosFacade() {
        super(Solicitudsuministros.class);
    }

    @Override
    protected String modificarObjetos(Solicitudsuministros nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<despacho>" + nuevo.getDespacho() + "</despacho>";
        retorno += "<empleadodespacho>" + nuevo.getEmpleadodespacho() + "</empleadodespacho>";
        retorno += "<empleadosolicita>" + nuevo.getEmpleadosolicita() + "</empleadosolicita>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";

        return retorno;
    }

}
