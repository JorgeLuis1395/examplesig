/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import javax.faces.application.Resource;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import javax.faces.event.ValueChangeEvent;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarAsignacion;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.RascomprasFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Rascompras;
import org.entidades.sfccbdmq.Reformas;
import org.errores.sfccbdmq.ConsultarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "auxiliarPresupuestoSfccbdmq")
@ViewScoped
public class AuxiliarpresupuestoBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public AuxiliarpresupuestoBean() {

    }
    private Asignaciones asignacion;
    private Certificaciones certificacion;
    private List<Detallecertificaciones> detallesCertificaciones;
    private List<Detallecompromiso> detallesCompromiso;
    private List<Rascompras> detallesRasCompras;
    private List<Reformas> detallesReformas;
    private List<AuxiliarAsignacion> listaAuxiliar;
    private List<Asignaciones> listaAsignaciones;
    private List listaAsignacion;
    private int anio;
    private double totalEjecutado;
    private Integer numeroControl;
    private String motivo;
    private String codigo;
    private String proyecto;
    private Codigos fuente;

    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioAsignacion = new Formulario();
    private Formulario formularioReporte = new Formulario();
    @EJB
    private RascomprasFacade ejbRasCompras;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private DetallecertificacionesFacade ejbDetallesCert;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;
    @ManagedProperty(value = "#{asignacionesSfccbdmq}")
    private AsignacionesBean asignacionesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
