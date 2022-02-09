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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AperReposLiquiCaja;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DepositosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Depositos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallesvales;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Valescajas;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.KardexBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "liquidacionCajasSfccbdmq")
@ViewScoped
public class LiquidacionCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public LiquidacionCajasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Cajas caja;
    private List<Cajas> listaCajas;
    private Empleados empleado;
    private Organigrama direccion;
    private Resource reporte;
    private Resource reporteLiquidacion;
    private Resource reporteARL;
    private List<Renglones> listaReglones;
    private List<Renglones> listadoKardex;
    private List<Detallecompromiso> listaDetallesC;
    private List<Kardexbanco> listadoPropio;
    private List<Kardexbanco> listadoPropioDesconocidos;
    private List<Renglones> listadoRol;
    private Cabeceras cabecera;
    private Kardexbanco kardex;
    private Kardexbanco kardexDeposito;
    private double reponer;
    private Resource reporteLiq;
    private int numeroLiq;
    private Codigos cod;
    private int anio;
    private int completo;
    private Renglones RenglonDescuento;
    private boolean masDepositos = false;
    private boolean masDepositosDesconocidos = false;
    private boolean masDescuentos = false;
    private Kardexbanco kardexDepositoDesconocido;
    @EJB
    private CajasFacade ejbCajas;
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
    private ValescajasFacade ejbVales;
    @EJB
    private DetallesvalesFacade ejbDetallesvale;
    @EJB
    private RetencionescomprasFacade ejbReten;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecertificacionesFacade ejbDetCert;
    @EJB
    private FondosFacade ejbFondos;
    @EJB
    private DepositosFacade ejbDepositos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
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
        String nombreForma = "LiquidacionCajasVista";
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
        if (caja == null) {
            MensajesErrores.advertencia("Seleccione una caja");
            return null;
        }
        reponer = 0;
        kardex = new Kardexbanco();
        kardex.setFechamov(new Date());
