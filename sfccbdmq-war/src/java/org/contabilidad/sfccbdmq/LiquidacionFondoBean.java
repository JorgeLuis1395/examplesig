/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.LiquidaciondeCompras;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DepositosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallesfondoFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ReposiscionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.SucursalesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Depositos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallesfondo;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Reposisciones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Sucursales;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Valesfondos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.KardexBean;
import org.personal.sfccbdmq.ValesCajaBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "liquidacionFondosSfccbdmq")
@ViewScoped
public class LiquidacionFondoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoFondoBean
     */
    public LiquidacionFondoBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioAuto = new Formulario();
    private Fondos fondo;
    private List<Fondos> listaFondos;
    private Empleados empleado;
    private Organigrama direccion;
    private Resource reporte;
    private List<Renglones> listaReglones;
    private List<Renglones> listaReglonesGasto;
    private List<Renglones> listaReglonesInversion;
    private List<Detallecompromiso> listaDetallesC;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private List<AuxiliarCarga> renglonesRetencion;
    private Cabeceras cabecera;
    private Kardexbanco kardex;
    private Kardexbanco kardexDeposito;
    private Kardexbanco kardexDepositoDesconocido;
    private double reponer;
    private double valorAnterior;
    private Cuentas gastoFondo;
    private Cuentas gastoReclasificacion;
    private boolean parcial;
    private Autorizaciones autorizacion2;
    private Autorizaciones autorizacionProveedor;
    private Reposisciones reposicion;
    private Impuestos impuesto;
    private Puntoemision puntoemision;
    private Sucursales sucursal;
    private Resource reporteCompromiso;
    private Resource reporteComp;
    private Date fechaImp;

    private Renglones RenglonDescuento;
    private boolean masDepositos = false;
    private boolean masDepositosDesconocidos = false;
    private boolean masDescuentos = false;
    private List<Renglones> listadoRol;
    private List<Kardexbanco> listadoPropio;
    private List<Kardexbanco> listadoPropioDesconocidos;
    @EJB
    private FondosFacade ejbFondos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private ValesfondosFacade ejbVales;
    @EJB
    private DetallesfondoFacade ejbDetallesvale;
    @EJB
    private RetencionescomprasFacade ejbReten;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private PuntoemisionFacade ejbPuntoemision;
    @EJB
    private SucursalesFacade ejbSucursales;
    @EJB
    private ReposiscionesFacade ejbReposisciones;
    @EJB
    private EmpresasFacade ejbEmpresas;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private DepositosFacade ejbDepositos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private CajasFacade ejbCajas;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "LiquidacionFondosVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }

    }
    // fin perfiles

    public String modificar() {
        if (fondo == null) {
            MensajesErrores.advertencia("Seleccione una fondo");
            return null;
        }
        reponer = 0;
        valorAnterior = 0;
        kardex = new Kardexbanco();
        kardex.setFechamov(new Date());
//        kardex.setValor(fondo.getValor());
        try {
            //Facturas anteriores cuando es liquidado parcial
            Map parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo and o.estado=6");
            parametros.put("fondo", fondo);
            parametros.put(";campos", "o.baseimponiblecero + o.baseimponible + o.iva");
            valorAnterior = ejbVales.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo and o.estado=4");
            parametros.put("fondo", fondo);
            parametros.put(";campos", "o.baseimponiblecero + o.baseimponible + o.iva");
            reponer = ejbVales.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo");
            parametros.put("fondo", fondo);
            List<Valesfondos> lista = ejbVales.encontarParametros(parametros);
            double vlorRet = 0;
            for (Valesfondos v : lista) {
                parametros = new HashMap();
                parametros.put(";campos", "o.valor + o.valoriva");
                parametros.put(";where", "o.obligacion=:fondo");
                parametros.put("fondo", v.getObligacion());
                double valorRet = ejbReten.sumarCampo(parametros).doubleValue();
                vlorRet += valorRet;
            }
            // sumar retenciones

            reponer -= vlorRet;
        } catch (ConsultarException ex) {
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        double totalKardex = fondo.getValor().doubleValue() - reponer;
        kardex.setValor(new BigDecimal(totalKardex));
        listaReglones = null;
        listaReglonesGasto = null;
        reposicion = new Reposisciones();
        reposicion.setBase(BigDecimal.ZERO);
        reposicion.setBase0(BigDecimal.ZERO);
        reposicion.setIva(BigDecimal.ZERO);
        reposicion.setNumerodocumento(0);
        reposicion.setAjuste(0);
        reposicion.setFecha(new Date());
        formulario.editar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return null;
    }

    public String traer53() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anio = c.get(Calendar.YEAR);
        try {
            Codigos fondoChica = ejbCodigos.traerCodigo("GASTGEN", "F");
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.asignacion.proyecto.anio=:anio");
            parametros.put("codigo", fondoChica.getParametros() + "%");
            parametros.put("anio", anio);
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            List<Detallecertificaciones> aux = ejbDetCert.encontarParametros(parametros);
            for (Detallecertificaciones d : aux) {
                return d.getAsignacion().getClasificador().getCodigo().substring(0, 2);
            }
            return "XX";
            //return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String contabilizarCaja(boolean definitivo) {
        if (getKardexDeposito() == null) {
            return null;
        }
        if (reponer == 0) {
            MensajesErrores.advertencia("Caja Liquidada sin nada que reponer");
            return null;
        }
        kardex.setValor(BigDecimal.ZERO);
        Cuentas cuentaBanco = null;
        if (kardex.getValor().doubleValue() != 0) {
            if (getKardexDeposito().getBanco() == null) {
                return null;
            }
            cuentaBanco = getCuentasBean().traerCodigo(getKardexDeposito().getBanco().getCuenta());
            getCuentasBean().setCuenta(cuentaBanco);
            if (getCuentasBean().validaCuentaMovimiento()) {
                MensajesErrores.advertencia("Cuenta contable de banco mal configurada");
                return null;
            }
        }
        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");
        Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(codigo.getDescripcion());
        getCuentasBean().setCuenta(cuentaMovimiento);
        if (getCuentasBean().validaCuentaMovimiento()) {
            MensajesErrores.advertencia("Cuenta contable de movimiento mal configurada");
            return null;
        }
        kardex.setEstado(2);
        kardex.setFechaabono(kardex.getFechamov());
        int productoBanco = -1;
        int productoTipo = 1;
        if (kardexDeposito.getTipomov().getIngreso()) {
            productoBanco = 1;
            productoTipo = -1;
        }
        Tipoasiento ta = getKardexDeposito().getTipomov().getTipoasiento();

        int numeroAsiento = ta.getUltimo();
        numeroAsiento++;
        ta.setUltimo(numeroAsiento);
        listaReglones = new LinkedList<>();
        try {
            //
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            String partida = traer53();
            String cuenta = ctaInicio + partida + ctaFin;
            Cuentas cuentaEmpleado = getCuentasBean().traerCodigo(cuenta);
            getCuentasBean().setCuenta(cuentaEmpleado);
            if (getCuentasBean().validaCuentaMovimiento()) {
                MensajesErrores.advertencia("Cuenta contable de empleado  mal configurada : " + cuenta);
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            //
            Cabeceras cab = new Cabeceras();
            if (definitivo) {
                ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());

                cab.setDescripcion(getKardex().getObservaciones());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(getKardexDeposito().getTipomov().getTipoasiento());
                cab.setNumero(numeroAsiento);
                cab.setFecha(kardex.getFechamov());
                cab.setIdmodulo(kardexDeposito.getId());
                cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cab.setOpcion("LIQUIDACION_CAJAS");
                ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
            }
            Renglones r1 = new Renglones(); // reglon de banco

            if (kardex.getValor().doubleValue() != 0) {
                if (definitivo) {
                    r1.setCabecera(cab);
                }
                r1.setReferencia(kardex.getObservaciones() + " R1");
                r1.setValor(new BigDecimal(kardex.getValor().doubleValue() * productoBanco));
                r1.setCuenta(cuentaBanco.getCodigo());
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
                r1.setFecha(kardex.getFechamov());
                if (cuentaBanco.getAuxiliares() != null) {
                    r1.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                }
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
            }

            Renglones r0 = new Renglones(); // reglon de empleado
            if (definitivo) {
                r0.setCabecera(cab);
            }
            double valorF = 0;
            if (kardex.getValor().doubleValue() != 0) {
                valorF = fondo.getValor().doubleValue() - kardex.getValor().doubleValue();
            } else {
                valorF = reponer;
            }
            r0.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
            r0.setReferencia(fondo.getReferencia() + " R0");
            r0.setValor(new BigDecimal(valorF * productoBanco));
            r0.setCuenta(cuenta);
            r0.setPresupuesto(cuenta);
            r0.setFecha(kardex.getFechamov());
            // las dos cuentas
            Renglones r = new Renglones(); // reglon de caja chica
            if (definitivo) {
                r.setCabecera(cab);
            }
            valorF = 0;
            if (kardex.getValor().doubleValue() != 0) {
                valorF = fondo.getValor().doubleValue();
            } else {
                valorF = reponer;
            }
            r.setReferencia(kardex.getObservaciones() + " R");
            r.setValor(new BigDecimal(valorF * productoTipo));
            r.setCuenta(cuentaMovimiento.getCodigo());
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            r.setFecha(kardex.getFechamov());
            if (cuentaMovimiento.getAuxiliares() != null) {
                r.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
            }
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());

            // los dos renglones extras que pide Juan carlos
//            buscar lo quidado
            parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", fondo);
            parametros.put("estado", 4);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            List<Valesfondos> listaVales = ejbVales.encontarParametros(parametros);
            double valor = 0;
            for (Valesfondos v : listaVales) {
                valor += v.getValor().doubleValue();
                if (definitivo) {
                    double cuadrado = 0;
                    if (kardex.getValor().doubleValue() != 0) {
                        cuadrado = (r1.getValor().doubleValue() + r0.getValor().doubleValue()) - r.getValor().doubleValue();
                    } else {
                        cuadrado = r0.getValor().doubleValue() - r.getValor().doubleValue();
                    }
                    if (cuadrado != 0) {
                        MensajesErrores.advertencia("Asiento se encuentra descuadrado");
                        return null;
                    }
                    v.setEstado(6);
                    ejbVales.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            if (valor != 0) {
                Renglones r2 = new Renglones(); // reglon de banco
                if (definitivo) {
                    r2.setCabecera(cab);
                }
                r2.setReferencia(kardex.getObservaciones() + " R2");
//                r2.setValor(new BigDecimal(valor * productoTipo));
                r2.setValor(new BigDecimal(valor * productoTipo));
                r2.setCuenta(cuentaMovimiento.getCodigo());
                r2.setPresupuesto(cuentaMovimiento.getPresupuesto());
                r2.setFecha(kardex.getFechamov());
                if (cuentaMovimiento.getAuxiliares() != null) {
                    r2.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                }
                r2.setPresupuesto(cuentaMovimiento.getPresupuesto());

                Renglones r3 = new Renglones(); // reglon de empleado

                if (definitivo) {
                    r3.setCabecera(cab);
                }
                r3.setReferencia(kardex.getObservaciones() + " R3");
                r3.setValor(new BigDecimal(valor * -1 * productoTipo));
                r3.setCuenta(cuentaEmpleado.getCodigo());
                r3.setPresupuesto(cuentaEmpleado.getPresupuesto());
                r3.setFecha(kardex.getFechamov());
                if (cuentaEmpleado.getAuxiliares() != null) {
                    r3.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                }
                r3.setPresupuesto(cuentaEmpleado.getPresupuesto());
                listaReglones.add(r2);
                listaReglones.add(r3);
                if (definitivo) {
                    ejbRenglones.create(r2, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(r3, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            if (kardex.getValor().doubleValue() != 0) {
                listaReglones.add(r1);
            }
            if (r0.getValor().doubleValue() != 0) {
                listaReglones.add(r0);
            }
            if (r.getValor().doubleValue() != 0) {
                listaReglones.add(r);
            }
            if (definitivo) {
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                ejbRenglones.create(r0, parametrosSeguridad.getLogueado().getUserid());
                if (kardex.getValor().doubleValue() != 0) {
                    ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String contabilizar(boolean definitivo) {
//        if (reponer == 0) {
//            MensajesErrores.advertencia("Fondo Liquidar sin nada que reponer");
//            return null;
//        }
//
//        if (kardex.getTipomov() == null) {
//            return null;
//        }
        if (kardex == null) {
            return null;
        }
        kardex.setValor(BigDecimal.ZERO);

        if (fondo == null) {
            return null;
        }
        Cuentas cuentaBanco = null;
        if (!parcial) {
            if (kardex.getValor().doubleValue() != 0) {
                if (getKardexDeposito() != null) {
                    if (getKardexDeposito().getBanco() == null) {
                        return null;
                    }
                    cuentaBanco = getCuentasBean().traerCodigo(getKardexDeposito().getBanco().getCuenta());
                    getCuentasBean().setCuenta(cuentaBanco);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable de banco mal configurada");
                        return null;
                    }
                }
            }
        }
        String vale = ejbCabecera.validarCierre(kardex.getFechamov());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");
        Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(codigo.getDescripcion());
//        Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(kardex.getTipomov().getCuenta());

        getCuentasBean().setCuenta(cuentaMovimiento);
        if (getCuentasBean().validaCuentaMovimiento()) {
            MensajesErrores.advertencia("Cuenta contable de movimiento mal configurada");
            return null;
        }
        kardex.setEstado(2);
        kardex.setFechaabono(kardex.getFechamov());
//        formulario.editar();
        int productoBanco = -1;
        int productoTipo = 1;
        if (!parcial) {
            if (getKardexDeposito() != null) {
                if (kardexDeposito.getTipomov().getIngreso()) {
                    productoBanco = 1;
                    productoTipo = -1;
                }
            } else {
                productoBanco = 1;
                productoTipo = -1;
            }
        } else {
            productoBanco = 1;
            productoTipo = -1;
        }
//        Tipoasiento ta = getKardexDeposito().getTipomov().getTipoasiento();

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.rubro=7 and o.nombre like 'FONDO%'");
            List<Tipoasiento> listaTa = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento ta = null;
            for (Tipoasiento t : listaTa) {
                ta = t;
            }
            if (ta == null) {
                MensajesErrores.advertencia("No existe tipo de asiento para rubro 7");
                return null;
            }
            int numeroAsiento = ta.getUltimo();
            listaReglones = new LinkedList<>();
            listaReglonesGasto = new LinkedList<>();
            listaReglonesInversion = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            Fondos fNuevo = new Fondos();
            if (parcial) {
                parametros = new HashMap();
                String where = "  o.fondo=:fondo and o.estado=:estado ";
                parametros.put("fondo", fondo);
                parametros.put("estado", 4);
                parametros.put(";where", where);
                parametros.put(";orden", "o.fecha desc");
                List<Valesfondos> listaVales = ejbVales.encontarParametros(parametros);
                if (definitivo) {
                    fNuevo.setFecha(fondo.getFecha());
                    fNuevo.setEmpleado(fondo.getEmpleado());
                    fNuevo.setDepartamento(fondo.getDepartamento());
                    fNuevo.setObservaciones(fondo.getObservaciones());
                    fNuevo.setJefe(fondo.getJefe());
                    fNuevo.setCerrado(Boolean.TRUE);
                    fNuevo.setValor(fondo.getValor());
                    if (fondo.getKardexbanco() != null) {
                        fNuevo.setKardexbanco(fondo.getKardexbanco());
                    }
                    fNuevo.setPrcvale(fondo.getPrcvale());
                    fNuevo.setReferencia(fondo.getReferencia());
                    fNuevo.setApertura(fondo);
                    if (fondo.getKardexliquidacion() != null) {
                        fNuevo.setKardexliquidacion(fondo.getKardexliquidacion());
                    }
                    if (fondo.getRenglonliquidacion() != null) {
                        fNuevo.setRenglonliquidacion(fondo.getRenglonliquidacion());
                    }
                    ejbFondos.create(fNuevo, parametrosSeguridad.getLogueado().getUserid());
                }
            }
//            String partida = dc.getAsignacion().getClasificador().getCodigo().substring(0, 2);
            Cabeceras cabGasto = new Cabeceras();
            Cabeceras cab = new Cabeceras();
            Cabeceras cabReclasificacion = new Cabeceras();
            if (definitivo) {
                int num = numeroAsiento + 1;
                //Cabecera del gasto
                cabGasto.setDescripcion(getKardex().getObservaciones());
                cabGasto.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabGasto.setDia(new Date());
                cabGasto.setTipo(ta);
                cabGasto.setNumero(numeroAsiento);
                cabGasto.setFecha(kardex.getFechamov());
                if (parcial) {
                    cabGasto.setIdmodulo(fNuevo.getId());
                } else {
                    cabGasto.setIdmodulo(fondo.getId());
                }
                cabGasto.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cabGasto.setOpcion("GASTO_FONDOS");
                ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
                int numA = numeroAsiento + 2;
                //Cabecera Liquidacion
                cab.setDescripcion(getKardex().getObservaciones());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                if (!parcial) {
                    if (getKardexDeposito() != null) {
                        cab.setTipo(getKardexDeposito().getTipomov().getTipoasiento());
                    } else {
                        cab.setTipo(ta);
                    }
                } else {
                    cab.setTipo(ta);
                }
                cab.setNumero(numA);
                cab.setFecha(kardex.getFechamov());
                if (parcial) {
                    cab.setIdmodulo(fNuevo.getId());
                } else {
                    cab.setIdmodulo(fondo.getId());
                }
                cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cab.setOpcion("LIQUIDACION_FONDOS");
                ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
                int nume = numeroAsiento + 3;
                //Cabecera Reclasificacion  --  se graba solo si tiene reclasificacion
                cabReclasificacion.setDescripcion(getKardex().getObservaciones());
                cabReclasificacion.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabReclasificacion.setDia(new Date());
                cabReclasificacion.setTipo(ta);
                cabReclasificacion.setNumero(nume);
                cabReclasificacion.setFecha(kardex.getFechamov());
                if (parcial) {
                    cabReclasificacion.setIdmodulo(fNuevo.getId());
                } else {
                    cabReclasificacion.setIdmodulo(fondo.getId());
                }
                cabReclasificacion.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cabReclasificacion.setOpcion("GASTO_FONDOS_RECLASIFICACION");
                ta.setUltimo(nume);
                ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            }
            parametros = new HashMap();
            parametros.put(";where", "o.vale.fondo=:vale and o.vale.estado = 4");
            parametros.put("vale", fondo);
            List<Detallesfondo> lista = ejbDetallesvale.encontarParametros(parametros);
            //Se crea por cada detalle los renglones
            String cuenta = "";
            //Inicio de cuentas por renglones
            double valorLiquidacion = 0;
            double valorRetencionImpuesto = 0;
            for (Detallesfondo df : lista) {
                gastoFondo = null;
                gastoReclasificacion = null;
                String partida = traer53();
                String cuentaPresupuesto = df.getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
                partida = cuentaPresupuesto.substring(0, 2);
                cuenta = ctaInicio + partida + ctaFin;
                Cuentas cuentaEmpleado = getCuentasBean().traerCodigo(cuenta);
                getCuentasBean().setCuenta(cuentaEmpleado);
                if (getCuentasBean().validaCuentaMovimiento()) {
                    MensajesErrores.advertencia("Cuenta contable de empleado  mal configurada : " + cuenta);
                    return null;
                }
                if (df.getCuenta() != null) {
                    if (df.getCuenta().getCodigonif() == null || df.getCuenta().getCodigonif().isEmpty()) {
                        gastoFondo = df.getCuenta();
                    }
                    if (!(df.getCuenta().getCodigonif() == null || df.getCuenta().getCodigonif().isEmpty())) {
                        gastoReclasificacion = df.getCuenta();
                    }
                }
                double valoValidado = 0;
                double valoValidadoReclasif = 0;
                double valoTotal = 0;
                if (gastoFondo != null) {
                    valoValidado = df.getValor().doubleValue();
                }
                if (gastoReclasificacion != null) {
                    valoValidadoReclasif = df.getValor().doubleValue();
                }
                valoTotal = valoValidado + valoValidadoReclasif;
                List<Obligaciones> obli = new LinkedList<>();
                if (df.getVale().getObligacion() != null) {
                    obli.add(df.getVale().getObligacion());
                }
                double retencion = 0;
                double impuestoD = 0;
                String cuentaRet;
                String cuentaImp;
                Cuentas cuentaRetencion = null;
                Cuentas cuentaImpuesto = null;
                for (Obligaciones o : obli) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion");
                    parametros.put("obligacion", o);
                    List<Retencionescompras> listaRC = ejbReten.encontarParametros(parametros);
                    for (Retencionescompras rc : listaRC) {
                        if (rc.getRetencion() != null) {
                            retencion += rc.getValor().doubleValue();
                            cuentaRet = ctaInicio + rc.getPartida() + rc.getRetencion().getReclasificacion() + rc.getRetencion().getCodigo();
                            cuentaRetencion = getCuentasBean().traerCodigo(cuentaRet);
                        }
                        if (rc.getRetencionimpuesto() != null) {
                            impuestoD += rc.getValoriva().doubleValue();
                            cuentaImp = ctaInicio + rc.getPartida() + rc.getRetencionimpuesto().getReclasificacion() + rc.getRetencionimpuesto().getCodigo();
                            cuentaImpuesto = getCuentasBean().traerCodigo(cuentaImp);
                        }
                    }
                }
                // las dos cuentas
                if (valoTotal < 0) {
                    MensajesErrores.advertencia("Valor Validado no puede ser menor a cero");
                }
                if (valoTotal > 0) {
                    Renglones rGasto = new Renglones(); // reglon de gasto
                    if (gastoFondo != null) {
                        if (definitivo) {
                            rGasto.setCabecera(cabGasto);
                        }
                        rGasto.setReferencia(kardex.getObservaciones());
                        rGasto.setValor(new BigDecimal(valoValidado));
                        rGasto.setCuenta(gastoFondo.getCodigo());
                        rGasto.setPresupuesto(gastoFondo.getPresupuesto());
                        rGasto.setFecha(kardex.getFechamov());
                        if (gastoFondo.getAuxiliares() != null) {
                            rGasto.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                        }
                        Cuentas ctaGasto = cuentasBean.traerCodigo(gastoFondo.getCodigo());
                        inversiones(ctaGasto, rGasto, 1);
                    }
                    Renglones rGastoRec = new Renglones(); // reglon de gasto
                    if (gastoReclasificacion != null) {
                        if (definitivo) {
                            rGastoRec.setCabecera(cabGasto);
                        }
                        rGastoRec.setReferencia(kardex.getObservaciones());
                        rGastoRec.setValor(new BigDecimal(valoValidadoReclasif));
                        rGastoRec.setCuenta(gastoReclasificacion.getCodigo());
                        rGastoRec.setPresupuesto(gastoReclasificacion.getPresupuesto());
                        rGastoRec.setFecha(kardex.getFechamov());
                        if (gastoReclasificacion.getAuxiliares() != null) {
                            rGastoRec.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                        }
                        Cuentas ctaGastoRec = cuentasBean.traerCodigo(gastoReclasificacion.getCodigo());
                        inversiones(ctaGastoRec, rGastoRec, 1);
                    }
                    Renglones r2 = new Renglones();
                    if (retencion != 0) {
                        if (definitivo) {
                            r2.setCabecera(cabGasto);
                        }
                        r2.setReferencia(kardex.getObservaciones() + " R2");
                        r2.setValor(new BigDecimal(retencion * -1));
                        r2.setCuenta(cuentaRetencion.getCodigo());
                        r2.setPresupuesto(cuentaRetencion.getPresupuesto());
                        r2.setFecha(kardex.getFechamov());
                        r2.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                        Cuentas ctar2 = cuentasBean.traerCodigo(cuentaRetencion.getCodigo());
                        inversiones(ctar2, r2, 1);
                    }
                    Renglones r3 = new Renglones();
                    if (impuestoD != 0) {
                        if (definitivo) {
                            r3.setCabecera(cabGasto);
                        }
                        r3.setReferencia(kardex.getObservaciones() + " R3");
                        r3.setValor(new BigDecimal(impuestoD * -1));
                        r3.setCuenta(cuentaImpuesto.getCodigo());
                        r3.setPresupuesto(cuentaImpuesto.getPresupuesto());
                        r3.setFecha(kardex.getFechamov());
                        r3.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                        Cuentas ctar3 = cuentasBean.traerCodigo(cuentaImpuesto.getCodigo());
                        inversiones(ctar3, r3, 1);
                    }
                    Renglones rPorPagar = new Renglones(); // reglon por pagar
                    rPorPagar.setReferencia(kardex.getObservaciones());
                    if (retencion == 0 && impuestoD == 0) {
                        rPorPagar.setValor(new BigDecimal(valoTotal * -1));

                    }
                    if (retencion != 0 && impuestoD != 0) {
                        rPorPagar.setValor(new BigDecimal((valoTotal - (retencion + impuestoD)) * -1));
                    } else {
                        if (retencion != 0) {
                            rPorPagar.setValor(new BigDecimal((valoTotal - retencion) * -1));
                        }
                        if (impuestoD != 0) {
                            rPorPagar.setValor(new BigDecimal((valoTotal - impuestoD) * -1));
                        }
                    }
                    rPorPagar.setCuenta(cuentaEmpleado.getCodigo());
                    rPorPagar.setPresupuesto(cuentaEmpleado.getPresupuesto());
                    rPorPagar.setFecha(kardex.getFechamov());
                    rPorPagar.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                    Cuentas ctaPorPAgar = cuentasBean.traerCodigo(cuentaEmpleado.getCodigo());
                    inversiones(ctaPorPAgar, rPorPagar, 1);
                    if (gastoFondo != null) {
                        listaReglonesGasto.add(rGasto);
                        listaReglonesGasto.add(rPorPagar);
                    }
                    if (retencion != 0) {
                        listaReglonesGasto.add(r2);
                    }
                    if (impuestoD != 0) {
                        listaReglonesGasto.add(r3);
                    }
                    if (gastoReclasificacion != null) {
                        listaReglonesGasto.add(rGastoRec);
                        listaReglonesGasto.add(rPorPagar);
                    }
                    //Renglones por cada partida

                    Renglones r0 = new Renglones(); // reglon de empleado
                    r0.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                    r0.setReferencia(fondo.getReferencia() + " R0");
//                    r0.setValor(new BigDecimal(df.getValor().doubleValue() * productoBanco));
                    r0.setCuenta(cuenta);
                    r0.setPresupuesto(cuenta);
//                    r0.setFecha(fondo.getFecha());
                    r0.setFecha(kardex.getFechamov());
                    if (retencion == 0 && impuestoD == 0) {
                        r0.setValor(new BigDecimal(df.getValor().doubleValue() * productoBanco));
                    }
                    if (retencion != 0 && impuestoD != 0) {
                        r0.setValor(new BigDecimal((df.getValor().doubleValue() - (retencion + impuestoD)) * productoBanco));
                    } else {
                        if (retencion != 0) {
                            r0.setValor(new BigDecimal((df.getValor().doubleValue() - retencion) * productoBanco));
                        }
                        if (impuestoD != 0) {
                            r0.setValor(new BigDecimal((df.getValor().doubleValue() - impuestoD) * productoBanco));
                        }
                    }
                    valorRetencionImpuesto += retencion + impuestoD;
                    Cuentas ctaro = cuentasBean.traerCodigo(cuenta);
                    inversiones(ctaro, r0, 1);
                    valorLiquidacion += df.getValor().doubleValue();
                    listaReglones.add(r0);
                }
            }
            //Fin de renglones
            //Inicio de renglones de Liquidacion
            Renglones r1 = new Renglones(); // reglon de banco
            if (kardex.getValor().doubleValue() != 0) {
                if (definitivo) {
                    r1.setCabecera(cab);
                }
                r1.setReferencia(kardex.getObservaciones() + " R1");
                r1.setValor(new BigDecimal(kardex.getValor().doubleValue() * productoBanco));
                r1.setCuenta(cuentaBanco.getCodigo());
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
                r1.setFecha(kardex.getFechamov());
                if (cuentaBanco.getAuxiliares() != null) {
                    r1.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
                }
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
                Cuentas ctar1 = cuentasBean.traerCodigo(cuentaBanco.getCodigo());
                inversiones(ctar1, r1, 1);
            }
            //Renglones por cada partida

            double valorF = 0;
            // las dos cuentas
            Renglones r = new Renglones(); // reglon de fondo chica
            if (definitivo) {
                r.setCabecera(cab);
            }
            valorF = 0;
            if (kardex.getValor().doubleValue() != 0) {
                valorF = fondo.getValor().doubleValue();
            } else {
//                valorF = reponer;
                valorF = valorLiquidacion - valorRetencionImpuesto;
            }
            r.setReferencia(kardex.getObservaciones() + " R");
            r.setValor(new BigDecimal(valorF * productoTipo));
            r.setCuenta(cuentaMovimiento.getCodigo());
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            r.setFecha(kardex.getFechamov());
            if (cuentaMovimiento.getAuxiliares() != null) {
                r.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
            }
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            inversiones(cuentaMovimiento, r, 1);
            // los dos renglones extras que pide Juan carlos
//            buscar lo quidado
            parametros = new HashMap();
            String where = "  o.fondo=:fondo and o.estado=:estado ";
            parametros.put("fondo", fondo);
            parametros.put("estado", 4);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            List<Valesfondos> listaVales = ejbVales.encontarParametros(parametros);
//            double valor = 0;
//            double cuadrado = 0;
//            for (Valesfondos v : listaVales) {
//                valor += v.getValor().doubleValue();
//            }
//            cuadrado = valorLiquidacion - valor;
//            if (cuadrado != 0) {
//                MensajesErrores.advertencia("Asiento se encuentra descuadrado");
//                return null;
//            }

            for (Valesfondos v : listaVales) {
                if (definitivo) {
                    v.setEstado(6);
                    if (parcial) {
                        v.setFondo(fNuevo);
                    }
                    ejbVales.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            if (kardex.getValor().doubleValue() != 0) {
                listaReglones.add(r1);
            }
//            if (r0.getValor().doubleValue() != 0) {
//                listaReglones.add(r0);
//            }
            if (r.getValor().doubleValue() != 0) {
                listaReglones.add(r);
            }
            if (definitivo) {
                if (!listaReglonesGasto.isEmpty()) {
                    ejbCabecera.create(cabGasto, parametrosSeguridad.getLogueado().getUserid());
                }
                if (!listaReglones.isEmpty()) {
                    ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
                }
                if (!listaReglonesInversion.isEmpty()) {
                    ejbCabecera.create(cabReclasificacion, parametrosSeguridad.getLogueado().getUserid());
                }

                for (Renglones rg : listaReglonesGasto) {
                    rg.setCabecera(cabGasto);
                    ejbRenglones.create(rg, parametrosSeguridad.getLogueado().getUserid());
                }
                for (Renglones rl : listaReglones) {
                    rl.setCabecera(cab);
                    ejbRenglones.create(rl, parametrosSeguridad.getLogueado().getUserid());
                }
                for (Renglones ri : listaReglonesInversion) {
                    ri.setCabecera(cabReclasificacion);
                    ejbRenglones.create(ri, parametrosSeguridad.getLogueado().getUserid());
                }
//                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                //                ejbRenglones.create(r0, parametrosSeguridad.getLogueado().getUserid());
                if (kardex.getValor().doubleValue() != 0) {
                    ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
                }

                List<Detallesfondo> lisaDetalles = traerDetallesVale();
                Certificaciones certificacion = null;
                for (Detallesfondo dv : lisaDetalles) {
                    certificacion = dv.getDetallecertificacion().getCertificacion();
                }
                Codigos cod = codigosBean.traerCodigo("NUM", "05");
                if (cod == null) {
                    MensajesErrores.advertencia("No existe numeración para compromiso");
                    return null;
                }
                if (cod.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }
                Integer num = Integer.parseInt(cod.getParametros());
                Integer nume = num + 1;
                Compromisos c = null;
                Detallecompromiso dc = null;
                for (Detallesfondo dv : lisaDetalles) {
                    if (dv.getDetallecompromiso() != null) {
                        c = dv.getDetallecompromiso().getCompromiso();
                    }
                }
                if (c == null) {
                    c = new Compromisos();
                    c.setFecha(kardex.getFechamov());
                    c.setContrato(null);
                    c.setImpresion(fondo.getFecha());
                    c.setMotivo(fondo.getObservaciones());
                    c.setProveedor(null);
                    c.setCertificacion(certificacion);
                    c.setEmpleado(fondo.getEmpleado());
                    c.setBeneficiario(fondo.getEmpleado());
                    c.setDireccion(fondo.getDepartamento());
                    c.setNumerocomp(nume);
                    ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
                    cod.setParametros(nume + "");
                    ejbCodigos.edit(cod, parametrosSeguridad.getLogueado().getUserid());
                    for (Detallesfondo dv : lisaDetalles) {
                        //            CREACION DE DETALLES COMPROMISOS
                        dc = new Detallecompromiso();
                        dc.setCompromiso(c);
                        dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                        dc.setMotivo(fondo.getObservaciones());
                        dc.setFecha(kardex.getFechamov());
                        dc.setValor(dv.getValor());
                        dc.setSaldo(new BigDecimal(BigInteger.ZERO));
                        ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
//                ACTUALIZA DETALLE DE VALE AGREGANDO EL DETALLE DE COMPROMISO
                        dv.setDetallecompromiso(dc);
                        ejbDetallesvale.edit(dv, parametrosSeguridad.getLogueado().getUserid());
                    }
                } else {
                    for (Detallesfondo dv : lisaDetalles) {
                        if (dv.getDetallecompromiso() != null) {
                            dc = dv.getDetallecompromiso();
                            dc.setValor(dv.getValor());
                            ejbDetallecompromiso.edit(dc, parametrosSeguridad.getLogueado().getUserid());
                        } else {
                            dc = new Detallecompromiso();
                            dc.setCompromiso(c);
                            dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                            dc.setMotivo(fondo.getObservaciones());
                            dc.setFecha(kardex.getFechamov());
                            dc.setValor(dv.getValor());
                            dc.setSaldo(new BigDecimal(BigInteger.ZERO));
                            ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
                        }
                    }

                }
//                ejbKardexbanco.edit(kardex, parametrosSeguridad.getLogueado().getUserid());
                if (parcial) {
                    fondo = fNuevo;
                }
            }
//            formularioImpresion.cancelar();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String contabilizarDesconocidos(double valorF) {
        if (fondo == null) {
            return null;
        }

        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");
        Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(codigo.getDescripcion());
        getCuentasBean().setCuenta(cuentaMovimiento);
        if (getCuentasBean().validaCuentaMovimiento()) {
            MensajesErrores.advertencia("Cuenta contable de movimiento mal configurada");
            return null;
        }
        Codigos codigoND = codigosBean.traerCodigo("CUENTASCXC", "DEPNOID");
        Cuentas cuentaMovimientoND = getCuentasBean().traerCodigo(codigoND.getParametros());
        getCuentasBean().setCuenta(cuentaMovimientoND);
        if (getCuentasBean().validaCuentaMovimiento()) {
            MensajesErrores.advertencia("Cuenta contable de movimiento mal configurada");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.rubro=7 and o.nombre like 'FONDO%'");
            List<Tipoasiento> listaTa = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento ta = null;
            for (Tipoasiento t : listaTa) {
                ta = t;
            }
            if (ta == null) {
                MensajesErrores.advertencia("No existe tipo de asiento para rubro 7");
                return null;
            }
            int numeroAsiento = ta.getUltimo();
            int numA = numeroAsiento + 1;
            Cabeceras cabDesc = new Cabeceras();
            cabDesc.setDescripcion(getKardex().getObservaciones());
            cabDesc.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabDesc.setDia(new Date());
            cabDesc.setTipo(ta);
            cabDesc.setNumero(numA);
            cabDesc.setFecha(kardex.getFechamov());
            cabDesc.setIdmodulo(fondo.getId());
            cabDesc.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            cabDesc.setOpcion("FONDOS_DEPOSITO_DESCONOCIDO");
            ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            ejbCabecera.create(cabDesc, parametrosSeguridad.getLogueado().getUserid());

            //renglones
            Renglones r1 = new Renglones(); // reglon no especificados
            r1.setCabecera(cabDesc);
            r1.setReferencia(kardex.getObservaciones());
            r1.setValor(new BigDecimal(valorF));
            r1.setCuenta(cuentaMovimientoND.getCodigo());
            r1.setPresupuesto(cuentaMovimientoND.getPresupuesto());
            r1.setFecha(kardex.getFechamov());
//            if (cuentaMovimientoND.getAuxiliares() != null) {
//                r1.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
//            }

            Renglones r = new Renglones(); // reglon de fondo 
            r.setReferencia(kardex.getObservaciones());
            r.setValor(new BigDecimal(valorF * -1));
            r.setCuenta(cuentaMovimiento.getCodigo());
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            r.setFecha(kardex.getFechamov());
            if (cuentaMovimiento.getAuxiliares() != null) {
                r.setAuxiliar(fondo.getEmpleado().getEntidad().getPin());
            }
            ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
            ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void inversiones(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
//                rasInvInt.setDetallecompromiso(ras.getDetallecompromiso());
            }
            rasInvInt.setFecha(kardex.getFechamov());
            estaEnRasInversiones(rasInvInt);
            Renglones rasContrapate = new Renglones();
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasContrapate.setFecha(kardex.getFechamov());

//            rasContrapate.setCuenta(ras.getCuenta());
            Cuentas ctaNif = validaCuenta(cuenta.getCodigonif());
            if (ctaNif == null) {
                rasContrapate.setCuenta(ras.getCuenta());
            } else {
                if (!((ctaNif.getCodigonif() == null) || (ctaNif.getCodigonif().isEmpty()))) {
                    rasContrapate.setCuenta(ctaNif.getCodigonif());
                } else {
                    rasContrapate.setCuenta(ras.getCuenta());
                }
            }
            estaEnRasInversiones(rasContrapate);

        }
    }

    private Cuentas validaCuenta(String cuenta) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", cuenta);
            List<Cuentas> cl;
            cl = ejbCuentas.encontarParametros(parametros);

            if (cl.isEmpty()) {
                return null;
            }
            for (Cuentas c : cl) {
                if (c.getImputable()) {
                    return c;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean estaEnRasInversiones(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
//        if (r1.getDetallecompromiso() == null) {
//            r1.setDetallecompromiso(new Detallecompromiso());
//        }
        for (Renglones r : listaReglonesInversion) {
//            if (r.getDetallecompromiso() == null) {
//                r.setDetallecompromiso(new Detallecompromiso());
//            }
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }

            if ((r.getDetallecompromiso() != null) && (r1.getDetallecompromiso() != null)) {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getDetallecompromiso().equals(r1.getDetallecompromiso())
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            } else {
                if ((r.getCuenta().equals(r1.getCuenta()))
                        && r.getAuxiliar().equals(r1.getAuxiliar())) {
                    double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                    r.setValor(new BigDecimal(valor));
                    return true;
                }
            }
        }
        listaReglonesInversion.add(r1);
        return false;
    }

    public List<Detallesfondo> traerDetallesVale() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.vale.fondo=:fondo and o.vale.estado=6");
        parametros.put("fondo", fondo);
        try {
            return ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Detallesfondo> traerDetallesValeEstado4() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.vale.fondo=:fondo and o.vale.estado=4");
        parametros.put("fondo", fondo);
        try {
            return ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if ((kardex.getDocumento() == null) || (kardex.getDocumento().isEmpty())) {
            MensajesErrores.advertencia("Es necesario documento en movimiento");
            return true;
        }
        if ((kardex.getFechamov() == null) || (kardex.getFechamov().after(new Date()))) {
            MensajesErrores.advertencia("Es necesario fecha de movimiento válido en movimiento");
            return true;
        }
        if (kardex.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Es necesario fecha de movimiento válido mayor o igual a periodo vigente");
            return true;
        }
//        if (kardex.getBanco() == null) {
//            MensajesErrores.advertencia("Es necesario banco del movimiento");
//            return true;
//        }
        if (kardex.getBanco() == null) {
            MensajesErrores.advertencia("Es necesario tipo de movimiento");
            return true;
        }
        if ((kardex.getValor() == null) || (kardex.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Es necesario valor de movimiento");
            return true;
        }

        Empleados e = fondo.getEmpleado();
        kardex.setAuxiliar(empleadosBean.getEntidad().getPin());

        return false;
    }

    public String insertar() {
//        if (validar()) {
//            return null;
//        }
        try {
            //Buscar si existen vales en otro estado
            Map parametros = new HashMap();
            if (!parcial) {
                parametros = new HashMap();
                parametros.put(";where", "o.fondo=:fondo and o.estado!=4");
                parametros.put("fondo", fondo);
                List<Valesfondos> lista = ejbVales.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    MensajesErrores.advertencia("Existen vales de fondo en proceso");
                    return null;
                }
                fondo.setCerrado(Boolean.TRUE);
            } else {
                fondo.setCerrado(Boolean.FALSE);
            }
            kardex.setFechagraba(new Date());

            double cuadre3 = Math.round(fondo.getValor().doubleValue());
            double dividido3 = cuadre3 / 100;
            BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
            double valorFondo = (valortotal3.doubleValue());
            double valor = 0;

            if (masDepositosDesconocidos) {
                for (Kardexbanco kb : listadoPropioDesconocidos) {
                    if (kb.getSeleccionado()) {
                        Depositos d = new Depositos();
                        d.setKardexliquidacion(kb);
                        d.setFondo(fondo);
                        ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                    }
                    valor += kb.getValor().doubleValue();
                }
                double cuadre = Math.round(valor);
                double dividido = cuadre / 100;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double valorDepo = (valortotal.doubleValue());
                //Si el valor de los depositos es igual al valor del viatico solo se guarda el registro sin contabilizacion 
                if (valorDepo == valorFondo) {
                    fondo.setCerrado(Boolean.TRUE);
                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                    contabilizarDesconocidos(valorDepo);
                } else {
                    if (reposicion.getDocumento() == null) {
                    } else {
                        if (grabarReposicion()) {
                            MensajesErrores.advertencia("Error al grabar Reposicion");
                            return null;
                        }
                    }
                    contabilizarDesconocidos(valorDepo);
                    contabilizar(true);
                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                }
                imprimirGasto();
            } else {
                if (kardexDepositoDesconocido != null) {
                    fondo.setKardexliquidacion(kardexDepositoDesconocido);
                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                    double cuadre = Math.round(kardexDepositoDesconocido.getValor().doubleValue());
                    double dividido = cuadre / 100;
                    BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                    double valorDepo = (valortotal.doubleValue());
                    if (valorDepo == valorFondo) {
                        fondo.setCerrado(Boolean.TRUE);
                        ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                        contabilizarDesconocidos(valorDepo);
                    } else {
                        if (reposicion.getDocumento() == null) {
                        } else {
                            if (grabarReposicion()) {
                                MensajesErrores.advertencia("Error al grabar Reposicion");
                                return null;
                            }
                        }
                        contabilizarDesconocidos(valorDepo);
                        contabilizar(true);
                        ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                    }
                    imprimirGasto();
                } else {
                    if (masDepositos) {
                        for (Kardexbanco kb : listadoPropio) {
                            if (kb.getSeleccionado()) {
                                Depositos d = new Depositos();
                                d.setKardexliquidacion(kb);
                                d.setFondo(fondo);
                                ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                            }
                            valor += kb.getValor().doubleValue();
                        }
                        double cuadre = Math.round(valor);
                        double dividido = cuadre / 100;
                        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                        double valorDepo = (valortotal.doubleValue());
                        //Si el valor de los depositos es igual al valor del viatico solo se guarda el registro sin contabilizacion 
                        if (valorDepo == valorFondo) {
                            fondo.setCerrado(Boolean.TRUE);
                            ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                        } else {
                            if (reposicion.getDocumento() == null) {
                            } else {
                                if (grabarReposicion()) {
                                    MensajesErrores.advertencia("Error al grabar Reposicion");
                                    return null;
                                }
                            }
                            contabilizar(true);
                            ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                        }
                        imprimirGasto();
                    } else {
                        if (kardexDeposito != null) {
                            fondo.setKardexliquidacion(kardexDeposito);
                            ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                            double cuadre = Math.round(kardexDeposito.getValor().doubleValue());
                            double dividido = cuadre / 100;
                            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                            double valorDepo = (valortotal.doubleValue());
                            if (valorDepo == valorFondo) {
                                fondo.setCerrado(Boolean.TRUE);
                                ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                            } else {
                                if (reposicion.getDocumento() == null) {
                                } else {
                                    if (grabarReposicion()) {
                                        MensajesErrores.advertencia("Error al grabar Reposicion");
                                        return null;
                                    }
                                }
                                contabilizar(true);
                                ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                            }
                            imprimirGasto();
                        } else {
                            if (masDescuentos) {
                                for (Renglones r : listadoRol) {
                                    if (r.getSeleccionado()) {
                                        Depositos d = new Depositos();
                                        d.setRenglonliquidacion(r);
                                        d.setFondo(fondo);
                                        ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                                    }
                                    valor += r.getValor().doubleValue();
                                }
                                double cuadre = Math.round(valor);
                                double dividido = cuadre / 100;
                                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                                double valorDesc = (valortotal.doubleValue());
                                if (valorDesc == valorFondo) {
                                    fondo.setCerrado(Boolean.TRUE);
                                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                                } else {
                                    if (reposicion.getDocumento() == null) {
                                    } else {
                                        if (grabarReposicion()) {
                                            MensajesErrores.advertencia("Error al grabar Reposicion");
                                            return null;
                                        }
                                    }
                                    contabilizar(true);
                                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                                }
                                imprimirGasto();
                            } else {
                                if (RenglonDescuento != null) {
                                    fondo.setRenglonliquidacion(RenglonDescuento);
                                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                                    double cuadre = Math.round(RenglonDescuento.getValor().doubleValue());
                                    double dividido = cuadre / 100;
                                    BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                                    double valorDesc = (valortotal.doubleValue());
                                    if (valorDesc == valorFondo) {
                                        fondo.setCerrado(Boolean.TRUE);
                                        ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                                    } else {
                                        if (reposicion.getDocumento() == null) {
                                        } else {
                                            if (grabarReposicion()) {
                                                MensajesErrores.advertencia("Error al grabar Reposicion");
                                                return null;
                                            }
                                            contabilizar(true);
                                            ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                                        }
                                    }
                                    imprimirGasto();
                                } else {
                                    if (reposicion.getDocumento() == null) {
                                    } else {
                                        if (grabarReposicion()) {
                                            MensajesErrores.advertencia("Error al grabar Reposicion");
                                            return null;
                                        }
                                    }
                                    contabilizar(true);

                                    ejbFondos.edit(fondo, parametrosSeguridad.getLogueado().getUserid());
                                    imprimirGasto();
                                }
                            }
                        }
                    }
                }
            }
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        fondo = null;
        reponer = 0;
        formulario.cancelar();
        MensajesErrores.informacion("Registro grabado con éxito");
        return null;
    }

    public boolean grabarReposicion() {
        try {
            if (!(reposicion.getDocumento().getCodigo().equals("OTR"))) {
                if (reposicion.getNumerodocumento() == null || reposicion.getNumerodocumento() == 0) {
                    MensajesErrores.advertencia("Ingrese número de Documento");
                    return true;
                }
            }
            if (impuesto != null) {
                if (reposicion.getBase() == null) {
                    reposicion.setBase(BigDecimal.ZERO);
                }
                double base = reposicion.getBase().setScale(2, RoundingMode.HALF_UP).doubleValue();
                double cuadrei = impuesto.getPorcentaje().setScale(2).doubleValue() / 100;
                double ajuste = (double) reposicion.getAjuste() / 100;
                double producto = (base * cuadrei) + ajuste;
                DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
                String vstr = df.format(producto);
                BigDecimal ivb = new BigDecimal(vstr).setScale(2, RoundingMode.HALF_UP);
                reposicion.setIva(ivb);
            } else {
                reposicion.setIva(BigDecimal.ZERO);
            }
            if (puntoemision != null) {
                reposicion.setPuuntoemision(puntoemision.getCodigo());
            }
            if (sucursal != null) {
                reposicion.setEstablecimiento(sucursal.getRuc());
            }
            Map parametros = new HashMap();
            if (reposicion.getDocumento().getCodigo().equals("LIQCOM")) {
                parametros = new HashMap();
                parametros.put(";where", "o.numerodocumento=:numerodocumento");
                parametros.put("numerodocumento", reposicion.getNumerodocumento());
                List<Reposisciones> lista = ejbReposisciones.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    MensajesErrores.advertencia("Número de Liquidación de Compras duplicado");
                    return true;
                }

            }
            ejbReposisciones.create(reposicion, parametrosSeguridad.getLogueado().getUserid());
            parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo and o.estado=4");
            parametros.put("fondo", fondo);
            List<Valesfondos> lista2 = ejbVales.encontarParametros(parametros);
            if (!lista2.isEmpty()) {
                for (Valesfondos vf : lista2) {
                    vf.setReposiscion(reposicion);
                    ejbVales.edit(vf, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public SelectItem[] getComboSucursales() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.ruc desc");
        try {
            return Combos.getSelectItems(ejbSucursales.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(SucursalesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPuntoemision() {
        if (sucursal == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.sucursal=:sucursal");
        parametros.put("sucursal", sucursal);
        try {
            return Combos.getSelectItems(ejbPuntoemision.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PuntoEmisionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String sube() {
        int ajuste = reposicion.getAjuste();
        ajuste++;
        reposicion.setAjuste(ajuste);
        return null;
    }

    public String baja() {
        int ajuste = reposicion.getAjuste();
        ajuste--;
        reposicion.setAjuste(ajuste);
        return null;
    }

    public SelectItem[] getComboAutorizaciones2() {
        if (fondo.getEmpleado() == null) {
            return null;
        }
        if (reposicion.getDocumento() == null) {
            return null;
        }
        try {
            String ruc = fondo.getEmpleado().getEntidad().getPin() + "001";
            Map parametros = new HashMap();
            parametros.put(";where", "o.ruc=:ruc");
            parametros.put("ruc", ruc);
            List<Empresas> lista = ejbEmpresas.encontarParametros(parametros);
            Empresas e = null;
            if (!lista.isEmpty()) {
                e = lista.get(0);
                parametros = new HashMap();
                parametros.put(";where", "o.empresa=:empresa and o.tipodocumento=:tipo");
                parametros.put("empresa", e);
                parametros.put(";orden", "o.inicio desc");
                parametros.put("tipo", reposicion.getDocumento());
                return Combos.getSelectItems(ejbAutorizaciones.encontarParametros(parametros), false);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearAutorizacion() {
        try {
            if (fondo.getEmpleado() == null) {
                MensajesErrores.advertencia("Seleccione un proveedor  primero");
                return null;
            }
            String ruc = fondo.getEmpleado().getEntidad().getPin() + "001";
            Map parametros = new HashMap();
            parametros.put(";where", "o.ruc=:ruc");
            parametros.put("ruc", ruc);
            List<Empresas> lista = ejbEmpresas.encontarParametros(parametros);
            Empresas e = null;
            if (!lista.isEmpty()) {
                e = lista.get(0);
            } else {
                MensajesErrores.advertencia("El usuario no tiene ruc");
                return null;
            }

            formularioAuto.insertar();
            autorizacionProveedor = new Autorizaciones();
            autorizacionProveedor.setEmpresa(e);
            autorizacionProveedor.setFechaemision(new Date());
            autorizacionProveedor.setFechacaducidad(new Date());

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validarAutorizacion() {

        if ((autorizacionProveedor.getEstablecimiento() == null) || (autorizacionProveedor.getEstablecimiento().isEmpty())) {
            MensajesErrores.advertencia("Es necesario establecimiento de autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getAutorizacion() == null) || (autorizacionProveedor.getAutorizacion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario autorización de autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getPuntoemision() == null) || (autorizacionProveedor.getPuntoemision().isEmpty())) {
            MensajesErrores.advertencia("Es necesario punto de emisión de autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getFechacaducidad() == null)) {
            MensajesErrores.advertencia("Es necesario fecha de caducidad de autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getFechaemision() == null)) {
            MensajesErrores.advertencia("Es necesario fecha de emisión de autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getFechacaducidad().before(autorizacionProveedor.getFechaemision()))) {
            MensajesErrores.advertencia("Fecha de caducidad debe ser mayor a fecha de emisión en autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getInicio() == null) || (autorizacionProveedor.getInicio() <= 0)) {
            MensajesErrores.advertencia("Es necesario Fin de serie válido en autorizacion");
            return true;
        }
        if ((autorizacionProveedor.getFin() == null) || (autorizacionProveedor.getFin() <= autorizacionProveedor.getInicio())) {
            MensajesErrores.advertencia("Es necesario Inicio de serie válido menor a finen autorizacion");
            return true;
        }
        return false;
    }

    public String insertarAutorizacion() {
        if (validarAutorizacion()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            ejbAutorizaciones.create(autorizacionProveedor, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAuto.cancelar();
//        buscar();
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
     * @return the fondo
     */
    public Fondos getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(Fondos fondo) {
        this.fondo = fondo;
    }

    /**
     * @return the listaFondos
     */
    public List<Fondos> getListaFondos() {
        return listaFondos;
    }

    /**
     * @param listaFondos the listaFondos to set
     */
    public void setListaFondos(List<Fondos> listaFondos) {
        this.listaFondos = listaFondos;
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the direccion
     */
    public Organigrama getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(Organigrama direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the formularioImpresion
     */
    public Formulario getFormularioImpresion() {
        return formularioImpresion;
    }

    /**
     * @param formularioImpresion the formularioImpresion to set
     */
    public void setFormularioImpresion(Formulario formularioImpresion) {
        this.formularioImpresion = formularioImpresion;
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

    /**
     * @return the listaReglones
     */
    public List<Renglones> getListaReglones() {
        return listaReglones;
    }

    /**
     * @param listaReglones the listaReglones to set
     */
    public void setListaReglones(List<Renglones> listaReglones) {
        this.listaReglones = listaReglones;
    }

    /**
     * @return the listaDetallesC
     */
    public List<Detallecompromiso> getListaDetallesC() {
        return listaDetallesC;
    }

    /**
     * @param listaDetallesC the listaDetallesC to set
     */
    public void setListaDetallesC(List<Detallecompromiso> listaDetallesC) {
        this.listaDetallesC = listaDetallesC;
    }

    /**
     * @return the cabecera
     */
    public Cabeceras getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabeceras cabecera) {
        this.cabecera = cabecera;
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

    public List<Renglones> getListaReglonesPreliminar() {
        contabilizar(false);
        return listaReglones;
    }

    public SelectItem[] getComboFondos() {
        try {
            Map parametros = new HashMap();
            String where = "   o.cerrado=false and o.kardexbanco is not null";
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            listaFondos = ejbFondos.encontarParametros(parametros);
            return Combos.getSelectItems(listaFondos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(kardex.getValor().doubleValue());

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

    private void traerRenglones() {
        if (kardex != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id"
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion='KARDEX BANCOS'");
            parametros.put("id", kardex.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                listaReglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

//    public String imprimir(Kardexbanco kardex) {
//        this.kardex = kardex;
//        traerRenglones();
//        for (Renglones r : listaReglones) {
//            Cuentas c = getCuentasBean().traerCodigo(r.getCuenta());
//            r.setNombre(c.getNombre());
//            double valor = r.getValor().doubleValue();
//            if (valor > 0) {
//                r.setDebitos(valor);
//            } else {
//                r.setCreditos(valor * -1);
//            }
//            if (c.getAuxiliares() != null) {
//
//                VistaAuxiliares v = parametrosSeguridad.traerAuxiliar(r.getAuxiliar());
//                r.setAuxiliarNombre(v == null ? "" : v.getNombre());
//            }
//        }
//        Collections.sort(listaReglones, new valorComparator());
//        Map parametros = new HashMap();
//        parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
//        parametros.put("empresa", kardex.getBeneficiario());
//        parametros.put("nombrelogo", "Comprobante de " + kardex.getTipomov().getDescripcion() + " No : " + kardex.getId());
//        parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
//        parametros.put("fecha", kardex.getFechamov());
//        if (kardex.getEgreso() == null) {
//            parametros.put("documento", kardex.getDocumento());
//        } else {
//            parametros.put("documento", kardex.getEgreso().toString());
//        }
//
//        parametros.put("modulo", getValorStr());
//        parametros.put("descripcion", kardex.getObservaciones());
//        parametros.put("obligaciones", getValorStr());
//        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
//        parametros.put("SUBREPORT_DIR", realPath + "/");
//        Calendar c = Calendar.getInstance();
//        reporte = new Reportesds(parametros,
//                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
//                listaReglones, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf");
//        formularioImpresion.editar();
//        return null;
//    }
    /**
     * @return the reponer
     */
    public double getReponer() {
        return reponer;
    }

    /**
     * @param reponer the reponer to set
     */
    public void setReponer(double reponer) {
        this.reponer = reponer;
    }

    public SelectItem[] getComboKardex() {
        if (fondo == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.tipomov.tipo='OTR' and o.auxiliar=:auxiliar and o.estado=2";
            parametros.put(";where", where);
            parametros.put("auxiliar", fondo.getEmpleado().getEntidad().getPin());
//            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            List<Kardexbanco> listado = ejbKardexbanco.encontarParametros(parametros);
            listadoPropio = new LinkedList<>();
            for (Kardexbanco k : listado) {
                parametros = new HashMap();
                where = "  o.kardexliquidacion=:kardex";
                parametros.put(";where", where);
                parametros.put("kardex", k);
                List<Fondos> listaFondoKardex = ejbFondos.encontarParametros(parametros);
                if (listaFondoKardex.isEmpty()) {
                    listadoPropio.add(k);
                }
            }
            return Combos.getSelectItems(listadoPropio, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboRenglones() {
        if (fondo == null) {
            return null;
        }
        try {
            listadoRol = new LinkedList<>();
            Codigos codigo = codigosBean.traerCodigo("GASTGEN", "F");
            Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(codigo.getDescripcion());
            Codigos codigoRol = codigosBean.traerCodigo("TIPOROL", "ROL");
            Map parametros = new HashMap();
            parametros.put(";where", "o.cuenta=:cuenta and o.auxiliar=:auxiliar and o.cabecera.tipo.codigo=:codigo");
            parametros.put("cuenta", cuentaMovimiento.getCodigo());
            parametros.put("auxiliar", fondo.getEmpleado().getEntidad().getPin());
            parametros.put("codigo", codigoRol.getParametros());
            listadoRol = ejbRenglones.encontarParametros(parametros);
            return Combos.getSelectItems(listadoRol, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboKardexDesconocido() {
        if (fondo == null) {
            return null;
        }
        try {
            listadoPropioDesconocidos = new LinkedList<>();
            Codigos codigo = codigosBean.traerCodigo("CUENTASCXC", "DEPNOID");
            Map parametros = new HashMap();
            parametros.put(";where", "o.cuenta=:cuenta and o.valor < 0");
            parametros.put("cuenta", codigo.getParametros());
            List<Renglones> listado = ejbRenglones.encontarParametros(parametros);
            for (Renglones k : listado) {
                parametros = new HashMap();
                parametros.put(";where", "o.id=:id and o.estado = 2");
                parametros.put("id", k.getCabecera().getIdmodulo());
                List<Kardexbanco> lista = ejbKardexbanco.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    Kardexbanco kar = lista.get(0);
                    parametros = new HashMap();
                    parametros.put(";where", "o.kardexliquidacion=:kardexliquidacion");
                    parametros.put("kardexliquidacion", kar);
                    List<Fondos> listaFondoKardex = ejbFondos.encontarParametros(parametros);
                    List<Depositos> listaDepositoKardex = ejbDepositos.encontarParametros(parametros);
                    List<Viaticosempleado> listaViaticoKardex = ejbViaticosempleado.encontarParametros(parametros);
                    //En cajas chicas se crea una nueva caja y en el kardex esta el de liquidacion
                    parametros = new HashMap();
                    parametros.put(";where", "o.kardexbanco=:kardexbanco and o.kardexbanco.origen='MOVIMIENTOS BANCO'");
                    parametros.put("kardexbanco", kar);
                    List<Cajas> listaCajaKardex = ejbCajas.encontarParametros(parametros);
                    if (listaDepositoKardex.isEmpty()) {
                        if (listaFondoKardex.isEmpty()) {
                            if (listaViaticoKardex.isEmpty()) {
                                if (listaCajaKardex.isEmpty()) {
                                    listadoPropioDesconocidos.add(kar);
                                }
                            }
                        }
                    }
                }
            }
            return Combos.getSelectItems(listadoPropioDesconocidos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the kardexDeposito
     */
    public Kardexbanco getKardexDeposito() {
        return kardexDeposito;
    }

    /**
     * @param kardexDeposito the kardexDeposito to set
     */
    public void setKardexDeposito(Kardexbanco kardexDeposito) {
        this.kardexDeposito = kardexDeposito;
    }

    /**
     * @return the gastoFondo
     */
    public Cuentas getGastoFondo() {
        return gastoFondo;
    }

    /**
     * @param gastoFondo the gastoFondo to set
     */
    public void setGastoFondo(Cuentas gastoFondo) {
        this.gastoFondo = gastoFondo;
    }

    /**
     * @return the listaReglonesGasto
     */
    public List<Renglones> getListaReglonesGasto() {
        return listaReglonesGasto;
    }

    /**
     * @param listaReglonesGasto the listaReglonesGasto to set
     */
    public void setListaReglonesGasto(List<Renglones> listaReglonesGasto) {
        this.listaReglonesGasto = listaReglonesGasto;
    }

    public String imprimirGasto() {
        try {
            fechaImp = null;
            List<Renglones> renglonesGasto = traerRenglonesGasto();
            List<Renglones> renglonesLiquidacion = traerRenglonesLiquidacion();
            List<Renglones> renglonesReclasificacion = traerRenglonesReclasificacion();
            List<Renglones> listadoDeposito = new LinkedList<>();
            List<Renglones> listadoDepositoVarios = traerRenglonesDepositoVarios(fondo);
            List<Renglones> listadoRenglonesVarios = traerRenglonesVarios(fondo);
            if (fondo.getKardexliquidacion() != null) {
                listadoDeposito = traerRenglonesDeposito(fondo);
            }

            List<Valesfondos> listaVale = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo and o.estado=6");
            parametros.put("fondo", fondo);
            listaVale = ejbVales.encontarParametros(parametros);
            Valesfondos vc = null;
            if (!listaVale.isEmpty()) {
                vc = listaVale.get(0);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("LIQUIDACIÓN DE FONDO NO : " + fondo.getId());
            pdf.agregaParrafo("\n");

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor Asignado :   " + df.format(fondo.getValor())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Beneficiario :  " + fondo.getEmpleado().toString()));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha:   " + (sdf.format(fondo.getFecha()))));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha:   " + (sdf.format(fechaImp != null ? fechaImp : fondo.getFecha()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaParrafo("\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();

            if (vc != null) {
                if (vc.getReposiscion() != null) {
                    if (vc.getReposiscion().getBase() == null) {
                        vc.getReposiscion().setBase(BigDecimal.ZERO);
                    }
                    if (vc.getReposiscion().getBase0() == null) {
                        vc.getReposiscion().setBase0(BigDecimal.ZERO);
                    }
                    if (vc.getReposiscion().getIva() == null) {
                        vc.getReposiscion().setIva(BigDecimal.ZERO);
                    }
                    double subtotal = vc.getReposiscion().getBase().doubleValue();
                    double subtotal0 = vc.getReposiscion().getBase0().doubleValue();
                    double total = subtotal + vc.getReposiscion().getIva().doubleValue() + subtotal0;

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo de Documento"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposiscion().getDocumento().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número de Documento"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposiscion().getNumerodocumento().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Subtotal 12"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, subtotal));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Subtotal 0"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, subtotal0));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Iva"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposiscion().getIva().doubleValue()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Valor"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, total));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(vc.getReposiscion().getFecha())));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, fondo.getEmpleado().toString()));

                    pdf.agregarTabla(null, columnas, 2, 100, null);
                    pdf.agregaParrafo("Concepto : " + vc.getReposiscion().getDescripcion() + "\n\n");
                }
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            Cabeceras c = new Cabeceras();
            String obs = "";
            //Gasto
            if (renglonesGasto != null) {
                if (!renglonesGasto.isEmpty()) {
                    for (Renglones r : renglonesGasto) {
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
                        obs = r.getCabecera().getDescripcion();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION GASTO N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }

            //Reclasificacion
            if (renglonesReclasificacion != null) {
                if (!renglonesReclasificacion.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesReclasificacion) {
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
                        obs = r.getCabecera().getDescripcion();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION RECLASIFICACION N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            // Liquidacion
            if (renglonesLiquidacion != null) {
                if (!renglonesReclasificacion.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : renglonesLiquidacion) {
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
                        obs = r.getCabecera().getDescripcion();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "LIQUIDACION N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Depositos
            if (listadoDeposito != null) {
                if (!listadoDeposito.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listadoDeposito) {
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITO  N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Varios Depositos
            if (listadoDepositoVarios != null) {
                if (!listadoDepositoVarios.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listadoDepositoVarios) {
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITOS N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Descuento por Rol
            if (fondo.getRenglonliquidacion() != null) {
                titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(fondo.getRenglonliquidacion().getCabecera().getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, fondo.getRenglonliquidacion().getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(fondo.getRenglonliquidacion().getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, fondo.getRenglonliquidacion().getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, fondo.getRenglonliquidacion().getAuxiliar()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(fondo.getRenglonliquidacion().getValor())));

                c = fondo.getRenglonliquidacion().getCabecera();
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DESCUENTO POR ROL N° " + c.getNumero());
                pdf.agregaParrafo("\n");
            }
            //Varios Descuentos
            if (listadoRenglonesVarios != null) {
                if (!listadoRenglonesVarios.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listadoRenglonesVarios) {
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DESCUENTOS N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Reembolsos
            pdf.agregaParrafo("\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Documento"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Numero Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base 0"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Iva"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas = new LinkedList<>();
            double base = 0.0;
            double base0 = 0.0;
            double iva = 0.0;
            double total = 0.0;
            for (Valesfondos v : listaVale) {
                if (v.getReposiscion() != null) {
                    if (!v.getTipodocumento().getCodigo().equals("OTR")) {
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getProveedor() != null ? (v.getProveedor().getEmpresa() != null ? v.getProveedor().getEmpresa().getRuc() : "") : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getTipodocumento().getNombre()));
                        columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getEstablecimiento() + v.getPuntoemision() + v.getNumero()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getBaseimponible().doubleValue()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getBaseimponiblecero().doubleValue()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getIva().doubleValue()));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getBaseimponible().doubleValue() + v.getBaseimponiblecero().doubleValue() + v.getIva().doubleValue()));
                        base += v.getBaseimponible().doubleValue();
                        base0 += v.getBaseimponiblecero().doubleValue();
                        iva += v.getIva().doubleValue();
                    }
                }
            }
            total = base + base0 + iva;
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, base));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, base0));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, iva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, total));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "REEMBOLSO");
            pdf.agregaParrafo("\n");
            //Reembolsos otros documentos
            pdf.agregaParrafo("\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Documento"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Numero Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Base 0"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Iva"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas = new LinkedList<>();
            base = 0.0;
            base0 = 0.0;
            iva = 0.0;
            total = 0.0;
            for (Valesfondos v : listaVale) {
                if (v.getTipodocumento().getCodigo().equals("OTR")) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getProveedor() != null ? (v.getProveedor().getEmpresa() != null ? v.getProveedor().getEmpresa().getRuc() : "") : ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getTipodocumento().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getEstablecimiento() + v.getPuntoemision() + v.getNumero()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getBaseimponible().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getBaseimponiblecero().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getIva().doubleValue()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, v.getBaseimponible().doubleValue() + v.getBaseimponiblecero().doubleValue() + v.getIva().doubleValue()));
                    base += v.getBaseimponible().doubleValue();
                    base0 += v.getBaseimponiblecero().doubleValue();
                    iva += v.getIva().doubleValue();
                }
            }
            total = base + base0 + iva;
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, base));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, base0));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, iva));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, total));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "OTROS DOCUMENTOS");
            pdf.agregaParrafo("\n");
            //AFECTACION PRESUPUESTARIA
            sumaDebitos = 0;
            sumaCreditos = 0;
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            double totalCompromiso = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.vale.fondo=:vale");
            parametros.put("vale", fondo);
            List<Detallesfondo> listaDetalleFondos = ejbDetallesvale.encontarParametros(parametros);
            for (Detallesfondo d : listaDetalleFondos) {
                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getDetallecompromiso().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                totalCompromiso += d.getValor().doubleValue();
            }

            columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL "));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, totalCompromiso));
            pdf.agregarTablaReporte(titulos, columnas, 2, 100, "AFECTACION PRESUPUESTARIA");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("Observaciones: " + obs);
            pdf.agregaParrafo("\n\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);

            //Formulario
            parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo and o.reposiscion.documento.codigo='LIQCOM'");
            parametros.put("fondo", fondo);
            List<Valesfondos> listaValeLiq = ejbVales.encontarParametros(parametros);
            if (!listaValeLiq.isEmpty()) {
                grabarEnHoja(fondo);
            }

            //Compromiso
            String texto2 = "";
            parametros = new HashMap();
            parametros.put(";where", "o.vale.fondo=:fondo");
            parametros.put("fondo", fondo);
            List<Detallesfondo> listaDetalles = ejbDetallesvale.encontarParametros(parametros);
            if (!listaDetalles.isEmpty()) {
                Detallesfondo dv = listaDetalles.get(0);
                if (dv.getDetallecompromiso() != null) {
                    Detallecompromiso dc = dv.getDetallecompromiso();
                    texto2 = "Compromiso : " + dc.getCompromiso().getNumerocomp() + " [" + dc.getAsignacion().getProyecto().getCodigo() + " - " + dc.getAsignacion().getClasificador().getCodigo() + "]";

                    pdf.agregaParrafo(texto2);
                    pdf.agregaParrafo("\n\n");
                }
                imprimirCompromiso(listaDetalles);
            }
            setReporte(pdf.traerRecurso());

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.insertar();
        return null;
    }

    public String grabarEnHoja(Fondos fon) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo and o.reposiscion.documento.codigo='LIQCOM'");
            parametros.put("fondo", fon);
            List<Valesfondos> listaValeLiq = ejbVales.encontarParametros(parametros);
            if (!listaValeLiq.isEmpty()) {
                LiquidaciondeCompras hoja = new LiquidaciondeCompras();
                hoja.llenarFondo(listaValeLiq);
                reporteComp = hoja.traerRecurso();
            }

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirCompromiso(List<Detallesfondo> listaDetalles) {
        Compromisos comp = null;
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        SimpleDateFormat anio = new SimpleDateFormat("yyyy");
        if (!listaDetalles.isEmpty()) {
            Detallesfondo dv = listaDetalles.get(0);
            if (dv.getDetallecompromiso() == null) {
                MensajesErrores.advertencia("No existe detalle de compromiso");;
                return null;
            }
            comp = dv.getDetallecompromiso().getCompromiso();
        }
        if (comp != null) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.compromiso=:compromiso");
                parametros.put("compromiso", comp);
                List<Detallecompromiso> detallees = ejbDetallecompromiso.encontarParametros(parametros);
                String direccion = "";
                for (Detallecompromiso d : detallees) {
                    if (d.getCompromiso() != null) {
                        if (d.getCompromiso().getCertificacion() != null) {
                            if (d.getCompromiso().getCertificacion().getDireccion() != null) {
                                direccion = d.getCompromiso().getCertificacion().getDireccion().toString();
                            }
                        }

                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
                DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                        null, parametrosSeguridad.getLogueado().getUserid());
                pdf.agregaTitulo("INFORME DE COMPROMISO\n");
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Compromiso :"));
//            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getId().toString()));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, comp.getNumerocomp() + ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf1.format(comp.getFecha()).toUpperCase()));
                pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio.format(comp.getFecha())
                        + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
                pdf.agregarTabla(null, columnas, 2, 100, null);
                pdf.agregaTitulo("\n");
                String proveedor = "";
                String contrato = "";
                if (comp.getProveedor() != null) {
                    proveedor = comp.getProveedor().getEmpresa().toString();
                    if (comp.getContrato() != null) {
                        contrato = comp.getContrato().toString();
                    }
                } else if (comp.getBeneficiario() != null) {
                    proveedor = comp.getBeneficiario().getEntidad().toString();

                }
                List<AuxiliarReporte> titulosReporte = new LinkedList<>();
                int totalCol = 6;
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBL"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
                titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
                titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                columnas = new LinkedList<>();
                double total = 0;
                for (Detallecompromiso d : detallees) {
                    String que = "";
                    for (Auxiliar a : titulos) {
                        que += a.getTitulo1() + " : ";
                        que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                    }
                    que += " Programa : " + d.getAsignacion().getProyecto().toString();
                    d.setArbolProyectos(que);
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(d.getFecha())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getNombre()));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                    total += d.getValor().doubleValue();
                }

                DecimalFormat df = new DecimalFormat("###,##0.00");
                pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
                pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + contrato + " " + comp.getMotivo());
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\nEl Monto del compromiso asciende a: " + df.format(total) + "\n");
                pdf.agregaParrafo("\nTotal compromiso: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
                pdf.agregaParrafo("\n\n");
//            FM14SEP2018
                if (comp.getCertificacion() != null) {
                    if (comp.getCertificacion().getNumerocert() != null) {
                        pdf.agregaParrafo("\nCertificación Nro: " + comp.getCertificacion().getNumerocert() + " - " + comp.getCertificacion().getMotivo() + "\n");
                        pdf.agregaParrafo("\n\n");
                    }
                }
//            FM14SEP2018
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista de Presupuesto"));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Jefe de Presupuesto/Director Financiero"));

                pdf.agregarTabla(null, columnas, 2, 100, null);
                reporteCompromiso = pdf.traerRecurso();
            } catch (IOException | DocumentException | ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesGasto() {
        if (fondo != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion "
                    + " in ('GASTO_FONDOS')");
            parametros.put("id", fondo.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                List<Renglones> rl = ejbRenglones.encontarParametros(parametros);
                if(!rl.isEmpty()){
                    fechaImp = rl.get(0).getFecha();
                }
                return rl;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesLiquidacion() {
        if (fondo != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion "
                    + " in ('LIQUIDACION_FONDOS')");
            parametros.put("id", fondo.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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
        if (fondo != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion "
                    + " in ('GASTO_FONDOS_RECLASIFICACION')");
            parametros.put("id", fondo.getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
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

    private List<Renglones> traerRenglonesDeposito(Fondos ve) {
        if (ve != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('KARDEX BANCOS')");
            parametros.put("id", ve.getKardexliquidacion().getId());
            parametros.put(";orden", "o.cuenta desc,o.valor");
            try {
                List<Renglones> rl = ejbRenglones.encontarParametros(parametros);

                return rl;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesDepositoVarios(Fondos ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo");
            parametros.put("fondo", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);

                for (Depositos d : rl) {
                    if (d.getKardexliquidacion() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('KARDEX BANCOS')");
                        parametros.put("id", d.getKardexliquidacion().getId());
                        parametros.put(";orden", "o.cuenta desc,o.valor");
                        rl2 = ejbRenglones.encontarParametros(parametros);
                        for (Renglones r : rl2) {
                            rl3.add(r);
                        }
                    }
                }

                return rl3;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesVarios(Fondos ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.fondo=:fondo");
            parametros.put("fondo", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);

                for (Depositos d : rl) {
                    if (d.getRenglonliquidacion() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.id=:id");
                        parametros.put("id", d.getRenglonliquidacion().getId());
                        parametros.put(";orden", "o.cuenta desc,o.valor");
                        rl2 = ejbRenglones.encontarParametros(parametros);
                        for (Renglones r : rl2) {
                            rl3.add(r);
                        }
                    }
                }

                return rl3;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * @return the renglonesAuxiliar
     */
    public List<AuxiliarCarga> getRenglonesAuxiliar() {
        return renglonesAuxiliar;
    }

    /**
     * @param renglonesAuxiliar the renglonesAuxiliar to set
     */
    public void setRenglonesAuxiliar(List<AuxiliarCarga> renglonesAuxiliar) {
        this.renglonesAuxiliar = renglonesAuxiliar;
    }

    /**
     * @return the renglonesRetencion
     */
    public List<AuxiliarCarga> getRenglonesRetencion() {
        return renglonesRetencion;
    }

    /**
     * @param renglonesRetencion the renglonesRetencion to set
     */
    public void setRenglonesRetencion(List<AuxiliarCarga> renglonesRetencion) {
        this.renglonesRetencion = renglonesRetencion;
    }

    /**
     * @return the parcial
     */
    public boolean isParcial() {
        return parcial;
    }

    /**
     * @param parcial the parcial to set
     */
    public void setParcial(boolean parcial) {
        this.parcial = parcial;
    }

    /**
     * @return the autorizacion2
     */
    public Autorizaciones getAutorizacion2() {
        return autorizacion2;
    }

    /**
     * @param autorizacion2 the autorizacion2 to set
     */
    public void setAutorizacion2(Autorizaciones autorizacion2) {
        this.autorizacion2 = autorizacion2;
    }

    /**
     * @return the autorizacionProveedor
     */
    public Autorizaciones getAutorizacionProveedor() {
        return autorizacionProveedor;
    }

    /**
     * @param autorizacionProveedor the autorizacionProveedor to set
     */
    public void setAutorizacionProveedor(Autorizaciones autorizacionProveedor) {
        this.autorizacionProveedor = autorizacionProveedor;
    }

    /**
     * @return the reposicion
     */
    public Reposisciones getReposicion() {
        return reposicion;
    }

    /**
     * @param reposicion the reposicion to set
     */
    public void setReposicion(Reposisciones reposicion) {
        this.reposicion = reposicion;
    }

    /**
     * @return the impuesto
     */
    public Impuestos getImpuesto() {
        return impuesto;
    }

    /**
     * @param impuesto the impuesto to set
     */
    public void setImpuesto(Impuestos impuesto) {
        this.impuesto = impuesto;
    }

    /**
     * @return the puntoemision
     */
    public Puntoemision getPuntoemision() {
        return puntoemision;
    }

    /**
     * @param puntoemision the puntoemision to set
     */
    public void setPuntoemision(Puntoemision puntoemision) {
        this.puntoemision = puntoemision;
    }

    /**
     * @return the sucursal
     */
    public Sucursales getSucursal() {
        return sucursal;
    }

    /**
     * @param sucursal the sucursal to set
     */
    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * @return the reporteCompromiso
     */
    public Resource getReporteCompromiso() {
        return reporteCompromiso;
    }

    /**
     * @param reporteCompromiso the reporteCompromiso to set
     */
    public void setReporteCompromiso(Resource reporteCompromiso) {
        this.reporteCompromiso = reporteCompromiso;
    }

    /**
     * @return the reporteComp
     */
    public Resource getReporteComp() {
        return reporteComp;
    }

    /**
     * @param reporteComp the reporteComp to set
     */
    public void setReporteComp(Resource reporteComp) {
        this.reporteComp = reporteComp;
    }

    /**
     * @return the proyectoBean
     */
    public ProyectosBean getProyectoBean() {
        return proyectoBean;
    }

    /**
     * @param proyectoBean the proyectoBean to set
     */
    public void setProyectoBean(ProyectosBean proyectoBean) {
        this.proyectoBean = proyectoBean;
    }

    /**
     * @return the gastoReclasificacion
     */
    public Cuentas getGastoReclasificacion() {
        return gastoReclasificacion;
    }

    /**
     * @param gastoReclasificacion the gastoReclasificacion to set
     */
    public void setGastoReclasificacion(Cuentas gastoReclasificacion) {
        this.gastoReclasificacion = gastoReclasificacion;
    }

    /**
     * @return the valorAnterior
     */
    public double getValorAnterior() {
        return valorAnterior;
    }

    /**
     * @param valorAnterior the valorAnterior to set
     */
    public void setValorAnterior(double valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    /**
     * @return the formularioAuto
     */
    public Formulario getFormularioAuto() {
        return formularioAuto;
    }

    /**
     * @param formularioAuto the formularioAuto to set
     */
    public void setFormularioAuto(Formulario formularioAuto) {
        this.formularioAuto = formularioAuto;
    }

    /**
     * @return the RenglonDescuento
     */
    public Renglones getRenglonDescuento() {
        return RenglonDescuento;
    }

    /**
     * @param RenglonDescuento the RenglonDescuento to set
     */
    public void setRenglonDescuento(Renglones RenglonDescuento) {
        this.RenglonDescuento = RenglonDescuento;
    }

    /**
     * @return the masDepositos
     */
    public boolean isMasDepositos() {
        return masDepositos;
    }

    /**
     * @param masDepositos the masDepositos to set
     */
    public void setMasDepositos(boolean masDepositos) {
        this.masDepositos = masDepositos;
    }

    /**
     * @return the masDescuentos
     */
    public boolean isMasDescuentos() {
        return masDescuentos;
    }

    /**
     * @param masDescuentos the masDescuentos to set
     */
    public void setMasDescuentos(boolean masDescuentos) {
        this.masDescuentos = masDescuentos;
    }

    /**
     * @return the listadoRol
     */
    public List<Renglones> getListadoRol() {
        return listadoRol;
    }

    /**
     * @param listadoRol the listadoRol to set
     */
    public void setListadoRol(List<Renglones> listadoRol) {
        this.listadoRol = listadoRol;
    }

    /**
     * @return the listadoPropio
     */
    public List<Kardexbanco> getListadoPropio() {
        return listadoPropio;
    }

    /**
     * @param listadoPropio the listadoPropio to set
     */
    public void setListadoPropio(List<Kardexbanco> listadoPropio) {
        this.listadoPropio = listadoPropio;
    }

    /**
     * @return the masDepositosDesconocidos
     */
    public boolean isMasDepositosDesconocidos() {
        return masDepositosDesconocidos;
    }

    /**
     * @param masDepositosDesconocidos the masDepositosDesconocidos to set
     */
    public void setMasDepositosDesconocidos(boolean masDepositosDesconocidos) {
        this.masDepositosDesconocidos = masDepositosDesconocidos;
    }

    /**
     * @return the listadoPropioDesconocidos
     */
    public List<Kardexbanco> getListadoPropioDesconocidos() {
        return listadoPropioDesconocidos;
    }

    /**
     * @param listadoPropioDesconocidos the listadoPropioDesconocidos to set
     */
    public void setListadoPropioDesconocidos(List<Kardexbanco> listadoPropioDesconocidos) {
        this.listadoPropioDesconocidos = listadoPropioDesconocidos;
    }

    /**
     * @return the kardexDepositoDesconocido
     */
    public Kardexbanco getKardexDepositoDesconocido() {
        return kardexDepositoDesconocido;
    }

    /**
     * @param kardexDepositoDesconocido the kardexDepositoDesconocido to set
     */
    public void setKardexDepositoDesconocido(Kardexbanco kardexDepositoDesconocido) {
        this.kardexDepositoDesconocido = kardexDepositoDesconocido;
    }

    /**
     * @return the fechaImp
     */
    public Date getFechaImp() {
        return fechaImp;
    }

    /**
     * @param fechaImp the fechaImp to set
     */
    public void setFechaImp(Date fechaImp) {
        this.fechaImp = fechaImp;
    }
}
