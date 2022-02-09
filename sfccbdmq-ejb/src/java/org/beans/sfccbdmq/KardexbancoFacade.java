/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Kardexbanco;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class KardexbancoFacade extends AbstractFacade<Kardexbanco> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KardexbancoFacade() {
        super(Kardexbanco.class);
    }

    @Override
    protected String modificarObjetos(Kardexbanco nuevo) {
        String retorno = "";
        retorno += "<formapago>" + nuevo.getFormapago() + "</formapago>";
        retorno += "<codigospi>" + nuevo.getCodigospi() + "</codigospi>";
        retorno += "<usuariograba>" + nuevo.getUsuariograba() + "</usuariograba>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<tipomov>" + nuevo.getTipomov() + "</tipomov>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<bancotransferencia>" + nuevo.getBancotransferencia() + "</bancotransferencia>";
        retorno += "<anticipo>" + nuevo.getAnticipo() + "</anticipo>";
        retorno += "<spi>" + nuevo.getSpi() + "</spi>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<cuentatrans>" + nuevo.getCuentatrans() + "</cuentatrans>";
        retorno += "<tcuentatrans>" + nuevo.getTcuentatrans() + "</tcuentatrans>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<usuarioentrega>" + nuevo.getUsuarioentrega() + "</usuarioentrega>";
        retorno += "<fentrega>" + nuevo.getFentrega() + "</fentrega>";
        retorno += "<beneficiario>" + nuevo.getBeneficiario() + "</beneficiario>";
        retorno += "<egreso>" + nuevo.getEgreso() + "</egreso>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<fechamov>" + nuevo.getFechamov() + "</fechamov>";
        retorno += "<fechaabono>" + nuevo.getFechaabono() + "</fechaabono>";
        retorno += "<banco>" + nuevo.getBanco() + "</banco>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fechagraba>" + nuevo.getFechagraba() + "</fechagraba>";

        return retorno;
    }

}
