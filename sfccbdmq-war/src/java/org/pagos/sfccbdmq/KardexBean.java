/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pagos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.ReciboIngresos;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.CajasFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.FondosFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SpiFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.ValescajasFacade;
import org.beans.sfccbdmq.ValesfondosFacade;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Bancos;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Spi;
import org.entidades.sfccbdmq.Tipoasiento;
import org.entidades.sfccbdmq.Tipomovbancos;
import org.entidades.sfccbdmq.Valescajas;
import org.entidades.sfccbdmq.Valesfondos;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.personal.sfccbdmq.ValesCajaBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "kardexBancosSfccbdmq")
@ViewScoped
public class KardexBean {

    /**
     * Creates a new instance of KardexbancoBean
     */
    public KardexBean() {
        listakardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private Kardexbanco kardex;
    private LazyDataModel<Kardexbanco> listakardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Perfiles perfil;
    @EJB
    private KardexbancoFacade ejbKardexbanco;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private TipoasientoFacade ejbTipoAsiento;
    @EJB
    private CabeceraempleadosFacade ejbCabEmp;
    @EJB
    private SpiFacade ejbSpi;
    @EJB
    private FondosFacade ejbFondos;
    @EJB
    private ValesfondosFacade ejbValesfondos;
    @EJB
    private CajasFacade ejbCajas;
    @EJB
    private ValescajasFacade ejbValescajas;
    @EJB
    private ViaticosFacade ejbViaticos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private DetalleviaticosFacade ejbDetalleviaticos;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{clientesSfccbdmq}")
    private ClientesBean clientesBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private String documento;
    private String observaciones;
    private String auxiliar;
    private Bancos banco;
    private Date desde;
    private Date hasta;
    private Tipomovbancos tipo;
    private double valor;
    private boolean proveedores;
    private int numero;
    private Integer numeroSpi;
    private List<Renglones> renglones;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;
    private Resource reportePropuesta;
    private Resource reporteRecibo;

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
     * @return the listakardex
     */
    public LazyDataModel<Kardexbanco> getListakardex() {
        return listakardex;
    }

    /**
     * @param listakardex the listakardex to set
     */
    public void setListakardex(LazyDataModel<Kardexbanco> listakardex) {
        this.listakardex = listakardex;
    }

    public String buscar() {

        listakardex = new LazyDataModel<Kardexbanco>() {

            @Override
            public List<Kardexbanco> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fechamov desc,o.id");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fechamov between :desde and :hasta and o.tipomov.tipo in ('OTR','CAJA','VIAT') and o.estado>=0";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (tipo != null) {
                    where += " and o.tipomov=:tipomov";
                    parametros.put("tipomov", tipo);
                }
                if (banco != null) {
                    where += " and o.banco=:banco";
                    parametros.put("banco", banco);
                }
                if ((valor > 0)) {
                    where += "and  o.valor=:valor";
                    parametros.put("valor", valor);
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("observaciones", "%" + observaciones.toUpperCase() + "%");
                }
                if (!((documento == null) || (documento.isEmpty()))) {
                    where += " and upper(o.documento) like :documento";
                    parametros.put("documento", "%" + documento.toUpperCase() + "%");
                }
                if ((numero > 0)) {
                    where = " o.id=:id and o.tipomov.tipo='OTR' and o.estado>=0";
                    parametros = new HashMap();
                    parametros.put("id", numero);
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbKardexbanco.contar(parametros);
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
                listakardex.setRowCount(total);
                try {
                    return ejbKardexbanco.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo() throws ConsultarException {

        formulario.insertar();
        kardex = new Kardexbanco();
        kardex.setUsuariograba(seguridadbean.getLogueado());
        Map parametros = new HashMap();
        parametros.put(";campo", "o.numero");
        numeroSpi = ejbSpi.maximoNumero(parametros);
        if ((numeroSpi == null) || (numeroSpi == 0)) {
            numeroSpi = 307;
        }
        numeroSpi++;
        return null;
    }

    public String modifica(Kardexbanco kardex) {

        this.kardex = kardex;

//        ES-2018-04-02 TRAER EL AUXILIAR INDICADO
//        Cuentas c = cuentasBean.traerCodigo(kardex.getTipomov().getCuenta());
//        if (c.getAuxiliares() != null) {
//            if (c.getAuxiliares().getParametros().equals("P")) {
//                proveedoresBean.setRuc(kardex.getAuxiliar());
//            } else if (c.getAuxiliares().getParametros().equals("E")) {
//                empleadosBean.setCedula(kardex.getAuxiliar());
//            } else if (c.getAuxiliares().getParametros().equals("C")) {
//                clientesBean.setRuc(kardex.getAuxiliar());
//            }
//        }
        if (kardex.getSpi() == null) {
            try {
                Map parametros = new HashMap();
                parametros.put(";campo", "o.numero");
                numeroSpi = ejbSpi.maximoNumero(parametros);
                if ((numeroSpi == null) || (numeroSpi == 0)) {
                    numeroSpi = 307;
                }
                numeroSpi++;
            } catch (ConsultarException ex) {
                Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            numeroSpi = kardex.getSpi().getNumero();
        }
        formulario.editar();

        return null;
    }

    public String contabiliza(Kardexbanco karde) {
        kardex = karde;
        contabilizar(false);
        reporte = null;
        reportePropuesta = null;
        reporteRecibo = null;
        formularioImprimir.insertar();
        return null;
    }

    public String imprimir(Kardexbanco kardex) {
        reporte = null;
        reportePropuesta = null;
        reporteRecibo = null;
        this.kardex = kardex;
        try {
            traerRenglones();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            DocumentoPDF pdf = new DocumentoPDF("Comprobante de " + this.kardex.getTipomov().getDescripcion(), null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
            List<AuxiliarReporte> columnas = new LinkedList<>();

            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "Paguese a la orden de:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, kardex.getBeneficiario() != null ? kardex.getBeneficiario() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 10, false, "Valor :"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 10, false, getValorStr()));
            pdf.agregarTabla(null, columnas, 2, 100, null);

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "Banco :            Cta No : " + kardex.getBanco().getCuenta() + " Banco : " + kardex.getBanco().getNombre()));
//            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "Fecha de emisón : " + kardex.getFechagraba() != null ? sdf.format(kardex.getFechagraba()) : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "Fecha de emisón : " + kardex.getFechamov() != null ? sdf.format(kardex.getFechamov()) : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "C. Egreso :       " + kardex.getEgreso() != null ? kardex.getEgreso() + "" : (kardex.getDocumento() != null ? kardex.getDocumento() : "")));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "La suma de :      " + getCuantoStr()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 10, false, "Referencia :      " + kardex.getObservaciones()));
            pdf.agregarTabla(null, columnas, 1, 100, null);

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Nombre Cuenta"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "Auxiliar"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Débitos"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "Créditos"));
            columnas = new LinkedList<>();
            double sumaDebitos = 0.0;
            double sumaCreditos = 0.0;
            pdf.agregaParrafo("\n\n");
            if (renglones != null) {
                for (Renglones r : renglones) {
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, r.getCuenta()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, cuentasBean.traerCodigo(r.getCuenta()).getNombre()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, r.getReferencia()));
                    columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, r.getAuxiliar()));
                    double valor = r.getValor().doubleValue();
                    if (valor > 0) {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valor));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, 0.0));
                        sumaDebitos += valor;
                    } else {
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, 0.0));
                        columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valor * -1));
                        sumaCreditos += valor * -1;
                    }
                }
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 8, false, "TOTAL"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, sumaDebitos));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, sumaCreditos));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "");
            pdf.agregaParrafo("\n\n");
            reporte = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (kardex.getPropuesta() != null) {
            imprimirPropuesta(kardex);
        }
        if (kardex.getTipomov().getGeneracomprobante()) {
            grabarEnHoja(kardex);
        }
        formularioImprimir.editar();
        return null;
    }

    public String imprimirPropuesta(Kardexbanco k) {
        try {
            DocumentoPDF pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());

            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha emisión"));
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
            String tipoCuenta;
            if (k.getTcuentatrans() == null) {
                tipoCuenta = "";
            } else {
                tipoCuenta = k.getTcuentatrans().toString();
            }
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBeneficiario()));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, numeroCuenta));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, tipoCuenta));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, k.getBancotransferencia() != null ? k.getBancotransferencia().toString() : ""));
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
            pdf.agregaTitulo("PROPUESTA DE PAGO - " + k.getPropuesta());
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

            reportePropuesta = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarEnHoja(Kardexbanco ing) {
        try {
            ReciboIngresos hoja = new ReciboIngresos();
            hoja.llenarKardex(ing);
            reporteRecibo = hoja.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValesCajaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public class valorComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Renglones r = (Renglones) o1;
            Renglones r1 = (Renglones) o2;
            return r1.getValor().
                    compareTo(r.getValor());

        }
    }

    public String grabarContabilizar() {

        grabarFinalViaticoCajaFondo();
        contabilizar(true);
        return null;
    }

    public String grabarFinalViaticoCajaFondo() {
        try {
            if (kardex.getTipomov().getTipo().equals("OTR")) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.empleado.entidad.pin=:pin and o.cerrado=false");
                parametros.put("pin", kardex.getAuxiliar());
                List<Fondos> listaFondo = ejbFondos.encontarParametros(parametros);
                if (!listaFondo.isEmpty()) {
                    for (Fondos f : listaFondo) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.fondo=:fondo");
                        parametros.put("fondo", f);
                        List<Valesfondos> listaValeF = ejbValesfondos.encontarParametros(parametros);
                        if (listaValeF.isEmpty()) {
                            double cuadre = Math.round(f.getValor().doubleValue());
                            double dividido = cuadre / 100;
                            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                            double valor1 = (valortotal.doubleValue());

                            double cuadre3 = Math.round(kardex.getValor().doubleValue());
                            double dividido3 = cuadre3 / 100;
                            BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
                            double valor2 = (valortotal3.doubleValue());

                            if (valor1 == valor2) {
                                f.setCerrado(Boolean.TRUE);
                                ejbFondos.edit(f, seguridadbean.getLogueado().getUserid());
                            }
                        }
                    }
                }
            } else {
                if (kardex.getTipomov().getTipo().equals("CAJA")) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.empleado.entidad.pin=:pin and o.liquidado=false and o.apertura is null");
                    parametros.put("pin", kardex.getAuxiliar());
                    List<Cajas> listaCaja = ejbCajas.encontarParametros(parametros);
                    if (!listaCaja.isEmpty()) {
                        for (Cajas c : listaCaja) {
                            parametros = new HashMap();
                            parametros.put(";where", "o.caja=:caja");
                            parametros.put("caja", c);
                            List<Valescajas> listaValeC = ejbValescajas.encontarParametros(parametros);
                            if (listaValeC.isEmpty()) {
                                double cuadre = Math.round(c.getValor().doubleValue());
                                double dividido = cuadre / 100;
                                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                                double valor1 = (valortotal.doubleValue());

                                double cuadre3 = Math.round(kardex.getValor().doubleValue());
                                double dividido3 = cuadre3 / 100;
                                BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
                                double valor2 = (valortotal3.doubleValue());

                                if (valor1 == valor2) {
                                    c.setLiquidado(Boolean.TRUE);
                                    ejbCajas.edit(c, seguridadbean.getLogueado().getUserid());
                                }
                            }
                        }
                    }
                } else {
                    if (kardex.getTipomov().getTipo().equals("VIAT")) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.empleado.entidad.pin=:pin");
                        parametros.put("pin", kardex.getAuxiliar());
                        List<Viaticosempleado> listaViaticos = ejbViaticosempleado.encontarParametros(parametros);
                        if (!listaViaticos.isEmpty()) {
                            for (Viaticosempleado ve : listaViaticos) {
                                parametros = new HashMap();
                                parametros.put(";where", "o.viaticosempleado=:viaticosempleado");
                                parametros.put("viaticosempleado", ve);
                                Viaticos v = ve.getViatico();
                                List<Detalleviaticos> listaDetalleV = ejbDetalleviaticos.encontarParametros(parametros);
                                if (listaDetalleV.isEmpty()) {
                                    double cuadre = Math.round(ve.getValor().doubleValue());
                                    double dividido = cuadre / 100;
                                    BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                                    double valor1 = (valortotal.doubleValue());

                                    double cuadre3 = Math.round(kardex.getValor().doubleValue());
                                    double dividido3 = cuadre3 / 100;
                                    BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
                                    double valor2 = (valortotal3.doubleValue());

                                    if (valor1 == valor2) {
                                        ve.setRealizoviaje(Boolean.FALSE);
                                        ve.setFechaliquidacion(kardex.getFechamov());
                                        ve.setEstado(3);
                                        ejbViaticosempleado.edit(ve, seguridadbean.getLogueado().getUserid());
                                        v.setVigente(Boolean.FALSE);
                                        ejbViaticos.edit(v, seguridadbean.getLogueado().getUserid());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String contabilizar(boolean definitivo) {
        try {

            Cuentas cuentaBanco = getCuentasBean().traerCodigo(getKardex().getBanco().getCuenta());
            getCuentasBean().setCuenta(cuentaBanco);
            if (getCuentasBean().validaCuentaMovimiento()) {
                MensajesErrores.advertencia("Cuenta contable de banco mal configurada");
                return null;
            }
            Cuentas cuentaMovimiento = getCuentasBean().traerCodigo(kardex.getTipomov().getCuenta());
            getCuentasBean().setCuenta(cuentaMovimiento);
            if (getCuentasBean().validaCuentaMovimiento()) {
                MensajesErrores.advertencia("Cuenta contable de movimiento mal configurada");
                return null;
            }
            String vale = ejbCabecera.validarCierre(kardex.getFechamov());
            if (vale != null) {
                MensajesErrores.advertencia(vale);
                return null;
            }
            kardex.setEstado(2);
            kardex.setFechaabono(kardex.getFechamov());
//        formulario.editar();
            int productoBanco = -1;
            int productoTipo = 1;
            if (kardex.getTipomov().getIngreso()) {
                productoBanco = 1;
                productoTipo = -1;
            }
            Tipoasiento ta = getKardex().getTipomov().getTipoasiento();

            int numeroAsiento = ta.getUltimo();
            numeroAsiento++;
            ta.setUltimo(numeroAsiento);
            renglones = new LinkedList<>();

            Cabeceras cab = new Cabeceras();
            if (definitivo) {
                ejbTipoAsiento.edit(ta, seguridadbean.getLogueado().getUserid());

                cab.setDescripcion(getKardex().getObservaciones());
                cab.setModulo(perfil.getMenu().getIdpadre().getModulo());
                cab.setDia(new Date());
                cab.setTipo(getKardex().getTipomov().getTipoasiento());
                cab.setNumero(numeroAsiento);
                cab.setFecha(kardex.getFechamov());
                cab.setIdmodulo(kardex.getId());
                cab.setUsuario(seguridadbean.getLogueado().getUserid());
                cab.setOpcion("KARDEX BANCOS");
                ejbCabecera.create(cab, seguridadbean.getLogueado().getUserid());
            }
            Renglones r1 = new Renglones(); // reglon de banco
            if (definitivo) {
                r1.setCabecera(cab);
            }
            r1.setReferencia(kardex.getObservaciones());
            r1.setValor(new BigDecimal(kardex.getValor().doubleValue() * productoBanco));
            r1.setCuenta(cuentaBanco.getCodigo());
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            r1.setFecha(kardex.getFechamov());
            if (cuentaBanco.getAuxiliares() != null) {
                r1.setAuxiliar(kardex.getAuxiliar());
            }
            r1.setPresupuesto(cuentaBanco.getPresupuesto());
            Renglones r = new Renglones(); // reglon de banco
            if (definitivo) {
                r.setCabecera(cab);
            }
            r.setReferencia(kardex.getObservaciones());
            r.setValor(new BigDecimal(kardex.getValor().doubleValue() * productoTipo));
            r.setCuenta(cuentaMovimiento.getCodigo());
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            r.setFecha(kardex.getFechamov());
            if (cuentaMovimiento.getAuxiliares() != null) {
                r.setAuxiliar(kardex.getAuxiliar());
            }
            r.setPresupuesto(cuentaMovimiento.getPresupuesto());
            if (kardex.getTipomov() != null) {

                if (kardex.getTipomov().getSpi()) {
                    if (definitivo) {
                        // crea el SPI creo que esta en rebotes

                        Spi spi = new Spi();
                        spi.setEstado(0);
                        spi.setFecha(kardex.getFechamov());
                        spi.setUsuario(seguridadbean.getLogueado().getUserid());
                        spi.setBanco(kardex.getBanco());
                        spi.setNumero(getNumeroSpi());
                        ejbSpi.create(spi, seguridadbean.getLogueado().getUserid());
                        kardex.setSpi(spi);
                        kardex.setEstado(2);
                        kardex.setFechaabono(kardex.getFechamov());
//                        ejbKardexbanco.edit(kardex, seguridadbean.getLogueado().getUserid());
//                        formularioImprimir.cancelar();
                    }
//                    return null;
                } // Fin de definitivo

            }
            if (definitivo) {
                ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
                ejbRenglones.create(r1, seguridadbean.getLogueado().getUserid());
                ejbKardexbanco.edit(kardex, seguridadbean.getLogueado().getUserid());
            }
            getRenglones().add(r);
            getRenglones().add(r1);
            formularioImprimir.cancelar();
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void traerRenglones() {
        if (kardex != null) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera.idmodulo=:id"
                    + " and o.cabecera.modulo=:modulo and o.cabecera.opcion='KARDEX BANCOS'");
            parametros.put("id", kardex.getId());
            parametros.put("modulo", perfil.getMenu().getIdpadre().getModulo());
            try {
                renglones = ejbRenglones.encontarParametros(parametros);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String eliminar(Kardexbanco kardex) {

        this.kardex = kardex;
        formulario.eliminar();

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
        if (kardex.getBanco() == null) {
            MensajesErrores.advertencia("Es necesario banco del movimiento");
            return true;
        }

        if (kardex.getTipomov() == null) {
            MensajesErrores.advertencia("Es necesario tipo de movimiento");
            return true;
        }
        if (kardex.getTipomov().getSpi()) {
            if ((kardex.getPropuesta() == null) || (kardex.getPropuesta().isEmpty())) {
                MensajesErrores.advertencia("Es necesario Propuesta de pago");
                return true;
            }
        }
        if ((kardex.getValor() == null) || (kardex.getValor().doubleValue() <= 0)) {
            MensajesErrores.advertencia("Es necesario valor de movimiento");
            return true;
        }
        int esAuxiliar = getPideAuxiliar();
        switch (esAuxiliar) {
            case 1:
                if (getProveedoresBean().getProveedor() == null) {
                    MensajesErrores.advertencia("Es necesario proveedor");
                    return true;
                }
                kardex.setAuxiliar(proveedoresBean.getProveedor().getEmpresa().getRuc());
                String beneficiario = proveedoresBean.getEmpresa().getNombre();
                if (!((proveedoresBean.getProveedor().getNombrebeneficiario() == null) || (proveedoresBean.getProveedor().getNombrebeneficiario().isEmpty()))) {
                    beneficiario = proveedoresBean.getProveedor().getNombrebeneficiario();
                }
                if (!(kardex.getTipomov().getIngreso())) {
                    kardex.setBeneficiario(beneficiario);
                    if ((proveedoresBean.getProveedor().getCtabancaria() == null) || (proveedoresBean.getProveedor().getCtabancaria().isEmpty())) {
                        MensajesErrores.advertencia("Es necesario No cuenta bancaria del proveedor");
                        return true;
                    }
                    kardex.setProveedor(proveedoresBean.getProveedor());
                    kardex.setCuentatrans(proveedoresBean.getProveedor().getCtabancaria());
                    if ((proveedoresBean.getProveedor().getTipocta() == null)) {
                        MensajesErrores.advertencia("Es necesario tipo de cuenta bancaria del proveedor");
                        return true;
                    }
                    String tipoCta = proveedoresBean.getProveedor().getTipocta().getParametros();
                    kardex.setTcuentatrans(Integer.parseInt(tipoCta));
                } else {
                    kardex.setBeneficiario(beneficiario);
                }
                break;
            case 2:
                if (empleadosBean.getEntidad() == null) {
                    MensajesErrores.advertencia("Es necesario empleado");
                    return true;
                }
                Empleados e = empleadosBean.getEntidad().getEmpleados();
                kardex.setAuxiliar(empleadosBean.getEntidad().getPin());
                Codigos codigoBanco = traerBanco(e);
                if (codigoBanco == null) {
                    MensajesErrores.advertencia("No existe banco para empleado : " + e.toString());
                    return true;
                }
                String ctaTrans = traer(e, "NUMCUENTA");
                if (ctaTrans == null) {
                    MensajesErrores.advertencia("No existe No cuenta para empleado : " + e.toString());
                    return true;
                }

                String tipoCta1 = traerTipoCuenta(e);
                if (tipoCta1 == null) {
                    MensajesErrores.advertencia("No existe Tipo de cuentapara empleado : " + e.toString());
                    return true;
                }

                kardex.setBeneficiario(e.getEntidad().toString());
                kardex.setBancotransferencia(codigoBanco);
                kardex.setCuentatrans(traer(e, "NUMCUENTA"));
                kardex.setTcuentatrans(Integer.parseInt(tipoCta1));
                break;
            case 3:
                if (clientesBean.getCliente() == null) {
                    MensajesErrores.advertencia("Es necesario cliente");
                    return true;
                }
                kardex.setAuxiliar(clientesBean.getCliente().getEmpresa().getRuc());
                break;
            case 4:
                if ((kardex.getAuxiliar() == null) || (kardex.getAuxiliar().isEmpty())) {
                    MensajesErrores.advertencia("Es necesario Auxiliar");
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            kardex.setFechagraba(new Date());
            kardex.setOrigen("MOVIMIENTOS BANCO");
            kardex.setEstado(0);
            ejbKardexbanco.create(kardex, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
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
            kardex.setEstado(0);
            ejbKardexbanco.edit(kardex, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbKardexbanco.remove(kardex, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(KardexBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public Kardexbanco traer(Integer id) throws ConsultarException {
        return ejbKardexbanco.find(id);
    }

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "KardexbancoVista";
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
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
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
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * @return the banco
     */
    public Bancos getBanco() {
        return banco;
    }

    /**
     * @param banco the banco to set
     */
    public void setBanco(Bancos banco) {
        this.banco = banco;
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
     * @return the tipo
     */
    public Tipomovbancos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tipomovbancos tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the numero
     */
    public int getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(int numero) {
        this.numero = numero;
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
        return ConvertirNumeroALetras.convertNumberToLetter(kardex.getValor().doubleValue());
    }

    public String getValorStr() {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(kardex.getValor().doubleValue());
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
     * @return the proveedores
     */
    public boolean isProveedores() {
        return proveedores;
    }

    /**
     * @param proveedores the proveedores to set
     */
    public void setProveedores(boolean proveedores) {
        this.proveedores = proveedores;
    }

    public int getPideAuxiliar() {
        if (kardex == null) {
            return 0;
        }
        if (kardex.getTipomov() == null) {
            return 0;
        }
        Cuentas c = cuentasBean.traerCodigo(kardex.getTipomov().getCuenta());
        if (c == null) {
            return 0;
        }
        if (c.getAuxiliares() != null) {
            if (c.getAuxiliares().getParametros().equals("P")) {
                return 1;
            } else if (c.getAuxiliares().getParametros().equals("E")) {
                return 2;
            } else if (c.getAuxiliares().getParametros().equals("C")) {
                return 3;
            }
        }
        return 4;
    }

    /**
     * @return the clientesBean
     */
    public ClientesBean getClientesBean() {
        return clientesBean;
    }

    /**
     * @param clientesBean the clientesBean to set
     */
    public void setClientesBean(ClientesBean clientesBean) {
        this.clientesBean = clientesBean;
    }

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String traerTipoCuenta(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='TIPOCUENTA' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getParametros();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Codigos traerBanco(Empleados e) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto='INSTBANC' and o.empleado=:empleado");
            paremetros.put("empleado", e);
//            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                // traer el codigo
                return getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public String traer(Empleados e, String que) {
        try {
            Map paremetros = new HashMap();
            paremetros.put(";where", "o.cabecera.texto=:que and o.empleado=:empleado");
            paremetros.put("empleado", e);
            paremetros.put("que", que);
            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
            for (Cabeceraempleados c : lista) {
                String retorno = c.getValortexto();
                if (c.getCabecera().getTipodato() == 4) {
                    // traer el codigo
                    Codigos codigo = codigosBean.traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
                    return codigo.getCodigo() + " - " + codigo.getNombre();
                }
                return retorno;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    public String traerTipoCuenta(Empleados e) {
//        try {
//            Map paremetros = new HashMap();
//            paremetros.put(";where", "o.cabecera.texto='TIPOCUENTA' and o.empleado=:empleado");
//            paremetros.put("empleado", e);
////            paremetros.put("que", que);
//            List<Cabeceraempleados> lista = ejbCabEmp.encontarParametros(paremetros);
//            for (Cabeceraempleados c : lista) {
//                String retorno = c.getValortexto();
//                if (c.getCabecera().getTipodato() == 4) {
//                    // traer el codigo
//                    Codigos codigo = getCodigosBean().traerCodigo(c.getCabecera().getCodigo(), c.getCodigo());
//                    return codigo.getParametros();
//                }
//                return retorno;
//            }
//        } catch (ConsultarException ex) {
//            Logger.getLogger(PagoRolEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    /**
     * @return the numeroSpi
     */
    public Integer getNumeroSpi() {
        return numeroSpi;
    }

    /**
     * @param numeroSpi the numeroSpi to set
     */
    public void setNumeroSpi(Integer numeroSpi) {
        this.numeroSpi = numeroSpi;
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
     * @return the reporteRecibo
     */
    public Resource getReporteRecibo() {
        return reporteRecibo;
    }

    /**
     * @param reporteRecibo the reporteRecibo to set
     */
    public void setReporteRecibo(Resource reporteRecibo) {
        this.reporteRecibo = reporteRecibo;
    }
}
