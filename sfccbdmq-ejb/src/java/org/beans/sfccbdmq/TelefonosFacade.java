/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Telefonos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TelefonosFacade extends AbstractFacade<Telefonos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TelefonosFacade() {
        super(Telefonos.class);
    }

    @Override
    protected String modificarObjetos(Telefonos nuevo) {
        String retorno = "";
        retorno += "<codigoarea>" + nuevo.getCodigoarea() + "</codigoarea>";
        retorno += "<extencion>" + nuevo.getExtencion() + "</extencion>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";

        return retorno;
    }

}
