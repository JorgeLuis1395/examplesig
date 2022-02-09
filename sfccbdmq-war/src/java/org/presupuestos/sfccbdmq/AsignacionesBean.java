/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.presupuestos.sfccbdmq;


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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.AsignacionesFacade;
import org.beans.sfccbdmq.DetallecertificacionesFacade;
import org.beans.sfccbdmq.ReformasFacade;
import org.entidades.sfccbdmq.Asignaciones;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "asignacionesSfccbdmq")
@ViewScoped
public class AsignacionesBean {

    /**
     * Creates a new instance of AsignacionesBean
     */
    public AsignacionesBean() {
    }
//    private Proyectos proyectosBean.getProyectoSeleccionado();
    private Asignaciones asignacion;
    private Codigos fuente;
    private int anio;
    private List<Asignaciones> asignaciones;
    private List asignacionesLista;
    private Formulario formulario = new Formulario();
    private Formulario formularioClasificador = new Formulario();
    private Perfiles perfil;
    private String clasificador;
    private String codigoProducto;
    private String nombreProducto;
    private String nombrePartida;
    private String codigoPartida;
    @EJB
    private AsignacionesFacade ejbAsignaciones;
    @EJB
    private DetallecertificacionesFacade ejbCertificaciones;
    @EJB
    private ReformasFacade ejbReformas;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{clasificadorSfccbdmq}")
    private ClasificadorBean clasificadorBean;
    @ManagedProperty(value = "#{proyectosSfccbdmq}")
    private ProyectosBean proyectosBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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
     * @return the asignaciones
     */
    public List<Asignaciones> getAsignaciones() {
        return asignaciones;
    }

