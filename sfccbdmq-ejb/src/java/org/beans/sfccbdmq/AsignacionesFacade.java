/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Asignaciones;
import org.errores.sfccbdmq.InsertarException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
public class AsignacionesFacade extends AbstractFacade<Asignaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AsignacionesFacade() {
        super(Asignaciones.class);
    }

    @Override
    protected String modificarObjetos(Asignaciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<clasificador>" + nuevo.getClasificador() + "</clasificador>";
        retorno += "<fuente>" + nuevo.getFuente() + "</fuente>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cerrado>" + nuevo.getCerrado() + "</cerrado>";

        return retorno;
    }

    public void cerrar(Integer anio) throws InsertarException {
        try {
            Query q = em.createQuery("Update Asignaciones as o SET o.cerrado=true WHERE o.proyecto.anio=:anio");
            q.setParameter("anio", anio);
            q.executeUpdate();
        } catch (Exception e) {
            throw new InsertarException("CIERRE ASIGANCIONES", e);
        } finally {
            Logger.getLogger("CIERRE ASIGANCIONES").log(Level.INFO, "CIERRE ANIO:{0}", anio);
        }

    }

}
