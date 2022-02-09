/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import org.entidades.sfccbdmq.Licencias;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Empleados;

/**
 *
 * @author edwin
 */
@Stateless
public class LicenciasFacade extends AbstractFacade<Licencias> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    static private int MINUTOS_24_HORAS = 86400000;
    static private int MINUTOS_8_HORAS = 28800000;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicenciasFacade() {
        super(Licencias.class);
    }

    @Override
    protected String modificarObjetos(Licencias nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<empleado>" + nuevo.getEmpleado() + "</empleado>";
        retorno += "<inicio>" + nuevo.getInicio() + "</inicio>";
        retorno += "<fin>" + nuevo.getFin() + "</fin>";
        retorno += "<desde>" + nuevo.getDesde() + "</desde>";
        retorno += "<hasta>" + nuevo.getHasta() + "</hasta>";
        retorno += "<autoriza>" + nuevo.getAutoriza() + "</autoriza>";
        retorno += "<valida>" + nuevo.getValida() + "</valida>";
        retorno += "<solicitud>" + nuevo.getSolicitud() + "</solicitud>";
        retorno += "<fautoriza>" + nuevo.getFautoriza() + "</fautoriza>";
        retorno += "<fvalida>" + nuevo.getFvalida() + "</fvalida>";
        retorno += "<aprovado>" + nuevo.getAprovado() + "</aprovado>";

        return retorno;
    }

    public BigDecimal factor(Licencias licencia, Date periodoInicial, Date periodoFinal) {
        Empleados e = licencia.getEmpleado();
        if (licencia == null) {
            return new BigDecimal(BigInteger.ONE);
        }
        if (licencia.getTipo() == null) {
            return new BigDecimal(BigInteger.ONE);
        }
        if (licencia.getTipo().getTipo() == 2) {
            // vacaciones
            // ver si la diferencia de tiempo es de un dia
            Calendar desde = Calendar.getInstance();
            Calendar hasta = Calendar.getInstance();
            desde.setTime(licencia.getInicio());
            hasta.setTime(licencia.getFin());
            int diaDesde = desde.get(Calendar.DATE);
            int mesDesde = desde.get(Calendar.MONTH);
            int diaHasta = hasta.get(Calendar.DATE);
            int mesHasta = hasta.get(Calendar.MONTH);
            if (mesDesde == mesHasta) {
                if (diaDesde == diaHasta) {
                    if (e.getCargoactual().getOperativo()) {
                        // contar los permisos con cargo a vacaciones
                        // esta puesto que sea solo los que no non por horas
                        Query q = em.createQuery("Select count(o) From Licencias as o "
                                + "WHERE o.empleado=:empleado and o.inicio between :desde and :hasta"
                                + " and o.tipo.tipo=2 and "
                                + " o.inicio=o.fin"
                                + " and o.autoriza is not null "
                                + " and o.aprovado=true");
                        q.setParameter("empleado", e);
                        q.setParameter("desde", periodoInicial);
                        q.setParameter("hasta", periodoFinal);
                        int contar = ((Long) q.getSingleResult()).intValue();
                        if (contar > 2) {
                            return e.getFactor();
                        }
                    }
                }
            }

        }
        return new BigDecimal(BigInteger.ONE);
    }

