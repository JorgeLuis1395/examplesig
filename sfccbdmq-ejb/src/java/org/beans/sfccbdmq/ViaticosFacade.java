/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;

/**
 *
 * @author edison
 */
@Stateless
public class ViaticosFacade extends AbstractFacade<Viaticos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ViaticosFacade() {
        super(Viaticos.class);
    }
    
    @Override
    protected String modificarObjetos(Viaticos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<lugar>" + nuevo.getLugar() + "</lugar>";
        retorno += "<certificacion>" + nuevo.getCertificacion() + "</certificacion>";
        retorno += "<partida>" + nuevo.getPartida() + "</partida>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<vigente>" + nuevo.getVigente() + "</vigente>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        
        return retorno;
    }
    
}
