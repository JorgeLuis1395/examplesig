/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Habilidades;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class HabilidadesFacade extends AbstractFacade<Habilidades> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HabilidadesFacade() {
        super(Habilidades.class);
    }

    @Override
    protected String modificarObjetos(Habilidades nuevo) {
        String retorno = "";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<interes>" + nuevo.getInteres() + "</interes>";
        retorno += "<habilidad>" + nuevo.getHabilidad() + "</habilidad>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
