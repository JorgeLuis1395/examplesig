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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.CargosFacade;
import org.beans.sfccbdmq.EscalassalarialesFacade;
import org.beans.sfccbdmq.FormulariosFacade;
import org.beans.sfccbdmq.NivelesgestionFacade;
import org.beans.sfccbdmq.PondnivelgestionFacade;
import org.entidades.sfccbdmq.Nivelesgestion;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "nivelesGestion")
@ViewScoped
public class NivelesGestionBean implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Creates a new instance of NivelesGestionBean
     */
    
    private Formulario formulario = new Formulario();
    private Nivelesgestion nivel;
    private List<Nivelesgestion> listaNiveles;
    private String codigo;
    private String nombre;
    @EJB
    private NivelesgestionFacade ejbNiveles;
    @EJB
    private EscalassalarialesFacade ejbEscalas;
    @EJB
    private FormulariosFacade ejbFormularios;
    @EJB
    private PondnivelgestionFacade ejbPond;
    @EJB
    private CargosFacade ejbCargos;
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
        String nombreForma = "NivelesGestionVista";
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
    public NivelesGestionBean() {
    }

    

    public String nuevo() {
        
        nivel = new Nivelesgestion();
        nivel.setActivo(Boolean.TRUE);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {
        
        nivel = listaNiveles.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        
        nivel = listaNiveles.get(formulario.getFila().getRowIndex());
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
            String where = "  o.activo=true";
            //parametros.put(";where", " o.institucion=:institucion and o.activo=true");
            //CODIGO
            if (!((codigo == null) || (codigo.isEmpty()))) {
                where += " and upper(o.codigo) like:codigo";
                parametros.put("codigo", "%" + codigo.toUpperCase() + "%");
            }
            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            parametros.put(";where", where);
            listaNiveles = ejbNiveles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        if ((nivel.getCodigo() == null || nivel.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Código es necesario");
            return true;
        }
         if (!formulario.isModificar()) {
            Map parametros = new HashMap();
            parametros.put(";where", "upper(o.codigo) like:codigo and o.activo=true");
            parametros.put("codigo", "%" + nivel.getCodigo().toUpperCase() + "%");
            List<Nivelesgestion> aux = new LinkedList<>();
            try {
                aux = ejbNiveles.encontarParametros(parametros);
                if (!(aux.isEmpty())) {
                    MensajesErrores.advertencia("Código de Nivel de Gestión ya registrado");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger("").log(Level.SEVERE, null, ex);
            }

        }
        if ((nivel.getNombre() == null || nivel.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if ((nivel.getDescripcion() == null || nivel.getDescripcion().isEmpty())) {
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
            nivel.setCodigo(nivel.getCodigo().toUpperCase());
            ejbNiveles.create(nivel, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbNiveles.edit(nivel, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
       
        try {
            Map parametros=new HashMap();
            parametros.put(";where","o.nivelgestion=:nivelgestion");
            parametros.put("nivelgestion",nivel);
            int total=ejbPond.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existe ponderacion no es posible borrar");
                return null;
            }
            total=ejbFormularios.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existe formularios no es posible borrar");
                return null;
            }
            parametros=new HashMap();
            parametros.put(";where","o.nivel=:nivelgestion");
            parametros.put("nivelgestion",nivel);
            total=ejbCargos.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existen cargos no es posible borrar");
                return null;
            }
            parametros=new HashMap();
            parametros.put(";where","o.nivel=:nivelgestion");
            parametros.put("nivelgestion",nivel);
            total=ejbCargos.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existen cargos no es posible borrar");
                return null;
            }
            parametros=new HashMap();
            parametros.put(";where","o.niveldegestion=:nivelgestion");
            parametros.put("nivelgestion",nivel);
            total=ejbEscalas.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existen escalas no es posible borrar");
                return null;
            }
            nivel.setActivo(Boolean.FALSE);
            ejbNiveles.edit(nivel, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Nivelesgestion traer(Integer id) throws ConsultarException {
        return ejbNiveles.find(id);
    }

    public SelectItem[] getComboNivelesGestion() {
        buscar();
        return Combos.getSelectItems(listaNiveles, true);
    }

    public List<Nivelesgestion> getNiveles() {
        Map parametros = new HashMap();
        parametros.put(";where", " o.activo=true");
        try {
            return ejbNiveles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(NivelesGestionBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the nivel
     */
    public Nivelesgestion getNivel() {
        return nivel;
    }

    /**
     * @param nivel the nivel to set
     */
    public void setNivel(Nivelesgestion nivel) {
        this.nivel = nivel;
    }

    /**
     * @return the listaNiveles
     */
    public List<Nivelesgestion> getListaNiveles() {
        return listaNiveles;
    }

    /**
     * @param listaNiveles the listaNiveles to set
     */
    public void setListaNiveles(List<Nivelesgestion> listaNiveles) {
        this.listaNiveles = listaNiveles;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
