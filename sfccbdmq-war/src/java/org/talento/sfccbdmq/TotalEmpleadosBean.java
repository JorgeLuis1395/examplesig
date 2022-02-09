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
import java.text.SimpleDateFormat;
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
import org.beans.sfccbdmq.CabeceraempleadosFacade;
import org.beans.sfccbdmq.CabecerasrrhhFacade;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.entidades.sfccbdmq.Cabeceraempleados;
import org.entidades.sfccbdmq.Cabecerasrrhh;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "totalEmpleados")
@ViewScoped
public class TotalEmpleadosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Empleados> empleados;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EntidadesFacade ejbEntidad;

    @EJB
    private CabecerasrrhhFacade ejbCabeceras;
    @EJB
    private CabeceraempleadosFacade ejbCabecerasempleados;
    private List<Cabecerasrrhh> listaCabecerarrhh;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    private Formulario formulario = new Formulario();
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Perfiles perfil;

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
        String nombreForma = "TotalEmpleadosVista";
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
        Map parametros = new HashMap();
        parametros.put(";orden", "o.entidad.apellidos");
        parametros.put(";where", "o.activo=true");
        try {
            setEmpleados(ejbEmpleados.encontarParametros(parametros));
            parametros = new HashMap();
            parametros.put(";orden", "o.texto");
            listaCabecerarrhh = ejbCabeceras.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TotalEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // fin perfiles

    /**
     * Creates a new instance of EmpleadosBean
     */
    public TotalEmpleadosBean() {

    }

    public String buscar() {

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

    /**
     * @return the empleados
     */
    public List<Empleados> getEmpleados() {
        return empleados;
    }

    /**
     * @param empleados the empleados to set
     */
    public void setEmpleados(List<Empleados> empleados) {
        this.empleados = empleados;
    }

    /**
     * @return the listaCabecerarrhh
     */
    public List<Cabecerasrrhh> getListaCabecerarrhh() {
        return listaCabecerarrhh;
    }

    /**
     * @param listaCabecerarrhh the listaCabecerarrhh to set
     */
    public void setListaCabecerarrhh(List<Cabecerasrrhh> listaCabecerarrhh) {
        this.listaCabecerarrhh = listaCabecerarrhh;
    }

    public String getDato(Cabecerasrrhh c, Empleados e) {
        String dato = "";
        Map parametros = new HashMap();
        parametros.put(";where", "o.empleado=:empleado and o.cabecera=:cabecera");
        parametros.put("cabecera", c);
        parametros.put("empleado", e);
        try {
            List<Cabeceraempleados> cl = ejbCabecerasempleados.encontarParametros(parametros);
            for (Cabeceraempleados ce : cl) {
                if (ce.getValorfecha()!=null){
                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
                    return sdf.format(ce.getValorfecha());
                }
                if (ce.getValortexto()!=null){
                    return ce.getValortexto();
                }
                if (ce.getValornumerico()!=null){
                    return ce.getValornumerico().toString();
                }
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(TotalEmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dato;
    }
}
