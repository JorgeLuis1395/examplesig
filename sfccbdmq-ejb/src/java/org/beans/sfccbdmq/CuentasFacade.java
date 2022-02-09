/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cuentas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CuentasFacade extends AbstractFacade<Cuentas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CuentasFacade() {
        super(Cuentas.class);
    }

    @Override
    protected String modificarObjetos(Cuentas nuevo) {
        String retorno = "";
        retorno += "<ccosto>" + nuevo.getCcosto() + "</ccosto>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<imputable>" + nuevo.getImputable() + "</imputable>";
        retorno += "<presupuesto>" + nuevo.getPresupuesto() + "</presupuesto>";
        retorno += "<auxiliares>" + nuevo.getAuxiliares() + "</auxiliares>";
        retorno += "<tipoajuste>" + nuevo.getTipoajuste() + "</tipoajuste>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";

        return retorno;
    }

}
