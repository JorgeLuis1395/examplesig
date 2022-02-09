/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Adicionalessuministro;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AdicionalessuministroFacade extends AbstractFacade<Adicionalessuministro> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdicionalessuministroFacade() {
        super(Adicionalessuministro.class);
    }

    @Override
    protected String modificarObjetos(Adicionalessuministro nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<suministro>" + nuevo.getSuministro() + "</suministro>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<tipodato>" + nuevo.getTipodato() + "</tipodato>";
        retorno += "<obligatorio>" + nuevo.getObligatorio() + "</obligatorio>";
        retorno += "<individual>" + nuevo.getIndividual() + "</individual>";

        return retorno;
    }

}
