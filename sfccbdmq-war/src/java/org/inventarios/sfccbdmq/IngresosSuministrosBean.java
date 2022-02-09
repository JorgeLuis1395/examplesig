/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarKardex;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.BitacorasuministroFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.LotessuministrosFacade;
import org.beans.sfccbdmq.PermisosbodegasFacade;
import org.beans.sfccbdmq.SolicitudsuministrosFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.beans.sfccbdmq.UnidadesFacade;
import org.compras.sfccbdmq.ObligacionesCompromisoBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.BodegasBean;
import org.entidades.sfccbdmq.Bitacorasuministro;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Lotessuministros;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Permisosbodegas;
import org.entidades.sfccbdmq.Solicitudsuministros;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Txinventarios;
import org.entidades.sfccbdmq.Unidades;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "ingresosSuministrosSfccbdmq")
@ViewScoped
public class IngresosSuministrosBean {

    /**
     * @return the listaErrores
     */
    public List<AuxiliarKardex> getListaErrores() {
        return listaErrores;
    }

    /**
     * @param listaErrores the listaErrores to set
     */
    public void setListaErrores(List<AuxiliarKardex> listaErrores) {
        this.listaErrores = listaErrores;
    }

    /**
     * @return the suminitrosBean
     */
    public SuministrosBean getSuminitrosBean() {
        return suminitrosBean;
    }

    /**
     * @param suminitrosBean the suminitrosBean to set
     */
    public void setSuminitrosBean(SuministrosBean suminitrosBean) {
        this.suminitrosBean = suminitrosBean;
    }

