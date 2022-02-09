/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.personal.sfccbdmq;

import org.pacpoa.sfccbdmq.*;
import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.CabecerareformaspoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Cabecerareformaspoa;
import org.entidades.sfccbdmq.Reformaspoa;
import org.entidades.sfccbdmq.Trackingspoa;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.errores.sfccbdmq.BorrarException;
import com.lowagie.text.DocumentException;
import java.io.IOException;
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
import org.auxiliares.sfccbdmq.Auxiliar;
import org.auxiliares.sfccbdmq.AuxiliarCargaPoa;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesPoaBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.utilitarios.sfccbdmq.Recurso;
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Empleados;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "gestionarReformasPoa")
@ViewScoped
public class GestionReformasPoaBean {

    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{asignacionesPoa}")
    private AsignacionesPoaBean asignacionesPoaBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{imagenesPoa}")
    private ImagenesPoaBean imagenesPoaBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoa;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;

    public GestionReformasPoaBean() {
    }

    private Asignacionespoa asignacion;
    private Reformaspoa reforma;
    private List<Reformaspoa> reformas;
    private List<Reformaspoa> reformasb;
    private List<Cabecerareformaspoa> cabecerasReformaspoa;
    private Cabecerareformaspoa cabeceraReforma = new Cabecerareformaspoa();
    private Formulario formulario = new Formulario();
    private Formulario formularioCabecera = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private List errores;
    private List<AuxiliarCargaPoa> totales = new LinkedList<>();

    private Recurso reportepdf;
    private DocumentoPDF pdf;

    private Date vigente;

    @EJB
    private AsignacionespoaFacade ejbAsignaciones;
    @EJB
    private ReformaspoaFacade ejbReformas;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificaciones;
    @EJB
    private CabecerareformaspoaFacade ejbCabeceras;
    @EJB
    private TrackingspoaFacade ejbTrackings;
    @EJB
    private ProyectospoaFacade ejbProyectos;
    @EJB
    private CabeceraempleadosFacade ejbCabemp;

    private int anio;
    private Integer id;
    private Date desde;
    private Date hasta;
    private boolean bloqueaFuente;
    private String direccion;
    private Empleados empleado;

