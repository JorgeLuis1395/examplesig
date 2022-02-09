/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Externos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ExternosFacade extends AbstractFacade<Externos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExternosFacade() {
        super(Externos.class);
    }

    @Override
    protected String modificarObjetos(Externos nuevo) {
        String retorno = "";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<telefonos>" + nuevo.getTelefonos() + "</telefonos>";
        retorno += "<direccion>" + nuevo.getDireccion() + "</direccion>";

        return retorno;
    }

}
