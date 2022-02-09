/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Retenciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class RetencionesFacade extends AbstractFacade<Retenciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RetencionesFacade() {
        super(Retenciones.class);
    }

    @Override
    protected String modificarObjetos(Retenciones nuevo) {
        String retorno = "";
        retorno += "<reclasificacion>" + nuevo.getReclasificacion() + "</reclasificacion>";
        retorno += "<ats>" + nuevo.getAts() + "</ats>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<porcentaje>" + nuevo.getPorcentaje() + "</porcentaje>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<etiqueta>" + nuevo.getEtiqueta() + "</etiqueta>";
        retorno += "<impuesto>" + nuevo.getImpuesto() + "</impuesto>";
        retorno += "<bien>" + nuevo.getBien() + "</bien>";
        retorno += "<especial>" + nuevo.getEspecial() + "</especial>";

        return retorno;
    }

}
