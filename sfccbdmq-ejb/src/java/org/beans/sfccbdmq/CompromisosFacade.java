/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Compromisos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CompromisosFacade extends AbstractFacade<Compromisos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CompromisosFacade() {
        super(Compromisos.class);
    }

    @Override
    protected String modificarObjetos(Compromisos nuevo) {
        String retorno = "";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<motivo>" + nuevo.getMotivo() + "</motivo>";
        retorno += "<impresion>" + nuevo.getImpresion() + "</impresion>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<usado>" + nuevo.getUsado() + "</usado>";
        retorno += "<fechareposicion>" + nuevo.getFechareposicion() + "</fechareposicion>";
        retorno += "<banco>" + nuevo.getBanco() + "</banco>";
        retorno += "<responsable>" + nuevo.getResponsable() + "</responsable>";
        retorno += "<archivo>" + nuevo.getArchivo()+ "</archivo>";
        retorno += "<empleado>" + nuevo.getEmpleado()+ "</empleado>";

        return retorno;
    }

}