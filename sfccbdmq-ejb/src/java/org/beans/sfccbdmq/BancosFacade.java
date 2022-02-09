/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Bancos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class BancosFacade extends AbstractFacade<Bancos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BancosFacade() {
        super(Bancos.class);
    }

    @Override
    protected String modificarObjetos(Bancos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<corriente>" + nuevo.getCorriente() + "</corriente>";
        retorno += "<numerocuenta>" + nuevo.getNumerocuenta() + "</numerocuenta>";
        retorno += "<fondorotativo>" + nuevo.getFondorotativo() + "</fondorotativo>";
        retorno += "<contacto>" + nuevo.getContacto() + "</contacto>";
        retorno += "<email>" + nuevo.getEmail() + "</email>";
        retorno += "<telefono>" + nuevo.getTelefono() + "</telefono>";
        retorno += "<conciliable>" + nuevo.getConciliable() + "</conciliable>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<egreso>" + nuevo.getEgreso() + "</egreso>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<ultimo>" + nuevo.getUltimo() + "</ultimo>";
        retorno += "<transferencia>" + nuevo.getTransferencia() + "</transferencia>";
        return retorno;
    }

}
