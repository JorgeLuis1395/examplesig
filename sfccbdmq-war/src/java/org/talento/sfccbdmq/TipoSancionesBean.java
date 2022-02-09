/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

;

import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.auxiliares.sfccbdmq.Combos;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.seguridad.sfccbdmq.ConfiguracionBean;
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
import org.beans.sfccbdmq.HistorialsancionesFacade;
import org.beans.sfccbdmq.TiposancionesFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposanciones;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */


@ManagedBean(name = "tipoSancion")
@ViewScoped
public class TipoSancionesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of TipoSancionBean
     */
    public TipoSancionesBean() {
    }

    private Formulario formulario = new Formulario();
    private Tiposanciones sancion;
    private List<Tiposanciones> listaSanciones;
    private String nombre;
    private Codigos tipo;

    @EJB
    private TiposancionesFacade ejbTsanciones;
    @EJB
    private HistorialsancionesFacade ejbHistorial;

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
        String nombreForma = "TipoSancionVista";
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

        sancion = new Tiposanciones();
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {
        sancion = listaSanciones.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        sancion = listaSanciones.get(formulario.getFila().getRowIndex());
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
            String where = "";

            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                if (!where.isEmpty()){
                    where+=" and ";
                }
                where += " upper(o.nombre) like:nombre";
                parametros.put("nombre",  nombre.toUpperCase() + "%");
            }
            //DURACIÓN
            if (tipo != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += " o.tipo =:tipo";
                parametros.put("tipo", tipo);
            }

            parametros.put(";where", where);
            parametros.put(";orden", "o.nombre asc");
            listaSanciones = ejbTsanciones.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(TipoSancionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {

        if ((sancion.getNombre() == null || sancion.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if (sancion.getTipo() == null) {
            MensajesErrores.advertencia("Tipo es necesario");
            return true;
        }
        if (sancion.getEspecunaria()) {
            if ((sancion.getSancion() == null) || (sancion.getSancion().doubleValue()<=0)) {
                MensajesErrores.advertencia("Tipo es % valor de la sanción");
                return true;
            }
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            ejbTsanciones.create(sancion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoSancionesBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbTsanciones.edit(sancion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoSancionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        try {
            Map parametros=new HashMap();
            parametros.put(";where", "o.tipo=:tipo");
            parametros.put("tipo", sancion);
            int total=ejbHistorial.contar(parametros);
            if (total>0){
                MensajesErrores.advertencia("Ya existe movimientos no es posible borrar");
                return null;
            }
            ejbTsanciones.remove(sancion, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException | ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(TipoSancionesBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Tiposanciones traer(Integer id) throws ConsultarException {
        return ejbTsanciones.find(id);
    }

    public SelectItem[] getComboTiposSancion() {
        buscar();
        return Combos.getSelectItems(listaSanciones, true);
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
     * @return the sancion
     */
    public Tiposanciones getSancion() {
        return sancion;
    }

    /**
     * @param sancion the sancion to set
     */
    public void setSancion(Tiposanciones sancion) {
        this.sancion = sancion;
    }

    /**
     * @return the listaSanciones
     */
    public List<Tiposanciones> getListaSanciones() {
        return listaSanciones;
    }

    /**
     * @param listaSanciones the listaSanciones to set
     */
    public void setListaSanciones(List<Tiposanciones> listaSanciones) {
        this.listaSanciones = listaSanciones;
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

    /**
     * @return the tipo
     */
    public Codigos getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Codigos tipo) {
        this.tipo = tipo;
    }

}
