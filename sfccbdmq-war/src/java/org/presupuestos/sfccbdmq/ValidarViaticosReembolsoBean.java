/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;

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
import org.contabilidad.sfccbdmq.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.DetallecompromisoFacade;
import org.beans.sfccbdmq.DetalleviaticosFacade;
import org.beans.sfccbdmq.DocumentosFacade;
import org.beans.sfccbdmq.ObligacionesFacade;
import org.beans.sfccbdmq.PuntoemisionFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.beans.sfccbdmq.RetencionesFacade;
import org.beans.sfccbdmq.RetencionescomprasFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.PagoRetencionesBean;
import org.compras.sfccbdmq.ProveedoresBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Compromisos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Detallecompromiso;
import org.entidades.sfccbdmq.Detalleviaticos;
import org.entidades.sfccbdmq.Formatos;
import org.entidades.sfccbdmq.Obligaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Puntoemision;
import org.entidades.sfccbdmq.Retencionescompras;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.icefaces.ace.model.table.LazyDataModel;
import org.personal.sfccbdmq.ViaticosEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author edison
 */
@ManagedBean(name = "validaViaticosReembolsosSfccbdmq")
@ViewScoped
public class ValidarViaticosReembolsoBean {

    private Perfiles perfil;
    private LazyDataModel<Viaticos> listadoViaticos;
    private Formulario formulario = new Formulario();
    private Viaticosempleado empleado;
    private List<Viaticosempleado> listaEmpleados;
    private Date desde;
    private Date hasta;
    private String motivo;
    private Codigos tipoViaje;
    private Integer tipoPartida;
    private List<Detalleviaticos> listaDetalles = new LinkedList<>();
    private Detalleviaticos detalle;

    private Obligaciones obligacion;
    private Retencionescompras retencion;
    private List<Retencionescompras> listaRetenciones;
    private Puntoemision puntoEmision;
    private Formulario formularioRetencion = new Formulario();
    private boolean volverV = false;
    private Asignaciones asignacion;
    private Date fecha;

    @EJB
    private DetalleviaticosFacade ejbDetalleViatico;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private ViaticosFacade ejbViaticos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleados;
    @EJB
    private ObligacionesFacade ejbObligaciones;
    @EJB
    private RetencionescomprasFacade ejbRetenciones;
    @EJB
    private PuntoemisionFacade ejbPuntos;
    @EJB
    private DocumentosFacade ejbDocumentos;
    @EJB
    private RetencionesFacade ejbRetencionesSri;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private CertificacionesFacade ejbCertificaciones;
    @EJB
    private DetallecertificacionesFacade ejbDetalles;
    @EJB
    private ReformasFacade ejbReformas;
    @EJB
    private CertificacionespoaFacade ejbCertificacionespoa;
    @EJB
    private DetallecertificacionespoaFacade ejbDetallepoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;
    @EJB
    private PartidaspoaFacade ejbPartidaspoa;
    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private CompromisosFacade ejbCompromisos;
    @EJB
    private DetallecompromisoFacade ejbDetallecompromiso;

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{viaticosSfccbdmq}")
    private ViaticosBean viaticosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorsBean;

