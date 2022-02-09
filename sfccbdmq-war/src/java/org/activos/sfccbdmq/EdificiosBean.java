/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.activos.sfccbdmq;

import org.seguridad.sfccbdmq.CiudadesBean;
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
import org.beans.sfccbdmq.EdificiosFacade;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "edificiosSfccbdmq")
@ViewScoped
public class EdificiosBean implements Serializable {

    /**
     * Creates a new instance of EdificiosBean
     */
    public EdificiosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{ciudadesSfccbdmq}")
    private CiudadesBean ciudadesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<Edificios> edificios;
    private Edificios edificio;
    @EJB
    private EdificiosFacade ejbEdificio;
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
        String nombreForma = "EdificiosVista";
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
     * @return the edificios
     */
    public List<Edificios> getEdificios() {
        return edificios;
    }

    /**
     * @param edificios the edificios to set
     */
    public void setEdificios(List<Edificios> edificios) {
        this.edificios = edificios;
    }

    /**
     * @return the
     */
    public Edificios getEdificio() {
        return edificio;
    }

    /**
     * @param edificio the edificio to set
     */
    public void setEdificio(Edificios edificio) {
        this.edificio = edificio;
    }

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//        }
        edificio = new Edificios();
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        edificio = edificios.get(formulario.getFila().getRowIndex());
        if (edificio.getCiudad() != null) {
            ciudadesBean.setProvincia(edificio.getCiudad().getProvincia());
        }
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        edificio = edificios.get(formulario.getFila().getRowIndex());
        if (edificio.getCiudad() != null) {
            ciudadesBean.setProvincia(edificio.getCiudad().getProvincia());
        }
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
            String where = "";
            if (ciudadesBean.getProvincia() != null) {
                if (ciudadesBean.getUbicacion() != null) {
                    // pide ciudad
                    where = "o.ciudad=:ciudad";
                    parametros.put("ciudad", ciudadesBean.getUbicacion());
                } else {
                    // pide provincia
                    where = "o.ciudad.provincia=:provincia";
                    parametros.put("provincia", ciudadesBean.getProvincia());
                }
            }
            if (!where.isEmpty()) {
                parametros.put(";where", where);
            }
            edificios = ejbEdificio.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((edificio.getCalleprimaria() == null) || (edificio.getCalleprimaria().isEmpty())) {
            MensajesErrores.advertencia("Es necesario calle primaria");
            return true;
        }
        if ((edificio.getNumero() == null) || (edificio.getNumero().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Número");
            return true;
        }
        if ((edificio.getNombre() == null) || (edificio.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if (edificio.getCiudad() == null) {
            MensajesErrores.advertencia("Es necesario ciudad");
            return true;
        }
//        if (todos) {
//            edificio.setMudulo(null);
//        } else {
//            edificio.setMudulo(Combos.getModuloStr());
//        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbEdificio.create(edificio, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbEdificio.edit(edificio, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbEdificio.remove(edificio, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Edificios traer(Integer id) throws ConsultarException {
        return ejbEdificio.find(id);
    }

    public SelectItem[] getComboEdificio() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.internos=true");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEdificioEspacio() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.internos=true");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public SelectItem[] getComboEdificioEspacioCarapungo() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.nombre='Edificio Carapungo'");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
     public SelectItem[] getComboEdificioTodo() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
//            parametros.put(";where", "o.internos=true");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEdificioEspacioTodo() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
//            parametros.put(";where", "o.internos=true");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public List<Edificios> getComboEdificioLista() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.internos=true");
            edificios = ejbEdificio.encontarParametros(parametros);

            return edificios;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEdificioExterno() {
        try {
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.internos=false");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboEdificioEspacioExterno() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.internos=false");
            edificios = ejbEdificio.encontarParametros(parametros);
            return Combos.getSelectItems(edificios, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Edificios> getComboEdificioListaExterno() {
        try {
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.internos=false");
            edificios = ejbEdificio.encontarParametros(parametros);

            return edificios;
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EdificiosBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the ciudadesBean
     */
    public CiudadesBean getCiudadesBean() {
        return ciudadesBean;
    }

    /**
     * @param ciudadesBean the ciudadesBean to set
     */
    public void setCiudadesBean(CiudadesBean ciudadesBean) {
        this.ciudadesBean = ciudadesBean;
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
