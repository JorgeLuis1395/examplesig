/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CertificacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.DocumentospoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Certificacionespoa;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.entidades.sfccbdmq.Documentospoa;
import org.entidades.sfccbdmq.Partidaspoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingspoa;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStoreException;
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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
//import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.firma.FileUtils;
import org.utilitarios.firma.FirmadorPDF;
import org.wscliente.sfccbdmq.AuxiliarEntidad;

/**
 *
 * @author luis
 */
@ManagedBean(name = "certificacionesPoa")
@ViewScoped
public class CertificacionesPoaBean {

    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectoPoaBean;
    @ManagedProperty(value = "#{asignacionesPoa}")
    private AsignacionesPoaBean asignacionesPoaBean;
    @ManagedProperty(value = "#{calculosPoa}")
    private CalculosPoaBean calculosPacpoa;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoaBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{imagenesPoa}")
    private ImagenesPoaBean imagenesPoaBean;

    private Asignacionespoa asignacion;
    private Certificacionespoa certificacion;
    private List<Detallecertificacionespoa> detalles;
    private List<Asignacionespoa> listaAsignacion;
    private List<Detallecertificacionespoa> detallesb;
    private LazyDataModel<Certificacionespoa> certificaciones;
    private List<Certificacionespoa> certificacionesCompromiso;
    private Detallecertificacionespoa detalle;
//    private Certificaciones numero;
    private String numeroControl;
    private String numeroSolicitud;
    private String motivo;
    private String codigo;
    private String partida;
    private String direccion;
    private String estado;
    private String fuente;
    private String observacionAdjudicacion;
    private String observacionAprobacion;
    private String observacionLiquidacion;
    private String observacionDesaprobado;
    private boolean imprimir;
    private Integer numeroDocumento;
    private String impresas = "TRUE";
    private String liquidadas = "FALSE";
    private String anuladas = "FALSE";
    private Recurso reportepdf;
    private DocumentoPDF pdf;
    private int anio;
    private Date desde;
    private Date hasta;
    private Date vigente;
    private Proyectospoa proyectoSeleccionado;
    private Integer tipoBuscar = 2;
    private String codigoOrganigrama;
    private String clave;
    private String cerfificaQue;
    private boolean infima = false;
    private Formulario formulario = new Formulario();
    private Formulario formularioNumero = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioAsignacion = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioConfirmacion = new Formulario();
    private Formulario formularioFirma = new Formulario();
    private Formulario formularioDesaprobar = new Formulario();
    private Formulario formularioCertq = new Formulario();
    private File archivoFirmar;
    private Recurso recurso;
    private String nombreArchivo;
    private String tipoArchivo;

    @EJB
    private CertificacionespoaFacade ejbCertificacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private DetallecertificacionespoaFacade ejbDetalles;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private TrackingspoaFacade ejbTrackingspoa;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreo;
    @EJB
    private DocumentospoaFacade ejbDocumentospoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private CargosxorganigramaFacade ejbCargosxorganigrama;
    // Financieras
    @EJB
    private CertificacionesFacade ejbCertificacionesFinancieras;
    @EJB
    private DetallecertificacionesFacade ejbDetalleFinancieras;
    @EJB
    private ReformasFacade ejbReformasFinancieras;
    @EJB
    private AsignacionesFacade ejbAsignacionesFinanciera;
    @EJB
    private ProyectosFacade ejbProyectosFinanciera;
    @EJB
    private ClasificadoresFacade ejbPartidasFinanciera;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetComp;

    public CertificacionesPoaBean() {
        certificaciones = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        certificacion = new Certificacionespoa();
    }

