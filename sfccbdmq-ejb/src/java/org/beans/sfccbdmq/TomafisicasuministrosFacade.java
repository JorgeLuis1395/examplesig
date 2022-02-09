/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tomafisica;
import org.entidades.sfccbdmq.Tomafisicasuministros;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
public class TomafisicasuministrosFacade extends AbstractFacade<Tomafisicasuministros> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TomafisicasuministrosFacade() {
        super(Tomafisicasuministros.class);
    }

    @Override
    protected String modificarObjetos(Tomafisicasuministros nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<bodega>" + nuevo.getBodega() + "</bodega>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<fechafin>" + nuevo.getFechafin() + "</fechafin>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";

        return retorno;
    }
    public void generaToma(Tomafisicasuministros t){
        Query q=em.createNativeQuery("insert into tomasuministro  (saldo,suministro,tomafisica) select "
                + "saldo,suministro,"+t.getId()+" from bodegasuministro where bodega="+t.getBodega().getId()+"");
        q.executeUpdate();
    }
}
