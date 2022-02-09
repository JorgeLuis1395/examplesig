/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cabecerasrrhh;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CabecerasrrhhFacade extends AbstractFacade<Cabecerasrrhh> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecerasrrhhFacade() {
        super(Cabecerasrrhh.class);
    }

    @Override
    protected String modificarObjetos(Cabecerasrrhh nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<datoimpresion>" + nuevo.getDatoimpresion() + "</datoimpresion>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<tipodato>" + nuevo.getTipodato() + "</tipodato>";
        retorno += "<lista>" + nuevo.getLista() + "</lista>";
        retorno += "<grupo>" + nuevo.getGrupo() + "</grupo>";
        retorno += "<ayuda>" + nuevo.getAyuda() + "</ayuda>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";

        return retorno;
    }

}
