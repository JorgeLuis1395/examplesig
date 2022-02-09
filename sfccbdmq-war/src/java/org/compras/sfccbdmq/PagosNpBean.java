/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

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
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AuxiliaresnpFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.PagosnpFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.contabilidad.sfccbdmq.VistaAuxiliaresBean;
import org.entidades.sfccbdmq.Auxiliaresnp;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Pagosnp;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.BorrarException;
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
@ManagedBean(name = "pagosNpSfccbdmq")
@ViewScoped
public class PagosNpBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public PagosNpBean() {
        Calendar c = Calendar.getInstance();
        pagosNp = new LazyDataModel<Pagosnp>() {

            @Override
            public List<Pagosnp> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };
    }
    private LazyDataModel<Pagosnp> pagosNp;
    private List<Auxiliaresnp> detalles;
    private List<Renglones> renglones;
    private Auxiliaresnp detalle;
    private Pagosvencimientos pago;
    private Pagosnp pagoNp = new Pagosnp();
    private Formulario formulario = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioReporte = new Formulario();

    @EJB
    private PagosnpFacade ejbPagosnp;
    @EJB
    private AuxiliaresnpFacade ejbDetalles;
    @EJB
    private PagosvencimientosFacade ejbVencimientos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private TipoasientoFacade ejbTipoasiento;
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
    @ManagedProperty(value = "#{vistaAuxiliaresSfccbdmq}")
    private VistaAuxiliaresBean vistaAuxiliaresBean;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private String concepto;
    private Integer estado;
    private Integer numero;
    private Integer id;
    private Resource reporte;

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
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        if (redirect == null) {
//            return;
        } else {
            String perfilLocal = (String) params.get("p");
            String nombreForma = "PagosNoPresupestarioVista";
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

    public List<Pagosnp> cargaBusqueda(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
        Map parametros = new HashMap();
        if (scs.length == 0) {
            parametros.put(";orden", "o.fecha desc,o.id");
        } else {
            parametros.put(";orden", "o." + scs[0].getPropertyName()
                    + (scs[0].isAscending() ? " ASC" : " DESC"));
        }
        String where = "o.fecha between :desde and :hasta ";
        for (Map.Entry e : map.entrySet()) {
            String claveLocal = (String) e.getKey();
            String valor = (String) e.getValue();

            where += " and upper(o." + claveLocal + ") like :" + claveLocal;
            parametros.put(claveLocal, valor.toUpperCase() + "%");
        }
        if (proveedorBean.getProveedor() != null) {

            where += " and o.proveedor=:proveedor";
            parametros.put("proveedor", proveedorBean.getProveedor());
        }
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        if (!((concepto == null) || (concepto.isEmpty()))) {
            where += " and upper(o.descripcion) like:concepto";
            parametros.put("concepto", concepto.toUpperCase() + "%");
        }

        if (numero != null) {
            where += " and o.id=:documento";
            parametros.put("documento", numero);
        }

        int total = 0;
        try {
            parametros.put(";where", where);
            total = ejbPagosnp.contar(parametros);
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
        pagosNp.setRowCount(total);
        try {
            return ejbPagosnp.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {

        pagosNp = new LazyDataModel<Pagosnp>() {

            @Override
            public List<Pagosnp> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return cargaBusqueda(i, pageSize, scs, map);
            }
        };

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
     * @return the pagosNp
     */
    public LazyDataModel<Pagosnp> getPagosnp() {
        return pagosNp;
    }

    /**
     * @param pagosNp the pagosNp to set
     */
    public void setPagosnp(LazyDataModel<Pagosnp> pagosNp) {
        this.pagosNp = pagosNp;
    }

    /**
     * @return the pagoNp
     */
    public Pagosnp getPagoNp() {
        return pagoNp;
    }

    /**
     * @param pagoNp the pagoNp to set
     */
    public void setPagoNp(Pagosnp pagoNp) {
        this.pagoNp = pagoNp;
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
     * @return the detalles
     */
    public List<Auxiliaresnp> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Auxiliaresnp> detalles) {
        this.detalles = detalles;
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
     * @return the detalle
     */
    public Auxiliaresnp getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Auxiliaresnp detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
    }

    public String cancelar() {
        buscarDetalles();
        formularioDetalle.cancelar();
        return null;
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

    private void traerRenglones() {
        if (pagoNp != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='PAGOSNP'");
            parametros.put("id", pagoNp.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PagosNpBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void buscarDetalles() {
        if (pagoNp != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.pagonp=:pagonp");
            parametros.put("pagonp", pagoNp);
            try {
                detalles = ejbDetalles.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(PagosNpBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String getValorStr() {
        if (detalles == null) {
            return "0.00";
        }
        double valor = 0;
        for (Auxiliaresnp np : detalles) {
            valor += np.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(valor);
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

    public String crear() {
        if (proveedorBean.getProveedor() == null) {

            MensajesErrores.advertencia("Seleccione un proveedor primero");
            return null;
        }
        formulario.insertar();
        pagoNp = new Pagosnp();
        pagoNp.setProveedor(proveedorBean.getProveedor());
        pagoNp.setFecha(new Date());
        pagoNp.setUsuario(seguridadbean.getLogueado().getUserid());
        detalles = new LinkedList<>();
        return null;
    }

    public String modificar(Pagosnp pagoNp) {

        this.pagoNp = pagoNp;
        formulario.editar();
        buscarDetalles();
        return null;
    }

    public String eliminar(Pagosnp pagoNp) {

        this.pagoNp = pagoNp;
        formulario.eliminar();
        buscarDetalles();
        traerRenglones();
        return null;
    }

    private boolean validar() {

        if (pagoNp.getDescripcion() == null || (pagoNp.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Ingrese Descripción");
            return true;
        }
        if (pagoNp.getFecha() == null) {
            MensajesErrores.advertencia("Ingrese Fecha");
            return true;
        }
        if (pagoNp.getCuentas() == null) {
            MensajesErrores.advertencia("Seleccione la configuración de las cuentas");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbPagosnp.create(pagoNp, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();

        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbPagosnp.edit(pagoNp, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabaryContabilizar() {
        if (validar()) {
            return null;
        }
        if (detalles.isEmpty()) {
            MensajesErrores.advertencia("No existen auxiliares");
            return null;
        }
        try {
            double valor = 0;
            renglones = new LinkedList<>();
            String cuentaDebe = pagoNp.getCuentas().getDescripcion();
            String cuentaHaber = pagoNp.getCuentas().getParametros();
            Cuentas ctaDebe = cuentasBean.traerCodigo(cuentaDebe);
            Cuentas ctaHaber = cuentasBean.traerCodigo(cuentaHaber);
            if (ctaDebe == null) {
                MensajesErrores.advertencia("Mal configurada cuenta de debe");
                return null;
            }
            if (ctaHaber == null) {
                MensajesErrores.advertencia("Mal configurada cuenta de haber");
                return null;
            }
            String vale = ejbCabeceras.validarCierre(pagoNp.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            pagoNp.setContabilizacion(new Date());
            
            if (pagoNp.getId() == null) {
                ejbPagosnp.create(pagoNp, seguridadbean.getLogueado().getUserid());
            } else {
                ejbPagosnp.edit(pagoNp, seguridadbean.getLogueado().getUserid());
            }
            for (Auxiliaresnp a : detalles) {
                Renglones renglonDebe = new Renglones();
                if (ctaDebe.getAuxiliares() != null) {
                    renglonDebe.setAuxiliar(a.getAuxiliar());
                }
                renglonDebe.setFecha(pagoNp.getFecha());
                renglonDebe.setCuenta(cuentaDebe);
                renglonDebe.setReferencia(pagoNp.getDescripcion());
                renglonDebe.setValor(a.getValor());
                estaEnRenglones(renglonDebe);
                //
                Renglones renglonHaber = new Renglones();
                if (ctaHaber.getAuxiliares() != null) {
                    renglonHaber.setAuxiliar(a.getAuxiliar());
                }
                renglonHaber.setFecha(pagoNp.getFecha());
                renglonHaber.setCuenta(cuentaHaber);
                renglonHaber.setReferencia(pagoNp.getDescripcion());
                renglonHaber.setValor(a.getValor().negate());
                estaEnRenglones(renglonHaber);
                valor += a.getValor().doubleValue();
            }
            Map parametros = new HashMap();
            parametros.put(";where", " o.modulo=:modulo");
            Codigos modulo = perfil.getMenu().getIdpadre().getModulo();
            parametros.put("modulo", modulo);
            List<Tipoasiento> listaTipo = ejbTipoasiento.encontarParametros(parametros);
            Tipoasiento tipo = null;
            for (Tipoasiento t : listaTipo) {
                tipo = t;
            }
            int numeroAsiento = tipo.getUltimo();
            numeroAsiento++;
            tipo.setUltimo(numeroAsiento);
            ejbTipoasiento.edit(tipo, seguridadbean.getLogueado().getUserid());
            Cabeceras cab = new Cabeceras();
            cab.setFecha(pagoNp.getFecha());
            cab.setDescripcion(pagoNp.getDescripcion());
            cab.setDia(new Date());
            cab.setUsuario(seguridadbean.getLogueado().getUserid());
            cab.setIdmodulo(pagoNp.getId());
            cab.setModulo(modulo);
            cab.setOpcion("PAGOSNP");
            cab.setNumero(numeroAsiento);
            cab.setTipo(tipo);
            ejbCabeceras.create(cab, seguridadbean.getLogueado().getUserid());
            for (Renglones r : renglones) {
                r.setCabecera(cab);
                r.setFecha(cab.getFecha());
                r.setReferencia(cab.getDescripcion());
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
            }
            Pagosvencimientos pago = new Pagosvencimientos();
            pago.setValor(new BigDecimal(valor));
            pago.setPagonp(pagoNp);
            pago.setFecha(cab.getFecha());
            pago.setFechapago(cab.getFecha());
//            pago.set
            ejbVencimientos.create(pago, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        // imprimir nota de egreso casi igual que pagos lotes
        imprimir(pagoNp);
        buscar();
        formulario.cancelar();
        return null;
    }

    private boolean estaEnRenglones(Renglones r1) {
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        for (Renglones r : renglones) {
            if (r.getCuenta().equals(r1.getCuenta())) {
                if (r.getAuxiliar() == null) {
                    r.setAuxiliar("");
                }
                if (r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    r.setFecha(new Date());
                    return true;
                }
            }
        }
        renglones.add(r1);
        return false;
    }

    public String borrar() {
        try {
            for (Auxiliaresnp d : detalles) {
                ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
            }
            Cabeceras cab = null;
            for (Renglones r : renglones) {
                cab = r.getCabecera();
                ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
            }
            if (cab != null) {
                ejbCabeceras.remove(cab, seguridadbean.getLogueado().getUserid());
            }
            ejbPagosnp.remove(pagoNp, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    // detalles
    public String crearDetalle() {
        if (pagoNp.getDescripcion() == null) {

            MensajesErrores.advertencia("Ingrese una descripción primero");
            return null;
        }

        if (pagoNp.getId() == null) {
            try {
                ejbPagosnp.create(pagoNp, seguridadbean.getLogueado().getUserid());
            } catch (InsertarException ex) {
                Logger.getLogger(PagosNpBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        detalle = new Auxiliaresnp();
        detalle.setPagonp(pagoNp);
        formularioDetalle.insertar();
        return null;
    }

    public String modificarDetalle(Auxiliaresnp detalle) {

        this.detalle = detalle;
        formularioDetalle.editar();
        return null;
    }

    public String eliminarDetalle(Auxiliaresnp detalle) {

        this.detalle = detalle;
        formularioDetalle.eliminar();
        return null;
    }

    private boolean validarDetalle() {
        if (vistaAuxiliaresBean.getAuxiliar() == null) {
            MensajesErrores.advertencia("Seleccione un auxiliar");
            return true;
        }
        detalle.setAuxiliar(vistaAuxiliaresBean.getCodigo());
        if (detalle.getAuxiliar() == null || (detalle.getAuxiliar().isEmpty())) {
            MensajesErrores.advertencia("Seleccione un auxiliar");
            return true;
        }
        if (detalle.getValor() == null) {
            MensajesErrores.advertencia("Ingrese un valor");
            return true;
        }
        if (detalle.getValor().doubleValue() <= 0) {
            MensajesErrores.advertencia("Ingrese un valor válido");
            return true;
        }

        return false;
    }

    public String insertarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        try {
            ejbDetalles.create(detalle, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioDetalle.cancelar();

        buscarDetalles();
        return null;
    }

    public String grabarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        try {
            ejbDetalles.edit(detalle, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarDetalles();
        formularioDetalle.cancelar();
        return null;
    }

    public String borrarDetalle() {
        try {

            ejbDetalles.remove(detalle, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarDetalles();
        formularioDetalle.cancelar();
        return null;
    }

    /**
     * @return the vistaAuxiliaresBean
     */
    public VistaAuxiliaresBean getVistaAuxiliaresBean() {
        return vistaAuxiliaresBean;
    }

    /**
     * @param vistaAuxiliaresBean the vistaAuxiliaresBean to set
     */
    public void setVistaAuxiliaresBean(VistaAuxiliaresBean vistaAuxiliaresBean) {
        this.vistaAuxiliaresBean = vistaAuxiliaresBean;
    }

    public String imprimir(Pagosnp pagoNp) {

        try {
            this.pagoNp = pagoNp;
            traerRenglones();
            formularioReporte.insertar();
//        Hacer el reporte
            String empresa = pagoNp.getProveedor().getEmpresa().toString();
            String email = pagoNp.getProveedor().getEmpresa().getEmail();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("ORDEN DE PAGO NO : PNP" + pagoNp.getId(), null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("Beneficiario : " + empresa);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", sdf.format(pagoNp.getFecha())));
//            columnas.add(new AuxiliarReporte("String", "Beneficiario"));
//            columnas.add(new AuxiliarReporte("String", empresa)
//            );
            columnas.add(new AuxiliarReporte("String", "email"));
            columnas.add(new AuxiliarReporte("String", email));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            // un parafo
            pdf.agregaParrafo("Concepto : " + pagoNp.getDescripcion() + "\n\n");
            DecimalFormat df = new DecimalFormat("000000");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
//            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));

            columnas = new LinkedList<>();
//            valorModificaciones=0;
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;

            for (Renglones r : renglones) {
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAuxiliar()));
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    sumaDebitos += valor;
                } else {
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valor * -1));
                    sumaCreditos += valor * -1;
                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION PAGOS NO PRESUPUESTARIOS");
            // reclasificación

            pdf.agregaParrafo("\n\n");

            // reembolsos fin
            pdf.agregaParrafo("\n\n");
            Codigos textoCodigo = getCodigosBean().traerCodigo("PAGOS", "PAGOS");
            String texto = "";
            if (textoCodigo != null) {
                texto = textoCodigo.getParametros().replace("#compromiso#", "PNP" + pagoNp.getId().toString());
            }

            pdf.agregaParrafo("Programación de Caja : \n\n");
            pdf.agregaParrafo(texto);
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", "Pagado por:"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", "Contador"));
            columnas.add(new AuxiliarReporte("String", "Director Financiero"));
            columnas.add(new AuxiliarReporte("String", "Tesorero"));

            pdf.agregarTabla(null, columnas, 4, 100, null);
            setReporte(pdf.traerRecurso());

//        formulario.editar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(PagoRetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
}
