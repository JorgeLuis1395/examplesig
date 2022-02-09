/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.contabilidad.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import org.beans.sfccbdmq.VistaAuxiliaresFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.VistaAuxiliares;
import org.errores.sfccbdmq.ConsultarException;
import org.icefaces.ace.event.TextChangeEvent;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "vistaAuxiliaresSfccbdmq")
@ViewScoped
public class VistaAuxiliaresBean {

    /**
     * Creates a new instance of AuxiliaresBean
     */
    public VistaAuxiliaresBean() {
    }
    private VistaAuxiliares auxiliar;
    private String codigo;
    private String nombre;
    private boolean porCodigo=true;
    private List<VistaAuxiliares> auxiliares;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private VistaAuxiliaresFacade ejbAuxiliares;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the auxiliar
     */
    public VistaAuxiliares getAuxiliar() {
        return auxiliar;
    }

    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(VistaAuxiliares auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the auxiliares
     */
    public List<VistaAuxiliares> getAuxiliares() {
        return auxiliares;
    }

    /**
     * @param auxiliares the auxiliares to set
     */
    public void setAuxiliares(List<VistaAuxiliares> auxiliares) {
        this.auxiliares = auxiliares;
    }

    public String buscar() {
       
        Map parametros = new HashMap();
        parametros.put(";orden", "o.codigo desc");
        try {
            auxiliares = ejbAuxiliares.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(VistaAuxiliaresBean.class.getName()).log(Level.SEVERE, null, ex);
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
   public void cambiaCodigo(ValueChangeEvent event) {
        auxiliar = null;
        if (auxiliares==null){
            return;
        }
        String newWord = (String) event.getNewValue();
        for (VistaAuxiliares v : auxiliares) {
            if (v.getCodigo().compareToIgnoreCase(newWord) == 0) {
                auxiliar = v;
            }
        }

    }
   public void cambiaNombre(ValueChangeEvent event) {
        auxiliar = null;
        if (auxiliares==null){
            return;
        }
        String newWord = (String) event.getNewValue();
        for (VistaAuxiliares v : auxiliares) {
            if (v.getNombre().compareToIgnoreCase(newWord) == 0) {
                auxiliar = v;
            }
        }

    }
   public void codigoChangeEventHandler(TextChangeEvent event) {
        auxiliar = null;
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = " upper(o.codigo) like :codigo";
        parametros.put("codigo", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            auxiliares = ejbAuxiliares.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }
   public void nombreChangeEventHandler(TextChangeEvent event) {
        auxiliar = null;
        String codigoBuscar = event.getNewValue() != null ? (String) event.getNewValue() : "";
        Map parametros = new HashMap();
        String where = " upper(o.nombre) like :nombre";
        parametros.put("nombre", codigoBuscar.toUpperCase() + "%");
        parametros.put(";where", where);
        parametros.put(";orden", " o.codigo");
        int total = 15;
        // Contadores
        parametros.put(";inicial", 0);
        parametros.put(";final", total);
        try {
            auxiliares = ejbAuxiliares.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.error(ex.getMessage() + ":" + ex.getCause());
            Logger.getLogger("ERROR").log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * @return the porCodigo
     */
    public boolean isPorCodigo() {
        return porCodigo;
    }

    /**
     * @param porCodigo the porCodigo to set
     */
    public void setPorCodigo(boolean porCodigo) {
        this.porCodigo = porCodigo;
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
    public VistaAuxiliares traerAuxiliar(String codigo){
        Map parametros=new HashMap();
        parametros.put(";where", "o.codigo=:codigo");
        parametros.put("codigo", codigo);
        try {
            List<VistaAuxiliares> lista=ejbAuxiliares.encontarParametros(parametros);
            for (VistaAuxiliares v:lista){
                return v;
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(ParametrosSfccbdmqBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        VistaAuxiliares v=new VistaAuxiliares();
        v.setCodigo(codigo);
        v.setNombre(codigo);
        v.setTipo("N");
        v.setId(0);
        return v;
    }
}
