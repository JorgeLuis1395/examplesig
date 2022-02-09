/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Idiomas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class IdiomasFacade extends AbstractFacade<Idiomas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IdiomasFacade() {
        super(Idiomas.class);
    }

    @Override
    protected String modificarObjetos(Idiomas nuevo) {
        String retorno = "";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<idioma>" + nuevo.getIdioma() + "</idioma>";
        retorno += "<hablado>" + nuevo.getHablado() + "</hablado>";
        retorno += "<escrito>" + nuevo.getEscrito() + "</escrito>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";

        return retorno;
    }

}
