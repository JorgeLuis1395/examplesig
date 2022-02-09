/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;;


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
import org.beans.sfccbdmq.RequerimientoscargoFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Requerimientoscargo;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "requerimientosCargo")
@ViewScoped
public class RequerimientosCargoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of RequerimientosCargoBean
     */
    public RequerimientosCargoBean() {
    }
    
    private Formulario formulario = new Formulario();
    private Requerimientoscargo requerimiento;
    private List<Requerimientoscargo> listaRequerimientos;
    private Codigos competencia;
    private boolean entrevista = false;
    private String nombre;

    @EJB
    private RequerimientoscargoFacade ejbRequerimientos;

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
        String nombreForma = "RequerimientosCargoVista";
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
        
        if (getCompetencia() == null) {
            MensajesErrores.advertencia("Seleccione Competencia");
            return null;
        }
        requerimiento = new Requerimientoscargo();
        requerimiento.setActivo(Boolean.TRUE);
        requerimiento.setCompetencia(competencia);
        formulario.insertar();
        return null;
    }

    public String modificar() {
       
        requerimiento = listaRequerimientos.get(formulario.getFila().getRowIndex());
        competencia = requerimiento.getCompetencia();
        formulario.editar();
        return null;
    }

    public String eliminar() {
        
        requerimiento = listaRequerimientos.get(formulario.getFila().getRowIndex());
        setCompetencia(requerimiento.getCompetencia());
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
       
        if (competencia == null) {
            MensajesErrores.advertencia("Seleccione Competencia");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.activo=true and o.competencia=:competencia";
            parametros.put("competencia", competencia);
            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.valor) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            parametros.put(";where", where);
            parametros.put(";orden", "o.orden asc");
            listaRequerimientos = ejbRequerimientos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(RequerimientosCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        if (requerimiento.getCompetencia() == null) {
            MensajesErrores.advertencia("Competencia es necesario");
            return true;
        }
        if ((requerimiento.getValor() == null || requerimiento.getValor().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (requerimiento.getOrden() == null) {
            MensajesErrores.advertencia("Orden es necesario");
            return true;
        }
        
//        if (requerimiento.getPesoevaluacion() == null) {
//            MensajesErrores.advertencia("Peso de evaluación es necesario");
//            return true;
//        }
//        if (requerimiento.getPesoevaluacion().compareTo(BigDecimal.ZERO) < 0) {
//            MensajesErrores.advertencia("Peso de evaluación debe ser positivo y mayor a cero");
//            return true;
//        }
        return false;
    }

    public String insertar() {
       
        if (validar()) {
            return null;
        }
        try {
            if (competencia.getMaestro().getCodigo().equals("VEE")) {
                requerimiento.setEntrevista(Boolean.TRUE);
            } else {
                requerimiento.setEntrevista(Boolean.FALSE);
            }
            ejbRequerimientos.create(requerimiento, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(RequerimientosCargoBean.class.getName()).log(Level.SEVERE, null, ex);
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
            if (competencia.getMaestro().getCodigo().equals("VEE")) {
                requerimiento.setEntrevista(Boolean.TRUE);
            } else {
                requerimiento.setEntrevista(Boolean.FALSE);
            }
            ejbRequerimientos.edit(requerimiento, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(RequerimientosCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        
        try {
            requerimiento.setActivo(Boolean.FALSE);
            ejbRequerimientos.edit(requerimiento, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(RequerimientosCargoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Requerimientoscargo traer(Integer id) throws ConsultarException {
        return ejbRequerimientos.find(id);
    }

    public SelectItem[] getComboRequerimientos() {
        buscar();
        return Combos.getSelectItems(listaRequerimientos, true);
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
     * @return the requerimiento
     */
    public Requerimientoscargo getRequerimiento() {
        return requerimiento;
    }

    /**
     * @param requerimiento the requerimiento to set
     */
    public void setRequerimiento(Requerimientoscargo requerimiento) {
        this.requerimiento = requerimiento;
    }

    /**
     * @return the listaRequerimientos
     */
    public List<Requerimientoscargo> getListaRequerimientos() {
        return listaRequerimientos;
    }

    /**
     * @param listaRequerimientos the listaRequerimientos to set
     */
    public void setListaRequerimientos(List<Requerimientoscargo> listaRequerimientos) {
        this.listaRequerimientos = listaRequerimientos;
    }

   

    /**
     * @return the competencia
     */
    public Codigos getCompetencia() {
        return competencia;
    }

    /**
     * @param competencia the competencia to set
     */
    public void setCompetencia(Codigos competencia) {
        this.competencia = competencia;
    }

    /**
     * @return the entrevista
     */
    public boolean isEntrevista() {
        return entrevista;
    }

    /**
     * @param entrevista the entrevista to set
     */
    public void setEntrevista(boolean entrevista) {
        this.entrevista = entrevista;
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
