/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.VistaAuxiliares;

/**
 *
 * @author edwin
 */
@Stateless
public class VistaAuxiliaresFacade extends AbstractFacade<VistaAuxiliares> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VistaAuxiliaresFacade() {
        super(VistaAuxiliares.class);
    }

    @Override
    protected String modificarObjetos(VistaAuxiliares nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo()+ "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre()+ "</nombre>";
        retorno += "<tipo>" + nuevo.getTipo()+ "</tipo>";
        return retorno;
    }
    
}
