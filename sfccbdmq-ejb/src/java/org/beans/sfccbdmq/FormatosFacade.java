/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Formatos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class FormatosFacade extends AbstractFacade<Formatos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FormatosFacade() {
        super(Formatos.class);
    }

    @Override
    protected String modificarObjetos(Formatos nuevo) {
        String retorno = "";
        retorno += "<formato>" + nuevo.getFormato() + "</formato>";
        retorno += "<escxc>" + nuevo.getEscxc() + "</escxc>";
        retorno += "<escxp>" + nuevo.getEscxp() + "</escxp>";
        retorno += "<nivel>" + nuevo.getNivel() + "</nivel>";
        retorno += "<cxpfin>" + nuevo.getCxpfin() + "</cxpfin>";
        retorno += "<cxpinicio>" + nuevo.getCxpinicio() + "</cxpinicio>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<inicial>" + nuevo.getInicial() + "</inicial>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

}
