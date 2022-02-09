/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cursos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CursosFacade extends AbstractFacade<Cursos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CursosFacade() {
        super(Cursos.class);
    }

    @Override
    protected String modificarObjetos(Cursos nuevo) {
        String retorno = "";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<duracion>" + nuevo.getDuracion() + "</duracion>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";
        retorno += "<recibido>" + nuevo.getRecibido() + "</recibido>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";

        return retorno;
    }

}
