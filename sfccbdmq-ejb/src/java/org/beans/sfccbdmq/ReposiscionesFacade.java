/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Reposisciones;

/**
 *
 * @author luis
 */
@Stateless
public class ReposiscionesFacade extends AbstractFacade<Reposisciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReposiscionesFacade() {
        super(Reposisciones.class);
    }

    @Override
    protected String modificarObjetos(Reposisciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<base0>" + nuevo.getBase0() + "</base0>";
        retorno += "<base>" + nuevo.getBase() + "</base>";
        retorno += "<iva>" + nuevo.getIva() + "</iva>";
        retorno += "<puuntoemision>" + nuevo.getPuuntoemision() + "</puuntoemision>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento() + "</establecimiento>";
        retorno += "<numerodocumento>" + nuevo.getNumerodocumento() + "</numerodocumento>";

        return retorno;
    }

}
