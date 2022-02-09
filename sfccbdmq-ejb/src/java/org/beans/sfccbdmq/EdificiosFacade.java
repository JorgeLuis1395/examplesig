/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Edificios;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EdificiosFacade extends AbstractFacade<Edificios> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EdificiosFacade() {
        super(Edificios.class);
    }

    @Override
    protected String modificarObjetos(Edificios nuevo) {
        String retorno = "";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<telefonos>" + nuevo.getTelefonos() + "</telefonos>";
        retorno += "<ciudad>" + nuevo.getCiudad() + "</ciudad>";
        retorno += "<pisos>" + nuevo.getPisos() + "</pisos>";
        retorno += "<parqueaderos>" + nuevo.getParqueaderos() + "</parqueaderos>";
        retorno += "<callesecundaria>" + nuevo.getCallesecundaria() + "</callesecundaria>";
        retorno += "<calleprimaria>" + nuevo.getCalleprimaria() + "</calleprimaria>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";

        return retorno;
    }

}
