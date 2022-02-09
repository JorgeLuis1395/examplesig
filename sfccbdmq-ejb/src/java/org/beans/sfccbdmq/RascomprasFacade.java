/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Rascompras;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author edwin
 */
@Stateless
public class RascomprasFacade extends AbstractFacade<Rascompras> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RascomprasFacade() {
        super(Rascompras.class);
    }

    @Override
    protected String modificarObjetos(Rascompras nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<referencia>" + nuevo.getReferencia() + "</referencia>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cba>" + nuevo.getCba() + "</cba>";
        retorno += "<obligacion>" + nuevo.getObligacion() + "</obligacion>";
        retorno += "<cc>" + nuevo.getCc() + "</cc>";
        retorno += "<tipoegreso>" + nuevo.getTipoegreso() + "</tipoegreso>";
        retorno += "<retencion>" + nuevo.getRetencion() + "</retencion>";
        retorno += "<valorret>" + nuevo.getValorret() + "</valorret>";
        retorno += "<impuesto>" + nuevo.getImpuesto() + "</impuesto>";
        retorno += "<anticipo>" + nuevo.getAnticipo() + "</anticipo>";
        retorno += "<idcuenta>" + nuevo.getIdcuenta() + "</idcuenta>";
        retorno += "<bien>" + nuevo.getBien() + "</bien>";
        retorno += "<detallecompromiso>" + nuevo.getDetallecompromiso() + "</detallecompromiso>";
        retorno += "<valorimpuesto>" + nuevo.getValorimpuesto() + "</valorimpuesto>";
        retorno += "<vretimpuesto>" + nuevo.getVretimpuesto() + "</vretimpuesto>";
        retorno += "<retimpuesto>" + nuevo.getRetimpuesto() + "</retimpuesto>";
        retorno += "<notacredito>" + nuevo.getNotacredito() + "</notacredito>";

        return retorno;
    }
    public void ponerPagado(Obligaciones obligacion,Date fecha){
        Query q=em.createQuery("Update Rascompras as o Set o.pagado=:pagado where o.obligacion=:obligacion");
        q.setParameter("obligacion",obligacion);
        q.setParameter("pagado",fecha);
        q.executeUpdate();
    }
    public void quitarPagado(Obligaciones obligacion){
        Query q=em.createQuery("Update Rascompras as o Set o.pagado=null where o.obligacion=:obligacion");
        q.setParameter("obligacion",obligacion);
        q.executeUpdate();
    }
}
