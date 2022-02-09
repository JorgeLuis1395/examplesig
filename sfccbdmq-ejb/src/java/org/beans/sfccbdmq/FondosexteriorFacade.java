/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Fondosexterior;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class FondosexteriorFacade extends AbstractFacade<Fondosexterior> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FondosexteriorFacade() {
        super(Fondosexterior.class);
    }

    @Override
    protected String modificarObjetos(Fondosexterior nuevo) {
       String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<empleado>" + nuevo.getEmpleado()+ "</empleado>";
        retorno += "<departamento>" + nuevo.getDepartamento()+ "</departamento>";
        retorno += "<observaciones>" + nuevo.getObservaciones()+ "</observaciones>";
        retorno += "<jefe>" + nuevo.getJefe()+ "</jefe>";
        retorno += "<certificacion>" + nuevo.getCertificacion()+ "</certificacion>";
        retorno += "<cerrado>" + nuevo.getCerrado()+ "</cerrado>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        retorno += "<kardexbanco>" + nuevo.getKardexbanco()+ "</kardexbanco>";
        retorno += "<referencia>" + nuevo.getReferencia()+ "</referencia>";

        return retorno;
    }

    
    
}
