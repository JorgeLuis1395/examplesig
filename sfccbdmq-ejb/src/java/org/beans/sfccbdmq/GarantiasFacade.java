/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Garantias;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class GarantiasFacade extends AbstractFacade<Garantias> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GarantiasFacade() {
        super(Garantias.class);
    }

    @Override
    protected String modificarObjetos(Garantias nuevo) {
        String retorno = "";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<anticipo>" + nuevo.getAnticipo() + "</anticipo>";
        retorno += "<renovacion>" + nuevo.getRenovacion() + "</renovacion>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fincanciera>" + nuevo.getFincanciera() + "</fincanciera>";
        retorno += "<monto>" + nuevo.getMonto() + "</monto>";
        retorno += "<aseguradora>" + nuevo.getAseguradora() + "</aseguradora>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<contabilizacion>" + nuevo.getContabilizacion() + "</contabilizacion>";
        retorno += "<cancelacion>" + nuevo.getCancelacion() + "</cancelacion>";
        retorno += "<vencimiento>" + nuevo.getVencimiento() + "</vencimiento>";
        retorno += "<objeto>" + nuevo.getObjeto() + "</objeto>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";

        return retorno;
    }

}
