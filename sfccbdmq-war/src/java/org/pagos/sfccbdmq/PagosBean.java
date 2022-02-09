/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
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
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Tipoegreso;
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
@ManagedBean(name = "pagosSfccbdmq")
@ViewScoped
public class PagosBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PagosBean() {
        Calendar c = Calendar.getInstance();
        pagos = new LazyDataModel<Pagosvencimientos>() {

            @Override
            public List<Pagosvencimientos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Pagosvencimientos> pagos;
    private Pagosvencimientos pago;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String concepto;
    private Tipoegreso tipoEgreso;
    private Codigos tipoDocumento;
    private Contratos contrato;
    private Integer numero;
    private Integer id;
    private Kardexbanco kardex;
    private List<Renglones> renglones;
    private List<Renglones> ras;
    private boolean pagados;
    //

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
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
//            return;
        } else {
            String perfilLocal = (String) params.get("p");
            String nombreForma = "PagosvencimientosVista";
            if (perfilLocal == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
            this.setPerfil(seguridadbean.traerPerfil(perfilLocal));
            if (this.getPerfil() == null) {
                MensajesErrores.fatal("Sin perfil válido");
                seguridadbean.cerraSession();
            }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//            if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//                if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                    MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                    seguridadbean.cerraSession();
//                }
//            }
        }
    }

