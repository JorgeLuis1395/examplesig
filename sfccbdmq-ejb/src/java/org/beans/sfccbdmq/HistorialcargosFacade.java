/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Historialcargos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class HistorialcargosFacade extends AbstractFacade<Historialcargos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistorialcargosFacade() {
        super(Historialcargos.class);
    }

    @Override
    protected String modificarObjetos(Historialcargos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<tipoacciones>" + nuevo.getTipoacciones() + "</tipoacciones>";
        retorno += "<vigente>" + nuevo.getVigente() + "</vigente>";
        retorno += "<aprobacion>" + nuevo.getAprobacion() + "</aprobacion>";
        retorno += "<sueldobase>" + nuevo.getSueldobase() + "</sueldobase>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<tipocontrato>" + nuevo.getTipocontrato() + "</tipocontrato>";

        return retorno;
    }

}