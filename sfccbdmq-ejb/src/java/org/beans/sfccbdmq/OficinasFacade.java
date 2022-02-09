/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Oficinas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class OficinasFacade extends AbstractFacade<Oficinas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OficinasFacade() {
        super(Oficinas.class);
    }

    @Override
    protected String modificarObjetos(Oficinas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<piso>" + nuevo.getPiso() + "</piso>";
        retorno += "<edificio>" + nuevo.getEdificio() + "</edificio>";
        retorno += "<parqueaderos>" + nuevo.getParqueaderos() + "</parqueaderos>";
        retorno += "<telefonos>" + nuevo.getTelefonos() + "</telefonos>";
        retorno += "<organigrama>" + nuevo.getOrganigrama() + "</organigrama>";
        retorno += "<centrocosto>" + nuevo.getCentrocosto() + "</centrocosto>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";

        return retorno;
    }

}
