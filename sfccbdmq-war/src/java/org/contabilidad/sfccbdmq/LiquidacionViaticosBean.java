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
import java.util.Collections;
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
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DepositosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ReposiscionesFacade;
import org.beans.sfccbdmq.SucursalesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
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
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Reposisciones;
import org.entidades.sfccbdmq.Sucursales;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.KardexBean;
import org.presupuestos.sfccbdmq.ViaticosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "liquidacionViaticosSfccbdmq")
@ViewScoped
public class LiquidacionViaticosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public LiquidacionViaticosBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private Formulario formularioAuto = new Formulario();
    private Formulario formularioRegresar = new Formulario();
    private Empleados empleado;
    private Organigrama direccion;
    private Cuentas gastoViaticos;
    private Resource reporte;
    private Resource reporteLiq;
    private Resource reporteComp;
    private List<Renglones> listaReglones;
    private List<Renglones> listaReglonesInversion;
    private List<Detallecompromiso> listaDetallesC;
    private List<Kardexbanco> listadoPropio;
    private List<Kardexbanco> listadoPropioDesconocidos;
    private List<Renglones> listadoRol;
    private Cabeceras cabecera;
    private Kardexbanco kardex;
    private Viaticosempleado viaticoEmpleado;
    private List<Detalleviaticos> listaDetalles = new LinkedList<>();
    private Detalleviaticos detalle;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private String obs;
    private Reposisciones reposicion;
    private Impuestos impuesto;
    private Puntoemision puntoemision;
    private Sucursales sucursal;
    private Autorizaciones autorizacion2;
    private Autorizaciones autorizacionProveedor;
    private boolean masDepositos = false;
    private boolean masDescuentos = false;
    private boolean masDepositosDesconocidos = false;
    private Kardexbanco kardexDeposito;
    private Kardexbanco kardexDepositoDesconocido;
    private Renglones RenglonDescuento;
    private Date fechaLiquidacion = new Date();
    @EJB
    private DetalleviaticosFacade ejbDetalleViatico;
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
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private DetallecertificacionesFacade ejbDetalleCertificacion;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ReposiscionesFacade ejbReposisciones;
    @EJB
    private EmpresasFacade ejbEmpresas;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private PuntoemisionFacade ejbPuntoemision;
    @EJB
    private SucursalesFacade ejbSucursales;
    @EJB
    private DepositosFacade ejbDepositos;
    @EJB
    private FondosFacade ejbFondos;
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
    @ManagedProperty(value = "#{viaticosSfccbdmq}")
    private ViaticosBean viaticosBean;

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
        String nombreForma = "LiquidacionViaticosVista";
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

//        if (viaticosBean.getTipoPartida() == null) {
//            MensajesErrores.advertencia("Indicar el tipo de viaje");
//            return null;
//        }
//        if (viaticosBean.getViatico() == null) {
//            MensajesErrores.advertencia("Indicar el viaje");
//            return null;
//        }
        listadoPropio = new LinkedList<>();
        listadoRol = new LinkedList<>();
        if (viaticoEmpleado == null) {
            MensajesErrores.advertencia("Seleccione un empleado");
            return null;
        }
        viaticoEmpleado.setFechaliquidacion(new Date());
        Map parametros = new HashMap();
        parametros.put(";where", "o.viaticoempleado=:empleado");
        parametros.put("empleado", viaticoEmpleado);
        parametros.put(";orden", "o.tipo desc, o.fecha desc");
        try {
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);

            kardex = new Kardexbanco();
            if (viaticoEmpleado.getKardex() == null) {
                MensajesErrores.advertencia("No existe desembolso del dinero");
                return null;
            }
