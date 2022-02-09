/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Autorizaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AutorizacionesFacade extends AbstractFacade<Autorizaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AutorizacionesFacade() {
        super(Autorizaciones.class);
    }

    @Override
    protected String modificarObjetos(Autorizaciones nuevo) {
        String retorno = "";
        retorno += "<fechaemision>" + nuevo.getFechaemision() + "</fechaemision>";
        retorno += "<fin>" + nuevo.getFin() + "</fin>";
        retorno += "<inicio>" + nuevo.getInicio() + "</inicio>";
        retorno += "<puntoemision>" + nuevo.getPuntoemision() + "</puntoemision>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento() + "</establecimiento>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipodocumento>" + nuevo.getTipodocumento() + "</tipodocumento>";
        retorno += "<fechacaducidad>" + nuevo.getFechacaducidad() + "</fechacaducidad>";
        retorno += "<autorizacion>" + nuevo.getAutorizacion() + "</autorizacion>";

        return retorno;
    }

}
