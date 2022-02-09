/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Solicitudescargo;
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
public class SolicitudescargoFacade extends AbstractFacade<Solicitudescargo> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitudescargoFacade() {
        super(Solicitudescargo.class);
    }

    @Override
    protected String modificarObjetos(Solicitudescargo nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<solicitud>" + nuevo.getSolicitud() + "</solicitud>";
        retorno += "<area>" + nuevo.getArea() + "</area>";
        retorno += "<cargodentrocatalogo>" + nuevo.getCargodentrocatalogo() + "</cargodentrocatalogo>";
        retorno += "<cargovacante>" + nuevo.getCargovacante() + "</cargovacante>";
        retorno += "<motivovacante>" + nuevo.getMotivovacante() + "</motivovacante>";
        retorno += "<fechageneracionvacante>" + nuevo.getFechageneracionvacante() + "</fechageneracionvacante>";
        retorno += "<tipocontrato>" + nuevo.getTipocontrato() + "</tipocontrato>";
        retorno += "<numerovacantes>" + nuevo.getNumerovacantes() + "</numerovacantes>";
        retorno += "<estadocivil>" + nuevo.getEstadocivil() + "</estadocivil>";
        retorno += "<sexorequerido>" + nuevo.getSexorequerido() + "</sexorequerido>";
        retorno += "<disponibilidad>" + nuevo.getDisponibilidad() + "</disponibilidad>";
        retorno += "<horarioentradatrabajo>" + nuevo.getHorarioentradatrabajo() + "</horarioentradatrabajo>";
        retorno += "<horariosalidatrabajo>" + nuevo.getHorariosalidatrabajo() + "</horariosalidatrabajo>";
        retorno += "<diastrabajo>" + nuevo.getDiastrabajo() + "</diastrabajo>";
        retorno += "<lugartrabajo>" + nuevo.getLugartrabajo() + "</lugartrabajo>";
        retorno += "<disponbilidadviajar>" + nuevo.getDisponbilidadviajar() + "</disponbilidadviajar>";
        retorno += "<rangoingreomensual>" + nuevo.getRangoingreomensual() + "</rangoingreomensual>";
        retorno += "<otrosbeneficios>" + nuevo.getOtrosbeneficios() + "</otrosbeneficios>";
        retorno += "<fecharecepcion>" + nuevo.getFecharecepcion() + "</fecharecepcion>";
        retorno += "<fechaenvio>" + nuevo.getFechaenvio() + "</fechaenvio>";
        retorno += "<vistobueno>" + nuevo.getVistobueno() + "</vistobueno>";
        retorno += "<vistobueno2>" + nuevo.getVistobueno2() + "</vistobueno2>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<rangoingreomensual2>" + nuevo.getRangoingreomensual2() + "</rangoingreomensual2>";
        retorno += "<recomendadodepartamento>" + nuevo.getRecomendadodepartamento() + "</recomendadodepartamento>";
        retorno += "<cargosolicitante>" + nuevo.getCargosolicitante() + "</cargosolicitante>";
        retorno += "<vigente>" + nuevo.getVigente() + "</vigente>";
        retorno += "<recomendadopor>" + nuevo.getRecomendadopor() + "</recomendadopor>";

        return retorno;
    }
//Solictudes Vigentes y no borradass

    public List<Solicitudescargo> solicitudesvigentes() {
        Query q = getEntityManager().createQuery("SELECT OBJECT(o) "
                + "FROM  Solicitudescargo as o WHERE o.activo=true and o.vigente=true");
        return q.getResultList();
    }
}
