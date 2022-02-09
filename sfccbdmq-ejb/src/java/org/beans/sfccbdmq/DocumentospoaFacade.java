/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Documentospoa;

/**
 *
 * @author luis
 */
@Stateless
public class DocumentospoaFacade extends AbstractFacade<Documentospoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentospoaFacade() {
        super(Documentospoa.class);
    }

    @Override
    protected String modificarObjetos(Documentospoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<nombrearchivo>" + nuevo.getNombrearchivo() + "</nombrearchivo>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<path>" + nuevo.getPath() + "</path>";
        retorno += "<certificacion>" + nuevo.getCertificacion() + "</certificacion>";
        retorno += "<cabecerareforma>" + nuevo.getCabecerareforma() + "</cabecerareforma>";

        return retorno;
    }

}
