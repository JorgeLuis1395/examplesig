/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.beans.sfccbdmq;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.delectronicos.sfccbdmq.Factura;
import org.delectronicos.sfccbdmq.Impuesto;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cierres;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Descuentoscompras;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallescompras;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Informes;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;
import org.utilitarios.sfccbdmq.FacturaSri;

/**
 *
 * @author edwin
 */
@Stateless
public class DocumentoselectronicosFacade extends AbstractFacade<Documentoselectronicos> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private CabecerainventarioFacade ejbCabInv;
    private List<Renglones> ras;
    private List<Renglones> rasAnticipo;
    private List<Renglones> rasInversiones;
    private List<Renglones> rasMultas;
    private List<Renglones> rasDescuento;
    private List<Renglones> rasExterior;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DocumentoselectronicosFacade() {
        super(Documentoselectronicos.class);
    }

    @Override
    protected String modificarObjetos(Documentoselectronicos nuevo) {
        String retorno = "";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<ruc>" + nuevo.getRuc() + "</ruc>";
        retorno += "<clave>" + nuevo.getClave() + "</clave>";
        retorno += "<fechaemision>" + nuevo.getFechaemision() + "</fechaemision>";
        retorno += "<tipo>" + nuevo.getTipo() + "</tipo>";
        retorno += "<utilizada>" + nuevo.getUtilizada() + "</utilizada>";
        retorno += "<valortotal>" + nuevo.getValortotal() + "</valortotal>";
        return retorno;
    }

    public Codigos traerCodigo(String maestro, String codigo) {
        try {
            return ejbCodigos.traerCodigo(maestro, codigo);
        } catch (ConsultarException ex) {
            Logger.getLogger("CODIGPOS").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void inversiones(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia("UNO");
//            rasInvInt.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
//                rasInvInt.setDetallecompromiso(ras.getDetallecompromiso());
            }
            rasInvInt.setFecha(new Date());
            estaEnRasInversiones(rasInvInt);
            Renglones rasContrapate = new Renglones();
//            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia("DOS");
//            rasContrapate.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasContrapate.setFecha(new Date());

            Cuentas ctaNif = validaCuenta(cuenta.getCodigonif());
            if (ctaNif == null) {
                rasContrapate.setCuenta(ras.getCuenta());
            } else {
                if (!((ctaNif.getCodigonif() == null) || (ctaNif.getCodigonif().isEmpty()))) {
                    rasContrapate.setCuenta(ctaNif.getCodigonif());
                } else {
                    rasContrapate.setCuenta(ras.getCuenta());
                }
            }
            estaEnRasInversiones(rasContrapate);
        }
    }

    /**
     *
     * Metodo para contabilizar el asiento
     *
     * @param listadoDocumentos
     * @param cabeceraDocumentos
     * @param anulado
     * @param modulo
     * @return
     * @throws java.lang.Exception
     */
    public Map preContabilizar(List<Documentoselectronicos> listadoDocumentos,
            int anulado, Codigos modulo, Cabdocelect cabeceraDocumentos
    ) throws Exception {

        String cuenta;
        Formatos f = traerFormato();
        Codigos codigoReclasificacion = traerCodigo("TIPREC", "RET");
        Codigos codigoReclasificacionInv = traerCodigo("TIPREC", "INVER");

        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();
        Query q = em.createQuery("Select object(o) from Tipoasiento as o where o.modulo=:modulo");
        q.setParameter("modulo", modulo);
        Cabeceras cas = new Cabeceras();
        Tipoasiento tipo = null;
        Tipoasiento tipoReclasificacion = null;
        List<Tipoasiento> listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipo = t;
        }

        q = em.createQuery("Select object(o) from Tipoasiento as o where o.codigo=:codigo");
        q.setParameter("codigo", codigoReclasificacionInv.getNombre());
        listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipoReclasificacion = t;
        }
        Query qDetallePresupuesto = em.createQuery("Select OBJECT(o) FROM Detallescompras as o WHERE o.cabeceradoc=:cabecera");
        qDetallePresupuesto.setParameter("cabecera", cabeceraDocumentos);
        List<Detallescompras> listaDetallesCompras = qDetallePresupuesto.getResultList();
