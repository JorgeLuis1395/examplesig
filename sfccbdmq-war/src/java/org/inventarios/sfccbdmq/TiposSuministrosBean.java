/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

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
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.TiposuministrosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposuministros;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "tiposSuministrosSfccbdmq")
@ViewScoped
public class TiposSuministrosBean implements Serializable {

    /**
     * Creates a new instance of TiposuministrosBean
     */
    public TiposSuministrosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;

    private Formulario formulario = new Formulario();
    private List<Tiposuministros> tipos;
    private Tiposuministros tipo;
    private Codigos familiaSuministros;
    @EJB
    private TiposuministrosFacade ejbTipo;
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
        String empleadoString = (String) params.get("x");
//        if (empleadoString != null) {
//            return;
//        }
//        if (redirect == null) {
//            return;
//        }
        String nombreForma = "TiposSuministrosVista";
//        if (perfil == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.logout();
//        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfil));
//        if (this.getPerfil() == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.logout();
//        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(parametrosSeguridad.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                parametrosSeguridad.logout();
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
     * @return the tipos
     */
    public List<Tiposuministros> getTiposuministros() {
        return tipos;
    }

    /**
     * @param tipos the tipos to set
     */
    public void setTiposuministros(List<Tiposuministros> tipos) {
        this.tipos = tipos;
    }

    /**
     * @return the
     */
    public Tiposuministros getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposuministros tipo) {
        this.tipo = tipo;
    }

    // colocamos los metodos en verbo
    public String crear() {
        tipo = new Tiposuministros();
        formulario.insertar();
        return null;
    }

    public String modificar(Tiposuministros tipo) {
//        tipo = tipos.get(formulario.getFila().getRowIndex());
        this.tipo = tipo;
        formulario.editar();
        return null;
    }

    public String eliminar(Tiposuministros tipo) {
//        tipo = tipos.get(formulario.getFila().getRowIndex());
        this.tipo = tipo;
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
            parametros.put(";orden", "o.codigo");
            tipos = ejbTipo.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((tipo.getCodigo() == null) || (tipo.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código");
            return true;
        }
        if ((tipo.getNombre() == null) || (tipo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if ((tipo.getCuenta() == null) || (tipo.getCuenta().isEmpty())) {
            MensajesErrores.advertencia("Es necesario cuenta");
            return true;
        }
        // ver si código ya existe
        if (formulario.isNuevo()) {

            Map parametros = new HashMap();
            parametros.put(";where", "o.codigo=:codigo");
            parametros.put("codigo", tipo.getCodigo());
            try {
                int total = ejbTipo.contar(parametros);
                if (total > 0) {
                    MensajesErrores.advertencia("Ya existe tipo con el código : " + tipo.getCodigo());
                }
            } catch (ConsultarException ex) {
                Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbTipo.create(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbTipo.edit(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbTipo.remove(tipo, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Tiposuministros traer(Integer id) throws ConsultarException {
        return ejbTipo.find(id);
    }

    public SelectItem[] getComboTipo() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTipoEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTipoFamiliaEspacio() {
        if (familiaSuministros == null) {
            return null;
        }
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.familia=:familia");
            parametros.put("familia", familiaSuministros);
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboTipoFamilia() {
        if (familiaSuministros == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.familia=:familia");
            parametros.put("familia", familiaSuministros);
            tipos = ejbTipo.encontarParametros(parametros);
            return Combos.getSelectItems(tipos, false);
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

    /**
     * @return the familiaSuministros
     */
    public Codigos getFamiliaSuministros() {
        return familiaSuministros;
    }

    /**
     * @param familiaSuministros the familiaSuministros to set
     */
    public void setFamiliaSuministros(Codigos familiaSuministros) {
        this.familiaSuministros = familiaSuministros;
    }

    public Tiposuministros traerCodigo(Codigos familia, String codigo) {
        if (familia == null) {
            return null;
        }
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.familia=:familia and o.codigo=:codigo");
            parametros.put("familia", familia);
            parametros.put("codigo", codigo);
            List<Tiposuministros> listaTipos = ejbTipo.encontarParametros(parametros);
            for (Tiposuministros t : listaTipos) {
                return t;
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TiposSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}