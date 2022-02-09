/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Postulaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PostulacionesFacade extends AbstractFacade<Postulaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PostulacionesFacade() {
        super(Postulaciones.class);
    }

    @Override
    protected String modificarObjetos(Postulaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<solicitudcargo>" + nuevo.getSolicitudcargo() + "</solicitudcargo>";
        retorno += "<observacion>" + nuevo.getObservacion() + "</observacion>";
        retorno += "<fechaingreso>" + nuevo.getFechaingreso() + "</fechaingreso>";
        retorno += "<fechaprueba>" + nuevo.getFechaprueba() + "</fechaprueba>";
        retorno += "<fechaentrevista>" + nuevo.getFechaentrevista() + "</fechaentrevista>";
        retorno += "<fecharesultados>" + nuevo.getFecharesultados() + "</fecharesultados>";
        retorno += "<responsable>" + nuevo.getResponsable() + "</responsable>";
        retorno += "<validacion>" + nuevo.getValidacion() + "</validacion>";
        retorno += "<nota>" + nuevo.getNota() + "</nota>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<validacionmerito>" + nuevo.getValidacionmerito() + "</validacionmerito>";
        retorno += "<validacionposicion>" + nuevo.getValidacionposicion() + "</validacionposicion>";
        retorno += "<observacionmeritos>" + nuevo.getObservacionmeritos() + "</observacionmeritos>";
        retorno += "<observacionoposicion>" + nuevo.getObservacionoposicion() + "</observacionoposicion>";
        retorno += "<ganador>" + nuevo.getGanador() + "</ganador>";

        return retorno;
    }

}
