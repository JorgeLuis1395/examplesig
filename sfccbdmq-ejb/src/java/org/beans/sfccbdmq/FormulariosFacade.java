/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Formularios;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class FormulariosFacade extends AbstractFacade<Formularios> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FormulariosFacade() {
        super(Formularios.class);
    }

    @Override
    protected String modificarObjetos(Formularios nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nivelgestion>" + nuevo.getNivelgestion() + "</nivelgestion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";

        return retorno;
    }

}
