/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Escalassalariales;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EscalassalarialesFacade extends AbstractFacade<Escalassalariales> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EscalassalarialesFacade() {
        super(Escalassalariales.class);
    }

    @Override
    protected String modificarObjetos(Escalassalariales nuevo) {
        String retorno = "";
        retorno += "<grado>" + nuevo.getGrado() + "</grado>";
        retorno += "<sueldobase>" + nuevo.getSueldobase() + "</sueldobase>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";

        return retorno;
    }

}
