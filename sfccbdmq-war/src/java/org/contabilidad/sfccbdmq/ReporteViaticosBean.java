/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.presupuestos.sfccbdmq.*;
import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DepositosFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Depositos;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.pagos.sfccbdmq.PagoCajaChicaBean;
import org.personal.sfccbdmq.ViaticosEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteViaticosSfccbdmq")
@ViewScoped
public class ReporteViaticosBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteViaticosBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private Formulario formularioLiquidacion = new Formulario();
    private Formulario formularioBorrar = new Formulario();
    private List<Detalleviaticos> listaDetalles;
    private List<Kardexbanco> listadoKardex;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private Resource reporte;
    private Resource reporteKardex;
    private Resource reportePropuesta;
    private Perfiles perfil;
    private Viaticosempleado viaticoEmpleado;
    private String tipoFecha = "o.viatico.desde";
    private Date desde;
    private Date hasta;
    private Integer tipoPartida;
    private Integer estado;
    private Viaticos viatico;
    private Resource reporteLiq;
    private Resource reporteDetalle;
    private Resource reporteComp;
    private String obs;

    private List<Viaticosempleado> listadoEmpleados;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{viaticosSfccbdmq}")
    private ViaticosBean viaticosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleados;
    @EJB
    private DetalleviaticosFacade ejbDetalleViatico;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private ViaticosFacade ejbViaticos;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private DepositosFacade ejbDepositos;
    @EJB
    private CompromisosFacade ejbcCompromisos;
    @EJB
    private CabecerasFacade ejbCabeceras;

    @PostConstruct
    private void activar() {
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteNivelesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
    }

    public String buscar() {
        try {
            listadoEmpleados = new LinkedList<>();
            Map parametros = new HashMap();
            String where = "" + tipoFecha + " between :desde and :hasta  and o.viatico.reembolso=false";

            if (tipoPartida != null) {
                where += " and o.viatico.partida=:partida";
                parametros.put("partida", consultaPartida(tipoPartida));
            }
            if (viatico != null) {
                where += " and o.viatico=:viatico";
                parametros.put("viatico", viatico);
            }
            if (estado != null) {
                if (estado == 0) {
                    where += " and o.estado=0";
                }
                if (estado == 1) {
                    where += " and o.estado=1";
                }
                if (estado == 2) {
                    where += " and o.estado=2";
                }
                if (estado == 3) {
                    where += " and o.estado=3";
                }
            }
            parametros.put(";where", where);
            parametros.put(";orden", tipoFecha + " desc, o.id desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);

            listadoEmpleados = ejbViaticosEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String detalle(Viaticosempleado ve) {
        listaDetalles = new LinkedList<>();
        this.viaticoEmpleado = ve;
        viaticosBean.setEmpleado(ve);
        Map parametros = new HashMap();
        parametros.put(";where", "o.viaticoempleado=:empleado ");
        parametros.put("empleado", viaticoEmpleado);
        try {
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String traerEstado(Integer ve) {
        if (ve != null) {
            switch (ve) {
                case -2:
                    return "No realizó viaje";
                case 0:
                    return "Asignado";
                case 1:
                    return "Pagado";
                case 2:
                    return "Validado";
                case 3:
                    return "Liquidado";
                default:
                    return "";
            }
        }
        return "";
    }

    public String traerTipo(String part) {
        if (part != null) {
            switch (part.trim()) {
                case "530304":
                    return "Externo";
                case "530303":
                    return "Interno";
                default:
                    return "";
            }
        }
        return "";
    }

    public String traerCompromiso(String dcS) {
        try {
            if (!(dcS == null || dcS.isEmpty())) {
                Detallecompromiso d;
                int dc = Integer.parseInt(dcS);
                Map parametros = new HashMap();
                parametros.put(";where", "o.id=:id");
                parametros.put("id", dc);
                List<Detallecompromiso> lista = ejbDetallecompromiso.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    d = lista.get(0);
                    return d.getCompromiso().getNumerocomp() + "";
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String imprimirKardex() {
        try {
            if (listadoEmpleados.isEmpty()) {
                MensajesErrores.advertencia("No existen empleados");
                return null;
            }
            listadoKardex = new LinkedList<>();
            for (Viaticosempleado ve : listadoEmpleados) {
                if (ve.getKardex() == null) {
                    MensajesErrores.advertencia("No se puede imprimir el pago");
                    return null;
                } else {
                    listadoKardex.add(ve.getKardex());
                }
            }
            if (listadoKardex.isEmpty() || listadoKardex == null) {
                MensajesErrores.advertencia("No existe pago a empleados");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
//            pdf.agregaParrafo("\n\n");
//            boolean segunda = false;
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (Viaticosempleado ve : listadoEmpleados) {
                if (ve.getKardex() != null) {
//                    if (segunda) {
//                        pdf.agregaParrafo("\n\n");
//                        pdf.agregaParrafo("\n\n");
//                        columnas = new LinkedList<>();
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Tesorero"));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
//                        pdf.finDePagina();
//                    }
//                    segunda = true;

                    columnas = new LinkedList<>();
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getKardex().getBanco().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Fecha :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(ve.getKardex().getFechamov())));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Cuenta T:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getKardex().getCuentatrans()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Beneficiario :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getKardex().getBeneficiario()));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Banco T :"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ve.getKardex().getBancotransferencia().toString()));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Valor:"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ConvertirNumeroALetras.convertNumberToLetter(ve.getKardex().getValor().doubleValue())));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, df.format(ve.getKardex().getValor())));
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaTitulo("COMPROBANTE DE PAGO VIÁTICOS - " + ve.getKardex().getEgreso());
                    pdf.agregaParrafo("\n\n");
                    pdf.agregarTabla(null, columnas, 4, 100, null);

                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo(ve.getKardex().getObservaciones() + "\n\n");
                    // asiento
                    List<Renglones> renglones = traer(ve.getKardex());
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
                                        Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
                                        if (p != null) {
                                            auxiliar = p.toString();

                                        }
                                        break;
                                    }
                                    case "E":
                                        String e = empleadoBean.traerCedula(r.getAuxiliar());
                                        if (e != null) {
                                            auxiliar = e;

                                        }
                                        break;
                                    case "C": {
                                        Empresas p = proveedoresBean.taerRuc(r.getAuxiliar());
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
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Tesorero"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director Financiero"));
                pdf.agregarTabla(null, columnas, 2, 100, null);
            }
            reporteKardex = pdf.traerRecurso();
            imprimir();
            getFormularioReportes().insertar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
//    *********************** OJOJOJOJO IMPRIMIR PROMESA

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());
//            boolean segunda = false;
            for (Kardexbanco k : listadoKardex) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
//                if (segunda) {
//                    pdf.finDePagina();
//                }
//                segunda = true;
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
                Codigos codigo = codigosBean.traerCodigo("GASTGEN", "V");
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
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='PAGO_VIATICOS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
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

    public String consultaPartida(int valor) throws ConsultarException {
        Codigos codviaticos = ejbViaticos.traerCodigo("GASTGEN", "V", null);
        if (codviaticos == null) {
            MensajesErrores.advertencia("Sin configuración de código en Gastos Generales");
            return null;
        }
        String[] registro = codviaticos.getParametros().split("#");
        if (registro != null) {
            return registro[valor];
        }
        return null;
    }

    public SelectItem[] getComboViaticos() {
        try {
            if (tipoPartida != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.partida=:partida and o.vigente=true");
                parametros.put(";orden", "o.lugar");
                parametros.put("partida", consultaPartida(tipoPartida));
                return Combos.getSelectItems(ejbViaticos.encontarParametros(parametros), true);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirLiq(Viaticosempleado ve) {
        reporteLiq = null;
        reporteComp = null;
        reporteDetalle = null;
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
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
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
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Pagado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contador"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            setReporteLiq(pdf.traerRecurso());
            if (ve.getDetallecompromiso() != null) {
                imprimirCompromiso(ve.getDetallecompromiso());
            }
            imprimirDetalle(ve);
            formularioLiquidacion.insertar();
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
            Detallecompromiso dc = lista.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            SimpleDateFormat anio = new SimpleDateFormat("yyyy");
            DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
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
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista/Jefe de Presupuesto"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteComp = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimirDetalle(Viaticosempleado ve) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", ve);
            List<Detalleviaticos> listaDetalle = ejbDetalleViatico.encontarParametros(parametros);
            List<Detalleviaticos> listaDetalleA = new LinkedList<>();
            List<Detalleviaticos> listaDetalleH = new LinkedList<>();
            List<Detalleviaticos> listaDetalle30 = new LinkedList<>();
            for (Detalleviaticos dv : listaDetalle) {
                if (dv.getTipo() == null) {
                    listaDetalle30.add(dv);
                } else {
                    if (!dv.getTipo()) {
                        listaDetalleA.add(dv);
                    } else {
                        if (dv.getTipocomprobante().equals("INGRESADO AUTOMATICAMENTE POR SISTEMA NO NECESIDAD DE VALES") || dv.getTipocomprobante().equals("30% ASIGNADO POR LEY")) {
                            listaDetalle30.add(dv);
                        } else {
                            listaDetalleH.add(dv);
                        }
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("REPORTE DE LIQUIDACIÓN DE VIATICOS");
            pdf.agregaParrafo("\n");

            List<AuxiliarReporte> columnas = new LinkedList<>();
            String detalle = ve.toString();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Detalle :   " + detalle));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            if (ve.getViatico().getPais() == null) {
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Valor viatico asignado :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(ve.getValor())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Valor a justificar 70% :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(ve.getValor().doubleValue() * 0.7)));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Valor asignado por ley 30% :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(ve.getValor().doubleValue() * 0.3)));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Liquidación según facturas adjuntas :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(getTotalViaticosValidados(listaDetalle))));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Liquidación validada según facturas validadas (70%) :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(getTotalViaticosValidados(listaDetalle))));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Diferencia :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(getTotalDiferencia(ve, listaDetalle))));
                pdf.agregarTabla(null, columnas, 4, 100, null);
            } else {
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Valor viatico asignado :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(ve.getValor())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Valor asignado por ley :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(ve.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Liquidación validada según facturas :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(getTotalViaticosValidados(listaDetalle))));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Diferencia :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, df.format(getTotalDiferencia(ve, listaDetalle))));
                pdf.agregarTabla(null, columnas, 4, 100, null);
            }

            pdf.agregaParrafo("\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Consumo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Lugar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo comprobante"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "N° Comprobante"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Detalle Validado"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor Validado"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Validado"));

            columnas = new LinkedList<>();
            //Alimentación
            if (listaDetalleA != null) {
                if (!listaDetalleA.isEmpty()) {
                    double totalValor = 0;
                    double totalValorValidado = 0;
                    for (Detalleviaticos dv : listaDetalleA) {
                        if (dv.getValor() == null) {
                            dv.setValor(BigDecimal.ZERO);
                        }
                        if (dv.getValorvalidado() == null) {
                            dv.setValorvalidado(BigDecimal.ZERO);
                        }
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "ALIMENTACIÓN"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(dv.getFecha())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getLugar() != null ? dv.getLugar() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getTipocomprobante() != null ? dv.getTipocomprobante() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getNrocomprobante() != null ? dv.getNrocomprobante() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getProveedor() != null ? dv.getProveedor() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getDetallevalidado() != null ? dv.getDetallevalidado() : ""));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(dv.getValor())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(dv.getValorvalidado())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getValidado() ? "VALIDADO" : "NO VALIDADO"));
                        totalValor += dv.getValor().doubleValue();
                        totalValorValidado += dv.getValorvalidado().doubleValue();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(totalValor)));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(totalValorValidado)));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    pdf.agregarTablaReporte(titulos, columnas, 9, 100, "ALIMENTACIÓN");
                    pdf.agregaParrafo("\n");
                }
            }
            columnas = new LinkedList<>();
            //Hospedaje
            if (listaDetalleH != null) {
                if (!listaDetalleH.isEmpty()) {
                    double totalValor = 0;
                    double totalValorValidado = 0;
                    for (Detalleviaticos dv : listaDetalleH) {
                        if (dv.getValor() == null) {
                            dv.setValor(BigDecimal.ZERO);
                        }
                        if (dv.getValorvalidado() == null) {
                            dv.setValorvalidado(BigDecimal.ZERO);
                        }
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "HOSPEDAJE"));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(dv.getFecha())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getLugar() != null ? dv.getLugar() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getTipocomprobante() != null ? dv.getTipocomprobante() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getNrocomprobante() != null ? dv.getNrocomprobante() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getProveedor() != null ? dv.getProveedor() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getDetallevalidado() != null ? dv.getDetallevalidado() : ""));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(dv.getValor())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(dv.getValorvalidado())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getValidado() ? "VALIDADO" : "NO VALIDADO"));
                        totalValor += dv.getValor().doubleValue();
                        totalValorValidado += dv.getValorvalidado().doubleValue();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(totalValor)));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(totalValorValidado)));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    pdf.agregarTablaReporte(titulos, columnas, 9, 100, "HOSPEDAJE");
                    pdf.agregaParrafo("\n");
                }
            }
            columnas = new LinkedList<>();
            //30%
            if (listaDetalle30 != null) {
                if (!listaDetalle30.isEmpty()) {
                    double totalValor = 0;
                    double totalValorValidado = 0;
                    for (Detalleviaticos dv : listaDetalle30) {
                        if (dv.getValor() == null) {
                            dv.setValor(BigDecimal.ZERO);
                        }
                        if (dv.getValorvalidado() == null) {
                            dv.setValorvalidado(BigDecimal.ZERO);
                        }
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(dv.getFecha())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getLugar() != null ? dv.getLugar() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getTipocomprobante() != null ? dv.getTipocomprobante() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getNrocomprobante() != null ? dv.getNrocomprobante() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getProveedor() != null ? dv.getProveedor() : ""));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getDetallevalidado() != null ? dv.getDetallevalidado() : ""));
//                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(dv.getValor())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(dv.getValorvalidado())));
                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, dv.getValidado() ? "VALIDADO" : "NO VALIDADO"));
                        totalValor += dv.getValor().doubleValue();
                        totalValorValidado += dv.getValorvalidado().doubleValue();
                    }
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(totalValor)));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(totalValorValidado)));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    pdf.agregarTablaReporte(titulos, columnas, 9, 100, "VALOR ASIGNADO POR LEY");
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
            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporteDetalle = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getTotalViaticosValidados(List<Detalleviaticos> lista) {
        double valor = 0;
        if (lista != null) {
            for (Detalleviaticos d : lista) {
                if (d.getValorvalidado() != null) {
                    if (d.getValor().doubleValue() != 0) {
                        valor += d.getValorvalidado().doubleValue();
                    }
                }
            }
        }
        return valor;
    }

    public double getTotalViaticosValidadosTotal(List<Detalleviaticos> lista) {
        double valor = 0;
        if (lista != null) {
            for (Detalleviaticos d : lista) {
                if (d.getValorvalidado() != null) {
                    valor += d.getValorvalidado().doubleValue();
                }
            }
        }
        return valor;
    }

    public double getTotalDiferencia(Viaticosempleado ve, List<Detalleviaticos> lista) {
        double valor = 0;
        if (ve != null) {
            valor = ve.getValor().doubleValue();
            return valor - getTotalViaticosValidadosTotal(lista);
        }
        return valor;
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

    public String imprimirLiquidacionViatico() {
        reporteLiq = null;
        buscar();
        try {
            if (listadoEmpleados.isEmpty()) {
                MensajesErrores.advertencia("No existen empleados");
                return null;
            }
            Date fecha = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            List<AuxiliarReporte> columnas;
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            for (Viaticosempleado ve : listadoEmpleados) {
                List<Renglones> listadoGasto = traerRenglonesGasto(ve);
                List<Renglones> listadoLiquidacion = traerRenglonesLiquidacion(ve);
                List<Renglones> listadoDeposito = new LinkedList<>();
                List<Renglones> listadoDepositoVarios = traerRenglonesDepositoVarios(ve);
                List<Renglones> listadoRenglonesVarios = traerRenglonesVarios(ve);
                if (ve.getKardexliquidacion() != null) {
                    listadoDeposito = traerRenglonesDeposito(ve);
                }
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
                        pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITOS");
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
                        pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DESCUENTOS");
                        pdf.agregaParrafo("\n");
                    }
                }
                pdf.agregaParrafo("\n");
                if (obs != null) {
                    pdf.agregaParrafo("Observaciones: " + obs);
                }
                pdf.agregaParrafo("Fecha: " + sdf.format(fecha));
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
            getFormularioLiquidacion().insertar();
            imprimirCompromisoCompleto();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimirCompromisoCompleto() {
        reporteComp = null;
        try {
            DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas;
            for (Viaticosempleado ve : listadoEmpleados) {
                if (ve.getDetallecompromiso() != null) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.id=:id");
                    parametros.put("id", ve.getDetallecompromiso());
                    List<Detallecompromiso> lista = ejbDetallecompromiso.encontarParametros(parametros);
                    if (lista.isEmpty()) {
                        MensajesErrores.advertencia("No existe compromiso para el usuario" + ve.getEmpleado().toString());
                        return null;
                    }
                    Detallecompromiso dc = lista.get(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
                    SimpleDateFormat anio = new SimpleDateFormat("yyyy");

                    pdf.agregaTitulo("INFORME DE COMPROMISO\n");
                    columnas = new LinkedList<>();
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

                }
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista/Jefe de Presupuesto"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Director Financiero"));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteComp = pdf.traerRecurso();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String borrarLiquidacion(Viaticosempleado ve) {
        viaticoEmpleado = ve;
        if (ve.getFechaliquidacion() != null) {
            String vale = ejbCabeceras.validarCierre(viaticoEmpleado.getFechaliquidacion());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
        }
        formularioBorrar.insertar();
        return null;
    }

    public String grabarBorrarLiquidacion() {
        try {
            if (viaticoEmpleado.getEstado().equals(-2) && !viaticoEmpleado.getRealizoviaje()) {
                viaticoEmpleado.setEstado(0);
                viaticoEmpleado.setRealizoviaje(Boolean.TRUE);
                ejbViaticosEmpleados.edit(viaticoEmpleado, seguridadbean.getLogueado().getUserid());
            } else {
                int dc = viaticoEmpleado.getDetallecompromiso() != null ? viaticoEmpleado.getDetallecompromiso() : 0;
                viaticoEmpleado.setFechaliquidacion(null);
                viaticoEmpleado.setEstado(2);
                viaticoEmpleado.setKardexliquidacion(null);
                viaticoEmpleado.setRenglonliquidacion(null);
                viaticoEmpleado.setDetallecompromiso(null);
                Map parametros = new HashMap();
                parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
                parametros.put("viaticoempleado", viaticoEmpleado);
                List<Depositos> listaDepositos = ejbDepositos.encontarParametros(parametros);
                if (!listaDepositos.isEmpty()) {
                    for (Depositos d : listaDepositos) {
                        ejbDepositos.remove(d, seguridadbean.getLogueado().getUserid());
                    }
                }
                ejbViaticosEmpleados.edit(viaticoEmpleado, seguridadbean.getLogueado().getUserid());
                parametros = new HashMap();
                parametros.put(";where", "o.id=:id");
                parametros.put("id", dc);
                List<Detallecompromiso> listaDetallesCompromisos = ejbDetallecompromiso.encontarParametros(parametros);
                if (!listaDetallesCompromisos.isEmpty()) {
                    Compromisos c = listaDetallesCompromisos.get(0).getCompromiso();
                    for (Detallecompromiso dcc : listaDetallesCompromisos) {
                        ejbDetallecompromiso.remove(dcc, seguridadbean.getLogueado().getUserid());
                    }
                    ejbcCompromisos.remove(c, seguridadbean.getLogueado().getUserid());
                }
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera.idmodulo=:idmodulo and o.cabecera.opcion in ('GASTO_VIATICOS','LIQUIDACION_VIATICOS','VIATICOS_DEPOSITO_DESCONOCIDO')");
                parametros.put("idmodulo", viaticoEmpleado.getId());
                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
                if (!listaRenglones.isEmpty()) {
                    Cabeceras ca = listaRenglones.get(0).getCabecera();
                    for (Renglones r : listaRenglones) {
                        ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                }
                parametros = new HashMap();
                parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion in ('GASTO_VIATICOS','LIQUIDACION_VIATICOS','VIATICOS_DEPOSITO_DESCONOCIDO')");
                parametros.put("idmodulo", viaticoEmpleado.getId());
                List<Cabeceras> listaCabeceras = ejbCabeceras.encontarParametros(parametros);
                if (!listaCabeceras.isEmpty()) {
                    for (Cabeceras caa : listaCabeceras) {
                        ejbCabeceras.remove(caa, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
        } catch (BorrarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioBorrar.cancelar();
        return null;
    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
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
     * @return the listadoEmpleados
     */
    public List<Viaticosempleado> getListadoEmpleados() {
        return listadoEmpleados;
    }

    /**
     * @param listadoEmpleados the listadoEmpleados to set
     */
    public void setListadoEmpleados(List<Viaticosempleado> listadoEmpleados) {
        this.listadoEmpleados = listadoEmpleados;
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
     * @return the proveedoresBean
     */
    public ProveedoresBean getProveedoresBean() {
        return proveedoresBean;
    }

    /**
     * @param proveedoresBean the proveedoresBean to set
     */
    public void setProveedoresBean(ProveedoresBean proveedoresBean) {
        this.proveedoresBean = proveedoresBean;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the reporteKardex
     */
    public Resource getReporteKardex() {
        return reporteKardex;
    }

    /**
     * @param reporteKardex the reporteKardex to set
     */
    public void setReporteKardex(Resource reporteKardex) {
        this.reporteKardex = reporteKardex;
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
     * @return the tipoPartida
     */
    public Integer getTipoPartida() {
        return tipoPartida;
    }

    /**
     * @param tipoPartida the tipoPartida to set
     */
    public void setTipoPartida(Integer tipoPartida) {
        this.tipoPartida = tipoPartida;
    }

    /**
     * @return the viatico
     */
    public Viaticos getViatico() {
        return viatico;
    }

    /**
     * @param viatico the viatico to set
     */
    public void setViatico(Viaticos viatico) {
        this.viatico = viatico;
    }

    /**
     * @return the estado
     */
    public Integer getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Integer estado) {
        this.estado = estado;
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
     * @return the formularioLiquidacion
     */
    public Formulario getFormularioLiquidacion() {
        return formularioLiquidacion;
    }

    /**
     * @param formularioLiquidacion the formularioLiquidacion to set
     */
    public void setFormularioLiquidacion(Formulario formularioLiquidacion) {
        this.formularioLiquidacion = formularioLiquidacion;
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
     * @return the formularioBorrar
     */
    public Formulario getFormularioBorrar() {
        return formularioBorrar;
    }

    /**
     * @param formularioBorrar the formularioBorrar to set
     */
    public void setFormularioBorrar(Formulario formularioBorrar) {
        this.formularioBorrar = formularioBorrar;
    }

    /**
     * @return the reporteDetalle
     */
    public Resource getReporteDetalle() {
        return reporteDetalle;
    }

    /**
     * @param reporteDetalle the reporteDetalle to set
     */
    public void setReporteDetalle(Resource reporteDetalle) {
        this.reporteDetalle = reporteDetalle;
    }

}
