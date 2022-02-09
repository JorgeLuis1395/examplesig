/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Empresas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class EmpresasFacade extends AbstractFacade<Empresas> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpresasFacade() {
        super(Empresas.class);
    }

    @Override
    protected String modificarObjetos(Empresas nuevo) {
        String retorno = "";
        retorno += "<especial>" + nuevo.getEspecial() + "</especial>";
        retorno += "<tipodoc>" + nuevo.getTipodoc() + "</tipodoc>";
        retorno += "<telefono1>" + nuevo.getTelefono1() + "</telefono1>";
        retorno += "<email>" + nuevo.getEmail() + "</email>";
        retorno += "<nombrecomercial>" + nuevo.getNombrecomercial() + "</nombrecomercial>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";
        retorno += "<ruc>" + nuevo.getRuc() + "</ruc>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<telefono2>" + nuevo.getTelefono2() + "</telefono2>";
        retorno += "<web>" + nuevo.getWeb() + "</web>";
        retorno += "<celular>" + nuevo.getCelular() + "</celular>";
        retorno += "<personanatural>" + nuevo.getPersonanatural() + "</personanatural>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";

        return retorno;
    }

}
