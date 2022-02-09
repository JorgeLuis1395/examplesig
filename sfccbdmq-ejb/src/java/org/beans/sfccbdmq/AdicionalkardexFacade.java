/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Adicionalkardex;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AdicionalkardexFacade extends AbstractFacade<Adicionalkardex> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdicionalkardexFacade() {
        super(Adicionalkardex.class);
    }

    @Override
    protected String modificarObjetos(Adicionalkardex nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<kardex>" + nuevo.getKardex() + "</kardex>";
        retorno += "<adicional>" + nuevo.getAdicional() + "</adicional>";
        retorno += "<valornumerico>" + nuevo.getValornumerico() + "</valornumerico>";
        retorno += "<valortexto>" + nuevo.getValortexto() + "</valortexto>";
        retorno += "<valorfecha>" + nuevo.getValorfecha() + "</valorfecha>";

        return retorno;
    }

}
