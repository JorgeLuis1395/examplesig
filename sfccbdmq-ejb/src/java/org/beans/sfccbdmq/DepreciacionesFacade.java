/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import org.entidades.sfccbdmq.Depreciaciones;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author edwin
 */
@Stateless
public class DepreciacionesFacade extends AbstractFacade<Depreciaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DepreciacionesFacade() {
        super(Depreciaciones.class);
    }

    @Override
    protected String modificarObjetos(Depreciaciones nuevo) {
        String retorno = "";
        retorno += "<depreciacion>" + nuevo.getDepreciacion() + "</depreciacion>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<activo>" + nuevo.getActivo() + "</activo>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";

        return retorno;
    }

    public int ultimoMes() throws ConsultarException {
        try {
            Calendar c = Calendar.getInstance();
            javax.persistence.Query q = getEntityManager().createQuery("Select max(o.anio*100+o.mes) From Depreciaciones as o where o.baja is null");
//            javax.persistence.Query q = getEntityManager().createQuery("Select max(o.anio*100+o.mes) From Depreciaciones as o");
//            javax.persistence.Query q = getEntityManager().createQuery("Select max(o.mes) From Depreciaciones as o");
            Integer mesLong = ((Integer) q.getSingleResult());
            int mes = 0;
            if (mesLong != null) {
                mes = mesLong.intValue();
            }
            // deberia pasar la fecha actual
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");
            if (mes==0){
                mes=c.get(Calendar.MONTH);
                int anio=c.get(Calendar.YEAR);
                if (mes==0){
                    mes=12;
                    anio--;
                } 
                mes=anio*100+mes;
            }
//            if (mes == 0) {
//                mes = c.get(Calendar.MONTH) + 1;
//            }
            return mes;
        } catch (Exception e) {
            throw new ConsultarException("MES", e);
        }
    }

    public int ultimoAnio() throws ConsultarException {
        try {
            Calendar c = Calendar.getInstance();
            javax.persistence.Query q = getEntityManager().createQuery("Select max(o.anio) From Depreciaciones as o");
            Integer anioLong = ((Integer) q.getSingleResult());
            int anio = 0;
            if (anioLong != null) {
                anio = anioLong;
            }
//            if (anio == 0) {
//                anio = c.get(Calendar.YEAR);
//            }
            return anio;
        } catch (Exception e) {
            throw new ConsultarException("ANIO", e);
        }
    }
    public String depreciar(List<Depreciaciones> listaDepreciaciones,String anioMes,String usuario){
        for (Depreciaciones d : listaDepreciaciones) {
                em.persist(d);
                // crear el tarcking
//                Trackingactivos tacking = new Trackingactivos();
//                tacking.setActivo(d.getActivo());
//                tacking.setDescripcion("Generación Depreciación :" + anioMes);
//                tacking.setFecha(new Date());
//                tacking.setTipo(8);
//                tacking.setUsuario(usuario);
//                em.persist(tacking);
            }
        return null;
    }
}
