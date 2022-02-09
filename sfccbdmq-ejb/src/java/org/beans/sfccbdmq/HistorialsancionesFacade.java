/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Historialsanciones;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class HistorialsancionesFacade extends AbstractFacade<Historialsanciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistorialsancionesFacade() {
        super(Historialsanciones.class);
    }

    @Override
    protected String modificarObjetos(Historialsanciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<especunaria>" + nuevo.getEspecunaria()+ "</especunaria>";
        retorno += "<esleve>" + nuevo.getEsleve()+ "</esleve>";
        retorno += "<faplicacion>" + nuevo.getFaplicacion() + "</faplicacion>";
        retorno += "<faprobacion>" + nuevo.getFaprobacion() + "</faprobacion>";
        retorno += "<fsancion>" + nuevo.getFsancion() + "</fsancion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";

        return retorno;
    }

}
