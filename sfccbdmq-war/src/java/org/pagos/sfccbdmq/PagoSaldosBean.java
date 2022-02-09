/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
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
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "pagosSaldosSfccbdmq")
@ViewScoped
public class PagoSaldosBean {

    /**
     * @return the numeroSpi
     */
    public Integer getNumeroSpi() {
        return numeroSpi;
    }

    /**
     * @param numeroSpi the numeroSpi to set
     */
    public void setNumeroSpi(Integer numeroSpi) {
        this.numeroSpi = numeroSpi;
    }

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PagoSaldosBean() {
        Calendar c = Calendar.getInstance();
        listadoKardex = new LazyDataModel<Kardexbanco>() {
            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Kardexbanco> listadoKardex;
    private List<Kardexbanco> listaKardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioRebote = new Formulario();
    private Formulario formularioAnticipo = new Formulario();
    private Formulario formularioPago = new Formulario();
    private Formulario formularioNc = new Formulario();
    private Formulario formularioValor = new Formulario();
    private Formulario formularioPropuesta = new Formulario();
    private Cabeceras asiento;
    private Cuentas ctaInicial;
    private Integer numeroSpi;
    private Cabeceras asientoReclasificacion;
    private String comprobanteEgreso = "Comprobante de Egreso";
    private String propuesta;
    private Resource reportePropuesta;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private SpiFacade ejbSpi;
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
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String concepto;
    private Integer numero;
    private Integer id;
    private Kardexbanco kardex;
    private List<Renglones> renglones;
    private List<Renglones> ras;
    private List<Renglones> rasReclasificaion;
    private List<Cuentas> listadoCuentas;
    private List<Cuentas> cuentasCombo;
    private List<Cuentas> cuentas;
    private boolean proveedores = true;
    private Resource reporte;
    private Renglones renglon;
    private Renglones renglonSelecciona;
    private Renglones renglonValor;
    private double apagar;
    private boolean puedeImprimir = false;
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
            String nombreForma = "PagosSaldosVista";
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
        puedeImprimir = false;

    }

    public List<Kardexbanco> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        puedeImprimir = false;
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechamov desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = " o.fechamov between :desde and :hasta and o.estado in (0,1,2,3)"
                + "  and o.anticipo is null and o.cuentasaldo is not null";
//                + " and (o.proveedor is not null) and o.anticipo is null and o.cuentasaldo is not null";
//        String where = " o.fechamov between :desde and :hasta and o.estado>=0 and (o.proveedor is not null or o.usuarioentrega is not null) and o.anticipo is null";
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
            where += " and upper(o.observaciones) like :concepto";
            parametros.put("concepto", "%" + getConcepto().toUpperCase() + "%");
        }

        if (numero != null) {
            where += " and o.egreso=:documento";
            parametros.put("documento", numero);
        }
        if (propuesta != null) {
            where += " and o.propuesta=:propuesta";
            parametros.put("propuesta", propuesta);
        }
//        if (tipoDocumento != null) {
//            where += " and o.obligacion.tipodocumento=:tipodocumento";
//            parametros.put("tipodocumento", tipoDocumento);
//            
//        }
        if ((id != null) && (id > 0)) {
            parametros = new HashMap();
            where = "  o.id=:id";
            parametros.put("id", id);
        }
        int total = 0;
        try {
            parametros.put(";where", where);
            listaKardex = ejbKardex.encontarParametros(parametros);
            if (!listaKardex.isEmpty()) {
                puedeImprimir = true;
            }
            total = ejbKardex.contar(parametros);
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
            return ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        listadoKardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

        return null;
    }

