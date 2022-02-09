/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import javax.faces.application.Resource;
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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
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
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
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
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "pagosSaldosFrSfccbdmq")
@ViewScoped
public class PagosSaldosFrBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PagosSaldosFrBean() {
        Calendar c = Calendar.getInstance();
        listadoKardex = new LazyDataModel<Kardexbanco>() {
            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Kardexbanco> listadoKardex;
    private Pagosvencimientos pago;
//    private Pagosvencimientos pago;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioAnticipo = new Formulario();
    private Formulario formularioPago = new Formulario();
    private Cabeceras asiento;
    private Cabeceras asientoReclasificacion;
    private String comprobanteEgreso = "Comprobante de Egreso";
    private Kardexbanco kardexRebote;
    private Formulario formularioRebote = new Formulario();
    private Cuentas ctaInicial;
    private List<Cuentas> cuentas;
    private List<Renglones> valoresPagar;
    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @EJB
    private CuentasFacade ejbCuentas;

    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
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
    private Renglones renglon;
    private List<Renglones> rasReclasificaion;
    private boolean pagados;
    private double cuantoAnticipo;
    private Anticipos anticipoAplicar;
    private Resource reporte;
    private boolean proveedores = true;
    private boolean proveedoresBeneficiarios = true;
    private Entidades entidad;
    private Empresas proveedor;
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
            String nombreForma = "PagosSaldosFrVista";
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

    public List<Kardexbanco> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fechamov desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = " o.fechamov between :desde and :hasta "
                + " and o.cuentasaldo like 'FR%'";
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
            where += " and upper(o.observaciones) like:concepto";
            parametros.put("concepto", getConcepto().toUpperCase() + "%");
        }

