/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CertificacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Certificacionespoa;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.entidades.sfccbdmq.Proyectospoa;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.ImpuestosFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Impuestos;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Proyectos;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.firma.FileUtils;
import org.utilitarios.firma.FirmadorPDF;

/**
 *
 * @author luis
 */
@ManagedBean(name = "certificacionPac")
@ViewScoped
public class CertificacionesPacBean {

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

    private Certificacionespoa certificacion;
    private LazyDataModel<Certificacionespoa> listacCertificacionesPac;
    private Detallecertificacionespoa detalle;
    private String numeroControl;
    private String numeroControlPac;
    private String motivo;
    private String direccion;
    private String impresas = "TRUE";
    private int anio;
    private Date desde;
    private Date hasta;
    private Date vigente;
    private Double impuesto;
    private DocumentoPDF pdf;
    private Recurso reportepdf;
    private String usuariopac;
    private Date fechapac;
    private Date fechaDoc;
    private String obsRechazo;
    private boolean imprimir;
    private Proyectospoa proyecto;
    private String clave;
    private File archivoFirmar;
    private Recurso recurso;
    private String nombreArchivo;
    private String tipoArchivo;
    private Formulario formulario = new Formulario();
    private Formulario formularioFirma = new Formulario();
    private Formulario formularioCertificacion = new Formulario();
    private Formulario formularioImpresion = new Formulario();

    @EJB
    private CertificacionespoaFacade ejbCertificacionespoa;
    @EJB
    private DetallecertificacionespoaFacade ejbDetalles;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private ImpuestosFacade ejbImpuestos;
    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    // Financieras
    @EJB
    private CertificacionesFacade ejbCertificacionesFinancieras;
    @EJB
    private ReformaspoaFacade ejbReformasPoa;
    @EJB
    private DetallecertificacionesFacade ejbDetalleFinancieras;
    @EJB
    private AsignacionesFacade ejbAsignacionesFinanciera;
    @EJB
    private ProyectosFacade ejbProyectosFinanciera;
    @EJB
    private ClasificadoresFacade ejbPartidasFinanciera;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private CodigosFacade ejbCodigosFacade;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private TrackingspoaFacade ejbTrackingspoa;

