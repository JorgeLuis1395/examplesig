/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pacpoa.sfccbdmq;

import org.beans.sfccbdmq.AsignacionespoaFacade;
import org.beans.sfccbdmq.DetallecertificacionespoaFacade;
import org.beans.sfccbdmq.ProyectospoaFacade;
import org.beans.sfccbdmq.ReformaspoaFacade;
import org.beans.sfccbdmq.TrackingspoaFacade;
import org.entidades.sfccbdmq.Asignacionespoa;
import org.entidades.sfccbdmq.Partidaspoa;
import org.entidades.sfccbdmq.Proyectospoa;
import org.entidades.sfccbdmq.Trackingspoa;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.utilitarios.sfccbdmq.Recurso;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.model.table.LazyDataModel;
import org.icefaces.ace.model.table.SortCriteria;
import org.seguridad.sfccbdmq.CodigosBean;

/**
 *
 * @author luis
 */
@ManagedBean(name = "asignacionesPoa")
@ViewScoped
public class AsignacionesPoaBean {

    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{partidasPoa}")
    private PartidasPoaBean partidasPoaBean;
    @ManagedProperty(value = "#{proyectosPoa}")
    private ProyectosPoaBean proyectosPoaBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    @ManagedProperty(value = "#{consultasPoa}")
    private ConsultasPoaBean consultasPoaBean;

    private Asignacionespoa asignacion;
    private String fuente;
    private LazyDataModel<Asignacionespoa> asignaciones;
    private List<Asignacionespoa> asignacionesLista;
    private Formulario formulario = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Formulario formularioImpresion = new Formulario();
    private Formulario formularioCertificacion = new Formulario();
    private String separador = ";";
    private String claveBuscar;
    private String codigoProducto;
    private String nombreProducto;
    private String nombrePartida;
    private String codigoPartida;
    private Recurso reportepdf;
    private DocumentoPDF pdf;

    @EJB
    private AsignacionespoaFacade ejbAsignacionespoa;
    @EJB
    private DetallecertificacionespoaFacade ejbCertificacionespoa;
    @EJB
    private ReformaspoaFacade ejbReformaspoa;
    @EJB
    private TrackingspoaFacade ejbTrackingspoa;
    @EJB
    private ProyectospoaFacade ejbProyectospoa;

    public AsignacionesPoaBean() {
        asignaciones = new LazyDataModel<Asignacionespoa>() {

            @Override
            public List<Asignacionespoa> load(int i, int i1, SortCriteria[] scs, Map<String, String> map) {
                return null;
            }
        };
    }

