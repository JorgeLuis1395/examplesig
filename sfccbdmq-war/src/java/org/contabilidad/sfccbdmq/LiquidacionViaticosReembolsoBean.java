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
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
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
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.CuentasFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ReposiscionesFacade;
import org.beans.sfccbdmq.SucursalesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Reposisciones;
import org.entidades.sfccbdmq.Sucursales;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "liquidacionViaticosReembolsoSfccbdmq")
@ViewScoped
public class LiquidacionViaticosReembolsoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public LiquidacionViaticosReembolsoBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private Formulario formularioAuto = new Formulario();
    private Formulario formularioRegresar = new Formulario();
    private Viaticosempleado viaticoEmpleado;
    private List<Detalleviaticos> listaDetalles = new LinkedList<>();
    private Resource reporte;
    private Resource reporteComp;
    private List<Renglones> listaReglones;
    private Cabeceras cabecera;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private Reposisciones reposicion;
    private Impuestos impuesto;
    private Puntoemision puntoemision;
    private Sucursales sucursal;
    private Autorizaciones autorizacion2;
    private Autorizaciones autorizacionProveedor;
    private Date fecha = new Date();
    @EJB
    private DetalleviaticosFacade ejbDetalleViatico;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private ViaticosFacade ejbViaticos;
    @EJB
    private DetallecertificacionesFacade ejbDetalleCertificacion;
    @EJB
    private CertificacionesFacade ejbCertificaciones;
    @EJB
    private CuentasFacade ejbCuentas;
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

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
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

            listaReglones = null;
            //liquidacion de compras
            reposicion = new Reposisciones();
            reposicion.setBase(BigDecimal.ZERO);
            reposicion.setBase0(BigDecimal.ZERO);
            reposicion.setIva(BigDecimal.ZERO);
            reposicion.setNumerodocumento(0);
            reposicion.setAjuste(0);
            reposicion.setFecha(new Date());
            reposicion.setDocumento(null);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            viaticoEmpleado.setFechaliquidacion(fecha);
            Map parametros = new HashMap();
            parametros.put(";where", "o.presupuesto=:presupuesto and o.activo=true");
            parametros.put("presupuesto", viaticoEmpleado.getViatico().getPartida());
            List<Cuentas> listaCuentas = ejbCuentas.encontarParametros(parametros);
            Cuentas cuentaViaticos = null;
            if (!listaCuentas.isEmpty()) {
                cuentaViaticos = listaCuentas.get(0);
            }
            cuentasBean.setCuenta(cuentaViaticos);
            if (cuentasBean.validaCuentaMovimiento()) {
                return null;
            }
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
            String partida = viaticoEmpleado.getViatico().getPartida().substring(0, 2);
            String cuenta = ctaInicio + partida + ctaFin;
            Cuentas cuentaEmpleado = cuentasBean.traerCodigo(cuenta);
            cuentasBean.setCuenta(cuentaEmpleado);
            if (cuentasBean.validaCuentaMovimiento()) {
                MensajesErrores.advertencia("Cuenta contable de empleado  mal configurada : " + cuenta);
                return null;
            }
            String vale = ejbCabecera.validarCierre(fecha);
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            // Traer la cuenta con la partida
            Cabeceras cab = new Cabeceras();
            if (definitivo) {
                cab.setDescripcion(viaticoEmpleado.getViatico().getMotivo());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(ta);
                cab.setNumero(numero);
                cab.setFecha(fecha);
                cab.setIdmodulo(viaticoEmpleado.getId());
                cab.setUsuario(parametrosSeguridad.getLogueado().getUserid());
                cab.setOpcion("GASTO_VIATICOS");
                ejbCabecera.create(cab, parametrosSeguridad.getLogueado().getUserid());
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
                rGasto.setReferencia(viaticoEmpleado.getViatico().getMotivo());
                rGasto.setValor(new BigDecimal(valoValidado));
                rGasto.setCuenta(cuentaViaticos.getCodigo());
                rGasto.setPresupuesto(cuentaViaticos.getPresupuesto());
                rGasto.setFecha(fecha);
                if (cuentaViaticos.getAuxiliares() != null) {
                    rGasto.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
                }
                Renglones rPorPagar = new Renglones(); // reglon por pagar
                if (definitivo) {
                    rPorPagar.setCabecera(cab);
                }
                rPorPagar.setReferencia(viaticoEmpleado.getViatico().getMotivo());
                rPorPagar.setValor(new BigDecimal(valoValidado * -1));
                rPorPagar.setCuenta(cuentaEmpleado.getCodigo());
                rPorPagar.setPresupuesto(cuentaEmpleado.getPresupuesto());
                rPorPagar.setFecha(fecha);
                if (cuentaEmpleado.getAuxiliares() != null) {
                    rPorPagar.setAuxiliar(viaticoEmpleado.getEmpleado().getEntidad().getPin());
                }
                listaReglones.add(rGasto);
                listaReglones.add(rPorPagar);
                if (definitivo) {
                    viaticoEmpleado.setEstado(3);
                    ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(rGasto, parametrosSeguridad.getLogueado().getUserid());
                    ejbRenglones.create(rPorPagar, parametrosSeguridad.getLogueado().getUserid());
                }

            }

        } catch (GrabarException | InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        if (viaticoEmpleado.getFechaliquidacion().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Es necesario fecha de movimiento válido mayor o igual a periodo vigente");
            return true;
        }
        return false;
    }

    public String insertar() {
        try {
            viaticoEmpleado.setFechaliquidacion(fecha);
            if (validar()) {
                return null;
            }
            contabilizar(true);
            viaticoEmpleado.setEstado(3);
            ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
            if (reposicion.getDocumento() != null) {
                grabarReposicion();
            }
            imprimir(viaticoEmpleado);
            imprimirCompromiso(viaticoEmpleado.getDetallecompromiso());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        viaticoEmpleado = null;
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
            Map parametros;
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

    public List<Renglones> getListaReglonesPreliminar() {
        contabilizar(false);
        return listaReglones;
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

    public SelectItem[] getComboEmpleadosViaticosDirecto() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico.vigente=true and o.estado=2 and o.kardexliquidacion is null  and o.viatico.reembolso=true");
            List<Viaticosempleado> lista = ejbViaticosempleado.encontarParametros(parametros);
            return Combos.getSelectItems(lista, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir(Viaticosempleado ve) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            List<AuxiliarReporte> columnas;
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, parametrosSeguridad.getLogueado().getUserid());
            List<Renglones> listadoGasto = traerRenglonesGasto(ve);
            pdf.agregaTitulo("LIQUIDACIÓN DE VIATICOS : " + ve.getId());
            pdf.agregaParrafo("\n");
            //Detalles de la asignacion del viatico
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor Asignado :   " + df.format(ve.getValor())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Beneficiario :  " + ve.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha desde:   " + (sdf.format(ve.getDesde()))));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha hasta:   " + (sdf.format(ve.getHasta()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaParrafo("\n");
            columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
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

            pdf.agregaParrafo("\n");
            pdf.agregaParrafo("Observaciones: " + ve.getViatico().getMotivo());
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
            reporte = pdf.traerRecurso();
            formularioReportes.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
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

            pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + dc.getCompromiso().getMotivo());
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
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
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
            viaticoEmpleado.setDetallecompromiso(null);
            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso.certificacion=:certificacion");
            parametros.put("certificacion", viaticoEmpleado.getViatico().getCertificacion());
            List<Detallecompromiso> listaDetalle = ejbDetallecompromiso.encontarParametros(parametros);
            Compromisos comp = null;
            if (!listaDetalle.isEmpty()) {
                comp = listaDetalle.get(0).getCompromiso();
            }
            for (Detallecompromiso dc : listaDetalle) {
                ejbDetallecompromiso.remove(dc, parametrosSeguridad.getLogueado().getUserid());
            }
            if (comp != null) {
                ejbCompromisos.remove(comp, parametrosSeguridad.getLogueado().getUserid());
            }
            parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", viaticoEmpleado.getViatico().getCertificacion());
            List<Detallecertificaciones> lista = ejbDetalleCertificacion.encontarParametros(parametros);
            Certificaciones c = null;
            if (!listaDetalle.isEmpty()) {
                c = lista.get(0).getCertificacion();
            }
            viaticoEmpleado.getViatico().setCertificacion(null);
            for (Detallecertificaciones dc : lista) {
                ejbDetalleCertificacion.remove(dc, parametrosSeguridad.getLogueado().getUserid());
            }
            if (c != null) {
                ejbCertificaciones.remove(c, parametrosSeguridad.getLogueado().getUserid());
            }
            ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
            ejbViaticos.edit(viaticoEmpleado.getViatico(), parametrosSeguridad.getLogueado().getUserid());
            viaticoEmpleado.setEstado(2);
            viaticoEmpleado.setFechaliquidacion(null);
            parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado");
            parametros.put("empleado", viaticoEmpleado);
            List<Detalleviaticos> listaDetallesBorrar = ejbDetalleViatico.encontarParametros(parametros);
            for (Detalleviaticos dv : listaDetallesBorrar) {
                dv.setValidado(false);
                dv.setValorvalidado(null);
                ejbDetalleViatico.edit(dv, parametrosSeguridad.getLogueado().getUserid());
            }
            ejbViaticosempleado.edit(viaticoEmpleado, parametrosSeguridad.getLogueado().getUserid());
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BorrarException ex) {
            Logger.getLogger(LiquidacionViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioRegresar.cancelar();
        return null;
    }

    public double getTotalViaticos() {
        double valor = 0;
        if (listaDetalles != null) {
            for (Detalleviaticos d : listaDetalles) {
                valor += d.getValor().doubleValue();
            }
        }
        return valor;
    }

    public double getTotalViaticosValidados() {
        double valor = 0;
        if (listaDetalles != null) {
            for (Detalleviaticos d : listaDetalles) {
                if (d.getValorvalidado() != null) {
                    valor += d.getValorvalidado().doubleValue();
                }
            }
        }
        return valor;
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

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
