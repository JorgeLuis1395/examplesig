/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Renglones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.pagos.sfccbdmq.AbonosEmpleadoBean;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.pagos.sfccbdmq.PagoRolEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "pagosVencimientosSfccbdmq")
@ViewScoped
public class PagosVencimientosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Pagosvencimientos> listadoPagosvencimientos;
    private List<Pagosvencimientos> listadoPropuestaPago;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioObservaciones = new Formulario();
    private Formulario formularioCrear = new Formulario();
    private String tipoFecha = "o.inicio";
    private Date desde;
    private Date hasta;
    private Integer factura;
    private Integer ordenPago;
    private int pagado;
    private boolean propuestas;
    private String observacionPropuesta;
    private String observacionAnt;
    private Resource reporte;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private Pagosvencimientos pagoVencimiento;
    private double totalPagosvencimientos;

    @EJB
    private PagosvencimientosFacade ejbPagosvencimientos;
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private DocumentoselectronicosFacade ejbDoc;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private AnticiposFacade ejbAntcipos;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

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

    /* @return the parametrosSeguridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "PagosVencimientosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of ContratosEmpleadoBean
     */
    public PagosVencimientosBean() {

        listadoPagosvencimientos = new LazyDataModel<Pagosvencimientos>() {

            @Override
            public List<Pagosvencimientos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }

        };
    }

    public String buscar() {

        totalPagosvencimientos = 0;

        listadoPagosvencimientos = new LazyDataModel<Pagosvencimientos>() {
            @Override
            public List<Pagosvencimientos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {

                try {
                    Map parametros = new HashMap();
                    String where = "o.obligacion.fechacontable between :desde and :hasta ";
                    if (!((factura == null) || (factura > 0))) {
                        where += " and o.obligacion.documento=:documento";
                        parametros.put("documento", factura);
                    }
                    if (proveedorBean.getProveedor() != null) {
                        where += " and o.obligacion.proveedor=:proveedor";
                        parametros.put("proveedor", proveedorBean.getProveedor());

                    }
                    if (pagado == 0) {
                        where += " and o.kardexbanco is null";

                    } else if (pagado == 1) {
                        where += " and o.kardexbanco is not null";
                    }
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.obligacion.fechacontable desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    totalPagosvencimientos = ejbPagosvencimientos.sumarCampo(parametros).doubleValue();
                    parametros = new HashMap();
                    where = "o.obligacion.fechacontable between :desde and :hasta ";
                    if (!((factura == null) || (factura > 0))) {
                        where += " and o.obligacion.documento=:documento";
                        parametros.put("documento", factura);
                    }
                    if (proveedorBean.getProveedor() != null) {
                        where += " and o.obligacion.proveedor=:proveedor";
                        parametros.put("proveedor", proveedorBean.getProveedor());

                    }
                    if (pagado == 0) {
                        where += " and o.kardexbanco is null";

                    } else if (pagado == 1) {
                        where += " and o.kardexbanco is not null";
                    }
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.obligacion.fechacontable desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    int total = ejbPagosvencimientos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoPagosvencimientos.setRowCount(total);
                    return ejbPagosvencimientos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }

        };

        return null;
    }

    public String buscarPropuesta() {
        boolean otra = false;
        try {
            if (ordenPago != null) {
                if (ordenPago > 0) {
//                Documentoselectronicos doc;
//                doc.getCabeccera().getId()
                    String where = "o.cabeccera.id=:cabecera and o.obligacion.fechacontable "
                            + "is not null  and o.cabeccera.compromiso.proveedor is not null";
                    Map parametros = new HashMap();
                    parametros.put("cabecera", ordenPago);
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.cabeccera.compromiso.proveedor.id");
                    List<Documentoselectronicos> listado = ejbDoc.encontarParametros(parametros);
                    listadoPropuestaPago = new LinkedList<>();
                    for (Documentoselectronicos d : listado) {
                        parametros = new HashMap();
                        where = "o.obligacion=:obligacion and o.kardexbanco is null and o.valor>0";
                        parametros.put("obligacion", d.getObligacion());
                        parametros.put(";where", where);

                        List<Pagosvencimientos> lpagosList = ejbPagosvencimientos.encontarParametros(parametros);
                        for (Pagosvencimientos p : lpagosList) {
//                            if (p.getKardexbanco() == null) {
                            listadoPropuestaPago.add(p);
//                            }
                        }
                    }
//                    ********************************************** 
//                            para los beneficiarios
//                    *************************************************                
                    where = "o.cabeccera.id=:cabecera and o.obligacion.fechacontable is not null and o.cabeccera.compromiso.beneficiario is not null";
                    parametros = new HashMap();
                    parametros.put("cabecera", ordenPago);
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.cabeccera.compromiso.beneficiario.id");
                    listado = ejbDoc.encontarParametros(parametros);
//                    listadoPropuestaPago = new LinkedList<>();
                    for (Documentoselectronicos d : listado) {
                        parametros = new HashMap();
                        where = "o.obligacion=:obligacion and o.kardexbanco is null and o.valor>0";
                        parametros.put("obligacion", d.getObligacion());
                        parametros.put(";where", where);

                        List<Pagosvencimientos> lpagosList = ejbPagosvencimientos.encontarParametros(parametros);
                        for (Pagosvencimientos p : lpagosList) {
                            listadoPropuestaPago.add(p);
                        }
                    }
//                    **********************************************
//                    fin beneficiarios
//                    **************************************************
                } else {
                    otra = true;
                }
            } else {
                otra = true;
            }
            if (otra) {
                Map parametros = new HashMap();
                String where = "o.obligacion.fechacontable between :desde and :hasta ";

                if (!((factura == null) || (factura <= 0))) {
                    where += " and o.obligacion.documento=:documento";
                    parametros.put("documento", factura);
                }

                if (proveedorBean.getProveedor() != null) {
                    where += " and o.obligacion.proveedor=:proveedor";
                    parametros.put("proveedor", proveedorBean.getProveedor());

                }
                where += " and o.kardexbanco is null and o.valor>0";
                parametros.put(";where", where);

                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                parametros.put(";orden", "o.obligacion.proveedor.id");
                listadoPropuestaPago = ejbPagosvencimientos.encontarParametros(parametros);
                //                    ********************************************** 
//                            para los beneficiarios
//                    *************************************************                
                where = " o.obligacion.fechacontable between :desde and :hasta "
                        + " and o.cabeccera.compromiso.beneficiario is not null ";
                parametros = new HashMap();
//                parametros.put("cabecera", ordenPago);
                parametros.put(";where", where);
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
//                parametros.put(";orden", "o.cabeccera.compromiso.beneficiario.id");
                List<Documentoselectronicos> listado = ejbDoc.encontarParametros(parametros);
//                    listadoPropuestaPago = new LinkedList<>();
                for (Documentoselectronicos d : listado) {
                    parametros = new HashMap();
                    where = "o.obligacion=:obligacion and o.kardexbanco is null and o.valor>0";
                    parametros.put("obligacion", d.getObligacion());
                    parametros.put(";where", where);

                    List<Pagosvencimientos> lpagosList = ejbPagosvencimientos.encontarParametros(parametros);
                    for (Pagosvencimientos p : lpagosList) {
                        listadoPropuestaPago.add(p);
                    }
                }
//                    **********************************************
//                    fin beneficiarios
//                    **************************************************

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String seleccionarTodos() {
        for (Pagosvencimientos p : listadoPropuestaPago) {
            p.setSeleccionar(true);
        }
        return null;
    }

    public String proponerSeleccionados() {
        boolean si = false;
        for (Pagosvencimientos p : listadoPropuestaPago) {
            if (p.isSeleccionar()) {
                si = true;
                Proveedores pro = p.getObligacion().getProveedor();
                if (pro == null) {
                    Empleados em = p.getObligacion().getCompromiso().getBeneficiario();
                    if (em == null) {
                        MensajesErrores.advertencia("No existe información de a quien pagar ");
                        return null;
                    }
                    Codigos codigoBanco = traerBanco(em);
                    if (codigoBanco == null) {
                        MensajesErrores.advertencia("No existe banco para empleado: " + em.getEntidad().toString());
                        return null;
                    }
                    String numeroCuenta = traer(em, "NUMCUENTA");
                    if ((numeroCuenta == null) || (numeroCuenta.isEmpty())) {
                        MensajesErrores.advertencia("No existe número de cuenta para empleado: " + em.getEntidad().toString());
                        return null;
                    }
                    String tipoCuenta = traerTipoCuenta(em);
                    if ((tipoCuenta == null) || (tipoCuenta.isEmpty())) {
                        MensajesErrores.advertencia("No existe tipo de cuenta para empleado: " + em.getEntidad().toString());
                        return null;
                    }
                } else {
                    Codigos codigoBanco = pro.getBanco();
                    if (codigoBanco == null) {
                        MensajesErrores.advertencia("No existe banco para proveedor : " + pro.getEmpresa().toString());
                        return null;
                    }

                    if (pro.getCtabancaria() == null) {
                        MensajesErrores.advertencia("No existe No cuenta para proveedor : " + pro.getEmpresa().toString());
                        return null;
                    }

                    String tipoCta = pro.getTipocta().getParametros();
                    if (tipoCta == null) {
                        MensajesErrores.advertencia("No existe Tipo de cuenta para proveedor : " + pro.getEmpresa().toString());
                        return null;
                    }
                }
            }
        }
        if (!si) {
            MensajesErrores.advertencia("No existe nada seleccionado");
            return null;
        }

        formulario.insertar();
        return null;
    }

    public String aprueba() {
//        this.prestamo = prestamo;
        // ver cuanto debe
        try {
            if ((observacionPropuesta == null) || (observacionPropuesta.isEmpty())) {
                MensajesErrores.advertencia("Favor coloque una observación");
                return null;
            }

            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            Proveedores proveedor = null;
            Empleados empleado = null;
            List<Pagosvencimientos> pagadosKardex = new LinkedList<>();
            double valorKardex = 0;
            int ultimo = 0;
            for (Pagosvencimientos p : listadoPropuestaPago) {
                if (p.isSeleccionar()) {
                    Proveedores pro = p.getObligacion().getProveedor();
                    Obligaciones obli = p.getObligacion();
                    Compromisos com = obli.getCompromiso();
                    Empleados em = com == null ? null
                            : com.getBeneficiario();
                    if (pro == null) {
                        ultimo = 1;
                        if (em != null) { ///IF DE EMPLEADO
                            if ((empleado == null)) {
                                if (proveedor != null) {
                                    Kardexbanco k = new Kardexbanco();
                                    Codigos codigoBanco = proveedor.getBanco();
                                    k.setBancotransferencia(codigoBanco);
                                    k.setCuentatrans(proveedor.getCtabancaria());
                                    k.setObservaciones(null);
                                    k.setBanco(null);
                                    k.setFechamov(null);
                                    k.setTipomov(null);
                                    k.setFechaabono(null);
                                    k.setFechagraba(null);
                                    k.setOrigen("PAGO PROVEEDORES");
                                    k.setCodigospi(null);
                                    k.setValor(new BigDecimal(valorKardex));
                                    k.setPropuesta(observacionPropuesta);
                                    k.setProveedor(proveedor);
                                    k.setUsuariograba(parametrosSeguridad.getLogueado());
                                    String tipoCta = proveedor.getTipocta().getParametros();
                                    k.setTcuentatrans(Integer.parseInt(tipoCta));
                                    k.setEstado(0);
                                    k.setBeneficiario(proveedor.getNombrebeneficiario());
                                    k.setDocumento(proveedor.getEmpresa().getRuc());
                                    ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                                    for (Pagosvencimientos pv : pagadosKardex) {
                                        pv.setKardexbanco(k);
                                        pv.setPagado(Boolean.TRUE);
                                        k.setEgreso(pv.getId());
                                        ejbPagosvencimientos.edit(pv, parametrosSeguridad.getLogueado().getUserid());
                                    }
                                    pagadosKardex = new LinkedList<>();
                                    proveedor = null;
                                }
                                empleado = em;
                                double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                                double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                                valorKardex = p.getValor().doubleValue();
//                                - (vanticipo + vmulta);
                                pagadosKardex.add(p);
                            } else {
                                if ((empleado.equals(em))) {
                                    double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                                    double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                                    valorKardex += p.getValor().doubleValue();
//                                    - (vanticipo + vmulta);
                                    pagadosKardex.add(p);
                                } else {
                                    Codigos codigoBanco = traerBanco(empleado);
                                    String numeroCuenta = traer(empleado, "NUMCUENTA");
                                    String tipoCuenta = traerTipoCuenta(empleado);
                                    Kardexbanco k = new Kardexbanco();
                                    k.setBancotransferencia(codigoBanco);
                                    k.setCuentatrans(numeroCuenta);
                                    k.setObservaciones(null);
                                    k.setBanco(null);
                                    k.setFechamov(null);
                                    k.setTipomov(null);
                                    k.setFechaabono(null);
                                    k.setFechagraba(null);
                                    k.setOrigen("PAGO PROVEEDORES");
                                    k.setCodigospi(null);
                                    k.setValor(new BigDecimal(valorKardex));
                                    k.setPropuesta(observacionPropuesta);
                                    k.setProveedor(null);
                                    k.setUsuariograba(parametrosSeguridad.getLogueado());
                                    k.setTcuentatrans(Integer.parseInt(tipoCuenta));
                                    k.setEstado(0);
                                    k.setBeneficiario(empleado.getEntidad().toString());
                                    k.setDocumento(empleado.getEntidad().getPin());
                                    ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                                    for (Pagosvencimientos pv : pagadosKardex) {
                                        pv.setKardexbanco(k);
                                        pv.setPagado(Boolean.TRUE);
                                        k.setEgreso(pv.getId());
                                        ejbPagosvencimientos.edit(pv, parametrosSeguridad.getLogueado().getUserid());
                                    }
                                    empleado = em;
                                    double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                                    double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                                    valorKardex = p.getValor().doubleValue();
//                                    - (vanticipo + vmulta);
                                    pagadosKardex = new LinkedList<>();
                                    pagadosKardex.add(p);
                                }

                            }
                        } ///////////FIN DE IF EMPLEADO
                    } else {
                        ultimo = 2;
                        if ((proveedor == null)) {
                            proveedor = pro;
                            double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                            double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                            valorKardex += p.getValor().doubleValue();
//                                - (vanticipo + vmulta);
                            pagadosKardex.add(p);
                        } else {
                            if ((proveedor.equals(pro))) {
                                double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                                double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                                valorKardex += p.getValor().doubleValue();
//                                    - (vanticipo + vmulta);
                                pagadosKardex.add(p);
                            } else {
                                Kardexbanco k = new Kardexbanco();
                                Codigos codigoBanco = proveedor.getBanco();
                                k.setBancotransferencia(codigoBanco);
                                k.setCuentatrans(proveedor.getCtabancaria());
                                k.setObservaciones(null);
                                k.setBanco(null);
                                k.setFechamov(null);
                                k.setTipomov(null);
                                k.setFechaabono(null);
                                k.setFechagraba(null);
                                k.setOrigen("PAGO PROVEEDORES");
                                k.setCodigospi(null);
                                k.setValor(new BigDecimal(valorKardex));
                                k.setPropuesta(observacionPropuesta);
                                k.setProveedor(proveedor);
                                k.setUsuariograba(parametrosSeguridad.getLogueado());
                                String tipoCta = proveedor.getTipocta().getParametros();
                                k.setTcuentatrans(Integer.parseInt(tipoCta));
                                k.setEstado(0);
                                k.setBeneficiario(proveedor.getNombrebeneficiario());
                                k.setDocumento(proveedor.getEmpresa().getRuc());
                                ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                                for (Pagosvencimientos pv : pagadosKardex) {
                                    pv.setKardexbanco(k);
                                    pv.setPagado(Boolean.TRUE);
                                    k.setEgreso(pv.getId());
                                    ejbPagosvencimientos.edit(pv, parametrosSeguridad.getLogueado().getUserid());
                                }
                                proveedor = pro;
                                double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                                double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                                valorKardex = p.getValor().doubleValue();
//                                    - (vanticipo + vmulta);
                                pagadosKardex = new LinkedList<>();
                                pagadosKardex.add(p);
                            }

                        }
                    }
                }
            }
            if (ultimo == 2) {
                if (pagadosKardex.size() > 0) {
                    Kardexbanco k = new Kardexbanco();
                    Codigos codigoBanco = proveedor.getBanco();
                    k.setBancotransferencia(codigoBanco);
                    k.setCuentatrans(proveedor.getCtabancaria());
                    k.setObservaciones(null);
                    k.setBanco(null);
                    k.setFechamov(null);
                    k.setTipomov(null);
                    k.setFechaabono(null);
                    k.setFechagraba(null);
                    k.setOrigen("PAGO PROVEEDORES");
                    k.setCodigospi(null);
                    k.setValor(new BigDecimal(valorKardex));
                    k.setPropuesta(observacionPropuesta);
                    k.setProveedor(proveedor);
                    k.setUsuariograba(parametrosSeguridad.getLogueado());
                    String tipoCta = proveedor.getTipocta().getParametros();
                    k.setTcuentatrans(Integer.parseInt(tipoCta));
                    k.setEstado(0);
                    k.setBeneficiario(proveedor.getNombrebeneficiario());
                    ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                    for (Pagosvencimientos pv : pagadosKardex) {
                        pv.setKardexbanco(k);
                        pv.setPagado(Boolean.TRUE);
                        k.setEgreso(pv.getId());
                        ejbPagosvencimientos.edit(pv, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            } else if (ultimo == 1) {
                if (empleado != null) {
                    Codigos codigoBanco = traerBanco(empleado);
                    String numeroCuenta = traer(empleado, "NUMCUENTA");
                    String tipoCuenta = traerTipoCuenta(empleado);
                    Kardexbanco k = new Kardexbanco();
                    k.setBancotransferencia(codigoBanco);
                    k.setCuentatrans(numeroCuenta);
                    k.setObservaciones(null);
                    k.setBanco(null);
                    k.setFechamov(null);
                    k.setTipomov(null);
                    k.setFechaabono(null);
                    k.setFechagraba(null);
                    k.setOrigen("PAGO PROVEEDORES");
                    k.setCodigospi(null);
                    k.setValor(new BigDecimal(valorKardex));
                    k.setPropuesta(observacionPropuesta);
                    k.setProveedor(null);
                    k.setUsuariograba(parametrosSeguridad.getLogueado());
                    k.setTcuentatrans(Integer.parseInt(tipoCuenta));
                    k.setEstado(0);
                    k.setBeneficiario(empleado.getEntidad().toString());
                    k.setDocumento(empleado.getEntidad().getPin());
                    ejbKardex.create(k, parametrosSeguridad.getLogueado().getUserid());
                    for (Pagosvencimientos pv : pagadosKardex) {
                        pv.setKardexbanco(k);
                        pv.setPagado(Boolean.TRUE);
                        k.setEgreso(pv.getId());
                        ejbPagosvencimientos.edit(pv, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            }
//            hacer un reporte
            imprimir();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AbonosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Se aprobó corecctamete");
        formulario.cancelar();
        buscarPropuesta();
        observacionPropuesta = null;
//        buscar();
        return null;
    }

    public String quitarTodos() {
        for (Pagosvencimientos p : listadoPropuestaPago) {
            p.setSeleccionar(false);
        }
        return null;
    }

    public double getTotalSeleccionado() {
        double valor = 0;
        if (listadoPropuestaPago == null) {
            return 0;
        }
        for (Pagosvencimientos p : listadoPropuestaPago) {
            if (p.isSeleccionar()) {
                double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                valor += p.getValor().doubleValue();
//                valor += p.getValor().doubleValue() - (vanticipo + vmulta);
            }
        }
        return valor;
    }

    public String botonImprimir(AuxiliarReporte aux) {
        if (aux.getDato() == null) {
            MensajesErrores.advertencia("No existe nada que imprimir");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fechamov desc,o.id");
        String where = "  ";
        where += "  upper(o.propuesta) = :propuetas";
        parametros.put("propuetas", aux.getDato().toUpperCase());
        try {
            parametros.put(";where", where);
            observacionPropuesta = aux.getDato().toUpperCase();
            List<Kardexbanco> lista = ejbKardex.encontarParametros(parametros);
            listadoPropuestaPago = new LinkedList<>();
            for (Kardexbanco k : lista) {
                Pagosvencimientos p = new Pagosvencimientos();
                parametros = new HashMap();
                parametros.put(";where", "o.kardexbanco=:kardex");
                parametros.put("kardex", k);
                List<Pagosvencimientos> listaPagos = ejbPagosvencimientos.encontarParametros(parametros);
                for (Pagosvencimientos p1 : listaPagos) {
                    p1.setSeleccionar(true);
                    listadoPropuestaPago.add(p1);
                }
                if (k.getAnticipo() != null) {
                    p.setValor(k.getAnticipo().getValor());
                    p.setKardexbanco(k);
                    listadoPropuestaPago.add(p);
                }

            }
            imprimir();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        try {

            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + observacionPropuesta, null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "C. Anticipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double valorTotal = 0;
            for (Pagosvencimientos p : listadoPropuestaPago) {
                if (p.isSeleccionar()) {
                    columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getFechaemision()));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getConcepto()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getContrato() == null ? "" : p.getObligacion().getContrato().getNumero()));
                    Obligaciones obli = p.getObligacion();
                    Compromisos comp = obli.getCompromiso();
                    if (obli.getProveedor() != null) {
                        ////////////////////////////DATOS DEL PROVEEDOR//////////////////////////////////////
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getProveedor().getNombrebeneficiario()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getAnticipo() != null ? p.getAnticipo().getCuenta() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getProveedor().getTipocta().toString()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getProveedor().getCtabancaria()));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getProveedor().getBanco().toString()));
                        ///////////////////////////////////////////////////////////////////////////////////////////////////////
                    } else if (comp != null) {
                        /////////////////////////////////EMPLEADO
                        Empleados emp = comp.getBeneficiario();
                        if (emp != null) {
//                            Empleados empleado = p.getCompromiso().getEmpleado();
                            Codigos codigoBanco = traerBanco(emp);
                            String numeroCuenta = traer(emp, "NUMCUENTA");
                            String tipoCuenta = traerTipoCuenta(emp);
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, emp.toString()));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigoBanco.toString()));
                        } else {
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        }
                        /////////////////////FIN EMPLEADO
                    } else {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false,
                            p.getObligacion().getEstablecimiento() + "-"
                            + p.getObligacion().getPuntoemision() + "-"
                            + p.getObligacion().getDocumento().toString()));
                    double vanticipo = p.getValoranticipo() == null ? 0 : p.getValoranticipo().doubleValue();
                    double vmulta = p.getValormulta() == null ? 0 : p.getValormulta().doubleValue();
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                            p.getValor().doubleValue()));
//                            p.getValor().doubleValue() - (vanticipo + vmulta)));
                    valorTotal += p.getValor().doubleValue();
//                    valorTotal += p.getValor().doubleValue() - (vanticipo + vmulta);
                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
            pdf.agregarTablaReporte(titulos, columnas, 10, 100, null);
            pdf.agregaParrafo("\n\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporte(pdf.traerRecurso());
            setNombreArchivo("Propuesta.pdf");
            setTipoArchivo("Exportar a PDF");
            setTipoMime("application/pdf");
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

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
     * @return the tipoFecha
     */
    public String getTipoFecha() {
        return tipoFecha;
    }

    /**
     * @param tipoFecha the tipoFecha to set
     */
    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
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
     * @return the listadoPagosvencimientos
     */
    public LazyDataModel<Pagosvencimientos> getListadoPagosvencimientos() {
        return listadoPagosvencimientos;
    }

    /**
     * @param listadoPagosvencimientos the listadoPagosvencimientos to set
     */
    public void setListadoPagosvencimientos(LazyDataModel<Pagosvencimientos> listadoPagosvencimientos) {
        this.listadoPagosvencimientos = listadoPagosvencimientos;
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
     * @return the totalPagosvencimientos
     */
    public double getTotalPagosvencimientos() {
        return totalPagosvencimientos;
    }

    /**
     * @param totalPagosvencimientos the totalPagosvencimientos to set
     */
    public void setTotalPagosvencimientos(double totalPagosvencimientos) {
        this.totalPagosvencimientos = totalPagosvencimientos;
    }

    public double getValorObligacion() {
        Pagosvencimientos o = (Pagosvencimientos) listadoPagosvencimientos.getRowData();
        String where = "o.obligacion=:obligacion";
        Map parametros = new HashMap();
        parametros.put(";where", where);
        parametros.put("obligacion", o.getObligacion());
        parametros.put(";campo", "o.valor+o.valorimpuesto");
        try {
            return ejbRasCompras.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            Logger.getLogger(PagosVencimientosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the pagado
     */
    public int getPagado() {
        return pagado;
    }

    /**
     * @param pagado the pagado to set
     */
    public void setPagado(int pagado) {
        this.pagado = pagado;
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

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    /**
     * @return the listadoPropuestaPago
     */
    public List<Pagosvencimientos> getListadoPropuestaPago() {
        return listadoPropuestaPago;
    }

    /**
     * @param listadoPropuestaPago the listadoPropuestaPago to set
     */
    public void setListadoPropuestaPago(List<Pagosvencimientos> listadoPropuestaPago) {
        this.listadoPropuestaPago = listadoPropuestaPago;
    }

    /**
     * @return the observacionPropuesta
     */
    public String getObservacionPropuesta() {
        return observacionPropuesta;
    }

    /**
     * @param observacionPropuesta the observacionPropuesta to set
     */
    public void setObservacionPropuesta(String observacionPropuesta) {
        this.observacionPropuesta = observacionPropuesta;
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
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
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
     * @return the propuestas
     */
    public boolean isPropuestas() {
        return propuestas;
    }

    /**
     * @param propuestas the propuestas to set
     */
    public void setPropuestas(boolean propuestas) {
        this.propuestas = propuestas;
    }

    public List<AuxiliarReporte> getPropuestasAnteriores() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.propuesta is not null");
//            parametros.put(";where", "o.banco is not null and o.propuesta is not null");
            parametros.put(";campo", "o.propuesta");
            parametros.put(";suma", "sum(o.valor)");
            List<Object[]> lista = ejbKardex.sumar(parametros);
            int size = lista.size();
//            int size = lista.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
//            items[0] = new SelectItem(null, "---");
//            i++;
            DecimalFormat df = new DecimalFormat("###,##0.00");
            List<AuxiliarReporte> retorno = new LinkedList<>();
            for (Object[] o : lista) {
                String nombre = (String) o[0];
                BigDecimal valor = (BigDecimal) o[1];
                AuxiliarReporte a = new AuxiliarReporte();
                a.setDato(nombre);
                a.setValor(valor);
//                retorno.add( nombre + "  (" + df.format(valor.doubleValue()) + ")");
                retorno.add(a);

            }
            return retorno;
        } catch (ConsultarException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String editar(AuxiliarReporte aux) {
        if (aux.getDato() == null) {
            MensajesErrores.advertencia("No existe nada que borrar");
            return null;
        }
        observacionPropuesta = aux.getDato().toUpperCase();
        observacionAnt = aux.getDato().toUpperCase();
        getFormularioObservaciones().insertar();
        return null;
    }

    public String cambiar() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.fechamov desc,o.id");
        String where = "  ";
        where += "  upper(o.propuesta) = :propuetas";
        parametros.put("propuetas", observacionAnt);
        try {
            parametros.put(";where", where);
            List<Kardexbanco> lista = ejbKardex.encontarParametros(parametros);
            listadoPropuestaPago = new LinkedList<>();

            for (Kardexbanco k : lista) {
                k.setPropuesta(observacionPropuesta);
                ejbKardex.edit(k, parametrosSeguridad.getLogueado().getUserid());

            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formularioObservaciones.cancelar();
        MensajesErrores.advertencia("Propuesta se cambio correctamente");
        return null;
    }

    public String borrar(AuxiliarReporte aux) {
        if (aux.getDato() == null) {
            MensajesErrores.advertencia("No existe nada que borrar");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fechamov desc,o.id");
        String where = "  ";
        where += "  upper(o.propuesta) = :propuetas";
        parametros.put("propuetas", aux.getDato().toUpperCase());
        try {
            parametros.put(";where", where);
            observacionPropuesta = aux.getDato().toUpperCase();
            List<Kardexbanco> lista = ejbKardex.encontarParametros(parametros);
            listadoPropuestaPago = new LinkedList<>();

            for (Kardexbanco k : lista) {
                if (k.getSpi() != null) {
                    MensajesErrores.advertencia("No se puede borrar existe SPI");
                    return null;
                }
                if (k.getBanco() != null) {
                    MensajesErrores.advertencia("No se puede borrar existe Egreso");
                    return null;
                }

            }
            for (Kardexbanco k : lista) {
                parametros = new HashMap();
                where = "  ";
                where += "o.kardexbanco = :kardex";
                parametros.put("kardex", k);
                parametros.put(";where", where);
                List<Pagosvencimientos> listaP = ejbPagosvencimientos.encontarParametros(parametros);
                for (Pagosvencimientos p : listaP) {
                    p.setPagado(false);
                    p.setKardexbanco(null);
                    ejbPagosvencimientos.edit(p, parametrosSeguridad.getLogueado().getUserid());
                }

                k.setPropuesta(null);
                if (k.getAnticipo() != null) {
                    Anticipos a = k.getAnticipo();
                    a.setEstado(0);
                    ejbAntcipos.edit(a, parametrosSeguridad.getLogueado().getUserid());
                }
                ejbKardex.remove(k, parametrosSeguridad.getLogueado().getUserid());

            }
        } catch (ConsultarException | BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        MensajesErrores.advertencia("Propuesta se borro correctamente");
        return null;
    }

    /**
     * @return the ordenPago
     */
    public Integer getOrdenPago() {
        return ordenPago;
    }

    /**
     * @param ordenPago the ordenPago to set
     */
    public void setOrdenPago(Integer ordenPago) {
        this.ordenPago = ordenPago;
    }

    /**
     * @return the formularioObservaciones
     */
    public Formulario getFormularioObservaciones() {
        return formularioObservaciones;
    }

    /**
     * @param formularioObservaciones the formularioObservaciones to set
     */
    public void setFormularioObservaciones(Formulario formularioObservaciones) {
        this.formularioObservaciones = formularioObservaciones;
    }
//    ******************************************************
//    información del empleado del banco

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
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
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
//    **********************************************************

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
     * @return the formularioCrear
     */
    public Formulario getFormularioCrear() {
        return formularioCrear;
    }

    /**
     * @param formularioCrear the formularioCrear to set
     */
    public void setFormularioCrear(Formulario formularioCrear) {
        this.formularioCrear = formularioCrear;
    }

    // metodos para crear el pago debe ser con cualquier auxiliar? o solo proveedores
    public String nuevoPago() {
        if (proveedorBean.getProveedor() != null) {
            MensajesErrores.advertencia("Seleccione el proveedor");
            return null;

        }
        pagoVencimiento = new Pagosvencimientos();
        formularioCrear.insertar();
        return null;
    }

    public String insertarPago() {
//        pagoVencimiento.get
//        formularioCrear.cancelar();
        return null;
    }

    /**
     * @return the pagoVencimiento
     */
    public Pagosvencimientos getPagoVencimiento() {
        return pagoVencimiento;
    }

    /**
     * @param pagoVencimiento the pagoVencimiento to set
     */
    public void setPagoVencimiento(Pagosvencimientos pagoVencimiento) {
        this.pagoVencimiento = pagoVencimiento;
    }
}
