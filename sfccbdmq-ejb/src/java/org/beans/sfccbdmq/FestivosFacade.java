/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Festivos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class FestivosFacade extends AbstractFacade<Festivos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FestivosFacade() {
        super(Festivos.class);
    }

    @Override
    protected String modificarObjetos(Festivos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";

        return retorno;
    }

}
