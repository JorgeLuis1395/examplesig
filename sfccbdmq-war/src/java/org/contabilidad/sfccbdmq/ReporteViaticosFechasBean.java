/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

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
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DepositosFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Depositos;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.pagos.sfccbdmq.KardexPagosBean;
import org.pagos.sfccbdmq.PagoCajaChicaBean;
import org.presupuestos.sfccbdmq.ProyectosBean;
import org.presupuestos.sfccbdmq.ReporteReformasBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "reporteViaticosFechasSfccbdmq")
@ViewScoped
public class ReporteViaticosFechasBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public ReporteViaticosFechasBean() {
    }

    private Formulario formulario = new Formulario();
    private Formulario formularioReportes = new Formulario();
    private Formulario formularioImpresion = new Formulario();

    private Viaticosempleado viaticosEmpleado;
    private List<Viaticosempleado> listaViaticosempleado;
    private List<Detalleviaticos> listaDetalleViaticos;
    private List<AuxiliarCarga> renglonesAuxiliar;
    private List<AuxiliarCarga> renglonesRetencion;
    private List<Kardexbanco> listadoKardex;
    private Resource reporte;
    private Resource reporteKardex;
    private Resource reportePropuesta;
    private Perfiles perfil;
    private Date desde;
    private Date hasta;
    private int pago = 3;
    private String egreso;
    private Resource reporteCompromiso;
    private Resource reporteComp;
    private String tipoFecha = "o.viatico.fecha";

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private DetalleviaticosFacade ejbDetalleviaticos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private DepositosFacade ejbDepositos;
    @EJB
    private CabecerasFacade ejbCabeceras;

    public String buscar() {

        Map parametros = new HashMap();

        String where = "" + getTipoFecha() + " between :desde and :hasta  and o.viatico.reembolso=false";

        if (empleadoBean.getEntidad() != null) {
            where += " and o.empleado=:empleado";
            parametros.put("empleado", empleadoBean.getEntidad().getEmpleados());
        }
        //0 = no --- 1 = si
        if (pago == 0) {
            where += " and o.kardex is null";
        } else if (pago == 1) {
            where += " and o.kardex is not null";
        }
        if (!(egreso == null || egreso.isEmpty())) {
            where += " and o.observaciones like :egreso";
            parametros.put("egreso", "%" + egreso + "%");
        }

        parametros.put(";where", where);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.empleado.entidad.apellidos,o.viatico.fecha desc");

        try {
            listaViaticosempleado = ejbViaticosempleado.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public String traerEstado(Integer ve) {
//        if (ve != null) {
//            switch (ve) {
//                case 0:
//                    return "Asignado";
//                case 1:
//                    return "Pagado";
//                case 2:
//                    return "Validado";
//                case 3:
//                    return "Liquidado";
//                default:
//                    return "";
//            }
//        }
//        return "";
//    }
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
            MensajesErrores.fatal("Sin perfil v??lido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil v??lido");
            seguridadbean.cerraSession();
        }
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
    }

    public String detalle(Viaticosempleado ve) {
        try {
            listaDetalleViaticos = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", ve);
            listaDetalleViaticos = ejbDetalleviaticos.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();

        return null;
    }

    public String imprimirGasto(Viaticosempleado caj) {
        viaticosEmpleado = caj;
        try {
            List<Renglones> renglonesGasto = traerRenglonesGasto();
            List<Renglones> renglonesLiquidacion = traerRenglonesLiquidacion();
            List<Renglones> renglonesReclasificacion = traerRenglonesReclasificacion();
            List<Renglones> listadoDeposito = new LinkedList<>();
            List<Renglones> listadoDepositoVarios = traerRenglonesDepositoVarios(viaticosEmpleado);
            List<Renglones> listadoRenglonesVarios = traerRenglonesVarios(viaticosEmpleado);
            if (viaticosEmpleado.getKardexliquidacion() != null) {
                listadoDeposito = traerRenglonesDeposito(viaticosEmpleado);
            }

            List<Detalleviaticos> listaVale = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado and o.estado=6");
            parametros.put("viaticoempleado", viaticosEmpleado);
            listaVale = ejbDetalleviaticos.encontarParametros(parametros);
            Detalleviaticos vc = null;
            if (!listaVale.isEmpty()) {
                vc = listaVale.get(0);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("LIQUIDACI??N DE FONDO NO : " + viaticosEmpleado.getId());
            pdf.agregaParrafo("\n");

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Valor Asignado :   " + df.format(viaticosEmpleado.getValor())));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Beneficiario :  " + viaticosEmpleado.getEmpleado().toString()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, "Fecha:   " + (sdf.format(viaticosEmpleado.getViatico().getFecha()))));
            pdf.agregarTabla(null, columnas, 1, 100, null);
            pdf.agregaParrafo("\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            columnas = new LinkedList<>();

            if (vc != null) {
                if (vc.getReposicion() != null) {
                    if (vc.getReposicion().getBase() == null) {
                        vc.getReposicion().setBase(BigDecimal.ZERO);
                    }
                    if (vc.getReposicion().getBase0() == null) {
                        vc.getReposicion().setBase0(BigDecimal.ZERO);
                    }
                    if (vc.getReposicion().getIva() == null) {
                        vc.getReposicion().setIva(BigDecimal.ZERO);
                    }
                    double subtotal = vc.getReposicion().getBase().doubleValue();
                    double subtotal0 = vc.getReposicion().getBase0().doubleValue();
                    double total = subtotal + vc.getReposicion().getIva().doubleValue() + subtotal0;

                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo de Documento"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposicion().getDocumento().getNombre()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "N??mero de Documento"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposicion().getNumerodocumento().toString()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Subtotal 12"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, subtotal));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Subtotal 0"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, subtotal0));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Iva"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, vc.getReposicion().getIva().doubleValue()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Valor"));
                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, total));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha de emisi??n"));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(vc.getReposicion().getFecha())));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, viaticosEmpleado.getEmpleado().toString()));

                    pdf.agregarTabla(null, columnas, 2, 100, null);
                    pdf.agregaParrafo("Concepto : " + vc.getReposicion().getDescripcion() + "\n\n");
                }
            }
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "D??bitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Cr??ditos"));
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION GASTO N?? " + c.getNumero());
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "CONTABILIZACION RECLASIFICACION N?? " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            // Liquidacion
            if (renglonesLiquidacion != null) {
                if (!renglonesLiquidacion.isEmpty()) {
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "LIQUIDACION N?? " + c.getNumero());
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITO  N?? " + c.getNumero());
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITOS N?? " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
            }
            //Descuento por Rol
            if (viaticosEmpleado.getRenglonliquidacion() != null) {
                titulos = new LinkedList<>();
                columnas = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Centro de Costo"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(viaticosEmpleado.getRenglonliquidacion().getCabecera().getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, viaticosEmpleado.getRenglonliquidacion().getCuenta()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, cuentasBean.traerCodigo(viaticosEmpleado.getRenglonliquidacion().getCuenta()).getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, viaticosEmpleado.getRenglonliquidacion().getCentrocosto()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, viaticosEmpleado.getRenglonliquidacion().getAuxiliar()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(viaticosEmpleado.getRenglonliquidacion().getValor())));

                c = viaticosEmpleado.getRenglonliquidacion().getCabecera();
                pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DESCUENTO POR ROL N?? " + c.getNumero());
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
                    pdf.agregarTablaReporte(titulos, columnas, 6, 100, "DEPOSITOS N?? " + c.getNumero());
                    pdf.agregaParrafo("\n");
                }
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

            pdf.agregaParrafo("\n");

            sumaDebitos = 0;
            sumaCreditos = 0;
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            double totalCompromiso = 0;
            parametros = new HashMap();
            parametros.put(";where", "o.vale.viaticosEmpleado=:vale");
            parametros.put("vale", viaticosEmpleado);
            List<Detalleviaticos> listaDetalleViaticosempleado = ejbDetalleviaticos.encontarParametros(parametros);
            for (Detalleviaticos d : listaDetalleViaticosempleado) {
                columnas.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getViaticoempleado().getDetallecompromiso().toString()));
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