    /**
     * @param asignaciones the asignaciones to set
     */
    public void setAsignaciones(List<Asignaciones> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String buscar() {
        Map parametros = new HashMap();
        String where = "  o.id is not null and o.proyecto.anio=:anio";
        parametros.put("anio", proyectosBean.getAnio());
        parametros.put(";orden", "o.clasificador.codigo,o.fuente.nombre");
        if (!((codigoProducto == null) || (codigoProducto.isEmpty()))) {
            where += " and upper(o.proyecto.codigo) like :codigoProyecto";
            parametros.put("codigoProyecto", codigoProducto.toUpperCase() + "%");
        }
        if (!((nombreProducto == null) || (nombreProducto.isEmpty()))) {
            where += " and upper(o.proyecto.nombre) like :nombreProyecto";
            parametros.put("nombreProyecto", nombreProducto.toUpperCase() + "%");
        }
        if (!((codigoPartida == null) || (codigoPartida.isEmpty()))) {
            where += " and upper(o.clasificador.codigo) like :clasificador";
            parametros.put("clasificador", codigoPartida.toUpperCase() + "%");
        }
        if (!((nombrePartida == null) || (nombrePartida.isEmpty()))) {
            where += " and upper(o.clasificador.nombre) like :partidaNombre";
            parametros.put("partidaNombre", nombrePartida.toUpperCase() + "%");
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            where += " and o.proyecto=:proyecto";
            parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
        }
        if (!((clasificador == null) || (clasificador.isEmpty()))) {
            where += " and o.clasificador.codigo like :clasificador";
            parametros.put("clasificador", clasificador + "%");
        }
        parametros.put(";where", where);
        try {
            asignaciones = ejbAsignaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (proyectosBean.getProyectoSeleccionado() == null) {

            MensajesErrores.advertencia("Seleccione un producto primero");
            return null;
        }
        // contar los cerradoas a ver sistena
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto.anio=:anio and o.cerrado=true");
        parametros.put("anio", proyectosBean.getProyectoSeleccionado().getAnio());
        try {
            int total = ejbAsignaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No se puede crear más cuentas año ya cerrado para asignaciones");
                return null;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formularioClasificador.insertar();
        asignacion = new Asignaciones();
        asignacion.setProyecto(proyectosBean.getProyectoSeleccionado());
        asignacion.setCerrado(Boolean.FALSE);
        getFormulario().insertar();
        return null;
    }

    public String modificar(Asignaciones asignacion) {
        try {
            formularioClasificador.cancelar();
            this.asignacion = asignacion;
            Map parametros = new HashMap();
//            asignacion.setCerrado(Boolean.FALSE);
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            total = ejbReformas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            getFormulario().editar();
//            clasificadorBean.getFormulario().cancelar();
        } catch (ConsultarException ex) {
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String cerrar() {
        try {
            if (proyectosBean.getAnio() <= 0) {
                MensajesErrores.advertencia("Coloque el año de cierre");
                return null;
            }
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.proyecto.anio=:anio and o.cerrado=true");
//            parametros.put("anio", proyectosBean.getAnio());
//            try {
//                int total = ejbAsignaciones.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.advertencia("No se puede cerrar año ya cerrado");
//                    return null;
//                }
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
            // ver si esta cuadrado
            Map parametros = new HashMap();
//            asignacion.setCerrado(Boolean.FALSE);
            parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=true");
            parametros.put("anio", proyectosBean.getAnio());
            parametros.put(";campo", "o.valor");
            double ingresos = ejbAsignaciones.sumarCampo(parametros).doubleValue();
            parametros = new HashMap();
//            asignacion.setCerrado(Boolean.FALSE);
            parametros.put(";where", "o.proyecto.anio=:anio and o.clasificador.ingreso=false");
            parametros.put("anio", proyectosBean.getAnio());
            parametros.put(";campo", "o.valor");
            double egresos = ejbAsignaciones.sumarCampo(parametros).doubleValue();
            double total = ingresos - egresos;
            if (Math.round(total) != 0) {
                MensajesErrores.advertencia("Asignación inicial descuadra no es posible cerrar");
                return null;
            }
            ejbAsignaciones.cerrar(proyectosBean.getAnio());
//            clasificadorBean.getFormulario().cancelar();
        } catch (ConsultarException | InsertarException ex) {
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        MensajesErrores.informacion("Año se cerró correctamente");
        return null;
    }

    public String eliminar(Asignaciones asignacion) {
        try {
            this.asignacion = asignacion;
            formularioClasificador.cancelar();
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            total = ejbReformas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            getFormulario().eliminar();
//            clasificadorBean.getFormulario().cancelar();
        } catch (ConsultarException ex) {
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String insertar() {
        asignacion.setClasificador(clasificadorBean.getClasificador());
        if (asignacion.getClasificador() == null) {
            MensajesErrores.advertencia("Seleccione un clasificador");
            return null;
        }
        if (asignacion.getFuente() == null) {
            MensajesErrores.advertencia("Seleccione una fuente");
            return null;
        }
        if (asignacion.getValor().doubleValue() < 0) {
            MensajesErrores.advertencia("Valor debe ser mayor o igual a cero");
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            Map parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.fuente=:fuente and o.proyecto=:proyecto");
            parametros.put("clasificador", asignacion.getClasificador());
            parametros.put("fuente", asignacion.getFuente());
            parametros.put("proyecto", asignacion.getProyecto());
            int total = ejbAsignaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("ya existe" + proyectosBean.getProyectoSeleccionado() + "en este proyecto");
                return null;
            }
            ejbAsignaciones.create(asignacion, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        formularioClasificador.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.asignacion=:asignacion");
            parametros.put("asignacion", asignacion);
            int total = ejbCertificaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            total = ejbReformas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible modificar");
                return null;
            }
            //Editar Partida de la asiganacion
            asignacion.setClasificador(clasificadorBean.getClasificador());
            if (asignacion.getClasificador() == null) {
                MensajesErrores.advertencia("Seleccione un clasificador");
                return null;
            }
            if (asignacion.getFuente() == null) {
                MensajesErrores.advertencia("Seleccione una fuente");
                return null;
            }
            if (asignacion.getValor().doubleValue() < 0) {
                MensajesErrores.advertencia("Valor debe ser mayor o igual a cero");
                return null;
            }
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            parametros = new HashMap();
            parametros.put(";where", "o.clasificador=:clasificador and o.fuente=:fuente and o.proyecto=:proyecto");
            parametros.put("clasificador", asignacion.getClasificador());
            parametros.put("fuente", asignacion.getFuente());
            parametros.put("proyecto", asignacion.getProyecto());
            int totala = ejbAsignaciones.contar(parametros);
            if (totala > 0) {
                MensajesErrores.advertencia("ya existe" + proyectosBean.getProyectoSeleccionado() + "en este proyecto");
                return null;
            }
            ejbAsignaciones.edit(asignacion, seguridadbean.getLogueado().getUserid());
            //Fin edicion
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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
            int total = ejbCertificaciones.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen certificaciones ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            total = ejbReformas.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("Existen reformas ya realizadas para esta asignación, no es posible borrar");
                return null;
            }
            ejbAsignaciones.remove(asignacion, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        formularioClasificador.cancelar();
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

    public Asignaciones traer(Integer id) throws ConsultarException {
        return ejbAsignaciones.find(id);
    }

    public SelectItem[] getComboAsignaciones() {
        if (proyectosBean.getProyectoSeleccionado() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.proyecto=:proyecto and o.fuente=:fuente");
        parametros.put("proyecto", proyectosBean.getProyectoSeleccionado());
        parametros.put("fuente", fuente);
        parametros.put(";orden", "o.clasificador.codigo,o.fuente.nombre");
        try {
            asignaciones = ejbAsignaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(asignaciones, true);

    }

    public SelectItem[] getComboFuenteClasificador() {
        if (clasificadorBean.getClasificador() == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador=:clasificador  and o.proyecto.anio=:anio");
        parametros.put("clasificador", clasificadorBean.getClasificador());
        parametros.put("anio", getAnio());
        parametros.put(";orden", "o.fuente.nombre");
        try {
            List<Asignaciones> lAsignaciones = ejbAsignaciones.encontarParametros(parametros);
            List<Codigos> listaFuentes = new LinkedList<>();

            for (Asignaciones x : lAsignaciones) {
                esta(x.getFuente(), listaFuentes);
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
        if (fuente == null) {
            return null;
        }
        if (clasificadorBean.getClasificador() == null) {
            return null;
        }

        Map parametros = new HashMap();
        parametros.put(";where", "o.clasificador=:clasificador and o.fuente=:fuente and o.proyecto.anio=:anio");
        parametros.put("clasificador", clasificadorBean.getClasificador());
        parametros.put("fuente", fuente);
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

    public SelectItem[] getComboAsignacionesEgresos() {

        if (proyectosBean.getPrograma() == null) {
            // selecciona proyecto
            return null;
        }
        String codigo = proyectosBean.getPrograma().getCodigo();
        int anioParametro = 0;
        if (proyectosBean.getProyecto() != null) {
            // selecciona proyecto
            codigo = proyectosBean.getProyecto().getCodigo();
            anioParametro = proyectosBean.getProyecto().getAnio();
        }
        if (proyectosBean.getProyectoSeleccionado() != null) {
            codigo = proyectosBean.getProyectoSeleccionado().getCodigo();
            anioParametro = proyectosBean.getProyectoSeleccionado().getAnio();
        }
        Map parametros = new HashMap();

        parametros.put("proyecto", codigo + "%");
        parametros.put("anio", anioParametro);
        if (fuente != null) {
            parametros.put("fuente", fuente);
            parametros.put(";where", "o.proyecto.codigo like :proyecto and o.proyecto.anio=:anio"
                    + "and o.fuente=:fuente and o.clasificador.ingreso=false");
        } else {
            parametros.put(";where", "o.proyecto.codigo like :proyecto  and o.proyecto.anio=:anio"
                    + "and o.clasificador.ingreso=false");
        }

        parametros.put(";orden", "o.proyecto.codigo,o.clasificador.codigo,o.fuente.nombre");
        try {
            asignaciones = ejbAsignaciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AsignacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Combos.getSelectItems(asignaciones, true);
    }

//    public void cambiaCodigo(ValueChangeEvent event) {
//        //clasificador = null;
//        if (proyectosBean.getProyectoSeleccionado() != null) {
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
//            Map parametros = new HashMap();
////            if (proyectosBean.getPrograma() == null) {
////                // selecciona proyecto
////                asignacion = null;
////                return;
////            }
//            String codigo = proyectosBean.getProyectoSeleccionado().getCodigo();
////            if (proyectosBean.getProyecto() != null) {
////                // selecciona proyecto
////                codigo = proyectosBean.getProyecto().getCodigo();
////            }
//
//            parametros.put("proyecto", codigo + "%");
//            parametros.put("cuenta", newWord + "%");
//            if (fuente != null) {
//                parametros.put("fuente", fuente);
//                parametros.put(";where", "o.proyecto.codigo like :proyecto and o.fuente=:fuente and o.clasificador.ingreso=false and o.clasificador.codigo like :cuenta");
//            } else {
//                parametros.put(";where", "o.proyecto.codigo like :proyecto  and o.clasificador.ingreso=false o.clasificador.codigo like :cuenta");
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
//            asignacionesLista = new ArrayList();
//            for (Asignaciones e : aux) {
//                SelectItem s = new SelectItem(e, e.getClasificador().getCodigo());
//                asignacionesLista.add(s);
//            }
//            if (autoComplete.getSelectedItem() != null) {
//                asignacion = (Asignaciones) autoComplete.getSelectedItem().getValue();
//            } else {
//
//                Asignaciones tmp = null;
//                for (int i = 0, max = asignacionesLista.size(); i < max; i++) {
//                    SelectItem e = (SelectItem) asignacionesLista.get(i);
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ProyectosVista";
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
        Date desde = getConfiguracionBean().getConfiguracion().getPinicialpresupuesto();
        Calendar ca = Calendar.getInstance();
        ca.setTime(desde);
        anio = ca.get(Calendar.YEAR);
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
     * @return the asignacionesLista
     */
    public List getAsignacionesLista() {
        return asignacionesLista;
    }

    /**
     * @param asignacionesLista the asignacionesLista to set
     */
    public void setAsignacionesLista(List asignacionesLista) {
        this.asignacionesLista = asignacionesLista;
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
     * @return the clasificador
     */
    public String getClasificador() {
        return clasificador;
    }

    /**
     * @param clasificador the clasificador to set
     */
    public void setClasificador(String clasificador) {
        this.clasificador = clasificador;
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

}
