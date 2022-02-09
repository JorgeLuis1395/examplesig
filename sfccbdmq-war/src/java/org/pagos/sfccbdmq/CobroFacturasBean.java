/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.ReciboIngresos;
import org.beans.sfccbdmq.CabecerafacturasFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallefacturasFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.delectronicos.sfccbdmq.Factura;
import org.entidades.sfccbdmq.Cabecerafacturas;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Clientes;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallefacturas;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Productos;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.personal.sfccbdmq.ValesCajaBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "cobroFacturasSfcbdmq")
@ViewScoped
public class CobroFacturasBean implements Serializable {

    /**
     * @return the cuentaRelacionada
     */
    public Codigos getCuentaRelacionada() {
        return cuentaRelacionada;
    }

    /**
     * @param cuentaRelacionada the cuentaRelacionada to set
     */
    public void setCuentaRelacionada(Codigos cuentaRelacionada) {
        this.cuentaRelacionada = cuentaRelacionada;
    }

    /**
     * @return the fechaCxc
     */
    public Date getFechaCxc() {
        return fechaCxc;
    }

    /**
     * @param fechaCxc the fechaCxc to set
     */
    public void setFechaCxc(Date fechaCxc) {
        this.fechaCxc = fechaCxc;
    }

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadBean;
    @ManagedProperty(value = "#{clientesSfccbdmq}")
    private ClientesBean clientesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    private Formulario formulario = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioCxc = new Formulario();

    private List<Cabecerafacturas> listaFacturas;
    private List<Productos> productos;
    private List<Detallefacturas> listaDetalle;
    private List<Renglones> listaReglonesInversion;
    private String clave;
    private Codigos cuentaRelacionada;
    private Perfiles perfil;
    private Codigos tipoDocumento;
    private Puntoemision puntoEmision;
    private Kardexbanco kardexPropuesta;
    private List<Renglones> renglones;
    private List<Renglones> rasReclasificaion;
    private Resource reporte;
    private String cuentaCompra;
    private Date fechaCxc;
    private Resource reporteRecibo;
    private String proyecto;
    private String partida;
    private String fuente;

    @EJB
    private CabecerafacturasFacade ejbCabecerafacturas;
    @EJB
    private DetallefacturasFacade ejbDetallefacturas;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private KardexbancoFacade ejbKardex;

    public CobroFacturasBean() {
    }

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String p = (String) params.get("p");
        String nombreForma = "CobroFacturasVista";

