/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

;

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
import org.beans.sfccbdmq.TipojubilacionFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tipojubilacion;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */


@ManagedBean(name = "tipoJubilacionSfccbdmq")
@ViewScoped
public class TipoJubilacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoJubilacionBean
     */
    public TipoJubilacionBean() {
    }

    private Formulario formulario = new Formulario();
    private Tipojubilacion jubilacion;
    private List<Tipojubilacion> listaJubilacions;
    private String nombre;
    private Integer tipo;

    @EJB
    private TipojubilacionFacade ejbTjubilacions;
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
        String nombreForma = "TipoJubilacionVista";
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

        jubilacion = new Tipojubilacion();
        jubilacion.setConsecutivas(0);
        jubilacion.setEdadminima(0);
        jubilacion.setNumeroaportes(0);
        jubilacion.setDiscapacidad(false);
        formulario.insertar();
        return null;
    }

    public String modificar() {
        jubilacion = listaJubilacions.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        jubilacion = listaJubilacions.get(formulario.getFila().getRowIndex());
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

            if (!where.isEmpty()) {
                parametros.put(";where", where);
            }
            parametros.put(";orden", "o.nombre asc");
            listaJubilacions = ejbTjubilacions.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoJubilacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if ((jubilacion.getNombre() == null || jubilacion.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if ((jubilacion.getNumeroaportes() == null || jubilacion.getNumeroaportes() <= 0)) {
            MensajesErrores.advertencia("Numero de imposiciones es necesario");
            return true;
        }
//        if (jubilacion.getTipo() == 2) {
//            jubilacion.setCargovacaciones(Boolean.TRUE);
//        }
//        if (jubilacion.getTipo() == 1) {
//            if ((jubilacion.getDuracion() == null) || (jubilacion.getDuracion() < 0)) {
//                MensajesErrores.advertencia("Duración es obligatoria");
//                return true;
//            }
//        } else {
//            jubilacion.setDuracion(null);
//        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbTjubilacions.create(jubilacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoJubilacionBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbTjubilacions.edit(jubilacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoJubilacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", jubilacion);
            int total = ejbLicencias.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar ya utilizado en jubilacions");
                return null;
            }
            ejbTjubilacions.remove(jubilacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoJubilacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Tipojubilacion traer(Integer id) throws ConsultarException {
        return ejbTjubilacions.find(id);
    }

    

    public SelectItem[] getComboJubilacions() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre asc");
            return Combos.getSelectItems(ejbTjubilacions.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoJubilacionBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the jubilacion
     */
    public Tipojubilacion getJubilacion() {
        return jubilacion;
    }

    /**
     * @param jubilacion the jubilacion to set
     */
    public void setJubilacion(Tipojubilacion jubilacion) {
        this.jubilacion = jubilacion;
    }

    /**
     * @return the listaJubilacions
     */
    public List<Tipojubilacion> getListaJubilacions() {
        return listaJubilacions;
    }

    /**
     * @param listaJubilacions the listaJubilacions to set
     */
    public void setListaJubilacions(List<Tipojubilacion> listaJubilacions) {
        this.listaJubilacions = listaJubilacions;
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
