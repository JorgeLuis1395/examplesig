/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Ordenesdecompra;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class OrdenesdecompraFacade extends AbstractFacade<Ordenesdecompra> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdenesdecompraFacade() {
        super(Ordenesdecompra.class);
    }

    @Override
    protected String modificarObjetos(Ordenesdecompra nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<fechaelaboracion>" + nuevo.getFechaelaboracion() + "</fechaelaboracion>";
        retorno += "<fechadefinitiva>" + nuevo.getFechadefinitiva() + "</fechadefinitiva>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<compromiso>" + nuevo.getCompromiso() + "</compromiso>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";

        return retorno;
    }

}
