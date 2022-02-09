/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Bkdirecciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class BkdireccionesFacade extends AbstractFacade<Bkdirecciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BkdireccionesFacade() {
        super(Bkdirecciones.class);
    }

    @Override
    protected String modificarObjetos(Bkdirecciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<principal>" + nuevo.getPrincipal() + "</principal>";
        retorno += "<secundaria>" + nuevo.getSecundaria() + "</secundaria>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";
        retorno += "<telefonos>" + nuevo.getTelefonos() + "</telefonos>";
        retorno += "<empleado>" + nuevo.getEmpleado()+ "</empresa>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";

        return retorno;
    }

}
