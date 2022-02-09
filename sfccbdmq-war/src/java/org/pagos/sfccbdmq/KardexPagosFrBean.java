/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Tipoegreso;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "kardexPagosFrSfccbdmq")
@ViewScoped
public class KardexPagosFrBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public KardexPagosFrBean() {
        Calendar c = Calendar.getInstance();
        listadoKardex = new LazyDataModel<Pagosvencimientos>() {
            @Override
            public List<Pagosvencimientos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Pagosvencimientos> listadoKardex;
    private Pagosvencimientos pago;
//    private Pagosvencimientos pago;
    private List<Pagosvencimientos> pagos;
    private List<Pagosvencimientos> pagosb;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioAnticipo = new Formulario();
    private Formulario formularioPago = new Formulario();
    private Formulario formularioCuentas = new Formulario();
    private Cabeceras asiento;
    private Cabeceras asientoReclasificacion;
    private String comprobanteEgreso = "Comprobante de Egreso";
    private Kardexbanco kardexRebote;
    private Formulario formularioRebote = new Formulario();
    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @EJB
    private AnticiposFacade ejbAnticipos;
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
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private FormatosFacade ejbFormatos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
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
    private Bancos banco;
    private List<Renglones> renglones;
    private List<Renglones> ras;
    private List<Renglones> rasReclasificaion;
    private List<Renglones> listaCuentasRenglones;
    private boolean pagados;
    private boolean proveedorBeneficiario;
    private double cuantoAnticipo;
    private Anticipos anticipoAplicar;
    private Resource reporte;
    private Renglones renglon;
    private List<Cuentas> cuentas;
    private Cuentas ctaInicial;
    private int ver = 0;
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
            String nombreForma = "PagosvencimientosFrVista";
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
            parametros.put(";orden", "o.kardexbanco.fechamov desc,o.kardexbanco.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = " o.kardexbanco.fechamov between :desde and :hasta and o.kardexbanco.estado in (0,1,2,3) and o.compromiso is not null";
//        String where = " o.kardexbanco.fechamov between :desde and :hasta and o.kardexbanco.estado>=0 and o.compromiso is not null";
//        String where = " o.fechapago between :desde and :hasta and o.obligacion.estado=2 and o.pagado=:pagado";
//                        + "and o.detcertificacion.certificacion.anulado=false ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }

        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
//        parametros.put("pagado", pagados);
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.kardexbanco.observaciones) like:concepto";
            parametros.put("concepto", getConcepto().toUpperCase() + "%");
        }
        if (tipoEgreso != null) {
            where += " and o.kardexbanco.obligacion.tipopago=:tipopago";
            parametros.put("tipopago", tipoEgreso);
        }
        if (numero != null) {
            where += " and o.kardexbanco.egreso=:documento";
            parametros.put("documento", numero);
        }
//        if (tipoDocumento != null) {
//            where += " and o.obligacion.tipodocumento=:tipodocumento";
//            parametros.put("tipodocumento", tipoDocumento);
//            
//        }
        if ((id != null) && (id > 0)) {
            parametros = new HashMap();
            where = " and o.kardexbanco.id=:id";
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
        listadoKardex.setRowCount(total);
        try {
            return ejbPagosvencimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        listadoKardex = new LazyDataModel<Pagosvencimientos>() {

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
        listaCuentasRenglones = new LinkedList<>();
        getKardex().setValor(pago.getValor());
        getKardex().setFechaabono(new Date());
        getKardex().setFechagraba(new Date());
        kardex.setOrigen("PAGO PROVEEDORES FR");
        getKardex().setFechamov(new Date());
        getKardex().setUsuariograba(seguridadbean.getLogueado());
        formulario.editar();

        return null;
    }

    private void traerRenglones() {
        if (kardex != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO PROVEEDORES'");
            parametros.put("id", kardex.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                // traer reclasificacion
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO PROVEEDORES RECLASIFICACION'");
                parametros.put("id", kardex.getId());
                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                rasReclasificaion = ejbRenglones.encontarParametros(parametros);
//                formularioReporte.insertar();
                Collections.sort(renglones, new valorComparator());
                Collections.sort(rasReclasificaion, new valorComparator());
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    public String imprimirKardex(Pagosvencimientos p) {

//        pagos = new LinkedList<>();
        pagosb = new LinkedList<>();
//        formulario.editar();
        setKardex(p.getKardexbanco());
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        try {
            pagos = ejbPagosvencimientos.encontarParametros(parametros);
            // un paso mas traer las obligaciones de cada compromiso y generar un pago
            List<Pagosvencimientos> listaPagos = new LinkedList<>();
            for (Pagosvencimientos pag : pagos) {
                parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put("compromiso", pag.getCompromiso());
                List<Obligaciones> lobli = ejbObligaciones.encontarParametros(parametros);
                for (Obligaciones obli : lobli) {
                    Pagosvencimientos pagInterno = new Pagosvencimientos();
                    pagInterno.setValor(obli.getApagar());
                    pagInterno.setObligacion(obli);
                    listaPagos.add(pagInterno);
                }
            }
            traerRenglones();
            List<Renglones> listaRenglonesInternos = new LinkedList<>();
            for (Renglones r : renglones) {
                Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                r.setNombre(c.getNombre());
                double valor = r.getValor().doubleValue();
                r.setListaPagos(listaPagos);
                if (valor > 0) {
                    r.setDebitos(valor);
                } else {
                    r.setCreditos(valor * -1);
                }
                if (c.getAuxiliares() != null) {
                    VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                    r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                }
                listaRenglonesInternos.add(r);
            }
            for (Renglones r : rasReclasificaion) {
                Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                r.setNombre(c.getNombre());
                double valor = r.getValor().doubleValue();
                r.setListaPagos(listaPagos);
                if (valor > 0) {
                    r.setDebitos(valor);
                } else {
                    r.setCreditos(valor * -1);
                }
                if (c.getAuxiliares() != null) {
                    VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                    r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                }
                listaRenglonesInternos.add(r);
            }
            parametros = new HashMap();
            parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
            parametros.put("empresa", kardex.getBeneficiario());
            parametros.put("nombrelogo", "Comprobante de Egreso No : " + kardex.getId());
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", kardex.getFechamov());
            parametros.put("documento", kardex.getEgreso().toString());
            parametros.put("modulo", getCuantoStr());
            parametros.put("descripcion", kardex.getObservaciones());
            parametros.put("obligaciones", getValorStr());
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
            parametros.put("SUBREPORT_DIR", realPath + "/");
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
                    listaRenglonesInternos, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf"));

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioReporte.insertar();
        return null;

    }

//    private void ponerListaKardex(List<Kardexbanco> listaKardex, Kardexbanco kb) {
//        for (Kardexbanco k : listaKardex) {
//            if (k.getCodigospi().equals(kb.getCodigospi())) {
//                k.setValor(new BigDecimal(k.getValor().doubleValue() + kb.getValor().doubleValue()));
//                return;
//            }
//        }
//        listaKardex.add(kb);
//    }
    private boolean estaEnRas(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : ras) {
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(kardex.getFechamov());
                return true;
            }
        }
        ras.add(r1);
        return false;
    }

    private boolean estaEnRenglones(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglones) {
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(kardex.getFechamov());
                return true;
            }
        }
        renglones.add(r1);
        return false;
    }

    private boolean estaEnRenglonesReclasificacion(Renglones r1) {
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : rasReclasificaion) {
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
                if (r1.getValor() == null) {
                    r1.setValor(BigDecimal.ZERO);
                }
                if (r.getValor() == null) {
                    r.setValor(BigDecimal.ZERO);
                }
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(kardex.getFechamov());
                return true;
            }
        }
        rasReclasificaion.add(r1);
        return false;
    }

