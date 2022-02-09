/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tipomovactivos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TipomovactivosFacade extends AbstractFacade<Tipomovactivos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipomovactivosFacade() {
        super(Tipomovactivos.class);
    }

    @Override
    protected String modificarObjetos(Tipomovactivos nuevo) {
        String retorno = "";
        retorno += "<cuenta1>" + nuevo.getCuenta1() + "</cuenta1>";
        retorno += "<tipoasientocontrol>" + nuevo.getTipoasientocontrol() + "</tipoasientocontrol>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<contabiliza>" + nuevo.getContabiliza() + "</contabiliza>";
        retorno += "<tipoasiento>" + nuevo.getTipoasiento() + "</tipoasiento>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<debito>" + nuevo.getDebito() + "</debito>";

        return retorno;
    }

}
