/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Gruposusuarios;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class GruposusuariosFacade extends AbstractFacade<Gruposusuarios> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GruposusuariosFacade() {
        super(Gruposusuarios.class);
    }

    @Override
    protected String modificarObjetos(Gruposusuarios nuevo) {
        String retorno = "";
        retorno += "<modulo>" + nuevo.getModulo() + "</modulo>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<grupo>" + nuevo.getGrupo() + "</grupo>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