    /**
     * Creates a new instance of CabeceraBean
     */
    public IngresosSuministrosBean() {
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
    private PermisosbodegasFacade ejbPermisos;
    @EJB
    private TxinventariosFacade ejbTxInventario;
    @EJB
    private LotessuministrosFacade ejbLotes;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private UnidadesFacade ejbUnidades;
    @EJB
    private SolicitudsuministrosFacade ejbSolSum;
    @EJB
    private DetallesolicitudFacade ejbDetSol;
    @EJB
    private BitacorasuministroFacade ejbBitacora;
    @EJB
    private BodegasuministroFacade ejbBodSum;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private UnidadesFacade ejbuUnidades;
    @EJB
    private CabecerasFacade ejbCabeceras;
    private Cabecerainventario cabecera;
    private Kardexinventario kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioErrores = new Formulario();
    private int estadoIngreso;
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Cabecerainventario> cabeceras;
    private List<Kardexinventario> listaKardex;
    private List<Kardexinventario> listaKardexb;
    private List<AuxiliarKardex> listaErrores;
    private Integer factura;
    private Integer numero;
    private double descuentoInterno;
    private double descuentoExterno;
    private double cantidadModificada;
    private Integer estado = -10;
    private String observaciones;
//    private String lote;
//    private Date fechaCaduca;
    private String separador = ";";
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Solicitudsuministros solicitud;
    private Bodegas bodega;
    private Bodegas bodegaDestino;
    private boolean transferencia;
    private Contratos contrato;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{obligacionesCompromisoSfccbdmq}")
    private ObligacionesCompromisoBean obligacionesBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suminitrosBean;
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
        String nombreForma = "IngresosSuministrosVista";
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
                    parametros.put(";orden", "o.fecha desc,o.numero desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.fecha between :desde and :hasta and o.txid.ingreso=true";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                // Numero de factura
                if (!((factura == null) || (factura <= 0))) {
                    where += " and o.factura =:documento";
                    parametros.put("documento", factura);
                }
                if ((estado != null)) {
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

    public String nuevo() {
        cabecera = new Cabecerainventario();
        cabecera.setBodega(bodega);
        formulario.insertar();
        cabecera.setEstado(0); // digitada
        listaKardex = new LinkedList<>();
        listaKardexb = new LinkedList<>();
        proveedoresBean.setRuc(null);
        proveedoresBean.setProveedor(null);
        descuentoExterno = 0;
        solicitud = new Solicitudsuministros();
        return null;
    }

    public String salir() {
        cabecera = null;
        formulario.cancelar();
        listaKardex = new LinkedList<>();
        listaKardexb = new LinkedList<>();
        proveedoresBean.setRuc(null);
        proveedoresBean.setProveedor(null);
        return null;
    }

    public String modifica(int estadoIngreso) {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
        this.estadoIngreso = estadoIngreso;
        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        if (estadoIngreso != 0) {
            int estadoActual = cabecera.getEstado();
            estadoActual++;
            if (estadoActual != estadoIngreso) {
                MensajesErrores.advertencia("No corresponde al estado a actualizar");
                return null;
            }
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("INVENTARIOSUM");
        imagenesBean.Buscar();
        descuentoExterno = 0;
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        listaKardexb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String transferencia() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
        // taer la kardex
        // solo egresos si es transferencia
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        estado = 0;
        estadoIngreso = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera.getCabecera());
        imagenesBean.setIdModulo(cabecera.getId());
        imagenesBean.setModulo("INVENTARIOSUM");
        imagenesBean.Buscar();
        cabecera.setEstado(estado);
        descuentoExterno = 0;
        try {
            List<Kardexinventario> listadoKardex = ejbKardex.encontarParametros(parametros);
            listaKardex = new LinkedList<>();
            for (Kardexinventario k : listadoKardex) {
                Kardexinventario k1 = new Kardexinventario();
                k1.setBodega(null);
                k1.setCabecerainventario(cabecera.getCabecera());
                if (k.getCantidad().doubleValue() > 0) {
                    k1.setCosto(k.getCostopromedio());
                } else {
                    k1.setCosto(k.getCostopinversion());
                }
//                k1.setCosto(k.getCostopromedio());
                k1.setCantidad(k.getCantidad());
                k1.setCantidadinversion(k.getCantidadinversion());
                k1.setCostopinversion(k.getCostopinversion());
                k1.setCostopromedio(k.getCostopromedio());
                k1.setFecha(null);
                k1.setHora(new Date());
                k1.setObservaciones(k.getObservaciones());
                k1.setSigno(1);
                k1.setTipoorden(1);
                k1.setSuministro(k.getSuministro());
                k1.setSaldoanterior(k.getSaldoanterior());
                k1.setSaldoanteriorinversion(k.getSaldoanteriorinversion());
                k1.setUnidad(k.getUnidad());
                k1.setLote(k.getLote());
                listaKardex.add(k1);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        listaKardexb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String imprimir() {
        this.cabecera = (Cabecerainventario) cabeceras.getRowData();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
            parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera and o.estado=3");
            parametros.put("cabecera", cabecera);
            List<Bitacorasuministro> bl = ejbBitacora.encontarParametros(parametros);
            String aprobado = "";
            for (Bitacorasuministro b : bl) {
                aprobado = b.getUsuario().getEntidad().toString();
            }
            DocumentoPDF pdf;
            DecimalFormat formatoMes = new DecimalFormat("00");
            DecimalFormat formatoAnio = new DecimalFormat("0000");
            List<AuxiliarReporte> titulos = new LinkedList<>();

            String cabeceraStr = configuracionBean.getConfiguracion().getNombre();
            cabeceraStr += "\n" + "INGRESO DE SUMINISTROS";

//            cabecera += "MES : " + formatoMes.format(mesDesde)
//                    + "/" + formatoAnio.format(anio);
            pdf = new DocumentoPDF(cabeceraStr, null, seguridadbean.getLogueado().getUserid());
            ///CABECERA
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Fecha de Mov. : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(this.cabecera.getFecha())));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Estado"));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, traerEstado(cabecera.getEstado())));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Transacción : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getTxid().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Bodega : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getBodega().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "No. : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getNumero() == null ? "" : cabecera.getNumero().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Proveedor : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getProveedor() == null ? "" : cabecera.getProveedor().getEmpresa().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Contrato : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getContrato() == null ? "" : cabecera.getContrato().toString()));

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "No Compromiso : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getCompromiso() == null ? "" : cabecera.getCompromiso().getNumerocomp().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Factura : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getFactura() == null ? "" : cabecera.getFactura().toString()));
            if (cabecera.getCabecera() != null) {
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "B. transf. : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getCabecera().getBodega().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Número de Egreso en transferencia"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getCabecera().getNumero().toString()));
            }
            pdf.agregarTabla(null, columnas, 4, 100, null);
            ////FIN DE CABECERA
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("Observaciones : " + cabecera.getObservaciones() + "\n\n");

            int totalCol = 8;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "No."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "SUMINISTRO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CANTIDAD"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CANTIDAD INVERSION"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "COSTO UNITARIO"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "IMPUESTO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "TOTAL"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "LOTE"));