    @PostConstruct
    private void activar() {
//        List<Codigos> configuracion = codigosBean.traerCodigoMaestro("CONF");
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        for (Codigos c : configuracion) {
//            try {
//                if (c.getCodigo().equals("PVGT")) {
//                    vigente = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PINI")) {
//                    desde = sdf.parse(c.getParametros());
//                }
//                if (c.getCodigo().equals("PFIN")) {
//                    hasta = sdf.parse(c.getParametros());
//                }
//            } catch (ParseException ex) {
//                Logger.getLogger(ReporteTotalReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        vigente = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);

//        direccion = null;
//        if (seguridadbean.getLogueado().getEmpleados() != null) {
//            if (seguridadbean.getLogueado().getEmpleados().getCargoactual() != null) {
//                Organigrama o = seguridadbean.traerDireccion(seguridadbean.getLogueado().getEmpleados().getCargoactual().getOrganigrama().getCodigo());
//                if (o != null) {
//                    // direccion
//                    direccion = o.getCodigo();
//                }
//            }
//        }
    }

    public String buscar() {
        certificaciones = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc, o.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.roles=false " + " and o.fecha between :desde and :hasta and o.rechazadopac=false ";
//                        + " and (o.impreso=true or o.anulado=true or o.rechazado=true or o.aprobardga=true)";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
//                parametros.put("anio", getAnio());
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", getMotivo().toUpperCase() + "%");
                }
                if (!((impresas == null) || (impresas.isEmpty()))) {
                    if (getImpresas().equalsIgnoreCase("true")) {
                        where += " and o.impreso=true";
                    } else {
                        where += " and o.impreso=false";
                    }
                }
                if (!(numeroSolicitud == null || numeroSolicitud.isEmpty())) {
                    int numero = Integer.parseInt(numeroSolicitud);
                    where += " and o.id=:id";
                    parametros.put("id", numero);
                }
                if (numeroControl != null && !numeroControl.isEmpty()) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", getNumeroControl());
                }
                if (direccion != null && !direccion.isEmpty()) {
                    where += " and o.direccion=:direccion";
                    parametros.put("direccion", getDireccion());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCertificacionespoa.contar(parametros);
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
                getCertificacionespoa().setRowCount(total);
                try {
                    return ejbCertificacionespoa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String buscarAprobadas() {
        certificaciones = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc, o.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio and  o.roles=false "
                        + " and o.fecha between :desde and :hasta and o.impreso=true";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("anio", getAnio());
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", getMotivo().toUpperCase() + "%");
                }

                if (!((impresas == null) || (impresas.isEmpty()))) {
                    if (getImpresas().equalsIgnoreCase("true")) {
                        where += " and o.apresupuestaria=true";
                    } else {
                        where += " and o.apresupuestaria=false";
                    }
                }
                if (!((liquidadas == null) || (liquidadas.isEmpty()))) {
                    if (liquidadas.equalsIgnoreCase("true")) {
                        where += " and o.liquidar=true";
                    } else {
                        where += " and o.liquidar=false";
                    }
                }
                if (numeroControl != null && !numeroControl.isEmpty()) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", getNumeroControl());
                }
                if (direccion != null && !direccion.isEmpty()) {
                    where += " and o.direccion=:direccion";
                    parametros.put("direccion", getDireccion());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCertificacionespoa.contar(parametros);
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
                getCertificacionespoa().setRowCount(total);
                try {
                    return ejbCertificacionespoa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String buscarSolicitudesLiquidacion() {
        certificaciones = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc, o.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio and  o.roles=false " + " and o.fecha between :desde and :hasta and o.apresupuestaria=true and o.anulado=false";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("anio", getAnio());
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", getMotivo().toUpperCase() + "%");
                }
//                if (!((impresas == null) || (impresas.isEmpty()))) {
//                    if (getImpresas().equalsIgnoreCase("true")) {
//                        where += " and o.apresupuestaria=true";
//                    } else {
//                        where += " and o.apresupuestaria=false";
//                    }
//                }
//                if (!((liquidadas == null) || (liquidadas.isEmpty()))) {
//                    if (liquidadas.equalsIgnoreCase("true")) {
//                        where += " and o.liquidar=true";
//                    } else {
//                        where += " and o.liquidar=false";
//                    }
//                }
                if (numeroControl != null && !numeroControl.isEmpty()) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", getNumeroControl());
                }
                if (direccion != null && !direccion.isEmpty()) {
                    where += " and o.direccion=:direccion";
                    parametros.put("direccion", getDireccion());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCertificacionespoa.contar(parametros);
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
                getCertificacionespoa().setRowCount(total);
                try {
                    return ejbCertificacionespoa.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String buscarLiquidacion() {
        certificaciones = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc, o.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio and  o.roles=false " + " and o.fecha between :desde and :hasta and o.apresupuestaria=true and o.liquidar=true";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("anio", getAnio());
                parametros.put("desde", getDesde());
                parametros.put("hasta", getHasta());
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", getMotivo().toUpperCase() + "%");
                }
//                if (!((impresas == null) || (impresas.isEmpty()))) {
//                    if (impresas.equalsIgnoreCase("true")) {
//                        where += " and o.apresupuestaria=true";
//                    } else {
//                        where += " and o.apresupuestaria=false";
//                    }
//                }
                if (!((liquidadas == null) || (liquidadas.isEmpty()))) {
                    if (liquidadas.equalsIgnoreCase("true")) {
                        where += " and o.anulado=true";
                    } else {
                        where += " and o.anulado=false";
                    }
                }
                if (numeroControl != null && !numeroControl.isEmpty()) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", getNumeroControl());
                }
                if (direccion != null && !direccion.isEmpty()) {
                    where += " and o.direccion=:direccion";
                    parametros.put("direccion", getDireccion());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCertificacionespoa.contar(parametros);
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
                getCertificacionespoa().setRowCount(total);
                try {
                    return ejbCertificacionespoa.encontarParametros(parametros);
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
        if (direccion == null || direccion.isEmpty()) {
            MensajesErrores.advertencia("Seleccione una dirección");
            return null;
        }
        try {
            Codigos cod = ejbCodigos.traerCodigo("NUM", "03");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para certificaciones");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
            Integer num = Integer.parseInt(cod.getParametros());
            Integer nume = num + 1;

            formularioClasificador.insertar();
            certificacion = new Certificacionespoa();
            certificacion.setDireccion(direccion);
            certificacion.setImpreso(Boolean.FALSE);
            certificacion.setImpresopac(Boolean.FALSE);
            certificacion.setGenerapac(Boolean.FALSE);
            certificacion.setAnulado(Boolean.FALSE);
            certificacion.setRoles(Boolean.FALSE);
            certificacion.setRechazado(Boolean.FALSE);
            certificacion.setApresupuestaria(Boolean.FALSE);
            certificacion.setLiquidar(Boolean.FALSE);
            certificacion.setRechazadopac(Boolean.FALSE);
            Calendar c = Calendar.getInstance();
//            certificacion.setAnio(c.get(Calendar.YEAR));
            certificacion.setAnio(anio);
            certificacion.setFecha(new Date());
            detalles = new LinkedList<>();
            detallesb = new LinkedList<>();
            imagenesPoaBean.setListaDocumentos(new LinkedList<>());
            certificacion.setNumero(nume + "");

        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
    }

    public String modificar(Certificacionespoa cer) {
        formularioClasificador.cancelar();
        this.certificacion = cer;
        if (cer.getCertificaque() != null) {
            cerfificaQue = cer.getCertificaque();
        }

        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();
        if (certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación aprobada no es posible modificar");
            return null;
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación anulada no es posible modificar");
            return null;
        }
        if (certificacion.getRechazado()) {
            MensajesErrores.advertencia("Certificación rechazadas no es posible modificar");
            return null;
        }
        try {
            Codigos cod = ejbCodigos.traerCodigo("NUM", "03");
            if (cod == null) {
                MensajesErrores.advertencia("No existe numeración para certificaciones");
                return null;
            }
            if (cod.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }
            Integer num = Integer.parseInt(cod.getParametros());
            Integer nume = num + 1;
            if (certificacion.getNumero() == null || certificacion.getNumero().isEmpty()) {
                certificacion.setNumero(nume + "");
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        detallesb = new LinkedList<>();
        formulario.editar();
        return null;
    }

    public String aprobacionPresupuestaria(Certificacionespoa certificacion) {
        observacionAprobacion = null;
        formularioClasificador.cancelar();
        this.certificacion = certificacion;
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String eliminar(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();

        if (certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación aprobada");
            return null;
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación anulada");
            return null;
        }
        if (certificacion.getRechazado()) {
            MensajesErrores.advertencia("Certificación rechazada");
            return null;
        }
        formularioClasificador.cancelar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String desaprobar(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.pacpoa=:pacpoa");
            parametros.put("pacpoa", certificacion.getId());
            List<Certificaciones> lista = ejbCertificacionesFinancieras.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("El Certificado ya se escuentra en Presupuesto");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioDesaprobar.insertar();
        return null;
    }

    public String anular(Certificacionespoa certif) {
        this.certificacion = certif;
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();
        if (certificacion.getAprobardga() == null) {
            certificacion.setAprobardga(Boolean.FALSE);
        }
//        if (!certificacion.getAprobardga()) {
//            MensajesErrores.advertencia("Certificacion no aprobada por DGA");
//            return null;
//        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación ya anulada");
            return null;
        }
        if (!certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación no aprobada");
            return null;
        }
        formularioClasificador.cancelar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String liquidar(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();
        if (!certificacion.getApresupuestaria()) {
            MensajesErrores.advertencia("Certificación no aprobada");
            return null;
        }
        formularioClasificador.cancelar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    public String insertar() {
        if ((detalles == null) || (detalles.isEmpty())) {
            MensajesErrores.advertencia("Es necesario al menos una cuenta");
            return null;
        }
        if ((certificacion.getMemo() == null) || (certificacion.getMemo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario N° de memorando");
            return null;
        }
        if ((certificacion.getNumero() == null) || (certificacion.getNumero().isEmpty())) {
            MensajesErrores.advertencia("Es necesario número de certificación");
            return null;
        }
        if ((certificacion.getMotivo() == null) || (certificacion.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario motivo de certificación");
            return null;
        }
        if ((certificacion.getFecha() == null) || (certificacion.getFecha().before(vigente))) {
            MensajesErrores.advertencia("Fecha de certificación debe ser mayor a periodo vigente");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.numero=:numero and o.anulado=false and o.anio=:anio");
            parametros.put("numero", certificacion.getNumero());
            parametros.put("anio", anio);
            List<Certificacionespoa> lista = ejbCertificacionespoa.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("El número se encuentra duplicado para el año: " + anio);
                return null;
            }
//            for (Detallecertificacionespoa d : detalles) {
//                String mensaje = "En las solicitudes de certificación con números de control: ";
//                String numeros = "";
//                parametros = new HashMap();
//                parametros.put(";where", " o.asignacion=:asignacion "
//                        + "and o.certificacion.impreso=false "
//                        + "and o.certificacion.anulado=false "
//                        + "and o.certificacion.rechazado=false");
//                parametros.put("asignacion", d.getAsignacion());
//                List<Detallecertificacionespoa> aux = ejbDetalles.encontarParametros(parametros);
//                for (Detallecertificacionespoa dc : aux) {
//                    numeros += dc.getCertificacion().getNumero() + ", ";
//                }
//                if (!numeros.isEmpty()) {
//                    mensaje += numeros;
//                    formularioConfirmacion.insertar();
//                    MensajesErrores.informacion(mensaje.substring(0, mensaje.length() - 2)
//                            + "; ya se encuentra presente el producto " + d.getAsignacion().getProyecto().getNombre().toUpperCase()
//                            + ".\n ¿Desea continuar de todas formas?");
//                } else {
            confirmarIngresar();
//                }
//            }
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");
        } catch (ConsultarException | InsertarException | IOException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String confirmarIngresar() throws InsertarException, IOException, GrabarException {

        try {
            certificacion.setEstado("Enviar");
            certificacion.setCertificaque(cerfificaQue);
            certificacion.setInfimanoplanificada(Boolean.FALSE);
            ejbCertificacionespoa.create(certificacion, seguridadbean.getLogueado().getUserid());
            if (certificacion.getNumero() == null) {
                Calendar cal = Calendar.getInstance();
                String aa = "" + cal.get(Calendar.YEAR);
                String ordinal = String.format("%03d", certificacion.getId());
//El numero es el nuevo id hasta igualar las certificaciones
                certificacion.setNumero(certificacion.getId().toString());
                certificacion.setNumeropoa(ordinal + "-CPOA-DPI-CBDMQ-" + aa);
                certificacion.setCertificaque(cerfificaQue);
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            for (Detallecertificacionespoa d : detalles) {
                d.setCertificacion(certificacion);
                ejbDetalles.create(d, seguridadbean.getLogueado().getUserid());
            }
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación realizada: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            try {
                Codigos cod = ejbCodigos.traerCodigo("NUM", "03");
                cod.setParametros(certificacion.getNumero());
                ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
            } catch (org.errores.sfccbdmq.ConsultarException | org.errores.sfccbdmq.GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ".");
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ".");

        } catch (InsertarException | GrabarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.insertarDocumentos("certificaciones");
        MensajesErrores.informacion("se creo certificación con número de control: " + certificacion.getNumero());
        formularioConfirmacion.cancelar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        try {
            if ((certificacion.getMemo() == null) || (certificacion.getMemo().isEmpty())) {
                MensajesErrores.advertencia("Es necesario N° de memorando");
                return null;
            }
            if ((certificacion.getNumero() == null) || (certificacion.getNumero().isEmpty())) {
                MensajesErrores.advertencia("Es necesario N° de certificación");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where ", "o.numero!=:numero and o.anulado=false");
            parametros.put("numero", certificacion.getNumero());
            List<Certificacionespoa> lista = ejbCertificacionespoa.encontarParametros(parametros);
//            if(!lista.isEmpty()){
//                MensajesErrores.advertencia("El número de certificación ya se encuentra");
//                return null;
//            }
            if ((certificacion.getMotivo() == null) || (certificacion.getMotivo().isEmpty())) {
                MensajesErrores.advertencia("Es necesario motivo de certificación");
                return null;
            }
            if ((certificacion.getFecha() == null) || (certificacion.getFecha().before(vigente))) {
                MensajesErrores.advertencia("Fecha de certificación debe ser mayor a periodo vigente");
                return null;
            }
            if ((detalles == null) || (detalles.isEmpty())) {
                MensajesErrores.advertencia("Es necesario al menos una cuenta");
                return null;
            }
            if (certificacion.getNumeropoa() != null) {
                Calendar cal = Calendar.getInstance();
                String aa = "" + cal.get(Calendar.YEAR);
//                String ordinal = String.format("%03d", certificacion.getid);
                Integer numero = Integer.parseInt(certificacion.getNumero());
                String ordinal = String.format("%03d", numero);
                certificacion.setNumeropoa(ordinal + "-CPOA-DPI-CBDMQ-" + aa);
            }

            parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
            parametros.put("asignacion", asignacion);
            double sumaC = ejbDetalles.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            double sumaR = ejbReformaspoa.sumarCampo(parametros).doubleValue();
//            if (detalle != null) {
//                double valor = sumaC + detalle.getValor().doubleValue();
//                if (valor > asignacion.getValor().doubleValue() + sumaR) {
//                    MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
//                    return null;
//                }
//            }
//            int total = ejbDetalles.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
//                return null;
//            }
            certificacion.setCertificaque(cerfificaQue);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            for (Detallecertificacionespoa d : detalles) {
                d.setCertificacion(certificacion);
                if (d.getId() == null) {
                    ejbDetalles.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalles.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Detallecertificacionespoa d : detallesb) {
                if (d.getId() != null) {
                    ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
                }
            }
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación aprobada: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");
            Codigos cod = ejbCodigos.traerCodigo("NUM", "03");
            cod.setParametros(certificacion.getNumero());
            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException | InsertarException | BorrarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String grabarPresupuesto() {
        try {
            if ((observacionAprobacion == null) || (observacionAprobacion.trim().isEmpty())) {
                MensajesErrores.advertencia("Observación necesaria");
                return null;
            }
            // pasar al lado financiero
            for (Detallecertificacionespoa d : detalles) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                parametros.put("anio", anio);
                List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                Proyectos p = null;
                for (Proyectos p1 : listap) {
                    p = p1;
                }
                if (p == null) {
                    MensajesErrores.advertencia("Proyecto PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getProyecto().toString());
                    return null;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                Clasificadores c = null;
                for (Clasificadores c1 : listac) {
                    c = c1;
                }
                if (c == null) {
                    MensajesErrores.advertencia("Partida PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getPartida().toString());
                    return null;
                }
                // Buscar la fuente
                Codigos co = null;
                List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                for (Codigos co1 : listaCodigos) {
                    co = co1;
                }
                if (co == null) {
                    MensajesErrores.advertencia("Fuente de Financiamiento PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getFuente().toString());
                    return null;
                }
                // Buscar Asignación
                parametros = new HashMap();
                parametros.put(";where", "o.clasificador=:clasificador "
                        + " and o.proyecto=:proyecto and o.fuente=:fuente");
                parametros.put("clasificador", c);
                parametros.put("proyecto", p);
                parametros.put("fuente", co);
                List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                Asignaciones af = null;
                for (Asignaciones af1 : listaa) {
                    af = af1;
                }
                if (af == null) {
                    if (d.getValor().doubleValue() >= 0) {
                        // creo la signación

                    } else {
                        MensajesErrores.advertencia("No existe dinero para ejecutar esta certificación en Sistema Financiero : "
                                + c.toString() + " - " + p.toString() + " - "
                                + d.getAsignacion().getFuente().toString());
                        return null;
                    }
                }
            }
            // Organigrama
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", certificacion.getDireccion());
            List<Organigrama> listaOrg = ejbOrganigrama.encontarParametros(parametros);
            Organigrama o = null;
            for (Organigrama o1 : listaOrg) {
                o = o1;
            }
            Certificaciones certFin = new Certificaciones();
            certFin.setAnio(certificacion.getAnio());
            certFin.setFecha(certificacion.getFecha());
            certFin.setMemo(certificacion.getMemo());
            certFin.setMotivo(certificacion.getMotivo());
            certFin.setRoles(certificacion.getRoles());
            certFin.setDireccion(o);
            certFin.setImpreso(false);
            ejbCertificacionesFinancieras.create(certFin, seguridadbean.getLogueado().getUserid());
            for (Detallecertificacionespoa d : detalles) {
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                parametros.put("anio", anio);
                List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                Proyectos p = null;
                for (Proyectos p1 : listap) {
                    p = p1;
                }
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                Clasificadores c = null;
                for (Clasificadores c1 : listac) {
                    c = c1;
                }

                // Buscar la fuente
                Codigos co = null;
                List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                for (Codigos co1 : listaCodigos) {
                    co = co1;
                }

                // Buscar Asignación
                parametros = new HashMap();
                parametros.put(";where", "o.clasificador=:clasificador "
                        + " and o.proyecto=:proyecto and o.fuente=:fuente");
                parametros.put("clasificador", c);
                parametros.put("proyecto", p);
                parametros.put("fuente", co);
                List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                Asignaciones af = null;
                for (Asignaciones af1 : listaa) {
                    af = af1;
                }
                if (af == null) {
                    // creo la signación
                    af = new Asignaciones();
                    af.setProyecto(p);
                    af.setClasificador(c);
                    af.setCerrado(true);
                    af.setFuente(co);
                    af.setValor(new BigDecimal(BigInteger.ZERO));
                    ejbAsignacionesFinanciera.create(af, seguridadbean.getLogueado().getUserid());
                }
                Detallecertificaciones d1 = new Detallecertificaciones();
                d1.setAsignacion(af);
                d1.setCertificacion(certFin);
                d1.setValor(d.getValor());
                ejbDetalleFinancieras.create(d1, seguridadbean.getLogueado().getUserid());
            }
            // fin financiero
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            if (certificacion.getApresupuestaria()) {
                Trackingspoa tracking = new Trackingspoa();
                tracking.setReformanombre(false);
                tracking.setFecha(new Date());
                tracking.setCertificacion(certificacion);
                tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
                tracking.setObservaciones(""
                        + "Certificación presupuestaria aprobada: \n"
                        + "Documento :" + certificacion.getNumero() + "\n"
                        + "Observaciones: " + observacionAprobacion);
                ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            }
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ".");
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ".");

        } catch (GrabarException | InsertarException | IOException | MessagingException | org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarAprobadas();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String borrar() {
        try {
            //buscar que no existan reformas,certificaciones para poder borrar
            if (certificacion.getAnulado()) {
                MensajesErrores.advertencia("Certificación ya anulada");
                return null;
            }
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
                }
            }
            if (detallesb != null) {
                for (Detallecertificacionespoa d : detallesb) {
                    if (d.getId() != null) {
                        ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            for (Documentospoa d : imagenesPoaBean.getListaDocumentos()) {
                if (d.getId() != null) {
                    ejbDocumentospoa.remove(d, seguridadbean.getLogueado().getUserid());
                }
            }
            ejbCertificacionespoa.remove(certificacion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String grabarDesaprobar() {
        try {
            certificacion.setImpreso(Boolean.FALSE);
            certificacion.setImpresopac(Boolean.FALSE);
            certificacion.setGenerapac(Boolean.FALSE);
            certificacion.setUsuariopac(null);
            certificacion.setFechaaprobacion(null);
            certificacion.setFechadocumento(null);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación desaprobada: \n"
                    + "Documento :" + certificacion.getNumero() + "\n"
                    + "Observaciones: " + observacionDesaprobado);
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioDesaprobar.cancelar();
        return null;
    }

    public String grabarAnular() {
        try {
            if ((observacionLiquidacion == null) || (observacionLiquidacion.trim().isEmpty())) {
                MensajesErrores.advertencia("Observación necesaria");
                return null;
            }
            //Financiero
            Certificaciones c = null;
            Map parametros = new HashMap();
            parametros.put(";where", "o.pacpoa=:pacpoa");
            parametros.put("pacpoa", certificacion.getId());
            List<Certificaciones> listaCertificacionF = ejbCertificacionesFinancieras.encontarParametros(parametros);
            if (!listaCertificacionF.isEmpty()) {
                c = listaCertificacionF.get(0);
            }
            if (c != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.certificacion=:certificacion");
                parametros.put("certificacion", c);
                List<Compromisos> listaComp = ejbCompromisos.encontarParametros(parametros);
                if (!listaComp.isEmpty()) {
                    MensajesErrores.advertencia("La certtificación ya tiene Compromiso en Presupuesto");
                    return null;
                } else {
                    c.setAnulado(Boolean.TRUE);
                    c.setFechaanulado(certificacion.getFecha());
                    c.setObservacionanulado(observacionLiquidacion);
                    c.setUsuarioanulado(seguridadbean.getLogueado().getUserid());
                    ejbCertificacionesFinancieras.edit(c, seguridadbean.getLogueado().getUserid());
                    parametros = new HashMap();
                    parametros.put(";where", "o.certificacion=:certificacion");
                    parametros.put("certificacion", c);
                    List<Detallecertificaciones> listaDetalleFinanciero = ejbDetalleFinancieras.encontarParametros(parametros);
                    if (!listaDetalleFinanciero.isEmpty()) {
                        for (Detallecertificaciones dcf : listaDetalleFinanciero) {
                            Detallecertificaciones dcfNuevo = new Detallecertificaciones();
                            Double valor = -dcf.getValor().doubleValue();
                            dcfNuevo.setAsignacion(dcf.getAsignacion());
                            dcfNuevo.setValor(new BigDecimal(valor));
                            dcfNuevo.setCertificacion(dcf.getCertificacion());
                            dcfNuevo.setFecha(dcf.getFecha());
                            ejbDetalleFinancieras.create(dcfNuevo, seguridadbean.getLogueado().getUserid());
                        }
                    }
                    //Fin Financiero 
                    certificacion.setAnulado(true);
                    ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
                    parametros = new HashMap();
                    parametros.put(";where", "o.certificacion=:certificacion");
                    parametros.put("certificacion", certificacion);
                    List<Detallecertificacionespoa> listaDetallePoa = ejbDetalles.encontarParametros(parametros);
                    if (!listaDetallePoa.isEmpty()) {
                        for (Detallecertificacionespoa dcp : listaDetallePoa) {
                            Detallecertificacionespoa dcpNuevo = new Detallecertificacionespoa();
                            Double valor = -dcp.getValor().doubleValue();
                            dcpNuevo.setAsignacion(dcp.getAsignacion());
                            dcpNuevo.setValor(new BigDecimal(valor));
                            dcpNuevo.setCertificacion(dcp.getCertificacion());
                            dcpNuevo.setFecha(dcp.getFecha());
                            ejbDetalles.create(dcpNuevo, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            } else {
                certificacion.setAnulado(true);
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
                parametros = new HashMap();
                parametros.put(";where", "o.certificacion=:certificacion");
                parametros.put("certificacion", certificacion);
                List<Detallecertificacionespoa> listaDetallePoa = ejbDetalles.encontarParametros(parametros);
                if (!listaDetallePoa.isEmpty()) {
                    for (Detallecertificacionespoa dcp : listaDetallePoa) {
                        Detallecertificacionespoa dcpNuevo = new Detallecertificacionespoa();
                        Double valor = -dcp.getValor().doubleValue();
                        dcpNuevo.setAsignacion(dcp.getAsignacion());
                        dcpNuevo.setValor(new BigDecimal(valor));
                        dcpNuevo.setCertificacion(dcp.getCertificacion());
                        dcpNuevo.setFecha(dcp.getFecha());
                        ejbDetalles.create(dcpNuevo, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación anulada: \n"
                    + "Documento :" + certificacion.getNumero() + "\n"
                    + "Observaciones: " + observacionLiquidacion);
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ", ha sido anulada.");
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ", ha sido anulada.");

        } catch (GrabarException | InsertarException | MessagingException | UnsupportedEncodingException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscarAprobadas();
        return null;
    }

    public String grabarSolicitudLiquidacion() {
        try {
            if ((observacionLiquidacion == null) || (observacionLiquidacion.trim().isEmpty())) {
                MensajesErrores.advertencia("Observación necesaria");
                return null;
            }
            certificacion.setLiquidar(Boolean.TRUE);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Solicitud de Liquidación de Certificación \n"
                    + "Documento :" + certificacion.getNumero() + "\n"
                    + "Observaciones: " + observacionLiquidacion);
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Liquidación de Certificación", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ".");
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Liquidación de Certificación", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ".");

        } catch (GrabarException | InsertarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarAprobadas();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String grabarLiquidacion() {
        try {
            if ((observacionLiquidacion == null) || (observacionLiquidacion.trim().isEmpty())) {
                MensajesErrores.advertencia("Observación necesaria");
                return null;
            }
            certificacion.setAnulado(Boolean.TRUE);
            certificacion.setLiquidar(Boolean.TRUE);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Liquidación de Certificación \n"
                    + "Documento :" + certificacion.getNumero() + "\n"
                    + "Observaciones: " + observacionLiquidacion);
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Liquidación de Certificación", "Liquidación de Certificación POA Nro: " + certificacion.getMemo());
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Liquidación de Certificación", "Liquidación de Certificación POA Nro: " + certificacion.getMemo());

        } catch (GrabarException | InsertarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscarAprobadas();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String nuevoDetalle() {

        if (certificacion.getDireccion() == null || certificacion.getDireccion().isEmpty()) {
            MensajesErrores.advertencia("Seleccione una dirección");
            return null;
        }
        direccion = certificacion.getDireccion();
        formularioClasificador.insertar();
        formularioDetalle.insertar();
        detalle = new Detallecertificacionespoa();
        partida = null;
        proyectoPoaBean.setAnio(anio);
        proyectoPoaBean.setProyectoSeleccionado(null);
        proyectoPoaBean.setCodigo(null);
        asignacion = null;
        codigo = null;
        return null;
    }

    public String modificaDetalle() {
        int fila = formularioDetalle.getFila().getRowIndex();
        this.detalle = detalles.get(fila);
        formularioDetalle.setIndiceFila(fila);
        formularioClasificador.cancelar();
        formularioDetalle.editar();
        asignacion = detalle.getAsignacion();
        codigo = asignacion.getPartida().getCodigo();
        return null;
    }

    public String borraDetalle() {
        int fila = formularioDetalle.getFila().getRowIndex();
        this.detalle = detalles.get(fila);
        formularioDetalle.setIndiceFila(fila);
        formularioClasificador.cancelar();
        formularioDetalle.eliminar();
        asignacion = detalle.getAsignacion();
        codigo = asignacion.getPartida().getCodigo();
        return null;
    }

    private boolean estaAsignacion(Asignacionespoa a) {
        for (Detallecertificacionespoa d : detalles) {
            if (d.getAsignacion().equals(a)) {
                MensajesErrores.advertencia("Partida ya se encuentra en certificación");
                return true;
            }
        }
        return false;
    }

    public String insertarDetalle() {
        if (asignacion == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }
        if (estaAsignacion(asignacion)) {
            MensajesErrores.advertencia("Partida ya se encuentra en certificación");
            return null;
        }
        detalle.setAsignacion(asignacion);
        detalle.setFecha(certificacion.getFecha());
        if (detalle.getValor() == null) {
            MensajesErrores.advertencia("Valor debe ser distinto a cero");
            return null;
        }
        if (detalle.getValor().doubleValue() <= 0) {
            MensajesErrores.advertencia("Valor debe ser distinto a cero");
            return null;
        }
        // debe certificar la suma de lo asignado + lo reformado + certificaciones <= certificacion actual
        double asignado = asignacion.getValor().doubleValue() * 100;
        double reformas = calculaTotalReformas(asignacion) * 100;
        double tCertificacionespoa = calculaTotalCertificacionespoa(asignacion) * 100;
        double suma = asignado + reformas - tCertificacionespoa;
        double valor = detalle.getValor().doubleValue() * 100;
        double compara = Math.round(suma - valor);
        if (compara < 0) {
            MensajesErrores.advertencia("Certificación exede el saldo de la partida");
            return null;
        }

        //Financiero*******************************************************************************
        try {
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    //Proyecto Financiero
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap;
                    listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    if (p == null) {
                        MensajesErrores.advertencia("Proyecto PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getProyecto().toString());
                        return null;
                    }
                    //Clasificador Financiero
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", d.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }
                    if (c == null) {
                        MensajesErrores.advertencia("Partida PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getPartida().toString());
                        return null;
                    }
                    // Buscar la fuente
                    Codigos co = null;
                    List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                    for (Codigos co1 : listaCodigos) {
                        if (co1.getCodigo().equals(d.getAsignacion().getFuente())) {
                            co = co1;
                        }
                    }
                    if (co == null) {
                        MensajesErrores.advertencia("Fuente de Financiamiento PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getFuente().toString());
                        return null;
                    }
                    // Buscar Asignación
                    parametros = new HashMap();
                    parametros.put(";where", "o.clasificador=:clasificador "
                            + " and o.proyecto=:proyecto and o.fuente=:fuente");
                    parametros.put("clasificador", c);
                    parametros.put("proyecto", p);
                    parametros.put("fuente", co);
                    List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                    Asignaciones af = null;
                    for (Asignaciones af1 : listaa) {
                        af = af1;
                    }
                    if (af == null) {
                        if (d.getValor().doubleValue() >= 0) {
                            // creo la signación
                        } else {
                            MensajesErrores.advertencia("No existe dinero para ejecutar esta certificación en Sistema Financiero : "
                                    + c.toString() + " - " + p.toString() + " - "
                                    + d.getAsignacion().getFuente().toString());
                            return null;
                        }
                    }
                }
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Fin Financiero*******************************************************************************

        detalles.add(detalle);
        String tipo = detalle.getAsignacion().getProyecto().getTipocompra();
        if (cerfificaQue == null || cerfificaQue.isEmpty()) {
            cerfificaQue = tipo != null ? (tipo + " - " + detalle.getAsignacion().getProyecto().getNombre()) : detalle.getAsignacion().getProyecto().getNombre();
        } else {
            cerfificaQue = cerfificaQue + "\n" + (tipo != null ? (tipo + " - " + detalle.getAsignacion().getProyecto().getNombre()) : detalle.getAsignacion().getProyecto().getNombre());
        }

        formularioClasificador.cancelar();
        formularioDetalle.cancelar();
        return null;
    }

    public String grabarDetalle() {

        if (detalle.getValor().doubleValue() == 0) {
            MensajesErrores.advertencia("Valor debe ser distinto a cero");
            return null;
        }
        double asignado = asignacion.getValor().doubleValue() * 100;
        double reformas = calculaTotalReformas(asignacion) * 100;
        double tCertificacionespoa = calculaTotalCertificacionespoa(asignacion) * 100;
        double suma = asignado + reformas - tCertificacionespoa;
        double valor = detalle.getValor().doubleValue() * 100;
        double compara = Math.round(suma - valor);
        if (compara < 0) {
            MensajesErrores.advertencia("Certificación exede el saldo de la partida");
            return null;
        }
        detalles.set(formularioDetalle.getIndiceFila(), detalle);
        String tipo = detalle.getAsignacion().getProyecto().getTipocompra();
        if (cerfificaQue == null || cerfificaQue.isEmpty()) {
            cerfificaQue = tipo != null ? (tipo + " - " + detalle.getAsignacion().getProyecto().getNombre()) : detalle.getAsignacion().getProyecto().getNombre();
        } else {
            cerfificaQue = cerfificaQue + "\n" + (tipo != null ? (tipo + " - " + detalle.getAsignacion().getProyecto().getNombre()) : detalle.getAsignacion().getProyecto().getNombre());
        }
        formularioClasificador.cancelar();
        formularioDetalle.cancelar();
        return null;
    }

    public String eliminarDetalle() {
        detallesb.add(detalle);
        detalles.remove(formularioDetalle.getIndiceFila());
        formularioClasificador.cancelar();
        formularioDetalle.cancelar();
        return null;
    }

    private double calculaTotalCertificacionespoa(Asignacionespoa a) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.adjudicado");
            parametros.put("asignacion", a);
            if (detalle.getId() != null) {
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false and o.id!=:id");
                parametros.put("id", detalle.getId());
            } else {
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
            }
            double totalAdjudicado = ejbDetalles.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("asignacion", a);
            if (detalle.getId() != null) {
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false and o.id!=:id and o.adjudicado is null");
                parametros.put("id", detalle.getId());
            } else {
                parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
            }
            double totalCertificado = ejbDetalles.sumarCampo(parametros).doubleValue();

            return totalCertificado + totalAdjudicado;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getValorCertificacion() {
        Certificacionespoa c = (Certificacionespoa) certificaciones.getRowData();
        return traerValorCertificacion(c);
    }

    public double traerValorCertificacion(Certificacionespoa c) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.adjudicado");
            parametros.put("certificacion", c);
            parametros.put(";where", "o.certificacion=:certificacion");
            double valorAdjudicado = ejbDetalles.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("certificacion", c);
            parametros.put(";where", "o.certificacion=:certificacion and o.adjudicado is null");
            double valorCertificado = ejbDetalles.sumarCampo(parametros).doubleValue();

            return valorAdjudicado + valorCertificado;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean traerEstado(Certificacionespoa c) {
        //Si no hay en ao ede la certificacion no esta aprobada por el director
        boolean retorno = false;
        if (c.getNumero() == null) {
            if (c.getAnio() == null) {
//                retorno = "INGRESADO";
                retorno = false;
            } else {
//                retorno = "APROBADO";
                retorno = true;
            }
        } else {
            retorno = true;
        }
        return retorno;
    }

    public double getSaldoCertificacion() {
        Certificacionespoa c = (Certificacionespoa) certificaciones.getRowData();
        return traerSaldoCert(c);
    }

    public double traerSaldoCert(Certificacionespoa c) {
        try {
            Map parametros = new HashMap();
            if (c.getFinaciero() != null) {
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.compromiso.certificacion.id=:certificacion");
                parametros.put("certificacion", c.getFinaciero());
                return ejbDetComp.sumarCampo(parametros).doubleValue();
            } else {
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.compromiso.certificacion.pacpoa=:pacpoa");
                parametros.put("pacpoa", c.getId());
                return ejbDetComp.sumarCampo(parametros).doubleValue();
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoCertificacionFinal() {
        Certificacionespoa c = (Certificacionespoa) certificaciones.getRowData();
        return traerSaldoCert(c);
    }

    public double getSaldo(Certificacionespoa c) {
        double saldo = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", c);
            double valorCert = ejbDetalles.sumarCampo(parametros).doubleValue();
            double valorComp = 0;
            if (c.getFinaciero() != null) {
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.compromiso.certificacion.id=:certificacion");
                parametros.put("certificacion", c.getFinaciero());
                valorComp = ejbDetComp.sumarCampo(parametros).doubleValue();
            } else {
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.compromiso.certificacion.pacpoa=:pacpoa");
                parametros.put("pacpoa", c.getId());
                valorComp = ejbDetComp.sumarCampo(parametros).doubleValue();
            }

            saldo = valorCert - valorComp;
        } catch ( ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saldo;
    }

    public double getValorCertificacionespoa() {
        return calculaTotalCertificacionespoa(asignacion);
    }

    public double getValorReformas() {
        return calculaTotalReformas(asignacion);
    }

    public double calculaTotalReformas(Asignacionespoa a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
        try {
            return ejbReformaspoa.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Detallecertificacionespoa traer(Integer id) throws ConsultarException {
        return ejbDetalles.find(id);
    }

    public Certificacionespoa traerC(Integer id) throws ConsultarException {
        return ejbCertificacionespoa.find(id);
    }

    public String getTotalCertificacion() {
        double retorno = 0;
        if (detalles == null) {
            return "0.00";
        }
        for (Detallecertificacionespoa d : detalles) {
            retorno += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        //clasificador = null;
        asignacion = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            asignacion = null;
        }
        listaAsignacion = new LinkedList<>();
        //traer la consulta 
        Map parametros = new HashMap();
        String where = "o.proyecto.anio=:anio " + " and o.partida.ingreso=false and upper(o.proyecto.direccion)=:direccion and o.activo=true and o.proyecto.activo=true";
//        parametros.put("anio", proyectoPoaBean.getAnio());
        parametros.put("anio", anio);
        parametros.put("direccion", direccion);

        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.proyecto.codigo) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.proyecto.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.proyecto.nombre) like :codigo";
            parametros.put("codigo", newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.proyecto.nombre");
        }
        parametros.put(";where", where);
        listaAsignacion = null;
        //Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        try {
            listaAsignacion = ejbAsignacionespoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        asignacion = null;
        if (listaAsignacion == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Asignacionespoa a : listaAsignacion) {
            String aComparar = tipoBuscar == 1 ? a.toStringCodigo() : a.toStringNombre();
            if (aComparar.compareToIgnoreCase(newWord) == 0) {
                asignacion = a;
            }
        }
    }

    public String getBlancos() {
        return "<br><br> <br> <br> <br>";
    }

    public List<Partidaspoa> getListaPartidas() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.proyecto.anio=:anio and o.partida.ingreso = false");
        parametros.put("anio", anio);
        parametros.put(";orden", "o.partida.codigo");
        try {
            List<Asignacionespoa> lAsignacionespoa = ejbAsignacionespoa.encontarParametros(parametros);
            List<Partidaspoa> listaPartidas = new LinkedList<>();
            for (Asignacionespoa x : lAsignacionespoa) {
                double reformas = calculaTotalReformas(x);
                // ver la suma de certificaciones eso resta el valor actual 
                double valorCert = calculaTotalCertificacionespoa(x);
                double saldo = x.getValor().doubleValue() + reformas - valorCert;
                if (saldo > 0) {
                    // ver si el clasificador esta ya en la lista
                    estaYaPartida(x.getPartida(), listaPartidas);
                }
            }
            return listaPartidas;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Certificacionespoa.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboFuentePartida() {
        if (partida == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.partida.codigo=:partida and o.proyecto.anio=:anio");
        parametros.put("partida", partida);
        parametros.put("anio", anio);
        parametros.put(";orden", "o.fuente");
        try {
            List<Asignacionespoa> lAsignacionespoa = ejbAsignacionespoa.encontarParametros(parametros);
            List<Codigos> listaFuentes = new LinkedList<>();
            for (Asignacionespoa x : lAsignacionespoa) {
                double reformas = calculosPacpoa.traerReformas(x, desde, hasta, null, x.getFuente());
                double valorCert = calculosPacpoa.traerCertificaciones(x, desde, hasta, detalle, x.getFuente());
                double saldo = x.getValor().doubleValue() + reformas - valorCert;
                if (saldo > 0) {
                    // ver si el clasificador esta ya en la lista
                    esta(codigosBean.traerCodigo("FUENFIN", x.getFuente()), listaFuentes);
                }
            }
            return consultasPoaBean.getSelectItemsCodigos(listaFuentes, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(Certificacionespoa.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void esta(Codigos f, List<Codigos> listaFuentes) {
        for (Codigos f1 : listaFuentes) {
            if (f.getCodigo().equals(f1.getCodigo())) {
                return;
            }
        }
        listaFuentes.add(f);
    }

    private void estaYaPartida(Partidaspoa c, List<Partidaspoa> listaClasificadores) {
        for (Partidaspoa c1 : listaClasificadores) {
            if (c1.equals(c)) {
                return;
            }
        }
        listaClasificadores.add(c);
    }

    public SelectItem[] getComboAsignacionespoaFuente() {
        if (fuente == null || fuente.isEmpty()) {
            return null;
        }
        if (partida == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.partida.codigo=:partida and o.fuente=:fuente and o.proyecto.anio=:anio and o.activo=true and o.proyecto.direccion=:direccion and o.cerrado=true");
        parametros.put("partida", partida);
        parametros.put("fuente", fuente);
        parametros.put("direccion", direccion);
        parametros.put("anio", anio);
        parametros.put(";orden", "o.proyecto.codigo, o.fuente");
        try {
            List<Asignacionespoa> lAsignacionespoa = ejbAsignacionespoa.encontarParametros(parametros);
            int size = lAsignacionespoa.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Asignacionespoa x : lAsignacionespoa) {
                items[i++] = new SelectItem(x, x.getProyecto().getCodigo() + " - " + x.getProyecto().getNombre());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String reporte(Certificacionespoa certificacio) {
        try {
            certificacion = certificacio;
            recurso = null;
            traerImagen();
            if (!certificacion.getImpreso()) {
                MensajesErrores.advertencia("La certificación debe estar aprobada antes de imprimir");
                return null;
            }
            formularioImpresion.editar();
            imprimir = certificacion.getImpreso();
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacio);
            detalles = ejbDetalles.encontarParametros(parametros);
            double total = 0;
            for (Detallecertificacionespoa d : detalles) {
                total += d.getValor().doubleValue();
            }
            Detallecertificacionespoa d1 = new Detallecertificacionespoa();
            d1.setValor(new BigDecimal(total));
            detalles.add(d1);
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            //titulos
            List<Auxiliar> lista = proyectoPoaBean.getTitulos();
            for (Auxiliar a : lista) {
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, a.getTitulo1()));
            }
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FECHA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FUENTE DE FINANCIAMIENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "VALOR INCLUIDO IVA"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            pdf = new DocumentoPDF("CERTIFICACIÓN  DE POA", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//            pdf.agregaParrafo("N°: " + (certificacion.getNumeropoa() != null ? certificacion.getNumeropoa() : certificacion.getId() + "-CPOA-DPI-CBDMQ-" + anio), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("N°: " + (certificacion.getNumeropoa() != null ? certificacion.getNumeropoa() : certificacion.getNumero() + "-CPOA-DPI-CBDMQ-" + anio), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(certificacion.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("El Director de Planificación Institucional certifica que:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            Detallecertificacionespoa d2 = detalles.get(0);
            if (d2 != null) {
                String tipo = d2.getAsignacion().getProyecto().getTipocompra();
//                pdf.agregaParrafo((d2.getAsignacion().getProyecto().getTipocompra() != null ? d2.getAsignacion().getProyecto().getTipocompra() : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase(), AuxiliarReporte.ALIGN_CENTER, 11, false);
                pdf.agregaParrafo((certificacion.getCertificaque() != null ? (certificacion.getCertificaque()) : ((tipo != null ? tipo : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase())), AuxiliarReporte.ALIGN_CENTER, 11, false);
            }
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Solicitado en memorando Nº " + (certificacion.getMemo() != null ? certificacion.getMemo() : " ") + ".", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("CERTIFICACIÓN  DE POA", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Son tomados del PLAN OPERATIVO ANUAL " + certificacion.getAnio() + ", según se detalla a continuación:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, proyectoPoaBean.dividir1(a, d.getAsignacion().getProyecto())));
                    }
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, sdf1.format(d.getFecha())));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, d.getAsignacion().getProyecto().getNombre()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, d.getAsignacion().getPartida().getCodigo()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, codigosBean.traerCodigo("FUENFIN", d.getAsignacion().getFuente()).getNombre()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, d.getAsignacion().getProyecto().getCodigo()));
                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                } else {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
                    }
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "TOTAL"));
                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, d.getValor().doubleValue()));
                }
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafo("\n\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            valores = new LinkedList<>();
            List<String> tit = new LinkedList<>();
            tit.add("REVISADO POR:");
            tit.add("CERTIFICADO POR:");
//            parametros = new HashMap();
//            parametros.put(";where", "o.activo=true and o.cargoactual.activo=true and o.cargoactual.descripcion='DIRECTOR DE PLANIFICACION'");
//            List<Empleados> list = ejbEmpleados.encontarParametros(parametros);
//            Empleados e = null;
//            if (!list.isEmpty()) {
//                e = list.get(0);
//            }
            Codigos cod2 = ejbCodigos.traerCodigo("FIRCERT", "03");
            if (cod2 == null) {
                MensajesErrores.advertencia("No existe codigo de firmas de Certificación");
                return null;
            }
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "_____________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "_____________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, certificacion.getUsuario() != null ? certificacion.getUsuario() : ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, cod2.getParametros() != null ? cod2.getParametros() : ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "ANALISTA DE PLANIFICACIÓN"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, cod2.getDescripcion() != null ? cod2.getDescripcion() : ""));
            List<Codigos> aux = codigosBean.traerCodigoMaestro("DIREC");
            for (Codigos c : aux) {
                if (c.getCodigo().equals("CRP")) {
                    AuxiliarEntidad o = consultasPoaBean.traerEmpleadoCargo(c.getDescripcion());
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, o.getNombres() + " " + o.getApellidos()));
                }
            }
            for (Codigos c : aux) {
                if (c.getCodigo().equals("CAP")) {
                    AuxiliarEntidad o = consultasPoaBean.traerEmpleadoCargo(c.getDescripcion());
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, o.getNombres() + " " + o.getApellidos() + "\n" + "EXPERTO PRINCIPAL EN PLANIFICACIÓN"));
                }
            }
            pdf.agregarTabla(tit, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);
            reportepdf = pdf.traerRecurso();
            archivoFirmar = pdf.traerArchivo();
//            FirmadorPDF firmador = new FirmadorPDF(this.certificacion.getNumero(), "140567Ev", "1708846223",
//                    configuracionBean.getConfiguracion().getDirectorio(), reportepdf);
//            firmador.firmar();
        } catch (ConsultarException | IOException | DocumentException  ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException ex) {
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerImagen() throws GrabarException {
        try {
            recurso = null;
            if (certificacion != null) {
                if (certificacion.getArchivo() != null) {
                    nombreArchivo = "Certificacion: " + certificacion.getNumero();
                    tipoArchivo = "Certificacion";
                    recurso = new Recurso(Files.readAllBytes(Paths.get(certificacion.getArchivo())));
                }
            }
        } catch (IOException ex) {
            if (recurso == null) {
                certificacion.setArchivo(null);
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            MensajesErrores.advertencia("Documento firmado no se encuentra");
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String firma() {
        formularioFirma.insertar();
        return null;

    }

    public String grabarFirma() {
        try {
            System.out.println("Inicio Firma");
            FirmadorPDF firmador = new FirmadorPDF("certificacionPoa-" + this.certificacion.getId(), clave, seguridadbean.getLogueado().getPin(), configuracionBean.getConfiguracion().getDirectorio(), archivoFirmar, pdf.getNumeroPaginas());
            boolean archivoOk = false;
            if (certificacion.getArchivo() != null) {
                File existente = new File(certificacion.getArchivo());
                if (existente.exists()) {
                    byte[] docByteArry = FileUtils.fileConvertToByteArray(existente);
                    if (docByteArry.length != 0) {
                        archivoOk = true;
                    }
                }
            }

            if (!archivoOk) {
                firmador.firmar();
                certificacion.setArchivo(configuracionBean.getConfiguracion().getDirectorio() + "/firmados/" + "certificacionPoa-" + certificacion.getId() + ".pdf");
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
                reporte(certificacion);
            } else {
                firmador.editarFirma();
                reporte(certificacion);
            }
            System.out.println("Fin Firma");
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        clave = null;
//        formularioImpresion.cancelar();
        formularioFirma.cancelar();
        return null;
    }

    public String aprobar(Certificacionespoa certif) {
        this.certificacion = certif;
        if (certificacion.getAprobardga() == null) {
            certificacion.setAprobardga(Boolean.FALSE);
        }
//        if (!certificacion.getAprobardga()) {
//            MensajesErrores.advertencia("Certificacion no aprobada por DGA");
//            return null;
//        }
        if (certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación aprobada");
            return null;
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación anulada");
            return null;
        }
        if (certificacion.getRechazado()) {
            MensajesErrores.advertencia("Certificación rechazada");
            return null;
        }
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.buscarDocumentos();
        formularioImpresion.editar();
        imprimir = certificacion.getImpreso();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        double total = 0;
        for (Detallecertificacionespoa d : detalles) {
            total += d.getValor().doubleValue();
        }
        Detallecertificacionespoa d1 = new Detallecertificacionespoa();
        d1.setValor(new BigDecimal(total));
        detalles.add(d1);
        return null;
    }

    public String grabarAprobacion() {
        try {
            if (certificacion.getFechaaprobacion() == null) {
                MensajesErrores.advertencia("Ingresar Fecha");
                return null;
            }
            if (certificacion.getUsuario() == null) {
                MensajesErrores.advertencia("Seleccione Usuario");
                return null;
            }
            certificacion.setImpreso(true);

            for (Detallecertificacionespoa dc : detalles) {
                if (dc.getId() != null) {
                    detalle = dc;
                    asignacion = detalle.getAsignacion();
                    double asignado = asignacion.getValor().doubleValue() * 100;
                    double reformas = calculaTotalReformas(asignacion) * 100;
                    double tCertificacionespoa = calculaTotalCertificacionespoa(asignacion) * 100;
                    double suma = asignado + reformas - tCertificacionespoa;
                    double valor = detalle.getValor().doubleValue() * 100;
                    double compara = Math.round(suma - valor);
                    if (compara < 0) {
                        MensajesErrores.advertencia("Certificación exede el saldo de la partida");
                        return null;
                    }
                }
            }
            // enviar al financiero
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    Map parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    if (p == null) {
                        MensajesErrores.advertencia("Proyecto PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getProyecto().toString());
                        return null;
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", d.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }
                    if (c == null) {
                        MensajesErrores.advertencia("Partida PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getPartida().toString());
                        return null;
                    }
                    // Buscar la fuente
                    Codigos co = null;
                    List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                    for (Codigos co1 : listaCodigos) {
                        if (co1.getCodigo().equals(d.getAsignacion().getFuente())) {
                            co = co1;
                        }
                    }
                    if (co == null) {
                        MensajesErrores.advertencia("Fuente de Financiamiento PAC-POA no tiene su similar en Sistema Financiero : " + d.getAsignacion().getFuente().toString());
                        return null;
                    }
                    // Buscar Asignación
                    parametros = new HashMap();
                    parametros.put(";where", "o.clasificador=:clasificador "
                            + " and o.proyecto=:proyecto and o.fuente=:fuente");
                    parametros.put("clasificador", c);
                    parametros.put("proyecto", p);
                    parametros.put("fuente", co);
                    List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                    Asignaciones af = null;
                    for (Asignaciones af1 : listaa) {
                        af = af1;
                    }
                    if (af == null) {
                        if (d.getValor().doubleValue() >= 0) {
                            // creo la asignación

                        } else {
                            MensajesErrores.advertencia("No existe dinero para ejecutar esta certificación en Sistema Financiero : "
                                    + c.toString() + " - " + p.toString() + " - "
                                    + d.getAsignacion().getFuente().toString());
                            return null;
                        }
                    }
                }
            }
            // fin financiero
            if (certificacion.getNumero() == null) {
                MensajesErrores.advertencia("La certificación debe tener un número de control");
                return null;
            }
            if (certificacion.getNumero() == null) {
                if (certificacion.getNumero().length() < 8) {
                    Calendar cal = Calendar.getInstance();
                    String aa = "" + cal.get(Calendar.YEAR);
                    String ordinal = String.format("%03d", certificacion.getId());
                    certificacion.setNumero(certificacion.getId().toString());
                    certificacion.setNumeropoa(ordinal + "-CPOA-DPI-CBDMQ-" + aa);
                }
            }
            if (certificacion.getNumeropoa() == null) {
//                certificacion.setNumeropoa(certificacion.getId() + "-CPOA-DPI-CBDMQ-" + anio);
                certificacion.setNumeropoa(certificacion.getNumero() + "-CPOA-DPI-CBDMQ-" + anio);
            }

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación aprobada: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");
            certificacion.setInfimanoplanificada(Boolean.FALSE);
            //Infima no planificada 
            // financiero
            if (infima) {
                certificacion.setInfimanoplanificada(Boolean.TRUE);
                grabarFinanciero();
            } else {
                Integer contar = 1;
                for (Detallecertificacionespoa dcp : detalles) {
                    if (dcp.getId() != null) {
                        String buscarProyecto = dcp.getAsignacion().getProyecto().getCodigo();
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                        parametros.put("codigo", buscarProyecto);
                        parametros.put("anio", anio);
                        List<Proyectospoa> listapp = ejbProyectospoa.encontarParametros(parametros);
                        for (Proyectospoa pp : listapp) {
                            if (pp.getPac() == null) {
                                pp.setPac(Boolean.FALSE);
                            }
//                            if (pp.getPac() && pp.getFechautilizacion() == null) {
                            if (pp.getPac()) {
                                //Aprobacion del pac desde la vista CertificaciónPacVista
//                                parametros = new HashMap();
//                                parametros.put("where", " o.generapac=true");
//                                String ordinal = String.format("%03d", ejbCertificacionespoa.contar(parametros) + 1);
//                                certificacion.setNumeropac(ordinal + "-PAC-DA-CBDMQ-" + anio);
//                                certificacion.setImpresopac(true);
//                                certificacion.setFechapac(new Date());
                                if (pp.getFechautilizacion() != null) {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.certificacion.generapac=true "
                                            + " and o.asignacion.proyecto.fechautilizacion is not null "
                                            + " and o.certificacion.rechazadopac=false");
                                    parametros.put("proyecto", pp);
                                    List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
                                    if (lista.isEmpty()) {
                                        contar = 2;
                                        certificacion.setGenerapac(true);
                                    } else {
                                        Detallecertificacionespoa cpac = lista.get(0);
                                        String numeropac = "";
                                        if (cpac.getCertificacion().getNumeropac() != null) {
                                            numeropac = cpac.getCertificacion().getNumeropac();
                                        }
                                        MensajesErrores.advertencia("Certificación ya tiene PAC, Número Solicitud: "
                                                + cpac.getCertificacion().getId() + " Número PAC: " + numeropac);
                                        contar = 1;
                                    }
                                } else {
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.certificacion.generapac=true "
                                            + " and o.asignacion.proyecto.fechautilizacion is null"
                                            + " and o.certificacion.rechazadopac=false");
                                    parametros.put("proyecto", pp);
                                    List<Detallecertificacionespoa> lista3 = ejbDetalles.encontarParametros(parametros);
                                    if (lista3.isEmpty()) {
                                        contar = 2;
                                        certificacion.setGenerapac(true);
                                    } else {
                                        Detallecertificacionespoa cpac = lista3.get(0);
                                        String numeropac = "";
                                        if (cpac.getCertificacion().getNumeropac() != null) {
                                            numeropac = cpac.getCertificacion().getNumeropac();
                                        }
                                        MensajesErrores.advertencia("Certificación ya tiene PAC, Número Solicitud: "
                                                + cpac.getCertificacion().getId() + " Número PAC: " + numeropac);
                                        contar = 1;
                                    }
                                }
                            }
                        }
                    }
                }
                if (contar == 1) {
                    // financiero**********************************************************************
                    grabarFinanciero();
                    // fin financiero********************************************************************
                }
            }
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Certificación POA",
                        "Solicitud de Certificación POA Nro: " + certificacion.getMemo()
                        + ", Número de Solicitud: " + certificacion.getId()
                        + ", Número POA: " + certificacion.getNumero()
                        + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ", ha sido aprobada.");
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Certificación POA",
                    "Solicitud de Certificación POA Nro: " + certificacion.getMemo()
                    + ", Número de Solicitud: " + certificacion.getId()
                    + ", Número POA: " + certificacion.getNumero()
                    + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ", ha sido aprobada.");

        } catch (GrabarException | InsertarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException  | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (certificacion.getNumeropac() != null) {
//            MensajesErrores.informacion("se creo certificación PAC con número de control: " + certificacion.getNumeropac());
            MensajesErrores.informacion("Se creo certificación PAC");
        }
        formularioImpresion.cancelar();
        return null;
    }

    public String grabarFinanciero() {
        try {
            //Organigrama
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", certificacion.getDireccion());
            List<Organigrama> listaOrg = ejbOrganigrama.encontarParametros(parametros);
            Organigrama o = null;
            for (Organigrama o1 : listaOrg) {
                o = o1;
            }
            //Buscar si hay la certificacion para aumentar el detalle
            Certificaciones certFin;
            parametros = new HashMap();
            parametros.put(";where", "o.pacpoa=:pacpoa");
            parametros.put("pacpoa", certificacion.getId());
            List<Certificaciones> listaCretFin = ejbCertificacionesFinancieras.encontarParametros(parametros);
            if (listaCretFin.isEmpty()) {
                certFin = new Certificaciones();
                certFin.setAnio(certificacion.getAnio());
                certFin.setAnulado(false);
                certFin.setFecha(certificacion.getFecha());
                certFin.setMemo(certificacion.getMemo());
                certFin.setMotivo(certificacion.getMotivo());
                certFin.setRoles(certificacion.getRoles());
                certFin.setPacpoa(certificacion.getId());
                certFin.setDireccion(o);
                certFin.setImpreso(false);
                ejbCertificacionesFinancieras.create(certFin, seguridadbean.getLogueado().getUserid());
            } else {
                certFin = listaCretFin.get(0);
            }
            for (Detallecertificacionespoa dcp : detalles) {
                if (dcp.getId() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", dcp.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", dcp.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }
                    // Buscar la fuente
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", dcp.getAsignacion().getFuente());
                    Codigos co = null;
                    List<Codigos> listaCodigos = ejbCodigos.encontarParametros(parametros);
                    for (Codigos co1 : listaCodigos) {
                        co = co1;
                    }
                    // Buscar Asignación
                    parametros = new HashMap();
                    parametros.put(";where", "o.clasificador=:clasificador "
                            + " and o.proyecto=:proyecto and o.fuente=:fuente");
                    parametros.put("clasificador", c);
                    parametros.put("proyecto", p);
                    parametros.put("fuente", co);
                    List<Asignaciones> listaa = ejbAsignacionesFinanciera.encontarParametros(parametros);
                    Asignaciones af = null;
                    for (Asignaciones af1 : listaa) {
                        af = af1;
                    }
//                    if (af == null) {
//                        // creo la signación
//                        af = new Asignaciones();
//                        af.setProyecto(p);
//                        af.setClasificador(c);
//                        af.setCerrado(true);
//                        af.setFuente(co);
//                        af.setValor(new BigDecimal(BigInteger.ZERO));
//                        ejbAsignacionesFinanciera.create(af, seguridadbean.getLogueado().getUserid());
//                    }
                    Detallecertificaciones d1 = new Detallecertificaciones();
                    d1.setAsignacion(af);
                    d1.setCertificacion(certFin);
                    d1.setValor(dcp.getValor());
                    d1.setFecha(certFin.getFecha());
                    ejbDetalleFinancieras.create(d1, seguridadbean.getLogueado().getUserid());
                }
            }
        } catch (org.errores.sfccbdmq.ConsultarException | org.errores.sfccbdmq.InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarRechazo() {
        if (certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación aprobada");
            return null;
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación anulada");
            return null;
        }
        if (certificacion.getRechazado()) {
            MensajesErrores.advertencia("Certificación rechazada");
            return null;
        }
        if (certificacion.getFechaaprobacion() == null) {
            MensajesErrores.advertencia("Ingresar Fecha");
            return null;
        }
        if (certificacion.getUsuario() == null) {
            MensajesErrores.advertencia("Seleccione Usuario");
            return null;
        }
        if ((certificacion.getObservacionrechazo() == null) || (certificacion.getObservacionrechazo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario observación");
            return null;
        }
        certificacion.setRechazado(Boolean.TRUE);
        certificacion.setGenerapac(Boolean.FALSE);
        try {
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación rechazada: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ", ha sido rechazada.");
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Certificación POA", "Solicitud de Certificación POA Nro: " + certificacion.getMemo() + ", realizada por " + consultasPoaBean.traerDireccion(certificacion.getDireccion()) + ", ha sido rechazada.");

            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");
        } catch (GrabarException | InsertarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.cancelar();
        return null;
    }

    public SelectItem[] getComboUsuarios() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.entidad desc");
        parametros.put(";where", "o.cargoactual.organigrama.codigo='A.5' and o.activo=true and o.fechasalida=null");
        try {
            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
            SelectItem[] items = new SelectItem[lista.size() + 1];
            int i = 1;
            items[0] = new SelectItem(null, "---");
            for (Empleados x : lista) {
                items[i++] = new SelectItem(x.getEntidad().toString(), x.toString());
            }
            return items;
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarAsignacion() {
        try {
            certificacion.setEstado("Asignado");
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación Asignada a: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.cancelar();
        return null;

    }

    public String grabarRevision() {
        try {
            certificacion.setEstado("Revisado");
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación Revisada por: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.cancelar();
        return null;
    }

    public String modificarNumero(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
        formularioNumero.editar();
        return null;
    }

    public String modificarCertificaque(Certificacionespoa certi) {
        try {
            this.certificacion = certi;
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            for (Detallecertificacionespoa de : lista) {
                String tipo = de.getAsignacion().getProyecto().getTipocompra();
                if (certificacion.getCertificaque() == null) {
                    if (tipo != null) {
                        certificacion.setCertificaque(tipo + "-" + de.getAsignacion().getProyecto().getNombre().toUpperCase());
                    }
                }

            }

        } catch (ConsultarException ex) {
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCertq.editar();
        return null;
    }

    public String grabarNumero() {
        try {
            if ((certificacion.getNumero() == null) || (certificacion.getNumero().isEmpty())) {
                MensajesErrores.advertencia("Es necesario N° de certificación");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where ", "o.numero=:numero and o.anulado=false");
            parametros.put("numero", certificacion.getNumero());
            List<Certificacionespoa> lista = ejbCertificacionespoa.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("El número de certificación ya se encuentra");
                return null;
            }
            Integer numero = Integer.parseInt(certificacion.getNumero());
            if (certificacion.getNumeropoa() != null) {
                Calendar cal = Calendar.getInstance();
                String aa = "" + cal.get(Calendar.YEAR);
                String ordinal = String.format("%03d", numero);
                certificacion.setNumeropoa(ordinal + "-CPOA-DPI-CBDMQ-" + aa);
            }
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioNumero.cancelar();
        return null;
    }

    public String grabarCertificaQue() {
        try {
            if ((certificacion.getCertificaque() == null) || (certificacion.getCertificaque().isEmpty())) {
                MensajesErrores.advertencia("Es necesario descripción de certifica que");
                return null;
            }
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioNumero.cancelar();
        return null;
    }

    public String traerProducto(Certificacionespoa cer) {
        String producto = "";
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cer);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            for (Detallecertificacionespoa dcp : lista) {
                producto += dcp.getAsignacion().getProyecto().toString() + "\n";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return producto;
    }

    /**
     * @return the proyectoPoaBean
     */
    public ProyectosPoaBean getProyectoPoaBean() {
        return proyectoPoaBean;
    }

    /**
     * @param proyectoPoaBean the proyectoPoaBean to set
     */
    public void setProyectoPoaBean(ProyectosPoaBean proyectoPoaBean) {
        this.proyectoPoaBean = proyectoPoaBean;
    }

    /**
     * @return the asignacionesPoaBean
     */
    public AsignacionesPoaBean getAsignacionespoaPoaBean() {
        return asignacionesPoaBean;
    }

    /**
     * @param asignacionesPoaBean the asignacionesPoaBean to set
     */
    public void setAsignacionespoaPoaBean(AsignacionesPoaBean asignacionesPoaBean) {
        this.asignacionesPoaBean = asignacionesPoaBean;
    }

    /**
     * @return the calculosPacpoa
     */
    public CalculosPoaBean getCalculosPacpoa() {
        return calculosPacpoa;
    }

    /**
     * @param calculosPacpoa the calculosPacpoa to set
     */
    public void setCalculosPacpoa(CalculosPoaBean calculosPacpoa) {
        this.calculosPacpoa = calculosPacpoa;
    }

    /**
     * @return the asignacion
     */
    public Asignacionespoa getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(Asignacionespoa asignacion) {
        this.asignacion = asignacion;
    }

    /**
     * @return the certificacion
     */
    public Certificacionespoa getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(Certificacionespoa certificacion) {
        this.certificacion = certificacion;
    }

    /**
     * @return the detalles
     */
    public List<Detallecertificacionespoa> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Detallecertificacionespoa> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the detallesb
     */
    public List<Detallecertificacionespoa> getDetallesb() {
        return detallesb;
    }

    /**
     * @param detallesb the detallesb to set
     */
    public void setDetallesb(List<Detallecertificacionespoa> detallesb) {
        this.detallesb = detallesb;
    }

    /**
     * @return the certificaciones
     */
    public LazyDataModel<Certificacionespoa> getCertificacionespoa() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificacionespoa(LazyDataModel<Certificacionespoa> certificaciones) {
        this.certificaciones = certificaciones;
    }

    /**
     * @return the certificacionesCompromiso
     */
    public List<Certificacionespoa> getCertificacionespoaCompromiso() {
        return certificacionesCompromiso;
    }

    /**
     * @param certificacionesCompromiso the certificacionesCompromiso to set
     */
    public void setCertificacionespoaCompromiso(List<Certificacionespoa> certificacionesCompromiso) {
        this.certificacionesCompromiso = certificacionesCompromiso;
    }

    /**
     * @return the detalle
     */
    public Detallecertificacionespoa getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecertificacionespoa detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the numeroControl
     */
    public String getNumeroControl() {
        return numeroControl;
    }

    /**
     * @param numeroControl the numeroControl to set
     */
    public void setNumeroControl(String numeroControl) {
        this.numeroControl = numeroControl;
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the partida
     */
    public String getPartida() {
        return partida;
    }

    /**
     * @param partida the partida to set
     */
    public void setPartida(String partida) {
        this.partida = partida;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the fuente
     */
    public String getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    /**
     * @return the imprimir
     */
    public boolean isImprimir() {
        return imprimir;
    }

    /**
     * @param imprimir the imprimir to set
     */
    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }

    /**
     * @return the numeroDocumento
     */
    public Integer getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * @param numeroDocumento the numeroDocumento to set
     */
    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    /**
     * @return the impresas
     */
    public String getImpresas() {
        return impresas;
    }

    /**
     * @param impresas the impresas to set
     */
    public void setImpresas(String impresas) {
        this.impresas = impresas;
    }

    /**
     * @return the reportepdf
     */
    public Recurso getReportepdf() {
        return reportepdf;
    }

    /**
     * @param reportepdf the reportepdf to set
     */
    public void setReportepdf(Recurso reportepdf) {
        this.reportepdf = reportepdf;
    }

    /**
     * @return the pdf
     */
    public DocumentoPDF getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(DocumentoPDF pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
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
     * @return the formularioClasificador
     */
    public Formulario getFormularioClasificador() {
        return formularioClasificador;
    }

    /**
     * @param formularioClasificador the formularioClasificador to set
     */
    public void setFormularioClasificador(Formulario formularioClasificador) {
        this.formularioClasificador = formularioClasificador;
    }

    /**
     * @return the formularioDetalle
     */
    public Formulario getFormularioDetalle() {
        return formularioDetalle;
    }

    /**
     * @param formularioDetalle the formularioDetalle to set
     */
    public void setFormularioDetalle(Formulario formularioDetalle) {
        this.formularioDetalle = formularioDetalle;
    }

    /**
     * @return the formularioAsignacion
     */
    public Formulario getFormularioAsignacion() {
        return formularioAsignacion;
    }

    /**
     * @param formularioAsignacion the formularioAsignacion to set
     */
    public void setFormularioAsignacion(Formulario formularioAsignacion) {
        this.formularioAsignacion = formularioAsignacion;
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
     * @return the formularioConfirmacion
     */
    public Formulario getFormularioConfirmacion() {
        return formularioConfirmacion;
    }

    /**
     * @param formularioConfirmacion the formularioConfirmacion to set
     */
    public void setFormularioConfirmacion(Formulario formularioConfirmacion) {
        this.formularioConfirmacion = formularioConfirmacion;
    }

    /**
     * @return the observacionAdjudicacion
     */
    public String getObservacionAdjudicacion() {
        return observacionAdjudicacion;
    }

    /**
     * @param observacionAdjudicacion the observacionAdjudicacion to set
     */
    public void setObservacionAdjudicacion(String observacionAdjudicacion) {
        this.observacionAdjudicacion = observacionAdjudicacion;
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
     * @return the asignacionesPoaBean
     */
    public AsignacionesPoaBean getAsignacionesPoaBean() {
        return asignacionesPoaBean;
    }

    /**
     * @param asignacionesPoaBean the asignacionesPoaBean to set
     */
    public void setAsignacionesPoaBean(AsignacionesPoaBean asignacionesPoaBean) {
        this.asignacionesPoaBean = asignacionesPoaBean;
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
     * @return the consultasPoaBean
     */
    public ConsultasPoaBean getConsultasPoaBean() {
        return consultasPoaBean;
    }

    /**
     * @param consultasPoaBean the consultasPoaBean to set
     */
    public void setConsultasPoaBean(ConsultasPoaBean consultasPoaBean) {
        this.consultasPoaBean = consultasPoaBean;
    }

    /**
     * @return the certificaciones
     */
    public LazyDataModel<Certificacionespoa> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(LazyDataModel<Certificacionespoa> certificaciones) {
        this.certificaciones = certificaciones;
    }

    /**
     * @return the certificacionesCompromiso
     */
    public List<Certificacionespoa> getCertificacionesCompromiso() {
        return certificacionesCompromiso;
    }

    /**
     * @param certificacionesCompromiso the certificacionesCompromiso to set
     */
    public void setCertificacionesCompromiso(List<Certificacionespoa> certificacionesCompromiso) {
        this.certificacionesCompromiso = certificacionesCompromiso;
    }

    /**
     * @return the vigente
     */
    public Date getVigente() {
        return vigente;
    }

    /**
     * @param vigente the vigente to set
     */
    public void setVigente(Date vigente) {
        this.vigente = vigente;
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
     * @return the imagenesPoaBean
     */
    public ImagenesPoaBean getImagenesPoaBean() {
        return imagenesPoaBean;
    }

    /**
     * @param imagenesPoaBean the imagenesPoaBean to set
     */
    public void setImagenesPoaBean(ImagenesPoaBean imagenesPoaBean) {
        this.imagenesPoaBean = imagenesPoaBean;
    }

    /**
     * @return the observacionAprobacion
     */
    public String getObservacionAprobacion() {
        return observacionAprobacion;
    }

    /**
     * @param observacionAprobacion the observacionAprobacion to set
     */
    public void setObservacionAprobacion(String observacionAprobacion) {
        this.observacionAprobacion = observacionAprobacion;
    }

    /**
     * @return the observacionLiquidacion
     */
    public String getObservacionLiquidacion() {
        return observacionLiquidacion;
    }

    /**
     * @param observacionLiquidacion the observacionLiquidacion to set
     */
    public void setObservacionLiquidacion(String observacionLiquidacion) {
        this.observacionLiquidacion = observacionLiquidacion;
    }

    /**
     * @return the liquidadas
     */
    public String getLiquidadas() {
        return liquidadas;
    }

    /**
     * @param liquidadas the liquidadas to set
     */
    public void setLiquidadas(String liquidadas) {
        this.liquidadas = liquidadas;
    }

    /**
     * @return the anuladas
     */
    public String getAnuladas() {
        return anuladas;
    }

    /**
     * @param anuladas the anuladas to set
     */
    public void setAnuladas(String anuladas) {
        this.anuladas = anuladas;
    }

    /**
     * @return the proyectoSeleccionado
     */
    public Proyectospoa getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    /**
     * @param proyectoSeleccionado the proyectoSeleccionado to set
     */
    public void setProyectoSeleccionado(Proyectospoa proyectoSeleccionado) {
        this.proyectoSeleccionado = proyectoSeleccionado;
    }

    /**
     * @return the tipoBuscar
     */
    public Integer getTipoBuscar() {
        return tipoBuscar;
    }

    /**
     * @param tipoBuscar the tipoBuscar to set
     */
    public void setTipoBuscar(Integer tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

    /**
     * @return the listaAsignacion
     */
    public List<Asignacionespoa> getListaAsignacion() {
        return listaAsignacion;
    }

    /**
     * @param listaAsignacion the listaAsignacion to set
     */
    public void setListaAsignacion(List<Asignacionespoa> listaAsignacion) {
        this.listaAsignacion = listaAsignacion;
    }

    /**
     * @return the codigoOrganigrama
     */
    public String getCodigoOrganigrama() {
        return codigoOrganigrama;
    }

    /**
     * @param codigoOrganigrama the codigoOrganigrama to set
     */
    public void setCodigoOrganigrama(String codigoOrganigrama) {
        this.codigoOrganigrama = codigoOrganigrama;
    }

    /**
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the formularioFirma
     */
    public Formulario getFormularioFirma() {
        return formularioFirma;
    }

    /**
     * @param formularioFirma the formularioFirma to set
     */
    public void setFormularioFirma(Formulario formularioFirma) {
        this.formularioFirma = formularioFirma;
    }

    /**
     * @return the clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * @return the observacionDesaprobado
     */
    public String getObservacionDesaprobado() {
        return observacionDesaprobado;
    }

    /**
     * @param observacionDesaprobado the observacionDesaprobado to set
     */
    public void setObservacionDesaprobado(String observacionDesaprobado) {
        this.observacionDesaprobado = observacionDesaprobado;
    }

    /**
     * @return the formularioDesaprobar
     */
    public Formulario getFormularioDesaprobar() {
        return formularioDesaprobar;
    }

    /**
     * @param formularioDesaprobar the formularioDesaprobar to set
     */
    public void setFormularioDesaprobar(Formulario formularioDesaprobar) {
        this.formularioDesaprobar = formularioDesaprobar;
    }

//     public String adjudicar(Certificacionespoa certificacion) {
//        formularioClasificador.cancelar();
//        this.certificacion = certificacion;
//        if (certificacion.getAnulado()) {
//            MensajesErrores.advertencia("No se puede adjudicar una certificación anulada");
//            return null;
//        }
//        if (certificacion.getRechazado()) {
//            MensajesErrores.advertencia("No se puede adjudicar una certificación rechazada");
//            return null;
//        }
//        if (!certificacion.getImpreso()) {
//            MensajesErrores.advertencia("No se puede adjudicar una certificación sin haber sido aprobada");
//            return null;
//        }
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.certificacion=:certificacion");
//        parametros.put("certificacion", certificacion);
//        try {
//            detalles = ejbDetalles.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        detallesb = new LinkedList<>();
//        formulario.editar();
//        return null;
//
//    }
//
//    public String grabarAdjudicar() {
//        try {
//            //Ver una observación
//            if ((observacionAdjudicacion == null) || (observacionAdjudicacion.trim().isEmpty())) {
//                MensajesErrores.advertencia("Observación necesaria");
//                return null;
//            }
//            if ((detalles == null) || (detalles.isEmpty())) {
//                MensajesErrores.advertencia("Es necesario al menos una cuenta");
//                return null;
//            }
//            for (Detallecertificacionespoa d : detalles) {
//                if (d.getAdjudicado() == null) {
//                    MensajesErrores.advertencia("Es necesario que al menos un registro sea adjudicado");
//                    return null;
//                }
//            }
//            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
//            String valores = "";
//
//            for (Detallecertificacionespoa d : detalles) {
//                ejbDetalles.edit(d, seguridadbean.getLogueado().getUserid());
//                if (d.getAdjudicado() != null) {
//                    valores = d.getAsignacion().getProyecto().getCodigo() + ": $" + Math.round(d.getAdjudicado().doubleValue()) + "\n";
//                }
//            }
//            Trackingspoa tracking = new Trackingspoa();
//            tracking.setReformanombre(false);
//            tracking.setFecha(new Date());
//            tracking.setCertificacion(certificacion);
//            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
//            tracking.setObservaciones(""
//                    + "Certificación adjudicada con valores:\n"
//                    + valores
//                    + "Documento :" + certificacion.getNumero() + "\n"
//                    + "Observaciones: " + observacionAdjudicacion);
//            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
//        } catch (GrabarException | InsertarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        buscar();
//        formulario.cancelar();
//        formularioClasificador.cancelar();
//        return null;
//    }
//    public String grabarDetalleAdjudicacion() {
//        if (detalle.getAdjudicado().doubleValue() == 0) {
//            MensajesErrores.advertencia("Valor adjudicado debe ser distinto a cero");
//            return null;
//        }
//        if (detalle.getAdjudicado().doubleValue() > detalle.getValor().doubleValue()) {
//            MensajesErrores.advertencia("Valor de adjudicación excede el de la certificación");
//            return null;
//        }
//        detalles.set(formularioDetalle.getIndiceFila(), detalle);
//        formularioClasificador.cancelar();
//        formularioDetalle.cancelar();
//        return null;
//    }
    /**
     * @return the cerfificaQue
     */
    public String getCerfificaQue() {
        return cerfificaQue;
    }

    /**
     * @param cerfificaQue the cerfificaQue to set
     */
    public void setCerfificaQue(String cerfificaQue) {
        this.cerfificaQue = cerfificaQue;
    }

    /**
     * @return the infima
     */
    public boolean isInfima() {
        return infima;
    }

    /**
     * @param infima the infima to set
     */
    public void setInfima(boolean infima) {
        this.infima = infima;
    }

    /**
     * @return the formularioNumero
     */
    public Formulario getFormularioNumero() {
        return formularioNumero;
    }

    /**
     * @param formularioNumero the formularioNumero to set
     */
    public void setFormularioNumero(Formulario formularioNumero) {
        this.formularioNumero = formularioNumero;
    }

//    /**
//     * @return the numero
//     */
//    public Certificaciones getNumero() {
//        return numero;
//    }
//
//    /**
//     * @param numero the numero to set
//     */
//    public void setNumero(Certificaciones numero) {
//        this.numero = numero;
//    }
    /**
     * @return the formularioCertq
     */
    public Formulario getFormularioCertq() {
        return formularioCertq;
    }

    /**
     * @param formularioCertq the formularioCertq to set
     */
    public void setFormularioCertq(Formulario formularioCertq) {
        this.formularioCertq = formularioCertq;
    }

    /**
     * @return the numeroSolicitud
     */
    public String getNumeroSolicitud() {
        return numeroSolicitud;
    }

    /**
     * @param numeroSolicitud the numeroSolicitud to set
     */
    public void setNumeroSolicitud(String numeroSolicitud) {
        this.numeroSolicitud = numeroSolicitud;
    }

    /**
     * @return the recurso
     */
    public Recurso getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
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
}
