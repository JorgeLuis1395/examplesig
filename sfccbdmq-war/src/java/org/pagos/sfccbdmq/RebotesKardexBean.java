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
import java.text.SimpleDateFormat;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
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
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "rebotesKardexSfccbdmq")
@ViewScoped
public class RebotesKardexBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public RebotesKardexBean() {
        Calendar c = Calendar.getInstance();
        listadoKardex = new LazyDataModel<Kardexbanco>() {
            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Kardexbanco> listadoKardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioRebote = new Formulario();
    private Formulario formularioPago = new Formulario();
    private Cabeceras asiento;
    private Cabeceras asientoReclasificacion;
    private String comprobanteEgreso = "Comprobante de Egreso";
    private Resource reporte;

    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabecera;
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
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String concepto;
    private String cuentaAntigua;
    private String auxiliarAntigua;
    private Tipoegreso tipoEgreso;
    private Codigos tipoDocumento;
    private Contratos contrato;
    private Integer numero;
    private Integer id;
    private Kardexbanco kardex;
    private Kardexbanco kardexRebote;
    private Integer numeroSpi;
    private List<Renglones> renglones;
    private List<Renglones> ras;
    private List<Renglones> rasReclasificaion;
    private boolean pagados;
    private double cuantoAnticipo;
    private Anticipos anticipoAplicar;
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
            String nombreForma = "RebotesVista";
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
        String where = " o.fechamov between :desde and :hasta and o.estado in (2,4)";
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
            parametros.put("concepto", getConcepto().toUpperCase() + "%");
        }
        if (tipoEgreso != null) {
            where += " and o.obligacion.tipopago=:tipopago";
            parametros.put("tipopago", tipoEgreso);
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
//            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion like 'PAGO%'");
            parametros.put(";where", "o.cabecera.idmodulo=:id and (o.cabecera.opcion like 'PAGO%' or o.cabecera.opcion like 'REBOTE_PROVEEDORES%')");
            parametros.put("id", kardex.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : renglones) {
                    if (r.getValor().doubleValue() > 0) {
                        cuentaAntigua = r.getCuenta();
                        auxiliarAntigua = r.getAuxiliar();
                    }
                    r.setSigno(-1);
                }
                // traer reclasificacion
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id  and o.cabecera.opcion like 'PAGO PROVEEDORES RECLASIFICACION%'");
                parametros.put("id", kardex.getId());
//                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                rasReclasificaion = ejbRenglones.encontarParametros(parametros);
//                formularioReporte.insertar();
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void traerRenglonesRol() {
        renglones = new LinkedList<>();
        try {
            if (kardex != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.spi=:spi");
                parametros.put("spi", kardex.getSpi());
                parametros.put(";orden", "o.id desc");
                List<Kardexbanco> lista = ejbKardex.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Kardexbanco k = lista.get(0);
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera.idmodulo=:id and (o.cabecera.opcion like 'PAGO%' or o.cabecera.opcion like 'REBOTE_PROVEEDORES%')"
                            + " and o.auxiliar=:auxiliar");
                    parametros.put("id", k.getId());
                    parametros.put("auxiliar", kardex.getAuxiliar());
                    renglones = ejbRenglones.encontarParametros(parametros);
                    for (Renglones r : renglones) {
                        if (r.getValor().doubleValue() > 0) {
                            cuentaAntigua = r.getCuenta();
                            auxiliarAntigua = r.getAuxiliar();
                        }
                        r.setSigno(-1);
                        double valorBanco = r.getValor().doubleValue() * -1;
                        Renglones rBanco = new Renglones();

                        rBanco.setCuenta(kardex.getBanco().getCuenta());
                        rBanco.setReferencia(r.getReferencia());
                        rBanco.setValor(new BigDecimal(valorBanco));
                        rBanco.setSigno(-1);
//Le pongo al id solo para q pase la validacion al grabar por que  se crea el otro asiento a partir del asiento 
//que ya esta generado y para el nuevo asiento le estoy poniendo el valor al banco

                        rBanco.setId(1);
                        renglones.add(rBanco);
                        return;
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void traerRenglonesPositivo() {
        if (kardex != null) {
            Map parametros = new HashMap();
//            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion like 'PAGO%'");
            parametros.put(";where", "o.cabecera.idmodulo=:id and (o.cabecera.opcion like 'PAGO%' or o.cabecera.opcion like 'REBOTE_PROVEEDORES%')");
            parametros.put("id", kardex.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : renglones) {
                    if (r.getValor().doubleValue() > 0) {
                        cuentaAntigua = r.getCuenta();
                        auxiliarAntigua = r.getAuxiliar();
                    }
                    r.setSigno(1);
                }
                // traer reclasificacion
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:id  and o.cabecera.opcion like 'PAGO PROVEEDORES RECLASIFICACION%'");
                parametros.put("id", kardex.getId());
//                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                rasReclasificaion = ejbRenglones.encontarParametros(parametros);
//                formularioReporte.insertar();
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void traerRenglonesRolPositivo() {
        renglones = new LinkedList<>();
        try {
            if (kardex != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.spi=:spi");
                parametros.put("spi", kardex.getSpi());
                parametros.put(";orden", "o.id desc");
                List<Kardexbanco> lista = ejbKardex.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Kardexbanco k = lista.get(0);
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera.idmodulo=:id and (o.cabecera.opcion like 'PAGO%' or o.cabecera.opcion like 'REBOTE_PROVEEDORES%')"
                            + " and o.auxiliar=:auxiliar");
                    parametros.put("id", k.getId());
                    parametros.put("auxiliar", kardex.getAuxiliar());
                    renglones = ejbRenglones.encontarParametros(parametros);
                    for (Renglones r : renglones) {
                        if (r.getValor().doubleValue() > 0) {
                            cuentaAntigua = r.getCuenta();
                            auxiliarAntigua = r.getAuxiliar();
                        }
                        r.setSigno(1);
                        double valorBanco = r.getValor().doubleValue() * -1;
                        Renglones rBanco = new Renglones();
                        rBanco.setCuenta(kardex.getBanco().getCuenta());
                        rBanco.setReferencia(r.getReferencia());
                        rBanco.setValor(new BigDecimal(valorBanco));
                        rBanco.setSigno(1);
//Le pongo al id solo para q pase la validacion al grabar por que  se crea el otro asiento a partir del asiento 
//que ya esta generado y para el nuevo asiento le estoy poniendo el valor al banco
                        rBanco.setId(1);
                        renglones.add(rBanco);
                        return;
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private boolean estaEnRenglones(Renglones r1) {
        for (Renglones r : renglones) {
            if (r.getCuenta().equals(r1.getCuenta())) {
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
        for (Renglones r : rasReclasificaion) {
            if (r.getCuenta().equals(r1.getCuenta())) {
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
        if (kardex == null) {
            return "";
        }

        if (kardex.getValor() == null) {
            kardex.setValor(BigDecimal.ZERO);
        }
        return ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue());
    }

    public String getValorStr() {
        if (kardex == null) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (kardex.getValor() == null) {
            kardex.setValor(BigDecimal.ZERO);
        }
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

    public String imprimirKardex(Kardexbanco kardex) {

//        pagos = new LinkedList<>();
//        formulario.editar();
        setKardex(kardex);
        Map parametros = new HashMap();
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", kardex);
        traerRenglones();
        formularioReporte.insertar();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("Comprobante de Egreso No: " + kardex.getId(), null, seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor :         " + getValorStr()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "No documento :         " + (kardex.getEgreso() != null ? kardex.getEgreso() + "" : "")));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Pagese a la orden de : " + (kardex.getBeneficiario() != null ? kardex.getBeneficiario() : "")));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "La suma de :           " + getCuantoStr()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha de emisión:      " + (sdf.format(kardex.getFechamov()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaParrafo("\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Origen"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Banco :    " + (kardex.getBanco() != null ? kardex.getBanco().getNombre() : "")));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Cta No:    " + (kardex.getBanco() != null ? kardex.getBanco().getNumerocuenta() : "")));
            if (kardex.getFormapago().getParametros().equals("C")) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Cheque No: " + (kardex.getDocumento() != null ? kardex.getDocumento() : "")));
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "SPI NO:    " + (kardex.getSpi() != null ? kardex.getSpi().getNumero() + "" : "")));
            pdf.agregarTabla(null, columnas, 1, 100, "");
            pdf.agregaParrafo("\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Destino"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Banco :    " + (kardex.getBancotransferencia() != null ? kardex.getBancotransferencia().toString() : "")));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Cta No:    " + (kardex.getCuentatrans() != null ? kardex.getCuentatrans() : "")));
            pdf.agregarTabla(null, columnas, 1, 100, "");

            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Referencia: " + (kardex.getObservaciones() != null ? kardex.getObservaciones() : "")));
            pdf.agregarTabla(null, columnas, 1, 100, "");
            pdf.agregaParrafo("\n\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglones == null) {
                renglones = new LinkedList<>();
            }
            columnas = new LinkedList<>();
            for (Renglones r : renglones) {
                String cuenta = "";
                String auxiliar = r.getAuxiliar();
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                if (cta != null) {
                    cuenta = cta.getNombre();
                    if (cta.getAuxiliares() != null) {
                        switch (cta.getAuxiliares().getParametros()) {
                            case "P": {
                                Empresas p = proveedorBean.taerRuc(r.getAuxiliar());
                                if (p != null) {
                                    auxiliar = p.toString();

                                }
                                break;
                            }
                            case "E":
                                String e = empleadosBean.traerCedula(r.getAuxiliar());
                                if (e != null) {
                                    auxiliar = e;

                                }
                                break;
                            case "C": {
                                Empresas p = proveedorBean.taerRuc(r.getAuxiliar());
                                if (p != null) {
                                    auxiliar = p.toString();

                                }
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));

                double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                if (valor > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    sumaDebitos += valor;
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                    sumaCreditos += valor * -1;
                }

            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Preparado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporte = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String reboteKardex(Kardexbanco kardex) {

        setKardex(kardex);
        kardexRebote = new Kardexbanco();
        traerRenglones();
        if (renglones.isEmpty()) {
            traerRenglonesRol();
        }
        formularioRebote.insertar();
        return null;
    }

    public String pagarReboteKardex(Kardexbanco kardex) {

        try {
            setKardex(kardex);
            traerRenglonesPositivo();
            if (renglones.isEmpty()) {
                traerRenglonesRolPositivo();
            }
            kardexRebote = new Kardexbanco();
            Map parametros = new HashMap();
            parametros.put(";campo", "o.numero");
            numeroSpi = ejbSpi.maximoNumero(parametros);
            if ((numeroSpi == null) || (numeroSpi == 0)) {
                numeroSpi = 307;
            }
            numeroSpi++;
            formularioRebote.editar();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RebotesKardexBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabaReboteKardex() {
        if (kardexRebote.getFechamov() == null) {
            MensajesErrores.fatal("Es necesario fecha");
            return null;
        }
        if (kardexRebote.getTipomov() == null) {
            MensajesErrores.fatal("Es necesario Tipo de movimiento");
            return null;
        }
        if (kardexRebote.getFechamov().before(kardex.getFechamov())) {
            MensajesErrores.fatal("Es necesario fecha de movientos ea mayor a fecha de egreso");
            return null;
        }
        String vale = ejbCabecera.validarCierre(kardexRebote.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        try {
            Tipoasiento ta = kardexRebote.getTipomov().getTipoasiento();
            ejbKardex.create(kardexRebote, seguridadbean.getLogueado().getUserid());
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            ras = new LinkedList<>();
            asiento = new Cabeceras();
//            asiento.setDescripcion("Pago de rebote: " + kardex.getObservaciones());
            asiento.setDescripcion("Asiento de rebote: " + kardex.getObservaciones());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(kardexRebote.getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardexRebote.getFechamov());
            asiento.setIdmodulo(kardexRebote.getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("REBOTE_PROVEEDORES");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            for (Renglones r1 : renglones) {
                if (r1.getId() != null) {
                    Renglones r = new Renglones();
                    r.setCuenta(r1.getCuenta());
                    // es lo mismo dice gerardo
                    r.setValor(r1.getValor());
                    r.setAuxiliar(r1.getAuxiliar());
                    r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
                    r.setFecha(kardexRebote.getFechamov());
                    r.setCabecera(asiento);
                    r.setSigno(-1);

                    ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                }
            }
//            renglones = new LinkedList<>();
//            Renglones r = new Renglones();
//            r.setCuenta(kardex.getBanco().getCuenta());
//            // es lo mismo dice gerardo
//            r.setValor(kardex.getValor().negate());
//            r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
//            r.setFecha(kardexRebote.getFechamov());
//            r.setCabecera(asiento);
//            r.setSigno(-1);
//
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            renglones.add(r);
//            r = new Renglones();
//            r.setCuenta(cuentaAntigua);
////            r.setCuenta(kardexRebote.getTipomov().getCuenta());
//            r.setValor(kardex.getValor());
//            r.setReferencia("Asiento de Rebote Egreso NO : " + kardex.getId().toString());
//            r.setFecha(kardexRebote.getFechamov());
//            r.setCabecera(asiento);
//            r.setSigno(-1);
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            renglones.add(r);
            kardex.setEstado(4);
            kardexRebote.setProveedor(kardex.getProveedor());
            kardexRebote.setBanco(kardex.getBanco());
            kardexRebote.setObservaciones("Rebote Egreso NO : " + kardex.getId().toString());

            kardexRebote.setBeneficiario(kardex.getBeneficiario());
            kardexRebote.setFechagraba(new Date());
            kardexRebote.setOrigen("PAGO PROVEEDORES");
//            kardex.setOrigen("REBOTES");
            kardexRebote.setEstado(5);
            kardexRebote.setValor(kardex.getValor());
            kardexRebote.setBancotransferencia(kardex.getBancotransferencia());
            kardexRebote.setCuentatrans(kardex.getCuentatrans());
            kardexRebote.setTcuentatrans(kardex.getTcuentatrans());
            kardexRebote.setDocumento(kardex.getDocumento());
            kardexRebote.setFormapago(kardex.getFormapago());
            ejbKardex.edit(kardexRebote, seguridadbean.getLogueado().getUserid());
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RebotesKardexBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        kardex = kardexRebote;
        formularioRebote.cancelar();
        comprobanteEgreso = "Comprobante de Ingreso";
        imprimirKardex(kardex);
        return null;
    }

    public String grabaPagoReboteKardex() {
        if (kardexRebote.getFechamov() == null) {
            MensajesErrores.fatal("Es necesario fecha");
            return null;
        }
        if (kardexRebote.getTipomov() == null) {
            MensajesErrores.fatal("Es necesario Tipo de movimiento");
            return null;
        }
        if (kardexRebote.getFechamov().before(kardex.getFechamov())) {
            MensajesErrores.fatal("Es necesario fecha de movientos ea mayor a fecha de egreso");
            return null;
        }
        String vale = ejbCabecera.validarCierre(kardexRebote.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        try {
            Tipoasiento ta = kardexRebote.getTipomov().getTipoasiento();
            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
            ejbKardex.create(kardexRebote, seguridadbean.getLogueado().getUserid());
            ras = new LinkedList<>();
            asiento = new Cabeceras();
            asiento.setDescripcion("Pago de Rebote Egreso NO : " + kardex.getId().toString());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setDia(new Date());
            asiento.setTipo(kardexRebote.getTipomov().getTipoasiento());
            asiento.setNumero(numeroAsiento);
            asiento.setFecha(kardexRebote.getFechamov());
            asiento.setIdmodulo(kardexRebote.getId());
            asiento.setUsuario(seguridadbean.getLogueado().getUserid());
            asiento.setModulo(perfil.getMenu().getIdpadre().getModulo());
            asiento.setOpcion("PAGO_REBOTE_PROVEEDORES");
            ejbCabecera.create(asiento, seguridadbean.getLogueado().getUserid());
            renglones = new LinkedList<>();
            Renglones r = new Renglones();
            r.setCuenta(cuentaAntigua);
//            r.setCuenta(kardexRebote.getTipomov().getCuenta());
            r.setValor(kardex.getValor());
            kardexRebote.setObservaciones("Pago de : " + kardex.getObservaciones());
            r.setCabecera(asiento);
            r.setFecha(kardexRebote.getFechamov());
            r.setSigno(1);
            r.setReferencia("Pago de Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            r.setAuxiliar(auxiliarAntigua);
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            renglones.add(r);
            r = new Renglones();
            r.setCuenta(kardex.getBanco().getCuenta());
            r.setValor(new BigDecimal(kardex.getValor().doubleValue() * -1));
            r.setReferencia("Pago de Asiento de Rebote Egreso NO : " + kardex.getId().toString());
            r.setFecha(kardexRebote.getFechamov());
            r.setCabecera(asiento);
            r.setSigno(1);
            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            renglones.add(r);

            Spi spi = new Spi();
            spi.setEstado(0);
//            spi.setFecha(kardex.getFechamov());
            spi.setFecha(kardexRebote.getFechamov());
            spi.setUsuario(seguridadbean.getLogueado().getUserid());
            spi.setBanco(kardex.getBanco());
//            spi.setNumero(numeroSpi);
            spi.setNumero(kardexRebote.getEgreso());
            MensajesErrores.informacion("Número de SPI :" + numeroSpi.toString());
            ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
            kardex.setEstado(5);
            kardexRebote.setProveedor(kardex.getProveedor());
            kardexRebote.setBanco(kardex.getBanco());
            kardexRebote.setObservaciones("Pago de : " + kardex.getObservaciones());
            kardexRebote.setBeneficiario(kardex.getBeneficiario());
            kardexRebote.setFechagraba(new Date());
            kardexRebote.setOrigen("REBOTE");
            kardexRebote.setEstado(2);
            kardexRebote.setSpi(spi);
            kardexRebote.setValor(kardex.getValor());
            kardexRebote.setDocumento(kardex.getDocumento());
            kardexRebote.setFormapago(kardex.getFormapago());
            ejbKardex.edit(kardexRebote, seguridadbean.getLogueado().getUserid());
            ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
            // crear el spi
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RebotesKardexBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        kardex = kardexRebote;
        formularioRebote.cancelar();
        comprobanteEgreso = "Comprobante de Egreso";
        imprimirKardex(kardex);
        return null;
    }

    public String getValorAPagar() {
        double retorno = 0;
        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        return df.format(retorno);
    }

    public double getApagar() {
        double retorno = 0;

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
     * @return the auxiliarAntigua
     */
    public String getAuxiliarAntigua() {
        return auxiliarAntigua;
    }

    /**
     * @param auxiliarAntigua the auxiliarAntigua to set
     */
    public void setAuxiliarAntigua(String auxiliarAntigua) {
        this.auxiliarAntigua = auxiliarAntigua;
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
}
