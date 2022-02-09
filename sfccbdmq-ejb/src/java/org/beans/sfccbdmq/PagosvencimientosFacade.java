/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Pagosvencimientos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PagosvencimientosFacade extends AbstractFacade<Pagosvencimientos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagosvencimientosFacade() {
        super(Pagosvencimientos.class);
    }

    @Override
    protected String modificarObjetos(Pagosvencimientos nuevo) {
        String retorno = "";
        retorno += "<valoranticipo>" + nuevo.getValoranticipo() + "</valoranticipo>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<obligacion>" + nuevo.getObligacion() + "</obligacion>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<pagado>" + nuevo.getPagado() + "</pagado>";
        retorno += "<fechapago>" + nuevo.getFechapago() + "</fechapago>";
        retorno += "<compromiso>" + nuevo.getCompromiso() + "</compromiso>";
        retorno += "<anticipo>" + nuevo.getAnticipo() + "</anticipo>";
        retorno += "<kardexbanco>" + nuevo.getKardexbanco() + "</kardexbanco>";

        return retorno;
    }

}
