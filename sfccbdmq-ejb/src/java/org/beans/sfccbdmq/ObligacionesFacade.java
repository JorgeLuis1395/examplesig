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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.entidades.sfccbdmq.Activoobligacion;
import org.entidades.sfccbdmq.Activos;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cierres;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Trackingactivos;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author edwin
 */
@Stateless
public class ObligacionesFacade extends AbstractFacade<Obligaciones> {

    @PersistenceContext(unitName = "sfccbdmq-ejbPU")
    private EntityManager em;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private CabecerainventarioFacade ejbCabInv;
    private List<Renglones> ras;
    private List<Renglones> rasInversiones;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ObligacionesFacade() {
        super(Obligaciones.class);
    }

    @Override
    protected String modificarObjetos(Obligaciones nuevo) {
        String retorno = "";
        retorno += "<fechar>" + nuevo.getFechar() + "</fechar>";
        retorno += "<id>" + nuevo.getId() + "</id>";
        retorno += "<proveedor>" + nuevo.getProveedor() + "</proveedor>";
        retorno += "<documento>" + nuevo.getDocumento() + "</documento>";
        retorno += "<fechavencimiento>" + nuevo.getFechavencimiento() + "</fechavencimiento>";
        retorno += "<autorizacion>" + nuevo.getAutorizacion() + "</autorizacion>";
        retorno += "<fechaingreso>" + nuevo.getFechaingreso() + "</fechaingreso>";
        retorno += "<usuario>" + nuevo.getUsuario() + "</usuario>";
        retorno += "<puntoemision>" + nuevo.getPuntoemision() + "</puntoemision>";
        retorno += "<establecimiento>" + nuevo.getEstablecimiento() + "</establecimiento>";
        retorno += "<fechaemision>" + nuevo.getFechaemision() + "</fechaemision>";
        retorno += "<apagar>" + nuevo.getApagar() + "</apagar>";
        retorno += "<establecimientor>" + nuevo.getEstablecimientor() + "</establecimientor>";
        retorno += "<autoretencion>" + nuevo.getAutoretencion() + "</autoretencion>";
        retorno += "<fechacaduca>" + nuevo.getFechacaduca() + "</fechacaduca>";
        retorno += "<concepto>" + nuevo.getConcepto() + "</concepto>";
        retorno += "<estado>" + nuevo.getEstado() + "</estado>";
        retorno += "<tipodocumento>" + nuevo.getTipodocumento() + "</tipodocumento>";
        retorno += "<fechacontable>" + nuevo.getFechacontable() + "</fechacontable>";
        retorno += "<tipoobligacion>" + nuevo.getTipoobligacion() + "</tipoobligacion>";
        retorno += "<facturaelectronica>" + nuevo.getFacturaelectronica() + "</facturaelectronica>";
        retorno += "<contrato>" + nuevo.getContrato() + "</contrato>";
        retorno += "<claver>" + nuevo.getClaver() + "</claver>";
        retorno += "<cuentaproveedor>" + nuevo.getCuentaproveedor() + "</cuentaproveedor>";
        retorno += "<compromiso>" + nuevo.getCompromiso() + "</compromiso>";
        retorno += "<puntor>" + nuevo.getPuntor() + "</puntor>";
        retorno += "<electronica>" + nuevo.getElectronica() + "</electronica>";
        retorno += "<numeror>" + nuevo.getNumeror() + "</numeror>";

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
            rasInvInt.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
//                rasInvInt.setDetallecompromiso(ras.getDetallecompromiso());
            }
            rasInvInt.setFecha(new Date());
            estaEnRasInversiones(rasInvInt);
            Renglones rasContrapate = new Renglones();
            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasContrapate.setFecha(new Date());
            estaEnRasInversiones(rasContrapate);

        }
    }

    /**
     *
     * Metodo para contabilizar el asiento
     *
     * @param obligacion
     * @param usuario
     * @param anulado
     * @param modulo
     * @param numeroFactura
     * @param numeroFacturaSuministtros
     * @return
     * @throws java.lang.Exception
     */
    public String contabilizar(Obligaciones obligacion,
            String usuario, int anulado, Codigos modulo,
            String numeroFactura, Integer numeroFacturaSuministtros) throws Exception {
        String cuenta;
        // Traer cuenta
        if (obligacion.getProveedor() == null) {
            return "ERROR: No se puede contabilizar: Necesario seleccionar un proveedor";
        }
        String cierre=validarCierre(obligacion.getFechaemision());
        if (cierre!=null){
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
        q = em.createQuery("Select object(o) from Rascompras as o where o.obligacion=:obligacion and (o.cba='P' or o.cba='E')");
        q.setParameter("obligacion", obligacion);
        List<Rascompras> rasCuentas = q.getResultList();

        q = em.createQuery("Select object(o) from Rascompras as o where o.obligacion=:obligacion order by o.detallecompromiso.id, o.cba asc");
        q.setParameter("obligacion", obligacion);
        List<Rascompras> rc = q.getResultList();
        double totalObligacion = 0;
        for (Rascompras r : rc) {
            double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
            double valorimpuesto = r.getValorimpuesto() == null ? 0 : r.getValorimpuesto().doubleValue();
            double valorret = r.getValorret() == null ? 0 : r.getValorret().doubleValue();
            double vretImpuesto = r.getVretimpuesto() == null ? 0 : r.getVretimpuesto().doubleValue();
            totalObligacion += valor - valorret - vretImpuesto;
            double linea = valor + valorimpuesto - valorret - valorimpuesto;
            r.setValor(new BigDecimal(valor));
            r.setValorimpuesto(new BigDecimal(valorimpuesto));
            r.setValorret(new BigDecimal(valorret));
            r.setVretimpuesto(new BigDecimal(vretImpuesto));
            if (r.getBaseimponibleimpuesto() == null) {
                r.setBaseimponibleimpuesto(BigDecimal.ZERO);
            }
            // incremeta el impuesto
//            Detallecompromiso d = r.getDetallecompromiso();
//            double saldo = d.getSaldo().doubleValue();
//            d.setSaldo(new BigDecimal(saldo - linea));
//            em.merge(d);
            // armada la cuenta de proveedor
        }
        ras = new LinkedList<>();
        rasInversiones = new LinkedList<>();
        for (Rascompras r : rc) {
            String presupuesto = r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            presupuesto = presupuesto.substring(0, tamano);
//            totalObligacion += r.getValor().doubleValue();
            String cuentaProveedor = f.getCxpinicio() + presupuesto + f.getCxpfin();
            Cuentas cuentaValidar = validaCuenta(cuentaProveedor);
            if (cuentaValidar == null) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaProveedor;
            }
            // inversiones

            Renglones rasCandidato = new Renglones();
            rasCandidato.setCuenta(r.getIdcuenta().getCodigo());
            double valor = (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue()) * anulado;
            if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
                valor = (r.getValor().doubleValue()
                        + (r.getBaseimponibleimpuesto().doubleValue()
                        * r.getImpuesto().getPorcentaje().doubleValue() / 100)) * anulado;
            }
            if (r.getValorprima() != null) {
                if (r.getImpuesto() != null) {
                    valor = (r.getValor().doubleValue()
                            + (r.getValor().doubleValue()
                            * r.getImpuesto().getPorcentaje().doubleValue() / 100)) * anulado;
                    if (r.getBaseimponibleimpuesto() != null) {
                        if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valor = (r.getValor().doubleValue()
                                    + (r.getBaseimponibleimpuesto().doubleValue()
                                    * r.getImpuesto().getPorcentaje().doubleValue() / 100)) * anulado;
                        }
                    }
                }
            }
            rasCandidato.setValor(new BigDecimal(valor));
            rasCandidato.setReferencia(r.getReferencia());
            rasCandidato.setDetallecompromiso(r.getDetallecompromiso());
