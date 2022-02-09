/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallesvales;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proveedores;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.personal.sfccbdmq.ValesCajaBean;
import org.presupuestos.sfccbdmq.PresupuestoCajasBean;
import org.presupuestos.sfccbdmq.ReporteReformasBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "cuentasCajasSfccbdmq")
@ViewScoped
public class ContabilizacionCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public ContabilizacionCajasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioDetalleVale = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioVales = new Formulario();
    private Formulario formularioCaja = new Formulario();
    private Formulario formularioRegresar = new Formulario();
    private Cajas caja;
    private Cajas cajan;
    private List<Cajas> listaCajas;
    private Empleados empleado;
    private Organigrama direccion;
    private Recurso reporte;
    private List<Valescajas> listaVales;
    private Valescajas vale;
    private Detallesvales detalle;
    private List<Detallesvales> listaDetalle;
    private List<Detallesvales> listaTodosDetalles;
    private Integer estadonuevo = 1;
    private Documentos autorizacion = new Documentos();
    private List<Renglones> listaReglones;
    private List<Renglones> listaReglonesInversion;
    private List<Detallecompromiso> listaDetallesC;
    private List<Compromisos> listaCompromisos;
    private Cabeceras cabecera;
    private Compromisos compromiso;

    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private ValescajasFacade ejbVales;
    @EJB
    private DetallecertificacionesFacade ejbDetallescertificacion;
    @EJB
    private DetallesvalesFacade ejbDetallesvale;
    @EJB
    private AutorizacionesFacade ejbAutorizaciones;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @EJB
    private CuentasFacade ejbCuentas;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private CabecerasFacade ejbCabeceras;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    /////////////NUevo
    private Obligaciones obligacion;
    private Retencionescompras retencion;
    private List<Retencionescompras> listaRetenciones;
    private Puntoemision puntoEmision;
    private Formulario formularioRetencion = new Formulario();
    @EJB
    private RetencionescomprasFacade ejbRetenciones;
    @EJB
    private RetencionesFacade ejbRetencionesSri;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private PuntoemisionFacade ejbPuntos;

    //////////////////////////////
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
        String nombreForma = "CuentasCajasVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        cajan = new Cajas();
        cajan.setFecha(new Date());
    }

    // fin perfiles
    //////////////////////NUEVO
    private void traerRetenciones() {
        if (obligacion != null) {
            if (obligacion.getId() != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", obligacion);
                try {
                    listaRetenciones = ejbRetenciones.encontarParametros(parametros);
                    if (obligacion.getPuntor() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                        parametros.put("codigo", obligacion.getPuntor());
                        parametros.put("sucursal", obligacion.getEstablecimientor());
                        List<Puntoemision> lpt = ejbPuntos.encontarParametros(parametros);
                        for (Puntoemision p : lpt) {
                            puntoEmision = p;
                        }
                    }
                } catch (ConsultarException ex) {
                    Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
//////////////////////FIN NUEVO

    public String modificar(int fila) {
        vale = listaVales.get(fila);
//        if ((vale.getEstado().equals(0)) || (vale.getEstado().equals(2))) {
//            MensajesErrores.advertencia("Debe estar en estado de reposición para poder modificar");
//            return null;
//            //estadonuevo = vale.getEstado();
//        }
//////////////////////NUEVO
        obligacion = vale.getObligacion();
        if (obligacion == null) {
            obligacion = new Obligaciones();
            obligacion.setContrato(null);
            obligacion.setConcepto(caja.getObservaciones());
            obligacion.setFechaemision(caja.getFecha());
            obligacion.setEstado(2); // No se si 1 o que
            obligacion.setProveedor(vale.getProveedor()); // No se si 1 o que
            obligacion.setFechaingreso(new Date());
            obligacion.setUsuario(parametrosSeguridad.getLogueado());
//            obligacion.setCompromiso(vale.getCompromiso());
        }
        traerRetenciones();
        //////////////////////NUEVO
        listaDetalle = new LinkedList<>();
        buscarDetalle();
        formulario.editar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscarCajas() {
        try {
            empleado = empleadosBean.getEmpleadoSeleccionado();
            Map parametros = new HashMap();
            String where = "  o.apertura is null ";

            //NOMBRE
            if (direccion != null) {

                where += " and o.departamento=:departamento";
                parametros.put("departamento", direccion);
            }
            if (empleado != null) {

                where += " and o.empleado=:empleado";
                parametros.put("empleado", empleado);
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaCajas = ejbCajas.encontarParametros(parametros);
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        if (caja == null) {
            MensajesErrores.advertencia("Selecciones un Empleado");
            listaVales = new LinkedList<>();
            return null;
        }
        try {
//            if (estadonuevo == 0) {
//                estadonuevo++;
//            }
            Map parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
            parametros.put("estado", 2);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
            if (listaVales != null) {
                formularioVales.insertar();
            }
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
//        if (cajan.getDepartamento() == null) {
//            MensajesErrores.advertencia("Departamento es necesario");
//            return true;
//        }
//        if (cajan.getEmpleado() == null) {
//            MensajesErrores.advertencia("Custodio es necesario");
//            return true;
//        }
//        if ((cajan.getValor() == null) || (cajan.getValor().doubleValue() <= 0)) {
//            MensajesErrores.advertencia("Valor es necesario");
//            return true;
//        }
//        if ((cajan.getObservaciones() == null) || (cajan.getObservaciones().isEmpty())) {
//            MensajesErrores.advertencia("Observaciones es necesario");
//            return true;
//        }
//        if (cajan.getAutorizacion() == null) {
//            MensajesErrores.advertencia("Autorización es necesario");
//            return true;
//        }
//        if (cajan.getNrodocumento() == null) {
//            MensajesErrores.advertencia("Número de documento es necesario");
//            return true;
//        }
        if (autorizacion == null) {
            MensajesErrores.advertencia("Autorización es necesario");
            return true;
        }
        if (cajan.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesario");
            return true;
        }
        if (!(listaReglones.size() > 0)) {
            MensajesErrores.advertencia("Requiere al menos un vale de caja para reembolso ");
            return true;
        }
        //////////////////////NUEVO
        if (listaRetenciones != null) {
            if (puntoEmision == null) {
                MensajesErrores.advertencia("Es necesario punto de emisión de las retenciones ");
                return true;
            }
            Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
            Map parametros = new HashMap();
            parametros.put(";where", "o.punto=:punto and o.documento=:documento");
            parametros.put("punto", puntoEmision);
            parametros.put("documento", ret);
            Documentos documento = null;
            try {
                List<Documentos> documentos = ejbDocumentos.encontarParametros(parametros);
                for (Documentos d : documentos) {
                    documento = d;
                }
                if (documento != null) {
                    obligacion.setPuntor(documento.getPunto().getCodigo());
                    obligacion.setEstablecimientor(documento.getPunto().getSucursal().getRuc());
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        //////////////////////FIN NUEVO
        return false;
    }

    public String grabar() {
        traerDetallesVale();
        traerDetallesRetenciones();
        try {
            if (listaTodosDetalles == null) {
                MensajesErrores.advertencia("No hay nada que grabar");
                return null;
            }
            if (listaTodosDetalles.isEmpty()) {
                MensajesErrores.advertencia("No hay nada que grabar");
                return null;
            }
            listaReglones = new LinkedList<>();
            listaReglonesInversion = new LinkedList<>();
            listaCompromisos = new LinkedList<>();
            listaDetallesC = new LinkedList<>();

            double valor = 0;
            for (Detallesvales dv : listaTodosDetalles) {

                Renglones r = new Renglones();
                r.setAuxiliar(dv.getVale().getCaja().getEmpleado().getEntidad().getPin());
                r.setValor(dv.getValor());
                valor += dv.getValor().negate().doubleValue();
                r.setReferencia(dv.getVale().getCaja().getReferencia());
                r.setFecha(dv.getVale().getCaja().getFecha());
                r.setCuenta(dv.getCuenta().getCodigo());
                estaEnRas(r);
                inversiones(dv.getCuenta(), r, 1);
//                listaReglones.add(r);
                //DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setFecha(cajan.getFecha());
                dc.setValor(dv.getValor());
                listaDetallesC.add(dc);
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            String partida = caja.getEmpleado().getPartida().substring(0, 2);
            String cuentaGrabar = ctaInicio + partida + ctaFin;
            Cuentas cuentaRmu = getCuentasBean().traerCodigo(cuentaGrabar);
            if (cuentaRmu == null) {

                MensajesErrores.advertencia("No existe cuenta x pagar empleado : " + cuentaGrabar);
                return null;

            }
            Renglones r = new Renglones();
            r.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
            r.setValor(new BigDecimal(valor));
            r.setReferencia(caja.getReferencia());
            r.setFecha(caja.getFecha());
            r.setCuenta(cuentaGrabar);
            estaEnRas(r);
            inversiones(cuentaRmu, r, 1);
            /////////////////////////RETENCIONES
            for (Retencionescompras rc : listaRetenciones) {
                // proveedor
                String ruc = rc.getObligacion().getProveedor().getEmpresa().getRuc();
                obligacion = rc.getObligacion();
                String cuentaProveedor = ctaInicio + rc.getPartida() + ctaFin;
                Cuentas ctaproveedor = cuentasBean.traerCodigo(cuentaProveedor);
                if (rc.getRetencion() != null) {
                    double valorRetencion = r.getValor().doubleValue();
                    Renglones rasProveedor = new Renglones();
                    // armar la cuenta proveedor

                    rasProveedor.setCuenta(cuentaProveedor);
                    rasProveedor.setReferencia(rc.getObligacion().getConcepto());
                    rasProveedor.setFecha(rc.getObligacion().getFechaemision());
                    rasProveedor.setDetallecompromiso(null);
                    rasProveedor.setAuxiliar(ruc);
                    double valorProveedor = (valorRetencion);
                    rasProveedor.setValor(new BigDecimal(valorProveedor));
                    // 
                    estaEnRas(rasProveedor);
                    inversiones(ctaproveedor, rasProveedor, 1);
                    // retenciones
                    String ctaRetencion = ctaInicio + rc.getPartida()
                            + rc.getRetencion().getCuenta() + rc.getRetencion().getCodigo();
                    Cuentas cuentaValidar = cuentasBean.traerCodigo(ctaRetencion);
                    Renglones rasRetencion = new Renglones();
                    // armar la cuenta
                    rasRetencion.setCuenta(ctaRetencion);
                    rasRetencion.setValor(new BigDecimal(valorRetencion * -1));
                    rasRetencion.setReferencia(rc.getObligacion().getConcepto());
                    if (cuentaValidar.getAuxiliares() != null) {
                        rasRetencion.setAuxiliar(ruc);
                    }
                    rasRetencion.setFecha(rc.getObligacion().getFechaemision());
                    valor += valorRetencion;
                    inversiones(cuentaValidar, rasRetencion, 1);
                    estaEnRas(rasRetencion);
                }
                // contabilizamos el impuesto
                if (rc.getRetencionimpuesto() != null) {
                    // proveedor
                    double valorRetencion = rc.getValoriva().doubleValue();
                    Renglones rasProveedor = new Renglones();
                    // armar la cuenta proveedor
                    rasProveedor.setCuenta(cuentaProveedor);
                    rasProveedor.setReferencia(obligacion.getConcepto());
                    rasProveedor.setFecha(obligacion.getFechaemision());
                    rasProveedor.setDetallecompromiso(null);
                    rasProveedor.setAuxiliar(ruc);
                    double valorProveedor = (valorRetencion);
                    rasProveedor.setValor(new BigDecimal(valorProveedor));
                    // 
                    estaEnRas(rasProveedor);
                    inversiones(ctaproveedor, rasProveedor, 1);
                    // retenciones
                    String ctaRetencion = ctaInicio + rc.getPartida()
                            + rc.getRetencionimpuesto().getCuenta()
                            + rc.getRetencionimpuesto().getCodigo();
                    Cuentas cuentaValidar = cuentasBean.traerCodigo(ctaRetencion);
                    Renglones rasRetencion = new Renglones();
                    // armar la cuenta
                    rasRetencion.setCuenta(ctaRetencion);
                    rasRetencion.setValor(new BigDecimal(valorRetencion * -1));
                    rasRetencion.setReferencia(obligacion.getConcepto());
                    if (cuentaValidar.getAuxiliares() != null) {
                        rasRetencion.setAuxiliar(ruc);
                    }
                    rasRetencion.setFecha(obligacion.getFechaemision());
                    valor += valorRetencion;
                    inversiones(cuentaValidar, rasRetencion, 1);
                    estaEnRas(rasRetencion);
                }
//                }
            }
            /////////////////////////////
            formulario.cancelar();
            formularioVales.cancelar();
            formularioCaja.insertar();
//            buscar();

        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean estaEnRas(Renglones r1) {
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }

        for (Renglones r : listaReglones) {
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
        listaReglones.add(r1);
        return false;
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
            rasInvInt.setFecha(new Date());
            estaEnRasInversiones(rasInvInt);
            Renglones rasContrapate = new Renglones();
            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia(ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasContrapate.setFecha(new Date());
            estaEnRasInversiones(rasContrapate);

        }
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

    public Formatos traerFormato() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lf = ejbFormatos.encontarParametros(parametros);
//        Query q = em.createQuery("Select Object(o) From Formatos as o where o.escxp=true");
//        List<Formatos> lf = q.getResultList();
            for (Formatos f : lf) {
                return f;
            }

        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String confirmar() {
        if (validar()) {
            return null;
        }
        try {
            Formatos f = traerFormato();
            if (f == null) {
                MensajesErrores.advertencia("ERROR: No se puede contabilizar: Necesario cxp en formatos");
                return null;
            }
            Codigos codigoReclasificacion = codigosBean.traerCodigo("TIPREC", "RET");
//            Codigos codigoReclasificacionInv = codigosBean.traerCodigo("TIPREC", "INVER");
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("ERROR: No existe creado códigos para reclasificación de cuentas");
                return null;
            }
//            if (codigoReclasificacionInv == null) {
//                return "ERROR: No existe creado códigos para reclasificación de cuentas de inversiones";
//            }
            String xx = f.getFormato().replace(".", "#");
            String[] aux1 = xx.split("#");
            int tamano = aux1[f.getNivel() - 1].length();
            Map parametros = new HashMap();
            parametros.put(";where", "o.modulo=:modulo");
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            Cabeceras cas = new Cabeceras();
            Tipoasiento tipo = null;
            Tipoasiento tipoReclasificacion = null;
            List<Tipoasiento> listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipo = t;
            }
            if (tipo == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para este módulo");
                return null;
            }

            parametros = new HashMap();
            parametros.put(";where", " o.codigo=:codigo");
            parametros.put("codigo", codigoReclasificacion.getNombre());
            listaTipo = ejbTipoAsiento.encontarParametros(parametros);
            for (Tipoasiento t : listaTipo) {
                tipoReclasificacion = t;
            }
            if (tipoReclasificacion == null) {
                MensajesErrores.advertencia("ERROR: No existe tipo de asiento para reclasificación de inversiones");
                return null;
            }
            String vale = ejbCabeceras.validarCierre(caja.getFecha());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja and o.estado=2");
            parametros.put("caja", caja);
            List<Valescajas> aux = ejbVales.encontarParametros(parametros);
            if (aux.size() > 0) {
//                CREACION DE LA NUEVA CAJA
                cajan = new Cajas();
                cajan.setApertura(caja);
                cajan.setApertura(null);
                cajan.setLiquidado(Boolean.FALSE);
                cajan.setJefe(caja.getJefe());
                cajan.setKardexbanco(null);
                cajan.setDepartamento(caja.getDepartamento());
                cajan.setEmpleado(caja.getEmpleado());
                cajan.setObservaciones(caja.getObservaciones());
                cajan.setValor(caja.getValor());
                cajan.setApertura(caja);
                cajan.setPrcvale(caja.getPrcvale());
                cajan.setFecha(new Date());
                ejbCajas.create(cajan, parametrosSeguridad.getLogueado().getUserid());
                for (Valescajas v : aux) {
                    v.setCaja(cajan);
                    v.setEstado(0);
                    ejbVales.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            // CREACION DE CABECERA
            cabecera = new Cabeceras();
            cabecera.setFecha(caja.getFecha());
            cabecera.setUsuario(cajan.getEmpleado().getEntidad().getUserid());
            cabecera.setNumero(tipo.getUltimo() + 1);
            cabecera.setDescripcion(caja.getObservaciones());
            cabecera.setDia(new Date());
            cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
            cabecera.setIdmodulo(cajan.getId());
            cabecera.setOpcion("REPOSICION CAJA CHICA");
            cabecera.setTipo(tipo);
            ejbCabeceras.create(cabecera, parametrosSeguridad.getLogueado().getUserid());
            tipo.setUltimo(tipo.getUltimo() + 1);
            ejbTipoAsiento.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
            //          CREACION DE COMPROMISOS
            Certificaciones certificacion = null;
            for (Detallesvales dv : listaTodosDetalles) {
                certificacion = dv.getDetallecertificacion().getCertificacion();
            }
            Compromisos c = new Compromisos();
            c.setFecha(cajan.getFecha());
            c.setContrato(null);
            c.setImpresion(caja.getFecha());
            c.setMotivo(caja.getObservaciones());
            c.setProveedor(null);
//                c.setNumeroanterior();
            c.setCertificacion(certificacion);
            c.setEmpleado(cajan.getEmpleado());
//                c.setDevengado();
            c.setDireccion(cajan.getDepartamento());
            ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
            for (Detallesvales dv : listaTodosDetalles) {
//            CREACION DE DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setCompromiso(c);
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setMotivo(caja.getObservaciones());
                dc.setFecha(cajan.getFecha());
//                dc.setSaldo();
                dc.setValor(dv.getValor());
                ejbDetallecompromiso.create(dc, parametrosSeguridad.getLogueado().getUserid());
//                ACTUALIZA DETALLE DE VALE AGREGANDO EL DETALLE DE COMPROMISO
                dv.setDetallecompromiso(dc);
                ejbDetallesvale.edit(dv, parametrosSeguridad.getLogueado().getUserid());
            }
//            CREACION DE RENGLONES
            for (Renglones r : listaReglones) {
//                if (cabecera != null) {
                r.setFecha(cabecera.getFecha());
                r.setReferencia(cabecera.getDescripcion());
                r.setCabecera(cabecera);
//                }
                ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
            }
            if (!listaReglonesInversion.isEmpty()) {
                cabecera = new Cabeceras();
                cabecera.setFecha(caja.getFecha());
                cabecera.setUsuario(cajan.getEmpleado().getEntidad().getUserid());
                cabecera.setNumero(tipoReclasificacion.getUltimo() + 1);
                cabecera.setDescripcion(caja.getObservaciones());
                cabecera.setDia(new Date());
                cabecera.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cabecera.setIdmodulo(cajan.getId());
                cabecera.setOpcion("REPOSICION CAJA CHICA");
                cabecera.setTipo(tipoReclasificacion);
                ejbCabeceras.create(cabecera, parametrosSeguridad.getLogueado().getUserid());
                tipoReclasificacion.setUltimo(tipoReclasificacion.getUltimo() + 1);
                ejbTipoAsiento.edit(tipoReclasificacion, parametrosSeguridad.getLogueado().getUserid());
                for (Renglones r : listaReglonesInversion) {
//                if (cabecera != null) {
                    r.setFecha(cabecera.getFecha());
                    r.setReferencia(cabecera.getDescripcion());
                    r.setCabecera(cabecera);
//                }
                    ejbRenglones.create(r, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            int nroActual = autorizacion.getNumeroactual();
            autorizacion.setNumeroactual(nroActual + 1);
            ejbDocumentos.edit(autorizacion, parametrosSeguridad.getLogueado().getUserid());
            /// retenciones ///////////////
            if (!listaRetenciones.isEmpty()) {
                Codigos ret = codigosBean.traerCodigo("DOCS", "RET");
                for (Retencionescompras rc : listaRetenciones) {
                    Obligaciones o = rc.getObligacion();
                    o.setFechacontable(o.getFechaemision());
                    o.setEstado(2);
                    if (o.getPuntor() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.punto.codigo=:punto and o.documento=:documento");
                        parametros.put("punto", o.getPuntor());
                        parametros.put("documento", ret);
                        List<Documentos> documentos = ejbDocumentos.encontarParametros(parametros);
                        Documentos doc = null;
                        for (Documentos dx : documentos) {
                            doc = dx;
                        }
                        if (doc != null) {
                            Integer numret = doc.getNumeroactual();
                            numret++;
                            o.setNumeror(numret);
                            doc.setNumeroactual(numret);
                            ejbDocumentos.edit(doc, parametrosSeguridad.getLogueado().getUserid());
                        } else {
                            o.setNumeror(o.getId());
                        }

                    }
                    ejbObligaciones.edit(o, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            /////////////// FIN
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioVales.cancelar();
        formularioCaja.cancelar();
        formularioDetalleVale.cancelar();
        buscar();
        //buscar();
        return null;
    }

    public String grabarDetalleVale() {
        if (vale.getDetallecertificacion() != null) {
            listaVales.add(vale);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

//    ES-2018-03-29 VALE DE CAJA
    public String grabarVale() {

        try {
            if (!(vale.getTipodocumento().getCodigo().equals("OTR"))) {
                if ((vale.getAutorizacion() == null) || (vale.getAutorizacion().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria autorización");
                    return null;
                }
                if ((vale.getPuntoemision() == null) || (vale.getPuntoemision().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria punto de emisión");
                    return null;
                }
                if ((vale.getEstablecimiento() == null) || (vale.getEstablecimiento().isEmpty())) {
                    MensajesErrores.advertencia("Necesaria establecimiento");
                    return null;
                }
                if ((vale.getNumero() == null) || (vale.getNumero() <= 0)) {
                    MensajesErrores.advertencia("Necesaria número de documento");
                    return null;
                }
            }
//            vale.setEstado(estadonuevo);
            if ((listaDetalle == null) || (listaDetalle.isEmpty())) {
                if (vale.getEstado() == 2) {
                    MensajesErrores.advertencia("No puede autorizar reembolso si no hay detalle del mismo");
//                    vale.setEstado(1);
                    return null;
                }
            }

            BigDecimal ciento = new BigDecimal(100);
            if (vale.getImpuesto() != null) {
//                vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
                double valorIva = vale.getBaseimponible().doubleValue() * vale.getImpuesto().getPorcentaje().doubleValue() / 100;
                double ivaValor = (double) vale.getAjuste() / 100;
                valorIva += ivaValor;
                vale.setIva(new BigDecimal(valorIva + vale.getAjuste() / 100));
            } else {
                vale.setIva(new BigDecimal(BigInteger.ZERO));
            }
            if (obligacion != null) {
                if (obligacion.getId() != null) {
                    vale.setObligacion(obligacion);
                }
            }
            for (Detallesvales ld : listaDetalle) {
                if (ld.getCuenta() == null) {
                    MensajesErrores.advertencia("Necesaria cuenta en renglón");
                    return null;
                }

            }
            for (Detallesvales ld : listaDetalle) {

                ld.setVale(vale);
                if (ld.getId() != null) {
                    ejbDetallesvale.edit(ld, parametrosSeguridad.getLogueado().getUserid());
                } else {
                    ejbDetallesvale.create(ld, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            vale.setEstado(4);
            ejbVales.edit(vale, parametrosSeguridad.getLogueado().getUserid());
            //////////////////////////////
            obligacion.setAutorizacion(vale.getAutorizacion());
            obligacion.setTipodocumento(vale.getTipodocumento());
            obligacion.setFechacaduca(vale.getFecha());
            obligacion.setEstablecimiento(vale.getEstablecimiento());
            obligacion.setPuntoemision(vale.getPuntoemision());

            ejbObligaciones.edit(obligacion, parametrosSeguridad.getLogueado().getUserid());
            /////////////////////////////////////////////
            double aux = vale.getBaseimponiblecero().doubleValue() + vale.getBaseimponible().doubleValue() + vale.getIva().doubleValue();
//            if (aux >= traerValor()) {
//                for (Detallesvales ld : listaDetalle) {
//                    ld.setVale(vale);
//                    if (ld.getId() != null) {
//                        ejbDetallesvale.edit(ld, parametrosSeguridad.getLogueado().getUserid());
//                    } else {
//                        ejbDetallesvale.create(ld, parametrosSeguridad.getLogueado().getUserid());
//                    }
//                }
//            } else {
//                MensajesErrores.advertencia("La suma de detalles de vale supera el valor de la factura");
//                return null;
//            }

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String getTotalVale() {
        double valor = traerValor();
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
    }

    public Double getTotalDetallesCom() {
        double valor = 0;
        for (Detallecompromiso dc : listaDetallesC) {
            valor += dc.getValor().doubleValue();
        }
        return valor;
    }

    public Double getTotalRenglones() {
        double valor = 0;
        for (Renglones r : listaReglones) {
            valor += r.getValor().doubleValue();
        }
        return valor;
    }

    public double traerValor() {
        if (vale == null) {
            return 0;
        }
        double valor = 0;
        for (Detallesvales d : listaDetalle) {
            valor += d.getValor().doubleValue();
        }
        return valor;
    }

//    ES-2018-03-28 DETALLES DE VALE DE CAJA
    public String buscarDetalle() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.vale=:vale");
        parametros.put("vale", vale);
        try {
            listaDetalle = ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevoDetalle() throws ConsultarException {
        detalle = new Detallesvales();
        detalle.setVale(vale);
        formularioDetalleVale.insertar();
        return null;
    }

    public String modificarDetalle(Detallesvales dt) {
        this.detalle = dt;
        formularioDetalleVale.editar();
        return null;
    }

    public String eliminarDetalle(Detallesvales dt) {
        this.detalle = dt;
        formularioDetalleVale.eliminar();
        return null;
    }

    public String cancelarDetalle() {
        formularioDetalleVale.cancelar();
        buscarDetalle();
        return null;
    }

    private boolean validarDetalle() {
        if (detalle.getDetallecertificacion() == null) {
            MensajesErrores.advertencia("Detalle de certificación es necesario");
            return true;
        }
        if (detalle.getCuenta() == null) {
            MensajesErrores.advertencia("Cuenta es necesario");
            return true;
        }
        if ((detalle.getValor() == null) || (detalle.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Valor es necesario");
            return true;
        }
        if (detalle.getValor().doubleValue() > valorSaldo(detalle.getDetallecertificacion().getAsignacion())) {
            MensajesErrores.advertencia("El valor indicado excede el saldo de la certificación");
            return true;
        }

        return false;
    }

    public String ingresarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        listaDetalle.add(detalle);
        formularioDetalleVale.cancelar();
        //buscarDetalle();
        return null;
    }

    public String grabarDetalle() {
        if (validarDetalle()) {
            return null;
        }
        //listaDetalle.add(detalle);
        formularioDetalleVale.cancelar();
        //buscarDetalle();
        return null;
    }

    public String borrarDetalle() {
        listaDetalle.remove(detalle);
        formularioDetalleVale.cancelar();
        //buscarDetalle();
        return null;
    }

    //COMBO CAJAS 
    public SelectItem[] getComboCajas() {
        try {
            Map parametros = new HashMap();
            String where = "  o.apertura is null ";
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
            listaCajas = ejbCajas.encontarParametros(parametros);
            return Combos.getSelectItems(listaCajas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //COMBO DETALLES CERTIFICACIÓN
    public SelectItem[] getComboDetallesCertificacion() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        int anio = c.get(Calendar.YEAR);
        try {
            Codigos cajaChica = ejbCodigos.traerCodigo("GASTGEN", "C");
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto.codigo=:codigo and o.asignacion.proyecto.anio=:anio");
            parametros.put("codigo", cajaChica.getParametros());
            parametros.put("anio", anio);
            List<Detallecertificaciones> aux = ejbDetallescertificacion.encontarParametros(parametros);
            List<Detallecertificaciones> conSaldo = new LinkedList<>();
            for (Detallecertificaciones d : aux) {
                if (valorSaldo(d.getAsignacion()) > 0) {
                    conSaldo.add(d);
                }
            }
            return Combos.getSelectItems(conSaldo, true);
            //return Combos.getSelectItems(aux, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cambiaValorImponible(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (vale.getImpuesto() != null) {
            BigDecimal ciento = new BigDecimal(100);
            vale.setIva(valor.multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
        }
    }

    public void cambiaValorImponibleCero(ValueChangeEvent ve) {
        BigDecimal valor = (BigDecimal) ve.getNewValue();
        DecimalFormat df = new DecimalFormat("0.00");
        if (vale.getImpuesto() != null) {
            BigDecimal ciento = new BigDecimal(100);
            vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
        }
    }

    public SelectItem[] getComboAutorizaciones() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.documento.codigo=:tipo");
        parametros.put("tipo", "LIQCOM");
        parametros.put(";orden", "o.inicial desc");
        try {
            List<Documentos> autorizaciones = ejbDocumentos.encontarParametros(parametros);
            return Combos.getSelectItems(autorizaciones, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboCuentas(Detallesvales detalle) {

        if (detalle.getDetallecertificacion() == null) {
            return null;
        }
        // solo para quitar el error
        String cuentaPresupuesto = detalle.getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
        Map parametros = new HashMap();
        parametros.put(";where", "o.presupuesto=:presupuesto");
        parametros.put("presupuesto", cuentaPresupuesto);
        try {
            List<Cuentas> cl = ejbCuentas.encontarParametros(parametros);
            return Combos.getSelectItems(cl, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

//    CALCULA EL VALOR DEL SALDO DE LOS DETALLES DE CERTIFICACION
    private double valorSaldo(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion");

        try {
            double reformas = ejbReformas.sumarCampo(parametros).doubleValue();
            double asignacion = a.getValor().doubleValue();
            double certificaciones = ejbDetallescertificacion.sumarCampo(parametros).doubleValue();
            double valorCompromisos = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
            if (detalle.getId() != null) {
                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put("asignacion", a);
                parametros.put("id", detalle.getId());
                parametros.put(";where", "o.asignacion=:asignacion and o.id!=:id");
                valorCompromisos = ejbDetallecompromiso.sumarCampoDoble(parametros);
            }
//            return asignacion + reformas - (certificaciones + valorCompromisos);
            return certificaciones - valorCompromisos;
//            if (compromiso.getCertificacion() == null) {
//                return asignacion + reformas - (certificaciones + valorCompromisos);
//            } else {
//                return certificaciones - valorCompromisos;
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void traerDetallesVale() {
        listaTodosDetalles = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", " o.vale.caja=:caja and o.vale.estado=2");
        parametros.put("caja", caja);
        try {
            listaTodosDetalles = ejbDetallesvale.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public SelectItem[] getComboEmpleados() {
        if (caja == null) {
            return null;
        }
        if (caja.getDepartamento() == null) {
            return null;
        }
        Map parametros = new HashMap();
        String where = "";
        where += " o.activo=true and o.cargoactual.organigrama=:organigrama";
        parametros.put("organigrama", caja.getDepartamento());
        parametros.put(";where", where);
//        parametros.put(";orden", " o.codigo");
        try {
            return Combos.getSelectItems(ejbEmpleados.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String sube() {
        int ajuste = vale.getAjuste();
        ajuste++;
        vale.setAjuste(ajuste);
        return null;
    }

    public String baja() {
        int ajuste = vale.getAjuste();
        ajuste--;
        vale.setAjuste(ajuste);
        return null;
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
    public Recurso getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Recurso reporte) {
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
     * @return the cajan
     */
    public Cajas getCajan() {
        return cajan;
    }

    /**
     * @param cajan the cajan to set
     */
    public void setCajan(Cajas cajan) {
        this.cajan = cajan;
    }

    /**
     * @return the vale
     */
    public Valescajas getVale() {
        return vale;
    }

    /**
     * @param vale the vale to set
     */
    public void setVale(Valescajas vale) {
        this.vale = vale;
    }

    /**
     * @return the listaVales
     */
    public List<Valescajas> getListaVales() {
        return listaVales;
    }

    /**
     * @param listaVales the listaVales to set
     */
    public void setListaVales(List<Valescajas> listaVales) {
        this.listaVales = listaVales;
    }

    /**
     * @return the formularioDetalleVale
     */
    public Formulario getFormularioDetalleVale() {
        return formularioDetalleVale;
    }

    /**
     * @param formularioDetalleVale the formularioDetalleVale to set
     */
    public void setFormularioDetalleVale(Formulario formularioDetalleVale) {
        this.formularioDetalleVale = formularioDetalleVale;
    }

    /**
     * @return the detalle
     */
    public Detallesvales getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallesvales detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the listaDetalle
     */
    public List<Detallesvales> getListaDetalle() {
        return listaDetalle;
    }

    /**
     * @param listaDetalle the listaDetalle to set
     */
    public void setListaDetalle(List<Detallesvales> listaDetalle) {
        this.listaDetalle = listaDetalle;
    }

    /**
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
    }

    /**
     * @return the estadonuevo
     */
    public Integer getEstadonuevo() {
        return estadonuevo;
    }

    /**
     * @param estadonuevo the estadonuevo to set
     */
    public void setEstadonuevo(Integer estadonuevo) {
        this.estadonuevo = estadonuevo;
    }

    /**
     * @return the formularioVales
     */
    public Formulario getFormularioVales() {
        return formularioVales;
    }

    /**
     * @param formularioVales the formularioVales to set
     */
    public void setFormularioVales(Formulario formularioVales) {
        this.formularioVales = formularioVales;
    }

    /**
     * @return the formularioCaja
     */
    public Formulario getFormularioCaja() {
        return formularioCaja;
    }

    /**
     * @param formularioCaja the formularioCaja to set
     */
    public void setFormularioCaja(Formulario formularioCaja) {
        this.formularioCaja = formularioCaja;
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
     * @return the listaTodosDetalles
     */
    public List<Detallesvales> getListaTodosDetalles() {
        return listaTodosDetalles;
    }

    /**
     * @param listaTodosDetalles the listaTodosDetalles to set
     */
    public void setListaTodosDetalles(List<Detallesvales> listaTodosDetalles) {
        this.listaTodosDetalles = listaTodosDetalles;
    }

    /**
     * @return the autorizacion
     */
    public Documentos getAutorizacion() {
        return autorizacion;
    }

    /**
     * @param autorizacion the autorizacion to set
     */
    public void setAutorizacion(Documentos autorizacion) {
        this.autorizacion = autorizacion;
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
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
    }

    /**
     * @return the listaCompromisos
     */
    public List<Compromisos> getListaCompromisos() {
        return listaCompromisos;
    }

    /**
     * @param listaCompromisos the listaCompromisos to set
     */
    public void setListaCompromisos(List<Compromisos> listaCompromisos) {
        this.listaCompromisos = listaCompromisos;
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
     * @return the obligacion
     */
    public Obligaciones getObligacion() {
        return obligacion;
    }

    /**
     * @param obligacion the obligacion to set
     */
    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    /**
     * @return the retencion
     */
    public Retencionescompras getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
    }

    /**
     * @return the listaRetenciones
     */
    public List<Retencionescompras> getListaRetenciones() {
        return listaRetenciones;
    }

    /**
     * @param listaRetenciones the listaRetenciones to set
     */
    public void setListaRetenciones(List<Retencionescompras> listaRetenciones) {
        this.listaRetenciones = listaRetenciones;
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
     * @return the formularioRetencion
     */
    public Formulario getFormularioRetencion() {
        return formularioRetencion;
    }

    /**
     * @param formularioRetencion the formularioRetencion to set
     */
    public void setFormularioRetencion(Formulario formularioRetencion) {
        this.formularioRetencion = formularioRetencion;
    }

    /////////////////NUEVO///////////////////////////////
    public String nuevaRetencion() {
        retencion = new Retencionescompras();
        retencion.setBien(true);
        retencion.setBaseimponible(new BigDecimal(BigInteger.ZERO));
        retencion.setBaseimponible0(new BigDecimal(BigInteger.ZERO));
        retencion.setIva(new BigDecimal(BigInteger.ZERO));
        retencion.setValor(new BigDecimal(BigInteger.ZERO));
        retencion.setValoriva(new BigDecimal(BigInteger.ZERO));
        formularioRetencion.insertar();

        return null;
    }

    public String modificaRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioRetencion.editar();
        return null;
    }

    public String borraRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioRetencion.eliminar();
        return null;
    }

    private boolean validarRetenciones() {
        double valor = 0;
        if (retencion.getRetencion() != null) {
            if ((retencion.getValorprima() == null) || (retencion.getValorprima().doubleValue() <= 0)) {
                valor = (retencion.getBaseimponible().doubleValue() + retencion.getBaseimponible0().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            } else {
                valor = (retencion.getValorprima().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            }
        }

        double iva = 0;
        if (retencion.getImpuesto() != null) {
            iva = retencion.getBaseimponible().doubleValue()
                    * retencion.getImpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteIva();
            iva += ajuste / 100;
        }
        double valorIva = 0;
        if (retencion.getRetencionimpuesto() != null) {
            valorIva = iva * retencion.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteRi();
            valorIva += ajuste / 100;
        }
        retencion.setIva(new BigDecimal(iva));
        retencion.setValor(new BigDecimal(valor));
        retencion.setValoriva(new BigDecimal(valorIva));
        return false;
    }

    public String insertarRetenciones() {
        if (validarRetenciones()) {
            return null;
        }
        try {
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, parametrosSeguridad.getLogueado().getUserid());
            }
            retencion.setObligacion(obligacion);
            ejbRetenciones.create(retencion, parametrosSeguridad.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public String grabarRetenciones() {
//        if (validarDetalle()) {
//            return null;
//        }
        try {
            ejbRetenciones.edit(retencion, parametrosSeguridad.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public String borrarRetenciones() {

        try {
            ejbRetenciones.remove(retencion, parametrosSeguridad.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(PagoRetencionesBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public void traerDetallesRetenciones() {
        listaRetenciones = new LinkedList<>();
        Map parametros = new HashMap();
        parametros.put(";where", " o.obligacion.vale.caja=:caja and o.vale.estado=2");
        parametros.put("caja", caja);
        try {
            listaRetenciones = ejbRetenciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void estaPartida(String partida, List<String> partidas) {
        if (partidas == null) {
            partidas = new LinkedList<>();
            partidas.add(partida);
            return;
        }
        for (String p : partidas) {
            if (p.equalsIgnoreCase(partida)) {
                return;
            }
        }
        partidas.add(partida);
    }

    public SelectItem[] getComboPartidas() {
        Formatos f = ejbDocumentos.traerFormato();
        if (f == null) {
            MensajesErrores.advertencia("Mal configurado formato");
            return null;
        }
        List<String> partidas = new LinkedList<>();
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
        if (listaDetalle == null) {
            return null;
        }
        int tamano = aux[f.getNivel() - 1].length();
        for (Detallesvales d : listaDetalle) {
            String cuentaPresupuesto = d.getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
            String presupuesto = cuentaPresupuesto.substring(0, tamano);
            estaPartida(presupuesto, partidas);
        }
        return Combos.comboStrings(partidas, false);
    }

    public SelectItem[] getComboRetenciones() {
        if (retencion == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false and o.bien=:bien");
        parametros.put("bien", retencion.getBien());
        try {
            return Combos.getSelectItems(ejbRetencionesSri.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(RetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboRetencionesImpuestos() {
        if (retencion == null) {
            return null;
        }
        boolean especial = false;
        if (obligacion.getProveedor() != null) {

            especial = obligacion.getProveedor().getEmpresa().getEspecial();
        } else {
            Proveedores pro = vale.getProveedor();
            if (pro != null) {
                obligacion.setProveedor(pro);
                especial = obligacion.getProveedor().getEmpresa().getEspecial();
            }
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true and o.bien=:bien and o.especial=:especial");
        parametros.put("especial", especial);
        parametros.put("bien", retencion.getBien());
        try {
            return Combos.getSelectItems(ejbRetencionesSri.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(RetencionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String regresarPresupuesto() {
        if (caja == null) {
            MensajesErrores.advertencia("Selecciones un Empleado");
            listaVales = new LinkedList<>();
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
            parametros.put("estado", 2);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(PresupuestoCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioRegresar().insertar();
        return null;
    }

    public String grabarRegreso() {
        try {
            if (!listaVales.isEmpty()) {
                for (Valescajas vc : listaVales) {
                    vc.setEstado(1);
                    ejbVales.edit(vc, parametrosSeguridad.getLogueado().getUserid());
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.vale=:vale");
                    parametros.put("vale", vc);
                    List<Detallesvales> lista = ejbDetallesvale.encontarParametros(parametros);
                    if (!lista.isEmpty()) {
                        for (Detallesvales dv : lista) {
                            ejbDetallesvale.remove(dv, parametrosSeguridad.getLogueado().getUserid());
                        }
                    }
                }
            }
        } catch (GrabarException | BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.cancelar();
        formularioVales.cancelar();
        return null;
    }

    /////////////////NUEVO///////////////////////////////
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
