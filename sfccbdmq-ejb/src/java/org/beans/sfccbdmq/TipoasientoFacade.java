/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipoasiento;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipoasientoFacade extends AbstractFacade<Tipoasiento> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoasientoFacade() {
        super(Tipoasiento.class);
    }

    @Override
    protected String modificarObjetos(Tipoasiento nuevo) {
        String retorno = "";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<ultimo>" + nuevo.getUltimo() + "</ultimo>";
        retorno += "<modulo>" + nuevo.getModulo() + "</modulo>";
        retorno += "<equivalencia>" + nuevo.getEquivalencia() + "</equivalencia>";
        retorno += "<editable>" + nuevo.getEditable() + "</editable>";
        retorno += "<rubro>" + nuevo.getRubro() + "</rubro>";

        return retorno;
    }

}
