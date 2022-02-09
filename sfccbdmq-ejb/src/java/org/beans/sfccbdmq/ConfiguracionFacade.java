/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Configuracion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ConfiguracionFacade extends AbstractFacade<Configuracion> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracionFacade() {
        super(Configuracion.class);
    }

    @Override
    protected String modificarObjetos(Configuracion nuevo) {
        String retorno = "";
        retorno += "<tadepreciacion>" + nuevo.getTadepreciacion() + "</tadepreciacion>";
        retorno += "<ctaresultado>" + nuevo.getCtaresultado() + "</ctaresultado>";
        retorno += "<ctareacumulados>" + nuevo.getCtareacumulados() + "</ctareacumulados>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tacierre>" + nuevo.getTacierre() + "</tacierre>";
        retorno += "<taapertura>" + nuevo.getTaapertura() + "</taapertura>";
        retorno += "<ambiente>" + nuevo.getAmbiente() + "</ambiente>";
        retorno += "<direccion>" + nuevo.getDireccion() + "</direccion>";
        retorno += "<especial>" + nuevo.getEspecial() + "</especial>";
        retorno += "<renta>" + nuevo.getRenta() + "</renta>";
        retorno += "<directorio>" + nuevo.getDirectorio() + "</directorio>";
        retorno += "<urlsri>" + nuevo.getUrlsri() + "</urlsri>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<ruc>" + nuevo.getRuc() + "</ruc>";
        retorno += "<pinicial>" + nuevo.getPinicial() + "</pinicial>";
        retorno += "<pfinal>" + nuevo.getPfinal() + "</pfinal>";
        retorno += "<pvigente>" + nuevo.getPvigente() + "</pvigente>";
        retorno += "<clave>" + nuevo.getClave() + "</clave>";
        retorno += "<generando_nomina>" + nuevo.getGenerandoNomina() + "</generando_nomina>";

        return retorno;
    }

}
