/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Trackingspoa;

/**
 *
 * @author luis
 */
@Stateless
public class TrackingspoaFacade extends AbstractFacade<Trackingspoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrackingspoaFacade() {
        super(Trackingspoa.class);
    }

    @Override
    protected String modificarObjetos(Trackingspoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<responsable>" + nuevo.getResponsable() + "</responsable>";
        retorno += "<certificacion>" + nuevo.getCertificacion() + "</certificacion>";
        retorno += "<reforma>" + nuevo.getReforma() + "</reforma>";
        retorno += "<reformanombre>" + nuevo.getReformanombre() + "</reformanombre>";
        retorno += "<asignacion>" + nuevo.getAsignacion() + "</asignacion>";
        retorno += "<reformapac>" + nuevo.getReformapac() + "</reformapac>";
        retorno += "<aprobadopac>" + nuevo.getAprobadopac() + "</aprobadopac>";
        retorno += "<utilizado>" + nuevo.getUtilizado() + "</utilizado>";
        return retorno;
    }

}
