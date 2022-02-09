/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Marcacionesbiometrico;

/**
 *
 * @author edwin
 */
@Stateless
public class MarcacionesbiometricoFacade extends AbstractFacade<Marcacionesbiometrico> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MarcacionesbiometricoFacade() {
        super(Marcacionesbiometrico.class);
    }

    @Override
    protected String modificarObjetos(Marcacionesbiometrico nuevo) {
        String retorno = "";
        return retorno;
    }
    
}