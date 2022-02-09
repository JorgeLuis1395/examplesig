/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cierres;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;

/**
 *
 * @author edwin
 */
@Stateless
public class CabecerasFacade extends AbstractFacade<Cabeceras> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecerasFacade() {
        super(Cabeceras.class);
    }

    @Override
    protected String modificarObjetos(Cabeceras nuevo) {
        String retorno = "";
        retorno += "<idmodulo>" + nuevo.getIdmodulo() + "</idmodulo>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<descripcion>" + nuevo.getDescripcion() + "</descripcion>";
        retorno += "<asientotipo>" + nuevo.getAsientotipo() + "</asientotipo>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<dia>" + nuevo.getDia() + "</dia>";
        retorno += "<modulo>" + nuevo.getModulo() + "</modulo>";
        retorno += "<opcion>" + nuevo.getOpcion() + "</opcion>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<anulado>" + nuevo.getAnulado() + "</anulado>";

        return retorno;
    }

    public void grabarAsiento(Cabeceras cabecera, List<Renglones> renglones, List<Renglones> renglonesb) {
        if (cabecera.getId() == null) {
            Tipoasiento t = cabecera.getTipo();
            Integer n = t.getUltimo() + 1;
            t.setUltimo(n);
            cabecera.setNumero(n);
            em.merge(t);
            em.persist(cabecera);
            for (Renglones r : renglones) {
                r.setCabecera(cabecera);
                r.setFecha(cabecera.getFecha());
                em.persist(r);
            }
        } else {
            em.merge(cabecera);
            for (Renglones r : renglones) {
                r.setCabecera(cabecera);
                r.setFecha(cabecera.getFecha());
                if (r.getId() == null) {

                    em.persist(r);
                } else {
                    em.merge(r);
                }
            }
        }
        if (renglonesb!=null){
            for (Renglones r : renglonesb) {
                if (r.getId()!=null){
                Query   qBorrar=em.createNativeQuery("Delete from Renglones where id=?");
                qBorrar.setParameter("1", r.getId());
                qBorrar.executeUpdate();
                    //em.remove(r);
                }
            }
        }

    }

    public String validarCuadre(List<Renglones> renglones) {
        double total=0;
        for (Renglones r:renglones){
            total+=r.getValor().doubleValue()*r.getSigno().doubleValue();
        }
        int cuadre=(int) total*100;
        if (cuadre !=0){
            return "Asiento descuadrado";
        }
        
        return null;
    }
    public String validarCierre(Date fecha) {
        if (fecha==null){
            return "FECHA INVALIDA";
        }
        Calendar fechaAsiento = Calendar.getInstance();
        fechaAsiento.setTime(fecha);
        int anio = fechaAsiento.get(Calendar.YEAR);
        int mes = fechaAsiento.get(Calendar.MONTH);
        Query q = em.createQuery("SELECT OBJECT(o) FROM Cierres as o WHERE o.anio=:anio");
        q.setParameter("anio", anio);
        List<Cierres> lista = q.getResultList();
        for (Cierres c : lista) {
            switch (mes) {
                case 0:
                    if (c.getEnero()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 1:
                    if (c.getFebrero()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 2:
                    if (c.getMarzo()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 3:
                    if (c.getAbril()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 4:
                    if (c.getMayo()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 5:
                    if (c.getJunio()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 6:
                    if (c.getJulio()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 7:
                    if (c.getAgosto()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 8:
                    if (c.getSeptiembre()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 9:
                    if (c.getOctubre()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 10:
                    if (c.getNoviembre()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                case 11:
                    if (c.getDiciembre()) {
                        return "MES YA CERRADO";
                    } else {
                        return null;
                    }
                default:
                    break;
            }
        }
        
        return "No Existe información de cierre";
    }
}