    public String buscar() {
        asignaciones = new LazyDataModel<Asignacionespoa>() {

            @Override
            public List<Asignacionespoa> load(int i, int pageSize, SortCriteria[] scs, Map<String, String> map) {
                Map parametros = new HashMap();
                if (scs.length == 0) {
                    parametros.put(";orden", "o.proyecto.direccion, o.proyecto.codigo,o.partida.codigo");
                } else {
                    parametros.put(";orden", "o." + scs[0].getPropertyName()
                            + (scs[0].isAscending() ? " ASC" : " DESC"));
                }
                String where = "  o.proyecto.anio=:anio  and o.activo=true";
                parametros.put("anio", getProyectosPoaBean().getAnio());

                for (Map.Entry e : map.entrySet()) {
                    String clave = (String) e.getKey();

                    String valor = (String) e.getValue();
                    if (valor.toUpperCase().equals("SI") || valor.toUpperCase().equals("NO")) {
                        where += " and o." + clave + " =" + (valor.toUpperCase().equals("SI") ? "true" : "false");
                    } else if (valor.toUpperCase().equals("--")) {
                        where += " and o." + clave + " is not null";

                    } else {
                        where += " and upper(o." + clave + ") like :" + clave;
                        parametros.put(clave, valor.toUpperCase() + "%");
                    }
                }

                if (!(proyectosPoaBean.getDireccion() == null || proyectosPoaBean.getDireccion().isEmpty())) {
                    where += " and upper(o.proyecto.direccion) =:direccion";
                    parametros.put("direccion", getProyectosPoaBean().getDireccion().toUpperCase());
                }
                if (!((codigoProducto == null) || (codigoProducto.isEmpty()))) {
                    where += " and upper(o.proyecto.codigo) like :codigoProyecto";
                    parametros.put("codigoProyecto", codigoProducto.toUpperCase() + "%");
                }
                if (!((nombreProducto == null) || (nombreProducto.isEmpty()))) {
                    where += " and upper(o.proyecto.nombre) like :nombreProyecto";
                    parametros.put("nombreProyecto", nombreProducto.toUpperCase() + "%");
                }

//                if (!(proyectosPoaBean.getProyectoSeleccionado() == null)) {
//                    if (proyectosPoaBean.getTipoBuscar() == 1) {
//                        where += " and o.proyecto.codigo like :proyecto";
//                        parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado().getCodigo() + "%");
//                    } else {
//                        where += " and o.proyecto.nombre like :proyecto";
//                        parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado().getNombre() + "%");
//                    }
//                }
//                if (!(partidasPoaBean.getPartidaPoa() == null)) {
//                    if (partidasPoaBean.getTipoBuscar() == 1) {
//                        where += " and o.partida.codigo like :partida";
//                        parametros.put("partida", partidasPoaBean.getPartidaPoa().getCodigo() + "%");
//                    } else {
//                        where += " and o.partida.nombre like :partida";
//                        parametros.put("partida", partidasPoaBean.getPartidaPoa().getNombre() + "%");
//                    }
//                }
                if (!((codigoPartida == null) || (codigoPartida.isEmpty()))) {
                    where += " and upper(o.partida.codigo) like :partida";
                    parametros.put("partida", codigoPartida.toUpperCase() + "%");
                }
                if (!((nombrePartida == null) || (nombrePartida.isEmpty()))) {
                    where += " and upper(o.partida.nombre) like :partidaNombre";
                    parametros.put("partidaNombre", nombrePartida.toUpperCase() + "%");
                }
                int total = 0;

                try {
                    parametros.put(";where", where);
                    total = ejbAsignacionespoa.contar(parametros);
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
                getAsignaciones().setRowCount(total);
                try {
                    return ejbAsignacionespoa.encontarParametros(parametros);
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
//        if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
//            MensajesErrores.advertencia("Seleccione un producto primero");
//            return null;
//        }
//        partidasPoaBean.setIngreso(getProyectosPoaBean().getProyectoSeleccionado().getIngreso());
//        partidasPoaBean.setCodigo("");
//        partidasPoaBean.setPartidaPoa(null);
        // contar los cerradoas a ver sistena
//        Map parametros = new HashMap();
//        parametros.put(";where", "o.proyecto.anio=:anio and o.cerrado=true");
//        parametros.put("anio", getProyectosPoaBean().getProyectoSeleccionado().getAnio());
//        try {
//            int total = ejbAsignacionespoa.contar(parametros);
//            if (total > 0) {
//                MensajesErrores.advertencia("No se puede crear más cuentas año ya cerrado para asignaciones");
//                return null;
//            }
//        } catch (ConsultarException ex) {
//            MensajesErrores.fatal(ex.getMessage());
//            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        formularioClasificador.insertar();
        asignacion = new Asignacionespoa();
//        asignacion.setProyecto(getProyectosPoaBean().getProyectoSeleccionado());
        asignacion.setCerrado(Boolean.FALSE);
//        asignacion.setDireccion(direccion);
        asignacion.setActivo(true);
        formulario.insertar();
        return null;
    }

    public String modificar(Asignacionespoa asignacion) {
        try {
            formularioClasificador.cancelar();
            this.asignacion = asignacion;
            Map parametros = new HashMap();
//            asignacion.setCerrado(Boolean.FALSE);
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificacionespoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            total = ejbReformaspoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            proyectosPoaBean.setProyectoSeleccionado(asignacion.getProyecto());
            partidasPoaBean.setPartidaPoa(asignacion.getPartida());
//            partidasPoaBean.setCodigo(asignacion.getPartida().getCodigo());
            formulario.editar();
//            clasificadorBean.formulario.cancelar();
        } catch (ConsultarException ex) {
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cerrar() {
        try {
            if (getProyectosPoaBean().getAnio() == null || getProyectosPoaBean().getAnio() <= 0) {
                MensajesErrores.advertencia("Coloque el año de cierre");
                return null;
            }
            // ver si esta cuadrado
            Map parametros = new HashMap();
            //asignacion.setCerrado(Boolean.FALSE);
            parametros.put(";where", "o.proyecto.anio=:anio and o.partida.ingreso=true");
            parametros.put("anio", getProyectosPoaBean().getAnio());
            parametros.put(";campo", "o.valor");
            double ingresos = ejbAsignacionespoa.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
            //asignacion.setCerrado(Boolean.FALSE);
            parametros.put(";where", "o.proyecto.anio=:anio and o.partida.ingreso=false");
            parametros.put("anio", getProyectosPoaBean().getAnio());
            parametros.put(";campo", "o.valor");
            double egresos = ejbAsignacionespoa.sumarCampo(parametros).doubleValue();
            double total = ingresos - egresos;
            if (Math.round(total) != 0) {
                MensajesErrores.advertencia("Asignación inicial descuadra no es posible cerrar");
                return null;
            }
            ejbAsignacionespoa.cerrar(getProyectosPoaBean().getAnio());
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Año se cerró correctamente");
        return null;
    }

    public String eliminar(Asignacionespoa asignacion) {
        try {
            this.asignacion = asignacion;
            formularioClasificador.cancelar();
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificacionespoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            total = ejbReformaspoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            formulario.eliminar();
//            clasificadorBean.formulario.cancelar();
        } catch (ConsultarException ex) {
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String insertar() {
        if (getProyectosPoaBean().getProyectoSeleccionado() == null) {
            MensajesErrores.advertencia("Seleccione un producto primero");
            return null;
        }
        partidasPoaBean.setIngreso(getProyectosPoaBean().getProyectoSeleccionado().getIngreso());
        partidasPoaBean.setCodigo("");
//        partidasPoaBean.setPartidaPoa(null);
        asignacion.setPartida(partidasPoaBean.getPartidaPoa());
        asignacion.setProyecto(getProyectosPoaBean().getProyectoSeleccionado());
        if (asignacion.getPartida() == null) {
            MensajesErrores.advertencia("Seleccione una partida");
            return null;
        }

        if (asignacion.getValor() == null || asignacion.getValor().doubleValue() < 0) {
            MensajesErrores.advertencia("Valor debe ser mayor o igual a cero");
            return null;
        }
        try {
            // buscar si existe la partida en ese proyecto
            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.proyecto=:proyecto and o.fuente=:fuente");
            parametros.put("partida", asignacion.getPartida());
            parametros.put("proyecto", asignacion.getProyecto());
            parametros.put("fuente", asignacion.getFuente());
            int total = ejbAsignacionespoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("ya existe la partida: " + asignacion.getPartida().toString() + " en este proyecto");
                return null;
            }
            ejbAsignacionespoa.create(asignacion, seguridadbean.getLogueado().getUserid());

            try {
                ejbProyectospoa.edit(asignacion.getProyecto(), seguridadbean.getLogueado().getUserid());
            } catch (GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setAsignacion(asignacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Asignación creada: \n"
                    + "Asignación :" + asignacion.getProyecto().getDetalle() + " \n"
                    + "Partida :" + asignacion.getPartida().toString() + " \n"
                    + "Producto :" + asignacion.getProyecto().toString() + " \n"
                    + "Valor :" + asignacion.getValor());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioClasificador.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        try {
            asignacion.setPartida(partidasPoaBean.getPartidaPoa());
            asignacion.setProyecto(getProyectosPoaBean().getProyectoSeleccionado());
            if (asignacion.getPartida() == null) {
                MensajesErrores.advertencia("Seleccione una partida");
                return null;
            }
            if (asignacion.getProyecto() == null) {
                MensajesErrores.advertencia("Seleccione un proyecto");
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificacionespoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            total = ejbReformaspoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.proyecto=:proyecto and o.fuente=:fuente and o.id!=:id");
            parametros.put("partida", asignacion.getPartida());
            parametros.put("proyecto", asignacion.getProyecto());
            parametros.put("fuente", asignacion.getFuente());
            parametros.put("id", asignacion.getId());
            total = ejbAsignacionespoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("ya existe la partida: " + asignacion.getPartida().toString() + " en este proyecto");
                return null;
            }
            ejbAsignacionespoa.edit(asignacion, seguridadbean.getLogueado().getUserid());
            try {
                ejbProyectospoa.edit(asignacion.getProyecto(), seguridadbean.getLogueado().getUserid());
            } catch (GrabarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setAsignacion(asignacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Asignación modificada: \n"
                    + "Asignación :" + asignacion.getProyecto().getDetalle() + " \n"
                    + "Partida :" + asignacion.getPartida().toString() + " \n"
                    + "Producto :" + asignacion.getProyecto().toString() + " \n"
                    + "Valor :" + asignacion.getValor());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException | ConsultarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificacionespoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            total = ejbReformaspoa.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            asignacion.setActivo(Boolean.FALSE);
            ejbAsignacionespoa.edit(asignacion, seguridadbean.getLogueado().getUserid());

            Trackingspoa tracking = new Trackingspoa();
            tracking.setReformanombre(false);
            tracking.setFecha(new Date());
            tracking.setAsignacion(asignacion);
            tracking.setResponsable(seguridadbean.getLogueado().getApellidos() + " " + seguridadbean.getLogueado().getNombres());
            tracking.setObservaciones(""
                    + "Asignación borrada: \n"
                    + "Asignación :" + asignacion.getProyecto().getDetalle() + " \n"
                    + "Partida :" + asignacion.getPartida().toString() + " \n"
                    + "Producto :" + asignacion.getProyecto().toString() + " \n"
                    + "Valor :" + asignacion.getValor());
            ejbTrackingspoa.create(tracking, seguridadbean.getLogueado().getUserid());
        } catch (ConsultarException | InsertarException | GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
        return null;
    }

    public Asignacionespoa traer(Integer id) throws ConsultarException {
        return ejbAsignacionespoa.find(id);
    }

    public SelectItem[] getComboAsignacionespoa() {
        if (proyectosPoaBean.getProyectoSeleccionado() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto=:proyecto");
        parametros.put("proyecto", getProyectosPoaBean().getProyectoSeleccionado());
        parametros.put(";orden", "o.partida.codigo,o.fuente");
        try {
            return Combos.getSelectItems(ejbAsignacionespoa.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboAsignacionespoaEgresos() {

        if (getProyectosPoaBean().getPrograma() == null) {
            // selecciona proyecto
            return null;
        }
        String codigo = getProyectosPoaBean().getPrograma().getCodigo();
        int anioParametro = 0;
        if (getProyectosPoaBean().getProyecto() != null) {
            // selecciona proyecto
            codigo = getProyectosPoaBean().getProyecto().getCodigo();
            anioParametro = getProyectosPoaBean().getProyecto().getAnio();
        }
        if (getProyectosPoaBean().getProyectoSeleccionado() != null) {
            codigo = getProyectosPoaBean().getProyectoSeleccionado().getCodigo();
            anioParametro = getProyectosPoaBean().getProyectoSeleccionado().getAnio();
        }
        Map parametros = new HashMap();

        parametros.put("proyecto", codigo + "%");
        parametros.put("anio", anioParametro);
        if (fuente != null) {
            parametros.put("fuente", fuente);
            parametros.put(";where", "o.proyecto.codigo like :proyecto and o.proyecto.anio=:anio"
                    + "and o.fuente=:fuente and o.partida.ingreso=false");
        } else {
            parametros.put(";where", "o.proyecto.codigo like :proyecto  and o.proyecto.anio=:anio"
                    + "and o.partida.ingreso=false");
        }

        parametros.put(";orden", "o.proyecto.codigo,o.partida.codigo,o.fuente.nombre");
        try {
            return Combos.getSelectItems(ejbAsignacionespoa.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void codigoChangeEventHandler(TextChangeEvent event) {
        if (getProyectosPoaBean().getProyectoSeleccionado() != null) {
            MensajesErrores.advertencia("Seleccione un producto");
            return;
        }
        asignacion = null;
        String newWord = event.getNewValue() != null ? (String) event.getNewValue() : "";
        if ((newWord == null) || (newWord.isEmpty())) {
            asignacion = null;
        }
        // traer la consulta
        Map parametros = new HashMap();
        String codigo = getProyectosPoaBean().getProyectoSeleccionado().getCodigo();
        parametros.put("proyecto", codigo + "%");
        parametros.put("cuenta", newWord + "%");
        if (fuente != null) {
            parametros.put("fuente", fuente);
            parametros.put(";where", "o.proyecto.codigo like :proyecto and o.fuente=:fuente and o.partida.ingreso=false and o.partida.codigo like :cuenta");
        } else {
            parametros.put(";where", "o.proyecto.codigo like :proyecto  and o.partida.ingreso=false o.partida.codigo like :cuenta");
        }
        int total = 15;
        // Contadores
        asignacionesLista = null;
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            asignacionesLista = ejbAsignacionespoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }
    }

    public void cambiaCodigo(ValueChangeEvent event) {
        asignacion = null;
        if (asignacionesLista == null) {
            return;
        }
        String newWord = (String) event.getNewValue();
        for (Asignacionespoa a : asignacionesLista) {
            String aComparar = a.toString();
            if (aComparar.compareToIgnoreCase(newWord) == 0) {
                asignacion = a;
            }
        }
    }

    public SelectItem[] getComboAsignacionespoaPartida() {
        if (partidasPoaBean.getPartidaPoa() == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.partida=:partida";
            parametros.put("partida", partidasPoaBean.getPartidaPoa());

            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.proyecto.codigo,o.fuente");

            return Combos.getSelectItems(ejbAsignacionespoa.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboClasificadorFuente() {
        if (fuente == null) {
            return null;
        }
        if (partidasPoaBean.getPartidaPoa() == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.partida=:partida and o.fuente=:fuente");
            parametros.put("partida", partidasPoaBean.getPartidaPoa());
            parametros.put("fuente", fuente);
            parametros.put(";orden", "o.proyecto.codigo,o.fuente");

            return Combos.getSelectItems(ejbAsignacionespoa.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Asignacionespoa> getListaAsignaciones() {
        if (partidasPoaBean.getPartidaPoa() == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "o.partida=:partida and o.proyecto.anio=:anio";
            parametros.put("partida", partidasPoaBean.getPartidaPoa());
            parametros.put("anio", proyectosPoaBean.getAnio());

            if (fuente != null) {
                where += " and o.fuente=:fuente";
                parametros.put("fuente", fuente);
            }
            if (proyectosPoaBean.getDireccion() != null) {
                where += " and o.proyecto.direccion=:direccion";
                parametros.put("direccion", proyectosPoaBean.getDireccion());
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.proyecto.codigo,o.fuente");

            return ejbAsignacionespoa.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void archivoListener(FileEntryEvent e) {
        FileEntry fe = (FileEntry) e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        Calendar cAnio = Calendar.getInstance();

        //get data About File
        for (FileEntryResults.FileInfo i : results.getFiles()) {

            if (i.isSaved()) {

                File file = i.getFile();
                if (file != null) {
                    try {
                        parent = file.getParentFile();

                        BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

                        String sb = entrada.readLine();
                        String[] cabecera = sb.split(separador);// lee los caracteres en el arreglo

                        while ((sb = entrada.readLine()) != null) {
                            Asignacionespoa asig = new Asignacionespoa();
                            String[] aux = sb.split(separador);// lee los caracteres en el arreglo
                            for (int j = 0; j < aux.length; j++) {
                                if (j < cabecera.length) {
                                    ubicar(asig, cabecera[j].toLowerCase(), aux[j].toUpperCase());
                                }
                            }
                            asig.setActivo(Boolean.TRUE);
                            asig.setCerrado(Boolean.FALSE);
                            if (!asig.getValor().equals(BigDecimal.ZERO)) {
                                Asignacionespoa existe = traerAsignacion(asig.getProyecto(), asig.getPartida(), asig.getFuente());
                                if (existe == null) {
                                    ejbAsignacionespoa.create(asig, seguridadbean.getLogueado().getUserid());
                                } else {
                                    existe.setValor(asig.getValor());
                                    ejbAsignacionespoa.edit(existe, seguridadbean.getLogueado().getUserid());
                                }
                            }
                        }
                        entrada.close();
                    } catch (UnsupportedEncodingException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException | InsertarException | GrabarException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
                        Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    MensajesErrores.fatal("Archivo no puede ser cargado: "
                            + i.getStatus().getFacesMessage(
                                    FacesContext.getCurrentInstance(),
                                    fe, i).getSummary());
                }
            }

        }
    }

    public Asignacionespoa traerAsignacion(Proyectospoa proyecto, Partidaspoa partida, String fuente) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.proyecto=:proyecto and o.partida=:partida and o.fuente=:fuente");
            parametros.put("proyecto", proyecto);
            parametros.put("partida", partida);
            parametros.put("fuente", fuente);
            List<Asignacionespoa> lista;

            lista = ejbAsignacionespoa.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Asignacionespoa traerAsignacion(String proyecto, Partidaspoa partida, String fuente) {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.proyecto.codigo=:proyecto and o.partida=:partida and o.fuente=:fuente");
            parametros.put("proyecto", proyecto);
            parametros.put("partida", partida);
            parametros.put("fuente", fuente);
            List<Asignacionespoa> lista;

            lista = ejbAsignacionespoa.encontarParametros(parametros);
            if (!lista.isEmpty()) {
                return lista.get(0);
            }

        } catch (ConsultarException ex) {
            MensajesErrores.advertencia(ex.getMessage() + " - " + ex.getCause());
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void ubicar(Asignacionespoa am, String titulo, String valor) {
        if (titulo.contains("proyecto")) {
            am.setProyecto(proyectosPoaBean.traerCodigo(valor));
            if (am.getProyecto() == null) {
                MensajesErrores.advertencia("Proyecto no encontrado " + valor);
                System.out.println("Proyecto no encontrado " + valor);
            }
        } else if (titulo.contains("partida")) {

            if (partidasPoaBean.traerCodigo(valor) == null) {
                MensajesErrores.advertencia("Partida no encontrada " + valor);
                System.out.println("Partida no encontrado " + valor);
            } else {
                am.setPartida(partidasPoaBean.traerCodigo(valor));
            }
        } else if (titulo.contains("valor")) {
            am.setValor(new BigDecimal(valor));
        } else if (titulo.contains("fuente")) {
            am.setFuente(valor);

        }
    }

    public boolean habilitarSubida() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.cerrado=true and o.proyecto.anio=:anio");
        parametros.put("anio", getProyectosPoaBean().getAnio());
        try {
            if (ejbAsignacionespoa.contar(parametros) > 0) {
                return false;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(AsignacionesPoaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * @return the partidasPoaBean
     */
    public PartidasPoaBean getPartidasPoaBean() {
        return partidasPoaBean;
    }

    /**
     * @param partidasPoaBean the partidasPoaBean to set
     */
    public void setPartidasPoaBean(PartidasPoaBean partidasPoaBean) {
        this.partidasPoaBean = partidasPoaBean;
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
     * @param asignacion the asignacion to set
     */
    public void setAsignacion(Asignacionespoa asignacion) {
        this.asignacion = asignacion;
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
     * @return the asignaciones
     */
    public LazyDataModel<Asignacionespoa> getAsignacionespoa() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignacionespoa(LazyDataModel<Asignacionespoa> asignaciones) {
        this.asignaciones = asignaciones;
    }

    /**
     * @return the asignacionesLista
     */
    public List getAsignacionespoaLista() {
        return asignacionesLista;
    }

    /**
     * @param asignacionesLista the asignacionesLista to set
     */
    public void setAsignacionespoaLista(List asignacionesLista) {
        this.asignacionesLista = asignacionesLista;
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
     * @return the asignacion
     */
    public Asignacionespoa getAsignacion() {
        return asignacion;
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
     * @return the asignaciones
     */
    public LazyDataModel<Asignacionespoa> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(LazyDataModel<Asignacionespoa> asignaciones) {
        this.asignaciones = asignaciones;
    }

    /**
     * @return the asignacionesLista
     */
    public List<Asignacionespoa> getAsignacionesLista() {
        return asignacionesLista;
    }

    /**
     * @param asignacionesLista the asignacionesLista to set
     */
    public void setAsignacionesLista(List<Asignacionespoa> asignacionesLista) {
        this.asignacionesLista = asignacionesLista;
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
     * @return the claveBuscar
     */
    public String getClaveBuscar() {
        return claveBuscar;
    }

    /**
     * @param claveBuscar the claveBuscar to set
     */
    public void setClaveBuscar(String claveBuscar) {
        this.claveBuscar = claveBuscar;
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
     * @return the codigoProducto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * @param codigoProducto the codigoProducto to set
     */
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * @return the nombreProducto
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * @param nombreProducto the nombreProducto to set
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * @return the nombrePartida
     */
    public String getNombrePartida() {
        return nombrePartida;
    }

    /**
     * @param nombrePartida the nombrePartida to set
     */
    public void setNombrePartida(String nombrePartida) {
        this.nombrePartida = nombrePartida;
    }

    /**
     * @return the codigoPartida
     */
    public String getCodigoPartida() {
        return codigoPartida;
    }

    /**
     * @param codigoPartida the codigoPartida to set
     */
    public void setCodigoPartida(String codigoPartida) {
        this.codigoPartida = codigoPartida;
    }

//    public SelectItem[] getPartidasPoa(){
//        if (proyectosPoaBean.getProyectoSeleccionado()==null){
//            return null;
//        }
//        
//    }
}
