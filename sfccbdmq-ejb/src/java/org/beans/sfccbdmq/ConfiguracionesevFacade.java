/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Configuracionesev;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ConfiguracionesevFacade extends AbstractFacade<Configuracionesev> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracionesevFacade() {
        super(Configuracionesev.class);
    }

    @Override
    protected String modificarObjetos(Configuracionesev nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fechainicio>" + nuevo.getFechainicio() + "</fechainicio>";
        retorno += "<fechafin>" + nuevo.getFechafin() + "</fechafin>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<formulario>" + nuevo.getFormulario() + "</formulario>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";

        return retorno;
    }

}
