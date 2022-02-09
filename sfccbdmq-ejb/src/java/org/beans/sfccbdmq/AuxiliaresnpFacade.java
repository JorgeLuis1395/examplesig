/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Auxiliaresnp;

/**
 *
 * @author edwin
 */
@Stateless
public class AuxiliaresnpFacade extends AbstractFacade<Auxiliaresnp> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AuxiliaresnpFacade() {
        super(Auxiliaresnp.class);
    }

    @Override
    protected String modificarObjetos(Auxiliaresnp nuevo) {
        String retorno = "";
        retorno += "<auxiliar>" + nuevo.getAuxiliar()+ "</auxiliar>";
        retorno += "<pagonp>" + nuevo.getPagonp()+ "</pagonp>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        return retorno;

    }

}
