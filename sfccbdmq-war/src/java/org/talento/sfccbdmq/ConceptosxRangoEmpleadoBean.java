/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
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
import org.beans.sfccbdmq.ConceptoxrangosFacade;
import org.entidades.sfccbdmq.Conceptos;
import org.entidades.sfccbdmq.Conceptoxrangos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "conceptosxRangoEmpleado")
@ViewScoped
public class ConceptosxRangoEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    
//    @ManagedProperty(value = "#{imagenesSfccbdmq}")
//    private ImagenesBean imagenesBean;
    private List<Conceptoxrangos> listaConceptos;
    private Conceptoxrangos concepto;
    private Conceptos conceptoBuscar;
    private Formulario formulario = new Formulario();
    @EJB
    private ConceptoxrangosFacade ejbConceptos;
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
        String nombreForma = "ConceptosxRangosEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
//            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
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
     * Creates a new instance of ConceptosEmpleadoBean
     */
    public ConceptosxRangoEmpleadoBean() {
    }

    public String buscar() {

        if (conceptoBuscar==null){
            MensajesErrores.advertencia("Seleccione un cargo");
            return null;
        }

        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.inicial desc,o.final1 asc");
            parametros.put(";where", "o.concepto=:concepto");
            parametros.put("concepto", conceptoBuscar);
            setListaConceptos(ejbConceptos.encontarParametros(parametros));
        } catch (ConsultarException ex) {
            Logger.getLogger(ConceptosxRangoEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
       if (conceptoBuscar==null){
            MensajesErrores.advertencia("Seleccione un cargo");
            return null;
        }
        concepto = new Conceptoxrangos();
        concepto.setConcepto(conceptoBuscar);
        getFormulario().insertar();
        return null;
    }

    public String modifica(Conceptoxrangos concepto) {
        this.concepto = concepto;
        getFormulario().editar();
        return null;
    }

    
    public String borra(Conceptoxrangos concepto) {
        this.concepto = concepto;
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {
         if ((concepto.getInicial()== null) ) {
            MensajesErrores.advertencia("Rango inicial es obligatorio");
            return true;
        }
        if (concepto.getFinal1()== null ) {
            MensajesErrores.advertencia("Rango final es obligatorio");
            return true;
        }
        if (concepto.getFinal1()<=concepto.getInicial() ) {
            MensajesErrores.advertencia("Rango final debe ser mayor que rango inicial");
            return true;
        }

        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbConceptos.create(concepto, parametrosSeguridad.getLogueado().getUserid());
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
            ejbConceptos.edit(concepto, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String eliminar() {
        try {

            ejbConceptos.remove(concepto, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    

    /**
     * @return the listaConceptos
     */
    public List<Conceptoxrangos> getListaConceptos() {
        return listaConceptos;
    }

    /**
     * @param listaConceptos the listaConceptos to set
     */
    public void setListaConceptos(List<Conceptoxrangos> listaConceptos) {
        this.listaConceptos = listaConceptos;
    }

    /**
     * @return the concepto
     */
    public Conceptoxrangos getConcepto() {
        return concepto;
    }

    /**
     * @param concepto the concepto to set
     */
    public void setConcepto(Conceptoxrangos concepto) {
        this.concepto = concepto;
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
     * @return the conceptoBuscar
     */
    public Conceptos getConceptoBuscar() {
        return conceptoBuscar;
    }

    /**
     * @param conceptoBuscar the conceptoBuscar to set
     */
    public void setConceptoBuscar(Conceptos conceptoBuscar) {
        this.conceptoBuscar = conceptoBuscar;
    }

}
