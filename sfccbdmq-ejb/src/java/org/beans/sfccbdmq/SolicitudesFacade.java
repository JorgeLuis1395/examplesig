/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Solicitudes;

/**
 *
 * @author luis
 */
@Stateless
public class SolicitudesFacade extends AbstractFacade<Solicitudes> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudesFacade() {
        super(Solicitudes.class);
    }

    @Override
    protected String modificarObjetos(Solicitudes nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fechasolicitud>" + nuevo.getFechasolicitud()+ "</fechasolicitud>";
        retorno += "<usuariosolicitante>" + nuevo.getUsuariosolicitante()+ "</usuariosolicitante>";
        retorno += "<usuariotranferir>" + nuevo.getUsuariotranferir()+ "</usuariotranferir>";
        retorno += "<fecharespuesta>" + nuevo.getFecharespuesta()+ "</fecharespuesta>";
        retorno += "<estado>" + nuevo.getEstado()+ "</estado>";
        retorno += "<usuarioaprobacion>" + nuevo.getUsuarioaprobacion()+ "</usuarioaprobacion>";
        
        return retorno;
    }

}
