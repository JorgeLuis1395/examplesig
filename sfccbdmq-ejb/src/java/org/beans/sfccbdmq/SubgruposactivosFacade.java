/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Subgruposactivos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class SubgruposactivosFacade extends AbstractFacade<Subgruposactivos> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubgruposactivosFacade() {
        super(Subgruposactivos.class);
    }

    @Override
    protected String modificarObjetos(Subgruposactivos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId()+ "</id>";
        retorno += "<codigo>" + nuevo.getCodigo()+ "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre()+ "</nombre>";
        retorno += "<grupo>" + nuevo.getGrupo()+ "</grupo>";
        return retorno;
    }
    
}
