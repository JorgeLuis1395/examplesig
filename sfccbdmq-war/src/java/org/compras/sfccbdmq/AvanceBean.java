/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compras.sfccbdmq;

import org.seguridad.sfccbdmq.ConfiguracionBean;
import org.seguridad.sfccbdmq.EntidadesBean;
import org.auxiliares.sfccbdmq.Formulario;
import org.auxiliares.sfccbdmq.ImagenesBean;
import org.auxiliares.sfccbdmq.MensajesErrores;
import org.formularios.sfccbdmq.ParametrosSfccbdmqBean;
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
import org.beans.sfccbdmq.AvanceFacade;
import org.entidades.sfccbdmq.Avance;
import org.entidades.sfccbdmq.Contratos;
import org.entidades.sfccbdmq.Perfiles;
import org.errores.sfccbdmq.BorrarException;
import org.errores.sfccbdmq.ConsultarException;
import org.errores.sfccbdmq.GrabarException;
import org.errores.sfccbdmq.InsertarException;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "avancesSfccbdmq")
@ViewScoped
public class AvanceBean {

    /**
     * Creates a new instance of AvanceBean
     */
    public AvanceBean() {
    }
    private Avance avance;
    private Contratos contrato;
    private List<Avance> listaAvances;
    private Formulario formulario = new Formulario();
    private Perfiles perfil;
    @EJB
    private AvanceFacade ejbAvance;
    @ManagedProperty(value = "#{parametrosSfccbdmq}")
    private ParametrosSfccbdmqBean seguridadbean;
    @ManagedProperty(value = "#{proveedoresSfccbdmq}")
    private ProveedoresBean proveedorBean;
    @ManagedProperty(value = "#{entidadesSfccbdmq}")
    private EntidadesBean entidadesBean;
    @ManagedProperty(value = "#{imagenesSfccbdmq}")
    private ImagenesBean imagenesBean;
    @ManagedProperty(value = "#{configuracionSfccbdmq}")
    private ConfiguracionBean configuracionBean;
    /**
     * @return the avance
     */
    public Avance getAvance() {
        return avance;
    }

    /**
     * @param avance the avance to set
     */
    public void setAvance(Avance avance) {
        this.avance = avance;
    }

    /**
     * @return the avances
     */
    public List<Avance> getListaAvance() {
        return listaAvances;
    }

    /**
     * @param avances the avances to set
     */
    public void setListaAvance(List<Avance> listaAvances) {
        this.listaAvances = listaAvances;
    }

    public String buscar() {
        if (contrato == null) {
            listaAvances = null;
            MensajesErrores.advertencia("Seleccione un contrato  primero");
            return null;
        }
        Map parametros = new HashMap();
        parametros.put(";where", "o.contrato=:contrato");
        parametros.put("contrato", contrato);
        parametros.put(";orden", "o.fecha desc");
        try {
            listaAvances = ejbAvance.encontarParametros(parametros);
        } catch (ConsultarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AvanceBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String crear() {
        if (contrato == null) {

            MensajesErrores.advertencia("Seleccione un contrato primero");
            return null;
        }
        formulario.insertar();
        avance = new Avance();
        avance.setContrato(contrato);
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("AVANCES");
        imagenesBean.Buscar();
        return null;
    }

    public String modificar(Avance avance) {

        this.avance = avance;
        contrato = avance.getContrato();
        imagenesBean.setIdModulo(contrato.getId());
        imagenesBean.setModulo("AVANCES");
        imagenesBean.Buscar();
        formulario.editar();

        return null;
    }

    public String eliminar(Avance avance) {
        this.avance = avance;
        formulario.eliminar();

        return null;
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

    private boolean validar() {

        if ((avance.getFecha() == null)) {
            MensajesErrores.advertencia("Es necesario Fecha de inicio");
            return true;
        }
        if ((avance.getPorcentaje() == null)) {
            MensajesErrores.advertencia("Es necesario Valor de inicio");
            return true;
        }
        try {
            // sumar todos los avances del proyecto
            if (avance.getId() == null) {

                Map parametros = new HashMap();
                parametros.put(";where", "o.contrato=:contrato");
                parametros.put("contrato", contrato);
                parametros.put(";campo", "o.porcentaje");
                double suma = ejbAvance.sumarCampo(parametros).doubleValue();
                double porcentaje = avance.getPorcentaje().doubleValue();
                if (suma + porcentaje > 100) {
                    MensajesErrores.advertencia("Es % supera el 100%");
                    return true;
                }
            } else {
                Map parametros = new HashMap();
                parametros.put(";where", "o.contrato=:contrato and o.id!=:id");
                parametros.put("contrato", contrato);
                parametros.put("id", avance.getId());
                parametros.put(";campo", "o.porcentaje");
                double suma = ejbAvance.sumarCampo(parametros).doubleValue();
                double porcentaje = avance.getPorcentaje().doubleValue();
                if (suma + porcentaje > 100) {
                    MensajesErrores.advertencia("Es % supera el 100%");
                    return true;
                }
            }
        } catch (ConsultarException ex) {
            Logger.getLogger(AvanceBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String insertar() {
        if (validar()) {
            return null;
        }
        try {
            // buscar si existe clasiificador ya con esa fuente en ese proyecto
            avance.setUsuario(seguridadbean.getLogueado().getUserid());
            ejbAvance.create(avance, seguridadbean.getLogueado().getUserid());
        } catch (InsertarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AvanceBean.class.getName()).log(Level.SEVERE, null, ex);
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

            ejbAvance.edit(avance, seguridadbean.getLogueado().getUserid());
        } catch (GrabarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AvanceBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
    }

    public String borrar() {
        try {
            // buscar que no existan reformas,certificaciones para poder borrar

            ejbAvance.remove(avance, seguridadbean.getLogueado().getUserid());
        } catch (BorrarException ex) {
            MensajesErrores.fatal(ex.getMessage());
            Logger.getLogger(AvanceBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        buscar();
        formulario.cancelar();
        return null;
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

    public Avance traer(Integer id) throws ConsultarException {
        return ejbAvance.find(id);
    }

    @PostConstruct
    private void activar() {

        Map params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String perfil = (String) params.get("p");
        String redirect = (String) params.get("faces-redirect");
        if (redirect == null) {
            return;
        }
        String empleadoString = (String) params.get("x");
        if (empleadoString != null) {
            return;
        }
        String nombreForma = "AvanceVista";
        if (perfil == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
        this.setPerfil(seguridadbean.traerPerfil(perfil));
        if (this.getPerfil() == null) {
            MensajesErrores.fatal("Sin perfil válido");
            seguridadbean.cerraSession();
        }
//        if (nombreForma==null){
//            nombreForma="";
//        }
//        if (nombreForma.contains(this.getPerfil().getMenu().getFormulario())) {
//            if (!this.perfil.getGrupo().equals(seguridadbean.getGrpUsuario().getGrupo())) {
//                MensajesErrores.fatal("Sin perfil válido, grupo invalido");
//                seguridadbean.cerraSession();
//            }
//        }
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
     * @return the entidadesBean
     */
    public EntidadesBean getEntidadesBean() {
        return entidadesBean;
    }

    /**
     * @param entidadesBean the entidadesBean to set
     */
    public void setEntidadesBean(EntidadesBean entidadesBean) {
        this.entidadesBean = entidadesBean;
    }

    /**
     * @return the contrato
     */
    public Contratos getContrato() {
        return contrato;
    }

    /**
     * @param contrato the contrato to set
     */
    public void setContrato(Contratos contrato) {
        this.contrato = contrato;
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
