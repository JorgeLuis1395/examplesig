/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Kardexinventario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class KardexinventarioFacade extends AbstractFacade<Kardexinventario> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KardexinventarioFacade() {
        super(Kardexinventario.class);
    }

    @Override
    protected String modificarObjetos(Kardexinventario nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cabecerainventario>" + nuevo.getCabecerainventario() + "</cabecerainventario>";
        retorno += "<bodega>" + nuevo.getBodega() + "</bodega>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<hora>" + nuevo.getHora() + "</hora>";
        retorno += "<suministro>" + nuevo.getSuministro() + "</suministro>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<costo>" + nuevo.getCosto() + "</costo>";
        retorno += "<costopromedio>" + nuevo.getCostopromedio() + "</costopromedio>";
        retorno += "<unidad>" + nuevo.getUnidad() + "</unidad>";
        retorno += "<saldoanterior>" + nuevo.getSaldoanterior() + "</saldoanterior>";

        return retorno;
    }

}
