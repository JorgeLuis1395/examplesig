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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.LiquidaciondeCompras;
import org.beans.sfccbdmq.AutorizacionesFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.EmpresasFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ReposiscionesFacade;
import org.beans.sfccbdmq.SucursalesFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.codehaus.groovy.ast.ClassHelper;
import org.compras.sfccbdmq.AutorizacionesBean;
import org.compras.sfccbdmq.PagoLoteBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Autorizaciones;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallesvales;
import org.entidades.sfccbdmq.Documentos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Reposisciones;
import org.entidades.sfccbdmq.Sucursales;
import org.entidades.sfccbdmq.Valescajas;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.pagos.sfccbdmq.PagoCajaChicaBean;
import org.personal.sfccbdmq.ValesCajaBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteReposicionesCajasSfccbdmq")
@ViewScoped
public class ReporteReposicionesCajasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoCajaBean
     */
    public ReporteReposicionesCajasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioDetalleVale = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioVales = new Formulario();
    private Formulario formularioCaja = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private Formulario formularioImrpimir = new Formulario();
    private Formulario formularioReposicion = new Formulario();
    private Formulario formularioAuto = new Formulario();
    private Formulario formularioCom = new Formulario();

    private Cajas caja;
    private Cajas cajaBuscar;
    private List<Cajas> listaCajas;
    private Empleados empleado;
    private Organigrama direccion;
    private Resource reporte;
    private Resource reportePropuesta;
    private Recurso reporteReposicion;
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
    private List<Kardexbanco> listadoKardex;
    private Autorizaciones autorizacion2;
    private Autorizaciones autorizacionProveedor;
    private Reposisciones reposicion;
    private Impuestos impuesto;
    private Puntoemision puntoemision;
    private Sucursales sucursal;
    private Resource reporteComp;
    private Resource reporteCompromiso;
    private Recurso reporteARL;

    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private ValescajasFacade ejbVales;
    @EJB
    private DetallesvalesFacade ejbDetallesvale;
    @EJB
    private RenglonesFacade ejbRenglones;
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
    private DetallecompromisoFacade ejbDetallecompromiso;

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
        String nombreForma = "ReporteReposicionesCajasVista";
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
    }
    // fin perfiles

    public String buscarCajas() {
        if (cajaBuscar == null) {
            MensajesErrores.advertencia("Seleccione una caja");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.apertura =:caja";
            parametros.put("caja", cajaBuscar);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaCajas = ejbCajas.encontarParametros(parametros);
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            String where = "  o.caja=:caja ";
            parametros.put("caja", caja);
//            parametros.put("estado", 1);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
            if (listaVales != null) {
                formularioVales.insertar();
            }
//            estadonuevo = 2;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double traerValorVales(Cajas cajaVale) {
        Map parametros = new HashMap();
        String where = "  o.caja=:caja ";
        parametros.put("caja", cajaVale);
//            parametros.put("estado", 1);
        parametros.put(";where", where);
        parametros.put(";orden", "o.fecha desc");
        try {
            return ejbVales.sumarCampo(parametros).doubleValue();
//           
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

    //COMBO CAJAS 
    public SelectItem[] getComboCajas() {
        try {
            Map parametros = new HashMap();
            String where = "  o.apertura is null ";
            parametros.put(";where", where);
            parametros.put(";orden", "o.empleado.entidad.apellidos ");
//            listaCajas = ejbCajas.encontarParametros(parametros);
            return Combos.getSelectItems(ejbCajas.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
        return;
    }

    public String imprimirKardex() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        caja = listaCajas.get(formulario.getIndiceFila());
        listadoKardex = new LinkedList<>();

        try {
            Map parametros = new HashMap();
            String where = "  o.caja=:caja ";
            parametros.put("caja", caja);
//            parametros.put("estado", 1);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            listaVales = ejbVales.encontarParametros(parametros);
            Kardexbanco kn = null;
            for (Valescajas vc : listaVales) {
                if (vc.getCaja().getKardexbanco() == null) {
                    MensajesErrores.advertencia("No se puede imprimir el pago");
                    return null;
                } else {
                    kn = vc.getCaja().getKardexbanco();
                }
            }
            if (kn == null) {
                MensajesErrores.advertencia("No existe kardex banco");
            }
            listadoKardex.add(kn);
            if (listadoKardex.isEmpty() || listadoKardex == null) {
                MensajesErrores.advertencia("No existe pago");
                return null;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, parametrosSeguridad.getLogueado().getUserid());
            boolean segunda = false;
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (Kardexbanco k : listadoKardex) {
                if (segunda) {
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
                    pdf.agregarTabla(null, columnas, 3, 100, null);
                    pdf.finDePagina();
                }
                segunda = true;
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBanco().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Fecha :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(k.getFechamov())));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Cuenta T:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getCuentatrans()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Beneficiario :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco T :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, df.format(k.getValor())));
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("COMPROBANTE DE PAGO CAJA CHICA - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
                pdf.agregarTabla(null, columnas, 4, 100, null);

                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo(k.getObservaciones() + "\n\n");
                // asiento
                List<Renglones> renglones = traer(k);
                //
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
                double sumaDebitos = 0.0;
                double sumaCreditos = 0.0;
                //
                Collections.sort(renglones, new valorComparator());
                columnas = new LinkedList<>();
                for (Renglones r : renglones) {

                    String cuenta = "";
                    String auxiliar = r.getAuxiliar();
                    Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                    if (cta != null) {
                        cuenta = cta.getNombre();
                        if (cta.getAuxiliares() != null) {
                            switch (cta.getAuxiliares().getParametros()) {
                                case "P": {
                                    Empresas p = proveedorsBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                case "E":
                                    String e = empleadosBean.traerCedula(r.getAuxiliar());
                                    if (e != null) {
                                        auxiliar = e;

                                    }
                                    break;
                                case "C": {
                                    Empresas p = proveedorsBean.taerRuc(r.getAuxiliar());
                                    if (p != null) {
                                        auxiliar = p.toString();

                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuenta));
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, auxiliar));

                    double valor = r.getValor() == null ? 0 : r.getValor().doubleValue();
                    if (valor > 0) {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        sumaDebitos += valor;
                    } else {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, 0.0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(valor)));
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

            }
            // disponible el pagos
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporte = pdf.traerRecurso();
            imprimir();
            formularioReportes.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, parametrosSeguridad.getLogueado().getUserid());
            boolean segunda = false;
            for (Kardexbanco k : listadoKardex) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                if (segunda) {
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
                    pdf.agregarTabla(null, columnas, 3, 100, null);
                    pdf.finDePagina();
                }
                segunda = true;
                columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
                titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
//                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Anticipo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                double valorTotal = 0;
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                String numeroCuenta = k.getCuentatrans();
                Codigos codigo = codigosBean.traerCodigo("GASTGEN", "C");
                String numeroCuenta = codigo.getDescripcion();
                String tipoCuenta = k.getTcuentatrans().toString();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia().toString()));
                /////////////////////FIN EMPLEADO
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false,
                        k.getValor().doubleValue()));
                valorTotal += k.getValor().doubleValue();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("PROPUESTA DE PAGO - " + k.getEgreso());
                pdf.agregaParrafo("\n\n");
                pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
                pdf.agregarTabla(null, columnas, 3, 100, null);
            }
            reportePropuesta = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private List<Renglones> traer(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion like 'PAGO_CAJAS_CHICAS%'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public String insertarReposicion() {
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        caja = listaCajas.get(formulario.getIndiceFila());
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", caja);
            List<Valescajas> listaVale;
            listaVale = ejbVales.encontarParametros(parametros);
            Valescajas vc = null;
            if (!listaVale.isEmpty()) {
                vc = listaVale.get(0);
                if (vc.getReposicion() == null) {
                    if (caja.getEmpleado() == null) {
                        MensajesErrores.advertencia("Seleccione un proveedor  primero");
                        return null;
                    }

                    reposicion = new Reposisciones();
                    reposicion.setBase(BigDecimal.ZERO);
                    reposicion.setBase0(BigDecimal.ZERO);
                    reposicion.setIva(BigDecimal.ZERO);
                    reposicion.setNumerodocumento(0);
                    reposicion.setAjuste(0);
                    reposicion.setFecha(new Date());
                    formularioReposicion.insertar();
                } else {
                    imprimirReposicion(caja);
                }
            } else {
                MensajesErrores.advertencia("No existe reposicion de caja");
                return null;
            }
            imprimirSolicitud(caja);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarReposicion() {
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
        try {
            ejbReposisciones.create(reposicion, parametrosSeguridad.getLogueado().getUserid());

            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", caja);
            List<Valescajas> listaVale;
            listaVale = ejbVales.encontarParametros(parametros);
            if (!listaVale.isEmpty()) {
                for (Valescajas vc : listaVale) {
                    vc.setReposicion(reposicion);
                    ejbVales.edit(vc, parametrosSeguridad.getLogueado().getUserid());
                }
            }

        } catch (InsertarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimirReposicion(caja);
        formularioReposicion.cancelar();
        return null;
    }

    public String imprimirReposicion(Cajas caj) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.caja=:caja");
            parametros.put("caja", caj);
            List<Valescajas> listaVale = ejbVales.encontarParametros(parametros);
            Valescajas vc = null;
            if (!listaVale.isEmpty()) {
                vc = listaVale.get(0);
            } else {
                MensajesErrores.advertencia("No existe reposicion de caja");
                return null;
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
            double ivaModificar = 0;
            for (Valescajas v : listaVale) {
                if (!v.getTipodocumento().getCodigo().equals("OTR")) {
                    if (v.getIva() == null) {
                        v.setIva(BigDecimal.ZERO);
                    }
                    ivaModificar += v.getIva().doubleValue();
                }
            }

            if (vc != null) {
                if (vc.getReposicion() != null) {
                    if (vc.getReposicion().getBase() == null) {
                        vc.getReposicion().setBase(BigDecimal.ZERO);
                    }
                    if (vc.getReposicion().getBase0() == null) {
                        vc.getReposicion().setBase0(BigDecimal.ZERO);
                    }
                    double subtotal = vc.getReposicion().getBase().doubleValue();
                    double subtotal0 = vc.getReposicion().getBase0().doubleValue();
                    vc.getReposicion().setIva(new BigDecimal(ivaModificar));
                    double total = subtotal + vc.getReposicion().getIva().doubleValue() + subtotal0;
                    try {
                        ejbReposisciones.edit(vc.getReposicion(), parametrosSeguridad.getLogueado().getUserid());
                    } catch (GrabarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
                    }

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
            }
            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
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
            String texto = "";
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
            reporteReposicion = pdf.traerRecurso();
            formularioImrpimir.insertar();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoLoteBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            autorizacionProveedor = new Autorizaciones();
            autorizacionProveedor.setEmpresa(e);
            autorizacionProveedor.setFechaemision(new Date());
            autorizacionProveedor.setFechacaducidad(new Date());
            formularioAuto.insertar();
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
            ejbAutorizaciones.create(autorizacionProveedor, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAuto.cancelar();
        return null;
    }

    public SelectItem[] getComboAutorizaciones2() {
        if (caja == null) {
            return null;
        }
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

    public String imprimirSolicitud(Cajas caja) {
        try {

            Map parametros = new HashMap();
            String where = "  o.caja=:caja";
            parametros.put("caja", caja);
            parametros.put(";where", where);
            parametros.put(";orden", "o.fecha desc");
            List<Valescajas> lista = ejbVales.encontarParametros(parametros);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            org.auxiliares.sfccbdmq.DocumentoPDF pdf = new org.auxiliares.sfccbdmq.DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD ",
                    null, parametrosSeguridad.getLogueado().getUserid());
            pdf.agregaParrafoCompleto("SOLUCITUD DE APERTURA, REPOSICIÓN O LIQUIDACIÓN DEL FONDO FIJO DE CAJA CHICA", AuxiliarReporte.ALIGN_CENTER, 9, Boolean.TRUE);
            pdf.agregaTitulo("\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, true, "FORMULARIO AF-1"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 7, false, " No "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 7, false, caja.getNumeroreposicion() != null ? caja.getNumeroreposicion() + "" : ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            Date fecha;
            if (caja.getFechareembolso() == null) {
                fecha = new Date();
            } else {
                fecha = caja.getFechareembolso();
            }
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Lugar y Fecha :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Quito, " + sdf.format(fecha)));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Para :"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, caja.getEmpleado().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "De Estación:"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, caja.getDepartamento().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Unidad Administrativa"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaTitulo("\n");
            columnas = new LinkedList<>();
            double valor = 0;
            for (Valescajas vc : lista) {
                valor += vc.getValor().doubleValue();
            }
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "APERTURA "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "REPOSICIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "X"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO SOLICITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valor)));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "LIQUIDAIÓN "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "MONTO DEPOSITADO: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            pdf.agregaTitulo("\n");
            if (!lista.isEmpty()) {
                List<AuxiliarReporte> titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "TIPO DE DOCUMENTO (Factura,nota de Venta,Formulario AF-2,recibo)"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "NÚMERO"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "FECHA"));
                titulos.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, true, "CONCEPTO"));
                titulos.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, true, "PROVEEDOR"));
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "VALOR \n DESEMBOLSADO"));

                for (Valescajas vc : lista) {
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getTipodocumento().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getNumero().toString()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(vc.getFecha())));
                    columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getConcepto()));
                    columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, vc.getProveedor().getEmpresa().toString()));
                    columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(vc.getValor())));
                }

                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 15, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor Total Utilizado "));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valor)));
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, null);
            }

            pdf.agregaTitulo("\n");
            pdf.agregaParrafoCompleto("OBSERVACIONES: " + caja.getObservaciones(), AuxiliarReporte.ALIGN_LEFT, 6, Boolean.FALSE);
            pdf.agregaTitulo("\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "_________________________________"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "RESPONSABLE DEL FONDO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_CENTER, 6, false, "JEFE INMEDIATO"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre: " + caja.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "Nombre: "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "cc: " + caja.getEmpleado().getEntidad().getPin()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 6, false, "cc: "));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteARL = pdf.traerRecurso();
            formularioImrpimir.insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AperturaCajasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReposicionesCajasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the listadoKardex
     */
    public List<Kardexbanco> getListadoKardex() {
        return listadoKardex;
    }

    /**
     * @param listadoKardex the listadoKardex to set
     */
    public void setListadoKardex(List<Kardexbanco> listadoKardex) {
        this.listadoKardex = listadoKardex;
    }

    /**
     * @return the reportePropuesta
     */
    public Resource getReportePropuesta() {
        return reportePropuesta;
    }

    /**
     * @param reportePropuesta the reportePropuesta to set
     */
    public void setReportePropuesta(Resource reportePropuesta) {
        this.reportePropuesta = reportePropuesta;
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
     * @return the reporteReposicion
     */
    public Recurso getReporteReposicion() {
        return reporteReposicion;
    }

    /**
     * @param reporteReposicion the reporteReposicion to set
     */
    public void setReporteReposicion(Recurso reporteReposicion) {
        this.reporteReposicion = reporteReposicion;
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
     * @return the formularioReposicion
     */
    public Formulario getFormularioReposicion() {
        return formularioReposicion;
    }

    /**
     * @param formularioReposicion the formularioReposicion to set
     */
    public void setFormularioReposicion(Formulario formularioReposicion) {
        this.formularioReposicion = formularioReposicion;
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
     * @return the cajaBuscar
     */
    public Cajas getCajaBuscar() {
        return cajaBuscar;
    }

    /**
     * @param cajaBuscar the cajaBuscar to set
     */
    public void setCajaBuscar(Cajas cajaBuscar) {
        this.cajaBuscar = cajaBuscar;
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
     * @return the formularioCom
     */
    public Formulario getFormularioCom() {
        return formularioCom;
    }

    /**
     * @param formularioCom the formularioCom to set
     */
    public void setFormularioCom(Formulario formularioCom) {
        this.formularioCom = formularioCom;
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
     * @return the reporteARL
     */
    public Recurso getReporteARL() {
        return reporteARL;
    }

    /**
     * @param reporteARL the reporteARL to set
     */
    public void setReporteARL(Recurso reporteARL) {
        this.reporteARL = reporteARL;
    }

}
