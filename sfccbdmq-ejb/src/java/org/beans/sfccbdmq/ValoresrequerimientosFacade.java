/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Areasseleccion;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Valoresrequerimientos;
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
public class ValoresrequerimientosFacade extends AbstractFacade<Valoresrequerimientos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValoresrequerimientosFacade() {
        super(Valoresrequerimientos.class);
    }

    @Override
    protected String modificarObjetos(Valoresrequerimientos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<requerimiento>" + nuevo.getRequerimiento() + "</requerimiento>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<area>" + nuevo.getArea() + "</area>";
        retorno += "<comportamiento>" + nuevo.getComportamiento() + "</comportamiento>";

        return retorno;
    }
//Requerimientos Especificos por Cargo

    public List<Valoresrequerimientos> formacionesacademicascargo(Cargos cargo) {
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) FROM Valoresrequerimientos as o WHERE o.cargo=:cargo  and o.activo=true  and o.area is not null order  by o.requerimiento.competencia.nombre asc");
        q.setParameter("cargo", cargo);
        return q.getResultList();
    }

    //Requerimientos Especificos por Area
    public List<Valoresrequerimientos> formacionesacademicasarea(Areasseleccion area, Cargos cargo) {
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) FROM Valoresrequerimientos as o WHERE o.area=:area  and o.cargo=:cargo and o.activo=true  and o.area.activo=true  order  by o.requerimiento.competencia.nombre asc");
        q.setParameter("area", area);
        q.setParameter("cargo", cargo);
        return q.getResultList();
    }
}
