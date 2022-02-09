/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Hcontratosempelado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class HcontratosempeladoFacade extends AbstractFacade<Hcontratosempelado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HcontratosempeladoFacade() {
        super(Hcontratosempelado.class);
    }

    @Override
    protected String modificarObjetos(Hcontratosempelado nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipocontrato>" + nuevo.getTipocontrato() + "</tipocontrato>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";

        return retorno;
    }

}
