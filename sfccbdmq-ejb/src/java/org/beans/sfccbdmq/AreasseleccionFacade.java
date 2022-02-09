/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Areasseleccion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edison
 */
@Stateless
public class AreasseleccionFacade extends AbstractFacade<Areasseleccion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AreasseleccionFacade() {
        super(Areasseleccion.class);
    }

//   public List<Calificacionesempleado>  ValoresRequerimiento(Valoresrequerimientos valor) {
//   Query q = getEntityManager().createQuery("SELECT OBJECT(o) FROM Calificacionesempleado as o WHERE  o.valoresrequerimiento=:valor and o.valoresrequerimiento.activo=true");
//   q.setParameter("valor", valor);
//   return q.getResultList();
//   }
    @Override
    protected String modificarObjetos(Areasseleccion nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<ponderacion>" + nuevo.getPonderacion() + "</ponderacion>";
        retorno += "<notamaxima>" + nuevo.getNotamaxima() + "</notamaxima>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<prueba>" + nuevo.getPrueba() + "</prueba>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";

        return retorno;
    }

}
