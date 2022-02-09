/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luis
 */
@Stateless
public class AsignacionespoaFacade extends AbstractFacade<Asignacionespoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AsignacionespoaFacade() {
        super(Asignacionespoa.class);
    }

    public void cerrar(int anio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void cerrar(Integer anio) throws InsertarException {
        try {
            Query q = em.createQuery("Select Object(o) from Asignacionespoa as o where o.proyecto.anio=:anio");
            q.setParameter("anio", anio);
            List<Asignacionespoa> lista = q.getResultList();
            for (Asignacionespoa a : lista) {
                a.setCerrado(Boolean.TRUE);
                em.merge(a);
            }
        } catch (Exception e) {
            throw new InsertarException("CIERRE ASIGNACIONES", e);
        } finally {
            Logger.getLogger("CIERRE ASIGNACIONES").log(Level.INFO, "CIERRE AÑO:{0}", anio);
        }

    }

    @Override
    protected String modificarObjetos(Asignacionespoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<partida>" + nuevo.getPartida() + "</partida>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cerrado>" + nuevo.getCerrado() + "</cerrado>";
        retorno += "<fuente>" + nuevo.getFuente() + "</fuente>";
        retorno += "<pac>" + nuevo.getFuente() + "</pac>";
        retorno += "<valoraux>" + nuevo.getFuente() + "</valoraux>";

        return retorno;
    }

}
