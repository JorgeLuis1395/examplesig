/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Verificacionesdesempenio;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class VerificacionesdesempenioFacade extends AbstractFacade<Verificacionesdesempenio> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VerificacionesdesempenioFacade() {
        super(Verificacionesdesempenio.class);
    }

    @Override
    protected String modificarObjetos(Verificacionesdesempenio nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<experiencia>" + nuevo.getExperiencia() + "</experiencia>";
        retorno += "<valoracion>" + nuevo.getValoracion() + "</valoracion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";

        return retorno;
    }

}
