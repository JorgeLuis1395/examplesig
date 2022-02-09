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
import java.math.RoundingMode;
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
import org.beans.sfccbdmq.AdicionalkardexFacade;
import org.beans.sfccbdmq.BitacorasuministroFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.LotessuministrosFacade;
import org.beans.sfccbdmq.PermisosbodegasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.SolicitudsuministrosFacade;
import org.beans.sfccbdmq.TipoasientoFacade;
import org.beans.sfccbdmq.TxinventariosFacade;
import org.beans.sfccbdmq.UnidadesFacade;
import org.compras.sfccbdmq.ObligacionesCompromisoBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.BodegasBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Bitacorasuministro;
import org.entidades.sfccbdmq.Bodegas;
import org.entidades.sfccbdmq.Bodegasuministro;
import org.entidades.sfccbdmq.Cabecerainventario;
import org.entidades.sfccbdmq.Cabeceras;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Lotessuministros;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Permisosbodegas;
import org.entidades.sfccbdmq.Renglones;
import org.entidades.sfccbdmq.Solicitudsuministros;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tipoasiento;
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
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "egresosSuministrosSfccbdmq")
@ViewScoped
public class EgresosSuministrosBean {

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
     * Creates a new instance of CabeceraBean
     */
    public EgresosSuministrosBean() {
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
    private AdicionalkardexFacade ejbAdKardex;
    @EJB
    private KardexinventarioFacade ejbKardex;
    @EJB
    private UnidadesFacade ejbUnidades;
    @EJB
    private SolicitudsuministrosFacade ejbSolSum;
    @EJB
    private LotessuministrosFacade ejbLotes;
    @EJB
    private BitacorasuministroFacade ejbBitacora;
    @EJB
    private BodegasuministroFacade ejbBodSum;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private TipoasientoFacade ejbTipos;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private CabecerasFacade ejbCabecera;
    @EJB
    private CabecerasFacade ejbCabeceras;
    private Cabecerainventario cabecera;
    private Cabecerainventario cabeceraTransferencia;
    private Kardexinventario kardex;
    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioObligacion = new Formulario();
    private Formulario formularioErrores = new Formulario();
    private List<AuxiliarKardex> listaErrores;
    private int estadoIngreso;
    private String separador = ";";
    private List<Renglones> renglones;
    private List<Renglones> renglonesReclasificacion;
    // autocompletar paar que ponga el custodio
    //
    private LazyDataModel<Cabecerainventario> cabeceras;
    private List<Kardexinventario> listaKardex;
    private List<Kardexinventario> listaKardexb;
    private Integer factura;
    private Integer numero;
    private double descuentoInterno;
    private double descuentoExterno;
    private double cantidadModificada;
    private Integer estado = -10;
    private String observaciones;
    private Date desde;
    private Date hasta;
    private Txinventarios tipo;
    private Solicitudsuministros solicitud;
    private Bodegas bodega;
    private Bodegas bodegaDestino;
    private boolean transferencia;
    private Contratos contrato;
    private double sumaTotal;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{obligacionesCompromisoSfccbdmq}")
    private ObligacionesCompromisoBean obligacionesBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suministroBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    private Perfiles perfil;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suminitrosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
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
        String nombreForma = "EgresosSuministrosVista";
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
        sumaTotal = 0;
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
                String where = "  o.fecha between :desde and :hasta and o.txid.ingreso=false";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();
                    String pal = "palabra";

                    where += " and upper(o." + clave + ") like :" + pal;
                    parametros.put(pal, valor.toUpperCase() + "%");
                }
                // Numero de factura
                if (!((factura == null) || (factura <= 0))) {
                    where += " and o.factura =:documento";
                    parametros.put("documento", factura);
                }
                if ((estado != null)) {
                    where += " and o.estado=:estado";
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
                if (entidadesBean.getEntidad() != null) {
                    where += " and o.solicitante=:solicitante";
                    parametros.put("solicitante", entidadesBean.getEntidad().getEmpleados());
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
        cabeceraTransferencia = new Cabecerainventario();
        formulario.insertar();
        cabecera.setEstado(0); // digitada
        cabeceraTransferencia.setEstado(-3); // en espera
        listaKardex = new LinkedList<>();
        cabecera.setBodega(bodega);
        listaKardexb = new LinkedList<>();
        proveedoresBean.setRuc(null);
        proveedoresBean.setProveedor(null);
        descuentoExterno = 0;
        solicitud = new Solicitudsuministros();
        entidadesBean.setApellidos(null);
        entidadesBean.setEntidad(null);
        return null;
    }

    public String salir() {
        cabecera = null;
        cabeceraTransferencia = null;
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
        if (cabecera.getSolicitante() == null) {
            entidadesBean.setApellidos(null);
            entidadesBean.setEntidad(null);
        } else {
            entidadesBean.setApellidos(cabecera.getSolicitante().getEntidad().getApellidos());
            entidadesBean.setEntidad(cabecera.getSolicitante().getEntidad());
        }
        cabeceraTransferencia = new Cabecerainventario();
        if (cabecera.getTxid().getTransaferencia() != null) {
            cabeceraTransferencia = cabecera.getCabecera();
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
            if (estadoIngreso == 1) {
                armaRenglones();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        listaKardexb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String imprimir() {
        this.cabecera = (Cabecerainventario) cabeceras.getRowData();
        cabeceraTransferencia = new Cabecerainventario();
        if (cabecera.getTxid().getTransaferencia() != null) {
            cabeceraTransferencia = cabecera.getCabecera();
        }
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

//            for (Kardexinventario k : listaKardex) {
//                
//            }
            listaKardex = ejbKardex.encontarParametros(parametros);
//            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
            DocumentoPDF pdf;
            DecimalFormat formatoMes = new DecimalFormat("00");
            DecimalFormat formatoAnio = new DecimalFormat("0000");
            List<AuxiliarReporte> titulos = new LinkedList<>();

            String cabeceraStr = configuracionBean.getConfiguracion().getNombre();
            cabeceraStr += "\n" + "EGRESO DE SUMINISTROS";

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

            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Solicitante : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getSolicitante() == null ? "" : cabecera.getSolicitante().getEntidad().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Dirección : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getDireccion() == null ? "" : cabecera.getDireccion().toString()));

//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "No Compromiso : "));
//            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getCompromiso() == null ? "" : cabecera.getCompromiso().getNumerocomp().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Factura : "));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getFactura() == null ? "" : cabecera.getFactura().toString()));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, ""));
            columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            if (cabecera.getCabecera() != null) {
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "B. transf. : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getCabecera().getBodega().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "No. Ingr. transf"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getCabecera().getNumero().toString()));
            }
            if (cabecera.getProveedor() != null) {
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Proveedor : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, cabecera.getProveedor().getEmpresa().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, ""));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
            }
            pdf.agregarTabla(null, columnas, 4, 100, null);
            ////FIN DE CABECERA
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("Observaciones : " + cabecera.getObservaciones() + "\n\n");

            int totalCol = 9;
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "No."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "CODIGO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "LOTE"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "SUMINISTRO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CANTIDAD"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CANTIDAD INVERSION"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "COSTO PROMEDIO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "COSTO PROMEDIO INVERSION"));
//            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, true, "IMPUESTO"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "TOTAL"));
//            totalCol++;
            int i = 0;
            double valorTotal = 0;
            double valorDescuento = 0;
            List<AuxiliarReporte> campos = new LinkedList<>();
            for (Kardexinventario k : listaKardex) {
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, String.valueOf(++i)));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getSuministro().getCodigobarras()));
                campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getLote() == null ? "" : k.getLote().getLote()));
                campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getSuministro().getNombre()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCantidad() == null ? 0 : k.getCantidad().doubleValue()));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));

                double cuadrec = Math.round((k.getCostopromedio() == null ? 0 : k.getCostopromedio().doubleValue()) * 1000);
                String c = cuadrec + "";
                if (c.length() > 5) {
                    String c2 = c.substring(4, 5);
                    if (c2.equals("5")) {
                        cuadrec = cuadrec + 1;
                    }
                }
                double divididoc = cuadrec / 1000;
                BigDecimal valortotalc = new BigDecimal(divididoc).setScale(2, RoundingMode.HALF_UP);
                double costop = (valortotalc.doubleValue());

                double cuadrei = Math.round((k.getCostopinversion() == null ? 0 : k.getCostopinversion().doubleValue()) * 1000);
                String ci = cuadrec + "";
                if (ci.length() > 5) {
                    String ci2 = ci.substring(4, 5);
                    if (ci2.equals("5")) {
                        cuadrei = cuadrei + 1;
                    }
                }
                double divididoi = cuadrei / 1000;
                BigDecimal valortotali = new BigDecimal(divididoi).setScale(2, RoundingMode.HALF_UP);
                double costoi = (valortotali.doubleValue());

                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, costop));
                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, costoi));

                double totalLinea = (k.getCostopromedio() == null ? 0 : k.getCostopromedio().doubleValue())
                        * (k.getCantidad() == null ? 0 : k.getCantidad().doubleValue());

                double totalLineai = ((k.getCostopinversion() == null ? 0 : k.getCostopinversion().doubleValue())
                        * (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));

                double cuadre = Math.round((totalLinea + totalLineai) * 1000);
                String li = cuadre + "";
                if (li.length() > 5) {
                    String li2 = li.substring(4, 5);
                    if (li2.equals("5")) {
                        cuadre = cuadre + 1;
                    }
                }
                double dividido = cuadre / 1000;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double saldo = (valortotal.doubleValue());

                campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, saldo));
                valorTotal += saldo;

            }
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, "TOTAL"));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, ""));
//            campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 4, false, ""));
            campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, valorTotal));
            pdf.agregarTablaReporte(titulos, campos, totalCol, 100, "RENGLONES");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            String solicitante = cabecera.getSolicitante() == null ? "" : cabecera.getSolicitante().getEntidad().toString();
            String elaborado = seguridadbean.getLogueado() == null ? "" : seguridadbean.getLogueado().toString();
            Entidades entidad = ejbEntidades.traerUserId(cabecera.getUsuario());
            if (entidad != null) {
                elaborado = entidad.toString();
            }
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 4, true, ""));
            campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, false, "______________________________"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, "ENTREGA"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, "RECIBI CONFORME"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, "RESPONSABLE DE LA UNIDAD DE BIENES"));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, elaborado));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, solicitante));
            campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_CENTER, 8, true, ""));
            pdf.agregarTabla(null, campos, 3, 100, null);
            reporte = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormularioImprimir().editar();
        return null;
    }

    public String elimina() {

        cabecera = (Cabecerainventario) cabeceras.getRowData();
        cabeceraTransferencia = new Cabecerainventario();
        String vale = ejbCabeceras.validarCierre(cabecera.getFecha());
        if (vale != null) {
            MensajesErrores.advertencia(vale);
            return null;
        }
        if (cabecera.getTxid().getTransaferencia() != null) {
            cabeceraTransferencia = cabecera.getCabecera();
        }
        if (cabecera.getCabecera() != null) {
            transferencia = true;
        }
        if (cabecera.getSolicitante() == null) {
            entidadesBean.setApellidos(null);
            entidadesBean.setEntidad(null);
        } else {
            entidadesBean.setApellidos(cabecera.getSolicitante().getEntidad().getApellidos());
            entidadesBean.setEntidad(cabecera.getSolicitante().getEntidad());
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
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
        if (entidadesBean.getEntidad() == null) {
            MensajesErrores.advertencia("Necesario Beneficiario");
            return true;
        }
        if (entidadesBean.getEntidad().getEmpleados().getCargoactual() == null) {
            MensajesErrores.advertencia("Necesario que empleado tenga cargo");
            return true;
        }
        if (cabecera.getTxid().getTransaferencia() != null) {
            if (cabeceraTransferencia.getBodega() == null) {
                MensajesErrores.advertencia("Necesario bodega de transferencia");
                return true;
            }
            if (cabeceraTransferencia.getBodega().equals(cabecera.getBodega())) {
                MensajesErrores.advertencia("No se puede hacer una transferencia a la misma bodega");
                return true;
            }
        }

//        if (cabecera.getTxid().getProveedor()) {
//            if (proveedoresBean.getProveedor() == null) {
//                MensajesErrores.advertencia("Necesario proveedor en esta transacción");
//                return true;
//            }
//        }
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
            Empleados empleado = entidadesBean.getEntidad().getEmpleados();
            cabecera.setEstado(0);
            cabecera.setFechadigitacion(new Date());
            cabecera.setSolicitante(empleado);
            cabecera.setDireccion(empleado.getCargoactual().getOrganigrama());
            if (cabecera.getTxid().getProveedor()) {
//                ************************************************ OJOJOJOJOJOJJO
                cabecera.setProveedor(proveedoresBean.getProveedor());
            } else {
                cabecera.setProveedor(null);
                cabecera.setContrato(null);
            }

            // buscar si ya esta
//            boolean sale=true;
//            while (sale){
//                Map parametros=new HashMap();
//                parametros.put(";where", "o.numero=:numero and o.txid=:tipo");
//                parametros.put("tipo", cabecera.getTxid());
//                parametros.put("numero", numeroTx++);
//                int total=ejbCabecerainventario.contar(parametros);
//                if (total==0){
//                    numeroTx--;
//                    sale=false;
//                }
//            }
            cabecera.setNumero(numeroTx);
            // crear la solicitud
            ejbCabecerainventario.create(cabecera, getSeguridadbean().getLogueado().getUserid());
            for (Kardexinventario k : listaKardex) {
                // ver descuento global
                if (descuentoExterno > 0) {
                    // aplca a cada linea
                    double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                    k.setCosto(new Float(valor));
                }
                k.setSigno(-1);
                k.setTipoorden(2);
                k.setCabecerainventario(cabecera);
                k.setBodega(cabecera.getBodega());
                k.setFecha(cabecera.getFecha());
                k.setNumero(cabecera.getNumero());
                ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());
            }
            if (cabecera.getTxid().getTransaferencia() != null) {
                t = ejbTxInventario.find(cabecera.getTxid().getTransaferencia().getId());
                numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
                t.setUltimo(numeroTx);
                ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
                cabeceraTransferencia.setTxid(cabecera.getTxid().getTransaferencia());
                cabeceraTransferencia.setNumero(numeroTx);
                cabeceraTransferencia.setFecha(cabecera.getFecha());
                cabeceraTransferencia.setFechadigitacion(cabecera.getFechadigitacion());
                cabeceraTransferencia.setObservaciones(cabecera.getObservaciones());
                cabeceraTransferencia.setCabecera(cabecera);
                ejbCabecerainventario.create(cabeceraTransferencia, getSeguridadbean().getLogueado().getUserid());
                cabecera.setCabecera(cabeceraTransferencia);
                ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            }
            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Codigos codigoReclasificacion = getCodigosBean().traerCodigo("TIPREC", "INVER");
            if (codigoReclasificacion == null) {
                MensajesErrores.advertencia("No existe configuracion para tipo de asiento de reclasificacion");
                return null;
            }

            double valorC = 0;
            for (Renglones r : renglones) {
                valorC += r.getValor() == null ? 0 : r.getValor().doubleValue();
            }
            double cuadre = Math.round(valorC * 100);
            double dividido = cuadre / 100;
            BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            if (valorBase.doubleValue() != 0) {
                MensajesErrores.advertencia("Asiento descuadrado no es posible contabilizar");
                return null;
            }
            valorC = 0;
            for (Renglones r : renglonesReclasificacion) {
                valorC += r.getValor() == null ? 0 : r.getValor().doubleValue();
            }
            double cuadre2 = Math.round(valorC * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valorBase2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            if (valorBase2.doubleValue() != 0) {
                MensajesErrores.advertencia("Asiento descuadrado no es posible contabilizar");
                return null;
            }

            // actualizar kardex i borrar los borrados
            for (Kardexinventario k : listaKardex) {
                k.setCabecerainventario(cabecera);
                k.setBodega(cabecera.getBodega());
                k.setFecha(cabecera.getFecha());
                k.setHora(new Date());
                k.setNumero(cabecera.getNumero());
                int signo = -1;
                // ver descuento global
                if (descuentoExterno > 0) {
                    // aplca a cada linea
                    double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                    k.setCosto(new Float(valor));
                }
//                signo = 1;
                if (k.getId() == null) {
                    k.setSigno(-1);
                    k.setTipoorden(2);
                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());

                } else {
                    k.setTipoorden(2);
                    k.setSigno(-1);

                    ejbKardex.edit(k, getSeguridadbean().getLogueado().getUserid());
                    // mas dificil ya que hay que ver en cardex i es transferencia

                } // fin else si existe 

            }// fin for kardex
            for (Kardexinventario k : listaKardexb) {// borrar
                if (k.getId() != null) {
                    // se pueden boorrar pero primero la trasferencia
                    if (transferencia) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.cabecera=:cabecera and o.suministro=:suministro");
                        parametros.put("cabecera", k.getCabecerainventario());
                        parametros.put("suministro", k.getSuministro());
                        List<Kardexinventario> ListaCambiar = ejbKardex.encontarParametros(parametros);
                        for (Kardexinventario k1 : ListaCambiar) {
                            ejbKardex.remove(k1, getSeguridadbean().getLogueado().getUserid());
                        }
                    }// fin if transferencia
                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }

            }// alos borrados
            if (estadoIngreso == 1) {
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
            cabecera.setSolicitante(entidadesBean.getEntidad().getEmpleados());
            cabecera.setDireccion(entidadesBean.getEntidad().getEmpleados().getCargoactual().getOrganigrama());
            cabecera.setEstado(estadoIngreso);
            cabecera.setUsuario(seguridadbean.getLogueado().toString());
            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardex);
            // grabar el asiento
            if (cabecera.getTxid().getContabiliza()) {
                // asiento
                if (!((renglones == null) || (renglones.isEmpty()))) {
                    Tipoasiento ta = cabecera.getTxid().getTipoasiento();
                    if (ta == null) {
                        MensajesErrores.fatal("No existe tipo de asiento");
                        return null;
                    }
                    int numeroAsiento = ta.getUltimo();
                    numeroAsiento++;
                    ta.setUltimo(numeroAsiento);
                    ejbTipos.edit(ta, seguridadbean.getLogueado().getUserid());
                    Cabeceras c = new Cabeceras();
                    c.setDescripcion("EGR: " + cabecera.getNumero() + " " + cabecera.getObservaciones());
                    c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    c.setDia(cabecera.getFecha());
                    c.setTipo(ta);
                    c.setNumero(numeroAsiento);
                    c.setFecha(cabecera.getFecha());
                    c.setIdmodulo(cabecera.getId());
                    c.setUsuario(getSeguridadbean().getLogueado().getUserid());
                    c.setModulo(perfil.getMenu().getIdpadre().getModulo());
                    c.setOpcion("INVENTARIOS");
                    ejbCabecera.create(c, getSeguridadbean().getLogueado().getUserid());
                    for (Renglones r1 : getRenglones()) {
                        r1.setCabecera(c);
                        r1.setFecha(cabecera.getFecha());
                        r1.setReferencia("EGR: " + cabecera.getNumero() + " " + cabecera.getObservaciones());
                        ejbRenglones.create(r1, getSeguridadbean().getLogueado().getUserid());
                    }
                }
//                if (!((renglonesReclasificacion == null) || (renglonesReclasificacion.isEmpty()))) {
//                    Tipoasiento tipoAisentoReclasificacion = null;
//                    Map parametros = new HashMap();
//                    parametros.put(";where", "o.codigo=:codigo");
//                    parametros.put("codigo", codigoReclasificacion.getNombre());
//                    List<Tipoasiento> listaTipo = ejbTipos.encontarParametros(parametros);
//                    for (Tipoasiento t : listaTipo) {
//                        tipoAisentoReclasificacion = t;
//                    }
//                    int numeroAsiento = tipoAisentoReclasificacion.getUltimo();
//                    numeroAsiento++;
//                    tipoAisentoReclasificacion.setUltimo(numeroAsiento);
//                    ejbTipos.edit(tipoAisentoReclasificacion, seguridadbean.getLogueado().getUserid());
//                    Cabeceras c = new Cabeceras();
//                    c.setDescripcion(cabecera.getObservaciones());
//                    c.setDia(new Date());
//                    c.setFecha(cabecera.getFecha());
//                    c.setIdmodulo(cabecera.getId());
//                    c.setModulo(perfil.getMenu().getIdpadre().getModulo());
//                    c.setNumero(numeroAsiento);
//                    c.setOpcion("INVENTARIOS_RECLASIFICACION");
//                    c.setTipo(tipoAisentoReclasificacion);
//                    c.setUsuario(seguridadbean.getLogueado().getUserid());
//                    ejbCabecera.create(c, seguridadbean.getLogueado().getUserid());
//                    for (Renglones r : renglonesReclasificacion) {
//                        r.setCabecera(c);
//                        r.setFecha(cabecera.getFecha());
//                        r.setReferencia(cabecera.getObservaciones());
//                        ejbRenglones.create(r, seguridadbean.getLogueado().getUserid());
//                    }
//                }
                ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            }
            // fin asiento
