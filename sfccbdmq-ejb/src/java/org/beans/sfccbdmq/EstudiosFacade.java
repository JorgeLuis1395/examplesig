/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Estudios;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EstudiosFacade extends AbstractFacade<Estudios> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstudiosFacade() {
        super(Estudios.class);
    }

    @Override
    protected String modificarObjetos(Estudios nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<nivel>" + nuevo.getNivel() + "</nivel>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";
        retorno += "<universidad>" + nuevo.getUniversidad() + "</universidad>";
        retorno += "<pais>" + nuevo.getPais() + "</pais>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<titulo>" + nuevo.getTitulo() + "</titulo>";
        retorno += "<responvalid>" + nuevo.getResponvalid() + "</responvalid>";
        retorno += "<obsvalidado>" + nuevo.getObsvalidado() + "</obsvalidado>";
        retorno += "<imagen>" + nuevo.getImagen() + "</imagen>";

        return retorno;
    }

}