//    @ManagedProperty(value = "#{compromisosSfccbdmq}")
//    private CompromisosBean compromisosBean;
    private boolean imprimir;
    private Codigos tipoDocumento;
    private Integer numeroDocumento;
    private String impresas = "TRUE";
    private Date desde;
    private Date hasta;
    private Perfiles perfil;
    private int indice = 0;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;
    private Resource recurso;
    private DocumentoPDF pdf;

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
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        anio = c.get(Calendar.YEAR);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DATE, 1);
        desde = c.getTime();
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DATE, 31);
        hasta = c.getTime();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "AuxiliarPresupuestoVista";
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
     * @return the asignacion
     */
    public Asignaciones getAsignacion() {
        return asignacion;
    }

    /**
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(Asignaciones asignacion) {
        this.asignacion = asignacion;
    }

    /**
     * @return the certificacion
     */
    public Certificaciones getCertificacion() {
        return certificacion;
    }

    /**
     * @param certificacion the certificacion to set
     */
    public void setCertificacion(Certificaciones certificacion) {
        this.certificacion = certificacion;
    }

    public String buscar() {
        listaAsignaciones = new LinkedList<>();
        Map parametros = new HashMap();
        String where;
        if ((proyecto == null) || (proyecto.isEmpty())) {
            where = "o.proyecto.anio=:anio";
        } else {
            where = "o.proyecto.anio=:anio and upper(o.proyecto.codigo) like :codigo ";
            parametros.put("codigo", proyecto + "%");
        }
        if (clasificadorBean.getClasificador() != null) {
            where += " and  upper(o.clasificador.codigo) like :partida";
            parametros.put("partida", clasificadorBean.getClasificador().getCodigo() + "%");
        }
        if (fuente != null) {
            where += " and  o.fuente =:fuente";
            parametros.put("fuente", fuente);
        }
        // traer las asignaciones
        parametros.put(";orden", "o.clasificador.codigo,o.proyecto.codigo,o.fuente.codigo");
        parametros.put(";where", where);
        parametros.put("anio", anio);
        try {
            setListaAsignaciones(ejbAsignaciones.encontarParametros(parametros));
            if (!listaAsignaciones.isEmpty()) {
                traerAsignacion(getListaAsignaciones().get(0));

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliarpresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        listaAsignaciones = new LinkedList<>();
        Map parametros = new HashMap();
        String where;
        if ((proyecto == null) || (proyecto.isEmpty())) {
            where = "o.proyecto.anio=:anio";
        } else {
            where = "o.proyecto.anio=:anio and upper(o.proyecto.codigo) like :codigo ";
            parametros.put("codigo", proyecto + "%");
        }
        if (clasificadorBean.getClasificador() != null) {
            where += " and  upper(o.clasificador.codigo) like :partida";
            parametros.put("partida", clasificadorBean.getClasificador().getCodigo() + "%");
        }
        if (fuente != null) {
            where += " and  o.fuente =:fuente";
            parametros.put("fuente", fuente);
        }
        // traer las asignaciones
        parametros.put(";orden", "o.clasificador.codigo,o.proyecto.codigo,o.fuente.codigo");
        parametros.put(";where", where);
        parametros.put("anio", anio);
        try {
            List<Asignaciones> lista=ejbAsignaciones.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                pdf = new DocumentoPDF("AUXILIAR PARTIDAS\n", null, seguridadbean.getLogueado().getUserid());
                for (Asignaciones a : lista) {
                    traerImprimir(a);
                }
                recurso = pdf.traerRecurso();
                formularioReporte.insertar();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliarpresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliarpresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerAsignacion(Asignaciones a) {

        asignacion = a;
        Map parametros = new HashMap();
        parametros.put(";where", "o.asignacion=:asignacion");
        parametros.put("asignacion", a);
        parametros.put(";orden", "o.fecha desc");
        try {
            detallesReformas = ejbReformas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", a);
            parametros.put(";orden", "o.certificacion.fecha asc");
            detallesCertificaciones = ejbDetallesCert.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.detallecompromiso.detallecertificacion.asignacion=:asignacion");
            parametros.put("asignacion", a);
            parametros.put(";orden", "o.obligacion.fechaemision asc");
            detallesRasCompras = ejbRasCompras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.detallecertificacion.asignacion=:asignacion");
            parametros.put("asignacion", a);
            parametros.put(";orden", "o.compromiso.fecha asc");
            detallesCompromiso = ejbDetComp.encontarParametros(parametros);
            listaAuxiliar = calculosBean.traerEjecutado(a);
            List<Detallecompromiso> lDetalle = calculosBean.traerDevengadoRoles(a);
            for (Detallecompromiso d : lDetalle) {
                Rascompras r = new Rascompras();
                Obligaciones o = new Obligaciones();
                o.setFechaemision(d.getFecha());
                o.setConcepto(d.getCompromiso().getMotivo());
                r.setReferencia(d.getMotivo());
                r.setValor(d.getValor());
                r.setValorimpuesto(BigDecimal.ZERO);
                r.setObligacion(o);
                detallesRasCompras.add(r);
               
            }
             listaAuxiliar = calculosBean.traerEjecutado(a);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliarpresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);

        }
        return null;
    }

    public String traerImprimir(Asignaciones a) {

//        if (asignacion == null) {
//            MensajesErrores.advertencia("Seleccione una partida");
//            return null;
//        }
//        asignacion = a;
        Map parametros = new HashMap();
        parametros.put(";where", "o.asignacion=:asignacion");
        parametros.put("asignacion", a);
        parametros.put(";orden", "o.fecha desc");
        try {
            List<Reformas> detallesReformasImp = ejbReformas.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", a);
            parametros.put(";orden", "o.certificacion.fecha asc");
            List<Detallecertificaciones> detallesCertificacionesImp = ejbDetallesCert.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.detallecompromiso.detallecertificacion.asignacion=:asignacion");
            parametros.put("asignacion", a);
            parametros.put(";orden", "o.obligacion.fechaemision asc");
            List<Rascompras> detallesRasComprasImp = ejbRasCompras.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.detallecertificacion.asignacion=:asignacion");
            parametros.put("asignacion", a);
            parametros.put(";orden", "o.compromiso.fecha asc");
            List<Detallecompromiso> detallesCompromisoImp = ejbDetComp.encontarParametros(parametros);
            // incio del reporte

            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Año Actual :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,String.valueOf(getAnio())));
            for (Auxiliar paux : proyectoBean.getTitulos()) {
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,paux.getTitulo1()));
                columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,proyectoBean.dividir(paux, a.getProyecto())));

            }
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Producto"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,a.getProyecto().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Fuente de Financiamiento"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,a.getFuente().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,"Total Asignación"));
            columnas.add(new AuxiliarReporte("Double", a.getValor().doubleValue()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false,""));
            pdf.agregarTabla(null, columnas, 4, 100, null);
            // un parafo
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulos = new LinkedList<>();
//           

            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No."));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Motivo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Reforma Actual"));
            columnas = new LinkedList<>();
            double total = 0;
            for (Reformas r : detallesReformasImp) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getFecha()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCabecera().getId().toString()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getCabecera().getMotivo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getValor().doubleValue()));
                total += r.getValor().doubleValue();
            }
            // total
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 4, 100, "REFORMAS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No Control"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Tip Doc."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "No Doc."));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            total = 0;
            for (Detallecertificaciones d : detallesCertificacionesImp) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getCertificacion().getFecha()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getCertificacion().getId().toString()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getCertificacion().getTipodocumento().getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getCertificacion().getNumerodocumeto().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                total += d.getValor().doubleValue();
            }
            // total
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 5, 100, "CERTIFICACIONES");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Motivo"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Certificación"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            total = 0;
            for (Detallecompromiso d : detallesCompromisoImp) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getFecha()));
                if (d.getProveedor() == null) {
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, (d.getCompromiso().getProveedor() == null ? "" : d.getCompromiso().getProveedor().getEmpresa().toString())));
                } else {
                    columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getProveedor().getEmpresa().toString()));

                }
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, (d.getCompromiso().getContrato() == null ? "" : d.getCompromiso().getContrato().toString())));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getCompromiso().getMotivo()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getCompromiso().getCertificacion().getId().toString()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                total += d.getValor().doubleValue();
            }
            // total
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "COMPROMISOS");
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proveedor"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Contrato"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Referencia"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            total = 0;
            for (Rascompras d : detallesRasComprasImp) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getFechaemision()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getProveedor().getEmpresa().toString()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, (d.getObligacion().getContrato() == null ? "" : d.getObligacion().getContrato().toString())));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getObligacion().getConcepto()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getReferencia()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue() + d.getValorimpuesto().doubleValue()));
                total += d.getValor().doubleValue() + d.getValorimpuesto().doubleValue();
            }
            // roles
            List<Detallecompromiso> lDetalle = calculosBean.traerDevengadoRoles(a);
            for (Detallecompromiso d : lDetalle) {
                Rascompras r = new Rascompras();
                Obligaciones o = new Obligaciones();
                o.setFechaemision(d.getFecha());
                o.setConcepto(d.getCompromiso().getMotivo());
                r.setReferencia(d.getMotivo());
                r.setValor(d.getValor());
                r.setValorimpuesto(BigDecimal.ZERO);
                r.setObligacion(o);
                detallesRasComprasImp.add(r);
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getFecha()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getCompromiso().getMotivo()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getMotivo()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                total += d.getValor().doubleValue();
            }
            // total
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, null));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 6, 100, "OBLIGACIONES - DEVENGADO");
            //Ejecutado
            List<AuxiliarAsignacion> listaAuxiliarImp = calculosBean.traerEjecutado(a);
            pdf.agregaParrafo("\n\n");
            titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "SPI"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha SPI"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Concepto"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "No. Egreso"));
            titulos.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Banco"));
            titulos.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            total = 0;
            for (AuxiliarAsignacion as : listaAuxiliarImp) {
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, as.getDesde()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, as.getCodigo()));
                columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, as.getHasta()));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, as.getNombre()));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, as.getProyecto()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, as.getFuente()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, as.getValor()));
                total += as.getValor();
            }
            // total
            setTotalEjecutado(total);
            columnas.add(new AuxiliarReporte("Date", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, null));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Total"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, ""));
            columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, total));
            pdf.agregarTablaReporte(titulos, columnas, 7, 100, "EJECUTADO");

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AuxiliarpresupuestoBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the clasificadorBean
     */
    public ClasificadorBean getClasificadorBean() {
        return clasificadorBean;
    }

    /**
     * @param clasificadorBean the clasificadorBean to set
     */
    public void setClasificadorBean(ClasificadorBean clasificadorBean) {
        this.clasificadorBean = clasificadorBean;
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

    public String cancelar() {
        formulario.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";

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
     * @return the tipoDocumento
     */
    public Codigos getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento the tipoDocumento to set
     */
    public void setTipoDocumento(Codigos tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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
     * @return the proyectoBean
     */
    public ProyectosBean getProyectoBean() {
        return proyectoBean;
    }

    /**
     * @param proyectoBean the proyectoBean to set
     */
    public void setProyectoBean(ProyectosBean proyectoBean) {
        this.proyectoBean = proyectoBean;
    }

    /**
     * @return the asignacionesBean
     */
    public AsignacionesBean getAsignacionesBean() {
        return asignacionesBean;
    }

    /**
     * @param asignacionesBean the asignacionesBean to set
     */
    public void setAsignacionesBean(AsignacionesBean asignacionesBean) {
        this.asignacionesBean = asignacionesBean;
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
     * @return the numeroControl
     */
    public Integer getNumeroControl() {
        return numeroControl;
    }

    /**
     * @param numeroControl the numeroControl to set
     */
    public void setNumeroControl(Integer numeroControl) {
        this.numeroControl = numeroControl;
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

//    public void cambiaCodigo(ValueChangeEvent event) {
//        //clasificador = null;
//        if (proyectoBean.getProyectoSeleccionado() == null) {
//            MensajesErrores.advertencia("Seleccione un producto");
//            return;
//        }
//        asignacion = null;
//        if (event.getComponent() instanceof SelectInputText) {
//            // get the number of displayable records from the component
//            SelectInputText autoComplete
//                    = (SelectInputText) event.getComponent();
//            // get the new value typed by component user.
//            String newWord = (String) event.getNewValue();
//            //        getEmpresasBeans().setComercial("");
//            if ((newWord == null) || (newWord.isEmpty())) {
//                asignacion = null;
//            }
//            List<Asignaciones> aux = new LinkedList<>();
//            // traer la consulta
//
//            Map parametros = new HashMap();
//            if (proyectoBean.getProyectoSeleccionado() == null) {
//                // selecciona proyecto
//                asignacion = null;
//                return;
//            }
////            String codigo = proyectoBean.getPrograma().getCodigo();
////            if (proyectoBean.getProyecto() != null) {
////                // selecciona proyecto
////                codigo = proyectoBean.getProyecto().getCodigo();
////            }
////            if (asignacionesBean.getPartida() != null) {
////                codigo = asignacionesBean.getPartida().getCodigo();
////            }
//
//            parametros.put("proyecto", proyectoBean.getProyectoSeleccionado());
//            parametros.put("cuenta", newWord + "%");
//            parametros.put("anio", proyectoBean.getProyectoSeleccionado().getAnio());
//            if (asignacionesBean.getFuente() != null) {
//                parametros.put("fuente", asignacionesBean.getFuente());
//                parametros.put(";where", "o.proyecto=:proyecto and o.proyecto.anio=:anio "
//                        + "and o.fuente=:fuente and o.clasificador.ingreso=false and o.clasificador.codigo like :cuenta");
//            } else {
//                parametros.put(";where", "o.proyecto=:proyecto  and o.proyecto.anio=:anio "
//                        + "and o.clasificador.ingreso=false o.clasificador.codigo like :cuenta");
//            }
//            int total = ((SelectInputText) event.getComponent()).getRows();
//            // Contadores
//
//            parametros.put(";inicial", 0);
//            parametros.put(";final", total);
//            try {
//                aux = ejbAsignaciones.encontarParametros(parametros);
//            } catch (ConsultarException ex) {
//                MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
//                Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
//            }
//            listaAsignacion = new ArrayList();
//            for (Asignaciones e : aux) {
//                SelectItem s = new SelectItem(e, e.getClasificador().getCodigo());
//                listaAsignacion.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                asignacion = (Asignaciones) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Asignaciones tmp = null;
//                for (int i = 0, max = listaAsignacion.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) listaAsignacion.get(i);
//                    if (e.getLabel().compareToIgnoreCase(newWord) == 0) {
//                        tmp = (Asignaciones) e.getValue();
//                    }
//
//                }
//                if (tmp != null) {
//                    asignacion = tmp;
//                }
//            }
//
//        }
//    }

    /**
     * @return the listaAsignacion
     */
    public List getListaAsignacion() {
        return listaAsignacion;
    }

    /**
     * @param listaAsignacion the listaAsignacion to set
     */
    public void setListaAsignacion(List listaAsignacion) {
        this.listaAsignacion = listaAsignacion;
    }

    /**
     * @return the detallesCertificaciones
     */
    public List<Detallecertificaciones> getDetallesCertificaciones() {
        return detallesCertificaciones;
    }

    /**
     * @param detallesCertificaciones the detallesCertificaciones to set
     */
    public void setDetallesCertificaciones(List<Detallecertificaciones> detallesCertificaciones) {
        this.detallesCertificaciones = detallesCertificaciones;
    }

    /**
     * @return the detallesCompromiso
     */
    public List<Detallecompromiso> getDetallesCompromiso() {
        return detallesCompromiso;
    }

    /**
     * @param detallesCompromiso the detallesCompromiso to set
     */
    public void setDetallesCompromiso(List<Detallecompromiso> detallesCompromiso) {
        this.detallesCompromiso = detallesCompromiso;
    }

    /**
     * @return the detallesRasCompras
     */
    public List<Rascompras> getDetallesRasCompras() {
        return detallesRasCompras;
    }

    /**
     * @param detallesRasCompras the detallesRasCompras to set
     */
    public void setDetallesRasCompras(List<Rascompras> detallesRasCompras) {
        this.detallesRasCompras = detallesRasCompras;
    }

    /**
     * @return the detallesReformas
     */
    public List<Reformas> getDetallesReformas() {
        return detallesReformas;
    }

    /**
     * @param detallesReformas the detallesReformas to set
     */
    public void setDetallesReformas(List<Reformas> detallesReformas) {
        this.detallesReformas = detallesReformas;
    }

    public String getTotalCertificacion() {
        double retorno = 0;
        if (detallesCertificaciones == null) {
            return "0.00";
        }
        for (Detallecertificaciones d : detallesCertificaciones) {
            retorno += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public String getValorTotalCompromisoStr() {
        if (detallesCompromiso == null) {
            return "0.00";
        }
        double retorno = 0;
        for (Detallecompromiso d : detallesCompromiso) {
            retorno += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public String getValorTotalObligacionesStr() {
        if (detallesRasCompras == null) {
            return "0.00";
        }
        double retorno = 0;
        for (Rascompras d : detallesRasCompras) {
            retorno += d.getValor().doubleValue() + d.getValorimpuesto().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public String getValorDevengadoCompromisosStr() {
        if (detallesRasCompras == null) {
            return "0.00";
        }
        double retorno = 0;
        for (Detallecompromiso d : detallesCompromiso) {
            retorno += d.getValor().doubleValue();
        }
        for (Rascompras d : detallesRasCompras) {
            retorno -= d.getValor().doubleValue() + d.getValorimpuesto().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public double getTotalCertificaciones() {
        double retorno = 0;
        if (detallesCertificaciones == null) {
            return 0;
        }
        for (Detallecertificaciones d : detallesCertificaciones) {
            retorno += d.getValor().doubleValue();
        }
        return retorno;
    }

    public double getTotalReformas() {
        double retorno = 0;
        if (detallesReformas == null) {
            return 0;
        }
        for (Reformas d : detallesReformas) {
            retorno += d.getValor().doubleValue();
        }
        return retorno;
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
     * @return the recurso
     */
    public Resource getRecurso() {
        return recurso;
    }

    /**
     * @param recurso the recurso to set
     */
    public void setRecurso(Resource recurso) {
        this.recurso = recurso;
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
     * @return the listaAuxiliar
     */
    public List<AuxiliarAsignacion> getListaAuxiliar() {
        return listaAuxiliar;
    }

    /**
     * @param listaAuxiliar the listaAuxiliar to set
     */
    public void setListaAuxiliar(List<AuxiliarAsignacion> listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * @return the totalEjecutado
     */
    public double getTotalEjecutado() {
        return totalEjecutado;
    }

    /**
     * @param totalEjecutado the totalEjecutado to set
     */
    public void setTotalEjecutado(double totalEjecutado) {
        this.totalEjecutado = totalEjecutado;
    }

    /**
     * @return the proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * @param proyecto the proyecto to set
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * @return the fuente
     */
    public Codigos getFuente() {
        return fuente;
    }

    /**
     * @param fuente the fuente to set
     */
    public void setFuente(Codigos fuente) {
        this.fuente = fuente;
    }

    public String siguiente() {
        setIndice(getIndice() + 1);
        if (getIndice() >= getListaAsignaciones().size() - 1) {
            setIndice(0);
        }
        asignacion = getListaAsignaciones().get(getIndice());
        traerAsignacion(asignacion);
        return null;
    }

    public String anterior() {
        setIndice(getIndice() - 1);
        if (getIndice() < 0) {
            setIndice(getListaAsignaciones().size() - 1);
        }
        asignacion = getListaAsignaciones().get(getIndice());
        traerAsignacion(asignacion);
        return null;
    }

    public String inicio() {
        setIndice(0);
        asignacion = getListaAsignaciones().get(getIndice());
        traerAsignacion(asignacion);
        return null;
    }

    public String fin() {
        setIndice(getListaAsignaciones().size() - 1);
        asignacion = getListaAsignaciones().get(getIndice());
        traerAsignacion(asignacion);
        return null;
    }

    public void valueChangeMethod(ValueChangeEvent e) {
        setIndice((int) e.getNewValue());
        if (getIndice() < 0) {
            setIndice(getListaAsignaciones().size() - 1);
        }
        if (getIndice() >= getListaAsignaciones().size() - 1) {
            setIndice(0);
        }
        asignacion = getListaAsignaciones().get(getIndice());
        traerAsignacion(asignacion);
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return the listaAsignaciones
     */
    public List<Asignaciones> getListaAsignaciones() {
        return listaAsignaciones;
    }

    /**
     * @param listaAsignaciones the listaAsignaciones to set
     */
    public void setListaAsignaciones(List<Asignaciones> listaAsignaciones) {
        this.listaAsignaciones = listaAsignaciones;
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
}
