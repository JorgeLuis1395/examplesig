/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipoajuste;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipoajusteFacade extends AbstractFacade<Tipoajuste> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoajusteFacade() {
        super(Tipoajuste.class);
    }

    @Override
    protected String modificarObjetos(Tipoajuste nuevo) {
        String retorno = "";
        retorno += "<tasiento>" + nuevo.getTasiento() + "</tasiento>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<fechaaplicacion>" + nuevo.getFechaaplicacion() + "</fechaaplicacion>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";

        return retorno;
    }

}
