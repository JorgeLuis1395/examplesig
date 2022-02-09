/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Nivelesgestion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class NivelesgestionFacade extends AbstractFacade<Nivelesgestion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NivelesgestionFacade() {
        super(Nivelesgestion.class);
    }

    @Override
    protected String modificarObjetos(Nivelesgestion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";

        return retorno;
    }

}
