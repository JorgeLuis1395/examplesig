/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.List;
import org.entidades.sfccbdmq.Documentos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Formatos;

/**
 *
 * @author edwin
 */
@Stateless
public class DocumentosFacade extends AbstractFacade<Documentos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentosFacade() {
        super(Documentos.class);
    }

    @Override
    protected String modificarObjetos(Documentos nuevo) {
        String retorno = "";
        retorno += "<punto>" + nuevo.getPunto() + "</punto>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<autorizacion>" + nuevo.getAutorizacion() + "</autorizacion>";
        retorno += "<fechaultimo>" + nuevo.getFechaultimo() + "</fechaultimo>";
        retorno += "<numeroactual>" + nuevo.getNumeroactual() + "</numeroactual>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<inicial>" + nuevo.getInicial() + "</inicial>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<final>" + nuevo.getFinal1() + "</final>";

        return retorno;
    }

    public Formatos traerFormato() {
        Query q = em.createQuery("Select Object(o) From Formatos as o where o.escxp=true");
        List<Formatos> lf = q.getResultList();
        for (Formatos f : lf) {
            return f;
        }
        return null;
    }

}
