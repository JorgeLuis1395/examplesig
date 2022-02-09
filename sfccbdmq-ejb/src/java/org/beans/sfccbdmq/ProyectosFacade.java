/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Proyectos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ProyectosFacade extends AbstractFacade<Proyectos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProyectosFacade() {
        super(Proyectos.class);
    }

    @Override
    protected String modificarObjetos(Proyectos nuevo) {
        String retorno = "";
        retorno += "<ingreso>" + nuevo.getIngreso() + "</ingreso>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