//            if (cuentaValidar.getCcosto()) {
            rasCandidato.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());
//                rasCandidato.setDetallecompromiso(r.getDetallecompromiso());
//            }
            rasCandidato.setFecha(new Date());
            inversiones(r.getIdcuenta(), rasCandidato, anulado);
            estaEnRas(rasCandidato);
            if (r.getRetencion() != null) {
                // armada la cuenta de retención
                String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetencion().getCuenta() + r.getRetencion().getCodigo();
                cuentaValidar = null;
                cuentaValidar = validaCuenta(ctaRetencion);
                if (cuentaValidar == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                if (!((cuentaValidar.getCodigonif() == null) || (cuentaValidar.getCodigonif().isEmpty()))) {
                    Cuentas ctaInv = validaCuenta(cuentaValidar.getCodigonif());
                    if (ctaInv == null) {
                        return "ERROR: No existe cuenta de inversiones o no es imputable " + ctaRetencion;
                    }
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getValorret().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
//                rasRetencion.setReferencia(r.getRetencion().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
//                if (cuentaValidar.getCcosto()) {
                rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

//                }
//                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(cuentaValidar, rasRetencion, anulado);
                estaEnRas(rasRetencion);
//                rasRetencion.setCabecera(cas);
            } // fin retencion
            if (r.getRetimpuesto() != null) {
                // armada la cuenta de retención
                String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetimpuesto().getCuenta() + r.getRetimpuesto().getCodigo();
                cuentaValidar = validaCuenta(ctaRetencion);
                if (cuentaValidar == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getVretimpuesto().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
//                rasRetencion.setReferencia(r.getRetimpuesto().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
//                if (cuentaValidar.getCcosto()) {
                rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

//                }
//                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
//                rasRetencion.setCentrocosto(r.getCc());
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(cuentaValidar, rasRetencion, anulado);
                estaEnRas(rasRetencion);
            } // fin retencion impuesto
            // armar cuenta proveedor
            Renglones rasProveedor = new Renglones();
            // armar la cuenta proveedor
            Cuentas ctaproveedor = validaCuenta(cuentaProveedor);
            rasProveedor.setCuenta(cuentaProveedor);
            rasProveedor.setReferencia(r.getReferencia());
//            rasProveedor.setReferencia("Obligación Proveedor :[Tipo de documento :" + obligacion.getTipodocumento() + "], "
//                    + "Proveedor[" + obligacion.getProveedor().getEmpresa().getNombrecomercial() + "]");
            rasProveedor.setFecha(new Date());
            rasProveedor.setCabecera(cas);
//            if (ctaproveedor.getCcosto()) {
            rasProveedor.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

//            }
            rasProveedor.setDetallecompromiso(null);
            rasProveedor.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
            double valorProveedor = (valor) * -1;
            rasProveedor.setValor(new BigDecimal(valorProveedor));
            inversiones(ctaproveedor, rasProveedor, anulado);
            estaEnRas(rasProveedor);
            r.setCuenta(cuentaProveedor);
            r.setCc(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());
            em.merge(r);
        }
        // es las nuevas cuentas
        for (Rascompras r : rasCuentas) {
//            totalObligacion += r.getValor().doubleValue();
            Renglones rasCandidato = new Renglones();
            rasCandidato.setCuenta(r.getIdcuenta().getCodigo());
            double valor = (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue()) * anulado;
            rasCandidato.setValor(new BigDecimal(valor));
            rasCandidato.setReferencia(r.getReferencia());
            rasCandidato.setCentrocosto("");
            rasCandidato.setFecha(new Date());
            rasCandidato.setAuxiliar(r.getCc());
            rasCandidato.setDetallecompromiso(r.getDetallecompromiso());
            inversiones(r.getIdcuenta(), rasCandidato, anulado);
            estaEnRas(rasCandidato);
            if (r.getRetencion() != null) {
                // armada la cuenta de retención
                String ctaRetencion = codigoReclasificacion.getParametros() + r.getRetencion().getReclasificacion() + r.getRetencion().getCodigo();
                Cuentas ctaValida = validaCuenta(ctaRetencion);
                if (ctaValida == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getValorret().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
//                rasRetencion.setReferencia(r.getRetencion().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
//                if (ctaValida.getCcosto()) {
                rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

//                }
                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(ctaValida, rasRetencion, anulado);
                estaEnRas(rasRetencion);
//                rasRetencion.setCabecera(cas);
            } // fin retencion
            if (r.getRetimpuesto() != null) {
                // armada la cuenta de retención
                String ctaRetencion = codigoReclasificacion.getParametros() + r.getRetimpuesto().getReclasificacion() + r.getRetimpuesto().getCodigo();
                Cuentas ctaValida = validaCuenta(ctaRetencion);
                if (ctaValida == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getVretimpuesto().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
//                rasRetencion.setReferencia(r.getRetimpuesto().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
//                if (ctaValida.getCcosto()) {
                    rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

//                }
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(ctaValida, rasRetencion, anulado);
                estaEnRas(rasRetencion);
            } // fin retencion impuesto
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            String cuentaProveedor = codigoReclasificacion.getParametros() + f.getCxpfin();;
//            String cuentaProveedor = r.getCuenta();
            //******************************************************************************************************
            Cuentas ctaValida = validaCuenta(cuentaProveedor);
            if (ctaValida == null) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaProveedor;
            }
            // armar cuenta proveedor
            Renglones rasProveedor = new Renglones();
            // armar la cuenta proveedor
            rasProveedor.setCuenta(cuentaProveedor);
            rasProveedor.setReferencia(r.getReferencia());
//            rasProveedor.setReferencia("Obligación Proveedor :[Tipo de documento :" + obligacion.getTipodocumento() + "], "
//                    + "Proveedor[" + obligacion.getProveedor().getEmpresa().getNombrecomercial() + "]");
            rasProveedor.setFecha(new Date());
            rasProveedor.setCabecera(cas);
            rasProveedor.setAuxiliar(r.getCc());
            rasProveedor.setDetallecompromiso(r.getDetallecompromiso());
//            if (ctaValida.getCcosto()) {
//                rasProveedor.setCentrocosto(r.getDetallecompromiso().getDetallecertificacion().getAsignacion().getProyecto().getCodigo());
//            }
            double valorProveedor = (valor) * -1;
            rasProveedor.setValor(new BigDecimal(valorProveedor));
            inversiones(ctaValida, rasProveedor, anulado);
            estaEnRas(rasProveedor);
            r.setCuenta(cuentaProveedor);

            em.merge(r);
        }
        cas.setTipo(tipo);
        cas.setFecha(obligacion.getFechaemision());
//        cas.setFecha(new Date());
        cas.setDia(new Date());
        cas.setIdmodulo(obligacion.getId());
        if (anulado > 0) {
            cas.setDescripcion(obligacion.getConcepto());
        } else {
            cas.setDescripcion("Anular Aisento de " + obligacion.getConcepto());
        }
        cas.setModulo(modulo);
        cas.setUsuario(usuario);
        cas.setOpcion("OBLIGACIONES");
        cas.setNumero(tipo.getUltimo() + 1);
        em.persist(cas);
        tipo.setUltimo(tipo.getUltimo() + 1);
        em.merge(tipo);
        // grabar ras
        for (Renglones r : ras) {
            r.setCabecera(cas);
            r.setFecha(obligacion.getFechaemision());
            if (r.getValor().doubleValue() != 0) {
                em.persist(r);
            }
        }
        if (!rasInversiones.isEmpty()) {
            cas = new Cabeceras();
            cas.setTipo(tipoReclasificacion);
            cas.setFecha(obligacion.getFechaemision());
//        cas.setFecha(new Date());
            cas.setDia(new Date());
            cas.setIdmodulo(obligacion.getId());
            if (anulado > 0) {
                cas.setDescripcion(obligacion.getConcepto());
            } else {
                cas.setDescripcion("Anular Aisento de " + obligacion.getConcepto());
            }
            cas.setModulo(modulo);
            cas.setUsuario(usuario);
            cas.setOpcion("OBLIGACIONES_INV");
            cas.setNumero(tipoReclasificacion.getUltimo() + 1);
            em.persist(cas);
            tipoReclasificacion.setUltimo(tipoReclasificacion.getUltimo() + 1);
            em.merge(tipoReclasificacion);
            for (Renglones r : rasInversiones) {
                r.setCabecera(cas);
                r.setFecha(obligacion.getFechaemision());
                if (r.getValor().doubleValue() != 0) {
                    em.persist(r);
                }
            }
        }
        obligacion.setFechacontable(new Date());
        obligacion.setEstado(2);
        em.merge(obligacion);
        if (numeroFactura != null) {
            grabarActivos(obligacion, numeroFactura, usuario);
        }
        if (numeroFacturaSuministtros != null) {
            grabarSuministros(obligacion, numeroFacturaSuministtros, usuario);
        }
        return tipo.getUltimo().toString();
    }

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }

        for (Renglones r : ras) {
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
        ras.add(r1);
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
        for (Renglones r : rasInversiones) {
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
        rasInversiones.add(r1);
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

    public String validaContabilizar(Obligaciones obligacion, String usuario, int anulado, Codigos modulo) throws Exception {
        String cuenta;
        // Traer cuenta
        if (obligacion.getProveedor() == null) {
            return "ERROR: No se puede contabilizar: Necesario seleccionar un proveedor";
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
        q = em.createQuery("Select object(o) from Rascompras as o where o.obligacion=:obligacion and (o.cba='P' or o.cba='E')");
        q.setParameter("obligacion", obligacion);
        List<Rascompras> rasCuentas = q.getResultList();

        q = em.createQuery("Select object(o) from Rascompras as o where o.obligacion=:obligacion order by o.detallecompromiso.id, o.cba asc");
        q.setParameter("obligacion", obligacion);
        List<Rascompras> rc = q.getResultList();
        double totalObligacion = 0;
        for (Rascompras r : rc) {
            totalObligacion += r.getValor().doubleValue() + r.getValorimpuesto().doubleValue() - r.getValorret().doubleValue() - r.getVretimpuesto().doubleValue();
            double linea = r.getValor().doubleValue() + r.getValorimpuesto().doubleValue() - r.getValorret().doubleValue() - r.getVretimpuesto().doubleValue();
            // incremeta el impuesto
//            Detallecompromiso d = r.getDetallecompromiso();
//            double saldo = d.getSaldo().doubleValue();
//            d.setSaldo(new BigDecimal(saldo - linea));
//            em.merge(d);
            // armada la cuenta de proveedor
        }
        ras = new LinkedList<>();
        rasInversiones = new LinkedList<>();
        for (Rascompras r : rc) {
            String presupuesto = r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            presupuesto = presupuesto.substring(0, tamano);
//            totalObligacion += r.getValor().doubleValue();
            String cuentaProveedor = f.getCxpinicio() + presupuesto + f.getCxpfin();
            Cuentas cuentaValidar = validaCuenta(cuentaProveedor);
            if (cuentaValidar == null) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaProveedor;
            }

            if (r.getRetencion() != null) {
                // armada la cuenta de retención
                String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetencion().getCuenta() + r.getRetencion().getCodigo();
                cuentaValidar = null;
                cuentaValidar = validaCuenta(ctaRetencion);
                if (cuentaValidar == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                // es retencion
            } // fin retencion
            if (r.getRetimpuesto() != null) {
                // armada la cuenta de retención
                String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetimpuesto().getCuenta() + r.getRetimpuesto().getCodigo();
                cuentaValidar = validaCuenta(ctaRetencion);
                if (cuentaValidar == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }

            } // fin retencion impuesto
            // armar cuenta proveedor
            Renglones rasProveedor = new Renglones();
            // armar la cuenta proveedor
            Cuentas ctaproveedor = validaCuenta(cuentaProveedor);
            if (ctaproveedor == null) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + ctaproveedor.getCodigo();
            }
        }
        // es las nuevas cuentas
        for (Rascompras r : rasCuentas) {
//            totalObligacion += r.getValor().doubleValue();

            if (r.getRetencion() != null) {
                // armada la cuenta de retención
                String ctaRetencion = codigoReclasificacion.getParametros() + r.getRetencion().getReclasificacion() + r.getRetencion().getCodigo();
                Cuentas ctaValida = validaCuenta(ctaRetencion);
                if (ctaValida == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                // es retencion
            } // fin retencion
            if (r.getRetimpuesto() != null) {
                // armada la cuenta de retención
                String ctaRetencion = codigoReclasificacion.getParametros() + r.getRetimpuesto().getReclasificacion() + r.getRetimpuesto().getCodigo();
                Cuentas ctaValida = validaCuenta(ctaRetencion);
                if (ctaValida == null) {
                    return "ERROR: No existe cuenta de retención o no es imputable " + ctaRetencion;
                }
                // es retencion
            } // fin retencion impuesto
            String cuentaProveedor = codigoReclasificacion.getParametros() + codigoReclasificacion.getDescripcion();
            Cuentas ctaValida = validaCuenta(cuentaProveedor);
            if (ctaValida == null) {
                return "ERROR: No existe cuenta de proveedor o no es imputable " + cuentaProveedor;
            }
            // armar cuenta proveedor
        }
        return tipo.getUltimo().toString();
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

    public List<Renglones> contabilizarAntes(Obligaciones obligacion,
            String usuario, int anulado, Codigos modulo,
            String numeroFactura, Integer numeroFacturaSuministtros) throws Exception {
        String cuenta;
        // Traer cuenta
        if (obligacion.getProveedor() == null) {
            return null;
        }
        Formatos f = traerFormato();
        if (f == null) {
            return null;
        }
        Codigos codigoReclasificacion = traerCodigo("TIPREC", "RET");
        Codigos codigoReclasificacionInv = traerCodigo("TIPREC", "INVER");
        if (codigoReclasificacion == null) {
            return null;
        }
        if (codigoReclasificacionInv == null) {
            return null;
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
            return null;
        }
        q = em.createQuery("Select object(o) from Tipoasiento as o where o.codigo=:codigo");
        q.setParameter("codigo", codigoReclasificacionInv.getNombre());
        listaTipo = q.getResultList();
        for (Tipoasiento t : listaTipo) {
            tipoReclasificacion = t;
        }
        if (tipoReclasificacion == null) {
            return null;
        }
        double anticipo = 0;
        q = em.createQuery("Select object(o) from Rascompras as o where o.obligacion=:obligacion and (o.cba='P' or o.cba='E')");
        q.setParameter("obligacion", obligacion);
        List<Rascompras> rasCuentas = q.getResultList();

        q = em.createQuery("Select object(o) from Rascompras as o where o.obligacion=:obligacion order by o.detallecompromiso.id, o.cba asc");
        q.setParameter("obligacion", obligacion);
        List<Rascompras> rc = q.getResultList();
        double totalObligacion = 0;
        for (Rascompras r : rc) {
            double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
            double valorimpuesto = r.getValorimpuesto() == null ? 0 : r.getValorimpuesto().doubleValue();
            double valorret = r.getValorret() == null ? 0 : r.getValorret().doubleValue();
            double vretImpuesto = r.getVretimpuesto() == null ? 0 : r.getVretimpuesto().doubleValue();
            totalObligacion += valor - valorret - vretImpuesto;
            double linea = valor + valorimpuesto - valorret - valorimpuesto;
            r.setValor(new BigDecimal(valor));
            r.setValorimpuesto(new BigDecimal(valorimpuesto));
            r.setValorret(new BigDecimal(valorret));
            r.setVretimpuesto(new BigDecimal(vretImpuesto));
            if (r.getBaseimponibleimpuesto() == null) {
                r.setBaseimponibleimpuesto(BigDecimal.ZERO);
            }
            // incremeta el impuesto
//            Detallecompromiso d = r.getDetallecompromiso();
//            double saldo = d.getSaldo().doubleValue();
//            d.setSaldo(new BigDecimal(saldo - linea));
//            em.merge(d);
            // armada la cuenta de proveedor
        }
        ras = new LinkedList<>();
        rasInversiones = new LinkedList<>();
        for (Rascompras r : rc) {
            String presupuesto = r.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
            presupuesto = presupuesto.substring(0, tamano);
//            totalObligacion += r.getValor().doubleValue();
            String cuentaProveedor = f.getCxpinicio() + presupuesto + f.getCxpfin();
            Cuentas cuentaValidar = validaCuenta(cuentaProveedor);
            if (cuentaValidar == null) {
                return null;
            }
            // inversiones

            Renglones rasCandidato = new Renglones();
            rasCandidato.setCuenta(r.getIdcuenta().getCodigo());
            double valor = (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue()) * anulado;
            if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
                valor = (r.getValor().doubleValue()
                        + (r.getBaseimponibleimpuesto().doubleValue()
                        * r.getImpuesto().getPorcentaje().doubleValue() / 100)) * anulado;
            }
            if (r.getValorprima() != null) {
                if (r.getImpuesto() != null) {
                    valor = (r.getValor().doubleValue()
                            + (r.getValor().doubleValue()
                            * r.getImpuesto().getPorcentaje().doubleValue() / 100)) * anulado;
                    if (r.getBaseimponibleimpuesto() != null) {
                        if (r.getBaseimponibleimpuesto().doubleValue() > 0) {
                            valor = (r.getValor().doubleValue()
                                    + (r.getBaseimponibleimpuesto().doubleValue()
                                    * r.getImpuesto().getPorcentaje().doubleValue() / 100)) * anulado;
                        }
                    }
                }
            }
            rasCandidato.setValor(new BigDecimal(valor));
            rasCandidato.setReferencia(r.getReferencia());
            rasCandidato.setDetallecompromiso(r.getDetallecompromiso());
            if (cuentaValidar.getCcosto()) {
                rasCandidato.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());
//                rasCandidato.setDetallecompromiso(r.getDetallecompromiso());
            }
            rasCandidato.setFecha(new Date());
            inversiones(r.getIdcuenta(), rasCandidato, anulado);
            estaEnRas(rasCandidato);
            if (r.getRetencion() != null) {
                // armada la cuenta de retención
                String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetencion().getCuenta() + r.getRetencion().getCodigo();
                cuentaValidar = null;
                cuentaValidar = validaCuenta(ctaRetencion);
                if (cuentaValidar == null) {
                    return null;
                }
                if (!((cuentaValidar.getCodigonif() == null) || (cuentaValidar.getCodigonif().isEmpty()))) {
                    Cuentas ctaInv = validaCuenta(cuentaValidar.getCodigonif());
                    if (ctaInv == null) {
                        return null;
                    }
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getValorret().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
//                rasRetencion.setReferencia(r.getRetencion().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
                if (cuentaValidar.getCcosto()) {
                    rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

                }
//                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(cuentaValidar, rasRetencion, anulado);
                estaEnRas(rasRetencion);
//                rasRetencion.setCabecera(cas);
            } // fin retencion
            if (r.getRetimpuesto() != null) {
                // armada la cuenta de retención
                String ctaRetencion = f.getCxpinicio() + presupuesto + r.getRetimpuesto().getCuenta() + r.getRetimpuesto().getCodigo();
                cuentaValidar = validaCuenta(ctaRetencion);
                if (cuentaValidar == null) {
                    return null;
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getVretimpuesto().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
//                rasRetencion.setReferencia(r.getRetimpuesto().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
                if (cuentaValidar.getCcosto()) {
                    rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

                }
//                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
//                rasRetencion.setCentrocosto(r.getCc());
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(cuentaValidar, rasRetencion, anulado);
                estaEnRas(rasRetencion);
            } // fin retencion impuesto
            // armar cuenta proveedor
            Renglones rasProveedor = new Renglones();
            // armar la cuenta proveedor
            Cuentas ctaproveedor = validaCuenta(cuentaProveedor);
            rasProveedor.setCuenta(cuentaProveedor);
            rasProveedor.setReferencia(r.getReferencia());
//            rasProveedor.setReferencia("Obligación Proveedor :[Tipo de documento :" + obligacion.getTipodocumento() + "], "
//                    + "Proveedor[" + obligacion.getProveedor().getEmpresa().getNombrecomercial() + "]");
            rasProveedor.setFecha(new Date());
            rasProveedor.setCabecera(cas);
            if (ctaproveedor.getCcosto()) {
                rasProveedor.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

            }
            rasProveedor.setDetallecompromiso(null);
            rasProveedor.setAuxiliar(obligacion.getProveedor().getEmpresa().getRuc());
            double valorProveedor = (valor) * -1;
            rasProveedor.setValor(new BigDecimal(valorProveedor));
            inversiones(ctaproveedor, rasProveedor, anulado);
            estaEnRas(rasProveedor);
            r.setCuenta(cuentaProveedor);
            r.setCc(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());
//            em.merge(r);
        }
        // es las nuevas cuentas
        for (Rascompras r : rasCuentas) {
//            totalObligacion += r.getValor().doubleValue();
            Renglones rasCandidato = new Renglones();
            rasCandidato.setCuenta(r.getIdcuenta().getCodigo());
            double valor = (r.getValor().doubleValue() + r.getValorimpuesto().doubleValue()) * anulado;
            rasCandidato.setValor(new BigDecimal(valor));
            rasCandidato.setReferencia(r.getReferencia());
            rasCandidato.setCentrocosto("");
            rasCandidato.setFecha(new Date());
            rasCandidato.setAuxiliar(r.getCc());
            rasCandidato.setDetallecompromiso(r.getDetallecompromiso());
            inversiones(r.getIdcuenta(), rasCandidato, anulado);
            estaEnRas(rasCandidato);
            if (r.getRetencion() != null) {
                // armada la cuenta de retención
                String ctaRetencion = codigoReclasificacion.getParametros() + r.getRetencion().getReclasificacion() + r.getRetencion().getCodigo();
                Cuentas ctaValida = validaCuenta(ctaRetencion);
                if (ctaValida == null) {
                    return null;
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getValorret().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
//                rasRetencion.setReferencia(r.getRetencion().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
                if (ctaValida.getCcosto()) {
                    rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

                }
                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(ctaValida, rasRetencion, anulado);
                estaEnRas(rasRetencion);
//                rasRetencion.setCabecera(cas);
            } // fin retencion
            if (r.getRetimpuesto() != null) {
                // armada la cuenta de retención
                String ctaRetencion = codigoReclasificacion.getParametros() + r.getRetimpuesto().getReclasificacion() + r.getRetimpuesto().getCodigo();
                Cuentas ctaValida = validaCuenta(ctaRetencion);
                if (ctaValida == null) {
                    return null;
                }
                // es retencion
                Renglones rasRetencion = new Renglones();
                // armar la cuenta
                rasRetencion.setCuenta(ctaRetencion);
                double valorRetencion = r.getVretimpuesto().doubleValue() * anulado * -1;
                rasRetencion.setValor(new BigDecimal(valorRetencion));
                rasRetencion.setReferencia(r.getReferencia());
                rasRetencion.setDetallecompromiso(r.getDetallecompromiso());
//                rasRetencion.setReferencia(r.getRetimpuesto().getNombre() + " Proveedor : " + r.getObligacion().getProveedor().getEmpresa() + " RET:" + r.getObligacion().getNumeror());
                if (ctaValida.getCcosto()) {
                    rasRetencion.setCentrocosto(r.getDetallecompromiso().getAsignacion().getProyecto().getCodigo());

                }
                rasRetencion.setFecha(new Date());
                valor += valorRetencion;
                inversiones(ctaValida, rasRetencion, anulado);
                estaEnRas(rasRetencion);
            } // fin retencion impuesto
            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            String cuentaProveedor = codigoReclasificacion.getParametros() + f.getCxpfin();;
//            String cuentaProveedor = r.getCuenta();
            //******************************************************************************************************
            Cuentas ctaValida = validaCuenta(cuentaProveedor);
            if (ctaValida == null) {
                return null;
            }
            // armar cuenta proveedor
            Renglones rasProveedor = new Renglones();
            // armar la cuenta proveedor
            rasProveedor.setCuenta(cuentaProveedor);
            rasProveedor.setReferencia(r.getReferencia());
//            rasProveedor.setReferencia("Obligación Proveedor :[Tipo de documento :" + obligacion.getTipodocumento() + "], "
//                    + "Proveedor[" + obligacion.getProveedor().getEmpresa().getNombrecomercial() + "]");
            rasProveedor.setFecha(new Date());
            rasProveedor.setCabecera(cas);
            rasProveedor.setAuxiliar(r.getCc());
            rasProveedor.setDetallecompromiso(r.getDetallecompromiso());
//            if (ctaValida.getCcosto()) {
//                rasProveedor.setCentrocosto(r.getDetallecompromiso().getDetallecertificacion().getAsignacion().getProyecto().getCodigo());
//            }
            double valorProveedor = (valor) * -1;
            rasProveedor.setValor(new BigDecimal(valorProveedor));
            inversiones(ctaValida, rasProveedor, anulado);
            estaEnRas(rasProveedor);
            r.setCuenta(cuentaProveedor);

//            em.merge(r);
        }
        cas.setTipo(tipo);
        cas.setFecha(obligacion.getFechaemision());
//        cas.setFecha(new Date());
        cas.setDia(new Date());
        cas.setIdmodulo(obligacion.getId());
        if (anulado > 0) {
            cas.setDescripcion(obligacion.getConcepto());
        } else {
            cas.setDescripcion("Anular Aisento de " + obligacion.getConcepto());
        }
        cas.setModulo(modulo);
        cas.setUsuario(usuario);
        cas.setOpcion("OBLIGACIONES");
        cas.setNumero(tipo.getUltimo() + 1);
//        em.persist(cas);
        tipo.setUltimo(tipo.getUltimo() + 1);
//        em.merge(tipo);
        // grabar ras
        for (Renglones r : ras) {
            r.setCabecera(cas);
            r.setFecha(obligacion.getFechaemision());
            if (r.getValor().doubleValue() != 0) {
//                em.persist(r);
            }
        }
        if (!rasInversiones.isEmpty()) {
            cas = new Cabeceras();
            cas.setTipo(tipoReclasificacion);
            cas.setFecha(obligacion.getFechaemision());
//        cas.setFecha(new Date());
            cas.setDia(new Date());
            cas.setIdmodulo(obligacion.getId());
            if (anulado > 0) {
                cas.setDescripcion(obligacion.getConcepto());
            } else {
                cas.setDescripcion("Anular Aisento de " + obligacion.getConcepto());
            }
            cas.setModulo(modulo);
            cas.setUsuario(usuario);
            cas.setOpcion("OBLIGACIONES_INV");
            cas.setNumero(tipoReclasificacion.getUltimo() + 1);
//            em.persist(cas);
            tipoReclasificacion.setUltimo(tipoReclasificacion.getUltimo() + 1);
//            em.merge(tipoReclasificacion);
            for (Renglones r : rasInversiones) {
                r.setCabecera(cas);
                r.setFecha(obligacion.getFechaemision());
                estaEnRas(r);
                if (r.getValor().doubleValue() != 0) {
//                    em.persist(r);
                }
            }
        }
//        obligacion.setFechacontable(new Date());
//        obligacion.setEstado(2);
//        em.merge(obligacion);
//        if (numeroFactura != null) {
//            grabarActivos(obligacion, numeroFactura, usuario);
//        }
//        if (numeroFacturaSuministtros != null) {
//            grabarSuministros(obligacion, numeroFacturaSuministtros, usuario);
//        }
        return ras;
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