    public List<Pagosvencimientos> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = " o.fechapago between :desde and :hasta  and o.pagado=:pagado";
//        String where = " o.fechapago between :desde and :hasta and o.obligacion.estado=2 and o.pagado=:pagado";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
        if (proveedorBean.getEmpresa() != null) {
            if (contrato != null) {
                where += " and o.obligacion.contrato=:contrato";
                parametros.put("contrato", contrato);
            } else {
                where += " and o.obligacion.proveedor=:proveedor";
                parametros.put("proveedor", proveedorBean.getEmpresa().getProveedores());
            }
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put("pagado", pagados);
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.obligacion.concepto) like:concepto";
            parametros.put("concepto", getConcepto().toUpperCase() + "%");
        }
        if (tipoEgreso != null) {
            where += " and o.obligacion.tipopago=:tipopago";
            parametros.put("tipopago", tipoEgreso);
        }
        if (tipoDocumento != null) {
            where += " and o.obligacion.tipodocumento=:tipodocumento";
            parametros.put("tipodocumento", tipoDocumento);
            if (numero != null) {
                where += " and o.obligacion.documento=:documento";
                parametros.put("documento", numero);
            }
        }
        if ((id != null) && (id > 0)) {
            parametros = new HashMap();
            where = " and o.obligacion.id=:id";
            parametros.put("id", id);
        }
        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbPagosvencimientos.contar(parametros);
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
        pagos.setRowCount(total);
        try {
            return ejbPagosvencimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        pagos = new LazyDataModel<Pagosvencimientos>() {

            @Override
            public List<Pagosvencimientos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

        return null;
    }

    public String modificar(Pagosvencimientos pago) {
        this.pago = pago;
        if (pago.getFecha() != null) {
            MensajesErrores.advertencia("Pago ya contabilizada no es posible modificar");
            return null;
        }
        if (pago.getObligacion() != null) {
            if (pago.getObligacion().getEstado() != 2) {
                MensajesErrores.advertencia("Obligación no contabilizada");
                return null;
            }
        } else {
            if (pago.getCompromiso() == null) {
                MensajesErrores.advertencia("No existe compromiso");
                return null;
            }
        }
        setKardex(new Kardexbanco());
        if (pago.getCompromiso() == null) {
            getKardex().setBeneficiario(pago.getObligacion().getProveedor().getEmpresa().toString());
            getKardex().setBancotransferencia(pago.getObligacion().getProveedor().getBanco());
        } else {
            // Poner banco nacional de fomento
            getKardex().setBeneficiario(pago.getCompromiso().getBanco().getTransferencia().getNombre());
//            getKardex().setBeneficiario(configuracionBean.getConfiguracion().getNombre());
            getKardex().setBancotransferencia(pago.getCompromiso().getBanco().getTransferencia());
        }

        getKardex().setValor(pago.getValor());
        getKardex().setFechaabono(new Date());
        getKardex().setFechagraba(new Date());
        kardex.setOrigen("PAGOS PROVEEDORES");
        getKardex().setFechamov(new Date());
        getKardex().setUsuariograba(seguridadbean.getLogueado());
        formulario.editar();

        return null;
    }

    public String imprimir(Pagosvencimientos pago) {
//        kardex = pago.getKardexbanco();
        this.pago = pago;
        Map parametros = new HashMap();
        parametros.put(";where", "o.pago=:pago");
        parametros.put("pago", pago);
        List<Kardexbanco> kl;
        try {
            kl = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : kl) {
                kardex = k;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        // traer renglones
        traerRenglones();
        formularioReporte.insertar();

        return null;
    }

    private void traerRenglones() {
        if (pago != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO PROVEEDORES'");
            parametros.put("id", pago.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String grabarKardex() {
        // ver cuentas o ya estan validadas?
        if (getKardex().getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        if ((getKardex().getEgreso() == null) || (kardex.getEgreso() <= 0)) {
            MensajesErrores.advertencia("Necesario un número de egreso");
            return null;
        }
        String vale = ejbCabecera.validarCierre(kardex.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        if (getKardex().getFormapago().getParametros().contains("T")) {
            List<Kardexbanco> listaKardex = new LinkedList<>();
            if (pago.getCompromiso() == null) {
//                if (pago.getObligacion().getProveedor().getLimitetransferencia() == null) {
//                    MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
//                    return null;
//                }
//                if (pago.getValor().doubleValue() > pago.getObligacion().getProveedor().getLimitetransferencia().doubleValue()) {
//                    MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
//                    return null;
//                }
                getKardex().setCuentatrans(pago.getObligacion().getProveedor().getCtabancaria());
                getKardex().setBeneficiario(pago.getObligacion().getProveedor().getNombrebeneficiario());
                getKardex().setBancotransferencia(pago.getObligacion().getProveedor().getBanco());
                getKardex().setTcuentatrans(Integer.parseInt(pago.getObligacion().getProveedor().getTipocta().getParametros()));
                Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
                getCuentasBean().setCuenta(cuentaBanco);
                if (getCuentasBean().validaCuentaMovimiento()) {
                    return null;
                }
                try {
//            kardex.setEstado(2);
                    pago.setFecha(new Date());
                    pago.setPagado(Boolean.TRUE);
                    String obs = "Pago Obligación No:" + pago.getObligacion().getId().toString() + " Proveedor : " + pago.getObligacion().getProveedor().getEmpresa().toString();
                    if (pago.getObligacion().getContrato() != null) {
                        obs += " Contrato " + pago.getObligacion().getContrato().getTitulo() + "No :" + pago.getObligacion().getContrato().getNumero();
                    }
                    obs += "  [" + getKardex().getObservaciones() + "]";
                    kardex.setProveedor(pago.getObligacion().getProveedor());
//                    kardex.setPago(pago);
                    getKardex().setObservaciones(obs);
                    kardex.setEstado(2);
                    // ver los codigos del spi
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.cba!='A'");
                    parametros.put("obligacion", pago.getObligacion());
                    List<Rascompras> rl = ejbRasCompras.encontarParametros(parametros);

                    for (Rascompras rc : rl) {
                        String presupuesto = rc.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                        presupuesto = presupuesto.substring(0, 2);
                        Kardexbanco kb = new Kardexbanco();
                        kb.setProveedor(kardex.getProveedor());
//                        kb.setPago(pago);
                        kb.setObservaciones(obs);
                        kb.setFormapago(kardex.getFormapago());
                        kb.setEstado(2);
                        kb.setBanco(kardex.getBanco());
                        kb.setBeneficiario(kardex.getBeneficiario());
                        kb.setCuentatrans(kardex.getCuentatrans());
                        kb.setBancotransferencia(kardex.getBancotransferencia());
                        kb.setTcuentatrans(kardex.getTcuentatrans());
                        double valor = rc.getValor().doubleValue() + rc.getValorimpuesto().doubleValue() - rc.getValorret().doubleValue() - rc.getVretimpuesto().doubleValue();
                        kb.setValor(new BigDecimal(valor));
                        if ((presupuesto.equals("53"))) {
                            kb.setCodigospi("40102");
                        } else {
                            kb.setCodigospi("40200");
                        }
                        ponerListaKardex(listaKardex, kb);

                    }
                    for (Kardexbanco kb : listaKardex) {
//                        kb.setPago(pago);
                        ejbKardex.create(kb, seguridadbean.getLogueado().getUserid());
                    }

//                    pago.setKardexbanco(getKardex());
                    ejbPagosvencimientos.edit(pago, seguridadbean.getLogueado().getUserid());
                    Tipoasiento ta = getKardex().getTipomov().getTipoasiento();

                    int numeroAsiento = ta.getUltimo();
                    numeroAsiento++;
                    ta.setUltimo(numeroAsiento);
                    ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
                    Cabeceras cab = new Cabeceras();
                    cab.setDescripcion(getKardex().getObservaciones());
                    cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    cab.setDia(new Date());
                    cab.setTipo(getKardex().getTipomov().getTipoasiento());
                    cab.setNumero(numeroAsiento);
                    cab.setFecha(kardex.getFechamov());
                    cab.setIdmodulo(pago.getId());
                    cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    cab.setOpcion("PAGO PROVEEDORES");
                    ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
                    // Hacer el asiento en base a la suma del rascompras

                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setCabecera(cab);
                    r1.setReferencia("Pago Proveedores Banco :" + getKardex().getBanco().getNombre()
                            + " de la Cuenta " + (getKardex().getBanco().getCorriente() ? " Corriente No: " : " Ahorros No: ")
                            + getKardex().getBanco().getNumerocuenta());
                    r1.setValor(new BigDecimal(pago.getValor().doubleValue() * -1));
                    r1.setCuenta(cuentaBanco.getCodigo());
                    r1.setPresupuesto(cuentaBanco.getPresupuesto());
                    r1.setFecha(cab.getFecha());
                    if (cuentaBanco.getAuxiliares() != null) {
                        r1.setAuxiliar(pago.getObligacion().getProveedor().getEmpresa().getRuc());
                    }
//                    r1.setFecha(new Date());
                    r1.setPresupuesto(cuentaBanco.getPresupuesto());
                    ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                    // traer rascompras ahi esta la cuenta y la forma de distribuir

                    double totalObligacion = 0;
                    ras = new LinkedList<>();
                    for (Rascompras rc : rl) {
                        Renglones r = new Renglones(); // reglon de provaeedores
                        r.setCabecera(cab);
                        r.setReferencia(obs);
                        r.setValor(rc.getValor());
                        r.setCuenta(rc.getCuenta());
                        Cuentas cuentaP = getCuentasBean().traerCodigo(rc.getCuenta());
                        r.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            r.setAuxiliar(pago.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        estaEnRas(r);
                        r.setFecha(cab.getFecha());
                        totalObligacion += rc.getValor().doubleValue();
                        if (rc.getRetencion() != null) {
                            // armada la cuenta de retención
                            r = new Renglones(); // reglon de provaeedores
                            r.setCabecera(cab);
                            r.setReferencia(obs);
                            r.setValor(new BigDecimal(rc.getValorret().doubleValue() * -1));
                            r.setCuenta(rc.getCuenta());
                            cuentaP = getCuentasBean().traerCodigo(rc.getCuenta());
                            r.setPresupuesto(cuentaP.getPresupuesto());
                            if (cuentaP.getAuxiliares() != null) {
                                r.setAuxiliar(pago.getObligacion().getProveedor().getEmpresa().getRuc());
                            }
                            estaEnRas(r);
                            r.setFecha(cab.getFecha());
                            totalObligacion += rc.getValorret().doubleValue() * -1;
                        }
                    }
                    double valorPago = pago.getValor().doubleValue();
                    renglones = new LinkedList<>();
                    for (Renglones r : ras) {
                        r.setCabecera(cab);
                        double valor = r.getValor().doubleValue();
                        double valorColocar = valorPago * valor / totalObligacion;
                        r.setValor(new BigDecimal(valorColocar));
                        r.setFecha(cab.getFecha());
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                        getRenglones().add(r);
                    }
                    getRenglones().add(r1);
                    getFormularioReporte().editar();
                    formulario.cancelar();
                } catch (InsertarException | GrabarException | ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
// el resto es fondo rotativo
                getKardex().setCuentatrans(pago.getCompromiso().getBanco().getNumerocuenta());
                getKardex().setBeneficiario(pago.getCompromiso().getBanco().getNombre());
                getKardex().setBancotransferencia(pago.getCompromiso().getBanco().getTransferencia());
                if (pago.getCompromiso().getBanco().getCorriente()) {
                    getKardex().setTcuentatrans(1);
                } else {
                    getKardex().setTcuentatrans(2);
                }
                Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
                getCuentasBean().setCuenta(cuentaBanco);
                if (getCuentasBean().validaCuentaMovimiento()) {
                    return null;
                }
                try {
//            kardex.setEstado(2);
                    pago.setFecha(new Date());
                    pago.setPagado(Boolean.TRUE);
                    String responsable = "";
                    if (pago.getCompromiso().getResponsable() != null) {
                        responsable = " Responsable : " + pago.getCompromiso().getResponsable().getEntidad().toString();
                    }
                    String obs = "Pago Fondo Rotativo No:" + pago.getCompromiso().getId().toString() + responsable;
                    obs += "  [" + getKardex().getObservaciones() + "]";
                    kardex.setProveedor(null);
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso.compromiso=:compromiso and o.cba!='A'");
                    parametros.put("compromiso", pago.getCompromiso());
                    List<Rascompras> rl = ejbRasCompras.encontarParametros(parametros);
//                    kardex.setPago(pago);
                    getKardex().setObservaciones(obs);
                    kardex.setEstado(2);
                    for (Rascompras rc : rl) {
                        String presupuesto = rc.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                        presupuesto = presupuesto.substring(0, 2);
                        Kardexbanco kb = new Kardexbanco();
                        kb.setProveedor(kardex.getProveedor());
//                        kb.setPago(pago);
                        kb.setObservaciones(obs);
                        kb.setEstado(2);
                        kb.setBanco(kardex.getBanco());
                        kb.setFormapago(kardex.getFormapago());
                        kb.setBeneficiario(kardex.getBeneficiario());
                        kb.setCuentatrans(kardex.getCuentatrans());
                        kb.setBancotransferencia(kardex.getBancotransferencia());
                        kb.setTcuentatrans(kardex.getTcuentatrans());
//                        kb.setEstado(kardex.getEstado());
                        double valor = rc.getValor().doubleValue() + rc.getValorimpuesto().doubleValue() - rc.getValorret().doubleValue() - rc.getVretimpuesto().doubleValue();
                        kb.setValor(new BigDecimal(valor));
                        if ((presupuesto.equals("53"))) {
                            kb.setCodigospi("40102");
                        } else {
                            kb.setCodigospi("40200");
                        }
                        ponerListaKardex(listaKardex, kb);

                    }
                    for (Kardexbanco kb : listaKardex) {
//                        kb.setPago(pago);
                        ejbKardex.create(kb, seguridadbean.getLogueado().getUserid());
                    }
                    ejbPagosvencimientos.edit(pago, seguridadbean.getLogueado().getUserid());
                    Tipoasiento ta = getKardex().getTipomov().getTipoasiento();
                    int numeroAsiento = ta.getUltimo();
                    numeroAsiento++;
                    ta.setUltimo(numeroAsiento);
                    ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
                    Cabeceras cab = new Cabeceras();
                    cab.setDescripcion("REP FONDO ROTATIVO" + getKardex().getObservaciones());
                    cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    cab.setDia(new Date());
                    cab.setTipo(getKardex().getTipomov().getTipoasiento());
                    cab.setNumero(numeroAsiento);
                    cab.setFecha(kardex.getFechamov());
                    cab.setIdmodulo(pago.getId());
                    cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    cab.setOpcion("PAGO PROVEEDORES");
                    ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
                    // Hacer el asiento en base a la suma del rascompras

                    Renglones r1 = new Renglones(); // reglon de banco
                    r1.setCabecera(cab);
                    r1.setReferencia("Pago Proveedores Banco :" + getKardex().getBanco().getNombre()
                            + " de la Cuenta " + (getKardex().getBanco().getCorriente() ? " Corriente No: " : " Ahorros No: ")
                            + getKardex().getBanco().getNumerocuenta());
                    r1.setValor(new BigDecimal(pago.getValor().doubleValue() * -1));
                    r1.setCuenta(cuentaBanco.getCodigo());
                    r1.setPresupuesto(cuentaBanco.getPresupuesto());
//                    r1.setFecha(new Date());
                    if (cuentaBanco.getAuxiliares() != null) {
                        r1.setAuxiliar(pago.getObligacion().getProveedor().getEmpresa().getRuc());
                    }
                    r1.setFecha(cab.getFecha());
                    r1.setPresupuesto(cuentaBanco.getPresupuesto());
                    ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                    // traer rascompras ahi esta la cuenta y la forma de distribuir

                    double totalObligacion = 0;
                    ras = new LinkedList<>();
                    for (Rascompras rc : rl) {
                        Renglones r = new Renglones(); // reglon de provaeedores
                        r.setCabecera(cab);
                        r.setReferencia(obs);
                        double valor = rc.getValor().doubleValue() + rc.getValorimpuesto().doubleValue() - rc.getValorret().doubleValue() - rc.getVretimpuesto().doubleValue();
                        r.setValor(new BigDecimal(valor));
                        r.setCuenta(rc.getCuenta());
                        Cuentas cuentaP = getCuentasBean().traerCodigo(rc.getCuenta());
                        r.setPresupuesto(cuentaP.getPresupuesto());
                        if (cuentaP.getAuxiliares() != null) {
                            r.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                        }
                        estaEnRas(r);
                        r.setFecha(cab.getFecha());
                        totalObligacion += valor;
//                        if (rc.getRetencion() != null) {
//                            // armada la cuenta de retención
//                            r = new Renglones(); // reglon de provaeedores
//                            r.setCabecera(cab);
//                            r.setReferencia(obs);
//                            r.setValor(new BigDecimal(rc.getValorret().doubleValue() * -1));
//                            r.setCuenta(rc.getCuenta());
//                            cuentaP = getCuentasBean().traerCodigo(rc.getCuenta());
//                            r.setPresupuesto(cuentaP.getPresupuesto());
//                            if (cuentaP.getAuxiliares() != null) {
//                                r.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
//                            }
//                            estaEnRas(r);
//                            totalObligacion += rc.getValorret().doubleValue() * -1;
//                        }
                    }
                    double valorPago = pago.getValor().doubleValue();
                    renglones = new LinkedList<>();
                    for (Renglones r : ras) {
                        r.setCabecera(cab);
                        double valor = r.getValor().doubleValue();
                        double valorColocar = valorPago * valor / totalObligacion;
                        r.setValor(new BigDecimal(valorColocar));
                        r.setFecha(cab.getFecha());
                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                        getRenglones().add(r);
                    }
                    getRenglones().add(r1);
                    getFormularioReporte().editar();
                    formulario.cancelar();
                } catch (InsertarException | GrabarException | ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        // colocar observaciones
//        renglones = new LinkedList<>();
        // ver en proveedor
//        String clasificacionProveedor = pago.getObligacion().getProveedor().getClasificacion().getParametros();
//        if (clasificacionProveedor == null) {
//            MensajesErrores.advertencia("Clasificación del proveedor mal configurado");
//            return null;
//        }
//        if (clasificacionProveedor.indexOf("#") <= 0) {
//            MensajesErrores.advertencia("Clasificación del proveedor mal configurado");
//            return null;
//        }
//        String[] cuentasProveedor = clasificacionProveedor.split("#");
//        if (cuentasProveedor.length == 0) {
//            MensajesErrores.advertencia("Clasificación del proveedor mal configurado");
//            return null;
//        }
//        Cuentas cuentaAnticipo = getCuentasBean().traerCodigo(cuentasProveedor[0]);
//        getCuentasBean().setCuenta(cuentaAnticipo);
//        if (getCuentasBean().validaCuentaMovimiento()) {
//            return null;
//        }
        return null;
    }

    private void ponerListaKardex(List<Kardexbanco> listaKardex, Kardexbanco kb) {
        for (Kardexbanco k : listaKardex) {
            if (k.getCodigospi().equals(kb.getCodigospi())) {
                k.setValor(new BigDecimal(k.getValor().doubleValue() + kb.getValor().doubleValue()));
                return;
            }
        }
        listaKardex.add(kb);
    }

    private boolean estaEnRas(Renglones r1) {
        for (Renglones r : ras) {
            if (r.getCuenta().equals(r1.getCuenta())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(new Date());
                return true;
            }
        }
        ras.add(r1);
        return false;
    }

    public String eliminar(Pagosvencimientos pago) {
        this.pago = pago;

        getFormulario().eliminar();

        return null;
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

    /**
     * @return the pagos
     */
    public LazyDataModel<Pagosvencimientos> getPagosvencimientos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagosvencimientos(LazyDataModel<Pagosvencimientos> pagos) {
        this.pagos = pagos;
    }

    /**
     * @return the pago
     */
    public Pagosvencimientos getPago() {
        return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(Pagosvencimientos pago) {
        this.pago = pago;
    }

    /**
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
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
     * @return the tipoEgreso
     */
    public Tipoegreso getTipoEgreso() {
        return tipoEgreso;
    }

    /**
     * @param tipoEgreso the tipoEgreso to set
     */
    public void setTipoEgreso(Tipoegreso tipoEgreso) {
        this.tipoEgreso = tipoEgreso;
    }

    /**
     * @return the tipoDocumento
     */
    public Codigos getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento the tipoDocumento to set
     */
    public void setTipoDocumento(Codigos tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
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

    public String cancelar() {
        formulario.cancelar();
        return "ComprasVista.jsf?faces-redirect=true";
    }

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
     * @return the concepto
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * @return the kardex
     */
    public Kardexbanco getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
    }

    /**
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
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

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(pago.getValor().doubleValue());
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(kardex.getValor().doubleValue());
    }

    /**
     * @return the pagados
     */
    public boolean isPagados() {
        return pagados;
    }

    /**
     * @param pagados the pagados to set
     */
    public void setPagados(boolean pagados) {
        this.pagados = pagados;
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
}
