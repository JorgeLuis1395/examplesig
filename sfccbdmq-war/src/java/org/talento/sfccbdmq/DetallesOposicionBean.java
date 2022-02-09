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
import org.beans.sfccbdmq.AreasseleccionFacade;
import org.beans.sfccbdmq.DetallesoposicionFacade;
import org.entidades.sfccbdmq.Areasseleccion;
import org.entidades.sfccbdmq.Detallesoposicion;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "detallesOposicion")
@ViewScoped
public class DetallesOposicionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of DetallesOposicionBean
     */
    public DetallesOposicionBean() {
    }
    private Formulario formulario = new Formulario();
    private Areasseleccion area;
    private Detallesoposicion detalle;
    private List<Detallesoposicion> listaDetalles;
    private String nombre;

    @EJB
    private DetallesoposicionFacade ejbDetalles;
    @EJB
    private AreasseleccionFacade ejbAreas;

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

    public String nuevo() {
        
        if (area == null) {
            MensajesErrores.advertencia("Seleccione un Área");
            return null;
        }
        detalle = new Detallesoposicion();
        detalle.setActivo(Boolean.TRUE);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {
        
        detalle = listaDetalles.get(formulario.getFila().getRowIndex());
        area = detalle.getArea();
        formulario.editar();
        return null;
    }

    public String eliminar() {
       
        detalle = listaDetalles.get(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
        
        if (area == null) {
            MensajesErrores.advertencia("Seleccione Área");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = " o.activo=true and o.area=:area";
            //parametros.put(";where", " o.area.tipo.institucion=:institucion and o.activo=true and o.area=:area");
            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
            parametros.put("area", area);
            parametros.put(";where", where);
            listaDetalles = ejbDetalles.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(DetallesOposicionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        BigDecimal tope = new BigDecimal(100.0000);
        if ((detalle.getNombre() == null || detalle.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
        if ((detalle.getNotamaxima() == null || detalle.getNotamaxima().compareTo(BigDecimal.ZERO) < 1)) {
            MensajesErrores.advertencia("Nota Máxima debe ser valor positivo");
            return true;
        }
        if ((detalle.getOrden() == null)) {
            MensajesErrores.advertencia("Orden es necesario");
            return true;
        }
//        if ((detalle.getDescripcion() == null || detalle.getDescripcion().isEmpty())) {
//            MensajesErrores.advertencia("Descripción es necesario");
//            return true;
//        }
        return false;
    }

    public String insertar() {
        
        if (validar()) {
            return null;
        }
        listaDetalles.add(detalle);
        if (ControlNotaMaxima()) {
            buscar();
            return null;
        }
        try {
            detalle.setArea(area);
            ejbDetalles.create(detalle, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(DetallesOposicionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
        
        if (validar()) {
            return null;
        }
        if (ControlNotaMaxima()) {
            buscar();
            return null;
        }
        try {
            detalle.setArea(area);
            ejbDetalles.edit(detalle, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(DetallesOposicionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
        
        try {
            detalle.setActivo(Boolean.FALSE);
            ejbDetalles.edit(detalle, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AreasSeleccionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Detallesoposicion traer(Integer id) throws ConsultarException {
        return ejbDetalles.find(id);
    }

    public SelectItem[] getComboDetallesOposicion() {
        buscar();
        return Combos.getSelectItems(listaDetalles, true);
    }

    public BigDecimal totalNotaMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaDetalles) {
            Detallesoposicion apsi = (Detallesoposicion) as;
            if (apsi != null && apsi.getNotamaxima() != null) {
                suma = suma.add(apsi.getNotamaxima());
            }
        }
        return suma;
    }

    public boolean ControlNotaMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaDetalles) {
            Detallesoposicion apsi = (Detallesoposicion) as;
            if (apsi != null && apsi.getNotamaxima() != null) {
                suma = suma.add(apsi.getNotamaxima());
            }
        }
        int ref = 0;
        BigDecimal tope = area.getNotamaxima();
        ref = suma.compareTo(tope);
        if (ref >= 1) {
            MensajesErrores.advertencia("La Suma de Notas Máximas no puede exceder " + area.getNotamaxima());
            return true;
        }
        return false;
    }

    public SelectItem[] getComboAreasOposicion() throws ConsultarException {
        Map parametros = new HashMap();
        parametros.put(";where", "o.activo=true and o.tipo.estatus=false");
        List<Areasseleccion> aux = ejbAreas.encontarParametros(parametros);
        return Combos.getSelectItems(aux, true);
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
     * @return the area
     */
    public Areasseleccion getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(Areasseleccion area) {
        this.area = area;
    }

    /**
     * @return the detalle
     */
    public Detallesoposicion getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(Detallesoposicion detalle) {
        this.detalle = detalle;
    }

    /**
     * @return the listaDetalles
     */
    public List<Detallesoposicion> getListaDetalles() {
        return listaDetalles;
    }

    /**
     * @param listaDetalles the listaDetalles to set
     */
    public void setListaDetalles(List<Detallesoposicion> listaDetalles) {
        this.listaDetalles = listaDetalles;
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