    @PostConstruct
    private void activar() {
        hasta = configuracionBean.getConfiguracion().getPfinal();
        desde = configuracionBean.getConfiguracion().getPinicial();
        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String perfilStr = (String) params.get("p");
        if (perfilStr == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfilStr));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
    }

    /**
     * Creates a new instance of CertificacionesBean
     */
    public ValidarViaticosReembolsoBean() {
    }

    public SelectItem[] getComboEmpleadosViaticosDirecto() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viatico.vigente=true and o.estado=0 and o.kardex is null and o.fechaliquidacion is null  and o.viatico.reembolso=true");
            parametros.put(";orden", "o.empleado.entidad.apellidos,o.empleado.entidad.nombres,o.desde");
            List<Viaticosempleado> lista = ejbViaticosEmpleados.encontarParametros(parametros);
            List<Viaticosempleado> retorno = new LinkedList<>();
            return Combos.getSelectItems(lista, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboProyectos() {
        try {
            if (empleado == null) {
                return null;
            }
            if (fecha == null) {
                return null;
            }
            Calendar ca = Calendar.getInstance();
            ca.setTime(fecha);
            int anio = ca.get(Calendar.YEAR);
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.activo=true");
            parametros.put("codigo", empleado.getViatico().getPartida());
            List<Clasificadores> listaC = ejbClasificadores.encontarParametros(parametros);
            Clasificadores c = null;
            if (!listaC.isEmpty()) {
                c = listaC.get(0);
            }
            if (c == null) {
                return null;
            }

            parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.proyecto.anio=:anio");
            parametros.put("clasificador", c);
            parametros.put("anio", anio);
            List<Asignaciones> lista = ejbAsignaciones.encontarParametros(parametros);
            return Combos.getSelectItems(lista, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String buscar() {
        volverV = false;
        asignacion = null;
        try {
            if (empleado == null) {
                MensajesErrores.advertencia("Indicar el empleado");
                return null;
            }
            fecha = new Date();
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado and (o.validado is null or o.validado=false )");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
            empleado.setRealizoviaje(Boolean.TRUE);
            parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado and o.validado=true");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            List<Detalleviaticos> lista = ejbDetalleViatico.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                volverV = true;
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String validarNuevamente() {
        try {
            if (empleado == null) {
                MensajesErrores.advertencia("Indicar el empleado");
                return null;
            }
            empleado.setRealizoviaje(Boolean.TRUE);
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado");
            parametros.put("empleado", empleado);
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
            for (Detalleviaticos dv : listaDetalles) {
                dv.setValidado(false);
                dv.setValorvalidado(null);
                ejbDetalleViatico.edit(dv, seguridadbean.getLogueado().getUserid());
            }
            buscar();
            MensajesErrores.advertencia("Transacción exitosa");
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String validar() {
        empleado = listaEmpleados.get(formulario.getFila().getRowIndex());

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.viaticoempleado=:empleado ");
            parametros.put("empleado", empleado);
            parametros.put(";orden", "o.tipo desc, o.fecha desc");
            listaDetalles = ejbDetalleViatico.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String validarDetalle(Detalleviaticos det) {
        this.detalle = det;
        imagenesBean.setIdModulo(detalle.getId());
        imagenesBean.setModulo("DETALLEVIATICO");
        imagenesBean.Buscar();
        obligacion = detalle.getObligacion();
        if (obligacion == null) {
            obligacion = new Obligaciones();
            obligacion.setContrato(null);
            obligacion.setFechaemision(detalle.getFecha());
            obligacion.setEstado(2); // No se si 1 o que
            obligacion.setFechaingreso(new Date());
            obligacion.setFechar(detalle.getFecha());
            obligacion.setUsuario(seguridadbean.getLogueado());
        }
        traerRetenciones();
        formulario.editar();
        return null;
    }

    public String quitarValidacion(Detalleviaticos det) {
        this.detalle = det;
        detalle.setValidado(Boolean.FALSE);
        try {
            ejbDetalleViatico.edit(detalle, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String grabar() {
        if (asignacion == null) {
            MensajesErrores.advertencia("Seleccione un Proyecto");
            return null;
        }

        if (listaDetalles == null) {
            listaDetalles = new LinkedList<>();
        }
        try {
            if (empleado.getContrafactura() == null) {
                empleado.setContrafactura(Boolean.FALSE);
            }
            empleado.setRealizoviaje(Boolean.TRUE);
            empleado.setNrosubsistencias(0);
            double suma = 0;
            for (Detalleviaticos d : listaDetalles) {
                if (!d.getValidado()) {
                    MensajesErrores.advertencia("Faltan detalles por validar");
                    return null;
                }
                if (d.getValorvalidado() == null) {
                    MensajesErrores.advertencia("Faltan valores por validar");
                    return null;
                }
                if (d.getValorvalidado() != null) {
                    suma += d.getValorvalidado().doubleValue();
                }
            }
            empleado.setValor(new BigDecimal(suma));
            empleado.setEstado(2);
            if (validarPresupuesto(suma)) {
                MensajesErrores.advertencia("Excede el saldo presupuestado");
                return null;
            }
            grabarCertificacionCompromiso(suma);
            ejbViaticosEmpleados.edit(empleado, seguridadbean.getLogueado().getUserid());
            listaDetalles = null;
            empleado = null;
            asignacion = null;
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Validado correctamente");
        return null;
    }

    public boolean validarPresupuesto(double suma) {
        double valorAsignacion = asignacion.getValor().doubleValue();
        double valorReformas = calculaTotalReformas();
        double valorCertificacion = calculaTotalCertificaciones();
        double total = valorAsignacion + valorReformas - (valorCertificacion + suma);
        if (total < 0) {
            return true;
        }
        return false;
    }

    public double calculaTotalReformas() {
        if (asignacion == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put("asignacion", asignacion);
        parametros.put("anio", asignacion.getProyecto().getAnio());
        parametros.put(";where", "o.asignacion=:asignacion and o.cabecera.definitivo=true  and o.asignacion.proyecto.anio=:anio");
        try {
            return ejbReformas.sumarCampo(parametros).doubleValue();

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double calculaTotalCertificaciones() {
        if (asignacion == null) {
            return 0;
        }
        Map parametros = new HashMap();
        parametros.put(";campo", "o.valor");
        parametros.put(";where", "o.asignacion=:asignacion and o.certificacion.impreso=true and o.certificacion.anulado=false "
                + " and o.asignacion.proyecto.anio=:anio");
        parametros.put("asignacion", asignacion);
        parametros.put("anio", asignacion.getProyecto().getAnio());
        try {
            return ejbDetalles.sumarCampo(parametros).doubleValue();
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ReporteReformasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void grabarCertificacionCompromiso(double suma) {
        try {
            //Certificación
            Integer num = 0;
            Integer nume = 0;
            Codigos cod = ejbCodigos.traerCodigo("NUM", "04");
            if (cod == null) {
                num = 0;
            } else {
                if (cod.getParametros() == null) {
                    num = 0;
                } else {
                    num = Integer.parseInt(cod.getParametros());
                    nume = num + 1;
                }
            }
            Certificaciones cert = new Certificaciones();
            cert.setFecha(fecha);
            cert.setMotivo(empleado.getViatico().getMotivo());
            cert.setImpreso(true);
            cert.setAnio(asignacion.getProyecto().getAnio());
            cert.setAnulado(false);
            cert.setRoles(false);
            cert.setDireccion(empleado.getEmpleado().getCargoactual().getOrganigrama());
            cert.setMemo(empleado.getViatico().getMotivo());
            cert.setNumerocert(nume);
            ejbCertificaciones.create(cert, seguridadbean.getLogueado().getUserid());
            if (cod != null) {
                cod.setParametros(nume + "");
                ejbCodigos.edit(cod, seguridadbean.getLogueado().getUserid());
            }
            empleado.getViatico().setCertificacion(cert);
            ejbViaticos.edit(empleado.getViatico(), seguridadbean.getLogueado().getUserid());
            //Detalle certificaion
            Detallecertificaciones dc = new Detallecertificaciones();
            dc.setAsignacion(asignacion);
            dc.setValor(new BigDecimal(suma));
            dc.setCertificacion(cert);
            dc.setFecha(fecha);
            ejbDetalles.create(dc, seguridadbean.getLogueado().getUserid());
            //Certificación POA
            Certificacionespoa certFin = new Certificacionespoa();
            certFin.setAnio(cert.getAnio());
            certFin.setAnulado(false);
            certFin.setFecha(cert.getFecha());
            certFin.setMemo(cert.getMemo());
            certFin.setMotivo(cert.getMotivo());
            certFin.setRoles(cert.getRoles());
            certFin.setFinaciero(cert.getId());
            certFin.setImpreso(true);
            certFin.setNumero("0");
            ejbCertificacionespoa.create(certFin, seguridadbean.getLogueado().getUserid());
            //Detalle certificacion POA
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put("certificacion", cert);
            List<Detallecertificaciones> listaCert = ejbDetalles.encontarParametros(parametros);
            for (Detallecertificaciones d : listaCert) {
                if (d.getId() != null) {
                    parametros = new HashMap();
                    parametros.put(";where", "o.codigo=:codigo and o.anio=:anio and o.activo=true");
                    parametros.put("codigo", d.getAsignacion().getProyecto().getCodigo());
                    parametros.put("anio", d.getAsignacion().getProyecto().getAnio());
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
                            + " and o.proyecto=:proyecto and o.fuente=:fuente and o.activo=true and o.proyecto.anio=:anio");
                    parametros.put("partida", c);
                    parametros.put("proyecto", p);
                    parametros.put("fuente", co.getCodigo());
                    parametros.put("anio", cert.getAnio());
                    List<Asignacionespoa> listaa = ejbAsignacionespoa.encontarParametros(parametros);
                    Asignacionespoa af = null;
                    for (Asignacionespoa af1 : listaa) {
                        af = af1;
                    }
                    Detallecertificacionespoa d2 = new Detallecertificacionespoa();
                    d2.setAsignacion(af);
                    d2.setCertificacion(certFin);
                    d2.setValor(new BigDecimal(suma));
                    d2.setFecha(fecha);
                    ejbDetallepoa.create(d2, seguridadbean.getLogueado().getUserid());
                }
            }
            //Compromisos
            int anioCertificacion = cert.getAnio();
            int numeC = 0;
            int numC = 0;
            Calendar ca = Calendar.getInstance();
            ca.setTime(new Date());
            int anioActual = ca.get(Calendar.YEAR);
            Codigos codi;
            if (anioCertificacion == anioActual) {
                codi = ejbCodigos.traerCodigo("NUM", "09");
                if (codi == null) {
                    numC = 0;
                } else {
                    if (codi.getParametros() == null) {
                        numC = 0;
                    } else {
                        numC = Integer.parseInt(codi.getParametros());
                        numeC = numC + 1;
                    }
                }
            } else {
                codi = ejbCodigos.traerCodigo("NUM", "05");
                if (codi == null) {
                    numC = 0;
                } else {
                    if (codi.getParametros() == null) {
                        numC = 0;
                    } else {
                        numC = Integer.parseInt(codi.getParametros());
                        numeC = numC + 1;
                    }
                }
            }
            //Compromisos
            Compromisos c = new Compromisos();
            c.setFecha(fecha);
            c.setContrato(null);
            c.setImpresion(empleado.getFechaliquidacion());
            c.setMotivo(empleado.getViatico().getMotivo());
            c.setProveedor(null);
            c.setCertificacion(cert);
            c.setEmpleado(empleado.getEmpleado());
            c.setBeneficiario(empleado.getEmpleado());
            c.setNumerocomp(numeC);
            c.setAnio(empleado.getViatico().getCertificacion().getAnio());
            c.setImpresion(fecha);
            ejbCompromisos.create(c, seguridadbean.getLogueado().getUserid());
            if (codi != null) {
                codi.setParametros(numeC + "");
                ejbCodigos.edit(codi, seguridadbean.getLogueado().getUserid());
            }
            //Detalle Compromisos
            Detallecompromiso dcopm = new Detallecompromiso();
            dcopm.setFecha(fecha);
            dcopm.setCompromiso(c);
            dcopm.setAsignacion(asignacion);
            dcopm.setMotivo(empleado.getViatico().getMotivo());
            dcopm.setFecha(empleado.getFechaliquidacion());
            dcopm.setValor(new BigDecimal(suma));
            dcopm.setSaldo(new BigDecimal(BigInteger.ZERO));
            ejbDetallecompromiso.create(dcopm, seguridadbean.getLogueado().getUserid());
            empleado.setDetallecompromiso(dcopm.getId());
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String grabarDetalle() {
        if (detalle.getValorvalidado() == null) {
            MensajesErrores.advertencia("Indicar el valor validado");
            return null;
        }
        if (detalle.getDetallevalidado() == null || detalle.getDetallevalidado().trim().isEmpty()) {
            MensajesErrores.advertencia("Indicar el detalle de validación");
            return null;
        }
        try {
            detalle.setValidado(Boolean.TRUE);
            if (obligacion.getDocumento() != null) {
                if (obligacion != null) {
                    if (obligacion.getId() != null) {
                        detalle.setObligacion(obligacion);
                    } else {
                        ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
                        detalle.setObligacion(obligacion);
                    }
                } else {
                    ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
                    detalle.setObligacion(obligacion);
                }
                obligacion.setFechacaduca(detalle.getFecha());
                obligacion.setProveedor(proveedorsBean.getProveedor());
                ejbObligaciones.edit(obligacion, seguridadbean.getLogueado().getUserid());
            }
            ejbDetalleViatico.edit(detalle, seguridadbean.getLogueado().getUserid());
            double suma = 0;
            for (Detalleviaticos dv : listaDetalles) {
                if (dv.getValorvalidado() != null) {
                    suma += dv.getValorvalidado().doubleValue();
                }
            }
            empleado.setValor(new BigDecimal(suma));
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(ValidarViaticosReembolsoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
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
     * @return the empleadosBean
     */
    public EmpleadosBean getEmpleadosBean() {
        return empleadosBean;
    }

    /**
     * @param empleadosBean the empleadosBean to set
     */
    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
        this.empleadosBean = empleadosBean;
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
     * @return the tipoViaje
     */
    public Codigos getTipoViaje() {
        return tipoViaje;
    }

    /**
     * @param tipoViaje the tipoViaje to set
     */
    public void setTipoViaje(Codigos tipoViaje) {
        this.tipoViaje = tipoViaje;
    }

    /**
     * @return the tipoPartida
     */
    public Integer getTipoPartida() {
        return tipoPartida;
    }

    /**
     * @param tipoPartida the tipoPartida to set
     */
    public void setTipoPartida(Integer tipoPartida) {
        this.tipoPartida = tipoPartida;
    }

    /**
     * @return the empleado
     */
    public Viaticosempleado getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Viaticosempleado empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the listaEmpleados
     */
    public List<Viaticosempleado> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<Viaticosempleado> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the listadoViaticos
     */
    public LazyDataModel<Viaticos> getListadoViaticos() {
        return listadoViaticos;
    }

    /**
     * @param listadoViaticos the listadoViaticos to set
     */
    public void setListadoViaticos(LazyDataModel<Viaticos> listadoViaticos) {
        this.listadoViaticos = listadoViaticos;
    }

    /**
     * @return the viaticosBean
     */
    public ViaticosBean getViaticosBean() {
        return viaticosBean;
    }

    /**
     * @param viaticosBean the viaticosBean to set
     */
    public void setViaticosBean(ViaticosBean viaticosBean) {
        this.viaticosBean = viaticosBean;
    }

    /**
     * @return the listaDetalles
     */
    public List<Detalleviaticos> getListaDetalles() {
        return listaDetalles;
    }

    /**
     * @param listaDetalles the listaDetalles to set
     */
    public void setListaDetalles(List<Detalleviaticos> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    /**
     * @return the detalle
     */
    public Detalleviaticos getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detalleviaticos detalle) {
        this.detalle = detalle;
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
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
    }

    /////////////////NUEVO///////////////////////////////
    private void traerRetenciones() {
        if (obligacion != null) {
            if (obligacion.getId() != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.obligacion=:obligacion");
                parametros.put("obligacion", obligacion);
                try {
                    listaRetenciones = ejbRetenciones.encontarParametros(parametros);
                    if (obligacion.getPuntor() != null) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.codigo=:codigo and o.sucursal.ruc=:sucursal");
                        parametros.put("codigo", obligacion.getPuntor());
                        parametros.put("sucursal", obligacion.getEstablecimientor());
                        List<Puntoemision> lpt = ejbPuntos.encontarParametros(parametros);
                        for (Puntoemision p : lpt) {
                            puntoEmision = p;
                        }
                    }
                } catch (ConsultarException ex) {
                    Logger.getLogger(ContabilizacionCajasBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
//////////////////////FIN NUEVO

    public String nuevaRetencion() {
        retencion = new Retencionescompras();
        retencion.setBien(true);
        retencion.setBaseimponible(new BigDecimal(BigInteger.ZERO));
        retencion.setBaseimponible0(new BigDecimal(BigInteger.ZERO));
        retencion.setIva(new BigDecimal(BigInteger.ZERO));
        retencion.setValor(new BigDecimal(BigInteger.ZERO));
        retencion.setValoriva(new BigDecimal(BigInteger.ZERO));
        formularioRetencion.insertar();

        return null;
    }

    public String modificaRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioRetencion.editar();
        return null;
    }

    public String borraRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
        formularioRetencion.eliminar();
        return null;
    }

    private boolean validarRetenciones() {
        double valor = 0;
        if (proveedorsBean.getProveedor() == null) {
            MensajesErrores.advertencia("Ingrese Proveedor");
            return true;
        }
        if (obligacion.getTipodocumento() == null) {
            MensajesErrores.advertencia("Seleccione tipo de documento");
            return true;
        }
        if (obligacion.getDocumento() == null) {
            MensajesErrores.advertencia("Ingrese número");
            return true;
        }
        if (obligacion.getEstablecimiento() == null || obligacion.getEstablecimiento().isEmpty()) {
            MensajesErrores.advertencia("Ingrese establecimiento");
            return true;
        }
        if (obligacion.getPuntoemision() == null || obligacion.getPuntoemision().isEmpty()) {
            MensajesErrores.advertencia("Ingrese punto de emision");
            return true;
        }
        if (obligacion.getAutorizacion() == null || obligacion.getAutorizacion().isEmpty()) {
            MensajesErrores.advertencia("Ingrese autorización");
            return true;
        }
        if (retencion.getRetencion() != null) {
            if ((retencion.getValorprima() == null) || (retencion.getValorprima().doubleValue() <= 0)) {
                valor = (retencion.getBaseimponible().doubleValue() + retencion.getBaseimponible0().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            } else {
                valor = (retencion.getValorprima().doubleValue())
                        * retencion.getRetencion().getPorcentaje().doubleValue() / 100;
                double ajuste = retencion.getAjusteRf();
                valor += ajuste / 100;
            }
        }

        double iva = 0;
        if (retencion.getImpuesto() != null) {
            iva = retencion.getBaseimponible().doubleValue()
                    * retencion.getImpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteIva();
            iva += ajuste / 100;
        }
        double valorIva = 0;
        if (retencion.getRetencionimpuesto() != null) {
            valorIva = iva * retencion.getRetencionimpuesto().getPorcentaje().doubleValue() / 100;
            double ajuste = retencion.getAjusteRi();
            valorIva += ajuste / 100;
        }
        retencion.setIva(new BigDecimal(iva));
        retencion.setValor(new BigDecimal(valor));
        retencion.setValoriva(new BigDecimal(valorIva));
        return false;
    }

    public String insertarRetenciones() {
        if (validarRetenciones()) {
            return null;
        }
        try {
            if (obligacion.getId() == null) {
                ejbObligaciones.create(obligacion, seguridadbean.getLogueado().getUserid());
            }
            retencion.setObligacion(obligacion);
            ejbRetenciones.create(retencion, seguridadbean.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public String grabarRetenciones() {
//        if (validarDetalle()) {
//            return null;
//        }
        try {
            ejbRetenciones.edit(retencion, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public String borrarRetenciones() {

        try {
            ejbRetenciones.remove(retencion, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            Logger.getLogger(PagoRetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        traerRetenciones();
        formularioRetencion.cancelar();
        return null;
    }

    public void traerDetallesRetenciones() {
        listaRetenciones = new LinkedList<>();
//        Map parametros = new HashMap();
//        parametros.put(";where", " o.obligacion.vale.fondo=:fondo and o.vale.estado=2");
//        parametros.put("fondo", viatico);
//        try {
//            listaRetenciones = ejbRetenciones.encontarParametros(parametros);
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AutorizacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * @return the obligacion
     */
    public Obligaciones getObligacion() {
        return obligacion;
    }

    /**
     * @param obligacion the obligacion to set
     */
    public void setObligacion(Obligaciones obligacion) {
        this.obligacion = obligacion;
    }

    /**
     * @return the retencion
     */
    public Retencionescompras getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(Retencionescompras retencion) {
        this.retencion = retencion;
    }

    /**
     * @return the listaRetenciones
     */
    public List<Retencionescompras> getListaRetenciones() {
        return listaRetenciones;
    }

    /**
     * @param listaRetenciones the listaRetenciones to set
     */
    public void setListaRetenciones(List<Retencionescompras> listaRetenciones) {
        this.listaRetenciones = listaRetenciones;
    }

    /**
     * @return the puntoEmision
     */
    public Puntoemision getPuntoEmision() {
        return puntoEmision;
    }

    /**
     * @param puntoEmision the puntoEmision to set
     */
    public void setPuntoEmision(Puntoemision puntoEmision) {
        this.puntoEmision = puntoEmision;
    }

    /**
     * @return the formularioRetencion
     */
    public Formulario getFormularioRetencion() {
        return formularioRetencion;
    }

    /**
     * @param formularioRetencion the formularioRetencion to set
     */
    public void setFormularioRetencion(Formulario formularioRetencion) {
        this.formularioRetencion = formularioRetencion;
    }

    public SelectItem[] getComboRetenciones() {
        if (retencion == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=false and o.bien=:bien");
        parametros.put("bien", retencion.getBien());
        try {
            return Combos.getSelectItems(ejbRetencionesSri.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPartidas() {
        Formatos f = ejbDocumentos.traerFormato();
        if (f == null) {
            MensajesErrores.advertencia("Mal configurado formato");
            return null;
        }
        List<String> partidas = new LinkedList<>();
        String xx = f.getFormato().replace(".", "#");
        String[] aux = xx.split("#");
//        if (listaDetalle == null) {
        if (detalle == null) {
            return null;
        }
        int tamano = aux[f.getNivel() - 1].length();
        String cuentaPresupuesto = empleado.getViatico().getPartida();
        String presupuesto = cuentaPresupuesto.substring(0, tamano);
        estaPartida(presupuesto, partidas);
        return Combos.comboStrings(partidas, false);
    }

    private void estaPartida(String partida, List<String> partidas) {
        if (partidas == null) {
            partidas = new LinkedList<>();
            partidas.add(partida);
            return;
        }
        for (String p : partidas) {
            if (p.equalsIgnoreCase(partida)) {
                return;
            }
        }
        partidas.add(partida);
    }

    public SelectItem[] getComboRetencionesImpuestos() {
        if (retencion == null) {
            return null;
        }
        boolean especial = false;
        if (obligacion.getProveedor() != null) {

            especial = obligacion.getProveedor().getEmpresa().getEspecial();
        } else {
//            Proveedores pro = vale.getProveedor();
//            if (pro != null) {
//                obligacion.setProveedor(pro);
//                especial = obligacion.getProveedor().getEmpresa().getEspecial();
//            }
        }
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        parametros.put(";where", "o.impuesto=true and o.bien=:bien and o.especial=:especial");
        parametros.put("especial", especial);
        parametros.put("bien", retencion.getBien());
        try {
            return Combos.getSelectItems(ejbRetencionesSri.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(RetencionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the proveedorsBean
     */
    public ProveedoresBean getProveedorsBean() {
        return proveedorsBean;
    }

    /**
     * @param proveedorsBean the proveedorsBean to set
     */
    public void setProveedorsBean(ProveedoresBean proveedorsBean) {
        this.proveedorsBean = proveedorsBean;
    }

    /**
     * @return the volverV
     */
    public boolean isVolverV() {
        return volverV;
    }

    /**
     * @param volverV the volverV to set
     */
    public void setVolverV(boolean volverV) {
        this.volverV = volverV;
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
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

}
