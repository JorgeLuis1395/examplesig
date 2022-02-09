/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Fondos;

/**
 *
 * @author edwin
 */
@Stateless
public class FondosFacade extends AbstractFacade<Fondos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FondosFacade() {
        super(Fondos.class);
    }

    @Override
    protected String modificarObjetos(Fondos nuevo) {
       String retorno = "";
        retorno += "<observaciones>" + nuevo.getObservaciones()+ "</observaciones>";
        retorno += "<certificacion>" + nuevo.getCertificacion()+ "</certificacion>";
        retorno += "<departamento>" + nuevo.getDepartamento()+ "</departamento>";
        retorno += "<empleado>" + nuevo.getEmpleado()+ "</empleado>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<id>" + nuevo.getId()+ "</id>";
        retorno += "<jefe>" + nuevo.getJefe()+ "</jefe>";
        retorno += "<cerrado>" + nuevo.getCerrado()+ "</cerrado>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        retorno += "<prcvale>" + nuevo.getPrcvale()+ "</prcvale>";
        retorno += "<autorizacion>" + nuevo.getAutorizacion()+ "</autorizacion>";
        retorno += "<tipodocumento>" + nuevo.getTipodocumento()+ "</tipodocumento>";
        retorno += "<referencia>" + nuevo.getReferencia()+ "</referencia>";
        retorno += "<kardexbanco>" + nuevo.getKardexbanco()+ "</kardexbanco>";
        retorno += "<nrodocumento>" + nuevo.getNrodocumento()+ "</nrodocumento>";
        return retorno;
    }
    
}
