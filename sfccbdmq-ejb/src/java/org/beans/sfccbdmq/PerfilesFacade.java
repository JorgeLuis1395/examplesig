/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Perfiles;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PerfilesFacade extends AbstractFacade<Perfiles> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerfilesFacade() {
        super(Perfiles.class);
    }

    @Override
    protected String modificarObjetos(Perfiles nuevo) {
        String retorno = "";
        retorno += "<consulta>" + nuevo.getConsulta() + "</consulta>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<grupo>" + nuevo.getGrupo() + "</grupo>";
        retorno += "<menu>" + nuevo.getMenu() + "</menu>";
        retorno += "<modificacion>" + nuevo.getModificacion() + "</modificacion>";
        retorno += "<borrado>" + nuevo.getBorrado() + "</borrado>";
        retorno += "<nuevo>" + nuevo.getNuevo() + "</nuevo>";

        return retorno;
    }

}
