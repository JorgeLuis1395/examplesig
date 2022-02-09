/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.HashMap;
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
import org.beans.sfccbdmq.LicenciasFacade;
import org.beans.sfccbdmq.TipopermisosFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipopermisos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "tipoPermiso")
@ViewScoped
public class TipoPermisosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoPermisoBean
     */
    public TipoPermisosBean() {
    }

    private Formulario formulario = new Formulario();
    private Tipopermisos permiso;
    private List<Tipopermisos> listaPermisos;
    private String nombre;
    private int tipo;

    @EJB
    private TipopermisosFacade ejbTpermisos;
    @EJB
    private LicenciasFacade ejbLicencias;

    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;

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

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "TipoPermisoVista";
        if (perfilString == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
        if (this.getPerfil() == null) {
            org.auxiliares.sfccbdmq.MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
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

    public String nuevo() {
        permiso = new Tipopermisos();
        permiso.setDiasaprobarautomatico(Boolean.FALSE);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        permiso = listaPermisos.get(formulario.getFila().getRowIndex());
        if (permiso.getDiasaprobarautomatico() == null) {
            permiso.setDiasaprobarautomatico(Boolean.FALSE);
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
        permiso = listaPermisos.get(formulario.getFila().getRowIndex());
        if (permiso.getDiasaprobarautomatico() == null) {
            permiso.setDiasaprobarautomatico(Boolean.FALSE);
        }
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        try {
            Map parametros = new HashMap();
            String where = "";

            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += "  upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            //DURACIÓN
            if (tipo > -10) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.tipo =:tipo";
                parametros.put("tipo", tipo);
            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.tipo,o.nombre asc");
            listaPermisos = ejbTpermisos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

//        if ((permiso.getNombre() == null || permiso.getNombre().isEmpty())) {
//            MensajesErrores.advertencia("Nombre es necesario");
//            return true;
//        }
//        if (permiso.getTipo() == 2) {
//            permiso.setCargovacaciones(Boolean.TRUE);
//        }
//        if (permiso.getTipo() == 1) {
//            if ((permiso.getDuracion() == null) || (permiso.getDuracion() < 0)) {
//                MensajesErrores.advertencia("Duración es obligatoria");
//                return true;
//            }
//        } else {
//            permiso.setDuracion(null);
//        }
        if (permiso.getDiasaprobarautomatico()) {
            if (permiso.getDiasaprobar() == null) {
                MensajesErrores.advertencia("Ingrese número de días en que se ejecutará la autorización automatica");
                return true;
            }
        } else {
            permiso.setDiasaprobar(null);
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbTpermisos.create(permiso, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbTpermisos.edit(permiso, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", permiso);
            int total = ejbLicencias.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar ya utilizado en permisos");
                return null;
            }
            ejbTpermisos.remove(permiso, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Tipopermisos traer(Integer id) throws ConsultarException {
        return ejbTpermisos.find(id);
    }

    public SelectItem[] getComboTiposPermiso() {
        try {
            Map parametros = new HashMap();
//            String where = "o.tipo=1 and o.muestra=true";

//            parametros.put(";where", where);
            parametros.put(";orden", "o.tipo,o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPermisos() {
        try {
            Map parametros = new HashMap();
            String where = "o.tipo=1 and o.muestra=true";

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboPermisosTodos() {
        try {
            Map parametros = new HashMap();
            String where = "o.tipo=1 ";

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboLicencias() {
        try {
            Map parametros = new HashMap();
            String where = "o.tipo=0 and o.muestra=true";

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboVacaciones() {
        try {
            Map parametros = new HashMap();
            String where = "o.tipo=2 and o.muestra=true";

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboLicenciasTodos() {
        try {
            Map parametros = new HashMap();
            String where = "o.tipo=0";

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboVacacionesTodos() {
        try {
            Map parametros = new HashMap();
            String where = "o.tipo=2 ";

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTpermisos.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoPermisosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the permiso
     */
    public Tipopermisos getPermiso() {
        return permiso;
    }

    /**
     * @param permiso the permiso to set
     */
    public void setPermiso(Tipopermisos permiso) {
        this.permiso = permiso;
    }

    /**
     * @return the listaPermisos
     */
    public List<Tipopermisos> getListaPermisos() {
        return listaPermisos;
    }

    /**
     * @param listaPermisos the listaPermisos to set
     */
    public void setListaPermisos(List<Tipopermisos> listaPermisos) {
        this.listaPermisos = listaPermisos;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the tipo
     */
    public Integer getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

}
