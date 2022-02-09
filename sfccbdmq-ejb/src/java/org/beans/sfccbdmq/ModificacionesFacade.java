/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Modificaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ModificacionesFacade extends AbstractFacade<Modificaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModificacionesFacade() {
        super(Modificaciones.class);
    }

    @Override
    protected String modificarObjetos(Modificaciones nuevo) {
        String retorno = "";
        retorno += "<objeto>" + nuevo.getObjeto() + "</objeto>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<monto>" + nuevo.getMonto() + "</monto>";
        retorno += "<fechafin>" + nuevo.getFechafin() + "</fechafin>";

        return retorno;
    }

}
