/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seguridad.sfccbdmq;

import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
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
import org.beans.sfccbdmq.GruposusuariosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Gruposusuarios;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "usuariosGrpSfccbdmq")
@ViewScoped
public class UsuariosGrpBean implements Serializable {

    /**
     * Creates a new instance of UsuariosGruposBean
     */
    public UsuariosGrpBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean usariosBean;
    private Gruposusuarios grupoUsuario;
    protected Codigos modulo;
    @EJB
    private GruposusuariosFacade ejbGrpUsr;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "UsrGrpVista";
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
     * @return the grupoUsuario
     */
    public Gruposusuarios getGrupoUsuario() {
        return grupoUsuario;
    }

    /**
     * @param grupoUsuario the grupoUsuario to set
     */
    public void setGrupoUsuario(Gruposusuarios grupoUsuario) {
        this.grupoUsuario = grupoUsuario;
    }

    /**
     * @return the usariosBean
     */
    public EntidadesBean getUsariosBean() {
        return usariosBean;
    }

    /**
     * @param usariosBean the usariosBean to set
     */
    public void setUsariosBean(EntidadesBean usariosBean) {
        this.usariosBean = usariosBean;
    }

    public String grabar() {
        try {
            if (grupoUsuario.getId() == null) {
                grupoUsuario.setModulo(modulo);
                ejbGrpUsr.create(getGrupoUsuario(), seguridadbean.getLogueado().getUserid());
            } else {
//                grupoUsuario.setModulo(modulo);
                ejbGrpUsr.edit(getGrupoUsuario(), seguridadbean.getLogueado().getUserid());
            }
        } catch (GrabarException | InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(UsuariosGrpBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        usariosBean.setEntidad(null);
        usariosBean.setApellidos(null);
        return null;
    }

    public String buscar() {
        formulario.cancelar();
        Entidades e = usariosBean.getEntidad();
        if (e == null) {
            MensajesErrores.informacion("Es necesario un usuario válido");
            return null;
        }

        grupoUsuario = new Gruposusuarios();
        grupoUsuario.setUsuario(e);
        grupoUsuario.setModulo(modulo);
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.usuario=:usuario and o.modulo=:modulo");
            parametros.put("usuario", e);
            parametros.put("modulo", modulo);

            List<Gruposusuarios> lgu = ejbGrpUsr.encontarParametros(parametros);
            if (!((lgu == null) || (lgu.isEmpty()))) {
                setGrupoUsuario(lgu.get(0));
//                formulario.editar();
            }

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(UsuariosGrpBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.editar();
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
     * @return the modulo
     */
    public Codigos getModulo() {
        return modulo;
    }

    /**
     * @param modulo the modulo to set
     */
    public void setModulo(Codigos modulo) {
        this.modulo = modulo;
    }
}
