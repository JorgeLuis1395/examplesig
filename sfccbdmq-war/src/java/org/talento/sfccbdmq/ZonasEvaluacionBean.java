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
import org.beans.sfccbdmq.PerspectivasFacade;
import org.beans.sfccbdmq.ZonasevaluacionFacade;
import org.entidades.sfccbdmq.Cargos;
import org.entidades.sfccbdmq.Oficinas;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Perspectivas;
import org.entidades.sfccbdmq.Zonasevaluacion;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author luisfernando
 */
@ManagedBean(name = "zonasEvaluacion")
@ViewScoped
public class ZonasEvaluacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    

    private Formulario formulario = new Formulario();
    private Zonasevaluacion zonaevaluacion;
    private List<Zonasevaluacion> listaZonasEvaluacion;

    private Oficinas area;
    private Cargos cargo;

    @EJB
    private ZonasevaluacionFacade ejbZonasevaluacion;
    @EJB
    private PerspectivasFacade ejbPerspectivas;
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
        String nombreForma = "OficinasVista";
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
    /**
     * Creates a new instance of ZonasEvaluacionBean
     */
    public ZonasEvaluacionBean() {
    }

    public String crear() {
        

        zonaevaluacion = new Zonasevaluacion();
        zonaevaluacion.setActivo(Boolean.TRUE);
        formulario.insertar();
        buscar();
        return null;

    }

    public String buscar() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true");
        try {
            listaZonasEvaluacion = ejbZonasevaluacion.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ZonasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String modificar() {
        
        zonaevaluacion = listaZonasEvaluacion.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formulario.editar();
        return null;
    }

    public String eliminar() {
        
        zonaevaluacion = listaZonasEvaluacion.get(formulario.getFila().getRowIndex());
        formulario.setIndiceFila(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }

    public String borrar() {
        zonaevaluacion.setActivo(Boolean.FALSE);
        try {
            ejbZonasevaluacion.edit(zonaevaluacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ZonasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    private boolean ponderacionMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Zonasevaluacion ze : listaZonasEvaluacion) {
            suma = suma.add(ze.getPonderacion());
        }
        int ref;
        BigDecimal tope = BigDecimal.valueOf(100.00);
        ref = suma.compareTo(tope);
        if (ref >= 1) {
            MensajesErrores.advertencia("No puede exceder " + tope);
            return true;
        }
        return false;
    }

    private boolean validar() {
        if (zonaevaluacion.getZonaevaluacion() == null || zonaevaluacion.getZonaevaluacion().isEmpty()) {
            MensajesErrores.advertencia("Zona de Evaluación necesaria");
            return true;
        }
        if (zonaevaluacion.getPonderacion() == null || zonaevaluacion.getPonderacion().compareTo(BigDecimal.ZERO) < 1) {
            MensajesErrores.advertencia("La ponderación debe ser un valor positivo");
            return true;
        }

        return ponderacionMaxima();
    }

    public String insertar() {
        listaZonasEvaluacion.add(zonaevaluacion);
        if (validar()) {
            buscar();
            return null;
        }
        try {
            ejbZonasevaluacion.create(zonaevaluacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ZonasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        listaZonasEvaluacion.set(formulario.getIndiceFila(), zonaevaluacion);
        if (validar()) {
            return null;
        }
        try {
            ejbZonasevaluacion.edit(zonaevaluacion, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(ZonasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public SelectItem[] getComboPerspectivas() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", " o.cargoevaluado=:cargo and o.activo = true");
            parametros.put("cargo", cargo);
            List<Perspectivas> lp = ejbPerspectivas.encontarParametros(parametros);
            return Combos.getSelectItems(lp, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(ZonasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SelectItem[] getComboZonasEvaluacion() {
        try {
            Map parametros = new HashMap();
            parametros.put(";where", "o.activo=true");

            listaZonasEvaluacion = ejbZonasevaluacion.encontarParametros(parametros);

            return Combos.getSelectItems(listaZonasEvaluacion, true);
        } catch (ConsultarException ex) {
            Logger.getLogger(ZonasEvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigDecimal getTotal() {
        BigDecimal retorno = new BigDecimal(0);
        for (Zonasevaluacion ze : listaZonasEvaluacion) {
            retorno = retorno.add(ze.getPonderacion());
        }
        return retorno;
    }

    public Zonasevaluacion traer(Integer id) throws ConsultarException {
        return ejbZonasevaluacion.find(id);
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
     * @return the zonaevaluacion
     */
    public Zonasevaluacion getZonaevaluacion() {
        return zonaevaluacion;
    }

    /**
     * @param zonaevaluacion the zonaevaluacion to set
     */
    public void setZonaevaluacion(Zonasevaluacion zonaevaluacion) {
        this.zonaevaluacion = zonaevaluacion;
    }

    /**
     * @return the listaZonasEvaluacion
     */
    public List<Zonasevaluacion> getListaZonasEvaluacion() {
        return listaZonasEvaluacion;
    }

    /**
     * @param listaZonasEvaluacion the listaZonasEvaluacion to set
     */
    public void setListaZonasEvaluacion(List<Zonasevaluacion> listaZonasEvaluacion) {
        this.listaZonasEvaluacion = listaZonasEvaluacion;
    }

    /**
     * @return the area
     */
    public Oficinas getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(Oficinas area) {
        this.area = area;
    }

    /**
     * @return the cargo
     */
    public Cargos getCargo() {
        return cargo;
    }

    /**
     * @param cargo the cargo to set
     */
    public void setCargo(Cargos cargo) {
        this.cargo = cargo;
    }

    

}
