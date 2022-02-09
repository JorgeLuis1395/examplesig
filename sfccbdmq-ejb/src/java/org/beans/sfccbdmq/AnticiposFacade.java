/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Anticipos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class AnticiposFacade extends AbstractFacade<Anticipos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnticiposFacade() {
        super(Anticipos.class);
    }

    @Override
    protected String modificarObjetos(Anticipos nuevo) {
        String retorno = "";
        retorno += "<fechaemision>" + nuevo.getFechaemision() + "</fechaemision>";
        retorno += "<cc>" + nuevo.getCc() + "</cc>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fechavencimiento>" + nuevo.getFechavencimiento() + "</fechavencimiento>";
        retorno += "<aplicado>" + nuevo.getAplicado() + "</aplicado>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<fechacontable>" + nuevo.getFechacontable() + "</fechacontable>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<pagado>" + nuevo.getPagado() + "</pagado>";
        retorno += "<fechaingreso>" + nuevo.getFechaingreso() + "</fechaingreso>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";

        return retorno;
    }

}
