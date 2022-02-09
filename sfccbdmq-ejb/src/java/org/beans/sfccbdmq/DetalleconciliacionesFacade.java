/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Detalleconciliaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author luis
 */
@Stateless
public class DetalleconciliacionesFacade extends AbstractFacade<Detalleconciliaciones> {
    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleconciliacionesFacade() {
        super(Detalleconciliaciones.class);
    }

    @Override
    protected String modificarObjetos(Detalleconciliaciones nuevo) {
        String retorno = "";
        retorno += "<comprobante>" + nuevo.getComprobante()+ "</comprobante>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<concepto>" + nuevo.getConcepto()+ "</concepto>";
        retorno += "<descripcion>" + nuevo.getDescripcion()+ "</descripcion>";
        retorno += "<fecha>" + nuevo.getFecha()+ "</fecha>";
        retorno += "<oficio>" + nuevo.getOficio()+ "</oficio>";
        retorno += "<referencia>" + nuevo.getReferencia()+ "</referencia>";
        retorno += "<spi>" + nuevo.getSpi()+ "</spi>";
        retorno += "<tipo>" + nuevo.getTipo()+ "</tipo>";
        retorno += "<conciliacion>" + nuevo.getConciliacion()+ "</conciliacion>";
        retorno += "<valor>" + nuevo.getValor()+ "</valor>";

        return retorno;
    }
    
}
