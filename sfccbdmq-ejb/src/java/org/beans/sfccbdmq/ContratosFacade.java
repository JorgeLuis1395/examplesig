/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Contratos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ContratosFacade extends AbstractFacade<Contratos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratosFacade() {
        super(Contratos.class);
    }

    @Override
    protected String modificarObjetos(Contratos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<obra>" + nuevo.getObra() + "</obra>";
        retorno += "<esfirma>" + nuevo.getEsfirma() + "</esfirma>";
        retorno += "<fpago>" + nuevo.getFpago() + "</fpago>";
        retorno += "<fechaanticipo>" + nuevo.getFechaanticipo() + "</fechaanticipo>";
        retorno += "<formapago>" + nuevo.getFormapago() + "</formapago>";
        retorno += "<anticipo>" + nuevo.getAnticipo() + "</anticipo>";
        retorno += "<vigente>" + nuevo.getVigente() + "</vigente>";
        retorno += "<titulo>" + nuevo.getTitulo() + "</titulo>";
        retorno += "<objeto>" + nuevo.getObjeto() + "</objeto>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<firma>" + nuevo.getFirma() + "</firma>";
        retorno += "<fin>" + nuevo.getFin() + "</fin>";
        retorno += "<inicio>" + nuevo.getInicio() + "</inicio>";
        retorno += "<administrador>" + nuevo.getAdministrador() + "</administrador>";
        retorno += "<certificacion>" + nuevo.getCertificacion() + "</certificacion>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";

        return retorno;
    }

}
