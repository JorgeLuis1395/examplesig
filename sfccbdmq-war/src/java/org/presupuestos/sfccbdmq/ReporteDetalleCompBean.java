/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.application.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoXLS;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.CabdocelectFacade;
import org.beans.sfccbdmq.DescuentoscomprasFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetallescomprasFacade;
import org.beans.sfccbdmq.DetallesfondoFacade;
import org.beans.sfccbdmq.DetallesvalesFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.DocumentoselectronicosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PagosvencimientosFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.RenglonesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.beans.sfccbdmq.VistaDevengadoFacade;
import org.beans.sfccbdmq.VistaEjecutadoFacade;
import org.compras.sfccbdmq.ProveedoresBean;
import org.contabilidad.sfccbdmq.CuentasBean;
import org.entidades.sfccbdmq.Cabdocelect;
import org.entidades.sfccbdmq.Cajas;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Cuentas;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detallescompras;
import org.entidades.sfccbdmq.Detallesfondo;
import org.entidades.sfccbdmq.Detallesvales;
import org.entidades.sfccbdmq.Documentoselectronicos;
import org.entidades.sfccbdmq.Fondos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.entidades.sfccbdmq.VistaDevengado;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteDetalleComp")
@ViewScoped
public class ReporteDetalleCompBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Detallecompromiso> listadoDetallecompromiso;
    private List<Detallecompromiso> listadoDetallecomprom;
    private List<Cabdocelect> listaCabeceras;
    private Formulario formulario = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private int validado;
    private Date desde;
    private Date desdeInicio;
    private Date hasta;
    private Contratos contrato;
    private Detallecompromiso detalle;
