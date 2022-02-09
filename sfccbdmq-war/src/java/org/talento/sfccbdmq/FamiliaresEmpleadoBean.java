/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talento.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import java.io.Serializable;
import java.util.Date;
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
import javax.faces.event.ValueChangeEvent;
import org.beans.sfccbdmq.EmpleadosFacade;
import org.beans.sfccbdmq.EntidadesFacade;
import org.beans.sfccbdmq.FamiliasFacade;
import org.entidades.sfccbdmq.Empleados;
import org.entidades.sfccbdmq.Entidades;
import org.entidades.sfccbdmq.Familias;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author evconsul
 */
@ManagedBean(name = "familiasEmpleado")
@ViewScoped
public class FamiliaresEmpleadoBean implements Serializable {

    private static final long serialVersionUID = 1L;
//    private LazyDataModel<Familias> listadoFamiliares;
    @ManagedProperty(value = "#{empleados}")
    private EmpleadosBean empleadoBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    private List<Familias> listaFamilias;
    private Familias familiaEmpleado;
    private Entidades entidad;
    private String cedula;
    private Empleados empleado;
    private Formulario formulario = new Formulario();
    private Formulario formularioPrincipal = new Formulario();
    @EJB
    private FamiliasFacade ejbFamilias;
    @EJB
    private EmpleadosFacade ejbEmpleados;
    @EJB
    private EntidadesFacade ejbEntidades;
    // todo para perfiles 
    private Perfiles perfil;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean parametrosSeguridad;
//    private Date desde;
//    private Date hasta;
//     private Date fechaNacimientoDesde;
//    private Date fechaNacimientoHasta;
//    private Codigos genero;
//    private Codigos tipsang;
//    private Codigos parentesco;
//    private Codigos ecivil;
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
        String nombreForma = "FamiliasEmpleadosVista";
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
     * Creates a new instance of FamiliasEmpleadoBean
     */
    public FamiliaresEmpleadoBean() {

    }

    public String buscar() {

        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }

