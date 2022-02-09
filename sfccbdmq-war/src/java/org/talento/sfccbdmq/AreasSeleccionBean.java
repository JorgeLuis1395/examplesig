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
import org.entidades.sfccbdmq.Areasseleccion;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Tiposseleccion;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edison
 */
@ManagedBean(name = "areasSeleccion")
@ViewScoped
public class AreasSeleccionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of AreasSeleccionBean
     */
    public AreasSeleccionBean() {
    }

    private Formulario formulario = new Formulario();
    private Tiposseleccion tipo;
    private Areasseleccion area;
    private List<Areasseleccion> listaAreas;
    private String nombre;

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
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "AreaSeleccionVista";
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
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }

        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione un Tipo");
            return null;
        }
        area = new Areasseleccion();
        area.setActivo(Boolean.TRUE);
        area.setPrueba(Boolean.FALSE);
        formulario.insertar();
        buscar();
        return null;
    }

    public String modificar() {

        area = listaAreas.get(formulario.getFila().getRowIndex());
        tipo = area.getTipo();
        formulario.editar();
        return null;
    }

    public String eliminar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        area = listaAreas.get(formulario.getFila().getRowIndex());
        formulario.eliminar();
        return null;
    }

    public String cancelar() {
        formulario.cancelar();
        buscar();
        return null;
    }

    public String buscar() {
//        if (!menusBean.getPerfil().getConsulta()) {
//            MensajesErrores.advertencia("No tiene autorización para consultar");
//            return null;
//        }
//        if (institucion == null) {
//            MensajesErrores.advertencia("Seleccione Institución");
//            return null;
//        }
        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione Tipo");
            return null;
        }
        try {
            Map parametros = new HashMap();
            String where = "  o.activo=true and o.tipo=:tipo";
            //parametros.put(";where", " o.tipo.institucion=:institucion and o.activo=true and o.tipo=:tipo");
            //NOMBRE
            if (!((nombre == null) || (nombre.isEmpty()))) {
                where += " and upper(o.nombre) like:nombre";
                parametros.put("nombre", "%" + nombre.toUpperCase() + "%");
            }
//            parametros.put("institucion", institucion);
            parametros.put("tipo", tipo);
            parametros.put(";where", where);
            parametros.put(";orden", "o.orden asc");
            listaAreas = ejbAreas.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + " : " + ex.getCause());
            Logger.getLogger(AreasSeleccionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean validar() {
        BigDecimal tope = new BigDecimal(100.0000);
        if ((area.getNombre() == null || area.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Nombre es necesario");
            return true;
        }
//        if ((area.getPonderacion() == null || area.getPonderacion().compareTo(BigDecimal.ZERO) < 1)) {
//            MensajesErrores.advertencia("Ponderación debe ser valor positivo");
//            return true;
//        }
//        if (area.getPonderacion().compareTo(tope) > 0) {
//            MensajesErrores.advertencia("Ponderación no debe exceder el 100%");
//            return true;
//        }
        if ((area.getNotamaxima() == null || area.getNotamaxima().compareTo(BigDecimal.ZERO) < 1)) {
            MensajesErrores.advertencia("Ponderación debe ser valor positivo");
            return true;
        }
        if (tipo == null) {
            MensajesErrores.advertencia("Seleccione un tipo");
            return true;
        }
        if (area.getOrden() == null) {
            MensajesErrores.advertencia("Indique el Orden");
            return true;
        }
        return false;
    }

    public String insertar() {
//        if (!menusBean.getPerfil().getNuevo()) {
//            MensajesErrores.advertencia("No tiene autorización para crear un registro");
//            return null;
//        }
        if (validar()) {
            return null;
        }
        listaAreas.add(area);
        if (ControlNotaMaxima()) {
            buscar();
            return null;
        }
//        if (ControlPorcentaje()) {
//            buscar();
//            return null;
//        }
        try {
            area.setTipo(tipo);
            ejbAreas.create(area, parametrosSeguridad.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AreasSeleccionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String grabar() {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//            return null;
//        }
        if (validar()) {
            return null;
        }
        if (ControlNotaMaxima()) {
            buscar();
            return null;
        }
//        if (ControlPorcentaje()) {
//            buscar();
//            return null;
//        }
        try {
            area.setTipo(tipo);
            ejbAreas.edit(area, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AreasSeleccionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public String borrar() {
//        if (!menusBean.getPerfil().getBorrado()) {
//            MensajesErrores.advertencia("No tiene autorización para borrar un registro");
//            return null;
//        }
        try {
            area.setActivo(Boolean.FALSE);
            ejbAreas.edit(area, parametrosSeguridad.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(AreasSeleccionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }

    public Areasseleccion traer(Integer id) throws ConsultarException {
        return ejbAreas.find(id);
    }

    public SelectItem[] getComboAreasSeleccion() {
        buscar();
        return Combos.getSelectItems(listaAreas, true);
    }

    public BigDecimal totalNotaMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaAreas) {
            Areasseleccion apsi = (Areasseleccion) as;
            if (apsi != null && apsi.getNotamaxima() != null) {
                suma = suma.add(apsi.getNotamaxima());
            }
        }
        return suma;
    }

    public boolean ControlNotaMaxima() {
        BigDecimal suma = new BigDecimal(0);
        for (Object as : listaAreas) {
            Areasseleccion apsi = (Areasseleccion) as;
            if (apsi != null && apsi.getNotamaxima() != null) {
                suma = suma.add(apsi.getNotamaxima());
            }
        }
        int ref = 0;
        BigDecimal tope = tipo.getNotamaxima();
        ref = suma.compareTo(tope);
        if (ref >= 1) {
            MensajesErrores.advertencia("La Suma de Notas Máximas no puede exceder " + ref);
            return true;
        }
        return false;
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
     * @return the listaAreas
     */
    public List<Areasseleccion> getListaAreas() {
        return listaAreas;
    }

    /**
     * @param listaAreas the listaAreas to set
     */
    public void setListaAreas(List<Areasseleccion> listaAreas) {
        this.listaAreas = listaAreas;
    }

    /**
     * @return the tipo
     */
    public Tiposseleccion getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Tiposseleccion tipo) {
        this.tipo = tipo;
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

    public SelectItem[] getComboAreas() {
        Map parametros = new HashMap();
        parametros.put(";where", "o.tipo.estatus=true and o.activo=true");
        try {
            return Combos.getSelectItems(ejbAreas.encontarParametros(parametros), true);
        } catch (ConsultarException ex) {
            Logger.getLogger(CargosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
