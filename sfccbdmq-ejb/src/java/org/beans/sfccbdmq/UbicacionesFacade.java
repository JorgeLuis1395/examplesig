/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Ubicaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class UbicacionesFacade extends AbstractFacade<Ubicaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UbicacionesFacade() {
        super(Ubicaciones.class);
    }

    @Override
    protected String modificarObjetos(Ubicaciones nuevo) {
        String retorno = "";
        retorno += "<provincia>" + nuevo.getProvincia() + "</provincia>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
