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
import org.beans.sfccbdmq.TiposseleccionFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposseleccion;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */


@ManagedBean(name = "tiposSelecciones")
@ViewScoped
public class TiposSeleccionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TiposSeleccionesBean
     */
    public TiposSeleccionesBean() {
    }

    private Formulario formulario = new Formulario();
    private Tiposseleccion tipo;
    private List<Tiposseleccion> listaTipos;
    private String nombre;

    @EJB
    private TiposseleccionFacade ejbTipos;

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
        String nombreForma = "TiposSeleccionVistaVista";
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

        tipo = new Tiposseleccion();
        tipo.setActivo(Boolean.TRUE);
        tipo.setEstatus(Boolean.FALSE);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {
        tipo = listaTipos.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        tipo = listaTipos.get(formulario.getFila().getRowIndex());
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
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            parametros.put(";where", where);
            listaTipos = ejbTipos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(Tiposseleccion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        BigDecimal tope = new BigDecimal(100.0000);
        if ((tipo.getNombre() == null || tipo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if ((tipo.getPonderacion() == null || tipo.getPonderacion().compareTo(BigDecimal.ZERO) < 1)) {
            MensajesErrores.advertencia("Ponderación debe ser valor positivo");
            return true;
        }
        if (tipo.getPonderacion().compareTo(tope) > 0) {
            MensajesErrores.advertencia("Ponderación no debe exceder el 100%");
            return true;
        }
        if ((tipo.getNotamaxima() == null || tipo.getNotamaxima().compareTo(BigDecimal.ZERO) < 1)) {
            MensajesErrores.advertencia("Nota Máxima debe ser valor positivo");
            return true;
        }
        if ((tipo.getNotaminima() == null || tipo.getNotaminima().compareTo(BigDecimal.ZERO) < 1)) {
            MensajesErrores.advertencia("Nota Mínima debe ser valor positivo");
            return true;
        }
        if (tipo.getNotaminima().compareTo(tipo.getNotamaxima()) > 0) {
            MensajesErrores.advertencia("Nota Mínima no debe ser mayor a Nota Máxima");
            return true;
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        listaTipos.add(tipo);
        if (ControlPorcentaje()) {
            buscar();
            return null;
        }
        try {
            ejbTipos.create(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSeleccionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {

        if (validar()) {
            return null;
        }
        if (ControlPorcentaje()) {
            buscar();
            return null;
        }
        try {
            ejbTipos.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSeleccionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            tipo.setActivo(Boolean.FALSE);
            ejbTipos.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSeleccionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Tiposseleccion traer(Integer id) throws ConsultarException {
        return ejbTipos.find(id);
    }

    public SelectItem[] getComboTiposSeleccion() {
        buscar();
        return Combos.getSelectItems(listaTipos, true);
    }

    public BigDecimal totalPromedio() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaTipos) {
            Tiposseleccion apsi = (Tiposseleccion) as;
            if (apsi != null && apsi.getPonderacion() != null) {
                suma = suma.add(apsi.getPonderacion());
            }
        }
        return suma;
    }

    public boolean ControlPorcentaje() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaTipos) {
            Tiposseleccion apsi = (Tiposseleccion) as;
            if (apsi != null && apsi.getPonderacion() != null) {
                suma = suma.add(apsi.getPonderacion());
            }
        }
        int ref = 0;
        BigDecimal tope = new BigDecimal(100.0000);
        ref = suma.compareTo(tope);
        if (ref >= 1) {
            MensajesErrores.advertencia("El porcentaje asignado excede el 100% del total");
            return true;
        }
        return false;
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
     * @return the tipo
     */
    public Tiposseleccion getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposseleccion tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the listaTipos
     */
    public List<Tiposseleccion> getListaTipos() {
        return listaTipos;
    }

    /**
     * @param listaTipos the listaTipos to set
     */
    public void setListaTipos(List<Tiposseleccion> listaTipos) {
        this.listaTipos = listaTipos;
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
