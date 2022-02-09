/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Certificacionesempleado;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CertificacionesempleadoFacade extends AbstractFacade<Certificacionesempleado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CertificacionesempleadoFacade() {
        super(Certificacionesempleado.class);
    }

    @Override
    protected String modificarObjetos(Certificacionesempleado nuevo) {
        String retorno = "";
        retorno += "<certificaion>" + nuevo.getCertificaion() + "</certificaion>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<imagen>" + nuevo.getImagen() + "</imagen>";
        retorno += "<validado>" + nuevo.getValidado() + "</validado>";

        return retorno;
    }

}