    public CertificacionesPacBean() {
        listacCertificacionesPac = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
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
        vigente = getConfiguracionBean().getConfiguracion().getPvigentepresupuesto();
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
        listacCertificacionesPac = new LazyDataModel<Certificacionespoa>() {
            @Override
            public List<Certificacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc, o.id desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.anio=:anio "
                        + " and o.fecha between :desde and :hasta and o.generapac=true and o.impreso=true";
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
                        where += " and o.impreso=true";
                    } else {
                        where += " and o.impreso=false";
                    }
                }
                if (getNumeroControl() != null && !numeroControl.isEmpty()) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", getNumeroControl());
                }
                if (numeroControlPac != null && !numeroControlPac.isEmpty()) {
                    where += " and o.numeropac like :numeropac";
                    parametros.put("numeropac", numeroControlPac + "%");
                }
                if (getDireccion() != null && !direccion.isEmpty()) {
                    where += " and o.direccion=:direccion";
                    parametros.put("direccion", getDireccion());
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCertificacionespoa.contar(parametros);

                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    getListacCertificacionesPac().setRowCount(total);
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

//    public String modificarPAC(Detallecertificacionespoa detalle) {
    public String modificarPAC(Certificacionespoa cert) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> list = ejbDetalles.encontarParametros(parametros);
            if (!list.isEmpty()) {
                detalle = list.get(0);
            }
            this.proyecto = detalle.getAsignacion().getProyecto();
            if (proyecto.getFechautilizacion() != null) {
                MensajesErrores.advertencia("La Certificación PAC ya esta llena");
                return null;
            }
            if (proyecto.getImpuesto() != null) {
                impuesto = proyecto.getImpuesto().doubleValue();
            } else {
                impuesto = 0.00;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCertificacion.editar();
        return null;
    }

    public String grabarCertificacionPAC() {
        if (validarCertificacionPAC()) {
            return null;
        }
        try {
            if (proyecto.getImpuesto() == null) {
                proyecto.setImpuesto(BigDecimal.ZERO);
                proyecto.setValoriva(new BigDecimal(traerValorIvaAsignacion(detalle)));
            } else {
                proyecto.setValoriva(new BigDecimal(traerValorIvaAsignacion(detalle)));
            }
            ejbProyectospoa.edit(proyecto, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCertificacion.cancelar();
        return null;
    }

    public String reporte(Certificacionespoa cert) {
        try {
            certificacion = cert;
            reportepdf = null;
            traerImagen();
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> list = ejbDetalles.encontarParametros(parametros);
            if (!list.isEmpty()) {
                detalle = list.get(0);
            }
            this.proyecto = detalle.getAsignacion().getProyecto();
            if (proyecto.getValoriva() == null) {
                proyecto.setValoriva(BigDecimal.ZERO);
            }
            if (!detalle.getCertificacion().getImpresopac()) {
                MensajesErrores.advertencia("La certificación PAC debe tener responsable antes de imprimir");
                return null;
            }
            imprimir = detalle.getCertificacion().getImpresopac();

            if (proyecto.getImpuesto() != null) {
                impuesto = proyecto.getImpuesto().doubleValue();
            } else {
                impuesto = 0.00;
            }
            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            if (proyecto.getId() != null) {
//                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "AÑO"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "PARTIDA"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CPC"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "TIPO COMPRA"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "DETALLE"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CANTIDAD"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "UNIDAD"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "COSTO UNITARIO"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CUATRIMESTRE 1"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CUATRIMESTRE 2"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CUATRIMESTRE 3"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "TIPO PRODUCTO"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CATÁLOGO"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "PROCEDIMIENTO CONTRATACIÓN"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "FONDOS BID"));
                if (proyecto.getFondobid() != null) {
//                    if (proyecto.getFondobid()) {
                    titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "CÓDIGO OPERACIÓN"));
                    titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "NÚMERO OPERACIÓN"));
//                    }
                }
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "REGIMEN"));
                titulos.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, true, "TIPO PRESUPUESTO"));

                SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
                DecimalFormat df = new DecimalFormat("###,##0.00");
                pdf = new DocumentoPDF("Certificación PAC", null, seguridadbean.getLogueado().getUserid());
                pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                pdf.agregaParrafo("N° " + detalle.getCertificacion().getNumeropac(), AuxiliarReporte.ALIGN_CENTER, 11, true);
                if (proyecto.getFechautilizacion() != null) {
                    pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    pdf.agregaParrafo("Fecha: Quito, " + sdf.format(proyecto.getFechautilizacion()), AuxiliarReporte.ALIGN_LEFT, 11, false);
                } else {
                    pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
                    pdf.agregaParrafo("Fecha: Quito, " + sdf.format(cert.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
                }

                Codigos textoCodigo = codigosBean.traerCodigo("RECERPAC", "01");
                if (textoCodigo == null) {
                    MensajesErrores.fatal("No configurado Reglamento");
                    return null;
                }
                if (textoCodigo.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra Reglamento");
                    return null;
                }

                pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

                String texto = textoCodigo.getParametros().replace("#detalle#", (getDetalle() != null ? proyecto.getDetalle() : ""));
                texto = texto.replace("#anio#", anio + "");
                pdf.agregaParrafo(texto, AuxiliarReporte.ALIGN_JUSTIFIED, 11, false);
                pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

//                valores.add(new AuxiliarReporte("Integer", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, anio ));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, detalle != null ? traerPartidaProyecto(detalle) : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCpc() != null ? proyecto.getCpc() : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getTipocompra() != null ? consultasPoaBean.traerTipoCompra(proyecto.getTipocompra()) : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getDetalle() != null ? proyecto.getDetalle() : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCantidad() != null ? proyecto.getCantidad().toString() : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getUnidad() != null ? consultasPoaBean.traerTipoUnidad(proyecto.getUnidad()) : ""));
//                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, df.format(proyecto.getValoriva())));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, df.format(traerValorAsignacion(detalle))));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCuatrimestre1() != null ? proyecto.getCuatrimestre1() ? "S" : "" : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCuatrimestre2() != null ? proyecto.getCuatrimestre2() ? "S" : "" : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCuatrimestre3() != null ? proyecto.getCuatrimestre3() ? "S" : "" : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getTipoproducto() != null ? consultasPoaBean.traerTipoProducto(proyecto.getTipoproducto()) : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCatalogoelectronico() != null ? proyecto.getCatalogoelectronico() ? "SI" : "NO" : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getProcedimientocontratacion() != null ? consultasPoaBean.traerTipoProcedimiento(proyecto.getProcedimientocontratacion()) : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getFondobid() != null ? proyecto.getFondobid() ? "SI" : "NO" : ""));
                if (proyecto.getFondobid() != null) {
//                    if (proyecto.getFondobid()) {
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getCodigooperacion() != null ? proyecto.getCodigooperacion() : ""));
                    valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getNumerooperacion() != null ? proyecto.getNumerooperacion() : ""));