//        kardex.setValor(caja.getValor());
        numeroLiq = 0;
        try {
            cod = ejbCodigos.traerCodigo("NUM", "08");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para apertura");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        int numero = Integer.parseInt(cod.getParametros());
        int num = numero + 1;
        if (caja.getNumeroliquidacion() == null) {
            numeroLiq = num;
        } else {
            if (caja.getNumeroliquidacion() == 0) {
                numeroLiq = num;
            } else {
                numeroLiq = caja.getNumeroliquidacion();
            }
        }

        try {

            Map parametros = new HashMap();
            parametros.put(";where", "o.apertura=:caja");
            parametros.put("caja", caja);
            parametros.put(";orden", "o.id desc");
            List<Cajas> listaUltimaCaja = ejbCajas.encontarParametros(parametros);

            Cajas ultimaCaja = null;
            if (!listaUltimaCaja.isEmpty()) {
                ultimaCaja = listaUltimaCaja.get(0);
            } else {
                ultimaCaja = caja;
            }
            parametros = new HashMap();
            parametros.put(";campos", "o.baseimponiblecero + o.baseimponible + o.iva");
            parametros.put(";where", "o.caja=:caja and o.caja.kardexbanco is null");
            parametros.put("caja", ultimaCaja);
            reponer = ejbVales.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja and o.caja.kardexbanco is null");
            parametros.put("caja", ultimaCaja);
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);
            double vlorRet = 0;
            for (Valescajas v : lista) {
                parametros = new HashMap();
                parametros.put(";campos", "o.valor + o.valoriva");
                parametros.put(";where", "o.obligacion=:caja");
                parametros.put("caja", v.getObligacion());
                double valorRet = ejbReten.sumarCampo(parametros).doubleValue();
                vlorRet += valorRet;
            }
            // sumar retenciones

            reponer -= vlorRet;
        } catch (ConsultarException ex) {
            Logger.getLogger(LiquidacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (reponer != 0) {
            double totalKardex = caja.getValor().doubleValue() - reponer;
            kardex.setValor(new BigDecimal(totalKardex));
        } else {
            kardex.setValor(BigDecimal.ZERO);
        }
        listaReglones = null;
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
            Codigos cajaChica = ejbCodigos.traerCodigo("GASTGEN", "C");
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto.codigo like :codigo and o.asignacion.proyecto.anio=:anio");
            parametros.put("codigo", cajaChica.getParametros() + "%");
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

    public String contabilizar(boolean definitivo, Cajas caj) {
        if (getKardex() == null) {
            return null;
        }
        if (caja == null) {
            return null;
        }
//        if (reponer == 0) {
//            MensajesErrores.advertencia("Caja Liquidada sin nada que reponer");
//            return null;
//        }
//
//        if (kardex.getTipomov() == null) {
//            return null;
//        }
//        if (kardex.getValor() == null) {
//        kardex.setValor(BigDecimal.ZERO);
//        }
        kardex.setValor(BigDecimal.ZERO);
        Cuentas cuentaBanco = null;
//        if (kardex.getValor().doubleValue() != 0) {
        if (reponer == 0) {
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
        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "C");
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
        if (getKardexDeposito() != null) {
            if (kardexDeposito.getTipomov().getIngreso()) {
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
            parametros.put(";where", "o.rubro=5 and o.nombre like 'CAJAS CHICAS%'");
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
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            listaReglones = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
//            String partida = dc.getAsignacion().getClasificador().getCodigo().substring(0, 2);
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
                cab.setTipo(ta);
                cab.setNumero(numeroAsiento);
                cab.setFecha(kardex.getFechamov());
//                cab.setIdmodulo(kardexDeposito.getId());
                cab.setIdmodulo(caj.getId());
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
                    r1.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
                }
                r1.setPresupuesto(cuentaBanco.getPresupuesto());
            }

            Renglones r0 = new Renglones(); // reglon de empleado
            if (definitivo) {
                r0.setCabecera(cab);
            }
//            if (kardex.getValor() == null) {
//                kardex.setValor(BigDecimal.ZERO);
//            }
            double valorF = 0;
            if (kardex.getValor().doubleValue() != 0) {
//            if (reponer == 0) {
                valorF = caja.getValor().doubleValue() - kardexDeposito.getValor().doubleValue();
            } else {
                valorF = reponer;
            }
            r0.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
            r0.setReferencia(caja.getReferencia() + " R0");
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
//            if (reponer == 0) {
                valorF = caja.getValor().doubleValue() - kardexDeposito.getValor().doubleValue();
            } else {
                valorF = reponer;
            }
            r.setReferencia(kardex.getObservaciones() + " R");
            r.setValor(new BigDecimal(valorF * productoTipo));
            r.setCuenta(cuentaMovimiento.getCodigo());
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            r.setFecha(kardex.getFechamov());
            if (cuentaMovimiento.getAuxiliares() != null) {
                r.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
            }
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
//            if (definitivo) {
//                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
//                ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
//                ejbKardexbanco.edit(kardex, parametrosSeguridad.getLogueado().getUserid());
//            }

            // los dos renglones extras que pide Juan carlos
//            buscar lo quidado
            parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
            parametros.put("estado", 4);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            List<Valescajas> listaVales = ejbVales.encontarParametros(parametros);
            double valor = 0;
            for (Valescajas v : listaVales) {
                valor += v.getValor().doubleValue();
                if (definitivo) {
                    double cuadrado = 0;
//                    if (kardex.getValor().doubleValue() != 0) {
                    if (reponer == 0) {
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
                    r2.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
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
                    r3.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
                }
                r3.setPresupuesto(cuentaEmpleado.getPresupuesto());
                listaReglones.add(r2);
                listaReglones.add(r3);
                if (definitivo) {
                    ejbRenglones.create(r2, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(r3, parametrosSeguridad.getLogueado().getUserid());
                }
            }
//            if (kardex.getValor().doubleValue() != 0) {
            if (r1.getValor() != null) {
                if (r1.getValor().doubleValue() != 0) {
                    listaReglones.add(r1);
                }
            }
            if (r0.getValor().doubleValue() != 0) {
                listaReglones.add(r0);
            }
            if (r.getValor().doubleValue() != 0) {
                listaReglones.add(r);
            }
            if (definitivo) {
//                List<Detallesvales> lisaDetalles = traerDetallesVale();
//                Certificaciones certificacion = null;
//                for (Detallesvales dv : lisaDetalles) {
//                    certificacion = dv.getDetallecertificacion().getCertificacion();
//                }
//                Compromisos c = new Compromisos();
//                c.setFecha(caja.getFecha());
//                c.setContrato(null);
//                c.setImpresion(caja.getFecha());
//                c.setMotivo(caja.getObservaciones());
//                c.setProveedor(null);
//                c.setCertificacion(certificacion);
//                c.setEmpleado(caja.getEmpleado());
//                c.setDireccion(caja.getDepartamento());
//                ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
//                for (Detallesvales dv : lisaDetalles) {
////            CREACION DE DETALLES COMPROMISOS
//                    Detallecompromiso dc = new Detallecompromiso();
//                    dc.setCompromiso(c);
//                    dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
//                    dc.setMotivo(caja.getObservaciones());
//                    dc.setFecha(caja.getFecha());
//                    dc.setValor(dv.getValor());
//                    dc.setSaldo(new BigDecimal(BigInteger.ZERO));
//                    ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
////                ACTUALIZA DETALLE DE VALE AGREGANDO EL DETALLE DE COMPROMISO
//                    dv.setDetallecompromiso(dc);
//                    ejbDetallesvale.edit(dv, parametrosSeguridad.getLogueado().getUserid());
//                }
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                ejbRenglones.create(r0, parametrosSeguridad.getLogueado().getUserid());
//                if (kardex.getValor().doubleValue() != 0) {
                if (r1.getValor() != null) {
                    if (r1.getValor().doubleValue() != 0) {
                        ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
//                ejbKardexbanco.edit(kardex, parametrosSeguridad.getLogueado().getUserid());
            }
//            formularioImpresion.cancelar();
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String contabilizarDesconocidos(double valorF, Cajas caj) {
        if (caj == null) {
            return null;
        }

        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "C");
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
            parametros.put(";where", "o.rubro=5 and o.nombre like 'CAJAS CHICAS%'");
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
            cabDesc.setIdmodulo(caj.getId());
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
                r.setAuxiliar(caj.getEmpleado().getEntidad().getPin());
            }
            ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
            ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<Detallesvales> traerDetallesVale() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.vale.caja=:caja and o.vale.estado=2");
        parametros.put("caja", caja);
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

        Empleados e = caja.getEmpleado();
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
            parametros.put(";where", "o.caja=:caja and o.estado!=5");
            parametros.put("caja", caja);
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("Existen vales de caja en proceso");
                return null;
            }
            if (numeroLiq == 0) {
                MensajesErrores.advertencia("Número de Solicitud de Liquidación es necesario");
                return null;
            }
//            DecimalFormat df = new DecimalFormat("0000");
//            completo = Integer.valueOf(String.valueOf(anio) + df.format(numeroLiq));
//            if (numeroLiq != 0) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.numeroapertura=:numeroreposicion or o.numeroreposicion=:numeroreposicion or o.numeroliquidacion=:numeroreposicion");
//                parametros.put("numeroreposicion", completo);
//                List<Cajas> listac = ejbCajas.encontarParametros(parametros);
//                if (!listac.isEmpty()) {
//                    MensajesErrores.advertencia("Número de Solicitud de Liquidación duplicado");
//                    return null;
//                }
//            }
//            caja.setNumeroliquidacion(completo);
            caja.setNumeroliquidacion(numeroLiq);
            // buscar si existe clasificador ya con esa fuente en ese proyecto
            caja.setLiquidado(Boolean.TRUE);
            kardex.setFechagraba(new Date());
//            kardex.setOrigen("LIQUIDACION CAJA CHICA");
//            ejbKardexbanco.create(kardex, parametrosSeguridad.getLogueado().getUserid());
            ejbCajas.edit(caja, parametrosSeguridad.getLogueado().getUserid());
            cod.setParametros(numeroLiq + "");
            ejbCodigos.edit(cod, parametrosSeguridad.getLogueado().getUserid());

            Cajas cajan = new Cajas();
            cajan.setApertura(caja);
            cajan.setLiquidado(Boolean.FALSE);
            cajan.setJefe(caja.getJefe());
            cajan.setDepartamento(caja.getDepartamento());
            cajan.setEmpleado(caja.getEmpleado());
            cajan.setValor(caja.getValor());
            cajan.setPrcvale(caja.getPrcvale());
            cajan.setFecha(kardex.getFechamov());
            cajan.setObservaciones(kardex.getObservaciones());
            cajan.setNumeroapertura(caja.getNumeroapertura() != null ? caja.getNumeroapertura() : 0);
            cajan.setNumeroreposicion(caja.getNumeroreposicion() != null ? caja.getNumeroreposicion() : 0);
            cajan.setNumeroliquidacion(caja.getNumeroliquidacion() != null ? caja.getNumeroliquidacion() : 0);
            ejbCajas.create(cajan, parametrosSeguridad.getLogueado().getUserid());

            double valor = 0;

            if (masDepositosDesconocidos) {
                for (Kardexbanco kb : listadoPropioDesconocidos) {
                    if (kb.getSeleccionado()) {
                        Depositos d = new Depositos();
                        d.setKardexliquidacion(kb);
                        d.setCaja(cajan);
                        ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                    }
                    valor += kb.getValor().doubleValue();
                }
                cajan.setValor(new BigDecimal(valor));

            } else {
                if (kardexDepositoDesconocido != null) {
                    cajan.setValor(kardexDepositoDesconocido.getValor());
                    cajan.setKardexbanco(kardexDepositoDesconocido);
                } else {
                    if (masDepositos) {
                        for (Kardexbanco kb : listadoPropio) {
                            if (kb.getSeleccionado()) {
                                Depositos d = new Depositos();
                                d.setKardexliquidacion(kb);
                                d.setCaja(cajan);
                                ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                            }
                            valor += kb.getValor().doubleValue();
                        }
                        cajan.setValor(new BigDecimal(valor));
                    } else {
                        if (kardexDeposito != null) {
                            cajan.setKardexbanco(kardexDeposito);
                            cajan.setValor(kardexDeposito.getValor());
                        } else {
                            if (masDescuentos) {
                                for (Renglones r : listadoRol) {
                                    if (r.getSeleccionado()) {
                                        Depositos d = new Depositos();
                                        d.setRenglonliquidacion(r);
                                        d.setCaja(cajan);
                                        ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                                    }
                                    valor += r.getValor().doubleValue();
                                }
                                cajan.setValor(new BigDecimal(valor));
                            } else {
                                if (RenglonDescuento != null) {
                                    cajan.setRenglonliquidacion(RenglonDescuento);
                                    cajan.setValor(RenglonDescuento.getValor());
                                }
                            }
                        }
                    }
                }
            }
            ejbCajas.edit(cajan, parametrosSeguridad.getLogueado().getUserid());
            contabilizar(true, cajan);
            imprimir(cajan);
            imprimirSolicitud(cajan);

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        caja = null;
        reponer = 0;
        formulario.cancelar();
        MensajesErrores.informacion("Registro grabado con éxito");
        formularioImpresion.insertar();
        return null;
    }

    public String imprimir(Cajas caj) {
        try {
            listaReglones = new LinkedList<>();
            listadoKardex = new LinkedList<>();
            if (kardexDeposito != null) {
                listadoKardex = traerKardex(kardexDeposito);
            }
//            listaReglones = traerLiquidacion(kardexDeposito);
            listaReglones = traerLiquidacion(caj);

            List<Renglones> listadoDepositoVarios = traerRenglonesDepositoVarios(caj);
            List<Renglones> listadoRenglonesVarios = traerRenglonesVarios(caj);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("LIQUIDACIÓN DE CAJA CHICA N° : " + caj.getId(), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario: "));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, caj.getEmpleado().toString()));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaParrafo("\n\n");

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            Cabeceras c = new Cabeceras();
            if (!listadoKardex.isEmpty()) {
                for (Renglones r : listadoKardex) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
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
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITO N° " + c.getNumero());
            }
            pdf.agregaParrafo("\n\n");
            sumaDebitos = 0.0;
            sumaCreditos = 0.0;
            if (!listaReglones.isEmpty()) {
                columnas = new LinkedList<>();
                for (Renglones r : listaReglones) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
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
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "LIQUIDACIÓN N° " + c.getNumero());
            }
            pdf.agregaParrafo("\n\n");
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
            if (caj.getRenglonliquidacion() != null) {
                titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(caj.getRenglonliquidacion().getCabecera().getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, caj.getRenglonliquidacion().getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(caj.getRenglonliquidacion().getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, caj.getRenglonliquidacion().getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, caj.getRenglonliquidacion().getAuxiliar()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(caj.getRenglonliquidacion().getValor())));

                c = caj.getRenglonliquidacion().getCabecera();
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
            pdf.agregaParrafo("\n\n");

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporteLiq(pdf.traerRecurso());
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerKardex(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='KARDEX BANCOS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    private List<Renglones> traerLiquidacion(Kardexbanco k) {
    private List<Renglones> traerLiquidacion(Cajas k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='LIQUIDACION_CAJAS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteLiquidacionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerRenglonesDepositoVarios(Cajas ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);
                if (!rl.isEmpty()) {
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
                }

                return rl3;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private List<Renglones> traerRenglonesVarios(Cajas ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);
                if (!rl.isEmpty()) {
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
                }

                return rl3;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String imprimirSolicitud(Cajas caja) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ",
                    null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("SOLUCITUD DE APERTURA, REPOSICIÓN O LIQUIDACIÓN DEL FONDO FIJO DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n\n");

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "FORMULARIO AF-1"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, " No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getNumeroliquidacion() != null ? caja.getNumeroliquidacion() + "" : ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Quito, " + sdf.format(caja.getFecha())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "De Estación:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Unidad Administrativa"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            pdf.agregarTabla(null, columnas, 2, 100, null);

            pdf.agregaTitulo("\n");

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "APERTURA "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "REPOSICIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO SOLICITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "LIQUIDAIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "X"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "MONTO DEPOSITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 9, false, df.format(kardexDeposito != null ? kardexDeposito.getValor() : 0)));
            pdf.agregarTabla(null, columnas, 4, 100, null);

            pdf.agregaTitulo("\n\n");
            pdf.agregaParrafoCompleto("OBSERVACIONES: " + caja.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 9, Boolean.FALSE);
            pdf.agregaTitulo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "RESPONSABLE DEL FONDO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 9, false, "JEFE INMEDIATO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre: " + caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Nombre: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cc: " + caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "cc: "));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteARL = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarEnHoja(Cajas caj) {
        try {
            if (caj.getNumeroliquidacion() == null) {
                caj.setNumeroliquidacion(0);
            }
            AperReposLiquiCaja hoja = new AperReposLiquiCaja();
            hoja.llenar(caj, 3, null, kardexDeposito.getValor().doubleValue());
//            reporteLiq = hoja.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the caja
     */
    public Cajas getCaja() {
        return caja;
    }

    /**
     * @param caja the caja to set
     */
    public void setCaja(Cajas caja) {
        this.caja = caja;
    }

    /**
     * @return the listaCajas
     */
    public List<Cajas> getListaCajas() {
        return listaCajas;
    }

    /**
     * @param listaCajas the listaCajas to set
     */
    public void setListaCajas(List<Cajas> listaCajas) {
        this.listaCajas = listaCajas;
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
        contabilizar(false, caja);
        return listaReglones;
    }

    public SelectItem[] getComboCajas() {
        try {
            Map parametros = new HashMap();
            String where = "  o.apertura is null and o.liquidado=false";
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            listaCajas = ejbCajas.encontarParametros(parametros);
            return Combos.getSelectItems(listaCajas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String imprimir(Kardexbanco kardex) {
        this.kardex = kardex;
        traerRenglones();
        for (Renglones r : listaReglones) {
            Cuentas c = getCuentasBean().traerCodigo(r.getCuenta());
            r.setNombre(c.getNombre());
            double valor = r.getValor().doubleValue();
            if (valor > 0) {
                r.setDebitos(valor);
            } else {
                r.setCreditos(valor * -1);
            }
            if (c.getAuxiliares() != null) {

                VistaAuxiliares v = parametrosSeguridad.traerAuxiliar(r.getAuxiliar());
                r.setAuxiliarNombre(v == null ? "" : v.getNombre());
            }
        }
        Collections.sort(listaReglones, new valorComparator());
        Map parametros = new HashMap();
        parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
        parametros.put("empresa", kardex.getBeneficiario());
        parametros.put("nombrelogo", "Comprobante de " + kardex.getTipomov().getDescripcion() + " No : " + kardex.getId());
        parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
        parametros.put("fecha", kardex.getFechamov());
        if (kardex.getEgreso() == null) {
            parametros.put("documento", kardex.getDocumento());
        } else {
            parametros.put("documento", kardex.getEgreso().toString());
        }

        parametros.put("modulo", getValorStr());
        parametros.put("descripcion", kardex.getObservaciones());
        parametros.put("obligaciones", getValorStr());
        String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
        parametros.put("SUBREPORT_DIR", realPath + "/");
        Calendar c = Calendar.getInstance();
        reporte = new Reportesds(parametros,
                FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
                listaReglones, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        formularioImpresion.editar();
        return null;
    }

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
        if (caja == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.tipomov.tipo='CAJA' and o.auxiliar=:auxiliar and o.estado=2";
            parametros.put(";where", where);
            parametros.put("auxiliar", caja.getEmpleado().getEntidad().getPin());
//            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            List<Kardexbanco> listado = ejbKardexbanco.encontarParametros(parametros);
            listadoPropio = new LinkedList<>();
            for (Kardexbanco k : listado) {
                parametros = new HashMap();
                where = "  o.kardexbanco=:kardex";
                parametros.put(";where", where);
                parametros.put("kardex", k);
                int total = ejbCajas.contar(parametros);
                if (total == 0) {
                    listadoPropio.add(k);
                }
            }
            return Combos.getSelectItems(listadoPropio, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboKardexDesconocido() {
        if (caja == null) {
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

    public SelectItem[] getComboRenglones() {
        if (caja == null) {
            return null;
        }
        try {
            listadoRol = new LinkedList<>();
            Codigos codigo = codigosBean.traerCodigo("GASTGEN", "C");
            Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(codigo.getDescripcion());
            Codigos codigoRol = codigosBean.traerCodigo("TIPOROL", "ROL");
            Map parametros = new HashMap();
            parametros.put(";where", "o.cuenta=:cuenta and o.auxiliar=:auxiliar and o.cabecera.tipo.codigo=:codigo");
            parametros.put("cuenta", cuentaMovimiento.getCodigo());
            parametros.put("auxiliar", caja.getEmpleado().getEntidad().getPin());
            parametros.put("codigo", codigoRol.getParametros());
            listadoRol = ejbRenglones.encontarParametros(parametros);
            return Combos.getSelectItems(listadoRol, true);
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
     * @return the reporteLiq
     */
    public Resource getReporteLiq() {
        return reporteLiq;
    }

    /**
     * @param reporteLiq the reporteLiq to set
     */
    public void setReporteLiq(Resource reporteLiq) {
        this.reporteLiq = reporteLiq;
    }

    /**
     * @return the numeroLiq
     */
    public int getNumeroLiq() {
        return numeroLiq;
    }

    /**
     * @param numeroLiq the numeroLiq to set
     */
    public void setNumeroLiq(int numeroLiq) {
        this.numeroLiq = numeroLiq;
    }

    /**
     * @return the cod
     */
    public Codigos getCod() {
        return cod;
    }

    /**
     * @param cod the cod to set
     */
    public void setCod(Codigos cod) {
        this.cod = cod;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the completo
     */
    public int getCompleto() {
        return completo;
    }

    /**
     * @param completo the completo to set
     */
    public void setCompleto(int completo) {
        this.completo = completo;
    }

    /**
     * @return the listadoKardex
     */
    public List<Renglones> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(List<Renglones> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    /**
     * @return the reporteLiquidacion
     */
    public Resource getReporteLiquidacion() {
        return reporteLiquidacion;
    }

    /**
     * @param reporteLiquidacion the reporteLiquidacion to set
     */
    public void setReporteLiquidacion(Resource reporteLiquidacion) {
        this.reporteLiquidacion = reporteLiquidacion;
    }

    /**
     * @return the reporteARL
     */
    public Resource getReporteARL() {
        return reporteARL;
    }

    /**
     * @param reporteARL the reporteARL to set
     */
    public void setReporteARL(Resource reporteARL) {
        this.reporteARL = reporteARL;
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
}
