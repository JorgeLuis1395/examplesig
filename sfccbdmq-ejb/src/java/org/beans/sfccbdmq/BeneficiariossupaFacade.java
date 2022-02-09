/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Beneficiariossupa;

/**
 *
 * @author luis
 */
@Stateless
public class BeneficiariossupaFacade extends AbstractFacade<Beneficiariossupa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BeneficiariossupaFacade() {
        super(Beneficiariossupa.class);
    }

    @Override
    protected String modificarObjetos(Beneficiariossupa nuevo) {
      String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<cedulabeneficiario>" + nuevo.getCedulabeneficiario() + "</cedulabeneficiario>";
        retorno += "<nombrebeneficiario>" + nuevo.getNombrebeneficiario() + "</nombrebeneficiario>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<codigossupa>" + nuevo.getCodigossupa() + "</codigossupa>";

        return retorno;
    }
    
}
