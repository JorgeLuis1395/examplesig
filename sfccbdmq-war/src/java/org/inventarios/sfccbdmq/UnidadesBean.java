/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Combos;
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
import javax.faces.model.SelectItem;
import org.beans.sfccbdmq.UnidadesFacade;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Unidades;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "unidadesSfccbdmq")
@ViewScoped
public class UnidadesBean implements Serializable {

    /**
     * Creates a new instance of UnidadesBean
     */
    public UnidadesBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

    private Formulario formulario = new Formulario();
    private List<Unidades> unidades;
    private Unidades unidad;
    @EJB
    private UnidadesFacade ejbUnidad;
    private Perfiles perfil;
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

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "UnidadesVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
//        }
////        if (nombreForma==null){
////            nombreForma="";
////        }
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
     * @return the unidades
     */
    public List<Unidades> getUnidades() {
        return unidades;
    }

    /**
     * @param unidades the unidades to set
     */
    public void setUnidades(List<Unidades> unidades) {
        this.unidades = unidades;
    }

    /**
     * @return the
     */
    public Unidades getUnidad() {
        return unidad;
    }

    /**
     * @param unidad the unidad to set
     */
    public void setUnidad(Unidades unidad) {
        this.unidad = unidad;
    }

    // colocamos los metodos en verbo
    public String crear() {
        unidad = new Unidades();
        formulario.insertar();
        return null;
    }

    public String nuevohijo() {
        getFormulario().insertar();
        setUnidad(new Unidades());
        Unidades un = getUnidades().get(getFormulario().getFila().getRowIndex());
        unidad.setBase(un);
        return null;
    }

    public String modificar() {
        unidad = unidades.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        unidad = unidades.get(formulario.getFila().getRowIndex());
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
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.equivalencia");
            unidades = ejbUnidad.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(UnidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((unidad.getEquivalencia() == null) || (unidad.getEquivalencia().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if ((unidad.getFactor() == null) || (unidad.getFactor() <= 0)) {
            MensajesErrores.advertencia("Es necesario factor");
            return true;
        }

        // ver si código ya existe
        if (formulario.isNuevo()) {

            Map parametros = new HashMap();
            parametros.put(";where", "o.equivalencia=:equivalencia");
            parametros.put("equivalencia", unidad.getEquivalencia());
            try {
                int total = ejbUnidad.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Ya existe unidad con el código : " + unidad.getEquivalencia());
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(UnidadesBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbUnidad.create(unidad, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(UnidadesBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbUnidad.edit(unidad, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(UnidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbUnidad.remove(unidad, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(UnidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Unidades traer(Integer id) throws ConsultarException {
        return ejbUnidad.find(id);
    }

    public SelectItem[] getCombounidadesf() {
        try {
//            Map parametros = new HashMap();

            Map parametros = new HashMap();
            parametros.put(";orden", "o.equivalencia");
            parametros.put(";where", "o.base is null");
            List<Unidades> ls = ejbUnidad.encontarParametros(parametros);
            return Combos.getSelectItems(ls, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getCombounidades() {
        try {
//            Map parametros = new HashMap();

            Map parametros = new HashMap();
            parametros.put(";orden", "o.equivalencia");
            parametros.put(";where", "o.base is null");
            List<Unidades> ls = ejbUnidad.encontarParametros(parametros);
            return Combos.getSelectItems(ls, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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

}
