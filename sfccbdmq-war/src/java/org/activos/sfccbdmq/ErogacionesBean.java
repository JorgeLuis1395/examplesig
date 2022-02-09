/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

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
import org.beans.sfccbdmq.ErogacionesFacade;
import org.entidades.sfccbdmq.Erogaciones;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "erogacionesSfccbdmq")
@ViewScoped
public class ErogacionesBean implements Serializable {

    /**
     * Creates a new instance of ErogacionesBean
     */
    public ErogacionesBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<Erogaciones> erogaciones;
    private Erogaciones erogacion;
    private String descripcion;
    @EJB
    private ErogacionesFacade ejbErogacion;
    private Perfiles perfil;

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
        if (redirect==null){
            return;
        }
        String nombreForma = "ErogacionesVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            parametrosSeguridad.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.cerraSession();
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

    /**
     * @return the erogaciones
     */
    public List<Erogaciones> getErogaciones() {
        return erogaciones;
    }

    /**
     * @param erogaciones the erogaciones to set
     */
    public void setErogaciones(List<Erogaciones> erogaciones) {
        this.erogaciones = erogaciones;
    }

    /**
     * @return the 
     */
    public Erogaciones getErogacion() {
        return erogacion;
    }

    /**
     * @param erogacion the erogacion to set
     */
    public void setErogacion(Erogaciones erogacion) {
        this.erogacion = erogacion;
    }

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//        }
        erogacion = new Erogaciones();
        erogacion.setVidautil(0);
        erogacion.setValor(new BigDecimal(0));
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        erogacion = erogaciones.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        erogacion = erogaciones.get(formulario.getFila().getRowIndex());
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
//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            if (((descripcion==null) || (descripcion.isEmpty()))){
                parametros.put(";where", "upper(o.descripcion) like :descripcion");
                parametros.put("descripcion", descripcion.toUpperCase()+"%");
            }
            erogaciones = ejbErogacion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ErogacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((erogacion.getDescripcion()== null) || (erogacion.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario decripción");
            return true;
        }
//        if ((erogacion.getValor()== null) || (erogacion.getValor().doubleValue()<=0)) {
//            MensajesErrores.advertencia("Es necesario valor de la erogación");
//            return true;
//        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbErogacion.create(erogacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ErogacionesBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbErogacion.edit(erogacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ErogacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbErogacion.remove(erogacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ErogacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Erogaciones traer(Integer id) throws ConsultarException {
        return ejbErogacion.find(id);
    }

    public SelectItem[] getComboErogacion() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            parametros.put(";where", "o.activo is null");
            erogaciones = ejbErogacion.encontarParametros(parametros);
            return Combos.getSelectItems(erogaciones, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ErogacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboErogacionEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.descripcion");
            parametros.put(";where", "o.activo is null");
            erogaciones = ejbErogacion.encontarParametros(parametros);
            return Combos.getSelectItems(erogaciones, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(ErogacionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the parametrosSeguridad
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
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