//            //Formulario
//            parametros = new HashMap();
//            parametros.put(";where", "o.viaticosEmpleado=:viaticosEmpleado and o.reposiscion.documento.codigo='LIQCOM'");
//            parametros.put("viaticosEmpleado", viaticosEmpleado);
//            List<Detalleviaticos> listaValeLiq = ejbValesViaticosempleado.encontarParametros(parametros);
//            if (!listaValeLiq.isEmpty()) {
//                grabarEnHoja(viaticosEmpleado);
//            }
            //Compromiso
            String texto2 = "";
            parametros = new HashMap();
            parametros.put(";where", "o.vale.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", viaticosEmpleado);
            List<Detalleviaticos> listaDetalles = ejbDetalleviaticos.encontarParametros(parametros);
            if (!listaDetalles.isEmpty()) {
                Detalleviaticos dv = listaDetalles.get(0);
                if (dv.getViaticoempleado().getDetallecompromiso() != null) {
//                    Detallecompromiso dc = dv.getViaticoempleado().getDetallecompromiso();
//                    texto2 = "Compromiso : " + dc.getCompromiso().getNumerocomp() + " [" + dc.getAsignacion().getProyecto().getCodigo() + " - " + dc.getAsignacion().getClasificador().getCodigo() + "]";

                    pdf.agregaParrafo(texto2);
                    pdf.agregaParrafo("\n\n");
                }
//                imprimirCompromiso(listaDetalles);
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

//    public String grabarEnHoja(Viaticosempleado caj) {
//        try {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.viaticosEmpleado=:viaticosEmpleado and o.reposiscion.documento.codigo='LIQCOM'");
//            parametros.put("viaticosEmpleado", caj);
//            List<Detalleviaticos> listaValeLiq = ejbValesViaticosempleado.encontarParametros(parametros);
//            if (!listaValeLiq.isEmpty()) {
//                LiquidaciondeCompras hoja = new LiquidaciondeCompras();
//                hoja.llenar(caj, listaValeLiq);
//                reporteComp = hoja.traerRecurso();
//            }
//
//        } catch (IOException | DocumentException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ReposicionViaticosempleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    public String imprimirCompromiso(List<Detalleviaticos> listaDetalles) {
//        Compromisos comp = null;
//        List<Auxiliar> titulos = proyectoBean.getTitulos();
//        SimpleDateFormat anio = new SimpleDateFormat("yyyy");
//        if (!listaDetalles.isEmpty()) {
//            Detalleviaticos dv = listaDetalles.get(0);
//            comp = dv.getViaticoempleado().getDetallecompromiso();
//        }
//        if (comp != null) {
//            try {
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.compromiso=:compromiso");
//                parametros.put("compromiso", comp);
//                List<Detallecompromiso> detallees = ejbDetallecompromiso.encontarParametros(parametros);
//                String direccion = "";
//                for (Detallecompromiso d : detallees) {
//                    if (d.getCompromiso() != null) {
//                        if (d.getCompromiso().getCertificacion() != null) {
//                            if (d.getCompromiso().getCertificacion().getDireccion() != null) {
//                                direccion = d.getCompromiso().getCertificacion().getDireccion().toString();
//                            }
//                        }
//
//                    }
//                }
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
//                DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
//                        null, seguridadbean.getLogueado().getUserid());
//                pdf.agregaTitulo("INFORME DE COMPROMISO\n");
//                List<AuxiliarReporte> columnas = new LinkedList<>();
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Compromiso :"));
////            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getId().toString()));
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, comp.getNumerocomp() + ""));
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf1.format(comp.getFecha()).toUpperCase()));
//                pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio econ??mico del a??o " + anio.format(comp.getFecha())
//                        + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
//                pdf.agregarTabla(null, columnas, 2, 100, null);
//                pdf.agregaTitulo("\n");
//                String proveedor = "";
//                String contrato = "";
//                if (comp.getProveedor() != null) {
//                    proveedor = comp.getProveedor().getEmpresa().toString();
//                    if (comp.getContrato() != null) {
//                        contrato = comp.getContrato().toString();
//                    }
//                } else if (comp.getBeneficiario() != null) {
//                    proveedor = comp.getBeneficiario().getEntidad().toString();
//
//                }
//                List<AuxiliarReporte> titulosReporte = new LinkedList<>();
//                int totalCol = 6;
//                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBL"));
//                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
//                titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
//                titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
//                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
//                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
//                columnas = new LinkedList<>();
//                double total = 0;
//                for (Detallecompromiso d : detallees) {
//                    String que = "";
//                    for (Auxiliar a : titulos) {
//                        que += a.getTitulo1() + " : ";
//                        que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";
//
//                    }
//                    que += " Programa : " + d.getAsignacion().getProyecto().toString();
//                    d.setArbolProyectos(que);
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(d.getFecha())));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
//                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
//                    columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
//                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getNombre()));
//                    columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
//                    total += d.getValor().doubleValue();
//                }
//
//                DecimalFormat df = new DecimalFormat("###,##0.00");
//                pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
//                pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + contrato + " " + comp.getMotivo());
//                pdf.agregaParrafo("\n\n");
//                pdf.agregaParrafo("\nEl Monto del compromiso asciende a: " + df.format(total) + "\n");
//                pdf.agregaParrafo("\nTotal compromiso: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
//                pdf.agregaParrafo("\n\n");
////            FM14SEP2018
//                if (comp.getCertificacion() != null) {
//                    if (comp.getCertificacion().getNumerocert() != null) {
//                        pdf.agregaParrafo("\nCertificaci??n Nro: " + comp.getCertificacion().getNumerocert() + " - " + comp.getCertificacion().getMotivo() + "\n");
//                        pdf.agregaParrafo("\n\n");
//                    }
//                }
////            FM14SEP2018
//                pdf.agregaParrafo("\n\n");
//                columnas = new LinkedList<>();
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista de Presupuesto"));
//                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Jefe de Presupuesto/Director Financiero"));
//
//                pdf.agregarTabla(null, columnas, 2, 100, null);
//                reporteCompromiso = pdf.traerRecurso();
//            } catch (IOException | DocumentException | ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(LiquidacionViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
    private List<Renglones> traerRenglonesGasto() {
        if (viaticosEmpleado != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion "
                    + " in ('GASTO_FONDOS')");
            parametros.put("id", viaticosEmpleado.getId());
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

    private List<Renglones> traerRenglonesLiquidacion() {
        if (viaticosEmpleado != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion "
                    + " in ('LIQUIDACION_FONDOS')");
            parametros.put("id", viaticosEmpleado.getId());
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
        if (viaticosEmpleado != null) {
            renglonesAuxiliar = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion "
                    + " in ('GASTO_FONDOS_RECLASIFICACION')");
            parametros.put("id", viaticosEmpleado.getId());
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
            parametros.put(";where", "o.viaticosEmpleado=:viaticosEmpleado");
            parametros.put("viaticosEmpleado", ve);
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
            parametros.put(";where", "o.viaticosEmpleado=:viaticosEmpleado");
            parametros.put("viaticosEmpleado", ve);
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

    public String imprimirKardex() {
        try {
            if (listaViaticosempleado.isEmpty()) {
                MensajesErrores.advertencia("No existen empleados");
                return null;
            }
            listadoKardex = new LinkedList<>();
            for (Viaticosempleado ve : listaViaticosempleado) {
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
            boolean segunda = false;
            List<AuxiliarReporte> columnas = new LinkedList<>();
            for (Kardexbanco k : listadoKardex) {
                if (segunda) {
                    pdf.finDePagina();
                }
                segunda = true;

                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBanco().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, sdf.format(k.getFechamov())));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getCuentatrans()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBeneficiario()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco T :"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Valor:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));

                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, df.format(k.getValor())));
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
                pdf.agregaParrafo("\n\n");
                pdf.agregaTitulo("COMPROBANTE DE PAGO FONDO A RENDIR CUENTAS - " + k.getEgreso());
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
                pdf.agregaParrafo("\n\n");
                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Revisado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Aprobado por:"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tesorero"));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Director Financiero"));
                pdf.agregarTabla(null, columnas, 2, 100, null);
            }
            reporteKardex = pdf.traerRecurso();
            imprimir();
            formularioReportes.insertar();

        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
