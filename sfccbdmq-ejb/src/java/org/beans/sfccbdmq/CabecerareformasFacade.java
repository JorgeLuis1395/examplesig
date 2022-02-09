/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cabecerareformas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CabecerareformasFacade extends AbstractFacade<Cabecerareformas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecerareformasFacade() {
        super(Cabecerareformas.class);
    }

    @Override
    protected String modificarObjetos(Cabecerareformas nuevo) {
        String retorno = "";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<definitivo>" + nuevo.getDefinitivo() + "</definitivo>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";

        return retorno;
    }

}
