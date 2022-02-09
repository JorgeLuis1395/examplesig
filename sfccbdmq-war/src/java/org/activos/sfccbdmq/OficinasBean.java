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
import org.beans.sfccbdmq.OficinasFacade;
import org.entidades.sfccbdmq.Edificios;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Organigrama;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "oficinasSfccbdmq")
@ViewScoped
public class OficinasBean implements Serializable {

    /**
     * Creates a new instance of OficinasBean
     */
    public OficinasBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    private Formulario formulario = new Formulario();
    private List<Oficinas> oficinas;
    private Oficinas oficina;
    @EJB
    private OficinasFacade ejbOficina;
    private Perfiles perfil;
    private Organigrama organigrama;
    private Edificios edificio;
    private Edificios edificio2;

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
        String perfilString = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "OficinasVista";
        if (perfilString == null) {
//            MensajesErrores.fatal("Sin perfil válido");
//            parametrosSeguridad.cerraSession();
            return;
        }
        this.setPerfil(parametrosSeguridad.traerPerfil(perfilString));
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
     * @return the oficinas
     */
    public List<Oficinas> getOficinas() {
        return oficinas;
    }

    /**
     * @param oficinas the oficinas to set
     */
    public void setOficinas(List<Oficinas> oficinas) {
        this.oficinas = oficinas;
    }

    /**
     * @return the
     */
    public Oficinas getOficina() {
        return oficina;
    }

    /**
     * @param oficina the oficina to set
     */
    public void setOficina(Oficinas oficina) {
        this.oficina = oficina;
    }

    // colocamos los metodos en verbo
    public String crear() {
        // se deberia chequear perfil?
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//        }
        oficina = new Oficinas();
        formulario.insertar();
        return null;
    }

    public String modificar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
        oficina = oficinas.get(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//        }
        oficina = oficinas.get(formulario.getFila().getRowIndex());
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
            if (organigrama != null) {
                where += "o.organigrama=:organigrama";
                parametros.put("organigrama", organigrama);
            }
            if (edificio != null) {
                if (!where.isEmpty()) {
                    where += " and ";
                }
                where += "o.edificio=:edificio";
                parametros.put("edificio", edificio);
            }
            parametros.put(";where", where);
            oficinas = ejbOficina.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((oficina.getTelefonos() == null) || (oficina.getTelefonos().isEmpty())) {
            MensajesErrores.advertencia("Es necesario Teléfonos");
            return true;
        }
//        if ((oficina.getCentrocosto()== null) || (oficina.getCentrocosto().isEmpty())) {
//            MensajesErrores.advertencia("Es necesario Centro de Costo");
//            return true;
//        }
        if ((oficina.getParqueaderos() == null)) {
            MensajesErrores.advertencia("Es necesario Número");
            return true;
        }
        if ((oficina.getNombre() == null) || (oficina.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if (oficina.getPiso() == null) {
            MensajesErrores.advertencia("Es necesario Piso");
            return true;
        }
//        if (oficina.getOrganigrama() == null) {
//            MensajesErrores.advertencia("Es necesario organigrama");
//            return true;
//        }
//        if (todos) {
//            oficina.setMudulo(null);
//        } else {
//            oficina.setMudulo(Combos.getModuloStr());
//        }
        return false;
    }

    public String insertar() {

        if (validar()) {
            return null;
        }
        try {

            ejbOficina.create(oficina, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
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
            ejbOficina.edit(oficina, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {

        try {
            ejbOficina.remove(oficina, parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Oficinas traer(Integer id) throws ConsultarException {
        return ejbOficina.find(id);
    }

    public SelectItem[] getComboOficina() {
        try {
            if (edificio == null) {
                return null;
            }
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.edificio=:edificio and o.tipo.parametros='FALSE'");
            parametros.put("edificio", edificio);
            oficinas = ejbOficina.encontarParametros(parametros);
            return Combos.getSelectItems(oficinas, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOficinaBaja() {
        try {
            if (edificio == null) {
                return null;
            }
//            Map parametros = new HashMap();
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.edificio=:edificio and o.tipo.parametros='TRUE'");
            parametros.put("edificio", edificio);
            oficinas = ejbOficina.encontarParametros(parametros);
            return Combos.getSelectItems(oficinas, false);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOficinaEspacio() {
        try {
            if (edificio == null) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.edificio=:edificio and o.tipo.parametros='FALSE'");
            parametros.put("edificio", edificio);
            oficinas = ejbOficina.encontarParametros(parametros);
            return Combos.getSelectItems(oficinas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public SelectItem[] getComboOficinaEspacio2() {
        try {
            if (edificio2 == null) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.edificio=:edificio and o.tipo.parametros='FALSE'");
            parametros.put("edificio", edificio2);
            oficinas = ejbOficina.encontarParametros(parametros);
            return Combos.getSelectItems(oficinas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboOficinaBajaEspacio() {
        try {
            if (edificio == null) {
                return null;
            }
            Map parametros = new HashMap();
            parametros.put(";orden", "o.nombre");
            parametros.put(";where", "o.edificio=:edificio and o.tipo.parametros='TRUE'");
            parametros.put("edificio", edificio);
            oficinas = ejbOficina.encontarParametros(parametros);
            return Combos.getSelectItems(oficinas, true);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(OficinasBean.class.getName()).log(Level.SEVERE, null, ex);
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
     * @return the organigrama
     */
    public Organigrama getOrganigrama() {
        return organigrama;
    }

    /**
     * @param organigrama the organigrama to set
     */
    public void setOrganigrama(Organigrama organigrama) {
        this.organigrama = organigrama;
    }

    /**
     * @return the edificio
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
     * @return the edificio2
     */
    public Edificios getEdificio2() {
        return edificio2;
    }

    /**
     * @param edificio2 the edificio2 to set
     */
    public void setEdificio2(Edificios edificio2) {
        this.edificio2 = edificio2;
    }
}
