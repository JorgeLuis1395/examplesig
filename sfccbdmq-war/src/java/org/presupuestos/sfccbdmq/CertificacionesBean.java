/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CertificacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Certificacionespoa;
import org.entidades.sfccbdmq.Detallecertificacionespoa;
import org.entidades.sfccbdmq.Partidaspoa;
import org.entidades.sfccbdmq.Proyectospoa;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
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
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "certificacionesSfccbdmq")
@ViewScoped
public class CertificacionesBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public CertificacionesBean() {

        certificaciones = new LazyDataModel<Certificaciones>() {

            @Override
            public List<Certificaciones> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
        certificacion = new Certificaciones();
    }
    private Asignaciones asignacion;
    private Certificaciones certificacion;
    private List<Detallecertificaciones> detalles;
    private List listaAsignacion;
    private List<Detallecertificaciones> detallesb;
    private LazyDataModel<Certificaciones> certificaciones;
    private List<Certificaciones> certificacionesCompromiso;
    private Detallecertificaciones detalle;
    private int anio;
    private Integer numeroControl;
    private Integer numeroControlPoa;
    private Integer numeroSistemaPoa;
    private String motivo;
    private String codigo;
    private String partida;
    private Codigos fuente;
    private Recurso reportepdf;
    private Recurso recurso;
    private String nombreArchivo;
    private String tipoArchivo;
    private String clave;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioAsignacion = new Formulario();
    private Formulario formularioReporte = new Formulario();
    private Formulario formularioFirma = new Formulario();
    private Formulario formularioAnular = new Formulario();
    private Formulario formularioAjuste = new Formulario();
    private Formulario formularioLiberarSaldo = new Formulario();
    private File archivoFirmar;
    private DocumentoPDF pdf;
    @EJB
    private CertificacionesFacade ejbCertificaciones;
    @EJB
    private CertificacionespoaFacade ejbCertificacionesPoa;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private DetallecertificacionesFacade ejbDetalles;
    @EJB
    private DetallecompromisoFacade ejbDetComp;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private OrganigramaFacade ejbOrganigrama;
    @EJB
    private DetallecertificacionespoaFacade ejbDetallepoa;
    @EJB
    private CertificacionespoaFacade ejbCertificacionespoa;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreo;
    @EJB
    private EmpleadosFacade ejbemEmpleados;
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
    private Date fechaLiberar;
    private double saldoLiberar;
    private int codigoNumero;
    private Codigos cod;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{calulosPresupuesto}")
    private CalulosPresupuestoBean calculosBean;

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
        desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        hasta = getConfiguracionBean().getConfiguracion().getPfinalpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "CertificacionesVista";
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
        if ((numeroDocumento != null)) {
            if (numeroDocumento > 0) {
                if (tipoDocumento == null) {
                    MensajesErrores.advertencia("Seleccione un tipo de documento para buscar por número");
                    return null;
                }
            }
        }
        certificaciones = new LazyDataModel<Certificaciones>() {
            @Override
            public List<Certificaciones> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.id desc,o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }

                String where = "  o.anio=:anio and  o.roles=false "
                        + " and o.dependencia is null and o.fecha between :desde and :hasta";
                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();
                    String valor = (String) e.getValue();

                    where += " and upper(o." + clave + ") like :" + clave;
                    parametros.put(clave, valor.toUpperCase() + "%");
                }
                parametros.put("anio", anio);
                parametros.put("desde", desde);
                parametros.put("hasta", hasta);
                if (!((motivo == null) || (motivo.isEmpty()))) {
                    where += " and upper(o.motivo) like :motivo";
                    parametros.put("motivo", motivo.toUpperCase() + "%");
                }
                if (tipoDocumento != null) {
                    where += " and o.tipodocumento=:tipodocumento";
                    parametros.put("tipodocumento", tipoDocumento);
                    if (numeroDocumento != null) {
                        if (numeroDocumento > 0) {
                            where += " and o.numerodocumeto=:numerodocumeto";
                            parametros.put("numerodocumeto", numeroDocumento);
                        }
                    }
                }
                if (!((impresas == null) || (impresas.isEmpty()))) {
                    if (impresas.equalsIgnoreCase("true")) {
                        where += " and o.impreso=true";
                    } else {
                        where += " and o.impreso=false";
                    }
                }