//    public void legalizar(Licencias l) {
//        Calendar desde = Calendar.getInstance();
//        desde.setTime(l.getInicio());
//        int mes = desde.get(Calendar.MONTH) + 1;
//        int anio = desde.get(Calendar.YEAR);
//        long tiempo = 0;
//        if ((l.getTipo().getTipo() == 1) || (l.getTipo().getTipo() == 0)) {
//            // permisos
//            // si es horas
//            if (l.getTipo().getCargovacaciones()) {
//                BigDecimal factorEncontrado = factor(l.getEmpleado());
//                double factor = 1;
////                if (factorEncontrado != null) {
////                    factor = factorEncontrado.doubleValue();
////                }
//                if (l.getTipo().getHoras()) {
//                    l.setFretorno(l.getHasta());
//                    tiempo = ((l.getHasta().getTime() - l.getDesde().getTime()) / 60000);
//                    if (l.getTiempolounch() != null) {
//                        tiempo -= l.getTiempolounch();
//                    }
//
//                } else {
//                    l.setFretorno(l.getFin());
//                    tiempo = ((l.getFin().getTime() - l.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
//                    // ver festivos de los extremos
//                    int extremos = extremos(l.getInicio(), l.getFin());
//                    extremos += extremos(l.getFin(), l.getInicio());
//                    tiempo -= extremos;
//                }
//                tiempo *= factor;
//            }
//        } else {
//            // vacaciones
//            l.setFretorno(l.getFin());
//            tiempo = ((l.getFin().getTime() - l.getInicio().getTime()) / (60000 * 60 * 24)) + 1;
//            int extremos = extremos(l.getInicio(), l.getFin());
//            extremos += extremos(l.getFin(), l.getInicio());
//            tiempo -= extremos;
//        }
//        if (tiempo > 0) {
//            // ver el 40 60
//            // traer el registro de vacaciones de este mes
//            Query q1 = em.createQuery("Select Object(o) "
//                    + " FROM Vaccaciones as o "
//                    + " where o.anio =:anio and o.mes=:mes "
//                    + " and o.empleado=:empleado");
//            q1.setParameter("mes", mes);
//            q1.setParameter("anio", anio);
//            q1.setParameter("empleado", l.getEmpleado());
//            List<Vaccaciones> listaVacaciones = q1.getResultList();
//
//            if ((listaVacaciones == null) || (listaVacaciones.isEmpty())) {
//                String valor60 = String.valueOf(tiempo * 8 * 60);
//                String valor40 = String.valueOf(tiempo * 8 * 40);
//                Vaccaciones v = new Vaccaciones();
//                v.setAnio(anio);
//                v.setMes(mes);
//                v.setEmpleado(l.getEmpleado());
//                v.setGanado(0);
//                v.setGanadofs(0);
//                v.setUtilizado(new Integer(valor60));
//                v.setUtilizadofs(new Integer(valor40));
//                em.persist(v);
//            } else {
//                Vaccaciones v = listaVacaciones.get(0);
//                String valor60 = String.valueOf(tiempo * 8 * 60 + v.getUtilizado());
//                String valor40 = String.valueOf(tiempo * 8 * 40 + v.getUtilizadofs());
//                v.setUtilizado(new Integer(valor60));
//                v.setUtilizadofs(new Integer(valor40));
//                em.merge(v);
//            }
//
//            // generar la acción de personal
//            if (!l.getTipo().getHoras()) {
//                int tiempoAccion = l.getTipo().getTiempoaccion() == null ? 0 : l.getTipo().getTiempoaccion();
//                if (tiempo >= tiempoAccion) {
//                    // crear la acción de personal
//                    if (l.getTipo().getAccion() != null) {
//                        Historialcargos hc = new Historialcargos();
//                        hc.setActivo(Boolean.TRUE);
//                        hc.setAprobacion(Boolean.TRUE);
//                        hc.setCargo(l.getEmpleado().getCargoactual());
//                        hc.setDesde(l.getInicio());
//                        hc.setHasta(l.getFin());
//                        hc.setMotivo(l.getTipo().getNombre());
//                        hc.setFecha(l.getInicio());
//                        hc.setSueldobase(l.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase());
//                        hc.setEmpleado(l.getEmpleado());
//                        hc.setPartidaindividual(l.getEmpleado().getPartidaindividual());
//                        hc.setTipoacciones(l.getTipo().getAccion());
//                        hc.setTipocontrato(l.getEmpleado().getTipocontrato());
//                        hc.setUsuario("PROCESO AUTOMATICO");
//                        hc.setVigente(Boolean.TRUE);
//                        em.persist(hc);
//                        l.setAccion(hc);
//                        em.merge(l);
//                    }
//                }
//            }
//        }
//    }
    private int extremos(Date desde, Date hasta) {
        int dias = (int) ((desde.getTime() - hasta.getTime()) / (1000 * 60 * 60 * 24));
        Calendar fecha = Calendar.getInstance();
        int retorno = 0;
        if (dias > 0) {
            fecha.setTime(desde);
            for (int i = 0; i <= dias; i++) {
                Query q2 = em.createQuery("Select COUNT(o) "
                        + " From  Festivos as o Where o.fecha =:fecha "
                        + " ");

                q2.setParameter("fecha", fecha.getTime());
                int cuanto = ((Long) q2.getSingleResult()).intValue();
                if (cuanto == 0) {
                    return retorno;
                }
                retorno++;
            }
        } else {
            fecha.setTime(hasta);
            dias *= -1;
            for (int i = dias; i == 0; i--) {
                Query q2 = em.createQuery("Select COUNT(o) "
                        + " From  Festivos as o Where o.fecha =:fecha "
                        + " ");

                q2.setParameter("fecha", fecha.getTime());
                int cuanto = ((Long) q2.getSingleResult()).intValue();
                if (cuanto == 0) {
                    return retorno;
                }
                retorno++;
            }
        }
        return retorno;
    }

    // calculo de la licencia
    // desde y hasta son horas
