/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Productos;

/**
 *
 * @author luis
 */
@Stateless
public class ProductosFacade extends AbstractFacade<Productos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductosFacade() {
        super(Productos.class);
    }

    @Override
    protected String modificarObjetos(Productos nuevo) {
String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<categoria>" + nuevo.getCategoria()+ "</categoria>";
        retorno += "<codigo>" + nuevo.getCodigo()+ "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre()+ "</nombre>";
        retorno += "<impuesto>" + nuevo.getImpuesto()+ "</impuesto>";
        retorno += "<preciounitario>" + nuevo.getPreciounitario()+ "</preciounitario>";
        return retorno;    
    }
    
}
