/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Contactos;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class ContactosFacade extends AbstractFacade<Contactos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContactosFacade() {
        super(Contactos.class);
    }

    @Override
    protected String modificarObjetos(Contactos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<email>" + nuevo.getEmail() + "</email>";
        retorno += "<telefono>" + nuevo.getTelefono() + "</telefono>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<sexo>" + nuevo.getSexo() + "</sexo>";
        retorno += "<titulo>" + nuevo.getTitulo() + "</titulo>";
        retorno += "<cargo>" + nuevo.getCargo() + "</cargo>";
        retorno += "<empresa>" + nuevo.getEmpresa() + "</empresa>";
        retorno += "<fechan>" + nuevo.getFechan() + "</fechan>";
        retorno += "<apellido>" + nuevo.getApellido() + "</apellido>";
        retorno += "<nombre>" + nuevo.getNombre() + "</nombre>";

        return retorno;
    }

}
