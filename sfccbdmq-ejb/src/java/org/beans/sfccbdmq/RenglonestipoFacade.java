/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Renglonestipo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class RenglonestipoFacade extends AbstractFacade<Renglonestipo> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RenglonestipoFacade() {
        super(Renglonestipo.class);
    }

    @Override
    protected String modificarObjetos(Renglonestipo nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<asientotipo>" + nuevo.getAsientotipo() + "</asientotipo>";
        retorno += "<centrocosto>" + nuevo.getCentrocosto() + "</centrocosto>";
        retorno += "<auxiliar>" + nuevo.getAuxiliar() + "</auxiliar>";
        retorno += "<presupuesto>" + nuevo.getPresupuesto() + "</presupuesto>";

        return retorno;
    }

}