//                    }
                }
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getRegimen() != null ? consultasPoaBean.traerTipoRegimen(proyecto.getRegimen()) : ""));
                valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 5, false, proyecto.getPresupuesto() != null ? consultasPoaBean.traerTipoPresupuesto(proyecto.getPresupuesto()) : ""));
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");

            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("CPC: " + (proyecto.getCpc() != null ? proyecto.getCpc() : "") + " - " + consultasPoaBean.traerCPC(proyecto.getCpc()), AuxiliarReporte.ALIGN_LEFT, 8, false);

            pdf.agregaParrafo("\n\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            Codigos cod2 = ejbCodigos.traerCodigo("FIRCERT", "04");
            if (cod2 == null) {
                MensajesErrores.advertencia("No existe codigo de firmas de Certificación PAC");
                return null;
            }
//            String emp = cert.getUsuariopac().substring(0, 10);
//            parametros = new HashMap();
//            parametros.put(";where", "o.entidad.apellidos like :apellidos and  o.cargoactual.organigrama.codigo like 'C.5%'");
//            parametros.put("apellidos", emp.toString() + "%");
//            List<Empleados> lista = ejbEmpleados.encontarParametros(parametros);
//            String cargo = "";
//            if (!lista.isEmpty()) {
//                Empleados e = lista.get(0);
//                cargo = e.getCargoactual().getCargo().getDescripcion();
//            }
            valores = new LinkedList<>();
            List<String> tit = new LinkedList<>();
            tit.add("REVISADO POR:");
            tit.add("CERTIFICADO POR:");
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "\n\n\n\n\n"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "_____________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "_____________________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, cert.getUsuariopac() != null ? cert.getUsuariopac() : ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, cod2.getParametros() != null ? cod2.getParametros() : ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "UNIDAD DE COMPRAS PÚBLICAS"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, cod2.getDescripcion() != null ? cod2.getDescripcion() : ""));
            pdf.agregarTabla(tit, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);
            reportepdf = pdf.traerRecurso();
            ejbCertificacionespoa.edit(detalle.getCertificacion(), seguridadbean.getLogueado().getUserid());
            archivoFirmar = pdf.traerArchivo();
        } catch (IOException | DocumentException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.editar();
        return null;
    }

    public String traerImagen() throws GrabarException {
        try {
            recurso = null;
            if (certificacion != null) {
                if (certificacion.getArchivopac() != null) {
                    setNombreArchivo("Certificacion: " + certificacion.getNumeropac());
                    setTipoArchivo("Certificacion PAC");
                    recurso = new Recurso(Files.readAllBytes(Paths.get(certificacion.getArchivopac())));
                }
            }
        } catch (IOException ex) {
            if (recurso == null) {
                certificacion.setArchivopac(null);
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            MensajesErrores.advertencia("Documento firmado no se encuentra");
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public Impuestos traerImpuesto(double imp) {

        Map parametros = new HashMap();
        parametros.put(";where", "o.porcentaje=:porcentaje and o.activo=true");
        parametros.put("porcentaje", imp);
        try {
            List<Impuestos> listaImp = ejbImpuestos.encontarParametros(parametros);
            for (Impuestos c : listaImp) {
                return c;
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validarCertificacionPAC() {
        if (proyecto.getCpc() == null) {
            MensajesErrores.advertencia("Seleccione CPC");
            return true;
        }
        if (proyecto.getTipocompra() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Compra");
            return true;
        }
        if (proyecto.getDetalle() == null || proyecto.getDetalle().isEmpty()) {
            MensajesErrores.advertencia("Ingresar Detalle");
            return true;
        }
        if (proyecto.getCantidad() == null) {
            MensajesErrores.advertencia("Ingresar Cantidad");
            return true;
        }
        if (proyecto.getUnidad() == null) {
            MensajesErrores.advertencia("Seleccione Unidad");
            return true;
        }
        if (proyecto.getRegimen() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Régimen");
            return true;
        }
        if (proyecto.getTipoproducto() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Producto");
            return true;
        }
        if (proyecto.getProcedimientocontratacion() == null) {
            MensajesErrores.advertencia("Seleccione Procedimiento de Contratación");
            return true;
        }
        if (proyecto.getPresupuesto() == null) {
            MensajesErrores.advertencia("Seleccione Tipo de Presupuesto de Inversión");
            return true;
        }
        if (proyecto.getFondobid()) {
            if (proyecto.getCodigooperacion() == null || proyecto.getCodigooperacion().isEmpty()) {
                MensajesErrores.advertencia("Ingrese código de operación");
                return true;
            }
            if (proyecto.getNumerooperacion() == null || proyecto.getNumerooperacion().isEmpty()) {
                MensajesErrores.advertencia("Ingrese número de operación");
                return true;
            }
        }
//        if (proyecto.getFechautilizacion() == null) {
//            MensajesErrores.advertencia("Seleccione Fecha de Utilización");
//            return true;
//        }
        return false;
    }

    public Double traerValorAsignacion(Detallecertificacionespoa detalle) {
        double valorTotal = 0.00;
        double valorInicial = 0.00;
        double valorReformas = 0.00;
        double valorAsignacion = 0.00;
        try {

//            valorInicial = detalle.getAsignacion().getProyecto().getValoriva().doubleValue();
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion.proyecto=:proyecto and o.asignacion.proyecto.anio=:anio");
            parametros.put("proyecto", detalle.getAsignacion().getProyecto());
            parametros.put("anio", anio);
            parametros.put(";campo", "o.valor");
            valorReformas += ejbReformasPoa.sumarCampo(parametros).doubleValue();
            if (detalle.getAsignacion().getProyecto().getValoriva() == null) {
                detalle.getAsignacion().getProyecto().setValoriva(BigDecimal.ZERO);
            }
            valorAsignacion = detalle.getAsignacion().getProyecto().getValoriva().doubleValue();

            if (detalle.getAsignacion().getProyecto().getImpuesto() == null) {
                detalle.getAsignacion().getProyecto().setImpuesto(BigDecimal.ZERO);
            }
            if (detalle.getAsignacion().getProyecto().getImpuesto().doubleValue() == 0) {
                valorTotal = valorAsignacion + (valorReformas);
            } else {
                double imp = (detalle.getAsignacion().getProyecto().getImpuesto().doubleValue() / 100) + 1;
                valorTotal = valorAsignacion + (valorReformas / imp);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valorTotal;
    }

    public Double traerValorIvaAsignacion(Detallecertificacionespoa detalle) {
        double valorTotal = 0.00;
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto=:proyecto");
        parametros.put("proyecto", proyecto);
        try {
            List<Asignacionespoa> li = ejbAsignaciones.encontarParametros(parametros);
            for (Asignacionespoa a : li) {
                if (proyecto.getImpuesto() == BigDecimal.ZERO) {
                    valorTotal += ((a.getValor().doubleValue() / (impuesto + 100)) * (100)) * proyecto.getCantidad();
                } else {
                    valorTotal += (((a.getValor().doubleValue() / (impuesto + 100)) * (100)) * proyecto.getCantidad())
                            + ((((a.getValor().doubleValue() / (impuesto + 100)) * (100)) * proyecto.getCantidad()) * (proyecto.getImpuesto().doubleValue() / 100));
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valorTotal;
    }

    public String traerPartidaProyecto(Detallecertificacionespoa detalle) {
        String asig = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto=:proyecto");
        parametros.put("proyecto", proyecto);
        try {
            List<Asignacionespoa> li = ejbAsignaciones.encontarParametros(parametros);
            for (Asignacionespoa a : li) {

                double cuadre = Math.round((a.getValor().doubleValue()) * 100);
                double dividido = cuadre / 100;
                BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
                double totalAsignacion = valortotal.doubleValue();

                parametros = new HashMap();
                parametros.put(";campo", "o.valor");
                parametros.put(";where", "o.asignacion=:asignacion");
                parametros.put("asignacion", a);
                double valorReformas = ejbReformasPoa.sumarCampo(parametros).doubleValue();

                double cuadre2 = Math.round((valorReformas) * 100);
                double dividido2 = cuadre2 / 100;
                BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
                double totalReforma = valortotal2.doubleValue();

                if (totalAsignacion + totalReforma != 0) {//Estaba comentado ver xq se comento
                    asig += a.getPartida().getCodigo() + " / ";
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return asig;
    }

    public String dividir1(org.auxiliares.sfccbdmq.Auxiliar a, Detallecertificacionespoa d) {
        if (d == null) {
            return null;
        }
        if (a == null) {
            return null;
        }
        String codigoTemporal = d.getAsignacion().getProyecto().getCodigo().substring(0, a.getIndice());
        Map parametros = new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigoTemporal);
        try {
            List<Proyectospoa> lpro = ejbProyectospoa.encontarParametros(parametros);
            for (Proyectospoa p1 : lpro) {
                return p1.getNombre();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<org.auxiliares.sfccbdmq.Auxiliar> getTitulos() {
        List<org.auxiliares.sfccbdmq.Auxiliar> lista = new LinkedList<>();
        Codigos c = codigosBean.traerCodigo("LONPRO", String.valueOf(anio));
        if (c == null) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        if ((c.getParametros() == null) || (c.getParametros().isEmpty())) {
            MensajesErrores.fatal("Falla en configuración, asegurese que la longitud de los proyectos esta creada en códigos");
            return null;
        }
        // aquí ver el nivel
        String formato = c.getParametros().replace(".", "#");
        String formatoTitulos = c.getDescripcion().replace(".", "#");
        String[] sinpuntos = formato.split("#");
        String[] sinpuntosTitulos = formatoTitulos.split("#");
        int longitudAux = 0;
        for (int i = 0; i < sinpuntos.length - 1; i++) {
            org.auxiliares.sfccbdmq.Auxiliar a = new org.auxiliares.sfccbdmq.Auxiliar();
            String sinBlancos = sinpuntos[i].trim();
            longitudAux += sinBlancos.length();
            a.setTitulo1(sinpuntosTitulos[i]);
            a.setIndice(longitudAux);
            lista.add(a);
        }
        return lista;
    }

    public String getBlancos() {
        return "<br><br> <br> <br> <br>";
    }

    public SelectItem[] getComboUsuariosPac() {

        Map parametros = new HashMap();
        parametros.put(";orden", "o.entidad desc");
        parametros.put(";where", "o.cargoactual.organigrama.codigo like 'C.5%' and o.activo=true and o.fechasalida=null");
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
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String aprobar(Certificacionespoa cert) {
        try {
            fechaDoc = new Date();
            fechapac = new Date();
            usuariopac = "";
            obsRechazo = "";
            imprimir = false;
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> list = ejbDetalles.encontarParametros(parametros);
            if (!list.isEmpty()) {
                for (Detallecertificacionespoa dc : list) {
                    if (dc.getAsignacion().getProyecto().getCpc() != null) {

                    }
                }
                detalle = list.get(0);
            }
            this.proyecto = detalle.getAsignacion().getProyecto();
            this.certificacion = cert;
            if (certificacion.getImpresopac()) {
                MensajesErrores.advertencia("La Certificación PAC ya tiene responsable");
                return null;
            }
            if (certificacion.getAnulado()) {
                MensajesErrores.advertencia("La Certificación POA anulada");
                return null;
            }
            fechapac = certificacion.getFechapac();
            if (proyecto.getImpuesto() != null) {
                impuesto = proyecto.getImpuesto().doubleValue();
            } else {
                impuesto = 0.00;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.editar();
        return null;
    }

    public String rechazar(Certificacionespoa cert) {
        try {
            certificacion = cert;
            fechaDoc = new Date();
            fechapac = new Date();
            usuariopac = "";
            obsRechazo = "";
            Map parametros = new HashMap();
//            parametros.put(";where", "o.pacpoa=:pacpoa and o.anulado=false");
//            parametros.put("pacpoa", cert.getId());
//            List<Certificaciones> lista = ejbCertificacionesFinancieras.encontarParametros(parametros);
//            if (!lista.isEmpty()) {
//                MensajesErrores.advertencia("Certificación aprobada en Presupuesto");
//                return null;
//            }
//            parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> list = ejbDetalles.encontarParametros(parametros);
            if (!list.isEmpty()) {
                for (Detallecertificacionespoa dc : list) {
                    if (dc.getAsignacion().getProyecto().getCpc() != null) {

                    }
                }
                detalle = list.get(0);
            }
            this.proyecto = detalle.getAsignacion().getProyecto();
            this.certificacion = detalle.getCertificacion();
//            if (certificacion.getAnulado()) {
//                MensajesErrores.advertencia("La Certificación POA anulada");
//                return null;
//            }
            fechapac = certificacion.getFechapac();
            if (proyecto.getImpuesto() != null) {
                impuesto = proyecto.getImpuesto().doubleValue();
            } else {
                impuesto = 0.00;
            }
        } catch (ConsultarException ex) {
//        } catch (ConsultarException | org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.editar();
        return null;
    }

    public String grabarAprobacion() {
        try {
            if (fechapac == null) {
                MensajesErrores.advertencia("Ingresar Fecha");
                return null;
            }
            if (usuariopac == null) {
                MensajesErrores.advertencia("Seleccione Usuario");
                return null;
            }
//            if (certificacion.getImpresopac()) {
//                MensajesErrores.advertencia("La Certificación PAC ya aprobada");
//                return null;
//            }
//            if (certificacion.getAnulado()) {
//                MensajesErrores.advertencia("La Certificación POA anulada");
//                return null;
//            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            for (Detallecertificacionespoa dp : lista) {
                //Numero pac desde codigos
                Codigos numero = codigosBean.traerCodigo("NUM", "01");
                if (numero == null) {
                    MensajesErrores.fatal("No configurado el número para la Certificación PAC");
                    return null;
                }
                if (numero.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }

                Integer num = Integer.parseInt(numero.getParametros());
                Integer nume = num + 1;
                if (dp.getCertificacion().getNumeropac() == null) {
                    String ordinal = String.format("%03d", nume);
                    dp.getCertificacion().setNumeropac(ordinal + "-PAC-DA-CBDMQ-" + anio);
                    numero.setParametros(nume + "");
                    ejbCodigosFacade.edit(numero, seguridadbean.getLogueado().getUserid());
                }
                dp.getCertificacion().setImpresopac(Boolean.TRUE);
                dp.getCertificacion().setFechapac(fechapac);
                dp.getCertificacion().setFechadocumento(fechaDoc);
                dp.getCertificacion().setUsuariopac(usuariopac);
                ejbCertificacionespoa.edit(dp.getCertificacion(), seguridadbean.getLogueado().getUserid());

                parametros = new HashMap();
                parametros.put(";where", "o.asignacion.proyecto=:proyecto");
                parametros.put("proyecto", proyecto);
                List<Detallecertificacionespoa> listaP = ejbDetalles.encontarParametros(parametros);
                for (Detallecertificacionespoa p : listaP) {
                    p.getAsignacion().getProyecto().setFechautilizacion(fechapac);
                    ejbProyectospoa.edit(p.getAsignacion().getProyecto(), seguridadbean.getLogueado().getUserid());
                }

                // financiero**********************************************************************
                // pasar al lado financiero
                // Organigrama
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", dp.getCertificacion().getDireccion());
                List<Organigrama> listaOrg = ejbOrganigrama.encontarParametros(parametros);
                Organigrama o = null;
                for (Organigrama o1 : listaOrg) {
                    o = o1;
                }
                Certificaciones certFin;
                parametros = new HashMap();
                parametros.put(";where", "o.pacpoa=:pacpoa");
                parametros.put("pacpoa", certificacion.getId());
                List<Certificaciones> listaCretFin = ejbCertificacionesFinancieras.encontarParametros(parametros);
                if (listaCretFin.isEmpty()) {
                    certFin = new Certificaciones();
                    certFin.setAnio(dp.getCertificacion().getAnio());
                    certFin.setAnulado(false);
                    certFin.setFecha(dp.getCertificacion().getFecha());
                    certFin.setMemo(dp.getCertificacion().getMemo());
                    certFin.setMotivo(dp.getCertificacion().getMotivo());
                    certFin.setRoles(dp.getCertificacion().getRoles());
                    certFin.setPacpoa(dp.getCertificacion().getId());
                    certFin.setDireccion(o);
                    certFin.setImpreso(false);
                    ejbCertificacionesFinancieras.create(certFin, seguridadbean.getLogueado().getUserid());
                } else {
                    certFin = listaCretFin.get(0);
                }

                if (dp.getId() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", dp.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", dp.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }
                    // Buscar la fuente
                    Codigos co = null;
                    List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                    for (Codigos co1 : listaCodigos) {
                        if (co1.getCodigo().equals(dp.getAsignacion().getFuente())) {
                            co = co1;
                        }
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
//                    d1.setCertificacion(certFin);
                    d1.setAsignacion(af);
                    d1.setCertificacion(certFin);
                    d1.setValor(dp.getValor());
                    d1.setFecha(certFin.getFecha());
                    ejbDetalleFinancieras.create(d1, seguridadbean.getLogueado().getUserid());
                }
                // fin financiero
                // fin financiero********************************************************************
            }

        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.cancelar();
        return null;
    }

    public String traerProyecto(Certificacionespoa cert) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            String proyectos = "";
            for (Detallecertificacionespoa detallePoa : lista) {
                if (detallePoa.getAsignacion().getProyecto().getCpc() != null) {
                    proyectos = detallePoa.getAsignacion().getProyecto().getCodigo();
                }
            }
            return proyectos;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    public String traerProyectoDetalle(Certificacionespoa cert) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            String proyectos = "";
            for (Detallecertificacionespoa detallePoa : lista) {
                if (detallePoa.getAsignacion().getProyecto().getCpc() != null) {
                    proyectos = detallePoa.getAsignacion().getProyecto().getDetalle();
                }
            }
            return proyectos;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    public String grabarRechazo() {
        try {

            //Solo se anula pac
            if (usuariopac == null) {
                MensajesErrores.advertencia("Seleccione Usuario");
                return null;
            }
            if (obsRechazo == null || obsRechazo.isEmpty()) {
                MensajesErrores.advertencia("Es necesario observación");
                return null;
            }
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.pacpoa=:pacpoa and o.impreso=true");
//            parametros.put("pacpoa", certificacion.getId());
//            List<Certificaciones> lista = ejbCertificacionesFinancieras.encontarParametros(parametros);
//            if (!lista.isEmpty()) {
//                MensajesErrores.advertencia("Certificación aprobada en Presupuesto");
//                return null;
//            } else {
//                parametros = new HashMap();
//                parametros.put(";where", "o.pacpoa=:pacpoa");
//                parametros.put("pacpoa", certificacion.getId());
//                List<Certificaciones> lista2 = ejbCertificacionesFinancieras.encontarParametros(parametros);
//                if (!lista2.isEmpty()) {
//                    Certificaciones c = lista2.get(0);
//                    c.setAnulado(Boolean.TRUE);
//                    c.setFechaanulado(c.getFecha());
//                    ejbCertificacionesFinancieras.edit(c, seguridadbean.getLogueado().getUserid());
//                    parametros = new HashMap();
//                    parametros.put(";where", "o.certificacion=:certificacion");
//                    parametros.put("certificacion", c);
//                    List<Detallecertificaciones> lista3 = ejbDetalleFinancieras.encontarParametros(parametros);
//                    if (!lista3.isEmpty()) {
//                        for (Detallecertificaciones dc : lista3) {
//                            Detallecertificaciones dcNuevo = new Detallecertificaciones();
//                            dcNuevo = dc;
//                            double valor = dc.getValor().doubleValue() * -1;
//                            dcNuevo.setValor(new BigDecimal(valor));
//                            ejbDetalleFinancieras.create(dcNuevo, seguridadbean.getLogueado().getUserid());
//                        }
//                    }
//                }
//            }
            certificacion.setObsrechazadopac(obsRechazo);
            certificacion.setUsuariopac(usuariopac);
            certificacion.setRechazadopac(Boolean.TRUE);
//            certificacion.setAnulado(Boolean.TRUE);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
//            parametros = new HashMap();
//            parametros.put(";where", "o.certificacion=:certificacion");
//            parametros.put("certificacion", certificacion);
//            List<Detallecertificacionespoa> lista4 = ejbDetalles.encontarParametros(parametros);
//            if (!lista4.isEmpty()) {
//                for (Detallecertificacionespoa dc : lista4) {
//                    Detallecertificacionespoa dcNuevo2 = new Detallecertificacionespoa();
//                    dcNuevo2 = dc;
//                    double valor = dc.getValor().doubleValue() * -1;
//                    dcNuevo2.setValor(new BigDecimal(valor));
//                    ejbDetalles.create(dcNuevo2, seguridadbean.getLogueado().getUserid());
//                }
//            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.cancelar();
        return null;
    }

    public String generar(Certificacionespoa cert) {
        certificacion = cert;
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", cert);
        List<Detallecertificacionespoa> list;
        try {
            list = ejbDetalles.encontarParametros(parametros);

            if (!list.isEmpty()) {
                detalle = list.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        fechaDoc = new Date();
        fechapac = new Date();
        usuariopac = "";
        obsRechazo = "";
        formularioImpresion.insertar();
        return null;
    }

    public String grabarGenerar() {
        try {
            if (fechapac == null) {
                MensajesErrores.advertencia("Ingresar Fecha");
                return null;
            }
            if (usuariopac == null) {
                MensajesErrores.advertencia("Seleccione Usuario");
                return null;
            }
            Codigos numero = codigosBean.traerCodigo("NUM", "01");
            if (numero == null) {
                MensajesErrores.fatal("No configurado el número para la Certificación PAC");
                return null;
            }
            if (numero.getParametros() == null) {
                MensajesErrores.advertencia("No se encuentra numeración");
                return null;
            }

            Integer num = Integer.parseInt(numero.getParametros());
            Integer nume = num + 1;
            String ordinal = String.format("%03d", nume);
            String nuevoNumero = (ordinal + "-PAC-DA-CBDMQ-" + anio);
            numero.setParametros(nume + "");
            ejbCodigosFacade.edit(numero, seguridadbean.getLogueado().getUserid());

            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            Certificacionespoa certNueva = new Certificacionespoa();
            certNueva.setFecha(certificacion.getFecha());
            certNueva.setImpreso(Boolean.TRUE);
            certNueva.setAnulado(Boolean.FALSE);
            certNueva.setRoles(Boolean.FALSE);
            certNueva.setNumero(certificacion.getNumero());
            certNueva.setFechapac(fechapac);
            certNueva.setUsuariopac(usuariopac);
            certNueva.setImpresopac(Boolean.TRUE);
            certNueva.setNumeropac(nuevoNumero);
            certNueva.setNumeropoa(certificacion.getNumeropoa());
            certNueva.setFechadocumento(fechaDoc);
            certNueva.setRechazadopac(Boolean.FALSE);
            certNueva.setImpreso(certificacion.getImpreso());
            certNueva.setAnio(certificacion.getAnio());
            certNueva.setGenerapac(certificacion.getGenerapac());
            certNueva.setDireccion(certificacion.getDireccion());
            ejbCertificacionespoa.create(certNueva, seguridadbean.getLogueado().getUserid());

            for (Detallecertificacionespoa dp : lista) {
                Detallecertificacionespoa dcNueva = new Detallecertificacionespoa();
                dcNueva.setAsignacion(dp.getAsignacion());
                dcNueva.setValor(dp.getValor());
                dcNueva.setCertificacion(certNueva);
                dcNueva.setFecha(certNueva.getFecha());
                ejbDetalles.create(dcNueva, seguridadbean.getLogueado().getUserid());
                dp.setFecha(null);
                ejbDetalles.edit(dp, seguridadbean.getLogueado().getUserid());

            }
            parametros = new HashMap();
            parametros.put(";where", "o.pacpoa=:pacpoa and o.impreso=true and o.anulado=false");
            parametros.put("pacpoa", certificacion.getId());
            List<Certificaciones> listaVer = ejbCertificacionesFinancieras.encontarParametros(parametros);
            if (listaVer.isEmpty()) {

                // financiero**********************************************************************
                // pasar al lado financiero
                // Organigrama
                parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", certNueva.getDireccion());
                List<Organigrama> listaOrg = ejbOrganigrama.encontarParametros(parametros);
                Organigrama o = null;
                for (Organigrama o1 : listaOrg) {
                    o = o1;
                }
                Certificaciones certFin;
                parametros = new HashMap();
                parametros.put(";where", "o.pacpoa=:pacpoa");
                parametros.put("pacpoa", certNueva.getId());
                List<Certificaciones> listaCretFin = ejbCertificacionesFinancieras.encontarParametros(parametros);
                if (listaCretFin.isEmpty()) {
                    certFin = new Certificaciones();
                    certFin.setAnio(certNueva.getAnio());
                    certFin.setAnulado(false);
                    certFin.setFecha(certNueva.getFecha());
                    certFin.setMemo(certNueva.getMemo());
                    certFin.setMotivo(certNueva.getMotivo());
                    certFin.setRoles(certNueva.getRoles());
                    certFin.setPacpoa(certNueva.getId());
                    certFin.setDireccion(o);
                    certFin.setImpreso(false);
                    ejbCertificacionesFinancieras.create(certFin, seguridadbean.getLogueado().getUserid());
                } else {
                    certFin = listaCretFin.get(0);
                }

                for (Detallecertificacionespoa dp : lista) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                    parametros.put("codigo", dp.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", anio);
                    List<Proyectos> listap = ejbProyectosFinanciera.encontarParametros(parametros);
                    Proyectos p = null;
                    for (Proyectos p1 : listap) {
                        p = p1;
                    }
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo");
                    parametros.put("codigo", dp.getAsignacion().getPartida().getCodigo());
                    List<Clasificadores> listac = ejbPartidasFinanciera.encontarParametros(parametros);
                    Clasificadores c = null;
                    for (Clasificadores c1 : listac) {
                        c = c1;
                    }
                    // Buscar la fuente
                    Codigos co = null;
                    List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                    for (Codigos co1 : listaCodigos) {
                        if (co1.getCodigo().equals(dp.getAsignacion().getFuente())) {
                            co = co1;
                        }
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
                    Detallecertificaciones d1 = new Detallecertificaciones();
                    d1.setAsignacion(af);
                    d1.setCertificacion(certFin);
                    d1.setValor(dp.getValor());
                    d1.setFecha(certFin.getFecha());
                    ejbDetalleFinancieras.create(d1, seguridadbean.getLogueado().getUserid());
                }
            } else {
                Certificaciones c = listaVer.get(0);
                c.setPacpoa(certNueva.getId());
                ejbCertificacionesFinancieras.edit(c, seguridadbean.getLogueado().getUserid());
            }
            // fin financiero
            // fin financiero********************************************************************

        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImpresion.cancelar();

        return null;
    }

    public String firma() {
        getFormularioFirma().insertar();
        return null;
    }

    public String grabarFirma() {
        try {
            FirmadorPDF firmador = new FirmadorPDF("certificacionPac-" + this.certificacion.getId(), getClave(), seguridadbean.getLogueado().getPin(), configuracionBean.getConfiguracion().getDirectorio(), archivoFirmar, pdf.getNumeroPaginas());
            boolean archivoOk = false;
            if (certificacion.getArchivopac() != null) {
                File existente = new File(certificacion.getArchivopac());
                if (existente.exists()) {
                    byte[] docByteArry = FileUtils.fileConvertToByteArray(existente);
                    if (docByteArry.length != 0) {
                        archivoOk = true;
                    }
                }
            }

            if (!archivoOk) {
                firmador.firmar();
                certificacion.setArchivopac(configuracionBean.getConfiguracion().getDirectorio() + "/firmados/" + "certificacionPac-" + certificacion.getId() + ".pdf");
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
                reporte(certificacion);
            } else {
                firmador.editarFirma();
                reporte(certificacion);
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPacBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        setClave(null);
//        formularioImpresion.cancelar();
        formularioFirma.cancelar();
        return null;
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
     * @return the formularioCertificacion
     */
    public Formulario getFormularioCertificacion() {
        return formularioCertificacion;
    }

    /**
     * @param formularioCertificacion the formularioCertificacion to set
     */
    public void setFormularioCertificacion(Formulario formularioCertificacion) {
        this.formularioCertificacion = formularioCertificacion;
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
     * @return the impuesto
     */
    public Double getImpuesto() {
        return impuesto;
    }

    /**
     * @param impuesto the impuesto to set
     */
    public void setImpuesto(Double impuesto) {
        this.impuesto = impuesto;
    }

    /**
     * @return the proyecto
     */
    public Proyectospoa getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(Proyectospoa proyecto) {
        this.proyecto = proyecto;
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
     * @return the usuariopac
     */
    public String getUsuariopac() {
        return usuariopac;
    }

    /**
     * @param usuariopac the usuariopac to set
     */
    public void setUsuariopac(String usuariopac) {
        this.usuariopac = usuariopac;
    }

    /**
     * @return the fechapac
     */
    public Date getFechapac() {
        return fechapac;
    }

    /**
     * @param fechapac the fechapac to set
     */
    public void setFechapac(Date fechapac) {
        this.fechapac = fechapac;
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
     * @return the listacCertificacionesPac
     */
    public LazyDataModel<Certificacionespoa> getListacCertificacionesPac() {
        return listacCertificacionesPac;
    }

    /**
     * @param listacCertificacionesPac the listacCertificacionesPac to set
     */
    public void setListacCertificacionesPac(LazyDataModel<Certificacionespoa> listacCertificacionesPac) {
        this.listacCertificacionesPac = listacCertificacionesPac;
    }

    /**
     * @return the numeroControlPac
     */
    public String getNumeroControlPac() {
        return numeroControlPac;
    }

    /**
     * @param numeroControlPac the numeroControlPac to set
     */
    public void setNumeroControlPac(String numeroControlPac) {
        this.numeroControlPac = numeroControlPac;
    }

    /**
     * @return the fechaDoc
     */
    public Date getFechaDoc() {
        return fechaDoc;
    }

    /**
     * @param fechaDoc the fechaDoc to set
     */
    public void setFechaDoc(Date fechaDoc) {
        this.fechaDoc = fechaDoc;
    }

    /**
     * @return the obsRechazo
     */
    public String getObsRechazo() {
        return obsRechazo;
    }

    /**
     * @param obsRechazo the obsRechazo to set
     */
    public void setObsRechazo(String obsRechazo) {
        this.obsRechazo = obsRechazo;
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
     * @return the archivoFirmar
     */
    public File getArchivoFirmar() {
        return archivoFirmar;
    }

    /**
     * @param archivoFirmar the archivoFirmar to set
     */
    public void setArchivoFirmar(File archivoFirmar) {
        this.archivoFirmar = archivoFirmar;
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
