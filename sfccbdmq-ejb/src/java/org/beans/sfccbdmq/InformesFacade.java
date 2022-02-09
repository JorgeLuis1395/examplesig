/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Informes;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class InformesFacade extends AbstractFacade<Informes> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InformesFacade() {
        super(Informes.class);
    }

    @Override
    protected String modificarObjetos(Informes nuevo) {
        String retorno = "";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<multa>" + nuevo.getMulta() + "</multa>";

        return retorno;
    }

}
