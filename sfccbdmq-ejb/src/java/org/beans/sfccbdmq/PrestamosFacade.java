/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Prestamos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class PrestamosFacade extends AbstractFacade<Prestamos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PrestamosFacade() {
        super(Prestamos.class);
    }

    @Override
    protected String modificarObjetos(Prestamos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<fechasolicitud>" + nuevo.getFechasolicitud() + "</fechasolicitud>";
        retorno += "<fechaaprobacion>" + nuevo.getFechaaprobacion() + "</fechaaprobacion>";
        retorno += "<tipo>" + nuevo.getTipo()+ "</tipo>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<couta>" + nuevo.getCouta() + "</couta>";
        retorno += "<pagado>" + nuevo.getPagado() + "</pagado>";
        retorno += "<aprobadopor>" + nuevo.getAprobadopor() + "</aprobadopor>";
        retorno += "<aprobado>" + nuevo.getAprobado() + "</aprobado>";

        return retorno;
    }

}
