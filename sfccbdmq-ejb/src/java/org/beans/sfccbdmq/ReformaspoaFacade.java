/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Reformaspoa;

/**
 *
 * @author luis
 */
@Stateless
public class ReformaspoaFacade extends AbstractFacade<Reformaspoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReformaspoaFacade() {
        super(Reformaspoa.class);
    }

    @Override
    protected String modificarObjetos(Reformaspoa nuevo) {
       String retorno = "";
       retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cabecera>" + nuevo.getCabecera() + "</cabecera>";
        retorno += "<asignacion>" + nuevo.getAsignacion() + "</asignacion>";
        retorno += "<requerimiento>" + nuevo.getRequerimiento() + "</requerimiento>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<nombreproyecto>" + nuevo.getNombreproyecto() + "</nombreproyecto>";
        retorno += "<aprobado>" + nuevo.getAprobado() + "</aprobado>";
        
        

        return retorno;
    }
    
}
