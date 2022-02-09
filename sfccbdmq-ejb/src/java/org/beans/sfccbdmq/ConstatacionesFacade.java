/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Constataciones;
import org.entidades.sfccbdmq.Empleados;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luis
 */
@Stateless
public class ConstatacionesFacade extends AbstractFacade<Constataciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConstatacionesFacade() {
        super(Constataciones.class);
    }

    @Override
    protected String modificarObjetos(Constataciones nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cicustodio>" + nuevo.getCicustodio()+ "</cicustodio>";
        retorno += "<codigobien>" + nuevo.getCodigobien()+ "</codigobien>";
        retorno += "<codigo>" + nuevo.getCodigo()+ "</codigo>";
        retorno += "<descripcion>" + nuevo.getDescripcion()+ "</descripcion>";
        retorno += "<nroserie>" + nuevo.getNroserie()+ "</nroserie>";
        retorno += "<ubicacion>" + nuevo.getUbicacion()+ "</ubicacion>";
        retorno += "<fechaconstatacion>" + nuevo.getFechaconstatacion()+ "</fechaconstatacion>";
        retorno += "<estadobien>" + nuevo.getEstadobien()+ "</estadobien>";
        retorno += "<constatado>" + nuevo.getConstatado()+ "</constatado>";
        retorno += "<foto>" + nuevo.getFoto()+ "</foto>";
        retorno += "<color>" + nuevo.getColor()+ "</color>";
        retorno += "<observacion>" + nuevo.getObservacion()+ "</observacion>";
        retorno += "<estado>" + nuevo.getEstado()+ "</estado>";
        retorno += "<anioconstatacion>" + nuevo.getAnioconstatacion()+ "</anioconstatacion>";

        return retorno;
    }
    
    public void finalizar(String cicustodio) throws InsertarException {
        try {
            Query q = em.createQuery("Update Constataciones as o SET o.estado=true WHERE o.cicustodio=:cicustodio");
            q.setParameter("cicustodio", cicustodio);
            q.executeUpdate();
        } catch (Exception e) {
            throw new InsertarException("FINALIZAR CONSTATACIONES", e);
        } finally {
            Logger.getLogger("FINALIZAR CONSTATACIONES").log(Level.INFO, "FINALIZAR CONSTATACIÓN:{0}", cicustodio);
        }

    }
    
}