//                if (numeroControl != null) {
//                    if (numeroControl > 0) {
//                        where = " o.id=:id";
//                        parametros = new HashMap();
//                        parametros.put("id", numeroControl);
//                    }
//                }
                if (numeroControl != null) {
                    if (numeroControl > 0) {
//                        parametros = new HashMap();
                        where += " and o.numerocert=:numerocert";
                        parametros.put("numerocert", numeroControl);
                    }
                }
                if (numeroControlPoa != null) {
                    buscarPacpoa(numeroControlPoa);
                    where += " and  o.pacpoa=:pacpoa";
                    parametros.put("pacpoa", numeroSistemaPoa);
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCertificaciones.contar(parametros);
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
                getCertificaciones().setRowCount(total);
                try {
                    return ejbCertificaciones.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        return null;
    }

    public String buscarParaCompromiso() {
        Map parametros = new HashMap();
        parametros.put(";orden", "o.fecha desc");
        String where = "  o.anio=:anio and  o.roles=false "
                + " and o.dependencia is null and o.fecha between :desde and :hasta";

        parametros.put("anio", anio);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);

        if (!((motivo == null) || (motivo.isEmpty()))) {
            where += " and upper(o.motivo) like :motivo";
            parametros.put("motivo", motivo.toUpperCase() + "%");
        }
        if (numeroControl != null) {
            if (numeroControl > 0) {
                parametros = new HashMap();
                where = " o.numerocert=:numerocert and o.anio=:anio";
                parametros.put("numerocert", numeroControl);
                parametros.put("anio", anio);
            }
        }

        try {
            parametros.put(";where", where);
            List<Certificaciones> lista = ejbCertificaciones.encontarParametros(parametros);
            certificacionesCompromiso = new LinkedList<>();
            for (Certificaciones c : lista) {
                double saldo = traerSaldoCert(c);
                double valor = traerValorCertificacion(c);
                if (valor - saldo > 0) {
                    c.setSaldo(saldo);
                    c.setMonto(valor);
                    certificacionesCompromiso.add(c);
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger("").log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String buscarPacpoa(Integer numero) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.numero=:numero and o.anio=:anio");
            parametros.put("numero", numero + "");
            parametros.put("anio", anio);
            List<Certificacionespoa> lista = ejbCertificacionesPoa.encontarParametros(parametros);
            for (Certificacionespoa c : lista) {
                numeroSistemaPoa = c.getId();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerPacpoa(Integer numero) {
        if (numero == null) {
            return "";
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", numero);
            List<Certificacionespoa> lista = ejbCertificacionesPoa.encontarParametros(parametros);
            for (Certificacionespoa c : lista) {
                return c != null ? (c.getNumero() + "") : "";
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
//        if (asignacion == null) {
//
//            MensajesErrores.advertencia("Seleccione una partida primero");
//            return null;
//        }
//        formularioClasificador.insertar();
        try {
            cod = ejbCodigos.traerCodigo("NUM", "04");
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
            certificacion = new Certificaciones();
            certificacion.setImpreso(Boolean.FALSE);
            certificacion.setAnulado(Boolean.FALSE);
            certificacion.setRoles(Boolean.FALSE);
            Calendar c = Calendar.getInstance();
//            certificacion.setAnio(c.get(Calendar.YEAR));
            certificacion.setAnio(anio);
            certificacion.setFecha(new Date());
//            certificacion.setNumerocert(nume);
            codigoNumero = nume;
            detalles = new LinkedList<>();
            detallesb = new LinkedList<>();
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().insertar();
        return null;
    }

    public String modificar(Certificaciones certifi) {
        certificacion = certifi;
        if (certificacion == null) {
            return null;
        }
//        try {
//        formularioClasificador.cancelar();
//        if (certificacion.getImpreso()) {
//            MensajesErrores.advertencia("Certificación ya impresa no es posible modificar");
//            return null;
//        }
        if (certificacion.getAnulado() == null) {
            certificacion.setAnulado(false);
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación ya anulada");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
            cod = ejbCodigos.traerCodigo("NUM", "04");
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
            if (certificacion.getNumerocert() == null) {
//                certificacion.setNumerocert(nume);
                codigoNumero = nume;
            } else {
                codigoNumero = certificacion.getNumerocert();
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        detallesb = new LinkedList<>();
        getFormulario().editar();

        return null;
    }

    public String asignar(Certificaciones certificacion) {

//        try {
//        formularioClasificador.cancelar();
        this.certificacion = certificacion;
        if (certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación ya impresa no es posible modificar");
            return null;
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación ya anulada");
            return null;
        }
        formularioAsignacion.editar();

        return null;
    }

    public String reporte(Certificaciones certificacion) {
        this.certificacion = certificacion;
        if (certificacion.getAnulado() == null) {
            certificacion.setAnulado(Boolean.FALSE);
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación ya anulada");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
            double total = 0;
            List<Detallecertificaciones> listaReporte = new LinkedList<>();
            List<AuxiliarReporte> columnasReporte = new LinkedList<>();
            for (Detallecertificaciones d : detalles) {
                detalle = d;
                double asignado = d.getAsignacion().getValor().doubleValue() * 100;
                double reformas = calculaTotalReformas(d.getAsignacion()) * 100;
                double tCertificaciones = calculaTotalCertificaciones(d.getAsignacion()) * 100;
                double suma = asignado + reformas - tCertificaciones;
                double valor = d.getValor().doubleValue() * 100;
                double compara = Math.round(suma - valor);
                if (compara < 0) {
                    MensajesErrores.advertencia("Certificación exede el saldo de la partida " + d.getAsignacion().getClasificador().getCodigo());
//                    return null;
                }
                total += d.getValor().doubleValue();
                String que = "";
                for (Auxiliar a : titulos) {
                    que += a.getTitulo1() + " : ";
                    que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                }
                que += " - Producto : " + d.getAsignacion().getProyecto().toString() + " ";
                d.setArbolProyectos(que);
                if (d.getAsignacion() != null) {
                    listaReporte.add(d);
                    // aqui toca el reporte
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf1.format(d.getFecha() == null ? new Date() : d.getFecha())));
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                    columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                    columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getArbolProyectos()));
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getCodigo()));
                    columnasReporte.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(d.getValor().doubleValue())));
                }

            }
            formularioImpresion.editar();

            imprimir = certificacion.getImpreso();
            Detallecertificaciones d1 = new Detallecertificaciones();
            d1.setValor(new BigDecimal(total));
            detalles.add(d1);
            DecimalFormat df = new DecimalFormat("###,##0.00");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
//            pdf.agregaTitulo("INFORME DE DISPONIBILIDAD\n");
            pdf.agregaTitulo("CERTIFICACIÓN PRESUPUESTARIA\n");

//          "CERTIFICACION PRESUPUESTARIA DEL AÑO " + anio
//            pdf.agregaTitulo("CERTIFICACION PRESUPUESTARIA DEL AÑO " + anio);
//            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Disponibilidad :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, certificacion.getNumerocert() + ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf.format(certificacion.getFecha()).toUpperCase()));
            pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio
                    + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Dirección :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, certificacion.getDireccion() == null
                    ? "" : certificacion.getDireccion().toString()));
            pdf.agregarTabla(null, columnas, 2, 100, null);

            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBLE"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            pdf.agregarTablaReporte(titulosReporte, columnasReporte, 6, 100, "PARTIDAS");
            pdf.agregaParrafo("Observaciones :" + certificacion.getMotivo());
            pdf.agregaParrafo("\nEl Monto de la disponibilidad asciende a: " + df.format(total) + "\n");
            DecimalFormat df1 = new DecimalFormat("0.00");
            pdf.agregaParrafo("\nTotal disponibilidad: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
            Codigos cod1 = ejbCodigos.traerCodigo("FIRCERT", "01");
            if (cod1 == null) {
                MensajesErrores.advertencia("No existe codigo de firmas de Certificación");
                return null;
            }
            Codigos cod2 = ejbCodigos.traerCodigo("FIRCERT", "02");
            if (cod2 == null) {
                MensajesErrores.advertencia("No existe codigo de firmas de Certificación");
                return null;
            }
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod1.getDescripcion() != null ? cod1.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod2.getDescripcion() != null ? cod2.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod1.getParametros() != null ? cod1.getParametros() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod2.getParametros() != null ? cod2.getParametros() : ""));

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reportepdf = pdf.traerRecurso();

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String firma() {
        formularioFirma.insertar();
        return null;

    }

    public String grabarFirma() {
        try {
            System.out.println("numero: " + pdf.getNumeroPaginas());
            FirmadorPDF firmador = new FirmadorPDF("certificacion-" + this.certificacion.getId(), clave, seguridadbean.getLogueado().getPin(), configuracionBean.getConfiguracion().getDirectorio(), archivoFirmar, pdf.getNumeroPaginas());
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
                certificacion.setArchivo(configuracionBean.getConfiguracion().getDirectorio() + "/firmados/" + "certificacion-" + this.certificacion.getId() + ".pdf");

                ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
                imprimi(certificacion);

            } else {
                firmador.editarFirma();
                imprimi(certificacion);
            }
        } catch (GrabarException | IOException | KeyStoreException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        clave = null;
//        formularioImpresion.cancelar();
        formularioFirma.cancelar();
        return null;
    }

    public String aprobar(Certificaciones cert) {
        certificacion = cert;
        detalles = new LinkedList<>();
        if (certificacion.getAnulado() == null) {
            certificacion.setAnulado(Boolean.FALSE);
        }
        if (certificacion.getImpreso() == null) {
            certificacion.setImpreso(Boolean.FALSE);
        }
        if (certificacion.getImpreso()) {
            MensajesErrores.advertencia("Certificación ya aprobada");
            return null;
        }
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación ya anulada");
            return null;
        }
        if (certificacion.getNumerocert() == null) {
            MensajesErrores.advertencia("Certificación no tiene número");
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
            if ((detalles == null) || (detalles.isEmpty())) {
                MensajesErrores.advertencia("No existe detalles de la certificacion");
                return null;
            }
            formularioImpresion.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabarAprobado() {
        try {
            if (certificacion.getImpreso() == null) {
                certificacion.setImpreso(false);
            }
            if (certificacion.getTipodocumento() == null) {
                List<Codigos> tipoDoc = codigosBean.traerCodigoMaestro(Constantes.DOCUMENTOS_PRESUPUESTO);
                for (Codigos c : tipoDoc) {
                    certificacion.setTipodocumento(c);
                }
                ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }

            //// pacpoa**********************************************************************
            if (certificacion.getPacpoa() == null) {
//            // pasar al lado pacpoa
                for (Detallecertificaciones d : detalles) {
                    if (d.getId() != null) {
                        Map parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio and o.activo=true");
                        parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                        parametros.put("anio", anio);
                        List<Proyectospoa> listap = ejbProyectospoa.encontarParametros(parametros);
                        Proyectospoa p = null;
                        for (Proyectospoa p1 : listap) {
                            p = p1;
                        }
                        if (p == null) {
                            MensajesErrores.advertencia("Proyecto Financiero no tiene su similar en Sistema PAC-POA : " + d.getAsignacion().getProyecto().toString());
                            return null;
                        }
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.activo=true");
                        parametros.put("codigo", d.getAsignacion().getClasificador().getCodigo());
                        List<Partidaspoa> listac = ejbPartidaspoa.encontarParametros(parametros);
                        Partidaspoa c = null;
                        for (Partidaspoa c1 : listac) {
                            c = c1;
                        }
                        if (c == null) {
                            MensajesErrores.advertencia("Partida Financiero no tiene su similar en Sistema PAC-POA : " + d.getAsignacion().getClasificador().toString());
                            return null;
                        }
                        // Buscar la fuente
                        Codigos co = null;
                        List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                        for (Codigos co1 : listaCodigos) {
                            if (co1.equals(d.getAsignacion().getFuente())) {
                                co = co1;
                            }
                        }
                        if (co == null) {
                            MensajesErrores.advertencia("Fuente de Financiamiento Financiero no tiene su similar en Sistema PAC-POA: " + d.getAsignacion().getFuente().toString());
                            return null;
                        }
                        // Buscar Asignación
                        parametros = new HashMap();
                        parametros.put(";where", "o.partida=:partida "
                                + " and o.proyecto=:proyecto and o.fuente=:fuente and o.activo=true");
                        parametros.put("partida", c);
                        parametros.put("proyecto", p);
                        parametros.put("fuente", co.getCodigo());
                        List<Asignacionespoa> listaa = ejbAsignacionespoa.encontarParametros(parametros);
                        Asignacionespoa af = null;
                        for (Asignacionespoa af1 : listaa) {
                            af = af1;
                        }
                        if (af == null) {
                            if (d.getValor().doubleValue() >= 0) {
                                // creo la signación

                            } else {
                                MensajesErrores.advertencia("No existe dinero para ejecutar esta certificación en Sistema PAC-POA: "
                                        + c.toString() + " - " + p.toString() + " - "
                                        + d.getAsignacion().getFuente().toString());
                                return null;
                            }
                        }
                    }
                }

                // Organigrama
                Map parametros = new HashMap();
                parametros.put(";where", "o.codigo=:codigo");
                parametros.put("codigo", certificacion.getDireccion().getCodigo());
                List<Organigrama> listaOrg = ejbOrganigrama.encontarParametros(parametros);
                Organigrama o = null;
                for (Organigrama o1 : listaOrg) {
                    o = o1;
                }
                parametros = new HashMap();
                parametros.put("where", " o.fecha=:fecha");
                parametros.put("fecha", certificacion.getFecha());
                String ordinal = String.format("%03d", ejbCertificacionespoa.contar(parametros) + 1);
                Calendar cal = Calendar.getInstance();
                cal.setTime(certificacion.getFecha());
                String aa = "" + cal.get(Calendar.YEAR);
                String mm = String.format("%02d", cal.get(Calendar.MONTH) + 1);
                String dd = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

                Certificacionespoa certFin = new Certificacionespoa();
                certFin.setAnio(certificacion.getAnio());
                certFin.setAnulado(false);
                certFin.setFecha(certificacion.getFecha());
                certFin.setMemo(certificacion.getMemo());
                certFin.setMotivo(certificacion.getMotivo());
                certFin.setRoles(certificacion.getRoles());
                certFin.setFinaciero(certificacion.getId());
                certFin.setDireccion(certificacion.getDireccion().getCodigo());
                certFin.setImpreso(true);
                certFin.setNumero("0");
                certFin.setInfimanoplanificada(Boolean.FALSE);
                certFin.setRechazadopac(Boolean.FALSE);
                ejbCertificacionesPoa.create(certFin, seguridadbean.getLogueado().getUserid());
                for (Detallecertificaciones d : detalles) {
                    if (d.getId() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio and o.activo=true");
                        parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                        parametros.put("anio", anio);
                        List<Proyectospoa> listap = ejbProyectospoa.encontarParametros(parametros);
                        Proyectospoa p = null;
                        for (Proyectospoa p1 : listap) {
                            p = p1;
                        }
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.activo=true");
                        parametros.put("codigo", d.getAsignacion().getClasificador().getCodigo());
                        List<Partidaspoa> listac = ejbPartidaspoa.encontarParametros(parametros);
                        Partidaspoa c = null;
                        for (Partidaspoa c1 : listac) {
                            c = c1;
                        }

                        // Buscar la fuente
                        Codigos co = null;
                        List<Codigos> listaCodigos = codigosBean.getFuentesFinanciamiento();
                        for (Codigos co1 : listaCodigos) {
                            if (co1.equals(d.getAsignacion().getFuente())) {
                                co = co1;
                            }
                        }

                        // Buscar Asignación
                        parametros = new HashMap();
                        parametros.put(";where", "o.partida=:partida "
                                + " and o.proyecto=:proyecto and o.fuente=:fuente and o.activo=true");
                        parametros.put("partida", c);
                        parametros.put("proyecto", p);
                        parametros.put("fuente", co.getCodigo());
                        List<Asignacionespoa> listaa = ejbAsignacionespoa.encontarParametros(parametros);
                        Asignacionespoa af = null;
                        for (Asignacionespoa af1 : listaa) {
                            af = af1;
                        }
                        Detallecertificacionespoa d2 = new Detallecertificacionespoa();
//                    d1.setCertificacion(certFin);
                        d2.setAsignacion(af);
                        d2.setCertificacion(certFin);
                        d2.setValor(d.getValor());
                        d2.setFecha(certFin.getFecha());
                        ejbDetallepoa.create(d2, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
//
//            // Fin pacpoa
//            // Fin pacpoa********************************************************************

//Envio de notificacion al area requiriente cuando se aprueba en financiero
            Empleados emple = null;
            Map parametros = new HashMap();
            parametros.put(";where", "o.cargoactual.organigrama.codigo=:organigrama");
            parametros.put("organigrama", certificacion.getDireccion().getCodigo());
            List<Empleados> lista = ejbemEmpleados.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                emple = lista.get(0);
            }
            if (emple != null) {
                String email = emple.getEntidad().getEmail();
                ejbCorreo.enviarCorreo(email != null ? email : " ", "Certificación Financiero", "Certificación Financiero Nro: " + certificacion.getMemo() + ", ha sido aprobada ");
            }

        } catch (GrabarException | ConsultarException | MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InsertarException ex) {
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imprimir();
        formularioImpresion.cancelar();
        return null;
    }

    private void imprimir() {
        if ((detalles == null) || (detalles.isEmpty())) {
            MensajesErrores.advertencia("Es necesario al menos una cuenta");
            return;
        }
        certificacion.setImpreso(Boolean.TRUE);
        try {
            ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String eliminar(Certificaciones certificacion) {
//        try {
        if (certificacion == null) {
            detalles = new LinkedList<>();
            return null;
        }
        this.certificacion = certificacion;
        if (certificacion.getAnulado()) {
            MensajesErrores.advertencia("Certificación ya anulado");
            return null;
        }
//        formularioClasificador.cancelar();
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
            List<Compromisos> li = ejbCompromisos.encontarParametros(parametros);
            if (!li.isEmpty()) {
                MensajesErrores.advertencia("Certificación tiene Compromiso");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        getFormulario().eliminar();

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

    public String insertar() {
//        certificacion.setAsignacion(asignacion);
//        if (certificacion.getAsignacion() == null) {
//            MensajesErrores.advertencia("Seleccione una asignación");
//            return null;
//        }
//        if ((certificacion.getMotivo() == null) || (certificacion.getMotivo().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario motivo de certificación");
//            return null;
//        }
//        certificacion.setFecha(new Date());
//        if (certificacion.getValor().doubleValue() <= 0) {
//            MensajesErrores.advertencia("Valor debe ser mayor o igual a cero");
//            return null;
//        }
        if ((detalles == null) || (detalles.isEmpty())) {
            MensajesErrores.advertencia("Es necesario al menos una cuenta");
            return null;
        }
        if ((certificacion.getMotivo() == null) || (certificacion.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario motivo de certificación");
            return null;
        }
        if ((certificacion.getFecha() == null) || (certificacion.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
            MensajesErrores.advertencia("Fecha de certificación debe ser mayor a periodo vigente");
            return null;
        }
        try {
            int numeroDocumentoInterno = Integer.valueOf(certificacion.getTipodocumento().getParametros());
            numeroDocumentoInterno++;
            certificacion.setNumerodocumeto(numeroDocumentoInterno);
            Map parametros = new HashMap();
            parametros.put(";where", "o.numerocert=:numerocert and o.anio=:anio");
            parametros.put("numerocert", codigoNumero);
            parametros.put("anio", anio);

            List<Certificaciones> lista = ejbCertificaciones.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("Número de certificación duplicada");
                return null;
            } else {
                certificacion.setNumerocert(codigoNumero);
            }
            ejbCertificaciones.create(certificacion, seguridadbean.getLogueado().getUserid());
            Codigos c = certificacion.getTipodocumento();
            c.setParametros(String.valueOf(numeroDocumentoInterno));
            ejbCodigos.edit(c, seguridadbean.getLogueado().getUserid());
            for (Detallecertificaciones d : detalles) {
                d.setCertificacion(certificacion);
                if (d.getValor().doubleValue() > 0) {
                    d.setFecha(certificacion.getFecha());
                }
                ejbDetalles.create(d, seguridadbean.getLogueado().getUserid());
            }
//            cod = ejbCodigos.traerCodigo("NUM", "04");
            cod.setParametros(certificacion.getNumerocert() + "");
            ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("se creo certificación con número de control: " + certificacion.getId().toString());
        formulario.cancelar();
        formularioClasificador.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        try {
            if ((certificacion.getMotivo() == null) || (certificacion.getMotivo().isEmpty())) {
                MensajesErrores.advertencia("Es necesario motivo de certificación");
                return null;
            }
            if ((certificacion.getFecha() == null) || (certificacion.getFecha().before(configuracionBean.getConfiguracion().getPvigente()))) {
                MensajesErrores.advertencia("Fecha de certificación debe ser mayor a periodo vigente");
                return null;
            }
//            if (certificacion.getImpreso()) {
//                MensajesErrores.advertencia("Certificación ya impresa no es posible modificar");
//                return null;
//            }
            if ((detalles == null) || (detalles.isEmpty())) {
                MensajesErrores.advertencia("Es necesario al menos una cuenta");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
//            int total = ejbCertificaciones.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
//                return null;
//            }
            int total = ejbReformas.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible modificar");
//                return null;
//            }
            if (certificacion.getNumerocert() != null) {
                if (!certificacion.getNumerocert().equals(codigoNumero)) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.numerocert=:numerocert and o.anio=:anio");
                    parametros.put("numerocert", codigoNumero);
                    parametros.put("anio", anio);
                    List<Certificaciones> lista = ejbCertificaciones.encontarParametros(parametros);
                    if (!lista.isEmpty()) {
                        MensajesErrores.advertencia("Número de certificación duplicada");
                        return null;
                    } else {
                        certificacion.setNumerocert(codigoNumero);
                    }
                }
            } else {
                certificacion.setNumerocert(codigoNumero);
            }
            ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
            for (Detallecertificaciones d : detalles) {
                d.setCertificacion(certificacion);
                if (d.getValor().doubleValue() > 0) {
                    d.setFecha(certificacion.getFecha());
                }
                if (d.getId() == null) {
                    ejbDetalles.create(d, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbDetalles.edit(d, seguridadbean.getLogueado().getUserid());
                }
            }
            for (Detallecertificaciones d : detallesb) {
                if (d.getId() != null) {
                    ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
                }
            }

            int num = Integer.parseInt(cod.getParametros());
            if (certificacion.getNumerocert() > num) {
                cod.setParametros(certificacion.getNumerocert() + "");
                ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | ConsultarException | InsertarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }
//    public String grabarAsignar() {
//        
//        try {
//            
//            ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
//            
//        } catch (GrabarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        formularioAsignacion.cancelar();
//        return null;
//    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            if (certificacion.getAnulado()) {
                MensajesErrores.advertencia("Certificación ya anulado");
                return null;
            }
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.asignacion=:asignacion");
//            parametros.put("asignacion", asignacion);
////            int total = ejbCertificaciones.contar(parametros);
////            if (total > 0) {
////                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible borrar");
////                return null;
////            }
//            int total = ejbReformas.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Existen reformas ya realizadas para esta certificación, no es posible borrar");
//                return null;
//            }
            // ver bien si se crea un documento que anule el anterior pero los valores ya usados hay que ver
//            if (certificacion.getImpreso()) {
//                certificacion.setAnulado(true);
//                ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
//                Certificaciones c = new Certificaciones();
//                c.setMotivo("Anulación de Certificación ");
//                c.setFecha(new Date());
//                c.setNumerodocumeto(certificacion.getNumerodocumeto());
//                c.setTipodocumento(certificacion.getTipodocumento());
//                c.setImpreso(true);
//                c.setAnulado(true);
//                c.setDependencia(certificacion);
//                ejbCertificaciones.create(c, seguridadbean.getLogueado().getUserid());
//                for (Detallecertificaciones d1 : detalles) {
//                    Detallecertificaciones d = new Detallecertificaciones();
//                    d.setAsignacion(d1.getAsignacion());
//                    d.setCertificacion(c);
//                    d.setValor(new BigDecimal(d.getValor().doubleValue() * -1));
//                    ejbDetalles.create(d, seguridadbean.getLogueado().getUserid());
//                }
//            } else {

            //PAAPOA
            Map parametros = new HashMap();
            parametros.put(";where", "o.id=:certificacion");
            parametros.put("certificacion", certificacion.getPacpoa());
            List<Certificacionespoa> lista = ejbCertificacionesPoa.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                Certificacionespoa certificacioPP = lista.get(0);
                if (certificacioPP != null) {
                    certificacioPP.setImpreso(Boolean.FALSE);
                    certificacioPP.setFechaaprobacion(null);
                    certificacioPP.setUsuario(null);
                    certificacioPP.setImpresopac(Boolean.FALSE);
                    certificacioPP.setFechadocumento(null);
                    certificacioPP.setFechapac(null);
                    certificacioPP.setUsuariopac(null);
                    ejbCertificacionesPoa.edit(certificacioPP, seguridadbean.getLogueado().getUserid());
                }

            }
            //FIN PACPOA
            for (Detallecertificaciones d : detalles) {
                if (d.getId() != null) {
                    ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
                }
            }
            if (detallesb != null) {
                for (Detallecertificaciones d : detallesb) {
                    if (d.getId() != null) {
                        ejbDetalles.remove(d, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            ejbCertificaciones.remove(certificacion, seguridadbean.getLogueado().getUserid());
//            }

        } catch (BorrarException ex) {
//        } catch (GrabarException | BorrarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GrabarException | ConsultarException ex) {
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String salir() {
        formularioImpresion.cancelar();
        formularioDetalle.cancelar();
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
     * @return the detalles
     */
    public List<Detallecertificaciones> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Detallecertificaciones> detalles) {
        this.detalles = detalles;
    }

    /**
     * @return the detalle
     */
    public Detallecertificaciones getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallecertificaciones detalle) {
        this.detalle = detalle;
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

    /**
     * @return the certificaciones
     */
    public LazyDataModel<Certificaciones> getCertificaciones() {
        return certificaciones;
    }

    /**
     * @param certificaciones the certificaciones to set
     */
    public void setCertificaciones(LazyDataModel<Certificaciones> certificaciones) {
        this.certificaciones = certificaciones;
    }

    public String cancelar() {
        formulario.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String nuevoDetalle() {
        formularioClasificador.insertar();
        formularioDetalle.insertar();
        detalle = new Detallecertificaciones();
//        detalle.setFecha(certificacion.getFecha());
        clasificadorBean.setClasificador(null);
        clasificadorBean.setCodigo(null);
        partida = null;
        fuente = null;
//        proyectoBean.setAnio(anio);
//        proyectoBean.setProyectoSeleccionado(null);
//        proyectoBean.setCodigo(null);
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
        codigo = asignacion.getClasificador().getCodigo();
        return null;
    }

    public String borraDetalle() {
        int fila = formularioDetalle.getFila().getRowIndex();
        this.detalle = detalles.get(fila);
        formularioDetalle.setIndiceFila(fila);
        formularioClasificador.cancelar();
        formularioDetalle.eliminar();
        asignacion = detalle.getAsignacion();
        codigo = asignacion.getClasificador().getCodigo();
        return null;
    }

    private boolean estaAsignacion(Asignaciones a) {
        for (Detallecertificaciones d : detalles) {
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
        if (detalle.getValor().doubleValue() == 0) {
            MensajesErrores.advertencia("Valor debe ser distinto a cero");
            return null;
        }
        // debe certificar la suma de lo asignado + lo reformado + certificaciones <= certificacion actual
        double asignado = asignacion.getValor().doubleValue() * 100;
        double reformas = calculaTotalReformas(asignacion) * 100;
        double tCertificaciones = calculaTotalCertificaciones(asignacion) * 100;
        double suma = asignado + reformas - tCertificaciones;
        double valor = detalle.getValor().doubleValue() * 100;
        double compara = Math.round(suma - valor);
        if (compara < 0) {
            MensajesErrores.advertencia("Certificación exede el saldo de la partida");
            return null;
        }
        detalle.setFecha(certificacion.getFecha());
        detalles.add(detalle);
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
        double tCertificaciones = calculaTotalCertificaciones(asignacion) * 100;
        double suma = asignado + reformas - tCertificaciones;
        double valor = detalle.getValor().doubleValue() * 100;
        double compara = Math.round(suma - valor);
//        if (compara < 0) {
//            MensajesErrores.advertencia("Certificación exede el saldo de la partida");
//            return null;
//        }
        detalles.set(formularioDetalle.getIndiceFila(), detalle);
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

    private double calculaTotalCertificaciones(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put("anio", anio);
        if (detalle.getId() != null) {
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false "
                    //            parametros.put(";where", "o.asignacion=:asignacion "
                    //                    + "and o.certificacion.impreso=true "
                    + "and o.id!=:id and o.asignacion.proyecto.anio=:anio");
            parametros.put("id", detalle.getId());
        } else {
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false and o.asignacion.proyecto.anio=:anio");
        }

        try {
            return ejbDetalles.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getValorCertificacion() {
        Certificaciones c = (Certificaciones) certificaciones.getRowData();
        return traerValorCertificacion(c);
    }

    public double traerValorCertificacion(Certificaciones c) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("certificacion", c);
        parametros.put(";where", "o.certificacion=:certificacion");
        try {
            return ejbDetalles.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoCertificacion() {
        Certificaciones c = (Certificaciones) certificaciones.getRowData();
        return traerSaldoCert(c);
    }

    public double traerSaldoCert(Certificaciones c) {
        Map parametros = new HashMap();
        parametros.put("certificacion", c);
        parametros.put(";where", "o.compromiso.certificacion=:certificacion");
        try {
            parametros.put(";campo", "o.valor");
//            double valorComprometido = ejbDetComp.sumarCampo(parametros).doubleValue();
//
//            parametros = new HashMap();
//            parametros.put("certificacion", c);
//            parametros.put(";where", "o.compromiso.certificacion=:certificacion");
//            parametros.put(";campo", "o.saldo");
//            double valorSaldo = ejbDetComp.sumarCampo(parametros).doubleValue();
//            return valorComprometido - valorSaldo;
            return ejbDetComp.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getValorCertificaciones() {
        return calculaTotalCertificaciones(asignacion);
    }

    public double getValorReformas() {
        return calculaTotalReformas(asignacion);
    }

    public double calculaTotalReformas(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put("anio", anio);
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true  and o.asignacion.proyecto.anio=:anio");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String anular(Certificaciones certifica) {
        detalles = null;
        try {
            this.certificacion = certifica;
            double saldo;
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            parametros.put(";campo", "o.valor");
            saldo = ejbDetalles.sumarCampo(parametros).doubleValue();
            if (saldo == 0) {
                MensajesErrores.advertencia("No existe saldo");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            List<Compromisos> lista = ejbCompromisos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("Certificación tiene Compromiso");
                return null;
            }
            List<Detallecertificaciones> listaDetalle = ejbDetalles.encontarParametros(parametros);
            for (Detallecertificaciones dc : listaDetalle) {

                parametros = new HashMap();
                parametros.put(";where", "o.detallecertificacion=:detallecertificacion");
                parametros.put("detallecertificacion", dc);
                List<Detallecompromiso> listaDetalleCompromiso = ejbDetComp.encontarParametros(parametros);
                if (!listaDetalleCompromiso.isEmpty()) {
                    MensajesErrores.advertencia("Certificación tiene Compromiso");
                    return null;
                }
            }
            parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            detalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnular.editar();
        return null;
    }

    public String grabarAnular() {
        if ((certificacion.getObservacionanulado() == null) || (certificacion.getObservacionanulado().isEmpty())) {
            MensajesErrores.advertencia("Es necesario observación");
            return null;
        }
        if (certificacion.getFechaanulado() == null) {
            MensajesErrores.advertencia("Es necesario fecha de anulación");
            return null;
        }
        certificacion.setUsuarioanulado(seguridadbean.getLogueado().getUserid());
        certificacion.setAnulado(Boolean.TRUE);
        try {
            for (Detallecertificaciones dc : detalles) {
                Detallecertificaciones dNuevo = new Detallecertificaciones();
                Double total = -dc.getValor().doubleValue();
                dNuevo.setAsignacion(dc.getAsignacion());
                dNuevo.setValor(new BigDecimal(total));
                dNuevo.setCertificacion(certificacion);
                dNuevo.setFecha(certificacion.getFechaanulado());
                ejbDetalles.create(dNuevo, seguridadbean.getLogueado().getUserid());
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion.id=:certificacion");
            parametros.put("certificacion", certificacion.getPacpoa());
            List<Detallecertificacionespoa> listaDetCertPoa = ejbDetallepoa.encontarParametros(parametros);
            for (Detallecertificacionespoa dcp : listaDetCertPoa) {
                Detallecertificacionespoa dNuevoPoa = new Detallecertificacionespoa();
                Double total = -dcp.getValor().doubleValue();
                dNuevoPoa.setAsignacion(dcp.getAsignacion());
                dNuevoPoa.setValor(new BigDecimal(total));
                dNuevoPoa.setCertificacion(dcp.getCertificacion());
                dNuevoPoa.setFecha(certificacion.getFechaanulado());
                ejbDetallepoa.create(dNuevoPoa, seguridadbean.getLogueado().getUserid());
            }
            Integer buscarCertificacion = certificacion.getPacpoa();
            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", buscarCertificacion);
            List<Certificacionespoa> listaCert = ejbCertificacionesPoa.encontarParametros(parametros);
            if (!listaCert.isEmpty()) {
                Certificacionespoa c = listaCert.get(0);
                if (c != null) {
                    c.setAnulado(Boolean.TRUE);
                    ejbCertificacionesPoa.edit(c, seguridadbean.getLogueado().getUserid());
                }
            }
            ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAnular.cancelar();
        return null;
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

    public Detallecertificaciones traer(Integer id) throws ConsultarException {
        return ejbDetalles.find(id);
    }

    public String getTotalCertificacion() {
        double retorno = 0;
        if (detalles == null) {
            return "0.00";
        }
        for (Detallecertificaciones d : detalles) {
            retorno += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public SelectItem[] getComoDetalle() {
        if (asignacionesBean.getFuente() == null) {
            return null;
        }
        if (proyectoBean.getProyectoSeleccionado() == null) {
            return null;
        }
//        if (asignacionesBean.getPartida() == null) {
//            return null;
//        }
        Map parametros = new HashMap();
        try {
            parametros.put(";where", "o.asignacion.fuente=:fuente and "
                    + " o.asignacion.proyecto=:proyecto and "
                    + " o.certificacion.impreso=true and "
                    + " o.certificacion.roles=false and "
                    + " o.certificacion.anulado=false");
            parametros.put("fuente", asignacionesBean.getFuente());
            parametros.put("proyecto", proyectoBean.getProyectoSeleccionado());
//            parametros.put("proyecto", asignacionesBean.getPartida());
            return Combos.getSelectItems(ejbDetalles.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(CertificacionesBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//    public String getProgramaStr() {
//        return traerPrograma().toString();
//    }
//    private Proyectos traerPrograma(){
//        Codigos c = getCodigosBean().traerCodigo("LONPRO", "PROG");
//        int longitud = Integer.parseInt(c.getParametros());
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.tipo='PROG' and o.anio=:anio and o.codigo=:codigo");
//        parametros.put("anio", certificacion.getAnio());
//        parametros.put("codigo", certificacion.getAsignacion().getProyecto().getCodigo().subSequence(0, longitud));
//        try {
//            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
//            for (Proyectos p:listaProyectos){
//                return p;
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//    public String getProyectoStr() {
//        Codigos c = getCodigosBean().traerCodigo("LONPRO", "PROY");
//        int longitud = Integer.parseInt(c.getParametros());
//        Map parametros = new HashMap();
//        Proyectos pt=traerPrograma();
//        parametros.put(";where", "o.tipo='PROY' and o.anio=:anio and o.codigo=:codigo");
//        parametros.put("anio", compromiso.getDetcertificacion().getCertificacion().getAnio());
//        parametros.put("codigo",  compromiso.getDetcertificacion().getAsignacion().getProyecto().getCodigo().subSequence(0,pt.getCodigo().length()+ longitud));
//        try {
//            List<Proyectos> listaProyectos = ejbProyectos.encontarParametros(parametros);
//            for (Proyectos p:listaProyectos){
//                return p.getCodigo()+" - "+p.getNombre();
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(ProyectosBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

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
//    public String seleccionaCertificacion(Certificaciones certificacion){
//        this.certificacion=certificacion;
//        compromisosBean.setCertificacion(certificacion);
//        compromisosBean.getFormularioCertificacion().cancelar();
//        compromisosBean.getFormulario().insertar();
//        return null;
//    }

//    /**
//     * @return the compromisosBean
//     */
//    public CompromisosBean getCompromisosBean() {
//        return compromisosBean;
//    }
//
//    /**
//     * @param compromisosBean the compromisosBean to set
//     */
//    public void setCompromisosBean(CompromisosBean compromisosBean) {
//        this.compromisosBean = compromisosBean;
//    }
    public boolean isEditar() {
        Certificaciones c = (Certificaciones) certificaciones.getRowData();
        Map parametros = new HashMap();
        parametros.put(";where", "o.detallecertificacion.certificacion=:certificacion");
        parametros.put("certificacion", c);
        try {
            int total = ejbDetComp.contar(parametros);
            return total != 0;

        } catch (ConsultarException ex) {
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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

    public String getBlancos() {
        return "<br><br> <br> <br> <br>";
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

    public List<Clasificadores> getListaClasificadores() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.proyecto.anio=:anio");
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.clasificador.codigo");
        try {
            List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);

            List<Clasificadores> listaClasificadores = new LinkedList<>();

            for (Asignaciones x : lAsignaciones) {
                double reformas = calculosBean.traerReformas(x, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), null, null, anio);
                // ver la suma de certificaciones eso resta el valor actual
                double valorCert = calculosBean.traerCertificaciones(x, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), detalle, null, anio);
                double saldo = x.getValor().doubleValue() + reformas - valorCert;
                if (saldo > 0) {
                    // ver si el clasificador esta ya en la lista
                    estaYaclasificador(x.getClasificador(), listaClasificadores);
                }
            }
            return listaClasificadores;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void estaYaclasificador(Clasificadores c, List<Clasificadores> listaClasificadores) {
        for (Clasificadores c1 : listaClasificadores) {
            if (c1.equals(c)) {
                return;
            }
        }
        listaClasificadores.add(c);
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

    public SelectItem[] getComboFuenteClasificador() {
        if (partida == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador.codigo=:clasificador and o.proyecto.anio=:anio");
        parametros.put("clasificador", partida);
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.fuente.nombre");
        try {
            List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);
            List<Codigos> listaFuentes = new LinkedList<>();

            for (Asignaciones x : lAsignaciones) {
                double reformas = calculosBean.traerReformas(x, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), null, x.getFuente(), anio);
                double valorCert = calculosBean.traerCertificaciones(x, configuracionBean.getConfiguracion().getPinicial(), configuracionBean.getConfiguracion().getPfinal(), detalle, x.getFuente(), anio);
                double saldo = x.getValor().doubleValue() + reformas - valorCert;
                if (saldo > 0) {
                    // ver si el clasificador esta ya en la lista
                    esta(x.getFuente(), listaFuentes);
                }

            }
            return Combos.getSelectItems(listaFuentes, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void esta(Codigos f, List<Codigos> listaFuentes) {
        for (Codigos f1 : listaFuentes) {
            if (f.equals(f1)) {
                return;
            }
        }
        listaFuentes.add(f);
    }

    public SelectItem[] getComboClasificadorFuente() {
        if (getFuente() == null) {
            return null;
        }
        if (partida == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador.codigo=:clasificador and o.fuente=:fuente and o.proyecto.anio=:anio");
        parametros.put("clasificador", partida);
        parametros.put("fuente", getFuente());
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.proyecto.codigo,o.fuente.nombre");
        try {
            List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);
            int size = lAsignaciones.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Asignaciones x : lAsignaciones) {
                items[i++] = new SelectItem(x, x.getProyecto().getCodigo() + " - " + x.getProyecto().getNombre());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String ajusteCertificacion(Certificaciones cert) {
        certificacion = cert;
        fechaLiberar = certificacion.getFecha();
        detalle = new Detallecertificaciones();
        saldoLiberar = 0;
        formularioAjuste.insertar();
        return null;
    }

    public String grabarAjusteCertificacion() {
        try {
            if (saldoLiberar > getValorSumado2()) {
                MensajesErrores.advertencia("El valor de ajuste es mayor que el saldo asignado");
                return null;
            }
            Detallecertificaciones dNuevo = new Detallecertificaciones();
            dNuevo.setAsignacion(detalle.getAsignacion());
            dNuevo.setValor(new BigDecimal(saldoLiberar));
            dNuevo.setCertificacion(certificacion);
            dNuevo.setFecha(fechaLiberar);
            ejbDetalles.create(dNuevo, seguridadbean.getLogueado().getUserid());

            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion.id=:certificacion");
            parametros.put("certificacion", certificacion.getPacpoa());
            List<Detallecertificacionespoa> listaDetCertPoa = ejbDetallepoa.encontarParametros(parametros);
            if (!listaDetCertPoa.isEmpty()) {
                Detallecertificacionespoa dcp = listaDetCertPoa.get(0);
                Detallecertificacionespoa dNuevoPoa = new Detallecertificacionespoa();
                dNuevoPoa.setAsignacion(dcp.getAsignacion());
                dNuevoPoa.setValor(new BigDecimal(saldoLiberar));
                dNuevoPoa.setCertificacion(dcp.getCertificacion());
                dNuevoPoa.setFecha(fechaLiberar);
                ejbDetallepoa.create(dNuevoPoa, seguridadbean.getLogueado().getUserid());
            }

        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException ex) {
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioAjuste.cancelar();
        return null;
    }

    public double getValorSumado() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        saldoLiberar = 0;
        double total = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("certificacion", certificacion);
            List<Detallecertificaciones> lista = ejbDetalles.encontarParametros(parametros);
            double certifica = 0;
            if (!lista.isEmpty()) {
                detalle = lista.get(0);
                certifica = detalle.getValor().doubleValue();
            }

            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", detalle.getAsignacion());
            double reformas = ejbReformas.sumarCampo(parametros).doubleValue();
            double asignac = detalle.getAsignacion().getValor().doubleValue();

            double totals = asignac + reformas - (certifica);
            double cuadre = Math.round((totals) * 100);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            total = valortotal.doubleValue();
            if (saldoLiberar == 0) {
                saldoLiberar = total;
            }
            return total;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getValorSumado2() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        double total = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("certificacion", certificacion);
            List<Detallecertificaciones> lista = ejbDetalles.encontarParametros(parametros);
            double certifica = 0;
            if (!lista.isEmpty()) {
                detalle = lista.get(0);
                certifica = detalle.getValor().doubleValue();
            }

            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", detalle.getAsignacion());
            double reformas = ejbReformas.sumarCampo(parametros).doubleValue();
            double asignac = detalle.getAsignacion().getValor().doubleValue();

            double totals = asignac + reformas - (certifica);
            double cuadre = Math.round((totals) * 100);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            total = valortotal.doubleValue();

            return total;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public SelectItem[] getComboAsignacionCertificacion() {
        if (certificacion == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            List<Detallecertificaciones> lDetalles = ejbDetalles.encontarParametros(parametros);
            Asignaciones a = null;
            List<Detallecertificaciones> lDetalles1 = new LinkedList<>();
            for (Detallecertificaciones x : lDetalles) {
                if (x.getValor().doubleValue() > 0) {
                    if (a == null) {
                        a = x.getAsignacion();
                        lDetalles1.add(x);
                    }
                    if (!(a.equals(x.getAsignacion()))) {
                        lDetalles1.add(x);
                        a = x.getAsignacion();
                    }
                }
            }
            int size = lDetalles1.size() + 1;
            SelectItem[] items = new SelectItem[size];
            int i = 0;
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
            for (Detallecertificaciones x : lDetalles1) {
                items[i++] = new SelectItem(x.getAsignacion(), x.getAsignacion().toString());
            }
            return items;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String liberarSaldo(Certificaciones cert) {
        certificacion = cert;
        fechaLiberar = certificacion.getFecha();
        saldoLiberar = 0;
        detalle = new Detallecertificaciones();
        formularioLiberarSaldo.insertar();
        return null;
    }

    public String grabarLiberarSaldo() {
//        Asignaciones a = detalle.getAsignacion();
        try {
            if (saldoLiberar > getValorComprometido2()) {
                MensajesErrores.advertencia("El saldo a liberar es mayor que el saldo certificado");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion and o.asignacion=:asignacion");
            parametros.put("certificacion", certificacion);
            parametros.put("asignacion", detalle.getAsignacion());
            List<Detallecertificaciones> listaDetalleCert = ejbDetalles.encontarParametros(parametros);
            Double total = 0.00;
            if (!listaDetalleCert.isEmpty()) {
                Detallecertificaciones dNuevo = new Detallecertificaciones();
                dNuevo.setAsignacion(detalle.getAsignacion());
                dNuevo.setValor(new BigDecimal(-saldoLiberar));
                dNuevo.setCertificacion(certificacion);
                dNuevo.setFecha(fechaLiberar);
                ejbDetalles.create(dNuevo, seguridadbean.getLogueado().getUserid());
            }
            parametros = new HashMap();
            parametros.put(";where", "o.certificacion.id=:certificacion and o.asignacion.partida.codigo=:codigo and o.asignacion.proyecto.codigo=:proyecto");
            parametros.put("certificacion", certificacion.getPacpoa());
            parametros.put("codigo", detalle.getAsignacion().getClasificador().getCodigo());
            parametros.put("proyecto", detalle.getAsignacion().getProyecto().getCodigo());
            List<Detallecertificacionespoa> listaDetCertPoa = ejbDetallepoa.encontarParametros(parametros);
            if (!listaDetCertPoa.isEmpty()) {
                Detallecertificacionespoa dPoa = listaDetCertPoa.get(0);
                Detallecertificacionespoa dNuevoPoa = new Detallecertificacionespoa();
                dNuevoPoa.setAsignacion(dPoa.getAsignacion());
                dNuevoPoa.setValor(new BigDecimal(-saldoLiberar));
                dNuevoPoa.setCertificacion(dPoa.getCertificacion());
                dNuevoPoa.setFecha(fechaLiberar);
                ejbDetallepoa.create(dNuevoPoa, seguridadbean.getLogueado().getUserid());
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioLiberarSaldo.cancelar();
        return null;
    }

    public double getValorComprometido() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        saldoLiberar = 0;
        double total = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("certificacion", certificacion);
//            List<Detallecertificaciones> lista = ejbDetalles.encontarParametros(parametros);
//            double certifica = 0;
//            if (!lista.isEmpty()) {
//                detalle = lista.get(0);
//                certifica = detalle.getValor().doubleValue();
//            }
            double certifica = ejbDetalles.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion=:certificacion");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("certificacion", certificacion);
            double valorCompromisos = ejbDetComp.sumarCampo(parametros).doubleValue();

            double totals = certifica - (valorCompromisos);
            double cuadre = Math.round((totals) * 100);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            total = valortotal.doubleValue();
            if (saldoLiberar == 0) {
                saldoLiberar = total;
            }
            return total;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getValorComprometido2() {
        if (detalle == null) {
            return 0;
        }
        if (detalle.getAsignacion() == null) {
            return 0;
        }
        double total = 0;
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion=:certificacion");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("certificacion", certificacion);
            double certifica = ejbDetalles.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion=:certificacion");
            parametros.put("asignacion", detalle.getAsignacion());
            parametros.put("certificacion", certificacion);
            double valorCompromisos = ejbDetComp.sumarCampo(parametros).doubleValue();

            double totals = certifica - (valorCompromisos);
            double cuadre = Math.round((totals) * 100);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            total = valortotal.doubleValue();
            return total;

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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

    /**
     * @return the certificacionesCompromiso
     */
    public List<Certificaciones> getCertificacionesCompromiso() {
        return certificacionesCompromiso;
    }

    /**
     * @param certificacionesCompromiso the certificacionesCompromiso to set
     */
    public void setCertificacionesCompromiso(List<Certificaciones> certificacionesCompromiso) {
        this.certificacionesCompromiso = certificacionesCompromiso;
    }

    /**
     * @return the numeroControlPoa
     */
    public Integer getNumeroControlPoa() {
        return numeroControlPoa;
    }

    /**
     * @param numeroControlPoa the numeroControlPoa to set
     */
    public void setNumeroControlPoa(Integer numeroControlPoa) {
        this.numeroControlPoa = numeroControlPoa;
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
     * @return the formularioAnular
     */
    public Formulario getFormularioAnular() {
        return formularioAnular;
    }

    /**
     * @param formularioAnular the formularioAnular to set
     */
    public void setFormularioAnular(Formulario formularioAnular) {
        this.formularioAnular = formularioAnular;
    }

    /**
     * @return the numeroSistemaPoa
     */
    public Integer getNumeroSistemaPoa() {
        return numeroSistemaPoa;
    }

    /**
     * @param numeroSistemaPoa the numeroSistemaPoa to set
     */
    public void setNumeroSistemaPoa(Integer numeroSistemaPoa) {
        this.numeroSistemaPoa = numeroSistemaPoa;
    }

    /**
     * @return the fechaLiberar
     */
    public Date getFechaLiberar() {
        return fechaLiberar;
    }

    /**
     * @param fechaLiberar the fechaLiberar to set
     */
    public void setFechaLiberar(Date fechaLiberar) {
        this.fechaLiberar = fechaLiberar;
    }

    /**
     * @return the saldoLiberar
     */
    public double getSaldoLiberar() {
        return saldoLiberar;
    }

    /**
     * @param saldoLiberar the saldoLiberar to set
     */
    public void setSaldoLiberar(double saldoLiberar) {
        this.saldoLiberar = saldoLiberar;
    }

    /**
     * @return the formularioAjuste
     */
    public Formulario getFormularioAjuste() {
        return formularioAjuste;
    }

    /**
     * @param formularioAjuste the formularioAjuste to set
     */
    public void setFormularioAjuste(Formulario formularioAjuste) {
        this.formularioAjuste = formularioAjuste;
    }

    /**
     * @return the formularioLiberarSaldo
     */
    public Formulario getFormularioLiberarSaldo() {
        return formularioLiberarSaldo;
    }

    /**
     * @param formularioLiberarSaldo the formularioLiberarSaldo to set
     */
    public void setFormularioLiberarSaldo(Formulario formularioLiberarSaldo) {
        this.formularioLiberarSaldo = formularioLiberarSaldo;
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

    public String imprimi(Certificaciones cert) {
        try {
            certificacion = cert;
            recurso = null;
            traerImagen();
//        if (certificacion.getAnulado()) {
//            MensajesErrores.advertencia("Certificación ya anulada");
//            return null;
//        }

            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", certificacion);
            List<Auxiliar> titulos = proyectoBean.getTitulos();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            detalles = ejbDetalles.encontarParametros(parametros);
            double total = 0;
            List<Detallecertificaciones> listaReporte = new LinkedList<>();
            List<AuxiliarReporte> columnasReporte = new LinkedList<>();
            for (Detallecertificaciones d : detalles) {
                detalle = d;
                double asignado = d.getAsignacion().getValor().doubleValue() * 100;
                double reformas = calculaTotalReformas(d.getAsignacion()) * 100;
                double tCertificaciones = calculaTotalCertificaciones(d.getAsignacion()) * 100;
                double suma = asignado + reformas - tCertificaciones;
                double valor = d.getValor().doubleValue() * 100;
                double compara = Math.round(suma - valor);
                if (compara < 0) {
                    MensajesErrores.advertencia("Certificación exede el saldo de la partida " + d.getAsignacion().getClasificador().getCodigo());
//                    return null;
                }
                total += d.getValor().doubleValue();
                String que = "";
                for (Auxiliar a : titulos) {
                    que += a.getTitulo1() + " : ";
                    que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                }
                que += " - Producto : " + d.getAsignacion().getProyecto().toString() + " ";
                d.setArbolProyectos(que);
                if (d.getAsignacion() != null) {
                    listaReporte.add(d);
                    // aqui toca el reporte
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf1.format(d.getFecha() == null ? new Date() : d.getFecha())));
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                    columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                    columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getArbolProyectos()));
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getCodigo()));
                    columnasReporte.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(d.getValor().doubleValue())));
                }

            }
            formularioImpresion.editar();
            if (certificacion.getImpreso() == null) {
                certificacion.setImpreso(false);
            }
            if (certificacion.getTipodocumento() == null) {
                List<Codigos> tipoDoc = codigosBean.traerCodigoMaestro(Constantes.DOCUMENTOS_PRESUPUESTO);
                for (Codigos c : tipoDoc) {
                    certificacion.setTipodocumento(c);
                }
                ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            imprimir = certificacion.getImpreso();
            Detallecertificaciones d1 = new Detallecertificaciones();
            d1.setValor(new BigDecimal(total));
            detalles.add(d1);
            DecimalFormat df = new DecimalFormat("###,##0.00");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("CERTIFICACIÓN PRESUPUESTARIA\n");

//          "CERTIFICACION PRESUPUESTARIA DEL AÑO " + anio
//            pdf.agregaTitulo("CERTIFICACION PRESUPUESTARIA DEL AÑO " + anio);
//            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Disponibilidad :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, certificacion.getNumerocert() + ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf.format(certificacion.getFecha()).toUpperCase()));
            pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio
                    + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Dirección :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, certificacion.getDireccion() == null
                    ? "" : certificacion.getDireccion().toString()));
            pdf.agregarTabla(null, columnas, 2, 100, null);

            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBLE"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            pdf.agregarTablaReporte(titulosReporte, columnasReporte, 6, 100, "PARTIDAS");
            pdf.agregaParrafo("Observaciones :" + certificacion.getMotivo());
            pdf.agregaParrafo("\nEl Monto de la disponibilidad asciende a: " + df.format(total) + "\n");

            double cuadre = Math.round(total * 100);
            double dividido = cuadre / 100;
            BigDecimal valortotal = new BigDecimal(dividido).setScale(2, RoundingMode.HALF_UP);
            double totalS = (valortotal.doubleValue());

            DecimalFormat df1 = new DecimalFormat("0.00");
            pdf.agregaParrafo("\nTotal disponibilidad: " + ConvertirNumeroALetras.convertNumberToLetter(totalS) + "\n");
//            pdf.agregaParrafo("\nTotal disponibilidad: " + ConvertirNumeroALetras.convertNumberToLetter(df1.format(total)) + "\n");
//            pdf.agregaParrafo("\nTotal disponibilidad: " + ConvertirNumeroALetras.convertNumberToLetter("6585.60") + "\n");
            Codigos cod1 = ejbCodigos.traerCodigo("FIRCERT", "01");
            if (cod1 == null) {
                MensajesErrores.advertencia("No existe codigo de firmas de Certificación");
                return null;
            }
            Codigos cod2 = ejbCodigos.traerCodigo("FIRCERT", "02");
            if (cod2 == null) {
                MensajesErrores.advertencia("No existe codigo de firmas de Certificación");
                return null;
            }
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod1.getDescripcion() != null ? cod1.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod2.getDescripcion() != null ? cod2.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod1.getParametros() != null ? cod1.getParametros() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, cod2.getParametros() != null ? cod2.getParametros() : ""));

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reportepdf = pdf.traerRecurso();
            archivoFirmar = pdf.traerArchivo();
        } catch (IOException | DocumentException | ConsultarException | GrabarException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String traerImagen() throws GrabarException {
        try {
            recurso = null;
            if (certificacion != null) {
                if (certificacion.getArchivo() != null) {
                    nombreArchivo = "Certificacion: " + certificacion.getNumerocert();
                    tipoArchivo = "Certificacion Presupuestaria";
                    recurso = new Recurso(Files.readAllBytes(Paths.get(certificacion.getArchivo())));
                }
            }
        } catch (IOException ex) {
            if (recurso == null) {
                certificacion.setArchivo(null);
                ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            MensajesErrores.advertencia("Documento firmado no se encuentra");
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public String imprimirMovimientos(Certificaciones certificacion) {
        this.certificacion = certificacion;
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        try {
            detalles = ejbDetalles.encontarParametros(parametros);
            double total = 0;
            List<AuxiliarReporte> columnasReporte = new LinkedList<>();
            pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
            DecimalFormat df = new DecimalFormat("###,##0.00");
            SimpleDateFormat sdf = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            pdf.agregaTitulo("CERTIFICACIÓN PRESUPUESTARIA\n");
            pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio
                    + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Disponibilidad :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, certificacion.getNumerocert() + ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf.format(certificacion.getFecha()).toUpperCase()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Dirección :"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, certificacion.getDireccion() == null
                    ? "" : certificacion.getDireccion().toString()));
            pdf.agregarTabla(null, columnas, 2, 100, null);
            for (Detallecertificaciones d : detalles) {
                detalle = d;
                double asignado = d.getAsignacion().getValor().doubleValue() * 100;
                double reformas = calculaTotalReformas(d.getAsignacion()) * 100;
                double tCertificaciones = calculaTotalCertificaciones(d.getAsignacion()) * 100;
                double suma = asignado + reformas - tCertificaciones;
                double valor = d.getValor().doubleValue() * 100;
                double compara = Math.round(suma - valor);
                if (compara < 0) {
                    MensajesErrores.advertencia("Certificación exede el saldo de la partida " + d.getAsignacion().getClasificador().getCodigo());
                }
                total += d.getValor().doubleValue();

                pdf.agregaParrafo("\n\n");
                List<AuxiliarReporte> titulosReporte = new LinkedList<>();
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fecha"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
                titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
                titulosReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
                titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
                String que = "";
                for (Auxiliar a : titulos) {
                    que += a.getTitulo1() + " : ";
                    que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                }
                que += " - Producto : " + d.getAsignacion().getProyecto().toString() + " ";
                d.setArbolProyectos(que);
                if (d.getAsignacion() != null) {
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf1.format(d.getFecha() == null ? new Date() : d.getFecha())));
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                    columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                    columnasReporte.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getArbolProyectos()));
                    columnasReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getCodigo()));
                    columnasReporte.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, Math.abs(d.getValor().doubleValue())));
                }
                pdf.agregarTablaReporte(titulosReporte, columnasReporte, 6, 100, "CERTIFICACIÓN");
            }

            formularioImpresion.editar();
            if (certificacion.getImpreso() == null) {
                certificacion.setImpreso(false);
            }
            if (certificacion.getTipodocumento() == null) {
                List<Codigos> tipoDoc = codigosBean.traerCodigoMaestro(Constantes.DOCUMENTOS_PRESUPUESTO);
                for (Codigos c : tipoDoc) {
                    certificacion.setTipodocumento(c);
                }
                ejbCertificaciones.edit(certificacion, seguridadbean.getLogueado().getUserid());
            }
            imprimir = certificacion.getImpreso();

            pdf.agregaParrafo("Observaciones : " + certificacion.getMotivo());
            pdf.agregaParrafo("\nTotal disponibilidad: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");

            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.error(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConsultarException | GrabarException ex) {
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the codigoNumero
     */
    public int getCodigoNumero() {
        return codigoNumero;
    }

    /**
     * @param codigoNumero the codigoNumero to set
     */
    public void setCodigoNumero(int codigoNumero) {
        this.codigoNumero = codigoNumero;
    }

    /**
     * @return the cod
     */
    public Codigos getCod() {
        return cod;
    }

    /**
     * @param cod the cod to set
     */
    public void setCod(Codigos cod) {
        this.cod = cod;
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
