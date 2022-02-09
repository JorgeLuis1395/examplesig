/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Flujodecaja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class FlujodecajaFacade extends AbstractFacade<Flujodecaja> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FlujodecajaFacade() {
        super(Flujodecaja.class);
    }

    @Override
    protected String modificarObjetos(Flujodecaja nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<abril>" + nuevo.getAbril()+ "</abril>";
        retorno += "<abrilr>" + nuevo.getAbrilr()+ "</abrilr>";
        retorno += "<agosto>" + nuevo.getAgosto()+ "</agosto>";
        retorno += "<agostor>" + nuevo.getAgostor()+ "</agostor>";
        retorno += "<diciembre>" + nuevo.getDiciembre()+ "</diciembre>";
        retorno += "<diciembrer>" + nuevo.getDiciembrer()+ "</diciembrer>";
        retorno += "<enero>" + nuevo.getEnero()+ "</enero>";
        retorno += "<eneror>" + nuevo.getEneror()+ "</eneror>";
        retorno += "<febrero>" + nuevo.getFebrero()+ "</febrero>";
        retorno += "<febreror>" + nuevo.getFebreror()+ "</febreror>";
        retorno += "<fuente>" + nuevo.getFuente()+ "</fuente>";
        retorno += "<julio>" + nuevo.getJulio()+ "</julio>";
        retorno += "<julior>" + nuevo.getJulior()+ "</julior>";
        retorno += "<junio>" + nuevo.getJunio()+ "</junio>";
        retorno += "<junior>" + nuevo.getJunior()+ "</junior>";
        retorno += "<marzo>" + nuevo.getMarzo()+ "</marzo>";
        retorno += "<marzor>" + nuevo.getMarzor()+ "</marzor>";
        retorno += "<mayo>" + nuevo.getMayo()+ "</mayo>";
        retorno += "<mayor>" + nuevo.getMayor()+ "</mayor>";
        retorno += "<noviembre>" + nuevo.getNoviembre()+ "</noviembre>";
        retorno += "<noviembrer>" + nuevo.getNoviembrer()+ "</noviembrer>";
        retorno += "<octubre>" + nuevo.getOctubre()+ "</octubre>";
        retorno += "<octubrer>" + nuevo.getOctubrer()+ "</octubrer>";
        retorno += "<partida>" + nuevo.getPartida()+ "</partida>";
        retorno += "<proyecto>" + nuevo.getProyecto()+ "</proyecto>";
        retorno += "<septiembre>" + nuevo.getSeptiembre()+ "</septiembre>";
        retorno += "<septiembrer>" + nuevo.getSeptiembrer()+ "</septiembrer>";

        return retorno;
    }
    
}
