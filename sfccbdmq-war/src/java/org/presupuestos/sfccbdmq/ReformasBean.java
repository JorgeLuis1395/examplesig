/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.PartidaspoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
import org.entidades.sfccbdmq.Partidaspoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Reformaspoa;
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
import org.auxiliares.sfccbdmq.AuxiliarCarga;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CabecerareformasFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.ProyectosFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Cabecerareformas;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Proyectos;
import org.entidades.sfccbdmq.Reformas;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "reformasSfccbdmq")
@ViewScoped
public class ReformasBean {

    /**
     * Creates a new instance of ReformasBean
     */
    public ReformasBean() {
    }
    private Asignaciones asignacion;
    private Reformas reforma;
    private List<Reformas> reformas;
    private List<Reformas> reformasb;
    private List<Cabecerareformas> cabecerasReformas;
    private Cabecerareformas cabeceraReforma = new Cabecerareformas();
    private Formulario formulario = new Formulario();
    private Formulario formularioCabecera = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private List errores;
    private List<AuxiliarCarga> totales;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private DetallecertificacionesFacade ejbCertificaciones;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;
    @EJB
    private CabecerareformasFacade ejbCabeceras;
    @EJB
    private ProyectosFacade ejbProyectos;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private CabecerareformaspoaFacade ejbCabecerareformaspoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{asignacionesSfccbdmq}")
    private AsignacionesBean asignacionesBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private int anio;
    private Integer id;
    private Perfiles perfil;
    private String separador = ",";
    private Date desde;
    private Date hasta;
    private boolean bloqueaFuente;

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
        desde = configuracionBean.getConfiguracion().getPinicialpresupuesto();
        hasta = configuracionBean.getConfiguracion().getPfinalpresupuesto();

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilStr = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReformasVista";
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
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
     * @return the reforma
     */
    public Reformas getReforma() {
        return reforma;
    }

    /**
     * @param reforma the reforma to set
     */
    public void setReforma(Reformas reforma) {
        this.reforma = reforma;
    }

    /**
     * @return the reformas
     */
    public List<Reformas> getReformas() {
        return reformas;
    }

    /**
     * @param reformas the reformas to set
     */
    public void setReformas(List<Reformas> reformas) {
        this.reformas = reformas;
    }