    @PostConstruct
    private void activar() {
        vigente = getConfiguracionBean().getConfiguracion().getPvigente();
        desde = getConfiguracionBean().getConfiguracion().getPinicial();
        hasta = getConfiguracionBean().getConfiguracion().getPfinal();
        Calendar ca = Calendar.getInstance();
        ca.setTime(vigente);
        anio = ca.get(Calendar.YEAR);

        if (seguridadbean.traerDireccionEmpleado() != null) {
            if (seguridadbean.traerDireccionEmpleado().getCodigo() != null) {
                direccion = seguridadbean.traerDireccionEmpleado().getCodigo();
            } else {
                direccion = null;
            }
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
        if (anio <= 0) {
            reformas = null;
            MensajesErrores.advertencia("Proporcione un año de ejercicio");
            return null;
        }
        Map parametros = new HashMap();
        String where = "o.anio=:anio and o.fecha between :desde and :hasta and o.definitivo=false";
        parametros.put(";where", where);
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
            cabecerasReformaspoa = ejbCabeceras.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crearCabecera() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.definitivo=false");
        try {

            if (ejbCabeceras.contar(parametros) > 0) {
                MensajesErrores.advertencia("Solo debe existir una reforma sin definitivo");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        cabeceraReforma = new Cabecerareformaspoa();
        cabeceraReforma.setFecha(new Date());
        cabeceraReforma.setDefinitivo(Boolean.FALSE);
        reformas = new LinkedList<>();
        reformasb = new LinkedList<>();
        errores = new LinkedList<>();
        formularioCabecera.insertar();
        imagenesPoaBean.setListaDocumentos(new LinkedList<>());
        bloqueaFuente = false;
        return null;
    }

    public String crear() {

        formularioClasificador.insertar();
        reforma = new Reformaspoa();
        reforma.setAsignacion(asignacion);
        formulario.insertar();
        reforma.setFecha(vigente);
        partidasPoaBean.setPartidaPoa(null);
        proyectosPoaBean.setProyectoSeleccionado(null);
        partidasPoaBean.setCodigo(null);
        proyectosPoaBean.setCodigo(null);
        totales = new LinkedList<>();
        return null;
    }

    public String modificarCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (!isPuedePedir()) {
            MensajesErrores.advertencia("No tiene permiso para solcitar una reforma");
            return null;
        }
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera and upper(o.asignacion.proyecto.direccion)=:direccion");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put("direccion", direccion);

        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        imagenesPoaBean.setCabeceraReforma(this.cabeceraReforma);
        imagenesPoaBean.buscarDocumentos();
        calculaTotales();
        formularioCabecera.editar();
        errores = new LinkedList<>();
        return null;
    }

    public String imprimirCabecera(Cabecerareformaspoa cabeceraReforma) {

        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
            Reformaspoa rtotal = new Reformaspoa();
            rtotal.setTotalSubactividad(0);
            rtotal.setTotalReforma(0);
            rtotal.setTotalTotal(0);
            for (Reformaspoa r : reformas) {
                r.setTotalSubactividad(r.getAsignacion().getValor().doubleValue() + calculaTotalReformaspoa(r));
                r.setTotalReforma(r.getValor().doubleValue());
                r.setTotalTotal(r.getTotalSubactividad() + r.getTotalReforma());
                rtotal.setTotalSubactividad(rtotal.getTotalSubactividad() + r.getTotalSubactividad());
                rtotal.setTotalReforma(rtotal.getTotalReforma() + r.getTotalReforma());
                rtotal.setTotalTotal(rtotal.getTotalTotal() + r.getTotalTotal());
            }
            reformas.add(rtotal);
            calculaTotales();

            pdf = new DocumentoPDF(""
                    + "Reformaspoa Presupuesto año "
                    + anio, seguridadbean.getLogueado().getUserid());

            pdf.agregaParrafo("\nNúmero de control: " + cabeceraReforma.getId(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Tipo: " + consultasPoa.traerTipoReforma(cabeceraReforma.getTipo()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            pdf.agregaParrafo("Fecha: " + sdf.format(cabeceraReforma.getFecha()), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("Concepto: " + cabeceraReforma.getMotivo(), AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);

            List<AuxiliarReporte> valores = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "Ingresos"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "Egresos"));
//            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "Total"));
//
//            for (AuxiliarCarga auxCarga : totales) {
//                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, auxCarga.getIngresos().doubleValue()));
//                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, auxCarga.getEgresos().doubleValue()));
//                valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, auxCarga.getIngresos().doubleValue() - auxCarga.getEgresos().doubleValue()));
//            }
//            pdf.agregarTablaReporte(titulos, valores, 3, 100, "TOTALES");
//
//            pdf.agregaParrafo("\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
//
//            valores = new LinkedList<>();
//            titulos = new LinkedList<>();

            List<Auxiliar> lista = proyectosPoaBean.getTitulos();
            for (Auxiliar a : lista) {
                titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, a.getTitulo1()));
            }

            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "PRODUCTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "PARTIDA"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, true, "CÓDIGO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "CODIFICADO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "INCREMENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "DECREMENTO"));
            titulos.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "TOTAL"));

            for (Reformaspoa r : reformas) {
                if (r.getId() != null) {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, proyectosPoaBean.dividir1(a, r.getAsignacion().getProyecto())));
                    }

                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAsignacion().getProyecto().getNombre()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAsignacion().getPartida().getCodigo()));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, r.getAsignacion().getProyecto().getCodigo()));

                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalSubactividad()));

                    if (r.getTotalReforma() > 0) {
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalReforma()));
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                    } else {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, ""));
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalReforma() * -1));
                    }

                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, false, r.getTotalTotal()));
                } else {
                    for (Auxiliar a : lista) {
                        valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    }
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));

                    valores.add(new AuxiliarReporte("String", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, "TOTALES"));
                    for (AuxiliarCargaPoa ac : totales) {
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, ac.getIngresos().doubleValue()));
                        valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, ac.getEgresos().doubleValue()));
                    }
                    valores.add(new AuxiliarReporte("Double", 8, AuxiliarReporte.ALIGN_RIGHT, 6, true, r.getTotalTotal()));
                }
            }
            pdf.agregarTablaReporte(titulos, valores, titulos.size(), 100, "");
            pdf.agregaParrafo("\n\n", AuxiliarReporte.ALIGN_LEFT, 11, false);
            pdf.agregaParrafo("f.____________________________", AuxiliarReporte.ALIGN_CENTER, 11, false);
            pdf.agregaParrafo("Responsable", AuxiliarReporte.ALIGN_CENTER, 11, false);

            reportepdf = pdf.traerRecurso();

        } catch (ConsultarException | IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.editar();
        return null;
    }

    public String definitivaCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible modificar una reforma que ya es definitiva");
            return null;
        }

        if (partidasPoaBean.getPartidaPoa() == null) {
            MensajesErrores.advertencia("Debe crear al menos una reforma");
            return null;
        }

        this.cabeceraReforma = cabeceraReforma;
        // buscar reformas

        try {
            // Asignaciones + Reformaspoa + Reforma Nueva < Certificaciones
            Map parametros = new HashMap();
            parametros.put(";where", "o.cabecera=:cabecera");
            parametros.put("cabecera", cabeceraReforma);
            List<Reformaspoa> rl = ejbReformas.encontarParametros(parametros);
            for (Reformaspoa r : rl) {
                double asignaciones = r.getAsignacion().getValor().doubleValue();
                double reformasTotal = calculaTotalReformaspoa(r);
                double certificacionesTotal = calculaTotalCertificaciones(r);
                double reformaactual = r.getValor().doubleValue();
                // Asignaciones + Reformaspoa + Reforma Nueva > Certificaciones
                double a = Math.round((asignaciones + reformasTotal + reformaactual) * 100);
                double b = Math.round(certificacionesTotal * 100);

                if (Math.round((asignaciones + reformasTotal + reformaactual) * 100) < Math.round(certificacionesTotal * 100)) {
                    MensajesErrores.advertencia("No es posible colocar como definitiva reforma, no cuadra con certificaciones la cuenta- Proyecto:"
                            + r.getAsignacion().getProyecto().getCodigo() + " " + r.getAsignacion().getProyecto().getNombre());
                    return null;
                }
            }
            cabeceraReforma.setDefinitivo(Boolean.TRUE);
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma definitiva: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCabecera.cancelar();
        errores = new LinkedList<>();
        imprimirCabecera(cabeceraReforma);
        MensajesErrores.informacion("Reforma se grabó correctamente");
        return null;
    }

    public String modificar() {
        reforma = reformas.get(formulario.getFila().getRowIndex());
//        direccion = reforma.getAsignacion().getProyecto().getDireccion();
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioClasificador.cancelar();
        asignacion = reforma.getAsignacion();
        partidasPoaBean.setPartidaPoa(asignacion.getPartida());
        partidasPoaBean.setCodigo(asignacion.getPartida().getCodigo());
        formulario.editar();
        return null;
    }

    public String eliminarCabecera(Cabecerareformaspoa cabeceraReforma) {
        if (cabeceraReforma.getDefinitivo()) {
            MensajesErrores.advertencia("No es posible borrar una reforma que ya es definitiva");
            return null;
        }
        this.cabeceraReforma = cabeceraReforma;
        imagenesPoaBean.setCabeceraReforma(cabeceraReforma);
        imagenesPoaBean.buscarDocumentos();
        // buscar reformas
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera=:cabecera");
        parametros.put("cabecera", cabeceraReforma);
        parametros.put(";orden", "o.id");
        try {
            reformas = ejbReformas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioCabecera.eliminar();
        return null;
    }

    public String eliminar() {
        reforma = reformas.get(formulario.getFila().getRowIndex());
//        direccion = reforma.getAsignacion().getProyecto().getDireccion();
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        asignacion = reforma.getAsignacion();
        formulario.eliminar();
        return null;
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
//            cabeceraReforma.setDireccion(direccion);
//            cabeceraReforma.setFecha(new Date());
            cabeceraReforma.setAnio(c.get(Calendar.YEAR));
            cabeceraReforma.setTipo("TRASP");
            ejbCabeceras.create(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            for (Reformaspoa r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
            }
            imagenesPoaBean.setCabeceraReforma(cabeceraReforma);
            imagenesPoaBean.insertarDocumentos("reformas");

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma creada: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | IOException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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

//            cabeceraReforma.setDireccion(direccion);
            ejbCabeceras.edit(cabeceraReforma, seguridadbean.getLogueado().getUserid());
            imagenesPoaBean.insertarDocumentos("reformas");
            for (Reformaspoa r : reformas) {
                r.setCabecera(cabeceraReforma);
                r.setFecha(cabeceraReforma.getFecha());
                if (r.getId() == null) {
                    ejbReformas.create(r, seguridadbean.getLogueado().getUserid());
                } else {
                    ejbReformas.edit(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (reformasb != null) {
                for (Reformaspoa r : reformasb) {
                    if (r.getId() != null) {
                        ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                    }
                }
            }

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setReforma(cabeceraReforma);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Reforma modificada: \n"
                    + "Motivo :" + cabeceraReforma.getMotivo());
            ejbTrackings.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | GrabarException | BorrarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    public String borrarCabecera() {

        try {

            for (Reformaspoa r : reformas) {
                if (r.getId() != null) {
                    ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                }
            }
            if (reformasb != null) {
                if (reformasb != null) {
                    for (Reformaspoa r : reformasb) {
                        if (r.getId() != null) {
                            ejbReformas.remove(r, seguridadbean.getLogueado().getUserid());
                        }
                    }
                }
            }
            ejbCabeceras.remove(cabeceraReforma, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formularioCabecera.cancelar();
        return null;
    }

    private boolean validar() {

        double cuadre = 0;
        for (AuxiliarCargaPoa a : totales) {
            double i = a.getIngresos().doubleValue() * 100;
            double e = a.getEgresos().doubleValue() * 100;
            if (Math.round(i - e) != 0) {
                MensajesErrores.advertencia("No está cuadrada la fuente de financiamiento [Ingresos - Egresos]: " + a.getFuente());
                return true;
            }
            cuadre = a.getIngresos().doubleValue() + a.getEgresos().doubleValue();
        }

//        if (reformas.isEmpty()) {
//            MensajesErrores.advertencia("No existen datos a importar");
//            return true;
//        }
//        if (cabeceraReforma.getTipo() == null) {
//            MensajesErrores.advertencia("Seleccione un tipo de reforma");
//            return true;
//        }
        if (cabeceraReforma.getFecha() == null) {
            MensajesErrores.advertencia("Ingrese fecha de reforma");
            return true;
        }
        if (cabeceraReforma.getFecha().before(vigente)) {
            MensajesErrores.advertencia("Fecha debe ser mayor o igual al periodo vigente");
            return true;
        }

        return false;
    }

    public String insertar() {
//
//        if (direccion == null || direccion.isEmpty()) {
//            MensajesErrores.advertencia("Seleccione una dirección");
//            return null;
//        }
        // buscar Aisgnacion dependiendo del clasificador
        if (partidasPoaBean.getPartidaPoa() == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }
        if (proyectosPoaBean.getProyectoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un Programa");
            return null;
        }
        if (proyectosPoaBean.getProyectoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un Programa");
            return null;
        }

        if (reforma.getValor() == null) {
            reforma.setValor(BigDecimal.ZERO);
        }
        if (reforma.getValor() == BigDecimal.ZERO) {
            MensajesErrores.advertencia("Debe ingresar un valor y debe ser diferente de cero");
            return null;
        }
        if (!proyectosPoaBean.getProyectoSeleccionado().getIngreso().equals(partidasPoaBean.getPartidaPoa().getIngreso())) {
            MensajesErrores.advertencia("La partida y el proyecto deben ser o sólo de ingresos o sólo de gastos");
            return null;
        }
        // buscar partida en asignación
        Map parametros = new HashMap();
        parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto");
        parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
        parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
        asignacion = null;
        try {
            List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
            for (Asignacionespoa a : la) {
                asignacion = a;
            }
            if (asignacion == null) {
                asignacion = new Asignacionespoa();
//                asignacion.setDireccion(direccion);
                asignacion.setFuente(asignacionesPoaBean.getFuente());
                asignacion.setActivo(true);
                asignacion.setPartida(partidasPoaBean.getPartidaPoa());
                asignacion.setProyecto(proyectosPoaBean.getProyectoSeleccionado());
                asignacion.setValor(BigDecimal.ZERO);
                asignacion.setCerrado(Boolean.TRUE);
                ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());
            }
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
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
        double reformasTotal = calculaTotalReformaspoa(reforma);
        double certificacionesTotal = calculaTotalCertificaciones(reforma);
        double reformaactual = reforma.getValor().doubleValue();
        // Asignaciones + Reformaspoa + Reforma Nueva > Certificaciones
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
            MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                    + df.format(asignaciones) + " Reformaspoa Anteriores = "
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
        if (reforma.getValor() == BigDecimal.ZERO) {
            MensajesErrores.advertencia("Debe ingresar un valor y debe ser diferente de cero");
            return null;
        }
        reformas.set(formulario.getIndiceFila(), reforma);
        double asignaciones = reforma.getAsignacion().getValor().doubleValue();
        double reformasTotal = calculaTotalReformaspoa(reforma);
        double certificacionesTotal = calculaTotalCertificaciones(reforma);
        double reformaactual = reforma.getValor().doubleValue();
        // Asignaciones + Reformaspoa + Reforma Nueva > Certificaciones
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        if (asignaciones + reformasTotal + reformaactual < certificacionesTotal) {
            MensajesErrores.advertencia("Total sobrepasa la cantidad disponible en partida : Asignaciones ="
                    + df.format(asignaciones) + " Reformaspoa Anteriores = " + df.format(reformasTotal) + " Certificaciones : " + df.format(certificacionesTotal));
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
        reformasb.add(reforma);
        formulario.cancelar();
        formularioClasificador.cancelar();
        calculaTotales();
        return null;
    }

//    private void calculaTotales() {
//        double totalIngreso = 0;
//        double totalEgreso = 0;
//        totales = new LinkedList<>();
//
//        AuxiliarCarga auxiliarCarga = new AuxiliarCarga();
//        auxiliarCarga.setFuente(null);
//        auxiliarCarga.setIngresos(new BigDecimal(0));
//        auxiliarCarga.setEgresos(new BigDecimal(0));
//        totales.add(auxiliarCarga);
//
//        for (Reformaspoa r : reformas) {
//            Asignaciones a = r.getAsignacion();
//            if (a != null) {
//
//                for (AuxiliarCarga auxCarga : totales) {
//
//                    if (a.getPartidaPoa().getIngreso()) {
//                        if (r.getValor().doubleValue() > 0) {
//                            // Es un egreso
//                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue()));
//                            totalIngreso += r.getValor().doubleValue();
//                        } else {
//                            // es un ingreso
//                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue() * -1));
//                            totalEgreso += r.getValor().doubleValue() * -1;
//                        }
//                    } else { // se trata de un egreso
//                        if (r.getValor().doubleValue() > 0) {
//                            // es un egreso
//                            auxCarga.setEgresos(new BigDecimal(auxCarga.getEgresos().doubleValue() + r.getValor().doubleValue()));
//                            totalEgreso += r.getValor().doubleValue();
//                        } else {
//                            // es un ingreso
//                            auxCarga.setIngresos(new BigDecimal(auxCarga.getIngresos().doubleValue() + r.getValor().doubleValue() * -1));
//                            totalIngreso += r.getValor().doubleValue() * -1;
//                        }
//                    } // fin if de ingreso y egreso
//                } // fin for auxCarga
//            }
//        }// fin for reforma
//
//        bloqueaFuente = !(totalIngreso == totalEgreso);
//    }
    private void calculaTotales() {
        double totalIngreso = 0;
        double totalEgreso = 0;
        totales = new LinkedList<>();
        List<Codigos> fuentes = codigosBean.traerCodigoMaestro("FUENFIN");
        for (Codigos c : fuentes) {
            AuxiliarCargaPoa auxiliarCarga = new AuxiliarCargaPoa();
            auxiliarCarga.setFuente(c);
            auxiliarCarga.setIngresos(new BigDecimal(0));
            auxiliarCarga.setEgresos(new BigDecimal(0));
            totales.add(auxiliarCarga);
        }

        for (Reformaspoa r : reformas) {
            Asignacionespoa a = r.getAsignacion();
            for (AuxiliarCargaPoa auxCarga : totales) {
                if (r.getAsignacion() != null) {
                    if (auxCarga.getFuente().getCodigo().equals(r.getAsignacion().getFuente())) {
                        if (a.getPartida().getIngreso()) {
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
                }
            } // fin for auxCarga
        }// fin for reforma
        AuxiliarCargaPoa aux = new AuxiliarCargaPoa();
        aux.setFuente(null);
        aux.setIngresos(new BigDecimal(totalIngreso));
        aux.setEgresos(new BigDecimal(totalEgreso));
        getTotales().add(aux);
        bloqueaFuente = !(totalIngreso == totalEgreso);
    }

    public String cancelar() {
        formularioCabecera.cancelar();
        return "PresupuestoVista.jsf?faces-redirect=true";
    }

    public String salir() {
        formularioCabecera.cancelar();
        cabeceraReforma = new Cabecerareformaspoa();
        cabecerasReformaspoa = new LinkedList<>();
        return null;
    }

    public double getTotalReformaspoa() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
        return calculaTotalReformaspoa(r);
    }

    public double getTotalReformaspoaImp() {
        Reformaspoa r = reformas.get(formularioImprimir.getFila().getRowIndex());
        // todas las reformas anteriores de esta partida
        // sumar de todo el anio
        return calculaTotalReformaspoa(r);
    }

    public double getSaldoActual() {
        double asiganacionLocal = getValorAsignaciones();
        double reformaLocal = getTotalReformaspoaAisgnacion();
        double certificacion = getValorCertificacion();
        return asiganacionLocal + reformaLocal - certificacion;
    }

    public double getTotalReformaspoaAisgnacion() {
//        if ((asignacion == null)) {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            // buscar asignacion

            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.activo=true");
            parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
            try {
                asignacion = null;
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
                }
                if (asignacion == null) {
                    return 0;
                }
                return calculaTotalReformaspoaAsignacion(asignacion);
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    public double getValorAsignaciones() {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.proyecto=:actividad and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("partida", partidasPoaBean.getPartidaPoa());
            parametros.put("direccion", direccion);
            parametros.put("actividad", proyectosPoaBean.getProyectoSeleccionado());
            try {
                asignacion = null;
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
                    asignacion = a;
                    return a.getValor().doubleValue();
                }
                if (asignacion == null) {
                    return 0;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return 0;
    }

    public double getValorCertificacion() {
//        if ((asignacion == null)) {
        if ((partidasPoaBean.getPartidaPoa() != null)) {
            // buscar asignacion

            if (proyectosPoaBean.getProyectoSeleccionado() == null) {
                return 0;
            }

            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:clasificador and o.proyecto=:proyecto and o.proyecto.direccion=:direccion and o.activo=true");
            parametros.put("clasificador", partidasPoaBean.getPartidaPoa());
            parametros.put("direccion", direccion);
            parametros.put("proyecto", proyectosPoaBean.getProyectoSeleccionado());
            try {
                asignacion = null;
                List<Asignacionespoa> la = ejbAsignaciones.encontarParametros(parametros);
                for (Asignacionespoa a : la) {
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
                Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
//            } else {
//            } else {
//            } else {
//            } else {
//            } else {
//            } else {
//            } else {
//            } else {

        }
//        }
        return 0;
//        return calculaTotalCertificacionesAsignacion(asignacion);
    }

    private double calculaTotalCertificacionesAsignacion(Asignacionespoa a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false");
        try {
            return ejbCertificaciones.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double calculaTotalReformaspoa(Reformaspoa r) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", r.getAsignacion());
        parametros.put("id", r.getId());
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true and o.id!=:id");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double calculaTotalReformaspoaAsignacion(Asignacionespoa a) {
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", a);
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getTotalCertificaciones() {
        Reformaspoa r = reformas.get(formulario.getFila().getRowIndex());

        return calculaTotalCertificaciones(r);
    }

    public double getTotalCertificacionesImp() {
        Reformaspoa r = reformas.get(formularioImprimir.getFila().getRowIndex());

        return calculaTotalCertificaciones(r);
    }

    private double calculaTotalCertificaciones(Reformaspoa r) {
        try {
            Map parametros = new HashMap();
            parametros.put(";campo", "o.adjudicado");
            parametros.put("asignacion", r.getAsignacion());
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.anulado=false and o.certificacion.impreso=true and o.adjudicado is not null");

            double totalAdjudicado = ejbCertificaciones.sumarCampo(parametros).doubleValue();

            parametros = new HashMap();
            parametros.put(";campo", "o.valor");
            parametros.put("asignacion", r.getAsignacion());
            parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.anulado=false and o.certificacion.impreso=true and o.adjudicado is null");

            double totalCertificado = ejbCertificaciones.sumarCampo(parametros).doubleValue();

            return totalCertificado + totalAdjudicado;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean isPuedePedir() {
        if (empleado == null) {
            return false;
        }
        // traer los datos que e necesita
//        PSR
        Map parametros = new HashMap();
        parametros.put(";where", "o.cabecera.texto='PSR' and o.empleado=:empleado");
        parametros.put("empleado", empleado);
        try {
            List<Cabeceraempleados> lExtra = ejbCabemp.encontarParametros(parametros);

            for (Cabeceraempleados c : lExtra) {
                if (c.getValortexto().equalsIgnoreCase("SI")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (org.errores.sfccbdmq.ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(GestionReformasPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
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
     * @return the proyectosPoaBean
     */
    public ProyectosPoaBean getProyectosPoaBean() {
        return proyectosPoaBean;
    }

    /**
     * @param proyectosPoaBean the proyectosPoaBean to set
     */
    public void setProyectosPoaBean(ProyectosPoaBean proyectosPoaBean) {
        this.proyectosPoaBean = proyectosPoaBean;
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
     * @return the reforma
     */
    public Reformaspoa getReforma() {
        return reforma;
    }

    /**
     * @param reforma the reforma to set
     */
    public void setReforma(Reformaspoa reforma) {
        this.reforma = reforma;
    }

    /**
     * @return the reformas
     */
    public List<Reformaspoa> getReformaspoa() {
        return reformas;
    }

    /**
     * @param reformas the reformas to set
     */
    public void setReformaspoa(List<Reformaspoa> reformas) {
        this.reformas = reformas;
    }

    /**
     * @return the reformasb
     */
    public List<Reformaspoa> getReformaspoab() {
        return reformasb;
    }

    /**
     * @param reformasb the reformasb to set
     */
    public void setReformaspoab(List<Reformaspoa> reformasb) {
        this.reformasb = reformasb;
    }

    /**
     * @return the cabecerasReformaspoa
     */
    public List<Cabecerareformaspoa> getCabecerasReformaspoa() {
        return cabecerasReformaspoa;
    }

    /**
     * @param cabecerasReformaspoa the cabecerasReformaspoa to set
     */
    public void setCabecerasReformaspoa(List<Cabecerareformaspoa> cabecerasReformaspoa) {
        this.cabecerasReformaspoa = cabecerasReformaspoa;
    }

    /**
     * @return the cabeceraReforma
     */
    public Cabecerareformaspoa getCabeceraReforma() {
        return cabeceraReforma;
    }

    /**
     * @param cabeceraReforma the cabeceraReforma to set
     */
    public void setCabeceraReforma(Cabecerareformaspoa cabeceraReforma) {
        this.cabeceraReforma = cabeceraReforma;
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

    /**
     * @return the totales
     */
    public List<AuxiliarCargaPoa> getTotales() {
        return totales;
    }

    /**
     * @param totales the totales to set
     */
    public void setTotales(List<AuxiliarCargaPoa> totales) {
        this.totales = totales;
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
     * @return the consultasPoa
     */
    public ConsultasPoaBean getConsultasPoa() {
        return consultasPoa;
    }

    /**
     * @param consultasPoa the consultasPoa to set
     */
    public void setConsultasPoa(ConsultasPoaBean consultasPoa) {
        this.consultasPoa = consultasPoa;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
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

}