//            totalCol++;
            int i = 0;
            double valorTotal = 0;
            double valorDescuento = 0;
            List<AuxiliarReporte> campos = new LinkedList<>();
            for (Kardexinventario k : listaKardex) {
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, String.valueOf(++i)));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getSuministro().getCodigobarras()));
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getSuministro().getNombre()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCantidad() == null ? 0 : k.getCantidad().doubleValue()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCosto().doubleValue()));
//                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, k.getSuministro().getImpuesto().getPorcentaje().doubleValue()));
                double totalLinea = k.getCosto().doubleValue()
                        * ((k.getCantidad() == null ? 0 : k.getCantidad().doubleValue())
                        + (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, totalLinea));
                valorTotal += totalLinea;
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getLote() == null ? "" : (k.getLote().getLote() == null ? "" : k.getLote().getLote())));
            }
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, "TOTAL"));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
//            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, ""));
            campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valorTotal));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            pdf.agregarTablaReporte(titulos, campos, totalCol, 100, "RENGLONES");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            String revisado = cabecera.getProveedor() == null ? "" : cabecera.getProveedor().getEmpresa().toString();
            Entidades entidad = ejbEntidades.traerUserId(cabecera.getUsuario());

            String elaborado = seguridadbean.getLogueado() == null ? "" : seguridadbean.getLogueado().toString();
            if (entidad != null) {
                elaborado = entidad.toString();
            }
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, revisado));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, elaborado));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, ""));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, "ENTREGA"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, "RECIBI CONFORME "));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, "RESPONSABLE UNIDAD DE BIENES"));
            pdf.agregarTabla(null, campos, 3, 100, null);
            reporte = pdf.traerRecurso();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public String elimina() {

        cabecera = (Cabecerainventario) cabeceras.getRowData();
        String vale = ejbCabeceras.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        // taer la foto
        Map parametros = new HashMap();

        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cabecera);
        try {
            listaKardex = ejbKardex.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        listaKardexb = new LinkedList<>();
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if ((cabecera.getObservaciones() == null) || (cabecera.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es Obligatorio es obligatorio");
            return true;
        }
        if (cabecera.getFecha() == null) {
            MensajesErrores.advertencia("Fecha es necesaria");
            return true;
        }
        
        String vale = ejbCabeceras.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return true;
        }
        
        if (cabecera.getBodega() == null) {
            MensajesErrores.advertencia("Bodega es necesaria");
            return true;
        }
        if (cabecera.getTxid() == null) {
            MensajesErrores.advertencia("Necesaria Transacción");
            return true;
        }

        if (cabecera.getTxid().getProveedor()) {
            if (proveedoresBean.getProveedor() == null) {
                MensajesErrores.advertencia("Necesario proveedor en esta transacción");
                return true;
            }
        }

        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return true;
        }

        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }

        try {
            // crear solicitud
            Txinventarios t = ejbTxInventario.find(cabecera.getTxid().getId());
            int numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
            t.setUltimo(numeroTx);
            ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
            cabecera.setEstado(0);
            cabecera.setFechadigitacion(new Date());
            if (cabecera.getTxid().getProveedor()) {
//                ************************************************ OJOJOJOJOJOJJO
                cabecera.setProveedor(proveedoresBean.getProveedor());
            } else {
                cabecera.setProveedor(null);
                cabecera.setContrato(null);
            }
            // buscar si ya esta
            cabecera.setNumero(numeroTx);
            cabecera.setUsuario(seguridadbean.getLogueado().toString());
            // crear la solicitud

            ejbCabecerainventario.create(cabecera, getSeguridadbean().getLogueado().getUserid());
            for (Kardexinventario k : listaKardex) {
                // ver descuento global
                if (descuentoExterno > 0) {
                    // aplca a cada linea
                    double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                    k.setCosto(new Float(valor));
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.equivalencia='UNIDAD'");
                List<Unidades> listaU = ejbuUnidades.encontarParametros(parametros);
                Unidades u = listaU.get(0);
                k.setSigno(1);
                k.setTipoorden(1);
                k.setCabecerainventario(cabecera);
                k.setBodega(cabecera.getBodega());
                k.setFecha(cabecera.getFecha());
                k.setNumero(cabecera.getNumero());
                k.setUnidad(u);
                if (k.getSuministro().getLote() == null) {
                    k.getSuministro().setLote(false);
                }
                if (k.getSuministro().getLote()) {
                    // buscar y armar el lote
                    parametros = new HashMap();
                    parametros.put(";where", "o.lote=:lote and o.fechaCaduca=:fechaCaduca");
                    parametros.put("lote", k.getNombreLote());
                    parametros.put("fechaCaduca", k.getFechaCaduca());
                    List<Lotessuministros> lotes = ejbLotes.encontarParametros(parametros);
                    if (lotes.isEmpty()) {
                        Lotessuministros l = new Lotessuministros();
                        l.setCantidad(new BigDecimal(k.getCantidad()));
                        l.setLote(k.getNombreLote());
                        l.setSuministro(k.getSuministro());
                        l.setFechaCaduca(k.getFechaCaduca());
                        ejbLotes.create(l, getSeguridadbean().getLogueado().getUserid());
                        k.setLote(l);
                    } else {
                        k.setLote(lotes.get(0));
                    }
                }
                ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());
            }
            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        buscar();
        return null;
    }

    public String grabar() {
        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return null;
        }
        try {

            // actualizar kardex i borrar los borrados
            for (Kardexinventario k : listaKardex) {
                k.setCabecerainventario(cabecera);
                k.setTipoorden(1);
                if (cabecera.getCabecera() != null) {
                    if (estadoIngreso == 3) {
                        k.setBodega(cabecera.getBodega());
                    }
                } else {
                    k.setBodega(cabecera.getBodega());
                }
                k.setFecha(cabecera.getFecha());
                k.setNumero(cabecera.getNumero());
                int signo = 1;
                // ver descuento global
                if (descuentoExterno > 0) {
                    // aplca a cada linea
                    double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                    k.setCosto(new Float(valor));
                }
                signo = 1;
                if (k.getSuministro().getLote() == null) {
                    k.getSuministro().setLote(false);
                }
                if (k.getId() == null) {
                    k.setSigno(signo);

                    if (k.getSuministro().getLote()) {
                        if (k.getLote() == null) {
                            // buscar y armar el lote
                            Map parametros = new HashMap();
                            parametros.put(";where", "o.lote=:lote and o.fechaCaduca=:fechaCaduca");
                            parametros.put("lote", k.getNombreLote());
                            parametros.put("fechaCaduca", k.getFechaCaduca());
                            List<Lotessuministros> lotes = ejbLotes.encontarParametros(parametros);
                            if (lotes.isEmpty()) {
                                Lotessuministros l = new Lotessuministros();
                                l.setCantidad(new BigDecimal(k.getCantidad()));
                                l.setLote(k.getNombreLote());
                                l.setSuministro(k.getSuministro());
                                l.setFechaCaduca(k.getFechaCaduca());
                                ejbLotes.create(l, getSeguridadbean().getLogueado().getUserid());
                                k.setLote(l);
                            } else {
                                k.setLote(lotes.get(0));
                            }
                        }
                    }
                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());

                } else {
                    k.setSigno(signo);

                    ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());
                    // mas dificil ya que hay que ver en cardex i es transferencia

                } // fin else si existe 
            }// fin for kardex
            for (Kardexinventario k : listaKardexb) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia
//                    if (transferencia) {
//                        Map parametros = new HashMap();
//                        parametros.put(";where", "o.cabecera=:cabecera and o.suministro=:suministro");
//                        parametros.put("cabecera", k.getCabecerainventario());
//                        parametros.put("suministro", k.getSuministro());
//                        List<Kardexinventario> ListaCambiar = ejbKardex.encontarParametros(parametros);
//                        for (Kardexinventario k1 : ListaCambiar) {
//                            ejbKardex.remove(k1, getSeguridadbean().getLogueado().getUserid());
//                        }
//                    }// fin if transferencia
                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados
            if (estadoIngreso == 3) {
                String retorno = ejbCabecerainventario.grabarDefinitivo(cabecera, listaKardex);
                if (retorno != null) {
                    MensajesErrores.fatal(retorno);
                    return null;
                }
            }
            // grabar el tracking
            if (estadoIngreso > 0) {
                Bitacorasuministro bi = new Bitacorasuministro();
                bi.setUsuario(seguridadbean.getLogueado().getEmpleados());
                bi.setEstado(estadoIngreso);
                bi.setFecha(new Date());
                bi.setCabecera(cabecera);
                ejbBitacora.create(bi, seguridadbean.getLogueado().getUserid());
            }
            cabecera.setEstado(estadoIngreso);
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
        } catch (GrabarException | InsertarException | BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        buscar();
        return null;
    }

    public String grabarAnular() {
        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return null;
        }
        try {

            // actualizar kardex i borrar los borrados
            for (Kardexinventario k : listaKardex) {
                // borrar
                ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
            }// fin for kardex
            for (Kardexinventario k : listaKardexb) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia

                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados

            // grabar el tracking
//            if (estadoIngreso > 0) {
            Bitacorasuministro bi = new Bitacorasuministro();
            bi.setUsuario(seguridadbean.getLogueado().getEmpleados());
            bi.setEstado(-1);
            bi.setFecha(new Date());
            bi.setCabecera(cabecera);
            ejbBitacora.create(bi, seguridadbean.getLogueado().getUserid());
//            }
            cabecera.setEstado(-1);

            Cabecerainventario cabEgreso = cabecera.getCabecera();
            cabEgreso.setEstado(-1);
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.edit(cabEgreso, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
        } catch (GrabarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        buscar();
        return null;
    }

    public String borrar() {

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabecera);
            List<Bitacorasuministro> listab = ejbBitacora.encontarParametros(parametros);
//            cabecera.setEstado(-1);
//            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            for (Kardexinventario k : listaKardex) {
                ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
            }
            for (Bitacorasuministro bk : listab) {
                ejbBitacora.remove(bk, getSeguridadbean().getLogueado().getUserid());
            }
            ejbCabecerainventario.remove(cabecera, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
        } catch (BorrarException ex) {
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
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

    // ver si le dejo cambiar si no ha entregado el producto
    public String nuevoKardex() {
        if (cabecera.getTxid() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de movimiento antes de ingresar productos");
            return null;
        }
        if (cabecera.getBodega() == null) {
            MensajesErrores.advertencia("Seleccione una bodega antes de ingresar productos");
            return null;
        }
        if (cabecera.getTxid().getIngreso()) {
            if (transferencia) {
                MensajesErrores.advertencia("No se pueden realizar transferencias de transacciones de ingreso");
                return null;
            }
        }
        descuentoInterno = 0;
        kardex = new Kardexinventario();
        kardex.setCantidad(new Float(0));
        kardex.setCantidadinversion(new Float(0));
        formularioRenglones.insertar();
        return null;
    }

    public String modificaKardex(Kardexinventario kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());
        descuentoInterno = 0;
        cantidadModificada = kardex.getCantidad();
        formularioRenglones.editar();
        return null;
    }

    public String borraKardex(Kardexinventario kardex) {
        this.kardex = kardex;
        formularioRenglones.setIndiceFila(formularioRenglones.getFila().getRowIndex());
        formularioRenglones.eliminar();
        return null;
    }

    private boolean validarRenglon() {
        if ((kardex.getCodigoSuministro() == null) || (kardex.getCodigoSuministro().isEmpty())) {
            MensajesErrores.advertencia("Selecccione un suministro");
            return true;
        }
        Suministros sum = suminitrosBean.traerCodigo(kardex.getCodigoSuministro());
        if ((kardex.getCantidad() == null) || (kardex.getCantidad() <= 0)) {
            if ((kardex.getCantidadinversion() == null) || (kardex.getCantidadinversion() <= 0)) {
                MensajesErrores.advertencia("Ingrese una cantidad válida");
                return true;
            }
        }
        if ((kardex.getSuministro() == null)) {
            MensajesErrores.advertencia("Ingrese un Suministro");
            return true;
        }
        kardex.setSuministro(sum);
        if (sum.getLote() == null) {
            sum.setLote(false);
        }
        if (sum.getLote()) {
            if ((kardex.getNombreLote() == null) || (kardex.getNombreLote().isEmpty())) {
                MensajesErrores.advertencia("Ingrese un número de lote");
                return true;
            }
            if (kardex.getFechaCaduca() == null) {
                MensajesErrores.advertencia("Ingrese una fecha de caducidad del lote");
                return true;
            }
            // buscar el lote en la base
        }

        if ((kardex.getUnidad() == null)) {
            MensajesErrores.advertencia("Ingrese una udidad del Suministro");
            return true;
        }

        if ((kardex.getCosto() == null) || (kardex.getCosto() <= 0)) {
            MensajesErrores.advertencia("Ingrese una costo para ingreso");
            return true;
        }
        if (descuentoInterno > 0) {
            double valor = kardex.getCosto().doubleValue() * (1 - descuentoInterno / 100);
            kardex.setCosto(new Float(valor));
        }

        // 
        return false;
    }

    private void estaSuministro(Kardexinventario k) {
        for (Kardexinventario k1 : listaKardex) {
            if (k1.getSuministro().equals(k.getSuministro())) {
                // suma la cantidad conserva el costo inicial o pone el promedio
                k1.setCantidad(k1.getCantidad() + k.getCantidad());
                return;
            }
        }
        listaKardex.add(k);
    }

    public String insertarRenglon() {
        if (validarRenglon()) {
            return null;
        }
        estaSuministro(kardex);
        formularioRenglones.cancelar();
        return null;
    }

    public String grabarRenglon() {
        if (validarRenglon()) {
            return null;
        }
        listaKardex.set(formularioRenglones.getIndiceFila(), kardex);
        formularioRenglones.cancelar();
        return null;
    }

    public String borrarRenglon() {

        listaKardex.remove(formularioRenglones.getIndiceFila());
        formularioRenglones.cancelar();
        return null;
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

    public SelectItem[] getUnidadesSuministro() {
        if (kardex == null) {
            return null;
        }
        if (kardex.getSuministro() == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();

            Map parametros = new HashMap();
//            parametros.put(";orden", "o.base=:base");
            parametros.put(";where", "o.base=:base");
            parametros.put("base", kardex.getSuministro().getUnidad());
            List<Unidades> ls = ejbUnidades.encontarParametros(parametros);
            List<Unidades> retorno = new LinkedList<>();
            if (kardex.getSuministro().getUnidad() == null) {
                parametros = new HashMap();
//            parametros.put(";orden", "o.base=:base");
                ls = ejbUnidades.encontarParametros(parametros);
                for (Unidades u : ls) {
                    retorno.add(u);
                }
            } else {
                retorno.add(kardex.getSuministro().getUnidad());
                for (Unidades u : ls) {
                    retorno.add(u);
                }
            }
            return Combos.getSelectItems(retorno, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    public boolean getMostrarObligacion() {
        Cabecerainventario cab = (Cabecerainventario) cabeceras.getRowData();
        if (cab.getCabecera() != null) {
            return false;
        }
        if (cab.getEstado() <= 1) {
            if (cab.getTxid().getIngreso()) {
                if (cab.getTxid().getProveedor()) {
                    if (cab.getOrdencompra() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean getMostrarModificar() {
        Cabecerainventario cab = (Cabecerainventario) cabeceras.getRowData();

        if (cab.getEstado() < 1) {
            if (cab.getTxid().getIngreso()) {
                if (cab.getTxid().getProveedor()) {
                    if (cab.getOrdencompra() == null) {
                        return true;
                    }
                }
            } else {
                if (cab.getSolicitud() == null) {
                    return true;
                }
//                poner estado que pueda ver

            }

        }
        return false;
    }

    public String traerObligaciones() {
        cabecera = (Cabecerainventario) cabeceras.getRowData();
//        proveedoresBean.setEmpresa(cabecera.getProveedor().getEmpresa());
//        proveedoresBean.setProveedor(cabecera.getProveedor());
        getObligacionesBean().setEstado(2);
        getObligacionesBean().setCompromiso(cabecera.getOrdencompra().getCompromiso());
        obligacionesBean.buscar();
        formularioObligacion.insertar();
        return null;
    }

    public String seleccionarObligaciones(Obligaciones o) {
        cabecera.setObligacion(o);
        try {
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        proveedoresBean.setEmpresa(null);
        proveedoresBean.setProveedor(null);
        cabecera = null;
        formularioObligacion.cancelar();
        buscar();
        return null;
    }

    public String salirObligaciones() {

        proveedoresBean.setEmpresa(null);
        proveedoresBean.setProveedor(null);
        cabecera = null;
        formularioObligacion.cancelar();
        buscar();
        return null;
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

    public double getCantidadTotal() {
        if (listaKardex == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexinventario k : listaKardex) {
            retorno += k.getCantidad();
        }
        return retorno;
    }

    public double getCostoTotal() {
        if (listaKardex == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexinventario k : listaKardex) {
            if (k.getCosto() == null) {
                retorno += k.getCosto();
            }
        }
        return retorno;
    }

    public double getTotal() {
        if (listaKardex == null) {
            return 0;
        }
        double retorno = 0;
        for (Kardexinventario k : listaKardex) {
            double cantidad = (k.getCantidad() == null ? 0 : k.getCantidad());
            double cantidadinversion = (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
            if (k.getCosto() == null) {
                retorno += (cantidad + cantidadinversion);
            } else {
                retorno += (cantidad + cantidadinversion) * k.getCosto();
            }
        }
        return retorno;
    }

    public double getSaldo() {
        double retorno = 0;
        try {
            if (kardex == null) {
                return 0;
            }
            if (kardex.getSuministro() == null) {
                return 0;
            }
            if (cabecera.getTxid() == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
//            if (cabecera.getTxid().getIngreso()) {
//                return 0;
//            }

            // ir a la bodega a ver el saldo
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            kardex.setCostopromedio(bs.getCostopromedio());
            parametros = new HashMap();
            if (cabecera.getId() == null) {
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro ");
//                        + " and o.cabecerainventario.estado=0 "
//                        + " and o.cabecerainventario.txid.ingreso=false");
//                        + " and o.cabecerainventario.txid.ingreso=false");
            } else {
//                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 "
//                        + "and o.cabecerainventario!=:cabecera and o.cabecerainventario.txid.ingreso=false");
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro and o.cabecerainventario!=:cabecera");
                parametros.put("cabecera", cabecera);
            }
//            ojo ver los ingresos y egresos ***************************************+
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            parametros.put(";campo", "o.cantidad*o.signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            retorno = valorOtros;
//            retorno = bs.getSaldo() - valorOtros;
//        for (Kardexinventario k : listaKardex) {
//                retorno += k.getCantidad();
//        }

        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getSaldoInversion() {
        double retorno = 0;
        try {
            if (kardex == null) {
                return 0;
            }
            if (kardex.getSuministro() == null) {
                return 0;
            }
            if (cabecera.getTxid() == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
            if (cabecera.getTxid().getIngreso()) {
                return 0;
            }

            // ir a la bodega a ver el saldo
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            parametros = new HashMap();
//            if (cabecera.getId() == null) {
//                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0");
//            } else {
//                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 and o.cabecerainventario!=:cabecera");
//                parametros.put("cabecera", cabecera.getId());
//            }
            if (cabecera.getId() == null) {
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro ");
//                        + " and o.cabecerainventario.estado=0 "
//                        + " and o.cabecerainventario.txid.ingreso=false");
//                        + " and o.cabecerainventario.txid.ingreso=false");
            } else {
//                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 "
//                        + "and o.cabecerainventario!=:cabecera and o.cabecerainventario.txid.ingreso=false");
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro and o.cabecerainventario!=:cabecera");
                parametros.put("cabecera", cabecera);
            }
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            parametros.put(";campo", "o.cantidadinversion*signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            kardex.setCostopinversion(bs.getCostopromedioinversion());
            retorno = valorOtros;
//        for (Kardexinventario k : listaKardex) {
//                retorno += k.getCantidad();
//        }

        } catch (ConsultarException ex) {
            Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String grabarDefinitivo() {

        String retorno = ejbCabecerainventario.grabarDefinitivo(cabecera, listaKardex);
        if (retorno != null) {
            MensajesErrores.fatal(retorno);
            return null;
        }
        formulario.cancelar();
        formularioImprimir.insertar();

        buscar();
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

    /**
     * @return the estadoIngreso
     */
    public int getEstadoIngreso() {
        return estadoIngreso;
    }

    /**
     * @param estadoIngreso the estadoIngreso to set
     */
    public void setEstadoIngreso(int estadoIngreso) {
        this.estadoIngreso = estadoIngreso;
    }

    public SelectItem[] getComboEstados() {
        int size = 6;
        SelectItem[] items = new SelectItem[size];
        int i = 0;

        items[0] = new SelectItem(null, "--- Seleccione uno ---");
        items[++i] = new SelectItem(0, "INGRESO");
        items[++i] = new SelectItem(1, "RECEPCION");
        items[++i] = new SelectItem(2, "REVISION");
        items[++i] = new SelectItem(3, "APROBACION");
        items[++i] = new SelectItem(-3, "TRANSFERENCIA");
        return items;
    }

    public String traerEstado(int estado) {
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
        return "";
    }

    public Permisosbodegas traerPerfil(Bodegas b) {
        Map parametros = new HashMap();
        try {
            parametros.put(";where", "o.usuario=:usuario and o.bodega=:bodega");
            parametros.put("bodega", b);
            parametros.put("usuario", seguridadbean.getLogueado().getEmpleados());
            List<Permisosbodegas> permisos = ejbPermisos.encontarParametros(parametros);
            for (Permisosbodegas pb : permisos) {
                return pb;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(BodegasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // poner si puede usar los botones
    public boolean isPuedeNuevo() {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getIngreso();
    }

    public boolean isPuedeModifica(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getIngreso() && item.getEstado() == 0;
    }

    public boolean isPuedeTransferencia(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getIngreso() && item.getEstado() == -3;
    }

    public boolean isPuedeRecepcion(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getRecepcion() && item.getEstado() == 0;
    }

    public boolean isPuedeRevision(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getRevision() && item.getEstado() == 1;
    }

    public boolean isPuedeAprobacion(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getAprobacion() && item.getEstado() == 2;
    }

    /**
     * @return the separador
     */
    public String getSeparador() {
        return separador;
    }

    /**
     * @param separador the separador to set
     */
    public void setSeparador(String separador) {
        this.separador = separador;
    }

    private void ubicar(String titulo, String valor, Kardexinventario k) {
        if (titulo.contains("codigo")) {
            k.setCodigoSuministro(valor);

        } else if (titulo.equals("cantidad")) {
            valor = valor.replace(",", ".");
            k.setCantidad(new Float(valor));
        } else if (titulo.equals("cantidadinv")) {
            valor = valor.replace(",", ".");
            k.setCantidadinversion(new Float(valor));

        } else if (titulo.contains("costo")) {
            valor = valor.replace(",", ".");
            k.setCosto(new Float(valor));

        } else if (titulo.contains("lote")) {
            k.setNombreLote(valor);
        } else if (titulo.contains("caduca")) {
            Date fechaCaduca = new Date(valor);
            k.setFechaCaduca(fechaCaduca);
        }
    }

    // sube desde un archivo
    public void archivoListener(FileEntryEvent e) throws IOException {
        if (cabecera.getTxid() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de transacción");
            return;
        }
        listaErrores = new LinkedList<>();
        listaKardex = new LinkedList<>();
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        List<Kardexinventario> renLista = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    try {
                        parent = file.getParentFile();

                        BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                        String sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo

                        while ((sb = entrada.readLine()) != null) {
                            Kardexinventario k = new Kardexinventario();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(cabecera[j].toLowerCase(), aux[j].toUpperCase(), k);
                                }
                            }
                            Suministros sum = suminitrosBean.traerCodigo(k.getCodigoSuministro());
                            if (sum == null) {
                                AuxiliarKardex auxK = new AuxiliarKardex();
                                auxK.setTransaccion(k.getCodigoSuministro());
                                auxK.setObservaciones("No existe suministro con código");
                                listaErrores.add(auxK);
//                                MensajesErrores.advertencia("No existe suministro con código" + k.getCodigoSuministro());
//                                return;
                            }
                            if (sum != null) {
                                if (sum.getLote() == null) {
                                    sum.setLote(false);

                                }
                                if (sum.getLote()) {
                                    if ((k.getNombreLote() == null) || (k.getNombreLote().isEmpty())) {
                                        AuxiliarKardex auxK = new AuxiliarKardex();
                                        auxK.setTransaccion(k.getCodigoSuministro());
                                        auxK.setObservaciones("Necesario número de lote :");
                                        listaErrores.add(auxK);
//                                    MensajesErrores.advertencia("Necesario número de lote : " + k.getCodigoSuministro());
//                                    return;
                                    }
                                    if (k.getFechaCaduca() == null) {
                                        AuxiliarKardex auxK = new AuxiliarKardex();
                                        auxK.setTransaccion(k.getCodigoSuministro());
                                        auxK.setObservaciones("Necesario fecha de caducidad  del lote : ");
                                        listaErrores.add(auxK);
//                                    MensajesErrores.advertencia("Necesario fecha de caducidad  del lote : " + k.getCodigoSuministro());
//                                    return;
                                    }
                                }
                                k.setSuministro(sum);

                                float cantidad = k.getCantidad() == null ? 0 : k.getCantidad();
                                float cantidadi = k.getCantidadinversion() == null ? 0 : k.getCantidadinversion();
                                if (cantidad + cantidadi <= 0) {
                                    AuxiliarKardex auxK = new AuxiliarKardex();
                                    auxK.setTransaccion(k.getCodigoSuministro());
                                    auxK.setObservaciones("Necesaria cantidad ");
                                    listaErrores.add(auxK);
//                                MensajesErrores.advertencia("Necesaria cantidad para " + k.getCodigoSuministro());
//                                return;
                                }
                                renLista.add(k);
//                            renglones.add(r);
                                listaKardex.add(k);
                            }
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.advertencia(ex.getMessage());
                        Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(
                                    FacesContext.getCurrentInstance(),
                                    fe, i).getSummary());
                }
            }

        }
        if (listaErrores.size() > 0) {
            formularioErrores.insertar();
        } else {
            listaKardex = renLista;
        }
    }

    // fin
    public void cambiaCodigo(ValueChangeEvent event) {
        if (kardex != null) {
            String newWord = (String) event.getNewValue();
            kardex.setSuministro(suminitrosBean.traerCodigo(newWord));
        }
    }

    public double traerTotal(Cabecerainventario cab) {
        double total = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cab);
        try {
            List<Kardexinventario> lista = ejbKardex.encontarParametros(parametros);
            for (Kardexinventario k : lista) {
                double totalLinea = k.getCosto().doubleValue()
                        * ((k.getCantidad() == null ? 0 : k.getCantidad().doubleValue())
                        + (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));
                total += totalLinea;

            }
            return total;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the formularioErrores
     */
    public Formulario getFormularioErrores() {
        return formularioErrores;
    }

    /**
     * @param formularioErrores the formularioErrores to set
     */
    public void setFormularioErrores(Formulario formularioErrores) {
        this.formularioErrores = formularioErrores;
    }
}
