/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipomovbancos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipomovbancosFacade extends AbstractFacade<Tipomovbancos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipomovbancosFacade() {
        super(Tipomovbancos.class);
    }

    @Override
    protected String modificarObjetos(Tipomovbancos nuevo) {
        String retorno = "";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<tipoasiento>" + nuevo.getTipoasiento() + "</tipoasiento>";
        retorno += "<spi>" + nuevo.getSpi() + "</spi>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";

        return retorno;
    }
   
}
