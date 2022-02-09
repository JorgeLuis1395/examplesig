/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Direcciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DireccionesFacade extends AbstractFacade<Direcciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DireccionesFacade() {
        super(Direcciones.class);
    }

    @Override
    protected String modificarObjetos(Direcciones nuevo) {
        String retorno = "";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento() + "</establecimiento>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<secundaria>" + nuevo.getSecundaria() + "</secundaria>";
        retorno += "<principal>" + nuevo.getPrincipal() + "</principal>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<telefonos>" + nuevo.getTelefonos() + "</telefonos>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";

        return retorno;
    }

}
