/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.pacpoa.sfccbdmq.*;
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
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.errores.sfccbdmq.BorrarException;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CargosxorganigramaFacade;
import org.beans.sfccbdmq.HistorialcargosFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Historialcargos;
import org.entidades.sfccbdmq.Organigrama;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.presupuestos.sfccbdmq.CertificacionesBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.utilitarios.firma.FileUtils;
import org.utilitarios.firma.FirmadorPDF;
import org.wscliente.sfccbdmq.AuxiliarEntidad;

/**
 *
 * @author luis
 */
@ManagedBean(name = "certificacionPoa")
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
    private List<Proyectospoa> listaProyecto;
    private List<Asignacionespoa> listaAsignacionCertificar;
    private List<Detallecertificacionespoa> detallesb;
    private LazyDataModel<Certificacionespoa> certificaciones;
    private List<Certificacionespoa> certificacionesCompromiso;
    private Detallecertificacionespoa detalle;
    private String numeroControl;
    private String motivo;
    private String codigo;
    private String partida;
    private String direccion;
    private String estado;
    private String fuente;
    private String observacionAdjudicacion;
    private String observacionAprobacion;
    private String observacionLiquidacion;
    private boolean imprimir;
    private Integer numeroDocumento;
    private String impresas = "TRUE";
    private String liquidadas = "FALSE";
    private String anuladas = "FALSE";
    private Recurso reportepdf;
    private Recurso reporteSolicitudpdf;
    private DocumentoPDF pdf;
    private Recurso recurso;
    private int anio;
    private Date desde;
    private Date hasta;
    private Date vigente;
    private Proyectospoa proyectoSeleccionado;
    private Integer tipoBuscar = 2;
    private String codigoOrganigrama;
    private Empleados empleado;
    private String cerfificaQue;
    private Boolean puedeEditar = false;
    private boolean dga;
    private String cargo = "";
    private String clave;
    private File archivoFirmar;
    private String nombreArchivo;
    private String tipoArchivo;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioSolicitud = new Formulario();
    private Formulario formularioAprobar = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioAsignacion = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioConfirmacion = new Formulario();
    private Formulario formularioDetalleNuevo = new Formulario();
    private Formulario formularioFirma = new Formulario();

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
    private CabeceraempleadosFacade ejbCabemp;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private HistorialcargosFacade ejbHistorialcargos;
    @EJB
    private CargosxorganigramaFacade ejbcargCargosxorganigrama;

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
        vigente = getConfiguracionBean().getConfiguracion().getPvigentepresupuesto();
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        cargo = null;
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);
        direccion = null;
        try {
            if (seguridadbean.getLogueado().getEmpleados() != null) {
                Organigrama oDireccion = null;
                Map parametros = new HashMap();
                parametros.put(";where", "o.empleado=:empleado and o.activo=true and o.desde between :desde and :hasta");
                parametros.put("empleado", seguridadbean.getLogueado().getEmpleados());
                parametros.put("desde", getConfiguracionBean().getConfiguracion().getPinicialpresupuesto());
                parametros.put("hasta", new Date());
                List<Historialcargos> listaBuscar = ejbHistorialcargos.encontarParametros(parametros);
                if (!listaBuscar.isEmpty()) {
                    Historialcargos hc = listaBuscar.get(0);
                    oDireccion = seguridadbean.traerDireccion(hc.getCargo().getOrganigrama().getCodigo());
                    if (oDireccion != null) {
                        // solo los direcctores pueden aprobar la solicitud creada
                        puedeEditar = true;
                        direccion = oDireccion.getCodigo();
                        cargo = hc.getCargo().getCargo().getNombre();
                    }
                } else {
                    if (seguridadbean.getLogueado().getEmpleados().getCargoactual() != null) {
                        oDireccion = seguridadbean.traerDireccion(seguridadbean.getLogueado().getEmpleados().getCargoactual().getOrganigrama().getCodigo());
                        if (oDireccion != null) {
                            // solo los direcctores pueden aprobar la solicitud creada
                            puedeEditar = true;
                            direccion = oDireccion.getCodigo();
                            cargo = seguridadbean.getLogueado().getEmpleados().getCargoactual().getCargo().getNombre();
                        }
                    }
                }
                if (direccion == null || direccion.isEmpty()) {
                    Organigrama o = seguridadbean.traerDireccion(seguridadbean.getLogueado().getEmpleados().getCargoactual().getOrganigrama().getCodigo().substring(0, 3));
                    if (o != null) {
                        // direccion
                        direccion = o.getCodigo();
                        puedeEditar = false;

                    }
                }
                if (direccion.equals("C")) {
                    dga = true;
                } else {
                    dga = false;
                }
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (seguridadbean.traerEmpleado() != null) {
            if (seguridadbean.traerEmpleado() != null) {
                empleado = seguridadbean.traerEmpleado();
            } else {
                empleado = null;
            }
        }
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
                String where = "o.roles=false " + " and o.fecha between :desde and :hasta";
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
                if (numeroControl != null && !numeroControl.isEmpty()) {
                    where += " and o.numero=:numero";
                    parametros.put("numero", getNumeroControl());
                }
                if (direccion != null && !direccion.isEmpty()) {
                    if (!direccion.equals("C")) {
                        where += " and o.direccion=:direccion";
                        parametros.put("direccion", getDireccion());
                    }
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
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso para solcitar una certificación");
            return null;
        }
        if (direccion == null || direccion.isEmpty()) {
            MensajesErrores.advertencia("Seleccione una dirección");
            return null;
        }
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
//        certificacion.setAnio(anio); el año se pone cuando apruebe el director
        certificacion.setFecha(new Date());
        detalles = new LinkedList<>();
        detallesb = new LinkedList<>();
        imagenesPoaBean.setListaDocumentos(new LinkedList<>());
        formulario.insertar();
        return null;
    }

    public String modificar(Certificacionespoa certificacion) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso para solcitar una certificación");
            return null;
        }
        formularioClasificador.cancelar();
        this.certificacion = certificacion;
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

    public String eliminar(Certificacionespoa certificacion) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso para solcitar una certificación");
            return null;
        }
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

    public String insertar() {
        if ((detalles == null) || (detalles.isEmpty())) {
            MensajesErrores.advertencia("Es necesario al menos una cuenta");
            return null;
        }
        if ((certificacion.getMemo() == null) || (certificacion.getMemo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario N° de memorando");
            return null;
        }
//        if ((certificacion.getNumero()== null) || (certificacion.getNumero().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario N° de certificación");
//            return null;
//        }
        if ((certificacion.getMotivo() == null) || (certificacion.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario motivo de certificación");
            return null;
        }
        if ((certificacion.getFecha() == null) || (certificacion.getFecha().before(vigente))) {
            MensajesErrores.advertencia("Fecha de certificación debe ser mayor a periodo vigente");
            return null;
        }

        try {
            // nuemrar
//            for (Detallecertificacionespoa d : detalles) {
//                String mensaje = "En las solicitudes de certificación con números de control: ";
//                String numeros = "";
//                Map parametros = new HashMap();
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
//                }
//            }
            confirmarIngresar();
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");
//        } catch (ConsultarException | InsertarException | IOException | GrabarException ex) {
        } catch (InsertarException | IOException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String confirmarIngresar() throws InsertarException, IOException, GrabarException {

        try {
            certificacion.setEstado("Enviar");
            certificacion.setCertificaque(cerfificaQue);
            ejbCertificacionespoa.create(certificacion, seguridadbean.getLogueado().getUserid());
            for (Detallecertificacionespoa d : detalles) {
                d.setCertificacion(certificacion);
                d.setFecha(certificacion.getFecha());
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

            Codigos textoCodigo = getCodigosBean().traerCodigo("MAIL", "SOLCERTPOA");
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            if (textoCodigo.getDescripcion() == null) {
                MensajesErrores.fatal("No configurado email en codigos");
                return null;
            }
            if (textoCodigo.getParametros() == null) {
                MensajesErrores.fatal("No configurado testo de envio de email en codigos");
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fecha = sdf.format(new Date());
            String texto = textoCodigo.getParametros().replace("#numero#", certificacion.getMemo());
            texto = texto.replace("#direccion#", consultasPoaBean.traerDireccion(certificacion.getDireccion()));
            texto = texto.replace("#nombre#", seguridadbean.getLogueado().toString());
            texto = texto.replace("#fecha#", fecha);

            String formato = textoCodigo.getDescripcion().replace(";", "#");
            String[] sinpuntos = formato.split("#");

            for (int i = 0; i < sinpuntos.length; i++) {
                String correos = sinpuntos[i];
                ejbCorreo.enviarCorreo(correos, "Solicitud de Certificación POA", texto);
            }

            Empleados emple = seguridadbean.getLogueado().getEmpleados();
            String e = seguridadbean.getLogueado().getEmail();
            if (emple != null) {
                Empleados d = seguridadbean.traerJefe(emple);
                ejbCorreo.enviarCorreo(d != null ? d.getEntidad().getEmail() : " ", "Solicitud de Certificación POA", texto);
            }
            ejbCorreo.enviarCorreo(e != null ? e : " ", "Solicitud de Certificación POA", texto);

        } catch (InsertarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imagenesPoaBean.setCertificacion(certificacion);
        imagenesPoaBean.insertarDocumentos("certificaciones");
//        MensajesErrores.informacion("se creo certificación con número de control: " + certificacion.getNumero());
        MensajesErrores.informacion("se creo certificación con número de solicitud: " + certificacion.getId());
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
//            if ((certificacion.getNumero() == null) || (certificacion.getNumero().isEmpty())) {
//                MensajesErrores.advertencia("Es necesario N° de certificación");
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
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso = true");
            parametros.put("asignacion", asignacion);
            int total = ejbDetalles.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            certificacion.setCertificaque(cerfificaQue);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            for (Detallecertificacionespoa d : detalles) {
                d.setCertificacion(certificacion);
                d.setFecha(certificacion.getFecha());
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
                    + "Certificación creada: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
            imagenesPoaBean.setCertificacion(certificacion);
            imagenesPoaBean.insertarDocumentos("certificaciones");
        } catch (GrabarException | ConsultarException | InsertarException | BorrarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
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
        formularioDetalleNuevo.editar();
        asignacion = detalle.getAsignacion();
        codigo = asignacion.getPartida().getCodigo();
        return null;
    }

    public String borraDetalle() {
        int fila = formularioDetalle.getFila().getRowIndex();
        this.detalle = detalles.get(fila);
        formularioDetalleNuevo.setIndiceFila(fila);
        formularioClasificador.cancelar();
        formularioDetalleNuevo.eliminar();
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
        detalles.add(detalle);
        String tipo = detalle.getAsignacion().getProyecto().getTipocompra();
        if (cerfificaQue == null || cerfificaQue.isEmpty()) {
            cerfificaQue = tipo != null ? (tipo + " - " + detalle.getAsignacion().getProyecto().getNombre()) : detalle.getAsignacion().getProyecto().getNombre();
        } else {
            cerfificaQue = cerfificaQue + "\n" + (tipo != null ? (tipo + " - " + detalle.getAsignacion().getProyecto().getNombre()) : detalle.getAsignacion().getProyecto().getNombre());
        }
//        formularioClasificador.cancelar();
//        formularioDetalle.cancelar();
        formularioDetalleNuevo.cancelar();
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
        formularioDetalleNuevo.cancelar();
        return null;
    }

    public String eliminarDetalle() {
        detallesb.add(detalle);
        detalles.remove(formularioDetalle.getIndiceFila());
        formularioClasificador.cancelar();
        formularioDetalleNuevo.cancelar();
        return null;
    }

    public double calculaTotalCertificacionespoa(Asignacionespoa a) {
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
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public Detallecertificacionespoa traer(Integer id) throws ConsultarException {
        return ejbDetalles.find(id);
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
        String where = "o.proyecto.anio=:anio " + "and o.partida.ingreso=false and upper(o.proyecto.direccion)=:direccion";
//        parametros.put("anio", proyectoPoaBean.getAnio());
        parametros.put("anio", anio);
        parametros.put("direccion", direccion);

        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.proyecto.codigo) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.proyecto.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.proyecto.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
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
            String aComparar = tipoBuscar == 1 ? a.toStringCodigo2() : a.toStringNombre2();
            if (aComparar.compareToIgnoreCase(newWord) == 0) {
                asignacion = a;
            }
        }
    }

    public void proyectoChangeEventHandler(TextChangeEvent event) {
        //clasificador = null;
        proyectoSeleccionado = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            proyectoSeleccionado = null;
        }
        listaProyecto = new LinkedList<>();
        //traer la consulta 
        Map parametros = new HashMap();
        String where = "o.anio=:anio and upper(o.direccion)=:direccion and o.activo=true";
//        parametros.put("anio", proyectoPoaBean.getAnio());
        parametros.put("anio", anio);
        parametros.put("direccion", direccion);

        if (tipoBuscar == 1) {//codigo
            where += " and  upper(o.codigo) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.codigo");
        } else if (tipoBuscar == 2) {//nombre
            where += " and  upper(o.nombre) like :codigo";
            parametros.put("codigo", "%" + newWord.toUpperCase() + "%");
            parametros.put(";orden", " o.nombre");
        }
        parametros.put(";where", where);
        listaProyecto = null;
        //Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", 15);
        try {
            listaProyecto = ejbProyectos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaProyecto(ValueChangeEvent event) {
        proyectoSeleccionado = null;
        if (listaProyecto == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Proyectospoa a : listaProyecto) {
            String aComparar = tipoBuscar == 1 ? a.toString() : a.toStringNombre();
            if (aComparar.compareToIgnoreCase(newWord) == 0) {
                proyectoSeleccionado = a;
            }
        }
        if (proyectoSeleccionado != null) {
            llenarAsignacion();
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

    public String reporte(Certificacionespoa cert) {
        certificacion = cert;
        if (certificacion.getAnio() == null) {
            MensajesErrores.advertencia("La certificación debe estar aprobada antes de imprimir");
            return null;
        }
        formularioImpresion.insertar();
        imprimir = certificacion.getImpreso();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
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
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FUENTE DE FINANCIAMIENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "VALOR INCLUIDO IVA"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            pdf = new DocumentoPDF("CERTIFICACIÓN  POA - PRESUPUESTARIA", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("N°: " + certificacion.getNumero() + "-CPOA-" + certificacion.getAnio(), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(certificacion.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Se certifica que:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            Detallecertificacionespoa d2 = detalles.get(0);
            if (d2 != null) {
                String tipo = d2.getAsignacion().getProyecto().getTipocompra();
//                pdf.agregaParrafo((d2.getAsignacion().getProyecto().getTipocompra() != null ? d2.getAsignacion().getProyecto().getTipocompra() : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase(), AuxiliarReporte.ALIGN_CENTER, 11, false);
                pdf.agregaParrafo((certificacion.getCertificaque() != null ? (certificacion.getCertificaque()) : ((tipo != null ? tipo : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase())), AuxiliarReporte.ALIGN_CENTER, 11, false);
            }
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Solicitado en memorando Nº " + certificacion.getMemo() + ".", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Se encuentra en el PLAN OPERATIVO ANUAL " + certificacion.getAnio() + ", según se detalla a continuación:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, proyectoPoaBean.dividir1(a, d.getAsignacion().getProyecto())));
                    }
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
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
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
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String aprobar(Certificacionespoa cert) {
        certificacion = cert;
        if (certificacion.getAnio() != null) {
            MensajesErrores.advertencia("Certificacion ya aprobada");
            return null;
        }
        certificacion.setAnio(anio);
        formularioAprobar.insertar();
        imprimir = certificacion.getImpreso();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
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
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FUENTE DE FINANCIAMIENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "VALOR INCLUIDO IVA"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            pdf = new DocumentoPDF("CERTIFICACIÓN  POA - PRESUPUESTARIA", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("N°: " + certificacion.getNumero() + "-CPOA-" + certificacion.getAnio(), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(certificacion.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Se certifica que:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            Detallecertificacionespoa d2 = detalles.get(0);
            if (d2 != null) {
                String tipo = d2.getAsignacion().getProyecto().getTipocompra();
//                pdf.agregaParrafo((d2.getAsignacion().getProyecto().getTipocompra() != null ? d2.getAsignacion().getProyecto().getTipocompra() : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase(), AuxiliarReporte.ALIGN_CENTER, 11, false);
                pdf.agregaParrafo((certificacion.getCertificaque() != null ? (certificacion.getCertificaque()) : ((tipo != null ? tipo : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase())), AuxiliarReporte.ALIGN_CENTER, 11, false);
            }
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Solicitado en memorando Nº " + certificacion.getMemo() + ".", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Se encuentra en el PLAN OPERATIVO ANUAL " + certificacion.getAnio() + ", según se detalla a continuación:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, proyectoPoaBean.dividir1(a, d.getAsignacion().getProyecto())));
                    }
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
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            List<Codigos> aux = codigosBean.traerCodigoMaestro("DIREC");
            for (Codigos ca : aux) {
                if (ca.getCodigo().equals("CRP")) {
                    AuxiliarEntidad o = consultasPoaBean.traerEmpleadoCargo(ca.getDescripcion());
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, o.getNombres() + " " + o.getApellidos()));
                }
            }
            for (Codigos cc : aux) {
                if (cc.getCodigo().equals("CAP")) {
                    AuxiliarEntidad o = consultasPoaBean.traerEmpleadoCargo(cc.getDescripcion());
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, o.getNombres() + " " + o.getApellidos() + "\n" + "EXPERTO PRINCIPAL EN PLANIFICACIÓN"));
                }
            }
            pdf.agregarTabla(tit, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);
            reportepdf = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarAprobar() {
        try {
            Calendar c = Calendar.getInstance();
            certificacion.setAnio(anio);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación aprobada: \n"
                    + "Documento :" + certificacion.getNumero());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioImpresion.cancelar();
        formularioClasificador.cancelar();
        formularioAprobar.cancelar();
        return null;
    }

    public String aprobarDGA(Certificacionespoa cert) {
        certificacion = cert;
        if (certificacion.getAnio() == null) {
            Organigrama d = seguridadbean.traerDireccion(certificacion.getDireccion());
            MensajesErrores.advertencia("Certificación no Aprobada por Director de: " + d.toString());
            return null;
        }
        if (certificacion.getAprobardga() == null) {
            certificacion.setAprobardga(Boolean.FALSE);
        }
        if (certificacion.getAprobardga()) {
            MensajesErrores.advertencia("Certificacion ya aprobada por DGA");
            return null;
        }
        formularioAprobar.editar();
        imprimir = certificacion.getImpreso();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
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
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "FUENTE DE FINANCIAMIENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, true, "VALOR INCLUIDO IVA"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            pdf = new DocumentoPDF("CERTIFICACIÓN  POA - PRESUPUESTARIA", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("\n\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("N°: " + certificacion.getNumero() + "-CPOA-" + certificacion.getAnio(), AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Fecha: Quito, " + sdf.format(certificacion.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Se certifica que:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            Detallecertificacionespoa d2 = detalles.get(0);
            if (d2 != null) {
                String tipo = d2.getAsignacion().getProyecto().getTipocompra();
//                pdf.agregaParrafo((d2.getAsignacion().getProyecto().getTipocompra() != null ? d2.getAsignacion().getProyecto().getTipocompra() : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase(), AuxiliarReporte.ALIGN_CENTER, 11, false);
                pdf.agregaParrafo((certificacion.getCertificaque() != null ? (certificacion.getCertificaque()) : ((tipo != null ? tipo : "") + " - " + d2.getAsignacion().getProyecto().getNombre().toUpperCase())), AuxiliarReporte.ALIGN_CENTER, 11, false);
            }
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Solicitado en memorando Nº " + certificacion.getMemo() + ".", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Se encuentra en el PLAN OPERATIVO ANUAL " + certificacion.getAnio() + ", según se detalla a continuación:", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            for (Detallecertificacionespoa d : detalles) {
                if (d.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, proyectoPoaBean.dividir1(a, d.getAsignacion().getProyecto())));
                    }
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
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, ""));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, "____________________________"));
            List<Codigos> aux = codigosBean.traerCodigoMaestro("DIREC");
            for (Codigos ca : aux) {
                if (ca.getCodigo().equals("CRP")) {
                    AuxiliarEntidad o = consultasPoaBean.traerEmpleadoCargo(ca.getDescripcion());
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, o.getNombres() + " " + o.getApellidos()));
                }
            }
            for (Codigos cc : aux) {
                if (cc.getCodigo().equals("CAP")) {
                    AuxiliarEntidad o = consultasPoaBean.traerEmpleadoCargo(cc.getDescripcion());
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_CENTER, 6, false, o.getNombres() + " " + o.getApellidos() + "\n" + "EXPERTO PRINCIPAL EN PLANIFICACIÓN"));
                }
            }
            pdf.agregarTabla(tit, valores, 2, 100, "", AuxiliarReporte.ALIGN_CENTER);
            reportepdf = pdf.traerRecurso();
        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarAprobarDGA() {
        try {
            if (certificacion.getAnio() == null) {
                MensajesErrores.advertencia("Certificación no Aprobada por Director");
                return null;
            }
            certificacion.setAprobardga(Boolean.TRUE);
            ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setCertificacion(certificacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Certificación aprobada DGA \n"
                    + "Documento :" + certificacion.getId());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioImpresion.cancelar();
        formularioClasificador.cancelar();
        formularioAprobar.cancelar();
        return null;
    }

    public boolean isPuedePedir() {
        if (empleado == null) {
            return false;
        }
        // traer los datos que e necesita
//        PSC
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='PSC' and o.empleado=:empleado");
        parametros.put("empleado", empleado);
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);

            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto() != null) {
                    if (c.getValortexto().equalsIgnoreCase("SI")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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

    public void llenarAsignacion() {
        listaAsignacionCertificar = new LinkedList<>();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.proyecto.codigo=:codigo and o.proyecto.anio=:anio");
            parametros.put("codigo", proyectoSeleccionado.getCodigo());
            parametros.put("anio", proyectoSeleccionado.getAnio());
            listaAsignacionCertificar = ejbAsignacionespoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String nuevoDetalle2(Asignacionespoa asig) {
        asignacion = asig;
        detalle = new Detallecertificacionespoa();
        detalle.setAsignacion(asig);
        detalle.setCertificacion(certificacion);
        formularioDetalleNuevo.insertar();

        return null;
    }

    public boolean traerEstado(Certificacionespoa c) {
        //Si no hay en ao ede la certificacion no esta aprobada por el director
        boolean retorno = false;
//        String retorno = "";
        if (c.getNumero() == null) {
            if (c.getAnio() == null) {
//                retorno = "INGRESADO";
                retorno = false;
            } else {
//                retorno = "APROBADO";
                retorno = true;
            }
        } else {
//            retorno = "APROBADO";
            retorno = true;
        }
        return retorno;
    }

    public String solicitud(Certificacionespoa cert, boolean elec) {
        certificacion = cert;
        formularioSolicitud.insertar();
        try {
            traerImagen();
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de'  yyyy");
            DecimalFormat df = new DecimalFormat("###,##0.00");
            Codigos codigoTexto = codigosBean.traerCodigo("TEXSOLCERYREF", "01");
            if (codigoTexto == null) {
                MensajesErrores.advertencia("No existe texto de Solicitud de Certificación");
                return null;
            }
            String nombreD = "";
            String cargoD = "";
            if (codigoTexto.getNombre() != null) {
                String[] campos = codigoTexto.getNombre().split("@");
                if (campos != null && campos.length == 2) {
                    nombreD = campos[0];
                    cargoD = campos[1];
                }
            }
            String texto1 = "";
            String texto2 = "";
            if (codigoTexto.getDescripcion() != null) {
                String[] campos = codigoTexto.getDescripcion().split("@");
                if (campos != null && campos.length == 2) {
                    texto1 = campos[0];
                    texto2 = campos[1];
                }
            }

            double valor = 0;
            String codig = "";
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            parametros.put(";campo", "o.valor");
            valor = ejbDetalles.sumarCampo(parametros).doubleValue();

            String textoD = texto2.replace("#valor#", df.format(valor));
            textoD = textoD.replace("#valorletras#", ConvertirNumeroALetras.convertNumberToLetter(valor));

            parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificacionespoa> lista = ejbDetalles.encontarParametros(parametros);
            String textoP = "";
            String SubActividad ="";
            for (Detallecertificacionespoa dt : lista) {
                codig = dt.getAsignacion().getProyecto().getCodigo().substring(0, 4) + " "
                        + dt.getAsignacion().getProyecto().getCodigo().substring(4, 7) + "-"
                        + dt.getAsignacion().getProyecto().getCodigo().substring(7, 10);
                textoP = codigoTexto.getParametros().replace("#programa#", traerPrograma(dt.getAsignacion().getProyecto().getCodigo().substring(0, 2)));
                textoP = textoP.replace("#proyecto#", traerProyecto(dt.getAsignacion().getProyecto().getCodigo().substring(0, 4)));
                textoP = textoP.replace("#actividad#", traerActividad(dt.getAsignacion().getProyecto().getCodigo().substring(0, 7)));
                textoP = textoP.replace("#subactividad#", traerSubactividad(dt.getAsignacion().getProyecto().getCodigo()));
                textoP = textoP.replace("#codig#", codig);
                textoP = textoP.replace("#partid#", dt.getAsignacion().getPartida().getCodigo());
                SubActividad = traerSubactividad(dt.getAsignacion().getProyecto().getCodigo());
            }

            Codigos codigoTextoC = codigosBean.traerCodigo("TEXSOLCERYREF", "03");
            if (codigoTextoC == null) {
                MensajesErrores.advertencia("No existe texto de Solicitud de Certificación");
                return null;
            }
            String nombre1 = "";
            String cargo1 = "";
            String nombre2 = "";
            String cargo2 = "";
            String nombre3 = "";
            String cargo3 = "";
            if (codigoTextoC.getNombre() != null) {
                String[] campos = codigoTextoC.getNombre().split("@");
                if (campos != null && campos.length == 2) {
                    nombre1 = campos[0];
                    cargo1 = campos[1];
                }
            }
            if (codigoTextoC.getDescripcion() != null) {
                String[] campos = codigoTextoC.getDescripcion().split("@");
                if (campos != null && campos.length == 2) {
                    nombre2 = campos[0];
                    cargo2 = campos[1];
                }
            }
            if (codigoTextoC.getParametros() != null) {
                String[] campos = codigoTextoC.getParametros().split("@");
                if (campos != null && campos.length == 2) {
                    nombre3 = campos[0];
                    cargo3 = campos[1];
                }
            }

            pdf = new DocumentoPDF("", null, seguridadbean.getLogueado().getUserid());
            pdf.agregaParrafo("Memorando Nro. CBDMQ-" + direccion + "-" + cert.getAnio() + "-" + cert.getId() + "MEM", AuxiliarReporte.ALIGN_RIGHT, 11, true);
            pdf.agregaParrafo("Quito, D.M., " + sdf.format(cert.getFecha()), AuxiliarReporte.ALIGN_RIGHT, 11, true);

            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            List<AuxiliarReporte> valores = new LinkedList<>();
            valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, "PARA: "));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, false, nombreD));
            valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, ""));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, true, cargoD));
            valores.add(new AuxiliarReporte("String", 5, AuxiliarReporte.ALIGN_LEFT, 10, true, "ASUNTO: "));
            valores.add(new AuxiliarReporte("String", 9, AuxiliarReporte.ALIGN_LEFT, 10, false, "Solicitud de Certificaciones POA, PAC y PRESUPUESTO para la " + SubActividad + "."));
            pdf.agregarTablaSolicitud(null, valores, new float[]{new Float(15), new Float(75)}, 100, "");
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafoFormato(texto1, 10, false);
            pdf.agregaParrafoFormato(SubActividad, 10, true);
            pdf.agregaParrafoFormato(textoD, 10, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafo(textoP, AuxiliarReporte.ALIGN_LEFT, 10, false);
            pdf.agregaParrafo("\n\n\n\n", AuxiliarReporte.ALIGN_LEFT, 10, false);
            if (elec) {
                pdf.agregaParrafo("Documento firmado electrónicamente", AuxiliarReporte.ALIGN_JUSTIFIED, 10, true);
            }
            pdf.agregaParrafo("Ing. " + seguridadbean.getLogueado().toString(), AuxiliarReporte.ALIGN_JUSTIFIED, 10, false);
            pdf.agregaParrafo(cargo, AuxiliarReporte.ALIGN_JUSTIFIED, 10, true);
            pdf.agregaParrafo("\n Copia:", AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + nombre1, AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + cargo1, AuxiliarReporte.ALIGN_JUSTIFIED, 9, true);
            pdf.agregaParrafo("\n         " + nombre2, AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + cargo2, AuxiliarReporte.ALIGN_JUSTIFIED, 9, true);
            pdf.agregaParrafo("\n         " + nombre3, AuxiliarReporte.ALIGN_JUSTIFIED, 9, false);
            pdf.agregaParrafo("         " + cargo3, AuxiliarReporte.ALIGN_JUSTIFIED, 9, true);
            reporteSolicitudpdf = pdf.traerRecurso();
            archivoFirmar = pdf.traerArchivo();
        } catch (IOException | DocumentException | ConsultarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerPrograma(String programa) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", programa);
            List<Proyectospoa> lista = ejbProyectos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0).getNombre();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String traerProyecto(String proyecto) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", proyecto);
            List<Proyectospoa> lista = ejbProyectos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0).getNombre();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String traerActividad(String actividad) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", actividad);
            List<Proyectospoa> lista = ejbProyectos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0).getNombre();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String traerSubactividad(String subactividad) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", subactividad);
            List<Proyectospoa> lista = ejbProyectos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0).getNombre();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String firma() {
        getFormularioFirma().insertar();
        return null;

    }

    public String grabarFirma() {
        try {
            solicitud(certificacion, true);
            if (certificacion.getArchivosolicitud() != null) {
                MensajesErrores.advertencia("Solicitud ya firmada electronicamente");
                return null;
            }
            FirmadorPDF firmador = new FirmadorPDF("SolicitudCertificacion-" + this.certificacion.getId(), clave, seguridadbean.getLogueado().getPin(), configuracionBean.getConfiguracion().getDirectorio(), archivoFirmar, pdf.getNumeroPaginas());
            boolean archivoOk = false;
            if (certificacion.getArchivosolicitud() != null) {
                File existente = new File(certificacion.getArchivosolicitud());
                if (existente.exists()) {
                    byte[] docByteArry = FileUtils.fileConvertToByteArray(existente);
                    if (docByteArry.length != 0) {
                        archivoOk = true;
                    }
                }
            }

            if (!archivoOk) {
                firmador.firmarSolicitud();
                certificacion.setArchivosolicitud(configuracionBean.getConfiguracion().getDirectorio() + "/firmados/" + "SolicitudCertificacion-" + this.certificacion.getId() + ".pdf");

                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
                solicitud(certificacion, true);

            } else {
                firmador.editarFirma();
                solicitud(certificacion, true);
            }
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        setClave(null);
//        formularioImpresion.cancelar();
        formularioFirma.cancelar();
        return null;
    }

    public String traerImagen() throws GrabarException {
        try {
            recurso = null;
            if (certificacion != null) {
                if (certificacion.getArchivosolicitud() != null) {
                    setNombreArchivo("Certificacion: " + certificacion.getId());
                    setTipoArchivo("Certificacion Presupuestaria");
                    recurso = new Recurso(Files.readAllBytes(Paths.get(certificacion.getArchivosolicitud())));
                }
            }
        } catch (IOException ex) {
            if (recurso == null) {
                certificacion.setArchivosolicitud(null);
                ejbCertificacionespoa.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            MensajesErrores.advertencia("Documento firmado no se encuentra");
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }

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
     * @return the puedeEditar
     */
    public Boolean getPuedeEditar() {
        return puedeEditar;
    }

    /**
     * @param puedeEditar the puedeEditar to set
     */
    public void setPuedeEditar(Boolean puedeEditar) {
        this.puedeEditar = puedeEditar;
    }

    /**
     * @return the listaAsignacionCertificar
     */
    public List<Asignacionespoa> getListaAsignacionCertificar() {
        return listaAsignacionCertificar;
    }

    /**
     * @param listaAsignacionCertificar the listaAsignacionCertificar to set
     */
    public void setListaAsignacionCertificar(List<Asignacionespoa> listaAsignacionCertificar) {
        this.listaAsignacionCertificar = listaAsignacionCertificar;
    }

    /**
     * @return the listaProyecto
     */
    public List<Proyectospoa> getListaProyecto() {
        return listaProyecto;
    }

    /**
     * @param listaProyecto the listaProyecto to set
     */
    public void setListaProyecto(List<Proyectospoa> listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    /**
     * @return the formularioDetalleNuevo
     */
    public Formulario getFormularioDetalleNuevo() {
        return formularioDetalleNuevo;
    }

    /**
     * @param formularioDetalleNuevo the formularioDetalleNuevo to set
     */
    public void setFormularioDetalleNuevo(Formulario formularioDetalleNuevo) {
        this.formularioDetalleNuevo = formularioDetalleNuevo;
    }

    /**
     * @return the dga
     */
    public boolean isDga() {
        return dga;
    }

    /**
     * @param dga the dga to set
     */
    public void setDga(boolean dga) {
        this.dga = dga;
    }

    /**
     * @return the formularioAprobar
     */
    public Formulario getFormularioAprobar() {
        return formularioAprobar;
    }

    /**
     * @param formularioAprobar the formularioAprobar to set
     */
    public void setFormularioAprobar(Formulario formularioAprobar) {
        this.formularioAprobar = formularioAprobar;
    }

    /**
     * @return the formularioSolicitud
     */
    public Formulario getFormularioSolicitud() {
        return formularioSolicitud;
    }

    /**
     * @param formularioSolicitud the formularioSolicitud to set
     */
    public void setFormularioSolicitud(Formulario formularioSolicitud) {
        this.formularioSolicitud = formularioSolicitud;
    }

    /**
     * @return the reporteSolicitudpdf
     */
    public Recurso getReporteSolicitudpdf() {
        return reporteSolicitudpdf;
    }

    /**
     * @param reporteSolicitudpdf the reporteSolicitudpdf to set
     */
    public void setReporteSolicitudpdf(Recurso reporteSolicitudpdf) {
        this.reporteSolicitudpdf = reporteSolicitudpdf;
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
