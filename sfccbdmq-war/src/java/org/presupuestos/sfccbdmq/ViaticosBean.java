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
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import javax.mail.MessagingException;
import org.auxiliares.sfccbdmq.AuxiliarReporte;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.DocumentoPDF;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.CertificacionesFacade;
import org.beans.sfccbdmq.ClasificadoresFacade;
import org.beans.sfccbdmq.CompromisosFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.KardexbancoFacade;
import org.beans.sfccbdmq.OrganigramaFacade;
import org.beans.sfccbdmq.SfccbdmqCorreosFacade;
import org.beans.sfccbdmq.ViaticosFacade;
import org.beans.sfccbdmq.ViaticosempleadoFacade;
import org.compras.sfccbdmq.PagosVencimientosBean;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Certificaciones;
import org.entidades.sfccbdmq.Clasificadores;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Detallecertificaciones;
import org.entidades.sfccbdmq.Kardexbanco;
import org.entidades.sfccbdmq.Viaticos;
import org.entidades.sfccbdmq.Viaticosempleado;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.pagos.sfccbdmq.PagoPrestamosBean;
import org.personal.sfccbdmq.ViaticosEmpleadoBean;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.talento.sfccbdmq.EmpleadosBean;
import org.utilitarios.sfccbdmq.Recurso;

/**
 *
 * @author edison
 */
