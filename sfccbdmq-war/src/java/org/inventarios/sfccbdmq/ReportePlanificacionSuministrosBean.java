/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import javax.faces.application.Resource;
import org.talento.sfccbdmq.EmpleadosBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.Reportesds;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
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
import org.beans.sfccbdmq.BodegasuministroFacade;
import org.beans.sfccbdmq.DetallesolicitudFacade;
import org.beans.sfccbdmq.EmpleadosuministrosFacade;
import org.beans.sfccbdmq.SuministrosFacade;
import org.entidades.sfccbdmq.Empleadosuministros;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigramasuministros;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Suministros;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reporteSuministroEmpresa")
@ViewScoped
public class ReportePlanificacionSuministrosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Organigramasuministros> listaOrgSuministros;
    private List<Suministros> listaSuministros;
    private List<Empleadosuministros> listaEmpleadosuministros;
    private Tiposuministros tipo;
    private Oficinas oficina;
    private int anio;
    private int revisado = -1;
    private int cuatrimestre;
    private Formulario formulario = new Formulario();
    private Formulario formularioOrgSol = new Formulario();
    @EJB
    private SuministrosFacade ejbSuministros;
    @EJB
    private BodegasuministroFacade ejbBodegaSuministro;
    @EJB
    private DetallesolicitudFacade ejbDetSol;
    @EJB
    private EmpleadosuministrosFacade ejbEmpleadosuministros;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Resource reporte;

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
    /* @return the parametrosSeguridad
     */

    public ParametrosSfccbdmqBean getParametrosSeguridad() {
        return parametrosSeguridad;
    }

    /**
     * @param parametrosSeguridad the parametrosSeguridad to set
     */
    public void setParametrosSeguridad(ParametrosSfccbdmqBean parametrosSeguridad) {
        this.parametrosSeguridad = parametrosSeguridad;
    }

    @PostConstruct
    private void activar() {
        Calendar c = Calendar.getInstance();
        c.setTime(configuracionBean.getConfiguracion().getPvigente());
        setAnio(c.get((Calendar.YEAR)));
//        anioActual = anio;

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
//        if (redirect == null) {
//            return;
//        }
        String nombreForma = "ReporteSuministroEmpresaVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
//            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
//            }
//        }
    }

    // fin perfiles
    /**
     * Creates a new instance of EmpleadosuministrosEmpleadoBean
     */
    public ReportePlanificacionSuministrosBean() {
    }

    public String buscar() {

//        if (oficina == null) {
//            MensajesErrores.advertencia("Ingrese un empleado");
//            return null;
//        }
//        if (tipo == null) {
//            MensajesErrores.advertencia("Seleccione un tipo ");
//            return null;
//        }
//        if (anio < anioActual) {
//            MensajesErrores.advertencia("No se puede planificar para años anteriores");
//            return null;
//        }
//        if (anio > anioActual + 1) {
//            MensajesErrores.advertencia("No se puede planificar para 2 años o más");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            String where = "o.trimestre=:trimestre and o.anio=:anio";
            parametros.put("anio", anio);
            parametros.put("trimestre", cuatrimestre);
            parametros.put(";where", where);
            parametros.put(";orden", "o.suministro.tipo.familia.nombre,o.suministro.tipo.nombre");
//            listaEmpleadosuministros = ejbEmpleadosuministros.encontarParametros(parametros);
            listaSuministros = new LinkedList<>();
            parametros = new HashMap();
//            parametros.put(";orden", "o.nombre");
            parametros.put(";orden", "o.tipo.familia.nombre,o.tipo.nombre");
            List<Suministros> suministros = ejbSuministros.encontarParametros(parametros);
            for (Suministros s : suministros) {

                // traer el saldo de la bodega
                parametros = new HashMap();
                parametros.put(";where", "o.suministro=:suministro");
                parametros.put(";campo", "o.saldo");
                parametros.put("suministro", s);
                double cantidadBodega = ejbBodegaSuministro.sumarCampoDoble(parametros);
                parametros = new HashMap();
                parametros.put(";where", "o.suministro=:suministro");
                parametros.put(";campo", "o.saldoinversion");
                parametros.put("suministro", s);
                double cantidadInvBodega = ejbBodegaSuministro.sumarCampoDoble(parametros);
                // traer lo planificado
                parametros = new HashMap();
//                if (s.getId()==661){
//                    MensajesErrores.advertencia("si");
//                }
                where = "o.trimestre=:trimestre and o.anio=:anio and o.suministro=:suministro";
                parametros.put("anio", anio);
                parametros.put("trimestre", cuatrimestre);
                parametros.put(";campo", "o.cantidad");
                parametros.put(";where", where);
                parametros.put("suministro", s);
                double cantidadPlanificada = ejbEmpleadosuministros.sumarCampoDoble(parametros);
                parametros = new HashMap();
                where = "o.trimestre=:trimestre and o.anio=:anio and o.suministro=:suministro";
                parametros.put("anio", anio);
                parametros.put("trimestre", cuatrimestre);
                parametros.put(";campo", "o.cantidadinv");
                parametros.put(";where", where);
                parametros.put("suministro", s);
                double cantidadPlanificadaInv = ejbEmpleadosuministros.sumarCampoDoble(parametros);
                s.setCantidad(cantidadPlanificada);
                s.setCantidadinv(cantidadPlanificadaInv);
                s.setCantidadbodega(cantidadBodega);
                s.setCantidadbodinv(cantidadInvBodega);
                if (cantidadPlanificada > cantidadBodega) {
                    s.setRequerido(cantidadPlanificada - cantidadBodega);
                } else {
                    s.setRequerido(0);
                }
                if (cantidadPlanificadaInv > cantidadInvBodega) {
                    s.setRequeridoinv(cantidadPlanificadaInv - cantidadInvBodega);
                } else {
                    s.setRequeridoinv(0);
                }
                s.setTotalRequerido(s.getRequerido()+s.getRequeridoinv());
                if (cantidadPlanificada + cantidadPlanificadaInv > 0) {
//                if (cantidad+cantidadInv+cantidadPlanificada+cantidadPlanificadaInv > 0) {
                    listaSuministros.add(s);
                }
            }
            parametros = new HashMap();
            parametros.put("empresa", configuracionBean.getConfiguracion().getNombre());
            parametros.put("expresado", "REPORTE DE PLANIFICACIÓN DE SUMINISTROS");
            parametros.put("tipo", "");
            parametros.put("anio", anio);
            parametros.put("cuatrimestre", cuatrimestre);
            parametros.put("nombrelogo", "logo-new.png");
            parametros.put("usuario", parametrosSeguridad.getLogueado().getUserid());
            parametros.put("fecha", new Date());
            Calendar c = Calendar.getInstance();
            setReporte(new Reportesds(parametros,
                    FacesContext.getCurrentInstance().getExternalContext().getRealPath("reportes/Planificacion.jasper"),
                    listaSuministros, "Suministros" + String.valueOf(c.getTimeInMillis()) + ".pdf"));
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePlanificacionSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.insertar();
        return null;
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
     * @return the listaOrgSuministros
     */
    public List<Organigramasuministros> getListaOrgSuministros() {
        return listaOrgSuministros;
    }

    /**
     * @param listaOrgSuministros the listaOrgSuministros to set
     */
    public void setListaOrgSuministros(List<Organigramasuministros> listaOrgSuministros) {
        this.listaOrgSuministros = listaOrgSuministros;
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
     * @return the tipo
     */
    public Tiposuministros getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposuministros tipo) {
        this.tipo = tipo;
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
     * @return the oficina
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    /**
     * @return the revisado
     */
    public int getRevisado() {
        return revisado;
    }

    /**
     * @param revisado the revisado to set
     */
    public void setRevisado(int revisado) {
        this.revisado = revisado;
    }

    /**
     * @return the listaSuministros
     */
    public List<Suministros> getListaSuministros() {
        return listaSuministros;
    }

    /**
     * @param listaSuministros the listaSuministros to set
     */
    public void setListaSuministros(List<Suministros> listaSuministros) {
        this.listaSuministros = listaSuministros;
    }

    /**
     * @return the formularioOrgSol
     */
    public Formulario getFormularioOrgSol() {
        return formularioOrgSol;
    }

    /**
     * @param formularioOrgSol the formularioOrgSol to set
     */
    public void setFormularioOrgSol(Formulario formularioOrgSol) {
        this.formularioOrgSol = formularioOrgSol;
    }

    /**
     * @return the cuatrimestre
     */
    public int getCuatrimestre() {
        return cuatrimestre;
    }

    /**
     * @param cuatrimestre the cuatrimestre to set
     */
    public void setCuatrimestre(int cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }

    /**
     * @return the reporte
     */
    public Resource getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Resource reporte) {
        this.reporte = reporte;
    }
}
