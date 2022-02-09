/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cargosxorganigrama;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CargosxorganigramaFacade extends AbstractFacade<Cargosxorganigrama> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CargosxorganigramaFacade() {
        super(Cargosxorganigrama.class);
    }

    @Override
    protected String modificarObjetos(Cargosxorganigrama nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<organigrama>" + nuevo.getOrganigrama() + "</organigrama>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<plazas>" + nuevo.getPlazas() + "</plazas>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<reporta>" + nuevo.getReporta() + "</reporta>";
        retorno += "<supervisa>" + nuevo.getSupervisa() + "</supervisa>";
        retorno += "<coordina>" + nuevo.getCoordina() + "</coordina>";
        retorno += "<tipocontrato>" + nuevo.getTipocontrato() + "</tipocontrato>";

        return retorno;
    }

}
