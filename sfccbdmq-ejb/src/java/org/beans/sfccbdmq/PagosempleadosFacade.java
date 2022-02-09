/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.entidades.sfccbdmq.Pagosempleados;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Configuracion;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Novedadesxempleado;
import org.entidades.sfccbdmq.Pagosempleados;
//import org.entidades.sfccbdmq.Pagosempleados_;

/**
 *
 * @author edwin
 */
@Stateless
public class PagosempleadosFacade extends AbstractFacade<Pagosempleados> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagosempleadosFacade() {
        super(Pagosempleados.class);
    }

    @Override
    protected String modificarObjetos(Pagosempleados nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<anio>" + nuevo.getAnio() + "</anio>";
        retorno += "<mes>" + nuevo.getMes() + "</mes>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<cantidad>" + nuevo.getCantidad() + "</cantidad>";
        retorno += "<pagado>" + nuevo.getPagado() + "</pagado>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<fechapago>" + nuevo.getFechapago() + "</fechapago>";
        retorno += "<kardexbanco>" + nuevo.getKardexbanco() + "</kardexbanco>";

        return retorno;
    }

/*    public void borrarNomina(int mes, int anio) {
//        em.createQuery(new CriteriaDelete)
//        Query q = em.createQuery("Delete from Pagosempleados as o Where o.anio=:anio and o.mes=:mes "
//                + " ");
//        q.setParameter("mes", mes);
//        q.setParameter("anio", anio);
//        q.executeUpdate();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<Pagosempleados> c
                = builder.createCriteriaDelete(Pagosempleados.class);
        Root<Pagosempleados> p = c.from(Pagosempleados.class);
        Predicate condition1 = builder.ge(
                p.get(Pagosempleados_.anio),
                anio);
        Predicate condition2 = builder.equal(
                p.get(Pagosempleados_.mes),
                mes);
        Predicate condition5 = builder.equal(
                p.get(Pagosempleados_.cargado),
                false);
//        Predicate condition4 = builder.equal(
//                p.get(Pagosempleados_.cargado),
//                null);
        Predicate condition4 = builder.isNull(p.get(Pagosempleados_.cargado));
        Predicate condition3 = builder.or(condition4, condition5);
        c.where(condition1, condition2, condition3);
        Query query = em.createQuery(c);
        int rowsAffected = query.executeUpdate();
//         Predicate condition = criteriaBuilder.equal(from.get("Username"), username);
//        from.get("Username")
    }

    public void borrarNominaDia(int mes, int anio, Date dia) {
//        em.createQuery(new CriteriaDelete)
//        Query q = em.createQuery("Delete from Pagosempleados as o Where o.anio=:anio and o.mes=:mes "
//                + " ");
//        q.setParameter("mes", mes);
//        q.setParameter("anio", anio);
//        q.executeUpdate();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<Pagosempleados> c
                = builder.createCriteriaDelete(Pagosempleados.class);
        Root<Pagosempleados> p = c.from(Pagosempleados.class);
        Predicate condition1 = builder.ge(
                p.get(Pagosempleados_.anio),
                anio);
        Predicate condition2 = builder.equal(
                p.get(Pagosempleados_.mes),
                mes);
        Predicate condition5 = builder.equal(
                p.get(Pagosempleados_.cargado),
                false);
        Predicate condition6 = builder.notEqual(
                p.get(Pagosempleados_.dia),
                dia);
        Predicate condition4 = builder.isNull(p.get(Pagosempleados_.cargado));
        Predicate condition3 = builder.or(condition4, condition5);
        c.where(condition1, condition2, condition3, condition6);
        Query query = em.createQuery(c);
        int rowsAffected = query.executeUpdate();
//         Predicate condition = criteriaBuilder.equal(from.get("Username"), username);
//        from.get("Username")
    }

    public void borrarNominaDia(Date dia) {
//        em.createQuery(new CriteriaDelete)
//        Query q = em.createQuery("Delete from Pagosempleados as o Where o.anio=:anio and o.mes=:mes "
//                + " ");
//        q.setParameter("mes", mes);
//        q.setParameter("anio", anio);
//        q.executeUpdate();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<Pagosempleados> c
                = builder.createCriteriaDelete(Pagosempleados.class);
        Root<Pagosempleados> p = c.from(Pagosempleados.class);
        Predicate condition1 = builder.notEqual(
                p.get(Pagosempleados_.dia),
                dia);

        Predicate condition5 = builder.equal(
                p.get(Pagosempleados_.cargado),
                false);
//        Predicate condition4 = builder.equal(
//                p.get(Pagosempleados_.cargado),
//                null);
        Predicate condition4 = builder.isNull(p.get(Pagosempleados_.cargado));
        Predicate condition3 = builder.or(condition4, condition5);
        c.where(condition1, condition3);
        Query query = em.createQuery(c);
        int rowsAffected = query.executeUpdate();
//         Predicate condition = criteriaBuilder.equal(from.get("Username"), username);
//        from.get("Username")
    }

    public void borrarNomina(int mes, int anio, Conceptos concepto) {
//        em.createQuery(new CriteriaDelete)
//        Query q = em.createQuery("Delete from Pagosempleados as o Where o.anio=:anio and o.mes=:mes "
//                + " ");
//        q.setParameter("mes", mes);
//        q.setParameter("anio", anio);
//        q.executeUpdate();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<Pagosempleados> c
                = builder.createCriteriaDelete(Pagosempleados.class);
        Root<Pagosempleados> p = c.from(Pagosempleados.class);
        Predicate condition1 = builder.ge(
                p.get(Pagosempleados_.anio),
                anio);
        Predicate condition2 = builder.equal(
                p.get(Pagosempleados_.mes),
                mes);
        Predicate condition3 = builder.equal(
                p.get(Pagosempleados_.concepto),
                concepto);
        Predicate condition4 = builder.equal(
                p.get(Pagosempleados_.cargado),
                false);
        c.where(condition1, condition2, condition3, condition4);
        Query query = em.createQuery(c);
        int rowsAffected = query.executeUpdate();
    }
*/
    public void ponerCuentasPago(Conceptos campo, Pagosempleados p, String ctaInicio, String ctaFin, String partida) {
        p.setClasificador(null);
        p.setPagado(Boolean.FALSE);
        if (partida == null) {
            return;
        }
        if (((campo.getPartida() == null) || (campo.getPartida().isEmpty()))) {
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            }
            if (!((campo.getCuenta() == null) || (campo.getCuenta().isEmpty()))) {
                p.setCuentaporpagar(campo.getCuenta());
            }

        } else {
            p.setClasificador(partida + campo.getPartida());
            // buscar cuentas que tienen ese clasificador
            Query q = em.createQuery("Select OBJECT(o) FROM Cuentas as o "
                    + " where o.presupuesto=:presupuesto and o.imputable=true order by o.codigo");
            q.setParameter("presupuesto", partida + campo.getPartida());
            List<Cuentas> lCuentas = q.getResultList();
            String ctaContable = "";
            for (Cuentas cta : lCuentas) {
                ctaContable = cta.getCodigo();
            }
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            } else {
                p.setCuentaporpagar(null);
            }
            p.setCuentagasto(ctaContable);

        }
    }

    public void ponerCuentasConceptoAnt(Conceptos campo, Pagosempleados p, String ctaInicio, String ctaFin, String partida) {
//        p.setClasificador(null);
//        p.setPagado(Boolean.FALSE);
        if (partida == null) {
            return;
        }
        if (((campo.getPartida() == null) || (campo.getPartida().isEmpty()))) {
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            }
            if (!((campo.getCuenta() == null) || (campo.getCuenta().isEmpty()))) {
                p.setCuentaporpagar(campo.getCuenta());

            }

        } else {
//            p.setClasificador(partida + campo.getPartida());
            // buscar cuentas que tienen ese clasificador
            Query q = em.createQuery("Select OBJECT(o) FROM Cuentas as o "
                    + " where o.presupuesto=:presupuesto and o.imputable=true order by o.codigo");
            q.setParameter("presupuesto", partida + campo.getPartida());
            List<Cuentas> lCuentas = q.getResultList();
            String ctaContable = "";
            for (Cuentas cta : lCuentas) {
                ctaContable = cta.getCodigo();
            }
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            } else {
                p.setCuentaporpagar(null);
            }
            p.setCuentagasto(ctaContable);

        }
    }

    public double calculaConcepto(double rmu, Conceptos c, double subrogaciones,
            Empleados e, int diasTrabajados, int diasSurogacion, double ingresos, Date finMes) {
        double valor = 0;
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(finMes);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int anio = calendario.get(Calendar.YEAR);
        Query q = em.createQuery("SELECT OBJECT(o) FROM Configuracion as o");
        List<Configuracion> lconfig = q.getResultList();
        Configuracion conf = new Configuracion();
        for (Configuracion cf : lconfig) {
            conf = cf;
        }

        if (c.getEsporcentaje()) {
            if (null != c.getDeque())//                            tomar el valor
            // de que
            {
                switch (c.getDeque()) {
                    case 0: {
                        //                                return "SMV";
//                    valor = configuracionBean.getConfiguracion().getSmv() * c.getValor() / Constantes.DIAS_POR_MES;
                        double v = (c.getValor() == null ? 0 : c.getValor());
                        valor = conf.getSmv() * v / 100;
//                        valor = Math.rint(valor * 100) / 100;
                        break;
                    }
                    case 1: {
                        //                                return "RMU";
                        float v = (c.getValor() == null ? 0 : c.getValor());
                        valor = (rmu + subrogaciones) * v / 100;
//                        valor = Math.rint(valor * 100) / 100;
//                    valor = (rmu + subrogaciones) * c.getValor() / Constantes.DIAS_POR_MES;
                        break;
                    }
                    case 2: {
                        //                                return "Ingresos";
                        // toca ver despues de que esten todos los ingresos comolo almaceno no se
                        float v = (c.getValor() == null ? 0 : c.getValor());
                        valor = (float) (ingresos) * v / 100;
//                        valor = Math.rint(valor * 100) / 100;
//                        valor = (ingresos) * c.getValor() / Constantes.DIAS_POR_MES;
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        if (c.getEsvalor()) {
            valor = c.getValor();
//            valor = Math.rint(valor * 100) / 100;
        }
        if (!((c.getFormula() == null) || (c.getFormula().isEmpty()))) {
            valor = ejecutaSp(new Float(rmu), new Float(subrogaciones), new Float(ingresos),
                    diasTrabajados, diasSurogacion, e.getId(), c.getCodigo(), finMes);

            BigDecimal bd = new BigDecimal(valor);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            valor = bd.doubleValue();
            //            double cuadre = Math.round(valor * 100);
//            double dividido = cuadre / 100;
//            valor =dividido;

//            valor = Math.rint(valor * 100) / 100;
        }
        // si es formula ejecutar ya vemos luego
        //*****************************************

        // ver si tiene una novedad
        float novedad = 0;
        float unidades = 0;
        if (c.getNovedad()) {
            // tiene novedad hay que traerla puede ser al final
            Query q1 = em.createQuery("SELECT OBJECT(o) "
                    + " FROM Novedadesxempleado as o "
                    + " WHERE o.concepto=:concepto and o.mes=:mes and o.anio=:anio and o.empleado=:empleado");

            q1.setParameter("concepto", c);
            q1.setParameter("empleado", e);
            q1.setParameter("mes", mes);
            q1.setParameter("anio", anio);
            List<Novedadesxempleado> listaNovedades = q1.getResultList();
            for (Novedadesxempleado n : listaNovedades) {
                novedad = n.getValor() == null ? 0 : n.getValor();
                unidades = n.getValor() == null ? 0 : n.getValor();
            }
//                if (novedad != 0) {
            if (valor == 0) {
                valor = novedad;
//                valor = Math.rint(valor * 100) / 100;
            } else {
                if (c.getHoras()) {
                    valor = valor * novedad / (8 * 30);
//                    valor = Math.rint(valor * 100) / 100;
                } else {
                    valor = valor * novedad / 30;
//                    valor = Math.rint(valor * 100) / 100;
                }
            }
//                }
        }

        return valor;
    }

    private float ejecutaSp(float rmu, float subrogacion, float ingresos,
            int diasTrabajados, int diasSubTrabajados,
            Integer idEmpleado, String codigoFormula, Date fechaFormula) {
        Query q = em.createNativeQuery("select " + codigoFormula + "(?1,?2,?3,?4,?5,?6,?7,?8,?9):: numeric(10,2)");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(fechaFormula);
        q.setParameter(1, rmu);
        q.setParameter(2, subrogacion);
        q.setParameter(3, ingresos);
        q.setParameter(4, diasTrabajados);
        q.setParameter(5, diasSubTrabajados);
        q.setParameter(6, idEmpleado);
        q.setParameter(7, c.get(Calendar.DATE));
        q.setParameter(8, c.get(Calendar.MONTH) + 1);
        q.setParameter(9, c.get(Calendar.YEAR));
        Object retorno = q.getSingleResult();
        if (retorno == null) {
            return 0;
        }
        return ((BigDecimal) retorno).floatValue();

//        return 0;
    }

    public void ponerCuentasPago(Conceptos campo, Pagosempleados p, String ctaInicio,
            String ctaFin, String partida, Compromisos compromiso) {
        p.setClasificador(null);
        p.setPagado(Boolean.FALSE);
        if (partida == null) {
            return;
        }
//        if (campo.getId()==32){
//            int x=10;
//        }
        if (((campo.getPartida() == null) || (campo.getPartida().isEmpty()))) {
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            }
            if (!((campo.getCuenta() == null) || (campo.getCuenta().isEmpty()))) {
                p.setCuentaporpagar(campo.getCuenta());

            }

        } else {
            if (campo.getPartida().trim().length() == 6) {
                p.setClasificador(campo.getPartida());
                p.setCuentagasto(campo.getCuenta());
            } else {
                p.setClasificador(partida + campo.getPartida());

                // buscar cuentas que tienen ese clasificador
                Query q = em.createQuery("Select OBJECT(o) FROM Cuentas as o "
                        + " where o.presupuesto=:presupuesto and o.imputable=true");
                String cuenta = p.getClasificador();
                q.setParameter("presupuesto", cuenta.trim());
                List<Cuentas> lCuentas = q.getResultList();
                String ctaContable = "";
                for (Cuentas cta : lCuentas) {
                    ctaContable = cta.getCodigo();
                }

                p.setCuentagasto(ctaContable);
            }
            p.setCompromiso(compromiso);
            p.setProyecto(p.getEmpleado().getProyecto() == null ? "" : p.getEmpleado().getProyecto().getCodigo());
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            } else {
                p.setCuentaporpagar(null);
            }
        }
    }

    public void ponerCuentasConcepto(Conceptos campo, Pagosempleados p, String ctaInicio, String ctaFin, String partida) {
//        p.setClasificador(null);
//        p.setPagado(Boolean.FALSE);
//        if (partida == null) {
//            return;
//        }
//        if (((campo.getPartida() == null) || (campo.getPartida().isEmpty()))) {
//            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
//                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
//            }
//            if (!((campo.getCuenta() == null) || (campo.getCuenta().isEmpty()))) {
//                p.setCuentaporpagar(campo.getCuenta());
//
//            }
//
//        } else {
////            p.setClasificador(partida + campo.getPartida());
//            // buscar cuentas que tienen ese clasificador
//            Query q = em.createQuery("Select OBJECT(o) FROM Cuentas as o "
//                    + " where o.presupuesto=:presupuesto and o.imputable=true order by o.codigo");
//            q.setParameter("presupuesto", partida + campo.getPartida());
//            List<Cuentas> lCuentas = q.getResultList();
//            String ctaContable = "";
//            for (Cuentas cta : lCuentas) {
//                ctaContable = cta.getCodigo();
//            }
//            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
//                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
//            } else {
//                p.setCuentaporpagar(null);
//            }
//            p.setCuentagasto(ctaContable);
//
//        }
        p.setClasificador(null);
        p.setPagado(Boolean.FALSE);
        if (partida == null) {
            return;
        }
//        if (campo.getId()==32){
//            int x=10;
//        }
        if (((campo.getPartida() == null) || (campo.getPartida().isEmpty()))) {
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            } else {
                if (!((campo.getCuenta() == null) || (campo.getCuenta().isEmpty()))) {
                    p.setCuentaporpagar(campo.getCuenta());

                }
            }

        } else {
            if (campo.getPartida().trim().length() == 6) {
                p.setClasificador(campo.getPartida());
                p.setCuentagasto(campo.getCuenta());
            } else {
                p.setClasificador(partida + campo.getPartida());

                // buscar cuentas que tienen ese clasificador
                Query q = em.createQuery("Select OBJECT(o) FROM Cuentas as o "
                        + " where o.presupuesto=:presupuesto and o.imputable=true");
                String cuenta = p.getClasificador();
                q.setParameter("presupuesto", cuenta.trim());
                List<Cuentas> lCuentas = q.getResultList();
                String ctaContable = "";
                for (Cuentas cta : lCuentas) {
                    ctaContable = cta.getCodigo();
                }

                p.setCuentagasto(ctaContable);
            }
            if (!((campo.getCuentaporpagar() == null) || (campo.getCuentaporpagar().isEmpty()))) {
                p.setCuentaporpagar(ctaInicio + partida + campo.getCuentaporpagar());
            } else {
                p.setCuentaporpagar(null);
            }
        }
    }
}
