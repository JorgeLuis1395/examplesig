/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seguridad.sfccbdmq;

import org.auxiliares.sfccbdmq.Codificador;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.beans.sfccbdmq.CodigosFacade;
import org.beans.sfccbdmq.GruposusuariosFacade;
import org.beans.sfccbdmq.MenusistemaFacade;
import org.beans.sfccbdmq.PerfilesFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Gruposusuarios;
import org.entidades.sfccbdmq.Menusistema;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.component.menuitem.MenuItem;
import org.icefaces.ace.component.submenu.Submenu;
import org.icefaces.ace.model.DefaultMenuModel;
import org.icefaces.ace.model.MenuModel;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "menusSfccbdmq")
@SessionScoped
public class MenusBean {

    /**
     * Creates a new instance of MenusBean
     */
    public MenusBean() {
    }
    private MenuModel menuModulos;
    private Codigos menuSeleccionado;
    private MenuModel menuOpciones;
    @EJB
    private CodigosFacade ejbCodigos;
    @EJB
    private MenusistemaFacade ejbMenus;
    @EJB
    private GruposusuariosFacade ejbGrupos;
    @EJB
    private PerfilesFacade ejbPerfiles;
    private SelectItem[] comboModulos;
    private String formaSeleccionada;
    private String tema;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
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
     * @return the menuModulos
     */
    public MenuModel getMenuModulos() {
        return menuModulos;
    }

    /**
     * @param menuModulos the menuModulos to set
     */
    public void setMenuModulos(MenuModel menuModulos) {
        this.menuModulos = menuModulos;
    }

    /**
     * @return the menuSeleccionado
     */
    public Codigos getMenuSeleccionado() {
        return menuSeleccionado;
    }

    /**
     * @param menuSeleccionado the menuSeleccionado to set
     */
    public void setMenuSeleccionado(Codigos menuSeleccionado) {
        this.menuSeleccionado = menuSeleccionado;
    }

    /**
     * @return the menuOpciones
     */
    public MenuModel getMenuOpciones() {
        return menuOpciones;
    }

    /**
     * @param menuOpciones the menuOpciones to set
     */
    public void setMenuOpciones(MenuModel menuOpciones) {
        this.menuOpciones = menuOpciones;
    }

