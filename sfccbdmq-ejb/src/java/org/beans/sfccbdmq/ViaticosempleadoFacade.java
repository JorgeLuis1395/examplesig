/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.entidades.sfccbdmq.Viaticosempleado;

/**
 *
 * @author edison
 */
@Stateless
public class ViaticosempleadoFacade extends AbstractFacade<Viaticosempleado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ViaticosempleadoFacade() {
        super(Viaticosempleado.class);
    }
    
    @Override
    protected String modificarObjetos(Viaticosempleado nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<viatico>" + nuevo.getViatico() + "</viatico>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<detallecompromiso>" + nuevo.getDetallecompromiso() + "</detallecompromiso>";
        retorno += "<kardex>" + nuevo.getKardex() + "</kardex>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<fechaliquidacion>" + nuevo.getFechaliquidacion() + "</fechaliquidacion>";
        retorno += "<contrafactura>" + nuevo.getContrafactura() + "</contrafactura>";
        retorno += "<subsistencia>" + nuevo.getSubsistencia() + "</subsistencia>";
        retorno += "<nrosubsistencias>" + nuevo.getNrosubsistencias() + "</nrosubsistencias>";
        
        return retorno;
    }
    
}