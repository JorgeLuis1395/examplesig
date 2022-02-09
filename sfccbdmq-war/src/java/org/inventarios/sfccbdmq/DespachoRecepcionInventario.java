/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AdicionalkardexFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "despachoTxSfccbdmq")
@ViewScoped
public class DespachoRecepcionInventario {

    /**
     * Creates a new instance of CabeceraBean
     */
    public DespachoRecepcionInventario() {
        cabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private CabecerainventarioFacade ejbCabecerainventario;

    @EJB
    private TxinventariosFacade ejbTxInventario;
    @EJB
    private AdicionalkardexFacade ejbAdKardex;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private BodegasuministroFacade ejbBodSum;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    private Cabecerainventario cabecera;
    private Kardexinventario kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private List<Renglones> renglones;
    private List<Renglones> renglonesReclasificacion;
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Cabecerainventario> cabeceras;
    private List<Kardexinventario> listaKardex;
    private Integer factura;
    private Integer numero;
    private Integer estado = -10;
    private String observaciones;
    private String observacionesAsiento;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Bodegas bodega;
    private Bodegas bodegaDestino;
    private boolean transferencia;
    private Contratos contrato;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    /**
     * @return the configuracionBean
     */
    public ConfiguracionBean getConfiguracionBean() {
        return configuracionBean;
    }

    /**
     * @param configuracionBean the configuracionBean to set
     */
    public void setConfiguracionBean(ConfiguracionBean configuracionBean) {
        this.configuracionBean = configuracionBean;
    }

    /**
     * @return the perfil
     */
    public Perfiles getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(Perfiles perfil) {
        this.perfil = perfil;
    }

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "DespachoRecepcionVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
//    }
    }