        if (p == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadBean.cerraSession();
        }
        perfil = seguridadBean.traerPerfil((String) params.get("p"));
        if (this.perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadBean.cerraSession();
        }
//        if (nombreForma.contains(perfil.getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadBean.getGrupo().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadBean.cerraSession();
//            }
//        }
    }

    public String buscar() {
        Map parametros = new HashMap();
        String where = "o.fechacontabilizacion is not null and o.kardexbanco is null and o.cuentacobro is null";
        if (clientesBean.getCliente().getId() == null) {
            MensajesErrores.advertencia("Seleccione un Cliente");
            return null;
        }
        where += " and  o.cliente=:cliente";
        parametros.put("cliente", clientesBean.getCliente());
        parametros.put(";where", where);

        parametros.put(";orden", "o.fecha desc");
        try {
            listaFacturas = ejbCabecerafacturas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the seguridadBean
     */
    public ParametrosSfccbdmqBean getSeguridadBean() {
        return seguridadBean;
    }

    /**
     * @param seguridadBean the seguridadBean to set
     */
    public void setSeguridadBean(ParametrosSfccbdmqBean seguridadBean) {
        this.seguridadBean = seguridadBean;
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
     * @return the listaFacturas
     */
    public List<Cabecerafacturas> getListaFacturas() {
        return listaFacturas;
    }

    /**
     * @param listaFacturas the listaFacturas to set
     */
    public void setListaFacturas(List<Cabecerafacturas> listaFacturas) {
        this.listaFacturas = listaFacturas;
    }

    /**
     * @return the productos
     */
    public List<Productos> getProductos() {
        return productos;
    }

    /**
     * @param productos the productos to set
     */
    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
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

    /**
     * @return the listaDetalle
     */
    public List<Detallefacturas> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * @param listaDetalle the listaDetalle to set
     */
    public void setListaDetalle(List<Detallefacturas> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    /**
     * @return the clientesBean
     */
    public ClientesBean getClientesBean() {
        return clientesBean;
    }

    /**
     * @param clientesBean the clientesBean to set
     */
    public void setClientesBean(ClientesBean clientesBean) {
        this.clientesBean = clientesBean;
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
     * @return the puntoEmision
     */
    public Puntoemision getPuntoEmision() {
        return puntoEmision;
    }

    /**
     * @param puntoEmision the puntoEmision to set
     */
    public void setPuntoEmision(Puntoemision puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    /**
     * @return the listaReglonesInversion
     */
    public List<Renglones> getListaReglonesInversion() {
        return listaReglonesInversion;
    }

    /**
     * @param listaReglonesInversion the listaReglonesInversion to set
     */
    public void setListaReglonesInversion(List<Renglones> listaReglonesInversion) {
        this.listaReglonesInversion = listaReglonesInversion;
    }

    /**
     * @return the formularioContabilizar
     */
    public Formulario getFormularioContabilizar() {
        return formularioContabilizar;
    }

    /**
     * @param formularioContabilizar the formularioContabilizar to set
     */
    public void setFormularioContabilizar(Formulario formularioContabilizar) {
        this.formularioContabilizar = formularioContabilizar;
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
     * **********************************************************************
     */
    public String seleccionarTodos() {
        for (Cabecerafacturas cf : listaFacturas) {
            cf.setSeleccionar(true);
        }
        return null;
    }

    public String quitarTodos() {
        for (Cabecerafacturas cf : listaFacturas) {
            cf.setSeleccionar(false);
        }
        return null;
    }

    public String pagarSeleccionados() {
        reporteRecibo = null;
        boolean si = false;
        Clientes cliente = null;
        int masDeUno = 0;
        double valor = 0;
        proyecto = "";
        partida = "";
        fuente = "";

        try {
            for (Cabecerafacturas cf : listaFacturas) {

                if (cf.isSeleccionar()) {
                    si = true;
                    Clientes cli = cf.getCliente();
                    if (!cli.equals(cliente)) {
                        masDeUno++;
                        cliente = cli;
                    }
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.factura=:factura");
                    parametros.put("factura", cf);
                    List<Detallefacturas> lista = ejbDetallefacturas.encontarParametros(parametros);
                    if (!lista.isEmpty()) {
                        Detallefacturas df = lista.get(0);
                        proyecto = df.getAsignacion().getProyecto().getCodigo();
                        partida = df.getAsignacion().getClasificador().getCodigo();
                        fuente = df.getAsignacion().getFuente().getCodigo();
                    }
                }
            }
            if (masDeUno > 1) {
                MensajesErrores.advertencia("Solo se puede pagar agrupado a un cliente");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        setKardexPropuesta(new Kardexbanco());
        getKardexPropuesta().setFechamov(new Date());
        getKardexPropuesta().setFechaabono(new Date());
        getKardexPropuesta().setFechagraba(new Date());
        getKardexPropuesta().setFechamov(new Date());
        getKardexPropuesta().setValor(new BigDecimal(getValorTotal()));
        getKardexPropuesta().setNumerorecibo(0);
        getKardexPropuesta().setBeneficiario(clientesBean.getEmpresa().getNombre());
        getKardexPropuesta().setAuxiliar(clientesBean.getEmpresa().getRuc());
        formulario.insertar();
        return null;
    }

    public String pagarSeleccionadosCxc() {
        reporteRecibo = null;
        boolean si = false;
        Clientes cliente = null;
        int masDeUno = 0;
        double valor = 0;
        try {
            for (Cabecerafacturas cf : listaFacturas) {
                if (cf.isSeleccionar()) {
                    si = true;
                    Clientes cli = cf.getCliente();
                    if (!cli.equals(cliente)) {
                        masDeUno++;
                        cliente = cli;
                    }
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.factura=:factura");
                    parametros.put("factura", cf);
                    List<Detallefacturas> lista;
                    lista = ejbDetallefacturas.encontarParametros(parametros);

                    if (!lista.isEmpty()) {
                        Detallefacturas df = lista.get(0);
                        proyecto = df.getAsignacion().getProyecto().getCodigo();
                        partida = df.getAsignacion().getClasificador().getCodigo();
                        fuente = df.getAsignacion().getFuente().getCodigo();
                    }
                }
            }
            if (masDeUno > 1) {
                MensajesErrores.advertencia("Solo se puede pagar agrupado a un cliente");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        fechaCxc = new Date();
        formularioCxc.insertar();
        return null;
    }

    /**
     * @return the kardexPropuesta
     */
    public Kardexbanco getKardexPropuesta() {
        return kardexPropuesta;
    }

    /**
     * @param kardexPropuesta the kardexPropuesta to set
     */
    public void setKardexPropuesta(Kardexbanco kardexPropuesta) {
        this.kardexPropuesta = kardexPropuesta;
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

    public double getValorTotal() {
        double retorno = 0;
        if (listaFacturas == null) {
            return 0;
        }
        try {
            for (Cabecerafacturas cf : listaFacturas) {

                if (cf.isSeleccionar()) {
                    // traer sumado los detalles
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.factura=:factura");
                    parametros.put("factura", cf);
//                    parametros.put(";campo", "(o.valor*o.cantidad)*(1+valorimpuesto/100)-(o.valor*o.cantidad)*(1-descuento/100)");
                    List<Detallefacturas> lista = ejbDetallefacturas.encontarParametros(parametros);
                    double valor = 0;
                    for (Detallefacturas d : lista) {
                        double valorProducto = d.getCantidad().doubleValue() * d.getValor().doubleValue();
                        double valorDescuento = valorProducto * (d.getDescuento().doubleValue() / 100);
                        double valorImpuesto = (valorProducto - valorDescuento) * (d.getValorimpuesto().doubleValue() / 100);
                        double valorLinea = valorProducto + valorImpuesto;
                        valor += valorLinea;
                    }
                    retorno += valor;
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getValorImpuesto() {
        double retorno = 0;
        if (listaFacturas == null) {
            return 0;
        }
        try {
            for (Cabecerafacturas cf : listaFacturas) {

                if (cf.isSeleccionar()) {
                    // traer sumado los detalles
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.factura=:factura");
                    parametros.put("factura", cf);
//                    parametros.put(";campo", "(o.valor*o.cantidad)*(1+valorimpuesto/100)-(o.valor*o.cantidad)*(1-descuento/100)");
                    List<Detallefacturas> lista = ejbDetallefacturas.encontarParametros(parametros);
                    double valor = 0;
                    for (Detallefacturas d : lista) {
                        double valorProducto = d.getCantidad().doubleValue() * d.getValor().doubleValue();
                        double valorDescuento = valorProducto * (d.getDescuento().doubleValue() / 100);
                        double valorImpuesto = (valorProducto - valorDescuento) * (d.getValorimpuesto().doubleValue() / 100);
                        double valorLinea = valorImpuesto;
                        valor += valorLinea;
                        cuentaCompra = d.getImpuesto().getCuentacompras();
                    }
                    retorno += valor;
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getValor() {
        if (listaFacturas == null) {
            return 0;
        }
        if (listaFacturas.isEmpty()) {
            return 0;
        }
        double retorno = 0;
        Cabecerafacturas cf = listaFacturas.get(formulario.getFila().getRowIndex());
        try {
            // traer sumado los detalles
            Map parametros = new HashMap();
            parametros.put(";where", "o.factura=:factura");
            parametros.put("factura", cf);
//                    parametros.put(";campo", "(o.valor*o.cantidad)*(1+valorimpuesto/100)-(o.valor*o.cantidad)*(1-descuento/100)");
            List<Detallefacturas> lista = ejbDetallefacturas.encontarParametros(parametros);
            double valor = 0;
            for (Detallefacturas d : lista) {

                double valorProducto = d.getCantidad().doubleValue() * d.getValor().doubleValue();
                double valorDescuento = valorProducto * (d.getDescuento().doubleValue() / 100);
                double valorImpuesto = (valorProducto - valorDescuento) * (d.getValorimpuesto().doubleValue() / 100);
                double valorLinea = valorProducto + valorImpuesto;
                valor += valorLinea;
            }
            retorno = valor;
        } catch (ConsultarException ex) {
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
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

    public List<Renglones> getListaRenglones() {
        if (clientesBean.getCliente().getId() == null) {
            return null;
        }
        if (kardexPropuesta == null) {
            return null;
        }
        if (kardexPropuesta.getBanco() == null) {
            return null;
        }
        listaReglonesInversion = new LinkedList<>();
        List<Renglones> listaRenglones = new LinkedList<>();
        double valor = getValorTotal();
        double impuesto = getValorImpuesto();
        Renglones renglonCliente = new Renglones();
        Cuentas cuenta = cuentasBean.traerCodigo(clientesBean.getCliente().getCuentaingresos());
        if (cuenta.getAuxiliares() != null) {
            renglonCliente.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
        }
        renglonCliente.setCuenta(cuenta.getCodigo());
        renglonCliente.setValor(new BigDecimal((valor - impuesto) * -1));
        listaRenglones.add(renglonCliente);
        //Impuesto
        if (impuesto != 0) {
            Renglones renglonImpuesto = new Renglones();
            Cuentas cuentaCompra2 = cuentasBean.traerCodigo(cuentaCompra);
            if (cuentaCompra2.getAuxiliares() != null) {
                renglonImpuesto.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
            }
            renglonImpuesto.setCuenta(cuentaCompra2.getCodigo());
            renglonImpuesto.setValor(new BigDecimal(impuesto * -1));
            listaRenglones.add(renglonImpuesto);
        }
        //Fin impuesto
        // llena el uno
        Renglones renglonBanco = new Renglones();
        Cuentas cuentaBanco = cuentasBean.traerCodigo(kardexPropuesta.getBanco().getCuenta());
        if (cuentaBanco.getAuxiliares() != null) {
            renglonBanco.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
        }
        renglonBanco.setCuenta(cuentaBanco.getCodigo());
        renglonBanco.setValor(new BigDecimal(valor));
        listaRenglones.add(renglonBanco);
        return listaRenglones;
    }

    public List<Renglones> getListaRenglonesCxc() {
        if (clientesBean.getCliente().getId() == null) {
            return null;
        }
        if (cuentaRelacionada == null) {
            return null;
        }

        listaReglonesInversion = new LinkedList<>();
        List<Renglones> listaRenglones = new LinkedList<>();
        double valor = getValorTotal();
        double impuesto = getValorImpuesto();
        Renglones renglonCliente = new Renglones();
        Cuentas cuenta = cuentasBean.traerCodigo(clientesBean.getCliente().getCuentaingresos());
        if (cuenta.getAuxiliares() != null) {
            renglonCliente.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
        }
        renglonCliente.setCuenta(cuenta.getCodigo());
        renglonCliente.setValor(new BigDecimal((valor - impuesto) * -1));
        listaRenglones.add(renglonCliente);
        //Impuesto
        if (impuesto != 0) {
            Renglones renglonImpuesto = new Renglones();
            Cuentas cuentaCompra2 = cuentasBean.traerCodigo(cuentaCompra);
            if (cuentaCompra2.getAuxiliares() != null) {
                renglonImpuesto.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
            }
            renglonImpuesto.setCuenta(cuentaCompra2.getCodigo());
            renglonImpuesto.setValor(new BigDecimal(impuesto * -1));
            listaRenglones.add(renglonImpuesto);
        }
        //Fin impuesto
        // llena el uno
        Renglones renglonBanco = new Renglones();
        Cuentas cuentaBanco = cuentasBean.traerCodigo(cuentaRelacionada.getParametros());
        if (cuentaBanco.getAuxiliares() != null) {
            renglonBanco.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
        }
        renglonBanco.setCuenta(cuentaBanco.getCodigo());
        renglonBanco.setValor(new BigDecimal(valor));
        listaRenglones.add(renglonBanco);
        return listaRenglones;
    }

    public String grabarPago() {
        if (kardexPropuesta.getBanco() == null) {
            MensajesErrores.advertencia("Seleccione un banco primero");
            return null;
        }
        if (kardexPropuesta.getEgreso() == null) {
            MensajesErrores.advertencia("Es necesario número de documento");
            return null;
        }
        if (kardexPropuesta.getObservaciones() == null) {
            MensajesErrores.advertencia("Es necesario observaciones");
            return null;
        }
        if (kardexPropuesta.getObservaciones().isEmpty()) {
            MensajesErrores.advertencia("Es necesario número de documento");
            return null;
        }
        if (kardexPropuesta.getNumerorecibo() == null) {
            MensajesErrores.advertencia("Es necesario número de Comprobante de ingreso");
            return null;
        }
        if (kardexPropuesta.getNumerorecibo() == 0) {
            MensajesErrores.advertencia("Es necesario número de Comprobante de ingreso");
            return null;
        }
        if (listaReglonesInversion == null) {
            listaReglonesInversion = new LinkedList<>();
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.modulo=:modulo and o.codigo='79'");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            Cabeceras cas = new Cabeceras();
            Tipoasiento tipo = null;
            List<Tipoasiento> listaTipo;
            List<Renglones> lista = getListaRenglones();

            listaTipo = ejbTipoAsiento.encontarParametros(parametros);

            for (Tipoasiento t : listaTipo) {
                tipo = t;
            }
            if (tipo == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para este módulo");
                return null;
            }
            String vale = ejbCabeceras.validarCierre(kardexPropuesta.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            ejbKardex.create(kardexPropuesta, seguridadBean.getLogueado().getUserid());
            int numero = tipo.getUltimo() + 1;
            Cabeceras cabecera = new Cabeceras();
            cabecera.setFecha(kardexPropuesta.getFechamov());
            cabecera.setUsuario(seguridadBean.getLogueado().getUserid());
            cabecera.setNumero(numero);
            cabecera.setDescripcion(kardexPropuesta.getObservaciones());
            cabecera.setDia(new Date());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setIdmodulo(kardexPropuesta.getId());
            cabecera.setOpcion("FACTURACION_COBRANZA");
            cabecera.setTipo(tipo);
            ejbCabeceras.create(cabecera, seguridadBean.getLogueado().getUserid());
            numero++;
            //            CREACION DE RENGLONES
            for (Renglones r : lista) {
//                if (cabecera != null) {
                r.setFecha(cabecera.getFecha());
                r.setReferencia(cabecera.getDescripcion());
                r.setCabecera(cabecera);
                String c = cuentasBean.traerPartida(r.getCuenta());
                if (c != null) {
                    r.setFuente(fuente);
                    r.setPresupuesto(partida);
                    r.setCentrocosto(proyecto);
                }
//                }
                ejbRenglones.create(r, seguridadBean.getLogueado().getUserid());
            }
            if (!listaReglonesInversion.isEmpty()) {
                cabecera = new Cabeceras();
                cabecera.setFecha(kardexPropuesta.getFechamov());
                cabecera.setUsuario(seguridadBean.getLogueado().getUserid());
                cabecera.setNumero(numero);
                cabecera.setDescripcion(kardexPropuesta.getObservaciones());
                cabecera.setDia(new Date());
                cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabecera.setIdmodulo(kardexPropuesta.getId());
                cabecera.setOpcion("FACTURACION_RECLASIFICACION_COBRANZA");
                cabecera.setTipo(tipo);
                ejbCabeceras.create(cabecera, seguridadBean.getLogueado().getUserid());
                numero++;
                for (Renglones r : listaReglonesInversion) {
                    r.setFecha(cabecera.getFecha());
                    r.setReferencia(cabecera.getDescripcion());
                    r.setCabecera(cabecera);
                    ejbRenglones.create(r, seguridadBean.getLogueado().getUserid());
                }
            }
            tipo.setUltimo(numero);
            ejbTipoAsiento.edit(tipo, seguridadBean.getLogueado().getUserid());
            for (Cabecerafacturas cf : listaFacturas) {
                if (cf.isSeleccionar()) {
                    cf.setKardexbanco(kardexPropuesta);
                    ejbCabecerafacturas.edit(cf, seguridadBean.getLogueado().getUserid());
                }
            }
            grabarEnHoja(kardexPropuesta);
            imprimir(kardexPropuesta);
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro creado correctamente");
        buscar();
        formulario.cancelar();
        return null;
    }

    public String grabarPagoCxc() {
        if (fechaCxc == null) {
            MensajesErrores.advertencia("Es necesario una fecha");
            return null;
        }
        if (cuentaRelacionada == null) {
            MensajesErrores.advertencia("Es necesario Cuenta Relacionada");
            return null;
        }

        if (listaReglonesInversion == null) {
            listaReglonesInversion = new LinkedList<>();
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.modulo=:modulo and o.codigo='79'");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            Cabeceras cas = new Cabeceras();
            Tipoasiento tipo = null;
            List<Tipoasiento> listaTipo;
            List<Renglones> lista = getListaRenglonesCxc();

            listaTipo = ejbTipoAsiento.encontarParametros(parametros);

            for (Tipoasiento t : listaTipo) {
                tipo = t;
            }
            if (tipo == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para este módulo");
                return null;
            }
            String vale = ejbCabeceras.validarCierre(fechaCxc);
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            Integer idModulo = 0;
            for (Cabecerafacturas cf : listaFacturas) {
                idModulo = cf.getId();
            }
            int numero = tipo.getUltimo() + 1;
            Cabeceras cabecera = new Cabeceras();
            cabecera.setFecha(fechaCxc);
            cabecera.setUsuario(seguridadBean.getLogueado().getUserid());
            cabecera.setNumero(numero);
            cabecera.setDescripcion("Cierre de cuentas por cobrar Facturas");
            cabecera.setDia(new Date());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setIdmodulo(idModulo);
            cabecera.setOpcion("FACTURACION_COBRANZA_CXC");
            cabecera.setTipo(tipo);
            ejbCabeceras.create(cabecera, seguridadBean.getLogueado().getUserid());
            numero++;
            //            CREACION DE RENGLONES
            for (Renglones r : lista) {
//                if (cabecera != null) {
                r.setFecha(cabecera.getFecha());
                r.setReferencia(cabecera.getDescripcion());
                r.setCabecera(cabecera);
//                }
                ejbRenglones.create(r, seguridadBean.getLogueado().getUserid());
            }
            if (!listaReglonesInversion.isEmpty()) {
                Cabeceras cabecerainv = new Cabeceras();
                cabecerainv.setFecha(kardexPropuesta.getFechamov());
                cabecerainv.setUsuario(seguridadBean.getLogueado().getUserid());
                cabecerainv.setNumero(numero);
                cabecerainv.setDescripcion(kardexPropuesta.getObservaciones());
                cabecerainv.setDia(new Date());
                cabecerainv.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabecerainv.setIdmodulo(idModulo);
                cabecerainv.setOpcion("FACTURACION_RECLASIFICACION_COBRANZA");
                cabecerainv.setTipo(tipo);
                ejbCabeceras.create(cabecerainv, seguridadBean.getLogueado().getUserid());
                numero++;
                for (Renglones r : listaReglonesInversion) {
                    r.setFecha(cabecera.getFecha());
                    r.setReferencia(cabecera.getDescripcion());
                    r.setCabecera(cabecera);
                    ejbRenglones.create(r, seguridadBean.getLogueado().getUserid());
                }
            }
            tipo.setUltimo(numero);
            ejbTipoAsiento.edit(tipo, seguridadBean.getLogueado().getUserid());
            for (Cabecerafacturas cf : listaFacturas) {
                if (cf.isSeleccionar()) {
                    cf.setCuentacobro(cuentaRelacionada.getParametros());
                    cf.setAsientoCierre(cabecera);
                    ejbCabecerafacturas.edit(cf, seguridadBean.getLogueado().getUserid());
                }
            }
            grabarEnHoja(kardexPropuesta);
//            imprimir(kardexPropuesta);
        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Registro creado correctamente");
        buscar();
        formularioCxc.cancelar();
        return null;
    }

    public String grabarEnHoja(Kardexbanco ing) {
        try {
            ReciboIngresos hoja = new ReciboIngresos();
            hoja.llenarKardex(ing);
            reporteRecibo = hoja.traerRecurso();
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir(Kardexbanco ing) {
        Map parametros = new HashMap();
        kardexPropuesta = ing;
        parametros.put(";where", "o.kardexbanco=:kardexbanco");
        parametros.put("kardexbanco", ing);
        List<Cabecerafacturas> lista = new LinkedList<>();
        try {
            lista = ejbCabecerafacturas.encontarParametros(parametros);
            Cabecerafacturas factura = null;
            if (!lista.isEmpty()) {
                factura = lista.get(0);
            }
            List<Renglones> renglonesLocal = traerRenglonesGasto();
            List<Renglones> renglonesRecla = traerRenglonesReclasificacion();
            formularioImprimir.insertar();
            String empresa = "";
            String email = "";
            if (factura.getCliente().getEmpresa() != null) {
                empresa = factura.getCliente().getEmpresa().toString();
                email = factura.getCliente().getEmpresa().getEmail();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Cabeceras c = new Cabeceras();
            if (renglonesLocal != null) {
                if (!renglonesLocal.isEmpty()) {
                    for (Renglones r : renglonesLocal) {
                        c = r.getCabecera();
                    }
                }
            }
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadBean.getLogueado().getUserid());
//            pdf.agregaTitulo("Beneficiario : " + empresa);
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Beneficiario"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, empresa));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Fecha de emisión"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(factura.getFechacontabilizacion())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Documento"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getTipo().getNombre() + " - " + c.getNumero()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Módulo"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, c.getModulo() + " - " + c.getIdmodulo()));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaParrafo("Descripción : " + factura.getObservaciones() + "\n\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            pdf.agregaParrafo("\n");
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglonesLocal != null) {
                if (!renglonesLocal.isEmpty()) {
                    for (Renglones r : renglonesLocal) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
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
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION N° ");
                }
            }
            pdf.agregaParrafo("\n");
            // reclasificación
            if (renglonesRecla != null) {
                if (!renglonesRecla.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesRecla) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
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
                        c = r.getCabecera();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION RECLASIFICACION N° " + c.getNumero());
                }
            }
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
//            columnas = new LinkedList<>();
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
//            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporte(pdf.traerRecurso());
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerRenglonesGasto() {
        if (kardexPropuesta != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('FACTURACION_COBRANZA')");
            parametros.put("id", kardexPropuesta.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            try {
                List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                return rl;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesReclasificacion() {
        if (kardexPropuesta != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('FACTURACION_RECLASIFICACION_COBRANZA')");
            parametros.put("id", kardexPropuesta.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            try {
                List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                return rl;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
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
     * @return the cuentaCompra
     */
    public String getCuentaCompra() {
        return cuentaCompra;
    }

    /**
     * @param cuentaCompra the cuentaCompra to set
     */
    public void setCuentaCompra(String cuentaCompra) {
        this.cuentaCompra = cuentaCompra;
    }

    /**
     * @return the reporteRecibo
     */
    public Resource getReporteRecibo() {
        return reporteRecibo;
    }

    /**
     * @param reporteRecibo the reporteRecibo to set
     */
    public void setReporteRecibo(Resource reporteRecibo) {
        this.reporteRecibo = reporteRecibo;
    }

    /**
     * @return the formularioCxc
     */
    public Formulario getFormularioCxc() {
        return formularioCxc;
    }

    /**
     * @param formularioCxc the formularioCxc to set
     */
    public void setFormularioCxc(Formulario formularioCxc) {
        this.formularioCxc = formularioCxc;
    }

    /**
     * @return the proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

}
