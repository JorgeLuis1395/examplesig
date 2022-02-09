/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Detallefacturas;

/**
 *
 * @author luis
 */
@Stateless
public class DetallefacturasFacade extends AbstractFacade<Detallefacturas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallefacturasFacade() {
        super(Detallefacturas.class);
    }

    @Override
    protected String modificarObjetos(Detallefacturas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<producto>" + nuevo.getProducto()+ "</producto>";
        retorno += "<factura>" + nuevo.getFactura()+ "</factura>";
        retorno += "<cantidad>" + nuevo.getCantidad()+ "</cantidad>";
        retorno += "<descuento>" + nuevo.getDescuento()+ "</descuento>";
        retorno += "<valorimpuesto>" + nuevo.getValorimpuesto()+ "</valorimpuesto>";
        return retorno;
    }

}
