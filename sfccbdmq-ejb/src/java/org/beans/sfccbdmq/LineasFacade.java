/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Lineas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class LineasFacade extends AbstractFacade<Lineas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LineasFacade() {
        super(Lineas.class);
    }

    @Override
    protected String modificarObjetos(Lineas nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<posicion>" + nuevo.getPosicion() + "</posicion>";
        retorno += "<campo>" + nuevo.getCampo() + "</campo>";
        retorno += "<operacion>" + nuevo.getOperacion() + "</operacion>";
        retorno += "<reporte>" + nuevo.getReporte() + "</reporte>";

        return retorno;
    }

}