//    private String proyecto;
    private String clasificador;
    private String motivo;
    private Integer numero;
    private List<Rascompras> listaRascompras;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private RascomprasFacade ejbRascompras;
    @EJB
    private DetallescomprasFacade ejbDetallescompras;
    @EJB
    private VistaDevengadoFacade ejbVistaDevengado;
    @EJB
    private VistaEjecutadoFacade ejbVistaEjecutado;
    @EJB
    private DocumentoselectronicosFacade ejbDocumentoselectronicos;
    @EJB
    private PagosvencimientosFacade ejbpPagosvencimientos;
    @EJB
    private DetallesvalesFacade ejbDetallesvales;
    @EJB
    private RenglonesFacade ejbRenglones;
    @EJB
    private DetallesfondoFacade ejbDetallesfondo;
    @EJB
    private ViaticosempleadoFacade ejbViaticosempleado;
    @EJB
    private DetalleviaticosFacade ejbDetalleviaticos;
    @EJB
    private DescuentoscomprasFacade ejbDescuentoscompras;
    @EJB
    private RetencionescomprasFacade ejbRetencionescompras;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private CabdocelectFacade ejbCabdocelectFacade;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedoresBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean partidasBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private Resource reporte;
    private Resource reporteXls;
    private Resource reportePXls;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoMime;
    private double valorTotal;
    private double valorsaldo;
    private int anulado;
    private Codigos cod51;
    private Codigos cod71;
    private List<AuxiliarAsignacion> asignaciones;
    private List<AuxiliarAsignacion> cedulaContableDevengadoEjecutado;
    private double repartir511;
    private double repartir512;
    private double repartir711;
    private double repartir712;

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

    @PostConstruct
    private void activar() {
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReporteDetalleCompVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
        listadoDetallecomprom = new LinkedList<>();
    }

    // fin perfiles
    /**
     * Creates a new instance of DetallecompromisoEmpleadoBean
     */
    public ReporteDetalleCompBean() {
        listadoDetallecompromiso = new LazyDataModel<Detallecompromiso>() {

            @Override
            public List<Detallecompromiso> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscarAnt() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        int anio = c.get(Calendar.YEAR);
        proyectosBean.setAnio(anio);
        listadoDetallecomprom = new LinkedList<>();
        setListadoDetallecompromiso(new LazyDataModel<Detallecompromiso>() {
            @Override
            public List<Detallecompromiso> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                //                        + " and o.compromiso.impresion is not null and (o.compromiso.anulado=false or o.compromiso.anulado is null)";
                String where = "(o.fecha between :desde and :hasta  or o.compromiso.fechaanulado between :desde and :hasta) and o.compromiso.impresion is not null";
                Map parametros = new HashMap();
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                int total;
                if (proveedoresBean.getProveedor() != null) {
                    where += " and (o.compromiso.proveedor=:proveedor or o.proveedor=:proveedor1)";
                    parametros.put("proveedor", proveedoresBean.getProveedor());
                    parametros.put("proveedor1", proveedoresBean.getProveedor());
                }
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and o.compromiso.motivo like :motivo";
                    parametros.put("motivo", motivo + "%");
                }
                if (!((numero == null) || (numero <= 0))) {
                    where += " and o.compromiso.numerocomp=:numerocomp";
                    parametros.put("numerocomp", numero);
                }
                if (contrato != null) {
                    where += " and o.compromiso.contrato=:contrato";
                    parametros.put("contrato", contrato);
                }
                if (!((clasificador == null) || (clasificador.isEmpty()))) {
                    where += " and o.asignacion.clasificador.codigo like :clasificador";
                    parametros.put("clasificador", clasificador + "%");
                }
                if (proyectosBean.getProyectoSeleccionado() != null) {
                    where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
                    parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
                }
                if (anulado == 1) {
                    where += " and o.compromiso.anulado=true ";
                } else if (anulado == -1) {
                    where += " and o.compromiso.anulado=false ";
                }
                try {
                    parametros.put(";where", where);
                    parametros.put(";orden", "o.compromiso.id desc,o.fecha desc");
                    parametros.put("desde", desde);
                    parametros.put("hasta", hasta);
                    listadoDetallecomprom = ejbDetallecompromiso.encontarParametros(parametros);
                    total = ejbDetallecompromiso.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListadoDetallecompromiso().setRowCount(total);
                    return ejbDetallecompromiso.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        return null;
    }

    public String buscar() {

        ejbDetallecompromiso.calculaFechaEjecutado();
        Calendar c = Calendar.getInstance();

        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        int anio = c.get(Calendar.YEAR);

        proyectosBean.setAnio(anio);
        listadoDetallecomprom = new LinkedList<>();
        String where = "(o.fecha between :desde and :hasta  or o.compromiso.fechaanulado between :desde and :hasta) and o.compromiso.impresion is not null";
        Map parametros = new HashMap();

        if (proveedoresBean.getProveedor()
                != null) {
            where += " and (o.compromiso.proveedor=:proveedor or o.proveedor=:proveedor1)";
            parametros.put("proveedor", proveedoresBean.getProveedor());
            parametros.put("proveedor1", proveedoresBean.getProveedor());
        }
        if (!((motivo
                == null) || (motivo.isEmpty()))) {
            where += " and o.compromiso.motivo like :motivo";
            parametros.put("motivo", motivo + "%");
        }
        if (!((numero == null) || (numero
                <= 0))) {
            where += " and o.compromiso.numerocomp=:numerocomp";
            parametros.put("numerocomp", numero);
        }
        if (contrato
                != null) {
            where += " and o.compromiso.contrato=:contrato";
            parametros.put("contrato", contrato);
        }
        if (!((clasificador
                == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }

        if (proyectosBean.getProyectoSeleccionado()
                != null) {
            where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
        }
        if (anulado
                == 1) {
            where += " and o.compromiso.anulado=true ";
        } else if (anulado
                == -1) {
            where += " and o.compromiso.anulado=false ";
        }

        try {
            parametros.put(";where", where);
            parametros.put(";orden", "o.compromiso.id desc,o.fecha desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            listadoDetallecomprom = ejbDetallecompromiso.encontarParametros(parametros);

            cod51 = codigosBean.traerCodigo("AJUPAPPREJE", "51");
            if (cod51 == null) {
                MensajesErrores.advertencia("No existe partidas 51 de ajuste para el ejecutado");
                return null;
            }
            cod71 = codigosBean.traerCodigo("AJUPAPPREJE", "71");
            if (cod71 == null) {
                MensajesErrores.advertencia("No existe partidas 71 de ajuste para el ejecutado");
                return null;
            }
            listaCabeceras = new LinkedList<>();
            getValorEjecutadoContable();

            for (Detallecompromiso dc : listadoDetallecomprom) {
                getDevengadoEjecutado(dc);
            }

            comparar(listadoDetallecomprom);

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void comparar(List<Detallecompromiso> lista) {
        String[] campos = cod51.getDescripcion().split("#");
        String partida1 = campos[0];
        String partida2 = campos[1];
        String[] campos71 = cod71.getDescripcion().split("#");
        String partida3 = campos71[0];
        String partida4 = campos71[1];

        double valor511 = 0;
        double valor5112 = 0;
        double valor512 = 0;
        double valor5122 = 0;
        double valor711 = 0;
        double valor7112 = 0;
        double valor712 = 0;
        double valor7122 = 0;
        for (Detallecompromiso a : listadoDetallecomprom) {
            String partidaDC = a.getAsignacion().getClasificador().getCodigo();
            if (partida1.equals(partidaDC)) {
                valor511 += a.getValorDevengado() * repartir511;
                double cuadre6 = Math.round((a.getValorDevengado() * repartir511) * 100);
                double dividido6 = cuadre6 / 100;
                BigDecimal valortotal6 = new BigDecimal(dividido6).setScale(2, RoundingMode.HALF_UP);
                double totalT = (valortotal6.doubleValue());
                valor5112 += totalT;
            }
            if (partida2.equals(partidaDC)) {
                valor512 += a.getValorDevengado() * repartir512;
                double cuadre6 = Math.round((a.getValorDevengado() * repartir512) * 100);
                double dividido6 = cuadre6 / 100;
                BigDecimal valortotal6 = new BigDecimal(dividido6).setScale(2, RoundingMode.HALF_UP);
                double totalT = (valortotal6.doubleValue());
                valor5122 += totalT;
            }
            if (partida3.equals(partidaDC)) {
                valor711 += a.getValorDevengado() * repartir711;
                double cuadre6 = Math.round((a.getValorDevengado() * repartir711) * 100);
                double dividido6 = cuadre6 / 100;
                BigDecimal valortotal6 = new BigDecimal(dividido6).setScale(2, RoundingMode.HALF_UP);
                double totalT = (valortotal6.doubleValue());
                valor7112 += totalT;
            }
            if (partida4.equals(partidaDC)) {
                valor712 += a.getValorDevengado() * repartir712;
                double cuadre6 = Math.round((a.getValorDevengado() * repartir712) * 100);
                double dividido6 = cuadre6 / 100;
                BigDecimal valortotal6 = new BigDecimal(dividido6).setScale(2, RoundingMode.HALF_UP);
                double totalT = (valortotal6.doubleValue());
                valor7122 += totalT;
            }

        }
        cambiado(lista, partida1, valor511, valor5112);
        cambiado(lista, partida2, valor512, valor5122);
        cambiado(lista, partida3, valor711, valor7112);
        cambiado(lista, partida4, valor712, valor7122);

    }

    private void cambiado(List<Detallecompromiso> lista, String codigo, double valor1, double valor2) {

        double cuadre1 = Math.round((valor1) * 100);
        double dividido1 = cuadre1 / 100;
        BigDecimal valortotal1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
        valor1 = (valortotal1.doubleValue());

        double cuadre = Math.round((valor2) * 100);
        double dividido = cuadre / 100;
        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
        valor2 = (valortotal.doubleValue());

        double saldo = valor1 - valor2;

        double cuadre6 = Math.round((saldo) * 100);
        double dividido6 = cuadre6 / 100;
        BigDecimal valortotal6 = new BigDecimal(dividido6).setScale(2, RoundingMode.HALF_UP);
        saldo = (valortotal6.doubleValue());
        for (Detallecompromiso aa : lista) {
            if (aa.getAsignacion().getClasificador().getCodigo() != null) {
                if (!aa.getAsignacion().getClasificador().getCodigo().isEmpty()) {
                    if (aa.getAsignacion().getClasificador().getCodigo().equals(codigo)) {
                        aa.setValorEjecutado(aa.getValorEjecutado() + saldo);
                        return;
                    }
                }
            }
        }
    }

    public String imprimir() {
        listadoDetallecomprom = new LinkedList<>();
        Map parametros = new HashMap();
        String where = "(o.fecha between :desde and :hasta  or o.compromiso.fechaanulado between :desde and :hasta) and o.compromiso.impresion is not null";
        if (proveedoresBean.getProveedor() != null) {
            where += " and (o.compromiso.proveedor=:proveedor or o.proveedor=:proveedor1)";
            parametros.put("proveedor", proveedoresBean.getProveedor());
            parametros.put("proveedor1", proveedoresBean.getProveedor());
        }
        if (!((motivo == null) || (motivo.isEmpty()))) {
            where += " and o.compromiso.motivo like :motivo";
            parametros.put("motivo", motivo + "%");
        }
        if (!((numero == null) || (numero <= 0))) {
            where += " and o.compromiso.numerocomp=:numerocomp";
            parametros.put("numerocomp", numero);
        }
        if (contrato != null) {
            where += " and o.compromiso.contrato=:contrato";
            parametros.put("contrato", contrato);
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.asignacion.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and upper(o.asignacion.proyecto.codigo) like :proyecto";
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado().getCodigo().toUpperCase() + "%");
        }
        if (anulado == 1) {
            where += " and o.compromiso.anulado=true ";
        } else if (anulado == -1) {
            where += " and o.compromiso.anulado=false ";
        }
        try {
            parametros.put(";where", where);
            parametros.put(";orden", "o.compromiso.id desc,o.fecha desc");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            List<Detallecompromiso> dl = ejbDetallecompromiso.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE COMPROMISOS");
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            Calendar cal = Calendar.getInstance();
            reporte = new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/DetalleComp.jasper"),
                    dl, "Compromisos" + String.valueOf(cal.getTimeInMillis()) + ".pdf");
            formularioImprimir.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String hojaElectronica() {
        listadoDetallecomprom = new LinkedList<>();
        buscar();
        reportePXls = null;
        reporteXls = null;
        try {
            DocumentoXLS xls = new DocumentoXLS("Detalle compromiso");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Fecha"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "No. Compromiso"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Motivo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proveedor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Contrato"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Proyecto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Producto"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Origen"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Saldo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Devengado"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Ejecutado"));
            xls.agregarFila(campos, true);

            for (Detallecompromiso dc : listadoDetallecomprom) {
                DecimalFormat df = new DecimalFormat("###,###,##0.00");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                if (dc != null) {
                    if (dc.getCompromiso() != null) {
                        if (dc.getSaldo() == null) {
                            dc.setSaldo(BigDecimal.ZERO);
                        }

                        campos = new LinkedList<>();
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromiso().getFecha() != null ? (sdf.format(dc.getCompromiso().getFecha())) : ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromiso().getNumerocomp().toString()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromiso().getMotivo()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromiso().getProveedor() != null ? dc.getCompromiso().getProveedor().getEmpresa().toString()
                                : (dc.getCompromiso().getBeneficiario() != null ? dc.getCompromiso().getBeneficiario().toString() : "")));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCompromiso().getContrato() != null ? dc.getCompromiso().getContrato().toString() : ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getProyecto().getNombre()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getProyecto().toString()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getAsignacion().getClasificador().toString()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getOrigen()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValor())));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getSaldo())));

                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValorDevengado())));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValorEjecutado())));

                        xls.agregarFila(campos, false);
                    }
                }

            }

            setNombreArchivo("DetalleCompromiso.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            setReporteXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getTotalValor() {
        if (!(listadoDetallecomprom.isEmpty() || listadoDetallecomprom == null)) {
            valorTotal = 0;
            for (Detallecompromiso dc : listadoDetallecomprom) {
                if (dc.getCompromiso().getImpresion() != null) {
                    if (dc.getCompromiso().getAnulado() == null) {
                        dc.getCompromiso().setAnulado(Boolean.FALSE);
                    }
//                    if (!dc.getCompromiso().getAnulado()) {
                    if (dc.getValor() == null) {
                        dc.setValor(BigDecimal.ZERO);
                    }
//                        valorTotal += dc.getValor().doubleValue();
//                    }
                    valorTotal += dc.getValor().doubleValue();

                }
            }
        }
        return valorTotal;
    }

    public double getTotalSaldo() {
        if (!(listadoDetallecomprom.isEmpty() || listadoDetallecomprom == null)) {
            valorsaldo = 0;
            for (Detallecompromiso dc : listadoDetallecomprom) {
                if (dc.getCompromiso().getImpresion() != null) {
                    if (dc.getCompromiso().getAnulado() == null) {
                        dc.getCompromiso().setAnulado(Boolean.FALSE);
                    }
//                    if (!dc.getCompromiso().getAnulado()) {
                    if (dc.getSaldo() == null) {
                        dc.setSaldo(BigDecimal.ZERO);
                    }
//                        valorsaldo += dc.getSaldo().doubleValue();
//                    }
                    valorsaldo += dc.getSaldo().doubleValue();

                }
            }
        }
        return valorsaldo;
    }

    public String getFechaAnulado(Detallecompromiso dc) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dc.getCompromiso().getAnulado() == null) {
            return "NO";
        }
        if (dc.getCompromiso().getAnulado()) {
            return "SI - " + sdf.format(dc.getCompromiso().getFechaanulado());
        } else {
            return "NO";
        }
    }

    public void getDevengadoEjecutado(Detallecompromiso dc) {
        try {

            Calendar ca = Calendar.getInstance();
            ca.setTime(desde);
            int anio = ca.get(Calendar.YEAR);
            Calendar fechaParaInicio = Calendar.getInstance();
            fechaParaInicio.set(anio, 0, 1);
            desdeInicio = fechaParaInicio.getTime();

            //Valor devengado
            double valor = 0;
            Map parametros = new HashMap();
            parametros.put(";where", "o.iddetalle=:iddetalle and o.fecha<=:hasta");
            parametros.put("iddetalle", dc.getId());
            parametros.put("hasta", hasta);
            parametros.put(";campo", "o.valorDevengado");
            valor = ejbVistaDevengado.sumarCampo(parametros).doubleValue();
            dc.setValorDevengado(valor);
            //origen desde la vista
            String origen = "";
            parametros = new HashMap();
            parametros.put(";where", "o.iddetalle=:iddetalle");
            parametros.put("iddetalle", dc.getId());
            List<VistaDevengado> lista = ejbVistaDevengado.encontarParametros(parametros);
            if (lista.isEmpty()) {
                origen = "";
            } else {
                origen += lista.get(0).getOrigen();
            }
            dc.setOrigen(origen);
            if (dc.getValorDevengado() == 0) {
                dc.setValorEjecutado(0);
            } else {

                if (partida51o71(dc, valor)) {
                    return;
                }
                //valor ejecutado --busco en detallescompras con el detalle compromiso
                double valorEjecutado = 0;
                if (origen.equals("PAGOS LOTES")) {

                    parametros = new HashMap();
                    parametros.put(";where", "o.compromiso=:compromiso");
                    parametros.put("compromiso", dc.getCompromiso());
                    List<Detallecompromiso> listaDetalleCompromisos = ejbDetallecompromiso.encontarParametros(parametros);
                    if (!listaDetalleCompromisos.isEmpty()) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.detallecompromiso.compromiso=:compromiso");
                        parametros.put("compromiso", dc.getCompromiso());
                        List<Detallescompras> listaDC = ejbDetallescompras.encontarParametros(parametros);

                        List<Cabdocelect> listaNueva = new LinkedList<>();
                        for (Detallescompras dcompras : listaDC) {
                            yaesta(listaNueva, dcompras.getCabeceradoc());
                        }
                        valorEjecutado = pagoLotes(listaNueva);
                        dividir(valorEjecutado, dc, listaNueva);
                    }
                    return;
                }

                if (origen.equals("ROLES") || origen.equals("COMPROMISO DEVENGADO/EJECUTADO")) {
                    dc.setValorEjecutado(valor);
                    return;
                }
                if (origen.equals("CAJAS CHICAS")) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso=:detallecompromiso");
                    parametros.put("detallecompromiso", dc);
                    List<Detallesvales> listaDetallesvales = ejbDetallesvales.encontarParametros(parametros);
                    //busco los renglones de la caja 
                    if (!listaDetallesvales.isEmpty()) {
                        double valorRenglones = 0;
                        Cajas c = listaDetallesvales.get(0).getVale().getCaja();
                        if (c.getApertura() != null) {
                            if (c.getKardexbanco() != null) {
                                if (c.getKardexbanco().getFechamov() != null) {
                                    if (c.getKardexbanco().getFechamov().before(hasta)) {
                                        parametros = new HashMap();
                                        parametros.put(";where", "o.cabecera.idmodulo=:caja and o.cabecera.opcion='REPOSICION CAJA CHICA'"
                                                + " and o.cabecera.tipo.nombre = 'CAJAS CHICAS' and o.valor > 0");
//                                + " and o.cabecera.tipo.nombre = 'CAJAS CHICAS' and o.valor > 0 and o.fecha<=:hasta");
                                        parametros.put("caja", c.getId());
//                        parametros.put("hasta", hasta);
                                        parametros.put(";campo", "o.valor");
                                        valorRenglones = ejbRenglones.sumarCampo(parametros).doubleValue();
                                    }
                                }
                            } else {
                                if (c.getApertura().getLiquidado() == null) {
                                    c.getApertura().setLiquidado(Boolean.FALSE);
                                }
                                if (c.getApertura().getLiquidado()) {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.cabecera.idmodulo=:caja and o.cabecera.opcion='REPOSICION CAJA CHICA'"
                                            + " and o.cabecera.tipo.nombre = 'CAJAS CHICAS' and o.valor > 0");
                                    parametros.put("caja", c.getId());
                                    parametros.put(";campo", "o.valor");
                                    valorRenglones = ejbRenglones.sumarCampo(parametros).doubleValue();
                                }
                            }
                        } else {
                            parametros = new HashMap();
                            parametros.put(";where", "o.cabecera.idmodulo=:caja and o.cabecera.opcion='REPOSICION CAJA CHICA'"
                                    + " and o.cabecera.tipo.nombre = 'CAJAS CHICAS' and o.valor > 0 and o.fecha<=:hasta");
                            parametros.put("caja", c.getId());
                            parametros.put("hasta", hasta);
                            parametros.put(";campo", "o.valor");
                            valorRenglones = ejbRenglones.sumarCampo(parametros).doubleValue();
                        }
                        //divido el total para cada vale
                        dividirCaja(valorRenglones, c, dc);
                    }
                    return;
                }
                if (origen.equals("FONDOS ROTATIVOS")) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso=:detallecompromiso");
                    parametros.put("detallecompromiso", dc);
                    List<Detallesfondo> listaDetallesvales = ejbDetallesfondo.encontarParametros(parametros);
                    //busco los renglones de la caja 
                    if (!listaDetallesvales.isEmpty()) {
                        Fondos c = listaDetallesvales.get(0).getVale().getFondo();
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera.idmodulo=:fondo and o.cabecera.opcion='LIQUIDACION_FONDOS'"
                                + "  and o.valor > 0 and o.fecha<=:hasta");
                        parametros.put("fondo", c.getId());
                        parametros.put("hasta", hasta);
                        parametros.put(";campo", "o.valor");
                        double valorRenglones = ejbRenglones.sumarCampo(parametros).doubleValue();
                        //divido el total para cada vale
                        dividirFondo(valorRenglones, c, dc);
                    }
                    return;
                }

                if (origen.equals("VIATICOS")) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso=:detallecompromiso");
                    parametros.put("detallecompromiso", dc.getId());
                    List<Viaticosempleado> listaDetallesviaticos = ejbViaticosempleado.encontarParametros(parametros);
                    //busco los renglones de la caja 
                    if (!listaDetallesviaticos.isEmpty()) {
                        Viaticosempleado c = listaDetallesviaticos.get(0);
                        parametros = new HashMap();
                        parametros.put(";where", "o.cabecera.idmodulo=:ve and o.cabecera.opcion='LIQUIDACION_VIATICOS'"
                                + "  and o.valor > 0 and o.fecha<=:hasta");
                        parametros.put("ve", c.getId());
                        parametros.put("hasta", hasta);
                        parametros.put(";campo", "o.valor");
                        double valorRenglones = ejbRenglones.sumarCampo(parametros).doubleValue();
                        //divido el total para cada vale
                        dividirViatico(valorRenglones, c, dc);
                    }
                    return;
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double pagoLotes(List<Cabdocelect> listacab) {
        double valorEjecutado = 0;

        try {

            Calendar fechaRetencion = Calendar.getInstance();
            fechaRetencion.setTime(hasta);
            fechaRetencion.add(Calendar.MONTH, -1);
            Date hastaRetencion = fechaRetencion.getTime();
            //busco la obligacion
            double valorPagos = 0;
            double valorDescuentos = 0;
            double valorAnticipos = 0;
            double valorMulta = 0;
            double valorRetencion = 0;

            for (Cabdocelect dcompras : listacab) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.cabeccera=:cabeccera and o.cabeccera.anulado is null");
                parametros.put("cabeccera", dcompras);
                List<Documentoselectronicos> listade = ejbDocumentoselectronicos.encontarParametros(parametros);
                for (Documentoselectronicos de : listade) {
                    //busco en pagosvencimoento con la obligacion
                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.kardexbanco.fechamov between :desde and :hasta");
                    parametros.put("obligacion", de.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
                    valorPagos += ejbpPagosvencimientos.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.valoranticipo is not null"
                            + " and o.fechapago between :desde and :hasta");
                    parametros.put("obligacion", de.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valoranticipo");
                    valorAnticipos += ejbpPagosvencimientos.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.valormulta is not null and o.multa is not null and "
                            + "o.multa.contabilizado between :desde and :hasta");
                    parametros.put("obligacion", de.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valormulta");
                    valorMulta += ejbpPagosvencimientos.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.valormulta is not null and o.multa is null and "
                            + "o.fechapago between :desde and :hasta");
                    parametros.put("obligacion", de.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valormulta");
                    valorMulta += ejbpPagosvencimientos.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.obligacion.fechacontable between :desde and :hasta");
                    parametros.put("obligacion", de.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hasta);
                    parametros.put(";campo", "o.valor");
                    valorDescuentos += ejbDescuentoscompras.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.obligacion.fechar between :desde and :hasta");
                    parametros.put("obligacion", de.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hastaRetencion);
                    parametros.put(";campo", "(o.valor + o.valoriva)");
                    valorRetencion += ejbRetencionescompras.sumarCampo(parametros).doubleValue();
                }
            }
            valorEjecutado = valorPagos + valorAnticipos + valorDescuentos + valorMulta + valorRetencion;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return valorEjecutado;
    }

    public void yaesta(List<Cabdocelect> listaNueva, Cabdocelect ca) {
        for (Cabdocelect c : listaNueva) {
            if (ca.equals(c)) {
                return;
            }
        }
        listaNueva.add(ca);
        getListaCabeceras().add(ca);
    }

    public void dividir(double valor, Detallecompromiso dc, List<Cabdocelect> listacab) {
        try {
            if (dc.getValor() == dc.getSaldo()) {
                dc.setValorEjecutado(0);
            } else {
                Map parametros = new HashMap();
                parametros.put(";where", "o.detallecompromiso.compromiso=:compromiso and o.valor>0 "
                        + "and o.fechaEjecucion between :desde and :hasta");
                parametros.put("compromiso", dc.getCompromiso());
                parametros.put(";campo", "o.valor");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                double comp = ejbDetallescompras.sumarCampo(parametros).doubleValue();

                parametros = new HashMap();
                parametros.put(";where", "o.detallecompromiso=:detallecompromiso and o.valor>0 "
                        + "and o.fechaEjecucion between :desde and :hasta");
                parametros.put("detallecompromiso", dc);
                parametros.put(";campo", "o.valor");
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                double valordc = ejbDetallescompras.sumarCampo(parametros).doubleValue();

                Calendar fechaRetencion = Calendar.getInstance();
                fechaRetencion.setTime(hasta);
                fechaRetencion.add(Calendar.MONTH, -1);
                Date hastaRetencion = fechaRetencion.getTime();
                double valorRetencionPartida = 0;
                double valorRetencion = 0;
                for (Cabdocelect dcompras : listacab) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.cabeccera=:cabeccera and o.cabeccera.anulado is null");
                    parametros.put("cabeccera", dcompras);
                    List<Documentoselectronicos> listade = ejbDocumentoselectronicos.encontarParametros(parametros);
                    for (Documentoselectronicos de : listade) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.obligacion=:obligacion and o.obligacion.fechar >:hasta"
                                + " and o.partida=:partida");
                        parametros.put("obligacion", de.getObligacion());
                        parametros.put("hasta", hastaRetencion);
                        parametros.put("partida", dc.getAsignacion().getClasificador().getCodigo().substring(0, 2));
                        parametros.put(";campo", "(o.valor + o.valoriva)");
                        valorRetencionPartida += ejbRetencionescompras.sumarCampo(parametros).doubleValue();
                        parametros = new HashMap();
                        parametros.put(";where", "o.obligacion=:obligacion and o.obligacion.fechar >:hasta");
                        parametros.put("obligacion", de.getObligacion());
                        parametros.put("hasta", hastaRetencion);
                        parametros.put(";campo", "(o.valor + o.valoriva)");
                        valorRetencion += ejbRetencionescompras.sumarCampo(parametros).doubleValue();
                    }
                }
                boolean masDeUnaPartida = masPartidas(dc);
                if (valorRetencion != 0) {
                    if (masDeUnaPartida) {
                        valor = valor + valorRetencion;
                    }
                }

                if (comp != 0) {
                    double porcentaje = (valordc * 100) / comp;
                    double valorFinal = valor * (porcentaje / 100);
                    if (valorRetencionPartida != 0) {
                        if (masDeUnaPartida) {
                            valorFinal = valorFinal - valorRetencionPartida;
                        }
                    }
                    dc.setValorEjecutado(valorFinal);
                } else {

                    parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso.compromiso=:compromiso and o.valor>0");
                    parametros.put("compromiso", dc.getCompromiso());
                    parametros.put(";campo", "o.valor");
                    comp = ejbDetallescompras.sumarCampo(parametros).doubleValue();

                    parametros = new HashMap();
                    parametros.put(";where", "o.detallecompromiso=:detallecompromiso and o.valor>0");
                    parametros.put("detallecompromiso", dc);
                    parametros.put(";campo", "o.valor");
                    valordc = ejbDetallescompras.sumarCampo(parametros).doubleValue();

                    double porcentaje = (valordc * 100) / comp;
                    double valorFinal = valor * (porcentaje / 100);
                    dc.setValorEjecutado(valorFinal);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean masPartidas(Detallecompromiso dc) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", dc.getCompromiso());
            List<Detallecompromiso> lista = ejbDetallecompromiso.encontarParametros(parametros);
            for (Detallecompromiso d : lista) {
                if (!d.getAsignacion().getClasificador().getCodigo().substring(0, 2).equals(dc.getAsignacion().getClasificador().getCodigo().substring(0, 2))) {
                    return true;
                }
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void dividirCaja(double valor, Cajas c, Detallecompromiso dc) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.vale.caja=:caja");
            parametros.put("caja", c);
            parametros.put(";campo", "o.valor");
            double comp = ejbDetallesvales.sumarCampo(parametros).doubleValue();
            double porcentaje = (dc.getValor().doubleValue() * 100) / comp;
            double valorFinal = valor * (porcentaje / 100);
            dc.setValorEjecutado(valorFinal);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dividirFondo(double valor, Fondos c, Detallecompromiso dc) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.vale.fondo=:fondo");
            parametros.put("fondo", c);
            parametros.put(";campo", "o.valor");
            double comp = ejbDetallesfondo.sumarCampo(parametros).doubleValue();
            double porcentaje = (dc.getValor().doubleValue() * 100) / comp;
            double valorFinal = valor * (porcentaje / 100);
            dc.setValorEjecutado(valorFinal);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dividirViatico(double valor, Viaticosempleado c, Detallecompromiso dc) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:viaticoempleado");
            parametros.put("viaticoempleado", c);
            parametros.put(";campo", "o.valorvalidado");
            double comp = ejbDetalleviaticos.sumarCampo(parametros).doubleValue();
            double porcentaje = (dc.getValor().doubleValue() * 100) / comp;
            double valorFinal = valor * (porcentaje / 100);
            dc.setValorEjecutado(valorFinal);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean partida51o71(Detallecompromiso dc, double valor) {
        String[] campos = cod51.getDescripcion().split("#");
        String partida1 = campos[0];
        String partida2 = campos[1];
        String[] campos71 = cod71.getDescripcion().split("#");
        String partida3 = campos71[0];
        String partida4 = campos71[1];
        String clasificador = dc.getAsignacion().getClasificador().getCodigo();
        if (clasificador.substring(0, 2).equals("51")) {
            if (clasificador.equals(partida1)) {
                double cuadre4 = Math.round((valor * repartir511) * 100);
                double dividido4 = cuadre4 / 100;
                BigDecimal valortotal4 = new BigDecimal(dividido4).setScale(3, RoundingMode.HALF_UP);
                double valor1 = (valortotal4.doubleValue());
                dc.setValorEjecutado(valor1);
            } else {
                if (clasificador.equals(partida2)) {
                    double cuadre4 = Math.round((valor * repartir512) * 100);
                    double dividido4 = cuadre4 / 100;
                    BigDecimal valortotal4 = new BigDecimal(dividido4).setScale(3, RoundingMode.HALF_UP);
                    double valor1 = (valortotal4.doubleValue());
                    dc.setValorEjecutado(valor1);
                } else {

                    dc.setValorEjecutado(valor);
                }
            }
            return true;
        }
        if (clasificador.substring(0, 2).equals("71")) {
            if (clasificador.equals(partida3)) {
                double cuadre4 = Math.round((valor * repartir711) * 100);
                double dividido4 = cuadre4 / 100;
                BigDecimal valortotal4 = new BigDecimal(dividido4).setScale(3, RoundingMode.HALF_UP);
                double valor1 = (valortotal4.doubleValue());
                dc.setValorEjecutado(valor1);
            } else {
                if (clasificador.equals(partida4)) {
                    double cuadre4 = Math.round((valor * repartir712) * 100);
                    double dividido4 = cuadre4 / 100;
                    BigDecimal valortotal4 = new BigDecimal(dividido4).setScale(3, RoundingMode.HALF_UP);
                    double valor1 = (valortotal4.doubleValue());
                    dc.setValorEjecutado(valor1);
                } else {
                    dc.setValorEjecutado(valor);
                }
            }
            return true;
        }

        return false;
    }

    public void getValorEjecutadoContable() {
        try {
            asignaciones = new LinkedList<>();

            double valorContable51 = 0;
            double valorContable71 = 0;
            double valorPresupuesto51 = 0;
            double valorPresupuesto71 = 0;

            List<Codigos> lista = codigosBean.traerCodigoMaestro("AJUPAPPREJE");
            for (Codigos c1 : lista) {
                Codigos c = codigosBean.traerCodigo("DEVEJE", c1.getCodigo());
                llenar(c);
            }
            for (AuxiliarAsignacion aa : asignaciones) {
                if (aa.getCodigo().substring(0, 2).equals("51")) {
                    valorContable51 += aa.getValor();
                }
                if (aa.getCodigo().substring(0, 2).equals("71")) {
                    valorContable71 += aa.getValor();
                }
            }
            String cuenta51 = "21351";
            String cuenta71 = "21371";
            Map parametros = new HashMap();
            parametros.put(";where", "o.cuenta like :cuenta and o.valor>0 "
                    + "and o.fecha between :desde and :hasta");
            parametros.put(";campo", "(o.valor * o.signo)");
            parametros.put("cuenta", cuenta51 + "%");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            valorPresupuesto51 = ejbRenglones.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            parametros.put(";where", "o.cuenta like :cuenta and o.valor>0 "
                    + "and o.fecha between :desde and :hasta");
            parametros.put(";campo", "(o.valor * o.signo)");
            parametros.put("cuenta", cuenta71 + "%");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            valorPresupuesto71 = ejbRenglones.sumarCampo(parametros).doubleValue();

            double direferncia51 = 0;
            double direferncia71 = 0;
            double repartirPartida511 = 0;
            double repartirPartida512 = 0;
            double repartirPartida711 = 0;
            double repartirPartida712 = 0;

            direferncia51 = valorContable51 - valorPresupuesto51;
            direferncia71 = valorContable71 - valorPresupuesto71;

            repartir511 = 0;
            repartir512 = 0;
            repartir711 = 0;
            repartir712 = 0;

            cod51 = codigosBean.traerCodigo("AJUPAPPREJE", "51");
            String[] campos = cod51.getDescripcion().split("#");
            String partida1 = campos[0];
            String partida2 = campos[1];
            String[] camposP = cod51.getParametros().split("#");
            double porcen1 = Double.parseDouble(camposP[0]);
            double porcen2 = Double.parseDouble(camposP[1]);

            double cuadre1 = Math.round((direferncia51 * (porcen1 / 100)) * 100);
            double dividido1 = cuadre1 / 100;
            BigDecimal valortotal1 = new BigDecimal(dividido1).setScale(2, RoundingMode.HALF_UP);
            repartirPartida511 = (valortotal1.doubleValue());

            double cuadre2 = Math.round((direferncia51 * (porcen2 / 100)) * 100);
            double dividido2 = cuadre2 / 100;
            BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
            repartirPartida512 = (valortotal2.doubleValue());

            cod71 = codigosBean.traerCodigo("AJUPAPPREJE", "71");
            String[] campos71 = cod71.getDescripcion().split("#");
            String partida711 = campos71[0];
            String partida712 = campos71[1];
            String[] campos72 = cod71.getParametros().split("#");
            double porcen721 = Double.parseDouble(campos72[0]);
            double porcen722 = Double.parseDouble(campos72[1]);

            double cuadre3 = Math.round((direferncia71 * (porcen721 / 100)) * 100);
            double dividido3 = cuadre3 / 100;
            BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
            repartirPartida711 = (valortotal3.doubleValue());

            double cuadre4 = Math.round((direferncia71 * (porcen722 / 100)) * 100);
            double dividido4 = cuadre4 / 100;
            BigDecimal valortotal4 = new BigDecimal(dividido4).setScale(2, RoundingMode.HALF_UP);
            repartirPartida712 = (valortotal4.doubleValue());

            double valor = 0;
            double valorMenos = 0;
            for (AuxiliarAsignacion aa : asignaciones) {
                if (aa.getCodigo().substring(0, 2).equals("51")) {
                    if (aa.getCodigo().equals(partida1)) {
                        double cuadre = Math.round((aa.getEjecutado()) * 100);
                        double dividido = cuadre / 100;
                        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                        double listo = (valortotal.doubleValue());
                        valor = aa.getDevengado();
                        valorMenos = listo - repartirPartida511;
                        repartir511 = (valorMenos / valor);

                    } else {
                        if (aa.getCodigo().equals(partida2)) {
                            double cuadre = Math.round((aa.getEjecutado()) * 100);
                            double dividido = cuadre / 100;
                            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                            double listo = (valortotal.doubleValue());
                            valor = aa.getDevengado();
                            valorMenos = listo - repartirPartida512;
                            repartir512 = (valorMenos / valor);
                        }
                    }
                }
                if (aa.getCodigo().substring(0, 2).equals("71")) {
                    if (aa.getCodigo().equals(partida711)) {
                        double cuadre = Math.round((aa.getEjecutado()) * 100);
                        double dividido = cuadre / 100;
                        BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                        double listo = (valortotal.doubleValue());
                        valor = aa.getDevengado();
                        valorMenos = listo - repartirPartida711;
                        repartir711 = (valorMenos / valor);

                    } else {
                        if (aa.getCodigo().equals(partida712)) {
                            double cuadre = Math.round((aa.getEjecutado()) * 100);
                            double dividido = cuadre / 100;
                            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                            double listo = (valortotal.doubleValue());
                            valor = aa.getDevengado();
                            valorMenos = listo - repartirPartida712;
                            repartir712 = (valorMenos / valor);
                        }
                    }
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void llenar(Codigos c) {
        // divido la configuracion
        if (c.getDescripcion().indexOf("+") > 0) {
            String dividir = c.getDescripcion().replace("+", "#");
            String[] divicionConfig = dividir.split("#");
            for (String s : divicionConfig) {
                llenaLines(s);
            }
        } else {

            llenaLines(c.getDescripcion());
        }
        Collections.sort(asignaciones, new valorComparator());

    }

    public class valorComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            AuxiliarAsignacion r = (AuxiliarAsignacion) o1;
            AuxiliarAsignacion r1 = (AuxiliarAsignacion) o2;
            return r.getCodigo().
                    compareTo(r1.getCodigo());

        }
    }

    private void llenaLines(String codigo) {
        try {
            Map parametros = new HashMap();
//        ver signo
            boolean signo = false;
            int indice = codigo.indexOf("D");
            if (codigo.indexOf("D") >= 0) {
                // debitos
                signo = true;
                codigo = codigo.replace("D", "");
            } else if (codigo.indexOf("#") > 0) {
                // Para saldos Iniciales
//                tomo los asientos i en el preiodo desde hasta
                codigo = codigo.replace("I", "");
                String[] cuentaPartida = codigo.split("#");
                Calendar cDesde = Calendar.getInstance();
                Calendar cHasta = Calendar.getInstance();
                cDesde.setTime(desde);
                cHasta.setTime(hasta);
                int anioDesde = cDesde.get(Calendar.YEAR);
                int anioHasta = cHasta.get(Calendar.YEAR);
                cDesde.set(anioDesde, 0, 1);
                cHasta.set(anioHasta, 11, 31);

                //traer la sumatoria de esa cuenta: 11103 y partida:370102
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
//                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", cDesde.getTime());
                parametros.put("hasta", cHasta.getTime());
                parametros.put("cuenta", cuentaPartida[0] + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and  o.cabecera.tipo.equivalencia.codigo  in ('I') ");
                double valorDos = ejbRenglones.sumarCampo(parametros).doubleValue();

                // traer el devengado de la cedula
                llenarPartidaDevengado(cuentaPartida[2], Math.abs(valorDos), cuentaPartida[0]);

                //traer la sumatoria de esa cuenta: 11115 y partida:370102
                parametros = new HashMap();
                parametros.put(";campo", "o.valor*o.signo");
//                parametros.put(";orden", "o.cuenta");
//                parametros.put(";campo", "o.cuenta");
                parametros.put("desde", cDesde.getTime());
                parametros.put("hasta", cHasta.getTime());
                parametros.put("cuenta", cuentaPartida[1] + "%");
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and  o.cabecera.tipo.equivalencia.codigo  in ('I') ");
                double valorTres = ejbRenglones.sumarCampo(parametros).doubleValue();

                // traer el devengado de la cedula
                llenarPartidaDevengado(cuentaPartida[2], Math.abs(valorTres), cuentaPartida[1]);

                return;
                //
            } else {

                codigo = codigo.replace("C", "");
            }

// traer los renglones agrupados por cuentas
            parametros = new HashMap();
            parametros.put(";suma", "sum(o.valor*o.signo)");
            parametros.put(";orden", "o.cuenta");
            parametros.put(";campo", "o.cuenta");
            parametros.put("desde", desde);
            parametros.put("hasta", hasta);
            parametros.put("cuenta", codigo.trim() + "%");
            if (signo) {
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor>0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
            } else {
                parametros.put(";where", "o.fecha between :desde and :hasta and o.cuenta like :cuenta and o.valor<0 and o.cabecera.tipo.equivalencia.codigo not in ('I','C') and o.cabecera.tipo.codigo not in ('25')");
            }

            List<Object[]> resultado = ejbRenglones.sumar(parametros);
            for (Object[] o : resultado) {
                String cuenta = (String) o[0];
                BigDecimal valorBd = (BigDecimal) o[1];
                llenarPorPartida(cuenta, valorBd.abs().doubleValue());
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteCedullaContableBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int llenarPartidaDevengado(String partida, double valor, String cuenta) {
        for (AuxiliarAsignacion a : asignaciones) {
            if (a.getCodigo().equals(partida)) {
                a.setValor(a.getValor() + valor);
                return 1;
            }
        }
        Clasificadores cla = getPartidasBean().traerCodigo(partida);
        if (cla != null) {

            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setCodigo(partida);
            a.setNombre(cla.getNombre());
            a.setProyecto(cuenta);
            a.setValor(valor);
            double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
            double ejecutado = calculosBean.traerEjecutado(null, partida, desde, hasta, null);
            a.setDevengado(devengado);
            a.setEjecutado(ejecutado);
            a.setFuente("EGR");
            asignaciones.add(a);
        }
        return 0;
    }

    public void llenarPorPartida(String cuenta, double valor) {
        Cuentas c = getCuentasBean().traerCodigo(cuenta);
        String partida = c.getPresupuesto();
        if (partida == null) {
            partida = "";
        }
        for (AuxiliarAsignacion a : asignaciones) {
            if (a.getCodigo().equals(partida)) {
                a.setValor(a.getValor() + valor);
                return;
            }
        }

        Clasificadores cla = getPartidasBean().traerCodigo(partida);
        if (cla != null) {
            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setCodigo(partida);
            a.setNombre(cla.getNombre());
            a.setProyecto(cuenta);
            a.setValor(valor);
            double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
            double ejecutado = calculosBean.traerEjecutado(null, partida, desde, hasta, null);
            a.setDevengado(devengado);
            a.setEjecutado(ejecutado);
            a.setFuente("EGR");
            asignaciones.add(a);
        }
    }

    public void llenarPorPartidaDevengado(String cuenta, double valor) {
        Cuentas c = getCuentasBean().traerCodigo(cuenta);
        String partida = c.getPresupuesto();
        if (partida == null) {
            partida = "";
        }
//        if (partida.equals("580801")) {
//        if (cuenta.equals("6360101000")) {
//            int x = 0;
//        }
        for (AuxiliarAsignacion a : cedulaContableDevengadoEjecutado) {
            if (a.getCodigo().equals(partida)) {
                a.setValor(a.getValor() + valor);
                return;
            }
        }

        Clasificadores cla = getPartidasBean().traerCodigo(partida);
        if (cla != null) {

            AuxiliarAsignacion a = new AuxiliarAsignacion();
            a.setCodigo(partida);
            a.setNombre(cla.getNombre());
            a.setProyecto(cuenta);
            a.setValor(valor);
            if (cla.getIngreso()) {
                double devengado = calculosBean.traerDevengadoIngresosVista(null, partida, desde, hasta, null);
                double ejecutado = calculosBean.traerEjecutadoIngresosVista(null, partida, desde, hasta, null);
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("ING");
            } else {
                double devengado = calculosBean.traerDevengado(null, partida, desde, hasta, null);
                double ejecutado = calculosBean.traerEjecutado(null, partida, desde, hasta, null);
                a.setDevengado(devengado);
                a.setEjecutado(ejecutado);
                a.setFuente("EGR");
            }
            cedulaContableDevengadoEjecutado.add(a);
        }
    }

    public String hojaElectronicaPagosLote() {
        buscar();
        try {
            Calendar fechaRetencion = Calendar.getInstance();
            fechaRetencion.setTime(hasta);
            fechaRetencion.add(Calendar.MONTH, -1);
            Date hastaRetencion = fechaRetencion.getTime();
            reportePXls = null;
            reporteXls = null;
            DocumentoXLS xls = new DocumentoXLS("Detalle");
            List<AuxiliarReporte> campos = new LinkedList<>();
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Tipo"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Compromiso id "));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Compromiso num."));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Cabecera"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Partida"));
            campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Valor"));
            xls.agregarFila(campos, true);
            DecimalFormat df = new DecimalFormat("###,###,##0.00");
            for (Cabdocelect c : listaCabeceras) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.cabeceradoc=:cabeceradoc");
                parametros.put("cabeceradoc", c);
                List<Detallescompras> lista = ejbDetallescompras.encontarParametros(parametros);
                for (Detallescompras dc : lista) {
                    campos = new LinkedList<>();
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Compromiso"));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getDetallecompromiso().getCompromiso().getId() + ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getDetallecompromiso().getCompromiso().getNumerocomp() + ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getCabeceradoc().getId() + ""));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, dc.getDetallecompromiso().getAsignacion().getClasificador().getCodigo()));
                    campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(dc.getValor().doubleValue())));
                    xls.agregarFila(campos, false);
                }
                parametros = new HashMap();
                parametros.put(";where", "o.cabeccera=:cabeccera");
                parametros.put("cabeccera", c);
                List<Documentoselectronicos> listao = ejbDocumentoselectronicos.encontarParametros(parametros);
                for (Documentoselectronicos o : listao) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.obligacion=:obligacion and o.obligacion.fechar between :desde and :hasta");
                    parametros.put("obligacion", o.getObligacion());
                    parametros.put("desde", desdeInicio);
                    parametros.put("hasta", hastaRetencion);
                    List<Retencionescompras> listR = ejbRetencionescompras.encontarParametros(parametros);
                    for (Retencionescompras r : listR) {
                        campos = new LinkedList<>();
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, "Retencion"));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, ""));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, r.getPartida()));
                        campos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 9, false, df.format(r.getValor().doubleValue() + r.getValoriva().doubleValue())));
                        xls.agregarFila(campos, false);
                    }
                }
            }

            setNombreArchivo("Detalle.xls");
            setTipoArchivo("Exportar a XLS");
            setTipoMime("application/xls");
            setReportePXls(xls.traerRecurso());
            getFormularioReporte().insertar();
        } catch (IOException | DocumentException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ReporteDetalleCompBean.class
                    .getName()).log(Level.SEVERE, null, ex);
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
     * @return the listadoFamiliares
     */
    public LazyDataModel<Detallecompromiso> getListadoDetallecompromiso() {
        return listadoDetallecompromiso;
    }

    /**
     * @param listadoFamiliares the listadoFamiliares to set
     */
    public void setListadoFamiliares(LazyDataModel<Detallecompromiso> listadoFamiliares) {
        this.setListadoDetallecompromiso(listadoFamiliares);
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
     * @return the validado
     */
    public int getValidado() {
        return validado;
    }

    /**
     * @param validado the validado to set
     */
    public void setValidado(int validado) {
        this.validado = validado;
    }

    /**
     * @param listadoDetallecompromiso the listadoDetallecompromiso to set
     */
    public void setListadoDetallecompromiso(LazyDataModel<Detallecompromiso> listadoDetallecompromiso) {
        this.listadoDetallecompromiso = listadoDetallecompromiso;
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

//    /**
//     * @return the proyecto
//     */
//    public String getProyecto() {
//        return proyecto;
//    }
//
//    /**
//     * @param proyecto the proyecto to set
//     */
//    public void setProyecto(String proyecto) {
//        this.proyecto = proyecto;
//    }
    /**
     * @return the clasificador
     */
    public String getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(String clasificador) {
        this.clasificador = clasificador;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
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
     * @return the listaRascompras
     */
    public List<Rascompras> getListaRascompras() {
        return listaRascompras;
    }

    /**
     * @param listaRascompras the listaRascompras to set
     */
    public void setListaRascompras(List<Rascompras> listaRascompras) {
        this.listaRascompras = listaRascompras;
    }

    public String traerObligaciones(Detallecompromiso d) {
        double total = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.detallecompromiso=:detallecompromiso");
        parametros.put("detallecompromiso", d);
        detalle = d;
        try {
            listaRascompras = ejbRascompras.encontarParametros(parametros);
            Rascompras r1 = new Rascompras();
            r1.setReferencia("TOTAL");
            r1.setValor(BigDecimal.ZERO);
            r1.setValorimpuesto(BigDecimal.ZERO);
            r1.setValorret(BigDecimal.ZERO);
            r1.setVretimpuesto(BigDecimal.ZERO);
            for (Rascompras r : listaRascompras) {
                total += r.getObligacion().getApagar() == null ? 0 : r.getObligacion().getApagar().doubleValue();
                r1.setValor(new BigDecimal(r.getValor().doubleValue() + r1.getValor().doubleValue()));
                r1.setValorimpuesto(new BigDecimal(r.getValorimpuesto().doubleValue() + r1.getValorimpuesto().doubleValue()));
                r1.setValorret(new BigDecimal(r.getValorret().doubleValue() + r1.getValorret().doubleValue()));
                r1.setVretimpuesto(new BigDecimal(r.getVretimpuesto().doubleValue() + r1.getVretimpuesto().doubleValue()));
            }
            Obligaciones o = new Obligaciones();
            o.setApagar(new BigDecimal(total));
            o.setConcepto("TOTAL");
            r1.setObligacion(o);
            listaRascompras.add(r1);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(ReporteDetalleCompBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        formulario.insertar();
        return null;
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
     * @return the proyectosBean
     */
    public ProyectosBean getProyectosBean() {
        return proyectosBean;
    }

    /**
     * @param proyectosBean the proyectosBean to set
     */
    public void setProyectosBean(ProyectosBean proyectosBean) {
        this.proyectosBean = proyectosBean;
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
     * @return the detalle
     */
    public Detallecompromiso getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecompromiso detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the reporteXls
     */
    public Resource getReporteXls() {
        return reporteXls;
    }

    /**
     * @param reporteXls the reporteXls to set
     */
    public void setReporteXls(Resource reporteXls) {
        this.reporteXls = reporteXls;
    }

    /**
     * @return the nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * @param nombreArchivo the nombreArchivo to set
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the tipoMime
     */
    public String getTipoMime() {
        return tipoMime;
    }

    /**
     * @param tipoMime the tipoMime to set
     */
    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    /**
     * @return the formularioReporte
     */
    public Formulario getFormularioReporte() {
        return formularioReporte;
    }

    /**
     * @param formularioReporte the formularioReporte to set
     */
    public void setFormularioReporte(Formulario formularioReporte) {
        this.formularioReporte = formularioReporte;
    }

    /**
     * @return the listadoDetallecomprom
     */
    public List<Detallecompromiso> getListadoDetallecomprom() {
        return listadoDetallecomprom;
    }

    /**
     * @param listadoDetallecomprom the listadoDetallecomprom to set
     */
    public void setListadoDetallecomprom(List<Detallecompromiso> listadoDetallecomprom) {
        this.listadoDetallecomprom = listadoDetallecomprom;
    }

    /**
     * @return the valorTotal
     */
    public double getValorTotal() {
        return valorTotal;
    }

    /**
     * @param valorTotal the valorTotal to set
     */
    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * @return the valorsaldo
     */
    public double getValorsaldo() {
        return valorsaldo;
    }

    /**
     * @param valorsaldo the valorsaldo to set
     */
    public void setValorsaldo(double valorsaldo) {
        this.valorsaldo = valorsaldo;
    }

    /**
     * @return the anulado
     */
    public int getAnulado() {
        return anulado;
    }

    /**
     * @param anulado the anulado to set
     */
    public void setAnulado(int anulado) {
        this.anulado = anulado;
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
     * @return the cod51
     */
    public Codigos getCod51() {
        return cod51;
    }

    /**
     * @param cod51 the cod51 to set
     */
    public void setCod51(Codigos cod51) {
        this.cod51 = cod51;
    }

    /**
     * @return the cod71
     */
    public Codigos getCod71() {
        return cod71;
    }

    /**
     * @param cod71 the cod71 to set
     */
    public void setCod71(Codigos cod71) {
        this.cod71 = cod71;
    }

    /**
     * @return the asignaciones
     */
    public List<AuxiliarAsignacion> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<AuxiliarAsignacion> asignaciones) {
        this.asignaciones = asignaciones;
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
     * @return the partidasBean
     */
    public ClasificadorBean getPartidasBean() {
        return partidasBean;
    }

    /**
     * @param partidasBean the partidasBean to set
     */
    public void setPartidasBean(ClasificadorBean partidasBean) {
        this.partidasBean = partidasBean;
    }

    /**
     * @return the calculosBean
     */
    public CalulosPresupuestoBean getCalculosBean() {
        return calculosBean;
    }

    /**
     * @param calculosBean the calculosBean to set
     */
    public void setCalculosBean(CalulosPresupuestoBean calculosBean) {
        this.calculosBean = calculosBean;
    }

    /**
     * @return the repartir511
     */
    public double getRepartir511() {
        return repartir511;
    }

    /**
     * @param repartir511 the repartir511 to set
     */
    public void setRepartir511(double repartir511) {
        this.repartir511 = repartir511;
    }

    /**
     * @return the repartir512
     */
    public double getRepartir512() {
        return repartir512;
    }

    /**
     * @param repartir512 the repartir512 to set
     */
    public void setRepartir512(double repartir512) {
        this.repartir512 = repartir512;
    }

    /**
     * @return the repartir711
     */
    public double getRepartir711() {
        return repartir711;
    }

    /**
     * @param repartir711 the repartir711 to set
     */
    public void setRepartir711(double repartir711) {
        this.repartir711 = repartir711;
    }

    /**
     * @return the repartir712
     */
    public double getRepartir712() {
        return repartir712;
    }

    /**
     * @param repartir712 the repartir712 to set
     */
    public void setRepartir712(double repartir712) {
        this.repartir712 = repartir712;
    }

    /**
     * @return the listaCabeceras
     */
    public List<Cabdocelect> getListaCabeceras() {
        return listaCabeceras;
    }

    /**
     * @param listaCabeceras the listaCabeceras to set
     */
    public void setListaCabeceras(List<Cabdocelect> listaCabeceras) {
        this.listaCabeceras = listaCabeceras;
    }

    /**
     * @return the reportePXls
     */
    public Resource getReportePXls() {
        return reportePXls;
    }

    /**
     * @param reportePXls the reportePXls to set
     */
    public void setReportePXls(Resource reportePXls) {
        this.reportePXls = reportePXls;
    }

}
