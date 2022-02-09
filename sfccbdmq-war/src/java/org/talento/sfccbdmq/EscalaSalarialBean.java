/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.beans.sfccbdmq.EscalassalarialesFacade;
import org.entidades.sfccbdmq.Escalassalariales;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "escala")
@ViewScoped
public class EscalaSalarialBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of EscalaSalarialBean
     */
    public EscalaSalarialBean() {
    }

    private Formulario formulario = new Formulario();
    private Escalassalariales escala;
    private List<Escalassalariales> listaEscalas;
    private String nombre;
    private Nivelesgestion rol;
    @EJB
    private EscalassalarialesFacade ejbEscalas;

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
        String nombreForma = "EscalaSalarialVista";
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

        escala = new Escalassalariales();
        escala.setActivo(Boolean.TRUE);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {

        escala = listaEscalas.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {

        escala = listaEscalas.get(formulario.getFila().getRowIndex());
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
                where += " and upper(o.nombre) like :nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            if (rol != null) {
                where += " and o.niveldegestion=:niveldegestion";
                parametros.put("niveldegestion", rol);
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            listaEscalas = ejbEscalas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        if ((escala.getNombre() == null || escala.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if ((escala.getCodigo() == null || escala.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Codigo es necesario");
            return true;
        }
        if ((escala.getSueldobase() == null || escala.getSueldobase().compareTo(BigDecimal.ZERO) < 0)) {
            MensajesErrores.advertencia("Sueldo base debe ser una cantidad positiva");
            return true;
        }
        if (escala.getGrado() == null) {
            MensajesErrores.advertencia("Grado es necesario");
            return true;
        }
        if (escala.getNiveldegestion()== null) {
            MensajesErrores.advertencia("Rol es necesario");
            return true;
        }
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", escala.getCodigo());
            try {
                int total = ejbEscalas.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Código ya existe");
                    return true;
                }
            } catch (ConsultarException ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
            ejbEscalas.create(escala, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbEscalas.edit(escala, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            escala.setActivo(Boolean.FALSE);
            ejbEscalas.edit(escala, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EscalaSalarialBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Escalassalariales traer(Integer id) throws ConsultarException {
        return ejbEscalas.find(id);
    }

    public SelectItem[] getComboEscalas() {
        buscar();
        return Combos.getSelectItems(listaEscalas, true);
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
     * @return the escala
     */
    public Escalassalariales getEscala() {
        return escala;
    }

    /**
     * @param escala the escala to set
     */
    public void setEscala(Escalassalariales escala) {
        this.escala = escala;
    }

    /**
     * @return the listaEscalas
     */
    public List<Escalassalariales> getListaEscalas() {
        return listaEscalas;
    }

    /**
     * @param listaEscalas the listaEscalas to set
     */
    public void setListaEscalas(List<Escalassalariales> listaEscalas) {
        this.listaEscalas = listaEscalas;
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
     * @return the rol
     */
    public Nivelesgestion getRol() {
        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(Nivelesgestion rol) {
        this.rol = rol;
    }
}
