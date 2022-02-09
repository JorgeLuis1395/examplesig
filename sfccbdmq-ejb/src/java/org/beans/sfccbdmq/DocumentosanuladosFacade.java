/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Documentosanulados;

/**
 *
 * @author edwin
 */
@Stateless
public class DocumentosanuladosFacade extends AbstractFacade<Documentosanulados> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentosanuladosFacade() {
        super(Documentosanulados.class);
    }

    @Override
    protected String modificarObjetos(Documentosanulados nuevo) {
       String retorno = "";
        retorno += "<autorizacion>" + nuevo.getAutorizacion()+ "</autorizacion>";
        retorno += "<observaciones>" + nuevo.getObservaciones()+ "</observaciones>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<documento>" + nuevo.getDocumento()+ "</documento>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<numero>" + nuevo.getNumero()+ "</numero>";

        return retorno;
    }
    
}