        try {
            Map parametros = new HashMap();
            String where = "o.entidad=:empleado ";
            parametros.put("empleado", empleadoBean.getEmpleadoSeleccionado().getEntidad());
            parametros.put(";where", where);
//            parametros.put(";orden", "o.fecha desc");
            listaFamilias = ejbFamilias.encontarParametros(parametros);

        } catch (ConsultarException ex) {
            Logger.getLogger(FamiliaresEmpleadoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String nuevo() {
        if (empleadoBean.getEmpleadoSeleccionado() == null) {
            MensajesErrores.advertencia("Ingrese un empleado");
            return null;
        }
        empleado = new Empleados();
        entidad = new Entidades();
        familiaEmpleado = new Familias();
        familiaEmpleado.setFechaingreso(new Date());
        getFamiliaEmpleado().setEntidad(empleadoBean.getEmpleadoSeleccionado().getEntidad());
        Integer idEMpleado = empleadoBean.getEmpleadoSeleccionado().getId();
        imagenesBean.setIdModulo(idEMpleado);
        imagenesBean.setModulo("FAMILIA");
        imagenesBean.Buscar();
        getFormulario().insertar();
        return null;
    }

    public String modifica(Familias familiaEmpleado) {
        this.setFamiliaEmpleado(familiaEmpleado);
        imagenesBean.setIdModulo(familiaEmpleado.getEntidad().getEmpleados().getId());
        imagenesBean.setModulo("FAMILIA");
        imagenesBean.Buscar();
        getFormulario().editar();
        return null;
    }

    public String borra(Familias familiaEmpleado) {
        this.setFamiliaEmpleado(familiaEmpleado);
        getFormulario().eliminar();
        return null;
    }

    public boolean validar() {

        
        if (familiaEmpleado.getParentesco() == null) {
            MensajesErrores.advertencia("Seleccione un parentesco");
            return true;
        }
        if ((familiaEmpleado.getCedula()== null) || (familiaEmpleado.getCedula().isEmpty())) {
            MensajesErrores.advertencia("CI o RUC es obligatorio");
            return true;
        }
        
        if ((familiaEmpleado.getNombres() == null) || (familiaEmpleado.getNombres().isEmpty())) {
            MensajesErrores.advertencia("Nombres es obligatorio");
            return true;
        }
        if ((familiaEmpleado.getApellidos() == null) || (familiaEmpleado.getApellidos().isEmpty())) {
            MensajesErrores.advertencia("Apellidos es obligatorio");
            return true;
        }
        if ((familiaEmpleado.getEmail() == null) || (familiaEmpleado.getEmail().isEmpty())) {
            MensajesErrores.advertencia("Email es obligatorio");
            return true;
        }
        if ((familiaEmpleado.getFechanacimiento()== null)) {
            MensajesErrores.advertencia("Fecha nacimiento obligatorio");
            return true;
        }
        // validar no dos veces la misma entidad si es nueva
//        if (formulario.isNuevo()) {
//            Map parametros = new HashMap();
//            parametros.put(";where", "o.entidad1=:entidad1 and o.entidad2=:entidad2");
//            parametros.put("entidad1", empleadoBean.getEmpleadoSeleccionado().getEntidad());
//            parametros.put("entidad2", entidad);
//            try {
//                int total = ejbFamilias.contar(parametros);
//                if (total > 0) {
//                    MensajesErrores.advertencia("Ya existe este parentesco");
//                    buscar();
//                    return true;
//                }
//            } catch (ConsultarException ex) {
//                MensajesErrores.fatal(ex.getMessage());
//                Logger
//                        .getLogger(FamiliaresEmpleadoBean.class
//                                .getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        return false;
    }

    public String insertar() {
        
        if (validar()) {
            return null;
        }
        try {
            if (!familiaEmpleado.getDiscapacidad()){
                familiaEmpleado.setDetallediscapacidad(null);
            }
            ejbFamilias.create(getFamiliaEmpleado(), parametrosSeguridad.getLogueado().getUserid());

        } catch (InsertarException ex) {
            Logger.getLogger(EmpleadosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String grabar() {
        if (validar()) {
            return null;
        }
        try {
            if (!familiaEmpleado.getDiscapacidad()){
                familiaEmpleado.setDetallediscapacidad(null);
            }
            ejbFamilias.edit(getFamiliaEmpleado(), parametrosSeguridad.getLogueado().getUserid());

        } catch (GrabarException ex) {
            Logger.getLogger(EmpleadosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
        return null;
    }

    public String eliminar() {
        try {

            ejbFamilias.remove(getFamiliaEmpleado(), parametrosSeguridad.getLogueado().getUserid());
        } catch (BorrarException ex) {
            Logger.getLogger(EmpleadosBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        getFormulario().cancelar();
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
     * @return the listaFamilias
     */
    public List<Familias> getListaFamilias() {
        return listaFamilias;
    }

    /**
     * @param listaFamilias the listaFamilias to set
     */
    public void setListaFamilias(List<Familias> listaFamilias) {
        this.listaFamilias = listaFamilias;
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
     * @return the familiaEmpleado
     */
    public Familias getFamiliaEmpleado() {
        return familiaEmpleado;
    }

    /**
     * @param familiaEmpleado the familiaEmpleado to set
     */
    public void setFamiliaEmpleado(Familias familiaEmpleado) {
        this.familiaEmpleado = familiaEmpleado;
    }

    public String insertarEmpleado() {
//        empleadoBean.grabarEntidad();
        try {
            ejbFamilias.create(familiaEmpleado, parametrosSeguridad.getLogueado().getUserid());
            formulario.editar();
            empleadoBean.getFormulario().cancelar();
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger
                    .getLogger(FamiliaresEmpleadoBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    

    public String cargaListner() {
        buscar();
        formularioPrincipal.insertar();
        return null;
    }

    /**
     * @return the formularioPrincipal
     */
    public Formulario getFormularioPrincipal() {
        return formularioPrincipal;
    }

    /**
     * @param formularioPrincipal the formularioPrincipal to set
     */
    public void setFormularioPrincipal(Formulario formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }

    /**
     * @return the entidad
     */
    public Entidades getEntidad() {
        return entidad;
    }

    /**
     * @param entidad the entidad to set
     */
    public void setEntidad(Entidades entidad) {
        this.entidad = entidad;
    }

    /**
     * @return the empleado
     */
    public Empleados getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(Empleados empleado) {
        this.empleado = empleado;
    }
    public void cambiaPin(ValueChangeEvent ve) {
        String valor = (String) ve.getNewValue();
        if ((valor == null) || (valor.isEmpty())) {
            formulario.cancelar();
        } else {
            Map parametros = new HashMap();
            String where = " ";
            where += "  o.pin = :pin";
            parametros.put("pin", valor);
            parametros.put(";where", where);
            try {
                List<Entidades> el = ejbEntidades.encontarParametros(parametros);
                for (Entidades e : el) {
                    entidad = e;
                    entidad.setActivo(Boolean.TRUE);
                    return;
                }
            } catch (ConsultarException  ex) {
                MensajesErrores.fatal(ex.getMessage());
                Logger.getLogger(EmpleadosBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (entidad == null) {
                setEmpleado(new Empleados());
                setEntidad(new Entidades());
                entidad.setPin(valor);
                // trearer los datos de la cabecera
                formulario.insertar();

            }
        }
    }

    /**
     * @return the cedula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * @param cedula the cedula to set
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
}