//        Proveedores proveedor = cabeceraDocumentos.getCompromiso().getProveedor();
        String ruc = "";
        if (cabeceraDocumentos.getCompromiso().getProveedor() == null) {
            if (cabeceraDocumentos.getCompromiso().getBeneficiario() != null) {
                ruc = cabeceraDocumentos.getCompromiso().getBeneficiario().getEntidad().getPin();
            }

        } else {
            ruc = cabeceraDocumentos.getCompromiso().getProveedor().getEmpresa().getRuc();
        }
        ras = new LinkedList<>();
        setRasInversiones(new LinkedList<>());
        rasAnticipo = new LinkedList<>();
        rasDescuento = new LinkedList<>();
        rasMultas = new LinkedList<>();
        rasExterior = new LinkedList<>();
        String ctaProveedor = "";
        int fondoid = 0;
        for (Detallescompras d : listaDetallesCompras) {
            String presupuesto = d.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            presupuesto = presupuesto.substring(0, tamano);
            String cuentaProveedor = f.getCxpinicio() + presupuesto + f.getCxpfin();
            ctaProveedor = cuentaProveedor;
            Cuentas cuentaValidar = validaCuenta(cuentaProveedor);
            if (d.getValor().doubleValue() != 0) {
                Renglones rasCandidato = new Renglones();
                rasCandidato.setCuenta(d.getCuenta().getCodigo());
                double valor = (d.getValor().doubleValue()) * anulado;
                rasCandidato.setValor(new BigDecimal(valor));
                rasCandidato.setReferencia(cabeceraDocumentos.getObservaciones());
                rasCandidato.setDetallecompromiso(d.getDetallecompromiso());
                rasCandidato.setCentrocosto(d.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());
                rasCandidato.setFecha(cabeceraDocumentos.getFecha());
                if (d.getCuenta().getAuxiliares() != null) {
                    rasCandidato.setAuxiliar(ruc);
                }
                inversiones(d.getCuenta(), rasCandidato, anulado);
                estaEnRas(rasCandidato);

                Renglones rasProveedor = new Renglones();
                rasProveedor.setCuenta(cuentaProveedor);
                rasProveedor.setReferencia(cabeceraDocumentos.getObservaciones());
                rasProveedor.setFecha(cabeceraDocumentos.getFecha());
                rasProveedor.setCabecera(cas);
//            rasProveedor.setCentrocosto(d.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());
//            rasProveedor.setDetallecompromiso(d.getDetallecompromiso());
                rasProveedor.setAuxiliar(ruc);
                double valorProveedor = (valor) * -1;
                rasProveedor.setValor(new BigDecimal(valorProveedor));
                estaEnRas(rasProveedor);
                inversiones(cuentaValidar, rasProveedor, anulado);
            }
        }
        Date fechaMasGrande = null;
        for (Documentoselectronicos d : listadoDocumentos) {
            Obligaciones obligacion = d.getObligacion();
            if (fechaMasGrande == null) {
                fechaMasGrande = obligacion.getFechaemision();
            } else {
                if (fechaMasGrande.getTime() < obligacion.getFechaemision().getTime()) {
                    fechaMasGrande = obligacion.getFechaemision();
                }
            }
            q = em.createQuery("Select object(o) from Retencionescompras as o WHERE o.obligacion=:obligacion");
            q.setParameter("obligacion", obligacion);
            List<Retencionescompras> listaRetenciones = q.getResultList();
            // descuentos
            q = em.createQuery("Select object(o) from Descuentoscompras as o WHERE o.obligacion=:obligacion");
            q.setParameter("obligacion", obligacion);
            List<Descuentoscompras> listaDiscuentos = q.getResultList();
            double valor = 0;

            for (Retencionescompras r : listaRetenciones) {
                // proveedor
                String cuentaProveedor = f.getCxpinicio() + r.getPartida() + f.getCxpfin();
                Cuentas ctaproveedor = validaCuenta(cuentaProveedor);
                if (r.getRetencion() != null) {
                    double valorRetencion = r.getValor().doubleValue() * anulado;

                    ctaProveedor = cuentaProveedor;
                    Renglones rasProveedor = new Renglones();
                    // armar la cuenta proveedor

                    rasProveedor.setCuenta(cuentaProveedor);
                    rasProveedor.setReferencia(obligacion.getConcepto());
                    rasProveedor.setFecha(obligacion.getFechaemision());
                    rasProveedor.setCabecera(cas);
                    rasProveedor.setDetallecompromiso(null);
                    rasProveedor.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
                    double valorProveedor = (valorRetencion);
                    rasProveedor.setValor(new BigDecimal(valorProveedor));
                    // 
                    estaEnRas(rasProveedor);
                    inversiones(ctaproveedor, rasProveedor, anulado);
                    // retenciones
                    String ctaRetencion = f.getCxpinicio() + r.getPartida()
                            + r.getRetencion().getCuenta() + r.getRetencion().getCodigo();
                    Cuentas cuentaValidar = validaCuenta(ctaRetencion);
                    Renglones rasRetencion = new Renglones();
                    // armar la cuenta
                    rasRetencion.setCuenta(ctaRetencion);
                    rasRetencion.setValor(new BigDecimal(valorRetencion * -1));
                    rasRetencion.setReferencia(obligacion.getConcepto());
                    if (cuentaValidar.getAuxiliares() != null) {
                        rasRetencion.setAuxiliar(ruc);
                    }
                    rasRetencion.setFecha(obligacion.getFechaemision());
                    valor += valorRetencion;
                    inversiones(cuentaValidar, rasRetencion, anulado);
                    estaEnRas(rasRetencion);
                }
                // contabilizamos el impuesto
                if (r.getRetencionimpuesto() != null) {
                    // proveedor
                    double valorRetencion = r.getValoriva().doubleValue() * anulado;
                    Renglones rasProveedor = new Renglones();
                    // armar la cuenta proveedor
                    rasProveedor.setCuenta(cuentaProveedor);
                    rasProveedor.setReferencia(obligacion.getConcepto());
                    rasProveedor.setFecha(obligacion.getFechaemision());
                    rasProveedor.setCabecera(cas);
                    rasProveedor.setDetallecompromiso(null);
                    rasProveedor.setAuxiliar(ruc);
                    double valorProveedor = (valorRetencion);
                    rasProveedor.setValor(new BigDecimal(valorProveedor));
                    // 
                    estaEnRas(rasProveedor);
                    inversiones(ctaproveedor, rasProveedor, anulado);
                    // retenciones
                    String ctaRetencion = f.getCxpinicio() + r.getPartida()
                            + r.getRetencionimpuesto().getCuenta()
                            + r.getRetencionimpuesto().getCodigo();
                    Cuentas cuentaValidar = validaCuenta(ctaRetencion);
                    Renglones rasRetencion = new Renglones();
                    // armar la cuenta
                    rasRetencion.setCuenta(ctaRetencion);
                    rasRetencion.setValor(new BigDecimal(valorRetencion * -1));
                    rasRetencion.setReferencia(obligacion.getConcepto());
                    if (cuentaValidar.getAuxiliares() != null) {
                        rasRetencion.setAuxiliar(ruc);
                    }
                    rasRetencion.setFecha(obligacion.getFechaemision());
                    valor += valorRetencion;
                    inversiones(cuentaValidar, rasRetencion, anulado);
                    estaEnRas(rasRetencion);
                }
//                }
            }
            //********************************************Descuentos
            for (Descuentoscompras dcom : listaDiscuentos) {
                Renglones rasProveedor = new Renglones();
                // armar la cuenta proveedor
                rasProveedor.setCuenta(dcom.getCuentaProveedor().getCodigo());
                if ((dcom.getReferencia() == null) || (dcom.getReferencia().isEmpty())) {
                    rasProveedor.setReferencia(obligacion.getConcepto());
                } else {
                    rasProveedor.setReferencia(dcom.getReferencia());
                }
                rasProveedor.setFecha(obligacion.getFechaemision());
                rasProveedor.setCabecera(cas);
                rasProveedor.setDetallecompromiso(null);
                if (dcom.getCuentaProveedor().getAuxiliares() != null) {
                    rasProveedor.setAuxiliar(dcom.getAuxiliar());
                }

                rasProveedor.setValor(dcom.getValor());
                // 
//                estaEnRas(rasProveedor);
                rasDescuento.add(rasProveedor);
                // Segundo renglon
                Renglones rasCuenta = new Renglones();
                // armar la cuenta proveedor
                rasCuenta.setCuenta(dcom.getCuentaContable().getCodigo());
                rasCuenta.setReferencia(obligacion.getConcepto());
                rasCuenta.setFecha(obligacion.getFechaemision());
                rasCuenta.setCabecera(cas);
                rasCuenta.setDetallecompromiso(null);
                if (dcom.getCuentaContable().getAuxiliares() != null) {
                    rasCuenta.setAuxiliar(dcom.getAuxiliar());
                }

                rasCuenta.setValor(dcom.getValor().negate());
                // 
//                estaEnRas(rasCuenta);
                rasDescuento.add(rasCuenta);
            }
            //******************************************** anticipos
            Query qPagos = em.createQuery("SELECT OBJECT(o) FROM Pagosvencimientos as o WHERE o.obligacion=:obligacion");
            qPagos.setParameter("obligacion", obligacion);
            List<Pagosvencimientos> listaPagos = qPagos.getResultList();
            double valorAnticipo = 0;
            for (Pagosvencimientos p : listaPagos) {

                if (p.getValoranticipo() != null) {
                    if (p.getAnticipo() != null) {

                        Anticipos a = p.getAnticipo();
                        // traer el kardex banco con que se pago el anticipo
                        Kardexbanco kant = null;
                        Query qKa = em.createQuery("Select OBJECT(o) FROM Kardexbanco as o WHERE o.anticipo=:anticipo");
                        qKa.setParameter("anticipo", a);
                        List<Kardexbanco> lkb = qKa.getResultList();
                        for (Kardexbanco kb : lkb) {
                            kant = kb;
                        }
                        if (kant != null) {
                            // traer los renglones del anticipo
                            Query qAnticipos = em.createQuery("SELECT OBJECT(o) "
                                    + " FROM Renglones as o "
                                    + " WHERE ((o.cabecera.idmodulo=:id and o.cabecera.opcion='PAGO PROVEEDORES') or "
                                    + " (o.cabecera.opcion ='ANTICIPO PROVEEDORES' and o.cabecera.idmodulo=:id1)) and o.valor>0");
//                                + " o.cabecera.opcion='ANTICIPO PROVEEDORES' and o.valor>0");
                            qAnticipos.setParameter("id", kant.getId());
                            qAnticipos.setParameter("id1", kant.getAnticipo().getId());
                            List<Renglones> renglonesAnticipo = qAnticipos.getResultList();
                            for (Renglones rg : renglonesAnticipo) {
//                            if (rg.getValor()<0){
                                Renglones rAnticipo = new Renglones(); // reglon de provaeedores
                                rAnticipo.setReferencia("APLICACION ANTICIPO : " + p.getAnticipo().getReferencia());
                                valorAnticipo = p.getValoranticipo().doubleValue();
                                int signo = (rg.getValor().doubleValue() >= 0 ? -1 : 1);

                                rAnticipo.setValor(new BigDecimal(valorAnticipo * signo * anulado));
                                rAnticipo.setCuenta(rg.getCuenta());
                                Cuentas cuentaP = validaCuenta(rg.getCuenta());
                                rAnticipo.setPresupuesto(cuentaP.getPresupuesto());
                                if (cuentaP.getAuxiliares() != null) {
                                    rAnticipo.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                                }
                                rasAnticipo.add(rAnticipo);
//                            ras.add(rAnticipo);
//                            estaEnRas(rAnticipo);
                                inversiones(cuentaP, rAnticipo, anulado);
                                Renglones rProveedor = new Renglones(); // reglon de provaeedores
                                rProveedor.setReferencia("APLICACION ANTICIPO : " + p.getAnticipo().getReferencia());
//                            double valorMultaProveedor = multa.getMulta().doubleValue();
                                rProveedor.setValor(new BigDecimal(valorAnticipo * anulado));
                                rProveedor.setCuenta(ctaProveedor);
                                cuentaP = validaCuenta(ctaProveedor);
                                rProveedor.setPresupuesto(cuentaP.getPresupuesto());
                                if (cuentaP.getAuxiliares() != null) {
                                    rProveedor.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                                }
//                            estaEnRas(rProveedor);
//                            ras.add(rProveedor);
                                rasAnticipo.add(rProveedor);
//                            }
                            }
                        } // fin kardex nulo
                    } else {
                        // años anteriores
                        Codigos codigo = null;
                        if (p.getObligacion().getProveedor().getEmpresa().getRuc().equals("1768153530001")) {
                            codigo = traerCodigo("ANTANT", "02");

                        } else {
                            codigo = traerCodigo("ANTANT", "01");
                        }
                        // primera cuenta
                        Renglones rAnticipo = new Renglones(); // reglon de provaeedores
                        rAnticipo.setReferencia("Aplicación Anticipo : " + "SALDOS INICIALES");
                        valorAnticipo = p.getValoranticipo().doubleValue();
                        rAnticipo.setValor(new BigDecimal(valorAnticipo * anulado));
                        rAnticipo.setCuenta(codigo.getDescripcion());
                        Cuentas cuentaP = validaCuenta(codigo.getDescripcion());
                        rAnticipo.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            rAnticipo.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        rasAnticipo.add(rAnticipo);
                        inversiones(cuentaP, rAnticipo, anulado);
                        // Segunda cuenta
                        rAnticipo = new Renglones(); // reglon de provaeedores
                        rAnticipo.setReferencia("Aplicación Anticipo : SALDOS INICIALES");
                        valorAnticipo = p.getValoranticipo().doubleValue();
                        rAnticipo.setValor(new BigDecimal(valorAnticipo * anulado * -1));

                        rAnticipo.setCuenta(codigo.getNombre());
                        cuentaP = validaCuenta(codigo.getNombre());
                        rAnticipo.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            rAnticipo.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        rasAnticipo.add(rAnticipo);
                        // Tercera cuenta
                        rAnticipo = new Renglones(); // reglon de provaeedores
                        rAnticipo.setReferencia("Aplicación Anticipo : SALDOS INCIALES");
                        valorAnticipo = p.getValoranticipo().doubleValue();
                        rAnticipo.setValor(new BigDecimal(valorAnticipo * anulado));
                        rAnticipo.setCuenta(ctaProveedor);
                        cuentaP = validaCuenta(ctaProveedor);
                        rAnticipo.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            rAnticipo.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        rasAnticipo.add(rAnticipo);
                        // cuarta cuenta
                        rAnticipo = new Renglones(); // reglon de provaeedores
                        rAnticipo.setReferencia("Aplicación Anticipo : SALDOS INICIALES");
                        valorAnticipo = p.getValoranticipo().doubleValue();
                        rAnticipo.setValor(new BigDecimal(valorAnticipo * anulado * -1));
                        rAnticipo.setCuenta(codigo.getDescripcion());
                        cuentaP = validaCuenta(codigo.getDescripcion());
                        rAnticipo.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            rAnticipo.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        rasAnticipo.add(rAnticipo);
                    }
                }
                // para multas
                if (p.getMulta() != null) {
                    Informes multa = p.getMulta();
                    // traer los renglones del anticipo
                    Query qIngresos = em.createQuery("SELECT OBJECT(o) "
                            + " FROM Renglones as o  WHERE o.cabecera.opcion=:multa and o.cabecera.idmodulo=:idmodulo"
                            + " and o.valor>0");
                    qIngresos.setParameter("multa", "MULTAS");
                    qIngresos.setParameter("idmodulo", p.getMulta().getId());
                    List<Renglones> renglonesIngresos = qIngresos.getResultList();
                    for (Renglones ig : renglonesIngresos) {
                        // multa
//                        if (ig.getValor().doubleValue() > 0) {
                        Renglones rMulta = new Renglones(); // reglon de multa
                        rMulta.setReferencia("APLICACION MULTA : " + multa.getTexto());
                        double valorMulta = multa.getMulta().negate().doubleValue();
                        rMulta.setValor(new BigDecimal(valorMulta * anulado));
                        rMulta.setCuenta(ig.getCuenta());
                        Cuentas cuentaMulta = validaCuenta(ig.getCuenta());
                        rMulta.setPresupuesto(cuentaMulta.getPresupuesto());
                        if (cuentaMulta.getAuxiliares() != null) {
                            rMulta.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        rasMultas.add(rMulta);
//                            ras.add(rMulta);
//                        estaEnRasMultas(rMulta);
                        inversiones(cuentaMulta, rMulta, anulado);

                        Renglones rProveedor = new Renglones(); // reglon de provaeedores
                        rProveedor.setReferencia("APLICACION MULTA : " + multa.getTexto());
                        double valorMultaProveedor = multa.getMulta().doubleValue();
                        rProveedor.setValor(new BigDecimal(valorMultaProveedor * anulado));
                        rProveedor.setCuenta(ctaProveedor);
                        Cuentas cuentaP = validaCuenta(ctaProveedor);
                        rProveedor.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            rProveedor.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        rasMultas.add(rProveedor);
//                            ras.add(rProveedor);
//                        estaEnRasMultas(rProveedor);
                        inversiones(cuentaP, rProveedor, anulado);
//                        }
                    }
                }
            }
            //Pagos al exterior
            if (obligacion.getPagoExterior() != null) {
                Query qFondos = em.createQuery("Select object(o) from Fondos as o WHERE o.exterior=:exterior");
                qFondos.setParameter("exterior", obligacion.getPagoExterior());
                List<Fondos> listaFondos = qFondos.getResultList();
                //Contabilizacion Fondos al exterior
                for (Fondos fo : listaFondos) {
                    Cuentas cuentaP = validaCuenta(ctaProveedor);
                    Renglones rasProveedor = new Renglones();
                    rasProveedor.setCuenta(ctaProveedor);
                    rasProveedor.setReferencia(obligacion.getConcepto());
                    rasProveedor.setFecha(obligacion.getFechaemision());
                    rasProveedor.setDetallecompromiso(null);
                    if (cuentaP.getAuxiliares() != null) {
                        rasProveedor.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
                    }
                    rasProveedor.setValor(fo.getValor());
//                    rasExterior.add(rasProveedor);
                    estaEnRasExterior(rasProveedor);

                    //Buscar el pago del fondo
                    Query qRenglon = em.createQuery("Select object(o) from Renglones as o WHERE o.cabecera.idmodulo=:idmodulo"
                            + " and o.valor > 0 and o.cabecera.opcion='PAGO FONDO A RENDIR CUENTAS'");
                    qRenglon.setParameter("idmodulo", fo.getId());
                    List<Renglones> listaRenglon = qRenglon.getResultList();
                    String cuentaFondo = "";
                    if (!listaRenglon.isEmpty()) {
                        cuentaFondo = listaRenglon.get(0).getCuenta();
                    } else {
                        Query qCuenta = em.createQuery("Select object(o) from Codigos as o WHERE o.codigo='F' and o.maestro.codigo='GASTGEN'");
                        List<Codigos> listacodigos = qCuenta.getResultList();
                        if (!listacodigos.isEmpty()) {
                            cuentaFondo = listacodigos.get(0).getDescripcion();
                        }

                        // armar la cuenta
                        Cuentas cuentaF = validaCuenta(cuentaFondo);
                        Renglones rasFondo = new Renglones();
                        rasFondo.setCuenta(cuentaFondo);
                        rasFondo.setReferencia(obligacion.getConcepto());
                        rasFondo.setDetallecompromiso(null);
                        if (cuentaF.getAuxiliares() != null) {
                            rasFondo.setAuxiliar(fo.getEmpleado().getEntidad().getPin());
                        }
                        rasFondo.setValor(new BigDecimal(fo.getValor().doubleValue() * -1));
//                        rasExterior.add(rasFondo);
                        estaEnRasExterior(rasFondo);
                    }

                }
            }
        }

        ////////////////////////////////ATERIOR//////////////////////////////////////////////////////////
        // grabar ras
        for (Renglones r : ras) {
            r.setFecha(fechaMasGrande);

        }
        if (!rasInversiones.isEmpty()) {

            for (Renglones r : getRasInversiones()) {
                r.setFecha(fechaMasGrande);
            }
        }
        if (rasMultas != null) {
            if (!rasMultas.isEmpty()) {

                for (Renglones r : getRasMultas()) {
                    r.setFecha(fechaMasGrande);
                }
            }
            Collections.sort(rasMultas, new valorComparator());
        }
        // ver anticipos
        Collections.sort(ras, new valorComparator());
//        Collections.sort(rasAnticipo, new valorComparator());
        Collections.sort(rasInversiones, new valorComparator());
        if (!rasExterior.isEmpty()) {

            for (Renglones r : rasExterior) {
                r.setFecha(fechaMasGrande);
            }
        }

        // colocar anticipos
        Map retorno = new HashMap();
        retorno.put("renglones", ras);
        retorno.put("rasInversiones", rasInversiones);
        retorno.put("rasAnticipos", rasAnticipo);
        retorno.put("rasMultas", rasMultas);
        retorno.put("rasDescuento", rasDescuento);
        retorno.put("rasExterior", rasExterior);
        return retorno;
    }

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        Detallecompromiso detalleParametro = r1.getDetallecompromiso();
        if (detalleParametro == null) {
            detalleParametro = new Detallecompromiso();
        }
        for (Renglones r : ras) {
            Detallecompromiso detalleInterno = r.getDetallecompromiso();
            if (detalleInterno == null) {
                detalleInterno = new Detallecompromiso();
            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
//            if ((r.getDetallecompromiso() != null) && (r1.getDetallecompromiso() != null)) {
            // comprara si uno 
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && detalleInterno.equals(detalleParametro)
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
//            } else {
////            } else if ((r.getDetallecompromiso() == null) && (r1.getDetallecompromiso() == null)) {
//                if ((r.getCuenta().equals(r1.getCuenta()))
//                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
//                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
//                    r.setValor(new BigDecimal(valor));
//                    return true;
//                }
//            }
        }
        ras.add(r1);
        return false;
    }

    private boolean estaEnRasExterior(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : rasExterior) {
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            // comprara si uno 
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        rasExterior.add(r1);
        return false;
    }

    private boolean estaEnRasMultas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        Detallecompromiso detalleParametro = r1.getDetallecompromiso();
        if (detalleParametro == null) {
            detalleParametro = new Detallecompromiso();
        }
        for (Renglones r : rasMultas) {
            Detallecompromiso detalleInterno = r.getDetallecompromiso();
            if (detalleInterno == null) {
                detalleInterno = new Detallecompromiso();
            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            // comprara si uno 
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && detalleInterno.equals(detalleParametro)
                    && r.getAuxiliar().equals(r1.getAuxiliar())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                return true;
            }
        }
        rasMultas.add(r1);
        return false;
    }

    private boolean estaEnRasInversiones(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
//        if (r1.getDetallecompromiso() == null) {
//            r1.setDetallecompromiso(new Detallecompromiso());
//        }
        for (Renglones r : getRasInversiones()) {
//            if (r.getDetallecompromiso() == null) {
//                r.setDetallecompromiso(new Detallecompromiso());
//            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }

            if ((r.getDetallecompromiso() != null) && (r1.getDetallecompromiso() != null)) {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getDetallecompromiso().equals(r1.getDetallecompromiso())
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            } else {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            }
        }
        getRasInversiones().add(r1);
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

    private Cuentas validaCuenta(String cuenta) {
        Query q = em.createQuery("Select Object(o) From Cuentas as o Where o.codigo=:codigo");
        q.setParameter("codigo", cuenta);
        List<Cuentas> cl = q.getResultList();
        if (cl.isEmpty()) {
            return null;
        }
        for (Cuentas c : cl) {
            if (c.getImputable()) {
                return c;
            }
        }
        return null;
    }

    public String validaContabilizar(List<Documentoselectronicos> listadoDocumentos,
            int anulado, Codigos modulo, Cabdocelect cabeceraDoc) throws Exception {
        String cuenta;
        // valida fecha
        String cierre = validarCierre(cabeceraDoc.getFecha());
        if (cierre != null) {
            return cierre;
        }
// Traer cuenta
        Formatos f = traerFormato();
        if (f == null) {
            return "ERROR: No se puede contabilizar: Necesario cxp en formatos";
        }
        Codigos codigoReclasificacion = traerCodigo("TIPREC", "RET");
        Codigos codigoReclasificacionInv = traerCodigo("TIPREC", "INVER");
        if (codigoReclasificacion == null) {
            return "ERROR: No existe creado códigos para reclasificación de cuentas";
        }
        if (codigoReclasificacionInv == null) {
            return "ERROR: No existe creado códigos para reclasificación de cuentas de inversiones";
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();
        Query q = em.createQuery("Select object(o) from Tipoasiento as o where o.modulo=:modulo");
        q.setParameter("modulo", modulo);
        Cabeceras cas = new Cabeceras();
        Tipoasiento tipo = null;
        Tipoasiento tipoReclasificacion = null;
        List<Tipoasiento> listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipo = t;
        }
        if (tipo == null) {
            return "ERROR: No existe tipo de asiento para este módulo";
        }
        q = em.createQuery("Select object(o) from Tipoasiento as o where o.codigo=:codigo");
        q.setParameter("codigo", codigoReclasificacionInv.getNombre());
        listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipoReclasificacion = t;
        }
        if (tipoReclasificacion == null) {
            return "ERROR: No existe tipo de asiento para reclasificación de inversiones";
        }
        double anticipo = 0;
        Query qDetallePresupuesto = em.createQuery("Select OBJECT(o) FROM Detallescompras as o WHERE o.cabeceradoc=:cabecera");
        qDetallePresupuesto.setParameter("cabecera", cabeceraDoc);
        List<Detallescompras> listaDetallesCompras = qDetallePresupuesto.getResultList();
//        Proveedores proveedor = cabeceraDoc.getCompromiso().getProveedor();
//        if (proveedor == null) {
//            return "ERROR: No se puede contabilizar: Necesario seleccionar un proveedor";
//        }
        for (Detallescompras d : listaDetallesCompras) {
            String presupuesto = d.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            presupuesto = presupuesto.substring(0, tamano);
            String cuentaProveedor = f.getCxpinicio() + presupuesto + f.getCxpfin();
            Cuentas cuentaValidar = validaCuenta(cuentaProveedor);
            if (cuentaValidar == null) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaProveedor;
            }
        }
        for (Documentoselectronicos d : listadoDocumentos) {
            Obligaciones obligacion = d.getObligacion();
            q = em.createQuery("Select object(o) from Retencionescompras as o WHERE o.obligacion=:obligacion");
            q.setParameter("obligacion", obligacion);
            List<Retencionescompras> listaRetenciones = q.getResultList();
            for (Retencionescompras r : listaRetenciones) {
                if (r.getRetencion() != null) {
                    String pr = r.getPartida();
                    String cr = r.getRetencion().getCuenta();
                    String cor = r.getRetencion().getCodigo();
                    String ctaRetencion = f.getCxpinicio() + r.getPartida()
                            + r.getRetencion().getCuenta() + r.getRetencion().getCodigo();
                    Cuentas cuentaValidar = validaCuenta(ctaRetencion);
                    if (cuentaValidar == null) {
                        return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                    }
                }
            }
        }
        return null;
    }

    private void grabarActivos(Obligaciones o, String factura, String usuario) {

//        Borrar los activos obligacion;
//        Query qBorrar = em.createQuery("Delete from Activoobligacion as o where o.obligacion=:obligacion");
//        qBorrar.setParameter("obligacion", o);
//        qBorrar.executeUpdate();
        Query q = em.createQuery("Select Object(o) From Activos as o "
                + " where o.proveedor=:proveedor and o.factura=:factura");
        q.setParameter("proveedor", o.getProveedor());
        q.setParameter("factura", factura);
        List<Activos> listaActivos = q.getResultList();
        for (Activos a : listaActivos) {
            a.setFechaalta(o.getFechaemision());
            em.merge(a);
            Trackingactivos t = new Trackingactivos();
            t.setActivo(a);
            t.setDescripcion("ALTA DE ACTIVO");
            t.setFecha(new Date());
            t.setTipo(2);
            t.setCuenta1(a.getAlta().getCuenta());
            t.setCuenta2(a.getAlta().getDebito());
            t.setUsuario(usuario);
            t.setValor(a.getValoralta().floatValue());
            t.setValornuevo(a.getValoradquisiscion().floatValue());
            em.persist(t);

            q = em.createQuery("Select OBJECT(o) from Activoobligacion as o where o.obligacion=:obligacion and o.activo=:activo");
            q.setParameter("obligacion", o);
            q.setParameter("activo", a);
            List<Activoobligacion> lact = q.getResultList();
            boolean noEntra = true;
            for (Activoobligacion ao1 : lact) {
                ao1.setFecha(o.getFechaemision());
                ao1.setObligacion(o);
                ao1.setActivo(a);
                ao1.setValor(a.getValoralta().floatValue());
                em.merge(ao1);
                noEntra = false;
            }
            if (noEntra) {
                Activoobligacion ao = new Activoobligacion();
                ao.setActivo(a);
                ao.setFecha(o.getFechaemision());
                ao.setObligacion(o);
                ao.setValor(a.getValoralta().floatValue());
                em.persist(ao);
            }
        }
    }

    private void grabarSuministros(Obligaciones o, Integer factura, String usuario) {
        Query q;
        if (o.getContrato() == null) {
            q = em.createQuery("Select Object(o) From Cabecerainventario as o "
                    + " where o.proveedor=:proveedor and o.factura=:factura "
                    + " and o.contrato is null and o.estado=0 "
                    + " and o.txid.ingreso=true and o.obligacion is null"
                    + " and  o.txid.contabiliza=false");
            q.setParameter("proveedor", o.getProveedor());

            q.setParameter("factura", factura);
        } else {
            q = em.createQuery("Select Object(o) From Cabecerainventario as o "
                    + " where o.proveedor=:proveedor and o.factura=:factura "
                    + " and o.contrato=:contrato and o.estado=0 "
                    + " and o.txid.ingreso=true and o.obligacion is null"
                    + " and  o.txid.contabiliza=false");
            q.setParameter("proveedor", o.getProveedor());
            q.setParameter("contrato", o.getContrato());
            q.setParameter("factura", factura);
        }
        List<Cabecerainventario> listaInventario = q.getResultList();
        for (Cabecerainventario c : listaInventario) {
            ejbCabInv.grabarDefinitivo(c);
        }
    }

    public String contabilizarRenglones(Cabdocelect cabeceraDocumentos,
            String usuario, int anulado, Codigos modulo, int contReclasif,
            List<Renglones> ras,
            List<Renglones> rasAnticipo,
            List<Renglones> rasInversiones,
            List<Renglones> rasMultas,
            List<Renglones> rasDescuento,
            List<Renglones> rasExterior
    ) throws Exception {
        String cierre = validarCierre(cabeceraDocumentos.getFecha());
        if (cierre != null) {
            return cierre;
        }
        Formatos f = traerFormato();
        if (f == null) {
            return "ERROR: No se puede contabilizar: Necesario cxp en formatos";
        }
        Codigos codigoReclasificacion = traerCodigo("TIPREC", "RET");
        Codigos codigoReclasificacionInv = traerCodigo("TIPREC", "INVER");
        if (codigoReclasificacion == null) {
            return "ERROR: No existe creado códigos para reclasificación de cuentas";
        }
        if (codigoReclasificacionInv == null) {
            return "ERROR: No existe creado códigos para reclasificación de cuentas de inversiones";
        }
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        int tamano = aux[f.getNivel() - 1].length();
        Query q = em.createQuery("Select object(o) from Tipoasiento as o where o.modulo=:modulo and o.rubro=2");
        q.setParameter("modulo", modulo);
        Cabeceras cas = new Cabeceras();
        Tipoasiento tipo = null;
        Tipoasiento tipoReclasificacion = null;
        Tipoasiento tipoMultas = null;
        List<Tipoasiento> listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipo = t;
        }
        if (tipo == null) {
            return "ERROR: No existe tipo de asiento para este módulo";
        }
        q = em.createQuery("Select object(o) from Tipoasiento as o where o.codigo=:codigo");
        q.setParameter("codigo", codigoReclasificacionInv.getNombre());
        listaTipo = q.getResultList();

        for (Tipoasiento t : listaTipo) {
            tipoReclasificacion = t;
        }
        if (tipoReclasificacion == null) {
            return "ERROR: No existe tipo de asiento para reclasificación de inversiones";
        }
        if (!rasMultas.isEmpty()) {
            List<Codigos> listaCodigos = super.traerCodigos("CTAMULTAS", "MULTAS");
            if (listaCodigos.isEmpty()) {
                return "ERROR: No existe tipo de asiento para multas";
            }
            Codigos ctasMultas = listaCodigos.get(0);
            String[] auxMultas = ctasMultas.getParametros().split("#");
            q = em.createQuery("Select object(o) from Tipoasiento as o where o.codigo=:codigo");
            String codigoMultaTipo = auxMultas[0].trim();
            q.setParameter("codigo", codigoMultaTipo);
            listaTipo = q.getResultList();

            for (Tipoasiento t : listaTipo) {
                tipoMultas = t;
            }
        }
        ////////////////////////////////

        // ******************************************************************************inicia el cas
        cas.setTipo(tipo);
        cas.setFecha(cabeceraDocumentos.getFecha());
