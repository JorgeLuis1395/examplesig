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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AdicionalkardexFacade;
import org.beans.sfccbdmq.BitacorasuministroFacade;
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.CabecerainventarioFacade;
import org.beans.sfccbdmq.CabecerasFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.KardexinventarioFacade;
import org.beans.sfccbdmq.LotessuministrosFacade;
import org.beans.sfccbdmq.PermisosbodegasFacade;
import org.beans.sfccbdmq.PlantillasFacade;
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
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Constataciones;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Kardexinventario;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Permisosbodegas;
import org.entidades.sfccbdmq.Plantillas;
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
import org.seguridad.sfccbdmq.EntidadesBean;
import org.talento.sfccbdmq.CargoxOrganigramaBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "egresosPlanillasSfccbdmq")
@ViewScoped
public class EgresosPlantillasBean {

    /**
     * Creates a new instance of CabeceraBean
     */
    public EgresosPlantillasBean() {
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
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private PlantillasFacade ejbPlantillas;
    @EJB
    private CabecerasFacade ejbCabeceras;

    private Formulario formulario = new Formulario();
    private Formulario formularioRenglones = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Cabecerainventario cabecera;
    private Cabecerainventario cabeceraTransferencia;
    private Kardexinventario kardex;
    private int estadoIngreso;
    // autocompletar paar que ponga el custodio
    private LazyDataModel<Cabecerainventario> cabeceras;
    private List<Kardexinventario> listaKardex;
    private List<Kardexinventario> listaKardexb;
    private List<Empleados> listaEmpleados;
    private List<Empleados> listaEmpleadosSeleccionados;
    private List<Cabecerainventario> listacabeceras;
    private Integer factura;
    private double descuentoInterno;
    private double descuentoExterno;
    private double cantidadModificada;
    private String observaciones;
    private Codigos tipoCodigos;
    private Solicitudsuministros solicitud;
    private Bodegas bodega;
    private boolean transferencia;
    private Contratos contrato;
    private int operativo;
    private Resource reporte;
    private Codigos genero;
    private Cargosxorganigrama cargo;
    private Perfiles perfil;
    private Unidades unidad;
    private String separador = ";";

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{obligacionesCompromisoSfccbdmq}")
    private ObligacionesCompromisoBean obligacionesBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suministroBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{suministrosSfccbdmq}")
    private SuministrosBean suminitrosBean;
    @ManagedProperty(value = "#{cargoxorganigrama}")
    private CargoxOrganigramaBean cxcBean;

    @PostConstruct
    private void activar() {
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

        nuevo();

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
        getCxcBean().setOrganigramaL(null);
        getCxcBean().setNomOrganigrama(null);
        return null;
    }

    public String buscarEmpleado() {

        Map parametros = new HashMap();

        String where = "o.activo=true and o.cargoactual is not null and o.fechasalida is null and o.entidad.activo=true";
        if (entidadesBean.getEntidad() != null) {
            where += " and o.id=:id";
            parametros.put("id", entidadesBean.getEntidad().getEmpleados().getId());
        }

        if (cxcBean.getOrganigramaL() != null) {
            if (!(cargo == null)) {
                where += " and o.cargoactual=:carg ";
                parametros.put("carg", cargo);
            } else {
                where += " and upper(o.cargoactual.organigrama.codigo) like :organigrama ";
                parametros.put("organigrama", cxcBean.getOrganigramaL().getCodigo() + "%");
            }
        }
        if (!(genero == null)) {
            where += " and o.entidad.genero=:genero ";
            parametros.put("genero", genero);
        }
        if (operativo == 1) {
            where += " and o.operativo=true ";
        } else if (operativo == -1) {
            where += " and o.operativo=false ";
        }

        try {
            parametros.put(";where", where);
            parametros.put(";orden", "o.entidad.apellidos asc");
            listaEmpleados = ejbEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
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

    public String imprimir(List<Cabecerainventario> lista) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // taer la kardex
        // solo egresos si es transferencia
        try {
            DocumentoPDF pdf;
            String cabeceraStr = configuracionBean.getConfiguracion().getNombre();
            cabeceraStr += "\n" + "EGRESO DE SUMINISTROS";
            pdf = new DocumentoPDF(cabeceraStr, null, seguridadbean.getLogueado().getUserid(), 1);
            boolean segunda = false;
            for (Cabecerainventario c : lista) {

                if (c.getCabecera() != null) {
                    transferencia = true;
                }
                Map parametros = new HashMap();
                parametros.put(";where", "o.cabecerainventario=:cabecera");
                parametros.put(";orden", "o.suministro.nombre");
                parametros.put("cabecera", c);

                List<Kardexinventario> listaK = ejbKardex.encontarParametros(parametros);
                List<AuxiliarReporte> titulos = new LinkedList<>();

                if (segunda) {
                    pdf.agregaParrafo("\n\n");
                    pdf.agregaParrafo("\n\n");
                    pdf.finDePagina();
                }
                segunda = true;

                ///CABECERA
                List<AuxiliarReporte> columnas = new LinkedList<>();
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Fecha de Mov. : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, sdf.format(c.getFecha())));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Estado"));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, traerEstado(c.getEstado())));

                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Transacción : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getTxid().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Bodega : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getBodega().toString()));

                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "No. : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getNumero() == null ? "" : c.getNumero().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));

                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Solicitante : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getSolicitante() == null ? "" : c.getSolicitante().getEntidad().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Dirección : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getDireccion() == null ? "" : c.getDireccion().toString()));

                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Factura : "));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getFactura() == null ? "" : c.getFactura().toString()));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, ""));
                columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                if (c.getCabecera() != null) {
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "B. transf. : "));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCabecera().getBodega().toString()));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "No. Ingr. transf"));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getCabecera().getNumero().toString()));
                }
                if (c.getProveedor() != null) {
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, "Proveedor : "));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, c.getProveedor().getEmpresa().toString()));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, true, ""));
                    columnas.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                }
                pdf.agregarTabla(null, columnas, 4, 100, null);
                ////FIN DE CABECERA
                pdf.agregaParrafo("\n\n");
                pdf.agregaParrafo("Observaciones : " + c.getObservaciones() + "\n\n");

                int totalCol = 9;
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "No."));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "CODIGO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, true, "LOTE"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, true, "SUMINISTRO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CANTIDAD"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "CANTIDAD INVERSION"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "COSTO PROMEDIO"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "COSTO PROMEDIO INVERSION"));
                titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 8, true, "TOTAL"));
