/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Detallesolicitudpersonal;

/**
 *
 * @author luis
 */
@Stateless
public class DetallesolicitudpersonalFacade extends AbstractFacade<Detallesolicitudpersonal> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallesolicitudpersonalFacade() {
        super(Detallesolicitudpersonal.class);
    }

    @Override
    protected String modificarObjetos(Detallesolicitudpersonal nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<aprobado>" + nuevo.getAprobado() + "</aprobado>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<fechaaprobado>" + nuevo.getFechaaprobado() + "</fechaaprobado>";
        retorno += "<solicitudpersonal>" + nuevo.getSolicitudpersonal() + "</solicitudpersonal>";
        retorno += "<numerocertificado>" + nuevo.getNumerocertificado() + "</numerocertificado>";
        retorno += "<mes>" + nuevo.getMes()+ "</mes>";
        retorno += "<anio>" + nuevo.getAnio()+ "</anio>";
        return retorno;
    }
}