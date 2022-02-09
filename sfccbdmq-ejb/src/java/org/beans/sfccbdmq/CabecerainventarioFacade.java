/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Kardexinventario;

/**
 *
 * @author edwin
 */
@Stateless
public class CabecerainventarioFacade extends AbstractFacade<Cabecerainventario> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabecerainventarioFacade() {
        super(Cabecerainventario.class);
    }

    @Override
    protected String modificarObjetos(Cabecerainventario nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<txid>" + nuevo.getTxid() + "</txid>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<fechadigitacion>" + nuevo.getFechadigitacion() + "</fechadigitacion>";
        retorno += "<fechacontable>" + nuevo.getFechacontable() + "</fechacontable>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<obligacion>" + nuevo.getObligacion() + "</obligacion>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<bodega>" + nuevo.getBodega() + "</bodega>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<cabecera>" + nuevo.getCabecera() + "</cabecera>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";

        return retorno;
    }

    public String grabarDefinitivo(Cabecerainventario c) {
        Query q = em.createQuery("Select Object(o) From Kardexinventario as o WHERE o.cabecerainventario=:cabecera");
        q.setParameter("cabecera", c);
        List<Kardexinventario> listaKardex = q.getResultList();
        List<Bodegasuministro> listaBxS = new LinkedList<>();
        for (Kardexinventario k : listaKardex) {
            // costear si es ingreso
            if (k.getUnidad() != null) {
                if (k.getUnidad().getBase() != null) {
                    k.setCantidad(k.getCantidad() * k.getUnidad().getFactor());
                    k.setUnidad(k.getUnidad().getBase());
                }
            }

            // buscar en Bodega producto
            q = em.createQuery("Select Object(o) From Bodegasuministro as o "
                    + "Where o.bodega=:bodega and o.suministro=:suministro");
            q.setParameter("bodega", c.getBodega());
            q.setParameter("suministro", k.getSuministro());
            List<Bodegasuministro> listaBodegas = q.getResultList();
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                if (!c.getTxid().getIngreso()) {

                    return "No existe saldo en bodega para poder emitir salida Suministro :" + k.getSuministro().getNombre();
                }
                bs = new Bodegasuministro();
                bs.setBodega(c.getBodega());
                bs.setSuministro(k.getSuministro());
                bs.setSaldo(new Float(0));
                bs.setSaldoinversion(new Float(0));
                bs.setCostopromedio(new Float(0));
                bs.setCostopromedioinversion(new Float(0));
                bs.setFecha(new Date());
                bs.setHora(new Date());
                bs.setUnidad(k.getSuministro().getUnidad());
            }
//                solo si es egreso

            k.setFecha(new Date());
            k.setHora(new Date());

            if (c.getTxid().getIngreso()) {
                // toca costear

            } else {
                // desscuenta de inventario
//                if (k.getCantidad() - bs.getSaldo() > 0) {
//                    if (!c.getTxid().getIngreso()) {
//                        return "No existe stock en bodega para poder sacar : Suministro" + k.getSuministro().getNombre();
//                    }
//                }
            }
            costoPromedior(k, bs, c);
            k.setCabecerainventario(c);
            if (k.getId() == null) {
                em.persist(k);
            } else {
                em.merge(k);

            }
            listaBxS.add(bs);
        }
        for (Bodegasuministro b : listaBxS) {
            if (b.getId() == null) {
                em.persist(b);
            } else {
                em.merge(b);
            }
        }
        c.setEstado(1);
        em.merge(c);

        return null;
    }

    public String grabarDefinitivo(Cabecerainventario c, List<Kardexinventario> listaKardex) {
        List<Bodegasuministro> listaBxS = new LinkedList<>();
        for (Kardexinventario k : listaKardex) {
            // costear si es ingreso
            if (k.getUnidad() != null) {
                if (k.getUnidad().getBase() != null) {
                    k.setCantidad(k.getCantidad() * k.getUnidad().getFactor());
                    k.setUnidad(k.getUnidad().getBase());
                }
            }

            // bsucar en Bodega producto
            Query q = em.createQuery("Select Object(o) From Bodegasuministro as o "
                    + "Where o.bodega=:bodega and o.suministro=:suministro");
            q.setParameter("bodega", c.getBodega());
            q.setParameter("suministro", k.getSuministro());
            List<Bodegasuministro> listaBodegas = q.getResultList();
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
//                if (!c.getTxid().getIngreso()) {
//
//                    return "No existe saldo en bodega para poder emitir salida Suministro : " + k.getSuministro().getNombre();
//                }
                bs = new Bodegasuministro();
                bs.setBodega(c.getBodega());
                bs.setSuministro(k.getSuministro());
                bs.setSaldo(new Float(0));
                bs.setSaldoinversion(new Float(0));
                bs.setCostopromedio(new Float(0));
                bs.setCostopromedioinversion(new Float(0));
                bs.setFecha(new Date());
                bs.setHora(new Date());
                bs.setUnidad(k.getSuministro().getUnidad());
            }
//                solo si es egreso

//            k.setFecha(new Date());
            k.setHora(new Date());

            if (c.getTxid().getIngreso()) {
                // toca costear

            } else {
                // desscuenta de inventario
            }
            costoPromedior(k, bs, c);
            k.setCabecerainventario(c);
            if (k.getId() == null) {
                em.persist(k);
            } else {
                em.merge(k);

            }
            listaBxS.add(bs);
        }
        for (Bodegasuministro b : listaBxS) {
            if (b.getId() == null) {
                em.persist(b);
            } else {
                em.merge(b);
            }
        }
        c.setEstado(1);
        em.merge(c);

        return null;
    }

    public void costoPromedior(Kardexinventario k, Bodegasuministro bs, Cabecerainventario c) {

        float costo = (k.getCosto() == null ? 0 : k.getCosto());
        float costoInversion = (k.getCosto() == null ? 0 : k.getCosto());
        float cantidad = 0;
        float cantidadInversion = 0;
//        float cantidadTotal = 0;
        k.setSaldoanterior(bs.getSaldo());
        k.setSaldoanteriorinversion(bs.getSaldoinversion());
        if (c.getTxid().getIngreso()) {
            cantidad = (bs.getSaldo() == null ? 0 : bs.getSaldo())
                    + (k.getCantidad() == null ? 0 : k.getCantidad());
            cantidadInversion = (bs.getSaldoinversion() == null ? 0 : bs.getSaldoinversion())
                    + (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
//            cantidadTotal = cantidad + cantidadInversion;
            double saldo = (bs.getSaldo() == null ? 0 : bs.getSaldo());
            double saldoInv = (bs.getSaldoinversion() == null ? 0 : bs.getSaldoinversion());
            costo = (float) (((bs.getCostopromedio() == null ? 0 : bs.getCostopromedio())
                    * (saldo)
                    + (k.getCosto() == null ? 0 : k.getCosto())
                    * k.getCantidad().doubleValue())
                    / (cantidad == 0 ? 1 : cantidad));
            bs.setCostopromedio(costo);
            costoInversion = (float) (((bs.getCostopromedioinversion() == null ? 0 : bs.getCostopromedioinversion())
                    * (saldoInv)
                    + (k.getCosto() == null ? 0 : k.getCosto())
                    * k.getCantidadinversion().doubleValue())
                    / (cantidadInversion == 0 ? 1 : cantidadInversion));
            bs.setCostopromedio(costo);
            bs.setCostopromedioinversion(costoInversion);

        } else {
            cantidad = (bs.getSaldo() == null ? 0 : bs.getSaldo())
                    - (k.getCantidad() == null ? 0 : k.getCantidad());
            cantidadInversion = (bs.getSaldoinversion() == null ? 0 : bs.getSaldoinversion())
                    - (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
            costo = (k.getCostopromedio()== null ? 0 : k.getCostopromedio());
            costoInversion = (k.getCostopinversion()== null ? 0 : k.getCostopinversion());
//            k.setCosto(bs.getCostopromedio());
        }
        bs.setSaldo(cantidad);
        bs.setSaldoinversion(cantidadInversion);
        k.setCostopromedio(costo);
        k.setCostopinversion(costoInversion);
    }

    public void ejecutarSaldos() {
        Query q = em.createNativeQuery("SELECT  saldoskardex()");
        Object retorno = q.getSingleResult();
    }

    public void ejecutarSaldosSuministro(List<Kardexinventario> lista) {
        for (Kardexinventario k : lista) {
            if (k.getBodega() != null) {
                if (k.getSuministro() != null) {
                    Query q = em.createNativeQuery("SELECT  saldoskardexsuministro(?1,?2)");
                    q.setParameter(1, k.getSuministro().getId());
                    q.setParameter(2, k.getBodega().getId());
                    Object retorno = q.getSingleResult();
                }
            }
        }
    }
}