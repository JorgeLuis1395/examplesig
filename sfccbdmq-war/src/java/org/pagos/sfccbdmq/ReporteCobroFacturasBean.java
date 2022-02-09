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
@ManagedBean(name = "reporteCobroFacturasSfcbdmq")
@ViewScoped
public class ReporteCobroFacturasBean implements Serializable {

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
    private Cabecerafacturas factura;
    private Perfiles perfil;
    private Detallefacturas detalle;
    private Codigos tipoDocumento;
    private Puntoemision puntoEmision;
    private Kardexbanco kardexPropuesta;
    private List<Renglones> renglones;
    private List<Renglones> rasReclasificaion;
    private Resource reporte;
    private String cuentaCompra;
    private Date fechaCxc;
    private Resource reporteRecibo;
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

    public ReporteCobroFacturasBean() {
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
        String where = "o.fechacontabilizacion is not null and o.kardexbanco is not null";
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
            Logger.getLogger(ReporteCobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            Logger.getLogger(ReporteCobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReporteCobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReporteCobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
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

    public String imprimir(Cabecerafacturas cab) {
        try {
            Map parametros = new HashMap();
            kardexPropuesta = cab.getKardexbanco();
            if (cab.getKardexbanco().getBeneficiario() == null) {
                kardexPropuesta.setBeneficiario(cab.getCliente().getEmpresa().toString());
                ejbKardex.edit(kardexPropuesta, seguridadBean.getLogueado().getUserid());
            }
            parametros.put(";where", "o.kardexbanco=:kardexbanco");
            parametros.put("kardexbanco", kardexPropuesta);
            List<Cabecerafacturas> lista = new LinkedList<>();
            lista = ejbCabecerafacturas.encontarParametros(parametros);
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
            grabarEnHoja(kardexPropuesta);
        } catch (ConsultarException | IOException | DocumentException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteCobroFacturasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the factura
     */
    public Cabecerafacturas getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Cabecerafacturas factura) {
        this.factura = factura;
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
     * @return the detalle
     */
    public Detallefacturas getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallefacturas detalle) {
        this.detalle = detalle;
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

}
