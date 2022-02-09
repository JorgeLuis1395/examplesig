/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Cabeceraempleados;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class CabeceraempleadosFacade extends AbstractFacade<Cabeceraempleados> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabeceraempleadosFacade() {
        super(Cabeceraempleados.class);
    }

    @Override
    protected String modificarObjetos(Cabeceraempleados nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<valortexto>" + nuevo.getValortexto() + "</valortexto>";
        retorno += "<datoimpresion>" + nuevo.getDatoimpresion() + "</datoimpresion>";
        retorno += "<orden>" + nuevo.getOrden() + "</orden>";
        retorno += "<valornumerico>" + nuevo.getValornumerico() + "</valornumerico>";
        retorno += "<grupo>" + nuevo.getGrupo() + "</grupo>";
        retorno += "<riesgo>" + nuevo.getRiesgo() + "</riesgo>";
        retorno += "<tipodato>" + nuevo.getTipodato() + "</tipodato>";
        retorno += "<ayuda>" + nuevo.getAyuda() + "</ayuda>";
        retorno += "<codigo>" + nuevo.getCodigo() + "</codigo>";
        retorno += "<cabecera>" + nuevo.getCabecera() + "</cabecera>";

        return retorno;
    }

}
