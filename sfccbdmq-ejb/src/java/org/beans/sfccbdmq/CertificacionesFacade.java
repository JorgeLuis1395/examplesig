/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.Arrays;
import org.entidades.sfccbdmq.Certificaciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CertificacionesFacade extends AbstractFacade<Certificaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CertificacionesFacade() {
        super(Certificaciones.class);
    }

    @Override
    protected String modificarObjetos(Certificaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<anulado>" + nuevo.getAnulado() + "</anulado>";
        retorno += "<dependencia>" + nuevo.getDependencia() + "</dependencia>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<impreso>" + nuevo.getImpreso() + "</impreso>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<numerodocumeto>" + nuevo.getNumerodocumeto() + "</numerodocumeto>";
        retorno += "<tipodocumento>" + nuevo.getTipodocumento() + "</tipodocumento>";
        retorno += "<archivo>" + nuevo.getArchivo()+ "</archivo>";

        return retorno;
    }

}