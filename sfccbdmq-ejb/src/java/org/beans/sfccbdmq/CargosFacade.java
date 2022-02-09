/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cargos;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
public class CargosFacade extends AbstractFacade<Cargos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CargosFacade() {
        super(Cargos.class);
    }

    @Override
    protected String modificarObjetos(Cargos nuevo) {
        String retorno = "";
        retorno += "<niveleducacion>" + nuevo.getNiveleducacion() + "</niveleducacion>";
//        retorno += "<organigrama>" + nuevo.getOrganigrama() + "</organigrama>";
//        retorno += "<asignacion>" + nuevo.getAsignacion() + "</asignacion>";
        retorno += "<escalasalarial>" + nuevo.getEscalasalarial() + "</escalasalarial>";
        retorno += "<codigoalterno>" + nuevo.getCoodigoalterno()+ "</codigoalterno>";
//        retorno += "<ordinal>" + nuevo.getOrdinal() + "</ordinal>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

    public List<Cargos> listarcargos() {
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) FROM Cargos as o WHERE o.activo=true order by o.nombre asc");
        return q.getResultList();
    }
}
