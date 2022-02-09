/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Conceptoxrangos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ConceptoxrangosFacade extends AbstractFacade<Conceptoxrangos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConceptoxrangosFacade() {
        super(Conceptoxrangos.class);
    }

    @Override
    protected String modificarObjetos(Conceptoxrangos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<inicial>" + nuevo.getInicial() + "</inicial>";
        retorno += "<final>" + nuevo.getFinal1() + "</final>";
        retorno += "<porcentaje>" + nuevo.getPorcentaje() + "</porcentaje>";

        return retorno;
    }

}
