/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.faces.application.Resource;

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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.ConvertirNumeroALetras;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.auxiliares.sfccbdmq.Reportesds;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.FormatosFacade;
import org.beans.sfccbdmq.PagosempleadosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.contabilidad.sfccbdmq.DeatalleAnalisisBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Pagosempleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.mozilla.jss.asn1.BOOLEAN;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "compromisosSobreSfccbdmq")
@ViewScoped
public class CompromisosRolBean {

    /**
     * Creates a new instance of CertificacionesBean
     */
    public CompromisosRolBean() {

        listaCertificaciones = new LazyDataModel<Compromisos>() {

            @Override
            public List<Compromisos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }
    private LazyDataModel<Compromisos> compromisos;
    private LazyDataModel<Compromisos> listaCertificaciones;
    private List<Detallecompromiso> detallees;
    private List<Detallecertificaciones> detallesCertificaciones;
    private Detallecompromiso detalle;
    private Detallecertificaciones detalleCertificacion;
    private List<Detallecertificaciones> detallesCertb;
    private List<Pagosempleados> pagosConceptos;
    private List<Pagosempleados> pagosRmu;
    private Certificaciones certificacion;
    private Asignaciones asignaconDetalle;
    private Compromisos compromiso;
    private double valorAnt;
    private int anio;
    private int mes;
    private Integer numeroControl;
    private String motivo;
    private boolean imprimirNuevo;
    private Formulario formulario = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioCertificacion = new Formulario();
    private Formulario formularioDetalle = new Formulario();
    private Formulario formularioConcepto = new Formulario();
    private boolean compromisoConcepto = false;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private FormatosFacade ejbFormatos;
    @EJB
    private CertificacionesFacade ejbCertificacion;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private DetallecertificacionesFacade ejbDetalles;
    @EJB
    private PagosempleadosFacade ejbPagosEmpleados;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private ReformasFacade ejbReformas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectoBean;
    @ManagedProperty(value = "#{asignacionesSfccbdmq}")
    private AsignacionesBean asignacionesBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    private Perfiles perfil;
    private Date desde;
    private Integer numeroCertificacion;
    private Integer numero;
    private Conceptos concepto;
    private Date hasta;
    private Boolean muchos;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporteCompromiso;
    private Resource reporteCertificacion;
    private int codigoNumero;
    private int anioCompromiso;
    private Date fechaCompromiso;

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
        desde = configuracionBean.getConfiguracion().getPinicial();
        hasta = configuracionBean.getConfiguracion().getPfinal();
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH) + 1;
        c.setTime(new Date());
        anioCompromiso = c.get(Calendar.YEAR);
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfil = (String) params.get("p");
        String nombreForma = "CompromisosSobreVista";
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
        fechaCompromiso = new Date();
    }

    public String buscar() {

        listaCertificaciones = new LazyDataModel<Compromisos>() {
            @Override
            public List<Compromisos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.fecha desc");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, mes - 1);
                c.set(Calendar.YEAR, anio);
                c.set(Calendar.DATE, 1);
                desde = c.getTime();
                c.set(Calendar.MONTH, mes);
                c.set(Calendar.YEAR, anio);
                c.set(Calendar.DATE, 1);
                hasta = c.getTime();
                String where = "o.anio=:anio and   o.nomina=true "
                        //                String where = "  o.anio=:anio and o.roles=false"
                        + " and o.proveedor is null and o.fecha between :desde and :hasta";
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

                if (numeroControl != null) {
                    if (numeroControl > 0) {
                        where += " and  o.numerocomp=:id";
//                        parametros = new HashMap();
                        parametros.put("id", numeroControl);
                    }
                }
                int total = 0;
                try {
                    parametros.put(";where", where);
                    total = ejbCompromisos.contar(parametros);
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
                listaCertificaciones.setRowCount(total);
                try {
                    return ejbCompromisos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        return null;
    }

//    public String nuevo() {
//        formularioCertificacion.editar();
//        return null;
//    }
    public String crear() {
        // ver si ya esta eha para este mes
        // tocaria borrar lo que esta hecho
        // ver si para el mes y año  de la nomina ya esta creada la certificación
        try {
            codigoNumero = 0;
            Codigos codi;
            if (anio == anioCompromiso) {
                codi = ejbCodigos.traerCodigo("NUM", "09");
                if (codi == null) {
                    MensajesErrores.advertencia("No existe numeración para compromisos Actuales");
                    return null;
                }
                if (codi.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }

                Integer num = Integer.parseInt(codi.getParametros());
                Integer nume = num + 1;
                codigoNumero = nume;
            } else {
                codi = ejbCodigos.traerCodigo("NUM", "05");
                if (codi == null) {
                    MensajesErrores.advertencia("No existe numeración para Compromisos Anteriores");
                    return null;
                }
                if (codi.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }
                Integer num = Integer.parseInt(codi.getParametros());
                Integer nume = num + 1;
                codigoNumero = nume;
            }

            concepto = null;
            Map parametros = new HashMap();
            String where = "o.mes=:mes and o.anio=:anio "
                    + " and o.clasificador is not null "
                    + " and o.liquidacion=false "
                    + " and o.pagado=true";
            // traer compromiso y ver si ya esta impreso
            Calendar cCert = Calendar.getInstance();
            cCert.set(Calendar.MONTH, mes - 1);
            cCert.set(Calendar.YEAR, anio);
            cCert.set(Calendar.DATE, 1);
            if (cCert.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Mes anterior a periodo vigente");
                return null;
            }
            // al primero de mes
            parametros.put("where", where);
//            parametros.put("fecha", configuracionBean.getConfiguracion().getPvigente());
//            parametros.put("fecha", cCert.getTime());
            parametros.put("anio", anio);
            parametros.put("mes", mes);
            int total = ejbPagosEmpleados.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Mes ya generado presupuesto");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.escxp=true");
            List<Formatos> lFormatos = ejbFormatos.encontarParametros(parametros);
            String ctaInicio = "";
            String ctaFin = "";
            for (Formatos f : lFormatos) {
                ctaInicio = f.getCxpinicio();
                ctaFin = f.getCxpfin();
            }
            certificacion = new Certificaciones();
            compromiso = new Compromisos();
            detallees = new LinkedList<>();
            detallesCertificaciones = new LinkedList<>();
            // traer todo lo que esta en el rol y compparar con la certificacion
            detallees = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio "
                    + " and o.valor is not null "
                    + " and (o.liquidacion=false or o.liquidacion is null) "
                    + " and o.prestamo is null "
                    + " and o.sancion is null and o.concepto.codigo not in ('D13A','D14A')");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            pagosConceptos = new LinkedList<>();
//            parametros.put(";orden", "o.empleado.proyecto.codigo,o.clasificador");
            List<Pagosempleados> listaPagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
            parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio and o.valor is not null "
                    + " and (o.liquidacion=false or o.liquidacion is null) "
                    + " and o.prestamo is null and o.sancion is null and o.concepto is null");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
            List<Pagosempleados> listaCNULL = ejbPagosEmpleados.encontarParametros(parametros);
            if (!listaCNULL.isEmpty()) {
                listaPagosConceptos.addAll(listaCNULL);
            }

            Codigos subrogacionCodigos = codigosBean.traerCodigoParametro(Constantes.TIPO_ACCIONES, "SUBROGACION");
            Codigos encargoCodigos = codigosBean.traerCodigoParametro(Constantes.TIPO_ACCIONES, "ENCARGO");

            for (Pagosempleados p : listaPagosConceptos) {

                /// nuevo cuando no viene la partida
                String partida = p.getEmpleado().getPartida().substring(0, 2);
                String cuenta = p.getEmpleado().getPartida().substring(2, 6);

                // fin de la partida
                Detallecompromiso d1 = new Detallecompromiso();
                double concepto = p.getValor() == null ? 0 : p.getValor().doubleValue();
                double subrogacion = p.getCantidad() == null ? 0 : p.getCantidad().doubleValue();
                double engargo = p.getEncargo() == null ? 0 : p.getEncargo().doubleValue();
                if (p.getConcepto() == null) {
                    if (engargo > -0.001 && engargo < 0.001 && engargo != 0) {
                        engargo = 0;
                    }
                    // sueldo base
//                    double valor = p.getConcepto() == null ? concepto + subrogacion + engargo : concepto;
                    p.setClasificador(partida + cuenta);
                    Asignaciones a = traerAsignacion(p, false);
                    if (a == null) {
                        MensajesErrores.fatal("No existe Asignación Inicial para RMU : " + p.getClasificador() + " " + p.getEmpleado().toString() + " Proyecto :" + p.getEmpleado().getProyecto());
                        return null;
                    }
                    d1.setAsignacion(a);
                    d1.setValor(new BigDecimal(concepto));
                    estaCompromiso(d1);
                    // lo mismo para el concepto Subrogacion
                    if (subrogacion > 0) {
//                    if (p.getClasificadorencargo() != null) {
                        d1 = new Detallecompromiso();
                        String cuentaSubrogacion = "";
                        if (subrogacionCodigos != null) {
                            cuentaSubrogacion = partida + subrogacionCodigos.getDescripcion();
                        }
                        p.setClasificadorencargo(cuentaSubrogacion);
                        a = traerAsignacion(p, true);
                        if (a == null) {
                            MensajesErrores.fatal("No existe Asignación Inicial para Subrogación : " + p.getClasificadorencargo() + " " + p.getEmpleado().toString() + " Proyecto :" + p.getEmpleado().getProyecto());
                            return null;
                        }
                        d1.setValor(new BigDecimal(subrogacion));
                        d1.setAsignacion(a);
                        p.setFuente(a.getFuente());
                        p.setAsignacionTemporal(a);
                        estaCompromiso(d1);
                    }
                    if (engargo > 0) {

//                    if (p.getClasificadorencargo() != null) {
                        d1 = new Detallecompromiso();
                        String cuentaSubrogacion = "";
                        if (encargoCodigos != null) {
                            cuentaSubrogacion = partida + encargoCodigos.getDescripcion();
                        }
                        p.setClasificadorencargo(cuentaSubrogacion);
                        a = traerAsignacion(p, true);
                        if (a == null) {
                            MensajesErrores.fatal("No existe Asignación Inicial para Encargo : " + p.getClasificadorencargo() + " " + p.getEmpleado().toString() + " Proyecto :" + p.getEmpleado().getProyecto());
                            return null;
                        }
                        d1.setValor(new BigDecimal(engargo));
                        d1.setAsignacion(a);
                        p.setFuente(a.getFuente());
                        p.setAsignacionTemporal(a);
                        estaCompromiso(d1);
                    }
                } else {
                    // concepto normal
                    if (p.getConcepto().getGenerapresupuesto() == null) {
                        p.getConcepto().setGenerapresupuesto(false);
                    }

                    if (!p.getConcepto().getCodigo().equals("D13A")) {
                        if (!p.getConcepto().getCodigo().equals("D14A")) {
                            if (p.getConcepto().getGenerapresupuesto()) {
                                int por = 1;
                                Conceptos campo = p.getConcepto();
                                if (campo.getPartida().trim().length() == 6) {
                                    p.setClasificador(campo.getPartida());
                                } else {
                                    p.setClasificador(partida + campo.getPartida());
                                }

//                        ejbPagosEmpleados.ponerCuentasConcepto(p.getConcepto(), p, ctaInicio, ctaFin, partida);
                                if (campo.getSignopresupuesto() == null) {
                                    campo.setSignopresupuesto(false);
                                }
                                if (campo.getSignopresupuesto()) {
                                    por = por * -1;
                                }
                                if (concepto != 0) {

                                    d1.setValor(new BigDecimal(concepto * por));
                                    Asignaciones a = traerAsignacion(p, false);
                                    if (a == null) {
                                        MensajesErrores.fatal("No existe Asignación Inicial para Concepto : "
                                                + p.getClasificador() + " " + p.getEmpleado().getEntidad().toString()
                                                + " " + campo.getNombre() + " Proyecto :" + p.getEmpleado().getProyecto());
                                        return null;
                                    }
                                    p.setFuente(a.getFuente());
                                    d1.setAsignacion(a);
                                    p.setAsignacionTemporal(a);
                                    estaCompromiso(d1);
                                }
                            }

                        }
                    }
                }

            }
            pagosConceptos = listaPagosConceptos;
            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String crearConcepto() {
        // ver si ya esta eha para este mes
        // tocaria borrar lo que esta hecho
        // ver si para el mes y año  de la nomina ya esta creada la certificación
        try {
            if (concepto == null) {
                MensajesErrores.advertencia("Seleccione un concepto primero");
                return null;
            }
            Calendar cCert = Calendar.getInstance();
            cCert.set(Calendar.MONTH, mes - 1);
            cCert.set(Calendar.YEAR, anio);
            cCert.set(Calendar.DATE, 1);
            if (cCert.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Mes anterior a periodo vigente");
                return null;
            }
            //Buscar si ya esta generado el compromiso
//            Map parametros = new HashMap();
//            String where = "o.clasificador is not null and (o.liquidacion=false or o.liquidacion is null)"
//                    + " and o.pagado=true and o.concepto=:concepto and o.compromiso is not null";
//            parametros.put(";where", where);
//            parametros.put("concepto", concepto);
//            int total = ejbPagosEmpleados.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Mes ya generado presupuesto");
//                return null;
//            }
            certificacion = new Certificaciones();
            compromiso = new Compromisos();
            detallees = new LinkedList<>();
            detallesCertificaciones = new LinkedList<>();
            detallees = new LinkedList<>();
            pagosConceptos = new LinkedList<>();

            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador is not null and (o.liquidacion=false or o.liquidacion is null)"
                    + " and o.pagado=false and o.concepto=:concepto and o.compromiso is null");
            parametros.put("concepto", concepto);
            List<Pagosempleados> listaPagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
            //Agrgar a la lista de pagos los ajustes
            parametros = new HashMap();
            parametros.put(";where", "o.clasificador is not null and (o.liquidacion=false or o.liquidacion is null)"
                    + " and o.pagado=false and o.compromiso is null "
                    + " and o.concepto.titulo=:titulo and o.anio=:anio and o.concepto.codigo!=:codigo");
            parametros.put("titulo", concepto.getTitulo());
            parametros.put("anio", anio);
            parametros.put("codigo", concepto.getCodigo());
            List<Pagosempleados> listaPagosAd = ejbPagosEmpleados.encontarParametros(parametros);
            if (!listaPagosAd.isEmpty()) {
                listaPagosConceptos.addAll(listaPagosAd);
            }
            for (Pagosempleados p : listaPagosConceptos) {
                Detallecompromiso d1 = new Detallecompromiso();
                double conceptoValor = p.getValor() == null ? 0 : p.getValor().doubleValue();
                // concepto normal
                if (p.getConcepto().getGenerapresupuesto() == null) {
                    p.getConcepto().setGenerapresupuesto(true);
                }
                if (p.getConcepto().getGenerapresupuesto()) {
                    int por = 1;
                    if (p.getConcepto().getSignopresupuesto() == null) {
                        p.getConcepto().setSignopresupuesto(false);
                    }
                    if (p.getConcepto().getSignopresupuesto()) {
                        por = por * -1;
                    }
                    d1.setValor(new BigDecimal(conceptoValor * por));
                    Asignaciones a = traerAsignacion(p, false);
                    if (a == null) {
                        MensajesErrores.fatal("No existe Asignación Inicial para Concepto : "
                                + p.getClasificador() + " " + p.getEmpleado().getEntidad().toString()
                                + " " + p.getConcepto().getNombre());
                        return null;
                    }
                    p.setFuente(a.getFuente());
                    d1.setAsignacion(a);
                    p.setAsignacionTemporal(a);
                    estaCompromiso(d1);
                }
            }
            codigoNumero = 0;
            Codigos codi;
            if (anio == anioCompromiso) {
                codi = ejbCodigos.traerCodigo("NUM", "09");
                if (codi == null) {
                    MensajesErrores.advertencia("No existe numeración para compromisos Actuales");
                    return null;
                }
                if (codi.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }

                Integer num = Integer.parseInt(codi.getParametros());
                Integer nume = num + 1;
                codigoNumero = nume;
            } else {
                codi = ejbCodigos.traerCodigo("NUM", "05");
                if (codi == null) {
                    MensajesErrores.advertencia("No existe numeración para Compromisos Anteriores");
                    return null;
                }
                if (codi.getParametros() == null) {
                    MensajesErrores.advertencia("No se encuentra numeración");
                    return null;
                }
                Integer num = Integer.parseInt(codi.getParametros());
                Integer nume = num + 1;
                codigoNumero = nume;
            }
            compromisoConcepto = true;
            pagosConceptos = listaPagosConceptos;
            formulario.insertar();
            formularioConcepto.cancelar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearConConcepto() {
        formularioConcepto.insertar();
        return null;
    }

    public String crearLiquidacion() {
        // ver si ya esta eha para este mes
        // tocaria borrar lo que esta hecho
        // ver si para el mes y año  de la nomina ya esta creada la certificación
        try {
            concepto = null;
            Map parametros = new HashMap();
            String where = "o.fecha=:fecha and o.roles=true";
            // traer compromiso y ver si ya esta impreso
            Calendar cCert = Calendar.getInstance();
            cCert.set(Calendar.MONTH, mes - 1);
            cCert.set(Calendar.YEAR, anio);
            cCert.set(Calendar.DATE, 1);
            if (cCert.getTime().before(configuracionBean.getConfiguracion().getPvigente())) {
                MensajesErrores.advertencia("Mes anterior a periodo vigente");
                return null;
            }
            // al primero de mes
            parametros.put("where", where);
//            parametros.put("fecha", configuracionBean.getConfiguracion().getPvigente());
            parametros.put("fecha", cCert.getTime());
//            parametros.put("anio", anio);
//            int total = ejbCertificacion.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("Mes ya generado presupuesto");
//                return null;
//            }
            certificacion = new Certificaciones();
            compromiso = new Compromisos();
            detallees = new LinkedList<>();
            detallesCertificaciones = new LinkedList<>();
            // traer todo lo que esta en el rol y compparar con la certificacion
            detallees = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.mes=:mes and o.anio=:anio "
                    + "  and o.clasificador is not null "
                    + " and (o.concepto.liquidacion=true or o.concepto.vacaciones=true) "
                    + "and o.pagado=false ");
            parametros.put("mes", mes);
            parametros.put("anio", anio);
//            parametros.put(";orden", "o.empleado.proyecto.codigo,o.clasificador");
            pagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
            for (Pagosempleados p : pagosConceptos) {
                Detallecompromiso d1 = new Detallecompromiso();
                double concepto = p.getValor() == null ? 0 : p.getValor().doubleValue();
                // concepto normal
                int por = 1;
                if (p.getConcepto().getSignopresupuesto() == null) {
                    p.getConcepto().setSignopresupuesto(false);
                }
                if (p.getConcepto().getSignopresupuesto()) {
                    por = por * -1;
                }
                d1.setValor(new BigDecimal(concepto * por));
                Asignaciones a = traerAsignacion(p, false);
                if (a == null) {
                    MensajesErrores.fatal("No existe Asignación Inicial para Concepto : "
                            + p.getClasificador() + " " + p.getEmpleado().getEntidad().toString()
                            + " " + p.getConcepto().getNombre());
                    return null;
                }
                p.setFuente(a.getFuente());
                d1.setAsignacion(a);
                p.setAsignacionTemporal(a);
                estaCompromiso(d1);

            }
            formulario.insertar();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private Asignaciones traerAsignacion(Pagosempleados p, boolean subrogacion) {
        try {
//            if (p.getEmpleado().getId()==68){
//                MensajesErrores.advertencia("Hola");
//            }
//            if (p.getId()==34908){
//                int x=0;
//            }
            double valorPedido = p.getValor().doubleValue();
            Map parametros = new HashMap();
            String clasificadorOriginal = "";
//            parametros.put(";where", " o.clasificador.codigo=:clasificador and o.proyecto=:proyecto");
            parametros.put(";where", " o.clasificador.codigo=:clasificador and o.proyecto=:proyecto and o.proyecto.anio=:anio");
            if (subrogacion) {
                if (p.getClasificadorencargo().length() == 8) {
                    clasificadorOriginal = p.getClasificadorencargo().substring(2, 8);
                } else {
                    clasificadorOriginal = p.getClasificadorencargo();
                }
                parametros.put("clasificador", p.getClasificadorencargo());
                if (p.getEncargo() == null) {
                    p.setEncargo(new Float(0));
                }
                if (p.getCantidad() == null) {
                    p.setCantidad(new Float(0));
                }
                valorPedido = p.getEncargo().doubleValue() + p.getCantidad().doubleValue();
            } else {
                clasificadorOriginal = p.getClasificador();
                parametros.put("clasificador", p.getClasificador());
            }
            parametros.put("proyecto", p.getEmpleado().getProyecto());
            parametros.put("anio", anio);
            List<Asignaciones> listaAsignaciones = ejbAsignaciones.encontarParametros(parametros);
            String setetaUno = "71";
            String cincuentaUno = "51";
            String queViene = p.getClasificador().substring(0, 2);
            int original = 0;
//             if (listaAsignaciones.isEmpty()) {
//                original++;
//                Empleados emp = p.getEmpleado();
//                Entidades ent = emp.getEntidad();
//                String concepto = p.getConcepto() == null ? "RMU" : p.getConcepto().getNombre();
//                MensajesErrores.advertencia("No existe  asignación para :" + p.getEmpleado().getEntidad().toString() + ";" + p.getEmpleado().getId() + " ; " + p.getClasificador() + " ; " + p.getEmpleado().getProyecto().getCodigo() + ";" + concepto);
//                return null;
//            }
//            if (p.getEmpleado().getId()==5){
//                String xx="";
//            }
            if (listaAsignaciones.isEmpty()) {
                // ver clasificador original
                parametros = new HashMap();
                parametros.put(";where", " o.clasificador.codigo=:clasificador and o.proyecto.anio=:anio");
                parametros.put("clasificador", clasificadorOriginal);
                parametros.put("anio", anio);
                listaAsignaciones = ejbAsignaciones.encontarParametros(parametros);
                if (listaAsignaciones.isEmpty()) {

                    String clasificador = p.getClasificador().substring(2, p.getClasificador().length());
                    if (setetaUno.equals(queViene)) {
                        clasificador = cincuentaUno + clasificador;
                    } else {
                        clasificador = setetaUno + clasificador;
                    }
                    original++;
                    // buelve a buscar
                    parametros = new HashMap();
                    parametros.put(";where", " o.clasificador.codigo=:clasificador and o.proyecto=:proyecto and o.proyecto.anio=:anio");
                    parametros.put("clasificador", clasificador);
                    parametros.put("proyecto", p.getEmpleado().getProyecto());
                    parametros.put("anio", anio);
                    listaAsignaciones = ejbAsignaciones.encontarParametros(parametros);
                    if (listaAsignaciones.isEmpty()) {
                        // buscar sin proyecto
                        original++;
                        parametros = new HashMap();
                        parametros.put(";where", " o.clasificador.codigo=:clasificador and o.proyecto.anio=:anio");
                        parametros.put("clasificador", clasificador);
                        parametros.put("anio", anio);
                        listaAsignaciones = ejbAsignaciones.encontarParametros(parametros);
                        if (listaAsignaciones.isEmpty()) {
                            original++;
                            Empleados emp = p.getEmpleado();
                            Entidades ent = emp.getEntidad();
                            String concepto = p.getConcepto() == null ? "RMU" : p.getConcepto().getNombre();
                            MensajesErrores.advertencia("No existe  asignación para :" + p.getEmpleado().getEntidad().toString() + ";" + p.getEmpleado().getId() + " ; " + p.getClasificador() + " ; " + p.getEmpleado().getProyecto().getCodigo() + ";" + concepto);
                            return null;
                        }
                    }
                }
            }
            // ver si hay plata
            Asignaciones as = listaAsignaciones.get(0);
            double valorAsignacion = as.getValor().doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
//                        parametros.put("anio", anio);
            parametros.put("codigo", as);
            parametros.put(";where", "o.asignacion=:codigo "
                    + "");
//                    + "and o.cabecera.definitivo=true");
            double valorReformas = ejbReformas.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
//                        parametros.put("anio", anio);
            parametros.put("asignacion", as);
            parametros.put(";where", "o.asignacion=:asignacion"
                    + " and o.certificacion.impreso=true");
            double valorCertificaciones = ejbDetalles.sumarCampo(parametros).doubleValue();
            // ver si es original o cambio

            double valor = valorAsignacion + valorReformas - (valorCertificaciones + valorPedido);
            if (valor > 0) {
                return as;
            }
            // hay que ver donde se quedo
            if (original == 0) {
//                se quedo en el original

            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

//    private boolean esta(Detallecompromiso d1) {
//        for (Detallecompromiso d : detallees) {
//            if (d.getAsignacion().equals(d1.getAsignacion())) {
//                double valor = d.getValor().doubleValue() + d1.getValor().doubleValue();
//                d.setValor(new BigDecimal(valor));
//                return true;
//            }
//        }
//        detallees.add(d1);
//        return false;
//    }
    private boolean estaCompromiso(Detallecompromiso d1) {

        for (Detallecompromiso d : detallees) {
            if (d.getAsignacion().equals(d1.getAsignacion())) {
                double valor = d.getValor().doubleValue();
                double valor1 = d1.getValor().doubleValue();
                d.setValor(new BigDecimal(valor + valor1));
                return true;
            }
        }
        detallees.add(d1);
        return false;
    }

    public String borra(Compromisos c) {
        this.compromiso = c;
        Map parametros = new HashMap();
        parametros.put(";where", "o.certificacion=:certificacion");
        parametros.put("certificacion", certificacion);
        try {
            // cambiar a buscar directo el compromiso una sola
            detallesCertificaciones = ejbDetalles.encontarParametros(parametros);
            detallesCertb = new LinkedList<>();
            detallees = new LinkedList<>();
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:certificacion");
            parametros.put("certificacion", c);
            detallees = ejbDetallecompromiso.encontarParametros(parametros);
            // traer el compromiso uno solo por roles
            parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            pagosConceptos = ejbPagosEmpleados.encontarParametros(parametros);
            // quitar el compromiso de pagos empleados
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        getFormulario().eliminar();
        return null;
    }

    public String imprimir(Compromisos compromiso) {
        this.compromiso = compromiso;
        setImprimirNuevo(compromiso.getImpresion() == null);
        //*******************************************************************//
        compromiso.setImpresion(compromiso.getFecha());
//        compromiso.setImpresion(new Date());
        List<Auxiliar> titulos = proyectoBean.getTitulos();
        try {

            Map parametros = new HashMap();
            parametros.put(";where", "o.compromiso=:compromiso");
            parametros.put("compromiso", compromiso);
            detallees = ejbDetallecompromiso.encontarParametros(parametros);
            String direccion = "";
            for (Detallecompromiso d : detallees) {
                if (d.getCompromiso() != null) {
                    if (d.getCompromiso().getCertificacion() != null) {
                        if (d.getCompromiso().getCertificacion().getDireccion() != null) {
                            direccion = d.getCompromiso().getCertificacion().getDireccion().toString();
                        }
                    }

                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("EEEEEE, d MMMMMM  'del' yyyy");
            DocumentoPDF pdf = new DocumentoPDF(configuracionBean.getConfiguracion().getNombre() + "\n DIRECCION FINANCIERA - DEPARTAMENTO DE PRESUPUESTO",
                    null, seguridadbean.getLogueado().getUserid());
            pdf.agregaTitulo("INFORME DE COMPROMISO\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Compromiso :"));
//            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getId().toString()));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, compromiso.getNumerocomp() + ""));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Fecha : "));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, sdf1.format(compromiso.getFecha()).toUpperCase()));
            pdf.agregaParrafo("\nRevisado el Presupuesto del ejercicio económico del año " + anio
                    + ", EXISTE LA DISPONIBILIDAD PRESUPUESTARIA para acceder al gasto cuyo detalle es el siguiente:\n");
            pdf.agregarTabla(null, columnas, 2, 100, null);
            pdf.agregaTitulo("\n");
            String proveedor = "";
            String contrato = "";
            if (compromiso.getProveedor() != null) {
                proveedor = compromiso.getProveedor().getEmpresa().toString();
                if (compromiso.getContrato() != null) {
                    contrato = compromiso.getContrato().toString();
                }
            } else if (compromiso.getBeneficiario() != null) {
                proveedor = compromiso.getBeneficiario().getEntidad().toString();

            }
            List<AuxiliarReporte> titulosReporte = new LinkedList<>();
            int totalCol = 6;
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "CBTE. CTBL"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Partida"));
            titulosReporte.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, true, "Nombre"));
            titulosReporte.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, true, "Proyecto"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, true, "Fuente"));
            titulosReporte.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Valor"));
            columnas = new LinkedList<>();
            double total = 0;
            for (Detallecompromiso d : detallees) {
                String que = "";
                for (Auxiliar a : titulos) {
                    que += a.getTitulo1() + " : ";
                    que += proyectoBean.dividir(a, d.getAsignacion().getProyecto()) + " ";

                }
                que += " Programa : " + d.getAsignacion().getProyecto().toString();
                d.setArbolProyectos(que);
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, sdf.format(d.getFecha() == null ? compromiso.getFecha() : d.getFecha())));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getCodigo()));
                columnas.add(new AuxiliarReporte("String", 2, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getClasificador().getNombre()));
                columnas.add(new AuxiliarReporte("String", 4, AuxiliarReporte.ALIGN_LEFT, 6, false, que));
                columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, d.getAsignacion().getFuente().getNombre()));
                columnas.add(new AuxiliarReporte("Double", 1, AuxiliarReporte.ALIGN_RIGHT, 6, false, d.getValor().doubleValue()));
                total += d.getValor().doubleValue();
            }

            DecimalFormat df = new DecimalFormat("###,##0.00");
            pdf.agregarTablaReporte(titulosReporte, columnas, totalCol, 100, null);
            pdf.agregaParrafo("\nObservaciones : A FAVOR DE " + proveedor + " POR " + contrato + " " + compromiso.getMotivo());
            pdf.agregaParrafo("\n\n");
            pdf.agregaParrafo("\nEl Monto del compromiso asciende a: " + df.format(total) + "\n");
            pdf.agregaParrafo("\nTotal compromiso: " + ConvertirNumeroALetras.convertNumberToLetter(total) + "\n");
            pdf.agregaParrafo("\n\n");
//            FM14SEP2018
            if (compromiso.getCertificacion() != null) {
                if (compromiso.getCertificacion().getNumerocert() != null) {
                    pdf.agregaParrafo("\nCertificación Nro: " + compromiso.getCertificacion().getNumerocert() + " - " + compromiso.getCertificacion().getMotivo() + "\n");
                    pdf.agregaParrafo("\n\n");
                }
            }
//            FM14SEP2018
            pdf.agregaParrafo("\n\n");
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Analista de Presupuesto"));
            columnas.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 7, false, "Jefe de Presupuesto/Director Financiero"));

            pdf.agregarTabla(null, columnas, 2, 100, null);
            reporteCompromiso = pdf.traerRecurso();
            ejbCompromisos.edit(compromiso, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioImpresion.insertar();

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

    public String getValorDetalleCert() {
        if (detallesCertificaciones == null) {
            return "0.00";
        }
        double retorno = 0;
        for (Detallecertificaciones o : detallesCertificaciones) {
            retorno += o.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public double getValorTotalCompromiso() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", c);
        parametros.put(";campo", "o.valor");
        try {
            retorno = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public String getValorTotalCompromisoStr() {
        double retorno = 0;
        for (Detallecompromiso d : detallees) {
            retorno += d.getValor().doubleValue();
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        return df.format(retorno);
    }

    public double getSaldoTotalCompromiso() {
        Compromisos c = (Compromisos) compromisos.getRowData();
        double retorno = 0;
        Map parametros = new HashMap();
        parametros.put(";where", "o.compromiso=:compromiso");
        parametros.put("compromiso", c);
        parametros.put(";campo", "o.saldo");
        try {
            retorno = ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    public boolean validar() {

        if ((compromiso.getMotivo() == null) || (compromiso.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario motivo de compromiso");
            return true;
        }
        if (codigoNumero < 0) {
            MensajesErrores.advertencia("Es necesario Número");
            return true;
        }
        if ((detallees == null) || (detallees.isEmpty())) {
            MensajesErrores.advertencia("No existe nada que grabar");
            return true;
        }
        if (fechaCompromiso == null) {
            MensajesErrores.advertencia("Ingresar fecha del compromiso");
            return true;
        }
        if (fechaCompromiso.after(new Date())) {
            MensajesErrores.advertencia("Fecha de de Compromiso de ser menor a hoy");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, mes - 1);
            c.set(Calendar.YEAR, anio);
            c.set(Calendar.DATE, 1);
            Map parametros = new HashMap();
            parametros.put(";where", "o.numerocomp=:numerocomp and o.anio=:anio");
            parametros.put("numerocomp", compromiso.getNumerocomp());
            parametros.put("anio", anio);
            List<Compromisos> lista = ejbCompromisos.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                MensajesErrores.advertencia("Número de compromiso duplicada");
                return null;
            } else {
                compromiso.setNumerocomp(codigoNumero);
            }

            compromiso.setFecha(fechaCompromiso);
            compromiso.setFechareposicion(fechaCompromiso);
            compromiso.setImpresion(fechaCompromiso);
            compromiso.setNomina(true);
            compromiso.setAnio(anio);
            compromiso.setDevengado(3);
            if (compromisoConcepto) {
                compromiso.setUsado(true);
            }
            ejbCompromisos.create(compromiso, seguridadbean.getLogueado().getUserid());
            for (Detallecompromiso d : detallees) {
                d.setCompromiso(compromiso);
                d.setFecha(fechaCompromiso);
                ejbDetallecompromiso.create(d, seguridadbean.getLogueado().getUserid());
            }
            for (Pagosempleados p : pagosConceptos) {
                // ver que detalle corresponde para poner
                p.setCompromiso(compromiso);
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());
            }

            Codigos codi;
            if (anioCompromiso == anioCompromiso) {
                codi = ejbCodigos.traerCodigo("NUM", "09");
                codi.setParametros(compromiso.getNumerocomp() + "");
                ejbCodigos.edit(codi, seguridadbean.getLogueado().getUserid());
            } else {
                codi = ejbCodigos.traerCodigo("NUM", "05");
                codi.setParametros(compromiso.getNumerocomp() + "");
                ejbCodigos.edit(codi, seguridadbean.getLogueado().getUserid());
            }

        } catch (InsertarException | GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        compromisoConcepto = false;
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            // borrar compromisos de pagos empleado
            for (Pagosempleados p : pagosConceptos) {
                p.setPagado(false);
                p.setFuente(null);
                p.setCompromiso(null);
                ejbPagosEmpleados.edit(p, seguridadbean.getLogueado().getUserid());
            }
            for (Detallecompromiso d : detallees) {
                ejbDetallecompromiso.remove(d, seguridadbean.getLogueado().getUserid());
            }
            ejbCompromisos.remove(compromiso, seguridadbean.getLogueado().getUserid());
            // 
        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CompromisosRolBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
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
     * @return the compromisos
     */
    public LazyDataModel<Compromisos> getCompromisos() {
        return compromisos;
    }

    /**
     * @param compromisos the compromisos to set
     */
    public void setCompromisos(LazyDataModel<Compromisos> compromisos) {
        this.compromisos = compromisos;
    }

    /**
     * @return the compromiso
     */
    public Compromisos getCompromiso() {
        return compromiso;
    }

    /**
     * @param compromiso the compromiso to set
     */
    public void setCompromiso(Compromisos compromiso) {
        this.compromiso = compromiso;
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
     * @return the imprimirNuevo
     */
    public boolean isImprimirNuevo() {
        return imprimirNuevo;
    }

    /**
     * @param imprimirNuevo the imprimirNuevo to set
     */
    public void setImprimirNuevo(boolean imprimirNuevo) {
        this.imprimirNuevo = imprimirNuevo;
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

//   
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
     * @return the numeroCertificacion
     */
    public Integer getNumeroCertificacion() {
        return numeroCertificacion;
    }

    /**
     * @param numeroCertificacion the numeroCertificacion to set
     */
    public void setNumeroCertificacion(Integer numeroCertificacion) {
        this.numeroCertificacion = numeroCertificacion;
    }

    /**
     * @return the detallees
     */
    public List<Detallecompromiso> getDetalles() {
        return detallees;
    }

    /**
     * @param detallees the detallees to set
     */
    public void setDetalles(List<Detallecompromiso> detallees) {
        this.detallees = detallees;
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

    public SelectItem[] getComboPartidas() {

        if (certificacion == null) {
            return null;
        }
        Map parametros = new HashMap();
        try {
            parametros.put(";where", "o.certificacion=:certificacion and "
                    + " o.certificacion.anulado=false");
            parametros.put("certificacion", certificacion);
            return Combos.getSelectItems(ejbDetalles.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(CertificacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    /**
     * @return the muchos
     */
    public Boolean getMuchos() {
        return muchos;
    }

    /**
     * @param muchos the muchos to set
     */
    public void setMuchos(Boolean muchos) {
        this.muchos = muchos;
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
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the listaCertificaciones
     */
    public LazyDataModel<Compromisos> getListaCertificaciones() {
        return listaCertificaciones;
    }

    /**
     * @param listaCertificaciones the listaCertificaciones to set
     */
    public void setListaCertificaciones(LazyDataModel<Compromisos> listaCertificaciones) {
        this.listaCertificaciones = listaCertificaciones;
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

    public double getValorCertificacion() {
        Compromisos c = (Compromisos) listaCertificaciones.getRowData();
//        Certificaciones c = (Certificaciones) listaCertificaciones.getRowData();
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("compromiso", c);
        parametros.put(";where", "o.compromiso=:compromiso");
        try {
            return ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getSaldoCertificacion() {
        Certificaciones c = (Certificaciones) listaCertificaciones.getRowData();
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("certificacion", c);
        parametros.put(";where", "o.detallecertificacion.certificacion=:certificacion");
        try {
            return ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    /**
     * @return the detallesCertb
     */
    public List<Detallecertificaciones> getDetallesCertb() {
        return detallesCertb;
    }

    /**
     * @param detallesCertb the detallesCertb to set
     */
    public void setDetallesCertb(List<Detallecertificaciones> detallesCertb) {
        this.detallesCertb = detallesCertb;
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
     * @return the detalleCertificacion
     */
    public Detallecertificaciones getDetalleCertificacion() {
        return detalleCertificacion;
    }

    /**
     * @param detalleCertificacion the detalleCertificacion to set
     */
    public void setDetalleCertificacion(Detallecertificaciones detalleCertificacion) {
        this.detalleCertificacion = detalleCertificacion;
    }

    /**
     * @return the asignaconDetalle
     */
    public Asignaciones getAsignaconDetalle() {
        return asignaconDetalle;
    }

    /**
     * @param asignaconDetalle the asignaconDetalle to set
     */
    public void setAsignaconDetalle(Asignaciones asignaconDetalle) {
        this.asignaconDetalle = asignaconDetalle;
    }

    public double getValorReformas() {
        return calculaTotalReformas(asignaconDetalle);
    }

    public double calculaTotalReformas(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getValorCertificaciones() {
        return calculaTotalCertificaciones(asignaconDetalle);
    }

    private double calculaTotalCertificaciones(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true");
        try {
            return ejbDetalles.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public String modificaDetalle(Integer fila) {
//        int fila = formularioDetalle.getFila().getRowIndex();
        this.detalleCertificacion = detallesCertificaciones.get(fila);
        formularioDetalle.setIndiceFila(fila);
        formularioDetalle.editar();
        asignaconDetalle = detalleCertificacion.getAsignacion();
        clasificadorBean.setCodigo(asignaconDetalle.getClasificador().getCodigo());
        clasificadorBean.setClasificador(asignaconDetalle.getClasificador());
        asignacionesBean.setFuente(asignaconDetalle.getFuente());
//        codigo = asignacion.getClasificador().getCodigo();
        return null;
    }

    public String grabarDetalle() {
        if (asignaconDetalle == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }

        detalleCertificacion.setAsignacion(asignaconDetalle);
        if (detalleCertificacion.getValor() == null) {
            MensajesErrores.advertencia("Valor debe ser distinto a cero");
            return null;
        }
        if (detalleCertificacion.getValor().doubleValue() <= 0) {
            MensajesErrores.advertencia("Valor debe ser distinto a cero");
            return null;
        }
        // debe certificar la suma de lo asignado + lo reformado + certificaciones <= certificacion actual
        double asignado = asignaconDetalle.getValor().doubleValue() * 100;
        double reformas = calculaTotalReformas(asignaconDetalle) * 100;
        double tCertificaciones = calculaTotalCertificaciones(asignaconDetalle) * 100;
        double suma = asignado + reformas - tCertificaciones;
        double valor = detalleCertificacion.getValor().doubleValue() * 100;
        int i = 0;
        for (Detallecertificaciones d : detallesCertificaciones) {
            if (i <= formularioDetalle.getIndiceFila()) {
                if (d.getAsignacion().equals(asignaconDetalle)) {
                    valor += d.getValor().doubleValue();
                }
            }
        }
        double compara = Math.round(suma - valor);
        if (compara <= 0) {
            MensajesErrores.advertencia("Certificación exede el saldo de la partida");
            return null;
        }
        detallesCertificaciones.set(formularioDetalle.getIndiceFila(), detalleCertificacion);
        formularioDetalle.cancelar();
        return null;
    }

    private boolean estaAsignacion(Asignaciones a) {
        for (Detallecertificaciones d : detallesCertificaciones) {
            if (d.getAsignacion().equals(a)) {
                MensajesErrores.advertencia("Partida ya se encuentra en certificación");
                return true;
            }
        }
        return false;
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
     * @return the reporteCompromiso
     */
    public Resource getReporteCompromiso() {
        return reporteCompromiso;
    }

    /**
     * @param reporteCompromiso the reporteCompromiso to set
     */
    public void setReporteCompromiso(Resource reporteCompromiso) {
        this.reporteCompromiso = reporteCompromiso;
    }

    /**
     * @return the reporteCertificacion
     */
    public Resource getReporteCertificacion() {
        return reporteCertificacion;
    }

    /**
     * @param reporteCertificacion the reporteCertificacion to set
     */
    public void setReporteCertificacion(Resource reporteCertificacion) {
        this.reporteCertificacion = reporteCertificacion;
    }

    public String salir() {
//        MensajesErrores.informacion("Salir sin grabar");
        formulario.cancelar();
        return null;
    }

    /**
     * @return the formularioConcepto
     */
    public Formulario getFormularioConcepto() {
        return formularioConcepto;
    }

    /**
     * @param formularioConcepto the formularioConcepto to set
     */
    public void setFormularioConcepto(Formulario formularioConcepto) {
        this.formularioConcepto = formularioConcepto;
    }

    /**
     * @return the concepto
     */
    public Conceptos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptos concepto) {
        this.concepto = concepto;
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
     * @return the anioCompromiso
     */
    public int getAnioCompromiso() {
        return anioCompromiso;
    }

    /**
     * @param anioCompromiso the anioCompromiso to set
     */
    public void setAnioCompromiso(int anioCompromiso) {
        this.anioCompromiso = anioCompromiso;
    }

    /**
     * @return the fechaCompromiso
     */
    public Date getFechaCompromiso() {
        return fechaCompromiso;
    }

    /**
     * @param fechaCompromiso the fechaCompromiso to set
     */
    public void setFechaCompromiso(Date fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    /**
     * @return the compromisoConcepto
     */
    public boolean isCompromisoConcepto() {
        return compromisoConcepto;
    }

    /**
     * @param compromisoConcepto the compromisoConcepto to set
     */
    public void setCompromisoConcepto(boolean compromisoConcepto) {
        this.compromisoConcepto = compromisoConcepto;
    }
}
