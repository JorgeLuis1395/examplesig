/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Bitacorasuministro;

/**
 *
 * @author edwin
 */
@Stateless
public class BitacorasuministroFacade extends AbstractFacade<Bitacorasuministro> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BitacorasuministroFacade() {
        super(Bitacorasuministro.class);
    }

    @Override
    protected String modificarObjetos(Bitacorasuministro nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cabecera>" + nuevo.getCabecera()+ "</v>";
        retorno += "<estado>" + nuevo.getEstado()+ "</estado>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        return retorno;
    }
    
}