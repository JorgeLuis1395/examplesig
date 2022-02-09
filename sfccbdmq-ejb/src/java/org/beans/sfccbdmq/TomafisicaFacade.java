/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Tomafisica;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
public class TomafisicaFacade extends AbstractFacade<Tomafisica> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TomafisicaFacade() {
        super(Tomafisica.class);
    }

    @Override
    protected String modificarObjetos(Tomafisica nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<responsable>" + nuevo.getResponsable() + "</responsable>";
        retorno += "<fechafin>" + nuevo.getFechafin() + "</fechafin>";

        return retorno;
    }
    // crea la toma
    public void generaToma(Tomafisica t){
        Query q=em.createNativeQuery("insert into detalletoma  (activo,toma,estado,localidad,"
                + "custodio,verificado,codigobarras) select id,"+t.getId()+""
                + ",estado,localizacion,custodio,'false',barras from activos");
        q.executeUpdate();
    }
}
