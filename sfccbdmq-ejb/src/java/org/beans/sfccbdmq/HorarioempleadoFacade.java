/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Horarioempleado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class HorarioempleadoFacade extends AbstractFacade<Horarioempleado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HorarioempleadoFacade() {
        super(Horarioempleado.class);
    }

    @Override
    protected String modificarObjetos(Horarioempleado nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<hora>" + nuevo.getHora() + "</hora>";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";

        return retorno;
    }

}
