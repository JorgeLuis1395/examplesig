/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Valesfondos;

/**
 *
 * @author edwin
 */
@Stateless
public class ValesfondosexteriorFacade extends AbstractFacade<Valesfondos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValesfondosexteriorFacade() {
        super(Valesfondos.class);
    }

    @Override
    protected String modificarObjetos(Valesfondos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fondo>" + nuevo.getFondo() + "</fondo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<tipodocumento>" + nuevo.getTipodocumento() + "</tipodocumento>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento() + "</establecimiento>";
        retorno += "<puntoemision>" + nuevo.getPuntoemision() + "</puntoemision>";
        retorno += "<autorizacion>" + nuevo.getAutorizacion() + "</autorizacion>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<baseimponiblecero>" + nuevo.getBaseimponiblecero() + "</baseimponiblecero>";
        retorno += "<baseimponible>" + nuevo.getBaseimponible() + "</baseimponible>";
        retorno += "<iva>" + nuevo.getIva() + "</iva>";
        retorno += "<impuesto>" + nuevo.getImpuesto() + "</impuesto>";
        retorno += "<obligacion>" + nuevo.getObligacion() + "</obligacion>";

        return retorno;
    }

}
