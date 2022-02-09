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
import org.beans.sfccbdmq.ActivosFacade;
import org.beans.sfccbdmq.GrupoactivosFacade;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "gruposActivosSfccbdmq")
@ViewScoped
public class GrupoActivosBean implements Serializable {

    /**
     * Creates a new instance of GrupoactivosBean
     */
    public GrupoActivosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<Grupoactivos> grupos;
    private Grupoactivos grupo;
    @EJB
    private GrupoactivosFacade ejbGrupo;
    @EJB
    private ActivosFacade ejbActivos;
   
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
        if (redirect == null) {
            return;
        }
        String nombreForma = "GruposVista";
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
     * @return the grupos
     */
    public List<Grupoactivos> getGrupos() {
        return grupos;
    }

    /**
     * @param grupos the grupos to set
     */
    public void setGrupos(List<Grupoactivos> grupos) {
        this.grupos = grupos;
    }

    /**
     * @return the
     */
    public Grupoactivos getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupoactivos grupo) {
        this.grupo = grupo;
    }

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//        }
        grupo = new Grupoactivos();
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        grupo = grupos.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        grupo = grupos.get(formulario.getFila().getRowIndex());
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
            parametros.put(";orden", "o.nombre");
            grupos = ejbGrupo.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(GrupoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((grupo.getCodigo() == null) || (grupo.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Código");
            return true;
        }
        if ((grupo.getDescripcion() == null) || (grupo.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Descripción");
            return true;
        }
        if ((grupo.getNombre() == null) || (grupo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if ((grupo.getIniciodepreciccion() == null) || (grupo.getIniciodepreciccion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario cuenta de inicio depreciación");
            return true;
        }
        if ((grupo.getFindepreciacion() == null) || (grupo.getFindepreciacion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario cuenta de fin depreciación");
            return true;
        }

        if (grupo.getDepreciable() != null) {
            if (grupo.getDepreciable()) {
                if ((grupo.getValorresidual() == null) || (grupo.getValorresidual().doubleValue() <= 0)) {
                    MensajesErrores.advertencia("Es necesario valor residual");
                    return true;
                }
                if ((grupo.getVidautil() == null) || (grupo.getVidautil() <= 0)) {
                    MensajesErrores.advertencia("Es necesario vida útil");
                    return true;
                }
                if (grupo.getMetododepreciacion() == null) {
                    MensajesErrores.advertencia("Es necesario Método de depreciación");
                    return true;
                }
            }
        }

        if (grupo.getTipo() == null) {
            MensajesErrores.advertencia("Es necesario Tipo de Activo");
            return true;
        }
//        if (todos) {
//            grupo.setMudulo(null);
//        } else {
//            grupo.setMudulo(Combos.getModuloStr());
//        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {
            grupo.setSecuencia(0);
            ejbGrupo.create(grupo, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(GrupoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbGrupo.edit(grupo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(GrupoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            // ver si existen activos
            Map parametros = new HashMap();
            parametros.put(";where", "o.grupo=:grupo");
            parametros.put("grupo", grupo);
            int total = ejbActivos.contar(parametros);
            if (total > 0) {
                MensajesErrores.advertencia("No es posible borrar existen activos en este grupo");
                return null;
            }
            ejbGrupo.remove(grupo, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(GrupoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Grupoactivos traer(Integer id) throws ConsultarException {
        return ejbGrupo.find(id);
    }

    public SelectItem[] getComboGrupo() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            return Combos.getSelectItems(ejbGrupo.encontarParametros(parametros), false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(GrupoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboGrupoEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
//            grupos = ejbGrupo.encontarParametros(parametros);
            return Combos.getSelectItems(ejbGrupo.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(GrupoActivosBean.class.getName()).log(Level.SEVERE, null, ex);
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
}