    private void traerRenglones() {
        if (kardex != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and"
                    + " o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO SALDOS INICIALES'");
            parametros.put("id", kardex.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : renglones) {
                    Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                    r.setNombre(c.getNombre());
                    double valor = r.getValor().doubleValue();
//                    r.setListaPagos(pagos);
                    if (valor > 0) {
                        r.setDebitos(valor);
                    } else {
                        r.setCreditos(valor * -1);
                    }
                    if (c.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                        r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                    }
                }
                // traer reclasificacion
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO_SALDOS_INICIALES_RECLASIFICACION'");
                parametros.put("id", kardex.getId());
                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                rasReclasificaion = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : rasReclasificaion) {
                    Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
                    r.setNombre(c.getNombre());
                    double valor = r.getValor().doubleValue();
//                    r.setListaPagos(pagos);
                    if (valor > 0) {
                        r.setDebitos(valor);
                    } else {
                        r.setCreditos(valor * -1);
                    }
                    if (c.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                        r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                    }
                }
//                formularioReporte.insertar();
                Collections.sort(renglones, new valorComparator());
                Collections.sort(rasReclasificaion, new valorComparator());

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    public String cancelar() {
        formulario.cancelar();
        return "TesoreriaVista.jsf?faces-redirect=true";
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
    public LazyDataModel<Kardexbanco> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(LazyDataModel<Kardexbanco> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    // inicio el nuevo codigo luego boorro el resto
    public String nuevoKardex() {
        proveedorBean.setEmpresa(null);
        proveedorBean.setRuc(null);
        formulario.insertar();
        setKardex(new Kardexbanco());
        kardex.setUsuariograba(seguridadbean.getLogueado());
        getKardex().setValor(new BigDecimal(0));
        getKardex().setFechaabono(new Date());
        getKardex().setFechagraba(new Date());
        kardex.setOrigen("PAGO SALDOS INICIALES");
        getKardex().setFechamov(new Date());
        getKardex().setUsuariograba(seguridadbean.getLogueado());
        ras = new LinkedList<>();
        return null;
    }

    public String borraKardex(Kardexbanco kardex) {
//        *****Para poder eliminar el pago del saldo inicial
        kardex.setSpi(null);
        try {
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
//        *********
        if (kardex.getSpi() != null) {
            MensajesErrores.informacion("No es posible borrar pago ya registrado en SPI");
            return null;
        }
//        pagos = new LinkedList<>();
        formulario.eliminar();
        setKardex(kardex);
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        proveedorBean.setEmpresa(kardex.getProveedor() == null ? null : kardex.getProveedor().getEmpresa());
        proveedorBean.setProveedor(kardex.getProveedor());
        proveedorBean.setRuc(kardex.getProveedor() == null ? "" : kardex.getProveedor().getEmpresa().getRuc());
        traerRenglones();

        return null;
    }

    public String borraCuenta(int fila) {
        ras.remove(fila);

        return null;
    }

    public String eliminar() {
        try {
            // editar pagos

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

            buscar();

        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirKardex(Kardexbanco kardex) {

//        pagos = new LinkedList<>();
//        formulario.editar();
        setKardex(kardex);
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);

        DecimalFormat df = new DecimalFormat("###,##0.00");
        proveedorBean.setEmpresa(kardex.getProveedor() == null ? null : kardex.getProveedor().getEmpresa());
        proveedorBean.setProveedor(kardex.getProveedor());
        proveedorBean.setRuc(kardex.getProveedor() == null ? null : kardex.getProveedor().getEmpresa().getRuc());
        traerRenglones();
        // unir renglones
        List<Renglones> listaRenglones = new LinkedList<>();
        for (Renglones r : rasReclasificaion) {
            listaRenglones.add(r);
            Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
            r.setNombre(c.getNombre());
            if (c.getAuxiliares() != null) {
                VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                r.setAuxiliarNombre(v == null ? "" : v.getNombre());
            } else {
                if (r.getAuxiliar() != null) {
                    String xuNombre = empleadosBean.traerCedula(r.getAuxiliar());
                    if (xuNombre == null) {
                        Empresas e = proveedoresBean.taerRuc(r.getAuxiliar());
                        if (e == null) {
                            xuNombre = r.getAuxiliar();

                        } else {
                            xuNombre = e.toString();
                        }
                    }
                    r.setAuxiliarNombre(xuNombre);
                }
            }
        }
        for (Renglones r : renglones) {
            Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
            r.setNombre(c.getNombre());
            if (c.getAuxiliares() != null) {
                VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                r.setAuxiliarNombre(v == null ? "" : v.getNombre());
            } else {
                if (r.getAuxiliar() != null) {
                    VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                    r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                }
            }
            listaRenglones.add(r);
        }

        String nBanco = kardex.getBancotransferencia() == null ? "" : kardex.getBancotransferencia().getNombre();
        String cuentaTx = kardex.getCuentatrans() == null ? "" : kardex.getCuentatrans();
        parametros = new HashMap();
        parametros.put("expresado", " Cta No : " + cuentaTx + " Banco : " + nBanco);
        parametros.put("empresa", kardex.getBeneficiario());
        parametros.put("nombrelogo", "Comprobante de Egreso No: " + kardex.getId());
        parametros.put("usuario", seguridadbean.getLogueado().getUserid());
        parametros.put("fecha", kardex.getFechamov());
        parametros.put("documento", kardex.getEgreso().toString());
        parametros.put("modulo", getCuantoStr());
        parametros.put("descripcion", kardex.getObservaciones());
        parametros.put("obligaciones", getValorStr());
        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
        parametros.put("SUBREPORT_DIR", realPath + "/");
        Calendar c = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
                listaRenglones, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf");

        formularioReporte.insertar();
        return null;
    }

    private boolean validar() {

        if (getKardex().getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return true;
        }
        if (kardex.getFormapago() == null) {
            MensajesErrores.advertencia("Seleccione una forma de pago");
            return true;
        }
        if ((getKardex().getEgreso() == null) || (kardex.getEgreso() <= 0)) {
            MensajesErrores.advertencia("Necesario un número de egreso");
            return true;
        }
        if (ras == null) {
            MensajesErrores.advertencia("Nada que pagar");
            return true;
        }
        if (ras.isEmpty()) {
            MensajesErrores.advertencia("Nada que pagar");
            return true;
        }
        if (proveedores) {
            kardex.setProveedor(proveedorBean.getProveedor());
            if (proveedorBean.getProveedor().getNombrebeneficiario() == null) {
                MensajesErrores.advertencia("Proveedor no tiene beneficiario");
                return true;
            }
            if (proveedorBean.getProveedor().getBanco() == null) {
                MensajesErrores.advertencia("Proveedor no tiene banco");
                return true;
            }
            if (proveedorBean.getProveedor().getCtabancaria() == null) {
                MensajesErrores.advertencia("Proveedor no tiene cuenta bancaria");
                return true;
            }

        } else {
            String tipoCta = traerTipoCuenta(empleadosBean.getEmpleadoSeleccionado());

            if (tipoCta == null) {
                MensajesErrores.advertencia("Empleado no tiene tipo de cuenta del banco");
                return true;
            }
            Codigos codigoBanco = traerBanco(empleadosBean.getEmpleadoSeleccionado());
            if (codigoBanco == null) {
                MensajesErrores.advertencia("empleado no tiene cuenta bancaria");
                return true;
            }

        }
        // validar las líneas dependiendo de si es transferencia
        double valorAtransferir = 0;

//        double limite = proveedorBean.getEmpresa().getProveedores().getLimitetransferencia().doubleValue();
//        if (getKardex().getFormapago().getParametros().contains("T")) {
//
////            if (proveedorBean.getProveedor().getLimitetransferencia() == null) {
////                MensajesErrores.advertencia("No existe límite de  trasferencia de proveedor");
////                return true;
////            }
////            double limite = proveedorBean.getProveedor().getLimitetransferencia().doubleValue();
////
////            kardex.setValor(new BigDecimal(getValorAPagarNuevo()));
////            if (getValorAPagarNuevo() > limite) {
////                MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
////                return true;
////            }
//        }
        return false;
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            Logger.getLogger(PagoRolEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String insertarKardex() {
        try {
            if (validar()) {
                return null;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = cal.get(Calendar.YEAR);
            kardex.setOrigen("PAGO SALDOS INICIALES");
            if (proveedores) {
                kardex.setProveedor(proveedorBean.getProveedor());
                kardex.setBeneficiario(proveedorBean.getProveedor().getNombrebeneficiario());
                kardex.setBancotransferencia(proveedorBean.getProveedor().getBanco());
                kardex.setCuentatrans(proveedorBean.getProveedor().getCtabancaria());
                kardex.setDocumento(proveedorBean.getEmpresa().getRuc());
                String tipoCta = proveedorBean.getProveedor().getTipocta().getParametros();
                kardex.setTcuentatrans(Integer.parseInt(tipoCta));
            } else {
                kardex.setProveedor(null);
                kardex.setBeneficiario(empleadosBean.getEmpleadoSeleccionado().getEntidad().toString());
                String tipoCta = traerTipoCuenta(empleadosBean.getEmpleadoSeleccionado());

                kardex.setTcuentatrans(Integer.parseInt(tipoCta));
                Codigos codigoBanco = traerBanco(empleadosBean.getEmpleadoSeleccionado());
                kardex.setBancotransferencia(codigoBanco);
                kardex.setDocumento(empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
                kardex.setCuentatrans(traer(empleadosBean.getEmpleadoSeleccionado(), "NUMCUENTA"));
            }
            kardex.setValor(new BigDecimal(getValorAPagarNuevo()));
            kardex.setSaldosanio(anio);
            kardex.setEstado(0);

            Spi spi = new Spi();
            spi.setEstado(0);
            spi.setFecha(kardex.getFechamov());
            spi.setUsuario(seguridadbean.getLogueado().getUserid());
            spi.setBanco(kardex.getBanco());
            spi.setNumero(getNumeroSpi());
            int numeroSpi = Integer.parseInt(kardex.getPropuesta());
            Map parametros = new HashMap();
            parametros.put(";where", "o.propuesta=:propuesta and o.spi.numero=:numero");
            parametros.put("propuesta", kardex.getPropuesta());
            parametros.put("numero", numeroSpi);
            List<Kardexbanco> listaConSpi = ejbKardex.encontarParametros(parametros);
            if (!listaConSpi.isEmpty()) {
                Kardexbanco KardexSpi = listaConSpi.get(0);
                spi = KardexSpi.getSpi();
                kardex.setSpi(spi);
            } else {
                if (kardex.getTipomov().getSpi()) {
                    ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
                    kardex.setSpi(spi);
                }
            }
//            kardex.setCuentasaldo(ctaInicial.getCodigo());
            ejbKardex.create(kardex, seguridadbean.getLogueado().getUserid());
// armar contabilizacion
            contabilizarPago();

// ver si cada pago tiene la información correcta del proveedor an
            imprimirKardex(kardex);

        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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

    public SelectItem[] getComboPagosCuentas() {
        if (proveedores) {
            if (proveedorBean.getEmpresa() == null) {
                return null;
            }
        } else {
            if (empleadosBean.getEmpleadoSeleccionado() == null) {
                return null;
            }
        }

        // va el algoritmo de las cuentas
        Map parametros = new HashMap();
        parametros.put(";where", "o.escxp=true");
        try {
            List<Formatos> formatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicialInterna = "2";
            for (Formatos f : formatos) {
                ctaInicialInterna = f.getInicial();
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.tipo.codigo like '0%' "
                    + " and o.auxiliar like :auxiliar and o.cuenta like :cuenta and o.cabecera.fecha =:fecha");
            if (proveedores) {
                String ruc = proveedorBean.getEmpresa().getRuc().substring(0, 1);
                if (ruc.equals("P")) {
                    parametros.put("auxiliar", proveedorBean.getEmpresa().getRuc());
                } else {
                    ruc = proveedorBean.getEmpresa().getRuc().substring(0, 10);
                    parametros.put("auxiliar", ruc + "%");
                }
            } else {
                parametros.put("auxiliar", empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
            }
            parametros.put("cuenta", ctaInicialInterna + "%");
            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
            Calendar cal = Calendar.getInstance();
            cal.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = cal.get(Calendar.YEAR);
            List<Renglones> listaSaldos = ejbRenglones.encontarParametros(parametros);
            List<Renglones> listaRenglones = new LinkedList<>();
            for (Renglones r : listaSaldos) {
                // traer lo pagado
                parametros = new HashMap();
                parametros.put(";where", "o.cuentasaldo like :cuentasaldo"
                        + " and o.saldosanio=:saldosanio and o.documento=:documento");
                parametros.put("saldosanio", anio);
                parametros.put("documento", r.getAuxiliar());
                parametros.put("cuentasaldo", "%[" + r.getCuenta() + "," + r.getAuxiliar() + "," + r.getId() + "]%");
                List<Kardexbanco>lista = ejbKardex.encontarParametros(parametros);
//                double total = ejbKardex.sumarCampo(parametros).doubleValue();
//                double valorRas = Math.abs(r.getValor().doubleValue());
//                total -= valorRas;
                if (lista.isEmpty()) {
                    listaRenglones.add(r);
                } else {
                    double total = 0;
                    for(Kardexbanco k:lista){
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion='PAGO SALDOS INICIALES'"
                                + " and o.valor > 0");
                        parametros.put("idmodulo", k.getId());
                        List<Renglones>lista2=ejbRenglones.encontarParametros(parametros);
                        if(!lista2.isEmpty()){
                            for(Renglones r2 : lista2){
                                if(r2.getCuenta().substring(8, 10).equals(r.getCuenta().substring(8, 10))){
                                    total += r2.getValor().doubleValue();
                                }
                            }
                        }
                    }
                    double valorRenglon = r.getValor().doubleValue() * -1;
                    if (valorRenglon > total) {
                        double nuevoValor = valorRenglon - total;
                        r.setValor(new BigDecimal(nuevoValor));
                        listaRenglones.add(r);
                    }
                }
            }

            int size = listaRenglones.size();
            SelectItem[] items = new SelectItem[size];
            int i = 0;
//            items[0] = new SelectItem(0, "--- Seleccione uno ---");
//            i++;
            DecimalFormat df = new DecimalFormat("###,##0.00");
            boolean si = true;
            for (Renglones x : listaRenglones) {
                if (si) {
                    renglonSelecciona = x;
                }
                double valor = Math.abs(x.getValor().doubleValue());
                x.setValor(new BigDecimal(valor));
                x.setDebitos(valor);
                Cuentas cta = cuentasBean.traerCodigo(x.getCuenta());
                items[i++] = new SelectItem(x, cta.toString() + " :" + df.format(x.getValor().doubleValue()));
                si = false;
            }

            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPagosCuentasAnterior() {
        if (proveedores) {
            if (proveedorBean.getEmpresa() == null) {
                return null;
            }
        } else {
            if (empleadosBean.getEmpleadoSeleccionado() == null) {
                return null;
            }
        }

        // va el algoritmo de las cuentas
        Map parametros = new HashMap();
        parametros.put(";where", "o.escxp=true");
        try {
            List<Formatos> formatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicialInterna = "2";
            for (Formatos f : formatos) {
                ctaInicialInterna = f.getInicial();
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.tipo.codigo like '0%' "
                    + " and o.auxiliar=:auxiliar and o.cuenta like :cuenta and o.cabecera.fecha =:fecha");
            if (proveedores) {
                parametros.put("auxiliar", proveedorBean.getEmpresa().getRuc());
            } else {
                parametros.put("auxiliar", empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
            }
            parametros.put("cuenta", ctaInicialInterna + "%");
            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
            Calendar cal = Calendar.getInstance();
            cal.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = cal.get(Calendar.YEAR);
            List<Renglones> listaSaldos = ejbRenglones.encontarParametros(parametros);
            cuentasCombo = new LinkedList<>();
            for (Renglones r : listaSaldos) {
                // traer lo pagado
                parametros = new HashMap();
                parametros.put(";where", "o.cuentasaldo like :cuentasaldo"
                        + " and o.saldosanio=:saldosanio and o.documento=:documento");
                parametros.put("saldosanio", anio);
                parametros.put("documento", r.getAuxiliar());
                parametros.put(";campo", "o.valor");
                parametros.put("cuentasaldo", "%[" + r.getCuenta() + "," + r.getAuxiliar() + "," + r.getId() + "]%");
                double total = ejbKardex.sumarCampo(parametros).doubleValue();
                double valorRas = Math.abs(r.getValor().doubleValue());
//                total -= valorRas;
                if (total == 0) {
                    Cuentas cta = cuentasBean.traerCodigo(r.getCuenta());
                    cta.setRenglon(r.getId());
                    cta.setValor(valorRas);
                    cuentasCombo.add(cta);
                }
            }

            int size = cuentasCombo.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            DecimalFormat df = new DecimalFormat("###,##0.00");
            for (Cuentas x : cuentasCombo) {
                items[i++] = new SelectItem(x, x.toString() + " :" + df.format(x.getValor()));
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String agregarValor() {
        if (renglonValor.getValor() != null) {
            double valor = renglonValor.getValor().abs().doubleValue();
            renglonValor.setCreditos(valor);
            renglonValor.setDebitos(valor);
        }
        formularioValor.insertar();
        return null;
    }

    public String agregar() {
        if (ras == null) {
            ras = new LinkedList<>();
        }
        for (Renglones c : ras) {
            if (c.equals(renglonValor)) {
                return null;
            }
        }
//        Renglones r = new Renglones();
//        r.setCuenta(renglonSelecciona.getCuenta());
//        r.setValor(new BigDecimal(getValorAPagar()));
//        renglonSelecciona.setValor(renglonSelecciona.getValor().abs());
        ras.add(renglonValor);
        formularioValor.cancelar();
        return null;
    }

    public double getValorAPagar() {
//        if (proveedores) {
//            if (proveedorBean.getEmpresa() == null) {
//                return 0;
//            }
//        } else {
//            if (empleadosBean.getEmpleadoSeleccionado() == null) {
//                return 0;
//            }
//        }
        if (ctaInicial == null) {
            return 0;
        }
//        if (ctaInicial.getRenglon() == 0) {
//            return 0;
//        }
        // va el algoritmo de las cuentas
//        Map parametros = new HashMap();
//        try {
//            parametros.put(";where", "o.cabecera.tipo.codigo like '0%' "
//                    + " and o.auxiliar=:auxiliar "
//                    + " and o.id=:id"
//                    + " and o.cuenta = :cuenta and o.cabecera.fecha =:fecha");
//            if (proveedores) {
//                parametros.put("auxiliar", proveedorBean.getEmpresa().getRuc());
//            } else {
//                parametros.put("auxiliar", empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
//            }
//            parametros.put("cuenta", ctaInicial.getCodigo());
//            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
//            parametros.put(";campo", "o.valor");
//                for (Cuentas c:cuentasCombo){
//                    if (c.equals(ctaInicial)){
//                        return c.getValor();
//                    }
//                }
//            return Math.abs(ejbRenglones.sumarCampo(parametros).doubleValue());

//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return apagar;
    }

    public double getValorAPagarNuevo() {
        if (ras == null) {
            return 0;
        }
        double valor = 0;
        for (Renglones r : ras) {
//            valor += r.getValor().doubleValue();
            valor += r.getDebitos();
        }
        return valor;
    }

    private void contabilizarPago() {
        try {
            // traer el tipo de codgios para reclasificacion

            Tipoasiento ta = getKardex().getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            int numeroAsiento1 = ta.getUltimo();
            numeroAsiento += 2;
            numeroAsiento1++;
            ta.setUltimo(numeroAsiento);
            Codigos codigo = codigosBean.traerCodigo(Constantes.CUENTAS_LIQUIDACIONES, Constantes.CUENTAS_LIQUIDACIONES);
            if (codigo == null) {
                MensajesErrores.error("Falta cuenta para liquidaciones");
                return;
            }
            String[] cuentaPoner = codigo.getParametros().split("#");
            if ((cuentaPoner == null) || (cuentaPoner.length < 2)) {
                MensajesErrores.error("Falta cuenta para liquidaciones");
                return;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return;
            }
            int longitud = cuentaPoner[0].length();

            // buscar tipo de siento de reclasificaion
            asiento = new Cabeceras();
            asiento.setDescripcion(getKardex().getObservaciones());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(getKardex().getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardex.getFechamov());
            asiento.setIdmodulo(kardex.getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("PAGO SALDOS INICIALES");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());

            Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
            renglones = new LinkedList<>();
            rasReclasificaion = new LinkedList<>();
            Renglones r1 = new Renglones(); // reglon de banco
            r1.setCabecera(asiento);
            r1.setReferencia(kardex.getObservaciones());
            r1.setValor(kardex.getValor().negate());
            r1.setCuenta(cuentaBanco.getCodigo());
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            r1.setFecha(kardex.getFechamov());
            if (cuentaBanco.getAuxiliares() != null) {
                if (proveedores) {
                    r1.setAuxiliar(proveedorBean.getProveedor().getEmpresa().getRuc());
                } else {
                    r1.setAuxiliar(empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
                }
            }
            r1.setFecha(kardex.getFechamov());
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            try {
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            } catch (InsertarException ex) {
                Logger.getLogger(PagoSaldosBean.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            renglones.add(r1);
            String cta = "";
            List<Renglones> listaReclasificaciones = new LinkedList<>();
            for (Renglones r2 : ras) {
                Renglones r = new Renglones(); // reglon de banco
                r.setCabecera(asiento);
                r.setReferencia(kardex.getObservaciones());
                cta += "[" + r2.getCuenta() + "," + r2.getAuxiliar() + "," + r2.getId() + "]";
                r.setValor(new BigDecimal(r2.getDebitos()));
                r.setFecha(kardex.getFechamov());
                r.setAuxiliar(r2.getAuxiliar());
                r.setPresupuesto(r2.getPresupuesto());

                // aqui va la inteligencia que pide
                String acomparar = r2.getCuenta().substring(0, longitud);
                if (acomparar.equals(cuentaPoner[0])) {
                    // encontro el 224
                    String cuentaOriginal = r2.getCuenta();
                    String cuentaCambiar = r2.getCuenta();
                    String cuentaUno = cuentaCambiar.replace(acomparar, cuentaPoner[1]);
                    // haber
                    Renglones rdebe = new Renglones(); // reglon de banco
                    rdebe.setReferencia(kardex.getObservaciones());
                    rdebe.setCuenta(cuentaOriginal);
                    rdebe.setValor(new BigDecimal(r2.getDebitos()));
                    rdebe.setFecha(kardex.getFechamov());
                    rdebe.setAuxiliar(r2.getAuxiliar());
                    rdebe.setPresupuesto(r2.getPresupuesto());
                    listaReclasificaciones.add(rdebe);
                    // haber
                    Renglones rhaber = new Renglones(); // reglon de banco
                    rhaber.setReferencia(kardex.getObservaciones());
                    rhaber.setCuenta(cuentaUno);
                    rhaber.setValor(new BigDecimal(r2.getDebitos() * -1));
                    rhaber.setFecha(kardex.getFechamov());
                    rhaber.setAuxiliar(r2.getAuxiliar());
                    rhaber.setPresupuesto(r2.getPresupuesto());
                    listaReclasificaciones.add(rhaber);
                    r.setCuenta(cuentaUno);
                } else {
                    r.setCuenta(r2.getCuenta());
                }

                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                renglones.add(r);
            }
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            if (!listaReclasificaciones.isEmpty()) {
//                numeroAsiento++;
                Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "RET");
                Map parametros = new HashMap();
                parametros.put("codigo", codigoReclasificacion.getNombre());
                parametros.put(";where", "o.codigo=:codigo");
                List<Tipoasiento> lista = ejbTipoAsiento.encontarParametros(parametros);
                for (Tipoasiento t : lista) {
                    ta = t;
                }
                numeroAsiento1 = ta.getUltimo() + 1;
                ta.setUltimo(numeroAsiento1);
                asiento = new Cabeceras();
                asiento.setDescripcion(getKardex().getObservaciones());
                asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
                asiento.setDia(new Date());
                asiento.setTipo(ta);
                asiento.setNumero(numeroAsiento1);
                asiento.setFecha(kardex.getFechamov());
                asiento.setIdmodulo(kardex.getId());
                asiento.setUsuario(seguridadbean.getLogueado().getUserid());
                asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
                asiento.setOpcion("PAGO_SALDOS_INICIALES_RECLASIFICACION");
                ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
                for (Renglones rcla : listaReclasificaciones) {
                    rcla.setCabecera(asiento);
                    rcla.setFecha(kardex.getFechamov());
                    rcla.setReferencia(kardex.getObservaciones());
                    ejbRenglones.create(rcla, seguridadbean.getLogueado().getUserid());
                    rasReclasificaion.add(rcla);

                }
                ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

            }
            kardex.setCodigospi("40102");
            kardex.setEstado(2);
            kardex.setCuentasaldo(cta);
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());

            // armar el reporte
            formulario.cancelar();

            formularioReporte.insertar();
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String imprimir() {
        try {
            String observacionPropuesta = "";
            for (Kardexbanco ko : listaKardex) {
                observacionPropuesta = ko.getPropuesta();
            }
            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + observacionPropuesta, null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double valorTotal = 0;
            for (Kardexbanco k : listaKardex) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                String numeroCuenta = k.getCuentatrans();
                String tipoCuenta = k.getTcuentatrans().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));
                /////////////////////FIN EMPLEADO
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        k.getValor().doubleValue()));
                valorTotal += k.getValor().doubleValue();
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            reportePropuesta = pdf.traerRecurso();
            formularioPropuesta.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

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

    /**
     * @return the formularioNc
     */
    public Formulario getFormularioNc() {
        return formularioNc;
    }

    /**
     * @param formularioNc the formularioNc to set
     */
    public void setFormularioNc(Formulario formularioNc) {
        this.formularioNc = formularioNc;
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

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    /**
     * @return the proveedores
     */
    public boolean isProveedores() {
        return proveedores;
    }

    /**
     * @param proveedores the proveedores to set
     */
    public void setProveedores(boolean proveedores) {
        this.proveedores = proveedores;
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
     * @return the listadoCuentas
     */
    public List<Cuentas> getListadoCuentas() {
        return listadoCuentas;
    }

    /**
     * @param listadoCuentas the listadoCuentas to set
     */
    public void setListadoCuentas(List<Cuentas> listadoCuentas) {
        this.listadoCuentas = listadoCuentas;
    }

    /**
     * @return the ras
     */
    public List<Renglones> getRas() {
        return ras;
    }

    /**
     * @param ras the ras to set
     */
    public void setRas(List<Renglones> ras) {
        this.ras = ras;
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

    private void estaEnCuentas(Cuentas cta) {
        for (Cuentas c : cuentas) {
            if (c.equals(cta)) {
                return;
            }
        }
        cuentas.add(cta);
    }

    public SelectItem[] getComboCuentas() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.descripcion in ('A','I')");
        try {
            List<Formatos> formatos = ejbFormatos.encontarParametros(parametros);
            cuentas = new LinkedList<>();
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
            return Combos.getSelectItems(cuentas, true);
//            int size = cuentas.size() + 1;
//            SelectItem[] items = new SelectItem[size];
//            int i = 0;
//                items[0] = new SelectItem(null, "---");
//            for (Cuentas x : cuentas) {
//                items[i++] = new SelectItem(x.getCodigo(), x.toString());
//            }
//            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String agregarCuenta() {
        renglon = new Renglones();
        formularioPago.insertar();
        return null;
    }

    public String grabarCuenta() {
        if (ctaInicial == null) {
            MensajesErrores.advertencia("Seleccione una cuenta");
            return null;
        }

        if ((renglon.getValor() == null) || (renglon.getValor().doubleValue() == 0)) {
            MensajesErrores.advertencia("Seleccione un valor");
            return null;
        }
        renglon.setCuenta(ctaInicial.getCodigo());
        ras.add(renglon);
        formularioPago.cancelar();
        ctaInicial = null;
        return null;
    }

    public void cambiaCuenta(ValueChangeEvent event) {
        Cuentas cta = (Cuentas) event.getNewValue();
        if (cta != null) {
            apagar = cta.getValor();
        }
    }

    public Renglones traer(Integer id) throws ConsultarException {
        return ejbRenglones.find(id);
    }

    /**
     * @return the renglonSelecciona
     */
    public Renglones getRenglonSelecciona() {
        return renglonSelecciona;
    }

    /**
     * @param renglonSelecciona the renglonSelecciona to set
     */
    public void setRenglonSelecciona(Renglones renglonSelecciona) {
        this.renglonSelecciona = renglonSelecciona;
    }

    /**
     * @return the formularioValor
     */
    public Formulario getFormularioValor() {
        return formularioValor;
    }

    /**
     * @param formularioValor the formularioValor to set
     */
    public void setFormularioValor(Formulario formularioValor) {
        this.formularioValor = formularioValor;
    }

    /**
     * @return the renglonValor
     */
    public Renglones getRenglonValor() {
        return renglonValor;
    }

    /**
     * @param renglonValor the renglonValor to set
     */
    public void setRenglonValor(Renglones renglonValor) {
        this.renglonValor = renglonValor;
    }

    public String borraKardexSpi(Kardexbanco kardex) {

//        pagos = new LinkedList<>();
        formulario.eliminar();
        setKardex(kardex);
        this.kardex.setSpi(null);
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        proveedorBean.setEmpresa(kardex.getProveedor() == null ? null : kardex.getProveedor().getEmpresa());
        proveedorBean.setProveedor(kardex.getProveedor());
        proveedorBean.setRuc(kardex.getProveedor() == null ? "" : kardex.getProveedor().getEmpresa().getRuc());
        traerRenglones();

        return null;
    }

    /**
     * @return the propuesta
     */
    public String getPropuesta() {
        return propuesta;
    }

    /**
     * @param propuesta the propuesta to set
     */
    public void setPropuesta(String propuesta) {
        this.propuesta = propuesta;
    }

    /**
     * @return the reportePropuesta
     */
    public Resource getReportePropuesta() {
        return reportePropuesta;
    }

    /**
     * @param reportePropuesta the reportePropuesta to set
     */
    public void setReportePropuesta(Resource reportePropuesta) {
        this.reportePropuesta = reportePropuesta;
    }

    /**
     * @return the listaKardex
     */
    public List<Kardexbanco> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Kardexbanco> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the puedeImprimir
     */
    public boolean isPuedeImprimir() {
        return puedeImprimir;
    }

    /**
     * @param puedeImprimir the puedeImprimir to set
     */
    public void setPuedeImprimir(boolean puedeImprimir) {
        this.puedeImprimir = puedeImprimir;
    }

    /**
     * @return the formularioPropuesta
     */
    public Formulario getFormularioPropuesta() {
        return formularioPropuesta;
    }

    /**
     * @param formularioPropuesta the formularioPropuesta to set
     */
    public void setFormularioPropuesta(Formulario formularioPropuesta) {
        this.formularioPropuesta = formularioPropuesta;
    }
}
