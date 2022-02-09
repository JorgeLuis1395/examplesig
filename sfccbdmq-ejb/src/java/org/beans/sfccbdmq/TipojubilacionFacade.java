/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipojubilacion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipojubilacionFacade extends AbstractFacade<Tipojubilacion> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipojubilacionFacade() {
        super(Tipojubilacion.class);
    }

    @Override
    protected String modificarObjetos(Tipojubilacion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre()+ "</nombre>";
        retorno += "<consecutivas>" + nuevo.getConsecutivas()+ "</consecutivas>";
        retorno += "<discapacidad>" + nuevo.getDiscapacidad()+ "</discapacidad>";
        retorno += "<edadminima>" + nuevo.getEdadminima()+ "</edadminima>";
        retorno += "<numeroaportes>" + nuevo.getNumeroaportes()+ "</numeroaportes>";

        return retorno;
    }
    
}
