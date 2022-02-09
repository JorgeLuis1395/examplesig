/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;;


import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;

import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import java.io.Serializable;
import java.math.BigDecimal;
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
import org.beans.sfccbdmq.ValoracionesdesempenioFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Valoracionesdesempenio;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "valoracionDesempenio")
@ViewScoped
public class ValoracionDesempenioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of ValoracionDesempenioBean
     */
    
    private Formulario formulario = new Formulario();
    private Valoracionesdesempenio valoracion;
    private List<Valoracionesdesempenio> listaValoraciones;
    private String nombre;

    @EJB
    private ValoracionesdesempenioFacade ejbValoraciones;

    public ValoracionDesempenioBean() {
    }

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
        String nombreForma = "OficinasVista";
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
        
        valoracion = new Valoracionesdesempenio();
        valoracion.setActivo(Boolean.TRUE);
        valoracion.setMinimo(BigDecimal.ONE);
        valoracion.setMaximo(BigDecimal.TEN);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {
        valoracion = listaValoraciones.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        valoracion = listaValoraciones.get(formulario.getFila().getRowIndex());
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
            String where = " o.activo=true";
            //parametros.put(";where", " o.institucion=:institucion and o.activo=true");
            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            parametros.put(";where", where);
            listaValoraciones = ejbValoraciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ValoracionDesempenioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        if ((valoracion.getNombre() == null || valoracion.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
         if (valoracion.getMinimo() == null || valoracion.getMinimo().compareTo(BigDecimal.ZERO) <= 0) {
            MensajesErrores.advertencia("Nota mínima es necesaria");
            return true;
        }
        if (valoracion.getMaximo() == null || valoracion.getMaximo().compareTo(BigDecimal.ZERO) <= 0) {
            MensajesErrores.advertencia("Nota máxima es necesaria");
            return true;
        }
        if (valoracion.getMinimo().compareTo(valoracion.getMaximo()) >= 0) {
            MensajesErrores.advertencia("Nota Mínima no debe ser mayor que la Nota Máxima");
            return true;
        }
        if ((valoracion.getDescripcion() == null || valoracion.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Descripción es necesario");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbValoraciones.create(valoracion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ValoracionDesempenioBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbValoraciones.edit(valoracion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ValoracionDesempenioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        
        try {
            valoracion.setActivo(Boolean.FALSE);
            ejbValoraciones.edit(valoracion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ValoracionDesempenioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Valoracionesdesempenio traer(Integer id) throws ConsultarException {
        return ejbValoraciones.find(id);
    }

    public SelectItem[] getComboValoracionDesempenio() {
        buscar();
        return Combos.getSelectItems(listaValoraciones, true);
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
     * @return the valoracion
     */
    public Valoracionesdesempenio getValoracion() {
        return valoracion;
    }

    /**
     * @param valoracion the valoracion to set
     */
    public void setValoracion(Valoracionesdesempenio valoracion) {
        this.valoracion = valoracion;
    }

    /**
     * @return the listaValoraciones
     */
    public List<Valoracionesdesempenio> getListaValoraciones() {
        return listaValoraciones;
    }

    /**
     * @param listaValoraciones the listaValoraciones to set
     */
    public void setListaValoraciones(List<Valoracionesdesempenio> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
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

}