    /**
     * @return the cabecera
     */
    public Cabecerainventario getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabecerainventario cabecera) {
        this.cabecera = cabecera;
    }

    /**
     * @return the formulario
     */
    public Formulario getFormulario() {
        return formulario;
    }

    /**
     * @param formulario the formulario to set
     */
    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * @return the cabeceras
     */
    public LazyDataModel<Cabecerainventario> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Cabecerainventario> cabeceras) {
        this.cabeceras = cabeceras;
    }

    public String buscar() {
        cabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta and o.txid.contabiliza=true";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                // Numero de factura
                if (!((factura == null) || (factura <= 0))) {
                    where += " and o.obligacion.documento =:documento";
                    parametros.put("documento", factura);
                }
                if (!((estado == null) || (estado <= -10))) {
                    where += " and o.estado =:estado";
                    parametros.put("estado", estado);
                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.numero =:numero";
                    parametros.put("numero", numero);
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("marca", observaciones.toUpperCase() + "%");
                }
                if (bodega != null) {
                    where += " and o.bodega=:bodega";
                    parametros.put("bodega", bodega);
                }

                if (tipo != null) {
                    where += " and o.txid=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (contrato != null) {
                    where += " and o.ordencompra.compromiso.contrato=:contrato";
                    parametros.put("contrato", contrato);
                }
                if (proveedoresBean.getProveedor() != null) {
                    where += " and o.ordencompra.compromiso.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getProveedor());
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    total = ejbCabecerainventario.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }

                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                cabeceras.setRowCount(total);
                try {
                    return ejbCabecerainventario.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String modifica() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
        observacionesAsiento = "";
        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("INVENTARIOSUM");
        imagenesBean.Buscar();

        // crear el Aisteno
        renglones = new LinkedList<>();

        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            if (cabecera.getTxid().getContabiliza()) {
                for (Kardexinventario i : listaKardex) {
                    Renglones r = new Renglones();
                    // armar cuenta
                    String cuenta = i.getSuministro().getTipo().getCuenta();
                    String cuentaInversion = i.getSuministro().getTipo().getCuentainversion();
//                    String cuenta = cabecera.getTxid().getIniciales() + i.getSuministro().getTipo().getCuenta();
                    // como coloco el cc
                    Cuentas cuentaUno = getCuentasBean().traerCodigo(cuenta);
                    Cuentas cuentaUnoInversion = getCuentasBean().traerCodigo(cuentaInversion);
                    getCuentasBean().setCuenta(cuentaUno);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                        return null;
                    }
                    getCuentasBean().setCuenta(cuentaUnoInversion);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuentaInversion);
                        return null;
                    }
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(observacionesAsiento);
                    double cantidad = (i.getCantidad() == null ? 0 : i.getCantidad().doubleValue());
                    double cantidadInversion = (i.getCantidadinversion() == null ? 0 : i.getCantidadinversion().doubleValue());
                    double costo = i.getCosto() == null ? 1 : i.getCosto().doubleValue();
                    double costoInversion = i.getCostopinversion() == null ? 1 : i.getCostopinversion().doubleValue();
                    if (!i.getCabecerainventario().getTxid().getIngreso()) {
                        //sale al costo promedio
                        parametros = new HashMap();
                        parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
                        parametros.put("bodega", cabecera.getBodega());
                        parametros.put("suministro", i.getSuministro());
                        List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
                        Bodegasuministro bs = null;
                        for (Bodegasuministro b : listaBodegas) {
                            bs = b;
                        }
                        if (bs == null) {
                            MensajesErrores.advertencia("No existe costo promedio para generar asiento de egreso : " + i.getSuministro().toString());
                            return null;
                        }
                        costo = bs.getCostopromedio();
                        costoInversion = bs.getCostopromedioinversion();
                        i.setCosto(new Float(costo));
                        i.setCostopinversion(new Float(costoInversion));
                    }
                    double valor = costo * cantidad;
                    double valorInversion = costoInversion * cantidadInversion;
                    if (valor != 0) {
                        r.setValor(new BigDecimal(valor));
                        r.setFecha(cabecera.getFecha());
                        r.setCuenta(cuenta);
                        r.setReferencia(observacionesAsiento);
                        estaEnRas(r);
                        //
                        Cuentas ctaReclas = cuentasBean.traerCodigo(cuenta);
                        reclasificacion(ctaReclas, r, 1);
                        //
                        
                        r = new Renglones();
                        // armar cuenta
//                    cuenta = cabecera.getTxid().getCuenta();
                        cuenta = i.getSuministro().getTipo().getCodigoconsumo();
//                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getCuenta();
                        // como coloco el cc
                        cuentaUno = getCuentasBean().traerCodigo(cuenta);
                        getCuentasBean().setCuenta(cuentaUno);
                        if (getCuentasBean().validaCuentaMovimiento()) {
                            MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                            return null;
                        }
                        r.setFecha(cabecera.getFecha());
                        r.setCuenta(cuenta);
                        r.setReferencia(observacionesAsiento);
                        r.setValor(new BigDecimal(valor * -1));
                        estaEnRas(r);
                        //
                        ctaReclas = cuentasBean.traerCodigo(cuenta);
                        reclasificacion(ctaReclas, r, 1);
                        //
                    }
                    // inversion
                    // primera linea
                    if (valorInversion != 0) {
                        r = new Renglones();
                        r.setValor(new BigDecimal(valor));
                        r.setFecha(cabecera.getFecha());
                        r.setCuenta(cuentaInversion);
                        r.setReferencia(observacionesAsiento);
                        r.setValor(new BigDecimal(valorInversion));
                        estaEnRas(r);
                        //
                        Cuentas ctaReclas = cuentasBean.traerCodigo(cuentaInversion);
                        reclasificacion(ctaReclas, r, 1);
                        //
                        // Segunda linea
                        r = new Renglones();
                        // armar cuenta
//                    cuenta = cabecera.getTxid().getCuenta();
                        cuenta = i.getSuministro().getTipo().getCodigoinversion();
//                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getCuenta();
                        // como coloco el cc
                        cuentaUno = getCuentasBean().traerCodigo(cuenta);
                        getCuentasBean().setCuenta(cuentaUno);
                        if (getCuentasBean().validaCuentaMovimiento()) {
                            MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                            return null;
                        }
                        r.setFecha(cabecera.getFecha());
                        r.setCuenta(cuenta);
                        r.setReferencia(observacionesAsiento);
                        r.setValor(new BigDecimal(valorInversion * -1));
                        estaEnRas(r);
                        //
                        ctaReclas = cuentasBean.traerCodigo(cuenta);
                        reclasificacion(ctaReclas, r, 1);
                        //
                    }
                    //
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DespachoRecepcionInventario.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    ///////////////////Reclasificacion
    private void reclasificacion(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia("Reclasificación : " + ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
            }
            rasInvInt.setFecha(new Date());
            rasInvInt.setNombre(cuenta.getNombre());
            if (rasInvInt.getValor().doubleValue() > 0) {
                rasInvInt.setDebitos(rasInvInt.getValor().doubleValue());
            } else {
                rasInvInt.setCreditos(rasInvInt.getValor().doubleValue() * -1);
            }
            estaEnRenglonesReclasificacion(rasInvInt);
            Renglones rasContrapate = new Renglones();
            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia("Reclasificación Contraparte : " + ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasInvInt.setNombre(cuenta.getNombre());
            if (rasInvInt.getValor().doubleValue() > 0) {
                rasInvInt.setDebitos(rasInvInt.getValor().doubleValue());
            } else {
                rasInvInt.setCreditos(rasInvInt.getValor().doubleValue() * -1);
            }
            rasContrapate.setFecha(new Date());
            estaEnRenglonesReclasificacion(rasContrapate);

        }
    }

    private boolean estaEnRenglonesReclasificacion(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
//        for (Renglones r : renglonesReclasificacion) {
//            if ((r.getCuenta().equals(r1.getCuenta()))
//                    && (r.getCentrocosto().equals(r1.getCentrocosto()))
//                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
//                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
//                r.setValor(new BigDecimal(valor));
//                r.setFecha(new Date());
//                return true;
//            }
//        }
        renglonesReclasificacion.add(r1);
        return false;
    }
    /////////////////////////////////

    public String modificaAnt() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
        observacionesAsiento = "";
        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("INVENTARIOSUM");
        imagenesBean.Buscar();

        // crear el Aisteno
        renglones = new LinkedList<>();

        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
            if (cabecera.getTxid().getContabiliza()) {
                for (Kardexinventario i : listaKardex) {
                    Renglones r = new Renglones();
                    // armar cuenta
                    String cuenta = cabecera.getTxid().getIniciales() + i.getSuministro().getTipo().getFamilia().getCodigo();
                    String cuentaInversion = cabecera.getTxid().getInicialesinversion() + i.getSuministro().getTipo().getFamilia().getCodigo();
//                    String cuenta = cabecera.getTxid().getIniciales() + i.getSuministro().getTipo().getCuenta();
                    // como coloco el cc
                    Cuentas cuentaUno = getCuentasBean().traerCodigo(cuenta);
                    Cuentas cuentaUnoInversion = getCuentasBean().traerCodigo(cuentaInversion);
                    getCuentasBean().setCuenta(cuentaUno);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                        return null;
                    }
                    getCuentasBean().setCuenta(cuentaUnoInversion);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuentaInversion);
                        return null;
                    }
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(observacionesAsiento);
                    double cantidad = (i.getCantidad() == null ? 0 : i.getCantidad().doubleValue());
                    double cantidadInversion = (i.getCantidadinversion() == null ? 0 : i.getCantidadinversion().doubleValue());
                    double costo = i.getCosto() == null ? 1 : i.getCosto().doubleValue();
                    if (!i.getCabecerainventario().getTxid().getIngreso()) {
                        //sale al costo promedio
                        parametros = new HashMap();
                        parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
                        parametros.put("bodega", cabecera.getBodega());
                        parametros.put("suministro", i.getSuministro());
                        List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
                        Bodegasuministro bs = null;
                        for (Bodegasuministro b : listaBodegas) {
                            bs = b;
                        }
                        if (bs == null) {
                            MensajesErrores.advertencia("No existe costo promedio para generar asiento de egreso : " + i.getSuministro().toString());
                            return null;
                        }
                        costo = bs.getCostopromedio();
                        i.setCosto(new Float(costo));
                    }
                    double valor = costo * cantidad;
                    double valorInversion = costo * cantidadInversion;
                    r.setValor(new BigDecimal(valor));
                    estaEnRas(r);
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(observacionesAsiento);
                    r = new Renglones();
                    // armar cuenta
