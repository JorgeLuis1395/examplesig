/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Descuentoscompras;

/**
 *
 * @author edwin
 */
@Stateless
public class DescuentoscomprasFacade extends AbstractFacade<Descuentoscompras> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DescuentoscomprasFacade() {
        super(Descuentoscompras.class);
    }

    @Override
    protected String modificarObjetos(Descuentoscompras nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cuentacontable>" + nuevo.getCuentaContable()+ "</cuentacontable>";
        retorno += "<cuentaproveedor>" + nuevo.getCuentaProveedor()+ "</cuentaproveedor>";
        retorno += "<esempleado>" + nuevo.getEsEmpleado()+ "</esempleado>";
        retorno += "<obligacion>" + nuevo.getObligacion()+ "</obligacion>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";
        return retorno;
    }
    
}