//     inicio y fin son fecha
    public int contarVacaciones(Licencias licencia) {
        Calendar primeroEnero = Calendar.getInstance();
        Calendar treintayUnoDiciembre = Calendar.getInstance();
        primeroEnero.set(primeroEnero.get(Calendar.YEAR), 0, 1);
        treintayUnoDiciembre.setTime(licencia.getFin());
//        treintayUnoDiciembre.set(primeroEnero.get(Calendar.YEAR), 11, 31);
        // se rforma para contar hasta la fecha
//        treintayUnoDiciembre.set(primeroEnero.get(Calendar.YEAR), 11, 31);
        Query q = em.createQuery("SELECT COUNT(o) FROM Licencias as o "
                + " WHERE o.inicio=o.fin and o.tipo.tipo=2 "
                + " and o.flegaliza is not null and o.cuanto>0 "
                + " and o.empleado=:empleado "
                + " and o.inicio between :desde and :hasta and o.id != :id");
        q.setParameter("empleado", licencia.getEmpleado());
        q.setParameter("desde", primeroEnero.getTime());
        q.setParameter("id", licencia.getId());
        q.setParameter("hasta", treintayUnoDiciembre.getTime());

        return ((Long) q.getSingleResult()).intValue();
    }

    public int calculaTiempo(Licencias licencia) {
        // ver el tipo
        System.out.println("Calula Tiempo " + licencia.getId());
        Empleados e = licencia.getEmpleado();
        
        // Delia pide que el factor se tome encuenta solo cuando es 3
        double factorEmpleado = (e.getFactor() == null ? 1 : (e.getFactor().doubleValue() == 3 ? 3 : 1));
        if (!((licencia.getCantidad()==null) || (licencia.getCantidad()==0))){
            factorEmpleado=licencia.getCantidad();
        }
        boolean operativo = e.getOperativo();
        int factor = 0;
        int diferenciaMinutos = 0;
        if (licencia.getTipo().getTipo() == 1) {
            if (licencia.getTipo().getCargovacaciones()) {
//            se habla de permisos
                long fin = licencia.getFin().getTime();
                long inicio = licencia.getInicio().getTime();
                long diferencia = ((fin - inicio) / MINUTOS_24_HORAS);
                int diferenciaDias = (int) (diferencia);
                if (diferenciaDias == 0) {
//                es solo del día
                    fin = licencia.getHasta().getTime();
                    inicio = licencia.getDesde().getTime();
                    double diferencia1 = (fin - inicio) / (1000 * 60 * factorEmpleado);
                    diferenciaMinutos = (int) (diferencia1);
                } else {
                    
                    Calendar horaInicial = Calendar.getInstance();
                    horaInicial.setTime(licencia.getDesde());
                    Calendar horaFinal = Calendar.getInstance();
                    horaFinal.setTime(licencia.getHasta());
                    Calendar fechaInicial = Calendar.getInstance();
                    fechaInicial.setTime(licencia.getInicio());
                    Calendar fechaFinal = Calendar.getInstance();
                    fechaFinal.setTime(licencia.getFin());
                    fechaFinal.set(Calendar.HOUR_OF_DAY, horaFinal.get(Calendar.HOUR_OF_DAY));
                    fechaFinal.set(Calendar.MINUTE, horaFinal.get(Calendar.MINUTE));
                    fechaInicial.set(Calendar.HOUR_OF_DAY, horaInicial.get(Calendar.HOUR_OF_DAY));
                    fechaInicial.set(Calendar.MINUTE, horaInicial.get(Calendar.MINUTE));
                    // resto las fechas y tengo ya el valor a colocar
                    fin = fechaFinal.getTimeInMillis();
                    inicio = fechaInicial.getTimeInMillis();
                    double diferencia1 = ((fin - inicio) / (60 * 1000*factorEmpleado)) ;
                    diferenciaMinutos = (int) (diferencia1);

                } // fin diferencia dias
            }
        } else if (licencia.getTipo().getTipo() == 2) {
//            vacaciones
//            solo se ven inicio y fin
            long fin = licencia.getFin().getTime();
            long inicio = licencia.getInicio().getTime();
            long diferencia = ((fin - inicio) / MINUTOS_24_HORAS) + 1;
            int diferenciaDias = (int) (diferencia);
            if (diferenciaDias == 1) {
                // un dia
                if (operativo) {
                    // son 3 días
                    int cuantas = contarVacaciones(licencia);
                    if (cuantas >= 2) {
                        diferenciaDias *= factorEmpleado;
//                        factor = 3;
                    }
                }

            }// fin if dias
            diferenciaMinutos = diferenciaDias * 8 * 60;
        } // fin vacaciones
        licencia.setCuanto(diferenciaMinutos);
        licencia.setCantidad((int) factorEmpleado);
        em.merge(licencia);
        System.out.println("Fin Calula Tiempo cambio a " + diferenciaMinutos);
        return diferenciaMinutos;
    }
    public int calculaTiempoNuevo(Licencias licencia) {
        // ver el tipo
        Empleados e = licencia.getEmpleado();
        System.out.println("Calula Tiempo " + licencia.getId());
        // Delia pide que el factor se tome encuenta solo cuando es 3
        double factorEmpleado = licencia.getCantidad();
        boolean operativo = e.getOperativo();
        int factor = 0;
        int diferenciaMinutos = 0;
        if (licencia.getTipo().getTipo() == 1) {
            if (licencia.getTipo().getCargovacaciones()) {
//            se habla de permisos
                long fin = licencia.getFin().getTime();
                long inicio = licencia.getInicio().getTime();
                long diferencia = ((fin - inicio) / MINUTOS_24_HORAS);
                int diferenciaDias = (int) (diferencia);
                if (diferenciaDias == 0) {
//                es solo del día
                    fin = licencia.getHasta().getTime();
                    inicio = licencia.getDesde().getTime();
                    double diferencia1 = (fin - inicio) / (1000 * 60 * factorEmpleado);
                    diferenciaMinutos = (int) (diferencia1);
                } else {
                    Calendar horaInicial = Calendar.getInstance();
                    horaInicial.setTime(licencia.getInicio());
                    Calendar horaFinal = Calendar.getInstance();
                    horaFinal.setTime(licencia.getFin());
                    Calendar fechaInicial = Calendar.getInstance();
                    fechaInicial.setTime(licencia.getDesde());
                    Calendar fechaFinal = Calendar.getInstance();
                    fechaFinal.setTime(licencia.getHasta());
                    fechaFinal.set(Calendar.HOUR_OF_DAY, horaFinal.get(Calendar.HOUR_OF_DAY));
                    fechaFinal.set(Calendar.MINUTE, horaFinal.get(Calendar.MINUTE));
                    fechaInicial.set(Calendar.HOUR_OF_DAY, horaInicial.get(Calendar.HOUR_OF_DAY));
                    fechaInicial.set(Calendar.MINUTE, horaInicial.get(Calendar.MINUTE));
                    // resto las fechas y tengo ya el valor a colocar
                    fin = fechaFinal.getTimeInMillis();
                    inicio = fechaInicial.getTimeInMillis();
                    diferencia = ((fin - inicio) / (60 * 1000)) + 1;
                    diferenciaMinutos = (int) (diferencia);

                } // fin diferencia dias
            }
        } else if (licencia.getTipo().getTipo() == 2) {
//            vacaciones
//            solo se ven inicio y fin
            long fin = licencia.getFin().getTime();
            long inicio = licencia.getInicio().getTime();
            long diferencia = ((fin - inicio) / MINUTOS_24_HORAS) + 1;
            int diferenciaDias = (int) (diferencia);
            if (diferenciaDias == 1) {
                // un dia
                if (operativo) {
                    // son 3 días
                    int cuantas = contarVacaciones(licencia);
                    if (cuantas >= 3) {
                        diferenciaDias *= factorEmpleado;
//                        factor = 3;
                    }
                }

            }// fin if dias
            diferenciaMinutos = diferenciaDias * 8 * 60;
        } // fin vacaciones
        licencia.setCuanto(diferenciaMinutos);
//        licencia.setCantidad((int) factorEmpleado);
        em.merge(licencia);
        System.out.println("Fin Calula Tiempo cambio a " + diferenciaMinutos);
        return diferenciaMinutos;
    }
}