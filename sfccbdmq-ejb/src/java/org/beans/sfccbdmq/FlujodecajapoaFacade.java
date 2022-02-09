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
import org.entidades.sfccbdmq.Flujodecajapoa;

/**
 *
 * @author luis
 */
@Stateless
public class FlujodecajapoaFacade extends AbstractFacade<Flujodecajapoa> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FlujodecajapoaFacade() {
        super(Flujodecajapoa.class);
    }

    public void borrar(int anio) {
        Query q = em.createQuery("Select Object(o) FROM Flujodecajapoa as o where o.proyecto.anio=:anio");
        q.setParameter("anio", anio);
        List<Flujodecajapoa> lista = q.getResultList();
        for (Flujodecajapoa f : lista) {
            em.remove(f);
        }
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaDelete<Flujodecajapoa> c
//                = builder.createCriteriaDelete(Flujodecajapoa.class);
//        Root<Flujodecajapoa> p = c.from(Flujodecajapoa.class);
//        Predicate condition1 = builder.ge(
//                p.get(Flujodecajapoa_ .anio),
//                anio);
//        
//        c.where(condition1);
//        Query query = em.createQuery(c);
//        int rowsAffected = query.executeUpdate();
    }

    @Override
    protected String modificarObjetos(Flujodecajapoa nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<proyecto>" + nuevo.getProyecto() + "</proyecto>";
        retorno += "<partida>" + nuevo.getPartida() + "</partida>";
        retorno += "<fuente>" + nuevo.getFuente() + "</fuente>";
        retorno += "<abril>" + nuevo.getAbril() + "</abril>";
        retorno += "<abrilr>" + nuevo.getAbrilr() + "</abrilr>";
        retorno += "<agosto>" + nuevo.getAgosto() + "</agosto>";
        retorno += "<agostor>" + nuevo.getAgostor() + "</agostor>";
        retorno += "<diciembre>" + nuevo.getDiciembre() + "</diciembre>";
        retorno += "<diciembrer>" + nuevo.getDiciembrer() + "</diciembrer>";
        retorno += "<enero>" + nuevo.getEnero() + "</enero>";
        retorno += "<eneror>" + nuevo.getEneror() + "</eneror>";
        retorno += "<febrero>" + nuevo.getFebrero() + "</febrero>";
        retorno += "<febreror>" + nuevo.getFebreror() + "</febreror>";
        retorno += "<julio>" + nuevo.getJulio() + "</julio>";
        retorno += "<julior>" + nuevo.getJulior() + "</julior>";
        retorno += "<junio>" + nuevo.getJunio() + "</junio>";
        retorno += "<junior>" + nuevo.getJunior() + "</junior>";
        retorno += "<marzo>" + nuevo.getMarzo() + "</marzo>";
        retorno += "<marzor>" + nuevo.getMarzor() + "</marzor>";
        retorno += "<mayo>" + nuevo.getMayo() + "</mayo>";
        retorno += "<mayor>" + nuevo.getMayor() + "</mayor>";
        retorno += "<noviembre>" + nuevo.getNoviembre() + "</noviembre>";
        retorno += "<noviembrer>" + nuevo.getNoviembrer() + "</noviembrer>";
        retorno += "<octubre>" + nuevo.getOctubre() + "</octubre>";
        retorno += "<octubrer>" + nuevo.getOctubrer() + "</octubrer>";
        retorno += "<septiembre>" + nuevo.getSeptiembre() + "</septiembre>";
        retorno += "<septiembrer>" + nuevo.getSeptiembrer() + "</septiembrer>";
        return retorno;
    }

}
