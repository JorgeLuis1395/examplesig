/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inventarios.sfccbdmq;

//import com.icesoft.faces.component.selectinputtext.SelectInputText;
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
import org.auxiliares.sfccbdmq.Constantes;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.beans.sfccbdmq.CodigosFacade;
import org.entidades.sfccbdmq.Codigos;
import org.entidades.sfccbdmq.Maestros;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
import org.seguridad.sfccbdmq.*;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "familiasSuministrosSfccbdmq")
@ViewScoped
public class FamiliasSuministrosBean implements Serializable {

    /**
     * Creates a new instance of CodigosBean
     */
    public FamiliasSuministrosBean() {
    }
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    private Formulario formulario = new Formulario();
    private Formulario formularioBuscar = new Formulario();
    private List<Codigos> codigos;
    private List<Codigos> codigosAutocompletar;
    private Codigos codigo;
    private String nombre;
    private Integer maestro;
    private Maestros maestroEntidad;
    private String codigoMaestro;
    @EJB
    private CodigosFacade ejbCodigos;
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
        String nombreForma = "FamiliasSuministrosVista";
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            return;
//            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        try {
            // se deberia chequear perfil?
            maestroEntidad = ejbCodigos.traerMaestroCodigo(Constantes.FAMILIA_SUMINISTROS);
        } catch (ConsultarException ex) {
            Logger.getLogger(FamiliasSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();

    }

    /**
     * @return the seguridadbean
     */
    public ParametrosSfccbdmqBean getSeguridadbean() {
        return seguridadbean;
    }

    /**
     * @param seguridadbean the seguridadbean to set
     */
    public void setSeguridadbean(ParametrosSfccbdmqBean seguridadbean) {
        this.seguridadbean = seguridadbean;
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
     * @return the codigos
     */
    public List<Codigos> getCodigos() {
        return codigos;
    }

    /**
     * @param codigos the codigos to set
     */
    public void setCodigos(List<Codigos> codigos) {
        this.codigos = codigos;
    }

    /**
     * @return the codigo
     */
    public Codigos getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(Codigos codigo) {
        this.codigo = codigo;
    }

    // colocamos los metodos en verbo
    public String crear() {

        codigo = new Codigos();
        setCodigoMaestro(getMaestroEntidad().getCodigo());
        codigo.setMaestro(getMaestroEntidad());
//        

        formulario.insertar();
        return null;
    }

    public String modificar(Codigos codigo) {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
//        codigo = codigos.get(formulario.getFila().getRowIndex());
        this.codigo = codigo;
        formulario.editar();
        return null;
    }

    public String eliminar(Codigos codigo) {
//        if (!menusBean.getPerfil().getModificacion()) {
//            MensajesErrores.advertencia("No tiene autorización para modificar un registro");
//        }
//        codigo = codigos.get(formulario.getFila().getRowIndex());
        this.codigo = codigo;
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
            parametros.put(";where", "o.maestro.codigo=:maestroParametro and o.activo=true");
            parametros.put("maestroParametro", Constantes.FAMILIA_SUMINISTROS);
//            parametros.put("modulo", maestroEntidad.getCodigo());
            codigos = ejbCodigos.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(FamiliasSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // acciones de base de datos

    private boolean validar() {
        if ((codigo.getCodigo() == null) || (codigo.getCodigo().isEmpty())) {
            MensajesErrores.advertencia("Es necesario código");
            return true;
        }
        if ((codigo.getNombre() == null) || (codigo.getNombre().isEmpty())) {
            MensajesErrores.advertencia("Es necesario nombre");
            return true;
        }
        if ((codigo.getDescripcion() == null) || (codigo.getDescripcion().isEmpty())) {
            MensajesErrores.advertencia("Es necesario descripción");
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
        try {
            //            codigo.setIdmaestro(maestroEntidad);
            codigo.setActivo(Boolean.TRUE);
            ejbCodigos.create(codigo, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            Logger.getLogger(FamiliasSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            codigo.setActivo(Boolean.TRUE);
            ejbCodigos.edit(codigo, seguridadbean.getLogueado().getUserid());

        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(FamiliasSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
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
//            codigo.setActivo(Boolean.FALSE);
            ejbCodigos.remove(codigo, seguridadbean.getLogueado().getUserid());

        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage() + "-" + ex.getCause());
            Logger.getLogger(FamiliasSuministrosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        formulario.cancelar();
        buscar();
        return null;
    }
    // falta el combo

    public Codigos traer(Integer id) throws ConsultarException {
        return ejbCodigos.find(id);
    }

    /**
     * @return the maestro
     */
    public Integer getMaestro() {
        return maestro;
    }

    /**
     * @param maestro the maestro to set
     */
    public void setMaestro(Integer maestro) {
        this.maestro = maestro;
    }

    /**
     * @return the codigoMaestro
     */
    public String getCodigoMaestro() {
        return codigoMaestro;
    }

    /**
     * @param codigoMaestro the codigoMaestro to set
     */
    public void setCodigoMaestro(String codigoMaestro) {
        this.codigoMaestro = codigoMaestro;
    }

    /**
     * @return the maestroEntidad
     */
    public Maestros getMaestroEntidad() {
        return maestroEntidad;
    }

    /**
     * @param maestroEntidad the maestroEntidad to set
     */
    public void setMaestroEntidad(Maestros maestroEntidad) {
        this.maestroEntidad = maestroEntidad;
    }

    /**
     * @return the formularioBuscar
     */
    public Formulario getFormularioBuscar() {
        return formularioBuscar;
    }

    /**
     * @param formularioBuscar the formularioBuscar to set
     */
    public void setFormularioBuscar(Formulario formularioBuscar) {
        this.formularioBuscar = formularioBuscar;
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