//            ejbCabecerainventario.ejecutarSaldosSuministro(listaKardexb);
        } catch (GrabarException | InsertarException | BorrarException | ConsultarException ex) {
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
            Cabecerainventario cabTransferencia = cabecera.getCabecera();
            if (cabTransferencia != null) {
                if (cabTransferencia.getEstado() > 0) {
                    MensajesErrores.advertencia("Existe un ingreso por transferencia que se debe anular antes de borrar este egreso");
                    return null;
                }
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabecera);
            List<Bitacorasuministro> listab = ejbBitacora.encontarParametros(parametros);
//            cabecera.setEstado(-1);
//            ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
            List<Kardexinventario> listaParaBorrar = new LinkedList<>();

            for (Kardexinventario k : listaKardex) {
                Kardexinventario k1 = new Kardexinventario();
                k1.setBodega(cabecera.getBodega());

                k1.setSuministro(k.getSuministro());
                listaParaBorrar.add(k1);
                ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
            }
            for (Bitacorasuministro bk : listab) {
                ejbBitacora.remove(bk, getSeguridadbean().getLogueado().getUserid());
            }
            if (cabTransferencia != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.cabecerainventario=:cabecera");
                parametros.put("cabecera", cabTransferencia);
                List<Kardexinventario> listaKb = ejbKardex.encontarParametros(parametros);
                //
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera=:cabecera");
                parametros.put("cabecera", cabTransferencia);
                listab = ejbBitacora.encontarParametros(parametros);
                List<Kardexinventario> listaParaBorrar1 = new LinkedList<>();

                for (Kardexinventario k : listaKb) {
                    Kardexinventario k1 = new Kardexinventario();
                    k1.setBodega(cabecera.getBodega());

                    k1.setSuministro(k.getSuministro());
                    listaParaBorrar1.add(k1);
                    ejbKardex.remove(k, getSeguridadbean().getLogueado().getUserid());
                }
                for (Bitacorasuministro bk : listab) {
                    ejbBitacora.remove(bk, getSeguridadbean().getLogueado().getUserid());
                }
                cabTransferencia.setCabecera(null);
                ejbCabecerainventario.edit(cabeceraTransferencia, getSeguridadbean().getLogueado().getUserid());
                cabecera.setCabecera(null);
                ejbCabecerainventario.edit(cabecera, getSeguridadbean().getLogueado().getUserid());
                ejbCabecerainventario.remove(cabeceraTransferencia, getSeguridadbean().getLogueado().getUserid());
                ejbCabecerainventario.ejecutarSaldosSuministro(listaParaBorrar1);
            }
            parametros = new HashMap();
            parametros.put(";where", "o.idmodulo=:idmodulo and o.fecha=:fecha and o.opcion = 'INVENTARIOS'");
            parametros.put("idmodulo", cabecera.getId());
            parametros.put("fecha", cabecera.getFecha());
            List<Cabeceras> list = ejbCabecera.encontarParametros(parametros);
            for (Cabeceras c : list) {
                parametros = new HashMap();
                parametros.put(";where", "o.cabecera=:cabecera");
                parametros.put("cabecera", c);
                List<Renglones> listaRenglones = ejbRenglones.encontarParametros(parametros);
                for (Renglones r : listaRenglones) {
                    ejbRenglones.remove(r, seguridadbean.getLogueado().getUserid());
                }
                ejbCabecera.remove(c, seguridadbean.getLogueado().getUserid());
            }

            ejbCabecerainventario.remove(cabecera, getSeguridadbean().getLogueado().getUserid());
            ejbCabecerainventario.ejecutarSaldosSuministro(listaParaBorrar);
        } catch (BorrarException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
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
        Suministros sum = suministroBean.traerCodigo(kardex.getCodigoSuministro());
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
        if ((kardex.getUnidad() == null)) {
            MensajesErrores.advertencia("Ingrese una unidad del Suministro");
            return true;
        }

//        if ((kardex.getCosto() == null) || (kardex.getCosto() <= 0)) {
//            MensajesErrores.advertencia("Ingrese una costo para ingreso");
//            return true;
//        }
        if (descuentoInterno > 0) {
            double valor = kardex.getCosto().doubleValue() * (1 - descuentoInterno / 100);
            kardex.setCosto(new Float(valor));
        }
        double cantidad = kardex.getCantidad() == null ? 0 : kardex.getCantidad();
        double cantidadInversion = kardex.getCantidadinversion() == null ? 0 : kardex.getCantidadinversion();

        if (cantidad != 0) {
            cantidad = getSaldo() - cantidad;
            if (cantidad < 0) {
                MensajesErrores.advertencia("Cantidad sobrepasa el saldo de bodega -saldo: " + getSaldo() + " -cantidad: " + kardex.getCantidad() + " - " + kardex.getSuministro().getCodigobarras());
                return true;
            }
        }
        if (cantidadInversion != 0) {
            cantidadInversion = getSaldoInversion() - cantidadInversion;
            if (cantidadInversion < 0) {
                MensajesErrores.advertencia("Cantidad inversión sobrepasa el saldo de bodega " + kardex.getCodigoSuministro());
                return true;
            }
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
        listaKardexb.add(kardex);
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
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
//            double cantidad = (k.getCantidad() == null ? 0 : k.getCantidad());
//            double cantidadinversion = (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion());
//            if (k.getCosto() == null) {
//                retorno += (cantidad + cantidadinversion);
//            } else {
//                retorno += (cantidad + cantidadinversion) * k.getCosto();
//            }
            double totalLinea = k.getCostopromedio() == null ? 0 : k.getCostopromedio().doubleValue()
                    * (k.getCantidad() == null ? 0 : k.getCantidad().doubleValue());
            double totalLineai = ((k.getCostopinversion() == null ? 0 : k.getCostopinversion().doubleValue())
                    * (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));
            retorno += totalLinea + totalLineai;
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
            if (kardex.getSuministro().getLote()) {
                if (kardex.getLote() == null) {
                    return 0;
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.bodega=:bodega and o.lote=:lote");
//                parametros.put("suministro", kardex.getSuministro());
                parametros.put("bodega", cabecera.getBodega());
                parametros.put("lote", kardex.getLote());
                parametros.put(";campo", "(o.cantidad)*o.signo");
//                parametros.put(";campo", "(o.cantidad+o.cantidadinversion)*o.signo");
                double cuantos = ejbKardex.sumarCampoDoble(parametros);
                return cuantos;
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
            parametros = new HashMap();
//            if (cabecera.getId() == null) {
//                parametros.put(";where", "o.bodega=:bodega "
//                        + "and o.suministro=:suministro "
//                        + "and o.cabecerainventario.estado=0 and o.cabecerainventario.txid.ingreso=false");
//            } else {
//                parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro and o.cabecerainventario.estado=0 "
//                        + "and o.cabecerainventario!=:cabecera and o.cabecerainventario.txid.ingreso=false");
//                parametros.put("cabecera", cabecera);
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
//            ojo ver los ingresos y egresos ***************************************+
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            parametros.put(";campo", "o.cantidad*o.signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            retorno = valorOtros;
//            retorno = bs.getSaldo() - valorOtros;
            kardex.setCostopromedio(bs.getCostopromedio());
//        for (Kardexinventario k : listaKardex) {
//                retorno += k.getCantidad();
//        }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            if (kardex.getSuministro().getLote()) {
                if (kardex.getLote() == null) {
                    return 0;
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.bodega=:bodega and o.lote=:lote");
//                parametros.put("suministro", kardex.getSuministro());
                parametros.put("bodega", cabecera.getBodega());
                parametros.put("lote", kardex.getLote());
                parametros.put(";campo", "(o.cantidadinversion)*o.signo");
//                parametros.put(";campo", "(o.cantidad+o.cantidadinversion)*o.signo");
                double cuantos = ejbKardex.sumarCampoDoble(parametros);
                return cuantos;
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
            kardex.setCostopinversion(bs.getCostopromedioinversion());
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
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", kardex.getSuministro());
            parametros.put(";campo", "o.cantidadinversion*o.signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            double saldoBs = bs.getSaldoinversion();
            retorno = valorOtros;
//            retorno = saldoBs - valorOtros;

//        for (Kardexinventario k : listaKardex) {
//                retorno += k.getCantidad();
//        }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
//        imprimir();
//        formularioImprimir.insertar();

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
        int size = 4;
        SelectItem[] items = new SelectItem[size];
        int i = 0;

        items[0] = new SelectItem(null, "--- Seleccione uno ---");
        items[++i] = new SelectItem(0, "REGISTRO");
        items[++i] = new SelectItem(1, "DESPACHO");
        items[++i] = new SelectItem(-1, "ANULADO");
//        items[++i] = new SelectItem(i, "REVISION");
//        items[++i] = new SelectItem(i, "APROBACION");
        return items;
    }

    public String traerEstado(int estado) {
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

    public boolean isPuedeDespacho(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        // calcular los renglones para presentar en la forma

        return p.getRecepcion() && item.getEstado() == 0;
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        if (kardex != null) {
            String newWord = (String) event.getNewValue();
            kardex.setSuministro(suministroBean.traerCodigo(newWord));
        }
    }

    public double traerTotal(Cabecerainventario cab) {
        double total = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecerainventario=:cabecera");
//        parametros.put(";orden", "o.suministro.nombre");
        parametros.put("cabecera", cab);
        try {
            List<Kardexinventario> lista = ejbKardex.encontarParametros(parametros);
            for (Kardexinventario k : lista) {
                double totalLinea = k.getCostopromedio() == null ? 0 : k.getCostopromedio().doubleValue()
                        * (k.getCantidad() == null ? 0 : k.getCantidad().doubleValue());
                double totalLineai = ((k.getCostopinversion() == null ? 0 : k.getCostopinversion().doubleValue())
                        * (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));

                double cuadre = Math.round((totalLinea + totalLineai) * 1000);
                String li = cuadre + "";
                if (li.length() > 5) {
                    String li2 = li.substring(4, 5);
                    if (li2.equals("5")) {
                        cuadre = cuadre + 1;
                    }
                }
                double dividido = cuadre / 1000;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double saldo = (valortotal.doubleValue());

                total += saldo;

            }
            sumaTotal += total;
            return total;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void archivoListener(FileEntryEvent e) throws IOException {
        if (cabecera.getTxid() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de transacción");
            return;
        }
        listaErrores = new LinkedList<>();
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
                                auxK.setObservaciones("No existe suministro con código " + k.getCodigoSuministro());
                                listaErrores.add(auxK);
//                                MensajesErrores.advertencia("No existe suministro con código" + k.getCodigoSuministro());
//                                listaKardex = new LinkedList<>();
//                                return;
                            }
                            k.setSuministro(sum);

                            float cantidad = k.getCantidad() == null ? 0 : k.getCantidad();
                            float cantidadi = k.getCantidadinversion() == null ? 0 : k.getCantidadinversion();
                            if (cantidad + cantidadi <= 0) {
                                AuxiliarKardex auxK = new AuxiliarKardex();
                                auxK.setTransaccion(k.getCodigoSuministro());
                                auxK.setObservaciones("Necesaria cantidad en suministro " + k.getCodigoSuministro());
                                listaErrores.add(auxK);
//                                MensajesErrores.advertencia("Necesaria cantidad para " + k.getCodigoSuministro());
//                                listaKardex = new LinkedList<>();
//                                return;
                            }
                            if (k.getSuministro() != null) {
                                if (k.getSuministro().getLote() != null) {
                                    k.getSuministro().setLote(k.getSuministro().getLote());
                                } else {
                                    k.getSuministro().setLote(false);
                                }

                                if (k.getSuministro().getLote() != null) {
                                    if (k.getSuministro().getLote()) {
                                        if ((k.getNombreLote() == null) || (k.getNombreLote().isEmpty())) {
                                            AuxiliarKardex auxK = new AuxiliarKardex();
                                            auxK.setTransaccion(k.getCodigoSuministro());
                                            auxK.setObservaciones("Necesario número de lote en suministro " + k.getCodigoSuministro());
                                            listaErrores.add(auxK);
//                                    MensajesErrores.advertencia("Necesario número de lote : " + k.getCodigoSuministro());
//                                    return;
                                        }
                                        if (k.getFechaCaduca() == null) {
                                            AuxiliarKardex auxK = new AuxiliarKardex();
                                            auxK.setTransaccion(k.getCodigoSuministro());
                                            auxK.setObservaciones("Necesario fecha de caducidad  del lote en suministro " + k.getCodigoSuministro());
                                            listaErrores.add(auxK);
//                                    MensajesErrores.advertencia("Necesario fecha de caducidad  del lote : " + k.getCodigoSuministro());
//                                    return;
                                        }

                                        // buscar y armar el lote
                                        Map parametros = new HashMap();
                                        parametros.put(";where", "o.lote=:lote and o.fechaCaduca=:fechaCaduca");
                                        parametros.put("lote", k.getNombreLote());
                                        parametros.put("fechaCaduca", k.getFechaCaduca());
                                        List<Lotessuministros> lotes = ejbLotes.encontarParametros(parametros);
                                        if (lotes.isEmpty()) {
                                            AuxiliarKardex auxK = new AuxiliarKardex();
                                            auxK.setTransaccion(k.getCodigoSuministro());
                                            auxK.setObservaciones("Necesario lote válido para realizar el egreso en suministro " + k.getCodigoSuministro());
                                            listaErrores.add(auxK);
//                                    MensajesErrores.advertencia("Necesario lote válido para realizar el egreso: " + k.getCodigoSuministro());
//                                    return;
                                        } else {
                                            k.setLote(lotes.get(0));
                                        }
                                    }
                                }

                                Map parametros = new HashMap();
                                parametros.put(";where", "o.equivalencia='UNIDAD' and o.factor=1");
                                List<Unidades> listaUnidad = ejbUnidades.encontarParametros(parametros);
                                Unidades unidad = null;
                                if (!listaUnidad.isEmpty()) {
                                    unidad = listaUnidad.get(0);
                                    k.setUnidad(unidad);
                                }

                                kardex = k;
                                if (validarRenglon()) {
                                    listaKardex = new LinkedList<>();
                                    return;
                                }
                                renLista.add(k);
//                            renglones.add(r);
                            }
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.advertencia(ex.getMessage());
                        Logger.getLogger(IngresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ConsultarException ex) {
                        Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public SelectItem[] getComboLotes() {
        if (kardex == null) {
            return null;
        }
        if (kardex.getSuministro() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.suministro=:suministro and o.fechaCaduca>=:fecha");
        parametros.put("suministro", kardex.getSuministro());
        parametros.put("fecha", cabecera.getFecha());
        try {
            List<Lotessuministros> lotes = ejbLotes.encontarParametros(parametros);
            List<Lotessuministros> listaCombo = new LinkedList<>();
            for (Lotessuministros l : lotes) {
                parametros = new HashMap();
                parametros.put(";where", "o.suministro=:suministro and o.bodega=:bodega and o.lote=:lote");
                parametros.put("suministro", kardex.getSuministro());
                parametros.put("bodega", cabecera.getBodega());
                parametros.put("lote", l);
                parametros.put(";campo", "(o.cantidad+o.cantidadinversion)*o.signo");
                double cuantos = ejbKardex.sumarCampoDoble(parametros);
                if (cuantos > 0) {
                    l.setCantidad(new BigDecimal(cuantos));
                    listaCombo.add(l);

                }
            }
            return Combos.getSelectItems(listaCombo, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

    ////////////////////////////////////////////7Contabiliza
    public String armaRenglones() {
        renglones = new LinkedList<>();
        renglonesReclasificacion = new LinkedList<>();
        if (cabecera.getTxid().getContabiliza()) {
            for (Kardexinventario i : listaKardex) {
                Renglones r = new Renglones();
                // armar cuenta
                String cuenta = i.getSuministro().getTipo().getCuenta();
                String cuentaInversion = i.getSuministro().getTipo().getCuentainversion();
//                    String cuenta = cabecera.getTxid().getIniciales() + i.getSuministro().getTipo().getCuenta();
                // como coloco el cc
                Cuentas cuentaUno = getCuentasBean().traerCodigo(cuenta);
                Cuentas cuentaUnoInversion = getCuentasBean().traerCodigo(cuentaInversion);
                getCuentasBean().setCuenta(cuentaUno);
                if (getCuentasBean().validaCuentaMovimiento()) {
                    MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta + " Familia : " + i.getSuministro().getTipo().getCodigo());
                    return null;
                }
                getCuentasBean().setCuenta(cuentaUnoInversion);
                if (getCuentasBean().validaCuentaMovimiento()) {
                    MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuentaInversion);
                    return null;
                }
                r.setFecha(cabecera.getFecha());
                r.setCuenta(cuenta);
                r.setReferencia(cabecera.getObservaciones());
                double cantidad = (i.getCantidad() == null ? 0 : i.getCantidad().doubleValue());
                double cantidadInversion = (i.getCantidadinversion() == null ? 0 : i.getCantidadinversion().doubleValue());
                double costo = i.getCostopromedio() == null ? 1 : i.getCostopromedio().doubleValue();
                double costoInversion = i.getCostopinversion() == null ? 1 : i.getCostopinversion().doubleValue();
                double valor = costo * cantidad;
                double valorInversion = costoInversion * cantidadInversion;

                double cuadre = Math.round(valor * 100);
                double dividido = cuadre / 100;
                BigDecimal valorBase = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                valor = valorBase.doubleValue();

                if (valor != 0) {
                    r.setValor(new BigDecimal(valor * -1));
                    r.setReferencia(cabecera.getObservaciones());
                    estaEnRas(r);
                    //
//                    Cuentas ctaReclas = getCuentasBean().traerCodigo(cuenta);
//                    reclasificacion(ctaReclas, r, 1);
                    //

                    r = new Renglones();
                    // armar cuenta
//                    cuenta = cabecera.getTxid().getCuenta();
                    cuenta = i.getSuministro().getTipo().getCodigoconsumo();
//                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getCuenta();
                    // como coloco el cc
                    cuentaUno = getCuentasBean().traerCodigo(cuenta);
                    getCuentasBean().setCuenta(cuentaUno);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                        return null;
                    }
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(cabecera.getObservaciones());
                    r.setValor(new BigDecimal(valor));
                    estaEnRas(r);
                    //
//                    ctaReclas = getCuentasBean().traerCodigo(cuenta);
//                    reclasificacion(ctaReclas, r, 1);
                    //
                }
                // inversion
                // primera linea
                if (valorInversion != 0) {
                    r = new Renglones();
//                    r.setValor(new BigDecimal(valor * -1));
                    r.setValor(new BigDecimal(valorInversion * -1));
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuentaInversion);
                    r.setReferencia(cabecera.getObservaciones());
//                    r.setValor(new BigDecimal(valorInversion));
                    estaEnRas(r);
                    //
                    Cuentas ctaReclas = getCuentasBean().traerCodigo(cuentaInversion);
                    reclasificacion(ctaReclas, r, 1);
                    //
                    // Segunda linea
                    r = new Renglones();
                    // armar cuenta
//                    cuenta = cabecera.getTxid().getCuenta();
                    cuenta = i.getSuministro().getTipo().getCodigoinversion();
//                    cuenta = cabecera.getTxid().getCuenta() + i.getSuministro().getTipo().getCuenta();
                    // como coloco el cc
                    cuentaUno = getCuentasBean().traerCodigo(cuenta);
                    getCuentasBean().setCuenta(cuentaUno);
                    if (getCuentasBean().validaCuentaMovimiento()) {
                        MensajesErrores.advertencia("Cuenta contable mal configurada : " + cuenta);
                        return null;
                    }
                    r.setFecha(cabecera.getFecha());
                    r.setCuenta(cuenta);
                    r.setReferencia(cabecera.getObservaciones());
                    r.setValor(new BigDecimal(valorInversion));
                    estaEnRas(r);
                    //
//                    ctaReclas = getCuentasBean().traerCodigo(cuenta);
//                    reclasificacion(ctaReclas, r, 1);
                    //
                }
                //
            }

        }
        return null;
    }

    private boolean estaEnRas(Renglones r1) {
        for (Renglones r : getRenglones()) {
            if (r.getCuenta().equals(r1.getCuenta())) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
                r.setFecha(cabecera.getFecha());
                return true;
            }
        }
        getRenglones().add(r1);
        return false;
    }

    private void reclasificacion(Cuentas cuenta, Renglones ras, int anulado) {
        if (!((cuenta.getCodigonif() == null) || (cuenta.getCodigonif().isEmpty()))) {
            Renglones rasInvInt = new Renglones();
            rasInvInt.setCuenta(cuenta.getCodigonif());
            double valor = (ras.getValor().doubleValue()) * anulado;
            rasInvInt.setValor(new BigDecimal(valor));
            rasInvInt.setReferencia("Reclasificación : " + ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasInvInt.setCentrocosto(ras.getCentrocosto());
            }
            rasInvInt.setFecha(new Date());
            rasInvInt.setNombre(cuenta.getNombre());
            rasInvInt.setValor(new BigDecimal(valor));
            if (rasInvInt.getValor().doubleValue() > 0) {
                rasInvInt.setDebitos(rasInvInt.getValor().doubleValue());
            } else {
                rasInvInt.setCreditos(rasInvInt.getValor().doubleValue() * -1);
            }
            estaEnRenglonesReclasificacion(rasInvInt);
            Renglones rasContrapate = new Renglones();
            rasContrapate.setCuenta(ras.getCuenta());
            valor = valor * -1;
            rasContrapate.setValor(new BigDecimal(valor));
            rasContrapate.setReferencia("Reclasificación Contraparte : " + ras.getReferencia());
            if (cuenta.getCcosto()) {
                rasContrapate.setCentrocosto(ras.getCentrocosto());
            }
            rasInvInt.setNombre(cuenta.getNombre());
            if (rasInvInt.getValor().doubleValue() > 0) {
                rasInvInt.setDebitos(rasInvInt.getValor().doubleValue());
            } else {
                rasInvInt.setCreditos(rasInvInt.getValor().doubleValue() * -1);
            }
            rasContrapate.setFecha(new Date());
            rasContrapate.setValor(new BigDecimal(valor));
            estaEnRenglonesReclasificacion(rasContrapate);

        }
    }

    private boolean estaEnRenglonesReclasificacion(Renglones r1) {
//        r1.setAuxiliar("");
        if (r1.getAuxiliar() == null) {
            r1.setAuxiliar("");
        }
        if (r1.getCentrocosto() == null) {
            r1.setCentrocosto("");
        }

        for (Renglones r : renglonesReclasificacion) {
            if (r.getAuxiliar() == null) {
                r.setAuxiliar("");
            }
            if (r.getCentrocosto() == null) {
                r.setCentrocosto("");
            }
            if ((r.getCuenta().equals(r1.getCuenta()))
                    && (r.getCentrocosto().equals(r1.getCentrocosto()))
                    && (r.getAuxiliar().equals(r1.getAuxiliar()))) {
                double valor = r1.getValor().doubleValue() + r.getValor().doubleValue();
                r.setValor(new BigDecimal(valor));
//                r.setFecha(new Date());
                return true;
            }
        }
        renglonesReclasificacion.add(r1);
        return false;
    }
    /////////////////////////////////////////////////////////////

    /**
     * @param renglones the renglones to set
     */
    public void setRenglones(List<Renglones> renglones) {
        this.renglones = renglones;
    }

    /**
     * @return the renglonesReclasificacion
     */
    public List<Renglones> getRenglonesReclasificacion() {
        return renglonesReclasificacion;
    }

    /**
     * @param renglonesReclasificacion the renglonesReclasificacion to set
     */
    public void setRenglonesReclasificacion(List<Renglones> renglonesReclasificacion) {
        this.renglonesReclasificacion = renglonesReclasificacion;
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

    /**
     * @return the cabeceraTransferencia
     */
    public Cabecerainventario getCabeceraTransferencia() {
        return cabeceraTransferencia;
    }

    /**
     * @param cabeceraTransferencia the cabeceraTransferencia to set
     */
    public void setCabeceraTransferencia(Cabecerainventario cabeceraTransferencia) {
        this.cabeceraTransferencia = cabeceraTransferencia;
    }

    /**
     * @return the suministroBean
     */
    public SuministrosBean getSuministroBean() {
        return suministroBean;
    }

    /**
     * @param suministroBean the suministroBean to set
     */
    public void setSuministroBean(SuministrosBean suministroBean) {
        this.suministroBean = suministroBean;
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
     * @return the renglones
     */
    public List<Renglones> getRenglones() {
        return renglones;
    }

    /**
     * @return the sumaTotal
     */
    public double getSumaTotal() {
        return sumaTotal;
    }

    /**
     * @param sumaTotal the sumaTotal to set
     */
    public void setSumaTotal(double sumaTotal) {
        this.sumaTotal = sumaTotal;
    }
}
