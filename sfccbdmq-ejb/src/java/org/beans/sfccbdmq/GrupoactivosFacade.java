/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Grupoactivos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class GrupoactivosFacade extends AbstractFacade<Grupoactivos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoactivosFacade() {
        super(Grupoactivos.class);
    }

    @Override
    protected String modificarObjetos(Grupoactivos nuevo) {
        String retorno = "";
        retorno += "<iniciodepreciccion>" + nuevo.getIniciodepreciccion() + "</iniciodepreciccion>";
        retorno += "<metododepreciacion>" + nuevo.getMetododepreciacion() + "</metododepreciacion>";
        retorno += "<depreciable>" + nuevo.getDepreciable() + "</depreciable>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<secuencia>" + nuevo.getSecuencia() + "</secuencia>";
        retorno += "<findepreciacion>" + nuevo.getFindepreciacion() + "</findepreciacion>";
        retorno += "<valorresidual>" + nuevo.getValorresidual() + "</valorresidual>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<vidautil>" + nuevo.getVidautil() + "</vidautil>";

        return retorno;
    }

}
