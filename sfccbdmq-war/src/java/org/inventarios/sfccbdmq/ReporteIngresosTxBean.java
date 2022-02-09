/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;
import org.compras.sfccbdmq.ObligacionesCompromisoBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.beans.sfccbdmq.AdicionalkardexFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.OrganigramasuministrosFacade;
import org.beans.sfccbdmq.SolicitudsuministrosFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.beans.sfccbdmq.UnidadesFacade;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Solicitudsuministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reporteIngresosTxSfccbdmq")
@ViewScoped
public class ReporteIngresosTxBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public ReporteIngresosTxBean() {
        cabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    @EJB
    private CabecerainventarioFacade ejbCabecerainventario;

    @EJB
    private TxinventariosFacade ejbTxInventario;
    @EJB
    private AdicionalkardexFacade ejbAdKardex;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private UnidadesFacade ejbUnidades;
    @EJB
    private SolicitudsuministrosFacade ejbSolSum;
    @EJB
    private DetallesolicitudFacade ejbDetSol;
    @EJB
    private OrganigramasuministrosFacade ejbOrgaSum;
    @EJB
    private BodegasuministroFacade ejbBodSum;
    private Cabecerainventario cabecera;
    private Kardexinventario kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Cabecerainventario> cabeceras;
    private List<Kardexinventario> listaKardex;
    private Integer factura;
    private Integer numero;
    private double descuentoInterno;
    private double descuentoExterno;
    private Integer estado = -10;
    private String observaciones;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Solicitudsuministros solicitud;
    private Bodegas bodega;
    private Bodegas bodegaDestino;
    private boolean transferencia;
    private boolean ingreso = true;
    private Contratos contrato;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{obligacionesCompromisoSfccbdmq}")
    private ObligacionesCompromisoBean obligacionesBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;

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
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "ReporteInventarioVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            seguridadbean.cerraSession();
//        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
//    }
    }

    /**
     * @return the cabecera
     */
    public Cabecerainventario getCabecera() {
        return cabecera;
    }

    /**
     * @param cabecera the cabecera to set
     */
    public void setCabecera(Cabecerainventario cabecera) {
        this.cabecera = cabecera;
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
     * @return the cabeceras
     */
    public LazyDataModel<Cabecerainventario> getCabeceras() {
        return cabeceras;
    }

    /**
     * @param cabeceras the cabeceras to set
     */
    public void setCabeceras(LazyDataModel<Cabecerainventario> cabeceras) {
        this.cabeceras = cabeceras;
    }

    public String buscar() {
        cabeceras = new LazyDataModel<Cabecerainventario>() {

            @Override
            public List<Cabecerainventario> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha asc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta  and o.txid.ingreso=:ingreso";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                // Numero de factura
                if (!((factura == null) || (factura <= 0))) {
                    where += " and o.obligacion.documento =:documento";
                    parametros.put("documento", factura);
                }
                if (!((estado == null) || (estado <= -10))) {
                    where += " and o.estado =:estado";
                    parametros.put("estado", estado);
                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.numero =:numero";
                    parametros.put("numero", numero);
                }
                if (!((observaciones == null) || (observaciones.isEmpty()))) {
                    where += " and upper(o.observaciones) like :observaciones";
                    parametros.put("marca", observaciones.toUpperCase() + "%");
                }
                if (bodega != null) {
                    where += " and o.bodega=:bodega";
                    parametros.put("bodega", bodega);
                }

                if (tipo != null) {
                    where += " and o.txid=:tipo";
                    parametros.put("tipo", tipo);
                }
                if (contrato != null) {
                    where += " and o.ordencompra.compromiso.contrato=:contrato";
                    parametros.put("contrato", contrato);
                }
                if (proveedoresBean.getProveedor() != null) {
                    where += " and o.ordencompra.compromiso.proveedor=:proveedor";
                    parametros.put("proveedor", proveedoresBean.getProveedor());
                }

                int total = 0;

                try {
                    parametros.put(";where", where);
                    parametros.put("ingreso", ingreso);
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);

                    total = ejbCabecerainventario.contar(parametros);
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
                cabeceras.setRowCount(total);
                try {
                    return ejbCabecerainventario.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String imprimir() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
//            parametros = new HashMap();
//            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
//            parametros.put("expresado", cabecera.getTxid().getNombre() + " No : " + cabecera.getId());
//            parametros.put("nombrelogo", "logo-new.png");
//            parametros.put("usuario", seguridadbean.getLogueado().getUserid());
//            parametros.put("fecha", cabecera.getFecha());
//            parametros.put("documento", cabecera.getId());
//            parametros.put("modulo", cabecera.getBodega().getNombre());
//            parametros.put("descripcion", cabecera.getObservaciones());
//            parametros.put("transaccion", cabecera.getTxid().getNombre());
//            parametros.put("proveedor", (cabecera.getProveedor() == null ? null : cabecera.getProveedor().getEmpresa().toString()));
//            parametros.put("contrato", (cabecera.getContrato() == null ? null : cabecera.getContrato().toString()));
//            Calendar c = Calendar.getInstance();
//            reporte = new Reportesds(parametros,
//                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Inventario.jasper"),
//                    listaKardex, "Inventario" + String.valueOf(c.getTimeInMillis()) + ".pdf");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("Fecha de emisón : " + sdf.format(cabecera.getFecha()));
            pdf.agregaParrafo("Documento : " + cabecera.getNumero());
            pdf.agregaParrafo("Bodega : " + cabecera.getBodega().getNombre());
            pdf.agregaParrafo("Transacción : " + cabecera.getTxid().getNombre());
            pdf.agregaParrafo("Descripción : " + cabecera.getObservaciones());

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "Suministro"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "Unidad"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "Cantidad"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "C. Inversión"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "Costo"));
            titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, true, "Total"));
            double total = 0;
            for (Kardexinventario a : listaKardex) {
                double valor = 0;
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getSuministro().getCodigobarras()));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getUnidad().getEquivalencia()));
                if (a.getCantidad() != 0) {
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCantidad()));
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, 0));
                    valor = a.getCantidad() * a.getCosto();

                } else {
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, 0));
                    valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCantidadinversion()));
                    valor = a.getCantidadinversion() * a.getCosto();
                }
                total += valor;
                valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, a.getCosto()));
                valores.add(new AuxiliarReporte("Double", 9, AuxiliarReporte.ALIGN_LEFT, 8, false, valor));
            }

            pdf.agregarTablaReporte(titulos, valores, 6, 100, "");

            pdf.agregaParrafo("\n");
            double iva = 0.00;
            valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "Total: "));
            valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, total));
            pdf.agregarTabla(null, valores, 2, 100, "");

            valores = new LinkedList<>();

            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 8, false, "___________________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "Preparado por"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "Revisado por"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 11, false, "Aprobado por"));
            pdf.agregarTabla(null, valores, 3, 100, "");

            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteIngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteIngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public double traerTotal(Cabecerainventario c) {

        // taer la kardex
        // solo egresos si es transferencia
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", c);
        double valor = 0;
        try {

            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Kardexinventario k : listaKardex) {
                valor += (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion())
                        * (k.getCostopinversion() == null ? 0 : k.getCostopinversion());
                valor += (k.getCantidad() == null ? 0 : k.getCantidad())
                        * (k.getCostopromedio() == null ? 0 : k.getCostopromedio());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteIngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public double getTotal() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
        return traerTotal(cabecera);
    }

    public double getCantidad() {
        Cabecerainventario c = (Cabecerainventario) cabeceras.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", c);
        double valor = 0;
        try {

            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Kardexinventario k : listaKardex) {
                valor += (k.getCantidad() == null ? 0 : k.getCantidad());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteIngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public double getCantidadInv() {
        Cabecerainventario c = (Cabecerainventario) cabeceras.getRowData();

        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", c);
        double valor = 0;
        try {

            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Kardexinventario k : listaKardex) {
                valor += (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteIngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
    }

    public double getTotalCabecera() {
//        cabecera = (Cabecerainventario) cabeceras.getRowData();

        // taer la kardex
        // solo egresos si es transferencia
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        double valor = 0;
        try {

            listaKardex = ejbKardex.encontarParametros(parametros);
            for (Kardexinventario k : listaKardex) {
                valor += (k.getCantidad()) * k.getCostopromedio();
                valor += (k.getCantidadinversion()) * k.getCostopinversion();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteIngresosTxBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return valor;
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

    public Cabecerainventario traer(Integer id) throws ConsultarException {
        return ejbCabecerainventario.find(id);
    }

    /**
     * @return the tipo
     */
    public Txinventarios getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Txinventarios tipo) {
        this.tipo = tipo;
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
     * @return the formularioRenglones
     */
    public Formulario getFormularioRenglones() {
        return formularioRenglones;
    }

    /**
     * @param formularioRenglones the formularioRenglones to set
     */
    public void setFormularioRenglones(Formulario formularioRenglones) {
        this.formularioRenglones = formularioRenglones;
    }

    /**
     * @return the listaKardex
     */
    public List<Kardexinventario> getListaKardex() {
        return listaKardex;
    }

    /**
     * @param listaKardex the listaKardex to set
     */
    public void setListaKardex(List<Kardexinventario> listaKardex) {
        this.listaKardex = listaKardex;
    }

    /**
     * @return the kardex
     */
    public Kardexinventario getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexinventario kardex) {
        this.kardex = kardex;
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
     * @return the bodega
     */
    public Bodegas getBodega() {
        return bodega;
    }

    /**
     * @param bodega the bodega to set
     */
    public void setBodega(Bodegas bodega) {
        this.bodega = bodega;
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
     * @return the factura
     */
    public Integer getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Integer factura) {
        this.factura = factura;
    }

    /**
     * @return the bodegaDestino
     */
    public Bodegas getBodegaDestino() {
        return bodegaDestino;
    }

    /**
     * @param bodegaDestino the bodegaDestino to set
     */
    public void setBodegaDestino(Bodegas bodegaDestino) {
        this.bodegaDestino = bodegaDestino;
    }

    /**
     * @return the transferencia
     */
    public boolean isTransferencia() {
        return transferencia;
    }

    /**
     * @param transferencia the transferencia to set
     */
    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
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
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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

    /**
     * @return the formularioObligacion
     */
    public Formulario getFormularioObligacion() {
        return formularioObligacion;
    }

    /**
     * @param formularioObligacion the formularioObligacion to set
     */
    public void setFormularioObligacion(Formulario formularioObligacion) {
        this.formularioObligacion = formularioObligacion;
    }

    /**
     * @return the obligacionesBean
     */
    public ObligacionesCompromisoBean getObligacionesBean() {
        return obligacionesBean;
    }

    /**
     * @param obligacionesBean the obligacionesBean to set
     */
    public void setObligacionesBean(ObligacionesCompromisoBean obligacionesBean) {
        this.obligacionesBean = obligacionesBean;
    }

    /**
     * @return the descuentoInterno
     */
    public double getDescuentoInterno() {
        return descuentoInterno;
    }

    /**
     * @param descuentoInterno the descuentoInterno to set
     */
    public void setDescuentoInterno(double descuentoInterno) {
        this.descuentoInterno = descuentoInterno;
    }

    /**
     * @return the descuentoExterno
     */
    public double getDescuentoExterno() {
        return descuentoExterno;
    }

    /**
     * @param descuentoExterno the descuentoExterno to set
     */
    public void setDescuentoExterno(double descuentoExterno) {
        this.descuentoExterno = descuentoExterno;
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
     * @return the solicitud
     */
    public Solicitudsuministros getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(Solicitudsuministros solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return the ingreso
     */
    public boolean isIngreso() {
        return ingreso;
    }

    /**
     * @param ingreso the ingreso to set
     */
    public void setIngreso(boolean ingreso) {
        this.ingreso = ingreso;
    }

    public String getTotalGeneral() {
        Map parametros = new HashMap();
        String where = "  o.cabecerainventario.fecha between :desde and :hasta  and o.cabecerainventario.txid.ingreso=:ingreso";
        // Numero de factura
        if (!((factura == null) || (factura <= 0))) {
            where += " and o.cabecerainventario.obligacion.documento =:documento";
            parametros.put("documento", factura);
        }
        if (!((estado == null) || (estado <= -10))) {
            where += " and o.cabecerainventario.estado =:estado";
            parametros.put("estado", estado);
        }
        if (!((numero == null) || (numero <= 0))) {
            where += " and o.cabecerainventario.numero =:numero";
            parametros.put("numero", numero);
        }
        if (!((observaciones == null) || (observaciones.isEmpty()))) {
            where += " and upper(o.cabecerainventario.observaciones) like :observaciones";
            parametros.put("marca", observaciones.toUpperCase() + "%");
        }
        if (bodega != null) {
            where += " and o.cabecerainventario.bodega=:bodega";
            parametros.put("bodega", bodega);
        }

        if (tipo != null) {
            where += " and o.cabecerainventario.txid=:tipo";
            parametros.put("tipo", tipo);
        }
        if (contrato != null) {
            where += " and o.cabecerainventario.ordencompra.compromiso.contrato=:contrato";
            parametros.put("contrato", contrato);
        }
        if (proveedoresBean.getProveedor() != null) {
            where += " and o.cabecerainventario.ordencompra.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getProveedor());
        }

        int total = 0;

        try {
            parametros.put(";where", where);
            parametros.put("ingreso", ingreso);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put(";campo", "(o.cantidad+o.cantidadinversion)*o.costopromedio");

            double valor = ejbKardex.sumarCampoDoble(parametros);
            DecimalFormat df = new DecimalFormat("###,##0.00");
            return df.format(valor);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String imprimirTodo() {

        Map parametros = new HashMap();
        Map parametros1 = new HashMap();
        parametros.put(";orden", "o.fecha asc");
        String where = "  o.fecha between :desde and :hasta  and o.txid.ingreso=:ingreso";
        // Numero de factura
        if (!((factura == null) || (factura <= 0))) {
            where += " and o.obligacion.documento =:documento";
            parametros.put("documento", factura);
        }
        if (!((estado == null) || (estado <= -10))) {
            where += " and o.estado =:estado";
            parametros.put("estado", estado);
            if (estado == 0) {
                parametros1.put("estado", "Digitadas");
            } else if (estado == 1) {
                parametros1.put("estado", "Contabilizadas");
            } else if (estado == -1) {
                parametros1.put("estado", "Anuladas");
            }

        }
        if (!((numero == null) || (numero <= 0))) {
            where += " and o.numero =:numero";
            parametros.put("numero", numero);
            parametros1.put("numero", numero);
        }
        if (!((observaciones == null) || (observaciones.isEmpty()))) {
            where += " and upper(o.observaciones) like :observaciones";
            parametros.put("observaciones", observaciones);
        }
        if (bodega != null) {
            where += " and o.bodega=:bodega";
            parametros.put("bodega", bodega);
            parametros1.put("bodega", bodega.getNombre());
        }

        if (tipo != null) {
            where += " and o.txid=:tipo";
            parametros.put("tipo", tipo);
        }
        if (contrato != null) {
            where += " and o.ordencompra.compromiso.contrato=:contrato";
            parametros.put("contrato", contrato);

        }
        if (proveedoresBean.getProveedor() != null) {
            where += " and o.ordencompra.proveedor=:proveedor";
            parametros.put("proveedor", proveedoresBean.getProveedor());
            parametros1.put("proveedor", proveedoresBean.getProveedor().getEmpresa().toString());
        }

        try {
            parametros.put(";where", where);
            parametros.put("ingreso", ingreso);
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Cabecerainventario> lista = ejbCabecerainventario.encontarParametros(parametros);
            for (Cabecerainventario c : lista) {
                if (c.getEstado() == 0) {
                    c.setEstadoTexto("Digitadas");
                } else if (c.getEstado() == 1) {
                    c.setEstadoTexto("Contabilizadas");
                } else if (c.getEstado() == -1) {
                    c.setEstadoTexto("Anuladas");
                }
                if (c.getProveedor() != null) {
                    c.setBeneficiario(c.getProveedor().getEmpresa().toString());
                } else if (c.getSolicitud() != null) {
                    c.setBeneficiario(c.getSolicitud().getEmpleadosolicita().getEntidad().toString());
                }
                c.setTotal(traerTotal(c));
            }
            parametros1.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros1.put("expresado", "Reporte de Movimientos de Inventario");
            parametros1.put("nombrelogo", "logo-new.png");
            parametros1.put("usuario", seguridadbean.getLogueado().getUserid());
            parametros1.put("desde", desde);
            parametros1.put("hasta", hasta);
            if (ingreso) {
                parametros1.put("tipo", "INGRESOS");
            } else {
                parametros1.put("tipo", "EGRESOS");
            }

            Calendar c = Calendar.getInstance();
            reporte = new Reportesds(parametros1,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/MovimientosInv.jasper"),
                    lista, "MovimientosInv" + String.valueOf(c.getTimeInMillis()) + ".pdf");
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formularioImprimir.insertar();
        return null;
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

    public String traerEstado(int estado, boolean ingreso) {
        if (ingreso) {
            switch (estado) {
                case 0:
                    return "INGRESO";
                case 1:
                    return "RECEPCION";
                case 2:
                    return "REVISION";
                case 3:
                    return "APROBACION";
                case -3:
                    return "TRANSFERENCIA";
                default:
                    break;
            }
        } else {
            switch (estado) {
                case 0:
                    return "REGISTRO";
                case 1:
                    return "DESPACHO";
                case -1:
                    return "ANULADO";
//            case 3:
//                return "APROBACION";
                default:
                    break;
            }
        }
        return "";
    }
}
