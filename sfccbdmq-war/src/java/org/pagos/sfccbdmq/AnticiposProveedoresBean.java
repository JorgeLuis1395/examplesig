/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import javax.faces.application.Resource;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.AnticiposFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.ContratosFacade;
import org.beans.sfccbdmq.GarantiasFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.compras.sfccbdmq.ObligacionesBean;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Anticipos;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empresas;
import org.entidades.sfccbdmq.Garantias;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Pagosvencimientos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "anticiposProveedoresSfccbdmq")
@ViewScoped
public class AnticiposProveedoresBean {

    /**
     * Creates a new instance of AnticiposBean
     */
    public AnticiposProveedoresBean() {

        anticipos = new LazyDataModel<Anticipos>() {

            @Override
            public List<Anticipos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private Anticipos anticipo;
    private LazyDataModel<Anticipos> anticipos;
    private Formulario formulario = new Formulario();
    private Formulario formularioContabilizar = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Perfiles perfil;
    @EJB
    private AnticiposFacade ejbAnticipos;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private GarantiasFacade ejbGarantias;
    @EJB
    private PagosvencimientosFacade ejbPagos;
    @EJB
    private ContratosFacade ejbContratos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    private Contratos contrato;
    private String tipoFecha = "o.fechaemision";
    private Date desde;
    private Date hasta;
    private boolean aplicado;
    private String referencia;
    private Kardexbanco kardex;
    private List<Renglones> renglones;
    private List<Cuentas> cuentas;
    private Resource reporte;

    /**
     * @return the anticipo
     */
    public Anticipos getAnticipo() {
        return anticipo;
    }

    /**
     * @param anticipo the anticipo to set
     */
    public void setAnticipo(Anticipos anticipo) {
        this.anticipo = anticipo;
    }

    /**
     * @return the anticipos
     */
    public LazyDataModel<Anticipos> getAnticipos() {
        return anticipos;
    }

    /**
     * @param anticipos the anticipos to set
     */
    public void setAnticipos(LazyDataModel<Anticipos> anticipos) {
        this.anticipos = anticipos;
    }

    public String buscar() {
//        if (proveedorBean.getEmpresa() == null) {
//            anticipos = null;
//            MensajesErrores.advertencia("Seleccione un proveedor  primero");
//            return null;
//        }

        anticipos = new LazyDataModel<Anticipos>() {

            @Override
            public List<Anticipos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fechaemision desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = tipoFecha + " between :desde and :hasta and o.proveedor is not null";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (!((referencia == null) || (referencia.isEmpty()))) {
                    where += " and upper(o.referencia) like :referencia";
                    parametros.put("referencia", referencia.toUpperCase() + "%");
                }
                if (contrato != null) {
                    where += " and o.contrato=:contrato";
                    parametros.put("contrato", contrato);
                } else {
                    if (proveedorBean.getEmpresa() != null) {
                        where += " and o.proveedor=:proveedor";
                        parametros.put("proveedor", proveedorBean.getProveedor());
                    }
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbAnticipos.contar(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                int endIndex = i + pageSize;
                if (endIndex > total) {
                    endIndex = total;
                }
                parametros.put(";inicial", i);
                parametros.put(";final", endIndex);
                anticipos.setRowCount(total);
                try {
                    return ejbAnticipos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }

                return null;
            }
        };

        return null;
    }

    public String crear() {
        if (proveedorBean.getProveedor() == null) {
            MensajesErrores.advertencia("Seleccione un proveedor  primero");
            return null;
        }
        if (contrato == null) {
            MensajesErrores.advertencia("Seleccione un contrato  primero");
            return null;
        }
        if (contrato.getAnticipo() == null) {
            MensajesErrores.advertencia("Contrato no tiene anticipo");
            return null;
        }

        anticipo = new Anticipos();
        anticipo.setFechaemision(new Date());
        anticipo.setPagado(false);
        anticipo.setContrato(contrato);
        anticipo.setValor(contrato.getAnticipo());
        anticipo.setProveedor(proveedorBean.getProveedor());
        anticipo.setEstado(0);
        if (validarAntes()) {
            return null;
        }
        formulario.insertar();
        // debe estar ya el contrato
        return null;
    }

    private boolean validarAntes() {

        if (anticipo.getContrato() == null) {
            MensajesErrores.advertencia("Contrato es necesario en anticipo");
            return true;
        }
        if (anticipo.getContrato().getEstado() == null) {
            MensajesErrores.advertencia("Mal configurado contrato, sin estado");
            return true;
        }
        if (anticipo.getContrato().getEstado() != 1) {
            MensajesErrores.advertencia("Contrato sin compromiso presupuestario");
            return true;
        }
//        anticipo.setValor(anticipo.getContrato().getAnticipo());

        // sumar todos los anticipos
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
//        parametros.put(";campo", "o.valor");
        parametros.put("contrato", anticipo.getContrato());
        try {
            if (formulario.isNuevo()) {
                int total = ejbAnticipos.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Un solo anticipo por Contrato");
                    return true;
                }
            }
            if (anticipo.getProveedor().getGarantia() == null) {
                anticipo.getProveedor().setGarantia(true);
            }
            if (anticipo.getProveedor().getGarantia()) {
                // sumar garantias
                parametros = new HashMap();
                parametros.put(";where", "o.contrato=:contrato and o.tipo.parametros='TRUE'");
//            parametros.put(";campo", "o.monto");
                parametros.put("contrato", anticipo.getContrato());
                Garantias g = null;
                List<Garantias> gl = ejbGarantias.encontarParametros(parametros);

//            double valorGarantias = ejbGarantias.sumarCampo(parametros).doubleValue();
                double valorGarantias = 0;
                for (Garantias g1 : gl) {
                    g = g1;
                    valorGarantias = g1.getMonto().doubleValue() * 100;
                }
                if (g == null) {
                    MensajesErrores.advertencia("No exsite garantia para emitir anticipo");
                    return true;
                }
                double valorAnticipo = anticipo.getContrato().getAnticipo().doubleValue() * 100;
                double total = Math.round((valorGarantias - valorAnticipo) / 100);
                if (total < 0) {
                    DecimalFormat df = new DecimalFormat("###,###,##0.0");
                    MensajesErrores.advertencia("Valor de garantias no puede ser menor al del anticipo en contrato : "
                            + "[Garantias = " + df.format(valorGarantias / 100) + "] [Antcipo = " + df.format(valorAnticipo / 100) + "]");
                    return true;
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public String modificar(Anticipos anticipo) {

        this.anticipo = anticipo;
        formulario.editar();
        proveedorBean.setEmpresa(anticipo.getProveedor().getEmpresa());
        proveedorBean.setRuc(anticipo.getProveedor().getEmpresa().getRuc());

        return null;
    }

    public String imprimirKardex(Anticipos anticipo) {
        try {
            this.anticipo = anticipo;

            proveedorBean.setEmpresa(anticipo.getProveedor().getEmpresa());
            proveedorBean.setRuc(anticipo.getProveedor().getEmpresa().getRuc());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, seguridadbean.getLogueado().getUserid());
            boolean segunda = false;
            List<AuxiliarReporte> columnas = new LinkedList<>();
            Map parametros = new HashMap();
            parametros.put(";where", "o.anticipo=:anticipo");
            parametros.put("anticipo", anticipo);
            List<Kardexbanco> listadoKardex = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : listadoKardex) {
                if (segunda) {
                    pdf.finDePagina();
                }
                segunda = true;

                columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", "Banco :"));
                columnas.add(new AuxiliarReporte("String", k.getBanco().toString()));
                columnas.add(new AuxiliarReporte("String", "Fecha :"));
                columnas.add(new AuxiliarReporte("String", sdf.format(k.getFechamov())));

                columnas.add(new AuxiliarReporte("String", "Cuenta T:"));
                columnas.add(new AuxiliarReporte("String", k.getCuentatrans()));
                columnas.add(new AuxiliarReporte("String", ""));
                columnas.add(new AuxiliarReporte("String", ""));

                columnas.add(new AuxiliarReporte("String", "Beneficiario :"));
                columnas.add(new AuxiliarReporte("String", k.getBeneficiario()));

                columnas.add(new AuxiliarReporte("String", "Banco T :"));
                columnas.add(new AuxiliarReporte("String", k.getBancotransferencia().toString()));

                columnas.add(new AuxiliarReporte("String", "Valor:"));
                columnas.add(new AuxiliarReporte("String", ConvertirNumeroALetras.convertNumberToLetter(k.getValor().doubleValue())));
                columnas.add(new AuxiliarReporte("String", ""));

                columnas.add(new AuxiliarReporte("String", df.format(k.getValor())));
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
                traerRenglones();
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
                                    Empresas p = proveedorBean.taerRuc(r.getAuxiliar());
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
                                    Empresas p = proveedorBean.taerRuc(r.getAuxiliar());
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

            // obligaciones
            columnas = new LinkedList<>();

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
//            imprimir();
            formularioImprimir.insertar();
        } catch (IOException | DocumentException ex) {
            Logger.getLogger(KardexPagosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimir(Anticipos anticipo) {
        try {
            this.anticipo = anticipo;
            Map parametros = new HashMap();
            parametros.put(";where", "o.anticipo=:anticipo");
            parametros.put("anticipo", anticipo);
            String observacionPropuesta = "";
            List<Kardexbanco> listadoKardex = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : listadoKardex) {
                observacionPropuesta = k.getPropuesta();
            }
            DocumentoPDF pdf = new DocumentoPDF("PROPUESTA DE PAGO  " + observacionPropuesta, null, seguridadbean.getLogueado().getUserid());
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
            titulos.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Beneficiario"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Anticipo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tipo Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Documento"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            double valorTotal = 0;
            for (Kardexbanco k : listadoKardex) {
//                if (k.isSeleccionar()) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getFechamov()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getObservaciones()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, anticipo.getContrato().toString()));
//                String numeroCuenta = k.getCuentatrans();
                String numeroCuenta = anticipo.getCuenta();
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
//                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, valorTotal));
            pdf.agregarTablaReporte(titulos, columnas, 9, 100, null);
            pdf.agregaParrafo("\n\n\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Contador/a Financiero/a"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Director/a Financiero/a"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporte = pdf.traerRecurso();
            formularioImprimir.editar();

        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimirAnt(Anticipos anticipo) {

        this.anticipo = anticipo;

        proveedorBean.setEmpresa(anticipo.getProveedor().getEmpresa());
        proveedorBean.setRuc(anticipo.getProveedor().getEmpresa().getRuc());
        Map parametros = new HashMap();
        parametros.put(";where", "o.anticipo=:anticipo");
        parametros.put("anticipo", anticipo);
        List<Kardexbanco> lk;
        try {
            lk = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : lk) {
                kardex = k;
            }
            traerRenglones();

            // Imprimir 
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            DocumentoPDF pdf = new DocumentoPDF("",
                    null, seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", "Banco :"));
            columnas.add(new AuxiliarReporte("String", kardex.getBanco().toString()));
            columnas.add(new AuxiliarReporte("String", "Fecha :"));
            columnas.add(new AuxiliarReporte("String", sdf.format(kardex.getFechamov())));

            columnas.add(new AuxiliarReporte("String", "Cuenta T:"));
            columnas.add(new AuxiliarReporte("String", kardex.getCuentatrans()));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", ""));

            columnas.add(new AuxiliarReporte("String", "Beneficiario :"));
            columnas.add(new AuxiliarReporte("String", kardex.getBeneficiario()));

            columnas.add(new AuxiliarReporte("String", "Banco T :"));
            columnas.add(new AuxiliarReporte("String", kardex.getBancotransferencia().toString()));

            columnas.add(new AuxiliarReporte("String", "Valor:"));
            columnas.add(new AuxiliarReporte("String", ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue())));
            columnas.add(new AuxiliarReporte("String", ""));

            columnas.add(new AuxiliarReporte("String", df.format(kardex.getValor())));
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo(configuracionBean.getConfiguracion().getNombre());
            pdf.agregaParrafo("\n\n");
            pdf.agregaTitulo("COMPROBANTE DE PAGO ");
//            pdf.agregaTitulo("COMPROBANTE DE PAGO " + kardex.getDocumento());
            pdf.agregaParrafo("\n\n");
            pdf.agregarTabla(null, columnas, 4, 100, null);

            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo(kardex.getObservaciones() + "\n\n");

            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Debe"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Haber"));
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            if (renglones == null) {
                renglones = new LinkedList<>();
            }
            columnas = new LinkedList<>();
            for (Renglones r : renglones) {

                String cuenta = "";
                String auxiliar = r.getAuxiliar();
                Cuentas cta = getCuentasBean().traerCodigo(r.getCuenta());
                if (cta != null) {
                    cuenta = cta.getNombre();
                    if (cta.getAuxiliares() != null) {
                        VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                        auxiliar = (v == null ? "" : v.getNombre());
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
            // obligaciones
//            titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Orden Pago"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
//            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
//            columnas = new LinkedList<>();
//            for (Pagosvencimientos p : pagos) {
////                        columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(p.getObligacion().getFechaemision())));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(traerOrdenPago(p.getObligacion()))));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().getId().toString()));
//                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getObligacion().
//                        getContrato() == null ? "" : p.getObligacion().getContrato().toString()));
//                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false,
//                        p.getObligacion().getConcepto()));
//                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, p.getValor().doubleValue()));
//            }
//            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "ORDENES DE PAGO");
//            pdf.finDePagina();
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", ""));
            columnas.add(new AuxiliarReporte("String", "Contador"));
            columnas.add(new AuxiliarReporte("String", "Director Financiero"));

            pdf.agregarTabla(null, columnas, 3, 100, null);
            reporte = pdf.traerRecurso();
//            formularioImprimir.insertar();
            // fin imprimir
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.editar();
        return null;
    }

    public String imprimir1(Anticipos anticipo) {

        this.anticipo = anticipo;

        proveedorBean.setEmpresa(anticipo.getProveedor().getEmpresa());
        proveedorBean.setRuc(anticipo.getProveedor().getEmpresa().getRuc());
        Map parametros = new HashMap();
        parametros.put(";where", "o.anticipo=:anticipo");
        parametros.put("anticipo", anticipo);
        List<Kardexbanco> lk;
        try {
            lk = ejbKardex.encontarParametros(parametros);
            for (Kardexbanco k : lk) {
                kardex = k;
            }
            traerRenglones();

            for (Renglones r : renglones) {
                Cuentas c = getCuentasBean().traerCodigo(r.getCuenta());
                r.setNombre(c.getNombre());
                double valor = r.getValor().doubleValue();
                if (valor > 0) {
                    r.setDebitos(valor);
                } else {
                    r.setCreditos(valor * -1);
                }
                if (c.getAuxiliares() != null) {
                    VistaAuxiliares v = seguridadbean.traerAuxiliar(r.getAuxiliar());
                    r.setAuxiliarNombre(v == null ? "" : v.getNombre());
                }
            }
            Collections.sort(renglones, new valorComparator());
            parametros = new HashMap();
            parametros.put("expresado", " Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre());
            parametros.put("empresa", kardex.getBeneficiario());
            parametros.put("nombrelogo", "Comprobante de Egreso No : " + kardex.getId());
            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros.put("fecha", kardex.getFechamov());
            parametros.put("documento", kardex.getEgreso().toString());
            parametros.put("modulo", getCuantoStr());
            parametros.put("descripcion", kardex.getObservaciones());
            parametros.put("obligaciones", getValorStr());
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes");
            parametros.put("SUBREPORT_DIR", realPath + "/");
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Egreso.jasper"),
                    renglones, "Egreso" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImprimir.editar();
        return null;
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
            parametros.put(";where", "o.cabecera.idmodulo=:id "
                    + "and o.cabecera.opcion='ANTICIPO PROVEEDORES'");
//                    + "and o.cabecera.modulo=:modulo and o.cabecera.opcion='ANTICIPO PROVEEDORES'");
            parametros.put("id", anticipo.getId());
//            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
                // traer reclasificacion

            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ObligacionesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String aprobarAnticipo(Anticipos anticipo) {

        this.anticipo = anticipo;
        kardex = new Kardexbanco();
        kardex.setUsuariograba(seguridadbean.getLogueado());
        kardex.setAnticipo(anticipo);
        kardex.setValor(anticipo.getValor());
        kardex.setProveedor(anticipo.getProveedor());
        kardex.setBeneficiario(anticipo.getProveedor().getNombrebeneficiario());
        if (anticipo.getContrato() == null) {
            kardex.setObservaciones(" Anticipo proveedor : " + anticipo.getProveedor().getEmpresa().getRuc());
        } else {
            kardex.setObservaciones(" Anticipo proveedor : " + anticipo.getProveedor().getEmpresa().getRuc()
                    + " Contrato " + anticipo.getContrato().getTitulo()
                    + " No: " + anticipo.getContrato().getNumero());
//                    " con Objeto " + anticipo.getContrato().getObjeto());
        }
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setFechamov(anticipo.getFechaemision());
        kardex.setUsuariograba(seguridadbean.getLogueado());
        kardex.setDocumento(anticipo.getProveedor().getEmpresa().getRuc());
        kardex.setOrigen("ANTICIPO PROVEEDORES");
        formularioContabilizar.editar();
        // ver en proveedor
        String clasificacionProveedor = anticipo.getProveedor().getClasificacion().getParametros();
        if (clasificacionProveedor == null) {
            //MensajesErrores.advertencia("Clasificación del proveedor mal configurado");
            return null;
        }
        if (clasificacionProveedor.indexOf("#") <= 0) {
            //MensajesErrores.advertencia("Clasificación del proveedor mal configurado");
            return null;
        }
        String[] cuentasProveedor = clasificacionProveedor.split("#");
        if (cuentasProveedor.length == 0) {
            //MensajesErrores.advertencia("Clasificación del proveedor mal configurado");
            return null;
        }
        cuentas = new LinkedList<>();
        for (String cuentasProveedor1 : cuentasProveedor) {
            Cuentas cuentaAnticipo = cuentasBean.traerCodigo(cuentasProveedor1);
            cuentas.add(cuentaAnticipo);
        }
        return null;
    }

    public String grabarKardex() {
        // ver cuentas o ya estan validadas?
//        if ((getKardex().getEgreso() == null) || (kardex.getEgreso() <= 0)) {
//            MensajesErrores.advertencia("Necesario un número de egreso");
//            return null;
//        }
        if ((anticipo.getCuenta() == null) || (anticipo.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Seleccione la cuenta de anticipo de proveedores");
            return null;
        }
//        if (kardex.getBanco() == null) {
//            MensajesErrores.advertencia("Seleccione un banco primero");
//            return null;
//        }
//        if (kardex.getFormapago().getParametros().contains("T")) {
//            if (anticipo.getProveedor().getLimitetransferencia() == null) {
//                MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
//                return null;
//            }
//            if (anticipo.getValor().doubleValue() > anticipo.getProveedor().getLimitetransferencia().doubleValue()) {
//                MensajesErrores.advertencia("Valor sobre pasa límite de trasferencia de proveedor");
//                return null;
//            }
        kardex.setCuentatrans(anticipo.getProveedor().getCtabancaria());
        kardex.setBancotransferencia(anticipo.getProveedor().getBanco());
        kardex.setTcuentatrans(Integer.parseInt(anticipo.getProveedor().getTipocta().getParametros()));
//        }
//        Cuentas cuentaBanco = cuentasBean.traerCodigo(kardex.getBanco().getCuenta());
//        cuentasBean.setCuenta(cuentaBanco);
//        if (cuentasBean.validaCuentaMovimiento()) {
//            return null;
//        }
//        renglones = new LinkedList<>();
        // ver en proveedor

//        Cuentas cuentaAnticipo = cuentasBean.traerCodigo(anticipo.getCuenta());
        try {
//            kardex.setEstado(2);
            anticipo.setEstado(2);
            anticipo.setFechacontable(new Date());
//            anticipo.setCuenta(cuentaAnticipo.getCodigo());
            String obs = kardex.getObservaciones();
            kardex.setObservaciones(anticipo.getReferencia());
            kardex.setEstado(2);
            kardex.setDocumento(kardex.getProveedor().getEmpresa().getRuc());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            kardex.setPropuesta("ANTIPO PROVEEDORES DEL" + sdf.format(kardex.getFechamov()));
            kardex.setCuenta(anticipo.getCuenta());
            kardex.setAuxiliar(anticipo.getCuenta());
            ejbKardex.create(kardex, seguridadbean.getLogueado().getUserid());
            ejbAnticipos.edit(anticipo, seguridadbean.getLogueado().getUserid());
//            Tipoasiento ta = kardex.getTipomov().getTipoasiento();
//
//            int numero = ta.getUltimo();
//            numero++;
//            ta.setUltimo(numero);
//            ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());
//            Cabeceras cab = new Cabeceras();
//            cab.setDescripcion(kardex.getObservaciones());
//            cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
//            cab.setDia(new Date());
//            cab.setTipo(kardex.getTipomov().getTipoasiento());
//            cab.setNumero(numero);
//            cab.setFecha(kardex.getFechamov());
//            cab.setIdmodulo(anticipo.getId());
//            cab.setOpcion("ANTICIPO PROVEEDORES");
//            cab.setUsuario(seguridadbean.getLogueado().getUserid());
//            ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
//            Renglones r1 = new Renglones(); // reglon de banco
//            r1.setCabecera(cab);
//            r1.setFecha(kardex.getFechamov());
//            r1.setReferencia(kardex.getObservaciones());
//            r1.setValor(new BigDecimal(anticipo.getValor().doubleValue() * -1));
//            r1.setCuenta(cuentaBanco.getCodigo());
//            if (cuentaBanco.getAuxiliares() != null) {
//                r1.setAuxiliar(anticipo.getProveedor().getEmpresa().getRuc());
//            }
//            r1.setPresupuesto(cuentaBanco.getPresupuesto());
//            Renglones r = new Renglones(); // reglon de banco
//            r.setCabecera(cab);
//            r.setReferencia(kardex.getObservaciones());
//            r.setFecha(kardex.getFechamov());
//            r.setValor(new BigDecimal(anticipo.getValor().doubleValue()));
//            r.setCuenta(cuentaAnticipo.getCodigo());
//            if (cuentaAnticipo.getAuxiliares() != null) {
//                r.setAuxiliar(anticipo.getProveedor().getEmpresa().getRuc());
//            }
//            r.setPresupuesto(cuentaAnticipo.getPresupuesto());
//            ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//            ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
//            renglones.add(r);
//            renglones.add(r1);
////            formularioImprimir.editar();
            formularioContabilizar.cancelar();
            Contratos con = anticipo.getContrato();
            con.setEstado(2);
            ejbContratos.edit(con, seguridadbean.getLogueado().getUserid());
//            imprimir(anticipo);
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String eliminar(Anticipos anticipo) {
        if (anticipo.getAplicado() == null) {
            anticipo.setAplicado(false);
        }
        if (anticipo.getAplicado()) {
            MensajesErrores.advertencia("Antcipo ya aplicado no se puede borrar");
            return null;
        }

        this.anticipo = anticipo;
        formulario.eliminar();
        proveedorBean.setEmpresa(anticipo.getProveedor().getEmpresa());
        proveedorBean.setRuc(anticipo.getProveedor().getEmpresa().getRuc());
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

    private boolean validar() {

        if ((anticipo.getReferencia() == null) || (anticipo.getReferencia().isEmpty())) {
            MensajesErrores.advertencia("Es necesaria referencia");
            return true;
        }
        anticipo.setProveedor(proveedorBean.getProveedor());
        if ((anticipo.getProveedor() == null)) {
            MensajesErrores.advertencia("Es necesario proveedor");
            return true;
        }

//        if ((anticipo.getContrato() != null)) {
//            // ver monto
//
//            MensajesErrores.advertencia("Es necesario fecha de caducidad de anticipo");
//            return true;
//        }
        if ((anticipo.getFechaemision() == null)) {
            MensajesErrores.advertencia("Es necesario fecha de emisión de anticipo");
            return true;
        }
        if ((anticipo.getFechaemision().before(configuracionBean.getConfiguracion().getPvigente()))) {
            MensajesErrores.advertencia("Es necesario fecha de emisión de anticipo mayor o igual a periodo vigente");
            return true;
        }
//        if ((anticipo.getFechavencimiento() == null)) {
//            MensajesErrores.advertencia("Es necesario fecha de emisión de anticipo");
//            return true;
//        }

        // anticcipo solo de un contrato
        if (anticipo.getContrato() == null) {
            MensajesErrores.advertencia("Contrato es necesario en anticipo");
            return true;
        }
        if (anticipo.getContrato().getEstado() == null) {
            MensajesErrores.advertencia("Mal configurado contrato, sin estado");
            return true;
        }
        if (anticipo.getContrato().getEstado() != 1) {
            MensajesErrores.advertencia("Contrato sin compromiso presupuestario");
            return true;
        }
        anticipo.setValor(anticipo.getContrato().getAnticipo());

        // sumar todos los anticipos
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
//        parametros.put(";campo", "o.valor");
        parametros.put("contrato", anticipo.getContrato());
        try {
            if (formulario.isNuevo()) {
                int total = ejbAnticipos.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Un solo anticipo por Contrato");
                    return true;
                }
            }
            if (anticipo.getProveedor().getGarantia() == null) {
                anticipo.getProveedor().setGarantia(true);
            }
            if (anticipo.getProveedor().getGarantia()) {
                // sumar garantias
                parametros = new HashMap();
                parametros.put(";where", "o.contrato=:contrato and o.tipo.parametros='TRUE'");
//            parametros.put(";campo", "o.monto");
                parametros.put("contrato", anticipo.getContrato());
                Garantias g = null;
                List<Garantias> gl = ejbGarantias.encontarParametros(parametros);

//            double valorGarantias = ejbGarantias.sumarCampo(parametros).doubleValue();
                double valorGarantias = 0;
                for (Garantias g1 : gl) {
                    g = g1;
                    valorGarantias = g1.getMonto().doubleValue() * 100;
                }
                if (g == null) {
                    MensajesErrores.advertencia("No exsite garantia para emitir anticipo");
                    return true;
                }
//            if (anticipo.getContrato().getValor().doubleValue() < valorGarantias) {
//                MensajesErrores.advertencia("Valor de garantias no puede ser menor al del anticipo en contrato");
//                return true;
//            }
                double valorAnticipo = anticipo.getContrato().getAnticipo().doubleValue() * 100;
                double total = Math.round((valorGarantias - valorAnticipo) / 100);
//            if (total > 0) {
//ES-2018-03-23
                if (total < 0) {
//            if ( total!=0) {
                    DecimalFormat df = new DecimalFormat("###,###,##0.0");
                    MensajesErrores.advertencia("Valor de garantias no puede ser menor al del anticipo en contrato : "
                            + "[Garantias = " + df.format(valorGarantias / 100) + "] [Anticipo = " + df.format(valorAnticipo / 100) + "]");
                    return true;
                }
            }

//            if ((anticipo.getFechaemision().after(g.getVencimiento()))) {
//                MensajesErrores.advertencia("Fecha de vencimiento de garantia debe ser mayor a fecha de emisión en anticipo");
//                return true;
//            }
//            if ((anticipo.getFechaemision().after(g.getDesde()))) {
//                MensajesErrores.advertencia("Fecha de inicio de garantia debe ser mayor a fecha de emisión en anticipo");
//                return true;
//            }
//            if ((anticipo.getContrato().getAnticipo().doubleValue() - valorAnticipos) < anticipo.getValor().doubleValue()) {
//                MensajesErrores.advertencia("Valor no puede ser igual al anticipo contrato");
//                return true;
//            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        // el valor controlar si no sobrepasa el del contrato
        // ver si me toca ver si ya tiene pagos normales ya se dio el anticipo
//        if ((anticipo.getInicio() == null) || (anticipo.getInicio() <= 0)) {
//            MensajesErrores.advertencia("Es necesario Fin de serie válido en anticipo");
//            return true;
//        }
//        if ((anticipo.getFin() == null) || (anticipo.getFin() <= anticipo.getInicio())) {
//            MensajesErrores.advertencia("Es necesario Inicio de serie válido menor a finen anticipo");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            anticipo.setFechaingreso(new Date());
            anticipo.setUsuario(seguridadbean.getLogueado());
            ejbAnticipos.create(anticipo, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {

            ejbAnticipos.edit(anticipo, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            if (anticipo.getFechacontable() == null) {
                ejbAnticipos.remove(anticipo, seguridadbean.getLogueado().getUserid());
            } else {
                // anular kardex
                Map parametros = new HashMap();
                parametros.put(";where", "o.anticipo=:anticipo");
                parametros.put("anticipo", anticipo);
                List<Kardexbanco> kl = ejbKardex.encontarParametros(parametros);
                int totalPagos = ejbPagos.contar(parametros);
                if (totalPagos > 0) {
                    MensajesErrores.advertencia("Antcipo ya aplicado, no se puede borrar");
                    return null;
                }
                for (Kardexbanco k : kl) {
                    kardex = k;
                }
                if (kardex != null) {
                    if (kardex.getSpi() != null) {
                        MensajesErrores.fatal("Antcipo ya en SPI no es posible borrar");
                        return null;
                    }
                    ejbKardex.remove(kardex, seguridadbean.getLogueado().getUserid());
                }
                parametros = new HashMap();
                parametros.put(";where", "o.idmodulo=:idmodulo and o.opcion='ANTICIPO PROVEEDORES' and o.modulo=:modulo");
                parametros.put("idmodulo", anticipo.getId());
                parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
                List<Cabeceras> cl = ejbCabecera.encontarParametros(parametros);
                for (Cabeceras c1 : cl) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabecera=:cabecera");
                    parametros.put("cabecera", c1);
                    List<Renglones> rlen = ejbRenglones.encontarParametros(parametros);
                    for (Renglones r : rlen) {
                        ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                    ejbCabecera.remove(c1, seguridadbean.getLogueado().getUserid());
                }
                Contratos con = anticipo.getContrato();
                con.setEstado(1);
                ejbContratos.edit(con, seguridadbean.getLogueado().getUserid());
                ejbAnticipos.remove(anticipo, seguridadbean.getLogueado().getUserid());

            }
        } catch (BorrarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
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

    public Anticipos traer(Integer id) throws ConsultarException {
        return ejbAnticipos.find(id);
    }

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPfinal();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "AnticiposProveedorVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
////            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
////                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
////                seguridadbean.cerraSession();
////            }
//        }
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
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    public SelectItem[] getComboAnticiposProveedor() {
        if (proveedorBean.getEmpresa() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.proveedor=:proveedor and o.pagado=false ");
//        parametros.put(";where", "o.proveedor=:proveedor and o.pagado=false and o.contrato is null");
        parametros.put("proveedor", proveedorBean.getEmpresa().getProveedores());
//        parametros.put(";orden", "o.inicio desec");
        try {
            return Combos.getSelectItems(ejbAnticipos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboAnticiposContrato() {
        if (contrato == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato and o.pagado=false ");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.inicio desec");
        try {
            return Combos.getSelectItems(ejbAnticipos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AnticiposProveedoresBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
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
     * @return the aplicado
     */
    public boolean isAplicado() {
        return aplicado;
    }

    /**
     * @param aplicado the aplicado to set
     */
    public void setAplicado(boolean aplicado) {
        this.aplicado = aplicado;
    }

    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * @return the formularioContabilizar
     */
    public Formulario getFormularioContabilizar() {
        return formularioContabilizar;
    }

    /**
     * @param formularioContabilizar the formularioContabilizar to set
     */
    public void setFormularioContabilizar(Formulario formularioContabilizar) {
        this.formularioContabilizar = formularioContabilizar;
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
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
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

    public String getCuantoStr() {
        return ConvertirNumeroALetras.convertNumberToLetter(anticipo.getValor().doubleValue());
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(anticipo.getValor().doubleValue());
    }

    public SelectItem[] getComboCuentas() {
        if (cuentas == null) {
            return null;
        }
        int size = cuentas.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        items[0] = new SelectItem(0, "--- Seleccione uno ---");
//        i++;
        for (Cuentas x : cuentas) {
            items[i++] = new SelectItem(x.getCodigo(), x.toString());
        }
        return items;
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
}