//        kardex.setFechamov(new Date());
//        kardex.setValor(viaticoEmpleado.getValor());
            listaReglones = null;
            //liquidacion de compras
            reposicion = new Reposisciones();
            reposicion.setBase(BigDecimal.ZERO);
            reposicion.setBase0(BigDecimal.ZERO);
            reposicion.setIva(BigDecimal.ZERO);
            reposicion.setNumerodocumento(0);
            reposicion.setAjuste(0);
            reposicion.setFecha(new Date());

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return null;
    }

    public String contabilizar(boolean definitivo) {
        if (viaticoEmpleado == null) {
            return null;
        }
        if (kardex == null) {
            return null;
        }
        viaticoEmpleado.setFechaliquidacion(fechaLiquidacion);
        kardex.setFechamov(viaticoEmpleado.getFechaliquidacion());
        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "V");
        Cuentas cuentaViaticos = cuentasBean.traerCodigo(codigo.getDescripcion());
        cuentasBean.setCuenta(cuentaViaticos);
        if (cuentasBean.validaCuentaMovimiento()) {
//            MensajesErrores.advertencia("Cuenta contable de movimiento mal configurada");
            return null;
        }
        if (gastoViaticos == null) {
//            MensajesErrores.advertencia("Cuenta contable de gasto es necesario");
            return null;
        }
        int productoBanco = -1;
        int productoTipo = 1;

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
            // Tipo de Asiento
            parametros = new HashMap();
            parametros.put(";where", "o.rubro=6 and o.nombre like 'VIATICOS%'");
            List<Tipoasiento> listaTa = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento ta = null;
            for (Tipoasiento t : listaTa) {
                ta = t;
            }
            if (ta == null) {
                MensajesErrores.advertencia("No existe tipo de asiento para rubro 6");
                return null;
            }
            int numero = ta.getUltimo();
            numero++;
            //
            String partida = viaticoEmpleado.getViatico().getPartida().substring(0, 2);
            String cuenta = ctaInicio + partida + ctaFin;
            Cuentas cuentaEmpleado = cuentasBean.traerCodigo(cuenta);
            cuentasBean.setCuenta(cuentaEmpleado);
            if (cuentasBean.validaCuentaMovimiento()) {
                MensajesErrores.advertencia("Cuenta contable de empleado  mal configurada : " + cuenta);
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            // Traer la cuenta con la partida
            //
            Cabeceras cab = new Cabeceras();
            Cabeceras cabLiquid = new Cabeceras();
            if (definitivo) {

                cab.setDescripcion(getKardex().getObservaciones());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(ta);
                cab.setNumero(numero);
                cab.setFecha(kardex.getFechamov());
                cab.setIdmodulo(viaticoEmpleado.getId());
                cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cab.setOpcion("GASTO_VIATICOS");
                numero++;
                cabLiquid.setDescripcion(getKardex().getObservaciones());
                cabLiquid.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabLiquid.setDia(new Date());
                cabLiquid.setTipo(ta);
                cabLiquid.setNumero(numero);
                cabLiquid.setFecha(kardex.getFechamov());
                cabLiquid.setIdmodulo(viaticoEmpleado.getId());
                cabLiquid.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cabLiquid.setOpcion("LIQUIDACION_VIATICOS");
                ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
                ejbCabecera.create(cabLiquid, parametrosSeguridad.getLogueado().getUserid());
                ta.setUltimo(numero);
                ejbTipoAsiento.edit(ta, parametrosSeguridad.getLogueado().getUserid());
            }
            parametros = new HashMap();
            parametros.put(";where", "o.validado=true and o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", viaticoEmpleado);
            parametros.put(";campo", "o.valorvalidado");
            double valoValidado = ejbDetalleViatico.sumarCampo(parametros).doubleValue();
            // las dos cuentas
            if (valoValidado < 0) {
                MensajesErrores.advertencia("Valor Validado no puede ser menor a cero");
            }
            if (valoValidado > 0) {
                Renglones rGasto = new Renglones(); // reglon de gasto
                if (definitivo) {
                    rGasto.setCabecera(cab);
                }
                rGasto.setReferencia(kardex.getObservaciones());
                rGasto.setValor(new BigDecimal(valoValidado));
                rGasto.setCuenta(gastoViaticos.getCodigo());
                rGasto.setPresupuesto(gastoViaticos.getPresupuesto());
                rGasto.setFecha(kardex.getFechamov());
                if (gastoViaticos.getAuxiliares() != null) {
                    rGasto.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
                }
                Renglones rPorPagar = new Renglones(); // reglon por pagar
                if (definitivo) {
                    rPorPagar.setCabecera(cab);
                }
                rPorPagar.setReferencia(kardex.getObservaciones());
                rPorPagar.setValor(new BigDecimal(valoValidado * -1));
                rPorPagar.setCuenta(cuentaEmpleado.getCodigo());
                rPorPagar.setPresupuesto(cuentaEmpleado.getPresupuesto());
                rPorPagar.setFecha(kardex.getFechamov());
                if (cuentaEmpleado.getAuxiliares() != null) {
                    rPorPagar.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
                }

                Renglones rPorPagarPositivo = new Renglones(); // reglon por pagar
                if (definitivo) {
                    rPorPagarPositivo.setCabecera(cabLiquid);
                }
                rPorPagarPositivo.setReferencia(kardex.getObservaciones());
                rPorPagarPositivo.setValor(new BigDecimal(valoValidado));
                rPorPagarPositivo.setCuenta(cuentaEmpleado.getCodigo());
                rPorPagarPositivo.setPresupuesto(cuentaEmpleado.getPresupuesto());
                rPorPagarPositivo.setFecha(kardex.getFechamov());
                if (cuentaEmpleado.getAuxiliares() != null) {
                    rPorPagarPositivo.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
                }
                Renglones rViaticos = new Renglones(); // reglon por pagar
                if (definitivo) {
                    rViaticos.setCabecera(cabLiquid);
                }
                rViaticos.setReferencia(kardex.getObservaciones());
                rViaticos.setValor(new BigDecimal(valoValidado * -1));
                rViaticos.setCuenta(cuentaViaticos.getCodigo());
                rViaticos.setPresupuesto(cuentaViaticos.getPresupuesto());
                rViaticos.setFecha(kardex.getFechamov());
                if (cuentaViaticos.getAuxiliares() != null) {
                    rViaticos.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
                }
                listaReglones.add(rGasto);
                listaReglones.add(rPorPagar);
                listaReglones.add(rPorPagarPositivo);
                listaReglones.add(rViaticos);
                if (definitivo) {
                    int anioCertificacion = viaticoEmpleado.getViatico().getCertificacion().getAnio();
                    int nume = 0;
                    Date desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
                    Calendar ca = Calendar.getInstance();
                    ca.setTime(desde);
                    int anioActual = ca.get(Calendar.YEAR);
                    Codigos codi;
                    if (anioCertificacion == anioActual) {
                        codi = ejbCodigos.traerCodigo("NUM", "09");
                        if (codi == null) {
                            MensajesErrores.advertencia("No existe numeración para compromisos Actuales");
                            return null;
                        }
                        if (codi.getParametros() == null) {
                            MensajesErrores.advertencia("No se encuentra numeración");
                            return null;
                        }

                        Integer num = Integer.parseInt(codi.getParametros());
                        nume = num + 1;
                    } else {
                        codi = ejbCodigos.traerCodigo("NUM", "05");
                        if (codi == null) {
                            MensajesErrores.advertencia("No existe numeración para Compromisos Anteriores");
                            return null;
                        }
                        if (codi.getParametros() == null) {
                            MensajesErrores.advertencia("No se encuentra numeración");
                            return null;
                        }
                        Integer num = Integer.parseInt(codi.getParametros());
                        nume = num + 1;
                    }
                    Compromisos c = new Compromisos();
                    c.setFecha(viaticoEmpleado.getFechaliquidacion());
                    c.setContrato(null);
                    c.setImpresion(viaticoEmpleado.getFechaliquidacion());
                    c.setMotivo(viaticoEmpleado.getViatico().getMotivo());
                    c.setProveedor(null);
                    c.setCertificacion(viaticoEmpleado.getViatico().getCertificacion());
                    c.setEmpleado(viaticoEmpleado.getEmpleado());
                    c.setBeneficiario(viaticoEmpleado.getEmpleado());
                    c.setNumerocomp(nume);
                    c.setAnio(viaticoEmpleado.getViatico().getCertificacion().getAnio());
//                c.setDireccion(caja.getDepartamento());
                    ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
                    codi.setParametros(nume + "");
                    ejbCodigos.edit(codi, parametrosSeguridad.getLogueado().getUserid());
//            CREACION DE DETALLES COMPROMISOS
                    Detallecompromiso dc = new Detallecompromiso();
                    dc.setCompromiso(c);
                    dc.setAsignacion(traerDetalleCertificacion(viaticoEmpleado.getViatico().getCertificacion()).getAsignacion());
                    dc.setMotivo(viaticoEmpleado.getViatico().getMotivo());
                    dc.setFecha(viaticoEmpleado.getFechaliquidacion());
                    dc.setValor(BigDecimal.valueOf(valoValidado));
                    dc.setSaldo(new BigDecimal(BigInteger.ZERO));
                    ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
//                ACTUALIZA DETALLE DE VALE AGREGANDO EL DETALLE DE COMPROMISO
                    viaticoEmpleado.setDetallecompromiso(dc.getId());
                    viaticoEmpleado.setEstado(3);
                    ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());

                    ejbRenglones.create(rGasto, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(rPorPagar, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(rPorPagarPositivo, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(rViaticos, parametrosSeguridad.getLogueado().getUserid());
//                    viaticoEmpleado.setEstado(3);
//                    viaticoEmpleado.setKardexliquidacion(kardexDeposito);
//                    ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
                }

            }

//            formularioImpresion.cancelar();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

//        if ((kardex.getDocumento() == null) || (kardex.getDocumento().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario documento en movimiento");
//            return true;
//        }
//        if ((viaticoEmpleado.getFechaliquidacion() == null) || (viaticoEmpleado.getFechaliquidacion().before(viaticoEmpleado.getViatico().getCertificacion().getFecha()))) {
//            MensajesErrores.advertencia("Es necesario fecha de movimiento válido en movimiento posterior a la certificación");
//            return true;
//        }
//
        if (viaticoEmpleado.getFechaliquidacion().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Es necesario fecha de movimiento válido mayor o igual a periodo vigente");
            return true;
        }

        if (kardex.getObservaciones() == null || kardex.getObservaciones().isEmpty()) {
            MensajesErrores.advertencia("Es necesario observaciones");
            return true;
        }
//         if (kardexDeposito==null) {
//            MensajesErrores.advertencia("Es necesario fecha de movimiento válido en movimiento");
//            return true;
//        }
//        if (kardex.getFechamov().before(configuracionBean.getConfiguracion().getPvigente())) {
//            MensajesErrores.advertencia("Es necesario fecha de movimiento válido mayor o igual a periodo vigente");
//            return true;
//        }
//        if (kardex.getBanco() == null) {
//            MensajesErrores.advertencia("Es necesario banco del movimiento");
//            return true;
//        }
//        if (kardex.getBanco() == null) {
//            MensajesErrores.advertencia("Es necesario tipo de movimiento");
//            return true;
//        }
//        if ((kardex.getValor() == null) || (kardex.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Es necesario valor de movimiento");
//            return true;
//        }
//        Empleados e = viaticoEmpleado.getEmpleado();
//        kardex.setAuxiliar(empleadosBean.getEntidad().getPin());
        return false;
    }

    public String insertar() {
        viaticoEmpleado.setFechaliquidacion(fechaLiquidacion);
        if (validar()) {
            return null;
        }
        try {
//        try {
//            caja.setLiquidado(Boolean.TRUE);
//            kardex.setFechagraba(new Date());
//            kardex.setOrigen("LIQUIDACION VIÁTICOS");
//            kardex.setFechamov(viaticoEmpleado.getFechaliquidacion());
//            if (kardex.getId() == null) {
//                ejbKardexbanco.create(kardex, parametrosSeguridad.getLogueado().getUserid());
//            } else {
//                ejbKardexbanco.edit(kardex, parametrosSeguridad.getLogueado().getUserid());
//            }
            //Valor del viatico
            double cuadre3 = Math.round(viaticoEmpleado.getValor().doubleValue());
            double dividido3 = cuadre3 / 100;
            BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
            double valorVIat = (valortotal3.doubleValue());

            double valor = 0;
            if (masDepositosDesconocidos) {
                for (Kardexbanco kb : listadoPropioDesconocidos) {
                    if (kb.getSeleccionado()) {
                        Depositos d = new Depositos();
                        d.setKardexliquidacion(kb);
                        d.setViaticoempleado(viaticoEmpleado);
                        ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                    }
                    valor += kb.getValor().doubleValue();
                }
                double cuadre = Math.round(valor);
                double dividido = cuadre / 100;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double valorDepo = (valortotal.doubleValue());
                //Si el valor de los depositos es igual al valor del viatico solo se guarda el registro sin contabilizacion 
                if (valorDepo == valorVIat) {
                    viaticoEmpleado.setEstado(3);
                    ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
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
                }
                imprimirLiq(viaticoEmpleado);
            } else {
                if (kardexDepositoDesconocido != null) {
                    viaticoEmpleado.setKardexliquidacion(kardexDepositoDesconocido);
                    double cuadre = Math.round(kardexDepositoDesconocido.getValor().doubleValue());
                    double dividido = cuadre / 100;
                    BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                    double valorDepo = (valortotal.doubleValue());
                    if (valorDepo == valorVIat) {
                        viaticoEmpleado.setEstado(3);
                        ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
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
                    }
                    imprimirLiq(viaticoEmpleado);
                } else {
                    //Mas de un deposito
                    if (masDepositos) {
                        for (Kardexbanco kb : listadoPropio) {
                            if (kb.getSeleccionado()) {
                                Depositos d = new Depositos();
                                d.setKardexliquidacion(kb);
                                d.setViaticoempleado(viaticoEmpleado);
                                ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());

                            }
                            valor += kb.getValor().doubleValue();
                        }
                        double cuadre = Math.round(valor);
                        double dividido = cuadre / 100;
                        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                        double valorDepo = (valortotal.doubleValue());
                        //Si el valor de los depositos es igual al valor del viatico solo se guarda el registro sin contabilizacion 
                        if (valorDepo == valorVIat) {
                            viaticoEmpleado.setEstado(3);
                            ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
                        } else {
                            if (reposicion.getDocumento() == null) {
                            } else {
                                if (grabarReposicion()) {
                                    MensajesErrores.advertencia("Error al grabar Reposicion");
                                    return null;
                                }
                            }
                            contabilizar(true);
                        }
                        imprimirLiq(viaticoEmpleado);
                    } else {
                        if (kardexDeposito != null) {
                            viaticoEmpleado.setKardexliquidacion(kardexDeposito);
                            double cuadre = Math.round(kardexDeposito.getValor().doubleValue());
                            double dividido = cuadre / 100;
                            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                            double valorDepo = (valortotal.doubleValue());
                            if (valorDepo == valorVIat) {
                                viaticoEmpleado.setEstado(3);
                                ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
                            } else {
                                if (reposicion.getDocumento() == null) {
                                } else {
                                    if (grabarReposicion()) {
                                        MensajesErrores.advertencia("Error al grabar Reposicion");
                                        return null;
                                    }
                                }
                                contabilizar(true);
                            }
                            imprimirLiq(viaticoEmpleado);
                        } else {
                            if (masDescuentos) {
                                for (Renglones r : listadoRol) {
                                    if (r.getSeleccionado()) {
                                        Depositos d = new Depositos();
                                        d.setRenglonliquidacion(r);
                                        d.setViaticoempleado(viaticoEmpleado);
                                        ejbDepositos.create(d, parametrosSeguridad.getLogueado().getUserid());
                                    }
                                    valor += r.getValor().doubleValue();
                                }
                                double cuadre = Math.round(valor);
                                double dividido = cuadre / 100;
                                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                                double valorDesc = (valortotal.doubleValue());
                                if (valorDesc == valorVIat) {
                                    viaticoEmpleado.setEstado(3);
                                    ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
                                } else {
                                    if (reposicion.getDocumento() == null) {
                                    } else {
                                        if (grabarReposicion()) {
                                            MensajesErrores.advertencia("Error al grabar Reposicion");
                                            return null;
                                        }
                                    }
                                    contabilizar(true);
                                }
                                imprimirLiq(viaticoEmpleado);
                            } else {
                                if (RenglonDescuento != null) {
                                    viaticoEmpleado.setRenglonliquidacion(RenglonDescuento);
                                    double cuadre = Math.round(RenglonDescuento.getValor().doubleValue());
                                    double dividido = cuadre / 100;
                                    BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                                    double valorDesc = (valortotal.doubleValue());
                                    if (valorDesc == valorVIat) {
                                        viaticoEmpleado.setEstado(3);
                                        ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
                                    } else {
                                        if (reposicion.getDocumento() == null) {
                                        } else {
                                            if (grabarReposicion()) {
                                                MensajesErrores.advertencia("Error al grabar Reposicion");
                                                return null;
                                            }
                                        }
                                        contabilizar(true);
                                    }
                                    imprimirLiq(viaticoEmpleado);
                                } else {
                                    if (reposicion.getDocumento() == null) {
                                    } else {
                                        if (reposicion.getDocumento() == null) {
                                        } else {
                                            if (grabarReposicion()) {
                                                MensajesErrores.advertencia("Error al grabar Reposicion");
                                                return null;
                                            }
                                        }
                                    }
                                    contabilizar(true);
                                    imprimirLiq(viaticoEmpleado);
                                }
                            }
                        }
                    }
                }
            }
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        viaticoEmpleado = null;
        listadoPropio = new LinkedList<>();
        listadoRol = new LinkedList<>();
        formulario.cancelar();
        MensajesErrores.informacion("Registro grabado con éxito");
        return null;
    }

    public String contabilizarDesconocidos(double valorF) {
        if (viaticoEmpleado == null) {
            return null;
        }

        Codigos codigo = codigosBean.traerCodigo("GASTGEN", "V");
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
            parametros.put(";where", "o.rubro=6");
            List<Tipoasiento> listaTa = ejbTipoAsiento.encontarParametros(parametros);
            Tipoasiento ta = null;
            for (Tipoasiento t : listaTa) {
                ta = t;
            }
            if (ta == null) {
                MensajesErrores.advertencia("No existe tipo de asiento para rubro 6");
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
            cabDesc.setIdmodulo(viaticoEmpleado.getId());
            cabDesc.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            cabDesc.setOpcion("VIATICOS_DEPOSITO_DESCONOCIDO");
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
                r.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
            }
            ejbRenglones.create(r1, parametrosSeguridad.getLogueado().getUserid());
            ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", viaticoEmpleado);
            List<Detalleviaticos> lista2 = ejbDetalleViatico.encontarParametros(parametros);
            if (!lista2.isEmpty()) {
                for (Detalleviaticos vf : lista2) {
                    vf.setReposicion(reposicion);
                    ejbDetalleViatico.edit(vf, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionFondoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Detallecertificaciones traerDetalleCertificacion(Certificaciones aux) throws ConsultarException {
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", aux);
        parametros.put(";inicial", 0);
        parametros.put(";final", 1);
        List<Detallecertificaciones> lista = ejbDetalleCertificacion.encontarParametros(parametros);
        return lista.get(0);
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
                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public SelectItem[] getComboAutorizaciones2() {
        if (viaticoEmpleado.getEmpleado() == null) {
            return null;
        }
        if (reposicion.getDocumento() == null) {
            return null;
        }
        try {
            String ruc = viaticoEmpleado.getEmpleado().getEntidad().getPin() + "001";
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
            if (viaticoEmpleado.getEmpleado() == null) {
                MensajesErrores.advertencia("Seleccione un proveedor  primero");
                return null;
            }
            String ruc = viaticoEmpleado.getEmpleado().getEntidad().getPin() + "001";
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
            Logger
                    .getLogger(PuntoEmisionBean.class
                            .getName()).log(Level.SEVERE, null, ex);
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

    /**
     * @return the viaticoEmpleado
     */
    public Viaticosempleado getViaticoEmpleado() {
        return viaticoEmpleado;
    }

    /**
     * @param viaticoEmpleado the viaticoEmpleado to set
     */
    public void setViaticoEmpleado(Viaticosempleado viaticoEmpleado) {
        this.viaticoEmpleado = viaticoEmpleado;
    }

    /**
     * @return the viaticosBean
     */
    public ViaticosBean getViaticosBean() {
        return viaticosBean;
    }

    /**
     * @param viaticosBean the viaticosBean to set
     */
    public void setViaticosBean(ViaticosBean viaticosBean) {
        this.viaticosBean = viaticosBean;
    }

    /**
     * @return the listaDetalles
     */
    public List<Detalleviaticos> getListaDetalles() {
        return listaDetalles;
    }

    /**
     * @param listaDetalles the listaDetalles to set
     */
    public void setListaDetalles(List<Detalleviaticos> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    /**
     * @return the detalle
     */
    public Detalleviaticos getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detalleviaticos detalle) {
        this.detalle = detalle;
    }

    public SelectItem[] getComboEmpleadosViaticosDirecto() {
        try {
//            if (viatico != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico.vigente=true and o.estado=2 and o.kardexliquidacion is null  and o.viatico.reembolso=false");
//                parametros.put("viatico", viatico);
            List<Viaticosempleado> lista = ejbViaticosempleado.encontarParametros(parametros);
//            List<Viaticosempleado> retorno =new LinkedList<>();

//            for (Viaticosempleado v : lista) {
//                parametros = new HashMap();
//                parametros.put(";where", "o.viaticoempleado=:empleado");
//                parametros.put("empleado", v);
//                int total = ejbDetalleViatico.contar(parametros);
//                if (total>0){
//                    retorno.add(v);
//                }
//            }
            return Combos.getSelectItems(lista, true);
//            }
        } catch (ConsultarException ex) {
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboKardex() {
        if (viaticoEmpleado == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.tipomov.tipo='VIAT' and o.auxiliar=:auxiliar and o.estado=2";
            parametros.put(";where", where);
            parametros.put("auxiliar", viaticoEmpleado.getEmpleado().getEntidad().getPin());
//            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            List<Kardexbanco> listado = ejbKardexbanco.encontarParametros(parametros);
            listadoPropio = new LinkedList<>();
            for (Kardexbanco k : listado) {
                parametros = new HashMap();
                where = "  o.kardexliquidacion=:kardex";
                parametros.put(";where", where);
                parametros.put("kardex", k);
                int total = ejbViaticosempleado.contar(parametros);
                int total2 = ejbDepositos.contar(parametros);
                if (total == 0 && total2 == 0) {
                    listadoPropio.add(k);
                }
            }
            return Combos.getSelectItems(listadoPropio, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboKardexDesconocido() {
        if (viaticoEmpleado == null) {
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
        if (viaticoEmpleado == null) {
            return null;
        }
        try {
            listadoRol = new LinkedList<>();
            Codigos codigo = codigosBean.traerCodigo("GASTGEN", "V");
            Cuentas cuentaViaticos = cuentasBean.traerCodigo(codigo.getDescripcion());
            Codigos codigoRol = codigosBean.traerCodigo("TIPOROL", "ROL");
            Map parametros = new HashMap();
            parametros.put(";where", "o.cuenta=:cuenta and o.auxiliar=:auxiliar and o.cabecera.tipo.codigo=:codigo");
            parametros.put("cuenta", cuentaViaticos.getCodigo());
            parametros.put("auxiliar", viaticoEmpleado.getEmpleado().getEntidad().getPin());
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

    public SelectItem[] getComboCuentasPresupuesto() {
        if (viaticoEmpleado == null) {
            return null;
        }
        if (viaticoEmpleado.getViatico() == null) {
            return null;
        }
        if (viaticoEmpleado.getViatico().getPartida() == null) {
            return null;
        }
        // solo para quitar el error
        Map parametros = new HashMap();
        parametros.put(";where", "o.presupuesto=:codigo");
        parametros.put("codigo", viaticoEmpleado.getViatico().getPartida());
        try {
            List<Cuentas> cuentas = ejbCuentas.encontarParametros(parametros);
            return Combos.getSelectItems(cuentas, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimirLiq(Viaticosempleado ve) {
        try {
            Date fecha = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            List<Renglones> listadoGasto = traerRenglonesGasto(ve);
            List<Renglones> listadoLiquidacion = traerRenglonesLiquidacion(ve);
            List<Renglones> listadoDeposito = new LinkedList<>();
            List<Renglones> listadoDepositoVarios = traerRenglonesDepositoVarios(ve);
            List<Renglones> listadoRenglonesVarios = traerRenglonesVarios(ve);
            if (ve.getKardexliquidacion() != null) {
                listadoDeposito = traerRenglonesDeposito(ve);
            }
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("LIQUIDACIÓN DE VIATICOS : " + ve.getId());
            pdf.agregaParrafo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor Asignado :   " + df.format(ve.getValor())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Beneficiario :  " + ve.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha desde:   " + (sdf.format(ve.getDesde()))));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha hasta:   " + (sdf.format(ve.getHasta()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaParrafo("\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Créditos"));
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            Cabeceras c = new Cabeceras();
            //Gasto
            if (listadoGasto != null) {
                if (!listadoGasto.isEmpty()) {
                    for (Renglones r : listadoGasto) {
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
                        fecha = r.getCabecera().getFecha();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACIÓN GASTO N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Liquidacion
            if (listadoLiquidacion != null) {
                if (!listadoLiquidacion.isEmpty()) {
                    columnas = new LinkedList<>();
                    sumaDebitos = 0.0;
                    sumaCreditos = 0.0;
                    for (Renglones r : listadoLiquidacion) {
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACIÓN LIQUIDACIÓN N° " + c.getNumero());
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
            if (ve.getRenglonliquidacion() != null) {
                titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(ve.getRenglonliquidacion().getCabecera().getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getRenglonliquidacion().getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(ve.getRenglonliquidacion().getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getRenglonliquidacion().getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getRenglonliquidacion().getAuxiliar()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(ve.getRenglonliquidacion().getValor())));

                c = ve.getRenglonliquidacion().getCabecera();
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITOS N° " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            pdf.agregaParrafo("\n");
            if (obs != null) {
                pdf.agregaParrafo("Observaciones: " + obs);
            }
            pdf.agregaParrafo("Fecha: " + sdf.format(fecha));
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
            imprimirCompromiso(ve.getDetallecompromiso());
            formularioReportes.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirCompromiso(Integer dcompromiso) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", dcompromiso);
            List<Detallecompromiso> lista = ejbDetallecompromiso.encontarParametros(parametros);
            if (lista.isEmpty()) {
                return null;
            }
            Detallecompromiso dc = lista.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            SimpleDateFormat anio = new SimpleDateFormat("yyyy");
            DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaTitulo("INFORME DE COMPROMISO\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Compromiso :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, dc.getCompromiso().getNumerocomp() + ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf1.format(dc.getCompromiso().getFecha()).toUpperCase()));
            pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio.format(dc.getCompromiso().getFecha())
                    + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaTitulo("\n");
            String proveedor = "";
            String contrato = "";
            if (dc.getCompromiso().getProveedor() != null) {
                proveedor = dc.getCompromiso().getProveedor().getEmpresa().toString();
                if (dc.getCompromiso().getContrato() != null) {
                    contrato = dc.getCompromiso().getContrato().toString();
                }
            } else if (dc.getCompromiso().getBeneficiario() != null) {
                proveedor = dc.getCompromiso().getBeneficiario().getEntidad().toString();

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
            String que = "";
            que = " Programa : " + dc.getAsignacion().getProyecto().toString();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(dc.getFecha())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dc.getAsignacion().getClasificador().getCodigo()));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, dc.getAsignacion().getClasificador().getNombre()));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dc.getAsignacion().getFuente().getNombre()));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, dc.getValor().doubleValue()));
            total += dc.getValor().doubleValue();
            DecimalFormat df = new DecimalFormat("###,##0.00");
            pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
            if (obs != null) {
                pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + obs.toUpperCase());
            } else {
                pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + dc.getCompromiso().getMotivo());
            }
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\nEl Monto del compromiso asciende a: " + df.format(total) + "\n");
            pdf.agregaParrafo("\nTotal compromiso: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
            pdf.agregaParrafo("\n\n");
            if (dc.getCompromiso().getCertificacion() != null) {
                if (dc.getCompromiso().getCertificacion().getNumerocert() != null) {
                    pdf.agregaParrafo("\nCertificación Nro: " + dc.getCompromiso().getCertificacion().getNumerocert() + " - " + dc.getCompromiso().getCertificacion().getMotivo() + "\n");
                    pdf.agregaParrafo("\n\n");
                }
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista de Presupuesto"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Jefe de Presupuesto/Director Financiero"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteComp = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<Renglones> traerRenglonesGasto(Viaticosempleado ve) {
        if (ve != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('GASTO_VIATICOS')");
            parametros.put("id", ve.getId());
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

    private List<Renglones> traerRenglonesLiquidacion(Viaticosempleado ve) {
        if (ve != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('LIQUIDACION_VIATICOS')");
            parametros.put("id", ve.getId());
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

    private List<Renglones> traerRenglonesDeposito(Viaticosempleado ve) {
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

    private List<Renglones> traerRenglonesDepositoVarios(Viaticosempleado ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);

                for (Depositos d : rl) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion in ('KARDEX BANCOS')");
                    parametros.put("id", d.getKardexliquidacion().getId());
                    parametros.put(";orden", "o.cuenta desc,o.valor");
                    rl2 = ejbRenglones.encontarParametros(parametros);
                    for (Renglones r : rl2) {
                        rl3.add(r);
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

    private List<Renglones> traerRenglonesVarios(Viaticosempleado ve) {
        if (ve != null) {
            List<Renglones> rl2 = new LinkedList<>();
            List<Renglones> rl3 = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", ve);
            parametros.put(";orden", "o.id desc");
            try {
                List<Depositos> rl = ejbDepositos.encontarParametros(parametros);

                for (Depositos d : rl) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.id=:id");
                    parametros.put("id", d.getRenglonliquidacion().getId());
                    parametros.put(";orden", "o.cuenta desc,o.valor");
                    rl2 = ejbRenglones.encontarParametros(parametros);
                    for (Renglones r : rl2) {
                        rl3.add(r);
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

    public String regresar() {
        formularioRegresar.insertar();
        return null;
    }

    public String grabarRegresar() {
        try {
            viaticoEmpleado.setEstado(0);
            viaticoEmpleado.setFechaliquidacion(null);

            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado");
            parametros.put("empleado", viaticoEmpleado);
            List<Detalleviaticos>listaDetallesBorrar = ejbDetalleViatico.encontarParametros(parametros);

            for (Detalleviaticos dv : listaDetallesBorrar) {
                dv.setValidado(false);
                dv.setValorvalidado(null);
                if (dv.getTipocomprobante().equals("30% ASIGNADO POR LEY")) {
                    ejbDetalleViatico.remove(dv, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetalleViatico.edit(dv, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
        } catch (ConsultarException | BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.cancelar();
        return null;
    }

    /**
     * @return the gastoViaticos
     */
    public Cuentas getGastoViaticos() {
        return gastoViaticos;
    }

    /**
     * @param gastoViaticos the gastoViaticos to set
     */
    public void setGastoViaticos(Cuentas gastoViaticos) {
        this.gastoViaticos = gastoViaticos;
    }

    public double getTotalViaticos() {
        double valor = 0;
//        for (Detalleviaticos d : listaDetalles) {
        if (listaDetalles != null) {
            for (Detalleviaticos d : listaDetalles) {
                valor += d.getValor().doubleValue();
            }
        }
        return valor;
//        DecimalFormat df = new DecimalFormat("###,##0.00");
//        return df.format(valor);
    }

    public double getTotalViaticosValidados() {
        double valor = 0;
//        for (Detalleviaticos d : listaDetalles) {
        if (listaDetalles != null) {
            for (Detalleviaticos d : listaDetalles) {
                if (d.getValorvalidado() != null) {
                    valor += d.getValorvalidado().doubleValue();
                }
            }
        }
        return valor;
//        DecimalFormat df = new DecimalFormat("###,##0.00");
//        return df.format(valor);
    }

    public double getTotalDiferencia() {
        double valor = 0;
        if (viaticoEmpleado != null) {
            valor = viaticoEmpleado.getValor().doubleValue();
            return valor - getTotalViaticosValidados();
        }
        return valor;
    }

    /**
     * @return the fechaLiquidacion
     */
    public Date getFechaLiquidacion() {
        return fechaLiquidacion;
    }

    /**
     * @param fechaLiquidacion the fechaLiquidacion to set
     */
    public void setFechaLiquidacion(Date fechaLiquidacion) {
        this.fechaLiquidacion = fechaLiquidacion;
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
     * @return the formularioReportes
     */
    public Formulario getFormularioReportes() {
        return formularioReportes;
    }

    /**
     * @param formularioReportes the formularioReportes to set
     */
    public void setFormularioReportes(Formulario formularioReportes) {
        this.formularioReportes = formularioReportes;
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
     * @return the obs
     */
    public String getObs() {
        return obs;
    }

    /**
     * @param obs the obs to set
     */
    public void setObs(String obs) {
        this.obs = obs;
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
     * @return the formularioRegresar
     */
    public Formulario getFormularioRegresar() {
        return formularioRegresar;
    }

    /**
     * @param formularioRegresar the formularioRegresar to set
     */
    public void setFormularioRegresar(Formulario formularioRegresar) {
        this.formularioRegresar = formularioRegresar;
    }
}