    @PostConstruct
    private void cargarModulos() {
        Map parmetros = new HashMap();
        parmetros.put(";where", "o.maestro.codigo='MODU'");
//        parmetros.put(";orden", "o.nombre");
        try {
            List<Codigos> codigosModulos = ejbCodigos.encontarParametros(parmetros);
//            menuModulos = new DefaultMenuModel();
//            for (Codigos c : codigosModulos) {
//                MenuItem nuevo = new MenuItem();
//                nuevo.setId(c.getCodigo() + "_mmi_" + c.getId());
//                nuevo.setValue(c.getDescripcion());
//                nuevo.setUrl(c.getNombre() + "/" + c.getParametros() + ".jsf?faces-redirect=true");
//                menuModulos.addMenuItem(nuevo);
//
//            }
            comboModulos = Combos.getSelectItems(codigosModulos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the comboModulos
     */
    public SelectItem[] getComboModulos() {
        return comboModulos;
    }

    /**
     * @param comboModulos the comboModulos to set
     */
    public void setComboModulos(SelectItem[] comboModulos) {
        this.comboModulos = comboModulos;
    }

    public void cambiaModulo(ValueChangeEvent vce) {

        Codigos c = (Codigos) vce.getNewValue();
        if (!c.equals(menuSeleccionado)) {
            menuSeleccionado = c;
            try {
                FacesContext.getCurrentInstance().getExternalContext()
                        //                        .redirect(c.getDescripcion()+ "/" + c.getParametros() + ".jsf");
                        .redirect(c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true");

                // cargar el menu
            } catch (IOException ex) {
                Logger.getLogger(MenusBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String llenarPersonal(boolean inicio) {
        String retorno = null;
        try {
            Codigos c = ejbCodigos.traerCodigo("MODU", "PER");
            // ver el grupo tiuene acceso al modulo
//            Gruposusuarios grupo;
            Map parametros = new HashMap();
//            empleadosBean.setEmpleado(seguridadbean.getLogueado().getEmpleados());
            Codificador cc = new Codificador();
            String claveCodificada = cc.getEncoded(seguridadbean.getLogueado().getPin(), "MD5");
            menuOpciones = new DefaultMenuModel();
            parametros = new HashMap();
            parametros.put(";where", "o.modulo=:modulo");
            parametros.put("modulo", c);
            parametros.put(";orden", "o.texto");
            List<Menusistema> listaMenus = ejbMenus.encontarParametros(parametros);
            for (Menusistema m : listaMenus) {
                Submenu nuevoSubmenu = new Submenu();
                nuevoSubmenu.setId("sm_" + (m.getId()));
                nuevoSubmenu.setLabel(m.getTexto());
                Map par = new HashMap();
                par.put("menu", m);
                par.put(";where", "o.idpadre=:menu");
                par.put(";orden", "o.texto");
                List<Menusistema> listaMenusOpciones = ejbMenus.encontarParametros(par);
                //
                for (Menusistema mn : listaMenusOpciones) {
                    MenuItem nuevo = new MenuItem();
                    Calendar cd = Calendar.getInstance();
                    nuevo.setId("mnu_" + cd.getTimeInMillis() + "_mmi_" + mn.getId());
                    nuevo.setValue(mn.getTexto());
                    nuevo.setUrl(mn.getFormulario() + ".jsf?faces-redirect=true&x="
                            + seguridadbean.getLogueado().getEmpleados().getId()
                            + "&p=" + seguridadbean.getLogueado().getId());
                    nuevoSubmenu.getChildren().add(nuevo);
                }
                menuOpciones.addSubmenu(nuevoSubmenu);
            }
            if (inicio) {
                retorno = c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true";
            } else {
                ExternalContext ctx
                        = FacesContext.getCurrentInstance().getExternalContext();
                String ctxPath
                        = ((ServletContext) ctx.getContext()).getContextPath();
                retorno=ctxPath  +"/" + c.getDescripcion() + "/" + c.getParametros() + ".jsf";
                 ctx.redirect(retorno);
//                 ctx.redirect(ctxPath  +"/" + c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true");
//                retorno=FacesContext.getCurrentInstance().getExternalContext().getRealPath(c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true");
//                FacesContext.getCurrentInstance().getExternalContext()
//                        .redirect(ctxPath+"/" + c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true");
            }
            menuSeleccionado = c;
        } catch (ConsultarException | IOException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retorno;
    }

    public String iralModulo(String codigo) {
        String retorno = null;
        String retorno1 = null;
        try {
            if (codigo.equals("PER")) {
                if (seguridadbean.getLogueado().getEmpleados() == null) {
                    MensajesErrores.error("Usuario no tiene permisos para este módulo");
                    return null;
                }
                if (seguridadbean.getLogueado().getEmpleados().getCargoactual() == null) {
                    MensajesErrores.error("Usuario no tiene permisos para este módulo");
                    return null;
                }
                return llenarPersonal(false);
            } else {

                Codigos c = ejbCodigos.traerCodigo("MODU", codigo);
                // ver el grupo tiuene acceso al modulo
                Gruposusuarios grupo;
                Map parametros = new HashMap();
                parametros.put(";where", "o.usuario=:usuario and o.modulo=:modulo");
                parametros.put("modulo", c);
                parametros.put("usuario", getSeguridadbean().getLogueado());
                List<Gruposusuarios> listaGrupos = ejbGrupos.encontarParametros(parametros);
                if ((listaGrupos == null) || (listaGrupos.isEmpty())) {
                    MensajesErrores.error("Usuario no tiene permisos para este módulo");
                    return null;
                }
                grupo = listaGrupos.get(0);
                menuOpciones = new DefaultMenuModel();
                parametros = new HashMap();
                parametros.put(";where", "o.modulo=:modulo");
                parametros.put("modulo", c);
                parametros.put(";orden", "o.texto");
                List<Menusistema> listaMenus = ejbMenus.encontarParametros(parametros);
                for (Menusistema m : listaMenus) {
                    Submenu nuevoSubmenu = new Submenu();
                    nuevoSubmenu.setId("sm_" + (m.getId()));
                    nuevoSubmenu.setLabel(m.getTexto());
                    Map par = new HashMap();
                    par.put("grupo", grupo.getGrupo());
                    par.put("menu", m);
                    par.put(";where", " o.grupo=:grupo and o.menu.idpadre=:menu");
                    par.put(";orden", "o.menu.texto");
                    List<Perfiles> disponibles = ejbPerfiles.encontarParametros(par);
                    //
                    for (Perfiles p : disponibles) {
                        MenuItem nuevo = new MenuItem();
                        nuevo.setId(nuevoSubmenu.getId() + "_mmi_" + p.getId());
                        nuevo.setValue(p.getMenu().getTexto());
                        nuevo.setUrl(p.getMenu().getFormulario() + ".jsf?faces-redirect=true&p=" + p.getId());
                        nuevoSubmenu.getChildren().add(nuevo);
                    }
                    menuOpciones.addSubmenu(nuevoSubmenu);
                }
                retorno = "../" + c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true";
                retorno1 = c.getDescripcion() + "/" + c.getParametros() + ".jsf";
                menuSeleccionado = c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        ir(retorno1);
//        return retorno;
        return null;
    }

    public void ir(String donde) {
        ExternalContext ctx
                = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath
                = ((ServletContext) ctx.getContext()).getContextPath();

        try {
            ctx.redirect(ctxPath + "/" + donde);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String iralModuloInicio(String codigo) {
        String retorno = null;
        try {
            if (codigo.equals("PER")) {
                if (seguridadbean.getLogueado().getEmpleados() == null) {
                    MensajesErrores.error("Usuario no tiene permisos para este módulo");
                    return null;
                }
                if (seguridadbean.getLogueado().getEmpleados().getCargoactual() == null) {
                    MensajesErrores.error("Usuario no tiene permisos para este módulo");
                    return null;
                }
                retorno = llenarPersonal(true);
                return retorno;
            } else {
                Codigos c = ejbCodigos.traerCodigo("MODU", codigo);
                // ver el grupo tiuene acceso al modulo
                Gruposusuarios grupo;
                Map parametros = new HashMap();
                parametros.put(";where", "o.usuario=:usuario and o.modulo=:modulo");
                parametros.put("modulo", c);
                parametros.put("usuario", getSeguridadbean().getLogueado());
                List<Gruposusuarios> listaGrupos = ejbGrupos.encontarParametros(parametros);
                if ((listaGrupos == null) || (listaGrupos.isEmpty())) {
                    MensajesErrores.error("Usuario no tiene permisos para este módulo");
                    return null;
                }
                grupo = listaGrupos.get(0);
                menuOpciones = new DefaultMenuModel();
                parametros = new HashMap();
                parametros.put(";where", "o.modulo=:modulo");
                parametros.put("modulo", c);
                parametros.put(";orden", "o.texto");
                List<Menusistema> listaMenus = ejbMenus.encontarParametros(parametros);
                for (Menusistema m : listaMenus) {
                    Submenu nuevoSubmenu = new Submenu();
                    nuevoSubmenu.setId("sm_" + (m.getId()));
                    nuevoSubmenu.setLabel(m.getTexto());
                    Map par = new HashMap();
                    par.put("grupo", grupo.getGrupo());
                    par.put("menu", m);
                    par.put(";where", " o.grupo=:grupo and o.menu.idpadre=:menu");
                    par.put(";orden", "o.menu.texto");
                    List<Perfiles> disponibles = ejbPerfiles.encontarParametros(par);
                    //
                    for (Perfiles p : disponibles) {
                        MenuItem nuevo = new MenuItem();
                        nuevo.setId(nuevoSubmenu.getId() + "_mmi_" + p.getId());
                        nuevo.setValue(p.getMenu().getTexto());
                        nuevo.setUrl(p.getMenu().getFormulario() + ".jsf?faces-redirect=true&p=" + p.getId());
                        nuevoSubmenu.getChildren().add(nuevo);
                    }
                    menuOpciones.addSubmenu(nuevoSubmenu);
                }
                retorno = c.getDescripcion() + "/" + c.getParametros() + ".jsf?faces-redirect=true";
                menuSeleccionado = c;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(MenusBean.class.getName()).log(Level.SEVERE, null, ex);
        }
//        ir(retorno);
        return retorno;
//        return null;
    }

    /**
     * @return the formaSeleccionada
     */
    public String getFormaSeleccionada() {
        return formaSeleccionada;
    }

    /**
     * @param formaSeleccionada the formaSeleccionada to set
     */
    public void setFormaSeleccionada(String formaSeleccionada) {
        this.formaSeleccionada = formaSeleccionada;
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

//    /**
//     * @return the empleadosBean
//     */
//    public EmpleadosBean getEmpleadosBean() {
//        return empleadosBean;
//    }
//
//    /**
//     * @param empleadosBean the empleadosBean to set
//     */
//    public void setEmpleadosBean(EmpleadosBean empleadosBean) {
//        this.empleadosBean = empleadosBean;
//    }
    /**
     * @return the tema
     */
    public String getTema() {
        return tema;
    }

    /**
     * @param tema the tema to set
     */
    public void setTema(String tema) {
        this.tema = tema;
    }
}
