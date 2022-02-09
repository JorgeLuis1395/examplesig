/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Valescajas;

/**
 *
 * @author edwin
 */
@Stateless
public class ValescajasFacade extends AbstractFacade<Valescajas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValescajasFacade() {
        super(Valescajas.class);
    }

    @Override
    protected String modificarObjetos(Valescajas nuevo) {
        String retorno = "";
        retorno += "<autorizacion>" + nuevo.getAutorizacion()+ "</autorizacion>";
        retorno += "<concepto>" + nuevo.getConcepto()+ "</concepto>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento()+ "</establecimiento>";
        retorno += "<puntoemision>" + nuevo.getPuntoemision()+ "</puntoemision>";
        retorno += "<baseimponible>" + nuevo.getBaseimponible()+ "</baseimponible>";
        retorno += "<baseimponiblecero>" + nuevo.getBaseimponiblecero()+ "</baseimponiblecero>";
        retorno += "<id>" + nuevo.getId()+ "</id>";
        retorno += "<caja>" + nuevo.getCaja()+ "</caja>";
        retorno += "<estado>" + nuevo.getEstado()+ "</estado>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        retorno += "<iva>" + nuevo.getIva()+ "</iva>";
        retorno += "<numero>" + nuevo.getNumero()+ "</numero>";
        retorno += "<proveedor>" + nuevo.getProveedor()+ "</proveedor>";
        retorno += "<tipodocumento>" + nuevo.getTipodocumento()+ "</tipodocumento>";
        retorno += "<impuesto>" + nuevo.getImpuesto()+ "</impuesto>";
        
        return retorno;
    }
    
}