//        cas.setFecha(new Date());
        cas.setDia(new Date());
        cas.setIdmodulo(cabeceraDocumentos.getId());
        if (anulado > 0) {
            cas.setDescripcion(cabeceraDocumentos.getObservaciones());
        } else {
            cas.setDescripcion("Anular Aisento de " + cabeceraDocumentos.getObservaciones());
        }
        cas.setModulo(modulo);
        cas.setUsuario(usuario);
        cas.setOpcion("OBLIGACIONES_LOTE");
        cas.setNumero(tipo.getUltimo() + 1);
        em.persist(cas);
        tipo.setUltimo(tipo.getUltimo() + 1);
        em.merge(tipo);
        // grabar ras
        for (Renglones r : ras) {
            r.setCabecera(cas);
            r.setFecha(cas.getFecha());
            if (r.getValor().doubleValue() != 0) {

                em.persist(r);
            }
        }
        if (contReclasif == 1) {
            if (!rasInversiones.isEmpty()) {
                cas = new Cabeceras();
                cas.setTipo(tipoReclasificacion);
                cas.setFecha(cabeceraDocumentos.getFecha());
//        cas.setFecha(new Date());
                cas.setDia(new Date());
                cas.setIdmodulo(cabeceraDocumentos.getId());
                if (anulado > 0) {
                    cas.setDescripcion(cabeceraDocumentos.getObservaciones());
                } else {
                    cas.setDescripcion("Anular Aisento de " + cabeceraDocumentos.getObservaciones());
                }
                cas.setModulo(modulo);
                cas.setUsuario(usuario);
                cas.setOpcion("OBLIGACIONES_INV_LOTE");
                cas.setNumero(tipoReclasificacion.getUltimo() + 1);
                em.persist(cas);
                tipoReclasificacion.setUltimo(tipoReclasificacion.getUltimo() + 1);
                em.merge(tipoReclasificacion);
                for (Renglones r : rasInversiones) {
                    r.setCabecera(cas);
                    r.setFecha(cas.getFecha());
                    if (r.getValor().doubleValue() != 0) {
                        em.persist(r);
                    }
                }
            }
        }
        if (!rasAnticipo.isEmpty()) {
            cas = new Cabeceras();
            cas.setTipo(tipoReclasificacion);
            cas.setFecha(cabeceraDocumentos.getFecha());
//        cas.setFecha(new Date());
            cas.setDia(new Date());
            cas.setIdmodulo(cabeceraDocumentos.getId());
            if (anulado > 0) {
                cas.setDescripcion(cabeceraDocumentos.getObservaciones());
            } else {
                cas.setDescripcion("Anular Aisento de " + cabeceraDocumentos.getObservaciones());
            }
            cas.setModulo(modulo);
            cas.setUsuario(usuario);
            cas.setOpcion("OBLIGACIONES_ANTICIPO_LOTE");
            cas.setNumero(tipoReclasificacion.getUltimo() + 1);
            em.persist(cas);
            tipoReclasificacion.setUltimo(tipoReclasificacion.getUltimo() + 1);
            em.merge(tipoReclasificacion);
            for (Renglones r : rasAnticipo) {
                r.setCabecera(cas);
                r.setFecha(cas.getFecha());
                if (r.getValor().doubleValue() != 0) {
                    em.persist(r);
                }
            }
        }
        if (!rasMultas.isEmpty()) {

            cas = new Cabeceras();
            cas.setTipo(tipoMultas);
            cas.setFecha(cabeceraDocumentos.getFecha());
//        cas.setFecha(new Date());
            cas.setDia(new Date());
            cas.setIdmodulo(cabeceraDocumentos.getId());
            if (anulado > 0) {
                cas.setDescripcion(cabeceraDocumentos.getObservaciones());
            } else {
                cas.setDescripcion("Anular Aisento de " + cabeceraDocumentos.getObservaciones());
            }
            cas.setModulo(modulo);
            cas.setUsuario(usuario);
            cas.setOpcion("OBLIGACIONES_MULTAS_LOTE");
            cas.setNumero(tipoMultas.getUltimo() + 1);
            em.persist(cas);
            tipoMultas.setUltimo(tipoMultas.getUltimo() + 1);
            em.merge(tipoMultas);
            for (Renglones r : rasMultas) {
                r.setCabecera(cas);
                r.setFecha(cas.getFecha());
                if (r.getValor().doubleValue() != 0) {
                    em.persist(r);
                }
            }
        }
        if (!rasDescuento.isEmpty()) {

            cas = new Cabeceras();
            cas.setTipo(tipo);
            cas.setFecha(cabeceraDocumentos.getFecha());
//        cas.setFecha(new Date());
            cas.setDia(new Date());
            cas.setIdmodulo(cabeceraDocumentos.getId());
            if (anulado > 0) {
                cas.setDescripcion(cabeceraDocumentos.getObservaciones());
            } else {
                cas.setDescripcion("Anular Aisento de " + cabeceraDocumentos.getObservaciones());
            }
            cas.setModulo(modulo);
            cas.setUsuario(usuario);
            cas.setOpcion("OBLIGACIONES_DESCUENTO_LOTE");
            cas.setNumero(tipo.getUltimo() + 1);
            em.persist(cas);
            tipo.setUltimo(tipo.getUltimo() + 1);
            em.merge(tipo);
            for (Renglones r : rasDescuento) {
                r.setCabecera(cas);
                r.setFecha(cas.getFecha());
                if (r.getValor().doubleValue() != 0) {
                    em.persist(r);
                }
            }
        }
        if (!rasExterior.isEmpty()) {
            cas = new Cabeceras();
            cas.setTipo(tipo);
            cas.setFecha(cabeceraDocumentos.getFecha());
            cas.setDia(new Date());
            cas.setIdmodulo(cabeceraDocumentos.getId());
            cas.setDescripcion(cabeceraDocumentos.getObservaciones());
            cas.setModulo(modulo);
            cas.setUsuario(usuario);
            cas.setOpcion("OBLIGACIONES_EXTERIOR_LOTE");
            cas.setNumero(tipo.getUltimo() + 1);
            em.persist(cas);
            tipo.setUltimo(tipo.getUltimo() + 1);
            em.merge(tipo);
            for (Renglones r : rasExterior) {
                r.setCabecera(cas);
                r.setFecha(cas.getFecha());
                if (r.getValor().doubleValue() != 0) {
                    em.persist(r);
                }
            }
        }
        return tipo.getUltimo().toString();
    }

    /**
     * @return the rasAnticipo
     */
    public List<Renglones> getRasAnticipo() {
        return rasAnticipo;
    }

    /**
     * @param rasAnticipo the rasAnticipo to set
     */
    public void setRasAnticipo(List<Renglones> rasAnticipo) {
        this.rasAnticipo = rasAnticipo;
    }

    /**
     * @return the rasInversiones
     */
    public List<Renglones> getRasInversiones() {
        return rasInversiones;
    }

    /**
     * @param rasInversiones the rasInversiones to set
     */
    public void setRasInversiones(List<Renglones> rasInversiones) {
        this.rasInversiones = rasInversiones;
    }

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    public Factura cargarMarshal(File xmlStr) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Factura.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Factura factura = (Factura) jaxbUnmarshaller.unmarshal(xmlStr);
            return factura;
        } catch (JAXBException ex) {

//            Logger.getLogger(LeerEmialsSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Documentoselectronicos traer(String clave, String ruc) {
        Documentoselectronicos doc = new Documentoselectronicos();
        doc.setUtilizada(false);
        Query q = em.createQuery("SELECT OBJECT(o) FROM Documentoselectronicos as o "
                + " WHERE o.clave=:clave and o.ruc=:ruc");
        q.setParameter("clave", clave);
        q.setParameter("ruc", ruc);
        List<Documentoselectronicos> lista = q.getResultList();
        for (Documentoselectronicos d : lista) {
            return d;
        }
        return doc;
    }

    public String cargaFactura(File fxlm, String xml, String ruc) {
        Factura f = cargarMarshal(fxlm);
        if (ruc.equals("1790931676001")) {
            int x = 0;
        }
        if (f != null) {
            if (!f.getInfoTributaria().getRuc().equals(ruc)) {
                return "RUC NO CORRESPONDE A PROVEEDOR";
            }
            Query q = em.createQuery("SELECT OBJECT(o) FROM Documentoselectronicos as o "
                    + " WHERE o.clave=:clave and o.ruc=:ruc");
            q.setParameter("clave", f.getInfoTributaria().getClaveAcceso());
            q.setParameter("ruc", f.getInfoTributaria().getRuc());
            List<Documentoselectronicos> lista = q.getResultList();
            Documentoselectronicos doc = new Documentoselectronicos();
            doc.setUtilizada(false);
            for (Documentoselectronicos d : lista) {
                doc = d;
            }
            if (!doc.getUtilizada()) {
                doc.setAutorizacion(f.getInfoTributaria().getClaveAcceso());
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
                String fechaEmision = f.getInfoFactura().getFechaEmision();
                fechaEmision = fechaEmision.replace("-", "/");
                if (fechaEmision.length() > 10) {
                    fechaEmision = fechaEmision.substring(0, 10);
                }
                String[] fechaDividida = fechaEmision.split("/");
                if (fechaDividida[0].length() == 4) {
                    // es el año
                    sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                }
                Date fEmision;
                try {
                    fEmision = sdf1.parse(fechaEmision);
                    doc.setFechaemision(fEmision);
                } catch (ParseException ex) {
                    Logger.getLogger(DocumentoselectronicosFacade.class.getName()).log(Level.SEVERE, null, ex);
                }

                doc.setRuc(f.getInfoTributaria().getRuc());
                doc.setClave(f.getInfoTributaria().getClaveAcceso());
                doc.setTipo(0);
                doc.setUtilizada(false);
            } else {
                doc.setAutorizacion(f.getInfoTributaria().getClaveAcceso());
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
                String fechaEmision = f.getInfoFactura().getFechaEmision();
                fechaEmision = fechaEmision.replace("-", "/");
                if (fechaEmision.length() > 10) {
                    fechaEmision = fechaEmision.substring(0, 10);
                }
                String[] fechaDividida = fechaEmision.split("/");
                if (fechaDividida[0].length() == 4) {
                    // es el año
                    sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                }
                Date fEmision;
                try {
                    fEmision = sdf1.parse(fechaEmision);
                    doc.setFechaemision(fEmision);
                } catch (ParseException ex) {
                    Logger.getLogger(DocumentoselectronicosFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            if (doc.getAutorizacion().equals("0904201801179005388100120019990080820540094283111")) {
//                int x = 0;
//            }
            try {
                if (!doc.getUtilizada()) {

                    doc.setAutorizacion(f.getInfoTributaria().getClaveAcceso());
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
                    String fechaEmision = f.getInfoFactura().getFechaEmision();
                    fechaEmision = fechaEmision.replace("-", "/");
                    if (fechaEmision.length() > 10) {
                        fechaEmision = fechaEmision.substring(0, 10);
                    }
                    String[] fechaDividida = fechaEmision.split("/");
                    if (fechaDividida[0].length() == 4) {
                        // es el año
                        sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                    }
                    Date fEmision = sdf1.parse(fechaEmision);
                    doc.setFechaemision(fEmision);
                    doc.setRuc(f.getInfoTributaria().getRuc());
                    doc.setClave(f.getInfoTributaria().getClaveAcceso());
                    doc.setTipo(0);
                    doc.setUtilizada(false);
                }
                q = em.createQuery("SELECT OBJECT(o) FROM Proveedores as o "
                        + " WHERE o.empresa.ruc=:ruc");
                q.setParameter("ruc", f.getInfoTributaria().getRuc());
                String adicional = null;
                List<Proveedores> lprov = q.getResultList();
                for (Proveedores pv : lprov) {
                    adicional = pv.getAdicionales();
                }
                doc.setNumero(f.getInfoTributaria().getEstab() + "-" + f.getInfoTributaria().getPtoEmi() + "-" + f.getInfoTributaria().getSecuencial());
                BigDecimal totalSinImpuestos = f.getInfoFactura().getTotalSinImpuestos();
//                for ()
                double valorIva = 0;
                double base = 0;
                double base0 = 0;
//                double totalAdicional = 0;
                double importeTotal = f.getInfoFactura().getImporteTotal().doubleValue();
                for (Factura.Detalles.Detalle d : f.getDetalles().getDetalle()) {
                    Factura.Detalles.Detalle.Impuestos i = d.getImpuestos();
                    for (Impuesto i1 : i.getImpuesto()) {
                        if ((i1.getCodigo().equals("2")) || (i1.getCodigo().equals("0"))) {
                            if (i1.getTarifa().equals(12)) {
                                base += i1.getBaseImponible().doubleValue();
                                valorIva += i1.getValor().doubleValue();
                            } else {
                                base0 += i1.getBaseImponible().doubleValue();
                            }
                        }
                    }
                    if (adicional != null) {

                        for (Factura.InfoAdicional.CampoAdicional a : f.getInfoAdicional().getCampoAdicional()) {
                            String[] adiVector = adicional.split("#");
                            String valorAcomparar = adiVector[0];
                            if (a.getNombre().equalsIgnoreCase(valorAcomparar)) {

                                String valor = a.getValue().replace(",", "");
                                double vadicional = Double.parseDouble(valor);

                                if (adiVector.length > 1) {
                                    // traer la suma
                                    importeTotal = importeTotal + vadicional;
                                    base0 += vadicional;
                                } else {
                                    double resto = Math.abs(f.getInfoFactura().getImporteTotal().doubleValue() - vadicional);
                                    importeTotal = importeTotal + resto;
                                    base0 += resto;
                                }
                            }
                        }
                    }
                }
                if (!doc.getUtilizada()) {
                    doc.setBaseimponible(new BigDecimal(base));
                    doc.setBaseimponible0(new BigDecimal(base0));
                    doc.setIva(new BigDecimal(valorIva));
//                doc.setBaseimponible(f.getInfoFactura().getTotalSinImpuestos());
                    doc.setValortotal(new BigDecimal(importeTotal));
                }
                doc.setXml(xml);
                if (doc.getId() == null) {
                    System.err.println("Insertado 0" + doc.getNumero());
                    em.persist(doc);
                } else {
                    System.err.println("Grabado 0" + doc.getNumero());
                    em.merge(doc);
                }
                return "OK";
            } catch (ParseException ex) {
                return ex.getMessage();
            }
        } else {
            FacturaSri fac = cargar(xml);
            if (fac != null) {
                if (!fac.getInfoTributaria().getRuc().equals(ruc)) {
                    return "RUC NO CORRESPONDE A PROVEEDOR";
                }
//                if (ruc.equals("1792545323001")) {
//                    int x = 0;
//                }
                Query q = em.createQuery("SELECT OBJECT(o) FROM Documentoselectronicos as o "
                        + " WHERE o.clave=:clave and o.ruc=:ruc");
                String clave = fac.getInfoTributaria().getClaveAcceso();
                String ruc1 = fac.getInfoTributaria().getRuc();
                q.setParameter("clave", clave.trim());
                q.setParameter("ruc", ruc.trim());
                List<Documentoselectronicos> lista = q.getResultList();
                Documentoselectronicos doc1 = new Documentoselectronicos();
                doc1.setUtilizada(false);
                for (Documentoselectronicos d : lista) {
                    doc1 = d;
                }
                if (!doc1.getUtilizada()) {
                    doc1.setAutorizacion(fac.getInfoTributaria().getClaveAcceso());
                    Date fEmision = fac.getInfoFactura().getFechaEmision();
                    doc1.setFechaemision(fEmision);
                    doc1.setRuc(fac.getInfoTributaria().getRuc());
                    doc1.setClave(fac.getInfoTributaria().getClaveAcceso());
                    doc1.setTipo(0);
                    doc1.setUtilizada(false);
                } else {
                    Date fEmision = fac.getInfoFactura().getFechaEmision();
                    doc1.setFechaemision(fEmision);
                    doc1.setRuc(fac.getInfoTributaria().getRuc());
                    doc1.setClave(fac.getInfoTributaria().getClaveAcceso());
                    doc1.setTipo(0);
                }
                q = em.createQuery("SELECT OBJECT(o) FROM Proveedores as o "
                        + " WHERE o.empresa.ruc=:ruc");
                q.setParameter("ruc", fac.getInfoTributaria().getRuc());
                String adicional = null;
                List<Proveedores> lprov = q.getResultList();
                for (Proveedores pv : lprov) {
                    adicional = pv.getAdicionales();
                }
                doc1.setNumero(fac.getInfoTributaria().getEstab() + "-" + fac.getInfoTributaria().getPtoEmi() + "-" + fac.getInfoTributaria().getSecuencial());
                BigDecimal totalSinImpuestos = new BigDecimal(fac.getInfoFactura().getTotalSinImpuestos().trim());
                double valorIva = 0;
                double base = 0;
                double base0 = 0;
                double totalAdicional = 0;
                double importeTotal = Double.parseDouble(fac.getInfoFactura().getImporteTotal().trim());
                for (FacturaSri.Detalle d : fac.getDetalles()) {
                    for (FacturaSri.Impuesto i : d.getImpuestos()) {
                        if ((i.getCodigo().equals("2")) || (i.getCodigo().equals("0"))) {
                            String tarifa = i.getTarifa().replace(",", ".");
                            tarifa = tarifa.replace("%", "");

                            BigDecimal bg = new BigDecimal(BigInteger.ZERO);
                            try {
                                bg = new BigDecimal(tarifa.trim());
                            } catch (Exception e) {
                                int x = 0;
                            }
                            Integer tari = bg.intValue();
                            if (tari == 12) {
                                i.setTarifa("12");
                                String ibase = i.getBaseImponible().trim();
                                BigDecimal dase = new BigDecimal(aNumero(ibase));
                                base += dase.doubleValue();
                                String ivalor = i.getValor().trim();
                                dase = new BigDecimal(aNumero(ivalor));
                                valorIva += dase.doubleValue();
                            } else {
                                String ibase = i.getBaseImponible().trim();
                                BigDecimal dase = new BigDecimal(aNumero(ibase));
                                base0 += dase.doubleValue();
                            }
                        }
                    }
                }
                if (adicional != null) {
                    String[] adiVector = adicional.split("#");
                    String valorAcomparar = reemplazarCaracteresRaros(adiVector[0]).trim();
                    for (FacturaSri.CampoAdicional a : fac.getInfoAdicional()) {

                        String valorComparado = reemplazarCaracteresRaros(a.getNombre()).trim();

                        if (valorComparado.equalsIgnoreCase(valorAcomparar)) {

                            double vadicional = aNumero(a.getContent());
                            if (adiVector.length > 1) {
                                // traer la suma
                                importeTotal = importeTotal + vadicional;
                                base0 += vadicional;
                            } else {
                                double resto = Math.abs(importeTotal - vadicional);
                                importeTotal = importeTotal + resto;
                                base0 += resto;
                            }
                        }
                    }
                }
                if (!doc1.getUtilizada()) {
//                String valor1 = fac.getInfoFactura().getTotalSinImpuestos();
                    doc1.setBaseimponible(new BigDecimal(base));
                    doc1.setBaseimponible0(new BigDecimal(base0));
                    doc1.setIva(new BigDecimal(valorIva));
//                valor1 = fac.getInfoFactura().getImporteTotal().trim();
                    doc1.setValortotal(new BigDecimal(importeTotal));
                }
                doc1.setXml(xml);
                if (doc1.getId() == null) {
                    em.persist(doc1);
//                    System.err.println("Insertado 1" + doc1.getNumero());
                } else {
//                    System.err.println("Grabado 1" + doc1.getNumero());
                    em.merge(doc1);
                }
                return "OK";
            }

        }

        return null;
    }

    public String reemplazarCaracteresRaros(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ/%&,.#+-¡?¿()!°|[]\"";
//        System.out.println("Original"+original.length());
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC                   ";
//        System.out.println("Ascii"+ascii.length());
        String output = input;
        if (output == null) {
            return "";
        }
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    private double aNumero(String contenido) {
        try {

            String valor = contenido;
            int primerComa = valor.indexOf(",");
            if (primerComa > 0) {
                // no hay
                if (primerComa == valor.length() - 2) {
                    // es decimal
                    valor = valor.replace(".", "");
                    valor = valor.replace(",", ".");
                } else {
                    valor = valor.replace(",", "");
                }
            }
            return Double.parseDouble(valor);
        } catch (NumberFormatException ee) {
//            System.out.println(("Valor : " + contenido));
        }
        return 0;
    }

    public String traerValor(String xml, String etiqueta) {
        String retorno = "";
        int donde = xml.indexOf("<" + etiqueta + ">");
//        int hasta = xml.indexOf("</");
        int hasta = xml.indexOf("</" + etiqueta + ">");
        if (hasta - donde > 0) {
            retorno = xml.substring(donde, hasta);
            retorno = retorno.replace("<" + etiqueta + ">", "");
        }
        return retorno;
    }

    public FacturaSri cargar(String xmlStr) {
        // crear el archivo en base al string
//        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//        cargar(is);
        try {
            xmlStr = xmlStr.replace("&lt;", "<");
            xmlStr = xmlStr.replace("&gt;", ">");
            xmlStr = xmlStr.replace("&quot;", "'");
            FacturaSri facturaSri = new FacturaSri();
            String numeroAutorizacion = traerValor(xmlStr, "numeroAutorizacion");
            // Información Autorización
            FacturaSri.Autorizacion aut;
            aut = facturaSri.getAutorizacion();

            String estado = traerValor(xmlStr, "estado");
            aut.setNumeroAutorizacion(numeroAutorizacion);
            aut.setEstado(estado);
            String fechaAutorizacion = traerValor(xmlStr, "fechaAutorizacion");
            //retirar si hay <> </>
//            if (fechaAutorizacion.substring(0, 1).equals("<")) {
//
//            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
            fechaAutorizacion = fechaAutorizacion.replace("-", "/");
            fechaAutorizacion = fechaAutorizacion.replace("T", " ");
            fechaAutorizacion = fechaAutorizacion.replace("PM", " ");
//                if (fechaAutorizacion.length() > 18) {
//                    fechaAutorizacion = fechaAutorizacion.substring(0, 18);
//                }
            if (fechaAutorizacion.length() > 10) {
                fechaAutorizacion = fechaAutorizacion.substring(0, 10);
            }
            String[] fechaDivididaAutorizacion = fechaAutorizacion.split("/");
            if (fechaDivididaAutorizacion[0].length() == 4) {
                // es el año
                sdf = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fAutoriz = sdf.parse(fechaAutorizacion);
            aut.setFechaAutorizacion(fAutoriz);
            facturaSri.setAutorizacion(aut);
//                raiz = document.getRootElement();

            // Información Tributaria
            FacturaSri.InfoTributaria it;
            it = facturaSri.getInfoTributaria();
            String claveAcceso = traerValor(xmlStr, "claveAcceso");
            String punto = traerValor(xmlStr, "ptoEmi");
            String sucursal = traerValor(xmlStr, "estab");
            String secuencial = traerValor(xmlStr, "secuencial");
            String ambiente = traerValor(xmlStr, "ambiente");
            String codDocSustento = traerValor(xmlStr, "codDoc");
            it.setPtoEmi(punto);
            it.setCodDoc(codDocSustento);
            it.setEstab(sucursal);
            it.setSecuencial(secuencial);
            it.setClaveAcceso(claveAcceso);
            it.setAmbiente(ambiente);
            it.setRuc(traerValor(xmlStr, "ruc"));
            it.setRazonSocial(traerValor(xmlStr, "razonSocial"));
            it.setDirMatriz(traerValor(xmlStr, "dirMatriz"));
            // Info factura
            FacturaSri.InfoFactura ifac;
            ifac = facturaSri.getInfoFactura();
            String fechaEmision = traerValor(xmlStr, "fechaEmision");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyy");
            fechaEmision = fechaEmision.replace("-", "/");
            if (fechaEmision.length() > 10) {
                fechaEmision = fechaEmision.substring(0, 10);
            }
            String[] fechaDividida = fechaEmision.split("/");
            if (fechaDividida[0].length() == 4) {
                // es el año
                sdf1 = new SimpleDateFormat("yyyy/MM/dd");
            }
            Date fEmision = sdf1.parse(fechaEmision);
            facturaSri.setFechaEmi(fEmision);
            ifac.setFechaEmision(fEmision);
            ifac.setContribuyenteEspecial(traerValor(xmlStr, "contribuyenteEspecial"));
            ifac.setDirEstablecimiento(traerValor(xmlStr, "dirEstablecimiento"));
            ifac.setObligadoContabilidad(traerValor(xmlStr, "obligadoContabilidad"));
            ifac.setTipoIdentificacionComprador(traerValor(xmlStr, "tipoIdentificacionComprador"));
            ifac.setRazonSocialComprador(traerValor(xmlStr, "razonSocialComprador"));
            ifac.setIdentificacionComprador(traerValor(xmlStr, "identificacionComprador"));
            ifac.setTotalSinImpuestos(traerValor(xmlStr, "totalSinImpuestos"));
            ifac.setTotalDescuento(traerValor(xmlStr, "totalDescuento"));
            ifac.setPropina(traerValor(xmlStr, "propina"));
            ifac.setImporteTotal(traerValor(xmlStr, "importeTotal"));
            ifac.setMoneda(traerValor(xmlStr, "moneda"));
            String totalComImp = traerValor(xmlStr, "totalConImpuestos");
            String[] totalConImpuestos = totalComImp.split("</totalImpuesto>");
//            List list = totalConImpuestos.getChildren();
            for (int k = 0; k < totalConImpuestos.length; k++) {
                String tarifa = traerValor(totalConImpuestos[k], "tarifa");
                if ((tarifa == null) || (tarifa.isEmpty())) {
                    tarifa = "0";
                }
                String codigo = traerValor(totalConImpuestos[k], "codigo");
                if ((codigo == null) || (codigo.isEmpty())) {
                    codigo = "0";
                }
                String valor = traerValor(totalConImpuestos[k], "valor");
                if ((valor == null) || (valor.isEmpty())) {
                    valor = "0";
                }
                String baseImponible = traerValor(totalConImpuestos[k], "baseImponible");
                if ((baseImponible == null) || (baseImponible.isEmpty())) {
                    baseImponible = "0";
                }
                String descuentoAdicional = traerValor(totalConImpuestos[k], "descuentoAdicional");
                if ((descuentoAdicional == null) || (descuentoAdicional.isEmpty())) {
                    descuentoAdicional = "0";
                }
                ifac.agregarTotalConImpuestos(
                        codigo, valor, tarifa, descuentoAdicional,
                        traerValor(totalConImpuestos[k], "codigoPorcentaje"),
                        baseImponible);
            }

            facturaSri.setInfoFactura(ifac);
            String detallesStr = traerValor(xmlStr, "detalles");
            String[] detalles = detallesStr.split("</detalle>");
//            list = dEtalles.getChildren();
            for (int k = 0; k < detalles.length; k++) {
                String detalle = detalles[k];
                String impuestosStr = traerValor(detalle, "impuestos");
                String[] impuestos = impuestosStr.split("</impuesto>");
                List impLista = new LinkedList();
//                Element tabla = (Element) list.get(k);
//                List list1 = tabla.getChildren();
                for (int l = 0; l < impuestos.length; l++) {
//                    Element tablaDetalle = (Element) list1.get(l);
                    impLista.add(facturaSri.cargaImpuesto(traerValor(impuestos[l], "codigo"),
                            traerValor(impuestos[l], "valor"),
                            traerValor(impuestos[l], "codigoPorcentaje"),
                            traerValor(impuestos[l], "baseImponible"),
                            traerValor(impuestos[l], "tarifa")));
                }
                facturaSri.cargaDetalle(traerValor(detalle, "precioUnitario"),
                        traerValor(detalle, "descuento"),
                        traerValor(detalle, "codigoPrincipal"),
                        impLista,
                        traerValor(detalle, "cantidad"),
                        traerValor(detalle, "descripcion"),
                        traerValor(detalle, "precioTotalSinImpuesto"));
            }
//            Element infoAdicional = raiz1.getChild("infoAdicional");
            String infoAdicional = traerValor(xmlStr, "infoAdicional");
            if (!((infoAdicional == null) || (infoAdicional.isEmpty()))) {
                String[] info = infoAdicional.split("</campoAdicional>");
                //listaAdicionales = new LinkedList<>();
                for (int m = 0; m < info.length; m++) {
                    //Se obtiene el elemento 'tabla'

                    String campo = info[m];
                    if (campo.indexOf(">") > 0) {
                        int intNombre = campo.indexOf("nombre=");
                        int fin = campo.indexOf(">");
                        if (intNombre > 0) {
                            String nombreCampo = campo.substring(intNombre, fin > intNombre ? fin : intNombre);
                            nombreCampo = nombreCampo.replace("nombre=", "");
                            int donde = campo.indexOf(">");
                            int hasta = campo.length() > donde ? campo.length() : donde;
//                    int hasta = campo.indexOf("</");

                            String valor = campo.substring(donde, hasta);
//                            if (valor.isEmpty()) {
//                                int x = 0;
//                            }
                            valor = valor.replace(">", "");
                            facturaSri.cargaAdicional(nombreCampo, valor);
                        }
                    }
                }
            }
            return facturaSri;

            //
        } catch (ParseException ex) {

        }
        return null;
    }

    /**
     * @return the rasMultas
     */
    public List<Renglones> getRasMultas() {
        return rasMultas;
    }

    /**
     * @param rasMultas the rasMultas to set
     */
    public void setRasMultas(List<Renglones> rasMultas) {
        this.rasMultas = rasMultas;
    }

    /**
     * @return the rasDescuento
     */
    public List<Renglones> getRasDescuento() {
        return rasDescuento;
    }

    /**
     * @param rasDescuento the rasDescuento to set
     */
    public void setRasDescuento(List<Renglones> rasDescuento) {
        this.rasDescuento = rasDescuento;
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
