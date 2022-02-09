/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Custodios;

/**
 *
 * @author luis
 */
@Stateless
public class CustodiosFacade extends AbstractFacade<Custodios> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CustodiosFacade() {
        super(Custodios.class);
    }

    @Override
    protected String modificarObjetos(Custodios nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<ciadministrativo>" + nuevo.getCiadministrativo() + "</ciadministrativo>";
        retorno += "<cibien>" + nuevo.getCibien() + "</cibien>";

        return retorno;
    }

    public String traerAdministrativo(String ci) {
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) from Custodios as o where o.cibien=:ci");
        q.setParameter("ci", ci);
        List<Custodios> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0).getCiadministrativo() != null ? lista.get(0).getCiadministrativo() : "";

        }
        return "";
    }

    public boolean esAdministrativo(String ci) {
        Query q = getEntityManager().createQuery("SELECT COUNT(o) from Custodios as o where o.ciadministrativo=:ci");
        q.setParameter("ci", ci);
        return ((Long) q.getSingleResult()) > 0;
    }
}