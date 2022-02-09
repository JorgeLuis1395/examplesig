/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.LiquidaciondeCompras;
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
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ReposiscionesFacade;
import org.beans.sfccbdmq.SucursalesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagoLoteBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Perfiles;
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
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Reposisciones;
import org.entidades.sfccbdmq.Sucursales;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.personal.sfccbdmq.ValesCajaBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.presupuestos.sfccbdmq.ReporteReformasBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reposicionCajasSfccbdmq")
@ViewScoped
public class ReposicionCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public ReposicionCajasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioDetalleVale = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioVales = new Formulario();
    private Formulario formularioCaja = new Formulario();
    private Formulario formularioImrpimir = new Formulario();
    private Formulario formularioAuto = new Formulario();
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
    private Resource reporteComp;
    private Autorizaciones autorizacion2;
    private Autorizaciones autorizacionProveedor;
    private Reposisciones reposicion;
    private Impuestos impuesto;
    private Puntoemision puntoemision;
    private Sucursales sucursal;
    private Resource reporteCompromiso;

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
    @EJB
    private EmpresasFacade ejbEmpresas;
    @EJB
    private PuntoemisionFacade ejbPuntoemision;
    @EJB
    private SucursalesFacade ejbSucursales;
    @EJB
    private ReposiscionesFacade ejbReposisciones;
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
        String nombreForma = "AprobacionCajasVista";
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

    public String modificar(int fila) {
        vale = listaVales.get(fila);
//        if ((vale.getEstado().equals(0)) || (vale.getEstado().equals(2))) {
//            MensajesErrores.advertencia("Debe estar en estado de reposición para poder modificar");
//            return null;
//            //estadonuevo = vale.getEstado();
//        }
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
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            parametros.put("estado", 4);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
            if (listaVales != null) {
                formularioVales.insertar();
            }
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
//        if (cajan.getDepartamento() == null) {
//            MensajesErrores.advertencia("Departamento es necesario");
//            return true;
//        }
        if (cajan.getFecha() == null) {
            MensajesErrores.advertencia("Fecha obligatoria");
            return true;
        }
        if (cajan.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha debe ser mayor a periodo vigente");
            return true;
        }
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
        return false;
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
            List<Detallecertificaciones> aux = ejbDetallescertificacion.encontarParametros(parametros);
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

    public String grabar() {
        traerDetallesVale();
        try {
            if (listaTodosDetalles == null) {
                MensajesErrores.advertencia("No hay nada que grabar, vales de caja sin contabilizar todavía");
                return null;
            }
            if (listaTodosDetalles.isEmpty()) {
                MensajesErrores.advertencia("No hay nada que grabar, vales de caja sin contabilizar todavía");
                return null;
            }
            listaReglones = new LinkedList<>();
            listaReglonesInversion = new LinkedList<>();
            listaCompromisos = new LinkedList<>();
            listaDetallesC = new LinkedList<>();

            double valor = 0;
            for (Detallesvales dv : listaTodosDetalles) {
                Renglones r = new Renglones();
                Cuentas cuentaRmu = getCuentasBean().traerCodigo(dv.getCuenta().getCodigo());
                if (cuentaRmu.getAuxiliares() != null) {
                    r.setAuxiliar(dv.getVale().getCaja().getEmpleado().getEntidad().getPin());
                }
                r.setValor(dv.getValor());
//                valor += dv.getValor().negate().doubleValue();
                valor = dv.getValor().negate().doubleValue();
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

                Map parametros = new HashMap();
                parametros.put(";where", "o.escxp=true");
                List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
                String ctaInicio = "";
                String ctaFin = "";
                for (Formatos f : lFormatos) {
                    ctaInicio = f.getCxpinicio();
                    ctaFin = f.getCxpfin();
                }
//                String partida = traer53();
                String cuentaPresupuesto = dv.getDetallecertificacion().getAsignacion().getClasificador().getCodigo();
                String partida = cuentaPresupuesto.substring(0, 2);
                String cuentaGrabar = ctaInicio + partida + ctaFin;
                Cuentas cuentaRmu2 = getCuentasBean().traerCodigo(cuentaGrabar);
                if (cuentaRmu2 == null) {

                    MensajesErrores.advertencia("No existe cuenta x pagar empleado : " + cuentaGrabar);
                    return null;

                }
                Renglones r2 = new Renglones();
                if (cuentaRmu2.getAuxiliares() != null) {
                    r2.setAuxiliar(caja.getEmpleado().getEntidad().getPin());
                }
                r2.setValor(new BigDecimal(valor));
                r2.setReferencia(cajan.getReferencia());
                r2.setFecha(cajan.getFecha());
                r2.setCuenta(cuentaGrabar);
                estaEnRas(r2);
                inversiones(cuentaRmu2, r2, 1);
            }
            formulario.cancelar();
            formularioVales.cancelar();
            formularioCaja.insertar();
//            buscar();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        reposicion = new Reposisciones();
        reposicion.setBase(BigDecimal.ZERO);
        reposicion.setBase0(BigDecimal.ZERO);
        reposicion.setIva(BigDecimal.ZERO);
        reposicion.setNumerodocumento(0);
        reposicion.setAjuste(0);
        reposicion.setFecha(new Date());
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
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String confirmar() {
        if (validar()) {
            return null;
        }
        if (reposicion.getNumerodocumento() == null || reposicion.getNumerodocumento() == 0) {
            MensajesErrores.advertencia("Ingrese número de Documento");
            return null;
        }
        if (impuesto != null) {
            if (reposicion.getBase() == null) {
                reposicion.setBase(BigDecimal.ZERO);
            }
            double base = reposicion.getBase().setScale(2, RoundingMode.HALF_UP).doubleValue();
            double cuadre = impuesto.getPorcentaje().setScale(2).doubleValue() / 100;
            double ajuste = (double) reposicion.getAjuste() / 100;
            double producto = (base * cuadre) + ajuste;
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
        if (reposicion.getDocumento().getCodigo().equals("LIQCOM")) {
            try {
                Map parametros = new HashMap();
                parametros.put(";where", "o.numerodocumento=:numerodocumento");
                parametros.put("numerodocumento", reposicion.getNumerodocumento());
                List<Reposisciones> lista = ejbReposisciones.encontarParametros(parametros);
//                List<Cajas> lista = ejbCajas.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    MensajesErrores.advertencia("Número de Liquidación de Compras duplicado");
                    return null;
                } else {
                    caja.setNumeroliquidacion(reposicion.getNumerodocumento());
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            ejbReposisciones.create(reposicion, parametrosSeguridad.getLogueado().getUserid());
            ejbCajas.edit(caja, parametrosSeguridad.getLogueado().getUserid());

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
            parametros.put(";where", "o.modulo=:modulo and o.rubro=5");
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
            parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja and o.estado=4");
            parametros.put("caja", caja);
            List<Valescajas> aux = ejbVales.encontarParametros(parametros);
            if (aux.size() > 0) {
//                CREACION DE LA NUEVA CAJA
                double valor = 0;
                for (Detallesvales dv : listaTodosDetalles) {
                    valor += dv.getValor().doubleValue();
                }
//                cajan = new Cajas();
                cajan.setApertura(caja);
                cajan.setLiquidado(Boolean.FALSE);
                cajan.setJefe(caja.getJefe());
                cajan.setKardexbanco(null);
                cajan.setDepartamento(caja.getDepartamento());
                cajan.setEmpleado(caja.getEmpleado());
                cajan.setValor(new BigDecimal(valor));
                cajan.setPrcvale(caja.getPrcvale());
                cajan.setNumeroapertura(caja.getNumeroapertura() != null ? caja.getNumeroapertura() : 0);
                cajan.setNumeroreposicion(caja.getNumeroreposicion() != null ? caja.getNumeroreposicion() : 0);
                cajan.setNumeroliquidacion(caja.getNumeroliquidacion() != null ? caja.getNumeroliquidacion() : 0);
                ejbCajas.create(cajan, parametrosSeguridad.getLogueado().getUserid());
                for (Valescajas v : aux) {
                    v.setCaja(cajan);
                    v.setEstado(5);
                    v.setReposicion(reposicion);
                    v.setNumerocompra(reposicion.getNumerodocumento());
                    ejbVales.edit(v, parametrosSeguridad.getLogueado().getUserid());
                }
            }
            // CREACION DE CABECERA
            cabecera = new Cabeceras();
            cabecera.setFecha(cajan.getFecha());
            cabecera.setUsuario(parametrosSeguridad.getLogueado().getUserid());
            cabecera.setNumero(tipo.getUltimo() + 1);
            cabecera.setDescripcion(cajan.getObservaciones());
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
            int anioCertificacion = 0;
            int nume = 0;
            for (Detallesvales dv : listaTodosDetalles) {
                certificacion = dv.getDetallecertificacion().getCertificacion();
                anioCertificacion = dv.getDetallecertificacion().getCertificacion().getAnio();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
            int anioActual = calendar.get(Calendar.YEAR);
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
            c.setFecha(cajan.getFecha());
            c.setContrato(null);
            c.setImpresion(cajan.getFecha());
            c.setMotivo(cajan.getObservaciones());
            c.setProveedor(null);
            c.setEmpleado(empleado);
            c.setBeneficiario(caja.getEmpleado());
            c.setCertificacion(certificacion);
            c.setEmpleado(cajan.getEmpleado());
            c.setDireccion(cajan.getDepartamento());
            c.setNumerocomp(nume);
            Calendar fechaAnio = Calendar.getInstance();
            fechaAnio.setTime(cajan.getFecha());
            int anio = fechaAnio.get(Calendar.YEAR);
            c.setAnio(anio);
            ejbCompromisos.create(c, parametrosSeguridad.getLogueado().getUserid());
            codi.setParametros(nume + "");
            ejbCodigos.edit(codi, parametrosSeguridad.getLogueado().getUserid());
            for (Detallesvales dv : listaTodosDetalles) {
//            CREACION DE DETALLES COMPROMISOS
                Detallecompromiso dc = new Detallecompromiso();
                dc.setCompromiso(c);
                dc.setAsignacion(dv.getDetallecertificacion().getAsignacion());
                dc.setMotivo(cajan.getObservaciones());
                dc.setFecha(cajan.getFecha());
                dc.setSaldo(new BigDecimal(BigInteger.ZERO));
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
                cabecera.setFecha(cajan.getFecha());
                cabecera.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cabecera.setNumero(tipoReclasificacion.getUltimo() + 1);
                cabecera.setDescripcion(cajan.getObservaciones());
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
            int nroActual = autorizacion.getNumeroactual() + 1;
            autorizacion.setNumeroactual(nroActual);
            ejbDocumentos.edit(autorizacion, parametrosSeguridad.getLogueado().getUserid());
            Imprimir(cajan);
        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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

            if ((vale.getNumero() == null) || (vale.getNumero() <= 0)) {
                MensajesErrores.advertencia("Necesaria número de documento");
                return null;
            }
//            vale.setEstado(estadonuevo);
            if ((listaDetalle == null) || (listaDetalle.isEmpty())) {
                if (vale.getEstado() == 2) {
                    MensajesErrores.advertencia("No puede autorizar reembolso si no hay detalle del mismo");
                    vale.setEstado(1);
                    return null;
                }
            }

            BigDecimal ciento = new BigDecimal(100);
            if (vale.getImpuesto() != null) {
                vale.setIva(vale.getBaseimponible().multiply(vale.getImpuesto().getPorcentaje()).divide(ciento));
            } else {
                vale.setIva(new BigDecimal(BigInteger.ZERO));
            }
            ejbVales.edit(vale, parametrosSeguridad.getLogueado().getUserid());
            double aux = vale.getBaseimponiblecero().doubleValue() + vale.getBaseimponible().doubleValue() + vale.getIva().doubleValue();
            if (aux >= traerValor()) {
                for (Detallesvales ld : listaDetalle) {
                    ld.setVale(vale);
                    if (ld.getId() != null) {
                        ejbDetallesvale.edit(ld, parametrosSeguridad.getLogueado().getUserid());
                    } else {
                        ejbDetallesvale.create(ld, parametrosSeguridad.getLogueado().getUserid());
                    }
                }
            } else {
                MensajesErrores.advertencia("La suma de detalles de vale supera el valor de la factura");
                return null;
            }

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
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public SelectItem[] getComboAutorizaciones2() {
        if (caja.getEmpleado() == null) {
            return null;
        }
        if (reposicion.getDocumento() == null) {
            return null;
        }
        try {
            String ruc = caja.getEmpleado().getEntidad().getPin() + "001";
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

    public SelectItem[] getComboCuentas() {
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
        parametros.put(";where", " o.vale.caja=:caja and o.vale.estado=4");
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

    public String grabarEnHoja(Cajas caj) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja and o.reposicion.documento.codigo='LIQCOM'");
            parametros.put("caja", caj);
            List<Valescajas> listaValeLiq = ejbVales.encontarParametros(parametros);
            if (!listaValeLiq.isEmpty()) {
                LiquidaciondeCompras hoja = new LiquidaciondeCompras();
                hoja.llenar(caj, listaValeLiq);
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
     * @return the formularioImrpimir
     */
    public Formulario getFormularioImrpimir() {
        return formularioImrpimir;
    }

    /**
     * @param formularioImrpimir the formularioImrpimir to set
     */
    public void setFormularioImrpimir(Formulario formularioImrpimir) {
        this.formularioImrpimir = formularioImrpimir;
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

    public String crear() {
        try {
            if (caja.getEmpleado() == null) {
                MensajesErrores.advertencia("Seleccione un proveedor  primero");
                return null;
            }
            String ruc = caja.getEmpleado().getEntidad().getPin() + "001";
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

    public String insertar() {
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

    public String Imprimir(Cajas caj) {
        try {
            List<Valescajas> listaVale = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", caj);
            listaVale = ejbVales.encontarParametros(parametros);
            Valescajas vc = null;
            if (!listaVale.isEmpty()) {
                vc = listaVale.get(0);
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion='REPOSICION CAJA CHICA' and o.cabecera.tipo.nombre='CAJAS CHICAS'");
            parametros.put("idmodulo", caj.getId());
            List<Renglones> renglones = ejbRenglones.encontarParametros(parametros);
            if (renglones.isEmpty()) {
                MensajesErrores.advertencia("No existe reposicion de caja");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion='REPOSICION CAJA CHICA' and o.cabecera.tipo.nombre='ASIENTOS DE RECLASIFICACION'");
            parametros.put("idmodulo", caj.getId());
            List<Renglones> listaRenglonesReclasificacion = ejbRenglones.encontarParametros(parametros);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("REPOSICIÓN DE CAJA CHICA N° : " + caj.getId(), null, parametrosSeguridad.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();

            if (vc != null) {
                double subtotal = vc.getReposicion().getBase().doubleValue();
                double subtotal0 = vc.getReposicion().getBase0().doubleValue();
                double total = subtotal + vc.getReposicion().getIva().doubleValue() + subtotal0;

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo de Documento"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposicion().getDocumento().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Número de Documento"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposicion().getNumerodocumento().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Subtotal 12"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, subtotal));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Subtotal 0"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, subtotal0));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Iva"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposicion().getIva().doubleValue()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Valor"));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, total));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisión"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(vc.getReposicion().getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, caj.getEmpleado().toString()));

                pdf.agregarTabla(null, columnas, 2, 100, null);
                pdf.agregaParrafo("Concepto : " + vc.getReposicion().getDescripcion() + "\n\n");
            }
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            for (Renglones r : renglones) {
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
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION");
            pdf.agregaParrafo("\n\n");
            sumaDebitos = 0.0;
            sumaCreditos = 0.0;
            if (!listaRenglonesReclasificacion.isEmpty()) {
                columnas = new LinkedList<>();
                for (Renglones r : listaRenglonesReclasificacion) {
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
                }
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaDebitos));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, sumaCreditos));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "RECLASIFICACIÓN");
            }
            pdf.agregaParrafo("\n\n");
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
            String texto = "";
            for (Valescajas v : listaVale) {
                if (!v.getTipodocumento().getCodigo().equals("OTR")) {
                    if (v.getBaseimponible() == null) {
                        v.setBaseimponible(BigDecimal.ZERO);
                    }
                    if (v.getBaseimponiblecero() == null) {
                        v.setBaseimponiblecero(BigDecimal.ZERO);
                    }
                    if (v.getIva() == null) {
                        v.setIva(BigDecimal.ZERO);
                    }
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
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "REEMBOLSO");
            pdf.agregaParrafo("\n\n");

            //Formulario
            parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja and o.reposicion.documento.codigo='LIQCOM'");
            parametros.put("caja", caj);
            List<Valescajas> listaValeLiq = ejbVales.encontarParametros(parametros);
            if (!listaValeLiq.isEmpty()) {
                grabarEnHoja(caj);
            }
            //Compromiso
            parametros = new HashMap();
            parametros.put(";where", "o.vale.caja=:caja");
            parametros.put("caja", caj);
            List<Detallesvales> listaDetalles = ejbDetallesvale.encontarParametros(parametros);
            if (!listaDetalles.isEmpty()) {
                Detallesvales dv = listaDetalles.get(0);
                if (dv.getDetallecompromiso() != null) {
                    Detallecompromiso dc = dv.getDetallecompromiso();
                    texto = "Compromiso : " + dc.getCompromiso().getNumerocomp() + " [" + dc.getAsignacion().getProyecto().getCodigo() + " - " + dc.getAsignacion().getClasificador().getCodigo() + "]";

                    pdf.agregaParrafo(texto);
                    pdf.agregaParrafo("\n\n");
                }
                imprimirCompromiso(listaDetalles);
            }

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
            setReporte(pdf.traerRecurso());
            formularioImrpimir.insertar();

//        formulario.editar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirCompromiso(List<Detallesvales> listaDetalles) {
        Compromisos comp = null;
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        SimpleDateFormat anio = new SimpleDateFormat("yyyy");
        if (!listaDetalles.isEmpty()) {
            Detallesvales dv = listaDetalles.get(0);
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

    public String regresar() {
        if (caja == null) {
            MensajesErrores.advertencia("Selecciones un Empleado");
            listaVales = new LinkedList<>();
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
            parametros.put("estado", 4);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);
            if (lista == null) {
                MensajesErrores.advertencia("No existen Vales");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.insertar();

        return null;
    }

    public String grabarRegresar() {
        try {
            Map parametros = new HashMap();
            String where = "  o.caja=:caja and o.estado=:estado ";
            parametros.put("caja", caja);
            parametros.put("estado", 4);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);
            if (lista != null) {
                for (Valescajas vc : lista) {
                    vc.setEstado(2);
                    ejbVales.edit(vc, parametrosSeguridad.getLogueado().getUserid());
                }
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReposicionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.cancelar();
        return null;
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
