/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Proveedores;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ProveedoresFacade extends AbstractFacade<Proveedores> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProveedoresFacade() {
        super(Proveedores.class);
    }

    @Override
    protected String modificarObjetos(Proveedores nuevo) {
        String retorno = "";
        retorno += "<clasificacion>" + nuevo.getClasificacion() + "</clasificacion>";
        retorno += "<limitecredito>" + nuevo.getLimitecredito() + "</limitecredito>";
        retorno += "<diasdecredito>" + nuevo.getDiasdecredito() + "</diasdecredito>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<limitetransferencia>" + nuevo.getLimitetransferencia() + "</limitetransferencia>";
        retorno += "<banco>" + nuevo.getBanco() + "</banco>";
        retorno += "<ctabancaria>" + nuevo.getCtabancaria() + "</ctabancaria>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<direccion>" + nuevo.getDireccion() + "</direccion>";
        retorno += "<nombrebeneficiario>" + nuevo.getNombrebeneficiario() + "</nombrebeneficiario>";
        retorno += "<rucbeneficiario>" + nuevo.getRucbeneficiario() + "</rucbeneficiario>";
        retorno += "<tipocta>" + nuevo.getTipocta() + "</tipocta>";

        return retorno;
    }

}