//                    cuenta = cabecera.getTxid().getCuenta();
                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getFamilia().getCodigo();
//                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getCuenta();
                    // como coloco el cc
                    cuentaUno = getCuentasBean().traerCodigo(cuenta);
                    getCuentasBean().setCuenta(cuentaUno);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                        return null;
                    }
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(observacionesAsiento);
                    r.setValor(new BigDecimal(valor * -1));
                    estaEnRas(r);
                    // inversion
                    // primera linea
                    r = new Renglones();
                    r.setValor(new BigDecimal(valor));
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuentaInversion);
                    r.setReferencia(observacionesAsiento);
                    r.setValor(new BigDecimal(valorInversion));
                    estaEnRas(r);
                    // Segunda linea
                    r = new Renglones();
                    // armar cuenta
//                    cuenta = cabecera.getTxid().getCuenta();
                    cuenta = cabecera.getTxid().getCuentainversion() + i.getSuministro().getTipo().getFamilia().getCodigo();
//                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getCuenta();
                    // como coloco el cc
                    cuentaUno = getCuentasBean().traerCodigo(cuenta);
                    getCuentasBean().setCuenta(cuentaUno);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                        return null;
                    }
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(observacionesAsiento);
                    r.setValor(new BigDecimal(valorInversion * -1));
                    estaEnRas(r);
                    //
                }

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DespachoRecepcionInventario.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    private boolean estaEnRas(Renglones r1) {
        for (Renglones r : renglones) {
            if (r.getCuenta().equals(r1.getCuenta())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(cabecera.getFecha());
                return true;
            }
        }
        renglones.add(r1);
        return false;
    }

    public String imprimir() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(DespachoRecepcionInventario.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public String grabar() {

        try {
            if ((observacionesAsiento == null) || (observacionesAsiento.isEmpty())) {
                MensajesErrores.advertencia("Ingrese una observación para el asiento");
                return null;
            }
            Codigos codigoReclasificacion = getCodigosBean().traerCodigo("TIPREC", "INVER");
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("No existe configuracion para tipo de asiento de reclasificacion");
                return null;
            }
            String vale = ejbCabecera.validarCierre(cabecera.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            cabecera.setEstado(2);
            cabecera.setFechacontable(new Date());
            if (cabecera.getTxid().getContabiliza()) {
                // asiento
                Tipoasiento ta = cabecera.getTxid().getTipoasiento();
                if (ta == null) {
                    MensajesErrores.fatal("No existe tipo de asiento");
                    return null;
                }
                int numeroAsiento = ta.getUltimo();
                numeroAsiento++;
                ta.setUltimo(numeroAsiento);
                ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
                Cabeceras c = new Cabeceras();
                c.setDescripcion(observacionesAsiento);
                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                c.setDia(new Date());
                c.setTipo(ta);
                c.setNumero(numeroAsiento);
                c.setFecha(cabecera.getFecha());
                c.setIdmodulo(cabecera.getId());
                c.setUsuario(getSeguridadbean().getLogueado().getUserid());
                c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                c.setOpcion("SUMINISTROS");
                ejbCabecera.create(c, getSeguridadbean().getLogueado().getUserid());
                for (Renglones r1 : getRenglones()) {
                    r1.setCabecera(c);
                    r1.setFecha(cabecera.getFecha());
                    r1.setReferencia(observacionesAsiento);
                    ejbRenglones.create(r1, getSeguridadbean().getLogueado().getUserid());
                }
                if (!renglonesReclasificacion.isEmpty()) {
                    Tipoasiento tipoAisentoReclasificacion = null;
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", codigoReclasificacion.getNombre());
                    List<Tipoasiento> listaTipo = ejbTipos.encontarParametros(parametros);
                    for (Tipoasiento t : listaTipo) {
                        tipoAisentoReclasificacion = t;
                    }
                    numeroAsiento = tipoAisentoReclasificacion.getUltimo();
                    numeroAsiento++;
                    tipoAisentoReclasificacion.setUltimo(numeroAsiento);
                    ejbTipos.edit(tipoAisentoReclasificacion, seguridadbean.getLogueado().getUserid());
                    c = new Cabeceras();
                    c.setDescripcion(observaciones);
                    c.setDia(new Date());
                    c.setFecha(cabecera.getFecha());
                    c.setIdmodulo(cabecera.getId());
                    c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    c.setNumero(numeroAsiento);
                    c.setOpcion("ASIENTO_RECLASIFICACION_INVENTARIOS");
                    c.setTipo(tipoAisentoReclasificacion);
                    c.setUsuario(seguridadbean.getLogueado().getUserid());
                    ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
                    for (Renglones r : renglonesReclasificacion) {
                        r.setCabecera(c);
                        r.setFecha(cabecera.getFecha());
                        r.setReferencia(observaciones);
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                    }
                }
                ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioImprimir.insertar();

        buscar();
        return null;
    }

    public String grabarAnt() {

        try {

            // actualizar kardex i borrar los borrados
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
                Map parametros = new HashMap();
                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
                parametros.put("bodega", cabecera.getBodega());
                parametros.put("suministro", k.getSuministro());
                List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
                Bodegasuministro bs = null;
                for (Bodegasuministro b : listaBodegas) {
                    bs = b;
                }
                if (bs == null) {
                    if (!cabecera.getTxid().getIngreso()) {

                        MensajesErrores.fatal("No existe saldo en bodega para poder emitir salida Suministro :" + k.getSuministro().getNombre());
                        return null;
                    }
                    bs = new Bodegasuministro();
                    bs.setBodega(cabecera.getBodega());
                    bs.setSuministro(k.getSuministro());
                    bs.setSaldo(new Float(0));
                    bs.setSaldoinversion(new Float(0));
                    bs.setCostopromedio(new Float(0));
                    bs.setFecha(new Date());
                    bs.setHora(new Date());
                    bs.setUnidad(k.getSuministro().getUnidad());
                }
                if (k.getCantidad() - bs.getSaldo() > 0) {
                    if (!cabecera.getTxid().getIngreso()) {
                        MensajesErrores.fatal("No existe stock en bodega para poder sacar : Suministro" + k.getSuministro().getNombre());
                        return null;
                    }
                }
                ////OJOOOOOO//////
                ejbCabecerainventario.costoPromedior(k, bs, cabecera);
//                costoPromedior(k, bs);
                listaBxS.add(bs);
            }
            for (Kardexinventario k : listaKardex) {
                k.setFecha(new Date());
                k.setHora(new Date());
                if (cabecera.getTxid().getIngreso()) {
                    // toca costear
                } else {
                    // desscuenta de inventario
                }
                ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());
                // mas dificil ya que hay que ver en cardex i es transferencia

            }// fin for kardex
            for (Bodegasuministro b : listaBxS) {
                if (b.getId() == null) {
                    ejbBodSum.create(b, getSeguridadbean().getLogueado().getUserid());
                } else {
                    ejbBodSum.edit(b, getSeguridadbean().getLogueado().getUserid());
                }
            }
            cabecera.setEstado(2);

            // asiento
            Tipoasiento ta = cabecera.getTxid().getTipoasiento();
            if (ta == null) {
                MensajesErrores.fatal("No existe tipo de asiento");
                return null;
            }
            String vale = ejbCabecera.validarCierre(new Date());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
            Cabeceras c = new Cabeceras();
            c.setDescripcion("Asiento de Registro de Suministros:" + cabecera.getObservaciones());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setDia(new Date());
            c.setTipo(ta);
            c.setNumero(numeroAsiento);
            c.setFecha(new Date());
            c.setIdmodulo(cabecera.getId());
            c.setUsuario(getSeguridadbean().getLogueado().getUserid());
            c.setModulo(perfil.getMenu().getIdpadre().getModulo());
            c.setOpcion("SUMINISTROS");
            ejbCabecera.create(c, getSeguridadbean().getLogueado().getUserid());
            for (Renglones r1 : getRenglones()) {
                r1.setCabecera(c);
                ejbRenglones.create(r1, getSeguridadbean().getLogueado().getUserid());
            }
            cabecera.setFechacontable(new Date());
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioImprimir.insertar();

        buscar();
        return null;
    }

//    public void costoPromedior(Kardexinventario k, Bodegasuministro bs) {
//
//        float costo = (k.getCosto() == null ? 0 : k.getCosto());
//        float cantidad = 0;
//        float cantidadInversion = 0;
//        float cantidadTotal = 0;
//        k.setSaldoanterior(bs.getSaldo()==null?0:bs.getSaldo());
//        k.setSaldoanteriorinversion(bs.getSaldoinversion()==null?0:bs.getSaldoinversion());
//        if (cabecera.getTxid().getIngreso()) {
//            cantidad = (bs.getSaldo() == null ? 0 : bs.getSaldo())
//                    + (k.getCantidad() == null ? 0 : k.getCantidad());
//            cantidadInversion = (bs.getSaldoinversion() == null ? 0 : bs.getSaldoinversion())
//                    + (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
//            cantidadTotal = cantidad + cantidadInversion;
//            double saldoTotal = (bs.getSaldo() == null ? 0 : bs.getSaldo()) + (bs.getSaldoinversion() == null ? 0 : bs.getSaldoinversion());
//            costo = (float) (((bs.getCostopromedio() == null ? 0 : bs.getCostopromedio())
//                    * (saldoTotal)
//                    + (k.getCosto() == null ? 0 : k.getCosto())
//                    * k.getCantidad().doubleValue())
//                    / (cantidadTotal == 0 ? 1 : cantidadTotal));
//            bs.setCostopromedio(costo);
//
//        } else {
//            cantidad = (bs.getSaldo() == null ? 0 : bs.getSaldo())
//                    - (k.getCantidad() == null ? 0 : k.getCantidad());
//            cantidadInversion = (bs.getSaldoinversion() == null ? 0 : bs.getSaldoinversion())
//                    - (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
//            costo = bs.getCostopromedio();
//            k.setCosto(bs.getCostopromedio());
//        }
//        bs.setSaldo(cantidad);
//        bs.setSaldoinversion(cantidadInversion);
//        k.setCostopromedio(costo);
//    }
    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
    }

    public Cabecerainventario traer(Integer id) throws ConsultarException {
        return ejbCabecerainventario.find(id);
    }

    /**
     * @return the tipo
     */
    public Txinventarios getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Txinventarios tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the listaKardex
     */
    public List<Kardexinventario> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Kardexinventario> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the kardex
     */
    public Kardexinventario getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexinventario kardex) {
        this.kardex = kardex;
    }

    /**
     * @return the desde
     */
    public Date getDesde() {
        return desde;
    }

    /**
     * @param desde the desde to set
     */
    public void setDesde(Date desde) {
        this.desde = desde;
    }

    /**
     * @return the hasta
     */
    public Date getHasta() {
        return hasta;
    }

    /**
     * @param hasta the hasta to set
     */
    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

    /**
     * @return the bodega
     */
    public Bodegas getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
    }

    /**
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
    }

    /**
     * @return the bodegaDestino
     */
    public Bodegas getBodegaDestino() {
        return bodegaDestino;
    }

    /**
     * @param bodegaDestino the bodegaDestino to set
     */
    public void setBodegaDestino(Bodegas bodegaDestino) {
        this.bodegaDestino = bodegaDestino;
    }

    /**
     * @return the transferencia
     */
    public boolean isTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the transferencia to set
     */
    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
    }

    /**
     * @return the formularioImprimir
     */
    public Formulario getFormularioImprimir() {
        return formularioImprimir;
    }

    /**
     * @param formularioImprimir the formularioImprimir to set
     */
    public void setFormularioImprimir(Formulario formularioImprimir) {
        this.formularioImprimir = formularioImprimir;
    }

    /**
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
    }

    /**
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
    }

    /**
     * @return the observacionesAsiento
     */
    public String getObservacionesAsiento() {
        return observacionesAsiento;
    }

    /**
     * @param observacionesAsiento the observacionesAsiento to set
     */
    public void setObservacionesAsiento(String observacionesAsiento) {
        this.observacionesAsiento = observacionesAsiento;
    }

    /**
     * @return the renglonesReclasificacion
     */
    public List<Renglones> getRenglonesReclasificacion() {
        return renglonesReclasificacion;
    }

    /**
     * @param renglonesReclasificacion the renglonesReclasificacion to set
     */
    public void setRenglonesReclasificacion(List<Renglones> renglonesReclasificacion) {
        this.renglonesReclasificacion = renglonesReclasificacion;
    }

    /**
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }
}
