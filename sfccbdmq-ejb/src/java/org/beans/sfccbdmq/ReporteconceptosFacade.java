/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Reporteconceptos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ReporteconceptosFacade extends AbstractFacade<Reporteconceptos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReporteconceptosFacade() {
        super(Reporteconceptos.class);
    }

    @Override
    protected String modificarObjetos(Reporteconceptos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<reporte>" + nuevo.getReporte() + "</reporte>";
        retorno += "<titulo>" + nuevo.getTitulo() + "</titulo>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";

        return retorno;
    }

}
