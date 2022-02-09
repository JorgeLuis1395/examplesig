/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Cabecerareformaspoa;

/**
 *
 * @author luis
 */
@Stateless
public class CabecerareformaspoaFacade extends AbstractFacade<Cabecerareformaspoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecerareformaspoaFacade() {
        super(Cabecerareformaspoa.class);
    }

    @Override
    protected String modificarObjetos(Cabecerareformaspoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<definitivo>" + nuevo.getDefinitivo() + "</definitivo>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<observacion>" + nuevo.getObservacion() + "</observacion>";
        retorno += "<utilizado>" + nuevo.getUtilizado() + "</utilizado>";
        retorno += "<director>" + nuevo.getDirector() + "</director>";
        retorno += "<obsnegado>" + nuevo.getObsnegado() + "</obsnegado>";
        retorno += "<archivosolicitud>" + nuevo.getArchivosolicitud() + "</archivosolicitud>";

        return retorno;
    }

}
