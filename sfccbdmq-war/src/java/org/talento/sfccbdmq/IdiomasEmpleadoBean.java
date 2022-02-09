/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.Date;
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
import org.beans.sfccbdmq.IdiomasFacade;
import org.entidades.sfccbdmq.Idiomas;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "idiomasEmpleado")
@ViewScoped
public class IdiomasEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Idiomas> listaIdiomas;
    private Idiomas idioma;
    private Formulario formulario = new Formulario();
    private Formulario formularioPrincipal = new Formulario();
    @EJB
    private IdiomasFacade ejbIdiomas;
//    @EJB
//    private DocumentosempleadoFacade ejbDocumentos;
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
        String nombreForma = "IdiomasEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado=Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else  if (perfilString == null) {
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
    /**
     * Creates a new instance of IdiomasEmpleadoBean
     */
    public IdiomasEmpleadoBean() {
    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.empleado=:empleado and o.empleado.activo=true ";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            listaIdiomas = ejbIdiomas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(IdiomasEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        idioma = new Idiomas();
        idioma.setFechaingreso(new Date());
        idioma.setEmpleado(empleadoBean.getEmpleadoSeleccionado());
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("IDIOMA");
        imagenesBean.Buscar();
        getFormulario().insertar();
        return null;
    }

    public String modifica(Idiomas idioma) {
        this.idioma = idioma;
        imagenesBean.setIdModulo(idioma.getEmpleado().getId());
        imagenesBean.setModulo("IDIOMA");
        imagenesBean.Buscar();
        getFormulario().editar();
        return null;
    }

    public String borra(Idiomas idioma) {
        this.idioma = idioma;
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {

        if (idioma.getIdioma()== null || idioma.getIdioma().isEmpty()) {
            MensajesErrores.advertencia("Ingrese idioma");
            return true;
        }
        
        if (idioma.getHablado()== null) {
            MensajesErrores.advertencia("Ingrese % hablado");
            return true;
        }
        if (idioma.getEscrito()== null) {
            MensajesErrores.advertencia("Ingrese % escrito");
            return true;
        }
        if (idioma.getHablado()<=0) {
            MensajesErrores.advertencia("Ingrese % hablado");
            return true;
        }
        if (idioma.getHablado()>100) {
            MensajesErrores.advertencia("Ingrese % hablado");
            return true;
        }
        if (idioma.getEscrito()<=0) {
            MensajesErrores.advertencia("Ingrese % escrito");
            return true;
        }
        if (idioma.getEscrito()>100) {
            MensajesErrores.advertencia("Ingrese % escrito");
            return true;
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbIdiomas.create(idioma, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            ejbIdiomas.edit(idioma, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String eliminar() {
        try {

            ejbIdiomas.remove(idioma, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
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
     * @return the listaIdiomas
     */
    public List<Idiomas> getListaIdiomas() {
        return listaIdiomas;
    }

    /**
     * @param listaIdiomas the listaIdiomas to set
     */
    public void setListaIdiomas(List<Idiomas> listaIdiomas) {
        this.listaIdiomas = listaIdiomas;
    }

    /**
     * @return the idioma
     */
    public Idiomas getIdiome() {
        return idioma;
    }

    /**
     * @param idioma the idioma to set
     */
    public void setIdioma(Idiomas idioma) {
        this.idioma = idioma;
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
     * @return the formularioPrincipal
     */
    public Formulario getFormularioPrincipal() {
        return formularioPrincipal;
    }

    /**
     * @param formularioPrincipal the formularioPrincipal to set
     */
    public void setFormularioPrincipal(Formulario formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }
    public String cargaListner() {
        buscar();
        formularioPrincipal.insertar();
        return null;
    }
}
