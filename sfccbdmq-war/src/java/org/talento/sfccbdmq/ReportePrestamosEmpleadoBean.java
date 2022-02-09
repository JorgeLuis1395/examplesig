/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.compras.sfccbdmq.ProveedoresBean;
import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
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
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.PrestamosFacade;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Perfiles;
import org.entidades.sfccbdmq.Prestamos;
import org.errores.sfccbdmq.ConsultarException;
import org.seguridad.sfccbdmq.CodigosBean;
import org.seguridad.sfccbdmq.EntidadesBean;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "reportePrestamosEmpleado")
@ViewScoped
public class ReportePrestamosEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{codigosSfccbdmq}")
    private CodigosBean codigosBean;
    private List<Prestamos> listaPrestamos;

    private Prestamos prestamo;

    private Formulario formulario = new Formulario();

    @EJB
    private PrestamosFacade ejbPrestamos;

    @EJB
    private EntidadesFacade ejbEntidades;

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
//        String empleadoString = (String) params.get("x");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String nombreForma = "ReportePrestamosEmpleadosVista";
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            Integer idEmpleado = Integer.parseInt(empleadoString);
            empleadoBean.setEmpleadoSeleccionado(empleadoBean.traer(idEmpleado));
            return;
        } else if (perfilString == null) {
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
     * Creates a new instance of PrestamosEmpleadoBean
     */
    public ReportePrestamosEmpleadoBean() {
    }

    public String buscar() {

       
        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null and o.fechaFinanciero is not null and o.aprobadoFinanciero=true";
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public String buscarFinanciero() {

        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null and o.fechagarante is not null and o.aprobadogarante=true";
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public String buscarPrestamos() {

       
        try {
            Map parametros = new HashMap();
            String where = "o.empleado is not null and o.fechasolicitud is not null and o.empleado=:empleado ";
             parametros.put("empleado",empleadoBean.getEmpleadoSeleccionado());
            parametros.put(";where", where);
            parametros.put(";orden", "o.fechasolicitud desc");
            listaPrestamos = ejbPrestamos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            Logger.getLogger(ReportePrestamosEmpleadoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    

    //TRAER NOMBRE DEL GARANTE
    public String traerGarante(Integer idGarante) throws ConsultarException {
        Entidades retorno = ejbEntidades.find(idGarante);
        if (retorno == null) {
            return "---";
        } else {
            return retorno.toString();
        }
    }

    public Entidades traerCedula(String cedula) {
        if (cedula == null) {
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.pin=:cedula ");
        parametros.put("cedula", cedula);
        try {
            List<Entidades> el = ejbEntidades.encontarParametros(parametros);
            if (!((el == null) || (el.isEmpty()))) {
                return el.get(0);
            }
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(EntidadesBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * @return the empleadoBean
     */
    public EmpleadosBean getEmpleadoBean() {
        return empleadoBean;
    }

    /**
     * @param empleadoBean the empleadoBean to set
     */
    public void setEmpleadoBean(EmpleadosBean empleadoBean) {
        this.empleadoBean = empleadoBean;
    }

    /**
     * @return the listaPrestamos
     */
    public List<Prestamos> getListaPrestamos() {
        return listaPrestamos;
    }

    /**
     * @param listaPrestamos the listaPrestamos to set
     */
    public void setListaPrestamos(List<Prestamos> listaPrestamos) {
        this.listaPrestamos = listaPrestamos;
    }

    /**
     * @return the prestamo
     */
    public Prestamos getPrestamo() {
        return prestamo;
    }

    /**
     * @param prestamo the prestamo to set
     */
    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * @return the imagenesBean
     */
    public ImagenesBean getImagenesBean() {
        return imagenesBean;
    }

    /**
     * @param imagenesBean the imagenesBean to set
     */
    public void setImagenesBean(ImagenesBean imagenesBean) {
        this.imagenesBean = imagenesBean;
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
     * @return the proveedorBean
     */
    public ProveedoresBean getProveedorBean() {
        return proveedorBean;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedoresBean proveedorBean) {
        this.proveedorBean = proveedorBean;
    }

    /**
     * @return the codigosBean
     */
    public CodigosBean getCodigosBean() {
        return codigosBean;
    }

    /**
     * @param codigosBean the codigosBean to set
     */
    public void setCodigosBean(CodigosBean codigosBean) {
        this.codigosBean = codigosBean;
    }

}
