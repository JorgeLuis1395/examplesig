/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Erogaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ErogacionesFacade extends AbstractFacade<Erogaciones> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ErogacionesFacade() {
        super(Erogaciones.class);
    }

    @Override
    protected String modificarObjetos(Erogaciones nuevo) {
        String retorno = "";
        retorno += "<descripcion>" + nuevo.getDescripcion()+ "</descripcion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<activo>" + nuevo.getActivo()+ "</activo>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        retorno += "<valoractivo>" + nuevo.getValoractivo()+ "</valorvaloractivo>";
        retorno += "<vidautil>" + nuevo.getVidautil()+ "</vidautil>";
        retorno += "<vidautilactivo>" + nuevo.getVidautilactivo()+ "</vidautilactivo>";

        return retorno;
    }
    
}
