/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.formularios.sfccbdmq;

import org.auxiliares.sfccbdmq.Codificador;
import org.auxiliares.sfccbdmq.MensajesErrores;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.GruposusuariosFacade;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Gruposusuarios;
import org.errores.sfccbdmq.ConsultarException;
import org.seguridad.sfccbdmq.MenusBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "logueoSfccbdmq")
@ViewScoped
public class LogeoSfccbdmqBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of SeguridadBean
     */
    private Entidades usuario;
    private boolean modulos;
    private String usr;
    private String pwd;
    @EJB
    private EntidadesFacade ejbEntidades;
    @EJB
    private GruposusuariosFacade ejbGrupos;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguiridad;
    @ManagedProperty(value = "#{menusSfccbdmq}")
    private MenusBean menusBean;

    public LogeoSfccbdmqBean() {
    }

    public String login() {
        try {
            if ((usr == null) || (usr.isEmpty())) {
                MensajesErrores.advertencia("Ingrese un usuario válido");
                return null;
            }
            if ((pwd == null) || (pwd.isEmpty())) {
                MensajesErrores.advertencia("Ingrese una clave válida");
                return null;
            }
            Codificador c = new Codificador();
            usuario = ejbEntidades.login(usr, c.getEncoded(pwd, "MD5"));
            
            if (usuario == null) {
                usuario = ejbEntidades.loginAD(usr, pwd);
            }
            if (usuario == null) {
                MensajesErrores.advertencia("Usuario no registrado, o clave invalida");
                return null;
            }
            getParametrosSeguiridad().setLogueado(usuario);
            // veamos si tiene algun modulo de permiso
            Map parametros = new HashMap();
            parametros.put(";where", "o.usuario=:usuario ");
            parametros.put("usuario", usuario);
            List<Gruposusuarios> listaGrupos = ejbGrupos.encontarParametros(parametros);
            boolean moduloPersonal = true;
            for (Gruposusuarios g : listaGrupos) {
                if (moduloPersonal) {
                    if (!g.getModulo().getCodigo().equals("PER")) {
                        moduloPersonal = false;
                    }
                }
            }
            if (moduloPersonal) {
                return menusBean.llenarPersonal(true);
            }
            // ingresar al sistema

        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(LogeoSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        modulos = true;
        return null;
    }

    /**
     * @return the modulos
     */
    public boolean isModulos() {
        return modulos;
    }

    /**
     * @param modulos the modulos to set
     */
    public void setModulos(boolean modulos) {
        this.modulos = modulos;
    }

    /**
     * @return the usr
     */
    public String getUsr() {
        return usr;
    }

    /**
     * @param usr the usr to set
     */
    public void setUsr(String usr) {
        this.usr = usr;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the parametrosSeguiridad
     */
    public ParametrosSfccbdmqBean getParametrosSeguiridad() {
        return parametrosSeguiridad;
    }

    /**
     * @param parametrosSeguiridad the parametrosSeguiridad to set
     */
    public void setParametrosSeguiridad(ParametrosSfccbdmqBean parametrosSeguiridad) {
        this.parametrosSeguiridad = parametrosSeguiridad;
    }

    /**
     * @return the menusBean
     */
    public MenusBean getMenusBean() {
        return menusBean;
    }

    /**
     * @param menusBean the menusBean to set
     */
    public void setMenusBean(MenusBean menusBean) {
        this.menusBean = menusBean;
    }
}
