/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Solicitudpersonal;

/**
 *
 * @author luis
 */
@Stateless
public class SolicitudpersonalFacade extends AbstractFacade<Solicitudpersonal> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudpersonalFacade() {
        super(Solicitudpersonal.class);
    }

    @Override
    protected String modificarObjetos(Solicitudpersonal nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<fechasolicitud>" + nuevo.getFechasolicitud() + "</fechasolicitud>";
        retorno += "<empleado>" + nuevo.getEmpleado()+ "</empleado>";
        retorno += "<estado>" + nuevo.getEstado()+ "</estado>";
        retorno += "<numero>" + nuevo.getNumero()+ "</numero>";
        return retorno;
    }
    
}