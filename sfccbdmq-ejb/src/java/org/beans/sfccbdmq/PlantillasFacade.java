/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Plantillas;

/**
 *
 * @author luis
 */
@Stateless
public class PlantillasFacade extends AbstractFacade<Plantillas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlantillasFacade() {
        super(Plantillas.class);
    }

    @Override
    protected String modificarObjetos(Plantillas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";

        return retorno;
    }

}