//    *********************** OJOJOJOJO IMPRIMIR PROMESA

    public String imprimir() {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());
            boolean segunda = false;
            for (Kardexbanco k : listadoKardex) {
                List<AuxiliarReporte> columnas = new LinkedList<>();
                if (segunda) {
                    pdf.finDePagina();
                }
                segunda = true;
                columnas = new LinkedList<>();
                List<AuxiliarReporte> titulos = new LinkedList<>();
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisi??n"));
                titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta T"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                double valorTotal = 0;
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                String numeroCuenta = k.getCuentatrans();
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

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    private List<Renglones> traer(Kardexbanco k) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.idmodulo=:id and o.cabecera.opcion='PAGO FONDO A RENDIR CUENTAS'");
        parametros.put("id", k.getId());
        try {
            return ejbRenglones.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagoCajaChicaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerParcial(Viaticosempleado caj) {
        Map parametros = new HashMap();
        parametros.put(";where", "o.apertura=:apertura");
        parametros.put("apertura", caj);
        try {
            List<Viaticosempleado> lista = ejbViaticosempleado.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return "SI";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteViaticosFechasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "NO";
    }

    public double traerTotal(Viaticosempleado ve) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valorvalidado");
        parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
        parametros.put("viaticoempleado", ve);
        double valor;
        try {
            valor = ejbDetalleviaticos.sumarCampo(parametros).doubleValue();
            return valor;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String traerLiquidacion(Viaticosempleado ve) {
        String retorno = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
        parametros.put("viaticoempleado", ve);
        try {
            List<Detalleviaticos> lista = ejbDetalleviaticos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Detalleviaticos vf = lista.get(0);
                if (vf.getReposicion() != null) {
                    retorno = vf.getReposicion().getNumerodocumento() + "";
                    return retorno;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double traerValorLiquidacion(Viaticosempleado ve) {
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
        parametros.put("viaticoempleado", ve);
        try {
            List<Detalleviaticos> lista = ejbDetalleviaticos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Detalleviaticos vf = lista.get(0);
                if (vf.getReposicion() != null) {
                    if (vf.getReposicion().getBase() == null) {
                        vf.getReposicion().setBase(BigDecimal.ZERO);
                    }
                    if (vf.getReposicion().getBase0() == null) {
                        vf.getReposicion().setBase0(BigDecimal.ZERO);
                    }
                    if (vf.getReposicion().getIva() == null) {
                        vf.getReposicion().setIva(BigDecimal.ZERO);
                    }
                    retorno = vf.getReposicion().getBase().doubleValue()
                            + vf.getReposicion().getBase0().doubleValue()
                            + vf.getReposicion().getIva().doubleValue();
                    return retorno;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String traerFechaLiquidacionCompras(Viaticosempleado ve) {
        String retorno = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
        parametros.put("viaticoempleado", ve);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<Detalleviaticos> lista = ejbDetalleviaticos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Detalleviaticos vf = lista.get(0);
                if (vf.getReposicion() != null) {
                    retorno = sdf.format(vf.getReposicion().getFecha());
                    return retorno;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    
    public String traerFechaLiquidacion(Viaticosempleado ve) {
        String retorno = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion='LIQUIDACION_VIATICOS'");
        parametros.put("idmodulo", ve.getId());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            List<Cabeceras> lista = ejbCabeceras.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Cabeceras vf = lista.get(0);
                if (vf.getFecha() != null) {
                    retorno = sdf.format(vf.getFecha());
                    return retorno;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
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
     * @return the viaticosEmpleado
     */
    public Viaticosempleado getFondo() {
        return viaticosEmpleado;
    }

    /**
     * @param viaticosEmpleado the viaticosEmpleado to set
     */
    public void setFondo(Viaticosempleado viaticosEmpleado) {
        this.viaticosEmpleado = viaticosEmpleado;
    }

    /**
     * @return the listaViaticosempleado
     */
    public List<Viaticosempleado> getListaViaticosempleado() {
        return listaViaticosempleado;
    }

    /**
     * @param listaViaticosempleado the listaViaticosempleado to set
     */
    public void setListaViaticosempleado(List<Viaticosempleado> listaViaticosempleado) {
        this.listaViaticosempleado = listaViaticosempleado;
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
     * @return the pago
     */
    public int getPago() {
        return pago;
    }

    /**
     * @param pago the pago to set
     */
    public void setPago(int pago) {
        this.pago = pago;
    }

    /**
     * @return the egreso
     */
    public String getEgreso() {
        return egreso;
    }

    /**
     * @param egreso the egreso to set
     */
    public void setEgreso(String egreso) {
        this.egreso = egreso;
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
     * @return the listaDetalleViaticos
     */
    public List<Detalleviaticos> getListaDetalleViaticos() {
        return listaDetalleViaticos;
    }

    /**
     * @param listaDetalleViaticos the listaDetalleViaticos to set
     */
    public void setListaDetalleViaticos(List<Detalleviaticos> listaDetalleViaticos) {
        this.listaDetalleViaticos = listaDetalleViaticos;
    }
}
