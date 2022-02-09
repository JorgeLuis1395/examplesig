/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Suministros;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class SuministrosFacade extends AbstractFacade<Suministros> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SuministrosFacade() {
        super(Suministros.class);
    }

    @Override
    protected String modificarObjetos(Suministros nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<alias>" + nuevo.getAlias() + "</alias>";
        retorno += "<codigobarras>" + nuevo.getCodigobarras() + "</codigobarras>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<comercial>" + nuevo.getComercial() + "</comercial>";
        retorno += "<fechacreacion>" + nuevo.getFechacreacion() + "</fechacreacion>";
        retorno += "<unidad>" + nuevo.getUnidad() + "</unidad>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<rango>" + nuevo.getRango() + "</rango>";
        retorno += "<pvp>" + nuevo.getPvp() + "</pvp>";

        return retorno;
    }

}
