/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cierres;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Ingresos;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;

/**
 *
 * @author edwin
 */
@Stateless
public class IngresosFacade extends AbstractFacade<Ingresos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IngresosFacade() {
        super(Ingresos.class);
    }

    @Override
    protected String modificarObjetos(Ingresos nuevo) {
        String retorno = "";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<aprobar>" + nuevo.getAprobar() + "</aprobar>";
        retorno += "<contabilizar>" + nuevo.getContabilizar() + "</contabilizar>";
        retorno += "<observaciones>" + nuevo.getObservaciones() + "</observaciones>";
        retorno += "<kardexbanco>" + nuevo.getKardexbanco() + "</kardexbanco>";
        retorno += "<cuenta>" + nuevo.getCuenta() + "</cuenta>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<asigancion>" + nuevo.getAsigancion() + "</asigancion>";
        retorno += "<id>" + nuevo.getId() + "</id>";

        return retorno;
    }

    /**
     *
     * Metodo para contabilizar el asiento
     *
     * @param ingreso
     * @param usuario
     * @param anulado
     * @param modulo
     * @param definitivo
     * @return
     * @throws java.lang.Exception
     */
    public List<Renglones> contabilizar(Ingresos ingreso, int anulado, Codigos modulo, String usuario, boolean definitivo) throws Exception {
        String cuenta;
        // Traer cuenta

        Formatos f = traerFormato();
        List<Renglones> rLista = new LinkedList<>();
        if (f == null) {

//            return "ERROR: No se puede contabilizar: Necesario cxc en formatos";
            return null;
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();

        Cabeceras cas = new Cabeceras();
        Tipoasiento tipo = ingreso.getTipo().getTipoasiento();
        if (tipo == null) {
            return null;
//            return "ERROR: No existe tipo de asiento para este módulo";
        }
        String presupuesto = ingreso.getAsigancion().getClasificador().getCodigo();
        presupuesto = presupuesto.substring(0, tamano);
//        String cuentaPresupuesto = f.getCxpinicio() + presupuesto + f.getCxpfin();
        String cuentaPresupuesto = ingreso.getCliente().getCuentaingresos();
        Cuentas cta=validaCuenta(cuentaPresupuesto);
        Cuentas cta2=validaCuenta(ingreso.getCuenta());
        if (cta==null) {
            return null;
//            return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaPresupuesto;
        }
        if (cta2==null) {
            return null;
//            return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaPresupuesto;
        }
        String concepto = "Ingresos Tesorería - Partida : " + ingreso.getAsigancion().getClasificador()
                + " Fuente : " + ingreso.getAsigancion().getFuente().getNombre()
                + " Proyecto : " + ingreso.getAsigancion().getProyecto();
        cas.setTipo(tipo);
        cas.setFecha(ingreso.getFecha());
        cas.setDia(new Date());
        cas.setIdmodulo(ingreso.getId());
        if (anulado > 0) {
            cas.setDescripcion(ingreso.getObservaciones() + " " + concepto);
        } else {
            cas.setDescripcion("Anular Aisento de " + ingreso.getObservaciones() + " " + concepto);
        }
        cas.setModulo(modulo);
        cas.setUsuario(usuario);
        cas.setOpcion("INGRESOS1");
        cas.setNumero(tipo.getUltimo() + 1);
        if (definitivo) {
            em.persist(cas);
            tipo.setUltimo(tipo.getUltimo() + 1);
            em.merge(tipo);
        }
        Renglones rasCandidato = new Renglones();
        rasCandidato.setCuenta(cuentaPresupuesto);
        double valor = (ingreso.getValor().doubleValue()) * anulado;
        rasCandidato.setValor(new BigDecimal(valor));
        rasCandidato.setReferencia(ingreso.getObservaciones());
        rasCandidato.setPresupuesto(ingreso.getAsigancion().getClasificador().getCodigo());
        rasCandidato.setFecha(ingreso.getFecha());
        rasCandidato.setCabecera(cas);
        if (cta.getAuxiliares()!=null){
            rasCandidato.setAuxiliar(ingreso.getCliente().getEmpresa().getRuc());
        }
        if (cta.getCcosto()!=null){
            rasCandidato.setCentrocosto(ingreso.getAsigancion().getProyecto().getCodigo());
        }
        if (definitivo) {
            em.persist(rasCandidato);
        } else {
            rLista.add(rasCandidato);
        }
        // cuenta dos
        Renglones rasCuenta = new Renglones();
        // armar la cuenta proveedor
       
        rasCuenta.setCuenta(ingreso.getCuenta());
        rasCuenta.setReferencia(ingreso.getObservaciones());
        rasCuenta.setFecha(ingreso.getFecha());
        rasCuenta.setCabecera(cas);
        rasCuenta.setPresupuesto(ingreso.getAsigancion().getClasificador().getCodigo());
        rasCuenta.setValor(new BigDecimal(valor * -1));
        if (cta2.getAuxiliares()!=null){
            rasCandidato.setAuxiliar(ingreso.getCliente().getEmpresa().getRuc());
        }
        if (cta2.getCcosto()!=null){
            rasCandidato.setCentrocosto(ingreso.getAsigancion().getProyecto().getCodigo());
        }
        
         if (definitivo) {
            em.persist(rasCuenta);
        } else {
            rLista.add(rasCuenta);
        }
        
//        
        ingreso.setContabilizar(new Date());
        if (definitivo) {
            ingreso.setCuenta(rasCandidato.getCuenta());
            em.merge(ingreso);
        } 
        return rLista;
//        return tipo.getUltimo().toString();
    }

    public Formatos traerFormato() {
        Query q = em.createQuery("Select Object(o) From Formatos as o where o.escxc=true");
        List<Formatos> lf = q.getResultList();
        for (Formatos f : lf) {
            return f;
        }
        return null;
    }

    private Cuentas validaCuenta(String cuenta) {
        Query q = em.createQuery("Select Object(o) From Cuentas as o Where o.codigo=:codigo");
        q.setParameter("codigo", cuenta);
        List<Cuentas> cl = q.getResultList();
        for (Cuentas c : cl) {
            if (c.getImputable()) {
                return c;
            }
        }
        return null;
    }
    //    *****************************************************+

    public String validarCierre(Date fecha) {
        if (fecha == null) {
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
//    ******************************************************
}
