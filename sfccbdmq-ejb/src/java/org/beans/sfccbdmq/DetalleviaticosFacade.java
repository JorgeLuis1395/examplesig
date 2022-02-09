/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Viaticosempleado;

/**
 *
 * @author edison
 */
@Stateless
public class DetalleviaticosFacade extends AbstractFacade<Detalleviaticos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleviaticosFacade() {
        super(Detalleviaticos.class);
    }
    
    @Override
    protected String modificarObjetos(Detalleviaticos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<viaticoempleado>" + nuevo.getViaticoempleado() + "</viaticoempleado>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<lugar>" + nuevo.getLugar() + "</lugar>";
        retorno += "<tipocomprobante>" + nuevo.getTipocomprobante() + "</tipocomprobante>";
        retorno += "<nrocomprobante>" + nuevo.getNrocomprobante() + "</nrocomprobante>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";
        retorno += "<valorvalidado>" + nuevo.getValorvalidado() + "</valorvalidado>";
        retorno += "<detallevalidado>" + nuevo.getDetallevalidado() + "</detallevalidado>";
        
        return retorno;
    }
    
}
