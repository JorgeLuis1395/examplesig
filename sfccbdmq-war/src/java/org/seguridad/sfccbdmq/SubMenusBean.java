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
import org.beans.sfccbdmq.MenusistemaFacade;
import org.entidades.sfccbdmq.Menusistema;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "subMenusSfccbdmq")
@ViewScoped
public class SubMenusBean implements Serializable {

    /**
     * Creates a new instance of MenusBean
     */
    public SubMenusBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguiridad;

    private Formulario formulario = new Formulario();
    private List<Menusistema> menus;
    private Menusistema menu;
    private Menusistema menuPadre;
    private int modulo;
    @EJB
    private MenusistemaFacade ejbMenus;
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
        String nombreForma = "SubMenusVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguiridad.cerraSession();
        }
        this.setPerfil(parametrosSeguiridad.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguiridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguiridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguiridad.cerraSession();
//            }
//        }
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

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?

        menu = new Menusistema();
        menu.setModulo(null);
        menu.setIdpadre(menuPadre);
        formulario.insertar();
        return null;
    }

    public String modificar() {

        menu = menus.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {

        menu = menus.get(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }
    // buscar

    public String buscar() {

        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.idpadre=:padre");
            parametros.put(";orden", "o.texto");
            parametros.put("padre", menuPadre);
            menus = ejbMenus.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(SubMenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((menu.getTexto() == null) || (menu.getTexto().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Texto a desplegar");
            return true;
        }
        if ((menu.getFormulario() == null) || (menu.getFormulario().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre de formulario");
            return true;
        }
        if ((menu.getIdpadre() == null)) {
            MensajesErrores.advertencia("Es necesario seleccionar un  Menú");
            return true;
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
            ejbMenus.create(menu, getParametrosSeguiridad().getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubMenusBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbMenus.edit(menu, getParametrosSeguiridad().getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubMenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbMenus.remove(menu, getParametrosSeguiridad().getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubMenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    /**
     * @return the menus
     */
    public List<Menusistema> getMenus() {
        return menus;
    }

    /**
     * @param menus the menus to set
     */
    public void setMenus(List<Menusistema> menus) {
        this.menus = menus;
    }

    /**
     * @return the menu
     */
    public Menusistema getMenu() {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(Menusistema menu) {
        this.menu = menu;
    }

    /**
     * @return the menuPadre
     */
    public Menusistema getMenuPadre() {
        return menuPadre;
    }

    /**
     * @param menuPadre the menuPadre to set
     */
    public void setMenuPadre(Menusistema menuPadre) {
        this.menuPadre = menuPadre;
    }

    /**
     * @return the modulo
     */
    public int getModulo() {
        return modulo;
    }

    /**
     * @param modulo the modulo to set
     */
    public void setModulo(int modulo) {
        this.modulo = modulo;
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
}
