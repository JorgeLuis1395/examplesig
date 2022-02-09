/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Areasseleccion;
import org.entidades.sfccbdmq.Calificacionesempleado;
import org.entidades.sfccbdmq.Postulaciones;
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
public class CalificacionesempleadoFacade extends AbstractFacade<Calificacionesempleado> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalificacionesempleadoFacade() {
        super(Calificacionesempleado.class);
    }

    @Override
    protected String modificarObjetos(Calificacionesempleado nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<postulacion>" + nuevo.getPostulacion() + "</postulacion>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<definitivo>" + nuevo.getDefinitivo() + "</definitivo>";
        retorno += "<area>" + nuevo.getArea() + "</area>";

        return retorno;
    }
//    Verificar si un requerimiento ya esta en la calificiones

    public List<Calificacionesempleado> ValoresRequerimiento(Valoresrequerimientos valor) {
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) FROM Calificacionesempleado as o WHERE  o.valoresrequerimiento=:valor and o.valoresrequerimiento.activo=true");
        q.setParameter("valor", valor);
        return q.getResultList();
    }

    //Exite calificacion para esa area
    public Calificacionesempleado CalificacionesAreaEmpleado(Postulaciones potu, Areasseleccion area) {
        Calificacionesempleado calificacion = new Calificacionesempleado();
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) FROM Calificacionesempleado as o WHERE o.postulacion=:postulacion  and o.area=:area");
        q.setParameter("postulacion", potu);
        q.setParameter("area", area);
        List<Calificacionesempleado> calem = q.getResultList();
        for (Calificacionesempleado cl : calem) {
            calificacion = cl;
        }
        return calificacion;
    }
}
