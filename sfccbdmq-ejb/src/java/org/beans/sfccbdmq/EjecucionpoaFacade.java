/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Ejecucionpoa;

/**
 *
 * @author luis
 */
@Stateless
public class EjecucionpoaFacade extends AbstractFacade<Ejecucionpoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EjecucionpoaFacade() {
        super(Ejecucionpoa.class);
    }

    @Override
    protected String modificarObjetos(Ejecucionpoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<partida>" + nuevo.getPartida() + "</partida>";
        retorno += "<fuente>" + nuevo.getFuente() + "</fuente>";
        retorno += "<ejecutado>" + nuevo.getEjecutado()+ "</ejecutado>";
        retorno += "<comprometido>" + nuevo.getComprometido()+ "</comprometido>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";

        return retorno;
    }

}