@ManagedBean(name = "viaticosSfccbdmq")
@ViewScoped
public class ViaticosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private LazyDataModel<Viaticos> listadoViaticos;
    private Formulario formulario = new Formulario();
    private Formulario formularioEmpleados = new Formulario();
    private Formulario formularioConfirmacion = new Formulario();
    private Formulario formularioEditarValor = new Formulario();
    private Formulario formularioImprimir = new Formulario();
    private Date desde;
    private Date hasta;
    private Viaticosempleado empleado;
    private List<Viaticosempleado> listaEmpleados;
    private List<Viaticosempleado> listaEmpleadosB;
    private String motivo;
    private Codigos tipoViaje;
    private Viaticos viatico;
    private Integer tipoPartida;
    private Detallecertificaciones detalle;
    private List<Detallecertificaciones> detalles;
    private Asignaciones asignacion;
    private Certificaciones certificacion;
    private Kardexbanco kardex;
    private Date fechaCertificacion;
    private Recurso reportepdf;
    private String lugar;

    @EJB
    private ViaticosFacade ejbViaticos;
    @EJB
    private ViaticosempleadoFacade ejbViaticosEmpleados;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private DetallecertificacionesFacade ejbDetalleCertificacion;
    @EJB
    private ClasificadoresFacade ejbClasificadores;
    @EJB
    private KardexbancoFacade ejbKardex;
    @EJB
    private SfccbdmqCorreosFacade ejbCorreos;
    @EJB
    private CertificacionesFacade ejbCertificaciones;

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

    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadosBean;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{certificacionesSfccbdmq}")
    private CertificacionesBean certificacionesBean;
    @ManagedProperty(value = "#{pagoPrestamos}")
    private PagoPrestamosBean pagosBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;

    @PostConstruct
    private void activar() {
        setDesde(configuracionBean.getConfiguracion().getPinicialpresupuesto());
        setHasta(configuracionBean.getConfiguracion().getPfinalpresupuesto());
    }

    /**
     * Creates a new instance of DetallecertificacionesEmpleadoBean
     */
    public ViaticosBean() {
        listadoViaticos = new LazyDataModel<Viaticos>() {

            @Override
            public List<Viaticos> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        listadoViaticos = new LazyDataModel<Viaticos>() {
            @Override
            public List<Viaticos> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                String where = "o.id is not null and o.fecha between :desde and :hasta and o.reembolso=false";
                parametros.put(";orden", "o.motivo asc");

                int total;
                try {
                    if (!((motivo == null) || (motivo.isEmpty()))) {
                        where += " o.motivo like :motivo";
                        parametros.put("motivo", getMotivo() + "%");
                    }
                    if (tipoPartida != null) {
                        where += " and o.partida=:partida";
                        parametros.put("partida", consultaPartida(tipoPartida));
                    }
                    if (lugar != null) {
                        where += " and upper(o.lugar) like :lugar";
                        parametros.put("lugar", lugar.toUpperCase() + "%");
                    }

                    parametros.put(";where", where);
//                    parametros.put(";orden", "o.certificacion.fecha desc");
                    parametros.put("desde", getDesde());
                    parametros.put("hasta", getHasta());
                    total = ejbViaticos.contar(parametros);
                    int endIndex = i + pageSize;
                    if (endIndex > total) {
                        endIndex = total;
                    }
                    parametros.put(";inicial", i);
                    parametros.put(";final", endIndex);
                    listadoViaticos.setRowCount(total);
                    return ejbViaticos.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
                    Logger.getLogger("").log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        return null;
    }

    public String nuevo() {
        if (tipoPartida == null) {
            MensajesErrores.advertencia("Indique el tipo de viaje");
            return null;
        }

        try {
            viatico = new Viaticos();
            viatico.setVigente(Boolean.TRUE);
            viatico.setPartida(consultaPartida(tipoPartida));
            viatico.setEstado(0);
            viatico.setReembolso(Boolean.FALSE);
            listaEmpleados = new LinkedList<>();
            listaEmpleadosB = new LinkedList<>();
            detalle = new Detallecertificaciones();
            detalle.setFecha(new Date());
            certificacion = new Certificaciones();
            certificacionesBean.crear();
            fechaCertificacion = new Date();
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.insertar();
        return null;
    }

    public String modificar() {
        try {
            viatico = (Viaticos) listadoViaticos.getRowData();
            certificacion = viatico.getCertificacion();
            if (certificacion == null) {
                certificacion = new Certificaciones();
            }
            traerDetalleCertificacion(certificacion);
            tipoPartida = consultaTipo(viatico.getPartida());
            buscarEmp();
            for (Viaticosempleado ve : listaEmpleados) {
                if (tipoPartida == 1) {
                    BigDecimal precio = BigDecimal.valueOf(Double.parseDouble(ve.getTipo().getParametros()));
                    BigDecimal codPais = BigDecimal.valueOf(Double.parseDouble(ve.getViatico().getPais().getParametros()));
                    BigDecimal total = precio.multiply(codPais);
                    ve.setValorParcial(total.doubleValue());
                }
            }
            listaEmpleadosB = new LinkedList<>();
            certificacionesBean.setDetalle(detalle);
            certificacionesBean.modificar(certificacion);
            if (certificacion != null) {
                fechaCertificacion = certificacion.getFecha();
            } else {
                fechaCertificacion = new Date();
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
        try {
            viatico = (Viaticos) listadoViaticos.getRowData();
            tipoPartida = consultaTipo(viatico.getPartida());
            buscarEmp();
            listaEmpleadosB = new LinkedList<>();
            certificacionesBean.setDetalle(detalle);
            certificacionesBean.eliminar(viatico.getCertificacion());
            certificacion = viatico.getCertificacion();
            fechaCertificacion = certificacion.getFecha();
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.eliminar();
        return null;
    }

    private boolean validar() {
        if (viatico.getDesde() == null) {
            MensajesErrores.advertencia("Indique fecha 'desde'");
            return true;
        }
        if (viatico.getHasta() == null) {
            MensajesErrores.advertencia("Indique fecha 'hasta'");
            return true;
        }
        if (viatico.getHasta().before(viatico.getDesde())) {
            MensajesErrores.advertencia("Fechas 'desde' no puede ser mayor a la fecha de 'hasta' ");
            return true;
        }

        if (viatico.getLugar().isEmpty() || viatico.getLugar().trim().isEmpty()) {
            MensajesErrores.advertencia("Indique el lugar");
            return true;
        }

        if (viatico.getMotivo().isEmpty() || viatico.getMotivo().trim().isEmpty()) {
            MensajesErrores.advertencia("Indique el motivo");
            return true;
        }

        if (tipoPartida == 1) {
            if (viatico.getPais() == null) {
                MensajesErrores.advertencia("Indique el país de destino");
                return true;
            }
        }

//        if (validarKardex()) {
//            return true;
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }

        try {
            certificacionesBean.getCertificacion().setMotivo(viatico.getMotivo());
            certificacionesBean.getCertificacion().setFecha(fechaCertificacion);
            certificacionesBean.getCertificacion().setImpreso(Boolean.TRUE);
            certificacionesBean.getCertificacion().setAnulado(Boolean.FALSE);
            certificacionesBean.getCertificacion().setNumeroanterior(certificacion.getNumeroanterior());
            certificacionesBean.insertar();
            viatico.setFecha(new Date());
            if (certificacionesBean.getCertificacion() == null) {
                MensajesErrores.advertencia("No se creo cedrtificacion");
                return null;
            }
            if (certificacionesBean.getCertificacion().getId() == null) {
                MensajesErrores.advertencia("No se creo id de certificacion");
                return null;
            }
            viatico.setCertificacion(certificacionesBean.getCertificacion());
            ejbViaticos.create(viatico, seguridadbean.getLogueado().getUserid());
            for (Viaticosempleado ve : listaEmpleados) {
                ve.setViatico(viatico);
                ve.setEstado(0);
                ejbViaticosEmpleados.create(ve, seguridadbean.getLogueado().getUserid());
//                enviarNotificacion(ve);
//                if (ve.getId() == null) {
//                    ejbViaticosEmpleados.create(ve, seguridadbean.getLogueado().getUserid());
//                    nuevoKardex(ve);
//                    grabarKardex();
//                    //ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                } else {
//                    ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                    if (ve.getKardex() != null) {
//                        kardex = ve.getKardex();
//                        grabarKardex();
//                    } else {
//                        nuevoKardex(ve);
//                        grabarKardex();
//                    }
//                    ve.setKardex(kardex);
//                    ve.setValor(ve.getValor());
//                    ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                }

            }

            //// pacpoa**********************************************************************
            if (certificacion.getPacpoa() == null) {
//            // pasar al lado pacpoa
                // Organigrama
//                Map parametros = new HashMap();
//                parametros.put(";where", "o.codigo=:codigo");
//                parametros.put("codigo", certificacionesBean.getCertificacion().getDireccion().getCodigo());
//                List<Organigrama> listaOrg = ejbOrganigrama.encontarParametros(parametros);
//                Organigrama o = null;
//                for (Organigrama o1 : listaOrg) {
//                    o = o1;
//                }
                Map parametros = new HashMap();

                Certificacionespoa certFin = new Certificacionespoa();
                certFin.setAnio(certificacionesBean.getCertificacion().getAnio());
                certFin.setAnulado(false);
                certFin.setFecha(certificacionesBean.getCertificacion().getFecha());
                certFin.setMemo(certificacionesBean.getCertificacion().getMemo());
                certFin.setMotivo(certificacionesBean.getCertificacion().getMotivo());
                certFin.setRoles(certificacionesBean.getCertificacion().getRoles());
                certFin.setFinaciero(certificacionesBean.getCertificacion().getId());
//                certFin.setDireccion(certificacionesBean.getCertificacion().getDireccion().getCodigo());
                certFin.setImpreso(true);
                certFin.setNumero("0");
                ejbCertificacionespoa.create(certFin, seguridadbean.getLogueado().getUserid());
                parametros = new HashMap();
                parametros.put(";where", "o.certificacion=:certificacion");
                parametros.put("certificacion", certificacionesBean.getCertificacion());
                List<Detallecertificaciones> listaCert = ejbDetalleCertificacion.encontarParametros(parametros);

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
                        parametros.put("anio", certificacionesBean.getCertificacion().getAnio());
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
//            // Fin pacpoa
//            // Fin pacpoa********************************************************************
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioConfirmacion.cancelar();
//        imprimir();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            viatico.setFecha(new Date());
            viatico.setPartida(consultaPartida(tipoPartida));
            ejbViaticos.edit(viatico, seguridadbean.getLogueado().getUserid());
            for (Viaticosempleado ve : listaEmpleados) {
                ve.setViatico(viatico);
                ve.setEstado(0);
                ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                enviarNotificacion(ve);
//                if (ve.getId() == null) {
//                    ejbViaticosEmpleados.create(ve, seguridadbean.getLogueado().getUserid());
//                    nuevoKardex(ve);
//                    grabarKardex();
//                    //ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                } else {
//                    ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                    if (ve.getKardex() != null) {
//                        kardex = ve.getKardex();
//                        grabarKardex();
//                    } else {
//                        nuevoKardex(ve);
//                        grabarKardex();
//
//                    }
//                    ve.setKardex(kardex);
//                    ve.setValor(ve.getValor());
//                    ejbViaticosEmpleados.edit(ve, seguridadbean.getLogueado().getUserid());
//                }
            }
            certificacionesBean.getCertificacion().setMotivo(viatico.getMotivo());
            certificacionesBean.getCertificacion().setFecha(fechaCertificacion);
            certificacionesBean.getCertificacion().setNumeroanterior(certificacion.getNumeroanterior());
            certificacionesBean.grabar();
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        imprimir();
        formulario.cancelar();
        formularioConfirmacion.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            certificacion = viatico.getCertificacion();
            viatico.setCertificacion(null);
            ejbViaticos.edit(viatico, seguridadbean.getLogueado().getUserid());
            certificacionesBean.setCertificacion(certificacion);
            certificacionesBean.borrar();
            ejbViaticos.remove(viatico, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

//EMPLEADOS
    public String buscarEmp() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.viatico=:viatico");
        parametros.put("viatico", viatico);
        try {
            listaEmpleados = ejbViaticosEmpleados.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevoEmp() {
        if (tipoPartida == 1) {
            if (viatico.getPais() == null) {
                MensajesErrores.advertencia("Indique el país de destino");
                return null;
            }
        }
        empleadosBean.setEmpleadoSeleccionado(null);
        empleadosBean.setApellidos(null);
        empleado = new Viaticosempleado();
        empleado.setViatico(viatico);
        empleado.setDesde(viatico.getDesde());
        empleado.setHasta(viatico.getHasta());
        empleado.setNrosubsistencias(0);
        formularioEmpleados.insertar();
        return null;
    }

    public String modificarEmp() {
        empleado = listaEmpleados.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioEmpleados.editar();
        return null;
    }

    public String editarValorEmp() {
        empleado = listaEmpleados.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioEditarValor.editar();
        return null;
    }

    public String eliminarEmp() {
        empleado = listaEmpleados.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formularioEmpleados.eliminar();
        return null;
    }

    public boolean validarEmp() {
        if (empleado.getDesde() == null) {
            MensajesErrores.advertencia("Indique fecha 'desde'");
            return true;
        }
        if (empleado.getHasta() == null) {
            MensajesErrores.advertencia("Indique fecha 'hasta'");
            return true;
        }
        if (empleado.getHasta().before(empleado.getDesde())) {
            MensajesErrores.advertencia("Fechas 'desde' no puede ser mayor a la fecha de 'hasta' ");
            return true;
        }
        if (empleado.getDesde().before(viatico.getDesde()) || empleado.getDesde().after(viatico.getHasta())) {
            MensajesErrores.advertencia("Fechas 'desde' debe estar entre las fechas del viaje ");
            return true;
        }
        if (empleado.getHasta().before(viatico.getDesde()) || empleado.getHasta().after(viatico.getHasta())) {
            MensajesErrores.advertencia("Fechas 'hasta' debe estar entre las fechas del viaje ");
            return true;
        }
        if (empleado.getTipo() == null) {
            MensajesErrores.advertencia("Indique el tipo de Organizacional ");
            return true;
        }

        return false;
    }

    public String insertarEmp() {
        if (validarEmp()) {
            return null;
        }
        empleado.setEmpleado(empleadosBean.getEmpleadoSeleccionado());
        BigDecimal precio = BigDecimal.valueOf(Double.parseDouble(empleado.getTipo().getParametros()));
        BigDecimal dias = BigDecimal.valueOf(consultaDias(empleado.getDesde(), empleado.getHasta()));

        if (tipoPartida == 0) {
            BigDecimal total = BigDecimal.valueOf(consultaDias(empleado.getDesde(), empleado.getHasta())).multiply(precio);
            empleado.setValor(total);
        }
        if (tipoPartida == 1) {
            BigDecimal codPais = BigDecimal.valueOf(Double.parseDouble(empleado.getViatico().getPais().getParametros()));
            BigDecimal total = dias.multiply(precio).multiply(codPais);
//            BigDecimal total = precio.multiply(codPais);

            BigDecimal subsistencia = (precio).multiply(codPais).multiply(BigDecimal.valueOf(0.5));
            BigDecimal totalsubsistencia = subsistencia.multiply(BigDecimal.valueOf(empleado.getNrosubsistencias()));
            empleado.setSubsistencia(totalsubsistencia);
            empleado.setValorParcial(total.doubleValue());
            empleado.setValor(total.add(totalsubsistencia));
        }
        listaEmpleados.add(empleado);
        formularioEmpleados.cancelar();
        return null;
    }

    public String grabarEmp() {
        if (validarEmp()) {
            return null;
        }
//        BigDecimal precio = BigDecimal.valueOf(Double.parseDouble(empleado.getTipo().getParametros()));
//        BigDecimal total = BigDecimal.valueOf(consultaDias(empleado.getDesde(), empleado.getHasta())).multiply(precio);
//        empleado.setValor(total);
        empleado.setEmpleado(empleadosBean.getEmpleadoSeleccionado());
        BigDecimal precio = BigDecimal.valueOf(Double.parseDouble(empleado.getTipo().getParametros()));
        BigDecimal dias = BigDecimal.valueOf(consultaDias(empleado.getDesde(), empleado.getHasta()));

        if (tipoPartida == 0) {
            BigDecimal total = BigDecimal.valueOf(consultaDias(empleado.getDesde(), empleado.getHasta())).multiply(precio);
            empleado.setValor(total);
        }
        if (tipoPartida == 1) {
            BigDecimal codPais = BigDecimal.valueOf(Double.parseDouble(empleado.getViatico().getPais().getParametros()));
            BigDecimal total = dias.multiply(precio).multiply(codPais);

            BigDecimal subsistencia = (precio).multiply(codPais).multiply(BigDecimal.valueOf(0.5));
            BigDecimal totalsubsistencia = subsistencia.multiply(BigDecimal.valueOf(empleado.getNrosubsistencias()));
            empleado.setSubsistencia(totalsubsistencia);
            empleado.setValorParcial(total.doubleValue());
            empleado.setValor(total.add(totalsubsistencia));
        }
        listaEmpleados.set(formulario.getIndiceFila(), empleado);
        formularioEmpleados.cancelar();
        return null;
    }

    public String grabarValorEmp() {

        formularioEditarValor.cancelar();
        return null;
    }

    public String borrarEmp() {
        listaEmpleadosB.add(empleado);
        listaEmpleados.remove(formulario.getIndiceFila());
        formularioEmpleados.cancelar();
        return null;
    }

    public String consultaPartida(int valor) throws ConsultarException {
        Codigos codviaticos = ejbViaticos.traerCodigo("GASTGEN", "V", null);
        if (codviaticos == null) {
            MensajesErrores.advertencia("Sin configuración de código en Gastos Generales");
            return null;
        }
        String[] registro = codviaticos.getParametros().split("#");
        if (registro != null) {
            return registro[valor];
        }
        return null;
    }

    public double consultaDias(Date d, Date h) {
        long retorno = ((h.getTime() - d.getTime()) / (60000 * 60 * 24)) + 1;
        double factor = 1;
        return retorno * factor - 1;
    }

    public int consultaTipo(String tip) throws ConsultarException {
        Codigos codviaticos = ejbViaticos.traerCodigo("GASTGEN", "V", null);
        String[] registro = codviaticos.getParametros().split("#");
//        for (String fila : registro) {
//            String[] campos = fila.split("#");
        for (int i = 0; i < 2; i++) {
            if (tip.equals(registro[i])) {
                return i;
            }
        }
//        }
        return 0;
    }

    public String getTotalViaticos() {
        double valor = traerValor();
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(valor);
    }

    public double traerValor() {
        double valor = 0;
        for (Viaticosempleado d : listaEmpleados) {
            valor += d.getValor().doubleValue();
        }
        return valor;
    }

    public String confirmacion() {
        if (listaEmpleados.isEmpty()) {
            MensajesErrores.advertencia("Indique al menos un empleado para el viaje");
            return null;
        }
        traerAsignacion(viatico.getPartida());
        detalle.setFecha(new Date());
        detalle.setAsignacion(asignacion);
        detalle.setValor(BigDecimal.valueOf(traerValor()));
        certificacionesBean.setAsignacion(asignacion);
        certificacionesBean.setDetalle(detalle);
        //certificacionesBean.setDetalles(new LinkedList<>());

        if (detalle.getId() == null) {
            certificacionesBean.insertarDetalle();
        } else {
            certificacionesBean.grabarDetalle();
        }
        if (!listaEmpleadosB.isEmpty()) {
            for (Viaticosempleado ve : listaEmpleadosB) {
                try {
                    ejbViaticosEmpleados.remove(ve, seguridadbean.getLogueado().getUserid());
                } catch (BorrarException ex) {
                    MensajesErrores.advertencia(ex.getMessage());
                    Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        formularioConfirmacion.insertar();

        return null;
    }

    public void traerAsignacion(String partida) {
        asignacion = new Asignaciones();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo and o.activo=true");
            parametros.put("codigo", partida);
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            Clasificadores cla = ejbClasificadores.encontarParametros(parametros).get(0);

            if (cla != null) {
                parametros = new HashMap();
                parametros.put(";where", "o.clasificador=:clasificador and o.proyecto.anio=:anio");
                parametros.put("clasificador", cla);
                parametros.put("anio", certificacionesBean.getCertificacion().getAnio());
                parametros.put(";inicial", 0);
                parametros.put(";final", 1);
                List<Asignaciones> aux;
                aux = ejbAsignaciones.encontarParametros(parametros);
                if (!aux.isEmpty()) {
                    asignacion = ejbAsignaciones.encontarParametros(parametros).get(0);
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void traerDetalleCertificacion(Certificaciones cert) {
        detalle = new Detallecertificaciones();
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.certificacion=:certificacion");
            parametros.put(";inicial", 0);
            parametros.put(";final", 1);
            parametros.put("certificacion", cert);
            List<Detallecertificaciones> lista = ejbDetalleCertificacion.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                detalle = lista.get(0);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Viaticos traer(Integer id) throws ConsultarException {
        return ejbViaticos.find(id);
    }

    public SelectItem[] getComboViaticos() {
        try {
            if (tipoPartida != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.partida=:partida and o.vigente=true and o.reembolso=false");
                parametros.put(";orden", "o.lugar");
                parametros.put("partida", consultaPartida(tipoPartida));
                return Combos.getSelectItems(ejbViaticos.encontarParametros(parametros), true);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboViaticosNoPagados() {
        try {
            if (tipoPartida != null) {
                List<Viaticos> listaCombo = new LinkedList<>();
                Map parametros = new HashMap();
                parametros.put(";where", "o.partida=:partida and o.vigente=true and o.reembolso=false");
                parametros.put(";orden", "o.lugar");
                parametros.put("partida", consultaPartida(tipoPartida));
                List<Viaticos> lista = ejbViaticos.encontarParametros(parametros);
                if (!lista.isEmpty()) {
                    for (Viaticos v : lista) {
                        parametros = new HashMap();
                        parametros.put(";where", "o.viatico=:viatico and o.kardex is null");
                        parametros.put("viatico", v);
                        List<Viaticosempleado> listaViaticoEmpleado = ejbViaticosEmpleados.encontarParametros(parametros);
                        if (!listaViaticoEmpleado.isEmpty()) {
                            Viaticosempleado ve = listaViaticoEmpleado.get(0);
                            listaCombo.add(ve.getViatico());
                        }
                    }
                }
                return Combos.getSelectItems(listaCombo, true);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEmpleadosViaticos() {
        try {
            if (viatico != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.viatico=:viatico and o.empleado.entidad.activo=true and o.reembolso=false");
                parametros.put("viatico", viatico);
                return Combos.getSelectItems(ejbViaticosEmpleados.encontarParametros(parametros), true);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEmpleadosViaticos(Integer estado) {
        try {
            if (viatico != null) {
                Map parametros = new HashMap();
                parametros.put(";where", "o.viatico=:viatico and o.empleado.entidad.activo=true and o.estado=:estado and o.viatico.reembolso=false");
                parametros.put("viatico", viatico);
                parametros.put("estado", estado);
                return Combos.getSelectItems(ejbViaticosEmpleados.encontarParametros(parametros), true);
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ViaticosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String ponerVigencia(Viaticos v) {
        this.viatico = v;
        viatico.setVigente(!v.getVigente());
        try {
            ejbViaticos.edit(viatico, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    //KARDEX
    public String nuevoKardex(Viaticosempleado emp) {
        kardex = new Kardexbanco();
        kardex.setFechaabono(new Date());
        kardex.setFechagraba(new Date());
        kardex.setFechamov(new Date());
        //kardex.setCodigospi("40101");
        kardex.setBancotransferencia(pagosBean.traerBanco(emp.getEmpleado()));
        kardex.setCuentatrans(pagosBean.traer(emp.getEmpleado(), "NUMCUENTA"));
//        kardex.setCuentasaldo(traer(emp.getEmpleado(), "NUMCUENTA"));
        kardex.setBeneficiario(emp.getEmpleado().getEntidad().toString());
        kardex.setUsuariograba(seguridadbean.getLogueado());
        return null;
    }

    public boolean validarKardex() {
        for (Viaticosempleado ve : listaEmpleados) {
            Codigos codigoBanco = pagosBean.traerBanco(ve.getEmpleado());
            if (codigoBanco == null) {
                MensajesErrores.advertencia("No existe banco para empleado : " + ve.getEmpleado().toString());
                return true;
            }

            if (pagosBean.traer(ve.getEmpleado(), "NUMCUENTA") == null) {
                MensajesErrores.advertencia("No existe No cuenta para empleado : " + ve.getEmpleado().toString());
                return true;
            }

            String tipoCta = pagosBean.traerTipoCuenta(ve.getEmpleado());
            if (tipoCta == null) {
                MensajesErrores.advertencia("No existe Tipo de cuentapara empleado : " + ve.getEmpleado().toString());
                return true;
            }
        }
        return false;
    }

    public String grabarKardex() {
        try {
            if (kardex.getId() == null) {

                ejbKardex.create(kardex, seguridadbean.getLogueado().getUserid());

            } else {
                ejbKardex.edit(kardex, seguridadbean.getLogueado().getUserid());
            }
        } catch (InsertarException | GrabarException ex) {
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String enviarNotificacion(Viaticosempleado ve) {

        DecimalFormat df = new DecimalFormat("###,##0.00");
        Codigos textoCodigo = codigosBean.traerCodigo("MAIL", "NOTIFICACIONVIATICO");
        if (textoCodigo == null) {
            MensajesErrores.fatal("No configurado texto para email en codigos");
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String texto = textoCodigo.getParametros().replace("#empleado#", empleado.getEmpleado().toString());
            String correos = ve.getEmpleado().getEntidad().getEmail();
            if (ve.getEmpleado().getEntidad().getEmail().isEmpty()) {
                MensajesErrores.fatal("Correo de empleado no ingresado");
                return null;
            }
            if (textoCodigo == null) {
                MensajesErrores.fatal("No configurado texto para email en codigos");
                return null;
            }
            texto = texto.replace("#empleado#", ve.getEmpleado().toString());
            if (tipoPartida == 1) {
                texto = texto.replace("#pais#", ve.getViatico().getPais().getNombre());
            } else {
                texto = texto.replace("#pais#", "");
            }
            texto = texto.replace("#destino#", ve.getViatico().toString());

            if (correos != null) {
                ejbCorreos.enviarCorreo(correos, textoCodigo.getDescripcion(), texto);
                MensajesErrores.informacion("Se Ha Enviado un E-Mail a:" + correos);
            }
        } catch (MessagingException | UnsupportedEncodingException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String imprimir() {
        try {
            setViatico(null);
            if (viatico == null) {
                viatico = (Viaticos) listadoViaticos.getRowData();
                Map parametros = new HashMap();
                parametros.put(";where", "o.viatico=:viatico");
                parametros.put("viatico", viatico);
                try {
                    listaEmpleados = ejbViaticosEmpleados.encontarParametros(parametros);
                } catch (ConsultarException ex) {
                    Logger.getLogger(ViaticosBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            int tipop;
            if (viatico.getPais() == null) {
                tipop = 2;
            } else {
                tipop = 1;
            }
            DocumentoPDF pdf = new DocumentoPDF("EMPRESA PUBLICA METROPOLITANA DE LOGISTICA PARA LA SEGURIDAD", null, seguridadbean.getLogueado().getUserid());
//            pdf.agregaParrafoCompleto("REGLAMENTO DE VIATICOS POR COMISIÓN DE SERVICIOS EN EL INTERIOR DEL PAÍS RESOLUCIÓN "
//                    + "N° MRL-2013-0097 / REGISTRO OFICIAL AÑO II- N° 326 VIATICOS Y SUBSISTENCIAS AL INTERIOR DEL PAÍS", AuxiliarReporte.ALIGN_CENTER, 6, true);

            Codigos codigosEI = codigosBean.traerCodigo("TEXTVIA", "01");
            if (codigosEI == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            if (codigosEI.getParametros() == null) {
                MensajesErrores.advertencia("No existe configuración de texto");
                return null;
            }
            Codigos codigosEE = codigosBean.traerCodigo("TEXTVIA", "07");
            if (codigosEE == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            if (codigosEE.getParametros() == null) {
                MensajesErrores.advertencia("No existe configuración de texto");
                return null;
            }

            if (tipop == 1) {
                pdf.agregaParrafoCompleto(codigosEE.getParametros(), AuxiliarReporte.ALIGN_CENTER, 6, true);
            } else {
                pdf.agregaParrafoCompleto(codigosEI.getParametros(), AuxiliarReporte.ALIGN_CENTER, 6, true);
            }
            pdf.agregaParrafo("\n\n");
            Codigos codigosC = codigosBean.traerCodigo("TEXTVIA", "02");
            if (codigosC == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            if (codigosC.getParametros() == null) {
                MensajesErrores.advertencia("No existe configuración de texto");
                return null;
            }
            Codigos codigosI = codigosBean.traerCodigo("TEXTVIA", "06");
            if (codigosI == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            if (codigosI.getParametros() == null) {
                MensajesErrores.advertencia("No existe configuración de texto");
                return null;
            }
            pdf.agregaParrafoCompleto(viatico.getMotivo(), AuxiliarReporte.ALIGN_CENTER, 6, false);
            pdf.agregaParrafo("\n");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (tipop == 1) {
                pdf.agregaParrafoCompleto("PAIS: " + viatico.getPais().getNombre(), AuxiliarReporte.ALIGN_LEFT, 6, false);
            }
            pdf.agregaParrafoCompleto("LUGAR DE COMISIÓN: " + viatico.getLugar(), AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafoCompleto("FECHA DE SALIDA: " + sdf.format(viatico.getDesde()), AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafoCompleto("FECHA DE LLEGADA: " + sdf.format(viatico.getHasta()), AuxiliarReporte.ALIGN_LEFT, 6, false);
            pdf.agregaParrafo("\n\n");
            List<AuxiliarReporte> columnas = new LinkedList<>();
            List<AuxiliarReporte> titulos = new LinkedList<>();
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "CÉDULA"));
            titulos.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 6, true, "NOMBRE DE SERVIDORES QUE INTEGRA LA COMISIÓN"));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "CARGO"));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "REMUNERACIÓN"));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "NIVEL"));
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "VIATICO DIARIO"));
            titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "N° DIAS"));
            if (tipop == 1) {
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "COEFICIENTE"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "N° SUBSISTENCIAS"));
                titulos.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, true, "VALOR SUBSISTENCIAS"));
            }
            titulos.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, true, "TOTAL VIATICOS"));
            double valorTotal = 0;
            DecimalFormat df = new DecimalFormat("###,##0.00");
            double dias = 0;
            for (Viaticosempleado p : listaEmpleados) {
                dias = consultaDias(p.getDesde(), p.getHasta());
                BigDecimal codPais = new BigDecimal(0);
                double salario = p.getEmpleado().getCargoactual().getCargo().getEscalasalarial().getSueldobase().doubleValue();
                BigDecimal sueldo = new BigDecimal(salario);
                if (tipop == 1) {
                    BigDecimal precio = BigDecimal.valueOf(Double.parseDouble(p.getTipo().getParametros()));
                    BigDecimal d = BigDecimal.valueOf(consultaDias(p.getDesde(), p.getHasta()));
                    codPais = BigDecimal.valueOf(Double.parseDouble(p.getViatico().getPais().getParametros()));
                    BigDecimal total = precio.multiply(codPais);
                    p.setValorParcial(total.doubleValue());
                }
                BigDecimal parcial = new BigDecimal(p.getValorParcial());
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado() != null ? p.getEmpleado().getEntidad().getPin() : ""));
                columnas.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado() != null ? p.getEmpleado().toString() : ""));
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getEmpleado() != null ? p.getEmpleado().getCargoactual().getCargo().getNombre() : ""));
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, sueldo != null ? df.format(sueldo) : ""));
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getTipo() != null ? p.getTipo().getNombre() : ""));
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_RIGHT, 6, false, parcial != null ? df.format(parcial) : ""));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_RIGHT, 6, false, dias + ""));
                if (tipop == 1) {
                    columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, codPais != null ? df.format(codPais) : ""));
                    columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getNrosubsistencias() != null ? df.format(p.getNrosubsistencias()) : ""));
                    columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, p.getSubsistencia() != null ? df.format(p.getSubsistencia()) : ""));
                }
                columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_RIGHT, 6, false, p.getValor() != null ? df.format(p.getValor()) : ""));
                valorTotal += p.getValor().doubleValue();
            }
            if (tipop == 1) {
                pdf.agregarTablaReporte(titulos, columnas, 11, 100, null);
            } else {
                pdf.agregarTablaReporte(titulos, columnas, 8, 100, null);
            }

            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, "SUMAN"));
            columnas.add(new AuxiliarReporte("String", 10, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            if (tipop == 1) {
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
                columnas.add(new AuxiliarReporte("String", 3, AuxiliarReporte.ALIGN_LEFT, 6, false, ""));
            }
            columnas.add(new AuxiliarReporte("String", 6, AuxiliarReporte.ALIGN_RIGHT, 6, false, df.format(valorTotal)));
            if (tipop == 1) {
                pdf.agregarTabla(null, columnas, 11, 100, null);
            } else {
                pdf.agregarTabla(null, columnas, 8, 100, null);
            }

            pdf.agregaParrafo("\n\n");
            if (tipop == 1) {
                pdf.agregaParrafoCompleto(codigosC.getParametros(), AuxiliarReporte.ALIGN_LEFT, 6, false);
            } else {
                pdf.agregaParrafoCompleto(codigosI.getParametros(), AuxiliarReporte.ALIGN_LEFT, 6, false);
            }
            pdf.agregaParrafo("\n\n\n\n");
            Codigos codigosF1 = codigosBean.traerCodigo("TEXTVIA", "03");
            Codigos codigosF2 = codigosBean.traerCodigo("TEXTVIA", "04");
            Codigos codigosF3 = codigosBean.traerCodigo("TEXTVIA", "05");
            if (codigosF1 == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            if (codigosF2 == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            if (codigosF3 == null) {
                MensajesErrores.advertencia("No existe configuración de código");
                return null;
            }
            columnas = new LinkedList<>();
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "f:_________________________________"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Elaborado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Revisado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, "Aprobado por:"));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigosF1.getDescripcion() != null ? codigosF1.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigosF2.getDescripcion() != null ? codigosF2.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigosF3.getDescripcion() != null ? codigosF3.getDescripcion() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigosF1.getParametros() != null ? codigosF1.getParametros() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigosF2.getParametros() != null ? codigosF2.getParametros() : ""));
            columnas.add(new AuxiliarReporte("String", 1, AuxiliarReporte.ALIGN_LEFT, 6, false, codigosF3.getParametros() != null ? codigosF3.getParametros() : ""));
            pdf.agregarTabla(null, columnas, 3, 100, null);
            reportepdf = pdf.traerRecurso();
        } catch (IOException | DocumentException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(PagosVencimientosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formularioImprimir.insertar();
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
     * @return the viatico
     */
    public Viaticos getViatico() {
        return viatico;
    }

    /**
     * @param viatico the viatico to set
     */
    public void setViatico(Viaticos viatico) {
        this.viatico = viatico;
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
     * @return the formularioEmpleados
     */
    public Formulario getFormularioEmpleados() {
        return formularioEmpleados;
    }

    /**
     * @param formularioEmpleados the formularioEmpleados to set
     */
    public void setFormularioEmpleados(Formulario formularioEmpleados) {
        this.formularioEmpleados = formularioEmpleados;
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
     * @return the listaEmpleadosB
     */
    public List<Viaticosempleado> getListaEmpleadosB() {
        return listaEmpleadosB;
    }

    /**
     * @param listaEmpleadosB the listaEmpleadosB to set
     */
    public void setListaEmpleadosB(List<Viaticosempleado> listaEmpleadosB) {
        this.listaEmpleadosB = listaEmpleadosB;
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
     * @return the certificacionesBean
     */
    public CertificacionesBean getCertificacionesBean() {
        return certificacionesBean;
    }

    /**
     * @param certificacionesBean the certificacionesBean to set
     */
    public void setCertificacionesBean(CertificacionesBean certificacionesBean) {
        this.certificacionesBean = certificacionesBean;
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

    /**
     * @return the pagosBean
     */
    public PagoPrestamosBean getPagosBean() {
        return pagosBean;
    }

    /**
     * @param pagosBean the pagosBean to set
     */
    public void setPagosBean(PagoPrestamosBean pagosBean) {
        this.pagosBean = pagosBean;
    }

    /**
     * @return the kardex
     */
    public Kardexbanco getKardex() {
        return kardex;
    }

    /**
     * @param kardex the kardex to set
     */
    public void setKardex(Kardexbanco kardex) {
        this.kardex = kardex;
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
     * @return the fechaCertificacion
     */
    public Date getFechaCertificacion() {
        return fechaCertificacion;
    }

    /**
     * @param fechaCertificacion the fechaCertificacion to set
     */
    public void setFechaCertificacion(Date fechaCertificacion) {
        this.fechaCertificacion = fechaCertificacion;
    }

    /**
     * @return the formularioEditarValor
     */
    public Formulario getFormularioEditarValor() {
        return formularioEditarValor;
    }

    /**
     * @param formularioEditarValor the formularioEditarValor to set
     */
    public void setFormularioEditarValor(Formulario formularioEditarValor) {
        this.formularioEditarValor = formularioEditarValor;
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
     * @return the lugar
     */
    public String getLugar() {
        return lugar;
    }

    /**
     * @param lugar the lugar to set
     */
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}
