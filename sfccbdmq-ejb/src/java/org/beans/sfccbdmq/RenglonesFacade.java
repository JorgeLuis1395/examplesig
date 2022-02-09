/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.entidades.sfccbdmq.Renglones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@Stateless
public class RenglonesFacade extends AbstractFacade<Renglones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RenglonesFacade() {
        super(Renglones.class);
    }
//    @Override
//    public void create(Renglones entity, String usuario) throws InsertarException {
//        if (entity.getSigno()==null){
//            entity.setSigno(1);
//        }
//        super.create(entity, usuario);
//    }
//
//    @Override
//    public void edit(Renglones entity, String usuario) throws GrabarException {
//        if (entity.getSigno()==null){
//            entity.setSigno(1);
//        }
//        super.edit(entity, usuario);
//    }

    @Override
    protected String modificarObjetos(Renglones nuevo) {
        String retorno = "";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<presupuesto>" + nuevo.getPresupuesto() + "</presupuesto>";
        retorno += "<auxiliar>" + nuevo.getAuxiliar() + "</auxiliar>";
        retorno += "<centrocosto>" + nuevo.getCentrocosto() + "</centrocosto>";
        retorno += "<cabecera>" + nuevo.getCabecera() + "</cabecera>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";

        return retorno;
    }

}