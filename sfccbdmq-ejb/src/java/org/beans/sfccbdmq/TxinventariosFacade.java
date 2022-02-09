/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Txinventarios;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class TxinventariosFacade extends AbstractFacade<Txinventarios> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TxinventariosFacade() {
        super(Txinventarios.class);
    }

    @Override
    protected String modificarObjetos(Txinventarios nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<iniciales>" + nuevo.getIniciales() + "</iniciales>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<tipoasiento>" + nuevo.getTipoasiento() + "</tipoasiento>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<anulado>" + nuevo.getAnulado() + "</anulado>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<contabiliza>" + nuevo.getContabiliza() + "</contabiliza>";
        retorno += "<costea>" + nuevo.getCostea() + "</costea>";
        retorno += "<transaferencia>" + nuevo.getTransaferencia() + "</transaferencia>";
        retorno += "<transformacion>" + nuevo.getTransformacion() + "</transformacion>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";

        return retorno;
    }

}
