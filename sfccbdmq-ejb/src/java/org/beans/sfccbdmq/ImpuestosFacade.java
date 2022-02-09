/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Impuestos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ImpuestosFacade extends AbstractFacade<Impuestos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImpuestosFacade() {
        super(Impuestos.class);
    }

    @Override
    protected String modificarObjetos(Impuestos nuevo) {
        String retorno = "";
        retorno += "<cuentaventas>" + nuevo.getCuentaventas() + "</cuentaventas>";
        retorno += "<cuentacompras>" + nuevo.getCuentacompras() + "</cuentacompras>";
        retorno += "<porcentaje>" + nuevo.getPorcentaje() + "</porcentaje>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<etiqueta>" + nuevo.getEtiqueta() + "</etiqueta>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";

        return retorno;
    }

}