        if (numero != null) {
            where += " and o.egreso=:documento";
            parametros.put("documento", numero);
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
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO SALDOS PROVEEDORES FR'");
            parametros.put("id", kardex.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                // traer reclasificacion
//                parametros = new HashMap();
//                parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.modulo=:modulo and o.cabecera.opcion='PAGO PROVEEDORES RECLASIFICACION FR'");
//                parametros.put("id", kardex.getId());
//                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
//                rasReclasificaion = ejbRenglones.encontarParametros(parametros);
//                formularioReporte.insertar();
                Collections.sort(renglones, new valorComparator());
//                Collections.sort(rasReclasificaion, new valorComparator());
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
            return r.getValor().
                    compareTo(r1.getValor());

        }
    }
    public class valorComparatorProveedores implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Empresas r = (Empresas) o1;
            Empresas r1 = (Empresas) o2;
            return r.getNombre().
                    compareTo(r1.getNombre());

        }
    }
    public class valorComparatorEmpleados implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Entidades r = (Entidades) o1;
            Entidades r1 = (Entidades) o2;
            return r.getApellidos().
                    compareTo(r1.getApellidos());

        }
    }
    public String imprimirKardex(Kardexbanco kardex) {

//        pagos = new LinkedList<>();
//        formulario.editar();
        setKardex(kardex);
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        String htmlString = "<table>\n"
                + "<tr>\n"
                + "  <th colspan='5'  scope='col'>Detalle de Facturas</th>\n"
                + "</tr>\n"
                + " \n"
                + "<tr>\n"
                + "<th scope='col'>Concepto</th>"
                + "<th scope='col'>Proveedor</th>"
                + "<th scope='col'>Numero</th>"
                + "<th scope='col'>Contrato</th>"
                + "<th scope='col'>Valor</th>"
                + "</tr>";
        DecimalFormat df = new DecimalFormat("###,##0.00");
        htmlString += "</table>";

        traerRenglones();
        // unir renglones
        List<Renglones> listaRenglones = new LinkedList<>();
        for (Renglones r : renglones) {
            Cuentas c = cuentasBean.traerCodigo(r.getCuenta());
            r.setNombre(c.getNombre());
            double valor = r.getValor().doubleValue();
            if (valor > 0) {
                r.setDebitos(valor);
            } else {
                r.setCreditos(valor * -1);
            }
            Empresas e = getProveedoresBean().taerRuc(r.getAuxiliar());
            String xuNombre="";
            if (e != null) {
                xuNombre = getProveedoresBean().taerRuc(r.getAuxiliar()).getNombre();
            }
            if ((xuNombre == null) || (xuNombre.isEmpty())) {
                xuNombre = getEmpleadosBean().traerCedula(r.getAuxiliar());
            }
            r.setAuxiliarNombre(xuNombre);
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

    private boolean estaEnRas(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : valoresPagar) {
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
//                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
//                r.setValor(new BigDecimal(valor));
//                r.setFecha(new Date());
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
                r.setFecha(new Date());
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
                r.setFecha(new Date());
                return true;
            }
        }
        rasReclasificaion.add(r1);
        return false;
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
        entidadBean.setEntidad(null);
        entidadBean.setApellidos(null);
        valoresPagar = new LinkedList<>();
        ras = new LinkedList<>();
        rasReclasificaion = new LinkedList<>();
        renglones = new LinkedList<>();
        formulario.insertar();
        setKardex(new Kardexbanco());
        getKardex().setValor(new BigDecimal(0));
        getKardex().setFechaabono(new Date());
        getKardex().setFechagraba(new Date());
        kardex.setOrigen("PAGO PROVEEDORES FR");
        getKardex().setFechamov(new Date());
        getKardex().setUsuariograba(seguridadbean.getLogueado());
        return null;
    }

    private boolean validar() {
        if (valoresPagar.isEmpty()) {
            MensajesErrores.fatal("Es necesario seleccionar algúna cuenta ");
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

        return false;
    }

    public String insertarKardex() {
        if (proveedoresBeneficiarios) {
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(configuracionBean.getConfiguracion().getPinicial());
        int anio = cal.get(Calendar.YEAR);
        try {
            double valor = getTotalPagar();
            if (proveedoresBeneficiarios) {
                kardex.setUsuarioentrega(seguridadbean.getLogueado());
                kardex.setBeneficiario(proveedoresBean.getEmpresa().getRuc());
                kardex.setBancotransferencia(proveedoresBean.getProveedor().getBanco());
                kardex.setCuentatrans(proveedoresBean.getProveedor().getCtabancaria());
                kardex.setDocumento(proveedoresBean.getEmpresa().getRuc());
            } else {
                kardex.setUsuarioentrega(entidadBean.getEntidad());
                kardex.setBeneficiario(entidadBean.getEntidad().getApellidos()+" "+entidadBean.getEntidad().getNombres());
                kardex.setBancotransferencia(traerBanco(entidadBean.getEntidad().getEmpleados()));
                kardex.setCuentatrans(traerCuenta(entidadBean.getEntidad().getEmpleados()));
                kardex.setDocumento(entidadBean.getEntidad().getPin());
            }
            if (kardex.getBancotransferencia()==null){
                MensajesErrores.advertencia("Empleado Oproveedor no tiene banco de transferencia");
                return null;
            }
            if (kardex.getCuentatrans()==null){
                MensajesErrores.advertencia("Empleado Oproveedor no tiene cuenta de transferencia");
                return null;
            }
            kardex.setEstado(0);
            kardex.setValor(new BigDecimal(valor));
            kardex.setSaldosanio(anio);
            ejbKardex.create(kardex, seguridadbean.getLogueado().getUserid());
            // armar contabilizacion
            contabilizarPago();
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosSaldosFrBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // ver si cada pago tiene la información correcta del proveedor an
        imprimirKardex(kardex);
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
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
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
        valoresPagar.add(renglon);
        formularioPago.cancelar();
        ctaInicial = null;
        return null;
    }

    public String agregarPago() {
        if (ctaInicial == null) {
            MensajesErrores.advertencia("Seleccione una cuenta");
            return null;
        }
        if (proveedores) {
            if (proveedor == null) {
                MensajesErrores.advertencia("Seleccione un proveedor");
                return null;
            }
        } else {
            if (entidad == null) {
                MensajesErrores.advertencia("Seleccione un empleado");
                return null;
            }
        }
        if (valoresPagar == null) {
            valoresPagar = new LinkedList<>();
        }

        Renglones r = new Renglones();
        r.setCuenta(ctaInicial.getCodigo());
        r.setPresupuesto(ctaInicial.getPresupuesto());
        if (proveedores) {
            r.setAuxiliar(proveedor.getRuc());
        } else {
            r.setAuxiliar(entidad.getPin());
        }
        r.setValor(new BigDecimal(getValorPagar()));
        if (!estaEnRas(r)) {
            valoresPagar.add(r);
        }
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

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void contabilizarPago() {
        try {
            // traer el tipo de codgios para reclasificacion

            Tipoasiento ta = getKardex().getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);

            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
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
            asiento.setOpcion("PAGO SALDOS PROVEEDORES FR");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
            renglones = new LinkedList<>();
            Renglones r1 = new Renglones(); // reglon de banco
            r1.setCabecera(asiento);
            r1.setReferencia(kardex.getObservaciones());
            r1.setValor(kardex.getValor().negate());
            r1.setCuenta(cuentaBanco.getCodigo());
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            r1.setFecha(new Date());
            if (proveedoresBeneficiarios) {
                r1.setAuxiliar(proveedoresBean.getEmpresa().getRuc());
            } else {
                r1.setAuxiliar(entidadBean.getEntidad().getPin());
            }
//            if (cuentaBanco.getAuxiliares() != null) {
//                if (proveedores) {
//                    r1.setAuxiliar(proveedorBean.getProveedor().getEmpresa().getRuc());
//                } else {
//                    r1.setAuxiliar(empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
//                }
//            }
            r1.setFecha(kardex.getFechamov());
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
            renglones.add(r1);
            String cta = "FR";
            for (Renglones r : valoresPagar) {
//                r = new Renglones(); // reglon de banco
                r.setCabecera(asiento);
                r.setReferencia(kardex.getObservaciones());
//                if (cta != null) {
//                    cta += ",";
//                } else {
//                    cta = "";
//                }
                cta +="["+ r.getCuenta()+","+r.getAuxiliar()+","+r.getId()+"]";
//                r.setValor(kardex.getValor());
//                r.setCuenta(ctaInicial.getCodigo());
//                r.setPresupuesto(ctaInicial.getPresupuesto());
                r.setFecha(kardex.getFechamov());
//                if (ctaInicial.getAuxiliares() != null) {
//                    if (proveedores) {
//                        r.setAuxiliar(proveedorBean.getProveedor().getEmpresa().getRuc());
//                    } else {
//                        r.setAuxiliar(empleadosBean.getEmpleadoSeleccionado().getEntidad().getPin());
//                    }
//                }
                r.setFecha(kardex.getFechamov());
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                renglones.add(r);
            }

            kardex.setCodigospi("40102");
            kardex.setEstado(2);
            kardex.setCuentasaldo(cta);
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
            // armar el reporte
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();

        formularioReporte.insertar();
    }

    public String getValorAPagar() {
        double retorno = 0;
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (valoresPagar == null) {
            return "0.00";
        }
        for (Renglones r : valoresPagar) {
            double valor = r.getValor().doubleValue();
            retorno += valor;
        }
        return df.format(retorno);
    }

    public double getTotalPagar() {
        double retorno = 0;
        if (valoresPagar == null) {
            return 0;
        }
        for (Renglones r : valoresPagar) {
            double valor = r.getValor().doubleValue();
            retorno += valor;
        }
        return retorno;
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

    public SelectItem[] getComboProveedores() {
        // va el algoritmo de proveedores
        Map parametros = new HashMap();
        parametros.put(";where", "o.escxp=true");
        try {
            List<Formatos> formatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicialInterna = "2";
            for (Formatos f : formatos) {
                ctaInicialInterna = f.getInicial();
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.tipo.codigo like '0%' and o.auxiliar is not null"
                    + " and o.cuenta like :cuenta and o.cabecera.fecha =:fecha");

            parametros.put("cuenta", ctaInicialInterna + "%");
            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
            Calendar cal = Calendar.getInstance();
            cal.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = cal.get(Calendar.YEAR);
            List<Renglones> listaSaldos = ejbRenglones.encontarParametros(parametros);
            List<Empresas> listaProveedores = new LinkedList<>();
            for (Renglones r : listaSaldos) {
                // traer lo pagado
//                if (r.getAuxiliar().equals("1707815245001")){
//                    parametros.put("saldosanio", anio);
//                }
                parametros = new HashMap();
                parametros.put(";where", "o.cuentasaldo like :cuentasaldo"
                        + " and o.saldosanio=:saldosanio");
                parametros.put("saldosanio", anio);
                parametros.put(";campo", "o.valor");
                parametros.put("cuentasaldo", "%[" + r.getCuenta()+","+r.getAuxiliar() +","+r.getId()+ "]%");
                double total = ejbKardex.sumarCampo(parametros).doubleValue();
                double valorRas = Math.abs(r.getValor().doubleValue());
//                total -= valorRas;
                if (total == 0) {
                    Empresas e = proveedoresBean.taerRuc(r.getAuxiliar());
                    if (e != null) {
                        listaProveedores.add(e);
                    }
                }
            }
            Collections.sort(listaProveedores, new valorComparatorProveedores());
            // ordenar combo
            return Combos.getSelectItems(listaProveedores, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEmpleados() {
        // va el algoritmo de proveedores
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
                    + " and o.cuenta like :cuenta and o.cabecera.fecha =:fecha");

            parametros.put("cuenta", ctaInicialInterna + "%");
            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
            Calendar cal = Calendar.getInstance();
            cal.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = cal.get(Calendar.YEAR);
            List<Renglones> listaSaldos = ejbRenglones.encontarParametros(parametros);
            List<Entidades> listaEmpleados = new LinkedList<>();
            for (Renglones r : listaSaldos) {
                // traer lo pagado
                parametros = new HashMap();
                parametros.put(";where", "o.cuentasaldo like :cuentasaldo"
                        + " and o.saldosanio=:saldosanio");
                parametros.put("saldosanio", anio);
                parametros.put(";campo", "o.valor");
                parametros.put("cuentasaldo", "%" + r.getCuenta() + "%");
                double total = ejbKardex.sumarCampo(parametros).doubleValue();
                double valorRas = Math.abs(r.getValor().doubleValue());
//                total -= valorRas;
                if (total == 0) {
                    Entidades e = empleadosBean.traerCedulaEntidad(r.getAuxiliar());
                    if (e != null) {
                        listaEmpleados.add(e);
                    }
                }
            }
            Collections.sort(listaEmpleados, new valorComparatorEmpleados());
            return Combos.getSelectItems(listaEmpleados, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPagosCuentas() {
        if (proveedores) {
            if (proveedor == null) {
                return null;
            }
        } else {
            if (entidad == null) {
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
                parametros.put("auxiliar", proveedor.getRuc());
            } else {
                parametros.put("auxiliar", entidad.getPin());
            }
            parametros.put("cuenta", ctaInicialInterna + "%");
            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
            Calendar cal = Calendar.getInstance();
            cal.setTime(configuracionBean.getConfiguracion().getPinicial());
            int anio = cal.get(Calendar.YEAR);
            List<Renglones> listaSaldos = ejbRenglones.encontarParametros(parametros);
            List<Cuentas> listaCuentas = new LinkedList<>();
            for (Renglones r : listaSaldos) {
                // traer lo pagado
                parametros = new HashMap();
                parametros.put(";where", "o.cuentasaldo like :cuentasaldo"
                        + " and o.saldosanio=:saldosanio and o.documento=:documento");
                parametros.put("saldosanio", anio);
                parametros.put("documento", r.getAuxiliar());
                parametros.put(";campo", "o.valor");
                parametros.put("cuentasaldo", "%" + r.getCuenta() + "%");
                double total = ejbKardex.sumarCampo(parametros).doubleValue();
                double valorRas = Math.abs(r.getValor().doubleValue());
//                total -= valorRas;
                if (total == 0) {
                    listaCuentas.add(getCuentasBean().traerCodigo(r.getCuenta()));
                }
            }
            return Combos.getSelectItems(listaCuentas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    /**
     * @return the proveedor
     */
    public Empresas getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Empresas proveedor) {
        this.proveedor = proveedor;
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
     * @return the valoresPagar
     */
    public List<Renglones> getValoresPagar() {
        return valoresPagar;
    }

    /**
     * @param valoresPagar the valoresPagar to set
     */
    public void setValoresPagar(List<Renglones> valoresPagar) {
        this.valoresPagar = valoresPagar;
    }

    public double getValorPagar() {
        if (proveedores) {
            if (proveedor == null) {
                return 0;
            }
        } else {
            if (entidad == null) {
                return 0;
            }
        }
        if (ctaInicial == null) {
            return 0;
        }
        // va el algoritmo de las cuentas
        Map parametros = new HashMap();
        try {
            parametros.put(";where", "o.cabecera.tipo.codigo like '0%' "
                    + " and o.auxiliar=:auxiliar and o.cuenta = :cuenta and o.cabecera.fecha =:fecha");
            if (proveedores) {
                parametros.put("auxiliar", proveedor.getRuc());
            } else {
                parametros.put("auxiliar", entidad.getPin());
            }
            parametros.put("cuenta", ctaInicial.getCodigo());
            parametros.put("fecha", configuracionBean.getConfiguracion().getPinicial());
            parametros.put(";campo", "o.valor");
            return Math.abs(ejbRenglones.sumarCampo(parametros).doubleValue());

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoSaldosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the proveedoresBeneficiarios
     */
    public boolean isProveedoresBeneficiarios() {
        return proveedoresBeneficiarios;
    }

    /**
     * @param proveedoresBeneficiarios the proveedoresBeneficiarios to set
     */
    public void setProveedoresBeneficiarios(boolean proveedoresBeneficiarios) {
        this.proveedoresBeneficiarios = proveedoresBeneficiarios;
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
}
