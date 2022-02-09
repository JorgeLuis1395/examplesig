/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.contabilidad.sfccbdmq.CuentasBean;
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
import org.beans.sfccbdmq.SubgruposactivosFacade;
import org.entidades.sfccbdmq.Grupoactivos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Subgruposactivos;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "subGruposActivosSfccbdmq")
@ViewScoped
public class SubGruposBean implements Serializable {

    /**
     * Creates a new instance of SubgruposactivosBean
     */
    public SubGruposBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{cuentasSfccbdmq}")
    private CuentasBean cuentasBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<Subgruposactivos> subGrupos;
    private Subgruposactivos subGrupo;
    private Grupoactivos grupo;
    @EJB
    private SubgruposactivosFacade ejbSubGrupo;

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
        String nombreForma = "SubGruposActVista";
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
//                MensajesErrores.fatal("Sin perfil válido, subGrupo invalido");
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
     * @return the subGrupos
     */
    public List<Subgruposactivos> getSubGrupos() {
        return subGrupos;
    }

    /**
     * @param subGrupos the subGrupos to set
     */
    public void setSubGrupos(List<Subgruposactivos> subGrupos) {
        this.subGrupos = subGrupos;
    }

    /**
     * @return the
     */
    public Subgruposactivos getSubGrupo() {
        return subGrupo;
    }

    /**
     * @param subGrupo the subGrupo to set
     */
    public void setSubGrupo(Subgruposactivos subGrupo) {
        this.subGrupo = subGrupo;
    }

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//        }
        if (grupo == null) {
            MensajesErrores.advertencia("Seleccione un grupo primero");
            return null;
        }
        subGrupo = new Subgruposactivos();
        subGrupo.setGrupo(grupo);
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        subGrupo = subGrupos.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        subGrupo = subGrupos.get(formulario.getFila().getRowIndex());
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
        if (grupo == null) {
            MensajesErrores.advertencia("Seleccione un grupo primero");
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.grupo=:grupo");
            parametros.put("grupo", grupo);
            subGrupos = ejbSubGrupo.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {

        if ((subGrupo.getNombre() == null) || (subGrupo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Nombre");
            return true;
        }
        if ((subGrupo.getCodigo() == null) || (subGrupo.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Codigo");
            return true;
        }

        if ((subGrupo.getGrupo() == null)) {
            MensajesErrores.advertencia("Es necesario grupo ");
            return true;
        }
        // ver codigo
        if (formulario.isNuevo()) {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.grupo=:grupo and o.codigo=:codigo");
            parametros.put("grupo", subGrupo.getGrupo());
            parametros.put("codigo", subGrupo.getCodigo());
            try {
                int total = ejbSubGrupo.contar(parametros);
                if ((total>0)) {
                    MensajesErrores.advertencia("Código duplicado");
                    return true;
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        subGrupo.setCuenta(cuentasBean.getCuenta().getCodigo());

        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbSubGrupo.create(subGrupo, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbSubGrupo.edit(subGrupo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {

            ejbSubGrupo.remove(subGrupo, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Subgruposactivos traer(Integer id) throws ConsultarException {
        return ejbSubGrupo.find(id);
    }

    public SelectItem[] getComboSubGrupo() {
        if (grupo == null) {
            return null;
        }
        try {

//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.grupo=:grupo");
            parametros.put("grupo", grupo);
            subGrupos = ejbSubGrupo.encontarParametros(parametros);
            return Combos.getSelectItems(subGrupos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(SubGruposBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the cuentasBean
     */
    public CuentasBean getCuentasBean() {
        return cuentasBean;
    }

    /**
     * @param cuentasBean the cuentasBean to set
     */
    public void setCuentasBean(CuentasBean cuentasBean) {
        this.cuentasBean = cuentasBean;
    }

    /**
     * @return the grupo
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
