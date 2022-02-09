/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Sucursales;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class SucursalesFacade extends AbstractFacade<Sucursales> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SucursalesFacade() {
        super(Sucursales.class);
    }

    @Override
    protected String modificarObjetos(Sucursales nuevo) {
        String retorno = "";
        retorno += "<sector>" + nuevo.getSector() + "</sector>";
        retorno += "<cc>" + nuevo.getCc() + "</cc>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<ruc>" + nuevo.getRuc() + "</ruc>";
        retorno += "<razonsocial>" + nuevo.getRazonsocial() + "</razonsocial>";
        retorno += "<relacioncobranza>" + nuevo.getRelacioncobranza() + "</relacioncobranza>";
        retorno += "<principal>" + nuevo.getPrincipal() + "</principal>";
        retorno += "<secuendaria>" + nuevo.getSecuendaria() + "</secuendaria>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";

        return retorno;
    }

}
