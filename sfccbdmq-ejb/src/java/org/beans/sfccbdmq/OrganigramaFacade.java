/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Organigrama;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class OrganigramaFacade extends AbstractFacade<Organigrama> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrganigramaFacade() {
        super(Organigrama.class);
    }

    @Override
    protected String modificarObjetos(Organigrama nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<ordinal>" + nuevo.getOrdinal() + "</ordinal>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<superior>" + nuevo.getSuperior() + "</superior>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<codigoalterno>" + nuevo.getCodigoalterno()+ "</codigoalterno>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";

        return retorno;
    }

}
