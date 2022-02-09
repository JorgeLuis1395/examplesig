/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Cierres;

/**
 *
 * @author edwin
 */
@Stateless
public class CierresFacade extends AbstractFacade<Cierres> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CierresFacade() {
        super(Cierres.class);
    }

    @Override
    protected String modificarObjetos(Cierres nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<enero>" + nuevo.getEnero()+ "</enero>";
        retorno += "<febrero>" + nuevo.getFebrero()+ "</febrero>";
        retorno += "<marzo>" + nuevo.getMarzo()+ "</marzo>";
        retorno += "<abril>" + nuevo.getAbril()+ "</abril>";
        retorno += "<mayo>" + nuevo.getMayo()+ "</mayo>";
        retorno += "<junio>" + nuevo.getJunio()+ "</junio>";
        retorno += "<julio>" + nuevo.getJulio()+ "</julio>";
        retorno += "<agosto>" + nuevo.getAgosto()+ "</agosto>";
        retorno += "<septiembre>" + nuevo.getSeptiembre()+ "</septiembre>";
        retorno += "<octubre>" + nuevo.getOctubre()+ "</octubre>";
        retorno += "<noviembre>" + nuevo.getNoviembre()+ "</noviembre>";
        retorno += "<diciembre>" + nuevo.getDiciembre()+ "</diciembre>";
        retorno += "<anio>" + nuevo.getAnio()+ "</anio>";

        return retorno;
    }
    
}
