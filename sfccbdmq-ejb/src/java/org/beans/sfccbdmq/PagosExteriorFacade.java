/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.PagosExterior;

/**
 *
 * @author edwin
 */
@Stateless
public class PagosExteriorFacade extends AbstractFacade<PagosExterior> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagosExteriorFacade() {
        super(PagosExterior.class);
    }

    @Override
    protected String modificarObjetos(PagosExterior nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<dobleTributacion>" + nuevo.getDobleTributacion()+ "</dobleTributacion>";
        retorno += "<menorImposicion>" + nuevo.getMenorImposicion()+ "</menorImposicion>";
        retorno += "<paisFiscal>" + nuevo.getPaisFiscal()+ "</paisFiscal>";
        retorno += "<paisGeneral>" + nuevo.getPaisGeneral()+ "</paisGeneral>";
        retorno += "<paisPago>" + nuevo.getPaisPago()+ "</paisPago>";
        retorno += "<regimen>" + nuevo.getRegimen()+ "</regimen>";
        retorno += "<sujetoRetencion>" + nuevo.getSujetoRetencion()+ "</sujetoRetencion>";
        retorno += "<tipoRegimen>" + nuevo.getTipoRegimen()+ "</tipoRegimen>";
        return retorno;
    }
    
}