    public String buscar() {
        if (anio <= 0) {
            reformas = null;
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.anio=:anio and o.fecha between :desde and :hasta");
        parametros.put("anio", anio);
        parametros.put("desde", desde);
        parametros.put("hasta", hasta);
        parametros.put(";orden", "o.fecha desc");
        if (id != null) {
            parametros = new HashMap();
            parametros.put(";where", "o.id=:id");
            parametros.put("id", id);
        }
        try {
            cabecerasReformas = ejbCabeceras.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearCabecera() {
        cabeceraReforma = new Cabecerareformas();
        reformas = new LinkedList<>();
        reformasb = new LinkedList<>();
        errores = new LinkedList<>();
        formularioCabecera.insertar();
        bloqueaFuente = false;
        return null;
    }

    public String crear() {

        formularioClasificador.insertar();
//        Codigos f;
//        if (reforma!=null){
//            if (bloqueaFuente){
//                f=reforma.getAsignacion().getFuente();
//                asignacionesBean.setFuente(f);
//            }
//        }
        reforma = new Reformas();
        reforma.setAsignacion(asignacion);
        getFormulario().insertar();
        reforma.setFecha(configuracionBean.getConfiguracion().getPvigente());
        clasificadorBean.setClasificador(null);
        proyectosBean.setProyectoSeleccionado(null);
        clasificadorBean.setCodigo(null);
        proyectosBean.setCodigo(null);
        asignacionesBean.setFuente(null);
//        asignacionesBean.setPartida(null);

//        errores = new LinkedList<>();
        return null;
    }

    public String modificarCabecera(Cabecerareformas cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        calculaTotales();
        formularioCabecera.editar();
        errores = new LinkedList<>();
        return null;
    }

    public String imprimirCabecera(Cabecerareformas cabeceraReforma) {

        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
            Reformas rtotal = new Reformas();
            rtotal.setTotalAisgnacion(0);
            rtotal.setTotalReforma(0);
            rtotal.setTotalTotal(0);
            for (Reformas r : reformas) {
//                r.setTotalAisgnacion(r.getAsignacion().getValor().doubleValue() + calculaTotalReformas(r));
//                r.setTotalReforma(r.getValor().doubleValue());
//                r.setTotalTotal(r.getTotalAisgnacion() + r.getTotalReforma());
//                rtotal.setTotalAisgnacion(rtotal.getTotalAisgnacion() + r.getTotalAisgnacion());
//                rtotal.setTotalReforma(rtotal.getTotalReforma() + r.getTotalReforma());
//                rtotal.setTotalTotal(rtotal.getTotalTotal() + r.getTotalTotal());
                r.setTotalAisgnacion(r.getAsignacion().getValor().doubleValue() + calculaTotalReformas(r));
                r.setTotalReforma(r.getValor().doubleValue());
                r.setTotalTotal(r.getTotalAisgnacion() + r.getTotalReforma());
                rtotal.setTotalAisgnacion(rtotal.getTotalAisgnacion() + r.getTotalAisgnacion());
                rtotal.setTotalReforma(rtotal.getTotalReforma() + r.getTotalReforma());
                rtotal.setTotalTotal(rtotal.getTotalTotal() + r.getTotalTotal());
            }
            reformas.add(rtotal);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        calculaTotales();
        formularioImprimir.editar();
//        errores = new LinkedList<>();
        return null;
    }

    public String definitivaCabecera(Cabecerareformas cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas

        try {
            // Asignaciones + Reformas + Reforma Nueva < Certificaciones
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabeceraReforma);
            List<Reformas> rl = ejbReformas.encontarParametros(parametros);
            for (Reformas r : rl) {
                double asignaciones = r.getAsignacion().getValor().doubleValue();
                double reformasTotal = calculaTotalReformas(r);
                double certificacionesTotal = calculaTotalCertificaciones(r);
                double reformaactual = r.getValor().doubleValue();
                // Asignaciones + Reformas + Reforma Nueva > Certificaciones
                //                if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
//                    MensajesErrores.advertencia("No es posible colocar como definitiva reforma, no cuadra con certificaciones la cuenta- Proyecto:"
//                            + r.getAsignacion().getProyecto().getCodigo() + " " + r.getAsignacion().getProyecto().getNombre()
//                            + " Fuente : " + r.getAsignacion().getFuente().getNombre() + " cuenta : " + r.getAsignacion().getClasificador().toString());
//                    return null;
//                }
                double a = Math.round((asignaciones + reformasTotal + reformaactual) * 100);
                double b = Math.round(certificacionesTotal * 100);

                if (a < b) {
                    MensajesErrores.advertencia("No es posible colocar como definitiva reforma, no cuadra con certificaciones la cuenta- Proyecto:"
                            + r.getAsignacion().getProyecto().getCodigo() + " " + r.getAsignacion().getProyecto().getNombre());
                    return null;
                }
            }
            cabeceraReforma.setDefinitivo(Boolean.TRUE);
            if (cabeceraReforma.getPac() == null) {
                cabeceraReforma.setPac(Boolean.FALSE);
            }
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            //*************Grabar Reforma en el pacpoa*******************
            //*************Cabecera Reforma*******************
            if (!cabeceraReforma.getPac()) {
                Cabecerareformaspoa cabeceraReformaPoa = new Cabecerareformaspoa();
                cabeceraReformaPoa.setMotivo(cabeceraReforma.getMotivo());
                cabeceraReformaPoa.setFecha(cabeceraReforma.getFecha());
                cabeceraReformaPoa.setDefinitivo(Boolean.TRUE);
                cabeceraReformaPoa.setAnio(cabeceraReforma.getAnio());
                cabeceraReformaPoa.setTipo(cabeceraReforma.getTipo().getCodigo());
                cabeceraReformaPoa.setDocumento(cabeceraReforma.getMotivo());
                cabeceraReformaPoa.setObservacion(cabeceraReforma.getMotivo());
                cabeceraReformaPoa.setDirector(null);
                cabeceraReformaPoa.setUtilizado(true);
                ejbCabecerareformaspoa.create(cabeceraReformaPoa, seguridadbean.getLogueado().getUserid());
                //*************Fin Cabecera Reforma*******************
                //*************Detalle Reforma*******************
                for (Reformas r : rl) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.proyecto.codigo=:proyecto and o.fuente=:fuente and o.partida.codigo=:clasificador and o.proyecto.anio=:anio and o.cerrado is not null and o.activo=true");
                    parametros.put("proyecto", r.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", r.getAsignacion().getProyecto().getAnio());
                    parametros.put("fuente", r.getAsignacion().getFuente().getCodigo());
                    parametros.put("clasificador", r.getAsignacion().getClasificador().getCodigo());
                    List<Asignacionespoa> la = ejbAsignacionespoa.encontarParametros(parametros);
                    Asignacionespoa asig = null;
                    for (Asignacionespoa ast : la) {
                        asig = ast;
                    }
                    if (asig == null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.anio=:anio");
                        parametros.put("codigo", r.getAsignacion().getProyecto().getCodigo());
                        parametros.put("anio", anio);
                        List<Proyectospoa> listap = ejbProyectospoa.encontarParametros(parametros);
                        Proyectospoa p = null;
                        for (Proyectospoa p1 : listap) {
                            p = p1;
                        }
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo");
                        parametros.put("codigo", r.getAsignacion().getClasificador().getCodigo());
                        List<Partidaspoa> listac = ejbPartidaspoa.encontarParametros(parametros);
                        Partidaspoa c = null;
                        for (Partidaspoa c1 : listac) {
                            c = c1;
                        }
                        asig = new Asignacionespoa();
                        asig.setProyecto(p);
                        asig.setActivo(Boolean.TRUE);
                        asig.setPartida(c);
                        asig.setValor(BigDecimal.ZERO);
                        asig.setCerrado(Boolean.TRUE);
                        asig.setFuente(r.getAsignacion().getFuente().getCodigo());
                        ejbAsignacionespoa.create(asig, seguridadbean.getLogueado().getUserid());
                    } else {
                        Reformaspoa reformaPoa = new Reformaspoa();
                        reformaPoa.setFecha(r.getFecha());
                        reformaPoa.setValor(r.getValor());
                        reformaPoa.setCabecera(cabeceraReformaPoa);
                        reformaPoa.setAsignacion(asig);
                        ejbReformaspoa.create(reformaPoa, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
            //*************Fin Detalle Reforma*******************
            //*************Fin Grabar Reforma en el pacpoa*******************

        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InsertarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCabecera.cancelar();
        errores = new LinkedList<>();
        imprimirCabecera(cabeceraReforma);
        MensajesErrores.informacion("Reforma se grabó correctamente");
        return null;
    }

    public String modificar() {
        reforma = reformas.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioClasificador.cancelar();
        asignacion = reforma.getAsignacion();
        asignacionesBean.setFuente(reforma.getAsignacion().getFuente());
        clasificadorBean.setClasificador(asignacion.getClasificador());
        clasificadorBean.setCodigo(asignacion.getClasificador().getCodigo());
//        errores = new LinkedList<>();
        getFormulario().editar();
        return null;
    }

    public String eliminarCabecera(Cabecerareformas cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible borrar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCabecera.eliminar();
        return null;
    }

    public String eliminar() {
        reforma = reformas.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        asignacion = reforma.getAsignacion();
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

    public String insertarCabecera() {
        if (validar()) {
            return null;
        }
        if ((cabeceraReforma.getMotivo() == null) || (cabeceraReforma.getMotivo().isEmpty())) {
            MensajesErrores.advertencia("Ingrese una observación primero");
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            cabeceraReforma.setDefinitivo(Boolean.FALSE);
//            cabeceraReforma.setFecha(new Date());
            cabeceraReforma.setAnio(c.get(Calendar.YEAR));
            ejbCabeceras.create(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            for (Reformas r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    public String grabarCabecera() {
        if (validar()) {
            return null;
        }
        try {
            if ((cabeceraReforma.getMotivo() == null) || (cabeceraReforma.getMotivo().isEmpty())) {
                MensajesErrores.advertencia("Ingrese una observación primero");
                return null;
            }
//            Calendar c = Calendar.getInstance();
            cabeceraReforma.setDefinitivo(Boolean.FALSE);
//            cabeceraReforma.setFecha(new Date());
//            cabeceraReforma.setAnio(c.get(Calendar.YEAR));
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            for (Reformas r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                if (r.getId() == null) {
                    ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbReformas.edit(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (reformasb != null) {
                for (Reformas r : reformasb) {
                    if (r.getId() != null) {
                        ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }
        } catch (InsertarException | GrabarException | BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    public String borrarCabecera() {

        try {

            for (Reformas r : reformas) {
                if (r.getId() != null) {
                    ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (reformasb != null) {
                if (reformasb != null) {
                    for (Reformas r : reformasb) {
                        if (r.getId() != null) {
                            ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }
            ejbCabeceras.remove(cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    private boolean validar() {
        if (!errores.isEmpty()) {
            MensajesErrores.advertencia("Existen errores en imporatación de archivo. Corrijálos antes de continuar");
            return true;
        }
        if (!errores.isEmpty()) {
            MensajesErrores.advertencia("Existen errores en imporatación de archivo. Corrijálos antes de continuar");
            return true;
        }
        if (reformas.isEmpty()) {
            MensajesErrores.advertencia("No existen  datos a importar");
            return true;
        }

        if (cabeceraReforma.getTipo() == null) {
            MensajesErrores.advertencia("Seleccione un tipo de reforma");
            return true;
        }
        if (cabeceraReforma.getFecha() == null) {
            MensajesErrores.advertencia("Ingrese una fecha dde reforma");
            return true;
        }
        if (cabeceraReforma.getFecha().before(configuracionBean.getConfiguracion().getPvigente())) {
            MensajesErrores.advertencia("Fecha debe ser mayor o igual al periodo vigente");
            return true;
        }
        double cuadre = 0;
        double ingreso = 0;
        double egreso = 0;
        for (AuxiliarCarga a : totales) {
            //Cambiado xq querian cambiar de una fuente 998 a 002
            ingreso += a.getIngresos().doubleValue();
            egreso += a.getEgresos().doubleValue();
//            if (a.getIngresos().doubleValue() - a.getEgresos().doubleValue() != 0) {
//                MensajesErrores.advertencia("No está cuadrada la fuente de financiamiento [Ingresos - Egresos] : " + a.getFuente().getNombre());
//                return true;
//            }
            cuadre = a.getIngresos().doubleValue() + a.getEgresos().doubleValue();
        }
        double cuadre2 = Math.round(ingreso);
        double dividido2 = cuadre2 / 100;
        BigDecimal valortotal2 = new BigDecimal(dividido2).setScale(2, RoundingMode.HALF_UP);
        double ingreso1 = (valortotal2.doubleValue());

        double cuadre3 = Math.round(egreso);
        double dividido3 = cuadre3 / 100;
        BigDecimal valortotal3 = new BigDecimal(dividido3).setScale(2, RoundingMode.HALF_UP);
        double egreso2 = (valortotal3.doubleValue());

        if (ingreso1 - egreso2 != 0) {
            MensajesErrores.advertencia("No cuadrada los totales : ");
            return true;
        }
        return false;
    }

    public String insertar() {
        // buscar Aisgnacion dependiendo del clasificador
        if (clasificadorBean.getClasificador() == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }
        if (proyectosBean.getProyectoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un Programa");
            return null;
        }
        if (asignacionesBean.getFuente() == null) {
            MensajesErrores.advertencia("Seleccione una Fuente");
            return null;
        }
        // buscar partida en asignación
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
        parametros.put("clasificador", clasificadorBean.getClasificador());
        parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
        parametros.put("fuente", asignacionesBean.getFuente());
        asignacion = null;
        try {
            List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
            for (Asignaciones a : la) {
                asignacion = a;
            }
            if (asignacion == null) {
                asignacion = new Asignaciones();
                asignacion.setAnio(proyectosBean.getProyectoSeleccionado().getAnio());
                asignacion.setClasificador(clasificadorBean.getClasificador());
                asignacion.setFuente(asignacionesBean.getFuente());
                asignacion.setProyecto(proyectosBean.getProyectoSeleccionado());
                asignacion.setValor(BigDecimal.ZERO);
                asignacion.setCerrado(Boolean.TRUE);
                ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        reforma.setAsignacion(asignacion);

        if (reforma.getAsignacion() == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }

//        reforma.setFecha(new Date());
        if (reformas == null) {
            reformas = new LinkedList<>();
        }
        double asignaciones = reforma.getAsignacion().getValor().doubleValue();
        double reformasTotal = calculaTotalReformas(reforma);
        double certificacionesTotal = calculaTotalCertificaciones(reforma);
        double reformaactual = reforma.getValor().doubleValue();
        // Asignaciones + Reformas + Reforma Nueva > Certificaciones
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
            MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                    + df.format(asignaciones) + " Reformas Anteriores = "
                    + df.format(reformasTotal) + " Certificaciones : "
                    + df.format(certificacionesTotal));
            return null;
        }
        reformas.add(reforma);
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
        return null;
    }

    public String grabar() {
        reformas.set(formulario.getIndiceFila(), reforma);
        double asignaciones = reforma.getAsignacion().getValor().doubleValue();
        double reformasTotal = calculaTotalReformas(reforma);
        double certificacionesTotal = calculaTotalCertificaciones(reforma);
        double reformaactual = reforma.getValor().doubleValue();
        // Asignaciones + Reformas + Reforma Nueva > Certificaciones
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
            MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                    + df.format(asignaciones) + " Reformas Anteriores = " + df.format(reformasTotal) + " Certificaciones : " + df.format(certificacionesTotal));
            return null;
        }
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
        return null;
    }

    public String borrar() {
        if (reformasb == null) {
            reformasb = new LinkedList<>();
        }
        reformas.remove(formulario.getIndiceFila());
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
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
     * @return the cabecerasReformas
     */
    public List<Cabecerareformas> getCabecerasReformas() {
        return cabecerasReformas;
    }

    /**
     * @param cabecerasReformas the cabecerasReformas to set
     */
    public void setCabecerasReformas(List<Cabecerareformas> cabecerasReformas) {
        this.cabecerasReformas = cabecerasReformas;
    }

    /**
     * @return the cabeceraReforma
     */
    public Cabecerareformas getCabeceraReforma() {
        return cabeceraReforma;
    }

    /**
     * @param cabeceraReforma the cabeceraReforma to set
     */
    public void setCabeceraReforma(Cabecerareformas cabeceraReforma) {
        this.cabeceraReforma = cabeceraReforma;
    }

    /**
     * @return the formularioCabecera
     */
    public Formulario getFormularioCabecera() {
        return formularioCabecera;
    }

    /**
     * @param formularioCabecera the formularioCabecera to set
     */
    public void setFormularioCabecera(Formulario formularioCabecera) {
        this.formularioCabecera = formularioCabecera;
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

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();
        anio = cAnio.get(Calendar.YEAR);
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
                        reformasb = reformas;
                        reformas = new LinkedList<>();
                        totales = new LinkedList<>();
                        List<Codigos> fuentes = getCodigosBean().getFuentesFinanciamiento();
                        for (Codigos c : fuentes) {
                            AuxiliarCarga auxiliarCarga = new AuxiliarCarga();
                            auxiliarCarga.setFuente(c);
                            auxiliarCarga.setIngresos(new BigDecimal(0));
                            auxiliarCarga.setEgresos(new BigDecimal(0));
                            getTotales().add(auxiliarCarga);
                        }
                        double totalIngreso = 0;
                        double totalEgreso = 0;
                        setErrores(new LinkedList());
                        int registro = 0;
                        while ((sb = entrada.readLine()) != null) {
//                            Asignaciones a = new Asignaciones();
                            Reformas r = new Reformas();
                            Asignaciones a = new Asignaciones();
                            String[] aux = sb.split(getSeparador());// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(a, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            registro++;
                            // ver si esta ben el registro o es error 
                            if (a.getFuente() == null) {
                                getErrores().add("Fuente no válida en registro: " + String.valueOf(registro));
                            } else if (a.getClasificador() == null) {
                                getErrores().add("Clasificador no válido en registro: " + String.valueOf(registro));
                            } else {
                                Map parametros = new HashMap();
                                parametros.put(";where", "o.anio=:anio and o.codigo=:codigo");
                                parametros.put("anio", anio);
                                parametros.put("codigo", a.getProyectoStr());
                                List<Proyectos> lp = ejbProyectos.encontarParametros(parametros);
                                Proyectos pr = null;

                                //CONTROL PARA IDENTIFICAR PROYECTOS PADRES Y CREAR EN CASO DE QUE NO EXISTAN ES-2018-03-21
                                //VALIDACION PARA PROYECTOS QUE NO EXISTEN 
                                for (Proyectos p : lp) {
                                    pr = p;
                                }
                                if (pr == null) {
                                    getErrores().add("Proyecto no válido en registro: " + String.valueOf(registro));
                                } else {
                                    // buscar la asignación
                                    parametros = new HashMap();
                                    parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente and o.clasificador=:clasificador and o.proyecto.anio=:anio");
                                    parametros.put("proyecto", pr);
                                    parametros.put("anio", pr.getAnio());
                                    parametros.put("fuente", a.getFuente());
                                    parametros.put("clasificador", a.getClasificador());
                                    List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
                                    Asignaciones asig = null;
                                    for (Asignaciones ast : la) {
                                        asig = ast;
                                    }
                                    if (asig == null) {
                                        asig = new Asignaciones();
                                        asig.setCerrado(true);
                                        asig.setFuente(a.getFuente());
                                        asig.setProyecto(pr);
                                        asig.setClasificador(a.getClasificador());
                                        asig.setValor(BigDecimal.ZERO);
                                        ejbAsignaciones.create(asig, seguridadbean.getLogueado().getUserid());
//                                        getErrores().add("No existe partida : " + a.getClasificador().getCodigo());
                                    } else {
                                        // ver el total
                                        Reformas reformaSubir = new Reformas();
                                        reformaSubir.setAsignacion(asig);
                                        reformaSubir.setFecha(new Date());
                                        reformaSubir.setValor(a.getValor());
                                        reformas.add(reformaSubir);
                                        for (AuxiliarCarga auxCarga : getTotales()) {
                                            if (a.getFuente().equals(auxCarga.getFuente())) {
                                                if (a.getValor().doubleValue() > 0) {
                                                    auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + a.getValor().doubleValue()));
                                                    totalIngreso += a.getValor().doubleValue();
                                                } else {
                                                    auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + a.getValor().doubleValue() * -1));
                                                    totalEgreso += a.getValor().doubleValue() * -1;
                                                }
                                            } // fin if fuente
                                        } // fin for auxCarga
                                    }// fin else asig
                                } // fin if proyecto
                            }// fin else fuente

                        }//fin while
                        AuxiliarCarga aux = new AuxiliarCarga();
                        aux.setFuente(null);
                        aux.setIngresos(new BigDecimal(totalIngreso));
                        aux.setEgresos(new BigDecimal(totalEgreso));
                        getTotales().add(aux);
                        entrada.close();
                    } catch (UnsupportedEncodingException | FileNotFoundException | ConsultarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException | InsertarException ex) {
                        MensajesErrores.fatal(ex.getMessage());
                        Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
//

//               
                }
            } else {
                MensajesErrores.fatal("Archivo no puede ser cargado: "
                        + i.getStatus().getFacesMessage(
                                FacesContext.getCurrentInstance(),
                                fe, i).getSummary());
            }
        }

    }

    private void ubicar(Asignaciones am, String titulo, String valor) {

        if (titulo.contains("anio")) {
            am.setAnio(Integer.valueOf(valor));

        } else if (titulo.contains("clasificador")) {
            // buscar el clasificador
            am.setClasificador(clasificadorBean.traerCodigo(valor));
        } else if (titulo.contains("partida")) {
            // buscar el clasificador
            am.setClasificador(clasificadorBean.traerCodigo(valor));
        } else if (titulo.contains("proyecto")) {
            am.setProyectoStr(valor);
        } else if (titulo.contains("actividad")) {
            am.setProyectoStr(valor);
        } else if (titulo.contains("programa")) {
            am.setProyectoStr(valor);
        } else if (titulo.contains("fuente")) {
            am.setFuente(getCodigosBean().traerCodigo("FUENFIN", valor));
        } else if (titulo.contains("valor")) {
            am.setValor(new BigDecimal(valor));
        }

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
     * @return the errores
     */
    public List getErrores() {
        return errores;
    }

    /**
     * @param errores the errores to set
     */
    public void setErrores(List errores) {
        this.errores = errores;
    }

    /**
     * @return the totales
     */
    public List<AuxiliarCarga> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCarga> totales) {
        this.totales = totales;
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

    private void calculaTotales() {
        double totalIngreso = 0;
        double totalEgreso = 0;
        totales = new LinkedList<>();
        List<Codigos> fuentes = getCodigosBean().getFuentesFinanciamiento();
        for (Codigos c : fuentes) {
            AuxiliarCarga auxiliarCarga = new AuxiliarCarga();
            auxiliarCarga.setFuente(c);
            auxiliarCarga.setIngresos(new BigDecimal(0));
            auxiliarCarga.setEgresos(new BigDecimal(0));
            getTotales().add(auxiliarCarga);
        }

        for (Reformas r : reformas) {
            Asignaciones a = r.getAsignacion();
            for (AuxiliarCarga auxCarga : getTotales()) {
                if (auxCarga.getFuente().equals(r.getAsignacion().getFuente())) {
                    if (a.getClasificador().getIngreso()) {
                        if (r.getValor().doubleValue() > 0) {
                            // Es un egreso
                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
                            totalIngreso += r.getValor().doubleValue();
                        } else {
                            // es un ingreso
                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue() * -1));
                            totalEgreso += r.getValor().doubleValue() * -1;

                        }
                    } else { // se trata de un egreso
                        if (r.getValor().doubleValue() > 0) {
                            // es un egrso
                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
                            totalEgreso += r.getValor().doubleValue();
                        } else {
                            // es un ingreso
                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue() * -1));
                            totalIngreso += r.getValor().doubleValue() * -1;
                        }
                    } // fin if de ingreso y egreso
                } // fin if fuente
            } // fin for auxCarga
        }// fin for reforma
        AuxiliarCarga aux = new AuxiliarCarga();
        aux.setFuente(null);
        aux.setIngresos(new BigDecimal(totalIngreso));
        aux.setEgresos(new BigDecimal(totalEgreso));
        getTotales().add(aux);
        bloqueaFuente = !(totalIngreso == totalEgreso);
    }
//    private void calculaTotales() {
//        double totalIngreso = 0;
//        double totalEgreso = 0;
//        totales = new LinkedList<>();
//        List<Codigos> fuentes = getCodigosBean().getFuentesFinanciamiento();
//        for (Codigos c : fuentes) {
//            AuxiliarCarga auxiliarCarga = new AuxiliarCarga();
//            auxiliarCarga.setFuente(c);
//            auxiliarCarga.setIngresos(new BigDecimal(0));
//            auxiliarCarga.setEgresos(new BigDecimal(0));
//            getTotales().add(auxiliarCarga);
//        }
//
//        for (Reformas r : reformas) {
//            Asignaciones a = r.getAsignacion();
//            for (AuxiliarCarga auxCarga : getTotales()) {
//                if (auxCarga.getFuente().equals(r.getAsignacion().getFuente())) {
//                    if (r.getValor().doubleValue() > 0) {
//                        if (r.getAsignacion().getClasificador().getIngreso()) {
//                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
//                            totalIngreso += r.getValor().doubleValue();
//                        } else {
//                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
//                            totalEgreso += r.getValor().doubleValue();
//                        }
//
//                    } else {
//                        if (r.getAsignacion().getClasificador().getIngreso()) {
//                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
//                            totalEgreso += r.getValor().doubleValue();
//                        } else {
//                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
//                            totalIngreso += r.getValor().doubleValue();
//                        }
//                    }
//                } // fin if fuente
//            } // fin for auxCarga
//        }// fin for reforma
//        AuxiliarCarga aux = new AuxiliarCarga();
//        aux.setFuente(null);
//        aux.setIngresos(new BigDecimal(totalIngreso));
//        aux.setEgresos(new BigDecimal(totalEgreso));
//        getTotales().add(aux);
//    }

    public String cancelar() {
        formularioCabecera.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String salir() {
        formularioCabecera.cancelar();
        cabeceraReforma = new Cabecerareformas();
        cabecerasReformas = new LinkedList<>();
        return null;
    }

    public double getTotalReformas() {
        Reformas r = reformas.get(formulario.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
        return calculaTotalReformas(r);
    }

    public double getTotalReformasImp() {
        Reformas r = reformas.get(formularioImprimir.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
        return calculaTotalReformas(r);
    }

    public double getSaldoActual() {
        double asiganacionLocal = getValorAisgnacion();
        double reformaLocal = getTotalReformasAisgnacion();
        double certificacion = getValorCertificacion();
        double compromiso = getValorCompromiso();
        return asiganacionLocal + reformaLocal - (certificacion + compromiso);
    }

    public double getTotalReformasAisgnacion() {
//        if ((asignacion == null)) {
        if ((clasificadorBean.getClasificador() != null)) {
            // buscar asignacion

            if (proyectosBean.getProyectoSeleccionado() == null) {
                return 0;
            }
            if (asignacionesBean.getFuente() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("clasificador", clasificadorBean.getClasificador());
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
            parametros.put("fuente", asignacionesBean.getFuente());
            try {
                asignacion = null;
                List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignaciones a : la) {
                    asignacion = a;
                }
                if (asignacion == null) {
                    return 0;
                }
                return calculaTotalReformasAsignacion(asignacion);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getValorAisgnacion() {
//        if ((asignacion == null)) {
        if ((clasificadorBean.getClasificador() != null)) {
            // buscar asignacion

            if (proyectosBean.getProyectoSeleccionado() == null) {
                return 0;
            }
            if (asignacionesBean.getFuente() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("clasificador", clasificadorBean.getClasificador());
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
            parametros.put("fuente", asignacionesBean.getFuente());
            try {
                asignacion = null;
                List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignaciones a : la) {
                    asignacion = a;
                    return a.getValor().doubleValue();
                }
                if (asignacion == null) {
                    return 0;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            } else {
//                return 0;
//            }
//            } else {
//                return 0;
//            }
        }

        return 0;
    }

    public double getValorCertificacion() {
//        if ((asignacion == null)) {
        if ((clasificadorBean.getClasificador() != null)) {
            // buscar asignacion

            if (proyectosBean.getProyectoSeleccionado() == null) {
                return 0;
            }
            if (asignacionesBean.getFuente() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("clasificador", clasificadorBean.getClasificador());
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
            parametros.put("fuente", asignacionesBean.getFuente());
            try {
                asignacion = null;
                List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignaciones a : la) {
                    asignacion = a;
//                        return a.getValor().doubleValue();
                }
                if (asignacion == null) {
                    return 0;
                }
                // calcula el total de certificaion
                double retorno = calculaTotalCertificacionesAsignacion(asignacion);
                return retorno;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            } else {
//            } else {

        }
//        }
        return 0;
//        return calculaTotalCertificacionesAsignacion(asignacion);
    }

    public double getValorCompromiso() {
//        if ((asignacion == null)) {
        if ((clasificadorBean.getClasificador() != null)) {
            // buscar asignacion

            if (proyectosBean.getProyectoSeleccionado() == null) {
                return 0;
            }
            if (asignacionesBean.getFuente() == null) {
                return 0;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("clasificador", clasificadorBean.getClasificador());
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
            parametros.put("fuente", asignacionesBean.getFuente());
            try {
                asignacion = null;
                List<Asignaciones> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignaciones a : la) {
                    asignacion = a;
//                        return a.getValor().doubleValue();
                }
                if (asignacion == null) {
                    return 0;
                }
                // calcula el total de certificaion
                double retorno = calculaTotalsAsignacionCompromiso(asignacion);
                return retorno;
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            } else {
//            } else {

        }
//        }
        return 0;
//        return calculaTotalCertificacionesAsignacion(asignacion);
    }

    private double calculaTotalCertificacionesAsignacion(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true");
        try {
            return ejbCertificaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double calculaTotalsAsignacionCompromiso(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion is null");
        try {
            return ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double calculaTotalReformas(Reformas r) {
        Map parametros = new HashMap();
        if (r.getId() != null) {
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true and o.id!=:id");
            parametros.put("asignacion", r.getAsignacion());
            parametros.put("id", r.getId());
        } else {
            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
            parametros.put("asignacion", r.getAsignacion());
        }
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double calculaTotalReformasAsignacion(Asignaciones a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformas r = reformas.get(formulario.getFila().getRowIndex());

        return calculaTotalCertificaciones(r);
    }

    public double getTotalCompromiso() {
        Reformas r = reformas.get(formulario.getFila().getRowIndex());

        return calculaTotalCompromiso(r);
    }

    public double getTotalCertificacionesImp() {
        Reformas r = reformas.get(formularioImprimir.getFila().getRowIndex());

        return calculaTotalCertificaciones(r);
    }

    private double calculaTotalCertificaciones(Reformas r) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", r.getAsignacion());
        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.anulado=false and o.certificacion.impreso=true");
        try {
            return ejbCertificaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private double calculaTotalCompromiso(Reformas r) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", r.getAsignacion());
        parametros.put(";where", "o.asignacion=:asignacion and o.compromiso.certificacion is null");
        try {
            return ejbDetallecompromiso.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            Logger.getLogger(ReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
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
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @return the bloqueaFuente
     */
    public boolean isBloqueaFuente() {
        return bloqueaFuente;
    }

    /**
     * @param bloqueaFuente the bloqueaFuente to set
     */
    public void setBloqueaFuente(boolean bloqueaFuente) {
        this.bloqueaFuente = bloqueaFuente;
    }

}
