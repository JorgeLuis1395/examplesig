/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Bodegasuministro;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class BodegasuministroFacade extends AbstractFacade<Bodegasuministro> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BodegasuministroFacade() {
        super(Bodegasuministro.class);
    }

    @Override
    protected String modificarObjetos(Bodegasuministro nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<bodega>" + nuevo.getBodega() + "</bodega>";
        retorno += "<suministro>" + nuevo.getSuministro() + "</suministro>";
        retorno += "<saldo>" + nuevo.getSaldo() + "</saldo>";
        retorno += "<costopromedio>" + nuevo.getCostopromedio() + "</costopromedio>";
        retorno += "<unidad>" + nuevo.getUnidad() + "</unidad>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<hora>" + nuevo.getHora() + "</hora>";

        return retorno;
    }

}
