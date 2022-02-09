/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipopermisos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipopermisosFacade extends AbstractFacade<Tipopermisos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipopermisosFacade() {
        super(Tipopermisos.class);
    }

    @Override
    protected String modificarObjetos(Tipopermisos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<rmu>" + nuevo.getRmu() + "</rmu>";
        retorno += "<recursivo>" + nuevo.getRecursivo()+ "</recursivo>";

        return retorno;
    }

}
