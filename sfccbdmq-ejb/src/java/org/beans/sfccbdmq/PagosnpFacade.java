/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Pagosnp;

/**
 *
 * @author edwin
 */
@Stateless
public class PagosnpFacade extends AbstractFacade<Pagosnp> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagosnpFacade() {
        super(Pagosnp.class);
    }

    @Override
    protected String modificarObjetos(Pagosnp nuevo) {
       String retorno = "";
        retorno += "<descripcion>" + nuevo.getDescripcion()+ "</descripcion>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<contabilizacion>" + nuevo.getContabilizacion()+ "</contabilizacion>";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cuentas>" + nuevo.getCuentas()+ "</cuentas>";
        retorno += "<proveedor>" + nuevo.getProveedor()+ "</proveedor>";
        return retorno;
    }
    
}
