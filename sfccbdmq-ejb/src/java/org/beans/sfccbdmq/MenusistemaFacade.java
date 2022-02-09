/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Menusistema;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class MenusistemaFacade extends AbstractFacade<Menusistema> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MenusistemaFacade() {
        super(Menusistema.class);
    }
   

    @Override
    protected String modificarObjetos(Menusistema nuevo) {
        String retorno = "";
        retorno += "<texto>" + nuevo.getTexto() + "</texto>";
        retorno += "<modulo>" + nuevo.getModulo() + "</modulo>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<idpadre>" + nuevo.getIdpadre() + "</idpadre>";
        retorno += "<formulario>" + nuevo.getFormulario() + "</formulario>";
//        log(retorno, "I", "Clase", "xx");
        return retorno;
    }
//    private void log(String objeto, String operacion, String clase, String usuario) {
//        Eventos e=new Eventos();
//        e.setBean(clase);
//        e.setOperacion(operacion);
//        e.setUserid(usuario);
//        e.setObjeto(objeto);
//        e.setFecha(new Date());
//        e.setHora(new Date());
//        ejbLogs.create(e);
//    }

}
