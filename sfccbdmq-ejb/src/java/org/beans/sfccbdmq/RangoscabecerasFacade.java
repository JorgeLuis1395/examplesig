/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Rangoscabeceras;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class RangoscabecerasFacade extends AbstractFacade<Rangoscabeceras> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RangoscabecerasFacade() {
        super(Rangoscabeceras.class);
    }

    @Override
    protected String modificarObjetos(Rangoscabeceras nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cabecera>" + nuevo.getCabecera() + "</cabecera>";
        retorno += "<minimo>" + nuevo.getMinimo() + "</minimo>";
        retorno += "<maximo>" + nuevo.getMaximo() + "</maximo>";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<riesgo>" + nuevo.getRiesgo() + "</riesgo>";
        retorno += "<porcentaje>" + nuevo.getPorcentaje() + "</porcentaje>";

        return retorno;
    }

}
