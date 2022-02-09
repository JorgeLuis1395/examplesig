/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Permisosbodegas;

/**
 *
 * @author edwin
 */
@Stateless
public class PermisosbodegasFacade extends AbstractFacade<Permisosbodegas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PermisosbodegasFacade() {
        super(Permisosbodegas.class);
    }

    @Override
    protected String modificarObjetos(Permisosbodegas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<aprobacion>" + nuevo.getAprobacion()+ "</aprobacion>";
        retorno += "<aprobacionIngreso>" + nuevo.getAprobacionIngreso()+ "</aprobacionIngreso>";
        retorno += "<aprobacionRecepcion>" + nuevo.getAprobacionRecepcion()+ "</aprobacionRecepcion>";
        retorno += "<bodega>" + nuevo.getBodega()+ "</bodega>";
        retorno += "<despacho>" + nuevo.getDespacho()+ "</despacho>";
        retorno += "<ingreso>" + nuevo.getIngreso()+ "</ingreso>";
        retorno += "<ingresot>" + nuevo.getIngresot()+ "</ingresot>";
        retorno += "<recepcion>" + nuevo.getRecepcion()+ "</recepcion>";
        retorno += "<registro>" + nuevo.getRegistro()+ "</registro>";
        retorno += "<revision>" + nuevo.getRevision()+ "</revision>";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        return retorno;
    }
    
}
