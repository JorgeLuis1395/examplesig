/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Comprasuministros;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ComprasuministrosFacade extends AbstractFacade<Comprasuministros> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComprasuministrosFacade() {
        super(Comprasuministros.class);
    }

    @Override
    protected String modificarObjetos(Comprasuministros nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<partida>" + nuevo.getPartida()+ "</partida>";
        retorno += "<usuario>" + nuevo.getUsuario()+ "</usuario>";
        retorno += "<anio>" + nuevo.getAnio()+ "</anio>";
        retorno += "<cantidad>" + nuevo.getCantidad()+ "</cantidad>";
        retorno += "<fechauso>" + nuevo.getFechauso()+ "</fechauso>";
        retorno += "<periodo>" + nuevo.getPeriodo()+ "</periodo>";
        retorno += "<suministro>" + nuevo.getSuministro()+ "</suministro>";

        return retorno;
    }
    
}
