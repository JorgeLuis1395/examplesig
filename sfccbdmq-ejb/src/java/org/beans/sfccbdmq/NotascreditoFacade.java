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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cierres;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Notascredito;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;

/**
 *
 * @author edwin
 */
@Stateless
public class NotascreditoFacade extends AbstractFacade<Notascredito> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private CuentasFacade ejbCuentas;
    private List<Renglones> ras;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NotascreditoFacade() {
        super(Notascredito.class);
    }

    @Override
    protected String modificarObjetos(Notascredito nuevo) {
        String retorno = "";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<contabilizacion>" + nuevo.getContabilizacion() + "</contabilizacion>";
        retorno += "<punto>" + nuevo.getPunto() + "</punto>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento() + "</establecimiento>";
        retorno += "<numero>" + nuevo.getNumero() + "</numero>";
        retorno += "<valor>" + nuevo.getValor() + "</valor>";
        retorno += "<obligacion>" + nuevo.getObligacion() + "</obligacion>";
        retorno += "<emision>" + nuevo.getEmision() + "</emision>";
        retorno += "<autorizacion>" + nuevo.getAutorizacion() + "</autorizacion>";
        retorno += "<electronico>" + nuevo.getElectronico() + "</electronico>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<fecha>" + nuevo.getFecha() + "</fecha>";

        return retorno;
    }

    /**
     *
     * Metodo para contabilizar el asiento
     *
     * @param nota
     * @param usuario
     * @param anulado
     * @param modulo
     * @return
     * @throws java.lang.Exception
     */
    public String contabilizar(Notascredito nota, String usuario, int anulado, Codigos modulo) throws Exception {
        String cuenta;
        // Traer cuenta
        String cierre=validarCierre(nota.getFecha());
        if (cierre!=null){
            return cierre;
        }
        if (nota.getObligacion() == null) {
            return "ERROR: No se puede contabilizar: Necesario seleccionar una obligación";
        }
        Formatos f = traerFormato();
        if (f == null) {
            return "ERROR: No se puede contabilizar: Necesario cxp en formatos";
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();
        Query q = em.createQuery("Select object(o) from Tipoasiento as o where o.modulo=:modulo");
        q.setParameter("modulo", modulo);
        Cabeceras cas = new Cabeceras();
        Tipoasiento tipo = null;
        List<Tipoasiento> listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipo = t;
        }
        if (tipo == null) {
            return "ERROR: No existe tipo de asiento para este módulo";
        }
        double anticipo = 0;

        q = em.createQuery("Select object(o) from Rascompras as o where o.notacredito=:notacredito order by o.detallecompromiso.id, o.cba asc");
        q.setParameter("notacredito", nota);
        List<Rascompras> rc = q.getResultList();
        double totalObligacion = 0;
        for (Rascompras r : rc) {
            totalObligacion += r.getValor().doubleValue() + r.getValorimpuesto().doubleValue() - r.getValorret().doubleValue() - r.getVretimpuesto().doubleValue();
            double linea = r.getValor().doubleValue() + r.getValorimpuesto().doubleValue() - r.getValorret().doubleValue() - r.getVretimpuesto().doubleValue();
            // incremeta el impuesto
            Detallecompromiso d = r.getDetallecompromiso();
            double saldo = d.getSaldo().doubleValue();
            d.setSaldo(new BigDecimal(saldo - linea));
            em.merge(d);
            // armada la cuenta de proveedor
        }
        ras = new LinkedList<>();
        for (Rascompras r : rc) {

            String presupuesto = r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            presupuesto = presupuesto.substring(0, tamano);
//            totalObligacion += r.getValor().doubleValue();
            String cuentaProveedor = f.getCxpinicio() + presupuesto + f.getCxpfin();
            if (validaCuenta(cuentaProveedor)) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaProveedor;
            }
            Renglones rasCandidato = new Renglones();
            rasCandidato.setCuenta(r.getIdcuenta().getCodigo());
            double valor = (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue()) * anulado;
            rasCandidato.setValor(new BigDecimal(valor));
            rasCandidato.setReferencia(r.getReferencia());
            rasCandidato.setCentrocosto(r.getCc());
            rasCandidato.setFecha(new Date());
//            rasCandidato.setCabecera(cas);
            estaEnRas(rasCandidato);
            // armar cuenta proveedor
            Renglones rasProveedor = new Renglones();
            // armar la cuenta proveedor
            rasProveedor.setCuenta(cuentaProveedor);
            rasProveedor.setReferencia("NC Proveedor :[No. de documento :" + nota.getEstablecimiento() + nota.getPunto() + nota.getNumero() + "], "
                    + "Proveedor[" + nota.getObligacion().getProveedor().getEmpresa().getNombrecomercial() + "]");
            rasProveedor.setFecha(new Date());
            rasProveedor.setCabecera(cas);
            rasProveedor.setAuxiliar(nota.getObligacion().getProveedor().getEmpresa().getRuc());
            double valorProveedor = (valor) * -1;
//            double valorProveedor = (valor + (valor * anticipo / totalObligacion)) * -1;
            rasProveedor.setValor(new BigDecimal(valorProveedor));
            estaEnRas(rasProveedor);
            r.setCuenta(cuentaProveedor);
            em.merge(r);
        }
        cas.setTipo(tipo);
        cas.setFecha(nota.getEmision());
//        cas.setFecha(new Date());
        cas.setDia(new Date());
        cas.setIdmodulo(nota.getId());
        if (anulado > 0) {
            cas.setDescripcion(nota.getConcepto() + " - NC Proveedor : " + nota.getObligacion().getProveedor().
                    getEmpresa().getNombrecomercial() + "");
        } else {
            cas.setDescripcion("Anular Aisento de NC " + nota.getConcepto() + " - Obligación Proveedor : " + nota.getObligacion().getProveedor().
                    getEmpresa().getNombrecomercial() + "");
        }
        cas.setModulo(modulo);
        cas.setUsuario(usuario);
        cas.setOpcion("NOTACREDITO");
        cas.setNumero(tipo.getUltimo() + 1);
        em.persist(cas);
        tipo.setUltimo(tipo.getUltimo() + 1);
        em.merge(tipo);
        // grabar ras
        for (Renglones r : ras) {
            r.setCabecera(cas);
            r.setFecha(nota.getEmision());
            em.persist(r);
        }

        nota.setContabilizacion(new Date());

        em.merge(nota);

        return tipo.getUltimo().toString();
    }

    private boolean estaEnRas(Renglones r1) {
        for (Renglones r : ras) {
            if (r.getCuenta().equals(r1.getCuenta())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        ras.add(r1);
        return false;
    }

    public Formatos traerFormato() {
        Query q = em.createQuery("Select Object(o) From Formatos as o where o.escxp=true");
        List<Formatos> lf = q.getResultList();
        for (Formatos f : lf) {
            return f;
        }
        return null;
    }

    private boolean validaCuenta(String cuenta) {
        Query q = em.createQuery("Select Object(o) From Cuentas as o Where o.codigo=:codigo");
        q.setParameter("codigo", cuenta);
        List<Cuentas> cl = q.getResultList();
        if (cl.isEmpty()) {
            return true;
        }
        for (Cuentas c : cl) {
            if (!c.getImputable()) {
                return true;
            }
        }
        return false;
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