    public String eliminar(Pagosvencimientos pago) {
        pagosb.add(pago);
        pagos.remove(formularioPago.getFila().getRowIndex());
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
    public List<Pagosvencimientos> getPagos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagos(List<Pagosvencimientos> pagos) {
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
        return ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue());
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

    /**
     * @return the listadoKardex
     */
    public LazyDataModel<Pagosvencimientos> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(LazyDataModel<Pagosvencimientos> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    // inicio el nuevo codigo luego boorro el resto
    public String nuevoKardex() {
        entidadBean.setEntidad(null);
        entidadBean.setApellidos(null);
        pagos = new LinkedList<>();
        pagosb = new LinkedList<>();
        formulario.insertar();
        setKardex(new Kardexbanco());
        getKardex().setValor(new BigDecimal(0));
        getKardex().setFechaabono(new Date());
        getKardex().setFechagraba(new Date());
        getKardex().setFechamov(new Date());
        kardex.setOrigen("PAGO PROVEEDORES FR");
        getKardex().setUsuariograba(seguridadbean.getLogueado());
        return null;
    }

    public String borraKardex(Pagosvencimientos p) {
        if (kardex.getSpi() != null) {
            MensajesErrores.informacion("No es posible borrar pago ya registrado en SPI");
            return null;
        }
//        pagos = new LinkedList<>();
        pagosb = new LinkedList<>();
        formulario.eliminar();
        setKardex(p.getKardexbanco());
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        try {
            pagos = ejbPagosvencimientos.encontarParametros(parametros);
            traerRenglones();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String eliminar() {
        // editar pagos
        try {
            for (Pagosvencimientos p : pagos) {

                p.setFecha(null);
                p.setPagado(false);
                p.setKardexbanco(null);
                ejbPagosvencimientos.edit(p, seguridadbean.getLogueado().getUserid());
//                ejbRasCompras.quitarPagado(p.getObligacion());
            }
            // borrar asientos 
            Cabeceras c = null;
            for (Renglones r : renglones) {
                c = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());

            }
            if (c != null) {
                ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
            }
            c = null;
            for (Renglones r : rasReclasificaion) {
                c = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
            }
            // borra kardex
            ejbKardex.remove(kardex, seguridadbean.getLogueado().getUserid());
            formulario.cancelar();
        } catch (GrabarException | BorrarException ex) {
            Logger.getLogger(KardexPagosFrBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        return null;
    }

    private boolean validar() {
        if (pagos.isEmpty()) {
            MensajesErrores.fatal("Es necesario seleccionar algún");
            return true;
        }
        if (getKardex().getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return true;
        }
        if ((getKardex().getEgreso() == null) || (kardex.getEgreso() <= 0)) {
            MensajesErrores.advertencia("Necesario un número de egreso");
            return true;
        }

        // validar las líneas dependiendo de si es transferencia
        double valorAtransferir = 0;
//        double limite = proveedorBean.getEmpresa().getProveedores().getLimitetransferencia().doubleValue();
//        if (getKardex().getFormapago().getParametros().contains("T")) {
//            if (proveedorBean.getEmpresa().getProveedores().getLimitetransferencia() == null) {
//                MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
//                return true;
//            }
//            for (Pagosvencimientos p : pagos) {
//                valorAtransferir += p.getValor().doubleValue();
//            }
//            kardex.setValor(new BigDecimal(valorAtransferir));
//            if (valorAtransferir > limite) {
//                MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
//                return true;
//            }
//        }
        return false;
    }

    public String insertarKardex() {
        if (proveedorBeneficiario) {
            if (proveedoresBean.getEmpresa() == null) {
                MensajesErrores.advertencia("Favor seleccione un proveedor beneficiario");
                return null;
            }
        } else {
            if (entidadBean.getEntidad() == null) {
                MensajesErrores.advertencia("Favor seleccione un empleado beneficiario");
                return null;
            }
        }
        if (validar()) {
            return null;
        }
        if (validarCuentas()) {
            return null;
        }
        try {
            if (proveedorBeneficiario) {
                kardex.setUsuarioentrega(seguridadbean.getLogueado());
                kardex.setBeneficiario(proveedoresBean.getEmpresa().getRuc());
                kardex.setBancotransferencia(proveedoresBean.getProveedor().getBanco());
                kardex.setCuentatrans(proveedoresBean.getProveedor().getCtabancaria());
            } else {
                kardex.setUsuarioentrega(entidadBean.getEntidad());
                kardex.setBeneficiario(entidadBean.getEntidad().getPin());
                kardex.setBancotransferencia(traerBanco(entidadBean.getEntidad().getEmpleados()));
                kardex.setCuentatrans(traerCuenta(entidadBean.getEntidad().getEmpleados()));
            }
//            kardex.setUsuarioentrega(entidadBean.getEntidad());
//            kardex.setBeneficiario(entidadBean.getEntidad().toString());
            kardex.setEstado(0);
            ejbKardex.create(kardex, seguridadbean.getLogueado().getUserid());
            double valor = 0;
            for (Pagosvencimientos p : pagos) {
                p.setKardexbanco(kardex);
                p.setPagado(true);
                p.setFecha(new Date());
                valor += p.getValor().doubleValue();
//                kardex.setBancotransferencia(p.getCompromiso().getBanco().getTransferencia());
//                kardex.setCuentatrans(p.getCompromiso().getBanco().getNumerocuenta());
                ejbPagosvencimientos.edit(p, seguridadbean.getLogueado().getUserid());
//                ejbRasCompras.ponerPagado(p.getObligacion());
            }
            for (Renglones r : listaCuentasRenglones) {
                valor += r.getValor().doubleValue();
            }
            kardex.setValor(new BigDecimal(valor));
            // armar contabilizacion
            contabilizarPago();
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosFrBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // ver si cada pago tiene la información correcta del proveedor an
        return null;
    }

    /**
     * @return the formularioAnticipo
     */
    public Formulario getFormularioAnticipo() {
        return formularioAnticipo;
    }

    /**
     * @param formularioAnticipo the formularioAnticipo to set
     */
    public void setFormularioAnticipo(Formulario formularioAnticipo) {
        this.formularioAnticipo = formularioAnticipo;
    }

    public Pagosvencimientos traer(Integer id) throws ConsultarException {
        return ejbPagosvencimientos.find(id);
    }

    public SelectItem[] getComboPagosObligacion() {
//        if (entidadBean.getEntidad() == null) {
//            return null;
//        }
        if (getBanco() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso.banco=:banco"
                //        parametros.put(";where", "o.compromiso.responsable=:responsable and o.compromiso.banco=:banco"
                + " and o.pagado=false and o.kardexbanco is null");
//        parametros.put("responsable", entidadBean.getEntidad().getEmpleados());
        parametros.put("banco", banco);
        try {
            List<Pagosvencimientos> pl = ejbPagosvencimientos.encontarParametros(parametros);
            List<Pagosvencimientos> plAux = new LinkedList<>();
            for (Pagosvencimientos p : pl) {
                if (!estaPago(p)) {
                    plAux.add(p);
                }
            }
            int size = plAux.size();
            SelectItem[] items = new SelectItem[size + 1];
            items[0] = new SelectItem(0, "Seleccione uno");
            int i = 1;
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            for (Pagosvencimientos x : plAux) {
                x.setValoranticipo(BigDecimal.ZERO);
                items[i++] = new SelectItem(x, x.getCompromiso().getMotivo() + " (" + df.format(x.getValor()) + ")");
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosFrBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean estaPago(Pagosvencimientos p) {
        for (Pagosvencimientos p1 : pagos) {
            if (p1.equals(p)) {
                return true;
            }
        }
        for (Pagosvencimientos p1 : pagosb) {
            if (p1.getId() != null) {
                if (p1.equals(p)) {
                    return false;
                }
            }
        }
        return false;
    }

    public String agregarPago() {
        if (pago == null) {
            MensajesErrores.advertencia("Seleccione una obligación");
            return null;
        }
        pagos.add(pago);
        return null;
    }

    private boolean validarCuentas() {
        try {
            // traer el tipo de codgios para reclasificacion
            Codigos codigoReclasificacion = codigosBean.traerCodigo(Constantes.TIPO_RECLASIFICACION, "RET");
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("No existe creado códigos para reclasificación de cuentas");
                return true;
            }
            Formatos f = ejbObligaciones.traerFormato();
            if (f == null) {
                MensajesErrores.advertencia("No existe formato de retenciones");
                return true;
            }
            Tipoasiento ta = getKardex().getTipomov().getTipoasiento();

            // buscar tipo de siento de reclasificaion
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoReclasificacion.getNombre());
            List<Tipoasiento> tl = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento taReclafificacion = null;
            for (Tipoasiento t : tl) {
                taReclafificacion = t;
            }
            if (taReclafificacion == null) {
                MensajesErrores.fatal("No existe tipo  para reclasificacion");
                return true;
            }

            Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
            if (cuentaBanco == null) {
                MensajesErrores.fatal("Cuenta del Banco no existe en plan de cuentas : " + getKardex().getBanco().getCuenta());
                return true;
            }
            for (Pagosvencimientos p : pagos) {
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion.compromiso=:obligacion and o.cba!='A'");
                parametros.put("obligacion", p.getCompromiso());
                List<Rascompras> rl = ejbRasCompras.encontarParametros(parametros);
                Calendar c = Calendar.getInstance();
                int mesActual = c.get(Calendar.MONTH);
                c.setTime(p.getCompromiso().getFecha());
                int mesObligacion = c.get(Calendar.MONTH);
                for (Rascompras rc : rl) {
                    String presupuesto = rc.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                    presupuesto = presupuesto.substring(0, 2);
                    if (mesActual == mesObligacion) {
                        // genear el asiento de reclasificacion
                        // ver el algoritmo de reclasificacion en base a como se armo el siento de las retenciones
                        if (rc.getRetencion() != null) {
                            String ctaRetencion = f.getCxpinicio() + presupuesto + rc.getRetencion().getCuenta() + rc.getRetencion().getCodigo();
                            String ctaContraria = codigoReclasificacion.getParametros() + rc.getRetencion().getReclasificacion() + rc.getRetencion().getCodigo();
                            Cuentas cuentaRet = getCuentasBean().traerCodigo(ctaRetencion);
                            Cuentas cuentaContraria = getCuentasBean().traerCodigo(ctaContraria);
                            if (cuentaRet == null) {
                                MensajesErrores.fatal("Cuenta de  retención no existe en plan de cuentas : " + ctaRetencion);
                                return true;
                            }
                            if (cuentaContraria == null) {
                                MensajesErrores.fatal("Cuenta de  retención reclasificación no existe en plan de cuentas : " + ctaContraria);
                                return true;
                            }

                            if (rc.getRetimpuesto() != null) {
                                // armada la cuenta de retención
                                ctaRetencion = f.getCxpinicio() + presupuesto + rc.getRetimpuesto().getCuenta() + rc.getRetimpuesto().getCodigo();
                                ctaContraria = codigoReclasificacion.getParametros() + rc.getRetimpuesto().getReclasificacion() + rc.getRetimpuesto().getCodigo();
                                cuentaRet = getCuentasBean().traerCodigo(ctaRetencion);
                                cuentaContraria = getCuentasBean().traerCodigo(ctaContraria);
                                if (cuentaRet == null) {
                                    MensajesErrores.fatal("Cuenta de  retención no existe en plan de cuentas : " + ctaRetencion);
                                    return true;
                                }
                                if (cuentaContraria == null) {
                                    MensajesErrores.fatal("Cuenta de  retención reclasificación no existe en plan de cuentas : " + ctaContraria);
                                    return true;
                                }
                                // es retencion
                            }// fin if retencion de iva
                        } // fin if retencion nula
                    } // fin if de reclasificacion es otro asiento
                }
//                    pago.setKardexbanco(getKardex());
                if (p.getAnticipo() != null) {
                    Cuentas cuentaP = getCuentasBean().traerCodigo(p.getAnticipo().getCuenta());
                    if (cuentaP == null) {
                        MensajesErrores.fatal("Cuenta anticipo no existe en plan de cuentas : " + p.getAnticipo().getCuenta());
                        return true;
                    }
                }
                for (Rascompras rc : rl) {
                    Cuentas cuentaP = getCuentasBean().traerCodigo(rc.getCuenta());
                    if (cuentaP == null) {
                        MensajesErrores.fatal("Cuenta no existe en plan de cuentas : " + p.getAnticipo().getCuenta());
                        return true;
                    }
                }

            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void contabilizarPago() {
        try {
            // traer el tipo de codgios para reclasificacion
            Codigos codigoReclasificacion = codigosBean.traerCodigo(Constantes.TIPO_RECLASIFICACION, "RET");
            Formatos f = ejbObligaciones.traerFormato();
            renglones = new LinkedList<>();
            double pagoMayor = 0;
            String codigoMayor = "";
            Tipoasiento ta = getKardex().getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);

            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            // buscar tipo de siento de reclasificaion
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", codigoReclasificacion.getNombre());
            List<Tipoasiento> tl = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento taReclafificacion = null;
            for (Tipoasiento t : tl) {
                taReclafificacion = t;
            }
            if (taReclafificacion == null) {
                MensajesErrores.fatal("No existe tipo  para reclasificacion");
                return;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return;
            }
            asiento = new Cabeceras();
            asiento.setDescripcion(getKardex().getObservaciones());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(getKardex().getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardex.getFechamov());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setIdmodulo(kardex.getId());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("PAGO PROVEEDORES");

            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
            double totalObligacion = 0;
//            renglones=new LinkedList<>();
            ras = new LinkedList<>();
            rasReclasificaion = new LinkedList<>();
            for (Pagosvencimientos p : pagos) {
                parametros = new HashMap();
                parametros.put(";where", "o.obligacion.compromiso=:compromiso and o.cba!='A'");
                parametros.put("compromiso", p.getCompromiso());
                parametros.put(";orden", "o.obligacion.id");
                List<Rascompras> rl = ejbRasCompras.encontarParametros(parametros);
                Calendar c = Calendar.getInstance();
//                if (p.getObligacion().getFechacontable() == null) {
//                    p.getObligacion().setFechacontable(p.getObligacion().getFechaemision());
//                }
                int mesActual = c.get(Calendar.MONTH);
                c.setTime(p.getCompromiso().getFecha());
                int mesObligacion = c.get(Calendar.MONTH);
                double valorTotalPago = 0;
                for (Rascompras rc : rl) {
                    String presupuesto = rc.getDetallecompromiso().getAsignacion().getClasificador().getCodigo();
                    presupuesto = presupuesto.substring(0, 2);
                    double valor = rc.getValor().doubleValue() + rc.getValorimpuesto().doubleValue()
                            - rc.getValorret().doubleValue() - rc.getVretimpuesto().doubleValue();
                    valorTotalPago += valor;
                    if (pagoMayor < valor) {
                        if ((presupuesto.equals("53"))) {
                            codigoMayor = "40102";
                        } else {
                            codigoMayor = ("40200");
                        }
                        pagoMayor = valor;
                    }
                    if (mesActual == mesObligacion) {
                        if (rc.getRetencion() != null) {
                            // genear el asiento de reclasificacion
                            // ver el algoritmo de reclasificacion en base a como se armo el siento de las retenciones
                            String ctaRetencion = f.getCxpinicio() + presupuesto + rc.getRetencion().getCuenta() + rc.getRetencion().getCodigo();
                            String ctaContraria = codigoReclasificacion.getParametros() + rc.getRetencion().getReclasificacion() + rc.getRetencion().getCodigo();
                            Cuentas cuentaRet = getCuentasBean().traerCodigo(ctaRetencion);
                            Cuentas cuentaContraria = getCuentasBean().traerCodigo(ctaContraria);

                            // armar la cuenta
                            Renglones rasRetencion = new Renglones();
                            rasRetencion.setCuenta(ctaRetencion);
                            double valorRetencion = rc.getValorret().doubleValue();
                            rasRetencion.setValor(new BigDecimal(valorRetencion));
                            rasRetencion.setReferencia("Reclasificaión :" + rc.getRetencion().getNombre());
                            rasRetencion.setCentrocosto(rc.getCc());
                            if (cuentaRet.getAuxiliares() != null) {
                                rasRetencion.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                            }
                            rasRetencion.setAuxiliar(rc.getCc());
                            rasRetencion.setFecha(new Date());
                            if (cuentaRet.getAuxiliares() != null) {
                                rasRetencion.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                            }
                            estaEnRenglonesReclasificacion(rasRetencion);
                            // armar la cuenta contraria
                            Renglones rasContraria = new Renglones();
                            rasContraria.setCuenta(ctaContraria);
                            double valorContraria = valorRetencion * -1;
                            rasContraria.setValor(new BigDecimal(valorContraria));
                            rasContraria.setReferencia("Reclasificación " + rc.getRetencion().getNombre());
                            rasContraria.setCentrocosto(rc.getCc());
                            rasContraria.setFecha(new Date());
                            if (cuentaContraria.getAuxiliares() != null) {
                                rasContraria.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                            }
                            estaEnRenglonesReclasificacion(rasContraria);
                            if (rc.getRetimpuesto() != null) {
                                // armada la cuenta de retención
                                ctaRetencion = f.getCxpinicio() + presupuesto + rc.getRetimpuesto().getCuenta() + rc.getRetimpuesto().getCodigo();
                                ctaContraria = codigoReclasificacion.getParametros() + rc.getRetimpuesto().getReclasificacion() + rc.getRetimpuesto().getCodigo();
                                cuentaRet = getCuentasBean().traerCodigo(ctaRetencion);
                                cuentaContraria = getCuentasBean().traerCodigo(ctaContraria);
                                // es retencion
                                // armar la cuenta
                                rasRetencion = new Renglones();
                                rasRetencion.setCuenta(ctaRetencion);
                                valorRetencion = rc.getVretimpuesto().doubleValue();
                                rasRetencion.setValor(new BigDecimal(valorRetencion));
                                rasRetencion.setReferencia("Reclasificación :" + rc.getRetencion().getNombre());
                                rasRetencion.setCentrocosto(rc.getCc());
                                rasRetencion.setFecha(new Date());
                                if (cuentaRet.getAuxiliares() != null) {
                                    rasRetencion.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                                }
                                estaEnRenglonesReclasificacion(rasRetencion);
                                // armar la cuenta contraria
                                rasContraria = new Renglones();
                                rasContraria.setCuenta(ctaContraria);
                                valorContraria = valorRetencion * -1;
                                rasContraria.setValor(new BigDecimal(valorContraria));
                                rasContraria.setReferencia("Reclasificación " + rc.getRetencion().getNombre());
                                rasContraria.setCentrocosto(rc.getCc());
                                rasContraria.setFecha(new Date());
                                if (cuentaContraria.getAuxiliares() != null) {
                                    rasRetencion.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                                }
                                estaEnRenglonesReclasificacion(rasContraria);

                            } // fin if retencion de iva
                        }
                    } // fin if de reclasificacion es otro asiento
                }
//                    pago.setKardexbanco(getKardex());
                double valorAnticipo = 0;
                double valorCuentas = 0;
                for (Renglones r : listaCuentasRenglones) {
                    valorCuentas += r.getValor().doubleValue();
                }
                // Hacer el asiento en base a la suma del rascompras
                Renglones r1 = new Renglones(); // reglon de banco
                r1.setCabecera(asiento);
                r1.setReferencia(kardex.getObservaciones());
                r1.setValor(new BigDecimal((p.getValor().doubleValue() + valorCuentas - valorAnticipo) * -1));
//                r1.setValor(new BigDecimal((p.getValor().doubleValue() - valorAnticipo) * -1));
                r1.setCuenta(cuentaBanco.getCodigo());
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
                r1.setFecha(kardex.getFechamov());
                if (cuentaBanco.getAuxiliares() != null) {
                    r1.setAuxiliar(p.getObligacion().getProveedor().getEmpresa().getRuc());
                }
//                r1.setFecha(r1.setFecha(kardex.getFechamov()););
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
//                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                // traer rascompras ahi esta la cuenta y la forma de distribuir
                estaEnRenglones(r1);
                // anticipo

                for (Rascompras rc : rl) {
                    Renglones r = new Renglones(); // reglon de provaeedores
                    r.setCabecera(asiento);
                    r.setReferencia(kardex.getObservaciones());
                    double valor = rc.getValor().doubleValue() + rc.getValorimpuesto().doubleValue()
                            - rc.getValorret().doubleValue() - rc.getVretimpuesto().doubleValue();
                    // distribuir el anticipo
//                    if (valorAnticipo != 0) {
//                        valor = valor * valorAnticipo / valorTotalPago;
//                    }
                    r.setValor(new BigDecimal(valor));
                    r.setCuenta(rc.getCuenta());
                    Cuentas cuentaP = getCuentasBean().traerCodigo(rc.getCuenta());
                    r.setPresupuesto(cuentaP.getPresupuesto());
                    if (cuentaP.getAuxiliares() != null) {
                        r.setAuxiliar(rc.getObligacion().getProveedor().getEmpresa().getRuc());
                    }
                    estaEnRas(r);

                    totalObligacion += valor;

                }

//                getRenglones().add(r1);
            }
            kardex.setCodigospi(codigoMayor);
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
            double valorPago = kardex.getValor().doubleValue();

            // falta lo de el anticipo ya esta cada renglon que va de acuerdo a los proveedores
            for (Renglones r : renglones) {
                r.setCabecera(asiento);
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            for (Renglones r : listaCuentasRenglones) {
                r.setCabecera(asiento);
                r.setFecha(kardex.getFechamov());
                r.setReferencia(kardex.getObservaciones());
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            for (Renglones r : ras) {
                r.setCabecera(asiento);
                double valor = r.getValor().doubleValue();
                double valorColocar = valorPago * valor / totalObligacion;
                r.setValor(new BigDecimal(valorColocar));
                r.setFecha(kardex.getFechamov());
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                r.setReferencia(kardex.getObservaciones());
                getRenglones().add(r);
            }
            // grabar el asiento de reclasificacion
            if (!rasReclasificaion.isEmpty()) {
                // existen renglones
                numeroAsiento = taReclafificacion.getUltimo();
                numeroAsiento++;
                taReclafificacion.setUltimo(numeroAsiento);
                ejbTipoAsiento.edit(taReclafificacion, seguridadbean.getLogueado().getUserid());
                asientoReclasificacion = new Cabeceras();
                asientoReclasificacion.setDescripcion("Asiento de Reclasificación de cuentas Fondo Rotativo : " + getKardex().getObservaciones());
                asientoReclasificacion.setModulo(perfil.getMenu().getIdpadre().getModulo());
                asientoReclasificacion.setDia(new Date());
                asientoReclasificacion.setTipo(taReclafificacion);
                asientoReclasificacion.setNumero(numeroAsiento);
                asientoReclasificacion.setFecha(new Date());
                asientoReclasificacion.setUsuario(seguridadbean.getLogueado().getUserid());
                asientoReclasificacion.setIdmodulo(kardex.getId());
                asientoReclasificacion.setModulo(perfil.getMenu().getIdpadre().getModulo());
                asientoReclasificacion.setOpcion("PAGO PROVEEDORES RECLASIFICACION");
                ejbCabecera.create(asientoReclasificacion, seguridadbean.getLogueado().getUserid());
                for (Renglones r : rasReclasificaion) {
                    r.setCabecera(asientoReclasificacion);
                    r.setFecha(kardex.getFechamov());
                    r.setReferencia(kardex.getObservaciones());
                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }

            }

        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosFrBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioReporte.insertar();
    }

    public String getValorAPagar() {
        double retorno = 0;
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (pagos == null) {
            return df.format(retorno);
        }
        for (Pagosvencimientos p : pagos) {
            double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
            double valor = p.getValor().doubleValue();
            retorno += valor - vanticipo;
        }
        return df.format(retorno);
    }

    /**
     * @return the formularioPago
     */
    public Formulario getFormularioPago() {
        return formularioPago;
    }

    /**
     * @param formularioPago the formularioPago to set
     */
    public void setFormularioPago(Formulario formularioPago) {
        this.formularioPago = formularioPago;
    }

    /**
     * @return the cuantoAnticipo
     */
    public double getCuantoAnticipo() {
        return cuantoAnticipo;
    }

    /**
     * @param cuantoAnticipo the cuantoAnticipo to set
     */
    public void setCuantoAnticipo(double cuantoAnticipo) {
        this.cuantoAnticipo = cuantoAnticipo;
    }

    /**
     * @return the anticipoAplicar
     */
    public Anticipos getAnticipoAplicar() {
        return anticipoAplicar;
    }

    /**
     * @param anticipoAplicar the anticipoAplicar to set
     */
    public void setAnticipoAplicar(Anticipos anticipoAplicar) {
        this.anticipoAplicar = anticipoAplicar;
    }

    /**
     * @return the rasReclasificaion
     */
    public List<Renglones> getRasReclasificaion() {
        return rasReclasificaion;
    }

    /**
     * @param rasReclasificaion the rasReclasificaion to set
     */
    public void setRasReclasificaion(List<Renglones> rasReclasificaion) {
        this.rasReclasificaion = rasReclasificaion;
    }

    /**
     * @return the entidadBean
     */
    public EntidadesBean getEntidadBean() {
        return entidadBean;
    }

    /**
     * @param entidadBean the entidadBean to set
     */
    public void setEntidadBean(EntidadesBean entidadBean) {
        this.entidadBean = entidadBean;
    }

    /**
     * @return the banco
     */
    public Bancos getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(Bancos banco) {
        this.banco = banco;
    }

    /**
     * @return the asiento
     */
    public Cabeceras getAsiento() {
        return asiento;
    }

    /**
     * @param asiento the asiento to set
     */
    public void setAsiento(Cabeceras asiento) {
        this.asiento = asiento;
    }

    /**
     * @return the asientoReclasificacion
     */
    public Cabeceras getAsientoReclasificacion() {
        return asientoReclasificacion;
    }

    /**
     * @param asientoReclasificacion the asientoReclasificacion to set
     */
    public void setAsientoReclasificacion(Cabeceras asientoReclasificacion) {
        this.asientoReclasificacion = asientoReclasificacion;
    }

    public String reboteKardex(Kardexbanco kardex) {

        setKardex(kardex);
        setKardexRebote(new Kardexbanco());
        getFormularioRebote().insertar();
        return null;
    }

    public String grabaReboteKardex() {
        if (getKardexRebote().getFechamov() == null) {
            MensajesErrores.fatal("Es necesario fecha");
            return null;
        }
        if (getKardexRebote().getTipomov() == null) {
            MensajesErrores.fatal("Es necesario Tipo de movimiento");
            return null;
        }
        if (getKardexRebote().getFechamov().before(kardex.getFechamov())) {
            MensajesErrores.fatal("Es necesario fecha de movientos ea mayor a fecha de egreso");
            return null;
        }
        String vale = ejbCabecera.validarCierre(kardexRebote.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        try {
            Tipoasiento ta = getKardexRebote().getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            ras = new LinkedList<>();
            asiento = new Cabeceras();
            asiento.setDescripcion("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(getKardexRebote().getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(getKardexRebote().getFechamov());
            asiento.setIdmodulo(getKardexRebote().getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("REBOTE_PAGO_PROVEEDORES");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            renglones = new LinkedList<>();
            Renglones r = new Renglones();
            r.setCuenta(kardex.getBanco().getCuenta());
            r.setValor(kardex.getValor());
            r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            r.setFecha(getKardexRebote().getFechamov());

            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            renglones.add(r);
            r = new Renglones();
            r.setCuenta(getKardexRebote().getTipomov().getCuenta());
            r.setValor(new BigDecimal(kardex.getValor().doubleValue() * -1));
            r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            r.setFecha(getKardexRebote().getFechamov());
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            renglones.add(r);
            kardex.setEstado(4);
            getKardexRebote().setProveedor(kardex.getProveedor());
            getKardexRebote().setBanco(kardex.getBanco());
            getKardexRebote().setObservaciones("Rebote Egreso NO : " + kardex.getId().toString());
            getKardexRebote().setBeneficiario(kardex.getBeneficiario());
            getKardexRebote().setFechagraba(new Date());
            kardex.setOrigen("PAGO PROVEEDORES FR");
            getKardexRebote().setEstado(5);
            getKardexRebote().setValor(kardex.getValor());
            getKardexRebote().setDocumento(kardex.getDocumento());
            kardexRebote.setFormapago(kardex.getFormapago());
            ejbKardex.create(getKardexRebote(), seguridadbean.getLogueado().getUserid());
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        kardex = getKardexRebote();
        getFormularioRebote().cancelar();
        setComprobanteEgreso("Comprobante de Ingreso");
        formularioReporte.insertar();
        return null;
    }

    /**
     * @return the kardexRebote
     */
    public Kardexbanco getKardexRebote() {
        return kardexRebote;
    }

    /**
     * @param kardexRebote the kardexRebote to set
     */
    public void setKardexRebote(Kardexbanco kardexRebote) {
        this.kardexRebote = kardexRebote;
    }

    /**
     * @return the formularioRebote
     */
    public Formulario getFormularioRebote() {
        return formularioRebote;
    }

    /**
     * @param formularioRebote the formularioRebote to set
     */
    public void setFormularioRebote(Formulario formularioRebote) {
        this.formularioRebote = formularioRebote;
    }

    /**
     * @return the comprobanteEgreso
     */
    public String getComprobanteEgreso() {
        return comprobanteEgreso;
    }

    /**
     * @param comprobanteEgreso the comprobanteEgreso to set
     */
    public void setComprobanteEgreso(String comprobanteEgreso) {
        this.comprobanteEgreso = comprobanteEgreso;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
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
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
    }

    /**
     * @return the proveedorBeneficiario
     */
    public boolean isProveedorBeneficiario() {
        return proveedorBeneficiario;
    }

    /**
     * @param proveedorBeneficiario the proveedorBeneficiario to set
     */
    public void setProveedorBeneficiario(boolean proveedorBeneficiario) {
        this.proveedorBeneficiario = proveedorBeneficiario;
    }

    public String traerTipoCuenta(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='TIPOCUENTA' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getParametros();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Codigos traerBanco(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerCuenta(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='NUMCUENTA' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return c.getCabecera().getTexto();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traer(Empleados e, String que) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto=:que and o.empleado=:empleado");
            paremetros.put("empleado", e);
            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the listaCuentasRenglones
     */
    public List<Renglones> getListaCuentasRenglones() {
        return listaCuentasRenglones;
    }

    /**
     * @param listaCuentasRenglones the listaCuentasRenglones to set
     */
    public void setListaCuentasRenglones(List<Renglones> listaCuentasRenglones) {
        this.listaCuentasRenglones = listaCuentasRenglones;
    }

    public String grabarCuenta() {
        if (getCtaInicial() == null) {
            MensajesErrores.advertencia("Seleccione una cuenta");
            return null;
        }

        if ((getRenglon().getValor() == null) || (getRenglon().getValor().doubleValue() == 0)) {
            MensajesErrores.advertencia("Seleccione un valor");
            return null;
        }
        if (listaCuentasRenglones == null) {
            listaCuentasRenglones = new LinkedList<>();
        }
        getRenglon().setCuenta(getCtaInicial().getCodigo());
        listaCuentasRenglones.add(getRenglon());
        getFormularioCuentas().cancelar();
        setCtaInicial(null);
        setRenglon(new Renglones());
        setVer(2);
        return null;
    }

    public String agregarCuenta() {
        setRenglon(new Renglones());
        getFormularioCuentas().insertar();
        return null;
    }

    /**
     * @return the cuentas
     */
    public List<Cuentas> getCuentas() {
        return cuentas;
    }

    /**
     * @param cuentas the cuentas to set
     */
    public void setCuentas(List<Cuentas> cuentas) {
        this.cuentas = cuentas;
    }

    /**
     * @return the renglon
     */
    public Renglones getRenglon() {
        return renglon;
    }

    /**
     * @param renglon the renglon to set
     */
    public void setRenglon(Renglones renglon) {
        this.renglon = renglon;
    }

    /**
     * @return the ctaInicial
     */
    public Cuentas getCtaInicial() {
        return ctaInicial;
    }

    /**
     * @param ctaInicial the ctaInicial to set
     */
    public void setCtaInicial(Cuentas ctaInicial) {
        this.ctaInicial = ctaInicial;
    }

    /**
     * @return the formularioCuentas
     */
    public Formulario getFormularioCuentas() {
        return formularioCuentas;
    }

    /**
     * @param formularioCuentas the formularioCuentas to set
     */
    public void setFormularioCuentas(Formulario formularioCuentas) {
        this.formularioCuentas = formularioCuentas;
    }

    /**
     * @return the ver
     */
    public int getVer() {
        return ver;
    }

    /**
     * @param ver the ver to set
     */
    public void setVer(int ver) {
        this.ver = ver;
    }

    private void estaEnCuentas(Cuentas cta) {
        for (Cuentas c : getCuentas()) {
            if (c.equals(cta)) {
                return;
            }
        }
        getCuentas().add(cta);
    }

    public SelectItem[] getComboCuentas() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.descripcion in ('A','I')");
        try {
            List<Formatos> formatos = ejbFormatos.encontarParametros(parametros);
            setCuentas(new LinkedList<>());
            for (Formatos f : formatos) {

                if (!((f.getCxpinicio() == null) || (f.getCxpinicio().isEmpty()))) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.imputable =true and o.codigo like :codigo");
                    parametros.put("codigo", f.getCxpinicio() + "%");
                    parametros.put(";orden", "o.codigo");
                    List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
                    for (Cuentas c : cl) {
                        estaEnCuentas(c);
                    }
                }
            }
            return Combos.getSelectItems(getCuentas(), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String verCero() {
        ver = 0;
        return null;
    }

    public String verUno() {
        ver = 1;
        return null;
    }
}