//            totalCol++;
                int i = 0;
                double valorTotal = 0;
                double valorDescuento = 0;
                List<AuxiliarReporte> campos = new LinkedList<>();
                for (Kardexinventario k : listaK) {
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, String.valueOf(++i)));
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getSuministro().getCodigobarras()));
                    campos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getLote() == null ? "" : k.getLote().getLote()));
                    campos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 8, false, k.getSuministro().getNombre()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCantidad() == null ? 0 : k.getCantidad().doubleValue()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCostopromedio() == null ? 0.0 : k.getCostopromedio().doubleValue()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, k.getCostopinversion() == null ? 0.0 : k.getCostopinversion().doubleValue()));
                    double totalLinea = (k.getCostopromedio() == null ? 0 : k.getCostopromedio().doubleValue())
                            * (k.getCantidad() == null ? 0 : k.getCantidad().doubleValue());
                    double totalLineai = ((k.getCostopinversion() == null ? 0 : k.getCostopinversion().doubleValue())
                            * (k.getCantidadinversion() == null ? 0 : k.getCantidadinversion().doubleValue()));
                    campos.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 8, false, totalLinea + totalLineai));
                    valorTotal += totalLinea + totalLineai;

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
                String solicitante = c.getSolicitante() == null ? "" : c.getSolicitante().getEntidad().toString();
                String elaborado = seguridadbean.getLogueado() == null ? "" : seguridadbean.getLogueado().toString();
                Entidades entidad = ejbEntidades.traerUserId(c.getUsuario());
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
            }
            reporte = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosPlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        getFormularioImprimir().editar();
        return null;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        listacabeceras = new LinkedList<>();
        try {
            for (Empleados e : listaEmpleadosSeleccionados) {

                // crear solicitud 
                Txinventarios t = ejbTxInventario.find(cabecera.getTxid().getId());
                int numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
                t.setUltimo(numeroTx);

                Cabecerainventario cab = new Cabecerainventario();
                cab.setTxid(cabecera.getTxid());
                cab.setFecha(cabecera.getFecha());
                cab.setFechadigitacion(new Date());
                cab.setBodega(cabecera.getBodega());
                cab.setEstado(1);
                cab.setObservaciones(cabecera.getObservaciones());
                cab.setNumero(numeroTx);
                cab.setSolicitante(e);
                cab.setDireccion(e.getCargoactual().getOrganigrama());
                cab.setUsuario(seguridadbean.getLogueado().toString());

                if (cabecera.getTxid().getProveedor()) {
//                ************************************************ OJOJOJOJOJOJJO
                    cab.setProveedor(proveedoresBean.getProveedor());
                } else {
                    cab.setProveedor(null);
                    cab.setContrato(null);
                }
                // crear la solicitud
                ejbCabecerainventario.create(cab, getSeguridadbean().getLogueado().getUserid());
                ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
                listacabeceras.add(cab);
                Map parametros = new HashMap();
                parametros.put(";where", "o.tipo=:tipo");
                parametros.put("tipo", tipoCodigos);
                List<Plantillas> lista = ejbPlantillas.encontarParametros(parametros);
                List<Kardexinventario> listaK = new LinkedList<>();;
                for (Plantillas p : lista) {
                    listaK = new LinkedList<>();
                    Suministros sum = suministroBean.traerCodigo(p.getCodigo());
                    Kardexinventario k = new Kardexinventario();
                    if (descuentoExterno > 0) {
                        // aplca a cada linea
                        double valor = k.getCosto().doubleValue() * (1 - descuentoExterno / 100);
                        k.setCosto(new Float(valor));
                    }

                    parametros = new HashMap();
                    parametros.put(";where", "o.suministro=:suministro");
                    parametros.put("suministro", sum);
                    List<Bodegasuministro> listaB = ejbBodSum.encontarParametros(parametros);
                    Bodegasuministro bs = null;
                    if (!listaB.isEmpty()) {
                        bs = listaB.get(0);
                    }

                    k.setCabecerainventario(cab);
                    k.setBodega(cabecera.getBodega());
                    k.setFecha(cabecera.getFecha());
                    k.setSuministro(sum);
                    k.setCantidad(new Float(p.getCantidad()));
                    k.setCosto(new Float(0));
                    k.setCostopromedio(new Float(getCostoPromedio(sum)));
                    k.setUnidad(unidad);
                    k.setCantidadinversion(new Float(p.getCantidadinv()));
                    k.setSigno(-1);
                    k.setTipoorden(2);
                    k.setCostopinversion(new Float(getCostoPromedioInv(sum)));
                    k.setHora(new Date());
                    k.setSaldoanterior(bs.getSaldo());
                    k.setSaldoanteriorinversion(bs.getSaldoinversion());
                    ejbKardex.create(k, getSeguridadbean().getLogueado().getUserid());

                    if (p.getCantidad() != 0) {
                        bs.setSaldo(bs.getSaldo() - p.getCantidad());
                    }
                    if (p.getCantidadinv() != 0) {
                        bs.setSaldoinversion(bs.getSaldoinversion() - p.getCantidadinv());
                    }

                    ejbBodSum.edit(bs, getSeguridadbean().getLogueado().getUserid());
                }
                if (cab.getTxid().getTransaferencia() != null) {
                    t = ejbTxInventario.find(cab.getTxid().getTransaferencia().getId());
                    numeroTx = (t.getUltimo() == null ? 0 : t.getUltimo()) + 1;
                    t.setUltimo(numeroTx);
                    ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
                    cabeceraTransferencia.setTxid(cab.getTxid().getTransaferencia());
                    cabeceraTransferencia.setNumero(numeroTx);
                    cabeceraTransferencia.setFecha(cabecera.getFecha());
                    cabeceraTransferencia.setFechadigitacion(new Date());
                    cabeceraTransferencia.setObservaciones(cabecera.getObservaciones());
                    cabeceraTransferencia.setCabecera(cab);
                    ejbCabecerainventario.create(cabeceraTransferencia, getSeguridadbean().getLogueado().getUserid());
                    cab.setCabecera(cabeceraTransferencia);
                    ejbCabecerainventario.edit(cab, getSeguridadbean().getLogueado().getUserid());
                    ejbTxInventario.edit(t, getSeguridadbean().getLogueado().getUserid());
                }
                Bitacorasuministro bi = new Bitacorasuministro();
                bi.setUsuario(seguridadbean.getLogueado().getEmpleados());
                bi.setEstado(1);
                bi.setFecha(new Date());
                bi.setCabecera(cab);
                ejbBitacora.create(bi, seguridadbean.getLogueado().getUserid());
                ejbCabecerainventario.ejecutarSaldosSuministro(listaK);
            }
            imprimir(listacabeceras);

        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        proveedoresBean.setProveedor(null);
        proveedoresBean.setRuc(null);
        listaEmpleados = new LinkedList<>();
        listaEmpleadosSeleccionados = new LinkedList<>();
        listaKardex = new LinkedList<>();
        entidadesBean.setApellidos(null);
        entidadesBean.setEntidad(null);
        getCxcBean().setOrganigramaL(null);
        getCxcBean().setNomOrganigrama(null);
        return null;
    }

    private boolean validar() {
        if ((cabecera.getObservaciones() == null) || (cabecera.getObservaciones().isEmpty())) {
            MensajesErrores.advertencia("Observaciones es obligatorio");
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
        if ((listaKardex == null) || (listaKardex.isEmpty())) {
            MensajesErrores.advertencia("Necesario suministros para la transacción");
            return true;
        }

        return false;
    }

    // ver si le dejo cambiar si no ha entregado el producto
    public String nuevoKardex() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.tipo=:tipo");
        parametros.put("tipo", tipoCodigos);
        try {
            List<Plantillas> lista = ejbPlantillas.encontarParametros(parametros);
            if (lista.isEmpty()) {
                MensajesErrores.advertencia("Plantilla sin datos");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosPlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        if (cabecera.getFecha() == null) {
            MensajesErrores.advertencia("Ingrese una fecha de transacción");
            return null;
        }
        if (tipoCodigos == null) {
            MensajesErrores.advertencia("Seleccione una Plantilla");
            return null;
        }
        descuentoInterno = 0;
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

    public String insertarRenglon() {
        try {
            if (listaEmpleados.isEmpty()) {
                MensajesErrores.advertencia("Selecccione al menos un empleado");
                return null;
            }
            if (listaEmpleados == null) {
                MensajesErrores.advertencia("Selecccione al menos un empleado");
                return null;
            }
            listaEmpleadosSeleccionados = new LinkedList<>();
            for (Empleados em : listaEmpleados) {
                if (em.getSelecionado()) {
                    listaEmpleadosSeleccionados.add(em);
                }
            }
            int emple = listaEmpleadosSeleccionados.size();

            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", tipoCodigos);
            List<Plantillas> lista = ejbPlantillas.encontarParametros(parametros);

//        if (descuentoInterno > 0) {
//            double valor = kardex.getCosto().doubleValue() * (1 - descuentoInterno / 100);
//            kardex.setCosto(new Float(valor));
//        }
            for (Plantillas p : lista) {
                Suministros sum = suministroBean.traerCodigo(p.getCodigo());
                double cantidad = p.getCantidad() * emple;
                if (cantidad != 0) {
                    cantidad = getSaldo(sum) - cantidad;
                    if (cantidad < 0) {
                        MensajesErrores.advertencia("Cantidad sobrepasa el saldo de bodega -saldo: " + getSaldo(sum) + " -cantidad: " + p.getCantidad() * emple + " - " + sum.getCodigobarras());
                        return null;
                    }
                }
                double cantidadinv = p.getCantidadinv() * emple;
                if (cantidadinv != 0) {
                    cantidadinv = getSaldo(sum) - cantidadinv;
                    if (cantidadinv < 0) {
                        MensajesErrores.advertencia("Cantidad inversión sobrepasa el saldo de bodega -saldo: " + getSaldo(sum) + " -cantidad: " + p.getCantidadinv() * emple + " - " + sum.getCodigobarras());
                        return null;
                    }
                }
            }

            parametros = new HashMap();
            parametros.put(";where", "o.equivalencia='UNIDAD' and o.factor=1");
            List<Unidades> listaUnidad = ejbUnidades.encontarParametros(parametros);
            unidad = null;
            if (!listaUnidad.isEmpty()) {
                unidad = listaUnidad.get(0);
            }
            for (Empleados e : listaEmpleadosSeleccionados) {

                if (e.getCargoactual() == null) {
                    MensajesErrores.advertencia("Necesario que empleado tenga cargo:" + e.toString());
                    return null;
                }

                for (Plantillas p : lista) {
                    Suministros sum = suministroBean.traerCodigo(p.getCodigo());
                    Kardexinventario k = new Kardexinventario();
                    k.setSuministro(sum);
                    k.setCantidad(new Float(p.getCantidad()));
                    k.setCostopromedio(new Float(getCostoPromedio(sum)));
                    k.setCantidadinversion(new Float(p.getCantidad()));
                    k.setCostopromedio(new Float(getCostoPromedio(sum)));
                    k.setUnidad(unidad);
                    listaKardex.add(k);
                }
            }

            formularioRenglones.cancelar();
        } catch (ConsultarException ex) {
            Logger.getLogger(EgresosPlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

        return null;
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

    public boolean isPuedeDespacho(Cabecerainventario item) {
        if (bodega == null) {
            return false;
        }
        Permisosbodegas p = traerPerfil(bodega);
        return p.getRecepcion() && item.getEstado() == 0;
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

    public double getSaldo(Suministros s) {
        double retorno = 0;
        try {
            if (s == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
            if (cabecera.getTxid().getIngreso()) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", s);
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            parametros = new HashMap();
            if (cabecera.getId() == null) {
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro ");
            } else {
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro and o.cabecerainventario!=:cabecera");
                parametros.put("cabecera", cabecera);
            }
//            ojo ver los ingresos y egresos ***************************************+
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", s);
            parametros.put(";campo", "o.cantidad*o.signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            retorno = valorOtros;
//            kardex.setCostopromedio(bs.getCostopromedio());

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getSaldoInversion(Suministros s) {
        double retorno = 0;
        try {
            if (s == null) {
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
            parametros.put("suministro", s);
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
            } else {
                parametros.put(";where", "o.bodega=:bodega "
                        + " and o.suministro=:suministro and o.cabecerainventario!=:cabecera");
                parametros.put("cabecera", cabecera);
            }
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", s);
            parametros.put(";campo", "o.cantidadinversion*o.signo");
            double valorOtros = ejbKardex.sumarCampoDoble(parametros);
            retorno = valorOtros;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getCostoPromedio(Suministros s) {
        double retorno = 0;
        try {
            if (s == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", s);
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            retorno = bs.getCostopromedio();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public double getCostoPromedioInv(Suministros s) {
        double retorno = 0;
        try {
            if (s == null) {
                return 0;
            }
            if (cabecera.getBodega() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.bodega=:bodega and o.suministro=:suministro");
            parametros.put("bodega", cabecera.getBodega());
            parametros.put("suministro", s);
            List<Bodegasuministro> listaBodegas = ejbBodSum.encontarParametros(parametros);
            Bodegasuministro bs = null;
            for (Bodegasuministro b : listaBodegas) {
                bs = b;
            }
            if (bs == null) {
                return 0;
            }
            retorno = bs.getCostopromedioinversion();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(EgresosSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String seleccionar() {
        for (Empleados c : listaEmpleados) {
            if (!c.getSelecionado()) {
                c.setSelecionado(Boolean.TRUE);
            }
        }
        return null;
    }

    public String quitar() {
        for (Empleados c : listaEmpleados) {
            if (c.getSelecionado()) {
                c.setSelecionado(Boolean.FALSE);
            }
        }
        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        listaEmpleados = new LinkedList<>();
        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {
            if (i.isSaved()) {
                File file = i.getFile();
                if (file != null) {
                    parent = file.getParentFile();
                    BufferedReader entrada = null;
                    try {
                        entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                        //                        entrada = new BufferedReader(new FileReader(file));

                        String sb;
//                        try {
                        // linea de cabeceras
                        sb = entrada.readLine();
                        String[] cabecera = sb.split(getSeparador());// lee los caracteres en el arreglo
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
                            Entidades r = new Entidades();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(r, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            registro++;
                            // ver si esta ben el registro o es error 
                            if (r.getPin() == null) {
                                MensajesErrores.advertencia("Cédula no valida en registro: " + String.valueOf(registro));
                                return;
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.entidad.pin=:pin");
                                parametros.put("pin", r.getPin());
                                List<Empleados> ls = ejbEmpleados.encontarParametros(parametros);
                                Empleados s = null;
                                for (Empleados su : ls) {
                                    s = su;
                                }
                                if (s == null) {
                                    MensajesErrores.advertencia("Cédula no valida  " + r.getPin());
                                    return;
                                } else {
                                    s.setSelecionado(Boolean.TRUE);
                                    listaEmpleados.add(s);
                                }
                            }
                        }//fin while
                    } catch (UnsupportedEncodingException | FileNotFoundException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(PlantillasBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }

    }

    private void ubicar(Entidades am, String titulo, String valor) {
        if (titulo.contains("cedula")) {
            am.setPin(valor);
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
     * @return the listaKardexb
     */
    public List<Kardexinventario> getListaKardexb() {
        return listaKardexb;
    }

    /**
     * @param listaKardexb the listaKardexb to set
     */
    public void setListaKardexb(List<Kardexinventario> listaKardexb) {
        this.listaKardexb = listaKardexb;
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
     * @return the cantidadModificada
     */
    public double getCantidadModificada() {
        return cantidadModificada;
    }

    /**
     * @param cantidadModificada the cantidadModificada to set
     */
    public void setCantidadModificada(double cantidadModificada) {
        this.cantidadModificada = cantidadModificada;
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
     * @return the tipoCodigos
     */
    public Codigos getTipoCodigos() {
        return tipoCodigos;
    }

    /**
     * @param tipoCodigos the tipoCodigos to set
     */
    public void setTipoCodigos(Codigos tipoCodigos) {
        this.tipoCodigos = tipoCodigos;
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
     * @return the genero
     */
    public Codigos getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(Codigos genero) {
        this.genero = genero;
    }

    /**
     * @return the cargo
     */
    public Cargosxorganigrama getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargosxorganigrama cargo) {
        this.cargo = cargo;
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
     * @return the cxcBean
     */
    public CargoxOrganigramaBean getCxcBean() {
        return cxcBean;
    }

    /**
     * @param cxcBean the cxcBean to set
     */
    public void setCxcBean(CargoxOrganigramaBean cxcBean) {
        this.cxcBean = cxcBean;
    }

    /**
     * @return the listaEmpleados
     */
    public List<Empleados> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<Empleados> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the listaEmpleadosSeleccionados
     */
    public List<Empleados> getListaEmpleadosSeleccionados() {
        return listaEmpleadosSeleccionados;
    }

    /**
     * @param listaEmpleadosSeleccionados the listaEmpleadosSeleccionados to set
     */
    public void setListaEmpleadosSeleccionados(List<Empleados> listaEmpleadosSeleccionados) {
        this.listaEmpleadosSeleccionados = listaEmpleadosSeleccionados;
    }

    /**
     * @return the operativo
     */
    public int getOperativo() {
        return operativo;
    }

    /**
     * @param operativo the operativo to set
     */
    public void setOperativo(int operativo) {
        this.operativo = operativo;
    }

    /**
     * @return the unidad
     */
    public Unidades getUnidad() {
        return unidad;
    }

    /**
     * @param unidad the unidad to set
     */
    public void setUnidad(Unidades unidad) {
        this.unidad = unidad;
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
     * @return the listacabeceras
     */
    public List<Cabecerainventario> getListacabeceras() {
        return listacabeceras;
    }

    /**
     * @param listacabeceras the listacabeceras to set
     */
    public void setListacabeceras(List<Cabecerainventario> listacabeceras) {
        this.listacabeceras = listacabeceras;
    }

}
